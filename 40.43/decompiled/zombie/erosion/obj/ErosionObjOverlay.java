package zombie.erosion.obj;

import java.util.ArrayList;
import zombie.iso.IsoObject;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;

public class ErosionObjOverlay {
   private ErosionObjOverlaySprites sprites;
   public String name;
   public int stages;
   public boolean applyAlpha;
   public int cycleTime;

   public ErosionObjOverlay(ErosionObjOverlaySprites var1, int var2, boolean var3) {
      this.sprites = var1;
      this.name = var1.name;
      this.stages = var1.stages;
      this.applyAlpha = var3;
      this.cycleTime = var2;
   }

   public int setOverlay(IsoObject var1, int var2, int var3, int var4, float var5) {
      if (var3 >= 0 && var3 < this.stages && var1 != null) {
         if (var2 >= 0) {
            this.removeOverlay(var1, var2);
         }

         IsoSprite var6 = this.sprites.getSprite(var3, var4);
         IsoSpriteInstance var7 = var6.newInstance();
         if (this.applyAlpha) {
            var7.SetAlpha(var5);
            var7.SetTargetAlpha(var5);
         }

         if (var1.AttachedAnimSprite == null) {
            var1.AttachedAnimSprite = new ArrayList();
         }

         if (var1.AttachedAnimSpriteActual == null) {
            var1.AttachedAnimSpriteActual = new ArrayList();
         }

         var1.AttachedAnimSprite.add(var7);
         var1.AttachedAnimSpriteActual.add(var6);
         return var7.getID();
      } else {
         return -1;
      }
   }

   public boolean removeOverlay(IsoObject var1, int var2) {
      if (var1 == null) {
         return false;
      } else {
         ArrayList var3 = var1.AttachedAnimSprite;
         if (var3 != null && !var3.isEmpty()) {
            int var4;
            for(var4 = 0; var4 < var1.AttachedAnimSpriteActual.size(); ++var4) {
               if (((IsoSprite)var1.AttachedAnimSpriteActual.get(var4)).ID == var2) {
                  var1.AttachedAnimSpriteActual.remove(var4--);
               }
            }

            for(var4 = var3.size() - 1; var4 >= 0; --var4) {
               if (((IsoSpriteInstance)var3.get(var4)).getID() == var2) {
                  var3.remove(var4);
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }
   }
}
