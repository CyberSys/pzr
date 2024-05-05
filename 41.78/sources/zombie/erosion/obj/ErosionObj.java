package zombie.erosion.obj;

import java.util.ArrayList;
import zombie.erosion.ErosionMain;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoTree;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.util.list.PZArrayList;


public final class ErosionObj {
	private final ErosionObjSprites sprites;
	public String name;
	public int stages;
	public boolean hasSnow;
	public boolean hasFlower;
	public boolean hasChildSprite;
	public float bloomStart;
	public float bloomEnd;
	public boolean noSeasonBase;
	public int cycleTime = 1;

	public ErosionObj(ErosionObjSprites erosionObjSprites, int int1, float float1, float float2, boolean boolean1) {
		this.sprites = erosionObjSprites;
		this.name = erosionObjSprites.name;
		this.stages = erosionObjSprites.stages;
		this.hasSnow = erosionObjSprites.hasSnow;
		this.hasFlower = erosionObjSprites.hasFlower;
		this.hasChildSprite = erosionObjSprites.hasChildSprite;
		this.bloomStart = float1;
		this.bloomEnd = float2;
		this.noSeasonBase = boolean1;
		this.cycleTime = int1;
	}

	public IsoObject getObject(IsoGridSquare square, boolean boolean1) {
		PZArrayList pZArrayList = square.getObjects();
		for (int int1 = pZArrayList.size() - 1; int1 >= 0; --int1) {
			IsoObject object = (IsoObject)pZArrayList.get(int1);
			if (this.name.equals(object.getName())) {
				if (boolean1) {
					pZArrayList.remove(int1);
				}

				object.doNotSync = true;
				return object;
			}
		}

		return null;
	}

	public IsoObject createObject(IsoGridSquare square, int int1, boolean boolean1, int int2) {
		String string = this.sprites.getBase(int1, this.noSeasonBase ? 0 : int2);
		if (string == null) {
			string = "";
		}

		Object object;
		if (boolean1) {
			object = IsoTree.getNew();
			((IsoObject)object).sprite = (IsoSprite)IsoSpriteManager.instance.NamedMap.get(string);
			((IsoObject)object).square = square;
			((IsoObject)object).sx = 0.0F;
			((IsoTree)object).initTree();
		} else {
			object = IsoObject.getNew(square, string, this.name, false);
		}

		((IsoObject)object).setName(this.name);
		((IsoObject)object).doNotSync = true;
		return (IsoObject)object;
	}

	public boolean placeObject(IsoGridSquare square, int int1, boolean boolean1, int int2, boolean boolean2) {
		IsoObject object = this.createObject(square, int1, boolean1, int2);
		if (object != null && this.setStageObject(int1, object, int2, boolean2)) {
			object.doNotSync = true;
			if (!boolean1) {
				square.getObjects().add(object);
				object.addToWorld();
			} else {
				square.AddTileObject(object);
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean setStageObject(int int1, IsoObject object, int int2, boolean boolean1) {
		object.doNotSync = true;
		if (int1 >= 0 && int1 < this.stages && object != null) {
			String string = this.sprites.getBase(int1, this.noSeasonBase ? 0 : int2);
			if (string == null) {
				object.setSprite(this.getSprite(""));
				if (object.AttachedAnimSprite != null) {
					object.AttachedAnimSprite.clear();
				}

				return true;
			} else {
				IsoSprite sprite = this.getSprite(string);
				object.setSprite(sprite);
				if (this.hasChildSprite || this.hasFlower) {
					if (object.AttachedAnimSprite == null) {
						object.AttachedAnimSprite = new ArrayList();
					}

					object.AttachedAnimSprite.clear();
					if (this.hasChildSprite && int2 != 0) {
						string = this.sprites.getChildSprite(int1, int2);
						if (string != null) {
							sprite = this.getSprite(string);
							object.AttachedAnimSprite.add(sprite.newInstance());
						}
					}

					if (this.hasFlower && boolean1) {
						string = this.sprites.getFlower(int1);
						if (string != null) {
							sprite = this.getSprite(string);
							object.AttachedAnimSprite.add(sprite.newInstance());
						}
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	public boolean setStage(IsoGridSquare square, int int1, int int2, boolean boolean1) {
		IsoObject object = this.getObject(square, false);
		return object != null ? this.setStageObject(int1, object, int2, boolean1) : false;
	}

	public IsoObject removeObject(IsoGridSquare square) {
		return this.getObject(square, true);
	}

	private IsoSprite getSprite(String string) {
		return ErosionMain.getInstance().getSpriteManager().getSprite(string);
	}
}
