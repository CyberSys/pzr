package zombie.core.skinnedmodel.model;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import zombie.ai.states.ZombieStandState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.textures.Texture;
import zombie.core.utils.OnceEvery;
import zombie.debug.DebugLog;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.Vector2;

public class ModelInstance {
   public Model model;
   public AnimationPlayer AnimPlayer;
   SkinningData data;
   public Texture tex;
   public Texture textureRust = null;
   public Texture textureMask = null;
   public Texture textureLights = null;
   public Texture textureDamage1Overlay = null;
   public Texture textureDamage1Shell = null;
   public Texture textureDamage2Overlay = null;
   public Texture textureDamage2Shell = null;
   public boolean isVehicleBody = false;
   public boolean isVehicleWheel = false;
   public Matrix4f textureUninstall1 = new Matrix4f(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   public Matrix4f textureUninstall2 = new Matrix4f(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   public Matrix4f textureLightsEnables2 = new Matrix4f(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   public Matrix4f textureDamage1Enables1 = new Matrix4f(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   public Matrix4f textureDamage1Enables2 = new Matrix4f(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   public Matrix4f textureDamage2Enables1 = new Matrix4f(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   public Matrix4f textureDamage2Enables2 = new Matrix4f(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   public float textureRustA = 0.0F;
   public float refWindows = 0.5F;
   public float refBody = 0.4F;
   public float alpha = 0.0F;
   public Vector3f painColor = new Vector3f(0.0F, 0.5F, 0.5F);
   public IsoGameCharacter character;
   public IsoMovingObject object;
   public Vector3f[] origin = new Vector3f[3];
   public Matrix4f[] xfrm = new Matrix4f[3];
   public Vector3f[] worldPos = new Vector3f[3];
   public boolean updateLights;
   public float tintR = 1.0F;
   public float tintG = 1.0F;
   public float tintB = 1.0F;
   String lastAnimName = "";
   OnceEvery lightCheck = new OnceEvery(1.3F, true);
   public IsoLightSource[] lights = new IsoLightSource[3];

   public ModelInstance(Model var1, IsoGameCharacter var2, AnimationPlayer var3, boolean var4, boolean var5) {
      this.data = (SkinningData)var1.Tag;
      this.model = var1;
      this.tex = var1.tex;
      if (!var1.bStatic) {
         if (var3 == null) {
            var3 = new AnimationPlayer(this.data);
         }

         this.AnimPlayer = var3;
      }

      this.character = var2;
      this.object = var2;
      this.isVehicleBody = var4;
      this.isVehicleWheel = var5;
   }

   public void LoadTexture(String var1) {
      this.tex = Texture.getSharedTexture("media/textures/" + var1 + ".png");
      if (this.tex == null) {
         if (var1.equals("Vest_White")) {
            this.tex = Texture.getSharedTexture("media/textures/Shirt_White.png");
         } else if (var1.contains("Hair")) {
            this.tex = Texture.getSharedTexture("media/textures/F_Hair_White.png");
         } else if (var1.contains("Beard")) {
            this.tex = Texture.getSharedTexture("media/textures/F_Hair_White.png");
         } else {
            DebugLog.log("ERROR: model texture \"" + var1 + "\" wasn't found");
            boolean var2 = false;
         }
      }

   }

   public void Draw() {
      this.model.Draw(this);
   }

   public AnimationTrack Play(String var1, boolean var2, boolean var3, IsoGameCharacter var4) {
      if (this.model.bStatic) {
         return null;
      } else {
         boolean var5 = var4.legsSprite.CurrentAnim.FinishUnloopedOnFrame == 0;
         if (this.AnimPlayer == null) {
            this.AnimPlayer = new AnimationPlayer((SkinningData)this.model.Tag);
         }

         if (this.data != null) {
            AnimationTrack var6;
            if (var1.endsWith("_R")) {
               var1 = var1.substring(0, var1.length() - 2);
               var6 = this.AnimPlayer.StartClip((AnimationClip)this.data.AnimationClips.get(var1), var2, var3, var5);
               if (var6 != null) {
                  if (!this.lastAnimName.equals(var1)) {
                     var6.syncToFrame(var4.def, var4.legsSprite.CurrentAnim);
                  }

                  var6.reverse = true;
               }

               this.lastAnimName = var1;
               return var6;
            } else {
               var6 = this.AnimPlayer.StartClip((AnimationClip)this.data.AnimationClips.get(var1), var2, var3, var5);
               if (var6 != null) {
                  if (!this.lastAnimName.equals(var1)) {
                     var6.syncToFrame(var4.def, var4.legsSprite.CurrentAnim);
                  }

                  var6.reverse = false;
               }

               this.lastAnimName = var1;
               return var6;
            }
         } else {
            return null;
         }
      }
   }

   public void UpdateDir() {
      if (this.AnimPlayer != null) {
         this.SetDir(this.character.angle);
         if (this.character instanceof IsoZombie) {
            if (!this.character.IgnoreMovementForDirection && !((IsoZombie)this.character).bCrawling) {
               if (this.character.reqMovement.getLength() > 0.0F && this.character.getCurrentState() != ZombieStandState.instance()) {
                  this.character.DirectionFromVector(this.character.reqMovement);
                  this.SetDir(this.character.reqMovement);
               } else if (this.character.getCurrentState() != ZombieStandState.instance()) {
                  this.SetDir(this.character.dir.ToVector());
               }
            } else {
               this.SetDir(this.character.dir.ToVector());
            }
         }

      }
   }

   public void Update(float var1) {
      if (this.AnimPlayer != null && !this.AnimPlayer.Tracks.isEmpty()) {
         this.AnimPlayer.Update(var1, true, (Matrix4f)null);
      }

   }

   private void attachToBone(int var1, ModelInstance var2) {
   }

   public void SetDir(Vector2 var1) {
      ModelCamera.instance.setDir(var1, this);
   }
}
