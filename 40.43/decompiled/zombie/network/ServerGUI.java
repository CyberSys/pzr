package zombie.network;

import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.VBO.GLVertexBufferObject;
import zombie.core.opengl.RenderThread;
import zombie.core.physics.WorldSimulation;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.TextureDraw;
import zombie.core.textures.TextureID;
import zombie.core.textures.TexturePackPage;
import zombie.debug.LineDrawer;
import zombie.gameStates.MainScreenState;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWindow;
import zombie.iso.sprite.IsoSprite;
import zombie.ui.TextManager;
import zombie.vehicles.BaseVehicle;

public class ServerGUI {
   private static boolean created;
   private static int minX;
   private static int minY;
   private static int maxX;
   private static int maxY;
   private static int maxZ;
   private static final ArrayList GridStack = new ArrayList();
   private static final ArrayList MinusFloorCharacters = new ArrayList(1000);
   private static final ArrayList SolidFloor = new ArrayList(5000);
   private static final ArrayList VegetationCorpses = new ArrayList(5000);
   private static final ColorInfo defColorInfo = new ColorInfo();

   public static boolean isCreated() {
      return created;
   }

   public static void init() {
      created = true;

      try {
         Display.setFullscreen(false);
         Display.setResizable(false);
         Display.setVSyncEnabled(false);
         Display.setTitle("Project Zomboid Server");
         System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
         Core.width = 1366;
         Core.height = 768;
         Display.setDisplayMode(new DisplayMode(Core.width, Core.height));
         Display.create(new PixelFormat(32, 0, 24, 8, 0));
         Display.setIcon(MainScreenState.loadIcons());
         GLVertexBufferObject.init();
         TextureID.bUseCompression = false;
         TexturePackPage.bIgnoreWorldItemTextures = true;
         GameWindow.LoadTexturePack("UI");
         GameWindow.LoadTexturePack("UI2");
         GameWindow.LoadTexturePack("IconsMoveables");
         GameWindow.LoadTexturePack("RadioIcons");
         GameWindow.LoadTexturePack("ApComUI");
         GameWindow.LoadTexturePack("WeatherFx");
         TexturePackPage.bIgnoreWorldItemTextures = false;
         SpriteRenderer.instance = new SpriteRenderer();
         SpriteRenderer.instance.create();
         TextManager.instance.Init();
         GameWindow.LoadTexturePack("Tiles2x");
         GameWindow.LoadTexturePack("JumboTrees2x");
         TextureID.bUseCompression = false;
         GameWindow.LoadTexturePack("Tiles2x.floor");
         TextureID.bUseCompression = TextureID.bUseCompressionOption;
         GameWindow.LoadTexturePackDDS("Characters");
         IsoObjectPicker.Instance.Init();
         GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
         Display.releaseContext();
         RenderThread.init();
         Core.getInstance().initFBOs();
         ModelManager.instance.create();
      } catch (Exception var1) {
         var1.printStackTrace();
         created = false;
      }
   }

   public static void init2() {
      if (created) {
         BaseVehicle.LoadAllVehicleTextures();
      }
   }

   public static void shutdown() {
      if (created) {
         RenderThread.borrowContext();
         Display.destroy();
      }
   }

   public static void update() {
      if (created) {
         Mouse.update();
         GameKeyboard.update();
         Display.processMessages();
         if (Display.isCloseRequested()) {
         }

         if (Core.getInstance().OffscreenBuffer.Current != null) {
            Core.getInstance().OffscreenBuffer.zoom[0] = 2.0F;
            Core.getInstance().OffscreenBuffer.targetZoom[0] = 2.0F;
         }

         byte var0 = 0;
         Core.getInstance().StartFrame(var0, true);
         renderWorld();
         Core.getInstance().EndFrame(var0);
         Core.getInstance().RenderOffScreenBuffer();
         Core.getInstance().StartFrameUI();
         renderUI();
         Core.getInstance().EndFrameUI();
      }
   }

   private static IsoPlayer getPlayerToFollow() {
      for(int var0 = 0; var0 < GameServer.udpEngine.connections.size(); ++var0) {
         UdpConnection var1 = (UdpConnection)GameServer.udpEngine.connections.get(var0);
         if (var1.isFullyConnected()) {
            for(int var2 = 0; var2 < 4; ++var2) {
               IsoPlayer var3 = var1.players[var2];
               if (var3 != null && var3.OnlineID != -1) {
                  return var3;
               }
            }
         }
      }

      return null;
   }

