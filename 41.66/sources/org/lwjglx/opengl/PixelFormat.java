package org.lwjglx.opengl;


public final class PixelFormat implements PixelFormatLWJGL {
	private int bpp;
	private int alpha;
	private int depth;
	private int stencil;
	private int samples;
	private int colorSamples;
	private int num_aux_buffers;
	private int accum_bpp;
	private int accum_alpha;
	private boolean stereo;
	private boolean floating_point;
	private boolean floating_point_packed;
	private boolean sRGB;

	public PixelFormat() {
		this(0, 8, 0);
	}

	public PixelFormat(int int1, int int2, int int3) {
		this(int1, int2, int3, 0);
	}

	public PixelFormat(int int1, int int2, int int3, int int4) {
		this(0, int1, int2, int3, int4);
	}

	public PixelFormat(int int1, int int2, int int3, int int4, int int5) {
		this(int1, int2, int3, int4, int5, 0, 0, 0, false);
	}

	public PixelFormat(int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, boolean boolean1) {
		this(int1, int2, int3, int4, int5, int6, int7, int8, boolean1, false);
	}

	public PixelFormat(int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, boolean boolean1, boolean boolean2) {
		this.bpp = int1;
		this.alpha = int2;
		this.depth = int3;
		this.stencil = int4;
		this.samples = int5;
		this.num_aux_buffers = int6;
		this.accum_bpp = int7;
		this.accum_alpha = int8;
		this.stereo = boolean1;
		this.floating_point = boolean2;
		this.floating_point_packed = false;
		this.sRGB = false;
	}

	private PixelFormat(PixelFormat pixelFormat) {
		this.bpp = pixelFormat.bpp;
		this.alpha = pixelFormat.alpha;
		this.depth = pixelFormat.depth;
		this.stencil = pixelFormat.stencil;
		this.samples = pixelFormat.samples;
		this.colorSamples = pixelFormat.colorSamples;
		this.num_aux_buffers = pixelFormat.num_aux_buffers;
		this.accum_bpp = pixelFormat.accum_bpp;
		this.accum_alpha = pixelFormat.accum_alpha;
		this.stereo = pixelFormat.stereo;
		this.floating_point = pixelFormat.floating_point;
		this.floating_point_packed = pixelFormat.floating_point_packed;
		this.sRGB = pixelFormat.sRGB;
	}

	public int getBitsPerPixel() {
		return this.bpp;
	}

	public PixelFormat withBitsPerPixel(int int1) {
		if (int1 < 0) {
			throw new IllegalArgumentException("Invalid number of bits per pixel specified: " + int1);
		} else {
			PixelFormat pixelFormat = new PixelFormat(this);
			pixelFormat.bpp = int1;
			return pixelFormat;
		}
	}

	public int getAlphaBits() {
		return this.alpha;
	}

	public PixelFormat withAlphaBits(int int1) {
		if (int1 < 0) {
			throw new IllegalArgumentException("Invalid number of alpha bits specified: " + int1);
		} else {
			PixelFormat pixelFormat = new PixelFormat(this);
			pixelFormat.alpha = int1;
			return pixelFormat;
		}
	}

	public int getDepthBits() {
		return this.depth;
	}

	public PixelFormat withDepthBits(int int1) {
		if (int1 < 0) {
			throw new IllegalArgumentException("Invalid number of depth bits specified: " + int1);
		} else {
			PixelFormat pixelFormat = new PixelFormat(this);
			pixelFormat.depth = int1;
			return pixelFormat;
		}
	}

	public int getStencilBits() {
		return this.stencil;
	}

	public PixelFormat withStencilBits(int int1) {
		if (int1 < 0) {
			throw new IllegalArgumentException("Invalid number of stencil bits specified: " + int1);
		} else {
			PixelFormat pixelFormat = new PixelFormat(this);
			pixelFormat.stencil = int1;
			return pixelFormat;
		}
	}

	public int getSamples() {
		return this.samples;
	}

	public PixelFormat withSamples(int int1) {
		if (int1 < 0) {
			throw new IllegalArgumentException("Invalid number of samples specified: " + int1);
		} else {
			PixelFormat pixelFormat = new PixelFormat(this);
			pixelFormat.samples = int1;
			return pixelFormat;
		}
	}

	public PixelFormat withCoverageSamples(int int1) {
		return this.withCoverageSamples(int1, this.samples);
	}

	public PixelFormat withCoverageSamples(int int1, int int2) {
		if (int2 >= 0 && int1 >= 0 && (int2 != 0 || 0 >= int1) && int2 >= int1) {
			PixelFormat pixelFormat = new PixelFormat(this);
			pixelFormat.samples = int2;
			pixelFormat.colorSamples = int1;
			return pixelFormat;
		} else {
			throw new IllegalArgumentException("Invalid number of coverage samples specified: " + int2 + " - " + int1);
		}
	}

	public int getAuxBuffers() {
		return this.num_aux_buffers;
	}

	public PixelFormat withAuxBuffers(int int1) {
		if (int1 < 0) {
			throw new IllegalArgumentException("Invalid number of auxiliary buffers specified: " + int1);
		} else {
			PixelFormat pixelFormat = new PixelFormat(this);
			pixelFormat.num_aux_buffers = int1;
			return pixelFormat;
		}
	}

	public int getAccumulationBitsPerPixel() {
		return this.accum_bpp;
	}

	public PixelFormat withAccumulationBitsPerPixel(int int1) {
		if (int1 < 0) {
			throw new IllegalArgumentException("Invalid number of bits per pixel in the accumulation buffer specified: " + int1);
		} else {
			PixelFormat pixelFormat = new PixelFormat(this);
			pixelFormat.accum_bpp = int1;
			return pixelFormat;
		}
	}

	public int getAccumulationAlpha() {
		return this.accum_alpha;
	}

	public PixelFormat withAccumulationAlpha(int int1) {
		if (int1 < 0) {
			throw new IllegalArgumentException("Invalid number of alpha bits in the accumulation buffer specified: " + int1);
		} else {
			PixelFormat pixelFormat = new PixelFormat(this);
			pixelFormat.accum_alpha = int1;
			return pixelFormat;
		}
	}

	public boolean isStereo() {
		return this.stereo;
	}

	public PixelFormat withStereo(boolean boolean1) {
		PixelFormat pixelFormat = new PixelFormat(this);
		pixelFormat.stereo = boolean1;
		return pixelFormat;
	}

	public boolean isFloatingPoint() {
		return this.floating_point;
	}

	public PixelFormat withFloatingPoint(boolean boolean1) {
		PixelFormat pixelFormat = new PixelFormat(this);
		pixelFormat.floating_point = boolean1;
		if (boolean1) {
			pixelFormat.floating_point_packed = false;
		}

		return pixelFormat;
	}

	public PixelFormat withFloatingPointPacked(boolean boolean1) {
		PixelFormat pixelFormat = new PixelFormat(this);
		pixelFormat.floating_point_packed = boolean1;
		if (boolean1) {
			pixelFormat.floating_point = false;
		}

		return pixelFormat;
	}

	public boolean isSRGB() {
		return this.sRGB;
	}

	public PixelFormat withSRGB(boolean boolean1) {
		PixelFormat pixelFormat = new PixelFormat(this);
		pixelFormat.sRGB = boolean1;
		return pixelFormat;
	}
}
