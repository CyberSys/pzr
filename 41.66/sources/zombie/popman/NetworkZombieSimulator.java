package zombie.popman;

import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.Sets.SetView;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import zombie.VirtualZombieManager;
import zombie.ai.State;
import zombie.ai.states.ZombieHitReactionState;
import zombie.ai.states.ZombieOnGroundState;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.NetworkZombieVariables;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.network.packets.ZombiePacket;


public class NetworkZombieSimulator {
	public static final int MAX_ZOMBIES_PER_UPDATE = 300;
	private static final NetworkZombieSimulator instance = new NetworkZombieSimulator();
	private static final ZombiePacket zombiePacket = new ZombiePacket();
	private final ByteBuffer bb = ByteBuffer.allocate(1000000);
	private final ArrayList unknownZombies = new ArrayList();
	private final HashSet authoriseZombies = new HashSet();
	private final ArrayDeque SendQueue = new ArrayDeque();
	private final ArrayDeque ExtraSendQueue = new ArrayDeque();
	private HashSet authoriseZombiesCurrent = new HashSet();
	private HashSet authoriseZombiesLast = new HashSet();
	UpdateLimit ZombieSimulationReliableLimit = new UpdateLimit(1000L);

	public static NetworkZombieSimulator getInstance() {
		return instance;
	}

	public int getAuthorizedZombieCount() {
		return (int)IsoWorld.instance.CurrentCell.getZombieList().stream().filter((var0)->{
			return var0.authOwner == GameClient.connection;
		}).count();
	}

	public int getUnauthorizedZombieCount() {
		return (int)IsoWorld.instance.CurrentCell.getZombieList().stream().filter((var0)->{
			return var0.authOwner == null;
		}).count();
	}

	public void clear() {
		HashSet hashSet = this.authoriseZombiesCurrent;
		this.authoriseZombiesCurrent = this.authoriseZombiesLast;
		this.authoriseZombiesLast = hashSet;
		this.authoriseZombiesLast.removeIf((var0)->{
			return GameClient.getZombie(var0) == null;
		});
		this.authoriseZombiesCurrent.clear();
	}

	public void addExtraUpdate(IsoZombie zombie) {
		if (zombie.authOwner == GameClient.connection && !this.ExtraSendQueue.contains(zombie)) {
			this.ExtraSendQueue.add(zombie);
		}
	}

	public void add(short short1) {
		this.authoriseZombiesCurrent.add(short1);
	}

	public void added() {
		SetView setView = Sets.difference(this.authoriseZombiesCurrent, this.authoriseZombiesLast);
		UnmodifiableIterator unmodifiableIterator = setView.iterator();
		while (true) {
			while (unmodifiableIterator.hasNext()) {
				Short Short1 = (Short)unmodifiableIterator.next();
				IsoZombie zombie = GameClient.getZombie(Short1);
				if (zombie != null && zombie.OnlineID == Short1) {
					this.becomeLocal(zombie);
				} else if (!this.unknownZombies.contains(Short1)) {
					this.unknownZombies.add(Short1);
				}
			}

			SetView setView2 = Sets.difference(this.authoriseZombiesLast, this.authoriseZombiesCurrent);
			UnmodifiableIterator unmodifiableIterator2 = setView2.iterator();
			while (unmodifiableIterator2.hasNext()) {
				Short Short2 = (Short)unmodifiableIterator2.next();
				IsoZombie zombie2 = GameClient.getZombie(Short2);
				if (zombie2 != null) {
					this.becomeRemote(zombie2);
				}
			}

			synchronized (this.authoriseZombies) {
				this.authoriseZombies.clear();
				this.authoriseZombies.addAll(this.authoriseZombiesCurrent);
				return;
			}
		}
	}

	public void becomeLocal(IsoZombie zombie) {
		zombie.lastRemoteUpdate = 0;
		zombie.authOwner = GameClient.connection;
		zombie.authOwnerPlayer = IsoPlayer.getInstance();
		zombie.networkAI.setUpdateTimer(0.0F);
		zombie.AllowRepathDelay = 0.0F;
		zombie.networkAI.mindSync.restorePFBTarget();
	}

	public void becomeRemote(IsoZombie zombie) {
		if (zombie.isDead() && zombie.authOwner == GameClient.connection) {
			zombie.getNetworkCharacterAI().setLocal(true);
		}

		zombie.lastRemoteUpdate = 0;
		zombie.authOwner = null;
		zombie.authOwnerPlayer = null;
		if (zombie.group != null) {
			zombie.group.remove(zombie);
		}
	}

	public boolean isZombieSimulated(Short Short1) {
		synchronized (this.authoriseZombies) {
			return this.authoriseZombies.contains(Short1);
		}
	}

	public void receivePacket(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (DebugOptions.instance.Network.Client.UpdateZombiesFromPacket.getValue()) {
			short short1 = byteBuffer.getShort();
			for (short short2 = 0; short2 < short1; ++short2) {
				this.parseZombie(byteBuffer, udpConnection);
			}
		}
	}

