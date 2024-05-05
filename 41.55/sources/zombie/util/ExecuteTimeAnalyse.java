package zombie.util;

import java.io.PrintStream;


public class ExecuteTimeAnalyse {
	String caption;
	ExecuteTimeAnalyse.TimeStamp[] list;
	int listIndex = 0;

	public ExecuteTimeAnalyse(String string, int int1) {
		this.caption = string;
		this.list = new ExecuteTimeAnalyse.TimeStamp[int1];
		for (int int2 = 0; int2 < int1; ++int2) {
			this.list[int2] = new ExecuteTimeAnalyse.TimeStamp();
		}
	}

	public void reset() {
		this.listIndex = 0;
	}

	public void add(String string) {
		this.list[this.listIndex].time = System.nanoTime();
		this.list[this.listIndex].comment = string;
		++this.listIndex;
	}

	public long getNanoTime() {
		return this.listIndex == 0 ? 0L : System.nanoTime() - this.list[0].time;
	}

	public int getMsTime() {
		return this.listIndex == 0 ? 0 : (int)((System.nanoTime() - this.list[0].time) / 1000000L);
	}

	public void print() {
		long long1 = this.list[0].time;
		System.out.println("========== START === " + this.caption + " =============");
		for (int int1 = 1; int1 < this.listIndex; ++int1) {
			System.out.println(int1 + " " + this.list[int1].comment + ": " + (this.list[int1].time - long1) / 1000000L);
			long1 = this.list[int1].time;
		}

		PrintStream printStream = System.out;
		long long2 = System.nanoTime() - this.list[0].time;
		printStream.println("END: " + long2 / 1000000L);
		System.out.println("==========  END  === " + this.caption + " =============");
	}

	static class TimeStamp {
		long time;
		String comment;

		public TimeStamp(String string) {
			this.comment = string;
			this.time = System.nanoTime();
		}

		public TimeStamp() {
		}
	}
}
