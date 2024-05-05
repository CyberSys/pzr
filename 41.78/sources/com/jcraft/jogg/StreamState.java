package com.jcraft.jogg;


public class StreamState {
	public int e_o_s;
	int b_o_s;
	byte[] body_data;
	int body_fill;
	int body_storage;
	long[] granule_vals;
	long granulepos;
	byte[] header;
	int header_fill;
	int lacing_fill;
	int lacing_packet;
	int lacing_returned;
	int lacing_storage;
	int[] lacing_vals;
	long packetno;
	int pageno;
	int serialno;
	private int body_returned;

	public StreamState() {
		this.header = new byte[282];
		this.init();
	}

	StreamState(int int1) {
		this();
		this.init(int1);
	}

	public void clear() {
		this.body_data = null;
		this.lacing_vals = null;
		this.granule_vals = null;
	}

	public int eof() {
		return this.e_o_s;
	}

	public int flush(Page page) {
		boolean boolean1 = false;
		int int1 = this.lacing_fill > 255 ? 255 : this.lacing_fill;
		int int2 = 0;
		int int3 = 0;
		long long1 = this.granule_vals[0];
		if (int1 == 0) {
			return 0;
		} else {
			int int4;
			if (this.b_o_s == 0) {
				long1 = 0L;
				for (int4 = 0; int4 < int1; ++int4) {
					if ((this.lacing_vals[int4] & 255) < 255) {
						++int4;
						break;
					}
				}
			} else {
				for (int4 = 0; int4 < int1 && int3 <= 4096; ++int4) {
					int3 += this.lacing_vals[int4] & 255;
					long1 = this.granule_vals[int4];
				}
			}

			System.arraycopy("OggS".getBytes(), 0, this.header, 0, 4);
			this.header[4] = 0;
			this.header[5] = 0;
			byte[] byteArray;
			if ((this.lacing_vals[0] & 256) == 0) {
				byteArray = this.header;
				byteArray[5] = (byte)(byteArray[5] | 1);
			}

			if (this.b_o_s == 0) {
				byteArray = this.header;
				byteArray[5] = (byte)(byteArray[5] | 2);
			}

			if (this.e_o_s != 0 && this.lacing_fill == int4) {
				byteArray = this.header;
				byteArray[5] = (byte)(byteArray[5] | 4);
			}

			this.b_o_s = 1;
			int int5;
			for (int5 = 6; int5 < 14; ++int5) {
				this.header[int5] = (byte)((int)long1);
				long1 >>>= 8;
			}

			int int6 = this.serialno;
			for (int5 = 14; int5 < 18; ++int5) {
				this.header[int5] = (byte)int6;
				int6 >>>= 8;
			}

			if (this.pageno == -1) {
				this.pageno = 0;
			}

			int6 = this.pageno++;
			for (int5 = 18; int5 < 22; ++int5) {
				this.header[int5] = (byte)int6;
				int6 >>>= 8;
			}

			this.header[22] = 0;
			this.header[23] = 0;
			this.header[24] = 0;
			this.header[25] = 0;
			this.header[26] = (byte)int4;
			for (int5 = 0; int5 < int4; ++int5) {
				this.header[int5 + 27] = (byte)this.lacing_vals[int5];
				int2 += this.header[int5 + 27] & 255;
			}

			page.header_base = this.header;
			page.header = 0;
			page.header_len = this.header_fill = int4 + 27;
			page.body_base = this.body_data;
			page.body = this.body_returned;
			page.body_len = int2;
			this.lacing_fill -= int4;
			System.arraycopy(this.lacing_vals, int4, this.lacing_vals, 0, this.lacing_fill * 4);
			System.arraycopy(this.granule_vals, int4, this.granule_vals, 0, this.lacing_fill * 8);
			this.body_returned += int2;
			page.checksum();
			return 1;
		}
	}

