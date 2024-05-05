package com.sixlegs.png;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;


class ImageDataInputStream extends InputStream {
	private final PngInputStream in;
	private final StateMachine machine;
	private final byte[] onebyte = new byte[1];
	private boolean done;

	public ImageDataInputStream(PngInputStream pngInputStream, StateMachine stateMachine) {
		this.in = pngInputStream;
		this.machine = stateMachine;
	}

	public int read() throws IOException {
		return this.read(this.onebyte, 0, 1) == -1 ? -1 : 255 & this.onebyte[0];
	}

	public int read(byte[] byteArray, int int1, int int2) throws IOException {
		if (this.done) {
			return -1;
		} else {
			try {
				int int3 = 0;
				while (int3 != int2 && !this.done) {
					while (int3 != int2 && this.in.getRemaining() > 0) {
						int int4 = Math.min(int2 - int3, this.in.getRemaining());
						this.in.readFully(byteArray, int1 + int3, int4);
						int3 += int4;
					}

					if (this.in.getRemaining() <= 0) {
						this.in.endChunk(this.machine.getType());
						this.machine.nextState(this.in.startChunk());
						this.done = this.machine.getType() != 1229209940;
					}
				}

				return int3;
			} catch (EOFException eOFException) {
				this.done = true;
				return -1;
			}
		}
	}
}
