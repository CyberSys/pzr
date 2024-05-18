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


public class NatureGeneric extends ErosionCategory {
	private ArrayList objs = new ArrayList();
	private static final int GRASS = 0;
	private static final int FERNS = 1;
	private static final int GENERIC = 2;
	private ArrayList objsRef = new ArrayList();
	private int[] spawnChance = new int[100];

	public boolean replaceExistingObject(IsoGridSquare square, ErosionData.Square square2, ErosionData.Chunk chunk, boolean boolean1, boolean boolean2) {
		int int1 = square.getObjects().size();
		for (int int2 = int1 - 1; int2 >= 1; --int2) {
			IsoObject object = (IsoObject)square.getObjects().get(int2);
			IsoSprite sprite = object.getSprite();
			if (sprite != null && sprite.getName() != null && sprite.getName().startsWith("blends_grassoverlays")) {
				float float1 = 0.3F;
				float float2 = 12.0F;
				if ("Forest".equals(square.getZoneType())) {
					float1 = 0.5F;
					float2 = 6.0F;
				} else if ("DeepForest".equals(square.getZoneType())) {
					float1 = 0.7F;
					float2 = 3.0F;
				}

				NatureGeneric.CategoryData categoryData = (NatureGeneric.CategoryData)this.setCatModData(square2);
				ArrayList arrayList = (ArrayList)this.objsRef.get(0);
				int int3 = square2.noiseMainInt;
				int int4 = square2.rand(square.x, square.y, 101);
				if ((float)int4 < (float)int3 / float2) {
					if (square2.magicNum < float1) {
						arrayList = (ArrayList)this.objsRef.get(1);
					} else {
						arrayList = (ArrayList)this.objsRef.get(2);
					}

					categoryData.notGrass = true;
					categoryData.maxStage = int3 > 60 ? 1 : 0;
				} else {
					categoryData.maxStage = int3 > 67 ? 2 : (int3 > 50 ? 1 : 0);
				}

				categoryData.gameObj = (Integer)arrayList.get(square2.rand(square.x, square.y, arrayList.size()));
				categoryData.stage = categoryData.maxStage;
				categoryData.spawnTime = 0;
				categoryData.dispSeason = -1;
				ErosionObj erosionObj = (ErosionObj)this.objs.get(categoryData.gameObj);
				object.setName(erosionObj.name);
				object.doNotSync = true;
				categoryData.hasSpawned = true;
				return true;
			}
		}

		return false;
	}

	public boolean validateSpawn(IsoGridSquare square, ErosionData.Square square2, ErosionData.Chunk chunk, boolean boolean1, boolean boolean2, boolean boolean3) {
		if (square.getObjects().size() > (boolean2 ? 2 : 1)) {
			return false;
		} else {
			int int1 = square2.noiseMainInt;
			if (square2.rand(square.x, square.y, 101) < this.spawnChance[int1]) {
				float float1 = 0.3F;
				float float2 = 12.0F;
				if ("Forest".equals(square.getZoneType())) {
					float1 = 0.5F;
					float2 = 6.0F;
				} else if ("DeepForest".equals(square.getZoneType())) {
					float1 = 0.7F;
					float2 = 3.0F;
				}

				NatureGeneric.CategoryData categoryData = (NatureGeneric.CategoryData)this.setCatModData(square2);
				ArrayList arrayList = (ArrayList)this.objsRef.get(0);
				int int2 = square2.rand(square.x, square.y, 101);
				if ((float)int2 < (float)int1 / float2) {
					if (square2.magicNum < float1) {
						arrayList = (ArrayList)this.objsRef.get(1);
					} else {
						arrayList = (ArrayList)this.objsRef.get(2);
					}

					categoryData.notGrass = true;
					categoryData.maxStage = int1 > 60 ? 1 : 0;
				} else {
					categoryData.maxStage = int1 > 67 ? 2 : (int1 > 50 ? 1 : 0);
				}

				categoryData.gameObj = (Integer)arrayList.get(square2.rand(square.x, square.y, arrayList.size()));
				categoryData.stage = 0;
				categoryData.spawnTime = 100 - int1;
				return true;
			} else {
				return false;
			}
		}
	}

