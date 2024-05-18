package com.evildevil.engines.bubble.texture;


final class DDSurfaceDesc2 implements DDSurface {
	private final String DDS_IDENTIFIER = "DDS ";
	protected long identifier = 0L;
	private String identifierString = "";
	protected long size = 0L;
	protected long flags = 0L;
	protected long height = 0L;
	protected long width = 0L;
	protected long pitchOrLinearSize = 0L;
	protected long depth = 0L;
	protected long mipMapCount = 0L;
	protected long reserved = 0L;
	private DDPixelFormat pixelFormat = null;
	private DDSCaps2 caps2 = null;
	protected int reserved2 = 0;

	public DDSurfaceDesc2() {
		this.pixelFormat = new DDPixelFormat();
		this.caps2 = new DDSCaps2();
	}

	public void setIdentifier(long long1) throws TextureFormatException {
		this.identifier = long1;
		this.createIdentifierString();
	}

	private void createIdentifierString() throws TextureFormatException {
		byte[] byteArray = new byte[]{(byte)((int)this.identifier), (byte)((int)(this.identifier >> 8)), (byte)((int)(this.identifier >> 16)), (byte)((int)(this.identifier >> 24))};
		this.identifierString = new String(byteArray);
		String string = this.identifierString;
		this.getClass();
		if (!string.equalsIgnoreCase("DDS ")) {
			throw new TextureFormatException("The DDS Identifier is wrong. Have to be \"DDS \"!");
		}
	}

	public void setSize(long long1) throws TextureFormatException {
		if (long1 != 124L) {
			throw new TextureFormatException("Wrong DDSurfaceDesc2 size. DDSurfaceDesc2 size must be 124!");
		} else {
			this.size = long1;
		}
	}

	public void setFlags(long long1) throws TextureFormatException {
		this.flags = long1;
		if ((long1 & 1L) != 1L || (long1 & 4096L) != 4096L || (long1 & 4L) != 4L || (long1 & 2L) != 2L) {
			throw new TextureFormatException("One or more required flag bits are set wrong\nflags have to include \"DDSD_CAPS, DDSD_PIXELFORMAT, DDSD_WIDTH, DDSD_HEIGHT\"");
		}
	}

	public void setHeight(long long1) {
		this.height = Math.abs(long1);
	}

	public void setWidth(long long1) {
		this.width = long1;
	}

	public void setPitchOrLinearSize(long long1) {
		this.pitchOrLinearSize = long1;
		this.pitchOrLinearSize = (this.width + 3L) / 4L * ((this.height + 3L) / 4L) * 16L;
		if (this.pitchOrLinearSize > 1000000L) {
			this.pitchOrLinearSize = (this.width + 3L) / 4L * ((this.height + 3L) / 4L) * 16L;
		}
	}

	public void setDepth(long long1) {
		this.depth = long1;
	}

	public void setMipMapCount(long long1) {
		this.mipMapCount = long1;
	}

	public void setDDPixelFormat(DDPixelFormat dDPixelFormat) throws NullPointerException {
		if (dDPixelFormat == null) {
			throw new NullPointerException("DDPixelFormat can\'t be null. DDSurfaceDesc2 needs a valid DDPixelFormat.");
		} else {
			this.pixelFormat = dDPixelFormat;
		}
	}

	public DDPixelFormat getDDPixelformat() {
		return this.pixelFormat;
	}

	public void setDDSCaps2(DDSCaps2 dDSCaps2) throws NullPointerException {
		if (dDSCaps2 == null) {
			throw new NullPointerException("DDSCaps can\'t be null. DDSurfaceDesc2 needs a valid DDSCaps2.");
		} else {
			this.caps2 = dDSCaps2;
		}
	}

	public DDSCaps2 getDDSCaps2() {
		return this.caps2;
	}
}
