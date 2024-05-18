package zombie.iso.weather.fx;

import org.lwjgl.util.Rectangle;
import zombie.core.Color;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.iso.Vector2;


public abstract class WeatherParticle {
	protected ParticleRectangle parent;
	protected Rectangle bounds;
	protected Texture texture;
	protected Color color;
	protected Vector2 position;
	protected Vector2 velocity;
	protected float alpha;
	protected float speed;
	protected SteppedUpdateFloat alphaFadeMod;
	protected float renderAlpha;
	protected float oWidth;
	protected float oHeight;
	protected float zoomMultiW;
	protected float zoomMultiH;
	protected boolean recalcSizeOnZoom;
	protected float lastZoomMod;

	public WeatherParticle(Texture texture) {
		this.color = Color.white;
		this.position = new Vector2(0.0F, 0.0F);
		this.velocity = new Vector2(0.0F, 0.0F);
		this.alpha = 1.0F;
		this.speed = 0.0F;
		this.alphaFadeMod = new SteppedUpdateFloat(0.0F, 0.1F, 0.0F, 1.0F);
		this.renderAlpha = 0.0F;
		this.zoomMultiW = 0.0F;
		this.zoomMultiH = 0.0F;
		this.recalcSizeOnZoom = false;
		this.lastZoomMod = -1.0F;
		this.texture = texture;
		this.bounds = new Rectangle(0, 0, texture.getWidth(), texture.getHeight());
		this.oWidth = (float)this.bounds.getWidth();
		this.oHeight = (float)this.bounds.getHeight();
	}

	public WeatherParticle(Texture texture, int int1, int int2) {
		this.color = Color.white;
		this.position = new Vector2(0.0F, 0.0F);
		this.velocity = new Vector2(0.0F, 0.0F);
		this.alpha = 1.0F;
		this.speed = 0.0F;
		this.alphaFadeMod = new SteppedUpdateFloat(0.0F, 0.1F, 0.0F, 1.0F);
		this.renderAlpha = 0.0F;
		this.zoomMultiW = 0.0F;
		this.zoomMultiH = 0.0F;
		this.recalcSizeOnZoom = false;
		this.lastZoomMod = -1.0F;
		this.texture = texture;
		this.bounds = new Rectangle(0, 0, int1, int2);
		this.oWidth = (float)this.bounds.getWidth();
		this.oHeight = (float)this.bounds.getHeight();
	}

	protected void setParent(ParticleRectangle particleRectangle) {
		this.parent = particleRectangle;
	}

	public void update(float float1) {
		this.update(float1, true);
	}

	public void update(float float1, boolean boolean1) {
		this.alphaFadeMod.update(float1);
		Vector2 vector2;
		if (this.position.x > (float)this.parent.getWidth()) {
			vector2 = this.position;
			vector2.x -= (float)((int)(this.position.x / (float)this.parent.getWidth()) * this.parent.getWidth());
		} else if (this.position.x < 0.0F) {
			vector2 = this.position;
			vector2.x -= (float)((int)((this.position.x - (float)this.parent.getWidth()) / (float)this.parent.getWidth()) * this.parent.getWidth());
		}

		if (this.position.y > (float)this.parent.getHeight()) {
			vector2 = this.position;
			vector2.y -= (float)((int)(this.position.y / (float)this.parent.getHeight()) * this.parent.getHeight());
		} else if (this.position.y < 0.0F) {
			vector2 = this.position;
			vector2.y -= (float)((int)((this.position.y - (float)this.parent.getHeight()) / (float)this.parent.getHeight()) * this.parent.getHeight());
		}

		if (boolean1) {
			this.bounds.setLocation((int)this.position.x - this.bounds.getWidth() / 2, (int)this.position.y - this.bounds.getHeight() / 2);
		}
	}

	protected boolean updateZoomSize() {
		if (this.recalcSizeOnZoom && this.lastZoomMod != IsoWeatherFX.ZoomMod) {
			this.lastZoomMod = IsoWeatherFX.ZoomMod;
			this.oWidth = (float)this.bounds.getWidth();
			this.oHeight = (float)this.bounds.getHeight();
			if (this.lastZoomMod > 0.0F) {
				this.oWidth *= 1.0F + IsoWeatherFX.ZoomMod * this.zoomMultiW;
				this.oHeight *= 1.0F + IsoWeatherFX.ZoomMod * this.zoomMultiH;
			}

			return true;
		} else {
			return false;
		}
	}

	public void render(float float1, float float2) {
		SpriteRenderer.instance.render(this.texture, float1 + (float)this.bounds.getX(), float2 + (float)this.bounds.getY(), this.oWidth, this.oHeight, this.color.r, this.color.g, this.color.b, this.renderAlpha);
	}
}
