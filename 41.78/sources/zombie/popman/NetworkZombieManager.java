package zombie.popman;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import zombie.SystemDisabler;
import zombie.ai.State;
import zombie.ai.states.ZombieEatBodyState;
import zombie.ai.states.ZombieIdleState;
import zombie.ai.states.ZombieSittingState;
import zombie.ai.states.ZombieTurnAlerted;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.util.Type;


public class NetworkZombieManager {
	private static final NetworkZombieManager instance = new NetworkZombieManager();
	private final NetworkZombieList owns = new NetworkZombieList();
	private static final float NospottedDistanceSquared = 16.0F;

	public static NetworkZombieManager getInstance() {
		return instance;
	}

	public int getAuthorizedZombieCount(UdpConnection udpConnection) {
		return (int)IsoWorld.instance.CurrentCell.getZombieList().stream().filter((udpConnectionx)->{
			return udpConnectionx.authOwner == udpConnection;
		}).count();
	}

	public int getUnauthorizedZombieCount() {
		return (int)IsoWorld.instance.CurrentCell.getZombieList().stream().filter((var0)->{
			return var0.authOwner == null;
		}).count();
	}

	public static boolean canSpotted(IsoZombie zombie) {
		if (zombie.isRemoteZombie()) {
			return false;
		} else if (zombie.target != null && IsoUtils.DistanceToSquared(zombie.x, zombie.y, zombie.target.x, zombie.target.y) < 16.0F) {
			return false;
		} else {
			State state = zombie.getCurrentState();
			return state == null || state == ZombieIdleState.instance() || state == ZombieEatBodyState.instance() || state == ZombieSittingState.instance() || state == ZombieTurnAlerted.instance();
		}
	}

	public void updateAuth(IsoZombie zombie) {
		if (GameServer.bServer) {
			if (System.currentTimeMillis() - zombie.lastChangeOwner >= 2000L || zombie.authOwner == null) {
				if (SystemDisabler.zombiesSwitchOwnershipEachUpdate && GameServer.getPlayerCount() > 1) {
					int int1;
					if (zombie.authOwner == null) {
						for (int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
							UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
							if (udpConnection != null) {
								this.moveZombie(zombie, udpConnection, (IsoPlayer)null);
								break;
							}
						}
					} else {
						int1 = GameServer.udpEngine.connections.indexOf(zombie.authOwner) + 1;
						for (int int2 = 0; int2 < GameServer.udpEngine.connections.size(); ++int2) {
							UdpConnection udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get((int2 + int1) % GameServer.udpEngine.connections.size());
							if (udpConnection2 != null) {
								this.moveZombie(zombie, udpConnection2, (IsoPlayer)null);
								break;
							}
						}
					}
				} else {
					UdpConnection udpConnection3;
					if (zombie.target instanceof IsoPlayer) {
						udpConnection3 = GameServer.getConnectionFromPlayer((IsoPlayer)zombie.target);
						if (udpConnection3 != null && udpConnection3.isFullyConnected()) {
							float float1 = ((IsoPlayer)zombie.target).getRelevantAndDistance(zombie.x, zombie.y, (float)(udpConnection3.ReleventRange - 2));
							if (!Float.isInfinite(float1)) {
								this.moveZombie(zombie, udpConnection3, (IsoPlayer)zombie.target);
								if (Core.bDebug) {
									DebugLog.log(DebugType.Ownership, String.format("Zombie (%d) owner (\"%s\"): zombie has target", zombie.getOnlineID(), ((IsoPlayer)zombie.target).getUsername()));
								}

								return;
							}
						}
					}

					udpConnection3 = zombie.authOwner;
					IsoPlayer player = zombie.authOwnerPlayer;
					float float2 = Float.POSITIVE_INFINITY;
					if (udpConnection3 != null) {
						float2 = udpConnection3.getRelevantAndDistance(zombie.x, zombie.y, zombie.z);
					}

					int int3;
					UdpConnection udpConnection4;
					IsoPlayer[] playerArray;
					int int4;
					int int5;
					IsoPlayer player2;
					for (int3 = 0; int3 < GameServer.udpEngine.connections.size(); ++int3) {
						udpConnection4 = (UdpConnection)GameServer.udpEngine.connections.get(int3);
						if (udpConnection4 != udpConnection3) {
							playerArray = udpConnection4.players;
							int4 = playerArray.length;
							for (int5 = 0; int5 < int4; ++int5) {
								player2 = playerArray[int5];
								if (player2 != null && player2.isAlive()) {
									float float3 = player2.getRelevantAndDistance(zombie.x, zombie.y, (float)(udpConnection4.ReleventRange - 2));
									if (!Float.isInfinite(float3) && (udpConnection3 == null || float2 > float3 * 1.618034F)) {
										udpConnection3 = udpConnection4;
										float2 = float3;
										player = player2;
									}
								}
							}
						}
					}

					if (Core.bDebug && player != null && player != zombie.authOwnerPlayer) {
						DebugLog.log(DebugType.Ownership, String.format("Zombie (%d) owner (\"%s\"): zombie is closer", zombie.getOnlineID(), player.getUsername()));
					}

					if (udpConnection3 == null && zombie.isReanimatedPlayer()) {
						for (int3 = 0; int3 < GameServer.udpEngine.connections.size(); ++int3) {
							udpConnection4 = (UdpConnection)GameServer.udpEngine.connections.get(int3);
							if (udpConnection4 != udpConnection3) {
								playerArray = udpConnection4.players;
								int4 = playerArray.length;
								for (int5 = 0; int5 < int4; ++int5) {
									player2 = playerArray[int5];
									if (player2 != null && player2.isDead() && player2.ReanimatedCorpse == zombie) {
										udpConnection3 = udpConnection4;
										player = player2;
										if (Core.bDebug) {
											DebugLog.log(DebugType.Ownership, String.format("Zombie (%d) owner (\"%s\"): zombie is reanimated", zombie.getOnlineID(), player2.getUsername()));
										}
									}
								}
							}
						}
					}

					if (udpConnection3 != null && !udpConnection3.RelevantTo(zombie.x, zombie.y, (float)((udpConnection3.ReleventRange - 2) * 10))) {
						udpConnection3 = null;
					}

					this.moveZombie(zombie, udpConnection3, player);
				}
			}
		}
	}

