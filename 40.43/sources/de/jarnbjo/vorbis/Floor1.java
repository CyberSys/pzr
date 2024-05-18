package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


class Floor1 extends Floor implements Cloneable {
	private int[] partitionClassList;
	private int maximumClass;
	private int multiplier;
	private int rangeBits;
	private int[] classDimensions;
	private int[] classSubclasses;
	private int[] classMasterbooks;
	private int[][] subclassBooks;
	private int[] xList;
	private int[] yList;
	private int[] lowNeighbours;
	private int[] highNeighbours;
	private static final int[] RANGES = new int[]{256, 128, 86, 64};

	private Floor1() {
	}

	protected Floor1(BitInputStream bitInputStream, SetupHeader setupHeader) throws VorbisFormatException, IOException {
		this.maximumClass = -1;
		int int1 = bitInputStream.getInt(5);
		this.partitionClassList = new int[int1];
		int int2;
		for (int2 = 0; int2 < this.partitionClassList.length; ++int2) {
			this.partitionClassList[int2] = bitInputStream.getInt(4);
			if (this.partitionClassList[int2] > this.maximumClass) {
				this.maximumClass = this.partitionClassList[int2];
			}
		}

		this.classDimensions = new int[this.maximumClass + 1];
		this.classSubclasses = new int[this.maximumClass + 1];
		this.classMasterbooks = new int[this.maximumClass + 1];
		this.subclassBooks = new int[this.maximumClass + 1][];
		int2 = 2;
		for (int int3 = 0; int3 <= this.maximumClass; ++int3) {
			this.classDimensions[int3] = bitInputStream.getInt(3) + 1;
			int2 += this.classDimensions[int3];
			this.classSubclasses[int3] = bitInputStream.getInt(2);
			if (this.classDimensions[int3] > setupHeader.getCodeBooks().length || this.classSubclasses[int3] > setupHeader.getCodeBooks().length) {
				throw new VorbisFormatException("There is a class dimension or class subclasses entry higher than the number of codebooks in the setup header.");
			}

			if (this.classSubclasses[int3] != 0) {
				this.classMasterbooks[int3] = bitInputStream.getInt(8);
			}

			this.subclassBooks[int3] = new int[1 << this.classSubclasses[int3]];
			for (int int4 = 0; int4 < this.subclassBooks[int3].length; ++int4) {
				this.subclassBooks[int3][int4] = bitInputStream.getInt(8) - 1;
			}
		}

		this.multiplier = bitInputStream.getInt(2) + 1;
		this.rangeBits = bitInputStream.getInt(4);
		boolean boolean1 = false;
		ArrayList arrayList = new ArrayList();
		arrayList.add(new Integer(0));
		arrayList.add(new Integer(1 << this.rangeBits));
		int int5;
		for (int int6 = 0; int6 < int1; ++int6) {
			for (int5 = 0; int5 < this.classDimensions[this.partitionClassList[int6]]; ++int5) {
				arrayList.add(new Integer(bitInputStream.getInt(this.rangeBits)));
			}
		}

		this.xList = new int[arrayList.size()];
		this.lowNeighbours = new int[this.xList.length];
		this.highNeighbours = new int[this.xList.length];
		Iterator iterator = arrayList.iterator();
		for (int5 = 0; int5 < this.xList.length; ++int5) {
			this.xList[int5] = (Integer)iterator.next();
		}

		for (int5 = 0; int5 < this.xList.length; ++int5) {
			this.lowNeighbours[int5] = Util.lowNeighbour(this.xList, int5);
			this.highNeighbours[int5] = Util.highNeighbour(this.xList, int5);
		}
	}

	protected int getType() {
		return 1;
	}

	protected Floor decodeFloor(VorbisStream vorbisStream, BitInputStream bitInputStream) throws VorbisFormatException, IOException {
		if (!bitInputStream.getBit()) {
			return null;
		} else {
			Floor1 floor1 = (Floor1)this.clone();
			floor1.yList = new int[this.xList.length];
			int int1 = RANGES[this.multiplier - 1];
			floor1.yList[0] = bitInputStream.getInt(Util.ilog(int1 - 1));
			floor1.yList[1] = bitInputStream.getInt(Util.ilog(int1 - 1));
			int int2 = 2;
			for (int int3 = 0; int3 < this.partitionClassList.length; ++int3) {
				int int4 = this.partitionClassList[int3];
				int int5 = this.classDimensions[int4];
				int int6 = this.classSubclasses[int4];
				int int7 = (1 << int6) - 1;
				int int8 = 0;
				if (int6 > 0) {
					int8 = bitInputStream.getInt(vorbisStream.getSetupHeader().getCodeBooks()[this.classMasterbooks[int4]].getHuffmanRoot());
				}

				for (int int9 = 0; int9 < int5; ++int9) {
					int int10 = this.subclassBooks[int4][int8 & int7];
					int8 >>>= int6;
					if (int10 >= 0) {
						floor1.yList[int9 + int2] = bitInputStream.getInt(vorbisStream.getSetupHeader().getCodeBooks()[int10].getHuffmanRoot());
					} else {
						floor1.yList[int9 + int2] = 0;
					}
				}

				int2 += int5;
			}

			return floor1;
		}
	}

