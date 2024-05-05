package zombie.randomizedWorld.randomizedBuilding.TableStories;

import zombie.core.Rand;
import zombie.iso.BuildingDef;


public final class RBTSFoodPreparation extends RBTableStoryBase {

	public RBTSFoodPreparation() {
		this.chance = 8;
		this.ignoreAgainstWall = true;
		this.rooms.add("livingroom");
		this.rooms.add("kitchen");
	}

	public void randomizeBuilding(BuildingDef buildingDef) {
		this.addWorldItem("Base.BakingTray", this.table1.getSquare(), 0.695F, 0.648F, this.table1.getSurfaceOffsetNoTable() / 96.0F, 1);
		String string = "Base.Chicken";
		int int1 = Rand.Next(0, 4);
		switch (int1) {
		case 0: 
			string = "Base.Steak";
			break;
		
		case 1: 
			string = "Base.MuttonChop";
			break;
		
		case 2: 
			string = "Base.Smallbirdmeat";
		
		}
		this.addWorldItem(string, this.table1.getSquare(), 0.531F, 0.625F, this.table1.getSurfaceOffsetNoTable() / 96.0F);
		this.addWorldItem(string, this.table1.getSquare(), 0.836F, 0.627F, this.table1.getSurfaceOffsetNoTable() / 96.0F);
		this.addWorldItem(Rand.NextBool(2) ? "Base.Pepper" : "Base.Salt", this.table1.getSquare(), 0.492F, 0.94F, this.table1.getSurfaceOffsetNoTable() / 96.0F);
		this.addWorldItem("Base.KitchenKnife", this.table1.getSquare(), 0.492F, 0.29F, this.table1.getSurfaceOffsetNoTable() / 96.0F, 1);
		string = "farming.Tomato";
		int1 = Rand.Next(0, 4);
		switch (int1) {
		case 0: 
			string = "Base.BellPepper";
			break;
		
		case 1: 
			string = "Base.Broccoli";
			break;
		
		case 2: 
			string = "Base.Carrots";
		
		}
		this.addWorldItem(string, this.table1.getSquare(), 0.77F, 0.97F, this.table1.getSurfaceOffsetNoTable() / 96.0F, 70);
	}
}