	public void init(int int1) {
		if (this.body_data == null) {
			this.init();
		} else {
			int int2;
			for (int2 = 0; int2 < this.body_data.length; ++int2) {
				this.body_data[int2] = 0;
			}

			for (int2 = 0; int2 < this.lacing_vals.length; ++int2) {
				this.lacing_vals[int2] = 0;
			}

			for (int2 = 0; int2 < this.granule_vals.length; ++int2) {
				this.granule_vals[int2] = 0L;
			}
		}

		this.serialno = int1;
	}

	public int packetin(Packet packet) {
		int int1 = packet.bytes / 255 + 1;
		if (this.body_returned != 0) {
			this.body_fill -= this.body_returned;
			if (this.body_fill != 0) {
				System.arraycopy(this.body_data, this.body_returned, this.body_data, 0, this.body_fill);
			}

			this.body_returned = 0;
		}

		this.body_expand(packet.bytes);
		this.lacing_expand(int1);
		System.arraycopy(packet.packet_base, packet.packet, this.body_data, this.body_fill, packet.bytes);
		this.body_fill += packet.bytes;
		int int2;
		for (int2 = 0; int2 < int1 - 1; ++int2) {
			this.lacing_vals[this.lacing_fill + int2] = 255;
			this.granule_vals[this.lacing_fill + int2] = this.granulepos;
		}

		this.lacing_vals[this.lacing_fill + int2] = packet.bytes % 255;
		this.granulepos = this.granule_vals[this.lacing_fill + int2] = packet.granulepos;
		int[] intArray = this.lacing_vals;
		int int3 = this.lacing_fill;
		intArray[int3] |= 256;
		this.lacing_fill += int1;
		++this.packetno;
		if (packet.e_o_s != 0) {
			this.e_o_s = 1;
		}

		return 0;
	}

	public int packetout(Packet packet) {
		int int1 = this.lacing_returned;
		if (this.lacing_packet <= int1) {
			return 0;
		} else if ((this.lacing_vals[int1] & 1024) != 0) {
			++this.lacing_returned;
			++this.packetno;
			return -1;
		} else {
			int int2 = this.lacing_vals[int1] & 255;
			byte byte1 = 0;
			packet.packet_base = this.body_data;
			packet.packet = this.body_returned;
			packet.e_o_s = this.lacing_vals[int1] & 512;
			packet.b_o_s = this.lacing_vals[int1] & 256;
			int int3;
			for (int3 = byte1 + int2; int2 == 255; int3 += int2) {
				++int1;
				int int4 = this.lacing_vals[int1];
				int2 = int4 & 255;
				if ((int4 & 512) != 0) {
					packet.e_o_s = 512;
				}
			}

			packet.packetno = this.packetno;
			packet.granulepos = this.granule_vals[int1];
			packet.bytes = int3;
			this.body_returned += int3;
			this.lacing_returned = int1 + 1;
			++this.packetno;
			return 1;
		}
	}

