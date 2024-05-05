package zombie.iso.sprite.shapers;

import java.util.function.Consumer;
import zombie.core.textures.TextureDraw;


public class WallPaddingShaper implements Consumer {
	public static final WallPaddingShaper instance = new WallPaddingShaper();

	public void accept(TextureDraw textureDraw) {
		SpritePadding.applyIsoPadding(textureDraw, SpritePaddingSettings.getSettings().IsoPadding);
	}
}
