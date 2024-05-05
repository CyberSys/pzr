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
import java.io.InputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.krka.kahlua.vm.KahluaTable;
import zombie.CollisionManager;
import zombie.DebugFileWatcher;
import zombie.FliesSound;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MapCollisionData;
import zombie.PersistentOutfits;
import zombie.PredicatedFileWatcher;
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
import zombie.ai.states.FakeDeadZombieState;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.DummySoundEmitter;
import zombie.audio.ObjectAmbientEmitters;
import zombie.characters.HaloTextHelper;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorDesc;
import zombie.characters.TriggerSetAnimationRecorderFile;
import zombie.characters.ZombieVocalsManager;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.traits.TraitFactory;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.TilePropertyAliasMap;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.physics.WorldSimulation;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.properties.PropertyContainer;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.model.WorldItemAtlas;
import zombie.core.stash.StashSystem;
import zombie.core.textures.Texture;
import zombie.core.utils.OnceEvery;
import zombie.debug.DebugLog;
import zombie.debug.LineDrawer;
import zombie.erosion.ErosionGlobals;
import zombie.gameStates.GameLoadingState;
import zombie.globalObjects.GlobalObjectLookup;
import zombie.input.Mouse;
import zombie.inventory.ItemPickerJava;
import zombie.inventory.types.MapItem;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.isoregion.IsoRegions;
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
import zombie.iso.weather.WorldFlares;
import zombie.iso.weather.fog.ImprovedFog;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.network.BodyDamageSync;
import zombie.network.ClientServerMap;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetChecksum;
import zombie.network.PassengerMap;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.popman.ZombiePopulationManager;
import zombie.radio.ZomboidRadio;
import zombie.randomizedWorld.randomizedBuilding.RBBar;
import zombie.randomizedWorld.randomizedBuilding.RBBasic;
import zombie.randomizedWorld.randomizedBuilding.RBBurnt;
import zombie.randomizedWorld.randomizedBuilding.RBBurntCorpse;
import zombie.randomizedWorld.randomizedBuilding.RBBurntFireman;
import zombie.randomizedWorld.randomizedBuilding.RBCafe;
import zombie.randomizedWorld.randomizedBuilding.RBClinic;
import zombie.randomizedWorld.randomizedBuilding.RBHairSalon;
import zombie.randomizedWorld.randomizedBuilding.RBKateAndBaldspot;
import zombie.randomizedWorld.randomizedBuilding.RBLooted;
import zombie.randomizedWorld.randomizedBuilding.RBOffice;
import zombie.randomizedWorld.randomizedBuilding.RBOther;
import zombie.randomizedWorld.randomizedBuilding.RBPileOCrepe;
import zombie.randomizedWorld.randomizedBuilding.RBPizzaWhirled;
import zombie.randomizedWorld.randomizedBuilding.RBSafehouse;
import zombie.randomizedWorld.randomizedBuilding.RBSchool;
import zombie.randomizedWorld.randomizedBuilding.RBShopLooted;
import zombie.randomizedWorld.randomizedBuilding.RBSpiffo;
import zombie.randomizedWorld.randomizedBuilding.RBStripclub;
import zombie.randomizedWorld.randomizedBuilding.RandomizedBuildingBase;
import zombie.randomizedWorld.randomizedVehicleStory.RVSAmbulanceCrash;
import zombie.randomizedWorld.randomizedVehicleStory.RVSBanditRoad;
import zombie.randomizedWorld.randomizedVehicleStory.RVSBurntCar;
import zombie.randomizedWorld.randomizedVehicleStory.RVSCarCrash;
import zombie.randomizedWorld.randomizedVehicleStory.RVSCarCrashCorpse;
import zombie.randomizedWorld.randomizedVehicleStory.RVSChangingTire;
import zombie.randomizedWorld.randomizedVehicleStory.RVSConstructionSite;
import zombie.randomizedWorld.randomizedVehicleStory.RVSCrashHorde;
import zombie.randomizedWorld.randomizedVehicleStory.RVSFlippedCrash;
import zombie.randomizedWorld.randomizedVehicleStory.RVSPoliceBlockade;
import zombie.randomizedWorld.randomizedVehicleStory.RVSPoliceBlockadeShooting;
import zombie.randomizedWorld.randomizedVehicleStory.RVSTrailerCrash;
import zombie.randomizedWorld.randomizedVehicleStory.RVSUtilityVehicle;
import zombie.randomizedWorld.randomizedVehicleStory.RandomizedVehicleStoryBase;
import zombie.randomizedWorld.randomizedZoneStory.RZSBBQParty;
import zombie.randomizedWorld.randomizedZoneStory.RZSBaseball;
import zombie.randomizedWorld.randomizedZoneStory.RZSBeachParty;
import zombie.randomizedWorld.randomizedZoneStory.RZSBuryingCamp;
import zombie.randomizedWorld.randomizedZoneStory.RZSFishingTrip;
import zombie.randomizedWorld.randomizedZoneStory.RZSForestCamp;
import zombie.randomizedWorld.randomizedZoneStory.RZSForestCampEaten;
import zombie.randomizedWorld.randomizedZoneStory.RZSHunterCamp;
import zombie.randomizedWorld.randomizedZoneStory.RZSMusicFest;
import zombie.randomizedWorld.randomizedZoneStory.RZSMusicFestStage;
import zombie.randomizedWorld.randomizedZoneStory.RZSSexyTime;
import zombie.randomizedWorld.randomizedZoneStory.RZSTrapperCamp;
import zombie.savefile.ClientPlayerDB;
import zombie.savefile.PlayerDB;
import zombie.savefile.PlayerDBHelper;
import zombie.savefile.ServerPlayerDB;
import zombie.text.templating.TemplateText;
import zombie.ui.TutorialManager;
import zombie.util.AddCoopPlayer;
import zombie.util.SharedStrings;
import zombie.util.Type;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleIDMap;
import zombie.vehicles.VehicleManager;
import zombie.vehicles.VehiclesDB2;
import zombie.world.WorldDictionary;
import zombie.world.WorldDictionaryException;
import zombie.world.moddata.GlobalModData;


public final class IsoWorld {
	private String weather = "sunny";
	public final IsoMetaGrid MetaGrid = new IsoMetaGrid();
	private final ArrayList randomizedBuildingList = new ArrayList();
	private final ArrayList randomizedZoneList = new ArrayList();
	private final ArrayList randomizedVehicleStoryList = new ArrayList();
	private final RandomizedBuildingBase RBBasic = new RBBasic();
	private final HashMap spawnedZombieZone = new HashMap();
	private final HashMap allTiles = new HashMap();
	private final ArrayList tileImages = new ArrayList();
	private float flashIsoCursorA = 1.0F;
	private boolean flashIsoCursorInc = false;
	public SkyBox sky = null;
	private static PredicatedFileWatcher m_setAnimationRecordingTriggerWatcher;
	private static boolean m_animationRecorderActive = false;
	private static boolean m_animationRecorderDiscard = false;
	private int timeSinceLastSurvivorInHorde = 4000;
	private int m_frameNo = 0;
	public final Helicopter helicopter = new Helicopter();
	private boolean bHydroPowerOn = false;
	public final ArrayList Characters = new ArrayList();
	private final ArrayDeque freeEmitters = new ArrayDeque();
	private final ArrayList currentEmitters = new ArrayList();
	private final HashMap emitterOwners = new HashMap();
	public int x = 50;
	public int y = 50;
	public IsoCell CurrentCell;
	public static IsoWorld instance = new IsoWorld();
	public int TotalSurvivorsDead = 0;
	public int TotalSurvivorNights = 0;
	public int SurvivorSurvivalRecord = 0;
	public HashMap SurvivorDescriptors = new HashMap();
	public ArrayList AddCoopPlayers = new ArrayList();
	private static final IsoWorld.CompScoreToPlayer compScoreToPlayer = new IsoWorld.CompScoreToPlayer();
	static IsoWorld.CompDistToPlayer compDistToPlayer = new IsoWorld.CompDistToPlayer();
	public static String mapPath = "media/";
	public static boolean mapUseJar = true;
	boolean bLoaded = false;
	public static final HashMap PropertyValueMap = new HashMap();
	private static int WorldX = 0;
	private static int WorldY = 0;
	private SurvivorDesc luaDesc;
	private ArrayList luatraits;
	private int luaSpawnCellX = -1;
	private int luaSpawnCellY = -1;
	private int luaPosX = -1;
	private int luaPosY = -1;
	private int luaPosZ = -1;
	public static final int WorldVersion = 194;
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
	public static final int WorldVersion_BodyLocation = 144;
	public static final int WorldVersion_CharacterModelData = 145;
	public static final int WorldVersion_CharacterModelData2 = 146;
	public static final int WorldVersion_CharacterModelData3 = 147;
	public static final int WorldVersion_HumanVisualBlood = 148;
	public static final int WorldVersion_ItemContainerIdenticalItemsInt = 149;
	public static final int WorldVersion_PerkName = 152;
	public static final int WorldVersion_Thermos = 153;
	public static final int WorldVersion_AllPatches = 155;
	public static final int WorldVersion_ZombieRotStage = 156;
	public static final int WorldVersion_NewSandboxLootModifier = 157;
	public static final int WorldVersion_KateBobStorm = 158;
	public static final int WorldVersion_DeadBodyAngle = 159;
	public static final int WorldVersion_ChunkSpawnedRooms = 160;
	public static final int WorldVersion_DeathDragDown = 161;
	public static final int WorldVersion_CanUpgradePerk = 162;
	public static final int WorldVersion_ItemVisualFullType = 164;
	public static final int WorldVersion_VehicleBlood = 165;
	public static final int WorldVersion_DeadBodyZombieRotStage = 166;
	public static final int WorldVersion_Fitness = 167;
	public static final int WorldVersion_DeadBodyFakeDead = 168;
	public static final int WorldVersion_Fitness2 = 169;
	public static final int WorldVersion_NewFog = 170;
	public static final int WorldVersion_DeadBodyPersistentOutfitID = 171;
	public static final int WorldVersion_VehicleTowingID = 172;
	public static final int WorldVersion_VehicleJNITransform = 173;
	public static final int WorldVersion_VehicleTowAttachment = 174;
	public static final int WorldVersion_ContainerMaxCapacity = 175;
	public static final int WorldVersion_TimedActionInstantCheat = 176;
	public static final int WorldVersion_ClothingPatchSaveLoad = 178;
	public static final int WorldVersion_AttachedSlotType = 179;
	public static final int WorldVersion_NoiseMakerDuration = 180;
	public static final int WorldVersion_ChunkVehicles = 91;
	public static final int WorldVersion_PlayerVehicleSeat = 91;
	public static final int WorldVersion_MediaDisksAndTapes = 181;
	public static final int WorldVersion_AlreadyReadBooks1 = 182;
	public static final int WorldVersion_LampOnPillar2 = 183;
	public static final int WorldVersion_AlreadyReadBooks2 = 184;
	public static final int WorldVersion_PolygonZone = 185;
	public static final int WorldVersion_PolylineZone = 186;
	public static final int WorldVersion_NaturalHairBeardColor = 187;
	public static final int WorldVersion_CruiseSpeedSaving = 188;
	public static final int WorldVersion_KnownMediaLines = 189;
	public static final int WorldVersion_DeadBodyAtlas = 190;
	public static final int WorldVersion_Scarecrow = 191;
	public static final int WorldVersion_DeadBodyID = 192;
	public static final int WorldVersion_IgnoreRemoveSandbox = 193;
	public static final int WorldVersion_MapMetaBounds = 194;
	public static int SavedWorldVersion = -1;
	private boolean bDrawWorld = true;
	private final ArrayList zombieWithModel = new ArrayList();
	private final ArrayList zombieWithoutModel = new ArrayList();
	public static boolean NoZombies = false;
	public static int TotalWorldVersion = -1;
	public static int saveoffsetx;
	public static int saveoffsety;
	public boolean bDoChunkMapUpdate = true;
	private long emitterUpdateMS;
	public boolean emitterUpdate;
	private int updateSafehousePlayers = 200;

