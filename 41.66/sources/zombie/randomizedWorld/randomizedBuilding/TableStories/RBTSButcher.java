package zombie.randomizedWorld.randomizedBuilding.TableStories;

import zombie.core.Rand;
import zombie.iso.BuildingDef;


public final class RBTSButcher extends RBTableStoryBase {

	public RBTSButcher() {
		this.chance = 3;
		this.ignoreAgainstWall = true;
		this.rooms.add("livingroom");
		this.rooms.add("kitchen");
	}

	public void randomizeBuilding(BuildingDef buildingDef) {
		String string = "Base.DeadRabbit";
		String string2 = "Base.Rabbitmeat";
		int int1 = Rand.Next(0, 4);
		switch (int1) {
		case 0: 
			string = "Base.DeadBird";
			string2 = "Base.Smallbirdmeat";
			break;
		
		case 1: 
			string = "Base.DeadSquirrel";
			string2 = "Base.Smallanimalmeat";
			break;
		
		case 2: 
			string = "Base.Panfish";
			string2 = "Base.FishFillet";
			break;
		
		case 3: 
			string = "Base.BaitFish";
			string2 = "Base.FishFillet";
			break;
		
		case 4: 
			string = "Base.Catfish";
			string2 = "Base.FishFillet";
		
		}
		this.addWorldItem(string, this.table1.getSquare(), 0.453F, 0.64F, this.table1.getSurfaceOffsetNoTable() / 96.0F, 1);
		this.addWorldItem(string2, this.table1.getSquare(), 0.835F, 0.851F, this.table1.getSurfaceOffsetNoTable() / 96.0F);
		this.addWorldItem("Base.KitchenKnife", this.table1.getSquare(), 0.742F, 0.445F, this.table1.getSurfaceOffsetNoTable() / 96.0F);
	}
}
