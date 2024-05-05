package zombie.iso;

import gnu.trove.list.array.TIntArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.CRC32;
import zombie.ChunkMapFilenames;
import zombie.FliesSound;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.LoadGridsquarePerformanceWorkaround;
import zombie.LootRespawn;
import zombie.MapCollisionData;
import zombie.ReanimatedPlayers;
import zombie.SandboxOptions;
import zombie.SystemDisabler;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZombieSpawnRecorder;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.Lua.MapObjects;
import zombie.audio.ObjectAmbientEmitters;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.LoggerManager;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.physics.Bullet;
import zombie.core.physics.WorldSimulation;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.UdpConnection;
import zombie.core.stash.StashSystem;
import zombie.core.utils.BoundedQueue;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.erosion.ErosionData;
import zombie.erosion.ErosionMain;
import zombie.globalObjects.SGlobalObjects;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.RainManager;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.ChunkChecksum;
import zombie.network.ClientChunkRequest;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.MPStatistics;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.popman.ZombiePopulationManager;
import zombie.randomizedWorld.randomizedBuilding.RandomizedBuildingBase;
import zombie.randomizedWorld.randomizedVehicleStory.RandomizedVehicleStoryBase;
import zombie.randomizedWorld.randomizedVehicleStory.VehicleStorySpawnData;
import zombie.randomizedWorld.randomizedZoneStory.RandomizedZoneStoryBase;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.VehicleScript;
import zombie.util.StringUtils;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.CollideWithObstaclesPoly;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleType;
import zombie.vehicles.VehiclesDB2;


public final class IsoChunk {
	public static boolean bDoServerRequests = true;
	public int wx = 0;
	public int wy = 0;
	public final IsoGridSquare[][] squares;
	public FliesSound.ChunkData corpseData;
	public final NearestWalls.ChunkData nearestWalls = new NearestWalls.ChunkData();
	private ArrayList generatorsTouchingThisChunk;
	public int maxLevel = -1;
	public final ArrayList SoundList = new ArrayList();
	private int m_treeCount = 0;
	private int m_numberOfWaterTiles = 0;
	private IsoMetaGrid.Zone m_scavengeZone = null;
	private final TIntArrayList m_spawnedRooms = new TIntArrayList();
	public IsoChunk next;
	public final CollideWithObstaclesPoly.ChunkData collision = new CollideWithObstaclesPoly.ChunkData();
	public int m_adjacentChunkLoadedCounter = 0;
	public VehicleStorySpawnData m_vehicleStorySpawnData;
	public Object m_loadVehiclesObject = null;
	public final ObjectAmbientEmitters.ChunkData m_objectEmitterData = new ObjectAmbientEmitters.ChunkData();
	public IsoChunk.JobType jobType;
	public LotHeader lotheader;
	public final BoundedQueue FloorBloodSplats;
	public final ArrayList FloorBloodSplatsFade;
	private static final int MAX_BLOOD_SPLATS = 1000;
	private int nextSplatIndex;
	public static final byte[][] renderByIndex = new byte[][]{{1, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {1, 0, 0, 0, 0, 1, 0, 0, 0, 0}, {1, 0, 0, 1, 0, 0, 1, 0, 0, 0}, {1, 0, 0, 1, 0, 1, 0, 0, 1, 0}, {1, 0, 1, 0, 1, 0, 1, 0, 1, 0}, {1, 1, 0, 1, 1, 0, 1, 1, 0, 0}, {1, 1, 0, 1, 1, 0, 1, 1, 0, 1}, {1, 1, 1, 1, 0, 1, 1, 1, 1, 0}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 0}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};
	public final ArrayList refs;
	public boolean bLoaded;
	private boolean blam;
	private boolean addZombies;
	private boolean bFixed2x;
	public final boolean[] lightCheck;
	public final boolean[] bLightingNeverDone;
	public final ArrayList roomLights;
	public final ArrayList vehicles;
	public int lootRespawnHour;
	private long hashCodeObjects;
	public int ObjectsSyncCount;
	private static int AddVehicles_ForTest_vtype = 0;
	private static int AddVehicles_ForTest_vskin = 0;
	private static int AddVehicles_ForTest_vrot = 0;
	private static final ArrayList BaseVehicleCheckedVehicles = new ArrayList();
	protected boolean physicsCheck;
	private static final int MAX_SHAPES = 4;
	private final IsoChunk.PhysicsShapes[] shapes;
	private static final byte[] bshapes = new byte[4];
	private static final IsoChunk.ChunkGetter chunkGetter = new IsoChunk.ChunkGetter();
	private boolean loadedPhysics;
	public final Object vehiclesForAddToWorldLock;
	public ArrayList vehiclesForAddToWorld;
	public static final ConcurrentLinkedQueue loadGridSquare = new ConcurrentLinkedQueue();
	private static final int BLOCK_SIZE = 65536;
	private static ByteBuffer SliceBuffer = ByteBuffer.allocate(65536);
	private static ByteBuffer SliceBufferLoad = ByteBuffer.allocate(65536);
	public static final Object WriteLock = new Object();
	private static final ArrayList tempRoomDefs = new ArrayList();
	private static final ArrayList tempBuildings = new ArrayList();
	private static final ArrayList Locks = new ArrayList();
	private static final Stack FreeLocks = new Stack();
	private static final IsoChunk.SanityCheck sanityCheck = new IsoChunk.SanityCheck();
	private static final CRC32 crcLoad = new CRC32();
	private static final CRC32 crcSave = new CRC32();
	private static String prefix = "map_";
	private ErosionData.Chunk erosion;
	private static final HashMap Fix2xMap = new HashMap();
	public int randomID;
	public long revision;

	public void updateSounds() {
		synchronized (WorldSoundManager.instance.SoundList) {
			int int1 = this.SoundList.size();
			for (int int2 = 0; int2 < int1; ++int2) {
				WorldSoundManager.WorldSound worldSound = (WorldSoundManager.WorldSound)this.SoundList.get(int2);
				if (worldSound == null || worldSound.life <= 0) {
					this.SoundList.remove(int2);
					--int2;
					--int1;
				}
			}
		}
	}

	public IsoChunk(IsoCell cell) {
		this.jobType = IsoChunk.JobType.None;
		this.FloorBloodSplats = new BoundedQueue(1000);
		this.FloorBloodSplatsFade = new ArrayList();
		this.refs = new ArrayList();
		this.lightCheck = new boolean[4];
		this.bLightingNeverDone = new boolean[4];
		this.roomLights = new ArrayList();
		this.vehicles = new ArrayList();
		this.lootRespawnHour = -1;
		this.ObjectsSyncCount = 0;
		this.physicsCheck = false;
		this.shapes = new IsoChunk.PhysicsShapes[4];
		this.loadedPhysics = false;
		this.vehiclesForAddToWorldLock = new Object();
		this.vehiclesForAddToWorld = null;
		this.squares = new IsoGridSquare[8][100];
		for (int int1 = 0; int1 < 4; ++int1) {
			this.lightCheck[int1] = true;
			this.bLightingNeverDone[int1] = true;
		}

		MPStatistics.increaseRelevantChunk();
	}

	@Deprecated
	public long getHashCodeObjects() {
		this.recalcHashCodeObjects();
		return this.hashCodeObjects;
	}

	@Deprecated
	public void recalcHashCodeObjects() {
		long long1 = 0L;
		this.hashCodeObjects = long1;
	}

	@Deprecated
	public int hashCodeNoOverride() {
		return (int)this.hashCodeObjects;
	}

	public void addBloodSplat(float float1, float float2, float float3, int int1) {
		if (!(float1 < (float)(this.wx * 10)) && !(float1 >= (float)((this.wx + 1) * 10))) {
			if (!(float2 < (float)(this.wy * 10)) && !(float2 >= (float)((this.wy + 1) * 10))) {
				IsoGridSquare square = this.getGridSquare((int)(float1 - (float)(this.wx * 10)), (int)(float2 - (float)(this.wy * 10)), (int)float3);
				if (square != null && square.isSolidFloor()) {
					IsoFloorBloodSplat floorBloodSplat = new IsoFloorBloodSplat(float1 - (float)(this.wx * 10), float2 - (float)(this.wy * 10), float3, int1, (float)GameTime.getInstance().getWorldAgeHours());
					if (int1 < 8) {
						floorBloodSplat.index = ++this.nextSplatIndex;
						if (this.nextSplatIndex >= 10) {
							this.nextSplatIndex = 0;
						}
					}

					if (this.FloorBloodSplats.isFull()) {
						IsoFloorBloodSplat floorBloodSplat2 = (IsoFloorBloodSplat)this.FloorBloodSplats.removeFirst();
						floorBloodSplat2.fade = PerformanceSettings.getLockFPS() * 5;
						this.FloorBloodSplatsFade.add(floorBloodSplat2);
					}

					this.FloorBloodSplats.add(floorBloodSplat);
				}
			}
		}
	}

	public void AddCorpses(int int1, int int2) {
		if (!IsoWorld.getZombiesDisabled() && !"Tutorial".equals(Core.GameMode)) {
			IsoMetaChunk metaChunk = IsoWorld.instance.getMetaChunk(int1, int2);
			if (metaChunk != null) {
				float float1 = metaChunk.getZombieIntensity();
				float1 *= 0.1F;
				int int3 = 0;
				if (float1 < 1.0F) {
					if ((float)Rand.Next(100) < float1 * 100.0F) {
						int3 = 1;
					}
				} else {
					int3 = Rand.Next(0, (int)float1);
				}

				if (int3 > 0) {
					IsoGridSquare square = null;
					int int4 = 0;
					int int5;
					do {
						int int6 = Rand.Next(10);
						int5 = Rand.Next(10);
						square = this.getGridSquare(int6, int5, 0);
						++int4;
					}			 while (int4 < 100 && (square == null || !RandomizedBuildingBase.is2x2AreaClear(square)));

					if (int4 == 100) {
						return;
					}

					if (square != null) {
						byte byte1 = 14;
						if (Rand.Next(10) == 0) {
							byte1 = 50;
						}

						if (Rand.Next(40) == 0) {
							byte1 = 100;
						}

						for (int5 = 0; int5 < byte1; ++int5) {
							float float2 = (float)Rand.Next(3000) / 1000.0F;
							float float3 = (float)Rand.Next(3000) / 1000.0F;
							--float2;
							--float3;
							this.addBloodSplat((float)square.getX() + float2, (float)square.getY() + float3, (float)square.getZ(), Rand.Next(20));
						}

						boolean boolean1 = Rand.Next(15 - SandboxOptions.instance.TimeSinceApo.getValue()) == 0;
						VirtualZombieManager.instance.choices.clear();
						VirtualZombieManager.instance.choices.add(square);
						IsoZombie zombie = VirtualZombieManager.instance.createRealZombieAlways(Rand.Next(8), false);
						zombie.setX((float)square.x);
						zombie.setY((float)square.y);
						zombie.setFakeDead(false);
						zombie.setHealth(0.0F);
						zombie.upKillCount = false;
						if (!boolean1) {
							zombie.dressInRandomOutfit();
							for (int int7 = 0; int7 < 10; ++int7) {
								zombie.addHole((BloodBodyPartType)null);
								zombie.addBlood((BloodBodyPartType)null, false, true, false);
								zombie.addDirt((BloodBodyPartType)null, (Integer)null, false);
							}

							zombie.DoCorpseInventory();
						}

						zombie.setSkeleton(boolean1);
						if (boolean1) {
							zombie.getHumanVisual().setSkinTextureIndex(2);
						}

						IsoDeadBody deadBody = new IsoDeadBody(zombie, true);
						if (!boolean1 && Rand.Next(3) == 0) {
							VirtualZombieManager.instance.createEatingZombies(deadBody, Rand.Next(1, 4));
						} else if (!boolean1 && Rand.Next(10) == 0) {
							deadBody.setFakeDead(true);
							if (Rand.Next(5) == 0) {
								deadBody.setCrawling(true);
							}
						}
					}
				}
			}
		}
	}

	public void AddBlood(int int1, int int2) {
		IsoMetaChunk metaChunk = IsoWorld.instance.getMetaChunk(int1, int2);
		if (metaChunk != null) {
			float float1 = metaChunk.getZombieIntensity();
			float1 *= 0.1F;
			if (Rand.Next(40) == 0) {
				float1 += 10.0F;
			}

			int int3 = 0;
			if (float1 < 1.0F) {
				if ((float)Rand.Next(100) < float1 * 100.0F) {
					int3 = 1;
				}
			} else {
				int3 = Rand.Next(0, (int)float1);
			}

			if (int3 > 0) {
				VirtualZombieManager.instance.AddBloodToMap(int3, this);
			}
		}
	}

	private void checkVehiclePos(BaseVehicle baseVehicle, IsoChunk chunk) {
		this.fixVehiclePos(baseVehicle, chunk);
		IsoDirections directions = baseVehicle.getDir();
		IsoGridSquare square;
		switch (directions) {
		case E: 
		
		case W: 
			if (baseVehicle.x - (float)(chunk.wx * 10) < baseVehicle.getScript().getExtents().x) {
				square = IsoWorld.instance.CurrentCell.getGridSquare((double)(baseVehicle.x - baseVehicle.getScript().getExtents().x), (double)baseVehicle.y, (double)baseVehicle.z);
				if (square == null) {
					return;
				}

				this.fixVehiclePos(baseVehicle, square.chunk);
			}

			if (baseVehicle.x - (float)(chunk.wx * 10) > 10.0F - baseVehicle.getScript().getExtents().x) {
				square = IsoWorld.instance.CurrentCell.getGridSquare((double)(baseVehicle.x + baseVehicle.getScript().getExtents().x), (double)baseVehicle.y, (double)baseVehicle.z);
				if (square == null) {
					return;
				}

				this.fixVehiclePos(baseVehicle, square.chunk);
			}

			break;
		
		case N: 
		
		case S: 
			if (baseVehicle.y - (float)(chunk.wy * 10) < baseVehicle.getScript().getExtents().z) {
				square = IsoWorld.instance.CurrentCell.getGridSquare((double)baseVehicle.x, (double)(baseVehicle.y - baseVehicle.getScript().getExtents().z), (double)baseVehicle.z);
				if (square == null) {
					return;
				}

				this.fixVehiclePos(baseVehicle, square.chunk);
			}

			if (baseVehicle.y - (float)(chunk.wy * 10) > 10.0F - baseVehicle.getScript().getExtents().z) {
				square = IsoWorld.instance.CurrentCell.getGridSquare((double)baseVehicle.x, (double)(baseVehicle.y + baseVehicle.getScript().getExtents().z), (double)baseVehicle.z);
				if (square == null) {
					return;
				}

				this.fixVehiclePos(baseVehicle, square.chunk);
			}

		
		}
	}

	private boolean fixVehiclePos(BaseVehicle baseVehicle, IsoChunk chunk) {
		BaseVehicle.MinMaxPosition minMaxPosition = baseVehicle.getMinMaxPosition();
		boolean boolean1 = false;
		IsoDirections directions = baseVehicle.getDir();
		for (int int1 = 0; int1 < chunk.vehicles.size(); ++int1) {
			BaseVehicle.MinMaxPosition minMaxPosition2 = ((BaseVehicle)chunk.vehicles.get(int1)).getMinMaxPosition();
			float float1;
			switch (directions) {
			case E: 
			
			case W: 
				float1 = minMaxPosition2.minX - minMaxPosition.maxX;
				if (float1 > 0.0F && minMaxPosition.minY < minMaxPosition2.maxY && minMaxPosition.maxY > minMaxPosition2.minY) {
					baseVehicle.x -= float1;
					minMaxPosition.minX -= float1;
					minMaxPosition.maxX -= float1;
					boolean1 = true;
				} else {
					float1 = minMaxPosition.minX - minMaxPosition2.maxX;
					if (float1 > 0.0F && minMaxPosition.minY < minMaxPosition2.maxY && minMaxPosition.maxY > minMaxPosition2.minY) {
						baseVehicle.x += float1;
						minMaxPosition.minX += float1;
						minMaxPosition.maxX += float1;
						boolean1 = true;
					}
				}

				break;
			
			case N: 
			
			case S: 
				float1 = minMaxPosition2.minY - minMaxPosition.maxY;
				if (float1 > 0.0F && minMaxPosition.minX < minMaxPosition2.maxX && minMaxPosition.maxX > minMaxPosition2.minX) {
					baseVehicle.y -= float1;
					minMaxPosition.minY -= float1;
					minMaxPosition.maxY -= float1;
					boolean1 = true;
				} else {
					float1 = minMaxPosition.minY - minMaxPosition2.maxY;
					if (float1 > 0.0F && minMaxPosition.minX < minMaxPosition2.maxX && minMaxPosition.maxX > minMaxPosition2.minX) {
						baseVehicle.y += float1;
						minMaxPosition.minY += float1;
						minMaxPosition.maxY += float1;
						boolean1 = true;
					}
				}

			
			}
		}

		return boolean1;
	}

