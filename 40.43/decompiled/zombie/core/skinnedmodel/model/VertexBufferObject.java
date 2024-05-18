package zombie.core.skinnedmodel.model;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import zombie.core.VBO.IGLBufferObject;
import zombie.core.skinnedmodel.shader.Shader;

public class VertexBufferObject {
   public static IGLBufferObject funcs;
   VertexBufferObject.Vbo _handle;
   VertexBufferObject.VertexStride[] _vertexStride;
   VertexBufferObject.BeginMode _beginMode;
   public boolean bStatic = false;

   public VertexBufferObject(VertexPositionNormalTangentTextureSkin[] var1, int[] var2) {
      this.bStatic = false;
      this._handle = this.LoadVBO(var1, var2);
      this._vertexStride = new VertexBufferObject.VertexStride[6];

      for(int var3 = 0; var3 < this._vertexStride.length; ++var3) {
         this._vertexStride[var3] = new VertexBufferObject.VertexStride();
      }

      this._vertexStride[0].Type = VertexBufferObject.VertexType.VertexArray;
      this._vertexStride[0].Offset = 0;
      this._vertexStride[1].Type = VertexBufferObject.VertexType.NormalArray;
      this._vertexStride[1].Offset = 12;
      this._vertexStride[2].Type = VertexBufferObject.VertexType.TangentAray;
      this._vertexStride[2].Offset = 24;
      this._vertexStride[3].Type = VertexBufferObject.VertexType.TextureCoordArray;
      this._vertexStride[3].Offset = 36;
      this._vertexStride[4].Type = VertexBufferObject.VertexType.BlendWeightArray;
      this._vertexStride[4].Offset = 44;
      this._vertexStride[5].Type = VertexBufferObject.VertexType.BlendIndexArray;
      this._vertexStride[5].Offset = 60;
      this._beginMode = VertexBufferObject.BeginMode.Triangles;
   }

   public VertexBufferObject(VertexPositionNormalTangentTexture[] var1, int[] var2) {
      this.bStatic = true;
      this._handle = this.LoadVBO(var1, var2);
      boolean var3 = true;
      byte var5 = 4;
      this._vertexStride = new VertexBufferObject.VertexStride[var5];

      for(int var4 = 0; var4 < this._vertexStride.length; ++var4) {
         this._vertexStride[var4] = new VertexBufferObject.VertexStride();
      }

      this._vertexStride[0].Type = VertexBufferObject.VertexType.VertexArray;
      this._vertexStride[0].Offset = 0;
      this._vertexStride[1].Type = VertexBufferObject.VertexType.NormalArray;
      this._vertexStride[1].Offset = 12;
      this._vertexStride[2].Type = VertexBufferObject.VertexType.TangentAray;
      this._vertexStride[2].Offset = 24;
      this._vertexStride[3].Type = VertexBufferObject.VertexType.TextureCoordArray;
      this._vertexStride[3].Offset = 36;
      this._beginMode = VertexBufferObject.BeginMode.Triangles;
   }

   public void SetFaceDataOnly() {
      this._handle.FaceDataOnly = true;
   }

   VertexBufferObject.Vbo LoadVBO(VertexPositionNormalTangentTextureSkin[] var1, int[] var2) {
      VertexBufferObject.Vbo var3 = new VertexBufferObject.Vbo();
      boolean var4 = false;
      byte var5 = 76;
      var3.FaceDataOnly = false;
      ByteBuffer var6 = BufferUtils.createByteBuffer(var1.length * var5);
      ByteBuffer var7 = BufferUtils.createByteBuffer(var2.length * 4);

      int var8;
      for(var8 = 0; var8 < var1.length; ++var8) {
         var1[var8].put(var6);
      }

      for(var8 = 0; var8 < var2.length; ++var8) {
         var7.putInt(var2[var8]);
      }

      var6.flip();
      var7.flip();
      var3.VboID = funcs.glGenBuffers();
      funcs.glBindBuffer(funcs.GL_ARRAY_BUFFER(), var3.VboID);
      funcs.glBufferData(funcs.GL_ARRAY_BUFFER(), var6, funcs.GL_STATIC_DRAW());
      funcs.glGetBufferParameter(funcs.GL_ARRAY_BUFFER(), funcs.GL_BUFFER_SIZE(), var3.b);
      int var9 = var3.b.get();
      if (var1.length * var5 != var9) {
         throw new RuntimeException("Vertex data not uploaded correctly");
      } else {
         var3.EboID = funcs.glGenBuffers();
         funcs.glBindBuffer(funcs.GL_ELEMENT_ARRAY_BUFFER(), var3.EboID);
         funcs.glBufferData(funcs.GL_ELEMENT_ARRAY_BUFFER(), var7, funcs.GL_STATIC_DRAW());
         var3.b.clear();
         funcs.glGetBufferParameter(funcs.GL_ELEMENT_ARRAY_BUFFER(), funcs.GL_BUFFER_SIZE(), var3.b);
         var9 = var3.b.get();
         if (var2.length * 4 != var9) {
            throw new RuntimeException("Element data not uploaded correctly");
         } else {
            var3.NumElements = var2.length;
            var3.VertexStride = var5;
            return var3;
         }
      }
   }

