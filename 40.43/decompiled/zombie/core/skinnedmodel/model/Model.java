package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import org.joml.Math;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.Util;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderSettings;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.Matrix4;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.skinnedmodel.shader.ShaderManager;
import zombie.core.textures.Texture;
import zombie.debug.DebugOptions;
import zombie.iso.sprite.SkyBox;

public class Model {
   public static ArrayList ModelList = new ArrayList();
   public String Name;
   public Matrix4 Transform;
   public ModelMesh Mesh = new ModelMesh();
   public Model Parent;
   public ArrayList Children;
   public Shader Effect;
   public int ID = 0;
   public Object Tag;
   public boolean bStatic = false;
   public Texture tex = null;

   public Model(boolean var1) {
      this.Transform = Matrix4.Identity;
      this.Children = new ArrayList();
      this.ID = ModelList.size();
      ModelList.add(this);
      this.bStatic = var1;
   }

   public void Draw(ModelInstance var1) {
      float var3;
      if (var1.character == null) {
         GL11.glEnable(2884);
         GL11.glCullFace(1029);
         GL11.glEnable(2929);
         GL11.glDepthFunc(513);
         ModelCamera.instance.BeginVehicle(this, var1);
         int var2 = SpriteRenderer.instance.states[2].index;
         if (this.Effect != null) {
            this.Effect.Start();
            var3 = 1.0F - Math.min(RenderSettings.getInstance().getPlayerSettings(IsoPlayer.getPlayerIndex()).getDarkness() * 0.6F, 0.8F);
            var3 *= 0.9F;
            synchronized(var1.lights) {
               if (var1.lights[0] != null) {
                  this.Effect.setLight(0, (float)var1.lights[0].x, (float)var1.lights[0].y, (float)var1.lights[0].z, var1.lights[0].r, var1.lights[0].g, var1.lights[0].b, (float)var1.lights[0].radius, var1);
               } else {
                  this.Effect.setLight(0, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, var1);
               }

               if (var1.lights[1] != null) {
                  this.Effect.setLight(1, (float)var1.lights[1].x, (float)var1.lights[1].y, (float)var1.lights[1].z, var1.lights[1].r, var1.lights[1].g, var1.lights[1].b, (float)var1.lights[1].radius, var1);
               } else {
                  this.Effect.setLight(1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, var1);
               }

               if (var1.lights[2] != null) {
                  this.Effect.setLight(2, (float)var1.lights[2].x, (float)var1.lights[2].y, (float)var1.lights[2].z, var1.lights[2].r, var1.lights[2].g, var1.lights[2].b, (float)var1.lights[2].radius, var1);
               } else {
                  this.Effect.setLight(2, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, var1);
               }
            }

            if (var1.isVehicleBody) {
               GL13.glActiveTexture(33984);
               this.Effect.setTexture(var1.tex);
               GL11.glTexEnvi(8960, 8704, 7681);
               GL13.glActiveTexture(33985);
               this.Effect.setTexture(var1.textureRust);
               GL11.glTexEnvi(8960, 8704, 7681);
               this.Effect.setTextureRustA(var1.textureRustA);
               GL13.glActiveTexture(33986);
               this.Effect.setTexture(var1.textureMask);
               GL11.glTexEnvi(8960, 8704, 7681);
               GL13.glActiveTexture(33987);
               this.Effect.setTexture(var1.textureLights);
               GL11.glTexEnvi(8960, 8704, 7681);
               GL13.glActiveTexture(33988);
               this.Effect.setTexture(var1.textureDamage1Overlay);
               GL11.glTexEnvi(8960, 8704, 7681);
               GL13.glActiveTexture(33989);
               this.Effect.setTexture(var1.textureDamage1Shell);
               GL11.glTexEnvi(8960, 8704, 7681);
               GL13.glActiveTexture(33990);
               this.Effect.setTexture(var1.textureDamage2Overlay);
               GL11.glTexEnvi(8960, 8704, 7681);
               GL13.glActiveTexture(33991);
               this.Effect.setTexture(var1.textureDamage2Shell);
               GL11.glTexEnvi(8960, 8704, 7681);
               if (Core.getInstance().getPerfReflectionsOnLoad()) {
                  try {
                     GL13.glActiveTexture(33992);
                     this.Effect.setTexture((Texture)SkyBox.getInstance().getTextureCurrent());
                     GL11.glTexEnvi(8960, 8704, 7681);
                     GL13.glActiveTexture(33993);
                     this.Effect.setTexture((Texture)SkyBox.getInstance().getTexturePrev());
                     GL11.glTexEnvi(8960, 8704, 7681);
                     Util.checkGLError();
                  } catch (Throwable var8) {
                  }

                  this.Effect.setReflectionParam(SkyBox.getInstance().getTextureShift(), var1.refWindows, var1.refBody);
               }

               this.Effect.setTextureUninstall1(var1.textureUninstall1);
               this.Effect.setTextureUninstall2(var1.textureUninstall2);
               this.Effect.setTextureLightsEnables2(var1.textureLightsEnables2);
               this.Effect.setTextureDamage1Enables1(var1.textureDamage1Enables1);
               this.Effect.setTextureDamage1Enables2(var1.textureDamage1Enables2);
               this.Effect.setTextureDamage2Enables1(var1.textureDamage2Enables1);
               this.Effect.setTextureDamage2Enables2(var1.textureDamage2Enables2);
               this.Effect.setTexturePainColor(var1.painColor, 1.0F);
            } else if (var1.isVehicleWheel) {
               this.Effect.setShaderAlpha(1.0F);
               this.Effect.setTexture(var1.tex);
            } else {
               this.Effect.setTexture(var1.tex);
            }

            this.Effect.setAmbient(var3);
            this.Effect.setTint(var1.tintR, var1.tintG, var1.tintB);
            if (this.bStatic) {
               this.Effect.setTransformMatrix(var1.xfrm[var2]);
            }

            this.Mesh.Draw(this.Effect);
            this.Effect.End();
         }

         if (Core.bDebug && DebugOptions.instance.ModelRenderAxis.getValue() && var1.isVehicleBody) {
            for(int var11 = 0; var11 < 8; ++var11) {
               GL13.glActiveTexture('蓀' + var11);
               GL11.glDisable(3553);
            }

            GL11.glDisable(2929);
            GL11.glLineWidth(4.0F);
            GL11.glBegin(1);
            GL11.glColor3f(1.0F, 0.0F, 0.0F);
            GL11.glVertex3f(0.0F, 0.0F, 0.0F);
            GL11.glVertex3f(1.0F, 0.0F, 0.0F);
            GL11.glColor3f(0.0F, 1.0F, 0.0F);
            GL11.glVertex3f(0.0F, 0.0F, 0.0F);
            GL11.glVertex3f(0.0F, 1.0F, 0.0F);
            GL11.glColor3f(0.0F, 0.0F, 1.0F);
            GL11.glVertex3f(0.0F, 0.0F, 0.0F);
            GL11.glVertex3f(0.0F, 0.0F, 1.0F);
            GL11.glEnd();
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            GL11.glEnable(2929);
            GL13.glActiveTexture(33984);
            GL11.glEnable(3553);
         }

         ModelCamera.instance.End();
      } else if (var1.AnimPlayer != null) {
         if (var1.character.getCurrentSquare() != null) {
            synchronized(var1.lights) {
               GL11.glEnable(2884);
               GL11.glCullFace(1029);
               GL11.glEnable(2929);
               GL11.glDepthFunc(513);
               ModelCamera.instance.Begin(this);
               if (this.Effect != null) {
                  this.Effect.Start();
                  this.Effect.setMatrixPalette(var1.AnimPlayer.skinTransforms);
                  var3 = 1.0F;
                  if (!(var1.character instanceof DeadBodyAtlas.AtlasCharacter)) {
                     var3 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.instance.PlayerIndex);
                     if (var1.character.getCurrentSquare() != null && var1.character.getCurrentSquare().getRoom() != null) {
                        var3 *= 0.6F;
                     }
                  }

                  if (var1.lights[0] != null) {
                     this.Effect.setLight(0, (float)var1.lights[0].x, (float)var1.lights[0].y, (float)var1.lights[0].z, var1.lights[0].r, var1.lights[0].g, var1.lights[0].b, (float)var1.lights[0].radius, var1);
                  } else {
                     this.Effect.setLight(0, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, var1);
                  }

                  if (IsoPlayer.instance.getTorchStrength() > 0.0F) {
                     float var4 = IsoPlayer.instance.getX();
                     float var5 = IsoPlayer.instance.getY();
                     float var6 = IsoPlayer.instance.getZ();
                     var4 += IsoPlayer.instance.angle.x * 0.5F;
                     var5 += IsoPlayer.instance.angle.y * 0.5F;
                     var4 -= 0.5F;
                     var5 -= 0.5F;
                     var6 -= 0.5F;
                     this.Effect.setLight(1, var4, var5, var6, 1.0F, 0.85F, 0.6F, 16.0F, var1);
                  } else if (var1.lights[1] != null) {
                     this.Effect.setLight(1, (float)var1.lights[1].x, (float)var1.lights[1].y, (float)var1.lights[1].z, var1.lights[1].r, var1.lights[1].g, var1.lights[1].b, (float)var1.lights[1].radius, var1);
                  } else {
                     this.Effect.setLight(1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, var1);
                  }

                  if (var1.lights[2] != null) {
                     this.Effect.setLight(2, (float)var1.lights[2].x, (float)var1.lights[2].y, (float)var1.lights[2].z, var1.lights[2].r, var1.lights[2].g, var1.lights[2].b, (float)var1.lights[2].radius, var1);
                  } else {
                     this.Effect.setLight(2, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, var1);
                  }

                  this.Effect.setTexture(var1.tex);
                  this.Effect.setAmbient(var3);
               }

               this.Effect.setTint(var1.tintR, var1.tintG, var1.tintB);
               if (this.bStatic) {
                  this.Effect.setTexture(this.tex);
                  this.Effect.setTransformMatrix(var1.AnimPlayer.GetPropBoneMatrix());
               }

               this.Mesh.Draw(this.Effect);
               if (this.Effect != null) {
                  this.Effect.End();
               }

               if (Core.bDebug && DebugOptions.instance.ModelRenderAxis.getValue()) {
                  GL11.glLineWidth(4.0F);
                  GL11.glDisable(3553);
                  GL11.glDisable(2929);
                  GL11.glBegin(1);
                  GL11.glColor3f(1.0F, 0.0F, 0.0F);
                  GL11.glVertex3f(0.0F, 0.0F, 0.0F);
                  GL11.glVertex3f(1.0F, 0.0F, 0.0F);
                  GL11.glColor3f(0.0F, 1.0F, 0.0F);
                  GL11.glVertex3f(0.0F, 0.0F, 0.0F);
                  GL11.glVertex3f(0.0F, 1.0F, 0.0F);
                  GL11.glColor3f(0.0F, 0.0F, 1.0F);
                  GL11.glVertex3f(0.0F, 0.0F, 0.0F);
                  GL11.glVertex3f(0.0F, 0.0F, 1.0F);
                  GL11.glEnd();
                  GL11.glColor3f(1.0F, 1.0F, 1.0F);
                  GL11.glEnable(2929);
                  GL11.glLineWidth(1.0F);
               }

               if (Core.bDebug) {
               }

               ModelCamera.instance.End();
            }
         }
      }
   }

   public void CreateShader(String var1) {
      this.Effect = ShaderManager.instance.getOrCreateShader(var1, this.bStatic);
   }
}
