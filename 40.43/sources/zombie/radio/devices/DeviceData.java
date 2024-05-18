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

	public DeviceData(WaveSignalDevice waveSignalDevice) {
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
		this.parent = waveSignalDevice;
		this.presets = new DevicePresets();
		this.gameTime = GameTime.getInstance();
	}

	public void generatePresets() {
		if (this.presets == null) {
			this.presets = new DevicePresets();
		}

		this.presets.clearPresets();
		Map map;
		if (this.isTelevision) {
			map = ZomboidRadio.getInstance().GetChannelList("Television");
			if (map != null) {
				Iterator iterator = map.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry)iterator.next();
					if ((Integer)entry.getKey() >= this.minChannelRange && (Integer)entry.getKey() <= this.maxChannelRange) {
						this.presets.addPreset((String)entry.getValue(), (Integer)entry.getKey());
					}
				}
			}
		} else {
			int int1 = this.twoWay ? 100 : 800;
			map = ZomboidRadio.getInstance().GetChannelList("Radio");
			Entry entry2;
			Iterator iterator2;
			if (map != null) {
				iterator2 = map.entrySet().iterator();
				while (iterator2.hasNext()) {
					entry2 = (Entry)iterator2.next();
					if ((Integer)entry2.getKey() >= this.minChannelRange && (Integer)entry2.getKey() <= this.maxChannelRange && Rand.Next(1000) < int1) {
						this.presets.addPreset((String)entry2.getValue(), (Integer)entry2.getKey());
					}
				}
			}

			if (this.twoWay) {
				map = ZomboidRadio.getInstance().GetChannelList("Amateur");
				if (map != null) {
					iterator2 = map.entrySet().iterator();
					while (iterator2.hasNext()) {
						entry2 = (Entry)iterator2.next();
						if ((Integer)entry2.getKey() >= this.minChannelRange && (Integer)entry2.getKey() <= this.maxChannelRange && Rand.Next(1000) < int1) {
							this.presets.addPreset((String)entry2.getValue(), (Integer)entry2.getKey());
						}
					}
				}
			}

			if (this.isHighTier) {
				map = ZomboidRadio.getInstance().GetChannelList("Military");
				if (map != null) {
					iterator2 = map.entrySet().iterator();
					while (iterator2.hasNext()) {
						entry2 = (Entry)iterator2.next();
						if ((Integer)entry2.getKey() >= this.minChannelRange && (Integer)entry2.getKey() <= this.maxChannelRange && Rand.Next(1000) < 10) {
							this.presets.addPreset((String)entry2.getValue(), (Integer)entry2.getKey());
						}
					}
				}
			}
		}
	}

	protected Object clone() throws CloneNotSupportedException {
		DeviceData deviceData = (DeviceData)super.clone();
		deviceData.setDevicePresets((DevicePresets)this.presets.clone());
		deviceData.setParent((WaveSignalDevice)null);
		return deviceData;
	}

	public DeviceData getClone() {
		DeviceData deviceData;
		try {
			deviceData = (DeviceData)this.clone();
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
			deviceData = new DeviceData();
		}

		return deviceData;
	}

	public WaveSignalDevice getParent() {
		return this.parent;
	}

	public void setParent(WaveSignalDevice waveSignalDevice) {
		this.parent = waveSignalDevice;
	}

	public DevicePresets getDevicePresets() {
		return this.presets;
	}

	public void setDevicePresets(DevicePresets devicePresets) {
		if (devicePresets == null) {
			devicePresets = new DevicePresets();
		}

		this.presets = devicePresets;
	}

	public int getMinChannelRange() {
		return this.minChannelRange;
	}

	public void setMinChannelRange(int int1) {
		this.minChannelRange = int1 >= 200 && int1 <= 1000000 ? int1 : 200;
	}

	public int getMaxChannelRange() {
		return this.maxChannelRange;
	}

	public void setMaxChannelRange(int int1) {
		this.maxChannelRange = int1 >= 200 && int1 <= 1000000 ? int1 : 1000000;
	}

	public boolean getIsHighTier() {
		return this.isHighTier;
	}

	public void setIsHighTier(boolean boolean1) {
		this.isHighTier = boolean1;
	}

	public boolean getIsBatteryPowered() {
		return this.isBatteryPowered;
	}

	public void setIsBatteryPowered(boolean boolean1) {
		this.isBatteryPowered = boolean1;
	}

	public boolean getHasBattery() {
		return this.hasBattery;
	}

	public void setHasBattery(boolean boolean1) {
		this.hasBattery = boolean1;
	}

	public void addBattery(DrainableComboItem drainableComboItem) {
		if (!this.hasBattery && drainableComboItem != null && drainableComboItem.getFullType().equals("Base.Battery")) {
			ItemContainer itemContainer = drainableComboItem.getContainer();
			if (itemContainer != null) {
				if (itemContainer.getType().equals("floor") && drainableComboItem.getWorldItem() != null && drainableComboItem.getWorldItem().getSquare() != null) {
					drainableComboItem.getWorldItem().getSquare().transmitRemoveItemFromSquare(drainableComboItem.getWorldItem());
					drainableComboItem.getWorldItem().getSquare().getWorldObjects().remove(drainableComboItem.getWorldItem());
					drainableComboItem.getWorldItem().getSquare().chunk.recalcHashCodeObjects();
					drainableComboItem.getWorldItem().getSquare().getObjects().remove(drainableComboItem.getWorldItem());
					drainableComboItem.setWorldItem((IsoWorldInventoryObject)null);
				}

				this.powerDelta = drainableComboItem.getDelta();
				itemContainer.DoRemoveItem(drainableComboItem);
				this.hasBattery = true;
				this.transmitDeviceDataState((byte)2);
			}
		}
	}

	public InventoryItem getBattery(ItemContainer itemContainer) {
		if (this.hasBattery) {
			DrainableComboItem drainableComboItem = (DrainableComboItem)InventoryItemFactory.CreateItem("Base.Battery");
			drainableComboItem.setDelta(this.powerDelta);
			this.powerDelta = 0.0F;
			itemContainer.AddItem((InventoryItem)drainableComboItem);
			this.hasBattery = false;
			this.transmitDeviceDataState((byte)2);
			return drainableComboItem;
		} else {
			return null;
		}
	}

	public void transmitBattryChange() {
		this.transmitDeviceDataState((byte)2);
	}

	public void addHeadphones(InventoryItem inventoryItem) {
		if (this.headphoneType < 0 && (inventoryItem.getFullType().equals("Base.Headphones") || inventoryItem.getFullType().equals("Base.Earbuds"))) {
			ItemContainer itemContainer = inventoryItem.getContainer();
			if (itemContainer != null) {
				if (itemContainer.getType().equals("floor") && inventoryItem.getWorldItem() != null && inventoryItem.getWorldItem().getSquare() != null) {
					inventoryItem.getWorldItem().getSquare().transmitRemoveItemFromSquare(inventoryItem.getWorldItem());
					inventoryItem.getWorldItem().getSquare().getWorldObjects().remove(inventoryItem.getWorldItem());
					inventoryItem.getWorldItem().getSquare().chunk.recalcHashCodeObjects();
					inventoryItem.getWorldItem().getSquare().getObjects().remove(inventoryItem.getWorldItem());
					inventoryItem.setWorldItem((IsoWorldInventoryObject)null);
				}

				int int1 = inventoryItem.getFullType().equals("Base.Headphones") ? 0 : 1;
				itemContainer.DoRemoveItem(inventoryItem);
				this.setHeadphoneType(int1);
				this.transmitDeviceDataState((byte)6);
			}
		}
	}

	public InventoryItem getHeadphones(ItemContainer itemContainer) {
		if (this.headphoneType >= 0) {
			InventoryItem inventoryItem = null;
			if (this.headphoneType == 0) {
				inventoryItem = InventoryItemFactory.CreateItem("Base.Headphones");
			} else if (this.headphoneType == 1) {
				inventoryItem = InventoryItemFactory.CreateItem("Base.Earbuds");
			}

			if (inventoryItem != null) {
				itemContainer.AddItem(inventoryItem);
			}

			this.setHeadphoneType(-1);
			this.transmitDeviceDataState((byte)6);
		}

		return null;
	}

	public int getMicRange() {
		return this.micRange;
	}

	public void setMicRange(int int1) {
		this.micRange = int1;
	}

	public boolean getMicIsMuted() {
		return this.micIsMuted;
	}

	public void setMicIsMuted(boolean boolean1) {
		this.micIsMuted = boolean1;
	}

	public int getHeadphoneType() {
		return this.headphoneType;
	}

	public void setHeadphoneType(int int1) {
		this.headphoneType = int1;
	}

	public float getBaseVolumeRange() {
		return this.baseVolumeRange;
	}

	public void setBaseVolumeRange(float float1) {
		this.baseVolumeRange = float1;
	}

	public float getDeviceVolume() {
		return this.deviceVolume;
	}

	public void setDeviceVolume(float float1) {
		this.deviceVolume = float1 < 0.0F ? 0.0F : (float1 > 1.0F ? 1.0F : float1);
		this.transmitDeviceDataState((byte)4);
	}

	public void setDeviceVolumeRaw(float float1) {
		this.deviceVolume = float1 < 0.0F ? 0.0F : (float1 > 1.0F ? 1.0F : float1);
	}

	public boolean getIsTelevision() {
		return this.isTelevision;
	}

	public void setIsTelevision(boolean boolean1) {
		this.isTelevision = boolean1;
	}

	public String getDeviceName() {
		return this.deviceName;
	}

	public void setDeviceName(String string) {
		this.deviceName = string;
	}

	public boolean getIsTwoWay() {
		return this.twoWay;
	}

	public void setIsTwoWay(boolean boolean1) {
		this.twoWay = boolean1;
	}

	public int getTransmitRange() {
		return this.transmitRange;
	}

	public void setTransmitRange(int int1) {
		this.transmitRange = int1 > 0 ? int1 : 0;
	}

	public boolean getIsPortable() {
		return this.isPortable;
	}

	public void setIsPortable(boolean boolean1) {
		this.isPortable = boolean1;
	}

	public boolean getIsTurnedOn() {
		return this.isTurnedOn;
	}

	public void setIsTurnedOn(boolean boolean1) {
		if (this.canBePoweredHere()) {
			if (this.isBatteryPowered && !(this.powerDelta > 0.0F)) {
				this.isTurnedOn = false;
			} else {
				this.isTurnedOn = boolean1;
			}

			this.playSoundSend("RadioButton", false);
			this.transmitDeviceDataState((byte)0);
		} else if (this.isTurnedOn) {
			this.isTurnedOn = false;
			this.playSoundSend("RadioButton", false);
			this.transmitDeviceDataState((byte)0);
		}
	}

	public void setTurnedOnRaw(boolean boolean1) {
		this.isTurnedOn = boolean1;
	}

	public boolean canBePoweredHere() {
		if (this.isBatteryPowered) {
			return true;
		} else if (this.parent instanceof VehiclePart) {
			VehiclePart vehiclePart = (VehiclePart)this.parent;
			return vehiclePart.getItemType() != null && !vehiclePart.getItemType().isEmpty() && vehiclePart.getInventoryItem() == null ? false : vehiclePart.hasDevicePower();
		} else {
			boolean boolean1 = false;
			if (GameTime.getInstance().getNightsSurvived() < SandboxOptions.instance.getElecShutModifier()) {
				boolean1 = true;
			}

			if (this.parent != null && this.parent.getSquare() != null) {
				if (this.parent.getSquare().haveElectricity()) {
					boolean1 = true;
				} else if (this.parent.getSquare().getRoom() == null) {
					boolean1 = false;
				}
			} else {
				boolean1 = false;
			}

			return boolean1;
		}
	}

	public void setRandomChannel() {
		if (this.presets != null && this.presets.getPresets().size() > 0) {
			int int1 = Rand.Next(0, this.presets.getPresets().size());
			this.channel = ((PresetEntry)this.presets.getPresets().get(int1)).getFrequency();
		} else {
			this.channel = Rand.Next(this.minChannelRange, this.maxChannelRange);
			this.channel -= this.channel % 200;
		}
	}

	public int getChannel() {
		return this.channel;
	}

	public void setChannel(int int1) {
		this.setChannel(int1, true);
	}

	public void setChannel(int int1, boolean boolean1) {
		if (int1 >= this.minChannelRange && int1 <= this.maxChannelRange) {
			this.channel = int1;
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
			if (boolean1) {
				this.TriggerPlayerListening(true);
			}
		}
	}

	public void setChannelRaw(int int1) {
		this.channel = int1;
	}

	public float getUseDelta() {
		return this.useDelta;
	}

	public void setUseDelta(float float1) {
		this.useDelta = float1 / 60.0F;
	}

	public float getPower() {
		return this.powerDelta;
	}

	public void setPower(float float1) {
		if (float1 > 1.0F) {
			float1 = 1.0F;
		}

		if (float1 < 0.0F) {
			float1 = 0.0F;
		}

		this.powerDelta = float1;
	}

	public void TriggerPlayerListening(boolean boolean1) {
		if (this.isTurnedOn) {
			ZomboidRadio.getInstance().PlayerListensChannel(this.channel, true, this.isTelevision);
		}
	}

	public void playSoundSend(String string, boolean boolean1) {
		this.playSound(string, boolean1 ? this.deviceVolume * 0.4F : 0.05F, true);
	}

	public void playSoundLocal(String string, boolean boolean1) {
		this.playSound(string, boolean1 ? this.deviceVolume * 0.4F : 0.05F, false);
	}

	public void playSound(String string, float float1, boolean boolean1) {
		if (!GameServer.bServer) {
			this.setEmitterAndPos();
			if (this.emitter != null) {
				long long1 = boolean1 ? this.emitter.playSound(string) : this.emitter.playSoundImpl(string, false, (IsoObject)null);
				this.emitter.setVolume(long1, float1);
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
		Object object = null;
		if (this.parent != null && this.parent instanceof IsoObject) {
			object = (IsoObject)this.parent;
		} else if (this.parent != null && this.parent instanceof Radio) {
			object = IsoPlayer.getInstance();
		}

		if (object != null) {
			if (this.emitter == null) {
				this.emitter = IsoWorld.instance.getFreeEmitter(((IsoObject)object).getX() + 0.5F, ((IsoObject)object).getY() + 0.5F, (float)((int)((IsoObject)object).getZ()));
				IsoWorld.instance.currentEmitters.remove(this.emitter);
			} else {
				this.emitter.setPos(((IsoObject)object).getX() + 0.5F, ((IsoObject)object).getY() + 0.5F, (float)((int)((IsoObject)object).getZ()));
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

					String string = !this.isTelevision ? "RadioStatic" : "TelevisionTestBeep";
					if (this.radioLoopSound == 0L || this.signalCounter <= 0.0F && !this.emitter.isPlaying(string)) {
						if (this.radioLoopSound > 0L) {
							this.emitter.stopSound(this.radioLoopSound);
							if (this.isTelevision) {
								this.playSoundLocal("TelevisionZap", true);
							} else {
								this.playSoundLocal("RadioZap", true);
							}
						}

						this.radioLoopSound = this.emitter.playSoundImpl(string, false, (IsoObject)null);
						this.emitter.setVolume(this.radioLoopSound, this.deviceVolume * 0.4F);
					}

					this.emitter.tick();
				}
			}
		}
	}

	public void update(boolean boolean1, boolean boolean2) {
		if (this.lastMinuteStamp == -1L) {
			this.lastMinuteStamp = this.gameTime.getMinutesStamp();
		}

		if (this.gameTime.getMinutesStamp() > this.lastMinuteStamp) {
			long long1 = this.gameTime.getMinutesStamp() - this.lastMinuteStamp;
			this.lastMinuteStamp = this.gameTime.getMinutesStamp();
			this.listenCnt = (int)((long)this.listenCnt + long1);
			if (this.listenCnt >= 10) {
				this.listenCnt = 0;
			}

			if (!GameServer.bServer && this.isTurnedOn && boolean2 && (this.listenCnt == 0 || this.listenCnt == 5)) {
				this.TriggerPlayerListening(true);
			}

			if (this.isTurnedOn && this.isBatteryPowered && this.powerDelta > 0.0F) {
				float float1 = this.powerDelta - this.powerDelta % 0.01F;
				this.setPower(this.powerDelta - this.useDelta * (float)long1);
				if (this.listenCnt == 0 || this.powerDelta == 0.0F || this.powerDelta < float1) {
					if (boolean1 && GameServer.bServer) {
						this.transmitDeviceDataStateServer((byte)3, (UdpConnection)null);
					} else if (!boolean1 && GameClient.bClient) {
						this.transmitDeviceDataState((byte)3);
					}
				}
			}
		}

		if (this.isTurnedOn && (this.isBatteryPowered && this.powerDelta <= 0.0F || !this.canBePoweredHere())) {
			this.isTurnedOn = false;
			if (boolean1 && GameServer.bServer) {
				this.transmitDeviceDataStateServer((byte)0, (UdpConnection)null);
			} else if (!boolean1 && GameClient.bClient) {
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
			this.soundCounter = (float)((double)this.soundCounter - 1.25 * (double)GameTime.getInstance().getMultiplier());
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
				Object object = null;
				if (this.parent != null && this.parent instanceof IsoObject) {
					object = (IsoObject)this.parent;
				} else if (this.parent != null && this.parent instanceof Radio) {
					object = IsoPlayer.getInstance();
				} else if (this.parent instanceof VehiclePart) {
					object = ((VehiclePart)this.parent).getVehicle();
				}

				if (object != null) {
					int int1 = (int)(100.0F * this.deviceVolume);
					int int2 = this.getDeviceSoundVolumeRange();
					WorldSoundManager.instance.addSound((IsoObject)object, (int)((IsoObject)object).getX(), (int)((IsoObject)object).getY(), (int)((IsoObject)object).getZ(), int2, int1, int1 > 50);
				}
			}

			this.doTriggerWorldSound = false;
			this.soundCounter = (float)(300 + Rand.Next(0, 300));
		}
	}

	private void updateStaticSounds() {
		if (this.isTurnedOn) {
			float float1 = GameTime.getInstance().getMultiplier();
			this.nextStaticSound -= float1;
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
			Radio radio = (Radio)this.getParent();
			return radio.getPlayer() != null && radio.getPlayer().getSquare() != null && radio.getPlayer().getSquare().getRoom() != null ? 3 + (int)(this.baseVolumeRange * 0.4F * this.deviceVolume) : 5 + (int)(this.baseVolumeRange * this.deviceVolume);
		} else if (this.isIsoDevice()) {
			IsoWaveSignal waveSignal = (IsoWaveSignal)this.getParent();
			return waveSignal.getSquare() != null && waveSignal.getSquare().getRoom() != null ? 3 + (int)(this.baseVolumeRange * 0.5F * this.deviceVolume) : 5 + (int)(this.baseVolumeRange * 0.75F * this.deviceVolume);
		} else {
			return 5 + (int)(this.baseVolumeRange / 2.0F * this.deviceVolume);
		}
	}

	public void doReceiveSignal(int int1) {
		if (this.isTurnedOn) {
			this.lastRecordedDistance = int1;
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

	private void transmitDeviceDataState(byte byte1) {
		if (GameClient.bClient) {
			try {
				this.sendDeviceDataStatePacket(GameClient.connection, byte1);
			} catch (Exception exception) {
				System.out.print(exception.getMessage());
			}
		}
	}

	private void transmitDeviceDataStateServer(byte byte1, UdpConnection udpConnection) {
		if (GameServer.bServer) {
			try {
				for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
					UdpConnection udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int1);
					if (udpConnection == null || udpConnection != udpConnection2) {
						this.sendDeviceDataStatePacket(udpConnection2, byte1);
					}
				}
			} catch (Exception exception) {
				System.out.print(exception.getMessage());
			}
		}
	}

	private void sendDeviceDataStatePacket(UdpConnection udpConnection, byte byte1) throws IOException {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypesShort.doPacket((short)1004, byteBufferWriter);
		boolean boolean1 = false;
		if (this.isIsoDevice()) {
			IsoWaveSignal waveSignal = (IsoWaveSignal)this.getParent();
			IsoGridSquare square = waveSignal.getSquare();
			if (square != null) {
				byteBufferWriter.putByte((byte)1);
				byteBufferWriter.putInt(square.getX());
				byteBufferWriter.putInt(square.getY());
				byteBufferWriter.putInt(square.getZ());
				byteBufferWriter.putInt(square.getObjects().indexOf(waveSignal));
				boolean1 = true;
			}
		} else if (this.isInventoryDevice()) {
			Radio radio = (Radio)this.getParent();
			IsoPlayer player = null;
			if (radio.getEquipParent() != null && radio.getEquipParent() instanceof IsoPlayer) {
				player = (IsoPlayer)radio.getEquipParent();
			}

			if (player != null) {
				byteBufferWriter.putByte((byte)0);
				if (GameServer.bServer) {
					byteBufferWriter.putInt(player != null ? player.OnlineID : -1);
				} else {
					byteBufferWriter.putByte((byte)player.PlayerIndex);
				}

				if (player.getPrimaryHandItem() == radio) {
					byteBufferWriter.putByte((byte)1);
				} else if (player.getSecondaryHandItem() == radio) {
					byteBufferWriter.putByte((byte)2);
				} else {
					byteBufferWriter.putByte((byte)0);
				}

				boolean1 = true;
			}
		} else if (this.isVehicleDevice()) {
			VehiclePart vehiclePart = (VehiclePart)this.getParent();
			byteBufferWriter.putByte((byte)2);
			byteBufferWriter.putShort(vehiclePart.getVehicle().VehicleID);
			byteBufferWriter.putShort((short)vehiclePart.getIndex());
			boolean1 = true;
		}

		if (boolean1) {
			byteBufferWriter.putByte(byte1);
			label73: switch (byte1) {
			case 0: 
				byteBufferWriter.putByte((byte)(this.isTurnedOn ? 1 : 0));
				break;
			
			case 1: 
				byteBufferWriter.putInt(this.channel);
				break;
			
			case 2: 
				byteBufferWriter.putByte((byte)(this.hasBattery ? 1 : 0));
				byteBufferWriter.putFloat(this.powerDelta);
				break;
			
			case 3: 
				byteBufferWriter.putFloat(this.powerDelta);
				break;
			
			case 4: 
				byteBufferWriter.putFloat(this.deviceVolume);
				break;
			
			case 5: 
				byteBufferWriter.putInt(this.presets.getPresets().size());
				Iterator iterator = this.presets.getPresets().iterator();
				while (true) {
					if (!iterator.hasNext()) {
						break label73;
					}

					PresetEntry presetEntry = (PresetEntry)iterator.next();
					GameWindow.WriteString(byteBufferWriter.bb, presetEntry.getName());
					byteBufferWriter.putInt(presetEntry.getFrequency());
				}

			
			case 6: 
				byteBufferWriter.putInt(this.headphoneType);
			
			}

			udpConnection.endPacketImmediate();
		} else {
			udpConnection.cancelPacket();
		}
	}

	public void receiveDeviceDataStatePacket(ByteBuffer byteBuffer, UdpConnection udpConnection) throws IOException {
		if (GameClient.bClient || GameServer.bServer) {
			boolean boolean1 = GameServer.bServer;
			boolean boolean2 = this.isIsoDevice() || this.isVehicleDevice();
			byte byte1 = byteBuffer.get();
			switch (byte1) {
			case 0: 
				if (boolean1 && boolean2) {
					this.setIsTurnedOn(byteBuffer.get() == 1);
				} else {
					this.isTurnedOn = byteBuffer.get() == 1;
				}

				if (boolean1) {
					this.transmitDeviceDataStateServer(byte1, !boolean2 ? udpConnection : null);
				}

				break;
			
			case 1: 
				int int1 = byteBuffer.getInt();
				if (boolean1 && boolean2) {
					this.setChannel(int1);
				} else {
					this.channel = int1;
				}

				if (boolean1) {
					this.transmitDeviceDataStateServer(byte1, !boolean2 ? udpConnection : null);
				}

				break;
			
			case 2: 
				boolean boolean3 = byteBuffer.get() == 1;
				float float1 = byteBuffer.getFloat();
				if (boolean1 && boolean2) {
					this.hasBattery = boolean3;
					this.setPower(float1);
				} else {
					this.hasBattery = boolean3;
					this.powerDelta = float1;
				}

				if (boolean1) {
					this.transmitDeviceDataStateServer(byte1, !boolean2 ? udpConnection : null);
				}

				break;
			
			case 3: 
				float float2 = byteBuffer.getFloat();
				if (boolean1 && boolean2) {
					this.setPower(float2);
				} else {
					this.powerDelta = float2;
				}

				if (boolean1) {
					this.transmitDeviceDataStateServer(byte1, !boolean2 ? udpConnection : null);
				}

				break;
			
			case 4: 
				float float3 = byteBuffer.getFloat();
				if (boolean1 && boolean2) {
					this.setDeviceVolume(float3);
				} else {
					this.deviceVolume = float3;
				}

				if (boolean1) {
					this.transmitDeviceDataStateServer(byte1, !boolean2 ? udpConnection : null);
				}

				break;
			
			case 5: 
				int int2 = byteBuffer.getInt();
				for (int int3 = 0; int3 < int2; ++int3) {
					String string = GameWindow.ReadString(byteBuffer);
					int int4 = byteBuffer.getInt();
					if (int3 < this.presets.getPresets().size()) {
						PresetEntry presetEntry = (PresetEntry)this.presets.getPresets().get(int3);
						if (!presetEntry.getName().equals(string) || presetEntry.getFrequency() != int4) {
							presetEntry.setName(string);
							presetEntry.setFrequency(int4);
						}
					} else {
						this.presets.addPreset(string, int4);
					}
				}

				if (boolean1) {
					this.transmitDeviceDataStateServer((byte)5, !boolean2 ? udpConnection : null);
				}

				break;
			
			case 6: 
				this.headphoneType = byteBuffer.getInt();
				if (boolean1) {
					this.transmitDeviceDataStateServer(byte1, !boolean2 ? udpConnection : null);
				}

			
			}
		}
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		GameWindow.WriteString(byteBuffer, this.deviceName);
		byteBuffer.put((byte)(this.twoWay ? 1 : 0));
		byteBuffer.putInt(this.transmitRange);
		byteBuffer.putInt(this.micRange);
		byteBuffer.put((byte)(this.micIsMuted ? 1 : 0));
		byteBuffer.putFloat(this.baseVolumeRange);
		byteBuffer.putFloat(this.deviceVolume);
		byteBuffer.put((byte)(this.isPortable ? 1 : 0));
		byteBuffer.put((byte)(this.isTelevision ? 1 : 0));
		byteBuffer.put((byte)(this.isHighTier ? 1 : 0));
		byteBuffer.put((byte)(this.isTurnedOn ? 1 : 0));
		byteBuffer.putInt(this.channel);
		byteBuffer.putInt(this.minChannelRange);
		byteBuffer.putInt(this.maxChannelRange);
		byteBuffer.put((byte)(this.isBatteryPowered ? 1 : 0));
		byteBuffer.put((byte)(this.hasBattery ? 1 : 0));
		byteBuffer.putFloat(this.powerDelta);
		byteBuffer.putFloat(this.useDelta);
		byteBuffer.putInt(this.headphoneType);
		if (this.presets != null) {
			byteBuffer.put((byte)1);
			this.presets.save(byteBuffer, boolean1);
		} else {
			byteBuffer.put((byte)0);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		if (this.presets == null) {
			this.presets = new DevicePresets();
		}

		if (int1 >= 69) {
			this.deviceName = GameWindow.ReadString(byteBuffer);
			this.twoWay = byteBuffer.get() == 1;
			this.transmitRange = byteBuffer.getInt();
			this.micRange = byteBuffer.getInt();
			this.micIsMuted = byteBuffer.get() == 1;
			this.baseVolumeRange = byteBuffer.getFloat();
			this.deviceVolume = byteBuffer.getFloat();
			this.isPortable = byteBuffer.get() == 1;
			this.isTelevision = byteBuffer.get() == 1;
			this.isHighTier = byteBuffer.get() == 1;
			this.isTurnedOn = byteBuffer.get() == 1;
			this.channel = byteBuffer.getInt();
			this.minChannelRange = byteBuffer.getInt();
			this.maxChannelRange = byteBuffer.getInt();
			this.isBatteryPowered = byteBuffer.get() == 1;
			this.hasBattery = byteBuffer.get() == 1;
			this.powerDelta = byteBuffer.getFloat();
			this.useDelta = byteBuffer.getFloat();
			this.headphoneType = byteBuffer.getInt();
			if (byteBuffer.get() == 1) {
				this.presets.load(byteBuffer, int1, boolean1);
			}
		}
	}
}
