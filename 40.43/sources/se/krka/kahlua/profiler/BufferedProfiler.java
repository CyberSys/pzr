package se.krka.kahlua.profiler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BufferedProfiler implements Profiler {
	private final List buffer = new ArrayList();

	public void getSample(Sample sample) {
		this.buffer.add(sample);
	}

	public void sendTo(Profiler profiler) {
		Iterator iterator = this.buffer.iterator();
		while (iterator.hasNext()) {
			Sample sample = (Sample)iterator.next();
			profiler.getSample(sample);
		}
	}
}
