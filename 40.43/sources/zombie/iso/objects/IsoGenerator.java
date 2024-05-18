package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.WorldSoundManager;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Food;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;


public class IsoGenerator extends IsoObject {
	public float fuel = 0.0F;
	public boolean activated = false;
	public int condition = 0;
	private int lastHour = -1;
	public boolean connected = false;
	private int numberOfElectricalItems = 0;
	private boolean updateSurrounding = false;
	private static ArrayList AllGenerators = new ArrayList();
	private static int GENERATOR_RADIUS = 20;

	public IsoGenerator(IsoCell cell) {
		super(cell);
	}

	public IsoGenerator(InventoryItem inventoryItem, IsoCell cell, IsoGridSquare square) {
		super(cell, square, IsoWorld.instance.CurrentCell.SpriteManager.getSprite("appliances_misc_01_0"));
		if (inventoryItem != null) {
			this.setInfoFromItem(inventoryItem);
		}

		this.sprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite("appliances_misc_01_0");
		this.square = square;
		square.AddSpecialObject(this);
		if (GameClient.bClient) {
			this.transmitCompleteItemToServer();
		}
	}

	public IsoGenerator(InventoryItem inventoryItem, IsoCell cell, IsoGridSquare square, boolean boolean1) {
		super(cell, square, IsoWorld.instance.CurrentCell.SpriteManager.getSprite("appliances_misc_01_0"));
		if (inventoryItem != null) {
			this.setInfoFromItem(inventoryItem);
		}

		this.sprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite("appliances_misc_01_0");
		this.square = square;
		square.AddSpecialObject(this);
		if (GameClient.bClient && !boolean1) {
			this.transmitCompleteItemToServer();
		}
	}

	public void setInfoFromItem(InventoryItem inventoryItem) {
		this.condition = inventoryItem.getCondition();
		if (inventoryItem.getModData().rawget("fuel") instanceof Double) {
			this.fuel = ((Double)inventoryItem.getModData().rawget("fuel")).floatValue();
		}
	}

