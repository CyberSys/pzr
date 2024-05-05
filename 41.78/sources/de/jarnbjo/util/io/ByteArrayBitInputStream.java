package de.jarnbjo.util.io;

import java.io.IOException;


public class ByteArrayBitInputStream implements BitInputStream {
	private byte[] source;
	private byte currentByte;
	private int endian;
	private int byteIndex;
	private int bitIndex;

	public ByteArrayBitInputStream(byte[] byteArray) {
		this(byteArray, 0);
	}

	public ByteArrayBitInputStream(byte[] byteArray, int int1) {
		this.byteIndex = 0;
		this.bitIndex = 0;
		this.endian = int1;
		this.source = byteArray;
		this.currentByte = byteArray[0];
		this.bitIndex = int1 == 0 ? 0 : 7;
	}

	public boolean getBit() throws IOException {
		if (this.endian == 0) {
			if (this.bitIndex > 7) {
				this.bitIndex = 0;
				this.currentByte = this.source[++this.byteIndex];
			}

			return (this.currentByte & 1 << this.bitIndex++) != 0;
		} else {
			if (this.bitIndex < 0) {
				this.bitIndex = 7;
				this.currentByte = this.source[++this.byteIndex];
			}

			return (this.currentByte & 1 << this.bitIndex--) != 0;
		}
	}

	public int getInt(int int1) throws IOException {
		if (int1 > 32) {
			throw new IllegalArgumentException("Argument \"bits\" must be <= 32");
		} else {
			int int2 = 0;
			int int3;
			if (this.endian == 0) {
				for (int3 = 0; int3 < int1; ++int3) {
					if (this.getBit()) {
						int2 |= 1 << int3;
					}
				}
			} else {
				if (this.bitIndex < 0) {
					this.bitIndex = 7;
					this.currentByte = this.source[++this.byteIndex];
				}

				if (int1 <= this.bitIndex + 1) {
					int3 = this.currentByte & 255;
					int int4 = 1 + this.bitIndex - int1;
					int int5 = (1 << int1) - 1 << int4;
					int2 = (int3 & int5) >> int4;
					this.bitIndex -= int1;
				} else {
					int2 = (this.currentByte & 255 & (1 << this.bitIndex + 1) - 1) << int1 - this.bitIndex - 1;
					int1 -= this.bitIndex + 1;
					for (this.currentByte = this.source[++this.byteIndex]; int1 >= 8; this.currentByte = this.source[++this.byteIndex]) {
						int1 -= 8;
						int2 |= (this.source[this.byteIndex] & 255) << int1;
					}

					if (int1 > 0) {
						int3 = this.source[this.byteIndex] & 255;
						int2 |= int3 >> 8 - int1 & (1 << int1) - 1;
						this.bitIndex = 7 - int1;
					} else {
						this.currentByte = this.source[--this.byteIndex];
						this.bitIndex = -1;
					}
				}
			}

			return int2;
		}
	}

	public int getSignedInt(int int1) throws IOException {
		int int2 = this.getInt(int1);
		if (int2 >= 1 << int1 - 1) {
			int2 -= 1 << int1;
		}

		return int2;
	}

	public int getInt(HuffmanNode huffmanNode) throws IOException {
		for (; huffmanNode.value == null; huffmanNode = (this.currentByte & 1 << this.bitIndex++) != 0 ? huffmanNode.o1 : huffmanNode.o0) {
			if (this.bitIndex > 7) {
				this.bitIndex = 0;
				this.currentByte = this.source[++this.byteIndex];
			}
		}

		return huffmanNode.value;
	}

	public long getLong(int int1) throws IOException {
		if (int1 > 64) {
			throw new IllegalArgumentException("Argument \"bits\" must be <= 64");
		} else {
			long long1 = 0L;
			int int2;
			if (this.endian == 0) {
				for (int2 = 0; int2 < int1; ++int2) {
					if (this.getBit()) {
						long1 |= 1L << int2;
					}
				}
			} else {
				for (int2 = int1 - 1; int2 >= 0; --int2) {
					if (this.getBit()) {
						long1 |= 1L << int2;
					}
				}
			}

			return long1;
		}
	}

