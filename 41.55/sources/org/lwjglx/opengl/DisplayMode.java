package org.lwjglx.opengl;


public final class DisplayMode {
	private final int width;
	private final int height;
	private final int bpp;
	private final int freq;
	private final boolean fullscreen;

	public DisplayMode(int int1, int int2) {
		this(int1, int2, 0, 0, false);
	}

	DisplayMode(int int1, int int2, int int3, int int4) {
		this(int1, int2, int3, int4, true);
	}

	private DisplayMode(int int1, int int2, int int3, int int4, boolean boolean1) {
		this.width = int1;
		this.height = int2;
		this.bpp = int3;
		this.freq = int4;
		this.fullscreen = boolean1;
	}

	public boolean isFullscreenCapable() {
		return this.fullscreen;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getBitsPerPixel() {
		return this.bpp;
	}

	public int getFrequency() {
		return this.freq;
	}

	public boolean equals(Object object) {
		if (object != null && object instanceof DisplayMode) {
			DisplayMode displayMode = (DisplayMode)object;
			return displayMode.width == this.width && displayMode.height == this.height && displayMode.bpp == this.bpp && displayMode.freq == this.freq;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.width ^ this.height ^ this.freq ^ this.bpp;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(32);
		stringBuilder.append(this.width);
		stringBuilder.append(" x ");
		stringBuilder.append(this.height);
		stringBuilder.append(" x ");
		stringBuilder.append(this.bpp);
		stringBuilder.append(" @");
		stringBuilder.append(this.freq);
		stringBuilder.append("Hz");
		return stringBuilder.toString();
	}
}
