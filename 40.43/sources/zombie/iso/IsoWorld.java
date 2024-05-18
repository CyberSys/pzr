package zombie.iso;

import fmod.fmod.FMODSoundEmitter;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.krka.kahlua.vm.KahluaTable;
import zombie.CollisionManager;
import zombie.FliesSound;
import zombie.FrameLoader;
import zombie.GameApplet;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MapCollisionData;
import zombie.ReanimatedPlayers;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.ZombieGroupManager;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.DummySoundEmitter;
import zombie.behaviors.survivor.orders.FollowOrder;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorFactory;
import zombie.characters.SurvivorGroup;
import zombie.characters.SurvivorPersonality;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.traits.TraitFactory;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.TilePropertyAliasMap;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.physics.WorldSimulation;
import zombie.core.skinnedmodel.AutoZombieManager;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.stash.StashSystem;
import zombie.core.utils.OnceEvery;
import zombie.debug.DebugLog;
import zombie.debug.LineDrawer;
import zombie.erosion.ErosionGlobals;
import zombie.gameStates.GameLoadingState;
import zombie.globalObjects.GlobalObjectLookup;
import zombie.inventory.ItemContainerFiller;
import zombie.inventory.ItemPickerJava;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.isoregion.IsoRegion;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.ObjectRenderEffects;
import zombie.iso.objects.RainManager;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteGrid;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.sprite.SkyBox;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.network.BodyDamageSync;
import zombie.network.ChunkRevisions;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetChecksum;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.popman.ZombiePopulationManager;
import zombie.radio.ZomboidRadio;
import zombie.randomizedWorld.RBBasic;
import zombie.randomizedWorld.RBBurnt;
import zombie.randomizedWorld.RBLooted;
import zombie.randomizedWorld.RBSafehouse;
import zombie.randomizedWorld.RandomizedBuildingBase;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.ScriptCharacter;
import zombie.ui.TutorialManager;
import zombie.util.AddCoopPlayer;
import zombie.util.SharedStrings;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleIDMap;
import zombie.vehicles.VehicleManager;


public class IsoWorld {
	private float globalTemperature = 0.0F;
	private String weather = "sunny";
	public IsoMetaGrid MetaGrid = new IsoMetaGrid();
	private ArrayList randomizedBuildingList = new ArrayList();
	private RandomizedBuildingBase RBBasic = new RBBasic();
	public SkyBox sky = null;
	public Helicopter helicopter = new Helicopter();
	public ArrayDeque freeEmitters = new ArrayDeque();
	public ArrayList currentEmitters = new ArrayList();
	int movex = 0;
	int movey = 0;
	public int x = 50;
	public int y = 50;
	public String playerCell = "suburbs1";
	public IsoCell CurrentCell;
	public static IsoWorld instance = new IsoWorld();
	public Stack Groups = new Stack();
	public int TotalSurvivorsDead = 0;
	public int TotalSurvivorNights = 0;
	public int SurvivorSurvivalRecord = 0;
	public HashMap SurvivorDescriptors = new HashMap();
	private int cellSurvivorSpawns;
	private int cellRemoteness;
	public ArrayList AddCoopPlayers = new ArrayList();
	boolean caboltoo = false;
	static IsoWorld.CompDistToPlayer compDistToPlayer = new IsoWorld.CompDistToPlayer();
	public static String mapPath = "media/";
	public static boolean mapUseJar = true;
	boolean bLoaded = false;
	public static HashMap PropertyValueMap = new HashMap();
	private static int WorldX = 0;
	private static int WorldY = 0;
	public IsoSpriteManager spriteManager;
	private SurvivorDesc luaDesc;
	private ArrayList luatraits;
	private int luaSpawnCellX = -1;
	private int luaSpawnCellY = -1;
	private int luaPosX = -1;
	private int luaPosY = -1;
	private int luaPosZ = -1;
	public static final int WorldVersion = 143;
	public static final int WorldVersion_Barricade = 87;
	public static final int WorldVersion_SandboxOptions = 88;
	public static final int WorldVersion_FliesSound = 121;
	public static final int WorldVersion_LootRespawn = 125;
	public static final int WorldVersion_OverlappingGenerators = 127;
	public static final int WorldVersion_ItemContainerIdenticalItems = 128;
	public static final int WorldVersion_VehicleSirenStartTime = 129;
	public static final int WorldVersion_CompostLastUpdated = 130;
	public static final int WorldVersion_DayLengthHours = 131;
	public static final int WorldVersion_LampOnPillar = 132;
	public static final int WorldVersion_AlarmClockRingSince = 134;
	public static final int WorldVersion_ClimateAdded = 135;
	public static final int WorldVersion_VehicleLightFocusing = 135;
	public static final int WorldVersion_GeneratorFuelFloat = 138;
	public static final int WorldVersion_InfectionTime = 142;
	public static final int WorldVersion_ClimateColors = 143;
	public static final int WorldVersion_ChunkVehicles = 91;
	public static final int WorldVersion_PlayerVehicleSeat = 91;
	public static int SavedWorldVersion = -1;
	public String[][] cellMap = new String[10][10];
	OnceEvery spriteChange = new OnceEvery(0.3F);
	public boolean bDrawWorld = true;
	int savePlayerCount = 0;
	private ArrayList zombieWithModel = new ArrayList();
	static OnceEvery e = new OnceEvery(0.4F, false);
	int worldX = 0;
	int worldY = 0;
	static SurvivorGroup TestGroup = null;
	public static boolean NoZombies = false;
	public static int TotalWorldVersion = -1;
	public static int saveoffsetx;
	public static int saveoffsety;
	public boolean bDoChunkMapUpdate = true;
	private long emitterUpdateMS;
	public boolean emitterUpdate;

	public IsoMetaGrid getMetaGrid() {
		return this.MetaGrid;
	}

	public IsoMetaGrid.Zone registerZone(String string, String string2, int int1, int int2, int int3, int int4, int int5) {
		return this.MetaGrid.registerZone(string, string2, int1, int2, int3, int4, int5);
	}

	public void removeZone(IsoMetaGrid.Zone zone) {
		this.MetaGrid.removeZone(zone);
	}

	public IsoMetaGrid.Zone registerZoneNoOverlap(String string, String string2, int int1, int int2, int int3, int int4, int int5) {
		return this.MetaGrid.registerZoneNoOverlap(string, string2, int1, int2, int3, int4, int5);
	}

	public void removeZonesForLotDirectory(String string) {
		this.MetaGrid.removeZonesForLotDirectory(string);
	}

	public BaseSoundEmitter getFreeEmitter() {
		Object object = null;
		if (this.freeEmitters.isEmpty()) {
			object = Core.SoundDisabled ? new DummySoundEmitter() : new FMODSoundEmitter();
		} else {
			object = (BaseSoundEmitter)this.freeEmitters.pop();
		}

		this.currentEmitters.add(object);
		return (BaseSoundEmitter)object;
	}

	public BaseSoundEmitter getFreeEmitter(float float1, float float2, float float3) {
		BaseSoundEmitter baseSoundEmitter = this.getFreeEmitter();
		baseSoundEmitter.setPos(float1, float2, float3);
		return baseSoundEmitter;
	}

	public IsoMetaGrid.Zone registerVehiclesZone(String string, String string2, int int1, int int2, int int3, int int4, int int5, KahluaTable kahluaTable) {
		return this.MetaGrid.registerVehiclesZone(string, string2, int1, int2, int3, int4, int5, kahluaTable);
	}

	public void checkVehiclesZones() {
		this.MetaGrid.checkVehiclesZones();
	}

	public static byte[] createChecksum(String string) throws Exception {
		FileInputStream fileInputStream = new FileInputStream(string);
		byte[] byteArray = new byte[1024];
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		int int1;
		do {
			int1 = fileInputStream.read(byteArray);
			if (int1 > 0) {
				messageDigest.update(byteArray, 0, int1);
			}
		} while (int1 != -1);

		fileInputStream.close();
		return messageDigest.digest();
	}

	public void setGameMode(String string) {
		Core.GameMode = string;
		Core.bLastStand = "LastStand".equals(string);
		Core.getInstance().setChallenge(false);
	}

	public String getGameMode() {
		return Core.GameMode;
	}

	public void setWorld(String string) {
		Core.GameSaveWorld = string.trim();
	}

	public void setMap(String string) {
		Core.GameMap = string;
	}

	public String getMap() {
		return Core.GameMap;
	}

	public static String getMD5Checksum(String string) throws Exception {
		byte[] byteArray = createChecksum(string);
		String string2 = "";
		for (int int1 = 0; int1 < byteArray.length; ++int1) {
			string2 = string2 + Integer.toString((byteArray[int1] & 255) + 256, 16).substring(1);
		}

		return string2;
	}

	public static boolean DoChecksumCheck(String string, String string2) {
		String string3 = "";
		try {
			string3 = getMD5Checksum(string);
			if (!string3.equals(string2)) {
				return false;
			}
		} catch (Exception exception) {
			string3 = "";
			try {
				string3 = getMD5Checksum("D:/Dropbox/Zomboid/zombie/build/classes/" + string);
			} catch (Exception exception2) {
				return false;
			}
		}

		return string3.equals(string2);
	}

	public static boolean DoChecksumCheck() {
		if (!DoChecksumCheck("zombie/GameWindow.class", "c4a62b8857f0fb6b9c103ff6ef127a9b")) {
			return false;
		} else if (!DoChecksumCheck("zombie/GameWindow$1.class", "5d93dc446b2dc49092fe4ecb5edf5f17")) {
			return false;
		} else if (!DoChecksumCheck("zombie/GameWindow$2.class", "a3e3d2c8cf6f0efaa1bf7f6ceb572073")) {
			return false;
		} else if (!DoChecksumCheck("zombie/gameStates/MainScreenState.class", "206848ba7cb764293dd2c19780263854")) {
			return false;
		} else if (!DoChecksumCheck("zombie/FrameLoader$1.class", "0ebfcc9557cc28d53aa982a71616bf5b")) {
			return false;
		} else {
			return DoChecksumCheck("zombie/FrameLoader.class", "d5b1f7b2886a499d848c204f6a815776");
		}
	}

	private void LoadRemotenessVars() {
	}

