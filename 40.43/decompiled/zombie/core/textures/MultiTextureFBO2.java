package zombie.core.textures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import zombie.GameTime;
import zombie.IndieGL;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.debug.DebugLog;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.network.GameServer;
import zombie.network.ServerGUI;

public class MultiTextureFBO2 {
   private final float[] zoomLevels1x = new float[]{1.5F, 1.25F, 1.0F, 0.75F, 0.5F};
   private final float[] zoomLevels2x = new float[]{2.5F, 2.25F, 2.0F, 1.75F, 1.5F, 1.25F, 1.0F, 0.75F, 0.5F};
   private float[] zoomLevels;
   public TextureFBO Current;
   public volatile TextureFBO FBOrendered = null;
   public float[] zoom = new float[4];
   public float[] targetZoom = new float[4];
   public float[] startZoom = new float[4];
   private float zoomedInLevel;
   private float zoomedOutLevel;
   public boolean[] bAutoZoom = new boolean[4];
   public boolean bZoomEnabled = true;

   public MultiTextureFBO2() {
      for(int var1 = 0; var1 < 4; ++var1) {
         this.zoom[var1] = this.targetZoom[var1] = this.startZoom[var1] = 1.0F;
      }

   }

   public int getWidth(int var1) {
      return (int)((float)IsoCamera.getScreenWidth(var1) * this.zoom[var1]);
   }

   public int getHeight(int var1) {
      return (int)((float)IsoCamera.getScreenHeight(var1) * this.zoom[var1]);
   }

   public void setTargetZoom(int var1, float var2) {
      if (this.targetZoom[var1] != var2) {
         this.targetZoom[var1] = var2;
         this.startZoom[var1] = this.zoom[var1];
      }

   }

   public void setTargetZoomNoRestart(float var1) {
      if (this.targetZoom[IsoPlayer.getPlayerIndex()] != var1) {
         this.targetZoom[IsoPlayer.getPlayerIndex()] = var1;
      }

   }

   public ArrayList getDefaultZoomLevels() {
      ArrayList var1 = new ArrayList();
      float[] var2 = Core.TileScale == 2 ? this.zoomLevels2x : this.zoomLevels1x;

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var1.add(Math.round(var2[var3] * 100.0F));
      }

