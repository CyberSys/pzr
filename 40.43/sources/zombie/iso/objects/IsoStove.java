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

	public IsoStove(IsoCell cell, IsoGridSquare square, IsoSprite sprite) {
		super(cell, square, sprite);
	}

	public String getObjectName() {
		return "Stove";
	}

	public IsoStove(IsoCell cell) {
		super(cell);
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
			this.stopTime = 0.0;
			this.startTime = 0.0;
			this.secondsTimer = -1;
		}

		if (!GameServer.bServer) {
			if (this.Activated()) {
				if (this.stopTime > 0.0 && this.stopTime < GameTime.instance.getWorldAgeHours()) {
					if ("microwave".equals(this.container.getType())) {
						this.setActivated(false);
					} else if ("stove".equals(this.container.getType())) {
						this.emitter = IsoWorld.instance.getFreeEmitter(this.getX(), this.getY(), (float)((int)this.getZ()));
						this.emitter.playSoundImpl("StoveTimerExpired", true, this);
					}

					this.stopTime = 0.0;
					this.startTime = 0.0;
					this.secondsTimer = -1;
				}

				if (this.getMaxTemperature() > 0.0F && this.currentTemperature < this.getMaxTemperature()) {
					float float1 = (this.getMaxTemperature() - this.currentTemperature) / 700.0F;
					if (float1 < 0.05F) {
						float1 = 0.05F;
					}

					this.currentTemperature += float1 * GameTime.instance.getMultiplier();
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

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		if (int1 >= 28) {
			this.activated = byteBuffer.get() == 1;
		}

		if (int1 >= 106) {
			this.secondsTimer = byteBuffer.getInt();
			this.maxTemperature = byteBuffer.getFloat();
			this.firstTurnOn = byteBuffer.get() == 1;
			this.broken = byteBuffer.get() == 1;
		}

		if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
			GameClient.instance.objectSyncReq.putRequestLoad(this.square);
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		super.save(byteBuffer);
		byteBuffer.put((byte)(this.activated ? 1 : 0));
		byteBuffer.putInt(this.secondsTimer);
		byteBuffer.putFloat(this.maxTemperature);
		byteBuffer.put((byte)(this.firstTurnOn ? 1 : 0));
		byteBuffer.put((byte)(this.broken ? 1 : 0));
	}

	public void addToWorld() {
		if (this.container != null) {
			IsoCell cell = this.getCell();
			if (!cell.getProcessIsoObjects().contains(this)) {
				cell.getProcessIsoObjects().add(this);
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
		int int1 = this.getContainer().getItems().size();
		for (int int2 = 0; int2 < int1; ++int2) {
			InventoryItem inventoryItem = (InventoryItem)this.getContainer().getItems().get(int2);
			if (inventoryItem.getMetalValue() > 0.0F) {
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

	public void syncIsoObjectSend(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putInt(this.square.getX());
		byteBufferWriter.putInt(this.square.getY());
		byteBufferWriter.putInt(this.square.getZ());
		byte byte1 = (byte)this.square.getObjects().indexOf(this);
		byteBufferWriter.putByte(byte1);
		byteBufferWriter.putByte((byte)1);
		byteBufferWriter.putByte((byte)(this.activated ? 1 : 0));
		byteBufferWriter.putInt(this.secondsTimer);
		byteBufferWriter.putFloat(this.maxTemperature);
	}

	public void syncIsoObject(boolean boolean1, byte byte1, UdpConnection udpConnection, ByteBuffer byteBuffer) {
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
				boolean boolean2 = byte1 == 1;
				this.secondsTimer = byteBuffer.getInt();
				this.maxTemperature = byteBuffer.getFloat();
				this.setActivated(boolean2);
				this.container.addItemsToProcessItems();
				if (GameServer.bServer) {
					Iterator iterator = GameServer.udpEngine.connections.iterator();
					while (true) {
						UdpConnection udpConnection2;
						do {
							if (!iterator.hasNext()) {
								this.square.revisionUp();
								return;
							}

							udpConnection2 = (UdpConnection)iterator.next();
						}				 while (udpConnection != null && udpConnection2.getConnectedGUID() == udpConnection.getConnectedGUID());

						ByteBufferWriter byteBufferWriter2 = udpConnection2.startPacket();
						PacketTypes.doPacket((short)12, byteBufferWriter2);
						this.syncIsoObjectSend(byteBufferWriter2);
						udpConnection2.endPacketImmediate();
					}
				}
			}
		}
	}

	public void setActivated(boolean boolean1) {
		if (!this.isBroken()) {
			this.activated = boolean1;
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
					this.stopTime = this.startTime + (double)this.secondsTimer / 3600.0;
				}
			} else {
				this.stopTime = 0.0;
				this.startTime = 0.0;
				this.hasMetal = false;
			}

			this.doSound();
			this.doOverlay();
		}
	}

	private void doOverlay() {
		if (this.Activated() && this.getOverlaySprite() == null) {
			String[] stringArray = this.getSprite().getName().split("_");
			String string = stringArray[0] + "_" + stringArray[1] + "_" + stringArray[2] + "_" + (Integer.parseInt(stringArray[3]) + 64);
			this.setOverlaySprite(string);
		} else if (!this.Activated()) {
			this.setOverlaySprite((String)null);
		}
	}

	public void setTimer(int int1) {
		this.secondsTimer = int1;
		if (this.activated && this.secondsTimer > 0) {
			this.startTime = GameTime.instance.getWorldAgeHours();
			this.stopTime = this.startTime + (double)this.secondsTimer / 3600.0;
		}
	}

	public int getTimer() {
		return this.secondsTimer;
	}

	public float getMaxTemperature() {
		return this.maxTemperature;
	}

	public void setMaxTemperature(float float1) {
		this.maxTemperature = float1;
	}

	public int isRunningFor() {
		return this.startTime == 0.0 ? 0 : (int)((GameTime.instance.getWorldAgeHours() - this.startTime) * 3600.0);
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

	public void setBroken(boolean boolean1) {
		this.broken = boolean1;
	}
}
