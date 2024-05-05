package zombie;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.ZombiesZoneDefinition;
import zombie.characters.action.ActionGroup;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaChunk;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoFireManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.popman.NetworkZombieSimulator;
import zombie.popman.ZombiePopulationManager;
import zombie.vehicles.PolygonalMap2;


public final class VirtualZombieManager {
	private final ArrayDeque ReusableZombies = new ArrayDeque();
	private final HashSet ReusableZombieSet = new HashSet();
	private final ArrayList ReusedThisFrame = new ArrayList();
	private final ArrayList RecentlyRemoved = new ArrayList();
	public static VirtualZombieManager instance = new VirtualZombieManager();
	public int MaxRealZombies = 1;
	private final ArrayList m_tempZombies = new ArrayList();
	public final ArrayList choices = new ArrayList();
	private final ArrayList bestchoices = new ArrayList();
	HandWeapon w = null;

	public boolean removeZombieFromWorld(IsoZombie zombie) {
		boolean boolean1 = zombie.getCurrentSquare() != null;
		zombie.getEmitter().unregister();
		zombie.removeFromWorld();
		zombie.removeFromSquare();
		return boolean1;
	}

	private void reuseZombie(IsoZombie zombie) {
		if (zombie != null) {
			assert !IsoWorld.instance.CurrentCell.getObjectList().contains(zombie);
			assert !IsoWorld.instance.CurrentCell.getZombieList().contains(zombie);
			assert zombie.getCurrentSquare() == null || !zombie.getCurrentSquare().getMovingObjects().contains(zombie);
			if (!this.isReused(zombie)) {
				NetworkZombieSimulator.getInstance().remove(zombie);
				zombie.resetForReuse();
				this.addToReusable(zombie);
			}
		}
	}

	public void addToReusable(IsoZombie zombie) {
		if (zombie != null && !this.ReusableZombieSet.contains(zombie)) {
			this.ReusableZombies.addLast(zombie);
			this.ReusableZombieSet.add(zombie);
		}
	}

	public boolean isReused(IsoZombie zombie) {
		return this.ReusableZombieSet.contains(zombie);
	}

	public void init() {
		if (!GameClient.bClient) {
			IsoZombie zombie = null;
			if (!IsoWorld.getZombiesDisabled()) {
				for (int int1 = 0; int1 < this.MaxRealZombies; ++int1) {
					zombie = new IsoZombie(IsoWorld.instance.CurrentCell);
					zombie.getEmitter().unregister();
					this.addToReusable(zombie);
				}
			}
		}
	}

	public void Reset() {
		Iterator iterator = this.ReusedThisFrame.iterator();
		while (iterator.hasNext()) {
			IsoZombie zombie = (IsoZombie)iterator.next();
			if (zombie.vocalEvent != 0L) {
				zombie.getEmitter().stopSound(zombie.vocalEvent);
				zombie.vocalEvent = 0L;
			}
		}

		this.bestchoices.clear();
		this.choices.clear();
		this.RecentlyRemoved.clear();
		this.ReusableZombies.clear();
		this.ReusableZombieSet.clear();
		this.ReusedThisFrame.clear();
	}

	public void update() {
		long long1 = System.currentTimeMillis();
		int int1;
		IsoZombie zombie;
		for (int1 = this.RecentlyRemoved.size() - 1; int1 >= 0; --int1) {
			zombie = (IsoZombie)this.RecentlyRemoved.get(int1);
			zombie.updateEmitter();
			if (long1 - zombie.removedFromWorldMS > 5000L) {
				if (zombie.vocalEvent != 0L) {
					zombie.getEmitter().stopSound(zombie.vocalEvent);
					zombie.vocalEvent = 0L;
				}

				zombie.getEmitter().stopAll();
				this.RecentlyRemoved.remove(int1);
				this.ReusedThisFrame.add(zombie);
			}
		}

		if (!GameClient.bClient && !GameServer.bServer) {
			for (int1 = 0; int1 < IsoWorld.instance.CurrentCell.getZombieList().size(); ++int1) {
				zombie = (IsoZombie)IsoWorld.instance.CurrentCell.getZombieList().get(int1);
				if (!zombie.KeepItReal && zombie.getCurrentSquare() == null) {
					zombie.removeFromWorld();
					zombie.removeFromSquare();
					assert this.ReusedThisFrame.contains(zombie);
					assert !IsoWorld.instance.CurrentCell.getZombieList().contains(zombie);
					--int1;
				}
			}

			for (int1 = 0; int1 < this.ReusedThisFrame.size(); ++int1) {
				zombie = (IsoZombie)this.ReusedThisFrame.get(int1);
				this.reuseZombie(zombie);
			}

			this.ReusedThisFrame.clear();
		} else {
			for (int1 = 0; int1 < this.ReusedThisFrame.size(); ++int1) {
				zombie = (IsoZombie)this.ReusedThisFrame.get(int1);
				this.reuseZombie(zombie);
			}

			this.ReusedThisFrame.clear();
		}
	}

