package zombie.iso.sprite.shapers;

import zombie.core.textures.TextureDraw;


public class WallShaperN extends WallShaper {
	public static final WallShaperN instance = new WallShaperN();

	public void accept(TextureDraw textureDraw) {
		super.accept(textureDraw);
		textureDraw.x0 = textureDraw.x0 * 0.5F + textureDraw.x1 * 0.5F;
		textureDraw.x3 = textureDraw.x2 * 0.5F + textureDraw.x3 * 0.5F;
		textureDraw.u0 = textureDraw.u0 * 0.5F + textureDraw.u1 * 0.5F;
		textureDraw.u3 = textureDraw.u2 * 0.5F + textureDraw.u3 * 0.5F;
		WallPaddingShaper.instance.accept(textureDraw);
	}
}
