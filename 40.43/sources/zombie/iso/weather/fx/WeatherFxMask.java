package zombie.iso.weather.fx;

import java.util.ArrayList;
import java.util.Iterator;
import org.joml.Vector2i;
import org.joml.Vector3f;
import zombie.IndieGL;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderSettings;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureFBO;
import zombie.core.textures.TextureID;
import zombie.debug.DebugLog;
import zombie.input.GameKeyboard;
import zombie.iso.DiamondMatrixIterator;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.areas.isoregion.IsoRegion;
import zombie.iso.areas.isoregion.MasterRegion;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameServer;


public class WeatherFxMask {
	private static boolean DEBUG_KEYS = false;
	private static TextureFBO fboMask;
	private static TextureFBO fboParticles;
	public static IsoSprite floorSprite;
	public static IsoSprite wallNSprite;
	public static IsoSprite wallWSprite;
	public static IsoSprite wallNWSprite;
	public static IsoSprite wallSESprite;
	private static IsoSprite lightCone;
	private static Texture texWhite;
	public static final float CLOUD_MASK_MIN_VAL = 0.5F;
	private static final ArrayList mrTemp = new ArrayList();
	public static boolean hasMaskToDraw = true;
	private static int curPlayerIndex;
	private static IsoPlayer curPlayer;
	private static int curPlayerZ;
	private static MasterRegion curMasterRegion;
	private static ArrayList curConnectedRegions = new ArrayList();
	public static final int BIT_FLOOR = 0;
	public static final int BIT_WALLN = 1;
	public static final int BIT_WALLW = 2;
	public static final int BIT_IS_CUT = 4;
	public static final int BIT_CHARS = 8;
	public static final int BIT_OBJECTS = 16;
	public static final int BIT_WALL_SE = 32;
	private static float offsetX;
	private static float offsetY;
	private static ColorInfo defColorInfo;
	public static boolean DIAMOND_ITER_DONE;
	private static int DIAMOND_ROWS;
	private static int DISABLED_MASKS;
	public int x;
	public int y;
	public int z;
	public int flags;
	public IsoGridSquare gs;
	public boolean enabled;
	public static WeatherFxMask[] masks;
	private static int maskPointer;
	private static DiamondMatrixIterator dmiter;
	private static final Vector2i diamondMatrixPos;
	private static Vector3f tmpVec;
	private static IsoGameCharacter.TorchInfo tmpTorch;
	private static ColorInfo tmpColInfo;
	private static int[] test;
	private static String[] testNames;
	private static int var1;
	private static int var2;
	private static float var3;
	private static int SCR_MASK_ADD;
	private static int DST_MASK_ADD;
	private static int SCR_MASK_SUB;
	private static int DST_MASK_SUB;
	private static int SCR_PARTICLES;
	private static int DST_PARTICLES;
	private static int SCR_MERGE;
	private static int DST_MERGE;
	private static int SCR_FINAL;
	private static int DST_FINAL;
	private static int ID_SCR_MASK_ADD;
	private static int ID_DST_MASK_ADD;
	private static int ID_SCR_MASK_SUB;
	private static int ID_DST_MASK_SUB;
	private static int ID_SCR_MERGE;
	private static int ID_DST_MERGE;
	private static int ID_SCR_FINAL;
	private static int ID_DST_FINAL;
	private static int ID_SCR_PARTICLES;
	private static int ID_DST_PARTICLES;
	private static int TARGET_BLEND;
	private static boolean DEBUG_MASK;
	public static boolean MASKING_ENABLED;
	private static boolean DEBUG_MASK_AND_PARTICLES;
	private static final boolean DEBUG_THROTTLE_KEYS = false;
	private static int keypause;

	public static TextureFBO getFboMask() {
		return fboMask;
	}

	public static TextureFBO getFboParticles() {
		return fboParticles;
	}

	public static void init() throws Exception {
		if (!GameServer.bServer) {
			for (int int1 = 0; int1 < masks.length; ++int1) {
				if (masks[int1] == null) {
					masks[int1] = new WeatherFxMask();
				}
			}

			initGlIds();
			floorSprite = IsoWorld.instance.spriteManager.getSprite("floors_interior_tilesandwood_01_16");
			wallNSprite = IsoWorld.instance.spriteManager.getSprite("walls_interior_house_01_21");
			wallWSprite = IsoWorld.instance.spriteManager.getSprite("walls_interior_house_01_20");
			wallNWSprite = IsoWorld.instance.spriteManager.getSprite("walls_interior_house_01_22");
			wallSESprite = IsoWorld.instance.spriteManager.getSprite("walls_interior_house_01_23");
			lightCone = IsoWorld.instance.spriteManager.getSprite("media/textures/weather/light_cone_cut.png");
			texWhite = Texture.getSharedTexture("media/textures/weather/fogwhite.png");
			DEBUG_KEYS = Core.bDebug;
		}
	}