	public IsoMetaGrid getMetaGrid() {
		return this.MetaGrid;
	}

	public IsoMetaGrid.Zone registerZone(String string, String string2, int int1, int int2, int int3, int int4, int int5) {
		return this.MetaGrid.registerZone(string, string2, int1, int2, int3, int4, int5);
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

	public void takeOwnershipOfEmitter(BaseSoundEmitter baseSoundEmitter) {
		this.currentEmitters.remove(baseSoundEmitter);
	}

	public void setEmitterOwner(BaseSoundEmitter baseSoundEmitter, IsoObject object) {
		if (baseSoundEmitter != null && object != null) {
			if (!this.emitterOwners.containsKey(baseSoundEmitter)) {
				this.emitterOwners.put(baseSoundEmitter, object);
			}
		}
	}

	public void returnOwnershipOfEmitter(BaseSoundEmitter baseSoundEmitter) {
		if (baseSoundEmitter != null) {
			if (!this.currentEmitters.contains(baseSoundEmitter) && !this.freeEmitters.contains(baseSoundEmitter)) {
				if (baseSoundEmitter.isEmpty()) {
					FMODSoundEmitter fMODSoundEmitter = (FMODSoundEmitter)Type.tryCastTo(baseSoundEmitter, FMODSoundEmitter.class);
					if (fMODSoundEmitter != null) {
						fMODSoundEmitter.clearParameters();
					}

					this.freeEmitters.add(baseSoundEmitter);
				} else {
					this.currentEmitters.add(baseSoundEmitter);
				}
			}
		}
	}

	public IsoMetaGrid.Zone registerVehiclesZone(String string, String string2, int int1, int int2, int int3, int int4, int int5, KahluaTable kahluaTable) {
		return this.MetaGrid.registerVehiclesZone(string, string2, int1, int2, int3, int4, int5, kahluaTable);
	}

	public IsoMetaGrid.Zone registerMannequinZone(String string, String string2, int int1, int int2, int int3, int int4, int int5, KahluaTable kahluaTable) {
		return this.MetaGrid.registerMannequinZone(string, string2, int1, int2, int3, int4, int5, kahluaTable);
	}

	public void registerRoomTone(String string, String string2, int int1, int int2, int int3, int int4, int int5, KahluaTable kahluaTable) {
		this.MetaGrid.registerRoomTone(string, string2, int1, int2, int3, int4, int5, kahluaTable);
	}

	public void registerSpawnOrigin(int int1, int int2, int int3, int int4, KahluaTable kahluaTable) {
		ZombiePopulationManager.instance.registerSpawnOrigin(int1, int2, int3, int4, kahluaTable);
	}

	public void registerWaterFlow(float float1, float float2, float float3, float float4) {
		IsoWaterFlow.addFlow(float1, float2, float3, float4);
	}

	public void registerWaterZone(float float1, float float2, float float3, float float4, float float5, float float6) {
		IsoWaterFlow.addZone(float1, float2, float3, float4, float5, float6);
	}

	public void checkVehiclesZones() {
		this.MetaGrid.checkVehiclesZones();
	}

	public void setGameMode(String string) {
		Core.GameMode = string;
		Core.bLastStand = "LastStand".equals(string);
		Core.getInstance().setChallenge(false);
		Core.ChallengeID = null;
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

	public void renderTerrain() {
	}

	public int getFrameNo() {
		return this.m_frameNo;
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
		}
	}

	private static void initMessaging() {
		if (m_setAnimationRecordingTriggerWatcher == null) {
			m_setAnimationRecordingTriggerWatcher = new PredicatedFileWatcher(ZomboidFileSystem.instance.getMessagingDirSub("Trigger_AnimationRecorder.xml"), TriggerSetAnimationRecorderFile.class, IsoWorld::onTrigger_setAnimationRecorderTriggerFile);
			DebugFileWatcher.instance.add(m_setAnimationRecordingTriggerWatcher);
		}
	}

	private static void onTrigger_setAnimationRecorderTriggerFile(TriggerSetAnimationRecorderFile triggerSetAnimationRecorderFile) {
		m_animationRecorderActive = triggerSetAnimationRecorderFile.isRecording;
		m_animationRecorderDiscard = triggerSetAnimationRecorderFile.discard;
	}

	public static boolean isAnimRecorderActive() {
		return m_animationRecorderActive;
	}

	public static boolean isAnimRecorderDiscardTriggered() {
		return m_animationRecorderDiscard;
	}

	public IsoSurvivor CreateRandomSurvivor(SurvivorDesc survivorDesc, IsoGridSquare square, IsoPlayer player) {
		return null;
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

	public static int readInt(InputStream inputStream) throws EOFException, IOException {
		int int1 = inputStream.read();
		int int2 = inputStream.read();
		int int3 = inputStream.read();
		int int4 = inputStream.read();
		if ((int1 | int2 | int3 | int4) < 0) {
			throw new EOFException();
		} else {
			return (int1 << 0) + (int2 << 8) + (int3 << 16) + (int4 << 24);
		}
	}

	public static String readString(InputStream inputStream) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		int int1 = -1;
		boolean boolean1 = false;
		while (!boolean1) {
			switch (int1 = inputStream.read()) {
			case -1: 
			
			case 10: 
				boolean1 = true;
				break;
			
			case 13: 
				throw new IllegalStateException("\r\n unsupported");
			
			default: 
				stringBuilder.append((char)int1);
			
			}
		}

		if (int1 == -1 && stringBuilder.length() == 0) {
			return null;
		} else {
			return stringBuilder.toString();
		}
	}

	public void LoadTileDefinitions(IsoSpriteManager spriteManager, String string, int int1) {
		DebugLog.log("tiledef: loading " + string);
		boolean boolean1 = string.endsWith(".patch.tiles");
		try {
			FileInputStream fileInputStream = new FileInputStream(string);
			try {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				try {
					int int2 = readInt((InputStream)bufferedInputStream);
					int int3 = readInt((InputStream)bufferedInputStream);
					int int4 = readInt((InputStream)bufferedInputStream);
					SharedStrings sharedStrings = new SharedStrings();
					boolean boolean2 = false;
					boolean boolean3 = false;
					boolean boolean4 = Core.bDebug && Translator.getLanguage() == Translator.getDefaultLanguage();
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
					int int10 = 0;
					label723: while (true) {
						String string2;
						String string3;
						if (int10 >= int4) {
							String string4;
							ArrayList arrayList3;
							if (boolean3) {
								arrayList3 = new ArrayList(hashSet);
								Collections.sort(arrayList3);
								Iterator iterator = arrayList3.iterator();
								while (iterator.hasNext()) {
									string2 = (String)iterator.next();
									PrintStream printStream = System.out;
									string4 = string2.replaceAll(" ", "_").replaceAll("-", "_").replaceAll("\'", "").replaceAll("\\.", "");
									printStream.println(string4 + " = \"" + string2 + "\",");
								}
							}

							if (boolean4) {
								arrayList3 = new ArrayList(hashSet);
								Collections.sort(arrayList3);
								StringBuilder stringBuilder = new StringBuilder();
								Iterator iterator2 = arrayList3.iterator();
								while (iterator2.hasNext()) {
									string3 = (String)iterator2.next();
									if (Translator.getMoveableDisplayNameOrNull(string3) == null) {
										string4 = string3.replaceAll(" ", "_").replaceAll("-", "_").replaceAll("\'", "").replaceAll("\\.", "");
										stringBuilder.append(string4 + " = \"" + string3 + "\",\n");
									}
								}

								string2 = stringBuilder.toString();
								if (!string2.isEmpty()) {
									System.out.println("Missing translations in Moveables_EN.txt:\n" + string2);
								}
							}

							if (boolean2) {
								try {
									this.saveMovableStats(hashMap3, int1, int7, int8, int9, int6);
								} catch (Exception exception) {
								}
							}

							break;
						}

						String string5 = readString((InputStream)bufferedInputStream);
						string2 = string5.trim();
						string3 = readString((InputStream)bufferedInputStream);
						int int11 = readInt((InputStream)bufferedInputStream);
						int int12 = readInt((InputStream)bufferedInputStream);
						int int13 = readInt((InputStream)bufferedInputStream);
						int int14 = readInt((InputStream)bufferedInputStream);
						IsoSprite sprite;
						int int15;
						for (int int16 = 0; int16 < int14; ++int16) {
							if (boolean1) {
								sprite = (IsoSprite)spriteManager.NamedMap.get(string2 + "_" + int16);
								if (sprite == null) {
									continue;
								}
							} else if (int1 < 2) {
								sprite = spriteManager.AddSprite(string2 + "_" + int16, int1 * 100 * 1000 + 10000 + int13 * 1000 + int16);
							} else {
								sprite = spriteManager.AddSprite(string2 + "_" + int16, int1 * 512 * 512 + int13 * 512 + int16);
							}

							if (Core.bDebug) {
								if (this.allTiles.containsKey(string2)) {
									if (!boolean1) {
										((ArrayList)this.allTiles.get(string2)).add(string2 + "_" + int16);
									}
								} else {
									ArrayList arrayList4 = new ArrayList();
									arrayList4.add(string2 + "_" + int16);
									this.allTiles.put(string2, arrayList4);
								}
							}

							arrayList.add(sprite);
							if (!boolean1) {
								sprite.setName(string2 + "_" + int16);
								sprite.tileSheetIndex = int16;
							}

							if (sprite.name.contains("damaged") || sprite.name.contains("trash_")) {
								sprite.attachedFloor = true;
								sprite.getProperties().Set("attachedFloor", "true");
							}

							if (sprite.name.startsWith("f_bushes") && int16 <= 31) {
								sprite.isBush = true;
								sprite.attachedFloor = true;
							}

							int int17 = readInt((InputStream)bufferedInputStream);
							for (int int18 = 0; int18 < int17; ++int18) {
								string5 = readString((InputStream)bufferedInputStream);
								String string6 = string5.trim();
								string5 = readString((InputStream)bufferedInputStream);
								String string7 = string5.trim();
								IsoObjectType objectType = IsoObjectType.FromString(string6);
								if (objectType == IsoObjectType.MAX) {
									string6 = sharedStrings.get(string6);
									if (string6.equals("firerequirement")) {
										sprite.firerequirement = Integer.parseInt(string7);
									} else if (string6.equals("fireRequirement")) {
										sprite.firerequirement = Integer.parseInt(string7);
									} else if (string6.equals("BurntTile")) {
										sprite.burntTile = string7;
									} else if (string6.equals("ForceAmbient")) {
										sprite.forceAmbient = true;
										sprite.getProperties().Set(string6, string7);
									} else if (string6.equals("solidfloor")) {
										sprite.solidfloor = true;
										sprite.getProperties().Set(string6, string7);
									} else if (string6.equals("canBeRemoved")) {
										sprite.canBeRemoved = true;
										sprite.getProperties().Set(string6, string7);
									} else if (string6.equals("attachedFloor")) {
										sprite.attachedFloor = true;
										sprite.getProperties().Set(string6, string7);
									} else if (string6.equals("cutW")) {
										sprite.cutW = true;
										sprite.getProperties().Set(string6, string7);
									} else if (string6.equals("cutN")) {
										sprite.cutN = true;
										sprite.getProperties().Set(string6, string7);
									} else if (string6.equals("solid")) {
										sprite.solid = true;
										sprite.getProperties().Set(string6, string7);
									} else if (string6.equals("solidTrans")) {
										sprite.solidTrans = true;
										sprite.getProperties().Set(string6, string7);
									} else if (string6.equals("invisible")) {
										sprite.invisible = true;
										sprite.getProperties().Set(string6, string7);
									} else if (string6.equals("alwaysDraw")) {
										sprite.alwaysDraw = true;
										sprite.getProperties().Set(string6, string7);
									} else if (string6.equals("forceRender")) {
										sprite.forceRender = true;
										sprite.getProperties().Set(string6, string7);
									} else if ("FloorHeight".equals(string6)) {
										if ("OneThird".equals(string7)) {
											sprite.getProperties().Set(IsoFlagType.FloorHeightOneThird);
										} else if ("TwoThirds".equals(string7)) {
											sprite.getProperties().Set(IsoFlagType.FloorHeightTwoThirds);
										}
									} else if (string6.equals("MoveWithWind")) {
										sprite.moveWithWind = true;
										sprite.getProperties().Set(string6, string7);
									} else if (string6.equals("WindType")) {
										sprite.windType = Integer.parseInt(string7);
										sprite.getProperties().Set(string6, string7);
									} else if (string6.equals("RenderLayer")) {
										sprite.getProperties().Set(string6, string7);
										if ("Default".equals(string7)) {
											sprite.renderLayer = 0;
										} else if ("Floor".equals(string7)) {
											sprite.renderLayer = 1;
										}
									} else if (string6.equals("TreatAsWallOrder")) {
										sprite.treatAsWallOrder = true;
										sprite.getProperties().Set(string6, string7);
									} else {
										sprite.getProperties().Set(string6, string7);
										if ("WindowN".equals(string6) || "WindowW".equals(string6)) {
											sprite.getProperties().Set(string6, string7, false);
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
										string7 = "1";
									}

									sprite.getProperties().Set("tree", string7);
									sprite.getProperties().UnSet(IsoFlagType.solid);
									sprite.getProperties().Set(IsoFlagType.blocksight);
									int15 = Integer.parseInt(string7);
									if (string2.startsWith("vegetation_trees")) {
										int15 = 4;
									}

									if (int15 < 1) {
										int15 = 1;
									}

									if (int15 > 4) {
										int15 = 4;
									}

									if (int15 == 1 || int15 == 2) {
										sprite.getProperties().UnSet(IsoFlagType.blocksight);
									}
								}

								if (string6.equals("interior") && string7.equals("false")) {
									sprite.getProperties().Set(IsoFlagType.exterior);
								}

								if (string6.equals("HoppableN")) {
									sprite.getProperties().Set(IsoFlagType.collideN);
									sprite.getProperties().Set(IsoFlagType.canPathN);
									sprite.getProperties().Set(IsoFlagType.transparentN);
								}

								if (string6.equals("HoppableW")) {
									sprite.getProperties().Set(IsoFlagType.collideW);
									sprite.getProperties().Set(IsoFlagType.canPathW);
									sprite.getProperties().Set(IsoFlagType.transparentW);
								}

								if (string6.equals("WallN")) {
									sprite.getProperties().Set(IsoFlagType.collideN);
									sprite.getProperties().Set(IsoFlagType.cutN);
									sprite.setType(IsoObjectType.wall);
									sprite.cutN = true;
									sprite.getProperties().Set("WallN", "", false);
								}

								if (string6.equals("CantClimb")) {
									sprite.getProperties().Set(IsoFlagType.CantClimb);
								} else if (string6.equals("container")) {
									sprite.getProperties().Set(string6, string7, false);
								} else if (string6.equals("WallNTrans")) {
									sprite.getProperties().Set(IsoFlagType.collideN);
									sprite.getProperties().Set(IsoFlagType.cutN);
									sprite.getProperties().Set(IsoFlagType.transparentN);
									sprite.setType(IsoObjectType.wall);
									sprite.cutN = true;
									sprite.getProperties().Set("WallNTrans", "", false);
								} else if (string6.equals("WallW")) {
									sprite.getProperties().Set(IsoFlagType.collideW);
									sprite.getProperties().Set(IsoFlagType.cutW);
									sprite.setType(IsoObjectType.wall);
									sprite.cutW = true;
									sprite.getProperties().Set("WallW", "", false);
								} else if (string6.equals("windowN")) {
									sprite.getProperties().Set("WindowN", "WindowN");
									sprite.getProperties().Set(IsoFlagType.transparentN);
									sprite.getProperties().Set("WindowN", "WindowN", false);
								} else if (string6.equals("windowW")) {
									sprite.getProperties().Set("WindowW", "WindowW");
									sprite.getProperties().Set(IsoFlagType.transparentW);
									sprite.getProperties().Set("WindowW", "WindowW", false);
								} else if (string6.equals("cutW")) {
									sprite.getProperties().Set(IsoFlagType.cutW);
									sprite.cutW = true;
								} else if (string6.equals("cutN")) {
									sprite.getProperties().Set(IsoFlagType.cutN);
									sprite.cutN = true;
								} else if (string6.equals("WallWTrans")) {
									sprite.getProperties().Set(IsoFlagType.collideW);
									sprite.getProperties().Set(IsoFlagType.transparentW);
									sprite.getProperties().Set(IsoFlagType.cutW);
									sprite.setType(IsoObjectType.wall);
									sprite.cutW = true;
									sprite.getProperties().Set("WallWTrans", "", false);
								} else if (string6.equals("DoorWallN")) {
									sprite.getProperties().Set(IsoFlagType.cutN);
									sprite.cutN = true;
									sprite.getProperties().Set("DoorWallN", "", false);
								} else if (string6.equals("DoorWallW")) {
									sprite.getProperties().Set(IsoFlagType.cutW);
									sprite.cutW = true;
									sprite.getProperties().Set("DoorWallW", "", false);
								} else if (string6.equals("WallNW")) {
									sprite.getProperties().Set(IsoFlagType.collideN);
									sprite.getProperties().Set(IsoFlagType.cutN);
									sprite.getProperties().Set(IsoFlagType.collideW);
									sprite.getProperties().Set(IsoFlagType.cutW);
									sprite.setType(IsoObjectType.wall);
									sprite.cutW = true;
									sprite.cutN = true;
									sprite.getProperties().Set("WallNW", "", false);
								} else if (string6.equals("WallNWTrans")) {
									sprite.getProperties().Set(IsoFlagType.collideN);
									sprite.getProperties().Set(IsoFlagType.cutN);
									sprite.getProperties().Set(IsoFlagType.collideW);
									sprite.getProperties().Set(IsoFlagType.transparentN);
									sprite.getProperties().Set(IsoFlagType.transparentW);
									sprite.getProperties().Set(IsoFlagType.cutW);
									sprite.setType(IsoObjectType.wall);
									sprite.cutW = true;
									sprite.cutN = true;
									sprite.getProperties().Set("WallNWTrans", "", false);
								} else if (string6.equals("WallSE")) {
									sprite.getProperties().Set(IsoFlagType.cutW);
									sprite.getProperties().Set(IsoFlagType.WallSE);
									sprite.getProperties().Set("WallSE", "WallSE");
									sprite.cutW = true;
								} else if (string6.equals("WindowW")) {
									sprite.getProperties().Set(IsoFlagType.canPathW);
									sprite.getProperties().Set(IsoFlagType.collideW);
									sprite.getProperties().Set(IsoFlagType.cutW);
									sprite.getProperties().Set(IsoFlagType.transparentW);
									sprite.setType(IsoObjectType.windowFW);
									if (sprite.getProperties().Is(IsoFlagType.HoppableW)) {
										if (Core.bDebug) {
											DebugLog.log("ERROR: WindowW sprite shouldn\'t have HoppableW (" + sprite.getName() + ")");
										}

										sprite.getProperties().UnSet(IsoFlagType.HoppableW);
									}

									sprite.cutW = true;
								} else if (string6.equals("WindowN")) {
									sprite.getProperties().Set(IsoFlagType.canPathN);
									sprite.getProperties().Set(IsoFlagType.collideN);
									sprite.getProperties().Set(IsoFlagType.cutN);
									sprite.getProperties().Set(IsoFlagType.transparentN);
									sprite.setType(IsoObjectType.windowFN);
									if (sprite.getProperties().Is(IsoFlagType.HoppableN)) {
										if (Core.bDebug) {
											DebugLog.log("ERROR: WindowN sprite shouldn\'t have HoppableN (" + sprite.getName() + ")");
										}

										sprite.getProperties().UnSet(IsoFlagType.HoppableN);
									}

									sprite.cutN = true;
								} else if (string6.equals("UnbreakableWindowW")) {
									sprite.getProperties().Set(IsoFlagType.canPathW);
									sprite.getProperties().Set(IsoFlagType.collideW);
									sprite.getProperties().Set(IsoFlagType.cutW);
									sprite.getProperties().Set(IsoFlagType.transparentW);
									sprite.getProperties().Set(IsoFlagType.collideW);
									sprite.setType(IsoObjectType.wall);
									sprite.cutW = true;
								} else if (string6.equals("UnbreakableWindowN")) {
									sprite.getProperties().Set(IsoFlagType.canPathN);
									sprite.getProperties().Set(IsoFlagType.collideN);
									sprite.getProperties().Set(IsoFlagType.cutN);
									sprite.getProperties().Set(IsoFlagType.transparentN);
									sprite.getProperties().Set(IsoFlagType.collideN);
									sprite.setType(IsoObjectType.wall);
									sprite.cutN = true;
								} else if (string6.equals("UnbreakableWindowNW")) {
									sprite.getProperties().Set(IsoFlagType.cutN);
									sprite.getProperties().Set(IsoFlagType.transparentN);
									sprite.getProperties().Set(IsoFlagType.collideN);
									sprite.getProperties().Set(IsoFlagType.cutN);
									sprite.getProperties().Set(IsoFlagType.collideW);
									sprite.getProperties().Set(IsoFlagType.cutW);
									sprite.setType(IsoObjectType.wall);
									sprite.cutW = true;
									sprite.cutN = true;
								} else if ("NoWallLighting".equals(string6)) {
									sprite.getProperties().Set(IsoFlagType.NoWallLighting);
								} else if ("ForceAmbient".equals(string6)) {
									sprite.getProperties().Set(IsoFlagType.ForceAmbient);
								}

								if (string6.equals("name")) {
									sprite.setParentObjectName(string7);
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
								DebugLog.General.error("Window sprite has SmashedTileOffset but no GlassRemovedOffset (" + sprite.getName() + ")");
							}
						}

						this.setOpenDoorProperties(string2, arrayList);
						hashMap.clear();
						Iterator iterator3 = arrayList.iterator();
						while (true) {
							while (true) {
								String string8;
								do {
									if (!iterator3.hasNext()) {
										iterator3 = hashMap.entrySet().iterator();
										while (true) {
											while (true) {
												while (true) {
													String string9;
													ArrayList arrayList5;
													boolean boolean5;
													int int19;
													Iterator iterator4;
													boolean boolean6;
													IsoSprite sprite2;
													do {
														if (!iterator3.hasNext()) {
															arrayList.clear();
															++int10;
															continue label723;
														}

														Entry entry = (Entry)iterator3.next();
														string8 = (String)entry.getKey();
														if (!hashMap3.containsKey(string2)) {
															hashMap3.put(string2, new ArrayList());
														}

														if (!((ArrayList)hashMap3.get(string2)).contains(string8)) {
															((ArrayList)hashMap3.get(string2)).add(string8);
														}

														arrayList5 = (ArrayList)entry.getValue();
														if (arrayList5.size() == 1) {
															DebugLog.log("MOVABLES: Object has only one face defined for group: (" + string8 + ") sheet = " + string2);
														}

														if (arrayList5.size() == 3) {
															DebugLog.log("MOVABLES: Object only has 3 sprites, _might_ have a error in settings, group: (" + string8 + ") sheet = " + string2);
														}

														String[] stringArray2 = stringArray;
														int int20 = stringArray.length;
														for (int19 = 0; int19 < int20; ++int19) {
															String string10 = stringArray2[int19];
															((ArrayList)hashMap2.get(string10)).clear();
														}

														boolean5 = ((IsoSprite)arrayList5.get(0)).getProperties().Is("SpriteGridPos") && !((IsoSprite)arrayList5.get(0)).getProperties().Val("SpriteGridPos").equals("None");
														boolean6 = true;
														iterator4 = arrayList5.iterator();
														while (iterator4.hasNext()) {
															sprite2 = (IsoSprite)iterator4.next();
															boolean boolean7 = sprite2.getProperties().Is("SpriteGridPos") && !sprite2.getProperties().Val("SpriteGridPos").equals("None");
															if (boolean5 != boolean7) {
																boolean6 = false;
																DebugLog.log("MOVABLES: Difference in SpriteGrid settings for members of group: (" + string8 + ") sheet = " + string2);
																break;
															}

															if (!sprite2.getProperties().Is("Facing")) {
																boolean6 = false;
															} else {
																string9 = sprite2.getProperties().Val("Facing");
																byte byte1 = -1;
																switch (string9.hashCode()) {
																case 69: 
																	if (string9.equals("E")) {
																		byte1 = 1;
																	}

																	break;
																
																case 78: 
																	if (string9.equals("N")) {
																		byte1 = 0;
																	}

																	break;
																
																case 83: 
																	if (string9.equals("S")) {
																		byte1 = 2;
																	}

																	break;
																
																case 87: 
																	if (string9.equals("W")) {
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
																	DebugLog.log("MOVABLES: Invalid face (" + sprite2.getProperties().Val("Facing") + ") for group: (" + string8 + ") sheet = " + string2);
																	boolean6 = false;
																
																}
															}

															if (!boolean6) {
																DebugLog.log("MOVABLES: Not all members have a valid face defined for group: (" + string8 + ") sheet = " + string2);
																break;
															}
														}
													}											 while (!boolean6);

													int int21;
													int int22;
													if (!boolean5) {
														if (arrayList5.size() > 4) {
															DebugLog.log("MOVABLES: Object has too many faces defined for group: (" + string8 + ") sheet = " + string2);
														} else {
															String[] stringArray3 = stringArray;
															int15 = stringArray.length;
															for (int21 = 0; int21 < int15; ++int21) {
																string9 = stringArray3[int21];
																if (((ArrayList)hashMap2.get(string9)).size() > 1) {
																	DebugLog.log("MOVABLES: " + string9 + " face defined more than once for group: (" + string8 + ") sheet = " + string2);
																	boolean6 = false;
																}
															}

															if (boolean6) {
																++int8;
																iterator4 = arrayList5.iterator();
																while (iterator4.hasNext()) {
																	sprite2 = (IsoSprite)iterator4.next();
																	String[] stringArray4 = stringArray;
																	int int23 = stringArray.length;
																	for (int22 = 0; int22 < int23; ++int22) {
																		String string11 = stringArray4[int22];
																		ArrayList arrayList6 = (ArrayList)hashMap2.get(string11);
																		if (arrayList6.size() > 0 && arrayList6.get(0) != sprite2) {
																			sprite2.getProperties().Set(string11 + "offset", Integer.toString(arrayList.indexOf(arrayList6.get(0)) - arrayList.indexOf(sprite2)));
																		}
																	}
																}
															}
														}
													} else {
														int19 = 0;
														IsoSpriteGrid[] spriteGridArray = new IsoSpriteGrid[stringArray.length];
														int int24;
														IsoSprite sprite3;
														label693: for (int21 = 0; int21 < stringArray.length; ++int21) {
															ArrayList arrayList7 = (ArrayList)hashMap2.get(stringArray[int21]);
															if (arrayList7.size() > 0) {
																if (int19 == 0) {
																	int19 = arrayList7.size();
																}

																if (int19 != arrayList7.size()) {
																	DebugLog.log("MOVABLES: Sprite count mismatch for multi sprite movable, group: (" + string8 + ") sheet = " + string2);
																	boolean6 = false;
																	break;
																}

																arrayList2.clear();
																int22 = -1;
																int24 = -1;
																Iterator iterator5 = arrayList7.iterator();
																while (true) {
																	String string12;
																	String[] stringArray5;
																	int int25;
																	int int26;
																	if (iterator5.hasNext()) {
																		sprite3 = (IsoSprite)iterator5.next();
																		string12 = sprite3.getProperties().Val("SpriteGridPos");
																		if (!arrayList2.contains(string12)) {
																			arrayList2.add(string12);
																			stringArray5 = string12.split(",");
																			if (stringArray5.length == 2) {
																				int25 = Integer.parseInt(stringArray5[0]);
																				int26 = Integer.parseInt(stringArray5[1]);
																				if (int25 > int22) {
																					int22 = int25;
																				}

																				if (int26 > int24) {
																					int24 = int26;
																				}

																				continue;
																			}

																			DebugLog.log("MOVABLES: SpriteGrid position error for multi sprite movable, group: (" + string8 + ") sheet = " + string2);
																			boolean6 = false;
																		} else {
																			DebugLog.log("MOVABLES: double SpriteGrid position (" + string12 + ") for multi sprite movable, group: (" + string8 + ") sheet = " + string2);
																			boolean6 = false;
																		}
																	}

																	if (int22 == -1 || int24 == -1 || (int22 + 1) * (int24 + 1) != arrayList7.size()) {
																		DebugLog.log("MOVABLES: SpriteGrid dimensions error for multi sprite movable, group: (" + string8 + ") sheet = " + string2);
																		boolean6 = false;
																		break label693;
																	}

																	if (!boolean6) {
																		break label693;
																	}

																	spriteGridArray[int21] = new IsoSpriteGrid(int22 + 1, int24 + 1);
																	iterator5 = arrayList7.iterator();
																	while (iterator5.hasNext()) {
																		sprite3 = (IsoSprite)iterator5.next();
																		string12 = sprite3.getProperties().Val("SpriteGridPos");
																		stringArray5 = string12.split(",");
																		int25 = Integer.parseInt(stringArray5[0]);
																		int26 = Integer.parseInt(stringArray5[1]);
																		spriteGridArray[int21].setSprite(int25, int26, sprite3);
																	}

																	if (!spriteGridArray[int21].validate()) {
																		DebugLog.log("MOVABLES: SpriteGrid didn\'t validate for multi sprite movable, group: (" + string8 + ") sheet = " + string2);
																		boolean6 = false;
																		break label693;
																	}

																	break;
																}
															}
														}

														if (boolean6 && int19 != 0) {
															++int9;
															for (int21 = 0; int21 < stringArray.length; ++int21) {
																IsoSpriteGrid spriteGrid = spriteGridArray[int21];
																if (spriteGrid != null) {
																	IsoSprite[] spriteArray = spriteGrid.getSprites();
																	int24 = spriteArray.length;
																	for (int int27 = 0; int27 < int24; ++int27) {
																		sprite3 = spriteArray[int27];
																		sprite3.setSpriteGrid(spriteGrid);
																		for (int int28 = 0; int28 < stringArray.length; ++int28) {
																			if (int28 != int21 && spriteGridArray[int28] != null) {
																				sprite3.getProperties().Set(stringArray[int28] + "offset", Integer.toString(arrayList.indexOf(spriteGridArray[int28].getAnchorSprite()) - arrayList.indexOf(sprite3)));
																			}
																		}
																	}
																}
															}
														} else {
															DebugLog.log("MOVABLES: Error in multi sprite movable, group: (" + string8 + ") sheet = " + string2);
														}
													}
												}
											}
										}
									}

									sprite = (IsoSprite)iterator3.next();
									if (sprite.getProperties().Is("StopCar")) {
										sprite.setType(IsoObjectType.isMoveAbleObject);
									}
								}						 while (!sprite.getProperties().Is("IsMoveAble"));

								if (sprite.getProperties().Is("CustomName") && !sprite.getProperties().Val("CustomName").equals("")) {
									++int6;
									if (sprite.getProperties().Is("GroupName")) {
										String string13 = sprite.getProperties().Val("GroupName");
										string8 = string13 + " " + sprite.getProperties().Val("CustomName");
										if (!hashMap.containsKey(string8)) {
											hashMap.put(string8, new ArrayList());
										}

										((ArrayList)hashMap.get(string8)).add(sprite);
										hashSet.add(string8);
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
					}
				} catch (Throwable throwable) {
					try {
						bufferedInputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedInputStream.close();
			} catch (Throwable throwable3) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileInputStream.close();
		} catch (Exception exception2) {
			ExceptionLogger.logException(exception2);
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

		try {
			FileInputStream fileInputStream = new FileInputStream(string);
			try {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				try {
					int int2 = readInt((InputStream)bufferedInputStream);
					int int3 = readInt((InputStream)bufferedInputStream);
					int int4 = readInt((InputStream)bufferedInputStream);
					SharedStrings sharedStrings = new SharedStrings();
					for (int int5 = 0; int5 < int4; ++int5) {
						String string2 = readString((InputStream)bufferedInputStream);
						String string3 = string2.trim();
						String string4 = readString((InputStream)bufferedInputStream);
						this.tileImages.add(string4);
						int int6 = readInt((InputStream)bufferedInputStream);
						int int7 = readInt((InputStream)bufferedInputStream);
						int int8 = readInt((InputStream)bufferedInputStream);
						int int9 = readInt((InputStream)bufferedInputStream);
						for (int int10 = 0; int10 < int9; ++int10) {
							int int11 = readInt((InputStream)bufferedInputStream);
							for (int int12 = 0; int12 < int11; ++int12) {
								string2 = readString((InputStream)bufferedInputStream);
								String string5 = string2.trim();
								string2 = readString((InputStream)bufferedInputStream);
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
				} catch (Throwable throwable) {
					try {
						bufferedInputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedInputStream.close();
			} catch (Throwable throwable3) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileInputStream.close();
		} catch (Exception exception) {
			Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, (String)null, exception);
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

	private void setOpenDoorProperties(String string, ArrayList arrayList) {
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			IsoSprite sprite = (IsoSprite)arrayList.get(int1);
			if ((sprite.getType() == IsoObjectType.doorN || sprite.getType() == IsoObjectType.doorW) && !sprite.getProperties().Is(IsoFlagType.open)) {
				String string2 = sprite.getProperties().Val("DoubleDoor");
				if (string2 != null) {
					int int2 = PZMath.tryParseInt(string2, -1);
					if (int2 >= 5) {
						sprite.getProperties().Set(IsoFlagType.open);
					}
				} else {
					String string3 = sprite.getProperties().Val("GarageDoor");
					if (string3 != null) {
						int int3 = PZMath.tryParseInt(string3, -1);
						if (int3 >= 4) {
							sprite.getProperties().Set(IsoFlagType.open);
						}
					} else {
						IsoSprite sprite2 = (IsoSprite)IsoSpriteManager.instance.NamedMap.get(string + "_" + (sprite.tileSheetIndex + 2));
						if (sprite2 != null) {
							sprite2.setType(sprite.getType());
							sprite2.getProperties().Set(sprite.getType() == IsoObjectType.doorN ? IsoFlagType.doorN : IsoFlagType.doorW);
							sprite2.getProperties().Set(IsoFlagType.open);
						}
					}
				}
			}
		}
	}

	private void saveMovableStats(Map map, int int1, int int2, int int3, int int4, int int5) throws FileNotFoundException, IOException {
		File file = new File(ZomboidFileSystem.instance.getCacheDir());
		if (file.exists() && file.isDirectory()) {
			String string = ZomboidFileSystem.instance.getCacheDir();
			File file2 = new File(string + File.separator + "movables_stats_" + int1 + ".txt");
			try {
				FileWriter fileWriter = new FileWriter(file2, false);
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
						String string2 = (String)entry.getKey();
						fileWriter.write(string2 + System.lineSeparator());
						Iterator iterator2 = ((ArrayList)entry.getValue()).iterator();
						while (iterator2.hasNext()) {
							String string3 = (String)iterator2.next();
							fileWriter.write("\t" + string3 + System.lineSeparator());
						}
					}
				} catch (Throwable throwable) {
					try {
						fileWriter.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				fileWriter.close();
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

	private void loadedTileDefinitions() {
		CellLoader.smashedWindowSpriteMap.clear();
		Iterator iterator = IsoSpriteManager.instance.NamedMap.values().iterator();
		while (true) {
			IsoSprite sprite;
			PropertyContainer propertyContainer;
			do {
				if (!iterator.hasNext()) {
					return;
				}

				sprite = (IsoSprite)iterator.next();
				propertyContainer = sprite.getProperties();
			}	 while (!propertyContainer.Is(IsoFlagType.windowW) && !propertyContainer.Is(IsoFlagType.windowN));

			String string = propertyContainer.Val("SmashedTileOffset");
			if (string != null) {
				int int1 = PZMath.tryParseInt(string, 0);
				if (int1 != 0) {
					IsoSprite sprite2 = IsoSprite.getSprite(IsoSpriteManager.instance, sprite, int1);
					if (sprite2 != null) {
						CellLoader.smashedWindowSpriteMap.put(sprite2, sprite);
					}
				}
			}
		}
	}

	public boolean LoadPlayerForInfo() throws FileNotFoundException, IOException {
		if (GameClient.bClient) {
			return ClientPlayerDB.getInstance().loadNetworkPlayerInfo(1);
		} else {
			File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_p.bin");
			if (!file.exists()) {
				PlayerDB.getInstance().importPlayersFromVehiclesDB();
				return PlayerDB.getInstance().loadLocalPlayerInfo(1);
			} else {
				FileInputStream fileInputStream = new FileInputStream(file);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				synchronized (SliceY.SliceBufferLock) {
					SliceY.SliceBuffer.clear();
					int int1 = bufferedInputStream.read(SliceY.SliceBuffer.array());
					SliceY.SliceBuffer.limit(int1);
					bufferedInputStream.close();
					byte byte1 = SliceY.SliceBuffer.get();
					byte byte2 = SliceY.SliceBuffer.get();
					byte byte3 = SliceY.SliceBuffer.get();
					byte byte4 = SliceY.SliceBuffer.get();
					int int2 = -1;
					if (byte1 == 80 && byte2 == 76 && byte3 == 89 && byte4 == 82) {
						int2 = SliceY.SliceBuffer.getInt();
					} else {
						SliceY.SliceBuffer.rewind();
					}

					if (int2 >= 69) {
						String string = GameWindow.ReadString(SliceY.SliceBuffer);
						if (GameClient.bClient && int2 < 71) {
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
	}

	public void init() throws FileNotFoundException, IOException, WorldDictionaryException {
		if (!Core.bTutorial) {
			this.randomizedBuildingList.add(new RBSafehouse());
			this.randomizedBuildingList.add(new RBBurnt());
			this.randomizedBuildingList.add(new RBOther());
			this.randomizedBuildingList.add(new RBLooted());
			this.randomizedBuildingList.add(new RBBurntFireman());
			this.randomizedBuildingList.add(new RBBurntCorpse());
			this.randomizedBuildingList.add(new RBShopLooted());
			this.randomizedBuildingList.add(new RBKateAndBaldspot());
			this.randomizedBuildingList.add(new RBStripclub());
			this.randomizedBuildingList.add(new RBSchool());
			this.randomizedBuildingList.add(new RBSpiffo());
			this.randomizedBuildingList.add(new RBPizzaWhirled());
			this.randomizedBuildingList.add(new RBPileOCrepe());
			this.randomizedBuildingList.add(new RBCafe());
			this.randomizedBuildingList.add(new RBBar());
			this.randomizedBuildingList.add(new RBOffice());
			this.randomizedBuildingList.add(new RBHairSalon());
			this.randomizedBuildingList.add(new RBClinic());
			this.randomizedVehicleStoryList.add(new RVSUtilityVehicle());
			this.randomizedVehicleStoryList.add(new RVSConstructionSite());
			this.randomizedVehicleStoryList.add(new RVSBurntCar());
			this.randomizedVehicleStoryList.add(new RVSPoliceBlockadeShooting());
			this.randomizedVehicleStoryList.add(new RVSPoliceBlockade());
			this.randomizedVehicleStoryList.add(new RVSCarCrash());
			this.randomizedVehicleStoryList.add(new RVSAmbulanceCrash());
			this.randomizedVehicleStoryList.add(new RVSCarCrashCorpse());
			this.randomizedVehicleStoryList.add(new RVSChangingTire());
			this.randomizedVehicleStoryList.add(new RVSFlippedCrash());
			this.randomizedVehicleStoryList.add(new RVSBanditRoad());
			this.randomizedVehicleStoryList.add(new RVSTrailerCrash());
			this.randomizedVehicleStoryList.add(new RVSCrashHorde());
			this.randomizedZoneList.add(new RZSForestCamp());
			this.randomizedZoneList.add(new RZSForestCampEaten());
			this.randomizedZoneList.add(new RZSBuryingCamp());
			this.randomizedZoneList.add(new RZSBeachParty());
			this.randomizedZoneList.add(new RZSFishingTrip());
			this.randomizedZoneList.add(new RZSBBQParty());
			this.randomizedZoneList.add(new RZSHunterCamp());
			this.randomizedZoneList.add(new RZSSexyTime());
			this.randomizedZoneList.add(new RZSTrapperCamp());
			this.randomizedZoneList.add(new RZSBaseball());
			this.randomizedZoneList.add(new RZSMusicFestStage());
			this.randomizedZoneList.add(new RZSMusicFest());
		}

		zombie.randomizedWorld.randomizedBuilding.RBBasic.getUniqueRDSSpawned().clear();
		if (!GameClient.bClient && !GameServer.bServer) {
			BodyDamageSync.instance = null;
		} else {
			BodyDamageSync.instance = new BodyDamageSync();
		}

		if (GameServer.bServer) {
			Core.GameSaveWorld = GameServer.ServerName;
			String string = ZomboidFileSystem.instance.getCurrentSaveDir();
			File file = new File(string);
			if (!file.exists()) {
				GameServer.ResetID = Rand.Next(10000000);
				ServerOptions.instance.putSaveOption("ResetID", String.valueOf(GameServer.ResetID));
			}

			LuaManager.GlobalObject.createWorld(Core.GameSaveWorld);
		}

		SavedWorldVersion = this.readWorldVersion();
		int int1;
		if (!GameServer.bServer) {
			File file2 = ZomboidFileSystem.instance.getFileInCurrentSave("map_ver.bin");
			try {
				FileInputStream fileInputStream = new FileInputStream(file2);
				try {
					DataInputStream dataInputStream = new DataInputStream(fileInputStream);
					try {
						int1 = dataInputStream.readInt();
						if (int1 >= 25) {
							String string2 = GameWindow.ReadString(dataInputStream);
							if (!GameClient.bClient) {
								Core.GameMap = string2;
							}
						}

						if (int1 >= 74) {
							this.setDifficulty(GameWindow.ReadString(dataInputStream));
						}
					} catch (Throwable throwable) {
						try {
							dataInputStream.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					dataInputStream.close();
				} catch (Throwable throwable3) {
					try {
						fileInputStream.close();
					} catch (Throwable throwable4) {
						throwable3.addSuppressed(throwable4);
					}

					throw throwable3;
				}

				fileInputStream.close();
			} catch (FileNotFoundException fileNotFoundException) {
			}
		}

		if (!GameServer.bServer || !GameServer.bSoftReset) {
			this.MetaGrid.CreateStep1();
		}

		LuaEventManager.triggerEvent("OnPreDistributionMerge");
		LuaEventManager.triggerEvent("OnDistributionMerge");
		LuaEventManager.triggerEvent("OnPostDistributionMerge");
		ItemPickerJava.Parse();
		VehiclesDB2.instance.init();
		LuaEventManager.triggerEvent("OnInitWorld");
		if (!GameClient.bClient) {
			SandboxOptions.instance.load();
		}

		this.bHydroPowerOn = GameTime.getInstance().NightsSurvived < SandboxOptions.getInstance().getElecShutModifier();
		ZomboidGlobals.toLua();
		ItemPickerJava.InitSandboxLootSettings();
		this.SurvivorDescriptors.clear();
		IsoSpriteManager.instance.Dispose();
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
						GameClient.connection.forceDisconnect("world-timeout-response");
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
		IsoSpriteManager spriteManager = IsoSpriteManager.instance;
		this.tileImages.clear();
		ZomboidFileSystem zomboidFileSystem = ZomboidFileSystem.instance;
		this.LoadTileDefinitionsPropertyStrings(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions.tiles"), 0);
		this.LoadTileDefinitionsPropertyStrings(spriteManager, zomboidFileSystem.getMediaPath("newtiledefinitions.tiles"), 1);
		this.LoadTileDefinitionsPropertyStrings(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions_erosion.tiles"), 2);
		this.LoadTileDefinitionsPropertyStrings(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions_apcom.tiles"), 3);
		this.LoadTileDefinitionsPropertyStrings(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions_overlays.tiles"), 4);
		this.LoadTileDefinitionsPropertyStrings(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions_noiseworks.patch.tiles"), -1);
		ZomboidFileSystem.instance.loadModTileDefPropertyStrings();
		this.SetCustomPropertyValues();
		this.GenerateTilePropertyLookupTables();
		this.LoadTileDefinitions(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions.tiles"), 0);
		this.LoadTileDefinitions(spriteManager, zomboidFileSystem.getMediaPath("newtiledefinitions.tiles"), 1);
		this.LoadTileDefinitions(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions_erosion.tiles"), 2);
		this.LoadTileDefinitions(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions_apcom.tiles"), 3);
		this.LoadTileDefinitions(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions_overlays.tiles"), 4);
		this.LoadTileDefinitions(spriteManager, zomboidFileSystem.getMediaPath("tiledefinitions_noiseworks.patch.tiles"), -1);
		this.JumboTreeDefinitions(spriteManager, 5);
		ZomboidFileSystem.instance.loadModTileDefs();
		GameLoadingState.GameLoadingString = "";
		spriteManager.AddSprite("media/ui/missing-tile.png");
		LuaEventManager.triggerEvent("OnLoadedTileDefinitions", spriteManager);
		this.loadedTileDefinitions();
		if (GameServer.bServer && GameServer.bSoftReset) {
			WorldConverter.instance.softreset();
		}

		try {
			WeatherFxMask.init();
		} catch (Exception exception2) {
			System.out.print(exception2.getStackTrace());
		}

		TemplateText.Initialize();
		IsoRegions.init();
		ObjectRenderEffects.init();
		WorldConverter.instance.convert(Core.GameSaveWorld, spriteManager);
		if (!GameLoadingState.build23Stop) {
			SandboxOptions.instance.handleOldZombiesFile2();
			GameTime.getInstance().init();
			GameTime.getInstance().load();
			ImprovedFog.init();
			ZomboidRadio.getInstance().Init(SavedWorldVersion);
			GlobalModData.instance.init();
			if (GameServer.bServer && Core.getInstance().getPoisonousBerry() == null) {
				Core.getInstance().initPoisonousBerry();
			}

			if (GameServer.bServer && Core.getInstance().getPoisonousMushroom() == null) {
				Core.getInstance().initPoisonousMushroom();
			}

			ErosionGlobals.Boot(spriteManager);
			WorldDictionary.init();
			WorldMarkers.instance.init();
			if (GameServer.bServer) {
				SharedDescriptors.initSharedDescriptors();
			}

			PersistentOutfits.instance.init();
			VirtualZombieManager.instance.init();
			VehicleIDMap.instance.Reset();
			VehicleManager.instance = new VehicleManager();
			GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_InitMap");
			this.MetaGrid.CreateStep2();
			ClimateManager.getInstance().init(this.MetaGrid);
			SafeHouse.init();
			if (!GameClient.bClient) {
				StashSystem.init();
			}

			LuaEventManager.triggerEvent("OnLoadMapZones");
			this.MetaGrid.load();
			this.MetaGrid.loadZones();
			this.MetaGrid.processZones();
			LuaEventManager.triggerEvent("OnLoadedMapZones");
			if (GameServer.bServer) {
				ServerMap.instance.init(this.MetaGrid);
			}

			boolean boolean1 = false;
			boolean boolean2 = false;
			if (GameClient.bClient) {
				if (ClientPlayerDB.getInstance().clientLoadNetworkPlayer() && ClientPlayerDB.getInstance().isAliveMainNetworkPlayer()) {
					boolean2 = true;
				}
			} else {
				boolean2 = PlayerDBHelper.isPlayerAlive(ZomboidFileSystem.instance.getCurrentSaveDir(), 1);
			}

			if (GameServer.bServer) {
				ServerPlayerDB.setAllow(true);
			}

			if (!GameClient.bClient && !GameServer.bServer) {
				PlayerDB.setAllow(true);
			}

			boolean boolean3 = false;
			boolean boolean4 = false;
			boolean boolean5 = false;
			SafeHouse safeHouse;
			int int2;
			if (boolean2) {
				boolean1 = true;
				if (!this.LoadPlayerForInfo()) {
					return;
				}

				WorldX = IsoChunkMap.SWorldX[IsoPlayer.getPlayerIndex()];
				WorldY = IsoChunkMap.SWorldY[IsoPlayer.getPlayerIndex()];
				int2 = IsoChunkMap.WorldXA;
				int int3 = IsoChunkMap.WorldYA;
				int int4 = IsoChunkMap.WorldZA;
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
						for (int int5 = 0; int5 < SafeHouse.getSafehouseList().size(); ++int5) {
							safeHouse = (SafeHouse)SafeHouse.getSafehouseList().get(int5);
							if (safeHouse.getPlayers().contains(GameClient.username) && safeHouse.isRespawnInSafehouse(GameClient.username)) {
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
			KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("selectedDebugScenario");
			int int6;
			int int7;
			int int8;
			if (kahluaTable != null) {
				KahluaTable kahluaTable2 = (KahluaTable)kahluaTable.rawget("startLoc");
				int6 = ((Double)kahluaTable2.rawget("x")).intValue();
				int7 = ((Double)kahluaTable2.rawget("y")).intValue();
				int8 = ((Double)kahluaTable2.rawget("z")).intValue();
				IsoChunkMap.WorldXA = int6;
				IsoChunkMap.WorldYA = int7;
				IsoChunkMap.WorldZA = int8;
				WorldX = IsoChunkMap.WorldXA / 10;
				WorldY = IsoChunkMap.WorldYA / 10;
			}

			MapCollisionData.instance.init(instance.getMetaGrid());
			ZombiePopulationManager.instance.init(instance.getMetaGrid());
			PolygonalMap2.instance.init(instance.getMetaGrid());
			GlobalObjectLookup.init(instance.getMetaGrid());
			if (!GameServer.bServer) {
				SpawnPoints.instance.initSinglePlayer();
			}

			WorldStreamer.instance.create();
			this.CurrentCell = CellLoader.LoadCellBinaryChunk(spriteManager, WorldX, WorldY);
			ClimateManager.getInstance().postCellLoadSetSnow();
			GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_LoadWorld");
			MapCollisionData.instance.start();
			MapItem.LoadWorldMap();
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
			if (boolean1 && SystemDisabler.doPlayerCreation) {
				this.CurrentCell.LoadPlayer(SavedWorldVersion);
				if (GameClient.bClient) {
					IsoPlayer.getInstance().setUsername(GameClient.username);
				}

				ZomboidRadio.getInstance().getRecordedMedia().handleLegacyListenedLines(IsoPlayer.getInstance());
			} else {
				ZomboidRadio.getInstance().getRecordedMedia().handleLegacyListenedLines((IsoPlayer)null);
				safeHouse = null;
				if (IsoPlayer.numPlayers == 0) {
					IsoPlayer.numPlayers = 1;
				}

				int6 = IsoChunkMap.WorldXA;
				int7 = IsoChunkMap.WorldYA;
				int8 = IsoChunkMap.WorldZA;
				if (GameClient.bClient && !ServerOptions.instance.SpawnPoint.getValue().isEmpty()) {
					String[] stringArray2 = ServerOptions.instance.SpawnPoint.getValue().split(",");
					if (stringArray2.length != 3) {
						DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
					} else {
						try {
							int int9 = new Integer(stringArray2[0].trim());
							int int10 = new Integer(stringArray2[1].trim());
							int int11 = new Integer(stringArray2[2].trim());
							if (GameClient.bClient && ServerOptions.instance.SafehouseAllowRespawn.getValue()) {
								for (int int12 = 0; int12 < SafeHouse.getSafehouseList().size(); ++int12) {
									SafeHouse safeHouse2 = (SafeHouse)SafeHouse.getSafehouseList().get(int12);
									if (safeHouse2.getPlayers().contains(GameClient.username) && safeHouse2.isRespawnInSafehouse(GameClient.username)) {
										int9 = safeHouse2.getX() + safeHouse2.getH() / 2;
										int10 = safeHouse2.getY() + safeHouse2.getW() / 2;
										int11 = 0;
									}
								}
							}

							if (this.CurrentCell.getGridSquare(int9, int10, int11) != null) {
								int6 = int9;
								int7 = int10;
								int8 = int11;
							}
						} catch (NumberFormatException numberFormatException2) {
							DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
						}
					}
				}

				IsoGridSquare square = this.CurrentCell.getGridSquare(int6, int7, int8);
				if (SystemDisabler.doPlayerCreation && !GameServer.bServer) {
					if (square != null && square.isFree(false) && square.getRoom() != null) {
						IsoGridSquare square2 = square;
						square = square.getRoom().getFreeTile();
						if (square == null) {
							square = square2;
						}
					}

					IsoPlayer player = null;
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
							throw new RuntimeException("can\'t create player at x,y,z=" + int6 + "," + int7 + "," + int8 + " because the square is null");
						}

						WorldSimulation.instance.create();
						player = new IsoPlayer(instance.CurrentCell, this.getLuaPlayerDesc(), square.getX(), square.getY(), square.getZ());
						if (GameClient.bClient) {
							player.setUsername(GameClient.username);
						}

						player.setDir(IsoDirections.SE);
						player.sqlID = 1;
						IsoPlayer.players[0] = player;
						IsoPlayer.setInstance(player);
						IsoCamera.CamCharacter = player;
					}

					IsoPlayer player2 = IsoPlayer.getInstance();
					player2.applyTraits(this.getLuaTraits());
					ProfessionFactory.Profession profession = ProfessionFactory.getProfession(player2.getDescriptor().getProfession());
					Iterator iterator2;
					String string3;
					if (profession != null && !profession.getFreeRecipes().isEmpty()) {
						iterator2 = profession.getFreeRecipes().iterator();
						while (iterator2.hasNext()) {
							string3 = (String)iterator2.next();
							player2.getKnownRecipes().add(string3);
						}
					}

					iterator2 = this.getLuaTraits().iterator();
					label341: while (true) {
						TraitFactory.Trait trait;
						do {
							do {
								if (!iterator2.hasNext()) {
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

									LuaEventManager.triggerEvent("OnNewGame", player, square);
									break label341;
								}

								string3 = (String)iterator2.next();
								trait = TraitFactory.getTrait(string3);
							}					 while (trait == null);
						}				 while (trait.getFreeRecipes().isEmpty());

						Iterator iterator3 = trait.getFreeRecipes().iterator();
						while (iterator3.hasNext()) {
							String string4 = (String)iterator3.next();
							player2.getKnownRecipes().add(string4);
						}
					}
				}
			}

			if (PlayerDB.isAllow()) {
				PlayerDB.getInstance().m_canSavePlayers = true;
			}

			if (ClientPlayerDB.isAllow()) {
				ClientPlayerDB.getInstance().canSavePlayers = true;
			}

			TutorialManager.instance.ActiveControlZombies = false;
			ReanimatedPlayers.instance.loadReanimatedPlayers();
			if (IsoPlayer.getInstance() != null) {
				if (GameClient.bClient) {
					int int13 = (int)IsoPlayer.getInstance().getX();
					int1 = (int)IsoPlayer.getInstance().getY();
					int2 = (int)IsoPlayer.getInstance().getZ();
					while (int2 > 0) {
						IsoGridSquare square4 = this.CurrentCell.getGridSquare(int13, int1, int2);
						if (square4 != null && square4.TreatAsSolidFloor()) {
							break;
						}

						--int2;
						IsoPlayer.getInstance().setZ((float)int2);
					}
				}

				IsoPlayer.getInstance().setCurrent(this.CurrentCell.getGridSquare((int)IsoPlayer.getInstance().getX(), (int)IsoPlayer.getInstance().getY(), (int)IsoPlayer.getInstance().getZ()));
			}

			if (!this.bLoaded) {
				if (!this.CurrentCell.getBuildingList().isEmpty()) {
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
			initMessaging();
			WorldDictionary.onWorldLoaded();
		}
	}

	int readWorldVersion() {
		File file;
		FileInputStream fileInputStream;
		DataInputStream dataInputStream;
		if (GameServer.bServer) {
			file = ZomboidFileSystem.instance.getFileInCurrentSave("map_t.bin");
			try {
				fileInputStream = new FileInputStream(file);
				label107: {
					int int1;
					try {
						label118: {
							dataInputStream = new DataInputStream(fileInputStream);
							try {
								byte byte1 = dataInputStream.readByte();
								byte byte2 = dataInputStream.readByte();
								byte byte3 = dataInputStream.readByte();
								byte byte4 = dataInputStream.readByte();
								if (byte1 == 71 && byte2 == 77 && byte3 == 84 && byte4 == 77) {
									int1 = dataInputStream.readInt();
									break label118;
								}
							} catch (Throwable throwable) {
								try {
									dataInputStream.close();
								} catch (Throwable throwable2) {
									throwable.addSuppressed(throwable2);
								}

								throw throwable;
							}

							dataInputStream.close();
							break label107;
						}

						dataInputStream.close();
					} catch (Throwable throwable3) {
						try {
							fileInputStream.close();
						} catch (Throwable throwable4) {
							throwable3.addSuppressed(throwable4);
						}

						throw throwable3;
					}

					fileInputStream.close();
					return int1;
				}

				fileInputStream.close();
			} catch (FileNotFoundException fileNotFoundException) {
			} catch (IOException ioException) {
				ExceptionLogger.logException(ioException);
			}

			return -1;
		} else {
			file = ZomboidFileSystem.instance.getFileInCurrentSave("map_ver.bin");
			try {
				fileInputStream = new FileInputStream(file);
				int int2;
				try {
					dataInputStream = new DataInputStream(fileInputStream);
					try {
						int2 = dataInputStream.readInt();
					} catch (Throwable throwable5) {
						try {
							dataInputStream.close();
						} catch (Throwable throwable6) {
							throwable5.addSuppressed(throwable6);
						}

						throw throwable5;
					}

					dataInputStream.close();
				} catch (Throwable throwable7) {
					try {
						fileInputStream.close();
					} catch (Throwable throwable8) {
						throwable7.addSuppressed(throwable8);
					}

					throw throwable7;
				}

				fileInputStream.close();
				return int2;
			} catch (FileNotFoundException fileNotFoundException2) {
			} catch (IOException ioException2) {
				ExceptionLogger.logException(ioException2);
			}

			return -1;
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
		IsoSpriteManager.instance.Dispose();
		this.CurrentCell = null;
		CellLoader.wanderRoom = null;
		IsoLot.Dispose();
		IsoGameCharacter.getSurvivorMap().clear();
		IsoPlayer.getInstance().setCurrent((IsoGridSquare)null);
		IsoPlayer.getInstance().setLast((IsoGridSquare)null);
		IsoPlayer.getInstance().square = null;
		RainManager.reset();
		IsoFireManager.Reset();
		ObjectAmbientEmitters.Reset();
		ZombieVocalsManager.Reset();
		IsoWaterFlow.Reset();
		this.MetaGrid.Dispose();
		instance = new IsoWorld();
	}

	public void setDrawWorld(boolean boolean1) {
		this.bDrawWorld = boolean1;
	}

	public void sceneCullZombies() {
		this.zombieWithModel.clear();
		this.zombieWithoutModel.clear();
		int int1;
		for (int1 = 0; int1 < this.CurrentCell.getZombieList().size(); ++int1) {
			IsoZombie zombie = (IsoZombie)this.CurrentCell.getZombieList().get(int1);
			boolean boolean1 = false;
			for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
				IsoPlayer player = IsoPlayer.players[int2];
				if (player != null && zombie.current != null) {
					float float1 = (float)zombie.getScreenProperX(int2);
					float float2 = (float)zombie.getScreenProperY(int2);
					if (!(float1 < -100.0F) && !(float2 < -100.0F) && !(float1 > (float)(Core.getInstance().getOffscreenWidth(int2) + 100)) && !(float2 > (float)(Core.getInstance().getOffscreenHeight(int2) + 100)) && (zombie.getAlpha(int2) != 0.0F && zombie.legsSprite.def.alpha != 0.0F || zombie.current.isCouldSee(int2))) {
						boolean1 = true;
						break;
					}
				}
			}

			if (boolean1 && zombie.isCurrentState(FakeDeadZombieState.instance())) {
				boolean1 = false;
			}

			if (boolean1) {
				this.zombieWithModel.add(zombie);
			} else {
				this.zombieWithoutModel.add(zombie);
			}
		}

		Collections.sort(this.zombieWithModel, compScoreToPlayer);
		int1 = 0;
		int int3 = 0;
		int int4 = 0;
		short short1 = 510;
		PerformanceSettings.AnimationSkip = 0;
		int int5;
		IsoZombie zombie2;
		for (int5 = 0; int5 < this.zombieWithModel.size(); ++int5) {
			zombie2 = (IsoZombie)this.zombieWithModel.get(int5);
			if (int4 < short1) {
				if (!zombie2.Ghost) {
					++int3;
					++int4;
					zombie2.setSceneCulled(false);
					if (zombie2.legsSprite != null && zombie2.legsSprite.modelSlot != null) {
						if (int3 > PerformanceSettings.ZombieAnimationSpeedFalloffCount) {
							++int1;
							int3 = 0;
						}

						if (int4 < PerformanceSettings.ZombieBonusFullspeedFalloff) {
							zombie2.legsSprite.modelSlot.model.setInstanceSkip(int3 / PerformanceSettings.ZombieBonusFullspeedFalloff);
							int3 = 0;
						} else {
							zombie2.legsSprite.modelSlot.model.setInstanceSkip(int1 + PerformanceSettings.AnimationSkip);
						}

						if (zombie2.legsSprite.modelSlot.model.AnimPlayer != null) {
							if (int4 >= PerformanceSettings.numberZombiesBlended) {
								zombie2.legsSprite.modelSlot.model.AnimPlayer.bDoBlending = false;
							} else {
								zombie2.legsSprite.modelSlot.model.AnimPlayer.bDoBlending = !zombie2.isAlphaAndTargetZero(0) || !zombie2.isAlphaAndTargetZero(1) || !zombie2.isAlphaAndTargetZero(2) || !zombie2.isAlphaAndTargetZero(3);
							}
						}
					}
				}
			} else {
				zombie2.setSceneCulled(true);
				if (zombie2.hasAnimationPlayer()) {
					zombie2.getAnimationPlayer().bDoBlending = false;
				}
			}
		}

		for (int5 = 0; int5 < this.zombieWithoutModel.size(); ++int5) {
			zombie2 = (IsoZombie)this.zombieWithoutModel.get(int5);
			if (zombie2.hasActiveModel()) {
				zombie2.setSceneCulled(true);
			}

			if (zombie2.hasAnimationPlayer()) {
				zombie2.getAnimationPlayer().bDoBlending = false;
			}
		}
	}

	public void render() {
		IsoWorld.s_performance.isoWorldRender.invokeAndMeasure(this, IsoWorld::renderInternal);
	}

	private void renderInternal() {
		if (this.bDrawWorld) {
			if (IsoCamera.CamCharacter != null) {
				SpriteRenderer.instance.doCoreIntParam(0, IsoCamera.CamCharacter.x);
				SpriteRenderer.instance.doCoreIntParam(1, IsoCamera.CamCharacter.y);
				SpriteRenderer.instance.doCoreIntParam(2, IsoCamera.CamCharacter.z);
				try {
					this.sceneCullZombies();
				} catch (Throwable throwable) {
					ExceptionLogger.logException(throwable);
				}

				try {
					WeatherFxMask.initMask();
					DeadBodyAtlas.instance.render();
					WorldItemAtlas.instance.render();
					this.CurrentCell.render();
					this.DrawIsoCursorHelper();
					DeadBodyAtlas.instance.renderDebug();
					PolygonalMap2.instance.render();
					WorldSoundManager.instance.render();
					WorldFlares.debugRender();
					WorldMarkers.instance.debugRender();
					ObjectAmbientEmitters.getInstance().render();
					ZombieVocalsManager.instance.render();
					LineDrawer.render();
					WeatherFxMask.renderFxMask(IsoCamera.frameState.playerIndex);
					if (GameClient.bClient) {
						ClientServerMap.render(IsoCamera.frameState.playerIndex);
						PassengerMap.render(IsoCamera.frameState.playerIndex);
					}

					SkyBox.getInstance().render();
				} catch (Throwable throwable2) {
					ExceptionLogger.logException(throwable2);
				}
			}
		}
	}

	private void DrawIsoCursorHelper() {
		if (Core.getInstance().getOffscreenBuffer() == null) {
			IsoPlayer player = IsoPlayer.getInstance();
			if (player != null && !player.isDead() && player.isAiming() && player.PlayerIndex == 0 && player.JoypadBind == -1) {
				if (!GameTime.isGamePaused()) {
					float float1 = 0.05F;
					switch (Core.getInstance().getIsoCursorVisibility()) {
					case 0: 
						return;
					
					case 1: 
						float1 = 0.05F;
						break;
					
					case 2: 
						float1 = 0.1F;
						break;
					
					case 3: 
						float1 = 0.15F;
						break;
					
					case 4: 
						float1 = 0.3F;
						break;
					
					case 5: 
						float1 = 0.5F;
						break;
					
					case 6: 
						float1 = 0.75F;
					
					}

					if (Core.getInstance().isFlashIsoCursor()) {
						if (this.flashIsoCursorInc) {
							this.flashIsoCursorA += 0.1F;
							if (this.flashIsoCursorA >= 1.0F) {
								this.flashIsoCursorInc = false;
							}
						} else {
							this.flashIsoCursorA -= 0.1F;
							if (this.flashIsoCursorA <= 0.0F) {
								this.flashIsoCursorInc = true;
							}
						}

						float1 = this.flashIsoCursorA;
					}

					Texture texture = Texture.getSharedTexture("media/ui/isocursor.png");
					int int1 = (int)((float)(texture.getWidth() * Core.TileScale) / 2.0F);
					int int2 = (int)((float)(texture.getHeight() * Core.TileScale) / 2.0F);
					SpriteRenderer.instance.setDoAdditive(true);
					SpriteRenderer.instance.renderi(texture, Mouse.getX() - int1 / 2, Mouse.getY() - int2 / 2, int1, int2, float1, float1, float1, float1, (Consumer)null);
					SpriteRenderer.instance.setDoAdditive(false);
				}
			}
		}
	}

	public void update() {
		IsoWorld.s_performance.isoWorldUpdate.invokeAndMeasure(this, IsoWorld::updateInternal);
	}

	private void updateInternal() {
		++this.m_frameNo;
		try {
			if (GameServer.bServer) {
				VehicleManager.instance.serverUpdate();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		WorldSimulation.instance.update();
		ImprovedFog.update();
		this.helicopter.update();
		long long1 = System.currentTimeMillis();
		if (long1 - this.emitterUpdateMS >= 30L) {
			this.emitterUpdateMS = long1;
			this.emitterUpdate = true;
		} else {
			this.emitterUpdate = false;
		}

		int int1;
		for (int1 = 0; int1 < this.currentEmitters.size(); ++int1) {
			BaseSoundEmitter baseSoundEmitter = (BaseSoundEmitter)this.currentEmitters.get(int1);
			if (this.emitterUpdate || baseSoundEmitter.hasSoundsToStart()) {
				baseSoundEmitter.tick();
			}

			if (baseSoundEmitter.isEmpty()) {
				FMODSoundEmitter fMODSoundEmitter = (FMODSoundEmitter)Type.tryCastTo(baseSoundEmitter, FMODSoundEmitter.class);
				if (fMODSoundEmitter != null) {
					fMODSoundEmitter.clearParameters();
				}

				this.currentEmitters.remove(int1);
				this.freeEmitters.push(baseSoundEmitter);
				IsoObject object = (IsoObject)this.emitterOwners.remove(baseSoundEmitter);
				if (object != null && object.emitter == baseSoundEmitter) {
					object.emitter = null;
				}

				--int1;
			}
		}

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
		for (int1 = 0; int1 < this.CurrentCell.getBuildingList().size(); ++int1) {
			((IsoBuilding)this.CurrentCell.getBuildingList().get(int1)).update();
		}

		ClimateManager.getInstance().update();
		ObjectRenderEffects.updateStatic();
		this.CurrentCell.update();
		IsoRegions.update();
		HaloTextHelper.update();
		CollisionManager.instance.ResolveContacts();
		for (int1 = 0; int1 < this.AddCoopPlayers.size(); ++int1) {
			AddCoopPlayer addCoopPlayer = (AddCoopPlayer)this.AddCoopPlayers.get(int1);
			addCoopPlayer.update();
			if (addCoopPlayer.isFinished()) {
				this.AddCoopPlayers.remove(int1--);
			}
		}

		if (!GameServer.bServer) {
			IsoPlayer.UpdateRemovedEmitters();
		}

		try {
			if (PlayerDB.isAvailable()) {
				PlayerDB.getInstance().updateMain();
			}

			if (ClientPlayerDB.isAvailable()) {
				ClientPlayerDB.getInstance().updateMain();
			}

			VehiclesDB2.instance.updateMain();
		} catch (Exception exception2) {
			ExceptionLogger.logException(exception2);
		}

		if (this.updateSafehousePlayers > 0 && (GameServer.bServer || GameClient.bClient)) {
			--this.updateSafehousePlayers;
			if (this.updateSafehousePlayers == 0) {
				this.updateSafehousePlayers = 200;
				SafeHouse.updateSafehousePlayersConnected();
			}
		}

		m_animationRecorderDiscard = false;
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

	@Deprecated
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

	public ArrayList getRandomizedZoneList() {
		return this.randomizedZoneList;
	}

	public ArrayList getRandomizedBuildingList() {
		return this.randomizedBuildingList;
	}

	public ArrayList getRandomizedVehicleStoryList() {
		return this.randomizedVehicleStoryList;
	}

	public RandomizedVehicleStoryBase getRandomizedVehicleStoryByName(String string) {
		for (int int1 = 0; int1 < this.randomizedVehicleStoryList.size(); ++int1) {
			RandomizedVehicleStoryBase randomizedVehicleStoryBase = (RandomizedVehicleStoryBase)this.randomizedVehicleStoryList.get(int1);
			if (randomizedVehicleStoryBase.getName().equalsIgnoreCase(string)) {
				return randomizedVehicleStoryBase;
			}
		}

		return null;
	}

	public RandomizedBuildingBase getRBBasic() {
		return this.RBBasic;
	}

	public String getDifficulty() {
		return Core.getDifficulty();
	}

	public void setDifficulty(String string) {
		Core.setDifficulty(string);
	}

	public static boolean getZombiesDisabled() {
		return NoZombies || !SystemDisabler.doZombieCreation || SandboxOptions.instance.Zombies.getValue() == 6;
	}

	public static boolean getZombiesEnabled() {
		return !getZombiesDisabled();
	}

	public ClimateManager getClimateManager() {
		return ClimateManager.getInstance();
	}

	public IsoPuddles getPuddlesManager() {
		return IsoPuddles.getInstance();
	}

	public static int getWorldVersion() {
		return 194;
	}

	public HashMap getSpawnedZombieZone() {
		return this.spawnedZombieZone;
	}

	public int getTimeSinceLastSurvivorInHorde() {
		return this.timeSinceLastSurvivorInHorde;
	}

	public void setTimeSinceLastSurvivorInHorde(int int1) {
		this.timeSinceLastSurvivorInHorde = int1;
	}

	public float getWorldAgeDays() {
		float float1 = (float)GameTime.getInstance().getWorldAgeHours() / 24.0F;
		float1 += (float)((SandboxOptions.instance.TimeSinceApo.getValue() - 1) * 30);
		return float1;
	}

	public HashMap getAllTiles() {
		return this.allTiles;
	}

	public ArrayList getAllTilesName() {
		ArrayList arrayList = new ArrayList();
		Iterator iterator = this.allTiles.keySet().iterator();
		while (iterator.hasNext()) {
			arrayList.add((String)iterator.next());
		}

		Collections.sort(arrayList);
		return arrayList;
	}

	public ArrayList getAllTiles(String string) {
		return (ArrayList)this.allTiles.get(string);
	}

	public boolean isHydroPowerOn() {
		return this.bHydroPowerOn;
	}

	public void setHydroPowerOn(boolean boolean1) {
		this.bHydroPowerOn = boolean1;
	}

	public ArrayList getTileImageNames() {
		return this.tileImages;
	}

	private static class CompScoreToPlayer implements Comparator {

		public int compare(IsoZombie zombie, IsoZombie zombie2) {
			float float1 = this.getScore(zombie);
			float float2 = this.getScore(zombie2);
			if (float1 < float2) {
				return 1;
			} else {
				return float1 > float2 ? -1 : 0;
			}
		}

		public float getScore(IsoZombie zombie) {
			float float1 = Float.MIN_VALUE;
			for (int int1 = 0; int1 < 4; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && player.current != null) {
					float float2 = player.getZombieRelevenceScore(zombie);
					float1 = Math.max(float1, float2);
				}
			}

			return float1;
		}
	}

	private static class s_performance {
		static final PerformanceProfileProbe isoWorldUpdate = new PerformanceProfileProbe("IsoWorld.update");
		static final PerformanceProfileProbe isoWorldRender = new PerformanceProfileProbe("IsoWorld.render");
	}

	private static class CompDistToPlayer implements Comparator {
		public float px;
		public float py;

		public int compare(IsoZombie zombie, IsoZombie zombie2) {
			float float1 = IsoUtils.DistanceManhatten((float)((int)zombie.x), (float)((int)zombie.y), this.px, this.py);
			float float2 = IsoUtils.DistanceManhatten((float)((int)zombie2.x), (float)((int)zombie2.y), this.px, this.py);
			if (float1 < float2) {
				return -1;
			} else {
				return float1 > float2 ? 1 : 0;
			}
		}
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
}
