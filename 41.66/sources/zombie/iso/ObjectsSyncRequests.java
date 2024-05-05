package zombie.iso;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.SystemDisabler;
import zombie.Lua.LuaEventManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.UdpEngine;
import zombie.debug.DebugLog;
import zombie.inventory.ItemContainer;
import zombie.iso.objects.IsoLightSwitch;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;
import zombie.network.WorldItemTypes;


public final class ObjectsSyncRequests {
	public static final short ClientSendChunkHashes = 1;
	public static final short ServerSendGridSquareHashes = 2;
	public static final short ClientSendGridSquareRequest = 3;
	public static final short ServerSendGridSquareObjectsHashes = 4;
	public static final short ClientSendObjectRequests = 5;
	public static final short ServerSendObject = 6;
	public ArrayList requestsSyncIsoChunk;
	public ArrayList requestsSyncIsoGridSquare;
	public ArrayList requestsSyncIsoObject;
	public long timeout = 1000L;

	public ObjectsSyncRequests(boolean boolean1) {
		if (boolean1) {
			this.requestsSyncIsoChunk = new ArrayList();
			this.requestsSyncIsoGridSquare = new ArrayList();
			this.requestsSyncIsoObject = new ArrayList();
		} else {
			this.requestsSyncIsoGridSquare = new ArrayList();
		}
	}

	static int getObjectInsertIndex(long[] longArray, long[] longArray2, long long1) {
		if (long1 == longArray2[0]) {
			return 0;
		} else {
			int int1;
			for (int1 = 0; int1 < longArray.length; ++int1) {
				if (longArray[int1] == long1) {
					return -1;
				}
			}

			int1 = 0;
			for (int int2 = 0; int2 < longArray2.length; ++int2) {
				if (int1 < longArray.length && longArray2[int2] == longArray[int1]) {
					++int1;
				}

				if (longArray2[int2] == long1) {
					return int1;
				}
			}

			return -1;
		}
	}

	public void putRequestSyncIsoChunk(IsoChunk chunk) {
		if (!GameClient.bClient || SystemDisabler.doWorldSyncEnable) {
			ObjectsSyncRequests.SyncIsoChunk syncIsoChunk = new ObjectsSyncRequests.SyncIsoChunk();
			syncIsoChunk.x = chunk.wx;
			syncIsoChunk.y = chunk.wy;
			syncIsoChunk.hashCodeObjects = chunk.getHashCodeObjects();
			syncIsoChunk.reqTime = 0L;
			syncIsoChunk.reqCount = 0;
			synchronized (this.requestsSyncIsoChunk) {
				this.requestsSyncIsoChunk.add(syncIsoChunk);
			}
		}
	}

	public void putRequestSyncItemContainer(ItemContainer itemContainer) {
		if (itemContainer != null && itemContainer.parent != null && itemContainer.parent.square != null) {
			this.putRequestSyncIsoGridSquare(itemContainer.parent.square);
		}
	}

	public void putRequestSyncIsoGridSquare(IsoGridSquare square) {
		if (square != null) {
			ObjectsSyncRequests.SyncIsoGridSquare syncIsoGridSquare = new ObjectsSyncRequests.SyncIsoGridSquare();
			syncIsoGridSquare.x = square.x;
			syncIsoGridSquare.y = square.y;
			syncIsoGridSquare.z = square.z;
			syncIsoGridSquare.reqTime = 0L;
			syncIsoGridSquare.reqCount = 0;
			synchronized (this.requestsSyncIsoGridSquare) {
				if (!this.requestsSyncIsoGridSquare.contains(square)) {
					this.requestsSyncIsoGridSquare.add(syncIsoGridSquare);
				} else {
					DebugLog.log("Warning: [putRequestSyncIsoGridSquare] Tryed to add dublicate object.");
				}
			}
		}
	}

