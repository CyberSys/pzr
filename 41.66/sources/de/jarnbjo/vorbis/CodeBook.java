package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import de.jarnbjo.util.io.HuffmanNode;
import java.io.IOException;
import java.util.Arrays;


class CodeBook {
	private HuffmanNode huffmanRoot;
	private int dimensions;
	private int entries;
	private int[] entryLengths;
	private float[][] valueVector;
	private static long totalTime = 0L;

	protected CodeBook(BitInputStream bitInputStream) throws VorbisFormatException, IOException {
		if (bitInputStream.getInt(24) != 5653314) {
			throw new VorbisFormatException("The code book sync pattern is not correct.");
		} else {
			this.dimensions = bitInputStream.getInt(16);
			this.entries = bitInputStream.getInt(24);
			this.entryLengths = new int[this.entries];
			boolean boolean1 = bitInputStream.getBit();
			int int1;
			int int2;
			if (boolean1) {
				int1 = bitInputStream.getInt(5) + 1;
				int int3;
				for (int2 = 0; int2 < this.entryLengths.length; int2 += int3) {
					int3 = bitInputStream.getInt(Util.ilog(this.entryLengths.length - int2));
					if (int2 + int3 > this.entryLengths.length) {
						throw new VorbisFormatException("The codebook entry length list is longer than the actual number of entry lengths.");
					}

					Arrays.fill(this.entryLengths, int2, int2 + int3, int1);
					++int1;
				}
			} else {
				boolean boolean2 = bitInputStream.getBit();
				if (boolean2) {
					for (int2 = 0; int2 < this.entryLengths.length; ++int2) {
						if (bitInputStream.getBit()) {
							this.entryLengths[int2] = bitInputStream.getInt(5) + 1;
						} else {
							this.entryLengths[int2] = -1;
						}
					}
				} else {
					for (int2 = 0; int2 < this.entryLengths.length; ++int2) {
						this.entryLengths[int2] = bitInputStream.getInt(5) + 1;
					}
				}
			}

			if (!this.createHuffmanTree(this.entryLengths)) {
				throw new VorbisFormatException("An exception was thrown when building the codebook Huffman tree.");
			} else {
				int1 = bitInputStream.getInt(4);
				switch (int1) {
				case 1: 
				
				case 2: 
					float float1 = Util.float32unpack(bitInputStream.getInt(32));
					float float2 = Util.float32unpack(bitInputStream.getInt(32));
					int int4 = bitInputStream.getInt(4) + 1;
					boolean boolean3 = bitInputStream.getBit();
					boolean boolean4 = false;
					int int5;
					if (int1 == 1) {
						int5 = Util.lookup1Values(this.entries, this.dimensions);
					} else {
						int5 = this.entries * this.dimensions;
					}

					int[] intArray = new int[int5];
					int int6;
					for (int6 = 0; int6 < intArray.length; ++int6) {
						intArray[int6] = bitInputStream.getInt(int4);
					}

					this.valueVector = new float[this.entries][this.dimensions];
					if (int1 != 1) {
						throw new UnsupportedOperationException();
					} else {
						for (int6 = 0; int6 < this.entries; ++int6) {
							float float3 = 0.0F;
							int int7 = 1;
							for (int int8 = 0; int8 < this.dimensions; ++int8) {
								int int9 = int6 / int7 % int5;
								this.valueVector[int6][int8] = (float)intArray[int9] * float2 + float1 + float3;
								if (boolean3) {
									float3 = this.valueVector[int6][int8];
								}

								int7 *= int5;
							}
						}
					}

				
				case 0: 
					return;
				
				default: 
					throw new VorbisFormatException("Unsupported codebook lookup type: " + int1);
				
				}
			}
		}
	}

	private boolean createHuffmanTree(int[] intArray) {
		this.huffmanRoot = new HuffmanNode();
		for (int int1 = 0; int1 < intArray.length; ++int1) {
			int int2 = intArray[int1];
			if (int2 > 0 && !this.huffmanRoot.setNewValue(int2, int1)) {
				return false;
			}
		}

		return true;
	}

	protected int getDimensions() {
		return this.dimensions;
	}

	protected int getEntries() {
		return this.entries;
	}

	protected HuffmanNode getHuffmanRoot() {
		return this.huffmanRoot;
	}

	protected int readInt(BitInputStream bitInputStream) throws IOException {
		return bitInputStream.getInt(this.huffmanRoot);
	}

	protected void readVvAdd(float[][] floatArrayArray, BitInputStream bitInputStream, int int1, int int2) throws VorbisFormatException, IOException {
		int int3 = 0;
		int int4 = floatArrayArray.length;
		if (int4 != 0) {
			int int5 = (int1 + int2) / int4;
			int int6 = int1 / int4;
			while (int6 < int5) {
				float[] floatArray = this.valueVector[bitInputStream.getInt(this.huffmanRoot)];
				for (int int7 = 0; int7 < this.dimensions; ++int7) {
					int int8 = int3++;
					floatArrayArray[int8][int6] += floatArray[int7];
					if (int3 == int4) {
						int3 = 0;
						++int6;
					}
				}
			}
		}
	}
}
