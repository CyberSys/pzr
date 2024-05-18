package zombie.core.skinnedmodel.shader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.IndieFileLoader;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.textures.Texture;

public class Shader {
   public String name;
   private int TransformMatrixID = 0;
   public int ShaderID = 0;
   public int FragID = 0;
   public int VertID = 0;
   int MatrixID = 0;
   int Light0Direction;
   int Light0Colour;
   int Light1Direction;
   int Light1Colour;
   int Light2Direction;
   int Light2Colour;
   int Ambient;
   int TintColour;
   int Texture0;
   int TexturePainColor;
   int TextureRust;
   int TextureRustA;
   int TextureMask;
   int TextureLights;
   int TextureDamage1Overlay;
   int TextureDamage1Shell;
   int TextureDamage2Overlay;
   int TextureDamage2Shell;
   int TextureUninstall1;
   int TextureUninstall2;
   int TextureLightsEnables2;
   int TextureDamage1Enables1;
   int TextureDamage1Enables2;
   int TextureDamage2Enables1;
   int TextureDamage2Enables2;
   int Alpha;
   int TextureReflectionA;
   int TextureReflectionB;
   int ReflectionParam;
   public int BoneIndicesAttrib;
   public int BoneWeightsAttrib;
   Texture tex;
   boolean bStatic = false;
   static FloatBuffer floatBuffer;
   private static Vector3f tempVec3f = new Vector3f();
   private FloatBuffer floatBuffer2 = BufferUtils.createFloatBuffer(16);

