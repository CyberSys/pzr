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
         for(int var0 = 0; var0 < masks.length; ++var0) {
            if (masks[var0] == null) {
               masks[var0] = new WeatherFxMask();
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
         TextureFBO var0 = Core.getInstance().getOffscreenBuffer();
         if (Core.getInstance().getOffscreenBuffer() == null) {
            DebugLog.log("fbo=" + (var0 != null));
            return false;
         } else if (fboMask != null && fboParticles != null && fboMask.getTexture().getWidth() == var0.getTexture().getWidth() && fboMask.getTexture().getHeight() == var0.getTexture().getHeight()) {
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
               var1x = new Texture(var0.getTexture().getWidth(), var0.getTexture().getHeight());
               fboMask = new TextureFBO(var1x);
            } catch (Exception var14) {
               DebugLog.log((Object)var14.getStackTrace());
               var14.printStackTrace();
            } finally {
               TextureID.bUseCompression = TextureID.bUseCompressionOption;
            }

            try {
               TextureID.bUseCompression = false;
               var1x = new Texture(var0.getTexture().getWidth(), var0.getTexture().getHeight());
               fboParticles = new TextureFBO(var1x);
            } catch (Exception var12) {
               DebugLog.log((Object)var12.getStackTrace());
               var12.printStackTrace();
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

               label80:
               while(true) {
                  MasterRegion var0;
                  do {
                     if (mrTemp.size() <= 0) {
                        break label80;
                     }

                     var0 = (MasterRegion)mrTemp.remove(0);
                     curConnectedRegions.add(var0);
                  } while(var0.getNeighbors().size() == 0);

                  Iterator var1x = var0.getNeighbors().iterator();

                  while(var1x.hasNext()) {
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

   private static boolean isOnScreen(int var0, int var1x, int var2x) {
      float var3x = (float)((int)IsoUtils.XToScreenInt(var0, var1x, var2x, 0));
      float var4 = (float)((int)IsoUtils.YToScreenInt(var0, var1x, var2x, 0));
      var3x -= (float)((int)IsoCamera.frameState.OffX);
      var4 -= (float)((int)IsoCamera.frameState.OffY);
      if (var3x + (float)(32 * Core.TileScale) <= 0.0F) {
         return false;
      } else if (var4 + (float)(32 * Core.TileScale) <= 0.0F) {
         return false;
      } else if (var3x - (float)(32 * Core.TileScale) >= (float)IsoCamera.frameState.OffscreenWidth) {
         return false;
      } else {
         return !(var4 - (float)(96 * Core.TileScale) >= (float)IsoCamera.frameState.OffscreenHeight);
      }
   }

   public boolean isLoc(int var1x, int var2x, int var3x) {
      return this.x == var1x && this.y == var2x && this.z == var3x;
   }

   public static void addMaskLocation(IsoGridSquare var0, int var1x, int var2x, int var3x) {
      if (!GameServer.bServer && hasMaskToDraw && curPlayerZ == var3x) {
         IsoGridSquare var4;
         boolean var5;
         boolean var6;
         if (isInPlayerBuilding(var0, var1x, var2x, var3x)) {
            var4 = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(var1x, var2x - 1, var3x);
            var5 = !isInPlayerBuilding(var4, var1x, var2x - 1, var3x);
            var4 = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(var1x - 1, var2x, var3x);
            var6 = !isInPlayerBuilding(var4, var1x - 1, var2x, var3x);
            var4 = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(var1x - 1, var2x - 1, var3x);
            boolean var7 = !isInPlayerBuilding(var4, var1x - 1, var2x - 1, var3x);
            int var8 = 0;
            if (var5) {
               var8 |= 1;
            }

            if (var6) {
               var8 |= 2;
            }

            if (var7) {
               var8 |= 32;
            }

            boolean var9 = false;
            if (var0 != null && (var5 || var6 || var7)) {
               byte var10 = 24;
               if (var5 && !var0.getProperties().Is("WallN") && !var0.Is("WallNW")) {
                  addMask(var1x - 1, var2x, var3x, (IsoGridSquare)null, 8, false);
                  addMask(var1x, var2x, var3x, var0, var10);
                  addMask(var1x + 1, var2x, var3x, (IsoGridSquare)null, var10, false);
                  addMask(var1x + 2, var2x, var3x, (IsoGridSquare)null, 8, false);
                  addMask(var1x, var2x + 1, var3x, (IsoGridSquare)null, 8, false);
                  addMask(var1x + 1, var2x + 1, var3x, (IsoGridSquare)null, var10, false);
                  addMask(var1x + 2, var2x + 1, var3x, (IsoGridSquare)null, var10, false);
                  addMask(var1x + 2, var2x + 2, var3x, (IsoGridSquare)null, 16, false);
                  addMask(var1x + 3, var2x + 2, var3x, (IsoGridSquare)null, 16, false);
                  var9 = true;
               }

               if (var6 && !var0.getProperties().Is("WallW") && !var0.getProperties().Is("WallNW")) {
                  addMask(var1x, var2x - 1, var3x, (IsoGridSquare)null, 8, false);
                  addMask(var1x, var2x, var3x, var0, var10);
                  addMask(var1x, var2x + 1, var3x, (IsoGridSquare)null, var10, false);
                  addMask(var1x, var2x + 2, var3x, (IsoGridSquare)null, 8, false);
                  addMask(var1x + 1, var2x, var3x, (IsoGridSquare)null, 8, false);
                  addMask(var1x + 1, var2x + 1, var3x, (IsoGridSquare)null, var10, false);
                  addMask(var1x + 1, var2x + 2, var3x, (IsoGridSquare)null, var10, false);
                  addMask(var1x + 2, var2x + 2, var3x, (IsoGridSquare)null, 16, false);
                  addMask(var1x + 2, var2x + 3, var3x, (IsoGridSquare)null, 16, false);
                  var9 = true;
               }

               if (var7) {
                  addMask(var1x, var2x, var3x, var0, var8);
                  var9 = true;
               }
            }

            if (!var9) {
               addMask(var1x, var2x, var3x, var0, var8);
            }
         } else {
            var4 = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(var1x, var2x - 1, var3x);
            var5 = isInPlayerBuilding(var4, var1x, var2x - 1, var3x);
            var4 = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(var1x - 1, var2x, var3x);
            var6 = isInPlayerBuilding(var4, var1x - 1, var2x, var3x);
            if (!var5 && !var6) {
               var4 = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(var1x - 1, var2x - 1, var3x);
               if (isInPlayerBuilding(var4, var1x - 1, var2x - 1, var3x)) {
                  addMask(var1x, var2x, var3x, var0, 4);
               }
            } else {
               int var11 = 4;
               if (var5) {
                  var11 |= 1;
               }

               if (var6) {
                  var11 |= 2;
               }

               addMask(var1x, var2x, var3x, var0, var11);
            }
         }

      }
   }

   private static boolean isInPlayerBuilding(IsoGridSquare var0, int var1x, int var2x, int var3x) {
      if (var0 != null && var0.Is(IsoFlagType.solidfloor)) {
         if (var0.getBuilding() != null && var0.getBuilding() == curPlayer.getBuilding()) {
            return true;
         }

         if (var0.getBuilding() == null) {
            return curMasterRegion != null && var0.getMasterRegion() != null && var0.getMasterRegion().isFogMask() && (var0.getMasterRegion() == curMasterRegion || curConnectedRegions.contains(var0.getMasterRegion()));
         }
      } else {
         if (isInteriorLocation(var1x, var2x, var3x)) {
            return true;
         }

         if (var0 != null && var0.getBuilding() == null) {
            return curMasterRegion != null && var0.getMasterRegion() != null && var0.getMasterRegion().isFogMask() && (var0.getMasterRegion() == curMasterRegion || curConnectedRegions.contains(var0.getMasterRegion()));
         }

         if (var0 == null && curMasterRegion != null) {
            MasterRegion var4 = IsoRegion.getMasterRegion(var1x, var2x, var3x);
            return var4 != null && var4.isFogMask() && (var4 == curMasterRegion || curConnectedRegions.contains(var4));
         }
      }

      return false;
   }

   private static boolean isInteriorLocation(int var0, int var1x, int var2x) {
      for(int var4 = var2x; var4 >= 0; --var4) {
         IsoGridSquare var3x = IsoWorld.instance.getCell().getChunkMap(curPlayerIndex).getGridSquare(var0, var1x, var4);
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

   private static void addMask(int var0, int var1x, int var2x, IsoGridSquare var3x, int var4) {
      addMask(var0, var1x, var2x, var3x, var4, true);
   }

   private static void addMask(int var0, int var1x, int var2x, IsoGridSquare var3x, int var4, boolean var5) {
      if (hasMaskToDraw) {
         WeatherFxMask var6 = getMask(var0, var1x, var2x);
         WeatherFxMask var7;
         if (var6 == null) {
            var7 = getFreeMask();
            var7.x = var0;
            var7.y = var1x;
            var7.z = var2x;
            var7.flags = var4;
            var7.gs = var3x;
            var7.enabled = var5;
            if (!var5 && DISABLED_MASKS < DIAMOND_ROWS) {
               ++DISABLED_MASKS;
            }
         } else {
            if (var6.flags != var4) {
               var6.flags |= var4;
            }

            if (!var6.enabled && var5) {
               var7 = getFreeMask();
               var7.x = var0;
               var7.y = var1x;
               var7.z = var2x;
               var7.flags = var6.flags;
               var7.gs = var3x;
               var7.enabled = var5;
            } else {
               var6.enabled = var6.enabled ? var6.enabled : var5;
               if (var5 && var3x != null && var6.gs == null) {
                  var6.gs = var3x;
               }
            }
         }

      }
   }

   private static WeatherFxMask getFreeMask() {
      if (maskPointer >= masks.length) {
         DebugLog.log("Weather Mask buffer out of bounds. Increasing cache.");
         WeatherFxMask[] var0 = masks;
         masks = new WeatherFxMask[masks.length + 10000];

         for(int var1x = 0; var1x < masks.length; ++var1x) {
            if (var0[var1x] != null) {
               masks[var1x] = var0[var1x];
            } else {
               masks[var1x] = new WeatherFxMask();
            }
         }
      }

      return masks[maskPointer++];
   }

   private static boolean masksContains(int var0, int var1x, int var2x) {
      return getMask(var0, var1x, var2x) != null;
   }

   private static WeatherFxMask getMask(int var0, int var1x, int var2x) {
      if (maskPointer <= 0) {
         return null;
      } else {
         int var3x = maskPointer - 1 - (DIAMOND_ROWS + DISABLED_MASKS);
         if (var3x < 0) {
            var3x = 0;
         }

         for(int var4 = maskPointer - 1; var4 >= var3x; --var4) {
            if (masks[var4].isLoc(var0, var1x, var2x)) {
               return masks[var4];
            }
         }

         return null;
      }
   }

   private static void scanForTiles(int var0) {
      if (!DIAMOND_ITER_DONE) {
         IsoPlayer var1x = IsoPlayer.players[var0];
         int var2x = (int)var1x.getZ();
         byte var3x = 0;
         byte var4 = 0;
         int var5 = var3x + IsoCamera.getOffscreenWidth(var0);
         int var6 = var4 + IsoCamera.getOffscreenHeight(var0);
         float var7 = IsoUtils.XToIso((float)var3x, (float)var4, 0.0F);
         float var8 = IsoUtils.YToIso((float)var5, (float)var4, 0.0F);
         float var9 = IsoUtils.XToIso((float)var5, (float)var6, 6.0F);
         float var10 = IsoUtils.YToIso((float)var3x, (float)var6, 6.0F);
         float var11 = IsoUtils.XToIso((float)var5, (float)var4, 0.0F);
         int var12 = (int)var8;
         int var13 = (int)var10;
         int var14 = (int)var7;
         int var15 = (int)var9;
         DIAMOND_ROWS = (int)var11 * 4;
         var14 -= 2;
         var12 -= 2;
         dmiter.reset(var15 - var14);
         Vector2i var17 = diamondMatrixPos;

         while(dmiter.next(var17)) {
            if (var17 != null) {
               IsoGridSquare var16 = IsoWorld.instance.getCell().getChunkMap(var0).getGridSquare(var17.x + var14, var17.y + var12, var2x);
               if (var16 == null) {
                  addMaskLocation((IsoGridSquare)null, var17.x + var14, var17.y + var12, var2x);
               } else {
                  IsoChunk var18 = var16.getChunk();
                  if (var18 != null && var16.IsOnScreen()) {
                     addMaskLocation(var16, var17.x + var14, var17.y + var12, var2x);
                  }
               }
            }
         }

      }
   }

   private static void renderMaskFloor(int var0, int var1x, int var2x) {
      floorSprite.render((IsoObject)null, (float)var0, (float)var1x, (float)var2x, IsoDirections.N, offsetX, offsetY, defColorInfo, false);
   }

   private static void renderMaskWall(int var0, int var1x, int var2x, boolean var3x, boolean var4) {
      if (var3x && var4) {
         wallNWSprite.render((IsoObject)null, (float)var0, (float)var1x, (float)var2x, IsoDirections.N, offsetX, offsetY, defColorInfo, false);
      } else if (var3x) {
         wallNSprite.render((IsoObject)null, (float)var0, (float)var1x, (float)var2x, IsoDirections.N, offsetX, offsetY, defColorInfo, false);
      } else if (var4) {
         wallWSprite.render((IsoObject)null, (float)var0, (float)var1x, (float)var2x, IsoDirections.N, offsetX, offsetY, defColorInfo, false);
      } else {
         wallSESprite.render((IsoObject)null, (float)var0, (float)var1x, (float)var2x, IsoDirections.N, offsetX, offsetY, defColorInfo, false);
      }

   }

   public static void renderFxMask(int var0) {
      if (!GameServer.bServer && IsoWeatherFX.instance != null) {
         if (LuaManager.thread == null || !LuaManager.thread.bStep) {
            if (DEBUG_KEYS && Core.bDebug) {
               updateDebugKeys();
            }

            if (MASKING_ENABLED && !checkFbos()) {
               MASKING_ENABLED = false;
            }

            if (MASKING_ENABLED && hasMaskToDraw) {
               scanForTiles(var0);
               SpriteRenderer.instance.glIgnoreStyles(true);
               if (MASKING_ENABLED) {
                  SpriteRenderer.instance.glBuffer(4, var0);
                  SpriteRenderer.instance.glDoStartFrameFx(Core.getInstance().getOffscreenWidth(var0), Core.getInstance().getOffscreenHeight(var0), var0);
                  if (PerformanceSettings.LightingFrameSkip < 3) {
                     IsoWorld.instance.getCell().DrawStencilMask();
                     SpriteRenderer.instance.glClearColor(0, 0, 0, 0);
                     SpriteRenderer.instance.glClear(16640);
                     SpriteRenderer.instance.glClearColor(0, 0, 0, 255);
                  }

                  boolean var1x = true;
                  boolean var2x = false;

                  for(int var6 = 0; var6 < maskPointer; ++var6) {
                     WeatherFxMask var7 = masks[var6];
                     if (var7.enabled) {
                        boolean var4;
                        boolean var5;
                        if ((var7.flags & 4) == 4) {
                           SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
                           SpriteRenderer.instance.glBlendFunc(SCR_MASK_SUB, DST_MASK_SUB);
                           SpriteRenderer.instance.glBlendEquation(32779);
                           IndieGL.enableStencilTest();
                           IndieGL.enableAlphaTest();
                           IndieGL.glAlphaFunc(516, 0.02F);
                           IndieGL.glStencilFunc(517, 128, 128);
                           SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
                           var4 = (var7.flags & 1) == 1;
                           var5 = (var7.flags & 2) == 2;
                           renderMaskWall(var7.x, var7.y, var7.z, var4, var5);
                           SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
                           IndieGL.glStencilFunc(519, 255, 255);
                           SpriteRenderer.instance.glBlendEquation(32774);
                           SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
                        } else {
                           SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
                           SpriteRenderer.instance.glBlendFunc(SCR_MASK_ADD, DST_MASK_ADD);
                           SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
                           renderMaskFloor(var7.x, var7.y, var7.z);
                           var2x = (var7.flags & 16) == 16;
                           boolean var3x = (var7.flags & 8) == 8;
                           if (!var2x) {
                              var4 = (var7.flags & 1) == 1;
                              var5 = (var7.flags & 2) == 2;
                              if (!var4 && !var5) {
                                 if ((var7.flags & 32) == 32) {
                                    renderMaskWall(var7.x, var7.y, var7.z, false, false);
                                 }
                              } else {
                                 renderMaskWall(var7.x, var7.y, var7.z, var4, var5);
                              }
                           }

                           if (var2x && var7.gs != null) {
                              var7.gs.RenderMinusFloorFxMask(0, 0, var7.z + 1, 0, false, false, var1x);
                           }

                           if (var3x && var7.gs != null) {
                              var7.gs.RenderCharacters(var7.z + 1, false);
                              SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
                              SpriteRenderer.instance.glBlendFunc(SCR_MASK_ADD, DST_MASK_ADD);
                              SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
                           }
                        }
                     }
                  }

                  SpriteRenderer.instance.glBlendFunc(770, 771);
                  SpriteRenderer.instance.glBuffer(5, var0);
                  SpriteRenderer.instance.glDoEndFrameFx(var0);
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

               if (!RenderSettings.getInstance().getPlayerSettings(var0).isExterior()) {
                  drawFxLayered(var0, false, false, false);
               }

               if (IsoWeatherFX.instance.hasCloudsToRender()) {
                  drawFxLayered(var0, true, false, false);
               }

               if (IsoWeatherFX.instance.hasFogToRender()) {
                  drawFxLayered(var0, false, true, false);
               }

               if (IsoWeatherFX.instance.hasPrecipitationToRender()) {
                  drawFxLayered(var0, false, false, true);
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

   private static void drawFxLayered(int var0, boolean var1x, boolean var2x, boolean var3x) {
      int var4 = IsoCamera.getOffscreenLeft(var0);
      int var5 = IsoCamera.getOffscreenTop(var0);
      int var6 = IsoCamera.getOffscreenWidth(var0);
      int var7 = IsoCamera.getOffscreenHeight(var0);
      SpriteRenderer.instance.glBuffer(6, var0);
      SpriteRenderer.instance.glDoStartFrameFx(Core.getInstance().getOffscreenWidth(var0), Core.getInstance().getOffscreenHeight(var0), var0);
      if (!var1x && !var2x && !var3x) {
         int var8 = Core.getInstance().getOffscreenWidth(var0);
         int var9 = Core.getInstance().getOffscreenHeight(var0);
         Color var10 = RenderSettings.getInstance().getMaskClearColorForPlayer(var0);
         SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
         SpriteRenderer.instance.glBlendFuncSeparate(SCR_PARTICLES, DST_PARTICLES, 1, 771);
         SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
         SpriteRenderer.instance.render(texWhite, 0, 0, var8, var9, var10.r, var10.g, var10.b, var10.a);
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
         ((Texture)fboMask.getTexture()).rendershader2(var4, var5, var6, var7, var4, var5, var6, var7, 1.0F, 1.0F, 1.0F, 1.0F);
         SpriteRenderer.instance.glBlendEquation(32774);
      }

      SpriteRenderer.instance.glBlendFunc(770, 771);
      SpriteRenderer.instance.glBuffer(7, var0);
      SpriteRenderer.instance.glDoEndFrameFx(var0);
      if ((DEBUG_MASK || DEBUG_MASK_AND_PARTICLES) && !DEBUG_MASK_AND_PARTICLES) {
         SpriteRenderer.instance.glBlendFunc(770, 771);
         ((Texture)fboMask.getTexture()).render(0, Core.getInstance().getOffscreenHeight(var0), Core.getInstance().getOffscreenWidth(var0), -Core.getInstance().getOffscreenHeight(var0), 1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         SpriteRenderer.instance.glBlendFunc(SCR_FINAL, DST_FINAL);
         ((Texture)fboParticles.getTexture()).render(0, Core.getInstance().getOffscreenHeight(var0), Core.getInstance().getOffscreenWidth(var0), -Core.getInstance().getOffscreenHeight(var0), 1.0F, 1.0F, 1.0F, 1.0F);
      }

   }

   private static void initGlIds() {
      for(int var0 = 0; var0 < test.length; ++var0) {
         if (test[var0] == SCR_MASK_ADD) {
            ID_SCR_MASK_ADD = var0;
         } else if (test[var0] == DST_MASK_ADD) {
            ID_DST_MASK_ADD = var0;
         } else if (test[var0] == SCR_MASK_SUB) {
            ID_SCR_MASK_SUB = var0;
         } else if (test[var0] == DST_MASK_SUB) {
            ID_DST_MASK_SUB = var0;
         } else if (test[var0] == SCR_PARTICLES) {
            ID_SCR_PARTICLES = var0;
         } else if (test[var0] == DST_PARTICLES) {
            ID_DST_PARTICLES = var0;
         } else if (test[var0] == SCR_MERGE) {
            ID_SCR_MERGE = var0;
         } else if (test[var0] == DST_MERGE) {
            ID_DST_MERGE = var0;
         } else if (test[var0] == SCR_FINAL) {
            ID_SCR_FINAL = var0;
         } else if (test[var0] == DST_FINAL) {
            ID_DST_FINAL = var0;
         }
      }

   }

   private static void updateDebugKeys() {
      if (keypause > 0) {
         --keypause;
      }

      if (keypause == 0) {
         boolean var0 = false;
         boolean var1x = false;
         boolean var2x = false;
         boolean var3x = false;
         boolean var4 = false;
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
            var0 = true;
            var2x = true;
         } else if (MASKING_ENABLED && GameKeyboard.isKeyDown(80)) {
            DEBUG_MASK_AND_PARTICLES = !DEBUG_MASK_AND_PARTICLES;
            var0 = true;
            var3x = true;
         } else if (!GameKeyboard.isKeyDown(72) && GameKeyboard.isKeyDown(76)) {
            MASKING_ENABLED = !MASKING_ENABLED;
            var0 = true;
            var4 = true;
         }

         if (var0) {
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
            } else if (var4) {
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

   static {
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
