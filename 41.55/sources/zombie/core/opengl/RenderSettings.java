package zombie.core.opengl;

import java.util.function.Consumer;
import zombie.GameTime;
import zombie.IndieGL;
import zombie.SandboxOptions;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.weather.ClimateColorInfo;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.ClimateMoon;
import zombie.iso.weather.WorldFlares;
import zombie.network.GameServer;


public final class RenderSettings {
	private static RenderSettings instance;
	private static Texture texture;
	private static final float AMBIENT_MIN_SHADER = 0.4F;
	private static final float AMBIENT_MAX_SHADER = 1.0F;
	private static final float AMBIENT_MIN_LEGACY = 0.4F;
	private static final float AMBIENT_MAX_LEGACY = 1.0F;
	private final RenderSettings.PlayerRenderSettings[] playerSettings = new RenderSettings.PlayerRenderSettings[4];
	private Color defaultClear = new Color(0, 0, 0, 1);

	public static RenderSettings getInstance() {
		if (instance == null) {
			instance = new RenderSettings();
		}

		return instance;
	}

	public RenderSettings() {
		for (int int1 = 0; int1 < this.playerSettings.length; ++int1) {
			this.playerSettings[int1] = new RenderSettings.PlayerRenderSettings();
		}

		texture = Texture.getSharedTexture("media/textures/weather/fogwhite.png");
		if (texture == null) {
			DebugLog.log("Missing texture: media/textures/weather/fogwhite.png");
		}
	}

	public RenderSettings.PlayerRenderSettings getPlayerSettings(int int1) {
		return this.playerSettings[int1];
	}

	public void update() {
		if (!GameServer.bServer) {
			for (int int1 = 0; int1 < 4; ++int1) {
				if (IsoPlayer.players[int1] != null) {
					this.playerSettings[int1].updateRenderSettings(int1, IsoPlayer.players[int1]);
				}
			}
		}
	}

	public void applyRenderSettings(int int1) {
		if (!GameServer.bServer) {
			this.getPlayerSettings(int1).applyRenderSettings(int1);
		}
	}

	public void legacyPostRender(int int1) {
		if (!GameServer.bServer) {
			if (Core.getInstance().RenderShader == null || Core.getInstance().getOffscreenBuffer() == null) {
				this.getPlayerSettings(int1).legacyPostRender(int1);
			}
		}
	}

	public float getAmbientForPlayer(int int1) {
		RenderSettings.PlayerRenderSettings playerRenderSettings = this.getPlayerSettings(int1);
		return playerRenderSettings != null ? playerRenderSettings.getAmbient() : 0.0F;
	}

	public Color getMaskClearColorForPlayer(int int1) {
		RenderSettings.PlayerRenderSettings playerRenderSettings = this.getPlayerSettings(int1);
		return playerRenderSettings != null ? playerRenderSettings.getMaskClearColor() : this.defaultClear;
	}

	public static class PlayerRenderSettings {
		public ClimateColorInfo CM_GlobalLight = new ClimateColorInfo();
		public float CM_NightStrength = 0.0F;
		public float CM_Desaturation = 0.0F;
		public float CM_GlobalLightIntensity = 0.0F;
		public float CM_Ambient = 0.0F;
		public float CM_ViewDistance = 0.0F;
		public float CM_DayLightStrength = 0.0F;
		public float CM_FogIntensity = 0.0F;
		private Color blendColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);
		private ColorInfo blendInfo = new ColorInfo();
		private float blendIntensity = 0.0F;
		private float desaturation = 0.0F;
		private float darkness = 0.0F;
		private float night = 0.0F;
		private float viewDistance = 0.0F;
		private float ambient = 0.0F;
		private boolean applyNightVisionGoggles = false;
		private float goggleMod = 0.0F;
		private boolean isExterior = false;
		private float fogMod = 1.0F;
		private float rmod;
		private float gmod;
		private float bmod;
		private Color maskClearColor = new Color(0, 0, 0, 1);

