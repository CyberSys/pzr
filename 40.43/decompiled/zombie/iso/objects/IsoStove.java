package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import zombie.GameTime;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.interfaces.Activatable;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;

public class IsoStove extends IsoObject implements Activatable {
   boolean activated = false;
   long soundInstance = -1L;
   private float maxTemperature = 0.0F;
   private double stopTime;
   private double startTime;
   private float currentTemperature = 0.0F;
   private int secondsTimer = -1;
   private boolean firstTurnOn = true;
   private boolean broken = false;
   private boolean hasMetal = false;

   public IsoStove(IsoCell var1, IsoGridSquare var2, IsoSprite var3) {
      super(var1, var2, var3);
   }

   public String getObjectName() {
      return "Stove";
   }

   public IsoStove(IsoCell var1) {
      super(var1);
   }

   public boolean Activated() {
      return this.activated;
   }

   public void update() {
      if (this.Activated() && (this.container == null || !this.container.isPowered())) {
         this.setActivated(false);
         if (this.container != null) {
            this.container.addItemsToProcessItems();
         }
      }

      if (GameServer.bServer && this.Activated() && this.hasMetal && Rand.Next(Rand.AdjustForFramerate(200)) == 100) {
         IsoFireManager.StartFire(this.container.SourceGrid.getCell(), this.container.SourceGrid, true, 10000);
         this.setBroken(true);
         this.activated = false;
         this.stopTime = 0.0D;
         this.startTime = 0.0D;
         this.secondsTimer = -1;
      }

      if (!GameServer.bServer) {
         if (this.Activated()) {
            if (this.stopTime > 0.0D && this.stopTime < GameTime.instance.getWorldAgeHours()) {
               if ("microwave".equals(this.container.getType())) {
                  this.setActivated(false);
               } else if ("stove".equals(this.container.getType())) {
                  this.emitter = IsoWorld.instance.getFreeEmitter(this.getX(), this.getY(), (float)((int)this.getZ()));
                  this.emitter.playSoundImpl("StoveTimerExpired", true, this);
               }

               this.stopTime = 0.0D;
               this.startTime = 0.0D;
               this.secondsTimer = -1;
            }

            if (this.getMaxTemperature() > 0.0F && this.currentTemperature < this.getMaxTemperature()) {
               float var1 = (this.getMaxTemperature() - this.currentTemperature) / 700.0F;
               if (var1 < 0.05F) {
                  var1 = 0.05F;
               }

               this.currentTemperature += var1 * GameTime.instance.getMultiplier();
               if (this.currentTemperature > this.getMaxTemperature()) {
                  this.currentTemperature = this.getMaxTemperature();
               }
            } else if (this.currentTemperature > this.getMaxTemperature()) {
               this.currentTemperature -= (this.currentTemperature - this.getMaxTemperature()) / 1000.0F * GameTime.instance.getMultiplier();
               if (this.currentTemperature < 0.0F) {
                  this.currentTemperature = 0.0F;
               }
            }
         } else if (this.currentTemperature > 0.0F) {
            this.currentTemperature -= 0.1F * GameTime.instance.getMultiplier();
            this.currentTemperature = Math.max(this.currentTemperature, 0.0F);
         }

         if (this.container != null && "microwave".equals(this.container.getType())) {
            if (this.Activated()) {
               this.currentTemperature = this.getMaxTemperature();
            } else {
               this.currentTemperature = 0.0F;
            }
         }

      }
   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      super.load(var1, var2);
      if (var2 >= 28) {
         this.activated = var1.get() == 1;
      }

      if (var2 >= 106) {
         this.secondsTimer = var1.getInt();
         this.maxTemperature = var1.getFloat();
         this.firstTurnOn = var1.get() == 1;
         this.broken = var1.get() == 1;
      }

      if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
         GameClient.instance.objectSyncReq.putRequestLoad(this.square);
      }

   }

   public void save(ByteBuffer var1) throws IOException {
      super.save(var1);
      var1.put((byte)(this.activated ? 1 : 0));
      var1.putInt(this.secondsTimer);
      var1.putFloat(this.maxTemperature);
      var1.put((byte)(this.firstTurnOn ? 1 : 0));
      var1.put((byte)(this.broken ? 1 : 0));
   }

   public void addToWorld() {
      if (this.container != null) {
         IsoCell var1 = this.getCell();
         if (!var1.getProcessIsoObjects().contains(this)) {
            var1.getProcessIsoObjects().add(this);
         }

         this.container.addItemsToProcessItems();
         this.setActivated(this.activated);
      }
   }

   public void Toggle() {
      SoundManager.instance.PlayWorldSound("ToggleStove", this.getSquare(), 1.0F, 1.0F, 1.0F, false);
      this.setActivated(!this.activated);
      this.container.addItemsToProcessItems();
      this.syncIsoObject(false, (byte)(this.activated ? 1 : 0), (UdpConnection)null, (ByteBuffer)null);
   }

   public void sync() {
      this.syncIsoObject(false, (byte)(this.activated ? 1 : 0), (UdpConnection)null, (ByteBuffer)null);
   }

   private void doSound() {
      if (GameServer.bServer) {
         this.hasMetal();
      } else {
         if (this.getContainer() != null && "microwave".equals(this.getContainer().getType())) {
            if (this.activated) {
               if (this.soundInstance != -1L) {
                  this.emitter.stopSound(this.soundInstance);
               }

               this.emitter = IsoWorld.instance.getFreeEmitter(this.getX(), this.getY(), (float)((int)this.getZ()));
               if (this.hasMetal()) {
                  this.soundInstance = this.emitter.playSoundLoopedImpl("MicrowaveCookingMetal");
               } else {
                  this.soundInstance = this.emitter.playSoundLoopedImpl("MicrowaveRunning");
               }
            } else if (this.soundInstance != -1L) {
               this.emitter.stopSound(this.soundInstance);
               this.soundInstance = -1L;
               if (this.container != null && this.container.isPowered()) {
                  this.emitter.playSoundImpl("MicrowaveTimerExpired", true, this);
               }
            }
         }

      }
   }

   private boolean hasMetal() {
      int var1 = this.getContainer().getItems().size();

      for(int var2 = 0; var2 < var1; ++var2) {
         InventoryItem var3 = (InventoryItem)this.getContainer().getItems().get(var2);
         if (var3.getMetalValue() > 0.0F) {
            this.hasMetal = true;
            return true;
         }
      }

      this.hasMetal = false;
      return false;
   }

   public String getActivatableType() {
      return "stove";
   }

   public void syncIsoObjectSend(ByteBufferWriter var1) {
      var1.putInt(this.square.getX());
      var1.putInt(this.square.getY());
      var1.putInt(this.square.getZ());
      byte var2 = (byte)this.square.getObjects().indexOf(this);
      var1.putByte(var2);
      var1.putByte((byte)1);
      var1.putByte((byte)(this.activated ? 1 : 0));
      var1.putInt(this.secondsTimer);
      var1.putFloat(this.maxTemperature);
   }

   public void syncIsoObject(boolean var1, byte var2, UdpConnection var3, ByteBuffer var4) {
      if (this.square == null) {
         System.out.println("ERROR: " + this.getClass().getSimpleName() + " square is null");
      } else if (this.getObjectIndex() == -1) {
         System.out.println("ERROR: " + this.getClass().getSimpleName() + " not found on square " + this.square.getX() + "," + this.square.getY() + "," + this.square.getZ());
      } else {
         if (GameClient.bClient && !var1) {
            ByteBufferWriter var9 = GameClient.connection.startPacket();
            PacketTypes.doPacket((short)12, var9);
            this.syncIsoObjectSend(var9);
            GameClient.connection.endPacketImmediate();
            this.square.clientModify();
         } else if (var1) {
            boolean var5 = var2 == 1;
            this.secondsTimer = var4.getInt();
            this.maxTemperature = var4.getFloat();
            this.setActivated(var5);
            this.container.addItemsToProcessItems();
            if (GameServer.bServer) {
               Iterator var6 = GameServer.udpEngine.connections.iterator();

               while(true) {
                  UdpConnection var7;
                  do {
                     if (!var6.hasNext()) {
                        this.square.revisionUp();
                        return;
                     }

                     var7 = (UdpConnection)var6.next();
                  } while(var3 != null && var7.getConnectedGUID() == var3.getConnectedGUID());

                  ByteBufferWriter var8 = var7.startPacket();
                  PacketTypes.doPacket((short)12, var8);
                  this.syncIsoObjectSend(var8);
                  var7.endPacketImmediate();
               }
            }
         }

      }
   }

   public void setActivated(boolean var1) {
      if (!this.isBroken()) {
         this.activated = var1;
         if (this.firstTurnOn && this.getMaxTemperature() == 0.0F) {
            if ("microwave".equals(this.getContainer().getType()) && this.secondsTimer < 0) {
               this.maxTemperature = 100.0F;
            }

            if ("stove".equals(this.getContainer().getType()) && this.secondsTimer < 0) {
               this.maxTemperature = 200.0F;
            }
         }

         if (this.firstTurnOn) {
            this.firstTurnOn = false;
         }

         if (this.activated) {
            if ("microwave".equals(this.getContainer().getType()) && this.secondsTimer < 0) {
               this.secondsTimer = 3600;
            }

            if (this.secondsTimer > 0) {
               this.startTime = GameTime.instance.getWorldAgeHours();
               this.stopTime = this.startTime + (double)this.secondsTimer / 3600.0D;
            }
         } else {
            this.stopTime = 0.0D;
            this.startTime = 0.0D;
            this.hasMetal = false;
         }

         this.doSound();
         this.doOverlay();
      }
   }

   private void doOverlay() {
      if (this.Activated() && this.getOverlaySprite() == null) {
         String[] var1 = this.getSprite().getName().split("_");
         String var2 = var1[0] + "_" + var1[1] + "_" + var1[2] + "_" + (Integer.parseInt(var1[3]) + 64);
         this.setOverlaySprite(var2);
      } else if (!this.Activated()) {
         this.setOverlaySprite((String)null);
      }

   }

   public void setTimer(int var1) {
      this.secondsTimer = var1;
      if (this.activated && this.secondsTimer > 0) {
         this.startTime = GameTime.instance.getWorldAgeHours();
         this.stopTime = this.startTime + (double)this.secondsTimer / 3600.0D;
      }

   }

   public int getTimer() {
      return this.secondsTimer;
   }

   public float getMaxTemperature() {
      return this.maxTemperature;
   }

   public void setMaxTemperature(float var1) {
      this.maxTemperature = var1;
   }

   public int isRunningFor() {
      return this.startTime == 0.0D ? 0 : (int)((GameTime.instance.getWorldAgeHours() - this.startTime) * 3600.0D);
   }

   public float getCurrentTemperature() {
      return this.currentTemperature + 100.0F;
   }

   public boolean isTemperatureChanging() {
      return this.currentTemperature != (this.activated ? this.maxTemperature : 0.0F);
   }

   public boolean isBroken() {
      return this.broken;
   }

   public void setBroken(boolean var1) {
      this.broken = var1;
   }
}
