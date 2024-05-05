package zombie.randomizedWorld.randomizedBuilding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.ZombieSpawnRecorder;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorDesc;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemPickerJava;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.WeaponPart;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.SpawnPoints;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.randomizedWorld.RandomizedWorldBase;


public class RandomizedBuildingBase extends RandomizedWorldBase {
	private int chance = 0;
	private static int totalChance = 0;
	private static HashMap rbMap = new HashMap();
	protected static final int KBBuildingX = 10744;
	protected static final int KBBuildingY = 9409;
	private boolean alwaysDo = false;
	private static HashMap weaponsList = new HashMap();

	public void randomizeBuilding(BuildingDef buildingDef) {
		buildingDef.bAlarmed = false;
	}

	public void init() {
		if (weaponsList.isEmpty()) {
			weaponsList.put("Base.Shotgun", "Base.ShotgunShellsBox");
			weaponsList.put("Base.Pistol", "Base.Bullets9mmBox");
			weaponsList.put("Base.Pistol2", "Base.Bullets45Box");
			weaponsList.put("Base.Pistol3", "Base.Bullets44Box");
			weaponsList.put("Base.VarmintRifle", "Base.223Box");
			weaponsList.put("Base.HuntingRifle", "Base.308Box");
		}
	}

	public static void initAllRBMapChance() {
		for (int int1 = 0; int1 < IsoWorld.instance.getRandomizedBuildingList().size(); ++int1) {
			totalChance += ((RandomizedBuildingBase)IsoWorld.instance.getRandomizedBuildingList().get(int1)).getChance();
			rbMap.put((RandomizedBuildingBase)IsoWorld.instance.getRandomizedBuildingList().get(int1), ((RandomizedBuildingBase)IsoWorld.instance.getRandomizedBuildingList().get(int1)).getChance());
		}
	}

	public boolean isValid(BuildingDef buildingDef, boolean boolean1) {
		this.debugLine = "";
		if (GameClient.bClient) {
			return false;
		} else if (buildingDef.isAllExplored() && !boolean1) {
			return false;
		} else {
			if (!GameServer.bServer) {
				if (!boolean1 && IsoPlayer.getInstance().getSquare() != null && IsoPlayer.getInstance().getSquare().getBuilding() != null && IsoPlayer.getInstance().getSquare().getBuilding().def == buildingDef) {
					this.customizeStartingHouse(IsoPlayer.getInstance().getSquare().getBuilding().def);
					return false;
				}
			} else if (!boolean1) {
				for (int int1 = 0; int1 < GameServer.Players.size(); ++int1) {
					IsoPlayer player = (IsoPlayer)GameServer.Players.get(int1);
					if (player.getSquare() != null && player.getSquare().getBuilding() != null && player.getSquare().getBuilding().def == buildingDef) {
						return false;
					}
				}
			}

			boolean boolean2 = false;
			boolean boolean3 = false;
			boolean boolean4 = false;
			for (int int2 = 0; int2 < buildingDef.rooms.size(); ++int2) {
				RoomDef roomDef = (RoomDef)buildingDef.rooms.get(int2);
				if ("bedroom".equals(roomDef.name)) {
					boolean2 = true;
				}

				if ("kitchen".equals(roomDef.name) || "livingroom".equals(roomDef.name)) {
					boolean3 = true;
				}

				if ("bathroom".equals(roomDef.name)) {
					boolean4 = true;
				}
			}

			if (!boolean2) {
				this.debugLine = this.debugLine + "no bedroom ";
			}

			if (!boolean4) {
				this.debugLine = this.debugLine + "no bathroom ";
			}

			if (!boolean3) {
				this.debugLine = this.debugLine + "no living room or kitchen ";
			}

			return boolean2 && boolean4 && boolean3;
		}
	}

	private void customizeStartingHouse(BuildingDef buildingDef) {
	}

	public int getMinimumDays() {
		return this.minimumDays;
	}

