package zombie.popman;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.hash.TIntHashSet;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import se.krka.kahlua.vm.KahluaTable;
import zombie.DebugFileWatcher;
import zombie.GameTime;
import zombie.MapCollisionData;
import zombie.PersistentOutfits;
import zombie.PredicatedFileWatcher;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.ai.states.PathFindState;
import zombie.ai.states.WalkTowardState;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.gameStates.ChooseGameInfo;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.PolygonalMap2;


public final class ZombiePopulationManager {
	public static final ZombiePopulationManager instance = new ZombiePopulationManager();
	protected static final int SQUARES_PER_CHUNK = 10;
	protected static final int CHUNKS_PER_CELL = 30;
	protected static final int SQUARES_PER_CELL = 300;
	protected static final byte OLD_ZOMBIE_CRAWLER_CAN_WALK = 1;
	protected static final byte OLD_ZOMBIE_FAKE_DEAD = 2;
	protected static final byte OLD_ZOMBIE_CRAWLER = 3;
	protected static final byte OLD_ZOMBIE_WALKER = 4;
	protected static final int ZOMBIE_STATE_INITIALIZED = 1;
	protected static final int ZOMBIE_STATE_CRAWLING = 2;
	protected static final int ZOMBIE_STATE_CAN_WALK = 4;
	protected static final int ZOMBIE_STATE_FAKE_DEAD = 8;
	protected static final int ZOMBIE_STATE_CRAWL_UNDER_VEHICLE = 16;
	protected int minX;
	protected int minY;
	protected int width;
	protected int height;
	protected boolean bStopped;
	protected boolean bClient;
	private final DebugCommands dbgCommands = new DebugCommands();
	public static boolean bDebugLoggingEnabled = false;
	private final LoadedAreas loadedAreas = new LoadedAreas(false);
	private final LoadedAreas loadedServerCells = new LoadedAreas(true);
	private final PlayerSpawns playerSpawns = new PlayerSpawns();
	private short[] realZombieCount;
	private short[] realZombieCount2;
	private long realZombieUpdateTime = 0L;
	private final ArrayList saveRealZombieHack = new ArrayList();
	private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
	private final TIntHashSet newChunks = new TIntHashSet();
	private final ArrayList spawnOrigins = new ArrayList();
	public float[] radarXY;
	public int radarCount;
	public boolean radarRenderFlag;
	public boolean radarRequestFlag;
	private final ArrayList m_sittingDirections = new ArrayList();

	ZombiePopulationManager() {
		this.newChunks.setAutoCompactionFactor(0.0F);
	}

	private static native void n_init(boolean boolean1, boolean boolean2, int int1, int int2, int int3, int int4);

	private static native void n_config(float float1, float float2, float float3, int int1, float float4, float float5, float float6, float float7, int int2);

	private static native void n_setSpawnOrigins(int[] intArray);

	private static native void n_setOutfitNames(String[] stringArray);

	private static native void n_updateMain(float float1, double double1);

	private static native boolean n_hasDataForThread();

	private static native void n_updateThread();

	private static native boolean n_shouldWait();

	private static native void n_beginSaveRealZombies(int int1);

	private static native void n_saveRealZombies(int int1, ByteBuffer byteBuffer);

	private static native void n_save();

	private static native void n_stop();

