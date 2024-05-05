package com.sixlegs.png;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class PngImage implements Transparency {
	private static final PngConfig DEFAULT_CONFIG = (new PngConfig.Builder()).build();
	private final PngConfig config;
	private final Map props;
	private boolean read;

	public PngImage() {
		this(DEFAULT_CONFIG);
	}

	public PngImage(PngConfig pngConfig) {
		this.props = new HashMap();
		this.read = false;
		this.config = pngConfig;
	}

	public PngConfig getConfig() {
		return this.config;
	}

	public BufferedImage read(File file) throws IOException {
		return this.read(new BufferedInputStream(new FileInputStream(file)), true);
	}

	public BufferedImage read(InputStream inputStream, boolean boolean1) throws IOException {
		if (inputStream == null) {
			throw new NullPointerException("InputStream is null");
		} else {
			this.read = true;
			this.props.clear();
			int int1 = this.config.getReadLimit();
			BufferedImage bufferedImage = null;
			StateMachine stateMachine = new StateMachine(this);
			try {
				PngInputStream pngInputStream = new PngInputStream(inputStream);
				int int2;
				for (HashSet hashSet = new HashSet(); stateMachine.getState() != 6; pngInputStream.endChunk(int2)) {
					int2 = pngInputStream.startChunk();
					stateMachine.nextState(int2);
					try {
						ImageDataInputStream imageDataInputStream;
						if (int2 == 1229209940) {
							switch (int1) {
							case 2: 
								imageDataInputStream = null;
								return imageDataInputStream;
							
							case 3: 
								break;
							
							default: 
								imageDataInputStream = new ImageDataInputStream(pngInputStream, stateMachine);
								bufferedImage = this.createImage(imageDataInputStream, new Dimension(this.getWidth(), this.getHeight()));
								while ((int2 = stateMachine.getType()) == 1229209940) {
									skipFully(imageDataInputStream, (long)pngInputStream.getRemaining());
								}

							
							}
						}

						String string;
						if (!this.isMultipleOK(int2) && !hashSet.add(Integers.valueOf(int2))) {
							string = PngConstants.getChunkName(int2);
							throw new PngException("Multiple " + string + " chunks are not allowed", !PngConstants.isAncillary(int2));
						}

						try {
							this.readChunk(int2, pngInputStream, pngInputStream.getOffset(), pngInputStream.getRemaining());
						} catch (PngException pngException) {
							throw pngException;
						} catch (IOException ioException) {
							string = PngConstants.getChunkName(int2);
							throw new PngException("Malformed " + string + " chunk", ioException, !PngConstants.isAncillary(int2));
						}

						skipFully(pngInputStream, (long)pngInputStream.getRemaining());
						if (int2 == 1229472850 && int1 == 1) {
							imageDataInputStream = null;
							return imageDataInputStream;
						}
					} catch (PngException pngException2) {
						if (pngException2.isFatal()) {
							throw pngException2;
						}

						skipFully(pngInputStream, (long)pngInputStream.getRemaining());
						this.handleWarning(pngException2);
					}
				}

				BufferedImage bufferedImage2 = bufferedImage;
				return bufferedImage2;
			} finally {
				if (boolean1) {
					inputStream.close();
				}
			}
		}
	}

	protected BufferedImage createImage(InputStream inputStream, Dimension dimension) throws IOException {
		return ImageFactory.createImage(this, inputStream, dimension);
	}

	protected boolean handlePass(BufferedImage bufferedImage, int int1) {
		return true;
	}

	protected boolean handleProgress(BufferedImage bufferedImage, float float1) {
		return true;
	}

	protected void handleWarning(PngException pngException) throws PngException {
		if (this.config.getWarningsFatal()) {
			throw pngException;
		}
	}

	public int getWidth() {
		return this.getInt("width");
	}

	public int getHeight() {
		return this.getInt("height");
	}

	public int getBitDepth() {
		return this.getInt("bit_depth");
	}

	public boolean isInterlaced() {
		return this.getInt("interlace") != 0;
	}

	public int getColorType() {
		return this.getInt("color_type");
	}

	public int getTransparency() {
		int int1 = this.getColorType();
		return int1 != 6 && int1 != 4 && !this.props.containsKey("transparency") && !this.props.containsKey("palette_alpha") ? 1 : 3;
	}

	public int getSamples() {
		switch (this.getColorType()) {
		case 2: 
			return 3;
		
		case 3: 
		
		case 5: 
		
		default: 
			return 1;
		
		case 4: 
			return 2;
		
		case 6: 
			return 4;
		
		}
	}

	public float getGamma() {
		this.assertRead();
		return this.props.containsKey("gamma") ? ((Number)this.getProperty("gamma", Number.class, true)).floatValue() : this.config.getDefaultGamma();
	}

	public short[] getGammaTable() {
		this.assertRead();
		return createGammaTable(this.getGamma(), this.config.getDisplayExponent(), this.getBitDepth() == 16 && !this.config.getReduce16());
	}

	static short[] createGammaTable(float float1, float float2, boolean boolean1) {
		int int1 = 1 << (boolean1 ? 16 : 8);
		short[] shortArray = new short[int1];
		double double1 = 1.0 / ((double)float1 * (double)float2);
		for (int int2 = 0; int2 < int1; ++int2) {
			shortArray[int2] = (short)((int)(Math.pow((double)int2 / (double)(int1 - 1), double1) * (double)(int1 - 1)));
		}

		return shortArray;
	}

	public Color getBackground() {
		int[] intArray = (int[])this.getProperty("background_rgb", int[].class, false);
		if (intArray == null) {
			return null;
		} else {
			switch (this.getColorType()) {
			case 0: 
			
			case 4: 
				int int1 = intArray[0] * 255 / ((1 << this.getBitDepth()) - 1);
				return new Color(int1, int1, int1);
			
			case 1: 
			
			case 2: 
			
			default: 
				if (this.getBitDepth() == 16) {
					return new Color(intArray[0] >> 8, intArray[1] >> 8, intArray[2] >> 8);
				}

				return new Color(intArray[0], intArray[1], intArray[2]);
			
			case 3: 
				byte[] byteArray = (byte[])this.getProperty("palette", byte[].class, true);
				int int2 = intArray[0] * 3;
				return new Color(255 & byteArray[int2 + 0], 255 & byteArray[int2 + 1], 255 & byteArray[int2 + 2]);
			
			}
		}
	}

	public Object getProperty(String string) {
		this.assertRead();
		return this.props.get(string);
	}

	Object getProperty(String string, Class javaClass, boolean boolean1) {
		this.assertRead();
		Object object = this.props.get(string);
		if (object == null) {
			if (boolean1) {
				throw new IllegalStateException("Image is missing property \"" + string + "\"");
			}
		} else if (!javaClass.isAssignableFrom(object.getClass())) {
			throw new IllegalStateException("Property \"" + string + "\" has type " + object.getClass().getName() + ", expected " + javaClass.getName());
		}

		return object;
	}

	private int getInt(String string) {
		return ((Number)this.getProperty(string, Number.class, true)).intValue();
	}

	public Map getProperties() {
		return this.props;
	}

	public TextChunk getTextChunk(String string) {
		List list = (List)this.getProperty("text_chunks", List.class, false);
		if (string != null && list != null) {
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				TextChunk textChunk = (TextChunk)iterator.next();
				if (textChunk.getKeyword().equals(string)) {
					return textChunk;
				}
			}
		}

		return null;
	}

	protected void readChunk(int int1, DataInput dataInput, long long1, int int2) throws IOException {
		if (int1 != 1229209940) {
			if (this.config.getReadLimit() == 4 && PngConstants.isAncillary(int1)) {
				switch (int1) {
				case 1732332865: 
				
				case 1951551059: 
					break;
				
				default: 
					return;
				
				}
			}

			RegisteredChunks.read(int1, dataInput, int2, this);
		}
	}

	protected boolean isMultipleOK(int int1) {
		switch (int1) {
		case 1229209940: 
		
		case 1767135348: 
		
		case 1934642260: 
		
		case 1950701684: 
		
		case 2052348020: 
			return true;
		
		default: 
			return false;
		
		}
	}

	private void assertRead() {
		if (!this.read) {
			throw new IllegalStateException("Image has not been read");
		}
	}

	private static void skipFully(InputStream inputStream, long long1) throws IOException {
		while (long1 > 0L) {
			long long2 = inputStream.skip(long1);
			if (long2 == 0L) {
				if (inputStream.read() == -1) {
					throw new EOFException();
				}

				--long1;
			} else {
				long1 -= long2;
			}
		}
	}
}