	public void setMinimumDays(int int1) {
		this.minimumDays = int1;
	}

	public int getMinimumRooms() {
		return this.minimumRooms;
	}

	public void setMinimumRooms(int int1) {
		this.minimumRooms = int1;
	}

	public static void ChunkLoaded(IsoBuilding building) {
		if (!GameClient.bClient && building.def != null && !building.def.seen && building.def.isFullyStreamedIn()) {
			if (GameServer.bServer && GameServer.Players.isEmpty()) {
				return;
			}

			for (int int1 = 0; int1 < building.Rooms.size(); ++int1) {
				if (((IsoRoom)building.Rooms.get(int1)).def.bExplored) {
					return;
				}
			}

			if (!building.def.isAnyChunkNewlyLoaded()) {
				building.def.seen = true;
				return;
			}

			ArrayList arrayList = new ArrayList();
			for (int int2 = 0; int2 < IsoWorld.instance.getRandomizedBuildingList().size(); ++int2) {
				RandomizedBuildingBase randomizedBuildingBase = (RandomizedBuildingBase)IsoWorld.instance.getRandomizedBuildingList().get(int2);
				if (randomizedBuildingBase.isAlwaysDo() && randomizedBuildingBase.isValid(building.def, false)) {
					arrayList.add(randomizedBuildingBase);
				}
			}

			building.def.seen = true;
			if (building.def.x == 10744 && building.def.y == 9409 && Rand.Next(100) < 31) {
				RBKateAndBaldspot rBKateAndBaldspot = new RBKateAndBaldspot();
				rBKateAndBaldspot.randomizeBuilding(building.def);
				return;
			}

			RandomizedBuildingBase randomizedBuildingBase2;
			if (!arrayList.isEmpty()) {
				randomizedBuildingBase2 = (RandomizedBuildingBase)arrayList.get(Rand.Next(0, arrayList.size()));
				if (randomizedBuildingBase2 != null) {
					randomizedBuildingBase2.randomizeBuilding(building.def);
					return;
				}
			}

			if (GameServer.bServer && SpawnPoints.instance.isSpawnBuilding(building.getDef())) {
				return;
			}

			randomizedBuildingBase2 = IsoWorld.instance.getRBBasic();
			if ("Tutorial".equals(Core.GameMode)) {
				return;
			}

			try {
				int int3 = 10;
				switch (SandboxOptions.instance.SurvivorHouseChance.getValue()) {
				case 1: 
					return;
				
				case 2: 
					int3 -= 5;
				
				case 3: 
				
				default: 
					break;
				
				case 4: 
					int3 += 5;
					break;
				
				case 5: 
					int3 += 10;
					break;
				
				case 6: 
					int3 += 20;
				
				}

				if (Rand.Next(100) <= int3) {
					if (totalChance == 0) {
						initAllRBMapChance();
					}

					randomizedBuildingBase2 = getRandomStory();
					if (randomizedBuildingBase2 == null) {
						return;
					}
				}

				if (randomizedBuildingBase2.isValid(building.def, false) && randomizedBuildingBase2.isTimeValid(false)) {
					randomizedBuildingBase2.randomizeBuilding(building.def);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public int getChance() {
		return this.chance;
	}

	public void setChance(int int1) {
		this.chance = int1;
	}

	public boolean isAlwaysDo() {
		return this.alwaysDo;
	}

	public void setAlwaysDo(boolean boolean1) {
		this.alwaysDo = boolean1;
	}

	private static RandomizedBuildingBase getRandomStory() {
		int int1 = Rand.Next(totalChance);
		Iterator iterator = rbMap.keySet().iterator();
		int int2 = 0;
		RandomizedBuildingBase randomizedBuildingBase;
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			randomizedBuildingBase = (RandomizedBuildingBase)iterator.next();
			int2 += (Integer)rbMap.get(randomizedBuildingBase);
		} while (int1 >= int2);

		return randomizedBuildingBase;
	}

	public ArrayList addZombiesOnSquare(int int1, String string, Integer integer, IsoGridSquare square) {
		if (!IsoWorld.getZombiesDisabled() && !"Tutorial".equals(Core.GameMode)) {
			ArrayList arrayList = new ArrayList();
			for (int int2 = 0; int2 < int1; ++int2) {
				VirtualZombieManager.instance.choices.clear();
				VirtualZombieManager.instance.choices.add(square);
				IsoZombie zombie = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.getRandom().index(), false);
				if (zombie != null) {
					if ("Kate".equals(string) || "Bob".equals(string) || "Raider".equals(string)) {
						zombie.doDirtBloodEtc = false;
					}

					if (integer != null) {
						zombie.setFemaleEtc(Rand.Next(100) < integer);
					}

					if (string != null) {
						zombie.dressInPersistentOutfit(string);
						zombie.bDressInRandomOutfit = false;
					} else {
						zombie.bDressInRandomOutfit = true;
					}

					arrayList.add(zombie);
				}
			}

			ZombieSpawnRecorder.instance.record(arrayList, this.getClass().getSimpleName());
			return arrayList;
		} else {
			return null;
		}
	}

	public ArrayList addZombies(BuildingDef buildingDef, int int1, String string, Integer integer, RoomDef roomDef) {
		boolean boolean1 = roomDef == null;
		ArrayList arrayList = new ArrayList();
		if (!IsoWorld.getZombiesDisabled() && !"Tutorial".equals(Core.GameMode)) {
			if (roomDef == null) {
				roomDef = this.getRandomRoom(buildingDef, 6);
			}

			int int2 = 2;
			int int3 = roomDef.area / 2;
			if (int1 == 0) {
				if (SandboxOptions.instance.Zombies.getValue() == 1) {
					int3 += 4;
				} else if (SandboxOptions.instance.Zombies.getValue() == 2) {
					int3 += 3;
				} else if (SandboxOptions.instance.Zombies.getValue() == 3) {
					int3 += 2;
				} else if (SandboxOptions.instance.Zombies.getValue() == 5) {
					int3 -= 4;
				}

				if (int3 > 8) {
					int3 = 8;
				}

				if (int3 < int2) {
					int3 = int2 + 1;
				}
			} else {
				int2 = int1;
				int3 = int1;
			}

			int int4 = Rand.Next(int2, int3);
			for (int int5 = 0; int5 < int4; ++int5) {
				IsoGridSquare square = getRandomSpawnSquare(roomDef);
				if (square == null) {
					break;
				}

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
						zombie.bDressInRandomOutfit = true;
					}

					arrayList.add(zombie);
					if (boolean1) {
						roomDef = this.getRandomRoom(buildingDef, 6);
					}
				}
			}

			ZombieSpawnRecorder.instance.record(arrayList, this.getClass().getSimpleName());
			return arrayList;
		} else {
			return arrayList;
		}
	}

