package zombie.core.textures;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import zombie.GameWindow;
import zombie.debug.DebugLog;


public class Pcx {
	public static HashMap Cache = new HashMap();
	public byte[] imageData;
	public int imageWidth;
	public int imageHeight;
	public int[] palette;
	public int[] pic;

	public Pcx(String string) {
	}

	public Pcx(URL url) {
	}

	public Pcx(String string, int[] intArray) {
	}

	public Pcx(String string, String string2) {
	}

	public Image getImage() {
		int[] intArray = new int[this.imageWidth * this.imageHeight];
		int int1 = 0;
		int int2 = 0;
		for (int int3 = 0; int3 < this.imageWidth; ++int3) {
			for (int int4 = 0; int4 < this.imageHeight; ++int4) {
				intArray[int1++] = -16777216 | (this.imageData[int2++] & 255) << 16 | (this.imageData[int2++] & 255) << 8 | this.imageData[int2++] & 255;
			}
		}

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		return toolkit.createImage(new MemoryImageSource(this.imageWidth, this.imageHeight, intArray, 0, this.imageWidth));
	}

	int loadPCX(URL url) {
		try {
			InputStream inputStream = url.openStream();
			int int1 = inputStream.available();
			byte[] byteArray = new byte[int1 + 1];
			byteArray[int1] = 0;
			int int2;
			for (int2 = 0; int2 < int1; ++int2) {
				byteArray[int2] = (byte)inputStream.read();
			}

			inputStream.close();
			if (int1 == -1) {
				return -1;
			} else {
				Pcx.pcx_t pcx_t = new Pcx.pcx_t(byteArray);
				byte[] byteArray2 = pcx_t.data;
				if (pcx_t.manufacturer == '\n' && pcx_t.version == 5 && pcx_t.encoding == 1 && pcx_t.bits_per_pixel == '\b' && pcx_t.xmax < 640 && pcx_t.ymax < 480) {
					this.palette = new int[768];
					for (int2 = 0; int2 < 768; ++int2) {
						if (int1 - 128 - 768 + int2 < pcx_t.data.length) {
							this.palette[int2] = pcx_t.data[int1 - 128 - 768 + int2] & 255;
						}
					}

					this.imageWidth = pcx_t.xmax + 1;
					this.imageHeight = pcx_t.ymax + 1;
					int[] intArray = new int[(pcx_t.ymax + 1) * (pcx_t.xmax + 1)];
					this.pic = intArray;
					int[] intArray2 = intArray;
					int2 = 0;
					int int3 = 0;
					for (int int4 = 0; int4 <= pcx_t.ymax; int2 += pcx_t.xmax + 1) {
						int int5 = 0;
						while (int5 <= pcx_t.xmax) {
							byte byte1 = byteArray2[int3++];
							int int6;
							if ((byte1 & 192) == 192) {
								int6 = byte1 & 63;
								byte1 = byteArray2[int3++];
							} else {
								int6 = 1;
							}

							while (int6-- > 0) {
								intArray2[int2 + int5++] = byte1 & 255;
							}
						}

						++int4;
					}

					if (this.pic != null && this.palette != null) {
						this.imageData = new byte[(this.imageWidth + 1) * (this.imageHeight + 1) * 3];
						for (int int7 = 0; int7 < this.imageWidth * this.imageHeight; ++int7) {
							this.imageData[int7 * 3] = (byte)this.palette[this.pic[int7] * 3];
							this.imageData[int7 * 3 + 1] = (byte)this.palette[this.pic[int7] * 3 + 1];
							this.imageData[int7 * 3 + 2] = (byte)this.palette[this.pic[int7] * 3 + 2];
						}

						return 1;
					} else {
						return -1;
					}
				} else {
					DebugLog.log("Bad pcx file " + url);
					return -1;
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return 1;
		}
	}

	int loadPCXminusPal(String string) {
		try {
			if (Cache.containsKey(string)) {
				Pcx pcx = (Pcx)Cache.get(string);
				this.imageWidth = pcx.imageWidth;
				this.imageHeight = pcx.imageHeight;
				this.imageData = new byte[(pcx.imageWidth + 1) * (pcx.imageHeight + 1) * 3];
				for (int int1 = 0; int1 < pcx.imageWidth * pcx.imageHeight; ++int1) {
					this.imageData[int1 * 3] = (byte)this.palette[pcx.pic[int1] * 3];
					this.imageData[int1 * 3 + 1] = (byte)this.palette[pcx.pic[int1] * 3 + 1];
					this.imageData[int1 * 3 + 2] = (byte)this.palette[pcx.pic[int1] * 3 + 2];
				}

				return 1;
			} else {
				InputStream inputStream = GameWindow.class.getClassLoader().getResourceAsStream(string);
				if (inputStream == null) {
					return 0;
				} else {
					int int2 = inputStream.available();
					byte[] byteArray = new byte[int2 + 1];
					byteArray[int2] = 0;
					int int3;
					for (int3 = 0; int3 < int2; ++int3) {
						byteArray[int3] = (byte)inputStream.read();
					}

					inputStream.close();
					if (int2 == -1) {
						return -1;
					} else {
						Pcx.pcx_t pcx_t = new Pcx.pcx_t(byteArray);
						byte[] byteArray2 = pcx_t.data;
						if (pcx_t.manufacturer == '\n' && pcx_t.version == 5 && pcx_t.encoding == 1 && pcx_t.bits_per_pixel == '\b' && pcx_t.xmax < 640 && pcx_t.ymax < 480) {
							this.imageWidth = pcx_t.xmax + 1;
							this.imageHeight = pcx_t.ymax + 1;
							int[] intArray = new int[(pcx_t.ymax + 1) * (pcx_t.xmax + 1)];
							this.pic = intArray;
							int[] intArray2 = intArray;
							int3 = 0;
							int int4 = 0;
							for (int int5 = 0; int5 <= pcx_t.ymax; int3 += pcx_t.xmax + 1) {
								int int6 = 0;
								while (int6 <= pcx_t.xmax) {
									byte byte1 = byteArray2[int4++];
									int int7;
									if ((byte1 & 192) == 192) {
										int7 = byte1 & 63;
										byte1 = byteArray2[int4++];
									} else {
										int7 = 1;
									}

									while (int7-- > 0) {
										intArray2[int3 + int6++] = byte1 & 255;
									}
								}

								++int5;
							}

							if (this.pic != null && this.palette != null) {
								this.imageData = new byte[(this.imageWidth + 1) * (this.imageHeight + 1) * 3];
								for (int int8 = 0; int8 < this.imageWidth * this.imageHeight; ++int8) {
									this.imageData[int8 * 3] = (byte)this.palette[this.pic[int8] * 3];
									this.imageData[int8 * 3 + 1] = (byte)this.palette[this.pic[int8] * 3 + 1];
									this.imageData[int8 * 3 + 2] = (byte)this.palette[this.pic[int8] * 3 + 2];
								}

								Cache.put(string, this);
								return 1;
							} else {
								return -1;
							}
						} else {
							DebugLog.log("Bad pcx file " + string);
							return -1;
						}
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return 1;
		}
	}

	int loadPCXpal(String string) {
		try {
			InputStream inputStream = GameWindow.class.getClassLoader().getResourceAsStream(string);
			if (inputStream == null) {
				return 1;
			} else {
				int int1 = inputStream.available();
				byte[] byteArray = new byte[int1 + 1];
				byteArray[int1] = 0;
				int int2;
				for (int2 = 0; int2 < int1; ++int2) {
					byteArray[int2] = (byte)inputStream.read();
				}

				inputStream.close();
				if (int1 == -1) {
					return -1;
				} else {
					Pcx.pcx_t pcx_t = new Pcx.pcx_t(byteArray);
					byte[] byteArray2 = pcx_t.data;
					if (pcx_t.manufacturer == '\n' && pcx_t.version == 5 && pcx_t.encoding == 1 && pcx_t.bits_per_pixel == '\b' && pcx_t.xmax < 640 && pcx_t.ymax < 480) {
						this.palette = new int[768];
						for (int2 = 0; int2 < 768; ++int2) {
							if (int1 - 128 - 768 + int2 < pcx_t.data.length) {
								this.palette[int2] = pcx_t.data[int1 - 128 - 768 + int2] & 255;
							}
						}

						this.imageWidth = pcx_t.xmax + 1;
						this.imageHeight = pcx_t.ymax + 1;
						int[] intArray = new int[(pcx_t.ymax + 1) * (pcx_t.xmax + 1)];
						this.pic = intArray;
						boolean boolean1 = false;
						boolean boolean2 = false;
						if (this.pic != null && this.palette != null) {
							return 1;
						} else {
							return -1;
						}
					} else {
						DebugLog.log("Bad pcx file " + string);
						return -1;
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return 1;
		}
	}

	private void loadPCXpal(int[] intArray) {
		this.palette = intArray;
	}

	class pcx_t {
		char bits_per_pixel;
		short bytes_per_line;
		char color_planes;
		byte[] data;
		char encoding;
		byte[] filler = new byte[58];
		short hres;
		short vres;
		char manufacturer;
		int[] palette = new int[48];
		short palette_type;
		char reserved;
		char version;
		short xmin;
		short ymin;
		short xmax;
		short ymax;

		pcx_t(byte[] byteArray) {
			this.manufacturer = (char)byteArray[0];
			this.version = (char)byteArray[1];
			this.encoding = (char)byteArray[2];
			this.bits_per_pixel = (char)byteArray[3];
			this.xmin = (short)(byteArray[4] + (byteArray[5] << 8) & 255);
			this.ymin = (short)(byteArray[6] + (byteArray[7] << 8) & 255);
			this.xmax = (short)(byteArray[8] + (byteArray[9] << 8) & 255);
			this.ymax = (short)(byteArray[10] + (byteArray[11] << 8) & 255);
			this.hres = (short)(byteArray[12] + (byteArray[13] << 8) & 255);
			this.vres = (short)(byteArray[14] + (byteArray[15] << 8) & 255);
			int int1;
			for (int1 = 0; int1 < 48; ++int1) {
				this.palette[int1] = byteArray[16 + int1] & 255;
			}

			this.reserved = (char)byteArray[64];
			this.color_planes = (char)byteArray[65];
			this.bytes_per_line = (short)(byteArray[66] + (byteArray[67] << 8) & 255);
			this.palette_type = (short)(byteArray[68] + (byteArray[69] << 8) & 255);
			for (int1 = 0; int1 < 58; ++int1) {
				this.filler[int1] = byteArray[70 + int1];
			}

			this.data = new byte[byteArray.length - 128];
			for (int1 = 0; int1 < byteArray.length - 128; ++int1) {
				this.data[int1] = byteArray[128 + int1];
			}
		}
	}
}
