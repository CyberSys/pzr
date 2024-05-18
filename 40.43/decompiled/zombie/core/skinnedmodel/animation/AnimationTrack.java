package zombie.core.skinnedmodel.animation;

import java.util.List;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import zombie.GameTime;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.iso.sprite.IsoAnim;
import zombie.iso.sprite.IsoSpriteInstance;

public class AnimationTrack {
   public AnimationClip CurrentClip;
   public float currentTimeValue;
   int currentKeyframe;
   public boolean reverse;
   public boolean bLooping = true;
   public Keyframe[] Pose = new Keyframe[60];
   public static Keyframe[] NextPose = new Keyframe[60];
   public Keyframe[] PrevPose = new Keyframe[60];
   public float SpeedDelta = 1.0F;
   public float BlendDelta = 0.0F;
   public float BlendTime = 0.1F;
   public float BlendCurrentTime = 0.0F;
   public AnimationTrack.Mode mode;
   public boolean bFinished;
   public boolean bAnim;
   public boolean StopOnFrameOneAfterLoop;

   public AnimationTrack() {
      this.mode = AnimationTrack.Mode.In;
      this.bFinished = false;
      this.bAnim = true;
      this.StopOnFrameOneAfterLoop = false;
   }

   public void syncToFrame(IsoSpriteInstance var1, IsoAnim var2) {
      this.currentTimeValue = var1.Frame / (float)var2.Frames.size();
      this.currentTimeValue *= this.CurrentClip.Duration;
      this.currentKeyframe = 0;
   }

   public Quaternionf getRotation(Quaternionf var1, int var2) {
      if (this.PrevPose[var2] != null && PerformanceSettings.InterpolateAnims) {
         float var3 = (this.currentTimeValue - this.PrevPose[var2].Time) / (this.Pose[var2].Time - this.PrevPose[var2].Time);
         if (this.Pose[var2].Time - this.PrevPose[var2].Time == 0.0F) {
            var3 = 0.0F;
         }

         return AnimationPlayer.slerp(var1, this.PrevPose[var2].Rotation, this.Pose[var2].Rotation, var3, true);
      } else {
         var1.set((Quaternionfc)this.Pose[var2].Rotation);
         return var1;
      }
   }

   public Vector3f getPosition(Vector3f var1, int var2) {
      if (this.PrevPose[var2] != null && PerformanceSettings.InterpolateAnims) {
         float var3 = (this.currentTimeValue - this.PrevPose[var2].Time) / (this.Pose[var2].Time - this.PrevPose[var2].Time);
         if (this.Pose[var2].Time - this.PrevPose[var2].Time == 0.0F) {
            var3 = 0.0F;
         }

         AnimationPlayer.slerp(var1, this.PrevPose[var2].Position, this.Pose[var2].Position, var3, true);
         return var1;
      } else {
         var1.set((Vector3fc)this.Pose[var2].Position);
         return var1;
      }
   }

   public float get2DFrame(IsoAnim var1) {
      return this.currentTimeValue / this.CurrentClip.Duration * (float)var1.Frames.size();
   }

