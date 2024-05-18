package de.jarnbjo.ogg;

import de.jarnbjo.util.io.ByteArrayBitInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import zombie.debug.DebugLog;


public class OggPage {
	private int version;
	private boolean continued;
	private boolean bos;
	private boolean eos;
	private long absoluteGranulePosition;
	private int streamSerialNumber;
	private int pageSequenceNumber;
	private int pageCheckSum;
	private int[] segmentOffsets;
	private int[] segmentLengths;
	private int totalLength;
	private byte[] header;
	private byte[] segmentTable;
	private byte[] data;

	protected OggPage() {
	}

	private OggPage(int int1, boolean boolean1, boolean boolean2, boolean boolean3, long long1, int int2, int int3, int int4, int[] intArray, int[] intArray2, int int5, byte[] byteArray, byte[] byteArray2, byte[] byteArray3) {
		this.version = int1;
		this.continued = boolean1;
		this.bos = boolean2;
		this.eos = boolean3;
		this.absoluteGranulePosition = long1;
		this.streamSerialNumber = int2;
		this.pageSequenceNumber = int3;
		this.pageCheckSum = int4;
		this.segmentOffsets = intArray;
		this.segmentLengths = intArray2;
		this.totalLength = int5;
		this.header = byteArray;
		this.segmentTable = byteArray2;
		this.data = byteArray3;
	}

	public static OggPage create(RandomAccessFile randomAccessFile) throws IOException, EndOfOggStreamException, OggFormatException {
		return create(randomAccessFile, false);
	}

	public static OggPage create(RandomAccessFile randomAccessFile, boolean boolean1) throws IOException, EndOfOggStreamException, OggFormatException {
		return create((Object)randomAccessFile, boolean1);
	}

	public static OggPage create(InputStream inputStream) throws IOException, EndOfOggStreamException, OggFormatException {
		return create(inputStream, false);
	}

	public static OggPage create(InputStream inputStream, boolean boolean1) throws IOException, EndOfOggStreamException, OggFormatException {
		return create((Object)inputStream, boolean1);
	}

	public static OggPage create(byte[] byteArray) throws IOException, EndOfOggStreamException, OggFormatException {
		return create(byteArray, false);
	}

	public static OggPage create(byte[] byteArray, boolean boolean1) throws IOException, EndOfOggStreamException, OggFormatException {
		return create((Object)byteArray, boolean1);
	}

	private static OggPage create(Object object, boolean boolean1) throws IOException, EndOfOggStreamException, OggFormatException {
		try {
			int int1 = 27;
			byte[] byteArray = new byte[27];
			if (object instanceof RandomAccessFile) {
				RandomAccessFile randomAccessFile = (RandomAccessFile)object;
				if (randomAccessFile.getFilePointer() == randomAccessFile.length()) {
					return null;
				}

				randomAccessFile.readFully(byteArray);
			} else if (object instanceof InputStream) {
				readFully((InputStream)object, byteArray);
			} else if (object instanceof byte[]) {
				System.arraycopy((byte[])((byte[])object), 0, byteArray, 0, 27);
			}

			ByteArrayBitInputStream byteArrayBitInputStream = new ByteArrayBitInputStream(byteArray);
			int int2 = byteArrayBitInputStream.getInt(32);
			if (int2 != 1399285583) {
				String string;
				for (string = Integer.toHexString(int2); string.length() < 8; string = "0" + string) {
				}

				string = string.substring(6, 8) + string.substring(4, 6) + string.substring(2, 4) + string.substring(0, 2);
				char char1 = (char)Integer.valueOf(string.substring(0, 2), 16);
				char char2 = (char)Integer.valueOf(string.substring(2, 4), 16);
				char char3 = (char)Integer.valueOf(string.substring(4, 6), 16);
				char char4 = (char)Integer.valueOf(string.substring(6, 8), 16);
				DebugLog.log("Ogg packet header is 0x" + string + " (" + char1 + char2 + char3 + char4 + "), should be 0x4f676753 (OggS)");
			}

			int int3 = byteArrayBitInputStream.getInt(8);
			byte byte1 = (byte)byteArrayBitInputStream.getInt(8);
			boolean boolean2 = (byte1 & 1) != 0;
			boolean boolean3 = (byte1 & 2) != 0;
			boolean boolean4 = (byte1 & 4) != 0;
			long long1 = byteArrayBitInputStream.getLong(64);
			int int4 = byteArrayBitInputStream.getInt(32);
			int int5 = byteArrayBitInputStream.getInt(32);
			int int6 = byteArrayBitInputStream.getInt(32);
			int int7 = byteArrayBitInputStream.getInt(8);
			int[] intArray = new int[int7];
			int[] intArray2 = new int[int7];
			int int8 = 0;
			byte[] byteArray2 = new byte[int7];
			byte[] byteArray3 = new byte[1];
			for (int int9 = 0; int9 < int7; ++int9) {
				int int10 = 0;
				if (object instanceof RandomAccessFile) {
					int10 = ((RandomAccessFile)object).readByte() & 255;
				} else if (object instanceof InputStream) {
					int10 = ((InputStream)object).read();
				} else if (object instanceof byte[]) {
					byte byte2 = ((byte[])((byte[])object))[int1++];
					int10 = byte2 & 255;
				}

				byteArray2[int9] = (byte)int10;
				intArray2[int9] = int10;
				intArray[int9] = int8;
				int8 += int10;
			}

			byte[] byteArray4 = null;
			if (!boolean1) {
				byteArray4 = new byte[int8];
				if (object instanceof RandomAccessFile) {
					((RandomAccessFile)object).readFully(byteArray4);
				} else if (object instanceof InputStream) {
					readFully((InputStream)object, byteArray4);
				} else if (object instanceof byte[]) {
					System.arraycopy(object, int1, byteArray4, 0, int8);
				}
			}

			return new OggPage(int3, boolean2, boolean3, boolean4, long1, int4, int5, int6, intArray, intArray2, int8, byteArray, byteArray2, byteArray4);
		} catch (EOFException eOFException) {
			throw new EndOfOggStreamException();
		}
	}

	private static void readFully(InputStream inputStream, byte[] byteArray) throws IOException {
		int int1;
		for (int int2 = 0; int2 < byteArray.length; int2 += int1) {
			int1 = inputStream.read(byteArray, int2, byteArray.length - int2);
			if (int1 == -1) {
				throw new EndOfOggStreamException();
			}
		}
	}

	public long getAbsoluteGranulePosition() {
		return this.absoluteGranulePosition;
	}

	public int getStreamSerialNumber() {
		return this.streamSerialNumber;
	}

	public int getPageSequenceNumber() {
		return this.pageSequenceNumber;
	}

	public int getPageCheckSum() {
		return this.pageCheckSum;
	}

	public int getTotalLength() {
		return this.data != null ? 27 + this.segmentTable.length + this.data.length : this.totalLength;
	}

	public byte[] getData() {
		return this.data;
	}

	public byte[] getHeader() {
		return this.header;
	}

	public byte[] getSegmentTable() {
		return this.segmentTable;
	}

	public int[] getSegmentOffsets() {
		return this.segmentOffsets;
	}

	public int[] getSegmentLengths() {
		return this.segmentLengths;
	}

	public boolean isContinued() {
		return this.continued;
	}

	public boolean isFresh() {
		return !this.continued;
	}

	public boolean isBos() {
		return this.bos;
	}

	public boolean isEos() {
		return this.eos;
	}
}
