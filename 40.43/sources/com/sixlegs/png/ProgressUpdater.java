package com.sixlegs.png;

import java.awt.image.BufferedImage;


final class ProgressUpdater extends PixelProcessor {
	private static final int STEP_PERCENT = 5;
	private final PngImage png;
	private final BufferedImage image;
	private final PixelProcessor pp;
	private final int total;
	private final int step;
	private int count;
	private int mod;

	public ProgressUpdater(PngImage pngImage, BufferedImage bufferedImage, PixelProcessor pixelProcessor) {
		this.png = pngImage;
		this.image = bufferedImage;
		this.pp = pixelProcessor;
		this.total = pngImage.getWidth() * pngImage.getHeight();
		this.step = Math.max(1, this.total * 5 / 100);
	}

	public boolean process(int[] intArray, int int1, int int2, int int3, int int4, int int5) {
		boolean boolean1 = this.pp.process(intArray, int1, int2, int3, int4, int5);
		this.mod += int5;
		this.count += int5;
		if (this.mod > this.step) {
			this.mod %= this.step;
			boolean1 = boolean1 && this.png.handleProgress(this.image, 100.0F * (float)this.count / (float)this.total);
		}

		return boolean1;
	}
}
