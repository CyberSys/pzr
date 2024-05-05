package zombie.erosion.categories;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.erosion.ErosionData;
import zombie.erosion.obj.ErosionObj;
import zombie.erosion.obj.ErosionObjOverlay;
import zombie.erosion.obj.ErosionObjOverlaySprites;
import zombie.erosion.obj.ErosionObjSprites;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;


public final class StreetCracks extends ErosionCategory {
	private ArrayList objs = new ArrayList();
	private ArrayList crackObjs = new ArrayList();
	private int[] spawnChance = new int[100];

	public boolean replaceExistingObject(IsoGridSquare square, ErosionData.Square square2, ErosionData.Chunk chunk, boolean boolean1, boolean boolean2) {
		return false;
	}

	public boolean validateSpawn(IsoGridSquare square, ErosionData.Square square2, ErosionData.Chunk chunk, boolean boolean1, boolean boolean2, boolean boolean3) {
		int int1 = square2.noiseMainInt;
		int int2 = this.spawnChance[int1];
		if (int2 == 0) {
			return false;
		} else if (square2.rand(square.x, square.y, 101) >= int2) {
			return false;
		} else {
			StreetCracks.CategoryData categoryData = (StreetCracks.CategoryData)this.setCatModData(square2);
			categoryData.gameObj = square2.rand(square.x, square.y, this.crackObjs.size());
			categoryData.maxStage = int1 > 65 ? 2 : (int1 > 55 ? 1 : 0);
			categoryData.stage = 0;
			categoryData.spawnTime = 50 + (100 - int1);
			if (square2.magicNum > 0.5F) {
				categoryData.hasGrass = true;
			}

			return true;
		}
	}

	public void update(IsoGridSquare square, ErosionData.Square square2, ErosionCategory.Data data, ErosionData.Chunk chunk, int int1) {
		StreetCracks.CategoryData categoryData = (StreetCracks.CategoryData)data;
		if (int1 >= categoryData.spawnTime && !categoryData.doNothing) {
			IsoObject object = square.getFloor();
			if (categoryData.gameObj >= 0 && categoryData.gameObj < this.crackObjs.size() && object != null) {
				ErosionObjOverlay erosionObjOverlay = (ErosionObjOverlay)this.crackObjs.get(categoryData.gameObj);
				int int2 = categoryData.maxStage;
				int int3 = (int)Math.floor((double)((float)(int1 - categoryData.spawnTime) / ((float)erosionObjOverlay.cycleTime / ((float)int2 + 1.0F))));
				if (int3 < categoryData.stage) {
					int3 = categoryData.stage;
				}

				if (int3 >= erosionObjOverlay.stages) {
					int3 = erosionObjOverlay.stages - 1;
				}

				int int4;
				if (int3 != categoryData.stage) {
					int int5 = categoryData.curID;
					int4 = erosionObjOverlay.setOverlay(object, int5, int3, 0, 0.0F);
					if (int4 >= 0) {
						categoryData.curID = int4;
					}

					categoryData.stage = int3;
				} else if (!categoryData.hasGrass && int3 == erosionObjOverlay.stages - 1) {
					categoryData.doNothing = true;
				}

				if (categoryData.hasGrass) {
					ErosionObj erosionObj = (ErosionObj)this.objs.get(categoryData.gameObj);
					if (erosionObj != null) {
						int4 = this.currentSeason(square2.magicNum, erosionObj);
						boolean boolean1 = false;
						boolean boolean2 = false;
						this.updateObj(square2, data, square, erosionObj, boolean1, int3, int4, boolean2);
					}
				}
			} else {
				categoryData.doNothing = true;
			}
		}
	}

	public void init() {
		for (int int1 = 0; int1 < 100; ++int1) {
			this.spawnChance[int1] = int1 >= 40 ? (int)this.clerp((float)(int1 - 40) / 60.0F, 0.0F, 60.0F) : 0;
		}

		this.seasonDisp[5].season1 = 5;
		this.seasonDisp[5].season2 = 0;
		this.seasonDisp[5].split = false;
		this.seasonDisp[1].season1 = 1;
		this.seasonDisp[1].season2 = 0;
		this.seasonDisp[1].split = false;
		this.seasonDisp[2].season1 = 2;
		this.seasonDisp[2].season2 = 4;
		this.seasonDisp[2].split = true;
		this.seasonDisp[4].season1 = 4;
		this.seasonDisp[4].season2 = 5;
		this.seasonDisp[4].split = true;
		String string = "d_streetcracks_1_";
		int[] intArray = new int[]{5, 1, 2, 4};
		for (int int2 = 0; int2 <= 7; ++int2) {
			ErosionObjOverlaySprites erosionObjOverlaySprites = new ErosionObjOverlaySprites(3, "StreeCracks");
			ErosionObjSprites erosionObjSprites = new ErosionObjSprites(3, "CrackGrass", false, false, false);
			for (int int3 = 0; int3 <= 2; ++int3) {
				for (int int4 = 0; int4 <= intArray.length; ++int4) {
					int int5 = int4 * 24 + int3 * 8 + int2;
					if (int4 == 0) {
						erosionObjOverlaySprites.setSprite(int3, string + int5, 0);
					} else {
						erosionObjSprites.setBase(int3, string + int5, intArray[int4 - 1]);
					}
				}
			}

			this.crackObjs.add(new ErosionObjOverlay(erosionObjOverlaySprites, 60, false));
			this.objs.add(new ErosionObj(erosionObjSprites, 60, 0.0F, 0.0F, false));
		}
	}

	protected ErosionCategory.Data allocData() {
		return new StreetCracks.CategoryData();
	}

	public void getObjectNames(ArrayList arrayList) {
		for (int int1 = 0; int1 < this.objs.size(); ++int1) {
			if (((ErosionObj)this.objs.get(int1)).name != null && !arrayList.contains(((ErosionObj)this.objs.get(int1)).name)) {
				arrayList.add(((ErosionObj)this.objs.get(int1)).name);
			}
		}
	}

	private static final class CategoryData extends ErosionCategory.Data {
		public int gameObj;
		public int maxStage;
		public int spawnTime;
		public int curID = -999999;
		public boolean hasGrass;

		public void save(ByteBuffer byteBuffer) {
			super.save(byteBuffer);
			byteBuffer.put((byte)this.gameObj);
			byteBuffer.put((byte)this.maxStage);
			byteBuffer.putShort((short)this.spawnTime);
			byteBuffer.putInt(this.curID);
			byteBuffer.put((byte)(this.hasGrass ? 1 : 0));
		}

		public void load(ByteBuffer byteBuffer, int int1) {
			super.load(byteBuffer, int1);
			this.gameObj = byteBuffer.get();
			this.maxStage = byteBuffer.get();
			this.spawnTime = byteBuffer.getShort();
			this.curID = byteBuffer.getInt();
			this.hasGrass = byteBuffer.get() == 1;
		}
	}
}
