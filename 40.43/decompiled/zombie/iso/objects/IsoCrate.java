package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.WorldSoundManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.core.bucket.BucketManager;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.ui.ObjectTooltip;

public class IsoCrate extends IsoObject implements Thumpable {
   public boolean Barricaded = false;
   public Integer BarricideMaxStrength = 0;
   public Integer BarricideStrength = 0;
   public Integer Health = 10000;
   public boolean Locked = false;
   public Integer MaxHealth = 10000;
   public Integer PushedMaxStrength = 0;
   public Integer PushedStrength = 0;
   public IsoCrate.DoorType type;
   IsoSprite barricadeSprite;
   IsoSprite closedSprite;
   boolean open;
   IsoSprite openSprite;
   private boolean destroyed;

   public IsoCrate(IsoCell var1) {
      super(var1);
      this.type = IsoCrate.DoorType.WeakWooden;
      this.open = false;
      this.destroyed = false;
   }

   public String getObjectName() {
      return "Crate";
   }

   public IsoCrate(IsoCell var1, IsoGridSquare var2, int var3) {
      this.type = IsoCrate.DoorType.WeakWooden;
      this.open = false;
      this.destroyed = false;
      this.PushedMaxStrength = this.PushedStrength = 2500;
      this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
      this.sprite.getProperties().Set((IsoFlagType)IsoFlagType.solidtrans, (String)null);
      this.sprite.LoadFramesNoDirPageSimple("TileObjects2_0");
      this.square = var2;
      this.container = new ItemContainer("playerCrate", this.square, this, 6, 6);
      this.DirtySlice();
      switch(this.type) {
      case WeakWooden:
      default:
      }
   }

   public void Barricade(IsoGameCharacter var1) {
   }

   public void save(ByteBuffer var1) throws IOException {
      super.save(var1);
      var1.putInt(this.container.ID);
   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      super.load(var1, var2);
      this.PushedMaxStrength = this.PushedStrength = 2500;
      this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
      this.sprite.getProperties().Set((IsoFlagType)IsoFlagType.solidtrans, (String)null);
      this.sprite.LoadFramesNoDirPageSimple("TileObjects2_0");
      this.container = new ItemContainer(var1.getInt(), "playerCrate", this.square, this, 6, 6);
      this.container.load(var1, var2, false);
      switch(this.type) {
      case WeakWooden:
      default:
      }
   }

   public void DoTooltip(ObjectTooltip var1) {
      byte var2 = 60;
      String var3 = "";
      switch(this.type) {
      case WeakWooden:
         if (this.Locked) {
            var3 = "Crate";
         } else {
            var3 = "Crate";
         }
         break;
      case StrongWooden:
         var3 = "Strong Wooden Door";
      }

      byte var4 = 5;
      var1.DrawText(var3, 5.0D, (double)var4, 1.0D, 1.0D, 0.800000011920929D, 1.0D);
      int var9 = var4 + 20;
      int var5 = this.Health;
      int var6 = this.MaxHealth;
      int var7 = var5;
      int var8 = var6;
      if (this.IsStrengthenedByPushedItems()) {
         var5 += this.PushedMaxStrength;
         var6 += this.PushedMaxStrength;
         var5 /= 100;
         var6 /= 100;
         if (var7 < var8 && var7 > 0) {
            ++var5;
         }

         var1.DrawText("Health:", 5.0D, (double)var9, 1.0D, 1.0D, 0.800000011920929D, 1.0D);
      } else {
         var5 /= 100;
         var6 /= 100;
         if (var7 < var8 && var7 > 0) {
            ++var5;
         }

         var1.DrawText("Health:", 5.0D, (double)var9, 1.0D, 1.0D, 0.800000011920929D, 1.0D);
      }

      if ((double)var5 > (double)var6 * 0.75D) {
         var1.DrawText(var5 + "/" + var6, (double)var2, (double)var9, 0.30000001192092896D, 1.0D, 0.20000000298023224D, 1.0D);
      } else if ((double)var5 > (double)var6 * 0.33D) {
         var1.DrawText(var5 + "/" + var6, (double)var2, (double)var9, 0.800000011920929D, 1.0D, 0.20000000298023224D, 1.0D);
      } else {
         var1.DrawText(var5 + "/" + var6, (double)var2, (double)var9, 0.800000011920929D, 0.30000001192092896D, 0.20000000298023224D, 1.0D);
      }

      var9 += 15;
      if (this.Barricaded) {
         if (!this.IsStrengthenedByPushedItems()) {
            var1.DrawText("Barricade:", 5.0D, (double)var9, 1.0D, 1.0D, 0.800000011920929D, 1.0D);
         } else {
            var1.DrawText("Barricade:", 5.0D, (double)var9, 1.0D, 1.0D, 0.800000011920929D, 1.0D);
         }

         if ((double)(this.BarricideStrength / 100) > (double)(this.BarricideMaxStrength / 100) * 0.75D) {
            var1.DrawText(this.BarricideStrength / 100 + "/" + this.BarricideMaxStrength / 100, (double)var2, (double)var9, 0.30000001192092896D, 1.0D, 0.20000000298023224D, 1.0D);
         } else if ((double)(this.BarricideStrength / 100) > (double)(this.BarricideMaxStrength / 100) * 0.33D) {
            var1.DrawText(this.BarricideStrength / 100 + "/" + this.BarricideMaxStrength / 100, (double)var2, (double)var9, 0.800000011920929D, 1.0D, 0.20000000298023224D, 1.0D);
         } else {
            var1.DrawText(this.BarricideStrength / 100 + "/" + this.BarricideMaxStrength / 100, (double)var2, (double)var9, 0.800000011920929D, 0.30000001192092896D, 0.20000000298023224D, 1.0D);
         }
      }

      var9 += 19;
      var1.setHeight((double)var9);
   }

