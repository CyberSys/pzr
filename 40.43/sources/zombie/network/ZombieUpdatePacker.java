package zombie.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;
import zombie.VirtualZombieManager;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.sprite.IsoAnim;


public class ZombieUpdatePacker {
	public static ZombieUpdatePacker instance = new ZombieUpdatePacker();
	public static final int ZombieMaxRangeToPlayer = 70;
	public ZombieUpdatePacker.PlayerZombiePackInfo[] packInfo = new ZombieUpdatePacker.PlayerZombiePackInfo[512];
	public static int ZombiePacketsSentThisTime = 0;
	static final int ZOMBIE_UPDATE_SIZE = 21;
	static Vector2 tempo = new Vector2();
	private static final boolean SendZombieState = false;
	private ArrayList nearest = new ArrayList();
	private final float NEAR_DIST = 4.0F;

	public void addZombieToPacker(IsoZombie zombie) {
		if (zombie.OnlineID != -1) {
			if (zombie.legsSprite.CurrentAnim.name.contains("Stagger")) {
				boolean boolean1 = false;
			}

			for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				if (udpConnection.isFullyConnected()) {
					double double1 = ServerOptions.instance.ZombieUpdateRadiusLowPriority.getValue();
					if (double1 == 0.0) {
						if (udpConnection.ReleventTo(zombie.x, zombie.y)) {
							this.doAddZombie(zombie, udpConnection);
						}
					} else if (udpConnection.ReleventToPlayers((double)zombie.x, (double)zombie.y, double1)) {
						this.doAddZombie(zombie, udpConnection);
					}
				}
			}
		}
	}

	private void doAddZombie(IsoZombie zombie, UdpConnection udpConnection) {
		int int1 = udpConnection.index;
		if (this.packInfo[int1] == null) {
			this.packInfo[int1] = new ZombieUpdatePacker.PlayerZombiePackInfo();
		}

		if (!this.packInfo[int1].zombies.contains(zombie)) {
			this.packInfo[int1].zombies.add(zombie);
			this.packInfo[int1].guid = udpConnection.getConnectedGUID();
		}
	}

	public void clearZombies() {
		int int1 = GameServer.udpEngine.getMaxConnections();
		for (int int2 = 0; int2 < int1; ++int2) {
			if (this.packInfo[int2] != null) {
				this.packInfo[int2].zombies.clear();
			}
		}
	}

	public void packZombiesIntoPackets() {
		if (!GameServer.bFastForward) {
			this.addZombies();
			ZombiePacketsSentThisTime = 0;
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
							this.addZombies(byteBufferWriter, this.packInfo[int2]);
							udpConnection.endPacketSuperHighUnreliable();
							++ZombiePacketsSentThisTime;
						}
					}
				}
			}
		}
	}

	public void updateZombiesFromPacket(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		for (short short2 = 0; short2 < short1; ++short2) {
			short short3 = byteBuffer.getShort();
			float float1 = byteBuffer.getFloat();
			float float2 = byteBuffer.getFloat();
			float float3 = byteBuffer.getFloat();
			float float4 = float1;
			float float5 = float2;
			byte byte1 = byteBuffer.get();
			byte byte2 = byteBuffer.get();
			byte byte3 = byteBuffer.get();
			byte byte4 = byteBuffer.get();
			byte byte5 = byteBuffer.get();
			boolean boolean1 = (byte5 & 1) != 0;
			boolean boolean2 = (byte5 & 2) != 0;
			boolean boolean3 = (byte5 & 4) != 0;
			boolean boolean4 = (byte5 & 8) != 0;
			int int1 = byte5 >> 4 & 7;
			boolean boolean5 = (byte5 & 128) != 0;
			short short4 = byteBuffer.getShort();
			byte byte6 = (byte)(byte2 >> 2);
			boolean boolean6 = (byte2 & 2) != 0;
			boolean boolean7 = (byte2 & 1) != 0;
			Object object = null;
			boolean boolean8 = false;
			try {
				IsoZombie zombie = (IsoZombie)GameClient.IDToZombieMap.get(short3);
				if (zombie == null) {
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float4, (double)float5, (double)float3);
					if (square != null) {
						VirtualZombieManager.instance.choices.clear();
						VirtualZombieManager.instance.choices.add(square);
						zombie = VirtualZombieManager.instance.createRealZombieAlways(short4, 0, false);
						if (zombie != null) {
							zombie.setFakeDead(false);
							zombie.OnlineID = short3;
							GameClient.IDToZombieMap.put(short3, zombie);
							zombie.bx = float4;
							zombie.by = float5;
							for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
								IsoPlayer player = IsoPlayer.players[int2];
								if (square.isCanSee(int2)) {
									zombie.alpha[int2] = zombie.targetAlpha[int2] = 1.0F;
								}

								if (player != null && player.ReanimatedCorpseID == short3) {
									player.ReanimatedCorpseID = -1;
									player.ReanimatedCorpse = zombie;
								}
							}
						}

						zombie.serverState = (String)object;
						boolean8 = true;
					}

					if (zombie == null) {
						continue;
					}
				}

				IsoAnim anim = (IsoAnim)zombie.legsSprite.AnimStack.get(byte1);
				if (anim != null && (anim.equals("ZombieDeath") || anim.equals("ZombieStaggerBack") || anim.equals("ZombieGetUp")) && boolean8) {
					GameClient.instance.RecentlyDied.add(short3);
					VirtualZombieManager.instance.removeZombieFromWorld(zombie);
				} else {
					zombie.PlayAnim(anim.name);
					zombie.setDir(byte3);
					zombie.angle.set(zombie.dir.ToVector());
					tempo.x = float4 - zombie.bx;
					tempo.y = float5 - zombie.by;
					zombie.reqMovement.x = tempo.x;
					zombie.reqMovement.y = tempo.y;
					zombie.reqMovement.normalize();
					float float6 = tempo.getLength() / 5.0F;
					float6 = Math.max(float6, 0.1F);
					for (int int3 = 0; int3 < IsoPlayer.numPlayers; ++int3) {
						IsoPlayer player2 = IsoPlayer.players[int3];
						if (player2 != null && !player2.isDead() && (int)player2.z == (int)zombie.z) {
							float float7 = IsoUtils.DistanceToSquared(float4, float5, player2.x, player2.y);
							if (float7 < 16.0F) {
								float6 *= Math.max((1.0F - float7 / 16.0F) * 4.5F, 1.0F);
								break;
							}
						}
					}

					zombie.setBlendSpeed(float6);
					zombie.lastRemoteUpdate = 0;
					zombie.setX(float4);
					zombie.setY(float5);
					zombie.setZ(float3);
					zombie.setLx(float4);
					zombie.setLy(float5);
					zombie.def.Finished = boolean7;
					zombie.def.Frame = (float)byte6;
					zombie.setOnFloor(boolean1);
					zombie.bCrawling = boolean2;
					zombie.setIgnoreMovementForDirection(boolean3);
					zombie.def.AnimFrameIncrease = (float)byte4 / 128.0F;
					zombie.def.Looped = boolean6;
					zombie.thumpFlag = int1;
					zombie.mpIdleSound = boolean5;
					if (boolean4) {
						zombie.SetOnFire();
					} else {
						zombie.StopBurning();
					}

					if (!IsoWorld.instance.CurrentCell.getZombieList().contains(zombie)) {
						IsoWorld.instance.CurrentCell.getZombieList().add(zombie);
					}

					if (!IsoWorld.instance.CurrentCell.getObjectList().contains(zombie)) {
						IsoWorld.instance.CurrentCell.getObjectList().add(zombie);
					}

					zombie.serverState = (String)object;
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	private void addZombies(ByteBufferWriter byteBufferWriter, ZombieUpdatePacker.PlayerZombiePackInfo playerZombiePackInfo) {
		int int1 = byteBufferWriter.bb.remaining() / 21;
		if (int1 > playerZombiePackInfo.zombies.size()) {
			int1 = playerZombiePackInfo.zombies.size();
		}

		byteBufferWriter.putShort((short)int1);
		for (int int2 = 0; int2 < int1; ++int2) {
			long long1 = (long)byteBufferWriter.bb.position();
			IsoZombie zombie = (IsoZombie)playerZombiePackInfo.zombies.pop();
			byteBufferWriter.putShort(zombie.OnlineID);
			byte byte1 = (byte)zombie.legsSprite.AnimStack.indexOf(zombie.legsSprite.CurrentAnim);
			byte byte2 = (byte)((int)zombie.def.Frame);
			byte byte3 = (byte)(zombie.def.Finished ? 1 : 0);
			byte byte4 = (byte)(zombie.def.Looped ? 1 : 0);
			byte byte5 = (byte)(byte3 | byte4 << 1 | byte2 << 2);
			byte byte6 = (byte)(zombie.isOnFloor() ? 1 : 0);
			byte byte7 = (byte)(zombie.bCrawling ? 1 : 0);
			byte byte8 = (byte)(zombie.IgnoreMovementForDirection ? 1 : 0);
			byte byte9 = (byte)(zombie.isOnFire() ? 1 : 0);
			byte byte10 = (byte)zombie.thumpFlag;
			byte byte11 = (byte)(zombie.mpIdleSound ? 1 : 0);
			zombie.thumpSent = true;
			byte byte12 = (byte)(byte6 | byte7 << 1 | byte8 << 2 | byte9 << 3 | byte10 << 4 | byte11 << 7);
			byteBufferWriter.putFloat(zombie.x);
			byteBufferWriter.putFloat(zombie.y);
			byteBufferWriter.putFloat(zombie.z);
			byteBufferWriter.putByte(byte1);
			byteBufferWriter.putByte(byte5);
			byteBufferWriter.putByte((byte)zombie.dir.index());
			byteBufferWriter.putByte((byte)((int)(zombie.def.AnimFrameIncrease * 128.0F)));
			byteBufferWriter.putByte(byte12);
			byteBufferWriter.putShort((short)zombie.getDescriptor().getID());
			assert (long)byteBufferWriter.bb.position() - long1 == 21L;
		}
	}

	private void addZombies() {
		for (int int1 = 0; int1 < GameServer.Players.size(); ++int1) {
			IsoPlayer player = (IsoPlayer)GameServer.Players.get(int1);
			UdpConnection udpConnection = GameServer.getConnectionFromPlayer(player);
			if (udpConnection != null && udpConnection.isFullyConnected()) {
				player.zombiesToSend.update();
				for (int int2 = 0; int2 < 300; ++int2) {
					IsoZombie zombie = player.zombiesToSend.getZombie(int2);
					if (zombie == null) {
						break;
					}

					this.doAddZombie(zombie, udpConnection);
				}
			}
		}
	}

	private static class NearestComparator implements Comparator {
		public static ZombieUpdatePacker.NearestComparator instance = new ZombieUpdatePacker.NearestComparator();
		public IsoMovingObject testPlayer;

		public void init(IsoMovingObject movingObject) {
			this.testPlayer = movingObject;
		}

		public int compare(IsoZombie zombie, IsoZombie zombie2) {
			float float1 = zombie.DistToSquared(this.testPlayer);
			float float2 = zombie2.DistToSquared(this.testPlayer);
			if (float1 < float2) {
				return -1;
			} else {
				return float1 > float2 ? 1 : 0;
			}
		}
	}

	public class PlayerZombiePackInfo {
		public Stack zombies = new Stack();
		public long guid;
	}
}
