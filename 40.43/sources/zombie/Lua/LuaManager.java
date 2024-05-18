package zombie.Lua;

import fmod.fmod.EmitterType;
import fmod.fmod.FMODAudio;
import fmod.fmod.FMODManager;
import fmod.fmod.FMODSoundBank;
import fmod.fmod.FMODSoundEmitter;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.joml.Vector3f;
import org.luaj.kahluafork.compiler.FuncState;
import org.lwjgl.input.Keyboard;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.LuaCaller;
import se.krka.kahlua.integration.LuaReturn;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.Coroutine;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
import sun.util.BuddhistCalendar;
import zombie.AmbientStreamManager;
import zombie.BaseAmbientStreamManager;
import zombie.BaseSoundManager;
import zombie.DummySoundManager;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MapGroups;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.Quests.QuestCreator;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.ai.states.AttackState;
import zombie.ai.states.BurntToDeath;
import zombie.ai.states.ClimbDownSheetRopeState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbOverFenceState2;
import zombie.ai.states.ClimbSheetRopeState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.ClimbThroughWindowState2;
import zombie.ai.states.CrawlingZombieTurnState;
import zombie.ai.states.DieState;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.IdleState;
import zombie.ai.states.JustDieState;
import zombie.ai.states.LuaState;
import zombie.ai.states.LungeState;
import zombie.ai.states.OpenWindowState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.PlayerControlState;
import zombie.ai.states.ReanimatePlayerState;
import zombie.ai.states.ReanimateState;
import zombie.ai.states.SatChairState;
import zombie.ai.states.SatChairStateOut;
import zombie.ai.states.StaggerBackDieState;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.SwipeState;
import zombie.ai.states.SwipeStatePlayer;
import zombie.ai.states.ThumpState;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.WanderState;
import zombie.ai.states.ZombieStandState;
import zombie.audio.BaseSoundBank;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.DummySoundBank;
import zombie.audio.DummySoundEmitter;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.behaviors.Behavior;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.CharacterSoundEmitter;
import zombie.characters.DummyCharacterSoundEmitter;
import zombie.characters.Faction;
import zombie.characters.IsoDummyCameraCharacter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorFactory;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.BodyDamage.Nutrition;
import zombie.characters.CharacterTimedActions.LuaTimedAction;
import zombie.characters.CharacterTimedActions.LuaTimedActionNew;
import zombie.characters.Moodles.Moodle;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.Moodles.Moodles;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.ObservationFactory;
import zombie.characters.traits.TraitFactory;
import zombie.chat.ChatBase;
import zombie.chat.ChatManager;
import zombie.chat.ChatMessage;
import zombie.chat.ServerChatMessage;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigOption;
import zombie.config.DoubleConfigOption;
import zombie.config.EnumConfigOption;
import zombie.config.IntegerConfigOption;
import zombie.config.StringConfigOption;
import zombie.core.Clipboard;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.IndieFileLoader;
import zombie.core.Language;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.fonts.AngelCodeFont;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.ZLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.physics.Bullet;
import zombie.core.physics.WorldSimulation;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.VoiceManager;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelLoader;
import zombie.core.stash.Stash;
import zombie.core.stash.StashBuilding;
import zombie.core.stash.StashSystem;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.core.znet.GameServerDetails;
import zombie.core.znet.ISteamWorkshopCallback;
import zombie.core.znet.ServerBrowser;
import zombie.core.znet.SteamFriend;
import zombie.core.znet.SteamFriends;
import zombie.core.znet.SteamUGCDetails;
import zombie.core.znet.SteamUser;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.SteamWorkshop;
import zombie.core.znet.SteamWorkshopItem;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.erosion.ErosionConfig;
import zombie.erosion.ErosionMain;
import zombie.erosion.season.ErosionSeason;
import zombie.gameStates.ChooseGameInfo;
import zombie.gameStates.ConnectToServerState;
import zombie.gameStates.GameLoadingState;
import zombie.gameStates.GameState;
import zombie.gameStates.IngameState;
import zombie.gameStates.MainScreenState;
import zombie.globalObjects.CGlobalObjectSystem;
import zombie.globalObjects.CGlobalObjects;
import zombie.globalObjects.SGlobalObjectSystem;
import zombie.globalObjects.SGlobalObjects;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Keys;
import zombie.input.Mouse;
import zombie.inventory.FixingManager;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemPickerJava;
import zombie.inventory.ItemType;
import zombie.inventory.RecipeManager;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.ComboItem;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Key;
import zombie.inventory.types.KeyRing;
import zombie.inventory.types.Literature;
import zombie.inventory.types.Moveable;
import zombie.inventory.types.Radio;
import zombie.inventory.types.WeaponPart;
import zombie.iso.BuildingDef;
import zombie.iso.CellLoader;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoHeatSource;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoLot;
import zombie.iso.IsoLuaMover;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaChunk;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoPushableObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LightingJNI;
import zombie.iso.LosUtil;
import zombie.iso.MetaObject;
import zombie.iso.MultiStageBuilding;
import zombie.iso.RoomDef;
import zombie.iso.SliceY;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.isoregion.ChunkRegion;
import zombie.iso.areas.isoregion.DataCell;
import zombie.iso.areas.isoregion.DataChunk;
import zombie.iso.areas.isoregion.IsoRegion;
import zombie.iso.areas.isoregion.MasterRegion;
import zombie.iso.objects.BSFurnace;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoCarBatteryCharger;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoCrate;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoJukebox;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoMolotovCocktail;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoTelevision;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.IsoWheelieBin;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.ObjectRenderEffects;
import zombie.iso.objects.RainManager;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteGrid;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.weather.ClimateColorInfo;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.Temperature;
import zombie.iso.weather.ThunderStorm;
import zombie.iso.weather.WeatherPeriod;
import zombie.iso.weather.fx.IsoWeatherFX;
import zombie.modding.ModUtilsJava;
import zombie.network.CoopMaster;
import zombie.network.DBResult;
import zombie.network.DBTicket;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetChecksum;
import zombie.network.PacketTypes;
import zombie.network.Server;
import zombie.network.ServerOptions;
import zombie.network.ServerSettings;
import zombie.network.ServerSettingsManager;
import zombie.network.ServerWorldDatabase;
import zombie.network.Userlog;
import zombie.network.chat.ChatType;
import zombie.popman.ZombiePopulationManager;
import zombie.popman.ZombiePopulationRenderer;
import zombie.radio.RadioAPI;
import zombie.radio.RadioData;
import zombie.radio.ZomboidRadio;
import zombie.radio.StorySounds.DataPoint;
import zombie.radio.StorySounds.EventSound;
import zombie.radio.StorySounds.SLSoundManager;
import zombie.radio.StorySounds.StorySound;
import zombie.radio.StorySounds.StorySoundEvent;
import zombie.radio.devices.DeviceData;
import zombie.radio.devices.DevicePresets;
import zombie.radio.devices.PresetEntry;
import zombie.radio.scripting.RadioScriptManager;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.EvolvedRecipe;
import zombie.scripting.objects.Fixing;
import zombie.scripting.objects.GameSoundScript;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.ItemRecipe;
import zombie.scripting.objects.Recipe;
import zombie.scripting.objects.ScriptModule;
import zombie.scripting.objects.VehicleScript;
import zombie.spnetwork.SinglePlayerClient;
import zombie.ui.ActionProgressBar;
import zombie.ui.Clock;
import zombie.ui.LuaUIWindow;
import zombie.ui.ModalDialog;
import zombie.ui.MoodlesUI;
import zombie.ui.NewHealthPanel;
import zombie.ui.ObjectTooltip;
import zombie.ui.RadarPanel;
import zombie.ui.RadialMenu;
import zombie.ui.SpeedControls;
import zombie.ui.TextDrawObject;
import zombie.ui.TextManager;
import zombie.ui.UIDebugConsole;
import zombie.ui.UIElement;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.ui.UIServerToolbox;
import zombie.ui.UITextBox2;
import zombie.ui.UITransition;
import zombie.ui.VehicleGauge;
import zombie.ui.VirtualItemSlot;
import zombie.util.AddCoopPlayer;
import zombie.util.PublicServerUtil;
import zombie.util.list.PZArrayList;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.VehicleDoor;
import zombie.vehicles.VehicleLight;
import zombie.vehicles.VehicleManager;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehicleType;
import zombie.vehicles.VehicleWindow;


public class LuaManager {
	public static KahluaConverterManager converterManager = new KahluaConverterManager();
	public static J2SEPlatform platform = new J2SEPlatform();
	public static KahluaTable env;
	public static KahluaThread thread;
	public static KahluaThread debugthread;
	public static LuaCaller caller;
	public static LuaCaller debugcaller;
	public static LuaManager.Exposer exposer;
	public static ArrayList loaded;
	public static HashMap loadedReturn;
	static ArrayList paths;
	public static boolean checksumDone;
	public static ArrayList loadList;

	public static void outputTable(KahluaTable kahluaTable, int int1) {
	}

	public static void init() {
		loaded.clear();
		loadedReturn.clear();
		platform = new J2SEPlatform();
		if (env != null) {
			env.wipe();
		}

		env = platform.newEnvironment();
		converterManager = new KahluaConverterManager();
		if (thread != null) {
			thread.bReset = true;
		}

		thread = new KahluaThread(platform, env);
		debugthread = new KahluaThread(platform, env);
		UIManager.defaultthread = thread;
		caller = new LuaCaller(converterManager);
		debugcaller = new LuaCaller(converterManager);
		exposer = new LuaManager.Exposer(converterManager, platform, env);
		loaded = new ArrayList();
		checksumDone = false;
		GameClient.checksum = "";
		GameClient.checksumValid = false;
		KahluaNumberConverter.install(converterManager);
		LuaEventManager.register(platform, env);
		LuaHookManager.register(platform, env);
		if (CoopMaster.instance != null) {
			CoopMaster.instance.register(platform, env);
		}

		if (VoiceManager.instance != null) {
			VoiceManager.instance.LuaRegister(platform, env);
		}

		KahluaTable kahluaTable = env;
		exposer.exposeAll();
		exposer.TypeMap.put("function", LuaClosure.class);
		exposer.TypeMap.put("table", KahluaTable.class);
		outputTable(env, 0);
	}

	public static void LoadDir(String string) throws URISyntaxException {
	}

	public static void LoadDirBase(String string) throws Exception {
		LoadDirBase(string, false);
	}

	public static void LoadDirBase(String string, boolean boolean1) throws Exception {
		String string2 = "media/lua/" + string + "/";
		File file = new File(string2);
		if (!paths.contains(string2)) {
			paths.add(string2);
		}

		try {
			searchFolders(ZomboidFileSystem.instance.baseURI, file);
		} catch (IOException ioException) {
			ExceptionLogger.logException(ioException);
		}

		ArrayList arrayList = loadList;
		loadList = new ArrayList();
		ArrayList arrayList2 = ZomboidFileSystem.instance.getModIDs();
		for (int int1 = 0; int1 < arrayList2.size(); ++int1) {
			String string3 = ZomboidFileSystem.instance.getModDir((String)arrayList2.get(int1));
			if (string3 != null) {
				URI uRI = (new File(string3)).toURI();
				File file2 = new File(string3 + File.separator + "media" + File.separator + "lua" + File.separator + string);
				try {
					searchFolders(uRI, file2);
				} catch (IOException ioException2) {
					ExceptionLogger.logException(ioException2);
				}
			}
		}

		Collections.sort(arrayList);
		Collections.sort(loadList);
		arrayList.addAll(loadList);
		loadList.clear();
		loadList = arrayList;
		HashSet hashSet = new HashSet();
		Iterator iterator = loadList.iterator();
		while (true) {
			String string4;
			String string5;
			do {
				do {
					do {
						do {
							if (!iterator.hasNext()) {
								loadList.clear();
								return;
							}

							string4 = (String)iterator.next();
						}				 while (hashSet.contains(string4));

						hashSet.add(string4);
						string5 = ZomboidFileSystem.instance.getAbsolutePath(string4);
						if (string5 == null) {
							throw new IllegalStateException("couldn\'t find \"" + string4 + "\"");
						}

						if (!boolean1) {
							RunLua(string5);
						}
					}			 while (checksumDone);
				}		 while (string4.contains("SandboxVars.lua"));
			}	 while (!GameServer.bServer && !GameClient.bClient);

			NetChecksum.checksummer.addFile(string4, string5);
		}
	}

	public static void initChecksum() throws Exception {
		if (!checksumDone) {
			if (GameClient.bClient || GameServer.bServer) {
				NetChecksum.checksummer.reset(false);
			}
		}
	}

	public static void finishChecksum() {
		if (GameServer.bServer) {
			GameServer.checksum = NetChecksum.checksummer.checksumToString();
		} else {
			if (!GameClient.bClient) {
				return;
			}

			GameClient.checksum = NetChecksum.checksummer.checksumToString();
		}

		NetChecksum.GroupOfFiles.finishChecksum();
		checksumDone = true;
	}

	public static void LoadDirBase() throws Exception {
		initChecksum();
		LoadDirBase("shared");
		LoadDirBase("client");
	}

