package zombie.worldMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import zombie.SandboxOptions;
import zombie.ZomboidFileSystem;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.ShaderProgram;
import zombie.core.textures.TextureID;
import zombie.core.utils.ImageUtils;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.SliceY;
import zombie.iso.Vector2;
import zombie.worldMap.styles.WorldMapStyleLayer;


public class WorldMapVisited {
	private static WorldMapVisited instance;
	private int m_minX;
	private int m_minY;
	private int m_maxX;
	private int m_maxY;
	byte[] m_visited;
	boolean m_changed = false;
	int m_changeX1 = 0;
	int m_changeY1 = 0;
	int m_changeX2 = 0;
	int m_changeY2 = 0;
	private final int[] m_updateMinX = new int[4];
	private final int[] m_updateMinY = new int[4];
	private final int[] m_updateMaxX = new int[4];
	private final int[] m_updateMaxY = new int[4];
	private static final int TEXTURE_BPP = 4;
	private TextureID m_textureID;
	private int m_textureW = 0;
	private int m_textureH = 0;
	private ByteBuffer m_textureBuffer;
	private boolean m_textureChanged = false;
	private final WorldMapStyleLayer.RGBAf m_color = (new WorldMapStyleLayer.RGBAf()).init(0.85882354F, 0.84313726F, 0.7529412F, 1.0F);
	private final WorldMapStyleLayer.RGBAf m_gridColor;
	private boolean m_mainMenu;
	private static ShaderProgram m_shaderProgram;
	private static ShaderProgram m_gridShaderProgram;
	static final int UNITS_PER_CELL = 10;
	static final int SQUARES_PER_CELL = 300;
	static final int SQUARES_PER_UNIT = 30;
	static final int TEXTURE_PAD = 1;
	static final int BIT_VISITED = 1;
	static final int BIT_KNOWN = 2;
	Vector2 m_vector2;

	public WorldMapVisited() {
		this.m_gridColor = (new WorldMapStyleLayer.RGBAf()).init(this.m_color.r * 0.85F, this.m_color.g * 0.85F, this.m_color.b * 0.85F, 1.0F);
		this.m_mainMenu = false;
		this.m_vector2 = new Vector2();
		Arrays.fill(this.m_updateMinX, -1);
		Arrays.fill(this.m_updateMinY, -1);
		Arrays.fill(this.m_updateMaxX, -1);
		Arrays.fill(this.m_updateMaxY, -1);
	}

	public void setBounds(int int1, int int2, int int3, int int4) {
		if (int1 > int3 || int2 > int4) {
			int4 = 0;
			int2 = 0;
			int3 = 0;
			int1 = 0;
			this.m_mainMenu = true;
		}

		this.m_minX = int1;
		this.m_minY = int2;
		this.m_maxX = int3;
		this.m_maxY = int4;
		this.m_changed = true;
		this.m_changeX1 = 0;
		this.m_changeY1 = 0;
		this.m_changeX2 = this.getWidthInCells() * 10 - 1;
		this.m_changeY2 = this.getHeightInCells() * 10 - 1;
		this.m_visited = new byte[this.getWidthInCells() * 10 * this.getHeightInCells() * 10];
		this.m_textureW = this.calcTextureWidth();
		this.m_textureH = this.calcTextureHeight();
		this.m_textureBuffer = BufferUtils.createByteBuffer(this.m_textureW * this.m_textureH * 4);
		this.m_textureBuffer.limit(this.m_textureBuffer.capacity());
		int int5 = SandboxOptions.getInstance().Map.MapAllKnown.getValue() ? 0 : -1;
		byte byte1 = -1;
		byte byte2 = -1;
		byte byte3 = -1;
		for (int int6 = 0; int6 < this.m_textureBuffer.limit(); int6 += 4) {
			this.m_textureBuffer.put(int6, (byte)int5);
			this.m_textureBuffer.put(int6 + 1, byte1);
			this.m_textureBuffer.put(int6 + 2, byte2);
			this.m_textureBuffer.put(int6 + 3, byte3);
		}

		this.m_textureID = new TextureID(this.m_textureW, this.m_textureH, 0);
	}

	public int getMinX() {
		return this.m_minX;
	}