   VertexBufferObject.Vbo LoadVBO(VertexPositionNormalTangentTexture[] var1, int[] var2) {
      VertexBufferObject.Vbo var3 = new VertexBufferObject.Vbo();
      boolean var4 = false;
      byte var5 = 44;
      var3.FaceDataOnly = false;
      ByteBuffer var6 = BufferUtils.createByteBuffer(var1.length * var5);
      ByteBuffer var7 = BufferUtils.createByteBuffer(var2.length * 4);

      int var8;
      for(var8 = 0; var8 < var1.length; ++var8) {
         var1[var8].put(var6);
      }

      for(var8 = 0; var8 < var2.length; ++var8) {
         var7.putInt(var2[var8]);
      }

      var6.flip();
      var7.flip();
      var3.VboID = funcs.glGenBuffers();
      funcs.glBindBuffer(funcs.GL_ARRAY_BUFFER(), var3.VboID);
      funcs.glBufferData(funcs.GL_ARRAY_BUFFER(), var6, funcs.GL_STATIC_DRAW());
      funcs.glGetBufferParameter(funcs.GL_ARRAY_BUFFER(), funcs.GL_BUFFER_SIZE(), var3.b);
      int var9 = var3.b.get();
      if (var1.length * var5 != var9) {
         throw new RuntimeException("Vertex data not uploaded correctly");
      } else {
         var3.EboID = funcs.glGenBuffers();
         funcs.glBindBuffer(funcs.GL_ELEMENT_ARRAY_BUFFER(), var3.EboID);
         funcs.glBufferData(funcs.GL_ELEMENT_ARRAY_BUFFER(), var7, funcs.GL_STATIC_DRAW());
         var3.b.clear();
         funcs.glGetBufferParameter(funcs.GL_ELEMENT_ARRAY_BUFFER(), funcs.GL_BUFFER_SIZE(), var3.b);
         var9 = var3.b.get();
         if (var2.length * 4 != var9) {
            throw new RuntimeException("Element data not uploaded correctly");
         } else {
            var3.NumElements = var2.length;
            var3.VertexStride = var5;
            return var3;
         }
      }
   }

   public void Draw(Shader var1) {
      Draw(this._handle, this._vertexStride, this._beginMode, var1);
   }

   private static void Draw(VertexBufferObject.Vbo var0, VertexBufferObject.VertexStride[] var1, VertexBufferObject.BeginMode var2, Shader var3) {
      int var4 = 33984;
      int var5;
      if (!var0.FaceDataOnly) {
         funcs.glBindBuffer(funcs.GL_ARRAY_BUFFER(), var0.VboID);

         for(var5 = var1.length - 1; var5 >= 0; --var5) {
            switch(var1[var5].Type) {
            case VertexArray:
               GL11.glVertexPointer(3, 5126, var0.VertexStride, (long)var1[var5].Offset);
               GL11.glEnableClientState(32884);
               break;
            case NormalArray:
               GL11.glNormalPointer(5126, var0.VertexStride, (long)var1[var5].Offset);
               GL11.glEnableClientState(32885);
               break;
            case ColorArray:
               GL11.glColorPointer(3, 5121, var0.VertexStride, (long)var1[var5].Offset);
               GL11.glEnableClientState(32886);
               break;
            case TextureCoordArray:
               GL13.glActiveTexture(var4);
               GL13.glClientActiveTexture(var4);
               GL11.glTexCoordPointer(2, 5126, var0.VertexStride, (long)var1[var5].Offset);
               ++var4;
               GL11.glEnableClientState(32888);
               break;
            case TangentAray:
               GL11.glNormalPointer(5126, var0.VertexStride, (long)var1[var5].Offset);
               break;
            case BlendWeightArray:
               int var6 = var3.BoneWeightsAttrib;
               GL20.glVertexAttribPointer(var6, 4, 5126, false, var0.VertexStride, (long)var1[var5].Offset);
               GL20.glEnableVertexAttribArray(var6);
               break;
            case BlendIndexArray:
               int var7 = var3.BoneIndicesAttrib;
               GL20.glVertexAttribPointer(var7, 4, 5126, false, var0.VertexStride, (long)var1[var5].Offset);
               GL20.glEnableVertexAttribArray(var7);
            }
         }
      }

      funcs.glBindBuffer(funcs.GL_ELEMENT_ARRAY_BUFFER(), var0.EboID);
      GL11.glDrawElements(4, var0.NumElements, 5125, 0L);
      GL11.glDisableClientState(32885);
      if (var0.VertexStride > 44) {
         var5 = var3.BoneWeightsAttrib;
         GL20.glDisableVertexAttribArray(var5);
         var5 = var3.BoneIndicesAttrib;
         GL20.glDisableVertexAttribArray(var5);
      }

   }

   public class VertexStride {
      public VertexBufferObject.VertexType Type;
      public int Offset;
   }

   public class Vbo {
      public IntBuffer b = BufferUtils.createIntBuffer(4);
      public int VboID;
      public int EboID;
      public int NumElements;
      public int VertexStride;
      public boolean FaceDataOnly;
   }

   public static enum BeginMode {
      Triangles;
   }

   public static enum VertexType {
      VertexArray,
      NormalArray,
      ColorArray,
      IndexArray,
      TextureCoordArray,
      TangentAray,
      BlendWeightArray,
      BlendIndexArray;
   }
}
