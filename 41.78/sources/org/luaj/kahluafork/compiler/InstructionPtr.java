package org.luaj.kahluafork.compiler;


class InstructionPtr {
	final int[] code;
	final int idx;

	InstructionPtr(int[] intArray, int int1) {
		this.code = intArray;
		this.idx = int1;
	}

	int get() {
		return this.code[this.idx];
	}

	void set(int int1) {
		this.code[this.idx] = int1;
	}
}