	public HandWeapon addRandomRangedWeapon(ItemContainer itemContainer, boolean boolean1, boolean boolean2, boolean boolean3) {
		if (weaponsList == null || weaponsList.isEmpty()) {
			this.init();
		}

		ArrayList arrayList = new ArrayList(weaponsList.keySet());
		String string = (String)arrayList.get(Rand.Next(0, arrayList.size()));
		HandWeapon handWeapon = this.addWeapon(string, boolean1);
		if (handWeapon == null) {
			return null;
		} else {
			if (boolean2) {
				itemContainer.addItem(InventoryItemFactory.CreateItem((String)weaponsList.get(string)));
			}

			if (boolean3) {
				KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("WeaponUpgrades");
				if (kahluaTable == null) {
					return null;
				}

				KahluaTable kahluaTable2 = (KahluaTable)kahluaTable.rawget(handWeapon.getType());
				if (kahluaTable2 == null) {
					return null;
				}

				int int1 = Rand.Next(1, kahluaTable2.len() + 1);
				for (int int2 = 1; int2 <= int1; ++int2) {
					int int3 = Rand.Next(kahluaTable2.len()) + 1;
					WeaponPart weaponPart = (WeaponPart)InventoryItemFactory.CreateItem((String)kahluaTable2.rawget(int3));
					handWeapon.attachWeaponPart(weaponPart);
				}
			}

			return handWeapon;
		}
	}