	protected void computeFloor(float[] floatArray) {
		int int1 = floatArray.length;
		int int2 = this.xList.length;
		boolean[] booleanArray = new boolean[int2];
		int int3 = RANGES[this.multiplier - 1];
		int int4;
		int int5;
		int int6;
		int int7;
		int int8;
		for (int int9 = 2; int9 < int2; ++int9) {
			int4 = this.lowNeighbours[int9];
			int5 = this.highNeighbours[int9];
			int6 = Util.renderPoint(this.xList[int4], this.xList[int5], this.yList[int4], this.yList[int5], this.xList[int9]);
			int7 = this.yList[int9];
			int int10 = int3 - int6;
			int8 = int10 < int6 ? int10 * 2 : int6 * 2;
			if (int7 != 0) {
				booleanArray[int4] = true;
				booleanArray[int5] = true;
				booleanArray[int9] = true;
				if (int7 >= int8) {
					this.yList[int9] = int10 > int6 ? int7 - int6 + int6 : -int7 + int10 + int6 - 1;
				} else {
					this.yList[int9] = (int7 & 1) == 1 ? int6 - (int7 + 1 >> 1) : int6 + (int7 >> 1);
				}
			} else {
				booleanArray[int9] = false;
				this.yList[int9] = int6;
			}
		}

		int[] intArray = new int[int2];
		System.arraycopy(this.xList, 0, intArray, 0, int2);
		sort(intArray, this.yList, booleanArray);
		int4 = 0;
		int5 = 0;
		int6 = 0;
		int7 = this.yList[0] * this.multiplier;
		float[] floatArray2 = new float[floatArray.length];
		float[] floatArray3 = new float[floatArray.length];
		Arrays.fill(floatArray2, 1.0F);
		System.arraycopy(floatArray, 0, floatArray3, 0, floatArray.length);
		for (int8 = 1; int8 < int2; ++int8) {
			if (booleanArray[int8]) {
				int5 = this.yList[int8] * this.multiplier;
				int4 = intArray[int8];
				Util.renderLine(int6, int7, int4, int5, floatArray);
				Util.renderLine(int6, int7, int4, int5, floatArray2);
				int6 = int4;
				int7 = int5;
			}
		}

		for (float float1 = DB_STATIC_TABLE[int5]; int4 < int1 / 2; floatArray[int4++] = float1) {
		}
	}

	public Object clone() {
		Floor1 floor1 = new Floor1();
		floor1.classDimensions = this.classDimensions;
		floor1.classMasterbooks = this.classMasterbooks;
		floor1.classSubclasses = this.classSubclasses;
		floor1.maximumClass = this.maximumClass;
		floor1.multiplier = this.multiplier;
		floor1.partitionClassList = this.partitionClassList;
		floor1.rangeBits = this.rangeBits;
		floor1.subclassBooks = this.subclassBooks;
		floor1.xList = this.xList;
		floor1.yList = this.yList;
		floor1.lowNeighbours = this.lowNeighbours;
		floor1.highNeighbours = this.highNeighbours;
		return floor1;
	}

	private static final void sort(int[] intArray, int[] intArray2, boolean[] booleanArray) {
		byte byte1 = 0;
		int int1 = intArray.length;
		int int2 = int1 + byte1;
		for (int int3 = byte1; int3 < int2; ++int3) {
			for (int int4 = int3; int4 > byte1 && intArray[int4 - 1] > intArray[int4]; --int4) {
				int int5 = intArray[int4];
				intArray[int4] = intArray[int4 - 1];
				intArray[int4 - 1] = int5;
				int5 = intArray2[int4];
				intArray2[int4] = intArray2[int4 - 1];
				intArray2[int4 - 1] = int5;
				boolean boolean1 = booleanArray[int4];
				booleanArray[int4] = booleanArray[int4 - 1];
				booleanArray[int4 - 1] = boolean1;
			}
		}
	}

	private static final void swap(int[] intArray, int int1, int int2) {
		int int3 = intArray[int1];
		intArray[int1] = intArray[int2];
		intArray[int2] = int3;
	}

	private static final void swap(boolean[] booleanArray, int int1, int int2) {
		boolean boolean1 = booleanArray[int1];
		booleanArray[int1] = booleanArray[int2];
		booleanArray[int2] = boolean1;
	}
}
