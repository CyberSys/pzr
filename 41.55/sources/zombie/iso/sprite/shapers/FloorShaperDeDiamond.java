package zombie.iso.sprite.shapers;

import javax.xml.bind.annotation.XmlType;
import zombie.core.Color;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;


public class FloorShaperDeDiamond extends FloorShaper {
	public static final FloorShaperDeDiamond instance = new FloorShaperDeDiamond();

	public void accept(TextureDraw textureDraw) {
		int int1 = this.colTint;
		this.colTint = 0;
		super.accept(textureDraw);
		this.applyDeDiamondPadding(textureDraw);
		if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.Lighting.getValue()) {
			int int2 = this.col[0];
			int int3 = this.col[1];
			int int4 = this.col[2];
			int int5 = this.col[3];
			int int6 = Color.lerpABGR(int2, int5, 0.5F);
			int int7 = Color.lerpABGR(int3, int2, 0.5F);
			int int8 = Color.lerpABGR(int4, int3, 0.5F);
			int int9 = Color.lerpABGR(int5, int4, 0.5F);
			textureDraw.col0 = Color.blendBGR(textureDraw.col0, int6);
			textureDraw.col1 = Color.blendBGR(textureDraw.col1, int7);
			textureDraw.col2 = Color.blendBGR(textureDraw.col2, int8);
			textureDraw.col3 = Color.blendBGR(textureDraw.col3, int9);
			if (int1 != 0) {
				textureDraw.col0 = Color.tintABGR(textureDraw.col0, int1);
				textureDraw.col1 = Color.tintABGR(textureDraw.col1, int1);
				textureDraw.col2 = Color.tintABGR(textureDraw.col2, int1);
				textureDraw.col3 = Color.tintABGR(textureDraw.col3, int1);
			}
		}
	}

	private void applyDeDiamondPadding(TextureDraw textureDraw) {
		if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.IsoPaddingDeDiamond.getValue()) {
			FloorShaperDeDiamond.Settings settings = this.getSettings();
			FloorShaperDeDiamond.Settings.BorderSetting borderSetting = settings.getCurrentZoomSetting();
			float float1 = borderSetting.borderThicknessUp;
			float float2 = borderSetting.borderThicknessDown;
			float float3 = borderSetting.borderThicknessLR;
			float float4 = borderSetting.uvFraction;
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

	private FloorShaperDeDiamond.Settings getSettings() {
		return SpritePaddingSettings.getSettings().FloorDeDiamond;
	}

	@XmlType(name = "FloorShaperDeDiamondSettings")
	public static class Settings extends SpritePaddingSettings.GenericZoomBasedSettingGroup {
		public FloorShaperDeDiamond.Settings.BorderSetting ZoomedIn = new FloorShaperDeDiamond.Settings.BorderSetting(2.0F, 1.0F, 2.0F, 0.01F);
		public FloorShaperDeDiamond.Settings.BorderSetting NotZoomed = new FloorShaperDeDiamond.Settings.BorderSetting(2.0F, 1.0F, 2.0F, 0.01F);
		public FloorShaperDeDiamond.Settings.BorderSetting ZoomedOut = new FloorShaperDeDiamond.Settings.BorderSetting(2.0F, 0.0F, 2.5F, 0.0F);

		public FloorShaperDeDiamond.Settings.BorderSetting getCurrentZoomSetting() {
			return (FloorShaperDeDiamond.Settings.BorderSetting)getCurrentZoomSetting(this.ZoomedIn, this.NotZoomed, this.ZoomedOut);
		}

		public static class BorderSetting {
			public float borderThicknessUp = 3.0F;
			public float borderThicknessDown = 3.0F;
			public float borderThicknessLR = 0.0F;
			public float uvFraction = 0.01F;

			public BorderSetting() {
			}

			public BorderSetting(float float1, float float2, float float3, float float4) {
				this.borderThicknessUp = float1;
				this.borderThicknessDown = float2;
				this.borderThicknessLR = float3;
				this.uvFraction = float4;
			}
		}
	}
}
