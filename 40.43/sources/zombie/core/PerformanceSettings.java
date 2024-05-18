package zombie.core;

import zombie.ui.UIManager;


public class PerformanceSettings {
	public static boolean AutomaticFrameSkipping = false;
	public static int MaxAutomaticFrameSkips = 5;
	public static int ManualFrameSkips = 0;
	public static int LockFPS = 60;
	public static int LightingFrameSkip = 0;
	public static boolean NewRoofHiding = true;
	public static boolean LightingThread = true;
	public static int LightingFPS = 15;
	public static int numberOf3D = 6;
	public static boolean auto3DZombies = false;
	public static PerformanceSettings instance = new PerformanceSettings();
	public static boolean InterpolateAnims = true;
	public static int numberOf3DAlt = 0;
	public static boolean modelsEnabled = true;
	public static boolean support3D = true;
	public static boolean corpses3D = true;

	public int getModels() {
		return numberOf3D;
	}

	public void setModels(int int1) {
		numberOf3D = int1;
	}

	public boolean getModelsEnabled() {
		return modelsEnabled;
	}

	public void setModelsEnabled(boolean boolean1) {
		modelsEnabled = boolean1;
	}

	public int getFramerate() {
		return LockFPS;
	}

	public void setFramerate(int int1) {
		LockFPS = int1;
	}

	public void setLightingQuality(int int1) {
		LightingFrameSkip = int1;
	}

	public int getLightingQuality() {
		return LightingFrameSkip;
	}

	public void setNewRoofHiding(boolean boolean1) {
		NewRoofHiding = boolean1;
	}

	public boolean getNewRoofHiding() {
		return NewRoofHiding;
	}

	public void setLightingFPS(int int1) {
		int1 = Math.max(1, Math.min(120, int1));
		LightingFPS = int1;
		System.out.println("LightingFPS set to " + LightingFPS);
	}

	public boolean getSupports3D() {
		return support3D;
	}

	public int getLightingFPS() {
		return LightingFPS;
	}

	public void setCorpses3D(boolean boolean1) {
		corpses3D = boolean1;
	}

	public boolean getCorpses3D() {
		return corpses3D;
	}

	public int getUIRenderFPS() {
		return UIManager.useUIFBO ? Core.OptionUIRenderFPS : LockFPS;
	}
}
