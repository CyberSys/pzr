package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;


class SetupHeader {
	private static final long HEADER = 126896460427126L;
	private CodeBook[] codeBooks;
	private Floor[] floors;
	private Residue[] residues;
	private Mapping[] mappings;
	private Mode[] modes;

	public SetupHeader(VorbisStream vorbisStream, BitInputStream bitInputStream) throws VorbisFormatException, IOException {
		if (bitInputStream.getLong(48) != 126896460427126L) {
			throw new VorbisFormatException("The setup header has an illegal leading.");
		} else {
			int int1 = bitInputStream.getInt(8) + 1;
			this.codeBooks = new CodeBook[int1];
			int int2;
			for (int2 = 0; int2 < this.codeBooks.length; ++int2) {
				this.codeBooks[int2] = new CodeBook(bitInputStream);
			}

			int2 = bitInputStream.getInt(6) + 1;
			int int3;
			for (int3 = 0; int3 < int2; ++int3) {
				if (bitInputStream.getInt(16) != 0) {
					throw new VorbisFormatException("Time domain transformation != 0");
				}
			}

			int3 = bitInputStream.getInt(6) + 1;
			this.floors = new Floor[int3];
			int int4;
			for (int4 = 0; int4 < int3; ++int4) {
				this.floors[int4] = Floor.createInstance(bitInputStream, this);
			}

			int4 = bitInputStream.getInt(6) + 1;
			this.residues = new Residue[int4];
			int int5;
			for (int5 = 0; int5 < int4; ++int5) {
				this.residues[int5] = Residue.createInstance(bitInputStream, this);
			}

			int5 = bitInputStream.getInt(6) + 1;
			this.mappings = new Mapping[int5];
			int int6;
			for (int6 = 0; int6 < int5; ++int6) {
				this.mappings[int6] = Mapping.createInstance(vorbisStream, bitInputStream, this);
			}

			int6 = bitInputStream.getInt(6) + 1;
			this.modes = new Mode[int6];
			for (int int7 = 0; int7 < int6; ++int7) {
				this.modes[int7] = new Mode(bitInputStream, this);
			}

			if (!bitInputStream.getBit()) {
				throw new VorbisFormatException("The setup header framing bit is incorrect.");
			}
		}
	}

	public CodeBook[] getCodeBooks() {
		return this.codeBooks;
	}

	public Floor[] getFloors() {
		return this.floors;
	}

	public Residue[] getResidues() {
		return this.residues;
	}

	public Mapping[] getMappings() {
		return this.mappings;
	}

	public Mode[] getModes() {
		return this.modes;
	}
}
