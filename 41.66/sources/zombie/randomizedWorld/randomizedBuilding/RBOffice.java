package zombie.randomizedWorld.randomizedBuilding;

import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;


public final class RBOffice extends RandomizedBuildingBase {

	public void randomizeBuilding(BuildingDef buildingDef) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		for (int int1 = buildingDef.x - 1; int1 < buildingDef.x2 + 1; ++int1) {
			for (int int2 = buildingDef.y - 1; int2 < buildingDef.y2 + 1; ++int2) {
				for (int int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
					if (square != null && this.roomValid(square)) {
						for (int int4 = 0; int4 < square.getObjects().size(); ++int4) {
							IsoObject object = (IsoObject)square.getObjects().get(int4);
							if (object.isTableSurface() && Rand.NextBool(2) && square.getObjects().size() == 2 && object.getProperties().Val("BedType") == null && object.isTableSurface() && (object.getContainer() == null || "desk".equals(object.getContainer().getType()))) {
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

								int int7 = Rand.Next(0, 7);
								switch (int7) {
								case 0: 
									square.AddWorldInventoryItem("MugRed", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 1: 
									square.AddWorldInventoryItem("Mugl", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 2: 
									square.AddWorldInventoryItem("MugWhite", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 3: 
									square.AddWorldInventoryItem("PaperclipBox", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 4: 
									square.AddWorldInventoryItem("RubberBand", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F);
								
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean roomValid(IsoGridSquare square) {
		return square.getRoom() != null && "office".equals(square.getRoom().getName());
	}

	public boolean isValid(BuildingDef buildingDef, boolean boolean1) {
		return buildingDef.getRoom("office") != null || boolean1;
	}

	public RBOffice() {
		this.name = "Offices";
		this.setAlwaysDo(true);
	}
}
