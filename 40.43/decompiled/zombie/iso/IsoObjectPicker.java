package zombie.iso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.input.Mouse;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.vehicles.BaseVehicle;

public class IsoObjectPicker {
   public static IsoObjectPicker Instance = new IsoObjectPicker();
   public IsoObjectPicker.ClickObject[] ClickObjectStore = new IsoObjectPicker.ClickObject[15000];
   public int count = 0;
   public int counter = 0;
   public int maxcount = 0;
   public ArrayList ThisFrame = new ArrayList();
   IsoObjectPicker.ClickObject LastPickObject = null;
   public boolean dirty = true;
   public float xOffSinceDirty = 0.0F;
   public float yOffSinceDirty = 0.0F;
   static ArrayList choices = new ArrayList();
   static Comparator comp = new Comparator() {
      public int compare(IsoObjectPicker.ClickObject var1, IsoObjectPicker.ClickObject var2) {
         int var3 = var1.getScore();
         int var4 = var2.getScore();
         if (var3 > var4) {
            return 1;
         } else if (var3 < var4) {
            return -1;
         } else {
            return var1.tile != null && var1.tile.square != null && var2.tile != null && var1.tile.square == var2.tile.square ? var1.tile.getObjectIndex() - var2.tile.getObjectIndex() : 0;
         }
      }
   };
   float lx = 0.0F;
   float ly = 0.0F;
   public boolean wasDirty = false;
   static Vector2 tempo = new Vector2();
   static Vector2 tempo2 = new Vector2();

   public IsoObjectPicker getInstance() {
      return Instance;
   }

