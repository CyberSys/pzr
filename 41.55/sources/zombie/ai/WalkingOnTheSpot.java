package zombie.ai;

import zombie.GameTime;
import zombie.iso.IsoUtils;


public final class WalkingOnTheSpot {
	private float x;
	private float y;
	private float time;

	public boolean check(float float1, float float2) {
		if (IsoUtils.DistanceToSquared(this.x, this.y, float1, float2) < 0.010000001F) {
			this.time += GameTime.getInstance().getMultiplier();
		} else {
			this.x = float1;
			this.y = float2;
			this.time = 0.0F;
		}

		return this.time > 400.0F;
	}

	public void reset(float float1, float float2) {
		this.x = float1;
		this.y = float2;
		this.time = 0.0F;
	}
}