	private void parseZombie(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		ZombiePacket zombiePacket = zombiePacket;
		zombiePacket.parse(byteBuffer, udpConnection);
		if (zombiePacket.id == -1) {
			DebugLog.General.error("NetworkZombieSimulator.parseZombie id=" + zombiePacket.id);
		} else {
			try {
				IsoZombie zombie = (IsoZombie)GameClient.IDToZombieMap.get(zombiePacket.id);
				if (zombie == null) {
					if (IsoDeadBody.isDead(zombiePacket.id)) {
						DebugLog.log(DebugType.Multiplayer, "Skip dead zombie creation id=" + zombiePacket.id);
						return;
					}

					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)zombiePacket.realX, (double)zombiePacket.realY, (double)zombiePacket.realZ);
					if (square != null) {
						VirtualZombieManager.instance.choices.clear();
						VirtualZombieManager.instance.choices.add(square);
						zombie = VirtualZombieManager.instance.createRealZombieAlways(zombiePacket.descriptorID, IsoDirections.getRandom().index(), false);
						DebugLog.log(DebugType.ActionSystem, "ParseZombie: CreateRealZombieAlways id=" + zombiePacket.id);
						if (zombie != null) {
							zombie.setFakeDead(false);
							zombie.OnlineID = zombiePacket.id;
							GameClient.IDToZombieMap.put(zombiePacket.id, zombie);
							zombie.lx = zombie.nx = zombie.x = zombiePacket.realX;
							zombie.ly = zombie.ny = zombie.y = zombiePacket.realY;
							zombie.lz = zombie.z = (float)zombiePacket.realZ;
							zombie.setForwardDirection(zombie.dir.ToVector());
							zombie.setCurrent(square);
							zombie.networkAI.targetX = zombiePacket.x;
							zombie.networkAI.targetY = zombiePacket.y;
							zombie.networkAI.targetZ = zombiePacket.z;
							zombie.networkAI.predictionType = zombiePacket.moveType;
							NetworkZombieVariables.setInt(zombie, (short)0, zombiePacket.realHealth);
							NetworkZombieVariables.setInt(zombie, (short)2, zombiePacket.speedMod);
							NetworkZombieVariables.setInt(zombie, (short)1, zombiePacket.target);
							NetworkZombieVariables.setInt(zombie, (short)3, zombiePacket.timeSinceSeenFlesh);
							NetworkZombieVariables.setInt(zombie, (short)4, zombiePacket.smParamTargetAngle);
							NetworkZombieVariables.setBooleanVariables(zombie, zombiePacket.booleanVariables);
							if (zombie.isKnockedDown()) {
								zombie.setOnFloor(true);
								zombie.changeState(ZombieOnGroundState.instance());
							}

							zombie.setWalkType(zombiePacket.walkType.toString());
							zombie.realState = zombiePacket.realState;
							if (zombie.isReanimatedPlayer()) {
								zombie.getStateMachine().changeState((State)null, (Iterable)null);
							}

							for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
								IsoPlayer player = IsoPlayer.players[int1];
								if (square.isCanSee(int1)) {
									zombie.setAlphaAndTarget(int1, 1.0F);
								}

								if (player != null && player.ReanimatedCorpseID == zombiePacket.id && zombiePacket.id != -1) {
									player.ReanimatedCorpseID = -1;
									player.ReanimatedCorpse = zombie;
								}
							}

							zombie.networkAI.mindSync.parse(zombiePacket);
						} else {
							DebugLog.log("Error: VirtualZombieManager can\'t create zombie");
						}
					}

					if (zombie == null) {
						return;
					}
				}

				if (getInstance().isZombieSimulated(zombie.OnlineID)) {
					zombie.authOwner = GameClient.connection;
					zombie.authOwnerPlayer = IsoPlayer.getInstance();
					return;
				}

				zombie.authOwner = null;
				zombie.authOwnerPlayer = null;
				if (!zombie.networkAI.isSetVehicleHit() || !zombie.isCurrentState(ZombieHitReactionState.instance())) {
					zombie.networkAI.parse(zombiePacket);
					zombie.networkAI.mindSync.parse(zombiePacket);
				}

				zombie.lastRemoteUpdate = 0;
				if (!IsoWorld.instance.CurrentCell.getZombieList().contains(zombie)) {
					IsoWorld.instance.CurrentCell.getZombieList().add(zombie);
				}

