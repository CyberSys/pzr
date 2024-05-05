package zombie.iso.weather.fx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
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
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.input.GameKeyboard;
import zombie.iso.DiamondMatrixIterator;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.areas.isoregion.regions.IWorldRegion;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
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
	private static Texture texWhite;
	private static int curPlayerIndex;
	public static final int BIT_FLOOR = 0;
	public static final int BIT_WALLN = 1;
	public static final int BIT_WALLW = 2;
	public static final int BIT_IS_CUT = 4;
	public static final int BIT_CHARS = 8;
	public static final int BIT_OBJECTS = 16;
	public static final int BIT_WALL_SE = 32;
	public static final int BIT_DOOR = 64;
	public static float offsetX;
	public static float offsetY;
	public static ColorInfo defColorInfo;
	private static int DIAMOND_ROWS;
	public int x;
	public int y;
	public int z;
	public int flags;
	public IsoGridSquare gs;
	public boolean enabled;
	private static WeatherFxMask.PlayerFxMask[] playerMasks;
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
	private static final boolean DEBUG_THROTTLE_KEYS = true;
	private static int keypause;

	public static TextureFBO getFboMask() {
		return fboMask;
	}

	public static TextureFBO getFboParticles() {
		return fboParticles;
	}

	public static void init() throws Exception {
		if (!GameServer.bServer) {
			for (int int1 = 0; int1 < playerMasks.length; ++int1) {
				playerMasks[int1] = new WeatherFxMask.PlayerFxMask();
			}

			playerMasks[0].init();
			initGlIds();
			floorSprite = IsoSpriteManager.instance.getSprite("floors_interior_tilesandwood_01_16");
			wallNSprite = IsoSpriteManager.instance.getSprite("walls_interior_house_01_21");
			wallWSprite = IsoSpriteManager.instance.getSprite("walls_interior_house_01_20");
			wallNWSprite = IsoSpriteManager.instance.getSprite("walls_interior_house_01_22");
			wallSESprite = IsoSpriteManager.instance.getSprite("walls_interior_house_01_23");
			texWhite = Texture.getSharedTexture("media/textures/weather/fogwhite.png");
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
			} else {
				int var1x = Core.getInstance().getScreenWidth();
				int var2x = Core.getInstance().getScreenHeight();
				if (fboMask != null && fboParticles != null && fboMask.getTexture().getWidth() == var1x && fboMask.getTexture().getHeight() == var2x) {
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
					Texture var3x;
					try {
						var3x = new Texture(var1x, var2x, 16);
						fboMask = new TextureFBO(var3x);
					} catch (Exception exception) {
						DebugLog.log((Object)exception.getStackTrace());
						exception.printStackTrace();
					}

					try {
						var3x = new Texture(var1x, var2x, 16);
						fboParticles = new TextureFBO(var3x);
					} catch (Exception exception2) {
						DebugLog.log((Object)exception2.getStackTrace());
						exception2.printStackTrace();
					}

					return fboMask != null && fboParticles != null;
				}
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
			curPlayerIndex = IsoCamera.frameState.playerIndex;
			playerMasks[curPlayerIndex].initMask();
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

	public static boolean playerHasMaskToDraw(int int1) {
		return int1 < playerMasks.length ? playerMasks[int1].hasMaskToDraw : false;
	}

	public static void setDiamondIterDone(int int1) {
		if (int1 < playerMasks.length) {
			playerMasks[int1].DIAMOND_ITER_DONE = true;
		}
	}

	public static void forceMaskUpdate(int int1) {
		if (int1 < playerMasks.length) {
			playerMasks[int1].plrSquare = null;
		}
	}

	public static void forceMaskUpdateAll() {
		if (!GameServer.bServer) {
			for (int int1 = 0; int1 < playerMasks.length; ++int1) {
				playerMasks[int1].plrSquare = null;
			}
		}
	}

	private static boolean getIsStairs(IsoGridSquare square) {
		return square != null && (square.Has(IsoObjectType.stairsBN) || square.Has(IsoObjectType.stairsBW) || square.Has(IsoObjectType.stairsMN) || square.Has(IsoObjectType.stairsMW) || square.Has(IsoObjectType.stairsTN) || square.Has(IsoObjectType.stairsTW));
	}

	private static boolean getHasDoor(IsoGridSquare square) {
		return square != null && (square.Is(IsoFlagType.cutN) || square.Is(IsoFlagType.cutW)) && (square.Is(IsoFlagType.DoorWallN) || square.Is(IsoFlagType.DoorWallW)) && !square.Is(IsoFlagType.doorN) && !square.Is(IsoFlagType.doorW) ? square.getCanSee(curPlayerIndex) : false;
	}

	public static void addMaskLocation(IsoGridSquare square, int var1x, int var2x, int var3x) {
		if (!GameServer.bServer) {
			WeatherFxMask.PlayerFxMask playerFxMask = playerMasks[curPlayerIndex];
			if (playerFxMask.requiresUpdate) {
				if (playerFxMask.hasMaskToDraw && playerFxMask.playerZ == var3x) {
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
						boolean boolean5 = getIsStairs(square);
						if (square != null && (boolean1 || boolean2 || boolean3)) {
							byte byte1 = 24;
							if (boolean1 && !square.getProperties().Is(IsoFlagType.WallN) && !square.Is(IsoFlagType.WallNW)) {
								playerFxMask.addMask(var1x - 1, var2x, var3x, (IsoGridSquare)null, 8, false);
								playerFxMask.addMask(var1x, var2x, var3x, square, byte1);
								playerFxMask.addMask(var1x + 1, var2x, var3x, (IsoGridSquare)null, byte1, false);
								playerFxMask.addMask(var1x + 2, var2x, var3x, (IsoGridSquare)null, 8, false);
								playerFxMask.addMask(var1x, var2x + 1, var3x, (IsoGridSquare)null, 8, false);
								playerFxMask.addMask(var1x + 1, var2x + 1, var3x, (IsoGridSquare)null, byte1, false);
								playerFxMask.addMask(var1x + 2, var2x + 1, var3x, (IsoGridSquare)null, byte1, false);
								playerFxMask.addMask(var1x + 2, var2x + 2, var3x, (IsoGridSquare)null, 16, false);
								playerFxMask.addMask(var1x + 3, var2x + 2, var3x, (IsoGridSquare)null, 16, false);
								boolean4 = true;
							}

							if (boolean2 && !square.getProperties().Is(IsoFlagType.WallW) && !square.getProperties().Is(IsoFlagType.WallNW)) {
								playerFxMask.addMask(var1x, var2x - 1, var3x, (IsoGridSquare)null, 8, false);
								playerFxMask.addMask(var1x, var2x, var3x, square, byte1);
								playerFxMask.addMask(var1x, var2x + 1, var3x, (IsoGridSquare)null, byte1, false);
								playerFxMask.addMask(var1x, var2x + 2, var3x, (IsoGridSquare)null, 8, false);
								playerFxMask.addMask(var1x + 1, var2x, var3x, (IsoGridSquare)null, 8, false);
								playerFxMask.addMask(var1x + 1, var2x + 1, var3x, (IsoGridSquare)null, byte1, false);
								playerFxMask.addMask(var1x + 1, var2x + 2, var3x, (IsoGridSquare)null, byte1, false);
								playerFxMask.addMask(var1x + 2, var2x + 2, var3x, (IsoGridSquare)null, 16, false);
								playerFxMask.addMask(var1x + 2, var2x + 3, var3x, (IsoGridSquare)null, 16, false);
								boolean4 = true;
							}

							if (boolean3) {
								int int2 = boolean5 ? byte1 : int1;
								playerFxMask.addMask(var1x, var2x, var3x, square, int2);
								boolean4 = true;
							}
						}

						if (!boolean4) {
							int int3 = boolean5 ? 24 : int1;
							playerFxMask.addMask(var1x, var2x, var3x, square, int3);
						}
					} else {
						square2 = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(var1x, var2x - 1, var3x);
						boolean1 = isInPlayerBuilding(square2, var1x, var2x - 1, var3x);
						square2 = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(var1x - 1, var2x, var3x);
						boolean2 = isInPlayerBuilding(square2, var1x - 1, var2x, var3x);
						if (!boolean1 && !boolean2) {
							square2 = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(var1x - 1, var2x - 1, var3x);
							if (isInPlayerBuilding(square2, var1x - 1, var2x - 1, var3x)) {
								playerFxMask.addMask(var1x, var2x, var3x, square, 4);
							}
						} else {
							int int4 = 4;
							if (boolean1) {
								int4 |= 1;
							}

							if (boolean2) {
								int4 |= 2;
							}

							if (getHasDoor(square)) {
								int4 |= 64;
							}

							playerFxMask.addMask(var1x, var2x, var3x, square, int4);
						}
					}
				}
			}
		}
	}

	private static boolean isInPlayerBuilding(IsoGridSquare square, int var1x, int var2x, int var3x) {
		WeatherFxMask.PlayerFxMask playerFxMask = playerMasks[curPlayerIndex];
		if (square != null && square.Is(IsoFlagType.solidfloor)) {
			if (square.getBuilding() != null && square.getBuilding() == playerFxMask.player.getBuilding()) {
				return true;
			}

			if (square.getBuilding() == null) {
				return playerFxMask.curIsoWorldRegion != null && square.getIsoWorldRegion() != null && square.getIsoWorldRegion().isFogMask() && (square.getIsoWorldRegion() == playerFxMask.curIsoWorldRegion || playerFxMask.curConnectedRegions.contains(square.getIsoWorldRegion()));
			}
		} else {
			if (isInteriorLocation(var1x, var2x, var3x)) {
				return true;
			}

			if (square != null && square.getBuilding() == null) {
				return playerFxMask.curIsoWorldRegion != null && square.getIsoWorldRegion() != null && square.getIsoWorldRegion().isFogMask() && (square.getIsoWorldRegion() == playerFxMask.curIsoWorldRegion || playerFxMask.curConnectedRegions.contains(square.getIsoWorldRegion()));
			}

			if (square == null && playerFxMask.curIsoWorldRegion != null) {
				IWorldRegion iWorldRegion = IsoRegions.getIsoWorldRegion(var1x, var2x, var3x);
				return iWorldRegion != null && iWorldRegion.isFogMask() && (iWorldRegion == playerFxMask.curIsoWorldRegion || playerFxMask.curConnectedRegions.contains(iWorldRegion));
			}
		}

		return false;
	}

	private static boolean isInteriorLocation(int int1, int var1x, int var2x) {
		WeatherFxMask.PlayerFxMask var3x = playerMasks[curPlayerIndex];
		for (int int2 = var2x; int2 >= 0; --int2) {
			IsoGridSquare square = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(int1, var1x, int2);
			if (square != null) {
				if (square.getBuilding() != null && square.getBuilding() == var3x.player.getBuilding()) {
					return true;
				}

				if (square.Is(IsoFlagType.exterior)) {
					return false;
				}
			}
		}

		return false;
	}

	private static void scanForTiles(int int1) {
		WeatherFxMask.PlayerFxMask var1x = playerMasks[curPlayerIndex];
		if (!var1x.DIAMOND_ITER_DONE) {
			IsoPlayer var2x = IsoPlayer.players[int1];
			int var3x = (int)var2x.getZ();
			byte byte1 = 0;
			byte byte2 = 0;
			int int2 = byte1 + IsoCamera.getOffscreenWidth(int1);
			int int3 = byte2 + IsoCamera.getOffscreenHeight(int1);
			float float1 = IsoUtils.XToIso((float)byte1, (float)byte2, 0.0F);
			float float2 = IsoUtils.YToIso((float)int2, (float)byte2, 0.0F);
			float float3 = IsoUtils.XToIso((float)int2, (float)int3, 6.0F);
			float float4 = IsoUtils.YToIso((float)byte1, (float)int3, 6.0F);
			float float5 = IsoUtils.XToIso((float)int2, (float)byte2, 0.0F);
			int int4 = (int)float2;
			int int5 = (int)float4;
			int int6 = (int)float1;
			int int7 = (int)float3;
			DIAMOND_ROWS = (int)float5 * 4;
			int6 -= 2;
			int4 -= 2;
			dmiter.reset(int7 - int6);
			Vector2i vector2i = diamondMatrixPos;
			IsoChunkMap chunkMap = IsoWorld.instance.getCell().getChunkMap(int1);
			while (dmiter.next(vector2i)) {
				if (vector2i != null) {
					IsoGridSquare square = chunkMap.getGridSquare(vector2i.x + int6, vector2i.y + int4, var3x);
					if (square == null) {
						addMaskLocation((IsoGridSquare)null, vector2i.x + int6, vector2i.y + int4, var3x);
					} else {
						IsoChunk chunk = square.getChunk();
						if (chunk != null && square.IsOnScreen()) {
							addMaskLocation(square, vector2i.x + int6, vector2i.y + int4, var3x);
						}
					}
				}
			}
		}
	}

	private static void renderMaskFloor(int int1, int var1x, int var2x) {
		floorSprite.render((IsoObject)null, (float)int1, (float)var1x, (float)var2x, IsoDirections.N, offsetX, offsetY, defColorInfo, false);
	}

	private static void renderMaskWall(IsoGridSquare square, int var1x, int var2x, int var3x, boolean boolean1, boolean boolean2, int int1) {
		if (square != null) {
			IsoGridSquare square2 = square.nav[IsoDirections.S.index()];
			IsoGridSquare square3 = square.nav[IsoDirections.E.index()];
			long long1 = System.currentTimeMillis();
			boolean boolean3 = square2 != null && square2.getPlayerCutawayFlag(int1, long1);
			boolean boolean4 = square.getPlayerCutawayFlag(int1, long1);
			boolean boolean5 = square3 != null && square3.getPlayerCutawayFlag(int1, long1);
			IsoSprite sprite;
			IsoDirections directions;
			if (boolean1 && boolean2) {
				sprite = wallNWSprite;
				directions = IsoDirections.NW;
			} else if (boolean1) {
				sprite = wallNSprite;
				directions = IsoDirections.N;
			} else if (boolean2) {
				sprite = wallWSprite;
				directions = IsoDirections.W;
			} else {
				sprite = wallSESprite;
				directions = IsoDirections.W;
			}

			square.DoCutawayShaderSprite(sprite, directions, boolean3, boolean4, boolean5);
		}
	}

	private static void renderMaskWallNoCuts(int int1, int var1x, int var2x, boolean var3x, boolean boolean1) {
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
		if (DebugOptions.instance.Weather.Fx.getValue()) {
			if (!GameServer.bServer) {
				if (IsoWeatherFX.instance != null) {
					if (LuaManager.thread == null || !LuaManager.thread.bStep) {
						if (DEBUG_KEYS && Core.bDebug) {
							updateDebugKeys();
						}

						if (playerMasks[int1].maskEnabled) {
							WeatherFxMask.PlayerFxMask var1x = playerMasks[curPlayerIndex];
							if (var1x.maskEnabled) {
								if (MASKING_ENABLED && !checkFbos()) {
									MASKING_ENABLED = false;
								}

								if (MASKING_ENABLED && var1x.hasMaskToDraw) {
									scanForTiles(int1);
									int var2x = IsoCamera.getOffscreenLeft(int1);
									int var3x = IsoCamera.getOffscreenTop(int1);
									int int2 = IsoCamera.getOffscreenWidth(int1);
									int int3 = IsoCamera.getOffscreenHeight(int1);
									int int4 = IsoCamera.getScreenWidth(int1);
									int int5 = IsoCamera.getScreenHeight(int1);
									SpriteRenderer.instance.glIgnoreStyles(true);
									if (MASKING_ENABLED) {
										SpriteRenderer.instance.glBuffer(4, int1);
										SpriteRenderer.instance.glDoStartFrameFx(int2, int3, int1);
										if (PerformanceSettings.LightingFrameSkip < 3) {
											IsoWorld.instance.getCell().DrawStencilMask();
											SpriteRenderer.instance.glClearColor(0, 0, 0, 0);
											SpriteRenderer.instance.glClear(16640);
											SpriteRenderer.instance.glClearColor(0, 0, 0, 255);
										}

										boolean boolean1 = true;
										boolean boolean2 = false;
										WeatherFxMask[] weatherFxMaskArray = playerMasks[int1].masks;
										int int6 = playerMasks[int1].maskPointer;
										for (int int7 = 0; int7 < int6; ++int7) {
											WeatherFxMask weatherFxMask = weatherFxMaskArray[int7];
											if (weatherFxMask.enabled) {
												boolean boolean3;
												boolean boolean4;
												if ((weatherFxMask.flags & 4) == 4) {
													SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
													SpriteRenderer.instance.glBlendFunc(SCR_MASK_SUB, DST_MASK_SUB);
													SpriteRenderer.instance.glBlendEquation(32779);
													IndieGL.enableAlphaTest();
													IndieGL.glAlphaFunc(516, 0.02F);
													SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
													boolean3 = (weatherFxMask.flags & 1) == 1;
													boolean4 = (weatherFxMask.flags & 2) == 2;
													renderMaskWall(weatherFxMask.gs, weatherFxMask.x, weatherFxMask.y, weatherFxMask.z, boolean3, boolean4, int1);
													SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
													SpriteRenderer.instance.glBlendEquation(32774);
													SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
													boolean boolean5 = (weatherFxMask.flags & 64) == 64;
													if (boolean5 && weatherFxMask.gs != null) {
														SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
														SpriteRenderer.instance.glBlendFunc(SCR_MASK_ADD, DST_MASK_ADD);
														SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
														weatherFxMask.gs.RenderOpenDoorOnly();
													}
												} else {
													SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
													SpriteRenderer.instance.glBlendFunc(SCR_MASK_ADD, DST_MASK_ADD);
													SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
													renderMaskFloor(weatherFxMask.x, weatherFxMask.y, weatherFxMask.z);
													boolean2 = (weatherFxMask.flags & 16) == 16;
													boolean boolean6 = (weatherFxMask.flags & 8) == 8;
													if (!boolean2) {
														boolean3 = (weatherFxMask.flags & 1) == 1;
														boolean4 = (weatherFxMask.flags & 2) == 2;
														if (!boolean3 && !boolean4) {
															if ((weatherFxMask.flags & 32) == 32) {
																renderMaskWall(weatherFxMask.gs, weatherFxMask.x, weatherFxMask.y, weatherFxMask.z, false, false, int1);
															}
														} else {
															renderMaskWall(weatherFxMask.gs, weatherFxMask.x, weatherFxMask.y, weatherFxMask.z, boolean3, boolean4, int1);
														}
													}

													if (boolean2 && weatherFxMask.gs != null) {
														weatherFxMask.gs.RenderMinusFloorFxMask(weatherFxMask.z + 1, false, false);
													}

													if (boolean6 && weatherFxMask.gs != null) {
														weatherFxMask.gs.renderCharacters(weatherFxMask.z + 1, false, false);
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

									if (IsoWeatherFX.instance.hasFogToRender() && PerformanceSettings.FogQuality == 2) {
										drawFxLayered(int1, false, true, false);
									}

									if (Core.OptionRenderPrecipitation == 1 && IsoWeatherFX.instance.hasPrecipitationToRender()) {
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
				}
			}
		}
	}

	private static void drawFxLayered(int int1, boolean var1x, boolean var2x, boolean var3x) {
		int int2 = IsoCamera.getOffscreenLeft(int1);
		int int3 = IsoCamera.getOffscreenTop(int1);
		int int4 = IsoCamera.getOffscreenWidth(int1);
		int int5 = IsoCamera.getOffscreenHeight(int1);
		int int6 = IsoCamera.getScreenLeft(int1);
		int int7 = IsoCamera.getScreenTop(int1);
		int int8 = IsoCamera.getScreenWidth(int1);
		int int9 = IsoCamera.getScreenHeight(int1);
		SpriteRenderer.instance.glBuffer(6, int1);
		SpriteRenderer.instance.glDoStartFrameFx(int4, int5, int1);
		if (!var1x && !var2x && !var3x) {
			Color color = RenderSettings.getInstance().getMaskClearColorForPlayer(int1);
			SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
			SpriteRenderer.instance.glBlendFuncSeparate(SCR_PARTICLES, DST_PARTICLES, 1, 771);
			SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
			SpriteRenderer.instance.renderi(texWhite, 0, 0, int4, int5, color.r, color.g, color.b, color.a, (Consumer)null);
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
			((Texture)fboMask.getTexture()).rendershader2(0.0F, 0.0F, (float)int4, (float)int5, int6, int7, int8, int9, 1.0F, 1.0F, 1.0F, 1.0F);
			SpriteRenderer.instance.glBlendEquation(32774);
		}

		SpriteRenderer.instance.glBlendFunc(770, 771);
		SpriteRenderer.instance.glBuffer(7, int1);
		SpriteRenderer.instance.glDoEndFrameFx(int1);
		Texture texture;
		if ((DEBUG_MASK || DEBUG_MASK_AND_PARTICLES) && !DEBUG_MASK_AND_PARTICLES) {
			texture = (Texture)fboMask.getTexture();
			SpriteRenderer.instance.glBlendFunc(770, 771);
		} else {
			texture = (Texture)fboParticles.getTexture();
			SpriteRenderer.instance.glBlendFunc(SCR_FINAL, DST_FINAL);
		}

		float float1 = 1.0F;
		float float2 = 1.0F;
		float float3 = 1.0F;
		float float4 = 1.0F;
		float float5 = (float)int6 / (float)texture.getWidthHW();
		float float6 = (float)int7 / (float)texture.getHeightHW();
		float float7 = (float)(int6 + int8) / (float)texture.getWidthHW();
		float float8 = (float)(int7 + int9) / (float)texture.getHeightHW();
		SpriteRenderer.instance.render(texture, 0.0F, 0.0F, (float)int4, (float)int5, float1, float2, float3, float4, float5, float8, float7, float8, float7, float6, float5, float6);
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

			if (GameKeyboard.isKeyDown(79)) {
				--var1;
				if (var1 < 0) {
					var1 = test.length - 1;
				}

				boolean1 = true;
			} else if (GameKeyboard.isKeyDown(81)) {
				++var1;
				if (var1 >= test.length) {
					var1 = 0;
				}

				boolean1 = true;
			} else if (GameKeyboard.isKeyDown(75)) {
				--var2;
				if (var2 < 0) {
					var2 = test.length - 1;
				}

				boolean1 = true;
			} else if (GameKeyboard.isKeyDown(77)) {
				++var2;
				if (var2 >= test.length) {
					var2 = 0;
				}

				boolean1 = true;
			} else if (GameKeyboard.isKeyDown(71)) {
				--TARGET_BLEND;
				if (TARGET_BLEND < 0) {
					TARGET_BLEND = 4;
				}

				boolean1 = true;
				var1x = true;
			} else if (GameKeyboard.isKeyDown(73)) {
				++TARGET_BLEND;
				if (TARGET_BLEND >= 5) {
					TARGET_BLEND = 0;
				}

				boolean1 = true;
				var1x = true;
			} else if (MASKING_ENABLED && GameKeyboard.isKeyDown(82)) {
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

					String string = testNames[var1];
					DebugLog.log("Blendmode = " + string + " -> " + testNames[var2]);
				}

				keypause = 30;
			}
		}
	}

	static  {
		offsetX = (float)(32 * Core.TileScale);
		offsetY = (float)(96 * Core.TileScale);
		defColorInfo = new ColorInfo();
		DIAMOND_ROWS = 1000;
		playerMasks = new WeatherFxMask.PlayerFxMask[4];
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

	public static class PlayerFxMask {
		private WeatherFxMask[] masks;
		private int maskPointer = 0;
		private boolean maskEnabled = false;
		private IsoGridSquare plrSquare;
		private int DISABLED_MASKS = 0;
		private boolean requiresUpdate = false;
		private boolean hasMaskToDraw = true;
		private int playerIndex;
		private IsoPlayer player;
		private int playerZ;
		private IWorldRegion curIsoWorldRegion;
		private ArrayList curConnectedRegions = new ArrayList();
		private final ArrayList isoWorldRegionTemp = new ArrayList();
		private boolean DIAMOND_ITER_DONE = false;
		private boolean isFirstSquare = true;
		private IsoGridSquare firstSquare;

		private void init() {
			this.masks = new WeatherFxMask[30000];
			for (int int1 = 0; int1 < this.masks.length; ++int1) {
				if (this.masks[int1] == null) {
					this.masks[int1] = new WeatherFxMask();
				}
			}

			this.maskEnabled = true;
		}

		private void initMask() {
			if (!GameServer.bServer) {
				if (!this.maskEnabled) {
					this.init();
				}

				this.playerIndex = IsoCamera.frameState.playerIndex;
				this.player = IsoPlayer.players[this.playerIndex];
				this.playerZ = (int)this.player.getZ();
				this.DIAMOND_ITER_DONE = false;
				this.requiresUpdate = false;
				if (this.player != null) {
					if (this.isFirstSquare || this.plrSquare == null || this.plrSquare != this.player.getSquare()) {
						this.plrSquare = this.player.getSquare();
						this.maskPointer = 0;
						this.DISABLED_MASKS = 0;
						this.requiresUpdate = true;
						if (this.firstSquare == null) {
							this.firstSquare = this.plrSquare;
						}

						if (this.firstSquare != null && this.firstSquare != this.plrSquare) {
							this.isFirstSquare = false;
						}
					}

					this.curIsoWorldRegion = this.player.getMasterRegion();
					this.curConnectedRegions.clear();
					if (this.curIsoWorldRegion != null && this.player.getMasterRegion().isFogMask()) {
						this.isoWorldRegionTemp.clear();
						this.isoWorldRegionTemp.add(this.curIsoWorldRegion);
						label79: while (true) {
							IWorldRegion iWorldRegion;
							do {
								if (this.isoWorldRegionTemp.size() <= 0) {
									break label79;
								}

								iWorldRegion = (IWorldRegion)this.isoWorldRegionTemp.remove(0);
								this.curConnectedRegions.add(iWorldRegion);
							}				 while (iWorldRegion.getNeighbors().size() == 0);

							Iterator iterator = iWorldRegion.getNeighbors().iterator();
							while (iterator.hasNext()) {
								IsoWorldRegion worldRegion = (IsoWorldRegion)iterator.next();
								if (!this.isoWorldRegionTemp.contains(worldRegion) && !this.curConnectedRegions.contains(worldRegion) && worldRegion.isFogMask()) {
									this.isoWorldRegionTemp.add(worldRegion);
								}
							}
						}
					} else {
						this.curIsoWorldRegion = null;
					}
				}

				if (IsoWeatherFX.instance == null) {
					this.hasMaskToDraw = false;
				} else {
					this.hasMaskToDraw = true;
					if (this.hasMaskToDraw) {
						if ((this.player.getSquare() == null || this.player.getSquare().getBuilding() == null && this.player.getSquare().Is(IsoFlagType.exterior)) && (this.curIsoWorldRegion == null || !this.curIsoWorldRegion.isFogMask())) {
							this.hasMaskToDraw = false;
						} else {
							this.hasMaskToDraw = true;
						}
					}
				}
			}
		}

		private void addMask(int int1, int int2, int int3, IsoGridSquare square, int int4) {
			this.addMask(int1, int2, int3, square, int4, true);
		}

		private void addMask(int int1, int int2, int int3, IsoGridSquare square, int int4, boolean boolean1) {
			if (this.hasMaskToDraw && this.requiresUpdate) {
				if (!this.maskEnabled) {
					this.init();
				}

				WeatherFxMask weatherFxMask = this.getMask(int1, int2, int3);
				WeatherFxMask weatherFxMask2;
				if (weatherFxMask == null) {
					weatherFxMask2 = this.getFreeMask();
					weatherFxMask2.x = int1;
					weatherFxMask2.y = int2;
					weatherFxMask2.z = int3;
					weatherFxMask2.flags = int4;
					weatherFxMask2.gs = square;
					weatherFxMask2.enabled = boolean1;
					if (!boolean1 && this.DISABLED_MASKS < WeatherFxMask.DIAMOND_ROWS) {
						++this.DISABLED_MASKS;
					}
				} else {
					if (weatherFxMask.flags != int4) {
						weatherFxMask.flags |= int4;
					}

					if (!weatherFxMask.enabled && boolean1) {
						weatherFxMask2 = this.getFreeMask();
						weatherFxMask2.x = int1;
						weatherFxMask2.y = int2;
						weatherFxMask2.z = int3;
						weatherFxMask2.flags = weatherFxMask.flags;
						weatherFxMask2.gs = square;
						weatherFxMask2.enabled = boolean1;
					} else {
						weatherFxMask.enabled = weatherFxMask.enabled ? weatherFxMask.enabled : boolean1;
						if (boolean1 && square != null && weatherFxMask.gs == null) {
							weatherFxMask.gs = square;
						}
					}
				}
			}
		}

		private WeatherFxMask getFreeMask() {
			if (this.maskPointer >= this.masks.length) {
				DebugLog.log("Weather Mask buffer out of bounds. Increasing cache.");
				WeatherFxMask[] weatherFxMaskArray = this.masks;
				this.masks = new WeatherFxMask[this.masks.length + 10000];
				for (int int1 = 0; int1 < this.masks.length; ++int1) {
					if (weatherFxMaskArray[int1] != null) {
						this.masks[int1] = weatherFxMaskArray[int1];
					} else {
						this.masks[int1] = new WeatherFxMask();
					}
				}
			}

			return this.masks[this.maskPointer++];
		}

		private boolean masksContains(int int1, int int2, int int3) {
			return this.getMask(int1, int2, int3) != null;
		}

		private WeatherFxMask getMask(int int1, int int2, int int3) {
			if (this.maskPointer <= 0) {
				return null;
			} else {
				int int4 = this.maskPointer - 1 - (WeatherFxMask.DIAMOND_ROWS + this.DISABLED_MASKS);
				if (int4 < 0) {
					int4 = 0;
				}

				for (int int5 = this.maskPointer - 1; int5 >= int4; --int5) {
					if (this.masks[int5].isLoc(int1, int2, int3)) {
						return this.masks[int5];
					}
				}

				return null;
			}
		}
	}
}
