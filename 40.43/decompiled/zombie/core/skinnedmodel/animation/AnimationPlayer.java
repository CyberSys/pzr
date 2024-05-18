package zombie.core.skinnedmodel.animation;

import java.util.ArrayList;
import java.util.Stack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import zombie.GameTime;
import zombie.core.skinnedmodel.model.SkinningData;

public class AnimationPlayer {
   public Matrix4f propTransforms = new Matrix4f();
   public Matrix4f[] boneTransforms;
   public Matrix4f[] worldTransforms;
   public Matrix4f[] skinTransforms;
   public SkinningData skinningDataValue;
   public float angle;
   public float targetAngle;
   public float angleStep = 0.15F;
   private static Matrix4f tempMatrix4f = new Matrix4f();
   int propBone = -1;
   public static Stack freeTracks = new Stack();
   private static Keyframe tempKeyframe;
   private static Quaternionf tempQuat = new Quaternionf();
   private static Vector3f tempVec3f = new Vector3f();
   public ArrayList Tracks = new ArrayList();
   static Matrix4f Identity = new Matrix4f();

   public AnimationPlayer(SkinningData var1) {
      if (var1 == null) {
         throw new NullPointerException("skinningData");
      } else {
         this.skinningDataValue = var1;
         if (this.skinningDataValue.BoneIndices.containsKey("Bip01_Prop1")) {
            this.propBone = (Integer)this.skinningDataValue.BoneIndices.get("Bip01_Prop1");
         }

         this.boneTransforms = new Matrix4f[var1.BindPose.size()];
         this.worldTransforms = new Matrix4f[var1.BindPose.size()];
         this.skinTransforms = new Matrix4f[var1.BindPose.size()];

         for(int var2 = 0; var2 < var1.BindPose.size(); ++var2) {
            this.boneTransforms[var2] = new Matrix4f();
            this.worldTransforms[var2] = new Matrix4f();
            this.skinTransforms[var2] = new Matrix4f();
         }

      }
   }

   public Matrix4f GetPropBoneMatrix() {
      return this.propTransforms;
   }

   public AnimationTrack StartClip(AnimationClip var1, boolean var2, boolean var3, boolean var4) {
      if (var1 == null) {
         return null;
      } else {
         AnimationTrack var6;
         if (this.Tracks.size() > 0) {
            for(int var5 = 0; var5 < this.Tracks.size(); ++var5) {
               var6 = (AnimationTrack)this.Tracks.get(var5);
               if (var6.CurrentClip != var1) {
                  var6.mode = AnimationTrack.Mode.Out;
               }
            }
         }

         AnimationTrack var7 = this.getTrackPlaying(var1);
         if (var7 != null) {
            if (var7.mode == AnimationTrack.Mode.Out) {
               var7.mode = AnimationTrack.Mode.In;
               var7.currentKeyframe = 0;
               var7.currentTimeValue = 0.0F;
               var7.BlendCurrentTime = var7.BlendTime - var7.BlendCurrentTime;
               var7.bFinished = false;
            }

            var7.bAnim = !var3;
            var7.StopOnFrameOneAfterLoop = var4;
            var7.bLooping = var2;
            return var7;
         } else {
            var6 = freeTracks.isEmpty() ? new AnimationTrack() : (AnimationTrack)freeTracks.pop();
            var6.StartClip(var1, var2, var3);
            var6.bAnim = !var3;
            this.Tracks.add(var6);
            return var6;
         }
      }
   }

   private AnimationTrack getTrackPlaying(AnimationClip var1) {
      for(int var2 = 0; var2 < this.Tracks.size(); ++var2) {
         AnimationTrack var3 = (AnimationTrack)this.Tracks.get(var2);
         if (var3.CurrentClip == var1) {
            return var3;
         }
      }

      return null;
   }

