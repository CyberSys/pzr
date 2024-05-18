package se.krka.kahlua.profiler;


public class AggregatingProfiler implements Profiler {
	private final StacktraceCounter root = new StacktraceCounter();

	public synchronized void getSample(Sample sample) {
		this.root.addTime(sample.getTime());
		StacktraceCounter stacktraceCounter = this.root;
		for (int int1 = sample.getList().size() - 1; int1 >= 0; --int1) {
			StacktraceElement stacktraceElement = (StacktraceElement)sample.getList().get(int1);
			StacktraceCounter stacktraceCounter2 = stacktraceCounter.getOrCreateChild(stacktraceElement);
			stacktraceCounter2.addTime(sample.getTime());
			stacktraceCounter = stacktraceCounter2;
		}
	}

	public StacktraceNode toTree(int int1, double double1, int int2) {
		return StacktraceNode.createFrom(this.root, new FakeStacktraceElement("Root", "root"), int1, double1, int2);
	}
}