	public int getMinY() {
		return this.m_minY;
	}

	private int getWidthInCells() {
		return this.m_maxX - this.m_minX + 1;
	}

	private int getHeightInCells() {
		return this.m_maxY - this.m_minY + 1;
	}

	private int calcTextureWidth() {
		return ImageUtils.getNextPowerOfTwo(this.getWidthInCells() * 10 + 2);
	}

	private int calcTextureHeight() {
		return ImageUtils.getNextPowerOfTwo(this.getHeightInCells() * 10 + 2);
	}

	public void setKnownInCells(int int1, int int2, int int3, int int4) {
		this.setFlags(int1 * 300, int2 * 300, (int3 + 1) * 300, (int4 + 1) * 300, 2);
	}

	public void clearKnownInCells(int int1, int int2, int int3, int int4) {
		this.clearFlags(int1 * 300, int2 * 300, (int3 + 1) * 300, (int4 + 1) * 300, 2);
	}

	public void setVisitedInCells(int int1, int int2, int int3, int int4) {
		this.setFlags(int1 * 300, int2 * 300, int3 * 300, int4 * 300, 1);
	}

	public void clearVisitedInCells(int int1, int int2, int int3, int int4) {
		this.clearFlags(int1 * 300, int2 * 300, int3 * 300, int4 * 300, 1);
	}

	public void setKnownInSquares(int int1, int int2, int int3, int int4) {
		this.setFlags(int1, int2, int3, int4, 2);
	}

	public void clearKnownInSquares(int int1, int int2, int int3, int int4) {
		this.clearFlags(int1, int2, int3, int4, 2);
	}

	public void setVisitedInSquares(int int1, int int2, int int3, int int4) {
		this.setFlags(int1, int2, int3, int4, 1);
	}

	public void clearVisitedInSquares(int int1, int int2, int int3, int int4) {
		this.clearFlags(int1, int2, int3, int4, 1);
	}

	private void updateVisitedTexture() {
		this.m_textureID.bind();
		GL11.glTexImage2D(3553, 0, 6408, this.m_textureW, this.m_textureH, 0, 6408, 5121, this.m_textureBuffer);
	}

	public void renderMain() {
		this.m_textureChanged |= this.updateTextureData(this.m_textureBuffer, this.m_textureW);
	}

	private void initShader() {
		m_shaderProgram = ShaderProgram.createShaderProgram("worldMapVisited", false, true);
		if (m_shaderProgram.isCompiled()) {
		}
	}

	public void render(float float1, float float2, int int1, int int2, int int3, int int4, float float3, boolean boolean1) {
		if (!this.m_mainMenu) {
			GL13.glActiveTexture(33984);
			GL13.glClientActiveTexture(33984);
			GL11.glEnable(3553);
			if (this.m_textureChanged) {
				this.m_textureChanged = false;
				this.updateVisitedTexture();
			}

			this.m_textureID.bind();
			int int5 = boolean1 ? 9729 : 9728;
			GL11.glTexParameteri(3553, 10241, int5);
			GL11.glTexParameteri(3553, 10240, int5);
			GL11.glEnable(3042);
			GL11.glTexEnvi(8960, 8704, 8448);
			GL11.glColor4f(this.m_color.r, this.m_color.g, this.m_color.b, this.m_color.a);
			if (m_shaderProgram == null) {
				this.initShader();
			}

			if (m_shaderProgram.isCompiled()) {
				m_shaderProgram.Start();
				float float4 = (float)(1 + (int1 - this.m_minX) * 10) / (float)this.m_textureW;
				float float5 = (float)(1 + (int2 - this.m_minY) * 10) / (float)this.m_textureH;
				float float6 = (float)(1 + (int3 + 1 - this.m_minX) * 10) / (float)this.m_textureW;
				float float7 = (float)(1 + (int4 + 1 - this.m_minY) * 10) / (float)this.m_textureH;
				float float8 = (float)((int1 - this.m_minX) * 300) * float3;
				float float9 = (float)((int2 - this.m_minY) * 300) * float3;
				float float10 = (float)((int3 + 1 - this.m_minX) * 300) * float3;
				float float11 = (float)((int4 + 1 - this.m_minY) * 300) * float3;
				GL11.glBegin(7);
				GL11.glTexCoord2f(float4, float5);
				GL11.glVertex2f(float1 + float8, float2 + float9);
				GL11.glTexCoord2f(float6, float5);
				GL11.glVertex2f(float1 + float10, float2 + float9);
				GL11.glTexCoord2f(float6, float7);
				GL11.glVertex2f(float1 + float10, float2 + float11);
				GL11.glTexCoord2f(float4, float7);
				GL11.glVertex2f(float1 + float8, float2 + float11);
				GL11.glEnd();
				m_shaderProgram.End();
			}
		}
	}

