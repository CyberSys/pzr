package zombie.iso;

import gnu.trove.list.array.TIntArrayList;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import zombie.ChunkMapFilenames;
import zombie.core.logger.ExceptionLogger;
import zombie.popman.ObjectPool;
import zombie.util.BufferedRandomAccessFile;


public class IsoLot {
	public static final HashMap InfoHeaders = new HashMap();
	public static final ArrayList InfoHeaderNames = new ArrayList();
	public static final HashMap InfoFileNames = new HashMap();
	public static final ObjectPool pool = new ObjectPool(IsoLot::new);
	private String m_lastUsedPath = "";
	public int wx = 0;
	public int wy = 0;
	final int[] m_offsetInData = new int[800];
	final TIntArrayList m_data = new TIntArrayList();
	private RandomAccessFile m_in = null;
	LotHeader info;

	public static void Dispose() {
		InfoHeaders.clear();
		InfoHeaderNames.clear();
		InfoFileNames.clear();
		pool.forEach((var0)->{
			RandomAccessFile randomAccessFile = var0.m_in;
			if (randomAccessFile != null) {
				var0.m_in = null;
				try {
					randomAccessFile.close();
				} catch (IOException ioException) {
					ExceptionLogger.logException(ioException);
				}
			}
		});
	}

	public static String readString(BufferedRandomAccessFile bufferedRandomAccessFile) throws EOFException, IOException {
		String string = bufferedRandomAccessFile.getNextLine();
		return string;
	}

	public static int readInt(RandomAccessFile randomAccessFile) throws EOFException, IOException {
		int int1 = randomAccessFile.read();
		int int2 = randomAccessFile.read();
		int int3 = randomAccessFile.read();
		int int4 = randomAccessFile.read();
		if ((int1 | int2 | int3 | int4) < 0) {
			throw new EOFException();
		} else {
			return (int1 << 0) + (int2 << 8) + (int3 << 16) + (int4 << 24);
		}
	}

	public static int readShort(RandomAccessFile randomAccessFile) throws EOFException, IOException {
		int int1 = randomAccessFile.read();
		int int2 = randomAccessFile.read();
		if ((int1 | int2) < 0) {
			throw new EOFException();
		} else {
			return (int1 << 0) + (int2 << 8);
		}
	}

	public static synchronized void put(IsoLot lot) {
		lot.info = null;
		lot.m_data.resetQuick();
		pool.release((Object)lot);
	}

	public static synchronized IsoLot get(Integer integer, Integer integer2, Integer integer3, Integer integer4, IsoChunk chunk) {
		IsoLot lot = (IsoLot)pool.alloc();
		lot.load(integer, integer2, integer3, integer4, chunk);
		return lot;
	}

	public void load(Integer integer, Integer integer2, Integer integer3, Integer integer4, IsoChunk chunk) {
		String string = ChunkMapFilenames.instance.getHeader(integer, integer2);
		this.info = (LotHeader)InfoHeaders.get(string);
		this.wx = integer3;
		this.wy = integer4;
		chunk.lotheader = this.info;
		try {
			string = "world_" + integer + "_" + integer2 + ".lotpack";
			File file = new File((String)InfoFileNames.get(string));
			if (this.m_in == null || !this.m_lastUsedPath.equals(file.getAbsolutePath())) {
				if (this.m_in != null) {
					this.m_in.close();
				}

				this.m_in = new BufferedRandomAccessFile(file.getAbsolutePath(), "r", 4096);
				this.m_lastUsedPath = file.getAbsolutePath();
			}

			int int1 = 0;
			int int2 = this.wx - integer * 30;
			int int3 = this.wy - integer2 * 30;
			int int4 = int2 * 30 + int3;
			this.m_in.seek((long)(4 + int4 * 8));
			int int5 = readInt(this.m_in);
			this.m_in.seek((long)int5);
			this.m_data.resetQuick();
			int int6 = Math.min(this.info.levels, 8);
			for (int int7 = 0; int7 < int6; ++int7) {
				for (int int8 = 0; int8 < 10; ++int8) {
					for (int int9 = 0; int9 < 10; ++int9) {
						int int10 = int8 + int9 * 10 + int7 * 100;
						this.m_offsetInData[int10] = -1;
						if (int1 > 0) {
							--int1;
						} else {
							int int11 = readInt(this.m_in);
							if (int11 == -1) {
								int1 = readInt(this.m_in);
								if (int1 > 0) {
									--int1;
									continue;
								}
							}

							if (int11 > 1) {
								this.m_offsetInData[int10] = this.m_data.size();
								this.m_data.add(int11 - 1);
								int int12 = readInt(this.m_in);
								for (int int13 = 1; int13 < int11; ++int13) {
									int int14 = readInt(this.m_in);
									this.m_data.add(int14);
								}
							}
						}
					}
				}
			}
		} catch (Exception exception) {
			Arrays.fill(this.m_offsetInData, -1);
			this.m_data.resetQuick();
			ExceptionLogger.logException(exception);
		}
	}
}
