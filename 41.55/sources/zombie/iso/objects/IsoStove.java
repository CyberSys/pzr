package zombie.iso.objects;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.GameTime;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.audio.BaseSoundEmitter;
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
import zombie.util.Type;


public class IsoStove extends IsoObject implements Activatable {
	private static final ArrayList s_tempObjects = new ArrayList();
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
					if (this.isMicrowave()) {
						this.setActivated(false);
					} else if ("stove".equals(this.container.getType())) {
						this.getSpriteGridObjects(s_tempObjects);
						if (s_tempObjects.isEmpty() || this == s_tempObjects.get(0)) {
							BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (float)((int)this.getZ()));
							baseSoundEmitter.playSoundImpl("StoveTimerExpired", (IsoObject)this);
						}
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

			if (this.container != null && this.isMicrowave()) {
				if (this.Activated()) {
					this.currentTemperature = this.getMaxTemperature();
				} else {
					this.currentTemperature = 0.0F;
				}
			}
		}
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
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

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		byteBuffer.put((byte)(this.activated ? 1 : 0));
		byteBuffer.putInt(this.secondsTimer);
		byteBuffer.putFloat(this.maxTemperature);
		byteBuffer.put((byte)(this.firstTurnOn ? 1 : 0));
		byteBuffer.put((byte)(this.broken ? 1 : 0));
	}

	public void addToWorld() {
		if (this.container != null) {
			IsoCell cell = this.getCell();
			cell.addToProcessIsoObject(this);
			this.container.addItemsToProcessItems();
			this.setActivated(this.activated);
		}
	}

	public void Toggle() {
		SoundManager.instance.PlayWorldSound(this.isMicrowave() ? "ToggleMicrowave" : "ToggleStove", this.getSquare(), 1.0F, 1.0F, 1.0F, false);
		this.setActivated(!this.activated);
		this.container.addItemsToProcessItems();
		IsoGenerator.updateGenerator(this.square);
		this.syncIsoObject(false, (byte)(this.activated ? 1 : 0), (UdpConnection)null, (ByteBuffer)null);
		this.syncSpriteGridObjects(true, true);
	}

	public void sync() {
		this.syncIsoObject(false, (byte)(this.activated ? 1 : 0), (UdpConnection)null, (ByteBuffer)null);
	}

	private void doSound() {
		if (GameServer.bServer) {
			this.hasMetal();
		} else {
			if (this.isMicrowave()) {
				if (this.activated) {
					if (this.soundInstance != -1L) {
						this.emitter.stopSound(this.soundInstance);
					}

					this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (float)((int)this.getZ()));
					IsoWorld.instance.setEmitterOwner(this.emitter, this);
					if (this.hasMetal()) {
						this.soundInstance = this.emitter.playSoundLoopedImpl("MicrowaveCookingMetal");
					} else {
						this.soundInstance = this.emitter.playSoundLoopedImpl("MicrowaveRunning");
					}
				} else if (this.soundInstance != -1L) {
					this.emitter.stopSound(this.soundInstance);
					this.emitter = null;
					this.soundInstance = -1L;
					if (this.container != null && this.container.isPowered()) {
						BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (float)((int)this.getZ()));
						baseSoundEmitter.playSoundImpl("MicrowaveTimerExpired", (IsoObject)this);
					}
				}
			} else if (this.getContainer() != null && "stove".equals(this.container.getType())) {
				if (this.Activated()) {
					if (this.soundInstance != -1L) {
						this.emitter.stopSound(this.soundInstance);
					}

					this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (float)((int)this.getZ()));
					IsoWorld.instance.setEmitterOwner(this.emitter, this);
					this.soundInstance = this.emitter.playSoundLoopedImpl("StoveRunning");
				} else if (this.soundInstance != -1L) {
					this.emitter.stopSound(this.soundInstance);
					this.emitter = null;
					this.soundInstance = -1L;
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
			PrintStream printStream = System.out;
			String string = this.getClass().getSimpleName();
			printStream.println("ERROR: " + string + " not found on square " + this.square.getX() + "," + this.square.getY() + "," + this.square.getZ());
		} else {
			if (GameClient.bClient && !boolean1) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.doPacket((short)12, byteBufferWriter);
				this.syncIsoObjectSend(byteBufferWriter);
				GameClient.connection.endPacketImmediate();
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
				if (this.isMicrowave() && this.secondsTimer < 0) {
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
				if (this.isMicrowave() && this.secondsTimer < 0) {
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

	public boolean isMicrowave() {
		return this.getContainer() != null && this.getContainer().isMicrowave();
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

	public void syncSpriteGridObjects(boolean boolean1, boolean boolean2) {
		this.getSpriteGridObjects(s_tempObjects);
		for (int int1 = s_tempObjects.size() - 1; int1 >= 0; --int1) {
			IsoStove stove = (IsoStove)Type.tryCastTo((IsoObject)s_tempObjects.get(int1), IsoStove.class);
			if (stove != null && stove != this) {
				stove.activated = this.activated;
				stove.maxTemperature = this.maxTemperature;
				stove.firstTurnOn = this.firstTurnOn;
				stove.secondsTimer = this.secondsTimer;
				stove.startTime = this.startTime;
				stove.stopTime = this.stopTime;
				stove.hasMetal = this.hasMetal;
				stove.doOverlay();
				stove.doSound();
				if (boolean1) {
					if (stove.container != null) {
						stove.container.addItemsToProcessItems();
					}

					IsoGenerator.updateGenerator(stove.square);
				}

				if (boolean2) {
					stove.sync();
				}
			}
		}
	}
}
