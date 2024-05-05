package zombie.core.textures;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;


public final class PNGSize {
	private static final byte[] SIGNATURE = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
	private static final int IHDR = 1229472850;
	public int width;
	public int height;
	private int bitdepth;
	private int colorType;
	private int bytesPerPixel;
	private InputStream input;
	private final CRC32 crc = new CRC32();
	private final byte[] buffer = new byte[4096];
	private int chunkLength;
	private int chunkType;
	private int chunkRemaining;

	public void readSize(String string) {
		try {
			FileInputStream fileInputStream = new FileInputStream(string);
			try {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				try {
					this.readSize((InputStream)bufferedInputStream);
				} catch (Throwable throwable) {
					try {
						bufferedInputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedInputStream.close();
			} catch (Throwable throwable3) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileInputStream.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void readSize(InputStream inputStream) throws IOException {
		this.input = inputStream;
		this.readFully(this.buffer, 0, SIGNATURE.length);
		if (!this.checkSignature(this.buffer)) {
			throw new IOException("Not a valid PNG file");
		} else {
			this.openChunk(1229472850);
			this.readIHDR();
			this.closeChunk();
		}
	}

	private void readIHDR() throws IOException {
		this.checkChunkLength(13);
		this.readChunk(this.buffer, 0, 13);
		this.width = this.readInt(this.buffer, 0);
		this.height = this.readInt(this.buffer, 4);
		this.bitdepth = this.buffer[8] & 255;
		this.colorType = this.buffer[9] & 255;
	}

	private void openChunk() throws IOException {
		this.readFully(this.buffer, 0, 8);
		this.chunkLength = this.readInt(this.buffer, 0);
		this.chunkType = this.readInt(this.buffer, 4);
		this.chunkRemaining = this.chunkLength;
		this.crc.reset();
		this.crc.update(this.buffer, 4, 4);
	}

	private void openChunk(int int1) throws IOException {
		this.openChunk();
		if (this.chunkType != int1) {
			throw new IOException("Expected chunk: " + Integer.toHexString(int1));
		}
	}

	private void closeChunk() throws IOException {
		if (this.chunkRemaining > 0) {
			this.skip((long)(this.chunkRemaining + 4));
		} else {
			this.readFully(this.buffer, 0, 4);
			int int1 = this.readInt(this.buffer, 0);
			int int2 = (int)this.crc.getValue();
			if (int2 != int1) {
				throw new IOException("Invalid CRC");
			}
		}

		this.chunkRemaining = 0;
		this.chunkLength = 0;
		this.chunkType = 0;
	}

	private void checkChunkLength(int int1) throws IOException {
		if (this.chunkLength != int1) {
			throw new IOException("Chunk has wrong size");
		}
	}

	private int readChunk(byte[] byteArray, int int1, int int2) throws IOException {
		if (int2 > this.chunkRemaining) {
			int2 = this.chunkRemaining;
		}

		this.readFully(byteArray, int1, int2);
		this.crc.update(byteArray, int1, int2);
		this.chunkRemaining -= int2;
		return int2;
	}

	private void readFully(byte[] byteArray, int int1, int int2) throws IOException {
		do {
			int int3 = this.input.read(byteArray, int1, int2);
			if (int3 < 0) {
				throw new EOFException();
			}

			int1 += int3;
			int2 -= int3;
		} while (int2 > 0);
	}

	private int readInt(byte[] byteArray, int int1) {
		return byteArray[int1] << 24 | (byteArray[int1 + 1] & 255) << 16 | (byteArray[int1 + 2] & 255) << 8 | byteArray[int1 + 3] & 255;
	}

	private void skip(long long1) throws IOException {
		while (long1 > 0L) {
			long long2 = this.input.skip(long1);
			if (long2 < 0L) {
				throw new EOFException();
			}

			long1 -= long2;
		}
	}

	private boolean checkSignature(byte[] byteArray) {
		for (int int1 = 0; int1 < SIGNATURE.length; ++int1) {
			if (byteArray[int1] != SIGNATURE[int1]) {
				return false;
			}
		}

		return true;
	}
}
