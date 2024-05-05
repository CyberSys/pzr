package zombie.randomizedWorld.randomizedBuilding;

import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;


public final class RBBar extends RandomizedBuildingBase {

	public void randomizeBuilding(BuildingDef buildingDef) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		for (int int1 = buildingDef.x - 1; int1 < buildingDef.x2 + 1; ++int1) {
			for (int int2 = buildingDef.y - 1; int2 < buildingDef.y2 + 1; ++int2) {
				for (int int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
					if (square != null && this.roomValid(square)) {
						for (int int4 = 0; int4 < square.getObjects().size(); ++int4) {
							IsoObject object = (IsoObject)square.getObjects().get(int4);
							if (object.getSprite() != null && object.getSprite().getName() != null && (object.getSprite().getName().equals("recreational_01_6") || object.getSprite().getName().equals("recreational_01_7"))) {
								if (Rand.NextBool(3)) {
									this.addWorldItem("PoolBall", square, object);
								}

								if (Rand.NextBool(3)) {
									this.addWorldItem("Poolcue", square, object);
								}
							} else if (object.isTableSurface() && Rand.NextBool(2)) {
								if (Rand.NextBool(3)) {
									this.addWorldItem("Cigarettes", square, object);
									if (Rand.NextBool(2)) {
										this.addWorldItem("Lighter", square, object);
									}
								}

								int int5 = Rand.Next(7);
								switch (int5) {
								case 0: 
									this.addWorldItem("WhiskeyFull", square, object);
									break;
								
								case 1: 
									this.addWorldItem("Wine", square, object);
									break;
								
								case 2: 
									this.addWorldItem("Wine2", square, object);
									break;
								
								case 3: 
									this.addWorldItem("BeerCan", square, object);
									break;
								
								case 4: 
									this.addWorldItem("BeerBottle", square, object);
								
								}

								if (Rand.NextBool(3)) {
									int int6 = Rand.Next(7);
									switch (int6) {
									case 0: 
										this.addWorldItem("Crisps", square, object);
										break;
									
									case 1: 
										this.addWorldItem("Crisps2", square, object);
										break;
									
									case 2: 
										this.addWorldItem("Crisps3", square, object);
										break;
									
									case 3: 
										this.addWorldItem("Crisps4", square, object);
										break;
									
									case 4: 
										this.addWorldItem("Peanuts", square, object);
									
									}
								}

								if (Rand.NextBool(4)) {
									this.addWorldItem("CardDeck", square, object);
								}
							}
						}

						if (Rand.NextBool(20) && square.getRoom() != null && square.getRoom().getName().equals("bar") && square.getObjects().size() == 1 && Rand.NextBool(8)) {
							this.addWorldItem("Dart", square, (IsoObject)null);
						}
					}
				}
			}
		}
	}

	public boolean roomValid(IsoGridSquare square) {
		return square.getRoom() != null && "bar".equals(square.getRoom().getName());
	}

	public boolean isValid(BuildingDef buildingDef, boolean boolean1) {
		return buildingDef.getRoom("bar") != null && buildingDef.getRoom("stripclub") == null || boolean1;
	}

	public RBBar() {
		this.name = "Bar";
		this.setAlwaysDo(true);
	}
}
