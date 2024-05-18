package zombie.iso.objects;

import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCell;
import zombie.iso.IsoPhysicsObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSpriteInstance;

public class IsoZombieGiblets extends IsoPhysicsObject {
   public float tintb = 1.0F;
   public float tintg = 1.0F;
   public float tintr = 1.0F;
   public float time = 0.0F;

   public IsoZombieGiblets(IsoCell var1) {
      super(var1);
   }

   public boolean Serialize() {
      return false;
   }

   public String getObjectName() {
      return "ZombieGiblets";
   }

   public void update() {
      if (Rand.Next(Rand.AdjustForFramerate(8)) == 0 && this.getZ() > (float)((int)this.getZ()) && this.getCurrentSquare() != null && this.getCurrentSquare().getChunk() != null) {
         this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, (float)((int)this.z), Rand.Next(8));
      }

      if (Core.bLastStand && Rand.Next(Rand.AdjustForFramerate(10)) == 0 && this.getZ() > (float)((int)this.getZ()) && this.getCurrentSquare() != null && this.getCurrentSquare().getChunk() != null) {
         this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, (float)((int)this.z), Rand.Next(8));
      }

      super.update();
      this.time += GameTime.instance.getMultipliedSecondsSinceLastUpdate();
      if (this.velX == 0.0F && this.velY == 0.0F && this.getZ() == (float)((int)this.getZ())) {
         this.setCollidable(false);
         IsoWorld.instance.CurrentCell.getRemoveList().add(this);
      }

   }

   public void render(float var1, float var2, float var3, ColorInfo var4, boolean var5) {
      this.targetAlpha[IsoPlayer.getPlayerIndex()] = this.sprite.def.targetAlpha = this.def.targetAlpha = 1.0F - this.time / 1.0F;
      if (this.targetAlpha[IsoPlayer.getPlayerIndex()] < 0.0F) {
         this.targetAlpha[IsoPlayer.getPlayerIndex()] = 0.0F;
      }

      if (this.targetAlpha[IsoPlayer.getPlayerIndex()] > 1.0F) {
         this.targetAlpha[IsoPlayer.getPlayerIndex()] = 1.0F;
      }

      super.render(var1, var2, var3, var4, var5);
      if (Core.bDebug) {
      }

   }

   public IsoZombieGiblets(IsoZombieGiblets.GibletType var1, IsoCell var2, float var3, float var4, float var5, float var6, float var7) {
      super(var2);
      this.velX = var6;
      this.velY = var7;
      float var8 = (float)Rand.Next(4000) / 10000.0F;
      float var9 = (float)Rand.Next(4000) / 10000.0F;
      var8 -= 0.2F;
      var9 -= 0.2F;
      this.velX += var8;
      this.velY += var9;
      this.x = var3;
      this.y = var4;
      this.z = var5;
      this.nx = var3;
      this.ny = var4;
      this.alpha[IsoPlayer.getPlayerIndex()] = 0.5F;
      this.def = IsoSpriteInstance.get(this.sprite);
      this.def.alpha = 0.4F;
      this.sprite.def.alpha = 0.4F;
      this.offsetX = 0.0F;
      this.offsetY = 0.0F;
      switch(var1) {
      case A:
         this.sprite.LoadFramesNoDirPage("Giblet", "00", 3);
         break;
      case B:
         this.sprite.LoadFramesNoDirPage("Giblet", "01", 3);
         break;
      case Eye:
         this.sprite.LoadFramesNoDirPage("Eyeball", "00", 1);
      }

   }

   public static enum GibletType {
      A,
      B,
      Eye;
   }
}
