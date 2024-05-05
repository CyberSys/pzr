package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;


class Residue0 extends Residue {

	protected Residue0(BitInputStream bitInputStream, SetupHeader setupHeader) throws VorbisFormatException, IOException {
		super(bitInputStream, setupHeader);
	}

	protected int getType() {
		return 0;
	}

	protected void decodeResidue(VorbisStream vorbisStream, BitInputStream bitInputStream, Mode mode, int int1, boolean[] booleanArray, float[][] floatArrayArray) throws VorbisFormatException, IOException {
		throw new UnsupportedOperationException();
	}
}