	public void spawnItemsInContainers(BuildingDef buildingDef, String string, int int1) {
		ArrayList arrayList = new ArrayList();
		ItemPickerJava.ItemPickerRoom itemPickerRoom = (ItemPickerJava.ItemPickerRoom)ItemPickerJava.rooms.get(string);
		IsoCell cell = IsoWorld.instance.CurrentCell;
		int int2;
		for (int2 = buildingDef.x - 1; int2 < buildingDef.x2 + 1; ++int2) {
			for (int int3 = buildingDef.y - 1; int3 < buildingDef.y2 + 1; ++int3) {
				for (int int4 = 0; int4 < 8; ++int4) {
					IsoGridSquare square = cell.getGridSquare(int2, int3, int4);
					if (square != null) {
						for (int int5 = 0; int5 < square.getObjects().size(); ++int5) {
							IsoObject object = (IsoObject)square.getObjects().get(int5);
							if (Rand.Next(100) <= int1 && object.getContainer() != null && square.getRoom() != null && square.getRoom().getName() != null && itemPickerRoom.Containers.containsKey(object.getContainer().getType())) {
								object.getContainer().clear();
								arrayList.add(object.getContainer());
								object.getContainer().setExplored(true);
							}
						}
					}
				}
			}
		}

		for (int2 = 0; int2 < arrayList.size(); ++int2) {
			ItemContainer itemContainer = (ItemContainer)arrayList.get(int2);
			ItemPickerJava.fillContainerType(itemPickerRoom, itemContainer, "", (IsoGameCharacter)null);
			ItemPickerJava.updateOverlaySprite(itemContainer.getParent());
			if (GameServer.bServer) {
				GameServer.sendItemsInContainer(itemContainer.getParent(), itemContainer);
			}
		}
	}

	protected void removeAllZombies(BuildingDef buildingDef) {
		for (int int1 = buildingDef.x - 1; int1 < buildingDef.x + buildingDef.x2 + 1; ++int1) {
			for (int int2 = buildingDef.y - 1; int2 < buildingDef.y + buildingDef.y2 + 1; ++int2) {
				for (int int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = this.getSq(int1, int2, int3);
					if (square != null) {
						for (int int4 = 0; int4 < square.getMovingObjects().size(); ++int4) {
							square.getMovingObjects().remove(int4);
							--int4;
						}
					}
				}
			}
		}
	}

	public IsoWindow getWindow(IsoGridSquare square) {
		for (int int1 = 0; int1 < square.getObjects().size(); ++int1) {
			IsoObject object = (IsoObject)square.getObjects().get(int1);
			if (object instanceof IsoWindow) {
				return (IsoWindow)object;
			}
		}

		return null;
	}

	public IsoDoor getDoor(IsoGridSquare square) {
		for (int int1 = 0; int1 < square.getObjects().size(); ++int1) {
			IsoObject object = (IsoObject)square.getObjects().get(int1);
			if (object instanceof IsoDoor) {
				return (IsoDoor)object;
			}
		}

		return null;
	}