      return var1;
   }

   public void setZoomLevelsFromOption(String var1) {
      this.zoomLevels = Core.TileScale == 1 ? this.zoomLevels1x : this.zoomLevels2x;
      if (var1 != null && !var1.isEmpty()) {
         String[] var2 = var1.split(";");
         if (var2.length != 0) {
            ArrayList var3 = new ArrayList();
            String[] var4 = var2;
            int var5 = var2.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               String var7 = var4[var6];
               if (!var7.isEmpty()) {
                  try {
                     int var8 = Integer.parseInt(var7);
                     float[] var9 = this.zoomLevels;
                     int var10 = var9.length;

                     for(int var11 = 0; var11 < var10; ++var11) {
                        float var12 = var9[var11];
                        if (Math.round(var12 * 100.0F) == var8) {
                           if (!var3.contains(var8)) {
                              var3.add(var8);
                           }
                           break;
                        }
                     }
                  } catch (NumberFormatException var13) {
                  }
               }
            }

            if (!var3.contains(100)) {
               var3.add(100);
            }

            Collections.sort(var3, new Comparator() {
               public int compare(Integer var1, Integer var2) {
                  return var2 - var1;
               }
            });
            this.zoomLevels = new float[var3.size()];

            for(int var14 = 0; var14 < var3.size(); ++var14) {
               this.zoomLevels[var14] = (float)(Integer)var3.get(var14) / 100.0F;
            }

         }
      }
   }

   public void destroy() {
      if (this.Current != null) {
         this.Current.destroy();
         this.Current = null;
         this.FBOrendered = null;

         for(int var1 = 0; var1 < 4; ++var1) {
            this.zoom[var1] = this.targetZoom[var1] = 1.0F;
         }

      }
   }

   public void create(int var1, int var2) throws Exception {
      if (this.bZoomEnabled) {
         if (this.zoomLevels == null) {
            this.zoomLevels = Core.TileScale == 1 ? this.zoomLevels1x : this.zoomLevels2x;
         }

         this.zoomedInLevel = this.zoomLevels[this.zoomLevels.length - 1];
         this.zoomedOutLevel = this.zoomLevels[0];

         for(int var3 = 0; var3 < this.zoomLevels.length; ++var3) {
            float var4 = (float)var1 * this.zoomLevels[var3];
            float var5 = (float)var2 * this.zoomLevels[var3];

            try {
               this.Current = this.createTexture(var4, var5, false);
               if (this.Current != null) {
                  break;
               }
            } catch (Exception var7) {
               var7.printStackTrace();
               DebugLog.log("Failed to create FBO w:" + var4 + " h:" + var5);
               this.bZoomEnabled = false;
            }
         }

      }
   }

   public void update() {
      int var1 = IsoPlayer.getPlayerIndex();
      if (!this.bZoomEnabled) {
         this.zoom[var1] = this.targetZoom[var1] = 1.0F;
      }

      if (this.Current == null) {
         this.setCameraToCentre();
      } else {
         float var2;
         if (this.bAutoZoom[IsoPlayer.getPlayerIndex()] && IsoCamera.CamCharacter != null && this.bZoomEnabled) {
            var2 = 1.0F;
            if (IsoCamera.CamCharacter.getCurrentSquare().getRoom() == null && ((!(IsoPlayer.instance.closestZombie < 6.0F) || !IsoPlayer.instance.isTargetedByZombie()) && !(IsoPlayer.instance.lastTargeted < (float)(PerformanceSettings.LockFPS * 4)) || IsoPlayer.instance.IsRunning())) {
               var2 = this.zoomedOutLevel;
            } else {
               var2 = this.zoomedInLevel;
            }

            float var3 = IsoUtils.DistanceTo(IsoCamera.RightClickX[IsoPlayer.assumedPlayer], IsoCamera.RightClickY[IsoPlayer.assumedPlayer], 0.0F, 0.0F);
            float var4 = var3 / 300.0F;
            if (var4 > 1.0F) {
               var4 = 1.0F;
            }

            var2 += var4;
            if (var2 > this.zoomLevels[0]) {
               var2 = this.zoomLevels[0];
            }

            if (IsoCamera.CamCharacter.getVehicle() != null) {
               var2 = 1.5F;
            }

            this.setTargetZoom(var1, var2);
         }

         var2 = 0.004F * GameTime.instance.getMultiplier() / GameTime.instance.getTrueMultiplier() * (Core.TileScale == 2 ? 1.5F : 1.0F);
         if (!this.bAutoZoom[IsoPlayer.getPlayerIndex()]) {
            var2 *= 5.0F;
         } else if (this.targetZoom[var1] > this.zoom[var1]) {
            var2 *= 1.0F;
         }

         float[] var10000;
         if (this.targetZoom[var1] > this.zoom[var1]) {
            var10000 = this.zoom;
            var10000[var1] += var2;
            IsoPlayer.players[var1].dirtyRecalcGridStackTime = 2.0F;
            if (this.zoom[var1] > this.targetZoom[var1] || Math.abs(this.zoom[var1] - this.targetZoom[var1]) < 0.001F) {
               this.zoom[var1] = this.targetZoom[var1];
            }
         }

         if (this.targetZoom[var1] < this.zoom[var1]) {
            var10000 = this.zoom;
            var10000[var1] -= var2;
            IsoPlayer.players[var1].dirtyRecalcGridStackTime = 2.0F;
            if (this.zoom[var1] < this.targetZoom[var1] || Math.abs(this.zoom[var1] - this.targetZoom[var1]) < 0.001F) {
               this.zoom[var1] = this.targetZoom[var1];
            }
         }

         this.setCameraToCentre();
      }
   }

   private void setCameraToCentre() {
      float var1 = IsoCamera.OffX[IsoPlayer.getPlayerIndex()];
      float var2 = IsoCamera.OffY[IsoPlayer.getPlayerIndex()];
      if (IsoCamera.CamCharacter != null) {
         IsoGameCharacter var3 = IsoCamera.CamCharacter;
         var1 = IsoUtils.XToScreen(var3.x + IsoCamera.DeferedX[IsoPlayer.getPlayerIndex()], var3.y + IsoCamera.DeferedY[IsoPlayer.getPlayerIndex()], var3.z, 0);
         var2 = IsoUtils.YToScreen(var3.x + IsoCamera.DeferedX[IsoPlayer.getPlayerIndex()], var3.y + IsoCamera.DeferedY[IsoPlayer.getPlayerIndex()], var3.z, 0);
         var1 -= (float)(IsoCamera.getOffscreenWidth(IsoPlayer.getPlayerIndex()) / 2);
         var2 -= (float)(IsoCamera.getOffscreenHeight(IsoPlayer.getPlayerIndex()) / 2);
         var2 -= var3.getOffsetY() * 1.5F;
         var1 = (float)((int)var1);
         var2 = (float)((int)var2);
         var1 += (float)IsoCamera.PLAYER_OFFSET_X;
         var2 += (float)IsoCamera.PLAYER_OFFSET_Y;
      }

      IsoCamera.OffX[IsoPlayer.getPlayerIndex()] = var1;
      IsoCamera.OffY[IsoPlayer.getPlayerIndex()] = var2;
      IsoCamera.TOffX[IsoPlayer.getPlayerIndex()] = var1;
      IsoCamera.TOffY[IsoPlayer.getPlayerIndex()] = var2;
   }

   private TextureFBO createTexture(float var1, float var2, boolean var3) throws Exception {
      Texture var4;
      TextureFBO var5;
      if (var3) {
         try {
            TextureID.bUseCompression = false;
            var4 = new Texture((int)var1, (int)var2);
            var5 = new TextureFBO(var4);
            var5.destroy();
         } finally {
            TextureID.bUseCompression = TextureID.bUseCompressionOption;
         }

         return null;
      } else {
         try {
            TextureID.bUseCompression = false;
            var4 = new Texture((int)var1, (int)var2);
            var5 = new TextureFBO(var4);
         } finally {
            TextureID.bUseCompression = TextureID.bUseCompressionOption;
         }

         return var5;
      }
   }

   public void render() {
      if (this.Current != null) {
         if (this.bZoomEnabled) {
            IndieGL.glBind((Texture)this.Current.getTexture());
         }

         for(int var1 = 0; var1 < IsoPlayer.numPlayers; ++var1) {
            if (Core.getInstance().RenderShader != null) {
               IndieGL.StartShader(Core.getInstance().RenderShader.getID(), var1);
            }

            int var2 = IsoCamera.getScreenLeft(var1);
            int var3 = IsoCamera.getScreenTop(var1);
            int var4 = IsoCamera.getScreenWidth(var1);
            int var5 = IsoCamera.getScreenHeight(var1);
            if (IsoPlayer.players[var1] != null || GameServer.bServer && ServerGUI.isCreated()) {
               int var6 = IsoCamera.getOffscreenLeft(var1);
               int var7 = IsoCamera.getOffscreenTop(var1);
               int var8 = IsoCamera.getOffscreenWidth(var1);
               int var9 = IsoCamera.getOffscreenHeight(var1);
               if (this.bZoomEnabled && this.zoom[var1] > 0.5F) {
                  IndieGL.glTexParameteri(3553, 10241, 9729);
               } else {
                  IndieGL.glTexParameteri(3553, 10241, 9728);
               }

               if (this.zoom[var1] == 0.5F) {
                  IndieGL.glTexParameteri(3553, 10240, 9728);
               } else {
                  IndieGL.glTexParameteri(3553, 10240, 9729);
               }

               ((Texture)this.Current.getTexture()).rendershader2(var2, var3, var4, var5, var6, var7, var8, var9, 1.0F, 1.0F, 1.0F, 1.0F);
            } else {
               SpriteRenderer.instance.render((Texture)null, var2, var3, var4, var5, 0.0F, 0.0F, 0.0F, 1.0F);
            }
         }

         if (Core.getInstance().RenderShader != null) {
            IndieGL.StartShader(0, 0);
         }

      }
   }

   public TextureFBO getCurrent(int var1) {
      return this.Current;
   }

   public Texture getTexture(int var1) {
      return (Texture)this.Current.getTexture();
   }

   public void updateMipMaps() {
      this.getTexture(0).dataid.generateMipmap();
   }

   public void doZoomScroll(int var1, int var2) {
      this.targetZoom[var1] = this.getNextZoom(var1, var2);
   }

   public float getNextZoom(int var1, int var2) {
      if (this.bZoomEnabled && this.zoomLevels != null) {
         int var3;
         if (var2 > 0) {
            for(var3 = this.zoomLevels.length - 1; var3 > 0; --var3) {
               if (this.targetZoom[var1] == this.zoomLevels[var3]) {
                  return this.zoomLevels[var3 - 1];
               }
            }
         } else if (var2 < 0) {
            for(var3 = 0; var3 < this.zoomLevels.length - 1; ++var3) {
               if (this.targetZoom[var1] == this.zoomLevels[var3]) {
                  return this.zoomLevels[var3 + 1];
               }
            }
         }

         return this.targetZoom[var1];
      } else {
         return 1.0F;
      }
   }

   public float getMinZoom() {
      return this.bZoomEnabled && this.zoomLevels != null && this.zoomLevels.length != 0 ? this.zoomLevels[this.zoomLevels.length - 1] : 1.0F;
   }

   public float getMaxZoom() {
      return this.bZoomEnabled && this.zoomLevels != null && this.zoomLevels.length != 0 ? this.zoomLevels[0] : 1.0F;
   }

   public boolean test() {
      try {
         this.createTexture(16.0F, 16.0F, true);
         return true;
      } catch (Exception var2) {
         var2.printStackTrace();
         DebugLog.log("Failed to create Test FBO");
         Core.SafeMode = true;
         return false;
      }
   }
}
