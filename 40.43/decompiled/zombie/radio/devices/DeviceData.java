package zombie.radio.devices;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.WorldSoundManager;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Radio;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypesShort;
import zombie.radio.ZomboidRadio;
import zombie.vehicles.VehiclePart;

public class DeviceData implements Cloneable {
   private static final float deviceSpeakerSoundMod = 0.4F;
   private static final float deviceButtonSoundVol = 0.05F;
   protected String deviceName;
   protected boolean twoWay;
   protected int transmitRange;
   protected int micRange;
   protected boolean micIsMuted;
   protected float baseVolumeRange;
   protected float deviceVolume;
   protected boolean isPortable;
   protected boolean isTelevision;
   protected boolean isHighTier;
   protected boolean isTurnedOn;
   protected int channel;
   protected int minChannelRange;
   protected int maxChannelRange;
   protected DevicePresets presets;
   protected boolean isBatteryPowered;
   protected boolean hasBattery;
   protected float powerDelta;
   protected float useDelta;
   protected int lastRecordedDistance;
   protected int headphoneType;
   protected WaveSignalDevice parent;
   protected GameTime gameTime;
   protected boolean channelChangedRecently;
   protected BaseSoundEmitter emitter;
   protected ArrayList soundIDs;
   private float soundCounterStatic;
   protected long radioLoopSound;
   protected boolean doTriggerWorldSound;
   protected long lastMinuteStamp;
   protected int listenCnt;
   float nextStaticSound;
   protected float signalCounter;
   protected float soundCounter;

   public DeviceData() {
      this((WaveSignalDevice)null);
   }

   public DeviceData(WaveSignalDevice var1) {
      this.deviceName = "WaveSignalDevice";
      this.twoWay = false;
      this.transmitRange = 1000;
      this.micRange = 5;
      this.micIsMuted = false;
      this.baseVolumeRange = 15.0F;
      this.deviceVolume = 1.0F;
      this.isPortable = false;
      this.isTelevision = false;
      this.isHighTier = false;
      this.isTurnedOn = false;
      this.channel = 88000;
      this.minChannelRange = 200;
      this.maxChannelRange = 1000000;
      this.presets = null;
      this.isBatteryPowered = true;
      this.hasBattery = true;
      this.powerDelta = 1.0F;
      this.useDelta = 0.001F;
      this.lastRecordedDistance = -1;
      this.headphoneType = -1;
      this.parent = null;
      this.gameTime = null;
      this.channelChangedRecently = false;
      this.emitter = null;
      this.soundIDs = new ArrayList();
      this.soundCounterStatic = 0.0F;
      this.radioLoopSound = 0L;
      this.doTriggerWorldSound = false;
      this.lastMinuteStamp = -1L;
      this.listenCnt = 0;
      this.nextStaticSound = 0.0F;
      this.signalCounter = 0.0F;
      this.soundCounter = 0.0F;
      this.parent = var1;
      this.presets = new DevicePresets();
      this.gameTime = GameTime.getInstance();
   }

   public void generatePresets() {
      if (this.presets == null) {
         this.presets = new DevicePresets();
      }

      this.presets.clearPresets();
      Map var1;
      if (this.isTelevision) {
         var1 = ZomboidRadio.getInstance().GetChannelList("Television");
         if (var1 != null) {
            Iterator var2 = var1.entrySet().iterator();

            while(var2.hasNext()) {
               Entry var3 = (Entry)var2.next();
               if ((Integer)var3.getKey() >= this.minChannelRange && (Integer)var3.getKey() <= this.maxChannelRange) {
                  this.presets.addPreset((String)var3.getValue(), (Integer)var3.getKey());
               }
            }
         }
      } else {
         int var5 = this.twoWay ? 100 : 800;
         var1 = ZomboidRadio.getInstance().GetChannelList("Radio");
         Entry var4;
         Iterator var6;
         if (var1 != null) {
            var6 = var1.entrySet().iterator();

            while(var6.hasNext()) {
               var4 = (Entry)var6.next();
               if ((Integer)var4.getKey() >= this.minChannelRange && (Integer)var4.getKey() <= this.maxChannelRange && Rand.Next(1000) < var5) {
                  this.presets.addPreset((String)var4.getValue(), (Integer)var4.getKey());
               }
            }
         }

         if (this.twoWay) {
            var1 = ZomboidRadio.getInstance().GetChannelList("Amateur");
            if (var1 != null) {
               var6 = var1.entrySet().iterator();

               while(var6.hasNext()) {
                  var4 = (Entry)var6.next();
                  if ((Integer)var4.getKey() >= this.minChannelRange && (Integer)var4.getKey() <= this.maxChannelRange && Rand.Next(1000) < var5) {
                     this.presets.addPreset((String)var4.getValue(), (Integer)var4.getKey());
                  }
               }
            }
         }

         if (this.isHighTier) {
            var1 = ZomboidRadio.getInstance().GetChannelList("Military");
            if (var1 != null) {
               var6 = var1.entrySet().iterator();

               while(var6.hasNext()) {
                  var4 = (Entry)var6.next();
                  if ((Integer)var4.getKey() >= this.minChannelRange && (Integer)var4.getKey() <= this.maxChannelRange && Rand.Next(1000) < 10) {
                     this.presets.addPreset((String)var4.getValue(), (Integer)var4.getKey());
                  }
               }
            }
         }
      }

   }

