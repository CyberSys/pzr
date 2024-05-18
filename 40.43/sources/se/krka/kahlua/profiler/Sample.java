package se.krka.kahlua.profiler;

import java.util.List;


public class Sample {
	private final List list;
	private final long time;

	public Sample(List list, long long1) {
		this.list = list;
		this.time = long1;
	}

	public List getList() {
		return this.list;
	}

	public long getTime() {
		return this.time;
	}
}
