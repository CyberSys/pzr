package zombie.ui;

import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;


public final class VehicleGauge extends UIElement {
	protected int needleX;
	protected int needleY;
	protected float minAngle;
	protected float maxAngle;
	protected float value;
	protected Texture texture;
	protected int needleWidth = 45;

	public VehicleGauge(Texture texture, int int1, int int2, float float1, float float2) {
		this.texture = texture;
		this.needleX = int1;
		this.needleY = int2;
		this.minAngle = float1;
		this.maxAngle = float2;
		this.width = (float)texture.getWidth();
		this.height = (float)texture.getHeight();
	}

	public void setNeedleWidth(int int1) {
		this.needleWidth = int1;
	}

	public void render() {
		if (this.isVisible()) {
			super.render();
			this.DrawTexture(this.texture, 0.0, 0.0, 1.0);
			double double1 = this.minAngle < this.maxAngle ? Math.toRadians((double)(this.minAngle + (this.maxAngle - this.minAngle) * this.value)) : Math.toRadians((double)(this.maxAngle + (this.maxAngle - this.minAngle) * (1.0F - this.value)));
			double double2 = (double)this.needleX;
			double double3 = (double)this.needleY;
			double double4 = (double)this.needleX + (double)this.needleWidth * Math.cos(double1);
			double double5 = Math.ceil((double)this.needleY + (double)this.needleWidth * Math.sin(double1));
			int int1 = this.getAbsoluteX().intValue();
			int int2 = this.getAbsoluteY().intValue();
			SpriteRenderer.instance.renderline((Texture)null, int1 + (int)double2, int2 + (int)double3, int1 + (int)double4, int2 + (int)double5, 1.0F, 0.0F, 0.0F, 1.0F);
		}
	}

	public void setValue(float float1) {
		this.value = Math.min(float1, 1.0F);
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
}
