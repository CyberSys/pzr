package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SystemDisabler;
import zombie.WorldSoundManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Moveable;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoObject;
import zombie.iso.IsoRoomLight;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoRoom;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.PacketTypesShort;


public class IsoLightSwitch extends IsoObject {
	boolean Activated = false;
	public ArrayList lights = new ArrayList();
	public boolean lightRoom = false;
	public int RoomID = -1;
	public boolean bStreetLight = false;
	private boolean canBeModified = false;
	private boolean useBattery = false;
	private boolean hasBattery = false;
	private String bulbItem = "Base.LightBulb";
	private float power = 0.0F;
	private float delta = 2.5E-4F;
	private float primaryR = 1.0F;
	private float primaryG = 1.0F;
	private float primaryB = 1.0F;
	protected long lastMinuteStamp = -1L;
	protected int bulbBurnMinutes = -1;
	protected int lastMin = 0;
	protected int nextBreakUpdate = 60;

	public String getObjectName() {
		return "LightSwitch";
	}

	public IsoLightSwitch(IsoCell cell) {
		super(cell);
	}

	public IsoLightSwitch(IsoCell cell, IsoGridSquare square, IsoSprite sprite, int int1) {
		super(cell, square, sprite);
		this.RoomID = int1;
		if (sprite != null && sprite.getProperties().Is("lightR")) {
			if (sprite.getProperties().Is("IsMoveAble")) {
				this.canBeModified = true;
			}
		} else {
			this.lightRoom = true;
		}

		this.bStreetLight = sprite != null && sprite.getProperties().Is("streetlight");
		IsoRoom room = this.square.getRoom();
		if (room != null && this.lightRoom) {
			if (!square.haveElectricity() && GameTime.instance.NightsSurvived >= SandboxOptions.instance.getElecShutModifier()) {
				room.def.bLightsActive = false;
			}

			if (room.lightSwitches.size() > 0) {
				this.lights = ((IsoLightSwitch)room.lightSwitches.get(0)).lights;
				this.Activated = ((IsoLightSwitch)room.lightSwitches.get(0)).Activated;
			} else {
				this.Activated = room.def.bLightsActive;
				room.createLights(this.Activated);
				this.lights = room.lights;
			}

			room.lightSwitches.add(this);
		} else {
			this.Activated = true;
		}
	}

	public void addLightSourceFromSprite() {
		if (this.sprite != null && this.sprite.getProperties().Is("lightR")) {
			float float1 = Float.parseFloat(this.sprite.getProperties().Val("lightR")) / 255.0F;
			float float2 = Float.parseFloat(this.sprite.getProperties().Val("lightG")) / 255.0F;
			float float3 = Float.parseFloat(this.sprite.getProperties().Val("lightB")) / 255.0F;
			this.Activated = false;
			this.setActive(true, true);
			IsoLightSource lightSource = new IsoLightSource(this.square.getX(), this.square.getY(), this.square.getZ(), float1, float2, float3, 10);
			lightSource.bActive = this.Activated;
			lightSource.bHydroPowered = true;
			lightSource.switches.add(this);
			this.lights.add(lightSource);
		}
	}

	public boolean getCanBeModified() {
		return this.canBeModified;
	}

	public float getPower() {
		return this.power;
	}

	public void setPower(float float1) {
		this.power = float1;
	}

	public void setDelta(float float1) {
		this.delta = float1;
	}

	public float getDelta() {
		return this.delta;
	}

	public void setUseBattery(boolean boolean1) {
		this.setActive(false);
		this.useBattery = boolean1;
		if (GameClient.bClient) {
			this.syncCustomizedSettings((UdpConnection)null);
		}
	}

	public boolean getUseBattery() {
		return this.useBattery;
	}

	public boolean getHasBattery() {
		return this.hasBattery;
	}

	public void setHasBatteryRaw(boolean boolean1) {
		this.hasBattery = boolean1;
	}

