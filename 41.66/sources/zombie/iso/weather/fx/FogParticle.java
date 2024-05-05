package zombie.iso.weather.fx;

import zombie.core.textures.Texture;
import zombie.iso.Vector2;


public class FogParticle extends WeatherParticle {
	private double angleRadians = 0.0;
	private float lastAngle = -1.0F;
	private float lastIntensity = -1.0F;
	protected float angleOffset = 0.0F;
	private float alphaMod = 0.0F;
	private float tmpAngle = 0.0F;

	public FogParticle(Texture texture, int int1, int int2) {
		super(texture, int1, int2);
	}

	public void update(float float1) {
		if (this.lastAngle != IsoWeatherFX.instance.windAngle || this.lastIntensity != IsoWeatherFX.instance.windIntensity.value()) {
			this.tmpAngle = IsoWeatherFX.instance.windAngle + (this.angleOffset - this.angleOffset * 1.0F * IsoWeatherFX.instance.windIntensity.value());
			if (this.tmpAngle > 360.0F) {
				this.tmpAngle -= 360.0F;
			}

			if (this.tmpAngle < 0.0F) {
				this.tmpAngle += 360.0F;
			}

			this.angleRadians = Math.toRadians((double)this.tmpAngle);
			this.velocity.set((float)Math.cos(this.angleRadians) * this.speed, (float)Math.sin(this.angleRadians) * this.speed);
			this.lastAngle = IsoWeatherFX.instance.windAngle;
		}

		Vector2 vector2 = this.position;
		vector2.x += this.velocity.x * IsoWeatherFX.instance.windSpeedFog * float1;
		vector2 = this.position;
		vector2.y += this.velocity.y * IsoWeatherFX.instance.windSpeedFog * float1;
		super.update(float1);
		this.alphaMod = IsoWeatherFX.instance.fogIntensity.value();
		this.renderAlpha = this.alpha * this.alphaMod * this.alphaFadeMod.value() * IsoWeatherFX.instance.indoorsAlphaMod.value();
	}
}
