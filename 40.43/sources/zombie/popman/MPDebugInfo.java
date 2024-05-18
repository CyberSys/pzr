package zombie.popman;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;


public final class MPDebugInfo {
	public static final MPDebugInfo instance = new MPDebugInfo();
	private final ArrayList loadedCells = new ArrayList();
	private final ObjectPool cellPool = new ObjectPool(){
    
    protected MPDebugInfo.MPCell makeObject() {
        return new MPDebugInfo.MPCell();
    }
};
	private final LoadedAreas loadedAreas = new LoadedAreas(false);
	public ArrayList repopEvents = new ArrayList();
	private final ObjectPool repopEventPool = new ObjectPool(){
    
    protected MPDebugInfo.MPRepopEvent makeObject() {
        return new MPDebugInfo.MPRepopEvent();
    }
};
	private short repopEpoch = 0;
	private long requestTime = 0L;
	private boolean requestFlag = false;
	private boolean requestPacketReceived = false;
	private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
	private float RESPAWN_EVERY_HOURS = 1.0F;
	private float REPOP_DISPLAY_HOURS = 0.5F;

	private static native boolean n_hasData(boolean boolean1);

	private static native void n_requestData();

	private static native int n_getLoadedCellsCount();

	private static native int n_getLoadedCellsData(int int1, ByteBuffer byteBuffer);

	private static native int n_getLoadedAreasCount();

	private static native int n_getLoadedAreasData(int int1, ByteBuffer byteBuffer);

	private static native int n_getRepopEventCount();

	private static native int n_getRepopEventData(int int1, ByteBuffer byteBuffer);

	private void requestServerInfo() {
		if (GameClient.bClient) {
			long long1 = System.currentTimeMillis();
			if (this.requestTime + 1000L <= long1) {
				this.requestTime = long1;
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.doPacket((short)4, byteBufferWriter);
				byteBufferWriter.bb.put((byte)1);
				byteBufferWriter.bb.putShort(this.repopEpoch);
				GameClient.connection.endPacket();
			}
		}
	}

	public void clientPacket(ByteBuffer byteBuffer) {
		if (GameClient.bClient) {
			byte byte1 = byteBuffer.get();
			short short1;
			int int1;
			if (byte1 == 1) {
				this.cellPool.release(this.loadedCells);
				this.loadedCells.clear();
				this.RESPAWN_EVERY_HOURS = byteBuffer.getFloat();
				short1 = byteBuffer.getShort();
				for (int1 = 0; int1 < short1; ++int1) {
					MPDebugInfo.MPCell mPCell = (MPDebugInfo.MPCell)this.cellPool.alloc();
					mPCell.cx = byteBuffer.getShort();
					mPCell.cy = byteBuffer.getShort();
					mPCell.currentPopulation = byteBuffer.getShort();
					mPCell.desiredPopulation = byteBuffer.getShort();
					mPCell.lastRepopTime = byteBuffer.getFloat();
					this.loadedCells.add(mPCell);
				}

				this.loadedAreas.clear();
				short short2 = byteBuffer.getShort();
				for (int int2 = 0; int2 < short2; ++int2) {
					short short3 = byteBuffer.getShort();
					short short4 = byteBuffer.getShort();
					short short5 = byteBuffer.getShort();
					short short6 = byteBuffer.getShort();
					this.loadedAreas.add(short3, short4, short5, short6);
				}
			}

			if (byte1 == 2) {
				this.repopEventPool.release(this.repopEvents);
				this.repopEvents.clear();
				this.repopEpoch = byteBuffer.getShort();
				short1 = byteBuffer.getShort();
				for (int1 = 0; int1 < short1; ++int1) {
					MPDebugInfo.MPRepopEvent mPRepopEvent = (MPDebugInfo.MPRepopEvent)this.repopEventPool.alloc();
					mPRepopEvent.wx = byteBuffer.getShort();
					mPRepopEvent.wy = byteBuffer.getShort();
					mPRepopEvent.worldAge = byteBuffer.getFloat();
					this.repopEvents.add(mPRepopEvent);
				}
			}
		}
	}