   protected Object clone() throws CloneNotSupportedException {
      DeviceData var1 = (DeviceData)super.clone();
      var1.setDevicePresets((DevicePresets)this.presets.clone());
      var1.setParent((WaveSignalDevice)null);
      return var1;
   }

   public DeviceData getClone() {
      DeviceData var1;
      try {
         var1 = (DeviceData)this.clone();
      } catch (Exception var3) {
         System.out.println(var3.getMessage());
         var1 = new DeviceData();
      }

      return var1;
   }

   public WaveSignalDevice getParent() {
      return this.parent;
   }

   public void setParent(WaveSignalDevice var1) {
      this.parent = var1;
   }

   public DevicePresets getDevicePresets() {
      return this.presets;
   }

   public void setDevicePresets(DevicePresets var1) {
      if (var1 == null) {
         var1 = new DevicePresets();
      }

      this.presets = var1;
   }

   public int getMinChannelRange() {
      return this.minChannelRange;
   }

   public void setMinChannelRange(int var1) {
      this.minChannelRange = var1 >= 200 && var1 <= 1000000 ? var1 : 200;
   }

   public int getMaxChannelRange() {
      return this.maxChannelRange;
   }

   public void setMaxChannelRange(int var1) {
      this.maxChannelRange = var1 >= 200 && var1 <= 1000000 ? var1 : 1000000;
   }

   public boolean getIsHighTier() {
      return this.isHighTier;
   }

   public void setIsHighTier(boolean var1) {
      this.isHighTier = var1;
   }

   public boolean getIsBatteryPowered() {
      return this.isBatteryPowered;
   }

   public void setIsBatteryPowered(boolean var1) {
      this.isBatteryPowered = var1;
   }

   public boolean getHasBattery() {
      return this.hasBattery;
   }

   public void setHasBattery(boolean var1) {
      this.hasBattery = var1;
   }

   public void addBattery(DrainableComboItem var1) {
      if (!this.hasBattery && var1 != null && var1.getFullType().equals("Base.Battery")) {
         ItemContainer var2 = var1.getContainer();
         if (var2 != null) {
            if (var2.getType().equals("floor") && var1.getWorldItem() != null && var1.getWorldItem().getSquare() != null) {
               var1.getWorldItem().getSquare().transmitRemoveItemFromSquare(var1.getWorldItem());
               var1.getWorldItem().getSquare().getWorldObjects().remove(var1.getWorldItem());
               var1.getWorldItem().getSquare().chunk.recalcHashCodeObjects();
               var1.getWorldItem().getSquare().getObjects().remove(var1.getWorldItem());
               var1.setWorldItem((IsoWorldInventoryObject)null);
            }

            this.powerDelta = var1.getDelta();
            var2.DoRemoveItem(var1);
            this.hasBattery = true;
            this.transmitDeviceDataState((byte)2);
         }
      }

   }

   public InventoryItem getBattery(ItemContainer var1) {
      if (this.hasBattery) {
         DrainableComboItem var2 = (DrainableComboItem)InventoryItemFactory.CreateItem("Base.Battery");
         var2.setDelta(this.powerDelta);
         this.powerDelta = 0.0F;
         var1.AddItem((InventoryItem)var2);
         this.hasBattery = false;
         this.transmitDeviceDataState((byte)2);
         return var2;
      } else {
         return null;
      }
   }