	public static boolean checkFbos() {
		if (GameServer.bServer) {
			return false;
		} else {
			TextureFBO textureFBO = Core.getInstance().getOffscreenBuffer();
			if (Core.getInstance().getOffscreenBuffer() == null) {
				DebugLog.log("fbo=" + (textureFBO != null));
				return false;
			} else if (fboMask != null && fboParticles != null && fboMask.getTexture().getWidth() == textureFBO.getTexture().getWidth() && fboMask.getTexture().getHeight() == textureFBO.getTexture().getHeight()) {
				return fboMask != null && fboParticles != null;
			} else {
				if (fboMask != null) {
					fboMask.destroy();
				}

				if (fboParticles != null) {
					fboParticles.destroy();
				}

				fboMask = null;
				fboParticles = null;
				Texture var1x;
				try {
					TextureID.bUseCompression = false;
					var1x = new Texture(textureFBO.getTexture().getWidth(), textureFBO.getTexture().getHeight());
					fboMask = new TextureFBO(var1x);
				} catch (Exception exception) {
					DebugLog.log((Object)exception.getStackTrace());
					exception.printStackTrace();
				} finally {
					TextureID.bUseCompression = TextureID.bUseCompressionOption;
				}

				try {
					TextureID.bUseCompression = false;
					var1x = new Texture(textureFBO.getTexture().getWidth(), textureFBO.getTexture().getHeight());
					fboParticles = new TextureFBO(var1x);
				} catch (Exception exception2) {
					DebugLog.log((Object)exception2.getStackTrace());
					exception2.printStackTrace();
				} finally {
					TextureID.bUseCompression = TextureID.bUseCompressionOption;
				}

				return fboMask != null && fboParticles != null;
			}
		}
	}

	public static void destroy() {
		if (fboMask != null) {
			fboMask.destroy();
		}

		fboMask = null;
		if (fboParticles != null) {
			fboParticles.destroy();
		}

		fboParticles = null;
	}

	public static void initMask() {
		if (!GameServer.bServer) {
			maskPointer = 0;
			DISABLED_MASKS = 0;
			curPlayerIndex = IsoCamera.frameState.playerIndex;
			curPlayer = IsoPlayer.players[curPlayerIndex];
			curPlayerZ = (int)curPlayer.getZ();
			DIAMOND_ITER_DONE = false;
			if (curPlayer != null) {
				curMasterRegion = curPlayer.getMasterRegion();
				curConnectedRegions.clear();
				if (curMasterRegion != null && curPlayer.getMasterRegion().isFogMask()) {
					mrTemp.clear();
					mrTemp.add(curMasterRegion);
					label80: while (true) {
						MasterRegion masterRegion;
						do {
							if (mrTemp.size() <= 0) {
								break label80;
							}

							masterRegion = (MasterRegion)mrTemp.remove(0);
							curConnectedRegions.add(masterRegion);
						}				 while (masterRegion.getNeighbors().size() == 0);

						Iterator var1x = masterRegion.getNeighbors().iterator();
						while (var1x.hasNext()) {
							MasterRegion var2x = (MasterRegion)var1x.next();
							if (!mrTemp.contains(var2x) && !curConnectedRegions.contains(var2x) && var2x.isFogMask()) {
								mrTemp.add(var2x);
							}
						}
					}
				} else {
					curMasterRegion = null;
				}
			}

			if (IsoWeatherFX.instance == null) {
				hasMaskToDraw = false;
			} else {
				hasMaskToDraw = IsoWeatherFX.instance.hasCloudsToRender() && IsoWeatherFX.instance.getCloudIntensity() >= 0.5F || IsoWeatherFX.instance.hasPrecipitationToRender() || IsoWeatherFX.instance.hasFogToRender();
				hasMaskToDraw = true;
				if (hasMaskToDraw) {
					if ((curPlayer.getSquare() == null || curPlayer.getSquare().getBuilding() == null && curPlayer.getSquare().Is(IsoFlagType.exterior)) && (curMasterRegion == null || !curMasterRegion.isFogMask())) {
						hasMaskToDraw = false;
					} else {
						hasMaskToDraw = true;
					}
				}
			}
		}
	}

	private static boolean isOnScreen(int int1, int var1x, int var2x) {
		float var3x = (float)((int)IsoUtils.XToScreenInt(int1, var1x, var2x, 0));
		float float1 = (float)((int)IsoUtils.YToScreenInt(int1, var1x, var2x, 0));
		var3x -= (float)((int)IsoCamera.frameState.OffX);
		float1 -= (float)((int)IsoCamera.frameState.OffY);
		if (var3x + (float)(32 * Core.TileScale) <= 0.0F) {
			return false;
		} else if (float1 + (float)(32 * Core.TileScale) <= 0.0F) {
			return false;
		} else if (var3x - (float)(32 * Core.TileScale) >= (float)IsoCamera.frameState.OffscreenWidth) {
			return false;
		} else {
			return !(float1 - (float)(96 * Core.TileScale) >= (float)IsoCamera.frameState.OffscreenHeight);
		}
	}

