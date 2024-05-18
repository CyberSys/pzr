package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.core.Core;
import zombie.core.textures.ColorInfo;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.DrainableComboItem;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoHeatSource;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;

public class IsoBarbecue extends IsoObject {
   boolean bHasPropaneTank = false;
   int FuelAmount = 0;
   boolean bLit = false;
   protected float LastUpdateTime = -1.0F;
   protected float MinuteAccumulator = 0.0F;
   protected int MinutesSinceExtinguished = -1;
   IsoSprite normalSprite = null;
   IsoSprite noTankSprite = null;
   private IsoHeatSource heatSource;
   private static int SMOULDER_MINUTES = 10;

   public IsoBarbecue(IsoCell var1) {
      super(var1);
   }

   public IsoBarbecue(IsoCell var1, IsoGridSquare var2, IsoSprite var3) {
      super(var1, var2, var3);
      this.container = new ItemContainer("barbecue", var2, this, 1, 1);
      this.container.setExplored(true);
      this.bHasPropaneTank = this.isPropaneBBQ();
      if (this.bHasPropaneTank) {
         this.FuelAmount = 1200;
      }

      this.normalSprite = this.sprite;
      if (this.sprite != null && this.bHasPropaneTank) {
         byte var4 = 8;
         this.noTankSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (IsoSprite)this.sprite, var4);
      }

   }

   public String getObjectName() {
      return "Barbecue";
   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      super.load(var1, var2);
      this.bHasPropaneTank = var1.get() == 1;
      this.FuelAmount = var1.getInt();
      this.bLit = var1.get() == 1;
      this.LastUpdateTime = var1.getFloat();
      this.MinutesSinceExtinguished = var1.getInt();
      if (var1.get() == 1) {
         this.normalSprite = IsoSprite.getSprite(this.getCell().SpriteManager, var1.getInt());
      }

      if (var1.get() == 1) {
         this.noTankSprite = IsoSprite.getSprite(this.getCell().SpriteManager, var1.getInt());
      }

   }

   public void save(ByteBuffer var1) throws IOException {
      super.save(var1);
      var1.put((byte)(this.bHasPropaneTank ? 1 : 0));
      var1.putInt(this.FuelAmount);
      var1.put((byte)(this.bLit ? 1 : 0));
      var1.putFloat(this.LastUpdateTime);
      var1.putInt(this.MinutesSinceExtinguished);
      if (this.normalSprite != null) {
         var1.put((byte)1);
         var1.putInt(this.normalSprite.ID);
      } else {
         var1.put((byte)0);
      }

      if (this.noTankSprite != null) {
         var1.put((byte)1);
         var1.putInt(this.noTankSprite.ID);
      } else {
         var1.put((byte)0);
      }

   }

   public void setFuelAmount(int var1) {
      var1 = Math.max(0, var1);
      int var2 = this.getFuelAmount();
      if (var1 != var2) {
         this.FuelAmount = var1;
      }

   }

   public int getFuelAmount() {
      return this.FuelAmount;
   }

   public void addFuel(int var1) {
      this.setFuelAmount(this.getFuelAmount() + var1);
   }

   public int useFuel(int var1) {
      int var2 = this.getFuelAmount();
      boolean var3 = false;
      int var4;
      if (var2 >= var1) {
         var4 = var1;
      } else {
         var4 = var2;
      }

      this.setFuelAmount(var2 - var4);
      return var4;
   }

   public boolean hasFuel() {
      return this.getFuelAmount() > 0;
   }

   public boolean hasPropaneTank() {
      return this.isPropaneBBQ() && this.bHasPropaneTank;
   }

   public boolean isPropaneBBQ() {
      return this.getSprite() != null && this.getProperties().Is("propaneTank");
   }

   public void setPropaneTank(InventoryItem var1) {
      if (var1.getFullType().equals("Base.PropaneTank")) {
         this.bHasPropaneTank = true;
         this.FuelAmount = 1200;
         if (var1 instanceof DrainableComboItem) {
            this.FuelAmount = (int)((float)this.FuelAmount * ((DrainableComboItem)var1).getUsedDelta());
         }
      }

   }

   public InventoryItem removePropaneTank() {
      if (!this.bHasPropaneTank) {
         return null;
      } else {
         this.bHasPropaneTank = false;
         this.bLit = false;
         InventoryItem var1 = InventoryItemFactory.CreateItem("Base.PropaneTank");
         if (var1 instanceof DrainableComboItem) {
            ((DrainableComboItem)var1).setUsedDelta((float)this.getFuelAmount() / 1200.0F);
         }

         this.FuelAmount = 0;
         return var1;
      }
   }

   public void setLit(boolean var1) {
      this.bLit = var1;
   }

   public boolean isLit() {
      return this.bLit;
   }

   public void turnOn() {
      if (!this.isLit()) {
         this.setLit(true);
      }

   }

   public void turnOff() {
      if (this.isLit()) {
         this.setLit(false);
      }

   }

   public void toggle() {
      this.setLit(!this.isLit());
   }

   public void extinguish() {
      if (this.isLit()) {
         this.setLit(false);
         if (this.hasFuel() && !this.isPropaneBBQ()) {
            this.MinutesSinceExtinguished = 0;
         }
      }

   }

   public float getTemperature() {
      return this.isLit() ? 1.8F : 1.0F;
   }

   private void updateSprite() {
      if (this.isPropaneBBQ()) {
         if (this.hasPropaneTank()) {
            this.sprite = this.normalSprite;
         } else {
            this.sprite = this.noTankSprite;
         }
      }

   }

   private void updateHeatSource() {
      if (this.isLit()) {
         if (this.heatSource == null) {
            this.heatSource = new IsoHeatSource((int)this.getX(), (int)this.getY(), (int)this.getZ(), 3, 25);
            IsoWorld.instance.CurrentCell.addHeatSource(this.heatSource);
         }
      } else if (this.heatSource != null) {
         IsoWorld.instance.CurrentCell.removeHeatSource(this.heatSource);
         this.heatSource = null;
      }

   }

   public void update() {
      if (!GameClient.bClient) {
         boolean var1 = this.hasFuel();
         boolean var2 = this.isLit();
         float var3 = (float)GameTime.getInstance().getWorldAgeHours();
         if (this.LastUpdateTime < 0.0F) {
            this.LastUpdateTime = var3;
         } else if (this.LastUpdateTime > var3) {
            this.LastUpdateTime = var3;
         }

         if (var3 > this.LastUpdateTime) {
            this.MinuteAccumulator += (var3 - this.LastUpdateTime) * 60.0F;
            int var4 = (int)Math.floor((double)this.MinuteAccumulator);
            if (var4 > 0) {
               if (this.isLit()) {
                  DebugLog.log(DebugType.Fireplace, "IsoBarbecue burned " + var4 + " minutes (" + this.getFuelAmount() + " remaining)");
                  this.useFuel(var4);
                  if (!this.hasFuel()) {
                     this.extinguish();
                  }
               } else if (this.MinutesSinceExtinguished != -1) {
                  int var5 = Math.min(var4, SMOULDER_MINUTES - this.MinutesSinceExtinguished);
                  DebugLog.log(DebugType.Fireplace, "IsoBarbecue smoldered " + var5 + " minutes (" + this.getFuelAmount() + " remaining)");
                  this.MinutesSinceExtinguished += var4;
                  this.useFuel(var5);
                  if (!this.hasFuel() || this.MinutesSinceExtinguished >= SMOULDER_MINUTES) {
                     this.MinutesSinceExtinguished = -1;
                  }
               }

               this.MinuteAccumulator -= (float)var4;
            }
         }

         this.LastUpdateTime = var3;
         if (GameServer.bServer) {
            if (var1 != this.hasFuel() || var2 != this.isLit()) {
               this.sendObjectChange("state");
            }

            return;
         }
      }

      this.updateSprite();
      this.updateHeatSource();
      if (this.isLit() && (this.AttachedAnimSprite == null || this.AttachedAnimSprite.isEmpty())) {
         ColorInfo var6 = new ColorInfo(0.95F, 0.95F, 0.85F, 1.0F);
         this.AttachAnim("Smoke", "01", 4, IsoFireManager.SmokeAnimDelay, -14, 58, true, 0, false, 0.7F, var6);
         ((IsoSpriteInstance)this.AttachedAnimSprite.get(0)).alpha = ((IsoSpriteInstance)this.AttachedAnimSprite.get(0)).targetAlpha = 0.55F;
         ((IsoSpriteInstance)this.AttachedAnimSprite.get(0)).bCopyTargetAlpha = false;
      } else if (!this.isLit() && this.AttachedAnimSprite != null && !this.AttachedAnimSprite.isEmpty()) {
         this.RemoveAttachedAnims();
      }

      if (this.AttachedAnimSprite != null && !this.AttachedAnimSprite.isEmpty()) {
         int var7 = this.AttachedAnimSprite.size();

         for(int var8 = 0; var8 < var7; ++var8) {
            IsoSpriteInstance var9 = (IsoSpriteInstance)this.AttachedAnimSprite.get(var8);
            IsoSprite var10 = (IsoSprite)this.AttachedAnimSpriteActual.get(var8);
            var9.update();
            float var11 = GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F;
            var9.Frame += var9.AnimFrameIncrease * var11;
            if ((int)var9.Frame >= var10.CurrentAnim.Frames.size() && var10.Loop && var9.Looped) {
               var9.Frame = 0.0F;
            }
         }
      }

   }

   public void setSprite(IsoSprite var1) {
      this.noTankSprite = var1;
      this.normalSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (IsoSprite)var1, -8);
   }

   public void addToWorld() {
      IsoCell var1 = this.getCell();
      if (!var1.getProcessIsoObjects().contains(this)) {
         var1.getProcessIsoObjects().add(this);
      }

      this.container.addItemsToProcessItems();
   }

   public void removeFromWorld() {
      if (this.heatSource != null) {
         IsoWorld.instance.CurrentCell.removeHeatSource(this.heatSource);
         this.heatSource = null;
      }

      super.removeFromWorld();
   }

   public void render(float var1, float var2, float var3, ColorInfo var4, boolean var5) {
      if (this.AttachedAnimSpriteActual != null) {
         int var6 = Core.TileScale;

         for(int var7 = 0; var7 < this.AttachedAnimSpriteActual.size(); ++var7) {
            IsoSprite var8 = (IsoSprite)this.AttachedAnimSpriteActual.get(var7);
            var8.soffX = (short)(14 * var6);
            var8.soffY = (short)(-58 * var6);
            ((IsoSpriteInstance)this.AttachedAnimSprite.get(var7)).setScale((float)var6, (float)var6);
         }
      }

      super.render(var1, var2, var3, var4, var5);
   }

   public void saveChange(String var1, KahluaTable var2, ByteBuffer var3) {
      if ("state".equals(var1)) {
         var3.putInt(this.getFuelAmount());
         var3.put((byte)(this.isLit() ? 1 : 0));
         var3.put((byte)(this.hasPropaneTank() ? 1 : 0));
      }

   }

   public void loadChange(String var1, ByteBuffer var2) {
      if ("state".equals(var1)) {
         this.setFuelAmount(var2.getInt());
         this.setLit(var2.get() == 1);
         this.bHasPropaneTank = var2.get() == 1;
      }

   }
}
