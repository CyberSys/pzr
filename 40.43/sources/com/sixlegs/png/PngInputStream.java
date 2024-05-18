package com.sixlegs.png;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;


final class PngInputStream extends InputStream implements DataInput {
	private final CRC32 crc = new CRC32();
	private final InputStream in;
	private final DataInputStream data;
	private final byte[] tmp = new byte[4096];
	private long total;
	private int length;
	private int left;

	public PngInputStream(InputStream inputStream) throws IOException {
		this.in = inputStream;
		this.data = new DataInputStream(this);
		this.left = 8;
		long long1 = this.readLong();
		if (long1 != -8552249625308161526L) {
			throw new PngException("Improper signature, expected 0x" + Long.toHexString(-8552249625308161526L) + ", got 0x" + Long.toHexString(long1), true);
		} else {
			this.total += 8L;
		}
	}

	public int startChunk() throws IOException {
		this.left = 8;
		this.length = this.readInt();
		if (this.length < 0) {
			throw new PngException("Bad chunk length: " + (4294967295L & (long)this.length), true);
		} else {
			this.crc.reset();
			int int1 = this.readInt();
			this.left = this.length;
			this.total += 8L;
			return int1;
		}
	}

	public int endChunk(int int1) throws IOException {
		if (this.getRemaining() != 0) {
			throw new PngException(PngConstants.getChunkName(int1) + " read " + (this.length - this.left) + " bytes, expected " + this.length, true);
		} else {
			this.left = 4;
			int int2 = (int)this.crc.getValue();
			int int3 = this.readInt();
			if (int2 != int3) {
				throw new PngException("Bad CRC value for " + PngConstants.getChunkName(int1) + " chunk", true);
			} else {
				this.total += (long)(this.length + 4);
				return int2;
			}
		}
	}

	public int read() throws IOException {
		if (this.left == 0) {
			return -1;
		} else {
			int int1 = this.in.read();
			if (int1 != -1) {
				this.crc.update(int1);
				--this.left;
			}

			return int1;
		}
	}

	public int read(byte[] byteArray, int int1, int int2) throws IOException {
		if (int2 == 0) {
			return 0;
		} else if (this.left == 0) {
			return -1;
		} else {
			int int3 = this.in.read(byteArray, int1, Math.min(this.left, int2));
			if (int3 != -1) {
				this.crc.update(byteArray, int1, int3);
				this.left -= int3;
			}

			return int3;
		}
	}

	public long skip(long long1) throws IOException {
		int int1 = this.read(this.tmp, 0, (int)Math.min((long)this.tmp.length, long1));
		return int1 < 0 ? 0L : (long)int1;
	}

	public void close() {
		throw new UnsupportedOperationException("do not close me");
	}

	public boolean readBoolean() throws IOException {
		return this.readUnsignedByte() != 0;
	}

	public int readUnsignedByte() throws IOException {
		int int1 = this.read();
		if (int1 < 0) {
			throw new EOFException();
		} else {
			return int1;
		}
	}

	public byte readByte() throws IOException {
		return (byte)this.readUnsignedByte();
	}

	public int readUnsignedShort() throws IOException {
		int int1 = this.read();
		int int2 = this.read();
		if ((int1 | int2) < 0) {
			throw new EOFException();
		} else {
			return (int1 << 8) + (int2 << 0);
		}
	}

	public short readShort() throws IOException {
		return (short)this.readUnsignedShort();
	}

	public char readChar() throws IOException {
		return (char)this.readUnsignedShort();
	}

	public int readInt() throws IOException {
		int int1 = this.read();
		int int2 = this.read();
		int int3 = this.read();
		int int4 = this.read();
		if ((int1 | int2 | int3 | int4) < 0) {
			throw new EOFException();
		} else {
			return (int1 << 24) + (int2 << 16) + (int3 << 8) + (int4 << 0);
		}
	}

	public long readLong() throws IOException {
		return (4294967295L & (long)this.readInt()) << 32 | 4294967295L & (long)this.readInt();
	}

	public float readFloat() throws IOException {
		return Float.intBitsToFloat(this.readInt());
	}

	public double readDouble() throws IOException {
		return Double.longBitsToDouble(this.readLong());
	}

	public void readFully(byte[] byteArray) throws IOException {
		this.data.readFully(byteArray, 0, byteArray.length);
	}

	public void readFully(byte[] byteArray, int int1, int int2) throws IOException {
		this.data.readFully(byteArray, int1, int2);
	}

	public int skipBytes(int int1) throws IOException {
		return this.data.skipBytes(int1);
	}

	public String readLine() throws IOException {
		return this.data.readLine();
	}

	public String readUTF() throws IOException {
		return this.data.readUTF();
	}

	public int getRemaining() {
		return this.left;
	}

	public long getOffset() {
		return this.total;
	}
}