   public void Add(int var1, int var2, int var3, int var4, IsoGridSquare var5, IsoObject var6, boolean var7, float var8, float var9) {
      if (!((float)(var1 + var3) <= this.lx - 32.0F) && !((float)var1 >= this.lx + 32.0F) && !((float)(var2 + var4) <= this.ly - 32.0F) && !((float)var2 >= this.ly + 32.0F)) {
         if (this.ThisFrame.size() < 15000) {
            if (!var6.NoPicking) {
               boolean var10;
               if (var6 instanceof IsoSurvivor) {
                  var10 = false;
               }

               if (var6 instanceof IsoDoor) {
                  var10 = false;
               }

               if (var1 <= Core.getInstance().getOffscreenWidth(0)) {
                  if (var2 <= Core.getInstance().getOffscreenHeight(0)) {
                     if (var1 + var3 >= 0) {
                        if (var2 + var4 >= 0) {
                           IsoObjectPicker.ClickObject var11 = this.ClickObjectStore[this.ThisFrame.size()];
                           this.ThisFrame.add(var11);
                           this.count = this.ThisFrame.size();
                           var11.x = var1;
                           var11.y = var2;
                           var11.width = var3;
                           var11.height = var4;
                           var11.square = var5;
                           var11.tile = var6;
                           var11.flip = var7;
                           var11.scaleX = var8;
                           var11.scaleY = var9;
                           if (var11.tile instanceof IsoGameCharacter) {
                              var11.flip = false;
                           }

                           if (this.count > this.maxcount) {
                              this.maxcount = this.count;
                           }

                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void Init() {
      this.ThisFrame.clear();
      this.LastPickObject = null;

      for(int var1 = 0; var1 < 15000; ++var1) {
         this.ClickObjectStore[var1] = new IsoObjectPicker.ClickObject();
      }

   }

   public IsoObjectPicker.ClickObject ContextPick(int var1, int var2) {
      float var3 = (float)var1 * Core.getInstance().getZoom(0);
      float var4 = (float)var2 * Core.getInstance().getZoom(0);
      choices.clear();
      ++this.counter;

      for(int var5 = this.ThisFrame.size() - 1; var5 >= 0; --var5) {
         IsoObjectPicker.ClickObject var6 = (IsoObjectPicker.ClickObject)this.ThisFrame.get(var5);
         if ((!(var6.tile instanceof IsoPlayer) || var6.tile != IsoPlayer.players[0]) && (var6.tile.sprite == null || var6.tile.targetAlpha[0] != 0.0F)) {
            if (var6.tile != null && var6.tile.sprite != null) {
            }

            if (var3 > (float)var6.x && var4 > (float)var6.y && var3 <= (float)(var6.x + var6.width) && var4 <= (float)(var6.y + var6.height)) {
               if (var6.tile instanceof IsoPlayer) {
                  if (var6.tile.sprite == null || var6.tile.sprite.def == null || var6.tile.sprite.CurrentAnim == null || var6.tile.sprite.CurrentAnim.Frames == null || var6.tile.sprite.def.Frame < 0.0F || var6.tile.sprite.def.Frame >= (float)var6.tile.sprite.CurrentAnim.Frames.size()) {
                     continue;
                  }

                  int var7 = (int)(var3 - (float)var6.x);
                  int var8 = (int)(var4 - (float)var6.y);
                  Texture var9 = ((IsoDirectionFrame)var6.tile.sprite.CurrentAnim.Frames.get((int)var6.tile.sprite.def.Frame)).directions[var6.tile.dir.index()];
                  int var10 = Core.TileScale;
                  if (var6.flip) {
                     var7 = (int)((float)var7 - ((float)(var9.getWidthOrig() - var9.getWidth()) - var9.offsetX));
                     var8 = (int)((float)var8 - var9.offsetY);
                     var7 = var9.getWidth() - var8;
                  } else {
                     var7 = (int)((float)var7 - var9.offsetX * (float)var10);
                     var8 = (int)((float)var8 - var9.offsetY * (float)var10);
                  }

                  if (var7 >= 0 && var8 >= 0 && var7 <= var9.getWidth() * var10 && var8 <= var9.getHeight() * var10) {
                     var6.lx = (int)var3 - var6.x;
                     var6.ly = (int)var4 - var6.y;
                     this.LastPickObject = var6;
                     choices.clear();
                     choices.add(var6);
                     break;
                  }
               }

               if (var6.scaleX == 1.0F && var6.scaleY == 1.0F) {
                  if (var6.tile.isMaskClicked((int)(var3 - (float)var6.x), (int)(var4 - (float)var6.y), var6.flip)) {
                     if (var6.tile.rerouteMask != null) {
                        var6.tile = var6.tile.rerouteMask;
                     }

                     var6.lx = (int)var3 - var6.x;
                     var6.ly = (int)var4 - var6.y;
                     this.LastPickObject = var6;
                     choices.add(var6);
                  }
               } else {
                  float var12 = (float)var6.x + (var3 - (float)var6.x) / var6.scaleX;
                  float var13 = (float)var6.y + (var4 - (float)var6.y) / var6.scaleY;
                  if (var6.tile.isMaskClicked((int)(var12 - (float)var6.x), (int)(var13 - (float)var6.y), var6.flip)) {
                     if (var6.tile.rerouteMask != null) {
                        var6.tile = var6.tile.rerouteMask;
                     }

                     var6.lx = (int)var3 - var6.x;
                     var6.ly = (int)var4 - var6.y;
                     this.LastPickObject = var6;
                     choices.add(var6);
                  }
               }
            }
         }
      }

      if (choices.isEmpty()) {
         return null;
      } else {
         Collections.sort(choices, comp);
         IsoObjectPicker.ClickObject var11 = (IsoObjectPicker.ClickObject)choices.get(choices.size() - 1);
         return var11;
      }
   }

   public IsoObjectPicker.ClickObject Pick(int var1, int var2) {
      float var3 = (float)var1;
      float var4 = (float)var2;
      float var5 = (float)Core.getInstance().getScreenWidth();
      float var6 = (float)Core.getInstance().getScreenHeight();
      float var7 = var5 * Core.getInstance().getZoom(0);
      float var8 = var6 * Core.getInstance().getZoom(0);
      float var9 = (float)Core.getInstance().OffscreenBuffer.getWidth(0);
      float var10 = (float)Core.getInstance().OffscreenBuffer.getHeight(0);
      float var11 = var9 / var7;
      float var12 = var10 / var8;
      var3 -= var5 / 2.0F;
      var4 -= var6 / 2.0F;
      var3 /= var11;
      var4 /= var12;
      var3 += var5 / 2.0F;
      var4 += var6 / 2.0F;
      ++this.counter;

      for(int var13 = this.ThisFrame.size() - 1; var13 >= 0; --var13) {
         IsoObjectPicker.ClickObject var14 = (IsoObjectPicker.ClickObject)this.ThisFrame.get(var13);
         if (var14.tile.square != null) {
         }

         if (!(var14.tile instanceof IsoPlayer) && (var14.tile.sprite == null || var14.tile.targetAlpha[0] != 0.0F)) {
            if (var14.tile != null && var14.tile.sprite != null) {
            }

            if (var3 > (float)var14.x && var4 > (float)var14.y && var3 <= (float)(var14.x + var14.width) && var4 <= (float)(var14.y + var14.height)) {
               if (var14.tile instanceof IsoSurvivor) {
                  boolean var15 = false;
               } else if (var14.tile.isMaskClicked((int)(var3 - (float)var14.x), (int)(var4 - (float)var14.y), var14.flip)) {
                  if (var14.tile.rerouteMask != null) {
                     var14.tile = var14.tile.rerouteMask;
                  }

                  var14.lx = (int)var3 - var14.x;
                  var14.ly = (int)var4 - var14.y;
                  this.LastPickObject = var14;
                  return var14;
               }
            }
         }
      }

      return null;
   }

   public void StartRender() {
      float var1 = (float)Mouse.getX();
      float var2 = (float)Mouse.getY();
      if (var1 != this.lx || var2 != this.ly) {
         this.dirty = true;
      }

      this.lx = var1;
      this.ly = var2;
      if (this.dirty) {
         this.ThisFrame.clear();
         this.count = 0;
         this.wasDirty = true;
         this.dirty = false;
         this.xOffSinceDirty = 0.0F;
         this.yOffSinceDirty = 0.0F;
      } else {
         this.wasDirty = false;
      }

   }

   public IsoMovingObject PickTarget(int var1, int var2) {
      float var3 = (float)var1;
      float var4 = (float)var2;
      float var5 = (float)Core.getInstance().getScreenWidth();
      float var6 = (float)Core.getInstance().getScreenHeight();
      float var7 = var5 * Core.getInstance().getZoom(0);
      float var8 = var6 * Core.getInstance().getZoom(0);
      float var9 = (float)Core.getInstance().OffscreenBuffer.getWidth(0);
      float var10 = (float)Core.getInstance().OffscreenBuffer.getHeight(0);
      float var11 = var9 / var7;
      float var12 = var10 / var8;
      var3 -= var5 / 2.0F;
      var4 -= var6 / 2.0F;
      var3 /= var11;
      var4 /= var12;
      var3 += var5 / 2.0F;
      var4 += var6 / 2.0F;
      ++this.counter;

      for(int var13 = this.ThisFrame.size() - 1; var13 >= 0; --var13) {
         IsoObjectPicker.ClickObject var14 = (IsoObjectPicker.ClickObject)this.ThisFrame.get(var13);
         if (var14.tile.square != null) {
         }

         if (var14.tile != IsoPlayer.instance && (var14.tile.sprite == null || var14.tile.targetAlpha[IsoPlayer.getPlayerIndex()] != 0.0F)) {
            if (var14.tile != null && var14.tile.sprite != null) {
            }

            if (var3 > (float)var14.x && var4 > (float)var14.y && var3 <= (float)(var14.x + var14.width) && var4 <= (float)(var14.y + var14.height) && var14.tile instanceof IsoMovingObject && var14.tile.isMaskClicked((int)(var3 - (float)var14.x), (int)(var4 - (float)var14.y), var14.flip)) {
               if (var14.tile.rerouteMask != null) {
               }

               var14.lx = (int)(var3 - (float)var14.x);
               var14.ly = (int)(var4 - (float)var14.y);
               this.LastPickObject = var14;
               return (IsoMovingObject)var14.tile;
            }
         }
      }

      return null;
   }

   public boolean IsHeadShot(IsoMovingObject var1, int var2, int var3) {
      if (var1 instanceof IsoSurvivor) {
         return true;
      } else {
         for(int var4 = this.ThisFrame.size() - 1; var4 >= 0; --var4) {
            IsoObjectPicker.ClickObject var5 = (IsoObjectPicker.ClickObject)this.ThisFrame.get(var4);
            if (var5.tile == var1 && var5.tile.getMaskClickedY(var2 - var5.x, var3 - var5.y, var5.flip) < 15.0F) {
               return true;
            }
         }

         return false;
      }
   }

   public IsoObject PickWindow(int var1, int var2) {
      float var3 = (float)var1 * Core.getInstance().getZoom(0);
      float var4 = (float)var2 * Core.getInstance().getZoom(0);

      for(int var5 = this.ThisFrame.size() - 1; var5 >= 0; --var5) {
         IsoObjectPicker.ClickObject var6 = (IsoObjectPicker.ClickObject)this.ThisFrame.get(var5);
         if ((var6.tile instanceof IsoWindow || var6.tile instanceof IsoCurtain) && (var6.tile.sprite == null || var6.tile.targetAlpha[IsoPlayer.getPlayerIndex()] != 0.0F) && var3 >= (float)var6.x && var4 >= (float)var6.y && var3 < (float)(var6.x + var6.width) && var4 < (float)(var6.y + var6.height)) {
            int var7 = (int)(var3 - (float)var6.x);
            int var8 = (int)(var4 - (float)var6.y);
            if (var6.tile.isMaskClicked(var7, var8, var6.flip)) {
               return var6.tile;
            }

            if (var6.tile instanceof IsoWindow) {
               boolean var9 = false;
               boolean var10 = false;

               int var11;
               for(var11 = var8; var11 >= 0; --var11) {
                  if (var6.tile.isMaskClicked(var7, var11)) {
                     var9 = true;
                     break;
                  }
               }

               for(var11 = var8; var11 < var6.height; ++var11) {
                  if (var6.tile.isMaskClicked(var7, var11)) {
                     var10 = true;
                     break;
                  }
               }

               if (var9 && var10) {
                  return var6.tile;
               }
            }
         }
      }

      return null;
   }

   public IsoObject PickWindowFrame(int var1, int var2) {
      float var3 = (float)var1 * Core.getInstance().getZoom(0);
      float var4 = (float)var2 * Core.getInstance().getZoom(0);

      for(int var5 = this.ThisFrame.size() - 1; var5 >= 0; --var5) {
         IsoObjectPicker.ClickObject var6 = (IsoObjectPicker.ClickObject)this.ThisFrame.get(var5);
         if (IsoWindowFrame.isWindowFrame(var6.tile) && (var6.tile.sprite == null || var6.tile.targetAlpha[IsoPlayer.getPlayerIndex()] != 0.0F) && var3 >= (float)var6.x && var4 >= (float)var6.y && var3 < (float)(var6.x + var6.width) && var4 < (float)(var6.y + var6.height)) {
            int var7 = (int)(var3 - (float)var6.x);
            int var8 = (int)(var4 - (float)var6.y);
            if (var6.tile.isMaskClicked(var7, var8, var6.flip)) {
               return var6.tile;
            }

            boolean var9 = false;
            boolean var10 = false;

            int var11;
            for(var11 = var8; var11 >= 0; --var11) {
               if (var6.tile.isMaskClicked(var7, var11)) {
                  var9 = true;
                  break;
               }
            }

            for(var11 = var8; var11 < var6.height; ++var11) {
               if (var6.tile.isMaskClicked(var7, var11)) {
                  var10 = true;
                  break;
               }
            }

            if (var9 && var10) {
               return var6.tile;
            }
         }
      }

      return null;
   }

   public IsoObject PickThumpable(int var1, int var2) {
      float var3 = (float)var1 * Core.getInstance().getZoom(0);
      float var4 = (float)var2 * Core.getInstance().getZoom(0);

      for(int var5 = this.ThisFrame.size() - 1; var5 >= 0; --var5) {
         IsoObjectPicker.ClickObject var6 = (IsoObjectPicker.ClickObject)this.ThisFrame.get(var5);
         if (var6.tile instanceof IsoThumpable) {
            IsoThumpable var7 = (IsoThumpable)var6.tile;
            if ((var6.tile.sprite == null || var6.tile.targetAlpha[IsoPlayer.getPlayerIndex()] != 0.0F) && var3 >= (float)var6.x && var4 >= (float)var6.y && var3 < (float)(var6.x + var6.width) && var4 < (float)(var6.y + var6.height)) {
               int var8 = (int)(var3 - (float)var6.x);
               int var9 = (int)(var4 - (float)var6.y);
               if (var6.tile.isMaskClicked(var8, var9, var6.flip)) {
                  return var6.tile;
               }
            }
         }
      }

      return null;
   }

   public IsoObject PickCorpse(int var1, int var2) {
      float var3 = (float)var1 * Core.getInstance().getZoom(0);
      float var4 = (float)var2 * Core.getInstance().getZoom(0);

      for(int var5 = this.ThisFrame.size() - 1; var5 >= 0; --var5) {
         IsoObjectPicker.ClickObject var6 = (IsoObjectPicker.ClickObject)this.ThisFrame.get(var5);
         if (var3 >= (float)var6.x && var4 >= (float)var6.y && var3 < (float)(var6.x + var6.width) && var4 < (float)(var6.y + var6.height) && !(var6.tile.targetAlpha[IsoPlayer.getPlayerIndex()] < 1.0F)) {
            if (var6.tile.isMaskClicked((int)(var3 - (float)var6.x), (int)(var4 - (float)var6.y), var6.flip) && !(var6.tile instanceof IsoWindow)) {
               return null;
            }

            if (var6.tile instanceof IsoDeadBody && var6.tile.sprite != null && var6.tile.sprite.def != null && var6.tile.sprite.CurrentAnim != null && var6.tile.sprite.CurrentAnim.Frames != null && !(var6.tile.sprite.def.Frame >= (float)var6.tile.sprite.CurrentAnim.Frames.size())) {
               Texture var7 = ((IsoDirectionFrame)var6.tile.sprite.CurrentAnim.Frames.get((int)var6.tile.sprite.def.Frame)).getTexture(var6.tile.dir);
               if (var7 != null) {
                  int var8 = Core.TileScale;
                  if (var3 >= (float)var6.x + var7.offsetX * (float)var8 && var3 < (float)var6.x + (var7.offsetX + (float)var7.getWidth()) * (float)var8 && var4 >= (float)var6.y + var7.offsetY * (float)var8 && var4 < (float)var6.y + (var7.offsetY + (float)var7.getHeight()) * (float)var8) {
                     return var6.tile;
                  }
               }
            }
         }
      }

      return null;
   }

   public IsoObject PickTree(int var1, int var2) {
      float var3 = (float)var1 * Core.getInstance().getZoom(0);
      float var4 = (float)var2 * Core.getInstance().getZoom(0);

      for(int var5 = this.ThisFrame.size() - 1; var5 >= 0; --var5) {
         IsoObjectPicker.ClickObject var6 = (IsoObjectPicker.ClickObject)this.ThisFrame.get(var5);
         if (var6.tile instanceof IsoTree && (var6.tile.sprite == null || var6.tile.targetAlpha[IsoPlayer.getPlayerIndex()] != 0.0F) && var3 >= (float)var6.x && var4 >= (float)var6.y && var3 < (float)(var6.x + var6.width) && var4 < (float)(var6.y + var6.height)) {
            int var7 = (int)(var3 - (float)var6.x);
            int var8 = (int)(var4 - (float)var6.y);
            if (var6.tile.isMaskClicked(var7, var8, var6.flip)) {
               return var6.tile;
            }
         }
      }

      return null;
   }

   public BaseVehicle PickVehicle(int var1, int var2) {
      float var3 = IsoUtils.XToIso((float)var1, (float)var2, 0.0F);
      float var4 = IsoUtils.YToIso((float)var1, (float)var2, 0.0F);

      for(int var5 = 0; var5 < IsoWorld.instance.CurrentCell.getVehicles().size(); ++var5) {
         BaseVehicle var6 = (BaseVehicle)IsoWorld.instance.CurrentCell.getVehicles().get(var5);
         if (var6.isInBounds(var3, var4)) {
            return var6;
         }
      }

      return null;
   }

   public class ClickObject {
      public int height;
      public IsoGridSquare square;
      public IsoObject tile;
      public int width;
      public int x;
      public int y;
      private boolean flip;
      public int lx;
      public int ly;
      public float scaleX;
      public float scaleY;

      public int getScore() {
         float var1 = 1.0F;
         IsoObjectPicker.tempo.x = (float)this.square.getX() + 0.5F;
         IsoObjectPicker.tempo.y = (float)this.square.getY() + 0.5F;
         Vector2 var10000 = IsoObjectPicker.tempo;
         var10000.x -= IsoPlayer.instance.getX();
         var10000 = IsoObjectPicker.tempo;
         var10000.y -= IsoPlayer.instance.getY();
         IsoObjectPicker.tempo.normalize();
         Vector2 var2 = IsoPlayer.instance.getVectorFromDirection(IsoObjectPicker.tempo2);
         float var3 = var2.dot(IsoObjectPicker.tempo);
         var1 += Math.abs(var3 * 4.0F);
         if (this.tile instanceof IsoDoor || this.tile instanceof IsoThumpable && ((IsoThumpable)this.tile).isDoor()) {
            var1 += 6.0F;
            if (IsoPlayer.instance.getZ() > (float)this.square.getZ()) {
               var1 -= 1000.0F;
            }
         } else if (this.tile instanceof IsoWindow) {
            var1 += 4.0F;
            if (IsoPlayer.instance.getZ() > (float)this.square.getZ()) {
               var1 -= 1000.0F;
            }
         } else {
            if (IsoPlayer.instance.getCurrentSquare() != null && this.square.getRoom() == IsoPlayer.instance.getCurrentSquare().getRoom()) {
               ++var1;
            } else {
               var1 -= 100000.0F;
            }

            if (IsoPlayer.instance.getZ() > (float)this.square.getZ()) {
               var1 -= 1000.0F;
            }

            if (this.tile instanceof IsoPlayer) {
               var1 -= 100000.0F;
            } else if (this.tile instanceof IsoThumpable && this.tile.targetAlpha[IsoPlayer.getPlayerIndex()] < 0.99F) {
               var1 -= 100000.0F;
            }

            if (this.tile instanceof IsoCurtain) {
               var1 += 3.0F;
            } else if (this.tile instanceof IsoLightSwitch) {
               var1 += 20.0F;
            } else if (this.tile.sprite.Properties.Is(IsoFlagType.bed)) {
               var1 += 2.0F;
            } else if (this.tile.container != null) {
               var1 += 10.0F;
            } else if (this.tile instanceof IsoWaveSignal) {
               var1 += 20.0F;
            } else if (this.tile instanceof IsoThumpable && ((IsoThumpable)this.tile).getLightSource() != null) {
               var1 += 3.0F;
            } else if (this.tile.sprite.Properties.Is(IsoFlagType.waterPiped)) {
               var1 += 3.0F;
            } else if (this.tile.sprite.Properties.Is(IsoFlagType.solidfloor)) {
               var1 -= 100.0F;
            } else if (this.tile.sprite.getType() == IsoObjectType.WestRoofB) {
               var1 -= 100.0F;
            } else if (this.tile.sprite.getType() == IsoObjectType.WestRoofM) {
               var1 -= 100.0F;
            } else if (this.tile.sprite.getType() == IsoObjectType.WestRoofT) {
               var1 -= 100.0F;
            } else if (this.tile.sprite.Properties.Is(IsoFlagType.cutW) || this.tile.sprite.Properties.Is(IsoFlagType.cutN)) {
               var1 -= 2.0F;
            }
         }

         float var4 = IsoUtils.DistanceManhatten((float)this.square.getX() + 0.5F, (float)this.square.getY() + 0.5F, IsoPlayer.instance.getX(), IsoPlayer.instance.getY());
         var1 -= var4 / 2.0F;
         return (int)var1;
      }
   }
}
