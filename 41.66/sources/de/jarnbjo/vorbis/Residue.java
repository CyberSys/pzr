package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;
import java.util.HashMap;


abstract class Residue {
	protected int begin;
	protected int end;
	protected int partitionSize;
	protected int classifications;
	protected int classBook;
	protected int[] cascade;
	protected int[][] books;
	protected HashMap looks = new HashMap();

	protected Residue() {
	}

	protected Residue(BitInputStream bitInputStream, SetupHeader setupHeader) throws VorbisFormatException, IOException {
		this.begin = bitInputStream.getInt(24);
		this.end = bitInputStream.getInt(24);
		this.partitionSize = bitInputStream.getInt(24) + 1;
		this.classifications = bitInputStream.getInt(6) + 1;
		this.classBook = bitInputStream.getInt(8);
		this.cascade = new int[this.classifications];
		int int1 = 0;
		int int2;
		int int3;
		for (int2 = 0; int2 < this.classifications; ++int2) {
			int3 = 0;
			boolean boolean1 = false;
			int int4 = bitInputStream.getInt(3);
			if (bitInputStream.getBit()) {
				int3 = bitInputStream.getInt(5);
			}

			this.cascade[int2] = int3 << 3 | int4;
			int1 += Util.icount(this.cascade[int2]);
		}

		this.books = new int[this.classifications][8];
		for (int2 = 0; int2 < this.classifications; ++int2) {
			for (int3 = 0; int3 < 8; ++int3) {
				if ((this.cascade[int2] & 1 << int3) != 0) {
					this.books[int2][int3] = bitInputStream.getInt(8);
					if (this.books[int2][int3] > setupHeader.getCodeBooks().length) {
						throw new VorbisFormatException("Reference to invalid codebook entry in residue header.");
					}
				}
			}
		}
	}

	protected static Residue createInstance(BitInputStream bitInputStream, SetupHeader setupHeader) throws VorbisFormatException, IOException {
		int int1 = bitInputStream.getInt(16);
		switch (int1) {
		case 0: 
			return new Residue0(bitInputStream, setupHeader);
		
		case 1: 
			return new Residue2(bitInputStream, setupHeader);
		
		case 2: 
			return new Residue2(bitInputStream, setupHeader);
		
		default: 
			throw new VorbisFormatException("Residue type " + int1 + " is not supported.");
		
		}
	}

	protected abstract int getType();

	protected abstract void decodeResidue(VorbisStream vorbisStream, BitInputStream bitInputStream, Mode mode, int int1, boolean[] booleanArray, float[][] floatArrayArray) throws VorbisFormatException, IOException;

	protected int getBegin() {
		return this.begin;
	}

	protected int getEnd() {
		return this.end;
	}

	protected int getPartitionSize() {
		return this.partitionSize;
	}

	protected int getClassifications() {
		return this.classifications;
	}

	protected int getClassBook() {
		return this.classBook;
	}

	protected int[] getCascade() {
		return this.cascade;
	}

	protected int[][] getBooks() {
		return this.books;
	}

	protected final void fill(Residue residue) {
		residue.begin = this.begin;
		residue.books = this.books;
		residue.cascade = this.cascade;
		residue.classBook = this.classBook;
		residue.classifications = this.classifications;
		residue.end = this.end;
		residue.partitionSize = this.partitionSize;
	}

	protected Residue.Look getLook(VorbisStream vorbisStream, Mode mode) {
		Residue.Look look = (Residue.Look)this.looks.get(mode);
		if (look == null) {
			look = new Residue.Look(vorbisStream, mode);
			this.looks.put(mode, look);
		}

		return look;
	}

	class Look {
		int map;
		int parts;
		int stages;
		CodeBook[] fullbooks;
		CodeBook phrasebook;
		int[][] partbooks;
		int partvals;
		int[][] decodemap;
		int postbits;
		int phrasebits;
		int frames;

		protected Look(VorbisStream vorbisStream, Mode mode) {
			boolean boolean1 = false;
			boolean boolean2 = false;
			int int1 = 0;
			this.map = mode.getMapping();
			this.parts = Residue.this.getClassifications();
			this.fullbooks = vorbisStream.getSetupHeader().getCodeBooks();
			this.phrasebook = this.fullbooks[Residue.this.getClassBook()];
			int int2 = this.phrasebook.getDimensions();
			this.partbooks = new int[this.parts][];
			int int3;
			int int4;
			int int5;
			for (int3 = 0; int3 < this.parts; ++int3) {
				int4 = Util.ilog(Residue.this.getCascade()[int3]);
				if (int4 != 0) {
					if (int4 > int1) {
						int1 = int4;
					}

					this.partbooks[int3] = new int[int4];
					for (int5 = 0; int5 < int4; ++int5) {
						if ((Residue.this.getCascade()[int3] & 1 << int5) != 0) {
							this.partbooks[int3][int5] = Residue.this.getBooks()[int3][int5];
						}
					}
				}
			}

			this.partvals = (int)Math.rint(Math.pow((double)this.parts, (double)int2));
			this.stages = int1;
			this.decodemap = new int[this.partvals][];
			for (int3 = 0; int3 < this.partvals; ++int3) {
				int4 = int3;
				int5 = this.partvals / this.parts;
				this.decodemap[int3] = new int[int2];
				for (int int6 = 0; int6 < int2; ++int6) {
					int int7 = int4 / int5;
					int4 -= int7 * int5;
					int5 /= this.parts;
					this.decodemap[int3][int6] = int7;
				}
			}
		}

		protected int[][] getDecodeMap() {
			return this.decodemap;
		}

		protected int getFrames() {
			return this.frames;
		}

		protected int getMap() {
			return this.map;
		}

		protected int[][] getPartBooks() {
			return this.partbooks;
		}

		protected int getParts() {
			return this.parts;
		}

		protected int getPartVals() {
			return this.partvals;
		}

		protected int getPhraseBits() {
			return this.phrasebits;
		}

		protected CodeBook getPhraseBook() {
			return this.phrasebook;
		}

		protected int getPostBits() {
			return this.postbits;
		}

		protected int getStages() {
			return this.stages;
		}
	}
}
