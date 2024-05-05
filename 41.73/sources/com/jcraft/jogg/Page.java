package com.jcraft.jogg;


public class Page {
	private static int[] crc_lookup = new int[256];
	public int body;
	public byte[] body_base;
	public int body_len;
	public int header;
	public byte[] header_base;
	public int header_len;

	private static int crc_entry(int int1) {
		int int2 = int1 << 24;
		for (int int3 = 0; int3 < 8; ++int3) {
			if ((int2 & Integer.MIN_VALUE) != 0) {
				int2 = int2 << 1 ^ 79764919;
			} else {
				int2 <<= 1;
			}
		}

		return int2 & -1;
	}

	public int bos() {
		return this.header_base[this.header + 5] & 2;
	}

	public Page copy() {
		return this.copy(new Page());
	}

	public Page copy(Page page) {
		byte[] byteArray = new byte[this.header_len];
		System.arraycopy(this.header_base, this.header, byteArray, 0, this.header_len);
		page.header_len = this.header_len;
		page.header_base = byteArray;
		page.header = 0;
		byteArray = new byte[this.body_len];
		System.arraycopy(this.body_base, this.body, byteArray, 0, this.body_len);
		page.body_len = this.body_len;
		page.body_base = byteArray;
		page.body = 0;
		return page;
	}

	public int eos() {
		return this.header_base[this.header + 5] & 4;
	}

	public long granulepos() {
		long long1 = (long)(this.header_base[this.header + 13] & 255);
		long1 = long1 << 8 | (long)(this.header_base[this.header + 12] & 255);
		long1 = long1 << 8 | (long)(this.header_base[this.header + 11] & 255);
		long1 = long1 << 8 | (long)(this.header_base[this.header + 10] & 255);
		long1 = long1 << 8 | (long)(this.header_base[this.header + 9] & 255);
		long1 = long1 << 8 | (long)(this.header_base[this.header + 8] & 255);
		long1 = long1 << 8 | (long)(this.header_base[this.header + 7] & 255);
		long1 = long1 << 8 | (long)(this.header_base[this.header + 6] & 255);
		return long1;
	}

	public int serialno() {
		return this.header_base[this.header + 14] & 255 | (this.header_base[this.header + 15] & 255) << 8 | (this.header_base[this.header + 16] & 255) << 16 | (this.header_base[this.header + 17] & 255) << 24;
	}

	void checksum() {
		int int1 = 0;
		int int2;
		for (int2 = 0; int2 < this.header_len; ++int2) {
			int1 = int1 << 8 ^ crc_lookup[int1 >>> 24 & 255 ^ this.header_base[this.header + int2] & 255];
		}

		for (int2 = 0; int2 < this.body_len; ++int2) {
			int1 = int1 << 8 ^ crc_lookup[int1 >>> 24 & 255 ^ this.body_base[this.body + int2] & 255];
		}

		this.header_base[this.header + 22] = (byte)int1;
		this.header_base[this.header + 23] = (byte)(int1 >>> 8);
		this.header_base[this.header + 24] = (byte)(int1 >>> 16);
		this.header_base[this.header + 25] = (byte)(int1 >>> 24);
	}

	int continued() {
		return this.header_base[this.header + 5] & 1;
	}

	int pageno() {
		return this.header_base[this.header + 18] & 255 | (this.header_base[this.header + 19] & 255) << 8 | (this.header_base[this.header + 20] & 255) << 16 | (this.header_base[this.header + 21] & 255) << 24;
	}

	int version() {
		return this.header_base[this.header + 4] & 255;
	}

	static  {
	for (int var0 = 0; var0 < crc_lookup.length; ++var0) {
		crc_lookup[var0] = crc_entry(var0);
	}
	}
}
