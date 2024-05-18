package zombie.iso.objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.Util;
import zombie.GameTime;
import zombie.IndieGL;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.IndieFileLoader;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.textures.ColorInfo;
import zombie.debug.DebugLog;
import zombie.inventory.types.HandWeapon;
import zombie.iso.CellLoader;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.LosUtil;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;

public class IsoTree extends IsoObject {
   public static final int MAX_SIZE = 6;
   public int LogYield = 1;
   public int damage = 500;
   public int size = 4;
   public boolean bRenderFlag;
   public float fadeAlpha;

   public IsoTree(IsoCell var1) {
      super(var1);
   }

   public void save(ByteBuffer var1) throws IOException {
      super.save(var1);
      var1.putInt(this.LogYield);
      var1.putInt(this.damage);
   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      super.load(var1, var2);
      this.LogYield = var1.getInt();
      this.damage = var1.getInt();
      if (this.sprite != null && this.sprite.getProperties().Val("tree") != null) {
         this.size = Integer.parseInt(this.sprite.getProperties().Val("tree"));
         if (this.size < 1) {
            this.size = 1;
         }

         if (this.size > 6) {
            this.size = 6;
         }
      }

   }

   protected void checkMoveWithWind() {
      this.checkMoveWithWind(true);
   }

   public void reset() {
      super.reset();
   }

   public IsoTree(IsoGridSquare var1, String var2) {
      super(var1, var2, false);
      this.initTree();
   }

   public IsoTree(IsoGridSquare var1, IsoSprite var2) {
      super(var1.getCell(), var1, var2);
      this.initTree();
   }

   public void initTree() {
      this.setType(IsoObjectType.tree);
      if (this.sprite.getProperties().Val("tree") != null) {
         this.size = Integer.parseInt(this.sprite.getProperties().Val("tree"));
         if (this.size < 1) {
            this.size = 1;
         }

         if (this.size > 6) {
            this.size = 6;
         }
      } else {
         this.size = 4;
      }

      switch(this.size) {
      case 1:
      case 2:
         this.LogYield = 1;
         break;
      case 3:
      case 4:
         this.LogYield = 2;
         break;
      case 5:
         this.LogYield = 3;
         break;
      case 6:
         this.LogYield = 4;
      }

      this.damage = this.LogYield * 80;
   }

   public String getObjectName() {
      return "Tree";
   }

