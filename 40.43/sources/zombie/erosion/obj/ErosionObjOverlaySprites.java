package zombie.erosion.obj;

import zombie.erosion.ErosionMain;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;


public class ErosionObjOverlaySprites {
	public String name;
	public int stages;
	private ErosionObjOverlaySprites.Stage[] sprites;

	public ErosionObjOverlaySprites(int int1, String string) {
		this.name = string;
		this.stages = int1;
		this.sprites = new ErosionObjOverlaySprites.Stage[this.stages];
		for (int int2 = 0; int2 < this.stages; ++int2) {
			this.sprites[int2] = new ErosionObjOverlaySprites.Stage();
		}
	}

	public IsoSprite getSprite(int int1, int int2) {
		return this.sprites[int1].seasons[int2].getSprite();
	}

	public IsoSpriteInstance getSpriteInstance(int int1, int int2) {
		return this.sprites[int1].seasons[int2].getInstance();
	}

	public void setSprite(int int1, String string, int int2) {
		this.sprites[int1].seasons[int2] = new ErosionObjOverlaySprites.Sprite(string);
	}

	private static class Stage {
		public ErosionObjOverlaySprites.Sprite[] seasons;

		private Stage() {
			this.seasons = new ErosionObjOverlaySprites.Sprite[6];
		}

		Stage(Object object) {
			this();
		}
	}

	private class Sprite {
		private String sprite;

		public Sprite(String string) {
			this.sprite = string;
		}

		public IsoSprite getSprite() {
			return this.sprite != null ? ErosionMain.getInstance().getSpriteManager().getSprite(this.sprite) : null;
		}

		public IsoSpriteInstance getInstance() {
			return this.sprite != null ? ErosionMain.getInstance().getSpriteManager().getSprite(this.sprite).newInstance() : null;
		}
	}
}
