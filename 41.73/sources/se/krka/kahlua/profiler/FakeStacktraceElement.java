package se.krka.kahlua.profiler;


public class FakeStacktraceElement implements StacktraceElement {
	private final String name;
	private final String type;

	public FakeStacktraceElement(String string, String string2) {
		this.name = string;
		this.type = string2;
	}

	public String name() {
		return this.name;
	}

	public String type() {
		return this.type;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof FakeStacktraceElement)) {
			return false;
		} else {
			FakeStacktraceElement fakeStacktraceElement = (FakeStacktraceElement)object;
			if (!this.name.equals(fakeStacktraceElement.name)) {
				return false;
			} else {
				return this.type.equals(fakeStacktraceElement.type);
			}
		}
	}

	public int hashCode() {
		int int1 = this.name.hashCode();
		int1 = 31 * int1 + this.type.hashCode();
		return int1;
	}

	public String toString() {
		return this.name;
	}
}