   public void transmitBattryChange() {
      this.transmitDeviceDataState((byte)2);
   }

   public void addHeadphones(InventoryItem var1) {
      if (this.headphoneType < 0 && (var1.getFullType().equals("Base.Headphones") || var1.getFullType().equals("Base.Earbuds"))) {
         ItemContainer var2 = var1.getContainer();
         if (var2 != null) {
            if (var2.getType().equals("floor") && var1.getWorldItem() != null && var1.getWorldItem().getSquare() != null) {
               var1.getWorldItem().getSquare().transmitRemoveItemFromSquare(var1.getWorldItem());
               var1.getWorldItem().getSquare().getWorldObjects().remove(var1.getWorldItem());
               var1.getWorldItem().getSquare().chunk.recalcHashCodeObjects();
               var1.getWorldItem().getSquare().getObjects().remove(var1.getWorldItem());
               var1.setWorldItem((IsoWorldInventoryObject)null);
            }

            int var3 = var1.getFullType().equals("Base.Headphones") ? 0 : 1;
            var2.DoRemoveItem(var1);
            this.setHeadphoneType(var3);
            this.transmitDeviceDataState((byte)6);
         }
      }

   }

   public InventoryItem getHeadphones(ItemContainer var1) {
      if (this.headphoneType >= 0) {
         InventoryItem var2 = null;
         if (this.headphoneType == 0) {
            var2 = InventoryItemFactory.CreateItem("Base.Headphones");
         } else if (this.headphoneType == 1) {
            var2 = InventoryItemFactory.CreateItem("Base.Earbuds");
         }

         if (var2 != null) {
            var1.AddItem(var2);
         }

         this.setHeadphoneType(-1);
         this.transmitDeviceDataState((byte)6);
      }

      return null;
   }

   public int getMicRange() {
      return this.micRange;
   }

   public void setMicRange(int var1) {
      this.micRange = var1;
   }

   public boolean getMicIsMuted() {
      return this.micIsMuted;
   }

   public void setMicIsMuted(boolean var1) {
      this.micIsMuted = var1;
   }

   public int getHeadphoneType() {
      return this.headphoneType;
   }

   public void setHeadphoneType(int var1) {
      this.headphoneType = var1;
   }

   public float getBaseVolumeRange() {
      return this.baseVolumeRange;
   }

   public void setBaseVolumeRange(float var1) {
      this.baseVolumeRange = var1;
   }

   public float getDeviceVolume() {
      return this.deviceVolume;
   }

   public void setDeviceVolume(float var1) {
      this.deviceVolume = var1 < 0.0F ? 0.0F : (var1 > 1.0F ? 1.0F : var1);
      this.transmitDeviceDataState((byte)4);
   }

   public void setDeviceVolumeRaw(float var1) {
      this.deviceVolume = var1 < 0.0F ? 0.0F : (var1 > 1.0F ? 1.0F : var1);
   }

   public boolean getIsTelevision() {
      return this.isTelevision;
   }

   public void setIsTelevision(boolean var1) {
      this.isTelevision = var1;
   }

   public String getDeviceName() {
      return this.deviceName;
   }

   public void setDeviceName(String var1) {
      this.deviceName = var1;
   }

   public boolean getIsTwoWay() {
      return this.twoWay;
   }

   public void setIsTwoWay(boolean var1) {
      this.twoWay = var1;
   }

   public int getTransmitRange() {
      return this.transmitRange;
   }

   public void setTransmitRange(int var1) {
      this.transmitRange = var1 > 0 ? var1 : 0;
   }

   public boolean getIsPortable() {
      return this.isPortable;
   }

   public void setIsPortable(boolean var1) {
      this.isPortable = var1;
   }

   public boolean getIsTurnedOn() {
      return this.isTurnedOn;
   }

   public void setIsTurnedOn(boolean var1) {
      if (this.canBePoweredHere()) {
         if (this.isBatteryPowered && !(this.powerDelta > 0.0F)) {
            this.isTurnedOn = false;
         } else {
            this.isTurnedOn = var1;
         }

         this.playSoundSend("RadioButton", false);
         this.transmitDeviceDataState((byte)0);
      } else if (this.isTurnedOn) {
         this.isTurnedOn = false;
         this.playSoundSend("RadioButton", false);
         this.transmitDeviceDataState((byte)0);
      }

   }