	public IsoZombie createRealZombieAlways(int int1, boolean boolean1) {
		return this.createRealZombieAlways(int1, boolean1, 0);
	}

	public IsoZombie createRealZombieAlways(int int1, int int2, boolean boolean1) {
		int int3 = PersistentOutfits.instance.getOutfit(int1);
		return this.createRealZombieAlways(int2, boolean1, int3);
	}

	public IsoZombie createRealZombieAlways(int int1, boolean boolean1, int int2) {
		IsoZombie zombie = null;
		if (!SystemDisabler.doZombieCreation) {
			return null;
		} else if (this.choices != null && !this.choices.isEmpty()) {
			IsoGridSquare square = (IsoGridSquare)this.choices.get(Rand.Next(this.choices.size()));
			if (square == null) {
				return null;
			} else {
				if (this.w == null) {
					this.w = (HandWeapon)InventoryItemFactory.CreateItem("Base.Axe");
				}

				if ((GameServer.bServer || GameClient.bClient) && int2 == 0) {
					int2 = ZombiesZoneDefinition.pickPersistentOutfit(square);
				}

				if (this.ReusableZombies.isEmpty()) {
					zombie = new IsoZombie(IsoWorld.instance.CurrentCell);
					zombie.bDressInRandomOutfit = int2 == 0;
					zombie.setPersistentOutfitID(int2);
					IsoWorld.instance.CurrentCell.getObjectList().add(zombie);
				} else {
					zombie = (IsoZombie)this.ReusableZombies.removeFirst();
					this.ReusableZombieSet.remove(zombie);
					zombie.getHumanVisual().clear();
					zombie.clearAttachedItems();
					zombie.clearItemsToSpawnAtDeath();
					zombie.bDressInRandomOutfit = int2 == 0;
					zombie.setPersistentOutfitID(int2);
					zombie.setSitAgainstWall(false);
					zombie.setOnDeathDone(false);
					zombie.setOnKillDone(false);
					zombie.setDoDeathSound(true);
					zombie.setHitTime(0);
					zombie.setFallOnFront(false);
					zombie.setFakeDead(false);
					zombie.setReanimatedPlayer(false);
					zombie.setStateMachineLocked(false);
					Vector2 vector2 = zombie.dir.ToVector();
					vector2.x += (float)Rand.Next(200) / 100.0F - 0.5F;
					vector2.y += (float)Rand.Next(200) / 100.0F - 0.5F;
					vector2.normalize();
					zombie.setForwardDirection(vector2);
					IsoWorld.instance.CurrentCell.getObjectList().add(zombie);
					zombie.walkVariant = "ZombieWalk";
					zombie.DoZombieStats();
					if (zombie.isOnFire()) {
						IsoFireManager.RemoveBurningCharacter(zombie);
						zombie.setOnFire(false);
					}

					if (zombie.AttachedAnimSprite != null) {
						zombie.AttachedAnimSprite.clear();
					}

					zombie.thumpFlag = 0;
					zombie.thumpSent = false;
					zombie.mpIdleSound = false;
					zombie.soundSourceTarget = null;
					zombie.soundAttract = 0.0F;
					zombie.soundAttractTimeout = 0.0F;
					zombie.bodyToEat = null;
					zombie.eatBodyTarget = null;
					zombie.atlasTex = null;
					zombie.clearVariables();
					zombie.setStaggerBack(false);
					zombie.setKnockedDown(false);
					zombie.setKnifeDeath(false);
					zombie.setJawStabAttach(false);
					zombie.setCrawler(false);
					zombie.initializeStates();
					zombie.actionContext.setGroup(ActionGroup.getActionGroup("zombie"));
					zombie.advancedAnimator.OnAnimDataChanged(false);
					zombie.setDefaultState();
					zombie.getAnimationPlayer().resetBoneModelTransforms();
				}

				zombie.dir = IsoDirections.fromIndex(int1);
				zombie.setForwardDirection(zombie.dir.ToVector());
				zombie.getInventory().setExplored(false);
				if (boolean1) {
					zombie.bDressInRandomOutfit = true;
				}

				zombie.target = null;
				zombie.TimeSinceSeenFlesh = 100000.0F;
				if (!zombie.isFakeDead()) {
					if (SandboxOptions.instance.Lore.Toughness.getValue() == 1) {
						zombie.setHealth(3.5F + Rand.Next(0.0F, 0.3F));
					}

					if (SandboxOptions.instance.Lore.Toughness.getValue() == 2) {
						zombie.setHealth(1.5F + Rand.Next(0.0F, 0.3F));
					}

					if (SandboxOptions.instance.Lore.Toughness.getValue() == 3) {
						zombie.setHealth(0.5F + Rand.Next(0.0F, 0.3F));
					}

					if (SandboxOptions.instance.Lore.Toughness.getValue() == 4) {
						zombie.setHealth(Rand.Next(0.5F, 3.5F) + Rand.Next(0.0F, 0.3F));
					}
				} else {
					zombie.setHealth(0.5F + Rand.Next(0.0F, 0.3F));
				}

				float float1 = (float)Rand.Next(0, 1000);
				float float2 = (float)Rand.Next(0, 1000);
				float1 /= 1000.0F;
				float2 /= 1000.0F;
				float1 += (float)square.getX();
				float2 += (float)square.getY();
				zombie.setCurrent(square);
				zombie.setMovingSquareNow();
				zombie.setX(float1);
				zombie.setY(float2);
				zombie.setZ((float)square.getZ());
				if ((GameClient.bClient || GameServer.bServer) && zombie.networkAI != null) {
					zombie.networkAI.reset();
				}

				zombie.upKillCount = true;
				if (boolean1) {
					zombie.setDir(IsoDirections.fromIndex(Rand.Next(8)));
					zombie.setForwardDirection(zombie.dir.ToVector());
					zombie.setFakeDead(false);
					zombie.setHealth(0.0F);
					zombie.upKillCount = false;
					zombie.DoZombieInventory();
					new IsoDeadBody(zombie, true);
					return zombie;
				} else {
					synchronized (IsoWorld.instance.CurrentCell.getZombieList()) {
						zombie.getEmitter().register();
						IsoWorld.instance.CurrentCell.getZombieList().add(zombie);
						if (GameClient.bClient) {
							zombie.bRemote = true;
						}

						if (GameServer.bServer) {
							zombie.OnlineID = ServerMap.instance.getUniqueZombieId();
							if (zombie.OnlineID == -1) {
								IsoWorld.instance.CurrentCell.getZombieList().remove(zombie);
								IsoWorld.instance.CurrentCell.getObjectList().remove(zombie);
								this.ReusedThisFrame.add(zombie);
								return null;
							}

							ServerMap.instance.ZombieMap.put(zombie.OnlineID, zombie);
						}

						return zombie;
					}
				}
			}
		} else {
			return null;
		}
	}

