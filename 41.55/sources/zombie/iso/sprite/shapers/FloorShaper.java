package zombie.iso.sprite.shapers;

import java.util.function.Consumer;
import zombie.core.Color;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;


public class FloorShaper implements Consumer {
	protected final int[] col = new int[4];
	protected int colTint = 0;
	protected boolean isShore = false;
	protected final float[] waterDepth = new float[4];

	public void setVertColors(int int1, int int2, int int3, int int4) {
		this.col[0] = int1;
		this.col[1] = int2;
		this.col[2] = int3;
		this.col[3] = int4;
	}

	public void setAlpha4(float float1) {
		int int1 = (int)(float1 * 255.0F) & 255;
		this.col[0] = this.col[0] & 16777215 | int1 << 24;
		this.col[1] = this.col[1] & 16777215 | int1 << 24;
		this.col[2] = this.col[2] & 16777215 | int1 << 24;
		this.col[3] = this.col[3] & 16777215 | int1 << 24;
	}

	public void setShore(boolean boolean1) {
		this.isShore = boolean1;
	}

	public void setWaterDepth(float float1, float float2, float float3, float float4) {
		this.waterDepth[0] = float1;
		this.waterDepth[1] = float2;
		this.waterDepth[2] = float3;
		this.waterDepth[3] = float4;
	}

	public void setTintColor(int int1) {
		this.colTint = int1;
	}

	public void accept(TextureDraw textureDraw) {
		if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.Lighting.getValue()) {
			textureDraw.col0 = Color.blendBGR(textureDraw.col0, this.col[0]);
			textureDraw.col1 = Color.blendBGR(textureDraw.col1, this.col[1]);
			textureDraw.col2 = Color.blendBGR(textureDraw.col2, this.col[2]);
			textureDraw.col3 = Color.blendBGR(textureDraw.col3, this.col[3]);
		}

		if (this.isShore && DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.ShoreFade.getValue()) {
			textureDraw.col0 = Color.setAlphaChannelToABGR(textureDraw.col0, 1.0F - this.waterDepth[0]);
			textureDraw.col1 = Color.setAlphaChannelToABGR(textureDraw.col1, 1.0F - this.waterDepth[1]);
			textureDraw.col2 = Color.setAlphaChannelToABGR(textureDraw.col2, 1.0F - this.waterDepth[2]);
			textureDraw.col3 = Color.setAlphaChannelToABGR(textureDraw.col3, 1.0F - this.waterDepth[3]);
		}

		if (this.colTint != 0) {
			textureDraw.col0 = Color.tintABGR(textureDraw.col0, this.colTint);
			textureDraw.col1 = Color.tintABGR(textureDraw.col1, this.colTint);
			textureDraw.col2 = Color.tintABGR(textureDraw.col2, this.colTint);
			textureDraw.col3 = Color.tintABGR(textureDraw.col3, this.colTint);
		}

		SpritePadding.applyIsoPadding(textureDraw, this.getIsoPaddingSettings());
	}

	private SpritePadding.IsoPaddingSettings getIsoPaddingSettings() {
		return SpritePaddingSettings.getSettings().IsoPadding;
	}
}
