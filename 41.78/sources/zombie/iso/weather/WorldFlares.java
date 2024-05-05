package zombie.iso.weather;

import java.util.ArrayList;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderSettings;
import zombie.debug.LineDrawer;
import zombie.iso.IsoUtils;
import zombie.iso.Vector2;
import zombie.iso.weather.fx.SteppedUpdateFloat;


public class WorldFlares {
	public static final boolean ENABLED = true;
	public static boolean DEBUG_DRAW = false;
	public static int NEXT_ID = 0;
	private static ArrayList flares = new ArrayList();

	public static void Clear() {
		flares.clear();
	}

	public static int getFlareCount() {
		return flares.size();
	}

	public static WorldFlares.Flare getFlare(int int1) {
		return (WorldFlares.Flare)flares.get(int1);
	}

	public static WorldFlares.Flare getFlareID(int int1) {
		for (int int2 = 0; int2 < flares.size(); ++int2) {
			if (((WorldFlares.Flare)flares.get(int2)).id == int1) {
				return (WorldFlares.Flare)flares.get(int2);
			}
		}

		return null;
	}

	public static void launchFlare(float float1, int int1, int int2, int int3, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		if (flares.size() > 100) {
			flares.remove(0);
		}

		WorldFlares.Flare flare = new WorldFlares.Flare();
		flare.id = NEXT_ID++;
		flare.x = (float)int1;
		flare.y = (float)int2;
		flare.range = int3;
		flare.windSpeed = float2;
		flare.color.setExterior(float3, float4, float5, 1.0F);
		flare.color.setInterior(float6, float7, float8, 1.0F);
		flare.hasLaunched = true;
		flare.maxLifeTime = float1;
		flares.add(flare);
	}

	public static void update() {
		for (int int1 = flares.size() - 1; int1 >= 0; --int1) {
			((WorldFlares.Flare)flares.get(int1)).update();
			if (!((WorldFlares.Flare)flares.get(int1)).hasLaunched) {
				flares.remove(int1);
			}
		}
	}

	public static void applyFlaresForPlayer(RenderSettings.PlayerRenderSettings playerRenderSettings, int int1, IsoPlayer player) {
		for (int int2 = flares.size() - 1; int2 >= 0; --int2) {
			if (((WorldFlares.Flare)flares.get(int2)).hasLaunched) {
				((WorldFlares.Flare)flares.get(int2)).applyFlare(playerRenderSettings, int1, player);
			}
		}
	}

	public static void setDebugDraw(boolean boolean1) {
		DEBUG_DRAW = boolean1;
	}

	public static boolean getDebugDraw() {
		return DEBUG_DRAW;
	}

	public static void debugRender() {
		if (DEBUG_DRAW) {
			float float1 = 0.0F;
			for (int int1 = flares.size() - 1; int1 >= 0; --int1) {
				WorldFlares.Flare flare = (WorldFlares.Flare)flares.get(int1);
				float float2 = 0.5F;
				for (double double1 = 0.0; double1 < 6.283185307179586; double1 += 0.15707963267948966) {
					DrawIsoLine(flare.x + (float)flare.range * (float)Math.cos(double1), flare.y + (float)flare.range * (float)Math.sin(double1), flare.x + (float)flare.range * (float)Math.cos(double1 + 0.15707963267948966), flare.y + (float)flare.range * (float)Math.sin(double1 + 0.15707963267948966), float1, 1.0F, 1.0F, 1.0F, 0.25F, 1);
					DrawIsoLine(flare.x + float2 * (float)Math.cos(double1), flare.y + float2 * (float)Math.sin(double1), flare.x + float2 * (float)Math.cos(double1 + 0.15707963267948966), flare.y + float2 * (float)Math.sin(double1 + 0.15707963267948966), float1, 1.0F, 1.0F, 1.0F, 0.25F, 1);
				}
			}
		}
	}

