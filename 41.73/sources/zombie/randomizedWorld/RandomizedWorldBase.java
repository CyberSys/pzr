package zombie.randomizedWorld;

import java.util.ArrayList;
import java.util.List;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.ZombieSpawnRecorder;
import zombie.Lua.MapObjects;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorFactory;
import zombie.core.Rand;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.randomizedWorld.randomizedBuilding.RandomizedBuildingBase;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleType;
import zombie.vehicles.VehiclesDB2;


public class RandomizedWorldBase {
	private static final Vector2 s_tempVector2 = new Vector2();
	protected int minimumDays = 0;
	protected int maximumDays = 0;
	protected int minimumRooms = 0;
	protected boolean unique = false;
	private boolean rvsVehicleKeyAddedToZombie = false;
	protected String name = null;
	protected String debugLine = "";

	public BaseVehicle addVehicle(IsoMetaGrid.Zone zone, IsoGridSquare square, IsoChunk chunk, String string, String string2, IsoDirections directions) {
		return this.addVehicle(zone, square, chunk, string, string2, (Integer)null, directions, (String)null);
	}

	public BaseVehicle addVehicleFlipped(IsoMetaGrid.Zone zone, IsoGridSquare square, IsoChunk chunk, String string, String string2, Integer integer, IsoDirections directions, String string3) {
		if (square == null) {
			return null;
		} else {
			if (directions == null) {
				directions = IsoDirections.getRandom();
			}

			Vector2 vector2 = directions.ToVector();
			return this.addVehicleFlipped(zone, (float)square.x, (float)square.y, (float)square.z, vector2.getDirection(), string, string2, integer, string3);
		}
	}

