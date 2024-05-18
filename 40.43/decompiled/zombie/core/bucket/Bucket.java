package zombie.core.bucket;

import java.util.HashMap;
import java.util.Iterator;
import zombie.core.textures.Texture;
import zombie.iso.sprite.IsoSpriteManager;

public class Bucket {
   public IsoSpriteManager SpriteManager;
   private String name;
   private HashMap textures = new HashMap();

   public Bucket(IsoSpriteManager var1) {
      this.SpriteManager = var1;
   }

   public IsoSpriteManager getSpriteManager() {
      return this.SpriteManager;
   }

   public Bucket() {
      this.SpriteManager = new IsoSpriteManager();
   }

   public void AddTexture(String var1, Texture var2) {
      if (var2 != null) {
         this.textures.put(var1, var2);
      }

   }

   public void Dispose() {
      Iterator var1 = this.textures.values().iterator();

      while(var1.hasNext()) {
         Texture var2 = (Texture)var1.next();
         var2.destroy();
      }

      this.SpriteManager.Dispose();
   }

   public Texture getTexture(String var1) {
      return (Texture)this.textures.get(var1);
   }

   public boolean HasTexture(String var1) {
      return this.textures.containsKey(var1);
   }

   String getName() {
      return this.name;
   }

   void setName(String var1) {
      this.name = var1;
   }

   public void forgetTexture(String var1) {
      this.textures.remove(var1);
   }
}
