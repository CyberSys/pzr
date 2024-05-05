package zombie.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;


public class ByteBufferBackedInputStream extends InputStream {
	final ByteBuffer buf;

	public ByteBufferBackedInputStream(ByteBuffer byteBuffer) {
		Objects.requireNonNull(byteBuffer);
		this.buf = byteBuffer;
	}

	public int read() throws IOException {
		return !this.buf.hasRemaining() ? -1 : this.buf.get() & 255;
	}

	public int read(byte[] byteArray, int int1, int int2) throws IOException {
		if (!this.buf.hasRemaining()) {
			return -1;
		} else {
			int2 = Math.min(int2, this.buf.remaining());
			this.buf.get(byteArray, int1, int2);
			return int2;
		}
	}
}
