package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;


class Residue2 extends Residue {
	private double[][] decodedVectors;

	private Residue2() {
	}

	protected Residue2(BitInputStream bitInputStream, SetupHeader setupHeader) throws VorbisFormatException, IOException {
		super(bitInputStream, setupHeader);
	}

	protected int getType() {
		return 2;
	}

	protected void decodeResidue(VorbisStream vorbisStream, BitInputStream bitInputStream, Mode mode, int int1, boolean[] booleanArray, float[][] floatArrayArray) throws VorbisFormatException, IOException {
		Residue.Look look = this.getLook(vorbisStream, mode);
		CodeBook codeBook = vorbisStream.getSetupHeader().getCodeBooks()[this.getClassBook()];
		int int2 = codeBook.getDimensions();
		int int3 = this.getEnd() - this.getBegin();
		int int4 = int3 / this.getPartitionSize();
		int int5 = this.getPartitionSize();
		int int6 = look.getPhraseBook().getDimensions();
		int int7 = (int4 + int6 - 1) / int6;
		int int8 = 0;
		for (int int9 = 0; int9 < booleanArray.length; ++int9) {
			if (!booleanArray[int9]) {
				++int8;
			}
		}

		float[][] floatArrayArray2 = new float[int8][];
		int8 = 0;
		for (int int10 = 0; int10 < booleanArray.length; ++int10) {
			if (!booleanArray[int10]) {
				floatArrayArray2[int8++] = floatArrayArray[int10];
			}
		}

		int[][] intArrayArray = new int[int7][];
		for (int int11 = 0; int11 < look.getStages(); ++int11) {
			int int12 = 0;
			for (int int13 = 0; int12 < int4; ++int13) {
				int int14;
				if (int11 == 0) {
					int14 = bitInputStream.getInt(look.getPhraseBook().getHuffmanRoot());
					if (int14 == -1) {
						throw new VorbisFormatException("");
					}

					intArrayArray[int13] = look.getDecodeMap()[int14];
					if (intArrayArray[int13] == null) {
						throw new VorbisFormatException("");
					}
				}

				for (int14 = 0; int14 < int6 && int12 < int4; ++int12) {
					int int15 = this.begin + int12 * int5;
					if ((this.cascade[intArrayArray[int13][int14]] & 1 << int11) != 0) {
						CodeBook codeBook2 = vorbisStream.getSetupHeader().getCodeBooks()[look.getPartBooks()[intArrayArray[int13][int14]][int11]];
						if (codeBook2 != null) {
							codeBook2.readVvAdd(floatArrayArray2, bitInputStream, int15, int5);
						}
					}

					++int14;
				}
			}
		}
	}

	public Object clone() {
		Residue2 residue2 = new Residue2();
		this.fill(residue2);
		return residue2;
	}

	protected double[][] getDecodedVectors() {
		return this.decodedVectors;
	}
}
