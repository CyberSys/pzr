package zombie.erosion.categories;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.erosion.ErosionData;
import zombie.erosion.ErosionMain;
import zombie.erosion.obj.ErosionObj;
import zombie.erosion.obj.ErosionObjSprites;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.sprite.IsoSprite;


public final class Flowerbed extends ErosionCategory {
	private final int[] tileID = new int[]{16, 17, 18, 19, 20, 21, 22, 23, 28, 29, 30, 31};
	private final ArrayList objs = new ArrayList();

	public boolean replaceExistingObject(IsoGridSquare square, ErosionData.Square square2, ErosionData.Chunk chunk, boolean boolean1, boolean boolean2) {
		int int1 = square.getObjects().size();
		for (int int2 = int1 - 1; int2 >= 0; --int2) {
			IsoSprite sprite = ((IsoObject)square.getObjects().get(int2)).getSprite();
			if (sprite != null && sprite.getName() != null) {
				int int3;
				if (sprite.getName().startsWith("f_flowerbed_1")) {
					int3 = Integer.parseInt(sprite.getName().replace("f_flowerbed_1_", ""));
					if (int3 <= 23) {
						if (int3 >= 12) {
							int3 -= 12;
						}

						Flowerbed.CategoryData categoryData = (Flowerbed.CategoryData)this.setCatModData(square2);
						categoryData.hasSpawned = true;
						categoryData.gameObj = int3;
						categoryData.dispSeason = -1;
						ErosionObj erosionObj = (ErosionObj)this.objs.get(categoryData.gameObj);
						((IsoObject)square.getObjects().get(int2)).setName(erosionObj.name);
						return true;
					}
				}

				if (sprite.getName().startsWith("vegetation_ornamental_01")) {
					int3 = Integer.parseInt(sprite.getName().replace("vegetation_ornamental_01_", ""));
					for (int int4 = 0; int4 < this.tileID.length; ++int4) {
						if (this.tileID[int4] == int3) {
							Flowerbed.CategoryData categoryData2 = (Flowerbed.CategoryData)this.setCatModData(square2);
							categoryData2.hasSpawned = true;
							categoryData2.gameObj = int4;
							categoryData2.dispSeason = -1;
							ErosionObj erosionObj2 = (ErosionObj)this.objs.get(categoryData2.gameObj);
							((IsoObject)square.getObjects().get(int2)).setName(erosionObj2.name);
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public boolean validateSpawn(IsoGridSquare square, ErosionData.Square square2, ErosionData.Chunk chunk, boolean boolean1, boolean boolean2, boolean boolean3) {
		return false;
	}

	public void update(IsoGridSquare square, ErosionData.Square square2, ErosionCategory.Data data, ErosionData.Chunk chunk, int int1) {
		Flowerbed.CategoryData categoryData = (Flowerbed.CategoryData)data;
		if (!categoryData.doNothing) {
			if (categoryData.gameObj >= 0 && categoryData.gameObj < this.objs.size()) {
				ErosionObj erosionObj = (ErosionObj)this.objs.get(categoryData.gameObj);
				boolean boolean1 = false;
				byte byte1 = 0;
				int int2 = ErosionMain.getInstance().getSeasons().getSeason();
				boolean boolean2 = false;
				if (int2 == 5) {
					IsoObject object = erosionObj.getObject(square, false);
					if (object != null) {
						object.setSprite(ErosionMain.getInstance().getSpriteManager().getSprite("blends_natural_01_64"));
						object.setName((String)null);
					}

					this.clearCatModData(square2);
				} else {
					this.updateObj(square2, data, square, erosionObj, boolean1, byte1, int2, boolean2);
				}
			} else {
				this.clearCatModData(square2);
			}
		}
	}

	public void init() {
		String string = "vegetation_ornamental_01_";
		for (int int1 = 0; int1 < this.tileID.length; ++int1) {
			ErosionObjSprites erosionObjSprites = new ErosionObjSprites(1, "Flowerbed", false, false, false);
			erosionObjSprites.setBase(0, (String)(string + this.tileID[int1]), 1);
			erosionObjSprites.setBase(0, (String)(string + this.tileID[int1]), 2);
			erosionObjSprites.setBase(0, (String)(string + (this.tileID[int1] + 16)), 4);
			ErosionObj erosionObj = new ErosionObj(erosionObjSprites, 30, 0.0F, 0.0F, false);
			this.objs.add(erosionObj);
		}
	}

	protected ErosionCategory.Data allocData() {
		return new Flowerbed.CategoryData();
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

		public void save(ByteBuffer byteBuffer) {
			super.save(byteBuffer);
			byteBuffer.put((byte)this.gameObj);
		}

		public void load(ByteBuffer byteBuffer, int int1) {
			super.load(byteBuffer, int1);
			this.gameObj = byteBuffer.get();
		}
	}
}
