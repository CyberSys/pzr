package zombie.randomizedWorld.randomizedBuilding.TableStories;

import zombie.core.Rand;
import zombie.iso.BuildingDef;


public final class RBTSElectronics extends RBTableStoryBase {

	public RBTSElectronics() {
		this.chance = 5;
		this.rooms.add("livingroom");
		this.rooms.add("kitchen");
		this.rooms.add("bedroom");
	}

	public void randomizeBuilding(BuildingDef buildingDef) {
		String string = "Base.ElectronicsMag1";
		int int1 = Rand.Next(0, 4);
		switch (int1) {
		case 0: 
			string = "Base.ElectronicsMag2";
			break;
		
		case 1: 
			string = "Base.ElectronicsMag3";
			break;
		
		case 2: 
			string = "Base.ElectronicsMag5";
		
		}
		this.addWorldItem(string, this.table1.getSquare(), 0.36F, 0.789F, this.table1.getSurfaceOffsetNoTable() / 96.0F);
		this.addWorldItem("Base.ElectronicsScrap", this.table1.getSquare(), 0.71F, 0.82F, this.table1.getSurfaceOffsetNoTable() / 96.0F);
		this.addWorldItem("Base.Screwdriver", this.table1.getSquare(), 0.36F, 0.421F, this.table1.getSurfaceOffsetNoTable() / 96.0F);
		string = "Radio.CDPlayer";
		int1 = Rand.Next(0, 6);
		switch (int1) {
		case 0: 
			string = "Base.Torch";
			break;
		
		case 1: 
			string = "Base.Remote";
			break;
		
		case 2: 
			string = "Base.VideoGame";
			break;
		
		case 3: 
			string = "Base.CordlessPhone";
			break;
		
		case 4: 
			string = "Base.Headphones";
		
		}
		this.addWorldItem(string, this.table1.getSquare(), 0.695F, 0.43F, this.table1.getSurfaceOffsetNoTable() / 96.0F);
	}
}
