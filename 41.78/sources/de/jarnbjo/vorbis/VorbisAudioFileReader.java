package de.jarnbjo.vorbis;

import de.jarnbjo.ogg.BasicStream;
import de.jarnbjo.ogg.EndOfOggStreamException;
import de.jarnbjo.ogg.FileStream;
import de.jarnbjo.ogg.LogicalOggStream;
import de.jarnbjo.ogg.OggFormatException;
import de.jarnbjo.ogg.PhysicalOggStream;
import de.jarnbjo.ogg.UncachedUrlStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Collection;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.spi.AudioFileReader;


public class VorbisAudioFileReader extends AudioFileReader {

	public AudioFileFormat getAudioFileFormat(File file) throws IOException, UnsupportedAudioFileException {
		try {
			return this.getAudioFileFormat((PhysicalOggStream)(new FileStream(new RandomAccessFile(file, "r"))));
		} catch (OggFormatException oggFormatException) {
			throw new UnsupportedAudioFileException(oggFormatException.getMessage());
		}
	}

	public AudioFileFormat getAudioFileFormat(InputStream inputStream) throws IOException, UnsupportedAudioFileException {
		try {
			return this.getAudioFileFormat((PhysicalOggStream)(new BasicStream(inputStream)));
		} catch (OggFormatException oggFormatException) {
			throw new UnsupportedAudioFileException(oggFormatException.getMessage());
		}
	}

	public AudioFileFormat getAudioFileFormat(URL url) throws IOException, UnsupportedAudioFileException {
		try {
			return this.getAudioFileFormat((PhysicalOggStream)(new UncachedUrlStream(url)));
		} catch (OggFormatException oggFormatException) {
			throw new UnsupportedAudioFileException(oggFormatException.getMessage());
		}
	}

	private AudioFileFormat getAudioFileFormat(PhysicalOggStream physicalOggStream) throws IOException, UnsupportedAudioFileException {
		try {
			Collection collection = physicalOggStream.getLogicalStreams();
			if (collection.size() != 1) {
				throw new UnsupportedAudioFileException("Only Ogg files with one logical Vorbis stream are supported.");
			} else {
				LogicalOggStream logicalOggStream = (LogicalOggStream)collection.iterator().next();
				if (logicalOggStream.getFormat() != "audio/x-vorbis") {
					throw new UnsupportedAudioFileException("Only Ogg files with one logical Vorbis stream are supported.");
				} else {
					VorbisStream vorbisStream = new VorbisStream(logicalOggStream);
					AudioFormat audioFormat = new AudioFormat((float)vorbisStream.getIdentificationHeader().getSampleRate(), 16, vorbisStream.getIdentificationHeader().getChannels(), true, true);
					return new AudioFileFormat(VorbisAudioFileReader.VorbisFormatType.getInstance(), audioFormat, -1);
				}
			}
		} catch (OggFormatException oggFormatException) {
			throw new UnsupportedAudioFileException(oggFormatException.getMessage());
		} catch (VorbisFormatException vorbisFormatException) {
			throw new UnsupportedAudioFileException(vorbisFormatException.getMessage());
		}
	}

	public AudioInputStream getAudioInputStream(File file) throws IOException, UnsupportedAudioFileException {
		try {
			return this.getAudioInputStream((PhysicalOggStream)(new FileStream(new RandomAccessFile(file, "r"))));
		} catch (OggFormatException oggFormatException) {
			throw new UnsupportedAudioFileException(oggFormatException.getMessage());
		}
	}

	public AudioInputStream getAudioInputStream(InputStream inputStream) throws IOException, UnsupportedAudioFileException {
		try {
			return this.getAudioInputStream((PhysicalOggStream)(new BasicStream(inputStream)));
		} catch (OggFormatException oggFormatException) {
			throw new UnsupportedAudioFileException(oggFormatException.getMessage());
		}
	}

	public AudioInputStream getAudioInputStream(URL url) throws IOException, UnsupportedAudioFileException {
		try {
			return this.getAudioInputStream((PhysicalOggStream)(new UncachedUrlStream(url)));
		} catch (OggFormatException oggFormatException) {
			throw new UnsupportedAudioFileException(oggFormatException.getMessage());
		}
	}

	private AudioInputStream getAudioInputStream(PhysicalOggStream physicalOggStream) throws IOException, UnsupportedAudioFileException {
		try {
			Collection collection = physicalOggStream.getLogicalStreams();
			if (collection.size() != 1) {
				throw new UnsupportedAudioFileException("Only Ogg files with one logical Vorbis stream are supported.");
			} else {
				LogicalOggStream logicalOggStream = (LogicalOggStream)collection.iterator().next();
				if (logicalOggStream.getFormat() != "audio/x-vorbis") {
					throw new UnsupportedAudioFileException("Only Ogg files with one logical Vorbis stream are supported.");
				} else {
					VorbisStream vorbisStream = new VorbisStream(logicalOggStream);
					AudioFormat audioFormat = new AudioFormat((float)vorbisStream.getIdentificationHeader().getSampleRate(), 16, vorbisStream.getIdentificationHeader().getChannels(), true, true);
					return new AudioInputStream(new VorbisAudioFileReader.VorbisInputStream(vorbisStream), audioFormat, -1L);
				}
			}
		} catch (OggFormatException oggFormatException) {
			throw new UnsupportedAudioFileException(oggFormatException.getMessage());
		} catch (VorbisFormatException vorbisFormatException) {
			throw new UnsupportedAudioFileException(vorbisFormatException.getMessage());
		}
	}

	public static class VorbisFormatType extends Type {
		private static final VorbisAudioFileReader.VorbisFormatType instance = new VorbisAudioFileReader.VorbisFormatType();

		private VorbisFormatType() {
			super("VORBIS", "ogg");
		}

		public static Type getInstance() {
			return instance;
		}
	}

	public static class VorbisInputStream extends InputStream {
		private VorbisStream source;
		private byte[] buffer = new byte[8192];

		public VorbisInputStream(VorbisStream vorbisStream) {
			this.source = vorbisStream;
		}

		public int read() throws IOException {
			return 0;
		}

		public int read(byte[] byteArray) throws IOException {
			return this.read(byteArray, 0, byteArray.length);
		}

		public int read(byte[] byteArray, int int1, int int2) throws IOException {
			try {
				return this.source.readPcm(byteArray, int1, int2);
			} catch (EndOfOggStreamException endOfOggStreamException) {
				return -1;
			}
		}
	}
}
