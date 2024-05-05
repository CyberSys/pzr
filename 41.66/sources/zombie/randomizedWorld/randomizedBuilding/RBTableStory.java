package zombie.randomizedWorld.randomizedBuilding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;


public final class RBTableStory extends RandomizedBuildingBase {
	public static ArrayList allStories = new ArrayList();
	private float xOffset = 0.0F;
	private float yOffset = 0.0F;
	private IsoGridSquare currentSquare = null;
	public ArrayList fullTableMap = new ArrayList();
	public IsoObject table1 = null;
	public IsoObject table2 = null;

	public void initStories() {
		if (allStories.isEmpty()) {
			ArrayList arrayList = new ArrayList();
			arrayList.add("livingroom");
			arrayList.add("kitchen");
			ArrayList arrayList2 = new ArrayList();
			LinkedHashMap linkedHashMap = new LinkedHashMap();
			linkedHashMap.put("BakingPan", 50);
			linkedHashMap.put("CakePrep", 50);
			arrayList2.add(new RBTableStory.StorySpawnItem(linkedHashMap, (String)null, 100));
			arrayList2.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Chocolate", 100));
			arrayList2.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Butter", 70));
			arrayList2.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Flour", 70));
			arrayList2.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Spoon", 100));
			arrayList2.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "EggCarton", 100));
			arrayList2.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Egg", 100));
			allStories.add(new RBTableStory.StoryDef(arrayList2, arrayList));
			ArrayList arrayList3 = new ArrayList();
			arrayList3.add(new RBTableStory.StorySpawnItem(linkedHashMap, (String)null, 100));
			arrayList3.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Flour", 70));
			arrayList3.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Butter", 70));
			arrayList3.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "KitchenKnife", 100));
			arrayList3.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Egg", 100));
			arrayList3.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Spoon", 100));
			LinkedHashMap linkedHashMap2 = new LinkedHashMap();
			linkedHashMap2.put("BerryBlack", 50);
			linkedHashMap2.put("BerryBlue", 50);
			arrayList3.add(new RBTableStory.StorySpawnItem(linkedHashMap2, (String)null, 100));
			arrayList3.add(new RBTableStory.StorySpawnItem(linkedHashMap2, (String)null, 70));
			arrayList3.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Cherry", 100));
			arrayList3.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Pineapple", 70));
			allStories.add(new RBTableStory.StoryDef(arrayList3, arrayList));
			ArrayList arrayList4 = new ArrayList();
			arrayList4.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Rabbitmeat", 100, 0.1F));
			arrayList4.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Rabbitmeat", 70, 0.1F));
			arrayList4.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "DeadRabbit", 100, 0.15F));
			arrayList4.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Rabbitmeat", 100, 0.1F));
			arrayList4.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "HuntingKnife", 100));
			RBTableStory.StoryDef storyDef = new RBTableStory.StoryDef(arrayList4, arrayList);
			storyDef.addBlood = true;
			allStories.add(storyDef);
			ArrayList arrayList5 = new ArrayList();
			arrayList5.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Mugl", 100));
			arrayList5.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Cereal", 100));
			arrayList5.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Spoon", 100));
			LinkedHashMap linkedHashMap3 = new LinkedHashMap();
			linkedHashMap3.put("Coffee2", 50);
			linkedHashMap3.put("Teabag2", 50);
			arrayList5.add(new RBTableStory.StorySpawnItem(linkedHashMap3, (String)null, 100));
			allStories.add(new RBTableStory.StoryDef(arrayList5, arrayList));
			ArrayList arrayList6 = new ArrayList();
			arrayList6.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Socks_Ankle", 100));
			arrayList6.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Socks_Long", 70));
			arrayList6.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Thread", 100));
			arrayList6.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Thread", 50));
			arrayList6.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Needle", 100));
			arrayList6.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "RippedSheets", 100));
			allStories.add(new RBTableStory.StoryDef(arrayList6, arrayList));
			ArrayList arrayList7 = new ArrayList();
			arrayList7.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "BoxOfJars", 100, 0.15F));
			arrayList7.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "JarLid", 100));
			arrayList7.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "EmptyJar", 100));
			arrayList7.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Vinegar", 100));
			arrayList7.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Sugar", 100));
			LinkedHashMap linkedHashMap4 = new LinkedHashMap();
			linkedHashMap4.put("Carrots", 20);
			linkedHashMap4.put("farming.Tomato", 20);
			linkedHashMap4.put("farming.Potato", 20);
			linkedHashMap4.put("Eggplant", 20);
			linkedHashMap4.put("Leek", 20);
			arrayList7.add(new RBTableStory.StorySpawnItem(linkedHashMap4, (String)null, 100));
			allStories.add(new RBTableStory.StoryDef(arrayList7, arrayList));
			ArrayList arrayList8 = new ArrayList();
			arrayList8.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Screwdriver", 100));
			arrayList8.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "ScrewsBox", 100));
			arrayList8.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Screws", 100));
			arrayList8.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "ElectronicsScrap", 100));
			LinkedHashMap linkedHashMap5 = new LinkedHashMap();
			linkedHashMap5.put("VideoGame", 20);
			linkedHashMap5.put("CDplayer", 20);
			linkedHashMap5.put("CordlessPhone", 20);
			linkedHashMap5.put("HomeAlarm", 20);
			linkedHashMap5.put("MotionSensor", 20);
			arrayList8.add(new RBTableStory.StorySpawnItem(linkedHashMap5, (String)null, 100));
			allStories.add(new RBTableStory.StoryDef(arrayList8, arrayList));
			ArrayList arrayList9 = new ArrayList();
			arrayList9.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Drawer", 100, 0.2F));
			arrayList9.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Screwdriver", 100));
			arrayList9.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "NailsBox", 100));
			arrayList9.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Nails", 100));
			arrayList9.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Hammer", 50));
			arrayList9.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Needle", 100));
			arrayList9.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Woodglue", 100));
			allStories.add(new RBTableStory.StoryDef(arrayList9, arrayList));
			ArrayList arrayList10 = new ArrayList();
			arrayList10.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Sponge", 100, 0.1F));
			arrayList10.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "CleaningLiquid2", 100, 0.1F));
			arrayList10.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "DishCloth", 100));
			allStories.add(new RBTableStory.StoryDef(arrayList10, arrayList));
			arrayList = new ArrayList();
			arrayList.add("livingroom");
			arrayList.add("kitchen");
			arrayList.add("bedroom");
			ArrayList arrayList11 = new ArrayList();
			arrayList11.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "ToyCar", 100));
			arrayList11.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "ToyBear", 100, 0.1F));
			arrayList11.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "CatToy", 70));
			arrayList11.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "ToyCar", 80));
			arrayList11.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Bricktoys", 100));
			RBTableStory.StoryDef storyDef2 = new RBTableStory.StoryDef(arrayList11, arrayList);
			storyDef2.addBlood = true;
			allStories.add(storyDef2);
			ArrayList arrayList12 = new ArrayList();
			arrayList12.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Notebook", 100));
			arrayList12.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Pencil", 100));
			arrayList12.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Pencil", 70));
			arrayList12.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "BluePen", 80));
			arrayList12.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "Pen", 80));
			arrayList12.add(new RBTableStory.StorySpawnItem((LinkedHashMap)null, "RedPen", 80));
			allStories.add(new RBTableStory.StoryDef(arrayList12, arrayList));
		}
	}

	public boolean isValid(BuildingDef buildingDef, boolean boolean1) {
		return false;
	}

	public void randomizeBuilding(BuildingDef buildingDef) {
		this.initStories();
		if (this.table1 != null && this.table2 != null) {
			if (this.table1.getSquare() != null && this.table1.getSquare().getRoom() != null) {
				ArrayList arrayList = new ArrayList();
				for (int int1 = 0; int1 < allStories.size(); ++int1) {
					RBTableStory.StoryDef storyDef = (RBTableStory.StoryDef)allStories.get(int1);
					if (storyDef.rooms == null || storyDef.rooms.contains(this.table1.getSquare().getRoom().getName())) {
						arrayList.add(storyDef);
					}
				}

				if (!arrayList.isEmpty()) {
					RBTableStory.StoryDef storyDef2 = (RBTableStory.StoryDef)arrayList.get(Rand.Next(0, arrayList.size()));
					if (storyDef2 != null) {
						boolean boolean1 = true;
						if ((int)this.table1.getY() != (int)this.table2.getY()) {
							boolean1 = false;
						}

						this.doSpawnTable(storyDef2.items, boolean1);
						if (storyDef2.addBlood) {
							int int2 = (int)this.table1.getX() - 1;
							int int3 = (int)this.table1.getX() + 1;
							int int4 = (int)this.table1.getY() - 1;
							int int5 = (int)this.table2.getY() + 1;
							if (boolean1) {
								int2 = (int)this.table1.getX() - 1;
								int3 = (int)this.table2.getX() + 1;
								int4 = (int)this.table1.getY() - 1;
								int5 = (int)this.table2.getY() + 1;
							}

							for (int int6 = int2; int6 < int3 + 1; ++int6) {
								for (int int7 = int4; int7 < int5 + 1; ++int7) {
									int int8 = Rand.Next(7, 15);
									for (int int9 = 0; int9 < int8; ++int9) {
										this.currentSquare.getChunk().addBloodSplat((float)int6 + Rand.Next(-0.5F, 0.5F), (float)int7 + Rand.Next(-0.5F, 0.5F), this.table1.getZ(), Rand.Next(8));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void doSpawnTable(ArrayList arrayList, boolean boolean1) {
		this.xOffset = 0.0F;
		this.yOffset = 0.0F;
		int int1 = 0;
		if (boolean1) {
			this.xOffset = 0.6F;
			this.yOffset = Rand.Next(0.5F, 1.1F);
		} else {
			this.yOffset = 0.6F;
			this.xOffset = Rand.Next(0.5F, 1.1F);
		}

		for (this.currentSquare = this.table1.getSquare(); int1 < arrayList.size(); ++int1) {
			RBTableStory.StorySpawnItem storySpawnItem = (RBTableStory.StorySpawnItem)arrayList.get(int1);
			String string = this.getItemFromSSI(storySpawnItem);
			if (string != null) {
				InventoryItem inventoryItem = this.currentSquare.AddWorldInventoryItem(string, this.xOffset, this.yOffset, 0.4F);
				if (inventoryItem != null) {
					inventoryItem.setAutoAge();
					this.increaseOffsets(boolean1, storySpawnItem);
				}
			}
		}
	}

	private void increaseOffsets(boolean boolean1, RBTableStory.StorySpawnItem storySpawnItem) {
		float float1 = 0.15F + storySpawnItem.forcedOffset;
		float float2;
		if (boolean1) {
			this.xOffset += float1;
			if (this.xOffset > 1.0F) {
				this.currentSquare = this.table2.getSquare();
				this.xOffset = 0.35F;
			}

			for (float2 = this.yOffset; Math.abs(float2 - this.yOffset) < 0.11F; this.yOffset = Rand.Next(0.5F, 1.1F)) {
			}
		} else {
			this.yOffset += float1;
			if (this.yOffset > 1.0F) {
				this.currentSquare = this.table2.getSquare();
				this.yOffset = 0.35F;
			}

			for (float2 = this.xOffset; Math.abs(float2 - this.xOffset) < 0.11F; this.xOffset = Rand.Next(0.5F, 1.1F)) {
			}
		}
	}

	private String getItemFromSSI(RBTableStory.StorySpawnItem storySpawnItem) {
		if (Rand.Next(100) > storySpawnItem.chanceToSpawn) {
			return null;
		} else if (storySpawnItem.eitherObject != null && !storySpawnItem.eitherObject.isEmpty()) {
			int int1 = Rand.Next(100);
			int int2 = 0;
			Iterator iterator = storySpawnItem.eitherObject.keySet().iterator();
			String string;
			do {
				if (!iterator.hasNext()) {
					return null;
				}

				string = (String)iterator.next();
				int int3 = (Integer)storySpawnItem.eitherObject.get(string);
				int2 += int3;
			}	 while (int2 < int1);

			return string;
		} else {
			return storySpawnItem.object;
		}
	}

	public class StorySpawnItem {
		LinkedHashMap eitherObject = null;
		String object = null;
		Integer chanceToSpawn = null;
		float forcedOffset = 0.0F;

		public StorySpawnItem(LinkedHashMap linkedHashMap, String string, Integer integer) {
			this.eitherObject = linkedHashMap;
			this.object = string;
			this.chanceToSpawn = integer;
		}

		public StorySpawnItem(LinkedHashMap linkedHashMap, String string, Integer integer, float float1) {
			this.eitherObject = linkedHashMap;
			this.object = string;
			this.chanceToSpawn = integer;
			this.forcedOffset = float1;
		}
	}

	public class StoryDef {
		public ArrayList items = null;
		public boolean addBlood = false;
		public ArrayList rooms = null;

		public StoryDef(ArrayList arrayList) {
			this.items = arrayList;
		}

		public StoryDef(ArrayList arrayList, ArrayList arrayList2) {
			this.items = arrayList;
			this.rooms = arrayList2;
		}
	}
}
