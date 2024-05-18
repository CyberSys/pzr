package zombie.characters;

import java.util.HashMap;
import zombie.IndieGL;
import zombie.core.bucket.BucketManager;
import zombie.core.textures.ColorInfo;
import zombie.inventory.types.HandWeapon;
import zombie.iso.sprite.IsoAnim;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;

public class WeaponOverlayUtils {
   static HashMap SpriteMap = new HashMap();

   public static void DrawWeapon(HandWeapon var0, IsoGameCharacter var1, IsoSprite var2, float var3, float var4, float var5, ColorInfo var6) {
      if (var0 != null && var0.getWeaponSprite() != null) {
         IsoSprite var7;
         if (!SpriteMap.containsKey(var0.getWeaponSprite())) {
            IndieGL.End();
            var7 = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);

            for(int var8 = 0; var8 < var2.AnimStack.size(); ++var8) {
               String var9 = ((IsoAnim)var2.AnimStack.get(var8)).name;
               if (var9.endsWith("_R")) {
                  var7.LoadFramesReverseAltName(var0.getWeaponSprite(), var9.substring(0, var9.length() - 2), var9, ((IsoAnim)var2.AnimStack.get(var8)).Frames.size());
               } else {
                  var7.LoadFrames(var0.getWeaponSprite(), ((IsoAnim)var2.AnimStack.get(var8)).name, ((IsoAnim)var2.AnimStack.get(var8)).Frames.size());
               }
            }

            SpriteMap.put(var0.getWeaponSprite(), var7);
         }

         var7 = (IsoSprite)SpriteMap.get(var0.getWeaponSprite());
         if (var7.def == null) {
            var7.def = IsoSpriteInstance.get(var7);
         }

         var7.PlayAnim(var1.sprite.CurrentAnim.name);
         var7.def.Frame = var1.def.Frame;
         float var10 = var6.a;
         var6.a = var1.alpha[IsoPlayer.getPlayerIndex()];
         var7.render(var1.def, var1, var3, var4, var5, var1.dir, var1.offsetX + (float)IsoGameCharacter.RENDER_OFFSET_X * var1.def.getScaleX(), var1.offsetY + (float)IsoGameCharacter.RENDER_OFFSET_Y * var1.def.getScaleY(), var6);
         var6.a = var10;
      }
   }
}
