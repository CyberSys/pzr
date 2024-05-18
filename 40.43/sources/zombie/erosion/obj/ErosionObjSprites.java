package zombie.erosion.obj;

import java.util.ArrayList;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.iso.IsoDirections;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameServer;


public class ErosionObjSprites {
	public static final int SECTION_BASE = 0;
	public static final int SECTION_SNOW = 1;
	public static final int SECTION_FLOWER = 2;
	public static final int SECTION_CHILD = 3;
	public static final int NUM_SECTIONS = 4;
	public String name;
	public int stages;
	public boolean hasSnow;
	public boolean hasFlower;
	public boolean hasChildSprite;
	public boolean noSeasonBase;
	public int cycleTime = 1;
	private ErosionObjSprites.Stage[] sprites;

	public ErosionObjSprites(int int1, String string, boolean boolean1, boolean boolean2, boolean boolean3) {
		this.name = string;
		this.stages = int1;
		this.hasSnow = boolean1;
		this.hasFlower = boolean2;
		this.hasChildSprite = boolean3;
		this.sprites = new ErosionObjSprites.Stage[int1];
		for (int int2 = 0; int2 < int1; ++int2) {
			this.sprites[int2] = new ErosionObjSprites.Stage();
			this.sprites[int2].sections[0] = new ErosionObjSprites.Section();
			if (this.hasSnow) {
				this.sprites[int2].sections[1] = new ErosionObjSprites.Section();
			}

			if (this.hasFlower) {
				this.sprites[int2].sections[2] = new ErosionObjSprites.Section();
			}

			if (this.hasChildSprite) {
				this.sprites[int2].sections[3] = new ErosionObjSprites.Section();
			}
		}
	}

	private String getSprite(int int1, int int2, int int3) {
		return this.sprites[int1] != null && this.sprites[int1].sections[int2] != null && this.sprites[int1].sections[int2].seasons[int3] != null ? this.sprites[int1].sections[int2].seasons[int3].getNext() : null;
	}

	public String getBase(int int1, int int2) {
		return this.getSprite(int1, 0, int2);
	}

	public String getFlower(int int1) {
		return this.hasFlower ? this.getSprite(int1, 2, 0) : null;
	}

	public String getChildSprite(int int1, int int2) {
		return this.hasChildSprite ? this.getSprite(int1, 3, int2) : null;
	}

	private void setSprite(int int1, int int2, String string, int int3) {
		if (this.sprites[int1] != null && this.sprites[int1].sections[int2] != null) {
			this.sprites[int1].sections[int2].seasons[int3] = new ErosionObjSprites.Sprites(string);
		}
	}

	private void setSprite(int int1, int int2, ArrayList arrayList, int int3) {
		assert !arrayList.isEmpty();
		if (this.sprites[int1] != null && this.sprites[int1].sections[int2] != null) {
			this.sprites[int1].sections[int2].seasons[int3] = new ErosionObjSprites.Sprites(arrayList);
		}
	}

	public void setBase(int int1, String string, int int2) {
		this.setSprite(int1, 0, (String)string, int2);
	}

	public void setBase(int int1, ArrayList arrayList, int int2) {
		this.setSprite(int1, 0, (ArrayList)arrayList, int2);
	}

	public void setFlower(int int1, String string) {
		this.setSprite(int1, 2, (String)string, 0);
	}

	public void setFlower(int int1, ArrayList arrayList) {
		this.setSprite(int1, 2, (ArrayList)arrayList, 0);
	}

	public void setChildSprite(int int1, String string, int int2) {
		this.setSprite(int1, 3, (String)string, int2);
	}

	public void setChildSprite(int int1, ArrayList arrayList, int int2) {
		this.setSprite(int1, 3, (ArrayList)arrayList, int2);
	}

	private static class Stage {
		public ErosionObjSprites.Section[] sections;

		private Stage() {
			this.sections = new ErosionObjSprites.Section[4];
		}

		Stage(Object object) {
			this();
		}
	}

	private static class Section {
		public ErosionObjSprites.Sprites[] seasons;

		private Section() {
			this.seasons = new ErosionObjSprites.Sprites[6];
		}

		Section(Object object) {
			this();
		}
	}

	private static class Sprites {
		public ArrayList sprites = new ArrayList();
		private int index = -1;

		public Sprites(String string) {
			if (Core.bDebug || GameServer.bServer && GameServer.bDebug) {
				IsoSprite sprite = IsoWorld.instance.spriteManager.getSprite(string);
				if (sprite.CurrentAnim.Frames.size() == 0 || !GameServer.bServer && ((IsoDirectionFrame)sprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) == null || sprite.ID < 10000) {
					DebugLog.log("EMPTY SPRITE " + string);
				}
			}

			this.sprites.add(string);
		}

		public Sprites(ArrayList arrayList) {
			if (Core.bDebug || GameServer.bServer && GameServer.bDebug) {
				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					IsoSprite sprite = IsoWorld.instance.spriteManager.getSprite((String)arrayList.get(int1));
					if (sprite.CurrentAnim.Frames.size() == 0 || !GameServer.bServer && ((IsoDirectionFrame)sprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) == null || sprite.ID < 10000) {
						DebugLog.log("EMPTY SPRITE " + (String)arrayList.get(int1));
					}
				}
			}

			this.sprites.addAll(arrayList);
		}

		public String getNext() {
			if (++this.index >= this.sprites.size()) {
				this.index = 0;
			}

			return (String)this.sprites.get(this.index);
		}
	}
}
