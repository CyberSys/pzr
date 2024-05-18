package com.evildevil.engines.bubble.texture;


final class DDPixelFormat implements DDSurface {
	protected long size = 0L;
	protected long flags = 0L;
	protected long fourCC = 0L;
	private String fourCCString = "";
	protected long rgbBitCount = 0L;
	protected long rBitMask = 0L;
	protected long gBitMask = 0L;
	protected long bBitMask = 0L;
	protected long rgbAlphaBitMask = 0L;
	protected boolean isCompressed = true;

	public DDPixelFormat() {
	}

	public void setSize(long long1) throws TextureFormatException {
		if (long1 != 32L) {
			throw new TextureFormatException("Wrong DDPixelFormat size. DDPixelFormat size must be 32!");
		} else {
			this.size = long1;
		}
	}

	public void setFlags(long long1) {
		this.flags = long1;
		if ((long1 & 64L) == 64L) {
			this.isCompressed = false;
		} else if ((long1 & 4L) == 4L) {
			this.isCompressed = true;
		}
	}

	public void setFourCC(long long1) {
		this.fourCC = long1;
		if (this.isCompressed) {
			this.createFourCCString();
		}
	}

	private void createFourCCString() {
		byte[] byteArray = new byte[]{(byte)((int)this.fourCC), (byte)((int)(this.fourCC >> 8)), (byte)((int)(this.fourCC >> 16)), (byte)((int)(this.fourCC >> 24))};
		this.fourCCString = new String(byteArray);
	}

	public String getFourCCString() {
		return this.fourCCString;
	}

	public void setRGBBitCount(long long1) {
		this.rgbAlphaBitMask = long1;
	}

	public void setRBitMask(long long1) {
		this.rBitMask = long1;
	}

	public void setGBitMask(long long1) {
		this.gBitMask = long1;
	}

	public void setBBitMask(long long1) {
		this.bBitMask = long1;
	}

	public void setRGBAlphaBitMask(long long1) {
		this.rgbAlphaBitMask = long1;
	}
}
