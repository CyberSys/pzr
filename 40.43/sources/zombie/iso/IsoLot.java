package zombie.iso;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import zombie.ChunkMapFilenames;
import zombie.IntArrayCache;


public class IsoLot {
	public static HashMap InfoHeaders = new HashMap();
	public static ArrayList InfoHeaderNames = new ArrayList();
	public static HashMap InfoFileNames = new HashMap();
	Integer[][][][] data = (Integer[][][][])null;
	RandomAccessFile in = null;
	LotHeader info;
	public int wx = 0;
	public int wy = 0;
	static String lastUsedPath = "";
	public static Stack pool = new Stack();
	public ArrayList arrays = new ArrayList();

	public static void Dispose() {
		InfoHeaders.clear();
		InfoHeaderNames.clear();
		InfoFileNames.clear();
	}

	public static String readString(RandomAccessFile randomAccessFile) throws EOFException, IOException {
		String string = randomAccessFile.readLine();
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

	public static void put(IsoLot lot) {
		lot.info = null;
		ArrayList arrayList = lot.arrays;
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			Integer[] integerArray = (Integer[])arrayList.get(int1);
			for (int int2 = 0; int2 < integerArray.length; ++int2) {
				integerArray[int2] = 0;
			}

			IntArrayCache.instance.put(integerArray);
		}

		lot.arrays.clear();
		pool.push(lot);
	}

	public static IsoLot get(Integer integer, Integer integer2, Integer integer3, Integer integer4, IsoChunk chunk) {
		IsoLot lot;
		if (pool.isEmpty()) {
			lot = new IsoLot(integer, integer2, integer3, integer4, chunk);
			lot.load(integer, integer2, integer3, integer4, chunk);
			return lot;
		} else {
			lot = (IsoLot)pool.pop();
			lot.arrays.clear();
			lot.load(integer, integer2, integer3, integer4, chunk);
			return lot;
		}
	}

	public void load(Integer integer, Integer integer2, Integer integer3, Integer integer4, IsoChunk chunk) {
		String string = ChunkMapFilenames.instance.getHeader(integer, integer2);
		this.info = (LotHeader)InfoHeaders.get(string);
		this.wx = integer3;
		this.wy = integer4;
		chunk.lotheader = this.info;
		if (this.data == null) {
			this.data = new Integer[10][10][this.info.levels][];
		}

		try {
			string = "world_" + integer + "_" + integer2 + ".lotpack";
			File file = new File((String)InfoFileNames.get(string));
			if (file.exists()) {
			}

			if (this.in == null || !lastUsedPath.equals(file.getAbsolutePath())) {
				if (this.in != null) {
					this.in.close();
				}

				this.in = new RandomAccessFile(file.getAbsolutePath(), "r");
				lastUsedPath = file.getAbsolutePath();
			}

			int int1 = 0;
			int int2 = this.wx - integer * 30;
			int int3 = this.wy - integer2 * 30;
			int int4 = int2 * 30 + int3;
			this.in.seek((long)(4 + int4 * 8));
			int int5 = readInt(this.in);
			this.in.seek((long)int5);
			for (int int6 = 0; int6 < this.info.levels; ++int6) {
				for (int int7 = 0; int7 < 10; ++int7) {
					for (int int8 = 0; int8 < 10; ++int8) {
						if (int1 > 0) {
							--int1;
							this.data[int7][int8][int6] = null;
						} else {
							int int9 = readInt(this.in);
							if (int9 == -1) {
								int1 = readInt(this.in);
								if (int1 > 0) {
									--int1;
									this.data[int7][int8][int6] = null;
									continue;
								}
							}

							if (int9 > 1) {
								this.data[int7][int8][int6] = IntArrayCache.instance.get(int9 - 1);
								this.arrays.add(this.data[int7][int8][int6]);
								int int10 = readInt(this.in);
								for (int int11 = 1; int11 < int9; ++int11) {
									int int12 = readInt(this.in);
									this.data[int7][int8][int6][int11 - 1] = int12;
								}
							} else {
								this.data[int7][int8][int6] = null;
							}
						}
					}
				}
			}
		} catch (Exception exception) {
		}
	}

	public IsoLot(Integer integer, Integer integer2, Integer integer3, Integer integer4, IsoChunk chunk) {
	}

	public class Zone {
		public String name;
		public String val;
		public int x;
		public int y;
		public int z;
		public int w;
		public int h;
	}
}
