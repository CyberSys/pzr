package zombie.characters;

import zombie.iso.IsoCell;

public class IsoLuaCharacter extends IsoGameCharacter {
   public IsoLuaCharacter(float var1, float var2, float var3) {
      super((IsoCell)null, var1, var2, var3);
      this.descriptor = SurvivorFactory.CreateSurvivor();
      this.descriptor.setInstance(this);
      SurvivorDesc var4 = this.descriptor;
      this.InitSpriteParts(var4, var4.legs, var4.torso, var4.head, var4.top, var4.bottoms, var4.shoes, var4.skinpal, var4.toppal, var4.bottomspal, var4.shoespal, var4.hair, var4.extra);
   }

   public void update() {
   }
}
