package zombie.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;


public final class BufferedRandomAccessFile extends RandomAccessFile {
	private byte[] buffer;
	private int buf_end = 0;
	private int buf_pos = 0;
	private long real_pos = 0L;
	private final int BUF_SIZE;

	public BufferedRandomAccessFile(String string, String string2, int int1) throws IOException {
		super(string, string2);
		this.invalidate();
		this.BUF_SIZE = int1;
		this.buffer = new byte[this.BUF_SIZE];
	}

	public BufferedRandomAccessFile(File file, String string, int int1) throws IOException {
		super(file, string);
		this.invalidate();
		this.BUF_SIZE = int1;
		this.buffer = new byte[this.BUF_SIZE];
	}

	public final int read() throws IOException {
		if (this.buf_pos >= this.buf_end && this.fillBuffer() < 0) {
			return -1;
		} else {
			return this.buf_end == 0 ? -1 : this.buffer[this.buf_pos++] & 255;
		}
	}

	private int fillBuffer() throws IOException {
		int int1 = super.read(this.buffer, 0, this.BUF_SIZE);
		if (int1 >= 0) {
			this.real_pos += (long)int1;
			this.buf_end = int1;
			this.buf_pos = 0;
		}

		return int1;
	}

	private void invalidate() throws IOException {
		this.buf_end = 0;
		this.buf_pos = 0;
		this.real_pos = super.getFilePointer();
	}

	public int read(byte[] byteArray, int int1, int int2) throws IOException {
		int int3 = this.buf_end - this.buf_pos;
		if (int2 <= int3) {
			System.arraycopy(this.buffer, this.buf_pos, byteArray, int1, int2);
			this.buf_pos += int2;
			return int2;
		} else {
			for (int int4 = 0; int4 < int2; ++int4) {
				int int5 = this.read();
				if (int5 == -1) {
					return int4;
				}

				byteArray[int1 + int4] = (byte)int5;
			}

			return int2;
		}
	}

	public long getFilePointer() throws IOException {
		long long1 = this.real_pos;
		return long1 - (long)this.buf_end + (long)this.buf_pos;
	}

	public void seek(long long1) throws IOException {
		int int1 = (int)(this.real_pos - long1);
		if (int1 >= 0 && int1 <= this.buf_end) {
			this.buf_pos = this.buf_end - int1;
		} else {
			super.seek(long1);
			this.invalidate();
		}
	}

	public final String getNextLine() throws IOException {
		String string = null;
		if (this.buf_end - this.buf_pos <= 0 && this.fillBuffer() < 0) {
			throw new IOException("error in filling buffer!");
		} else {
			int int1 = -1;
			for (int int2 = this.buf_pos; int2 < this.buf_end; ++int2) {
				if (this.buffer[int2] == 10) {
					int1 = int2;
					break;
				}
			}

			if (int1 < 0) {
				StringBuilder stringBuilder = new StringBuilder(128);
				int int3;
				while ((int3 = this.read()) != -1 && int3 != 10) {
					stringBuilder.append((char)int3);
				}

				return int3 == -1 && stringBuilder.length() == 0 ? null : stringBuilder.toString();
			} else {
				if (int1 > 0 && this.buffer[int1 - 1] == 13) {
					string = new String(this.buffer, this.buf_pos, int1 - this.buf_pos - 1, StandardCharsets.UTF_8);
				} else {
					string = new String(this.buffer, this.buf_pos, int1 - this.buf_pos, StandardCharsets.UTF_8);
				}

				this.buf_pos = int1 + 1;
				return string;
			}
		}
	}
}
