package zombie.iso.sprite;

import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.HashMap;
import zombie.core.Color;
import zombie.debug.DebugLog;
import zombie.iso.SpriteDetails.IsoFlagType;


public class IsoSpriteManager {
	public HashMap NamedMap = new HashMap();
	public TIntObjectHashMap IntMap = new TIntObjectHashMap();

	public IsoSpriteManager() {
		IsoSprite sprite = new IsoSprite(this);
		sprite.name = "";
		sprite.ID = -1;
		sprite.Properties.Set(IsoFlagType.invisible);
		sprite.CurrentAnim = new IsoAnim();
		sprite.CurrentAnim.ID = sprite.AnimStack.size();
		sprite.AnimStack.add(sprite.CurrentAnim);
		sprite.AnimMap.put("default", sprite.CurrentAnim);
		this.NamedMap.put(sprite.name, sprite);
	}

	public void Dispose() {
		IsoSprite.DisposeAll();
		IsoAnim.DisposeAll();
		Object[] objectArray = this.IntMap.values();
		for (int int1 = 0; int1 < objectArray.length; ++int1) {
			IsoSprite sprite = (IsoSprite)objectArray[int1];
			sprite.Dispose();
			sprite.def = null;
			sprite.parentManager = null;
		}

		this.IntMap.clear();
		this.NamedMap.clear();
	}

	public IsoSprite getSprite(int int1) {
		return this.IntMap.containsKey(int1) ? (IsoSprite)this.IntMap.get(int1) : null;
	}

	public IsoSprite getSprite(String string) {
		return this.NamedMap.containsKey(string) ? (IsoSprite)this.NamedMap.get(string) : this.AddSprite(string);
	}

	public IsoSprite getOrAddSpriteCache(String string) {
		if (this.NamedMap.containsKey(string)) {
			return (IsoSprite)this.NamedMap.get(string);
		} else {
			IsoSprite sprite = new IsoSprite(this);
			sprite.LoadFramesNoDirPageSimple(string);
			this.NamedMap.put(string, sprite);
			return sprite;
		}
	}

	public IsoSprite getOrAddSpriteCache(String string, Color color) {
		int int1 = (int)(color.r * 255.0F);
		int int2 = (int)(color.g * 255.0F);
		int int3 = (int)(color.b * 255.0F);
		String string2 = string + "_" + int1 + "_" + int2 + "_" + int3;
		if (this.NamedMap.containsKey(string2)) {
			return (IsoSprite)this.NamedMap.get(string2);
		} else {
			IsoSprite sprite = new IsoSprite(this);
			sprite.LoadFramesNoDirPageSimple(string);
			this.NamedMap.put(string2, sprite);
			return sprite;
		}
	}

	public IsoSprite AddSprite(String string) {
		IsoSprite sprite = new IsoSprite(this);
		sprite.LoadFramesNoDirPageSimple(string);
		this.NamedMap.put(string, sprite);
		return sprite;
	}

	public IsoSprite AddSprite(String string, int int1) {
		IsoSprite sprite = new IsoSprite(this);
		sprite.LoadFramesNoDirPageSimple(string);
		if (this.NamedMap.containsKey(string)) {
			DebugLog.log("duplicate texture " + string + " ignore ID=" + int1 + ", use ID=" + ((IsoSprite)this.NamedMap.get(string)).ID);
			int1 = ((IsoSprite)this.NamedMap.get(string)).ID;
		}

		this.NamedMap.put(string, sprite);
		sprite.ID = int1;
		this.IntMap.put(int1, sprite);
		return sprite;
	}
}
