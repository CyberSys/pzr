package de.jarnbjo.ogg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;


public class BasicStream implements PhysicalOggStream {
	private boolean closed = false;
	private InputStream sourceStream;
	private Object drainLock = new Object();
	private LinkedList pageCache = new LinkedList();
	private long numberOfSamples = -1L;
	private int position = 0;
	private HashMap logicalStreams = new HashMap();
	private OggPage firstPage;
	int pageNumber = 2;

	public BasicStream(InputStream inputStream) throws OggFormatException, IOException {
		this.firstPage = OggPage.create(inputStream);
		this.position += this.firstPage.getTotalLength();
		LogicalOggStreamImpl logicalOggStreamImpl = new LogicalOggStreamImpl(this, this.firstPage.getStreamSerialNumber());
		this.logicalStreams.put(new Integer(this.firstPage.getStreamSerialNumber()), logicalOggStreamImpl);
		logicalOggStreamImpl.checkFormat(this.firstPage);
	}

	public Collection getLogicalStreams() {
		return this.logicalStreams.values();
	}

	public boolean isOpen() {
		return !this.closed;
	}

	public void close() throws IOException {
		this.closed = true;
		this.sourceStream.close();
	}

	public int getContentLength() {
		return -1;
	}

	public int getPosition() {
		return this.position;
	}

	public OggPage getOggPage(int int1) throws IOException {
		OggPage oggPage;
		if (this.firstPage != null) {
			oggPage = this.firstPage;
			this.firstPage = null;
			return oggPage;
		} else {
			oggPage = OggPage.create(this.sourceStream);
			this.position += oggPage.getTotalLength();
			return oggPage;
		}
	}

	private LogicalOggStream getLogicalStream(int int1) {
		return (LogicalOggStream)this.logicalStreams.get(new Integer(int1));
	}

	public void setTime(long long1) throws IOException {
		throw new UnsupportedOperationException("Method not supported by this class");
	}

	public boolean isSeekable() {
		return false;
	}
}