	public void serverPacket(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (GameServer.bServer) {
			if (udpConnection.accessLevel.equals("admin")) {
				byte byte1 = byteBuffer.get();
				short short1;
				if (byte1 == 1) {
					this.requestTime = System.currentTimeMillis();
					this.requestPacketReceived = true;
					short1 = byteBuffer.getShort();
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.doPacket((short)4, byteBufferWriter);
					byteBufferWriter.bb.put((byte)1);
					byteBufferWriter.bb.putFloat(this.RESPAWN_EVERY_HOURS);
					byteBufferWriter.bb.putShort((short)this.loadedCells.size());
					int int1;
					for (int1 = 0; int1 < this.loadedCells.size(); ++int1) {
						MPDebugInfo.MPCell mPCell = (MPDebugInfo.MPCell)this.loadedCells.get(int1);
						byteBufferWriter.bb.putShort(mPCell.cx);
						byteBufferWriter.bb.putShort(mPCell.cy);
						byteBufferWriter.bb.putShort(mPCell.currentPopulation);
						byteBufferWriter.bb.putShort(mPCell.desiredPopulation);
						byteBufferWriter.bb.putFloat(mPCell.lastRepopTime);
					}

					byteBufferWriter.bb.putShort((short)this.loadedAreas.count);
					for (int1 = 0; int1 < this.loadedAreas.count; ++int1) {
						int int2 = int1 * 4;
						byteBufferWriter.bb.putShort((short)this.loadedAreas.areas[int2++]);
						byteBufferWriter.bb.putShort((short)this.loadedAreas.areas[int2++]);
						byteBufferWriter.bb.putShort((short)this.loadedAreas.areas[int2++]);
						byteBufferWriter.bb.putShort((short)this.loadedAreas.areas[int2++]);
					}

					if (short1 != this.repopEpoch) {
						byte1 = 2;
					}

					udpConnection.endPacket();
				}

				if (byte1 != 2) {
					short short2;
					if (byte1 == 3) {
						short1 = byteBuffer.getShort();
						short2 = byteBuffer.getShort();
						ZombiePopulationManager.instance.dbgSpawnTimeToZero(short1, short2);
					} else if (byte1 == 4) {
						short1 = byteBuffer.getShort();
						short2 = byteBuffer.getShort();
						ZombiePopulationManager.instance.dbgClearZombies(short1, short2);
					} else if (byte1 == 5) {
						short1 = byteBuffer.getShort();
						short2 = byteBuffer.getShort();
						ZombiePopulationManager.instance.dbgSpawnNow(short1, short2);
					}
				} else {
					ByteBufferWriter byteBufferWriter2 = udpConnection.startPacket();
					PacketTypes.doPacket((short)4, byteBufferWriter2);
					byteBufferWriter2.bb.put((byte)2);
					byteBufferWriter2.bb.putShort(this.repopEpoch);
					byteBufferWriter2.bb.putShort((short)this.repopEvents.size());
					for (int int3 = 0; int3 < this.repopEvents.size(); ++int3) {
						MPDebugInfo.MPRepopEvent mPRepopEvent = (MPDebugInfo.MPRepopEvent)this.repopEvents.get(int3);
						byteBufferWriter2.bb.putShort((short)mPRepopEvent.wx);
						byteBufferWriter2.bb.putShort((short)mPRepopEvent.wy);
						byteBufferWriter2.bb.putFloat(mPRepopEvent.worldAge);
					}

					udpConnection.endPacket();
				}
			}
		}
	}

	public void request() {
		if (GameServer.bServer) {
			this.requestTime = System.currentTimeMillis();
		}
	}

	private void addRepopEvent(int int1, int int2, float float1) {
		float float2 = (float)GameTime.getInstance().getWorldAgeHours();
		while (!this.repopEvents.isEmpty() && ((MPDebugInfo.MPRepopEvent)this.repopEvents.get(0)).worldAge + this.REPOP_DISPLAY_HOURS < float2) {
			this.repopEventPool.release(this.repopEvents.remove(0));
		}

		this.repopEvents.add(((MPDebugInfo.MPRepopEvent)this.repopEventPool.alloc()).init(int1, int2, float1));
		++this.repopEpoch;
	}

