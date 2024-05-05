package zombie.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.ZomboidFileSystem;
import zombie.characters.IsoPlayer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.packets.PlayerPacket;


public class ReplayManager {
	private static final int ReplayManagerVersion = 1;
	private ReplayManager.State state;
	private IsoPlayer player;
	private ByteBuffer bbpp;
	private FileOutputStream outStream;
	private DataOutputStream output;
	private FileInputStream inStream;
	private DataInputStream input;
	private int inputVersion;
	private long inputTimeShift;
	private PlayerPacket nextpp;
	private long nextppTime;

	public ReplayManager(IsoPlayer player) {
		this.state = ReplayManager.State.Stop;
		this.player = null;
		this.bbpp = ByteBuffer.allocate(43);
		this.outStream = null;
		this.output = null;
		this.inStream = null;
		this.input = null;
		this.inputVersion = 0;
		this.inputTimeShift = 0L;
		this.nextpp = null;
		this.nextppTime = 0L;
		this.player = player;
	}

	public ReplayManager.State getState() {
		return this.state;
	}

	public boolean isPlay() {
		return this.state == ReplayManager.State.Playing;
	}

	public void recordPlayerPacket(PlayerPacket playerPacket) {
		if (this.state == ReplayManager.State.Recording && playerPacket.id == this.player.OnlineID) {
			this.bbpp.position(0);
			ByteBufferWriter byteBufferWriter = new ByteBufferWriter(this.bbpp);
			playerPacket.write(byteBufferWriter);
			try {
				this.output.writeLong(GameTime.getServerTime());
				this.output.write(PacketTypes.PacketType.PlayerUpdate.getId());
				this.output.write(byteBufferWriter.bb.array());
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	public boolean startRecordReplay(IsoPlayer player, String string) {
		File file = ZomboidFileSystem.instance.getFileInCurrentSave(string);
		if (this.player != null && this.state == ReplayManager.State.Recording) {
			DebugLog.log("ReplayManager: record replay already active for " + this.player.getUsername() + " user");
			return false;
		} else if (file.exists()) {
			DebugLog.log("ReplayManager: invalid filename \"" + string + "\"");
			return false;
		} else {
			try {
				this.outStream = new FileOutputStream(file);
				this.output = new DataOutputStream(this.outStream);
				this.output.write(1);
				this.output.writeLong(GameTime.getServerTime());
				this.player = player;
				this.state = ReplayManager.State.Recording;
				return true;
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				return false;
			}
		}
	}

	public boolean stopRecordReplay() {
		if (this.state != ReplayManager.State.Recording) {
			DebugLog.log("ReplayManager: record inactive");
			return false;
		} else {
			try {
				this.state = ReplayManager.State.Stop;
				this.player = null;
				this.output.flush();
				this.output.close();
				this.outStream.close();
				this.output = null;
				return true;
			} catch (IOException ioException) {
				ioException.printStackTrace();
				return false;
			}
		}
	}

	public boolean startPlayReplay(IsoPlayer player, String string, UdpConnection udpConnection) {
		File file = ZomboidFileSystem.instance.getFileInCurrentSave(string);
		if (this.state == ReplayManager.State.Playing) {
			DebugLog.log("ReplayManager: play replay already active for " + this.player.getUsername() + " user");
			return false;
		} else if (!file.exists()) {
			DebugLog.log("ReplayManager: invalid filename \"" + string + "\"");
			return false;
		} else {
			try {
				this.inStream = new FileInputStream(file);
				this.input = new DataInputStream(this.inStream);
				this.inputVersion = this.input.read();
				this.inputTimeShift = GameTime.getServerTime() - this.input.readLong();
				this.nextppTime = this.input.readLong();
				int int1 = this.input.read();
				if (int1 == PacketTypes.PacketType.PlayerUpdate.getId() || int1 == PacketTypes.PacketType.PlayerUpdateReliable.getId()) {
					this.input.read(this.bbpp.array());
					this.bbpp.position(0);
					this.nextpp = new PlayerPacket();
					this.nextpp.parse(this.bbpp, udpConnection);
				}

				this.player = player;
				this.state = ReplayManager.State.Playing;
				return true;
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				return false;
			}
		}
	}

	public boolean stopPlayReplay() {
		if (this.state != ReplayManager.State.Playing) {
			DebugLog.log("ReplayManager: play inactive");
			return false;
		} else {
			try {
				this.state = ReplayManager.State.Stop;
				this.player = null;
				this.input.close();
				this.inStream.close();
				this.input = null;
				this.inputVersion = 0;
				this.inputTimeShift = 0L;
				this.nextpp = null;
				return true;
			} catch (IOException ioException) {
				ioException.printStackTrace();
				return false;
			}
		}
	}

	public void update() {
		if (this.state == ReplayManager.State.Playing) {
			if (GameTime.getServerTime() >= this.nextppTime + this.inputTimeShift) {
				this.nextpp.id = this.player.OnlineID;
				PlayerPacket playerPacket = this.nextpp;
				playerPacket.realt = (int)((long)playerPacket.realt + this.inputTimeShift / 1000000L);
				IsoPlayer player = (IsoPlayer)GameServer.IDToPlayerMap.get(Integer.valueOf(this.nextpp.id));
				UdpConnection udpConnection = GameServer.getConnectionFromPlayer(player);
				try {
					if (player == null) {
						DebugLog.General.error("receivePlayerUpdate: Server received position for unknown player (id:" + this.nextpp.id + "). Server will ignore this data.");
					} else {
						player.networkAI.parse(this.nextpp);
						udpConnection.ReleventPos[player.PlayerIndex].x = this.nextpp.realx;
						udpConnection.ReleventPos[player.PlayerIndex].y = this.nextpp.realy;
						udpConnection.ReleventPos[player.PlayerIndex].z = (float)this.nextpp.realz;
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}

				int int1;
				for (int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
					UdpConnection udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int1);
					if (udpConnection.getConnectedGUID() != udpConnection2.getConnectedGUID()) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.PacketType.PlayerUpdate.doPacket(byteBufferWriter);
						this.nextpp.write(byteBufferWriter);
						PacketTypes.PacketType.PlayerUpdate.send(udpConnection2);
					}
				}

				try {
					this.nextppTime = this.input.readLong();
					int1 = this.input.read();
					if (int1 == PacketTypes.PacketType.PlayerUpdate.getId() || int1 == PacketTypes.PacketType.PlayerUpdateReliable.getId()) {
						this.bbpp.position(0);
						this.input.read(this.bbpp.array());
						this.bbpp.position(0);
						this.nextpp = new PlayerPacket();
						this.nextpp.parse(this.bbpp, udpConnection);
					}
				} catch (IOException ioException) {
					DebugLog.log("ReplayManager: stop playing replay");
					this.stopPlayReplay();
				}
			}
		}
	}

	public static enum State {

		Stop,
		Recording,
		Playing;

		private static ReplayManager.State[] $values() {
			return new ReplayManager.State[]{Stop, Recording, Playing};
		}
	}
}