   public void Update(AnimationPlayer var1, float var2, boolean var3, Matrix4f var4) {
      float var5 = var2 * GameTime.instance.getUnmoddedMultiplier();
      var2 *= this.CurrentClip.Duration * GameTime.instance.getUnmoddedMultiplier();
      var2 *= this.SpeedDelta;

      try {
         this.UpdateKeyframes(var2, var5, var3);
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }

   public void UpdateKeyframes(float var1, float var2, boolean var3) {
      if (this.CurrentClip == null) {
         throw new RuntimeException("AnimationPlayer.Update was called before StartClip");
      } else {
         if (this.mode == AnimationTrack.Mode.In || this.mode == AnimationTrack.Mode.Out) {
            this.BlendCurrentTime += var2;
            if (this.BlendCurrentTime > this.BlendTime) {
               if (this.mode == AnimationTrack.Mode.In) {
                  this.BlendCurrentTime = 0.0F;
                  this.mode = AnimationTrack.Mode.During;
               } else {
                  this.BlendCurrentTime = this.BlendTime;
                  this.bFinished = true;
               }
            }
         }

         if (this.mode == AnimationTrack.Mode.In) {
            this.BlendDelta = this.BlendCurrentTime / this.BlendTime;
         } else if (this.mode == AnimationTrack.Mode.Out) {
            this.BlendDelta = 1.0F - this.BlendCurrentTime / this.BlendTime;
            if (this.BlendDelta < 0.15F) {
               boolean var4 = false;
            }
         } else {
            this.BlendDelta = 1.0F;
         }

         if (!this.bAnim) {
            var1 = 0.0F;
         }

         if (var3) {
            if (this.reverse) {
               var1 = -var1;
               var1 += this.currentTimeValue;
            } else {
               var1 += this.currentTimeValue;
            }
         }

         if (!this.bAnim && this.StopOnFrameOneAfterLoop) {
            var1 = 0.0F;
         } else if (!this.bAnim && !this.StopOnFrameOneAfterLoop) {
            var1 = this.CurrentClip.Duration;
         }

         if (!this.bLooping) {
            if (var1 >= this.CurrentClip.Duration) {
               if (this.StopOnFrameOneAfterLoop) {
                  var1 = 0.0F;
               } else {
                  var1 = this.CurrentClip.Duration;
               }
            } else if (var1 < 0.0F) {
               if (this.StopOnFrameOneAfterLoop) {
                  var1 = this.CurrentClip.Duration;
               } else {
                  var1 = 0.0F;
               }
            }
         } else {
            while(var1 >= this.CurrentClip.Duration) {
               var1 -= this.CurrentClip.Duration;
            }

            while(var1 < 0.0F) {
               var1 += this.CurrentClip.Duration;
            }
         }

         if (this.reverse) {
            this.currentKeyframe = 0;
         } else if (var1 < this.currentTimeValue) {
            this.currentKeyframe = 0;
         }

         this.currentTimeValue = var1;

         for(List var8 = this.CurrentClip.Keyframes; this.currentKeyframe < var8.size(); ++this.currentKeyframe) {
            Keyframe var5 = (Keyframe)var8.get(this.currentKeyframe);
            if (this.currentKeyframe == var8.size() - 1 || var5.Time > this.currentTimeValue) {
               if (PerformanceSettings.InterpolateAnims) {
                  for(int var6 = 0; var6 < 60; ++var6) {
                     if (this.Pose[var6] == null || this.currentTimeValue >= this.Pose[var6].Time) {
                        Keyframe var7 = this.getNextKeyFrame(var6, this.currentKeyframe, this.Pose[var6]);
                        if (var7 != null) {
                           this.PrevPose[var7.Bone] = this.Pose[var7.Bone];
                           this.Pose[var7.Bone] = var7;
                        } else {
                           this.PrevPose[var6] = null;
                        }
                     }
                  }
               }
               break;
            }

            if (var5.Bone >= 0) {
               this.Pose[var5.Bone] = var5;
            }
         }

      }
   }

   private Keyframe getNextKeyFrame(int var1, int var2, Keyframe var3) {
      for(int var4 = var2; var4 < this.CurrentClip.KeyframeArray.length; ++var4) {
         Keyframe var5 = this.CurrentClip.KeyframeArray[var4];
         if (var5.Bone == var1 && var5.Time > this.currentTimeValue && var3 != var5) {
            return var5;
         }
      }

      return null;
   }

   public void StartClip(AnimationClip var1, boolean var2, boolean var3) {
      if (var1 != null) {
         if (this.CurrentClip != var1) {
            this.bLooping = var2;
            this.CurrentClip = var1;
            this.currentTimeValue = 0.0F;
            this.currentKeyframe = 0;
            this.bAnim = !var3;
            if (var1.Name.contains("ZombieWalk")) {
               this.currentTimeValue = (float)Rand.Next((int)var1.Duration * 10000) / 10000.0F * var1.Duration;
            }

         }
      }
   }

   public AnimationTrack reset() {
      this.CurrentClip = null;
      this.currentTimeValue = 0.0F;
      this.currentKeyframe = 0;
      this.reverse = false;
      this.bLooping = true;

      for(int var1 = 0; var1 < this.Pose.length; ++var1) {
         this.Pose[var1] = null;
         this.PrevPose[var1] = null;
      }

      this.SpeedDelta = 1.0F;
      this.BlendDelta = 0.0F;
      this.BlendTime = 0.1F;
      this.BlendCurrentTime = 0.0F;
      this.mode = AnimationTrack.Mode.In;
      this.bFinished = false;
      this.bAnim = true;
      this.StopOnFrameOneAfterLoop = false;
      return this;
   }

   public static enum Mode {
      In,
      During,
      Out;
   }
}
