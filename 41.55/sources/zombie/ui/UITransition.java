package zombie.ui;


public final class UITransition {
	private float duration = 100.0F;
	private float elapsed;
	private float frac;
	private boolean fadeOut;
	private boolean bIgnoreUpdateTime = false;
	private long updateTimeMS;
	private static long currentTimeMS;
	private static long elapsedTimeMS;

	public static void UpdateAll() {
		long long1 = System.currentTimeMillis();
		elapsedTimeMS = long1 - currentTimeMS;
		currentTimeMS = long1;
	}

	public void init(float float1, boolean boolean1) {
		this.duration = Math.max(float1, 1.0F);
		if (this.frac >= 1.0F) {
			this.elapsed = 0.0F;
		} else if (this.fadeOut != boolean1) {
			this.elapsed = (1.0F - this.frac) * this.duration;
		} else {
			this.elapsed = this.frac * this.duration;
		}

		this.fadeOut = boolean1;
	}

	public void update() {
		if (!this.bIgnoreUpdateTime && this.updateTimeMS != 0L) {
			long long1 = (long)this.duration;
			if (this.updateTimeMS + long1 < currentTimeMS) {
				this.elapsed = this.duration;
			}
		}

		this.updateTimeMS = currentTimeMS;
		this.frac = this.elapsed / this.duration;
		this.elapsed = Math.min(this.elapsed + (float)elapsedTimeMS, this.duration);
	}

	public float fraction() {
		return this.fadeOut ? 1.0F - this.frac : this.frac;
	}

	public void setFadeIn(boolean boolean1) {
		if (boolean1) {
			if (this.fadeOut) {
				this.init(100.0F, false);
			}
		} else if (!this.fadeOut) {
			this.init(200.0F, true);
		}
	}

	public void reset() {
		this.elapsed = 0.0F;
	}

	public void setIgnoreUpdateTime(boolean boolean1) {
		this.bIgnoreUpdateTime = boolean1;
	}

	public float getElapsed() {
		return this.elapsed;
	}

	public void setElapsed(float float1) {
		this.elapsed = float1;
	}
}
