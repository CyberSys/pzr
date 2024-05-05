package zombie.iso.weather.fx;

import java.util.function.Consumer;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.iso.Vector2;


public class RainParticle extends WeatherParticle {
	private double angleRadians = 0.0;
	private float lastAngle = -1.0F;
	private float lastIntensity = -1.0F;
	protected float angleOffset = 0.0F;
	private float alphaMod = 0.0F;
	private float incarnateAlpha = 1.0F;
	private float life = 0.0F;
	private RainParticle.RenderPoints rp;
	private boolean angleUpdate = false;
	private float tmpAngle = 0.0F;

	public RainParticle(Texture texture, int int1) {
		super(texture);
		if (int1 > 6) {
			this.bounds.setSize(Rand.Next(1, 2), int1);
		} else {
			this.bounds.setSize(1, int1);
		}

		this.oWidth = (float)this.bounds.getWidth();
		this.oHeight = (float)this.bounds.getHeight();
		this.recalcSizeOnZoom = true;
		this.zoomMultiW = 0.0F;
		this.zoomMultiH = 2.0F;
		this.setLife();
		this.rp = new RainParticle.RenderPoints();
		this.rp.setDimensions(this.oWidth, this.oHeight);
	}

	protected void setLife() {
		this.life = (float)Rand.Next(20, 60);
	}

	public void update(float float1) {
		this.angleUpdate = false;
		if (this.updateZoomSize()) {
			this.rp.setDimensions(this.oWidth, this.oHeight);
			this.angleUpdate = true;
		}

		if (this.angleUpdate || this.lastAngle != IsoWeatherFX.instance.windAngle || this.lastIntensity != IsoWeatherFX.instance.windPrecipIntensity.value()) {
			this.tmpAngle = IsoWeatherFX.instance.windAngle + (this.angleOffset - this.angleOffset * 0.5F * IsoWeatherFX.instance.windPrecipIntensity.value());
			if (this.tmpAngle > 360.0F) {
				this.tmpAngle -= 360.0F;
			}

			if (this.tmpAngle < 0.0F) {
				this.tmpAngle += 360.0F;
			}

			this.angleRadians = Math.toRadians((double)this.tmpAngle);
			this.velocity.set((float)Math.cos(this.angleRadians) * this.speed, (float)Math.sin(this.angleRadians) * this.speed);
			this.lastAngle = IsoWeatherFX.instance.windAngle;
			this.lastIntensity = IsoWeatherFX.instance.windPrecipIntensity.value();
			this.angleUpdate = true;
		}

		Vector2 vector2 = this.position;
		vector2.x += this.velocity.x * (1.0F + IsoWeatherFX.instance.windSpeed * 0.1F) * float1;
		vector2 = this.position;
		vector2.y += this.velocity.y * (1.0F + IsoWeatherFX.instance.windSpeed * 0.1F) * float1;
		--this.life;
		if (this.life < 0.0F) {
			this.setLife();
			this.incarnateAlpha = 0.0F;
			this.position.set((float)Rand.Next(0, this.parent.getWidth()), (float)Rand.Next(0, this.parent.getHeight()));
		}

		if (this.incarnateAlpha < 1.0F) {
			this.incarnateAlpha += 0.035F;
			if (this.incarnateAlpha > 1.0F) {
				this.incarnateAlpha = 1.0F;
			}
		}

		super.update(float1, false);
		this.bounds.setLocation((int)this.position.x, (int)this.position.y);
		if (this.angleUpdate) {
			this.tmpAngle += 90.0F;
			if (this.tmpAngle > 360.0F) {
				this.tmpAngle -= 360.0F;
			}

			if (this.tmpAngle < 0.0F) {
				this.tmpAngle += 360.0F;
			}

			this.angleRadians = Math.toRadians((double)this.tmpAngle);
			this.rp.rotate(this.angleRadians);
		}

		this.alphaMod = 1.0F - 0.2F * IsoWeatherFX.instance.windIntensity.value();
		this.renderAlpha = this.alpha * this.alphaMod * this.alphaFadeMod.value() * IsoWeatherFX.instance.indoorsAlphaMod.value() * this.incarnateAlpha;
		this.renderAlpha *= 0.55F;
		if (IsoWeatherFX.instance.playerIndoors) {
			this.renderAlpha *= 0.5F;
		}
	}

	public void render(float float1, float float2) {
		double double1 = (double)(float1 + (float)this.bounds.getX());
		double double2 = (double)(float2 + (float)this.bounds.getY());
		SpriteRenderer.instance.render(this.texture, double1 + this.rp.getX(0), double2 + this.rp.getY(0), double1 + this.rp.getX(1), double2 + this.rp.getY(1), double1 + this.rp.getX(2), double2 + this.rp.getY(2), double1 + this.rp.getX(3), double2 + this.rp.getY(3), this.color.r, this.color.g, this.color.b, this.renderAlpha, (Consumer)null);
	}

	private class RenderPoints {
		RainParticle.Point[] points = new RainParticle.Point[4];
		RainParticle.Point center = RainParticle.this.new Point();
		RainParticle.Point dim = RainParticle.this.new Point();

		public RenderPoints() {
			for (int int1 = 0; int1 < this.points.length; ++int1) {
				this.points[int1] = RainParticle.this.new Point();
			}
		}

		public double getX(int int1) {
			return this.points[int1].x;
		}

		public double getY(int int1) {
			return this.points[int1].y;
		}

		public void setCenter(float float1, float float2) {
			this.center.set((double)float1, (double)float2);
		}

		public void setDimensions(float float1, float float2) {
			this.dim.set((double)float1, (double)float2);
			this.points[0].setOrig((double)(-float1 / 2.0F), (double)(-float2 / 2.0F));
			this.points[1].setOrig((double)(float1 / 2.0F), (double)(-float2 / 2.0F));
			this.points[2].setOrig((double)(float1 / 2.0F), (double)(float2 / 2.0F));
			this.points[3].setOrig((double)(-float1 / 2.0F), (double)(float2 / 2.0F));
		}

		public void rotate(double double1) {
			double double2 = Math.cos(double1);
			double double3 = Math.sin(double1);
			for (int int1 = 0; int1 < this.points.length; ++int1) {
				this.points[int1].x = this.points[int1].origx * double2 - this.points[int1].origy * double3;
				this.points[int1].y = this.points[int1].origx * double3 + this.points[int1].origy * double2;
			}
		}
	}

	private class Point {
		private double origx;
		private double origy;
		private double x;
		private double y;

		public void setOrig(double double1, double double2) {
			this.origx = double1;
			this.origy = double2;
			this.x = double1;
			this.y = double2;
		}

		public void set(double double1, double double2) {
			this.x = double1;
			this.y = double2;
		}
	}
}
