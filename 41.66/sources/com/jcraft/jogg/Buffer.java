package com.jcraft.jogg;

import zombie.iso.areas.IsoArea;


public class Buffer {
	public static String version = "0a2a0q";
	private static final int BUFFER_INCREMENT = 256;
	private static final int[] mask = new int[]{0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 131071, 262143, 524287, 1048575, 2097151, 4194303, 8388607, 16777215, 33554431, 67108863, 134217727, 268435455, 536870911, 1073741823, Integer.MAX_VALUE, -1};
	byte[] buffer = null;
	int endbit = 0;
	int endbyte = 0;
	int ptr = 0;
	int storage = 0;

	public static int ilog(int int1) {
		int int2;
		for (int2 = 0; int1 > 0; int1 >>>= 1) {
			++int2;
		}

		return int2;
	}

	public static void report(String string) {
		System.err.println(string);
		System.exit(1);
	}

	public void adv(int int1) {
		int1 += this.endbit;
		this.ptr += int1 / 8;
		this.endbyte += int1 / 8;
		this.endbit = int1 & 7;
	}

	public void adv1() {
		++this.endbit;
		if (this.endbit > 7) {
			this.endbit = 0;
			++this.ptr;
			++this.endbyte;
		}
	}

	public int bits() {
		return this.endbyte * 8 + this.endbit;
	}

	public byte[] buffer() {
		return this.buffer;
	}

	public int bytes() {
		return this.endbyte + (this.endbit + 7) / 8;
	}

	public int look(int int1) {
		int int2 = mask[int1];
		int1 += this.endbit;
		if (this.endbyte + 4 >= this.storage && this.endbyte + (int1 - 1) / 8 >= this.storage) {
			return -1;
		} else {
			int int3 = (this.buffer[this.ptr] & 255) >>> this.endbit;
			if (int1 > 8) {
				int3 |= (this.buffer[this.ptr + 1] & 255) << 8 - this.endbit;
				if (int1 > 16) {
					int3 |= (this.buffer[this.ptr + 2] & 255) << 16 - this.endbit;
					if (int1 > 24) {
						int3 |= (this.buffer[this.ptr + 3] & 255) << 24 - this.endbit;
						if (int1 > 32 && this.endbit != 0) {
							int3 |= (this.buffer[this.ptr + 4] & 255) << 32 - this.endbit;
						}
					}
				}
			}

			return int2 & int3;
		}
	}

	public int look1() {
		return this.endbyte >= this.storage ? -1 : this.buffer[this.ptr] >> this.endbit & 1;
	}

	public int read(int int1) {
		int int2 = mask[int1];
		int1 += this.endbit;
		if (this.endbyte + 4 >= this.storage) {
			byte byte1 = -1;
			if (this.endbyte + (int1 - 1) / 8 >= this.storage) {
				this.ptr += int1 / 8;
				this.endbyte += int1 / 8;
				this.endbit = int1 & 7;
				return byte1;
			}
		}

		int int3 = (this.buffer[this.ptr] & 255) >>> this.endbit;
		if (int1 > 8) {
			int3 |= (this.buffer[this.ptr + 1] & 255) << 8 - this.endbit;
			if (int1 > 16) {
				int3 |= (this.buffer[this.ptr + 2] & 255) << 16 - this.endbit;
				if (int1 > 24) {
					int3 |= (this.buffer[this.ptr + 3] & 255) << 24 - this.endbit;
					if (int1 > 32 && this.endbit != 0) {
						int3 |= (this.buffer[this.ptr + 4] & 255) << 32 - this.endbit;
					}
				}
			}
		}

		int3 &= int2;
		this.ptr += int1 / 8;
		this.endbyte += int1 / 8;
		this.endbit = int1 & 7;
		return int3;
	}

	public void read(byte[] byteArray, int int1) {
		for (int int2 = 0; int1-- != 0; byteArray[int2++] = (byte)this.read(8)) {
		}
	}

