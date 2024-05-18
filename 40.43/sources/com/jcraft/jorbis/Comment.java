package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;
import com.jcraft.jogg.Packet;


public class Comment {
	private static byte[] _vorbis = "vorbis".getBytes();
	private static byte[] _vendor = "Xiphophorus libVorbis I 20000508".getBytes();
	private static final int OV_EIMPL = -130;
	public int[] comment_lengths;
	public int comments;
	public byte[][] user_comments;
	public byte[] vendor;

	static boolean tagcompare(byte[] byteArray, byte[] byteArray2, int int1) {
		for (int int2 = 0; int2 < int1; ++int2) {
			byte byte1 = byteArray[int2];
			byte byte2 = byteArray2[int2];
			if (90 >= byte1 && byte1 >= 65) {
				byte1 = (byte)(byte1 - 65 + 97);
			}

			if (90 >= byte2 && byte2 >= 65) {
				byte2 = (byte)(byte2 - 65 + 97);
			}

			if (byte1 != byte2) {
				return false;
			}
		}

		return true;
	}

	public void add(String string) {
		this.add(string.getBytes());
	}

	public void add_tag(String string, String string2) {
		if (string2 == null) {
			string2 = "";
		}

		this.add(string + "=" + string2);
	}

	public String getComment(int int1) {
		return this.comments <= int1 ? null : new String(this.user_comments[int1], 0, this.user_comments[int1].length - 1);
	}

	public String getVendor() {
		return new String(this.vendor, 0, this.vendor.length - 1);
	}

	public int header_out(Packet packet) {
		Buffer buffer = new Buffer();
		buffer.writeinit();
		if (this.pack(buffer) != 0) {
			return -130;
		} else {
			packet.packet_base = new byte[buffer.bytes()];
			packet.packet = 0;
			packet.bytes = buffer.bytes();
			System.arraycopy(buffer.buffer(), 0, packet.packet_base, 0, packet.bytes);
			packet.b_o_s = 0;
			packet.e_o_s = 0;
			packet.granulepos = 0L;
			return 0;
		}
	}

	public void init() {
		this.user_comments = (byte[][])null;
		this.comments = 0;
		this.vendor = null;
	}

	public String query(String string) {
		return this.query((String)string, 0);
	}

	public String query(String string, int int1) {
		int int2 = this.query(string.getBytes(), int1);
		if (int2 == -1) {
			return null;
		} else {
			byte[] byteArray = this.user_comments[int2];
			for (int int3 = 0; int3 < this.comment_lengths[int2]; ++int3) {
				if (byteArray[int3] == 61) {
					return new String(byteArray, int3 + 1, this.comment_lengths[int2] - (int3 + 1));
				}
			}

			return null;
		}
	}

	public String toString() {
		String string = "Vendor: " + new String(this.vendor, 0, this.vendor.length - 1);
		for (int int1 = 0; int1 < this.comments; ++int1) {
			string = string + "\nComment: " + new String(this.user_comments[int1], 0, this.user_comments[int1].length - 1);
		}

		string = string + "\n";
		return string;
	}

	void clear() {
		for (int int1 = 0; int1 < this.comments; ++int1) {
			this.user_comments[int1] = null;
		}

		this.user_comments = (byte[][])null;
		this.vendor = null;
	}

	int pack(Buffer buffer) {
		buffer.write(3, 8);
		buffer.write(_vorbis);
		buffer.write(_vendor.length, 32);
		buffer.write(_vendor);
		buffer.write(this.comments, 32);
		if (this.comments != 0) {
			for (int int1 = 0; int1 < this.comments; ++int1) {
				if (this.user_comments[int1] != null) {
					buffer.write(this.comment_lengths[int1], 32);
					buffer.write(this.user_comments[int1]);
				} else {
					buffer.write(0, 32);
				}
			}
		}

		buffer.write(1, 1);
		return 0;
	}

	int unpack(Buffer buffer) {
		int int1 = buffer.read(32);
		if (int1 < 0) {
			this.clear();
			return -1;
		} else {
			this.vendor = new byte[int1 + 1];
			buffer.read(this.vendor, int1);
			this.comments = buffer.read(32);
			if (this.comments < 0) {
				this.clear();
				return -1;
			} else {
				this.user_comments = new byte[this.comments + 1][];
				this.comment_lengths = new int[this.comments + 1];
				for (int int2 = 0; int2 < this.comments; ++int2) {
					int int3 = buffer.read(32);
					if (int3 < 0) {
						this.clear();
						return -1;
					}

					this.comment_lengths[int2] = int3;
					this.user_comments[int2] = new byte[int3 + 1];
					buffer.read(this.user_comments[int2], int3);
				}

				if (buffer.read(1) != 1) {
					this.clear();
					return -1;
				} else {
					return 0;
				}
			}
		}
	}

	private void add(byte[] byteArray) {
		byte[][] byteArrayArray = new byte[this.comments + 2][];
		if (this.user_comments != null) {
			System.arraycopy(this.user_comments, 0, byteArrayArray, 0, this.comments);
		}

		this.user_comments = byteArrayArray;
		int[] intArray = new int[this.comments + 2];
		if (this.comment_lengths != null) {
			System.arraycopy(this.comment_lengths, 0, intArray, 0, this.comments);
		}

		this.comment_lengths = intArray;
		byte[] byteArray2 = new byte[byteArray.length + 1];
		System.arraycopy(byteArray, 0, byteArray2, 0, byteArray.length);
		this.user_comments[this.comments] = byteArray2;
		this.comment_lengths[this.comments] = byteArray.length;
		++this.comments;
		this.user_comments[this.comments] = null;
	}

	private int query(byte[] byteArray, int int1) {
		boolean boolean1 = false;
		int int2 = 0;
		int int3 = byteArray.length + 1;
		byte[] byteArray2 = new byte[int3];
		System.arraycopy(byteArray, 0, byteArray2, 0, byteArray.length);
		byteArray2[byteArray.length] = 61;
		for (int int4 = 0; int4 < this.comments; ++int4) {
			if (tagcompare(this.user_comments[int4], byteArray2, int3)) {
				if (int1 == int2) {
					return int4;
				}

				++int2;
			}
		}

		return -1;
	}
}
