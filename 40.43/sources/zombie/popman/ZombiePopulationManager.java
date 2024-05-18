package zombie.popman;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import zombie.GameTime;
import zombie.MapCollisionData;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.WalkTowardState;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class ZombiePopulationManager {
	public static final ZombiePopulationManager instance = new ZombiePopulationManager();
	protected static final int SQUARES_PER_CHUNK = 10;
	protected static final int CHUNKS_PER_CELL = 30;
	protected static final int SQUARES_PER_CELL = 300;
	protected static final byte ZOMBIE_DEAD = 1;
	protected static final byte ZOMBIE_FAKE_DEAD = 2;
	protected static final byte ZOMBIE_CRAWLER = 3;
	protected static final byte ZOMBIE_WALKER = 4;
	protected int minX;
	protected int minY;
	protected int width;
	protected int height;
	protected boolean bStopped;
	protected boolean bClient;
	private final DebugCommands dbgCommands = new DebugCommands();
	private final LoadedAreas loadedAreas = new LoadedAreas(false);
	private final LoadedAreas loadedServerCells = new LoadedAreas(true);
	private PlayerSpawns playerSpawns = new PlayerSpawns();
	private short[] realZombieCount;
	private short[] realZombieCount2;
	private long realZombieUpdateTime = 0L;
	private final ArrayList saveRealZombieHack = new ArrayList();
	private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
	public float[] radarXY;
	public int radarCount;
	public boolean radarRenderFlag;
	public boolean radarRequestFlag;

	public static void init() {
		String string = "";
		if ("1".equals(System.getProperty("zomboid.debuglibs.popman"))) {
			DebugLog.log("***** Loading debug version of PZPopMan");
			string = "d";
		}

		if (System.getProperty("os.name").contains("OS X")) {
			System.loadLibrary("PZPopMan");
		} else if (System.getProperty("sun.arch.data.model").equals("64")) {
			System.loadLibrary("PZPopMan64" + string);
		} else {
			System.loadLibrary("PZPopMan32" + string);
		}
	}

	private static native void n_init(boolean boolean1, boolean boolean2, int int1, int int2, int int3, int int4);

	private static native void n_config(float float1, float float2, float float3, int int1, float float4, float float5, float float6, float float7, int int2);

	private static native void n_updateMain(float float1, double double1);

	private static native boolean n_hasDataForThread();

	private static native void n_updateThread();

	private static native boolean n_shouldWait();

	private static native void n_beginSaveRealZombies(int int1);

	private static native void n_saveRealZombies(int int1, ByteBuffer byteBuffer);

	private static native void n_save();

	private static native void n_stop();

	private static native void n_addZombie(float float1, float float2, float float3, byte byte1, short short1, byte byte2, int int1, int int2);

	private static native void n_aggroTarget(int int1, int int2, int int3);

	private static native void n_loadChunk(int int1, int int2, boolean boolean1);

	private static native void n_loadedAreas(int int1, int[] intArray, boolean boolean1);

	protected static native void n_realZombieCount(short short1, short[] shortArray);

	protected static native void n_spawnHorde(int int1, int int2, int int3, int int4, float float1, float float2, int int5);

	private static native void n_worldSound(int int1, int int2, int int3, int int4);

	private static native int n_getAddZombieCount();

	private static native int n_getAddZombieData(int int1, ByteBuffer byteBuffer);

	private static native boolean n_hasRadarData();

	private static native void n_requestRadarData();

	private static native int n_getRadarZombieData(float[] floatArray);

	private static void noise(String string) {
	}

	public void init(IsoMetaGrid metaGrid) {
		this.bClient = GameClient.bClient;
		if (!this.bClient) {
			this.minX = metaGrid.getMinX();
			this.minY = metaGrid.getMinY();
			this.width = metaGrid.getWidth();
			this.height = metaGrid.getHeight();
			this.bStopped = false;
			n_init(this.bClient, GameServer.bServer, this.minX, this.minY, this.width, this.height);
			this.onConfigReloaded();
		}
	}

	public void onConfigReloaded() {
		SandboxOptions.ZombieConfig zombieConfig = SandboxOptions.instance.zombieConfig;
		n_config((float)zombieConfig.PopulationMultiplier.getValue(), (float)zombieConfig.PopulationStartMultiplier.getValue(), (float)zombieConfig.PopulationPeakMultiplier.getValue(), zombieConfig.PopulationPeakDay.getValue(), (float)zombieConfig.RespawnHours.getValue(), (float)zombieConfig.RespawnUnseenHours.getValue(), (float)zombieConfig.RespawnMultiplier.getValue() * 100.0F, (float)zombieConfig.RedistributeHours.getValue(), zombieConfig.FollowSoundDistance.getValue());
	}

	public void playerSpawnedAt(int int1, int int2, int int3) {
		this.playerSpawns.addSpawn(int1, int2, int3);
	}

	public void addChunkToWorld(IsoChunk chunk) {
		if (!this.bClient) {
			n_loadChunk(chunk.wx, chunk.wy, true);
		}
	}

	public void removeChunkFromWorld(IsoChunk chunk) {
		if (!this.bClient) {
			if (!this.bStopped) {
				n_loadChunk(chunk.wx, chunk.wy, false);
				for (int int1 = 0; int1 < 8; ++int1) {
					for (int int2 = 0; int2 < 10; ++int2) {
						for (int int3 = 0; int3 < 10; ++int3) {
							IsoGridSquare square = chunk.getGridSquare(int3, int2, int1);
							if (square != null && !square.getMovingObjects().isEmpty()) {
								for (int int4 = 0; int4 < square.getMovingObjects().size(); ++int4) {
									IsoMovingObject movingObject = (IsoMovingObject)square.getMovingObjects().get(int4);
									if (movingObject instanceof IsoZombie) {
										IsoZombie zombie = (IsoZombie)movingObject;
										if ((!GameServer.bServer || !zombie.bIndoorZombie) && !zombie.isReanimatedPlayer()) {
											byte byte1 = 4;
											if (zombie.isFakeDead()) {
												byte1 = 2;
											} else if (zombie.bCrawling) {
												byte1 = 3;
											}

											if (int1 == 0 && square.getRoom() == null && (zombie.getCurrentState() == WalkTowardState.instance() || zombie.getCurrentState() == PathFindState.instance())) {
												n_addZombie(zombie.x, zombie.y, zombie.z, (byte)zombie.dir.index(), GameServer.bServer ? (short)zombie.getDescriptor().getID() : 0, byte1, zombie.getPathTargetX(), zombie.getPathTargetY());
											} else {
												n_addZombie(zombie.x, zombie.y, zombie.z, (byte)zombie.dir.index(), GameServer.bServer ? (short)zombie.getDescriptor().getID() : 0, byte1, -1, -1);
											}
										}
									}
								}
							}
						}
					}
				}

				if (GameServer.bServer) {
					MapCollisionData.instance.notifyThread();
				}
			}
		}
	}

	public void virtualizeZombie(IsoZombie zombie) {
		byte byte1 = 4;
		if (zombie.isFakeDead()) {
			byte1 = 2;
		} else if (zombie.bCrawling) {
			byte1 = 3;
		}

		n_addZombie(zombie.x, zombie.y, zombie.z, (byte)zombie.dir.index(), GameServer.bServer ? (short)zombie.getDescriptor().getID() : 0, byte1, zombie.getPathTargetX(), zombie.getPathTargetY());
		zombie.removeFromWorld();
		zombie.removeFromSquare();
	}

	public void setAggroTarget(int int1, int int2, int int3) {
		n_aggroTarget(int1, int2, int3);
	}

	public void createHordeFromTo(int int1, int int2, int int3, int int4, int int5) {
		n_spawnHorde(int1, int2, 0, 0, (float)int3, (float)int4, int5);
	}

	public void createHordeInAreaTo(int int1, int int2, int int3, int int4, int int5, int int6, int int7) {
		n_spawnHorde(int1, int2, int3, int4, (float)int5, (float)int6, int7);
	}

	public void addWorldSound(int int1, int int2, int int3, int int4) {
		if (!this.bClient) {
			n_worldSound(int1, int2, int3, int4);
		}
	}

	public void addWorldSound(WorldSoundManager.WorldSound worldSound) {
		if (!this.bClient) {
			if (worldSound.radius >= 50) {
				if (!worldSound.sourceIsZombie) {
					n_worldSound(worldSound.x, worldSound.y, worldSound.radius, worldSound.volume);
				}
			}
		}
	}

	private void updateRealZombieCount() {
		if (this.realZombieCount == null || this.realZombieCount.length != this.width * this.height) {
			this.realZombieCount = new short[this.width * this.height];
			this.realZombieCount2 = new short[this.width * this.height * 2];
		}

		Arrays.fill(this.realZombieCount, (short)0);
		ArrayList arrayList = IsoWorld.instance.CurrentCell.getZombieList();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			IsoZombie zombie = (IsoZombie)arrayList.get(int1);
			int int2 = (int)(zombie.x / 300.0F) - this.minX;
			int int3 = (int)(zombie.y / 300.0F) - this.minY;
			++this.realZombieCount[int2 + int3 * this.width];
		}

		short short1 = 0;
		for (int int4 = 0; int4 < this.width * this.height; ++int4) {
			if (this.realZombieCount[int4] > 0) {
				this.realZombieCount2[short1 * 2 + 0] = (short)int4;
				this.realZombieCount2[short1 * 2 + 1] = this.realZombieCount[int4];
				++short1;
			}
		}

		n_realZombieCount(short1, this.realZombieCount2);
	}

	public void updateMain() {
		if (!this.bClient) {
			long long1 = System.currentTimeMillis();
			n_updateMain(GameTime.getInstance().getMultiplier(), GameTime.getInstance().getWorldAgeHours());
			int int1 = 0;
			int int2 = 0;
			int int3 = n_getAddZombieCount();
			int int4 = 0;
			while (int4 < int3) {
				this.byteBuffer.clear();
				int int5 = n_getAddZombieData(int4, this.byteBuffer);
				int4 += int5;
				for (int int6 = 0; int6 < int5; ++int6) {
					float float1 = this.byteBuffer.getFloat();
					float float2 = this.byteBuffer.getFloat();
					float float3 = this.byteBuffer.getFloat();
					IsoDirections directions = IsoDirections.fromIndex(this.byteBuffer.get());
					short short1 = this.byteBuffer.getShort();
					byte byte1 = this.byteBuffer.get();
					int int7 = this.byteBuffer.getInt();
					int int8 = this.byteBuffer.getInt();
					if (GameServer.bServer && SharedDescriptors.getDescriptor(short1) == null) {
						short1 = (short)SharedDescriptors.pickRandomDescriptorID();
					}

					if (int7 == -1) {
						this.addZombieStanding(float1, float2, float3, directions, short1, byte1);
						++int1;
					} else {
						this.addZombieMoving(float1, float2, float3, directions, short1, byte1, int7, int8);
						++int2;
					}
				}
			}

			if (int1 > 0) {
				noise("unloaded -> real " + int3);
			}

			if (int2 > 0) {
				noise("virtual -> real " + int3);
			}

			if (this.radarRenderFlag && this.radarXY != null) {
				if (this.radarRequestFlag) {
					if (n_hasRadarData()) {
						this.radarCount = n_getRadarZombieData(this.radarXY);
						this.radarRenderFlag = false;
						this.radarRequestFlag = false;
					}
				} else {
					n_requestRadarData();
					this.radarRequestFlag = true;
				}
			}

			this.updateLoadedAreas();
			if (this.realZombieUpdateTime + 5000L < long1) {
				this.realZombieUpdateTime = long1;
				this.updateRealZombieCount();
			}

			if (GameServer.bServer) {
				MPDebugInfo.instance.serverUpdate();
			}

			boolean boolean1 = n_hasDataForThread();
			boolean boolean2 = MapCollisionData.instance.hasDataForThread();
			if (boolean1 || boolean2) {
				MapCollisionData.instance.notifyThread();
			}

			this.playerSpawns.update();
		}
	}

	private void addZombieStanding(float float1, float float2, float float3, IsoDirections directions, short short1, byte byte1) {
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)float1, (int)float2, (int)float3);
		if (square != null) {
			label45: {
				if (square.SolidFloorCached) {
					if (!square.SolidFloor) {
						break label45;
					}
				} else if (!square.TreatAsSolidFloor()) {
					break label45;
				}

				if (!this.playerSpawns.allowZombie(square)) {
					noise("removed zombie near player spawn " + (int)float1 + "," + (int)float2 + "," + (int)float3);
					return;
				}

				VirtualZombieManager.instance.choices.clear();
				VirtualZombieManager.instance.choices.add(square);
				IsoZombie zombie;
				if (GameServer.bServer) {
					zombie = VirtualZombieManager.instance.createRealZombieAlways(short1, directions.index(), false);
				} else {
					zombie = VirtualZombieManager.instance.createRealZombieAlways(directions.index(), false);
				}

				if (zombie != null) {
					zombie.setX(float1);
					zombie.setY(float2);
					if (byte1 == 2) {
						zombie.setHealth(0.5F + Rand.Next(0.0F, 0.3F));
						zombie.sprite = zombie.legsSprite;
						zombie.changeState(FakeDeadZombieState.instance());
					} else if (byte1 == 3) {
						zombie.bCrawling = true;
						zombie.setOnFloor(true);
						zombie.walkVariant = "ZombieWalk";
						zombie.DoZombieStats();
						return;
					}

					return;
				}

				return;
			}
		}

		noise("real -> unloaded");
		n_addZombie(float1, float2, float3, (byte)directions.index(), short1, byte1, -1, -1);
	}

	private void addZombieMoving(float float1, float float2, float float3, IsoDirections directions, short short1, byte byte1, int int1, int int2) {
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)float1, (int)float2, (int)float3);
		if (square != null) {
			label47: {
				if (square.SolidFloorCached) {
					if (!square.SolidFloor) {
						break label47;
					}
				} else if (!square.TreatAsSolidFloor()) {
					break label47;
				}

				VirtualZombieManager.instance.choices.clear();
				VirtualZombieManager.instance.choices.add(square);
				IsoZombie zombie;
				if (GameServer.bServer) {
					zombie = VirtualZombieManager.instance.createRealZombieAlways(short1, directions.index(), false);
				} else {
					zombie = VirtualZombieManager.instance.createRealZombieAlways(directions.index(), false);
				}

				if (zombie != null) {
					zombie.setX(float1);
					zombie.setY(float2);
					if (byte1 == 3) {
						zombie.bCrawling = true;
						zombie.setOnFloor(true);
						zombie.walkVariant = "ZombieWalk";
						zombie.DoZombieStats();
					}

					if (Math.abs((float)int1 - float1) > 1.0F || Math.abs((float)int2 - float2) > 1.0F) {
						zombie.AllowRepathDelay = -1.0F;
						zombie.pathToLocation(int1, int2, 0);
						return;
					}
				}

				return;
			}
		}

		noise("real -> virtual " + float1 + "," + float2);
		n_addZombie(float1, float2, float3, (byte)directions.index(), short1, byte1, int1, int2);
	}

	public void updateThread() {
		n_updateThread();
	}

	public boolean shouldWait() {
		synchronized (MapCollisionData.instance.renderLock) {
			return n_shouldWait();
		}
	}

	public void updateLoadedAreas() {
		if (this.loadedAreas.set()) {
			n_loadedAreas(this.loadedAreas.count, this.loadedAreas.areas, false);
		}

		if (GameServer.bServer && this.loadedServerCells.set()) {
			n_loadedAreas(this.loadedServerCells.count, this.loadedServerCells.areas, true);
		}
	}

	public void dbgSpawnTimeToZero(int int1, int int2) {
		if (!this.bClient || GameClient.accessLevel.equals("admin")) {
			this.dbgCommands.SpawnTimeToZero(int1, int2);
		}
	}

	public void dbgClearZombies(int int1, int int2) {
		if (!this.bClient || GameClient.accessLevel.equals("admin")) {
			this.dbgCommands.ClearZombies(int1, int2);
		}
	}

	public void dbgSpawnNow(int int1, int int2) {
		if (!this.bClient || GameClient.accessLevel.equals("admin")) {
			this.dbgCommands.SpawnNow(int1, int2);
		}
	}

	public void beginSaveRealZombies() {
		if (!this.bClient) {
			this.saveRealZombieHack.clear();
			ArrayList arrayList = IsoWorld.instance.CurrentCell.getZombieList();
			int int1;
			for (int1 = 0; int1 < arrayList.size(); ++int1) {
				IsoZombie zombie = (IsoZombie)arrayList.get(int1);
				if (!zombie.isReanimatedPlayer() && (!GameServer.bServer || !zombie.bIndoorZombie)) {
					this.saveRealZombieHack.add(zombie);
				}
			}

			int1 = this.saveRealZombieHack.size();
			n_beginSaveRealZombies(int1);
			int int2;
			for (int int3 = 0; int3 < int1; n_saveRealZombies(int2, this.byteBuffer)) {
				this.byteBuffer.clear();
				int2 = 0;
				while (int3 < int1) {
					int int4 = this.byteBuffer.position();
					IsoZombie zombie2 = (IsoZombie)this.saveRealZombieHack.get(int3++);
					this.byteBuffer.putFloat(zombie2.x);
					this.byteBuffer.putFloat(zombie2.y);
					this.byteBuffer.putFloat(zombie2.z);
					this.byteBuffer.put((byte)zombie2.dir.index());
					this.byteBuffer.putShort(GameServer.bServer ? (short)zombie2.getDescriptor().getID() : 0);
					byte byte1;
					if (zombie2.isFakeDead()) {
						byte1 = 2;
					} else if (zombie2.bCrawling) {
						byte1 = 3;
					} else {
						byte1 = 4;
					}

					this.byteBuffer.put(byte1);
					++int2;
					int int5 = this.byteBuffer.position() - int4;
					if (this.byteBuffer.position() + int5 > this.byteBuffer.capacity()) {
						break;
					}
				}
			}

			this.saveRealZombieHack.clear();
		}
	}

	public void endSaveRealZombies() {
		if (!this.bClient) {
			;
		}
	}

	public void save() {
		if (!this.bClient) {
			n_save();
		}
	}

	public void stop() {
		if (!this.bClient) {
			this.bStopped = true;
			n_stop();
			this.loadedAreas.clear();
			this.radarXY = null;
			this.radarCount = 0;
			this.radarRenderFlag = false;
			this.radarRequestFlag = false;
		}
	}
}
