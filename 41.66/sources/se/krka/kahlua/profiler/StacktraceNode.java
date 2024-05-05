package se.krka.kahlua.profiler;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class StacktraceNode {
	private final long time;
	private final StacktraceElement element;
	private final List children;

	public StacktraceNode(StacktraceElement stacktraceElement, List list, long long1) {
		this.element = stacktraceElement;
		this.children = list;
		this.time = long1;
	}

	public static StacktraceNode createFrom(StacktraceCounter stacktraceCounter, StacktraceElement stacktraceElement, int int1, double double1, int int2) {
		StacktraceNode stacktraceNode = new StacktraceNode(stacktraceElement, new ArrayList(), stacktraceCounter.getTime());
		if (int1 > 0) {
			Map map = stacktraceCounter.getChildren();
			ArrayList arrayList = new ArrayList(map.entrySet());
			Collections.sort(arrayList, new Comparator(){
				
				public int compare(Entry stacktraceElement, Entry int1) {
					return Long.signum(((StacktraceCounter)int1.getValue()).getTime() - ((StacktraceCounter)stacktraceElement.getValue()).getTime());
				}
			});

			for (int int3 = arrayList.size() - 1; int3 >= int2; --int3) {
				arrayList.remove(int3);
			}

			Iterator iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				Entry entry = (Entry)iterator.next();
				StacktraceElement stacktraceElement2 = (StacktraceElement)entry.getKey();
				StacktraceCounter stacktraceCounter2 = (StacktraceCounter)entry.getValue();
				if ((double)stacktraceCounter2.getTime() >= double1 * (double)stacktraceCounter.getTime()) {
					StacktraceNode stacktraceNode2 = createFrom(stacktraceCounter2, stacktraceElement2, int1 - 1, double1, int2);
					stacktraceNode.children.add(stacktraceNode2);
				}
			}
		}

		return stacktraceNode;
	}

	public void output(PrintWriter printWriter) {
		this.output(printWriter, "", this.time, this.time);
	}

	public void output(PrintWriter printWriter, String string, long long1, long long2) {
		printWriter.println(String.format("%-40s   %4d ms   %5.1f%% of parent	%5.1f%% of total", string + this.element.name() + " (" + this.element.type() + ")", this.time, 100.0 * (double)this.time / (double)long1, 100.0 * (double)this.time / (double)long2));
		String string2 = string + "  ";
		Iterator iterator = this.children.iterator();
		while (iterator.hasNext()) {
			StacktraceNode stacktraceNode = (StacktraceNode)iterator.next();
			stacktraceNode.output(printWriter, string2, this.time, long2);
		}
	}
}
