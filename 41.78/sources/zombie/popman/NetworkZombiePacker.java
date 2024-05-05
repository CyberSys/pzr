package zombie.popman;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import zombie.VirtualZombieManager;
import zombie.characters.IsoZombie;
import zombie.characters.NetworkZombieVariables;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.network.MPStatistics;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;
import zombie.network.packets.ZombiePacket;


public class NetworkZombiePacker {
	private static final NetworkZombiePacker instance = new NetworkZombiePacker();
	private final ArrayList zombiesDeleted = new ArrayList();
	private final ArrayList zombiesDeletedForSending = new ArrayList();
	private final HashSet zombiesReceived = new HashSet();
	private final ArrayList zombiesProcessing = new ArrayList();
	private final NetworkZombieList zombiesRequest = new NetworkZombieList();
	private final ZombiePacket packet = new ZombiePacket();
	private HashSet extraUpdate = new HashSet();
	private final ByteBuffer bb = ByteBuffer.allocate(1000000);
	UpdateLimit ZombieSimulationReliableLimit = new UpdateLimit(5000L);

	public static NetworkZombiePacker getInstance() {
		return instance;
	}

	public void setExtraUpdate() {
		for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
			if (udpConnection.isFullyConnected()) {
				this.extraUpdate.add(udpConnection);
			}
		}
	}

	public void deleteZombie(IsoZombie zombie) {
		synchronized (this.zombiesDeleted) {
			this.zombiesDeleted.add(new NetworkZombiePacker.DeletedZombie(zombie.OnlineID, zombie.x, zombie.y));
		}
	}

	public void receivePacket(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		short short2;
		for (int int1 = 0; int1 < short1; ++int1) {
			short2 = byteBuffer.getShort();
			IsoZombie zombie = (IsoZombie)ServerMap.instance.ZombieMap.get(short2);
			if (zombie != null && (udpConnection.accessLevel == 32 || zombie.authOwner == udpConnection)) {
				this.deleteZombie(zombie);
				DebugLog.Multiplayer.noise("Zombie was deleted id=%d (%f, %f)", zombie.OnlineID, zombie.x, zombie.y);
				VirtualZombieManager.instance.removeZombieFromWorld(zombie);
				MPStatistics.serverZombieCulled();
			}
		}

		short short3 = byteBuffer.getShort();
		for (int int2 = 0; int2 < short3; ++int2) {
			short short4 = byteBuffer.getShort();
			IsoZombie zombie2 = (IsoZombie)ServerMap.instance.ZombieMap.get((short)short4);
			if (zombie2 != null) {
				this.zombiesRequest.getNetworkZombie(udpConnection).zombies.add(zombie2);
			}
		}

		short2 = byteBuffer.getShort();
		for (int int3 = 0; int3 < short2; ++int3) {
			this.parseZombie(byteBuffer, udpConnection);
		}
	}

	public void parseZombie(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.packet.parse(byteBuffer, udpConnection);
		if (this.packet.id == -1) {
			DebugLog.General.error("NetworkZombiePacker.parseZombie id=" + this.packet.id);
		} else {
			try {
				IsoZombie zombie = (IsoZombie)ServerMap.instance.ZombieMap.get(this.packet.id);
				if (zombie == null) {
					return;
				}

				if (zombie.authOwner != udpConnection) {
					NetworkZombieManager.getInstance().recheck(udpConnection);
					this.extraUpdate.add(udpConnection);
					return;
				}

				this.applyZombie(zombie);
				zombie.lastRemoteUpdate = 0;
				if (!IsoWorld.instance.CurrentCell.getZombieList().contains(zombie)) {
					IsoWorld.instance.CurrentCell.getZombieList().add(zombie);
				}

				if (!IsoWorld.instance.CurrentCell.getObjectList().contains(zombie)) {
					IsoWorld.instance.CurrentCell.getObjectList().add(zombie);
				}

				zombie.zombiePacket.copy(this.packet);
				zombie.zombiePacketUpdated = true;
				synchronized (this.zombiesReceived) {
					this.zombiesReceived.add(zombie);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public void postupdate() {
		this.updateAuth();
		synchronized (this.zombiesReceived) {
			this.zombiesProcessing.clear();
			this.zombiesProcessing.addAll(this.zombiesReceived);
			this.zombiesReceived.clear();
		}
		synchronized (this.zombiesDeleted) {
			this.zombiesDeletedForSending.clear();
			this.zombiesDeletedForSending.addAll(this.zombiesDeleted);
			this.zombiesDeleted.clear();
		}
		Iterator iterator = GameServer.udpEngine.connections.iterator();
		while (iterator.hasNext()) {
			UdpConnection udpConnection = (UdpConnection)iterator.next();
			if (udpConnection != null && udpConnection.isFullyConnected()) {
				this.send(udpConnection);
			}
		}
	}

	private void updateAuth() {
		ArrayList arrayList = IsoWorld.instance.CurrentCell.getZombieList();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			IsoZombie zombie = (IsoZombie)arrayList.get(int1);
			NetworkZombieManager.getInstance().updateAuth(zombie);
		}
	}

	public int getZombieData(UdpConnection udpConnection, ByteBuffer byteBuffer) {
		int int1 = byteBuffer.position();
		byteBuffer.putShort((short)300);
		int int2 = 0;
		try {
			NetworkZombieList.NetworkZombie networkZombie = this.zombiesRequest.getNetworkZombie(udpConnection);
			while (!networkZombie.zombies.isEmpty()) {
				IsoZombie zombie = (IsoZombie)networkZombie.zombies.poll();
				zombie.zombiePacket.set(zombie);
				if (zombie.OnlineID != -1) {
					zombie.zombiePacket.write(byteBuffer);
					zombie.zombiePacketUpdated = false;
					++int2;
					if (int2 >= 300) {
						break;
					}
				}
			}

			int int3;
			for (int3 = 0; int3 < this.zombiesProcessing.size(); ++int3) {
				IsoZombie zombie2 = (IsoZombie)this.zombiesProcessing.get(int3);
				if (zombie2.authOwner != null && zombie2.authOwner != udpConnection && udpConnection.RelevantTo(zombie2.x, zombie2.y, (float)((udpConnection.ReleventRange - 2) * 10)) && zombie2.OnlineID != -1) {
					zombie2.zombiePacket.write(byteBuffer);
					zombie2.zombiePacketUpdated = false;
					++int2;
				}
			}

			int3 = byteBuffer.position();
			byteBuffer.position(int1);
			byteBuffer.putShort((short)int2);
			byteBuffer.position(int3);
		} catch (BufferOverflowException bufferOverflowException) {
			bufferOverflowException.printStackTrace();
		}

		return int2;
	}

	public void send(UdpConnection udpConnection) {
		this.bb.clear();
		this.bb.put((byte)(udpConnection.isNeighborPlayer ? 1 : 0));
		int int1 = this.bb.position();
		short short1 = 0;
		this.bb.putShort((short)0);
		Iterator iterator = this.zombiesDeletedForSending.iterator();
		while (iterator.hasNext()) {
			NetworkZombiePacker.DeletedZombie deletedZombie = (NetworkZombiePacker.DeletedZombie)iterator.next();
			if (udpConnection.RelevantTo(deletedZombie.x, deletedZombie.y)) {
				++short1;
				this.bb.putShort(deletedZombie.OnlineID);
			}
		}

		int int2 = this.bb.position();
		this.bb.position(int1);
		this.bb.putShort(short1);
		this.bb.position(int2);
		NetworkZombieManager.getInstance().getZombieAuth(udpConnection, this.bb);
		int int3 = this.getZombieData(udpConnection, this.bb);
		if (int3 > 0 || udpConnection.timerSendZombie.check() || this.extraUpdate.contains(udpConnection)) {
			this.extraUpdate.remove(udpConnection);
			udpConnection.timerSendZombie.reset(3800L);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType packetType;
			if (this.ZombieSimulationReliableLimit.Check()) {
				packetType = PacketTypes.PacketType.ZombieSimulationReliable;
			} else {
				packetType = PacketTypes.PacketType.ZombieSimulation;
			}

			packetType.doPacket(byteBufferWriter);
			byteBufferWriter.bb.put(this.bb.array(), 0, this.bb.position());
			packetType.send(udpConnection);
		}
	}

	private void applyZombie(IsoZombie zombie) {
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)this.packet.x, (int)this.packet.y, this.packet.z);
		zombie.lx = zombie.nx = zombie.x = this.packet.realX;
		zombie.ly = zombie.ny = zombie.y = this.packet.realY;
		zombie.lz = zombie.z = (float)this.packet.realZ;
		zombie.setForwardDirection(zombie.dir.ToVector());
		zombie.setCurrent(square);
		zombie.networkAI.targetX = this.packet.x;
		zombie.networkAI.targetY = this.packet.y;
		zombie.networkAI.targetZ = this.packet.z;
		zombie.networkAI.predictionType = this.packet.moveType;
		NetworkZombieVariables.setInt(zombie, (short)0, this.packet.realHealth);
		NetworkZombieVariables.setInt(zombie, (short)2, this.packet.speedMod);
		NetworkZombieVariables.setInt(zombie, (short)1, this.packet.target);
		NetworkZombieVariables.setInt(zombie, (short)3, this.packet.timeSinceSeenFlesh);
		NetworkZombieVariables.setInt(zombie, (short)4, this.packet.smParamTargetAngle);
		NetworkZombieVariables.setBooleanVariables(zombie, this.packet.booleanVariables);
		zombie.setWalkType(this.packet.walkType.toString());
		zombie.realState = this.packet.realState;
	}

	class DeletedZombie {
		short OnlineID;
		float x;
		float y;

		public DeletedZombie(short short1, float float1, float float2) {
			this.OnlineID = short1;
			this.x = float1;
			this.y = float2;
		}
	}
}