	public int readSignedRice(int int1) throws IOException {
		int int2 = -1;
		boolean boolean1 = false;
		boolean boolean2 = false;
		if (this.endian == 0) {
			throw new UnsupportedOperationException("ByteArrayBitInputStream.readSignedRice() is only supported in big endian mode");
		} else {
			byte byte1 = this.source[this.byteIndex];
			do {
				++int2;
				if (this.bitIndex < 0) {
					this.bitIndex = 7;
					++this.byteIndex;
					byte1 = this.source[this.byteIndex];
				}
			}	 while ((byte1 & 1 << this.bitIndex--) == 0);

			if (this.bitIndex < 0) {
				this.bitIndex = 7;
				++this.byteIndex;
			}

			int int3;
			int int4;
			if (int1 <= this.bitIndex + 1) {
				int3 = this.source[this.byteIndex] & 255;
				int int5 = 1 + this.bitIndex - int1;
				int int6 = (1 << int1) - 1 << int5;
				int4 = (int3 & int6) >> int5;
				this.bitIndex -= int1;
			} else {
				int4 = (this.source[this.byteIndex] & 255 & (1 << this.bitIndex + 1) - 1) << int1 - this.bitIndex - 1;
				int int7 = int1 - (this.bitIndex + 1);
				++this.byteIndex;
				while (int7 >= 8) {
					int7 -= 8;
					int4 |= (this.source[this.byteIndex] & 255) << int7;
					++this.byteIndex;
				}

				if (int7 > 0) {
					int3 = this.source[this.byteIndex] & 255;
					int4 |= int3 >> 8 - int7 & (1 << int7) - 1;
					this.bitIndex = 7 - int7;
				} else {
					--this.byteIndex;
					this.bitIndex = -1;
				}
			}

			int int8 = int2 << int1 | int4;
			return (int8 & 1) == 1 ? -(int8 >> 1) - 1 : int8 >> 1;
		}
	}

	public void readSignedRice(int int1, int[] intArray, int int2, int int3) throws IOException {
		if (this.endian == 0) {
			throw new UnsupportedOperationException("ByteArrayBitInputStream.readSignedRice() is only supported in big endian mode");
		} else {
			for (int int4 = int2; int4 < int2 + int3; ++int4) {
				int int5 = -1;
				boolean boolean1 = false;
				byte byte1 = this.source[this.byteIndex];
				do {
					++int5;
					if (this.bitIndex < 0) {
						this.bitIndex = 7;
						++this.byteIndex;
						byte1 = this.source[this.byteIndex];
					}
				}		 while ((byte1 & 1 << this.bitIndex--) == 0);

				if (this.bitIndex < 0) {
					this.bitIndex = 7;
					++this.byteIndex;
				}

				int int6;
				int int7;
				if (int1 <= this.bitIndex + 1) {
					int6 = this.source[this.byteIndex] & 255;
					int int8 = 1 + this.bitIndex - int1;
					int int9 = (1 << int1) - 1 << int8;
					int7 = (int6 & int9) >> int8;
					this.bitIndex -= int1;
				} else {
					int7 = (this.source[this.byteIndex] & 255 & (1 << this.bitIndex + 1) - 1) << int1 - this.bitIndex - 1;
					int int10 = int1 - (this.bitIndex + 1);
					++this.byteIndex;
					while (int10 >= 8) {
						int10 -= 8;
						int7 |= (this.source[this.byteIndex] & 255) << int10;
						++this.byteIndex;
					}

					if (int10 > 0) {
						int6 = this.source[this.byteIndex] & 255;
						int7 |= int6 >> 8 - int10 & (1 << int10) - 1;
						this.bitIndex = 7 - int10;
					} else {
						--this.byteIndex;
						this.bitIndex = -1;
					}
				}

				int6 = int5 << int1 | int7;
				intArray[int4] = (int6 & 1) == 1 ? -(int6 >> 1) - 1 : int6 >> 1;
			}
		}
	}

	public void align() {
		if (this.endian == 1 && this.bitIndex >= 0) {
			this.bitIndex = 7;
			++this.byteIndex;
		} else if (this.endian == 0 && this.bitIndex <= 7) {
			this.bitIndex = 0;
			++this.byteIndex;
		}
	}

	public void setEndian(int int1) {
		if (this.endian == 1 && int1 == 0) {
			this.bitIndex = 0;
			++this.byteIndex;
		} else if (this.endian == 0 && int1 == 1) {
			this.bitIndex = 7;
			++this.byteIndex;
		}

		this.endian = int1;
	}

	public byte[] getSource() {
		return this.source;
	}
}