	public void addBattery(IsoGameCharacter gameCharacter, InventoryItem inventoryItem) {
		if (this.canBeModified && this.useBattery && !this.hasBattery && inventoryItem != null && inventoryItem.getFullType().equals("Base.Battery")) {
			this.power = ((DrainableComboItem)inventoryItem).getUsedDelta();
			this.hasBattery = true;
			if (gameCharacter.getPrimaryHandItem() == inventoryItem) {
				gameCharacter.setPrimaryHandItem((InventoryItem)null);
			}

			if (gameCharacter.getSecondaryHandItem() == inventoryItem) {
				gameCharacter.setSecondaryHandItem((InventoryItem)null);
			}

			gameCharacter.getInventory().Remove(inventoryItem);
			if (GameClient.bClient) {
				this.syncCustomizedSettings((UdpConnection)null);
			}
		}
	}

	public DrainableComboItem removeBattery(IsoGameCharacter gameCharacter) {
		if (this.canBeModified && this.useBattery && this.hasBattery) {
			DrainableComboItem drainableComboItem = (DrainableComboItem)InventoryItemFactory.CreateItem("Base.Battery");
			if (drainableComboItem != null) {
				this.hasBattery = false;
				drainableComboItem.setUsedDelta(this.power >= 0.0F ? this.power : 0.0F);
				this.power = 0.0F;
				this.setActive(false, false, true);
				gameCharacter.getInventory().AddItem((InventoryItem)drainableComboItem);
				if (GameClient.bClient) {
					this.syncCustomizedSettings((UdpConnection)null);
				}

				return drainableComboItem;
			}
		}

		return null;
	}

	public boolean hasLightBulb() {
		return this.bulbItem != null;
	}

	public String getBulbItem() {
		return this.bulbItem;
	}

	public void setBulbItemRaw(String string) {
		this.bulbItem = string;
	}

	public void addLightBulb(IsoGameCharacter gameCharacter, InventoryItem inventoryItem) {
		if (!this.hasLightBulb() && inventoryItem != null && inventoryItem.getType().startsWith("LightBulb")) {
			IsoLightSource lightSource = this.getPrimaryLight();
			if (lightSource != null) {
				this.setPrimaryR(inventoryItem.getColorRed());
				this.setPrimaryG(inventoryItem.getColorGreen());
				this.setPrimaryB(inventoryItem.getColorBlue());
				this.bulbItem = inventoryItem.getFullType();
				if (gameCharacter.getPrimaryHandItem() == inventoryItem) {
					gameCharacter.setPrimaryHandItem((InventoryItem)null);
				}

				if (gameCharacter.getSecondaryHandItem() == inventoryItem) {
					gameCharacter.setSecondaryHandItem((InventoryItem)null);
				}

				gameCharacter.getInventory().Remove(inventoryItem);
				if (GameClient.bClient) {
					this.syncCustomizedSettings((UdpConnection)null);
				}
			}
		}
	}

	public InventoryItem removeLightBulb(IsoGameCharacter gameCharacter) {
		IsoLightSource lightSource = this.getPrimaryLight();
		if (lightSource != null && this.hasLightBulb()) {
			InventoryItem inventoryItem = InventoryItemFactory.CreateItem(this.bulbItem);
			if (inventoryItem != null) {
				inventoryItem.setColorRed(this.getPrimaryR());
				inventoryItem.setColorGreen(this.getPrimaryG());
				inventoryItem.setColorBlue(this.getPrimaryB());
				inventoryItem.setColor(new Color(lightSource.r, lightSource.g, lightSource.b));
				this.bulbItem = null;
				gameCharacter.getInventory().AddItem(inventoryItem);
				this.setActive(false, false, true);
				if (GameClient.bClient) {
					this.syncCustomizedSettings((UdpConnection)null);
				}

				return inventoryItem;
			}
		}

		return null;
	}

	private IsoLightSource getPrimaryLight() {
		return this.lights.size() > 0 ? (IsoLightSource)this.lights.get(0) : null;
	}

	public float getPrimaryR() {
		return this.getPrimaryLight() != null ? this.getPrimaryLight().r : this.primaryR;
	}

	public float getPrimaryG() {
		return this.getPrimaryLight() != null ? this.getPrimaryLight().g : this.primaryG;
	}

	public float getPrimaryB() {
		return this.getPrimaryLight() != null ? this.getPrimaryLight().b : this.primaryB;
	}

	public void setPrimaryR(float float1) {
		this.primaryR = float1;
		if (this.getPrimaryLight() != null) {
			this.getPrimaryLight().r = float1;
		}
	}

	public void setPrimaryG(float float1) {
		this.primaryG = float1;
		if (this.getPrimaryLight() != null) {
			this.getPrimaryLight().g = float1;
		}
	}