	private static native void n_addZombie(float float1, float float2, float float3, byte byte1, int int1, int int2, int int3, int int4);

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
		if (bDebugLoggingEnabled && (Core.bDebug || GameServer.bServer && GameServer.bDebug)) {
			DebugLog.log("ZPOP: " + string);
		}
	}

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

		DebugFileWatcher.instance.add(new PredicatedFileWatcher(ZomboidFileSystem.instance.getMessagingDirSub("Trigger_Zombie.xml"), ZombiePopulationManager::onTriggeredZombieFile));
	}

	private static void onTriggeredZombieFile(String string) {
		DebugLog.General.println("ZombiePopulationManager.onTriggeredZombieFile(" + string + ">");
		ZombieTriggerXmlFile zombieTriggerXmlFile;
		try {
			zombieTriggerXmlFile = (ZombieTriggerXmlFile)PZXmlUtil.parse(ZombieTriggerXmlFile.class, string);
		} catch (PZXmlParserException pZXmlParserException) {
			System.err.println("ZombiePopulationManager.onTriggeredZombieFile> Exception thrown. " + pZXmlParserException);
			pZXmlParserException.printStackTrace();
			return;
		}

		if (zombieTriggerXmlFile.spawnHorde > 0) {
			processTriggerSpawnHorde(zombieTriggerXmlFile);
		}

		if (zombieTriggerXmlFile.setDebugLoggingEnabled && bDebugLoggingEnabled != zombieTriggerXmlFile.bDebugLoggingEnabled) {
			bDebugLoggingEnabled = zombieTriggerXmlFile.bDebugLoggingEnabled;
			DebugLog.General.println("  bDebugLoggingEnabled: " + bDebugLoggingEnabled);
		}
	}

	private static void processTriggerSpawnHorde(ZombieTriggerXmlFile zombieTriggerXmlFile) {
		DebugLog.General.println("  spawnHorde: " + zombieTriggerXmlFile.spawnHorde);
		if (IsoPlayer.getInstance() != null) {
			IsoPlayer player = IsoPlayer.getInstance();
			instance.createHordeFromTo((int)player.x, (int)player.y, (int)player.x, (int)player.y, zombieTriggerXmlFile.spawnHorde);
		}
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
			String[] stringArray = (String[])PersistentOutfits.instance.getOutfitNames().toArray(new String[0]);
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				stringArray[int1] = stringArray[int1].toLowerCase();
			}

			n_setOutfitNames(stringArray);
			TIntArrayList tIntArrayList = new TIntArrayList();
			Iterator iterator = this.spawnOrigins.iterator();
			while (iterator.hasNext()) {
				ChooseGameInfo.SpawnOrigin spawnOrigin = (ChooseGameInfo.SpawnOrigin)iterator.next();
				tIntArrayList.add(spawnOrigin.x);
				tIntArrayList.add(spawnOrigin.y);
				tIntArrayList.add(spawnOrigin.w);
				tIntArrayList.add(spawnOrigin.h);
			}

			n_setSpawnOrigins(tIntArrayList.toArray());
		}
	}

	public void onConfigReloaded() {
		SandboxOptions.ZombieConfig zombieConfig = SandboxOptions.instance.zombieConfig;
		n_config((float)zombieConfig.PopulationMultiplier.getValue(), (float)zombieConfig.PopulationStartMultiplier.getValue(), (float)zombieConfig.PopulationPeakMultiplier.getValue(), zombieConfig.PopulationPeakDay.getValue(), (float)zombieConfig.RespawnHours.getValue(), (float)zombieConfig.RespawnUnseenHours.getValue(), (float)zombieConfig.RespawnMultiplier.getValue() * 100.0F, (float)zombieConfig.RedistributeHours.getValue(), zombieConfig.FollowSoundDistance.getValue());
	}

	public void registerSpawnOrigin(int int1, int int2, int int3, int int4, KahluaTable kahluaTable) {
		if (int1 >= 0 && int2 >= 0 && int3 >= 0 && int4 >= 0) {
			this.spawnOrigins.add(new ChooseGameInfo.SpawnOrigin(int1, int2, int3, int4));
		}
	}

	public void playerSpawnedAt(int int1, int int2, int int3) {
		this.playerSpawns.addSpawn(int1, int2, int3);
	}

	public void addChunkToWorld(IsoChunk chunk) {
		if (!this.bClient) {
			if (chunk.isNewChunk()) {
				int int1 = chunk.wy << 16 | chunk.wx;
				this.newChunks.add(int1);
			}

			n_loadChunk(chunk.wx, chunk.wy, true);
		}
	}

	public void removeChunkFromWorld(IsoChunk chunk) {
		if (!this.bClient) {
			if (!this.bStopped) {
				n_loadChunk(chunk.wx, chunk.wy, false);
				int int1;
				for (int1 = 0; int1 < 8; ++int1) {
					for (int int2 = 0; int2 < 10; ++int2) {
						for (int int3 = 0; int3 < 10; ++int3) {
							IsoGridSquare square = chunk.getGridSquare(int3, int2, int1);
							if (square != null && !square.getMovingObjects().isEmpty()) {
								for (int int4 = 0; int4 < square.getMovingObjects().size(); ++int4) {
									IsoMovingObject movingObject = (IsoMovingObject)square.getMovingObjects().get(int4);
									if (movingObject instanceof IsoZombie) {
										IsoZombie zombie = (IsoZombie)movingObject;
										if ((!GameServer.bServer || !zombie.bIndoorZombie) && !zombie.isReanimatedPlayer()) {
											int int5 = this.getZombieState(zombie);
											if (int1 != 0 || square.getRoom() != null || zombie.getCurrentState() != WalkTowardState.instance() && zombie.getCurrentState() != PathFindState.instance()) {
												n_addZombie(zombie.x, zombie.y, zombie.z, (byte)zombie.dir.index(), zombie.getPersistentOutfitID(), int5, -1, -1);
											} else {
												n_addZombie(zombie.x, zombie.y, zombie.z, (byte)zombie.dir.index(), zombie.getPersistentOutfitID(), int5, zombie.getPathTargetX(), zombie.getPathTargetY());
											}
										}
									}
								}
							}
						}
					}
				}

				int1 = chunk.wy << 16 | chunk.wx;
				this.newChunks.remove(int1);
				if (GameServer.bServer) {
					MapCollisionData.instance.notifyThread();
				}
			}
		}
	}

	public void virtualizeZombie(IsoZombie zombie) {
		int int1 = this.getZombieState(zombie);
		n_addZombie(zombie.x, zombie.y, zombie.z, (byte)zombie.dir.index(), zombie.getPersistentOutfitID(), int1, zombie.getPathTargetX(), zombie.getPathTargetY());
		zombie.removeFromWorld();
		zombie.removeFromSquare();
	}

	private int getZombieState(IsoZombie zombie) {
		int int1 = 1;
		if (zombie.isCrawling()) {
			int1 |= 2;
		}

		if (zombie.isCanWalk()) {
			int1 |= 4;
		}

		if (zombie.isFakeDead()) {
			int1 |= 8;
		}

		if (zombie.isCanCrawlUnderVehicle()) {
			int1 |= 16;
		}

		return int1;
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

	public void addWorldSound(WorldSoundManager.WorldSound worldSound, boolean boolean1) {
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
					int int7 = this.byteBuffer.getInt();
					int int8 = this.byteBuffer.getInt();
					int int9 = this.byteBuffer.getInt();
					int int10 = this.byteBuffer.getInt();
					int int11 = (int)float1 / 10;
					int int12 = (int)float2 / 10;
					int int13 = int12 << 16 | int11;
					if (this.newChunks.contains(int13)) {
						IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)float1, (int)float2, (int)float3);
						if (square != null && square.roomID != -1) {
							continue;
						}
					}

					if (int9 != -1 && this.loadedAreas.isOnEdge((int)float1, (int)float2)) {
						int9 = -1;
						int10 = -1;
					}

					if (int9 == -1) {
						this.addZombieStanding(float1, float2, float3, directions, int7, int8);
						++int1;
					} else {
						this.addZombieMoving(float1, float2, float3, directions, int7, int8, int9, int10);
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

	private void addZombieStanding(float float1, float float2, float float3, IsoDirections directions, int int1, int int2) {
		IsoGridSquare square;
		label64: {
			square = IsoWorld.instance.CurrentCell.getGridSquare((int)float1, (int)float2, (int)float3);
			if (square != null) {
				if (square.SolidFloorCached) {
					if (square.SolidFloor) {
						break label64;
					}
				} else if (square.TreatAsSolidFloor()) {
					break label64;
				}
			}

			noise("real -> unloaded");
			n_addZombie(float1, float2, float3, (byte)directions.index(), int1, int2, -1, -1);
			return;
		}
		if (!Core.bLastStand && !this.playerSpawns.allowZombie(square)) {
			noise("removed zombie near player spawn " + (int)float1 + "," + (int)float2 + "," + (int)float3);
		} else {
			VirtualZombieManager.instance.choices.clear();
			IsoGridSquare square2 = null;
			if (!this.isCrawling(int2) && !this.isFakeDead(int2) && Rand.Next(3) == 0) {
				square2 = this.getSquareForSittingZombie(float1, float2, (int)float3);
			}

			if (square2 != null) {
				VirtualZombieManager.instance.choices.add(square2);
			} else {
				VirtualZombieManager.instance.choices.add(square);
			}

			IsoZombie zombie = VirtualZombieManager.instance.createRealZombieAlways(int1, directions.index(), false);
			if (zombie != null) {
				if (square2 != null) {
					this.sitAgainstWall(zombie, square2);
				} else {
					zombie.setX(float1);
					zombie.setY(float2);
				}

				if (this.isFakeDead(int2)) {
					zombie.setHealth(0.5F + Rand.Next(0.0F, 0.3F));
					zombie.sprite = zombie.legsSprite;
					zombie.setFakeDead(true);
				} else if (this.isCrawling(int2)) {
					zombie.setCrawler(true);
					zombie.setCanWalk(this.isCanWalk(int2));
					zombie.setOnFloor(true);
					zombie.setFallOnFront(true);
					zombie.walkVariant = "ZombieWalk";
					zombie.DoZombieStats();
				}

				if (this.isInitialized(int2)) {
					zombie.setCanCrawlUnderVehicle(this.isCanCrawlUnderVehicle(int2));
				} else {
					this.firstTimeLoaded(zombie, int2);
				}
			}
		}
	}

	private IsoGridSquare getSquareForSittingZombie(float float1, float float2, int int1) {
		byte byte1 = 3;
		for (int int2 = -byte1; int2 < byte1; ++int2) {
			for (int int3 = -byte1; int3 < byte1; ++int3) {
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)float1 + int2, (int)float2 + int3, int1);
				if (square != null && square.isFree(true) && square.getBuilding() == null) {
					int int4 = square.getWallType();
					if (int4 != 0 && !PolygonalMap2.instance.lineClearCollide(float1, float2, (float)square.x + 0.5F, (float)square.y + 0.5F, square.z, (IsoMovingObject)null, false, true)) {
						return square;
					}
				}
			}
		}

		return null;
	}

	public void sitAgainstWall(IsoZombie zombie, IsoGridSquare square) {
		float float1 = (float)square.x + 0.5F;
		float float2 = (float)square.y + 0.5F;
		zombie.setX(float1);
		zombie.setY(float2);
		zombie.setSitAgainstWall(true);
		int int1 = square.getWallType();
		if (int1 != 0) {
			this.m_sittingDirections.clear();
			if ((int1 & 1) != 0 && (int1 & 4) != 0) {
				this.m_sittingDirections.add(IsoDirections.SE);
			}

			if ((int1 & 1) != 0 && (int1 & 8) != 0) {
				this.m_sittingDirections.add(IsoDirections.SW);
			}

			if ((int1 & 2) != 0 && (int1 & 4) != 0) {
				this.m_sittingDirections.add(IsoDirections.NE);
			}

			if ((int1 & 2) != 0 && (int1 & 8) != 0) {
				this.m_sittingDirections.add(IsoDirections.NW);
			}

			if ((int1 & 1) != 0) {
				this.m_sittingDirections.add(IsoDirections.S);
			}

			if ((int1 & 2) != 0) {
				this.m_sittingDirections.add(IsoDirections.N);
			}

			if ((int1 & 4) != 0) {
				this.m_sittingDirections.add(IsoDirections.E);
			}

			if ((int1 & 8) != 0) {
				this.m_sittingDirections.add(IsoDirections.W);
			}

			IsoDirections directions = (IsoDirections)PZArrayUtil.pickRandom((List)this.m_sittingDirections);
			if (GameClient.bClient) {
				int int2 = (square.x & 1) + (square.y & 1);
				directions = (IsoDirections)this.m_sittingDirections.get(int2 % this.m_sittingDirections.size());
			}

			zombie.setDir(directions);
			zombie.setForwardDirection(directions.ToVector());
			if (zombie.getAnimationPlayer() != null) {
				zombie.getAnimationPlayer().SetForceDir(zombie.getForwardDirection());
			}
		}
	}

	private void addZombieMoving(float float1, float float2, float float3, IsoDirections directions, int int1, int int2, int int3, int int4) {
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)float1, (int)float2, (int)float3);
		if (square != null) {
			label53: {
				if (square.SolidFloorCached) {
					if (!square.SolidFloor) {
						break label53;
					}
				} else if (!square.TreatAsSolidFloor()) {
					break label53;
				}

				if (!Core.bLastStand && !this.playerSpawns.allowZombie(square)) {
					noise("removed zombie near player spawn " + (int)float1 + "," + (int)float2 + "," + (int)float3);
					return;
				}

				VirtualZombieManager.instance.choices.clear();
				VirtualZombieManager.instance.choices.add(square);
				IsoZombie zombie = VirtualZombieManager.instance.createRealZombieAlways(int1, directions.index(), false);
				if (zombie != null) {
					zombie.setX(float1);
					zombie.setY(float2);
					if (this.isCrawling(int2)) {
						zombie.setCrawler(true);
						zombie.setCanWalk(this.isCanWalk(int2));
						zombie.setOnFloor(true);
						zombie.setFallOnFront(true);
						zombie.walkVariant = "ZombieWalk";
						zombie.DoZombieStats();
					}

					if (this.isInitialized(int2)) {
						zombie.setCanCrawlUnderVehicle(this.isCanCrawlUnderVehicle(int2));
					} else {
						this.firstTimeLoaded(zombie, int2);
					}

					if (Math.abs((float)int3 - float1) > 1.0F || Math.abs((float)int4 - float2) > 1.0F) {
						zombie.AllowRepathDelay = -1.0F;
						zombie.pathToLocation(int3, int4, 0);
						return;
					}
				}

				return;
			}
		}

		noise("real -> virtual " + float1 + "," + float2);
		n_addZombie(float1, float2, float3, (byte)directions.index(), int1, int2, int3, int4);
	}

	private boolean isInitialized(int int1) {
		return (int1 & 1) != 0;
	}

	private boolean isCrawling(int int1) {
		return (int1 & 2) != 0;
	}

	private boolean isCanWalk(int int1) {
		return (int1 & 4) != 0;
	}

	private boolean isFakeDead(int int1) {
		return (int1 & 8) != 0;
	}

	private boolean isCanCrawlUnderVehicle(int int1) {
		return (int1 & 16) != 0;
	}

	private void firstTimeLoaded(IsoZombie zombie, int int1) {
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
		if (!this.bClient || GameClient.connection.accessLevel == 32) {
			this.dbgCommands.SpawnTimeToZero(int1, int2);
		}
	}

	public void dbgClearZombies(int int1, int int2) {
		if (!this.bClient || GameClient.connection.accessLevel == 32) {
			this.dbgCommands.ClearZombies(int1, int2);
		}
	}

	public void dbgSpawnNow(int int1, int int2) {
		if (!this.bClient || GameClient.connection.accessLevel == 32) {
			this.dbgCommands.SpawnNow(int1, int2);
		}
	}

	public void beginSaveRealZombies() {
		if (!this.bClient) {
			this.saveRealZombieHack.clear();
			ArrayList arrayList = IsoWorld.instance.CurrentCell.getZombieList();
			Iterator iterator = arrayList.iterator();
			while (true) {
				IsoZombie zombie;
				do {
					do {
						if (!iterator.hasNext()) {
							int int1 = this.saveRealZombieHack.size();
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
									this.byteBuffer.putInt(zombie2.getPersistentOutfitID());
									int int5 = this.getZombieState(zombie2);
									this.byteBuffer.putInt(int5);
									++int2;
									int int6 = this.byteBuffer.position() - int4;
									if (this.byteBuffer.position() + int6 > this.byteBuffer.capacity()) {
										break;
									}
								}
							}

							this.saveRealZombieHack.clear();
							return;
						}

						zombie = (IsoZombie)iterator.next();
					}			 while (zombie.isReanimatedPlayer());
				}		 while (GameServer.bServer && zombie.bIndoorZombie);

				this.saveRealZombieHack.add(zombie);
			}
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
			this.newChunks.clear();
			this.spawnOrigins.clear();
			this.radarXY = null;
			this.radarCount = 0;
			this.radarRenderFlag = false;
			this.radarRequestFlag = false;
		}
	}
}