	public static void searchFolders(URI uRI, File file) throws IOException {
		if (file.isDirectory()) {
			String[] stringArray = file.list();
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				searchFolders(uRI, new File(file.getAbsolutePath() + File.separator + stringArray[int1]));
			}
		} else if (file.getAbsolutePath().toLowerCase().endsWith(".lua")) {
			loadList.add(ZomboidFileSystem.instance.getRelativeFile(uRI, file.getAbsolutePath()));
		}
	}

	public static String getLuaCacheDir() {
		String string = GameWindow.getCacheDir() + File.separator + "Lua";
		File file = new File(string);
		if (!file.exists()) {
			file.mkdir();
		}

		return string;
	}

	public static String getSandboxCacheDir() {
		String string = GameWindow.getCacheDir() + File.separator + "Sandbox Presets";
		File file = new File(string);
		if (!file.exists()) {
			file.mkdir();
		}

		return string;
	}

	public static void fillContainer(ItemContainer itemContainer, IsoPlayer player) {
		KahluaTable kahluaTable = (KahluaTable)env.rawget("ItemPicker");
		caller.pcall(thread, kahluaTable.rawget("fillContainer"), itemContainer, player);
	}

	public static void fillContainerCount(ItemContainer itemContainer, IsoPlayer player) {
		KahluaTable kahluaTable = (KahluaTable)env.rawget("ItemPicker");
		caller.pcall(thread, kahluaTable.rawget("fillContainerCount"), itemContainer, player, 5);
	}

	public static void updateOverlaySprite(IsoObject object) {
		ItemPickerJava.updateOverlaySprite(object);
	}

	public static LuaClosure getDotDelimitedClosure(String string) {
		String[] stringArray = string.split("\\.");
		KahluaTable kahluaTable = env;
		for (int int1 = 0; int1 < stringArray.length - 1; ++int1) {
			kahluaTable = (KahluaTable)env.rawget(stringArray[int1]);
		}

		return (LuaClosure)kahluaTable.rawget(stringArray[stringArray.length - 1]);
	}

	public static void dropItem(InventoryItem inventoryItem) {
		LuaClosure luaClosure = getDotDelimitedClosure("ISInventoryPaneContextMenu.dropItem");
		caller.pcall(thread, luaClosure, (Object)inventoryItem);
	}

	public static Object RunLua(String string) {
		return RunLua(string, false);
	}

	public static Object RunLua(String string, boolean boolean1) {
		string = string.replace("\\", "/");
		if (loaded.contains(string)) {
			return loadedReturn.get(string);
		} else {
			FuncState.currentFile = string.substring(string.lastIndexOf(47) + 1);
			FuncState.currentfullFile = string;
			String string2 = string;
			string = ZomboidFileSystem.instance.getString(string.replace("\\", "/"));
			DebugLog.log("Loading: " + ZomboidFileSystem.instance.getRelativeFile(string));
			InputStreamReader inputStreamReader;
			try {
				inputStreamReader = IndieFileLoader.getStreamReader(string);
			} catch (FileNotFoundException fileNotFoundException) {
				ExceptionLogger.logException(fileNotFoundException);
				return null;
			}

			LuaCompiler.rewriteEvents = boolean1;
			LuaClosure luaClosure = null;
			Object object;
			label119: {
				try {
					luaClosure = LuaCompiler.loadis((Reader)inputStreamReader, string.substring(string.lastIndexOf(47) + 1), env);
					break label119;
				} catch (Exception exception) {
					Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, "Error found in LUA file: " + string, (Object)null);
					ExceptionLogger.logException(exception);
					object = null;
				} finally {
					try {
						inputStreamReader.close();
					} catch (Exception exception2) {
					}
				}

				return object;
			}

			LuaReturn luaReturn = caller.protectedCall(thread, luaClosure);
			if (!luaReturn.isSuccess()) {
				Logger.getLogger(IsoWorld.class.getName()).log(Level.SEVERE, (String)null, luaReturn.getErrorString());
				Logger.getLogger(IsoWorld.class.getName()).log(Level.SEVERE, (String)null, luaReturn.getJavaException());
				Logger.getLogger(IsoWorld.class.getName()).log(Level.SEVERE, (String)null, luaReturn.getLuaStackTrace());
			}

			loaded.add(string2);
			object = luaReturn.isSuccess() && luaReturn.size() > 0 ? luaReturn.getFirst() : null;
			if (object != null) {
				loadedReturn.put(string2, object);
			} else {
				loadedReturn.remove(string2);
			}

			LuaCompiler.rewriteEvents = false;
			BaseVehicle.resetLuaFunctions();
			return object;
		}
	}

	public static void Test() throws IOException {
	}

	public static Object get(Object object) {
		return env.rawget(object);
	}

	public static void call(String string, Object object) {
		caller.pcall(thread, env.rawget(string), object);
	}

	private static void exposeKeyboardKeys(KahluaTable kahluaTable) {
		Object object = kahluaTable.rawget("Keyboard");
		if (object instanceof KahluaTable) {
			KahluaTable kahluaTable2 = (KahluaTable)object;
			Field[] fieldArray = Keyboard.class.getFields();
			try {
				Field[] fieldArray2 = fieldArray;
				int int1 = fieldArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					Field field = fieldArray2[int2];
					if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && field.getType().equals(Integer.TYPE) && field.getName().startsWith("KEY_") && !field.getName().endsWith("WIN")) {
						kahluaTable2.rawset(field.getName(), (double)field.getInt((Object)null));
					}
				}
			} catch (Exception exception) {
			}
		}
	}

	public static String getHourMinuteJava() {
		String string = Calendar.getInstance().get(12) + "";
		if (Calendar.getInstance().get(12) < 10) {
			string = "0" + string;
		}

		return Calendar.getInstance().get(11) + ":" + string;
	}

	public static KahluaTable copyTable(KahluaTable kahluaTable) {
		KahluaTable kahluaTable2 = platform.newTable();
		KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
		while (kahluaTableIterator.advance()) {
			Object object = kahluaTableIterator.getKey();
			Object object2 = kahluaTableIterator.getValue();
			if (object2 instanceof KahluaTable) {
				kahluaTable2.rawset(object, copyTable((KahluaTable)object2));
			} else {
				kahluaTable2.rawset(object, object2);
			}
		}

		return kahluaTable2;
	}

	static  {
		caller = new LuaCaller(converterManager);
		debugcaller = new LuaCaller(converterManager);
		loaded = new ArrayList();
		loadedReturn = new HashMap();
		paths = new ArrayList();
		checksumDone = false;
		loadList = new ArrayList();
	}

	public static class GlobalObject {
		static FileOutputStream outStream;
		static FileInputStream inStream;
		static FileReader inFileReader = null;
		static BufferedReader inBufferedReader = null;
		static long timeLastRefresh = 0L;

		@LuaMethod(name = "loadVehicleModel", global = true)
		public static Model loadVehicleModel(String string, String string2, String string3) {
			return loadZomboidModel(string, string2, string3, "vehicle", true);
		}

		@LuaMethod(name = "loadStaticZomboidModel", global = true)
		public static Model loadStaticZomboidModel(String string, String string2, String string3) {
			return loadZomboidModel(string, string2, string3, (String)null, true);
		}

		@LuaMethod(name = "loadSkinnedZomboidModel", global = true)
		public static Model loadSkinnedZomboidModel(String string, String string2, String string3) {
			return loadZomboidModel(string, string2, string3, (String)null, false);
		}

		@LuaMethod(name = "loadZomboidModel", global = true)
		public static Model loadZomboidModel(String string, String string2, String string3, String string4, boolean boolean1) {
			try {
				if (string3.startsWith("/")) {
					string3 = string3.substring(1);
				}

				if (string2.startsWith("/")) {
					string2 = string2.substring(1);
				}

				if (string4 == null) {
					string4 = "basicEffect";
				}

				Model model = ModelLoader.instance.Load(ZomboidFileSystem.instance.getString(string2), ZomboidFileSystem.instance.getString(string3), string4, boolean1);
				ModelManager.instance.ModelMap.put(string, model);
				model.Name = string;
				return model;
			} catch (IOException ioException) {
				ioException.printStackTrace();
				return null;
			}
		}

		@LuaMethod(name = "getSLSoundManager", global = true)
		public static SLSoundManager getSLSoundManager() {
			return null;
		}

		@LuaMethod(name = "getRadioAPI", global = true)
		public static RadioAPI getRadioAPI() {
			return RadioAPI.hasInstance() ? RadioAPI.getInstance() : null;
		}

		@LuaMethod(name = "getRadioTranslators", global = true)
		public static ArrayList getRadioTranslators(Language language) {
			return RadioData.getTranslatorNames(language);
		}

		@LuaMethod(name = "getZomboidRadio", global = true)
		public static ZomboidRadio getZomboidRadio() {
			return ZomboidRadio.hasInstance() ? ZomboidRadio.getInstance() : null;
		}

		@LuaMethod(name = "getRandomUUID", global = true)
		public static String getRandomUUID() {
			return ModUtilsJava.getRandomUUID();
		}

		@LuaMethod(name = "sendItemListNet", global = true)
		public static boolean sendItemListNet(IsoPlayer player, ArrayList arrayList, IsoPlayer player2, String string, String string2) {
			return ModUtilsJava.sendItemListNet(player, arrayList, player2, string, string2);
		}

		@LuaMethod(name = "instanceof", global = true)
		public static boolean instof(Object object, String string) {
			if ("PZKey".equals(string)) {
				boolean boolean1 = false;
			}

			if (object == null) {
				return false;
			} else if (LuaManager.exposer.TypeMap.containsKey(string)) {
				Class javaClass = (Class)LuaManager.exposer.TypeMap.get(string);
				return javaClass.isInstance(object);
			} else if (string.equals("LuaClosure") && object instanceof LuaClosure) {
				return true;
			} else {
				return string.equals("KahluaTableImpl") && object instanceof KahluaTableImpl;
			}
		}

		@LuaMethod(name = "serverConnect", global = true)
		public static void serverConnect(String string, String string2, String string3, String string4, String string5, String string6) {
			Core.GameMode = "Multiplayer";
			Core.setDifficulty("Hardcore");
			if (GameClient.connection != null) {
				GameClient.connection.forceDisconnect();
			}

			GameClient.bClient = true;
			GameClient.bCoopInvite = false;
			GameClient.instance.doConnect(string, string2, string3, string4, string5, string6);
		}

		@LuaMethod(name = "serverConnectCoop", global = true)
		public static void serverConnectCoop(String string) {
			Core.GameMode = "Multiplayer";
			Core.setDifficulty("Hardcore");
			if (GameClient.connection != null) {
				GameClient.connection.forceDisconnect();
			}

			GameClient.bClient = true;
			GameClient.bCoopInvite = true;
			GameClient.instance.doConnectCoop(string);
		}

		@LuaMethod(name = "sendPing", global = true)
		public static void sendPing() {
			if (GameClient.bClient) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPingPacket();
				PacketTypes.doPingPacket(byteBufferWriter);
				byteBufferWriter.putLong(System.currentTimeMillis());
				GameClient.connection.endPingPacket();
			}
		}

		@LuaMethod(name = "forceDisconnect", global = true)
		public static void forceDisconnect() {
			if (GameClient.connection != null) {
				GameClient.connection.forceDisconnect();
			}
		}

		@LuaMethod(name = "backToSinglePlayer", global = true)
		public static void backToSinglePlayer() {
			if (GameClient.bClient) {
				GameClient.instance.doDisconnect("going back to single-player");
				GameClient.bClient = false;
				timeLastRefresh = 0L;
			}
		}

		@LuaMethod(name = "isIngameState", global = true)
		public static boolean isIngameState() {
			return GameWindow.states.current == IngameState.instance;
		}

		@LuaMethod(name = "requestPacketCounts", global = true)
		public static void requestPacketCounts() {
			if (GameClient.bClient) {
				GameClient.instance.requestPacketCounts();
			}
		}

		@LuaMethod(name = "getPacketCounts", global = true)
		public static KahluaTable getPacketCounts(int int1) {
			return GameClient.bClient ? GameClient.instance.getPacketCounts(int1) : null;
		}

		@LuaMethod(name = "getAllItems", global = true)
		public static ArrayList getAllItems() {
			return ScriptManager.instance.getAllItems();
		}

		@LuaMethod(name = "scoreboardUpdate", global = true)
		public static void scoreboardUpdate() {
			GameClient.instance.scoreboardUpdate();
		}

		@LuaMethod(name = "save", global = true)
		public static void save(boolean boolean1) {
			try {
				GameWindow.save(true);
			} catch (FileNotFoundException fileNotFoundException) {
				Logger.getLogger(IngameState.class.getName()).log(Level.SEVERE, (String)null, fileNotFoundException);
			} catch (IOException ioException) {
				Logger.getLogger(IngameState.class.getName()).log(Level.SEVERE, (String)null, ioException);
			}
		}

		@LuaMethod(name = "saveGame", global = true)
		public static void saveGame() {
			try {
				GameWindow.save(true);
			} catch (FileNotFoundException fileNotFoundException) {
				Logger.getLogger(IngameState.class.getName()).log(Level.SEVERE, (String)null, fileNotFoundException);
			} catch (IOException ioException) {
				Logger.getLogger(IngameState.class.getName()).log(Level.SEVERE, (String)null, ioException);
			}
		}

		@LuaMethod(name = "getAllRecipes", global = true)
		public static ArrayList getAllRecipes() {
			return new ArrayList(ScriptManager.instance.getAllRecipes());
		}

		@LuaMethod(name = "requestUserlog", global = true)
		public static void requestUserlog(String string) {
			if (GameClient.bClient) {
				GameClient.instance.requestUserlog(string);
			}
		}

		@LuaMethod(name = "addUserlog", global = true)
		public static void addUserlog(String string, String string2, String string3) {
			if (GameClient.bClient) {
				GameClient.instance.addUserlog(string, string2, string3);
			}
		}

		@LuaMethod(name = "removeUserlog", global = true)
		public static void removeUserlog(String string, String string2, String string3) {
			if (GameClient.bClient) {
				GameClient.instance.removeUserlog(string, string2, string3);
			}
		}

		@LuaMethod(name = "tabToX", global = true)
		public static String tabToX(String string, int int1) {
			while (string.length() < int1) {
				string = string + " ";
			}

			return string;
		}

		@LuaMethod(name = "istype", global = true)
		public static boolean isType(Object object, String string) {
			if (LuaManager.exposer.TypeMap.containsKey(string)) {
				Class javaClass = (Class)LuaManager.exposer.TypeMap.get(string);
				return javaClass.equals(object.getClass());
			} else {
				return false;
			}
		}

		@LuaMethod(name = "isoToScreenX", global = true)
		public static float isoToScreenX(int int1, float float1, float float2, float float3) {
			IsoGameCharacter gameCharacter = IsoCamera.CamCharacter;
			IsoCamera.CamCharacter = IsoPlayer.players[int1];
			IsoPlayer player = IsoPlayer.instance;
			IsoPlayer.instance = IsoPlayer.players[int1];
			float float4 = IsoUtils.XToScreenExact(float1, float2, float3, 0);
			float4 /= Core.getInstance().getZoom(int1);
			IsoCamera.CamCharacter = gameCharacter;
			IsoPlayer.instance = player;
			return (float)IsoCamera.getScreenLeft(int1) + float4;
		}

		@LuaMethod(name = "isoToScreenY", global = true)
		public static float isoToScreenY(int int1, float float1, float float2, float float3) {
			IsoGameCharacter gameCharacter = IsoCamera.CamCharacter;
			IsoCamera.CamCharacter = IsoPlayer.players[int1];
			IsoPlayer player = IsoPlayer.instance;
			IsoPlayer.instance = IsoPlayer.players[int1];
			float float4 = IsoUtils.YToScreenExact(float1, float2, float3, 0);
			float4 /= Core.getInstance().getZoom(int1);
			IsoCamera.CamCharacter = gameCharacter;
			IsoPlayer.instance = player;
			return (float)IsoCamera.getScreenTop(int1) + float4;
		}

		@LuaMethod(name = "getAmbientStreamManager", global = true)
		public static BaseAmbientStreamManager getAmbientStreamManager() {
			return AmbientStreamManager.instance;
		}

		@LuaMethod(name = "getSleepingEvent", global = true)
		public static SleepingEvent getSleepingEvent() {
			return SleepingEvent.instance;
		}

		@LuaMethod(name = "setPlayerMovementActive", global = true)
		public static void setPlayerMovementActive(int int1, boolean boolean1) {
			IsoPlayer.players[int1].bJoypadMovementActive = boolean1;
		}

		@LuaMethod(name = "setActivePlayer", global = true)
		public static void setActivePlayer(int int1) {
			if (!GameClient.bClient) {
				IsoPlayer.instance = IsoPlayer.players[int1];
				IsoCamera.CamCharacter = IsoPlayer.instance;
			}
		}

		@LuaMethod(name = "getPlayer", global = true)
		public static IsoPlayer getPlayer() {
			return IsoPlayer.getInstance();
		}

		@LuaMethod(name = "getNumActivePlayers", global = true)
		public static int getNumActivePlayers() {
			return IsoPlayer.numPlayers;
		}

		@LuaMethod(name = "playServerSound", global = true)
		public static void playServerSound(String string, IsoGridSquare square) {
			GameServer.PlayWorldSoundServer(string, false, square, 0.2F, 5.0F, 1.1F, true);
		}

		@LuaMethod(name = "getMaxActivePlayers", global = true)
		public static int getMaxActivePlayers() {
			return 4;
		}

		@LuaMethod(name = "getPlayerScreenLeft", global = true)
		public static int getPlayerScreenLeft(int int1) {
			return IsoCamera.getScreenLeft(int1);
		}

		@LuaMethod(name = "getPlayerScreenTop", global = true)
		public static int getPlayerScreenTop(int int1) {
			return IsoCamera.getScreenTop(int1);
		}

		@LuaMethod(name = "getPlayerScreenWidth", global = true)
		public static int getPlayerScreenWidth(int int1) {
			return IsoCamera.getScreenWidth(int1);
		}

		@LuaMethod(name = "getPlayerScreenHeight", global = true)
		public static int getPlayerScreenHeight(int int1) {
			return IsoCamera.getScreenHeight(int1);
		}

		@LuaMethod(name = "getPlayerByOnlineID", global = true)
		public static IsoPlayer getPlayerByOnlineID(int int1) {
			if (GameServer.bServer) {
				return (IsoPlayer)GameServer.IDToPlayerMap.get(int1);
			} else {
				return GameClient.bClient ? (IsoPlayer)GameClient.IDToPlayerMap.get(int1) : null;
			}
		}

		@LuaMethod(name = "initUISystem", global = true)
		public static void initUISystem() {
			UIManager.init();
			LuaEventManager.triggerEvent("OnCreatePlayer", 0, IsoPlayer.players[0]);
		}

		@LuaMethod(name = "getPerformance", global = true)
		public static PerformanceSettings getPerformance() {
			return PerformanceSettings.instance;
		}

		@LuaMethod(name = "screenZoomIn", global = true)
		public void screenZoomIn() {
		}

		@LuaMethod(name = "screenZoomOut", global = true)
		public void screenZoomOut() {
		}

		@LuaMethod(name = "addSound", global = true)
		public void addSound(IsoObject object, int int1, int int2, int int3, int int4, int int5) {
			WorldSoundManager.instance.addSound(object, int1, int2, int3, int4, int5);
		}

		@LuaMethod(name = "sendAddXp", global = true)
		public void sendAddXp(IsoPlayer player, PerkFactory.Perks perks, int int1, boolean boolean1, boolean boolean2) {
			if (GameClient.bClient) {
				GameClient.instance.sendAddXpFromPlayerStatsUI(player, perks, int1, boolean1, boolean2);
			}
		}

		@LuaMethod(name = "SyncXp", global = true)
		public void SyncXp(IsoPlayer player) {
			if (GameClient.bClient) {
				GameClient.instance.sendSyncXp(player);
			}
		}

		@LuaMethod(name = "getDBSchema", global = true)
		public static void getDBSchema() {
			GameClient.instance.getDBSchema();
		}

		@LuaMethod(name = "getTableResult", global = true)
		public static void getTableResult(String string, int int1) {
			GameClient.instance.getTableResult(string, int1);
		}

		@LuaMethod(name = "addLevelUpPoint", global = true)
		public void addLevelUpPoint(IsoPlayer player) {
			if (GameClient.bClient) {
				GameClient.instance.addLevelUpPoint(player);
			}
		}

		@LuaMethod(name = "getWorldSoundManager", global = true)
		public static WorldSoundManager getWorldSoundManager() {
			return WorldSoundManager.instance;
		}

		@LuaMethod(name = "AddWorldSound", global = true)
		public static void AddWorldSound(IsoPlayer player, int int1) {
			WorldSoundManager.instance.addSound((IsoObject)null, (int)player.getX(), (int)player.getY(), (int)player.getZ(), int1, 10, false);
		}

		@LuaMethod(name = "AddNoiseToken", global = true)
		public static void AddNoiseToken(IsoGridSquare square, int int1) {
		}

		@LuaMethod(name = "pauseSoundAndMusic", global = true)
		public static void pauseSoundAndMusic() {
			SoundManager.instance.pauseSoundAndMusic();
		}

		@LuaMethod(name = "resumeSoundAndMusic", global = true)
		public static void resumeSoundAndMusic() {
			SoundManager.instance.resumeSoundAndMusic();
		}

		@LuaMethod(name = "isDemo", global = true)
		public static boolean isDemo() {
			Core.getInstance();
			return false;
		}

		@LuaMethod(name = "getTimeInMillis", global = true)
		public static long getTimeInMillis() {
			return System.currentTimeMillis();
		}

		@LuaMethod(name = "getCurrentCoroutine", global = true)
		public static Coroutine getCurrentCoroutine() {
			return LuaManager.thread.getCurrentCoroutine();
		}

		@LuaMethod(name = "reloadLuaFile", global = true)
		public static void reloadLuaFile(String string) {
			LuaManager.loaded.remove(string);
			LuaManager.RunLua(string, true);
		}

		@LuaMethod(name = "reloadServerLuaFile", global = true)
		public static void reloadServerLuaFile(String string) {
			if (GameServer.bServer) {
				string = GameWindow.getCacheDir() + File.separator + "Server" + File.separator + string;
				LuaManager.loaded.remove(string);
				LuaManager.RunLua(string, true);
			}
		}

		@LuaMethod(name = "getServerSpawnRegions", global = true)
		public static KahluaTable getServerSpawnRegions() {
			return !GameClient.bClient ? null : GameClient.instance.getServerSpawnRegions();
		}

		@LuaMethod(name = "getServerOptions", global = true)
		public static ServerOptions getServerOptions() {
			return ServerOptions.instance;
		}

		@LuaMethod(name = "getServerName", global = true)
		public static String getServerName() {
			return GameServer.ServerName;
		}

		@LuaMethod(name = "getSpecificPlayer", global = true)
		public static IsoPlayer getSpecificPlayer(int int1) {
			return IsoPlayer.players[int1];
		}

		@LuaMethod(name = "getCameraOffX", global = true)
		public static float getCameraOffX() {
			return IsoCamera.getOffX();
		}

		@LuaMethod(name = "getLatestSave", global = true)
		public static KahluaTable getLatestSave() {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			BufferedReader bufferedReader = null;
			try {
				bufferedReader = new BufferedReader(new FileReader(new File(GameWindow.getCacheDir() + File.separator + "latestSave.ini")));
			} catch (FileNotFoundException fileNotFoundException) {
				return kahluaTable;
			}

			try {
				String string = null;
				for (int int1 = 1; (string = bufferedReader.readLine()) != null; ++int1) {
					kahluaTable.rawset(int1, string);
				}

				bufferedReader.close();
				return kahluaTable;
			} catch (Exception exception) {
				return kahluaTable;
			}
		}

		@LuaMethod(name = "isCurrentExecutionPoint", global = true)
		public static boolean isCurrentExecutionPoint(String string, int int1) {
			int int2 = LuaManager.thread.currentCoroutine.getCallframeTop() - 1;
			if (int2 < 0) {
				int2 = 0;
			}

			LuaCallFrame luaCallFrame = LuaManager.thread.currentCoroutine.getCallFrame(int2);
			if (luaCallFrame.closure == null) {
				return false;
			} else {
				return luaCallFrame.closure.prototype.lines[luaCallFrame.pc] == int1 && string.equals(luaCallFrame.closure.prototype.filename);
			}
		}

		@LuaMethod(name = "toggleBreakOnChange", global = true)
		public static void toggleBreakOnChange(KahluaTable kahluaTable, Object object) {
			if (Core.bDebug) {
				LuaManager.thread.toggleBreakOnChange(kahluaTable, object);
			}
		}

		@LuaMethod(name = "isDebugEnabled", global = true)
		public static boolean isDebugEnabled() {
			return Core.bDebug;
		}

		@LuaMethod(name = "toggleBreakOnRead", global = true)
		public static void toggleBreakOnRead(KahluaTable kahluaTable, Object object) {
			if (Core.bDebug) {
				LuaManager.thread.toggleBreakOnRead(kahluaTable, object);
			}
		}

		@LuaMethod(name = "toggleBreakpoint", global = true)
		public static void toggleBreakpoint(String string, int int1) {
			string = string.replace("\\", "/");
			if (Core.bDebug) {
				LuaManager.thread.breakpointToggle(string, int1);
			}
		}

		@LuaMethod(name = "hasDataReadBreakpoint", global = true)
		public static boolean hasDataReadBreakpoint(KahluaTable kahluaTable, Object object) {
			return LuaManager.thread.hasReadDataBreakpoint(kahluaTable, object);
		}

		@LuaMethod(name = "hasDataBreakpoint", global = true)
		public static boolean hasDataBreakpoint(KahluaTable kahluaTable, Object object) {
			return LuaManager.thread.hasDataBreakpoint(kahluaTable, object);
		}

		@LuaMethod(name = "hasBreakpoint", global = true)
		public static boolean hasBreakpoint(String string, int int1) {
			return LuaManager.thread.hasBreakpoint(string, int1);
		}

		@LuaMethod(name = "getLoadedLuaCount", global = true)
		public static int getLoadedLuaCount() {
			return LuaManager.loaded.size();
		}

		@LuaMethod(name = "getLoadedLua", global = true)
		public static String getLoadedLua(int int1) {
			return (String)LuaManager.loaded.get(int1);
		}

		@LuaMethod(name = "isServer", global = true)
		public static boolean isServer() {
			return GameServer.bServer;
		}

		@LuaMethod(name = "isServerSoftReset", global = true)
		public static boolean isServerSoftReset() {
			return GameServer.bServer && System.getProperty("softreset") != null;
		}

		@LuaMethod(name = "isClient", global = true)
		public static boolean isClient() {
			return GameClient.bClient;
		}

		@LuaMethod(name = "canModifyPlayerStats", global = true)
		public static boolean canModifyPlayerStats() {
			return !GameClient.bClient ? true : GameClient.canModifyPlayerStats();
		}

		@LuaMethod(name = "executeQuery", global = true)
		public static void executeQuery(String string, KahluaTable kahluaTable) {
			GameClient.instance.executeQuery(string, kahluaTable);
		}

		@LuaMethod(name = "canSeePlayerStats", global = true)
		public static boolean canSeePlayerStats() {
			return GameClient.canSeePlayerStats();
		}

		@LuaMethod(name = "getAccessLevel", global = true)
		public static String getAccessLevel() {
			return GameClient.accessLevel;
		}

		@LuaMethod(name = "getOnlinePlayers", global = true)
		public static ArrayList getOnlinePlayers() {
			if (GameServer.bServer) {
				return GameServer.getPlayers();
			} else {
				return GameClient.bClient ? GameClient.instance.getPlayers() : null;
			}
		}

		@LuaMethod(name = "getDebug", global = true)
		public static boolean getDebug() {
			return Core.bDebug || GameServer.bServer && GameServer.bDebug;
		}

		@LuaMethod(name = "getCameraOffY", global = true)
		public static float getCameraOffY() {
			return IsoCamera.getOffY();
		}

		@LuaMethod(name = "createRegionFile", global = true)
		public static KahluaTable createRegionFile() {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			String string = IsoWorld.instance.getMap();
			if (string.equals("DEFAULT")) {
				MapGroups mapGroups = new MapGroups();
				mapGroups.createGroups();
				if (mapGroups.getNumberOfGroups() != 1) {
					throw new RuntimeException("GameMap is DEFAULT but there are multiple worlds to choose from");
				}

				mapGroups.setWorld(0);
				string = IsoWorld.instance.getMap();
			}

			if (!GameClient.bClient && !GameServer.bServer) {
				string = MapGroups.addMissingVanillaDirectories(string);
			}

			String[] stringArray = string.split(";");
			int int1 = 1;
			String[] stringArray2 = stringArray;
			int int2 = stringArray.length;
			for (int int3 = 0; int3 < int2; ++int3) {
				String string2 = stringArray2[int3];
				string2 = string2.trim();
				if (!string2.isEmpty()) {
					File file = new File(ZomboidFileSystem.instance.getString("media/maps/" + string2 + "/spawnpoints.lua"));
					if (file.exists()) {
						KahluaTable kahluaTable2 = LuaManager.platform.newTable();
						kahluaTable2.rawset("name", string2);
						kahluaTable2.rawset("file", "media/maps/" + string2 + "/spawnpoints.lua");
						kahluaTable.rawset(int1, kahluaTable2);
						++int1;
					}
				}
			}

			return kahluaTable;
		}

		@LuaMethod(name = "getMapDirectoryTable", global = true)
		public static KahluaTable getMapDirectoryTable() {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			File file = new File("media/maps/");
			String[] stringArray = file.list();
			if (stringArray == null) {
				return kahluaTable;
			} else {
				int int1 = 1;
				for (int int2 = 0; int2 < stringArray.length; ++int2) {
					String string = stringArray[int2];
					if (!string.equals("challengemaps")) {
						Double Double1 = (double)int1;
						kahluaTable.rawset(Double1, string);
						++int1;
					}
				}

				ChooseGameInfo chooseGameInfo = new ChooseGameInfo();
				Iterator iterator = ZomboidFileSystem.instance.mods.iterator();
				while (iterator.hasNext()) {
					String string2 = (String)iterator.next();
					ChooseGameInfo.Mod mod = null;
					try {
						mod = chooseGameInfo.getModDetails(string2);
					} catch (Exception exception) {
					}

					if (mod != null) {
						file = new File(mod.getDir() + "/media/maps/");
						if (file.exists()) {
							stringArray = file.list();
							if (stringArray != null) {
								for (int int3 = 0; int3 < stringArray.length; ++int3) {
									String string3 = stringArray[int3];
									ChooseGameInfo.Map map = ChooseGameInfo.getMapDetails(string3);
									if (map.getLotDirectories() != null && !map.getLotDirectories().isEmpty() && !string3.equals("challengemaps")) {
										kahluaTable.rawset((double)int1, string3);
										++int1;
									}
								}
							}
						}
					}
				}

				return kahluaTable;
			}
		}

		@LuaMethod(name = "deleteSave", global = true)
		public static void deleteSave(String string) {
			File file = new File(GameWindow.getSaveDir() + File.separator + string);
			String[] stringArray = file.list();
			if (stringArray != null) {
				for (int int1 = 0; int1 < stringArray.length; ++int1) {
					File file2 = new File(GameWindow.getSaveDir() + File.separator + string + File.separator + stringArray[int1]);
					if (file2.isDirectory()) {
						deleteSave(string + File.separator + file2.getName());
					}

					file2.delete();
				}

				file.delete();
			}
		}

		@LuaMethod(name = "sendPlayerExtraInfo", global = true)
		public static void sendPlayerExtraInfo(IsoPlayer player) {
			GameClient.sendPlayerExtraInfo(player);
		}

		@LuaMethod(name = "getServerAddressFromArgs", global = true)
		public static String getServerAddressFromArgs() {
			if (System.getProperty("args.server.connect") != null) {
				String string = System.getProperty("args.server.connect");
				System.clearProperty("args.server.connect");
				return string;
			} else {
				return null;
			}
		}

		@LuaMethod(name = "getServerPasswordFromArgs", global = true)
		public static String getServerPasswordFromArgs() {
			if (System.getProperty("args.server.password") != null) {
				String string = System.getProperty("args.server.password");
				System.clearProperty("args.server.password");
				return string;
			} else {
				return null;
			}
		}

		@LuaMethod(name = "getServerListFile", global = true)
		public static String getServerListFile() {
			return SteamUtils.isSteamModeEnabled() ? "ServerListSteam.txt" : "ServerList.txt";
		}

		@LuaMethod(name = "getServerList", global = true)
		public static KahluaTable getServerList() {
			ArrayList arrayList = new ArrayList();
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			BufferedReader bufferedReader = null;
			try {
				File file = new File(LuaManager.getLuaCacheDir() + File.separator + getServerListFile());
				if (!file.exists()) {
					file.createNewFile();
				}

				bufferedReader = new BufferedReader(new FileReader(file));
				String string = null;
				Server server = null;
				while ((string = bufferedReader.readLine()) != null) {
					if (string.startsWith("name=")) {
						server = new Server();
						arrayList.add(server);
						server.setName(string.replaceFirst("name=", ""));
					} else if (string.startsWith("ip=")) {
						server.setIp(string.replaceFirst("ip=", ""));
					} else if (string.startsWith("localip=")) {
						server.setLocalIP(string.replaceFirst("localip=", ""));
					} else if (string.startsWith("description=")) {
						server.setDescription(string.replaceFirst("description=", ""));
					} else if (string.startsWith("port=")) {
						server.setPort(string.replaceFirst("port=", ""));
					} else if (string.startsWith("user=")) {
						server.setUserName(string.replaceFirst("user=", ""));
					} else if (string.startsWith("password=")) {
						server.setPwd(string.replaceFirst("password=", ""));
					} else if (string.startsWith("serverpassword=")) {
						server.setServerPassword(string.replaceFirst("serverpassword=", ""));
					}
				}

				int int1 = 1;
				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					Server server2 = (Server)arrayList.get(int2);
					Double Double1 = (double)int1;
					kahluaTable.rawset(Double1, server2);
					++int1;
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			} finally {
				try {
					bufferedReader.close();
				} catch (Exception exception2) {
				}
			}

			return kahluaTable;
		}

		@LuaMethod(name = "ping", global = true)
		public static void ping(String string, String string2, String string3, String string4) {
			GameClient.askPing = true;
			serverConnect(string, string2, string3, "", string4, "");
		}

		@LuaMethod(name = "stopPing", global = true)
		public static void stopPing() {
			GameClient.askPing = false;
		}

		@LuaMethod(name = "transformIntoKahluaTable", global = true)
		public static KahluaTable transformIntoKahluaTable(HashMap hashMap) {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			Iterator iterator = hashMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry entry = (Entry)iterator.next();
				kahluaTable.rawset(entry.getKey(), entry.getValue());
			}

			return kahluaTable;
		}

		@LuaMethod(name = "getSaveDirectory", global = true)
		public static ArrayList getSaveDirectory(String string) {
			File file = new File(string + File.separator);
			if (!file.exists() && !Core.getInstance().isNoSave()) {
				file.mkdir();
			}

			String[] stringArray = file.list();
			if (stringArray == null) {
				return null;
			} else {
				ArrayList arrayList = new ArrayList();
				for (int int1 = 0; int1 < stringArray.length; ++int1) {
					File file2 = new File(string + File.separator + stringArray[int1]);
					if (file2.isDirectory()) {
						arrayList.add(file2);
					}
				}

				return arrayList;
			}
		}

		@LuaMethod(name = "getFullSaveDirectoryTable", global = true)
		public static KahluaTable getFullSaveDirectoryTable() {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			File file = new File(GameWindow.getSaveDir() + File.separator);
			if (!file.exists()) {
				file.mkdir();
			}

			String[] stringArray = file.list();
			if (stringArray == null) {
				return kahluaTable;
			} else {
				ArrayList arrayList = new ArrayList();
				int int1;
				for (int1 = 0; int1 < stringArray.length; ++int1) {
					File file2 = new File(GameWindow.getSaveDir() + File.separator + stringArray[int1]);
					if (file2.isDirectory() && !"Multiplayer".equals(stringArray[int1])) {
						ArrayList arrayList2 = getSaveDirectory(GameWindow.getSaveDir() + File.separator + stringArray[int1]);
						arrayList.addAll(arrayList2);
					}
				}

				Collections.sort(arrayList, new Comparator(){
					
					public int compare(File file, File stringArray) {
						return Long.valueOf(stringArray.lastModified()).compareTo(file.lastModified());
					}
				});

				int1 = 1;
				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					File file3 = (File)arrayList.get(int2);
					String string = getSaveName(file3);
					Double Double1 = (double)int1;
					kahluaTable.rawset(Double1, string);
					++int1;
				}

				return kahluaTable;
			}
		}

		public static String getSaveName(File file) {
			String[] stringArray = file.getAbsolutePath().split("\\" + File.separator);
			return stringArray[stringArray.length - 2] + File.separator + file.getName();
		}

		@LuaMethod(name = "getSaveDirectoryTable", global = true)
		public static KahluaTable getSaveDirectoryTable() {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			return kahluaTable;
		}

		public static List getStories() {
			ArrayList arrayList = new ArrayList();
			File file = new File("media" + File.separator + "stories");
			if (!file.exists()) {
				file.mkdir();
			}

			String[] stringArray = file.list();
			if (stringArray == null) {
				return arrayList;
			} else {
				for (int int1 = 0; int1 < stringArray.length; ++int1) {
					if ((new File(file.getPath() + File.separator + stringArray[int1])).isDirectory()) {
						File file2 = new File(file.getPath() + File.separator + stringArray[int1] + File.separator + "story.info");
						if (file2.exists()) {
							arrayList.add(stringArray[int1]);
						}
					}
				}

				return arrayList;
			}
		}

		public static List getMods() {
			ArrayList arrayList = new ArrayList();
			ZomboidFileSystem.instance.getAllModFolders(arrayList);
			return arrayList;
		}

		@LuaMethod(name = "doChallenge", global = true)
		public static void doChallenge(KahluaTable kahluaTable) {
			Core.GameMode = kahluaTable.rawget("gameMode").toString();
			Core.bLastStand = Core.GameMode.equals("LastStand");
			Core.getInstance().setChallenge(true);
			getWorld().setMap(kahluaTable.getString("world"));
			Integer integer = Rand.Next(100000000);
			IsoWorld.instance.setWorld(integer.toString());
			getWorld().bDoChunkMapUpdate = false;
		}

		@LuaMethod(name = "doTutorial", global = true)
		public static void doTutorial(KahluaTable kahluaTable) {
			Core.GameMode = "Tutorial";
			Core.bLastStand = false;
			Core.getInstance().setChallenge(false);
			Core.bTutorial = true;
			getWorld().setMap(kahluaTable.getString("world"));
			getWorld().bDoChunkMapUpdate = false;
		}

		@LuaMethod(name = "deleteAllGameModeSaves", global = true)
		public static void deleteAllGameModeSaves(String string) {
			String string2 = Core.GameMode;
			Core.GameMode = string;
			Path path = Paths.get(GameWindow.getGameModeCacheDir());
			if (!Files.exists(path, new LinkOption[0])) {
				Core.GameMode = string2;
			} else {
				try {
					Files.walkFileTree(path, new FileVisitor(){
						
						public FileVisitResult preVisitDirectory(Path string2, BasicFileAttributes path) throws IOException {
							return FileVisitResult.CONTINUE;
						}

						
						public FileVisitResult visitFile(Path string2, BasicFileAttributes path) throws IOException {
							Files.delete(string2);
							return FileVisitResult.CONTINUE;
						}

						
						public FileVisitResult visitFileFailed(Path string2, IOException path) throws IOException {
							path.printStackTrace();
							return FileVisitResult.CONTINUE;
						}

						
						public FileVisitResult postVisitDirectory(Path string2, IOException path) throws IOException {
							Files.delete(string2);
							return FileVisitResult.CONTINUE;
						}
					});
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}

				Core.GameMode = string2;
			}
		}

		@LuaMethod(name = "sledgeDestroy", global = true)
		public static void sledgeDestroy(IsoObject object) {
			if (GameClient.bClient) {
				GameClient.destroy(object);
			}
		}

		@LuaMethod(name = "getTickets", global = true)
		public static void getTickets(String string) {
			if (GameClient.bClient) {
				GameClient.getTickets(string);
			}
		}

		@LuaMethod(name = "addTicket", global = true)
		public static void addTicket(String string, String string2, int int1) {
			if (GameClient.bClient) {
				GameClient.addTicket(string, string2, int1);
			}
		}

		@LuaMethod(name = "removeTicket", global = true)
		public static void removeTicket(int int1) {
			if (GameClient.bClient) {
				GameClient.removeTicket(int1);
			}
		}

		@LuaMethod(name = "sendFactionInvite", global = true)
		public static void sendFactionInvite(Faction faction, IsoPlayer player, String string) {
			if (GameClient.bClient) {
				GameClient.sendFactionInvite(faction, player, string);
			}
		}

		@LuaMethod(name = "acceptFactionInvite", global = true)
		public static void acceptFactionInvite(Faction faction, String string) {
			if (GameClient.bClient) {
				GameClient.acceptFactionInvite(faction, string);
			}
		}

		@LuaMethod(name = "createHordeFromTo", global = true)
		public static void createHordeFromTo(float float1, float float2, float float3, float float4, int int1) {
			ZombiePopulationManager.instance.createHordeFromTo((int)float1, (int)float2, (int)float3, (int)float4, int1);
		}

		@LuaMethod(name = "createHordeInAreaTo", global = true)
		public static void createHordeInAreaTo(int int1, int int2, int int3, int int4, int int5, int int6, int int7) {
			ZombiePopulationManager.instance.createHordeInAreaTo(int1, int2, int3, int4, int5, int6, int7);
		}

		@LuaMethod(name = "spawnHorde", global = true)
		public static void spawnHorde(float float1, float float2, float float3, float float4, float float5, int int1) {
			for (int int2 = 0; int2 < int1; ++int2) {
				VirtualZombieManager.instance.choices.clear();
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)Rand.Next(float1, float3), (double)Rand.Next(float2, float4), (double)float5);
				if (square != null) {
					VirtualZombieManager.instance.choices.add(square);
					VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.fromIndex(Rand.Next(IsoDirections.Max.index())).index(), false);
				}
			}
		}

		@LuaMethod(name = "createZombie", global = true)
		public static IsoZombie createZombie(float float1, float float2, float float3, SurvivorDesc survivorDesc, int int1, IsoDirections directions) {
			VirtualZombieManager.instance.choices.clear();
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, (double)float3);
			VirtualZombieManager.instance.choices.add(square);
			return survivorDesc != null ? VirtualZombieManager.instance.createRealZombieAlways(directions.index(), false, survivorDesc, int1) : VirtualZombieManager.instance.createRealZombieAlways(directions.index(), false);
		}

		@LuaMethod(name = "triggerEvent", global = true)
		public static void triggerEvent(String string) {
			LuaEventManager.triggerEvent(string);
		}

		@LuaMethod(name = "triggerEvent", global = true)
		public static void triggerEvent(String string, Object object) {
			LuaEventManager.triggerEventGarbage(string, object);
		}

		@LuaMethod(name = "triggerEvent", global = true)
		public static void triggerEvent(String string, Object object, Object object2) {
			LuaEventManager.triggerEventGarbage(string, object, object2);
		}

		@LuaMethod(name = "triggerEvent", global = true)
		public static void triggerEvent(String string, Object object, Object object2, Object object3) {
			LuaEventManager.triggerEventGarbage(string, object, object2, object3);
		}

		@LuaMethod(name = "triggerEvent", global = true)
		public static void triggerEvent(String string, Object object, Object object2, Object object3, Object object4) {
			LuaEventManager.triggerEventGarbage(string, object, object2, object3, object4);
		}

		@LuaMethod(name = "debugLuaTable", global = true)
		public static void debugLuaTable(Object object, int int1) {
			if (int1 <= 1) {
				if (object instanceof KahluaTable) {
					KahluaTable kahluaTable = (KahluaTable)object;
					KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
					String string = "";
					for (int int2 = 0; int2 < int1; ++int2) {
						string = string + "\t";
					}

					do {
						Object object2 = kahluaTableIterator.getKey();
						Object object3 = kahluaTableIterator.getValue();
						if (object2 != null) {
							if (object3 != null) {
								DebugLog.log(string + object2 + " : " + object3.toString());
							}

							if (object3 instanceof KahluaTable) {
								debugLuaTable(object3, int1 + 1);
							}
						}
					}		 while (kahluaTableIterator.advance());

					if (kahluaTable.getMetatable() != null) {
						debugLuaTable(kahluaTable.getMetatable(), int1);
					}
				}
			}
		}

		@LuaMethod(name = "debugLuaTable", global = true)
		public static void debugLuaTable(Object object) {
			debugLuaTable(object, 0);
		}

		@LuaMethod(name = "sendItemsInContainer", global = true)
		public static void sendItemsInContainer(IsoObject object, ItemContainer itemContainer) {
			GameServer.sendItemsInContainer(object, itemContainer == null ? object.getContainer() : itemContainer);
		}

		@LuaMethod(name = "getModDirectoryTable", global = true)
		public static KahluaTable getModDirectoryTable() {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			List list = getMods();
			int int1 = 1;
			for (int int2 = 0; int2 < list.size(); ++int2) {
				String string = (String)list.get(int2);
				Double Double1 = (double)int1;
				kahluaTable.rawset(Double1, string);
				++int1;
			}

			return kahluaTable;
		}

		@LuaMethod(name = "getStoryDirectoryTable", global = true)
		public static KahluaTable getStoryDirectoryTable() {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			List list = getStories();
			int int1 = 1;
			for (int int2 = 0; int2 < list.size(); ++int2) {
				String string = (String)list.get(int2);
				Double Double1 = (double)int1;
				kahluaTable.rawset(Double1, string);
				++int1;
			}

			return kahluaTable;
		}

		@LuaMethod(name = "getModInfoByID", global = true)
		public static ChooseGameInfo.Mod getModInfoByID(String string) {
			try {
				return (new ChooseGameInfo()).getModDetails(string);
			} catch (Exception exception) {
				exception.printStackTrace();
				return null;
			}
		}

		@LuaMethod(name = "getModInfo", global = true)
		public static ChooseGameInfo.Mod getModInfo(String string) {
			ChooseGameInfo chooseGameInfo = new ChooseGameInfo();
			try {
				return chooseGameInfo.readModInfo(string);
			} catch (Exception exception) {
				Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, exception);
				return null;
			}
		}

		@LuaMethod(name = "getMapFoldersForMod", global = true)
		public static ArrayList getMapFoldersForMod(String string) {
			try {
				ChooseGameInfo chooseGameInfo = new ChooseGameInfo();
				ChooseGameInfo.Mod mod = chooseGameInfo.getModDetails(string);
				if (mod == null) {
					return null;
				} else {
					String string2 = mod.getDir() + File.separator + "media" + File.separator + "maps";
					File file = new File(string2);
					if (file.exists() && file.isDirectory()) {
						ArrayList arrayList = null;
						DirectoryStream directoryStream = Files.newDirectoryStream(file.toPath());
						Throwable throwable = null;
						try {
							Iterator iterator = directoryStream.iterator();
							while (iterator.hasNext()) {
								Path path = (Path)iterator.next();
								if (Files.isDirectory(path, new LinkOption[0])) {
									file = new File(string2 + File.separator + path.getFileName().toString() + File.separator + "map.info");
									if (file.exists()) {
										if (arrayList == null) {
											arrayList = new ArrayList();
										}

										arrayList.add(path.getFileName().toString());
									}
								}
							}
						} catch (Throwable throwable2) {
							throwable = throwable2;
							throw throwable2;
						} finally {
							if (directoryStream != null) {
								if (throwable != null) {
									try {
										directoryStream.close();
									} catch (Throwable throwable3) {
										throwable.addSuppressed(throwable3);
									}
								} else {
									directoryStream.close();
								}
							}
						}

						return arrayList;
					} else {
						return null;
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				return null;
			}
		}

		@LuaMethod(name = "spawnpointsExistsForMod", global = true)
		public static boolean spawnpointsExistsForMod(String string, String string2) {
			try {
				ChooseGameInfo chooseGameInfo = new ChooseGameInfo();
				ChooseGameInfo.Mod mod = chooseGameInfo.getModDetails(string);
				if (mod == null) {
					return false;
				} else {
					String string3 = mod.getDir() + File.separator + "media" + File.separator + "maps" + File.separator + string2 + File.separator + "spawnpoints.lua";
					return (new File(string3)).exists();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				return false;
			}
		}

		@LuaMethod(name = "getFileSeparator", global = true)
		public static String getFileSeparator() {
			return File.separator;
		}

		@LuaMethod(name = "getStoryInfo", global = true)
		public static ChooseGameInfo.Story getStoryInfo(String string) {
			ChooseGameInfo chooseGameInfo = new ChooseGameInfo();
			ChooseGameInfo.Story story = chooseGameInfo.new Story(string);
			try {
				chooseGameInfo.getStoryDetails(story, string);
			} catch (FileNotFoundException fileNotFoundException) {
				Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, fileNotFoundException);
			}

			return story;
		}

		@LuaMethod(name = "getStorySavedTable", global = true)
		public static KahluaTable getStorySavedTable() {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			List list = getStories();
			int int1 = 1;
			for (int int2 = 0; int2 < list.size(); ++int2) {
				String string = (String)list.get(int2);
				Core.GameMode = null;
				File file = new File(GameWindow.getGameModeCacheDir() + File.separator + string);
				if (file.exists() && file.list() != null) {
					Double Double1 = (double)int1;
					kahluaTable.rawset(Double1, string);
					++int1;
				}
			}

			if (int1 == 1) {
				return null;
			} else {
				return kahluaTable;
			}
		}

		@LuaMethod(name = "getScriptManager", global = true)
		public static ScriptManager getScriptManager() {
			return ScriptManager.instance;
		}

		@LuaMethod(name = "checkSaveFolderExists", global = true)
		public static boolean checkSaveFolderExists(String string) {
			File file = new File(GameWindow.getSaveDir() + File.separator + string);
			return file.exists();
		}

		@LuaMethod(name = "getAbsoluteSaveFolderName", global = true)
		public static String getAbsoluteSaveFolderName(String string) {
			File file = new File(GameWindow.getSaveDir() + File.separator + string);
			return file.getAbsolutePath();
		}

		@LuaMethod(name = "checkSaveFileExists", global = true)
		public static boolean checkSaveFileExists(String string) {
			File file = new File(GameWindow.getGameModeCacheDir() + Core.GameSaveWorld + File.separator + string);
			return file.exists();
		}

		@LuaMethod(name = "fileExists", global = true)
		public static boolean fileExists(String string) {
			String string2 = string.replace("/", File.separator);
			string2 = string2.replace("\\", File.separator);
			File file = new File(ZomboidFileSystem.instance.getString(string2));
			return file.exists();
		}

		@LuaMethod(name = "serverFileExists", global = true)
		public static boolean serverFileExists(String string) {
			String string2 = string.replace("/", File.separator);
			string2 = string2.replace("\\", File.separator);
			File file = new File(GameWindow.getCacheDir() + File.separator + "Server" + File.separator + string2);
			return file.exists();
		}

		@LuaMethod(name = "takeScreenshot", global = true)
		public static void takeScreenshot() {
			Core.getInstance().TakeFullScreenshot((String)null);
		}

		@LuaMethod(name = "takeScreenshot", global = true)
		public static void takeScreenshot(String string) {
			Core.getInstance().TakeFullScreenshot(string);
		}

		@LuaMethod(name = "instanceItem", global = true)
		public static InventoryItem instanceItem(Item item) {
			return InventoryItemFactory.CreateItem(item.module.name + "." + item.name);
		}

		@LuaMethod(name = "instanceItem", global = true)
		public static InventoryItem instanceItem(String string) {
			return InventoryItemFactory.CreateItem(string);
		}

		@LuaMethod(name = "createNewScriptItem", global = true)
		public static Item createNewScriptItem(String string, String string2, String string3, String string4, String string5) {
			Item item = new Item();
			item.module = ScriptManager.instance.getModule(string);
			item.module.ItemMap.put(string2, item);
			item.Icon = "Item_" + string5;
			item.DisplayName = string3;
			item.name = string2;
			try {
				item.type = Item.Type.valueOf(string4);
			} catch (Exception exception) {
			}

			return item;
		}

		@LuaMethod(name = "cloneItemType", global = true)
		public static Item cloneItemType(String string, String string2) {
			Item item = ScriptManager.instance.FindItem(string2);
			Item item2 = new Item();
			item2.module = item.getModule();
			item2.module.ItemMap.put(string, item2);
			return item2;
		}

		@LuaMethod(name = "require", global = true)
		public static Object require(String string) {
			String string2 = string;
			if (!string.endsWith(".lua")) {
				string2 = string + ".lua";
			}

			for (int int1 = 0; int1 < LuaManager.paths.size(); ++int1) {
				String string3 = (String)LuaManager.paths.get(int1);
				String string4 = ZomboidFileSystem.instance.getAbsolutePath(string3 + string2);
				if (string4 != null) {
					return LuaManager.RunLua(ZomboidFileSystem.instance.getString(string4));
				}
			}

			DebugLog.log("require(\"" + string + "\") failed");
			return null;
		}

		@LuaMethod(name = "getRenderer", global = true)
		public static SpriteRenderer getRenderer() {
			return SpriteRenderer.instance;
		}

		@LuaMethod(name = "getGameTime", global = true)
		public static GameTime getGameTime() {
			return GameTime.instance;
		}

		@LuaMethod(name = "getWorld", global = true)
		public static IsoWorld getWorld() {
			return IsoWorld.instance;
		}

		@LuaMethod(name = "getCell", global = true)
		public static IsoCell getCell() {
			return IsoWorld.instance.getCell();
		}

		@LuaMethod(name = "getSandboxOptions", global = true)
		public static SandboxOptions getSandboxOptions() {
			return SandboxOptions.instance;
		}

		@LuaMethod(name = "getFileOutput", global = true)
		public static DataOutputStream getFileOutput(String string) {
			if (string.contains("..")) {
				DebugLog.log("relative paths not allowed");
				return null;
			} else {
				String string2 = LuaManager.getLuaCacheDir() + File.separator + string;
				string2 = string2.replace("/", File.separator);
				string2 = string2.replace("\\", File.separator);
				String string3 = string2.substring(0, string2.lastIndexOf(File.separator));
				string3 = string3.replace("\\", "/");
				File file = new File(string3);
				if (!file.exists()) {
					file.mkdirs();
				}

				File file2 = new File(string2);
				try {
					outStream = new FileOutputStream(file2);
				} catch (FileNotFoundException fileNotFoundException) {
					Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, fileNotFoundException);
				}

				DataOutputStream dataOutputStream = new DataOutputStream(outStream);
				return dataOutputStream;
			}
		}

		@LuaMethod(name = "getAllSavedPlayers", global = true)
		public static List getAllSavedPlayers() throws IOException {
			ArrayList arrayList = new ArrayList();
			String string = LuaManager.getLuaCacheDir() + File.separator + "Players";
			string = string.replace("/", File.separator);
			string = string.replace("\\", File.separator);
			File file = new File(string);
			if (!file.exists()) {
				file.mkdir();
			}

			File[] fileArray = file.listFiles();
			int int1 = fileArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				File file2 = fileArray[int2];
				arrayList.add(new BufferedReader(new FileReader(file2)));
			}

			return arrayList;
		}

		@LuaMethod(name = "getSandboxPresets", global = true)
		public static List getSandboxPresets() throws IOException {
			ArrayList arrayList = new ArrayList();
			String string = LuaManager.getSandboxCacheDir();
			File file = new File(string);
			if (!file.exists()) {
				file.mkdir();
			}

			File[] fileArray = file.listFiles();
			int int1 = fileArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				File file2 = fileArray[int2];
				if (file2.getName().endsWith(".cfg")) {
					arrayList.add(file2.getName().replace(".cfg", ""));
				}
			}

			Collections.sort(arrayList);
			return arrayList;
		}

		@LuaMethod(name = "deleteSandboxPreset", global = true)
		public static void deleteSandboxPreset(String string) {
			if (string.contains("..")) {
				DebugLog.log("relative paths not allowed");
			} else {
				String string2 = LuaManager.getSandboxCacheDir() + File.separator + string + ".cfg";
				File file = new File(string2);
				if (file.exists()) {
					file.delete();
				}
			}
		}

		@LuaMethod(name = "getFileReader", global = true)
		public static BufferedReader getFileReader(String string, boolean boolean1) throws IOException {
			if (string.contains("..")) {
				DebugLog.log("relative paths not allowed");
				return null;
			} else {
				String string2 = LuaManager.getLuaCacheDir() + File.separator + string;
				string2 = string2.replace("/", File.separator);
				string2 = string2.replace("\\", File.separator);
				File file = new File(string2);
				if (!file.exists() && boolean1) {
					file.createNewFile();
				}

				if (file.exists()) {
					BufferedReader bufferedReader = null;
					try {
						bufferedReader = new BufferedReader(new FileReader(file));
					} catch (IOException ioException) {
						Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, ioException);
					}

					return bufferedReader;
				} else {
					return null;
				}
			}
		}

		@LuaMethod(name = "getModFileReader", global = true)
		public static BufferedReader getModFileReader(String string, String string2, boolean boolean1) throws IOException {
			if (!string2.isEmpty() && !string2.contains("..") && !(new File(string2)).isAbsolute()) {
				String string3 = GameWindow.getCacheDir() + File.separator + "mods" + File.separator + string2;
				if (string != null) {
					ChooseGameInfo chooseGameInfo = new ChooseGameInfo();
					ChooseGameInfo.Mod mod = chooseGameInfo.getModDetails(string);
					if (mod == null) {
						return null;
					}

					string3 = mod.getDir() + File.separator + string2;
				}

				string3 = string3.replace("/", File.separator);
				string3 = string3.replace("\\", File.separator);
				File file = new File(string3);
				if (!file.exists() && boolean1) {
					String string4 = string3.substring(0, string3.lastIndexOf(File.separator));
					File file2 = new File(string4);
					if (!file2.exists()) {
						file2.mkdirs();
					}

					file.createNewFile();
				}

				if (file.exists()) {
					BufferedReader bufferedReader = null;
					try {
						bufferedReader = new BufferedReader(new FileReader(file));
					} catch (IOException ioException) {
						Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, ioException);
					}

					return bufferedReader;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}

		@LuaMethod(name = "getModFileWriter", global = true)
		public static LuaManager.GlobalObject.LuaFileWriter getModFileWriter(String string, String string2, boolean boolean1, boolean boolean2) {
			if (!string2.isEmpty() && !string2.contains("..") && !(new File(string2)).isAbsolute()) {
				ChooseGameInfo chooseGameInfo = new ChooseGameInfo();
				ChooseGameInfo.Mod mod = chooseGameInfo.getModDetails(string);
				if (mod == null) {
					return null;
				} else {
					String string3 = mod.getDir() + File.separator + string2;
					string3 = string3.replace("/", File.separator);
					string3 = string3.replace("\\", File.separator);
					String string4 = string3.substring(0, string3.lastIndexOf(File.separator));
					File file = new File(string4);
					if (!file.exists()) {
						file.mkdirs();
					}

					File file2 = new File(string3);
					if (!file2.exists() && boolean1) {
						try {
							file2.createNewFile();
						} catch (IOException ioException) {
							Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, ioException);
						}
					}

					FileWriter fileWriter = null;
					try {
						fileWriter = new FileWriter(file2, boolean2);
					} catch (IOException ioException2) {
						Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, ioException2);
					}

					LuaManager.GlobalObject.LuaFileWriter luaFileWriter = new LuaManager.GlobalObject.LuaFileWriter();
					luaFileWriter.writer = fileWriter;
					return luaFileWriter;
				}
			} else {
				return null;
			}
		}

		@LuaMethod(name = "updateFire", global = true)
		public static void updateFire() {
			IsoFireManager.Update();
		}

		@LuaMethod(name = "deletePlayerSave", global = true)
		public static void deletePlayerSave(String string) {
			String string2 = LuaManager.getLuaCacheDir() + File.separator + "Players" + File.separator + "player" + string + ".txt";
			string2 = string2.replace("/", File.separator);
			string2 = string2.replace("\\", File.separator);
			File file = new File(string2);
			file.delete();
		}

		@LuaMethod(name = "getControllerCount", global = true)
		public static int getControllerCount() {
			return GameWindow.GameInput.getControllerCount() + (Core.bDebug ? 3 : 0);
		}

		@LuaMethod(name = "getControllerName", global = true)
		public static String getControllerName(int int1) {
			if (int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount() + (Core.bDebug ? 3 : 0)) {
				if (Core.bDebug) {
					if (int1 == GameWindow.GameInput.getControllerCount()) {
						return "My Funky Headset";
					}

					if (int1 == GameWindow.GameInput.getControllerCount() + 1) {
						return "My Awesome Keyboard";
					}

					if (int1 == GameWindow.GameInput.getControllerCount() + 2) {
						return "My Integrated Microphone";
					}
				}

				return GameWindow.GameInput.getController(int1).getName();
			} else {
				return "???";
			}
		}

		@LuaMethod(name = "getControllerAxisCount", global = true)
		public static int getControllerAxisCount(int int1) {
			return int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount() ? GameWindow.GameInput.getController(int1).getAxisCount() : 0;
		}

		@LuaMethod(name = "getControllerAxisValue", global = true)
		public static float getControllerAxisValue(int int1, int int2) {
			if (int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount()) {
				return int2 >= 0 && int2 < GameWindow.GameInput.getAxisCount(int1) ? GameWindow.GameInput.getController(int1).getAxisValue(int2) : 0.0F;
			} else {
				return 0.0F;
			}
		}

		@LuaMethod(name = "getControllerDeadZone", global = true)
		public static float getControllerDeadZone(int int1, int int2) {
			if (int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount()) {
				return int2 >= 0 && int2 < GameWindow.GameInput.getAxisCount(int1) ? JoypadManager.instance.getDeadZone(int1, int2) : 0.0F;
			} else {
				return 0.0F;
			}
		}

		@LuaMethod(name = "setControllerDeadZone", global = true)
		public static void setControllerDeadZone(int int1, int int2, float float1) {
			if (int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount()) {
				if (int2 >= 0 && int2 < GameWindow.GameInput.getAxisCount(int1)) {
					JoypadManager.instance.setDeadZone(int1, int2, float1);
				}
			}
		}

		@LuaMethod(name = "saveControllerSettings", global = true)
		public static void saveControllerSettings(int int1) {
			if (int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount()) {
				JoypadManager.instance.saveControllerSettings(int1);
			}
		}

		@LuaMethod(name = "getControllerButtonCount", global = true)
		public static int getControllerButtonCount(int int1) {
			return int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount() ? GameWindow.GameInput.getController(int1).getButtonCount() : 0;
		}

		@LuaMethod(name = "getControllerPovX", global = true)
		public static float getControllerPovX(int int1) {
			return int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount() ? GameWindow.GameInput.getController(int1).getPovX() : 0.0F;
		}

		@LuaMethod(name = "getControllerPovY", global = true)
		public static float getControllerPovY(int int1) {
			return int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount() ? GameWindow.GameInput.getController(int1).getPovY() : 0.0F;
		}

		@LuaMethod(name = "reloadControllerConfigFiles", global = true)
		public static void reloadControllerConfigFiles() {
			JoypadManager.instance.reloadControllerFiles();
		}

		@LuaMethod(name = "isJoypadPressed", global = true)
		public static boolean isJoypadPressed(int int1, int int2) {
			return GameWindow.GameInput.isButtonPressed(int2, int1);
		}

		@LuaMethod(name = "isJoypadDown", global = true)
		public static boolean isJoypadDown(int int1) {
			return JoypadManager.instance.isDownPressed(int1);
		}

		@LuaMethod(name = "getJoypadAimingAxisX", global = true)
		public static float getJoypadAimingAxisX(int int1) {
			return JoypadManager.instance.getAimingAxisX(int1);
		}

		@LuaMethod(name = "getJoypadAimingAxisY", global = true)
		public static float getJoypadAimingAxisY(int int1) {
			return JoypadManager.instance.getAimingAxisY(int1);
		}

		@LuaMethod(name = "getJoypadMovementAxisX", global = true)
		public static float getJoypadMovementAxisX(int int1) {
			return JoypadManager.instance.getMovementAxisX(int1);
		}

		@LuaMethod(name = "getJoypadMovementAxisY", global = true)
		public static float getJoypadMovementAxisY(int int1) {
			return JoypadManager.instance.getMovementAxisY(int1);
		}

		@LuaMethod(name = "getJoypadAButton", global = true)
		public static int getJoypadAButton(int int1) {
			JoypadManager.Joypad joypad = JoypadManager.instance.getFromControllerID(int1);
			return joypad != null ? joypad.getAButton() : -1;
		}

		@LuaMethod(name = "getJoypadBButton", global = true)
		public static int getJoypadBButton(int int1) {
			JoypadManager.Joypad joypad = JoypadManager.instance.getFromControllerID(int1);
			return joypad != null ? joypad.getBButton() : -1;
		}

		@LuaMethod(name = "getJoypadXButton", global = true)
		public static int getJoypadXButton(int int1) {
			JoypadManager.Joypad joypad = JoypadManager.instance.getFromControllerID(int1);
			return joypad != null ? joypad.getXButton() : -1;
		}

		@LuaMethod(name = "getJoypadYButton", global = true)
		public static int getJoypadYButton(int int1) {
			JoypadManager.Joypad joypad = JoypadManager.instance.getFromControllerID(int1);
			return joypad != null ? joypad.getYButton() : -1;
		}

		@LuaMethod(name = "getJoypadLBumper", global = true)
		public static int getJoypadLBumper(int int1) {
			JoypadManager.Joypad joypad = JoypadManager.instance.getFromControllerID(int1);
			return joypad != null ? joypad.getLBumper() : -1;
		}

		@LuaMethod(name = "getJoypadRBumper", global = true)
		public static int getJoypadRBumper(int int1) {
			JoypadManager.Joypad joypad = JoypadManager.instance.getFromControllerID(int1);
			return joypad != null ? joypad.getRBumper() : -1;
		}

		@LuaMethod(name = "getJoypadBackButton", global = true)
		public static int getJoypadBackButton(int int1) {
			JoypadManager.Joypad joypad = JoypadManager.instance.getFromControllerID(int1);
			return joypad != null ? joypad.getBackButton() : -1;
		}

		@LuaMethod(name = "getJoypadStartButton", global = true)
		public static int getJoypadStartButton(int int1) {
			JoypadManager.Joypad joypad = JoypadManager.instance.getFromControllerID(int1);
			return joypad != null ? joypad.getStartButton() : -1;
		}

		@LuaMethod(name = "wasMouseActiveMoreRecentlyThanJoypad", global = true)
		public static boolean wasMouseActiveMoreRecentlyThanJoypad() {
			if (IsoPlayer.instance == null) {
				return true;
			} else if (IsoPlayer.instance.getJoypadBind() == -1) {
				return true;
			} else {
				return JoypadManager.instance.getLastActivity(IsoPlayer.instance.getJoypadBind()) < Mouse.lastActivity;
			}
		}

		@LuaMethod(name = "reactivateJoypadAfterResetLua", global = true)
		public static boolean reactivateJoypadAfterResetLua() {
			if (GameWindow.ActivatedJoyPad != null) {
				LuaEventManager.triggerEvent("OnJoypadActivateUI", GameWindow.ActivatedJoyPad.getID());
				return true;
			} else {
				return false;
			}
		}

		private static void addPlayerToWorld(int int1, IsoPlayer player, boolean boolean1) {
			if (IsoPlayer.players[int1] != null) {
				IsoPlayer.players[int1].setModel((String)null);
				IsoPlayer.players[int1].updateUsername();
				IsoPlayer.players[int1] = null;
			}

			player.PlayerIndex = int1;
			if (boolean1) {
				player.applyTraits(IsoWorld.instance.getLuaTraits());
				player.createKeyRing();
				ProfessionFactory.Profession profession = ProfessionFactory.getProfession(player.getDescriptor().getProfession());
				Iterator iterator;
				String string;
				if (profession != null && !profession.getFreeRecipes().isEmpty()) {
					iterator = profession.getFreeRecipes().iterator();
					while (iterator.hasNext()) {
						string = (String)iterator.next();
						player.getKnownRecipes().add(string);
					}
				}

				iterator = IsoWorld.instance.getLuaTraits().iterator();
				label43: while (true) {
					TraitFactory.Trait trait;
					do {
						do {
							if (!iterator.hasNext()) {
								LuaEventManager.triggerEvent("OnNewGame", player, player.getCurrentSquare());
								break label43;
							}

							string = (String)iterator.next();
							trait = TraitFactory.getTrait(string);
						}			 while (trait == null);
					}		 while (trait.getFreeRecipes().isEmpty());

					Iterator iterator2 = trait.getFreeRecipes().iterator();
					while (iterator2.hasNext()) {
						String string2 = (String)iterator2.next();
						player.getKnownRecipes().add(string2);
					}
				}
			}

			IsoPlayer.numPlayers = Math.max(IsoPlayer.numPlayers, int1 + 1);
			IsoWorld.instance.AddCoopPlayers.add(new AddCoopPlayer(player));
			if (int1 == 0) {
				IsoPlayer.setInstance(player);
			}
		}

		@LuaMethod(name = "toInt", global = true)
		public static int toInt(double double1) {
			return (int)double1;
		}

		@LuaMethod(name = "getClientUsername", global = true)
		public static String getClientUsername() {
			return GameClient.bClient ? GameClient.username : null;
		}

		@LuaMethod(name = "setPlayerJoypad", global = true)
		public static void setPlayerJoypad(int int1, int int2, IsoPlayer player, String string) {
			if (IsoPlayer.players[int1] == null || IsoPlayer.players[int1].isDead()) {
				boolean boolean1 = player == null;
				if (player == null) {
					IsoPlayer player2 = IsoPlayer.instance;
					IsoWorld world = IsoWorld.instance;
					int int3 = world.getLuaPosX() + 300 * world.getLuaSpawnCellX();
					int int4 = world.getLuaPosY() + 300 * world.getLuaSpawnCellY();
					int int5 = world.getLuaPosZ();
					DebugLog.log("coop player spawning at " + int3 + "," + int4 + "," + int5);
					player = new IsoPlayer(world.CurrentCell, world.getLuaPlayerDesc(), int3, int4, int5);
					IsoPlayer.instance = player2;
					world.CurrentCell.getAddList().remove(player);
					world.CurrentCell.getObjectList().remove(player);
					player.SaveFileName = IsoPlayer.getUniqueFileName();
				}

				if (GameClient.bClient) {
					if (string != null) {
						assert int1 != 0;
						player.username = string;
						player.getModData().rawset("username", string);
					} else {
						assert int1 == 0;
						player.username = GameClient.username;
					}
				}

				addPlayerToWorld(int1, player, boolean1);
			}

			player.JoypadBind = int2;
			JoypadManager.instance.assignJoypad(int2, int1);
		}

		@LuaMethod(name = "setPlayerMouse", global = true)
		public static void setPlayerMouse(IsoPlayer player) {
			byte byte1 = 0;
			boolean boolean1 = player == null;
			if (player == null) {
				IsoPlayer player2 = IsoPlayer.instance;
				IsoWorld world = IsoWorld.instance;
				int int1 = world.getLuaPosX() + 300 * world.getLuaSpawnCellX();
				int int2 = world.getLuaPosY() + 300 * world.getLuaSpawnCellY();
				int int3 = world.getLuaPosZ();
				DebugLog.log("coop player spawning at " + int1 + "," + int2 + "," + int3);
				player = new IsoPlayer(world.CurrentCell, world.getLuaPlayerDesc(), int1, int2, int3);
				IsoPlayer.instance = player2;
				world.CurrentCell.getAddList().remove(player);
				world.CurrentCell.getObjectList().remove(player);
				player.SaveFileName = null;
			}

			if (GameClient.bClient) {
				player.username = GameClient.username;
			}

			addPlayerToWorld(byte1, player, boolean1);
		}

		@LuaMethod(name = "isJoypadUp", global = true)
		public static boolean isJoypadUp(int int1) {
			return JoypadManager.instance.isUpPressed(int1);
		}

		@LuaMethod(name = "isJoypadLeft", global = true)
		public static boolean isJoypadLeft(int int1) {
			return JoypadManager.instance.isLeftPressed(int1);
		}

		@LuaMethod(name = "isJoypadRight", global = true)
		public static boolean isJoypadRight(int int1) {
			return JoypadManager.instance.isRightPressed(int1);
		}

		@LuaMethod(name = "getButtonCount", global = true)
		public static int getButtonCount(int int1) {
			return GameWindow.GameInput.getController(int1).getButtonCount();
		}

		@LuaMethod(name = "getFileWriter", global = true)
		public static LuaManager.GlobalObject.LuaFileWriter getFileWriter(String string, boolean boolean1, boolean boolean2) {
			if (string.contains("..")) {
				DebugLog.log("relative paths not allowed");
				return null;
			} else {
				String string2 = LuaManager.getLuaCacheDir() + File.separator + string;
				string2 = string2.replace("/", File.separator);
				string2 = string2.replace("\\", File.separator);
				String string3 = string2.substring(0, string2.lastIndexOf(File.separator));
				string3 = string3.replace("\\", "/");
				File file = new File(string3);
				if (!file.exists()) {
					file.mkdirs();
				}

				File file2 = new File(string2);
				if (!file2.exists() && boolean1) {
					try {
						file2.createNewFile();
					} catch (IOException ioException) {
						Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, ioException);
					}
				}

				FileWriter fileWriter = null;
				try {
					fileWriter = new FileWriter(file2, boolean2);
				} catch (IOException ioException2) {
					Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, ioException2);
				}

				LuaManager.GlobalObject.LuaFileWriter luaFileWriter = new LuaManager.GlobalObject.LuaFileWriter();
				luaFileWriter.writer = fileWriter;
				return luaFileWriter;
			}
		}

		@LuaMethod(name = "getSandboxFileWriter", global = true)
		public static LuaManager.GlobalObject.LuaFileWriter getSandboxFileWriter(String string, boolean boolean1, boolean boolean2) {
			String string2 = LuaManager.getSandboxCacheDir() + File.separator + string;
			string2 = string2.replace("/", File.separator);
			string2 = string2.replace("\\", File.separator);
			String string3 = string2.substring(0, string2.lastIndexOf(File.separator));
			string3 = string3.replace("\\", "/");
			File file = new File(string3);
			if (!file.exists()) {
				file.mkdirs();
			}

			File file2 = new File(string2);
			if (!file2.exists() && boolean1) {
				try {
					file2.createNewFile();
				} catch (IOException ioException) {
					Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, ioException);
				}
			}

			FileWriter fileWriter = null;
			try {
				fileWriter = new FileWriter(file2, boolean2);
			} catch (IOException ioException2) {
				Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, ioException2);
			}

			LuaManager.GlobalObject.LuaFileWriter luaFileWriter = new LuaManager.GlobalObject.LuaFileWriter();
			luaFileWriter.writer = fileWriter;
			return luaFileWriter;
		}

		@LuaMethod(name = "createStory", global = true)
		public static void createStory(String string) {
			Core.GameMode = string;
			String string2 = GameWindow.getGameModeCacheDir();
			string2 = string2.replace("/", File.separator);
			string2 = string2.replace("\\", File.separator);
			int int1 = 1;
			File file = null;
			boolean boolean1 = false;
			while (!boolean1) {
				file = new File(string2 + File.separator + "Game" + int1);
				if (!file.exists()) {
					boolean1 = true;
				} else {
					++int1;
				}
			}

			Core.GameSaveWorld = "newstory";
			ScriptManager.instance.Trigger("OnPostLoadStory");
		}

		@LuaMethod(name = "createWorld", global = true)
		public static void createWorld(String string) {
			if (string == null || string.isEmpty()) {
				string = "blah";
			}

			string = sanitizeWorldName(string);
			String string2 = GameWindow.getGameModeCacheDir() + File.separator + string + File.separator;
			string2 = string2.replace("/", File.separator);
			string2 = string2.replace("\\", File.separator);
			String string3 = string2.substring(0, string2.lastIndexOf(File.separator));
			string3 = string3.replace("\\", "/");
			File file = new File(string3);
			if (!file.exists() && !Core.getInstance().isNoSave()) {
				file.mkdirs();
			}

			Core.GameSaveWorld = string;
		}

		@LuaMethod(name = "sanitizeWorldName", global = true)
		public static String sanitizeWorldName(String string) {
			return string.replace(" ", "_").replace("/", "").replace("\\", "").replace("?", "").replace("*", "").replace("<", "").replace(">", "").replace(":", "").replace("|", "").trim();
		}

		@LuaMethod(name = "forceChangeState", global = true)
		public static void forceChangeState(GameState gameState) {
			GameWindow.states.forceNextState(gameState);
		}

		@LuaMethod(name = "endFileOutput", global = true)
		public static void endFileOutput() {
			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException ioException) {
					Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, ioException);
				}
			}

			outStream = null;
		}

		@LuaMethod(name = "getFileInput", global = true)
		public static DataInputStream getFileInput(String string) throws IOException {
			String string2 = LuaManager.getLuaCacheDir() + File.separator + string;
			string2 = string2.replace("/", File.separator);
			string2 = string2.replace("\\", File.separator);
			File file = new File(string2);
			if (file.exists()) {
				try {
					inStream = new FileInputStream(file);
				} catch (FileNotFoundException fileNotFoundException) {
					Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, fileNotFoundException);
				}

				DataInputStream dataInputStream = new DataInputStream(inStream);
				return dataInputStream;
			} else {
				return null;
			}
		}

		@LuaMethod(name = "getGameFilesInput", global = true)
		public static DataInputStream getGameFilesInput(String string) {
			String string2 = string.replace("/", File.separator);
			string2 = string2.replace("\\", File.separator);
			File file = new File(ZomboidFileSystem.instance.getString(string2));
			if (file.exists()) {
				try {
					inStream = new FileInputStream(file);
				} catch (FileNotFoundException fileNotFoundException) {
					Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, fileNotFoundException);
				}

				DataInputStream dataInputStream = new DataInputStream(inStream);
				return dataInputStream;
			} else {
				return null;
			}
		}

		@LuaMethod(name = "getGameFilesTextInput", global = true)
		public static BufferedReader getGameFilesTextInput(String string) {
			if (!Core.getInstance().getDebug()) {
				return null;
			} else {
				String string2 = string.replace("/", File.separator);
				string2 = string2.replace("\\", File.separator);
				File file = new File(string2);
				if (file.exists()) {
					try {
						inFileReader = new FileReader(string);
						inBufferedReader = new BufferedReader(inFileReader);
						return inBufferedReader;
					} catch (FileNotFoundException fileNotFoundException) {
						Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, fileNotFoundException);
					}
				}

				return null;
			}
		}

		@LuaMethod(name = "endTextFileInput", global = true)
		public static void endTextFileInput() {
			if (inBufferedReader != null) {
				try {
					inBufferedReader.close();
					inFileReader.close();
				} catch (IOException ioException) {
					Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, ioException);
				}
			}

			inBufferedReader = null;
			inFileReader = null;
		}

		@LuaMethod(name = "endFileInput", global = true)
		public static void endFileInput() {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException ioException) {
					Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, ioException);
				}
			}

			inStream = null;
		}

		@LuaMethod(name = "getLineNumber", global = true)
		public static int getLineNumber(LuaCallFrame luaCallFrame) {
			if (luaCallFrame.closure == null) {
				return 0;
			} else {
				int int1 = luaCallFrame.pc;
				if (int1 < 0) {
					int1 = 0;
				}

				if (int1 >= luaCallFrame.closure.prototype.lines.length) {
					int1 = luaCallFrame.closure.prototype.lines.length - 1;
				}

				return luaCallFrame.closure.prototype.lines[int1];
			}
		}

		@LuaMethod(name = "ZombRand", global = true)
		public static double ZombRand(double double1) {
			if (double1 == 0.0) {
				return 0.0;
			} else {
				return double1 < 0.0 ? (double)(-Rand.Next(-((long)double1), Rand.randlua)) : (double)Rand.Next((long)double1, Rand.randlua);
			}
		}

		@LuaMethod(name = "ZombRandBetween", global = true)
		public static double ZombRandBetween(double double1, double double2) {
			return (double)Rand.Next((long)double1, (long)double2, Rand.randlua);
		}

		@LuaMethod(name = "ZombRand", global = true)
		public static double ZombRand(double double1, double double2) {
			return (double)Rand.Next((int)double1, (int)double2, Rand.randlua);
		}

		@LuaMethod(name = "ZombRandFloat", global = true)
		public static float ZombRandFloat(float float1, float float2) {
			return Rand.Next(float1, float2, Rand.randlua);
		}

		@LuaMethod(name = "getShortenedFilename", global = true)
		public static String getShortenedFilename(String string) {
			return string.substring(string.indexOf("lua/") + 4);
		}

		@LuaMethod(name = "isKeyDown", global = true)
		public static boolean isKeyDown(int int1) {
			return GameKeyboard.isKeyDown(int1);
		}

		@LuaMethod(name = "getFMODSoundBank", global = true)
		public static BaseSoundBank getFMODSoundBank() {
			return BaseSoundBank.instance;
		}

		@LuaMethod(name = "isSoundPlaying", global = true)
		public static boolean isSoundPlaying(Object object) {
			return object instanceof Double ? FMODManager.instance.isPlaying(((Double)object).longValue()) : false;
		}

		@LuaMethod(name = "stopSound", global = true)
		public static void stopSound(long long1) {
			FMODManager.instance.stopSound(long1);
		}

		@LuaMethod(name = "isShiftKeyDown", global = true)
		public static boolean isShiftKeyDown() {
			return GameKeyboard.isKeyDown(42) || GameKeyboard.isKeyDown(54);
		}

		@LuaMethod(name = "isCtrlKeyDown", global = true)
		public static boolean isCtrlKeyDown() {
			return GameKeyboard.isKeyDown(29) || GameKeyboard.isKeyDown(157);
		}

		@LuaMethod(name = "getCore", global = true)
		public static Core getCore() {
			return Core.getInstance();
		}

		@LuaMethod(name = "getDebugOptions", global = true)
		public static DebugOptions getDebugOptions() {
			return DebugOptions.instance;
		}

		@LuaMethod(name = "setShowPausedMessage", global = true)
		public static void setShowPausedMessage(boolean boolean1) {
			UIManager.setShowPausedMessage(boolean1);
		}

		@LuaMethod(name = "setGameSpeed", global = true)
		public static void setGameSpeed(int int1) {
			if (UIManager.getSpeedControls() != null) {
				UIManager.getSpeedControls().SetCurrentGameSpeed(int1);
			}
		}

		@LuaMethod(name = "getFilenameOfCallframe", global = true)
		public static String getFilenameOfCallframe(LuaCallFrame luaCallFrame) {
			return luaCallFrame.closure == null ? null : luaCallFrame.closure.prototype.filename;
		}

		@LuaMethod(name = "getFilenameOfClosure", global = true)
		public static String getFilenameOfClosure(LuaClosure luaClosure) {
			return luaClosure == null ? null : luaClosure.prototype.filename;
		}

		@LuaMethod(name = "getFirstLineOfClosure", global = true)
		public static int getFirstLineOfClosure(LuaClosure luaClosure) {
			return luaClosure == null ? 0 : luaClosure.prototype.lines[0];
		}

		@LuaMethod(name = "getLocalVarCount", global = true)
		public static int getLocalVarCount(Coroutine coroutine) {
			LuaCallFrame luaCallFrame = coroutine.currentCallFrame();
			return luaCallFrame == null ? 0 : luaCallFrame.LocalVarNames.size();
		}

		@LuaMethod(name = "isModActive", global = true)
		public static boolean isModActive(ChooseGameInfo.Mod mod) {
			String string = mod.getDir();
			if (mod.getId() != null) {
				string = mod.getId();
			}

			return ZomboidFileSystem.instance.mods.contains(string);
		}

		@LuaMethod(name = "openUrl", global = true)
		public static void openURl(String string) {
			Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			if (desktop != null && desktop.isSupported(Action.BROWSE)) {
				try {
					URI uRI = new URI(string);
					desktop.browse(uRI);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}

		@LuaMethod(name = "getActivatedMods", global = true)
		public static ArrayList getActivatedMods() {
			return ZomboidFileSystem.instance.mods;
		}

		@LuaMethod(name = "toggleModActive", global = true)
		public static void toggleModActive(ChooseGameInfo.Mod mod, boolean boolean1) {
			String string = mod.getDir();
			if (mod.getId() != null) {
				string = mod.getId();
			}

			if (boolean1 && !ZomboidFileSystem.instance.mods.contains(string)) {
				ZomboidFileSystem.instance.mods.add(string);
			} else if (!boolean1) {
				ZomboidFileSystem.instance.mods.remove(string);
			}
		}

		@LuaMethod(name = "saveModsFile", global = true)
		public static void saveModsFile() {
			ZomboidFileSystem.instance.saveModsFile();
		}

		@LuaMethod(name = "getLocalVarName", global = true)
		public static String getLocalVarName(Coroutine coroutine, int int1) {
			LuaCallFrame luaCallFrame = coroutine.currentCallFrame();
			return (String)luaCallFrame.LocalVarNames.get(int1);
		}

		@LuaMethod(name = "getLocalVarStack", global = true)
		public static int getLocalVarStack(Coroutine coroutine, int int1) {
			LuaCallFrame luaCallFrame = coroutine.currentCallFrame();
			return (Integer)luaCallFrame.LocalVarToStackMap.get(luaCallFrame.LocalVarNames.get(int1));
		}

		@LuaMethod(name = "getCallframeTop", global = true)
		public static int getCallframeTop(Coroutine coroutine) {
			return coroutine.getCallframeTop();
		}

		@LuaMethod(name = "getCoroutineTop", global = true)
		public static int getCoroutineTop(Coroutine coroutine) {
			return coroutine.getTop();
		}

		@LuaMethod(name = "getCoroutineObjStack", global = true)
		public static Object getCoroutineObjStack(Coroutine coroutine, int int1) {
			return coroutine.getObjectFromStack(int1);
		}

		@LuaMethod(name = "getCoroutineObjStackWithBase", global = true)
		public static Object getCoroutineObjStackWithBase(Coroutine coroutine, int int1) {
			return coroutine.getObjectFromStack(int1 - coroutine.currentCallFrame().localBase);
		}

		@LuaMethod(name = "localVarName", global = true)
		public static String localVarName(Coroutine coroutine, int int1) {
			int int2 = coroutine.getCallframeTop() - 1;
			if (int2 < 0) {
				boolean boolean1 = false;
			}

			return "";
		}

		@LuaMethod(name = "getCoroutineCallframeStack", global = true)
		public static LuaCallFrame getCoroutineCallframeStack(Coroutine coroutine, int int1) {
			return coroutine.getCallFrame(int1);
		}

		@LuaMethod(name = "createTile", global = true)
		public static void createTile(String string, IsoGridSquare square) {
			synchronized (IsoWorld.instance.CurrentCell.getSpriteManager().NamedMap) {
				IsoSprite sprite = (IsoSprite)IsoWorld.instance.CurrentCell.SpriteManager.NamedMap.get(string);
				if (sprite != null) {
					int int1 = 0;
					int int2 = 0;
					int int3 = 0;
					if (square != null) {
						int1 = square.getX();
						int2 = square.getY();
						int3 = square.getZ();
					}

					CellLoader.DoTileObjectCreation(sprite, sprite.getType(), square, IsoWorld.instance.CurrentCell, int1, int2, int3, (Stack)null, false, string);
				}
			}
		}

		@LuaMethod(name = "getNumClassFunctions", global = true)
		public static int getNumClassFunctions(Object object) {
			return object.getClass().getDeclaredMethods().length;
		}

		@LuaMethod(name = "getClassFunction", global = true)
		public static Method getClassFunction(Object object, int int1) {
			Method method = object.getClass().getDeclaredMethods()[int1];
			return method;
		}

		@LuaMethod(name = "getNumClassFields", global = true)
		public static int getNumClassFields(Object object) {
			return object.getClass().getDeclaredFields().length;
		}

		@LuaMethod(name = "getClassField", global = true)
		public static Field getClassField(Object object, int int1) {
			Field field = object.getClass().getDeclaredFields()[int1];
			field.setAccessible(true);
			return field;
		}

		@LuaMethod(name = "getDirectionTo", global = true)
		public static IsoDirections getDirectionTo(IsoGameCharacter gameCharacter, IsoObject object) {
			Vector2 vector2 = new Vector2(object.getX(), object.getY());
			vector2.x -= gameCharacter.x;
			vector2.y -= gameCharacter.y;
			return IsoDirections.fromAngle(vector2);
		}

		@LuaMethod(name = "translatePointXInOverheadMapToWindow", global = true)
		public static float translatePointXInOverheadMapToWindow(float float1, UIElement uIElement, float float2, float float3) {
			IngameState.draww = (float)uIElement.getWidth().intValue();
			return IngameState.translatePointX(float1, float3, float2, 0.0F);
		}

		@LuaMethod(name = "translatePointYInOverheadMapToWindow", global = true)
		public static float translatePointYInOverheadMapToWindow(float float1, UIElement uIElement, float float2, float float3) {
			IngameState.drawh = (float)uIElement.getHeight().intValue();
			return IngameState.translatePointY(float1, float3, float2, 0.0F);
		}

		@LuaMethod(name = "translatePointXInOverheadMapToWorld", global = true)
		public static float translatePointXInOverheadMapToWorld(float float1, UIElement uIElement, float float2, float float3) {
			IngameState.draww = (float)uIElement.getWidth().intValue();
			return IngameState.invTranslatePointX(float1, float3, float2, 0.0F);
		}

		@LuaMethod(name = "translatePointYInOverheadMapToWorld", global = true)
		public static float translatePointYInOverheadMapToWorld(float float1, UIElement uIElement, float float2, float float3) {
			IngameState.drawh = (float)uIElement.getHeight().intValue();
			return IngameState.invTranslatePointY(float1, float3, float2, 0.0F);
		}

		@LuaMethod(name = "drawOverheadMap", global = true)
		public static void drawOverheadMap(UIElement uIElement, float float1, float float2, float float3) {
			IngameState.renderDebugOverhead2(getCell(), 0, float1, uIElement.getAbsoluteX().intValue(), uIElement.getAbsoluteY().intValue(), float2, float3, uIElement.getWidth().intValue(), uIElement.getHeight().intValue());
		}

		@LuaMethod(name = "assaultPlayer", global = true)
		public static void assaultPlayer() {
			assert false;
		}

		@LuaMethod(name = "zpopNewRenderer", global = true)
		public static ZombiePopulationRenderer zpopNewRenderer() {
			return new ZombiePopulationRenderer();
		}

		@LuaMethod(name = "zpopSpawnTimeToZero", global = true)
		public static void zpopSpawnTimeToZero(int int1, int int2) {
			ZombiePopulationManager.instance.dbgSpawnTimeToZero(int1, int2);
		}

		@LuaMethod(name = "zpopClearZombies", global = true)
		public static void zpopClearZombies(int int1, int int2) {
			ZombiePopulationManager.instance.dbgClearZombies(int1, int2);
		}

		@LuaMethod(name = "zpopSpawnNow", global = true)
		public static void zpopSpawnNow(int int1, int int2) {
			ZombiePopulationManager.instance.dbgSpawnNow(int1, int2);
		}

		@LuaMethod(name = "addVirtualZombie", global = true)
		public static void addVirtualZombie(int int1, int int2) {
		}

		@LuaMethod(name = "setAggroTarget", global = true)
		public static void setAggroTarget(int int1, int int2, int int3) {
			ZombiePopulationManager.instance.setAggroTarget(int1, int2, int3);
		}

		@LuaMethod(name = "debugFullyStreamedIn", global = true)
		public static void debugFullyStreamedIn(int int1, int int2) {
			IngameState.instance.debugFullyStreamedIn(int1, int2);
		}

		@LuaMethod(name = "getClassFieldVal", global = true)
		public static Object getClassFieldVal(Object object, Field field) {
			try {
				return field.get(object);
			} catch (Exception exception) {
				return "<private>";
			}
		}

		@LuaMethod(name = "getMethodParameter", global = true)
		public static String getMethodParameter(Method method, int int1) {
			return method.getParameterTypes()[int1].getSimpleName();
		}

		@LuaMethod(name = "getMethodParameterCount", global = true)
		public static int getMethodParameterCount(Method method) {
			return method.getParameterTypes().length;
		}

		@LuaMethod(name = "breakpoint", global = true)
		public static void breakpoint() {
			boolean boolean1 = false;
		}

		@LuaMethod(name = "getGameSpeed", global = true)
		public static int getGameSpeed() {
			return UIManager.getSpeedControls() != null ? UIManager.getSpeedControls().getCurrentGameSpeed() : 0;
		}

		@LuaMethod(name = "getMouseXScaled", global = true)
		public static int getMouseXScaled() {
			return Mouse.getX();
		}

		@LuaMethod(name = "getMouseYScaled", global = true)
		public static int getMouseYScaled() {
			return Mouse.getY();
		}

		@LuaMethod(name = "getMouseX", global = true)
		public static int getMouseX() {
			return Mouse.getXA();
		}

		@LuaMethod(name = "setMouseXY", global = true)
		public static void setMouseXY(int int1, int int2) {
			Mouse.setXY(int1, int2);
		}

		@LuaMethod(name = "isMouseButtonDown", global = true)
		public static boolean isMouseButtonDown(int int1) {
			return Mouse.isButtonDown(int1);
		}

		@LuaMethod(name = "getMouseY", global = true)
		public static int getMouseY() {
			return Mouse.getYA();
		}

		@LuaMethod(name = "getSoundManager", global = true)
		public static BaseSoundManager getSoundManager() {
			return SoundManager.instance;
		}

		@LuaMethod(name = "getLastPlayedDate", global = true)
		public static String getLastPlayedDate(String string) {
			File file = new File(GameWindow.getSaveDir() + File.separator + string);
			if (!file.exists()) {
				return Translator.getText("UI_LastPlayed") + "???";
			} else {
				Date date = new Date(file.lastModified());
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				String string2 = simpleDateFormat.format(date);
				return Translator.getText("UI_LastPlayed") + string2;
			}
		}

		@LuaMethod(name = "getTextureFromSaveDir", global = true)
		public static Texture getTextureFromSaveDir(String string, String string2) {
			TextureID.UseFiltering = true;
			Texture texture = Texture.getSharedTexture(GameWindow.getSaveDir() + File.separator + string2 + File.separator + string);
			TextureID.UseFiltering = false;
			return texture;
		}

		@LuaMethod(name = "getSaveInfo", global = true)
		public static KahluaTable getSaveInfo(String string) {
			if (!string.contains(File.separator)) {
				string = IsoWorld.instance.getGameMode() + File.separator + string;
			}

			KahluaTable kahluaTable = LuaManager.platform.newTable();
			File file = new File(GameWindow.getSaveDir() + File.separator + string);
			if (file.exists()) {
				Date date = new Date(file.lastModified());
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				String string2 = simpleDateFormat.format(date);
				kahluaTable.rawset("lastPlayed", string2);
				String[] stringArray = string.split("\\" + File.separator);
				kahluaTable.rawset("saveName", file.getName());
				kahluaTable.rawset("gameMode", stringArray[stringArray.length - 2]);
			}

			file = new File(GameWindow.getSaveDir() + File.separator + string + File.separator + "map_ver.bin");
			if (file.exists()) {
				try {
					FileInputStream fileInputStream = new FileInputStream(file);
					DataInputStream dataInputStream = new DataInputStream(fileInputStream);
					int int1 = dataInputStream.readInt();
					kahluaTable.rawset("worldVersion", (double)int1);
					String string3;
					if (int1 >= 18) {
						try {
							string3 = GameWindow.ReadString(dataInputStream);
							if (string3.equals("DEFAULT")) {
								string3 = "Muldraugh, KY";
							}

							kahluaTable.rawset("mapName", string3);
						} catch (Exception exception) {
						}
					}

					if (int1 >= 74) {
						try {
							string3 = GameWindow.ReadString(dataInputStream);
							kahluaTable.rawset("difficulty", string3);
						} catch (Exception exception2) {
						}
					}

					dataInputStream.close();
				} catch (Exception exception3) {
					exception3.printStackTrace();
				}
			}

			file = new File(GameWindow.getSaveDir() + File.separator + string + File.separator + "map_p.bin");
			kahluaTable.rawset("playerAlive", file.exists());
			return kahluaTable;
		}

		@LuaMethod(name = "getServerSavedWorldVersion", global = true)
		public static int getServerSavedWorldVersion(String string) {
			File file = new File(GameWindow.getSaveDir() + File.separator + string + File.separator + "map_t.bin");
			if (file.exists()) {
				try {
					FileInputStream fileInputStream = new FileInputStream(file);
					Throwable throwable = null;
					int int1;
					try {
						DataInputStream dataInputStream = new DataInputStream(fileInputStream);
						Throwable throwable2 = null;
						try {
							byte byte1 = dataInputStream.readByte();
							byte byte2 = dataInputStream.readByte();
							byte byte3 = dataInputStream.readByte();
							byte byte4 = dataInputStream.readByte();
							if (byte1 != 71 || byte2 != 77 || byte3 != 84 || byte4 != 77) {
								int1 = 1;
								return int1;
							}

							int1 = dataInputStream.readInt();
						} catch (Throwable throwable3) {
							throwable2 = throwable3;
							throw throwable3;
						} finally {
							if (dataInputStream != null) {
								if (throwable2 != null) {
									try {
										dataInputStream.close();
									} catch (Throwable throwable4) {
										throwable2.addSuppressed(throwable4);
									}
								} else {
									dataInputStream.close();
								}
							}
						}
					} catch (Throwable throwable5) {
						throwable = throwable5;
						throw throwable5;
					} finally {
						if (fileInputStream != null) {
							if (throwable != null) {
								try {
									fileInputStream.close();
								} catch (Throwable throwable6) {
									throwable.addSuppressed(throwable6);
								}
							} else {
								fileInputStream.close();
							}
						}
					}

					return int1;
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}

			return 0;
		}

		@LuaMethod(name = "getMapInfo", global = true)
		public static KahluaTable getMapInfo(String string) {
			if (string.contains(";")) {
				string = string.split(";")[0];
			}

			ChooseGameInfo.Map map = ChooseGameInfo.getMapDetails(string);
			if (map == null) {
				return null;
			} else {
				KahluaTable kahluaTable = LuaManager.platform.newTable();
				kahluaTable.rawset("description", map.getDescription());
				kahluaTable.rawset("dir", map.getDirectory());
				KahluaTable kahluaTable2 = LuaManager.platform.newTable();
				byte byte1 = 1;
				Iterator iterator = map.getLotDirectories().iterator();
				while (iterator.hasNext()) {
					String string2 = (String)iterator.next();
					kahluaTable2.rawset((double)byte1, string2);
				}

				kahluaTable.rawset("lots", kahluaTable2);
				kahluaTable.rawset("thumb", map.getThumbnail());
				kahluaTable.rawset("title", map.getTitle());
				return kahluaTable;
			}
		}

		@LuaMethod(name = "getVehicleInfo", global = true)
		public static KahluaTable getVehicleInfo(BaseVehicle baseVehicle) {
			if (baseVehicle == null) {
				return null;
			} else {
				KahluaTable kahluaTable = LuaManager.platform.newTable();
				kahluaTable.rawset("name", baseVehicle.getScript().getName());
				kahluaTable.rawset("weight", baseVehicle.getMass());
				kahluaTable.rawset("speed", baseVehicle.getMaxSpeed());
				kahluaTable.rawset("frontEndDurability", Integer.toString(baseVehicle.frontEndDurability));
				kahluaTable.rawset("rearEndDurability", Integer.toString(baseVehicle.rearEndDurability));
				kahluaTable.rawset("currentFrontEndDurability", Integer.toString(baseVehicle.currentFrontEndDurability));
				kahluaTable.rawset("currentRearEndDurability", Integer.toString(baseVehicle.currentRearEndDurability));
				kahluaTable.rawset("engine_running", baseVehicle.isEngineRunning());
				kahluaTable.rawset("engine_started", baseVehicle.isEngineStarted());
				kahluaTable.rawset("engine_quality", baseVehicle.getEngineQuality());
				kahluaTable.rawset("engine_loudness", baseVehicle.getEngineLoudness());
				kahluaTable.rawset("engine_power", baseVehicle.getEnginePower());
				kahluaTable.rawset("battery_isset", baseVehicle.getBattery() != null);
				kahluaTable.rawset("battery_charge", baseVehicle.getBatteryCharge());
				kahluaTable.rawset("gas_amount", baseVehicle.getPartById("GasTank").getContainerContentAmount());
				kahluaTable.rawset("gas_capacity", baseVehicle.getPartById("GasTank").getContainerCapacity());
				VehiclePart vehiclePart = baseVehicle.getPartById("DoorFrontLeft");
				kahluaTable.rawset("doorleft_exist", vehiclePart != null);
				if (vehiclePart != null) {
					kahluaTable.rawset("doorleft_open", vehiclePart.getDoor().isOpen());
					kahluaTable.rawset("doorleft_locked", vehiclePart.getDoor().isLocked());
					kahluaTable.rawset("doorleft_lockbroken", vehiclePart.getDoor().isLockBroken());
					VehicleWindow vehicleWindow = vehiclePart.findWindow();
					kahluaTable.rawset("windowleft_exist", vehicleWindow != null);
					if (vehicleWindow != null) {
						kahluaTable.rawset("windowleft_open", vehicleWindow.isOpen());
						kahluaTable.rawset("windowleft_health", vehicleWindow.getHealth());
					}
				}

				VehiclePart vehiclePart2 = baseVehicle.getPartById("DoorFrontRight");
				kahluaTable.rawset("doorright_exist", vehiclePart2 != null);
				if (vehiclePart != null) {
					kahluaTable.rawset("doorright_open", vehiclePart2.getDoor().isOpen());
					kahluaTable.rawset("doorright_locked", vehiclePart2.getDoor().isLocked());
					kahluaTable.rawset("doorright_lockbroken", vehiclePart2.getDoor().isLockBroken());
					VehicleWindow vehicleWindow2 = vehiclePart2.findWindow();
					kahluaTable.rawset("windowright_exist", vehicleWindow2 != null);
					if (vehicleWindow2 != null) {
						kahluaTable.rawset("windowright_open", vehicleWindow2.isOpen());
						kahluaTable.rawset("windowright_health", vehicleWindow2.getHealth());
					}
				}

				kahluaTable.rawset("headlights_set", baseVehicle.hasHeadlights());
				kahluaTable.rawset("headlights_on", baseVehicle.getHeadlightsOn());
				if (baseVehicle.getPartById("Heater") != null) {
					kahluaTable.rawset("heater_isset", true);
					Object object = baseVehicle.getPartById("Heater").getModData().rawget("active");
					if (object == null) {
						kahluaTable.rawset("heater_on", false);
					} else {
						kahluaTable.rawset("heater_on", object == Boolean.TRUE);
					}
				} else {
					kahluaTable.rawset("heater_isset", false);
				}

				return kahluaTable;
			}
		}

		@LuaMethod(name = "getLotDirectories", global = true)
		public static ArrayList getLotDirectories() {
			return IsoWorld.instance.MetaGrid != null ? IsoWorld.instance.MetaGrid.getLotDirectories() : null;
		}

		@LuaMethod(name = "useTextureFiltering", global = true)
		public static void useTextureFiltering(boolean boolean1) {
			TextureID.UseFiltering = boolean1;
		}

		@LuaMethod(name = "getTexture", global = true)
		public static Texture getTexture(String string) {
			return Texture.getSharedTexture(string);
		}

		@LuaMethod(name = "getTextManager", global = true)
		public static TextManager getTextManager() {
			return TextManager.instance;
		}

		@LuaMethod(name = "setProgressBarValue", global = true)
		public static void setProgressBarValue(IsoPlayer player, int int1) {
			if (player.isLocalPlayer()) {
				UIManager.getProgressBar((double)player.getPlayerNum()).setValue((float)int1);
			}
		}

		@LuaMethod(name = "getText", global = true)
		public static String getText(String string) {
			return Translator.getText(string);
		}

		@LuaMethod(name = "getText", global = true)
		public static String getText(String string, Object object) {
			return Translator.getText(string, object);
		}

		@LuaMethod(name = "getText", global = true)
		public static String getText(String string, Object object, Object object2) {
			return Translator.getText(string, object, object2);
		}

		@LuaMethod(name = "getText", global = true)
		public static String getText(String string, Object object, Object object2, Object object3) {
			return Translator.getText(string, object, object2, object3);
		}

		@LuaMethod(name = "getText", global = true)
		public static String getText(String string, Object object, Object object2, Object object3, Object object4) {
			return Translator.getText(string, object, object2, object3, object4);
		}

		@LuaMethod(name = "getTextOrNull", global = true)
		public static String getTextOrNull(String string) {
			return Translator.getTextOrNull(string);
		}

		@LuaMethod(name = "getTextOrNull", global = true)
		public static String getTextOrNull(String string, Object object) {
			return Translator.getTextOrNull(string, object);
		}

		@LuaMethod(name = "getTextOrNull", global = true)
		public static String getTextOrNull(String string, Object object, Object object2) {
			return Translator.getTextOrNull(string, object, object2);
		}

		@LuaMethod(name = "getTextOrNull", global = true)
		public static String getTextOrNull(String string, Object object, Object object2, Object object3) {
			return Translator.getTextOrNull(string, object, object2, object3);
		}

		@LuaMethod(name = "getTextOrNull", global = true)
		public static String getTextOrNull(String string, Object object, Object object2, Object object3, Object object4) {
			return Translator.getTextOrNull(string, object, object2, object3, object4);
		}

		@LuaMethod(name = "getItemText", global = true)
		public static String getItemText(String string) {
			return Translator.getDisplayItemName(string);
		}

		@LuaMethod(name = "getRecipeDisplayName", global = true)
		public static String getRecipeDisplayName(String string) {
			return Translator.getRecipeName(string);
		}

		@LuaMethod(name = "getMyDocumentFolder", global = true)
		public static String getMyDocumentFolder() {
			return Core.getMyDocumentFolder();
		}

		@LuaMethod(name = "getSprite", global = true)
		public static IsoSprite getSprite(String string) {
			return getCell().SpriteManager.getSprite(string);
		}

		@LuaMethod(name = "getServerModData", global = true)
		public static void getServerModData() {
			GameClient.getCustomModData();
		}

		@LuaMethod(name = "isXBOXController", global = true)
		public static boolean isXBOXController() {
			for (int int1 = 0; int1 < GameWindow.GameInput.getControllerCount(); ++int1) {
				if (GameWindow.GameInput.getController(int1).getName().contains("XBOX 360")) {
					return true;
				}
			}

			return false;
		}

		@LuaMethod(name = "sendClientCommand", global = true)
		public static void sendClientCommand(String string, String string2, KahluaTable kahluaTable) {
			if (GameClient.bClient && GameClient.bIngame) {
				GameClient.instance.sendClientCommand((IsoPlayer)null, string, string2, kahluaTable);
			}
		}

		@LuaMethod(name = "sendClientCommand", global = true)
		public static void sendClientCommand(IsoPlayer player, String string, String string2, KahluaTable kahluaTable) {
			if (player != null && player.isLocalPlayer()) {
				if (GameClient.bClient && GameClient.bIngame) {
					GameClient.instance.sendClientCommand(player, string, string2, kahluaTable);
				} else {
					if (GameServer.bServer) {
						throw new IllegalStateException("can\'t call this function on the server");
					}

					SinglePlayerClient.sendClientCommand(player, string, string2, kahluaTable);
				}
			}
		}

		@LuaMethod(name = "sendServerCommand", global = true)
		public static void sendServerCommand(String string, String string2, KahluaTable kahluaTable) {
			if (GameServer.bServer) {
				GameServer.sendServerCommand(string, string2, kahluaTable);
			}
		}

		@LuaMethod(name = "sendServerCommand", global = true)
		public static void sendServerCommand(IsoPlayer player, String string, String string2, KahluaTable kahluaTable) {
			if (GameServer.bServer) {
				GameServer.sendServerCommand(player, string, string2, kahluaTable);
			}
		}

		@LuaMethod(name = "getOnlineUsername", global = true)
		public static String getOnlineUsername() {
			return IsoPlayer.instance.getDisplayName();
		}

		@LuaMethod(name = "isValidUserName", global = true)
		public static boolean isValidUserName(String string) {
			return ServerWorldDatabase.isValidUserName(string);
		}

		@LuaMethod(name = "getHourMinute", global = true)
		public static String getHourMinute() {
			return LuaManager.getHourMinuteJava();
		}

		@LuaMethod(name = "SendCommandToServer", global = true)
		public static void SendCommandToServer(String string) {
			GameClient.SendCommandToServer(string);
		}

		@LuaMethod(name = "isAdmin", global = true)
		public static boolean isAdmin() {
			return GameClient.bClient && GameClient.accessLevel.equals("admin");
		}

		@LuaMethod(name = "canModifyPlayerScoreboard", global = true)
		public static boolean canModifyPlayerScoreboard() {
			return GameClient.bClient && !GameClient.accessLevel.equals("");
		}

		@LuaMethod(name = "isAccessLevel", global = true)
		public static boolean isAccessLevel(String string) {
			if (GameClient.bClient) {
				return GameClient.accessLevel.equals("") ? false : GameClient.accessLevel.equals(string);
			} else {
				return false;
			}
		}

		@LuaMethod(name = "sendBandage", global = true)
		public static void sendBandage(int int1, int int2, boolean boolean1, float float1, boolean boolean2, String string) {
			GameClient.instance.sendBandage(int1, int2, boolean1, float1, boolean2, string);
		}

		@LuaMethod(name = "sendCataplasm", global = true)
		public static void sendCataplasm(int int1, int int2, float float1, float float2, float float3) {
			GameClient.instance.sendCataplasm(int1, int2, float1, float2, float3);
		}

		@LuaMethod(name = "sendStitch", global = true)
		public static void sendStitch(int int1, int int2, boolean boolean1, float float1) {
			GameClient.instance.sendStitch(int1, int2, boolean1, float1);
		}

		@LuaMethod(name = "sendWoundInfection", global = true)
		public static void sendWoundInfection(int int1, int int2, boolean boolean1) {
			GameClient.instance.sendWoundInfection(int1, int2, boolean1);
		}

		@LuaMethod(name = "sendDisinfect", global = true)
		public static void sendDisinfect(int int1, int int2, float float1) {
			GameClient.instance.sendDisinfect(int1, int2, float1);
		}

		@LuaMethod(name = "sendSplint", global = true)
		public static void sendSplint(int int1, int int2, boolean boolean1, float float1, String string) {
			GameClient.instance.sendSplint(int1, int2, boolean1, float1, string);
		}

		@LuaMethod(name = "sendAdditionalPain", global = true)
		public static void sendAdditionalPain(int int1, int int2, float float1) {
			GameClient.instance.sendAdditionalPain(int1, int2, float1);
		}

		@LuaMethod(name = "sendRemoveGlass", global = true)
		public static void sendRemoveGlass(int int1, int int2) {
			GameClient.instance.sendRemoveGlass(int1, int2);
		}

		@LuaMethod(name = "sendRemoveBullet", global = true)
		public static void sendRemoveBullet(int int1, int int2, int int3) {
			GameClient.instance.sendRemoveBullet(int1, int2, int3);
		}

		@LuaMethod(name = "sendCleanBurn", global = true)
		public static void sendCleanBurn(int int1, int int2) {
			GameClient.instance.sendCleanBurn(int1, int2);
		}

		@LuaMethod(name = "getGameClient", global = true)
		public static GameClient getGameClient() {
			return GameClient.instance;
		}

		@LuaMethod(name = "sendRequestInventory", global = true)
		public static void sendRequestInventory(IsoPlayer player) {
			GameClient.sendRequestInventory(player);
		}

		@LuaMethod(name = "InvMngGetItem", global = true)
		public static void InvMngGetItem(long long1, String string, IsoPlayer player) {
			GameClient.invMngRequestItem(long1, string, player);
		}

		@LuaMethod(name = "InvMngRemoveItem", global = true)
		public static void InvMngRemoveItem(long long1, IsoPlayer player) {
			GameClient.invMngRequestRemoveItem(long1, player);
		}

		@LuaMethod(name = "getConnectedPlayers", global = true)
		public static ArrayList getConnectedPlayers() {
			return GameClient.instance.getConnectedPlayers();
		}

		@LuaMethod(name = "getPlayerFromUsername", global = true)
		public static IsoPlayer getPlayerFromUsername(String string) {
			return GameClient.instance.getPlayerFromUsername(string);
		}

		@LuaMethod(name = "isCoopHost", global = true)
		public static boolean isCoopHost() {
			return GameClient.connection != null && GameClient.connection.isCoopHost;
		}

		@LuaMethod(name = "setAdmin", global = true)
		public static void setAdmin() {
			if (CoopMaster.instance.isRunning()) {
				String string = "admin";
				if (GameClient.connection.accessLevel.equals("admin")) {
					string = "";
				}

				GameClient.connection.accessLevel = string;
				GameClient.accessLevel = string;
				IsoPlayer.instance.accessLevel = string;
				GameClient.SendCommandToServer("/setaccesslevel \"" + IsoPlayer.instance.username + "\" \"" + (string.equals("") ? "none" : string) + "\"");
				if (string.equals("") && IsoPlayer.instance.invisible || string.equals("admin") && !IsoPlayer.instance.invisible) {
					GameClient.SendCommandToServer("/invisible");
				}
			}
		}

		@LuaMethod(name = "addWarningPoint", global = true)
		public static void addWarningPoint(String string, String string2, int int1) {
			if (GameClient.bClient) {
				GameClient.instance.addWarningPoint(string, string2, int1);
			}
		}

		@LuaMethod(name = "toggleSafetyServer", global = true)
		public static void toggleSafetyServer(IsoPlayer player) {
			GameClient.toggleSafety(player);
		}

		@LuaMethod(name = "disconnect", global = true)
		public static void disconnect() {
			GameClient.connection.forceDisconnect();
		}

		@LuaMethod(name = "writeLog", global = true)
		public static void writeLog(String string, String string2) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.doPacket((short)88, byteBufferWriter);
			byteBufferWriter.putUTF(string);
			byteBufferWriter.putUTF(string2);
			GameClient.connection.endPacket();
		}

		@LuaMethod(name = "doKeyPress", global = true)
		public static void doKeyPress(boolean boolean1) {
			GameKeyboard.doLuaKeyPressed = boolean1;
		}

		@LuaMethod(name = "getEvolvedRecipes", global = true)
		public static Stack getEvolvedRecipes() {
			return ScriptManager.instance.getAllEvolvedRecipes();
		}

		@LuaMethod(name = "getZone", global = true)
		public static IsoMetaGrid.Zone getZone(int int1, int int2, int int3) {
			return IsoWorld.instance.MetaGrid.getZoneAt(int1, int2, int3);
		}

		@LuaMethod(name = "getZones", global = true)
		public static ArrayList getZones(int int1, int int2, int int3) {
			return IsoWorld.instance.MetaGrid.getZonesAt(int1, int2, int3);
		}

		@LuaMethod(name = "replaceWith", global = true)
		public static String replaceWith(String string, String string2, String string3) {
			return string.replaceFirst(string2, string3);
		}

		@LuaMethod(name = "getTimestamp", global = true)
		public static long getTimestamp() {
			return System.currentTimeMillis() / 1000L;
		}

		@LuaMethod(name = "getTimestampMs", global = true)
		public static long getTimestampMs() {
			return System.currentTimeMillis();
		}

		@LuaMethod(name = "forceSnowCheck", global = true)
		public static void forceSnowCheck() {
			ErosionMain.getInstance().snowCheck();
		}

		@LuaMethod(name = "getGametimeTimestamp", global = true)
		public static long getGametimeTimestamp() {
			return GameTime.instance.getCalender().getTimeInMillis() / 1000L;
		}

		@LuaMethod(name = "canInviteFriends", global = true)
		public static boolean canInviteFriends() {
			if (GameClient.bClient && SteamUtils.isSteamModeEnabled()) {
				return CoopMaster.instance.isRunning() || !GameClient.bCoopInvite;
			} else {
				return false;
			}
		}

		@LuaMethod(name = "inviteFriend", global = true)
		public static void inviteFriend(String string) {
			if (CoopMaster.instance != null && CoopMaster.instance.isRunning()) {
				CoopMaster.instance.sendMessage("invite-add", string);
			}

			SteamFriends.InviteUserToGame(SteamUtils.convertStringToSteamID(string), "+connect " + GameClient.ip + ":" + GameClient.port);
		}

		@LuaMethod(name = "getFriendsList", global = true)
		public static KahluaTable getFriendsList() {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			if (!getSteamModeActive()) {
				return kahluaTable;
			} else {
				List list = SteamFriends.GetFriendList();
				int int1 = 1;
				for (int int2 = 0; int2 < list.size(); ++int2) {
					SteamFriend steamFriend = (SteamFriend)list.get(int2);
					Double Double1 = (double)int1;
					kahluaTable.rawset(Double1, steamFriend);
					++int1;
				}

				return kahluaTable;
			}
		}

		@LuaMethod(name = "getSteamModeActive", global = true)
		public static Boolean getSteamModeActive() {
			return SteamUtils.isSteamModeEnabled();
		}

		@LuaMethod(name = "isValidSteamID", global = true)
		public static boolean isValidSteamID(String string) {
			return string != null && !string.isEmpty() ? SteamUtils.isValidSteamID(string) : false;
		}

		@LuaMethod(name = "getCurrentUserSteamID", global = true)
		public static String getCurrentUserSteamID() {
			return SteamUtils.isSteamModeEnabled() && !GameServer.bServer ? SteamUser.GetSteamIDString() : null;
		}

		@LuaMethod(name = "getCurrentUserProfileName", global = true)
		public static String getCurrentUserProfileName() {
			return SteamUtils.isSteamModeEnabled() && !GameServer.bServer ? SteamFriends.GetFriendPersonaName(SteamUser.GetSteamID()) : null;
		}

		@LuaMethod(name = "getSteamScoreboard", global = true)
		public static boolean getSteamScoreboard() {
			if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
				String string = ServerOptions.instance.SteamScoreboard.getValue();
				return "true".equals(string) || GameClient.accessLevel.equals("admin") && "admin".equals(string);
			} else {
				return false;
			}
		}

		@LuaMethod(name = "isSteamOverlayEnabled", global = true)
		public static boolean isSteamOverlayEnabled() {
			return SteamUtils.isOverlayEnabled();
		}

		@LuaMethod(name = "activateSteamOverlayToWorkshop", global = true)
		public static void activateSteamOverlayToWorkshop() {
			if (SteamUtils.isOverlayEnabled()) {
				SteamFriends.ActivateGameOverlayToWebPage("steam://url/SteamWorkshopPage/108600");
			}
		}

		@LuaMethod(name = "activateSteamOverlayToWorkshopUser", global = true)
		public static void activateSteamOverlayToWorkshopUser() {
			if (SteamUtils.isOverlayEnabled()) {
				SteamFriends.ActivateGameOverlayToWebPage("steam://url/SteamIDCommunityFilesPage/" + SteamUser.GetSteamIDString() + "/108600");
			}
		}

		@LuaMethod(name = "activateSteamOverlayToWorkshopItem", global = true)
		public static void activateSteamOverlayToWorkshopItem(String string) {
			if (SteamUtils.isOverlayEnabled() && SteamUtils.isValidSteamID(string)) {
				SteamFriends.ActivateGameOverlayToWebPage("steam://url/CommunityFilePage/" + string);
			}
		}

		@LuaMethod(name = "activateSteamOverlayToWebPage", global = true)
		public static void activateSteamOverlayToWebPage(String string) {
			if (SteamUtils.isOverlayEnabled()) {
				SteamFriends.ActivateGameOverlayToWebPage(string);
			}
		}

		@LuaMethod(name = "getSteamProfileNameFromSteamID", global = true)
		public static String getSteamProfileNameFromSteamID(String string) {
			if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
				long long1 = SteamUtils.convertStringToSteamID(string);
				if (long1 != -1L) {
					return SteamFriends.GetFriendPersonaName(long1);
				}
			}

			return null;
		}

		@LuaMethod(name = "getSteamAvatarFromSteamID", global = true)
		public static Texture getSteamAvatarFromSteamID(String string) {
			if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
				long long1 = SteamUtils.convertStringToSteamID(string);
				if (long1 != -1L) {
					return Texture.getSteamAvatar(long1);
				}
			}

			return null;
		}

		@LuaMethod(name = "getSteamIDFromUsername", global = true)
		public static String getSteamIDFromUsername(String string) {
			if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
				IsoPlayer player = GameClient.instance.getPlayerFromUsername(string);
				if (player != null) {
					return SteamUtils.convertSteamIDToString(player.getSteamID());
				}
			}

			return null;
		}

		@LuaMethod(name = "resetRegionFile", global = true)
		public static void resetRegionFile() {
			ServerOptions.getInstance().resetRegionFile();
		}

		@LuaMethod(name = "getSteamProfileNameFromUsername", global = true)
		public static String getSteamProfileNameFromUsername(String string) {
			if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
				IsoPlayer player = GameClient.instance.getPlayerFromUsername(string);
				if (player != null) {
					return SteamFriends.GetFriendPersonaName(player.getSteamID());
				}
			}

			return null;
		}

		@LuaMethod(name = "getSteamAvatarFromUsername", global = true)
		public static Texture getSteamAvatarFromUsername(String string) {
			if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
				IsoPlayer player = GameClient.instance.getPlayerFromUsername(string);
				if (player != null) {
					return Texture.getSteamAvatar(player.getSteamID());
				}
			}

			return null;
		}

		@LuaMethod(name = "getSteamWorkshopStagedItems", global = true)
		public static ArrayList getSteamWorkshopStagedItems() {
			return SteamUtils.isSteamModeEnabled() ? SteamWorkshop.instance.loadStagedItems() : null;
		}

		@LuaMethod(name = "getSteamWorkshopItemIDs", global = true)
		public static ArrayList getSteamWorkshopItemIDs() {
			if (SteamUtils.isSteamModeEnabled()) {
				ArrayList arrayList = new ArrayList();
				String[] stringArray = SteamWorkshop.instance.GetInstalledItemFolders();
				if (stringArray == null) {
					return arrayList;
				} else {
					for (int int1 = 0; int1 < stringArray.length; ++int1) {
						String string = SteamWorkshop.instance.getIDFromItemInstallFolder(stringArray[int1]);
						if (string != null) {
							arrayList.add(string);
						}
					}

					return arrayList;
				}
			} else {
				return null;
			}
		}

		@LuaMethod(name = "getSteamWorkshopItemMods", global = true)
		public static ArrayList getSteamWorkshopItemMods(String string) {
			if (SteamUtils.isSteamModeEnabled()) {
				long long1 = SteamUtils.convertStringToSteamID(string);
				if (long1 > 0L) {
					return ZomboidFileSystem.instance.getWorkshopItemMods(long1);
				}
			}

			return null;
		}

		@LuaMethod(name = "sendPlayerStatsChange", global = true)
		public static void sendPlayerStatsChange(IsoPlayer player) {
			if (GameClient.bClient) {
				GameClient.instance.sendChangedPlayerStats(player);
			}
		}

		@LuaMethod(name = "sendPersonalColor", global = true)
		public static void sendPersonalColor(IsoPlayer player) {
			if (GameClient.bClient) {
				GameClient.instance.sendPersonalColor(player);
			}
		}

		@LuaMethod(name = "requestTrading", global = true)
		public static void requestTrading(IsoPlayer player, IsoPlayer player2) {
			GameClient.instance.requestTrading(player, player2);
		}

		@LuaMethod(name = "acceptTrading", global = true)
		public static void acceptTrading(IsoPlayer player, IsoPlayer player2, boolean boolean1) {
			GameClient.instance.acceptTrading(player, player2, boolean1);
		}

		@LuaMethod(name = "tradingUISendAddItem", global = true)
		public static void tradingUISendAddItem(IsoPlayer player, IsoPlayer player2, InventoryItem inventoryItem) {
			GameClient.instance.tradingUISendAddItem(player, player2, inventoryItem);
		}

		@LuaMethod(name = "tradingUISendRemoveItem", global = true)
		public static void tradingUISendRemoveItem(IsoPlayer player, IsoPlayer player2, int int1) {
			GameClient.instance.tradingUISendRemoveItem(player, player2, int1);
		}

		@LuaMethod(name = "tradingUISendUpdateState", global = true)
		public static void tradingUISendUpdateState(IsoPlayer player, IsoPlayer player2, int int1) {
			GameClient.instance.tradingUISendUpdateState(player, player2, int1);
		}

		@LuaMethod(name = "querySteamWorkshopItemDetails", global = true)
		public static void querySteamWorkshopItemDetails(ArrayList arrayList, LuaClosure luaClosure, Object object) {
			if (arrayList != null && luaClosure != null) {
				if (arrayList.isEmpty()) {
					if (object == null) {
						LuaManager.caller.pcall(LuaManager.thread, luaClosure, new Object[]{"Completed", new ArrayList()});
					} else {
						LuaManager.caller.pcall(LuaManager.thread, luaClosure, new Object[]{object, "Completed", new ArrayList()});
					}
				} else {
					new LuaManager.GlobalObject.ItemQuery(arrayList, luaClosure, object);
				}
			} else {
				throw new NullPointerException();
			}
		}

		@LuaMethod(name = "connectToServerStateCallback", global = true)
		public static void connectToServerStateCallback(String string) {
			if (ConnectToServerState.instance != null) {
				ConnectToServerState.instance.FromLua(string);
			}
		}

		@LuaMethod(name = "getPublicServersList", global = true)
		public static KahluaTable getPublicServersList() {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			if (!SteamUtils.isSteamModeEnabled() && !PublicServerUtil.isEnabled()) {
				return kahluaTable;
			} else if (System.currentTimeMillis() - timeLastRefresh < 60000L) {
				return kahluaTable;
			} else {
				ArrayList arrayList = new ArrayList();
				try {
					Server server;
					if (getSteamModeActive()) {
						ServerBrowser.RefreshInternetServers();
						List list = ServerBrowser.GetServerList();
						Iterator iterator = list.iterator();
						while (iterator.hasNext()) {
							GameServerDetails gameServerDetails = (GameServerDetails)iterator.next();
							server = new Server();
							server.setName(gameServerDetails.name);
							server.setDescription(gameServerDetails.gameDescription);
							server.setSteamId(Long.toString(gameServerDetails.steamId));
							server.setPing(Integer.toString(gameServerDetails.ping));
							server.setPlayers(Integer.toString(gameServerDetails.numPlayers));
							server.setMaxPlayers(Integer.toString(gameServerDetails.maxPlayers));
							server.setOpen(true);
							server.setIp(gameServerDetails.address);
							server.setPort(Integer.toString(gameServerDetails.port));
							server.setMods(gameServerDetails.tags);
							server.setVersion(Core.getInstance().getVersionNumber());
							server.setLastUpdate(1);
							arrayList.add(server);
						}

						System.out.printf("%d servers\n", list.size());
					} else {
						URL url = new URL(PublicServerUtil.webSite + "servers.xml");
						InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
						BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
						server = null;
						StringBuffer stringBuffer = new StringBuffer();
						String string;
						while ((string = bufferedReader.readLine()) != null) {
							stringBuffer.append(string).append('\n');
						}

						bufferedReader.close();
						DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
						DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
						Document document = documentBuilder.parse(new InputSource(new StringReader(stringBuffer.toString())));
						document.getDocumentElement().normalize();
						NodeList nodeList = document.getElementsByTagName("server");
						for (int int1 = 0; int1 < nodeList.getLength(); ++int1) {
							Node node = nodeList.item(int1);
							if (node.getNodeType() == 1) {
								Element element = (Element)node;
								Server server2 = new Server();
								server2.setName(element.getElementsByTagName("name").item(0).getTextContent());
								if (element.getElementsByTagName("desc").item(0) != null && !"".equals(element.getElementsByTagName("desc").item(0).getTextContent())) {
									server2.setDescription(element.getElementsByTagName("desc").item(0).getTextContent());
								}

								server2.setIp(element.getElementsByTagName("ip").item(0).getTextContent());
								server2.setPort(element.getElementsByTagName("port").item(0).getTextContent());
								server2.setPlayers(element.getElementsByTagName("players").item(0).getTextContent());
								server2.setMaxPlayers(element.getElementsByTagName("maxPlayers").item(0).getTextContent());
								if (element.getElementsByTagName("version") != null && element.getElementsByTagName("version").item(0) != null) {
									server2.setVersion(element.getElementsByTagName("version").item(0).getTextContent());
								}

								server2.setOpen(element.getElementsByTagName("open").item(0).getTextContent().equals("1"));
								Integer integer = Integer.parseInt(element.getElementsByTagName("lastUpdate").item(0).getTextContent());
								if (element.getElementsByTagName("mods").item(0) != null && !"".equals(element.getElementsByTagName("mods").item(0).getTextContent())) {
									server2.setMods(element.getElementsByTagName("mods").item(0).getTextContent());
								}

								server2.setLastUpdate((new Double(Math.floor((double)((getTimestamp() - (long)integer) / 60L)))).intValue());
								NodeList nodeList2 = element.getElementsByTagName("password");
								server2.setPasswordProtected(nodeList2 != null && nodeList2.getLength() != 0 && nodeList2.item(0).getTextContent().equals("1"));
								arrayList.add(server2);
							}
						}
					}

					int int2 = 1;
					for (int int3 = 0; int3 < arrayList.size(); ++int3) {
						Server server3 = (Server)arrayList.get(int3);
						Double Double1 = (double)int2;
						kahluaTable.rawset(Double1, server3);
						++int2;
					}

					timeLastRefresh = Calendar.getInstance().getTimeInMillis();
					return kahluaTable;
				} catch (Exception exception) {
					exception.printStackTrace();
					return null;
				}
			}
		}

		@LuaMethod(name = "steamRequestInternetServersList", global = true)
		public static void steamRequestInternetServersList() {
			ServerBrowser.RefreshInternetServers();
		}

		@LuaMethod(name = "steamReleaseInternetServersRequest", global = true)
		public static void steamReleaseInternetServersRequest() {
			ServerBrowser.Release();
		}

		@LuaMethod(name = "steamGetInternetServersCount", global = true)
		public static int steamRequestInternetServersCount() {
			return ServerBrowser.GetServerCount();
		}

		@LuaMethod(name = "steamGetInternetServerDetails", global = true)
		public static Server steamGetInternetServerDetails(int int1) {
			if (!ServerBrowser.IsRefreshing()) {
				return null;
			} else {
				GameServerDetails gameServerDetails = ServerBrowser.GetServerDetails(int1);
				if (gameServerDetails == null) {
					return null;
				} else {
					Server server = new Server();
					server.setName(gameServerDetails.name);
					server.setDescription("");
					server.setSteamId(Long.toString(gameServerDetails.steamId));
					server.setPing(Integer.toString(gameServerDetails.ping));
					server.setPlayers(Integer.toString(gameServerDetails.numPlayers));
					server.setMaxPlayers(Integer.toString(gameServerDetails.maxPlayers));
					server.setOpen(true);
					server.setPublic(true);
					server.setIp(gameServerDetails.address);
					server.setPort(Integer.toString(gameServerDetails.port));
					server.setMods(gameServerDetails.tags.replace(";hosted", "").replace("hidden", ""));
					server.setHosted(gameServerDetails.tags.endsWith(";hosted"));
					server.setVersion("");
					server.setLastUpdate(1);
					server.setPasswordProtected(gameServerDetails.passwordProtected);
					return server;
				}
			}
		}

		@LuaMethod(name = "steamRequestServerRules", global = true)
		public static boolean steamRequestServerRules(String string, int int1) {
			return ServerBrowser.RequestServerRules(string, int1);
		}

		@LuaMethod(name = "steamRequestServerDetails", global = true)
		public static boolean steamRequestServerDetails(String string, int int1) {
			return ServerBrowser.QueryServer(string, int1);
		}

		@LuaMethod(name = "isPublicServerListAllowed", global = true)
		public static boolean isPublicServerListAllowed() {
			return SteamUtils.isSteamModeEnabled() ? true : PublicServerUtil.isEnabled();
		}

		@LuaMethod(name = "is64bit", global = true)
		public static boolean is64bit() {
			return "64".equals(System.getProperty("sun.arch.data.model"));
		}

		@LuaMethod(name = "testSound", global = true)
		public static void testSound() {
			float float1 = (float)org.lwjgl.input.Mouse.getX() * Core.getInstance().getZoom(0);
			float float2 = (float)org.lwjgl.input.Mouse.getY() * Core.getInstance().getZoom(0);
			int int1 = (int)IsoPlayer.instance.getZ();
			int int2 = (int)IsoUtils.XToIso(float1 - 30.0F, (float)Core.getInstance().getOffscreenHeight(0) - float2 - 366.0F, (float)int1);
			int int3 = (int)IsoUtils.YToIso(float1 - 30.0F, (float)Core.getInstance().getOffscreenHeight(0) - float2 - 366.0F, (float)int1);
			float float3 = 50.0F;
			float float4 = 1.0F;
			AmbientStreamManager.Ambient ambient = (AmbientStreamManager)AmbientStreamManager.instance.new Ambient("burglar2", (float)int2, (float)int3, float3, float4);
			ambient.trackMouse = true;
			((AmbientStreamManager)AmbientStreamManager.instance).ambient.add(ambient);
		}

		@LuaMethod(name = "copyTable", global = true)
		public static KahluaTable copyTable(KahluaTable kahluaTable) {
			return LuaManager.copyTable(kahluaTable);
		}

		@LuaMethod(name = "getUrlInputStream", global = true)
		public static DataInputStream getUrlInputStream(String string) {
			if (string != null && (string.startsWith("https://") || string.startsWith("http://"))) {
				try {
					return new DataInputStream((new URL(string)).openStream());
				} catch (IOException ioException) {
					ioException.printStackTrace();
					return null;
				}
			} else {
				return null;
			}
		}

		@LuaMethod(name = "renderIsoCircle", global = true)
		public static void renderIsoCircle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, int int1) {
			double double1 = 0.3490658503988659;
			for (double double2 = 0.0; double2 < 6.283185307179586; double2 += double1) {
				float float9 = float1 + float4 * (float)Math.cos(double2);
				float float10 = float2 + float4 * (float)Math.sin(double2);
				float float11 = float1 + float4 * (float)Math.cos(double2 + double1);
				float float12 = float2 + float4 * (float)Math.sin(double2 + double1);
				float float13 = IsoUtils.XToScreenExact(float9, float10, float3, 0);
				float float14 = IsoUtils.YToScreenExact(float9, float10, float3, 0);
				float float15 = IsoUtils.XToScreenExact(float11, float12, float3, 0);
				float float16 = IsoUtils.YToScreenExact(float11, float12, float3, 0);
				LineDrawer.drawLine(float13, float14, float15, float16, float5, float6, float7, float8, int1);
			}
		}

		@LuaMethod(name = "configureLighting", global = true)
		public static void configureLighting(float float1) {
			if (LightingJNI.init) {
				LightingJNI.configure(float1);
			}
		}

		@LuaMethod(name = "testHelicopter", global = true)
		public static void testHelicopter() {
			IsoWorld.instance.helicopter.pickRandomTarget();
		}

		@LuaMethod(name = "getServerSettingsManager", global = true)
		public static ServerSettingsManager getServerSettingsManager() {
			return ServerSettingsManager.instance;
		}

		@LuaMethod(name = "rainConfig", global = true)
		public static void rainConfig(String string, int int1) {
			if ("alpha".equals(string)) {
				IsoWorld.instance.CurrentCell.setRainAlpha(int1);
			}

			if ("intensity".equals(string)) {
				IsoWorld.instance.CurrentCell.setRainIntensity(int1);
			}

			if ("speed".equals(string)) {
				IsoWorld.instance.CurrentCell.setRainSpeed(int1);
			}

			if ("reloadTextures".equals(string)) {
				IsoWorld.instance.CurrentCell.reloadRainTextures();
			}
		}

		@LuaMethod(name = "getVehicleById", global = true)
		public static BaseVehicle getVehicleById(int int1) {
			return GameServer.bServer ? VehicleManager.instance.getVehicleByID((short)int1) : VehicleManager.instance.getVehicleByID((short)int1);
		}

		@LuaMethod(name = "addCarCrash", global = true)
		public static void addCarCrash() {
			IsoGridSquare square = IsoPlayer.instance.getCurrentSquare();
			if (square != null) {
				IsoChunk chunk = square.getChunk();
				if (chunk != null) {
					IsoMetaGrid.Zone zone = square.getZone();
					if (zone != null) {
						if (chunk.canAddRandomCarCrash(zone, true)) {
							square.chunk.addRandomCarCrash(zone, true);
						}
					}
				}
			}
		}

		@LuaMethod(name = "addVehicle", global = true)
		public static BaseVehicle addVehicle(String string) {
			if (string != null && !string.isEmpty() && ScriptManager.instance.getVehicle(string) == null) {
				DebugLog.log("No such vehicle script \"" + string + "\"");
				return null;
			} else {
				WorldSimulation.instance.create();
				BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
				switch (Rand.Next(24)) {
				case 0: 
					baseVehicle.setScriptName("Base.PickUpVanLights");
					break;
				
				case 1: 
					baseVehicle.setScriptName("Base.PickUpVan");
					break;
				
				case 2: 
					baseVehicle.setScriptName("Base.PickUpTruckLights");
					break;
				
				case 3: 
					baseVehicle.setScriptName("Base.PickUpTruck");
					break;
				
				case 4: 
					baseVehicle.setScriptName("Base.PickupBurnt");
					break;
				
				case 5: 
					baseVehicle.setScriptName("Base.CarNormal");
					break;
				
				case 6: 
					baseVehicle.setScriptName("Base.CarStationWagon");
					break;
				
				case 7: 
					baseVehicle.setScriptName("Base.CarLights");
					break;
				
				case 8: 
					baseVehicle.setScriptName("Base.CarNormalBurnt");
					break;
				
				case 9: 
					baseVehicle.setScriptName("Base.NormalCarBurntPolice");
					break;
				
				case 10: 
					baseVehicle.setScriptName("Base.Van");
					break;
				
				case 11: 
					baseVehicle.setScriptName("Base.VanSeats");
					break;
				
				case 12: 
					baseVehicle.setScriptName("Base.VanAmbulance");
					break;
				
				case 13: 
					baseVehicle.setScriptName("Base.VanRadio");
					break;
				
				case 14: 
					baseVehicle.setScriptName("Base.StepVan");
					break;
				
				case 15: 
					baseVehicle.setScriptName("Base.AmbulanceBurnt");
					break;
				
				case 16: 
					baseVehicle.setScriptName("Base.VanRadioBurnt");
					break;
				
				case 17: 
					baseVehicle.setScriptName("Base.VanSeatsBurnt");
					break;
				
				case 18: 
					baseVehicle.setScriptName("Base.VanBurnt");
					break;
				
				case 19: 
					baseVehicle.setScriptName("Base.SportsCar");
					break;
				
				case 20: 
					baseVehicle.setScriptName("Base.SportsCarBurnt");
					break;
				
				case 21: 
					baseVehicle.setScriptName("Base.SmallCar");
					break;
				
				case 22: 
					baseVehicle.setScriptName("Base.SmallCarBurnt");
					break;
				
				case 23: 
					baseVehicle.setScriptName("Base.VanSpecial");
				
				}

				if (string != null && !string.isEmpty()) {
					baseVehicle.setScriptName(string);
				}

				baseVehicle.setX(IsoPlayer.instance.getX());
				baseVehicle.setY(IsoPlayer.instance.getY());
				baseVehicle.setZ(IsoPlayer.instance.getZ() + 2.0F);
				if (IsoChunk.doSpawnedVehiclesInInvalidPosition(baseVehicle)) {
					baseVehicle.setSquare(IsoPlayer.instance.getSquare());
					baseVehicle.square.chunk.vehicles.add(baseVehicle);
					baseVehicle.chunk = baseVehicle.square.chunk;
					baseVehicle.addToWorld();
				} else {
					DebugLog.log("ERROR: I can not spawn the vehicle. Invalid position. Try to change position.");
				}

				return null;
			}
		}

		@LuaMethod(name = "addAllVehicles", global = true)
		public static void addAllVehicles() {
			ArrayList arrayList = ScriptManager.instance.getAllVehicleScripts();
			float float1 = (float)(IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMinTiles() + 5);
			float float2 = IsoPlayer.instance.getY();
			float float3 = 0.0F;
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				VehicleScript vehicleScript = (VehicleScript)arrayList.get(int1);
				if (vehicleScript.getWheelCount() != 0 && vehicleScript.getModel() != null) {
					WorldSimulation.instance.create();
					BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
					baseVehicle.setScriptName(vehicleScript.getFullName());
					baseVehicle.setX(float1);
					baseVehicle.setY(float2);
					baseVehicle.setZ(float3);
					if (IsoChunk.doSpawnedVehiclesInInvalidPosition(baseVehicle)) {
						baseVehicle.setSquare(IsoPlayer.instance.getSquare());
						baseVehicle.square.chunk.vehicles.add(baseVehicle);
						baseVehicle.chunk = baseVehicle.square.chunk;
						baseVehicle.addToWorld();
						IsoChunk.addFromCheckedVehicles(baseVehicle);
					} else {
						DebugLog.log(vehicleScript.getName() + " not spawned, position invalid");
					}

					float1 += 4.0F;
				}
			}
		}

		@LuaMethod(name = "addPhysicsObject", global = true)
		public static BaseVehicle addPhysicsObject() {
			int int1 = Bullet.addPhysicsObject(getPlayer().getX(), getPlayer().getY());
			IsoPushableObject pushableObject = new IsoPushableObject(IsoWorld.instance.getCell(), IsoPlayer.instance.getCurrentSquare(), IsoWorld.instance.getCell().getSpriteManager().getSprite("trashcontainers_01_16"));
			WorldSimulation.instance.physicsObjectMap.put(int1, pushableObject);
			return null;
		}

		@LuaMethod(name = "reloadSoundFiles", global = true)
		public static void reloadSoundFiles() {
			try {
				Iterator iterator = ZomboidFileSystem.instance.ActiveFileMap.keySet().iterator();
				while (iterator.hasNext()) {
					String string = (String)iterator.next();
					if (string.matches(".*/sounds_.+\\.txt")) {
						GameSounds.ReloadFile(string);
					}
				}
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
			}
		}

		@LuaMethod(name = "reloadVehicles", global = true)
		public static void reloadVehicles() {
			try {
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_battery.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_brake.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_door.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_engine.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_engine_door.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_gastank.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_glovebox.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_headlight.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_heater.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_passenger.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_passenger_compartment.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_radio.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_seat.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_suspension.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_tire.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_trunk.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_trunk_door.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_window.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/template_windshield.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/vehicles.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/carnormal.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/vans.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/smallcar.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/sportcar.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/burntvehicles.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/vehicle_carluxury.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/vehicle_carmodern.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/vehicle_carmodern02.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/vehicle_offroad.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/vehicle_smallcar02.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/vehicle_suv.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/vehicle_taxi.txt", true);
				ScriptManager.instance.LoadFile("media/scripts/vehicles/vehicle_taxi2.txt", true);
				BaseVehicle.LoadAllVehicleTextures();
				Iterator iterator = IsoWorld.instance.CurrentCell.vehicles.iterator();
				while (iterator.hasNext()) {
					BaseVehicle baseVehicle = (BaseVehicle)iterator.next();
					baseVehicle.scriptReloaded();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		@LuaMethod(name = "proceedPM", global = true)
		public static String proceedPM(String string) {
			string = string.trim();
			String string2 = null;
			String string3 = null;
			Matcher matcher = Pattern.compile("(\"[^\"]*\\s+[^\"]*\"|[^\"]\\S*)\\s(.+)").matcher(string);
			if (matcher.matches()) {
				string2 = matcher.group(1);
				string3 = matcher.group(2);
				String string4 = string2.replaceAll("\"", "");
				(new Thread(()->{
					ChatManager.getInstance().sendWhisperMessage(string4, string3);
				})).start();

				return string2;
			} else if (Core.bDebug) {
				throw new RuntimeException();
			} else {
				return "";
			}
		}

		@LuaMethod(name = "processSayMessage", global = true)
		public static void processSayMessage(String string) {
			if (string != null && !string.isEmpty()) {
				string = string.trim();
				ChatManager.getInstance().sendMessageToChat(ChatType.say, string);
			}
		}

		@LuaMethod(name = "processGeneralMessage", global = true)
		public static void processGeneralMessage(String string) {
			if (string != null && !string.isEmpty()) {
				string = string.trim();
				ChatManager.getInstance().sendMessageToChat(ChatType.general, string);
			}
		}

		@LuaMethod(name = "processShoutMessage", global = true)
		public static void processShoutMessage(String string) {
			if (string != null && !string.isEmpty()) {
				string = string.trim();
				ChatManager.getInstance().sendMessageToChat(ChatType.shout, string);
			}
		}

		@LuaMethod(name = "proceedFactionMessage", global = true)
		public static void ProceedFactionMessage(String string) {
			if (string != null && !string.isEmpty()) {
				string = string.trim();
				ChatManager.getInstance().sendMessageToChat(ChatType.faction, string);
			}
		}

		@LuaMethod(name = "processSafehouseMessage", global = true)
		public static void ProcessSafehouseMessage(String string) {
			if (string != null && !string.isEmpty()) {
				string = string.trim();
				ChatManager.getInstance().sendMessageToChat(ChatType.safehouse, string);
			}
		}

		@LuaMethod(name = "processAdminChatMessage", global = true)
		public static void ProcessAdminChatMessage(String string) {
			if (string != null && !string.isEmpty()) {
				string = string.trim();
				ChatManager.getInstance().sendMessageToChat(ChatType.admin, string);
			}
		}

		@LuaMethod(name = "showWrongChatTabMessage", global = true)
		public static void showWrongChatTabMessage(int int1, int int2, String string) {
			String string2 = ChatManager.getInstance().getTabName((short)int1);
			String string3 = ChatManager.getInstance().getTabName((short)int2);
			String string4 = Translator.getText("UI_chat_wrong_tab", string2, string3, string);
			ChatManager.getInstance().showServerChatMessage(string4);
		}

		@LuaMethod(name = "focusOnTab", global = true)
		public static void focusOnTab(Short Short1) {
			ChatManager.getInstance().focusOnTab(Short1);
		}

		@LuaMethod(name = "updateChatSettings", global = true)
		public static void updateChatSettings(String string, boolean boolean1, boolean boolean2) {
			ChatManager.getInstance().updateChatSettings(string, boolean1, boolean2);
		}

		@LuaMethod(name = "checkPlayerCanUseChat", global = true)
		public static Boolean checkPlayerCanUseChat(String string) {
			string = string.trim();
			byte byte1 = -1;
			switch (string.hashCode()) {
			case -1769046940: 
				if (string.equals("/safehouse")) {
					byte1 = 10;
				}

				break;
			
			case -784181491: 
				if (string.equals("/faction")) {
					byte1 = 8;
				}

				break;
			
			case 1554: 
				if (string.equals("/a")) {
					byte1 = 1;
				}

				break;
			
			case 1559: 
				if (string.equals("/f")) {
					byte1 = 7;
				}

				break;
			
			case 1571: 
				if (string.equals("/r")) {
					byte1 = 14;
				}

				break;
			
			case 1572: 
				if (string.equals("/s")) {
					byte1 = 3;
				}

				break;
			
			case 1576: 
				if (string.equals("/w")) {
					byte1 = 11;
				}

				break;
			
			case 1578: 
				if (string.equals("/y")) {
					byte1 = 5;
				}

				break;
			
			case 48836: 
				if (string.equals("/sh")) {
					byte1 = 9;
				}

				break;
			
			case 1496850: 
				if (string.equals("/all")) {
					byte1 = 0;
				}

				break;
			
			case 1513820: 
				if (string.equals("/say")) {
					byte1 = 4;
				}

				break;
			
			case 47110715: 
				if (string.equals("/yell")) {
					byte1 = 6;
				}

				break;
			
			case 1438238848: 
				if (string.equals("/admin")) {
					byte1 = 2;
				}

				break;
			
			case 1453840684: 
				if (string.equals("/radio")) {
					byte1 = 13;
				}

				break;
			
			case 1624401011: 
				if (string.equals("/whisper")) {
					byte1 = 12;
				}

			
			}
			ChatType chatType;
			switch (byte1) {
			case 0: 
				chatType = ChatType.general;
				break;
			
			case 1: 
			
			case 2: 
				chatType = ChatType.admin;
				break;
			
			case 3: 
			
			case 4: 
				chatType = ChatType.say;
				break;
			
			case 5: 
			
			case 6: 
				chatType = ChatType.shout;
				break;
			
			case 7: 
			
			case 8: 
				chatType = ChatType.faction;
				break;
			
			case 9: 
			
			case 10: 
				chatType = ChatType.safehouse;
				break;
			
			case 11: 
			
			case 12: 
				chatType = ChatType.whisper;
				break;
			
			case 13: 
			
			case 14: 
				chatType = ChatType.radio;
				break;
			
			default: 
				chatType = ChatType.notDefined;
				DebugLog.log("Chat command not found");
			
			}
			return ChatManager.getInstance().isPlayerCanUseChat(chatType);
		}

		@LuaMethod(name = "reloadVehicleTextures", global = true)
		public static void reloadVehicleTextures(String string) {
			VehicleScript vehicleScript = ScriptManager.instance.getVehicle(string);
			if (vehicleScript == null) {
				DebugLog.log("no such vehicle script");
			} else {
				for (int int1 = 0; int1 < vehicleScript.getSkinCount(); ++int1) {
					VehicleScript.Skin skin = vehicleScript.getSkin(int1);
					if (skin != null && skin.texture != null) {
						Texture.reload("media/textures/" + skin.texture + ".png");
					}
				}

				if (vehicleScript.textureRust != null) {
					Texture.reload("media/textures/" + vehicleScript.textureRust + ".png");
				}

				if (vehicleScript.textureMask != null) {
					Texture.reload("media/textures/" + vehicleScript.textureMask + ".png");
				}

				if (vehicleScript.textureLights != null) {
					Texture.reload("media/textures/" + vehicleScript.textureLights + ".png");
				}

				if (vehicleScript.textureDamage1Overlay != null) {
					Texture.reload("media/textures/" + vehicleScript.textureDamage1Overlay + ".png");
				}

				if (vehicleScript.textureDamage1Shell != null) {
					Texture.reload("media/textures/" + vehicleScript.textureDamage1Shell + ".png");
				}

				if (vehicleScript.textureDamage2Overlay != null) {
					Texture.reload("media/textures/" + vehicleScript.textureDamage2Overlay + ".png");
				}

				if (vehicleScript.textureDamage2Shell != null) {
					Texture.reload("media/textures/" + vehicleScript.textureDamage2Shell + ".png");
				}
			}
		}

		@LuaMethod(name = "getClimateManager", global = true)
		public static ClimateManager getClimateManager() {
			return ClimateManager.getInstance();
		}

		@LuaMethod(name = "setVehicleModelCameraHack", global = true)
		public static void setVehicleModelCameraHack(float float1) {
			ModelCamera.instance.VehicleScaleHack = float1;
		}

		@LuaMethod(name = "getErosion", global = true)
		public static void getErosion() {
			ErosionMain.getInstance();
		}

		private static final class ItemQuery implements ISteamWorkshopCallback {
			private LuaClosure functionObj;
			private Object arg1;
			private long handle;

			public ItemQuery(ArrayList arrayList, LuaClosure luaClosure, Object object) {
				this.functionObj = luaClosure;
				this.arg1 = object;
				long[] longArray = new long[arrayList.size()];
				int int1 = 0;
				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					long long1 = SteamUtils.convertStringToSteamID((String)arrayList.get(int2));
					if (long1 != -1L) {
						longArray[int1++] = long1;
					}
				}

				this.handle = SteamWorkshop.instance.CreateQueryUGCDetailsRequest(longArray, this);
				if (this.handle == 0L) {
					SteamWorkshop.instance.RemoveCallback(this);
					if (object == null) {
						LuaManager.caller.pcall(LuaManager.thread, luaClosure, (Object)"NotCompleted");
					} else {
						LuaManager.caller.pcall(LuaManager.thread, luaClosure, new Object[]{object, "NotCompleted"});
					}
				}
			}

			public void onItemCreated(long long1, boolean boolean1) {
			}

			public void onItemNotCreated(int int1) {
			}

			public void onItemUpdated(boolean boolean1) {
			}

			public void onItemNotUpdated(int int1) {
			}

			public void onItemSubscribed(long long1) {
			}

			public void onItemNotSubscribed(long long1, int int1) {
			}

			public void onItemDownloaded(long long1) {
			}

			public void onItemNotDownloaded(long long1, int int1) {
			}

			public void onItemQueryCompleted(long long1, int int1) {
				if (long1 == this.handle) {
					SteamWorkshop.instance.RemoveCallback(this);
					ArrayList arrayList = new ArrayList();
					for (int int2 = 0; int2 < int1; ++int2) {
						SteamUGCDetails steamUGCDetails = SteamWorkshop.instance.GetQueryUGCResult(long1, int2);
						if (steamUGCDetails != null) {
							arrayList.add(steamUGCDetails);
						}
					}

					SteamWorkshop.instance.ReleaseQueryUGCRequest(long1);
					if (this.arg1 == null) {
						LuaManager.caller.pcall(LuaManager.thread, this.functionObj, new Object[]{"Completed", arrayList});
					} else {
						LuaManager.caller.pcall(LuaManager.thread, this.functionObj, new Object[]{this.arg1, "Completed", arrayList});
					}
				}
			}

			public void onItemQueryNotCompleted(long long1, int int1) {
				if (long1 == this.handle) {
					SteamWorkshop.instance.RemoveCallback(this);
					SteamWorkshop.instance.ReleaseQueryUGCRequest(long1);
					if (this.arg1 == null) {
						LuaManager.caller.pcall(LuaManager.thread, this.functionObj, (Object)"NotCompleted");
					} else {
						LuaManager.caller.pcall(LuaManager.thread, this.functionObj, new Object[]{this.arg1, "NotCompleted"});
					}
				}
			}
		}

		public static class LuaFileWriter {
			private FileWriter writer;

			public void write(String string) throws IOException {
				this.writer.write(string);
			}

			public void close() throws IOException {
				this.writer.close();
			}
		}
	}

	private static class Exposer extends LuaJavaClassExposer {
		private HashSet exposed = new HashSet();

		public Exposer(KahluaConverterManager kahluaConverterManager, Platform platform, KahluaTable kahluaTable) {
			super(kahluaConverterManager, platform, kahluaTable);
		}

		public void exposeAll() {
			this.setExposed(BufferedReader.class);
			this.setExposed(BufferedWriter.class);
			this.setExposed(DataInputStream.class);
			this.setExposed(DataOutputStream.class);
			this.setExposed(Double.class);
			this.setExposed(Long.class);
			this.setExposed(Float.class);
			this.setExposed(Integer.class);
			this.setExposed(Math.class);
			this.setExposed(Void.class);
			this.setExposed(SimpleDateFormat.class);
			this.setExposed(ArrayList.class);
			this.setExposed(Calendar.class);
			this.setExposed(EnumMap.class);
			this.setExposed(GregorianCalendar.class);
			this.setExposed(HashMap.class);
			this.setExposed(LinkedList.class);
			this.setExposed(Stack.class);
			this.setExposed(Vector.class);
			this.setExposed(Iterator.class);
			this.setExposed(FMODAudio.class);
			this.setExposed(FMODSoundBank.class);
			this.setExposed(FMODSoundEmitter.class);
			this.setExposed(Vector3f.class);
			this.setExposed(KahluaUtil.class);
			this.setExposed(BuddhistCalendar.class);
			this.setExposed(DummySoundBank.class);
			this.setExposed(DummySoundEmitter.class);
			this.setExposed(BaseSoundEmitter.class);
			this.setExposed(GameSound.class);
			this.setExposed(GameSoundClip.class);
			this.setExposed(Behavior.class);
			this.setExposed(Behavior.BehaviorResult.class);
			this.setExposed(PathFindBehavior.class);
			this.setExposed(AttackState.class);
			this.setExposed(BurntToDeath.class);
			this.setExposed(ClimbDownSheetRopeState.class);
			this.setExposed(ClimbOverFenceState.class);
			this.setExposed(ClimbOverFenceState2.class);
			this.setExposed(ClimbSheetRopeState.class);
			this.setExposed(ClimbThroughWindowState.class);
			this.setExposed(ClimbThroughWindowState2.class);
			this.setExposed(CrawlingZombieTurnState.class);
			this.setExposed(DieState.class);
			this.setExposed(FakeDeadZombieState.class);
			this.setExposed(IdleState.class);
			this.setExposed(JustDieState.class);
			this.setExposed(LuaState.class);
			this.setExposed(LungeState.class);
			this.setExposed(OpenWindowState.class);
			this.setExposed(PathFindState.class);
			this.setExposed(PlayerControlState.class);
			this.setExposed(ReanimatePlayerState.class);
			this.setExposed(ReanimateState.class);
			this.setExposed(SatChairState.class);
			this.setExposed(SatChairStateOut.class);
			this.setExposed(StaggerBackDieState.class);
			this.setExposed(StaggerBackState.class);
			this.setExposed(SwipeState.class);
			this.setExposed(SwipeStatePlayer.class);
			this.setExposed(ThumpState.class);
			this.setExposed(WalkTowardState.class);
			this.setExposed(WanderState.class);
			this.setExposed(ZombieStandState.class);
			this.setExposed(BodyPartType.class);
			this.setExposed(BodyPart.class);
			this.setExposed(BodyDamage.class);
			this.setExposed(GameKeyboard.class);
			this.setExposed(EmitterType.class);
			this.setExposed(LuaTimedAction.class);
			this.setExposed(LuaTimedActionNew.class);
			this.setExposed(Moodle.class);
			this.setExposed(Moodles.class);
			this.setExposed(MoodleType.class);
			this.setExposed(ProfessionFactory.class);
			this.setExposed(ProfessionFactory.Profession.class);
			this.setExposed(PerkFactory.class);
			this.setExposed(PerkFactory.Perk.class);
			this.setExposed(PerkFactory.Perks.class);
			this.setExposed(ObservationFactory.class);
			this.setExposed(ObservationFactory.Observation.class);
			this.setExposed(TraitFactory.class);
			this.setExposed(TraitFactory.Trait.class);
			this.setExposed(IsoDummyCameraCharacter.class);
			this.setExposed(Stats.class);
			this.setExposed(SurvivorDesc.class);
			this.setExposed(SurvivorFactory.class);
			this.setExposed(SurvivorFactory.SurvivorType.class);
			this.setExposed(IsoGameCharacter.class);
			this.setExposed(IsoGameCharacter.PerkInfo.class);
			this.setExposed(IsoGameCharacter.XP.class);
			this.setExposed(IsoPlayer.class);
			this.setExposed(IsoSurvivor.class);
			this.setExposed(IsoZombie.class);
			this.setExposed(Clipboard.class);
			this.setExposed(AngelCodeFont.class);
			this.setExposed(ZLogger.class);
			this.setExposed(PropertyContainer.class);
			this.setExposed(ColorInfo.class);
			this.setExposed(Texture.class);
			this.setExposed(SteamFriend.class);
			this.setExposed(SteamUGCDetails.class);
			this.setExposed(SteamWorkshopItem.class);
			this.setExposed(Color.class);
			this.setExposed(Colors.class);
			this.setExposed(Core.class);
			this.setExposed(Language.class);
			this.setExposed(PerformanceSettings.class);
			this.setExposed(SpriteRenderer.class);
			this.setExposed(Translator.class);
			this.setExposed(DebugOptions.class);
			this.setExposed(DebugOptions.BooleanDebugOption.class);
			this.setExposed(ErosionConfig.class);
			this.setExposed(ErosionConfig.Debug.class);
			this.setExposed(ErosionConfig.Season.class);
			this.setExposed(ErosionConfig.Seeds.class);
			this.setExposed(ErosionConfig.Time.class);
			this.setExposed(ErosionMain.class);
			this.setExposed(ErosionSeason.class);
			this.setExposed(ChooseGameInfo.Mod.class);
			this.setExposed(ChooseGameInfo.Story.class);
			this.setExposed(GameLoadingState.class);
			this.setExposed(MainScreenState.class);
			this.setExposed(CGlobalObjects.class);
			this.setExposed(CGlobalObjectSystem.class);
			this.setExposed(zombie.globalObjects.GlobalObject.class);
			this.setExposed(SGlobalObjects.class);
			this.setExposed(SGlobalObjectSystem.class);
			this.setExposed(Keys.class);
			this.setExposed(Mouse.class);
			this.setExposed(Clothing.class);
			this.setExposed(ComboItem.class);
			this.setExposed(Drainable.class);
			this.setExposed(DrainableComboItem.class);
			this.setExposed(Food.class);
			this.setExposed(HandWeapon.class);
			this.setExposed(InventoryContainer.class);
			this.setExposed(Key.class);
			this.setExposed(KeyRing.class);
			this.setExposed(Literature.class);
			this.setExposed(AlarmClock.class);
			this.setExposed(WeaponPart.class);
			this.setExposed(Moveable.class);
			this.setExposed(Radio.class);
			this.setExposed(ItemContainer.class);
			this.setExposed(InventoryItem.class);
			this.setExposed(InventoryItemFactory.class);
			this.setExposed(FixingManager.class);
			this.setExposed(RecipeManager.class);
			this.setExposed(IsoRegion.class);
			this.setExposed(DataCell.class);
			this.setExposed(DataChunk.class);
			this.setExposed(ChunkRegion.class);
			this.setExposed(MasterRegion.class);
			this.setExposed(IsoBuilding.class);
			this.setExposed(IsoRoom.class);
			this.setExposed(SafeHouse.class);
			this.setExposed(BarricadeAble.class);
			this.setExposed(IsoBarbecue.class);
			this.setExposed(IsoBarricade.class);
			this.setExposed(IsoCrate.class);
			this.setExposed(IsoCurtain.class);
			this.setExposed(IsoCarBatteryCharger.class);
			this.setExposed(IsoDeadBody.class);
			this.setExposed(IsoDoor.class);
			this.setExposed(IsoFire.class);
			this.setExposed(IsoFireManager.class);
			this.setExposed(IsoFireplace.class);
			this.setExposed(IsoGenerator.class);
			this.setExposed(IsoJukebox.class);
			this.setExposed(IsoLightSwitch.class);
			this.setExposed(IsoMolotovCocktail.class);
			this.setExposed(IsoWaveSignal.class);
			this.setExposed(IsoRadio.class);
			this.setExposed(IsoTelevision.class);
			this.setExposed(IsoStove.class);
			this.setExposed(IsoThumpable.class);
			this.setExposed(IsoTrap.class);
			this.setExposed(IsoTree.class);
			this.setExposed(IsoWheelieBin.class);
			this.setExposed(IsoWindow.class);
			this.setExposed(IsoWindowFrame.class);
			this.setExposed(IsoWorldInventoryObject.class);
			this.setExposed(IsoZombieGiblets.class);
			this.setExposed(RainManager.class);
			this.setExposed(ObjectRenderEffects.class);
			this.setExposed(IsoSprite.class);
			this.setExposed(IsoSpriteInstance.class);
			this.setExposed(IsoSpriteManager.class);
			this.setExposed(IsoSpriteGrid.class);
			this.setExposed(IsoFlagType.class);
			this.setExposed(IsoObjectType.class);
			this.setExposed(ClimateManager.class);
			this.setExposed(ClimateManager.DayInfo.class);
			this.setExposed(ClimateManager.ClimateFloat.class);
			this.setExposed(ClimateManager.ClimateColor.class);
			this.setExposed(ClimateManager.ClimateBool.class);
			this.setExposed(WeatherPeriod.class);
			this.setExposed(WeatherPeriod.WeatherStage.class);
			this.setExposed(WeatherPeriod.StrLerpVal.class);
			this.setExposed(ClimateManager.AirFront.class);
			this.setExposed(ThunderStorm.class);
			this.setExposed(ThunderStorm.ThunderCloud.class);
			this.setExposed(IsoWeatherFX.class);
			this.setExposed(Temperature.class);
			this.setExposed(Temperature.PlayerTempVars.class);
			this.setExposed(ClimateColorInfo.class);
			this.setExposed(BuildingDef.class);
			this.setExposed(IsoCamera.class);
			this.setExposed(IsoCell.class);
			this.setExposed(IsoChunkMap.class);
			this.setExposed(IsoDirections.class);
			this.setExposed(IsoGridSquare.class);
			this.setExposed(IsoHeatSource.class);
			this.setExposed(IsoLightSource.class);
			this.setExposed(IsoLot.class);
			this.setExposed(IsoLuaMover.class);
			this.setExposed(IsoMetaChunk.class);
			this.setExposed(IsoMetaCell.class);
			this.setExposed(IsoMetaGrid.class);
			this.setExposed(IsoMetaGrid.Trigger.class);
			this.setExposed(IsoMetaGrid.Zone.class);
			this.setExposed(IsoMovingObject.class);
			this.setExposed(IsoObject.class);
			this.setExposed(IsoObjectPicker.class);
			this.setExposed(IsoPushableObject.class);
			this.setExposed(IsoUtils.class);
			this.setExposed(IsoWorld.class);
			this.setExposed(LosUtil.class);
			this.setExposed(MetaObject.class);
			this.setExposed(RoomDef.class);
			this.setExposed(SliceY.class);
			this.setExposed(Vector2.class);
			this.setExposed(Vector3.class);
			this.setExposed(LuaEventManager.class);
			this.setExposed(MapObjects.class);
			this.setExposed(QuestCreator.class);
			this.setExposed(Server.class);
			this.setExposed(ServerOptions.class);
			this.setExposed(ServerOptions.BooleanServerOption.class);
			this.setExposed(ServerOptions.DoubleServerOption.class);
			this.setExposed(ServerOptions.IntegerServerOption.class);
			this.setExposed(ServerOptions.StringServerOption.class);
			this.setExposed(ServerOptions.TextServerOption.class);
			this.setExposed(ServerSettings.class);
			this.setExposed(ServerSettingsManager.class);
			this.setExposed(ZombiePopulationRenderer.class);
			this.setExposed(ZombiePopulationRenderer.BooleanDebugOption.class);
			this.setExposed(RadioAPI.class);
			this.setExposed(DeviceData.class);
			this.setExposed(DevicePresets.class);
			this.setExposed(PresetEntry.class);
			this.setExposed(ZomboidRadio.class);
			this.setExposed(RadioData.class);
			this.setExposed(RadioScriptManager.class);
			this.setExposed(SLSoundManager.class);
			this.setExposed(StorySound.class);
			this.setExposed(StorySoundEvent.class);
			this.setExposed(EventSound.class);
			this.setExposed(DataPoint.class);
			this.setExposed(EvolvedRecipe.class);
			this.setExposed(Fixing.class);
			this.setExposed(Fixing.Fixer.class);
			this.setExposed(Fixing.FixerSkill.class);
			this.setExposed(GameSoundScript.class);
			this.setExposed(Item.class);
			this.setExposed(Item.ClothingBodyLocation.class);
			this.setExposed(ItemRecipe.class);
			this.setExposed(Recipe.class);
			this.setExposed(Recipe.Result.class);
			this.setExposed(Recipe.Source.class);
			this.setExposed(ScriptModule.class);
			this.setExposed(VehicleScript.class);
			this.setExposed(VehicleScript.Area.class);
			this.setExposed(VehicleScript.Part.class);
			this.setExposed(VehicleScript.Passenger.class);
			this.setExposed(VehicleScript.Position.class);
			this.setExposed(VehicleScript.Wheel.class);
			this.setExposed(ScriptManager.class);
			this.setExposed(ActionProgressBar.class);
			this.setExposed(Clock.class);
			this.setExposed(UIDebugConsole.class);
			this.setExposed(LuaUIWindow.class);
			this.setExposed(ModalDialog.class);
			this.setExposed(MoodlesUI.class);
			this.setExposed(NewHealthPanel.class);
			this.setExposed(ObjectTooltip.class);
			this.setExposed(ObjectTooltip.Layout.class);
			this.setExposed(ObjectTooltip.LayoutItem.class);
			this.setExposed(RadarPanel.class);
			this.setExposed(RadialMenu.class);
			this.setExposed(SpeedControls.class);
			this.setExposed(TextManager.class);
			this.setExposed(UIElement.class);
			this.setExposed(UIFont.class);
			this.setExposed(UITransition.class);
			this.setExposed(UIManager.class);
			this.setExposed(UIServerToolbox.class);
			this.setExposed(UITextBox2.class);
			this.setExposed(VehicleGauge.class);
			this.setExposed(VirtualItemSlot.class);
			this.setExposed(TextDrawObject.class);
			this.setExposed(PZArrayList.class);
			this.setExposed(BaseVehicle.class);
			this.setExposed(PathFindBehavior2.class);
			this.setExposed(VehicleDoor.class);
			this.setExposed(VehicleLight.class);
			this.setExposed(VehiclePart.class);
			this.setExposed(VehicleType.class);
			this.setExposed(VehicleWindow.class);
			this.setExposed(DummySoundManager.class);
			this.setExposed(GameSounds.class);
			this.setExposed(GameTime.class);
			this.setExposed(GameWindow.class);
			this.setExposed(SandboxOptions.class);
			this.setExposed(SandboxOptions.BooleanSandboxOption.class);
			this.setExposed(SandboxOptions.DoubleSandboxOption.class);
			this.setExposed(SandboxOptions.EnumSandboxOption.class);
			this.setExposed(SandboxOptions.IntegerSandboxOption.class);
			this.setExposed(SoundManager.class);
			this.setExposed(SystemDisabler.class);
			this.setExposed(VirtualZombieManager.class);
			this.setExposed(DummyCharacterSoundEmitter.class);
			this.setExposed(CharacterSoundEmitter.class);
			this.setExposed(SoundManager.AmbientSoundEffect.class);
			this.setExposed(BaseAmbientStreamManager.class);
			this.setExposed(AmbientStreamManager.class);
			this.setExposed(Nutrition.class);
			this.setExposed(BSFurnace.class);
			this.setExposed(MultiStageBuilding.class);
			this.setExposed(MultiStageBuilding.Stage.class);
			this.setExposed(SleepingEvent.class);
			this.setExposed(IsoCompost.class);
			this.setExposed(Userlog.class);
			this.setExposed(Userlog.UserlogType.class);
			this.setExposed(ConfigOption.class);
			this.setExposed(BooleanConfigOption.class);
			this.setExposed(DoubleConfigOption.class);
			this.setExposed(EnumConfigOption.class);
			this.setExposed(IntegerConfigOption.class);
			this.setExposed(StringConfigOption.class);
			this.setExposed(Faction.class);
			this.setExposed(LuaManager.GlobalObject.LuaFileWriter.class);
			this.setExposed(Keyboard.class);
			this.setExposed(DBResult.class);
			this.setExposed(NonPvpZone.class);
			this.setExposed(DBTicket.class);
			this.setExposed(StashSystem.class);
			this.setExposed(StashBuilding.class);
			this.setExposed(Stash.class);
			this.setExposed(ItemType.class);
			this.setExposed(MapGroups.class);
			this.setExposed(ChatMessage.class);
			this.setExposed(ChatBase.class);
			this.setExposed(ServerChatMessage.class);
			if (Core.bDebug) {
				this.setExposed(Field.class);
				this.setExposed(Method.class);
				this.setExposed(Coroutine.class);
			}

			if (Core.bDebug) {
				try {
					this.exposeMethod(Class.class, Class.class.getMethod("getName"), LuaManager.env);
					this.exposeMethod(Class.class, Class.class.getMethod("getSimpleName"), LuaManager.env);
				} catch (NoSuchMethodException noSuchMethodException) {
					noSuchMethodException.printStackTrace();
				}
			}

			Iterator iterator = this.exposed.iterator();
			while (iterator.hasNext()) {
				Class javaClass = (Class)iterator.next();
				this.exposeLikeJavaRecursively(javaClass, LuaManager.env);
			}

			this.exposeGlobalFunctions(new LuaManager.GlobalObject());
			LuaManager.exposeKeyboardKeys(LuaManager.env);
		}

		public void setExposed(Class javaClass) {
			this.exposed.add(javaClass);
		}

		public boolean shouldExpose(Class javaClass) {
			return javaClass == null ? false : this.exposed.contains(javaClass);
		}
	}
}
