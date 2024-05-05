package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;


abstract class Mapping {

	protected static Mapping createInstance(VorbisStream vorbisStream, BitInputStream bitInputStream, SetupHeader setupHeader) throws VorbisFormatException, IOException {
		int int1 = bitInputStream.getInt(16);
		switch (int1) {
		case 0: 
			return new Mapping0(vorbisStream, bitInputStream, setupHeader);
		
		default: 
			throw new VorbisFormatException("Mapping type " + int1 + " is not supported.");
		
		}
	}

	protected abstract int getType();

	protected abstract int[] getAngles();

	protected abstract int[] getMagnitudes();

	protected abstract int[] getMux();

	protected abstract int[] getSubmapFloors();

	protected abstract int[] getSubmapResidues();

	protected abstract int getCouplingSteps();

	protected abstract int getSubmaps();
}