	public boolean isLoc(int var1x, int var2x, int var3x) {
		return this.x == var1x && this.y == var2x && this.z == var3x;
	}

	public static void addMaskLocation(IsoGridSquare square, int var1x, int var2x, int var3x) {
		if (!GameServer.bServer && hasMaskToDraw && curPlayerZ == var3x) {
			IsoGridSquare square2;
			boolean boolean1;
			boolean boolean2;
			if (isInPlayerBuilding(square, var1x, var2x, var3x)) {
				square2 = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(var1x, var2x - 1, var3x);
				boolean1 = !isInPlayerBuilding(square2, var1x, var2x - 1, var3x);
				square2 = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(var1x - 1, var2x, var3x);
				boolean2 = !isInPlayerBuilding(square2, var1x - 1, var2x, var3x);
				square2 = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(var1x - 1, var2x - 1, var3x);
				boolean boolean3 = !isInPlayerBuilding(square2, var1x - 1, var2x - 1, var3x);
				int int1 = 0;
				if (boolean1) {
					int1 |= 1;
				}

				if (boolean2) {
					int1 |= 2;
				}

				if (boolean3) {
					int1 |= 32;
				}

				boolean boolean4 = false;
				if (square != null && (boolean1 || boolean2 || boolean3)) {
					byte byte1 = 24;
					if (boolean1 && !square.getProperties().Is("WallN") && !square.Is("WallNW")) {
						addMask(var1x - 1, var2x, var3x, (IsoGridSquare)null, 8, false);
						addMask(var1x, var2x, var3x, square, byte1);
						addMask(var1x + 1, var2x, var3x, (IsoGridSquare)null, byte1, false);
						addMask(var1x + 2, var2x, var3x, (IsoGridSquare)null, 8, false);
						addMask(var1x, var2x + 1, var3x, (IsoGridSquare)null, 8, false);
						addMask(var1x + 1, var2x + 1, var3x, (IsoGridSquare)null, byte1, false);
						addMask(var1x + 2, var2x + 1, var3x, (IsoGridSquare)null, byte1, false);
						addMask(var1x + 2, var2x + 2, var3x, (IsoGridSquare)null, 16, false);
						addMask(var1x + 3, var2x + 2, var3x, (IsoGridSquare)null, 16, false);
						boolean4 = true;
					}

					if (boolean2 && !square.getProperties().Is("WallW") && !square.getProperties().Is("WallNW")) {
						addMask(var1x, var2x - 1, var3x, (IsoGridSquare)null, 8, false);
						addMask(var1x, var2x, var3x, square, byte1);
						addMask(var1x, var2x + 1, var3x, (IsoGridSquare)null, byte1, false);
						addMask(var1x, var2x + 2, var3x, (IsoGridSquare)null, 8, false);
						addMask(var1x + 1, var2x, var3x, (IsoGridSquare)null, 8, false);
						addMask(var1x + 1, var2x + 1, var3x, (IsoGridSquare)null, byte1, false);
						addMask(var1x + 1, var2x + 2, var3x, (IsoGridSquare)null, byte1, false);
						addMask(var1x + 2, var2x + 2, var3x, (IsoGridSquare)null, 16, false);
						addMask(var1x + 2, var2x + 3, var3x, (IsoGridSquare)null, 16, false);
						boolean4 = true;
					}

					if (boolean3) {
						addMask(var1x, var2x, var3x, square, int1);
						boolean4 = true;
					}
				}

				if (!boolean4) {
					addMask(var1x, var2x, var3x, square, int1);
				}
			} else {
				square2 = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(var1x, var2x - 1, var3x);
				boolean1 = isInPlayerBuilding(square2, var1x, var2x - 1, var3x);
				square2 = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(var1x - 1, var2x, var3x);
				boolean2 = isInPlayerBuilding(square2, var1x - 1, var2x, var3x);
				if (!boolean1 && !boolean2) {
					square2 = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(var1x - 1, var2x - 1, var3x);
					if (isInPlayerBuilding(square2, var1x - 1, var2x - 1, var3x)) {
						addMask(var1x, var2x, var3x, square, 4);
					}
				} else {
					int int2 = 4;
					if (boolean1) {
						int2 |= 1;
					}

					if (boolean2) {
						int2 |= 2;
					}

					addMask(var1x, var2x, var3x, square, int2);
				}
			}
		}
	}

