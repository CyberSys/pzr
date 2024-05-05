package zombie.erosion;

import zombie.erosion.season.ErosionIceQueen;
import zombie.iso.sprite.IsoSpriteManager;


public class ErosionClient {
	public static ErosionClient instance;

	public ErosionClient(IsoSpriteManager spriteManager, boolean boolean1) {
		instance = this;
		new ErosionIceQueen(spriteManager);
		ErosionRegions.init();
	}

	public static void Reset() {
		instance = null;
	}
}
