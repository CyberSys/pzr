package zombie.iso.sprite;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.GameApplet;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.properties.PropertyContainer;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Mask;
import zombie.core.textures.PaletteManager;
import zombie.core.textures.Texture;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.iso.IsoCamera;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.WorldConverter;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.ui.UIManager;

public class IsoSprite {
   public static int maxCount = 0;
   public int firerequirement;
   public String burntTile;
   public boolean forceAmbient;
   public boolean solidfloor;
   public boolean canBeRemoved;
   public boolean attachedFloor;
   public boolean cutW;
   public boolean cutN;
   public boolean solid;
   public boolean solidTrans;
   public boolean invisible;
   public boolean alwaysDraw;
   public boolean moveWithWind = false;
   public boolean isBush = false;
   public int windType = 1;
   public static float alphaStep = 0.05F;
   public boolean Animate = true;
   public boolean AnimateWhenPaused = false;
   public IsoAnim CurrentAnim = null;
   public boolean DeleteWhenFinished = false;
   public short sprOffX = 0;
   public boolean Loop = true;
   public short soffX = 0;
   public short soffY = 0;
   public PropertyContainer Properties = new PropertyContainer();
   public ColorInfo TintMod = new ColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
   public HashMap AnimMap = new HashMap(2);
   public ArrayList AnimStack = new ArrayList(1);
   float lx = -1.0F;
   float ly = -1.0F;
   float lz = -1.0F;
   IsoSpriteManager parentManager;
   public float Angle = 0.0F;
   public String name;
   private String parentObjectName = null;
   public boolean Scissor;
   public int ID = 20000000;
   public IsoSpriteInstance def;
   static ColorInfo info = new ColorInfo();
   public static int globalOffsetX = -1;
   public static int globalOffsetY = -1;
   public ModelManager.ModelSlot modelSlot;
   static HashMap AnimNameSet = new HashMap();
   IsoObjectType type;
   private IsoSpriteGrid spriteGrid;

   public IsoSpriteInstance newInstance() {
      return IsoSpriteInstance.get(this);
   }

   public PropertyContainer getProperties() {
      return this.Properties;
   }

   public void setProperties(PropertyContainer var1) {
      this.Properties = var1;
   }

   public String getParentObjectName() {
      return this.parentObjectName;
   }

   public IsoSprite() {
      this.type = IsoObjectType.MAX;
      if (IsoWorld.instance.CurrentCell != null) {
         this.parentManager = IsoWorld.instance.CurrentCell.SpriteManager;
      }

      this.def = IsoSpriteInstance.get(this);
   }

   public IsoSprite(IsoSpriteManager var1) {
      this.type = IsoObjectType.MAX;
      this.parentManager = var1;
      this.def = IsoSpriteInstance.get(this);
   }

   public void save(DataOutputStream var1) throws IOException {
      GameWindow.WriteString(var1, this.name);
   }

   public void load(DataInputStream var1) throws IOException {
      this.name = GameWindow.ReadString(var1);
      this.LoadFramesNoDirPageSimple(this.name);
   }

   public static IsoSprite CreateSprite(IsoSpriteManager var0) {
      IsoSprite var1 = new IsoSprite(var0);
      return var1;
   }

   public static IsoSprite getSprite(IsoSpriteManager var0, int var1) {
      if (WorldConverter.instance.TilesetConversions != null && !WorldConverter.instance.TilesetConversions.isEmpty() && WorldConverter.instance.TilesetConversions.containsKey(var1)) {
         var1 = (Integer)WorldConverter.instance.TilesetConversions.get(var1);
      }

      return var0.IntMap.containsKey(var1) ? (IsoSprite)var0.IntMap.get(var1) : null;
   }

   public static void setSpriteID(IsoSpriteManager var0, int var1, IsoSprite var2) {
      if (var0.IntMap.containsKey(var2.ID)) {
         var0.IntMap.remove(var2.ID);
         var2.ID = var1;
         var0.IntMap.put(var1, var2);
      }

   }

   public static IsoSprite getSprite(IsoSpriteManager var0, IsoSprite var1, int var2) {
      if (var1.name.contains("_")) {
         String[] var3 = var1.name.split("_");
         int var4 = Integer.parseInt(var3[var3.length - 1].trim());
         var4 += var2;
         return (IsoSprite)var0.NamedMap.get(var1.name.substring(0, var1.name.lastIndexOf("_")) + "_" + var4);
      } else {
         return null;
      }
   }

   public static IsoSprite getSprite(IsoSpriteManager var0, String var1, int var2) {
      IsoSprite var3 = (IsoSprite)var0.NamedMap.get(var1);
      String var4 = var3.name.substring(0, var3.name.lastIndexOf(95));
      String var5 = var3.name.substring(var3.name.lastIndexOf(95) + 1);
      if (var3.name.contains("_")) {
         int var6 = Integer.parseInt(var5.trim());
         var6 += var2;
         return var0.getSprite(var4 + "_" + var6);
      } else {
         return null;
      }
   }

   public int getSheetGridIdFromName() {
      return this.name != null ? getSheetGridIdFromName(this.name) : -1;
   }

   public static int getSheetGridIdFromName(String var0) {
      if (var0 != null) {
         int var1 = var0.lastIndexOf(95);
         if (var1 > 0 && var1 + 1 < var0.length()) {
            return Integer.parseInt(var0.substring(var1 + 1));
         }
      }

      return -1;
   }

   public static void DisposeAll() {
      AnimNameSet.clear();
   }

   public void Dispose() {
      Iterator var1 = this.AnimMap.values().iterator();

      while(var1.hasNext()) {
         IsoAnim var2 = (IsoAnim)var1.next();
         var2.Dispose();
      }

      this.AnimMap.clear();
      this.AnimStack.clear();
      this.CurrentAnim = null;
   }

