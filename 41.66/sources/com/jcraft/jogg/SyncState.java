package com.jcraft.jogg;


public class SyncState {
	public byte[] data;
	int bodybytes;
	int fill;
	int headerbytes;
	int returned;
	int storage;
	int unsynced;
	private byte[] chksum = new byte[4];
	private Page pageseek = new Page();

	public int buffer(int int1) {
		if (this.returned != 0) {
			this.fill -= this.returned;
			if (this.fill > 0) {
				System.arraycopy(this.data, this.returned, this.data, 0, this.fill);
			}

			this.returned = 0;
		}

		if (int1 > this.storage - this.fill) {
			int int2 = int1 + this.fill + 4096;
			if (this.data != null) {
				byte[] byteArray = new byte[int2];
				System.arraycopy(this.data, 0, byteArray, 0, this.data.length);
				this.data = byteArray;
			} else {
				this.data = new byte[int2];
			}

			this.storage = int2;
		}

		return this.fill;
	}

	public int clear() {
		this.data = null;
		return 0;
	}

	public int getBufferOffset() {
		return this.fill;
	}

	public int getDataOffset() {
		return this.returned;
	}

	public void init() {
	}

	public int pageout(Page page) {
		do {
			int int1 = this.pageseek(page);
			if (int1 > 0) {
				return 1;
			}

			if (int1 == 0) {
				return 0;
			}
		} while (this.unsynced != 0);

		this.unsynced = 1;
		return -1;
	}

	public int pageseek(Page page) {
		int int1 = this.returned;
		int int2 = this.fill - this.returned;
		int int3;
		int int4;
		if (this.headerbytes == 0) {
			if (int2 < 27) {
				return 0;
			}

			if (this.data[int1] != 79 || this.data[int1 + 1] != 103 || this.data[int1 + 2] != 103 || this.data[int1 + 3] != 83) {
				this.headerbytes = 0;
				this.bodybytes = 0;
				int3 = 0;
				for (int4 = 0; int4 < int2 - 1; ++int4) {
					if (this.data[int1 + 1 + int4] == 79) {
						int3 = int1 + 1 + int4;
						break;
					}
				}

				if (int3 == 0) {
					int3 = this.fill;
				}

				this.returned = int3;
				return -(int3 - int1);
			}

			int int5 = (this.data[int1 + 26] & 255) + 27;
			if (int2 < int5) {
				return 0;
			}

			for (int int6 = 0; int6 < (this.data[int1 + 26] & 255); ++int6) {
				this.bodybytes += this.data[int1 + 27 + int6] & 255;
			}

			this.headerbytes = int5;
		}

		if (this.bodybytes + this.headerbytes > int2) {
			return 0;
		} else {
			synchronized (this.chksum) {
				System.arraycopy(this.data, int1 + 22, this.chksum, 0, 4);
				this.data[int1 + 22] = 0;
				this.data[int1 + 23] = 0;
				this.data[int1 + 24] = 0;
				this.data[int1 + 25] = 0;
				Page page2 = this.pageseek;
				page2.header_base = this.data;
				page2.header = int1;
				page2.header_len = this.headerbytes;
				page2.body_base = this.data;
				page2.body = int1 + this.headerbytes;
				page2.body_len = this.bodybytes;
				page2.checksum();
				if (this.chksum[0] != this.data[int1 + 22] || this.chksum[1] != this.data[int1 + 23] || this.chksum[2] != this.data[int1 + 24] || this.chksum[3] != this.data[int1 + 25]) {
					System.arraycopy(this.chksum, 0, this.data, int1 + 22, 4);
					this.headerbytes = 0;
					this.bodybytes = 0;
					int3 = 0;
					for (int4 = 0; int4 < int2 - 1; ++int4) {
						if (this.data[int1 + 1 + int4] == 79) {
							int3 = int1 + 1 + int4;
							break;
						}
					}

					if (int3 == 0) {
						int3 = this.fill;
					}

					this.returned = int3;
					return -(int3 - int1);
				}
			}

			int1 = this.returned;
			if (page != null) {
				page.header_base = this.data;
				page.header = int1;
				page.header_len = this.headerbytes;
				page.body_base = this.data;
				page.body = int1 + this.headerbytes;
				page.body_len = this.bodybytes;
			}

			this.unsynced = 0;
			this.returned += (int2 = this.headerbytes + this.bodybytes);
			this.headerbytes = 0;
			this.bodybytes = 0;
			return int2;
		}
	}

	public int reset() {
		this.fill = 0;
		this.returned = 0;
		this.unsynced = 0;
		this.headerbytes = 0;
		this.bodybytes = 0;
		return 0;
	}

	public int wrote(int int1) {
		if (this.fill + int1 > this.storage) {
			return -1;
		} else {
			this.fill += int1;
			return 0;
		}
	}
}