	public void renderGrid(float float1, float float2, int int1, int int2, int int3, int int4, float float3, float float4) {
		if (!(float4 < 11.0F)) {
			if (m_gridShaderProgram == null) {
				m_gridShaderProgram = ShaderProgram.createShaderProgram("worldMapGrid", false, true);
			}

			if (m_gridShaderProgram.isCompiled()) {
				m_gridShaderProgram.Start();
				float float5 = float1 + (float)(int1 * 300 - this.m_minX * 300) * float3;
				float float6 = float2 + (float)(int2 * 300 - this.m_minY * 300) * float3;
				float float7 = float5 + (float)((int3 - int1 + 1) * 300) * float3;
				float float8 = float6 + (float)((int4 - int2 + 1) * 300) * float3;
				VBOLinesUV vBOLinesUV = WorldMapRenderer.m_vboLinesUV;
				vBOLinesUV.setMode(1);
				vBOLinesUV.setLineWidth(0.5F);
				vBOLinesUV.startRun(this.m_textureID);
				float float9 = this.m_gridColor.r;
				float float10 = this.m_gridColor.g;
				float float11 = this.m_gridColor.b;
				float float12 = this.m_gridColor.a;
				byte byte1 = 1;
				if (float4 < 13.0F) {
					byte1 = 8;
				} else if (float4 < 14.0F) {
					byte1 = 4;
				} else if (float4 < 15.0F) {
					byte1 = 2;
				}

				m_gridShaderProgram.setValue("UVOffset", this.m_vector2.set(0.5F / (float)this.m_textureW, 0.0F));
				int int5;
				for (int5 = int1 * 10; int5 <= (int3 + 1) * 10; int5 += byte1) {
					vBOLinesUV.reserve(2);
					vBOLinesUV.addElement(float1 + (float)(int5 * 30 - this.m_minX * 300) * float3, float6, 0.0F, (float)(1 + int5 - this.m_minX * 10) / (float)this.m_textureW, 1.0F / (float)this.m_textureH, float9, float10, float11, float12);
					vBOLinesUV.addElement(float1 + (float)(int5 * 30 - this.m_minX * 300) * float3, float8, 0.0F, (float)(1 + int5 - this.m_minX * 10) / (float)this.m_textureW, (float)(1 + this.getHeightInCells() * 10) / (float)this.m_textureH, float9, float10, float11, float12);
				}

				m_gridShaderProgram.setValue("UVOffset", this.m_vector2.set(0.0F, 0.5F / (float)this.m_textureH));
				for (int5 = int2 * 10; int5 <= (int4 + 1) * 10; int5 += byte1) {
					vBOLinesUV.reserve(2);
					vBOLinesUV.addElement(float5, float2 + (float)(int5 * 30 - this.m_minY * 300) * float3, 0.0F, 1.0F / (float)this.m_textureW, (float)(1 + int5 - this.m_minY * 10) / (float)this.m_textureH, float9, float10, float11, float12);
					vBOLinesUV.addElement(float7, float2 + (float)(int5 * 30 - this.m_minY * 300) * float3, 0.0F, (float)(1 + this.getWidthInCells() * 10) / (float)this.m_textureW, (float)(1 + int5 - this.m_minY * 10) / (float)this.m_textureH, float9, float10, float11, float12);
				}

				vBOLinesUV.flush();
				m_gridShaderProgram.End();
			}
		}
	}

	private void destroy() {
		if (this.m_textureID != null) {
			TextureID textureID = this.m_textureID;
			Objects.requireNonNull(textureID);
			RenderThread.invokeOnRenderContext(textureID::destroy);
		}

		this.m_textureBuffer = null;
		this.m_visited = null;
	}

