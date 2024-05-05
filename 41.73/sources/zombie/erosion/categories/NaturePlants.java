package zombie.erosion.categories;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.core.Rand;
import zombie.erosion.ErosionData;
import zombie.erosion.obj.ErosionObj;
import zombie.erosion.obj.ErosionObjSprites;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.sprite.IsoSprite;


public final class NaturePlants extends ErosionCategory {
	private final int[][] soilRef = new int[][]{{17, 17, 17, 17, 17, 17, 17, 17, 17, 1, 2, 8, 8}, {11, 12, 1, 2, 8, 1, 2, 8, 1, 2, 8, 1, 2, 8, 1, 2, 8}, {11, 12, 11, 12, 11, 12, 11, 12, 15, 16, 18, 19}, {22, 22, 22, 22, 22, 22, 22, 22, 22, 3, 4, 14}, {15, 16, 3, 4, 14, 3, 4, 14, 3, 4, 14, 3, 4, 14}, {11, 12, 15, 16, 15, 16, 15, 16, 15, 16, 21}, {13, 13, 13, 13, 13, 13, 13, 13, 13, 5, 6, 24}, {18, 19, 5, 6, 24, 5, 6, 24, 5, 6, 24, 5, 6, 24}, {18, 19, 18, 19, 18, 19, 18, 19, 20, 21}, {7, 7, 7, 7, 7, 7, 7, 7, 7, 9, 10, 23}, {19, 20, 9, 10, 23, 9, 10, 23, 9, 10, 23, 9, 10, 23}, {15, 16, 18, 19, 20, 19, 20, 19, 20}};
	private int[] spawnChance = new int[100];
	private ArrayList objs = new ArrayList();
	private final NaturePlants.PlantInit[] plants = new NaturePlants.PlantInit[]{new NaturePlants.PlantInit("Butterfly Weed", true, 0.05F, 0.25F), new NaturePlants.PlantInit("Butterfly Weed", true, 0.05F, 0.25F), new NaturePlants.PlantInit("Swamp Sunflower", true, 0.2F, 0.45F), new NaturePlants.PlantInit("Swamp Sunflower", true, 0.2F, 0.45F), new NaturePlants.PlantInit("Purple Coneflower", true, 0.1F, 0.35F), new NaturePlants.PlantInit("Purple Coneflower", true, 0.1F, 0.35F), new NaturePlants.PlantInit("Joe-Pye Weed", true, 0.8F, 1.0F), new NaturePlants.PlantInit("Blazing Star", true, 0.25F, 0.65F), new NaturePlants.PlantInit("Wild Bergamot", true, 0.45F, 0.6F), new NaturePlants.PlantInit("Wild Bergamot", true, 0.45F, 0.6F), new NaturePlants.PlantInit("White Beard-tongue", true, 0.2F, 0.65F), new NaturePlants.PlantInit("White Beard-tongue", true, 0.2F, 0.65F), new NaturePlants.PlantInit("Ironweed", true, 0.75F, 0.85F), new NaturePlants.PlantInit("White Baneberry", true, 0.4F, 0.8F), new NaturePlants.PlantInit("Wild Columbine", true, 0.85F, 1.0F), new NaturePlants.PlantInit("Wild Columbine", true, 0.85F, 1.0F), new NaturePlants.PlantInit("Jack-in-the-pulpit", false, 0.0F, 0.0F), new NaturePlants.PlantInit("Wild Ginger", true, 0.1F, 0.9F), new NaturePlants.PlantInit("Wild Ginger", true, 0.1F, 0.9F), new NaturePlants.PlantInit("Wild Geranium", true, 0.65F, 0.9F), new NaturePlants.PlantInit("Alumroot", true, 0.35F, 0.75F), new NaturePlants.PlantInit("Wild Blue Phlox", true, 0.15F, 0.55F), new NaturePlants.PlantInit("Polemonium Reptans", true, 0.4F, 0.6F), new NaturePlants.PlantInit("Foamflower", true, 0.45F, 1.0F)};

