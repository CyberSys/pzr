package zombie.randomizedWorld.randomizedBuilding;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import se.krka.kahlua.vm.KahluaTable;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemPickerJava;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameServer;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBandPractice;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBathroomZed;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBedroomZed;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBleach;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSCorpsePsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSDeadDrunk;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSFootballNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSGunmanInBathroom;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSGunslinger;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHenDo;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHockeyPsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHouseParty;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPokerNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPoliceAtHouse;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPrisonEscape;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPrisonEscapeWithPolice;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSkeletonPsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSpecificProfession;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSStagDo;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSStudentNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSuicidePact;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSTinFoilHat;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSZombieLockedBathroom;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSZombiesEating;
import zombie.randomizedWorld.randomizedDeadSurvivor.RandomizedDeadSurvivorBase;


public final class RBBasic extends RandomizedBuildingBase {
	private final ArrayList specificProfessionDistribution = new ArrayList();
	private final Map specificProfessionRoomDistribution = new HashMap();
	private final ArrayList coldFood = new ArrayList();
	private final Map plankStash = new HashMap();
	private final ArrayList deadSurvivorsStory = new ArrayList();
	private int totalChanceRDS = 0;
	private static final HashMap rdsMap = new HashMap();
	private static final ArrayList uniqueRDSSpawned = new ArrayList();
	private ArrayList tablesDone = new ArrayList();
	private boolean doneTable = false;

	public void randomizeBuilding(BuildingDef buildingDef) {
		this.tablesDone = new ArrayList();
		boolean boolean1 = Rand.Next(100) <= 20;
		ArrayList arrayList = new ArrayList();
		String string = (String)this.specificProfessionDistribution.get(Rand.Next(0, this.specificProfessionDistribution.size()));
		ItemPickerJava.ItemPickerRoom itemPickerRoom = (ItemPickerJava.ItemPickerRoom)ItemPickerJava.rooms.get(string);
		IsoCell cell = IsoWorld.instance.CurrentCell;
		boolean boolean2 = Rand.NextBool(9);
		int int1;
		for (int1 = buildingDef.x - 1; int1 < buildingDef.x2 + 1; ++int1) {
			for (int int2 = buildingDef.y - 1; int2 < buildingDef.y2 + 1; ++int2) {
				for (int int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
					if (square != null) {
						if (boolean2 && square.getFloor() != null && this.plankStash.containsKey(square.getFloor().getSprite().getName())) {
							IsoThumpable thumpable = new IsoThumpable(square.getCell(), square, (String)this.plankStash.get(square.getFloor().getSprite().getName()), false, (KahluaTable)null);
							thumpable.setIsThumpable(false);
							thumpable.container = new ItemContainer("plankstash", square, thumpable);
							square.AddSpecialObject(thumpable);
							square.RecalcAllWithNeighbours(true);
							boolean2 = false;
						}

						for (int int4 = 0; int4 < square.getObjects().size(); ++int4) {
							IsoObject object = (IsoObject)square.getObjects().get(int4);
							if (Rand.Next(100) <= 65 && object instanceof IsoDoor && !((IsoDoor)object).isExteriorDoor((IsoGameCharacter)null)) {
								((IsoDoor)object).ToggleDoorSilent();
								((IsoDoor)object).syncIsoObject(true, (byte)1, (UdpConnection)null, (ByteBuffer)null);
							}

							if (object instanceof IsoWindow) {
								IsoWindow window = (IsoWindow)object;
								if (Rand.NextBool(80)) {
									buildingDef.bAlarmed = false;
									window.ToggleWindow((IsoGameCharacter)null);
								}

								IsoCurtain curtain = window.HasCurtains();
								if (curtain != null && Rand.NextBool(15)) {
									curtain.ToggleDoorSilent();
								}
							}

							if (boolean1 && Rand.Next(100) <= 70 && object.getContainer() != null && square.getRoom() != null && square.getRoom().getName() != null && ((String)this.specificProfessionRoomDistribution.get(string)).contains(square.getRoom().getName()) && itemPickerRoom.Containers.containsKey(object.getContainer().getType())) {
								object.getContainer().clear();
								arrayList.add(object.getContainer());
								object.getContainer().setExplored(true);
							}

							if (Rand.Next(100) < 15 && object.getContainer() != null && object.getContainer().getType().equals("stove")) {
								InventoryItem inventoryItem = object.getContainer().AddItem((String)this.coldFood.get(Rand.Next(0, this.coldFood.size())));
								inventoryItem.setCooked(true);
								inventoryItem.setAutoAge();
							}

							if (!this.tablesDone.contains(object) && object.getProperties().isTable() && object.getProperties().getSurface() == 34 && object.getContainer() == null && !this.doneTable) {
								this.checkForTableSpawn(buildingDef, object);
							}
						}
					}
				}
			}
		}

		for (int1 = 0; int1 < arrayList.size(); ++int1) {
			ItemContainer itemContainer = (ItemContainer)arrayList.get(int1);
			ItemPickerJava.fillContainerType(itemPickerRoom, itemContainer, "", (IsoGameCharacter)null);
			ItemPickerJava.updateOverlaySprite(itemContainer.getParent());
			if (GameServer.bServer) {
				GameServer.sendItemsInContainer(itemContainer.getParent(), itemContainer);
			}
		}

		if (!boolean1 && Rand.Next(100) < 25) {
			this.addRandomDeadSurvivorStory(buildingDef);
			buildingDef.setAllExplored(true);
			buildingDef.bAlarmed = false;
		}

		this.doneTable = false;
	}