   public boolean isMaskClicked(IsoDirections var1, int var2, int var3) {
      try {
         Texture var4 = ((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)this.def.Frame)).directions[var1.index()];
         if (var4 == null) {
            return false;
         } else {
            Mask var5 = var4.getMask();
            if (var5 == null) {
               return false;
            } else {
               var2 = (int)((float)var2 - var4.offsetX);
               var3 = (int)((float)var3 - var4.offsetY);
               return var5.get(var2, var3);
            }
         }
      } catch (Exception var6) {
         Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var6);
         return true;
      }
   }

   public boolean isMaskClicked(IsoDirections var1, int var2, int var3, boolean var4) {
      if (this.CurrentAnim == null) {
         return false;
      } else {
         if (this.def == null) {
            this.def = IsoSpriteInstance.get(this);
         }

         try {
            if (this.CurrentAnim != null && this.CurrentAnim.Frames != null && !(this.def.Frame >= (float)this.CurrentAnim.Frames.size())) {
               Texture var5 = ((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)this.def.Frame)).directions[var1.index()];
               if (var5 == null) {
                  return false;
               } else {
                  Mask var6 = var5.getMask();
                  if (var6 == null) {
                     return false;
                  } else {
                     if (var4) {
                        var2 = (int)((float)var2 - ((float)(var5.getWidthOrig() - var5.getWidth()) - var5.offsetX));
                        var3 = (int)((float)var3 - var5.offsetY);
                        var2 = var5.getWidth() - var2;
                     } else {
                        var2 = (int)((float)var2 - var5.offsetX);
                        var3 = (int)((float)var3 - var5.offsetY);
                     }

                     return var2 >= 0 && var3 >= 0 && var2 <= var5.getWidth() && var3 <= var5.getHeight() ? var6.get(var2, var3) : false;
                  }
               }
            } else {
               return false;
            }
         } catch (Exception var7) {
            Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var7);
            return true;
         }
      }
   }

   public float getMaskClickedY(IsoDirections var1, int var2, int var3, boolean var4) {
      try {
         Texture var5 = ((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)this.def.Frame)).directions[var1.index()];
         if (var5 == null) {
            return 10000.0F;
         } else {
            Mask var6 = var5.getMask();
            if (var6 == null) {
               return 10000.0F;
            } else {
               if (var4) {
                  var2 = (int)((float)var2 - ((float)(var5.getWidthOrig() - var5.getWidth()) - var5.offsetX));
                  var3 = (int)((float)var3 - var5.offsetY);
                  var2 = var5.getWidth() - var2;
               } else {
                  var2 = (int)((float)var2 - var5.offsetX);
                  var3 = (int)((float)var3 - var5.offsetY);
                  var2 = var5.getWidth() - var2;
               }

               return (float)var3;
            }
         }
      } catch (Exception var7) {
         Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var7);
         return 10000.0F;
      }
   }

   public Texture LoadFrameExplicit(String var1) {
      this.CurrentAnim = new IsoAnim();
      this.AnimMap.put("default", this.CurrentAnim);
      this.CurrentAnim.ID = this.AnimStack.size();
      this.AnimStack.add(this.CurrentAnim);
      return this.CurrentAnim.LoadFrameExplicit(var1);
   }

   public void LoadFrames(String var1, String var2, int var3) {
      if (!this.AnimMap.containsKey(var2)) {
         this.CurrentAnim = new IsoAnim();
         this.AnimMap.put(var2, this.CurrentAnim);
         this.CurrentAnim.ID = this.AnimStack.size();
         this.AnimStack.add(this.CurrentAnim);
         this.CurrentAnim.LoadFrames(var1, var2, var3);
      }
   }

   public void LoadFramesReverseAltName(String var1, String var2, String var3, int var4) {
      if (!this.AnimMap.containsKey(var3)) {
         this.CurrentAnim = new IsoAnim();
         this.AnimMap.put(var3, this.CurrentAnim);
         this.CurrentAnim.ID = this.AnimStack.size();
         this.AnimStack.add(this.CurrentAnim);
         this.CurrentAnim.LoadFramesReverseAltName(var1, var2, var3, var4);
      }
   }

   public void DupeFrame() {
      this.CurrentAnim.DupeFrame();
   }

   public void LoadExtraFrame(String var1, String var2, int var3) {
      this.CurrentAnim.LoadExtraFrame(var1, var2, var3);
   }

   public void LoadFramesBits(String var1, String var2, String var3, int var4) {
      if (!this.AnimMap.containsKey(var3)) {
         this.CurrentAnim = new IsoAnim();
         this.AnimMap.put(var3, this.CurrentAnim);
         this.CurrentAnim.ID = this.AnimStack.size();
         this.AnimStack.add(this.CurrentAnim);
         this.CurrentAnim.LoadFramesBits(var1, var2, var3, var4);
      }
   }

   public void LoadFramesUseOtherFrame(String var1, String var2, String var3, String var4, int var5, String var6) {
      if (!this.AnimMap.containsKey(var3)) {
         this.CurrentAnim = new IsoAnim();
         this.AnimMap.put(var3, this.CurrentAnim);
         this.CurrentAnim.ID = this.AnimStack.size();
         this.AnimStack.add(this.CurrentAnim);
         this.CurrentAnim.LoadFramesUseOtherFrame(var1, var2, var3, var4, var5, var6);
      }
   }

   public void AddFramesUseOtherFrame(String var1, String var2, String var3, String var4, int var5, String var6) {
      this.CurrentAnim.LoadFramesUseOtherFrame(var1, var2, var3, var4, var5, var6);
   }

   public void LoadFramesBits(String var1, String var2, String var3, int var4, String var5) {
      if (!this.AnimMap.containsKey(var3)) {
         this.CurrentAnim = new IsoAnim();
         this.AnimMap.put(var3, this.CurrentAnim);
         this.CurrentAnim.ID = this.AnimStack.size();
         this.AnimStack.add(this.CurrentAnim);
         this.CurrentAnim.LoadFramesBits(var1, var2, var3, var4, var5);
      }
   }

   public void LoadFramesBits(String var1, String var2, int var3) {
      if (!this.AnimMap.containsKey(var2)) {
         this.CurrentAnim = new IsoAnim();
         this.AnimMap.put(var2, this.CurrentAnim);
         this.CurrentAnim.ID = this.AnimStack.size();
         this.AnimStack.add(this.CurrentAnim);
         this.CurrentAnim.LoadFramesBits(var1, var2, var3);
      }
   }

   public void LoadFramesBitRepeatFrame(String var1, String var2, String var3, int var4, String var5) {
      this.CurrentAnim = (IsoAnim)this.AnimMap.get(var3);
   }

   public void LoadFramesBitRepeatFrame(String var1, String var2, int var3) {
      if (!this.AnimMap.containsKey(var2)) {
         this.CurrentAnim = new IsoAnim();
         this.AnimMap.put(var2, this.CurrentAnim);
         this.CurrentAnim.ID = this.AnimStack.size();
         this.AnimStack.add(this.CurrentAnim);
         this.CurrentAnim.LoadFramesBitRepeatFrame(var1, var2, var3);
      }
   }

   public void LoadFramesNoDir(String var1, String var2, int var3) {
      if (!this.AnimMap.containsKey(var2)) {
         this.CurrentAnim = new IsoAnim();
         this.AnimMap.put(var2, this.CurrentAnim);
         this.CurrentAnim.ID = this.AnimStack.size();
         this.AnimStack.add(this.CurrentAnim);
         this.CurrentAnim.LoadFramesNoDir(var1, var2, var3);
      }
   }

   public void LoadFramesNoDirPage(String var1, String var2, int var3) {
      this.CurrentAnim = new IsoAnim();
      this.AnimMap.put(var2, this.CurrentAnim);
      this.CurrentAnim.ID = this.AnimStack.size();
      this.AnimStack.add(this.CurrentAnim);
      this.CurrentAnim.LoadFramesNoDirPage(var1, var2, var3);
   }

   public void LoadFramesNoDirPageDirect(String var1, String var2, int var3) {
      this.CurrentAnim = new IsoAnim();
      this.AnimMap.put(var2, this.CurrentAnim);
      this.CurrentAnim.ID = this.AnimStack.size();
      this.AnimStack.add(this.CurrentAnim);
      this.CurrentAnim.LoadFramesNoDirPageDirect(var1, var2, var3);
   }

   public void LoadFramesNoDirPageSimple(String var1) {
      if (this.AnimMap.containsKey("default")) {
         IsoAnim var2 = (IsoAnim)this.AnimMap.get("default");
         this.AnimStack.remove(var2);
         this.AnimMap.remove("default");
      }

      this.CurrentAnim = new IsoAnim();
      this.AnimMap.put("default", this.CurrentAnim);
      this.CurrentAnim.ID = this.AnimStack.size();
      this.AnimStack.add(this.CurrentAnim);
      this.CurrentAnim.LoadFramesNoDirPage(var1);
   }

   public void ReplaceCurrentAnimFrames(String var1) {
      if (this.CurrentAnim != null) {
         this.CurrentAnim.Frames.clear();
         this.CurrentAnim.LoadFramesNoDirPage(var1);
      }
   }

   public void LoadFramesPageSimple(String var1, String var2, String var3, String var4) {
      this.CurrentAnim = new IsoAnim();
      this.AnimMap.put("default", this.CurrentAnim);
      this.CurrentAnim.ID = this.AnimStack.size();
      this.AnimStack.add(this.CurrentAnim);
      this.CurrentAnim.LoadFramesPageSimple(var1, var2, var3, var4);
   }

   public void LoadFramesNoDirPalette(String var1, String var2, int var3, String var4) {
      if (!this.AnimMap.containsKey(var2)) {
         this.CurrentAnim = new IsoAnim();
         this.AnimMap.put(var2, this.CurrentAnim);
         this.CurrentAnim.ID = this.AnimStack.size();
         this.AnimStack.add(this.CurrentAnim);
         this.CurrentAnim.LoadFramesNoDirPalette(var1, var2, var3, var4);
      }
   }

   public void LoadFramesPalette(String var1, String var2, int var3, PaletteManager.PaletteInfo var4) {
      if (!this.AnimMap.containsKey(var2)) {
         this.CurrentAnim = new IsoAnim();
         this.AnimMap.put(var2, this.CurrentAnim);
         this.CurrentAnim.ID = this.AnimStack.size();
         this.AnimStack.add(this.CurrentAnim);
         this.CurrentAnim.LoadFramesPalette(var1, var2, var3, var4);
      }
   }

   public void LoadFramesPalette(String var1, String var2, int var3, String var4) {
      if (!this.AnimMap.containsKey(var2)) {
         this.CurrentAnim = new IsoAnim();
         this.AnimMap.put(var2, this.CurrentAnim);
         this.CurrentAnim.ID = this.AnimStack.size();
         this.AnimStack.add(this.CurrentAnim);
         this.CurrentAnim.LoadFramesPalette(var1, var2, var3, var4);
      }
   }

   public void LoadFramesPcx(String var1, String var2, int var3) {
      if (!this.AnimMap.containsKey(var2)) {
         this.CurrentAnim = new IsoAnim();
         this.AnimMap.put(var2, this.CurrentAnim);
         this.CurrentAnim.ID = this.AnimStack.size();
         this.AnimStack.add(this.CurrentAnim);
         this.CurrentAnim.LoadFramesPcx(var1, var2, var3);
      }
   }

   public void PlayAnimNoReset(String var1) {
      if (this.AnimMap.containsKey(var1) && (this.CurrentAnim == null || !this.CurrentAnim.name.equals(var1))) {
         this.CurrentAnim = (IsoAnim)this.AnimMap.get(var1);
      }

   }

   public void PlayAnim(IsoAnim var1) {
      if (this.CurrentAnim == null || this.CurrentAnim != var1) {
         this.CurrentAnim = var1;
      }

   }

   public void PlayAnim(String var1) {
      if ((this.CurrentAnim == null || !this.CurrentAnim.name.equals(var1)) && this.AnimMap.containsKey(var1)) {
         this.CurrentAnim = (IsoAnim)this.AnimMap.get(var1);
      }

   }

   public void PlayAnimUnlooped(String var1) {
      if (this.AnimMap.containsKey(var1)) {
         if (this.CurrentAnim == null || !this.CurrentAnim.name.equals(var1)) {
            this.CurrentAnim = (IsoAnim)this.AnimMap.get(var1);
         }

         this.CurrentAnim.looped = false;
      }

   }

   public void ChangeTintMod(ColorInfo var1) {
      this.TintMod.r = var1.r;
      this.TintMod.g = var1.g;
      this.TintMod.b = var1.b;
      this.TintMod.a = var1.a;
   }

   public void RenderGhostTile(int var1, int var2, int var3) {
      IsoSpriteInstance var4 = IsoSpriteInstance.get(this);
      var4.alpha = var4.targetAlpha = 0.6F;
      this.render(var4, (IsoObject)null, (float)var1, (float)var2, (float)var3, IsoDirections.N, (float)(32 * Core.TileScale), (float)(96 * Core.TileScale), IsoGridSquare.getDefColorInfo());
   }

   public void RenderGhostTileRed(int var1, int var2, int var3) {
      IsoSpriteInstance var4 = IsoSpriteInstance.get(this);
      var4.tintr = 0.65F;
      var4.tintg = 0.2F;
      var4.tintb = 0.2F;
      var4.alpha = var4.targetAlpha = 0.6F;
      this.render(var4, (IsoObject)null, (float)var1, (float)var2, (float)var3, IsoDirections.N, (float)(32 * Core.TileScale), (float)(96 * Core.TileScale), IsoGridSquare.getDefColorInfo());
   }

   public void RenderGhostTileColor(int var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      this.RenderGhostTileColor(var1, var2, var3, 0.0F, 0.0F, var4, var5, var6, var7);
   }

   public void RenderGhostTileColor(int var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      IsoSpriteInstance var10 = IsoSpriteInstance.get(this);
      var10.tintr = var6;
      var10.tintg = var7;
      var10.tintb = var8;
      var10.alpha = var10.targetAlpha = var9;
      IsoGridSquare.getDefColorInfo().r = IsoGridSquare.getDefColorInfo().g = IsoGridSquare.getDefColorInfo().b = IsoGridSquare.getDefColorInfo().a = 1.0F;
      int var11 = Core.TileScale;
      this.render(var10, (IsoObject)null, (float)var1, (float)var2, (float)var3, IsoDirections.N, (float)(32 * var11) + var4, (float)(96 * var11) + var5, IsoGridSquare.getDefColorInfo());
   }

   public void render(IsoObject var1, float var2, float var3, float var4, IsoDirections var5, float var6, float var7, ColorInfo var8) {
      if (this.def == null) {
         this.def = IsoSpriteInstance.get(this);
      }

      this.render(this.def, var1, var2, var3, var4, var5, var6, var7, var8, true);
   }

   public void render(IsoObject var1, float var2, float var3, float var4, IsoDirections var5, float var6, float var7, ColorInfo var8, boolean var9) {
      if (this.def == null) {
         this.def = IsoSpriteInstance.get(this);
      }

      this.render(this.def, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public float getScreenY(float var1, float var2, float var3, float var4, float var5) {
      float var6 = 0.0F;
      var6 = IsoUtils.YToScreen(var1 + this.def.offX, var2 + this.def.offY, var3 + this.def.offZ, 0);
      return var6;
   }

   public float getScreenX(float var1, float var2, float var3, float var4, float var5) {
      float var6 = 0.0F;
      var6 = IsoUtils.XToScreen(var1 + this.def.offX, var2 + this.def.offY, var3 + this.def.offZ, 0);
      return var6;
   }

   public void drawAt(IsoSpriteInstance var1, IsoObject var2, int var3, int var4, IsoDirections var5) {
      if (this.CurrentAnim != null && !this.CurrentAnim.Frames.isEmpty()) {
         try {
            float var6 = var1.Frame;
            if (var1.Frame >= (float)this.CurrentAnim.Frames.size()) {
               var6 = (float)(this.CurrentAnim.Frames.size() - 1);
            }

            if (var1.Frame < 0.0F) {
               var1.Frame = 0.0F;
               var6 = 0.0F;
            }

            if (var1 != null) {
               var1.renderprep(var2);
            }

            if (this.CurrentAnim == null) {
               return;
            }

            if (var2 instanceof IsoMovingObject && this.CurrentAnim != null && ((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)var6)).getTexture(var5) != null) {
               var3 -= ((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)var6)).getTexture(var5).getWidthOrig() / 2;
               var4 -= ((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)var6)).getTexture(var5).getHeightOrig();
            }

            info.r = 1.0F;
            info.g = 1.0F;
            info.b = 1.0F;
            info.a = 1.0F;
            if ((int)var6 < this.CurrentAnim.Frames.size()) {
               ((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)var6)).renderexplicit(var3, var4, var5, 1.0F, this.TintMod);
            } else {
               boolean var7 = false;
            }
         } catch (Exception var8) {
            IndieGL.End();
            Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var8);
         }

      }
   }

   public void render(IsoSpriteInstance var1, IsoObject var2, float var3, float var4, float var5, IsoDirections var6, float var7, float var8, ColorInfo var9) {
      this.render(var1, var2, var3, var4, var5, var6, var7, var8, var9, true);
   }

   public boolean hasActiveModel() {
      if (!ModelManager.instance.bDebugEnableModels) {
         return false;
      } else if (!PerformanceSettings.modelsEnabled) {
         return false;
      } else if (!ModelManager.instance.bCreated) {
         return false;
      } else {
         return this.modelSlot != null && this.modelSlot.active && !this.modelSlot.bRemove;
      }
   }

   public void renderVehicle(IsoSpriteInstance var1, IsoObject var2, float var3, float var4, float var5, IsoDirections var6, float var7, float var8, ColorInfo var9, boolean var10) {
      if (var1 != null) {
         if (this.hasActiveModel()) {
            SpriteRenderer.instance.drawModel(this.modelSlot);
         }

         info.r = var9.r;
         info.g = var9.g;
         info.b = var9.b;
         info.a = var9.a;

         try {
            if (var1 != null && var10) {
               var1.renderprep(var2);
            }

            float var11 = 0.0F;
            float var12 = 0.0F;
            if (globalOffsetX == -1) {
               globalOffsetX = -((int)IsoCamera.frameState.OffX);
               globalOffsetY = -((int)IsoCamera.frameState.OffY);
            }

            if (var2 == null || var2.sx == 0 || var2 instanceof IsoMovingObject) {
               var11 = IsoUtils.XToScreen(var3 + var1.offX, var4 + var1.offY, var5 + var1.offZ, 0);
               var12 = IsoUtils.YToScreen(var3 + var1.offX, var4 + var1.offY, var5 + var1.offZ, 0);
               this.lx = var3;
               this.ly = var4;
               this.lz = var5;
               var11 = (float)((int)var11);
               var12 = (float)((int)var12);
               var11 -= var7;
               var12 -= var8;
               if (var2 != null) {
                  var2.sx = (int)var11;
                  var2.sy = (int)var12;
               }
            }

            if (var2 != null) {
               var11 = (float)(var2.sx + globalOffsetX);
               var12 = (float)(var2.sy + globalOffsetY);
               var11 += (float)this.soffX;
               var12 += (float)this.soffY;
            } else {
               var11 += (float)globalOffsetX;
               var12 += (float)globalOffsetY;
               var11 += (float)this.soffX;
               var12 += (float)this.soffY;
            }

            float var13 = info.r;
            float var14 = info.g;
            float var15 = info.b;
            ColorInfo var10000;
            if (var1 != null && var10) {
               if (var1.tintr != 1.0F || var1.tintg != 1.0F || var1.tintb != 1.0F) {
                  var10000 = info;
                  var10000.r *= var1.tintr;
                  var10000 = info;
                  var10000.g *= var1.tintg;
                  var10000 = info;
                  var10000.b *= var1.tintb;
               }

               info.a = var1.alpha;
            }

            if (!this.hasActiveModel() && (this.TintMod.r != 1.0F || this.TintMod.g != 1.0F || this.TintMod.b != 1.0F)) {
               var10000 = info;
               var10000.r *= this.TintMod.r;
               var10000 = info;
               var10000.g *= this.TintMod.g;
               var10000 = info;
               var10000.b *= this.TintMod.b;
            }

            if (this.hasActiveModel()) {
               float var16 = 2.0F * var1.getScaleX() * (float)Core.TileScale;
               float var17 = -2.0F * var1.getScaleY() * (float)Core.TileScale;
               float var18 = ModelCamera.instance.VehicleScaleHack;
               var16 *= var18;
               var17 *= var18;
               int var19 = ModelManager.instance.bitmap.getTexture().getWidth();
               int var20 = ModelManager.instance.bitmap.getTexture().getHeight();
               var11 -= (float)var19 * var16 / 16.0F;
               var12 -= (float)var20 * var17 / 8.0F;
               var11 += 0.0F * var16;
               var12 += 69.0F * var17 / 2.0F + 43.0F * var17;
               var12 += 27.0F * (var18 - 1.0F) * (float)Core.TileScale;
               if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
                  SpriteRenderer.instance.render((Texture)ModelManager.instance.bitmap.getTexture(), var11, var12, (float)var19 * var16 / 8.0F, (float)var20 * var17 / 8.0F, 1.0F, 1.0F, 1.0F, info.a);
               } else {
                  SpriteRenderer.instance.render((Texture)ModelManager.instance.bitmap.getTexture(), var11, var12, (float)var19 * var16 / 8.0F, (float)var20 * var17 / 8.0F, info.r, info.g, info.b, info.a);
               }

               if (Core.bDebug && DebugOptions.instance.ModelRenderBounds.getValue()) {
                  LineDrawer.drawRect(var11, var12, (float)var19 * var16 / 8.0F, (float)var20 * var17 / 8.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1);
               }
            }

            info.r = 1.0F;
            info.g = 1.0F;
            info.b = 1.0F;
         } catch (Exception var21) {
            IndieGL.End();
            Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var21);
         }

      }
   }

   public void render(IsoSpriteInstance var1, IsoObject var2, float var3, float var4, float var5, IsoDirections var6, float var7, float var8, ColorInfo var9, boolean var10) {
      if (this.CurrentAnim != null && !this.CurrentAnim.Frames.isEmpty()) {
         if (this.CurrentAnim.FramesArray == null) {
            this.CurrentAnim.FramesArray = (IsoDirectionFrame[])this.CurrentAnim.Frames.toArray(this.CurrentAnim.FramesArray);
         }

         if (this.modelSlot != null && this.modelSlot.active && !this.modelSlot.bRemove && this.hasActiveModel()) {
            SpriteRenderer.instance.drawModel(this.modelSlot);
         }

         info.r = var9.r;
         info.g = var9.g;
         info.b = var9.b;
         info.a = var9.a;

         try {
            float var11 = var1.Frame;
            if (var1.Frame >= (float)this.CurrentAnim.Frames.size()) {
               var11 = (float)(this.CurrentAnim.FramesArray.length - 1);
            }

            if (var1.Frame < 0.0F) {
               var1.Frame = 0.0F;
               var11 = 0.0F;
            }

            if (var1 != null && var10) {
               if (var1.bCopyTargetAlpha && var2 != null) {
                  var1.targetAlpha = var2.targetAlpha[IsoCamera.frameState.playerIndex];
                  var1.alpha = var2.alpha[IsoCamera.frameState.playerIndex];
               } else {
                  var1.renderprep(var2);
               }
            }

            float var12 = 0.0F;
            float var13 = 0.0F;
            if (this.CurrentAnim == null) {
               return;
            }

            if (globalOffsetX == -1) {
               globalOffsetX = -((int)IsoCamera.frameState.OffX);
               globalOffsetY = -((int)IsoCamera.frameState.OffY);
            }

            if (var2 == null || var2.sx == 0 || var2 instanceof IsoMovingObject) {
               var12 = IsoUtils.XToScreen(var3 + var1.offX, var4 + var1.offY, var5 + var1.offZ, 0);
               var13 = IsoUtils.YToScreen(var3 + var1.offX, var4 + var1.offY, var5 + var1.offZ, 0);
               this.lx = var3;
               this.ly = var4;
               this.lz = var5;
               var12 = (float)((int)var12);
               var13 = (float)((int)var13);
               var12 -= var7;
               var13 -= var8;
               if (var2 != null) {
                  var2.sx = (int)var12;
                  var2.sy = (int)var13;
               }
            }

            if (var2 != null) {
               var12 = (float)(var2.sx + globalOffsetX);
               var13 = (float)(var2.sy + globalOffsetY);
               var12 += (float)this.soffX;
               var13 += (float)this.soffY;
            } else {
               var12 += (float)globalOffsetX;
               var13 += (float)globalOffsetY;
               var12 += (float)this.soffX;
               var13 += (float)this.soffY;
            }

            if (var2 instanceof IsoMovingObject && !this.hasActiveModel() && this.CurrentAnim != null && this.CurrentAnim.FramesArray[(int)var11].getTexture(var6) != null) {
               var12 -= (float)(this.CurrentAnim.FramesArray[(int)var11].getTexture(var6).getWidthOrig() / 2) * var1.getScaleX();
               var13 -= (float)this.CurrentAnim.FramesArray[(int)var11].getTexture(var6).getHeightOrig() * var1.getScaleY();
            }

            float var14 = info.r;
            float var15 = info.g;
            float var16 = info.b;
            ColorInfo var10000;
            if (var1 != null && var10) {
               if (var1.tintr != 1.0F || var1.tintg != 1.0F || var1.tintb != 1.0F) {
                  var10000 = info;
                  var10000.r *= var1.tintr;
                  var10000 = info;
                  var10000.g *= var1.tintg;
                  var10000 = info;
                  var10000.b *= var1.tintb;
               }

               info.a = var1.alpha;
            }

            if ((!(var2 instanceof IsoMovingObject) || !this.hasActiveModel()) && (this.TintMod.r != 1.0F || this.TintMod.g != 1.0F || this.TintMod.b != 1.0F)) {
               var10000 = info;
               var10000.r *= this.TintMod.r;
               var10000 = info;
               var10000.g *= this.TintMod.g;
               var10000 = info;
               var10000.b *= this.TintMod.b;
            }

            if ((int)var11 < this.CurrentAnim.FramesArray.length) {
               Texture var17 = this.CurrentAnim.FramesArray[(int)var11].getTexture(var6);
               if (Core.TileScale == 2 && var17 != null && var17.getWidthOrig() == 64 && var17.getHeightOrig() == 128) {
                  var1.setScale(2.0F, 2.0F);
               }

               if (Core.TileScale == 2 && var1.scaleX == 2.0F && var1.scaleY == 2.0F && var17 != null && var17.getWidthOrig() == 128 && var17.getHeightOrig() == 256) {
                  var1.setScale(1.0F, 1.0F);
               }

               float var18;
               float var19;
               boolean var23;
               if (var2 instanceof IsoMovingObject && this.hasActiveModel()) {
                  var18 = var1.getScaleX();
                  var19 = var1.getScaleY();
                  int var26 = ModelManager.instance.bitmap.getTexture().getWidth();
                  int var27 = ModelManager.instance.bitmap.getTexture().getHeight();
                  var12 -= (float)var26 * var18 / 16.0F;
                  var13 -= (float)var27 * var19 / 8.0F;
                  var12 += (float)IsoGameCharacter.RENDER_OFFSET_X * var18;
                  var13 += 69.0F * var19 / 2.0F + (float)IsoGameCharacter.RENDER_OFFSET_Y * var19;
                  SpriteRenderer.instance.render((Texture)ModelManager.instance.bitmap.getTexture(), var12, var13, (float)var26 * var18 / 8.0F, (float)var27 * var19 / 8.0F, info.r, info.g, info.b, info.a);
                  if (Core.bDebug && DebugOptions.instance.ModelRenderBounds.getValue()) {
                     LineDrawer.drawRect(var12, var13, (float)var26 * var18 / 8.0F, (float)var27 * var19 / 8.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1);
                  }

                  if ((int)var11 < this.CurrentAnim.FramesArray.length && IsoObjectPicker.Instance.wasDirty && IsoCamera.frameState.playerIndex == 0) {
                     Texture var29 = this.CurrentAnim.FramesArray[(int)var11].getTexture(var6);
                     if (var2 != null) {
                        var23 = var6 == IsoDirections.W || var6 == IsoDirections.SW || var6 == IsoDirections.S;
                        if (var1.Flip) {
                           var23 = !var23;
                        }

                        var12 = (float)(var2.sx + globalOffsetX);
                        var13 = (float)(var2.sy + globalOffsetY);
                        if (var2 instanceof IsoMovingObject) {
                           var12 -= (float)(var29.getWidthOrig() / 2) * var18;
                           var13 -= (float)var29.getHeightOrig() * var19;
                        }

                        IsoObjectPicker.Instance.Add((int)var12, (int)var13, (int)((float)var29.getWidthOrig() * var18), (int)((float)var29.getHeightOrig() * var19), var2.square, var2, var23, var18, var19);
                     }
                  }
               } else {
                  var18 = var1.scaleX;
                  var19 = var1.scaleY;
                  if (var18 == 1.0F && var19 == 1.0F) {
                     IsoDirectionFrame var25 = this.CurrentAnim.FramesArray[(int)var11];
                     if (var2 != null && var2.getObjectRenderEffectsToApply() != null) {
                        var25.render(var2.getObjectRenderEffectsToApply(), (float)((int)var12), (float)((int)var13), var6, info, var1.Flip, this.Angle);
                     } else {
                        var25.render((float)((int)var12), (float)((int)var13), var6, info, var1.Flip, this.Angle);
                     }

                     if (IsoObjectPicker.Instance.wasDirty && IsoCamera.frameState.playerIndex == 0 && (int)var11 < this.CurrentAnim.FramesArray.length && var2 != null && var17 != null) {
                        boolean var28 = var6 == IsoDirections.W || var6 == IsoDirections.SW || var6 == IsoDirections.S;
                        if (var1.Flip) {
                           var28 = !var28;
                        }

                        var12 = (float)(var2.sx + globalOffsetX);
                        var13 = (float)(var2.sy + globalOffsetY);
                        if (var2 instanceof IsoMovingObject) {
                           var12 -= (float)(var17.getWidthOrig() / 2) * var18;
                           var13 -= (float)var17.getHeightOrig() * var19;
                        }

                        IsoObjectPicker.Instance.Add((int)var12, (int)var13, (int)((float)var17.getWidthOrig() * var18), (int)((float)var17.getHeightOrig() * var19), var2.square, var2, var28, var18, var19);
                     }
                  } else {
                     Texture var20 = this.CurrentAnim.FramesArray[(int)var11].getTexture(var6);
                     if (var20 != null && var18 > 0.0F && var19 > 0.0F) {
                        float var21 = (float)var20.getWidth();
                        float var22 = (float)var20.getHeight();
                        if (Core.bDebug) {
                        }

                        if (var18 != 1.0F) {
                           var12 += var20.getOffsetX() * (var18 - 1.0F);
                           var21 *= var18;
                        }

                        if (var19 != 1.0F) {
                           var13 += var20.getOffsetY() * (var19 - 1.0F);
                           var22 *= var19;
                        }

                        if (var2 != null && var2.getObjectRenderEffectsToApply() != null) {
                           this.CurrentAnim.FramesArray[(int)var11].render(var2.getObjectRenderEffectsToApply(), (float)((int)var12), (float)((int)var13), var21, var22, var6, info, var1.Flip, this.Angle);
                        } else {
                           this.CurrentAnim.FramesArray[(int)var11].render((float)((int)var12), (float)((int)var13), var21, var22, var6, info, var1.Flip, this.Angle);
                        }

                        if ((int)var11 < this.CurrentAnim.FramesArray.length && IsoObjectPicker.Instance.wasDirty && IsoCamera.frameState.playerIndex == 0 && var2 != null) {
                           var23 = var6 == IsoDirections.W || var6 == IsoDirections.SW || var6 == IsoDirections.S;
                           if (var1.Flip) {
                              var23 = !var23;
                           }

                           var12 = (float)(var2.sx + globalOffsetX);
                           var13 = (float)(var2.sy + globalOffsetY);
                           if (var2 instanceof IsoMovingObject) {
                              var12 -= (float)(var20.getWidthOrig() / 2) * var18;
                              var13 -= (float)var20.getHeightOrig() * var19;
                           }

                           IsoObjectPicker.Instance.Add((int)var12, (int)var13, (int)((float)var20.getWidthOrig() * var18), (int)((float)var20.getHeightOrig() * var19), var2.square, var2, var23, var18, var19);
                        }
                     }
                  }
               }
            }

            info.r = var14;
            info.g = var15;
            info.b = var16;
         } catch (Exception var24) {
            IndieGL.End();
            Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var24);
         }

      }
   }

   public void renderBloodSplat(float var1, float var2, float var3, ColorInfo var4) {
      if (this.CurrentAnim != null && !this.CurrentAnim.Frames.isEmpty()) {
         boolean var5 = true;
         boolean var6 = true;
         byte var10 = 0;
         byte var11 = 0;

         try {
            if (globalOffsetX == -1) {
               globalOffsetX = -((int)IsoCamera.frameState.OffX);
               globalOffsetY = -((int)IsoCamera.frameState.OffY);
            }

            float var7 = IsoUtils.XToScreen(var1, var2, var3, 0);
            float var8 = IsoUtils.YToScreen(var1, var2, var3, 0);
            var7 = (float)((int)var7);
            var8 = (float)((int)var8);
            var7 -= (float)var10;
            var8 -= (float)var11;
            var7 += (float)globalOffsetX;
            var8 += (float)globalOffsetY;
            if (!(var7 >= (float)IsoCamera.frameState.OffscreenWidth) && !(var7 + 64.0F <= 0.0F)) {
               if (!(var8 >= (float)IsoCamera.frameState.OffscreenHeight) && !(var8 + 64.0F <= 0.0F)) {
                  info.r = var4.r;
                  info.g = var4.g;
                  info.b = var4.b;
                  info.a = var4.a;
                  ((IsoDirectionFrame)this.CurrentAnim.Frames.get(0)).render((float)((int)var7), (float)((int)var8), IsoDirections.N, info, false, this.Angle);
               }
            }
         } catch (Exception var9) {
            Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var9);
         }
      }
   }

   public void renderObjectPicker(IsoSpriteInstance var1, IsoObject var2, float var3, float var4, float var5, IsoDirections var6, float var7, float var8, ColorInfo var9) {
      if (this.CurrentAnim != null) {
         if (var1 != null) {
            if (IsoPlayer.instance == IsoPlayer.players[0]) {
               if (this.CurrentAnim != null && !this.CurrentAnim.Frames.isEmpty()) {
                  if (this.CurrentAnim.Frames.size() != 0) {
                     if (var1.Frame >= (float)this.CurrentAnim.Frames.size()) {
                        var1.Frame = 0.0F;
                     }

                     if (((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)var1.Frame)).getTexture(var6) != null) {
                        float var10 = (float)(var2.sx + globalOffsetX);
                        float var11 = (float)(var2.sy + globalOffsetY);
                        if (var2 instanceof IsoMovingObject) {
                           var10 -= (float)(((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)var1.Frame)).getTexture(var6).getWidthOrig() / 2) * var1.getScaleX();
                           var11 -= (float)((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)var1.Frame)).getTexture(var6).getHeightOrig() * var1.getScaleY();
                        }

                        if (var1.Frame < (float)this.CurrentAnim.Frames.size() && IsoObjectPicker.Instance.wasDirty && IsoCamera.frameState.playerIndex == 0) {
                           Texture var12 = ((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)var1.Frame)).getTexture(var6);
                           if (var2 != null) {
                              boolean var13 = var6 == IsoDirections.W || var6 == IsoDirections.SW || var6 == IsoDirections.S;
                              if (var1.Flip) {
                                 var13 = !var13;
                              }

                              IsoObjectPicker.Instance.Add((int)var10, (int)var11, (int)((float)var12.getWidthOrig() * var1.getScaleX()), (int)((float)var12.getHeightOrig() * var1.getScaleY()), var2.square, var2, var13, var1.getScaleX(), var1.getScaleY());
                           }
                        }

                     }
                  }
               }
            }
         }
      }
   }

   public void update() {
      this.update(this.def);
   }

   public void update(IsoSpriteInstance var1) {
      if (var1 == null) {
         var1 = IsoSpriteInstance.get(this);
      }

      if (this.CurrentAnim != null) {
         if (this.Animate && !var1.Finished) {
            float var2 = var1.Frame;
            if (this.AnimateWhenPaused) {
               var1.Frame = (float)((double)var1.Frame + (double)var1.AnimFrameIncrease * UIManager.getSecondsSinceLastRender() * 60.0D);
            } else if (UIManager.getSpeedControls() == null || UIManager.getSpeedControls().getCurrentGameSpeed() > 0) {
               var1.Frame += var1.AnimFrameIncrease * GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F;
            }

            if ((int)var1.Frame >= this.CurrentAnim.Frames.size() && this.Loop && var1.Looped) {
               var1.Frame = 0.0F;
            }

            if ((int)var2 != (int)var1.Frame) {
               var1.NextFrame = true;
            }

            if ((int)var1.Frame >= this.CurrentAnim.Frames.size() && (!this.Loop || !var1.Looped)) {
               var1.Finished = true;
               var1.Frame = (float)this.CurrentAnim.FinishUnloopedOnFrame;
               if (this.DeleteWhenFinished) {
                  this.Dispose();
                  this.Animate = false;
               }
            }
         }

      }
   }

   public void CacheAnims(String var1) {
      this.name = var1;
      Stack var2 = new Stack();

      for(int var3 = 0; var3 < this.AnimStack.size(); ++var3) {
         IsoAnim var4 = (IsoAnim)this.AnimStack.get(var3);
         String var5 = var1 + var4.name;
         var2.add(var5);
         if (!IsoAnim.GlobalAnimMap.containsKey(var5)) {
            IsoAnim.GlobalAnimMap.put(var5, var4);
         }
      }

      AnimNameSet.put(var1, var2.toArray());
   }

   public static boolean HasCache(String var0) {
      return AnimNameSet.containsKey(var0);
   }

   public void LoadCache(String var1) {
      Object[] var2 = (Object[])AnimNameSet.get(var1);
      this.name = var1;

      for(int var3 = 0; var3 < var2.length; ++var3) {
         String var4 = (String)var2[var3];
         IsoAnim var5 = (IsoAnim)IsoAnim.GlobalAnimMap.get(var4);
         this.AnimMap.put(var5.name, var5);
         this.AnimStack.add(var5);
         this.CurrentAnim = var5;
      }

   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setParentObjectName(String var1) {
      this.parentObjectName = var1;
   }

   public IsoObjectType getType() {
      return this.type;
   }

   public void setType(IsoObjectType var1) {
      this.type = var1;
   }

   public void AddProperties(IsoSprite var1) {
      this.getProperties().AddProperties(var1.getProperties());
   }

   public IsoSpriteInstance getDefaultSpriteInst() {
      return this.def;
   }

   public String getName() {
      return this.name;
   }

   public void setTintMod(ColorInfo var1) {
      this.TintMod = var1;
   }

   public ColorInfo getTintMod() {
      return this.TintMod;
   }

   public void setAnimate(boolean var1) {
      this.Animate = var1;
   }

   public void setAnimateWhenPaused(boolean var1) {
      this.AnimateWhenPaused = var1;
   }

   public void setSpriteGrid(IsoSpriteGrid var1) {
      this.spriteGrid = var1;
   }

   public IsoSpriteGrid getSpriteGrid() {
      return this.spriteGrid;
   }

   public boolean isMoveWithWind() {
      return this.moveWithWind;
   }
}