	private boolean isGoodVehiclePos(BaseVehicle baseVehicle, IsoChunk chunk) {
		int int1 = ((int)baseVehicle.x - 4) / 10 - 1;
		int int2 = ((int)baseVehicle.y - 4) / 10 - 1;
		int int3 = (int)Math.ceil((double)((baseVehicle.x + 4.0F) / 10.0F)) + 1;
		int int4 = (int)Math.ceil((double)((baseVehicle.y + 4.0F) / 10.0F)) + 1;
		for (int int5 = int2; int5 < int4; ++int5) {
			for (int int6 = int1; int6 < int3; ++int6) {
				IsoChunk chunk2 = GameServer.bServer ? ServerMap.instance.getChunk(int6, int5) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int6 * 10, int5 * 10, 0);
				if (chunk2 != null) {
					for (int int7 = 0; int7 < chunk2.vehicles.size(); ++int7) {
						BaseVehicle baseVehicle2 = (BaseVehicle)chunk2.vehicles.get(int7);
						if ((int)baseVehicle2.z == (int)baseVehicle.z && baseVehicle.testCollisionWithVehicle(baseVehicle2)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	private void AddVehicles_ForTest(IsoMetaGrid.Zone zone) {
		int int1;
		for (int1 = zone.y - this.wy * 10 + 3; int1 < 0; int1 += 6) {
		}

		int int2;
		for (int2 = zone.x - this.wx * 10 + 2; int2 < 0; int2 += 5) {
		}

		for (int int3 = int1; int3 < 10 && this.wy * 10 + int3 < zone.y + zone.h; int3 += 6) {
			for (int int4 = int2; int4 < 10 && this.wx * 10 + int4 < zone.x + zone.w; int4 += 5) {
				IsoGridSquare square = this.getGridSquare(int4, int3, 0);
				if (square != null) {
					BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
					baseVehicle.setZone("Test");
					switch (AddVehicles_ForTest_vtype) {
					case 0: 
						baseVehicle.setScriptName("Base.CarNormal");
						break;
					
					case 1: 
						baseVehicle.setScriptName("Base.SmallCar");
						break;
					
					case 2: 
						baseVehicle.setScriptName("Base.SmallCar02");
						break;
					
					case 3: 
						baseVehicle.setScriptName("Base.CarTaxi");
						break;
					
					case 4: 
						baseVehicle.setScriptName("Base.CarTaxi2");
						break;
					
					case 5: 
						baseVehicle.setScriptName("Base.PickUpTruck");
						break;
					
					case 6: 
						baseVehicle.setScriptName("Base.PickUpVan");
						break;
					
					case 7: 
						baseVehicle.setScriptName("Base.CarStationWagon");
						break;
					
					case 8: 
						baseVehicle.setScriptName("Base.CarStationWagon2");
						break;
					
					case 9: 
						baseVehicle.setScriptName("Base.VanSeats");
						break;
					
					case 10: 
						baseVehicle.setScriptName("Base.Van");
						break;
					
					case 11: 
						baseVehicle.setScriptName("Base.StepVan");
						break;
					
					case 12: 
						baseVehicle.setScriptName("Base.PickUpTruck");
						break;
					
					case 13: 
						baseVehicle.setScriptName("Base.PickUpVan");
						break;
					
					case 14: 
						baseVehicle.setScriptName("Base.CarStationWagon");
						break;
					
					case 15: 
						baseVehicle.setScriptName("Base.CarStationWagon2");
						break;
					
					case 16: 
						baseVehicle.setScriptName("Base.VanSeats");
						break;
					
					case 17: 
						baseVehicle.setScriptName("Base.Van");
						break;
					
					case 18: 
						baseVehicle.setScriptName("Base.StepVan");
						break;
					
					case 19: 
						baseVehicle.setScriptName("Base.SUV");
						break;
					
					case 20: 
						baseVehicle.setScriptName("Base.OffRoad");
						break;
					
					case 21: 
						baseVehicle.setScriptName("Base.ModernCar");
						break;
					
					case 22: 
						baseVehicle.setScriptName("Base.ModernCar02");
						break;
					
					case 23: 
						baseVehicle.setScriptName("Base.CarLuxury");
						break;
					
					case 24: 
						baseVehicle.setScriptName("Base.SportsCar");
						break;
					
					case 25: 
						baseVehicle.setScriptName("Base.PickUpVanLightsPolice");
						break;
					
					case 26: 
						baseVehicle.setScriptName("Base.CarLightsPolice");
						break;
					
					case 27: 
						baseVehicle.setScriptName("Base.PickUpVanLightsFire");
						break;
					
					case 28: 
						baseVehicle.setScriptName("Base.PickUpTruckLightsFire");
						break;
					
					case 29: 
						baseVehicle.setScriptName("Base.PickUpVanLights");
						break;
					
					case 30: 
						baseVehicle.setScriptName("Base.PickUpTruckLights");
						break;
					
					case 31: 
						baseVehicle.setScriptName("Base.CarLights");
						break;
					
					case 32: 
						baseVehicle.setScriptName("Base.StepVanMail");
						break;
					
					case 33: 
						baseVehicle.setScriptName("Base.VanSpiffo");
						break;
					
					case 34: 
						baseVehicle.setScriptName("Base.VanAmbulance");
						break;
					
					case 35: 
						baseVehicle.setScriptName("Base.VanRadio");
						break;
					
					case 36: 
						baseVehicle.setScriptName("Base.PickupBurnt");
						break;
					
					case 37: 
						baseVehicle.setScriptName("Base.CarNormalBurnt");
						break;
					
					case 38: 
						baseVehicle.setScriptName("Base.TaxiBurnt");
						break;
					
					case 39: 
						baseVehicle.setScriptName("Base.ModernCarBurnt");
						break;
					
					case 40: 
						baseVehicle.setScriptName("Base.ModernCar02Burnt");
						break;
					
					case 41: 
						baseVehicle.setScriptName("Base.SportsCarBurnt");
						break;
					
					case 42: 
						baseVehicle.setScriptName("Base.SmallCarBurnt");
						break;
					
					case 43: 
						baseVehicle.setScriptName("Base.SmallCar02Burnt");
						break;
					
					case 44: 
						baseVehicle.setScriptName("Base.VanSeatsBurnt");
						break;
					
					case 45: 
						baseVehicle.setScriptName("Base.VanBurnt");
						break;
					
					case 46: 
						baseVehicle.setScriptName("Base.SUVBurnt");
						break;
					
					case 47: 
						baseVehicle.setScriptName("Base.OffRoadBurnt");
						break;
					
					case 48: 
						baseVehicle.setScriptName("Base.PickUpVanLightsBurnt");
						break;
					
					case 49: 
						baseVehicle.setScriptName("Base.AmbulanceBurnt");
						break;
					
					case 50: 
						baseVehicle.setScriptName("Base.VanRadioBurnt");
						break;
					
					case 51: 
						baseVehicle.setScriptName("Base.PickupSpecialBurnt");
						break;
					
					case 52: 
						baseVehicle.setScriptName("Base.NormalCarBurntPolice");
						break;
					
					case 53: 
						baseVehicle.setScriptName("Base.LuxuryCarBurnt");
						break;
					
					case 54: 
						baseVehicle.setScriptName("Base.PickUpVanBurnt");
						break;
					
					case 55: 
						baseVehicle.setScriptName("Base.PickUpTruckMccoy");
					
					}

					baseVehicle.setDir(IsoDirections.W);
					double double1 = (double)(baseVehicle.getDir().toAngle() + 3.1415927F) % 6.283185307179586;
					baseVehicle.savedRot.setAngleAxis(double1, 0.0, 1.0, 0.0);
					if (AddVehicles_ForTest_vrot == 1) {
						baseVehicle.savedRot.setAngleAxis(1.5707963267948966, 0.0, 0.0, 1.0);
					}

					if (AddVehicles_ForTest_vrot == 2) {
						baseVehicle.savedRot.setAngleAxis(3.141592653589793, 0.0, 0.0, 1.0);
					}

					baseVehicle.jniTransform.setRotation(baseVehicle.savedRot);
					baseVehicle.setX((float)square.x);
					baseVehicle.setY((float)square.y + 3.0F - 3.0F);
					baseVehicle.setZ((float)square.z);
					baseVehicle.jniTransform.origin.set(baseVehicle.getX() - WorldSimulation.instance.offsetX, baseVehicle.getZ(), baseVehicle.getY() - WorldSimulation.instance.offsetY);
					baseVehicle.setScript();
					this.checkVehiclePos(baseVehicle, this);
					this.vehicles.add(baseVehicle);
					baseVehicle.setSkinIndex(AddVehicles_ForTest_vskin);
					++AddVehicles_ForTest_vrot;
					if (AddVehicles_ForTest_vrot >= 2) {
						AddVehicles_ForTest_vrot = 0;
						++AddVehicles_ForTest_vskin;
						if (AddVehicles_ForTest_vskin >= baseVehicle.getSkinCount()) {
							AddVehicles_ForTest_vtype = (AddVehicles_ForTest_vtype + 1) % 56;
							AddVehicles_ForTest_vskin = 0;
						}
					}
				}
			}
		}
	}

	private void AddVehicles_OnZone(IsoMetaGrid.VehicleZone vehicleZone, String string) {
		IsoDirections directions = IsoDirections.N;
		byte byte1 = 3;
		byte byte2 = 4;
		if ((vehicleZone.w == byte2 || vehicleZone.w == byte2 + 1 || vehicleZone.w == byte2 + 2) && (vehicleZone.h <= byte1 || vehicleZone.h >= byte2 + 2)) {
			directions = IsoDirections.W;
		}

		byte2 = 5;
		if (vehicleZone.dir != IsoDirections.Max) {
			directions = vehicleZone.dir;
		}

		if (directions != IsoDirections.N && directions != IsoDirections.S) {
			byte2 = 3;
			byte1 = 5;
		}

		byte byte3 = 10;
		float float1;
		for (float1 = (float)(vehicleZone.y - this.wy * 10) + (float)byte2 / 2.0F; float1 < 0.0F; float1 += (float)byte2) {
		}

		float float2;
		for (float2 = (float)(vehicleZone.x - this.wx * 10) + (float)byte1 / 2.0F; float2 < 0.0F; float2 += (float)byte1) {
		}

		float float3 = float1;
		label203: while (true) {
			if (float3 < 10.0F && (float)(this.wy * 10) + float3 < (float)(vehicleZone.y + vehicleZone.h)) {
				float float4 = float2;
				while (true) {
					label196: {
						if (float4 < 10.0F && (float)(this.wx * 10) + float4 < (float)(vehicleZone.x + vehicleZone.w)) {
							IsoGridSquare square = this.getGridSquare((int)float4, (int)float3, 0);
							if (square == null) {
								break label196;
							}

							VehicleType vehicleType = VehicleType.getRandomVehicleType(string);
							if (vehicleType != null) {
								int int1 = vehicleType.spawnRate;
								switch (SandboxOptions.instance.CarSpawnRate.getValue()) {
								case 1: 
								
								case 4: 
								
								default: 
									break;
								
								case 2: 
									int1 = (int)Math.ceil((double)((float)int1 / 10.0F));
									break;
								
								case 3: 
									int1 = (int)Math.ceil((double)((float)int1 / 1.5F));
									break;
								
								case 5: 
									int1 *= 2;
								
								}

								if (SystemDisabler.doVehiclesEverywhere || DebugOptions.instance.VehicleSpawnEverywhere.getValue()) {
									int1 = 100;
								}

								if (Rand.Next(100) <= int1) {
									BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
									baseVehicle.setZone(string);
									baseVehicle.setVehicleType(vehicleType.name);
									if (vehicleType.isSpecialCar) {
										baseVehicle.setDoColor(false);
									}

									if (!this.RandomizeModel(baseVehicle, vehicleZone, string, vehicleType)) {
										System.out.println("Problem with Vehicle spawning: " + string + " " + vehicleType);
										return;
									}

									byte byte4 = 15;
									switch (SandboxOptions.instance.CarAlarm.getValue()) {
									case 1: 
										byte4 = -1;
										break;
									
									case 2: 
										byte4 = 3;
										break;
									
									case 3: 
										byte4 = 8;
									
									case 4: 
									
									default: 
										break;
									
									case 5: 
										byte4 = 25;
										break;
									
									case 6: 
										byte4 = 50;
									
									}

									if (Rand.Next(100) < byte4) {
										baseVehicle.setAlarmed(true);
									}

									if (vehicleZone.isFaceDirection()) {
										baseVehicle.setDir(directions);
									} else if (directions != IsoDirections.N && directions != IsoDirections.S) {
										baseVehicle.setDir(Rand.Next(2) == 0 ? IsoDirections.W : IsoDirections.E);
									} else {
										baseVehicle.setDir(Rand.Next(2) == 0 ? IsoDirections.N : IsoDirections.S);
									}

									float float5;
									for (float5 = baseVehicle.getDir().toAngle() + 3.1415927F; (double)float5 > 6.283185307179586; float5 = (float)((double)float5 - 6.283185307179586)) {
									}

									if (vehicleType.randomAngle) {
										float5 = Rand.Next(0.0F, 6.2831855F);
									}

									baseVehicle.savedRot.setAngleAxis(float5, 0.0F, 1.0F, 0.0F);
									baseVehicle.jniTransform.setRotation(baseVehicle.savedRot);
									float float6 = baseVehicle.getScript().getExtents().z;
									float float7 = 0.5F;
									float float8 = (float)square.x + 0.5F;
									float float9 = (float)square.y + 0.5F;
									if (directions == IsoDirections.N) {
										float8 = (float)square.x + (float)byte1 / 2.0F - (float)((int)((float)byte1 / 2.0F));
										float9 = (float)vehicleZone.y + float6 / 2.0F + float7;
										if (float9 >= (float)(square.y + 1) && (int)float3 < byte3 - 1 && this.getGridSquare((int)float4, (int)float3 + 1, 0) != null) {
											square = this.getGridSquare((int)float4, (int)float3 + 1, 0);
										}
									} else if (directions == IsoDirections.S) {
										float8 = (float)square.x + (float)byte1 / 2.0F - (float)((int)((float)byte1 / 2.0F));
										float9 = (float)(vehicleZone.y + vehicleZone.h) - float6 / 2.0F - float7;
										if (float9 < (float)square.y && (int)float3 > 0 && this.getGridSquare((int)float4, (int)float3 - 1, 0) != null) {
											square = this.getGridSquare((int)float4, (int)float3 - 1, 0);
										}
									} else if (directions == IsoDirections.W) {
										float8 = (float)vehicleZone.x + float6 / 2.0F + float7;
										float9 = (float)square.y + (float)byte2 / 2.0F - (float)((int)((float)byte2 / 2.0F));
										if (float8 >= (float)(square.x + 1) && (int)float4 < byte3 - 1 && this.getGridSquare((int)float4 + 1, (int)float3, 0) != null) {
											square = this.getGridSquare((int)float4 + 1, (int)float3, 0);
										}
									} else if (directions == IsoDirections.E) {
										float8 = (float)(vehicleZone.x + vehicleZone.w) - float6 / 2.0F - float7;
										float9 = (float)square.y + (float)byte2 / 2.0F - (float)((int)((float)byte2 / 2.0F));
										if (float8 < (float)square.x && (int)float4 > 0 && this.getGridSquare((int)float4 - 1, (int)float3, 0) != null) {
											square = this.getGridSquare((int)float4 - 1, (int)float3, 0);
										}
									}

									if (float8 < (float)square.x + 0.005F) {
										float8 = (float)square.x + 0.005F;
									}

									if (float8 > (float)(square.x + 1) - 0.005F) {
										float8 = (float)(square.x + 1) - 0.005F;
									}

									if (float9 < (float)square.y + 0.005F) {
										float9 = (float)square.y + 0.005F;
									}

									if (float9 > (float)(square.y + 1) - 0.005F) {
										float9 = (float)(square.y + 1) - 0.005F;
									}

									baseVehicle.setX(float8);
									baseVehicle.setY(float9);
									baseVehicle.setZ((float)square.z);
									baseVehicle.jniTransform.origin.set(baseVehicle.getX() - WorldSimulation.instance.offsetX, baseVehicle.getZ(), baseVehicle.getY() - WorldSimulation.instance.offsetY);
									float float10 = 100.0F - Math.min(vehicleType.baseVehicleQuality * 120.0F, 100.0F);
									baseVehicle.rust = (float)Rand.Next(100) < float10 ? 1.0F : 0.0F;
									if (doSpawnedVehiclesInInvalidPosition(baseVehicle) || GameClient.bClient) {
										this.vehicles.add(baseVehicle);
									}

									if (vehicleType.chanceOfOverCar > 0 && Rand.Next(100) <= vehicleType.chanceOfOverCar) {
										this.spawnVehicleRandomAngle(square, vehicleZone, string);
									}
								}

								break label196;
							}

							System.out.println("Can\'t find car: " + string);
						}

						float3 += (float)byte2;
						continue label203;
					}

					float4 += (float)byte1;
				}
			}

			return;
		}
	}

	private void AddVehicles_OnZonePolyline(IsoMetaGrid.VehicleZone vehicleZone, String string) {
		byte byte1 = 5;
		Vector2 vector2 = new Vector2();
		for (int int1 = 0; int1 < vehicleZone.points.size() - 2; int1 += 2) {
			int int2 = vehicleZone.points.getQuick(int1);
			int int3 = vehicleZone.points.getQuick(int1 + 1);
			int int4 = vehicleZone.points.getQuick((int1 + 2) % vehicleZone.points.size());
			int int5 = vehicleZone.points.getQuick((int1 + 3) % vehicleZone.points.size());
			vector2.set((float)(int4 - int2), (float)(int5 - int3));
			for (float float1 = (float)byte1 / 2.0F; float1 < vector2.getLength(); float1 += (float)byte1) {
				float float2 = (float)int2 + vector2.x / vector2.getLength() * float1;
				float float3 = (float)int3 + vector2.y / vector2.getLength() * float1;
				if (float2 >= (float)(this.wx * 10) && float3 >= (float)(this.wy * 10) && float2 < (float)((this.wx + 1) * 10) && float3 < (float)((this.wy + 1) * 10)) {
					VehicleType vehicleType = VehicleType.getRandomVehicleType(string);
					if (vehicleType == null) {
						System.out.println("Can\'t find car: " + string);
						return;
					}

					BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
					baseVehicle.setZone(string);
					baseVehicle.setVehicleType(vehicleType.name);
					if (vehicleType.isSpecialCar) {
						baseVehicle.setDoColor(false);
					}

					if (!this.RandomizeModel(baseVehicle, vehicleZone, string, vehicleType)) {
						System.out.println("Problem with Vehicle spawning: " + string + " " + vehicleType);
						return;
					}

					byte byte2 = 15;
					switch (SandboxOptions.instance.CarAlarm.getValue()) {
					case 1: 
						byte2 = -1;
						break;
					
					case 2: 
						byte2 = 3;
						break;
					
					case 3: 
						byte2 = 8;
					
					case 4: 
					
					default: 
						break;
					
					case 5: 
						byte2 = 25;
						break;
					
					case 6: 
						byte2 = 50;
					
					}

					if (Rand.Next(100) < byte2) {
						baseVehicle.setAlarmed(true);
					}

					float float4 = vector2.x;
					float float5 = vector2.y;
					vector2.normalize();
					baseVehicle.setDir(IsoDirections.fromAngle(vector2));
					float float6;
					for (float6 = vector2.getDirectionNeg() + 0.0F; (double)float6 > 6.283185307179586; float6 = (float)((double)float6 - 6.283185307179586)) {
					}

					vector2.x = float4;
					vector2.y = float5;
					if (vehicleType.randomAngle) {
						float6 = Rand.Next(0.0F, 6.2831855F);
					}

					baseVehicle.savedRot.setAngleAxis(float6, 0.0F, 1.0F, 0.0F);
					baseVehicle.jniTransform.setRotation(baseVehicle.savedRot);
					IsoGridSquare square = this.getGridSquare((int)float2 - this.wx * 10, (int)float3 - this.wy * 10, 0);
					if (float2 < (float)square.x + 0.005F) {
						float2 = (float)square.x + 0.005F;
					}

					if (float2 > (float)(square.x + 1) - 0.005F) {
						float2 = (float)(square.x + 1) - 0.005F;
					}

					if (float3 < (float)square.y + 0.005F) {
						float3 = (float)square.y + 0.005F;
					}

					if (float3 > (float)(square.y + 1) - 0.005F) {
						float3 = (float)(square.y + 1) - 0.005F;
					}

					baseVehicle.setX(float2);
					baseVehicle.setY(float3);
					baseVehicle.setZ((float)square.z);
					baseVehicle.jniTransform.origin.set(baseVehicle.getX() - WorldSimulation.instance.offsetX, baseVehicle.getZ(), baseVehicle.getY() - WorldSimulation.instance.offsetY);
					float float7 = 100.0F - Math.min(vehicleType.baseVehicleQuality * 120.0F, 100.0F);
					baseVehicle.rust = (float)Rand.Next(100) < float7 ? 1.0F : 0.0F;
					if (doSpawnedVehiclesInInvalidPosition(baseVehicle) || GameClient.bClient) {
						this.vehicles.add(baseVehicle);
					}
				}
			}
		}
	}

	public static void removeFromCheckedVehicles(BaseVehicle baseVehicle) {
		BaseVehicleCheckedVehicles.remove(baseVehicle);
	}

	public static void addFromCheckedVehicles(BaseVehicle baseVehicle) {
		if (!BaseVehicleCheckedVehicles.contains(baseVehicle)) {
			BaseVehicleCheckedVehicles.add(baseVehicle);
		}
	}

	public static void Reset() {
		BaseVehicleCheckedVehicles.clear();
	}

	public static boolean doSpawnedVehiclesInInvalidPosition(BaseVehicle baseVehicle) {
		IsoGridSquare square;
		if (GameServer.bServer) {
			square = ServerMap.instance.getGridSquare((int)baseVehicle.getX(), (int)baseVehicle.getY(), 0);
			if (square != null && square.roomID != -1) {
				return false;
			}
		} else if (!GameClient.bClient) {
			square = IsoWorld.instance.CurrentCell.getGridSquare((int)baseVehicle.getX(), (int)baseVehicle.getY(), 0);
			if (square != null && square.roomID != -1) {
				return false;
			}
		}

		boolean boolean1 = true;
		for (int int1 = 0; int1 < BaseVehicleCheckedVehicles.size(); ++int1) {
			if (((BaseVehicle)BaseVehicleCheckedVehicles.get(int1)).testCollisionWithVehicle(baseVehicle)) {
				boolean1 = false;
			}
		}

		if (boolean1) {
			addFromCheckedVehicles(baseVehicle);
		}

		return boolean1;
	}

	private void spawnVehicleRandomAngle(IsoGridSquare square, IsoMetaGrid.Zone zone, String string) {
		boolean boolean1 = true;
		byte byte1 = 3;
		byte byte2 = 4;
		if ((zone.w == byte2 || zone.w == byte2 + 1 || zone.w == byte2 + 2) && (zone.h <= byte1 || zone.h >= byte2 + 2)) {
			boolean1 = false;
		}

		byte2 = 5;
		if (!boolean1) {
			byte2 = 3;
			byte1 = 5;
		}

		VehicleType vehicleType = VehicleType.getRandomVehicleType(string);
		if (vehicleType == null) {
			System.out.println("Can\'t find car: " + string);
		} else {
			BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
			baseVehicle.setZone(string);
			if (this.RandomizeModel(baseVehicle, zone, string, vehicleType)) {
				if (boolean1) {
					baseVehicle.setDir(Rand.Next(2) == 0 ? IsoDirections.N : IsoDirections.S);
				} else {
					baseVehicle.setDir(Rand.Next(2) == 0 ? IsoDirections.W : IsoDirections.E);
				}

				float float1 = Rand.Next(0.0F, 6.2831855F);
				baseVehicle.savedRot.setAngleAxis(float1, 0.0F, 1.0F, 0.0F);
				baseVehicle.jniTransform.setRotation(baseVehicle.savedRot);
				if (boolean1) {
					baseVehicle.setX((float)square.x + (float)byte1 / 2.0F - (float)((int)((float)byte1 / 2.0F)));
					baseVehicle.setY((float)square.y);
				} else {
					baseVehicle.setX((float)square.x);
					baseVehicle.setY((float)square.y + (float)byte2 / 2.0F - (float)((int)((float)byte2 / 2.0F)));
				}

				baseVehicle.setZ((float)square.z);
				baseVehicle.jniTransform.origin.set(baseVehicle.getX() - WorldSimulation.instance.offsetX, baseVehicle.getZ(), baseVehicle.getY() - WorldSimulation.instance.offsetY);
				if (doSpawnedVehiclesInInvalidPosition(baseVehicle) || GameClient.bClient) {
					this.vehicles.add(baseVehicle);
				}
			}
		}
	}

	public boolean RandomizeModel(BaseVehicle baseVehicle, IsoMetaGrid.Zone zone, String string, VehicleType vehicleType) {
		if (vehicleType.vehiclesDefinition.isEmpty()) {
			System.out.println("no vehicle definition found for " + string);
			return false;
		} else {
			float float1 = Rand.Next(0.0F, 100.0F);
			float float2 = 0.0F;
			VehicleType.VehicleTypeDefinition vehicleTypeDefinition = null;
			for (int int1 = 0; int1 < vehicleType.vehiclesDefinition.size(); ++int1) {
				vehicleTypeDefinition = (VehicleType.VehicleTypeDefinition)vehicleType.vehiclesDefinition.get(int1);
				float2 += vehicleTypeDefinition.spawnChance;
				if (float1 < float2) {
					break;
				}
			}

			String string2 = vehicleTypeDefinition.vehicleType;
			VehicleScript vehicleScript = ScriptManager.instance.getVehicle(string2);
			if (vehicleScript == null) {
				DebugLog.log("no such vehicle script \"" + string2 + "\" in IsoChunk.RandomizeModel");
				return false;
			} else {
				int int2 = vehicleTypeDefinition.index;
				baseVehicle.setScriptName(string2);
				baseVehicle.setScript();
				try {
					if (int2 > -1) {
						baseVehicle.setSkinIndex(int2);
					} else {
						baseVehicle.setSkinIndex(Rand.Next(baseVehicle.getSkinCount()));
					}

					return true;
				} catch (Exception exception) {
					DebugLog.log("problem with " + baseVehicle.getScriptName());
					exception.printStackTrace();
					return false;
				}
			}
		}
	}

	private void AddVehicles_TrafficJam_W(IsoMetaGrid.Zone zone, String string) {
		int int1;
		for (int1 = zone.y - this.wy * 10 + 1; int1 < 0; int1 += 3) {
		}

		int int2;
		for (int2 = zone.x - this.wx * 10 + 3; int2 < 0; int2 += 6) {
		}

		for (int int3 = int1; int3 < 10 && this.wy * 10 + int3 < zone.y + zone.h; int3 += 3 + Rand.Next(1)) {
			for (int int4 = int2; int4 < 10 && this.wx * 10 + int4 < zone.x + zone.w; int4 += 6 + Rand.Next(1)) {
				IsoGridSquare square = this.getGridSquare(int4, int3, 0);
				if (square != null) {
					VehicleType vehicleType = VehicleType.getRandomVehicleType(string);
					if (vehicleType == null) {
						System.out.println("Can\'t find car: " + string);
						break;
					}

					byte byte1 = 80;
					if (SystemDisabler.doVehiclesEverywhere || DebugOptions.instance.VehicleSpawnEverywhere.getValue()) {
						byte1 = 100;
					}

					if (Rand.Next(100) <= byte1) {
						BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
						baseVehicle.setZone("TrafficJam");
						baseVehicle.setVehicleType(vehicleType.name);
						if (!this.RandomizeModel(baseVehicle, zone, string, vehicleType)) {
							return;
						}

						baseVehicle.setScript();
						baseVehicle.setX((float)square.x + Rand.Next(0.0F, 1.0F));
						baseVehicle.setY((float)square.y + Rand.Next(0.0F, 1.0F));
						baseVehicle.setZ((float)square.z);
						baseVehicle.jniTransform.origin.set(baseVehicle.getX() - WorldSimulation.instance.offsetX, baseVehicle.getZ(), baseVehicle.getY() - WorldSimulation.instance.offsetY);
						if (this.isGoodVehiclePos(baseVehicle, this)) {
							baseVehicle.setSkinIndex(Rand.Next(baseVehicle.getSkinCount() - 1));
							baseVehicle.setDir(IsoDirections.W);
							float float1 = (float)Math.abs(zone.x + zone.w - square.x);
							float1 /= 20.0F;
							float1 = Math.min(2.0F, float1);
							float float2;
							for (float2 = baseVehicle.getDir().toAngle() + 3.1415927F - 0.25F + Rand.Next(0.0F, float1); (double)float2 > 6.283185307179586; float2 = (float)((double)float2 - 6.283185307179586)) {
							}

							baseVehicle.savedRot.setAngleAxis(float2, 0.0F, 1.0F, 0.0F);
							baseVehicle.jniTransform.setRotation(baseVehicle.savedRot);
							if (doSpawnedVehiclesInInvalidPosition(baseVehicle) || GameClient.bClient) {
								this.vehicles.add(baseVehicle);
							}
						}
					}
				}
			}
		}
	}

	private void AddVehicles_TrafficJam_E(IsoMetaGrid.Zone zone, String string) {
		int int1;
		for (int1 = zone.y - this.wy * 10 + 1; int1 < 0; int1 += 3) {
		}

		int int2;
		for (int2 = zone.x - this.wx * 10 + 3; int2 < 0; int2 += 6) {
		}

		for (int int3 = int1; int3 < 10 && this.wy * 10 + int3 < zone.y + zone.h; int3 += 3 + Rand.Next(1)) {
			for (int int4 = int2; int4 < 10 && this.wx * 10 + int4 < zone.x + zone.w; int4 += 6 + Rand.Next(1)) {
				IsoGridSquare square = this.getGridSquare(int4, int3, 0);
				if (square != null) {
					VehicleType vehicleType = VehicleType.getRandomVehicleType(string);
					if (vehicleType == null) {
						System.out.println("Can\'t find car: " + string);
						break;
					}

					byte byte1 = 80;
					if (SystemDisabler.doVehiclesEverywhere || DebugOptions.instance.VehicleSpawnEverywhere.getValue()) {
						byte1 = 100;
					}

					if (Rand.Next(100) <= byte1) {
						BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
						baseVehicle.setZone("TrafficJam");
						baseVehicle.setVehicleType(vehicleType.name);
						if (!this.RandomizeModel(baseVehicle, zone, string, vehicleType)) {
							return;
						}

						baseVehicle.setScript();
						baseVehicle.setX((float)square.x + Rand.Next(0.0F, 1.0F));
						baseVehicle.setY((float)square.y + Rand.Next(0.0F, 1.0F));
						baseVehicle.setZ((float)square.z);
						baseVehicle.jniTransform.origin.set(baseVehicle.getX() - WorldSimulation.instance.offsetX, baseVehicle.getZ(), baseVehicle.getY() - WorldSimulation.instance.offsetY);
						if (this.isGoodVehiclePos(baseVehicle, this)) {
							baseVehicle.setSkinIndex(Rand.Next(baseVehicle.getSkinCount() - 1));
							baseVehicle.setDir(IsoDirections.E);
							float float1 = (float)Math.abs(zone.x + zone.w - square.x - zone.w);
							float1 /= 20.0F;
							float1 = Math.min(2.0F, float1);
							float float2;
							for (float2 = baseVehicle.getDir().toAngle() + 3.1415927F - 0.25F + Rand.Next(0.0F, float1); (double)float2 > 6.283185307179586; float2 = (float)((double)float2 - 6.283185307179586)) {
							}

							baseVehicle.savedRot.setAngleAxis(float2, 0.0F, 1.0F, 0.0F);
							baseVehicle.jniTransform.setRotation(baseVehicle.savedRot);
							if (doSpawnedVehiclesInInvalidPosition(baseVehicle) || GameClient.bClient) {
								this.vehicles.add(baseVehicle);
							}
						}
					}
				}
			}
		}
	}

	private void AddVehicles_TrafficJam_S(IsoMetaGrid.Zone zone, String string) {
		int int1;
		for (int1 = zone.y - this.wy * 10 + 3; int1 < 0; int1 += 6) {
		}

		int int2;
		for (int2 = zone.x - this.wx * 10 + 1; int2 < 0; int2 += 3) {
		}

		for (int int3 = int1; int3 < 10 && this.wy * 10 + int3 < zone.y + zone.h; int3 += 6 + Rand.Next(-1, 1)) {
			for (int int4 = int2; int4 < 10 && this.wx * 10 + int4 < zone.x + zone.w; int4 += 3 + Rand.Next(1)) {
				IsoGridSquare square = this.getGridSquare(int4, int3, 0);
				if (square != null) {
					VehicleType vehicleType = VehicleType.getRandomVehicleType(string);
					if (vehicleType == null) {
						System.out.println("Can\'t find car: " + string);
						break;
					}

					byte byte1 = 80;
					if (SystemDisabler.doVehiclesEverywhere || DebugOptions.instance.VehicleSpawnEverywhere.getValue()) {
						byte1 = 100;
					}

					if (Rand.Next(100) <= byte1) {
						BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
						baseVehicle.setZone("TrafficJam");
						baseVehicle.setVehicleType(vehicleType.name);
						if (!this.RandomizeModel(baseVehicle, zone, string, vehicleType)) {
							return;
						}

						baseVehicle.setScript();
						baseVehicle.setX((float)square.x + Rand.Next(0.0F, 1.0F));
						baseVehicle.setY((float)square.y + Rand.Next(0.0F, 1.0F));
						baseVehicle.setZ((float)square.z);
						baseVehicle.jniTransform.origin.set(baseVehicle.getX() - WorldSimulation.instance.offsetX, baseVehicle.getZ(), baseVehicle.getY() - WorldSimulation.instance.offsetY);
						if (this.isGoodVehiclePos(baseVehicle, this)) {
							baseVehicle.setSkinIndex(Rand.Next(baseVehicle.getSkinCount() - 1));
							baseVehicle.setDir(IsoDirections.S);
							float float1 = (float)Math.abs(zone.y + zone.h - square.y - zone.h);
							float1 /= 20.0F;
							float1 = Math.min(2.0F, float1);
							float float2;
							for (float2 = baseVehicle.getDir().toAngle() + 3.1415927F - 0.25F + Rand.Next(0.0F, float1); (double)float2 > 6.283185307179586; float2 = (float)((double)float2 - 6.283185307179586)) {
							}

							baseVehicle.savedRot.setAngleAxis(float2, 0.0F, 1.0F, 0.0F);
							baseVehicle.jniTransform.setRotation(baseVehicle.savedRot);
							if (doSpawnedVehiclesInInvalidPosition(baseVehicle) || GameClient.bClient) {
								this.vehicles.add(baseVehicle);
							}
						}
					}
				}
			}
		}
	}

	private void AddVehicles_TrafficJam_N(IsoMetaGrid.Zone zone, String string) {
		int int1;
		for (int1 = zone.y - this.wy * 10 + 3; int1 < 0; int1 += 6) {
		}

		int int2;
		for (int2 = zone.x - this.wx * 10 + 1; int2 < 0; int2 += 3) {
		}

		for (int int3 = int1; int3 < 10 && this.wy * 10 + int3 < zone.y + zone.h; int3 += 6 + Rand.Next(-1, 1)) {
			for (int int4 = int2; int4 < 10 && this.wx * 10 + int4 < zone.x + zone.w; int4 += 3 + Rand.Next(1)) {
				IsoGridSquare square = this.getGridSquare(int4, int3, 0);
				if (square != null) {
					VehicleType vehicleType = VehicleType.getRandomVehicleType(string);
					if (vehicleType == null) {
						System.out.println("Can\'t find car: " + string);
						break;
					}

					byte byte1 = 80;
					if (SystemDisabler.doVehiclesEverywhere || DebugOptions.instance.VehicleSpawnEverywhere.getValue()) {
						byte1 = 100;
					}

					if (Rand.Next(100) <= byte1) {
						BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
						baseVehicle.setZone("TrafficJam");
						baseVehicle.setVehicleType(vehicleType.name);
						if (!this.RandomizeModel(baseVehicle, zone, string, vehicleType)) {
							return;
						}

						baseVehicle.setScript();
						baseVehicle.setX((float)square.x + Rand.Next(0.0F, 1.0F));
						baseVehicle.setY((float)square.y + Rand.Next(0.0F, 1.0F));
						baseVehicle.setZ((float)square.z);
						baseVehicle.jniTransform.origin.set(baseVehicle.getX() - WorldSimulation.instance.offsetX, baseVehicle.getZ(), baseVehicle.getY() - WorldSimulation.instance.offsetY);
						if (this.isGoodVehiclePos(baseVehicle, this)) {
							baseVehicle.setSkinIndex(Rand.Next(baseVehicle.getSkinCount() - 1));
							baseVehicle.setDir(IsoDirections.N);
							float float1 = (float)Math.abs(zone.y + zone.h - square.y);
							float1 /= 20.0F;
							float1 = Math.min(2.0F, float1);
							float float2;
							for (float2 = baseVehicle.getDir().toAngle() + 3.1415927F - 0.25F + Rand.Next(0.0F, float1); (double)float2 > 6.283185307179586; float2 = (float)((double)float2 - 6.283185307179586)) {
							}

							baseVehicle.savedRot.setAngleAxis(float2, 0.0F, 1.0F, 0.0F);
							baseVehicle.jniTransform.setRotation(baseVehicle.savedRot);
							if (doSpawnedVehiclesInInvalidPosition(baseVehicle) || GameClient.bClient) {
								this.vehicles.add(baseVehicle);
							}
						}
					}
				}
			}
		}
	}

	private void AddVehicles_TrafficJam_Polyline(IsoMetaGrid.Zone zone, String string) {
		Vector2 vector2 = new Vector2();
		Vector2 vector22 = new Vector2();
		float float1 = 0.0F;
		float float2 = zone.getPolylineLength();
		for (int int1 = 0; int1 < zone.points.size() - 2; int1 += 2) {
			int int2 = zone.points.getQuick(int1);
			int int3 = zone.points.getQuick(int1 + 1);
			int int4 = zone.points.getQuick(int1 + 2);
			int int5 = zone.points.getQuick(int1 + 3);
			vector2.set((float)(int4 - int2), (float)(int5 - int3));
			float float3 = vector2.getLength();
			vector22.set(vector2);
			vector22.tangent();
			vector22.normalize();
			float float4 = float1;
			float1 += float3;
			for (float float5 = 3.0F; float5 <= float3 - 3.0F; float5 += (float)(6 + Rand.Next(-1, 1))) {
				float float6 = PZMath.clamp(float5 + Rand.Next(-1.0F, 1.0F), 3.0F, float3 - 3.0F);
				float float7 = Rand.Next(-1.0F, 1.0F);
				float float8 = (float)int2 + vector2.x / float3 * float6 + vector22.x * float7;
				float float9 = (float)int3 + vector2.y / float3 * float6 + vector22.y * float7;
				this.TryAddVehicle_TrafficJam(zone, string, float8, float9, vector2, float4 + float6, float2);
				for (float float10 = 2.0F; float10 + 1.5F <= (float)zone.polylineWidth / 2.0F; float10 += 2.0F) {
					float7 = float10 + Rand.Next(-1.0F, 1.0F);
					if (float7 + 1.5F <= (float)zone.polylineWidth / 2.0F) {
						float6 = PZMath.clamp(float5 + Rand.Next(-2.0F, 2.0F), 3.0F, float3 - 3.0F);
						float8 = (float)int2 + vector2.x / float3 * float6 + vector22.x * float7;
						float9 = (float)int3 + vector2.y / float3 * float6 + vector22.y * float7;
						this.TryAddVehicle_TrafficJam(zone, string, float8, float9, vector2, float4 + float6, float2);
					}

					float7 = float10 + Rand.Next(-1.0F, 1.0F);
					if (float7 + 1.5F <= (float)zone.polylineWidth / 2.0F) {
						float6 = PZMath.clamp(float5 + Rand.Next(-2.0F, 2.0F), 3.0F, float3 - 3.0F);
						float8 = (float)int2 + vector2.x / float3 * float6 - vector22.x * float7;
						float9 = (float)int3 + vector2.y / float3 * float6 - vector22.y * float7;
						this.TryAddVehicle_TrafficJam(zone, string, float8, float9, vector2, float4 + float6, float2);
					}
				}
			}
		}
	}

	private void TryAddVehicle_TrafficJam(IsoMetaGrid.Zone zone, String string, float float1, float float2, Vector2 vector2, float float3, float float4) {
		if (!(float1 < (float)(this.wx * 10)) && !(float1 >= (float)((this.wx + 1) * 10)) && !(float2 < (float)(this.wy * 10)) && !(float2 >= (float)((this.wy + 1) * 10))) {
			IsoGridSquare square = this.getGridSquare((int)float1 - this.wx * 10, (int)float2 - this.wy * 10, 0);
			if (square != null) {
				VehicleType vehicleType = VehicleType.getRandomVehicleType(string + "W");
				if (vehicleType == null) {
					System.out.println("Can\'t find car: " + string);
				} else {
					byte byte1 = 80;
					if (SystemDisabler.doVehiclesEverywhere || DebugOptions.instance.VehicleSpawnEverywhere.getValue()) {
						byte1 = 100;
					}

					if (Rand.Next(100) <= byte1) {
						BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
						baseVehicle.setZone("TrafficJam");
						baseVehicle.setVehicleType(vehicleType.name);
						if (this.RandomizeModel(baseVehicle, zone, string, vehicleType)) {
							baseVehicle.setScript();
							baseVehicle.setX(float1);
							baseVehicle.setY(float2);
							baseVehicle.setZ((float)square.z);
							float float5 = vector2.x;
							float float6 = vector2.y;
							vector2.normalize();
							baseVehicle.setDir(IsoDirections.fromAngle(vector2));
							float float7 = vector2.getDirectionNeg();
							vector2.set(float5, float6);
							float float8 = 90.0F * (float3 / float4);
							for (float7 += Rand.Next(-float8, float8) * 0.017453292F; (double)float7 > 6.283185307179586; float7 = (float)((double)float7 - 6.283185307179586)) {
							}

							baseVehicle.savedRot.setAngleAxis(float7, 0.0F, 1.0F, 0.0F);
							baseVehicle.jniTransform.setRotation(baseVehicle.savedRot);
							baseVehicle.jniTransform.origin.set(baseVehicle.getX() - WorldSimulation.instance.offsetX, baseVehicle.getZ(), baseVehicle.getY() - WorldSimulation.instance.offsetY);
							if (this.isGoodVehiclePos(baseVehicle, this)) {
								baseVehicle.setSkinIndex(Rand.Next(baseVehicle.getSkinCount() - 1));
								if (doSpawnedVehiclesInInvalidPosition(baseVehicle)) {
									this.vehicles.add(baseVehicle);
								}
							}
						}
					}
				}
			}
		}
	}

	public void AddVehicles() {
		if (SandboxOptions.instance.CarSpawnRate.getValue() != 1) {
			if (VehicleType.vehicles.isEmpty()) {
				VehicleType.init();
			}

			if (!GameClient.bClient) {
				if (SandboxOptions.instance.EnableVehicles.getValue()) {
					if (!GameServer.bServer) {
						WorldSimulation.instance.create();
					}

					IsoMetaCell metaCell = IsoWorld.instance.getMetaGrid().getCellData(this.wx / 30, this.wy / 30);
					ArrayList arrayList = metaCell == null ? null : metaCell.vehicleZones;
					for (int int1 = 0; arrayList != null && int1 < arrayList.size(); ++int1) {
						IsoMetaGrid.VehicleZone vehicleZone = (IsoMetaGrid.VehicleZone)arrayList.get(int1);
						if (vehicleZone.x + vehicleZone.w >= this.wx * 10 && vehicleZone.y + vehicleZone.h >= this.wy * 10 && vehicleZone.x < (this.wx + 1) * 10 && vehicleZone.y < (this.wy + 1) * 10) {
							String string = vehicleZone.name;
							if (string.isEmpty()) {
								string = vehicleZone.type;
							}

							if (SandboxOptions.instance.TrafficJam.getValue()) {
								if (vehicleZone.isPolyline()) {
									if ("TrafficJam".equalsIgnoreCase(string)) {
										this.AddVehicles_TrafficJam_Polyline(vehicleZone, string);
										continue;
									}

									if ("RTrafficJam".equalsIgnoreCase(string) && Rand.Next(100) < 10) {
										this.AddVehicles_TrafficJam_Polyline(vehicleZone, string.replaceFirst("rtraffic", "traffic"));
										continue;
									}
								}

								if ("TrafficJamW".equalsIgnoreCase(string)) {
									this.AddVehicles_TrafficJam_W(vehicleZone, string);
								}

								if ("TrafficJamE".equalsIgnoreCase(string)) {
									this.AddVehicles_TrafficJam_E(vehicleZone, string);
								}

								if ("TrafficJamS".equalsIgnoreCase(string)) {
									this.AddVehicles_TrafficJam_S(vehicleZone, string);
								}

								if ("TrafficJamN".equalsIgnoreCase(string)) {
									this.AddVehicles_TrafficJam_N(vehicleZone, string);
								}

								if ("RTrafficJamW".equalsIgnoreCase(string) && Rand.Next(100) < 10) {
									this.AddVehicles_TrafficJam_W(vehicleZone, string.replaceFirst("rtraffic", "traffic"));
								}

								if ("RTrafficJamE".equalsIgnoreCase(string) && Rand.Next(100) < 10) {
									this.AddVehicles_TrafficJam_E(vehicleZone, string.replaceFirst("rtraffic", "traffic"));
								}

								if ("RTrafficJamS".equalsIgnoreCase(string) && Rand.Next(100) < 10) {
									this.AddVehicles_TrafficJam_S(vehicleZone, string.replaceFirst("rtraffic", "traffic"));
								}

								if ("RTrafficJamN".equalsIgnoreCase(string) && Rand.Next(100) < 10) {
									this.AddVehicles_TrafficJam_N(vehicleZone, string.replaceFirst("rtraffic", "traffic"));
								}
							}

							if (!StringUtils.containsIgnoreCase(string, "TrafficJam")) {
								if ("TestVehicles".equals(string)) {
									this.AddVehicles_ForTest(vehicleZone);
								} else if (VehicleType.hasTypeForZone(string)) {
									if (vehicleZone.isPolyline()) {
										this.AddVehicles_OnZonePolyline(vehicleZone, string);
									} else {
										this.AddVehicles_OnZone(vehicleZone, string);
									}
								}
							}
						}
					}

					IsoMetaChunk metaChunk = IsoWorld.instance.getMetaChunk(this.wx, this.wy);
					if (metaChunk != null) {
						for (int int2 = 0; int2 < metaChunk.numZones(); ++int2) {
							IsoMetaGrid.Zone zone = metaChunk.getZone(int2);
							this.addRandomCarCrash(zone, false);
						}
					}
				}
			}
		}
	}

	public void addSurvivorInHorde(boolean boolean1) {
		if (boolean1 || !IsoWorld.getZombiesDisabled()) {
			IsoMetaChunk metaChunk = IsoWorld.instance.getMetaChunk(this.wx, this.wy);
			if (metaChunk != null) {
				for (int int1 = 0; int1 < metaChunk.numZones(); ++int1) {
					IsoMetaGrid.Zone zone = metaChunk.getZone(int1);
					if (this.canAddSurvivorInHorde(zone, boolean1)) {
						byte byte1 = 4;
						float float1 = (float)GameTime.getInstance().getWorldAgeHours() / 24.0F;
						float1 += (float)((SandboxOptions.instance.TimeSinceApo.getValue() - 1) * 30);
						int int2 = (int)((float)byte1 + float1 * 0.03F);
						int2 = Math.max(15, int2);
						if (boolean1 || Rand.Next(0.0F, 500.0F) < 0.4F * (float)int2) {
							this.addSurvivorInHorde(zone);
							if (boolean1) {
								break;
							}
						}
					}
				}
			}
		}
	}

	private boolean canAddSurvivorInHorde(IsoMetaGrid.Zone zone, boolean boolean1) {
		if (!boolean1 && IsoWorld.instance.getTimeSinceLastSurvivorInHorde() > 0) {
			return false;
		} else if (!boolean1 && IsoWorld.getZombiesDisabled()) {
			return false;
		} else if (!boolean1 && zone.hourLastSeen != 0) {
			return false;
		} else if (!boolean1 && zone.haveConstruction) {
			return false;
		} else {
			return "Nav".equals(zone.getType());
		}
	}

	private void addSurvivorInHorde(IsoMetaGrid.Zone zone) {
		++zone.hourLastSeen;
		IsoWorld.instance.setTimeSinceLastSurvivorInHorde(5000);
		int int1 = Math.max(zone.x, this.wx * 10);
		int int2 = Math.max(zone.y, this.wy * 10);
		int int3 = Math.min(zone.x + zone.w, (this.wx + 1) * 10);
		int int4 = Math.min(zone.y + zone.h, (this.wy + 1) * 10);
		float float1 = (float)int1 + (float)(int3 - int1) / 2.0F;
		float float2 = (float)int2 + (float)(int4 - int2) / 2.0F;
		VirtualZombieManager.instance.choices.clear();
		IsoGridSquare square = this.getGridSquare((int)float1 - this.wx * 10, (int)float2 - this.wy * 10, 0);
		if (square.getBuilding() == null) {
			VirtualZombieManager.instance.choices.add(square);
			int int5 = Rand.Next(15, 20);
			for (int int6 = 0; int6 < int5; ++int6) {
				IsoZombie zombie = VirtualZombieManager.instance.createRealZombieAlways(Rand.Next(8), false);
				if (zombie != null) {
					zombie.dressInRandomOutfit();
					ZombieSpawnRecorder.instance.record(zombie, "addSurvivorInHorde");
				}
			}

			IsoZombie zombie2 = VirtualZombieManager.instance.createRealZombieAlways(Rand.Next(8), false);
			if (zombie2 != null) {
				ZombieSpawnRecorder.instance.record(zombie2, "addSurvivorInHorde");
				zombie2.setAsSurvivor();
			}
		}
	}

	public boolean canAddRandomCarCrash(IsoMetaGrid.Zone zone, boolean boolean1) {
		if (!boolean1 && zone.hourLastSeen != 0) {
			return false;
		} else if (!boolean1 && zone.haveConstruction) {
			return false;
		} else if (!"Nav".equals(zone.getType())) {
			return false;
		} else {
			int int1 = Math.max(zone.x, this.wx * 10);
			int int2 = Math.max(zone.y, this.wy * 10);
			int int3 = Math.min(zone.x + zone.w, (this.wx + 1) * 10);
			int int4 = Math.min(zone.y + zone.h, (this.wy + 1) * 10);
			if (zone.w > 30 && zone.h < 13) {
				return int3 - int1 >= 10 && int4 - int2 >= 5;
			} else if (zone.h > 30 && zone.w < 13) {
				return int3 - int1 >= 5 && int4 - int2 >= 10;
			} else {
				return false;
			}
		}
	}

	public void addRandomCarCrash(IsoMetaGrid.Zone zone, boolean boolean1) {
		if (this.vehicles.isEmpty()) {
			if ("Nav".equals(zone.getType())) {
				RandomizedVehicleStoryBase.doRandomStory(zone, this, false);
			}
		}
	}

	public static boolean FileExists(int int1, int int2) {
		File file = ChunkMapFilenames.instance.getFilename(int1, int2);
		if (file == null) {
			file = ZomboidFileSystem.instance.getFileInCurrentSave(prefix + int1 + "_" + int2 + ".bin");
		}

		long long1 = 0L;
		return file.exists();
	}

	private void checkPhysics() {
		if (this.physicsCheck) {
			WorldSimulation.instance.create();
			Bullet.beginUpdateChunk(this);
			byte byte1 = 0;
			if (byte1 < 8) {
				for (int int1 = 0; int1 < 10; ++int1) {
					for (int int2 = 0; int2 < 10; ++int2) {
						this.calcPhysics(int2, int1, byte1, this.shapes);
						int int3 = 0;
						for (int int4 = 0; int4 < 4; ++int4) {
							if (this.shapes[int4] != null) {
								bshapes[int3++] = (byte)(this.shapes[int4].ordinal() + 1);
							}
						}

						Bullet.updateChunk(int2, int1, byte1, int3, bshapes);
					}
				}
			}

			Bullet.endUpdateChunk();
			this.physicsCheck = false;
		}
	}

	private void calcPhysics(int int1, int int2, int int3, IsoChunk.PhysicsShapes[] physicsShapesArray) {
		for (int int4 = 0; int4 < 4; ++int4) {
			physicsShapesArray[int4] = null;
		}

		IsoGridSquare square = this.getGridSquare(int1, int2, int3);
		if (square != null) {
			int int5 = 0;
			boolean boolean1;
			int int6;
			if (int3 == 0) {
				boolean1 = false;
				for (int6 = 0; int6 < square.getObjects().size(); ++int6) {
					IsoObject object = (IsoObject)square.getObjects().get(int6);
					if (object.sprite != null && object.sprite.name != null && (object.sprite.name.contains("lighting_outdoor_") || object.sprite.name.equals("recreational_sports_01_21") || object.sprite.name.equals("recreational_sports_01_19") || object.sprite.name.equals("recreational_sports_01_32")) && (!object.getProperties().Is("MoveType") || !"WallObject".equals(object.getProperties().Val("MoveType")))) {
						boolean1 = true;
						break;
					}
				}

				if (boolean1) {
					physicsShapesArray[int5++] = IsoChunk.PhysicsShapes.Tree;
				}
			}

			boolean1 = false;
			if (!square.getSpecialObjects().isEmpty()) {
				int6 = square.getSpecialObjects().size();
				for (int int7 = 0; int7 < int6; ++int7) {
					IsoObject object2 = (IsoObject)square.getSpecialObjects().get(int7);
					if (object2 instanceof IsoThumpable && ((IsoThumpable)object2).isBlockAllTheSquare()) {
						boolean1 = true;
						break;
					}
				}
			}

			PropertyContainer propertyContainer = square.getProperties();
			if (square.hasTypes.isSet(IsoObjectType.isMoveAbleObject)) {
				physicsShapesArray[int5++] = IsoChunk.PhysicsShapes.Tree;
			}

			String string;
			if (square.hasTypes.isSet(IsoObjectType.tree)) {
				string = square.getProperties().Val("tree");
				String string2 = square.getProperties().Val("WindType");
				if (string == null) {
					physicsShapesArray[int5++] = IsoChunk.PhysicsShapes.Tree;
				}

				if (string != null && !string.equals("1") && (string2 == null || !string2.equals("2") || !string.equals("2") && !string.equals("1"))) {
					physicsShapesArray[int5++] = IsoChunk.PhysicsShapes.Tree;
				}
			} else if (!propertyContainer.Is(IsoFlagType.solid) && !propertyContainer.Is(IsoFlagType.solidtrans) && !propertyContainer.Is(IsoFlagType.blocksight) && !square.HasStairs() && !boolean1) {
				if (int3 > 0) {
					label206: {
						if (square.SolidFloorCached) {
							if (!square.SolidFloor) {
								break label206;
							}
						} else if (!square.TreatAsSolidFloor()) {
							break label206;
						}

						if (int5 == physicsShapesArray.length) {
							DebugLog.log(DebugType.General, "Error: Too many physics objects on gridsquare: " + square.x + ", " + square.y + ", " + square.z);
							return;
						}

						physicsShapesArray[int5++] = IsoChunk.PhysicsShapes.Floor;
					}
				}
			} else {
				if (int5 == physicsShapesArray.length) {
					DebugLog.log(DebugType.General, "Error: Too many physics objects on gridsquare: " + square.x + ", " + square.y + ", " + square.z);
					return;
				}

				physicsShapesArray[int5++] = IsoChunk.PhysicsShapes.Solid;
			}

			if (!square.getProperties().Is("CarSlowFactor")) {
				if (propertyContainer.Is(IsoFlagType.collideW) || propertyContainer.Is(IsoFlagType.windowW) || square.getProperties().Is(IsoFlagType.DoorWallW) && !square.getProperties().Is("GarageDoor")) {
					if (int5 == physicsShapesArray.length) {
						DebugLog.log(DebugType.General, "Error: Too many physics objects on gridsquare: " + square.x + ", " + square.y + ", " + square.z);
						return;
					}

					physicsShapesArray[int5++] = IsoChunk.PhysicsShapes.WallW;
				}

				if (propertyContainer.Is(IsoFlagType.collideN) || propertyContainer.Is(IsoFlagType.windowN) || square.getProperties().Is(IsoFlagType.DoorWallN) && !square.getProperties().Is("GarageDoor")) {
					if (int5 == physicsShapesArray.length) {
						DebugLog.log(DebugType.General, "Error: Too many physics objects on gridsquare: " + square.x + ", " + square.y + ", " + square.z);
						return;
					}

					physicsShapesArray[int5++] = IsoChunk.PhysicsShapes.WallN;
				}

				if (square.Is("PhysicsShape")) {
					if (int5 == physicsShapesArray.length) {
						DebugLog.log(DebugType.General, "Error: Too many physics objects on gridsquare: " + square.x + ", " + square.y + ", " + square.z);
						return;
					}

					string = square.getProperties().Val("PhysicsShape");
					if ("Solid".equals(string)) {
						physicsShapesArray[int5++] = IsoChunk.PhysicsShapes.Solid;
					} else if ("WallN".equals(string)) {
						physicsShapesArray[int5++] = IsoChunk.PhysicsShapes.WallN;
					} else if ("WallW".equals(string)) {
						physicsShapesArray[int5++] = IsoChunk.PhysicsShapes.WallW;
					} else if ("WallS".equals(string)) {
						physicsShapesArray[int5++] = IsoChunk.PhysicsShapes.WallS;
					} else if ("WallE".equals(string)) {
						physicsShapesArray[int5++] = IsoChunk.PhysicsShapes.WallE;
					} else if ("Tree".equals(string)) {
						physicsShapesArray[int5++] = IsoChunk.PhysicsShapes.Tree;
					} else if ("Floor".equals(string)) {
						physicsShapesArray[int5++] = IsoChunk.PhysicsShapes.Floor;
					}
				}
			}
		}
	}

	public boolean LoadBrandNew(int int1, int int2) {
		this.wx = int1;
		this.wy = int2;
		if (!CellLoader.LoadCellBinaryChunk(IsoWorld.instance.CurrentCell, int1, int2, this)) {
			return false;
		} else {
			if (!Core.GameMode.equals("Tutorial") && !Core.GameMode.equals("LastStand") && !GameClient.bClient) {
				this.addZombies = true;
			}

			return true;
		}
	}

	public boolean LoadOrCreate(int int1, int int2, ByteBuffer byteBuffer) {
		this.wx = int1;
		this.wy = int2;
		if (byteBuffer != null && !this.blam) {
			return this.LoadFromBuffer(int1, int2, byteBuffer);
		} else {
			File file = ChunkMapFilenames.instance.getFilename(int1, int2);
			if (file == null) {
				file = ZomboidFileSystem.instance.getFileInCurrentSave(prefix + int1 + "_" + int2 + ".bin");
			}

			if (file.exists() && !this.blam) {
				try {
					this.LoadFromDisk();
				} catch (Exception exception) {
					ExceptionLogger.logException(exception, "Error loading chunk " + int1 + "," + int2);
					if (GameServer.bServer) {
						LoggerManager.getLogger("map").write("Error loading chunk " + int1 + "," + int2);
						LoggerManager.getLogger("map").write(exception);
					}

					this.BackupBlam(int1, int2, exception);
					return false;
				}

				if (GameClient.bClient) {
					GameClient.instance.worldObjectsSyncReq.putRequestSyncIsoChunk(this);
				}

				return true;
			} else {
				return this.LoadBrandNew(int1, int2);
			}
		}
	}

	public boolean LoadFromBuffer(int int1, int int2, ByteBuffer byteBuffer) {
		this.wx = int1;
		this.wy = int2;
		if (!this.blam) {
			try {
				this.LoadFromDiskOrBuffer(byteBuffer);
				return true;
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				if (GameServer.bServer) {
					LoggerManager.getLogger("map").write("Error loading chunk " + int1 + "," + int2);
					LoggerManager.getLogger("map").write(exception);
				}

				this.BackupBlam(int1, int2, exception);
				return false;
			}
		} else {
			return this.LoadBrandNew(int1, int2);
		}
	}

	private void ensureSurroundNotNull(int int1, int int2, int int3) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		for (int int4 = -1; int4 <= 1; ++int4) {
			for (int int5 = -1; int5 <= 1; ++int5) {
				if ((int4 != 0 || int5 != 0) && int1 + int4 >= 0 && int1 + int4 < 10 && int2 + int5 >= 0 && int2 + int5 < 10) {
					IsoGridSquare square = this.getGridSquare(int1 + int4, int2 + int5, int3);
					if (square == null) {
						square = IsoGridSquare.getNew(cell, (SliceY)null, this.wx * 10 + int1 + int4, this.wy * 10 + int2 + int5, int3);
						this.setSquare(int1 + int4, int2 + int5, int3, square);
					}
				}
			}
		}
	}

	public void loadInWorldStreamerThread() {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		int int1;
		int int2;
		int int3;
		IsoGridSquare square;
		for (int1 = 0; int1 <= this.maxLevel; ++int1) {
			for (int2 = 0; int2 < 10; ++int2) {
				for (int3 = 0; int3 < 10; ++int3) {
					square = this.getGridSquare(int3, int2, int1);
					if (square == null && int1 == 0) {
						square = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, (SliceY)null, this.wx * 10 + int3, this.wy * 10 + int2, int1);
						this.setSquare(int3, int2, int1, square);
					}

					if (int1 == 0 && square.getFloor() == null) {
						DebugLog.log("ERROR: added floor at " + square.x + "," + square.y + "," + square.z + " because there wasn\'t one");
						IsoObject object = IsoObject.getNew();
						object.sprite = IsoSprite.getSprite(IsoSpriteManager.instance, (String)"carpentry_02_58", 0);
						object.square = square;
						square.Objects.add(0, object);
					}

					if (square != null) {
						if (int1 > 0 && !square.getObjects().isEmpty()) {
							this.ensureSurroundNotNull(int3, int2, int1);
							for (int int4 = int1 - 1; int4 > 0; --int4) {
								IsoGridSquare square2 = this.getGridSquare(int3, int2, int4);
								if (square2 == null) {
									square2 = IsoGridSquare.getNew(cell, (SliceY)null, this.wx * 10 + int3, this.wy * 10 + int2, int4);
									this.setSquare(int3, int2, int4, square2);
									this.ensureSurroundNotNull(int3, int2, int4);
								}
							}
						}

						square.RecalcProperties();
					}
				}
			}
		}

		assert chunkGetter.chunk == null;
		chunkGetter.chunk = this;
		for (int1 = 0; int1 < 10; ++int1) {
			label136: for (int2 = 0; int2 < 10; ++int2) {
				for (int3 = this.maxLevel; int3 > 0; --int3) {
					square = this.getGridSquare(int2, int1, int3);
					if (square != null && square.Is(IsoFlagType.solidfloor)) {
						--int3;
						while (true) {
							if (int3 < 0) {
								continue label136;
							}

							square = this.getGridSquare(int2, int1, int3);
							if (square != null && !square.haveRoof) {
								square.haveRoof = true;
								square.getProperties().UnSet(IsoFlagType.exterior);
							}

							--int3;
						}
					}
				}
			}
		}

		for (int1 = 0; int1 <= this.maxLevel; ++int1) {
			for (int2 = 0; int2 < 10; ++int2) {
				for (int3 = 0; int3 < 10; ++int3) {
					square = this.getGridSquare(int3, int2, int1);
					if (square != null) {
						square.RecalcAllWithNeighbours(true, chunkGetter);
					}
				}
			}
		}

		chunkGetter.chunk = null;
		for (int1 = 0; int1 <= this.maxLevel; ++int1) {
			for (int2 = 0; int2 < 10; ++int2) {
				for (int3 = 0; int3 < 10; ++int3) {
					square = this.getGridSquare(int3, int2, int1);
					if (square != null) {
						square.propertiesDirty = true;
					}
				}
			}
		}
	}

	private void RecalcAllWithNeighbour(IsoGridSquare square, IsoDirections directions, int int1) {
		byte byte1 = 0;
		byte byte2 = 0;
		if (directions != IsoDirections.W && directions != IsoDirections.NW && directions != IsoDirections.SW) {
			if (directions == IsoDirections.E || directions == IsoDirections.NE || directions == IsoDirections.SE) {
				byte1 = 1;
			}
		} else {
			byte1 = -1;
		}

		if (directions != IsoDirections.N && directions != IsoDirections.NW && directions != IsoDirections.NE) {
			if (directions == IsoDirections.S || directions == IsoDirections.SW || directions == IsoDirections.SE) {
				byte2 = 1;
			}
		} else {
			byte2 = -1;
		}

		int int2 = square.getX() + byte1;
		int int3 = square.getY() + byte2;
		int int4 = square.getZ() + int1;
		IsoGridSquare square2 = int1 == 0 ? square.nav[directions.index()] : IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, int4);
		if (square2 != null) {
			square.ReCalculateCollide(square2);
			square2.ReCalculateCollide(square);
			square.ReCalculatePathFind(square2);
			square2.ReCalculatePathFind(square);
			square.ReCalculateVisionBlocked(square2);
			square2.ReCalculateVisionBlocked(square);
		}

		if (int1 == 0) {
			switch (directions) {
			case E: 
				if (square2 == null) {
					square.e = null;
				} else {
					square.e = square.testPathFindAdjacent((IsoMovingObject)null, 1, 0, 0) ? null : square2;
					square2.w = square2.testPathFindAdjacent((IsoMovingObject)null, -1, 0, 0) ? null : square;
				}

				break;
			
			case W: 
				if (square2 == null) {
					square.w = null;
				} else {
					square.w = square.testPathFindAdjacent((IsoMovingObject)null, -1, 0, 0) ? null : square2;
					square2.e = square2.testPathFindAdjacent((IsoMovingObject)null, 1, 0, 0) ? null : square;
				}

				break;
			
			case N: 
				if (square2 == null) {
					square.n = null;
				} else {
					square.n = square.testPathFindAdjacent((IsoMovingObject)null, 0, -1, 0) ? null : square2;
					square2.s = square2.testPathFindAdjacent((IsoMovingObject)null, 0, 1, 0) ? null : square;
				}

				break;
			
			case S: 
				if (square2 == null) {
					square.s = null;
				} else {
					square.s = square.testPathFindAdjacent((IsoMovingObject)null, 0, 1, 0) ? null : square2;
					square2.n = square2.testPathFindAdjacent((IsoMovingObject)null, 0, -1, 0) ? null : square;
				}

				break;
			
			case NW: 
				if (square2 == null) {
					square.nw = null;
				} else {
					square.nw = square.testPathFindAdjacent((IsoMovingObject)null, -1, -1, 0) ? null : square2;
					square2.se = square2.testPathFindAdjacent((IsoMovingObject)null, 1, 1, 0) ? null : square;
				}

				break;
			
			case NE: 
				if (square2 == null) {
					square.ne = null;
				} else {
					square.ne = square.testPathFindAdjacent((IsoMovingObject)null, 1, -1, 0) ? null : square2;
					square2.sw = square2.testPathFindAdjacent((IsoMovingObject)null, -1, 1, 0) ? null : square;
				}

				break;
			
			case SE: 
				if (square2 == null) {
					square.se = null;
				} else {
					square.se = square.testPathFindAdjacent((IsoMovingObject)null, 1, 1, 0) ? null : square2;
					square2.nw = square2.testPathFindAdjacent((IsoMovingObject)null, -1, -1, 0) ? null : square;
				}

				break;
			
			case SW: 
				if (square2 == null) {
					square.sw = null;
				} else {
					square.sw = square.testPathFindAdjacent((IsoMovingObject)null, -1, 1, 0) ? null : square2;
					square2.ne = square2.testPathFindAdjacent((IsoMovingObject)null, 1, -1, 0) ? null : square;
				}

			
			}
		}
	}

	private void EnsureSurroundNotNullX(int int1, int int2, int int3) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		for (int int4 = int1 - 1; int4 <= int1 + 1; ++int4) {
			if (int4 >= 0 && int4 < 10) {
				IsoGridSquare square = this.getGridSquare(int4, int2, int3);
				if (square == null) {
					square = IsoGridSquare.getNew(cell, (SliceY)null, this.wx * 10 + int4, this.wy * 10 + int2, int3);
					cell.ConnectNewSquare(square, false);
				}
			}
		}
	}

	private void EnsureSurroundNotNullY(int int1, int int2, int int3) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		for (int int4 = int2 - 1; int4 <= int2 + 1; ++int4) {
			if (int4 >= 0 && int4 < 10) {
				IsoGridSquare square = this.getGridSquare(int1, int4, int3);
				if (square == null) {
					square = IsoGridSquare.getNew(cell, (SliceY)null, this.wx * 10 + int1, this.wy * 10 + int4, int3);
					cell.ConnectNewSquare(square, false);
				}
			}
		}
	}

