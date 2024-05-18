package zombie.core.skinnedmodel.model;

import zombie.core.skinnedmodel.shader.Shader;

public class ModelMesh {
   VertexBufferObject vb;
   public String Texture;

   public void SetVertexBuffer(VertexBufferObject var1) {
      this.vb = var1;
   }

   public void Draw(Shader var1) {
      if (this.vb != null) {
         this.vb.Draw(var1);
      }

   }
}
