package zombie.iso.sprite.shapers;

import javax.xml.bind.annotation.XmlType;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;


public class FloorShaperAttachedSprites extends FloorShaper {
	public static final FloorShaperAttachedSprites instance = new FloorShaperAttachedSprites();

	public void accept(TextureDraw textureDraw) {
		super.accept(textureDraw);
		this.applyAttachedSpritesPadding(textureDraw);
	}

	private void applyAttachedSpritesPadding(TextureDraw textureDraw) {
		if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.IsoPaddingAttached.getValue()) {
			FloorShaperAttachedSprites.Settings settings = this.getSettings();
			FloorShaperAttachedSprites.Settings.ASBorderSetting aSBorderSetting = settings.getCurrentZoomSetting();
			float float1 = aSBorderSetting.borderThicknessUp;
			float float2 = aSBorderSetting.borderThicknessDown;
			float float3 = aSBorderSetting.borderThicknessLR;
			float float4 = aSBorderSetting.uvFraction;
			float float5 = textureDraw.x1 - textureDraw.x0;
			float float6 = textureDraw.y2 - textureDraw.y1;
			float float7 = textureDraw.u1 - textureDraw.u0;
			float float8 = textureDraw.v2 - textureDraw.v1;
			float float9 = float7 * float3 / float5;
			float float10 = float8 * float1 / float6;
			float float11 = float8 * float2 / float6;
			float float12 = float4 * float9;
			float float13 = float4 * float10;
			float float14 = float4 * float11;
			SpritePadding.applyPadding(textureDraw, float3, float1, float3, float2, float12, float13, float12, float14);
		}
	}

	private FloorShaperAttachedSprites.Settings getSettings() {
		return SpritePaddingSettings.getSettings().AttachedSprites;
	}

	@XmlType(name = "FloorShaperAttachedSpritesSettings")
	public static class Settings extends SpritePaddingSettings.GenericZoomBasedSettingGroup {
		public FloorShaperAttachedSprites.Settings.ASBorderSetting ZoomedIn = new FloorShaperAttachedSprites.Settings.ASBorderSetting(2.0F, 1.0F, 3.0F, 0.01F);
		public FloorShaperAttachedSprites.Settings.ASBorderSetting NotZoomed = new FloorShaperAttachedSprites.Settings.ASBorderSetting(2.0F, 1.0F, 3.0F, 0.01F);
		public FloorShaperAttachedSprites.Settings.ASBorderSetting ZoomedOut = new FloorShaperAttachedSprites.Settings.ASBorderSetting(2.0F, 0.0F, 2.5F, 0.0F);

		public FloorShaperAttachedSprites.Settings.ASBorderSetting getCurrentZoomSetting() {
			return (FloorShaperAttachedSprites.Settings.ASBorderSetting)getCurrentZoomSetting(this.ZoomedIn, this.NotZoomed, this.ZoomedOut);
		}

		public static class ASBorderSetting {
			public float borderThicknessUp;
			public float borderThicknessDown;
			public float borderThicknessLR;
			public float uvFraction;

			public ASBorderSetting() {
			}

			public ASBorderSetting(float float1, float float2, float float3, float float4) {
				this.borderThicknessUp = float1;
				this.borderThicknessDown = float2;
				this.borderThicknessLR = float3;
				this.uvFraction = float4;
			}
		}
	}
}
