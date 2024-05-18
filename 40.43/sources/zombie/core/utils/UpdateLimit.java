package zombie.core.utils;


public class UpdateLimit {
	private long delay;
	private long last;

	public UpdateLimit(long long1) {
		this.delay = long1;
		this.last = System.currentTimeMillis();
	}

	public void BlockCheck() {
		this.last = System.currentTimeMillis() + this.delay;
	}

	public boolean Check() {
		if (System.currentTimeMillis() - this.last > this.delay) {
			if (System.currentTimeMillis() - this.last > 3L * this.delay) {
				this.last = System.currentTimeMillis();
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

	public double getTimePeriod() {
		return ((double)System.currentTimeMillis() - (double)this.last) / (double)this.delay;
	}
}
