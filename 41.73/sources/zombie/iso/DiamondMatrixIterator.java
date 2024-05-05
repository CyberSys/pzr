package zombie.iso;

import org.joml.Vector2i;


public class DiamondMatrixIterator {
	private int size;
	private int lineSize;
	private int line;
	private int column;

	public DiamondMatrixIterator(int int1) {
		this.size = int1;
		this.lineSize = 1;
		this.line = 0;
		this.column = 0;
	}

	public DiamondMatrixIterator reset(int int1) {
		this.size = int1;
		this.lineSize = 1;
		this.line = 0;
		this.column = 0;
		return this;
	}

	public void reset() {
		this.lineSize = 1;
		this.line = 0;
		this.column = 0;
	}

	public boolean next(Vector2i vector2i) {
		if (this.lineSize == 0) {
			vector2i.x = 0;
			vector2i.y = 0;
			return false;
		} else if (this.line == 0 && this.column == 0) {
			vector2i.set(0, 0);
			++this.column;
			return true;
		} else {
			if (this.column < this.lineSize) {
				++vector2i.x;
				--vector2i.y;
				++this.column;
			} else {
				this.column = 1;
				++this.line;
				if (this.line < this.size) {
					++this.lineSize;
					vector2i.x = 0;
					vector2i.y = this.line;
				} else {
					--this.lineSize;
					vector2i.x = this.line - this.size + 1;
					vector2i.y = this.size - 1;
				}
			}

			if (this.lineSize == 0) {
				vector2i.x = 0;
				vector2i.y = 0;
				return false;
			} else {
				return true;
			}
		}
	}

	public Vector2i i2line(int int1) {
		int int2 = 0;
		int int3;
		for (int3 = 1; int3 < this.size + 1; ++int3) {
			int2 += int3;
			if (int1 + 1 <= int2) {
				return new Vector2i(int1 - int2 + int3, int3 - 1);
			}
		}

		for (int3 = this.size + 1; int3 < this.size * 2; ++int3) {
			int2 += this.size * 2 - int3;
			if (int1 + 1 <= int2) {
				return new Vector2i(int1 - int2 + this.size * 2 - int3, int3 - 1);
			}
		}

		return null;
	}

	public Vector2i line2coord(Vector2i vector2i) {
		if (vector2i == null) {
			return null;
		} else {
			Vector2i vector2i2;
			int int1;
			if (vector2i.y < this.size) {
				vector2i2 = new Vector2i(0, vector2i.y);
				for (int1 = 0; int1 < vector2i.x; ++int1) {
					++vector2i2.x;
					--vector2i2.y;
				}

				return vector2i2;
			} else {
				vector2i2 = new Vector2i(vector2i.y - this.size + 1, this.size - 1);
				for (int1 = 0; int1 < vector2i.x; ++int1) {
					++vector2i2.x;
					--vector2i2.y;
				}

				return vector2i2;
			}
		}
	}
}
