package zombie.worldMap;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderThread;
import zombie.core.textures.ImageData;
import zombie.core.textures.MipMapLevel;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;


public final class ImagePyramid {
	String m_directory;
	String m_zipFile;
	FileSystem m_zipFS;
	final HashMap m_textures = new HashMap();
	final HashSet m_missing = new HashSet();
	int m_requestNumber = 0;
	int m_minX;
	int m_minY;
	int m_maxX;
	int m_maxY;
	float m_resolution = 1.0F;
	int m_minZ;
	int m_maxZ;
	int MAX_TEXTURES = 100;
	int MAX_REQUEST_NUMBER;

	public ImagePyramid() {
		this.MAX_REQUEST_NUMBER = Core.bDebug ? 10000 : Integer.MAX_VALUE;
	}

	public void setDirectory(String string) {
		if (this.m_zipFile != null) {
			this.m_zipFile = null;
			if (this.m_zipFS != null) {
				try {
					this.m_zipFS.close();
				} catch (IOException ioException) {
				}

				this.m_zipFS = null;
			}
		}

		this.m_directory = string;
	}

	public void setZipFile(String string) {
		this.m_directory = null;
		this.m_zipFile = string;
		this.m_zipFS = this.openZipFile();
		this.readInfoFile();
		this.m_minZ = Integer.MAX_VALUE;
		this.m_maxZ = Integer.MIN_VALUE;
		if (this.m_zipFS != null) {
			try {
				DirectoryStream directoryStream = Files.newDirectoryStream(this.m_zipFS.getPath("/"));
				try {
					Iterator iterator = directoryStream.iterator();
					while (iterator.hasNext()) {
						Path path = (Path)iterator.next();
						if (Files.isDirectory(path, new LinkOption[0])) {
							int int1 = PZMath.tryParseInt(path.getFileName().toString(), -1);
							this.m_minZ = PZMath.min(this.m_minZ, int1);
							this.m_maxZ = PZMath.max(this.m_maxZ, int1);
						}
					}
				} catch (Throwable throwable) {
					if (directoryStream != null) {
						try {
							directoryStream.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}
					}

					throw throwable;
				}

				if (directoryStream != null) {
					directoryStream.close();
				}
			} catch (IOException ioException) {
				ExceptionLogger.logException(ioException);
			}
		}
	}

	public Texture getImage(int int1, int int2, int int3) {
		String string = String.format("%dx%dx%d", int1, int2, int3);
		if (this.m_missing.contains(string)) {
			return null;
		} else {
			File file = new File(this.m_directory, String.format("%s%d%stile%dx%d.png", File.separator, int3, File.separator, int1, int2));
			if (!file.exists()) {
				this.m_missing.add(string);
				return null;
			} else {
				return Texture.getSharedTexture(file.getAbsolutePath());
			}
		}
	}

