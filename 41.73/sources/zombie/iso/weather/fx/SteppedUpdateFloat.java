package zombie.iso.weather.fx;


public class SteppedUpdateFloat {
	private float current;
	private float step;
	private float target;
	private float min;
	private float max;

	public SteppedUpdateFloat(float float1, float float2, float float3, float float4) {
		this.current = float1;
		this.step = float2;
		this.target = float1;
		this.min = float3;
		this.max = float4;
	}

	public float value() {
		return this.current;
	}

	public void setTarget(float float1) {
		this.target = this.clamp(this.min, this.max, float1);
	}

	public float getTarget() {
		return this.target;
	}

	public void overrideCurrentValue(float float1) {
		this.current = float1;
	}

	private float clamp(float float1, float float2, float float3) {
		float3 = Math.min(float2, float3);
		float3 = Math.max(float1, float3);
		return float3;
	}

	public void update(float float1) {
		if (this.current != this.target) {
			if (this.target > this.current) {
				this.current += this.step * float1;
				if (this.current > this.target) {
					this.current = this.target;
				}
			} else if (this.target < this.current) {
				this.current -= this.step * float1;
				if (this.current < this.target) {
					this.current = this.target;
				}
			}
		}
	}
}