   public void setTurnedOnRaw(boolean var1) {
      this.isTurnedOn = var1;
   }

   public boolean canBePoweredHere() {
      if (this.isBatteryPowered) {
         return true;
      } else if (this.parent instanceof VehiclePart) {
         VehiclePart var2 = (VehiclePart)this.parent;
         return var2.getItemType() != null && !var2.getItemType().isEmpty() && var2.getInventoryItem() == null ? false : var2.hasDevicePower();
      } else {
         boolean var1 = false;
         if (GameTime.getInstance().getNightsSurvived() < SandboxOptions.instance.getElecShutModifier()) {
            var1 = true;
         }

         if (this.parent != null && this.parent.getSquare() != null) {
            if (this.parent.getSquare().haveElectricity()) {
               var1 = true;
            } else if (this.parent.getSquare().getRoom() == null) {
               var1 = false;
            }
         } else {
            var1 = false;
         }

         return var1;
      }
   }

   public void setRandomChannel() {
      if (this.presets != null && this.presets.getPresets().size() > 0) {
         int var1 = Rand.Next(0, this.presets.getPresets().size());
         this.channel = ((PresetEntry)this.presets.getPresets().get(var1)).getFrequency();
      } else {
         this.channel = Rand.Next(this.minChannelRange, this.maxChannelRange);
         this.channel -= this.channel % 200;
      }

   }

   public int getChannel() {
      return this.channel;
   }

   public void setChannel(int var1) {
      this.setChannel(var1, true);
   }

   public void setChannel(int var1, boolean var2) {
      if (var1 >= this.minChannelRange && var1 <= this.maxChannelRange) {
         this.channel = var1;
         this.playSoundSend("RadioButton", false);
         if (this.isTelevision) {
            this.playSoundSend("TelevisionZap", true);
         } else {
            this.playSoundSend("RadioZap", true);
         }

         if (this.radioLoopSound > 0L) {
            this.emitter.stopSound(this.radioLoopSound);
            this.radioLoopSound = 0L;
         }

         this.transmitDeviceDataState((byte)1);
         if (var2) {
            this.TriggerPlayerListening(true);
         }
      }

   }

   public void setChannelRaw(int var1) {
      this.channel = var1;
   }

   public float getUseDelta() {
      return this.useDelta;
   }

   public void setUseDelta(float var1) {
      this.useDelta = var1 / 60.0F;
   }

   public float getPower() {
      return this.powerDelta;
   }

   public void setPower(float var1) {
      if (var1 > 1.0F) {
         var1 = 1.0F;
      }

      if (var1 < 0.0F) {
         var1 = 0.0F;
      }

      this.powerDelta = var1;
   }

   public void TriggerPlayerListening(boolean var1) {
      if (this.isTurnedOn) {
         ZomboidRadio.getInstance().PlayerListensChannel(this.channel, true, this.isTelevision);
      }

   }

   public void playSoundSend(String var1, boolean var2) {
      this.playSound(var1, var2 ? this.deviceVolume * 0.4F : 0.05F, true);
   }

   public void playSoundLocal(String var1, boolean var2) {
      this.playSound(var1, var2 ? this.deviceVolume * 0.4F : 0.05F, false);
   }

   public void playSound(String var1, float var2, boolean var3) {
      if (!GameServer.bServer) {
         this.setEmitterAndPos();
         if (this.emitter != null) {
            long var4 = var3 ? this.emitter.playSound(var1) : this.emitter.playSoundImpl(var1, false, (IsoObject)null);
            this.emitter.setVolume(var4, var2);
         }

      }
   }

   public void cleanSoundsAndEmitter() {
      if (this.emitter != null) {
         this.emitter.stopAll();
         IsoWorld.instance.freeEmitters.add(this.emitter);
         this.emitter = null;
         this.radioLoopSound = 0L;
      }

   }

   protected void setEmitterAndPos() {
      Object var1 = null;
      if (this.parent != null && this.parent instanceof IsoObject) {
         var1 = (IsoObject)this.parent;
      } else if (this.parent != null && this.parent instanceof Radio) {
         var1 = IsoPlayer.getInstance();
      }

      if (var1 != null) {
         if (this.emitter == null) {
            this.emitter = IsoWorld.instance.getFreeEmitter(((IsoObject)var1).getX() + 0.5F, ((IsoObject)var1).getY() + 0.5F, (float)((int)((IsoObject)var1).getZ()));
            IsoWorld.instance.currentEmitters.remove(this.emitter);
         } else {
            this.emitter.setPos(((IsoObject)var1).getX() + 0.5F, ((IsoObject)var1).getY() + 0.5F, (float)((int)((IsoObject)var1).getZ()));
         }

         if (this.radioLoopSound != 0L) {
            this.emitter.setVolume(this.radioLoopSound, this.deviceVolume * 0.4F);
         }
      }

   }

