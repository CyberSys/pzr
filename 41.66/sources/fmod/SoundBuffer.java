package fmod;

import zombie.debug.DebugLog;


public class SoundBuffer {
	public int Buf_Size;
	public int Buf_Read;
	public int Buf_Write;
	private short[] intdata;
	private int delay;

	public SoundBuffer(int int1) {
		this.Buf_Size = int1;
		this.Buf_Read = 0;
		this.Buf_Write = 0;
		this.delay = 1;
		this.intdata = new short[int1];
	}

	public void get(long long1, short[] shortArray) {
		int int1 = this.Buf_Write - this.Buf_Read;
		if (int1 < 0) {
			int1 += this.Buf_Size;
		}

		int int2;
		if ((long)int1 < long1) {
			for (int2 = 0; (long)int2 < long1 - 1L; ++int2) {
				shortArray[int2] = 0;
			}
		} else {
			int int3;
			if ((long)int1 > long1 * (long)this.delay * 2L) {
				if ((long)this.delay * long1 * 3L < (long)this.Buf_Size) {
					++this.delay;
				}

				DebugLog.log("[SoundBuffer] correct: delay: " + this.delay);
				this.Buf_Read = (int)((long)this.Buf_Write - long1 * (long)this.delay);
				if (this.Buf_Read < 0) {
					this.Buf_Read += this.Buf_Size;
				}

				int2 = 0;
				for (int3 = this.Buf_Read; (long)int2 < long1 * 2L; int3 = (int3 + 1) % this.Buf_Size) {
					this.intdata[int3] = 0;
					++int2;
				}
			} else {
				int2 = 0;
				for (int3 = this.Buf_Read; (long)int2 < long1 - 1L && int3 != this.Buf_Write; int3 = (int3 + 1) % this.Buf_Size) {
					shortArray[int2] = this.intdata[int3];
					++int2;
				}

				this.Buf_Read = int3;
			}
		}
	}

	public void push(long long1, short[] shortArray) {
		boolean boolean1 = false;
		int int1 = 0;
		int int2;
		for (int2 = this.Buf_Write; (long)int1 < long1 - 1L; int2 = (int2 + 1) % this.Buf_Size) {
			this.intdata[int2] = shortArray[int1];
			if (shortArray[int1] != 0) {
				boolean1 = true;
			}

			++int1;
		}

		if (boolean1) {
			this.Buf_Write = int2;
		}
	}

	public void push(long long1, byte[] byteArray) {
		boolean boolean1 = false;
		int int1 = 0;
		int int2;
		for (int2 = this.Buf_Write; (long)int1 < long1 - 1L; int2 = (int2 + 1) % this.Buf_Size) {
			this.intdata[int2] = (short)(byteArray[int1 + 1] * 256 + byteArray[int1]);
			if (byteArray[int1] != 0) {
				boolean1 = true;
			}

			int1 += 2;
		}

		if (boolean1) {
			this.Buf_Write = int2;
		}
	}

	public short[] buf() {
		return this.intdata;
	}
}
