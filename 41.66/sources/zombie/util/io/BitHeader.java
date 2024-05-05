package zombie.util.io;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedDeque;
import zombie.core.utils.Bits;
import zombie.debug.DebugLog;


public final class BitHeader {
	private static final ConcurrentLinkedDeque pool_byte = new ConcurrentLinkedDeque();
	private static final ConcurrentLinkedDeque pool_short = new ConcurrentLinkedDeque();
	private static final ConcurrentLinkedDeque pool_int = new ConcurrentLinkedDeque();
	private static final ConcurrentLinkedDeque pool_long = new ConcurrentLinkedDeque();
	public static boolean DEBUG = true;

	private static BitHeader.BitHeaderBase getHeader(BitHeader.HeaderSize headerSize, ByteBuffer byteBuffer, boolean boolean1) {
		if (headerSize == BitHeader.HeaderSize.Byte) {
			BitHeader.BitHeaderByte bitHeaderByte = (BitHeader.BitHeaderByte)pool_byte.poll();
			if (bitHeaderByte == null) {
				bitHeaderByte = new BitHeader.BitHeaderByte();
			}

			bitHeaderByte.setBuffer(byteBuffer);
			bitHeaderByte.setWrite(boolean1);
			return bitHeaderByte;
		} else if (headerSize == BitHeader.HeaderSize.Short) {
			BitHeader.BitHeaderShort bitHeaderShort = (BitHeader.BitHeaderShort)pool_short.poll();
			if (bitHeaderShort == null) {
				bitHeaderShort = new BitHeader.BitHeaderShort();
			}

			bitHeaderShort.setBuffer(byteBuffer);
			bitHeaderShort.setWrite(boolean1);
			return bitHeaderShort;
		} else if (headerSize == BitHeader.HeaderSize.Integer) {
			BitHeader.BitHeaderInt bitHeaderInt = (BitHeader.BitHeaderInt)pool_int.poll();
			if (bitHeaderInt == null) {
				bitHeaderInt = new BitHeader.BitHeaderInt();
			}

			bitHeaderInt.setBuffer(byteBuffer);
			bitHeaderInt.setWrite(boolean1);
			return bitHeaderInt;
		} else if (headerSize == BitHeader.HeaderSize.Long) {
			BitHeader.BitHeaderLong bitHeaderLong = (BitHeader.BitHeaderLong)pool_long.poll();
			if (bitHeaderLong == null) {
				bitHeaderLong = new BitHeader.BitHeaderLong();
			}

			bitHeaderLong.setBuffer(byteBuffer);
			bitHeaderLong.setWrite(boolean1);
			return bitHeaderLong;
		} else {
			return null;
		}
	}

	private BitHeader() {
	}

	public static void debug_print() {
		if (DEBUG) {
			DebugLog.log("*********************************************");
			DebugLog.log("ByteHeader = " + pool_byte.size());
			DebugLog.log("ShortHeader = " + pool_short.size());
			DebugLog.log("IntHeader = " + pool_int.size());
			DebugLog.log("LongHeader = " + pool_long.size());
		}
	}

	public static BitHeaderWrite allocWrite(BitHeader.HeaderSize headerSize, ByteBuffer byteBuffer) {
		return allocWrite(headerSize, byteBuffer, false);
	}

	public static BitHeaderWrite allocWrite(BitHeader.HeaderSize headerSize, ByteBuffer byteBuffer, boolean boolean1) {
		BitHeader.BitHeaderBase bitHeaderBase = getHeader(headerSize, byteBuffer, true);
		if (!boolean1) {
			bitHeaderBase.create();
		}

		return bitHeaderBase;
	}

	public static BitHeaderRead allocRead(BitHeader.HeaderSize headerSize, ByteBuffer byteBuffer) {
		return allocRead(headerSize, byteBuffer, false);
	}

	public static BitHeaderRead allocRead(BitHeader.HeaderSize headerSize, ByteBuffer byteBuffer, boolean boolean1) {
		BitHeader.BitHeaderBase bitHeaderBase = getHeader(headerSize, byteBuffer, false);
		if (!boolean1) {
			bitHeaderBase.read();
		}

		return bitHeaderBase;
	}

	public static enum HeaderSize {

		Byte,
		Short,
		Integer,
		Long;

		private static BitHeader.HeaderSize[] $values() {
			return new BitHeader.HeaderSize[]{Byte, Short, Integer, Long};
		}
	}

	public static class BitHeaderByte extends BitHeader.BitHeaderBase {
		private ConcurrentLinkedDeque pool;
		private byte header;

		private BitHeaderByte() {
		}

		public void release() {
			this.reset();
			BitHeader.pool_byte.offer(this);
		}

		public int getLen() {
			return Bits.getLen(this.header);
		}

		protected void reset_header() {
			this.header = 0;
		}

		protected void write_header() {
			this.buffer.put(this.header);
		}

		protected void read_header() {
			this.header = this.buffer.get();
		}

		protected void addflags_header(int int1) {
			this.header = Bits.addFlags(this.header, int1);
		}

		protected void addflags_header(long long1) {
			this.header = Bits.addFlags(this.header, long1);
		}

		protected boolean hasflags_header(int int1) {
			return Bits.hasFlags(this.header, int1);
		}

		protected boolean hasflags_header(long long1) {
			return Bits.hasFlags(this.header, long1);
		}

		protected boolean equals_header(int int1) {
			return this.header == int1;
		}

		protected boolean equals_header(long long1) {
			return (long)this.header == long1;
		}
	}

	public static class BitHeaderShort extends BitHeader.BitHeaderBase {
		private ConcurrentLinkedDeque pool;
		private short header;

		private BitHeaderShort() {
		}

