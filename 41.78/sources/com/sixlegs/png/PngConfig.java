package com.sixlegs.png;

import java.awt.Rectangle;


public final class PngConfig {
	public static final int READ_ALL = 0;
	public static final int READ_HEADER = 1;
	public static final int READ_UNTIL_DATA = 2;
	public static final int READ_EXCEPT_DATA = 3;
	public static final int READ_EXCEPT_METADATA = 4;
	final int readLimit;
	final float defaultGamma;
	final float displayExponent;
	final boolean warningsFatal;
	final boolean progressive;
	final boolean reduce16;
	final boolean gammaCorrect;
	final Rectangle sourceRegion;
	final int[] subsampling;
	final boolean convertIndexed;

	PngConfig(PngConfig.Builder builder) {
		this.readLimit = builder.readLimit;
		this.defaultGamma = builder.defaultGamma;
		this.displayExponent = builder.displayExponent;
		this.warningsFatal = builder.warningsFatal;
		this.progressive = builder.progressive;
		this.reduce16 = builder.reduce16;
		this.gammaCorrect = builder.gammaCorrect;
		this.sourceRegion = builder.sourceRegion;
		this.subsampling = builder.subsampling;
		this.convertIndexed = builder.convertIndexed;
		boolean boolean1 = this.getSourceXSubsampling() != 1 || this.getSourceYSubsampling() != 1;
		if (this.progressive && (boolean1 || this.getSourceRegion() != null)) {
			throw new IllegalStateException("Progressive rendering cannot be used with source regions or subsampling");
		}
	}

	public boolean getConvertIndexed() {
		return this.convertIndexed;
	}

	public boolean getReduce16() {
		return this.reduce16;
	}

	public float getDefaultGamma() {
		return this.defaultGamma;
	}

	public boolean getGammaCorrect() {
		return this.gammaCorrect;
	}

	public boolean getProgressive() {
		return this.progressive;
	}

	public float getDisplayExponent() {
		return this.displayExponent;
	}

	public int getReadLimit() {
		return this.readLimit;
	}

	public boolean getWarningsFatal() {
		return this.warningsFatal;
	}

	public Rectangle getSourceRegion() {
		return this.sourceRegion != null ? new Rectangle(this.sourceRegion) : null;
	}

	public int getSourceXSubsampling() {
		return this.subsampling[0];
	}

	public int getSourceYSubsampling() {
		return this.subsampling[1];
	}

	public int getSubsamplingXOffset() {
		return this.subsampling[2];
	}

	public int getSubsamplingYOffset() {
		return this.subsampling[3];
	}

	public static final class Builder {
		private static final int[] DEFAULT_SUBSAMPLING = new int[]{1, 1, 0, 0};
		int readLimit = 0;
		float defaultGamma = 0.45455F;
		float displayExponent = 2.2F;
		boolean warningsFatal;
		boolean progressive;
		boolean reduce16 = true;
		boolean gammaCorrect = true;
		Rectangle sourceRegion;
		int[] subsampling;
		boolean convertIndexed;

		public Builder() {
			this.subsampling = DEFAULT_SUBSAMPLING;
		}

		public Builder(PngConfig pngConfig) {
			this.subsampling = DEFAULT_SUBSAMPLING;
			this.readLimit = pngConfig.readLimit;
			this.defaultGamma = pngConfig.defaultGamma;
			this.displayExponent = pngConfig.displayExponent;
			this.warningsFatal = pngConfig.warningsFatal;
			this.progressive = pngConfig.progressive;
			this.reduce16 = pngConfig.reduce16;
			this.gammaCorrect = pngConfig.gammaCorrect;
			this.subsampling = pngConfig.subsampling;
		}

		public PngConfig build() {
			return new PngConfig(this);
		}

		public PngConfig.Builder reduce16(boolean boolean1) {
			this.reduce16 = boolean1;
			return this;
		}

		public PngConfig.Builder defaultGamma(float float1) {
			this.defaultGamma = float1;
			return this;
		}

		public PngConfig.Builder displayExponent(float float1) {
			this.displayExponent = float1;
			return this;
		}

		public PngConfig.Builder gammaCorrect(boolean boolean1) {
			this.gammaCorrect = boolean1;
			return this;
		}

		public PngConfig.Builder progressive(boolean boolean1) {
			this.progressive = boolean1;
			return this;
		}

		public PngConfig.Builder readLimit(int int1) {
			this.readLimit = int1;
			return this;
		}

		public PngConfig.Builder warningsFatal(boolean boolean1) {
			this.warningsFatal = boolean1;
			return this;
		}

		public PngConfig.Builder sourceRegion(Rectangle rectangle) {
			if (rectangle != null) {
				if (rectangle.x < 0 || rectangle.y < 0 || rectangle.width <= 0 || rectangle.height <= 0) {
					throw new IllegalArgumentException("invalid source region: " + rectangle);
				}

				this.sourceRegion = new Rectangle(rectangle);
			} else {
				this.sourceRegion = null;
			}

			return this;
		}

		public PngConfig.Builder sourceSubsampling(int int1, int int2, int int3, int int4) {
			if (int1 > 0 && int2 > 0 && int3 >= 0 && int3 < int1 && int4 >= 0 && int4 < int2) {
				this.subsampling = new int[]{int1, int2, int3, int4};
				return this;
			} else {
				throw new IllegalArgumentException("invalid subsampling values");
			}
		}

		public PngConfig.Builder convertIndexed(boolean boolean1) {
			this.convertIndexed = boolean1;
			return this;
		}
	}
}
