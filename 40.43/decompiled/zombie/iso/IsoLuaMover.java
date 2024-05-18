package zombie.iso;

import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.core.bucket.BucketManager;
import zombie.core.textures.ColorInfo;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.ui.UIManager;

public class IsoLuaMover extends IsoGameCharacter {
   public KahluaTable luaMoverTable;

   public IsoLuaMover(KahluaTable var1) {
      super((IsoCell)null, 0.0F, 0.0F, 0.0F);
      this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
      this.luaMoverTable = var1;
      if (this.def == null) {
         this.def = IsoSpriteInstance.get(this.sprite);
      }

   }

   public void playAnim(String var1, float var2, boolean var3, boolean var4) {
      this.sprite.PlayAnim(var1);
      float var5 = (float)this.sprite.CurrentAnim.Frames.size();
      float var6 = 1000.0F / var5;
      float var7 = var6 * var2;
      this.def.AnimFrameIncrease = var7 * GameTime.getInstance().getMultiplier();
      this.def.Finished = !var4;
      this.def.Looped = var3;
   }

   public String getObjectName() {
      return "IsoLuaMover";
   }

   public void update() {
      this.setBlendSpeed(1.0F);

      try {
         LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.luaMoverTable.rawget("update"), (Object)this.luaMoverTable);
      } catch (Exception var2) {
         var2.printStackTrace();
      }

      this.sprite.update(this.def);
      super.update();
   }

   public void render(float var1, float var2, float var3, ColorInfo var4, boolean var5) {
      float var6 = this.offsetY;
      var6 -= 100.0F;
      float var7 = this.offsetX;
      var7 -= 34.0F;
      this.sprite.render(this.def, this, this.x, this.y, this.z, this.dir, var7, var6, var4);

      try {
         LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.luaMoverTable.rawget("postrender"), this.luaMoverTable, var4, var5);
      } catch (Exception var9) {
         var9.printStackTrace();
      }

   }
}
