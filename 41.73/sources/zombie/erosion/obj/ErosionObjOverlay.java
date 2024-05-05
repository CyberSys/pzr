package zombie.erosion.obj;

import java.util.ArrayList;
import zombie.iso.IsoObject;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;


public final class ErosionObjOverlay {
	private final ErosionObjOverlaySprites sprites;
	public String name;
	public int stages;
	public boolean applyAlpha;
	public int cycleTime;

	public ErosionObjOverlay(ErosionObjOverlaySprites erosionObjOverlaySprites, int int1, boolean boolean1) {
		this.sprites = erosionObjOverlaySprites;
		this.name = erosionObjOverlaySprites.name;
		this.stages = erosionObjOverlaySprites.stages;
		this.applyAlpha = boolean1;
		this.cycleTime = int1;
	}

	public int setOverlay(IsoObject object, int int1, int int2, int int3, float float1) {
		if (int2 >= 0 && int2 < this.stages && object != null) {
			if (int1 >= 0) {
				this.removeOverlay(object, int1);
			}

			IsoSprite sprite = this.sprites.getSprite(int2, int3);
			IsoSpriteInstance spriteInstance = sprite.newInstance();
			if (object.AttachedAnimSprite == null) {
				object.AttachedAnimSprite = new ArrayList();
			}

			object.AttachedAnimSprite.add(spriteInstance);
			return spriteInstance.getID();
		} else {
			return -1;
		}
	}

	public boolean removeOverlay(IsoObject object, int int1) {
		if (object == null) {
			return false;
		} else {
			ArrayList arrayList = object.AttachedAnimSprite;
			if (arrayList != null && !arrayList.isEmpty()) {
				int int2;
				for (int2 = 0; int2 < object.AttachedAnimSprite.size(); ++int2) {
					if (((IsoSpriteInstance)object.AttachedAnimSprite.get(int2)).parentSprite.ID == int1) {
						object.AttachedAnimSprite.remove(int2--);
					}
				}

				for (int2 = arrayList.size() - 1; int2 >= 0; --int2) {
					if (((IsoSpriteInstance)arrayList.get(int2)).getID() == int1) {
						arrayList.remove(int2);
						return true;
					}
				}

				return false;
			} else {
				return false;
			}
		}
	}
}