	private void checkForTableSpawn(BuildingDef buildingDef, IsoObject object) {
		IsoObject object2 = null;
		IsoCell cell = IsoWorld.instance.CurrentCell;
		object2 = this.checkForTable(cell.getGridSquare((double)((int)object.getX() - 1), (double)((int)object.getY()), (double)object.getZ()), object);
		if (object2 == null) {
			object2 = this.checkForTable(cell.getGridSquare((double)((int)object.getX() + 1), (double)((int)object.getY()), (double)object.getZ()), object);
		}

		if (object2 == null) {
			object2 = this.checkForTable(cell.getGridSquare((double)((int)object.getX()), (double)((int)object.getY() - 1), (double)object.getZ()), object);
		}

		if (object2 == null) {
			object2 = this.checkForTable(cell.getGridSquare((double)((int)object.getX()), (double)((int)object.getY() + 1), (double)object.getZ()), object);
		}

		if (object2 != null && Rand.NextBool(8)) {
			this.tablesDone.add(object);
			this.tablesDone.add(object2);
			RBTableStory rBTableStory = new RBTableStory();
			rBTableStory.table1 = object;
			rBTableStory.table2 = object2;
			rBTableStory.randomizeBuilding(buildingDef);
			rBTableStory = null;
			this.doneTable = true;
		}
	}

	private IsoObject checkForTable(IsoGridSquare square, IsoObject object) {
		if (!this.tablesDone.contains(object) && square != null) {
			for (int int1 = 0; int1 < square.getObjects().size(); ++int1) {
				IsoObject object2 = (IsoObject)square.getObjects().get(int1);
				if (!this.tablesDone.contains(object2) && object2.getProperties().isTable() && object2.getProperties().getSurface() == 34 && object2.getContainer() == null && object2 != object) {
					return object2;
				}
			}

			return null;
		} else {
			return null;
		}
	}

	public void doProfessionStory(BuildingDef buildingDef, String string) {
		this.spawnItemsInContainers(buildingDef, string, 70);
	}

	private void addRandomDeadSurvivorStory(BuildingDef buildingDef) {
		this.initRDSMap(buildingDef);
		int int1 = Rand.Next(this.totalChanceRDS);
		Iterator iterator = rdsMap.keySet().iterator();
		int int2 = 0;
		while (iterator.hasNext()) {
			RandomizedDeadSurvivorBase randomizedDeadSurvivorBase = (RandomizedDeadSurvivorBase)iterator.next();
			int2 += (Integer)rdsMap.get(randomizedDeadSurvivorBase);
			if (int1 < int2) {
				randomizedDeadSurvivorBase.randomizeDeadSurvivor(buildingDef);
				if (randomizedDeadSurvivorBase.isUnique()) {
					getUniqueRDSSpawned().add(randomizedDeadSurvivorBase.getName());
				}

				break;
			}
		}
	}

	private void initRDSMap(BuildingDef buildingDef) {
		this.totalChanceRDS = 0;
		rdsMap.clear();
		for (int int1 = 0; int1 < this.deadSurvivorsStory.size(); ++int1) {
			RandomizedDeadSurvivorBase randomizedDeadSurvivorBase = (RandomizedDeadSurvivorBase)this.deadSurvivorsStory.get(int1);
			if (randomizedDeadSurvivorBase.isValid(buildingDef, false) && randomizedDeadSurvivorBase.isTimeValid(false) && (randomizedDeadSurvivorBase.isUnique() && !getUniqueRDSSpawned().contains(randomizedDeadSurvivorBase.getName()) || !randomizedDeadSurvivorBase.isUnique())) {
				this.totalChanceRDS += ((RandomizedDeadSurvivorBase)this.deadSurvivorsStory.get(int1)).getChance();
				rdsMap.put((RandomizedDeadSurvivorBase)this.deadSurvivorsStory.get(int1), ((RandomizedDeadSurvivorBase)this.deadSurvivorsStory.get(int1)).getChance());
			}
		}
	}