	public void moveZombie(IsoZombie zombie, UdpConnection udpConnection, IsoPlayer player) {
		if (zombie.isDead()) {
			if (zombie.authOwner == null && zombie.authOwnerPlayer == null) {
				zombie.becomeCorpse();
			} else {
				synchronized (this.owns.lock) {
					zombie.authOwner = null;
					zombie.authOwnerPlayer = null;
					zombie.getNetworkCharacterAI().resetSpeedLimiter();
				}

				NetworkZombiePacker.getInstance().setExtraUpdate();
			}

			if (Core.bDebug) {
				DebugLog.log(DebugType.Ownership, String.format("Zombie (%d) owner (\"%s\" / null): zombie is dead", zombie.getOnlineID(), player == null ? "" : player.getUsername()));
			}
		} else {
			if (player != null && player.getVehicle() != null && player.getVehicle().getSpeed2D() > 2.0F && player.getVehicle().getDriver() != player && player.getVehicle().getDriver() instanceof IsoPlayer) {
				player = (IsoPlayer)player.getVehicle().getDriver();
				udpConnection = GameServer.getConnectionFromPlayer(player);
				if (Core.bDebug) {
					DebugLog.log(DebugType.Ownership, String.format("Zombie (%d) owner (\"%s\"): zombie owner is driver", zombie.getOnlineID(), player == null ? "" : player.getUsername()));
				}
			}

			if (zombie.authOwner != udpConnection) {
				synchronized (this.owns.lock) {
					NetworkZombieList.NetworkZombie networkZombie;
					if (zombie.authOwner != null) {
						networkZombie = this.owns.getNetworkZombie(zombie.authOwner);
						if (networkZombie != null && !networkZombie.zombies.remove(zombie)) {
							DebugLog.log("moveZombie: There are no zombies in nz.zombies.");
						}
					}

					if (udpConnection != null) {
						networkZombie = this.owns.getNetworkZombie(udpConnection);
						if (networkZombie != null) {
							networkZombie.zombies.add(zombie);
							zombie.authOwner = udpConnection;
							zombie.authOwnerPlayer = player;
							zombie.getNetworkCharacterAI().resetSpeedLimiter();
							udpConnection.timerSendZombie.reset(0L);
						}
					} else {
						zombie.authOwner = null;
						zombie.authOwnerPlayer = null;
						zombie.getNetworkCharacterAI().resetSpeedLimiter();
					}
				}

				zombie.lastChangeOwner = System.currentTimeMillis();
				NetworkZombiePacker.getInstance().setExtraUpdate();
			}
		}
	}

