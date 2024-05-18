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


public class NatureBush extends ErosionCategory {
	private final int[][] soilRef = new int[][]{{11, 11, 12, 13}, {5, 5, 7, 8, 11, 11, 12, 13, 11, 11, 12, 13}, {5, 5, 7, 8, 5, 5, 7, 8, 11, 11, 12, 13}, {1, 1, 4, 5}, {5, 5, 7, 8, 1, 1, 4, 5, 1, 1, 4, 5}, {5, 5, 7, 8, 5, 5, 7, 8, 1, 1, 4, 5}, {9, 10, 14, 15}, {5, 5, 7, 8, 9, 10, 14, 15, 9, 10, 14, 15}, {5, 5, 7, 8, 5, 5, 7, 8, 9, 10, 14, 15}, {2, 3, 16, 16}, {5, 5, 7, 8, 2, 3, 16, 16, 2, 3, 16, 16}, {5, 5, 7, 8, 5, 5, 7, 8, 2, 3, 16, 16}};
	private ArrayList objs = new ArrayList();
	private int[] spawnChance = new int[100];
	private NatureBush.BushInit[] bush = new NatureBush.BushInit[]{new NatureBush.BushInit("Spicebush", 0.05F, 0.35F, false), new NatureBush.BushInit("Ninebark", 0.65F, 0.75F, true), new NatureBush.BushInit("Ninebark", 0.65F, 0.75F, true), new NatureBush.BushInit("Blueberry", 0.4F, 0.5F, true), new NatureBush.BushInit("Blackberry", 0.4F, 0.5F, true), new NatureBush.BushInit("Piedmont azalea", 0.0F, 0.15F, true), new NatureBush.BushInit("Piedmont azalea", 0.0F, 0.15F, true), new NatureBush.BushInit("Arrowwood viburnum", 0.3F, 0.8F, true), new NatureBush.BushInit("Red chokeberry", 0.9F, 1.0F, true), new NatureBush.BushInit("Red chokeberry", 0.9F, 1.0F, true), new NatureBush.BushInit("Beautyberry", 0.7F, 0.85F, true), new NatureBush.BushInit("New jersey tea", 0.4F, 0.8F, true), new NatureBush.BushInit("New jersey tea", 0.4F, 0.8F, true), new NatureBush.BushInit("Wild hydrangea", 0.2F, 0.35F, true), new NatureBush.BushInit("Wild hydrangea", 0.2F, 0.35F, true), new NatureBush.BushInit("Shrubby St. John\'s wort", 0.35F, 0.75F, true)};