   protected void updateEmitter() {
      if (!GameServer.bServer) {
         if (!this.isTurnedOn) {
            if (this.emitter != null && this.emitter.isPlaying("RadioButton")) {
               if (this.radioLoopSound > 0L) {
                  this.emitter.stopSound(this.radioLoopSound);
               }

               this.setEmitterAndPos();
               this.emitter.tick();
            } else {
               this.cleanSoundsAndEmitter();
            }
         } else {
            this.setEmitterAndPos();
            if (this.emitter != null) {
               if (this.signalCounter > 0.0F && !this.emitter.isPlaying("RadioTalk")) {
                  if (this.radioLoopSound > 0L) {
                     this.emitter.stopSound(this.radioLoopSound);
                  }

                  this.radioLoopSound = this.emitter.playSoundImpl("RadioTalk", false, (IsoObject)null);
                  this.emitter.setVolume(this.radioLoopSound, this.deviceVolume * 0.4F);
               }

               String var1 = !this.isTelevision ? "RadioStatic" : "TelevisionTestBeep";
               if (this.radioLoopSound == 0L || this.signalCounter <= 0.0F && !this.emitter.isPlaying(var1)) {
                  if (this.radioLoopSound > 0L) {
                     this.emitter.stopSound(this.radioLoopSound);
                     if (this.isTelevision) {
                        this.playSoundLocal("TelevisionZap", true);
                     } else {
                        this.playSoundLocal("RadioZap", true);
                     }
                  }

                  this.radioLoopSound = this.emitter.playSoundImpl(var1, false, (IsoObject)null);
                  this.emitter.setVolume(this.radioLoopSound, this.deviceVolume * 0.4F);
               }

               this.emitter.tick();
            }

         }
      }
   }

   public void update(boolean var1, boolean var2) {
      if (this.lastMinuteStamp == -1L) {
         this.lastMinuteStamp = this.gameTime.getMinutesStamp();
      }

      if (this.gameTime.getMinutesStamp() > this.lastMinuteStamp) {
         long var3 = this.gameTime.getMinutesStamp() - this.lastMinuteStamp;
         this.lastMinuteStamp = this.gameTime.getMinutesStamp();
         this.listenCnt = (int)((long)this.listenCnt + var3);
         if (this.listenCnt >= 10) {
            this.listenCnt = 0;
         }

         if (!GameServer.bServer && this.isTurnedOn && var2 && (this.listenCnt == 0 || this.listenCnt == 5)) {
            this.TriggerPlayerListening(true);
         }

         if (this.isTurnedOn && this.isBatteryPowered && this.powerDelta > 0.0F) {
            float var5 = this.powerDelta - this.powerDelta % 0.01F;
            this.setPower(this.powerDelta - this.useDelta * (float)var3);
            if (this.listenCnt == 0 || this.powerDelta == 0.0F || this.powerDelta < var5) {
               if (var1 && GameServer.bServer) {
                  this.transmitDeviceDataStateServer((byte)3, (UdpConnection)null);
               } else if (!var1 && GameClient.bClient) {
                  this.transmitDeviceDataState((byte)3);
               }
            }
         }
      }

      if (this.isTurnedOn && (this.isBatteryPowered && this.powerDelta <= 0.0F || !this.canBePoweredHere())) {
         this.isTurnedOn = false;
         if (var1 && GameServer.bServer) {
            this.transmitDeviceDataStateServer((byte)0, (UdpConnection)null);
         } else if (!var1 && GameClient.bClient) {
            this.transmitDeviceDataState((byte)0);
         }
      }

      this.updateEmitter();
      this.updateSimple();
   }

