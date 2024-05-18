package se.krka.kahlua.profiler;

import se.krka.kahlua.vm.Prototype;


public class LuaStacktraceElement implements StacktraceElement {
	private final int pc;
	private final Prototype prototype;

	public LuaStacktraceElement(int int1, Prototype prototype) {
		this.pc = int1;
		this.prototype = prototype;
	}

	public int getLine() {
		return this.pc >= 0 && this.pc < this.prototype.lines.length ? this.prototype.lines[this.pc] : 0;
	}

	public String getSource() {
		return this.prototype.name;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof LuaStacktraceElement)) {
			return false;
		} else {
			LuaStacktraceElement luaStacktraceElement = (LuaStacktraceElement)object;
			if (this.getLine() != luaStacktraceElement.getLine()) {
				return false;
			} else {
				return this.prototype.equals(luaStacktraceElement.prototype);
			}
		}
	}

	public int hashCode() {
		int int1 = this.getLine();
		int1 = 31 * int1 + this.prototype.hashCode();
		return int1;
	}

	public String toString() {
		return this.name();
	}

	public String name() {
		return this.getSource() + ":" + this.getLine();
	}

	public String type() {
		return "lua";
	}
}