	private static boolean isInPlayerBuilding(IsoGridSquare square, int var1x, int var2x, int var3x) {
		if (square != null && square.Is(IsoFlagType.solidfloor)) {
			if (square.getBuilding() != null && square.getBuilding() == curPlayer.getBuilding()) {
				return true;
			}

			if (square.getBuilding() == null) {
				return curMasterRegion != null && square.getMasterRegion() != null && square.getMasterRegion().isFogMask() && (square.getMasterRegion() == curMasterRegion || curConnectedRegions.contains(square.getMasterRegion()));
			}
		} else {
			if (isInteriorLocation(var1x, var2x, var3x)) {
				return true;
			}

			if (square != null && square.getBuilding() == null) {
				return curMasterRegion != null && square.getMasterRegion() != null && square.getMasterRegion().isFogMask() && (square.getMasterRegion() == curMasterRegion || curConnectedRegions.contains(square.getMasterRegion()));
			}

			if (square == null && curMasterRegion != null) {
				MasterRegion masterRegion = IsoRegion.getMasterRegion(var1x, var2x, var3x);
				return masterRegion != null && masterRegion.isFogMask() && (masterRegion == curMasterRegion || curConnectedRegions.contains(masterRegion));
			}
		}

		return false;
	}

	private static boolean isInteriorLocation(int int1, int var1x, int var2x) {
		for (int int2 = var2x; int2 >= 0; --int2) {
			IsoGridSquare var3x = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(int1, var1x, int2);
			if (var3x != null) {
				if (var3x.getBuilding() != null && var3x.getBuilding() == curPlayer.getBuilding()) {
					return true;
				}

				if (var3x.Is(IsoFlagType.exterior)) {
					return false;
				}
			}
		}

		return false;
	}

	private static void addMask(int int1, int var1x, int var2x, IsoGridSquare var3x, int int2) {
		addMask(int1, var1x, var2x, var3x, int2, true);
	}

	private static void addMask(int int1, int var1x, int var2x, IsoGridSquare var3x, int int2, boolean boolean1) {
		if (hasMaskToDraw) {
			WeatherFxMask weatherFxMask = getMask(int1, var1x, var2x);
			WeatherFxMask weatherFxMask2;
			if (weatherFxMask == null) {
				weatherFxMask2 = getFreeMask();
				weatherFxMask2.x = int1;
				weatherFxMask2.y = var1x;
				weatherFxMask2.z = var2x;
				weatherFxMask2.flags = int2;
				weatherFxMask2.gs = var3x;
				weatherFxMask2.enabled = boolean1;
				if (!boolean1 && DISABLED_MASKS < DIAMOND_ROWS) {
					++DISABLED_MASKS;
				}
			} else {
				if (weatherFxMask.flags != int2) {
					weatherFxMask.flags |= int2;
				}

				if (!weatherFxMask.enabled && boolean1) {
					weatherFxMask2 = getFreeMask();
					weatherFxMask2.x = int1;
					weatherFxMask2.y = var1x;
					weatherFxMask2.z = var2x;
					weatherFxMask2.flags = weatherFxMask.flags;
					weatherFxMask2.gs = var3x;
					weatherFxMask2.enabled = boolean1;
				} else {
					weatherFxMask.enabled = weatherFxMask.enabled ? weatherFxMask.enabled : boolean1;
					if (boolean1 && var3x != null && weatherFxMask.gs == null) {
						weatherFxMask.gs = var3x;
					}
				}
			}
		}
	}

	private static WeatherFxMask getFreeMask() {
		if (maskPointer >= masks.length) {
			DebugLog.log("Weather Mask buffer out of bounds. Increasing cache.");
			WeatherFxMask[] weatherFxMaskArray = masks;
			masks = new WeatherFxMask[masks.length + 10000];
			for (int var1x = 0; var1x < masks.length; ++var1x) {
				if (weatherFxMaskArray[var1x] != null) {
					masks[var1x] = weatherFxMaskArray[var1x];
				} else {
					masks[var1x] = new WeatherFxMask();
				}
			}
		}

		return masks[maskPointer++];
	}

	private static boolean masksContains(int int1, int var1x, int var2x) {
		return getMask(int1, var1x, var2x) != null;
	}

	private static WeatherFxMask getMask(int int1, int var1x, int var2x) {
		if (maskPointer <= 0) {
			return null;
		} else {
			int var3x = maskPointer - 1 - (DIAMOND_ROWS + DISABLED_MASKS);
			if (var3x < 0) {
				var3x = 0;
			}

			for (int int2 = maskPointer - 1; int2 >= var3x; --int2) {
				if (masks[int2].isLoc(int1, var1x, var2x)) {
					return masks[int2];
				}
			}

			return null;
		}
	}