	public void update(IsoGridSquare square, ErosionData.Square square2, ErosionCategory.Data data, ErosionData.Chunk chunk, int int1) {
		NatureGeneric.CategoryData categoryData = (NatureGeneric.CategoryData)data;
		if (int1 >= categoryData.spawnTime && !categoryData.doNothing) {
			if (categoryData.gameObj >= 0 && categoryData.gameObj < this.objs.size()) {
				ErosionObj erosionObj = (ErosionObj)this.objs.get(categoryData.gameObj);
				int int2 = categoryData.maxStage;
				int int3 = (int)Math.floor((double)((float)(int1 - categoryData.spawnTime) / ((float)erosionObj.cycleTime / ((float)int2 + 1.0F))));
				if (int3 > int2) {
					int3 = int2;
				}

				if (int3 >= erosionObj.stages) {
					int3 = erosionObj.stages - 1;
				}

				if (categoryData.stage == categoryData.maxStage) {
					int3 = categoryData.maxStage;
				}

				int int4 = 0;
				if (!categoryData.notGrass) {
					int4 = this.currentSeason(square2.magicNum, erosionObj);
					int int5 = this.getGroundGrassType(square);
					if (int5 == 2) {
						int4 = Math.max(int4, 3);
					} else if (int5 == 3) {
						int4 = Math.max(int4, 4);
					}
				}

				boolean boolean1 = false;
				boolean boolean2 = false;
				this.updateObj(square2, data, square, erosionObj, boolean1, int3, int4, boolean2);
			} else {
				categoryData.doNothing = true;
			}
		}
	}

	public void init() {
		for (int int1 = 0; int1 < 100; ++int1) {
			this.spawnChance[int1] = (int)this.clerp((float)(int1 - 0) / 100.0F, 0.0F, 99.0F);
		}

		this.seasonDisp[5].season1 = 5;
		this.seasonDisp[5].season2 = 0;
		this.seasonDisp[5].split = false;
		this.seasonDisp[1].season1 = 1;
		this.seasonDisp[1].season2 = 0;
		this.seasonDisp[1].split = false;
		this.seasonDisp[2].season1 = 2;
		this.seasonDisp[2].season2 = 3;
		this.seasonDisp[2].split = true;
		this.seasonDisp[4].season1 = 4;
		this.seasonDisp[4].season2 = 5;
		this.seasonDisp[4].split = true;
		int[] intArray = new int[]{1, 2, 3, 4, 5};
		int[] intArray2 = new int[]{2, 1, 0};
		int int2;
		for (int2 = 0; int2 < 3; ++int2) {
			this.objsRef.add(new ArrayList());
		}

		ErosionObjSprites erosionObjSprites;
		int int3;
		int int4;
		int int5;
		ErosionObj erosionObj;
		for (int2 = 0; int2 <= 5; ++int2) {
			erosionObjSprites = new ErosionObjSprites(3, "Grass", false, false, false);
			for (int3 = 0; int3 < intArray.length; ++int3) {
				for (int4 = 0; int4 < intArray2.length; ++int4) {
					int5 = 0 + int3 * 18 + int4 * 6 + int2;
					erosionObjSprites.setBase(intArray2[int4], "e_newgrass_1_" + int5, intArray[int3]);
				}
			}

			erosionObj = new ErosionObj(erosionObjSprites, 60, 0.0F, 0.0F, false);
			this.objs.add(erosionObj);
			((ArrayList)this.objsRef.get(0)).add(this.objs.size() - 1);
		}

		for (int2 = 0; int2 <= 15; ++int2) {
			erosionObjSprites = new ErosionObjSprites(2, "Generic", false, false, false);
			for (int3 = 0; int3 <= 1; ++int3) {
				int4 = int3 * 16 + int2;
				erosionObjSprites.setBase(int3, (String)("d_generic_1_" + int4), 0);
			}

			erosionObj = new ErosionObj(erosionObjSprites, 60, 0.0F, 0.0F, true);
			this.objs.add(erosionObj);
			((ArrayList)this.objsRef.get(2)).add(this.objs.size() - 1);
		}

		ErosionIceQueen erosionIceQueen = ErosionIceQueen.instance;
		for (int int6 = 0; int6 <= 7; ++int6) {
			ErosionObjSprites erosionObjSprites2 = new ErosionObjSprites(2, "Fern", true, false, false);
			for (int4 = 0; int4 <= 1; ++int4) {
				int5 = 48 + int4 * 32 + int6;
				erosionObjSprites2.setBase(int4, (String)("d_generic_1_" + int5), 0);
				erosionIceQueen.addSprite("d_generic_1_" + int5, "d_generic_1_" + (int5 + 16));
			}

			ErosionObj erosionObj2 = new ErosionObj(erosionObjSprites2, 60, 0.0F, 0.0F, true);
			this.objs.add(erosionObj2);
			((ArrayList)this.objsRef.get(1)).add(this.objs.size() - 1);
		}
	}