	public void sendRequests(UdpConnection udpConnection) {
		if (SystemDisabler.doWorldSyncEnable) {
			ByteBufferWriter byteBufferWriter;
			ByteBuffer byteBuffer;
			int int1;
			int int2;
			int int3;
			int int4;
			if (this.requestsSyncIsoChunk != null && this.requestsSyncIsoChunk.size() != 0) {
				byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.SyncObjects.doPacket(byteBufferWriter);
				byteBufferWriter.putShort((short)1);
				byteBuffer = byteBufferWriter.bb;
				int1 = byteBuffer.position();
				byteBufferWriter.putShort((short)0);
				int2 = 0;
				synchronized (this.requestsSyncIsoChunk) {
					for (int4 = this.requestsSyncIsoChunk.size() - 1; int4 >= 0; --int4) {
						ObjectsSyncRequests.SyncIsoChunk syncIsoChunk = (ObjectsSyncRequests.SyncIsoChunk)this.requestsSyncIsoChunk.get(int4);
						if (syncIsoChunk.reqCount > 3) {
							this.requestsSyncIsoChunk.remove(int4);
						} else {
							if (syncIsoChunk.reqTime == 0L) {
								syncIsoChunk.reqTime = System.currentTimeMillis();
								++int2;
								byteBuffer.putInt(syncIsoChunk.x);
								byteBuffer.putInt(syncIsoChunk.y);
								byteBuffer.putLong(syncIsoChunk.hashCodeObjects);
								++syncIsoChunk.reqCount;
							}

							if (System.currentTimeMillis() - syncIsoChunk.reqTime >= this.timeout) {
								syncIsoChunk.reqTime = System.currentTimeMillis();
								++int2;
								byteBuffer.putInt(syncIsoChunk.x);
								byteBuffer.putInt(syncIsoChunk.y);
								byteBuffer.putLong(syncIsoChunk.hashCodeObjects);
								++syncIsoChunk.reqCount;
							}

							if (int2 >= 5) {
								break;
							}
						}
					}
				}

				if (int2 == 0) {
					GameClient.connection.cancelPacket();
					return;
				}

				int3 = byteBuffer.position();
				byteBuffer.position(int1);
				byteBuffer.putShort((short)int2);
				byteBuffer.position(int3);
				PacketTypes.PacketType.SyncObjects.send(GameClient.connection);
			}

			if (this.requestsSyncIsoGridSquare != null && this.requestsSyncIsoGridSquare.size() != 0) {
				byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.SyncObjects.doPacket(byteBufferWriter);
				byteBufferWriter.putShort((short)3);
				byteBuffer = byteBufferWriter.bb;
				int1 = byteBuffer.position();
				byteBufferWriter.putShort((short)0);
				int2 = 0;
				synchronized (this.requestsSyncIsoGridSquare) {
					for (int4 = 0; int4 < this.requestsSyncIsoGridSquare.size(); ++int4) {
						ObjectsSyncRequests.SyncIsoGridSquare syncIsoGridSquare = (ObjectsSyncRequests.SyncIsoGridSquare)this.requestsSyncIsoGridSquare.get(int4);
						if (syncIsoGridSquare.reqCount > 3) {
							this.requestsSyncIsoGridSquare.remove(int4);
							--int4;
						} else {
							if (syncIsoGridSquare.reqTime == 0L) {
								syncIsoGridSquare.reqTime = System.currentTimeMillis();
								++int2;
								byteBuffer.putInt(syncIsoGridSquare.x);
								byteBuffer.putInt(syncIsoGridSquare.y);
								byteBuffer.put((byte)syncIsoGridSquare.z);
								++syncIsoGridSquare.reqCount;
							}

							if (System.currentTimeMillis() - syncIsoGridSquare.reqTime >= this.timeout) {
								syncIsoGridSquare.reqTime = System.currentTimeMillis();
								++int2;
								byteBuffer.putInt(syncIsoGridSquare.x);
								byteBuffer.putInt(syncIsoGridSquare.y);
								byteBuffer.put((byte)syncIsoGridSquare.z);
								++syncIsoGridSquare.reqCount;
							}

							if (int2 >= 100) {
								break;
							}
						}
					}
				}

				if (int2 == 0) {
					GameClient.connection.cancelPacket();
					return;
				}

				int3 = byteBuffer.position();
				byteBuffer.position(int1);
				byteBuffer.putShort((short)int2);
				byteBuffer.position(int3);
				PacketTypes.PacketType.SyncObjects.send(GameClient.connection);
			}

			if (this.requestsSyncIsoObject != null && this.requestsSyncIsoObject.size() != 0) {
				byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.SyncObjects.doPacket(byteBufferWriter);
				byteBufferWriter.putShort((short)5);
				byteBuffer = byteBufferWriter.bb;
				int1 = byteBuffer.position();
				byteBufferWriter.putShort((short)0);
				int2 = 0;
				synchronized (this.requestsSyncIsoObject) {
					for (int4 = 0; int4 < this.requestsSyncIsoObject.size(); ++int4) {
						ObjectsSyncRequests.SyncIsoObject syncIsoObject = (ObjectsSyncRequests.SyncIsoObject)this.requestsSyncIsoObject.get(int4);
						if (syncIsoObject.reqCount > 3) {
							this.requestsSyncIsoObject.remove(int4);
							--int4;
						} else {
							if (syncIsoObject.reqTime == 0L) {
								syncIsoObject.reqTime = System.currentTimeMillis();
								++int2;
								byteBuffer.putInt(syncIsoObject.x);
								byteBuffer.putInt(syncIsoObject.y);
								byteBuffer.put((byte)syncIsoObject.z);
								byteBuffer.putLong(syncIsoObject.hash);
								++syncIsoObject.reqCount;
							}

							if (System.currentTimeMillis() - syncIsoObject.reqTime >= this.timeout) {
								syncIsoObject.reqTime = System.currentTimeMillis();
								++int2;
								byteBuffer.putInt(syncIsoObject.x);
								byteBuffer.putInt(syncIsoObject.y);
								byteBuffer.put((byte)syncIsoObject.z);
								byteBuffer.putLong(syncIsoObject.hash);
								++syncIsoObject.reqCount;
							}

							if (int2 >= 100) {
								break;
							}
						}
					}
				}

				if (int2 == 0) {
					GameClient.connection.cancelPacket();
					return;
				}

				int3 = byteBuffer.position();
				byteBuffer.position(int1);
				byteBuffer.putShort((short)int2);
				byteBuffer.position(int3);
				PacketTypes.PacketType.SyncObjects.send(GameClient.connection);
			}
		}
	}