   private boolean isPlaying(AnimationClip var1) {
      for(int var2 = 0; var2 < this.Tracks.size(); ++var2) {
         AnimationTrack var3 = (AnimationTrack)this.Tracks.get(var2);
         if (var3.CurrentClip == var1) {
            return true;
         }
      }

      return false;
   }

   public void Update(float var1, boolean var2, Matrix4f var3) {
      long var4 = System.nanoTime();
      float var6 = this.angle - this.targetAngle;
      float var7 = this.angle - (this.targetAngle + 6.2831855F);
      float var8 = this.angle - (this.targetAngle - 6.2831855F);

      for(int var9 = 0; var9 < this.Tracks.size(); ++var9) {
         AnimationTrack var10 = (AnimationTrack)this.Tracks.get(var9);
         var10.Update(this, var1, var2, var3);
         if (var10.bFinished) {
            this.Tracks.remove(var9);
            freeTracks.push(var10.reset());
            --var9;
         }
      }

      if (this.angle != this.targetAngle) {
         if (Math.abs(var6) <= Math.abs(var7) && Math.abs(var6) <= Math.abs(var8)) {
            if (this.angle < this.targetAngle) {
               this.angle += this.angleStep * GameTime.instance.getMultiplier();
               if (this.angle > this.targetAngle) {
                  this.angle = this.targetAngle;
               }
            }

            if (this.angle > this.targetAngle) {
               this.angle -= this.angleStep * GameTime.instance.getMultiplier();
               if (this.angle < this.targetAngle) {
                  this.angle = this.targetAngle;
               }
            }

            if ((double)this.angle > 6.283185307179586D) {
               this.angle = (float)((double)this.angle - 6.283185307179586D);
            }

            if (this.angle < 0.0F) {
               this.angle = (float)((double)this.angle + 6.283185307179586D);
            }
         } else {
            float var12;
            float var13;
            if (Math.abs(var7) < Math.abs(var6) && Math.abs(var7) < Math.abs(var8)) {
               var12 = var7 < 0.0F ? 1.0F : -1.0F;
               var12 *= this.angleStep * GameTime.instance.getMultiplier();
               var13 = this.angle;
               this.angle += var12;
            } else if (Math.abs(var8) < Math.abs(var6) && Math.abs(var8) < Math.abs(var7)) {
               var12 = var8 < 0.0F ? 1.0F : -1.0F;
               var12 *= this.angleStep * GameTime.instance.getMultiplier();
               var13 = this.angle;
               this.angle += var12;
            }
         }

         if ((double)this.angle > 6.283185307179586D) {
            this.angle = (float)((double)this.angle - 6.283185307179586D);
         } else if (this.angle < 0.0F) {
            this.angle = (float)((double)this.angle + 6.283185307179586D);
         }
      }

      try {
         this.UpdateBoneTransforms(var1, var2);
         this.UpdateWorldTransforms(var3);
         this.UpdateSkinTransforms();
      } catch (Exception var11) {
         var11.printStackTrace();
      }

      this.propTransforms.set((Matrix4fc)this.worldTransforms[this.propBone]);
   }

   public void UpdateBoneTransforms(float var1, boolean var2) {
      if (this.Tracks.isEmpty()) {
         throw new RuntimeException("AnimationPlayer.Update was called before StartClip");
      } else {
         for(int var3 = 0; var3 < this.boneTransforms.length; ++var3) {
            Keyframe var4 = this.interpolateKeyframe(var3);
            this.boneTransforms[var3].rotation((Quaternionfc)var4.Rotation).transpose();
            tempMatrix4f.translation(var4.Position).transpose();
            this.boneTransforms[var3].mulGeneric(tempMatrix4f, this.boneTransforms[var3]);
         }

      }
   }

   public static final Vector3f slerp(Vector3f var0, Vector3f var1, Vector3f var2, float var3, boolean var4) {
      float var5 = var2.x - var1.x;
      float var6 = var2.y - var1.y;
      float var7 = var2.z - var1.z;
      var5 *= var3;
      var6 *= var3;
      var7 *= var3;
      var0.set(var1.x + var5, var1.y + var6, var1.z + var7);
      return var0;
   }

