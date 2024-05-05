package de.jarnbjo.ogg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class LogicalOggStreamImpl implements LogicalOggStream {
	private PhysicalOggStream source;
	private int serialNumber;
	private ArrayList pageNumberMapping = new ArrayList();
	private ArrayList granulePositions = new ArrayList();
	private int pageIndex = 0;
	private OggPage currentPage;
	private int currentSegmentIndex;
	private boolean open = true;
	private String format = "application/octet-stream";

	public LogicalOggStreamImpl(PhysicalOggStream physicalOggStream, int int1) {
		this.source = physicalOggStream;
		this.serialNumber = int1;
	}

	public void addPageNumberMapping(int int1) {
		this.pageNumberMapping.add(new Integer(int1));
	}

	public void addGranulePosition(long long1) {
		this.granulePositions.add(new Long(long1));
	}

	public synchronized void reset() throws OggFormatException, IOException {
		this.currentPage = null;
		this.currentSegmentIndex = 0;
		this.pageIndex = 0;
	}

	public synchronized OggPage getNextOggPage() throws EndOfOggStreamException, OggFormatException, IOException {
		if (this.source.isSeekable()) {
			this.currentPage = this.source.getOggPage((Integer)this.pageNumberMapping.get(this.pageIndex++));
		} else {
			this.currentPage = this.source.getOggPage(-1);
		}

		return this.currentPage;
	}

	public synchronized byte[] getNextOggPacket() throws EndOfOggStreamException, OggFormatException, IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		boolean boolean1 = false;
		if (this.currentPage == null) {
			this.currentPage = this.getNextOggPage();
		}

		int int1;
		do {
			if (this.currentSegmentIndex >= this.currentPage.getSegmentOffsets().length) {
				this.currentSegmentIndex = 0;
				if (this.currentPage.isEos()) {
					throw new EndOfOggStreamException();
				}

				if (this.source.isSeekable() && this.pageNumberMapping.size() <= this.pageIndex) {
					while (this.pageNumberMapping.size() <= this.pageIndex + 10) {
						try {
							Thread.sleep(1000L);
						} catch (InterruptedException interruptedException) {
						}
					}
				}

				this.currentPage = this.getNextOggPage();
				if (byteArrayOutputStream.size() == 0 && this.currentPage.isContinued()) {
					boolean boolean2 = false;
					while (!boolean2) {
						if (this.currentPage.getSegmentLengths()[this.currentSegmentIndex++] != 255) {
							boolean2 = true;
						}

						if (this.currentSegmentIndex > this.currentPage.getSegmentTable().length) {
							this.currentPage = this.source.getOggPage((Integer)this.pageNumberMapping.get(this.pageIndex++));
						}
					}
				}
			}

			int1 = this.currentPage.getSegmentLengths()[this.currentSegmentIndex];
			byteArrayOutputStream.write(this.currentPage.getData(), this.currentPage.getSegmentOffsets()[this.currentSegmentIndex], int1);
			++this.currentSegmentIndex;
		} while (int1 == 255);

		return byteArrayOutputStream.toByteArray();
	}

	public boolean isOpen() {
		return this.open;
	}

	public void close() throws IOException {
		this.open = false;
	}

	public long getMaximumGranulePosition() {
		Long Long1 = (Long)this.granulePositions.get(this.granulePositions.size() - 1);
		return Long1;
	}

	public synchronized long getTime() {
		return this.currentPage != null ? this.currentPage.getAbsoluteGranulePosition() : -1L;
	}

	public synchronized void setTime(long long1) throws IOException {
		boolean boolean1 = false;
		int int1;
		for (int1 = 0; int1 < this.granulePositions.size(); ++int1) {
			Long Long1 = (Long)this.granulePositions.get(int1);
			if (Long1 > long1) {
				break;
			}
		}

		this.pageIndex = int1;
		this.currentPage = this.source.getOggPage((Integer)this.pageNumberMapping.get(this.pageIndex++));
		this.currentSegmentIndex = 0;
		boolean boolean2 = false;
		int int2;
		do {
			if (this.currentSegmentIndex >= this.currentPage.getSegmentOffsets().length) {
				this.currentSegmentIndex = 0;
				if (this.pageIndex >= this.pageNumberMapping.size()) {
					throw new EndOfOggStreamException();
				}

				this.currentPage = this.source.getOggPage((Integer)this.pageNumberMapping.get(this.pageIndex++));
			}

			int2 = this.currentPage.getSegmentLengths()[this.currentSegmentIndex];
			++this.currentSegmentIndex;
		} while (int2 == 255);
	}

	public void checkFormat(OggPage oggPage) {
		byte[] byteArray = oggPage.getData();
		if (byteArray.length >= 7 && byteArray[1] == 118 && byteArray[2] == 111 && byteArray[3] == 114 && byteArray[4] == 98 && byteArray[5] == 105 && byteArray[6] == 115) {
			this.format = "audio/x-vorbis";
		} else if (byteArray.length >= 7 && byteArray[1] == 116 && byteArray[2] == 104 && byteArray[3] == 101 && byteArray[4] == 111 && byteArray[5] == 114 && byteArray[6] == 97) {
			this.format = "video/x-theora";
		} else if (byteArray.length == 4 && byteArray[0] == 102 && byteArray[1] == 76 && byteArray[2] == 97 && byteArray[3] == 67) {
			this.format = "audio/x-flac";
		}
	}

	public String getFormat() {
		return this.format;
	}
}