	public void receiveSyncIsoChunk(int int1, int int2) {
		synchronized (this.requestsSyncIsoChunk) {
			for (int int3 = 0; int3 < this.requestsSyncIsoChunk.size(); ++int3) {
				ObjectsSyncRequests.SyncIsoChunk syncIsoChunk = (ObjectsSyncRequests.SyncIsoChunk)this.requestsSyncIsoChunk.get(int3);
				if (syncIsoChunk.x == int1 && syncIsoChunk.y == int2) {
					this.requestsSyncIsoChunk.remove(int3);
					return;
				}
			}
		}
	}

	public void receiveSyncIsoGridSquare(int int1, int int2, int int3) {
		synchronized (this.requestsSyncIsoGridSquare) {
			for (int int4 = 0; int4 < this.requestsSyncIsoGridSquare.size(); ++int4) {
				ObjectsSyncRequests.SyncIsoGridSquare syncIsoGridSquare = (ObjectsSyncRequests.SyncIsoGridSquare)this.requestsSyncIsoGridSquare.get(int4);
				if (syncIsoGridSquare.x == int1 && syncIsoGridSquare.y == int2 && syncIsoGridSquare.z == int3) {
					this.requestsSyncIsoGridSquare.remove(int4);
					return;
				}
			}
		}
	}

	public void receiveSyncIsoObject(int int1, int int2, int int3, long long1) {
		synchronized (this.requestsSyncIsoObject) {
			for (int int4 = 0; int4 < this.requestsSyncIsoObject.size(); ++int4) {
				ObjectsSyncRequests.SyncIsoObject syncIsoObject = (ObjectsSyncRequests.SyncIsoObject)this.requestsSyncIsoObject.get(int4);
				if (syncIsoObject.x == int1 && syncIsoObject.y == int2 && syncIsoObject.z == int3 && syncIsoObject.hash == long1) {
					this.requestsSyncIsoObject.remove(int4);
					return;
				}
			}
		}
	}

