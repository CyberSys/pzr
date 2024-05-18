package zombie.iso;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.inventory.ItemContainer;
import zombie.iso.objects.IsoWheelieBin;
import zombie.iso.sprite.IsoSprite;

public class IsoPushableObject extends IsoMovingObject {
   float velx = 0.0F;
   float vely = 0.0F;
   public int carryCapacity = 100;
   public float emptyWeight = 4.5F;
   public ArrayList connectList = null;
   public float ox = 0.0F;
   public float oy = 0.0F;

   public String getObjectName() {
      return "Pushable";
   }

   public IsoPushableObject(IsoCell var1) {
      super(var1);
      this.getCell().getPushableObjectList().add(this);
   }

   public IsoPushableObject(IsoCell var1, int var2, int var3, int var4) {
      super(var1, true);
      this.getCell().getPushableObjectList().add(this);
   }

   public IsoPushableObject(IsoCell var1, IsoGridSquare var2, IsoSprite var3) {
      super(var1, var2, var3, true);
      this.setX((float)var2.getX() + 0.5F);
      this.setY((float)var2.getY() + 0.5F);
      this.setZ((float)var2.getZ());
      this.ox = this.getX();
      this.oy = this.getY();
      this.setNx(this.getX());
      this.setNy(this.getNy());
      this.offsetX = (float)(6 * Core.TileScale);
      this.offsetY = (float)(-30 * Core.TileScale);
      this.setWeight(6.0F);
      this.square = var2;
      this.setCurrent(var2);
      this.getCurrentSquare().getMovingObjects().add(this);
      this.Collidable = true;
      this.solid = true;
      this.shootable = false;
      this.width = 0.5F;
      this.alpha[IsoPlayer.getPlayerIndex()] = 0.0F;
      this.targetAlpha[IsoPlayer.getPlayerIndex()] = 0.0F;
      this.getCell().getPushableObjectList().add(this);
   }

   public void update() {
      float var1 = 0.0F;
      if (this.connectList != null) {
         Iterator var2 = this.connectList.iterator();

         while(var2.hasNext()) {
            IsoPushableObject var3 = (IsoPushableObject)var2.next();
            if (var3.alpha[IsoPlayer.getPlayerIndex()] > var1) {
               var1 = var3.alpha[IsoPlayer.getPlayerIndex()];
            }
         }

         this.alpha[IsoPlayer.getPlayerIndex()] = var1;
         this.targetAlpha[IsoPlayer.getPlayerIndex()] = var1;
      }

      super.update();
   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      super.load(var1, var2);
      if (!(this instanceof IsoWheelieBin)) {
         this.sprite = IsoWorld.instance.spriteManager.getSprite(var1.getInt());
      }

      if (var1.get() == 1) {
         this.container = new ItemContainer();
         this.container.load(var1, var2, false);
      }

   }

   public void save(ByteBuffer var1) throws IOException {
      super.save(var1);
      if (!(this instanceof IsoWheelieBin)) {
         var1.putInt(this.sprite.ID);
      }

      if (this.container != null) {
         var1.put((byte)1);
         this.container.save(var1, false);
      } else {
         var1.put((byte)0);
      }

   }

   public float getWeight(float var1, float var2) {
      if (this.container == null) {
         return this.emptyWeight;
      } else {
         float var3 = this.container.getContentsWeight() / (float)this.carryCapacity;
         if (var3 < 0.0F) {
            var3 = 0.0F;
         }

         if (var3 > 1.0F) {
            return this.getWeight() * 8.0F;
         } else {
            float var4 = this.getWeight() * var3 + this.emptyWeight;
            return var4;
         }
      }
   }

   public boolean Serialize() {
      return true;
   }

   public void DoCollideNorS() {
      if (this.connectList == null) {
         super.DoCollideNorS();
      } else {
         Iterator var1 = this.connectList.iterator();

         while(var1.hasNext()) {
            IsoPushableObject var2 = (IsoPushableObject)var1.next();
            if (var2 != this) {
               if (var2.ox < this.ox) {
                  var2.setNx(this.getNx() - 1.0F);
                  var2.setX(this.getX() - 1.0F);
               } else if (var2.ox > this.ox) {
                  var2.setNx(this.getNx() + 1.0F);
                  var2.setX(this.getX() + 1.0F);
               } else {
                  var2.setNx(this.getNx());
                  var2.setX(this.getX());
               }

               if (var2.oy < this.oy) {
                  var2.setNy(this.getNy() - 1.0F);
                  var2.setY(this.getY() - 1.0F);
               } else if (var2.oy > this.oy) {
                  var2.setNy(this.getNy() + 1.0F);
                  var2.setY(this.getY() + 1.0F);
               } else {
                  var2.setNy(this.getNy());
                  var2.setY(this.getY());
               }

               var2.setImpulsex(this.getImpulsex());
               var2.setImpulsey(this.getImpulsey());
            }
         }

      }
   }

   public void DoCollideWorE() {
      if (this.connectList == null) {
         super.DoCollideWorE();
      } else {
         Iterator var1 = this.connectList.iterator();

         while(var1.hasNext()) {
            IsoPushableObject var2 = (IsoPushableObject)var1.next();
            if (var2 != this) {
               if (var2.ox < this.ox) {
                  var2.setNx(this.getNx() - 1.0F);
                  var2.setX(this.getX() - 1.0F);
               } else if (var2.ox > this.ox) {
                  var2.setNx(this.getNx() + 1.0F);
                  var2.setX(this.getX() + 1.0F);
               } else {
                  var2.setNx(this.getNx());
                  var2.setX(this.getX());
               }

               if (var2.oy < this.oy) {
                  var2.setNy(this.getNy() - 1.0F);
                  var2.setY(this.getY() - 1.0F);
               } else if (var2.oy > this.oy) {
                  var2.setNy(this.getNy() + 1.0F);
                  var2.setY(this.getY() + 1.0F);
               } else {
                  var2.setNy(this.getNy());
                  var2.setY(this.getY());
               }

               var2.setImpulsex(this.getImpulsex());
               var2.setImpulsey(this.getImpulsey());
            }
         }

      }
   }
}