   public void updateSimple() {
      if (this.signalCounter >= 0.0F) {
         this.signalCounter -= 1.25F * GameTime.getInstance().getMultiplier();
      }

      if (this.soundCounter >= 0.0F) {
         this.soundCounter = (float)((double)this.soundCounter - 1.25D * (double)GameTime.getInstance().getMultiplier());
      }

      if (this.signalCounter <= 0.0F && this.lastRecordedDistance >= 0) {
         this.lastRecordedDistance = -1;
      }

      this.updateStaticSounds();
      if (GameClient.bClient) {
         this.updateEmitter();
      }

      if (this.doTriggerWorldSound && this.soundCounter <= 0.0F) {
         if (this.isTurnedOn && this.deviceVolume > 0.0F && (!this.isInventoryDevice() || this.headphoneType < 0) && (!GameClient.bClient && !GameServer.bServer || GameClient.bClient && this.isInventoryDevice() || GameServer.bServer && !this.isInventoryDevice())) {
            Object var1 = null;
            if (this.parent != null && this.parent instanceof IsoObject) {
               var1 = (IsoObject)this.parent;
            } else if (this.parent != null && this.parent instanceof Radio) {
               var1 = IsoPlayer.getInstance();
            } else if (this.parent instanceof VehiclePart) {
               var1 = ((VehiclePart)this.parent).getVehicle();
            }

            if (var1 != null) {
               int var2 = (int)(100.0F * this.deviceVolume);
               int var3 = this.getDeviceSoundVolumeRange();
               WorldSoundManager.instance.addSound((IsoObject)var1, (int)((IsoObject)var1).getX(), (int)((IsoObject)var1).getY(), (int)((IsoObject)var1).getZ(), var3, var2, var2 > 50);
            }
         }

         this.doTriggerWorldSound = false;
         this.soundCounter = (float)(300 + Rand.Next(0, 300));
      }

   }

   private void updateStaticSounds() {
      if (this.isTurnedOn) {
         float var1 = GameTime.getInstance().getMultiplier();
         this.nextStaticSound -= var1;
         if (this.nextStaticSound <= 0.0F) {
            if (this.parent != null) {
               this.parent.AddDeviceText(ZomboidRadio.getInstance().getRandomBzztFzzt(), 1.0F, 1.0F, 1.0F, (String)null, -1);
               this.doTriggerWorldSound = true;
            }

            this.setNextStaticSound();
         }

      }
   }

   private void setNextStaticSound() {
      this.nextStaticSound = Rand.Next(250.0F, 1500.0F);
   }

   public int getDeviceVolumeRange() {
      return 5 + (int)(this.baseVolumeRange * this.deviceVolume);
   }

   public int getDeviceSoundVolumeRange() {
      if (this.isInventoryDevice()) {
         Radio var2 = (Radio)this.getParent();
         return var2.getPlayer() != null && var2.getPlayer().getSquare() != null && var2.getPlayer().getSquare().getRoom() != null ? 3 + (int)(this.baseVolumeRange * 0.4F * this.deviceVolume) : 5 + (int)(this.baseVolumeRange * this.deviceVolume);
      } else if (this.isIsoDevice()) {
         IsoWaveSignal var1 = (IsoWaveSignal)this.getParent();
         return var1.getSquare() != null && var1.getSquare().getRoom() != null ? 3 + (int)(this.baseVolumeRange * 0.5F * this.deviceVolume) : 5 + (int)(this.baseVolumeRange * 0.75F * this.deviceVolume);
      } else {
         return 5 + (int)(this.baseVolumeRange / 2.0F * this.deviceVolume);
      }
   }

   public void doReceiveSignal(int var1) {
      if (this.isTurnedOn) {
         this.lastRecordedDistance = var1;
         this.signalCounter = 300.0F;
         this.doTriggerWorldSound = true;
         this.setNextStaticSound();
      }

   }

   public boolean isReceivingSignal() {
      return this.signalCounter > 0.0F;
   }

   public int getLastRecordedDistance() {
      return this.lastRecordedDistance;
   }

   public boolean isIsoDevice() {
      return this.getParent() != null && this.getParent() instanceof IsoWaveSignal;
   }

   public boolean isInventoryDevice() {
      return this.getParent() != null && this.getParent() instanceof Radio;
   }

   public boolean isVehicleDevice() {
      return this.getParent() instanceof VehiclePart;
   }

   public void transmitPresets() {
      this.transmitDeviceDataState((byte)5);
   }

   private void transmitDeviceDataState(byte var1) {
      if (GameClient.bClient) {
         try {
            this.sendDeviceDataStatePacket(GameClient.connection, var1);
         } catch (Exception var3) {
            System.out.print(var3.getMessage());
         }
      }

   }

