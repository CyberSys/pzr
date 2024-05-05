package zombie.core.utils;


public final class UpdateLimit {
	private long delay;
	private long last;
	private long lastPeriod;

	public UpdateLimit(long long1) {
		this.delay = long1;
		this.last = System.currentTimeMillis();
		this.lastPeriod = this.last;
	}

	public UpdateLimit(long long1, long long2) {
		this.delay = long1;
		this.last = System.currentTimeMillis() - long2;
		this.lastPeriod = this.last;
	}

	public void BlockCheck() {
		this.last = System.currentTimeMillis() + this.delay;
	}

	public void Reset(long long1) {
		this.delay = long1;
		this.last = System.currentTimeMillis();
		this.lastPeriod = System.currentTimeMillis();
	}

	public boolean Check() {
		long long1 = System.currentTimeMillis();
		if (long1 - this.last > this.delay) {
			if (long1 - this.last > 3L * this.delay) {
				this.last = long1;
			} else {
				this.last += this.delay;
			}

			return true;
		} else {
			return false;
		}
	}

	public long getLast() {
		return this.last;
	}

	public void updateTimePeriod() {
		long long1 = System.currentTimeMillis();
		if (long1 - this.last > this.delay) {
			if (long1 - this.last > 3L * this.delay) {
				this.last = long1;
			} else {
				this.last += this.delay;
			}
		}

		this.lastPeriod = long1;
	}

	public double getTimePeriod() {
		return Math.min(((double)System.currentTimeMillis() - (double)this.lastPeriod) / (double)this.delay, 1.0);
	}
}
