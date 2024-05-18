package zombie.erosion;

import zombie.erosion.season.ErosionIceQueen;
import zombie.erosion.season.ErosionSeason;
import zombie.iso.sprite.IsoSpriteManager;


public class ErosionGlobals {
	public static boolean EROSION_DEBUG = true;

	public static void Boot(IsoSpriteManager spriteManager) {
		new ErosionMain(spriteManager, EROSION_DEBUG);
	}

	public static void Reset() {
		ErosionMain.Reset();
		ErosionClient.Reset();
		ErosionIceQueen.Reset();
		ErosionSeason.Reset();
		ErosionRegions.Reset();
	}
}
