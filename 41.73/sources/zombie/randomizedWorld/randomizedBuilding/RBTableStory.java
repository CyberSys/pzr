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
