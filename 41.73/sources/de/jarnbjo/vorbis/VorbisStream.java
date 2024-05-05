package de.jarnbjo.vorbis;

import de.jarnbjo.ogg.LogicalOggStream;
import de.jarnbjo.util.io.ByteArrayBitInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;


public class VorbisStream {
	private LogicalOggStream oggStream;
	private IdentificationHeader identificationHeader;
	private CommentHeader commentHeader;
	private SetupHeader setupHeader;
	private AudioPacket lastAudioPacket;
	private AudioPacket nextAudioPacket;
	private LinkedList audioPackets = new LinkedList();
	private byte[] currentPcm;
	private int currentPcmIndex;
	private int currentPcmLimit;
	private static final int IDENTIFICATION_HEADER = 1;
	private static final int COMMENT_HEADER = 3;
	private static final int SETUP_HEADER = 5;
	private int bitIndex = 0;
	private byte lastByte = 0;
	private boolean initialized = false;
	private Object streamLock = new Object();
	private int pageCounter = 0;
	private int currentBitRate = 0;
	private long currentGranulePosition;
	public static final int BIG_ENDIAN = 0;
	public static final int LITTLE_ENDIAN = 1;

	public VorbisStream() {
	}

	public VorbisStream(LogicalOggStream logicalOggStream) throws VorbisFormatException, IOException {
		this.oggStream = logicalOggStream;
		for (int int1 = 0; int1 < 3; ++int1) {
			ByteArrayBitInputStream byteArrayBitInputStream = new ByteArrayBitInputStream(logicalOggStream.getNextOggPacket());
			int int2 = byteArrayBitInputStream.getInt(8);
			switch (int2) {
			case 1: 
				this.identificationHeader = new IdentificationHeader(byteArrayBitInputStream);
			
			case 2: 
			
			case 4: 
			
			default: 
				break;
			
			case 3: 
				this.commentHeader = new CommentHeader(byteArrayBitInputStream);
				break;
			
			case 5: 
				this.setupHeader = new SetupHeader(this, byteArrayBitInputStream);
			
			}
		}

		if (this.identificationHeader == null) {
			throw new VorbisFormatException("The file has no identification header.");
		} else if (this.commentHeader == null) {
			throw new VorbisFormatException("The file has no commentHeader.");
		} else if (this.setupHeader == null) {
			throw new VorbisFormatException("The file has no setup header.");
		} else {
			this.currentPcm = new byte[this.identificationHeader.getChannels() * this.identificationHeader.getBlockSize1() * 2];
		}
	}

	public IdentificationHeader getIdentificationHeader() {
		return this.identificationHeader;
	}

	public CommentHeader getCommentHeader() {
		return this.commentHeader;
	}

	protected SetupHeader getSetupHeader() {
		return this.setupHeader;
	}

	public boolean isOpen() {
		return this.oggStream.isOpen();
	}

	public void close() throws IOException {
		this.oggStream.close();
	}

	public int readPcm(byte[] byteArray, int int1, int int2) throws IOException {
		synchronized (this.streamLock) {
			int int3 = this.identificationHeader.getChannels();
			if (this.lastAudioPacket == null) {
				this.lastAudioPacket = this.getNextAudioPacket();
			}

			if (this.currentPcm == null || this.currentPcmIndex >= this.currentPcmLimit) {
				AudioPacket audioPacket = this.getNextAudioPacket();
				try {
					audioPacket.getPcm(this.lastAudioPacket, this.currentPcm);
					this.currentPcmLimit = audioPacket.getNumberOfSamples() * this.identificationHeader.getChannels() * 2;
				} catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
					return 0;
				}

				this.currentPcmIndex = 0;
				this.lastAudioPacket = audioPacket;
			}

			int int4 = 0;
			boolean boolean1 = false;
			int int5 = 0;
			int int6;
			for (int6 = this.currentPcmIndex; int6 < this.currentPcmLimit && int5 < int2; ++int6) {
				byteArray[int1 + int5++] = this.currentPcm[int6];
				++int4;
			}

			this.currentPcmIndex = int6;
			return int4;
		}
	}

	private AudioPacket getNextAudioPacket() throws VorbisFormatException, IOException {
		++this.pageCounter;
		byte[] byteArray = this.oggStream.getNextOggPacket();
		AudioPacket audioPacket = null;
		while (audioPacket == null) {
			try {
				audioPacket = new AudioPacket(this, new ByteArrayBitInputStream(byteArray));
			} catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
			}
		}

		this.currentGranulePosition += (long)audioPacket.getNumberOfSamples();
		this.currentBitRate = byteArray.length * 8 * this.identificationHeader.getSampleRate() / audioPacket.getNumberOfSamples();
		return audioPacket;
	}

	public long getCurrentGranulePosition() {
		return this.currentGranulePosition;
	}

	public int getCurrentBitRate() {
		return this.currentBitRate;
	}

	public byte[] processPacket(byte[] byteArray) throws VorbisFormatException, IOException {
		if (byteArray.length == 0) {
			throw new VorbisFormatException("Cannot decode a vorbis packet with length = 0");
		} else if ((byteArray[0] & 1) == 1) {
			ByteArrayBitInputStream byteArrayBitInputStream = new ByteArrayBitInputStream(byteArray);
			switch (byteArrayBitInputStream.getInt(8)) {
			case 1: 
				this.identificationHeader = new IdentificationHeader(byteArrayBitInputStream);
			
			case 2: 
			
			case 4: 
			
			default: 
				break;
			
			case 3: 
				this.commentHeader = new CommentHeader(byteArrayBitInputStream);
				break;
			
			case 5: 
				this.setupHeader = new SetupHeader(this, byteArrayBitInputStream);
			
			}

			return null;
		} else if (this.identificationHeader != null && this.commentHeader != null && this.setupHeader != null) {
			AudioPacket audioPacket = new AudioPacket(this, new ByteArrayBitInputStream(byteArray));
			this.currentGranulePosition += (long)audioPacket.getNumberOfSamples();
			if (this.lastAudioPacket == null) {
				this.lastAudioPacket = audioPacket;
				return null;
			} else {
				byte[] byteArray2 = new byte[this.identificationHeader.getChannels() * audioPacket.getNumberOfSamples() * 2];
				try {
					audioPacket.getPcm(this.lastAudioPacket, byteArray2);
				} catch (IndexOutOfBoundsException indexOutOfBoundsException) {
					Arrays.fill(byteArray2, (byte)0);
				}

				this.lastAudioPacket = audioPacket;
				return byteArray2;
			}
		} else {
			throw new VorbisFormatException("Cannot decode audio packet before all three header packets have been decoded.");
		}
	}
}