	public void save(ByteBuffer byteBuffer) {
		byteBuffer.putInt(this.m_minX);
		byteBuffer.putInt(this.m_minY);
		byteBuffer.putInt(this.m_maxX);
		byteBuffer.putInt(this.m_maxY);
		byteBuffer.putInt(10);
		byteBuffer.put(this.m_visited);
	}

	public void load(ByteBuffer byteBuffer, int int1) {
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		int int5 = byteBuffer.getInt();
		int int6 = byteBuffer.getInt();
		if (int2 == this.m_minX && int3 == this.m_minY && int4 == this.m_maxX && int5 == this.m_maxY && int6 == 10) {
			byteBuffer.get(this.m_visited);
		}
	}

	public void save() throws IOException {
		ByteBuffer byteBuffer = SliceY.SliceBuffer;
		byteBuffer.clear();
		byteBuffer.putInt(186);
		this.save(byteBuffer);
		String string = ZomboidFileSystem.instance.getGameModeCacheDir();
		File file = new File(string + File.separator + Core.GameSaveWorld + File.separator + "map_visited.bin");
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		try {
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
			try {
				bufferedOutputStream.write(byteBuffer.array(), 0, byteBuffer.position());
			} catch (Throwable throwable) {
				try {
					bufferedOutputStream.close();
				} catch (Throwable throwable2) {
					throwable.addSuppressed(throwable2);
				}

				throw throwable;
			}

			bufferedOutputStream.close();
		} catch (Throwable throwable3) {
			try {
				fileOutputStream.close();
			} catch (Throwable throwable4) {
				throwable3.addSuppressed(throwable4);
			}

			throw throwable3;
		}

		fileOutputStream.close();
	}

