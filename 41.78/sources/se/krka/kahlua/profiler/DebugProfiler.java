package se.krka.kahlua.profiler;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;


public class DebugProfiler implements Profiler {
	private PrintWriter output;

	public DebugProfiler(Writer writer) {
		this.output = new PrintWriter(writer);
	}

	public synchronized void getSample(Sample sample) {
		this.output.println("Sample: " + sample.getTime() + " ms");
		Iterator iterator = sample.getList().iterator();
		while (iterator.hasNext()) {
			StacktraceElement stacktraceElement = (StacktraceElement)iterator.next();
			PrintWriter printWriter = this.output;
			String string = stacktraceElement.name();
			printWriter.println("\t" + string + "\t" + stacktraceElement.type() + "\t" + stacktraceElement.hashCode());
		}
	}
}