   public Shader(String var1, boolean var2) {
      this.ShaderID = ARBShaderObjects.glCreateProgramObjectARB();
      this.name = var1;
      this.bStatic = var2;
      boolean var3;
      if (this.ShaderID != 0) {
         this.FragID = this.createFragShader("media/shaders/" + var1 + ".frag");
         if (!var2) {
            this.VertID = this.createVertShader("media/shaders/" + var1 + ".vert");
         } else {
            this.VertID = this.createVertShader("media/shaders/" + var1 + "_static.vert");
         }

         if (this.VertID != 0 && this.FragID != 0) {
            ARBShaderObjects.glAttachObjectARB(this.ShaderID, this.VertID);
            ARBShaderObjects.glAttachObjectARB(this.ShaderID, this.FragID);
            ARBShaderObjects.glLinkProgramARB(this.ShaderID);
            if (ARBShaderObjects.glGetObjectParameteriARB(this.ShaderID, 35714) == 0) {
               System.err.println(getLogInfo(this.ShaderID));
               this.VertID = 0;
               this.ShaderID = 0;
               this.FragID = 0;
               return;
            }

            ARBShaderObjects.glValidateProgramARB(this.ShaderID);
            if (ARBShaderObjects.glGetObjectParameteriARB(this.ShaderID, 35715) == 0) {
               System.err.println(getLogInfo(this.ShaderID));
               this.VertID = 0;
               this.ShaderID = 0;
               this.FragID = 0;
               return;
            }

            this.Start();
            if (!var2) {
               this.MatrixID = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "MatrixPalette");
            } else {
               this.TransformMatrixID = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "transform");
            }

            this.Light0Colour = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Light0Colour");
            this.Light0Direction = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Light0Direction");
            this.Light1Colour = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Light1Colour");
            this.Light1Direction = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Light1Direction");
            this.Light2Colour = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Light2Colour");
            this.Light2Direction = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Light2Direction");
            this.Ambient = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "AmbientColour");
            this.TintColour = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TintColour");
            this.Texture0 = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Texture0");
            this.TexturePainColor = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TexturePainColor");
            this.TextureRust = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureRust");
            this.TextureMask = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureMask");
            this.TextureLights = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureLights");
            this.TextureDamage1Overlay = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureDamage1Overlay");
            this.TextureDamage1Shell = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureDamage1Shell");
            this.TextureDamage2Overlay = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureDamage2Overlay");
            this.TextureDamage2Shell = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureDamage2Shell");
            this.TextureRustA = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureRustA");
            this.TextureUninstall1 = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureUninstall1");
            this.TextureUninstall2 = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureUninstall2");
            this.TextureLightsEnables2 = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureLightsEnables2");
            this.TextureDamage1Enables1 = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureDamage1Enables1");
            this.TextureDamage1Enables2 = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureDamage1Enables2");
            this.TextureDamage2Enables1 = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureDamage2Enables1");
            this.TextureDamage2Enables2 = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureDamage2Enables2");
            this.Alpha = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "Alpha");
            this.TextureReflectionA = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureReflectionA");
            this.TextureReflectionB = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "TextureReflectionB");
            this.ReflectionParam = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "ReflectionParam");
            if (this.Texture0 != -1) {
               ARBShaderObjects.glUniform1iARB(this.Texture0, 0);
            }

            if (this.TextureRust != -1) {
               ARBShaderObjects.glUniform1iARB(this.TextureRust, 1);
            }

            if (this.TextureMask != -1) {
               ARBShaderObjects.glUniform1iARB(this.TextureMask, 2);
            }

            if (this.TextureLights != -1) {
               ARBShaderObjects.glUniform1iARB(this.TextureLights, 3);
            }

            if (this.TextureDamage1Overlay != -1) {
               ARBShaderObjects.glUniform1iARB(this.TextureDamage1Overlay, 4);
            }

            if (this.TextureDamage1Shell != -1) {
               ARBShaderObjects.glUniform1iARB(this.TextureDamage1Shell, 5);
            }

            if (this.TextureDamage2Overlay != -1) {
               ARBShaderObjects.glUniform1iARB(this.TextureDamage2Overlay, 6);
            }

            if (this.TextureDamage2Shell != -1) {
               ARBShaderObjects.glUniform1iARB(this.TextureDamage2Shell, 7);
            }

            if (this.TextureReflectionA != -1) {
               ARBShaderObjects.glUniform1iARB(this.TextureReflectionA, 8);
            }

            if (this.TextureReflectionB != -1) {
               ARBShaderObjects.glUniform1iARB(this.TextureReflectionB, 9);
            }

            this.BoneIndicesAttrib = GL20.glGetAttribLocation(this.ShaderID, "boneIndices");
            this.BoneWeightsAttrib = GL20.glGetAttribLocation(this.ShaderID, "boneWeights");
            this.End();
         } else {
            var3 = false;
         }
      } else {
         var3 = false;
      }

   }

   private static String getLogInfo(int var0) {
      return ARBShaderObjects.glGetInfoLogARB(var0, ARBShaderObjects.glGetObjectParameteriARB(var0, 35716));
   }

   public void setTexture(Texture var1) {
      this.tex = var1;
      if (var1 != null) {
         var1.bind();
      }

   }

   public int getID() {
      return this.ShaderID;
   }

   public void Start() {
      ARBShaderObjects.glUseProgramObjectARB(this.ShaderID);
   }

   public void End() {
      ARBShaderObjects.glUseProgramObjectARB(0);
   }

   private int createVertShader(String var1) {
      int var2 = ARBShaderObjects.glCreateShaderObjectARB(35633);
      if (var2 == 0) {
         return 0;
      } else {
         String var3 = null;

         try {
            InputStreamReader var5 = IndieFileLoader.getStreamReader(var1, false);
            BufferedReader var6 = new BufferedReader(var5);

            String var4;
            while((var4 = var6.readLine()) != null) {
               if (var3 == null) {
                  var3 = var4.trim() + System.getProperty("line.separator");
               } else {
                  var3 = var3 + var4.trim() + System.getProperty("line.separator");
               }
            }
         } catch (Exception var7) {
            System.out.println("Fail reading vertex shading code");
            return 0;
         }

         while(var3.indexOf("#") != 0) {
            var3 = var3.substring(1);
         }

         ARBShaderObjects.glShaderSourceARB(var2, var3);
         ARBShaderObjects.glCompileShaderARB(var2);
         if (!printLogInfo(var2)) {
            var2 = 0;
         }

         return var2;
      }
   }

   private int createFragShader(String var1) {
      int var2 = ARBShaderObjects.glCreateShaderObjectARB(35632);
      if (var2 == 0) {
         return 0;
      } else {
         String var3 = null;

         try {
            InputStreamReader var5 = IndieFileLoader.getStreamReader(var1, false);
            BufferedReader var6 = new BufferedReader(var5);

            String var4;
            while((var4 = var6.readLine()) != null) {
               if (var3 == null) {
                  var3 = var4.trim() + System.getProperty("line.separator");
               } else {
                  var3 = var3 + var4.trim() + System.getProperty("line.separator");
               }
            }
         } catch (Exception var7) {
            System.out.println("Fail reading fragment shading code");
            return 0;
         }

         while(var3.indexOf("#") != 0) {
            var3 = var3.substring(1);
         }

         ARBShaderObjects.glShaderSourceARB(var2, var3);
         ARBShaderObjects.glCompileShaderARB(var2);
         if (!printLogInfo(var2)) {
            var2 = 0;
         }

         return var2;
      }
   }

   private static boolean printLogInfo(int var0) {
      IntBuffer var1 = BufferUtils.createIntBuffer(1);
      ARBShaderObjects.glGetObjectParameterARB(var0, 35716, var1);
      int var2 = var1.get();
      if (var2 > 1) {
         ByteBuffer var3 = BufferUtils.createByteBuffer(var2);
         var1.flip();
         ARBShaderObjects.glGetInfoLogARB(var0, var1, var3);
         byte[] var4 = new byte[var2];
         var3.get(var4);
         String var5 = new String(var4);
         System.out.println("Info log:\n" + var5);
         return true;
      } else {
         return true;
      }
   }

   public void updateParams() {
   }

   public void updateParamsSkin() {
   }

   public void setMatrixPalette(Matrix4f[] var1) {
      if (!this.bStatic) {
         if (floatBuffer == null) {
            floatBuffer = BufferUtils.createFloatBuffer(var1.length * 64);
         }

         floatBuffer.clear();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            var1[var2].get(floatBuffer);
            floatBuffer.position(floatBuffer.position() + 16);
         }

         floatBuffer.flip();
         ARBShaderObjects.glUniformMatrix4ARB(this.MatrixID, true, floatBuffer);
      }
   }

   public void setTint(float var1, float var2, float var3) {
      ARBShaderObjects.glUniform3fARB(this.TintColour, var1, var2, var3);
   }

   public void setTextureRustA(float var1) {
      ARBShaderObjects.glUniform1fARB(this.TextureRustA, var1);
   }

   public void setTexturePainColor(float var1, float var2, float var3, float var4) {
      ARBShaderObjects.glUniform4fARB(this.TexturePainColor, var1, var2, var3, var4);
   }

   public void setTexturePainColor(org.joml.Vector3f var1, float var2) {
      ARBShaderObjects.glUniform4fARB(this.TexturePainColor, var1.x(), var1.y(), var1.z(), var2);
   }

   public void setTexturePainColor(Vector4f var1) {
      ARBShaderObjects.glUniform4fARB(this.TexturePainColor, var1.x(), var1.y(), var1.z(), var1.w());
   }

   public void setReflectionParam(float var1, float var2, float var3) {
      ARBShaderObjects.glUniform3fARB(this.ReflectionParam, var1, var2, var3);
   }

   public void setTextureUninstall1(Matrix4f var1) {
      this.setMatrix(this.TextureUninstall1, var1);
   }

   public void setTextureUninstall2(Matrix4f var1) {
      this.setMatrix(this.TextureUninstall2, var1);
   }

   public void setTextureLightsEnables2(Matrix4f var1) {
      this.setMatrix(this.TextureLightsEnables2, var1);
   }

   public void setTextureDamage1Enables1(Matrix4f var1) {
      this.setMatrix(this.TextureDamage1Enables1, var1);
   }

   public void setTextureDamage1Enables2(Matrix4f var1) {
      this.setMatrix(this.TextureDamage1Enables2, var1);
   }

   public void setTextureDamage2Enables1(Matrix4f var1) {
      this.setMatrix(this.TextureDamage2Enables1, var1);
   }

   public void setTextureDamage2Enables2(Matrix4f var1) {
      this.setMatrix(this.TextureDamage2Enables2, var1);
   }

   public void setShaderAlpha(float var1) {
      ARBShaderObjects.glUniform1fARB(this.Alpha, var1);
   }

   public void setLight(int var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, ModelInstance var9) {
      int var10 = this.Light0Direction;
      int var11 = this.Light0Colour;
      if (var1 == 1) {
         var10 = this.Light1Direction;
         var11 = this.Light1Colour;
      }

      if (var1 == 2) {
         var10 = this.Light2Direction;
         var11 = this.Light2Colour;
      }

      Vector3f var12 = tempVec3f;
      var12.set(var2 + 0.5F, var3 + 0.5F, var4 + 1.0F);
      var12.x -= var9.object.x;
      var12.y -= var9.object.y;
      var12.z -= var9.object.z + 0.25F;
      float var13 = var12.length();
      float var14 = var12.y;
      var12.y = var12.z;
      var12.z = var14;
      if (var12.length() < 1.0E-4F) {
         var12.set(0.0F, 1.0F, 0.0F);
      }

      var12.normalise();
      float var15 = 1.0F - var13 / var8;
      if (var15 < 0.0F) {
         var15 = 0.0F;
      }

      if (var15 > 1.0F) {
         var15 = 1.0F;
      }

      var5 *= var15 * 0.55F;
      var6 *= var15 * 0.55F;
      var7 *= var15 * 0.55F;
      if (var9.character == null) {
         this.doVector3(var10, var12.x, var12.y, var12.z, (FloatBuffer)null);
      } else {
         this.doVector3(var10, -var12.x, var12.y, var12.z, (FloatBuffer)null);
      }

      this.doVector3(var11, var5, var6, var7, (FloatBuffer)null);
   }

   private void doVector3(int var1, float var2, float var3, float var4, FloatBuffer var5) {
      ARBShaderObjects.glUniform3fARB(var1, var2, var3, var4);
   }

   public void setAmbient(float var1) {
      ARBShaderObjects.glUniform3fARB(this.Ambient, var1, var1, var1);
   }

   public void setTransformMatrix(Matrix4f var1) {
      this.floatBuffer2.clear();
      var1.get(this.floatBuffer2);
      this.floatBuffer2.position(16);
      this.floatBuffer2.flip();
      ARBShaderObjects.glUniformMatrix4ARB(this.TransformMatrixID, true, this.floatBuffer2);
   }

   public void setMatrix(int var1, Matrix4f var2) {
      this.floatBuffer2.clear();
      var2.get(this.floatBuffer2);
      this.floatBuffer2.position(16);
      this.floatBuffer2.flip();
      ARBShaderObjects.glUniformMatrix4ARB(var1, true, this.floatBuffer2);
   }
}
