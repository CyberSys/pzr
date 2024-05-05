package zombie.iso.sprite.shapers;

import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;


public class SpritePadding {

	public static void applyPadding(TextureDraw textureDraw, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		float float9 = textureDraw.x0;
		float float10 = textureDraw.y0;
		float float11 = textureDraw.x1;
		float float12 = textureDraw.y1;
		float float13 = textureDraw.x2;
		float float14 = textureDraw.y2;
		float float15 = textureDraw.x3;
		float float16 = textureDraw.y3;
		float float17 = textureDraw.u0;
		float float18 = textureDraw.v0;
		float float19 = textureDraw.u1;
		float float20 = textureDraw.v1;
		float float21 = textureDraw.u2;
		float float22 = textureDraw.v2;
		float float23 = textureDraw.u3;
		float float24 = textureDraw.v3;
		textureDraw.x0 = float9 - float1;
		textureDraw.y0 = float10 - float2;
		textureDraw.u0 = float17 - float5;
		textureDraw.v0 = float18 - float6;
		textureDraw.x1 = float11 + float3;
		textureDraw.y1 = float12 - float2;
		textureDraw.u1 = float19 + float7;
		textureDraw.v1 = float20 - float6;
		textureDraw.x2 = float13 + float3;
		textureDraw.y2 = float14 + float4;
		textureDraw.u2 = float21 + float7;
		textureDraw.v2 = float22 + float8;
		textureDraw.x3 = float15 - float1;
		textureDraw.y3 = float16 + float4;
		textureDraw.u3 = float23 - float5;
		textureDraw.v3 = float24 + float8;
	}

	public static void applyPaddingBorder(TextureDraw textureDraw, float float1, float float2) {
		float float3 = textureDraw.x1 - textureDraw.x0;
		float float4 = textureDraw.y2 - textureDraw.y1;
		float float5 = textureDraw.u1 - textureDraw.u0;
		float float6 = textureDraw.v2 - textureDraw.v1;
		float float7 = float5 * float1 / float3;
		float float8 = float6 * float1 / float4;
		float float9 = float2 * float7;
		float float10 = float2 * float8;
		applyPadding(textureDraw, float1, float1, float1, float1, float9, float10, float9, float10);
	}

	public static void applyIsoPadding(TextureDraw textureDraw, SpritePadding.IsoPaddingSettings paddingSettings) {
		if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.IsoPadding.getValue()) {
			SpritePadding.IsoPaddingSettings.IsoBorderSetting borderSetting = paddingSettings.getCurrentZoomSetting();
			float float1 = borderSetting.borderThickness;
			float float2 = borderSetting.uvFraction;
			applyPaddingBorder(textureDraw, float1, float2);
		}
	}

	public static class IsoPaddingSettings extends SpritePaddingSettings.GenericZoomBasedSettingGroup {
		public SpritePadding.IsoPaddingSettings.IsoBorderSetting ZoomedIn = new SpritePadding.IsoPaddingSettings.IsoBorderSetting(1.0F, 0.99F);
		public SpritePadding.IsoPaddingSettings.IsoBorderSetting NotZoomed = new SpritePadding.IsoPaddingSettings.IsoBorderSetting(1.0F, 0.99F);
		public SpritePadding.IsoPaddingSettings.IsoBorderSetting ZoomedOut = new SpritePadding.IsoPaddingSettings.IsoBorderSetting(2.0F, 0.01F);

		public SpritePadding.IsoPaddingSettings.IsoBorderSetting getCurrentZoomSetting() {
			return (SpritePadding.IsoPaddingSettings.IsoBorderSetting)getCurrentZoomSetting(this.ZoomedIn, this.NotZoomed, this.ZoomedOut);
		}

		public static class IsoBorderSetting {
			public float borderThickness;
			public float uvFraction;

			public IsoBorderSetting() {
			}

			public IsoBorderSetting(float float1, float float2) {
				this.borderThickness = float1;
				this.uvFraction = float2;
			}
		}
	}
}