	public void load() throws IOException {
		String string = ZomboidFileSystem.instance.getGameModeCacheDir();
		File file = new File(string + File.separator + Core.GameSaveWorld + File.separator + "map_visited.bin");
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			try {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				try {
					ByteBuffer byteBuffer = SliceY.SliceBuffer;
					byteBuffer.clear();
					int int1 = bufferedInputStream.read(byteBuffer.array());
					byteBuffer.limit(int1);
					int int2 = byteBuffer.getInt();
					this.load(byteBuffer, int2);
				} catch (Throwable throwable) {
					try {
						bufferedInputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedInputStream.close();
			} catch (Throwable throwable3) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileInputStream.close();
		} catch (FileNotFoundException fileNotFoundException) {
		}
	}

	private void setFlags(int int1, int int2, int int3, int int4, int int5) {
		int1 -= this.m_minX * 300;
		int2 -= this.m_minY * 300;
		int3 -= this.m_minX * 300;
		int4 -= this.m_minY * 300;
		int int6 = this.getWidthInCells();
		int int7 = this.getHeightInCells();
		int1 = PZMath.clamp(int1, 0, int6 * 300 - 1);
		int2 = PZMath.clamp(int2, 0, int7 * 300 - 1);
		int3 = PZMath.clamp(int3, 0, int6 * 300 - 1);
		int4 = PZMath.clamp(int4, 0, int7 * 300 - 1);
		if (int1 != int3 && int2 != int4) {
			int int8 = int1 / 30;
			int int9 = int3 / 30;
			int int10 = int2 / 30;
			int int11 = int4 / 30;
			if (int3 % 30 == 0) {
				--int9;
			}

			if (int4 % 30 == 0) {
				--int11;
			}

			boolean boolean1 = false;
			int int12 = int6 * 10;
			for (int int13 = int10; int13 <= int11; ++int13) {
				for (int int14 = int8; int14 <= int9; ++int14) {
					byte byte1 = this.m_visited[int14 + int13 * int12];
					if ((byte1 & int5) != int5) {
						this.m_visited[int14 + int13 * int12] = (byte)(byte1 | int5);
						boolean1 = true;
					}
				}
			}

			if (boolean1) {
				this.m_changed = true;
				this.m_changeX1 = PZMath.min(this.m_changeX1, int8);
				this.m_changeY1 = PZMath.min(this.m_changeY1, int10);
				this.m_changeX2 = PZMath.max(this.m_changeX2, int9);
				this.m_changeY2 = PZMath.max(this.m_changeY2, int11);
			}
		}
	}

	private void clearFlags(int int1, int int2, int int3, int int4, int int5) {
		int1 -= this.m_minX * 300;
		int2 -= this.m_minY * 300;
		int3 -= this.m_minX * 300;
		int4 -= this.m_minY * 300;
		int int6 = this.getWidthInCells();
		int int7 = this.getHeightInCells();
		int1 = PZMath.clamp(int1, 0, int6 * 300 - 1);
		int2 = PZMath.clamp(int2, 0, int7 * 300 - 1);
		int3 = PZMath.clamp(int3, 0, int6 * 300 - 1);
		int4 = PZMath.clamp(int4, 0, int7 * 300 - 1);
		if (int1 != int3 && int2 != int4) {
			int int8 = int1 / 30;
			int int9 = int3 / 30;
			int int10 = int2 / 30;
			int int11 = int4 / 30;
			if (int3 % 30 == 0) {
				--int9;
			}

			if (int4 % 30 == 0) {
				--int11;
			}

			boolean boolean1 = false;
			int int12 = int6 * 10;
			for (int int13 = int10; int13 <= int11; ++int13) {
				for (int int14 = int8; int14 <= int9; ++int14) {
					byte byte1 = this.m_visited[int14 + int13 * int12];
					if ((byte1 & int5) != 0) {
						this.m_visited[int14 + int13 * int12] = (byte)(byte1 & ~int5);
						boolean1 = true;
					}
				}
			}

			if (boolean1) {
				this.m_changed = true;
				this.m_changeX1 = PZMath.min(this.m_changeX1, int8);
				this.m_changeY1 = PZMath.min(this.m_changeY1, int10);
				this.m_changeX2 = PZMath.max(this.m_changeX2, int9);
				this.m_changeY2 = PZMath.max(this.m_changeY2, int11);
			}
		}
	}

	private boolean updateTextureData(ByteBuffer byteBuffer, int int1) {
		if (!this.m_changed) {
			return false;
		} else {
			this.m_changed = false;
			byte byte1 = 4;
			int int2 = this.getWidthInCells() * 10;
			for (int int3 = this.m_changeY1; int3 <= this.m_changeY2; ++int3) {
				byteBuffer.position((1 + this.m_changeX1) * byte1 + (1 + int3) * int1 * byte1);
				for (int int4 = this.m_changeX1; int4 <= this.m_changeX2; ++int4) {
					byte byte2 = this.m_visited[int4 + int3 * int2];
					byteBuffer.put((byte)((byte2 & 2) != 0 ? 0 : -1));
					byteBuffer.put((byte)((byte2 & 1) != 0 ? 0 : -1));
					byteBuffer.put((byte)-1);
					byteBuffer.put((byte)-1);
				}
			}

			byteBuffer.position(0);
			this.m_changeX1 = Integer.MAX_VALUE;
			this.m_changeY1 = Integer.MAX_VALUE;
			this.m_changeX2 = Integer.MIN_VALUE;
			this.m_changeY2 = Integer.MIN_VALUE;
			return true;
		}
	}

	void setUnvisitedRGBA(float float1, float float2, float float3, float float4) {
		this.m_color.init(float1, float2, float3, float4);
	}

	void setUnvisitedGridRGBA(float float1, float float2, float float3, float float4) {
		this.m_gridColor.init(float1, float2, float3, float4);
	}

	boolean hasFlags(int int1, int int2, int int3, int int4, int int5, boolean boolean1) {
		int1 -= this.m_minX * 300;
		int2 -= this.m_minY * 300;
		int3 -= this.m_minX * 300;
		int4 -= this.m_minY * 300;
		int int6 = this.getWidthInCells();
		int int7 = this.getHeightInCells();
		int1 = PZMath.clamp(int1, 0, int6 * 300 - 1);
		int2 = PZMath.clamp(int2, 0, int7 * 300 - 1);
		int3 = PZMath.clamp(int3, 0, int6 * 300 - 1);
		int4 = PZMath.clamp(int4, 0, int7 * 300 - 1);
		if (int1 != int3 && int2 != int4) {
			int int8 = int1 / 30;
			int int9 = int3 / 30;
			int int10 = int2 / 30;
			int int11 = int4 / 30;
			if (int3 % 30 == 0) {
				--int9;
			}

			if (int4 % 30 == 0) {
				--int11;
			}

			int int12 = int6 * 10;
			for (int int13 = int10; int13 <= int11; ++int13) {
				for (int int14 = int8; int14 <= int9; ++int14) {
					byte byte1 = this.m_visited[int14 + int13 * int12];
					if (boolean1) {
						if ((byte1 & int5) != 0) {
							return true;
						}
					} else if ((byte1 & int5) != int5) {
						return false;
					}
				}
			}

			return !boolean1;
		} else {
			return false;
		}
	}

	boolean isCellVisible(int int1, int int2) {
		return this.hasFlags(int1 * 300, int2 * 300, (int1 + 1) * 300, (int2 + 1) * 300, 3, true);
	}

	public static WorldMapVisited getInstance() {
		IsoMetaGrid metaGrid = IsoWorld.instance.getMetaGrid();
		if (metaGrid == null) {
			throw new NullPointerException("IsoWorld.instance.MetaGrid is null");
		} else {
			if (instance == null) {
				instance = new WorldMapVisited();
				instance.setBounds(metaGrid.getMinX(), metaGrid.getMinY(), metaGrid.getMaxX(), metaGrid.getMaxY());
				try {
					instance.load();
					if (SandboxOptions.getInstance().Map.MapAllKnown.getValue()) {
						instance.setKnownInCells(metaGrid.getMinX(), metaGrid.getMinY(), metaGrid.getMaxX(), metaGrid.getMaxY());
					}
				} catch (Throwable throwable) {
					ExceptionLogger.logException(throwable);
				}
			}

			return instance;
		}
	}

	public static void update() {
		if (IsoWorld.instance != null) {
			WorldMapVisited worldMapVisited = getInstance();
			if (worldMapVisited != null) {
				for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					IsoPlayer player = IsoPlayer.players[int1];
					if (player != null && !player.isDead()) {
						byte byte1 = 25;
						int int2 = ((int)player.x - byte1) / 30;
						int int3 = ((int)player.y - byte1) / 30;
						int int4 = ((int)player.x + byte1) / 30;
						int int5 = ((int)player.y + byte1) / 30;
						if (((int)player.x + byte1) % 30 == 0) {
							--int4;
						}

						if (((int)player.y + byte1) % 30 == 0) {
							--int5;
						}

						if (int2 != worldMapVisited.m_updateMinX[int1] || int3 != worldMapVisited.m_updateMinY[int1] || int4 != worldMapVisited.m_updateMaxX[int1] || int5 != worldMapVisited.m_updateMaxY[int1]) {
							worldMapVisited.m_updateMinX[int1] = int2;
							worldMapVisited.m_updateMinY[int1] = int3;
							worldMapVisited.m_updateMaxX[int1] = int4;
							worldMapVisited.m_updateMaxY[int1] = int5;
							worldMapVisited.setFlags((int)player.x - byte1, (int)player.y - byte1, (int)player.x + byte1, (int)player.y + byte1, 3);
						}
					}
				}
			}
		}
	}

	public void forget() {
		this.clearKnownInCells(this.m_minX, this.m_minY, this.m_maxX, this.m_maxY);
		this.clearVisitedInCells(this.m_minX, this.m_minY, this.m_maxX, this.m_maxY);
		Arrays.fill(this.m_updateMinX, -1);
		Arrays.fill(this.m_updateMinY, -1);
		Arrays.fill(this.m_updateMaxX, -1);
		Arrays.fill(this.m_updateMaxY, -1);
	}

	public static void SaveAll() {
		WorldMapVisited worldMapVisited = instance;
		if (worldMapVisited != null) {
			try {
				worldMapVisited.save();
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		}
	}

	public static void Reset() {
		WorldMapVisited worldMapVisited = instance;
		if (worldMapVisited != null) {
			worldMapVisited.destroy();
			instance = null;
		}
	}
}