	public boolean replaceExistingObject(IsoGridSquare square, ErosionData.Square square2, ErosionData.Chunk chunk, boolean boolean1, boolean boolean2) {
		int int1 = square.getObjects().size();
		for (int int2 = int1 - 1; int2 >= 1; --int2) {
			IsoObject object = (IsoObject)square.getObjects().get(int2);
			IsoSprite sprite = object.getSprite();
			if (sprite != null && sprite.getName() != null) {
				int int3;
				if (sprite.getName().startsWith("vegetation_foliage")) {
					int3 = square2.soil;
					if (int3 < 0 || int3 >= this.soilRef.length) {
						int3 = square2.rand(square.x, square.y, this.soilRef.length);
					}

					int[] intArray = this.soilRef[int3];
					int int4 = square2.noiseMainInt;
					NatureBush.CategoryData categoryData = (NatureBush.CategoryData)this.setCatModData(square2);
					categoryData.gameObj = intArray[square2.rand(square.x, square.y, intArray.length)] - 1;
					categoryData.maxStage = (int)Math.floor((double)((float)int4 / 60.0F));
					categoryData.stage = categoryData.maxStage;
					categoryData.spawnTime = 0;
					square.RemoveTileObject(object);
					return true;
				}

				if (sprite.getName().startsWith("f_bushes_1_")) {
					int3 = Integer.parseInt(sprite.getName().replace("f_bushes_1_", ""));
					NatureBush.CategoryData categoryData2 = (NatureBush.CategoryData)this.setCatModData(square2);
					categoryData2.gameObj = int3 % 16;
					categoryData2.maxStage = 1;
					categoryData2.stage = categoryData2.maxStage;
					categoryData2.spawnTime = 0;
					square.RemoveTileObject(object);
					return true;
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
			int int2 = square2.rand(square.x, square.y, 101);
			if (int2 < this.spawnChance[int1]) {
				NatureBush.CategoryData categoryData = (NatureBush.CategoryData)this.setCatModData(square2);
				categoryData.gameObj = intArray[square2.rand(square.x, square.y, intArray.length)] - 1;
				categoryData.maxStage = (int)Math.floor((double)((float)int1 / 60.0F));
				categoryData.stage = 0;
				categoryData.spawnTime = 100 - int1;
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void update(IsoGridSquare square, ErosionData.Square square2, ErosionCategory.Data data, ErosionData.Chunk chunk, int int1) {
		NatureBush.CategoryData categoryData = (NatureBush.CategoryData)data;
		if (int1 >= categoryData.spawnTime && !categoryData.doNothing) {
			if (categoryData.gameObj >= 0 && categoryData.gameObj < this.objs.size()) {
				ErosionObj erosionObj = (ErosionObj)this.objs.get(categoryData.gameObj);
				int int2 = categoryData.maxStage;
				int int3 = (int)Math.floor((double)((float)(int1 - categoryData.spawnTime) / ((float)erosionObj.cycleTime / ((float)int2 + 1.0F))));
				if (int3 < categoryData.stage) {
					int3 = categoryData.stage;
				}

				if (int3 > int2) {
					int3 = int2;
				}

				int int4 = this.currentSeason(square2.magicNum, erosionObj);
				boolean boolean1 = this.currentBloom(square2.magicNum, erosionObj);
				boolean boolean2 = false;
				this.updateObj(square2, data, square, erosionObj, boolean2, int3, int4, boolean1);
			} else {
				categoryData.doNothing = true;
			}
		}
	}

	public void init() {
		for (int int1 = 0; int1 < 100; ++int1) {
			if (int1 >= 45 && int1 < 60) {
				this.spawnChance[int1] = (int)this.clerp((float)(int1 - 45) / 15.0F, 0.0F, 20.0F);
			}

			if (int1 >= 60 && int1 < 90) {
				this.spawnChance[int1] = (int)this.clerp((float)(int1 - 60) / 30.0F, 20.0F, 0.0F);
			}
		}

		this.seasonDisp[5].season1 = 0;
		this.seasonDisp[5].season2 = 0;
		this.seasonDisp[5].split = false;
		this.seasonDisp[1].season1 = 1;
		this.seasonDisp[1].season2 = 0;
		this.seasonDisp[1].split = false;
		this.seasonDisp[2].season1 = 2;
		this.seasonDisp[2].season2 = 2;
		this.seasonDisp[2].split = true;
		this.seasonDisp[4].season1 = 4;
		this.seasonDisp[4].season2 = 0;
		this.seasonDisp[4].split = true;
		ErosionIceQueen erosionIceQueen = ErosionIceQueen.instance;
		String string = "f_bushes_1_";
		for (int int2 = 1; int2 <= this.bush.length; ++int2) {
			int int3 = int2 - 1;
			int int4 = int3 - (int)Math.floor((double)((float)int3 / 8.0F)) * 8;
			NatureBush.BushInit bushInit = this.bush[int3];
			ErosionObjSprites erosionObjSprites = new ErosionObjSprites(2, bushInit.name, true, bushInit.hasFlower, true);
			int int5 = 0 + int4;
			int int6 = int5 + 16;
			int int7 = int6 + 16;
			int int8 = int7 + 16;
			int int9 = 64 + int3;
			int int10 = int9 + 16;
			erosionObjSprites.setBase(0, (String)(string + int5), 0);
			erosionObjSprites.setBase(1, (String)(string + (int5 + 8)), 0);
			erosionIceQueen.addSprite(string + int5, string + int6);
			erosionIceQueen.addSprite(string + (int5 + 8), string + (int6 + 8));
			erosionObjSprites.setChildSprite(0, (String)(string + int7), 1);
			erosionObjSprites.setChildSprite(1, (String)(string + (int7 + 8)), 1);
			erosionObjSprites.setChildSprite(0, (String)(string + int8), 4);
			erosionObjSprites.setChildSprite(1, (String)(string + (int8 + 8)), 4);
			erosionObjSprites.setChildSprite(0, (String)(string + int9), 2);
			erosionObjSprites.setChildSprite(1, (String)(string + (int9 + 32)), 2);
			if (bushInit.hasFlower) {
				erosionObjSprites.setFlower(0, (String)(string + int10));
				erosionObjSprites.setFlower(1, (String)(string + (int10 + 32)));
			}

			float float1 = bushInit.hasFlower ? bushInit.bloomstart : 0.0F;
			float float2 = bushInit.hasFlower ? bushInit.bloomend : 0.0F;
			ErosionObj erosionObj = new ErosionObj(erosionObjSprites, 60, float1, float2, true);
			this.objs.add(erosionObj);
		}
	}

	protected ErosionCategory.Data allocData() {
		return new NatureBush.CategoryData();
	}

	private class BushInit {
		public String name;
		public float bloomstart;
		public float bloomend;
		public boolean hasFlower;

		public BushInit(String string, float float1, float float2, boolean boolean1) {
			this.name = string;
			this.bloomstart = float1;
			this.bloomend = float2;
			this.hasFlower = boolean1;
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
