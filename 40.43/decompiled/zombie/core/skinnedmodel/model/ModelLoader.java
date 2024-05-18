package zombie.core.skinnedmodel.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import zombie.core.opengl.RenderThread;
import zombie.core.skinnedmodel.Vector3;
import zombie.core.skinnedmodel.Vector4;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.Keyframe;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.iso.Vector2;
import zombie.util.SharedStrings;

public class ModelLoader {
   public static ModelLoader instance = new ModelLoader();
   private SharedStrings sharedStrings = new SharedStrings();

   public Model Load(String var1, String var2, String var3, boolean var4) throws IOException {
      Model var5 = new Model(var4);
      RenderThread.borrowContext();
      ModelLoader.LoadMode var6 = ModelLoader.LoadMode.Version;
      VertexPositionNormalTangentTextureSkin[] var7 = null;
      VertexPositionNormalTangentTexture[] var8 = null;
      int[] var9 = null;
      HashMap var10 = new HashMap();
      ArrayList var11 = new ArrayList();
      ArrayList var12 = new ArrayList();
      ArrayList var13 = new ArrayList();
      ArrayList var14 = new ArrayList();
      BufferedReader var15 = new BufferedReader(new FileReader(var1));
      String var16 = null;
      int var17 = 0;
      int var18 = 0;
      int var19 = 0;
      boolean var20 = false;
      int var21 = 0;
      HashMap var22 = new HashMap();
      boolean var23 = false;

      while((var16 = var15.readLine()) != null) {
         try {
            if (var16.indexOf(35) != 0) {
               if (var16.contains("Tangent")) {
                  if (var4) {
                     var17 += 2;
                  }

                  var23 = true;
               }

               if (var17 > 0) {
                  --var17;
               } else {
                  String var25;
                  float var26;
                  int var27;
                  int var28;
                  float var32;
                  int var47;
                  String var49;
                  String[] var50;
                  int var51;
                  String var52;
                  switch(var6) {
                  case Version:
                     var6 = ModelLoader.LoadMode.ModelName;
                     break;
                  case ModelName:
                     var6 = ModelLoader.LoadMode.VertexStrideElementCount;
                     break;
                  case VertexStrideElementCount:
                     var6 = ModelLoader.LoadMode.VertexCount;
                     if (var4) {
                        var17 = 7;
                     } else {
                        var17 = 13;
                     }
                     break;
                  case VertexCount:
                     var18 = Integer.parseInt(var16);
                     var6 = ModelLoader.LoadMode.VertexBuffer;
                     var7 = new VertexPositionNormalTangentTextureSkin[var18];
                     var8 = new VertexPositionNormalTangentTexture[var18];
                     break;
                  case VertexBuffer:
                     var47 = 0;

                     for(; var47 < var18; ++var47) {
                        var50 = var16.split(",");
                        var26 = Float.parseFloat(var50[0].trim());
                        float var55 = Float.parseFloat(var50[1].trim());
                        float var56 = Float.parseFloat(var50[2].trim());
                        var16 = var15.readLine();
                        var50 = var16.split(",");
                        float var57 = Float.parseFloat(var50[0].trim());
                        float var58 = Float.parseFloat(var50[1].trim());
                        float var59 = Float.parseFloat(var50[2].trim());
                        var32 = 0.0F;
                        float var60 = 0.0F;
                        float var61 = 0.0F;
                        if (var23) {
                           var16 = var15.readLine();
                           var50 = var16.split(",");
                           var32 = Float.parseFloat(var50[0].trim());
                           var60 = Float.parseFloat(var50[1].trim());
                           var61 = Float.parseFloat(var50[2].trim());
                        }

                        var16 = var15.readLine();
                        var50 = var16.split(",");
                        float var62 = Float.parseFloat(var50[0].trim());
                        float var36 = Float.parseFloat(var50[1].trim());
                        float var37 = 0.0F;
                        float var38 = 0.0F;
                        float var39 = 0.0F;
                        float var40 = 0.0F;
                        int var41 = 0;
                        int var42 = 0;
                        int var43 = 0;
                        int var44 = 0;
                        if (!var4) {
                           var16 = var15.readLine();
                           var50 = var16.split(",");
                           var37 = Float.parseFloat(var50[0].trim());
                           var38 = Float.parseFloat(var50[1].trim());
                           var39 = Float.parseFloat(var50[2].trim());
                           var40 = Float.parseFloat(var50[3].trim());
                           var16 = var15.readLine();
                           var50 = var16.split(",");
                           var41 = Integer.parseInt(var50[0].trim());
                           var42 = Integer.parseInt(var50[1].trim());
                           var43 = Integer.parseInt(var50[2].trim());
                           var44 = Integer.parseInt(var50[3].trim());
                        }

                        var16 = var15.readLine();
                        if (!var4) {
                           var7[var47] = new VertexPositionNormalTangentTextureSkin(new Vector3(var26, var55, var56), new Vector3(var57, var58, var59), new Vector3(var32, var60, var61), new Vector2(var62, var36), new Vector4(var37, var38, var39, var40), new UInt4(var41, var42, var43, var44));
                        } else {
                           var8[var47] = new VertexPositionNormalTangentTexture(new Vector3(var26, var55, var56), new Vector3(var57, var58, var59), new Vector3(var32, var60, var61), new Vector2(var62, var36));
                        }
                     }

                     var6 = ModelLoader.LoadMode.NumberOfFaces;
                     break;
                  case NumberOfFaces:
                     var19 = Integer.parseInt(var16);
                     var9 = new int[var19 * 3];
                     var6 = ModelLoader.LoadMode.FaceData;
                     break;
                  case FaceData:
                     for(var47 = 0; var47 < var19; ++var47) {
                        var50 = var16.split(",");
                        var51 = Integer.parseInt(var50[0].trim());
                        var27 = Integer.parseInt(var50[1].trim());
                        var28 = Integer.parseInt(var50[2].trim());
                        var9[var47 * 3] = var51;
                        var9[var47 * 3 + 1] = var27;
                        var9[var47 * 3 + 2] = var28;
                        var16 = var15.readLine();
                     }

                     var6 = ModelLoader.LoadMode.NumberOfBones;
                     break;
                  case NumberOfBones:
                     var21 = Integer.parseInt(var16);
                     var6 = ModelLoader.LoadMode.SkeletonHierarchy;
                     break;
                  case SkeletonHierarchy:
                     for(var47 = 0; var47 < var21; ++var47) {
                        int var48 = Integer.parseInt(var16);
                        var16 = var15.readLine();
                        var51 = Integer.parseInt(var16);
                        var16 = var15.readLine();
                        var52 = this.sharedStrings.get(var16);
                        var16 = var15.readLine();
                        var11.add(var51);
                        var10.put(var52, var48);
                     }

                     var6 = ModelLoader.LoadMode.BindPose;
                     break;
                  case BindPose:
                     for(var47 = 0; var47 < var21; ++var47) {
                        var16 = var15.readLine();
                        var25 = var15.readLine();
                        var49 = var15.readLine();
                        var52 = var15.readLine();
                        var12.add(var47, this.getMatrix(var16, var25, var49, var52));
                        var16 = var15.readLine();
                     }

                     var6 = ModelLoader.LoadMode.InvBindPose;
                     break;
                  case InvBindPose:
                     for(var47 = 0; var47 < var21; ++var47) {
                        var16 = var15.readLine();
                        var25 = var15.readLine();
                        var49 = var15.readLine();
                        var52 = var15.readLine();
                        var14.add(var47, this.getMatrix(var16, var25, var49, var52));
                        var16 = var15.readLine();
                     }

                     var6 = ModelLoader.LoadMode.SkinOffsetMatrices;
                     break;
                  case SkinOffsetMatrices:
                     for(var47 = 0; var47 < var21; ++var47) {
                        var16 = var15.readLine();
                        var25 = var15.readLine();
                        var49 = var15.readLine();
                        var52 = var15.readLine();
                        var13.add(var47, this.getMatrix(var16, var25, var49, var52));
                        var16 = var15.readLine();
                     }

                     var6 = ModelLoader.LoadMode.NumberOfAnims;
                     break;
                  case NumberOfAnims:
                     int var46 = Integer.parseInt(var16);
                     var6 = ModelLoader.LoadMode.Anim;
                     break;
                  case Anim:
                     ArrayList var24 = new ArrayList();
                     var25 = var16;
                     var16 = var15.readLine();
                     var26 = Float.parseFloat(var16);
                     var16 = var15.readLine();
                     var27 = Integer.parseInt(var16);
                     var16 = var15.readLine();

                     for(var28 = 0; var28 < var27; ++var28) {
                        Keyframe var29 = new Keyframe();
                        int var30 = Integer.parseInt(var16);
                        var16 = var15.readLine();
                        String var31 = this.sharedStrings.get(var16);
                        var16 = var15.readLine();
                        var32 = Float.parseFloat(var16);
                        var16 = var15.readLine();
                        String var33 = var15.readLine();
                        Vector3f var34 = this.getVector(var16);
                        Quaternionf var35 = this.getQuaternion(var33);
                        if (var28 < var27 - 1) {
                           var16 = var15.readLine();
                        }

                        var29.Bone = var30;
                        var29.BoneName = var31;
                        var29.Time = var32;
                        var29.Rotation = var35;
                        var29.Position = var34;
                        var24.add(var29);
                     }

                     AnimationClip var54 = new AnimationClip(var26, var24, var25);
                     var22.put(var25, var54);
                  }
               }
            }
         } catch (Exception var45) {
            var45.printStackTrace();
         }
      }

      var15.close();
      if (!var4) {
         SkinningData var53 = new SkinningData(var22, var12, var14, var13, var11, var10);
         var5.Tag = var53;
         var5.Mesh.SetVertexBuffer(new VertexBufferObject(var7, var9));
      } else {
         var5.Mesh.SetVertexBuffer(new VertexBufferObject(var8, var9));
      }

      var5.CreateShader(var3);
      RenderThread.returnContext();
      TextureID.bUseCompression = false;
      var5.tex = Texture.getSharedTexture("media/textures/" + var2 + ".png");
      TextureID.bUseCompression = TextureID.bUseCompressionOption;
      return var5;
   }