		public void release() {
			this.reset();
			BitHeader.pool_short.offer(this);
		}

		public int getLen() {
			return Bits.getLen(this.header);
		}

		protected void reset_header() {
			this.header = 0;
		}

		protected void write_header() {
			this.buffer.putShort(this.header);
		}

		protected void read_header() {
			this.header = this.buffer.getShort();
		}

		protected void addflags_header(int int1) {
			this.header = Bits.addFlags(this.header, int1);
		}

		protected void addflags_header(long long1) {
			this.header = Bits.addFlags(this.header, long1);
		}

		protected boolean hasflags_header(int int1) {
			return Bits.hasFlags(this.header, int1);
		}

		protected boolean hasflags_header(long long1) {
			return Bits.hasFlags(this.header, long1);
		}

		protected boolean equals_header(int int1) {
			return this.header == int1;
		}

		protected boolean equals_header(long long1) {
			return (long)this.header == long1;
		}
	}

	public static class BitHeaderInt extends BitHeader.BitHeaderBase {
		private ConcurrentLinkedDeque pool;
		private int header;

		private BitHeaderInt() {
		}

		public void release() {
			this.reset();
			BitHeader.pool_int.offer(this);
		}

		public int getLen() {
			return Bits.getLen(this.header);
		}

		protected void reset_header() {
			this.header = 0;
		}

		protected void write_header() {
			this.buffer.putInt(this.header);
		}

		protected void read_header() {
			this.header = this.buffer.getInt();
		}

		protected void addflags_header(int int1) {
			this.header = Bits.addFlags(this.header, int1);
		}

		protected void addflags_header(long long1) {
			this.header = Bits.addFlags(this.header, long1);
		}

		protected boolean hasflags_header(int int1) {
			return Bits.hasFlags(this.header, int1);
		}

		protected boolean hasflags_header(long long1) {
			return Bits.hasFlags(this.header, long1);
		}

		protected boolean equals_header(int int1) {
			return this.header == int1;
		}

		protected boolean equals_header(long long1) {
			return (long)this.header == long1;
		}
	}

	public static class BitHeaderLong extends BitHeader.BitHeaderBase {
		private ConcurrentLinkedDeque pool;
		private long header;

		private BitHeaderLong() {
		}

		public void release() {
			this.reset();
			BitHeader.pool_long.offer(this);
		}

		public int getLen() {
			return Bits.getLen(this.header);
		}

		protected void reset_header() {
			this.header = 0L;
		}

		protected void write_header() {
			this.buffer.putLong(this.header);
		}

		protected void read_header() {
			this.header = this.buffer.getLong();
		}

		protected void addflags_header(int int1) {
			this.header = Bits.addFlags(this.header, int1);
		}

		protected void addflags_header(long long1) {
			this.header = Bits.addFlags(this.header, long1);
		}

		protected boolean hasflags_header(int int1) {
			return Bits.hasFlags(this.header, int1);
		}

		protected boolean hasflags_header(long long1) {
			return Bits.hasFlags(this.header, long1);
		}

		protected boolean equals_header(int int1) {
			return this.header == (long)int1;
		}

		protected boolean equals_header(long long1) {
			return this.header == long1;
		}
	}

	public abstract static class BitHeaderBase implements BitHeaderRead,BitHeaderWrite {
		protected boolean isWrite;
		protected ByteBuffer buffer;
		protected int start_pos = -1;

		protected void setBuffer(ByteBuffer byteBuffer) {
			this.buffer = byteBuffer;
		}

		protected void setWrite(boolean boolean1) {
			this.isWrite = boolean1;
		}

		public int getStartPosition() {
			return this.start_pos;
		}

		protected void reset() {
			this.buffer = null;
			this.isWrite = false;
			this.start_pos = -1;
			this.reset_header();
		}

		public abstract int getLen();

		public abstract void release();

		protected abstract void reset_header();

		protected abstract void write_header();

		protected abstract void read_header();

		protected abstract void addflags_header(int int1);

		protected abstract void addflags_header(long long1);

		protected abstract boolean hasflags_header(int int1);

		protected abstract boolean hasflags_header(long long1);

		protected abstract boolean equals_header(int int1);

		protected abstract boolean equals_header(long long1);

		public void create() {
			if (this.isWrite) {
				this.start_pos = this.buffer.position();
				this.reset_header();
				this.write_header();
			} else {
				throw new RuntimeException("BitHeader -> Cannot write to a non write Header.");
			}
		}

		public void write() {
			if (this.isWrite) {
				int int1 = this.buffer.position();
				this.buffer.position(this.start_pos);
				this.write_header();
				this.buffer.position(int1);
			} else {
				throw new RuntimeException("BitHeader -> Cannot write to a non write Header.");
			}
		}

		public void read() {
			if (!this.isWrite) {
				this.start_pos = this.buffer.position();
				this.read_header();
			} else {
				throw new RuntimeException("BitHeader -> Cannot read from a non read Header.");
			}
		}

		public void addFlags(int int1) {
			if (this.isWrite) {
				this.addflags_header(int1);
			} else {
				throw new RuntimeException("BitHeader -> Cannot set bits on a non write Header.");
			}
		}

		public void addFlags(long long1) {
			if (this.isWrite) {
				this.addflags_header(long1);
			} else {
				throw new RuntimeException("BitHeader -> Cannot set bits on a non write Header.");
			}
		}

		public boolean hasFlags(int int1) {
			return this.hasflags_header(int1);
		}

		public boolean hasFlags(long long1) {
			return this.hasflags_header(long1);
		}

		public boolean equals(int int1) {
			return this.equals_header(int1);
		}

		public boolean equals(long long1) {
			return this.equals_header(long1);
		}
	}
}
