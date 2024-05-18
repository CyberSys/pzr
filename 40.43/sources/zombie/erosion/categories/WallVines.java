package zombie.erosion.categories;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import zombie.erosion.ErosionData;
import zombie.erosion.ErosionMain;
import zombie.erosion.obj.ErosionObjOverlay;
import zombie.erosion.obj.ErosionObjOverlaySprites;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;


public class WallVines extends ErosionCategory {
	private ArrayList objs = new ArrayList();
	private static final int DIRNW = 0;
	private static final int DIRN = 1;
	private static final int DIRW = 2;
	private int[][] objsRef = new int[3][2];
	private HashMap spriteToObj = new HashMap();
	private HashMap spriteToStage = new HashMap();
	private int[] spawnChance = new int[100];

	public boolean replaceExistingObject(IsoGridSquare square, ErosionData.Square square2, ErosionData.Chunk chunk, boolean boolean1, boolean boolean2) {
		int int1 = square.getObjects().size();
		for (int int2 = int1 - 1; int2 >= 1; --int2) {
			IsoObject object = (IsoObject)square.getObjects().get(int2);
			if (object.AttachedAnimSpriteActual != null) {
				for (int int3 = 0; int3 < object.AttachedAnimSpriteActual.size(); ++int3) {
					IsoSprite sprite = (IsoSprite)object.AttachedAnimSpriteActual.get(int3);
					if (sprite != null && sprite.getName() != null && sprite.getName().startsWith("f_wallvines_1_") && this.spriteToObj.containsKey(sprite.getName())) {
						WallVines.CategoryData categoryData = (WallVines.CategoryData)this.setCatModData(square2);
						categoryData.gameObj = (Integer)this.spriteToObj.get(sprite.getName());
						int int4 = (Integer)this.spriteToStage.get(sprite.getName());
						categoryData.stage = int4;
						categoryData.maxStage = 2;
						categoryData.spawnTime = 0;
						object.AttachedAnimSpriteActual.remove(int3);
						if (object.AttachedAnimSprite != null && int3 < object.AttachedAnimSprite.size()) {
							object.AttachedAnimSprite.remove(int3);
						}

						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean validateSpawn(IsoGridSquare square, ErosionData.Square square2, ErosionData.Chunk chunk, boolean boolean1, boolean boolean2, boolean boolean3) {
		if (!boolean1) {
			return false;
		} else {
			int int1 = square2.noiseMainInt;
			int int2 = this.spawnChance[int1];
			if (int2 == 0) {
				return false;
			} else if (square2.rand(square.x, square.y, 101) >= int2) {
				return false;
			} else {
				boolean boolean4 = true;
				IsoObject object = this.validWall(square, true, true);
				IsoObject object2 = this.validWall(square, false, true);
				byte byte1;
				if (object != null && object2 != null) {
					byte1 = 0;
				} else if (object != null) {
					byte1 = 1;
				} else {
					if (object2 == null) {
						return false;
					}

					byte1 = 2;
				}

				WallVines.CategoryData categoryData = (WallVines.CategoryData)this.setCatModData(square2);
				categoryData.gameObj = this.objsRef[byte1][square2.rand(square.x, square.y, this.objsRef[byte1].length)];
				categoryData.maxStage = int1 > 65 ? 3 : (int1 > 60 ? 2 : (int1 > 55 ? 1 : 0));
				categoryData.stage = 0;
				categoryData.spawnTime = 100 - int1;
				if (categoryData.maxStage == 3) {
					IsoGridSquare square3 = IsoWorld.instance.CurrentCell.getGridSquare(square.getX(), square.getY(), square.getZ() + 1);
					if (square3 != null) {
						IsoObject object3 = this.validWall(square3, byte1 == 1, true);
						ErosionObjOverlay erosionObjOverlay = (ErosionObjOverlay)this.objs.get(categoryData.gameObj);
						if (object3 != null && erosionObjOverlay != null) {
							WallVines.CategoryData categoryData2 = new WallVines.CategoryData();
							categoryData2.gameObj = this.objsRef[byte1][square2.rand(square.x, square.y, this.objsRef[byte1].length)];
							categoryData2.maxStage = int1 > 75 ? 2 : (int1 > 70 ? 1 : 0);
							categoryData2.stage = 0;
							categoryData2.spawnTime = categoryData.spawnTime + (int)((float)erosionObjOverlay.cycleTime / ((float)categoryData.maxStage + 1.0F) * 4.0F);
							categoryData.hasTop = categoryData2;
						} else {
							categoryData.maxStage = 2;
						}
					} else {
						categoryData.maxStage = 2;
					}
				}

				return true;
			}
		}
	}

	public void update(IsoGridSquare square, ErosionData.Square square2, ErosionCategory.Data data, ErosionData.Chunk chunk, int int1) {
		WallVines.CategoryData categoryData = (WallVines.CategoryData)data;
		if (int1 >= categoryData.spawnTime && !categoryData.doNothing) {
			if (categoryData.gameObj >= 0 && categoryData.gameObj < this.objs.size()) {
				ErosionObjOverlay erosionObjOverlay = (ErosionObjOverlay)this.objs.get(categoryData.gameObj);
				int int2 = categoryData.maxStage;
				int int3 = (int)Math.floor((double)((float)(int1 - categoryData.spawnTime) / ((float)erosionObjOverlay.cycleTime / ((float)int2 + 1.0F))));
				if (int3 < categoryData.stage) {
					int3 = categoryData.stage;
				}

				if (int3 > int2) {
					int3 = int2;
				}

				if (int3 > erosionObjOverlay.stages) {
					int3 = erosionObjOverlay.stages;
				}

				if (int3 == 3 && categoryData.hasTop != null && categoryData.hasTop.spawnTime > int1) {
					int3 = 2;
				}

				int int4 = ErosionMain.getInstance().getSeasons().getSeason();
				if (int3 != categoryData.stage || categoryData.dispSeason != int4) {
					IsoObject object = null;
					IsoObject object2 = this.validWall(square, true, true);
					IsoObject object3 = this.validWall(square, false, true);
					if (object2 != null && object3 != null) {
						object = object2;
					} else if (object2 != null) {
						object = object2;
					} else if (object3 != null) {
						object = object3;
					}

					categoryData.dispSeason = int4;
					if (object != null) {
						int int5 = categoryData.curID;
						int int6 = erosionObjOverlay.setOverlay(object, int5, int3, int4, 0.0F);
						if (int6 >= 0) {
							categoryData.curID = int6;
						}
					} else {
						categoryData.doNothing = true;
					}

					if (int3 == 3 && categoryData.hasTop != null) {
						IsoGridSquare square3 = IsoWorld.instance.CurrentCell.getGridSquare(square.getX(), square.getY(), square.getZ() + 1);
						if (square3 != null) {
							this.update(square3, square2, categoryData.hasTop, chunk, int1);
						}
					}
				}
			} else {
				categoryData.doNothing = true;
			}
		}
	}

	public void init() {
		for (int int1 = 0; int1 < 100; ++int1) {
			this.spawnChance[int1] = int1 >= 50 ? 100 : 0;
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
		String string = "f_wallvines_1_";
		int[] intArray = new int[]{5, 2, 4, 1};
		int[] intArray2 = new int[]{2, 2, 1, 1, 0, 0};
		int[] intArray3 = new int[3];
		for (int int2 = 0; int2 < intArray2.length; ++int2) {
			ErosionObjOverlaySprites erosionObjOverlaySprites = new ErosionObjOverlaySprites(4, "WallVines");
			for (int int3 = 0; int3 <= 3; ++int3) {
				for (int int4 = 0; int4 <= 2; ++int4) {
					int int5 = int4 * 24 + int3 * 6 + int2;
					erosionObjOverlaySprites.setSprite(int3, string + int5, intArray[int4]);
					if (int4 == 2) {
						erosionObjOverlaySprites.setSprite(int3, string + int5, intArray[int4 + 1]);
					}

					this.spriteToObj.put(string + int5, this.objs.size());
					this.spriteToStage.put(string + int5, int3);
				}
			}

			this.objs.add(new ErosionObjOverlay(erosionObjOverlaySprites, 60, false));
			int[] intArray4 = this.objsRef[intArray2[int2]];
			int int6 = intArray2[int2];
			int int7 = intArray3[intArray2[int2]];
			intArray3[int6] = intArray3[intArray2[int2]] + 1;
			intArray4[int7] = this.objs.size() - 1;
		}
	}

	protected ErosionCategory.Data allocData() {
		return new WallVines.CategoryData();
	}

	private class CategoryData extends ErosionCategory.Data {
		public int gameObj;
		public int maxStage;
		public int spawnTime;
		public int curID;
		public WallVines.CategoryData hasTop;

		private CategoryData() {
			super();
			this.curID = -999999;
		}

		public void save(ByteBuffer byteBuffer) {
			super.save(byteBuffer);
			byteBuffer.put((byte)this.gameObj);
			byteBuffer.put((byte)this.maxStage);
			byteBuffer.putShort((short)this.spawnTime);
			byteBuffer.putInt(this.curID);
			if (this.hasTop != null) {
				byteBuffer.put((byte)1);
				byteBuffer.put((byte)this.hasTop.gameObj);
				byteBuffer.putShort((short)this.hasTop.spawnTime);
				byteBuffer.putInt(this.hasTop.curID);
			} else {
				byteBuffer.put((byte)0);
			}
		}

		public void load(ByteBuffer byteBuffer, int int1) {
			super.load(byteBuffer, int1);
			this.gameObj = byteBuffer.get();
			this.maxStage = byteBuffer.get();
			this.spawnTime = byteBuffer.getShort();
			this.curID = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			if (boolean1) {
				this.hasTop = WallVines.this.new CategoryData();
				this.hasTop.gameObj = byteBuffer.get();
				this.hasTop.spawnTime = byteBuffer.getShort();
				this.hasTop.curID = byteBuffer.getInt();
			}
		}

		CategoryData(Object object) {
			this();
		}
	}
}