	public boolean replaceExistingObject(IsoGridSquare square, ErosionData.Square square2, ErosionData.Chunk chunk, boolean boolean1, boolean boolean2) {
		int int1 = square.getObjects().size();
		for (int int2 = int1 - 1; int2 >= 1; --int2) {
			IsoObject object = (IsoObject)square.getObjects().get(int2);
			IsoSprite sprite = object.getSprite();
			if (sprite != null && sprite.getName() != null) {
				if (sprite.getName().startsWith("d_plants_1_")) {
					int int3 = Integer.parseInt(sprite.getName().replace("d_plants_1_", ""));
					NaturePlants.CategoryData categoryData = (NaturePlants.CategoryData)this.setCatModData(square2);
					categoryData.gameObj = int3 < 32 ? int3 % 8 : (int3 < 48 ? int3 % 8 + 8 : int3 % 8 + 16);
					categoryData.stage = 0;
					categoryData.spawnTime = 0;
					square.RemoveTileObjectErosionNoRecalc(object);
					return true;
				}

				NaturePlants.CategoryData categoryData2;
				if ("vegetation_groundcover_01_16".equals(sprite.getName()) || "vegetation_groundcover_01_17".equals(sprite.getName())) {
					categoryData2 = (NaturePlants.CategoryData)this.setCatModData(square2);
					categoryData2.gameObj = 21;
					categoryData2.stage = 0;
					categoryData2.spawnTime = 0;
					square.RemoveTileObjectErosionNoRecalc(object);
					while (true) {
						--int2;
						if (int2 <= 0) {
							return true;
						}

						object = (IsoObject)square.getObjects().get(int2);
						sprite = object.getSprite();
						if (sprite != null && sprite.getName() != null && sprite.getName().startsWith("vegetation_groundcover_01_")) {
							square.RemoveTileObjectErosionNoRecalc(object);
						}
					}
				}

				if ("vegetation_groundcover_01_18".equals(sprite.getName()) || "vegetation_groundcover_01_19".equals(sprite.getName()) || "vegetation_groundcover_01_20".equals(sprite.getName()) || "vegetation_groundcover_01_21".equals(sprite.getName()) || "vegetation_groundcover_01_22".equals(sprite.getName()) || "vegetation_groundcover_01_23".equals(sprite.getName())) {
					categoryData2 = (NaturePlants.CategoryData)this.setCatModData(square2);
					categoryData2.gameObj = Rand.Next(this.plants.length);
					categoryData2.stage = 0;
					categoryData2.spawnTime = 0;
					square.RemoveTileObjectErosionNoRecalc(object);
					while (true) {
						--int2;
						if (int2 <= 0) {
							return true;
						}

						object = (IsoObject)square.getObjects().get(int2);
						sprite = object.getSprite();
						if (sprite != null && sprite.getName() != null && sprite.getName().startsWith("vegetation_groundcover_01_")) {
							square.RemoveTileObjectErosionNoRecalc(object);
						}
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
			if (square2.rand(square.x, square.y, 101) < this.spawnChance[int1]) {
				NaturePlants.CategoryData categoryData = (NaturePlants.CategoryData)this.setCatModData(square2);
				categoryData.gameObj = intArray[square2.rand(square.x, square.y, intArray.length)] - 1;
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
		NaturePlants.CategoryData categoryData = (NaturePlants.CategoryData)data;
		if (int1 >= categoryData.spawnTime && !categoryData.doNothing) {
			if (categoryData.gameObj >= 0 && categoryData.gameObj < this.objs.size()) {
				ErosionObj erosionObj = (ErosionObj)this.objs.get(categoryData.gameObj);
				boolean boolean1 = false;
				byte byte1 = 0;
				int int2 = this.currentSeason(square2.magicNum, erosionObj);
				boolean boolean2 = this.currentBloom(square2.magicNum, erosionObj);
				this.updateObj(square2, data, square, erosionObj, boolean1, byte1, int2, boolean2);
			} else {
				this.clearCatModData(square2);
			}
		}
	}

	public void init() {
		for (int int1 = 0; int1 < 100; ++int1) {
			if (int1 >= 20 && int1 < 50) {
				this.spawnChance[int1] = (int)this.clerp((float)(int1 - 20) / 30.0F, 0.0F, 8.0F);
			} else if (int1 >= 50 && int1 < 80) {
				this.spawnChance[int1] = (int)this.clerp((float)(int1 - 50) / 30.0F, 8.0F, 0.0F);
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
		String string = "d_plants_1_";
		ArrayList arrayList = new ArrayList();
		for (int int2 = 0; int2 <= 7; ++int2) {
			arrayList.add(string + int2);
		}

		ArrayList arrayList2 = new ArrayList();
		for (int int3 = 8; int3 <= 15; ++int3) {
			arrayList2.add(string + int3);
		}

		byte byte1 = 16;
		for (int int4 = 0; int4 < this.plants.length; ++int4) {
			if (int4 >= 8) {
				byte1 = 24;
			}

			if (int4 >= 16) {
				byte1 = 32;
			}

			NaturePlants.PlantInit plantInit = this.plants[int4];
			ErosionObjSprites erosionObjSprites = new ErosionObjSprites(1, plantInit.name, false, plantInit.hasFlower, false);
			erosionObjSprites.setBase(0, (ArrayList)arrayList, 1);
			erosionObjSprites.setBase(0, (ArrayList)arrayList2, 4);
			erosionObjSprites.setBase(0, (String)(string + (byte1 + int4)), 2);
			erosionObjSprites.setFlower(0, (String)(string + (byte1 + int4 + 8)));
			float float1 = plantInit.hasFlower ? plantInit.bloomstart : 0.0F;
			float float2 = plantInit.hasFlower ? plantInit.bloomend : 0.0F;
			ErosionObj erosionObj = new ErosionObj(erosionObjSprites, 30, float1, float2, false);
			this.objs.add(erosionObj);
		}
	}

	protected ErosionCategory.Data allocData() {
		return new NaturePlants.CategoryData();
	}

	public void getObjectNames(ArrayList arrayList) {
		for (int int1 = 0; int1 < this.objs.size(); ++int1) {
			if (((ErosionObj)this.objs.get(int1)).name != null && !arrayList.contains(((ErosionObj)this.objs.get(int1)).name)) {
				arrayList.add(((ErosionObj)this.objs.get(int1)).name);
			}
		}
	}

	private class PlantInit {
		public String name;
		public boolean hasFlower;
		public float bloomstart;
		public float bloomend;

		public PlantInit(String string, boolean boolean1, float float1, float float2) {
			this.name = string;
			this.hasFlower = boolean1;
			this.bloomstart = float1;
			this.bloomend = float2;
		}
	}

	private static final class CategoryData extends ErosionCategory.Data {
		public int gameObj;
		public int spawnTime;

		public void save(ByteBuffer byteBuffer) {
			super.save(byteBuffer);
			byteBuffer.put((byte)this.gameObj);
			byteBuffer.putShort((short)this.spawnTime);
		}

		public void load(ByteBuffer byteBuffer, int int1) {
			super.load(byteBuffer, int1);
			this.gameObj = byteBuffer.get();
			this.spawnTime = byteBuffer.getShort();
		}
	}
}
