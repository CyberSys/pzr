package zombie.network.packets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import zombie.VirtualZombieManager;
import zombie.ai.State;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.NetworkCharacter;
import zombie.characters.NetworkZombieVariables;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.MPStatistic;
import zombie.network.PacketTypes;


public class ZombieUpdateInfoPacket {
	private static final boolean SendZombieState = false;
	private final ZombieUpdateInfoPacket.PlayerZombiePackInfo[] packInfo = new ZombieUpdateInfoPacket.PlayerZombiePackInfo[512];

	public void clear() {
		int int1 = this.packInfo.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			if (this.packInfo[int2] != null) {
				this.packInfo[int2].zombies.clear();
			}
		}
	}

	public void send() {
		if (!GameServer.bFastForward) {
			this.addZombiesToPackInfo();
			int int1 = GameServer.udpEngine.getMaxConnections();
			for (int int2 = 0; int2 < int1; ++int2) {
				if (this.packInfo[int2] != null && !this.packInfo[int2].zombies.isEmpty()) {
					long long1 = this.packInfo[int2].guid;
					UdpConnection udpConnection = GameServer.udpEngine.getActiveConnection(long1);
					if (udpConnection != null) {
						for (int int3 = this.packInfo[int2].zombies.size() - 1; int3 >= 0; --int3) {
							if (((IsoZombie)this.packInfo[int2].zombies.get(int3)).OnlineID == -1) {
								this.packInfo[int2].zombies.remove(int3);
							}
						}

						while (!this.packInfo[int2].zombies.isEmpty()) {
							ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
							PacketTypes.doPacket((short)10, byteBufferWriter);
							this.writeZombies(byteBufferWriter, this.packInfo[int2], int2);
							udpConnection.endPacketImmediate();
						}
					}
				}
			}
		}
	}

	public void receive(ByteBuffer byteBuffer) {
		if (DebugOptions.instance.Network.Client.UpdateZombiesFromPacket.getValue()) {
			short short1 = byteBuffer.getShort();
			for (short short2 = 0; short2 < short1; ++short2) {
				this.parseZombie(byteBuffer);
			}
		}
	}

	private void parseZombie(ByteBuffer byteBuffer) {
		ZombiePacket zombiePacket = ZombieUpdateInfoPacket.l_receive.zombiePacket;
		zombiePacket.parse(byteBuffer);
		Object object = null;
		try {
			IsoZombie zombie = (IsoZombie)GameClient.IDToZombieMap.get(zombiePacket.id);
			if (zombie == null) {
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)zombiePacket.realx, (double)zombiePacket.realy, (double)zombiePacket.realz);
				if (square != null) {
					VirtualZombieManager.instance.choices.clear();
					VirtualZombieManager.instance.choices.add(square);
					zombie = VirtualZombieManager.instance.createRealZombieAlways(zombiePacket.descriptorID, zombiePacket.realdir, false);
					if (zombie == null) {
						DebugLog.log("Error: VirtualZombieManager can\'t create zombie");
					} else {
						zombie.setFakeDead(false);
						zombie.OnlineID = zombiePacket.id;
						GameClient.IDToZombieMap.put(zombiePacket.id, zombie);
						zombie.lx = zombie.nx = zombie.x = zombiePacket.realx;
						zombie.ly = zombie.ny = zombie.y = zombiePacket.realy;
						zombie.lz = zombie.z = (float)zombiePacket.realz;
						zombie.setDir(IsoDirections.fromIndex(zombiePacket.realdir));
						zombie.setForwardDirection(zombie.dir.ToVector());
						zombie.setCurrent(square);
						zombie.setHealth(zombiePacket.realHealth);
						zombie.networkAI.targetX = zombiePacket.x;
						zombie.networkAI.targetY = zombiePacket.y;
						zombie.networkAI.targetZ = zombiePacket.z;
						zombie.networkAI.targetT = zombiePacket.t;
						zombie.networkAI.predictionType = NetworkCharacter.PredictionMoveTypes.values()[zombiePacket.type];
						zombie.networkAI.moveToTarget = null;
						NetworkZombieVariables.setInt(zombie, (short)1, zombiePacket.target);
						NetworkZombieVariables.setInt(zombie, (short)4, zombiePacket.eatBodyTarget);
						NetworkZombieVariables.setInt(zombie, (short)18, zombiePacket.smParamTargetAngle);
						NetworkZombieVariables.setBooleanVariables(zombie, zombiePacket.booleanVariables);
						zombie.speedMod = zombiePacket.speedMod;
						zombie.setWalkType(zombiePacket.walkType);
						if (zombie.isReanimatedPlayer()) {
							zombie.getStateMachine().changeState((State)null, (Iterable)null);
						}

						for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
							IsoPlayer player = IsoPlayer.players[int1];
							if (square.isCanSee(int1)) {
								zombie.setAlphaAndTarget(int1, 1.0F);
							}

							if (player != null && player.ReanimatedCorpseID == zombiePacket.id) {
								player.ReanimatedCorpseID = -1;
								player.ReanimatedCorpse = zombie;
							}
						}

						zombie.serverState = (String)object;
					}

					zombie.serverState = (String)object;
				} else {
					float float1 = IsoUtils.DistanceManhatten(zombiePacket.x, zombiePacket.y, IsoPlayer.getInstance().x, IsoPlayer.getInstance().y);
					DebugLog.log("Error: GridSquare blank for zombie unspooling: Distance to player =" + float1 + " Zombie.OID=" + zombiePacket.id);
				}

				if (zombie == null) {
					return;
				}
			}

			if (zombie.networkAI.hitVehicle == null) {
				zombie.networkAI.parse(zombiePacket, byteBuffer);
			}

			zombie.lastRemoteUpdate = 0;
			if (!IsoWorld.instance.CurrentCell.getZombieList().contains(zombie)) {
				IsoWorld.instance.CurrentCell.getZombieList().add(zombie);
			}

			if (!IsoWorld.instance.CurrentCell.getObjectList().contains(zombie)) {
				IsoWorld.instance.CurrentCell.getObjectList().add(zombie);
			}

			zombie.serverState = (String)object;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void writeZombies(ByteBufferWriter byteBufferWriter, ZombieUpdateInfoPacket.PlayerZombiePackInfo playerZombiePackInfo, int int1) {
		int int2 = byteBufferWriter.bb.remaining() / 57;
		int2 = PZMath.clamp(int2, 0, playerZombiePackInfo.zombies.size());
		byteBufferWriter.putShort((short)int2);
		for (int int3 = 0; int3 < int2; ++int3) {
			IsoZombie zombie = (IsoZombie)playerZombiePackInfo.zombies.pop();
			writeZombie(byteBufferWriter, zombie, int1);
		}
	}

	public static void writeZombie(ByteBufferWriter byteBufferWriter, IsoZombie zombie, int int1) {
		ZombiePacket zombiePacket = ZombieUpdateInfoPacket.l_send.zombiePacket;
		zombiePacket.set(zombie, int1);
		zombiePacket.write(byteBufferWriter);
	}

	private void addZombiesToPackInfo() {
		int int1 = 0;
		long long1 = System.currentTimeMillis();
		ArrayList arrayList = IsoWorld.instance.CurrentCell.getZombieList();
		Iterator iterator = GameServer.udpEngine.connections.iterator();
		while (iterator.hasNext()) {
			UdpConnection udpConnection = (UdpConnection)iterator.next();
			int int2 = 0;
			for (int int3 = 0; int2 < arrayList.size() && int3 < 300; ++int2) {
				IsoZombie zombie = (IsoZombie)arrayList.get(int2);
				if (zombie != null && !zombie.isDead() && zombie.networkAI.isUpdateNeeded(udpConnection.index) && udpConnection.RelevantToPlayers((double)zombie.x, (double)zombie.y, 40.0)) {
					this.addZombieToPackInfo(zombie, udpConnection.index, udpConnection.getConnectedGUID());
					++int1;
					++int3;
				}
			}
		}

		long long2 = System.currentTimeMillis();
		MPStatistic.instance.count1(long2 - long1);
		MPStatistic.instance.count2((long)int1);
		MPStatistic.instance.count3((long)arrayList.size());
	}

	private void addZombieToPackInfo(IsoZombie zombie, int int1, long long1) {
		if (this.packInfo[int1] == null) {
			this.packInfo[int1] = new ZombieUpdateInfoPacket.PlayerZombiePackInfo();
		}

		if (!this.packInfo[int1].zombies.contains(zombie)) {
			this.packInfo[int1].zombies.add(zombie);
			this.packInfo[int1].guid = long1;
		}
	}

	public static class PlayerZombiePackInfo {
		public final Stack zombies = new Stack();
		public long guid;
	}

	private static class l_receive {
		static final Vector2 diff = new Vector2();
		static final ZombiePacket zombiePacket = new ZombiePacket();
	}

	private static class l_send {
		static final ZombiePacket zombiePacket = new ZombiePacket();
	}
}