	public void addBarricade(IsoGridSquare square, int int1) {
		for (int int2 = 0; int2 < square.getObjects().size(); ++int2) {
			IsoObject object = (IsoObject)square.getObjects().get(int2);
			IsoGridSquare square2;
			boolean boolean1;
			IsoBarricade barricade;
			int int3;
			if (object instanceof IsoDoor) {
				if (!((IsoDoor)object).isBarricadeAllowed()) {
					continue;
				}

				square2 = square.getRoom() == null ? square : ((IsoDoor)object).getOppositeSquare();
				if (square2 != null && square2.getRoom() == null) {
					boolean1 = square2 != square;
					barricade = IsoBarricade.AddBarricadeToObject((IsoDoor)object, boolean1);
					if (barricade != null) {
						for (int3 = 0; int3 < int1; ++int3) {
							barricade.addPlank((IsoGameCharacter)null, (InventoryItem)null);
						}

						if (GameServer.bServer) {
							barricade.transmitCompleteItemToClients();
						}
					}
				}
			}

			if (object instanceof IsoWindow && ((IsoWindow)object).isBarricadeAllowed()) {
				square2 = square.getRoom() == null ? square : ((IsoWindow)object).getOppositeSquare();
				boolean1 = square2 != square;
				barricade = IsoBarricade.AddBarricadeToObject((IsoWindow)object, boolean1);
				if (barricade != null) {
					for (int3 = 0; int3 < int1; ++int3) {
						barricade.addPlank((IsoGameCharacter)null, (InventoryItem)null);
					}

					if (GameServer.bServer) {
						barricade.transmitCompleteItemToClients();
					}
				}
			}
		}
	}

	public InventoryItem addWorldItem(String string, IsoGridSquare square, float float1, float float2, float float3) {
		return this.addWorldItem(string, square, float1, float2, float3, 0);
	}

	public InventoryItem addWorldItem(String string, IsoGridSquare square, float float1, float float2, float float3, int int1) {
		if (string != null && square != null) {
			InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
			if (inventoryItem != null) {
				inventoryItem.setAutoAge();
				inventoryItem.setWorldZRotation(int1);
				if (inventoryItem instanceof HandWeapon) {
					inventoryItem.setCondition(Rand.Next(2, inventoryItem.getConditionMax()));
				}

				return square.AddWorldInventoryItem(inventoryItem, float1, float2, float3);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public InventoryItem addWorldItem(String string, IsoGridSquare square, IsoObject object) {
		if (string != null && square != null) {
			float float1 = 0.0F;
			if (object != null) {
				float1 = object.getSurfaceOffsetNoTable() / 96.0F;
			}

			InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
			if (inventoryItem != null) {
				inventoryItem.setAutoAge();
				return square.AddWorldInventoryItem(inventoryItem, Rand.Next(0.3F, 0.9F), Rand.Next(0.3F, 0.9F), float1);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public boolean isTableFor3DItems(IsoObject object, IsoGridSquare square) {
		return object.getSurfaceOffsetNoTable() > 0.0F && object.getContainer() == null && square.getProperties().Val("waterAmount") == null && !object.hasWater() && object.getProperties().Val("BedType") == null;
	}

	public static final class HumanCorpse extends IsoGameCharacter implements IHumanVisual {
		final HumanVisual humanVisual = new HumanVisual(this);
		final ItemVisuals itemVisuals = new ItemVisuals();
		public boolean isSkeleton = false;

		public HumanCorpse(IsoCell cell, float float1, float float2, float float3) {
			super(cell, float1, float2, float3);
			cell.getObjectList().remove(this);
			cell.getAddList().remove(this);
		}

		public void dressInNamedOutfit(String string) {
			this.getHumanVisual().dressInNamedOutfit(string, this.itemVisuals);
			this.getHumanVisual().synchWithOutfit(this.getHumanVisual().getOutfit());
		}

		public HumanVisual getHumanVisual() {
			return this.humanVisual;
		}

		public HumanVisual getVisual() {
			return this.humanVisual;
		}

		public void Dressup(SurvivorDesc survivorDesc) {
			this.wornItems.setFromItemVisuals(this.itemVisuals);
			this.wornItems.addItemsToItemContainer(this.inventory);
		}

		public boolean isSkeleton() {
			return this.isSkeleton;
		}
	}
}
