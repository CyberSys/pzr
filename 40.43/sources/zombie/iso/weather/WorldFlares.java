package zombie.iso.weather;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.opengl.RenderSettings;
import zombie.iso.weather.fx.SteppedUpdateFloat;


public class WorldFlares {
	private static ArrayList flares = new ArrayList();

	public static void launchFlare(float float1, int int1, int int2, float float2, float float3, float float4) {
		WorldFlares.Flare flare = new WorldFlares.Flare();
		flare.color.r = float2;
		flare.color.g = float3;
		flare.color.b = float4;
		flare.hasLaunched = true;
		flare.maxLifeTime = float1;
		flares.add(flare);
	}

	public static void update() {
		for (int int1 = flares.size() - 1; int1 >= 0; --int1) {
		}
	}

	public static void applyFlaresForPlayer(RenderSettings.PlayerRenderSettings playerRenderSettings, int int1, IsoPlayer player) {
	}

	private static class Flare {
		private Color color;
		private boolean hasLaunched;
		private SteppedUpdateFloat intensity;
		private float maxLifeTime;
		private float lifeTime;
		private int nextRandomTargetIntens;

		private Flare() {
			this.color = new Color(1.0F, 0.0F, 0.0F);
			this.hasLaunched = false;
			this.intensity = new SteppedUpdateFloat(0.0F, 0.01F, 0.0F, 1.0F);
			this.nextRandomTargetIntens = 10;
		}

		Flare(Object object) {
			this();
		}
	}
}
