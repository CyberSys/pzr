package de.jarnbjo.util.audio;

import java.io.IOException;
import javax.sound.sampled.AudioInputStream;


public class FadeableAudioInputStream extends AudioInputStream {
	private AudioInputStream stream;
	private boolean fading = false;
	private double phi = 0.0;

	public FadeableAudioInputStream(AudioInputStream audioInputStream) throws IOException {
		super(audioInputStream, audioInputStream.getFormat(), -1L);
	}

	public void fadeOut() {
		this.fading = true;
		this.phi = 0.0;
	}

	public int read(byte[] byteArray) throws IOException {
		return this.read(byteArray, 0, byteArray.length);
	}

	public int read(byte[] byteArray, int int1, int int2) throws IOException {
		int int3 = super.read(byteArray, int1, int2);
		if (this.fading) {
			boolean boolean1 = false;
			boolean boolean2 = false;
			boolean boolean3 = false;
			double double1 = 0.0;
			for (int int4 = int1; int4 < int1 + int3; int4 += 4) {
				int int5 = int4 + 1;
				int int6 = byteArray[int4] & 255;
				int6 |= byteArray[int5++] << 8;
				int int7 = byteArray[int5++] & 255;
				int7 |= byteArray[int5] << 8;
				if (this.phi < 1.5707963267948966) {
					this.phi += 1.5E-5;
				}

				double1 = Math.cos(this.phi);
				int6 = (int)((double)int6 * double1);
				int7 = (int)((double)int7 * double1);
				int5 = int4 + 1;
				byteArray[int4] = (byte)(int6 & 255);
				byteArray[int5++] = (byte)(int6 >> 8 & 255);
				byteArray[int5++] = (byte)(int7 & 255);
				byteArray[int5++] = (byte)(int7 >> 8 & 255);
			}
		}

		return int3;
	}
}