	public void update() {
		if (this.updateSurrounding && this.getSquare() != null) {
			this.setSurroundingElectricity();
			this.updateSurrounding = false;
		}

		if (this.isActivated()) {
			if (!GameServer.bServer && (this.emitter == null || this.emitter.isEmpty())) {
				if (this.emitter == null) {
					this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (float)((int)this.getZ()));
					IsoWorld.instance.currentEmitters.remove(this.emitter);
				}

				this.emitter.playSoundLoopedImpl("GeneratorLoop");
			}

			if (GameClient.bClient) {
				this.emitter.tick();
				return;
			}

			WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), 20, 1);
			if ((int)GameTime.getInstance().getWorldAgeHours() != this.lastHour) {
				int int1 = (int)GameTime.getInstance().getWorldAgeHours() - this.lastHour;
				float float1 = 0.0F;
				int int2 = 0;
				for (int int3 = 0; int3 < int1; ++int3) {
					float float2 = (float)Math.max(1, this.numberOfElectricalItems / 3);
					float2 *= 0.5F;
					float2 = (float)((double)float2 * SandboxOptions.instance.GeneratorFuelConsumption.getValue());
					float1 += float2;
					if (Rand.Next(30) == 0) {
						int2 += Rand.Next(2) + 1;
					}

					if (this.fuel - float1 <= 0.0F || this.condition - int2 <= 0) {
						break;
					}
				}

				this.fuel -= float1;
				if (this.fuel <= 0.0F) {
					this.setActivated(false);
					this.fuel = 0.0F;
				}

				this.condition -= int2;
				if (this.condition <= 0) {
					this.setActivated(false);
					this.condition = 0;
				}

				if (this.condition <= 20) {
					if (Rand.Next(10) == 0) {
						IsoFireManager.StartFire(this.getCell(), this.square, true, 1000);
						this.condition = 0;
						this.setActivated(false);
					} else if (Rand.Next(20) == 0) {
						this.square.explode();
						this.condition = 0;
						this.setActivated(false);
					}
				}

				this.lastHour = (int)GameTime.getInstance().getWorldAgeHours();
				if (GameServer.bServer) {
					this.syncIsoObject(false, (byte)0, (UdpConnection)null, (ByteBuffer)null);
				}
			}
		}

		if (this.emitter != null) {
			this.emitter.tick();
		}
	}

	public void setSurroundingElectricity() {
		this.numberOfElectricalItems = 1;
		if (!this.getSquare().getProperties().Is(IsoFlagType.exterior) && this.getSquare().getBuilding() != null) {
			this.getSquare().getBuilding().setToxic(this.isActivated());
		}

		IsoGridSquare square = null;
		int int1 = this.square.getX() - GENERATOR_RADIUS;
		int int2 = this.square.getX() + GENERATOR_RADIUS;
		int int3 = this.square.getY() - GENERATOR_RADIUS;
		int int4 = this.square.getY() + GENERATOR_RADIUS;
		int int5 = Math.max(0, this.getSquare().getZ() - 3);
		int int6 = Math.min(8, this.getSquare().getZ() + 3);
		int int7;
		int int8;
		int int9;
		int int10;
		for (int7 = int5; int7 < int6; ++int7) {
			for (int8 = int1; int8 <= int2; ++int8) {
				for (int9 = int3; int9 <= int4; ++int9) {
					if (IsoUtils.DistanceToSquared((float)int8, (float)int9, (float)this.getSquare().getX(), (float)this.getSquare().getY()) <= (float)(GENERATOR_RADIUS * GENERATOR_RADIUS)) {
						square = this.getCell().getGridSquare(int8, int9, int7);
						if (square != null && !square.getProperties().Is(IsoFlagType.exterior)) {
							square.setHaveElectricity(this.isActivated());
							for (int10 = 0; int10 < square.getObjects().size(); ++int10) {
								IsoObject object = (IsoObject)square.getObjects().get(int10);
								if (object != null && !(object instanceof IsoWorldInventoryObject)) {
									if (object instanceof IsoStove || object instanceof IsoRadio || object instanceof IsoTelevision) {
										++this.numberOfElectricalItems;
									}

									if (object.getContainerByType("fridge") != null) {
										++this.numberOfElectricalItems;
									}

									if (object.getContainerByType("freezer") != null) {
										++this.numberOfElectricalItems;
									}

									object.checkHaveElectricity();
								}
							}
						}
					}
				}
			}
		}

		if (this.square != null && this.square.chunk != null) {
			int7 = this.square.chunk.wx;
			int8 = this.square.chunk.wy;
			for (int9 = -2; int9 <= 2; ++int9) {
				for (int10 = -2; int10 <= 2; ++int10) {
					IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int7 + int10, int8 + int9) : IsoWorld.instance.CurrentCell.getChunk(int7 + int10, int8 + int9);
					if (chunk != null && this.touchesChunk(chunk)) {
						if (this.isActivated()) {
							chunk.addGeneratorPos(this.square.x, this.square.y, this.square.z);
						} else {
							chunk.removeGeneratorPos(this.square.x, this.square.y, this.square.z);
						}
					}
				}
			}
		}
	}

	private void updateFridgeFreezerItems(IsoObject object) {
		for (int int1 = 0; int1 < object.getContainerCount(); ++int1) {
			ItemContainer itemContainer = object.getContainerByIndex(int1);
			if ("fridge".equals(itemContainer.getType()) || "freezer".equals(itemContainer.getType())) {
				ArrayList arrayList = itemContainer.getItems();
				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					InventoryItem inventoryItem = (InventoryItem)arrayList.get(int2);
					if (inventoryItem instanceof Food) {
						inventoryItem.updateAge();
					}
				}
			}
		}
	}

	private void updateFridgeFreezerItems(IsoGridSquare square) {
		int int1 = square.getObjects().size();
		IsoObject[] objectArray = (IsoObject[])square.getObjects().getElements();
		for (int int2 = 0; int2 < int1; ++int2) {
			IsoObject object = objectArray[int2];
			this.updateFridgeFreezerItems(object);
		}
	}

	private void updateFridgeFreezerItems() {
		if (this.square != null) {
			int int1 = this.square.getX() - GENERATOR_RADIUS;
			int int2 = this.square.getX() + GENERATOR_RADIUS;
			int int3 = this.square.getY() - GENERATOR_RADIUS;
			int int4 = this.square.getY() + GENERATOR_RADIUS;
			int int5 = Math.max(0, this.square.getZ() - 3);
			int int6 = Math.min(8, this.square.getZ() + 3);
			for (int int7 = int5; int7 < int6; ++int7) {
				for (int int8 = int1; int8 <= int2; ++int8) {
					for (int int9 = int3; int9 <= int4; ++int9) {
						if (IsoUtils.DistanceToSquared((float)int8, (float)int9, (float)this.square.x, (float)this.square.y) <= (float)(GENERATOR_RADIUS * GENERATOR_RADIUS)) {
							IsoGridSquare square = this.getCell().getGridSquare(int8, int9, int7);
							if (square != null) {
								this.updateFridgeFreezerItems(square);
							}
						}
					}
				}
			}
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		this.connected = byteBuffer.get() == 1;
		this.activated = byteBuffer.get() == 1;
		if (int1 < 138) {
			this.fuel = (float)byteBuffer.getInt();
		} else {
			this.fuel = byteBuffer.getFloat();
		}

		this.condition = byteBuffer.getInt();
		this.lastHour = byteBuffer.getInt();
		if (int1 >= 96) {
			this.numberOfElectricalItems = byteBuffer.getInt();
		}

		this.updateSurrounding = true;
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		super.save(byteBuffer);
		byteBuffer.put((byte)(this.isConnected() ? 1 : 0));
		byteBuffer.put((byte)(this.isActivated() ? 1 : 0));
		byteBuffer.putFloat(this.getFuel());
		byteBuffer.putInt(this.getCondition());
		byteBuffer.putInt(this.lastHour);
		byteBuffer.putInt(this.numberOfElectricalItems);
	}

	public void remove() {
		if (GameClient.bClient) {
			GameClient.removeSpecialObjectFromSquare((IsoObject)this);
		} else {
			this.removeFromWorld();
			this.removeFromSquare();
			this.getCell().getProcessIsoObjectRemove().add(this);
		}
	}

	public void addToWorld() {
		if (!IsoWorld.instance.getCell().getProcessIsoObjects().contains(this)) {
			IsoWorld.instance.getCell().getProcessIsoObjects().add(this);
		}

		if (!AllGenerators.contains(this)) {
			AllGenerators.add(this);
		}

		if (GameClient.bClient) {
			GameClient.instance.objectSyncReq.putRequest(this.square, this);
		}
	}

	public void removeFromWorld() {
		AllGenerators.remove(this);
		if (this.emitter != null) {
			this.emitter.stopAll();
			IsoWorld.instance.freeEmitters.add(this.emitter);
			this.emitter = null;
		}

		super.removeFromWorld();
	}

	public String getObjectName() {
		return "IsoGenerator";
	}

	public float getFuel() {
		return this.fuel;
	}

	public void setFuel(float float1) {
		this.fuel = float1;
		if (this.fuel > 100.0F) {
			this.fuel = 100.0F;
		}

		if (this.fuel < 0.0F) {
			this.fuel = 0.0F;
		}

		if (GameServer.bServer) {
			this.syncIsoObject(false, (byte)0, (UdpConnection)null, (ByteBuffer)null);
		}

		if (GameClient.bClient) {
			this.syncIsoObject(false, (byte)0, (UdpConnection)null, (ByteBuffer)null);
		}
	}

	public boolean isActivated() {
		return this.activated;
	}

	public void setActivated(boolean boolean1) {
		if (boolean1 != this.activated) {
			if (!GameServer.bServer && this.emitter == null) {
				this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, this.getZ());
				IsoWorld.instance.currentEmitters.remove(this.emitter);
			}

			if (boolean1) {
				this.lastHour = (int)GameTime.getInstance().getWorldAgeHours();
				if (this.emitter != null) {
					this.emitter.playSound("GeneratorStarting");
				}
			} else if (this.emitter != null) {
				if (!this.emitter.isEmpty()) {
					this.emitter.stopAll();
				}

				this.emitter.playSound("GeneratorStopping");
			}

			try {
				this.updateFridgeFreezerItems();
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
			}

			this.activated = boolean1;
			this.setSurroundingElectricity();
			if (GameClient.bClient) {
				this.syncIsoObject(false, (byte)0, (UdpConnection)null, (ByteBuffer)null);
			}

			if (GameServer.bServer) {
				this.syncIsoObject(false, (byte)0, (UdpConnection)null, (ByteBuffer)null);
			}
		}
	}

	public void failToStart() {
		if (!GameServer.bServer) {
			if (this.emitter == null) {
				this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, this.getZ());
				IsoWorld.instance.currentEmitters.remove(this.emitter);
			}

			this.emitter.playSound("GeneratorFailedToStart");
		}
	}

	public int getCondition() {
		return this.condition;
	}

	public void setCondition(int int1) {
		this.condition = int1;
		if (this.condition > 100) {
			this.condition = 100;
		}

		if (this.condition < 0) {
			this.condition = 0;
		}

		if (GameServer.bServer) {
			this.syncIsoObject(false, (byte)0, (UdpConnection)null, (ByteBuffer)null);
		}

		if (GameClient.bClient) {
			this.syncIsoObject(false, (byte)0, (UdpConnection)null, (ByteBuffer)null);
		}
	}

	public boolean isConnected() {
		return this.connected;
	}

	public void setConnected(boolean boolean1) {
		this.connected = boolean1;
		if (GameClient.bClient) {
			this.syncIsoObject(false, (byte)0, (UdpConnection)null, (ByteBuffer)null);
		}
	}

	public void syncIsoObjectSend(ByteBufferWriter byteBufferWriter) {
		byte byte1 = (byte)this.getObjectIndex();
		byteBufferWriter.putInt(this.square.getX());
		byteBufferWriter.putInt(this.square.getY());
		byteBufferWriter.putInt(this.square.getZ());
		byteBufferWriter.putByte(byte1);
		byteBufferWriter.putByte((byte)1);
		byteBufferWriter.putByte((byte)0);
		byteBufferWriter.putFloat(this.fuel);
		byteBufferWriter.putInt(this.condition);
		byteBufferWriter.putByte((byte)(this.activated ? 1 : 0));
		byteBufferWriter.putByte((byte)(this.connected ? 1 : 0));
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
			} else if (GameServer.bServer && !boolean1) {
				Iterator iterator = GameServer.udpEngine.connections.iterator();
				while (iterator.hasNext()) {
					UdpConnection udpConnection2 = (UdpConnection)iterator.next();
					ByteBufferWriter byteBufferWriter2 = udpConnection2.startPacket();
					PacketTypes.doPacket((short)12, byteBufferWriter2);
					this.syncIsoObjectSend(byteBufferWriter2);
					udpConnection2.endPacketImmediate();
				}
			} else if (boolean1) {
				float float1 = byteBuffer.getFloat();
				int int1 = byteBuffer.getInt();
				boolean boolean2 = byteBuffer.get() == 1;
				boolean boolean3 = byteBuffer.get() == 1;
				this.sync(float1, int1, boolean3, boolean2);
				if (GameServer.bServer) {
					Iterator iterator2 = GameServer.udpEngine.connections.iterator();
					while (iterator2.hasNext()) {
						UdpConnection udpConnection3 = (UdpConnection)iterator2.next();
						if (udpConnection != null && udpConnection3.getConnectedGUID() != udpConnection.getConnectedGUID()) {
							ByteBufferWriter byteBufferWriter3 = udpConnection3.startPacket();
							PacketTypes.doPacket((short)12, byteBufferWriter3);
							this.syncIsoObjectSend(byteBufferWriter3);
							udpConnection3.endPacketImmediate();
						}
					}
				}
			}
		}
	}

	public void sync(float float1, int int1, boolean boolean1, boolean boolean2) {
		this.fuel = float1;
		this.condition = int1;
		this.connected = boolean1;
		if (this.activated != boolean2) {
			try {
				this.updateFridgeFreezerItems();
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
			}

			this.activated = boolean2;
			if (boolean2) {
				this.lastHour = (int)GameTime.getInstance().getWorldAgeHours();
			} else if (this.emitter != null) {
				this.emitter.stopAll();
			}

			this.setSurroundingElectricity();
		}
	}

	private boolean touchesChunk(IsoChunk chunk) {
		IsoGridSquare square = this.getSquare();
		assert square != null;
		if (square == null) {
			return false;
		} else {
			int int1 = chunk.wx * 10;
			int int2 = chunk.wy * 10;
			int int3 = int1 + 10 - 1;
			int int4 = int2 + 10 - 1;
			if (square.x - GENERATOR_RADIUS > int3) {
				return false;
			} else if (square.x + GENERATOR_RADIUS < int1) {
				return false;
			} else if (square.y - GENERATOR_RADIUS > int4) {
				return false;
			} else {
				return square.y + GENERATOR_RADIUS >= int2;
			}
		}
	}

	public static void chunkLoaded(IsoChunk chunk) {
		chunk.checkForMissingGenerators();
		int int1;
		for (int1 = -2; int1 <= 2; ++int1) {
			for (int int2 = -2; int2 <= 2; ++int2) {
				if (int2 != 0 || int1 != 0) {
					IsoChunk chunk2 = GameServer.bServer ? ServerMap.instance.getChunk(chunk.wx + int2, chunk.wy + int1) : IsoWorld.instance.CurrentCell.getChunk(chunk.wx + int2, chunk.wy + int1);
					if (chunk2 != null) {
						chunk2.checkForMissingGenerators();
					}
				}
			}
		}

		for (int1 = 0; int1 < AllGenerators.size(); ++int1) {
			IsoGenerator generator = (IsoGenerator)AllGenerators.get(int1);
			if (!generator.updateSurrounding && generator.touchesChunk(chunk)) {
				generator.updateSurrounding = true;
			}
		}
	}

	public static void updateSurroundingNow() {
		for (int int1 = 0; int1 < AllGenerators.size(); ++int1) {
			IsoGenerator generator = (IsoGenerator)AllGenerators.get(int1);
			if (generator.updateSurrounding && generator.getSquare() != null) {
				generator.updateSurrounding = false;
				generator.setSurroundingElectricity();
			}
		}
	}

	public static void Reset() {
		assert AllGenerators.isEmpty();
		AllGenerators.clear();
	}

	public static boolean isPoweringSquare(int int1, int int2, int int3, int int4, int int5, int int6) {
		int int7 = Math.max(0, int3 - 3);
		int int8 = Math.min(8, int3 + 3);
		if (int6 >= int7 && int6 < int8) {
			return IsoUtils.DistanceToSquared((float)int1, (float)int2, (float)int4, (float)int5) <= (float)(GENERATOR_RADIUS * GENERATOR_RADIUS);
		} else {
			return false;
		}
	}
}
