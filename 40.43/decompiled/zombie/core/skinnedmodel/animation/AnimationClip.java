package zombie.core.skinnedmodel.animation;

import java.util.List;

public class AnimationClip {
   public final String Name;
   public float Duration;
   public List Keyframes;
   public Keyframe[] KeyframeArray = new Keyframe[0];

   public AnimationClip(float var1, List var2, String var3) {
      this.Duration = var1;
      this.Keyframes = var2;
      this.KeyframeArray = (Keyframe[])this.Keyframes.toArray(this.KeyframeArray);
      this.Name = var3;
   }
}