	public BaseVehicle addVehicleFlipped(IsoMetaGrid.Zone zone, float float1, float float2, float float3, float float4, String string, String string2, Integer integer, String string3) {
		if (StringUtils.isNullOrEmpty(string)) {
			string = "junkyard";
		}

		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, (double)float3);
		if (square == null) {
			return null;
		} else {
			IsoChunk chunk = square.getChunk();
			IsoDirections directions = IsoDirections.fromAngle(float4);
			BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
			baseVehicle.specificDistributionId = string3;
			VehicleType vehicleType = VehicleType.getRandomVehicleType(string, false);
			if (!StringUtils.isNullOrEmpty(string2)) {
				baseVehicle.setScriptName(string2);
				baseVehicle.setScript();
				if (integer != null) {
					baseVehicle.setSkinIndex(integer);
				}
			} else {
				if (vehicleType == null) {
					return null;
				}

				baseVehicle.setVehicleType(vehicleType.name);
				if (!chunk.RandomizeModel(baseVehicle, zone, string, vehicleType)) {
					return null;
				}
			}

			if (vehicleType.isSpecialCar) {
				baseVehicle.setDoColor(false);
			}

			baseVehicle.setDir(directions);
			float float5;
			for (float5 = float4 - 1.5707964F; (double)float5 > 6.283185307179586; float5 = (float)((double)float5 - 6.283185307179586)) {
			}

			baseVehicle.savedRot.rotationXYZ(0.0F, -float5, 3.1415927F);
			baseVehicle.jniTransform.setRotation(baseVehicle.savedRot);
			baseVehicle.setX(float1);
			baseVehicle.setY(float2);
			baseVehicle.setZ(float3);
			if (IsoChunk.doSpawnedVehiclesInInvalidPosition(baseVehicle)) {
				baseVehicle.setSquare(square);
				square.chunk.vehicles.add(baseVehicle);
				baseVehicle.chunk = square.chunk;
				baseVehicle.addToWorld();
				VehiclesDB2.instance.addVehicle(baseVehicle);
			}

			baseVehicle.setGeneralPartCondition(0.2F, 70.0F);
			baseVehicle.rust = Rand.Next(100) < 70 ? 1.0F : 0.0F;
			return baseVehicle;
		}
	}

	public BaseVehicle addVehicle(IsoMetaGrid.Zone zone, IsoGridSquare square, IsoChunk chunk, String string, String string2, Integer integer, IsoDirections directions, String string3) {
		if (square == null) {
			return null;
		} else {
			if (directions == null) {
				directions = IsoDirections.getRandom();
			}

			Vector2 vector2 = directions.ToVector();
			vector2.rotate(Rand.Next(-0.5F, 0.5F));
			return this.addVehicle(zone, (float)square.x, (float)square.y, (float)square.z, vector2.getDirection(), string, string2, integer, string3);
		}
	}

	public BaseVehicle addVehicle(IsoMetaGrid.Zone zone, float float1, float float2, float float3, float float4, String string, String string2, Integer integer, String string3) {
		if (StringUtils.isNullOrEmpty(string)) {
			string = "junkyard";
		}

		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, (double)float3);
		if (square == null) {
			return null;
		} else {
			IsoChunk chunk = square.getChunk();
			IsoDirections directions = IsoDirections.fromAngle(float4);
			BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
			baseVehicle.specificDistributionId = string3;
			VehicleType vehicleType = VehicleType.getRandomVehicleType(string, false);
			if (!StringUtils.isNullOrEmpty(string2)) {
				baseVehicle.setScriptName(string2);
				baseVehicle.setScript();
				if (integer != null) {
					baseVehicle.setSkinIndex(integer);
				}
			} else {
				if (vehicleType == null) {
					return null;
				}

				baseVehicle.setVehicleType(vehicleType.name);
				if (!chunk.RandomizeModel(baseVehicle, zone, string, vehicleType)) {
					return null;
				}
			}

			if (vehicleType.isSpecialCar) {
				baseVehicle.setDoColor(false);
			}

			baseVehicle.setDir(directions);
			float float5;
			for (float5 = float4 - 1.5707964F; (double)float5 > 6.283185307179586; float5 = (float)((double)float5 - 6.283185307179586)) {
			}

			baseVehicle.savedRot.setAngleAxis(-float5, 0.0F, 1.0F, 0.0F);
			baseVehicle.jniTransform.setRotation(baseVehicle.savedRot);
			baseVehicle.setX(float1);
			baseVehicle.setY(float2);
			baseVehicle.setZ(float3);
			if (IsoChunk.doSpawnedVehiclesInInvalidPosition(baseVehicle)) {
				baseVehicle.setSquare(square);
				square.chunk.vehicles.add(baseVehicle);
				baseVehicle.chunk = square.chunk;
				baseVehicle.addToWorld();
				VehiclesDB2.instance.addVehicle(baseVehicle);
			}

			baseVehicle.setGeneralPartCondition(0.2F, 70.0F);
			baseVehicle.rust = Rand.Next(100) < 70 ? 1.0F : 0.0F;
			return baseVehicle;
		}
	}

	public static void removeAllVehiclesOnZone(IsoMetaGrid.Zone zone) {
		for (int int1 = zone.x; int1 < zone.x + zone.w; ++int1) {
			for (int int2 = zone.y; int2 < zone.y + zone.h; ++int2) {
				IsoGridSquare square = IsoCell.getInstance().getGridSquare(int1, int2, 0);
				if (square != null) {
					BaseVehicle baseVehicle = square.getVehicleContainer();
					if (baseVehicle != null) {
						baseVehicle.permanentlyRemove();
					}
				}
			}
		}
	}

	public ArrayList addZombiesOnVehicle(int int1, String string, Integer integer, BaseVehicle baseVehicle) {
		ArrayList arrayList = new ArrayList();
		if (baseVehicle == null) {
			return arrayList;
		} else {
			int int2 = 100;
			IsoGridSquare square = baseVehicle.getSquare();
			if (square != null && square.getCell() != null) {
				for (; int1 > 0; int2 = 100) {
					while (int2 > 0) {
						IsoGridSquare square2 = square.getCell().getGridSquare(Rand.Next(square.x - 4, square.x + 4), Rand.Next(square.y - 4, square.y + 4), square.z);
						if (square2 != null && square2.getVehicleContainer() == null) {
							--int1;
							arrayList.addAll(this.addZombiesOnSquare(1, string, integer, square2));
							break;
						}

						--int2;
					}
				}

				if (!this.rvsVehicleKeyAddedToZombie && !arrayList.isEmpty()) {
					IsoZombie zombie = (IsoZombie)arrayList.get(Rand.Next(0, arrayList.size()));
					zombie.addItemToSpawnAtDeath(baseVehicle.createVehicleKey());
					this.rvsVehicleKeyAddedToZombie = true;
				}

				return arrayList;
			} else {
				return arrayList;
			}
		}
	}

	public static IsoDeadBody createRandomDeadBody(RoomDef roomDef, int int1) {
		if (IsoWorld.getZombiesDisabled()) {
			return null;
		} else if (roomDef == null) {
			return null;
		} else {
			IsoGridSquare square = getRandomSquareForCorpse(roomDef);
			return square == null ? null : createRandomDeadBody(square, (IsoDirections)null, int1, 0, (String)null);
		}
	}

	public ArrayList addZombiesOnSquare(int int1, String string, Integer integer, IsoGridSquare square) {
		ArrayList arrayList = new ArrayList();
		if (IsoWorld.getZombiesDisabled()) {
			return arrayList;
		} else if (square == null) {
			return arrayList;
		} else {
			for (int int2 = 0; int2 < int1; ++int2) {
				VirtualZombieManager.instance.choices.clear();
				VirtualZombieManager.instance.choices.add(square);
				IsoZombie zombie = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.getRandom().index(), false);
				if (zombie != null) {
					if (integer != null) {
						zombie.setFemaleEtc(Rand.Next(100) < integer);
					}

					if (string != null) {
						zombie.dressInPersistentOutfit(string);
						zombie.bDressInRandomOutfit = false;
					} else {
						zombie.dressInRandomOutfit();
						zombie.bDressInRandomOutfit = false;
					}

					arrayList.add(zombie);
				}
			}

			ZombieSpawnRecorder.instance.record(arrayList, this.getClass().getSimpleName());
			return arrayList;
		}
	}

	public static IsoDeadBody createRandomDeadBody(int int1, int int2, int int3, IsoDirections directions, int int4) {
		return createRandomDeadBody(int1, int2, int3, directions, int4, 0);
	}

	public static IsoDeadBody createRandomDeadBody(int int1, int int2, int int3, IsoDirections directions, int int4, int int5) {
		IsoGridSquare square = IsoCell.getInstance().getGridSquare(int1, int2, int3);
		return createRandomDeadBody(square, directions, int4, int5, (String)null);
	}

	public static IsoDeadBody createRandomDeadBody(IsoGridSquare square, IsoDirections directions, int int1, int int2, String string) {
		if (square == null) {
			return null;
		} else {
			boolean boolean1 = directions == null;
			if (boolean1) {
				directions = IsoDirections.getRandom();
			}

			return createRandomDeadBody((float)square.x + Rand.Next(0.05F, 0.95F), (float)square.y + Rand.Next(0.05F, 0.95F), (float)square.z, directions.ToVector().getDirection(), boolean1, int1, int2, string);
		}
	}

	public static IsoDeadBody createRandomDeadBody(float float1, float float2, float float3, float float4, boolean boolean1, int int1, int int2, String string) {
		if (IsoWorld.getZombiesDisabled()) {
			return null;
		} else {
			IsoGridSquare square = IsoCell.getInstance().getGridSquare((double)float1, (double)float2, (double)float3);
			if (square == null) {
				return null;
			} else {
				IsoDirections directions = IsoDirections.fromAngle(float4);
				VirtualZombieManager.instance.choices.clear();
				VirtualZombieManager.instance.choices.add(square);
				IsoZombie zombie = VirtualZombieManager.instance.createRealZombieAlways(directions.index(), false);
				if (zombie == null) {
					return null;
				} else {
					if (string != null) {
						zombie.dressInPersistentOutfit(string);
						zombie.bDressInRandomOutfit = false;
					} else {
						zombie.dressInRandomOutfit();
					}

					if (Rand.Next(100) < int2) {
						zombie.setFakeDead(true);
						zombie.setCrawler(true);
						zombie.setCanWalk(false);
						zombie.setCrawlerType(1);
					} else {
						zombie.setFakeDead(false);
						zombie.setHealth(0.0F);
					}

					zombie.upKillCount = false;
					zombie.getHumanVisual().zombieRotStage = ((HumanVisual)zombie.getVisual()).pickRandomZombieRotStage();
					for (int int3 = 0; int3 < int1; ++int3) {
						zombie.addBlood((BloodBodyPartType)null, false, true, true);
					}

					zombie.DoCorpseInventory();
					zombie.setX(float1);
					zombie.setY(float2);
					zombie.getForwardDirection().setLengthAndDirection(float4, 1.0F);
					if (boolean1 || zombie.isSkeleton()) {
						alignCorpseToSquare(zombie, square);
					}

					IsoDeadBody deadBody = new IsoDeadBody(zombie, true);
					if (!deadBody.isFakeDead() && !deadBody.isSkeleton() && Rand.Next(20) == 0) {
						deadBody.setFakeDead(true);
						if (Rand.Next(5) == 0) {
							deadBody.setCrawling(true);
						}
					}

					return deadBody;
				}
			}
		}
	}

	public void addTraitOfBlood(IsoDirections directions, int int1, int int2, int int3, int int4) {
		for (int int5 = 0; int5 < int1; ++int5) {
			float float1 = 0.0F;
			float float2 = 0.0F;
			if (directions == IsoDirections.S) {
				float2 = Rand.Next(-2.0F, 0.5F);
			}

			if (directions == IsoDirections.N) {
				float2 = Rand.Next(-0.5F, 2.0F);
			}

			if (directions == IsoDirections.E) {
				float1 = Rand.Next(-2.0F, 0.5F);
			}

			if (directions == IsoDirections.W) {
				float1 = Rand.Next(-0.5F, 2.0F);
			}

			new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, IsoCell.getInstance(), (float)int2, (float)int3, (float)int4 + 0.2F, float1, float2);
		}
	}

	public void addTrailOfBlood(float float1, float float2, float float3, float float4, int int1) {
		Vector2 vector2 = s_tempVector2;
		for (int int2 = 0; int2 < int1; ++int2) {
			float float5 = Rand.Next(-0.5F, 2.0F);
			if (float5 < 0.0F) {
				vector2.setLengthAndDirection(float4 + 3.1415927F, -float5);
			} else {
				vector2.setLengthAndDirection(float4, float5);
			}

			new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, IsoCell.getInstance(), float1, float2, float3 + 0.2F, vector2.x, vector2.y);
		}
	}

	public void addBloodSplat(IsoGridSquare square, int int1) {
		for (int int2 = 0; int2 < int1; ++int2) {
			square.getChunk().addBloodSplat((float)square.x + Rand.Next(-0.5F, 0.5F), (float)square.y + Rand.Next(-0.5F, 0.5F), (float)square.z, Rand.Next(8));
		}
	}

	public void setAttachedItem(IsoZombie zombie, String string, String string2, String string3) {
		InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string2);
		if (inventoryItem != null) {
			inventoryItem.setCondition(Rand.Next(Math.max(2, inventoryItem.getConditionMax() - 5), inventoryItem.getConditionMax()));
			if (inventoryItem instanceof HandWeapon) {
				((HandWeapon)inventoryItem).randomizeBullets();
			}

			zombie.setAttachedItem(string, inventoryItem);
			if (!StringUtils.isNullOrEmpty(string3)) {
				zombie.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem(string3));
			}
		}
	}

	public static IsoGameCharacter createRandomZombie(RoomDef roomDef) {
		IsoGridSquare square = getRandomSpawnSquare(roomDef);
		return createRandomZombie(square.getX(), square.getY(), square.getZ());
	}

	public static IsoGameCharacter createRandomZombieForCorpse(RoomDef roomDef) {
		IsoGridSquare square = getRandomSquareForCorpse(roomDef);
		if (square == null) {
			return null;
		} else {
			IsoGameCharacter gameCharacter = createRandomZombie(square.getX(), square.getY(), square.getZ());
			if (gameCharacter != null) {
				alignCorpseToSquare(gameCharacter, square);
			}

			return gameCharacter;
		}
	}

	public static IsoDeadBody createBodyFromZombie(IsoGameCharacter gameCharacter) {
		if (IsoWorld.getZombiesDisabled()) {
			return null;
		} else {
			for (int int1 = 0; int1 < 6; ++int1) {
				gameCharacter.splatBlood(Rand.Next(1, 4), 0.3F);
			}

			IsoDeadBody deadBody = new IsoDeadBody(gameCharacter, true);
			return deadBody;
		}
	}

	public static IsoGameCharacter createRandomZombie(int int1, int int2, int int3) {
		RandomizedBuildingBase.HumanCorpse humanCorpse = new RandomizedBuildingBase.HumanCorpse(IsoWorld.instance.getCell(), (float)int1, (float)int2, (float)int3);
		humanCorpse.setDescriptor(SurvivorFactory.CreateSurvivor());
		humanCorpse.setFemale(humanCorpse.getDescriptor().isFemale());
		humanCorpse.setDir(IsoDirections.fromIndex(Rand.Next(8)));
		humanCorpse.initWornItems("Human");
		humanCorpse.initAttachedItems("Human");
		Outfit outfit = humanCorpse.getRandomDefaultOutfit();
		humanCorpse.dressInNamedOutfit(outfit.m_Name);
		humanCorpse.initSpritePartsEmpty();
		humanCorpse.Dressup(humanCorpse.getDescriptor());
		return humanCorpse;
	}

	private static boolean isSquareClear(IsoGridSquare square) {
		return square != null && canSpawnAt(square) && !square.HasStairs() && !square.HasTree() && !square.getProperties().Is(IsoFlagType.bed) && !square.getProperties().Is(IsoFlagType.waterPiped);
	}

	private static boolean isSquareClear(IsoGridSquare square, IsoDirections directions) {
		IsoGridSquare square2 = square.getAdjacentSquare(directions);
		return isSquareClear(square2) && !square.isSomethingTo(square2) && square.getRoomID() == square2.getRoomID();
	}

	public static boolean is1x2AreaClear(IsoGridSquare square) {
		return isSquareClear(square) && isSquareClear(square, IsoDirections.N);
	}

	public static boolean is2x1AreaClear(IsoGridSquare square) {
		return isSquareClear(square) && isSquareClear(square, IsoDirections.W);
	}

	public static boolean is2x1or1x2AreaClear(IsoGridSquare square) {
		return isSquareClear(square) && (isSquareClear(square, IsoDirections.W) || isSquareClear(square, IsoDirections.N));
	}

	public static boolean is2x2AreaClear(IsoGridSquare square) {
		return isSquareClear(square) && isSquareClear(square, IsoDirections.N) && isSquareClear(square, IsoDirections.W) && isSquareClear(square, IsoDirections.NW);
	}

	public static void alignCorpseToSquare(IsoGameCharacter gameCharacter, IsoGridSquare square) {
		int int1 = square.x;
		int int2 = square.y;
		IsoDirections directions = IsoDirections.fromIndex(Rand.Next(8));
		boolean boolean1 = is1x2AreaClear(square);
		boolean boolean2 = is2x1AreaClear(square);
		if (boolean1 && boolean2) {
			boolean1 = Rand.Next(2) == 0;
			boolean2 = !boolean1;
		}

		if (is2x2AreaClear(square)) {
			gameCharacter.setX((float)int1);
			gameCharacter.setY((float)int2);
		} else if (boolean1) {
			gameCharacter.setX((float)int1 + 0.5F);
			gameCharacter.setY((float)int2);
			directions = Rand.Next(2) == 0 ? IsoDirections.N : IsoDirections.S;
		} else if (boolean2) {
			gameCharacter.setX((float)int1);
			gameCharacter.setY((float)int2 + 0.5F);
			directions = Rand.Next(2) == 0 ? IsoDirections.W : IsoDirections.E;
		} else if (is1x2AreaClear(square.getAdjacentSquare(IsoDirections.S))) {
			gameCharacter.setX((float)int1 + 0.5F);
			gameCharacter.setY((float)int2 + 0.99F);
			directions = Rand.Next(2) == 0 ? IsoDirections.N : IsoDirections.S;
		} else if (is2x1AreaClear(square.getAdjacentSquare(IsoDirections.E))) {
			gameCharacter.setX((float)int1 + 0.99F);
			gameCharacter.setY((float)int2 + 0.5F);
			directions = Rand.Next(2) == 0 ? IsoDirections.W : IsoDirections.E;
		}

		gameCharacter.setDir(directions);
		gameCharacter.lx = gameCharacter.nx = gameCharacter.x;
		gameCharacter.ly = gameCharacter.ny = gameCharacter.y;
		gameCharacter.setScriptnx(gameCharacter.x);
		gameCharacter.setScriptny(gameCharacter.y);
	}

	public RoomDef getRandomRoom(BuildingDef buildingDef, int int1) {
		RoomDef roomDef = (RoomDef)buildingDef.getRooms().get(Rand.Next(0, buildingDef.getRooms().size()));
		if (int1 > 0 && roomDef.area >= int1) {
			return roomDef;
		} else {
			int int2 = 0;
			do {
				if (int2 > 20) {
					return roomDef;
				}

				++int2;
				roomDef = (RoomDef)buildingDef.getRooms().get(Rand.Next(0, buildingDef.getRooms().size()));
			}	 while (roomDef.area < int1);

			return roomDef;
		}
	}

	public RoomDef getRoom(BuildingDef buildingDef, String string) {
		for (int int1 = 0; int1 < buildingDef.rooms.size(); ++int1) {
			RoomDef roomDef = (RoomDef)buildingDef.rooms.get(int1);
			if (roomDef.getName().equalsIgnoreCase(string)) {
				return roomDef;
			}
		}

		return null;
	}

	public RoomDef getLivingRoomOrKitchen(BuildingDef buildingDef) {
		RoomDef roomDef = this.getRoom(buildingDef, "livingroom");
		if (roomDef == null) {
			roomDef = this.getRoom(buildingDef, "kitchen");
		}

		return roomDef;
	}

	private static boolean canSpawnAt(IsoGridSquare square) {
		if (square == null) {
			return false;
		} else {
			return square.HasStairs() ? false : VirtualZombieManager.instance.canSpawnAt(square.x, square.y, square.z);
		}
	}

	public static IsoGridSquare getRandomSpawnSquare(RoomDef roomDef) {
		return roomDef == null ? null : roomDef.getRandomSquare(RandomizedWorldBase::canSpawnAt);
	}

	public static IsoGridSquare getRandomSquareForCorpse(RoomDef roomDef) {
		IsoGridSquare square = roomDef.getRandomSquare(RandomizedWorldBase::is2x2AreaClear);
		IsoGridSquare square2 = roomDef.getRandomSquare(RandomizedWorldBase::is2x1or1x2AreaClear);
		if (square == null || square2 != null && Rand.Next(4) == 0) {
			square = square2;
		}

		return square;
	}

	public BaseVehicle spawnCarOnNearestNav(String string, BuildingDef buildingDef) {
		IsoGridSquare square = null;
		int int1 = (buildingDef.x + buildingDef.x2) / 2;
		int int2 = (buildingDef.y + buildingDef.y2) / 2;
		int int3;
		IsoGridSquare square2;
		for (int3 = int1; int3 < int1 + 20; ++int3) {
			square2 = IsoCell.getInstance().getGridSquare(int3, int2, 0);
			if (square2 != null && "Nav".equals(square2.getZoneType())) {
				square = square2;
				break;
			}
		}

		if (square != null) {
			return this.spawnCar(string, square);
		} else {
			for (int3 = int1; int3 > int1 - 20; --int3) {
				square2 = IsoCell.getInstance().getGridSquare(int3, int2, 0);
				if (square2 != null && "Nav".equals(square2.getZoneType())) {
					square = square2;
					break;
				}
			}

			if (square != null) {
				return this.spawnCar(string, square);
			} else {
				for (int3 = int2; int3 < int2 + 20; ++int3) {
					square2 = IsoCell.getInstance().getGridSquare(int1, int3, 0);
					if (square2 != null && "Nav".equals(square2.getZoneType())) {
						square = square2;
						break;
					}
				}

				if (square != null) {
					return this.spawnCar(string, square);
				} else {
					for (int3 = int2; int3 > int2 - 20; --int3) {
						square2 = IsoCell.getInstance().getGridSquare(int1, int3, 0);
						if (square2 != null && "Nav".equals(square2.getZoneType())) {
							square = square2;
							break;
						}
					}

					return square != null ? this.spawnCar(string, square) : null;
				}
			}
		}
	}

	private BaseVehicle spawnCar(String string, IsoGridSquare square) {
		BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
		baseVehicle.setScriptName(string);
		baseVehicle.setX((float)square.x + 0.5F);
		baseVehicle.setY((float)square.y + 0.5F);
		baseVehicle.setZ(0.0F);
		baseVehicle.savedRot.setAngleAxis(Rand.Next(0.0F, 6.2831855F), 0.0F, 1.0F, 0.0F);
		baseVehicle.jniTransform.setRotation(baseVehicle.savedRot);
		if (IsoChunk.doSpawnedVehiclesInInvalidPosition(baseVehicle)) {
			baseVehicle.keySpawned = 1;
			baseVehicle.setSquare(square);
			baseVehicle.square.chunk.vehicles.add(baseVehicle);
			baseVehicle.chunk = baseVehicle.square.chunk;
			baseVehicle.addToWorld();
			VehiclesDB2.instance.addVehicle(baseVehicle);
		}

		baseVehicle.setGeneralPartCondition(0.3F, 70.0F);
		return baseVehicle;
	}

	public InventoryItem addItemOnGround(IsoGridSquare square, String string) {
		return square != null && !StringUtils.isNullOrWhitespace(string) ? square.AddWorldInventoryItem(string, Rand.Next(0.2F, 0.8F), Rand.Next(0.2F, 0.8F), 0.0F) : null;
	}

	public InventoryItem addItemOnGround(IsoGridSquare square, InventoryItem inventoryItem) {
		return square != null && inventoryItem != null ? square.AddWorldInventoryItem(inventoryItem, Rand.Next(0.2F, 0.8F), Rand.Next(0.2F, 0.8F), 0.0F) : null;
	}

	public void addRandomItemsOnGround(RoomDef roomDef, String string, int int1) {
		for (int int2 = 0; int2 < int1; ++int2) {
			IsoGridSquare square = getRandomSpawnSquare(roomDef);
			this.addItemOnGround(square, string);
		}
	}

	public void addRandomItemsOnGround(RoomDef roomDef, ArrayList arrayList, int int1) {
		for (int int2 = 0; int2 < int1; ++int2) {
			IsoGridSquare square = getRandomSpawnSquare(roomDef);
			this.addRandomItemOnGround(square, arrayList);
		}
	}

	public InventoryItem addRandomItemOnGround(IsoGridSquare square, ArrayList arrayList) {
		if (square != null && !arrayList.isEmpty()) {
			String string = (String)PZArrayUtil.pickRandom((List)arrayList);
			return this.addItemOnGround(square, string);
		} else {
			return null;
		}
	}

	public HandWeapon addWeapon(String string, boolean boolean1) {
		HandWeapon handWeapon = (HandWeapon)InventoryItemFactory.CreateItem(string);
		if (handWeapon == null) {
			return null;
		} else {
			if (handWeapon.isRanged() && boolean1) {
				if (!StringUtils.isNullOrWhitespace(handWeapon.getMagazineType())) {
					handWeapon.setContainsClip(true);
				}

				handWeapon.setCurrentAmmoCount(Rand.Next(Math.max(handWeapon.getMaxAmmo() - 8, 0), handWeapon.getMaxAmmo() - 2));
			}

			return handWeapon;
		}
	}

	public IsoDeadBody createSkeletonCorpse(RoomDef roomDef) {
		if (roomDef == null) {
			return null;
		} else {
			IsoGridSquare square = roomDef.getRandomSquare(RandomizedWorldBase::is2x1or1x2AreaClear);
			if (square == null) {
				return null;
			} else {
				VirtualZombieManager.instance.choices.clear();
				VirtualZombieManager.instance.choices.add(square);
				IsoZombie zombie = VirtualZombieManager.instance.createRealZombieAlways(Rand.Next(8), false);
				if (zombie == null) {
					return null;
				} else {
					ZombieSpawnRecorder.instance.record(zombie, this.getClass().getSimpleName());
					alignCorpseToSquare(zombie, square);
					zombie.setFakeDead(false);
					zombie.setHealth(0.0F);
					zombie.upKillCount = false;
					zombie.setSkeleton(true);
					zombie.getHumanVisual().setSkinTextureIndex(Rand.Next(1, 3));
					return new IsoDeadBody(zombie, true);
				}
			}
		}
	}

	public boolean isTimeValid(boolean boolean1) {
		if (this.minimumDays != 0 && this.maximumDays != 0) {
			float float1 = (float)GameTime.getInstance().getWorldAgeHours() / 24.0F;
			float1 += (float)((SandboxOptions.instance.TimeSinceApo.getValue() - 1) * 30);
			if (this.minimumDays > 0 && float1 < (float)this.minimumDays) {
				return false;
			} else {
				return this.maximumDays <= 0 || !(float1 > (float)this.maximumDays);
			}
		} else {
			return true;
		}
	}

	public String getName() {
		return this.name;
	}

	public String getDebugLine() {
		return this.debugLine;
	}

	public void setDebugLine(String string) {
		this.debugLine = string;
	}

	public int getMaximumDays() {
		return this.maximumDays;
	}

	public void setMaximumDays(int int1) {
		this.maximumDays = int1;
	}

	public boolean isUnique() {
		return this.unique;
	}

	public void setUnique(boolean boolean1) {
		this.unique = boolean1;
	}

	public IsoGridSquare getSq(int int1, int int2, int int3) {
		return IsoWorld.instance.getCell().getGridSquare(int1, int2, int3);
	}

	public IsoObject addTileObject(int int1, int int2, int int3, String string) {
		return this.addTileObject(this.getSq(int1, int2, int3), string);
	}

	public IsoObject addTileObject(IsoGridSquare square, String string) {
		if (square == null) {
			return null;
		} else {
			IsoObject object = IsoObject.getNew(square, string, (String)null, false);
			square.AddTileObject(object);
			MapObjects.newGridSquare(square);
			MapObjects.loadGridSquare(square);
			return object;
		}
	}

	public IsoObject addTentNorthSouth(int int1, int int2, int int3) {
		this.addTileObject(int1, int2 - 1, int3, "camping_01_1");
		return this.addTileObject(int1, int2, int3, "camping_01_0");
	}

	public IsoObject addTentWestEast(int int1, int int2, int int3) {
		this.addTileObject(int1 - 1, int2, int3, "camping_01_2");
		return this.addTileObject(int1, int2, int3, "camping_01_3");
	}

	public BaseVehicle addTrailer(BaseVehicle baseVehicle, IsoMetaGrid.Zone zone, IsoChunk chunk, String string, String string2, String string3) {
		IsoGridSquare square = baseVehicle.getSquare();
		IsoDirections directions = baseVehicle.getDir();
		byte byte1 = 0;
		byte byte2 = 0;
		if (directions == IsoDirections.S) {
			byte2 = -3;
		}

		if (directions == IsoDirections.N) {
			byte2 = 3;
		}

		if (directions == IsoDirections.W) {
			byte1 = 3;
		}

		if (directions == IsoDirections.E) {
			byte1 = -3;
		}

		BaseVehicle baseVehicle2 = this.addVehicle(zone, this.getSq(square.x + byte1, square.y + byte2, square.z), chunk, string, string3, (Integer)null, directions, string2);
		if (baseVehicle2 != null) {
			baseVehicle.positionTrailer(baseVehicle2);
		}

		return baseVehicle2;
	}
}