	public int pagein(Page page) {
		byte[] byteArray = page.header_base;
		int int1 = page.header;
		byte[] byteArray2 = page.body_base;
		int int2 = page.body;
		int int3 = page.body_len;
		int int4 = 0;
		int int5 = page.version();
		int int6 = page.continued();
		int int7 = page.bos();
		int int8 = page.eos();
		long long1 = page.granulepos();
		int int9 = page.serialno();
		int int10 = page.pageno();
		int int11 = byteArray[int1 + 26] & 255;
		int int12 = this.lacing_returned;
		int int13 = this.body_returned;
		if (int13 != 0) {
			this.body_fill -= int13;
			if (this.body_fill != 0) {
				System.arraycopy(this.body_data, int13, this.body_data, 0, this.body_fill);
			}

			this.body_returned = 0;
		}

		if (int12 != 0) {
			if (this.lacing_fill - int12 != 0) {
				System.arraycopy(this.lacing_vals, int12, this.lacing_vals, 0, this.lacing_fill - int12);
				System.arraycopy(this.granule_vals, int12, this.granule_vals, 0, this.lacing_fill - int12);
			}

			this.lacing_fill -= int12;
			this.lacing_packet -= int12;
			this.lacing_returned = 0;
		}

		if (int9 != this.serialno) {
			return -1;
		} else if (int5 > 0) {
			return -1;
		} else {
			this.lacing_expand(int11 + 1);
			if (int10 != this.pageno) {
				for (int12 = this.lacing_packet; int12 < this.lacing_fill; ++int12) {
					this.body_fill -= this.lacing_vals[int12] & 255;
				}

				this.lacing_fill = this.lacing_packet;
				if (this.pageno != -1) {
					this.lacing_vals[this.lacing_fill++] = 1024;
					++this.lacing_packet;
				}

				if (int6 != 0) {
					for (int7 = 0; int4 < int11; ++int4) {
						int13 = byteArray[int1 + 27 + int4] & 255;
						int2 += int13;
						int3 -= int13;
						if (int13 < 255) {
							++int4;
							break;
						}
					}
				}
			}

			if (int3 != 0) {
				this.body_expand(int3);
				System.arraycopy(byteArray2, int2, this.body_data, this.body_fill, int3);
				this.body_fill += int3;
			}

			int12 = -1;
			int[] intArray;
			int int14;
			while (int4 < int11) {
				int13 = byteArray[int1 + 27 + int4] & 255;
				this.lacing_vals[this.lacing_fill] = int13;
				this.granule_vals[this.lacing_fill] = -1L;
				if (int7 != 0) {
					intArray = this.lacing_vals;
					int14 = this.lacing_fill;
					intArray[int14] |= 256;
					int7 = 0;
				}

				if (int13 < 255) {
					int12 = this.lacing_fill;
				}

				++this.lacing_fill;
				++int4;
				if (int13 < 255) {
					this.lacing_packet = this.lacing_fill;
				}
			}

			if (int12 != -1) {
				this.granule_vals[int12] = long1;
			}

			if (int8 != 0) {
				this.e_o_s = 1;
				if (this.lacing_fill > 0) {
					intArray = this.lacing_vals;
					int14 = this.lacing_fill - 1;
					intArray[int14] |= 512;
				}
			}

			this.pageno = int10 + 1;
			return 0;
		}
	}

	public int pageout(Page page) {
		return (this.e_o_s == 0 || this.lacing_fill == 0) && this.body_fill - this.body_returned <= 4096 && this.lacing_fill < 255 && (this.lacing_fill == 0 || this.b_o_s != 0) ? 0 : this.flush(page);
	}

	public int reset() {
		this.body_fill = 0;
		this.body_returned = 0;
		this.lacing_fill = 0;
		this.lacing_packet = 0;
		this.lacing_returned = 0;
		this.header_fill = 0;
		this.e_o_s = 0;
		this.b_o_s = 0;
		this.pageno = -1;
		this.packetno = 0L;
		this.granulepos = 0L;
		return 0;
	}

	void body_expand(int int1) {
		if (this.body_storage <= this.body_fill + int1) {
			this.body_storage += int1 + 1024;
			byte[] byteArray = new byte[this.body_storage];
			System.arraycopy(this.body_data, 0, byteArray, 0, this.body_data.length);
			this.body_data = byteArray;
		}
	}

	void destroy() {
		this.clear();
	}

	void init() {
		this.body_storage = 16384;
		this.body_data = new byte[this.body_storage];
		this.lacing_storage = 1024;
		this.lacing_vals = new int[this.lacing_storage];
		this.granule_vals = new long[this.lacing_storage];
	}

	void lacing_expand(int int1) {
		if (this.lacing_storage <= this.lacing_fill + int1) {
			this.lacing_storage += int1 + 32;
			int[] intArray = new int[this.lacing_storage];
			System.arraycopy(this.lacing_vals, 0, intArray, 0, this.lacing_vals.length);
			this.lacing_vals = intArray;
			long[] longArray = new long[this.lacing_storage];
			System.arraycopy(this.granule_vals, 0, longArray, 0, this.granule_vals.length);
			this.granule_vals = longArray;
		}
	}
}