	public int read1() {
		if (this.endbyte >= this.storage) {
			byte byte1 = -1;
			++this.endbit;
			if (this.endbit > 7) {
				this.endbit = 0;
				++this.ptr;
				++this.endbyte;
			}

			return byte1;
		} else {
			int int1 = this.buffer[this.ptr] >> this.endbit & 1;
			++this.endbit;
			if (this.endbit > 7) {
				this.endbit = 0;
				++this.ptr;
				++this.endbyte;
			}

			return int1;
		}
	}

	public int readB(int int1) {
		int int2 = 32 - int1;
		int1 += this.endbit;
		if (this.endbyte + 4 >= this.storage) {
			byte byte1 = -1;
			if (this.endbyte * 8 + int1 > this.storage * 8) {
				this.ptr += int1 / 8;
				this.endbyte += int1 / 8;
				this.endbit = int1 & 7;
				return byte1;
			}
		}

		int int3 = (this.buffer[this.ptr] & 255) << 24 + this.endbit;
		if (int1 > 8) {
			int3 |= (this.buffer[this.ptr + 1] & 255) << 16 + this.endbit;
			if (int1 > 16) {
				int3 |= (this.buffer[this.ptr + 2] & 255) << 8 + this.endbit;
				if (int1 > 24) {
					int3 |= (this.buffer[this.ptr + 3] & 255) << this.endbit;
					if (int1 > 32 && this.endbit != 0) {
						int3 |= (this.buffer[this.ptr + 4] & 255) >> 8 - this.endbit;
					}
				}
			}
		}

		int3 = int3 >>> (int2 >> 1) >>> (int2 + 1 >> 1);
		this.ptr += int1 / 8;
		this.endbyte += int1 / 8;
		this.endbit = int1 & 7;
		return int3;
	}

	public void readinit(byte[] byteArray, int int1) {
		this.readinit(byteArray, 0, int1);
	}

	public void readinit(byte[] byteArray, int int1, int int2) {
		this.ptr = int1;
		this.buffer = byteArray;
		this.endbit = this.endbyte = 0;
		this.storage = int2;
	}

	public void write(byte[] byteArray) {
		for (int int1 = 0; int1 < byteArray.length && byteArray[int1] != 0; ++int1) {
			this.write(byteArray[int1], 8);
		}
	}

	public void write(int int1, int int2) {
		if (this.endbyte + 4 >= this.storage) {
			byte[] byteArray = new byte[this.storage + 256];
			System.arraycopy(this.buffer, 0, byteArray, 0, this.storage);
			this.buffer = byteArray;
			this.storage += 256;
		}

		int1 &= mask[int2];
		int2 += this.endbit;
		byte[] byteArray2 = this.buffer;
		int int3 = this.ptr;
		byteArray2[int3] |= (byte)(int1 << this.endbit);
		if (int2 >= 8) {
			this.buffer[this.ptr + 1] = (byte)(int1 >>> 8 - this.endbit);
			if (int2 >= 16) {
				this.buffer[this.ptr + 2] = (byte)(int1 >>> 16 - this.endbit);
				if (int2 >= 24) {
					this.buffer[this.ptr + 3] = (byte)(int1 >>> 24 - this.endbit);
					if (int2 >= 32) {
						if (this.endbit > 0) {
							this.buffer[this.ptr + 4] = (byte)(int1 >>> 32 - this.endbit);
						} else {
							this.buffer[this.ptr + 4] = 0;
						}
					}
				}
			}
		}

		this.endbyte += int2 / 8;
		this.ptr += int2 / 8;
		this.endbit = int2 & 7;
	}

	public void writeclear() {
		this.buffer = null;
	}

	public void writeinit() {
		this.buffer = new byte[256];
		this.ptr = 0;
		this.buffer[0] = 0;
		this.storage = 256;
	}

	void reset() {
		this.ptr = 0;
		this.buffer[0] = 0;
		this.endbit = this.endbyte = 0;
	}

	static  {
	if (!version.equals(IsoArea.version)) {
		System.exit(0);
	}
	}
}