   private Vector3f getVector(String var1) {
      Vector3f var2 = new Vector3f();
      String[] var3 = var1.split(",");
      var2.x = Float.parseFloat(var3[0]);
      var2.y = Float.parseFloat(var3[1]);
      var2.z = Float.parseFloat(var3[2]);
      return var2;
   }

   private Quaternionf getQuaternion(String var1) {
      Quaternionf var2 = new Quaternionf();
      String[] var3 = var1.split(",");
      var2.x = Float.parseFloat(var3[0]);
      var2.y = Float.parseFloat(var3[1]);
      var2.z = Float.parseFloat(var3[2]);
      var2.w = Float.parseFloat(var3[3]);
      return var2;
   }

   private Matrix4f getMatrix(String var1, String var2, String var3, String var4) {
      boolean var5 = false;
      String[] var6 = var1.split(",");
      float var7 = Float.parseFloat(var6[0]);
      float var8 = Float.parseFloat(var6[1]);
      float var9 = Float.parseFloat(var6[2]);
      float var10 = Float.parseFloat(var6[3]);
      var6 = var2.split(",");
      float var11 = Float.parseFloat(var6[0]);
      float var12 = Float.parseFloat(var6[1]);
      float var13 = Float.parseFloat(var6[2]);
      float var14 = Float.parseFloat(var6[3]);
      var6 = var3.split(",");
      float var15 = Float.parseFloat(var6[0]);
      float var16 = Float.parseFloat(var6[1]);
      float var17 = Float.parseFloat(var6[2]);
      float var18 = Float.parseFloat(var6[3]);
      var6 = var4.split(",");
      float var19 = Float.parseFloat(var6[0]);
      float var20 = Float.parseFloat(var6[1]);
      float var21 = Float.parseFloat(var6[2]);
      float var22 = Float.parseFloat(var6[3]);
      return new Matrix4f(var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, var19, var20, var21, var22);
   }

   public static enum LoadMode {
      Version,
      ModelName,
      VertexStrideElementCount,
      VertexStrideSize,
      VertexStrideData,
      VertexCount,
      VertexBuffer,
      NumberOfFaces,
      FaceData,
      NumberOfBones,
      SkeletonHierarchy,
      BindPose,
      InvBindPose,
      SkinOffsetMatrices,
      NumberOfAnims,
      Anim;
   }
}