	public void serverUpdate() {
		if (GameServer.bServer) {
			long long1 = System.currentTimeMillis();
			if (this.requestTime + 10000L < long1) {
				this.requestFlag = false;
				this.requestPacketReceived = false;
			} else {
				int int1;
				int int2;
				int int3;
				int int4;
				short short1;
				if (this.requestFlag) {
					if (n_hasData(false)) {
						this.requestFlag = false;
						this.cellPool.release(this.loadedCells);
						this.loadedCells.clear();
						this.loadedAreas.clear();
						int1 = n_getLoadedCellsCount();
						int2 = 0;
						while (int2 < int1) {
							this.byteBuffer.clear();
							int3 = n_getLoadedCellsData(int2, this.byteBuffer);
							int2 += int3;
							for (int4 = 0; int4 < int3; ++int4) {
								MPDebugInfo.MPCell mPCell = (MPDebugInfo.MPCell)this.cellPool.alloc();
								mPCell.cx = this.byteBuffer.getShort();
								mPCell.cy = this.byteBuffer.getShort();
								mPCell.currentPopulation = this.byteBuffer.getShort();
								mPCell.desiredPopulation = this.byteBuffer.getShort();
								mPCell.lastRepopTime = this.byteBuffer.getFloat();
								this.loadedCells.add(mPCell);
							}
						}

						int1 = n_getLoadedAreasCount();
						int2 = 0;
						while (int2 < int1) {
							this.byteBuffer.clear();
							int3 = n_getLoadedAreasData(int2, this.byteBuffer);
							int2 += int3;
							for (int4 = 0; int4 < int3; ++int4) {
								boolean boolean1 = this.byteBuffer.get() == 0;
								short1 = this.byteBuffer.getShort();
								short short2 = this.byteBuffer.getShort();
								short short3 = this.byteBuffer.getShort();
								short short4 = this.byteBuffer.getShort();
								this.loadedAreas.add(short1, short2, short3, short4);
							}
						}
					}
				} else if (this.requestPacketReceived) {
					n_requestData();
					this.requestFlag = true;
					this.requestPacketReceived = false;
				}

				if (n_hasData(true)) {
					int1 = n_getRepopEventCount();
					int2 = 0;
					while (int2 < int1) {
						this.byteBuffer.clear();
						int3 = n_getRepopEventData(int2, this.byteBuffer);
						int2 += int3;
						for (int4 = 0; int4 < int3; ++int4) {
							short short5 = this.byteBuffer.getShort();
							short1 = this.byteBuffer.getShort();
							float float1 = this.byteBuffer.getFloat();
							this.addRepopEvent(short5, short1, float1);
						}
					}
				}
			}
		}
	}

	boolean isRespawnEnabled() {
		if (IsoWorld.getZombiesDisabled()) {
			return false;
		} else {
			return !(this.RESPAWN_EVERY_HOURS <= 0.0F);
		}
	}

