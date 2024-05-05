package org.lwjglx.opengl;

import org.lwjglx.Sys;


class Sync {
	private static final long NANOS_IN_SECOND = 1000000000L;
	private static long nextFrame = 0L;
	private static boolean initialised = false;
	private static Sync.RunningAvg sleepDurations = new Sync.RunningAvg(10);
	private static Sync.RunningAvg yieldDurations = new Sync.RunningAvg(10);

	public static void sync(int int1) {
		if (int1 > 0) {
			if (!initialised) {
				initialise();
			}

			try {
				long long1;
				long long2;
				for (long1 = getTime(); nextFrame - long1 > sleepDurations.avg(); long1 = long2) {
					Thread.sleep(1L);
					sleepDurations.add((long2 = getTime()) - long1);
				}

				sleepDurations.dampenForLowResTicker();
				for (long1 = getTime(); nextFrame - long1 > yieldDurations.avg(); long1 = long2) {
					Thread.yield();
					yieldDurations.add((long2 = getTime()) - long1);
				}
			} catch (InterruptedException interruptedException) {
			}

			nextFrame = Math.max(nextFrame + 1000000000L / (long)int1, getTime());
		}
	}

	private static void initialise() {
		initialised = true;
		sleepDurations.init(1000000L);
		yieldDurations.init((long)((int)((double)(-(getTime() - getTime())) * 1.333)));
		nextFrame = getTime();
		String string = System.getProperty("os.name");
		if (string.startsWith("Win")) {
			Thread thread = new Thread(new Runnable(){
				
				public void run() {
					try {
						Thread.sleep(Long.MAX_VALUE);
					} catch (Exception var2) {
					}
				}
			});

			thread.setName("LWJGL Timer");
			thread.setDaemon(true);
			thread.start();
		}
	}

	private static long getTime() {
		return Sys.getTime() * 1000000000L / Sys.getTimerResolution();
	}

	private static class RunningAvg {
		private final long[] slots;
		private int offset;
		private static final long DAMPEN_THRESHOLD = 10000000L;
		private static final float DAMPEN_FACTOR = 0.9F;

		public RunningAvg(int int1) {
			this.slots = new long[int1];
			this.offset = 0;
		}

		public void init(long long1) {
			while (this.offset < this.slots.length) {
				this.slots[this.offset++] = long1;
			}
		}

		public void add(long long1) {
			this.slots[this.offset++ % this.slots.length] = long1;
			this.offset %= this.slots.length;
		}

		public long avg() {
			long long1 = 0L;
			for (int int1 = 0; int1 < this.slots.length; ++int1) {
				long1 += this.slots[int1];
			}

			return long1 / (long)this.slots.length;
		}

		public void dampenForLowResTicker() {
			if (this.avg() > 10000000L) {
				for (int int1 = 0; int1 < this.slots.length; ++int1) {
					long[] longArray = this.slots;
					longArray[int1] = (long)((float)longArray[int1] * 0.9F);
				}
			}
		}
	}
}
