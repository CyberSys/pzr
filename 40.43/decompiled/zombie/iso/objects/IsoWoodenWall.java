package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.SoundManager;
import zombie.WorldSoundManager;
import zombie.ai.states.ThumpState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.core.textures.ColorInfo;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.ui.ObjectTooltip;

public class IsoWoodenWall extends IsoObject implements Thumpable {
   public int Barricaded = 0;
   public int BarricideMaxStrength = 0;
   public int BarricideStrength = 0;
   public int Health = 500;
   public boolean Locked = false;
   public int MaxHealth = 500;
   public int PushedMaxStrength = 0;
   public int PushedStrength = 0;
   public IsoWoodenWall.DoorType type;
   IsoSpriteInstance barricadeSprite;
   IsoSprite closedSprite;
   public boolean north;
   int gid;
   public boolean open;
   IsoSprite openSprite;
   private boolean destroyed;
   public boolean MetalBarricaded;
   KahluaTable table;

   public IsoWoodenWall(IsoCell var1) {
      super(var1);
      this.type = IsoWoodenWall.DoorType.WeakWooden;
      this.north = false;
      this.gid = -1;
      this.open = false;
      this.destroyed = false;
      this.MetalBarricaded = false;
   }

   public String getObjectName() {
      return "WoodenWall";
   }

   public void render(float var1, float var2, float var3, ColorInfo var4) {
      super.render(var1, var2, var3, var4, true);
   }