   private static void updateCamera(IsoPlayer var0) {
      byte var2 = 0;
      float var3 = IsoUtils.XToScreen(var0.x + IsoCamera.DeferedX[var2], var0.y + IsoCamera.DeferedY[var2], var0.z, 0);
      float var4 = IsoUtils.YToScreen(var0.x + IsoCamera.DeferedX[var2], var0.y + IsoCamera.DeferedY[var2], var0.z, 0);
      var3 -= (float)(IsoCamera.getOffscreenWidth(var2) / 2);
      var4 -= (float)(IsoCamera.getOffscreenHeight(var2) / 2);
      var4 -= var0.getOffsetY() * 1.5F;
      var3 = (float)((int)var3);
      var4 = (float)((int)var4);
      var3 += (float)IsoCamera.PLAYER_OFFSET_X;
      var4 += (float)IsoCamera.PLAYER_OFFSET_Y;
      IsoCamera.OffX[IsoPlayer.getPlayerIndex()] = var3;
      IsoCamera.OffY[IsoPlayer.getPlayerIndex()] = var4;
      IsoCamera.FrameState var5 = IsoCamera.frameState;
      var5.Paused = false;
      var5.playerIndex = var2;
      var5.CamCharacter = var0;
      var5.CamCharacterX = IsoCamera.CamCharacter.getX();
      var5.CamCharacterY = IsoCamera.CamCharacter.getY();
      var5.CamCharacterZ = IsoCamera.CamCharacter.getZ();
      var5.CamCharacterSquare = IsoCamera.CamCharacter.getCurrentSquare();
      var5.CamCharacterRoom = var5.CamCharacterSquare == null ? null : var5.CamCharacterSquare.getRoom();
      var5.OffX = IsoCamera.getOffX();
      var5.OffY = IsoCamera.getOffY();
      var5.OffscreenWidth = IsoCamera.getOffscreenWidth(var2);
      var5.OffscreenHeight = IsoCamera.getOffscreenHeight(var2);
   }

   private static void renderWorld() {
      IsoPlayer var0 = getPlayerToFollow();
      if (var0 != null) {
         byte var1 = 0;
         IsoPlayer.instance = var0;
         IsoPlayer.players[0] = var0;
         IsoCamera.CamCharacter = var0;
         updateCamera(var0);
         IsoSprite.globalOffsetX = -1;
         byte var2 = 0;
         byte var3 = 0;
         int var4 = var2 + IsoCamera.getOffscreenWidth(var1);
         int var5 = var3 + IsoCamera.getOffscreenHeight(var1);
         float var6 = IsoUtils.XToIso((float)var2, (float)var3, 0.0F);
         float var7 = IsoUtils.YToIso((float)var4, (float)var3, 0.0F);
         float var8 = IsoUtils.XToIso((float)var4, (float)var5, 6.0F);
         float var9 = IsoUtils.YToIso((float)var2, (float)var5, 6.0F);
         minY = (int)var7;
         maxY = (int)var9;
         minX = (int)var6;
         maxX = (int)var8;
         minX -= 2;
         minY -= 2;
         maxZ = (int)var0.getZ();
         IsoCell var10 = IsoWorld.instance.CurrentCell;
         var10.DrawStencilMask();
         IsoObjectPicker.Instance.StartRender();
         RenderTiles();

         int var11;
         for(var11 = 0; var11 < var10.getObjectList().size(); ++var11) {
            IsoMovingObject var12 = (IsoMovingObject)var10.getObjectList().get(var11);
            var12.renderlast();
         }

         for(var11 = 0; var11 < var10.getStaticUpdaterObjectList().size(); ++var11) {
            IsoObject var13 = (IsoObject)var10.getStaticUpdaterObjectList().get(var11);
            var13.renderlast();
         }

         if (WorldSimulation.instance.created) {
            TextureDraw.GenericDrawer var14 = WorldSimulation.getDrawer(var1);
            SpriteRenderer.instance.drawGeneric(var14);
         }

         WorldSoundManager.instance.render();
         LineDrawer.drawLines();
      }
   }