	public void receiveGridSquareHashes(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		for (int int1 = 0; int1 < short1; ++int1) {
			short short2 = byteBuffer.getShort();
			short short3 = byteBuffer.getShort();
			long long1 = byteBuffer.getLong();
			short short4 = byteBuffer.getShort();
			for (int int2 = 0; int2 < short4; ++int2) {
				int int3 = byteBuffer.get() + short2 * 10;
				int int4 = byteBuffer.get() + short3 * 10;
				byte byte1 = byteBuffer.get();
				int int5 = byteBuffer.getInt();
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int3, int4, byte1);
				if (square != null) {
					int int6 = square.getHashCodeObjectsInt();
					if (int6 != int5) {
						ObjectsSyncRequests.SyncIsoGridSquare syncIsoGridSquare = new ObjectsSyncRequests.SyncIsoGridSquare();
						syncIsoGridSquare.x = int3;
						syncIsoGridSquare.y = int4;
						syncIsoGridSquare.z = byte1;
						syncIsoGridSquare.reqTime = 0L;
						syncIsoGridSquare.reqCount = 0;
						synchronized (this.requestsSyncIsoGridSquare) {
							this.requestsSyncIsoGridSquare.add(syncIsoGridSquare);
						}
					}
				}
			}

			this.receiveSyncIsoChunk(short2, short3);
		}
	}

	public void receiveGridSquareObjectHashes(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		for (int int1 = 0; int1 < short1; ++int1) {
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			byte byte1 = byteBuffer.get();
			this.receiveSyncIsoGridSquare(int2, int3, byte1);
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, byte1);
			if (square == null) {
				return;
			}

			byte byte2 = byteBuffer.get();
			int int4 = byteBuffer.getInt() - 3;
			long[] longArray = new long[byte2];
			for (int int5 = 0; int5 < byte2; ++int5) {
				longArray[int5] = byteBuffer.getLong();
			}

			try {
				boolean[] booleanArray = new boolean[square.getObjects().size()];
				boolean[] booleanArray2 = new boolean[byte2];
				int int6;
				for (int6 = 0; int6 < byte2; ++int6) {
					booleanArray2[int6] = true;
				}

				int6 = 0;
				label83: while (true) {
					if (int6 >= square.getObjects().size()) {
						for (int6 = square.getObjects().size() - 1; int6 >= 0; --int6) {
							if (booleanArray[int6]) {
								((IsoObject)square.getObjects().get(int6)).removeFromWorld();
								((IsoObject)square.getObjects().get(int6)).removeFromSquare();
							}
						}

						int6 = 0;
						while (true) {
							if (int6 >= byte2) {
								break label83;
							}

							if (booleanArray2[int6]) {
								ObjectsSyncRequests.SyncIsoObject syncIsoObject = new ObjectsSyncRequests.SyncIsoObject();
								syncIsoObject.x = int2;
								syncIsoObject.y = int3;
								syncIsoObject.z = byte1;
								syncIsoObject.hash = longArray[int6];
								syncIsoObject.reqTime = 0L;
								syncIsoObject.reqCount = 0;
								synchronized (this.requestsSyncIsoObject) {
									this.requestsSyncIsoObject.add(syncIsoObject);
								}
							}

							++int6;
						}
					}

					booleanArray[int6] = false;
					long long1 = ((IsoObject)square.getObjects().get(int6)).customHashCode();
					boolean boolean1 = false;
					for (int int7 = 0; int7 < byte2; ++int7) {
						if (longArray[int7] == long1) {
							boolean1 = true;
							booleanArray2[int7] = false;
							break;
						}
					}

					if (!boolean1) {
						booleanArray[int6] = true;
					}

					++int6;
				}
			} catch (Throwable throwable) {
				DebugLog.log("ERROR: receiveGridSquareObjects " + throwable.getMessage());
			}

			square.RecalcAllWithNeighbours(true);
			IsoWorld.instance.CurrentCell.checkHaveRoof(square.getX(), square.getY());
			byteBuffer.position(int4);
		}

		LuaEventManager.triggerEvent("OnContainerUpdate");
	}

	public void receiveObject(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		byte byte1 = byteBuffer.get();
		long long1 = byteBuffer.getLong();
		this.receiveSyncIsoObject(int1, int2, byte1, long1);
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, byte1);
		if (square != null) {
			byte byte2 = byteBuffer.get();
			long[] longArray = new long[byte2];
			for (int int3 = 0; int3 < byte2; ++int3) {
				longArray[int3] = byteBuffer.getLong();
			}

			long[] longArray2 = new long[square.getObjects().size()];
			int int4;
			for (int4 = 0; int4 < square.getObjects().size(); ++int4) {
				longArray2[int4] = ((IsoObject)square.getObjects().get(int4)).customHashCode();
			}

			int4 = square.getObjects().size();
			int int5 = getObjectInsertIndex(longArray2, longArray, long1);
			if (int5 == -1) {
				DebugLog.log("ERROR: ObjectsSyncRequest.receiveObject OBJECT EXIST (" + int1 + ", " + int2 + ", " + byte1 + ") hash=" + long1);
			} else {
				IsoObject object = WorldItemTypes.createFromBuffer(byteBuffer);
				if (object != null) {
					object.loadFromRemoteBuffer(byteBuffer, false);
					square.getObjects().add(int5, object);
					if (object instanceof IsoLightSwitch) {
						((IsoLightSwitch)object).addLightSourceFromSprite();
					}

					object.addToWorld();
				}

				square.RecalcAllWithNeighbours(true);
				IsoWorld.instance.CurrentCell.checkHaveRoof(square.getX(), square.getY());
				LuaEventManager.triggerEvent("OnContainerUpdate");
			}
		}
	}

	public void serverSendRequests(UdpEngine udpEngine) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			this.serverSendRequests((UdpConnection)udpEngine.connections.get(int1));
		}

		synchronized (this.requestsSyncIsoGridSquare) {
			for (int int2 = 0; int2 < this.requestsSyncIsoGridSquare.size(); ++int2) {
				this.requestsSyncIsoGridSquare.remove(0);
			}
		}
	}

	public void serverSendRequests(UdpConnection udpConnection) {
		if (this.requestsSyncIsoGridSquare.size() != 0) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.SyncObjects.doPacket(byteBufferWriter);
			byteBufferWriter.putShort((short)4);
			int int1 = byteBufferWriter.bb.position();
			byteBufferWriter.putShort((short)0);
			int int2 = 0;
			int int3;
			for (int3 = 0; int3 < this.requestsSyncIsoGridSquare.size(); ++int3) {
				ObjectsSyncRequests.SyncIsoGridSquare syncIsoGridSquare = (ObjectsSyncRequests.SyncIsoGridSquare)this.requestsSyncIsoGridSquare.get(int3);
				if (udpConnection.RelevantTo((float)syncIsoGridSquare.x, (float)syncIsoGridSquare.y, 100.0F)) {
					IsoGridSquare square = ServerMap.instance.getGridSquare(syncIsoGridSquare.x, syncIsoGridSquare.y, syncIsoGridSquare.z);
					if (square != null) {
						++int2;
						byteBufferWriter.putInt(square.x);
						byteBufferWriter.putInt(square.y);
						byteBufferWriter.putByte((byte)square.z);
						byteBufferWriter.putByte((byte)square.getObjects().size());
						byteBufferWriter.putInt(0);
						int int4 = byteBufferWriter.bb.position();
						int int5;
						for (int5 = 0; int5 < square.getObjects().size(); ++int5) {
							byteBufferWriter.putLong(((IsoObject)square.getObjects().get(int5)).customHashCode());
						}

						int5 = byteBufferWriter.bb.position();
						byteBufferWriter.bb.position(int4 - 4);
						byteBufferWriter.putInt(int5);
						byteBufferWriter.bb.position(int5);
					}
				}
			}

			int3 = byteBufferWriter.bb.position();
			byteBufferWriter.bb.position(int1);
			byteBufferWriter.putShort((short)int2);
			byteBufferWriter.bb.position(int3);
			PacketTypes.PacketType.SyncObjects.send(GameClient.connection);
		}
	}

	private class SyncIsoChunk {
		int x;
		int y;
		long hashCodeObjects;
		long reqTime;
		int reqCount;
	}

	private class SyncIsoGridSquare {
		int x;
		int y;
		int z;
		long reqTime;
		int reqCount;

		public int hashCode() {
			return this.x + this.y + this.z;
		}
	}

	private class SyncIsoObject {
		int x;
		int y;
		int z;
		long hash;
		long reqTime;
		int reqCount;

		public int hashCode() {
			return (int)((long)(this.x + this.y + this.z) + this.hash);
		}
	}
}