		private void updateRenderSettings(int int1, IsoPlayer player) {
			ClimateManager climateManager = ClimateManager.getInstance();
			this.CM_GlobalLight = climateManager.getGlobalLight();
			this.CM_GlobalLightIntensity = climateManager.getGlobalLightIntensity();
			this.CM_Ambient = climateManager.getAmbient();
			this.CM_DayLightStrength = climateManager.getDayLightStrength();
			this.CM_NightStrength = climateManager.getNightStrength();
			this.CM_Desaturation = climateManager.getDesaturation();
			this.CM_ViewDistance = climateManager.getViewDistance();
			this.CM_FogIntensity = climateManager.getFogIntensity();
			climateManager.getThunderStorm().applyLightningForPlayer(this, int1, player);
			WorldFlares.applyFlaresForPlayer(this, int1, player);
			int int2 = SandboxOptions.instance.NightDarkness.getValue();
			this.desaturation = this.CM_Desaturation;
			this.viewDistance = this.CM_ViewDistance;
			this.applyNightVisionGoggles = player != null && player.isWearingNightVisionGoggles();
			this.isExterior = player != null && player.getCurrentSquare() != null && !player.getCurrentSquare().isInARoom();
			this.fogMod = 1.0F - this.CM_FogIntensity * 0.5F;
			this.night = this.CM_NightStrength;
			this.darkness = 1.0F - this.CM_DayLightStrength;
			if (this.isExterior) {
				this.setBlendColor(this.CM_GlobalLight.getExterior());
				this.blendIntensity = this.CM_GlobalLight.getExterior().a;
			} else {
				this.setBlendColor(this.CM_GlobalLight.getInterior());
				this.blendIntensity = this.CM_GlobalLight.getInterior().a;
			}

			this.ambient = this.CM_Ambient;
			this.viewDistance = this.CM_ViewDistance;
			--int2;
			float float1 = 0.2F + 0.1F * (float)int2;
			float1 += 0.075F * ClimateMoon.getMoonFloat() * this.night;
			if (!this.isExterior) {
				float1 *= 0.925F - 0.075F * this.darkness;
				this.desaturation *= 0.25F;
			}

			if (this.ambient < 0.2F && player.getCharacterTraits().NightVision.isSet()) {
				this.ambient = 0.2F;
			}

			this.ambient = float1 + (1.0F - float1) * this.ambient;
			if (Core.bLastStand) {
				this.ambient = 0.65F;
				this.darkness = 0.25F;
				this.night = 0.25F;
			}

			if (DebugOptions.instance.MultiplayerLightAmbient.getValue()) {
				this.ambient = 0.99F;
				this.darkness = 0.01F;
				this.night = 0.01F;
			}

			if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
				if (this.applyNightVisionGoggles) {
					this.ambient = 1.0F;
					this.rmod = GameTime.getInstance().Lerp(1.0F, 0.7F, this.darkness);
					this.gmod = GameTime.getInstance().Lerp(1.0F, 0.7F, this.darkness);
					this.bmod = GameTime.getInstance().Lerp(1.0F, 0.7F, this.darkness);
					this.maskClearColor.r = 0.0F;
					this.maskClearColor.g = 0.0F;
					this.maskClearColor.b = 0.0F;
					this.maskClearColor.a = 0.0F;
				} else {
					this.rmod = 1.0F;
					this.gmod = 1.0F;
					this.bmod = 1.0F;
					if (!this.isExterior) {
						this.maskClearColor.r = this.CM_GlobalLight.getInterior().r;
						this.maskClearColor.g = this.CM_GlobalLight.getInterior().g;
						this.maskClearColor.b = this.CM_GlobalLight.getInterior().b;
						this.maskClearColor.a = this.CM_GlobalLight.getInterior().a;
					} else {
						this.maskClearColor.r = 0.0F;
						this.maskClearColor.g = 0.0F;
						this.maskClearColor.b = 0.0F;
						this.maskClearColor.a = 0.0F;
					}
				}
			} else {
				this.desaturation *= 1.0F - this.darkness;
				this.blendInfo.r = this.blendColor.r;
				this.blendInfo.g = this.blendColor.g;
				this.blendInfo.b = this.blendColor.b;
				this.blendInfo.desaturate(this.desaturation);
				this.rmod = GameTime.getInstance().Lerp(1.0F, this.blendInfo.r, this.blendIntensity);
				this.gmod = GameTime.getInstance().Lerp(1.0F, this.blendInfo.g, this.blendIntensity);
				this.bmod = GameTime.getInstance().Lerp(1.0F, this.blendInfo.b, this.blendIntensity);
				if (this.applyNightVisionGoggles) {
					this.goggleMod = 1.0F - 0.9F * this.darkness;
					this.blendIntensity = 0.0F;
					this.night = 0.0F;
					this.ambient = 0.8F;
					this.rmod = 1.0F;
					this.gmod = 1.0F;
					this.bmod = 1.0F;
				}
			}
		}

		private void applyRenderSettings(int int1) {
			IsoGridSquare.rmod = this.rmod;
			IsoGridSquare.gmod = this.gmod;
			IsoGridSquare.bmod = this.bmod;
			IsoObject.rmod = this.rmod;
			IsoObject.gmod = this.gmod;
			IsoObject.bmod = this.bmod;
		}

		private void legacyPostRender(int int1) {
			SpriteRenderer.instance.glIgnoreStyles(true);
			if (this.applyNightVisionGoggles) {
				IndieGL.glBlendFunc(770, 768);
				SpriteRenderer.instance.render(RenderSettings.texture, 0.0F, 0.0F, (float)Core.getInstance().getOffscreenWidth(int1), (float)Core.getInstance().getOffscreenHeight(int1), 0.05F, 0.95F, 0.05F, this.goggleMod, (Consumer)null);
				IndieGL.glBlendFunc(770, 771);
			} else {
				IndieGL.glBlendFunc(774, 774);
				SpriteRenderer.instance.render(RenderSettings.texture, 0.0F, 0.0F, (float)Core.getInstance().getOffscreenWidth(int1), (float)Core.getInstance().getOffscreenHeight(int1), this.blendInfo.r, this.blendInfo.g, this.blendInfo.b, 1.0F, (Consumer)null);
				IndieGL.glBlendFunc(770, 771);
			}

			SpriteRenderer.instance.glIgnoreStyles(false);
		}

		public Color getBlendColor() {
			return this.blendColor;
		}

		public float getBlendIntensity() {
			return this.blendIntensity;
		}

		public float getDesaturation() {
			return this.desaturation;
		}

		public float getDarkness() {
			return this.darkness;
		}

		public float getNight() {
			return this.night;
		}

		public float getViewDistance() {
			return this.viewDistance;
		}

		public float getAmbient() {
			return this.ambient;
		}

		public boolean isApplyNightVisionGoggles() {
			return this.applyNightVisionGoggles;
		}

		public float getRmod() {
			return this.rmod;
		}

		public float getGmod() {
			return this.gmod;
		}

		public float getBmod() {
			return this.bmod;
		}

		public boolean isExterior() {
			return this.isExterior;
		}

		public float getFogMod() {
			return this.fogMod;
		}

		private void setBlendColor(Color color) {
			this.blendColor.a = color.a;
			this.blendColor.r = color.r;
			this.blendColor.g = color.g;
			this.blendColor.b = color.b;
		}

		public Color getMaskClearColor() {
			return this.maskClearColor;
		}
	}
}
