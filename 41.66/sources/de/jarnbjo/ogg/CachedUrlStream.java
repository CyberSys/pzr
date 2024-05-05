package de.jarnbjo.ogg;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;


public class CachedUrlStream implements PhysicalOggStream {
	private boolean closed;
	private URLConnection source;
	private InputStream sourceStream;
	private Object drainLock;
	private RandomAccessFile drain;
	private byte[] memoryCache;
	private ArrayList pageOffsets;
	private ArrayList pageLengths;
	private long numberOfSamples;
	private long cacheLength;
	private HashMap logicalStreams;
	private CachedUrlStream.LoaderThread loaderThread;

	public CachedUrlStream(URL url) throws OggFormatException, IOException {
		this(url, (RandomAccessFile)null);
	}

	public CachedUrlStream(URL url, RandomAccessFile randomAccessFile) throws OggFormatException, IOException {
		this.closed = false;
		this.drainLock = new Object();
		this.pageOffsets = new ArrayList();
		this.pageLengths = new ArrayList();
		this.numberOfSamples = -1L;
		this.logicalStreams = new HashMap();
		this.source = url.openConnection();
		if (randomAccessFile == null) {
			int int1 = this.source.getContentLength();
			if (int1 == -1) {
				throw new IOException("The URLConncetion\'s content length must be set when operating with a in-memory cache.");
			}

			this.memoryCache = new byte[int1];
		}

		this.drain = randomAccessFile;
		this.sourceStream = this.source.getInputStream();
		this.loaderThread = new CachedUrlStream.LoaderThread(this.sourceStream, randomAccessFile, this.memoryCache);
		(new Thread(this.loaderThread)).start();
		while (!this.loaderThread.isBosDone() || this.pageOffsets.size() < 20) {
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

	public long getCacheLength() {
		return this.cacheLength;
	}

	public OggPage getOggPage(int int1) throws IOException {
		synchronized (this.drainLock) {
			Long Long1 = (Long)this.pageOffsets.get(int1);
			Long Long2 = (Long)this.pageLengths.get(int1);
			if (Long1 != null) {
				if (this.drain != null) {
					this.drain.seek(Long1);
					return OggPage.create(this.drain);
				} else {
					byte[] byteArray = new byte[Long2.intValue()];
					System.arraycopy(this.memoryCache, Long1.intValue(), byteArray, 0, Long2.intValue());
					return OggPage.create(byteArray);
				}
			} else {
				return null;
			}
		}
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

	public class LoaderThread implements Runnable {
		private InputStream source;
		private RandomAccessFile drain;
		private byte[] memoryCache;
		private boolean bosDone = false;
		private int pageNumber;

		public LoaderThread(InputStream inputStream, RandomAccessFile randomAccessFile, byte[] byteArray) {
			this.source = inputStream;
			this.drain = randomAccessFile;
			this.memoryCache = byteArray;
		}

		public void run() {
			try {
				boolean boolean1 = false;
				OggPage oggPage;
				for (byte[] byteArray = new byte[8192]; !boolean1; CachedUrlStream.this.cacheLength = oggPage.getAbsoluteGranulePosition()) {
					oggPage = OggPage.create(this.source);
					synchronized (CachedUrlStream.this.drainLock) {
						int int1 = CachedUrlStream.this.pageOffsets.size();
						long long1 = int1 > 0 ? (Long)CachedUrlStream.this.pageOffsets.get(int1 - 1) + (Long)CachedUrlStream.this.pageLengths.get(int1 - 1) : 0L;
						byte[] byteArray2 = oggPage.getHeader();
						byte[] byteArray3 = oggPage.getSegmentTable();
						byte[] byteArray4 = oggPage.getData();
						if (this.drain != null) {
							this.drain.seek(long1);
							this.drain.write(byteArray2);
							this.drain.write(byteArray3);
							this.drain.write(byteArray4);
						} else {
							System.arraycopy(byteArray2, 0, this.memoryCache, (int)long1, byteArray2.length);
							System.arraycopy(byteArray3, 0, this.memoryCache, (int)long1 + byteArray2.length, byteArray3.length);
							System.arraycopy(byteArray4, 0, this.memoryCache, (int)long1 + byteArray2.length + byteArray3.length, byteArray4.length);
						}

						CachedUrlStream.this.pageOffsets.add(new Long(long1));
						CachedUrlStream.this.pageLengths.add(new Long((long)(byteArray2.length + byteArray3.length + byteArray4.length)));
					}

					if (!oggPage.isBos()) {
						this.bosDone = true;
					}

					if (oggPage.isEos()) {
						boolean1 = true;
					}

					LogicalOggStreamImpl logicalOggStreamImpl = (LogicalOggStreamImpl)CachedUrlStream.this.getLogicalStream(oggPage.getStreamSerialNumber());
					if (logicalOggStreamImpl == null) {
						logicalOggStreamImpl = new LogicalOggStreamImpl(CachedUrlStream.this, oggPage.getStreamSerialNumber());
						CachedUrlStream.this.logicalStreams.put(new Integer(oggPage.getStreamSerialNumber()), logicalOggStreamImpl);
						logicalOggStreamImpl.checkFormat(oggPage);
					}

					logicalOggStreamImpl.addPageNumberMapping(this.pageNumber);
					logicalOggStreamImpl.addGranulePosition(oggPage.getAbsoluteGranulePosition());
					++this.pageNumber;
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