   private void transmitDeviceDataStateServer(byte var1, UdpConnection var2) {
      if (GameServer.bServer) {
         try {
            for(int var3 = 0; var3 < GameServer.udpEngine.connections.size(); ++var3) {
               UdpConnection var4 = (UdpConnection)GameServer.udpEngine.connections.get(var3);
               if (var2 == null || var2 != var4) {
                  this.sendDeviceDataStatePacket(var4, var1);
               }
            }
         } catch (Exception var5) {
            System.out.print(var5.getMessage());
         }
      }

   }

   private void sendDeviceDataStatePacket(UdpConnection var1, byte var2) throws IOException {
      ByteBufferWriter var3 = var1.startPacket();
      PacketTypesShort.doPacket((short)1004, var3);
      boolean var4 = false;
      if (this.isIsoDevice()) {
         IsoWaveSignal var5 = (IsoWaveSignal)this.getParent();
         IsoGridSquare var6 = var5.getSquare();
         if (var6 != null) {
            var3.putByte((byte)1);
            var3.putInt(var6.getX());
            var3.putInt(var6.getY());
            var3.putInt(var6.getZ());
            var3.putInt(var6.getObjects().indexOf(var5));
            var4 = true;
         }
      } else if (this.isInventoryDevice()) {
         Radio var7 = (Radio)this.getParent();
         IsoPlayer var10 = null;
         if (var7.getEquipParent() != null && var7.getEquipParent() instanceof IsoPlayer) {
            var10 = (IsoPlayer)var7.getEquipParent();
         }

         if (var10 != null) {
            var3.putByte((byte)0);
            if (GameServer.bServer) {
               var3.putInt(var10 != null ? var10.OnlineID : -1);
            } else {
               var3.putByte((byte)var10.PlayerIndex);
            }

            if (var10.getPrimaryHandItem() == var7) {
               var3.putByte((byte)1);
            } else if (var10.getSecondaryHandItem() == var7) {
               var3.putByte((byte)2);
            } else {
               var3.putByte((byte)0);
            }

            var4 = true;
         }
      } else if (this.isVehicleDevice()) {
         VehiclePart var8 = (VehiclePart)this.getParent();
         var3.putByte((byte)2);
         var3.putShort(var8.getVehicle().VehicleID);
         var3.putShort((short)var8.getIndex());
         var4 = true;
      }

      if (var4) {
         var3.putByte(var2);
         label73:
         switch(var2) {
         case 0:
            var3.putByte((byte)(this.isTurnedOn ? 1 : 0));
            break;
         case 1:
            var3.putInt(this.channel);
            break;
         case 2:
            var3.putByte((byte)(this.hasBattery ? 1 : 0));
            var3.putFloat(this.powerDelta);
            break;
         case 3:
            var3.putFloat(this.powerDelta);
            break;
         case 4:
            var3.putFloat(this.deviceVolume);
            break;
         case 5:
            var3.putInt(this.presets.getPresets().size());
            Iterator var9 = this.presets.getPresets().iterator();

            while(true) {
               if (!var9.hasNext()) {
                  break label73;
               }

               PresetEntry var11 = (PresetEntry)var9.next();
               GameWindow.WriteString(var3.bb, var11.getName());
               var3.putInt(var11.getFrequency());
            }
         case 6:
            var3.putInt(this.headphoneType);
         }

         var1.endPacketImmediate();
      } else {
         var1.cancelPacket();
      }

   }

