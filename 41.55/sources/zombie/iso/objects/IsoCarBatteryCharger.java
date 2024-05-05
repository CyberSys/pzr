package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.opengl.Shader;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.model.WorldItemModelDrawer;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.DrainableComboItem;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
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
	protected long sound = 0L;

	public IsoCarBatteryCharger(IsoCell cell) {
		super(cell);
	}

	public IsoCarBatteryCharger(InventoryItem inventoryItem, IsoCell cell, IsoGridSquare square) {
		super(cell, square, (IsoSprite)null);
		if (inventoryItem == null) {
			throw new NullPointerException("item is null");
		} else {
			this.item = inventoryItem;
		}
	}

	public String getObjectName() {
		return "IsoCarBatteryCharger";
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		if (byteBuffer.get() == 1) {
			try {
				this.item = InventoryItem.loadItem(byteBuffer, int1);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		if (byteBuffer.get() == 1) {
			try {
				this.battery = InventoryItem.loadItem(byteBuffer, int1);
			} catch (Exception exception2) {
				exception2.printStackTrace();
			}
		}

		this.activated = byteBuffer.get() == 1;
		this.lastUpdate = byteBuffer.getFloat();
		this.chargeRate = byteBuffer.getFloat();
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		if (this.item == null) {
			assert false;
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte)1);
			this.item.saveWithSize(byteBuffer, false);
		}

		if (this.battery == null) {
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte)1);
			this.battery.saveWithSize(byteBuffer, false);
		}

		byteBuffer.put((byte)(this.activated ? 1 : 0));
		byteBuffer.putFloat(this.lastUpdate);
		byteBuffer.putFloat(this.chargeRate);
	}

	public void addToWorld() {
		super.addToWorld();
		this.getCell().addToProcessIsoObject(this);
	}

	public void removeFromWorld() {
		this.stopChargingSound();
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
			this.stopChargingSound();
		} else {
			boolean boolean1 = this.square != null && (this.square.haveElectricity() || GameTime.instance.NightsSurvived < SandboxOptions.instance.getElecShutModifier() && this.square.getRoom() != null);
			if (!boolean1) {
				this.activated = false;
			}

			if (!this.activated) {
				this.lastUpdate = -1.0F;
				this.stopChargingSound();
			} else {
				this.startChargingSound();
				DrainableComboItem drainableComboItem = (DrainableComboItem)this.battery;
				if (!(drainableComboItem.getUsedDelta() >= 1.0F)) {
					float float1 = (float)GameTime.getInstance().getWorldAgeHours();
					if (this.lastUpdate < 0.0F) {
						this.lastUpdate = float1;
					}

					if (this.lastUpdate > float1) {
						this.lastUpdate = float1;
					}

					float float2 = float1 - this.lastUpdate;
					if (float2 > 0.0F) {
						drainableComboItem.setUsedDelta(Math.min(1.0F, drainableComboItem.getUsedDelta() + this.chargeRate * float2));
						this.lastUpdate = float1;
					}
				}
			}
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		this.chargerSprite = this.configureSprite(this.item, this.chargerSprite);
		if (this.chargerSprite.CurrentAnim != null && !this.chargerSprite.CurrentAnim.Frames.isEmpty()) {
			Texture texture = ((IsoDirectionFrame)this.chargerSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir);
			if (texture != null) {
				float float4 = (float)texture.getWidthOrig() * this.chargerSprite.def.getScaleX() / 2.0F;
				float float5 = (float)texture.getHeightOrig() * this.chargerSprite.def.getScaleY() * 3.0F / 4.0F;
				this.offsetX = this.offsetY = 0.0F;
				this.setAlpha(IsoCamera.frameState.playerIndex, 1.0F);
				float float6 = 0.5F;
				float float7 = 0.5F;
				float float8 = 0.0F;
				this.sx = 0.0F;
				this.item.setWorldZRotation(315);
				if (!WorldItemModelDrawer.renderMain(this.getItem(), this.getSquare(), this.getX() + float6, this.getY() + float7, this.getZ() + float8, -1.0F)) {
					this.chargerSprite.render(this, float1 + float6, float2 + float7, float3 + float8, this.dir, this.offsetX + float4 + (float)(8 * Core.TileScale), this.offsetY + float5 + (float)(4 * Core.TileScale), colorInfo, true);
				}

				if (this.battery != null) {
					this.batterySprite = this.configureSprite(this.battery, this.batterySprite);
					if (this.batterySprite != null && this.batterySprite.CurrentAnim != null && !this.batterySprite.CurrentAnim.Frames.isEmpty()) {
						this.sx = 0.0F;
						this.getBattery().setWorldZRotation(90);
						if (!WorldItemModelDrawer.renderMain(this.getBattery(), this.getSquare(), this.getX() + 0.75F, this.getY() + 0.75F, this.getZ() + float8, -1.0F)) {
							this.batterySprite.render(this, float1 + float6, float2 + float7, float3 + float8, this.dir, this.offsetX + float4 - 8.0F + (float)Core.TileScale, this.offsetY + float5 - (float)(4 * Core.TileScale), colorInfo, true);
						}
					}
				}
			}
		}
	}

	public void renderObjectPicker(float float1, float float2, float float3, ColorInfo colorInfo) {
	}

	private IsoSprite configureSprite(InventoryItem inventoryItem, IsoSprite sprite) {
		String string = inventoryItem.getWorldTexture();
		Texture texture;
		try {
			texture = Texture.getSharedTexture(string);
			if (texture == null) {
				string = inventoryItem.getTex().getName();
			}
		} catch (Exception exception) {
			string = "media/inventory/world/WItem_Sack.png";
		}

		texture = Texture.getSharedTexture(string);
		boolean boolean1 = false;
		if (sprite == null) {
			sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
		}

		if (sprite.CurrentAnim == null) {
			sprite.LoadFramesNoDirPageSimple(string);
			sprite.CurrentAnim.name = string;
			boolean1 = true;
		} else if (!string.equals(sprite.CurrentAnim.name)) {
			sprite.ReplaceCurrentAnimFrames(string);
			sprite.CurrentAnim.name = string;
			boolean1 = true;
		}

		if (boolean1) {
			if (inventoryItem.getScriptItem() == null) {
				sprite.def.scaleAspect((float)texture.getWidthOrig(), (float)texture.getHeightOrig(), (float)(16 * Core.TileScale), (float)(16 * Core.TileScale));
			} else if (this.battery != null && this.battery.getScriptItem() != null) {
				float float1 = (float)Core.TileScale;
				float float2 = this.battery.getScriptItem().ScaleWorldIcon * (float1 / 2.0F);
				sprite.def.setScale(float2, float2);
			}
		}

		return sprite;
	}

	public void syncIsoObjectSend(ByteBufferWriter byteBufferWriter) {
		byte byte1 = (byte)this.getObjectIndex();
		byteBufferWriter.putInt(this.square.getX());
		byteBufferWriter.putInt(this.square.getY());
		byteBufferWriter.putInt(this.square.getZ());
		byteBufferWriter.putByte(byte1);
		byteBufferWriter.putByte((byte)1);
		byteBufferWriter.putByte((byte)0);
		if (this.battery == null) {
			byteBufferWriter.putByte((byte)0);
		} else {
			byteBufferWriter.putByte((byte)1);
			try {
				this.battery.saveWithSize(byteBufferWriter.bb, false);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		byteBufferWriter.putBoolean(this.activated);
		byteBufferWriter.putFloat(this.chargeRate);
	}

	public void syncIsoObject(boolean boolean1, byte byte1, UdpConnection udpConnection, ByteBuffer byteBuffer) {
		if (GameClient.bClient && !boolean1) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.doPacket((short)12, byteBufferWriter);
			this.syncIsoObjectSend(byteBufferWriter);
			GameClient.connection.endPacketImmediate();
		} else {
			Iterator iterator;
			UdpConnection udpConnection2;
			ByteBufferWriter byteBufferWriter2;
			if (GameServer.bServer && !boolean1) {
				iterator = GameServer.udpEngine.connections.iterator();
				while (iterator.hasNext()) {
					udpConnection2 = (UdpConnection)iterator.next();
					byteBufferWriter2 = udpConnection2.startPacket();
					PacketTypes.doPacket((short)12, byteBufferWriter2);
					this.syncIsoObjectSend(byteBufferWriter2);
					udpConnection2.endPacketImmediate();
				}
			} else if (boolean1) {
				if (byteBuffer.get() == 1) {
					try {
						this.battery = InventoryItem.loadItem(byteBuffer, 184);
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				} else {
					this.battery = null;
				}

				this.activated = byteBuffer.get() == 1;
				this.chargeRate = byteBuffer.getFloat();
				if (GameServer.bServer) {
					iterator = GameServer.udpEngine.connections.iterator();
					while (iterator.hasNext()) {
						udpConnection2 = (UdpConnection)iterator.next();
						if (udpConnection != null && udpConnection2 != udpConnection) {
							byteBufferWriter2 = udpConnection2.startPacket();
							PacketTypes.doPacket((short)12, byteBufferWriter2);
							this.syncIsoObjectSend(byteBufferWriter2);
							udpConnection2.endPacketImmediate();
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

	public void setBattery(InventoryItem inventoryItem) {
		if (inventoryItem != null) {
			if (!(inventoryItem instanceof DrainableComboItem)) {
				throw new IllegalArgumentException("battery isn\'t DrainableComboItem");
			}

			if (this.battery != null) {
				throw new IllegalStateException("battery already inserted");
			}
		}

		this.battery = inventoryItem;
	}

	public boolean isActivated() {
		return this.activated;
	}

	public void setActivated(boolean boolean1) {
		this.activated = boolean1;
	}

	public float getChargeRate() {
		return this.chargeRate;
	}

	public void setChargeRate(float float1) {
		if (float1 <= 0.0F) {
			throw new IllegalArgumentException("chargeRate <= 0.0f");
		} else {
			this.chargeRate = float1;
		}
	}

	private void startChargingSound() {
		if (!GameServer.bServer) {
			if (this.getObjectIndex() != -1) {
				if (this.sound != -1L) {
					if (this.emitter == null) {
						this.emitter = IsoWorld.instance.getFreeEmitter((float)this.square.x + 0.5F, (float)this.square.y + 0.5F, (float)this.square.z);
						IsoWorld.instance.takeOwnershipOfEmitter(this.emitter);
					}

					if (!this.emitter.isPlaying(this.sound)) {
						this.sound = this.emitter.playSound("CarBatteryChargerRunning");
						if (this.sound == 0L) {
							this.sound = -1L;
						}
					}

					this.emitter.tick();
				}
			}
		}
	}

	private void stopChargingSound() {
		if (!GameServer.bServer) {
			if (this.emitter != null) {
				this.emitter.stopOrTriggerSound(this.sound);
				this.sound = 0L;
				IsoWorld.instance.returnOwnershipOfEmitter(this.emitter);
				this.emitter = null;
			}
		}
	}
}
