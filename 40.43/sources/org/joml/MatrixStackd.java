package org.joml;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class MatrixStackd extends Matrix4d {
	private static final long serialVersionUID = 1L;
	private Matrix4d[] mats;
	private int curr;

	public MatrixStackd(int int1) {
		if (int1 < 1) {
			throw new IllegalArgumentException("stackSize must be >= 1");
		} else {
			this.mats = new Matrix4d[int1 - 1];
			for (int int2 = 0; int2 < this.mats.length; ++int2) {
				this.mats[int2] = new Matrix4d();
			}
		}
	}

	public MatrixStackd() {
	}

	public MatrixStackd clear() {
		this.curr = 0;
		this.identity();
		return this;
	}

	public MatrixStackd pushMatrix() {
		if (this.curr == this.mats.length) {
			throw new IllegalStateException("max stack size of " + (this.curr + 1) + " reached");
		} else {
			this.mats[this.curr++].set((Matrix4dc)this);
			return this;
		}
	}

	public MatrixStackd popMatrix() {
		if (this.curr == 0) {
			throw new IllegalStateException("already at the buttom of the stack");
		} else {
			this.set(this.mats[--this.curr]);
			return this;
		}
	}

	public int hashCode() {
		int int1 = super.hashCode();
		int1 = 31 * int1 + this.curr;
		for (int int2 = 0; int2 < this.curr; ++int2) {
			int1 = 31 * int1 + this.mats[int2].hashCode();
		}

		return int1;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!super.equals(object)) {
			return false;
		} else {
			if (object instanceof MatrixStackd) {
				MatrixStackd matrixStackd = (MatrixStackd)object;
				if (this.curr != matrixStackd.curr) {
					return false;
				}

				for (int int1 = 0; int1 < this.curr; ++int1) {
					if (!this.mats[int1].equals(matrixStackd.mats[int1])) {
						return false;
					}
				}
			}

			return true;
		}
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		super.writeExternal(objectOutput);
		objectOutput.writeInt(this.curr);
		for (int int1 = 0; int1 < this.curr; ++int1) {
			objectOutput.writeObject(this.mats[int1]);
		}
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		super.readExternal(objectInput);
		this.curr = objectInput.readInt();
		this.mats = new MatrixStackd[this.curr];
		for (int int1 = 0; int1 < this.curr; ++int1) {
			Matrix4d matrix4d = new Matrix4d();
			matrix4d.readExternal(objectInput);
			this.mats[int1] = matrix4d;
		}
	}
}
