package zombie;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import zombie.ai.states.ZombieStandState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorDesc;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.skinnedmodel.ModelManager;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoFireManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.popman.ZombiePopulationManager;
import zombie.vehicles.BaseVehicle;


public class VirtualZombieManager {
	private ArrayDeque ReusableZombies = new ArrayDeque();
	private HashSet ReusableZombieSet = new HashSet();
	public ArrayList ReusedThisFrame = new ArrayList();
	public static VirtualZombieManager instance = new VirtualZombieManager();
	public int MaxRealZombies = 250;
	public ArrayList choices = new ArrayList();
	ArrayList bestchoices = new ArrayList();
	HandWeapon w = null;

	public boolean removeZombieFromWorld(IsoZombie zombie) {
		if (GameServer.bServer) {
			for (int int1 = 0; int1 < GameServer.Players.size(); ++int1) {
				if (((IsoPlayer)GameServer.Players.get(int1)).DistTo(zombie) < 10.0F) {
				}
			}
		}

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
				if (ModelManager.instance.Contains.contains(zombie) && !ModelManager.instance.ToRemove.contains(zombie)) {
					ModelManager.instance.Remove((IsoGameCharacter)zombie);
				}

				zombie.setCurrent((IsoGridSquare)null);
				zombie.setLast((IsoGridSquare)null);
				zombie.getStateMachine().Lock = false;
				zombie.getStateMachine().setCurrent(ZombieStandState.instance());
				zombie.setIgnoreMovementForDirection(true);
				zombie.bCrawling = false;
				zombie.setOnFloor(false);
				zombie.PlayAnim("ZombieIdle");
				zombie.strength = -1;
				zombie.cognition = -1;
				zombie.speedType = -1;
				zombie.DoZombieStats();
				if (SandboxOptions.instance.Lore.Toughness.getValue() == 1) {
					zombie.setHealth(3.5F + Rand.Next(0.0F, 0.3F));
				}

				if (SandboxOptions.instance.Lore.Toughness.getValue() == 2) {
					zombie.setHealth(1.8F + Rand.Next(0.0F, 0.3F));
				}

				if (SandboxOptions.instance.Lore.Toughness.getValue() == 3) {
					zombie.setHealth(0.5F + Rand.Next(0.0F, 0.3F));
				}

				if (SandboxOptions.instance.Lore.Toughness.getValue() == 4) {
					zombie.setHealth(Rand.Next(0.5F, 3.5F) + Rand.Next(0.0F, 0.3F));
				}

				zombie.setCollidable(true);
				zombie.setShootable(true);
				if (zombie.isOnFire()) {
					IsoFireManager.RemoveBurningCharacter(zombie);
					zombie.setOnFire(false);
				}

				if (zombie.AttachedAnimSprite != null) {
					zombie.AttachedAnimSprite.clear();
				}

				if (zombie.AttachedAnimSpriteActual != null) {
					zombie.AttachedAnimSpriteActual.clear();
				}

				zombie.OnlineID = -1;
				zombie.bIndoorZombie = false;
				zombie.setVehicle4TestCollision((BaseVehicle)null);
				zombie.clearItemsToSpawnAtDeath();
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
			if (SystemDisabler.doZombieCreation) {
				for (int int1 = 0; int1 < this.MaxRealZombies + 100; ++int1) {
					SharedDescriptors.Descriptor descriptor = SharedDescriptors.pickRandomDescriptor();
					if (descriptor != null) {
						zombie = new IsoZombie(IsoWorld.instance.CurrentCell, descriptor.desc, descriptor.palette);
					} else {
						zombie = new IsoZombie(IsoWorld.instance.CurrentCell);
					}

					zombie.getEmitter().unregister();
					this.addToReusable(zombie);
				}
			}
		}
	}

	public void update() {
		int int1;
		IsoZombie zombie;
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
		return this.createRealZombieAlways(int1, boolean1, (SurvivorDesc)null, -1);
	}

	public IsoZombie createRealZombieAlways(int int1, int int2, boolean boolean1) {
		SharedDescriptors.Descriptor descriptor = SharedDescriptors.getDescriptor(int1);
		return descriptor == null ? this.createRealZombieAlways(int2, boolean1, (SurvivorDesc)null, -1) : this.createRealZombieAlways(int2, boolean1, descriptor.desc, descriptor.palette);
	}

	public IsoZombie createRealZombieAlways(int int1, boolean boolean1, SurvivorDesc survivorDesc, int int2) {
		IsoZombie zombie = null;
		if (!SystemDisabler.doZombieCreation) {
			return null;
		} else {
			if (this.w == null) {
				this.w = (HandWeapon)InventoryItemFactory.CreateItem("Base.Axe");
			}

			Vector2 vector2;
			if (this.ReusableZombies.isEmpty()) {
				if (survivorDesc != null) {
					zombie = new IsoZombie(IsoWorld.instance.CurrentCell, survivorDesc, int2);
				} else {
					SharedDescriptors.Descriptor descriptor = SharedDescriptors.pickRandomDescriptor();
					if (descriptor == null) {
						zombie = new IsoZombie(IsoWorld.instance.CurrentCell);
					} else {
						zombie = new IsoZombie(IsoWorld.instance.CurrentCell, descriptor.desc, descriptor.palette);
					}
				}

				IsoWorld.instance.CurrentCell.getObjectList().add(zombie);
			} else {
				zombie = (IsoZombie)this.ReusableZombies.removeFirst();
				this.ReusableZombieSet.remove(zombie);
				zombie.useDescriptor(survivorDesc, int2);
				zombie.bDead = false;
				zombie.setFakeDead(false);
				zombie.setReanimatedPlayer(false);
				zombie.getStateMachine().Lock = false;
				vector2 = zombie.dir.ToVector();
				zombie.angle.x = vector2.x;
				zombie.angle.y = vector2.y;
				Vector2 vector22 = zombie.angle;
				vector22.x += (float)Rand.Next(200) / 100.0F - 0.5F;
				vector22 = zombie.angle;
				vector22.y += (float)Rand.Next(200) / 100.0F - 0.5F;
				zombie.angle.normalize();
				zombie.getStateMachine().changeState(ZombieStandState.instance());
				zombie.PlayAnim("ZombieIdle");
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

				if (zombie.AttachedAnimSpriteActual != null) {
					zombie.AttachedAnimSpriteActual.clear();
				}

				zombie.bx = 0.0F;
				zombie.thumpFlag = 0;
				zombie.thumpSent = false;
				zombie.mpIdleSound = false;
				zombie.soundSourceTarget = null;
				zombie.soundAttract = 0.0F;
				zombie.soundAttractTimeout = 0.0F;
				zombie.clearItemsToSpawnAtDeath();
			}

			zombie.dir = IsoDirections.fromIndex(int1);
			vector2 = zombie.dir.ToVector();
			zombie.angle.x = vector2.x;
			zombie.angle.y = vector2.y;
			zombie.target = null;
			zombie.TimeSinceSeenFlesh = 100000.0F;
			zombie.nextRallyTime = -1.0F;
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
			IsoGridSquare square = (IsoGridSquare)this.choices.get(Rand.Next(this.choices.size()));
			if (square == null) {
				return null;
			} else {
				if (square == null) {
					for (int int3 = 0; int3 < this.choices.size(); ++int3) {
						if (this.choices.get(int3) != null) {
							square = (IsoGridSquare)this.choices.get(int3);
							break;
						}
					}

					if (square == null) {
						DebugLog.log("ERROR: createRealZombieAlways can not create zombie");
						return null;
					}
				}

				float1 += (float)square.getX();
				float2 += (float)square.getY();
				zombie.setCurrent(square);
				zombie.setX(float1);
				zombie.setY(float2);
				zombie.setZ((float)square.getZ());
				zombie.upKillCount = true;
				if (boolean1) {
					zombie.setDir(IsoDirections.fromIndex(Rand.Next(8)));
					vector2 = zombie.dir.ToVector();
					zombie.angle.x = vector2.x;
					zombie.angle.y = vector2.y;
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
		}
	}

	private IsoZombie createRealZombie(int int1, boolean boolean1) {
		Object object = null;
		if (GameClient.bClient) {
			return null;
		} else {
			return !SystemDisabler.doZombieCreation ? null : this.createRealZombieAlways(int1, boolean1);
		}
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

	public void addZombiesToMap(int int1, RoomDef roomDef) {
		this.addZombiesToMap(int1, roomDef, true);
	}

	public void addZombiesToMap(int int1, RoomDef roomDef, boolean boolean1) {
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

	public void tryAddIndoorZombies(RoomDef roomDef, boolean boolean1) {
		if (GameServer.bServer) {
			if (!IsoWorld.getZombiesDisabled()) {
				if (roomDef.getBuilding() != null && roomDef.getBuilding().getRooms().size() > 100 && roomDef.getArea() >= 20) {
					int int1 = roomDef.getBuilding().getRooms().size() - 95;
					if (int1 > 20) {
						int1 = 20;
					}

					if (SandboxOptions.instance.Zombies.getValue() == 1) {
						int1 += 10;
					} else if (SandboxOptions.instance.Zombies.getValue() == 2) {
						int1 += 5;
					} else if (SandboxOptions.instance.Zombies.getValue() == 4) {
						int1 -= 10;
					}

					if (roomDef.getArea() < 30) {
						int1 -= 6;
					}

					if (roomDef.getArea() < 50) {
						int1 -= 10;
					}

					if (roomDef.getArea() < 70) {
						int1 -= 13;
					}

					DebugLog.log(DebugType.Zombie, "addIndoorZombies " + roomDef.ID);
					this.addIndoorZombies(Rand.Next(int1, int1 + 10), roomDef, false);
				} else {
					byte byte1 = 7;
					if (SandboxOptions.instance.Zombies.getValue() == 1) {
						byte1 = 3;
					} else if (SandboxOptions.instance.Zombies.getValue() == 2) {
						byte1 = 6;
					} else if (SandboxOptions.instance.Zombies.getValue() == 4) {
						byte1 = 15;
					}

					if (Rand.Next(byte1) == 0) {
						DebugLog.log(DebugType.Zombie, "addIndoorZombies " + roomDef.ID);
						this.addIndoorZombies(Rand.Next(1, 3), roomDef, boolean1);
					}
				}
			}
		}
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
			ReanimatedPlayers.instance.removeReanimatedPlayerFromWorld(zombie);
		} else {
			if (!this.ReusedThisFrame.contains(zombie)) {
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

	public void roomSpotted(IsoRoom room) {
		if (!IsoWorld.getZombiesDisabled()) {
			if (!GameServer.bServer && !GameClient.bClient) {
				if (!Core.bLastStand) {
					byte byte1 = 7;
					if (SandboxOptions.instance.Zombies.getValue() == 1) {
						byte1 = 3;
					} else if (SandboxOptions.instance.Zombies.getValue() == 2) {
						byte1 = 6;
					} else if (SandboxOptions.instance.Zombies.getValue() == 4) {
						byte1 = 15;
					}

					if (room.getBuilding() != null && room.getBuilding().getRoomsNumber() > 100 && room.getSquares() != null && room.getSquares().size() >= 20) {
						int int1 = room.getBuilding().getRoomsNumber() - 95;
						if (int1 > 20) {
							int1 = 20;
						}

						if (SandboxOptions.instance.Zombies.getValue() == 1) {
							int1 += 10;
						} else if (SandboxOptions.instance.Zombies.getValue() == 2) {
							int1 += 5;
						} else if (SandboxOptions.instance.Zombies.getValue() == 4) {
							int1 -= 10;
						}

						if (room.getSquares() != null && room.getSquares().size() < 30) {
							int1 -= 6;
						}

						if (room.getSquares() != null && room.getSquares().size() < 50) {
							int1 -= 10;
						}

						if (room.getSquares() != null && room.getSquares().size() < 70) {
							int1 -= 13;
						}

						this.addZombiesToMap(Rand.Next(int1, int1 + 10), room.def, false);
					} else {
						if (Rand.Next(byte1) == 0) {
							this.addZombiesToMap(Rand.Next(1, 3), room.def, false);
						}
					}
				}
			}
		}
	}

	private boolean isBlockedInAllDirections(int int1, int int2, int int3) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
		if (square == null) {
			return false;
		} else {
			boolean boolean1 = square.pathMatrix[1][0][1] && square.nav[IsoDirections.N.index()] != null;
			boolean boolean2 = square.pathMatrix[1][2][1] && square.nav[IsoDirections.S.index()] != null;
			boolean boolean3 = square.pathMatrix[0][1][1] && square.nav[IsoDirections.W.index()] != null;
			boolean boolean4 = square.pathMatrix[2][1][1] && square.nav[IsoDirections.E.index()] != null;
			return boolean1 && boolean2 && boolean3 && boolean4;
		}
	}

	private boolean canPathOnlyN(int int1, int int2, int int3) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
		if (square == null) {
			return false;
		} else {
			boolean boolean1 = square.pathMatrix[1][0][1] && square.nav[IsoDirections.N.index()] != null;
			boolean boolean2 = square.pathMatrix[1][2][1] && square.nav[IsoDirections.S.index()] != null;
			boolean boolean3 = square.pathMatrix[0][1][1] && square.nav[IsoDirections.W.index()] != null;
			boolean boolean4 = square.pathMatrix[2][1][1] && square.nav[IsoDirections.E.index()] != null;
			return !boolean1 && boolean2 && boolean3 && boolean4;
		}
	}

	private boolean canPathOnlyS(int int1, int int2, int int3) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
		if (square == null) {
			return false;
		} else {
			boolean boolean1 = square.pathMatrix[1][0][1] && square.nav[IsoDirections.N.index()] != null;
			boolean boolean2 = square.pathMatrix[1][2][1] && square.nav[IsoDirections.S.index()] != null;
			boolean boolean3 = square.pathMatrix[0][1][1] && square.nav[IsoDirections.W.index()] != null;
			boolean boolean4 = square.pathMatrix[2][1][1] && square.nav[IsoDirections.E.index()] != null;
			return boolean1 && !boolean2 && boolean3 && boolean4;
		}
	}

	private boolean canPathOnlyW(int int1, int int2, int int3) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
		if (square == null) {
			return false;
		} else {
			boolean boolean1 = square.pathMatrix[1][0][1] && square.nav[IsoDirections.N.index()] != null;
			boolean boolean2 = square.pathMatrix[1][2][1] && square.nav[IsoDirections.S.index()] != null;
			boolean boolean3 = square.pathMatrix[0][1][1] && square.nav[IsoDirections.W.index()] != null;
			boolean boolean4 = square.pathMatrix[2][1][1] && square.nav[IsoDirections.E.index()] != null;
			return boolean1 && boolean2 && !boolean3 && boolean4;
		}
	}

	private boolean canPathOnlyE(int int1, int int2, int int3) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
		if (square == null) {
			return false;
		} else {
			boolean boolean1 = square.pathMatrix[1][0][1] && square.nav[IsoDirections.N.index()] != null;
			boolean boolean2 = square.pathMatrix[1][2][1] && square.nav[IsoDirections.S.index()] != null;
			boolean boolean3 = square.pathMatrix[0][1][1] && square.nav[IsoDirections.W.index()] != null;
			boolean boolean4 = square.pathMatrix[2][1][1] && square.nav[IsoDirections.E.index()] != null;
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
}