   public boolean HasTooltip() {
      return true;
   }

   public boolean isDestroyed() {
      return this.destroyed;
   }

   public boolean IsOpen() {
      return this.open;
   }

   public boolean IsStrengthenedByPushedItems() {
      IsoGridSquare var1 = this.square;
      IsoGridSquare var2 = null;
      var2 = var1.getCell().getGridSquare(var1.getX() - 1, var1.getY(), var1.getZ());
      return var2.getProperties().Is(IsoFlagType.pushable) || var1.getProperties().Is(IsoFlagType.pushable);
   }

   public boolean onMouseLeftClick(int var1, int var2) {
      return super.onMouseLeftClick(var1, var2);
   }

   public boolean TestCollide(IsoMovingObject var1, IsoGridSquare var2, IsoGridSquare var3) {
      if (var2 != this.square) {
         if (var1 != null) {
            var1.collideWith(this);
         }

         return true;
      } else {
         return false;
      }
   }

   public IsoObject.VisionResult TestVision(IsoGridSquare var1, IsoGridSquare var2) {
      return IsoObject.VisionResult.Blocked;
   }

   public void Thump(IsoMovingObject var1) {
      if (!this.isDestroyed()) {
         if (var1 instanceof IsoZombie) {
            if (var1.getCurrentSquare().getMovingObjects().size() >= 4) {
               this.Damage(1);
            }

            WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
         }

         if (!this.IsStrengthenedByPushedItems() && this.Health <= 0 || this.IsStrengthenedByPushedItems() && this.Health <= -this.PushedMaxStrength) {
            ((IsoGameCharacter)var1).getEmitter().playSound("breakdoor", this);
            WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
            var1.setThumpTarget((Thumpable)null);
            this.destroyed = true;
            this.square.getObjects().remove(this);
            this.square.getSpecialObjects().remove(this);
            byte var2 = 1;

            for(int var3 = 0; var3 < var2; ++var3) {
               this.square.AddWorldInventoryItem("Base.Plank", Rand.Next(-1.0F, 1.0F), Rand.Next(-1.0F, 1.0F), 0.0F);
            }
         }

      }
   }

   public void WeaponHit(IsoGameCharacter var1, HandWeapon var2) {
      if (!this.isDestroyed()) {
         this.Damage(var2.getDoorDamage());
         if (var2.getDoorHitSound() != null) {
            var1.getEmitter().playSound(var2.getDoorHitSound());
         }

         WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
         if (!this.IsStrengthenedByPushedItems() && this.Health <= 0 || this.IsStrengthenedByPushedItems() && this.Health <= -this.PushedMaxStrength) {
            var1.getEmitter().playSound("breakdoor", this);
            WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
            this.destroyed = true;
            this.square.getObjects().remove(this);
            this.square.getSpecialObjects().remove(this);
         }

      }
   }

   public void ToggleDoor(IsoGameCharacter var1) {
   }

   public void ToggleDoorSilent() {
   }

   public void Unbarricade() {
   }

   void Damage(int var1) {
      if (this.Barricaded) {
         this.BarricideStrength = this.BarricideStrength - var1;
         if (this.BarricideStrength <= 0) {
            this.Unbarricade();
         }
      } else {
         this.Health = this.Health - var1;
      }

   }

   public static enum DoorType {
      WeakWooden,
      StrongWooden;
   }
}