   private static void RenderTiles() {
      IsoCell var0 = IsoWorld.instance.CurrentCell;
      if (IsoCell.perPlayerRender[0] == null) {
         IsoCell.perPlayerRender[0] = new IsoCell.PerPlayerRender();
      }

      IsoCell.PerPlayerRender var2 = IsoCell.perPlayerRender[0];
      if (var2 == null) {
         IsoCell.perPlayerRender[0] = new IsoCell.PerPlayerRender();
      }

      var2.setSize(maxX - minX + 1, maxY - minY + 1);
      short[][][] var3 = var2.StencilValues;

      for(int var4 = 0; var4 <= maxZ; ++var4) {
         GridStack.clear();

         int var5;
         int var8;
         label104:
         for(var5 = minY; var5 < maxY; ++var5) {
            int var6 = minX;
            IsoGridSquare var7 = ServerMap.instance.getGridSquare(var6, var5, var4);
            var8 = IsoDirections.E.index();

            while(true) {
               while(true) {
                  if (var6 >= maxX) {
                     continue label104;
                  }

                  if (var4 == 0) {
                     var3[var6 - minX][var5 - minY][0] = 0;
                     var3[var6 - minX][var5 - minY][1] = 0;
                  }

                  if (var7 != null && var7.getY() != var5) {
                     var7 = null;
                  }

                  if (var7 == null) {
                     var7 = ServerMap.instance.getGridSquare(var6, var5, var4);
                     if (var7 == null) {
                        ++var6;
                        continue;
                     }
                  }

                  IsoChunk var9 = var7.getChunk();
                  if (var9 != null && var7.IsOnScreen()) {
                     GridStack.add(var7);
                  }

                  var7 = var7.nav[var8];
                  ++var6;
               }
            }
         }

         SolidFloor.clear();
         VegetationCorpses.clear();
         MinusFloorCharacters.clear();

         IsoGridSquare var12;
         for(var5 = 0; var5 < GridStack.size(); ++var5) {
            var12 = (IsoGridSquare)GridStack.get(var5);
            var12.setLightInfoServerGUIOnly(defColorInfo);
            int var13 = renderFloor(var12);
            if (!var12.getStaticMovingObjects().isEmpty()) {
               var13 |= 2;
            }

            for(var8 = 0; var8 < var12.getMovingObjects().size(); ++var8) {
               IsoMovingObject var14 = (IsoMovingObject)var12.getMovingObjects().get(var8);
               boolean var10 = var14.isOnFloor();
               if (var10 && var14 instanceof IsoZombie) {
                  IsoZombie var11 = (IsoZombie)var14;
                  var10 = var11.bCrawling || var11.legsSprite.CurrentAnim != null && var11.legsSprite.CurrentAnim.name.equals("ZombieDeath") && var11.def.isFinished();
               }

               if (var10) {
                  var13 |= 2;
               } else {
                  var13 |= 4;
               }
            }

            if ((var13 & 1) != 0) {
               SolidFloor.add(var12);
            }

            if ((var13 & 2) != 0) {
               VegetationCorpses.add(var12);
            }

            if ((var13 & 4) != 0) {
               MinusFloorCharacters.add(var12);
            }
         }

         LuaEventManager.triggerEvent("OnPostFloorLayerDraw", var4);

         for(var5 = 0; var5 < VegetationCorpses.size(); ++var5) {
            var12 = (IsoGridSquare)VegetationCorpses.get(var5);
            renderMinusFloor(var12, false, true);
            renderCharacters(var12, true);
         }

         for(var5 = 0; var5 < MinusFloorCharacters.size(); ++var5) {
            var12 = (IsoGridSquare)MinusFloorCharacters.get(var5);
            boolean var15 = renderMinusFloor(var12, false, false);
            renderCharacters(var12, false);
            if (var15) {
               renderMinusFloor(var12, true, false);
            }
         }
      }

      MinusFloorCharacters.clear();
      SolidFloor.clear();
      VegetationCorpses.clear();
   }

   private static int renderFloor(IsoGridSquare var0) {
      int var1 = 0;
      byte var2 = 0;

      for(int var3 = 0; var3 < var0.getObjects().size(); ++var3) {
         IsoObject var4 = (IsoObject)var0.getObjects().get(var3);
         boolean var5 = true;
         if (var4.sprite != null && !var4.sprite.Properties.Is(IsoFlagType.solidfloor)) {
            var5 = false;
            var1 |= 4;
         }

         if (var5) {
            IndieGL.glAlphaFunc(516, 0.0F);
            var4.alpha[var2] = 1.0F;
            var4.targetAlpha[var2] = 1.0F;
            var4.render((float)var0.x, (float)var0.y, (float)var0.z, defColorInfo, true);
            var4.renderObjectPicker((float)var0.x, (float)var0.y, (float)var0.z, defColorInfo);
            if ((var4.highlightFlags & 2) != 0) {
               var4.highlightFlags &= -2;
            }

            var1 |= 1;
         }

         if (!var5 && var4.sprite != null && (var4.sprite.Properties.Is(IsoFlagType.canBeRemoved) || var4.sprite.Properties.Is(IsoFlagType.attachedFloor))) {
            var1 |= 2;
         }
      }

      return var1;
   }