	public void setPrimaryB(float float1) {
		this.primaryB = float1;
		if (this.getPrimaryLight() != null) {
			this.getPrimaryLight().b = float1;
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		this.lightRoom = byteBuffer.get() == 1;
		this.RoomID = byteBuffer.getInt();
		this.Activated = byteBuffer.get() == 1;
		if (int1 >= 76) {
			this.canBeModified = byteBuffer.get() == 1;
			if (this.canBeModified) {
				this.useBattery = byteBuffer.get() == 1;
				this.hasBattery = byteBuffer.get() == 1;
				if (byteBuffer.get() == 1) {
					this.bulbItem = GameWindow.ReadString(byteBuffer);
				} else {
					this.bulbItem = null;
				}

				this.power = byteBuffer.getFloat();
				this.delta = byteBuffer.getFloat();
				this.setPrimaryR(byteBuffer.getFloat());
				this.setPrimaryG(byteBuffer.getFloat());
				this.setPrimaryB(byteBuffer.getFloat());
			}
		}

		if (int1 >= 79) {
			this.lastMinuteStamp = byteBuffer.getLong();
			this.bulbBurnMinutes = byteBuffer.getInt();
		}

		this.bStreetLight = this.sprite != null && this.sprite.getProperties().Is("streetlight");
		if (this.square != null) {
			IsoRoom room = this.square.getRoom();
			if (room != null && this.lightRoom) {
				if (room.lightSwitches.size() > 0) {
					this.Activated = room.def.bLightsActive;
					this.lights = ((IsoLightSwitch)room.lightSwitches.get(0)).lights;
				} else {
					this.Activated = room.def.bLightsActive;
					room.createLights(this.Activated);
					this.lights = room.lights;
				}

				room.lightSwitches.add(this);
			} else {
				float float1 = 0.9F;
				float float2 = 0.8F;
				float float3 = 0.7F;
				if (this.sprite != null && this.sprite.getProperties().Is("lightR")) {
					if (int1 >= 76) {
						float1 = this.primaryR;
						float2 = this.primaryG;
						float3 = this.primaryB;
					} else {
						float1 = Float.parseFloat(this.sprite.getProperties().Val("lightR")) / 255.0F;
						float2 = Float.parseFloat(this.sprite.getProperties().Val("lightG")) / 255.0F;
						float3 = Float.parseFloat(this.sprite.getProperties().Val("lightB")) / 255.0F;
					}
				}

				IsoLightSource lightSource = new IsoLightSource((int)this.getX(), (int)this.getY(), (int)this.getZ(), float1, float2, float3, 8);
				lightSource.bActive = this.Activated;
				lightSource.bWasActive = lightSource.bActive;
				lightSource.bHydroPowered = true;
				lightSource.switches.add(this);
				this.lights.add(lightSource);
			}

			if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
				GameClient.instance.objectSyncReq.putRequestLoad(this.square);
			}
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		super.save(byteBuffer);
		byteBuffer.put((byte)(this.lightRoom ? 1 : 0));
		byteBuffer.putInt(this.RoomID);
		byteBuffer.put((byte)(this.Activated ? 1 : 0));
		byteBuffer.put((byte)(this.canBeModified ? 1 : 0));
		if (this.canBeModified) {
			byteBuffer.put((byte)(this.useBattery ? 1 : 0));
			byteBuffer.put((byte)(this.hasBattery ? 1 : 0));
			byteBuffer.put((byte)(this.hasLightBulb() ? 1 : 0));
			if (this.hasLightBulb()) {
				GameWindow.WriteString(byteBuffer, this.bulbItem);
			}

			byteBuffer.putFloat(this.power);
			byteBuffer.putFloat(this.delta);
			byteBuffer.putFloat(this.getPrimaryR());
			byteBuffer.putFloat(this.getPrimaryG());
			byteBuffer.putFloat(this.getPrimaryB());
		}

		byteBuffer.putLong(this.lastMinuteStamp);
		byteBuffer.putInt(this.bulbBurnMinutes);
	}

	public boolean onMouseLeftClick(int int1, int int2) {
		if (IsoPlayer.getInstance() == null) {
			return false;
		} else if (IsoPlayer.getInstance().getCurrentSquare() == null) {
			return false;
		} else {
			int int3 = Math.abs(this.square.getX() - IsoPlayer.getInstance().getCurrentSquare().getX()) + Math.abs(this.square.getY() - IsoPlayer.getInstance().getCurrentSquare().getY() + Math.abs(this.square.getZ() - IsoPlayer.getInstance().getCurrentSquare().getZ()));
			if (int3 < 4 && !IsoPlayer.getInstance().getCurrentSquare().isWallTo(this.square)) {
				this.setActive(!this.Activated);
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean canSwitchLight() {
		if (this.bulbItem != null) {
			boolean boolean1 = GameTime.instance.NightsSurvived < SandboxOptions.instance.getElecShutModifier();
			boolean boolean2 = boolean1 ? this.square.getRoom() != null || this.bStreetLight : this.square.haveElectricity();
			if (!boolean2 && this.getCell() != null) {
				for (int int1 = 0; int1 >= (this.getZ() >= 1.0F ? -1 : 0); --int1) {
					for (int int2 = -1; int2 < 2; ++int2) {
						for (int int3 = -1; int3 < 2; ++int3) {
							if (int2 != 0 || int3 != 0 || int1 != 0) {
								IsoGridSquare square = this.getCell().getGridSquare((double)(this.getX() + (float)int2), (double)(this.getY() + (float)int3), (double)(this.getZ() + (float)int1));
								if (square != null && (boolean1 && square.getRoom() != null || square.haveElectricity())) {
									boolean2 = true;
									break;
								}
							}
						}

						if (boolean2) {
							break;
						}
					}
				}
			}

			if (!this.useBattery && boolean2 || this.canBeModified && this.useBattery && this.hasBattery && this.power > 0.0F) {
				return true;
			}
		}

		return false;
	}

	public boolean setActive(boolean boolean1) {
		return this.setActive(boolean1, false, false);
	}

	public boolean setActive(boolean boolean1, boolean boolean2) {
		return this.setActive(boolean1, boolean2, false);
	}

	public boolean setActive(boolean boolean1, boolean boolean2, boolean boolean3) {
		if (this.bulbItem == null) {
			boolean1 = false;
		}

		if (boolean1 == this.Activated) {
			return this.Activated;
		} else if (this.square.getRoom() == null && !this.canBeModified) {
			return this.Activated;
		} else {
			if (boolean3 || this.canSwitchLight()) {
				this.Activated = boolean1;
				if (!boolean2) {
					IsoWorld.instance.getFreeEmitter().playSound("LightSwitch", this.square);
					if (this.Activated && (GameTime.instance.getHour() > 22 || GameTime.instance.getHour() < 5)) {
						WorldSoundManager.instance.addSound((IsoObject)null, (int)this.getX(), (int)this.getY(), (int)this.getZ(), 50, 3);
					}

					this.switchLight(this.Activated);
					this.syncIsoObject(false, (byte)(this.Activated ? 1 : 0), (UdpConnection)null);
				}
			}

			return this.Activated;
		}
	}

	public boolean toggle() {
		return this.setActive(!this.Activated);
	}

	public void switchLight(boolean boolean1) {
		int int1;
		if (this.lightRoom && this.square.getRoom() != null) {
			this.square.getRoom().def.bLightsActive = boolean1;
			for (int1 = 0; int1 < this.square.getRoom().lightSwitches.size(); ++int1) {
				((IsoLightSwitch)this.square.getRoom().lightSwitches.get(int1)).Activated = boolean1;
			}

			if (GameServer.bServer) {
				int1 = this.square.getX() / 300;
				int int2 = this.square.getY() / 300;
				int int3 = this.square.getRoom().def.ID;
				GameServer.sendMetaGrid(int1, int2, int3);
			}
		}

		for (int1 = 0; int1 < this.lights.size(); ++int1) {
			IsoLightSource lightSource = (IsoLightSource)this.lights.get(int1);
			lightSource.bActive = boolean1;
		}

		IsoGridSquare.RecalcLightTime = -1;
		GameTime.instance.lightSourceUpdate = 100.0F;
	}

	public void getCustomSettingsFromItem(InventoryItem inventoryItem) {
		if (inventoryItem instanceof Moveable) {
			Moveable moveable = (Moveable)inventoryItem;
			if (moveable.isLight()) {
				this.useBattery = moveable.isLightUseBattery();
				this.hasBattery = moveable.isLightHasBattery();
				this.bulbItem = moveable.getLightBulbItem();
				this.power = moveable.getLightPower();
				this.delta = moveable.getLightDelta();
				this.setPrimaryR(moveable.getLightR());
				this.setPrimaryG(moveable.getLightG());
				this.setPrimaryB(moveable.getLightB());
			}
		}
	}

	public void setCustomSettingsToItem(InventoryItem inventoryItem) {
		if (inventoryItem instanceof Moveable) {
			Moveable moveable = (Moveable)inventoryItem;
			moveable.setLightUseBattery(this.useBattery);
			moveable.setLightHasBattery(this.hasBattery);
			moveable.setLightBulbItem(this.bulbItem);
			moveable.setLightPower(this.power);
			moveable.setLightDelta(this.delta);
			moveable.setLightR(this.primaryR);
			moveable.setLightG(this.primaryG);
			moveable.setLightB(this.primaryB);
		}
	}

	public void syncCustomizedSettings(UdpConnection udpConnection) {
		if (GameClient.bClient) {
			this.writeCustomizedSettingsPacket(GameClient.connection);
		} else if (GameServer.bServer) {
			Iterator iterator = GameServer.udpEngine.connections.iterator();
			while (true) {
				UdpConnection udpConnection2;
				do {
					if (!iterator.hasNext()) {
						return;
					}

					udpConnection2 = (UdpConnection)iterator.next();
				}		 while (udpConnection != null && udpConnection2.getConnectedGUID() == udpConnection.getConnectedGUID());

				this.writeCustomizedSettingsPacket(udpConnection2);
			}
		}
	}

	private void writeCustomizedSettingsPacket(UdpConnection udpConnection) {
		if (udpConnection != null) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypesShort.doPacket((short)1200, byteBufferWriter);
			this.writeLightSwitchObjectHeader(byteBufferWriter, (byte)(this.Activated ? 1 : 0));
			byteBufferWriter.putBoolean(this.canBeModified);
			byteBufferWriter.putBoolean(this.useBattery);
			byteBufferWriter.putBoolean(this.hasBattery);
			byteBufferWriter.putByte((byte)(this.bulbItem != null ? 1 : 0));
			if (this.bulbItem != null) {
				GameWindow.WriteString(byteBufferWriter.bb, this.bulbItem);
			}

			byteBufferWriter.putFloat(this.power);
			byteBufferWriter.putFloat(this.delta);
			byteBufferWriter.putFloat(this.primaryR);
			byteBufferWriter.putFloat(this.primaryG);
			byteBufferWriter.putFloat(this.primaryB);
			udpConnection.endPacket();
		}
	}

	private void readCustomizedSettingsPacket(ByteBuffer byteBuffer) {
		this.Activated = byteBuffer.get() == 1;
		this.canBeModified = byteBuffer.get() == 1;
		this.useBattery = byteBuffer.get() == 1;
		this.hasBattery = byteBuffer.get() == 1;
		if (byteBuffer.get() == 1) {
			this.bulbItem = GameWindow.ReadString(byteBuffer);
		} else {
			this.bulbItem = null;
		}

		this.power = byteBuffer.getFloat();
		this.delta = byteBuffer.getFloat();
		this.setPrimaryR(byteBuffer.getFloat());
		this.setPrimaryG(byteBuffer.getFloat());
		this.setPrimaryB(byteBuffer.getFloat());
	}

	public void receiveSyncCustomizedSettings(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (GameClient.bClient) {
			this.readCustomizedSettingsPacket(byteBuffer);
		} else if (GameServer.bServer) {
			this.readCustomizedSettingsPacket(byteBuffer);
			this.syncCustomizedSettings(udpConnection);
		}

		this.switchLight(this.Activated);
	}

	private void writeLightSwitchObjectHeader(ByteBufferWriter byteBufferWriter, byte byte1) {
		byteBufferWriter.putInt(this.square.getX());
		byteBufferWriter.putInt(this.square.getY());
		byteBufferWriter.putInt(this.square.getZ());
		byteBufferWriter.putByte((byte)this.square.getObjects().indexOf(this));
		byteBufferWriter.putByte(byte1);
	}

	public void syncIsoObjectSend(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putInt(this.square.getX());
		byteBufferWriter.putInt(this.square.getY());
		byteBufferWriter.putInt(this.square.getZ());
		byte byte1 = (byte)this.square.getObjects().indexOf(this);
		byteBufferWriter.putByte(byte1);
		byteBufferWriter.putByte((byte)1);
		byteBufferWriter.putByte((byte)(this.Activated ? 1 : 0));
	}

	public void syncIsoObject(boolean boolean1, byte byte1, UdpConnection udpConnection, ByteBuffer byteBuffer) {
		this.syncIsoObject(boolean1, byte1, udpConnection);
	}

	public void syncIsoObject(boolean boolean1, byte byte1, UdpConnection udpConnection) {
		if (this.square == null) {
			System.out.println("ERROR: " + this.getClass().getSimpleName() + " square is null");
		} else if (this.getObjectIndex() == -1) {
			System.out.println("ERROR: " + this.getClass().getSimpleName() + " not found on square " + this.square.getX() + "," + this.square.getY() + "," + this.square.getZ());
		} else {
			if (GameClient.bClient && !boolean1) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.doPacket((short)12, byteBufferWriter);
				this.syncIsoObjectSend(byteBufferWriter);
				GameClient.connection.endPacketImmediate();
				this.square.clientModify();
			} else if (boolean1) {
				if (byte1 == 1) {
					this.switchLight(true);
					this.Activated = true;
				} else {
					this.switchLight(false);
					this.Activated = false;
				}

				if (GameServer.bServer) {
					Iterator iterator = GameServer.udpEngine.connections.iterator();
					while (iterator.hasNext()) {
						UdpConnection udpConnection2 = (UdpConnection)iterator.next();
						ByteBufferWriter byteBufferWriter2;
						if (udpConnection != null) {
							if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
								byteBufferWriter2 = udpConnection2.startPacket();
								PacketTypes.doPacket((short)12, byteBufferWriter2);
								this.syncIsoObjectSend(byteBufferWriter2);
								udpConnection2.endPacketImmediate();
							}
						} else if (udpConnection2.ReleventTo((float)this.square.x, (float)this.square.y)) {
							byteBufferWriter2 = udpConnection2.startPacket();
							PacketTypes.doPacket((short)12, byteBufferWriter2);
							byteBufferWriter2.putInt(this.square.getX());
							byteBufferWriter2.putInt(this.square.getY());
							byteBufferWriter2.putInt(this.square.getZ());
							byte byte2 = (byte)this.square.getObjects().indexOf(this);
							if (byte2 != -1) {
								byteBufferWriter2.putByte(byte2);
							} else {
								byteBufferWriter2.putByte((byte)this.square.getObjects().size());
							}

							byteBufferWriter2.putByte((byte)1);
							byteBufferWriter2.putByte((byte)(this.Activated ? 1 : 0));
							udpConnection2.endPacketImmediate();
						}
					}

					this.square.revisionUp();
				}
			}
		}
	}

	public void update() {
		if (!GameServer.bServer && !GameClient.bClient || GameServer.bServer) {
			boolean boolean1 = false;
			if (!this.Activated) {
				this.lastMinuteStamp = -1L;
			}

			if (!this.lightRoom && this.canBeModified && this.Activated) {
				if (this.lastMinuteStamp == -1L) {
					this.lastMinuteStamp = GameTime.instance.getMinutesStamp();
				}

				if (GameTime.instance.getMinutesStamp() > this.lastMinuteStamp) {
					if (this.bulbBurnMinutes == -1) {
						int int1 = SandboxOptions.instance.getElecShutModifier() * 24 * 60;
						if (this.lastMinuteStamp < (long)int1) {
							this.bulbBurnMinutes = (int)this.lastMinuteStamp;
						} else {
							this.bulbBurnMinutes = int1;
						}
					}

					long long1 = GameTime.instance.getMinutesStamp() - this.lastMinuteStamp;
					this.lastMinuteStamp = GameTime.instance.getMinutesStamp();
					if (this.Activated && this.hasLightBulb()) {
						this.bulbBurnMinutes = (int)((long)this.bulbBurnMinutes + long1);
					}

					this.nextBreakUpdate = (int)((long)this.nextBreakUpdate - long1);
					if (this.nextBreakUpdate <= 0) {
						if (this.Activated && this.hasLightBulb()) {
							int int2 = Rand.Next(0, 1000);
							int int3 = this.bulbBurnMinutes / 10000;
							if (int2 < int3) {
								this.bulbBurnMinutes = 0;
								this.setActive(false, true, true);
								this.bulbItem = null;
								IsoWorld.instance.getFreeEmitter().playSound("LightbulbBurnedOut", this.square);
								boolean1 = true;
							}
						}

						this.nextBreakUpdate = 60;
					}

					if (this.useBattery && this.Activated && this.hasLightBulb() && this.hasBattery && this.power > 0.0F) {
						float float1 = this.power - this.power % 0.01F;
						this.power -= this.delta * (float)long1;
						if (this.power < 0.0F) {
							this.power = 0.0F;
						}

						if (long1 == 1L || this.power < float1) {
							boolean1 = true;
						}
					}
				}

				if (this.useBattery && this.Activated && (this.power <= 0.0F || !this.hasBattery)) {
					this.power = 0.0F;
					this.setActive(false, true, true);
					boolean1 = true;
				}
			}

			if (this.Activated && !this.hasLightBulb()) {
				this.setActive(false, true, true);
				boolean1 = true;
			}

			if (boolean1 && GameServer.bServer) {
				this.syncCustomizedSettings((UdpConnection)null);
			}
		}
	}

	public boolean isActivated() {
		return this.Activated;
	}

	public void addToWorld() {
		if (!this.Activated) {
			this.lastMinuteStamp = -1L;
		}

		if (!this.lightRoom && !this.lights.isEmpty()) {
			for (int int1 = 0; int1 < this.lights.size(); ++int1) {
				IsoWorld.instance.CurrentCell.getLamppostPositions().add(this.lights.get(int1));
			}
		}

		if (this.getCell() != null && this.canBeModified && !this.lightRoom && (!GameServer.bServer && !GameClient.bClient || GameServer.bServer)) {
			this.getCell().getStaticUpdaterObjectList().add(this);
		}
	}

	public void removeFromWorld() {
		if (!this.lightRoom && !this.lights.isEmpty()) {
			for (int int1 = 0; int1 < this.lights.size(); ++int1) {
				((IsoLightSource)this.lights.get(int1)).setActive(false);
				IsoWorld.instance.CurrentCell.removeLamppost((IsoLightSource)this.lights.get(int1));
			}

			this.lights.clear();
		}

		if (this.square != null && this.lightRoom) {
			IsoRoom room = this.square.getRoom();
			if (room != null) {
				room.lightSwitches.remove(this);
			}
		}

		super.removeFromWorld();
	}

	public static void chunkLoaded(IsoChunk chunk) {
		ArrayList arrayList = new ArrayList();
		int int1;
		int int2;
		int int3;
		for (int1 = 0; int1 < 10; ++int1) {
			for (int2 = 0; int2 < 10; ++int2) {
				for (int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = chunk.getGridSquare(int1, int2, int3);
					if (square != null) {
						IsoRoom room = square.getRoom();
						if (room != null && room.hasLightSwitches() && !arrayList.contains(room)) {
							arrayList.add(room);
						}
					}
				}
			}
		}

		int1 = chunk.wx * 10;
		int2 = (chunk.wx + 1) * 10;
		int3 = chunk.wy * 10;
		int int4 = (chunk.wy + 1) * 10;
		for (int int5 = 0; int5 < arrayList.size(); ++int5) {
			IsoRoom room2 = (IsoRoom)arrayList.get(int5);
			if (room2.lights.isEmpty()) {
				room2.createLights(room2.def.bLightsActive);
			}

			int int6;
			for (int6 = 0; int6 < room2.lights.size(); ++int6) {
				IsoLightSource lightSource = (IsoLightSource)room2.lights.get(int6);
				if (lightSource.x >= int1 && lightSource.x < int2 && lightSource.y >= int3 && lightSource.y < int4) {
					lightSource.chunk = chunk;
					lightSource.life = -1;
					if (!IsoWorld.instance.CurrentCell.getLamppostPositions().contains(lightSource)) {
						IsoWorld.instance.CurrentCell.getLamppostPositions().add(lightSource);
					}
				}
			}

			for (int6 = 0; int6 < room2.roomLights.size(); ++int6) {
				IsoRoomLight roomLight = (IsoRoomLight)room2.roomLights.get(int6);
				if (!chunk.roomLights.contains(roomLight)) {
					chunk.roomLights.add(roomLight);
				}
			}
		}
	}
}
