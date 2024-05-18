package zombie.erosion.categories;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.erosion.ErosionData;
import zombie.erosion.obj.ErosionObj;
import zombie.erosion.obj.ErosionObjSprites;
import zombie.erosion.season.ErosionIceQueen;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.sprite.IsoSprite;


public class NatureTrees extends ErosionCategory {
	private final int[][] soilRef = new int[][]{{2, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5}, {1, 1, 2, 2, 2, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5}, {2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 3, 3, 4, 4, 4, 5}, {1, 7, 7, 7, 9, 9, 9, 9, 9, 9, 9}, {2, 2, 1, 1, 1, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 9, 9, 9, 9}, {1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 7, 7, 7, 9}, {1, 2, 8, 8, 8, 6, 6, 6, 6, 6, 6, 6, 6}, {1, 1, 2, 2, 3, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 6, 6, 6, 6, 6}, {1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 8, 8, 8, 6}, {3, 10, 10, 10, 11, 11, 11, 11, 11, 11, 11}, {1, 1, 3, 3, 3, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11, 11, 11, 11}, {1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 10, 10, 10, 11}};
	private final NatureTrees.TreeInit[] trees = new NatureTrees.TreeInit[]{new NatureTrees.TreeInit("American Holly", "e_americanholly_1", true), new NatureTrees.TreeInit("Canadian Hemlock", "e_canadianhemlock_1", true), new NatureTrees.TreeInit("Virginia Pine", "e_virginiapine_1", true), new NatureTrees.TreeInit("Riverbirch", "e_riverbirch_1", false), new NatureTrees.TreeInit("Cockspur Hawthorn", "e_cockspurhawthorn_1", false), new NatureTrees.TreeInit("Dogwood", "e_dogwood_1", false), new NatureTrees.TreeInit("Carolina Silverbell", "e_carolinasilverbell_1", false), new NatureTrees.TreeInit("Yellowwood", "e_yellowwood_1", false), new NatureTrees.TreeInit("Eastern Redbud", "e_easternredbud_1", false), new NatureTrees.TreeInit("Redmaple", "e_redmaple_1", false), new NatureTrees.TreeInit("American Linden", "e_americanlinden_1", false)};
	private int[] spawnChance = new int[100];
	private ArrayList objs = new ArrayList();

