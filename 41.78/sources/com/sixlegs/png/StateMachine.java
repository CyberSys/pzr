package com.sixlegs.png;


class StateMachine {
	public static final int STATE_START = 0;
	public static final int STATE_SAW_IHDR = 1;
	public static final int STATE_SAW_IHDR_NO_PLTE = 2;
	public static final int STATE_SAW_PLTE = 3;
	public static final int STATE_IN_IDAT = 4;
	public static final int STATE_AFTER_IDAT = 5;
	public static final int STATE_END = 6;
	private PngImage png;
	private int state = 0;
	private int type;

	public StateMachine(PngImage pngImage) {
		this.png = pngImage;
	}

	public int getState() {
		return this.state;
	}

	public int getType() {
		return this.type;
	}

	public void nextState(int int1) throws PngException {
		this.state = nextState(this.png, this.state, this.type = int1);
	}

	private static int nextState(PngImage pngImage, int int1, int int2) throws PngException {
		for (int int3 = 0; int3 < 4; ++int3) {
			int int4 = 255 & int2 >>> 8 * int3;
			if (int4 < 65 || int4 > 90 && int4 < 97 || int4 > 122) {
				throw new PngException("Corrupted chunk type: 0x" + Integer.toHexString(int2), true);
			}
		}

		if (PngConstants.isPrivate(int2) && !PngConstants.isAncillary(int2)) {
			throw new PngException("Private critical chunk encountered: " + PngConstants.getChunkName(int2), true);
		} else {
			switch (int1) {
			case 0: 
				if (int2 == 1229472850) {
					return 1;
				}

				throw new PngException("IHDR chunk must be first chunk", true);
			
			case 1: 
			
			case 2: 
				switch (int2) {
				case 1229209940: 
					errorIfPaletted(pngImage);
					return 4;
				
				case 1347179589: 
					return 3;
				
				case 1649100612: 
					return 2;
				
				case 1749635924: 
					throw new PngException("PLTE must precede hIST", true);
				
				case 1951551059: 
					errorIfPaletted(pngImage);
					return 2;
				
				default: 
					return int1;
				
				}

			
			case 3: 
				switch (int2) {
				case 1229209940: 
					return 4;
				
				case 1229278788: 
					throw new PngException("Required data chunk(s) not found", true);
				
				case 1665684045: 
				
				case 1732332865: 
				
				case 1766015824: 
				
				case 1933723988: 
				
				case 1934772034: 
					throw new PngException(PngConstants.getChunkName(int2) + " cannot appear after PLTE", true);
				
				default: 
					return 3;
				
				}

			
			default: 
				switch (int2) {
				case 1229209940: 
					if (int1 == 4) {
						return 4;
					}

					throw new PngException("IDAT chunks must be consecutive", true);
				
				case 1229278788: 
					return 6;
				
				case 1347179589: 
				
				case 1649100612: 
				
				case 1665684045: 
				
				case 1732332865: 
				
				case 1749635924: 
				
				case 1766015824: 
				
				case 1866876531: 
				
				case 1883455820: 
				
				case 1883789683: 
				
				case 1933723988: 
				
				case 1933787468: 
				
				case 1934642260: 
				
				case 1934772034: 
				
				case 1934902610: 
				
				case 1951551059: 
					throw new PngException(PngConstants.getChunkName(int2) + " cannot appear after IDAT", true);
				
				default: 
					return 5;
				
				}

			
			}
		}
	}

	private static void errorIfPaletted(PngImage pngImage) throws PngException {
		if (pngImage.getColorType() == 3) {
			throw new PngException("Required PLTE chunk not found", true);
		}
	}
}
