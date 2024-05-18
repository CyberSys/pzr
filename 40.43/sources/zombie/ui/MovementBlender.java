package zombie.ui;

import zombie.iso.Vector2;


public class MovementBlender extends UIElement {
	public double sx = 0.0;
	public double sy = 0.0;
	public double Time = 0.0;
	public double TimeMax = 0.0;
	public double tx = 0.0;
	public double ty = 0.0;
	float lamount = -100.0F;

	public MovementBlender(UIElement uIElement) {
		this.x = uIElement.x;
		this.y = uIElement.y;
		this.sx = this.x;
		this.sy = this.y;
		this.tx = this.x;
		this.ty = this.y;
		uIElement.x = 0.0;
		uIElement.y = 0.0;
		this.width = uIElement.width;
		this.height = uIElement.height;
		this.AddChild(uIElement);
	}

	public void MoveTo(float float1, float float2, float float3) {
		if (this.tx != (double)float1 || this.ty != (double)float2) {
			this.TimeMax = (double)(float3 * 30.0F);
			this.Time = 0.0;
			this.sx = this.getX();
			this.sy = this.getY();
			this.tx = (double)float1;
			this.ty = (double)float2;
		}
	}

	public boolean Running() {
		double double1 = this.Time / this.TimeMax;
		return !(double1 > 1.0);
	}

	public void update() {
		super.update();
		++this.Time;
		double double1 = this.Time / this.TimeMax;
		if (double1 > 1.0) {
			double1 = 1.0;
		}

		if (double1 != 1.0 || this.lamount != 1.0F) {
			this.lamount = (float)double1;
			Vector2 vector2 = new Vector2();
			Vector2 vector22 = new Vector2();
			Vector2 vector23 = new Vector2();
			vector22.x = (float)this.sx;
			vector22.y = (float)this.sy;
			vector23.x = (float)this.tx;
			vector23.y = (float)this.ty;
			double1 = double1 > 1.0 ? 1.0 : (double1 < 0.0 ? 0.0 : double1);
			double1 = double1 * double1 * (3.0 - 2.0 * double1);
			vector2.x = (float)((double)vector22.x + (double)(vector23.x - vector22.x) * double1);
			vector2.y = (float)((double)vector22.y + (double)(vector23.y - vector22.y) * double1);
			this.setX((double)vector2.x);
			this.setY((double)vector2.y);
		}
	}
}