	public boolean replaceExistingObject(IsoGridSquare square, ErosionData.Square square2, ErosionData.Chunk chunk, boolean boolean1, boolean boolean2) {
		int int1 = square.getObjects().size();
		for (int int2 = int1 - 1; int2 >= 1; --int2) {
			IsoObject object = (IsoObject)square.getObjects().get(int2);
			IsoSprite sprite = object.getSprite();
			if (sprite != null && sprite.getName() != null) {
				int int3;
				int int4;
				NatureTrees.CategoryData categoryData;
				ErosionObj erosionObj;
				int[] intArray;
				if (sprite.getName().startsWith("jumbo_tree_01")) {
					int3 = square2.soil;
					if (int3 < 0 || int3 >= this.soilRef.length) {
						int3 = square2.rand(square.x, square.y, this.soilRef.length);
					}

					intArray = this.soilRef[int3];
					int4 = square2.noiseMainInt;
					categoryData = (NatureTrees.CategoryData)this.setCatModData(square2);
					categoryData.gameObj = intArray[square2.rand(square.x, square.y, intArray.length)] - 1;
					categoryData.maxStage = 5 + (int)Math.floor((double)((float)int4 / 51.0F)) - 1;
					categoryData.stage = categoryData.maxStage;
					categoryData.spawnTime = 0;
					categoryData.dispSeason = -1;
					erosionObj = (ErosionObj)this.objs.get(categoryData.gameObj);
					object.setName(erosionObj.name);
					categoryData.hasSpawned = true;
					return true;
				}

				if (sprite.getName().startsWith("vegetation_trees")) {
					int3 = square2.soil;
					if (int3 < 0 || int3 >= this.soilRef.length) {
						int3 = square2.rand(square.x, square.y, this.soilRef.length);
					}

					intArray = this.soilRef[int3];
					int4 = square2.noiseMainInt;
					categoryData = (NatureTrees.CategoryData)this.setCatModData(square2);
					categoryData.gameObj = intArray[square2.rand(square.x, square.y, intArray.length)] - 1;
					categoryData.maxStage = 3 + (int)Math.floor((double)((float)int4 / 51.0F)) - 1;
					categoryData.stage = categoryData.maxStage;
					categoryData.spawnTime = 0;
					categoryData.dispSeason = -1;
					erosionObj = (ErosionObj)this.objs.get(categoryData.gameObj);
					object.setName(erosionObj.name);
					categoryData.hasSpawned = true;
					return true;
				}

				for (int3 = 0; int3 < this.trees.length; ++int3) {
					if (sprite.getName().startsWith(this.trees[int3].tile)) {
						NatureTrees.CategoryData categoryData2 = (NatureTrees.CategoryData)this.setCatModData(square2);
						categoryData2.gameObj = int3;
						categoryData2.maxStage = 3;
						categoryData2.stage = categoryData2.maxStage;
						categoryData2.spawnTime = 0;
						square.RemoveTileObject(object);
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean validateSpawn(IsoGridSquare square, ErosionData.Square square2, ErosionData.Chunk chunk, boolean boolean1, boolean boolean2, boolean boolean3) {
		if (square.getObjects().size() > (boolean2 ? 2 : 1)) {
			return false;
		} else if (square2.soil >= 0 && square2.soil < this.soilRef.length) {
			int[] intArray = this.soilRef[square2.soil];
			int int1 = square2.noiseMainInt;
			int int2 = this.spawnChance[int1];
			if (int2 > 0 && square2.rand(square.x, square.y, 101) < int2) {
				NatureTrees.CategoryData categoryData = (NatureTrees.CategoryData)this.setCatModData(square2);
				categoryData.gameObj = intArray[square2.rand(square.x, square.y, intArray.length)] - 1;
				categoryData.maxStage = 2 + (int)Math.floor((double)((int1 - 50) / 17)) - 1;
				categoryData.stage = 0;
				categoryData.spawnTime = 30 + (100 - int1);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void update(IsoGridSquare square, ErosionData.Square square2, ErosionCategory.Data data, ErosionData.Chunk chunk, int int1) {
		NatureTrees.CategoryData categoryData = (NatureTrees.CategoryData)data;
		if (int1 >= categoryData.spawnTime && !categoryData.doNothing) {
			if (categoryData.gameObj >= 0 && categoryData.gameObj < this.objs.size()) {
				ErosionObj erosionObj = (ErosionObj)this.objs.get(categoryData.gameObj);
				int int2 = categoryData.maxStage;
				int int3 = (int)Math.floor((double)((float)(int1 - categoryData.spawnTime) / ((float)erosionObj.cycleTime / ((float)int2 + 1.0F))));
				if (int3 < data.stage) {
					int3 = data.stage;
				}

				if (int3 > int2) {
					int3 = int2;
				}

				boolean boolean1 = true;
				int int4 = this.currentSeason(square2.magicNum, erosionObj);
				boolean boolean2 = false;
				this.updateObj(square2, data, square, erosionObj, boolean1, int3, int4, boolean2);
			} else {
				this.clearCatModData(square2);
			}
		}
	}

	public void init() {
		for (int int1 = 0; int1 < 100; ++int1) {
			this.spawnChance[int1] = int1 >= 50 ? (int)this.clerp((float)(int1 - 50) / 50.0F, 0.0F, 90.0F) : 0;
		}

		int[] intArray = new int[]{0, 5, 1, 2, 3, 4};
		this.seasonDisp[5].season1 = 0;
		this.seasonDisp[5].season2 = 0;
		this.seasonDisp[5].split = false;
		this.seasonDisp[1].season1 = 1;
		this.seasonDisp[1].season2 = 0;
		this.seasonDisp[1].split = false;
		this.seasonDisp[2].season1 = 2;
		this.seasonDisp[2].season2 = 3;
		this.seasonDisp[2].split = true;
		this.seasonDisp[4].season1 = 4;
		this.seasonDisp[4].season2 = 0;
		this.seasonDisp[4].split = true;
		String string = null;
		ErosionIceQueen erosionIceQueen = ErosionIceQueen.instance;
		for (int int2 = 0; int2 < this.trees.length; ++int2) {
			String string2 = this.trees[int2].name;
			String string3 = this.trees[int2].tile;
			boolean boolean1 = !this.trees[int2].evergreen;
			ErosionObjSprites erosionObjSprites = new ErosionObjSprites(6, string2, true, false, boolean1);
			for (int int3 = 0; int3 < 6; ++int3) {
				for (int int4 = 0; int4 < intArray.length; ++int4) {
					int int5;
					if (int3 > 3) {
						int5 = 0 + int4 * 2 + (int3 - 4);
						if (int4 == 0) {
							string = string3.replace("_1", "JUMBO_1") + "_" + int5;
							erosionObjSprites.setBase(int3, (String)string, 0);
						} else if (int4 == 1) {
							erosionIceQueen.addSprite(string, string3.replace("_1", "JUMBO_1") + "_" + int5);
						} else if (boolean1) {
							erosionObjSprites.setChildSprite(int3, string3.replace("_1", "JUMBO_1") + "_" + int5, intArray[int4]);
						}
					} else {
						int5 = 0 + int4 * 4 + int3;
						if (int4 == 0) {
							string = string3 + "_" + int5;
							erosionObjSprites.setBase(int3, (String)string, 0);
						} else if (int4 == 1) {
							erosionIceQueen.addSprite(string, string3 + "_" + int5);
						} else if (boolean1) {
							erosionObjSprites.setChildSprite(int3, string3 + "_" + int5, intArray[int4]);
						}
					}
				}
			}

			ErosionObj erosionObj = new ErosionObj(erosionObjSprites, 60, 0.0F, 0.0F, true);
			this.objs.add(erosionObj);
		}
	}

	protected ErosionCategory.Data allocData() {
		return new NatureTrees.CategoryData();
	}

	private class TreeInit {
		public String name;
		public String tile;
		public boolean evergreen;

		public TreeInit(String string, String string2, boolean boolean1) {
			this.name = string;
			this.tile = string2;
			this.evergreen = boolean1;
		}
	}

	private class CategoryData extends ErosionCategory.Data {
		public int gameObj;
		public int maxStage;
		public int spawnTime;

		private CategoryData() {
			super();
		}

		public void save(ByteBuffer byteBuffer) {
			super.save(byteBuffer);
			byteBuffer.put((byte)this.gameObj);
			byteBuffer.put((byte)this.maxStage);
			byteBuffer.putShort((short)this.spawnTime);
		}

		public void load(ByteBuffer byteBuffer, int int1) {
			super.load(byteBuffer, int1);
			this.gameObj = byteBuffer.get();
			this.maxStage = byteBuffer.get();
			this.spawnTime = byteBuffer.getShort();
		}

		CategoryData(Object object) {
			this();
		}
	}
}
