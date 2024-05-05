package zombie.iso.sprite.shapers;

import zombie.core.textures.TextureDraw;


public class WallShaperWhole extends WallShaper {
	public static final WallShaperWhole instance = new WallShaperWhole();

	public void accept(TextureDraw textureDraw) {
		super.accept(textureDraw);
		WallPaddingShaper.instance.accept(textureDraw);
	}
}
