package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;


class Mapping0 extends Mapping {
	private int[] magnitudes;
	private int[] angles;
	private int[] mux;
	private int[] submapFloors;
	private int[] submapResidues;

	protected Mapping0(VorbisStream vorbisStream, BitInputStream bitInputStream, SetupHeader setupHeader) throws VorbisFormatException, IOException {
		int int1 = 1;
		if (bitInputStream.getBit()) {
			int1 = bitInputStream.getInt(4) + 1;
		}

		int int2 = vorbisStream.getIdentificationHeader().getChannels();
		int int3 = Util.ilog(int2 - 1);
		int int4;
		int int5;
		if (bitInputStream.getBit()) {
			int4 = bitInputStream.getInt(8) + 1;
			this.magnitudes = new int[int4];
			this.angles = new int[int4];
			for (int5 = 0; int5 < int4; ++int5) {
				this.magnitudes[int5] = bitInputStream.getInt(int3);
				this.angles[int5] = bitInputStream.getInt(int3);
				if (this.magnitudes[int5] == this.angles[int5] || this.magnitudes[int5] >= int2 || this.angles[int5] >= int2) {
					System.err.println(this.magnitudes[int5]);
					System.err.println(this.angles[int5]);
					throw new VorbisFormatException("The channel magnitude and/or angle mismatch.");
				}
			}
		} else {
			this.magnitudes = new int[0];
			this.angles = new int[0];
		}

		if (bitInputStream.getInt(2) != 0) {
			throw new VorbisFormatException("A reserved mapping field has an invalid value.");
		} else {
			this.mux = new int[int2];
			if (int1 > 1) {
				for (int4 = 0; int4 < int2; ++int4) {
					this.mux[int4] = bitInputStream.getInt(4);
					if (this.mux[int4] > int1) {
						throw new VorbisFormatException("A mapping mux value is higher than the number of submaps");
					}
				}
			} else {
				for (int4 = 0; int4 < int2; ++int4) {
					this.mux[int4] = 0;
				}
			}

			this.submapFloors = new int[int1];
			this.submapResidues = new int[int1];
			int4 = setupHeader.getFloors().length;
			int5 = setupHeader.getResidues().length;
			for (int int6 = 0; int6 < int1; ++int6) {
				bitInputStream.getInt(8);
				this.submapFloors[int6] = bitInputStream.getInt(8);
				this.submapResidues[int6] = bitInputStream.getInt(8);
				if (this.submapFloors[int6] > int4) {
					throw new VorbisFormatException("A mapping floor value is higher than the number of floors.");
				}

				if (this.submapResidues[int6] > int5) {
					throw new VorbisFormatException("A mapping residue value is higher than the number of residues.");
				}
			}
		}
	}

	protected int getType() {
		return 0;
	}

	protected int[] getAngles() {
		return this.angles;
	}

	protected int[] getMagnitudes() {
		return this.magnitudes;
	}

	protected int[] getMux() {
		return this.mux;
	}

	protected int[] getSubmapFloors() {
		return this.submapFloors;
	}

	protected int[] getSubmapResidues() {
		return this.submapResidues;
	}

	protected int getCouplingSteps() {
		return this.angles.length;
	}

	protected int getSubmaps() {
		return this.submapFloors.length;
	}
}