	public void doRandomDeadSurvivorStory(BuildingDef buildingDef, RandomizedDeadSurvivorBase randomizedDeadSurvivorBase) {
		randomizedDeadSurvivorBase.randomizeDeadSurvivor(buildingDef);
	}

	public RBBasic() {
		this.name = "RBBasic";
		this.deadSurvivorsStory.add(new RDSBleach());
		this.deadSurvivorsStory.add(new RDSGunslinger());
		this.deadSurvivorsStory.add(new RDSGunmanInBathroom());
		this.deadSurvivorsStory.add(new RDSZombieLockedBathroom());
		this.deadSurvivorsStory.add(new RDSDeadDrunk());
		this.deadSurvivorsStory.add(new RDSSpecificProfession());
		this.deadSurvivorsStory.add(new RDSZombiesEating());
		this.deadSurvivorsStory.add(new RDSBandPractice());
		this.deadSurvivorsStory.add(new RDSBathroomZed());
		this.deadSurvivorsStory.add(new RDSBedroomZed());
		this.deadSurvivorsStory.add(new RDSFootballNight());
		this.deadSurvivorsStory.add(new RDSHenDo());
		this.deadSurvivorsStory.add(new RDSStagDo());
		this.deadSurvivorsStory.add(new RDSStudentNight());
		this.deadSurvivorsStory.add(new RDSPokerNight());
		this.deadSurvivorsStory.add(new RDSSuicidePact());
		this.deadSurvivorsStory.add(new RDSPrisonEscape());
		this.deadSurvivorsStory.add(new RDSPrisonEscapeWithPolice());
		this.deadSurvivorsStory.add(new RDSSkeletonPsycho());
		this.deadSurvivorsStory.add(new RDSCorpsePsycho());
		this.deadSurvivorsStory.add(new RDSPoliceAtHouse());
		this.deadSurvivorsStory.add(new RDSHouseParty());
		this.deadSurvivorsStory.add(new RDSTinFoilHat());
		this.deadSurvivorsStory.add(new RDSHockeyPsycho());
		this.specificProfessionDistribution.add("Carpenter");
		this.specificProfessionDistribution.add("Electrician");
		this.specificProfessionDistribution.add("Farmer");
		this.specificProfessionDistribution.add("Nurse");
		this.specificProfessionRoomDistribution.put("Carpenter", "kitchen");
		this.specificProfessionRoomDistribution.put("Electrician", "kitchen");
		this.specificProfessionRoomDistribution.put("Farmer", "kitchen");
		this.specificProfessionRoomDistribution.put("Nurse", "kitchen");
		this.specificProfessionRoomDistribution.put("Nurse", "bathroom");
		this.coldFood.add("Base.Chicken");
		this.coldFood.add("Base.Steak");
		this.coldFood.add("Base.PorkChop");
		this.coldFood.add("Base.MuttonChop");
		this.coldFood.add("Base.MeatPatty");
		this.coldFood.add("Base.FishFillet");
		this.coldFood.add("Base.Salmon");
		this.plankStash.put("floors_interior_tilesandwood_01_40", "floors_interior_tilesandwood_01_56");
		this.plankStash.put("floors_interior_tilesandwood_01_41", "floors_interior_tilesandwood_01_57");
		this.plankStash.put("floors_interior_tilesandwood_01_42", "floors_interior_tilesandwood_01_58");
		this.plankStash.put("floors_interior_tilesandwood_01_43", "floors_interior_tilesandwood_01_59");
		this.plankStash.put("floors_interior_tilesandwood_01_44", "floors_interior_tilesandwood_01_60");
		this.plankStash.put("floors_interior_tilesandwood_01_45", "floors_interior_tilesandwood_01_61");
		this.plankStash.put("floors_interior_tilesandwood_01_46", "floors_interior_tilesandwood_01_62");
		this.plankStash.put("floors_interior_tilesandwood_01_47", "floors_interior_tilesandwood_01_63");
		this.plankStash.put("floors_interior_tilesandwood_01_52", "floors_interior_tilesandwood_01_68");
	}

	public ArrayList getSurvivorStories() {
		return this.deadSurvivorsStory;
	}

	public ArrayList getSurvivorProfession() {
		return this.specificProfessionDistribution;
	}

	public static ArrayList getUniqueRDSSpawned() {
		return uniqueRDSSpawned;
	}
}
