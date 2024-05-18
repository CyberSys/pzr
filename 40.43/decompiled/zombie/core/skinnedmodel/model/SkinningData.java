package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class SkinningData {
   public HashMap AnimationClips;
   public List BindPose;
   public List InverseBindPose;
   public List BoneOffset = new ArrayList();
   public List SkeletonHierarchy;
   public HashMap BoneIndices;

   public SkinningData(HashMap var1, List var2, List var3, List var4, List var5, HashMap var6) {
      this.AnimationClips = var1;
      this.BindPose = var2;
      this.InverseBindPose = var3;
      this.SkeletonHierarchy = var5;

      for(int var7 = 0; var7 < var5.size(); ++var7) {
         Matrix4f var8 = (Matrix4f)var4.get(var7);
         this.BoneOffset.add(var8);
      }

      this.BoneIndices = var6;
   }

   public void BindPoseCopyTo(Matrix4f[] var1, int var2) {
      for(int var3 = var2; var3 < this.BindPose.size(); ++var3) {
         var1[var3] = new Matrix4f((Matrix4fc)this.BindPose.get(var3));
      }

   }
}
