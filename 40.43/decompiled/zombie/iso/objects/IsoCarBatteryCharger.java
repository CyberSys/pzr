package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.DrainableComboItem;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;

public class IsoCarBatteryCharger extends IsoObject {
   protected InventoryItem item;
   protected InventoryItem battery;
   protected boolean activated;
   protected float lastUpdate = -1.0F;
   protected float chargeRate = 0.16666667F;
   protected IsoSprite chargerSprite;
   protected IsoSprite batterySprite;

   public IsoCarBatteryCharger(IsoCell var1) {
      super(var1);
   }

   public IsoCarBatteryCharger(InventoryItem var1, IsoCell var2, IsoGridSquare var3) {
      super(var2, var3, (IsoSprite)null);
      if (var1 == null) {
         throw new NullPointerException("item is null");
      } else {
         this.item = var1;
      }
   }

   public String getObjectName() {
      return "IsoCarBatteryCharger";
   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      super.load(var1, var2);
      String var3;
      if (var1.get() == 1) {
         var3 = GameWindow.ReadString(var1);
         var1.get();
         this.item = InventoryItemFactory.CreateItem(var3);
         this.item.load(var1, var2, false);
      }

      if (var1.get() == 1) {
         var3 = GameWindow.ReadString(var1);
         var1.get();
         this.battery = InventoryItemFactory.CreateItem(var3);
         this.battery.load(var1, var2, false);
      }

      this.activated = var1.get() == 1;
      this.lastUpdate = var1.getFloat();
      this.chargeRate = var1.getFloat();
   }

   public void save(ByteBuffer var1) throws IOException {
      super.save(var1);
      if (this.item == null) {
         assert false;

         var1.put((byte)0);
      } else {
         var1.put((byte)1);
         this.item.save(var1, false);
      }

      if (this.battery == null) {
         var1.put((byte)0);
      } else {
         var1.put((byte)1);
         this.battery.save(var1, false);
      }

      var1.put((byte)(this.activated ? 1 : 0));
      var1.putFloat(this.lastUpdate);
      var1.putFloat(this.chargeRate);
   }

   public void addToWorld() {
      super.addToWorld();
      this.getCell().addToProcessIsoObject(this);
   }

   public void removeFromWorld() {
      super.removeFromWorld();
   }

   public void update() {
      super.update();
      if (!(this.battery instanceof DrainableComboItem)) {
         this.battery = null;
      }

      if (this.battery == null) {
         this.lastUpdate = -1.0F;
         this.activated = false;
      } else {
         boolean var1 = this.square != null && (this.square.haveElectricity() || GameTime.instance.NightsSurvived < SandboxOptions.instance.getElecShutModifier() && this.square.getRoom() != null);
         if (!var1) {
            this.activated = false;
         }

         if (!this.activated) {
            this.lastUpdate = -1.0F;
         } else {
            DrainableComboItem var2 = (DrainableComboItem)this.battery;
            if (!(var2.getUsedDelta() >= 1.0F)) {
               float var3 = (float)GameTime.getInstance().getWorldAgeHours();
               if (this.lastUpdate < 0.0F) {
                  this.lastUpdate = var3;
               }

               if (this.lastUpdate > var3) {
                  this.lastUpdate = var3;
               }

               float var4 = var3 - this.lastUpdate;
               if (var4 > 0.0F) {
                  var2.setUsedDelta(Math.min(1.0F, var2.getUsedDelta() + this.chargeRate * var4));
                  this.lastUpdate = var3;
               }

            }
         }
      }
   }

   public void render(float var1, float var2, float var3, ColorInfo var4, boolean var5) {
      this.chargerSprite = this.configureSprite(this.item, this.chargerSprite);
      if (this.chargerSprite.CurrentAnim != null && !this.chargerSprite.CurrentAnim.Frames.isEmpty()) {
         Texture var6 = ((IsoDirectionFrame)this.chargerSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir);
         if (var6 != null) {
            float var7 = (float)var6.getWidthOrig() * this.chargerSprite.def.getScaleX() / 2.0F;
            float var8 = (float)var6.getHeightOrig() * this.chargerSprite.def.getScaleY() * 3.0F / 4.0F;
            this.offsetX = this.offsetY = 0.0F;
            this.alpha[IsoCamera.frameState.playerIndex] = 1.0F;
            float var9 = 0.5F;
            float var10 = 0.5F;
            float var11 = 0.0F;
            this.sx = 0;
            this.chargerSprite.render(this, var1 + var9, var2 + var10, var3 + var11, this.dir, this.offsetX + var7 + (float)(8 * Core.TileScale), this.offsetY + var8 + (float)(4 * Core.TileScale), var4);
            if (this.battery != null) {
               this.batterySprite = this.configureSprite(this.battery, this.batterySprite);
               if (this.batterySprite != null && this.batterySprite.CurrentAnim != null && !this.batterySprite.CurrentAnim.Frames.isEmpty()) {
                  this.sx = 0;
                  this.batterySprite.render(this, var1 + var9, var2 + var10, var3 + var11, this.dir, this.offsetX + var7 - 8.0F + (float)Core.TileScale, this.offsetY + var8 - (float)(4 * Core.TileScale), var4);
               }
            }

         }
      }
   }

