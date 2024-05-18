package zombie.core.skinnedmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import zombie.characters.IsoGameCharacter;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureFBO;
import zombie.core.textures.TextureID;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;

public class DeadBodyAtlas {
   public static final int ENTRY_WID;
   public static final int ENTRY_HGT;
   private TextureFBO fbo;
   public static DeadBodyAtlas instance;
   private HashMap EntryMap = new HashMap();
   private ArrayList AtlasList = new ArrayList();
   private static Stack JobPool;
   private ArrayList RenderJobs = new ArrayList();
   private ArrayList RenderJobsDone = new ArrayList();

   public Texture getBodyTexture(IsoDeadBody var1) {
      String var2 = this.getBodyKey(var1);
      if (this.EntryMap.containsKey(var2)) {
         return ((DeadBodyAtlas.AtlasEntry)this.EntryMap.get(var2)).tex;
      } else {
         DeadBodyAtlas.Atlas var3 = null;

         for(int var4 = 0; var4 < this.AtlasList.size(); ++var4) {
            if (!((DeadBodyAtlas.Atlas)this.AtlasList.get(var4)).isFull()) {
               var3 = (DeadBodyAtlas.Atlas)this.AtlasList.get(var4);
               break;
            }
         }

         if (var3 == null) {
            var3 = new DeadBodyAtlas.Atlas(1024, 1024);
            if (this.fbo == null) {
               return null;
            }

            this.AtlasList.add(var3);
         }

         DeadBodyAtlas.AtlasEntry var5 = var3.addBody(var2);
         this.EntryMap.put(var2, var5);
         this.RenderJobs.add(DeadBodyAtlas.RenderJob.getNew().init(var1, var5));
         return var5.tex;
      }
   }

   private String getBodyPartKey(IsoSprite var1) {
      if (var1 == null) {
         return "";
      } else {
         String var2 = ((IsoDirectionFrame)var1.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N).getName();
         return var2 + "_" + (int)(var1.TintMod.r * 255.0F) + "_" + (int)(var1.TintMod.g * 255.0F) + "_" + (int)(var1.TintMod.b * 255.0F);
      }
   }

   private String getBodyKey(IsoDeadBody var1) {
      String var2 = this.getBodyPartKey(var1.legsSprite) + this.getBodyPartKey(var1.topSprite) + this.getBodyPartKey(var1.bottomsSprite);

      for(int var3 = 0; var3 < var1.extraSprites.size(); ++var3) {
         var2 = var2 + this.getBodyPartKey((IsoSprite)var1.extraSprites.get(var3));
      }

      return var2;
   }

   public void render() {
      int var1;
      DeadBodyAtlas.RenderJob var2;
      for(var1 = 0; var1 < this.RenderJobsDone.size(); ++var1) {
         var2 = (DeadBodyAtlas.RenderJob)this.RenderJobsDone.get(var1);
         if (var2.done == 1) {
            ModelManager.instance.Remove(var2.modelChr);
            var2.done = 2;
         } else if (var2.done == 2 && var2.modelChr.legsSprite.modelSlot == null) {
            var2.modelChr.setCurrent((IsoGridSquare)null);
            this.RenderJobsDone.remove(var1--);
            JobPool.push(var2);
         }
      }

      for(var1 = 0; var1 < this.RenderJobs.size(); ++var1) {
         var2 = (DeadBodyAtlas.RenderJob)this.RenderJobs.get(var1);
         SpriteRenderer.instance.drawModel(var2.modelChr.legsSprite.modelSlot);
         SpriteRenderer.instance.toBodyAtlas(var2);
      }

      this.RenderJobsDone.addAll(this.RenderJobs);
      this.RenderJobs.clear();
   }

