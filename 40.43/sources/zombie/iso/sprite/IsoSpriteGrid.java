package zombie.iso.sprite;


public class IsoSpriteGrid {
	private IsoSprite[] sprites;
	private int width;
	private int height;

	public IsoSpriteGrid(int int1, int int2) {
		this.sprites = new IsoSprite[int1 * int2];
		this.width = int1;
		this.height = int2;
	}

	public IsoSprite getAnchorSprite() {
		return this.sprites.length > 0 ? this.sprites[0] : null;
	}

	public IsoSprite getSprite(int int1, int int2) {
		return this.getSpriteFromIndex(int2 * this.width + int1);
	}

	public void setSprite(int int1, int int2, IsoSprite sprite) {
		this.sprites[int2 * this.width + int1] = sprite;
	}

	public int getSpriteIndex(IsoSprite sprite) {
		for (int int1 = 0; int1 < this.sprites.length; ++int1) {
			IsoSprite sprite2 = this.sprites[int1];
			if (sprite2 != null && sprite2 == sprite) {
				return int1;
			}
		}

		return -1;
	}

	public int getSpriteGridPosX(IsoSprite sprite) {
		int int1 = this.getSpriteIndex(sprite);
		return int1 >= 0 ? int1 % this.width : -1;
	}

	public int getSpriteGridPosY(IsoSprite sprite) {
		int int1 = this.getSpriteIndex(sprite);
		return int1 >= 0 ? int1 / this.width : -1;
	}

	public IsoSprite getSpriteFromIndex(int int1) {
		return int1 >= 0 && int1 < this.sprites.length ? this.sprites[int1] : null;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public boolean validate() {
		for (int int1 = 0; int1 < this.sprites.length; ++int1) {
			if (this.sprites[int1] == null) {
				return false;
			}
		}

		return true;
	}

	public int getSpriteCount() {
		return this.sprites.length;
	}

	public IsoSprite[] getSprites() {
		return this.sprites;
	}
}