	protected ErosionCategory.Data allocData() {
		return new NatureGeneric.CategoryData();
	}

	private int toInt(char char1) {
		switch (char1) {
		case '0': 
			return 0;
		
		case '1': 
			return 1;
		
		case '2': 
			return 2;
		
		case '3': 
			return 3;
		
		case '4': 
			return 4;
		
		case '5': 
			return 5;
		
		case '6': 
			return 6;
		
		case '7': 
			return 7;
		
		case '8': 
			return 8;
		
		case '9': 
			return 9;
		
		default: 
			return 0;
		
		}
	}

	private int getGroundGrassType(IsoGridSquare square) {
		IsoObject object = square.getFloor();
		if (object == null) {
			return 0;
		} else {
			IsoSprite sprite = object.getSprite();
			if (sprite != null && sprite.getName() != null && sprite.getName().startsWith("blends_natural_01_")) {
				int int1 = 0;
				int int2;
				for (int2 = 18; int2 < sprite.getName().length(); ++int2) {
					int1 += this.toInt(sprite.getName().charAt(int2));
					if (int2 < sprite.getName().length() - 1) {
						int1 *= 10;
					}
				}

				int2 = int1 / 8;
				int int3 = int1 % 8;
				if (int2 == 2 && (int3 == 0 || int3 >= 5)) {
					return 1;
				}

				if (int2 == 4 && (int3 == 0 || int3 >= 5)) {
					return 2;
				}

				if (int2 == 6 && (int3 == 0 || int3 >= 5)) {
					return 3;
				}
			}

			return 0;
		}
	}

	private class CategoryData extends ErosionCategory.Data {
		public int gameObj;
		public int maxStage;
		public int spawnTime;
		public boolean notGrass;

		private CategoryData() {
			super();
		}

		public void save(ByteBuffer byteBuffer) {
			super.save(byteBuffer);
			byteBuffer.put((byte)this.gameObj);
			byteBuffer.put((byte)this.maxStage);
			byteBuffer.putShort((short)this.spawnTime);
			byteBuffer.put((byte)(this.notGrass ? 1 : 0));
		}

		public void load(ByteBuffer byteBuffer, int int1) {
			super.load(byteBuffer, int1);
			this.gameObj = byteBuffer.get();
			this.maxStage = byteBuffer.get();
			this.spawnTime = byteBuffer.getShort();
			this.notGrass = byteBuffer.get() == 1;
		}

		CategoryData(Object object) {
			this();
		}
	}
}