   public void renderObjectPicker(float var1, float var2, float var3, ColorInfo var4) {
   }

   private IsoSprite configureSprite(InventoryItem var1, IsoSprite var2) {
      String var4 = var1.getWorldTexture();

      Texture var3;
      try {
         var3 = Texture.getSharedTexture(var4);
         if (var3 == null) {
            var4 = var1.getTex().getName();
         }
      } catch (Exception var6) {
         var4 = "media/inventory/world/WItem_Sack.png";
      }

      var3 = Texture.getSharedTexture(var4);
      boolean var5 = false;
      if (var2 == null) {
         var2 = IsoSprite.CreateSprite(this.getCell().SpriteManager);
      }

      if (var2.CurrentAnim == null) {
         var2.LoadFramesNoDirPageSimple(var4);
         var2.CurrentAnim.name = var4;
         var5 = true;
      } else if (!var4.equals(var2.CurrentAnim.name)) {
         var2.ReplaceCurrentAnimFrames(var4);
         var2.CurrentAnim.name = var4;
         var5 = true;
      }

      if (var5) {
         if (var1.getScriptItem() != null && !var1.getScriptItem().ResizeWorldIcon) {
            if (this.battery.getScriptItem() != null) {
               var2.def.setScale(this.battery.getScriptItem().ScaleWorldIcon, this.battery.getScriptItem().ScaleWorldIcon);
            }
         } else {
            var2.def.scaleAspect((float)var3.getWidthOrig(), (float)var3.getHeightOrig(), (float)(16 * Core.TileScale), (float)(16 * Core.TileScale));
         }
      }

      return var2;
   }

   public void syncIsoObjectSend(ByteBufferWriter var1) {
      byte var2 = (byte)this.getObjectIndex();
      var1.putInt(this.square.getX());
      var1.putInt(this.square.getY());
      var1.putInt(this.square.getZ());
      var1.putByte(var2);
      var1.putByte((byte)1);
      var1.putByte((byte)0);
      if (this.battery == null) {
         var1.putByte((byte)0);
      } else {
         var1.putByte((byte)1);

         try {
            this.battery.save(var1.bb, false);
         } catch (IOException var4) {
            var4.printStackTrace();
         }
      }

      var1.putBoolean(this.activated);
      var1.putFloat(this.chargeRate);
   }

   public void syncIsoObject(boolean var1, byte var2, UdpConnection var3, ByteBuffer var4) {
      if (GameClient.bClient && !var1) {
         ByteBufferWriter var10 = GameClient.connection.startPacket();
         PacketTypes.doPacket((short)12, var10);
         this.syncIsoObjectSend(var10);
         GameClient.connection.endPacketImmediate();
      } else {
         UdpConnection var6;
         ByteBufferWriter var7;
         Iterator var9;
         if (GameServer.bServer && !var1) {
            var9 = GameServer.udpEngine.connections.iterator();

            while(var9.hasNext()) {
               var6 = (UdpConnection)var9.next();
               var7 = var6.startPacket();
               PacketTypes.doPacket((short)12, var7);
               this.syncIsoObjectSend(var7);
               var6.endPacketImmediate();
            }
         } else if (var1) {
            if (var4.get() == 1) {
               String var5 = GameWindow.ReadString(var4);
               var4.get();
               this.battery = InventoryItemFactory.CreateItem(var5);

               try {
                  this.battery.load(var4, 143, false);
               } catch (IOException var8) {
                  var8.printStackTrace();
               }
            } else {
               this.battery = null;
            }

            this.activated = var4.get() == 1;
            this.chargeRate = var4.getFloat();
            if (GameServer.bServer) {
               var9 = GameServer.udpEngine.connections.iterator();

               while(var9.hasNext()) {
                  var6 = (UdpConnection)var9.next();
                  if (var3 != null && var6 != var3) {
                     var7 = var6.startPacket();
                     PacketTypes.doPacket((short)12, var7);
                     this.syncIsoObjectSend(var7);
                     var6.endPacketImmediate();
                  }
               }
            }
         }
      }

   }

   public void sync() {
      this.syncIsoObject(false, (byte)0, (UdpConnection)null, (ByteBuffer)null);
   }

   public InventoryItem getItem() {
      return this.item;
   }

   public InventoryItem getBattery() {
      return this.battery;
   }

   public void setBattery(InventoryItem var1) {
      if (var1 != null) {
         if (!(var1 instanceof DrainableComboItem)) {
            throw new IllegalArgumentException("battery isn't DrainableComboItem");
         }

         if (this.battery != null) {
            throw new IllegalStateException("battery already inserted");
         }
      }

      this.battery = var1;
   }

   public boolean isActivated() {
      return this.activated;
   }

   public void setActivated(boolean var1) {
      this.activated = var1;
   }

   public float getChargeRate() {
      return this.chargeRate;
   }

   public void setChargeRate(float var1) {
      if (var1 <= 0.0F) {
         throw new IllegalArgumentException("chargeRate <= 0.0f");
      } else {
         this.chargeRate = var1;
      }
   }
}
