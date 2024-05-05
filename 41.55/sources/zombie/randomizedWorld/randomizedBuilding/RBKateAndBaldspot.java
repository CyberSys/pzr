package zombie.randomizedWorld.randomizedBuilding;

import java.util.ArrayList;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoZombie;
import zombie.core.ImmutableColor;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;


public class RBKateAndBaldspot extends RandomizedBuildingBase {

	public RBKateAndBaldspot() {
		this.name = "K&B story";
		this.setChance(0);
		this.setUnique(true);
	}

	public void randomizeBuilding(BuildingDef buildingDef) {
		buildingDef.bAlarmed = false;
		buildingDef.setHasBeenVisited(true);
		buildingDef.setAllExplored(true);
		ArrayList arrayList = this.addZombiesOnSquare(1, "Kate", 100, this.getSq(10746, 9412, 1));
		if (arrayList != null && !arrayList.isEmpty()) {
			IsoZombie zombie = (IsoZombie)arrayList.get(0);
			HumanVisual humanVisual = (HumanVisual)zombie.getVisual();
			humanVisual.setHairModel("Rachel");
			humanVisual.setHairColor(new ImmutableColor(0.83F, 0.67F, 0.27F));
			for (int int1 = 0; int1 < zombie.getItemVisuals().size(); ++int1) {
				ItemVisual itemVisual = (ItemVisual)zombie.getItemVisuals().get(int1);
				if (itemVisual.getClothingItemName().equals("Skirt_Knees")) {
					itemVisual.setTint(new ImmutableColor(0.54F, 0.54F, 0.54F));
				}
			}

			zombie.getHumanVisual().setSkinTextureIndex(1);
			zombie.addBlood(BloodBodyPartType.LowerLeg_L, true, true, true);
			zombie.addBlood(BloodBodyPartType.LowerLeg_L, true, true, true);
			zombie.addBlood(BloodBodyPartType.UpperLeg_L, true, true, true);
			zombie.addBlood(BloodBodyPartType.UpperLeg_L, true, true, true);
			zombie.bCrawling = true;
			zombie.setCanWalk(false);
			zombie.setCrawlerType(1);
			zombie.resetModelNextFrame();
			arrayList = this.addZombiesOnSquare(1, "Bob", 0, this.getSq(10747, 9412, 1));
			if (arrayList != null && !arrayList.isEmpty()) {
				IsoZombie zombie2 = (IsoZombie)arrayList.get(0);
				humanVisual = (HumanVisual)zombie2.getVisual();
				humanVisual.setHairModel("Baldspot");
				humanVisual.setHairColor(new ImmutableColor(0.337F, 0.173F, 0.082F));
				humanVisual.setBeardModel("");
				for (int int2 = 0; int2 < zombie2.getItemVisuals().size(); ++int2) {
					ItemVisual itemVisual2 = (ItemVisual)zombie2.getItemVisuals().get(int2);
					if (itemVisual2.getClothingItemName().equals("Trousers_DefaultTEXTURE_TINT")) {
						itemVisual2.setTint(new ImmutableColor(0.54F, 0.54F, 0.54F));
					}

					if (itemVisual2.getClothingItemName().equals("Shirt_FormalTINT")) {
						itemVisual2.setTint(new ImmutableColor(0.63F, 0.71F, 0.82F));
					}
				}

				zombie2.getHumanVisual().setSkinTextureIndex(1);
				zombie2.resetModelNextFrame();
				zombie2.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem("KatePic"));
				zombie2.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem("RippedSheets"));
				zombie2.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem("Pills"));
				InventoryItem inventoryItem = InventoryItemFactory.CreateItem("Hammer");
				inventoryItem.setCondition(1);
				zombie2.addItemToSpawnAtDeath(inventoryItem);
				zombie2.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem("Nails"));
				zombie2.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem("Plank"));
				arrayList = this.addZombiesOnSquare(1, "Raider", 0, this.getSq(10745, 9411, 0));
				if (arrayList != null && !arrayList.isEmpty()) {
					IsoZombie zombie3 = (IsoZombie)arrayList.get(0);
					humanVisual = (HumanVisual)zombie3.getVisual();
					humanVisual.setHairModel("Crewcut");
					humanVisual.setHairColor(new ImmutableColor(0.37F, 0.27F, 0.23F));
					humanVisual.setBeardModel("Goatee");
					for (int int3 = 0; int3 < zombie3.getItemVisuals().size(); ++int3) {
						ItemVisual itemVisual3 = (ItemVisual)zombie3.getItemVisuals().get(int3);
						if (itemVisual3.getClothingItemName().equals("Trousers_DefaultTEXTURE_TINT")) {
							itemVisual3.setTint(new ImmutableColor(0.54F, 0.54F, 0.54F));
						}

						if (itemVisual3.getClothingItemName().equals("Vest_DefaultTEXTURE_TINT")) {
							itemVisual3.setTint(new ImmutableColor(0.22F, 0.25F, 0.27F));
						}
					}

					zombie3.getHumanVisual().setSkinTextureIndex(1);
					InventoryItem inventoryItem2 = InventoryItemFactory.CreateItem("Shotgun");
					inventoryItem2.setCondition(0);
					zombie3.setAttachedItem("Rifle On Back", inventoryItem2);
					InventoryItem inventoryItem3 = InventoryItemFactory.CreateItem("BaseballBat");
					inventoryItem3.setCondition(1);
					zombie3.addItemToSpawnAtDeath(inventoryItem3);
					zombie3.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem("ShotgunShells"));
					zombie3.resetModelNextFrame();
					this.addItemOnGround(this.getSq(10747, 9412, 1), InventoryItemFactory.CreateItem("Pillow"));
					IsoGridSquare square = this.getSq(10745, 9410, 0);
					square.Burn();
					square = this.getSq(10745, 9411, 0);
					square.Burn();
					square = this.getSq(10746, 9411, 0);
					square.Burn();
					square = this.getSq(10745, 9410, 0);
					square.Burn();
					square = this.getSq(10745, 9412, 0);
					square.Burn();
					square = this.getSq(10747, 9410, 0);
					square.Burn();
					square = this.getSq(10746, 9409, 0);
					square.Burn();
					square = this.getSq(10745, 9409, 0);
					square.Burn();
					square = this.getSq(10744, 9410, 0);
					square.Burn();
					square = this.getSq(10747, 9411, 0);
					square.Burn();
					square = this.getSq(10746, 9412, 0);
					square.Burn();
					IsoGridSquare square2 = this.getSq(10746, 9410, 0);
					for (int int4 = 0; int4 < square2.getObjects().size(); ++int4) {
						IsoObject object = (IsoObject)square2.getObjects().get(int4);
						if (object.getContainer() != null) {
							InventoryItem inventoryItem4 = InventoryItemFactory.CreateItem("PotOfSoup");
							inventoryItem4.setCooked(true);
							inventoryItem4.setBurnt(true);
							object.getContainer().AddItem(inventoryItem4);
							break;
						}
					}

					this.addBarricade(this.getSq(10747, 9417, 0), 3);
					this.addBarricade(this.getSq(10745, 9417, 0), 3);
					this.addBarricade(this.getSq(10744, 9413, 0), 3);
					this.addBarricade(this.getSq(10744, 9412, 0), 3);
					this.addBarricade(this.getSq(10752, 9413, 0), 3);
				}
			}
		}
	}

	public boolean isValid(BuildingDef buildingDef, boolean boolean1) {
		this.debugLine = "";
		if (buildingDef.x == 10744 && buildingDef.y == 9409) {
			return true;
		} else {
			this.debugLine = "Need to be the K&B house";
			return false;
		}
	}
}
