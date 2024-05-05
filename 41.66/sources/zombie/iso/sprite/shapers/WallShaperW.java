package zombie.iso.sprite.shapers;

import zombie.core.textures.TextureDraw;


public class WallShaperW extends WallShaper {
	public static final WallShaperW instance = new WallShaperW();

	public void accept(TextureDraw textureDraw) {
		super.accept(textureDraw);
		textureDraw.x1 = textureDraw.x0 * 0.5F + textureDraw.x1 * 0.5F;
		textureDraw.x2 = textureDraw.x2 * 0.5F + textureDraw.x3 * 0.5F;
		textureDraw.u1 = textureDraw.u0 * 0.5F + textureDraw.u1 * 0.5F;
		textureDraw.u2 = textureDraw.u2 * 0.5F + textureDraw.u3 * 0.5F;
		WallPaddingShaper.instance.accept(textureDraw);
	}
}
