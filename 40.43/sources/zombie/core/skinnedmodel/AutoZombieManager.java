package zombie.core.skinnedmodel;

import zombie.GameWindow;
import zombie.core.PerformanceSettings;
import zombie.core.utils.OnceEvery;


public class AutoZombieManager {
	public static AutoZombieManager instance = new AutoZombieManager();
	OnceEvery every = new OnceEvery(0.6F);

	public void update() {
		if (this.every.Check() && PerformanceSettings.auto3DZombies) {
			float float1 = GameWindow.averageFPS;
			float float2 = (float)PerformanceSettings.LockFPS;
			float float3 = float1 / float2;
			if (PerformanceSettings.numberOf3D != 0) {
				PerformanceSettings.numberOf3D = 0;
			}

			if (float3 < 0.75F) {
				PerformanceSettings.numberOf3DAlt -= 2;
				if (PerformanceSettings.numberOf3DAlt < 0) {
					PerformanceSettings.numberOf3DAlt = 0;
				}
			} else if (float3 > 0.98F) {
				++PerformanceSettings.numberOf3DAlt;
				if (PerformanceSettings.numberOf3DAlt > 100) {
					PerformanceSettings.numberOf3DAlt = 100;
				}
			}
		}
	}
}