   public void receiveDeviceDataStatePacket(ByteBuffer var1, UdpConnection var2) throws IOException {
      if (GameClient.bClient || GameServer.bServer) {
         boolean var3 = GameServer.bServer;
         boolean var4 = this.isIsoDevice() || this.isVehicleDevice();
         byte var5 = var1.get();
         switch(var5) {
         case 0:
            if (var3 && var4) {
               this.setIsTurnedOn(var1.get() == 1);
            } else {
               this.isTurnedOn = var1.get() == 1;
            }

            if (var3) {
               this.transmitDeviceDataStateServer(var5, !var4 ? var2 : null);
            }
            break;
         case 1:
            int var6 = var1.getInt();
            if (var3 && var4) {
               this.setChannel(var6);
            } else {
               this.channel = var6;
            }

            if (var3) {
               this.transmitDeviceDataStateServer(var5, !var4 ? var2 : null);
            }
            break;
         case 2:
            boolean var7 = var1.get() == 1;
            float var8 = var1.getFloat();
            if (var3 && var4) {
               this.hasBattery = var7;
               this.setPower(var8);
            } else {
               this.hasBattery = var7;
               this.powerDelta = var8;
            }

            if (var3) {
               this.transmitDeviceDataStateServer(var5, !var4 ? var2 : null);
            }
            break;
         case 3:
            float var9 = var1.getFloat();
            if (var3 && var4) {
               this.setPower(var9);
            } else {
               this.powerDelta = var9;
            }

            if (var3) {
               this.transmitDeviceDataStateServer(var5, !var4 ? var2 : null);
            }
            break;
         case 4:
            float var10 = var1.getFloat();
            if (var3 && var4) {
               this.setDeviceVolume(var10);
            } else {
               this.deviceVolume = var10;
            }

            if (var3) {
               this.transmitDeviceDataStateServer(var5, !var4 ? var2 : null);
            }
            break;
         case 5:
            int var11 = var1.getInt();

            for(int var12 = 0; var12 < var11; ++var12) {
               String var13 = GameWindow.ReadString(var1);
               int var14 = var1.getInt();
               if (var12 < this.presets.getPresets().size()) {
                  PresetEntry var15 = (PresetEntry)this.presets.getPresets().get(var12);
                  if (!var15.getName().equals(var13) || var15.getFrequency() != var14) {
                     var15.setName(var13);
                     var15.setFrequency(var14);
                  }
               } else {
                  this.presets.addPreset(var13, var14);
               }
            }

            if (var3) {
               this.transmitDeviceDataStateServer((byte)5, !var4 ? var2 : null);
            }
            break;
         case 6:
            this.headphoneType = var1.getInt();
            if (var3) {
               this.transmitDeviceDataStateServer(var5, !var4 ? var2 : null);
            }
         }

      }
   }

   public void save(ByteBuffer var1, boolean var2) throws IOException {
      GameWindow.WriteString(var1, this.deviceName);
      var1.put((byte)(this.twoWay ? 1 : 0));
      var1.putInt(this.transmitRange);
      var1.putInt(this.micRange);
      var1.put((byte)(this.micIsMuted ? 1 : 0));
      var1.putFloat(this.baseVolumeRange);
      var1.putFloat(this.deviceVolume);
      var1.put((byte)(this.isPortable ? 1 : 0));
      var1.put((byte)(this.isTelevision ? 1 : 0));
      var1.put((byte)(this.isHighTier ? 1 : 0));
      var1.put((byte)(this.isTurnedOn ? 1 : 0));
      var1.putInt(this.channel);
      var1.putInt(this.minChannelRange);
      var1.putInt(this.maxChannelRange);
      var1.put((byte)(this.isBatteryPowered ? 1 : 0));
      var1.put((byte)(this.hasBattery ? 1 : 0));
      var1.putFloat(this.powerDelta);
      var1.putFloat(this.useDelta);
      var1.putInt(this.headphoneType);
      if (this.presets != null) {
         var1.put((byte)1);
         this.presets.save(var1, var2);
      } else {
         var1.put((byte)0);
      }

   }

   public void load(ByteBuffer var1, int var2, boolean var3) throws IOException {
      if (this.presets == null) {
         this.presets = new DevicePresets();
      }

      if (var2 >= 69) {
         this.deviceName = GameWindow.ReadString(var1);
         this.twoWay = var1.get() == 1;
         this.transmitRange = var1.getInt();
         this.micRange = var1.getInt();
         this.micIsMuted = var1.get() == 1;
         this.baseVolumeRange = var1.getFloat();
         this.deviceVolume = var1.getFloat();
         this.isPortable = var1.get() == 1;
         this.isTelevision = var1.get() == 1;
         this.isHighTier = var1.get() == 1;
         this.isTurnedOn = var1.get() == 1;
         this.channel = var1.getInt();
         this.minChannelRange = var1.getInt();
         this.maxChannelRange = var1.getInt();
         this.isBatteryPowered = var1.get() == 1;
         this.hasBattery = var1.get() == 1;
         this.powerDelta = var1.getFloat();
         this.useDelta = var1.getFloat();
         this.headphoneType = var1.getInt();
         if (var1.get() == 1) {
            this.presets.load(var1, var2, var3);
         }
      }

   }
}
