package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;


class AudioPacket {
	private int modeNumber;
	private Mode mode;
	private Mapping mapping;
	private int n;
	private boolean blockFlag;
	private boolean previousWindowFlag;
	private boolean nextWindowFlag;
	private int windowCenter;
	private int leftWindowStart;
	private int leftWindowEnd;
	private int leftN;
	private int rightWindowStart;
	private int rightWindowEnd;
	private int rightN;
	private float[] window;
	private float[][] pcm;
	private int[][] pcmInt;
	private Floor[] channelFloors;
	private boolean[] noResidues;
	private static final float[][] windows = new float[8][];

	protected AudioPacket(VorbisStream vorbisStream, BitInputStream bitInputStream) throws VorbisFormatException, IOException {
		SetupHeader setupHeader = vorbisStream.getSetupHeader();
		IdentificationHeader identificationHeader = vorbisStream.getIdentificationHeader();
		Mode[] modeArray = setupHeader.getModes();
		Mapping[] mappingArray = setupHeader.getMappings();
		Residue[] residueArray = setupHeader.getResidues();
		int int1 = identificationHeader.getChannels();
		if (bitInputStream.getInt(1) != 0) {
			throw new VorbisFormatException("Packet type mismatch when trying to create an audio packet.");
		} else {
			this.modeNumber = bitInputStream.getInt(Util.ilog(modeArray.length - 1));
			try {
				this.mode = modeArray[this.modeNumber];
			} catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
				throw new VorbisFormatException("Reference to invalid mode in audio packet.");
			}

			this.mapping = mappingArray[this.mode.getMapping()];
			int[] intArray = this.mapping.getMagnitudes();
			int[] intArray2 = this.mapping.getAngles();
			this.blockFlag = this.mode.getBlockFlag();
			int int2 = identificationHeader.getBlockSize0();
			int int3 = identificationHeader.getBlockSize1();
			this.n = this.blockFlag ? int3 : int2;
			if (this.blockFlag) {
				this.previousWindowFlag = bitInputStream.getBit();
				this.nextWindowFlag = bitInputStream.getBit();
			}

			this.windowCenter = this.n / 2;
			if (this.blockFlag && !this.previousWindowFlag) {
				this.leftWindowStart = this.n / 4 - int2 / 4;
				this.leftWindowEnd = this.n / 4 + int2 / 4;
				this.leftN = int2 / 2;
			} else {
				this.leftWindowStart = 0;
				this.leftWindowEnd = this.n / 2;
				this.leftN = this.windowCenter;
			}

			if (this.blockFlag && !this.nextWindowFlag) {
				this.rightWindowStart = this.n * 3 / 4 - int2 / 4;
				this.rightWindowEnd = this.n * 3 / 4 + int2 / 4;
				this.rightN = int2 / 2;
			} else {
				this.rightWindowStart = this.windowCenter;
				this.rightWindowEnd = this.n;
				this.rightN = this.n / 2;
			}

			this.window = this.getComputedWindow();
			this.channelFloors = new Floor[int1];
			this.noResidues = new boolean[int1];
			this.pcm = new float[int1][this.n];
			this.pcmInt = new int[int1][this.n];
			boolean boolean1 = true;
			int int4;
			int int5;
			int int6;
			for (int4 = 0; int4 < int1; ++int4) {
				int5 = this.mapping.getMux()[int4];
				int6 = this.mapping.getSubmapFloors()[int5];
				Floor floor = setupHeader.getFloors()[int6].decodeFloor(vorbisStream, bitInputStream);
				this.channelFloors[int4] = floor;
				this.noResidues[int4] = floor == null;
				if (floor != null) {
					boolean1 = false;
				}
			}

			if (!boolean1) {
				for (int4 = 0; int4 < intArray.length; ++int4) {
					if (!this.noResidues[intArray[int4]] || !this.noResidues[intArray2[int4]]) {
						this.noResidues[intArray[int4]] = false;
						this.noResidues[intArray2[int4]] = false;
					}
				}

				Residue[] residueArray2 = new Residue[this.mapping.getSubmaps()];
				for (int5 = 0; int5 < this.mapping.getSubmaps(); ++int5) {
					int6 = 0;
					boolean[] booleanArray = new boolean[int1];
					int int7;
					for (int7 = 0; int7 < int1; ++int7) {
						if (this.mapping.getMux()[int7] == int5) {
							booleanArray[int6++] = this.noResidues[int7];
						}
					}

					int7 = this.mapping.getSubmapResidues()[int5];
					Residue residue = residueArray[int7];
					residue.decodeResidue(vorbisStream, bitInputStream, this.mode, int6, booleanArray, this.pcm);
				}

				for (int5 = this.mapping.getCouplingSteps() - 1; int5 >= 0; --int5) {
					double double1 = 0.0;
					double double2 = 0.0;
					float[] floatArray = this.pcm[intArray[int5]];
					float[] floatArray2 = this.pcm[intArray2[int5]];
					for (int int8 = 0; int8 < floatArray.length; ++int8) {
						float float1 = floatArray2[int8];
						float float2 = floatArray[int8];
						if (float1 > 0.0F) {
							floatArray2[int8] = float2 > 0.0F ? float2 - float1 : float2 + float1;
						} else {
							floatArray[int8] = float2 > 0.0F ? float2 + float1 : float2 - float1;
							floatArray2[int8] = float2;
						}
					}
				}

				for (int5 = 0; int5 < int1; ++int5) {
					if (this.channelFloors[int5] != null) {
						this.channelFloors[int5].computeFloor(this.pcm[int5]);
					}
				}

				for (int5 = 0; int5 < int1; ++int5) {
					MdctFloat mdctFloat = this.blockFlag ? identificationHeader.getMdct1() : identificationHeader.getMdct0();
					mdctFloat.imdct(this.pcm[int5], this.window, this.pcmInt[int5]);
				}
			}
		}
	}

	private float[] getComputedWindow() {
		int int1 = (this.blockFlag ? 4 : 0) + (this.previousWindowFlag ? 2 : 0) + (this.nextWindowFlag ? 1 : 0);
		float[] floatArray = windows[int1];
		if (floatArray == null) {
			floatArray = new float[this.n];
			int int2;
			float float1;
			for (int2 = 0; int2 < this.leftN; ++int2) {
				float1 = (float)(((double)int2 + 0.5) / (double)this.leftN * 3.141592653589793 / 2.0);
				float1 = (float)Math.sin((double)float1);
				float1 *= float1;
				float1 = (float)((double)float1 * 1.5707963705062866);
				float1 = (float)Math.sin((double)float1);
				floatArray[int2 + this.leftWindowStart] = float1;
			}

			for (int2 = this.leftWindowEnd; int2 < this.rightWindowStart; floatArray[int2++] = 1.0F) {
			}

			for (int2 = 0; int2 < this.rightN; ++int2) {
				float1 = (float)(((double)(this.rightN - int2) - 0.5) / (double)this.rightN * 3.141592653589793 / 2.0);
				float1 = (float)Math.sin((double)float1);
				float1 *= float1;
				float1 = (float)((double)float1 * 1.5707963705062866);
				float1 = (float)Math.sin((double)float1);
				floatArray[int2 + this.rightWindowStart] = float1;
			}

			windows[int1] = floatArray;
		}

		return floatArray;
	}

	protected int getNumberOfSamples() {
		return this.rightWindowStart - this.leftWindowStart;
	}

	protected int getPcm(AudioPacket audioPacket, int[][] intArrayArray) {
		int int1 = this.pcm.length;
		int int2;
		for (int2 = 0; int2 < int1; ++int2) {
			int int3 = 0;
			int int4 = audioPacket.rightWindowStart;
			int[] intArray = audioPacket.pcmInt[int2];
			int[] intArray2 = this.pcmInt[int2];
			int[] intArray3 = intArrayArray[int2];
			for (int int5 = this.leftWindowStart; int5 < this.leftWindowEnd; ++int5) {
				int int6 = intArray[int4++] + intArray2[int5];
				if (int6 > 32767) {
					int6 = 32767;
				}

				if (int6 < -32768) {
					int6 = -32768;
				}

				intArray3[int3++] = int6;
			}
		}

		if (this.leftWindowEnd + 1 < this.rightWindowStart) {
			for (int2 = 0; int2 < int1; ++int2) {
				System.arraycopy(this.pcmInt[int2], this.leftWindowEnd, intArrayArray[int2], this.leftWindowEnd - this.leftWindowStart, this.rightWindowStart - this.leftWindowEnd);
			}
		}

		return this.rightWindowStart - this.leftWindowStart;
	}

	protected void getPcm(AudioPacket audioPacket, byte[] byteArray) {
		int int1 = this.pcm.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			int int3 = 0;
			int int4 = audioPacket.rightWindowStart;
			int[] intArray = audioPacket.pcmInt[int2];
			int[] intArray2 = this.pcmInt[int2];
			int int5;
			int int6;
			for (int6 = this.leftWindowStart; int6 < this.leftWindowEnd; ++int6) {
				int5 = intArray[int4++] + intArray2[int6];
				if (int5 > 32767) {
					int5 = 32767;
				}

				if (int5 < -32768) {
					int5 = -32768;
				}

				byteArray[int3 + int2 * 2 + 1] = (byte)(int5 & 255);
				byteArray[int3 + int2 * 2] = (byte)(int5 >> 8 & 255);
				int3 += int1 * 2;
			}

			int3 = (this.leftWindowEnd - this.leftWindowStart) * int1 * 2;
			for (int6 = this.leftWindowEnd; int6 < this.rightWindowStart; ++int6) {
				int5 = intArray2[int6];
				if (int5 > 32767) {
					int5 = 32767;
				}

				if (int5 < -32768) {
					int5 = -32768;
				}

				byteArray[int3 + int2 * 2 + 1] = (byte)(int5 & 255);
				byteArray[int3 + int2 * 2] = (byte)(int5 >> 8 & 255);
				int3 += int1 * 2;
			}
		}
	}

	protected float[] getWindow() {
		return this.window;
	}

	protected int getLeftWindowStart() {
		return this.leftWindowStart;
	}

	protected int getLeftWindowEnd() {
		return this.leftWindowEnd;
	}

	protected int getRightWindowStart() {
		return this.rightWindowStart;
	}

	protected int getRightWindowEnd() {
		return this.rightWindowEnd;
	}

	public int[][] getPcm() {
		return this.pcmInt;
	}

	public float[][] getFreqencyDomain() {
		return this.pcm;
	}
}
