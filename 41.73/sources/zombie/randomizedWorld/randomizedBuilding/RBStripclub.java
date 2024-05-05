package zombie.randomizedWorld.randomizedBuilding;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;


public final class RBStripclub extends RandomizedBuildingBase {

	public void randomizeBuilding(BuildingDef buildingDef) {
		buildingDef.bAlarmed = false;
		buildingDef.setHasBeenVisited(true);
		buildingDef.setAllExplored(true);
		IsoCell cell = IsoWorld.instance.CurrentCell;
		boolean boolean1 = Rand.NextBool(20);
		ArrayList arrayList = new ArrayList();
		for (int int1 = buildingDef.x - 1; int1 < buildingDef.x2 + 1; ++int1) {
			for (int int2 = buildingDef.y - 1; int2 < buildingDef.y2 + 1; ++int2) {
				for (int int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
					if (square != null) {
						for (int int4 = 0; int4 < square.getObjects().size(); ++int4) {
							IsoObject object = (IsoObject)square.getObjects().get(int4);
							int int5;
							int int6;
							if (Rand.NextBool(2) && "location_restaurant_pizzawhirled_01_16".equals(object.getSprite().getName())) {
								int5 = Rand.Next(1, 4);
								for (int6 = 0; int6 < int5; ++int6) {
									square.AddWorldInventoryItem("Money", Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
								}

								int6 = Rand.Next(1, 4);
								for (int int7 = 0; int7 < int6; ++int7) {
									int int8;
									for (int8 = Rand.Next(1, 7); arrayList.contains(int8); int8 = Rand.Next(1, 7)) {
									}

									switch (int8) {
									case 1: 
										square.AddWorldInventoryItem(boolean1 ? "Trousers" : "TightsFishnet_Ground", Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
										arrayList.add(1);
										break;
									
									case 2: 
										square.AddWorldInventoryItem("Vest_DefaultTEXTURE_TINT", Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
										arrayList.add(2);
										break;
									
									case 3: 
										square.AddWorldInventoryItem(boolean1 ? "Jacket_Fireman" : "BunnySuitBlack", Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
										arrayList.add(3);
										break;
									
									case 4: 
										square.AddWorldInventoryItem(boolean1 ? "Hat_Cowboy" : "Garter", Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
										arrayList.add(4);
										break;
									
									case 5: 
										if (!boolean1) {
											square.AddWorldInventoryItem("StockingsBlack", Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
										}

										arrayList.add(5);
									
									}
								}
							}

							if ("furniture_tables_high_01_16".equals(object.getSprite().getName()) || "furniture_tables_high_01_17".equals(object.getSprite().getName()) || "furniture_tables_high_01_18".equals(object.getSprite().getName())) {
								int5 = Rand.Next(1, 4);
								for (int6 = 0; int6 < int5; ++int6) {
									square.AddWorldInventoryItem("Money", Rand.Next(0.5F, 1.0F), Rand.Next(0.5F, 1.0F), object.getSurfaceOffsetNoTable() / 96.0F);
								}

								if (Rand.NextBool(3)) {
									this.addWorldItem("Cigarettes", square, object);
									if (Rand.NextBool(2)) {
										this.addWorldItem("Lighter", square, object);
									}
								}

								int6 = Rand.Next(7);
								switch (int6) {
								case 0: 
									square.AddWorldInventoryItem("WhiskeyFull", Rand.Next(0.5F, 1.0F), Rand.Next(0.5F, 1.0F), object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 1: 
									square.AddWorldInventoryItem("Wine", Rand.Next(0.5F, 1.0F), Rand.Next(0.5F, 1.0F), object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 2: 
									square.AddWorldInventoryItem("Wine2", Rand.Next(0.5F, 1.0F), Rand.Next(0.5F, 1.0F), object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 3: 
									square.AddWorldInventoryItem("BeerCan", Rand.Next(0.5F, 1.0F), Rand.Next(0.5F, 1.0F), object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 4: 
									square.AddWorldInventoryItem("BeerBottle", Rand.Next(0.5F, 1.0F), Rand.Next(0.5F, 1.0F), object.getSurfaceOffsetNoTable() / 96.0F);
								
								}
							}
						}
					}
				}
			}
		}

		RoomDef roomDef = buildingDef.getRoom("stripclub");
		if (boolean1) {
			this.addZombies(buildingDef, Rand.Next(2, 4), "WaiterStripper", 0, roomDef);
			this.addZombies(buildingDef, 1, "PoliceStripper", 0, roomDef);
			this.addZombies(buildingDef, 1, "FiremanStripper", 0, roomDef);
			this.addZombies(buildingDef, 1, "CowboyStripper", 0, roomDef);
			this.addZombies(buildingDef, Rand.Next(9, 15), (String)null, 100, roomDef);
		} else {
			this.addZombies(buildingDef, Rand.Next(2, 4), "WaiterStripper", 100, roomDef);
			this.addZombies(buildingDef, Rand.Next(2, 5), "StripperNaked", 100, roomDef);
			this.addZombies(buildingDef, Rand.Next(2, 5), "StripperBlack", 100, roomDef);
			this.addZombies(buildingDef, Rand.Next(2, 5), "StripperWhite", 100, roomDef);
			this.addZombies(buildingDef, Rand.Next(9, 15), (String)null, 0, roomDef);
		}
	}

	public boolean isValid(BuildingDef buildingDef, boolean boolean1) {
		return buildingDef.getRoom("stripclub") != null || boolean1;
	}

	public RBStripclub() {
		this.name = "Stripclub";
		this.setAlwaysDo(true);
	}
}
