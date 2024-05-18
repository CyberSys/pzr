package zombie.erosion.categories;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.erosion.ErosionData;
import zombie.erosion.obj.ErosionObjOverlay;
import zombie.erosion.obj.ErosionObjOverlaySprites;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;


public class WallCracks extends ErosionCategory {
	private ArrayList objs = new ArrayList();
	private static final int DIRNW = 0;
	private static final int DIRN = 1;
	private static final int DIRW = 2;
	private ArrayList objsRef = new ArrayList();
	private ArrayList botRef = new ArrayList();
	private ArrayList topRef = new ArrayList();
	private int[] spawnChance = new int[100];

	public boolean replaceExistingObject(IsoGridSquare square, ErosionData.Square square2, ErosionData.Chunk chunk, boolean boolean1, boolean boolean2) {
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
				IsoObject object = this.validWall(square, true, false);
				String string;
				if (object != null) {
					string = object.getSprite().getName();
					if (string != null && string.startsWith("fencing")) {
						object = null;
					}
				}

				IsoObject object2 = this.validWall(square, false, false);
				if (object2 != null) {
					string = object2.getSprite().getName();
					if (string != null && string.startsWith("fencing")) {
						object2 = null;
					}
				}

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

				boolean boolean5 = int1 < 35 && square2.magicNum > 0.3F;
				WallCracks.CategoryData categoryData = (WallCracks.CategoryData)this.setCatModData(square2);
				categoryData.gameObj = (Integer)((ArrayList)this.objsRef.get(byte1)).get(square2.rand(square.x, square.y, ((ArrayList)this.objsRef.get(byte1)).size()));
				categoryData.alpha = 0.0F;
				categoryData.spawnTime = int1;
				if (boolean5) {
					IsoGridSquare square3 = IsoWorld.instance.CurrentCell.getGridSquare(square.getX(), square.getY(), square.getZ() + 1);
					if (square3 != null) {
						IsoObject object3 = this.validWall(square3, byte1 == 1, false);
						if (object3 != null) {
							int int3 = square2.rand(square.x, square.y, ((ArrayList)this.botRef.get(byte1)).size());
							categoryData.gameObj = (Integer)((ArrayList)this.botRef.get(byte1)).get(int3);
							WallCracks.CategoryData categoryData2 = new WallCracks.CategoryData();
							categoryData2.gameObj = (Integer)((ArrayList)this.topRef.get(byte1)).get(int3);
							categoryData2.alpha = 0.0F;
							categoryData2.spawnTime = categoryData.spawnTime;
							categoryData.hasTop = categoryData2;
						}
					}
				}

				return true;
			}
		}
	}

	public void update(IsoGridSquare square, ErosionData.Square square2, ErosionCategory.Data data, ErosionData.Chunk chunk, int int1) {
		WallCracks.CategoryData categoryData = (WallCracks.CategoryData)data;
		if (int1 >= categoryData.spawnTime && !categoryData.doNothing) {
			if (categoryData.gameObj >= 0 && categoryData.gameObj < this.objs.size()) {
				ErosionObjOverlay erosionObjOverlay = (ErosionObjOverlay)this.objs.get(categoryData.gameObj);
				float float1 = categoryData.alpha;
				float float2 = (float)(int1 - categoryData.spawnTime) / 100.0F;
				if (float2 > 1.0F) {
					float2 = 1.0F;
				}

				if (float2 < 0.0F) {
					float2 = 0.0F;
				}

				if (float2 != float1) {
					IsoObject object = null;
					IsoObject object2 = this.validWall(square, true, false);
					IsoObject object3 = this.validWall(square, false, false);
					if (object2 != null && object3 != null) {
						object = object2;
					} else if (object2 != null) {
						object = object2;
					} else if (object3 != null) {
						object = object3;
					}

					if (object != null) {
						int int2 = categoryData.curID;
						byte byte1 = 0;
						int int3 = erosionObjOverlay.setOverlay(object, int2, byte1, 0, float2);
						if (int3 >= 0) {
							categoryData.alpha = float2;
							categoryData.curID = int3;
						}
					} else {
						categoryData.doNothing = true;
					}

					if (categoryData.hasTop != null) {
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
			this.spawnChance[int1] = int1 <= 50 ? 100 : 0;
		}

		String string = "d_wallcracks_1_";
		int[] intArray = new int[]{2, 2, 2, 1, 1, 1, 0, 0, 0};
		int int2;
		for (int2 = 0; int2 < 3; ++int2) {
			this.objsRef.add(new ArrayList());
			this.topRef.add(new ArrayList());
			this.botRef.add(new ArrayList());
		}

		for (int2 = 0; int2 < intArray.length; ++int2) {
			for (int int3 = 0; int3 <= 7; ++int3) {
				int int4 = int3 * 9 + int2;
				ErosionObjOverlaySprites erosionObjOverlaySprites = new ErosionObjOverlaySprites(1, "WallCracks");
				erosionObjOverlaySprites.setSprite(0, string + int4, 0);
				this.objs.add(new ErosionObjOverlay(erosionObjOverlaySprites, 60, true));
				((ArrayList)this.objsRef.get(intArray[int2])).add(this.objs.size() - 1);
				if (int3 == 0) {
					((ArrayList)this.botRef.get(intArray[int2])).add(this.objs.size() - 1);
				} else if (int3 == 1) {
					((ArrayList)this.topRef.get(intArray[int2])).add(this.objs.size() - 1);
				}
			}
		}
	}

	protected ErosionCategory.Data allocData() {
		return new WallCracks.CategoryData();
	}

	private class CategoryData extends ErosionCategory.Data {
		public int gameObj;
		public int spawnTime;
		public int curID;
		public float alpha;
		public WallCracks.CategoryData hasTop;

		private CategoryData() {
			super();
			this.curID = -999999;
		}

		public void save(ByteBuffer byteBuffer) {
			super.save(byteBuffer);
			byteBuffer.put((byte)this.gameObj);
			byteBuffer.putShort((short)this.spawnTime);
			byteBuffer.putInt(this.curID);
			byteBuffer.putFloat(this.alpha);
			if (this.hasTop != null) {
				byteBuffer.put((byte)1);
				byteBuffer.put((byte)this.hasTop.gameObj);
				byteBuffer.putShort((short)this.hasTop.spawnTime);
				byteBuffer.putInt(this.hasTop.curID);
				byteBuffer.putFloat(this.hasTop.alpha);
			} else {
				byteBuffer.put((byte)0);
			}
		}

		public void load(ByteBuffer byteBuffer, int int1) {
			super.load(byteBuffer, int1);
			this.gameObj = byteBuffer.get();
			this.spawnTime = byteBuffer.getShort();
			this.curID = byteBuffer.getInt();
			this.alpha = byteBuffer.getFloat();
			boolean boolean1 = byteBuffer.get() == 1;
			if (boolean1) {
				this.hasTop = WallCracks.this.new CategoryData();
				this.hasTop.gameObj = byteBuffer.get();
				this.hasTop.spawnTime = byteBuffer.getShort();
				this.hasTop.curID = byteBuffer.getInt();
				this.hasTop.alpha = byteBuffer.getFloat();
			}
		}

		CategoryData(Object object) {
			this();
		}
	}
}