	public IsoObject getItemFromXYZIndexBuffer(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = this.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			return null;
		} else {
			byte byte1 = byteBuffer.get();
			return byte1 >= 0 && byte1 < square.getObjects().size() ? (IsoObject)square.getObjects().get(byte1) : null;
		}
	}

	public IsoWorld() {
		if (!GameServer.bServer) {
			this.sky = SkyBox.getInstance();
		}
	}

	public void CreateSurvivorGroup(IsoGridSquare square, IsoPlayer player) {
		int int1 = Rand.Next(4);
		SurvivorDesc survivorDesc = SurvivorFactory.CreateSurvivor();
		IsoSurvivor survivor = this.CreateRandomSurvivor(survivorDesc, square, player);
		if (survivor != null) {
			if (IsoPlayer.DemoMode) {
				int1 = 0;
			}

			for (int int2 = 0; int2 < int1; ++int2) {
				SurvivorDesc survivorDesc2 = SurvivorFactory.CreateSurvivor();
				IsoGridSquare square2 = square;
				do {
					square = square2.getCell().getGridSquare(square2.getX() + (Rand.Next(10) - 5), square2.getY() + (Rand.Next(10) - 5), square2.getZ());
					if (square != null) {
						square.setCachedIsFree(false);
					}
				}		 while (square == null || !square.isFree(true));

				IsoSurvivor survivor2 = this.CreateRandomSurvivor(survivorDesc2, square, player);
				if (survivor2 != null) {
					survivorDesc2.AddToGroup(survivorDesc.getGroup());
					survivorDesc2.getMetCount().put(survivorDesc.getID(), 100);
					survivorDesc.getMetCount().put(survivorDesc2.getID(), 100);
					survivorDesc2.getInstance().GiveOrder(new FollowOrder(survivorDesc2.getInstance(), survivorDesc.getInstance(), 3), true);
				}
			}
		}
	}

	public IsoSurvivor CreateRandomSurvivor(SurvivorDesc survivorDesc, IsoGridSquare square, IsoPlayer player) {
		int int1 = 0;
		if (square.getW() != null) {
			++int1;
		}

		if (square.getS() != null) {
			++int1;
		}

		if (square.getN() != null) {
			++int1;
		}

		if (square.getE() != null) {
			++int1;
		}

		if (int1 <= 1) {
			return null;
		} else {
			IsoSurvivor survivor = null;
			survivor = new IsoSurvivor(SurvivorPersonality.Personality.GunNut, survivorDesc, instance.CurrentCell, square.getX(), square.getY(), square.getZ());
			if (Rand.Next(4) == 0) {
				survivor.getInventory().AddItem("Base.Plank");
			}

			if (Rand.Next(4) == 0) {
				survivor.getInventory().AddItem("Base.Plank");
			}

			if (Rand.Next(4) == 0) {
				survivor.getInventory().AddItem("Base.Nails");
			}

			if (Rand.Next(4) == 0) {
				survivor.getInventory().AddItem("Base.Nails");
			}

			if (Rand.Next(4) == 0) {
				survivor.getInventory().AddItem("Base.Nails");
			}

			if (Rand.Next(4) == 0) {
				survivor.getInventory().AddItem("Base.Hammer");
			}

			if (Rand.Next(4) == 0) {
				survivor.getInventory().AddItem("Base.Sheet");
			}

			if (Rand.Next(4) == 0) {
				survivor.getInventory().AddItem("Base.Sheet");
			}

			if (Rand.Next(4) == 0) {
				survivor.getInventory().AddItem("Base.Sheet");
			}

			if (Rand.Next(4) == 0) {
				survivor.getInventory().AddItem("Base.Torch");
			}

			survivor.getInventory().AddItem("Base.WaterBottleFull");
			survivorDesc.setInstance(survivor);
			switch (Rand.Next(11)) {
			case 0: 
			
			case 1: 
				survivor.getInventory().AddItem("Base.Hammer");
				break;
			
			case 2: 
			
			case 3: 
				survivor.getInventory().AddItem("Base.Plank");
				break;
			
			case 4: 
				survivor.getInventory().AddItem("Base.BaseballBatNails");
				break;
			
			case 5: 
			
			case 6: 
				survivor.getInventory().AddItem("Base.Axe");
				break;
			
			case 7: 
			
			case 8: 
				survivor.getInventory().AddItem("Base.BaseballBat");
				break;
			
			case 9: 
			
			case 10: 
				survivor.getInventory().AddItem("Base.Shotgun");
				survivor.getInventory().AddItem("Base.ShotgunShells");
				survivor.getInventory().AddItem("Base.ShotgunShells");
				survivor.getInventory().AddItem("Base.ShotgunShells");
			
			}

			survivor.setAllowBehaviours(true);
			return survivor;
		}
	}

	public void CreateSwarm(int int1, int int2, int int3, int int4, int int5) {
	}

	public void ForceKillAllZombies() {
		GameTime.getInstance().RemoveZombiesIndiscriminate(1000);
	}

	public static int readInt(RandomAccessFile randomAccessFile) throws EOFException, IOException {
		int int1 = randomAccessFile.read();
		int int2 = randomAccessFile.read();
		int int3 = randomAccessFile.read();
		int int4 = randomAccessFile.read();
		if ((int1 | int2 | int3 | int4) < 0) {
			throw new EOFException();
		} else {
			return (int1 << 0) + (int2 << 8) + (int3 << 16) + (int4 << 24);
		}
	}

	public static String readString(RandomAccessFile randomAccessFile) throws EOFException, IOException {
		String string = randomAccessFile.readLine();
		return string;
	}

	public void LoadTileDefinitions(IsoSpriteManager spriteManager, String string, int int1) {
		DebugLog.log("tiledef: loading " + string);
		RandomAccessFile randomAccessFile = null;
		try {
			File file = new File(string);
			randomAccessFile = new RandomAccessFile(file.getAbsolutePath(), "r");
			int int2 = readInt(randomAccessFile);
			int int3 = readInt(randomAccessFile);
			int int4 = readInt(randomAccessFile);
			SharedStrings sharedStrings = new SharedStrings();
			boolean boolean1 = false;
			boolean boolean2 = false;
			ArrayList arrayList = new ArrayList();
			HashMap hashMap = new HashMap();
			HashMap hashMap2 = new HashMap();
			String[] stringArray = new String[]{"N", "E", "S", "W"};
			for (int int5 = 0; int5 < stringArray.length; ++int5) {
				hashMap2.put(stringArray[int5], new ArrayList());
			}

			ArrayList arrayList2 = new ArrayList();
			HashMap hashMap3 = new HashMap();
			int int6 = 0;
			int int7 = 0;
			int int8 = 0;
			int int9 = 0;
			HashSet hashSet = new HashSet();
			String string2;
			label1317: for (int int10 = 0; int10 < int4; ++int10) {
				String string3 = readString(randomAccessFile);
				string2 = string3.trim();
				String string4 = readString(randomAccessFile);
				int int11 = readInt(randomAccessFile);
				int int12 = readInt(randomAccessFile);
				int int13 = readInt(randomAccessFile);
				int int14 = readInt(randomAccessFile);
				int int15;
				IsoSprite sprite;
				for (int15 = 0; int15 < int14; ++int15) {
					if (int1 < 2) {
						sprite = spriteManager.AddSprite(string2 + "_" + int15, int1 * 100 * 1000 + 10000 + int13 * 1000 + int15);
					} else {
						sprite = spriteManager.AddSprite(string2 + "_" + int15, int1 * 512 * 512 + int13 * 512 + int15);
					}

					arrayList.add(sprite);
					sprite.setName(string2 + "_" + int15);
					if (sprite.name.contains("damaged") || sprite.name.contains("trash_")) {
						sprite.attachedFloor = true;
						sprite.getProperties().Set("attachedFloor", "true");
					}

					if (sprite.name.startsWith("f_bushes") && int15 <= 31) {
						sprite.isBush = true;
						sprite.attachedFloor = true;
						sprite.getProperties().isBush = true;
					}

					int int16 = readInt(randomAccessFile);
					for (int int17 = 0; int17 < int16; ++int17) {
						string3 = readString(randomAccessFile);
						String string5 = string3.trim();
						string3 = readString(randomAccessFile);
						String string6 = string3.trim();
						IsoObjectType objectType = IsoObjectType.FromString(string5);
						if (objectType == IsoObjectType.MAX) {
							string5 = sharedStrings.get(string5);
							if (string5.equals("firerequirement")) {
								sprite.firerequirement = Integer.parseInt(string6);
							} else if (string5.equals("fireRequirement")) {
								sprite.firerequirement = Integer.parseInt(string6);
							} else if (string5.equals("BurntTile")) {
								sprite.burntTile = string6;
							} else if (string5.equals("ForceAmbient")) {
								sprite.forceAmbient = true;
								sprite.getProperties().Set(string5, string6);
							} else if (string5.equals("solidfloor")) {
								sprite.solidfloor = true;
								sprite.getProperties().Set(string5, string6);
							} else if (string5.equals("canBeRemoved")) {
								sprite.canBeRemoved = true;
								sprite.getProperties().Set(string5, string6);
							} else if (string5.equals("attachedFloor")) {
								sprite.attachedFloor = true;
								sprite.getProperties().Set(string5, string6);
							} else if (string5.equals("cutW")) {
								sprite.cutW = true;
								sprite.getProperties().Set(string5, string6);
							} else if (string5.equals("cutN")) {
								sprite.cutN = true;
								sprite.getProperties().Set(string5, string6);
							} else if (string5.equals("solid")) {
								sprite.solid = true;
								sprite.getProperties().Set(string5, string6);
							} else if (string5.equals("solidTrans")) {
								sprite.solidTrans = true;
								sprite.getProperties().Set(string5, string6);
							} else if (string5.equals("invisible")) {
								sprite.invisible = true;
								sprite.getProperties().Set(string5, string6);
							} else if (string5.equals("alwaysDraw")) {
								sprite.alwaysDraw = true;
								sprite.getProperties().Set(string5, string6);
							} else if ("FloorHeight".equals(string5)) {
								if ("OneThird".equals(string6)) {
									sprite.getProperties().Set(IsoFlagType.FloorHeightOneThird);
								} else if ("TwoThirds".equals(string6)) {
									sprite.getProperties().Set(IsoFlagType.FloorHeightTwoThirds);
								}
							} else if (string5.equals("MoveWithWind")) {
								sprite.moveWithWind = true;
								sprite.getProperties().Set(string5, string6);
							} else if (string5.equals("WindType")) {
								sprite.windType = Integer.parseInt(string6);
								sprite.getProperties().Set(string5, string6);
							} else {
								sprite.getProperties().Set(string5, string6);
								if ("WindowN".equals(string5) || "WindowW".equals(string5)) {
									sprite.getProperties().Set(string5, string6, false);
								}
							}
						} else {
							if (sprite.getType() != IsoObjectType.doorW && sprite.getType() != IsoObjectType.doorN || objectType != IsoObjectType.wall) {
								sprite.setType(objectType);
							}

							if (objectType == IsoObjectType.doorW) {
								sprite.getProperties().Set(IsoFlagType.doorW);
							} else if (objectType == IsoObjectType.doorN) {
								sprite.getProperties().Set(IsoFlagType.doorN);
							}
						}

						if (objectType == IsoObjectType.tree) {
							if (sprite.name.equals("e_riverbirch_1_1")) {
								string6 = "1";
							}

							sprite.getProperties().Set("tree", string6);
							sprite.getProperties().UnSet(IsoFlagType.solid);
							sprite.getProperties().Set(IsoFlagType.blocksight);
							int int18 = Integer.parseInt(string6);
							if (string2.startsWith("vegetation_trees")) {
								int18 = 4;
							}

							if (int18 < 1) {
								int18 = 1;
							}

							if (int18 > 4) {
								int18 = 4;
							}

							if (int18 == 1 || int18 == 2) {
								sprite.getProperties().UnSet(IsoFlagType.blocksight);
							}
						}

						if (string5.equals("interior") && string6.equals("false")) {
							sprite.getProperties().Set(IsoFlagType.exterior);
						}

						if (string5.equals("HoppableN")) {
							sprite.getProperties().Set(IsoFlagType.collideN);
							sprite.getProperties().Set(IsoFlagType.canPathN);
							sprite.getProperties().Set(IsoFlagType.transparentN, "");
						}

						if (string5.equals("HoppableW")) {
							sprite.getProperties().Set(IsoFlagType.collideW);
							sprite.getProperties().Set(IsoFlagType.canPathW);
							sprite.getProperties().Set(IsoFlagType.transparentW, "");
						}

						if (string5.equals("WallN")) {
							sprite.getProperties().Set(IsoFlagType.collideN);
							sprite.getProperties().Set(IsoFlagType.cutN);
							sprite.setType(IsoObjectType.wall);
							sprite.cutN = true;
						} else if (string5.equals("WallNTrans")) {
							sprite.getProperties().Set(IsoFlagType.collideN, "");
							sprite.getProperties().Set(IsoFlagType.cutN, "");
							sprite.getProperties().Set(IsoFlagType.transparentN, "");
							sprite.setType(IsoObjectType.wall);
							sprite.cutN = true;
						} else if (string5.equals("WallW")) {
							sprite.getProperties().Set(IsoFlagType.collideW);
							sprite.getProperties().Set(IsoFlagType.cutW);
							sprite.setType(IsoObjectType.wall);
							sprite.cutW = true;
						} else if (string5.equals("windowN")) {
							sprite.getProperties().Set("WindowN", "WindowN");
							sprite.getProperties().Set("WindowN", "WindowN", false);
						} else if (string5.equals("windowW")) {
							sprite.getProperties().Set("WindowW", "WindowW");
							sprite.getProperties().Set("WindowW", "WindowW", false);
						} else if (string5.equals("WallWTrans")) {
							sprite.getProperties().Set(IsoFlagType.collideW, "");
							sprite.getProperties().Set(IsoFlagType.transparentW, "");
							sprite.getProperties().Set(IsoFlagType.cutW, "");
							sprite.setType(IsoObjectType.wall);
							sprite.cutW = true;
						} else if (string5.equals("DoorWallN")) {
							sprite.getProperties().Set(IsoFlagType.cutN);
							sprite.getProperties().Set("DoorWallN", "DoorWallN");
							sprite.cutN = true;
						} else if (string5.equals("DoorWallW")) {
							sprite.getProperties().Set(IsoFlagType.cutW);
							sprite.getProperties().Set("DoorWallW", "DoorWallW");
							sprite.cutW = true;
						} else if (string5.equals("WallNW")) {
							sprite.getProperties().Set(IsoFlagType.collideN, "");
							sprite.getProperties().Set(IsoFlagType.cutN, "");
							sprite.getProperties().Set(IsoFlagType.collideW, "");
							sprite.getProperties().Set(IsoFlagType.cutW, "");
							sprite.setType(IsoObjectType.wall);
							sprite.cutW = true;
							sprite.cutN = true;
						} else if (string5.equals("WallNWTrans")) {
							sprite.getProperties().Set(IsoFlagType.collideN, "");
							sprite.getProperties().Set(IsoFlagType.cutN, "");
							sprite.getProperties().Set(IsoFlagType.collideW, "");
							sprite.getProperties().Set(IsoFlagType.transparentN, "");
							sprite.getProperties().Set(IsoFlagType.transparentW, "");
							sprite.getProperties().Set(IsoFlagType.cutW, "");
							sprite.setType(IsoObjectType.wall);
							sprite.cutW = true;
							sprite.cutN = true;
						} else if (string5.equals("WallSE")) {
							sprite.getProperties().Set(IsoFlagType.cutW, "");
							sprite.getProperties().Set(IsoFlagType.WallSE);
							sprite.getProperties().Set("WallSE", "WallSE");
							sprite.cutW = true;
						} else if (string5.equals("WindowW")) {
							sprite.getProperties().Set(IsoFlagType.canPathW, "");
							sprite.getProperties().Set(IsoFlagType.collideW, "");
							sprite.getProperties().Set(IsoFlagType.cutW, "");
							sprite.getProperties().Set(IsoFlagType.transparentW, "");
							sprite.setType(IsoObjectType.windowFW);
							if (sprite.getProperties().Is(IsoFlagType.HoppableW)) {
								if (Core.bDebug) {
									DebugLog.log("ERROR: WindowW sprite shouldn\'t have HoppableW (" + sprite.getName() + ")");
								}

								sprite.getProperties().UnSet(IsoFlagType.HoppableW);
							}

							sprite.cutW = true;
						} else if (string5.equals("WindowN")) {
							sprite.getProperties().Set(IsoFlagType.canPathN, "");
							sprite.getProperties().Set(IsoFlagType.collideN, "");
							sprite.getProperties().Set(IsoFlagType.cutN, "");
							sprite.getProperties().Set(IsoFlagType.transparentN, "");
							sprite.setType(IsoObjectType.windowFN);
							if (sprite.getProperties().Is(IsoFlagType.HoppableN)) {
								if (Core.bDebug) {
									DebugLog.log("ERROR: WindowN sprite shouldn\'t have HoppableN (" + sprite.getName() + ")");
								}

								sprite.getProperties().UnSet(IsoFlagType.HoppableN);
							}

							sprite.cutN = true;
						} else if (string5.equals("UnbreakableWindowW")) {
							sprite.getProperties().Set(IsoFlagType.canPathW, "");
							sprite.getProperties().Set(IsoFlagType.collideW, "");
							sprite.getProperties().Set(IsoFlagType.cutW, "");
							sprite.getProperties().Set(IsoFlagType.transparentW, "");
							sprite.getProperties().Set(IsoFlagType.collideW, "");
							sprite.setType(IsoObjectType.wall);
							sprite.cutW = true;
						} else if (string5.equals("UnbreakableWindowN")) {
							sprite.getProperties().Set(IsoFlagType.canPathN, "");
							sprite.getProperties().Set(IsoFlagType.collideN, "");
							sprite.getProperties().Set(IsoFlagType.cutN, "");
							sprite.getProperties().Set(IsoFlagType.transparentN, "");
							sprite.getProperties().Set(IsoFlagType.collideN, "");
							sprite.setType(IsoObjectType.wall);
							sprite.cutN = true;
						} else if (string5.equals("UnbreakableWindowNW")) {
							sprite.getProperties().Set(IsoFlagType.cutN, "");
							sprite.getProperties().Set(IsoFlagType.transparentN, "");
							sprite.getProperties().Set(IsoFlagType.collideN, "");
							sprite.getProperties().Set(IsoFlagType.cutN, "");
							sprite.getProperties().Set(IsoFlagType.collideW, "");
							sprite.getProperties().Set(IsoFlagType.cutW, "");
							sprite.setType(IsoObjectType.wall);
							sprite.cutW = true;
							sprite.cutN = true;
						} else if ("NoWallLighting".equals(string5)) {
							sprite.getProperties().Set(IsoFlagType.NoWallLighting);
						} else if ("ForceAmbient".equals(string5)) {
							sprite.getProperties().Set(IsoFlagType.ForceAmbient);
						}

						if (string5.equals("name")) {
							sprite.setParentObjectName(string6);
						}
					}

					if (sprite.getProperties().Is("lightR") || sprite.getProperties().Is("lightG") || sprite.getProperties().Is("lightB")) {
						if (!sprite.getProperties().Is("lightR")) {
							sprite.getProperties().Set("lightR", "0");
						}

						if (!sprite.getProperties().Is("lightG")) {
							sprite.getProperties().Set("lightG", "0");
						}

						if (!sprite.getProperties().Is("lightB")) {
							sprite.getProperties().Set("lightB", "0");
						}
					}

					sprite.getProperties().CreateKeySet();
					if (Core.bDebug && sprite.getProperties().Is("SmashedTileOffset") && !sprite.getProperties().Is("GlassRemovedOffset")) {
						DebugLog.log("ERROR: Window sprite has SmashedTileOffset but no GlassRemovedOffset (" + sprite.getName() + ")");
					}
				}

				hashMap.clear();
				String string7;
				for (int15 = 0; int15 < arrayList.size(); ++int15) {
					sprite = (IsoSprite)arrayList.get(int15);
					if (sprite.getProperties().Is("StopCar")) {
						sprite.setType(IsoObjectType.isMoveAbleObject);
					}

					if (sprite.getProperties().Is("IsMoveAble")) {
						if (sprite.getProperties().Is("CustomName") && !sprite.getProperties().Val("CustomName").equals("")) {
							++int6;
							if (sprite.getProperties().Is("GroupName")) {
								string7 = sprite.getProperties().Val("GroupName") + " " + sprite.getProperties().Val("CustomName");
								if (!hashMap.containsKey(string7)) {
									hashMap.put(string7, new ArrayList());
								}

								((ArrayList)hashMap.get(string7)).add(sprite);
								hashSet.add(string7);
							} else {
								if (!hashMap3.containsKey(string2)) {
									hashMap3.put(string2, new ArrayList());
								}

								if (!((ArrayList)hashMap3.get(string2)).contains(sprite.getProperties().Val("CustomName"))) {
									((ArrayList)hashMap3.get(string2)).add(sprite.getProperties().Val("CustomName"));
								}

								++int7;
								hashSet.add(sprite.getProperties().Val("CustomName"));
							}
						} else {
							DebugLog.log("[IMPORTANT] MOVABLES: Object has no custom name defined: sheet = " + string2);
						}
					}
				}

				Iterator iterator = hashMap.entrySet().iterator();
				while (true) {
					while (true) {
						while (true) {
							ArrayList arrayList3;
							boolean boolean3;
							int int19;
							boolean boolean4;
							IsoSprite sprite2;
							do {
								if (!iterator.hasNext()) {
									arrayList.clear();
									continue label1317;
								}

								Entry entry = (Entry)iterator.next();
								string7 = (String)entry.getKey();
								if (!hashMap3.containsKey(string2)) {
									hashMap3.put(string2, new ArrayList());
								}

								if (!((ArrayList)hashMap3.get(string2)).contains(string7)) {
									((ArrayList)hashMap3.get(string2)).add(string7);
								}

								arrayList3 = (ArrayList)entry.getValue();
								if (arrayList3.size() == 1) {
									DebugLog.log("MOVABLES: Object has only one face defined for group: (" + string7 + ") sheet = " + string2);
								}

								if (arrayList3.size() == 3) {
									DebugLog.log("MOVABLES: Object only has 3 sprites, _might_ have a error in settings, group: (" + string7 + ") sheet = " + string2);
								}

								for (int int20 = 0; int20 < stringArray.length; ++int20) {
									((ArrayList)hashMap2.get(stringArray[int20])).clear();
								}

								boolean3 = ((IsoSprite)arrayList3.get(0)).getProperties().Is("SpriteGridPos") && !((IsoSprite)arrayList3.get(0)).getProperties().Val("SpriteGridPos").equals("None");
								boolean4 = true;
								for (int19 = 0; int19 < arrayList3.size(); ++int19) {
									sprite2 = (IsoSprite)arrayList3.get(int19);
									boolean boolean5 = sprite2.getProperties().Is("SpriteGridPos") && !sprite2.getProperties().Val("SpriteGridPos").equals("None");
									if (boolean3 != boolean5) {
										boolean4 = false;
										DebugLog.log("MOVABLES: Difference in SpriteGrid settings for members of group: (" + string7 + ") sheet = " + string2);
										break;
									}

									if (!sprite2.getProperties().Is("Facing")) {
										boolean4 = false;
									} else {
										String string8 = sprite2.getProperties().Val("Facing");
										byte byte1 = -1;
										switch (string8.hashCode()) {
										case 69: 
											if (string8.equals("E")) {
												byte1 = 1;
											}

											break;
										
										case 78: 
											if (string8.equals("N")) {
												byte1 = 0;
											}

											break;
										
										case 83: 
											if (string8.equals("S")) {
												byte1 = 2;
											}

											break;
										
										case 87: 
											if (string8.equals("W")) {
												byte1 = 3;
											}

										
										}

										switch (byte1) {
										case 0: 
											((ArrayList)hashMap2.get("N")).add(sprite2);
											break;
										
										case 1: 
											((ArrayList)hashMap2.get("E")).add(sprite2);
											break;
										
										case 2: 
											((ArrayList)hashMap2.get("S")).add(sprite2);
											break;
										
										case 3: 
											((ArrayList)hashMap2.get("W")).add(sprite2);
											break;
										
										default: 
											DebugLog.log("MOVABLES: Invalid face (" + sprite2.getProperties().Val("Facing") + ") for group: (" + string7 + ") sheet = " + string2);
											boolean4 = false;
										
										}
									}

									if (!boolean4) {
										DebugLog.log("MOVABLES: Not all members have a valid face defined for group: (" + string7 + ") sheet = " + string2);
										break;
									}
								}
							}					 while (!boolean4);

							int int21;
							ArrayList arrayList4;
							if (!boolean3) {
								if (arrayList3.size() > 4) {
									DebugLog.log("MOVABLES: Object has too many faces defined for group: (" + string7 + ") sheet = " + string2);
								} else {
									for (int19 = 0; int19 < stringArray.length; ++int19) {
										if (((ArrayList)hashMap2.get(stringArray[int19])).size() > 1) {
											DebugLog.log("MOVABLES: " + stringArray[int19] + " face defined more than once for group: (" + string7 + ") sheet = " + string2);
											boolean4 = false;
										}
									}

									if (boolean4) {
										++int8;
										for (int19 = 0; int19 < arrayList3.size(); ++int19) {
											sprite2 = (IsoSprite)arrayList3.get(int19);
											for (int21 = 0; int21 < stringArray.length; ++int21) {
												arrayList4 = (ArrayList)hashMap2.get(stringArray[int21]);
												if (arrayList4.size() > 0 && arrayList4.get(0) != sprite2) {
													sprite2.getProperties().Set(stringArray[int21] + "offset", Integer.toString(arrayList.indexOf(arrayList4.get(0)) - arrayList.indexOf(sprite2)));
												}
											}
										}
									}
								}
							} else {
								int19 = 0;
								IsoSpriteGrid[] spriteGridArray = new IsoSpriteGrid[stringArray.length];
								int int22;
								IsoSprite sprite3;
								label1286: for (int21 = 0; int21 < stringArray.length; ++int21) {
									arrayList4 = (ArrayList)hashMap2.get(stringArray[int21]);
									if (arrayList4.size() > 0) {
										if (int19 == 0) {
											int19 = arrayList4.size();
										}

										if (int19 != arrayList4.size()) {
											DebugLog.log("MOVABLES: Sprite count mismatch for multi sprite movable, group: (" + string7 + ") sheet = " + string2);
											boolean4 = false;
											break;
										}

										arrayList2.clear();
										int int23 = -1;
										int22 = -1;
										Iterator iterator2 = arrayList4.iterator();
										while (true) {
											String string9;
											String[] stringArray2;
											int int24;
											int int25;
											if (iterator2.hasNext()) {
												sprite3 = (IsoSprite)iterator2.next();
												string9 = sprite3.getProperties().Val("SpriteGridPos");
												if (!arrayList2.contains(string9)) {
													arrayList2.add(string9);
													stringArray2 = string9.split(",");
													if (stringArray2.length == 2) {
														int24 = Integer.parseInt(stringArray2[0]);
														int25 = Integer.parseInt(stringArray2[1]);
														if (int24 > int23) {
															int23 = int24;
														}

														if (int25 > int22) {
															int22 = int25;
														}

														continue;
													}

													DebugLog.log("MOVABLES: SpriteGrid position error for multi sprite movable, group: (" + string7 + ") sheet = " + string2);
													boolean4 = false;
												} else {
													DebugLog.log("MOVABLES: double SpriteGrid position (" + string9 + ") for multi sprite movable, group: (" + string7 + ") sheet = " + string2);
													boolean4 = false;
												}
											}

											if (int23 == -1 || int22 == -1 || (int23 + 1) * (int22 + 1) != arrayList4.size()) {
												DebugLog.log("MOVABLES: SpriteGrid dimensions error for multi sprite movable, group: (" + string7 + ") sheet = " + string2);
												boolean4 = false;
												break label1286;
											}

											if (!boolean4) {
												break label1286;
											}

											spriteGridArray[int21] = new IsoSpriteGrid(int23 + 1, int22 + 1);
											iterator2 = arrayList4.iterator();
											while (iterator2.hasNext()) {
												sprite3 = (IsoSprite)iterator2.next();
												string9 = sprite3.getProperties().Val("SpriteGridPos");
												stringArray2 = string9.split(",");
												int24 = Integer.parseInt(stringArray2[0]);
												int25 = Integer.parseInt(stringArray2[1]);
												spriteGridArray[int21].setSprite(int24, int25, sprite3);
											}

											if (!spriteGridArray[int21].validate()) {
												DebugLog.log("MOVABLES: SpriteGrid didn\'t validate for multi sprite movable, group: (" + string7 + ") sheet = " + string2);
												boolean4 = false;
												break label1286;
											}

											break;
										}
									}
								}

								if (boolean4 && int19 != 0) {
									++int9;
									for (int21 = 0; int21 < stringArray.length; ++int21) {
										IsoSpriteGrid spriteGrid = spriteGridArray[int21];
										if (spriteGrid != null) {
											IsoSprite[] spriteArray = spriteGrid.getSprites();
											int22 = spriteArray.length;
											for (int int26 = 0; int26 < int22; ++int26) {
												sprite3 = spriteArray[int26];
												sprite3.setSpriteGrid(spriteGrid);
												for (int int27 = 0; int27 < stringArray.length; ++int27) {
													if (int27 != int21 && spriteGridArray[int27] != null) {
														sprite3.getProperties().Set(stringArray[int27] + "offset", Integer.toString(arrayList.indexOf(spriteGridArray[int27].getAnchorSprite()) - arrayList.indexOf(sprite3)));
													}
												}
											}
										}
									}
								} else {
									DebugLog.log("MOVABLES: Error in multi sprite movable, group: (" + string7 + ") sheet = " + string2);
								}
							}
						}
					}
				}
			}

			if (boolean2) {
				ArrayList arrayList5 = new ArrayList(hashSet);
				Collections.sort(arrayList5);
				Iterator iterator3 = arrayList5.iterator();
				while (iterator3.hasNext()) {
					string2 = (String)iterator3.next();
					System.out.println(string2.replaceAll(" ", "_").replaceAll("-", "_").replaceAll("\'", "").replaceAll("\\.", "") + " = \"" + string2 + "\",");
				}
			}

			if (boolean1) {
				try {
					this.saveMovableStats(hashMap3, int1, int7, int8, int9, int6);
				} catch (Exception exception) {
				}
			}
		} catch (Exception exception2) {
			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception2);
		} finally {
			try {
				randomAccessFile.close();
			} catch (Exception exception3) {
			}
		}
	}

	private void GenerateTilePropertyLookupTables() {
		TilePropertyAliasMap.instance.Generate(PropertyValueMap);
		PropertyValueMap.clear();
	}

	public void LoadTileDefinitionsPropertyStrings(IsoSpriteManager spriteManager, String string, int int1) {
		DebugLog.log("tiledef: loading " + string);
		if (!GameServer.bServer) {
			Thread.yield();
			Core.getInstance().DoFrameReady();
		}

		RandomAccessFile randomAccessFile = null;
		try {
			File file = new File(string);
			randomAccessFile = new RandomAccessFile(file.getAbsolutePath(), "r");
			int int2 = readInt(randomAccessFile);
			int int3 = readInt(randomAccessFile);
			int int4 = readInt(randomAccessFile);
			SharedStrings sharedStrings = new SharedStrings();
			for (int int5 = 0; int5 < int4; ++int5) {
				if (!GameServer.bServer) {
					Thread.yield();
					Core.getInstance().DoFrameReady();
				}

				String string2 = readString(randomAccessFile);
				String string3 = string2.trim();
				String string4 = readString(randomAccessFile);
				int int6 = readInt(randomAccessFile);
				int int7 = readInt(randomAccessFile);
				int int8 = readInt(randomAccessFile);
				int int9 = readInt(randomAccessFile);
				for (int int10 = 0; int10 < int9; ++int10) {
					int int11 = readInt(randomAccessFile);
					for (int int12 = 0; int12 < int11; ++int12) {
						string2 = readString(randomAccessFile);
						String string5 = string2.trim();
						string2 = readString(randomAccessFile);
						String string6 = string2.trim();
						IsoObjectType objectType = IsoObjectType.FromString(string5);
						string5 = sharedStrings.get(string5);
						ArrayList arrayList = null;
						if (PropertyValueMap.containsKey(string5)) {
							arrayList = (ArrayList)PropertyValueMap.get(string5);
						} else {
							arrayList = new ArrayList();
							PropertyValueMap.put(string5, arrayList);
						}

						if (!arrayList.contains(string6)) {
							arrayList.add(string6);
						}
					}
				}
			}
		} catch (Exception exception) {
			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
		} finally {
			try {
				randomAccessFile.close();
			} catch (Exception exception2) {
			}
		}
	}

	private void SetCustomPropertyValues() {
		((ArrayList)PropertyValueMap.get("WindowN")).add("WindowN");
		((ArrayList)PropertyValueMap.get("WindowW")).add("WindowW");
		((ArrayList)PropertyValueMap.get("DoorWallN")).add("DoorWallN");
		((ArrayList)PropertyValueMap.get("DoorWallW")).add("DoorWallW");
		((ArrayList)PropertyValueMap.get("WallSE")).add("WallSE");
		ArrayList arrayList = new ArrayList();
		for (int int1 = -96; int1 <= 96; ++int1) {
			String string = Integer.toString(int1);
			arrayList.add(string);
		}

		PropertyValueMap.put("Noffset", arrayList);
		PropertyValueMap.put("Soffset", arrayList);
		PropertyValueMap.put("Woffset", arrayList);
		PropertyValueMap.put("Eoffset", arrayList);
		((ArrayList)PropertyValueMap.get("tree")).add("5");
		((ArrayList)PropertyValueMap.get("tree")).add("6");
		((ArrayList)PropertyValueMap.get("lightR")).add("0");
		((ArrayList)PropertyValueMap.get("lightG")).add("0");
		((ArrayList)PropertyValueMap.get("lightB")).add("0");
	}

	private void saveMovableStats(Map map, int int1, int int2, int int3, int int4, int int5) throws FileNotFoundException, IOException {
		File file = new File(GameWindow.getCacheDir());
		if (file.exists() && file.isDirectory()) {
			File file2 = new File(GameWindow.getCacheDir() + File.separator + "movables_stats_" + int1 + ".txt");
			try {
				FileWriter fileWriter = new FileWriter(file2, false);
				Throwable throwable = null;
				try {
					fileWriter.write("### Movable objects ###" + System.lineSeparator());
					fileWriter.write("Single Face: " + int2 + System.lineSeparator());
					fileWriter.write("Multi Face: " + int3 + System.lineSeparator());
					fileWriter.write("Multi Face & Multi Sprite: " + int4 + System.lineSeparator());
					fileWriter.write("Total objects : " + (int2 + int3 + int4) + System.lineSeparator());
					fileWriter.write(" " + System.lineSeparator());
					fileWriter.write("Total sprites : " + int5 + System.lineSeparator());
					fileWriter.write(" " + System.lineSeparator());
					Iterator iterator = map.entrySet().iterator();
					while (iterator.hasNext()) {
						Entry entry = (Entry)iterator.next();
						fileWriter.write((String)entry.getKey() + System.lineSeparator());
						Iterator iterator2 = ((ArrayList)entry.getValue()).iterator();
						while (iterator2.hasNext()) {
							String string = (String)iterator2.next();
							fileWriter.write("\t" + string + System.lineSeparator());
						}
					}
				} catch (Throwable throwable2) {
					throwable = throwable2;
					throw throwable2;
				} finally {
					if (fileWriter != null) {
						if (throwable != null) {
							try {
								fileWriter.close();
							} catch (Throwable throwable3) {
								throwable.addSuppressed(throwable3);
							}
						} else {
							fileWriter.close();
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	private void addJumboTreeTileset(IsoSpriteManager spriteManager, int int1, String string, int int2, int int3, int int4) {
		byte byte1 = 2;
		for (int int5 = 0; int5 < int3; ++int5) {
			for (int int6 = 0; int6 < byte1; ++int6) {
				String string2 = "e_" + string + "JUMBO_1";
				int int7 = int5 * byte1 + int6;
				IsoSprite sprite = spriteManager.AddSprite(string2 + "_" + int7, int1 * 512 * 512 + int2 * 512 + int7);
				assert GameServer.bServer || !sprite.CurrentAnim.Frames.isEmpty() && ((IsoDirectionFrame)sprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) != null;
				sprite.setName(string2 + "_" + int7);
				sprite.setType(IsoObjectType.tree);
				sprite.getProperties().Set("tree", int6 == 0 ? "5" : "6");
				sprite.getProperties().UnSet(IsoFlagType.solid);
				sprite.getProperties().Set(IsoFlagType.blocksight);
				sprite.getProperties().CreateKeySet();
				sprite.moveWithWind = true;
				sprite.windType = int4;
			}
		}
	}

	private void JumboTreeDefinitions(IsoSpriteManager spriteManager, int int1) {
		this.addJumboTreeTileset(spriteManager, int1, "americanholly", 1, 2, 3);
		this.addJumboTreeTileset(spriteManager, int1, "americanlinden", 2, 6, 2);
		this.addJumboTreeTileset(spriteManager, int1, "canadianhemlock", 3, 2, 3);
		this.addJumboTreeTileset(spriteManager, int1, "carolinasilverbell", 4, 6, 1);
		this.addJumboTreeTileset(spriteManager, int1, "cockspurhawthorn", 5, 6, 2);
		this.addJumboTreeTileset(spriteManager, int1, "dogwood", 6, 6, 2);
		this.addJumboTreeTileset(spriteManager, int1, "easternredbud", 7, 6, 2);
		this.addJumboTreeTileset(spriteManager, int1, "redmaple", 8, 6, 2);
		this.addJumboTreeTileset(spriteManager, int1, "riverbirch", 9, 6, 1);
		this.addJumboTreeTileset(spriteManager, int1, "virginiapine", 10, 2, 1);
		this.addJumboTreeTileset(spriteManager, int1, "yellowwood", 11, 6, 2);
		byte byte1 = 12;
		byte byte2 = 0;
		IsoSprite sprite = spriteManager.AddSprite("jumbo_tree_01_" + byte2, int1 * 512 * 512 + byte1 * 512 + byte2);
		sprite.setName("jumbo_tree_01_" + byte2);
		sprite.setType(IsoObjectType.tree);
		sprite.getProperties().Set("tree", "4");
		sprite.getProperties().UnSet(IsoFlagType.solid);
		sprite.getProperties().Set(IsoFlagType.blocksight);
	}

	public boolean LoadPlayerForInfo() throws FileNotFoundException, IOException {
		File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_p.bin");
		if (!file.exists()) {
			return false;
		} else {
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			synchronized (SliceY.SliceBuffer) {
				SliceY.SliceBuffer.rewind();
				byte[] byteArray = SliceY.SliceBuffer.array();
				bufferedInputStream.read(SliceY.SliceBuffer.array());
				SliceY.SliceBuffer.rewind();
				bufferedInputStream.close();
				byte byte1 = SliceY.SliceBuffer.get();
				byte byte2 = SliceY.SliceBuffer.get();
				byte byte3 = SliceY.SliceBuffer.get();
				byte byte4 = SliceY.SliceBuffer.get();
				int int1 = -1;
				if (byte1 == 80 && byte2 == 76 && byte3 == 89 && byte4 == 82) {
					int1 = SliceY.SliceBuffer.getInt();
				} else {
					SliceY.SliceBuffer.rewind();
				}

				if (int1 >= 69) {
					String string = GameWindow.ReadString(SliceY.SliceBuffer);
					if (GameClient.bClient && int1 < 71) {
						string = ServerOptions.instance.ServerPlayerID.getValue();
					}

					if (GameClient.bClient && !IsoPlayer.isServerPlayerIDValid(string)) {
						GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_ServerPlayerIDMismatch");
						GameLoadingState.playerWrongIP = true;
						return false;
					}
				} else if (GameClient.bClient && ServerOptions.instance.ServerPlayerID.getValue().isEmpty()) {
					GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_ServerPlayerIDMissing");
					GameLoadingState.playerWrongIP = true;
					return false;
				}

				WorldX = SliceY.SliceBuffer.getInt();
				WorldY = SliceY.SliceBuffer.getInt();
				IsoChunkMap.WorldXA = SliceY.SliceBuffer.getInt();
				IsoChunkMap.WorldYA = SliceY.SliceBuffer.getInt();
				IsoChunkMap.WorldZA = SliceY.SliceBuffer.getInt();
				IsoChunkMap.WorldXA += 300 * saveoffsetx;
				IsoChunkMap.WorldYA += 300 * saveoffsety;
				IsoChunkMap.SWorldX[0] = WorldX;
				IsoChunkMap.SWorldY[0] = WorldY;
				int[] intArray = IsoChunkMap.SWorldX;
				intArray[0] += 30 * saveoffsetx;
				intArray = IsoChunkMap.SWorldY;
				intArray[0] += 30 * saveoffsety;
				return true;
			}
		}
	}

	public void init() throws FileNotFoundException, IOException {
		if (!Core.bTutorial) {
			this.randomizedBuildingList.add(new RBSafehouse());
			this.randomizedBuildingList.add(new RBBurnt());
			this.randomizedBuildingList.add(new RBLooted());
		}

		if (!GameClient.bClient && !GameServer.bServer) {
			BodyDamageSync.instance = null;
		} else {
			BodyDamageSync.instance = new BodyDamageSync();
		}

		if (GameServer.bServer) {
			Core.GameSaveWorld = GameServer.ServerName;
			LuaManager.GlobalObject.createWorld(Core.GameSaveWorld);
		}

		SavedWorldVersion = -1;
		File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_ver.bin");
		FileInputStream fileInputStream;
		DataInputStream dataInputStream;
		if (file.exists()) {
			fileInputStream = new FileInputStream(file);
			dataInputStream = new DataInputStream(fileInputStream);
			SavedWorldVersion = dataInputStream.readInt();
			if (SavedWorldVersion >= 25) {
				String string = GameWindow.ReadString(dataInputStream);
				if (!GameClient.bClient) {
					Core.GameMap = string;
				}
			}

			if (SavedWorldVersion >= 74) {
				this.setDifficulty(GameWindow.ReadString(dataInputStream));
			}

			dataInputStream.close();
		}

		if (!GameServer.bServer || System.getProperty("softreset") == null) {
			this.MetaGrid.CreateStep1();
		}

		LuaEventManager.triggerEvent("OnPreDistributionMerge");
		LuaEventManager.triggerEvent("OnDistributionMerge");
		LuaEventManager.triggerEvent("OnPostDistributionMerge");
		ItemPickerJava.Parse();
		LuaEventManager.triggerEvent("OnInitWorld");
		if (!GameClient.bClient) {
			file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_sand.bin");
			if (file.exists()) {
				fileInputStream = new FileInputStream(file);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				if (SliceY.SliceBuffer == null) {
					SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
				}

				synchronized (SliceY.SliceBuffer) {
					bufferedInputStream.read(SliceY.SliceBuffer.array());
					bufferedInputStream.close();
					SliceY.SliceBuffer.rewind();
					SandboxOptions.instance.load(SliceY.SliceBuffer);
					SandboxOptions.instance.handleOldZombiesFile1();
					SandboxOptions.instance.applySettings();
					SandboxOptions.instance.toLua();
				}
			} else {
				SandboxOptions.instance = new SandboxOptions();
				SandboxOptions.instance.updateFromLua();
			}
		}

		ZomboidGlobals.toLua();
		this.SurvivorDescriptors.clear();
		this.spriteManager = new IsoSpriteManager();
		if (GameClient.bClient && ServerOptions.instance.DoLuaChecksum.getValue()) {
			try {
				NetChecksum.comparer.beginCompare();
				GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_Checksum");
				long long1 = System.currentTimeMillis();
				long long2 = long1;
				while (!GameClient.checksumValid) {
					if (GameWindow.bServerDisconnected) {
						return;
					}

					if (System.currentTimeMillis() > long1 + 8000L) {
						DebugLog.log("checksum: timed out waiting for the server to respond");
						GameClient.connection.forceDisconnect();
						GameWindow.bServerDisconnected = true;
						GameWindow.kickReason = Translator.getText("UI_GameLoad_TimedOut");
						return;
					}

					if (System.currentTimeMillis() > long2 + 1000L) {
						DebugLog.log("checksum: waited one second");
						long2 += 1000L;
					}

					NetChecksum.comparer.update();
					if (GameClient.checksumValid) {
						break;
					}

					Thread.sleep(100L);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_LoadTileDef");
		this.LoadTileDefinitionsPropertyStrings(this.spriteManager, "media/tiledefinitions.tiles", 0);
		this.LoadTileDefinitionsPropertyStrings(this.spriteManager, "media/newtiledefinitions.tiles", 1);
		this.LoadTileDefinitionsPropertyStrings(this.spriteManager, "media/tiledefinitions_erosion.tiles", 2);
		this.LoadTileDefinitionsPropertyStrings(this.spriteManager, "media/tiledefinitions_apcom.tiles", 3);
		ZomboidFileSystem.instance.loadModTileDefPropertyStrings();
		this.SetCustomPropertyValues();
		this.GenerateTilePropertyLookupTables();
		this.LoadTileDefinitions(this.spriteManager, "media/tiledefinitions.tiles", 0);
		this.LoadTileDefinitions(this.spriteManager, "media/newtiledefinitions.tiles", 1);
		this.LoadTileDefinitions(this.spriteManager, "media/tiledefinitions_erosion.tiles", 2);
		this.LoadTileDefinitions(this.spriteManager, "media/tiledefinitions_apcom.tiles", 3);
		this.JumboTreeDefinitions(this.spriteManager, 4);
		ZomboidFileSystem.instance.loadModTileDefs();
		GameLoadingState.GameLoadingString = "";
		this.spriteManager.AddSprite("media/ui/missing-tile.png");
		LuaEventManager.triggerEvent("OnLoadedTileDefinitions", this.spriteManager);
		String string2 = "media/newtiledefinitions_143.tiles";
		File file2 = new File(string2);
		if (!file2.exists()) {
			string2 = "media/newtiledefinitions.tiles";
		}

		if (GameServer.bServer && System.getProperty("softreset") != null) {
			WorldConverter.instance.softreset(this.spriteManager);
		}

		try {
			WeatherFxMask.init();
		} catch (Exception exception2) {
			System.out.print(exception2.getStackTrace());
		}

		IsoRegion.init();
		ObjectRenderEffects.init();
		WorldConverter.instance.convert(Core.GameSaveWorld, this.spriteManager);
		if (!GameLoadingState.build23Stop) {
			SandboxOptions.instance.handleOldZombiesFile2();
			GameTime.getInstance().init();
			GameTime.getInstance().load();
			ZomboidRadio.getInstance().Init(SavedWorldVersion);
			if (GameServer.bServer && Core.getInstance().getPoisonousBerry() == null) {
				Core.getInstance().initPoisonousBerry();
			}

			if (GameServer.bServer && Core.getInstance().getPoisonousMushroom() == null) {
				Core.getInstance().initPoisonousMushroom();
			}

			ErosionGlobals.Boot(this.spriteManager);
			if (GameServer.bServer) {
				SharedDescriptors.initSharedDescriptors();
			}

			VirtualZombieManager.instance.init();
			VehicleIDMap.instance.Reset();
			VehicleManager.instance = new VehicleManager();
			String string3 = this.playerCell;
			this.playerCell = this.x + "_" + this.y;
			KahluaTable kahluaTable = LuaManager.env;
			Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, kahluaTable.rawget("getMainCellLot"), instance.x, instance.y);
			if (objectArray.length > 1) {
				string3 = (String)objectArray[1];
			}

			this.LoadRemotenessVars();
			GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_InitMap");
			this.MetaGrid.CreateStep2();
			ClimateManager.getInstance().init(this.MetaGrid);
			SafeHouse.init();
			LuaEventManager.triggerEvent("OnLoadMapZones");
			if (ChunkRevisions.USE_CHUNK_REVISIONS) {
				ChunkRevisions.instance = new ChunkRevisions();
			}

			File file3 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_meta.bin");
			FileInputStream fileInputStream2;
			BufferedInputStream bufferedInputStream2;
			byte[] byteArray;
			if (file3.exists()) {
				fileInputStream2 = new FileInputStream(file3);
				bufferedInputStream2 = new BufferedInputStream(fileInputStream2);
				if (SliceY.SliceBuffer == null) {
					SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
				}

				synchronized (SliceY.SliceBuffer) {
					SliceY.SliceBuffer.rewind();
					byteArray = SliceY.SliceBuffer.array();
					bufferedInputStream2.read(SliceY.SliceBuffer.array());
					SliceY.SliceBuffer.rewind();
					instance.MetaGrid.load(SliceY.SliceBuffer);
					SliceY.SliceBuffer.rewind();
				}

				try {
					bufferedInputStream2.close();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}

			file3 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_zone.bin");
			if (file3.exists()) {
				fileInputStream2 = new FileInputStream(file3);
				bufferedInputStream2 = new BufferedInputStream(fileInputStream2);
				if (SliceY.SliceBuffer == null) {
					SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
				}

				synchronized (SliceY.SliceBuffer) {
					SliceY.SliceBuffer.rewind();
					byteArray = SliceY.SliceBuffer.array();
					bufferedInputStream2.read(SliceY.SliceBuffer.array());
					SliceY.SliceBuffer.rewind();
					instance.MetaGrid.loadZone(SliceY.SliceBuffer, -1);
					SliceY.SliceBuffer.rewind();
				}

				try {
					bufferedInputStream2.close();
				} catch (IOException ioException2) {
					ioException2.printStackTrace();
				}
			}

			this.MetaGrid.processZones();
			if (GameServer.bServer) {
				ServerMap.instance.init(this.MetaGrid);
			}

			boolean boolean1 = false;
			File file4 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_p.bin");
			int int1 = 0;
			int int2 = 0;
			int int3 = 0;
			SafeHouse safeHouse;
			if (file4.exists()) {
				boolean1 = true;
				if (!this.LoadPlayerForInfo()) {
					return;
				}

				WorldX = IsoChunkMap.SWorldX[IsoPlayer.getPlayerIndex()];
				WorldY = IsoChunkMap.SWorldY[IsoPlayer.getPlayerIndex()];
				int1 = IsoChunkMap.WorldXA;
				int2 = IsoChunkMap.WorldYA;
				int3 = IsoChunkMap.WorldZA;
			} else {
				boolean1 = false;
				if (GameClient.bClient && !ServerOptions.instance.SpawnPoint.getValue().isEmpty()) {
					String[] stringArray = ServerOptions.instance.SpawnPoint.getValue().split(",");
					if (stringArray.length == 3) {
						try {
							IsoChunkMap.MPWorldXA = new Integer(stringArray[0].trim());
							IsoChunkMap.MPWorldYA = new Integer(stringArray[1].trim());
							IsoChunkMap.MPWorldZA = new Integer(stringArray[2].trim());
						} catch (NumberFormatException numberFormatException) {
							DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
							IsoChunkMap.MPWorldXA = 0;
							IsoChunkMap.MPWorldYA = 0;
							IsoChunkMap.MPWorldZA = 0;
						}
					} else {
						DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
					}
				}

				if (this.getLuaSpawnCellX() < 0 || GameClient.bClient && (IsoChunkMap.MPWorldXA != 0 || IsoChunkMap.MPWorldYA != 0)) {
					if (GameClient.bClient) {
						IsoChunkMap.WorldXA = IsoChunkMap.MPWorldXA;
						IsoChunkMap.WorldYA = IsoChunkMap.MPWorldYA;
						IsoChunkMap.WorldZA = IsoChunkMap.MPWorldZA;
						WorldX = IsoChunkMap.WorldXA / 10;
						WorldY = IsoChunkMap.WorldYA / 10;
					}
				} else {
					IsoChunkMap.WorldXA = this.getLuaPosX() + 300 * this.getLuaSpawnCellX();
					IsoChunkMap.WorldYA = this.getLuaPosY() + 300 * this.getLuaSpawnCellY();
					IsoChunkMap.WorldZA = this.getLuaPosZ();
					if (GameClient.bClient && ServerOptions.instance.SafehouseAllowRespawn.getValue()) {
						for (int int4 = 0; int4 < SafeHouse.getSafehouseList().size(); ++int4) {
							safeHouse = (SafeHouse)SafeHouse.getSafehouseList().get(int4);
							if (safeHouse.getPlayers().contains(GameClient.username)) {
								IsoChunkMap.WorldXA = safeHouse.getX() + safeHouse.getH() / 2;
								IsoChunkMap.WorldYA = safeHouse.getY() + safeHouse.getW() / 2;
								IsoChunkMap.WorldZA = 0;
							}
						}
					}

					WorldX = IsoChunkMap.WorldXA / 10;
					WorldY = IsoChunkMap.WorldYA / 10;
				}
			}

			Core.getInstance();
			KahluaTable kahluaTable2 = (KahluaTable)LuaManager.env.rawget("selectedDebugScenario");
			int int5;
			if (kahluaTable2 != null) {
				KahluaTable kahluaTable3 = (KahluaTable)kahluaTable2.rawget("startLoc");
				int int6 = ((Double)kahluaTable3.rawget("x")).intValue();
				int int7 = ((Double)kahluaTable3.rawget("y")).intValue();
				int5 = ((Double)kahluaTable3.rawget("z")).intValue();
				IsoChunkMap.WorldXA = int6;
				IsoChunkMap.WorldYA = int7;
				IsoChunkMap.WorldZA = int5;
				WorldX = IsoChunkMap.WorldXA / 10;
				WorldY = IsoChunkMap.WorldYA / 10;
			}

			MapCollisionData.instance.init(instance.getMetaGrid());
			ZombiePopulationManager.instance.init(instance.getMetaGrid());
			PolygonalMap2.instance.init(instance.getMetaGrid());
			GlobalObjectLookup.init(instance.getMetaGrid());
			WorldStreamer.instance.create();
			this.CurrentCell = CellLoader.LoadCellBinaryChunk(this.spriteManager, WorldX, WorldY);
			ClimateManager.getInstance().postCellLoadSetSnow();
			GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_LoadWorld");
			MapCollisionData.instance.start();
			while (WorldStreamer.instance.isBusy()) {
				try {
					Thread.sleep(100L);
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
			}

			ArrayList arrayList = new ArrayList();
			arrayList.addAll(IsoChunk.loadGridSquare);
			Iterator iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				IsoChunk chunk = (IsoChunk)iterator.next();
				this.CurrentCell.ChunkMap[0].setChunkDirect(chunk, false);
			}

			IsoChunk.bDoServerRequests = true;
			if (boolean1 && SystemDisabler.doPlayerCreation && !FrameLoader.bDedicated) {
				safeHouse = null;
				this.CurrentCell.getGridSquare(int1, int2, int3);
				this.CurrentCell.LoadPlayer(SavedWorldVersion);
				if (GameClient.bClient) {
					IsoPlayer.instance.setUsername(GameClient.username);
				}
			} else if (!FrameLoader.bDedicated) {
				ScriptCharacter scriptCharacter = ScriptManager.instance.FindCharacter("Player");
				if (scriptCharacter == null) {
					SurvivorDesc survivorDesc = SurvivorFactory.CreateSurvivor();
					IsoGridSquare square = null;
					if (IsoPlayer.numPlayers == 0) {
						IsoPlayer.numPlayers = 1;
					}

					IsoChunkMap chunkMap = this.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()];
					int5 = IsoChunkMap.WorldXA;
					chunkMap = this.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()];
					int int8 = IsoChunkMap.WorldYA;
					chunkMap = this.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()];
					int int9 = IsoChunkMap.WorldZA;
					if (GameClient.bClient && !ServerOptions.instance.SpawnPoint.getValue().isEmpty()) {
						String[] stringArray2 = ServerOptions.instance.SpawnPoint.getValue().split(",");
						if (stringArray2.length != 3) {
							DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
						} else {
							try {
								int int10 = new Integer(stringArray2[0].trim());
								int int11 = new Integer(stringArray2[1].trim());
								int int12 = new Integer(stringArray2[2].trim());
								if (GameClient.bClient && ServerOptions.instance.SafehouseAllowRespawn.getValue()) {
									for (int int13 = 0; int13 < SafeHouse.getSafehouseList().size(); ++int13) {
										SafeHouse safeHouse2 = (SafeHouse)SafeHouse.getSafehouseList().get(int13);
										if (safeHouse2.getPlayers().contains(GameClient.username)) {
											int10 = safeHouse2.getX() + safeHouse2.getH() / 2;
											int11 = safeHouse2.getY() + safeHouse2.getW() / 2;
											int12 = 0;
										}
									}
								}

								if (this.CurrentCell.getGridSquare(int10, int11, int12) != null) {
									int5 = int10;
									int8 = int11;
									int9 = int12;
								}
							} catch (NumberFormatException numberFormatException2) {
								DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
							}
						}
					}

					square = this.CurrentCell.getGridSquare(int5, int8, int9);
					if (SystemDisabler.doPlayerCreation && !GameServer.bServer) {
						if (square != null && square.isFree(false) && square.getRoom() != null) {
							IsoGridSquare square2 = square;
							square = square.getRoom().getFreeTile();
							if (square == null) {
								square = square2;
							}
						}

						IsoPlayer player = null;
						Core.getInstance();
						if (this.getLuaPlayerDesc() != null) {
							if (GameClient.bClient && ServerOptions.instance.SafehouseAllowRespawn.getValue()) {
								square = this.CurrentCell.getGridSquare(IsoChunkMap.WorldXA, IsoChunkMap.WorldYA, IsoChunkMap.WorldZA);
								if (square != null && square.isFree(false) && square.getRoom() != null) {
									IsoGridSquare square3 = square;
									square = square.getRoom().getFreeTile();
									if (square == null) {
										square = square3;
									}
								}
							}

							if (square == null) {
								throw new RuntimeException("can\'t create player at x,y,z=" + int5 + "," + int8 + "," + int9 + " because the square is null");
							}

							WorldSimulation.instance.create();
							player = new IsoPlayer(instance.CurrentCell, this.getLuaPlayerDesc(), square.getX(), square.getY(), square.getZ());
							if (GameClient.bClient) {
								player.setUsername(GameClient.username);
							}

							player.setDir(IsoDirections.SE);
							IsoPlayer.players[0] = player;
							IsoPlayer.instance = player;
							IsoCamera.CamCharacter = player;
						}

						IsoPlayer player2 = IsoPlayer.getInstance();
						player2.applyTraits(this.getLuaTraits());
						ProfessionFactory.Profession profession = ProfessionFactory.getProfession(player2.getDescriptor().getProfession());
						Iterator iterator2;
						String string4;
						if (profession != null && !profession.getFreeRecipes().isEmpty()) {
							iterator2 = profession.getFreeRecipes().iterator();
							while (iterator2.hasNext()) {
								string4 = (String)iterator2.next();
								player2.getKnownRecipes().add(string4);
							}
						}

						iterator2 = this.getLuaTraits().iterator();
						label362: while (true) {
							TraitFactory.Trait trait;
							do {
								do {
									if (!iterator2.hasNext()) {
										if (!GameClient.bClient) {
											StashSystem.init();
										}

										if (square != null && square.getRoom() != null) {
											square.getRoom().def.setExplored(true);
											square.getRoom().building.setAllExplored(true);
											if (!GameServer.bServer && !GameClient.bClient) {
												ZombiePopulationManager.instance.playerSpawnedAt(square.getX(), square.getY(), square.getZ());
											}
										}

										player2.createKeyRing();
										if (!GameClient.bClient) {
											Core.getInstance().initPoisonousBerry();
											Core.getInstance().initPoisonousMushroom();
										}

										player2.addSmallInjuries();
										LuaEventManager.triggerEvent("OnNewGame", player, square);
										break label362;
									}

									string4 = (String)iterator2.next();
									trait = TraitFactory.getTrait(string4);
								}						 while (trait == null);
							}					 while (trait.getFreeRecipes().isEmpty());

							Iterator iterator3 = trait.getFreeRecipes().iterator();
							while (iterator3.hasNext()) {
								String string5 = (String)iterator3.next();
								player2.getKnownRecipes().add(string5);
							}
						}
					}
				}
			}

			TutorialManager.instance.ActiveControlZombies = false;
			dataInputStream = null;
			ReanimatedPlayers.instance.loadReanimatedPlayers();
			ChunkSaveWorker.instance.LoadContainers();
			ScriptCharacter scriptCharacter2;
			if (!this.bLoaded && !GameServer.bServer && !FrameLoader.bClient && SystemDisabler.doPlayerCreation) {
				scriptCharacter2 = ScriptManager.instance.FindCharacter("Player");
			}

			int int14;
			if (IsoPlayer.getInstance() != null) {
				if (GameClient.bClient) {
					int14 = (int)IsoPlayer.getInstance().getX();
					int int15 = (int)IsoPlayer.getInstance().getY();
					int int16 = (int)IsoPlayer.getInstance().getZ();
					while (int16 > 0) {
						IsoGridSquare square4 = this.CurrentCell.getGridSquare(int14, int15, int16);
						if (square4 != null && square4.TreatAsSolidFloor()) {
							break;
						}

						--int16;
						IsoPlayer.getInstance().setZ((float)int16);
					}
				}

				IsoPlayer.getInstance().setCurrent(this.CurrentCell.getGridSquare((int)IsoPlayer.getInstance().getX(), (int)IsoPlayer.getInstance().getY(), (int)IsoPlayer.getInstance().getZ()));
			}

			if (!this.bLoaded) {
				if (!this.CurrentCell.getBuildingList().isEmpty()) {
					boolean boolean2 = true;
					KahluaTable kahluaTable4 = LuaManager.env;
					Object[] objectArray2 = LuaManager.caller.pcall(LuaManager.thread, kahluaTable4.rawget("getStartIndoorZombiesByGrid"), this.x, this.y);
					if (objectArray2.length > 1) {
						int14 = ((Double)objectArray2[1]).intValue();
					}
				}

				scriptCharacter2 = ScriptManager.instance.getCharacter("KateAndBaldspot.Kate");
				if (scriptCharacter2 != null) {
					TutorialManager.instance.wife = (IsoSurvivor)scriptCharacter2.Actual;
				}

				if (!this.bLoaded) {
					this.PopulateCellWithSurvivors();
				}
			}

			if (IsoPlayer.players[0] != null && !this.CurrentCell.getObjectList().contains(IsoPlayer.players[0])) {
				this.CurrentCell.getObjectList().add(IsoPlayer.players[0]);
			}

			LightingThread.instance.create();
			GameLoadingState.GameLoadingString = "";
		}
	}

	public ArrayList getLuaTraits() {
		if (this.luatraits == null) {
			this.luatraits = new ArrayList();
		}

		return this.luatraits;
	}

	public void addLuaTrait(String string) {
		this.getLuaTraits().add(string);
	}

	public SurvivorDesc getLuaPlayerDesc() {
		return this.luaDesc;
	}

	public void setLuaPlayerDesc(SurvivorDesc survivorDesc) {
		this.luaDesc = survivorDesc;
	}

	public void KillCell() {
		this.helicopter.deactivate();
		CollisionManager.instance.ContactMap.clear();
		IsoDeadBody.Reset();
		FliesSound.instance.Reset();
		IsoObjectPicker.Instance.Init();
		IsoChunkMap.SharedChunks.clear();
		SoundManager.instance.StopMusic();
		WorldSoundManager.instance.KillCell();
		ZombieGroupManager.instance.Reset();
		this.CurrentCell.Dispose();
		this.CurrentCell = null;
		CellLoader.wanderRoom = null;
		IsoLot.Dispose();
		IsoGameCharacter.getSurvivorMap().clear();
		IsoPlayer.getInstance().setCurrent((IsoGridSquare)null);
		IsoPlayer.getInstance().setLast((IsoGridSquare)null);
		IsoPlayer.getInstance().square = null;
		ItemContainerFiller.Containers.clear();
		ItemContainerFiller.DistributionTarget.clear();
		instance.Groups.clear();
		RainManager.reset();
		IsoFireManager.Reset();
		this.MetaGrid.Dispose();
		this.MetaGrid = null;
		this.spriteManager = null;
		instance = new IsoWorld();
	}

	public void setDrawWorld(boolean boolean1) {
		this.bDrawWorld = boolean1;
	}

	public void render() {
		if (this.bDrawWorld) {
			if (IsoCamera.CamCharacter != null) {
				++this.savePlayerCount;
				if (this.savePlayerCount > PerformanceSettings.LockFPS * 60) {
					GameWindow.savePlayer();
					this.savePlayerCount = 0;
				}

				int int1 = PerformanceSettings.numberOf3D;
				switch (PerformanceSettings.numberOf3D) {
				case 1: 
					int1 = 1;
					break;
				
				case 2: 
					int1 = 2;
					break;
				
				case 3: 
					int1 = 3;
					break;
				
				case 4: 
					int1 = 4;
					break;
				
				case 5: 
					int1 = 5;
					break;
				
				case 6: 
					int1 = 8;
					break;
				
				case 7: 
					int1 = 10;
					break;
				
				case 8: 
					int1 = 20;
					break;
				
				case 9: 
					int1 = 20000;
				
				}

				int1 += PerformanceSettings.numberOf3DAlt;
				ModelManager.instance.returnContext = false;
				try {
					synchronized (this.CurrentCell.getZombieList()) {
						this.zombieWithModel.clear();
						int int2;
						int int3;
						IsoZombie zombie;
						if (int1 >= this.CurrentCell.getZombieList().size()) {
							for (int2 = 0; int2 < this.CurrentCell.getZombieList().size(); ++int2) {
								zombie = (IsoZombie)this.CurrentCell.getZombieList().get(int2);
								for (int3 = 0; int3 < IsoPlayer.numPlayers; ++int3) {
									IsoPlayer player = IsoPlayer.players[int3];
									if (player != null && zombie.current != null && zombie.alpha[int3] > 0.0F) {
										if (!this.zombieWithModel.contains(zombie)) {
											this.zombieWithModel.add(zombie);
										}

										break;
									}
								}
							}
						} else {
							for (int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
								IsoPlayer player2 = IsoPlayer.players[int2];
								if (player2 != null) {
									compDistToPlayer.px = player2.getX();
									compDistToPlayer.py = player2.getY();
									Collections.sort(this.CurrentCell.getZombieList(), compDistToPlayer);
									int3 = 0;
									int int4 = 0;
									for (int int5 = 0; int5 < this.CurrentCell.getZombieList().size(); ++int5) {
										IsoZombie zombie2 = (IsoZombie)this.CurrentCell.getZombieList().get(int5);
										if (int1 > int3 && zombie2.current != null && zombie2.alpha[int2] > 0.0F) {
											if (!this.zombieWithModel.contains(zombie2)) {
												this.zombieWithModel.add(zombie2);
											}

											++int3;
										}

										++int4;
									}
								}
							}
						}

						for (int2 = 0; int2 < this.CurrentCell.getZombieList().size(); ++int2) {
							zombie = (IsoZombie)this.CurrentCell.getZombieList().get(int2);
							if (this.zombieWithModel.contains(zombie)) {
								zombie.setModel(zombie.isFemale() ? "kate" : "male");
							} else {
								zombie.setModel((String)null);
							}
						}
					}
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
				} finally {
					ModelManager.instance.returnContext = true;
				}

				try {
					long long1 = System.nanoTime();
					WeatherFxMask.initMask();
					this.CurrentCell.render();
					PolygonalMap2.instance.render();
					LineDrawer.render();
					this.sky.draw();
					WeatherFxMask.renderFxMask(IsoCamera.frameState.playerIndex);
					SkyBox.getInstance().render();
				} catch (Throwable throwable) {
					ExceptionLogger.logException(throwable);
				}
			}
		}
	}

	public void primUpdate() {
	}

	public void update() {
		try {
			if (GameServer.bServer) {
				VehicleManager.instance.serverUpdate();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		WorldSimulation.instance.update();
		this.helicopter.update();
		long long1 = System.currentTimeMillis();
		if (long1 - this.emitterUpdateMS >= 30L) {
			this.emitterUpdateMS = long1;
			this.emitterUpdate = true;
		} else {
			this.emitterUpdate = false;
		}

		for (int int1 = 0; int1 < this.currentEmitters.size(); ++int1) {
			BaseSoundEmitter baseSoundEmitter = (BaseSoundEmitter)this.currentEmitters.get(int1);
			if (this.emitterUpdate || baseSoundEmitter.hasSoundsToStart()) {
				baseSoundEmitter.tick();
			}

			if (baseSoundEmitter.isEmpty()) {
				this.currentEmitters.remove(int1);
				this.freeEmitters.push(baseSoundEmitter);
				--int1;
			}
		}

		AutoZombieManager.instance.update();
		if (!GameClient.bClient && !GameServer.bServer) {
			IsoMetaCell metaCell = this.MetaGrid.getCurrentCellData();
			if (metaCell != null) {
				metaCell.checkTriggers();
			}
		}

		WorldSoundManager.instance.initFrame();
		ZombieGroupManager.instance.preupdate();
		OnceEvery.update();
		CollisionManager.instance.initUpdate();
		boolean boolean1 = false;
		int int2 = this.cellSurvivorSpawns;
		if (IsoPlayer.DemoMode) {
			boolean boolean2 = true;
		}

		for (int2 = 0; int2 < this.CurrentCell.getBuildingList().size(); ++int2) {
			((IsoBuilding)this.CurrentCell.getBuildingList().get(int2)).update();
		}

		long long2 = System.nanoTime();
		ClimateManager.getInstance().update();
		ObjectRenderEffects.updateStatic();
		this.CurrentCell.update();
		IsoRegion.update();
		CollisionManager.instance.ResolveContacts();
		for (int int3 = 0; int3 < this.AddCoopPlayers.size(); ++int3) {
			AddCoopPlayer addCoopPlayer = (AddCoopPlayer)this.AddCoopPlayers.get(int3);
			addCoopPlayer.update();
			if (addCoopPlayer.isFinished()) {
				this.AddCoopPlayers.remove(int3--);
			}
		}
	}

	public IsoCell getCell() {
		return this.CurrentCell;
	}

	private void PopulateCellWithSurvivors() {
	}

	public int getWorldSquareY() {
		return this.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY * 10;
	}

	public int getWorldSquareX() {
		return this.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX * 10;
	}

	public IsoMetaChunk getMetaChunk(int int1, int int2) {
		return this.MetaGrid.getChunkData(int1, int2);
	}

	public IsoMetaChunk getMetaChunkFromTile(int int1, int int2) {
		return this.MetaGrid.getChunkDataFromTile(int1, int2);
	}

	public float getGlobalTemperature() {
		return ClimateManager.getInstance().getTemperature();
	}

	public void setGlobalTemperature(float float1) {
	}

	public String getWeather() {
		return this.weather;
	}

	public void setWeather(String string) {
		this.weather = string;
	}

	public int getLuaSpawnCellX() {
		return this.luaSpawnCellX;
	}

	public void setLuaSpawnCellX(int int1) {
		this.luaSpawnCellX = int1;
	}

	public int getLuaSpawnCellY() {
		return this.luaSpawnCellY;
	}

	public void setLuaSpawnCellY(int int1) {
		this.luaSpawnCellY = int1;
	}

	public int getLuaPosX() {
		return this.luaPosX;
	}

	public void setLuaPosX(int int1) {
		this.luaPosX = int1;
	}

	public int getLuaPosY() {
		return this.luaPosY;
	}

	public void setLuaPosY(int int1) {
		this.luaPosY = int1;
	}

	public int getLuaPosZ() {
		return this.luaPosZ;
	}

	public void setLuaPosZ(int int1) {
		this.luaPosZ = int1;
	}

	public String getWorld() {
		return Core.GameSaveWorld;
	}

	public void transmitWeather() {
		if (GameServer.bServer) {
			GameServer.sendWeather();
		}
	}

	public boolean isValidSquare(int int1, int int2, int int3) {
		return int3 >= 0 && int3 < 8 ? this.MetaGrid.isValidSquare(int1, int2) : false;
	}

	public ArrayList getRandomizedBuildingList() {
		return this.randomizedBuildingList;
	}

	public void setRandomizedBuildingList(ArrayList arrayList) {
		this.randomizedBuildingList = arrayList;
	}

	public RandomizedBuildingBase getRBBasic() {
		return this.RBBasic;
	}

	public void setRBBasic(RandomizedBuildingBase randomizedBuildingBase) {
		this.RBBasic = randomizedBuildingBase;
	}

	public String getDifficulty() {
		return Core.getDifficulty();
	}

	public void setDifficulty(String string) {
		Core.setDifficulty(string);
	}

	public static boolean getZombiesDisabled() {
		return NoZombies || !SystemDisabler.doZombieCreation || SandboxOptions.instance.Zombies.getValue() == 5;
	}

	public static boolean getZombiesEnabled() {
		return !getZombiesDisabled();
	}

	public ClimateManager getClimateManager() {
		return ClimateManager.getInstance();
	}

	public static int getWorldVersion() {
		return 143;
	}

	public class Frame {
		public ArrayList xPos = new ArrayList();
		public ArrayList yPos = new ArrayList();
		public ArrayList Type = new ArrayList();

		public Frame() {
			Iterator iterator = IsoWorld.instance.CurrentCell.getObjectList().iterator();
			while (iterator != null && iterator.hasNext()) {
				IsoMovingObject movingObject = (IsoMovingObject)iterator.next();
				boolean boolean1 = true;
				byte byte1;
				if (movingObject instanceof IsoPlayer) {
					byte1 = 0;
				} else if (movingObject instanceof IsoSurvivor) {
					byte1 = 1;
				} else {
					if (!(movingObject instanceof IsoZombie) || ((IsoZombie)movingObject).Ghost) {
						continue;
					}

					byte1 = 2;
				}

				this.xPos.add((int)movingObject.getX());
				this.yPos.add((int)movingObject.getY());
				this.Type.add(Integer.valueOf(byte1));
			}
		}
	}

	public static class MetaCell {
		public int x;
		public int y;
		public int zombieCount;
		public IsoDirections zombieMigrateDirection;
		public int[][] from = new int[3][3];
	}

	private static class CompDistToPlayer implements Comparator {
		public float px;
		public float py;

		private CompDistToPlayer() {
		}

		public int compare(IsoZombie zombie, IsoZombie zombie2) {
			float float1 = IsoUtils.DistanceManhatten((float)((int)zombie.x), (float)((int)zombie.y), this.px, this.py);
			float float2 = IsoUtils.DistanceManhatten((float)((int)zombie2.x), (float)((int)zombie2.y), this.px, this.py);
			if (float1 < float2) {
				return -1;
			} else {
				return float1 > float2 ? 1 : 0;
			}
		}

		CompDistToPlayer(Object object) {
			this();
		}
	}
}