   public static final Quaternionf slerp(Quaternionf var0, Quaternionf var1, Quaternionf var2, float var3, boolean var4) {
      float var5 = var1.dot(var2);
      double var6;
      double var8;
      if (1.0D - (double)Math.abs(var5) < 0.01D) {
         var6 = (double)(1.0F - var3);
         var8 = (double)var3;
      } else {
         double var10 = Math.acos((double)Math.abs(var5));
         double var12 = Math.sin(var10);
         var6 = Math.sin(var10 * (double)(1.0F - var3)) / var12;
         var8 = Math.sin(var10 * (double)var3) / var12;
      }

      if (var4 && (double)var5 < 0.0D) {
         var6 = -var6;
      }

      var0.set((float)(var6 * (double)var1.x + var8 * (double)var2.x), (float)(var6 * (double)var1.y + var8 * (double)var2.y), (float)(var6 * (double)var1.z + var8 * (double)var2.z), (float)(var6 * (double)var1.w + var8 * (double)var2.w));
      return var0;
   }

   private Keyframe interpolateKeyframe(int var1) {
      if (tempKeyframe == null) {
         tempKeyframe = new Keyframe();
         tempKeyframe.Rotation = new Quaternionf();
         tempKeyframe.Position = new Vector3f();
      }

      Keyframe var2 = tempKeyframe;
      ((AnimationTrack)this.Tracks.get(0)).getRotation(var2.Rotation, var1);
      ((AnimationTrack)this.Tracks.get(0)).getPosition(var2.Position, var1);

      for(int var3 = 1; var3 < this.Tracks.size(); ++var3) {
         AnimationTrack var4 = (AnimationTrack)this.Tracks.get(var3);
         float var5 = var4.BlendDelta;
         var2.Rotation = slerp(var2.Rotation, var2.Rotation, var4.getRotation(tempQuat, var1), var5, true);
         var2.Position = slerp(var2.Position, var2.Position, var4.getPosition(tempVec3f, var1), var5, true);
      }

      return var2;
   }

   public void UpdateWorldTransforms(Matrix4f var1) {
      Identity.identity();
      tempVec3f.set(0.0F, 1.0F, 0.0F);
      Identity.rotate(-this.angle, tempVec3f);
      this.boneTransforms[0].mul((Matrix4fc)Identity, this.worldTransforms[0]);

      for(int var2 = 1; var2 < this.worldTransforms.length; ++var2) {
         int var3 = (Integer)this.skinningDataValue.SkeletonHierarchy.get(var2);
         this.boneTransforms[var2].mul((Matrix4fc)this.worldTransforms[var3], this.worldTransforms[var2]);
      }

   }

   public void UpdateSkinTransforms() {
      for(int var1 = 0; var1 < this.worldTransforms.length; ++var1) {
         ((Matrix4f)this.skinningDataValue.BoneOffset.get(var1)).mul((Matrix4fc)this.worldTransforms[var1], this.skinTransforms[var1]);
      }

   }

   public void ResetToFrameOne() {
      if (!this.Tracks.isEmpty()) {
         ((AnimationTrack)this.Tracks.get(0)).bAnim = true;
         ((AnimationTrack)this.Tracks.get(0)).currentKeyframe = 0;
         ((AnimationTrack)this.Tracks.get(0)).currentTimeValue = 0.0F;
      }

   }

   public AnimationTrack getAnimTrack(String var1) {
      for(int var2 = 0; var2 < this.Tracks.size(); ++var2) {
         AnimationTrack var3 = (AnimationTrack)this.Tracks.get(var2);
         if (var3.CurrentClip.Name.equals(var1)) {
            return var3;
         }
      }

      return null;
   }
}