	public TextureID getTexture(int int1, int int2, int int3) {
		String string = String.format("%dx%dx%d", int1, int2, int3);
		if (this.m_textures.containsKey(string)) {
			ImagePyramid.PyramidTexture pyramidTexture = (ImagePyramid.PyramidTexture)this.m_textures.get(string);
			pyramidTexture.m_requestNumber = this.m_requestNumber++;
			if (this.m_requestNumber >= this.MAX_REQUEST_NUMBER) {
				this.resetRequestNumbers();
			}

			return pyramidTexture.m_textureID;
		} else if (this.m_missing.contains(string)) {
			return null;
		} else if (this.m_zipFile == null) {
			File file = new File(this.m_directory, String.format("%s%d%stile%dx%d.png", File.separator, int3, File.separator, int1, int2));
			if (!file.exists()) {
				this.m_missing.add(string);
				return null;
			} else {
				Texture texture = Texture.getSharedTexture(file.getAbsolutePath());
				return texture == null ? null : texture.getTextureId();
			}
		} else if (this.m_zipFS != null && this.m_zipFS.isOpen()) {
			try {
				Path path = this.m_zipFS.getPath(String.valueOf(int3), String.format("tile%dx%d.png", int1, int2));
				try {
					InputStream inputStream = Files.newInputStream(path);
					TextureID textureID;
					try {
						ImageData imageData = new ImageData(inputStream, false);
						ImagePyramid.PyramidTexture pyramidTexture2 = this.checkTextureCache(string);
						if (pyramidTexture2.m_textureID == null) {
							textureID = new TextureID(imageData);
							pyramidTexture2.m_textureID = textureID;
						} else {
							this.replaceTextureData(pyramidTexture2, imageData);
						}

						textureID = pyramidTexture2.m_textureID;
					} catch (Throwable throwable) {
						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (Throwable throwable2) {
								throwable.addSuppressed(throwable2);
							}
						}

						throw throwable;
					}

					if (inputStream != null) {
						inputStream.close();
					}

					return textureID;
				} catch (NoSuchFileException noSuchFileException) {
					this.m_missing.add(string);
				}
			} catch (Exception exception) {
				this.m_missing.add(string);
				exception.printStackTrace();
			}

			return null;
		} else {
			return null;
		}
	}

	private void replaceTextureData(ImagePyramid.PyramidTexture pyramidTexture, ImageData imageData) {
		char char1;
		if (GL.getCapabilities().GL_ARB_texture_compression) {
			char1 = '蓮';
		} else {
			char1 = 6408;
		}

		GL11.glBindTexture(3553, Texture.lastTextureID = pyramidTexture.m_textureID.getID());
		SpriteRenderer.ringBuffer.restoreBoundTextures = true;
		GL11.glTexImage2D(3553, 0, char1, imageData.getWidthHW(), imageData.getHeightHW(), 0, 6408, 5121, imageData.getData().getBuffer());
		imageData.dispose();
	}

	public void generateFiles(String string, String string2) throws Exception {
		ImageData imageData = new ImageData(string);
		if (imageData != null) {
			short short1 = 256;
			byte byte1 = 5;
			for (int int1 = 0; int1 < byte1; ++int1) {
				MipMapLevel mipMapLevel = imageData.getMipMapData(int1);
				float float1 = (float)imageData.getWidth() / (float)(1 << int1);
				float float2 = (float)imageData.getHeight() / (float)(1 << int1);
				int int2 = (int)Math.ceil((double)(float1 / (float)short1));
				int int3 = (int)Math.ceil((double)(float2 / (float)short1));
				for (int int4 = 0; int4 < int3; ++int4) {
					for (int int5 = 0; int5 < int2; ++int5) {
						BufferedImage bufferedImage = this.getBufferedImage(mipMapLevel, int5, int4, short1);
						this.writeImageToFile(bufferedImage, string2, int5, int4, int1);
					}
				}
			}
		}
	}

	public FileSystem openZipFile() {
		try {
			return FileSystems.newFileSystem(Paths.get(this.m_zipFile));
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return null;
		}
	}

	public void generateZip(String string, String string2) throws Exception {
		ImageData imageData = new ImageData(string);
		if (imageData != null) {
			short short1 = 256;
			FileOutputStream fileOutputStream = new FileOutputStream(string2);
			try {
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
				try {
					ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);
					try {
						byte byte1 = 5;
						for (int int1 = 0; int1 < byte1; ++int1) {
							MipMapLevel mipMapLevel = imageData.getMipMapData(int1);
							float float1 = (float)imageData.getWidth() / (float)(1 << int1);
							float float2 = (float)imageData.getHeight() / (float)(1 << int1);
							int int2 = (int)Math.ceil((double)(float1 / (float)short1));
							int int3 = (int)Math.ceil((double)(float2 / (float)short1));
							for (int int4 = 0; int4 < int3; ++int4) {
								for (int int5 = 0; int5 < int2; ++int5) {
									BufferedImage bufferedImage = this.getBufferedImage(mipMapLevel, int5, int4, short1);
									this.writeImageToZip(bufferedImage, zipOutputStream, int5, int4, int1);
								}
							}

							if (float1 <= (float)short1 && float2 <= (float)short1) {
								break;
							}
						}
					} catch (Throwable throwable) {
						try {
							zipOutputStream.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					zipOutputStream.close();
				} catch (Throwable throwable3) {
					try {
						bufferedOutputStream.close();
					} catch (Throwable throwable4) {
						throwable3.addSuppressed(throwable4);
					}

					throw throwable3;
				}

				bufferedOutputStream.close();
			} catch (Throwable throwable5) {
				try {
					fileOutputStream.close();
				} catch (Throwable throwable6) {
					throwable5.addSuppressed(throwable6);
				}

				throw throwable5;
			}

			fileOutputStream.close();
		}
	}

	BufferedImage getBufferedImage(MipMapLevel mipMapLevel, int int1, int int2, int int3) {
		BufferedImage bufferedImage = new BufferedImage(int3, int3, 2);
		int[] intArray = new int[int3];
		IntBuffer intBuffer = mipMapLevel.getBuffer().asIntBuffer();
		for (int int4 = 0; int4 < int3; ++int4) {
			intBuffer.get(int1 * int3 + (int2 * int3 + int4) * mipMapLevel.width, intArray);
			for (int int5 = 0; int5 < int3; ++int5) {
				int int6 = intArray[int5];
				int int7 = int6 & 255;
				int int8 = int6 >> 8 & 255;
				int int9 = int6 >> 16 & 255;
				int int10 = int6 >> 24 & 255;
				intArray[int5] = int10 << 24 | int7 << 16 | int8 << 8 | int9;
			}

			bufferedImage.setRGB(0, int4, int3, 1, intArray, 0, int3);
		}

		return bufferedImage;
	}

	void writeImageToFile(BufferedImage bufferedImage, String string, int int1, int int2, int int3) throws Exception {
		File file = new File(string + File.separator + int3);
		if (file.exists() || file.mkdirs()) {
			file = new File(file, String.format("tile%dx%d.png", int1, int2));
			ImageIO.write(bufferedImage, "png", file);
		}
	}

	void writeImageToZip(BufferedImage bufferedImage, ZipOutputStream zipOutputStream, int int1, int int2, int int3) throws Exception {
		zipOutputStream.putNextEntry(new ZipEntry(String.format("%d/tile%dx%d.png", int3, int1, int2)));
		ImageIO.write(bufferedImage, "PNG", zipOutputStream);
		zipOutputStream.closeEntry();
	}

	ImagePyramid.PyramidTexture checkTextureCache(String string) {
		ImagePyramid.PyramidTexture pyramidTexture;
		if (this.m_textures.size() < this.MAX_TEXTURES) {
			pyramidTexture = new ImagePyramid.PyramidTexture();
			pyramidTexture.m_key = string;
			pyramidTexture.m_requestNumber = this.m_requestNumber++;
			this.m_textures.put(string, pyramidTexture);
			if (this.m_requestNumber >= this.MAX_REQUEST_NUMBER) {
				this.resetRequestNumbers();
			}

			return pyramidTexture;
		} else {
			pyramidTexture = null;
			Iterator iterator = this.m_textures.values().iterator();
			while (true) {
				ImagePyramid.PyramidTexture pyramidTexture2;
				do {
					if (!iterator.hasNext()) {
						this.m_textures.remove(pyramidTexture.m_key);
						pyramidTexture.m_key = string;
						pyramidTexture.m_requestNumber = this.m_requestNumber++;
						this.m_textures.put(pyramidTexture.m_key, pyramidTexture);
						if (this.m_requestNumber >= this.MAX_REQUEST_NUMBER) {
							this.resetRequestNumbers();
						}

						return pyramidTexture;
					}

					pyramidTexture2 = (ImagePyramid.PyramidTexture)iterator.next();
				}		 while (pyramidTexture != null && pyramidTexture.m_requestNumber <= pyramidTexture2.m_requestNumber);

				pyramidTexture = pyramidTexture2;
			}
		}
	}

	void resetRequestNumbers() {
		ArrayList arrayList = new ArrayList(this.m_textures.values());
		arrayList.sort(Comparator.comparingInt((var0)->{
			return var0.m_requestNumber;
		}));
		this.m_requestNumber = 1;
		ImagePyramid.PyramidTexture pyramidTexture;
		for (Iterator iterator = arrayList.iterator(); iterator.hasNext(); pyramidTexture.m_requestNumber = this.m_requestNumber++) {
			pyramidTexture = (ImagePyramid.PyramidTexture)iterator.next();
		}

		arrayList.clear();
	}

	private void readInfoFile() {
		if (this.m_zipFS != null && this.m_zipFS.isOpen()) {
			Path path = this.m_zipFS.getPath("pyramid.txt");
			try {
				InputStream inputStream = Files.newInputStream(path);
				try {
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					try {
						BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
						String string;
						try {
							while ((string = bufferedReader.readLine()) != null) {
								if (string.startsWith("VERSION=")) {
									string = string.substring("VERSION=".length());
								} else if (string.startsWith("bounds=")) {
									string = string.substring("bounds=".length());
									String[] stringArray = string.split(" ");
									if (stringArray.length == 4) {
										this.m_minX = PZMath.tryParseInt(stringArray[0], -1);
										this.m_minY = PZMath.tryParseInt(stringArray[1], -1);
										this.m_maxX = PZMath.tryParseInt(stringArray[2], -1);
										this.m_maxY = PZMath.tryParseInt(stringArray[3], -1);
									}
								} else if (string.startsWith("resolution=")) {
									string = string.substring("resolution=".length());
									this.m_resolution = PZMath.tryParseFloat(string, 1.0F);
								}
							}
						} catch (Throwable throwable) {
							try {
								bufferedReader.close();
							} catch (Throwable throwable2) {
								throwable.addSuppressed(throwable2);
							}

							throw throwable;
						}

						bufferedReader.close();
					} catch (Throwable throwable3) {
						try {
							inputStreamReader.close();
						} catch (Throwable throwable4) {
							throwable3.addSuppressed(throwable4);
						}

						throw throwable3;
					}

					inputStreamReader.close();
				} catch (Throwable throwable5) {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (Throwable throwable6) {
							throwable5.addSuppressed(throwable6);
						}
					}

					throw throwable5;
				}

				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public void destroy() {
		if (this.m_zipFS != null) {
			try {
				this.m_zipFS.close();
			} catch (IOException ioException) {
			}

			this.m_zipFS = null;
		}

		RenderThread.invokeOnRenderContext(()->{
			Iterator iterator = this.m_textures.values().iterator();
			while (iterator.hasNext()) {
				ImagePyramid.PyramidTexture ioException = (ImagePyramid.PyramidTexture)iterator.next();
				ioException.m_textureID.destroy();
			}
		});
		this.m_missing.clear();
		this.m_textures.clear();
	}

	public static final class PyramidTexture {
		String m_key;
		int m_requestNumber;
		TextureID m_textureID;
	}
}
