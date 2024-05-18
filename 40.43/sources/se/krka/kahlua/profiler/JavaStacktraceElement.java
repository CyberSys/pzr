package se.krka.kahlua.profiler;

import se.krka.kahlua.vm.JavaFunction;


public class JavaStacktraceElement implements StacktraceElement {
	private final JavaFunction javaFunction;

	public JavaStacktraceElement(JavaFunction javaFunction) {
		this.javaFunction = javaFunction;
	}

	public String name() {
		return this.javaFunction.toString();
	}

	public String type() {
		return "java";
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof JavaStacktraceElement)) {
			return false;
		} else {
			JavaStacktraceElement javaStacktraceElement = (JavaStacktraceElement)object;
			return this.javaFunction == javaStacktraceElement.javaFunction;
		}
	}

	public int hashCode() {
		return this.javaFunction.hashCode();
	}
}
