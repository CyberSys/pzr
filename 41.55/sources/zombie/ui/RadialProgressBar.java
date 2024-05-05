package zombie.ui;

import se.krka.kahlua.vm.KahluaTable;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.iso.Vector2;


public final class RadialProgressBar extends UIElement {
	private static final boolean DEBUG = false;
	Texture radialTexture;
	float deltaValue = 1.0F;
	private static final RadialProgressBar.RadSegment[] segments = new RadialProgressBar.RadSegment[8];
	private final float PIx2 = 6.283185F;
	private final float PiOver2 = 1.570796F;

	public RadialProgressBar(KahluaTable kahluaTable, Texture texture) {
		super(kahluaTable);
		this.radialTexture = texture;
	}

	public void update() {
		super.update();
	}

	public void render() {
		if (this.enabled) {
			if (this.isVisible()) {
				if (this.Parent == null || this.Parent.maxDrawHeight == -1 || !((double)this.Parent.maxDrawHeight <= this.y)) {
					if (this.radialTexture != null) {
						float float1 = (float)(this.x + this.xScroll + this.getAbsoluteX());
						float float2 = (float)(this.y + this.yScroll + this.getAbsoluteY());
						float float3 = this.radialTexture.xStart;
						float float4 = this.radialTexture.yStart;
						float float5 = this.radialTexture.xEnd - this.radialTexture.xStart;
						float float6 = this.radialTexture.yEnd - this.radialTexture.yStart;
						float float7 = float1 + 0.5F * this.width;
						float float8 = float2 + 0.5F * this.height;
						float float9 = this.deltaValue;
						float float10 = float9 * 6.283185F - 1.570796F;
						Vector2 vector2 = new Vector2((float)Math.cos((double)float10), (float)Math.sin((double)float10));
						float float11;
						float float12;
						if (Math.abs(this.width / 2.0F / vector2.x) < Math.abs(this.height / 2.0F / vector2.y)) {
							float11 = Math.abs(this.width / 2.0F / vector2.x);
							float12 = Math.abs(0.5F / vector2.x);
						} else {
							float11 = Math.abs(this.height / 2.0F / vector2.y);
							float12 = Math.abs(0.5F / vector2.y);
						}

						float float13 = float7 + vector2.x * float11;
						float float14 = float8 + vector2.y * float11;
						float float15 = 0.5F + vector2.x * float12;
						float float16 = 0.5F + vector2.y * float12;
						int int1 = (int)(float9 * 8.0F);
						if (float9 <= 0.0F) {
							int1 = -1;
						}

						for (int int2 = 0; int2 < segments.length; ++int2) {
							RadialProgressBar.RadSegment radSegment = segments[int2];
							if (radSegment != null && int2 <= int1) {
								if (int2 != int1) {
									SpriteRenderer.instance.renderPoly(this.radialTexture, float1 + radSegment.vertex[0].x * this.width, float2 + radSegment.vertex[0].y * this.height, float1 + radSegment.vertex[1].x * this.width, float2 + radSegment.vertex[1].y * this.height, float1 + radSegment.vertex[2].x * this.width, float2 + radSegment.vertex[2].y * this.height, float1 + radSegment.vertex[2].x * this.width, float2 + radSegment.vertex[2].y * this.height, 1.0F, 1.0F, 1.0F, 1.0F, float3 + radSegment.uv[0].x * float5, float4 + radSegment.uv[0].y * float6, float3 + radSegment.uv[1].x * float5, float4 + radSegment.uv[1].y * float6, float3 + radSegment.uv[2].x * float5, float4 + radSegment.uv[2].y * float6, float3 + radSegment.uv[2].x * float5, float4 + radSegment.uv[2].y * float6);
								} else {
									SpriteRenderer.instance.renderPoly(this.radialTexture, float1 + radSegment.vertex[0].x * this.width, float2 + radSegment.vertex[0].y * this.height, float13, float14, float1 + radSegment.vertex[2].x * this.width, float2 + radSegment.vertex[2].y * this.height, float1 + radSegment.vertex[2].x * this.width, float2 + radSegment.vertex[2].y * this.height, 1.0F, 1.0F, 1.0F, 1.0F, float3 + radSegment.uv[0].x * float5, float4 + radSegment.uv[0].y * float6, float3 + float15 * float5, float4 + float16 * float6, float3 + radSegment.uv[2].x * float5, float4 + radSegment.uv[2].y * float6, float3 + radSegment.uv[2].x * float5, float4 + radSegment.uv[2].y * float6);
								}
							}
						}
					}
				}
			}
		}
	}

	public void setValue(float float1) {
		this.deltaValue = PZMath.clamp(float1, 0.0F, 1.0F);
	}

	public float getValue() {
		return this.deltaValue;
	}

	public void setTexture(Texture texture) {
		this.radialTexture = texture;
	}

	public Texture getTexture() {
		return this.radialTexture;
	}

	static  {
		segments[0] = new RadialProgressBar.RadSegment();
		segments[0].set(0.5F, 0.0F, 1.0F, 0.0F, 0.5F, 0.5F);
		segments[1] = new RadialProgressBar.RadSegment();
		segments[1].set(1.0F, 0.0F, 1.0F, 0.5F, 0.5F, 0.5F);
		segments[2] = new RadialProgressBar.RadSegment();
		segments[2].set(1.0F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F);
		segments[3] = new RadialProgressBar.RadSegment();
		segments[3].set(1.0F, 1.0F, 0.5F, 1.0F, 0.5F, 0.5F);
		segments[4] = new RadialProgressBar.RadSegment();
		segments[4].set(0.5F, 1.0F, 0.0F, 1.0F, 0.5F, 0.5F);
		segments[5] = new RadialProgressBar.RadSegment();
		segments[5].set(0.0F, 1.0F, 0.0F, 0.5F, 0.5F, 0.5F);
		segments[6] = new RadialProgressBar.RadSegment();
		segments[6].set(0.0F, 0.5F, 0.0F, 0.0F, 0.5F, 0.5F);
		segments[7] = new RadialProgressBar.RadSegment();
		segments[7].set(0.0F, 0.0F, 0.5F, 0.0F, 0.5F, 0.5F);
	}

	private static class RadSegment {
		Vector2[] vertex = new Vector2[3];
		Vector2[] uv = new Vector2[3];

		private RadialProgressBar.RadSegment set(int int1, float float1, float float2, float float3, float float4) {
			this.vertex[int1] = new Vector2(float1, float2);
			this.uv[int1] = new Vector2(float3, float4);
			return this;
		}

		private void set(float float1, float float2, float float3, float float4, float float5, float float6) {
			this.vertex[0] = new Vector2(float1, float2);
			this.vertex[1] = new Vector2(float3, float4);
			this.vertex[2] = new Vector2(float5, float6);
			this.uv[0] = new Vector2(float1, float2);
			this.uv[1] = new Vector2(float3, float4);
			this.uv[2] = new Vector2(float5, float6);
		}
	}
}
