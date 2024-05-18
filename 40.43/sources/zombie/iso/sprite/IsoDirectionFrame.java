package zombie.iso.sprite;

import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.iso.IsoDirections;
import zombie.iso.objects.ObjectRenderEffects;


public class IsoDirectionFrame {
	public Texture[] directions = new Texture[8];
	boolean bDoFlip = true;

	public IsoDirectionFrame(Texture texture) {
		this.SetAllDirections(texture);
	}

	public IsoDirectionFrame() {
	}

	public IsoDirectionFrame(Texture texture, Texture texture2, Texture texture3, Texture texture4, Texture texture5) {
		this.directions[0] = texture2;
		this.directions[1] = texture;
		this.directions[2] = texture2;
		this.directions[3] = texture3;
		this.directions[4] = texture4;
		this.directions[5] = texture5;
		this.directions[6] = texture4;
		this.directions[7] = texture3;
	}

	public IsoDirectionFrame(Texture texture, Texture texture2, Texture texture3, Texture texture4, Texture texture5, Texture texture6, Texture texture7, Texture texture8) {
		if (texture5 == null) {
			boolean boolean1 = false;
		}

		this.directions[0] = texture;
		this.directions[1] = texture8;
		this.directions[2] = texture7;
		this.directions[3] = texture6;
		this.directions[4] = texture5;
		this.directions[5] = texture4;
		this.directions[6] = texture3;
		this.directions[7] = texture2;
		this.bDoFlip = false;
	}

	public IsoDirectionFrame(Texture texture, Texture texture2, Texture texture3, Texture texture4) {
		this.directions[0] = texture;
		this.directions[1] = texture;
		this.directions[2] = texture4;
		this.directions[3] = texture4;
		this.directions[4] = texture2;
		this.directions[5] = texture2;
		this.directions[6] = texture3;
		this.directions[7] = texture3;
		this.bDoFlip = false;
	}

	public Texture getTexture(IsoDirections directions) {
		Texture texture = this.directions[directions.index()];
		return texture;
	}

	public void SetAllDirections(Texture texture) {
		this.directions[0] = texture;
		this.directions[1] = texture;
		this.directions[2] = texture;
		this.directions[3] = texture;
		this.directions[4] = texture;
		this.directions[5] = texture;
		this.directions[6] = texture;
		this.directions[7] = texture;
	}

	public void SetDirection(Texture texture, IsoDirections directions) {
		this.directions[directions.index()] = texture;
	}

	void render(float float1, float float2, IsoDirections directions, ColorInfo colorInfo, boolean boolean1) {
		Texture texture = this.directions[directions.index()];
		if (texture != null) {
			if (directions == IsoDirections.W || directions == IsoDirections.SW || directions == IsoDirections.S) {
				texture.flip = true;
			}

			if (boolean1) {
				texture.flip = !texture.flip;
			}

			if (texture != null) {
				if (!this.bDoFlip) {
					texture.flip = false;
				}

				texture.renderstrip((int)float1, (int)float2, texture.getWidth(), texture.getHeight(), colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a);
				texture.flip = false;
			}
		}
	}

	public void render(float float1, float float2, IsoDirections directions, ColorInfo colorInfo, boolean boolean1, float float3) {
		Texture texture = this.directions[directions.index()];
		if (texture != null) {
			if (boolean1) {
				texture.flip = !texture.flip;
			}

			if (texture != null) {
				if (!this.bDoFlip) {
					texture.flip = false;
				}

				texture.render((int)float1, (int)float2, (int)((float)texture.getWidth()), (int)((float)texture.getHeight()), colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a);
				texture.flip = false;
			}
		}
	}

	void render(float float1, float float2, float float3, float float4, IsoDirections directions, ColorInfo colorInfo, boolean boolean1, float float5) {
		Texture texture = this.directions[directions.index()];
		if (texture != null) {
			if (boolean1) {
				texture.flip = !texture.flip;
			}

			if (!this.bDoFlip) {
				texture.flip = false;
			}

			texture.render((int)float1, (int)float2, (int)float3, (int)float4, colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a);
			texture.flip = false;
		}
	}

	void render(ObjectRenderEffects objectRenderEffects, float float1, float float2, float float3, float float4, IsoDirections directions, ColorInfo colorInfo, boolean boolean1, float float5) {
		Texture texture = this.directions[directions.index()];
		if (texture != null) {
			if (boolean1) {
				texture.flip = !texture.flip;
			}

			if (!this.bDoFlip) {
				texture.flip = false;
			}

			texture.render(objectRenderEffects, (int)float1, (int)float2, (int)float3, (int)float4, colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a);
			texture.flip = false;
		}
	}

	void render(ObjectRenderEffects objectRenderEffects, float float1, float float2, IsoDirections directions, ColorInfo colorInfo, boolean boolean1, float float3) {
		Texture texture = this.directions[directions.index()];
		if (texture != null) {
			if (boolean1) {
				texture.flip = !texture.flip;
			}

			if (!this.bDoFlip) {
				texture.flip = false;
			}

			texture.render(objectRenderEffects, (int)float1, (int)float2, texture.getWidth(), texture.getHeight(), colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a);
			texture.flip = false;
		}
	}

	void render(float float1, float float2, float float3, IsoDirections directions, ColorInfo colorInfo, boolean boolean1, float float4) {
		Texture texture = this.directions[directions.index()];
		if (texture != null) {
			if (boolean1) {
				texture.flip = !texture.flip;
			}

			if (!this.bDoFlip) {
				texture.flip = false;
			}

			texture.renderstrip((int)float1, (int)float2, (int)((float)texture.getWidth()), (int)((float)texture.getHeight()), colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a);
			texture.flip = false;
		}
	}

	public void renderexplicit(int int1, int int2, IsoDirections directions, float float1) {
		this.renderexplicit(int1, int2, directions, float1, (ColorInfo)null);
	}

	public void renderexplicit(int int1, int int2, IsoDirections directions, float float1, ColorInfo colorInfo) {
		Texture texture = this.directions[directions.index()];
		if (texture != null) {
			float float2 = 1.0F;
			float float3 = 1.0F;
			float float4 = 1.0F;
			float float5 = 1.0F;
			if (colorInfo != null) {
				float2 *= colorInfo.a;
				float3 *= colorInfo.r;
				float4 *= colorInfo.g;
				float5 *= colorInfo.b;
			}

			texture.renderstrip(int1, int2, (int)((float)texture.getWidth() * float1), (int)((float)texture.getHeight() * float1), float3, float4, float5, float2);
		}
	}
}
