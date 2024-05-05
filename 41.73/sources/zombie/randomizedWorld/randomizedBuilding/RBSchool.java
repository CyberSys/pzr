package zombie.randomizedWorld.randomizedBuilding;

import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;


public final class RBSchool extends RandomizedBuildingBase {

	public void randomizeBuilding(BuildingDef buildingDef) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		for (int int1 = buildingDef.x - 1; int1 < buildingDef.x2 + 1; ++int1) {
			for (int int2 = buildingDef.y - 1; int2 < buildingDef.y2 + 1; ++int2) {
				for (int int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
					if (square != null && this.roomValid(square)) {
						int int4;
						for (int4 = 0; int4 < square.getObjects().size(); ++int4) {
							IsoObject object = (IsoObject)square.getObjects().get(int4);
							if (Rand.NextBool(3) && this.isTableFor3DItems(object, square)) {
								int int5 = Rand.Next(0, 8);
								switch (int5) {
								case 0: 
									square.AddWorldInventoryItem("Pen", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 1: 
									square.AddWorldInventoryItem("Pencil", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 2: 
									square.AddWorldInventoryItem("Crayons", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 3: 
									square.AddWorldInventoryItem("RedPen", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 4: 
									square.AddWorldInventoryItem("BluePen", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 5: 
									square.AddWorldInventoryItem("Eraser", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F);
								
								}

								int int6 = Rand.Next(0, 6);
								switch (int6) {
								case 0: 
									square.AddWorldInventoryItem("Doodle", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 1: 
									square.AddWorldInventoryItem("Book", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 2: 
									square.AddWorldInventoryItem("Notebook", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 3: 
									square.AddWorldInventoryItem("SheetPaper2", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F);
								
								}
							}
						}

						if (square.getRoom() != null && "classroom".equals(square.getRoom().getName())) {
							if (Rand.NextBool(50)) {
								int4 = Rand.Next(0, 10);
								switch (int4) {
								case 0: 
									square.AddWorldInventoryItem("Doodle", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
									break;
								
								case 1: 
									square.AddWorldInventoryItem("Book", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
									break;
								
								case 2: 
									square.AddWorldInventoryItem("Notebook", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
									break;
								
								case 3: 
									square.AddWorldInventoryItem("SheetPaper2", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
									break;
								
								case 4: 
									square.AddWorldInventoryItem("Pen", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
									break;
								
								case 5: 
									square.AddWorldInventoryItem("Pencil", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
									break;
								
								case 6: 
									square.AddWorldInventoryItem("Crayons", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
									break;
								
								case 7: 
									square.AddWorldInventoryItem("RedPen", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
									break;
								
								case 8: 
									square.AddWorldInventoryItem("BluePen", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
									break;
								
								case 9: 
									square.AddWorldInventoryItem("Eraser", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
								
								}
							}

							if (Rand.NextBool(120)) {
								square.AddWorldInventoryItem("Bag_Schoolbag", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
							}
						}
					}
				}
			}
		}
	}

	public boolean roomValid(IsoGridSquare square) {
		return square.getRoom() != null && "classroom".equals(square.getRoom().getName());
	}

	public boolean isValid(BuildingDef buildingDef, boolean boolean1) {
		return buildingDef.getRoom("classroom") != null || boolean1;
	}

	public RBSchool() {
		this.name = "School";
		this.setAlwaysDo(true);
	}
}