   private static boolean isSpriteOnSouthOrEastWall(IsoObject var0) {
      if (var0 instanceof IsoBarricade) {
         return var0.getDir() == IsoDirections.S || var0.getDir() == IsoDirections.E;
      } else if (!(var0 instanceof IsoCurtain)) {
         return false;
      } else {
         IsoCurtain var1 = (IsoCurtain)var0;
         return var1.getType() == IsoObjectType.curtainS || var1.getType() == IsoObjectType.curtainE;
      }
   }

   private static int DoWallLightingN(IsoGridSquare var0, IsoObject var1, int var2) {
      var1.render((float)var0.x, (float)var0.y, (float)var0.z, defColorInfo, true, false);
      return var2;
   }

   private static int DoWallLightingW(IsoGridSquare var0, IsoObject var1, int var2) {
      var1.render((float)var0.x, (float)var0.y, (float)var0.z, defColorInfo, true, false);
      return var2;
   }

   private static int DoWallLightingNW(IsoGridSquare var0, IsoObject var1, int var2) {
      var1.render((float)var0.x, (float)var0.y, (float)var0.z, defColorInfo, true, false);
      return var2;
   }

   private static boolean renderMinusFloor(IsoGridSquare var0, boolean var1, boolean var2) {
      int var3 = var1 ? var0.getObjects().size() - 1 : 0;
      int var4 = var1 ? 0 : var0.getObjects().size() - 1;
      int var5 = IsoCamera.frameState.playerIndex;
      IsoGridSquare var6 = IsoCamera.frameState.CamCharacterSquare;
      IsoRoom var7 = IsoCamera.frameState.CamCharacterRoom;
      int var10 = (int)(IsoUtils.XToScreenInt(var0.x, var0.y, var0.z, 0) - IsoCamera.frameState.OffX);
      int var11 = (int)(IsoUtils.YToScreenInt(var0.x, var0.y, var0.z, 0) - IsoCamera.frameState.OffY);
      boolean var12 = true;
      IsoCell var13 = var0.getCell();
      if (var10 + 32 * Core.TileScale <= var13.StencilX1 || var10 - 32 * Core.TileScale >= var13.StencilX2 || var11 + 32 * Core.TileScale <= var13.StencilY1 || var11 - 96 * Core.TileScale >= var13.StencilY2) {
         var12 = false;
      }

      int var14 = 0;
      boolean var15 = false;
      int var16 = var3;

      while(true) {
         if (var1) {
            if (var16 < var4) {
               break;
            }
         } else if (var16 > var4) {
            break;
         }

         IsoObject var17 = (IsoObject)var0.getObjects().get(var16);
         boolean var18 = true;
         IsoGridSquare.CircleStencil = false;
         if (var17.sprite != null && var17.sprite.getProperties().Is(IsoFlagType.solidfloor)) {
            var18 = false;
         }

         if ((!var2 || var17.sprite == null || var17.sprite.Properties.Is(IsoFlagType.canBeRemoved) || var17.sprite.Properties.Is(IsoFlagType.attachedFloor)) && (var2 || var17.sprite == null || !var17.sprite.Properties.Is(IsoFlagType.canBeRemoved) && !var17.sprite.Properties.Is(IsoFlagType.attachedFloor))) {
            if (var17.sprite != null && (var17.sprite.getType() == IsoObjectType.WestRoofB || var17.sprite.getType() == IsoObjectType.WestRoofM || var17.sprite.getType() == IsoObjectType.WestRoofT) && var0.z == maxZ && var0.z == (int)IsoCamera.CamCharacter.getZ()) {
               var18 = false;
            }

            if (IsoCamera.CamCharacter.isClimbing() && var17.sprite != null && !var17.sprite.getProperties().Is(IsoFlagType.solidfloor)) {
               var18 = true;
            }

            if (isSpriteOnSouthOrEastWall(var17)) {
               if (!var1) {
                  var18 = false;
               }

               var15 = true;
            } else if (var1) {
               var18 = false;
            }

            if (var18) {
               IndieGL.glAlphaFunc(516, 0.0F);
               IsoGridSquare var23;
               if (var17.sprite != null && !var0.getProperties().Is(IsoFlagType.blueprint) && (var17.sprite.getType() == IsoObjectType.doorFrW || var17.sprite.getType() == IsoObjectType.doorFrN || var17.sprite.getType() == IsoObjectType.doorW || var17.sprite.getType() == IsoObjectType.doorN || var17.sprite.getProperties().Is(IsoFlagType.cutW) || var17.sprite.getProperties().Is(IsoFlagType.cutN)) && PerformanceSettings.LightingFrameSkip < 3) {
                  if (var17.targetAlpha[var5] < 1.0F) {
                     boolean var22 = false;
                     if (var22) {
                        if (var17.sprite.getProperties().Is(IsoFlagType.cutW) && var0.getProperties().Is(IsoFlagType.WallSE)) {
                           var23 = var0.nav[IsoDirections.NW.index()];
                           if (var23 == null || var23.getRoom() == null) {
                              var22 = false;
                           }
                        } else if (var17.sprite.getType() != IsoObjectType.doorFrW && var17.sprite.getType() != IsoObjectType.doorW && !var17.sprite.getProperties().Is(IsoFlagType.cutW)) {
                           if (var17.sprite.getType() == IsoObjectType.doorFrN || var17.sprite.getType() == IsoObjectType.doorN || var17.sprite.getProperties().Is(IsoFlagType.cutN)) {
                              var23 = var0.nav[IsoDirections.N.index()];
                              if (var23 == null || var23.getRoom() == null) {
                                 var22 = false;
                              }
                           }
                        } else {
                           var23 = var0.nav[IsoDirections.W.index()];
                           if (var23 == null || var23.getRoom() == null) {
                              var22 = false;
                           }
                        }
                     }

                     if (!var22) {
                        IsoGridSquare.CircleStencil = var12;
                     }

                     var17.targetAlpha[var5] = 1.0F;
                     var17.alpha[var5] = 1.0F;
                  }

                  if (var17.sprite.getProperties().Is(IsoFlagType.cutW) && var17.sprite.getProperties().Is(IsoFlagType.cutN)) {
                     var14 = DoWallLightingNW(var0, var17, var14);
                  } else if (var17.sprite.getType() != IsoObjectType.doorFrW && var17.sprite.getType() != IsoObjectType.doorW && !var17.sprite.getProperties().Is(IsoFlagType.cutW)) {
                     if (var17.sprite.getType() == IsoObjectType.doorFrN || var17.sprite.getType() == IsoObjectType.doorN || var17.sprite.getProperties().Is(IsoFlagType.cutN)) {
                        var14 = DoWallLightingN(var0, var17, var14);
                     }
                  } else {
                     var14 = DoWallLightingW(var0, var17, var14);
                  }
               } else {
                  if (var6 != null) {
                  }

                  var17.targetAlpha[var5] = 1.0F;
                  if (IsoCamera.CamCharacter != null && var17.getProperties() != null && (var17.getProperties().Is(IsoFlagType.solid) || var17.getProperties().Is(IsoFlagType.solidtrans))) {
                     int var19 = var0.getX() - (int)IsoCamera.CamCharacter.getX();
                     int var20 = var0.getY() - (int)IsoCamera.CamCharacter.getY();
                     if (var19 > 0 && var19 < 3 && var20 >= 0 && var20 < 3 || var20 > 0 && var20 < 3 && var19 >= 0 && var19 < 3) {
                        var17.targetAlpha[var5] = 0.99F;
                     }
                  }

                  if (var17 instanceof IsoWindow && var17.targetAlpha[var5] < 1.0E-4F) {
                     IsoWindow var21 = (IsoWindow)var17;
                     var23 = var21.getOppositeSquare();
                     if (var23 != null && var23 != var0 && var23.lighting[var5].bSeen()) {
                        var17.targetAlpha[var5] = var23.lighting[var5].darkMulti() * 2.0F;
                     }
                  }

                  if (var17 instanceof IsoTree) {
                     if (var12 && var0.x >= (int)IsoCamera.frameState.CamCharacterX && var0.y >= (int)IsoCamera.frameState.CamCharacterY && var6 != null && var6.Is(IsoFlagType.exterior)) {
                        ((IsoTree)var17).bRenderFlag = true;
                     } else {
                        ((IsoTree)var17).bRenderFlag = false;
                     }
                  }

                  var17.render((float)var0.x, (float)var0.y, (float)var0.z, defColorInfo, true);
               }

               if (var17.sprite != null) {
                  var17.renderObjectPicker((float)var0.x, (float)var0.y, (float)var0.z, defColorInfo);
               }

               if ((var17.highlightFlags & 2) != 0) {
                  var17.highlightFlags &= -2;
               }
            }
         }

         var16 += var1 ? -1 : 1;
      }

      return var15;
   }

