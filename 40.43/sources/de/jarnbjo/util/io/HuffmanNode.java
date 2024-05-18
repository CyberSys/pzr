package de.jarnbjo.util.io;

import java.io.IOException;


public final class HuffmanNode {
	private HuffmanNode parent;
	private int depth;
	protected HuffmanNode o0;
	protected HuffmanNode o1;
	protected Integer value;
	private boolean full;

	public HuffmanNode() {
		this((HuffmanNode)null);
	}

	protected HuffmanNode(HuffmanNode huffmanNode) {
		this.depth = 0;
		this.full = false;
		this.parent = huffmanNode;
		if (huffmanNode != null) {
			this.depth = huffmanNode.getDepth() + 1;
		}
	}

	protected HuffmanNode(HuffmanNode huffmanNode, int int1) {
		this(huffmanNode);
		this.value = new Integer(int1);
		this.full = true;
	}

	protected int read(BitInputStream bitInputStream) throws IOException {
		HuffmanNode huffmanNode;
		for (huffmanNode = this; huffmanNode.value == null; huffmanNode = bitInputStream.getBit() ? huffmanNode.o1 : huffmanNode.o0) {
		}

		return huffmanNode.value;
	}

	protected HuffmanNode get0() {
		return this.o0 == null ? this.set0(new HuffmanNode(this)) : this.o0;
	}

	protected HuffmanNode get1() {
		return this.o1 == null ? this.set1(new HuffmanNode(this)) : this.o1;
	}

	protected Integer getValue() {
		return this.value;
	}

	private HuffmanNode getParent() {
		return this.parent;
	}

	protected int getDepth() {
		return this.depth;
	}

	private boolean isFull() {
		return this.full ? true : (this.full = this.o0 != null && this.o0.isFull() && this.o1 != null && this.o1.isFull());
	}

	private HuffmanNode set0(HuffmanNode huffmanNode) {
		return this.o0 = huffmanNode;
	}

	private HuffmanNode set1(HuffmanNode huffmanNode) {
		return this.o1 = huffmanNode;
	}

	private void setValue(Integer integer) {
		this.full = true;
		this.value = integer;
	}

	public boolean setNewValue(int int1, int int2) {
		if (this.isFull()) {
			return false;
		} else if (int1 == 1) {
			if (this.o0 == null) {
				this.set0(new HuffmanNode(this, int2));
				return true;
			} else if (this.o1 == null) {
				this.set1(new HuffmanNode(this, int2));
				return true;
			} else {
				return false;
			}
		} else {
			return this.get0().setNewValue(int1 - 1, int2) ? true : this.get1().setNewValue(int1 - 1, int2);
		}
	}
}
