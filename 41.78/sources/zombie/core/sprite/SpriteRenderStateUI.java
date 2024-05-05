package zombie.core.sprite;


public final class SpriteRenderStateUI extends GenericSpriteRenderState {
	public boolean bActive;

	public SpriteRenderStateUI(int int1) {
		super(int1);
	}

	public void clear() {
		try {
			this.bActive = true;
			super.clear();
		} finally {
			this.bActive = false;
		}
	}
}