   private static void renderCharacters(IsoGridSquare var0, boolean var1) {
      int var2 = var0.getStaticMovingObjects().size();

      int var3;
      IsoMovingObject var4;
      for(var3 = 0; var3 < var2; ++var3) {
         var4 = (IsoMovingObject)var0.getStaticMovingObjects().get(var3);
         if (var4.sprite != null && (!var1 || var4 instanceof IsoDeadBody) && (var1 || !(var4 instanceof IsoDeadBody))) {
            var4.render(var4.getX(), var4.getY(), var4.getZ(), defColorInfo, true);
            var4.renderObjectPicker(var4.getX(), var4.getY(), var4.getZ(), defColorInfo);
         }
      }

      var2 = var0.getMovingObjects().size();

      for(var3 = 0; var3 < var2; ++var3) {
         var4 = (IsoMovingObject)var0.getMovingObjects().get(var3);
         if (var4 != null && var4.sprite != null) {
            boolean var5 = var4.isOnFloor();
            if (var5 && var4 instanceof IsoZombie) {
               IsoZombie var6 = (IsoZombie)var4;
               var5 = var6.bCrawling || var6.legsSprite.CurrentAnim != null && var6.legsSprite.CurrentAnim.name.equals("ZombieDeath") && var6.def.isFinished();
            }

            if ((!var1 || var5) && (var1 || !var5)) {
               var4.alpha[0] = var4.targetAlpha[0] = 1.0F;
               if (var4 instanceof IsoGameCharacter) {
                  IsoGameCharacter var11 = (IsoGameCharacter)var4;
                  float var7 = (float)Core.TileScale;
                  float var8 = var11.offsetX + (float)IsoGameCharacter.RENDER_OFFSET_X * var7;
                  float var9 = var11.offsetY + (float)IsoGameCharacter.RENDER_OFFSET_Y * var7;
                  if (var11.sprite != null) {
                     var11.def.setScale(var7, var7);
                     if (!var11.isbUseParts()) {
                        var11.sprite.render(var11.def, var11, var11.x, var11.y, var11.z, var11.dir, var8, var9, defColorInfo);
                     } else {
                        var11.def.Flip = false;
                        var11.legsSprite.render(var11.def, var11, var11.x, var11.y, var11.z, var11.dir, var8, var9, defColorInfo);
                        if (!var11.hasActiveModel()) {
                           if (var11.torsoSprite != null) {
                              var11.torsoSprite.render(var11.def, var11, var11.x, var11.y, var11.z, var11.dir, var8, var9, defColorInfo);
                           }

                           if (var11.shoeSprite != null) {
                              var11.shoeSprite.render(var11.def, var11, var11.x, var11.y, var11.z, var11.dir, var8, var9, defColorInfo);
                           }

                           if (var11.bottomsSprite != null) {
                              var11.bottomsSprite.render(var11.def, var11, var11.x, var11.y, var11.z, var11.dir, var8, var9, defColorInfo);
                           }

                           if (var11.topSprite != null) {
                              var11.topSprite.render(var11.def, var11, var11.x, var11.y, var11.z, var11.dir, var8, var9, defColorInfo);
                           }

                           if (var11.headSprite != null) {
                              var11.headSprite.render(var11.def, var11, var11.x, var11.y, var11.z, var11.dir, var8, var9, defColorInfo);
                           }

                           if (var11.hairSprite != null) {
                              var11.hairSprite.render(var11.def, var11, var11.x, var11.y, var11.z, var11.dir, var8, var9, defColorInfo);
                           }

                           for(int var10 = 0; var10 < var11.extraSprites.size(); ++var10) {
                              ((IsoSprite)var11.extraSprites.get(var10)).render(var11.def, var11, var11.x, var11.y, var11.z, var11.dir, var8, var9, defColorInfo);
                           }
                        }
                     }
                  }
               } else {
                  var4.render(var4.getX(), var4.getY(), var4.getZ(), defColorInfo, true);
               }

               var4.renderObjectPicker(var4.getX(), var4.getY(), var4.getZ(), defColorInfo);
            }
         }
      }

   }

   private static void renderUI() {
   }
}