   public void Damage(float var1) {
      WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
      float var2 = var1 * 0.05F;
      this.damage = (int)((float)this.damage - var2);
      if (this.damage <= 0) {
         this.square.transmitRemoveItemFromSquare(this);
         this.square.RecalcAllWithNeighbours(true);
         int var3 = this.LogYield;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            this.square.AddWorldInventoryItem("Base.Log", 0.0F, 0.0F, 0.0F);
            if (Rand.Next(4) == 0) {
               this.square.AddWorldInventoryItem("Base.TreeBranch", 0.0F, 0.0F, 0.0F);
            }

            if (Rand.Next(4) == 0) {
               this.square.AddWorldInventoryItem("Base.Twigs", 0.0F, 0.0F, 0.0F);
            }
         }

         if (GameClient.bClient) {
            this.square.clientModify();
         }

         this.reset();
         CellLoader.isoTreeCache.add(this);

         for(var4 = 0; var4 < IsoPlayer.numPlayers; ++var4) {
            LosUtil.cachecleared[var4] = true;
         }

         IsoGridSquare.setRecalcLightTime(-1);
         GameTime.instance.lightSourceUpdate = 100.0F;
         LuaEventManager.triggerEvent("OnContainerUpdate");
      }

   }

   public void WeaponHit(IsoGameCharacter var1, HandWeapon var2) {
      var1.getEmitter().playSound("ChopTree");
      WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
      this.setRenderEffect(RenderEffectType.Hit_Tree_Shudder, true);
      float var3 = (float)var2.getTreeDamage();
      if (var1.HasTrait("Axeman") && var2.getCategories().contains("Axe")) {
         var3 *= 1.5F;
      }

      this.damage = (int)((float)this.damage - var3);
      if (this.damage <= 0) {
         this.square.transmitRemoveItemFromSquare(this);
         var1.getEmitter().playSound("FallingTree");
         this.square.RecalcAllWithNeighbours(true);
         int var4 = this.LogYield;

         int var5;
         for(var5 = 0; var5 < var4; ++var5) {
            this.square.AddWorldInventoryItem("Base.Log", 0.0F, 0.0F, 0.0F);
            if (Rand.Next(4) == 0) {
               this.square.AddWorldInventoryItem("Base.TreeBranch", 0.0F, 0.0F, 0.0F);
            }

            if (Rand.Next(4) == 0) {
               this.square.AddWorldInventoryItem("Base.Twigs", 0.0F, 0.0F, 0.0F);
            }
         }

         if (GameClient.bClient) {
            this.square.clientModify();
         }

         this.reset();
         CellLoader.isoTreeCache.add(this);

         for(var5 = 0; var5 < IsoPlayer.numPlayers; ++var5) {
            LosUtil.cachecleared[var5] = true;
         }

         IsoGridSquare.setRecalcLightTime(-1);
         GameTime.instance.lightSourceUpdate = 100.0F;
         LuaEventManager.triggerEvent("OnContainerUpdate");
      }

      LuaEventManager.triggerEvent("OnWeaponHitTree", var1, var2);
   }

   public void setHealth(int var1) {
      this.damage = Math.max(var1, 0);
   }

   public int getHealth() {
      return this.damage;
   }

   public int getMaxHealth() {
      return this.LogYield * 80;
   }

   public int getSize() {
      return this.size;
   }

   public float getSlowFactor(IsoMovingObject var1) {
      float var2 = 1.0F;
      if (var1 instanceof IsoGameCharacter) {
         if ("parkranger".equals(((IsoGameCharacter)var1).getDescriptor().getProfession())) {
            var2 = 1.5F;
         }

         if ("lumberjack".equals(((IsoGameCharacter)var1).getDescriptor().getProfession())) {
            var2 = 1.2F;
         }
      }

      if (this.size != 1 && this.size != 2) {
         return this.size != 3 && this.size != 4 ? 0.3F * var2 : 0.5F * var2;
      } else {
         return 0.8F * var2;
      }
   }

   public void render(float var1, float var2, float var3, ColorInfo var4, boolean var5) {
      int var6 = IsoCamera.frameState.playerIndex;
      if (!this.bRenderFlag && !(this.fadeAlpha < this.targetAlpha[var6])) {
         this.renderInner(var1, var2, var3, var4, var5);
      } else {
         IndieGL.enableStencilTest();
         IndieGL.glStencilFunc(517, 128, 128);
         this.renderInner(var1, var2, var3, var4, var5);
         if (this.bRenderFlag && this.fadeAlpha > 0.25F) {
            this.fadeAlpha -= IsoObject.alphaStep;
            if (this.fadeAlpha < 0.25F) {
               this.fadeAlpha = 0.25F;
            }
         }

         float var7;
         if (!this.bRenderFlag) {
            var7 = this.targetAlpha[var6];
            if (this.fadeAlpha < var7) {
               this.fadeAlpha += IsoObject.alphaStep;
               if (this.fadeAlpha > var7) {
                  this.fadeAlpha = var7;
               }
            }
         }

         var7 = this.alpha[var6];
         this.alpha[0] = this.fadeAlpha;
         IndieGL.glStencilFunc(514, 128, 128);
         this.renderInner(var1, var2, var3, var4, true);
         this.alpha[0] = var7;
         if (IsoTree.TreeShader.instance.ShaderID == -1) {
            RenderThread.borrowContext();

            try {
               IsoTree.TreeShader.instance.initShader();
            } finally {
               RenderThread.returnContext();
            }
         }

         if (IsoTree.TreeShader.instance.ShaderID > 0) {
            IndieGL.StartShader(IsoTree.TreeShader.instance.ShaderID, 0);
            SpriteRenderer.instance.ShaderUpdate(IsoTree.TreeShader.instance.ShaderID, IsoTree.TreeShader.instance.outlineAlpha, 1.0F - this.fadeAlpha);
            this.renderInner(var1, var2, var3, var4, true);
            IndieGL.StartShader(0, 0);
         }

         IndieGL.glStencilFunc(519, 255, 255);
      }

   }

   private void renderInner(float var1, float var2, float var3, ColorInfo var4, boolean var5) {
      float var6;
      float var7;
      if (this.sprite != null && this.sprite.name != null && this.sprite.name.contains("JUMBO")) {
         var6 = this.offsetX;
         var7 = this.offsetY;
         this.offsetX = (float)(384 * Core.TileScale / 2 - 96 * Core.TileScale);
         this.offsetY = (float)(256 * Core.TileScale - 32 * Core.TileScale);
         if (this.offsetX != var6 || this.offsetY != var7) {
            this.sx = 0;
         }
      } else {
         var6 = this.offsetX;
         var7 = this.offsetY;
         this.offsetX = (float)(32 * Core.TileScale);
         this.offsetY = (float)(96 * Core.TileScale);
         if (this.offsetX != var6 || this.offsetY != var7) {
            this.sx = 0;
         }
      }

      super.render(var1, var2, var3, var4, false);
      if (this.AttachedAnimSprite != null) {
         int var10 = this.AttachedAnimSprite.size();

         for(int var11 = 0; var11 < var10; ++var11) {
            IsoSpriteInstance var8 = (IsoSpriteInstance)this.AttachedAnimSprite.get(var11);
            float var9 = this.targetAlpha[IsoCamera.frameState.playerIndex];
            this.targetAlpha[IsoCamera.frameState.playerIndex] = 1.0F;
            var8.render(this, var1, var2, var3, this.dir, this.offsetX, this.offsetY, var4);
            this.targetAlpha[IsoCamera.frameState.playerIndex] = var9;
            var8.update();
         }
      }

   }

   public void setSprite(IsoSprite var1) {
      super.setSprite(var1);
      this.initTree();
   }

   public boolean isMaskClicked(int var1, int var2, boolean var3) {
      if (super.isMaskClicked(var1, var2, var3)) {
         return true;
      } else if (this.AttachedAnimSpriteActual == null) {
         return false;
      } else {
         for(int var4 = 0; var4 < this.AttachedAnimSpriteActual.size(); ++var4) {
            if (((IsoSprite)this.AttachedAnimSpriteActual.get(var4)).isMaskClicked(this.dir, var1, var2, var3)) {
               return true;
            }
         }

         return false;
      }
   }

   private static class TreeShader {
      public static final IsoTree.TreeShader instance = new IsoTree.TreeShader();
      private int ShaderID = -1;
      private int FragID = 0;
      private int VertID = 0;
      private int stepSize;
      private int outlineAlpha;

      private void initShader() {
         this.ShaderID = ARBShaderObjects.glCreateProgramObjectARB();
         if (this.ShaderID != 0) {
            this.FragID = this.createFragShader("media/shaders/outline.frag");
            this.VertID = this.createVertShader("media/shaders/outline.vert");
            if (this.VertID != 0 && this.FragID != 0) {
               ARBShaderObjects.glAttachObjectARB(this.ShaderID, this.VertID);
               ARBShaderObjects.glAttachObjectARB(this.ShaderID, this.FragID);
               ARBShaderObjects.glLinkProgramARB(this.ShaderID);
               ARBShaderObjects.glValidateProgramARB(this.ShaderID);
               if (ARBShaderObjects.glGetObjectParameteriARB(this.ShaderID, 35715) == 0) {
                  DebugLog.log(getLogInfo(this.ShaderID));
                  this.VertID = 0;
                  this.ShaderID = 0;
                  this.FragID = 0;
                  return;
               }

               this.stepSize = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "stepSize");
               this.outlineAlpha = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "outlineAlpha");
               ARBShaderObjects.glUseProgramObjectARB(this.ShaderID);
               ARBShaderObjects.glUniform2fARB(this.stepSize, 0.001F, 0.001F);
               ARBShaderObjects.glUseProgramObjectARB(0);
            } else {
               ARBShaderObjects.glDeleteObjectARB(this.ShaderID);
               this.ShaderID = 0;
            }
         }

         Util.checkGLError();
      }

      private int createVertShader(String var1) {
         int var2 = ARBShaderObjects.glCreateShaderObjectARB(35633);
         if (var2 == 0) {
            return 0;
         } else {
            String var3 = "";

            try {
               InputStreamReader var4 = IndieFileLoader.getStreamReader(var1, false);
               Throwable var5 = null;

               try {
                  BufferedReader var6 = new BufferedReader(var4);
                  Throwable var7 = null;

                  try {
                     String var8;
                     try {
                        while((var8 = var6.readLine()) != null) {
                           var3 = var3 + var8.trim() + System.lineSeparator();
                        }
                     } catch (Throwable var32) {
                        var7 = var32;
                        throw var32;
                     }
                  } finally {
                     if (var6 != null) {
                        if (var7 != null) {
                           try {
                              var6.close();
                           } catch (Throwable var31) {
                              var7.addSuppressed(var31);
                           }
                        } else {
                           var6.close();
                        }
                     }

                  }
               } catch (Throwable var34) {
                  var5 = var34;
                  throw var34;
               } finally {
                  if (var4 != null) {
                     if (var5 != null) {
                        try {
                           var4.close();
                        } catch (Throwable var30) {
                           var5.addSuppressed(var30);
                        }
                     } else {
                        var4.close();
                     }
                  }

               }
            } catch (Exception var36) {
               DebugLog.log("Fail reading vertex shading code");
               return 0;
            }

            while(var3.indexOf("#") != 0) {
               var3 = var3.substring(1);
            }

            ARBShaderObjects.glShaderSourceARB(var2, var3);
            ARBShaderObjects.glCompileShaderARB(var2);
            if (!this.printLogInfo("vertex shader", var2)) {
               var2 = 0;
            }

            return var2;
         }
      }

      private int createFragShader(String var1) {
         int var2 = ARBShaderObjects.glCreateShaderObjectARB(35632);
         if (var2 == 0) {
            return 0;
         } else {
            String var3 = "";

            try {
               InputStreamReader var4 = IndieFileLoader.getStreamReader(var1, false);
               Throwable var5 = null;

               try {
                  BufferedReader var6 = new BufferedReader(var4);
                  Throwable var7 = null;

                  try {
                     String var8;
                     try {
                        while((var8 = var6.readLine()) != null) {
                           var3 = var3 + var8.trim() + System.lineSeparator();
                        }
                     } catch (Throwable var32) {
                        var7 = var32;
                        throw var32;
                     }
                  } finally {
                     if (var6 != null) {
                        if (var7 != null) {
                           try {
                              var6.close();
                           } catch (Throwable var31) {
                              var7.addSuppressed(var31);
                           }
                        } else {
                           var6.close();
                        }
                     }

                  }
               } catch (Throwable var34) {
                  var5 = var34;
                  throw var34;
               } finally {
                  if (var4 != null) {
                     if (var5 != null) {
                        try {
                           var4.close();
                        } catch (Throwable var30) {
                           var5.addSuppressed(var30);
                        }
                     } else {
                        var4.close();
                     }
                  }

               }
            } catch (Exception var36) {
               DebugLog.log("Fail reading fragment shading code");
               return 0;
            }

            while(var3.indexOf("#") != 0) {
               var3 = var3.substring(1);
            }

            ARBShaderObjects.glShaderSourceARB(var2, var3);
            ARBShaderObjects.glCompileShaderARB(var2);
            if (!this.printLogInfo("fragment shader", var2)) {
               var2 = 0;
            }

            return var2;
         }
      }

      private boolean printLogInfo(String var1, int var2) {
         IntBuffer var3 = BufferUtils.createIntBuffer(1);
         ARBShaderObjects.glGetObjectParameterARB(var2, 35716, var3);
         int var4 = var3.get();
         if (var4 > 1) {
            ByteBuffer var5 = BufferUtils.createByteBuffer(var4);
            var3.flip();
            ARBShaderObjects.glGetInfoLogARB(var2, var3, var5);
            byte[] var6 = new byte[var4];
            var5.get(var6);
            String var7 = new String(var6);
            DebugLog.log("Info log (" + var1 + "):\n" + var7 + "-----");
         }

         return true;
      }

      private static String getLogInfo(int var0) {
         return ARBShaderObjects.glGetInfoLogARB(var0, ARBShaderObjects.glGetObjectParameteriARB(var0, 35716));
      }
   }
}
