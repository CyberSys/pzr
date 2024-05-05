package se.krka.kahlua.profiler;

import java.util.HashMap;
import java.util.Map;


public class StacktraceCounter {
	private final Map children = new HashMap();
	private long time = 0L;

	public void addTime(long long1) {
		this.time += long1;
	}

	public StacktraceCounter getOrCreateChild(StacktraceElement stacktraceElement) {
		StacktraceCounter stacktraceCounter = (StacktraceCounter)this.children.get(stacktraceElement);
		if (stacktraceCounter == null) {
			stacktraceCounter = new StacktraceCounter();
			this.children.put(stacktraceElement, stacktraceCounter);
		}

		return stacktraceCounter;
	}

	public long getTime() {
		return this.time;
	}

	public Map getChildren() {
		return this.children;
	}
}