	private static void DrawIsoLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, int int1) {
		float float10 = IsoUtils.XToScreenExact(float1, float2, float5, 0);
		float float11 = IsoUtils.YToScreenExact(float1, float2, float5, 0);
		float float12 = IsoUtils.XToScreenExact(float3, float4, float5, 0);
		float float13 = IsoUtils.YToScreenExact(float3, float4, float5, 0);
		LineDrawer.drawLine(float10, float11, float12, float13, float6, float7, float8, float9, int1);
	}

	public static class Flare {
		private int id;
		private float x;
		private float y;
		private int range;
		private float windSpeed = 0.0F;
		private ClimateColorInfo color = new ClimateColorInfo(1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F);
		private boolean hasLaunched = false;
		private SteppedUpdateFloat intensity = new SteppedUpdateFloat(0.0F, 0.01F, 0.0F, 1.0F);
		private float maxLifeTime;
		private float lifeTime;
		private int nextRandomTargetIntens = 10;
		private float perc = 0.0F;
		private WorldFlares.PlayerFlareLightInfo[] infos = new WorldFlares.PlayerFlareLightInfo[4];

		public Flare() {
			for (int int1 = 0; int1 < this.infos.length; ++int1) {
				this.infos[int1] = new WorldFlares.PlayerFlareLightInfo();
			}
		}

		public int getId() {
			return this.id;
		}

		public float getX() {
			return this.x;
		}

		public float getY() {
			return this.y;
		}

		public int getRange() {
			return this.range;
		}

		public float getWindSpeed() {
			return this.windSpeed;
		}

		public ClimateColorInfo getColor() {
			return this.color;
		}

		public boolean isHasLaunched() {
			return this.hasLaunched;
		}

		public float getIntensity() {
			return this.intensity.value();
		}

		public float getMaxLifeTime() {
			return this.maxLifeTime;
		}

		public float getLifeTime() {
			return this.lifeTime;
		}

		public float getPercent() {
			return this.perc;
		}

		public float getIntensityPlayer(int int1) {
			return this.infos[int1].intensity;
		}

		public float getLerpPlayer(int int1) {
			return this.infos[int1].lerp;
		}

		public float getDistModPlayer(int int1) {
			return this.infos[int1].distMod;
		}

		public ClimateColorInfo getColorPlayer(int int1) {
			return this.infos[int1].flareCol;
		}

		public ClimateColorInfo getOutColorPlayer(int int1) {
			return this.infos[int1].outColor;
		}

		private int GetDistance(int int1, int int2, int int3, int int4) {
			return (int)Math.sqrt(Math.pow((double)(int1 - int3), 2.0) + Math.pow((double)(int2 - int4), 2.0));
		}

		private void update() {
			if (this.hasLaunched) {
				if (this.lifeTime > this.maxLifeTime) {
					this.hasLaunched = false;
					return;
				}

				this.perc = this.lifeTime / this.maxLifeTime;
				this.nextRandomTargetIntens = (int)((float)this.nextRandomTargetIntens - GameTime.instance.getMultiplier());
				if (this.nextRandomTargetIntens <= 0) {
					this.intensity.setTarget(Rand.Next(0.8F, 1.0F));
					this.nextRandomTargetIntens = Rand.Next(5, 30);
				}

				this.intensity.update(GameTime.instance.getMultiplier());
				if (this.windSpeed > 0.0F) {
					Vector2 vector2 = new Vector2(this.windSpeed / 60.0F * ClimateManager.getInstance().getWindIntensity() * (float)Math.sin((double)ClimateManager.getInstance().getWindAngleRadians()), this.windSpeed / 60.0F * ClimateManager.getInstance().getWindIntensity() * (float)Math.cos((double)ClimateManager.getInstance().getWindAngleRadians()));
					this.x += vector2.x * GameTime.instance.getMultiplier();
					this.y += vector2.y * GameTime.instance.getMultiplier();
				}

				for (int int1 = 0; int1 < 4; ++int1) {
					WorldFlares.PlayerFlareLightInfo playerFlareLightInfo = this.infos[int1];
					IsoPlayer player = IsoPlayer.players[int1];
					if (player == null) {
						playerFlareLightInfo.intensity = 0.0F;
					} else {
						int int2 = this.GetDistance((int)this.x, (int)this.y, (int)player.getX(), (int)player.getY());
						if (int2 > this.range) {
							playerFlareLightInfo.intensity = 0.0F;
							playerFlareLightInfo.lerp = 1.0F;
						} else {
							playerFlareLightInfo.distMod = 1.0F - (float)int2 / (float)this.range;
							if (this.perc < 0.75F) {
								playerFlareLightInfo.lerp = 0.0F;
							} else {
								playerFlareLightInfo.lerp = (this.perc - 0.75F) / 0.25F;
							}

							playerFlareLightInfo.intensity = this.intensity.value();
						}

						float float1 = (1.0F - playerFlareLightInfo.lerp) * playerFlareLightInfo.distMod * playerFlareLightInfo.intensity;
						ClimateManager.ClimateFloat climateFloat = ClimateManager.getInstance().dayLightStrength;
						climateFloat.finalValue += (1.0F - ClimateManager.getInstance().dayLightStrength.finalValue) * float1;
						if (player != null) {
							player.dirtyRecalcGridStackTime = 1.0F;
						}
					}
				}

				this.lifeTime += GameTime.instance.getMultiplier();
			}
		}

		private void applyFlare(RenderSettings.PlayerRenderSettings playerRenderSettings, int int1, IsoPlayer player) {
			WorldFlares.PlayerFlareLightInfo playerFlareLightInfo = this.infos[int1];
			if (playerFlareLightInfo.distMod > 0.0F) {
				float float1 = 1.0F - playerRenderSettings.CM_DayLightStrength;
				float1 = playerRenderSettings.CM_NightStrength > float1 ? playerRenderSettings.CM_NightStrength : float1;
				float1 = PZMath.clamp(float1 * 2.0F, 0.0F, 1.0F);
				float float2 = 1.0F - playerFlareLightInfo.lerp;
				float2 *= playerFlareLightInfo.distMod;
				ClimateColorInfo climateColorInfo = playerRenderSettings.CM_GlobalLight;
				playerFlareLightInfo.outColor.setTo(climateColorInfo);
				Color color = playerFlareLightInfo.outColor.getExterior();
				float float3 = float1 * float2 * playerFlareLightInfo.intensity;
				color.g = playerFlareLightInfo.outColor.getExterior().g * (1.0F - float3 * 0.5F);
				color = playerFlareLightInfo.outColor.getInterior();
				float3 = float1 * float2 * playerFlareLightInfo.intensity;
				color.g = playerFlareLightInfo.outColor.getInterior().g * (1.0F - float3 * 0.5F);
				color = playerFlareLightInfo.outColor.getExterior();
				float3 = float1 * float2 * playerFlareLightInfo.intensity;
				color.b = playerFlareLightInfo.outColor.getExterior().b * (1.0F - float3 * 0.8F);
				color = playerFlareLightInfo.outColor.getInterior();
				float3 = float1 * float2 * playerFlareLightInfo.intensity;
				color.b = playerFlareLightInfo.outColor.getInterior().b * (1.0F - float3 * 0.8F);
				playerFlareLightInfo.flareCol.setTo(this.color);
				playerFlareLightInfo.flareCol.scale(float1);
				playerFlareLightInfo.flareCol.getExterior().a = 1.0F;
				playerFlareLightInfo.flareCol.getInterior().a = 1.0F;
				playerFlareLightInfo.outColor.getExterior().r = playerFlareLightInfo.outColor.getExterior().r > playerFlareLightInfo.flareCol.getExterior().r ? playerFlareLightInfo.outColor.getExterior().r : playerFlareLightInfo.flareCol.getExterior().r;
				playerFlareLightInfo.outColor.getExterior().g = playerFlareLightInfo.outColor.getExterior().g > playerFlareLightInfo.flareCol.getExterior().g ? playerFlareLightInfo.outColor.getExterior().g : playerFlareLightInfo.flareCol.getExterior().g;
				playerFlareLightInfo.outColor.getExterior().b = playerFlareLightInfo.outColor.getExterior().b > playerFlareLightInfo.flareCol.getExterior().b ? playerFlareLightInfo.outColor.getExterior().b : playerFlareLightInfo.flareCol.getExterior().b;
				playerFlareLightInfo.outColor.getExterior().a = playerFlareLightInfo.outColor.getExterior().a > playerFlareLightInfo.flareCol.getExterior().a ? playerFlareLightInfo.outColor.getExterior().a : playerFlareLightInfo.flareCol.getExterior().a;
				playerFlareLightInfo.outColor.getInterior().r = playerFlareLightInfo.outColor.getInterior().r > playerFlareLightInfo.flareCol.getInterior().r ? playerFlareLightInfo.outColor.getInterior().r : playerFlareLightInfo.flareCol.getInterior().r;
				playerFlareLightInfo.outColor.getInterior().g = playerFlareLightInfo.outColor.getInterior().g > playerFlareLightInfo.flareCol.getInterior().g ? playerFlareLightInfo.outColor.getInterior().g : playerFlareLightInfo.flareCol.getInterior().g;
				playerFlareLightInfo.outColor.getInterior().b = playerFlareLightInfo.outColor.getInterior().b > playerFlareLightInfo.flareCol.getInterior().b ? playerFlareLightInfo.outColor.getInterior().b : playerFlareLightInfo.flareCol.getInterior().b;
				playerFlareLightInfo.outColor.getInterior().a = playerFlareLightInfo.outColor.getInterior().a > playerFlareLightInfo.flareCol.getInterior().a ? playerFlareLightInfo.outColor.getInterior().a : playerFlareLightInfo.flareCol.getInterior().a;
				float float4 = 1.0F - float2 * playerFlareLightInfo.intensity;
				playerFlareLightInfo.outColor.interp(climateColorInfo, float4, climateColorInfo);
				float float5 = ClimateManager.lerp(float4, 0.35F, playerRenderSettings.CM_Ambient);
				playerRenderSettings.CM_Ambient = playerRenderSettings.CM_Ambient > float5 ? playerRenderSettings.CM_Ambient : float5;
				float float6 = ClimateManager.lerp(float4, 0.6F * playerFlareLightInfo.intensity, playerRenderSettings.CM_DayLightStrength);
				playerRenderSettings.CM_DayLightStrength = playerRenderSettings.CM_DayLightStrength > float6 ? playerRenderSettings.CM_DayLightStrength : float6;
				if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
					float float7 = ClimateManager.lerp(float4, 1.0F * float1, playerRenderSettings.CM_Desaturation);
					playerRenderSettings.CM_Desaturation = playerRenderSettings.CM_Desaturation > float7 ? playerRenderSettings.CM_Desaturation : float7;
				}
			}
		}
	}

	private static class PlayerFlareLightInfo {
		private float intensity;
		private float lerp;
		private float distMod;
		private ClimateColorInfo flareCol = new ClimateColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
		private ClimateColorInfo outColor = new ClimateColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
