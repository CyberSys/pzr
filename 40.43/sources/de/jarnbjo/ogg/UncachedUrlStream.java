package de.jarnbjo.ogg;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;


public class UncachedUrlStream implements PhysicalOggStream {
	private boolean closed = false;
	private URLConnection source;
	private InputStream sourceStream;
	private Object drainLock = new Object();
	private LinkedList pageCache = new LinkedList();
	private long numberOfSamples = -1L;
	private HashMap logicalStreams = new HashMap();
	private UncachedUrlStream.LoaderThread loaderThread;
	private static final int PAGECACHE_SIZE = 10;

	public UncachedUrlStream(URL url) throws OggFormatException, IOException {
		this.source = url.openConnection();
		this.sourceStream = this.source.getInputStream();
		this.loaderThread = new UncachedUrlStream.LoaderThread(this.sourceStream, this.pageCache);
		(new Thread(this.loaderThread)).start();
		while (!this.loaderThread.isBosDone() || this.pageCache.size() < 10) {
			try {
				Thread.sleep(200L);
			} catch (InterruptedException interruptedException) {
			}
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
		this.sourceStream.close();
	}

	public OggPage getOggPage(int int1) throws IOException {
		while (this.pageCache.size() == 0) {
			try {
				Thread.sleep(100L);
			} catch (InterruptedException interruptedException) {
			}
		}

		synchronized (this.drainLock) {
			return (OggPage)this.pageCache.removeFirst();
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

	public class LoaderThread implements Runnable {
		private InputStream source;
		private LinkedList pageCache;
		private RandomAccessFile drain;
		private byte[] memoryCache;
		private boolean bosDone = false;
		private int pageNumber;

		public LoaderThread(InputStream inputStream, LinkedList linkedList) {
			this.source = inputStream;
			this.pageCache = linkedList;
		}

		public void run() {
			try {
				boolean boolean1 = false;
				byte[] byteArray = new byte[8192];
				while (!boolean1) {
					OggPage oggPage = OggPage.create(this.source);
					synchronized (UncachedUrlStream.this.drainLock) {
						this.pageCache.add(oggPage);
					}

					if (!oggPage.isBos()) {
						this.bosDone = true;
					}

					if (oggPage.isEos()) {
						boolean1 = true;
					}

					LogicalOggStreamImpl logicalOggStreamImpl = (LogicalOggStreamImpl)UncachedUrlStream.this.getLogicalStream(oggPage.getStreamSerialNumber());
					if (logicalOggStreamImpl == null) {
						logicalOggStreamImpl = new LogicalOggStreamImpl(UncachedUrlStream.this, oggPage.getStreamSerialNumber());
						UncachedUrlStream.this.logicalStreams.put(new Integer(oggPage.getStreamSerialNumber()), logicalOggStreamImpl);
						logicalOggStreamImpl.checkFormat(oggPage);
					}

					++this.pageNumber;
					while (this.pageCache.size() > 10) {
						try {
							Thread.sleep(200L);
						} catch (InterruptedException interruptedException) {
						}
					}
				}
			} catch (EndOfOggStreamException endOfOggStreamException) {
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		public boolean isBosDone() {
			return this.bosDone;
		}
	}
}