	private static void scanForTiles(int int1) {
		if (!DIAMOND_ITER_DONE) {
			IsoPlayer var1x = IsoPlayer.players[int1];
			int var2x = (int)var1x.getZ();
			byte var3x = 0;
			byte byte1 = 0;
			int int2 = var3x + IsoCamera.getOffscreenWidth(int1);
			int int3 = byte1 + IsoCamera.getOffscreenHeight(int1);
			float float1 = IsoUtils.XToIso((float)var3x, (float)byte1, 0.0F);
			float float2 = IsoUtils.YToIso((float)int2, (float)byte1, 0.0F);
			float float3 = IsoUtils.XToIso((float)int2, (float)int3, 6.0F);
			float float4 = IsoUtils.YToIso((float)var3x, (float)int3, 6.0F);
			float float5 = IsoUtils.XToIso((float)int2, (float)byte1, 0.0F);
			int int4 = (int)float2;
			int int5 = (int)float4;
			int int6 = (int)float1;
			int int7 = (int)float3;
			DIAMOND_ROWS = (int)float5 * 4;
			int6 -= 2;
			int4 -= 2;
			dmiter.reset(int7 - int6);
			Vector2i vector2i = diamondMatrixPos;
			while (dmiter.next(vector2i)) {
				if (vector2i != null) {
					IsoGridSquare square = IsoWorld.instance.getCell().getChunkMap(int1).getGridSquare(vector2i.x + int6, vector2i.y + int4, var2x);
					if (square == null) {
						addMaskLocation((IsoGridSquare)null, vector2i.x + int6, vector2i.y + int4, var2x);
					} else {
						IsoChunk chunk = square.getChunk();
						if (chunk != null && square.IsOnScreen()) {
							addMaskLocation(square, vector2i.x + int6, vector2i.y + int4, var2x);
						}
					}
				}
			}
		}
	}

	private static void renderMaskFloor(int int1, int var1x, int var2x) {
		floorSprite.render((IsoObject)null, (float)int1, (float)var1x, (float)var2x, IsoDirections.N, offsetX, offsetY, defColorInfo, false);
	}

	private static void renderMaskWall(int int1, int var1x, int var2x, boolean var3x, boolean boolean1) {
		if (var3x && boolean1) {
			wallNWSprite.render((IsoObject)null, (float)int1, (float)var1x, (float)var2x, IsoDirections.N, offsetX, offsetY, defColorInfo, false);
		} else if (var3x) {
			wallNSprite.render((IsoObject)null, (float)int1, (float)var1x, (float)var2x, IsoDirections.N, offsetX, offsetY, defColorInfo, false);
		} else if (boolean1) {
			wallWSprite.render((IsoObject)null, (float)int1, (float)var1x, (float)var2x, IsoDirections.N, offsetX, offsetY, defColorInfo, false);
		} else {
			wallSESprite.render((IsoObject)null, (float)int1, (float)var1x, (float)var2x, IsoDirections.N, offsetX, offsetY, defColorInfo, false);
		}
	}

