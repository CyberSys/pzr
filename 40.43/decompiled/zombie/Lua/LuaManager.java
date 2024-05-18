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

   public static void outputTable(KahluaTable var0, int var1) {
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

      KahluaTable var0 = env;
      exposer.exposeAll();
      exposer.TypeMap.put("function", LuaClosure.class);
      exposer.TypeMap.put("table", KahluaTable.class);
      outputTable(env, 0);
   }

   public static void LoadDir(String var0) throws URISyntaxException {
   }

   public static void LoadDirBase(String var0) throws Exception {
      LoadDirBase(var0, false);
   }

   public static void LoadDirBase(String var0, boolean var1) throws Exception {
      String var2 = "media/lua/" + var0 + "/";
      File var3 = new File(var2);
      if (!paths.contains(var2)) {
         paths.add(var2);
      }

      try {
         searchFolders(ZomboidFileSystem.instance.baseURI, var3);
      } catch (IOException var10) {
         ExceptionLogger.logException(var10);
      }

      ArrayList var11 = loadList;
      loadList = new ArrayList();
      ArrayList var12 = ZomboidFileSystem.instance.getModIDs();

      for(int var4 = 0; var4 < var12.size(); ++var4) {
         String var5 = ZomboidFileSystem.instance.getModDir((String)var12.get(var4));
         if (var5 != null) {
            URI var6 = (new File(var5)).toURI();
            File var7 = new File(var5 + File.separator + "media" + File.separator + "lua" + File.separator + var0);

            try {
               searchFolders(var6, var7);
            } catch (IOException var9) {
               ExceptionLogger.logException(var9);
            }
         }
      }

      Collections.sort(var11);
      Collections.sort(loadList);
      var11.addAll(loadList);
      loadList.clear();
      loadList = var11;
      HashSet var13 = new HashSet();
      Iterator var14 = loadList.iterator();

      while(true) {
         String var15;
         String var16;
         do {
            do {
               do {
                  do {
                     if (!var14.hasNext()) {
                        loadList.clear();
                        return;
                     }

                     var15 = (String)var14.next();
                  } while(var13.contains(var15));

                  var13.add(var15);
                  var16 = ZomboidFileSystem.instance.getAbsolutePath(var15);
                  if (var16 == null) {
                     throw new IllegalStateException("couldn't find \"" + var15 + "\"");
                  }

                  if (!var1) {
                     RunLua(var16);
                  }
               } while(checksumDone);
            } while(var15.contains("SandboxVars.lua"));
         } while(!GameServer.bServer && !GameClient.bClient);

         NetChecksum.checksummer.addFile(var15, var16);
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

   public static void searchFolders(URI var0, File var1) throws IOException {
      if (var1.isDirectory()) {
         String[] var2 = var1.list();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            searchFolders(var0, new File(var1.getAbsolutePath() + File.separator + var2[var3]));
         }
      } else if (var1.getAbsolutePath().toLowerCase().endsWith(".lua")) {
         loadList.add(ZomboidFileSystem.instance.getRelativeFile(var0, var1.getAbsolutePath()));
      }

   }

   public static String getLuaCacheDir() {
      String var0 = GameWindow.getCacheDir() + File.separator + "Lua";
      File var1 = new File(var0);
      if (!var1.exists()) {
         var1.mkdir();
      }

      return var0;
   }

   public static String getSandboxCacheDir() {
      String var0 = GameWindow.getCacheDir() + File.separator + "Sandbox Presets";
      File var1 = new File(var0);
      if (!var1.exists()) {
         var1.mkdir();
      }

      return var0;
   }

   public static void fillContainer(ItemContainer var0, IsoPlayer var1) {
      KahluaTable var2 = (KahluaTable)env.rawget("ItemPicker");
      caller.pcall(thread, var2.rawget("fillContainer"), var0, var1);
   }

   public static void fillContainerCount(ItemContainer var0, IsoPlayer var1) {
      KahluaTable var2 = (KahluaTable)env.rawget("ItemPicker");
      caller.pcall(thread, var2.rawget("fillContainerCount"), var0, var1, 5);
   }

   public static void updateOverlaySprite(IsoObject var0) {
      ItemPickerJava.updateOverlaySprite(var0);
   }

   public static LuaClosure getDotDelimitedClosure(String var0) {
      String[] var1 = var0.split("\\.");
      KahluaTable var2 = env;

      for(int var3 = 0; var3 < var1.length - 1; ++var3) {
         var2 = (KahluaTable)env.rawget(var1[var3]);
      }

      return (LuaClosure)var2.rawget(var1[var1.length - 1]);
   }

   public static void dropItem(InventoryItem var0) {
      LuaClosure var1 = getDotDelimitedClosure("ISInventoryPaneContextMenu.dropItem");
      caller.pcall(thread, var1, (Object)var0);
   }

   public static Object RunLua(String var0) {
      return RunLua(var0, false);
   }

   public static Object RunLua(String var0, boolean var1) {
      var0 = var0.replace("\\", "/");
      if (loaded.contains(var0)) {
         return loadedReturn.get(var0);
      } else {
         FuncState.currentFile = var0.substring(var0.lastIndexOf(47) + 1);
         FuncState.currentfullFile = var0;
         String var2 = var0;
         var0 = ZomboidFileSystem.instance.getString(var0.replace("\\", "/"));
         DebugLog.log("Loading: " + ZomboidFileSystem.instance.getRelativeFile(var0));

         InputStreamReader var3;
         try {
            var3 = IndieFileLoader.getStreamReader(var0);
         } catch (FileNotFoundException var17) {
            ExceptionLogger.logException(var17);
            return null;
         }

         LuaCompiler.rewriteEvents = var1;
         LuaClosure var4 = null;

         Object var6;
         label119: {
            try {
               var4 = LuaCompiler.loadis((Reader)var3, var0.substring(var0.lastIndexOf(47) + 1), env);
               break label119;
            } catch (Exception var18) {
               Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, "Error found in LUA file: " + var0, (Object)null);
               ExceptionLogger.logException(var18);
               var6 = null;
            } finally {
               try {
                  var3.close();
               } catch (Exception var16) {
               }

            }

            return var6;
         }

         LuaReturn var5 = caller.protectedCall(thread, var4);
         if (!var5.isSuccess()) {
            Logger.getLogger(IsoWorld.class.getName()).log(Level.SEVERE, (String)null, var5.getErrorString());
            Logger.getLogger(IsoWorld.class.getName()).log(Level.SEVERE, (String)null, var5.getJavaException());
            Logger.getLogger(IsoWorld.class.getName()).log(Level.SEVERE, (String)null, var5.getLuaStackTrace());
         }

         loaded.add(var2);
         var6 = var5.isSuccess() && var5.size() > 0 ? var5.getFirst() : null;
         if (var6 != null) {
            loadedReturn.put(var2, var6);
         } else {
            loadedReturn.remove(var2);
         }

         LuaCompiler.rewriteEvents = false;
         BaseVehicle.resetLuaFunctions();
         return var6;
      }
   }

   public static void Test() throws IOException {
   }

   public static Object get(Object var0) {
      return env.rawget(var0);
   }

   public static void call(String var0, Object var1) {
      caller.pcall(thread, env.rawget(var0), var1);
   }

   private static void exposeKeyboardKeys(KahluaTable var0) {
      Object var1 = var0.rawget("Keyboard");
      if (var1 instanceof KahluaTable) {
         KahluaTable var2 = (KahluaTable)var1;
         Field[] var3 = Keyboard.class.getFields();

         try {
            Field[] var4 = var3;
            int var5 = var3.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Field var7 = var4[var6];
               if (Modifier.isStatic(var7.getModifiers()) && Modifier.isPublic(var7.getModifiers()) && Modifier.isFinal(var7.getModifiers()) && var7.getType().equals(Integer.TYPE) && var7.getName().startsWith("KEY_") && !var7.getName().endsWith("WIN")) {
                  var2.rawset(var7.getName(), (double)var7.getInt((Object)null));
               }
            }
         } catch (Exception var8) {
         }

      }
   }

   public static String getHourMinuteJava() {
      String var0 = Calendar.getInstance().get(12) + "";
      if (Calendar.getInstance().get(12) < 10) {
         var0 = "0" + var0;
      }

      return Calendar.getInstance().get(11) + ":" + var0;
   }

   public static KahluaTable copyTable(KahluaTable var0) {
      KahluaTable var1 = platform.newTable();
      KahluaTableIterator var2 = var0.iterator();

      while(var2.advance()) {
         Object var3 = var2.getKey();
         Object var4 = var2.getValue();
         if (var4 instanceof KahluaTable) {
            var1.rawset(var3, copyTable((KahluaTable)var4));
         } else {
            var1.rawset(var3, var4);
         }
      }

      return var1;
   }

   static {
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

      @LuaMethod(
         name = "loadVehicleModel",
         global = true
      )
      public static Model loadVehicleModel(String var0, String var1, String var2) {
         return loadZomboidModel(var0, var1, var2, "vehicle", true);
      }

      @LuaMethod(
         name = "loadStaticZomboidModel",
         global = true
      )
      public static Model loadStaticZomboidModel(String var0, String var1, String var2) {
         return loadZomboidModel(var0, var1, var2, (String)null, true);
      }

      @LuaMethod(
         name = "loadSkinnedZomboidModel",
         global = true
      )
      public static Model loadSkinnedZomboidModel(String var0, String var1, String var2) {
         return loadZomboidModel(var0, var1, var2, (String)null, false);
      }

      @LuaMethod(
         name = "loadZomboidModel",
         global = true
      )
      public static Model loadZomboidModel(String var0, String var1, String var2, String var3, boolean var4) {
         try {
            if (var2.startsWith("/")) {
               var2 = var2.substring(1);
            }

            if (var1.startsWith("/")) {
               var1 = var1.substring(1);
            }

            if (var3 == null) {
               var3 = "basicEffect";
            }

            Model var5 = ModelLoader.instance.Load(ZomboidFileSystem.instance.getString(var1), ZomboidFileSystem.instance.getString(var2), var3, var4);
            ModelManager.instance.ModelMap.put(var0, var5);
            var5.Name = var0;
            return var5;
         } catch (IOException var6) {
            var6.printStackTrace();
            return null;
         }
      }

      @LuaMethod(
         name = "getSLSoundManager",
         global = true
      )
      public static SLSoundManager getSLSoundManager() {
         return null;
      }

      @LuaMethod(
         name = "getRadioAPI",
         global = true
      )
      public static RadioAPI getRadioAPI() {
         return RadioAPI.hasInstance() ? RadioAPI.getInstance() : null;
      }

      @LuaMethod(
         name = "getRadioTranslators",
         global = true
      )
      public static ArrayList getRadioTranslators(Language var0) {
         return RadioData.getTranslatorNames(var0);
      }

      @LuaMethod(
         name = "getZomboidRadio",
         global = true
      )
      public static ZomboidRadio getZomboidRadio() {
         return ZomboidRadio.hasInstance() ? ZomboidRadio.getInstance() : null;
      }

      @LuaMethod(
         name = "getRandomUUID",
         global = true
      )
      public static String getRandomUUID() {
         return ModUtilsJava.getRandomUUID();
      }

      @LuaMethod(
         name = "sendItemListNet",
         global = true
      )
      public static boolean sendItemListNet(IsoPlayer var0, ArrayList var1, IsoPlayer var2, String var3, String var4) {
         return ModUtilsJava.sendItemListNet(var0, var1, var2, var3, var4);
      }

      @LuaMethod(
         name = "instanceof",
         global = true
      )
      public static boolean instof(Object var0, String var1) {
         if ("PZKey".equals(var1)) {
            boolean var2 = false;
         }

         if (var0 == null) {
            return false;
         } else if (LuaManager.exposer.TypeMap.containsKey(var1)) {
            Class var3 = (Class)LuaManager.exposer.TypeMap.get(var1);
            return var3.isInstance(var0);
         } else if (var1.equals("LuaClosure") && var0 instanceof LuaClosure) {
            return true;
         } else {
            return var1.equals("KahluaTableImpl") && var0 instanceof KahluaTableImpl;
         }
      }

      @LuaMethod(
         name = "serverConnect",
         global = true
      )
      public static void serverConnect(String var0, String var1, String var2, String var3, String var4, String var5) {
         Core.GameMode = "Multiplayer";
         Core.setDifficulty("Hardcore");
         if (GameClient.connection != null) {
            GameClient.connection.forceDisconnect();
         }

         GameClient.bClient = true;
         GameClient.bCoopInvite = false;
         GameClient.instance.doConnect(var0, var1, var2, var3, var4, var5);
      }

      @LuaMethod(
         name = "serverConnectCoop",
         global = true
      )
      public static void serverConnectCoop(String var0) {
         Core.GameMode = "Multiplayer";
         Core.setDifficulty("Hardcore");
         if (GameClient.connection != null) {
            GameClient.connection.forceDisconnect();
         }

         GameClient.bClient = true;
         GameClient.bCoopInvite = true;
         GameClient.instance.doConnectCoop(var0);
      }

      @LuaMethod(
         name = "sendPing",
         global = true
      )
      public static void sendPing() {
         if (GameClient.bClient) {
            ByteBufferWriter var0 = GameClient.connection.startPingPacket();
            PacketTypes.doPingPacket(var0);
            var0.putLong(System.currentTimeMillis());
            GameClient.connection.endPingPacket();
         }

      }

      @LuaMethod(
         name = "forceDisconnect",
         global = true
      )
      public static void forceDisconnect() {
         if (GameClient.connection != null) {
            GameClient.connection.forceDisconnect();
         }

      }

      @LuaMethod(
         name = "backToSinglePlayer",
         global = true
      )
      public static void backToSinglePlayer() {
         if (GameClient.bClient) {
            GameClient.instance.doDisconnect("going back to single-player");
            GameClient.bClient = false;
            timeLastRefresh = 0L;
         }

      }

      @LuaMethod(
         name = "isIngameState",
         global = true
      )
      public static boolean isIngameState() {
         return GameWindow.states.current == IngameState.instance;
      }

      @LuaMethod(
         name = "requestPacketCounts",
         global = true
      )
      public static void requestPacketCounts() {
         if (GameClient.bClient) {
            GameClient.instance.requestPacketCounts();
         }

      }

      @LuaMethod(
         name = "getPacketCounts",
         global = true
      )
      public static KahluaTable getPacketCounts(int var0) {
         return GameClient.bClient ? GameClient.instance.getPacketCounts(var0) : null;
      }

      @LuaMethod(
         name = "getAllItems",
         global = true
      )
      public static ArrayList getAllItems() {
         return ScriptManager.instance.getAllItems();
      }

      @LuaMethod(
         name = "scoreboardUpdate",
         global = true
      )
      public static void scoreboardUpdate() {
         GameClient.instance.scoreboardUpdate();
      }

      @LuaMethod(
         name = "save",
         global = true
      )
      public static void save(boolean var0) {
         try {
            GameWindow.save(true);
         } catch (FileNotFoundException var2) {
            Logger.getLogger(IngameState.class.getName()).log(Level.SEVERE, (String)null, var2);
         } catch (IOException var3) {
            Logger.getLogger(IngameState.class.getName()).log(Level.SEVERE, (String)null, var3);
         }

      }

      @LuaMethod(
         name = "saveGame",
         global = true
      )
      public static void saveGame() {
         try {
            GameWindow.save(true);
         } catch (FileNotFoundException var1) {
            Logger.getLogger(IngameState.class.getName()).log(Level.SEVERE, (String)null, var1);
         } catch (IOException var2) {
            Logger.getLogger(IngameState.class.getName()).log(Level.SEVERE, (String)null, var2);
         }

      }

      @LuaMethod(
         name = "getAllRecipes",
         global = true
      )
      public static ArrayList getAllRecipes() {
         return new ArrayList(ScriptManager.instance.getAllRecipes());
      }

      @LuaMethod(
         name = "requestUserlog",
         global = true
      )
      public static void requestUserlog(String var0) {
         if (GameClient.bClient) {
            GameClient.instance.requestUserlog(var0);
         }

      }

      @LuaMethod(
         name = "addUserlog",
         global = true
      )
      public static void addUserlog(String var0, String var1, String var2) {
         if (GameClient.bClient) {
            GameClient.instance.addUserlog(var0, var1, var2);
         }

      }

      @LuaMethod(
         name = "removeUserlog",
         global = true
      )
      public static void removeUserlog(String var0, String var1, String var2) {
         if (GameClient.bClient) {
            GameClient.instance.removeUserlog(var0, var1, var2);
         }

      }

      @LuaMethod(
         name = "tabToX",
         global = true
      )
      public static String tabToX(String var0, int var1) {
         while(var0.length() < var1) {
            var0 = var0 + " ";
         }

         return var0;
      }

      @LuaMethod(
         name = "istype",
         global = true
      )
      public static boolean isType(Object var0, String var1) {
         if (LuaManager.exposer.TypeMap.containsKey(var1)) {
            Class var2 = (Class)LuaManager.exposer.TypeMap.get(var1);
            return var2.equals(var0.getClass());
         } else {
            return false;
         }
      }

      @LuaMethod(
         name = "isoToScreenX",
         global = true
      )
      public static float isoToScreenX(int var0, float var1, float var2, float var3) {
         IsoGameCharacter var4 = IsoCamera.CamCharacter;
         IsoCamera.CamCharacter = IsoPlayer.players[var0];
         IsoPlayer var5 = IsoPlayer.instance;
         IsoPlayer.instance = IsoPlayer.players[var0];
         float var6 = IsoUtils.XToScreenExact(var1, var2, var3, 0);
         var6 /= Core.getInstance().getZoom(var0);
         IsoCamera.CamCharacter = var4;
         IsoPlayer.instance = var5;
         return (float)IsoCamera.getScreenLeft(var0) + var6;
      }

      @LuaMethod(
         name = "isoToScreenY",
         global = true
      )
      public static float isoToScreenY(int var0, float var1, float var2, float var3) {
         IsoGameCharacter var4 = IsoCamera.CamCharacter;
         IsoCamera.CamCharacter = IsoPlayer.players[var0];
         IsoPlayer var5 = IsoPlayer.instance;
         IsoPlayer.instance = IsoPlayer.players[var0];
         float var6 = IsoUtils.YToScreenExact(var1, var2, var3, 0);
         var6 /= Core.getInstance().getZoom(var0);
         IsoCamera.CamCharacter = var4;
         IsoPlayer.instance = var5;
         return (float)IsoCamera.getScreenTop(var0) + var6;
      }

      @LuaMethod(
         name = "getAmbientStreamManager",
         global = true
      )
      public static BaseAmbientStreamManager getAmbientStreamManager() {
         return AmbientStreamManager.instance;
      }

      @LuaMethod(
         name = "getSleepingEvent",
         global = true
      )
      public static SleepingEvent getSleepingEvent() {
         return SleepingEvent.instance;
      }

      @LuaMethod(
         name = "setPlayerMovementActive",
         global = true
      )
      public static void setPlayerMovementActive(int var0, boolean var1) {
         IsoPlayer.players[var0].bJoypadMovementActive = var1;
      }

      @LuaMethod(
         name = "setActivePlayer",
         global = true
      )
      public static void setActivePlayer(int var0) {
         if (!GameClient.bClient) {
            IsoPlayer.instance = IsoPlayer.players[var0];
            IsoCamera.CamCharacter = IsoPlayer.instance;
         }
      }

      @LuaMethod(
         name = "getPlayer",
         global = true
      )
      public static IsoPlayer getPlayer() {
         return IsoPlayer.getInstance();
      }

      @LuaMethod(
         name = "getNumActivePlayers",
         global = true
      )
      public static int getNumActivePlayers() {
         return IsoPlayer.numPlayers;
      }

      @LuaMethod(
         name = "playServerSound",
         global = true
      )
      public static void playServerSound(String var0, IsoGridSquare var1) {
         GameServer.PlayWorldSoundServer(var0, false, var1, 0.2F, 5.0F, 1.1F, true);
      }

      @LuaMethod(
         name = "getMaxActivePlayers",
         global = true
      )
      public static int getMaxActivePlayers() {
         return 4;
      }

      @LuaMethod(
         name = "getPlayerScreenLeft",
         global = true
      )
      public static int getPlayerScreenLeft(int var0) {
         return IsoCamera.getScreenLeft(var0);
      }

      @LuaMethod(
         name = "getPlayerScreenTop",
         global = true
      )
      public static int getPlayerScreenTop(int var0) {
         return IsoCamera.getScreenTop(var0);
      }

      @LuaMethod(
         name = "getPlayerScreenWidth",
         global = true
      )
      public static int getPlayerScreenWidth(int var0) {
         return IsoCamera.getScreenWidth(var0);
      }

      @LuaMethod(
         name = "getPlayerScreenHeight",
         global = true
      )
      public static int getPlayerScreenHeight(int var0) {
         return IsoCamera.getScreenHeight(var0);
      }

      @LuaMethod(
         name = "getPlayerByOnlineID",
         global = true
      )
      public static IsoPlayer getPlayerByOnlineID(int var0) {
         if (GameServer.bServer) {
            return (IsoPlayer)GameServer.IDToPlayerMap.get(var0);
         } else {
            return GameClient.bClient ? (IsoPlayer)GameClient.IDToPlayerMap.get(var0) : null;
         }
      }

      @LuaMethod(
         name = "initUISystem",
         global = true
      )
      public static void initUISystem() {
         UIManager.init();
         LuaEventManager.triggerEvent("OnCreatePlayer", 0, IsoPlayer.players[0]);
      }

      @LuaMethod(
         name = "getPerformance",
         global = true
      )
      public static PerformanceSettings getPerformance() {
         return PerformanceSettings.instance;
      }

      @LuaMethod(
         name = "screenZoomIn",
         global = true
      )
      public void screenZoomIn() {
      }

      @LuaMethod(
         name = "screenZoomOut",
         global = true
      )
      public void screenZoomOut() {
      }

      @LuaMethod(
         name = "addSound",
         global = true
      )
      public void addSound(IsoObject var1, int var2, int var3, int var4, int var5, int var6) {
         WorldSoundManager.instance.addSound(var1, var2, var3, var4, var5, var6);
      }

      @LuaMethod(
         name = "sendAddXp",
         global = true
      )
      public void sendAddXp(IsoPlayer var1, PerkFactory.Perks var2, int var3, boolean var4, boolean var5) {
         if (GameClient.bClient) {
            GameClient.instance.sendAddXpFromPlayerStatsUI(var1, var2, var3, var4, var5);
         }

      }

      @LuaMethod(
         name = "SyncXp",
         global = true
      )
      public void SyncXp(IsoPlayer var1) {
         if (GameClient.bClient) {
            GameClient.instance.sendSyncXp(var1);
         }

      }

      @LuaMethod(
         name = "getDBSchema",
         global = true
      )
      public static void getDBSchema() {
         GameClient.instance.getDBSchema();
      }

      @LuaMethod(
         name = "getTableResult",
         global = true
      )
      public static void getTableResult(String var0, int var1) {
         GameClient.instance.getTableResult(var0, var1);
      }

      @LuaMethod(
         name = "addLevelUpPoint",
         global = true
      )
      public void addLevelUpPoint(IsoPlayer var1) {
         if (GameClient.bClient) {
            GameClient.instance.addLevelUpPoint(var1);
         }

      }

      @LuaMethod(
         name = "getWorldSoundManager",
         global = true
      )
      public static WorldSoundManager getWorldSoundManager() {
         return WorldSoundManager.instance;
      }

      @LuaMethod(
         name = "AddWorldSound",
         global = true
      )
      public static void AddWorldSound(IsoPlayer var0, int var1) {
         WorldSoundManager.instance.addSound((IsoObject)null, (int)var0.getX(), (int)var0.getY(), (int)var0.getZ(), var1, 10, false);
      }

      @LuaMethod(
         name = "AddNoiseToken",
         global = true
      )
      public static void AddNoiseToken(IsoGridSquare var0, int var1) {
      }

      @LuaMethod(
         name = "pauseSoundAndMusic",
         global = true
      )
      public static void pauseSoundAndMusic() {
         SoundManager.instance.pauseSoundAndMusic();
      }

      @LuaMethod(
         name = "resumeSoundAndMusic",
         global = true
      )
      public static void resumeSoundAndMusic() {
         SoundManager.instance.resumeSoundAndMusic();
      }

      @LuaMethod(
         name = "isDemo",
         global = true
      )
      public static boolean isDemo() {
         Core.getInstance();
         return false;
      }

      @LuaMethod(
         name = "getTimeInMillis",
         global = true
      )
      public static long getTimeInMillis() {
         return System.currentTimeMillis();
      }

      @LuaMethod(
         name = "getCurrentCoroutine",
         global = true
      )
      public static Coroutine getCurrentCoroutine() {
         return LuaManager.thread.getCurrentCoroutine();
      }

      @LuaMethod(
         name = "reloadLuaFile",
         global = true
      )
      public static void reloadLuaFile(String var0) {
         LuaManager.loaded.remove(var0);
         LuaManager.RunLua(var0, true);
      }

      @LuaMethod(
         name = "reloadServerLuaFile",
         global = true
      )
      public static void reloadServerLuaFile(String var0) {
         if (GameServer.bServer) {
            var0 = GameWindow.getCacheDir() + File.separator + "Server" + File.separator + var0;
            LuaManager.loaded.remove(var0);
            LuaManager.RunLua(var0, true);
         }
      }

      @LuaMethod(
         name = "getServerSpawnRegions",
         global = true
      )
      public static KahluaTable getServerSpawnRegions() {
         return !GameClient.bClient ? null : GameClient.instance.getServerSpawnRegions();
      }

      @LuaMethod(
         name = "getServerOptions",
         global = true
      )
      public static ServerOptions getServerOptions() {
         return ServerOptions.instance;
      }

      @LuaMethod(
         name = "getServerName",
         global = true
      )
      public static String getServerName() {
         return GameServer.ServerName;
      }

      @LuaMethod(
         name = "getSpecificPlayer",
         global = true
      )
      public static IsoPlayer getSpecificPlayer(int var0) {
         return IsoPlayer.players[var0];
      }

      @LuaMethod(
         name = "getCameraOffX",
         global = true
      )
      public static float getCameraOffX() {
         return IsoCamera.getOffX();
      }

      @LuaMethod(
         name = "getLatestSave",
         global = true
      )
      public static KahluaTable getLatestSave() {
         KahluaTable var0 = LuaManager.platform.newTable();
         BufferedReader var1 = null;

         try {
            var1 = new BufferedReader(new FileReader(new File(GameWindow.getCacheDir() + File.separator + "latestSave.ini")));
         } catch (FileNotFoundException var4) {
            return var0;
         }

         try {
            String var2 = null;

            for(int var3 = 1; (var2 = var1.readLine()) != null; ++var3) {
               var0.rawset(var3, var2);
            }

            var1.close();
            return var0;
         } catch (Exception var5) {
            return var0;
         }
      }

      @LuaMethod(
         name = "isCurrentExecutionPoint",
         global = true
      )
      public static boolean isCurrentExecutionPoint(String var0, int var1) {
         int var2 = LuaManager.thread.currentCoroutine.getCallframeTop() - 1;
         if (var2 < 0) {
            var2 = 0;
         }

         LuaCallFrame var3 = LuaManager.thread.currentCoroutine.getCallFrame(var2);
         if (var3.closure == null) {
            return false;
         } else {
            return var3.closure.prototype.lines[var3.pc] == var1 && var0.equals(var3.closure.prototype.filename);
         }
      }

      @LuaMethod(
         name = "toggleBreakOnChange",
         global = true
      )
      public static void toggleBreakOnChange(KahluaTable var0, Object var1) {
         if (Core.bDebug) {
            LuaManager.thread.toggleBreakOnChange(var0, var1);
         }

      }

      @LuaMethod(
         name = "isDebugEnabled",
         global = true
      )
      public static boolean isDebugEnabled() {
         return Core.bDebug;
      }

      @LuaMethod(
         name = "toggleBreakOnRead",
         global = true
      )
      public static void toggleBreakOnRead(KahluaTable var0, Object var1) {
         if (Core.bDebug) {
            LuaManager.thread.toggleBreakOnRead(var0, var1);
         }

      }

      @LuaMethod(
         name = "toggleBreakpoint",
         global = true
      )
      public static void toggleBreakpoint(String var0, int var1) {
         var0 = var0.replace("\\", "/");
         if (Core.bDebug) {
            LuaManager.thread.breakpointToggle(var0, var1);
         }

      }

      @LuaMethod(
         name = "hasDataReadBreakpoint",
         global = true
      )
      public static boolean hasDataReadBreakpoint(KahluaTable var0, Object var1) {
         return LuaManager.thread.hasReadDataBreakpoint(var0, var1);
      }

      @LuaMethod(
         name = "hasDataBreakpoint",
         global = true
      )
      public static boolean hasDataBreakpoint(KahluaTable var0, Object var1) {
         return LuaManager.thread.hasDataBreakpoint(var0, var1);
      }

      @LuaMethod(
         name = "hasBreakpoint",
         global = true
      )
      public static boolean hasBreakpoint(String var0, int var1) {
         return LuaManager.thread.hasBreakpoint(var0, var1);
      }

      @LuaMethod(
         name = "getLoadedLuaCount",
         global = true
      )
      public static int getLoadedLuaCount() {
         return LuaManager.loaded.size();
      }

      @LuaMethod(
         name = "getLoadedLua",
         global = true
      )
      public static String getLoadedLua(int var0) {
         return (String)LuaManager.loaded.get(var0);
      }

      @LuaMethod(
         name = "isServer",
         global = true
      )
      public static boolean isServer() {
         return GameServer.bServer;
      }

      @LuaMethod(
         name = "isServerSoftReset",
         global = true
      )
      public static boolean isServerSoftReset() {
         return GameServer.bServer && System.getProperty("softreset") != null;
      }

      @LuaMethod(
         name = "isClient",
         global = true
      )
      public static boolean isClient() {
         return GameClient.bClient;
      }

      @LuaMethod(
         name = "canModifyPlayerStats",
         global = true
      )
      public static boolean canModifyPlayerStats() {
         return !GameClient.bClient ? true : GameClient.canModifyPlayerStats();
      }

      @LuaMethod(
         name = "executeQuery",
         global = true
      )
      public static void executeQuery(String var0, KahluaTable var1) {
         GameClient.instance.executeQuery(var0, var1);
      }

      @LuaMethod(
         name = "canSeePlayerStats",
         global = true
      )
      public static boolean canSeePlayerStats() {
         return GameClient.canSeePlayerStats();
      }

      @LuaMethod(
         name = "getAccessLevel",
         global = true
      )
      public static String getAccessLevel() {
         return GameClient.accessLevel;
      }

      @LuaMethod(
         name = "getOnlinePlayers",
         global = true
      )
      public static ArrayList getOnlinePlayers() {
         if (GameServer.bServer) {
            return GameServer.getPlayers();
         } else {
            return GameClient.bClient ? GameClient.instance.getPlayers() : null;
         }
      }

      @LuaMethod(
         name = "getDebug",
         global = true
      )
      public static boolean getDebug() {
         return Core.bDebug || GameServer.bServer && GameServer.bDebug;
      }

      @LuaMethod(
         name = "getCameraOffY",
         global = true
      )
      public static float getCameraOffY() {
         return IsoCamera.getOffY();
      }

      @LuaMethod(
         name = "createRegionFile",
         global = true
      )
      public static KahluaTable createRegionFile() {
         KahluaTable var0 = LuaManager.platform.newTable();
         String var1 = IsoWorld.instance.getMap();
         if (var1.equals("DEFAULT")) {
            MapGroups var2 = new MapGroups();
            var2.createGroups();
            if (var2.getNumberOfGroups() != 1) {
               throw new RuntimeException("GameMap is DEFAULT but there are multiple worlds to choose from");
            }

            var2.setWorld(0);
            var1 = IsoWorld.instance.getMap();
         }

         if (!GameClient.bClient && !GameServer.bServer) {
            var1 = MapGroups.addMissingVanillaDirectories(var1);
         }

         String[] var10 = var1.split(";");
         int var3 = 1;
         String[] var4 = var10;
         int var5 = var10.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String var7 = var4[var6];
            var7 = var7.trim();
            if (!var7.isEmpty()) {
               File var8 = new File(ZomboidFileSystem.instance.getString("media/maps/" + var7 + "/spawnpoints.lua"));
               if (var8.exists()) {
                  KahluaTable var9 = LuaManager.platform.newTable();
                  var9.rawset("name", var7);
                  var9.rawset("file", "media/maps/" + var7 + "/spawnpoints.lua");
                  var0.rawset(var3, var9);
                  ++var3;
               }
            }
         }

         return var0;
      }

      @LuaMethod(
         name = "getMapDirectoryTable",
         global = true
      )
      public static KahluaTable getMapDirectoryTable() {
         KahluaTable var0 = LuaManager.platform.newTable();
         File var1 = new File("media/maps/");
         String[] var2 = var1.list();
         if (var2 == null) {
            return var0;
         } else {
            int var3 = 1;

            for(int var4 = 0; var4 < var2.length; ++var4) {
               String var5 = var2[var4];
               if (!var5.equals("challengemaps")) {
                  Double var6 = (double)var3;
                  var0.rawset(var6, var5);
                  ++var3;
               }
            }

            ChooseGameInfo var12 = new ChooseGameInfo();
            Iterator var13 = ZomboidFileSystem.instance.mods.iterator();

            while(var13.hasNext()) {
               String var14 = (String)var13.next();
               ChooseGameInfo.Mod var7 = null;

               try {
                  var7 = var12.getModDetails(var14);
               } catch (Exception var11) {
               }

               if (var7 != null) {
                  var1 = new File(var7.getDir() + "/media/maps/");
                  if (var1.exists()) {
                     var2 = var1.list();
                     if (var2 != null) {
                        for(int var8 = 0; var8 < var2.length; ++var8) {
                           String var9 = var2[var8];
                           ChooseGameInfo.Map var10 = ChooseGameInfo.getMapDetails(var9);
                           if (var10.getLotDirectories() != null && !var10.getLotDirectories().isEmpty() && !var9.equals("challengemaps")) {
                              var0.rawset((double)var3, var9);
                              ++var3;
                           }
                        }
                     }
                  }
               }
            }

            return var0;
         }
      }

      @LuaMethod(
         name = "deleteSave",
         global = true
      )
      public static void deleteSave(String var0) {
         File var1 = new File(GameWindow.getSaveDir() + File.separator + var0);
         String[] var2 = var1.list();
         if (var2 != null) {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               File var4 = new File(GameWindow.getSaveDir() + File.separator + var0 + File.separator + var2[var3]);
               if (var4.isDirectory()) {
                  deleteSave(var0 + File.separator + var4.getName());
               }

               var4.delete();
            }

            var1.delete();
         }
      }

      @LuaMethod(
         name = "sendPlayerExtraInfo",
         global = true
      )
      public static void sendPlayerExtraInfo(IsoPlayer var0) {
         GameClient.sendPlayerExtraInfo(var0);
      }

      @LuaMethod(
         name = "getServerAddressFromArgs",
         global = true
      )
      public static String getServerAddressFromArgs() {
         if (System.getProperty("args.server.connect") != null) {
            String var0 = System.getProperty("args.server.connect");
            System.clearProperty("args.server.connect");
            return var0;
         } else {
            return null;
         }
      }

      @LuaMethod(
         name = "getServerPasswordFromArgs",
         global = true
      )
      public static String getServerPasswordFromArgs() {
         if (System.getProperty("args.server.password") != null) {
            String var0 = System.getProperty("args.server.password");
            System.clearProperty("args.server.password");
            return var0;
         } else {
            return null;
         }
      }

      @LuaMethod(
         name = "getServerListFile",
         global = true
      )
      public static String getServerListFile() {
         return SteamUtils.isSteamModeEnabled() ? "ServerListSteam.txt" : "ServerList.txt";
      }

      @LuaMethod(
         name = "getServerList",
         global = true
      )
      public static KahluaTable getServerList() {
         ArrayList var0 = new ArrayList();
         KahluaTable var1 = LuaManager.platform.newTable();
         BufferedReader var2 = null;

         try {
            File var3 = new File(LuaManager.getLuaCacheDir() + File.separator + getServerListFile());
            if (!var3.exists()) {
               var3.createNewFile();
            }

            var2 = new BufferedReader(new FileReader(var3));
            String var4 = null;
            Server var5 = null;

            while((var4 = var2.readLine()) != null) {
               if (var4.startsWith("name=")) {
                  var5 = new Server();
                  var0.add(var5);
                  var5.setName(var4.replaceFirst("name=", ""));
               } else if (var4.startsWith("ip=")) {
                  var5.setIp(var4.replaceFirst("ip=", ""));
               } else if (var4.startsWith("localip=")) {
                  var5.setLocalIP(var4.replaceFirst("localip=", ""));
               } else if (var4.startsWith("description=")) {
                  var5.setDescription(var4.replaceFirst("description=", ""));
               } else if (var4.startsWith("port=")) {
                  var5.setPort(var4.replaceFirst("port=", ""));
               } else if (var4.startsWith("user=")) {
                  var5.setUserName(var4.replaceFirst("user=", ""));
               } else if (var4.startsWith("password=")) {
                  var5.setPwd(var4.replaceFirst("password=", ""));
               } else if (var4.startsWith("serverpassword=")) {
                  var5.setServerPassword(var4.replaceFirst("serverpassword=", ""));
               }
            }

            int var6 = 1;

            for(int var7 = 0; var7 < var0.size(); ++var7) {
               Server var8 = (Server)var0.get(var7);
               Double var9 = (double)var6;
               var1.rawset(var9, var8);
               ++var6;
            }
         } catch (Exception var18) {
            var18.printStackTrace();
         } finally {
            try {
               var2.close();
            } catch (Exception var17) {
            }

         }

         return var1;
      }

      @LuaMethod(
         name = "ping",
         global = true
      )
      public static void ping(String var0, String var1, String var2, String var3) {
         GameClient.askPing = true;
         serverConnect(var0, var1, var2, "", var3, "");
      }

      @LuaMethod(
         name = "stopPing",
         global = true
      )
      public static void stopPing() {
         GameClient.askPing = false;
      }

      @LuaMethod(
         name = "transformIntoKahluaTable",
         global = true
      )
      public static KahluaTable transformIntoKahluaTable(HashMap var0) {
         KahluaTable var1 = LuaManager.platform.newTable();
         Iterator var2 = var0.entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            var1.rawset(var3.getKey(), var3.getValue());
         }

         return var1;
      }

      @LuaMethod(
         name = "getSaveDirectory",
         global = true
      )
      public static ArrayList getSaveDirectory(String var0) {
         File var1 = new File(var0 + File.separator);
         if (!var1.exists() && !Core.getInstance().isNoSave()) {
            var1.mkdir();
         }

         String[] var2 = var1.list();
         if (var2 == null) {
            return null;
         } else {
            ArrayList var3 = new ArrayList();

            for(int var4 = 0; var4 < var2.length; ++var4) {
               File var5 = new File(var0 + File.separator + var2[var4]);
               if (var5.isDirectory()) {
                  var3.add(var5);
               }
            }

            return var3;
         }
      }

      @LuaMethod(
         name = "getFullSaveDirectoryTable",
         global = true
      )
      public static KahluaTable getFullSaveDirectoryTable() {
         KahluaTable var0 = LuaManager.platform.newTable();
         File var1 = new File(GameWindow.getSaveDir() + File.separator);
         if (!var1.exists()) {
            var1.mkdir();
         }

         String[] var2 = var1.list();
         if (var2 == null) {
            return var0;
         } else {
            ArrayList var3 = new ArrayList();

            int var4;
            for(var4 = 0; var4 < var2.length; ++var4) {
               File var5 = new File(GameWindow.getSaveDir() + File.separator + var2[var4]);
               if (var5.isDirectory() && !"Multiplayer".equals(var2[var4])) {
                  ArrayList var6 = getSaveDirectory(GameWindow.getSaveDir() + File.separator + var2[var4]);
                  var3.addAll(var6);
               }
            }

            Collections.sort(var3, new Comparator() {
               public int compare(File var1, File var2) {
                  return Long.valueOf(var2.lastModified()).compareTo(var1.lastModified());
               }
            });
            var4 = 1;

            for(int var9 = 0; var9 < var3.size(); ++var9) {
               File var10 = (File)var3.get(var9);
               String var7 = getSaveName(var10);
               Double var8 = (double)var4;
               var0.rawset(var8, var7);
               ++var4;
            }

            return var0;
         }
      }

      public static String getSaveName(File var0) {
         String[] var1 = var0.getAbsolutePath().split("\\" + File.separator);
         return var1[var1.length - 2] + File.separator + var0.getName();
      }

      @LuaMethod(
         name = "getSaveDirectoryTable",
         global = true
      )
      public static KahluaTable getSaveDirectoryTable() {
         KahluaTable var0 = LuaManager.platform.newTable();
         return var0;
      }

      public static List getStories() {
         ArrayList var0 = new ArrayList();
         File var1 = new File("media" + File.separator + "stories");
         if (!var1.exists()) {
            var1.mkdir();
         }

         String[] var2 = var1.list();
         if (var2 == null) {
            return var0;
         } else {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               if ((new File(var1.getPath() + File.separator + var2[var3])).isDirectory()) {
                  File var4 = new File(var1.getPath() + File.separator + var2[var3] + File.separator + "story.info");
                  if (var4.exists()) {
                     var0.add(var2[var3]);
                  }
               }
            }

            return var0;
         }
      }

      public static List getMods() {
         ArrayList var0 = new ArrayList();
         ZomboidFileSystem.instance.getAllModFolders(var0);
         return var0;
      }

      @LuaMethod(
         name = "doChallenge",
         global = true
      )
      public static void doChallenge(KahluaTable var0) {
         Core.GameMode = var0.rawget("gameMode").toString();
         Core.bLastStand = Core.GameMode.equals("LastStand");
         Core.getInstance().setChallenge(true);
         getWorld().setMap(var0.getString("world"));
         Integer var1 = Rand.Next(100000000);
         IsoWorld.instance.setWorld(var1.toString());
         getWorld().bDoChunkMapUpdate = false;
      }

      @LuaMethod(
         name = "doTutorial",
         global = true
      )
      public static void doTutorial(KahluaTable var0) {
         Core.GameMode = "Tutorial";
         Core.bLastStand = false;
         Core.getInstance().setChallenge(false);
         Core.bTutorial = true;
         getWorld().setMap(var0.getString("world"));
         getWorld().bDoChunkMapUpdate = false;
      }

      @LuaMethod(
         name = "deleteAllGameModeSaves",
         global = true
      )
      public static void deleteAllGameModeSaves(String var0) {
         String var1 = Core.GameMode;
         Core.GameMode = var0;
         Path var2 = Paths.get(GameWindow.getGameModeCacheDir());
         if (!Files.exists(var2, new LinkOption[0])) {
            Core.GameMode = var1;
         } else {
            try {
               Files.walkFileTree(var2, new FileVisitor() {
                  public FileVisitResult preVisitDirectory(Path var1, BasicFileAttributes var2) throws IOException {
                     return FileVisitResult.CONTINUE;
                  }

                  public FileVisitResult visitFile(Path var1, BasicFileAttributes var2) throws IOException {
                     Files.delete(var1);
                     return FileVisitResult.CONTINUE;
                  }

                  public FileVisitResult visitFileFailed(Path var1, IOException var2) throws IOException {
                     var2.printStackTrace();
                     return FileVisitResult.CONTINUE;
                  }

                  public FileVisitResult postVisitDirectory(Path var1, IOException var2) throws IOException {
                     Files.delete(var1);
                     return FileVisitResult.CONTINUE;
                  }
               });
            } catch (IOException var4) {
               var4.printStackTrace();
            }

            Core.GameMode = var1;
         }
      }

      @LuaMethod(
         name = "sledgeDestroy",
         global = true
      )
      public static void sledgeDestroy(IsoObject var0) {
         if (GameClient.bClient) {
            GameClient.destroy(var0);
         }

      }

      @LuaMethod(
         name = "getTickets",
         global = true
      )
      public static void getTickets(String var0) {
         if (GameClient.bClient) {
            GameClient.getTickets(var0);
         }

      }

      @LuaMethod(
         name = "addTicket",
         global = true
      )
      public static void addTicket(String var0, String var1, int var2) {
         if (GameClient.bClient) {
            GameClient.addTicket(var0, var1, var2);
         }

      }

      @LuaMethod(
         name = "removeTicket",
         global = true
      )
      public static void removeTicket(int var0) {
         if (GameClient.bClient) {
            GameClient.removeTicket(var0);
         }

      }

      @LuaMethod(
         name = "sendFactionInvite",
         global = true
      )
      public static void sendFactionInvite(Faction var0, IsoPlayer var1, String var2) {
         if (GameClient.bClient) {
            GameClient.sendFactionInvite(var0, var1, var2);
         }

      }

      @LuaMethod(
         name = "acceptFactionInvite",
         global = true
      )
      public static void acceptFactionInvite(Faction var0, String var1) {
         if (GameClient.bClient) {
            GameClient.acceptFactionInvite(var0, var1);
         }

      }

      @LuaMethod(
         name = "createHordeFromTo",
         global = true
      )
      public static void createHordeFromTo(float var0, float var1, float var2, float var3, int var4) {
         ZombiePopulationManager.instance.createHordeFromTo((int)var0, (int)var1, (int)var2, (int)var3, var4);
      }

      @LuaMethod(
         name = "createHordeInAreaTo",
         global = true
      )
      public static void createHordeInAreaTo(int var0, int var1, int var2, int var3, int var4, int var5, int var6) {
         ZombiePopulationManager.instance.createHordeInAreaTo(var0, var1, var2, var3, var4, var5, var6);
      }

      @LuaMethod(
         name = "spawnHorde",
         global = true
      )
      public static void spawnHorde(float var0, float var1, float var2, float var3, float var4, int var5) {
         for(int var6 = 0; var6 < var5; ++var6) {
            VirtualZombieManager.instance.choices.clear();
            IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare((double)Rand.Next(var0, var2), (double)Rand.Next(var1, var3), (double)var4);
            if (var7 != null) {
               VirtualZombieManager.instance.choices.add(var7);
               VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.fromIndex(Rand.Next(IsoDirections.Max.index())).index(), false);
            }
         }

      }

      @LuaMethod(
         name = "createZombie",
         global = true
      )
      public static IsoZombie createZombie(float var0, float var1, float var2, SurvivorDesc var3, int var4, IsoDirections var5) {
         VirtualZombieManager.instance.choices.clear();
         IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare((double)var0, (double)var1, (double)var2);
         VirtualZombieManager.instance.choices.add(var6);
         return var3 != null ? VirtualZombieManager.instance.createRealZombieAlways(var5.index(), false, var3, var4) : VirtualZombieManager.instance.createRealZombieAlways(var5.index(), false);
      }

      @LuaMethod(
         name = "triggerEvent",
         global = true
      )
      public static void triggerEvent(String var0) {
         LuaEventManager.triggerEvent(var0);
      }

      @LuaMethod(
         name = "triggerEvent",
         global = true
      )
      public static void triggerEvent(String var0, Object var1) {
         LuaEventManager.triggerEventGarbage(var0, var1);
      }

      @LuaMethod(
         name = "triggerEvent",
         global = true
      )
      public static void triggerEvent(String var0, Object var1, Object var2) {
         LuaEventManager.triggerEventGarbage(var0, var1, var2);
      }

      @LuaMethod(
         name = "triggerEvent",
         global = true
      )
      public static void triggerEvent(String var0, Object var1, Object var2, Object var3) {
         LuaEventManager.triggerEventGarbage(var0, var1, var2, var3);
      }

      @LuaMethod(
         name = "triggerEvent",
         global = true
      )
      public static void triggerEvent(String var0, Object var1, Object var2, Object var3, Object var4) {
         LuaEventManager.triggerEventGarbage(var0, var1, var2, var3, var4);
      }

      @LuaMethod(
         name = "debugLuaTable",
         global = true
      )
      public static void debugLuaTable(Object var0, int var1) {
         if (var1 <= 1) {
            if (var0 instanceof KahluaTable) {
               KahluaTable var2 = (KahluaTable)var0;
               KahluaTableIterator var3 = var2.iterator();
               String var4 = "";

               for(int var5 = 0; var5 < var1; ++var5) {
                  var4 = var4 + "\t";
               }

               do {
                  Object var7 = var3.getKey();
                  Object var6 = var3.getValue();
                  if (var7 != null) {
                     if (var6 != null) {
                        DebugLog.log(var4 + var7 + " : " + var6.toString());
                     }

                     if (var6 instanceof KahluaTable) {
                        debugLuaTable(var6, var1 + 1);
                     }
                  }
               } while(var3.advance());

               if (var2.getMetatable() != null) {
                  debugLuaTable(var2.getMetatable(), var1);
               }
            }

         }
      }

      @LuaMethod(
         name = "debugLuaTable",
         global = true
      )
      public static void debugLuaTable(Object var0) {
         debugLuaTable(var0, 0);
      }

      @LuaMethod(
         name = "sendItemsInContainer",
         global = true
      )
      public static void sendItemsInContainer(IsoObject var0, ItemContainer var1) {
         GameServer.sendItemsInContainer(var0, var1 == null ? var0.getContainer() : var1);
      }

      @LuaMethod(
         name = "getModDirectoryTable",
         global = true
      )
      public static KahluaTable getModDirectoryTable() {
         KahluaTable var0 = LuaManager.platform.newTable();
         List var1 = getMods();
         int var2 = 1;

         for(int var3 = 0; var3 < var1.size(); ++var3) {
            String var4 = (String)var1.get(var3);
            Double var5 = (double)var2;
            var0.rawset(var5, var4);
            ++var2;
         }

         return var0;
      }

      @LuaMethod(
         name = "getStoryDirectoryTable",
         global = true
      )
      public static KahluaTable getStoryDirectoryTable() {
         KahluaTable var0 = LuaManager.platform.newTable();
         List var1 = getStories();
         int var2 = 1;

         for(int var3 = 0; var3 < var1.size(); ++var3) {
            String var4 = (String)var1.get(var3);
            Double var5 = (double)var2;
            var0.rawset(var5, var4);
            ++var2;
         }

         return var0;
      }

      @LuaMethod(
         name = "getModInfoByID",
         global = true
      )
      public static ChooseGameInfo.Mod getModInfoByID(String var0) {
         try {
            return (new ChooseGameInfo()).getModDetails(var0);
         } catch (Exception var2) {
            var2.printStackTrace();
            return null;
         }
      }

      @LuaMethod(
         name = "getModInfo",
         global = true
      )
      public static ChooseGameInfo.Mod getModInfo(String var0) {
         ChooseGameInfo var1 = new ChooseGameInfo();

         try {
            return var1.readModInfo(var0);
         } catch (Exception var3) {
            Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, var3);
            return null;
         }
      }

      @LuaMethod(
         name = "getMapFoldersForMod",
         global = true
      )
      public static ArrayList getMapFoldersForMod(String var0) {
         try {
            ChooseGameInfo var1 = new ChooseGameInfo();
            ChooseGameInfo.Mod var2 = var1.getModDetails(var0);
            if (var2 == null) {
               return null;
            } else {
               String var3 = var2.getDir() + File.separator + "media" + File.separator + "maps";
               File var4 = new File(var3);
               if (var4.exists() && var4.isDirectory()) {
                  ArrayList var5 = null;
                  DirectoryStream var6 = Files.newDirectoryStream(var4.toPath());
                  Throwable var7 = null;

                  try {
                     Iterator var8 = var6.iterator();

                     while(var8.hasNext()) {
                        Path var9 = (Path)var8.next();
                        if (Files.isDirectory(var9, new LinkOption[0])) {
                           var4 = new File(var3 + File.separator + var9.getFileName().toString() + File.separator + "map.info");
                           if (var4.exists()) {
                              if (var5 == null) {
                                 var5 = new ArrayList();
                              }

                              var5.add(var9.getFileName().toString());
                           }
                        }
                     }
                  } catch (Throwable var18) {
                     var7 = var18;
                     throw var18;
                  } finally {
                     if (var6 != null) {
                        if (var7 != null) {
                           try {
                              var6.close();
                           } catch (Throwable var17) {
                              var7.addSuppressed(var17);
                           }
                        } else {
                           var6.close();
                        }
                     }

                  }

                  return var5;
               } else {
                  return null;
               }
            }
         } catch (Exception var20) {
            var20.printStackTrace();
            return null;
         }
      }

      @LuaMethod(
         name = "spawnpointsExistsForMod",
         global = true
      )
      public static boolean spawnpointsExistsForMod(String var0, String var1) {
         try {
            ChooseGameInfo var2 = new ChooseGameInfo();
            ChooseGameInfo.Mod var3 = var2.getModDetails(var0);
            if (var3 == null) {
               return false;
            } else {
               String var4 = var3.getDir() + File.separator + "media" + File.separator + "maps" + File.separator + var1 + File.separator + "spawnpoints.lua";
               return (new File(var4)).exists();
            }
         } catch (Exception var5) {
            var5.printStackTrace();
            return false;
         }
      }

      @LuaMethod(
         name = "getFileSeparator",
         global = true
      )
      public static String getFileSeparator() {
         return File.separator;
      }

      @LuaMethod(
         name = "getStoryInfo",
         global = true
      )
      public static ChooseGameInfo.Story getStoryInfo(String var0) {
         ChooseGameInfo var1 = new ChooseGameInfo();
         ChooseGameInfo.Story var2 = var1.new Story(var0);

         try {
            var1.getStoryDetails(var2, var0);
         } catch (FileNotFoundException var4) {
            Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, var4);
         }

         return var2;
      }

      @LuaMethod(
         name = "getStorySavedTable",
         global = true
      )
      public static KahluaTable getStorySavedTable() {
         KahluaTable var0 = LuaManager.platform.newTable();
         List var1 = getStories();
         int var2 = 1;

         for(int var3 = 0; var3 < var1.size(); ++var3) {
            String var4 = (String)var1.get(var3);
            Core.GameMode = null;
            File var5 = new File(GameWindow.getGameModeCacheDir() + File.separator + var4);
            if (var5.exists() && var5.list() != null) {
               Double var6 = (double)var2;
               var0.rawset(var6, var4);
               ++var2;
            }
         }

         if (var2 == 1) {
            return null;
         } else {
            return var0;
         }
      }

      @LuaMethod(
         name = "getScriptManager",
         global = true
      )
      public static ScriptManager getScriptManager() {
         return ScriptManager.instance;
      }

      @LuaMethod(
         name = "checkSaveFolderExists",
         global = true
      )
      public static boolean checkSaveFolderExists(String var0) {
         File var1 = new File(GameWindow.getSaveDir() + File.separator + var0);
         return var1.exists();
      }

      @LuaMethod(
         name = "getAbsoluteSaveFolderName",
         global = true
      )
      public static String getAbsoluteSaveFolderName(String var0) {
         File var1 = new File(GameWindow.getSaveDir() + File.separator + var0);
         return var1.getAbsolutePath();
      }

      @LuaMethod(
         name = "checkSaveFileExists",
         global = true
      )
      public static boolean checkSaveFileExists(String var0) {
         File var1 = new File(GameWindow.getGameModeCacheDir() + Core.GameSaveWorld + File.separator + var0);
         return var1.exists();
      }

      @LuaMethod(
         name = "fileExists",
         global = true
      )
      public static boolean fileExists(String var0) {
         String var1 = var0.replace("/", File.separator);
         var1 = var1.replace("\\", File.separator);
         File var2 = new File(ZomboidFileSystem.instance.getString(var1));
         return var2.exists();
      }

      @LuaMethod(
         name = "serverFileExists",
         global = true
      )
      public static boolean serverFileExists(String var0) {
         String var1 = var0.replace("/", File.separator);
         var1 = var1.replace("\\", File.separator);
         File var2 = new File(GameWindow.getCacheDir() + File.separator + "Server" + File.separator + var1);
         return var2.exists();
      }

      @LuaMethod(
         name = "takeScreenshot",
         global = true
      )
      public static void takeScreenshot() {
         Core.getInstance().TakeFullScreenshot((String)null);
      }

      @LuaMethod(
         name = "takeScreenshot",
         global = true
      )
      public static void takeScreenshot(String var0) {
         Core.getInstance().TakeFullScreenshot(var0);
      }

      @LuaMethod(
         name = "instanceItem",
         global = true
      )
      public static InventoryItem instanceItem(Item var0) {
         return InventoryItemFactory.CreateItem(var0.module.name + "." + var0.name);
      }

      @LuaMethod(
         name = "instanceItem",
         global = true
      )
      public static InventoryItem instanceItem(String var0) {
         return InventoryItemFactory.CreateItem(var0);
      }

      @LuaMethod(
         name = "createNewScriptItem",
         global = true
      )
      public static Item createNewScriptItem(String var0, String var1, String var2, String var3, String var4) {
         Item var5 = new Item();
         var5.module = ScriptManager.instance.getModule(var0);
         var5.module.ItemMap.put(var1, var5);
         var5.Icon = "Item_" + var4;
         var5.DisplayName = var2;
         var5.name = var1;

         try {
            var5.type = Item.Type.valueOf(var3);
         } catch (Exception var7) {
         }

         return var5;
      }

      @LuaMethod(
         name = "cloneItemType",
         global = true
      )
      public static Item cloneItemType(String var0, String var1) {
         Item var2 = ScriptManager.instance.FindItem(var1);
         Item var3 = new Item();
         var3.module = var2.getModule();
         var3.module.ItemMap.put(var0, var3);
         return var3;
      }

      @LuaMethod(
         name = "require",
         global = true
      )
      public static Object require(String var0) {
         String var1 = var0;
         if (!var0.endsWith(".lua")) {
            var1 = var0 + ".lua";
         }

         for(int var2 = 0; var2 < LuaManager.paths.size(); ++var2) {
            String var3 = (String)LuaManager.paths.get(var2);
            String var4 = ZomboidFileSystem.instance.getAbsolutePath(var3 + var1);
            if (var4 != null) {
               return LuaManager.RunLua(ZomboidFileSystem.instance.getString(var4));
            }
         }

         DebugLog.log("require(\"" + var0 + "\") failed");
         return null;
      }

      @LuaMethod(
         name = "getRenderer",
         global = true
      )
      public static SpriteRenderer getRenderer() {
         return SpriteRenderer.instance;
      }

      @LuaMethod(
         name = "getGameTime",
         global = true
      )
      public static GameTime getGameTime() {
         return GameTime.instance;
      }

      @LuaMethod(
         name = "getWorld",
         global = true
      )
      public static IsoWorld getWorld() {
         return IsoWorld.instance;
      }

      @LuaMethod(
         name = "getCell",
         global = true
      )
      public static IsoCell getCell() {
         return IsoWorld.instance.getCell();
      }

      @LuaMethod(
         name = "getSandboxOptions",
         global = true
      )
      public static SandboxOptions getSandboxOptions() {
         return SandboxOptions.instance;
      }

      @LuaMethod(
         name = "getFileOutput",
         global = true
      )
      public static DataOutputStream getFileOutput(String var0) {
         if (var0.contains("..")) {
            DebugLog.log("relative paths not allowed");
            return null;
         } else {
            String var1 = LuaManager.getLuaCacheDir() + File.separator + var0;
            var1 = var1.replace("/", File.separator);
            var1 = var1.replace("\\", File.separator);
            String var2 = var1.substring(0, var1.lastIndexOf(File.separator));
            var2 = var2.replace("\\", "/");
            File var3 = new File(var2);
            if (!var3.exists()) {
               var3.mkdirs();
            }

            File var4 = new File(var1);

            try {
               outStream = new FileOutputStream(var4);
            } catch (FileNotFoundException var6) {
               Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, var6);
            }

            DataOutputStream var5 = new DataOutputStream(outStream);
            return var5;
         }
      }

      @LuaMethod(
         name = "getAllSavedPlayers",
         global = true
      )
      public static List getAllSavedPlayers() throws IOException {
         ArrayList var0 = new ArrayList();
         String var1 = LuaManager.getLuaCacheDir() + File.separator + "Players";
         var1 = var1.replace("/", File.separator);
         var1 = var1.replace("\\", File.separator);
         File var2 = new File(var1);
         if (!var2.exists()) {
            var2.mkdir();
         }

         File[] var3 = var2.listFiles();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            File var6 = var3[var5];
            var0.add(new BufferedReader(new FileReader(var6)));
         }

         return var0;
      }

      @LuaMethod(
         name = "getSandboxPresets",
         global = true
      )
      public static List getSandboxPresets() throws IOException {
         ArrayList var0 = new ArrayList();
         String var1 = LuaManager.getSandboxCacheDir();
         File var2 = new File(var1);
         if (!var2.exists()) {
            var2.mkdir();
         }

         File[] var3 = var2.listFiles();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            File var6 = var3[var5];
            if (var6.getName().endsWith(".cfg")) {
               var0.add(var6.getName().replace(".cfg", ""));
            }
         }

         Collections.sort(var0);
         return var0;
      }

      @LuaMethod(
         name = "deleteSandboxPreset",
         global = true
      )
      public static void deleteSandboxPreset(String var0) {
         if (var0.contains("..")) {
            DebugLog.log("relative paths not allowed");
         } else {
            String var1 = LuaManager.getSandboxCacheDir() + File.separator + var0 + ".cfg";
            File var2 = new File(var1);
            if (var2.exists()) {
               var2.delete();
            }

         }
      }

      @LuaMethod(
         name = "getFileReader",
         global = true
      )
      public static BufferedReader getFileReader(String var0, boolean var1) throws IOException {
         if (var0.contains("..")) {
            DebugLog.log("relative paths not allowed");
            return null;
         } else {
            String var2 = LuaManager.getLuaCacheDir() + File.separator + var0;
            var2 = var2.replace("/", File.separator);
            var2 = var2.replace("\\", File.separator);
            File var3 = new File(var2);
            if (!var3.exists() && var1) {
               var3.createNewFile();
            }

            if (var3.exists()) {
               BufferedReader var4 = null;

               try {
                  var4 = new BufferedReader(new FileReader(var3));
               } catch (IOException var6) {
                  Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, var6);
               }

               return var4;
            } else {
               return null;
            }
         }
      }

      @LuaMethod(
         name = "getModFileReader",
         global = true
      )
      public static BufferedReader getModFileReader(String var0, String var1, boolean var2) throws IOException {
         if (!var1.isEmpty() && !var1.contains("..") && !(new File(var1)).isAbsolute()) {
            String var3 = GameWindow.getCacheDir() + File.separator + "mods" + File.separator + var1;
            if (var0 != null) {
               ChooseGameInfo var4 = new ChooseGameInfo();
               ChooseGameInfo.Mod var5 = var4.getModDetails(var0);
               if (var5 == null) {
                  return null;
               }

               var3 = var5.getDir() + File.separator + var1;
            }

            var3 = var3.replace("/", File.separator);
            var3 = var3.replace("\\", File.separator);
            File var8 = new File(var3);
            if (!var8.exists() && var2) {
               String var9 = var3.substring(0, var3.lastIndexOf(File.separator));
               File var6 = new File(var9);
               if (!var6.exists()) {
                  var6.mkdirs();
               }

               var8.createNewFile();
            }

            if (var8.exists()) {
               BufferedReader var10 = null;

               try {
                  var10 = new BufferedReader(new FileReader(var8));
               } catch (IOException var7) {
                  Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, var7);
               }

               return var10;
            } else {
               return null;
            }
         } else {
            return null;
         }
      }

      @LuaMethod(
         name = "getModFileWriter",
         global = true
      )
      public static LuaManager.GlobalObject.LuaFileWriter getModFileWriter(String var0, String var1, boolean var2, boolean var3) {
         if (!var1.isEmpty() && !var1.contains("..") && !(new File(var1)).isAbsolute()) {
            ChooseGameInfo var4 = new ChooseGameInfo();
            ChooseGameInfo.Mod var5 = var4.getModDetails(var0);
            if (var5 == null) {
               return null;
            } else {
               String var6 = var5.getDir() + File.separator + var1;
               var6 = var6.replace("/", File.separator);
               var6 = var6.replace("\\", File.separator);
               String var7 = var6.substring(0, var6.lastIndexOf(File.separator));
               File var8 = new File(var7);
               if (!var8.exists()) {
                  var8.mkdirs();
               }

               File var9 = new File(var6);
               if (!var9.exists() && var2) {
                  try {
                     var9.createNewFile();
                  } catch (IOException var13) {
                     Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, var13);
                  }
               }

               FileWriter var10 = null;

               try {
                  var10 = new FileWriter(var9, var3);
               } catch (IOException var12) {
                  Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, var12);
               }

               LuaManager.GlobalObject.LuaFileWriter var11 = new LuaManager.GlobalObject.LuaFileWriter();
               var11.writer = var10;
               return var11;
            }
         } else {
            return null;
         }
      }

      @LuaMethod(
         name = "updateFire",
         global = true
      )
      public static void updateFire() {
         IsoFireManager.Update();
      }

      @LuaMethod(
         name = "deletePlayerSave",
         global = true
      )
      public static void deletePlayerSave(String var0) {
         String var1 = LuaManager.getLuaCacheDir() + File.separator + "Players" + File.separator + "player" + var0 + ".txt";
         var1 = var1.replace("/", File.separator);
         var1 = var1.replace("\\", File.separator);
         File var2 = new File(var1);
         var2.delete();
      }

      @LuaMethod(
         name = "getControllerCount",
         global = true
      )
      public static int getControllerCount() {
         return GameWindow.GameInput.getControllerCount() + (Core.bDebug ? 3 : 0);
      }

      @LuaMethod(
         name = "getControllerName",
         global = true
      )
      public static String getControllerName(int var0) {
         if (var0 >= 0 && var0 < GameWindow.GameInput.getControllerCount() + (Core.bDebug ? 3 : 0)) {
            if (Core.bDebug) {
               if (var0 == GameWindow.GameInput.getControllerCount()) {
                  return "My Funky Headset";
               }

               if (var0 == GameWindow.GameInput.getControllerCount() + 1) {
                  return "My Awesome Keyboard";
               }

               if (var0 == GameWindow.GameInput.getControllerCount() + 2) {
                  return "My Integrated Microphone";
               }
            }

            return GameWindow.GameInput.getController(var0).getName();
         } else {
            return "???";
         }
      }

      @LuaMethod(
         name = "getControllerAxisCount",
         global = true
      )
      public static int getControllerAxisCount(int var0) {
         return var0 >= 0 && var0 < GameWindow.GameInput.getControllerCount() ? GameWindow.GameInput.getController(var0).getAxisCount() : 0;
      }

      @LuaMethod(
         name = "getControllerAxisValue",
         global = true
      )
      public static float getControllerAxisValue(int var0, int var1) {
         if (var0 >= 0 && var0 < GameWindow.GameInput.getControllerCount()) {
            return var1 >= 0 && var1 < GameWindow.GameInput.getAxisCount(var0) ? GameWindow.GameInput.getController(var0).getAxisValue(var1) : 0.0F;
         } else {
            return 0.0F;
         }
      }

      @LuaMethod(
         name = "getControllerDeadZone",
         global = true
      )
      public static float getControllerDeadZone(int var0, int var1) {
         if (var0 >= 0 && var0 < GameWindow.GameInput.getControllerCount()) {
            return var1 >= 0 && var1 < GameWindow.GameInput.getAxisCount(var0) ? JoypadManager.instance.getDeadZone(var0, var1) : 0.0F;
         } else {
            return 0.0F;
         }
      }

      @LuaMethod(
         name = "setControllerDeadZone",
         global = true
      )
      public static void setControllerDeadZone(int var0, int var1, float var2) {
         if (var0 >= 0 && var0 < GameWindow.GameInput.getControllerCount()) {
            if (var1 >= 0 && var1 < GameWindow.GameInput.getAxisCount(var0)) {
               JoypadManager.instance.setDeadZone(var0, var1, var2);
            }
         }
      }

      @LuaMethod(
         name = "saveControllerSettings",
         global = true
      )
      public static void saveControllerSettings(int var0) {
         if (var0 >= 0 && var0 < GameWindow.GameInput.getControllerCount()) {
            JoypadManager.instance.saveControllerSettings(var0);
         }
      }

      @LuaMethod(
         name = "getControllerButtonCount",
         global = true
      )
      public static int getControllerButtonCount(int var0) {
         return var0 >= 0 && var0 < GameWindow.GameInput.getControllerCount() ? GameWindow.GameInput.getController(var0).getButtonCount() : 0;
      }

      @LuaMethod(
         name = "getControllerPovX",
         global = true
      )
      public static float getControllerPovX(int var0) {
         return var0 >= 0 && var0 < GameWindow.GameInput.getControllerCount() ? GameWindow.GameInput.getController(var0).getPovX() : 0.0F;
      }

      @LuaMethod(
         name = "getControllerPovY",
         global = true
      )
      public static float getControllerPovY(int var0) {
         return var0 >= 0 && var0 < GameWindow.GameInput.getControllerCount() ? GameWindow.GameInput.getController(var0).getPovY() : 0.0F;
      }

      @LuaMethod(
         name = "reloadControllerConfigFiles",
         global = true
      )
      public static void reloadControllerConfigFiles() {
         JoypadManager.instance.reloadControllerFiles();
      }

      @LuaMethod(
         name = "isJoypadPressed",
         global = true
      )
      public static boolean isJoypadPressed(int var0, int var1) {
         return GameWindow.GameInput.isButtonPressed(var1, var0);
      }

      @LuaMethod(
         name = "isJoypadDown",
         global = true
      )
      public static boolean isJoypadDown(int var0) {
         return JoypadManager.instance.isDownPressed(var0);
      }

      @LuaMethod(
         name = "getJoypadAimingAxisX",
         global = true
      )
      public static float getJoypadAimingAxisX(int var0) {
         return JoypadManager.instance.getAimingAxisX(var0);
      }

      @LuaMethod(
         name = "getJoypadAimingAxisY",
         global = true
      )
      public static float getJoypadAimingAxisY(int var0) {
         return JoypadManager.instance.getAimingAxisY(var0);
      }

      @LuaMethod(
         name = "getJoypadMovementAxisX",
         global = true
      )
      public static float getJoypadMovementAxisX(int var0) {
         return JoypadManager.instance.getMovementAxisX(var0);
      }

      @LuaMethod(
         name = "getJoypadMovementAxisY",
         global = true
      )
      public static float getJoypadMovementAxisY(int var0) {
         return JoypadManager.instance.getMovementAxisY(var0);
      }

      @LuaMethod(
         name = "getJoypadAButton",
         global = true
      )
      public static int getJoypadAButton(int var0) {
         JoypadManager.Joypad var1 = JoypadManager.instance.getFromControllerID(var0);
         return var1 != null ? var1.getAButton() : -1;
      }

      @LuaMethod(
         name = "getJoypadBButton",
         global = true
      )
      public static int getJoypadBButton(int var0) {
         JoypadManager.Joypad var1 = JoypadManager.instance.getFromControllerID(var0);
         return var1 != null ? var1.getBButton() : -1;
      }

      @LuaMethod(
         name = "getJoypadXButton",
         global = true
      )
      public static int getJoypadXButton(int var0) {
         JoypadManager.Joypad var1 = JoypadManager.instance.getFromControllerID(var0);
         return var1 != null ? var1.getXButton() : -1;
      }

      @LuaMethod(
         name = "getJoypadYButton",
         global = true
      )
      public static int getJoypadYButton(int var0) {
         JoypadManager.Joypad var1 = JoypadManager.instance.getFromControllerID(var0);
         return var1 != null ? var1.getYButton() : -1;
      }

      @LuaMethod(
         name = "getJoypadLBumper",
         global = true
      )
      public static int getJoypadLBumper(int var0) {
         JoypadManager.Joypad var1 = JoypadManager.instance.getFromControllerID(var0);
         return var1 != null ? var1.getLBumper() : -1;
      }

      @LuaMethod(
         name = "getJoypadRBumper",
         global = true
      )
      public static int getJoypadRBumper(int var0) {
         JoypadManager.Joypad var1 = JoypadManager.instance.getFromControllerID(var0);
         return var1 != null ? var1.getRBumper() : -1;
      }

      @LuaMethod(
         name = "getJoypadBackButton",
         global = true
      )
      public static int getJoypadBackButton(int var0) {
         JoypadManager.Joypad var1 = JoypadManager.instance.getFromControllerID(var0);
         return var1 != null ? var1.getBackButton() : -1;
      }

      @LuaMethod(
         name = "getJoypadStartButton",
         global = true
      )
      public static int getJoypadStartButton(int var0) {
         JoypadManager.Joypad var1 = JoypadManager.instance.getFromControllerID(var0);
         return var1 != null ? var1.getStartButton() : -1;
      }

      @LuaMethod(
         name = "wasMouseActiveMoreRecentlyThanJoypad",
         global = true
      )
      public static boolean wasMouseActiveMoreRecentlyThanJoypad() {
         if (IsoPlayer.instance == null) {
            return true;
         } else if (IsoPlayer.instance.getJoypadBind() == -1) {
            return true;
         } else {
            return JoypadManager.instance.getLastActivity(IsoPlayer.instance.getJoypadBind()) < Mouse.lastActivity;
         }
      }

      @LuaMethod(
         name = "reactivateJoypadAfterResetLua",
         global = true
      )
      public static boolean reactivateJoypadAfterResetLua() {
         if (GameWindow.ActivatedJoyPad != null) {
            LuaEventManager.triggerEvent("OnJoypadActivateUI", GameWindow.ActivatedJoyPad.getID());
            return true;
         } else {
            return false;
         }
      }

      private static void addPlayerToWorld(int var0, IsoPlayer var1, boolean var2) {
         if (IsoPlayer.players[var0] != null) {
            IsoPlayer.players[var0].setModel((String)null);
            IsoPlayer.players[var0].updateUsername();
            IsoPlayer.players[var0] = null;
         }

         var1.PlayerIndex = var0;
         if (var2) {
            var1.applyTraits(IsoWorld.instance.getLuaTraits());
            var1.createKeyRing();
            ProfessionFactory.Profession var3 = ProfessionFactory.getProfession(var1.getDescriptor().getProfession());
            Iterator var4;
            String var5;
            if (var3 != null && !var3.getFreeRecipes().isEmpty()) {
               var4 = var3.getFreeRecipes().iterator();

               while(var4.hasNext()) {
                  var5 = (String)var4.next();
                  var1.getKnownRecipes().add(var5);
               }
            }

            var4 = IsoWorld.instance.getLuaTraits().iterator();

            label43:
            while(true) {
               TraitFactory.Trait var6;
               do {
                  do {
                     if (!var4.hasNext()) {
                        LuaEventManager.triggerEvent("OnNewGame", var1, var1.getCurrentSquare());
                        break label43;
                     }

                     var5 = (String)var4.next();
                     var6 = TraitFactory.getTrait(var5);
                  } while(var6 == null);
               } while(var6.getFreeRecipes().isEmpty());

               Iterator var7 = var6.getFreeRecipes().iterator();

               while(var7.hasNext()) {
                  String var8 = (String)var7.next();
                  var1.getKnownRecipes().add(var8);
               }
            }
         }

         IsoPlayer.numPlayers = Math.max(IsoPlayer.numPlayers, var0 + 1);
         IsoWorld.instance.AddCoopPlayers.add(new AddCoopPlayer(var1));
         if (var0 == 0) {
            IsoPlayer.setInstance(var1);
         }

      }

      @LuaMethod(
         name = "toInt",
         global = true
      )
      public static int toInt(double var0) {
         return (int)var0;
      }

      @LuaMethod(
         name = "getClientUsername",
         global = true
      )
      public static String getClientUsername() {
         return GameClient.bClient ? GameClient.username : null;
      }

      @LuaMethod(
         name = "setPlayerJoypad",
         global = true
      )
      public static void setPlayerJoypad(int var0, int var1, IsoPlayer var2, String var3) {
         if (IsoPlayer.players[var0] == null || IsoPlayer.players[var0].isDead()) {
            boolean var4 = var2 == null;
            if (var2 == null) {
               IsoPlayer var5 = IsoPlayer.instance;
               IsoWorld var6 = IsoWorld.instance;
               int var7 = var6.getLuaPosX() + 300 * var6.getLuaSpawnCellX();
               int var8 = var6.getLuaPosY() + 300 * var6.getLuaSpawnCellY();
               int var9 = var6.getLuaPosZ();
               DebugLog.log("coop player spawning at " + var7 + "," + var8 + "," + var9);
               var2 = new IsoPlayer(var6.CurrentCell, var6.getLuaPlayerDesc(), var7, var8, var9);
               IsoPlayer.instance = var5;
               var6.CurrentCell.getAddList().remove(var2);
               var6.CurrentCell.getObjectList().remove(var2);
               var2.SaveFileName = IsoPlayer.getUniqueFileName();
            }

            if (GameClient.bClient) {
               if (var3 != null) {
                  assert var0 != 0;

                  var2.username = var3;
                  var2.getModData().rawset("username", var3);
               } else {
                  assert var0 == 0;

                  var2.username = GameClient.username;
               }
            }

            addPlayerToWorld(var0, var2, var4);
         }

         var2.JoypadBind = var1;
         JoypadManager.instance.assignJoypad(var1, var0);
      }

      @LuaMethod(
         name = "setPlayerMouse",
         global = true
      )
      public static void setPlayerMouse(IsoPlayer var0) {
         byte var1 = 0;
         boolean var2 = var0 == null;
         if (var0 == null) {
            IsoPlayer var3 = IsoPlayer.instance;
            IsoWorld var4 = IsoWorld.instance;
            int var5 = var4.getLuaPosX() + 300 * var4.getLuaSpawnCellX();
            int var6 = var4.getLuaPosY() + 300 * var4.getLuaSpawnCellY();
            int var7 = var4.getLuaPosZ();
            DebugLog.log("coop player spawning at " + var5 + "," + var6 + "," + var7);
            var0 = new IsoPlayer(var4.CurrentCell, var4.getLuaPlayerDesc(), var5, var6, var7);
            IsoPlayer.instance = var3;
            var4.CurrentCell.getAddList().remove(var0);
            var4.CurrentCell.getObjectList().remove(var0);
            var0.SaveFileName = null;
         }

         if (GameClient.bClient) {
            var0.username = GameClient.username;
         }

         addPlayerToWorld(var1, var0, var2);
      }

      @LuaMethod(
         name = "isJoypadUp",
         global = true
      )
      public static boolean isJoypadUp(int var0) {
         return JoypadManager.instance.isUpPressed(var0);
      }

      @LuaMethod(
         name = "isJoypadLeft",
         global = true
      )
      public static boolean isJoypadLeft(int var0) {
         return JoypadManager.instance.isLeftPressed(var0);
      }

      @LuaMethod(
         name = "isJoypadRight",
         global = true
      )
      public static boolean isJoypadRight(int var0) {
         return JoypadManager.instance.isRightPressed(var0);
      }

      @LuaMethod(
         name = "getButtonCount",
         global = true
      )
      public static int getButtonCount(int var0) {
         return GameWindow.GameInput.getController(var0).getButtonCount();
      }

      @LuaMethod(
         name = "getFileWriter",
         global = true
      )
      public static LuaManager.GlobalObject.LuaFileWriter getFileWriter(String var0, boolean var1, boolean var2) {
         if (var0.contains("..")) {
            DebugLog.log("relative paths not allowed");
            return null;
         } else {
            String var3 = LuaManager.getLuaCacheDir() + File.separator + var0;
            var3 = var3.replace("/", File.separator);
            var3 = var3.replace("\\", File.separator);
            String var4 = var3.substring(0, var3.lastIndexOf(File.separator));
            var4 = var4.replace("\\", "/");
            File var5 = new File(var4);
            if (!var5.exists()) {
               var5.mkdirs();
            }

            File var6 = new File(var3);
            if (!var6.exists() && var1) {
               try {
                  var6.createNewFile();
               } catch (IOException var10) {
                  Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, var10);
               }
            }

            FileWriter var7 = null;

            try {
               var7 = new FileWriter(var6, var2);
            } catch (IOException var9) {
               Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, var9);
            }

            LuaManager.GlobalObject.LuaFileWriter var8 = new LuaManager.GlobalObject.LuaFileWriter();
            var8.writer = var7;
            return var8;
         }
      }

      @LuaMethod(
         name = "getSandboxFileWriter",
         global = true
      )
      public static LuaManager.GlobalObject.LuaFileWriter getSandboxFileWriter(String var0, boolean var1, boolean var2) {
         String var3 = LuaManager.getSandboxCacheDir() + File.separator + var0;
         var3 = var3.replace("/", File.separator);
         var3 = var3.replace("\\", File.separator);
         String var4 = var3.substring(0, var3.lastIndexOf(File.separator));
         var4 = var4.replace("\\", "/");
         File var5 = new File(var4);
         if (!var5.exists()) {
            var5.mkdirs();
         }

         File var6 = new File(var3);
         if (!var6.exists() && var1) {
            try {
               var6.createNewFile();
            } catch (IOException var10) {
               Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, var10);
            }
         }

         FileWriter var7 = null;

         try {
            var7 = new FileWriter(var6, var2);
         } catch (IOException var9) {
            Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, var9);
         }

         LuaManager.GlobalObject.LuaFileWriter var8 = new LuaManager.GlobalObject.LuaFileWriter();
         var8.writer = var7;
         return var8;
      }

      @LuaMethod(
         name = "createStory",
         global = true
      )
      public static void createStory(String var0) {
         Core.GameMode = var0;
         String var1 = GameWindow.getGameModeCacheDir();
         var1 = var1.replace("/", File.separator);
         var1 = var1.replace("\\", File.separator);
         int var2 = 1;
         File var3 = null;
         boolean var4 = false;

         while(!var4) {
            var3 = new File(var1 + File.separator + "Game" + var2);
            if (!var3.exists()) {
               var4 = true;
            } else {
               ++var2;
            }
         }

         Core.GameSaveWorld = "newstory";
         ScriptManager.instance.Trigger("OnPostLoadStory");
      }

      @LuaMethod(
         name = "createWorld",
         global = true
      )
      public static void createWorld(String var0) {
         if (var0 == null || var0.isEmpty()) {
            var0 = "blah";
         }

         var0 = sanitizeWorldName(var0);
         String var1 = GameWindow.getGameModeCacheDir() + File.separator + var0 + File.separator;
         var1 = var1.replace("/", File.separator);
         var1 = var1.replace("\\", File.separator);
         String var2 = var1.substring(0, var1.lastIndexOf(File.separator));
         var2 = var2.replace("\\", "/");
         File var3 = new File(var2);
         if (!var3.exists() && !Core.getInstance().isNoSave()) {
            var3.mkdirs();
         }

         Core.GameSaveWorld = var0;
      }

      @LuaMethod(
         name = "sanitizeWorldName",
         global = true
      )
      public static String sanitizeWorldName(String var0) {
         return var0.replace(" ", "_").replace("/", "").replace("\\", "").replace("?", "").replace("*", "").replace("<", "").replace(">", "").replace(":", "").replace("|", "").trim();
      }

      @LuaMethod(
         name = "forceChangeState",
         global = true
      )
      public static void forceChangeState(GameState var0) {
         GameWindow.states.forceNextState(var0);
      }

      @LuaMethod(
         name = "endFileOutput",
         global = true
      )
      public static void endFileOutput() {
         if (outStream != null) {
            try {
               outStream.close();
            } catch (IOException var1) {
               Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, var1);
            }
         }

         outStream = null;
      }

      @LuaMethod(
         name = "getFileInput",
         global = true
      )
      public static DataInputStream getFileInput(String var0) throws IOException {
         String var1 = LuaManager.getLuaCacheDir() + File.separator + var0;
         var1 = var1.replace("/", File.separator);
         var1 = var1.replace("\\", File.separator);
         File var2 = new File(var1);
         if (var2.exists()) {
            try {
               inStream = new FileInputStream(var2);
            } catch (FileNotFoundException var4) {
               Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, var4);
            }

            DataInputStream var3 = new DataInputStream(inStream);
            return var3;
         } else {
            return null;
         }
      }

      @LuaMethod(
         name = "getGameFilesInput",
         global = true
      )
      public static DataInputStream getGameFilesInput(String var0) {
         String var1 = var0.replace("/", File.separator);
         var1 = var1.replace("\\", File.separator);
         File var2 = new File(ZomboidFileSystem.instance.getString(var1));
         if (var2.exists()) {
            try {
               inStream = new FileInputStream(var2);
            } catch (FileNotFoundException var4) {
               Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, var4);
            }

            DataInputStream var3 = new DataInputStream(inStream);
            return var3;
         } else {
            return null;
         }
      }

      @LuaMethod(
         name = "getGameFilesTextInput",
         global = true
      )
      public static BufferedReader getGameFilesTextInput(String var0) {
         if (!Core.getInstance().getDebug()) {
            return null;
         } else {
            String var1 = var0.replace("/", File.separator);
            var1 = var1.replace("\\", File.separator);
            File var2 = new File(var1);
            if (var2.exists()) {
               try {
                  inFileReader = new FileReader(var0);
                  inBufferedReader = new BufferedReader(inFileReader);
                  return inBufferedReader;
               } catch (FileNotFoundException var4) {
                  Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, var4);
               }
            }

            return null;
         }
      }

      @LuaMethod(
         name = "endTextFileInput",
         global = true
      )
      public static void endTextFileInput() {
         if (inBufferedReader != null) {
            try {
               inBufferedReader.close();
               inFileReader.close();
            } catch (IOException var1) {
               Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, var1);
            }
         }

         inBufferedReader = null;
         inFileReader = null;
      }

      @LuaMethod(
         name = "endFileInput",
         global = true
      )
      public static void endFileInput() {
         if (inStream != null) {
            try {
               inStream.close();
            } catch (IOException var1) {
               Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, var1);
            }
         }

         inStream = null;
      }

      @LuaMethod(
         name = "getLineNumber",
         global = true
      )
      public static int getLineNumber(LuaCallFrame var0) {
         if (var0.closure == null) {
            return 0;
         } else {
            int var1 = var0.pc;
            if (var1 < 0) {
               var1 = 0;
            }

            if (var1 >= var0.closure.prototype.lines.length) {
               var1 = var0.closure.prototype.lines.length - 1;
            }

            return var0.closure.prototype.lines[var1];
         }
      }

      @LuaMethod(
         name = "ZombRand",
         global = true
      )
      public static double ZombRand(double var0) {
         if (var0 == 0.0D) {
            return 0.0D;
         } else {
            return var0 < 0.0D ? (double)(-Rand.Next(-((long)var0), Rand.randlua)) : (double)Rand.Next((long)var0, Rand.randlua);
         }
      }

      @LuaMethod(
         name = "ZombRandBetween",
         global = true
      )
      public static double ZombRandBetween(double var0, double var2) {
         return (double)Rand.Next((long)var0, (long)var2, Rand.randlua);
      }

      @LuaMethod(
         name = "ZombRand",
         global = true
      )
      public static double ZombRand(double var0, double var2) {
         return (double)Rand.Next((int)var0, (int)var2, Rand.randlua);
      }

      @LuaMethod(
         name = "ZombRandFloat",
         global = true
      )
      public static float ZombRandFloat(float var0, float var1) {
         return Rand.Next(var0, var1, Rand.randlua);
      }

      @LuaMethod(
         name = "getShortenedFilename",
         global = true
      )
      public static String getShortenedFilename(String var0) {
         return var0.substring(var0.indexOf("lua/") + 4);
      }

      @LuaMethod(
         name = "isKeyDown",
         global = true
      )
      public static boolean isKeyDown(int var0) {
         return GameKeyboard.isKeyDown(var0);
      }

      @LuaMethod(
         name = "getFMODSoundBank",
         global = true
      )
      public static BaseSoundBank getFMODSoundBank() {
         return BaseSoundBank.instance;
      }

      @LuaMethod(
         name = "isSoundPlaying",
         global = true
      )
      public static boolean isSoundPlaying(Object var0) {
         return var0 instanceof Double ? FMODManager.instance.isPlaying(((Double)var0).longValue()) : false;
      }

      @LuaMethod(
         name = "stopSound",
         global = true
      )
      public static void stopSound(long var0) {
         FMODManager.instance.stopSound(var0);
      }

      @LuaMethod(
         name = "isShiftKeyDown",
         global = true
      )
      public static boolean isShiftKeyDown() {
         return GameKeyboard.isKeyDown(42) || GameKeyboard.isKeyDown(54);
      }

      @LuaMethod(
         name = "isCtrlKeyDown",
         global = true
      )
      public static boolean isCtrlKeyDown() {
         return GameKeyboard.isKeyDown(29) || GameKeyboard.isKeyDown(157);
      }

      @LuaMethod(
         name = "getCore",
         global = true
      )
      public static Core getCore() {
         return Core.getInstance();
      }

      @LuaMethod(
         name = "getDebugOptions",
         global = true
      )
      public static DebugOptions getDebugOptions() {
         return DebugOptions.instance;
      }

      @LuaMethod(
         name = "setShowPausedMessage",
         global = true
      )
      public static void setShowPausedMessage(boolean var0) {
         UIManager.setShowPausedMessage(var0);
      }

      @LuaMethod(
         name = "setGameSpeed",
         global = true
      )
      public static void setGameSpeed(int var0) {
         if (UIManager.getSpeedControls() != null) {
            UIManager.getSpeedControls().SetCurrentGameSpeed(var0);
         }
      }

      @LuaMethod(
         name = "getFilenameOfCallframe",
         global = true
      )
      public static String getFilenameOfCallframe(LuaCallFrame var0) {
         return var0.closure == null ? null : var0.closure.prototype.filename;
      }

      @LuaMethod(
         name = "getFilenameOfClosure",
         global = true
      )
      public static String getFilenameOfClosure(LuaClosure var0) {
         return var0 == null ? null : var0.prototype.filename;
      }

      @LuaMethod(
         name = "getFirstLineOfClosure",
         global = true
      )
      public static int getFirstLineOfClosure(LuaClosure var0) {
         return var0 == null ? 0 : var0.prototype.lines[0];
      }

      @LuaMethod(
         name = "getLocalVarCount",
         global = true
      )
      public static int getLocalVarCount(Coroutine var0) {
         LuaCallFrame var1 = var0.currentCallFrame();
         return var1 == null ? 0 : var1.LocalVarNames.size();
      }

      @LuaMethod(
         name = "isModActive",
         global = true
      )
      public static boolean isModActive(ChooseGameInfo.Mod var0) {
         String var1 = var0.getDir();
         if (var0.getId() != null) {
            var1 = var0.getId();
         }

         return ZomboidFileSystem.instance.mods.contains(var1);
      }

      @LuaMethod(
         name = "openUrl",
         global = true
      )
      public static void openURl(String var0) {
         Desktop var1 = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
         if (var1 != null && var1.isSupported(Action.BROWSE)) {
            try {
               URI var2 = new URI(var0);
               var1.browse(var2);
            } catch (Exception var3) {
               var3.printStackTrace();
            }
         }

      }

      @LuaMethod(
         name = "getActivatedMods",
         global = true
      )
      public static ArrayList getActivatedMods() {
         return ZomboidFileSystem.instance.mods;
      }

      @LuaMethod(
         name = "toggleModActive",
         global = true
      )
      public static void toggleModActive(ChooseGameInfo.Mod var0, boolean var1) {
         String var2 = var0.getDir();
         if (var0.getId() != null) {
            var2 = var0.getId();
         }

         if (var1 && !ZomboidFileSystem.instance.mods.contains(var2)) {
            ZomboidFileSystem.instance.mods.add(var2);
         } else if (!var1) {
            ZomboidFileSystem.instance.mods.remove(var2);
         }

      }

      @LuaMethod(
         name = "saveModsFile",
         global = true
      )
      public static void saveModsFile() {
         ZomboidFileSystem.instance.saveModsFile();
      }

      @LuaMethod(
         name = "getLocalVarName",
         global = true
      )
      public static String getLocalVarName(Coroutine var0, int var1) {
         LuaCallFrame var2 = var0.currentCallFrame();
         return (String)var2.LocalVarNames.get(var1);
      }

      @LuaMethod(
         name = "getLocalVarStack",
         global = true
      )
      public static int getLocalVarStack(Coroutine var0, int var1) {
         LuaCallFrame var2 = var0.currentCallFrame();
         return (Integer)var2.LocalVarToStackMap.get(var2.LocalVarNames.get(var1));
      }

      @LuaMethod(
         name = "getCallframeTop",
         global = true
      )
      public static int getCallframeTop(Coroutine var0) {
         return var0.getCallframeTop();
      }

      @LuaMethod(
         name = "getCoroutineTop",
         global = true
      )
      public static int getCoroutineTop(Coroutine var0) {
         return var0.getTop();
      }

      @LuaMethod(
         name = "getCoroutineObjStack",
         global = true
      )
      public static Object getCoroutineObjStack(Coroutine var0, int var1) {
         return var0.getObjectFromStack(var1);
      }

      @LuaMethod(
         name = "getCoroutineObjStackWithBase",
         global = true
      )
      public static Object getCoroutineObjStackWithBase(Coroutine var0, int var1) {
         return var0.getObjectFromStack(var1 - var0.currentCallFrame().localBase);
      }

      @LuaMethod(
         name = "localVarName",
         global = true
      )
      public static String localVarName(Coroutine var0, int var1) {
         int var2 = var0.getCallframeTop() - 1;
         if (var2 < 0) {
            boolean var3 = false;
         }

         return "";
      }

      @LuaMethod(
         name = "getCoroutineCallframeStack",
         global = true
      )
      public static LuaCallFrame getCoroutineCallframeStack(Coroutine var0, int var1) {
         return var0.getCallFrame(var1);
      }

      @LuaMethod(
         name = "createTile",
         global = true
      )
      public static void createTile(String var0, IsoGridSquare var1) {
         synchronized(IsoWorld.instance.CurrentCell.getSpriteManager().NamedMap) {
            IsoSprite var3 = (IsoSprite)IsoWorld.instance.CurrentCell.SpriteManager.NamedMap.get(var0);
            if (var3 != null) {
               int var4 = 0;
               int var5 = 0;
               int var6 = 0;
               if (var1 != null) {
                  var4 = var1.getX();
                  var5 = var1.getY();
                  var6 = var1.getZ();
               }

               CellLoader.DoTileObjectCreation(var3, var3.getType(), var1, IsoWorld.instance.CurrentCell, var4, var5, var6, (Stack)null, false, var0);
            }
         }
      }

      @LuaMethod(
         name = "getNumClassFunctions",
         global = true
      )
      public static int getNumClassFunctions(Object var0) {
         return var0.getClass().getDeclaredMethods().length;
      }

      @LuaMethod(
         name = "getClassFunction",
         global = true
      )
      public static Method getClassFunction(Object var0, int var1) {
         Method var2 = var0.getClass().getDeclaredMethods()[var1];
         return var2;
      }

      @LuaMethod(
         name = "getNumClassFields",
         global = true
      )
      public static int getNumClassFields(Object var0) {
         return var0.getClass().getDeclaredFields().length;
      }

      @LuaMethod(
         name = "getClassField",
         global = true
      )
      public static Field getClassField(Object var0, int var1) {
         Field var2 = var0.getClass().getDeclaredFields()[var1];
         var2.setAccessible(true);
         return var2;
      }

      @LuaMethod(
         name = "getDirectionTo",
         global = true
      )
      public static IsoDirections getDirectionTo(IsoGameCharacter var0, IsoObject var1) {
         Vector2 var2 = new Vector2(var1.getX(), var1.getY());
         var2.x -= var0.x;
         var2.y -= var0.y;
         return IsoDirections.fromAngle(var2);
      }

      @LuaMethod(
         name = "translatePointXInOverheadMapToWindow",
         global = true
      )
      public static float translatePointXInOverheadMapToWindow(float var0, UIElement var1, float var2, float var3) {
         IngameState.draww = (float)var1.getWidth().intValue();
         return IngameState.translatePointX(var0, var3, var2, 0.0F);
      }

      @LuaMethod(
         name = "translatePointYInOverheadMapToWindow",
         global = true
      )
      public static float translatePointYInOverheadMapToWindow(float var0, UIElement var1, float var2, float var3) {
         IngameState.drawh = (float)var1.getHeight().intValue();
         return IngameState.translatePointY(var0, var3, var2, 0.0F);
      }

      @LuaMethod(
         name = "translatePointXInOverheadMapToWorld",
         global = true
      )
      public static float translatePointXInOverheadMapToWorld(float var0, UIElement var1, float var2, float var3) {
         IngameState.draww = (float)var1.getWidth().intValue();
         return IngameState.invTranslatePointX(var0, var3, var2, 0.0F);
      }

      @LuaMethod(
         name = "translatePointYInOverheadMapToWorld",
         global = true
      )
      public static float translatePointYInOverheadMapToWorld(float var0, UIElement var1, float var2, float var3) {
         IngameState.drawh = (float)var1.getHeight().intValue();
         return IngameState.invTranslatePointY(var0, var3, var2, 0.0F);
      }

      @LuaMethod(
         name = "drawOverheadMap",
         global = true
      )
      public static void drawOverheadMap(UIElement var0, float var1, float var2, float var3) {
         IngameState.renderDebugOverhead2(getCell(), 0, var1, var0.getAbsoluteX().intValue(), var0.getAbsoluteY().intValue(), var2, var3, var0.getWidth().intValue(), var0.getHeight().intValue());
      }

      @LuaMethod(
         name = "assaultPlayer",
         global = true
      )
      public static void assaultPlayer() {
         assert false;

      }

      @LuaMethod(
         name = "zpopNewRenderer",
         global = true
      )
      public static ZombiePopulationRenderer zpopNewRenderer() {
         return new ZombiePopulationRenderer();
      }

      @LuaMethod(
         name = "zpopSpawnTimeToZero",
         global = true
      )
      public static void zpopSpawnTimeToZero(int var0, int var1) {
         ZombiePopulationManager.instance.dbgSpawnTimeToZero(var0, var1);
      }

      @LuaMethod(
         name = "zpopClearZombies",
         global = true
      )
      public static void zpopClearZombies(int var0, int var1) {
         ZombiePopulationManager.instance.dbgClearZombies(var0, var1);
      }

      @LuaMethod(
         name = "zpopSpawnNow",
         global = true
      )
      public static void zpopSpawnNow(int var0, int var1) {
         ZombiePopulationManager.instance.dbgSpawnNow(var0, var1);
      }

      @LuaMethod(
         name = "addVirtualZombie",
         global = true
      )
      public static void addVirtualZombie(int var0, int var1) {
      }

      @LuaMethod(
         name = "setAggroTarget",
         global = true
      )
      public static void setAggroTarget(int var0, int var1, int var2) {
         ZombiePopulationManager.instance.setAggroTarget(var0, var1, var2);
      }

      @LuaMethod(
         name = "debugFullyStreamedIn",
         global = true
      )
      public static void debugFullyStreamedIn(int var0, int var1) {
         IngameState.instance.debugFullyStreamedIn(var0, var1);
      }

      @LuaMethod(
         name = "getClassFieldVal",
         global = true
      )
      public static Object getClassFieldVal(Object var0, Field var1) {
         try {
            return var1.get(var0);
         } catch (Exception var3) {
            return "<private>";
         }
      }

      @LuaMethod(
         name = "getMethodParameter",
         global = true
      )
      public static String getMethodParameter(Method var0, int var1) {
         return var0.getParameterTypes()[var1].getSimpleName();
      }

      @LuaMethod(
         name = "getMethodParameterCount",
         global = true
      )
      public static int getMethodParameterCount(Method var0) {
         return var0.getParameterTypes().length;
      }

      @LuaMethod(
         name = "breakpoint",
         global = true
      )
      public static void breakpoint() {
         boolean var0 = false;
      }

      @LuaMethod(
         name = "getGameSpeed",
         global = true
      )
      public static int getGameSpeed() {
         return UIManager.getSpeedControls() != null ? UIManager.getSpeedControls().getCurrentGameSpeed() : 0;
      }

      @LuaMethod(
         name = "getMouseXScaled",
         global = true
      )
      public static int getMouseXScaled() {
         return Mouse.getX();
      }

      @LuaMethod(
         name = "getMouseYScaled",
         global = true
      )
      public static int getMouseYScaled() {
         return Mouse.getY();
      }

      @LuaMethod(
         name = "getMouseX",
         global = true
      )
      public static int getMouseX() {
         return Mouse.getXA();
      }

      @LuaMethod(
         name = "setMouseXY",
         global = true
      )
      public static void setMouseXY(int var0, int var1) {
         Mouse.setXY(var0, var1);
      }

      @LuaMethod(
         name = "isMouseButtonDown",
         global = true
      )
      public static boolean isMouseButtonDown(int var0) {
         return Mouse.isButtonDown(var0);
      }

      @LuaMethod(
         name = "getMouseY",
         global = true
      )
      public static int getMouseY() {
         return Mouse.getYA();
      }

      @LuaMethod(
         name = "getSoundManager",
         global = true
      )
      public static BaseSoundManager getSoundManager() {
         return SoundManager.instance;
      }

      @LuaMethod(
         name = "getLastPlayedDate",
         global = true
      )
      public static String getLastPlayedDate(String var0) {
         File var1 = new File(GameWindow.getSaveDir() + File.separator + var0);
         if (!var1.exists()) {
            return Translator.getText("UI_LastPlayed") + "???";
         } else {
            Date var2 = new Date(var1.lastModified());
            SimpleDateFormat var3 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String var4 = var3.format(var2);
            return Translator.getText("UI_LastPlayed") + var4;
         }
      }

      @LuaMethod(
         name = "getTextureFromSaveDir",
         global = true
      )
      public static Texture getTextureFromSaveDir(String var0, String var1) {
         TextureID.UseFiltering = true;
         Texture var2 = Texture.getSharedTexture(GameWindow.getSaveDir() + File.separator + var1 + File.separator + var0);
         TextureID.UseFiltering = false;
         return var2;
      }

      @LuaMethod(
         name = "getSaveInfo",
         global = true
      )
      public static KahluaTable getSaveInfo(String var0) {
         if (!var0.contains(File.separator)) {
            var0 = IsoWorld.instance.getGameMode() + File.separator + var0;
         }

         KahluaTable var1 = LuaManager.platform.newTable();
         File var2 = new File(GameWindow.getSaveDir() + File.separator + var0);
         if (var2.exists()) {
            Date var3 = new Date(var2.lastModified());
            SimpleDateFormat var4 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String var5 = var4.format(var3);
            var1.rawset("lastPlayed", var5);
            String[] var6 = var0.split("\\" + File.separator);
            var1.rawset("saveName", var2.getName());
            var1.rawset("gameMode", var6[var6.length - 2]);
         }

         var2 = new File(GameWindow.getSaveDir() + File.separator + var0 + File.separator + "map_ver.bin");
         if (var2.exists()) {
            try {
               FileInputStream var10 = new FileInputStream(var2);
               DataInputStream var11 = new DataInputStream(var10);
               int var12 = var11.readInt();
               var1.rawset("worldVersion", (double)var12);
               String var13;
               if (var12 >= 18) {
                  try {
                     var13 = GameWindow.ReadString(var11);
                     if (var13.equals("DEFAULT")) {
                        var13 = "Muldraugh, KY";
                     }

                     var1.rawset("mapName", var13);
                  } catch (Exception var8) {
                  }
               }

               if (var12 >= 74) {
                  try {
                     var13 = GameWindow.ReadString(var11);
                     var1.rawset("difficulty", var13);
                  } catch (Exception var7) {
                  }
               }

               var11.close();
            } catch (Exception var9) {
               var9.printStackTrace();
            }
         }

         var2 = new File(GameWindow.getSaveDir() + File.separator + var0 + File.separator + "map_p.bin");
         var1.rawset("playerAlive", var2.exists());
         return var1;
      }

      @LuaMethod(
         name = "getServerSavedWorldVersion",
         global = true
      )
      public static int getServerSavedWorldVersion(String var0) {
         File var1 = new File(GameWindow.getSaveDir() + File.separator + var0 + File.separator + "map_t.bin");
         if (var1.exists()) {
            try {
               FileInputStream var2 = new FileInputStream(var1);
               Throwable var3 = null;

               int var10;
               try {
                  DataInputStream var4 = new DataInputStream(var2);
                  Throwable var5 = null;

                  try {
                     byte var6 = var4.readByte();
                     byte var7 = var4.readByte();
                     byte var8 = var4.readByte();
                     byte var9 = var4.readByte();
                     if (var6 != 71 || var7 != 77 || var8 != 84 || var9 != 77) {
                        var10 = 1;
                        return var10;
                     }

                     var10 = var4.readInt();
                  } catch (Throwable var38) {
                     var5 = var38;
                     throw var38;
                  } finally {
                     if (var4 != null) {
                        if (var5 != null) {
                           try {
                              var4.close();
                           } catch (Throwable var37) {
                              var5.addSuppressed(var37);
                           }
                        } else {
                           var4.close();
                        }
                     }

                  }
               } catch (Throwable var40) {
                  var3 = var40;
                  throw var40;
               } finally {
                  if (var2 != null) {
                     if (var3 != null) {
                        try {
                           var2.close();
                        } catch (Throwable var36) {
                           var3.addSuppressed(var36);
                        }
                     } else {
                        var2.close();
                     }
                  }

               }

               return var10;
            } catch (Exception var42) {
               var42.printStackTrace();
            }
         }

         return 0;
      }

      @LuaMethod(
         name = "getMapInfo",
         global = true
      )
      public static KahluaTable getMapInfo(String var0) {
         if (var0.contains(";")) {
            var0 = var0.split(";")[0];
         }

         ChooseGameInfo.Map var1 = ChooseGameInfo.getMapDetails(var0);
         if (var1 == null) {
            return null;
         } else {
            KahluaTable var2 = LuaManager.platform.newTable();
            var2.rawset("description", var1.getDescription());
            var2.rawset("dir", var1.getDirectory());
            KahluaTable var3 = LuaManager.platform.newTable();
            byte var4 = 1;
            Iterator var5 = var1.getLotDirectories().iterator();

            while(var5.hasNext()) {
               String var6 = (String)var5.next();
               var3.rawset((double)var4, var6);
            }

            var2.rawset("lots", var3);
            var2.rawset("thumb", var1.getThumbnail());
            var2.rawset("title", var1.getTitle());
            return var2;
         }
      }

      @LuaMethod(
         name = "getVehicleInfo",
         global = true
      )
      public static KahluaTable getVehicleInfo(BaseVehicle var0) {
         if (var0 == null) {
            return null;
         } else {
            KahluaTable var1 = LuaManager.platform.newTable();
            var1.rawset("name", var0.getScript().getName());
            var1.rawset("weight", var0.getMass());
            var1.rawset("speed", var0.getMaxSpeed());
            var1.rawset("frontEndDurability", Integer.toString(var0.frontEndDurability));
            var1.rawset("rearEndDurability", Integer.toString(var0.rearEndDurability));
            var1.rawset("currentFrontEndDurability", Integer.toString(var0.currentFrontEndDurability));
            var1.rawset("currentRearEndDurability", Integer.toString(var0.currentRearEndDurability));
            var1.rawset("engine_running", var0.isEngineRunning());
            var1.rawset("engine_started", var0.isEngineStarted());
            var1.rawset("engine_quality", var0.getEngineQuality());
            var1.rawset("engine_loudness", var0.getEngineLoudness());
            var1.rawset("engine_power", var0.getEnginePower());
            var1.rawset("battery_isset", var0.getBattery() != null);
            var1.rawset("battery_charge", var0.getBatteryCharge());
            var1.rawset("gas_amount", var0.getPartById("GasTank").getContainerContentAmount());
            var1.rawset("gas_capacity", var0.getPartById("GasTank").getContainerCapacity());
            VehiclePart var2 = var0.getPartById("DoorFrontLeft");
            var1.rawset("doorleft_exist", var2 != null);
            if (var2 != null) {
               var1.rawset("doorleft_open", var2.getDoor().isOpen());
               var1.rawset("doorleft_locked", var2.getDoor().isLocked());
               var1.rawset("doorleft_lockbroken", var2.getDoor().isLockBroken());
               VehicleWindow var3 = var2.findWindow();
               var1.rawset("windowleft_exist", var3 != null);
               if (var3 != null) {
                  var1.rawset("windowleft_open", var3.isOpen());
                  var1.rawset("windowleft_health", var3.getHealth());
               }
            }

            VehiclePart var5 = var0.getPartById("DoorFrontRight");
            var1.rawset("doorright_exist", var5 != null);
            if (var2 != null) {
               var1.rawset("doorright_open", var5.getDoor().isOpen());
               var1.rawset("doorright_locked", var5.getDoor().isLocked());
               var1.rawset("doorright_lockbroken", var5.getDoor().isLockBroken());
               VehicleWindow var4 = var5.findWindow();
               var1.rawset("windowright_exist", var4 != null);
               if (var4 != null) {
                  var1.rawset("windowright_open", var4.isOpen());
                  var1.rawset("windowright_health", var4.getHealth());
               }
            }

            var1.rawset("headlights_set", var0.hasHeadlights());
            var1.rawset("headlights_on", var0.getHeadlightsOn());
            if (var0.getPartById("Heater") != null) {
               var1.rawset("heater_isset", true);
               Object var6 = var0.getPartById("Heater").getModData().rawget("active");
               if (var6 == null) {
                  var1.rawset("heater_on", false);
               } else {
                  var1.rawset("heater_on", var6 == Boolean.TRUE);
               }
            } else {
               var1.rawset("heater_isset", false);
            }

            return var1;
         }
      }

      @LuaMethod(
         name = "getLotDirectories",
         global = true
      )
      public static ArrayList getLotDirectories() {
         return IsoWorld.instance.MetaGrid != null ? IsoWorld.instance.MetaGrid.getLotDirectories() : null;
      }

      @LuaMethod(
         name = "useTextureFiltering",
         global = true
      )
      public static void useTextureFiltering(boolean var0) {
         TextureID.UseFiltering = var0;
      }

      @LuaMethod(
         name = "getTexture",
         global = true
      )
      public static Texture getTexture(String var0) {
         return Texture.getSharedTexture(var0);
      }

      @LuaMethod(
         name = "getTextManager",
         global = true
      )
      public static TextManager getTextManager() {
         return TextManager.instance;
      }

      @LuaMethod(
         name = "setProgressBarValue",
         global = true
      )
      public static void setProgressBarValue(IsoPlayer var0, int var1) {
         if (var0.isLocalPlayer()) {
            UIManager.getProgressBar((double)var0.getPlayerNum()).setValue((float)var1);
         }

      }

      @LuaMethod(
         name = "getText",
         global = true
      )
      public static String getText(String var0) {
         return Translator.getText(var0);
      }

      @LuaMethod(
         name = "getText",
         global = true
      )
      public static String getText(String var0, Object var1) {
         return Translator.getText(var0, var1);
      }

      @LuaMethod(
         name = "getText",
         global = true
      )
      public static String getText(String var0, Object var1, Object var2) {
         return Translator.getText(var0, var1, var2);
      }

      @LuaMethod(
         name = "getText",
         global = true
      )
      public static String getText(String var0, Object var1, Object var2, Object var3) {
         return Translator.getText(var0, var1, var2, var3);
      }

      @LuaMethod(
         name = "getText",
         global = true
      )
      public static String getText(String var0, Object var1, Object var2, Object var3, Object var4) {
         return Translator.getText(var0, var1, var2, var3, var4);
      }

      @LuaMethod(
         name = "getTextOrNull",
         global = true
      )
      public static String getTextOrNull(String var0) {
         return Translator.getTextOrNull(var0);
      }

      @LuaMethod(
         name = "getTextOrNull",
         global = true
      )
      public static String getTextOrNull(String var0, Object var1) {
         return Translator.getTextOrNull(var0, var1);
      }

      @LuaMethod(
         name = "getTextOrNull",
         global = true
      )
      public static String getTextOrNull(String var0, Object var1, Object var2) {
         return Translator.getTextOrNull(var0, var1, var2);
      }

      @LuaMethod(
         name = "getTextOrNull",
         global = true
      )
      public static String getTextOrNull(String var0, Object var1, Object var2, Object var3) {
         return Translator.getTextOrNull(var0, var1, var2, var3);
      }

      @LuaMethod(
         name = "getTextOrNull",
         global = true
      )
      public static String getTextOrNull(String var0, Object var1, Object var2, Object var3, Object var4) {
         return Translator.getTextOrNull(var0, var1, var2, var3, var4);
      }

      @LuaMethod(
         name = "getItemText",
         global = true
      )
      public static String getItemText(String var0) {
         return Translator.getDisplayItemName(var0);
      }

      @LuaMethod(
         name = "getRecipeDisplayName",
         global = true
      )
      public static String getRecipeDisplayName(String var0) {
         return Translator.getRecipeName(var0);
      }

      @LuaMethod(
         name = "getMyDocumentFolder",
         global = true
      )
      public static String getMyDocumentFolder() {
         return Core.getMyDocumentFolder();
      }

      @LuaMethod(
         name = "getSprite",
         global = true
      )
      public static IsoSprite getSprite(String var0) {
         return getCell().SpriteManager.getSprite(var0);
      }

      @LuaMethod(
         name = "getServerModData",
         global = true
      )
      public static void getServerModData() {
         GameClient.getCustomModData();
      }

      @LuaMethod(
         name = "isXBOXController",
         global = true
      )
      public static boolean isXBOXController() {
         for(int var0 = 0; var0 < GameWindow.GameInput.getControllerCount(); ++var0) {
            if (GameWindow.GameInput.getController(var0).getName().contains("XBOX 360")) {
               return true;
            }
         }

         return false;
      }

      @LuaMethod(
         name = "sendClientCommand",
         global = true
      )
      public static void sendClientCommand(String var0, String var1, KahluaTable var2) {
         if (GameClient.bClient && GameClient.bIngame) {
            GameClient.instance.sendClientCommand((IsoPlayer)null, var0, var1, var2);
         }

      }

      @LuaMethod(
         name = "sendClientCommand",
         global = true
      )
      public static void sendClientCommand(IsoPlayer var0, String var1, String var2, KahluaTable var3) {
         if (var0 != null && var0.isLocalPlayer()) {
            if (GameClient.bClient && GameClient.bIngame) {
               GameClient.instance.sendClientCommand(var0, var1, var2, var3);
            } else {
               if (GameServer.bServer) {
                  throw new IllegalStateException("can't call this function on the server");
               }

               SinglePlayerClient.sendClientCommand(var0, var1, var2, var3);
            }

         }
      }

      @LuaMethod(
         name = "sendServerCommand",
         global = true
      )
      public static void sendServerCommand(String var0, String var1, KahluaTable var2) {
         if (GameServer.bServer) {
            GameServer.sendServerCommand(var0, var1, var2);
         }

      }

      @LuaMethod(
         name = "sendServerCommand",
         global = true
      )
      public static void sendServerCommand(IsoPlayer var0, String var1, String var2, KahluaTable var3) {
         if (GameServer.bServer) {
            GameServer.sendServerCommand(var0, var1, var2, var3);
         }

      }

      @LuaMethod(
         name = "getOnlineUsername",
         global = true
      )
      public static String getOnlineUsername() {
         return IsoPlayer.instance.getDisplayName();
      }

      @LuaMethod(
         name = "isValidUserName",
         global = true
      )
      public static boolean isValidUserName(String var0) {
         return ServerWorldDatabase.isValidUserName(var0);
      }

      @LuaMethod(
         name = "getHourMinute",
         global = true
      )
      public static String getHourMinute() {
         return LuaManager.getHourMinuteJava();
      }

      @LuaMethod(
         name = "SendCommandToServer",
         global = true
      )
      public static void SendCommandToServer(String var0) {
         GameClient.SendCommandToServer(var0);
      }

      @LuaMethod(
         name = "isAdmin",
         global = true
      )
      public static boolean isAdmin() {
         return GameClient.bClient && GameClient.accessLevel.equals("admin");
      }

      @LuaMethod(
         name = "canModifyPlayerScoreboard",
         global = true
      )
      public static boolean canModifyPlayerScoreboard() {
         return GameClient.bClient && !GameClient.accessLevel.equals("");
      }

      @LuaMethod(
         name = "isAccessLevel",
         global = true
      )
      public static boolean isAccessLevel(String var0) {
         if (GameClient.bClient) {
            return GameClient.accessLevel.equals("") ? false : GameClient.accessLevel.equals(var0);
         } else {
            return false;
         }
      }

      @LuaMethod(
         name = "sendBandage",
         global = true
      )
      public static void sendBandage(int var0, int var1, boolean var2, float var3, boolean var4, String var5) {
         GameClient.instance.sendBandage(var0, var1, var2, var3, var4, var5);
      }

      @LuaMethod(
         name = "sendCataplasm",
         global = true
      )
      public static void sendCataplasm(int var0, int var1, float var2, float var3, float var4) {
         GameClient.instance.sendCataplasm(var0, var1, var2, var3, var4);
      }

      @LuaMethod(
         name = "sendStitch",
         global = true
      )
      public static void sendStitch(int var0, int var1, boolean var2, float var3) {
         GameClient.instance.sendStitch(var0, var1, var2, var3);
      }

      @LuaMethod(
         name = "sendWoundInfection",
         global = true
      )
      public static void sendWoundInfection(int var0, int var1, boolean var2) {
         GameClient.instance.sendWoundInfection(var0, var1, var2);
      }

      @LuaMethod(
         name = "sendDisinfect",
         global = true
      )
      public static void sendDisinfect(int var0, int var1, float var2) {
         GameClient.instance.sendDisinfect(var0, var1, var2);
      }

      @LuaMethod(
         name = "sendSplint",
         global = true
      )
      public static void sendSplint(int var0, int var1, boolean var2, float var3, String var4) {
         GameClient.instance.sendSplint(var0, var1, var2, var3, var4);
      }

      @LuaMethod(
         name = "sendAdditionalPain",
         global = true
      )
      public static void sendAdditionalPain(int var0, int var1, float var2) {
         GameClient.instance.sendAdditionalPain(var0, var1, var2);
      }

      @LuaMethod(
         name = "sendRemoveGlass",
         global = true
      )
      public static void sendRemoveGlass(int var0, int var1) {
         GameClient.instance.sendRemoveGlass(var0, var1);
      }

      @LuaMethod(
         name = "sendRemoveBullet",
         global = true
      )
      public static void sendRemoveBullet(int var0, int var1, int var2) {
         GameClient.instance.sendRemoveBullet(var0, var1, var2);
      }

      @LuaMethod(
         name = "sendCleanBurn",
         global = true
      )
      public static void sendCleanBurn(int var0, int var1) {
         GameClient.instance.sendCleanBurn(var0, var1);
      }

      @LuaMethod(
         name = "getGameClient",
         global = true
      )
      public static GameClient getGameClient() {
         return GameClient.instance;
      }

      @LuaMethod(
         name = "sendRequestInventory",
         global = true
      )
      public static void sendRequestInventory(IsoPlayer var0) {
         GameClient.sendRequestInventory(var0);
      }

      @LuaMethod(
         name = "InvMngGetItem",
         global = true
      )
      public static void InvMngGetItem(long var0, String var2, IsoPlayer var3) {
         GameClient.invMngRequestItem(var0, var2, var3);
      }

      @LuaMethod(
         name = "InvMngRemoveItem",
         global = true
      )
      public static void InvMngRemoveItem(long var0, IsoPlayer var2) {
         GameClient.invMngRequestRemoveItem(var0, var2);
      }

      @LuaMethod(
         name = "getConnectedPlayers",
         global = true
      )
      public static ArrayList getConnectedPlayers() {
         return GameClient.instance.getConnectedPlayers();
      }

      @LuaMethod(
         name = "getPlayerFromUsername",
         global = true
      )
      public static IsoPlayer getPlayerFromUsername(String var0) {
         return GameClient.instance.getPlayerFromUsername(var0);
      }

      @LuaMethod(
         name = "isCoopHost",
         global = true
      )
      public static boolean isCoopHost() {
         return GameClient.connection != null && GameClient.connection.isCoopHost;
      }

      @LuaMethod(
         name = "setAdmin",
         global = true
      )
      public static void setAdmin() {
         if (CoopMaster.instance.isRunning()) {
            String var0 = "admin";
            if (GameClient.connection.accessLevel.equals("admin")) {
               var0 = "";
            }

            GameClient.connection.accessLevel = var0;
            GameClient.accessLevel = var0;
            IsoPlayer.instance.accessLevel = var0;
            GameClient.SendCommandToServer("/setaccesslevel \"" + IsoPlayer.instance.username + "\" \"" + (var0.equals("") ? "none" : var0) + "\"");
            if (var0.equals("") && IsoPlayer.instance.invisible || var0.equals("admin") && !IsoPlayer.instance.invisible) {
               GameClient.SendCommandToServer("/invisible");
            }

         }
      }

      @LuaMethod(
         name = "addWarningPoint",
         global = true
      )
      public static void addWarningPoint(String var0, String var1, int var2) {
         if (GameClient.bClient) {
            GameClient.instance.addWarningPoint(var0, var1, var2);
         }

      }

      @LuaMethod(
         name = "toggleSafetyServer",
         global = true
      )
      public static void toggleSafetyServer(IsoPlayer var0) {
         GameClient.toggleSafety(var0);
      }

      @LuaMethod(
         name = "disconnect",
         global = true
      )
      public static void disconnect() {
         GameClient.connection.forceDisconnect();
      }

      @LuaMethod(
         name = "writeLog",
         global = true
      )
      public static void writeLog(String var0, String var1) {
         ByteBufferWriter var2 = GameClient.connection.startPacket();
         PacketTypes.doPacket((short)88, var2);
         var2.putUTF(var0);
         var2.putUTF(var1);
         GameClient.connection.endPacket();
      }

      @LuaMethod(
         name = "doKeyPress",
         global = true
      )
      public static void doKeyPress(boolean var0) {
         GameKeyboard.doLuaKeyPressed = var0;
      }

      @LuaMethod(
         name = "getEvolvedRecipes",
         global = true
      )
      public static Stack getEvolvedRecipes() {
         return ScriptManager.instance.getAllEvolvedRecipes();
      }

      @LuaMethod(
         name = "getZone",
         global = true
      )
      public static IsoMetaGrid.Zone getZone(int var0, int var1, int var2) {
         return IsoWorld.instance.MetaGrid.getZoneAt(var0, var1, var2);
      }

      @LuaMethod(
         name = "getZones",
         global = true
      )
      public static ArrayList getZones(int var0, int var1, int var2) {
         return IsoWorld.instance.MetaGrid.getZonesAt(var0, var1, var2);
      }

      @LuaMethod(
         name = "replaceWith",
         global = true
      )
      public static String replaceWith(String var0, String var1, String var2) {
         return var0.replaceFirst(var1, var2);
      }

      @LuaMethod(
         name = "getTimestamp",
         global = true
      )
      public static long getTimestamp() {
         return System.currentTimeMillis() / 1000L;
      }

      @LuaMethod(
         name = "getTimestampMs",
         global = true
      )
      public static long getTimestampMs() {
         return System.currentTimeMillis();
      }

      @LuaMethod(
         name = "forceSnowCheck",
         global = true
      )
      public static void forceSnowCheck() {
         ErosionMain.getInstance().snowCheck();
      }

      @LuaMethod(
         name = "getGametimeTimestamp",
         global = true
      )
      public static long getGametimeTimestamp() {
         return GameTime.instance.getCalender().getTimeInMillis() / 1000L;
      }

      @LuaMethod(
         name = "canInviteFriends",
         global = true
      )
      public static boolean canInviteFriends() {
         if (GameClient.bClient && SteamUtils.isSteamModeEnabled()) {
            return CoopMaster.instance.isRunning() || !GameClient.bCoopInvite;
         } else {
            return false;
         }
      }

      @LuaMethod(
         name = "inviteFriend",
         global = true
      )
      public static void inviteFriend(String var0) {
         if (CoopMaster.instance != null && CoopMaster.instance.isRunning()) {
            CoopMaster.instance.sendMessage("invite-add", var0);
         }

         SteamFriends.InviteUserToGame(SteamUtils.convertStringToSteamID(var0), "+connect " + GameClient.ip + ":" + GameClient.port);
      }

      @LuaMethod(
         name = "getFriendsList",
         global = true
      )
      public static KahluaTable getFriendsList() {
         KahluaTable var0 = LuaManager.platform.newTable();
         if (!getSteamModeActive()) {
            return var0;
         } else {
            List var1 = SteamFriends.GetFriendList();
            int var2 = 1;

            for(int var3 = 0; var3 < var1.size(); ++var3) {
               SteamFriend var4 = (SteamFriend)var1.get(var3);
               Double var5 = (double)var2;
               var0.rawset(var5, var4);
               ++var2;
            }

            return var0;
         }
      }

      @LuaMethod(
         name = "getSteamModeActive",
         global = true
      )
      public static Boolean getSteamModeActive() {
         return SteamUtils.isSteamModeEnabled();
      }

      @LuaMethod(
         name = "isValidSteamID",
         global = true
      )
      public static boolean isValidSteamID(String var0) {
         return var0 != null && !var0.isEmpty() ? SteamUtils.isValidSteamID(var0) : false;
      }

      @LuaMethod(
         name = "getCurrentUserSteamID",
         global = true
      )
      public static String getCurrentUserSteamID() {
         return SteamUtils.isSteamModeEnabled() && !GameServer.bServer ? SteamUser.GetSteamIDString() : null;
      }

      @LuaMethod(
         name = "getCurrentUserProfileName",
         global = true
      )
      public static String getCurrentUserProfileName() {
         return SteamUtils.isSteamModeEnabled() && !GameServer.bServer ? SteamFriends.GetFriendPersonaName(SteamUser.GetSteamID()) : null;
      }

      @LuaMethod(
         name = "getSteamScoreboard",
         global = true
      )
      public static boolean getSteamScoreboard() {
         if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
            String var0 = ServerOptions.instance.SteamScoreboard.getValue();
            return "true".equals(var0) || GameClient.accessLevel.equals("admin") && "admin".equals(var0);
         } else {
            return false;
         }
      }

      @LuaMethod(
         name = "isSteamOverlayEnabled",
         global = true
      )
      public static boolean isSteamOverlayEnabled() {
         return SteamUtils.isOverlayEnabled();
      }

      @LuaMethod(
         name = "activateSteamOverlayToWorkshop",
         global = true
      )
      public static void activateSteamOverlayToWorkshop() {
         if (SteamUtils.isOverlayEnabled()) {
            SteamFriends.ActivateGameOverlayToWebPage("steam://url/SteamWorkshopPage/108600");
         }

      }

      @LuaMethod(
         name = "activateSteamOverlayToWorkshopUser",
         global = true
      )
      public static void activateSteamOverlayToWorkshopUser() {
         if (SteamUtils.isOverlayEnabled()) {
            SteamFriends.ActivateGameOverlayToWebPage("steam://url/SteamIDCommunityFilesPage/" + SteamUser.GetSteamIDString() + "/108600");
         }

      }

      @LuaMethod(
         name = "activateSteamOverlayToWorkshopItem",
         global = true
      )
      public static void activateSteamOverlayToWorkshopItem(String var0) {
         if (SteamUtils.isOverlayEnabled() && SteamUtils.isValidSteamID(var0)) {
            SteamFriends.ActivateGameOverlayToWebPage("steam://url/CommunityFilePage/" + var0);
         }

      }

      @LuaMethod(
         name = "activateSteamOverlayToWebPage",
         global = true
      )
      public static void activateSteamOverlayToWebPage(String var0) {
         if (SteamUtils.isOverlayEnabled()) {
            SteamFriends.ActivateGameOverlayToWebPage(var0);
         }

      }

      @LuaMethod(
         name = "getSteamProfileNameFromSteamID",
         global = true
      )
      public static String getSteamProfileNameFromSteamID(String var0) {
         if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
            long var1 = SteamUtils.convertStringToSteamID(var0);
            if (var1 != -1L) {
               return SteamFriends.GetFriendPersonaName(var1);
            }
         }

         return null;
      }

      @LuaMethod(
         name = "getSteamAvatarFromSteamID",
         global = true
      )
      public static Texture getSteamAvatarFromSteamID(String var0) {
         if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
            long var1 = SteamUtils.convertStringToSteamID(var0);
            if (var1 != -1L) {
               return Texture.getSteamAvatar(var1);
            }
         }

         return null;
      }

      @LuaMethod(
         name = "getSteamIDFromUsername",
         global = true
      )
      public static String getSteamIDFromUsername(String var0) {
         if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
            IsoPlayer var1 = GameClient.instance.getPlayerFromUsername(var0);
            if (var1 != null) {
               return SteamUtils.convertSteamIDToString(var1.getSteamID());
            }
         }

         return null;
      }

      @LuaMethod(
         name = "resetRegionFile",
         global = true
      )
      public static void resetRegionFile() {
         ServerOptions.getInstance().resetRegionFile();
      }

      @LuaMethod(
         name = "getSteamProfileNameFromUsername",
         global = true
      )
      public static String getSteamProfileNameFromUsername(String var0) {
         if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
            IsoPlayer var1 = GameClient.instance.getPlayerFromUsername(var0);
            if (var1 != null) {
               return SteamFriends.GetFriendPersonaName(var1.getSteamID());
            }
         }

         return null;
      }

      @LuaMethod(
         name = "getSteamAvatarFromUsername",
         global = true
      )
      public static Texture getSteamAvatarFromUsername(String var0) {
         if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
            IsoPlayer var1 = GameClient.instance.getPlayerFromUsername(var0);
            if (var1 != null) {
               return Texture.getSteamAvatar(var1.getSteamID());
            }
         }

         return null;
      }

      @LuaMethod(
         name = "getSteamWorkshopStagedItems",
         global = true
      )
      public static ArrayList getSteamWorkshopStagedItems() {
         return SteamUtils.isSteamModeEnabled() ? SteamWorkshop.instance.loadStagedItems() : null;
      }

      @LuaMethod(
         name = "getSteamWorkshopItemIDs",
         global = true
      )
      public static ArrayList getSteamWorkshopItemIDs() {
         if (SteamUtils.isSteamModeEnabled()) {
            ArrayList var0 = new ArrayList();
            String[] var1 = SteamWorkshop.instance.GetInstalledItemFolders();
            if (var1 == null) {
               return var0;
            } else {
               for(int var2 = 0; var2 < var1.length; ++var2) {
                  String var3 = SteamWorkshop.instance.getIDFromItemInstallFolder(var1[var2]);
                  if (var3 != null) {
                     var0.add(var3);
                  }
               }

               return var0;
            }
         } else {
            return null;
         }
      }

      @LuaMethod(
         name = "getSteamWorkshopItemMods",
         global = true
      )
      public static ArrayList getSteamWorkshopItemMods(String var0) {
         if (SteamUtils.isSteamModeEnabled()) {
            long var1 = SteamUtils.convertStringToSteamID(var0);
            if (var1 > 0L) {
               return ZomboidFileSystem.instance.getWorkshopItemMods(var1);
            }
         }

         return null;
      }

      @LuaMethod(
         name = "sendPlayerStatsChange",
         global = true
      )
      public static void sendPlayerStatsChange(IsoPlayer var0) {
         if (GameClient.bClient) {
            GameClient.instance.sendChangedPlayerStats(var0);
         }

      }

      @LuaMethod(
         name = "sendPersonalColor",
         global = true
      )
      public static void sendPersonalColor(IsoPlayer var0) {
         if (GameClient.bClient) {
            GameClient.instance.sendPersonalColor(var0);
         }

      }

      @LuaMethod(
         name = "requestTrading",
         global = true
      )
      public static void requestTrading(IsoPlayer var0, IsoPlayer var1) {
         GameClient.instance.requestTrading(var0, var1);
      }

      @LuaMethod(
         name = "acceptTrading",
         global = true
      )
      public static void acceptTrading(IsoPlayer var0, IsoPlayer var1, boolean var2) {
         GameClient.instance.acceptTrading(var0, var1, var2);
      }

      @LuaMethod(
         name = "tradingUISendAddItem",
         global = true
      )
      public static void tradingUISendAddItem(IsoPlayer var0, IsoPlayer var1, InventoryItem var2) {
         GameClient.instance.tradingUISendAddItem(var0, var1, var2);
      }

      @LuaMethod(
         name = "tradingUISendRemoveItem",
         global = true
      )
      public static void tradingUISendRemoveItem(IsoPlayer var0, IsoPlayer var1, int var2) {
         GameClient.instance.tradingUISendRemoveItem(var0, var1, var2);
      }

      @LuaMethod(
         name = "tradingUISendUpdateState",
         global = true
      )
      public static void tradingUISendUpdateState(IsoPlayer var0, IsoPlayer var1, int var2) {
         GameClient.instance.tradingUISendUpdateState(var0, var1, var2);
      }

      @LuaMethod(
         name = "querySteamWorkshopItemDetails",
         global = true
      )
      public static void querySteamWorkshopItemDetails(ArrayList var0, LuaClosure var1, Object var2) {
         if (var0 != null && var1 != null) {
            if (var0.isEmpty()) {
               if (var2 == null) {
                  LuaManager.caller.pcall(LuaManager.thread, var1, (Object[])("Completed", new ArrayList()));
               } else {
                  LuaManager.caller.pcall(LuaManager.thread, var1, (Object[])(var2, "Completed", new ArrayList()));
               }

            } else {
               new LuaManager.GlobalObject.ItemQuery(var0, var1, var2);
            }
         } else {
            throw new NullPointerException();
         }
      }

      @LuaMethod(
         name = "connectToServerStateCallback",
         global = true
      )
      public static void connectToServerStateCallback(String var0) {
         if (ConnectToServerState.instance != null) {
            ConnectToServerState.instance.FromLua(var0);
         }

      }

      @LuaMethod(
         name = "getPublicServersList",
         global = true
      )
      public static KahluaTable getPublicServersList() {
         KahluaTable var0 = LuaManager.platform.newTable();
         if (!SteamUtils.isSteamModeEnabled() && !PublicServerUtil.isEnabled()) {
            return var0;
         } else if (System.currentTimeMillis() - timeLastRefresh < 60000L) {
            return var0;
         } else {
            ArrayList var1 = new ArrayList();

            try {
               Server var5;
               if (getSteamModeActive()) {
                  ServerBrowser.RefreshInternetServers();
                  List var2 = ServerBrowser.GetServerList();
                  Iterator var3 = var2.iterator();

                  while(var3.hasNext()) {
                     GameServerDetails var4 = (GameServerDetails)var3.next();
                     var5 = new Server();
                     var5.setName(var4.name);
                     var5.setDescription(var4.gameDescription);
                     var5.setSteamId(Long.toString(var4.steamId));
                     var5.setPing(Integer.toString(var4.ping));
                     var5.setPlayers(Integer.toString(var4.numPlayers));
                     var5.setMaxPlayers(Integer.toString(var4.maxPlayers));
                     var5.setOpen(true);
                     var5.setIp(var4.address);
                     var5.setPort(Integer.toString(var4.port));
                     var5.setMods(var4.tags);
                     var5.setVersion(Core.getInstance().getVersionNumber());
                     var5.setLastUpdate(1);
                     var1.add(var5);
                  }

                  System.out.printf("%d servers\n", var2.size());
               } else {
                  URL var18 = new URL(PublicServerUtil.webSite + "servers.xml");
                  InputStreamReader var20 = new InputStreamReader(var18.openStream());
                  BufferedReader var22 = new BufferedReader(var20);
                  var5 = null;
                  StringBuffer var6 = new StringBuffer();

                  String var24;
                  while((var24 = var22.readLine()) != null) {
                     var6.append(var24).append('\n');
                  }

                  var22.close();
                  DocumentBuilderFactory var7 = DocumentBuilderFactory.newInstance();
                  DocumentBuilder var8 = var7.newDocumentBuilder();
                  Document var9 = var8.parse(new InputSource(new StringReader(var6.toString())));
                  var9.getDocumentElement().normalize();
                  NodeList var10 = var9.getElementsByTagName("server");

                  for(int var11 = 0; var11 < var10.getLength(); ++var11) {
                     Node var12 = var10.item(var11);
                     if (var12.getNodeType() == 1) {
                        Element var13 = (Element)var12;
                        Server var14 = new Server();
                        var14.setName(var13.getElementsByTagName("name").item(0).getTextContent());
                        if (var13.getElementsByTagName("desc").item(0) != null && !"".equals(var13.getElementsByTagName("desc").item(0).getTextContent())) {
                           var14.setDescription(var13.getElementsByTagName("desc").item(0).getTextContent());
                        }

                        var14.setIp(var13.getElementsByTagName("ip").item(0).getTextContent());
                        var14.setPort(var13.getElementsByTagName("port").item(0).getTextContent());
                        var14.setPlayers(var13.getElementsByTagName("players").item(0).getTextContent());
                        var14.setMaxPlayers(var13.getElementsByTagName("maxPlayers").item(0).getTextContent());
                        if (var13.getElementsByTagName("version") != null && var13.getElementsByTagName("version").item(0) != null) {
                           var14.setVersion(var13.getElementsByTagName("version").item(0).getTextContent());
                        }

                        var14.setOpen(var13.getElementsByTagName("open").item(0).getTextContent().equals("1"));
                        Integer var15 = Integer.parseInt(var13.getElementsByTagName("lastUpdate").item(0).getTextContent());
                        if (var13.getElementsByTagName("mods").item(0) != null && !"".equals(var13.getElementsByTagName("mods").item(0).getTextContent())) {
                           var14.setMods(var13.getElementsByTagName("mods").item(0).getTextContent());
                        }

                        var14.setLastUpdate((new Double(Math.floor((double)((getTimestamp() - (long)var15) / 60L)))).intValue());
                        NodeList var16 = var13.getElementsByTagName("password");
                        var14.setPasswordProtected(var16 != null && var16.getLength() != 0 && var16.item(0).getTextContent().equals("1"));
                        var1.add(var14);
                     }
                  }
               }

               int var19 = 1;

               for(int var21 = 0; var21 < var1.size(); ++var21) {
                  Server var23 = (Server)var1.get(var21);
                  Double var25 = (double)var19;
                  var0.rawset(var25, var23);
                  ++var19;
               }

               timeLastRefresh = Calendar.getInstance().getTimeInMillis();
               return var0;
            } catch (Exception var17) {
               var17.printStackTrace();
               return null;
            }
         }
      }

      @LuaMethod(
         name = "steamRequestInternetServersList",
         global = true
      )
      public static void steamRequestInternetServersList() {
         ServerBrowser.RefreshInternetServers();
      }

      @LuaMethod(
         name = "steamReleaseInternetServersRequest",
         global = true
      )
      public static void steamReleaseInternetServersRequest() {
         ServerBrowser.Release();
      }

      @LuaMethod(
         name = "steamGetInternetServersCount",
         global = true
      )
      public static int steamRequestInternetServersCount() {
         return ServerBrowser.GetServerCount();
      }

      @LuaMethod(
         name = "steamGetInternetServerDetails",
         global = true
      )
      public static Server steamGetInternetServerDetails(int var0) {
         if (!ServerBrowser.IsRefreshing()) {
            return null;
         } else {
            GameServerDetails var1 = ServerBrowser.GetServerDetails(var0);
            if (var1 == null) {
               return null;
            } else {
               Server var2 = new Server();
               var2.setName(var1.name);
               var2.setDescription("");
               var2.setSteamId(Long.toString(var1.steamId));
               var2.setPing(Integer.toString(var1.ping));
               var2.setPlayers(Integer.toString(var1.numPlayers));
               var2.setMaxPlayers(Integer.toString(var1.maxPlayers));
               var2.setOpen(true);
               var2.setPublic(true);
               var2.setIp(var1.address);
               var2.setPort(Integer.toString(var1.port));
               var2.setMods(var1.tags.replace(";hosted", "").replace("hidden", ""));
               var2.setHosted(var1.tags.endsWith(";hosted"));
               var2.setVersion("");
               var2.setLastUpdate(1);
               var2.setPasswordProtected(var1.passwordProtected);
               return var2;
            }
         }
      }

      @LuaMethod(
         name = "steamRequestServerRules",
         global = true
      )
      public static boolean steamRequestServerRules(String var0, int var1) {
         return ServerBrowser.RequestServerRules(var0, var1);
      }

      @LuaMethod(
         name = "steamRequestServerDetails",
         global = true
      )
      public static boolean steamRequestServerDetails(String var0, int var1) {
         return ServerBrowser.QueryServer(var0, var1);
      }

      @LuaMethod(
         name = "isPublicServerListAllowed",
         global = true
      )
      public static boolean isPublicServerListAllowed() {
         return SteamUtils.isSteamModeEnabled() ? true : PublicServerUtil.isEnabled();
      }

      @LuaMethod(
         name = "is64bit",
         global = true
      )
      public static boolean is64bit() {
         return "64".equals(System.getProperty("sun.arch.data.model"));
      }

      @LuaMethod(
         name = "testSound",
         global = true
      )
      public static void testSound() {
         float var0 = (float)org.lwjgl.input.Mouse.getX() * Core.getInstance().getZoom(0);
         float var1 = (float)org.lwjgl.input.Mouse.getY() * Core.getInstance().getZoom(0);
         int var2 = (int)IsoPlayer.instance.getZ();
         int var3 = (int)IsoUtils.XToIso(var0 - 30.0F, (float)Core.getInstance().getOffscreenHeight(0) - var1 - 366.0F, (float)var2);
         int var4 = (int)IsoUtils.YToIso(var0 - 30.0F, (float)Core.getInstance().getOffscreenHeight(0) - var1 - 366.0F, (float)var2);
         float var5 = 50.0F;
         float var6 = 1.0F;
         AmbientStreamManager.Ambient var7 = (AmbientStreamManager)AmbientStreamManager.instance.new Ambient("burglar2", (float)var3, (float)var4, var5, var6);
         var7.trackMouse = true;
         ((AmbientStreamManager)AmbientStreamManager.instance).ambient.add(var7);
      }

      @LuaMethod(
         name = "copyTable",
         global = true
      )
      public static KahluaTable copyTable(KahluaTable var0) {
         return LuaManager.copyTable(var0);
      }

      @LuaMethod(
         name = "getUrlInputStream",
         global = true
      )
      public static DataInputStream getUrlInputStream(String var0) {
         if (var0 != null && (var0.startsWith("https://") || var0.startsWith("http://"))) {
            try {
               return new DataInputStream((new URL(var0)).openStream());
            } catch (IOException var2) {
               var2.printStackTrace();
               return null;
            }
         } else {
            return null;
         }
      }

      @LuaMethod(
         name = "renderIsoCircle",
         global = true
      )
      public static void renderIsoCircle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8) {
         double var9 = 0.3490658503988659D;

         for(double var11 = 0.0D; var11 < 6.283185307179586D; var11 += var9) {
            float var13 = var0 + var3 * (float)Math.cos(var11);
            float var14 = var1 + var3 * (float)Math.sin(var11);
            float var15 = var0 + var3 * (float)Math.cos(var11 + var9);
            float var16 = var1 + var3 * (float)Math.sin(var11 + var9);
            float var17 = IsoUtils.XToScreenExact(var13, var14, var2, 0);
            float var18 = IsoUtils.YToScreenExact(var13, var14, var2, 0);
            float var19 = IsoUtils.XToScreenExact(var15, var16, var2, 0);
            float var20 = IsoUtils.YToScreenExact(var15, var16, var2, 0);
            LineDrawer.drawLine(var17, var18, var19, var20, var4, var5, var6, var7, var8);
         }

      }

      @LuaMethod(
         name = "configureLighting",
         global = true
      )
      public static void configureLighting(float var0) {
         if (LightingJNI.init) {
            LightingJNI.configure(var0);
         }

      }

      @LuaMethod(
         name = "testHelicopter",
         global = true
      )
      public static void testHelicopter() {
         IsoWorld.instance.helicopter.pickRandomTarget();
      }

      @LuaMethod(
         name = "getServerSettingsManager",
         global = true
      )
      public static ServerSettingsManager getServerSettingsManager() {
         return ServerSettingsManager.instance;
      }

      @LuaMethod(
         name = "rainConfig",
         global = true
      )
      public static void rainConfig(String var0, int var1) {
         if ("alpha".equals(var0)) {
            IsoWorld.instance.CurrentCell.setRainAlpha(var1);
         }

         if ("intensity".equals(var0)) {
            IsoWorld.instance.CurrentCell.setRainIntensity(var1);
         }

         if ("speed".equals(var0)) {
            IsoWorld.instance.CurrentCell.setRainSpeed(var1);
         }

         if ("reloadTextures".equals(var0)) {
            IsoWorld.instance.CurrentCell.reloadRainTextures();
         }

      }

      @LuaMethod(
         name = "getVehicleById",
         global = true
      )
      public static BaseVehicle getVehicleById(int var0) {
         return GameServer.bServer ? VehicleManager.instance.getVehicleByID((short)var0) : VehicleManager.instance.getVehicleByID((short)var0);
      }

      @LuaMethod(
         name = "addCarCrash",
         global = true
      )
      public static void addCarCrash() {
         IsoGridSquare var0 = IsoPlayer.instance.getCurrentSquare();
         if (var0 != null) {
            IsoChunk var1 = var0.getChunk();
            if (var1 != null) {
               IsoMetaGrid.Zone var2 = var0.getZone();
               if (var2 != null) {
                  if (var1.canAddRandomCarCrash(var2, true)) {
                     var0.chunk.addRandomCarCrash(var2, true);
                  }
               }
            }
         }
      }

      @LuaMethod(
         name = "addVehicle",
         global = true
      )
      public static BaseVehicle addVehicle(String var0) {
         if (var0 != null && !var0.isEmpty() && ScriptManager.instance.getVehicle(var0) == null) {
            DebugLog.log("No such vehicle script \"" + var0 + "\"");
            return null;
         } else {
            WorldSimulation.instance.create();
            BaseVehicle var1 = new BaseVehicle(IsoWorld.instance.CurrentCell);
            switch(Rand.Next(24)) {
            case 0:
               var1.setScriptName("Base.PickUpVanLights");
               break;
            case 1:
               var1.setScriptName("Base.PickUpVan");
               break;
            case 2:
               var1.setScriptName("Base.PickUpTruckLights");
               break;
            case 3:
               var1.setScriptName("Base.PickUpTruck");
               break;
            case 4:
               var1.setScriptName("Base.PickupBurnt");
               break;
            case 5:
               var1.setScriptName("Base.CarNormal");
               break;
            case 6:
               var1.setScriptName("Base.CarStationWagon");
               break;
            case 7:
               var1.setScriptName("Base.CarLights");
               break;
            case 8:
               var1.setScriptName("Base.CarNormalBurnt");
               break;
            case 9:
               var1.setScriptName("Base.NormalCarBurntPolice");
               break;
            case 10:
               var1.setScriptName("Base.Van");
               break;
            case 11:
               var1.setScriptName("Base.VanSeats");
               break;
            case 12:
               var1.setScriptName("Base.VanAmbulance");
               break;
            case 13:
               var1.setScriptName("Base.VanRadio");
               break;
            case 14:
               var1.setScriptName("Base.StepVan");
               break;
            case 15:
               var1.setScriptName("Base.AmbulanceBurnt");
               break;
            case 16:
               var1.setScriptName("Base.VanRadioBurnt");
               break;
            case 17:
               var1.setScriptName("Base.VanSeatsBurnt");
               break;
            case 18:
               var1.setScriptName("Base.VanBurnt");
               break;
            case 19:
               var1.setScriptName("Base.SportsCar");
               break;
            case 20:
               var1.setScriptName("Base.SportsCarBurnt");
               break;
            case 21:
               var1.setScriptName("Base.SmallCar");
               break;
            case 22:
               var1.setScriptName("Base.SmallCarBurnt");
               break;
            case 23:
               var1.setScriptName("Base.VanSpecial");
            }

            if (var0 != null && !var0.isEmpty()) {
               var1.setScriptName(var0);
            }

            var1.setX(IsoPlayer.instance.getX());
            var1.setY(IsoPlayer.instance.getY());
            var1.setZ(IsoPlayer.instance.getZ() + 2.0F);
            if (IsoChunk.doSpawnedVehiclesInInvalidPosition(var1)) {
               var1.setSquare(IsoPlayer.instance.getSquare());
               var1.square.chunk.vehicles.add(var1);
               var1.chunk = var1.square.chunk;
               var1.addToWorld();
            } else {
               DebugLog.log("ERROR: I can not spawn the vehicle. Invalid position. Try to change position.");
            }

            return null;
         }
      }

      @LuaMethod(
         name = "addAllVehicles",
         global = true
      )
      public static void addAllVehicles() {
         ArrayList var0 = ScriptManager.instance.getAllVehicleScripts();
         float var1 = (float)(IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMinTiles() + 5);
         float var2 = IsoPlayer.instance.getY();
         float var3 = 0.0F;

         for(int var4 = 0; var4 < var0.size(); ++var4) {
            VehicleScript var5 = (VehicleScript)var0.get(var4);
            if (var5.getWheelCount() != 0 && var5.getModel() != null) {
               WorldSimulation.instance.create();
               BaseVehicle var6 = new BaseVehicle(IsoWorld.instance.CurrentCell);
               var6.setScriptName(var5.getFullName());
               var6.setX(var1);
               var6.setY(var2);
               var6.setZ(var3);
               if (IsoChunk.doSpawnedVehiclesInInvalidPosition(var6)) {
                  var6.setSquare(IsoPlayer.instance.getSquare());
                  var6.square.chunk.vehicles.add(var6);
                  var6.chunk = var6.square.chunk;
                  var6.addToWorld();
                  IsoChunk.addFromCheckedVehicles(var6);
               } else {
                  DebugLog.log(var5.getName() + " not spawned, position invalid");
               }

               var1 += 4.0F;
            }
         }

      }

      @LuaMethod(
         name = "addPhysicsObject",
         global = true
      )
      public static BaseVehicle addPhysicsObject() {
         int var0 = Bullet.addPhysicsObject(getPlayer().getX(), getPlayer().getY());
         IsoPushableObject var1 = new IsoPushableObject(IsoWorld.instance.getCell(), IsoPlayer.instance.getCurrentSquare(), IsoWorld.instance.getCell().getSpriteManager().getSprite("trashcontainers_01_16"));
         WorldSimulation.instance.physicsObjectMap.put(var0, var1);
         return null;
      }

      @LuaMethod(
         name = "reloadSoundFiles",
         global = true
      )
      public static void reloadSoundFiles() {
         try {
            Iterator var0 = ZomboidFileSystem.instance.ActiveFileMap.keySet().iterator();

            while(var0.hasNext()) {
               String var1 = (String)var0.next();
               if (var1.matches(".*/sounds_.+\\.txt")) {
                  GameSounds.ReloadFile(var1);
               }
            }
         } catch (Throwable var2) {
            ExceptionLogger.logException(var2);
         }

      }

      @LuaMethod(
         name = "reloadVehicles",
         global = true
      )
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
            Iterator var0 = IsoWorld.instance.CurrentCell.vehicles.iterator();

            while(var0.hasNext()) {
               BaseVehicle var1 = (BaseVehicle)var0.next();
               var1.scriptReloaded();
            }
         } catch (Exception var2) {
            var2.printStackTrace();
         }

      }

      @LuaMethod(
         name = "proceedPM",
         global = true
      )
      public static String proceedPM(String var0) {
         var0 = var0.trim();
         String var1 = null;
         String var2 = null;
         Matcher var3 = Pattern.compile("(\"[^\"]*\\s+[^\"]*\"|[^\"]\\S*)\\s(.+)").matcher(var0);
         if (var3.matches()) {
            var1 = var3.group(1);
            var2 = var3.group(2);
            String var4 = var1.replaceAll("\"", "");
            (new Thread(() -> {
               ChatManager.getInstance().sendWhisperMessage(var4, var2);
            })).start();
            return var1;
         } else if (Core.bDebug) {
            throw new RuntimeException();
         } else {
            return "";
         }
      }

      @LuaMethod(
         name = "processSayMessage",
         global = true
      )
      public static void processSayMessage(String var0) {
         if (var0 != null && !var0.isEmpty()) {
            var0 = var0.trim();
            ChatManager.getInstance().sendMessageToChat(ChatType.say, var0);
         }
      }

      @LuaMethod(
         name = "processGeneralMessage",
         global = true
      )
      public static void processGeneralMessage(String var0) {
         if (var0 != null && !var0.isEmpty()) {
            var0 = var0.trim();
            ChatManager.getInstance().sendMessageToChat(ChatType.general, var0);
         }
      }

      @LuaMethod(
         name = "processShoutMessage",
         global = true
      )
      public static void processShoutMessage(String var0) {
         if (var0 != null && !var0.isEmpty()) {
            var0 = var0.trim();
            ChatManager.getInstance().sendMessageToChat(ChatType.shout, var0);
         }
      }

      @LuaMethod(
         name = "proceedFactionMessage",
         global = true
      )
      public static void ProceedFactionMessage(String var0) {
         if (var0 != null && !var0.isEmpty()) {
            var0 = var0.trim();
            ChatManager.getInstance().sendMessageToChat(ChatType.faction, var0);
         }
      }

      @LuaMethod(
         name = "processSafehouseMessage",
         global = true
      )
      public static void ProcessSafehouseMessage(String var0) {
         if (var0 != null && !var0.isEmpty()) {
            var0 = var0.trim();
            ChatManager.getInstance().sendMessageToChat(ChatType.safehouse, var0);
         }
      }

      @LuaMethod(
         name = "processAdminChatMessage",
         global = true
      )
      public static void ProcessAdminChatMessage(String var0) {
         if (var0 != null && !var0.isEmpty()) {
            var0 = var0.trim();
            ChatManager.getInstance().sendMessageToChat(ChatType.admin, var0);
         }
      }

      @LuaMethod(
         name = "showWrongChatTabMessage",
         global = true
      )
      public static void showWrongChatTabMessage(int var0, int var1, String var2) {
         String var3 = ChatManager.getInstance().getTabName((short)var0);
         String var4 = ChatManager.getInstance().getTabName((short)var1);
         String var5 = Translator.getText("UI_chat_wrong_tab", var3, var4, var2);
         ChatManager.getInstance().showServerChatMessage(var5);
      }

      @LuaMethod(
         name = "focusOnTab",
         global = true
      )
      public static void focusOnTab(Short var0) {
         ChatManager.getInstance().focusOnTab(var0);
      }

      @LuaMethod(
         name = "updateChatSettings",
         global = true
      )
      public static void updateChatSettings(String var0, boolean var1, boolean var2) {
         ChatManager.getInstance().updateChatSettings(var0, var1, var2);
      }

      @LuaMethod(
         name = "checkPlayerCanUseChat",
         global = true
      )
      public static Boolean checkPlayerCanUseChat(String var0) {
         var0 = var0.trim();
         byte var3 = -1;
         switch(var0.hashCode()) {
         case -1769046940:
            if (var0.equals("/safehouse")) {
               var3 = 10;
            }
            break;
         case -784181491:
            if (var0.equals("/faction")) {
               var3 = 8;
            }
            break;
         case 1554:
            if (var0.equals("/a")) {
               var3 = 1;
            }
            break;
         case 1559:
            if (var0.equals("/f")) {
               var3 = 7;
            }
            break;
         case 1571:
            if (var0.equals("/r")) {
               var3 = 14;
            }
            break;
         case 1572:
            if (var0.equals("/s")) {
               var3 = 3;
            }
            break;
         case 1576:
            if (var0.equals("/w")) {
               var3 = 11;
            }
            break;
         case 1578:
            if (var0.equals("/y")) {
               var3 = 5;
            }
            break;
         case 48836:
            if (var0.equals("/sh")) {
               var3 = 9;
            }
            break;
         case 1496850:
            if (var0.equals("/all")) {
               var3 = 0;
            }
            break;
         case 1513820:
            if (var0.equals("/say")) {
               var3 = 4;
            }
            break;
         case 47110715:
            if (var0.equals("/yell")) {
               var3 = 6;
            }
            break;
         case 1438238848:
            if (var0.equals("/admin")) {
               var3 = 2;
            }
            break;
         case 1453840684:
            if (var0.equals("/radio")) {
               var3 = 13;
            }
            break;
         case 1624401011:
            if (var0.equals("/whisper")) {
               var3 = 12;
            }
         }

         ChatType var1;
         switch(var3) {
         case 0:
            var1 = ChatType.general;
            break;
         case 1:
         case 2:
            var1 = ChatType.admin;
            break;
         case 3:
         case 4:
            var1 = ChatType.say;
            break;
         case 5:
         case 6:
            var1 = ChatType.shout;
            break;
         case 7:
         case 8:
            var1 = ChatType.faction;
            break;
         case 9:
         case 10:
            var1 = ChatType.safehouse;
            break;
         case 11:
         case 12:
            var1 = ChatType.whisper;
            break;
         case 13:
         case 14:
            var1 = ChatType.radio;
            break;
         default:
            var1 = ChatType.notDefined;
            DebugLog.log("Chat command not found");
         }

         return ChatManager.getInstance().isPlayerCanUseChat(var1);
      }

      @LuaMethod(
         name = "reloadVehicleTextures",
         global = true
      )
      public static void reloadVehicleTextures(String var0) {
         VehicleScript var1 = ScriptManager.instance.getVehicle(var0);
         if (var1 == null) {
            DebugLog.log("no such vehicle script");
         } else {
            for(int var2 = 0; var2 < var1.getSkinCount(); ++var2) {
               VehicleScript.Skin var3 = var1.getSkin(var2);
               if (var3 != null && var3.texture != null) {
                  Texture.reload("media/textures/" + var3.texture + ".png");
               }
            }

            if (var1.textureRust != null) {
               Texture.reload("media/textures/" + var1.textureRust + ".png");
            }

            if (var1.textureMask != null) {
               Texture.reload("media/textures/" + var1.textureMask + ".png");
            }

            if (var1.textureLights != null) {
               Texture.reload("media/textures/" + var1.textureLights + ".png");
            }

            if (var1.textureDamage1Overlay != null) {
               Texture.reload("media/textures/" + var1.textureDamage1Overlay + ".png");
            }

            if (var1.textureDamage1Shell != null) {
               Texture.reload("media/textures/" + var1.textureDamage1Shell + ".png");
            }

            if (var1.textureDamage2Overlay != null) {
               Texture.reload("media/textures/" + var1.textureDamage2Overlay + ".png");
            }

            if (var1.textureDamage2Shell != null) {
               Texture.reload("media/textures/" + var1.textureDamage2Shell + ".png");
            }

         }
      }

      @LuaMethod(
         name = "getClimateManager",
         global = true
      )
      public static ClimateManager getClimateManager() {
         return ClimateManager.getInstance();
      }

      @LuaMethod(
         name = "setVehicleModelCameraHack",
         global = true
      )
      public static void setVehicleModelCameraHack(float var0) {
         ModelCamera.instance.VehicleScaleHack = var0;
      }

      @LuaMethod(
         name = "getErosion",
         global = true
      )
      public static void getErosion() {
         ErosionMain.getInstance();
      }

      private static final class ItemQuery implements ISteamWorkshopCallback {
         private LuaClosure functionObj;
         private Object arg1;
         private long handle;

         public ItemQuery(ArrayList var1, LuaClosure var2, Object var3) {
            this.functionObj = var2;
            this.arg1 = var3;
            long[] var4 = new long[var1.size()];
            int var5 = 0;

            for(int var6 = 0; var6 < var1.size(); ++var6) {
               long var7 = SteamUtils.convertStringToSteamID((String)var1.get(var6));
               if (var7 != -1L) {
                  var4[var5++] = var7;
               }
            }

            this.handle = SteamWorkshop.instance.CreateQueryUGCDetailsRequest(var4, this);
            if (this.handle == 0L) {
               SteamWorkshop.instance.RemoveCallback(this);
               if (var3 == null) {
                  LuaManager.caller.pcall(LuaManager.thread, var2, (Object)"NotCompleted");
               } else {
                  LuaManager.caller.pcall(LuaManager.thread, var2, (Object[])(var3, "NotCompleted"));
               }
            }

         }

         public void onItemCreated(long var1, boolean var3) {
         }

         public void onItemNotCreated(int var1) {
         }

         public void onItemUpdated(boolean var1) {
         }

         public void onItemNotUpdated(int var1) {
         }

         public void onItemSubscribed(long var1) {
         }

         public void onItemNotSubscribed(long var1, int var3) {
         }

         public void onItemDownloaded(long var1) {
         }

         public void onItemNotDownloaded(long var1, int var3) {
         }

         public void onItemQueryCompleted(long var1, int var3) {
            if (var1 == this.handle) {
               SteamWorkshop.instance.RemoveCallback(this);
               ArrayList var4 = new ArrayList();

               for(int var5 = 0; var5 < var3; ++var5) {
                  SteamUGCDetails var6 = SteamWorkshop.instance.GetQueryUGCResult(var1, var5);
                  if (var6 != null) {
                     var4.add(var6);
                  }
               }

               SteamWorkshop.instance.ReleaseQueryUGCRequest(var1);
               if (this.arg1 == null) {
                  LuaManager.caller.pcall(LuaManager.thread, this.functionObj, (Object[])("Completed", var4));
               } else {
                  LuaManager.caller.pcall(LuaManager.thread, this.functionObj, (Object[])(this.arg1, "Completed", var4));
               }

            }
         }

         public void onItemQueryNotCompleted(long var1, int var3) {
            if (var1 == this.handle) {
               SteamWorkshop.instance.RemoveCallback(this);
               SteamWorkshop.instance.ReleaseQueryUGCRequest(var1);
               if (this.arg1 == null) {
                  LuaManager.caller.pcall(LuaManager.thread, this.functionObj, (Object)"NotCompleted");
               } else {
                  LuaManager.caller.pcall(LuaManager.thread, this.functionObj, (Object[])(this.arg1, "NotCompleted"));
               }

            }
         }
      }

      public static class LuaFileWriter {
         private FileWriter writer;

         public void write(String var1) throws IOException {
            this.writer.write(var1);
         }

         public void close() throws IOException {
            this.writer.close();
         }
      }
   }

   private static class Exposer extends LuaJavaClassExposer {
      private HashSet exposed = new HashSet();

      public Exposer(KahluaConverterManager var1, Platform var2, KahluaTable var3) {
         super(var1, var2, var3);
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
            } catch (NoSuchMethodException var3) {
               var3.printStackTrace();
            }
         }

         Iterator var1 = this.exposed.iterator();

         while(var1.hasNext()) {
            Class var2 = (Class)var1.next();
            this.exposeLikeJavaRecursively(var2, LuaManager.env);
         }

         this.exposeGlobalFunctions(new LuaManager.GlobalObject());
         LuaManager.exposeKeyboardKeys(LuaManager.env);
      }

      public void setExposed(Class var1) {
         this.exposed.add(var1);
      }

      public boolean shouldExpose(Class var1) {
         return var1 == null ? false : this.exposed.contains(var1);
      }
   }
}
