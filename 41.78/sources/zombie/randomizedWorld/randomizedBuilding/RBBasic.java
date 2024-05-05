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
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoTelevision;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameServer;
import zombie.randomizedWorld.randomizedBuilding.TableStories.RBTableStoryBase;
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
	private static final HashMap kitchenSinkItems = new HashMap();
	private static final HashMap kitchenCounterItems = new HashMap();
	private static final HashMap kitchenStoveItems = new HashMap();
	private static final HashMap bathroomSinkItems = new HashMap();
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

							if (!this.tablesDone.contains(object) && object.getProperties().isTable() && object.getContainer() == null && !this.doneTable) {
								this.checkForTableSpawn(buildingDef, object);
							}
						}

						if (square.getRoom() != null && "kitchen".equals(square.getRoom().getName())) {
							this.doKitchenStuff(square);
						}

						if (square.getRoom() != null && "bathroom".equals(square.getRoom().getName())) {
							this.doBathroomStuff(square);
						}

						if (square.getRoom() != null && "bedroom".equals(square.getRoom().getName())) {
							this.doBedroomStuff(square);
						}

						if (square.getRoom() != null && "livingroom".equals(square.getRoom().getName())) {
							this.doLivingRoomStuff(square);
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

	private void doLivingRoomStuff(IsoGridSquare square) {
		IsoObject object = null;
		boolean boolean1 = false;
		int int1;
		for (int1 = 0; int1 < square.getObjects().size(); ++int1) {
			IsoObject object2 = (IsoObject)square.getObjects().get(int1);
			if (Rand.NextBool(5) && object2.getProperties().Val("BedType") == null && object2.getSurfaceOffsetNoTable() > 0.0F && object2.getSurfaceOffsetNoTable() < 30.0F && !(object2 instanceof IsoRadio)) {
				object = object2;
			}

			if (object2 instanceof IsoRadio || object2 instanceof IsoTelevision) {
				boolean1 = true;
				break;
			}
		}

		if (!boolean1 && object != null) {
			int1 = Rand.Next(0, 6);
			String string = "Base.TVRemote";
			switch (int1) {
			case 0: 
				string = "Base.TVRemote";
				break;
			
			case 1: 
				string = "Base.TVMagazine";
				break;
			
			case 2: 
				string = "Base.Newspaper";
				break;
			
			case 3: 
				string = "Base.VideoGame";
				break;
			
			case 4: 
				string = "Base.Mugl";
				break;
			
			case 5: 
				string = "Base.Headphones";
			
			}

			IsoDirections directions = this.getFacing(object.getSprite());
			if (directions != null) {
				if (directions == IsoDirections.E) {
					this.addWorldItem(string, square, 0.4F, Rand.Next(0.34F, 0.74F), object.getSurfaceOffsetNoTable() / 96.0F);
				}

				if (directions == IsoDirections.W) {
					this.addWorldItem(string, square, 0.64F, Rand.Next(0.34F, 0.74F), object.getSurfaceOffsetNoTable() / 96.0F);
				}

				if (directions == IsoDirections.N) {
					this.addWorldItem(string, square, Rand.Next(0.44F, 0.64F), 0.67F, object.getSurfaceOffsetNoTable() / 96.0F);
				}

				if (directions == IsoDirections.S) {
					this.addWorldItem(string, square, Rand.Next(0.44F, 0.64F), 0.42F, object.getSurfaceOffsetNoTable() / 96.0F);
				}
			}
		}
	}

	private void doBedroomStuff(IsoGridSquare square) {
		for (int int1 = 0; int1 < square.getObjects().size(); ++int1) {
			IsoObject object = (IsoObject)square.getObjects().get(int1);
			if (object.getSprite() == null || object.getSprite().getName() == null) {
				return;
			}

			int int2;
			if (Rand.NextBool(7) && object.getSprite().getName().contains("bedding") && object.getProperties().Val("BedType") != null) {
				int2 = Rand.Next(0, 14);
				switch (int2) {
				case 0: 
					this.addWorldItem("Shirt_FormalTINT", square, 0.6F, 0.6F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 1: 
					this.addWorldItem("Shirt_FormalWhite_ShortSleeveTINT", square, 0.6F, 0.6F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 2: 
					this.addWorldItem("Tshirt_DefaultDECAL_TINT", square, 0.6F, 0.6F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 3: 
					this.addWorldItem("Tshirt_PoloStripedTINT", square, 0.6F, 0.6F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 4: 
					this.addWorldItem("Tshirt_PoloTINT", square, 0.6F, 0.6F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 5: 
					this.addWorldItem("Jacket_WhiteTINT", square, 0.6F, 0.6F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 6: 
					this.addWorldItem("Jumper_DiamondPatternTINT", square, 0.6F, 0.6F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 7: 
					this.addWorldItem("Jumper_TankTopDiamondTINT", square, 0.6F, 0.6F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 8: 
					this.addWorldItem("HoodieDOWN_WhiteTINT", square, 0.6F, 0.6F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 9: 
					this.addWorldItem("Trousers_DefaultTEXTURE_TINT", square, 0.6F, 0.6F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 10: 
					this.addWorldItem("Trousers_WhiteTINT", square, 0.6F, 0.6F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 11: 
					this.addWorldItem("Trousers_Denim", square, 0.6F, 0.6F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 12: 
					this.addWorldItem("Trousers_Padded", square, 0.6F, 0.6F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 13: 
					this.addWorldItem("TrousersMesh_DenimLight", square, 0.6F, 0.6F, object.getSurfaceOffsetNoTable() / 96.0F);
				
				}
			}

			if (Rand.NextBool(7) && object.getContainer() != null && "sidetable".equals(object.getContainer().getType())) {
				int2 = Rand.Next(0, 4);
				String string = "Base.Book";
				switch (int2) {
				case 0: 
					string = "Base.Book";
					break;
				
				case 1: 
					string = "Base.Notebook";
					break;
				
				case 2: 
					string = "Base.VideoGame";
					break;
				
				case 3: 
					string = "Base.CDPlayer";
				
				}

				IsoDirections directions = this.getFacing(object.getSprite());
				if (directions != null) {
					if (directions == IsoDirections.E) {
						this.addWorldItem(string, square, 0.42F, Rand.Next(0.34F, 0.74F), object.getSurfaceOffsetNoTable() / 96.0F);
					}

					if (directions == IsoDirections.W) {
						this.addWorldItem(string, square, 0.64F, Rand.Next(0.34F, 0.74F), object.getSurfaceOffsetNoTable() / 96.0F);
					}

					if (directions == IsoDirections.N) {
						this.addWorldItem(string, square, Rand.Next(0.44F, 0.64F), 0.67F, object.getSurfaceOffsetNoTable() / 96.0F);
					}

					if (directions == IsoDirections.S) {
						this.addWorldItem(string, square, Rand.Next(0.44F, 0.64F), 0.42F, object.getSurfaceOffsetNoTable() / 96.0F);
					}
				}

				return;
			}
		}
	}

	private void doKitchenStuff(IsoGridSquare square) {
		boolean boolean1 = false;
		boolean boolean2 = false;
		for (int int1 = 0; int1 < square.getObjects().size(); ++int1) {
			IsoObject object = (IsoObject)square.getObjects().get(int1);
			if (object.getSprite() == null || object.getSprite().getName() == null) {
				return;
			}

			IsoDirections directions;
			if (!boolean1 && object.getSprite().getName().contains("sink") && Rand.NextBool(4)) {
				directions = this.getFacing(object.getSprite());
				if (directions != null) {
					this.generateSinkClutter(directions, object, square, kitchenSinkItems);
					boolean1 = true;
				}
			} else if (!boolean2 && object.getContainer() != null && "counter".equals(object.getContainer().getType()) && Rand.NextBool(6)) {
				boolean boolean3 = true;
				for (int int2 = 0; int2 < square.getObjects().size(); ++int2) {
					IsoObject object2 = (IsoObject)square.getObjects().get(int2);
					if (object2.getSprite() != null && object2.getSprite().getName() != null && object2.getSprite().getName().contains("sink") || object2 instanceof IsoStove || object2 instanceof IsoRadio) {
						boolean3 = false;
						break;
					}
				}

				if (boolean3) {
					IsoDirections directions2 = this.getFacing(object.getSprite());
					if (directions2 != null) {
						this.generateCounterClutter(directions2, object, square, kitchenCounterItems);
						boolean2 = true;
					}
				}
			} else if (object instanceof IsoStove && object.getContainer() != null && "stove".equals(object.getContainer().getType()) && Rand.NextBool(4)) {
				directions = this.getFacing(object.getSprite());
				if (directions != null) {
					this.generateKitchenStoveClutter(directions, object, square);
				}
			}
		}
	}

	private void doBathroomStuff(IsoGridSquare square) {
		boolean boolean1 = false;
		boolean boolean2 = false;
		for (int int1 = 0; int1 < square.getObjects().size(); ++int1) {
			IsoObject object = (IsoObject)square.getObjects().get(int1);
			if (object.getSprite() == null || object.getSprite().getName() == null) {
				return;
			}

			if (!boolean1 && !boolean2 && object.getSprite().getName().contains("sink") && Rand.NextBool(5) && object.getSurfaceOffsetNoTable() > 0.0F) {
				IsoDirections directions = this.getFacing(object.getSprite());
				if (directions != null) {
					this.generateSinkClutter(directions, object, square, bathroomSinkItems);
					boolean1 = true;
				}
			} else if (!boolean1 && !boolean2 && object.getContainer() != null && "counter".equals(object.getContainer().getType()) && Rand.NextBool(5)) {
				boolean boolean3 = true;
				for (int int2 = 0; int2 < square.getObjects().size(); ++int2) {
					IsoObject object2 = (IsoObject)square.getObjects().get(int2);
					if (object2.getSprite() != null && object2.getSprite().getName() != null && object2.getSprite().getName().contains("sink") || object2 instanceof IsoStove || object2 instanceof IsoRadio) {
						boolean3 = false;
						break;
					}
				}

				if (boolean3) {
					IsoDirections directions2 = this.getFacing(object.getSprite());
					if (directions2 != null) {
						this.generateCounterClutter(directions2, object, square, bathroomSinkItems);
						boolean2 = true;
					}
				}
			}
		}
	}

	private void generateKitchenStoveClutter(IsoDirections directions, IsoObject object, IsoGridSquare square) {
		int int1 = Rand.Next(1, 3);
		String string = (String)kitchenStoveItems.get(Rand.Next(1, kitchenStoveItems.size()));
		if (directions == IsoDirections.W) {
			switch (int1) {
			case 1: 
				this.addWorldItem(string, square, 0.5703125F, 0.8046875F, object.getSurfaceOffsetNoTable() / 96.0F);
				break;
			
			case 2: 
				this.addWorldItem(string, square, 0.5703125F, 0.2578125F, object.getSurfaceOffsetNoTable() / 96.0F);
			
			}
		}

		if (directions == IsoDirections.E) {
			switch (int1) {
			case 1: 
				this.addWorldItem(string, square, 0.5F, 0.7890625F, object.getSurfaceOffsetNoTable() / 96.0F);
				break;
			
			case 2: 
				this.addWorldItem(string, square, 0.5F, 0.1875F, object.getSurfaceOffsetNoTable() / 96.0F);
			
			}
		}

		if (directions == IsoDirections.S) {
			switch (int1) {
			case 1: 
				this.addWorldItem(string, square, 0.3125F, 0.53125F, object.getSurfaceOffsetNoTable() / 96.0F);
				break;
			
			case 2: 
				this.addWorldItem(string, square, 0.875F, 0.53125F, object.getSurfaceOffsetNoTable() / 96.0F);
			
			}
		}

		if (directions == IsoDirections.N) {
			switch (int1) {
			case 1: 
				this.addWorldItem(string, square, 0.3203F, 0.523475F, object.getSurfaceOffsetNoTable() / 96.0F);
				break;
			
			case 2: 
				this.addWorldItem(string, square, 0.8907F, 0.523475F, object.getSurfaceOffsetNoTable() / 96.0F);
			
			}
		}
	}

	private void generateCounterClutter(IsoDirections directions, IsoObject object, IsoGridSquare square, HashMap hashMap) {
		int int1 = Math.min(5, hashMap.size() + 1);
		int int2 = Rand.Next(1, int1);
		ArrayList arrayList = new ArrayList();
		int int3;
		for (int int4 = 0; int4 < int2; ++int4) {
			int3 = Rand.Next(1, 5);
			boolean boolean1 = false;
			while (!boolean1) {
				if (!arrayList.contains(int3)) {
					arrayList.add(int3);
					boolean1 = true;
				} else {
					int3 = Rand.Next(1, 5);
				}
			}

			if (arrayList.size() == 4) {
			}
		}

		ArrayList arrayList2 = new ArrayList();
		for (int3 = 0; int3 < arrayList.size(); ++int3) {
			int int5 = (Integer)arrayList.get(int3);
			int int6 = Rand.Next(1, hashMap.size() + 1);
			String string = null;
			while (string == null) {
				string = (String)hashMap.get(int6);
				if (arrayList2.contains(string)) {
					string = null;
					int6 = Rand.Next(1, hashMap.size() + 1);
				}
			}

			arrayList2.add(string);
			if (directions == IsoDirections.S) {
				switch (int5) {
				case 1: 
					this.addWorldItem(string, square, 0.138F, Rand.Next(0.2F, 0.523F), object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 2: 
					this.addWorldItem(string, square, 0.383F, Rand.Next(0.2F, 0.523F), object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 3: 
					this.addWorldItem(string, square, 0.633F, Rand.Next(0.2F, 0.523F), object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 4: 
					this.addWorldItem(string, square, 0.78F, Rand.Next(0.2F, 0.523F), object.getSurfaceOffsetNoTable() / 96.0F);
				
				}
			}

			if (directions == IsoDirections.N) {
				switch (int5) {
				case 1: 
					square.AddWorldInventoryItem(string, 0.133F, Rand.Next(0.53125F, 0.9375F), object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 2: 
					square.AddWorldInventoryItem(string, 0.38F, Rand.Next(0.53125F, 0.9375F), object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 3: 
					square.AddWorldInventoryItem(string, 0.625F, Rand.Next(0.53125F, 0.9375F), object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 4: 
					square.AddWorldInventoryItem(string, 0.92F, Rand.Next(0.53125F, 0.9375F), object.getSurfaceOffsetNoTable() / 96.0F);
				
				}
			}

			if (directions == IsoDirections.E) {
				switch (int5) {
				case 1: 
					square.AddWorldInventoryItem(string, Rand.Next(0.226F, 0.593F), 0.14F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 2: 
					square.AddWorldInventoryItem(string, Rand.Next(0.226F, 0.593F), 0.33F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 3: 
					square.AddWorldInventoryItem(string, Rand.Next(0.226F, 0.593F), 0.64F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 4: 
					square.AddWorldInventoryItem(string, Rand.Next(0.226F, 0.593F), 0.92F, object.getSurfaceOffsetNoTable() / 96.0F);
				
				}
			}

			if (directions == IsoDirections.W) {
				switch (int5) {
				case 1: 
					square.AddWorldInventoryItem(string, Rand.Next(0.5859375F, 0.9F), 0.21875F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 2: 
					square.AddWorldInventoryItem(string, Rand.Next(0.5859375F, 0.9F), 0.421875F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 3: 
					square.AddWorldInventoryItem(string, Rand.Next(0.5859375F, 0.9F), 0.71F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 4: 
					square.AddWorldInventoryItem(string, Rand.Next(0.5859375F, 0.9F), 0.9175F, object.getSurfaceOffsetNoTable() / 96.0F);
				
				}
			}
		}
	}

	private void generateSinkClutter(IsoDirections directions, IsoObject object, IsoGridSquare square, HashMap hashMap) {
		int int1 = Math.min(5, hashMap.size() + 1);
		int int2 = Rand.Next(1, int1);
		ArrayList arrayList = new ArrayList();
		int int3;
		for (int int4 = 0; int4 < int2; ++int4) {
			int3 = Rand.Next(1, 5);
			boolean boolean1 = false;
			while (!boolean1) {
				if (!arrayList.contains(int3)) {
					arrayList.add(int3);
					boolean1 = true;
				} else {
					int3 = Rand.Next(1, 5);
				}
			}

			if (arrayList.size() == 4) {
			}
		}

		ArrayList arrayList2 = new ArrayList();
		for (int3 = 0; int3 < arrayList.size(); ++int3) {
			int int5 = (Integer)arrayList.get(int3);
			int int6 = Rand.Next(1, hashMap.size() + 1);
			String string = null;
			while (string == null) {
				string = (String)hashMap.get(int6);
				if (arrayList2.contains(string)) {
					string = null;
					int6 = Rand.Next(1, hashMap.size() + 1);
				}
			}

			arrayList2.add(string);
			if (directions == IsoDirections.S) {
				switch (int5) {
				case 1: 
					this.addWorldItem(string, square, 0.71875F, 0.125F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 2: 
					this.addWorldItem(string, square, 0.0935F, 0.21875F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 3: 
					this.addWorldItem(string, square, 0.1328125F, 0.589375F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 4: 
					this.addWorldItem(string, square, 0.7890625F, 0.589375F, object.getSurfaceOffsetNoTable() / 96.0F);
				
				}
			}

			if (directions == IsoDirections.N) {
				switch (int5) {
				case 1: 
					this.addWorldItem(string, square, 0.921875F, 0.921875F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 2: 
					this.addWorldItem(string, square, 0.1640625F, 0.8984375F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 3: 
					this.addWorldItem(string, square, 0.021875F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 4: 
					this.addWorldItem(string, square, 0.8671875F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
				
				}
			}

			if (directions == IsoDirections.E) {
				switch (int5) {
				case 1: 
					this.addWorldItem(string, square, 0.234375F, 0.859375F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 2: 
					this.addWorldItem(string, square, 0.59375F, 0.875F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 3: 
					this.addWorldItem(string, square, 0.53125F, 0.125F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 4: 
					this.addWorldItem(string, square, 0.210937F, 0.1328125F, object.getSurfaceOffsetNoTable() / 96.0F);
				
				}
			}

			if (directions == IsoDirections.W) {
				switch (int5) {
				case 1: 
					this.addWorldItem(string, square, 0.515625F, 0.109375F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 2: 
					this.addWorldItem(string, square, 0.578125F, 0.890625F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 3: 
					this.addWorldItem(string, square, 0.8828125F, 0.8984375F, object.getSurfaceOffsetNoTable() / 96.0F);
					break;
				
				case 4: 
					this.addWorldItem(string, square, 0.8671875F, 0.1653125F, object.getSurfaceOffsetNoTable() / 96.0F);
				
				}
			}
		}
	}

	private IsoDirections getFacing(IsoSprite sprite) {
		if (sprite != null && sprite.getProperties().Is("Facing")) {
			String string = sprite.getProperties().Val("Facing");
			byte byte1 = -1;
			switch (string.hashCode()) {
			case 69: 
				if (string.equals("E")) {
					byte1 = 3;
				}

				break;
			
			case 78: 
				if (string.equals("N")) {
					byte1 = 0;
				}

				break;
			
			case 83: 
				if (string.equals("S")) {
					byte1 = 1;
				}

				break;
			
			case 87: 
				if (string.equals("W")) {
					byte1 = 2;
				}

			
			}

			switch (byte1) {
			case 0: 
				return IsoDirections.N;
			
			case 1: 
				return IsoDirections.S;
			
			case 2: 
				return IsoDirections.W;
			
			case 3: 
				return IsoDirections.E;
			
			}
		}

		return null;
	}

	private void checkForTableSpawn(BuildingDef buildingDef, IsoObject object) {
		if (Rand.NextBool(10)) {
			RBTableStoryBase rBTableStoryBase = RBTableStoryBase.getRandomStory(object.getSquare(), object);
			if (rBTableStoryBase != null) {
				rBTableStoryBase.randomizeBuilding(buildingDef);
				this.doneTable = true;
			}
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
		kitchenSinkItems.put(1, "Soap2");
		kitchenSinkItems.put(2, "CleaningLiquid2");
		kitchenSinkItems.put(3, "Sponge");
		kitchenCounterItems.put(1, "Dogfood");
		kitchenCounterItems.put(2, "CannedCorn");
		kitchenCounterItems.put(3, "CannedPeas");
		kitchenCounterItems.put(4, "CannedPotato2");
		kitchenCounterItems.put(5, "CannedSardines");
		kitchenCounterItems.put(6, "CannedTomato2");
		kitchenCounterItems.put(7, "CannedCarrots2");
		kitchenCounterItems.put(8, "CannedChili");
		kitchenCounterItems.put(9, "CannedBolognese");
		kitchenCounterItems.put(10, "TinOpener");
		kitchenCounterItems.put(11, "WaterBottleFull");
		kitchenCounterItems.put(12, "Cereal");
		kitchenCounterItems.put(13, "CerealBowl");
		kitchenCounterItems.put(14, "Spoon");
		kitchenCounterItems.put(15, "Fork");
		kitchenCounterItems.put(16, "KitchenKnife");
		kitchenCounterItems.put(17, "ButterKnife");
		kitchenCounterItems.put(18, "BreadKnife");
		kitchenCounterItems.put(19, "DishCloth");
		kitchenCounterItems.put(20, "RollingPin");
		kitchenCounterItems.put(21, "EmptyJar");
		kitchenCounterItems.put(22, "Bowl");
		kitchenCounterItems.put(23, "MugWhite");
		kitchenCounterItems.put(24, "MugRed");
		kitchenCounterItems.put(25, "Mugl");
		kitchenCounterItems.put(26, "WaterPot");
		kitchenCounterItems.put(27, "WaterSaucepan");
		kitchenCounterItems.put(28, "PotOfSoup");
		kitchenCounterItems.put(29, "StewBowl");
		kitchenCounterItems.put(30, "SoupBowl");
		kitchenCounterItems.put(31, "WaterSaucepanPasta");
		kitchenCounterItems.put(32, "WaterSaucepanRice");
		kitchenStoveItems.put(1, "WaterSaucepanRice");
		kitchenStoveItems.put(2, "WaterSaucepanPasta");
		kitchenStoveItems.put(3, "WaterPot");
		kitchenStoveItems.put(4, "PotOfSoup");
		kitchenStoveItems.put(5, "WaterSaucepan");
		kitchenStoveItems.put(6, "PotOfStew");
		kitchenStoveItems.put(7, "PastaPot");
		kitchenStoveItems.put(8, "RicePot");
		bathroomSinkItems.put(1, "Comb");
		bathroomSinkItems.put(2, "Cologne");
		bathroomSinkItems.put(3, "Antibiotics");
		bathroomSinkItems.put(4, "Bandage");
		bathroomSinkItems.put(5, "Pills");
		bathroomSinkItems.put(6, "PillsAntiDep");
		bathroomSinkItems.put(7, "PillsBeta");
		bathroomSinkItems.put(8, "PillsSleepingTablets");
		bathroomSinkItems.put(9, "PillsVitamins");
		bathroomSinkItems.put(10, "Lipstick");
		bathroomSinkItems.put(11, "MakeupEyeshadow");
		bathroomSinkItems.put(12, "MakeupFoundation");
		bathroomSinkItems.put(13, "Perfume");
		bathroomSinkItems.put(14, "Razor");
		bathroomSinkItems.put(15, "Toothbrush");
		bathroomSinkItems.put(16, "Toothpaste");
		bathroomSinkItems.put(17, "Tweezers");
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