	public void render(ZombiePopulationRenderer zombiePopulationRenderer, float float1) {
		this.requestServerInfo();
		float float2 = (float)GameTime.getInstance().getWorldAgeHours();
		IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
		zombiePopulationRenderer.outlineRect((float)(metaGrid.minX * 300) * 1.0F, (float)(metaGrid.minY * 300) * 1.0F, (float)((metaGrid.maxX - metaGrid.minX + 1) * 300) * 1.0F, (float)((metaGrid.maxY - metaGrid.minY + 1) * 300) * 1.0F, 1.0F, 1.0F, 1.0F, 0.25F);
		int int1;
		MPDebugInfo.MPCell mPCell;
		float float3;
		for (int1 = 0; int1 < this.loadedCells.size(); ++int1) {
			mPCell = (MPDebugInfo.MPCell)this.loadedCells.get(int1);
			zombiePopulationRenderer.outlineRect((float)(mPCell.cx * 300), (float)(mPCell.cy * 300), 300.0F, 300.0F, 1.0F, 1.0F, 1.0F, 0.25F);
			if (this.isRespawnEnabled()) {
				float3 = Math.min(float2 - mPCell.lastRepopTime, this.RESPAWN_EVERY_HOURS) / this.RESPAWN_EVERY_HOURS;
				if (mPCell.lastRepopTime > float2) {
					float3 = 0.0F;
				}

				zombiePopulationRenderer.outlineRect((float)(mPCell.cx * 300 + 1), (float)(mPCell.cy * 300 + 1), 298.0F, 298.0F, 0.0F, 1.0F, 0.0F, float3 * float3);
			}
		}

		for (int1 = 0; int1 < this.loadedAreas.count; ++int1) {
			int int2 = int1 * 4;
			int int3 = this.loadedAreas.areas[int2++];
			int int4 = this.loadedAreas.areas[int2++];
			int int5 = this.loadedAreas.areas[int2++];
			int int6 = this.loadedAreas.areas[int2++];
			zombiePopulationRenderer.outlineRect((float)(int3 * 10), (float)(int4 * 10), (float)(int5 * 10), (float)(int6 * 10), 0.7F, 0.7F, 0.7F, 1.0F);
		}

		for (int1 = 0; int1 < this.repopEvents.size(); ++int1) {
			MPDebugInfo.MPRepopEvent mPRepopEvent = (MPDebugInfo.MPRepopEvent)this.repopEvents.get(int1);
			if (!(mPRepopEvent.worldAge + this.REPOP_DISPLAY_HOURS < float2)) {
				float3 = 1.0F - (float2 - mPRepopEvent.worldAge) / this.REPOP_DISPLAY_HOURS;
				float3 = Math.max(float3, 0.1F);
				zombiePopulationRenderer.outlineRect((float)(mPRepopEvent.wx * 10), (float)(mPRepopEvent.wy * 10), 50.0F, 50.0F, 0.0F, 0.0F, 1.0F, float3);
			}
		}

		for (int1 = 0; int1 < IsoWorld.instance.CurrentCell.getZombieList().size(); ++int1) {
			IsoZombie zombie = (IsoZombie)IsoWorld.instance.CurrentCell.getZombieList().get(int1);
			float3 = 1.0F;
			float float4 = 1.0F;
			float float5 = 0.0F;
			zombiePopulationRenderer.renderZombie(zombie.x, zombie.y, float3, float4, float5);
		}

		zombiePopulationRenderer.renderZombie(IsoPlayer.instance.x, IsoPlayer.instance.y, 0.0F, 0.5F, 0.0F);
		if (float1 > 0.25F) {
			for (int1 = 0; int1 < this.loadedCells.size(); ++int1) {
				mPCell = (MPDebugInfo.MPCell)this.loadedCells.get(int1);
				zombiePopulationRenderer.renderCellInfo(mPCell.cx, mPCell.cy, mPCell.currentPopulation, mPCell.desiredPopulation, mPCell.lastRepopTime + this.RESPAWN_EVERY_HOURS - float2);
			}
		}
	}

	private static final class MPRepopEvent {
		public int wx;
		public int wy;
		public float worldAge;

		private MPRepopEvent() {
		}

		public MPDebugInfo.MPRepopEvent init(int int1, int int2, float float1) {
			this.wx = int1;
			this.wy = int2;
			this.worldAge = float1;
			return this;
		}

		MPRepopEvent(Object object) {
			this();
		}
	}

	private static final class MPCell {
		public short cx;
		public short cy;
		public short currentPopulation;
		public short desiredPopulation;
		public float lastRepopTime;

		private MPCell() {
		}

		MPDebugInfo.MPCell init(int int1, int int2, int int3, int int4, float float1) {
			this.cx = (short)int1;
			this.cy = (short)int2;
			this.currentPopulation = (short)int3;
			this.desiredPopulation = (short)int4;
			this.lastRepopTime = float1;
			return this;
		}

		MPCell(Object object) {
			this();
		}
	}
}