   public void renderUI() {
      if (Core.bDebug && DebugOptions.instance.DeadBodyAtlasRender.getValue()) {
         int var1 = 384 / Core.TileScale;
         int var2 = 0;
         int var3 = 0;

         for(int var4 = 0; var4 < this.AtlasList.size(); ++var4) {
            SpriteRenderer.instance.render((Texture)null, var2, var3, var1, var1, 1.0F, 1.0F, 1.0F, 0.75F);
            SpriteRenderer.instance.render(((DeadBodyAtlas.Atlas)this.AtlasList.get(var4)).tex, var2, var3, var1, var1, 1.0F, 1.0F, 1.0F, 1.0F);
            float var5 = (float)var1 / (float)((DeadBodyAtlas.Atlas)this.AtlasList.get(var4)).tex.getWidth();

            int var6;
            for(var6 = 0; var6 < ((DeadBodyAtlas.Atlas)this.AtlasList.get(var4)).tex.getWidth() / ENTRY_WID; ++var6) {
               SpriteRenderer.instance.renderline((Texture)null, (int)((float)var2 + (float)(var6 * ENTRY_WID) * var5), var3, (int)((float)var2 + (float)(var6 * ENTRY_WID) * var5), var3 + var1, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            for(var6 = 0; var6 < ((DeadBodyAtlas.Atlas)this.AtlasList.get(var4)).tex.getHeight() / ENTRY_HGT; ++var6) {
               SpriteRenderer.instance.renderline((Texture)null, var2, (int)((float)var3 + (float)(var6 * ENTRY_HGT) * var5), var2 + var1, (int)((float)var3 + (float)(var6 * ENTRY_HGT) * var5), 1.0F, 1.0F, 1.0F, 1.0F);
            }

            var3 += var1;
            if (var3 + var1 > Core.getInstance().getScreenHeight()) {
               var3 = 0;
               var2 += var1;
            }
         }
      }

   }

   public void toBodyAtlas(DeadBodyAtlas.RenderJob var1) {
      GL11.glPushAttrib(2048);
      this.fbo.startDrawing();
      if (this.fbo.getTexture() != var1.entry.atlas.tex) {
         this.fbo.swapTexture(var1.entry.atlas.tex);
      }

      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      int var2 = var1.entry.atlas.tex.getWidth();
      int var3 = var1.entry.atlas.tex.getHeight();
      GLU.gluOrtho2D(0.0F, (float)var2, 0.0F, (float)var3);
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      GL11.glEnable(3553);
      GL11.glDisable(3089);
      if (var1.entry.atlas.clear) {
         GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glClear(16640);
         GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
         var1.entry.atlas.clear = false;
      }

      ModelManager.instance.bitmap.getTexture().bind();
      int var4 = ModelManager.instance.bitmap.getTexture().getWidth() / 8 * Core.TileScale;
      int var5 = ModelManager.instance.bitmap.getTexture().getHeight() / 8 * Core.TileScale;
      int var6 = -32 * Core.TileScale;
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      GL11.glBegin(7);
      GL11.glVertex2i(var1.entry.x - (var4 - ENTRY_WID) / 2, var1.entry.y - (var5 - ENTRY_HGT) / 2 + var6);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2i(var1.entry.x - (var4 - ENTRY_WID) / 2 + var4, var1.entry.y - (var5 - ENTRY_HGT) / 2 + var6);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2i(var1.entry.x - (var4 - ENTRY_WID) / 2 + var4, var1.entry.y - (var5 - ENTRY_HGT) / 2 + var6 + var5);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2i(var1.entry.x - (var4 - ENTRY_WID) / 2, var1.entry.y - (var5 - ENTRY_HGT) / 2 + var6 + var5);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glEnd();
      this.fbo.endDrawing();
      GL11.glEnable(3089);
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPopAttrib();
      var1.done = 1;
   }

   static {
      ENTRY_WID = 85 * Core.TileScale;
      ENTRY_HGT = 51 * Core.TileScale;
      instance = new DeadBodyAtlas();
      JobPool = new Stack();
   }

   public static class RenderJob {
      public IsoDeadBody body;
      public DeadBodyAtlas.AtlasEntry entry;
      public IsoGameCharacter modelChr;
      public int done = 0;

      public static DeadBodyAtlas.RenderJob getNew() {
         return DeadBodyAtlas.JobPool.isEmpty() ? new DeadBodyAtlas.RenderJob() : (DeadBodyAtlas.RenderJob)DeadBodyAtlas.JobPool.pop();
      }

      private IsoSprite copySprite(IsoSprite var1) {
         if (var1 == null) {
            return null;
         } else {
            IsoSprite var2 = new IsoSprite(IsoWorld.instance.spriteManager);
            var2.LoadFramesNoDirPageSimple(((IsoDirectionFrame)var1.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N).getName());
            var2.TintMod.r = var1.TintMod.r;
            var2.TintMod.g = var1.TintMod.g;
            var2.TintMod.b = var1.TintMod.b;
            return var2;
         }
      }

      private static boolean isInteger(String var0) {
         try {
            Integer.parseInt(var0);
            return true;
         } catch (NumberFormatException var2) {
            return false;
         }
      }

      public DeadBodyAtlas.RenderJob init(IsoDeadBody var1, DeadBodyAtlas.AtlasEntry var2) {
         this.body = var1;
         this.entry = var2;
         if (this.modelChr == null) {
            this.modelChr = new DeadBodyAtlas.AtlasCharacter(IsoWorld.instance.CurrentCell, 0.0F, 0.0F, 0.0F);
            this.modelChr.def.Looped = false;
            this.modelChr.setbUseParts(true);
         }

         this.modelChr.sprite = this.modelChr.legsSprite = this.copySprite(var1.legsSprite);
         IsoDirectionFrame var5 = (IsoDirectionFrame)this.modelChr.legsSprite.CurrentAnim.Frames.get(0);
         String var6 = var5.getTexture(IsoDirections.N).getName();
         if (!var6.startsWith("Male_") && !var6.startsWith("Kate_")) {
            this.modelChr.legsSprite.name = var6.substring(0, var6.indexOf("_"));
         } else {
            String[] var7 = var6.split("_");
            if (isInteger(var7[1]) && isInteger(var7[2])) {
               this.modelChr.legsSprite.name = var7[0] + "_" + var7[1];
            } else {
               this.modelChr.legsSprite.name = var7[0];
            }
         }

         byte var3;
         byte var4;
         if (var5 != null && var5.getTexture(IsoDirections.N).getName().contains("ZombieDeath")) {
            this.modelChr.legsSprite.CurrentAnim.name = "ZombieDeath";
            var3 = 13;
            var4 = 14;
         } else {
            this.modelChr.legsSprite.CurrentAnim.name = "ZombieCrawl";
            var3 = 4;
            var4 = 11;
         }

         this.modelChr.def.parentSprite = this.modelChr.legsSprite;
         this.modelChr.def.Frame = 0.0F;
         this.modelChr.def.Looped = false;
         this.modelChr.topSprite = this.copySprite(var1.topSprite);
         this.modelChr.bottomsSprite = this.copySprite(var1.bottomsSprite);
         this.modelChr.extraSprites.clear();

         for(int var9 = 0; var9 < var1.extraSprites.size(); ++var9) {
            this.modelChr.extraSprites.add(this.copySprite((IsoSprite)var1.extraSprites.get(var9)));
         }

         this.modelChr.dir = var1.dir;
         this.modelChr.getVectorFromDirection(this.modelChr.angle);
         this.modelChr.x = var1.x;
         this.modelChr.y = var1.y;
         this.modelChr.z = var1.z;
         this.modelChr.setCurrent(var1.square);
         if (var1.square == null) {
            DebugLog.log("ERROR: body.square == null, no 3D corpse will be rendered");
         }

         ModelManager.instance.Add(this.modelChr);
         AnimationPlayer var10 = this.modelChr.legsSprite.modelSlot.model.AnimPlayer;
         AnimationTrack var8 = (AnimationTrack)var10.Tracks.get(0);
         var8.currentTimeValue = ((float)var3 + 1.0F) / (float)var4 * var8.CurrentClip.Duration;
         var10.Update(0.0F, true, (Matrix4f)null);
         this.done = 0;
         return this;
      }
   }

   public static final class AtlasCharacter extends IsoGameCharacter {
      public AtlasCharacter(IsoCell var1, float var2, float var3, float var4) {
         super(var1, var2, var3, var4);
      }
   }

   private class Atlas {
      public Texture tex;
      public ArrayList EntryList = new ArrayList();
      public boolean clear = true;

      public Atlas(int var2, int var3) {
         try {
            try {
               TextureID.bUseCompression = false;
               this.tex = new Texture(var2, var3);
            } finally {
               TextureID.bUseCompression = TextureID.bUseCompressionOption;
            }

            if (DeadBodyAtlas.this.fbo == null) {
               DeadBodyAtlas.this.fbo = new TextureFBO(this.tex, true);
            }
         } catch (Exception var8) {
            var8.printStackTrace();
         }

      }

      public boolean isFull() {
         int var1 = this.tex.getWidth() / DeadBodyAtlas.ENTRY_WID;
         int var2 = this.tex.getHeight() / DeadBodyAtlas.ENTRY_HGT;
         return this.EntryList.size() >= var1 * var2;
      }

      public DeadBodyAtlas.AtlasEntry addBody(String var1) {
         int var2 = this.tex.getWidth() / DeadBodyAtlas.ENTRY_WID;
         int var3 = this.EntryList.size();
         int var4 = var3 % var2;
         int var5 = var3 / var2;
         DeadBodyAtlas.AtlasEntry var6 = DeadBodyAtlas.this.new AtlasEntry();
         var6.atlas = this;
         var6.key = var1;
         var6.x = var4 * DeadBodyAtlas.ENTRY_WID;
         var6.y = var5 * DeadBodyAtlas.ENTRY_HGT;
         var6.w = DeadBodyAtlas.ENTRY_WID;
         var6.h = DeadBodyAtlas.ENTRY_HGT;
         var6.tex = this.tex.split(var6.x, var6.y, var6.w, var6.h);
         this.EntryList.add(var6);
         return var6;
      }
   }

   private class AtlasEntry {
      public DeadBodyAtlas.Atlas atlas;
      public String key;
      public int age;
      public int x;
      public int y;
      public int w;
      public int h;
      public Texture tex;

      private AtlasEntry() {
      }

      // $FF: synthetic method
      AtlasEntry(Object var2) {
         this();
      }
   }
}