	private IsoGridSquare pickEatingZombieSquare(float float1, float float2, float float3, float float4, int int1) {
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float3, (double)float4, (double)int1);
		if (square != null && this.canSpawnAt(square.x, square.y, square.z) && !square.HasStairs()) {
			return PolygonalMap2.instance.lineClearCollide(float1, float2, float3, float4, int1, (IsoMovingObject)null, false, true) ? null : square;
		} else {
			return null;
		}
	}

	public void createEatingZombies(IsoDeadBody deadBody, int int1) {
		if (!IsoWorld.getZombiesDisabled()) {
			for (int int2 = 0; int2 < int1; ++int2) {
				float float1 = deadBody.x;
				float float2 = deadBody.y;
				switch (int2) {
				case 0: 
					float1 -= 0.5F;
					break;
				
				case 1: 
					float1 += 0.5F;
					break;
				
				case 2: 
					float2 -= 0.5F;
					break;
				
				case 3: 
					float2 += 0.5F;
				
				}

				IsoGridSquare square = this.pickEatingZombieSquare(deadBody.x, deadBody.y, float1, float2, (int)deadBody.z);
				if (square != null) {
					this.choices.clear();
					this.choices.add(square);
					IsoZombie zombie = this.createRealZombieAlways(1, false);
					if (zombie != null) {
						ZombieSpawnRecorder.instance.record(zombie, "createEatingZombies");
						zombie.bDressInRandomOutfit = true;
						zombie.setX(float1);
						zombie.setY(float2);
						zombie.setZ(deadBody.z);
						zombie.faceLocationF(deadBody.x, deadBody.y);
						zombie.setEatBodyTarget(deadBody, true);
					}
				}
			}
		}
	}

	private IsoZombie createRealZombie(int int1, boolean boolean1) {
		return GameClient.bClient ? null : this.createRealZombieAlways(int1, boolean1);
	}

	public void AddBloodToMap(int int1, IsoChunk chunk) {
		for (int int2 = 0; int2 < int1; ++int2) {
			IsoGridSquare square = null;
			int int3 = 0;
			int int4;
			do {
				int int5 = Rand.Next(10);
				int4 = Rand.Next(10);
				square = chunk.getGridSquare(int5, int4, 0);
				++int3;
			}	 while (int3 < 100 && (square == null || !square.isFree(false)));

			if (square != null) {
				byte byte1 = 5;
				if (Rand.Next(10) == 0) {
					byte1 = 10;
				}

				if (Rand.Next(40) == 0) {
					byte1 = 20;
				}

				for (int4 = 0; int4 < byte1; ++int4) {
					float float1 = (float)Rand.Next(3000) / 1000.0F;
					float float2 = (float)Rand.Next(3000) / 1000.0F;
					--float1;
					--float2;
					chunk.addBloodSplat((float)square.getX() + float1, (float)square.getY() + float2, (float)square.getZ(), Rand.Next(12) + 8);
				}
			}
		}
	}

	public ArrayList addZombiesToMap(int int1, RoomDef roomDef) {
		return this.addZombiesToMap(int1, roomDef, true);
	}

	public ArrayList addZombiesToMap(int int1, RoomDef roomDef, boolean boolean1) {
		ArrayList arrayList = new ArrayList();
		if ("Tutorial".equals(Core.GameMode)) {
			return arrayList;
		} else {
			this.choices.clear();
			this.bestchoices.clear();
			IsoGridSquare square = null;
			int int2;
			int int3;
			for (int2 = 0; int2 < roomDef.rects.size(); ++int2) {
				int3 = roomDef.level;
				RoomDef.RoomRect roomRect = (RoomDef.RoomRect)roomDef.rects.get(int2);
				for (int int4 = roomRect.x; int4 < roomRect.getX2(); ++int4) {
					for (int int5 = roomRect.y; int5 < roomRect.getY2(); ++int5) {
						square = IsoWorld.instance.CurrentCell.getGridSquare(int4, int5, int3);
						if (square != null && this.canSpawnAt(int4, int5, int3)) {
							this.choices.add(square);
							boolean boolean2 = false;
							for (int int6 = 0; int6 < IsoPlayer.numPlayers; ++int6) {
								if (IsoPlayer.players[int6] != null && square.isSeen(int6)) {
									boolean2 = true;
								}
							}

							if (!boolean2) {
								this.bestchoices.add(square);
							}
						}
					}
				}
			}

			int1 = Math.min(int1, this.choices.size());
			if (!this.bestchoices.isEmpty()) {
				this.choices.addAll(this.bestchoices);
				this.choices.addAll(this.bestchoices);
			}

			for (int2 = 0; int2 < int1; ++int2) {
				if (!this.choices.isEmpty()) {
					roomDef.building.bAlarmed = false;
					int3 = Rand.Next(8);
					byte byte1 = 4;
					IsoZombie zombie = this.createRealZombie(int3, boolean1 ? Rand.Next(byte1) == 0 : false);
					if (zombie != null && zombie.getSquare() != null) {
						if (!GameServer.bServer) {
							zombie.bDressInRandomOutfit = true;
						}

						zombie.setX((float)((int)zombie.getX()) + (float)Rand.Next(2, 8) / 10.0F);
						zombie.setY((float)((int)zombie.getY()) + (float)Rand.Next(2, 8) / 10.0F);
						this.choices.remove(zombie.getSquare());
						this.choices.remove(zombie.getSquare());
						this.choices.remove(zombie.getSquare());
						arrayList.add(zombie);
					}
				} else {
					System.out.println("No choices for zombie.");
				}
			}

			this.bestchoices.clear();
			this.choices.clear();
			return arrayList;
		}
	}

	public void tryAddIndoorZombies(RoomDef roomDef, boolean boolean1) {
	}

	private void addIndoorZombies(int int1, RoomDef roomDef, boolean boolean1) {
		this.choices.clear();
		this.bestchoices.clear();
		IsoGridSquare square = null;
		int int2;
		int int3;
		for (int2 = 0; int2 < roomDef.rects.size(); ++int2) {
			int3 = roomDef.level;
			RoomDef.RoomRect roomRect = (RoomDef.RoomRect)roomDef.rects.get(int2);
			for (int int4 = roomRect.x; int4 < roomRect.getX2(); ++int4) {
				for (int int5 = roomRect.y; int5 < roomRect.getY2(); ++int5) {
					square = IsoWorld.instance.CurrentCell.getGridSquare(int4, int5, int3);
					if (square != null && this.canSpawnAt(int4, int5, int3)) {
						this.choices.add(square);
					}
				}
			}
		}

		int1 = Math.min(int1, this.choices.size());
		if (!this.bestchoices.isEmpty()) {
			this.choices.addAll(this.bestchoices);
			this.choices.addAll(this.bestchoices);
		}

		for (int2 = 0; int2 < int1; ++int2) {
			if (!this.choices.isEmpty()) {
				roomDef.building.bAlarmed = false;
				int3 = Rand.Next(8);
				byte byte1 = 4;
				IsoZombie zombie = this.createRealZombie(int3, boolean1 ? Rand.Next(byte1) == 0 : false);
				if (zombie != null && zombie.getSquare() != null) {
					ZombieSpawnRecorder.instance.record(zombie, "addIndoorZombies");
					zombie.bIndoorZombie = true;
					zombie.setX((float)((int)zombie.getX()) + (float)Rand.Next(2, 8) / 10.0F);
					zombie.setY((float)((int)zombie.getY()) + (float)Rand.Next(2, 8) / 10.0F);
					this.choices.remove(zombie.getSquare());
					this.choices.remove(zombie.getSquare());
					this.choices.remove(zombie.getSquare());
				}
			} else {
				System.out.println("No choices for zombie.");
			}
		}

		this.bestchoices.clear();
		this.choices.clear();
	}

	public void addIndoorZombiesToChunk(IsoChunk chunk, IsoRoom room, int int1, ArrayList arrayList) {
		if (int1 > 0) {
			float float1 = room.getRoomDef().getAreaOverlapping(chunk);
			int int2 = (int)Math.ceil((double)((float)int1 * float1));
			if (int2 > 0) {
				this.choices.clear();
				int int3 = room.def.level;
				int int4;
				for (int4 = 0; int4 < room.rects.size(); ++int4) {
					RoomDef.RoomRect roomRect = (RoomDef.RoomRect)room.rects.get(int4);
					int int5 = Math.max(chunk.wx * 10, roomRect.x);
					int int6 = Math.max(chunk.wy * 10, roomRect.y);
					int int7 = Math.min((chunk.wx + 1) * 10, roomRect.x + roomRect.w);
					int int8 = Math.min((chunk.wy + 1) * 10, roomRect.y + roomRect.h);
					for (int int9 = int5; int9 < int7; ++int9) {
						for (int int10 = int6; int10 < int8; ++int10) {
							IsoGridSquare square = chunk.getGridSquare(int9 - chunk.wx * 10, int10 - chunk.wy * 10, int3);
							if (square != null && this.canSpawnAt(int9, int10, int3)) {
								this.choices.add(square);
							}
						}
					}
				}

				if (!this.choices.isEmpty()) {
					room.def.building.bAlarmed = false;
					int2 = Math.min(int2, this.choices.size());
					for (int4 = 0; int4 < int2; ++int4) {
						IsoZombie zombie = this.createRealZombie(Rand.Next(8), false);
						if (zombie != null && zombie.getSquare() != null) {
							if (!GameServer.bServer) {
								zombie.bDressInRandomOutfit = true;
							}

							zombie.setX((float)((int)zombie.getX()) + (float)Rand.Next(2, 8) / 10.0F);
							zombie.setY((float)((int)zombie.getY()) + (float)Rand.Next(2, 8) / 10.0F);
							this.choices.remove(zombie.getSquare());
							arrayList.add(zombie);
						}
					}

					this.choices.clear();
				}
			}
		}
	}

	public void addIndoorZombiesToChunk(IsoChunk chunk, IsoRoom room) {
		if (room.def.spawnCount == -1) {
			room.def.spawnCount = this.getZombieCountForRoom(room);
		}

		this.m_tempZombies.clear();
		this.addIndoorZombiesToChunk(chunk, room, room.def.spawnCount, this.m_tempZombies);
		ZombieSpawnRecorder.instance.record(this.m_tempZombies, "addIndoorZombiesToChunk");
	}

	public void addDeadZombiesToMap(int int1, RoomDef roomDef) {
		boolean boolean1 = false;
		this.choices.clear();
		this.bestchoices.clear();
		IsoGridSquare square = null;
		int int2;
		int int3;
		for (int2 = 0; int2 < roomDef.rects.size(); ++int2) {
			int3 = roomDef.level;
			RoomDef.RoomRect roomRect = (RoomDef.RoomRect)roomDef.rects.get(int2);
			for (int int4 = roomRect.x; int4 < roomRect.getX2(); ++int4) {
				for (int int5 = roomRect.y; int5 < roomRect.getY2(); ++int5) {
					square = IsoWorld.instance.CurrentCell.getGridSquare(int4, int5, int3);
					if (square != null && square.isFree(false)) {
						this.choices.add(square);
						if (!GameServer.bServer) {
							boolean boolean2 = false;
							for (int int6 = 0; int6 < IsoPlayer.numPlayers; ++int6) {
								if (IsoPlayer.players[int6] != null && square.isSeen(int6)) {
									boolean2 = true;
								}
							}

							if (!boolean2) {
								this.bestchoices.add(square);
							}
						}
					}
				}
			}
		}

		int1 = Math.min(int1, this.choices.size());
		if (!this.bestchoices.isEmpty()) {
			this.choices.addAll(this.bestchoices);
			this.choices.addAll(this.bestchoices);
		}

		for (int2 = 0; int2 < int1; ++int2) {
			if (!this.choices.isEmpty()) {
				int3 = Rand.Next(8);
				this.createRealZombie(int3, true);
			}
		}

		this.bestchoices.clear();
		this.choices.clear();
	}

	public void RemoveZombie(IsoZombie zombie) {
		if (zombie.isReanimatedPlayer()) {
			if (zombie.vocalEvent != 0L) {
				zombie.getEmitter().stopSound(zombie.vocalEvent);
				zombie.vocalEvent = 0L;
			}

			ReanimatedPlayers.instance.removeReanimatedPlayerFromWorld(zombie);
		} else {
			if (zombie.isDead()) {
				if (!this.RecentlyRemoved.contains(zombie)) {
					zombie.removedFromWorldMS = System.currentTimeMillis();
					this.RecentlyRemoved.add(zombie);
				}
			} else if (!this.ReusedThisFrame.contains(zombie)) {
				this.ReusedThisFrame.add(zombie);
			}
		}
	}

	public void createHordeFromTo(float float1, float float2, float float3, float float4, int int1) {
		ZombiePopulationManager.instance.createHordeFromTo((int)float1, (int)float2, (int)float3, (int)float4, int1);
	}

	public IsoZombie createRealZombie(float float1, float float2, float float3) {
		this.choices.clear();
		this.choices.add(IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, (double)float3));
		if (!this.choices.isEmpty()) {
			int int1 = Rand.Next(8);
			return this.createRealZombie(int1, true);
		} else {
			return null;
		}
	}

	public IsoZombie createRealZombieNow(float float1, float float2, float float3) {
		this.choices.clear();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, (double)float3);
		if (square == null) {
			return null;
		} else {
			this.choices.add(square);
			if (!this.choices.isEmpty()) {
				int int1 = Rand.Next(8);
				return this.createRealZombie(int1, false);
			} else {
				return null;
			}
		}
	}

	private int getZombieCountForRoom(IsoRoom room) {
		if (IsoWorld.getZombiesDisabled()) {
			return 0;
		} else if (GameClient.bClient) {
			return 0;
		} else if (Core.bLastStand) {
			return 0;
		} else {
			int int1 = 7;
			if (SandboxOptions.instance.Zombies.getValue() == 1) {
				int1 = 3;
			} else if (SandboxOptions.instance.Zombies.getValue() == 2) {
				int1 = 4;
			} else if (SandboxOptions.instance.Zombies.getValue() == 3) {
				int1 = 6;
			} else if (SandboxOptions.instance.Zombies.getValue() == 5) {
				int1 = 15;
			}

			float float1 = 0.0F;
			IsoMetaChunk metaChunk = IsoWorld.instance.getMetaChunk(room.def.x / 10, room.def.y / 10);
			if (metaChunk != null) {
				float1 = metaChunk.getLootZombieIntensity();
				if (float1 > 4.0F) {
					int1 = (int)((float)int1 - (float1 / 2.0F - 2.0F));
				}
			}

			if (room.def.getArea() > 100) {
				int1 -= 2;
			}

			int1 = Math.max(2, int1);
			int int2;
			if (room.getBuilding() != null) {
				int2 = room.def.getArea();
				if (room.getBuilding().getRoomsNumber() > 100 && int2 >= 20) {
					int int3 = room.getBuilding().getRoomsNumber() - 95;
					if (int3 > 20) {
						int3 = 20;
					}

					if (SandboxOptions.instance.Zombies.getValue() == 1) {
						int3 += 10;
					} else if (SandboxOptions.instance.Zombies.getValue() == 2) {
						int3 += 7;
					} else if (SandboxOptions.instance.Zombies.getValue() == 3) {
						int3 += 5;
					} else if (SandboxOptions.instance.Zombies.getValue() == 4) {
						int3 -= 10;
					}

					if (int2 < 30) {
						int3 -= 6;
					}

					if (int2 < 50) {
						int3 -= 10;
					}

					if (int2 < 70) {
						int3 -= 13;
					}

					return Rand.Next(int3, int3 + 10);
				}
			}

			if (Rand.Next(int1) == 0) {
				byte byte1 = 1;
				int2 = (int)((float)byte1 + (float1 / 2.0F - 2.0F));
				if (room.def.getArea() < 30) {
					int2 -= 4;
				}

				if (room.def.getArea() > 85) {
					int2 += 2;
				}

				if (room.getBuilding().getRoomsNumber() < 7) {
					int2 -= 2;
				}

				if (SandboxOptions.instance.Zombies.getValue() == 1) {
					int2 += 3;
				} else if (SandboxOptions.instance.Zombies.getValue() == 2) {
					int2 += 2;
				} else if (SandboxOptions.instance.Zombies.getValue() == 3) {
					++int2;
				} else if (SandboxOptions.instance.Zombies.getValue() == 5) {
					int2 -= 2;
				}

				int2 = Math.max(0, int2);
				int2 = Math.min(7, int2);
				return Rand.Next(int2, int2 + 2);
			} else {
				return 0;
			}
		}
	}

	public void roomSpotted(IsoRoom room) {
		if (!GameClient.bClient) {
			room.def.forEachChunk((var0,roomx)->{
				roomx.addSpawnedRoom(var0.ID);
			});

			if (room.def.spawnCount == -1) {
				room.def.spawnCount = this.getZombieCountForRoom(room);
			}

			if (room.def.spawnCount > 0) {
				if (room.getBuilding().getDef().isFullyStreamedIn()) {
					ArrayList arrayList = this.addZombiesToMap(room.def.spawnCount, room.def, false);
					ZombieSpawnRecorder.instance.record(arrayList, "roomSpotted");
				} else {
					this.m_tempZombies.clear();
					room.def.forEachChunk((arrayListx,var3)->{
						this.addIndoorZombiesToChunk(var3, room, room.def.spawnCount, this.m_tempZombies);
					});

					ZombieSpawnRecorder.instance.record(this.m_tempZombies, "roomSpotted");
				}
			}
		}
	}

	private boolean isBlockedInAllDirections(int int1, int int2, int int3) {
		IsoGridSquare square = GameServer.bServer ? ServerMap.instance.getGridSquare(int1, int2, int3) : IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			return false;
		} else {
			boolean boolean1 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)1, (int)0, (int)1) && square.nav[IsoDirections.N.index()] != null;
			boolean boolean2 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)1, (int)2, (int)1) && square.nav[IsoDirections.S.index()] != null;
			boolean boolean3 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)0, (int)1, (int)1) && square.nav[IsoDirections.W.index()] != null;
			boolean boolean4 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)2, (int)1, (int)1) && square.nav[IsoDirections.E.index()] != null;
			return boolean1 && boolean2 && boolean3 && boolean4;
		}
	}

	private boolean canPathOnlyN(int int1, int int2, int int3) {
		IsoGridSquare square = GameServer.bServer ? ServerMap.instance.getGridSquare(int1, int2, int3) : IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			return false;
		} else {
			boolean boolean1 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)1, (int)0, (int)1) && square.nav[IsoDirections.N.index()] != null;
			boolean boolean2 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)1, (int)2, (int)1) && square.nav[IsoDirections.S.index()] != null;
			boolean boolean3 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)0, (int)1, (int)1) && square.nav[IsoDirections.W.index()] != null;
			boolean boolean4 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)2, (int)1, (int)1) && square.nav[IsoDirections.E.index()] != null;
			return !boolean1 && boolean2 && boolean3 && boolean4;
		}
	}

	private boolean canPathOnlyS(int int1, int int2, int int3) {
		IsoGridSquare square = GameServer.bServer ? ServerMap.instance.getGridSquare(int1, int2, int3) : IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			return false;
		} else {
			boolean boolean1 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)1, (int)0, (int)1) && square.nav[IsoDirections.N.index()] != null;
			boolean boolean2 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)1, (int)2, (int)1) && square.nav[IsoDirections.S.index()] != null;
			boolean boolean3 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)0, (int)1, (int)1) && square.nav[IsoDirections.W.index()] != null;
			boolean boolean4 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)2, (int)1, (int)1) && square.nav[IsoDirections.E.index()] != null;
			return boolean1 && !boolean2 && boolean3 && boolean4;
		}
	}

	private boolean canPathOnlyW(int int1, int int2, int int3) {
		IsoGridSquare square = GameServer.bServer ? ServerMap.instance.getGridSquare(int1, int2, int3) : IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			return false;
		} else {
			boolean boolean1 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)1, (int)0, (int)1) && square.nav[IsoDirections.N.index()] != null;
			boolean boolean2 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)1, (int)2, (int)1) && square.nav[IsoDirections.S.index()] != null;
			boolean boolean3 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)0, (int)1, (int)1) && square.nav[IsoDirections.W.index()] != null;
			boolean boolean4 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)2, (int)1, (int)1) && square.nav[IsoDirections.E.index()] != null;
			return boolean1 && boolean2 && !boolean3 && boolean4;
		}
	}

	private boolean canPathOnlyE(int int1, int int2, int int3) {
		IsoGridSquare square = GameServer.bServer ? ServerMap.instance.getGridSquare(int1, int2, int3) : IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			return false;
		} else {
			boolean boolean1 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)1, (int)0, (int)1) && square.nav[IsoDirections.N.index()] != null;
			boolean boolean2 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)1, (int)2, (int)1) && square.nav[IsoDirections.S.index()] != null;
			boolean boolean3 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)0, (int)1, (int)1) && square.nav[IsoDirections.W.index()] != null;
			boolean boolean4 = IsoGridSquare.getMatrixBit(square.pathMatrix, (int)2, (int)1, (int)1) && square.nav[IsoDirections.E.index()] != null;
			return boolean1 && boolean2 && boolean3 && !boolean4;
		}
	}

	public boolean canSpawnAt(int int1, int int2, int int3) {
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null && square.isFree(false)) {
			if (this.isBlockedInAllDirections(int1, int2, int3)) {
				return false;
			} else if (this.canPathOnlyE(int1, int2, int3) && this.canPathOnlyW(int1 + 1, int2, int3)) {
				return false;
			} else if (this.canPathOnlyE(int1 - 1, int2, int3) && this.canPathOnlyW(int1, int2, int3)) {
				return false;
			} else if (this.canPathOnlyS(int1, int2, int3) && this.canPathOnlyN(int1, int2 + 1, int3)) {
				return false;
			} else {
				return !this.canPathOnlyS(int1, int2 - 1, int3) || !this.canPathOnlyN(int1, int2, int3);
			}
		} else {
			return false;
		}
	}

	public int reusableZombiesSize() {
		return this.ReusableZombies.size();
	}
}