				if (!IsoWorld.instance.CurrentCell.getObjectList().contains(zombie)) {
					IsoWorld.instance.CurrentCell.getObjectList().add(zombie);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public boolean anyUnknownZombies() {
		return this.unknownZombies.size() > 0;
	}

	public void send() {
		if (this.authoriseZombies.size() != 0 || this.unknownZombies.size() != 0) {
			IsoZombie zombie;
			if (this.SendQueue.isEmpty()) {
				synchronized (this.authoriseZombies) {
					Iterator iterator = this.authoriseZombies.iterator();
					while (iterator.hasNext()) {
						Short Short1 = (Short)iterator.next();
						zombie = GameClient.getZombie(Short1);
						if (zombie != null && zombie.OnlineID != -1) {
							this.SendQueue.add(zombie);
						}
					}
				}
			}

			this.bb.clear();
			int int1;
			int int2;
			synchronized (ZombieCountOptimiser.zombiesForDelete) {
				int1 = ZombieCountOptimiser.zombiesForDelete.size();
				this.bb.putShort((short)int1);
				int2 = 0;
				while (true) {
					if (int2 >= int1) {
						ZombieCountOptimiser.zombiesForDelete.clear();
						break;
					}

					this.bb.putShort(((IsoZombie)ZombieCountOptimiser.zombiesForDelete.get(int2)).OnlineID);
					++int2;
				}
			}

			int int3 = this.unknownZombies.size();
			this.bb.putShort((short)int3);
			for (int1 = 0; int1 < int3; ++int1) {
				this.bb.putShort((Short)this.unknownZombies.get(int1));
			}

			this.unknownZombies.clear();
			int1 = this.bb.position();
			this.bb.putShort((short)300);
			int2 = 0;
			while (!this.SendQueue.isEmpty()) {
				zombie = (IsoZombie)this.SendQueue.poll();
				this.ExtraSendQueue.remove(zombie);
				zombie.zombiePacket.set(zombie);
				if (zombie.OnlineID != -1) {
					zombie.zombiePacket.write(this.bb);
					zombie.networkAI.targetX = zombie.realx = zombie.x;
					zombie.networkAI.targetY = zombie.realy = zombie.y;
					zombie.networkAI.targetZ = zombie.realz = (byte)((int)zombie.z);
					zombie.realdir = zombie.getDir();
					++int2;
					if (int2 >= 300) {
						break;
					}
				}
			}

			int int4;
			if (int2 < 300) {
				int4 = this.bb.position();
				this.bb.position(int1);
				this.bb.putShort((short)int2);
				this.bb.position(int4);
			}

			if (int2 > 0 || int3 > 0) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.PacketType packetType;
				if (int3 > 0 && this.ZombieSimulationReliableLimit.Check()) {
					packetType = PacketTypes.PacketType.ZombieSimulationReliable;
				} else {
					packetType = PacketTypes.PacketType.ZombieSimulation;
				}

				packetType.doPacket(byteBufferWriter);
				byteBufferWriter.bb.put(this.bb.array(), 0, this.bb.position());
				packetType.send(GameClient.connection);
			}

			if (!this.ExtraSendQueue.isEmpty()) {
				this.bb.clear();
				this.bb.putShort((short)0);
				this.bb.putShort((short)0);
				int1 = this.bb.position();
				this.bb.putShort((short)0);
				int4 = 0;
				while (!this.ExtraSendQueue.isEmpty()) {
					IsoZombie zombie2 = (IsoZombie)this.ExtraSendQueue.poll();
					zombie2.zombiePacket.set(zombie2);
					if (zombie2.OnlineID != -1) {
						zombie2.zombiePacket.write(this.bb);
						zombie2.networkAI.targetX = zombie2.realx = zombie2.x;
						zombie2.networkAI.targetY = zombie2.realy = zombie2.y;
						zombie2.networkAI.targetZ = zombie2.realz = (byte)((int)zombie2.z);
						zombie2.realdir = zombie2.getDir();
						++int4;
					}
				}

				int int5 = this.bb.position();
				this.bb.position(int1);
				this.bb.putShort((short)int4);
				this.bb.position(int5);
				if (int4 > 0) {
					ByteBufferWriter byteBufferWriter2 = GameClient.connection.startPacket();
					PacketTypes.PacketType.ZombieSimulation.doPacket(byteBufferWriter2);
					byteBufferWriter2.bb.put(this.bb.array(), 0, this.bb.position());
					PacketTypes.PacketType.ZombieSimulation.send(GameClient.connection);
				}
			}
		}
	}

	public void remove(IsoZombie zombie) {
		if (zombie != null && zombie.OnlineID != -1) {
			GameClient.IDToZombieMap.remove(zombie.OnlineID);
		}
	}

	public void clearTargetAuth(IsoPlayer player) {
		if (Core.bDebug) {
			DebugLog.log(DebugType.Multiplayer, "Clear zombies target and auth for player id=" + player.getOnlineID());
		}

		if (GameClient.bClient) {
			Iterator iterator = GameClient.IDToZombieMap.valueCollection().iterator();
			while (iterator.hasNext()) {
				IsoZombie zombie = (IsoZombie)iterator.next();
				if (zombie.target == player) {
					zombie.setTarget((IsoMovingObject)null);
				}
			}
		}
	}
}
