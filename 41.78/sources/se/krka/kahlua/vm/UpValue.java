package se.krka.kahlua.vm;


public final class UpValue {
	private Coroutine coroutine;
	private final int index;
	private Object value;

	public UpValue(Coroutine coroutine, int int1) {
		this.coroutine = coroutine;
		this.index = int1;
	}

	public int getIndex() {
		return this.index;
	}

	public final Object getValue() {
		return this.coroutine == null ? this.value : this.coroutine.objectStack[this.index];
	}

	public final void setValue(Object object) {
		if (this.coroutine == null) {
			this.value = object;
		} else {
			this.coroutine.objectStack[this.index] = object;
		}
	}

	public void close() {
		this.value = this.coroutine.objectStack[this.index];
		this.coroutine = null;
	}
}
