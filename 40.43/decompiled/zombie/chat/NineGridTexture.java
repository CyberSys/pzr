package zombie.chat;

import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;

public class NineGridTexture {
   private Texture topLeft;
   private Texture topMid;
   private Texture topRight;
   private Texture left;
   private Texture mid;
   private Texture right;
   private Texture botLeft;
   private Texture botMid;
   private Texture botRight;
   private int outer;

   public NineGridTexture(String var1, int var2) {
      this.outer = var2;
      this.topLeft = Texture.getTexture(var1 + "_topleft");
      this.topMid = Texture.getTexture(var1 + "_topmid");
      this.topRight = Texture.getTexture(var1 + "_topright");
      this.left = Texture.getTexture(var1 + "_left");
      this.mid = Texture.getTexture(var1 + "_mid");
      this.right = Texture.getTexture(var1 + "_right");
      this.botLeft = Texture.getTexture(var1 + "_botleft");
      this.botMid = Texture.getTexture(var1 + "_botmid");
      this.botRight = Texture.getTexture(var1 + "_botright");
   }

   public void renderInnerBased(int var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      var2 += 5;
      var4 -= 7;
      SpriteRenderer.instance.render(this.topLeft, var1 - this.outer, var2 - this.outer, this.outer, this.outer, var5, var6, var7, var8);
      SpriteRenderer.instance.render(this.topMid, var1, var2 - this.outer, var3, this.outer, var5, var6, var7, var8);
      SpriteRenderer.instance.render(this.topRight, var1 + var3, var2 - this.outer, this.outer, this.outer, var5, var6, var7, var8);
      SpriteRenderer.instance.render(this.left, var1 - this.outer, var2, this.outer, var4, var5, var6, var7, var8);
      SpriteRenderer.instance.render(this.mid, var1, var2, var3, var4, var5, var6, var7, var8);
      SpriteRenderer.instance.render(this.right, var1 + var3, var2, this.outer, var4, var5, var6, var7, var8);
      SpriteRenderer.instance.render(this.botLeft, var1 - this.outer, var2 + var4, this.outer, this.outer, var5, var6, var7, var8);
      SpriteRenderer.instance.render(this.botMid, var1, var2 + var4, var3, this.outer, var5, var6, var7, var8);
      SpriteRenderer.instance.render(this.botRight, var1 + var3, var2 + var4, this.outer, this.outer, var5, var6, var7, var8);
   }
}