   public IsoWoodenWall(IsoCell var1, IsoGridSquare var2, IsoSprite var3, boolean var4) {
      this.type = IsoWoodenWall.DoorType.WeakWooden;
      this.north = false;
      this.gid = -1;
      this.open = false;
      this.destroyed = false;
      this.MetalBarricaded = false;
      this.OutlineOnMouseover = true;
      this.PushedMaxStrength = this.PushedStrength = 2500;
      this.closedSprite = var3;
      this.openSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (IsoSprite)var3, 2);
      this.sprite = this.closedSprite;
      this.square = var2;
      this.north = var4;
      switch(this.type) {
      case WeakWooden:
         this.MaxHealth = this.Health = 500;
         break;
      case StrongWooden:
         this.MaxHealth = this.Health = 800;
      }

   }

   public IsoWoodenWall(IsoCell var1, IsoGridSquare var2, String var3, boolean var4) {
      this.type = IsoWoodenWall.DoorType.WeakWooden;
      this.north = false;
      this.gid = -1;
      this.open = false;
      this.destroyed = false;
      this.MetalBarricaded = false;
      this.OutlineOnMouseover = true;
      this.PushedMaxStrength = this.PushedStrength = 2500;
      this.closedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (String)var3, 0);
      this.openSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (String)var3, 2);
      this.sprite = this.closedSprite;
      this.square = var2;
      this.north = var4;
      switch(this.type) {
      case WeakWooden:
         this.MaxHealth = this.Health = 500;
         break;
      case StrongWooden:
         this.MaxHealth = this.Health = 800;
      }

   }

   public IsoWoodenWall(IsoCell var1, IsoGridSquare var2, String var3, boolean var4, KahluaTable var5) {
      this.type = IsoWoodenWall.DoorType.WeakWooden;
      this.north = false;
      this.gid = -1;
      this.open = false;
      this.destroyed = false;
      this.MetalBarricaded = false;
      this.OutlineOnMouseover = true;
      this.PushedMaxStrength = this.PushedStrength = 2500;
      this.closedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (String)var3, 0);
      this.openSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (String)var3, 2);
      this.table = var5;
      this.sprite = this.closedSprite;
      this.square = var2;
      this.north = var4;
      switch(this.type) {
      case WeakWooden:
         this.MaxHealth = this.Health = 500;
         break;
      case StrongWooden:
         this.MaxHealth = this.Health = 800;
      }

   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      this.open = var1.get() == 1;
      this.Locked = var1.get() == 1;
      this.north = var1.get() == 1;
      this.Barricaded = var1.getInt();
      this.Health = var1.getInt();
      this.MaxHealth = var1.getInt();
      this.BarricideStrength = var1.getInt();
      if (var2 >= 49) {
         this.BarricideMaxStrength = var1.getShort();
      } else {
         this.BarricideMaxStrength = Math.max(this.BarricideStrength, this.Barricaded * 1000);
      }

      this.closedSprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, var1.getInt());
      this.openSprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, var1.getInt());
      this.OutlineOnMouseover = true;
      this.PushedMaxStrength = this.PushedStrength = 2500;
      if (var2 >= 87) {
         this.MetalBarricaded = var1.get() == 1;
      }

   }

   public void save(ByteBuffer var1) throws IOException {
      super.save(var1);
      var1.put((byte)(this.open ? 1 : 0));
      var1.put((byte)(this.Locked ? 1 : 0));
      var1.put((byte)(this.north ? 1 : 0));
      var1.putInt(this.Barricaded);
      var1.putShort((short)this.BarricideMaxStrength);
      var1.putInt(this.Health);
      var1.putInt(this.MaxHealth);
      var1.putInt(this.BarricideStrength);
      var1.putInt(this.closedSprite.ID);
      var1.putInt(this.openSprite.ID);
      var1.put((byte)(this.MetalBarricaded ? 1 : 0));
   }

   public void Barricade(IsoGameCharacter var1, InventoryItem var2) {
      if (var2 != null) {
         this.DirtySlice();
         this.square.InvalidateSpecialObjectPaths();
         if (this.Barricaded < 4) {
            if (this.open) {
               this.ToggleDoor(var1);
            }

            IsoGridSquare.setRecalcLightTime(-1);
            if (var1 != null) {
               this.BarricideMaxStrength += (int)(1000.0F * ((float)var2.getCondition() / (float)var2.getConditionMax()) * var1.getBarricadeStrengthMod());
               this.BarricideStrength += (int)(1000.0F * ((float)var2.getCondition() / (float)var2.getConditionMax()) * var1.getBarricadeStrengthMod());
            } else {
               this.BarricideMaxStrength += (int)(1000.0F * ((float)var2.getCondition() / (float)var2.getConditionMax()));
               this.BarricideStrength += (int)(1000.0F * ((float)var2.getCondition() / (float)var2.getConditionMax()));
            }

            if (this.barricadeSprite != null && this.AttachedAnimSprite != null) {
               this.AttachedAnimSprite.remove(this.barricadeSprite);
            }

            Integer var3 = 8;
            if (this.north) {
               var3 = var3 + 1;
            }

            var3 = var3 + this.Barricaded * 2;
            this.barricadeSprite = IsoSpriteInstance.get(IsoSprite.getSprite(IsoWorld.instance.spriteManager, (String)("TileBarricade_" + var3), 0));
            this.AttachedAnimSprite.add(this.barricadeSprite);
            ++this.Barricaded;
         }
      }
   }

   public void DoTooltip(ObjectTooltip var1) {
      byte var2 = 60;
      String var3 = "";
      switch(this.type) {
      case WeakWooden:
         var3 = "Wooden Wall";
         break;
      case StrongWooden:
         var3 = "Strong Wooden Wall";
      }

      if (this.Barricaded > 0) {
         var3 = "Barricaded Wall";
         if (this.IsStrengthenedByPushedItems()) {
            var3 = "Heavy Barricaded Wall";
         }
      } else if (this.IsStrengthenedByPushedItems()) {
         var3 = "Blocked Wall";
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
      if (this.Barricaded > 0) {
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
      return false;
   }

   public boolean onMouseLeftClick(int var1, int var2) {
      return true;
   }

   public boolean TestPathfindCollide(IsoMovingObject var1, IsoGridSquare var2, IsoGridSquare var3) {
      boolean var4 = this.north;
      if (this.Barricaded == 0) {
         return false;
      } else if (var1 instanceof IsoSurvivor && ((IsoSurvivor)var1).getInventory().contains("Hammer")) {
         return false;
      } else {
         if (this.open) {
            var4 = !var4;
         }

         if (var2 == this.square) {
            if (var4 && var3.getY() < var2.getY()) {
               return true;
            }

            if (!var4 && var3.getX() < var2.getX()) {
               return true;
            }
         } else {
            if (var4 && var3.getY() > var2.getY()) {
               return true;
            }

            if (!var4 && var3.getX() > var2.getX()) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean TestCollide(IsoMovingObject var1, IsoGridSquare var2, IsoGridSquare var3) {
      boolean var4 = this.north;
      if (this.open) {
         var4 = !var4;
      }

      if (var2 == this.square) {
         if (var4 && var3.getY() < var2.getY()) {
            if (var1 != null) {
               var1.collideWith(this);
            }

            return true;
         }

         if (!var4 && var3.getX() < var2.getX()) {
            if (var1 != null) {
               var1.collideWith(this);
            }

            return true;
         }
      } else {
         if (var4 && var3.getY() > var2.getY()) {
            if (var1 != null) {
               var1.collideWith(this);
            }

            return true;
         }

         if (!var4 && var3.getX() > var2.getX()) {
            if (var1 != null) {
               var1.collideWith(this);
            }

            return true;
         }
      }

      return false;
   }

   public IsoObject.VisionResult TestVision(IsoGridSquare var1, IsoGridSquare var2) {
      boolean var3 = this.north;
      if (this.open) {
         var3 = !var3;
      }

      if (var2.getZ() != var1.getZ()) {
         return IsoObject.VisionResult.NoEffect;
      } else {
         if (var1 == this.square) {
            if (var3 && var2.getY() < var1.getY()) {
               return IsoObject.VisionResult.Blocked;
            }

            if (!var3 && var2.getX() < var1.getX()) {
               return IsoObject.VisionResult.Blocked;
            }
         } else {
            if (var3 && var2.getY() > var1.getY()) {
               return IsoObject.VisionResult.Blocked;
            }

            if (!var3 && var2.getX() > var1.getX()) {
               return IsoObject.VisionResult.Blocked;
            }
         }

         return IsoObject.VisionResult.NoEffect;
      }
   }

   public void Thump(IsoMovingObject var1) {
      if (!this.isDestroyed()) {
         int var2;
         int var3;
         int var4;
         if (var1 instanceof IsoZombie) {
            var2 = var1.getCurrentSquare().getMovingObjects().size();
            if (var1.getCurrentSquare().getW() != null) {
               var2 += var1.getCurrentSquare().getW().getMovingObjects().size();
            }

            if (var1.getCurrentSquare().getE() != null) {
               var2 += var1.getCurrentSquare().getE().getMovingObjects().size();
            }

            if (var1.getCurrentSquare().getS() != null) {
               var2 += var1.getCurrentSquare().getS().getMovingObjects().size();
            }

            if (var1.getCurrentSquare().getN() != null) {
               var2 += var1.getCurrentSquare().getN().getMovingObjects().size();
            }

            var3 = this.Barricaded > 0 ? 1 : 8;
            if (var2 >= var3 && !this.MetalBarricaded) {
               var4 = this.Barricaded;
               int var5 = ThumpState.getFastForwardDamageMultiplier();
               this.DirtySlice();
               this.Damage(1 * var5);
               if (GameServer.bServer && var4 > 0 && this.Barricaded <= 0) {
                  GameServer.PlayWorldSoundServer("breakdoor", false, this.getSquare(), 0.2F, 20.0F, 1.1F, true);
               }
            }

            WorldSoundManager.instance.addSound(var1, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
         }

         if (this.Health <= 0) {
            SoundManager.instance.PlayWorldSound("breakdoor", var1.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
            WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
            var1.setThumpTarget((Thumpable)null);
            this.destroyed = true;
            this.square.getObjects().remove(this);
            this.square.getSpecialObjects().remove(this);
            var2 = Rand.Next(2) + 1;

            for(var3 = 0; var3 < var2; ++var3) {
               this.square.AddWorldInventoryItem("Base.Plank", Rand.Next(-1.0F, 1.0F), Rand.Next(-1.0F, 1.0F), 0.0F);
            }

            this.square.AddWorldInventoryItem("Base.Doorknob", Rand.Next(-1.0F, 1.0F), Rand.Next(-1.0F, 1.0F), 0.0F);
            var3 = Rand.Next(3);

            for(var4 = 0; var4 < var3; ++var4) {
               this.square.AddWorldInventoryItem("Base.Hinge", Rand.Next(-1.0F, 1.0F), Rand.Next(-1.0F, 1.0F), 0.0F);
            }
         }

      }
   }

   public void WeaponHit(IsoGameCharacter var1, HandWeapon var2) {
      if (GameClient.bClient) {
         if (var1 instanceof IsoPlayer) {
            GameClient.instance.sendWeaponHit((IsoPlayer)var1, var2, this);
         }

      } else if (!this.open) {
         if (!this.isDestroyed()) {
            int var3 = this.Barricaded;
            this.Damage(var2.getDoorDamage());
            this.DirtySlice();
            if (var2.getDoorHitSound() != null) {
               var1.getEmitter().playSound(var2.getDoorHitSound(), this);
               if (GameServer.bServer) {
                  GameServer.PlayWorldSoundServer(var2.getDoorHitSound(), false, this.getSquare(), 0.2F, 20.0F, 1.0F, true);
               }
            }

            WorldSoundManager.instance.addSound(var1, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
            if (GameServer.bServer && var3 > 0 && this.Barricaded <= 0) {
               GameServer.PlayWorldSoundServer("breakdoor", false, this.getSquare(), 0.2F, 20.0F, 1.1F, true);
            }

            if (!this.IsStrengthenedByPushedItems() && this.Health <= 0 || this.IsStrengthenedByPushedItems() && this.Health <= -this.PushedMaxStrength) {
               var1.getEmitter().playSound("breakdoor", this);
               if (GameServer.bServer) {
                  GameServer.PlayWorldSoundServer("breakdoor", false, this.square, 0.2F, 20.0F, 1.1F, true);
               }

               WorldSoundManager.instance.addSound(var1, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
               var3 = Rand.Next(2) + 1;

               int var4;
               for(var4 = 0; var4 < var3; ++var4) {
                  this.square.AddWorldInventoryItem("Base.Plank", Rand.Next(-1.0F, 1.0F), Rand.Next(-1.0F, 1.0F), 0.0F);
               }

               this.square.AddWorldInventoryItem("Base.Doorknob", Rand.Next(-1.0F, 1.0F), Rand.Next(-1.0F, 1.0F), 0.0F);
               var4 = Rand.Next(3);

               for(int var5 = 0; var5 < var4; ++var5) {
                  this.square.AddWorldInventoryItem("Base.Hinge", Rand.Next(-1.0F, 1.0F), Rand.Next(-1.0F, 1.0F), 0.0F);
               }

               this.destroyed = true;
               this.square.getObjects().remove(this);
               this.square.getSpecialObjects().remove(this);
            }

         }
      }
   }

   public IsoGridSquare getOtherSideOfDoor(IsoGameCharacter var1) {
      if (this.north) {
         return var1.getCurrentSquare().getRoom() == this.square.getRoom() ? IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ()) : IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ());
      } else {
         return var1.getCurrentSquare().getRoom() == this.square.getRoom() ? IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ()) : IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ());
      }
   }

   public void ToggleDoorActual(IsoGameCharacter var1) {
      this.DirtySlice();
      this.square.InvalidateSpecialObjectPaths();
      if (var1 instanceof IsoPlayer && !this.open) {
      }

      if (var1 instanceof IsoSurvivor && var1.getInventory().contains("Hammer")) {
         if (this.Barricaded > 0) {
            this.Unbarricade(var1);
         }
      } else if (this.Barricaded > 0 || this.MetalBarricaded) {
         return;
      }

      if (this.Locked && var1 != null && var1 instanceof IsoPlayer && var1.getCurrentSquare().getRoom() == null && !this.open) {
         var1.getEmitter().playSound("DoorIsLocked", this);
         var1.Say("Gah, locked!");
         if (var1 instanceof IsoSurvivor) {
            var1.getMasterBehaviorList().reset();
         }

      } else {
         if (var1 instanceof IsoPlayer) {
            for(int var2 = 0; var2 < IsoPlayer.numPlayers; ++var2) {
               LosUtil.cachecleared[var2] = true;
            }

            IsoGridSquare.setRecalcLightTime(-1);
         }

         this.open = !this.open;
         this.sprite = this.closedSprite;
         if (this.open) {
            var1.getEmitter().playSound("OpenDoor", this);
            this.sprite = this.openSprite;
         } else {
            var1.getEmitter().playSound("CloseDoor", this);
         }

      }
   }

   public void ToggleDoor(IsoGameCharacter var1) {
      this.ToggleDoorActual(var1);
   }

   public void ToggleDoorSilent() {
      if (this.Barricaded <= 0 && !this.MetalBarricaded) {
         this.square.InvalidateSpecialObjectPaths();
         LosUtil.cachecleared[IsoPlayer.getPlayerIndex()] = true;
         IsoGridSquare.setRecalcLightTime(-1);
         this.open = !this.open;
         this.sprite = this.closedSprite;
         if (this.open) {
            this.sprite = this.openSprite;
         }

      }
   }

   public void Unbarricade(IsoGameCharacter var1) {
      this.DirtySlice();
      float var2;
      if (this.MetalBarricaded && var1 != null) {
         var2 = (float)this.BarricideStrength / (float)this.BarricideMaxStrength;
         InventoryItem var5 = InventoryItemFactory.CreateItem("Base.SheetMetal");
         var5.setCondition((int)((float)var5.getConditionMax() * var2));
         if (var5.getCondition() < 0) {
            var5.setCondition(0);
         }

         var1.getInventory().AddItem(var5);
      } else {
         if (this.Barricaded == 0) {
            return;
         }

         if (var1 != null && this.BarricideMaxStrength > 0) {
            var2 = (float)this.BarricideStrength / (float)this.BarricideMaxStrength;

            for(int var3 = 0; var3 < this.Barricaded; ++var3) {
               InventoryItem var4 = InventoryItemFactory.CreateItem("Base.Plank");
               var4.setCondition((int)((float)var4.getConditionMax() * var2));
               if (var4.getCondition() < 0) {
                  var4.setCondition(0);
               }

               var1.getInventory().AddItem(var4);
            }
         }
      }

      this.square.InvalidateSpecialObjectPaths();
      if (var1 != null) {
         var1.getEmitter().playSound("woodfall", this);
      }

      if (this.AttachedAnimSprite != null) {
         this.AttachedAnimSprite.clear();
      }

      this.Barricaded = 0;
      this.BarricideStrength = 0;
      this.BarricideMaxStrength = 0;
   }

   void Damage(int var1) {
      this.DirtySlice();
      if (this.Barricaded > 0) {
         this.BarricideStrength -= var1;
         if (this.BarricideStrength <= 0) {
            this.Unbarricade((IsoGameCharacter)null);
         }
      } else if (this.MetalBarricaded) {
         this.BarricideStrength -= var1;
         if (this.BarricideStrength <= this.BarricideMaxStrength / 2) {
            int var2 = 26;
            if (this.north) {
               ++var2;
            }

            this.barricadeSprite = new IsoSpriteInstance(IsoSprite.getSprite(IsoWorld.instance.spriteManager, (String)("constructedobjects_01_" + var2), 0));
            if (this.AttachedAnimSprite == null) {
               this.AttachedAnimSprite = new ArrayList(4);
               this.AttachedAnimSpriteActual = new ArrayList(4);
            }

            this.AttachedAnimSprite.add(this.barricadeSprite);
            this.AttachedAnimSpriteActual.add(IsoSprite.getSprite(IsoWorld.instance.spriteManager, (String)("constructedobjects_01_" + var2), 0));
         }

         if (this.BarricideStrength <= 0) {
            this.Unbarricade((IsoGameCharacter)null);
         }
      } else {
         this.Health -= var1;
      }

   }

   public void MetalBarricade(IsoGameCharacter var1, InventoryItem var2) {
      this.DirtySlice();
      IsoGridSquare.setRecalcLightTime(-1);
      if (var1 != null) {
         this.BarricideMaxStrength += (int)(5000.0F * ((float)var2.getCondition() / (float)var2.getConditionMax()) * var1.getBarricadeStrengthMod());
         this.BarricideStrength += (int)(5000.0F * ((float)var2.getCondition() / (float)var2.getConditionMax()) * var1.getBarricadeStrengthMod());
      } else {
         this.BarricideMaxStrength += (int)(5000.0F * ((float)var2.getCondition() / (float)var2.getConditionMax()));
         this.BarricideStrength += (int)(5000.0F * ((float)var2.getCondition() / (float)var2.getConditionMax()));
      }

      if (this.barricadeSprite != null && this.AttachedAnimSprite != null) {
         this.AttachedAnimSprite.remove(this.barricadeSprite);
      }

      int var3 = 24;
      if (this.north) {
         ++var3;
      }

      this.barricadeSprite = new IsoSpriteInstance(IsoSprite.getSprite(IsoWorld.instance.spriteManager, (String)("constructedobjects_01_" + var3), 0));
      if (this.AttachedAnimSprite == null) {
         this.AttachedAnimSprite = new ArrayList(4);
         this.AttachedAnimSpriteActual = new ArrayList(4);
      }

      this.AttachedAnimSprite.add(this.barricadeSprite);
      this.AttachedAnimSpriteActual.add(IsoSprite.getSprite(IsoWorld.instance.spriteManager, (String)("constructedobjects_01_" + var3), 0));
      this.square.InvalidateSpecialObjectPaths();

      for(int var4 = 0; var4 < IsoPlayer.numPlayers; ++var4) {
         LosUtil.cachecleared[var4] = true;
      }

      IsoGridSquare.setRecalcLightTime(-1);
      GameTime.instance.lightSourceUpdate = 100.0F;
      this.MetalBarricaded = true;
      if (this.square != null) {
         this.square.RecalcProperties();
      }

   }

   public boolean isMetalBarricaded() {
      return this.MetalBarricaded;
   }

   public static enum DoorType {
      WeakWooden,
      StrongWooden;
   }
}