	public void getZombieAuth(UdpConnection udpConnection, ByteBuffer byteBuffer) {
		NetworkZombieList.NetworkZombie networkZombie = this.owns.getNetworkZombie(udpConnection);
		int int1 = networkZombie.zombies.size();
		int int2 = 0;
		int int3 = byteBuffer.position();
		byteBuffer.putShort((short)int1);
		synchronized (this.owns.lock) {
			networkZombie.zombies.removeIf((var0)->{
				return var0.OnlineID == -1;
			});

			Iterator iterator = networkZombie.zombies.iterator();
			while (true) {
				if (!iterator.hasNext()) {
					break;
				}

				IsoZombie zombie = (IsoZombie)iterator.next();
				if (zombie.OnlineID != -1) {
					byteBuffer.putShort(zombie.OnlineID);
					++int2;
				} else {
					DebugLog.General.error("getZombieAuth: zombie.OnlineID == -1");
				}
			}
		}
		if (int2 < int1) {
			int int4 = byteBuffer.position();
			byteBuffer.position(int3);
			byteBuffer.putShort((short)int2);
			byteBuffer.position(int4);
		}
	}

	public LinkedList getZombieList(UdpConnection udpConnection) {
		NetworkZombieList.NetworkZombie networkZombie = this.owns.getNetworkZombie(udpConnection);
		return networkZombie.zombies;
	}

	public void clearTargetAuth(UdpConnection udpConnection, IsoPlayer player) {
		if (Core.bDebug) {
			DebugLog.log(DebugType.Multiplayer, "Clear zombies target and auth for player id=" + player.getOnlineID());
		}

		if (GameServer.bServer) {
			Iterator iterator = IsoWorld.instance.CurrentCell.getZombieList().iterator();
			while (iterator.hasNext()) {
				IsoZombie zombie = (IsoZombie)iterator.next();
				if (zombie.target == player) {
					zombie.setTarget((IsoMovingObject)null);
				}

				if (zombie.authOwner == udpConnection) {
					zombie.authOwner = null;
					zombie.authOwnerPlayer = null;
					zombie.getNetworkCharacterAI().resetSpeedLimiter();
					getInstance().updateAuth(zombie);
				}
			}
		}
	}

	public static void removeZombies(UdpConnection udpConnection) {
		int int1 = (IsoChunkMap.ChunkGridWidth / 2 + 2) * 10;
		IsoPlayer[] playerArray = udpConnection.players;
		int int2 = playerArray.length;
		for (int int3 = 0; int3 < int2; ++int3) {
			IsoPlayer player = playerArray[int3];
			if (player != null) {
				int int4 = (int)player.getX();
				int int5 = (int)player.getY();
				for (int int6 = 0; int6 < 8; ++int6) {
					for (int int7 = int5 - int1; int7 <= int5 + int1; ++int7) {
						for (int int8 = int4 - int1; int8 <= int4 + int1; ++int8) {
							IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int8, int7, int6);
							if (square != null && !square.getMovingObjects().isEmpty()) {
								for (int int9 = square.getMovingObjects().size() - 1; int9 >= 0; --int9) {
									IsoZombie zombie = (IsoZombie)Type.tryCastTo((IsoMovingObject)square.getMovingObjects().get(int9), IsoZombie.class);
									if (zombie != null) {
										NetworkZombiePacker.getInstance().deleteZombie(zombie);
										zombie.removeFromWorld();
										zombie.removeFromSquare();
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void recheck(UdpConnection udpConnection) {
		synchronized (this.owns.lock) {
			NetworkZombieList.NetworkZombie networkZombie = this.owns.getNetworkZombie(udpConnection);
			if (networkZombie != null) {
				networkZombie.zombies.removeIf((udpConnectionx)->{
					return udpConnectionx.authOwner != udpConnection;
				});
			}
		}
	}
}