	private void EnsureSurroundNotNull(int int1, int int2, int int3) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		IsoGridSquare square = this.getGridSquare(int1, int2, int3);
		if (square == null) {
			square = IsoGridSquare.getNew(cell, (SliceY)null, this.wx * 10 + int1, this.wy * 10 + int2, int3);
			cell.ConnectNewSquare(square, false);
		}
	}

	public void loadInMainThread() {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		IsoChunk chunk = cell.getChunk(this.wx - 1, this.wy);
		IsoChunk chunk2 = cell.getChunk(this.wx, this.wy - 1);
		IsoChunk chunk3 = cell.getChunk(this.wx + 1, this.wy);
		IsoChunk chunk4 = cell.getChunk(this.wx, this.wy + 1);
		IsoChunk chunk5 = cell.getChunk(this.wx - 1, this.wy - 1);
		IsoChunk chunk6 = cell.getChunk(this.wx + 1, this.wy - 1);
		IsoChunk chunk7 = cell.getChunk(this.wx + 1, this.wy + 1);
		IsoChunk chunk8 = cell.getChunk(this.wx - 1, this.wy + 1);
		IsoGridSquare square;
		int int1;
		int int2;
		for (int1 = 1; int1 < 8; ++int1) {
			for (int2 = 0; int2 < 10; ++int2) {
				if (chunk2 != null) {
					square = chunk2.getGridSquare(int2, 9, int1);
					if (square != null && !square.getObjects().isEmpty()) {
						this.EnsureSurroundNotNullX(int2, 0, int1);
					}
				}

				if (chunk4 != null) {
					square = chunk4.getGridSquare(int2, 0, int1);
					if (square != null && !square.getObjects().isEmpty()) {
						this.EnsureSurroundNotNullX(int2, 9, int1);
					}
				}
			}

			for (int2 = 0; int2 < 10; ++int2) {
				if (chunk != null) {
					square = chunk.getGridSquare(9, int2, int1);
					if (square != null && !square.getObjects().isEmpty()) {
						this.EnsureSurroundNotNullY(0, int2, int1);
					}
				}

				if (chunk3 != null) {
					square = chunk3.getGridSquare(0, int2, int1);
					if (square != null && !square.getObjects().isEmpty()) {
						this.EnsureSurroundNotNullY(9, int2, int1);
					}
				}
			}

			if (chunk5 != null) {
				square = chunk5.getGridSquare(9, 9, int1);
				if (square != null && !square.getObjects().isEmpty()) {
					this.EnsureSurroundNotNull(0, 0, int1);
				}
			}

			if (chunk6 != null) {
				square = chunk6.getGridSquare(0, 9, int1);
				if (square != null && !square.getObjects().isEmpty()) {
					this.EnsureSurroundNotNull(9, 0, int1);
				}
			}

			if (chunk7 != null) {
				square = chunk7.getGridSquare(0, 0, int1);
				if (square != null && !square.getObjects().isEmpty()) {
					this.EnsureSurroundNotNull(9, 9, int1);
				}
			}

			if (chunk8 != null) {
				square = chunk8.getGridSquare(9, 0, int1);
				if (square != null && !square.getObjects().isEmpty()) {
					this.EnsureSurroundNotNull(0, 9, int1);
				}
			}
		}

		for (int1 = 1; int1 < 8; ++int1) {
			for (int2 = 0; int2 < 10; ++int2) {
				if (chunk2 != null) {
					square = this.getGridSquare(int2, 0, int1);
					if (square != null && !square.getObjects().isEmpty()) {
						chunk2.EnsureSurroundNotNullX(int2, 9, int1);
					}
				}

				if (chunk4 != null) {
					square = this.getGridSquare(int2, 9, int1);
					if (square != null && !square.getObjects().isEmpty()) {
						chunk4.EnsureSurroundNotNullX(int2, 0, int1);
					}
				}
			}

			for (int2 = 0; int2 < 10; ++int2) {
				if (chunk != null) {
					square = this.getGridSquare(0, int2, int1);
					if (square != null && !square.getObjects().isEmpty()) {
						chunk.EnsureSurroundNotNullY(9, int2, int1);
					}
				}

				if (chunk3 != null) {
					square = this.getGridSquare(9, int2, int1);
					if (square != null && !square.getObjects().isEmpty()) {
						chunk3.EnsureSurroundNotNullY(0, int2, int1);
					}
				}
			}

			if (chunk5 != null) {
				square = this.getGridSquare(0, 0, int1);
				if (square != null && !square.getObjects().isEmpty()) {
					chunk5.EnsureSurroundNotNull(9, 9, int1);
				}
			}

			if (chunk6 != null) {
				square = this.getGridSquare(9, 0, int1);
				if (square != null && !square.getObjects().isEmpty()) {
					chunk6.EnsureSurroundNotNull(0, 9, int1);
				}
			}

			if (chunk7 != null) {
				square = this.getGridSquare(9, 9, int1);
				if (square != null && !square.getObjects().isEmpty()) {
					chunk7.EnsureSurroundNotNull(0, 0, int1);
				}
			}

			if (chunk8 != null) {
				square = this.getGridSquare(0, 9, int1);
				if (square != null && !square.getObjects().isEmpty()) {
					chunk8.EnsureSurroundNotNull(9, 0, int1);
				}
			}
		}

		for (int1 = 0; int1 <= this.maxLevel; ++int1) {
			for (int2 = 0; int2 < 10; ++int2) {
				for (int int3 = 0; int3 < 10; ++int3) {
					square = this.getGridSquare(int3, int2, int1);
					if (square != null) {
						if (int3 == 0 || int3 == 9 || int2 == 0 || int2 == 9) {
							IsoWorld.instance.CurrentCell.DoGridNav(square, IsoGridSquare.cellGetSquare);
							for (int int4 = -1; int4 <= 1; ++int4) {
								if (int3 == 0) {
									this.RecalcAllWithNeighbour(square, IsoDirections.W, int4);
									this.RecalcAllWithNeighbour(square, IsoDirections.NW, int4);
									this.RecalcAllWithNeighbour(square, IsoDirections.SW, int4);
								} else if (int3 == 9) {
									this.RecalcAllWithNeighbour(square, IsoDirections.E, int4);
									this.RecalcAllWithNeighbour(square, IsoDirections.NE, int4);
									this.RecalcAllWithNeighbour(square, IsoDirections.SE, int4);
								}

								if (int2 == 0) {
									this.RecalcAllWithNeighbour(square, IsoDirections.N, int4);
									if (int3 != 0) {
										this.RecalcAllWithNeighbour(square, IsoDirections.NW, int4);
									}

									if (int3 != 9) {
										this.RecalcAllWithNeighbour(square, IsoDirections.NE, int4);
									}
								} else if (int2 == 9) {
									this.RecalcAllWithNeighbour(square, IsoDirections.S, int4);
									if (int3 != 0) {
										this.RecalcAllWithNeighbour(square, IsoDirections.SW, int4);
									}

									if (int3 != 9) {
										this.RecalcAllWithNeighbour(square, IsoDirections.SE, int4);
									}
								}
							}

							IsoGridSquare square2 = square.nav[IsoDirections.N.index()];
							IsoGridSquare square3 = square.nav[IsoDirections.S.index()];
							IsoGridSquare square4 = square.nav[IsoDirections.W.index()];
							IsoGridSquare square5 = square.nav[IsoDirections.E.index()];
							if (square2 != null && square4 != null && (int3 == 0 || int2 == 0)) {
								this.RecalcAllWithNeighbour(square2, IsoDirections.W, 0);
							}

							if (square2 != null && square5 != null && (int3 == 9 || int2 == 0)) {
								this.RecalcAllWithNeighbour(square2, IsoDirections.E, 0);
							}

							if (square3 != null && square4 != null && (int3 == 0 || int2 == 9)) {
								this.RecalcAllWithNeighbour(square3, IsoDirections.W, 0);
							}

							if (square3 != null && square5 != null && (int3 == 9 || int2 == 9)) {
								this.RecalcAllWithNeighbour(square3, IsoDirections.E, 0);
							}
						}

						IsoRoom room = square.getRoom();
						if (room != null) {
							room.addSquare(square);
						}
					}
				}
			}
		}

		this.fixObjectAmbientEmittersOnAdjacentChunks(chunk3, chunk4);
		for (int1 = 0; int1 < 4; ++int1) {
			if (chunk != null) {
				chunk.lightCheck[int1] = true;
			}

			if (chunk2 != null) {
				chunk2.lightCheck[int1] = true;
			}

			if (chunk3 != null) {
				chunk3.lightCheck[int1] = true;
			}

			if (chunk4 != null) {
				chunk4.lightCheck[int1] = true;
			}

			if (chunk5 != null) {
				chunk5.lightCheck[int1] = true;
			}

			if (chunk6 != null) {
				chunk6.lightCheck[int1] = true;
			}

			if (chunk7 != null) {
				chunk7.lightCheck[int1] = true;
			}

			if (chunk8 != null) {
				chunk8.lightCheck[int1] = true;
			}
		}

		IsoLightSwitch.chunkLoaded(this);
	}

	private void fixObjectAmbientEmittersOnAdjacentChunks(IsoChunk chunk, IsoChunk chunk2) {
		if (!GameServer.bServer) {
			if (chunk != null || chunk2 != null) {
				for (int int1 = 0; int1 < 8; ++int1) {
					int int2;
					IsoGridSquare square;
					if (chunk != null) {
						for (int2 = 0; int2 < 10; ++int2) {
							square = chunk.getGridSquare(0, int2, int1);
							this.fixObjectAmbientEmittersOnSquare(square, false);
						}
					}

					if (chunk2 != null) {
						for (int2 = 0; int2 < 10; ++int2) {
							square = chunk2.getGridSquare(int2, 0, int1);
							this.fixObjectAmbientEmittersOnSquare(square, true);
						}
					}
				}
			}
		}
	}

	private void fixObjectAmbientEmittersOnSquare(IsoGridSquare square, boolean boolean1) {
		if (square != null && !square.getSpecialObjects().isEmpty()) {
			IsoObject object = square.getDoor(boolean1);
			if (object instanceof IsoDoor && ((IsoDoor)object).isExterior() && !object.hasObjectAmbientEmitter()) {
				object.addObjectAmbientEmitter((new ObjectAmbientEmitters.DoorLogic()).init(object));
			}

			IsoWindow window = square.getWindow(boolean1);
			if (window != null && window.isExterior() && !window.hasObjectAmbientEmitter()) {
				window.addObjectAmbientEmitter((new ObjectAmbientEmitters.WindowLogic()).init(window));
			}
		}
	}

	@Deprecated
	public void recalcNeighboursNow() {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		int int1;
		int int2;
		int int3;
		IsoGridSquare square;
		for (int1 = 0; int1 < 10; ++int1) {
			for (int2 = 0; int2 < 10; ++int2) {
				for (int3 = 0; int3 < 8; ++int3) {
					square = this.getGridSquare(int1, int2, int3);
					if (square != null) {
						if (int3 > 0 && !square.getObjects().isEmpty()) {
							square.EnsureSurroundNotNull();
							for (int int4 = int3 - 1; int4 > 0; --int4) {
								IsoGridSquare square2 = this.getGridSquare(int1, int2, int4);
								if (square2 == null) {
									square2 = IsoGridSquare.getNew(cell, (SliceY)null, this.wx * 10 + int1, this.wy * 10 + int2, int4);
									cell.ConnectNewSquare(square2, false);
								}
							}
						}

						square.RecalcProperties();
					}
				}
			}
		}

		for (int1 = 1; int1 < 8; ++int1) {
			IsoGridSquare square3;
			for (int2 = -1; int2 < 11; ++int2) {
				square3 = cell.getGridSquare(this.wx * 10 + int2, this.wy * 10 - 1, int1);
				if (square3 != null && !square3.getObjects().isEmpty()) {
					square3.EnsureSurroundNotNull();
				}

				square3 = cell.getGridSquare(this.wx * 10 + int2, this.wy * 10 + 10, int1);
				if (square3 != null && !square3.getObjects().isEmpty()) {
					square3.EnsureSurroundNotNull();
				}
			}

			for (int2 = 0; int2 < 10; ++int2) {
				square3 = cell.getGridSquare(this.wx * 10 - 1, this.wy * 10 + int2, int1);
				if (square3 != null && !square3.getObjects().isEmpty()) {
					square3.EnsureSurroundNotNull();
				}

				square3 = cell.getGridSquare(this.wx * 10 + 10, this.wy * 10 + int2, int1);
				if (square3 != null && !square3.getObjects().isEmpty()) {
					square3.EnsureSurroundNotNull();
				}
			}
		}

		for (int1 = 0; int1 < 10; ++int1) {
			for (int2 = 0; int2 < 10; ++int2) {
				for (int3 = 0; int3 < 8; ++int3) {
					square = this.getGridSquare(int1, int2, int3);
					if (square != null) {
						square.RecalcAllWithNeighbours(true);
						IsoRoom room = square.getRoom();
						if (room != null) {
							room.addSquare(square);
						}
					}
				}
			}
		}

		for (int1 = 0; int1 < 10; ++int1) {
			for (int2 = 0; int2 < 10; ++int2) {
				for (int3 = 0; int3 < 8; ++int3) {
					square = this.getGridSquare(int1, int2, int3);
					if (square != null) {
						square.propertiesDirty = true;
					}
				}
			}
		}

		IsoLightSwitch.chunkLoaded(this);
	}

	public void updateBuildings() {
	}

	public static void updatePlayerInBullet() {
		ArrayList arrayList = GameServer.getPlayers();
		Bullet.updatePlayerList(arrayList);
	}

	public void update() {
		if (!GameServer.bServer) {
			this.checkPhysics();
		}

		if (!this.loadedPhysics) {
			this.loadedPhysics = true;
			for (int int1 = 0; int1 < this.vehicles.size(); ++int1) {
				((BaseVehicle)this.vehicles.get(int1)).chunk = this;
			}
		}

		if (this.vehiclesForAddToWorld != null) {
			synchronized (this.vehiclesForAddToWorldLock) {
				for (int int2 = 0; int2 < this.vehiclesForAddToWorld.size(); ++int2) {
					((BaseVehicle)this.vehiclesForAddToWorld.get(int2)).addToWorld();
				}

				this.vehiclesForAddToWorld.clear();
				this.vehiclesForAddToWorld = null;
			}
		}

		this.updateVehicleStory();
	}

	public void updateVehicleStory() {
		if (this.bLoaded && this.m_vehicleStorySpawnData != null) {
			IsoMetaChunk metaChunk = IsoWorld.instance.getMetaChunk(this.wx, this.wy);
			if (metaChunk != null) {
				VehicleStorySpawnData vehicleStorySpawnData = this.m_vehicleStorySpawnData;
				for (int int1 = 0; int1 < metaChunk.numZones(); ++int1) {
					IsoMetaGrid.Zone zone = metaChunk.getZone(int1);
					if (vehicleStorySpawnData.isValid(zone, this)) {
						vehicleStorySpawnData.m_story.randomizeVehicleStory(zone, this);
						++zone.hourLastSeen;
						break;
					}
				}
			}
		}
	}

	public void setSquare(int int1, int int2, int int3, IsoGridSquare square) {
		assert square == null || square.x - this.wx * 10 == int1 && square.y - this.wy * 10 == int2 && square.z == int3;
		this.squares[int3][int2 * 10 + int1] = square;
		if (square != null) {
			square.chunk = this;
			if (square.z > this.maxLevel) {
				this.maxLevel = square.z;
			}
		}
	}

	public IsoGridSquare getGridSquare(int int1, int int2, int int3) {
		return int1 >= 0 && int1 < 10 && int2 >= 0 && int2 < 10 && int3 < 8 && int3 >= 0 ? this.squares[int3][int2 * 10 + int1] : null;
	}

	public IsoRoom getRoom(int int1) {
		return this.lotheader.getRoom(int1);
	}

	public void removeFromWorld() {
		loadGridSquare.remove(this);
		if (GameClient.bClient && GameClient.instance.bConnected) {
			try {
				GameClient.instance.sendAddedRemovedItems(true);
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		}

		try {
			MapCollisionData.instance.removeChunkFromWorld(this);
			ZombiePopulationManager.instance.removeChunkFromWorld(this);
			PolygonalMap2.instance.removeChunkFromWorld(this);
			this.collision.clear();
		} catch (Exception exception2) {
			exception2.printStackTrace();
		}

		byte byte1 = 100;
		int int1;
		for (int1 = 0; int1 < 8; ++int1) {
			for (int int2 = 0; int2 < byte1; ++int2) {
				IsoGridSquare square = this.squares[int1][int2];
				if (square != null) {
					RainManager.RemoveAllOn(square);
					square.clearWater();
					square.clearPuddles();
					if (square.getRoom() != null) {
						square.getRoom().removeSquare(square);
					}

					if (square.zone != null) {
						square.zone.removeSquare(square);
					}

					ArrayList arrayList = square.getMovingObjects();
					int int3;
					IsoMovingObject movingObject;
					for (int3 = 0; int3 < arrayList.size(); ++int3) {
						movingObject = (IsoMovingObject)arrayList.get(int3);
						if (movingObject instanceof IsoSurvivor) {
							IsoWorld.instance.CurrentCell.getSurvivorList().remove(movingObject);
							movingObject.Despawn();
						}

						movingObject.removeFromWorld();
						movingObject.current = movingObject.last = null;
						if (!arrayList.contains(movingObject)) {
							--int3;
						}
					}

					arrayList.clear();
					for (int3 = 0; int3 < square.getObjects().size(); ++int3) {
						IsoObject object = (IsoObject)square.getObjects().get(int3);
						object.removeFromWorld();
					}

					for (int3 = 0; int3 < square.getStaticMovingObjects().size(); ++int3) {
						movingObject = (IsoMovingObject)square.getStaticMovingObjects().get(int3);
						movingObject.removeFromWorld();
					}

					this.disconnectFromAdjacentChunks(square);
					square.softClear();
					square.chunk = null;
				}
			}
		}

		for (int1 = 0; int1 < this.vehicles.size(); ++int1) {
			BaseVehicle baseVehicle = (BaseVehicle)this.vehicles.get(int1);
			if (IsoWorld.instance.CurrentCell.getVehicles().contains(baseVehicle) || IsoWorld.instance.CurrentCell.addVehicles.contains(baseVehicle)) {
				DebugLog.log("IsoChunk.removeFromWorld: vehicle wasn\'t removed from world id=" + baseVehicle.VehicleID);
				baseVehicle.removeFromWorld();
			}
		}
	}

	private void disconnectFromAdjacentChunks(IsoGridSquare square) {
		int int1 = square.x % 10;
		int int2 = square.y % 10;
		if (int1 == 0 || int1 == 9 || int2 == 0 || int2 == 9) {
			int int3 = IsoDirections.N.index();
			int int4 = IsoDirections.S.index();
			if (square.nav[int3] != null && square.nav[int3].chunk != square.chunk) {
				square.nav[int3].nav[int4] = null;
				square.nav[int3].s = null;
			}

			int3 = IsoDirections.NW.index();
			int4 = IsoDirections.SE.index();
			if (square.nav[int3] != null && square.nav[int3].chunk != square.chunk) {
				square.nav[int3].nav[int4] = null;
				square.nav[int3].se = null;
			}

			int3 = IsoDirections.W.index();
			int4 = IsoDirections.E.index();
			if (square.nav[int3] != null && square.nav[int3].chunk != square.chunk) {
				square.nav[int3].nav[int4] = null;
				square.nav[int3].e = null;
			}

			int3 = IsoDirections.SW.index();
			int4 = IsoDirections.NE.index();
			if (square.nav[int3] != null && square.nav[int3].chunk != square.chunk) {
				square.nav[int3].nav[int4] = null;
				square.nav[int3].ne = null;
			}

			int3 = IsoDirections.S.index();
			int4 = IsoDirections.N.index();
			if (square.nav[int3] != null && square.nav[int3].chunk != square.chunk) {
				square.nav[int3].nav[int4] = null;
				square.nav[int3].n = null;
			}

			int3 = IsoDirections.SE.index();
			int4 = IsoDirections.NW.index();
			if (square.nav[int3] != null && square.nav[int3].chunk != square.chunk) {
				square.nav[int3].nav[int4] = null;
				square.nav[int3].nw = null;
			}

			int3 = IsoDirections.E.index();
			int4 = IsoDirections.W.index();
			if (square.nav[int3] != null && square.nav[int3].chunk != square.chunk) {
				square.nav[int3].nav[int4] = null;
				square.nav[int3].w = null;
			}

			int3 = IsoDirections.NE.index();
			int4 = IsoDirections.SW.index();
			if (square.nav[int3] != null && square.nav[int3].chunk != square.chunk) {
				square.nav[int3].nav[int4] = null;
				square.nav[int3].sw = null;
			}
		}
	}

	public void doReuseGridsquares() {
		byte byte1 = 100;
		for (int int1 = 0; int1 < 8; ++int1) {
			for (int int2 = 0; int2 < byte1; ++int2) {
				IsoGridSquare square = this.squares[int1][int2];
				if (square != null) {
					LuaEventManager.triggerEvent("ReuseGridsquare", square);
					for (int int3 = 0; int3 < square.getObjects().size(); ++int3) {
						IsoObject object = (IsoObject)square.getObjects().get(int3);
						if (object instanceof IsoTree) {
							object.reset();
							CellLoader.isoTreeCache.add((IsoTree)object);
						} else if (object instanceof IsoObject && object.getObjectName().equals("IsoObject")) {
							object.reset();
							CellLoader.isoObjectCache.add(object);
						} else {
							object.reuseGridSquare();
						}
					}

					square.discard();
					this.squares[int1][int2] = null;
				}
			}
		}

		this.resetForStore();
		assert !IsoChunkMap.chunkStore.contains(this);
		IsoChunkMap.chunkStore.add(this);
	}

	private static int bufferSize(int int1) {
		return (int1 + 65536 - 1) / 65536 * 65536;
	}

	private static ByteBuffer ensureCapacity(ByteBuffer byteBuffer, int int1) {
		if (byteBuffer == null || byteBuffer.capacity() < int1) {
			byteBuffer = ByteBuffer.allocate(bufferSize(int1));
		}

		return byteBuffer;
	}

	private static ByteBuffer ensureCapacity(ByteBuffer byteBuffer) {
		if (byteBuffer == null) {
			return ByteBuffer.allocate(65536);
		} else if (byteBuffer.capacity() - byteBuffer.position() < 65536) {
			ByteBuffer byteBuffer2 = ensureCapacity((ByteBuffer)null, byteBuffer.position() + 65536);
			return byteBuffer2.put(byteBuffer.array(), 0, byteBuffer.position());
		} else {
			return byteBuffer;
		}
	}

	public void LoadFromDisk() throws IOException {
		this.LoadFromDiskOrBuffer((ByteBuffer)null);
	}

	public void LoadFromDiskOrBuffer(ByteBuffer byteBuffer) throws IOException {
		sanityCheck.beginLoad(this);
		try {
			ByteBuffer byteBuffer2;
			if (byteBuffer == null) {
				SliceBufferLoad = SafeRead(prefix, this.wx, this.wy, SliceBufferLoad);
				byteBuffer2 = SliceBufferLoad;
			} else {
				byteBuffer2 = byteBuffer;
			}

			int int1 = this.wx * 10;
			int int2 = this.wy * 10;
			int1 /= 300;
			int2 /= 300;
			String string = ChunkMapFilenames.instance.getHeader(int1, int2);
			if (IsoLot.InfoHeaders.containsKey(string)) {
				this.lotheader = (LotHeader)IsoLot.InfoHeaders.get(string);
			}

			IsoCell.wx = this.wx;
			IsoCell.wy = this.wy;
			boolean boolean1 = byteBuffer2.get() == 1;
			int int3 = byteBuffer2.getInt();
			if (boolean1) {
				DebugLog.log("WorldVersion = " + int3 + ", debug = " + boolean1);
			}

			if (int3 > 194) {
				throw new RuntimeException("unknown world version " + int3 + " while reading chunk " + this.wx + "," + this.wy);
			}

			this.bFixed2x = int3 >= 85;
			int int4;
			if (int3 >= 61) {
				int4 = byteBuffer2.getInt();
				sanityCheck.checkLength((long)int4, (long)byteBuffer2.limit());
				long long1 = byteBuffer2.getLong();
				crcLoad.reset();
				crcLoad.update(byteBuffer2.array(), 17, byteBuffer2.limit() - 1 - 4 - 4 - 8);
				sanityCheck.checkCRC(long1, crcLoad.getValue());
			}

			int4 = 0;
			if (GameClient.bClient || GameServer.bServer) {
				int4 = ServerOptions.getInstance().BloodSplatLifespanDays.getValue();
			}

			float float1 = (float)GameTime.getInstance().getWorldAgeHours();
			int int5 = byteBuffer2.getInt();
			for (int int6 = 0; int6 < int5; ++int6) {
				IsoFloorBloodSplat floorBloodSplat = new IsoFloorBloodSplat();
				floorBloodSplat.load(byteBuffer2, int3);
				if (floorBloodSplat.worldAge > float1) {
					floorBloodSplat.worldAge = float1;
				}

				if (int4 <= 0 || !(float1 - floorBloodSplat.worldAge >= (float)(int4 * 24))) {
					if (int3 < 73 && floorBloodSplat.Type < 8) {
						floorBloodSplat.index = ++this.nextSplatIndex;
					}

					if (floorBloodSplat.Type < 8) {
						this.nextSplatIndex = floorBloodSplat.index % 10;
					}

					this.FloorBloodSplats.add(floorBloodSplat);
				}
			}

			IsoMetaGrid metaGrid = IsoWorld.instance.getMetaGrid();
			boolean boolean2 = false;
			int int7;
			int int8;
			for (int int9 = 0; int9 < 10; ++int9) {
				for (int7 = 0; int7 < 10; ++int7) {
					byte byte1 = byteBuffer2.get();
					for (int8 = 0; int8 < 8; ++int8) {
						IsoGridSquare square = null;
						boolean boolean3 = false;
						if ((byte1 & 1 << int8) != 0) {
							boolean3 = true;
						}

						if (boolean3) {
							if (square == null) {
								if (IsoGridSquare.loadGridSquareCache != null) {
									square = IsoGridSquare.getNew(IsoGridSquare.loadGridSquareCache, IsoWorld.instance.CurrentCell, (SliceY)null, int9 + this.wx * 10, int7 + this.wy * 10, int8);
								} else {
									square = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, (SliceY)null, int9 + this.wx * 10, int7 + this.wy * 10, int8);
								}
							}

							square.chunk = this;
							if (this.lotheader != null) {
								RoomDef roomDef = metaGrid.getRoomAt(square.x, square.y, square.z);
								int int10 = roomDef != null ? roomDef.ID : -1;
								square.setRoomID(int10);
								roomDef = metaGrid.getEmptyOutsideAt(square.x, square.y, square.z);
								if (roomDef != null) {
									IsoRoom room = this.getRoom(roomDef.ID);
									square.roofHideBuilding = room == null ? null : room.building;
								}
							}

							square.ResetIsoWorldRegion();
							this.setSquare(int9, int7, int8, square);
						}

						if (boolean3 && square != null) {
							square.load(byteBuffer2, int3, boolean1);
							square.FixStackableObjects();
							if (this.jobType == IsoChunk.JobType.SoftReset) {
								if (!square.getStaticMovingObjects().isEmpty()) {
									square.getStaticMovingObjects().clear();
								}

								for (int int11 = 0; int11 < square.getObjects().size(); ++int11) {
									IsoObject object = (IsoObject)square.getObjects().get(int11);
									object.softReset();
									if (object.getObjectIndex() == -1) {
										--int11;
									}
								}

								square.setOverlayDone(false);
							}
						}
					}
				}
			}

			if (int3 >= 45) {
				this.getErosionData().load(byteBuffer2, int3);
				this.getErosionData().set(this);
			}

			short short1;
			byte byte2;
			if (int3 >= 127) {
				short1 = byteBuffer2.getShort();
				if (short1 > 0 && this.generatorsTouchingThisChunk == null) {
					this.generatorsTouchingThisChunk = new ArrayList();
				}

				if (this.generatorsTouchingThisChunk != null) {
					this.generatorsTouchingThisChunk.clear();
				}

				for (int7 = 0; int7 < short1; ++int7) {
					int8 = byteBuffer2.getInt();
					int int12 = byteBuffer2.getInt();
					byte2 = byteBuffer2.get();
					IsoGameCharacter.Location location = new IsoGameCharacter.Location(int8, int12, byte2);
					this.generatorsTouchingThisChunk.add(location);
				}
			}

			this.vehicles.clear();
			if (!GameClient.bClient) {
				if (int3 >= 91) {
					short1 = byteBuffer2.getShort();
					for (int7 = 0; int7 < short1; ++int7) {
						byte byte3 = byteBuffer2.get();
						byte byte4 = byteBuffer2.get();
						byte2 = byteBuffer2.get();
						IsoObject object2 = IsoObject.factoryFromFileInput(IsoWorld.instance.CurrentCell, byteBuffer2);
						if (object2 != null && object2 instanceof BaseVehicle) {
							IsoGridSquare square2 = this.getGridSquare(byte3, byte4, byte2);
							object2.square = square2;
							((IsoMovingObject)object2).current = square2;
							try {
								object2.load(byteBuffer2, int3, boolean1);
								this.vehicles.add((BaseVehicle)object2);
								addFromCheckedVehicles((BaseVehicle)object2);
								if (this.jobType == IsoChunk.JobType.SoftReset) {
									object2.softReset();
								}
							} catch (Exception exception) {
								throw new RuntimeException(exception);
							}
						}
					}
				}

				if (int3 >= 125) {
					this.lootRespawnHour = byteBuffer2.getInt();
				}

				if (int3 >= 160) {
					byte byte5 = byteBuffer2.get();
					for (int7 = 0; int7 < byte5; ++int7) {
						int8 = byteBuffer2.getInt();
						this.addSpawnedRoom(int8);
					}
				}
			}
		} finally {
			sanityCheck.endLoad(this);
			this.bFixed2x = true;
		}

		if (this.getGridSquare(0, 0, 0) == null && this.getGridSquare(9, 9, 0) == null) {
			throw new RuntimeException("black chunk " + this.wx + "," + this.wy);
		}
	}

	public void doLoadGridsquare() {
		if (this.jobType == IsoChunk.JobType.SoftReset) {
			this.m_spawnedRooms.clear();
		}

		if (!GameServer.bServer) {
			this.loadInMainThread();
		}

		if (this.addZombies && !VehiclesDB2.instance.isChunkSeen(this.wx, this.wy)) {
			try {
				this.AddVehicles();
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
			}
		}

		this.AddZombieZoneStory();
		VehiclesDB2.instance.setChunkSeen(this.wx, this.wy);
		if (this.addZombies) {
			if (IsoWorld.instance.getTimeSinceLastSurvivorInHorde() > 0) {
				IsoWorld.instance.setTimeSinceLastSurvivorInHorde(IsoWorld.instance.getTimeSinceLastSurvivorInHorde() - 1);
			}

			this.addSurvivorInHorde(false);
		}

		this.update();
		if (!GameServer.bServer) {
			FliesSound.instance.chunkLoaded(this);
			NearestWalls.chunkLoaded(this);
		}

		int int1;
		if (this.addZombies) {
			int1 = 5 + SandboxOptions.instance.TimeSinceApo.getValue();
			int1 = Math.min(20, int1);
			if (Rand.Next(int1) == 0) {
				this.AddCorpses(this.wx, this.wy);
			}

			if (Rand.Next(int1 * 2) == 0) {
				this.AddBlood(this.wx, this.wy);
			}
		}

		LoadGridsquarePerformanceWorkaround.init(this.wx, this.wy);
		tempBuildings.clear();
		int int2;
		if (!GameClient.bClient) {
			for (int1 = 0; int1 < this.vehicles.size(); ++int1) {
				BaseVehicle baseVehicle = (BaseVehicle)this.vehicles.get(int1);
				if (!baseVehicle.addedToWorld && VehiclesDB2.instance.isVehicleLoaded(baseVehicle)) {
					baseVehicle.removeFromSquare();
					this.vehicles.remove(int1);
					--int1;
				} else {
					if (!baseVehicle.addedToWorld) {
						baseVehicle.addToWorld();
					}

					if (baseVehicle.sqlID == -1) {
						assert false;
						if (baseVehicle.square == null) {
							float float1 = 5.0E-4F;
							int int3 = this.wx * 10;
							int2 = this.wy * 10;
							int int4 = int3 + 10;
							int int5 = int2 + 10;
							float float2 = PZMath.clamp(baseVehicle.x, (float)int3 + float1, (float)int4 - float1);
							float float3 = PZMath.clamp(baseVehicle.y, (float)int2 + float1, (float)int5 - float1);
							baseVehicle.square = this.getGridSquare((int)float2 - this.wx * 10, (int)float3 - this.wy * 10, 0);
						}

						VehiclesDB2.instance.addVehicle(baseVehicle);
					}
				}
			}
		}

		this.m_treeCount = 0;
		this.m_scavengeZone = null;
		this.m_numberOfWaterTiles = 0;
		int int6;
		int int7;
		for (int1 = 0; int1 <= this.maxLevel; ++int1) {
			for (int6 = 0; int6 < 10; ++int6) {
				for (int7 = 0; int7 < 10; ++int7) {
					IsoGridSquare square = this.getGridSquare(int6, int7, int1);
					if (square != null && !square.getObjects().isEmpty()) {
						for (int2 = 0; int2 < square.getObjects().size(); ++int2) {
							IsoObject object = (IsoObject)square.getObjects().get(int2);
							object.addToWorld();
							if (int1 == 0 && object.getSprite() != null && object.getSprite().getProperties().Is(IsoFlagType.water)) {
								++this.m_numberOfWaterTiles;
							}
						}

						if (square.HasTree()) {
							++this.m_treeCount;
						}

						if (this.jobType != IsoChunk.JobType.SoftReset) {
							ErosionMain.LoadGridsquare(square);
						}

						if (this.addZombies) {
							MapObjects.newGridSquare(square);
						}

						MapObjects.loadGridSquare(square);
						LuaEventManager.triggerEvent("LoadGridsquare", square);
						LoadGridsquarePerformanceWorkaround.LoadGridsquare(square);
					}

					if (square != null && !square.getStaticMovingObjects().isEmpty()) {
						for (int2 = 0; int2 < square.getStaticMovingObjects().size(); ++int2) {
							IsoMovingObject movingObject = (IsoMovingObject)square.getStaticMovingObjects().get(int2);
							movingObject.addToWorld();
						}
					}

					if (square != null && square.getBuilding() != null && !tempBuildings.contains(square.getBuilding())) {
						tempBuildings.add(square.getBuilding());
					}
				}
			}
		}

		if (this.jobType != IsoChunk.JobType.SoftReset) {
			ErosionMain.ChunkLoaded(this);
		}

		if (this.jobType != IsoChunk.JobType.SoftReset) {
			SGlobalObjects.chunkLoaded(this.wx, this.wy);
		}

		ReanimatedPlayers.instance.addReanimatedPlayersToChunk(this);
		if (this.jobType != IsoChunk.JobType.SoftReset) {
			MapCollisionData.instance.addChunkToWorld(this);
			ZombiePopulationManager.instance.addChunkToWorld(this);
			PolygonalMap2.instance.addChunkToWorld(this);
			IsoGenerator.chunkLoaded(this);
			LootRespawn.chunkLoaded(this);
		}

		if (!GameServer.bServer) {
			ArrayList arrayList = IsoWorld.instance.CurrentCell.roomLights;
			for (int6 = 0; int6 < this.roomLights.size(); ++int6) {
				IsoRoomLight roomLight = (IsoRoomLight)this.roomLights.get(int6);
				if (!arrayList.contains(roomLight)) {
					arrayList.add(roomLight);
				}
			}
		}

		this.roomLights.clear();
		if (this.jobType != IsoChunk.JobType.SoftReset) {
			this.randomizeBuildingsEtc();
		}

		this.checkAdjacentChunks();
		try {
			if (GameServer.bServer && this.jobType != IsoChunk.JobType.SoftReset) {
				for (int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
					UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
					if (!udpConnection.chunkObjectState.isEmpty()) {
						for (int7 = 0; int7 < udpConnection.chunkObjectState.size(); int7 += 2) {
							short short1 = udpConnection.chunkObjectState.get(int7);
							short short2 = udpConnection.chunkObjectState.get(int7 + 1);
							if (short1 == this.wx && short2 == this.wy) {
								udpConnection.chunkObjectState.remove(int7, 2);
								int7 -= 2;
								ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
								PacketTypes.PacketType.ChunkObjectState.doPacket(byteBufferWriter);
								byteBufferWriter.putShort((short)this.wx);
								byteBufferWriter.putShort((short)this.wy);
								try {
									if (this.saveObjectState(byteBufferWriter.bb)) {
										PacketTypes.PacketType.ChunkObjectState.send(udpConnection);
									} else {
										udpConnection.cancelPacket();
									}
								} catch (Throwable throwable2) {
									throwable2.printStackTrace();
									udpConnection.cancelPacket();
								}
							}
						}
					}
				}
			}

			if (GameClient.bClient) {
				ByteBufferWriter byteBufferWriter2 = GameClient.connection.startPacket();
				PacketTypes.PacketType.ChunkObjectState.doPacket(byteBufferWriter2);
				byteBufferWriter2.putShort((short)this.wx);
				byteBufferWriter2.putShort((short)this.wy);
				PacketTypes.PacketType.ChunkObjectState.send(GameClient.connection);
			}
		} catch (Throwable throwable3) {
			ExceptionLogger.logException(throwable3);
		}
	}

	private void randomizeBuildingsEtc() {
		tempRoomDefs.clear();
		IsoWorld.instance.MetaGrid.getRoomsIntersecting(this.wx * 10 - 1, this.wy * 10 - 1, 11, 11, tempRoomDefs);
		int int1;
		for (int1 = 0; int1 < tempRoomDefs.size(); ++int1) {
			IsoRoom room = ((RoomDef)tempRoomDefs.get(int1)).getIsoRoom();
			if (room != null) {
				IsoBuilding building = room.getBuilding();
				if (!tempBuildings.contains(building)) {
					tempBuildings.add(building);
				}
			}
		}

		IsoBuilding building2;
		for (int1 = 0; int1 < tempBuildings.size(); ++int1) {
			building2 = (IsoBuilding)tempBuildings.get(int1);
			if (!GameClient.bClient && building2.def != null && building2.def.isFullyStreamedIn()) {
				StashSystem.doBuildingStash(building2.def);
			}

			RandomizedBuildingBase.ChunkLoaded(building2);
		}

		if (!GameClient.bClient && !tempBuildings.isEmpty()) {
			for (int1 = 0; int1 < tempBuildings.size(); ++int1) {
				building2 = (IsoBuilding)tempBuildings.get(int1);
				for (int int2 = 0; int2 < building2.Rooms.size(); ++int2) {
					IsoRoom room2 = (IsoRoom)building2.Rooms.get(int2);
					if (room2.def.bDoneSpawn && !this.isSpawnedRoom(room2.def.ID) && room2.def.intersects(this.wx * 10, this.wy * 10, 10, 10)) {
						this.addSpawnedRoom(room2.def.ID);
						VirtualZombieManager.instance.addIndoorZombiesToChunk(this, room2);
					}
				}
			}
		}
	}

	private void checkAdjacentChunks() {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		for (int int1 = -1; int1 <= 1; ++int1) {
			for (int int2 = -1; int2 <= 1; ++int2) {
				if (int2 != 0 || int1 != 0) {
					IsoChunk chunk = cell.getChunk(this.wx + int2, this.wy + int1);
					if (chunk != null) {
						++chunk.m_adjacentChunkLoadedCounter;
					}
				}
			}
		}
	}

	private void AddZombieZoneStory() {
		IsoMetaChunk metaChunk = IsoWorld.instance.getMetaChunk(this.wx, this.wy);
		if (metaChunk != null) {
			for (int int1 = 0; int1 < metaChunk.numZones(); ++int1) {
				IsoMetaGrid.Zone zone = metaChunk.getZone(int1);
				RandomizedZoneStoryBase.isValidForStory(zone, false);
			}
		}
	}

	public void setCache() {
		IsoWorld.instance.CurrentCell.setCacheChunk(this);
	}

	private static IsoChunk.ChunkLock acquireLock(int int1, int int2) {
		synchronized (Locks) {
			for (int int3 = 0; int3 < Locks.size(); ++int3) {
				if (((IsoChunk.ChunkLock)Locks.get(int3)).wx == int1 && ((IsoChunk.ChunkLock)Locks.get(int3)).wy == int2) {
					return ((IsoChunk.ChunkLock)Locks.get(int3)).ref();
				}
			}

			IsoChunk.ChunkLock chunkLock = FreeLocks.isEmpty() ? new IsoChunk.ChunkLock(int1, int2) : ((IsoChunk.ChunkLock)FreeLocks.pop()).set(int1, int2);
			Locks.add(chunkLock);
			return chunkLock.ref();
		}
	}

	private static void releaseLock(IsoChunk.ChunkLock chunkLock) {
		synchronized (Locks) {
			if (chunkLock.deref() == 0) {
				Locks.remove(chunkLock);
				FreeLocks.push(chunkLock);
			}
		}
	}

	public void setCacheIncludingNull() {
		for (int int1 = 0; int1 < 8; ++int1) {
			for (int int2 = 0; int2 < 10; ++int2) {
				for (int int3 = 0; int3 < 10; ++int3) {
					IsoGridSquare square = this.getGridSquare(int2, int3, int1);
					IsoWorld.instance.CurrentCell.setCacheGridSquare(this.wx * 10 + int2, this.wy * 10 + int3, int1, square);
				}
			}
		}
	}

	public void Save(boolean boolean1) throws IOException {
		if (!Core.getInstance().isNoSave() && !GameClient.bClient) {
			synchronized (WriteLock) {
				sanityCheck.beginSave(this);
				try {
					File file = ChunkMapFilenames.instance.getDir(Core.GameSaveWorld);
					if (!file.exists()) {
						file.mkdir();
					}

					SliceBuffer = this.Save(SliceBuffer, crcSave);
					if (!GameClient.bClient && !GameServer.bServer) {
						SafeWrite(prefix, this.wx, this.wy, SliceBuffer);
					} else {
						long long1 = ChunkChecksum.getChecksumIfExists(this.wx, this.wy);
						crcSave.reset();
						crcSave.update(SliceBuffer.array(), 0, SliceBuffer.position());
						if (long1 != crcSave.getValue()) {
							ChunkChecksum.setChecksum(this.wx, this.wy, crcSave.getValue());
							SafeWrite(prefix, this.wx, this.wy, SliceBuffer);
						}
					}

					if (!boolean1 && !GameServer.bServer) {
						if (this.jobType != IsoChunk.JobType.Convert) {
							WorldReuserThread.instance.addReuseChunk(this);
						} else {
							this.doReuseGridsquares();
						}
					}
				} finally {
					sanityCheck.endSave(this);
				}
			}
		} else {
			if (!boolean1 && !GameServer.bServer && this.jobType != IsoChunk.JobType.Convert) {
				WorldReuserThread.instance.addReuseChunk(this);
			}
		}
	}

	public static void SafeWrite(String string, int int1, int int2, ByteBuffer byteBuffer) throws IOException {
		IsoChunk.ChunkLock chunkLock = acquireLock(int1, int2);
		chunkLock.lockForWriting();
		try {
			File file = ChunkMapFilenames.instance.getFilename(int1, int2);
			sanityCheck.beginSaveFile(file.getAbsolutePath());
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				try {
					fileOutputStream.getChannel().truncate(0L);
					fileOutputStream.write(byteBuffer.array(), 0, byteBuffer.position());
				} catch (Throwable throwable) {
					try {
						fileOutputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				fileOutputStream.close();
			} finally {
				sanityCheck.endSaveFile();
			}
		} finally {
			chunkLock.unlockForWriting();
			releaseLock(chunkLock);
		}
	}

	public static ByteBuffer SafeRead(String string, int int1, int int2, ByteBuffer byteBuffer) throws IOException {
		IsoChunk.ChunkLock chunkLock = acquireLock(int1, int2);
		chunkLock.lockForReading();
		try {
			File file = ChunkMapFilenames.instance.getFilename(int1, int2);
			if (file == null) {
				file = ZomboidFileSystem.instance.getFileInCurrentSave(string + int1 + "_" + int2 + ".bin");
			}

			sanityCheck.beginLoadFile(file.getAbsolutePath());
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				try {
					byteBuffer = ensureCapacity(byteBuffer, (int)file.length());
					byteBuffer.clear();
					int int3 = fileInputStream.read(byteBuffer.array());
					byteBuffer.limit(PZMath.max(int3, 0));
				} catch (Throwable throwable) {
					try {
						fileInputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				fileInputStream.close();
			} finally {
				sanityCheck.endLoadFile(file.getAbsolutePath());
			}
		} finally {
			chunkLock.unlockForReading();
			releaseLock(chunkLock);
		}

		return byteBuffer;
	}

	public void SaveLoadedChunk(ClientChunkRequest.Chunk chunk, CRC32 cRC32) throws IOException {
		chunk.bb = this.Save(chunk.bb, cRC32);
	}

	public static boolean IsDebugSave() {
		return !Core.bDebug ? false : false;
	}

	public ByteBuffer Save(ByteBuffer byteBuffer, CRC32 cRC32) throws IOException {
		byteBuffer.rewind();
		byteBuffer = ensureCapacity(byteBuffer);
		byteBuffer.clear();
		byteBuffer.put((byte)(IsDebugSave() ? 1 : 0));
		byteBuffer.putInt(194);
		byteBuffer.putInt(0);
		byteBuffer.putLong(0L);
		int int1 = Math.min(1000, this.FloorBloodSplats.size());
		int int2 = this.FloorBloodSplats.size() - int1;
		byteBuffer.putInt(int1);
		int int3;
		for (int3 = int2; int3 < this.FloorBloodSplats.size(); ++int3) {
			IsoFloorBloodSplat floorBloodSplat = (IsoFloorBloodSplat)this.FloorBloodSplats.get(int3);
			floorBloodSplat.save(byteBuffer);
		}

		int3 = byteBuffer.position();
		boolean boolean1 = false;
		boolean boolean2 = false;
		boolean boolean3 = false;
		int int4;
		for (int4 = 0; int4 < 10; ++int4) {
			for (int int5 = 0; int5 < 10; ++int5) {
				byte byte1 = 0;
				int int6 = byteBuffer.position();
				byteBuffer.put(byte1);
				for (int int7 = 0; int7 < 8; ++int7) {
					IsoGridSquare square = this.getGridSquare(int4, int5, int7);
					byteBuffer = ensureCapacity(byteBuffer);
					if (square != null && square.shouldSave()) {
						byte1 = (byte)(byte1 | 1 << int7);
						int int8 = byteBuffer.position();
						while (true) {
							try {
								square.save(byteBuffer, (ObjectOutputStream)null, IsDebugSave());
								break;
							} catch (BufferOverflowException bufferOverflowException) {
								DebugLog.log("IsoChunk.Save: BufferOverflowException, growing ByteBuffer");
								byteBuffer = ensureCapacity(byteBuffer);
								byteBuffer.position(int8);
							}
						}
					}
				}

				int int9 = byteBuffer.position();
				byteBuffer.position(int6);
				byteBuffer.put(byte1);
				byteBuffer.position(int9);
			}
		}

		byteBuffer = ensureCapacity(byteBuffer);
		this.getErosionData().save(byteBuffer);
		if (this.generatorsTouchingThisChunk == null) {
			byteBuffer.putShort((short)0);
		} else {
			byteBuffer.putShort((short)this.generatorsTouchingThisChunk.size());
			for (int4 = 0; int4 < this.generatorsTouchingThisChunk.size(); ++int4) {
				IsoGameCharacter.Location location = (IsoGameCharacter.Location)this.generatorsTouchingThisChunk.get(int4);
				byteBuffer.putInt(location.x);
				byteBuffer.putInt(location.y);
				byteBuffer.put((byte)location.z);
			}
		}

		byteBuffer.putShort((short)0);
		if ((!GameServer.bServer || GameServer.bSoftReset) && !GameClient.bClient && !GameWindow.bLoadedAsClient) {
			VehiclesDB2.instance.unloadChunk(this);
		}

		if (GameClient.bClient) {
			int4 = ServerOptions.instance.HoursForLootRespawn.getValue();
			if (int4 > 0 && !(GameTime.getInstance().getWorldAgeHours() < (double)int4)) {
				this.lootRespawnHour = 7 + (int)(GameTime.getInstance().getWorldAgeHours() / (double)int4) * int4;
			} else {
				this.lootRespawnHour = -1;
			}
		}

		byteBuffer.putInt(this.lootRespawnHour);
		assert this.m_spawnedRooms.size() <= 127;
		byteBuffer.put((byte)this.m_spawnedRooms.size());
		for (int4 = 0; int4 < this.m_spawnedRooms.size(); ++int4) {
			byteBuffer.putInt(this.m_spawnedRooms.get(int4));
		}

		int4 = byteBuffer.position();
		cRC32.reset();
		cRC32.update(byteBuffer.array(), 17, int4 - 1 - 4 - 4 - 8);
		byteBuffer.position(5);
		byteBuffer.putInt(int4);
		byteBuffer.putLong(cRC32.getValue());
		byteBuffer.position(int4);
		return byteBuffer;
	}

	public boolean saveObjectState(ByteBuffer byteBuffer) throws IOException {
		boolean boolean1 = true;
		for (int int1 = 0; int1 < 8; ++int1) {
			for (int int2 = 0; int2 < 10; ++int2) {
				for (int int3 = 0; int3 < 10; ++int3) {
					IsoGridSquare square = this.getGridSquare(int3, int2, int1);
					if (square != null) {
						int int4 = square.getObjects().size();
						IsoObject[] objectArray = (IsoObject[])square.getObjects().getElements();
						for (int int5 = 0; int5 < int4; ++int5) {
							IsoObject object = objectArray[int5];
							int int6 = byteBuffer.position();
							byteBuffer.position(int6 + 2 + 2 + 4 + 2);
							int int7 = byteBuffer.position();
							object.saveState(byteBuffer);
							int int8 = byteBuffer.position();
							if (int8 > int7) {
								byteBuffer.position(int6);
								byteBuffer.putShort((short)(int3 + int2 * 10 + int1 * 10 * 10));
								byteBuffer.putShort((short)int5);
								byteBuffer.putInt(object.getObjectName().hashCode());
								byteBuffer.putShort((short)(int8 - int7));
								byteBuffer.position(int8);
								boolean1 = false;
							} else {
								byteBuffer.position(int6);
							}
						}
					}
				}
			}
		}

		if (boolean1) {
			return false;
		} else {
			byteBuffer.putShort((short)-1);
			return true;
		}
	}

	public void loadObjectState(ByteBuffer byteBuffer) throws IOException {
		for (short short1 = byteBuffer.getShort(); short1 != -1; short1 = byteBuffer.getShort()) {
			int int1 = short1 % 10;
			int int2 = short1 / 100;
			int int3 = (short1 - int2 * 10 * 10) / 10;
			short short2 = byteBuffer.getShort();
			int int4 = byteBuffer.getInt();
			short short3 = byteBuffer.getShort();
			int int5 = byteBuffer.position();
			IsoGridSquare square = this.getGridSquare(int1, int3, int2);
			if (square != null && short2 >= 0 && short2 < square.getObjects().size()) {
				IsoObject object = (IsoObject)square.getObjects().get(short2);
				if (int4 == object.getObjectName().hashCode()) {
					object.loadState(byteBuffer);
					assert byteBuffer.position() == int5 + short3;
				} else {
					byteBuffer.position(int5 + short3);
				}
			} else {
				byteBuffer.position(int5 + short3);
			}
		}
	}

	public void Blam(int int1, int int2) {
		for (int int3 = 0; int3 < 8; ++int3) {
			for (int int4 = 0; int4 < 10; ++int4) {
				for (int int5 = 0; int5 < 10; ++int5) {
					this.setSquare(int4, int5, int3, (IsoGridSquare)null);
				}
			}
		}

		this.blam = true;
	}

	private void BackupBlam(int int1, int int2, Exception exception) {
		File file = ZomboidFileSystem.instance.getFileInCurrentSave("blam");
		file.mkdirs();
		File file2;
		try {
			file2 = new File(file + File.separator + "map_" + int1 + "_" + int2 + "_error.txt");
			FileOutputStream fileOutputStream = new FileOutputStream(file2);
			PrintStream printStream = new PrintStream(fileOutputStream);
			exception.printStackTrace(printStream);
			printStream.close();
		} catch (Exception exception2) {
			exception2.printStackTrace();
		}

		file2 = ZomboidFileSystem.instance.getFileInCurrentSave("map_" + int1 + "_" + int2 + ".bin");
		if (file2.exists()) {
			File file3 = new File(file.getPath() + File.separator + "map_" + int1 + "_" + int2 + ".bin");
			try {
				copyFile(file2, file3);
			} catch (Exception exception3) {
				exception3.printStackTrace();
			}
		}
	}

	private static void copyFile(File file, File file2) throws IOException {
		if (!file2.exists()) {
			file2.createNewFile();
		}

		FileChannel fileChannel = null;
		FileChannel fileChannel2 = null;
		try {
			fileChannel = (new FileInputStream(file)).getChannel();
			fileChannel2 = (new FileOutputStream(file2)).getChannel();
			fileChannel2.transferFrom(fileChannel, 0L, fileChannel.size());
		} finally {
			if (fileChannel != null) {
				fileChannel.close();
			}

			if (fileChannel2 != null) {
				fileChannel2.close();
			}
		}
	}

	public ErosionData.Chunk getErosionData() {
		if (this.erosion == null) {
			this.erosion = new ErosionData.Chunk();
		}

		return this.erosion;
	}

	private static int newtiledefinitions(int int1, int int2) {
		byte byte1 = 1;
		return byte1 * 100 * 1000 + 10000 + int1 * 1000 + int2;
	}

	public static int Fix2x(IsoGridSquare square, int int1) {
		if (square != null && square.chunk != null) {
			if (square.chunk.bFixed2x) {
				return int1;
			} else {
				HashMap hashMap = IsoSpriteManager.instance.NamedMap;
				if (int1 >= newtiledefinitions(140, 48) && int1 <= newtiledefinitions(140, 51)) {
					return -1;
				} else if (int1 >= newtiledefinitions(8, 14) && int1 <= newtiledefinitions(8, 71) && int1 % 8 >= 6) {
					return -1;
				} else if (int1 == newtiledefinitions(92, 2)) {
					return int1 + 20;
				} else if (int1 == newtiledefinitions(92, 20)) {
					return int1 + 1;
				} else if (int1 == newtiledefinitions(92, 21)) {
					return int1 - 1;
				} else if (int1 >= newtiledefinitions(92, 26) && int1 <= newtiledefinitions(92, 29)) {
					return int1 + 6;
				} else if (int1 == newtiledefinitions(11, 16)) {
					return newtiledefinitions(11, 45);
				} else if (int1 == newtiledefinitions(11, 17)) {
					return newtiledefinitions(11, 43);
				} else if (int1 == newtiledefinitions(11, 18)) {
					return newtiledefinitions(11, 41);
				} else if (int1 == newtiledefinitions(11, 19)) {
					return newtiledefinitions(11, 47);
				} else if (int1 == newtiledefinitions(11, 24)) {
					return newtiledefinitions(11, 26);
				} else if (int1 == newtiledefinitions(11, 25)) {
					return newtiledefinitions(11, 27);
				} else if (int1 == newtiledefinitions(27, 42)) {
					return int1 + 1;
				} else if (int1 == newtiledefinitions(27, 43)) {
					return int1 - 1;
				} else if (int1 == newtiledefinitions(27, 44)) {
					return int1 + 3;
				} else if (int1 == newtiledefinitions(27, 47)) {
					return int1 - 2;
				} else if (int1 == newtiledefinitions(27, 45)) {
					return int1 + 1;
				} else if (int1 == newtiledefinitions(27, 46)) {
					return int1 - 2;
				} else if (int1 == newtiledefinitions(34, 4)) {
					return int1 + 1;
				} else if (int1 == newtiledefinitions(34, 5)) {
					return int1 - 1;
				} else if (int1 >= newtiledefinitions(14, 0) && int1 <= newtiledefinitions(14, 7)) {
					return -1;
				} else if (int1 >= newtiledefinitions(14, 8) && int1 <= newtiledefinitions(14, 12)) {
					return int1 + 72;
				} else if (int1 == newtiledefinitions(14, 13)) {
					return int1 + 71;
				} else if (int1 >= newtiledefinitions(14, 16) && int1 <= newtiledefinitions(14, 17)) {
					return int1 + 72;
				} else if (int1 == newtiledefinitions(14, 18)) {
					return int1 + 73;
				} else if (int1 == newtiledefinitions(14, 19)) {
					return int1 + 66;
				} else if (int1 == newtiledefinitions(14, 20)) {
					return -1;
				} else if (int1 == newtiledefinitions(14, 21)) {
					return newtiledefinitions(14, 89);
				} else if (int1 == newtiledefinitions(21, 0)) {
					return newtiledefinitions(125, 16);
				} else if (int1 == newtiledefinitions(21, 1)) {
					return newtiledefinitions(125, 32);
				} else if (int1 == newtiledefinitions(21, 2)) {
					return newtiledefinitions(125, 48);
				} else if (int1 == newtiledefinitions(26, 0)) {
					return newtiledefinitions(26, 6);
				} else if (int1 == newtiledefinitions(26, 6)) {
					return newtiledefinitions(26, 0);
				} else if (int1 == newtiledefinitions(26, 1)) {
					return newtiledefinitions(26, 7);
				} else if (int1 == newtiledefinitions(26, 7)) {
					return newtiledefinitions(26, 1);
				} else if (int1 == newtiledefinitions(26, 8)) {
					return newtiledefinitions(26, 14);
				} else if (int1 == newtiledefinitions(26, 14)) {
					return newtiledefinitions(26, 8);
				} else if (int1 == newtiledefinitions(26, 9)) {
					return newtiledefinitions(26, 15);
				} else if (int1 == newtiledefinitions(26, 15)) {
					return newtiledefinitions(26, 9);
				} else if (int1 == newtiledefinitions(26, 16)) {
					return newtiledefinitions(26, 22);
				} else if (int1 == newtiledefinitions(26, 22)) {
					return newtiledefinitions(26, 16);
				} else if (int1 == newtiledefinitions(26, 17)) {
					return newtiledefinitions(26, 23);
				} else if (int1 == newtiledefinitions(26, 23)) {
					return newtiledefinitions(26, 17);
				} else {
					int int2;
					if (int1 >= newtiledefinitions(148, 0) && int1 <= newtiledefinitions(148, 16)) {
						int2 = int1 - newtiledefinitions(148, 0);
						return newtiledefinitions(160, int2);
					} else if (int1 >= newtiledefinitions(42, 44) && int1 <= newtiledefinitions(42, 47) || int1 >= newtiledefinitions(42, 52) && int1 <= newtiledefinitions(42, 55)) {
						return -1;
					} else if (int1 == newtiledefinitions(43, 24)) {
						return int1 + 4;
					} else if (int1 == newtiledefinitions(43, 26)) {
						return int1 + 2;
					} else if (int1 == newtiledefinitions(43, 33)) {
						return int1 - 4;
					} else if (int1 == newtiledefinitions(44, 0)) {
						return newtiledefinitions(44, 1);
					} else if (int1 == newtiledefinitions(44, 1)) {
						return newtiledefinitions(44, 0);
					} else if (int1 == newtiledefinitions(44, 2)) {
						return newtiledefinitions(44, 7);
					} else if (int1 == newtiledefinitions(44, 3)) {
						return newtiledefinitions(44, 6);
					} else if (int1 == newtiledefinitions(44, 4)) {
						return newtiledefinitions(44, 5);
					} else if (int1 == newtiledefinitions(44, 5)) {
						return newtiledefinitions(44, 4);
					} else if (int1 == newtiledefinitions(44, 6)) {
						return newtiledefinitions(44, 3);
					} else if (int1 == newtiledefinitions(44, 7)) {
						return newtiledefinitions(44, 2);
					} else if (int1 == newtiledefinitions(44, 16)) {
						return newtiledefinitions(44, 45);
					} else if (int1 == newtiledefinitions(44, 17)) {
						return newtiledefinitions(44, 44);
					} else if (int1 == newtiledefinitions(44, 18)) {
						return newtiledefinitions(44, 46);
					} else if (int1 >= newtiledefinitions(44, 19) && int1 <= newtiledefinitions(44, 22)) {
						return int1 + 33;
					} else if (int1 == newtiledefinitions(44, 23)) {
						return newtiledefinitions(44, 47);
					} else if (int1 == newtiledefinitions(46, 8)) {
						return newtiledefinitions(46, 5);
					} else if (int1 == newtiledefinitions(46, 14)) {
						return newtiledefinitions(46, 10);
					} else if (int1 == newtiledefinitions(46, 15)) {
						return newtiledefinitions(46, 11);
					} else if (int1 == newtiledefinitions(46, 22)) {
						return newtiledefinitions(46, 14);
					} else if (int1 == newtiledefinitions(46, 23)) {
						return newtiledefinitions(46, 15);
					} else if (int1 == newtiledefinitions(46, 54)) {
						return newtiledefinitions(46, 55);
					} else if (int1 == newtiledefinitions(46, 55)) {
						return newtiledefinitions(46, 54);
					} else if (int1 == newtiledefinitions(106, 32)) {
						return newtiledefinitions(106, 34);
					} else if (int1 == newtiledefinitions(106, 34)) {
						return newtiledefinitions(106, 32);
					} else if (int1 != newtiledefinitions(47, 0) && int1 != newtiledefinitions(47, 4)) {
						if (int1 != newtiledefinitions(47, 1) && int1 != newtiledefinitions(47, 5)) {
							if (int1 >= newtiledefinitions(47, 8) && int1 <= newtiledefinitions(47, 13)) {
								return int1 + 8;
							} else if (int1 >= newtiledefinitions(47, 22) && int1 <= newtiledefinitions(47, 23)) {
								return int1 - 12;
							} else if (int1 >= newtiledefinitions(47, 44) && int1 <= newtiledefinitions(47, 47)) {
								return int1 + 4;
							} else if (int1 >= newtiledefinitions(47, 48) && int1 <= newtiledefinitions(47, 51)) {
								return int1 - 4;
							} else if (int1 == newtiledefinitions(48, 56)) {
								return newtiledefinitions(48, 58);
							} else if (int1 == newtiledefinitions(48, 58)) {
								return newtiledefinitions(48, 56);
							} else if (int1 == newtiledefinitions(52, 57)) {
								return newtiledefinitions(52, 58);
							} else if (int1 == newtiledefinitions(52, 58)) {
								return newtiledefinitions(52, 59);
							} else if (int1 == newtiledefinitions(52, 45)) {
								return newtiledefinitions(52, 44);
							} else if (int1 == newtiledefinitions(52, 46)) {
								return newtiledefinitions(52, 45);
							} else if (int1 == newtiledefinitions(54, 13)) {
								return newtiledefinitions(54, 18);
							} else if (int1 == newtiledefinitions(54, 15)) {
								return newtiledefinitions(54, 19);
							} else if (int1 == newtiledefinitions(54, 21)) {
								return newtiledefinitions(54, 16);
							} else if (int1 == newtiledefinitions(54, 22)) {
								return newtiledefinitions(54, 13);
							} else if (int1 == newtiledefinitions(54, 23)) {
								return newtiledefinitions(54, 17);
							} else if (int1 >= newtiledefinitions(67, 0) && int1 <= newtiledefinitions(67, 16)) {
								int2 = 64 + Rand.Next(16);
								return ((IsoSprite)hashMap.get("f_bushes_1_" + int2)).ID;
							} else if (int1 == newtiledefinitions(68, 6)) {
								return -1;
							} else if (int1 >= newtiledefinitions(68, 16) && int1 <= newtiledefinitions(68, 17)) {
								return ((IsoSprite)hashMap.get("d_plants_1_53")).ID;
							} else if (int1 >= newtiledefinitions(68, 18) && int1 <= newtiledefinitions(68, 23)) {
								int2 = Rand.Next(4) * 16 + Rand.Next(8);
								return ((IsoSprite)hashMap.get("d_plants_1_" + int2)).ID;
							} else {
								return int1 >= newtiledefinitions(79, 24) && int1 <= newtiledefinitions(79, 41) ? newtiledefinitions(81, int1 - newtiledefinitions(79, 24)) : int1;
							}
						} else {
							return int1 - 1;
						}
					} else {
						return int1 + 1;
					}
				}
			}
		} else {
			return int1;
		}
	}

	public static String Fix2x(String string) {
		int int1;
		if (Fix2xMap.isEmpty()) {
			HashMap hashMap = Fix2xMap;
			for (int1 = 48; int1 <= 51; ++int1) {
				hashMap.put("blends_streetoverlays_01_" + int1, "");
			}

			hashMap.put("fencing_01_14", "");
			hashMap.put("fencing_01_15", "");
			hashMap.put("fencing_01_22", "");
			hashMap.put("fencing_01_23", "");
			hashMap.put("fencing_01_30", "");
			hashMap.put("fencing_01_31", "");
			hashMap.put("fencing_01_38", "");
			hashMap.put("fencing_01_39", "");
			hashMap.put("fencing_01_46", "");
			hashMap.put("fencing_01_47", "");
			hashMap.put("fencing_01_62", "");
			hashMap.put("fencing_01_63", "");
			hashMap.put("fencing_01_70", "");
			hashMap.put("fencing_01_71", "");
			hashMap.put("fixtures_bathroom_02_2", "fixtures_bathroom_02_22");
			hashMap.put("fixtures_bathroom_02_20", "fixtures_bathroom_02_21");
			hashMap.put("fixtures_bathroom_02_21", "fixtures_bathroom_02_20");
			for (int1 = 26; int1 <= 29; ++int1) {
				hashMap.put("fixtures_bathroom_02_" + int1, "fixtures_bathroom_02_" + (int1 + 6));
			}

			hashMap.put("fixtures_counters_01_16", "fixtures_counters_01_45");
			hashMap.put("fixtures_counters_01_17", "fixtures_counters_01_43");
			hashMap.put("fixtures_counters_01_18", "fixtures_counters_01_41");
			hashMap.put("fixtures_counters_01_19", "fixtures_counters_01_47");
			hashMap.put("fixtures_counters_01_24", "fixtures_counters_01_26");
			hashMap.put("fixtures_counters_01_25", "fixtures_counters_01_27");
			for (int1 = 0; int1 <= 7; ++int1) {
				hashMap.put("fixtures_railings_01_" + int1, "");
			}

			for (int1 = 8; int1 <= 12; ++int1) {
				hashMap.put("fixtures_railings_01_" + int1, "fixtures_railings_01_" + (int1 + 72));
			}

			hashMap.put("fixtures_railings_01_13", "fixtures_railings_01_84");
			for (int1 = 16; int1 <= 17; ++int1) {
				hashMap.put("fixtures_railings_01_" + int1, "fixtures_railings_01_" + (int1 + 72));
			}

			hashMap.put("fixtures_railings_01_18", "fixtures_railings_01_91");
			hashMap.put("fixtures_railings_01_19", "fixtures_railings_01_85");
			hashMap.put("fixtures_railings_01_20", "");
			hashMap.put("fixtures_railings_01_21", "fixtures_railings_01_89");
			hashMap.put("floors_exterior_natural_01_0", "blends_natural_01_16");
			hashMap.put("floors_exterior_natural_01_1", "blends_natural_01_32");
			hashMap.put("floors_exterior_natural_01_2", "blends_natural_01_48");
			hashMap.put("floors_rugs_01_0", "floors_rugs_01_6");
			hashMap.put("floors_rugs_01_6", "floors_rugs_01_0");
			hashMap.put("floors_rugs_01_1", "floors_rugs_01_7");
			hashMap.put("floors_rugs_01_7", "floors_rugs_01_1");
			hashMap.put("floors_rugs_01_8", "floors_rugs_01_14");
			hashMap.put("floors_rugs_01_14", "floors_rugs_01_8");
			hashMap.put("floors_rugs_01_9", "floors_rugs_01_15");
			hashMap.put("floors_rugs_01_15", "floors_rugs_01_9");
			hashMap.put("floors_rugs_01_16", "floors_rugs_01_22");
			hashMap.put("floors_rugs_01_22", "floors_rugs_01_16");
			hashMap.put("floors_rugs_01_17", "floors_rugs_01_23");
			hashMap.put("floors_rugs_01_23", "floors_rugs_01_17");
			hashMap.put("furniture_bedding_01_42", "furniture_bedding_01_43");
			hashMap.put("furniture_bedding_01_43", "furniture_bedding_01_42");
			hashMap.put("furniture_bedding_01_44", "furniture_bedding_01_47");
			hashMap.put("furniture_bedding_01_47", "furniture_bedding_01_45");
			hashMap.put("furniture_bedding_01_45", "furniture_bedding_01_46");
			hashMap.put("furniture_bedding_01_46", "furniture_bedding_01_44");
			hashMap.put("furniture_tables_low_01_4", "furniture_tables_low_01_5");
			hashMap.put("furniture_tables_low_01_5", "furniture_tables_low_01_4");
			for (int1 = 0; int1 <= 5; ++int1) {
				hashMap.put("location_business_machinery_" + int1, "location_business_machinery_01_" + int1);
				hashMap.put("location_business_machinery_" + (int1 + 8), "location_business_machinery_01_" + (int1 + 8));
				hashMap.put("location_ business_machinery_" + int1, "location_business_machinery_01_" + int1);
				hashMap.put("location_ business_machinery_" + (int1 + 8), "location_business_machinery_01_" + (int1 + 8));
			}

			for (int1 = 44; int1 <= 47; ++int1) {
				hashMap.put("location_hospitality_sunstarmotel_01_" + int1, "");
			}

			for (int1 = 52; int1 <= 55; ++int1) {
				hashMap.put("location_hospitality_sunstarmotel_01_" + int1, "");
			}

			hashMap.put("location_hospitality_sunstarmotel_02_24", "location_hospitality_sunstarmotel_02_28");
			hashMap.put("location_hospitality_sunstarmotel_02_26", "location_hospitality_sunstarmotel_02_28");
			hashMap.put("location_hospitality_sunstarmotel_02_33", "location_hospitality_sunstarmotel_02_29");
			hashMap.put("location_restaurant_bar_01_0", "location_restaurant_bar_01_1");
			hashMap.put("location_restaurant_bar_01_1", "location_restaurant_bar_01_0");
			hashMap.put("location_restaurant_bar_01_2", "location_restaurant_bar_01_7");
			hashMap.put("location_restaurant_bar_01_3", "location_restaurant_bar_01_6");
			hashMap.put("location_restaurant_bar_01_4", "location_restaurant_bar_01_5");
			hashMap.put("location_restaurant_bar_01_5", "location_restaurant_bar_01_4");
			hashMap.put("location_restaurant_bar_01_6", "location_restaurant_bar_01_3");
			hashMap.put("location_restaurant_bar_01_7", "location_restaurant_bar_01_2");
			hashMap.put("location_restaurant_bar_01_16", "location_restaurant_bar_01_45");
			hashMap.put("location_restaurant_bar_01_17", "location_restaurant_bar_01_44");
			hashMap.put("location_restaurant_bar_01_18", "location_restaurant_bar_01_46");
			for (int1 = 19; int1 <= 22; ++int1) {
				hashMap.put("location_restaurant_bar_01_" + int1, "location_restaurant_bar_01_" + (int1 + 33));
			}

			hashMap.put("location_restaurant_bar_01_23", "location_restaurant_bar_01_47");
			hashMap.put("location_restaurant_pie_01_8", "location_restaurant_pie_01_5");
			hashMap.put("location_restaurant_pie_01_14", "location_restaurant_pie_01_10");
			hashMap.put("location_restaurant_pie_01_15", "location_restaurant_pie_01_11");
			hashMap.put("location_restaurant_pie_01_22", "location_restaurant_pie_01_14");
			hashMap.put("location_restaurant_pie_01_23", "location_restaurant_pie_01_15");
			hashMap.put("location_restaurant_pie_01_54", "location_restaurant_pie_01_55");
			hashMap.put("location_restaurant_pie_01_55", "location_restaurant_pie_01_54");
			hashMap.put("location_pizzawhirled_01_32", "location_pizzawhirled_01_34");
			hashMap.put("location_pizzawhirled_01_34", "location_pizzawhirled_01_32");
			hashMap.put("location_restaurant_seahorse_01_0", "location_restaurant_seahorse_01_1");
			hashMap.put("location_restaurant_seahorse_01_1", "location_restaurant_seahorse_01_0");
			hashMap.put("location_restaurant_seahorse_01_4", "location_restaurant_seahorse_01_5");
			hashMap.put("location_restaurant_seahorse_01_5", "location_restaurant_seahorse_01_4");
			for (int1 = 8; int1 <= 13; ++int1) {
				hashMap.put("location_restaurant_seahorse_01_" + int1, "location_restaurant_seahorse_01_" + (int1 + 8));
			}

			for (int1 = 22; int1 <= 23; ++int1) {
				hashMap.put("location_restaurant_seahorse_01_" + int1, "location_restaurant_seahorse_01_" + (int1 - 12));
			}

			for (int1 = 44; int1 <= 47; ++int1) {
				hashMap.put("location_restaurant_seahorse_01_" + int1, "location_restaurant_seahorse_01_" + (int1 + 4));
			}

			for (int1 = 48; int1 <= 51; ++int1) {
				hashMap.put("location_restaurant_seahorse_01_" + int1, "location_restaurant_seahorse_01_" + (int1 - 4));
			}

			hashMap.put("location_restaurant_spiffos_01_56", "location_restaurant_spiffos_01_58");
			hashMap.put("location_restaurant_spiffos_01_58", "location_restaurant_spiffos_01_56");
			hashMap.put("location_shop_fossoil_01_45", "location_shop_fossoil_01_44");
			hashMap.put("location_shop_fossoil_01_46", "location_shop_fossoil_01_45");
			hashMap.put("location_shop_fossoil_01_57", "location_shop_fossoil_01_58");
			hashMap.put("location_shop_fossoil_01_58", "location_shop_fossoil_01_59");
			hashMap.put("location_shop_greenes_01_13", "location_shop_greenes_01_18");
			hashMap.put("location_shop_greenes_01_15", "location_shop_greenes_01_19");
			hashMap.put("location_shop_greenes_01_21", "location_shop_greenes_01_16");
			hashMap.put("location_shop_greenes_01_22", "location_shop_greenes_01_13");
			hashMap.put("location_shop_greenes_01_23", "location_shop_greenes_01_17");
			hashMap.put("location_shop_greenes_01_67", "location_shop_greenes_01_70");
			hashMap.put("location_shop_greenes_01_68", "location_shop_greenes_01_67");
			hashMap.put("location_shop_greenes_01_70", "location_shop_greenes_01_71");
			hashMap.put("location_shop_greenes_01_75", "location_shop_greenes_01_78");
			hashMap.put("location_shop_greenes_01_76", "location_shop_greenes_01_75");
			hashMap.put("location_shop_greenes_01_78", "location_shop_greenes_01_79");
			for (int1 = 0; int1 <= 16; ++int1) {
				hashMap.put("vegetation_foliage_01_" + int1, "randBush");
			}

			hashMap.put("vegetation_groundcover_01_0", "blends_grassoverlays_01_16");
			hashMap.put("vegetation_groundcover_01_1", "blends_grassoverlays_01_8");
			hashMap.put("vegetation_groundcover_01_2", "blends_grassoverlays_01_0");
			hashMap.put("vegetation_groundcover_01_3", "blends_grassoverlays_01_64");
			hashMap.put("vegetation_groundcover_01_4", "blends_grassoverlays_01_56");
			hashMap.put("vegetation_groundcover_01_5", "blends_grassoverlays_01_48");
			hashMap.put("vegetation_groundcover_01_6", "");
			hashMap.put("vegetation_groundcover_01_44", "blends_grassoverlays_01_40");
			hashMap.put("vegetation_groundcover_01_45", "blends_grassoverlays_01_32");
			hashMap.put("vegetation_groundcover_01_46", "blends_grassoverlays_01_24");
			hashMap.put("vegetation_groundcover_01_16", "d_plants_1_53");
			hashMap.put("vegetation_groundcover_01_17", "d_plants_1_53");
			for (int1 = 18; int1 <= 23; ++int1) {
				hashMap.put("vegetation_groundcover_01_" + int1, "randPlant");
			}

			for (int1 = 20; int1 <= 23; ++int1) {
				hashMap.put("walls_exterior_house_01_" + int1, "walls_exterior_house_01_" + (int1 + 12));
				hashMap.put("walls_exterior_house_01_" + (int1 + 8), "walls_exterior_house_01_" + (int1 + 8 + 12));
			}

			for (int1 = 24; int1 <= 41; ++int1) {
				hashMap.put("walls_exterior_roofs_01_" + int1, "walls_exterior_roofs_03_" + int1);
			}
		}

		String string2 = (String)Fix2xMap.get(string);
		if (string2 == null) {
			return string;
		} else if ("randBush".equals(string2)) {
			int1 = 64 + Rand.Next(16);
			return "f_bushes_1_" + int1;
		} else if ("randPlant".equals(string2)) {
			int1 = Rand.Next(4) * 16 + Rand.Next(8);
			return "d_plants_1_" + int1;
		} else {
			return string2;
		}
	}

	public void addGeneratorPos(int int1, int int2, int int3) {
		if (this.generatorsTouchingThisChunk == null) {
			this.generatorsTouchingThisChunk = new ArrayList();
		}

		for (int int4 = 0; int4 < this.generatorsTouchingThisChunk.size(); ++int4) {
			IsoGameCharacter.Location location = (IsoGameCharacter.Location)this.generatorsTouchingThisChunk.get(int4);
			if (location.x == int1 && location.y == int2 && location.z == int3) {
				return;
			}
		}

		IsoGameCharacter.Location location2 = new IsoGameCharacter.Location(int1, int2, int3);
		this.generatorsTouchingThisChunk.add(location2);
	}

	public void removeGeneratorPos(int int1, int int2, int int3) {
		if (this.generatorsTouchingThisChunk != null) {
			for (int int4 = 0; int4 < this.generatorsTouchingThisChunk.size(); ++int4) {
				IsoGameCharacter.Location location = (IsoGameCharacter.Location)this.generatorsTouchingThisChunk.get(int4);
				if (location.x == int1 && location.y == int2 && location.z == int3) {
					this.generatorsTouchingThisChunk.remove(int4);
					--int4;
				}
			}
		}
	}

	public boolean isGeneratorPoweringSquare(int int1, int int2, int int3) {
		if (this.generatorsTouchingThisChunk == null) {
			return false;
		} else {
			for (int int4 = 0; int4 < this.generatorsTouchingThisChunk.size(); ++int4) {
				IsoGameCharacter.Location location = (IsoGameCharacter.Location)this.generatorsTouchingThisChunk.get(int4);
				if (IsoGenerator.isPoweringSquare(location.x, location.y, location.z, int1, int2, int3)) {
					return true;
				}
			}

			return false;
		}
	}

	public void checkForMissingGenerators() {
		if (this.generatorsTouchingThisChunk != null) {
			for (int int1 = 0; int1 < this.generatorsTouchingThisChunk.size(); ++int1) {
				IsoGameCharacter.Location location = (IsoGameCharacter.Location)this.generatorsTouchingThisChunk.get(int1);
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(location.x, location.y, location.z);
				if (square != null) {
					IsoGenerator generator = square.getGenerator();
					if (generator == null || !generator.isActivated()) {
						this.generatorsTouchingThisChunk.remove(int1);
						--int1;
					}
				}
			}
		}
	}

	public boolean isNewChunk() {
		return this.addZombies;
	}

	public void addSpawnedRoom(int int1) {
		if (!this.m_spawnedRooms.contains(int1)) {
			this.m_spawnedRooms.add(int1);
		}
	}

	public boolean isSpawnedRoom(int int1) {
		return this.m_spawnedRooms.contains(int1);
	}

	public IsoMetaGrid.Zone getScavengeZone() {
		if (this.m_scavengeZone != null) {
			return this.m_scavengeZone;
		} else {
			IsoMetaChunk metaChunk = IsoWorld.instance.getMetaGrid().getChunkData(this.wx, this.wy);
			if (metaChunk != null && metaChunk.numZones() > 0) {
				for (int int1 = 0; int1 < metaChunk.numZones(); ++int1) {
					IsoMetaGrid.Zone zone = metaChunk.getZone(int1);
					if ("DeepForest".equals(zone.type) || "Forest".equals(zone.type)) {
						this.m_scavengeZone = zone;
						return zone;
					}

					if ("Nav".equals(zone.type) || "Town".equals(zone.type)) {
						return null;
					}
				}
			}

			byte byte1 = 5;
			if (this.m_treeCount < byte1) {
				return null;
			} else {
				int int2 = 0;
				for (int int3 = -1; int3 <= 1; ++int3) {
					for (int int4 = -1; int4 <= 1; ++int4) {
						if (int4 != 0 || int3 != 0) {
							IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(this.wx + int4, this.wy + int3) : IsoWorld.instance.CurrentCell.getChunk(this.wx + int4, this.wy + int3);
							if (chunk != null && chunk.m_treeCount >= byte1) {
								++int2;
								if (int2 == 8) {
									byte byte2 = 10;
									this.m_scavengeZone = new IsoMetaGrid.Zone("", "Forest", this.wx * byte2, this.wy * byte2, 0, byte2, byte2);
									return this.m_scavengeZone;
								}
							}
						}
					}
				}

				return null;
			}
		}
	}

	public void resetForStore() {
		this.randomID = 0;
		this.revision = 0L;
		this.nextSplatIndex = 0;
		this.FloorBloodSplats.clear();
		this.FloorBloodSplatsFade.clear();
		this.jobType = IsoChunk.JobType.None;
		this.maxLevel = -1;
		this.bFixed2x = false;
		this.vehicles.clear();
		this.roomLights.clear();
		this.blam = false;
		this.lotheader = null;
		this.bLoaded = false;
		this.addZombies = false;
		this.physicsCheck = false;
		this.loadedPhysics = false;
		this.wx = 0;
		this.wy = 0;
		this.erosion = null;
		this.lootRespawnHour = -1;
		if (this.generatorsTouchingThisChunk != null) {
			this.generatorsTouchingThisChunk.clear();
		}

		this.m_treeCount = 0;
		this.m_scavengeZone = null;
		this.m_numberOfWaterTiles = 0;
		this.m_spawnedRooms.resetQuick();
		this.m_adjacentChunkLoadedCounter = 0;
		int int1;
		for (int1 = 0; int1 < this.squares.length; ++int1) {
			for (int int2 = 0; int2 < this.squares[0].length; ++int2) {
				this.squares[int1][int2] = null;
			}
		}

		for (int1 = 0; int1 < 4; ++int1) {
			this.lightCheck[int1] = true;
			this.bLightingNeverDone[int1] = true;
		}

		this.refs.clear();
		this.m_vehicleStorySpawnData = null;
		this.m_loadVehiclesObject = null;
		this.m_objectEmitterData.reset();
		MPStatistics.increaseStoredChunk();
	}

	public int getNumberOfWaterTiles() {
		return this.m_numberOfWaterTiles;
	}

	public void setRandomVehicleStoryToSpawnLater(VehicleStorySpawnData vehicleStorySpawnData) {
		this.m_vehicleStorySpawnData = vehicleStorySpawnData;
	}

	public boolean hasObjectAmbientEmitter(IsoObject object) {
		return this.m_objectEmitterData.hasObject(object);
	}

	public void addObjectAmbientEmitter(IsoObject object, ObjectAmbientEmitters.PerObjectLogic perObjectLogic) {
		this.m_objectEmitterData.addObject(object, perObjectLogic);
	}

	public void removeObjectAmbientEmitter(IsoObject object) {
		this.m_objectEmitterData.removeObject(object);
	}

	public static enum JobType {

		None,
		Convert,
		SoftReset;

		private static IsoChunk.JobType[] $values() {
			return new IsoChunk.JobType[]{None, Convert, SoftReset};
		}
	}
	private static enum PhysicsShapes {

		Solid,
		WallN,
		WallW,
		WallS,
		WallE,
		Tree,
		Floor;

		private static IsoChunk.PhysicsShapes[] $values() {
			return new IsoChunk.PhysicsShapes[]{Solid, WallN, WallW, WallS, WallE, Tree, Floor};
		}
	}

	private static class ChunkGetter implements IsoGridSquare.GetSquare {
		IsoChunk chunk;

		public IsoGridSquare getGridSquare(int int1, int int2, int int3) {
			int1 -= this.chunk.wx * 10;
			int2 -= this.chunk.wy * 10;
			return int1 >= 0 && int1 < 10 && int2 >= 0 && int2 < 10 && int3 >= 0 && int3 < 8 ? this.chunk.getGridSquare(int1, int2, int3) : null;
		}
	}

	private static class SanityCheck {
		public IsoChunk saveChunk;
		public String saveThread;
		public IsoChunk loadChunk;
		public String loadThread;
		public final ArrayList loadFile = new ArrayList();
		public String saveFile;

		public synchronized void beginSave(IsoChunk chunk) {
			if (this.saveChunk != null) {
				this.log("trying to save while already saving, wx,wy=" + chunk.wx + "," + chunk.wy);
			}

			if (this.loadChunk == chunk) {
				this.log("trying to save the same IsoChunk being loaded");
			}

			this.saveChunk = chunk;
			this.saveThread = Thread.currentThread().getName();
		}

		public synchronized void endSave(IsoChunk chunk) {
			this.saveChunk = null;
			this.saveThread = null;
		}

		public synchronized void beginLoad(IsoChunk chunk) {
			if (this.loadChunk != null) {
				this.log("trying to load while already loading, wx,wy=" + chunk.wx + "," + chunk.wy);
			}

			if (this.saveChunk == chunk) {
				this.log("trying to load the same IsoChunk being saved");
			}

			this.loadChunk = chunk;
			this.loadThread = Thread.currentThread().getName();
		}

		public synchronized void endLoad(IsoChunk chunk) {
			this.loadChunk = null;
			this.loadThread = null;
		}

		public synchronized void checkCRC(long long1, long long2) {
			if (long1 != long2) {
				this.log("CRC mismatch save=" + long1 + " load=" + long2);
			}
		}

		public synchronized void checkLength(long long1, long long2) {
			if (long1 != long2) {
				this.log("LENGTH mismatch save=" + long1 + " load=" + long2);
			}
		}

		public synchronized void beginLoadFile(String string) {
			if (string.equals(this.saveFile)) {
				this.log("attempted to load file being saved " + string);
			}

			this.loadFile.add(string);
		}

		public synchronized void endLoadFile(String string) {
			this.loadFile.remove(string);
		}

		public synchronized void beginSaveFile(String string) {
			if (this.loadFile.contains(string)) {
				this.log("attempted to save file being loaded " + string);
			}

			this.saveFile = string;
		}

		public synchronized void endSaveFile() {
			this.saveFile = null;
		}

		public synchronized void log(String string) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("SANITY CHECK FAIL! thread=\"" + Thread.currentThread().getName() + "\"\n");
			if (string != null) {
				stringBuilder.append(string + "\n");
			}

			if (this.saveChunk != null && this.saveChunk == this.loadChunk) {
				stringBuilder.append("exact same IsoChunk being saved + loaded\n");
			}

			if (this.saveChunk != null) {
				stringBuilder.append("save wx,wy=" + this.saveChunk.wx + "," + this.saveChunk.wy + " thread=\"" + this.saveThread + "\"\n");
			} else {
				stringBuilder.append("save chunk=null\n");
			}

			if (this.loadChunk != null) {
				stringBuilder.append("load wx,wy=" + this.loadChunk.wx + "," + this.loadChunk.wy + " thread=\"" + this.loadThread + "\"\n");
			} else {
				stringBuilder.append("load chunk=null\n");
			}

			String string2 = stringBuilder.toString();
			throw new RuntimeException(string2);
		}
	}

	private static class ChunkLock {
		public int wx;
		public int wy;
		public int count;
		public ReentrantReadWriteLock rw = new ReentrantReadWriteLock(true);

		public ChunkLock(int int1, int int2) {
			this.wx = int1;
			this.wy = int2;
		}

		public IsoChunk.ChunkLock set(int int1, int int2) {
			assert this.count == 0;
			this.wx = int1;
			this.wy = int2;
			return this;
		}

		public IsoChunk.ChunkLock ref() {
			++this.count;
			return this;
		}

		public int deref() {
			assert this.count > 0;
			return --this.count;
		}

		public void lockForReading() {
			this.rw.readLock().lock();
		}

		public void unlockForReading() {
			this.rw.readLock().unlock();
		}

		public void lockForWriting() {
			this.rw.writeLock().lock();
		}

		public void unlockForWriting() {
			this.rw.writeLock().unlock();
		}
	}
}