	public static void renderFxMask(int int1) {
		if (!GameServer.bServer && IsoWeatherFX.instance != null) {
			if (LuaManager.thread == null || !LuaManager.thread.bStep) {
				if (DEBUG_KEYS && Core.bDebug) {
					updateDebugKeys();
				}

				if (MASKING_ENABLED && !checkFbos()) {
					MASKING_ENABLED = false;
				}

				if (MASKING_ENABLED && hasMaskToDraw) {
					scanForTiles(int1);
					SpriteRenderer.instance.glIgnoreStyles(true);
					if (MASKING_ENABLED) {
						SpriteRenderer.instance.glBuffer(4, int1);
						SpriteRenderer.instance.glDoStartFrameFx(Core.getInstance().getOffscreenWidth(int1), Core.getInstance().getOffscreenHeight(int1), int1);
						if (PerformanceSettings.LightingFrameSkip < 3) {
							IsoWorld.instance.getCell().DrawStencilMask();
							SpriteRenderer.instance.glClearColor(0, 0, 0, 0);
							SpriteRenderer.instance.glClear(16640);
							SpriteRenderer.instance.glClearColor(0, 0, 0, 255);
						}

						boolean var1x = true;
						boolean var2x = false;
						for (int int2 = 0; int2 < maskPointer; ++int2) {
							WeatherFxMask weatherFxMask = masks[int2];
							if (weatherFxMask.enabled) {
								boolean boolean1;
								boolean boolean2;
								if ((weatherFxMask.flags & 4) == 4) {
									SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
									SpriteRenderer.instance.glBlendFunc(SCR_MASK_SUB, DST_MASK_SUB);
									SpriteRenderer.instance.glBlendEquation(32779);
									IndieGL.enableStencilTest();
									IndieGL.enableAlphaTest();
									IndieGL.glAlphaFunc(516, 0.02F);
									IndieGL.glStencilFunc(517, 128, 128);
									SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
									boolean1 = (weatherFxMask.flags & 1) == 1;
									boolean2 = (weatherFxMask.flags & 2) == 2;
									renderMaskWall(weatherFxMask.x, weatherFxMask.y, weatherFxMask.z, boolean1, boolean2);
									SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
									IndieGL.glStencilFunc(519, 255, 255);
									SpriteRenderer.instance.glBlendEquation(32774);
									SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
								} else {
									SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
									SpriteRenderer.instance.glBlendFunc(SCR_MASK_ADD, DST_MASK_ADD);
									SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
									renderMaskFloor(weatherFxMask.x, weatherFxMask.y, weatherFxMask.z);
									var2x = (weatherFxMask.flags & 16) == 16;
									boolean var3x = (weatherFxMask.flags & 8) == 8;
									if (!var2x) {
										boolean1 = (weatherFxMask.flags & 1) == 1;
										boolean2 = (weatherFxMask.flags & 2) == 2;
										if (!boolean1 && !boolean2) {
											if ((weatherFxMask.flags & 32) == 32) {
												renderMaskWall(weatherFxMask.x, weatherFxMask.y, weatherFxMask.z, false, false);
											}
										} else {
											renderMaskWall(weatherFxMask.x, weatherFxMask.y, weatherFxMask.z, boolean1, boolean2);
										}
									}

									if (var2x && weatherFxMask.gs != null) {
										weatherFxMask.gs.RenderMinusFloorFxMask(0, 0, weatherFxMask.z + 1, 0, false, false, var1x);
									}

									if (var3x && weatherFxMask.gs != null) {
										weatherFxMask.gs.RenderCharacters(weatherFxMask.z + 1, false);
										SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
										SpriteRenderer.instance.glBlendFunc(SCR_MASK_ADD, DST_MASK_ADD);
										SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
									}
								}
							}
						}

						SpriteRenderer.instance.glBlendFunc(770, 771);
						SpriteRenderer.instance.glBuffer(5, int1);
						SpriteRenderer.instance.glDoEndFrameFx(int1);
					}

					if (DEBUG_MASK_AND_PARTICLES) {
						SpriteRenderer.instance.glClearColor(0, 0, 0, 255);
						SpriteRenderer.instance.glClear(16640);
						SpriteRenderer.instance.glClearColor(0, 0, 0, 255);
					} else if (DEBUG_MASK) {
						SpriteRenderer.instance.glClearColor(0, 255, 0, 255);
						SpriteRenderer.instance.glClear(16640);
						SpriteRenderer.instance.glClearColor(0, 0, 0, 255);
					}

					if (!RenderSettings.getInstance().getPlayerSettings(int1).isExterior()) {
						drawFxLayered(int1, false, false, false);
					}

					if (IsoWeatherFX.instance.hasCloudsToRender()) {
						drawFxLayered(int1, true, false, false);
					}

					if (IsoWeatherFX.instance.hasFogToRender()) {
						drawFxLayered(int1, false, true, false);
					}

					if (IsoWeatherFX.instance.hasPrecipitationToRender()) {
						drawFxLayered(int1, false, false, true);
					}

					SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
					SpriteRenderer.instance.glIgnoreStyles(false);
				} else {
					if (IsoWorld.instance.getCell() != null && IsoWorld.instance.getCell().getWeatherFX() != null) {
						SpriteRenderer.instance.glIgnoreStyles(true);
						SpriteRenderer.instance.glBlendFunc(770, 771);
						IsoWorld.instance.getCell().getWeatherFX().render();
						SpriteRenderer.instance.glIgnoreStyles(false);
					}
				}
			}
		}
	}

