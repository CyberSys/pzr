package zombie.vehicles;


public class LightbarLightsMode {
	private long startTime = 0L;
	private int light = 0;
	private final int modeMax = 3;
	private int mode = 0;

	public int get() {
		return this.mode;
	}

	public void set(int int1) {
		if (int1 > 3) {
			this.mode = 3;
		} else if (int1 < 0) {
			this.mode = 0;
		} else {
			this.mode = int1;
			if (this.mode != 0) {
				this.start();
			}
		}
	}

	public void start() {
		this.startTime = System.currentTimeMillis();
	}

	public void update() {
		long long1 = System.currentTimeMillis() - this.startTime;
		switch (this.mode) {
		case 1: 
			long1 %= 1000L;
			if (long1 < 50L) {
				this.light = 0;
			} else if (long1 < 450L) {
				this.light = 1;
			} else if (long1 < 550L) {
				this.light = 0;
			} else if (long1 < 950L) {
				this.light = 2;
			} else {
				this.light = 0;
			}

			break;
		
		case 2: 
			long1 %= 1000L;
			if (long1 < 50L) {
				this.light = 0;
			} else if (long1 < 250L) {
				this.light = 1;
			} else if (long1 < 300L) {
				this.light = 0;
			} else if (long1 < 500L) {
				this.light = 1;
			} else if (long1 < 550L) {
				this.light = 0;
			} else if (long1 < 750L) {
				this.light = 2;
			} else if (long1 < 800L) {
				this.light = 0;
			} else {
				this.light = 2;
			}

			break;
		
		case 3: 
			long1 %= 300L;
			if (long1 < 25L) {
				this.light = 0;
			} else if (long1 < 125L) {
				this.light = 1;
			} else if (long1 < 175L) {
				this.light = 0;
			} else if (long1 < 275L) {
				this.light = 2;
			} else {
				this.light = 0;
			}

			break;
		
		default: 
			this.light = 0;
		
		}
	}

	public int getLightTexIndex() {
		return this.light;
	}

	public boolean isEnable() {
		return this.mode != 0;
	}
}
