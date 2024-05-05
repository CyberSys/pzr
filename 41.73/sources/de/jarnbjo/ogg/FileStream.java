package de.jarnbjo.ogg;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;


public class FileStream implements PhysicalOggStream {
	private boolean closed = false;
	private RandomAccessFile source;
	private long[] pageOffsets;
	private long numberOfSamples = -1L;
	private HashMap logicalStreams = new HashMap();

	public FileStream(RandomAccessFile randomAccessFile) throws OggFormatException, IOException {
		this.source = randomAccessFile;
		ArrayList arrayList = new ArrayList();
		int int1 = 0;
		try {
			while (true) {
				arrayList.add(new Long(this.source.getFilePointer()));
				OggPage oggPage = this.getNextPage(int1 > 0);
				if (oggPage == null) {
					break;
				}

				LogicalOggStreamImpl logicalOggStreamImpl = (LogicalOggStreamImpl)this.getLogicalStream(oggPage.getStreamSerialNumber());
				if (logicalOggStreamImpl == null) {
					logicalOggStreamImpl = new LogicalOggStreamImpl(this, oggPage.getStreamSerialNumber());
					this.logicalStreams.put(new Integer(oggPage.getStreamSerialNumber()), logicalOggStreamImpl);
				}

				if (int1 == 0) {
					logicalOggStreamImpl.checkFormat(oggPage);
				}

				logicalOggStreamImpl.addPageNumberMapping(int1);
				logicalOggStreamImpl.addGranulePosition(oggPage.getAbsoluteGranulePosition());
				if (int1 > 0) {
					this.source.seek(this.source.getFilePointer() + (long)oggPage.getTotalLength());
				}

				++int1;
			}
		} catch (EndOfOggStreamException endOfOggStreamException) {
		} catch (IOException ioException) {
			throw ioException;
		}

		this.source.seek(0L);
		this.pageOffsets = new long[arrayList.size()];
		int int2 = 0;
		for (Iterator iterator = arrayList.iterator(); iterator.hasNext(); this.pageOffsets[int2++] = (Long)iterator.next()) {
		}
	}

	public Collection getLogicalStreams() {
		return this.logicalStreams.values();
	}

	public boolean isOpen() {
		return !this.closed;
	}

	public void close() throws IOException {
		this.closed = true;
		this.source.close();
	}

	private OggPage getNextPage() throws EndOfOggStreamException, IOException, OggFormatException {
		return this.getNextPage(false);
	}

	private OggPage getNextPage(boolean boolean1) throws EndOfOggStreamException, IOException, OggFormatException {
		return OggPage.create(this.source, boolean1);
	}

	public OggPage getOggPage(int int1) throws IOException {
		this.source.seek(this.pageOffsets[int1]);
		return OggPage.create(this.source);
	}

	private LogicalOggStream getLogicalStream(int int1) {
		return (LogicalOggStream)this.logicalStreams.get(new Integer(int1));
	}

	public void setTime(long long1) throws IOException {
		Iterator iterator = this.logicalStreams.values().iterator();
		while (iterator.hasNext()) {
			LogicalOggStream logicalOggStream = (LogicalOggStream)iterator.next();
			logicalOggStream.setTime(long1);
		}
	}

	public boolean isSeekable() {
		return true;
	}
}