	private static void drawFxLayered(int int1, boolean var1x, boolean var2x, boolean var3x) {
		int int2 = IsoCamera.getOffscreenLeft(int1);
		int int3 = IsoCamera.getOffscreenTop(int1);
		int int4 = IsoCamera.getOffscreenWidth(int1);
		int int5 = IsoCamera.getOffscreenHeight(int1);
		SpriteRenderer.instance.glBuffer(6, int1);
		SpriteRenderer.instance.glDoStartFrameFx(Core.getInstance().getOffscreenWidth(int1), Core.getInstance().getOffscreenHeight(int1), int1);
		if (!var1x && !var2x && !var3x) {
			int int6 = Core.getInstance().getOffscreenWidth(int1);
			int int7 = Core.getInstance().getOffscreenHeight(int1);
			Color color = RenderSettings.getInstance().getMaskClearColorForPlayer(int1);
			SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
			SpriteRenderer.instance.glBlendFuncSeparate(SCR_PARTICLES, DST_PARTICLES, 1, 771);
			SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
			SpriteRenderer.instance.render(texWhite, 0, 0, int6, int7, color.r, color.g, color.b, color.a);
			SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
		} else if (IsoWorld.instance.getCell() != null && IsoWorld.instance.getCell().getWeatherFX() != null) {
			SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
			SpriteRenderer.instance.glBlendFuncSeparate(SCR_PARTICLES, DST_PARTICLES, 1, 771);
			SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
			IsoWorld.instance.getCell().getWeatherFX().renderLayered(var1x, var2x, var3x);
			SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
		}

		if (MASKING_ENABLED) {
			SpriteRenderer.instance.glBlendFunc(SCR_MERGE, DST_MERGE);
			SpriteRenderer.instance.glBlendEquation(32779);
			((Texture)fboMask.getTexture()).rendershader2(int2, int3, int4, int5, int2, int3, int4, int5, 1.0F, 1.0F, 1.0F, 1.0F);
			SpriteRenderer.instance.glBlendEquation(32774);
		}

		SpriteRenderer.instance.glBlendFunc(770, 771);
		SpriteRenderer.instance.glBuffer(7, int1);
		SpriteRenderer.instance.glDoEndFrameFx(int1);
		if ((DEBUG_MASK || DEBUG_MASK_AND_PARTICLES) && !DEBUG_MASK_AND_PARTICLES) {
			SpriteRenderer.instance.glBlendFunc(770, 771);
			((Texture)fboMask.getTexture()).render(0, Core.getInstance().getOffscreenHeight(int1), Core.getInstance().getOffscreenWidth(int1), -Core.getInstance().getOffscreenHeight(int1), 1.0F, 1.0F, 1.0F, 1.0F);
		} else {
			SpriteRenderer.instance.glBlendFunc(SCR_FINAL, DST_FINAL);
			((Texture)fboParticles.getTexture()).render(0, Core.getInstance().getOffscreenHeight(int1), Core.getInstance().getOffscreenWidth(int1), -Core.getInstance().getOffscreenHeight(int1), 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	private static void initGlIds() {
		for (int int1 = 0; int1 < test.length; ++int1) {
			if (test[int1] == SCR_MASK_ADD) {
				ID_SCR_MASK_ADD = int1;
			} else if (test[int1] == DST_MASK_ADD) {
				ID_DST_MASK_ADD = int1;
			} else if (test[int1] == SCR_MASK_SUB) {
				ID_SCR_MASK_SUB = int1;
			} else if (test[int1] == DST_MASK_SUB) {
				ID_DST_MASK_SUB = int1;
			} else if (test[int1] == SCR_PARTICLES) {
				ID_SCR_PARTICLES = int1;
			} else if (test[int1] == DST_PARTICLES) {
				ID_DST_PARTICLES = int1;
			} else if (test[int1] == SCR_MERGE) {
				ID_SCR_MERGE = int1;
			} else if (test[int1] == DST_MERGE) {
				ID_DST_MERGE = int1;
			} else if (test[int1] == SCR_FINAL) {
				ID_SCR_FINAL = int1;
			} else if (test[int1] == DST_FINAL) {
				ID_DST_FINAL = int1;
			}
		}
	}

	private static void updateDebugKeys() {
		if (keypause > 0) {
			--keypause;
		}

		if (keypause == 0) {
			boolean boolean1 = false;
			boolean var1x = false;
			boolean var2x = false;
			boolean var3x = false;
			boolean boolean2 = false;
			if (TARGET_BLEND == 0) {
				var1 = ID_SCR_MASK_ADD;
				var2 = ID_DST_MASK_ADD;
			} else if (TARGET_BLEND == 1) {
				var1 = ID_SCR_MASK_SUB;
				var2 = ID_DST_MASK_SUB;
			} else if (TARGET_BLEND == 2) {
				var1 = ID_SCR_MERGE;
				var2 = ID_DST_MERGE;
			} else if (TARGET_BLEND == 3) {
				var1 = ID_SCR_FINAL;
				var2 = ID_DST_FINAL;
			} else if (TARGET_BLEND == 4) {
				var1 = ID_SCR_PARTICLES;
				var2 = ID_DST_PARTICLES;
			}

			if (MASKING_ENABLED && GameKeyboard.isKeyDown(82)) {
				DEBUG_MASK = !DEBUG_MASK;
				boolean1 = true;
				var2x = true;
			} else if (MASKING_ENABLED && GameKeyboard.isKeyDown(80)) {
				DEBUG_MASK_AND_PARTICLES = !DEBUG_MASK_AND_PARTICLES;
				boolean1 = true;
				var3x = true;
			} else if (!GameKeyboard.isKeyDown(72) && GameKeyboard.isKeyDown(76)) {
				MASKING_ENABLED = !MASKING_ENABLED;
				boolean1 = true;
				boolean2 = true;
			}

			if (boolean1) {
				if (var1x) {
					if (TARGET_BLEND == 0) {
						DebugLog.log("TargetBlend = MASK_ADD");
					} else if (TARGET_BLEND == 1) {
						DebugLog.log("TargetBlend = MASK_SUB");
					} else if (TARGET_BLEND == 2) {
						DebugLog.log("TargetBlend = MERGE");
					} else if (TARGET_BLEND == 3) {
						DebugLog.log("TargetBlend = FINAL");
					} else if (TARGET_BLEND == 4) {
						DebugLog.log("TargetBlend = PARTICLES");
					}
				} else if (var2x) {
					DebugLog.log("DEBUG_MASK = " + DEBUG_MASK);
				} else if (var3x) {
					DebugLog.log("DEBUG_MASK_AND_PARTICLES = " + DEBUG_MASK_AND_PARTICLES);
				} else if (boolean2) {
					DebugLog.log("MASKING_ENABLED = " + MASKING_ENABLED);
				} else {
					if (TARGET_BLEND == 0) {
						ID_SCR_MASK_ADD = var1;
						ID_DST_MASK_ADD = var2;
						SCR_MASK_ADD = test[ID_SCR_MASK_ADD];
						DST_MASK_ADD = test[ID_DST_MASK_ADD];
					} else if (TARGET_BLEND == 1) {
						ID_SCR_MASK_SUB = var1;
						ID_DST_MASK_SUB = var2;
						SCR_MASK_SUB = test[ID_SCR_MASK_SUB];
						DST_MASK_SUB = test[ID_DST_MASK_SUB];
					} else if (TARGET_BLEND == 2) {
						ID_SCR_MERGE = var1;
						ID_DST_MERGE = var2;
						SCR_MERGE = test[ID_SCR_MERGE];
						DST_MERGE = test[ID_DST_MERGE];
					} else if (TARGET_BLEND == 3) {
						ID_SCR_FINAL = var1;
						ID_DST_FINAL = var2;
						SCR_FINAL = test[ID_SCR_FINAL];
						DST_FINAL = test[ID_DST_FINAL];
					} else if (TARGET_BLEND == 4) {
						ID_SCR_PARTICLES = var1;
						ID_DST_PARTICLES = var2;
						SCR_PARTICLES = test[ID_SCR_PARTICLES];
						DST_PARTICLES = test[ID_DST_PARTICLES];
					}

					DebugLog.log("Blendmode = " + testNames[var1] + " -> " + testNames[var2]);
				}

				keypause = 10;
			}
		}
	}

	static  {
		offsetX = (float)(32 * Core.TileScale);
		offsetY = (float)(96 * Core.TileScale);
		defColorInfo = new ColorInfo();
		DIAMOND_ITER_DONE = false;
		DIAMOND_ROWS = 1000;
		DISABLED_MASKS = 0;
		masks = new WeatherFxMask[30000];
		maskPointer = 0;
		dmiter = new DiamondMatrixIterator(0);
		diamondMatrixPos = new Vector2i();
		tmpVec = new Vector3f();
		tmpTorch = new IsoGameCharacter.TorchInfo();
		tmpColInfo = new ColorInfo();
		test = new int[]{0, 1, 768, 769, 774, 775, 770, 771, 772, 773, 32769, 32770, 32771, 32772, 776, 35065, 35066, 34185, 35067};
		testNames = new String[]{"GL_ZERO", "GL_ONE", "GL_SRC_COLOR", "GL_ONE_MINUS_SRC_COLOR", "GL_DST_COLOR", "GL_ONE_MINUS_DST_COLOR", "GL_SRC_ALPHA", "GL_ONE_MINUS_SRC_ALPHA", "GL_DST_ALPHA", "GL_ONE_MINUS_DST_ALPHA", "GL_CONSTANT_COLOR", "GL_ONE_MINUS_CONSTANT_COLOR", "GL_CONSTANT_ALPHA", "GL_ONE_MINUS_CONSTANT_ALPHA", "GL_SRC_ALPHA_SATURATE", "GL_SRC1_COLOR (33)", "GL_ONE_MINUS_SRC1_COLOR (33)", "GL_SRC1_ALPHA (15)", "GL_ONE_MINUS_SRC1_ALPHA (33)"};
		var1 = 1;
		var2 = 1;
		var3 = 1.0F;
		SCR_MASK_ADD = 770;
		DST_MASK_ADD = 771;
		SCR_MASK_SUB = 0;
		DST_MASK_SUB = 0;
		SCR_PARTICLES = 1;
		DST_PARTICLES = 771;
		SCR_MERGE = 770;
		DST_MERGE = 771;
		SCR_FINAL = 770;
		DST_FINAL = 771;
		TARGET_BLEND = 0;
		DEBUG_MASK = false;
		MASKING_ENABLED = true;
		DEBUG_MASK_AND_PARTICLES = false;
		keypause = 0;
	}
}
