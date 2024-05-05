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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.luaj.kahluafork.compiler.FuncState;
import org.lwjglx.input.Controller;
import org.lwjglx.input.Controllers;
import org.lwjglx.input.KeyCodes;
import org.lwjglx.input.Keyboard;
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
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
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
import zombie.ZombieSpawnRecorder;
import zombie.ZomboidFileSystem;
import zombie.ai.GameCharacterAIBrain;
import zombie.ai.MapKnowledge;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.ai.states.AttackState;
import zombie.ai.states.BurntToDeath;
import zombie.ai.states.ClimbDownSheetRopeState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbOverWallState;
import zombie.ai.states.ClimbSheetRopeState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.CloseWindowState;
import zombie.ai.states.CrawlingZombieTurnState;
import zombie.ai.states.FakeDeadAttackState;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.FishingState;
import zombie.ai.states.FitnessState;
import zombie.ai.states.IdleState;
import zombie.ai.states.LungeState;
import zombie.ai.states.OpenWindowState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.PlayerActionsState;
import zombie.ai.states.PlayerAimState;
import zombie.ai.states.PlayerEmoteState;
import zombie.ai.states.PlayerExtState;
import zombie.ai.states.PlayerFallDownState;
import zombie.ai.states.PlayerFallingState;
import zombie.ai.states.PlayerGetUpState;
import zombie.ai.states.PlayerHitReactionPVPState;
import zombie.ai.states.PlayerHitReactionState;
import zombie.ai.states.PlayerKnockedDown;
import zombie.ai.states.PlayerOnGroundState;
import zombie.ai.states.PlayerSitOnGroundState;
import zombie.ai.states.PlayerStrafeState;
import zombie.ai.states.SmashWindowState;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.SwipeStatePlayer;
import zombie.ai.states.ThumpState;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.ZombieFallDownState;
import zombie.ai.states.ZombieGetDownState;
import zombie.ai.states.ZombieGetUpState;
import zombie.ai.states.ZombieIdleState;
import zombie.ai.states.ZombieOnGroundState;
import zombie.ai.states.ZombieReanimateState;
import zombie.ai.states.ZombieSittingState;
import zombie.asset.Asset;
import zombie.asset.AssetPath;
import zombie.audio.BaseSoundBank;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.DummySoundBank;
import zombie.audio.DummySoundEmitter;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.audio.parameters.ParameterRoomType;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.BloodClothingType;
import zombie.characters.CharacterActionAnims;
import zombie.characters.CharacterSoundEmitter;
import zombie.characters.DummyCharacterSoundEmitter;
import zombie.characters.Faction;
import zombie.characters.HairOutfitDefinitions;
import zombie.characters.HaloTextHelper;
import zombie.characters.IsoDummyCameraCharacter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.Safety;
import zombie.characters.Stats;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorFactory;
import zombie.characters.ZombiesZoneDefinition;
import zombie.characters.AttachedItems.AttachedItem;
import zombie.characters.AttachedItems.AttachedItems;
import zombie.characters.AttachedItems.AttachedLocation;
import zombie.characters.AttachedItems.AttachedLocationGroup;
import zombie.characters.AttachedItems.AttachedLocations;
import zombie.characters.AttachedItems.AttachedWeaponDefinitions;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.BodyDamage.Fitness;
import zombie.characters.BodyDamage.Metabolics;
import zombie.characters.BodyDamage.Nutrition;
import zombie.characters.BodyDamage.Thermoregulator;
import zombie.characters.CharacterTimedActions.LuaTimedAction;
import zombie.characters.CharacterTimedActions.LuaTimedActionNew;
import zombie.characters.Moodles.Moodle;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.Moodles.Moodles;
import zombie.characters.WornItems.BodyLocation;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.characters.WornItems.BodyLocations;
import zombie.characters.WornItems.WornItem;
import zombie.characters.WornItems.WornItems;
import zombie.characters.action.ActionGroup;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.ObservationFactory;
import zombie.characters.traits.TraitCollection;
import zombie.characters.traits.TraitFactory;
import zombie.chat.ChatBase;
import zombie.chat.ChatManager;
import zombie.chat.ChatMessage;
import zombie.chat.ServerChatMessage;
import zombie.commands.PlayerType;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigOption;
import zombie.config.DoubleConfigOption;
import zombie.config.EnumConfigOption;
import zombie.config.IntegerConfigOption;
import zombie.config.StringConfigOption;
import zombie.core.BoxedStaticValues;
import zombie.core.Clipboard;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.GameVersion;
import zombie.core.ImmutableColor;
import zombie.core.IndieFileLoader;
import zombie.core.Language;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.ThreadGroups;
import zombie.core.Translator;
import zombie.core.fonts.AngelCodeFont;
import zombie.core.input.Input;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.opengl.RenderThread;
import zombie.core.physics.Bullet;
import zombie.core.physics.WorldSimulation;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.VoiceManager;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.advancedanimation.AnimNodeAssetManager;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import zombie.core.skinnedmodel.advancedanimation.debug.AnimatorDebugMonitor;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelAssetManager;
import zombie.core.skinnedmodel.model.WorldItemModelDrawer;
import zombie.core.skinnedmodel.population.BeardStyle;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingDecalGroup;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.DefaultClothing;
import zombie.core.skinnedmodel.population.HairStyle;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
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
import zombie.debug.BooleanDebugOption;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.debug.LineDrawer;
import zombie.erosion.ErosionConfig;
import zombie.erosion.ErosionData;
import zombie.erosion.ErosionMain;
import zombie.erosion.season.ErosionSeason;
import zombie.gameStates.AnimationViewerState;
import zombie.gameStates.AttachmentEditorState;
import zombie.gameStates.ChooseGameInfo;
import zombie.gameStates.ConnectToServerState;
import zombie.gameStates.DebugChunkState;
import zombie.gameStates.DebugGlobalObjectState;
import zombie.gameStates.GameLoadingState;
import zombie.gameStates.GameState;
import zombie.gameStates.IngameState;
import zombie.gameStates.LoadingQueueState;
import zombie.gameStates.MainScreenState;
import zombie.globalObjects.CGlobalObject;
import zombie.globalObjects.CGlobalObjectSystem;
import zombie.globalObjects.CGlobalObjects;
import zombie.globalObjects.SGlobalObject;
import zombie.globalObjects.SGlobalObjectSystem;
import zombie.globalObjects.SGlobalObjects;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.inventory.FixingManager;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemPickerJava;
import zombie.inventory.ItemType;
import zombie.inventory.RecipeManager;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.AlarmClockClothing;
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
import zombie.inventory.types.MapItem;
import zombie.inventory.types.Moveable;
import zombie.inventory.types.Radio;
import zombie.inventory.types.WeaponPart;
import zombie.inventory.types.WeaponType;
import zombie.iso.BentFences;
import zombie.iso.BrokenFences;
import zombie.iso.BuildingDef;
import zombie.iso.CellLoader;
import zombie.iso.ContainerOverlays;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoDirectionSet;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoHeatSource;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoLot;
import zombie.iso.IsoLuaMover;
import zombie.iso.IsoMarkers;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaChunk;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoPuddles;
import zombie.iso.IsoPushableObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWaterGeometry;
import zombie.iso.IsoWorld;
import zombie.iso.LightingJNI;
import zombie.iso.LosUtil;
import zombie.iso.MetaObject;
import zombie.iso.MultiStageBuilding;
import zombie.iso.RoomDef;
import zombie.iso.SearchMode;
import zombie.iso.SliceY;
import zombie.iso.TileOverlays;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.iso.WorldMarkers;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.isoregion.IsoRegionLogType;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.areas.isoregion.IsoRegionsLogger;
import zombie.iso.areas.isoregion.IsoRegionsRenderer;
import zombie.iso.areas.isoregion.data.DataCell;
import zombie.iso.areas.isoregion.data.DataChunk;
import zombie.iso.areas.isoregion.regions.IsoChunkRegion;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;
import zombie.iso.objects.BSFurnace;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoBrokenGlass;
import zombie.iso.objects.IsoCarBatteryCharger;
import zombie.iso.objects.IsoClothingDryer;
import zombie.iso.objects.IsoClothingWasher;
import zombie.iso.objects.IsoCombinationWasherDryer;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoJukebox;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoMolotovCocktail;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoStackedWasherDryer;
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
import zombie.iso.weather.ClimateForecaster;
import zombie.iso.weather.ClimateHistory;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.ClimateMoon;
import zombie.iso.weather.ClimateValues;
import zombie.iso.weather.Temperature;
import zombie.iso.weather.ThunderStorm;
import zombie.iso.weather.WeatherPeriod;
import zombie.iso.weather.WorldFlares;
import zombie.iso.weather.fog.ImprovedFog;
import zombie.iso.weather.fx.IsoWeatherFX;
import zombie.modding.ActiveMods;
import zombie.modding.ActiveModsFile;
import zombie.modding.ModUtilsJava;
import zombie.network.CoopMaster;
import zombie.network.DBResult;
import zombie.network.DBTicket;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ItemTransactionManager;
import zombie.network.MPStatistic;
import zombie.network.MPStatistics;
import zombie.network.NetChecksum;
import zombie.network.NetworkAIParams;
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
import zombie.profanity.ProfanityFilter;
import zombie.radio.ChannelCategory;
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
import zombie.radio.media.MediaData;
import zombie.radio.media.RecordedMedia;
import zombie.radio.scripting.DynamicRadioChannel;
import zombie.radio.scripting.RadioBroadCast;
import zombie.radio.scripting.RadioChannel;
import zombie.radio.scripting.RadioLine;
import zombie.radio.scripting.RadioScript;
import zombie.radio.scripting.RadioScriptManager;
import zombie.randomizedWorld.RandomizedWorldBase;
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
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBandPractice;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBathroomZed;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBedroomZed;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBleach;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSCorpsePsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSDeadDrunk;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSFootballNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSGunmanInBathroom;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSGunslinger;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHenDo;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHockeyPsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHouseParty;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPokerNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPoliceAtHouse;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPrisonEscape;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPrisonEscapeWithPolice;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSkeletonPsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSpecificProfession;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSStagDo;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSStudentNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSuicidePact;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSTinFoilHat;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSZombieLockedBathroom;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSZombiesEating;
import zombie.randomizedWorld.randomizedDeadSurvivor.RandomizedDeadSurvivorBase;
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
import zombie.randomizedWorld.randomizedZoneStory.RandomizedZoneStoryBase;
import zombie.savefile.ClientPlayerDB;
import zombie.savefile.PlayerDBHelper;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.EvolvedRecipe;
import zombie.scripting.objects.Fixing;
import zombie.scripting.objects.GameSoundScript;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.ItemRecipe;
import zombie.scripting.objects.MannequinScript;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.ModelScript;
import zombie.scripting.objects.MovableRecipe;
import zombie.scripting.objects.Recipe;
import zombie.scripting.objects.ScriptModule;
import zombie.scripting.objects.VehicleScript;
import zombie.spnetwork.SinglePlayerClient;
import zombie.text.templating.ReplaceProviderCharacter;
import zombie.text.templating.TemplateText;
import zombie.ui.ActionProgressBar;
import zombie.ui.Clock;
import zombie.ui.ModalDialog;
import zombie.ui.MoodlesUI;
import zombie.ui.NewHealthPanel;
import zombie.ui.ObjectTooltip;
import zombie.ui.RadarPanel;
import zombie.ui.RadialMenu;
import zombie.ui.RadialProgressBar;
import zombie.ui.SpeedControls;
import zombie.ui.TextDrawObject;
import zombie.ui.TextManager;
import zombie.ui.UI3DModel;
import zombie.ui.UIDebugConsole;
import zombie.ui.UIElement;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.ui.UIServerToolbox;
import zombie.ui.UITextBox2;
import zombie.ui.UITransition;
import zombie.ui.VehicleGauge;
import zombie.util.AddCoopPlayer;
import zombie.util.PZCalendar;
import zombie.util.PublicServerUtil;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.list.PZArrayList;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.EditVehicleState;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PathFindState2;
import zombie.vehicles.UI3DScene;
import zombie.vehicles.VehicleDoor;
import zombie.vehicles.VehicleLight;
import zombie.vehicles.VehicleManager;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehicleType;
import zombie.vehicles.VehicleWindow;
import zombie.vehicles.VehiclesDB2;
import zombie.world.moddata.ModData;
import zombie.worldMap.UIWorldMap;


public final class LuaManager {
	public static KahluaConverterManager converterManager = new KahluaConverterManager();
	public static J2SEPlatform platform = new J2SEPlatform();
	public static KahluaTable env;
	public static KahluaThread thread;
	public static KahluaThread debugthread;
	public static LuaCaller caller;
	public static LuaCaller debugcaller;
	public static LuaManager.Exposer exposer;
	public static ArrayList loaded;
	private static final HashSet loading;
	public static HashMap loadedReturn;
	public static boolean checksumDone;
	public static ArrayList loadList;
	static ArrayList paths;
	private static final HashMap luaFunctionMap;
	private static final HashSet s_wiping;

	public static void outputTable(KahluaTable kahluaTable, int int1) {
	}

	private static void wipeRecurse(KahluaTable kahluaTable) {
		if (!kahluaTable.isEmpty()) {
			if (!s_wiping.contains(kahluaTable)) {
				s_wiping.add(kahluaTable);
				KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
				while (kahluaTableIterator.advance()) {
					KahluaTable kahluaTable2 = (KahluaTable)Type.tryCastTo(kahluaTableIterator.getValue(), KahluaTable.class);
					if (kahluaTable2 != null) {
						wipeRecurse(kahluaTable2);
					}
				}

				s_wiping.remove(kahluaTable);
				kahluaTable.wipe();
			}
		}
	}

	public static void init() {
		loaded.clear();
		loading.clear();
		loadedReturn.clear();
		paths.clear();
		luaFunctionMap.clear();
		platform = new J2SEPlatform();
		if (env != null) {
			s_wiping.clear();
			wipeRecurse(env);
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
		if (exposer != null) {
			exposer.destroy();
		}

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
		File file = ZomboidFileSystem.instance.getMediaFile("lua" + File.separator + string);
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
				URI uRI = (new File(string3)).getCanonicalFile().toURI();
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
			DebugLog.General.println("luaChecksum: " + GameServer.checksum);
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
				String string = file.getCanonicalFile().getAbsolutePath();
				searchFolders(uRI, new File(string + File.separator + stringArray[int1]));
			}
		} else if (file.getAbsolutePath().toLowerCase().endsWith(".lua")) {
			loadList.add(ZomboidFileSystem.instance.getRelativeFile(uRI, file.getAbsolutePath()));
		}
	}

	public static String getLuaCacheDir() {
		String string = ZomboidFileSystem.instance.getCacheDir();
		String string2 = string + File.separator + "Lua";
		File file = new File(string2);
		if (!file.exists()) {
			file.mkdir();
		}

		return string2;
	}

	public static String getSandboxCacheDir() {
		String string = ZomboidFileSystem.instance.getCacheDir();
		String string2 = string + File.separator + "Sandbox Presets";
		File file = new File(string2);
		if (!file.exists()) {
			file.mkdir();
		}

		return string2;
	}

	public static void fillContainer(ItemContainer itemContainer, IsoPlayer player) {
		ItemPickerJava.fillContainer(itemContainer, player);
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

	public static void transferItem(IsoGameCharacter gameCharacter, InventoryItem inventoryItem, ItemContainer itemContainer, ItemContainer itemContainer2) {
		LuaClosure luaClosure = (LuaClosure)env.rawget("javaTransferItems");
		caller.pcall(thread, luaClosure, new Object[]{gameCharacter, inventoryItem, itemContainer, itemContainer2});
	}

	public static void dropItem(InventoryItem inventoryItem) {
		LuaClosure luaClosure = getDotDelimitedClosure("ISInventoryPaneContextMenu.dropItem");
		caller.pcall(thread, luaClosure, (Object)inventoryItem);
	}

	public static IsoGridSquare AdjacentFreeTileFinder(IsoGridSquare square, IsoPlayer player) {
		KahluaTable kahluaTable = (KahluaTable)env.rawget("AdjacentFreeTileFinder");
		LuaClosure luaClosure = (LuaClosure)kahluaTable.rawget("Find");
		return (IsoGridSquare)caller.pcall(thread, luaClosure, new Object[]{square, player}[1]);
	}

	public static Object RunLua(String string) {
		return RunLua(string, false);
	}

	public static Object RunLua(String string, boolean boolean1) {
		String string2 = string.replace("\\", "/");
		if (loading.contains(string2)) {
			DebugLog.Lua.warn("recursive require(): %s", string2);
			return null;
		} else {
			loading.add(string2);
			Object object;
			try {
				object = RunLuaInternal(string, boolean1);
			} finally {
				loading.remove(string2);
			}

			return object;
		}
	}

	private static Object RunLuaInternal(String string, boolean boolean1) {
		string = string.replace("\\", "/");
		if (loaded.contains(string)) {
			return loadedReturn.get(string);
		} else {
			FuncState.currentFile = string.substring(string.lastIndexOf(47) + 1);
			FuncState.currentfullFile = string;
			String string2 = string;
			string = ZomboidFileSystem.instance.getString(string.replace("\\", "/"));
			if (DebugLog.isEnabled(DebugType.Lua)) {
				DebugLog.Lua.println("Loading: " + ZomboidFileSystem.instance.getRelativeFile(string));
			}

			InputStreamReader inputStreamReader;
			try {
				inputStreamReader = IndieFileLoader.getStreamReader(string);
			} catch (FileNotFoundException fileNotFoundException) {
				ExceptionLogger.logException(fileNotFoundException);
				return null;
			}

			LuaCompiler.rewriteEvents = boolean1;
			LuaClosure luaClosure;
			try {
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				try {
					luaClosure = LuaCompiler.loadis((Reader)bufferedReader, string.substring(string.lastIndexOf(47) + 1), env);
				} catch (Throwable throwable) {
					try {
						bufferedReader.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedReader.close();
			} catch (Exception exception) {
				Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, "Error found in LUA file: " + string, (Object)null);
				ExceptionLogger.logException(exception);
				thread.debugException(exception);
				return null;
			}

			luaFunctionMap.clear();
			AttachedWeaponDefinitions.instance.m_dirty = true;
			DefaultClothing.instance.m_dirty = true;
			HairOutfitDefinitions.instance.m_dirty = true;
			ZombiesZoneDefinition.bDirty = true;
			LuaReturn luaReturn = caller.protectedCall(thread, luaClosure);
			if (!luaReturn.isSuccess()) {
				Logger.getLogger(IsoWorld.class.getName()).log(Level.SEVERE, luaReturn.getErrorString(), (Object)null);
				if (luaReturn.getJavaException() != null) {
					Logger.getLogger(IsoWorld.class.getName()).log(Level.SEVERE, luaReturn.getJavaException().toString(), (Object)null);
				}

				Logger.getLogger(IsoWorld.class.getName()).log(Level.SEVERE, luaReturn.getLuaStackTrace(), (Object)null);
			}

			loaded.add(string2);
			Object object = luaReturn.isSuccess() && luaReturn.size() > 0 ? luaReturn.getFirst() : null;
			if (object != null) {
				loadedReturn.put(string2, object);
			} else {
				loadedReturn.remove(string2);
			}

			LuaCompiler.rewriteEvents = false;
			return object;
		}
	}

	public static Object getFunctionObject(String string) {
		if (string != null && !string.isEmpty()) {
			Object object = luaFunctionMap.get(string);
			if (object != null) {
				return object;
			} else {
				KahluaTable kahluaTable = env;
				if (string.contains(".")) {
					String[] stringArray = string.split("\\.");
					for (int int1 = 0; int1 < stringArray.length - 1; ++int1) {
						KahluaTable kahluaTable2 = (KahluaTable)Type.tryCastTo(kahluaTable.rawget(stringArray[int1]), KahluaTable.class);
						if (kahluaTable2 == null) {
							DebugLog.General.error("no such function \"%s\"", string);
							return null;
						}

						kahluaTable = kahluaTable2;
					}

					object = kahluaTable.rawget(stringArray[stringArray.length - 1]);
				} else {
					object = kahluaTable.rawget(string);
				}

				if (!(object instanceof JavaFunction) && !(object instanceof LuaClosure)) {
					DebugLog.General.error("no such function \"%s\"", string);
					return null;
				} else {
					luaFunctionMap.put(string, object);
					return object;
				}
			}
		} else {
			return null;
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

	private static void exposeLuaCalendar() {
		KahluaTable kahluaTable = (KahluaTable)env.rawget("PZCalendar");
		if (kahluaTable != null) {
			Field[] fieldArray = Calendar.class.getFields();
			try {
				Field[] fieldArray2 = fieldArray;
				int int1 = fieldArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					Field field = fieldArray2[int2];
					if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && field.getType().equals(Integer.TYPE)) {
						kahluaTable.rawset(field.getName(), BoxedStaticValues.toDouble((double)field.getInt((Object)null)));
					}
				}
			} catch (Exception exception) {
			}

			env.rawset("Calendar", kahluaTable);
		}
	}

	public static String getHourMinuteJava() {
		Calendar calendar = Calendar.getInstance();
		String string = calendar.get(12).makeConcatWithConstants < invokedynamic > (calendar.get(12));
		if (Calendar.getInstance().get(12) < 10) {
			string = "0" + string;
		}

		int int1 = Calendar.getInstance().get(11);
		return int1 + ":" + string;
	}

	public static KahluaTable copyTable(KahluaTable kahluaTable) {
		return copyTable((KahluaTable)null, kahluaTable);
	}

	public static KahluaTable copyTable(KahluaTable kahluaTable, KahluaTable kahluaTable2) {
		if (kahluaTable == null) {
			kahluaTable = platform.newTable();
		} else {
			kahluaTable.wipe();
		}

		if (kahluaTable2 != null && !kahluaTable2.isEmpty()) {
			KahluaTableIterator kahluaTableIterator = kahluaTable2.iterator();
			while (kahluaTableIterator.advance()) {
				Object object = kahluaTableIterator.getKey();
				Object object2 = kahluaTableIterator.getValue();
				if (object2 instanceof KahluaTable) {
					kahluaTable.rawset(object, copyTable((KahluaTable)null, (KahluaTable)object2));
				} else {
					kahluaTable.rawset(object, object2);
				}
			}

			return kahluaTable;
		} else {
			return kahluaTable;
		}
	}

	static  {
		caller = new LuaCaller(converterManager);
		debugcaller = new LuaCaller(converterManager);
		loaded = new ArrayList();
		loading = new HashSet();
		loadedReturn = new HashMap();
		checksumDone = false;
		loadList = new ArrayList();
		paths = new ArrayList();
		luaFunctionMap = new HashMap();
		s_wiping = new HashSet();
	}

	public static final class Exposer extends LuaJavaClassExposer {
		private final HashSet exposed = new HashSet();

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
			this.setExposed(EnumMap.class);
			this.setExposed(HashMap.class);
			this.setExposed(LinkedList.class);
			this.setExposed(Stack.class);
			this.setExposed(Vector.class);
			this.setExposed(Iterator.class);
			this.setExposed(EmitterType.class);
			this.setExposed(FMODAudio.class);
			this.setExposed(FMODSoundBank.class);
			this.setExposed(FMODSoundEmitter.class);
			this.setExposed(Vector2f.class);
			this.setExposed(Vector3f.class);
			this.setExposed(KahluaUtil.class);
			this.setExposed(DummySoundBank.class);
			this.setExposed(DummySoundEmitter.class);
			this.setExposed(BaseSoundEmitter.class);
			this.setExposed(GameSound.class);
			this.setExposed(GameSoundClip.class);
			this.setExposed(AttackState.class);
			this.setExposed(BurntToDeath.class);
			this.setExposed(ClimbDownSheetRopeState.class);
			this.setExposed(ClimbOverFenceState.class);
			this.setExposed(ClimbOverWallState.class);
			this.setExposed(ClimbSheetRopeState.class);
			this.setExposed(ClimbThroughWindowState.class);
			this.setExposed(CloseWindowState.class);
			this.setExposed(CrawlingZombieTurnState.class);
			this.setExposed(FakeDeadAttackState.class);
			this.setExposed(FakeDeadZombieState.class);
			this.setExposed(FishingState.class);
			this.setExposed(FitnessState.class);
			this.setExposed(IdleState.class);
			this.setExposed(LungeState.class);
			this.setExposed(OpenWindowState.class);
			this.setExposed(PathFindState.class);
			this.setExposed(PlayerActionsState.class);
			this.setExposed(PlayerAimState.class);
			this.setExposed(PlayerEmoteState.class);
			this.setExposed(PlayerExtState.class);
			this.setExposed(PlayerFallDownState.class);
			this.setExposed(PlayerFallingState.class);
			this.setExposed(PlayerGetUpState.class);
			this.setExposed(PlayerHitReactionPVPState.class);
			this.setExposed(PlayerHitReactionState.class);
			this.setExposed(PlayerKnockedDown.class);
			this.setExposed(PlayerOnGroundState.class);
			this.setExposed(PlayerSitOnGroundState.class);
			this.setExposed(PlayerStrafeState.class);
			this.setExposed(SmashWindowState.class);
			this.setExposed(StaggerBackState.class);
			this.setExposed(SwipeStatePlayer.class);
			this.setExposed(ThumpState.class);
			this.setExposed(WalkTowardState.class);
			this.setExposed(ZombieFallDownState.class);
			this.setExposed(ZombieGetDownState.class);
			this.setExposed(ZombieGetUpState.class);
			this.setExposed(ZombieIdleState.class);
			this.setExposed(ZombieOnGroundState.class);
			this.setExposed(ZombieReanimateState.class);
			this.setExposed(ZombieSittingState.class);
			this.setExposed(GameCharacterAIBrain.class);
			this.setExposed(MapKnowledge.class);
			this.setExposed(BodyPartType.class);
			this.setExposed(BodyPart.class);
			this.setExposed(BodyDamage.class);
			this.setExposed(Thermoregulator.class);
			this.setExposed(Thermoregulator.ThermalNode.class);
			this.setExposed(Metabolics.class);
			this.setExposed(Fitness.class);
			this.setExposed(GameKeyboard.class);
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
			this.setExposed(IsoGameCharacter.Location.class);
			this.setExposed(IsoGameCharacter.PerkInfo.class);
			this.setExposed(IsoGameCharacter.XP.class);
			this.setExposed(IsoGameCharacter.CharacterTraits.class);
			this.setExposed(TraitCollection.TraitSlot.class);
			this.setExposed(TraitCollection.class);
			this.setExposed(IsoPlayer.class);
			this.setExposed(IsoSurvivor.class);
			this.setExposed(IsoZombie.class);
			this.setExposed(CharacterActionAnims.class);
			this.setExposed(HaloTextHelper.class);
			this.setExposed(HaloTextHelper.ColorRGB.class);
			this.setExposed(NetworkAIParams.class);
			this.setExposed(BloodBodyPartType.class);
			this.setExposed(Clipboard.class);
			this.setExposed(AngelCodeFont.class);
			this.setExposed(ZLogger.class);
			this.setExposed(PropertyContainer.class);
			this.setExposed(ClothingItem.class);
			this.setExposed(AnimatorDebugMonitor.class);
			this.setExposed(ColorInfo.class);
			this.setExposed(Texture.class);
			this.setExposed(SteamFriend.class);
			this.setExposed(SteamUGCDetails.class);
			this.setExposed(SteamWorkshopItem.class);
			this.setExposed(Color.class);
			this.setExposed(Colors.class);
			this.setExposed(Core.class);
			this.setExposed(GameVersion.class);
			this.setExposed(ImmutableColor.class);
			this.setExposed(Language.class);
			this.setExposed(PerformanceSettings.class);
			this.setExposed(SpriteRenderer.class);
			this.setExposed(Translator.class);
			this.setExposed(PZMath.class);
			this.setExposed(DebugLog.class);
			this.setExposed(DebugOptions.class);
			this.setExposed(BooleanDebugOption.class);
			this.setExposed(DebugType.class);
			this.setExposed(ErosionConfig.class);
			this.setExposed(ErosionConfig.Debug.class);
			this.setExposed(ErosionConfig.Season.class);
			this.setExposed(ErosionConfig.Seeds.class);
			this.setExposed(ErosionConfig.Time.class);
			this.setExposed(ErosionMain.class);
			this.setExposed(ErosionSeason.class);
			this.setExposed(AnimationViewerState.class);
			this.setExposed(AnimationViewerState.BooleanDebugOption.class);
			this.setExposed(AttachmentEditorState.class);
			this.setExposed(ChooseGameInfo.Mod.class);
			this.setExposed(DebugChunkState.class);
			this.setExposed(DebugChunkState.BooleanDebugOption.class);
			this.setExposed(DebugGlobalObjectState.class);
			this.setExposed(GameLoadingState.class);
			this.setExposed(LoadingQueueState.class);
			this.setExposed(MainScreenState.class);
			this.setExposed(CGlobalObject.class);
			this.setExposed(CGlobalObjects.class);
			this.setExposed(CGlobalObjectSystem.class);
			this.setExposed(SGlobalObject.class);
			this.setExposed(SGlobalObjects.class);
			this.setExposed(SGlobalObjectSystem.class);
			this.setExposed(Mouse.class);
			this.setExposed(AlarmClock.class);
			this.setExposed(AlarmClockClothing.class);
			this.setExposed(Clothing.class);
			this.setExposed(Clothing.ClothingPatch.class);
			this.setExposed(Clothing.ClothingPatchFabricType.class);
			this.setExposed(ComboItem.class);
			this.setExposed(Drainable.class);
			this.setExposed(DrainableComboItem.class);
			this.setExposed(Food.class);
			this.setExposed(HandWeapon.class);
			this.setExposed(InventoryContainer.class);
			this.setExposed(Key.class);
			this.setExposed(KeyRing.class);
			this.setExposed(Literature.class);
			this.setExposed(MapItem.class);
			this.setExposed(Moveable.class);
			this.setExposed(Radio.class);
			this.setExposed(WeaponPart.class);
			this.setExposed(ItemContainer.class);
			this.setExposed(ItemPickerJava.class);
			this.setExposed(InventoryItem.class);
			this.setExposed(InventoryItemFactory.class);
			this.setExposed(FixingManager.class);
			this.setExposed(RecipeManager.class);
			this.setExposed(IsoRegions.class);
			this.setExposed(IsoRegionsLogger.class);
			this.setExposed(IsoRegionsLogger.IsoRegionLog.class);
			this.setExposed(IsoRegionLogType.class);
			this.setExposed(DataCell.class);
			this.setExposed(DataChunk.class);
			this.setExposed(IsoChunkRegion.class);
			this.setExposed(IsoWorldRegion.class);
			this.setExposed(IsoRegionsRenderer.class);
			this.setExposed(IsoRegionsRenderer.BooleanDebugOption.class);
			this.setExposed(IsoBuilding.class);
			this.setExposed(IsoRoom.class);
			this.setExposed(SafeHouse.class);
			this.setExposed(BarricadeAble.class);
			this.setExposed(IsoBarbecue.class);
			this.setExposed(IsoBarricade.class);
			this.setExposed(IsoBrokenGlass.class);
			this.setExposed(IsoClothingDryer.class);
			this.setExposed(IsoClothingWasher.class);
			this.setExposed(IsoCombinationWasherDryer.class);
			this.setExposed(IsoStackedWasherDryer.class);
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
			this.setExposed(IsoMannequin.class);
			this.setExposed(IsoMolotovCocktail.class);
			this.setExposed(IsoWaveSignal.class);
			this.setExposed(IsoRadio.class);
			this.setExposed(IsoTelevision.class);
			this.setExposed(IsoStackedWasherDryer.class);
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
			this.setExposed(HumanVisual.class);
			this.setExposed(ItemVisual.class);
			this.setExposed(ItemVisuals.class);
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
			this.setExposed(ClimateColorInfo.class);
			this.setExposed(ClimateValues.class);
			this.setExposed(ClimateForecaster.class);
			this.setExposed(ClimateForecaster.DayForecast.class);
			this.setExposed(ClimateForecaster.ForecastValue.class);
			this.setExposed(ClimateHistory.class);
			this.setExposed(WorldFlares.class);
			this.setExposed(WorldFlares.Flare.class);
			this.setExposed(ImprovedFog.class);
			this.setExposed(ClimateMoon.class);
			this.setExposed(IsoPuddles.class);
			this.setExposed(IsoPuddles.PuddlesFloat.class);
			this.setExposed(BentFences.class);
			this.setExposed(BrokenFences.class);
			this.setExposed(ContainerOverlays.class);
			this.setExposed(IsoChunk.class);
			this.setExposed(BuildingDef.class);
			this.setExposed(IsoCamera.class);
			this.setExposed(IsoCell.class);
			this.setExposed(IsoChunkMap.class);
			this.setExposed(IsoDirections.class);
			this.setExposed(IsoDirectionSet.class);
			this.setExposed(IsoGridSquare.class);
			this.setExposed(IsoHeatSource.class);
			this.setExposed(IsoLightSource.class);
			this.setExposed(IsoLot.class);
			this.setExposed(IsoLuaMover.class);
			this.setExposed(IsoMetaChunk.class);
			this.setExposed(IsoMetaCell.class);
			this.setExposed(IsoMetaGrid.class);
			this.setExposed(IsoMetaGrid.Trigger.class);
			this.setExposed(IsoMetaGrid.VehicleZone.class);
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
			this.setExposed(TileOverlays.class);
			this.setExposed(Vector2.class);
			this.setExposed(Vector3.class);
			this.setExposed(WorldMarkers.class);
			this.setExposed(WorldMarkers.DirectionArrow.class);
			this.setExposed(WorldMarkers.GridSquareMarker.class);
			this.setExposed(WorldMarkers.PlayerHomingPoint.class);
			this.setExposed(SearchMode.class);
			this.setExposed(SearchMode.PlayerSearchMode.class);
			this.setExposed(SearchMode.SearchModeFloat.class);
			this.setExposed(IsoMarkers.class);
			this.setExposed(IsoMarkers.IsoMarker.class);
			this.setExposed(IsoMarkers.CircleIsoMarker.class);
			this.setExposed(LuaEventManager.class);
			this.setExposed(MapObjects.class);
			this.setExposed(ActiveMods.class);
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
			this.setExposed(DynamicRadioChannel.class);
			this.setExposed(RadioChannel.class);
			this.setExposed(RadioBroadCast.class);
			this.setExposed(RadioLine.class);
			this.setExposed(RadioScript.class);
			this.setExposed(RadioScript.ExitOption.class);
			this.setExposed(ChannelCategory.class);
			this.setExposed(SLSoundManager.class);
			this.setExposed(StorySound.class);
			this.setExposed(StorySoundEvent.class);
			this.setExposed(EventSound.class);
			this.setExposed(DataPoint.class);
			this.setExposed(RecordedMedia.class);
			this.setExposed(MediaData.class);
			this.setExposed(EvolvedRecipe.class);
			this.setExposed(Fixing.class);
			this.setExposed(Fixing.Fixer.class);
			this.setExposed(Fixing.FixerSkill.class);
			this.setExposed(GameSoundScript.class);
			this.setExposed(Item.class);
			this.setExposed(Item.Type.class);
			this.setExposed(ItemRecipe.class);
			this.setExposed(MannequinScript.class);
			this.setExposed(ModelAttachment.class);
			this.setExposed(ModelScript.class);
			this.setExposed(MovableRecipe.class);
			this.setExposed(Recipe.class);
			this.setExposed(Recipe.RequiredSkill.class);
			this.setExposed(Recipe.Result.class);
			this.setExposed(Recipe.Source.class);
			this.setExposed(ScriptModule.class);
			this.setExposed(VehicleScript.class);
			this.setExposed(VehicleScript.Area.class);
			this.setExposed(VehicleScript.Model.class);
			this.setExposed(VehicleScript.Part.class);
			this.setExposed(VehicleScript.Passenger.class);
			this.setExposed(VehicleScript.PhysicsShape.class);
			this.setExposed(VehicleScript.Position.class);
			this.setExposed(VehicleScript.Wheel.class);
			this.setExposed(ScriptManager.class);
			this.setExposed(TemplateText.class);
			this.setExposed(ReplaceProviderCharacter.class);
			this.setExposed(ActionProgressBar.class);
			this.setExposed(Clock.class);
			this.setExposed(UIDebugConsole.class);
			this.setExposed(ModalDialog.class);
			this.setExposed(MoodlesUI.class);
			this.setExposed(NewHealthPanel.class);
			this.setExposed(ObjectTooltip.class);
			this.setExposed(ObjectTooltip.Layout.class);
			this.setExposed(ObjectTooltip.LayoutItem.class);
			this.setExposed(RadarPanel.class);
			this.setExposed(RadialMenu.class);
			this.setExposed(RadialProgressBar.class);
			this.setExposed(SpeedControls.class);
			this.setExposed(TextManager.class);
			this.setExposed(UI3DModel.class);
			this.setExposed(UIElement.class);
			this.setExposed(UIFont.class);
			this.setExposed(UITransition.class);
			this.setExposed(UIManager.class);
			this.setExposed(UIServerToolbox.class);
			this.setExposed(UITextBox2.class);
			this.setExposed(VehicleGauge.class);
			this.setExposed(TextDrawObject.class);
			this.setExposed(PZArrayList.class);
			this.setExposed(PZCalendar.class);
			this.setExposed(BaseVehicle.class);
			this.setExposed(EditVehicleState.class);
			this.setExposed(PathFindBehavior2.BehaviorResult.class);
			this.setExposed(PathFindBehavior2.class);
			this.setExposed(PathFindState2.class);
			this.setExposed(UI3DScene.class);
			this.setExposed(VehicleDoor.class);
			this.setExposed(VehicleLight.class);
			this.setExposed(VehiclePart.class);
			this.setExposed(VehicleType.class);
			this.setExposed(VehicleWindow.class);
			this.setExposed(AttachedItem.class);
			this.setExposed(AttachedItems.class);
			this.setExposed(AttachedLocation.class);
			this.setExposed(AttachedLocationGroup.class);
			this.setExposed(AttachedLocations.class);
			this.setExposed(WornItems.class);
			this.setExposed(WornItem.class);
			this.setExposed(BodyLocation.class);
			this.setExposed(BodyLocationGroup.class);
			this.setExposed(BodyLocations.class);
			this.setExposed(DummySoundManager.class);
			this.setExposed(GameSounds.class);
			this.setExposed(GameTime.class);
			this.setExposed(GameWindow.class);
			this.setExposed(SandboxOptions.class);
			this.setExposed(SandboxOptions.BooleanSandboxOption.class);
			this.setExposed(SandboxOptions.DoubleSandboxOption.class);
			this.setExposed(SandboxOptions.StringSandboxOption.class);
			this.setExposed(SandboxOptions.EnumSandboxOption.class);
			this.setExposed(SandboxOptions.IntegerSandboxOption.class);
			this.setExposed(SoundManager.class);
			this.setExposed(SystemDisabler.class);
			this.setExposed(VirtualZombieManager.class);
			this.setExposed(WorldSoundManager.class);
			this.setExposed(WorldSoundManager.WorldSound.class);
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
			this.setExposed(RandomizedWorldBase.class);
			this.setExposed(RandomizedBuildingBase.class);
			this.setExposed(RBBurntFireman.class);
			this.setExposed(RBBasic.class);
			this.setExposed(RBBurnt.class);
			this.setExposed(RBOther.class);
			this.setExposed(RBStripclub.class);
			this.setExposed(RBSchool.class);
			this.setExposed(RBSpiffo.class);
			this.setExposed(RBPizzaWhirled.class);
			this.setExposed(RBOffice.class);
			this.setExposed(RBHairSalon.class);
			this.setExposed(RBClinic.class);
			this.setExposed(RBPileOCrepe.class);
			this.setExposed(RBCafe.class);
			this.setExposed(RBBar.class);
			this.setExposed(RBLooted.class);
			this.setExposed(RBSafehouse.class);
			this.setExposed(RBBurntCorpse.class);
			this.setExposed(RBShopLooted.class);
			this.setExposed(RBKateAndBaldspot.class);
			this.setExposed(RandomizedDeadSurvivorBase.class);
			this.setExposed(RDSZombiesEating.class);
			this.setExposed(RDSBleach.class);
			this.setExposed(RDSDeadDrunk.class);
			this.setExposed(RDSGunmanInBathroom.class);
			this.setExposed(RDSGunslinger.class);
			this.setExposed(RDSZombieLockedBathroom.class);
			this.setExposed(RDSBandPractice.class);
			this.setExposed(RDSBathroomZed.class);
			this.setExposed(RDSBedroomZed.class);
			this.setExposed(RDSFootballNight.class);
			this.setExposed(RDSHenDo.class);
			this.setExposed(RDSStagDo.class);
			this.setExposed(RDSStudentNight.class);
			this.setExposed(RDSPokerNight.class);
			this.setExposed(RDSSuicidePact.class);
			this.setExposed(RDSPrisonEscape.class);
			this.setExposed(RDSPrisonEscapeWithPolice.class);
			this.setExposed(RDSSkeletonPsycho.class);
			this.setExposed(RDSCorpsePsycho.class);
			this.setExposed(RDSSpecificProfession.class);
			this.setExposed(RDSPoliceAtHouse.class);
			this.setExposed(RDSHouseParty.class);
			this.setExposed(RDSTinFoilHat.class);
			this.setExposed(RDSHockeyPsycho.class);
			this.setExposed(RandomizedVehicleStoryBase.class);
			this.setExposed(RVSCarCrash.class);
			this.setExposed(RVSBanditRoad.class);
			this.setExposed(RVSAmbulanceCrash.class);
			this.setExposed(RVSCrashHorde.class);
			this.setExposed(RVSCarCrashCorpse.class);
			this.setExposed(RVSPoliceBlockade.class);
			this.setExposed(RVSPoliceBlockadeShooting.class);
			this.setExposed(RVSBurntCar.class);
			this.setExposed(RVSConstructionSite.class);
			this.setExposed(RVSUtilityVehicle.class);
			this.setExposed(RVSChangingTire.class);
			this.setExposed(RVSFlippedCrash.class);
			this.setExposed(RVSTrailerCrash.class);
			this.setExposed(RandomizedZoneStoryBase.class);
			this.setExposed(RZSForestCamp.class);
			this.setExposed(RZSForestCampEaten.class);
			this.setExposed(RZSBuryingCamp.class);
			this.setExposed(RZSBeachParty.class);
			this.setExposed(RZSFishingTrip.class);
			this.setExposed(RZSBBQParty.class);
			this.setExposed(RZSHunterCamp.class);
			this.setExposed(RZSSexyTime.class);
			this.setExposed(RZSTrapperCamp.class);
			this.setExposed(RZSBaseball.class);
			this.setExposed(RZSMusicFestStage.class);
			this.setExposed(RZSMusicFest.class);
			this.setExposed(MapGroups.class);
			this.setExposed(BeardStyles.class);
			this.setExposed(BeardStyle.class);
			this.setExposed(HairStyles.class);
			this.setExposed(HairStyle.class);
			this.setExposed(BloodClothingType.class);
			this.setExposed(WeaponType.class);
			this.setExposed(IsoWaterGeometry.class);
			this.setExposed(ModData.class);
			this.setExposed(WorldMarkers.class);
			this.setExposed(ChatMessage.class);
			this.setExposed(ChatBase.class);
			this.setExposed(ServerChatMessage.class);
			this.setExposed(Safety.class);
			if (Core.bDebug) {
				this.setExposed(Field.class);
				this.setExposed(Method.class);
				this.setExposed(Coroutine.class);
			}

			UIWorldMap.setExposed(this);
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
			LuaManager.exposeLuaCalendar();
		}

		public void setExposed(Class javaClass) {
			this.exposed.add(javaClass);
		}

		public boolean shouldExpose(Class javaClass) {
			return javaClass == null ? false : this.exposed.contains(javaClass);
		}
	}

	public static class GlobalObject {
		static FileOutputStream outStream;
		static FileInputStream inStream;
		static FileReader inFileReader = null;
		static BufferedReader inBufferedReader = null;
		static long timeLastRefresh = 0L;
		private static final LuaManager.GlobalObject.TimSortComparator timSortComparator = new LuaManager.GlobalObject.TimSortComparator();

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
				if (string2.startsWith("/")) {
					string2 = string2.substring(1);
				}

				if (string3.startsWith("/")) {
					string3 = string3.substring(1);
				}

				if (StringUtils.isNullOrWhitespace(string4)) {
					string4 = "basicEffect";
				}

				if ("vehicle".equals(string4) && !Core.getInstance().getPerfReflectionsOnLoad()) {
					string4 = string4 + "_noreflect";
				}

				Model model = ModelManager.instance.tryGetLoadedModel(string2, string3, boolean1, string4, false);
				if (model != null) {
					return model;
				} else {
					ModelManager.instance.setModelMetaData(string, string2, string3, string4, boolean1);
					Model.ModelAssetParams modelAssetParams = new Model.ModelAssetParams();
					modelAssetParams.bStatic = boolean1;
					modelAssetParams.meshName = string2;
					modelAssetParams.shaderName = string4;
					modelAssetParams.textureName = string3;
					modelAssetParams.textureFlags = ModelManager.instance.getTextureFlags();
					model = (Model)ModelAssetManager.instance.load(new AssetPath(string), modelAssetParams);
					if (model != null) {
						ModelManager.instance.putLoadedModel(string2, string3, boolean1, string4, model);
					}

					return model;
				}
			} catch (Exception exception) {
				DebugLog.General.error("LuaManager.loadZomboidModel> Exception thrown loading model: " + string + " mesh:" + string2 + " tex:" + string3 + " shader:" + string4 + " isStatic:" + boolean1);
				exception.printStackTrace();
				return null;
			}
		}

		@LuaMethod(name = "setModelMetaData", global = true)
		public static void setModelMetaData(String string, String string2, String string3, String string4, boolean boolean1) {
			if (string2.startsWith("/")) {
				string2 = string2.substring(1);
			}

			if (string3.startsWith("/")) {
				string3 = string3.substring(1);
			}

			ModelManager.instance.setModelMetaData(string, string2, string3, string4, boolean1);
		}

		@LuaMethod(name = "reloadModelsMatching", global = true)
		public static void reloadModelsMatching(String string) {
			ModelManager.instance.reloadModelsMatching(string);
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

		@LuaMethod(name = "getTranslatorCredits", global = true)
		public static ArrayList getTranslatorCredits(Language language) {
			File file = new File(ZomboidFileSystem.instance.getString("media/lua/shared/Translate/" + language.name() + "/credits.txt"));
			try {
				FileReader fileReader = new FileReader(file);
				ArrayList arrayList;
				try {
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					try {
						ArrayList arrayList2 = new ArrayList();
						String string;
						while ((string = bufferedReader.readLine()) != null) {
							if (!StringUtils.isNullOrWhitespace(string)) {
								arrayList2.add(string.trim());
							}
						}

						arrayList = arrayList2;
					} catch (Throwable throwable) {
						try {
							bufferedReader.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					bufferedReader.close();
				} catch (Throwable throwable3) {
					try {
						fileReader.close();
					} catch (Throwable throwable4) {
						throwable3.addSuppressed(throwable4);
					}

					throw throwable3;
				}

				fileReader.close();
				return arrayList;
			} catch (FileNotFoundException fileNotFoundException) {
				return null;
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				return null;
			}
		}

		@LuaMethod(name = "getBehaviourDebugPlayer", global = true)
		public static IsoGameCharacter getBehaviourDebugPlayer() {
			return null;
		}

		@LuaMethod(name = "setBehaviorStep", global = true)
		public static void setBehaviorStep(boolean boolean1) {
		}

		@LuaMethod(name = "getPuddlesManager", global = true)
		public static IsoPuddles getPuddlesManager() {
			return IsoPuddles.getInstance();
		}

		@LuaMethod(name = "setPuddles", global = true)
		public static void setPuddles(float float1) {
			IsoPuddles.PuddlesFloat puddlesFloat = IsoPuddles.getInstance().getPuddlesFloat(3);
			puddlesFloat.setEnableAdmin(true);
			puddlesFloat.setAdminValue(float1);
			puddlesFloat = IsoPuddles.getInstance().getPuddlesFloat(1);
			puddlesFloat.setEnableAdmin(true);
			puddlesFloat.setAdminValue(PZMath.clamp_01(float1 * 1.2F));
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
				GameClient.connection.forceDisconnect("lua-connect");
			}

			GameClient.instance.resetDisconnectTimer();
			GameClient.bClient = true;
			GameClient.bCoopInvite = false;
			ZomboidFileSystem.instance.cleanMultiplayerSaves();
			GameClient.instance.doConnect(string, string2, string3, string4, string5, string6);
		}

		@LuaMethod(name = "serverConnectCoop", global = true)
		public static void serverConnectCoop(String string) {
			Core.GameMode = "Multiplayer";
			Core.setDifficulty("Hardcore");
			if (GameClient.connection != null) {
				GameClient.connection.forceDisconnect("lua-connect-coop");
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
				GameClient.connection.forceDisconnect("lua-force-disconnect");
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

		@LuaMethod(name = "canConnect", global = true)
		public static boolean canConnect() {
			return GameClient.instance.canConnect();
		}

		@LuaMethod(name = "getReconnectCountdownTimer", global = true)
		public static String getReconnectCountdownTimer() {
			return GameClient.instance.getReconnectCountdownTimer();
		}

		@LuaMethod(name = "getPacketCounts", global = true)
		public static KahluaTable getPacketCounts(int int1) {
			return GameClient.bClient ? PacketTypes.getPacketCounts(int1) : null;
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
				GameWindow.save(boolean1);
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
			}
		}

		@LuaMethod(name = "saveGame", global = true)
		public static void saveGame() {
			save(true);
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
			float float4 = IsoUtils.XToScreen(float1, float2, float3, 0) - IsoCamera.cameras[int1].getOffX();
			float4 /= Core.getInstance().getZoom(int1);
			return (float)IsoCamera.getScreenLeft(int1) + float4;
		}

		@LuaMethod(name = "isoToScreenY", global = true)
		public static float isoToScreenY(int int1, float float1, float float2, float float3) {
			float float4 = IsoUtils.YToScreen(float1, float2, float3, 0) - IsoCamera.cameras[int1].getOffY();
			float4 /= Core.getInstance().getZoom(int1);
			return (float)IsoCamera.getScreenTop(int1) + float4;
		}

		@LuaMethod(name = "screenToIsoX", global = true)
		public static float screenToIsoX(int int1, float float1, float float2, float float3) {
			float float4 = Core.getInstance().getZoom(int1);
			float1 -= (float)IsoCamera.getScreenLeft(int1);
			float2 -= (float)IsoCamera.getScreenTop(int1);
			return IsoCamera.cameras[int1].XToIso(float1 * float4, float2 * float4, float3);
		}

		@LuaMethod(name = "screenToIsoY", global = true)
		public static float screenToIsoY(int int1, float float1, float float2, float float3) {
			float float4 = Core.getInstance().getZoom(int1);
			float1 -= (float)IsoCamera.getScreenLeft(int1);
			float2 -= (float)IsoCamera.getScreenTop(int1);
			return IsoCamera.cameras[int1].YToIso(float1 * float4, float2 * float4, float3);
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
				IsoPlayer.setInstance(IsoPlayer.players[int1]);
				IsoCamera.CamCharacter = IsoPlayer.getInstance();
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
				return (IsoPlayer)GameServer.IDToPlayerMap.get((short)int1);
			} else {
				return GameClient.bClient ? (IsoPlayer)GameClient.IDToPlayerMap.get((short)int1) : null;
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

		@LuaMethod(name = "getDBSchema", global = true)
		public static void getDBSchema() {
			GameClient.instance.getDBSchema();
		}

		@LuaMethod(name = "getTableResult", global = true)
		public static void getTableResult(String string, int int1) {
			GameClient.instance.getTableResult(string, int1);
		}

		@LuaMethod(name = "getWorldSoundManager", global = true)
		public static WorldSoundManager getWorldSoundManager() {
			return WorldSoundManager.instance;
		}

		@LuaMethod(name = "AddWorldSound", global = true)
		public static void AddWorldSound(IsoPlayer player, int int1, int int2) {
			WorldSoundManager.instance.addSound((Object)null, (int)player.getX(), (int)player.getY(), (int)player.getZ(), int1, int2, false);
		}

		@LuaMethod(name = "AddNoiseToken", global = true)
		public static void AddNoiseToken(IsoGridSquare square, int int1) {
		}

		@LuaMethod(name = "pauseSoundAndMusic", global = true)
		public static void pauseSoundAndMusic() {
			DebugLog.log("EXITDEBUG: pauseSoundAndMusic 1");
			SoundManager.instance.pauseSoundAndMusic();
			DebugLog.log("EXITDEBUG: pauseSoundAndMusic 2");
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
				String string2 = ZomboidFileSystem.instance.getCacheDir();
				string = string2 + File.separator + "Server" + File.separator + string;
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
				String string = ZomboidFileSystem.instance.getCacheDir();
				bufferedReader = new BufferedReader(new FileReader(new File(string + File.separator + "latestSave.ini")));
			} catch (FileNotFoundException fileNotFoundException) {
				return kahluaTable;
			}

			try {
				String string2 = null;
				for (int int1 = 1; (string2 = bufferedReader.readLine()) != null; ++int1) {
					kahluaTable.rawset(int1, string2);
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

		@LuaMethod(name = "sendVisual", global = true)
		public static void sendVisual(IsoPlayer player) {
			if (GameClient.bClient) {
				GameClient.instance.sendVisual(player);
			}
		}

		@LuaMethod(name = "sendClothing", global = true)
		public static void sendClothing(IsoPlayer player) {
			if (GameClient.bClient) {
				GameClient.instance.sendClothing(player, "", (InventoryItem)null);
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
			return GameServer.bServer && GameServer.bSoftReset;
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
			return PlayerType.toString(GameClient.connection.accessLevel);
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
			File file = ZomboidFileSystem.instance.getMediaFile("maps");
			String[] stringArray = file.list();
			if (stringArray == null) {
				return kahluaTable;
			} else {
				int int1 = 1;
				String string;
				for (int int2 = 0; int2 < stringArray.length; ++int2) {
					string = stringArray[int2];
					if (!string.equals("challengemaps")) {
						kahluaTable.rawset(int1, string);
						++int1;
					}
				}

				Iterator iterator = ZomboidFileSystem.instance.getModIDs().iterator();
				while (iterator.hasNext()) {
					string = (String)iterator.next();
					ChooseGameInfo.Mod mod = null;
					try {
						mod = ChooseGameInfo.getAvailableModDetails(string);
					} catch (Exception exception) {
					}

					if (mod != null) {
						file = new File(mod.getDir() + "/media/maps/");
						if (file.exists()) {
							stringArray = file.list();
							if (stringArray != null) {
								for (int int3 = 0; int3 < stringArray.length; ++int3) {
									String string2 = stringArray[int3];
									ChooseGameInfo.Map map = ChooseGameInfo.getMapDetails(string2);
									if (map.getLotDirectories() != null && !map.getLotDirectories().isEmpty() && !string2.equals("challengemaps")) {
										kahluaTable.rawset(int1, string2);
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
			String string2 = ZomboidFileSystem.instance.getSaveDir();
			File file = new File(string2 + File.separator + string);
			String[] stringArray = file.list();
			if (stringArray != null) {
				for (int int1 = 0; int1 < stringArray.length; ++int1) {
					File file2 = new File(ZomboidFileSystem.instance.getSaveDir() + File.separator + string + File.separator + stringArray[int1]);
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
				String string = LuaManager.getLuaCacheDir();
				File file = new File(string + File.separator + getServerListFile());
				if (!file.exists()) {
					file.createNewFile();
				}

				bufferedReader = new BufferedReader(new FileReader(file));
				String string2 = null;
				Server server = null;
				while ((string2 = bufferedReader.readLine()) != null) {
					if (string2.startsWith("name=")) {
						server = new Server();
						arrayList.add(server);
						server.setName(string2.replaceFirst("name=", ""));
					} else if (string2.startsWith("ip=")) {
						server.setIp(string2.replaceFirst("ip=", ""));
					} else if (string2.startsWith("localip=")) {
						server.setLocalIP(string2.replaceFirst("localip=", ""));
					} else if (string2.startsWith("description=")) {
						server.setDescription(string2.replaceFirst("description=", ""));
					} else if (string2.startsWith("port=")) {
						server.setPort(string2.replaceFirst("port=", ""));
					} else if (string2.startsWith("user=")) {
						server.setUserName(string2.replaceFirst("user=", ""));
					} else if (string2.startsWith("password=")) {
						server.setPwd(string2.replaceFirst("password=", ""));
					} else if (string2.startsWith("serverpassword=")) {
						server.setServerPassword(string2.replaceFirst("serverpassword=", ""));
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
			String string = ZomboidFileSystem.instance.getSaveDir();
			File file = new File(string + File.separator);
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
					string = ZomboidFileSystem.instance.getSaveDir();
					File file2 = new File(string + File.separator + stringArray[int1]);
					if (file2.isDirectory() && !"Multiplayer".equals(stringArray[int1])) {
						String string2 = ZomboidFileSystem.instance.getSaveDir();
						ArrayList arrayList2 = getSaveDirectory(string2 + File.separator + stringArray[int1]);
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
					String string3 = getSaveName(file3);
					Double Double1 = (double)int1;
					kahluaTable.rawset(Double1, string3);
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

		public static List getMods() {
			ArrayList arrayList = new ArrayList();
			ZomboidFileSystem.instance.getAllModFolders(arrayList);
			return arrayList;
		}

		@LuaMethod(name = "doChallenge", global = true)
		public static void doChallenge(KahluaTable kahluaTable) {
			Core.GameMode = kahluaTable.rawget("gameMode").toString();
			Core.ChallengeID = kahluaTable.rawget("id").toString();
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
			Core.ChallengeID = null;
			Core.getInstance().setChallenge(false);
			Core.bTutorial = true;
			getWorld().setMap(kahluaTable.getString("world"));
			getWorld().bDoChunkMapUpdate = false;
		}

		@LuaMethod(name = "deleteAllGameModeSaves", global = true)
		public static void deleteAllGameModeSaves(String string) {
			String string2 = Core.GameMode;
			Core.GameMode = string;
			Path path = Paths.get(ZomboidFileSystem.instance.getGameModeCacheDir());
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

		@LuaMethod(name = "sendSafehouseInvite", global = true)
		public static void sendSafehouseInvite(SafeHouse safeHouse, IsoPlayer player, String string) {
			if (GameClient.bClient) {
				GameClient.sendSafehouseInvite(safeHouse, player, string);
			}
		}

		@LuaMethod(name = "acceptSafehouseInvite", global = true)
		public static void acceptSafehouseInvite(SafeHouse safeHouse, String string) {
			if (GameClient.bClient) {
				GameClient.acceptSafehouseInvite(safeHouse, string);
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
					IsoZombie zombie = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.fromIndex(Rand.Next(IsoDirections.Max.index())).index(), false);
					zombie.dressInRandomOutfit();
					ZombieSpawnRecorder.instance.record(zombie, "LuaManager.spawnHorde");
				}
			}
		}

		@LuaMethod(name = "createZombie", global = true)
		public static IsoZombie createZombie(float float1, float float2, float float3, SurvivorDesc survivorDesc, int int1, IsoDirections directions) {
			VirtualZombieManager.instance.choices.clear();
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, (double)float3);
			VirtualZombieManager.instance.choices.add(square);
			IsoZombie zombie = VirtualZombieManager.instance.createRealZombieAlways(directions.index(), false);
			ZombieSpawnRecorder.instance.record(zombie, "LuaManager.createZombie");
			return zombie;
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
								DebugLog.Lua.debugln(string + object2 + " : " + object3.toString());
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

		@LuaMethod(name = "getModInfoByID", global = true)
		public static ChooseGameInfo.Mod getModInfoByID(String string) {
			try {
				return ChooseGameInfo.getModDetails(string);
			} catch (Exception exception) {
				exception.printStackTrace();
				return null;
			}
		}

		@LuaMethod(name = "getModInfo", global = true)
		public static ChooseGameInfo.Mod getModInfo(String string) {
			try {
				return ChooseGameInfo.readModInfo(string);
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				return null;
			}
		}

		@LuaMethod(name = "getMapFoldersForMod", global = true)
		public static ArrayList getMapFoldersForMod(String string) {
			try {
				ChooseGameInfo.Mod mod = ChooseGameInfo.getModDetails(string);
				if (mod == null) {
					return null;
				} else {
					String string2 = mod.getDir();
					String string3 = string2 + File.separator + "media" + File.separator + "maps";
					File file = new File(string3);
					if (file.exists() && file.isDirectory()) {
						ArrayList arrayList = null;
						DirectoryStream directoryStream = Files.newDirectoryStream(file.toPath());
						try {
							Iterator iterator = directoryStream.iterator();
							while (iterator.hasNext()) {
								Path path = (Path)iterator.next();
								if (Files.isDirectory(path, new LinkOption[0])) {
									file = new File(string3 + File.separator + path.getFileName().toString() + File.separator + "map.info");
									if (file.exists()) {
										if (arrayList == null) {
											arrayList = new ArrayList();
										}

										arrayList.add(path.getFileName().toString());
									}
								}
							}
						} catch (Throwable throwable) {
							if (directoryStream != null) {
								try {
									directoryStream.close();
								} catch (Throwable throwable2) {
									throwable.addSuppressed(throwable2);
								}
							}

							throw throwable;
						}

						if (directoryStream != null) {
							directoryStream.close();
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
				ChooseGameInfo.Mod mod = ChooseGameInfo.getModDetails(string);
				if (mod == null) {
					return false;
				} else {
					String string3 = mod.getDir();
					String string4 = string3 + File.separator + "media" + File.separator + "maps" + File.separator + string2 + File.separator + "spawnpoints.lua";
					return (new File(string4)).exists();
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

		@LuaMethod(name = "getScriptManager", global = true)
		public static ScriptManager getScriptManager() {
			return ScriptManager.instance;
		}

		@LuaMethod(name = "checkSaveFolderExists", global = true)
		public static boolean checkSaveFolderExists(String string) {
			String string2 = ZomboidFileSystem.instance.getSaveDir();
			File file = new File(string2 + File.separator + string);
			return file.exists();
		}

		@LuaMethod(name = "getAbsoluteSaveFolderName", global = true)
		public static String getAbsoluteSaveFolderName(String string) {
			String string2 = ZomboidFileSystem.instance.getSaveDir();
			File file = new File(string2 + File.separator + string);
			return file.getAbsolutePath();
		}

		@LuaMethod(name = "checkSaveFileExists", global = true)
		public static boolean checkSaveFileExists(String string) {
			File file = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave(string));
			return file.exists();
		}

		@LuaMethod(name = "checkSavePlayerExists", global = true)
		public static boolean checkSavePlayerExists() {
			if (!GameClient.bClient) {
				return PlayerDBHelper.isPlayerAlive(ZomboidFileSystem.instance.getCurrentSaveDir(), 1);
			} else if (ClientPlayerDB.getInstance() == null) {
				return false;
			} else {
				return ClientPlayerDB.getInstance().clientLoadNetworkPlayer() && ClientPlayerDB.getInstance().isAliveMainNetworkPlayer();
			}
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
			String string3 = ZomboidFileSystem.instance.getCacheDir();
			File file = new File(string3 + File.separator + "Server" + File.separator + string2);
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

		@LuaMethod(name = "checkStringPattern", global = true)
		public static boolean checkStringPattern(String string) {
			return !string.contains("[");
		}

		@LuaMethod(name = "instanceItem", global = true)
		public static InventoryItem instanceItem(Item item) {
			return InventoryItemFactory.CreateItem(item.moduleDotType);
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
			item.moduleDotType = item.module.name + "." + string2;
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

		@LuaMethod(name = "moduleDotType", global = true)
		public static String moduleDotType(String string, String string2) {
			return StringUtils.moduleDotType(string, string2);
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

			DebugLog.Lua.warn("require(\"" + string + "\") failed");
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

		@LuaMethod(name = "getMPStatistics", global = true)
		public static KahluaTable getStatistics() {
			return MPStatistics.getLuaStatistics();
		}

		@LuaMethod(name = "getMPStatus", global = true)
		public static KahluaTable getTime() {
			return MPStatistics.getLuaStatus();
		}

		@LuaMethod(name = "getMaxPlayers", global = true)
		public static Double getMaxPlayers() {
			return (double)GameClient.connection.maxPlayers;
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
				DebugLog.Lua.warn("relative paths not allowed");
				return null;
			} else {
				String string2 = LuaManager.getLuaCacheDir();
				String string3 = string2 + File.separator + string;
				string3 = string3.replace("/", File.separator);
				string3 = string3.replace("\\", File.separator);
				String string4 = string3.substring(0, string3.lastIndexOf(File.separator));
				string4 = string4.replace("\\", "/");
				File file = new File(string4);
				if (!file.exists()) {
					file.mkdirs();
				}

				File file2 = new File(string3);
				try {
					outStream = new FileOutputStream(file2);
				} catch (FileNotFoundException fileNotFoundException) {
					Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, fileNotFoundException);
				}

				DataOutputStream dataOutputStream = new DataOutputStream(outStream);
				return dataOutputStream;
			}
		}

		@LuaMethod(name = "getLastStandPlayersDirectory", global = true)
		public static String getLastStandPlayersDirectory() {
			return "LastStand";
		}

		@LuaMethod(name = "getLastStandPlayerFileNames", global = true)
		public static List getLastStandPlayerFileNames() throws IOException {
			ArrayList arrayList = new ArrayList();
			String string = LuaManager.getLuaCacheDir();
			String string2 = string + File.separator + getLastStandPlayersDirectory();
			string2 = string2.replace("/", File.separator);
			string2 = string2.replace("\\", File.separator);
			File file = new File(string2);
			if (!file.exists()) {
				file.mkdir();
			}

			File[] fileArray = file.listFiles();
			int int1 = fileArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				File file2 = fileArray[int2];
				if (!file2.isDirectory() && file2.getName().endsWith(".txt")) {
					String string3 = getLastStandPlayersDirectory();
					arrayList.add(string3 + File.separator + file2.getName());
				}
			}

			return arrayList;
		}

		@Deprecated
		@LuaMethod(name = "getAllSavedPlayers", global = true)
		public static List getAllSavedPlayers() throws IOException {
			ArrayList arrayList = new ArrayList();
			String string = LuaManager.getLuaCacheDir();
			String string2 = string + File.separator + getLastStandPlayersDirectory();
			string2 = string2.replace("/", File.separator);
			string2 = string2.replace("\\", File.separator);
			File file = new File(string2);
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
				DebugLog.Lua.warn("relative paths not allowed");
			} else {
				String string2 = LuaManager.getSandboxCacheDir();
				String string3 = string2 + File.separator + string + ".cfg";
				File file = new File(string3);
				if (file.exists()) {
					file.delete();
				}
			}
		}

		@LuaMethod(name = "getFileReader", global = true)
		public static BufferedReader getFileReader(String string, boolean boolean1) throws IOException {
			if (string.contains("..")) {
				DebugLog.Lua.warn("relative paths not allowed");
				return null;
			} else {
				String string2 = LuaManager.getLuaCacheDir();
				String string3 = string2 + File.separator + string;
				string3 = string3.replace("/", File.separator);
				string3 = string3.replace("\\", File.separator);
				File file = new File(string3);
				if (!file.exists() && boolean1) {
					file.createNewFile();
				}

				if (file.exists()) {
					BufferedReader bufferedReader = null;
					try {
						FileInputStream fileInputStream = new FileInputStream(file);
						InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
						bufferedReader = new BufferedReader(inputStreamReader);
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
				String string3 = ZomboidFileSystem.instance.getCacheDir();
				String string4 = string3 + File.separator + "mods" + File.separator + string2;
				if (string != null) {
					ChooseGameInfo.Mod mod = ChooseGameInfo.getModDetails(string);
					if (mod == null) {
						return null;
					}

					string3 = mod.getDir();
					string4 = string3 + File.separator + string2;
				}

				string4 = string4.replace("/", File.separator);
				string4 = string4.replace("\\", File.separator);
				File file = new File(string4);
				if (!file.exists() && boolean1) {
					String string5 = string4.substring(0, string4.lastIndexOf(File.separator));
					File file2 = new File(string5);
					if (!file2.exists()) {
						file2.mkdirs();
					}

					file.createNewFile();
				}

				if (file.exists()) {
					BufferedReader bufferedReader = null;
					try {
						FileInputStream fileInputStream = new FileInputStream(file);
						InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
						bufferedReader = new BufferedReader(inputStreamReader);
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

		public static void refreshAnimSets(boolean boolean1) {
			try {
				Iterator iterator;
				if (boolean1) {
					AnimationSet.Reset();
					iterator = AnimNodeAssetManager.instance.getAssetTable().values().iterator();
					while (iterator.hasNext()) {
						Asset asset = (Asset)iterator.next();
						AnimNodeAssetManager.instance.reload(asset);
					}
				}

				AnimationSet.GetAnimationSet("player", true);
				AnimationSet.GetAnimationSet("player-vehicle", true);
				AnimationSet.GetAnimationSet("zombie", true);
				AnimationSet.GetAnimationSet("zombie-crawler", true);
				for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					IsoPlayer player = IsoPlayer.players[int1];
					if (player != null) {
						player.advancedAnimator.OnAnimDataChanged(boolean1);
					}
				}

				iterator = IsoWorld.instance.CurrentCell.getZombieList().iterator();
				while (iterator.hasNext()) {
					IsoZombie zombie = (IsoZombie)iterator.next();
					zombie.advancedAnimator.OnAnimDataChanged(boolean1);
				}
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		}

		public static void reloadActionGroups() {
			try {
				ActionGroup.reloadAll();
			} catch (Exception exception) {
			}
		}

		@LuaMethod(name = "getModFileWriter", global = true)
		public static LuaManager.GlobalObject.LuaFileWriter getModFileWriter(String string, String string2, boolean boolean1, boolean boolean2) {
			if (!string2.isEmpty() && !string2.contains("..") && !(new File(string2)).isAbsolute()) {
				ChooseGameInfo.Mod mod = ChooseGameInfo.getModDetails(string);
				if (mod == null) {
					return null;
				} else {
					String string3 = mod.getDir();
					String string4 = string3 + File.separator + string2;
					string4 = string4.replace("/", File.separator);
					string4 = string4.replace("\\", File.separator);
					String string5 = string4.substring(0, string4.lastIndexOf(File.separator));
					File file = new File(string5);
					if (!file.exists()) {
						file.mkdirs();
					}

					File file2 = new File(string4);
					if (!file2.exists() && boolean1) {
						try {
							file2.createNewFile();
						} catch (IOException ioException) {
							Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, ioException);
						}
					}

					PrintWriter printWriter = null;
					try {
						FileOutputStream fileOutputStream = new FileOutputStream(file2, boolean2);
						OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
						printWriter = new PrintWriter(outputStreamWriter);
					} catch (IOException ioException2) {
						Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, ioException2);
					}

					return new LuaManager.GlobalObject.LuaFileWriter(printWriter);
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
			String string2 = LuaManager.getLuaCacheDir();
			String string3 = string2 + File.separator + "Players" + File.separator + "player" + string + ".txt";
			string3 = string3.replace("/", File.separator);
			string3 = string3.replace("\\", File.separator);
			File file = new File(string3);
			file.delete();
		}

		@LuaMethod(name = "getControllerCount", global = true)
		public static int getControllerCount() {
			return GameWindow.GameInput.getControllerCount();
		}

		@LuaMethod(name = "isControllerConnected", global = true)
		public static boolean isControllerConnected(int int1) {
			if (int1 >= 0 && int1 <= GameWindow.GameInput.getControllerCount()) {
				return GameWindow.GameInput.getController(int1) != null;
			} else {
				return false;
			}
		}

		@LuaMethod(name = "getControllerGUID", global = true)
		public static String getControllerGUID(int int1) {
			if (int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount()) {
				Controller controller = GameWindow.GameInput.getController(int1);
				return controller != null ? controller.getGUID() : "???";
			} else {
				return "???";
			}
		}

		@LuaMethod(name = "getControllerName", global = true)
		public static String getControllerName(int int1) {
			if (int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount()) {
				Controller controller = GameWindow.GameInput.getController(int1);
				return controller != null ? controller.getGamepadName() : "???";
			} else {
				return "???";
			}
		}

		@LuaMethod(name = "getControllerAxisCount", global = true)
		public static int getControllerAxisCount(int int1) {
			if (int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount()) {
				Controller controller = GameWindow.GameInput.getController(int1);
				return controller == null ? 0 : controller.getAxisCount();
			} else {
				return 0;
			}
		}

		@LuaMethod(name = "getControllerAxisValue", global = true)
		public static float getControllerAxisValue(int int1, int int2) {
			if (int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount()) {
				Controller controller = GameWindow.GameInput.getController(int1);
				if (controller == null) {
					return 0.0F;
				} else {
					return int2 >= 0 && int2 < controller.getAxisCount() ? controller.getAxisValue(int2) : 0.0F;
				}
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
			if (int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount()) {
				Controller controller = GameWindow.GameInput.getController(int1);
				return controller == null ? 0 : controller.getButtonCount();
			} else {
				return 0;
			}
		}

		@LuaMethod(name = "getControllerPovX", global = true)
		public static float getControllerPovX(int int1) {
			if (int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount()) {
				Controller controller = GameWindow.GameInput.getController(int1);
				return controller == null ? 0.0F : controller.getPovX();
			} else {
				return 0.0F;
			}
		}

		@LuaMethod(name = "getControllerPovY", global = true)
		public static float getControllerPovY(int int1) {
			if (int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount()) {
				Controller controller = GameWindow.GameInput.getController(int1);
				return controller == null ? 0.0F : controller.getPovY();
			} else {
				return 0.0F;
			}
		}

		@LuaMethod(name = "reloadControllerConfigFiles", global = true)
		public static void reloadControllerConfigFiles() {
			JoypadManager.instance.reloadControllerFiles();
		}

		@LuaMethod(name = "isJoypadPressed", global = true)
		public static boolean isJoypadPressed(int int1, int int2) {
			return GameWindow.GameInput.isButtonPressedD(int2, int1);
		}

		@LuaMethod(name = "isJoypadDown", global = true)
		public static boolean isJoypadDown(int int1) {
			return JoypadManager.instance.isDownPressed(int1);
		}

		@LuaMethod(name = "isJoypadLTPressed", global = true)
		public static boolean isJoypadLTPressed(int int1) {
			return JoypadManager.instance.isLTPressed(int1);
		}

		@LuaMethod(name = "isJoypadRTPressed", global = true)
		public static boolean isJoypadRTPressed(int int1) {
			return JoypadManager.instance.isRTPressed(int1);
		}

		@LuaMethod(name = "isJoypadLeftStickButtonPressed", global = true)
		public static boolean isJoypadLeftStickButtonPressed(int int1) {
			return JoypadManager.instance.isL3Pressed(int1);
		}

		@LuaMethod(name = "isJoypadRightStickButtonPressed", global = true)
		public static boolean isJoypadRightStickButtonPressed(int int1) {
			return JoypadManager.instance.isR3Pressed(int1);
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

		@LuaMethod(name = "getJoypadLeftStickButton", global = true)
		public static int getJoypadLeftStickButton(int int1) {
			JoypadManager.Joypad joypad = JoypadManager.instance.getFromControllerID(int1);
			return joypad != null ? joypad.getL3() : -1;
		}

		@LuaMethod(name = "getJoypadRightStickButton", global = true)
		public static int getJoypadRightStickButton(int int1) {
			JoypadManager.Joypad joypad = JoypadManager.instance.getFromControllerID(int1);
			return joypad != null ? joypad.getR3() : -1;
		}

		@LuaMethod(name = "wasMouseActiveMoreRecentlyThanJoypad", global = true)
		public static boolean wasMouseActiveMoreRecentlyThanJoypad() {
			if (IsoPlayer.players[0] == null) {
				JoypadManager.Joypad joypad = GameWindow.ActivatedJoyPad;
				if (joypad != null && !joypad.isDisabled()) {
					return JoypadManager.instance.getLastActivity(joypad.getID()) < Mouse.lastActivity;
				} else {
					return true;
				}
			} else {
				int int1 = IsoPlayer.players[0].getJoypadBind();
				if (int1 == -1) {
					return true;
				} else {
					return JoypadManager.instance.getLastActivity(int1) < Mouse.lastActivity;
				}
			}
		}

		@LuaMethod(name = "activateJoypadOnSteamDeck", global = true)
		public static void activateJoypadOnSteamDeck() {
			if (GameWindow.ActivatedJoyPad == null) {
				JoypadManager.instance.isAPressed(0);
				if (JoypadManager.instance.JoypadList.isEmpty()) {
					return;
				}

				JoypadManager.Joypad joypad = (JoypadManager.Joypad)JoypadManager.instance.JoypadList.get(0);
				GameWindow.ActivatedJoyPad = joypad;
			}

			if (IsoPlayer.getInstance() != null) {
				LuaEventManager.triggerEvent("OnJoypadActivate", GameWindow.ActivatedJoyPad.getID());
			} else {
				LuaEventManager.triggerEvent("OnJoypadActivateUI", GameWindow.ActivatedJoyPad.getID());
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

		@LuaMethod(name = "isJoypadConnected", global = true)
		public static boolean isJoypadConnected(int int1) {
			return JoypadManager.instance.isJoypadConnected(int1);
		}

		private static void addPlayerToWorld(int int1, IsoPlayer player, boolean boolean1) {
			if (IsoPlayer.players[int1] != null) {
				IsoPlayer.players[int1].getEmitter().stopAll();
				IsoPlayer.players[int1].getEmitter().unregister();
				IsoPlayer.players[int1].updateUsername();
				IsoPlayer.players[int1].setSceneCulled(true);
				IsoPlayer.players[int1] = null;
			}

			player.PlayerIndex = int1;
			if (GameClient.bClient && int1 != 0 && player.serverPlayerIndex != 1) {
				ClientPlayerDB.getInstance().forgetPlayer(player.serverPlayerIndex);
			}

			if (GameClient.bClient && int1 != 0 && player.serverPlayerIndex == 1) {
				player.serverPlayerIndex = ClientPlayerDB.getInstance().getNextServerPlayerIndex();
			}

			if (int1 == 0) {
				player.sqlID = 1;
			}

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
				label58: while (true) {
					TraitFactory.Trait trait;
					do {
						do {
							if (!iterator.hasNext()) {
								player.setDir(IsoDirections.SE);
								LuaEventManager.triggerEvent("OnNewGame", player, player.getCurrentSquare());
								break label58;
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
					IsoPlayer player2 = IsoPlayer.getInstance();
					IsoWorld world = IsoWorld.instance;
					int int3 = world.getLuaPosX() + 300 * world.getLuaSpawnCellX();
					int int4 = world.getLuaPosY() + 300 * world.getLuaSpawnCellY();
					int int5 = world.getLuaPosZ();
					DebugLog.Lua.debugln("coop player spawning at " + int3 + "," + int4 + "," + int5);
					player = new IsoPlayer(world.CurrentCell, world.getLuaPlayerDesc(), int3, int4, int5);
					IsoPlayer.setInstance(player2);
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
				IsoPlayer player2 = IsoPlayer.getInstance();
				IsoWorld world = IsoWorld.instance;
				int int1 = world.getLuaPosX() + 300 * world.getLuaSpawnCellX();
				int int2 = world.getLuaPosY() + 300 * world.getLuaSpawnCellY();
				int int3 = world.getLuaPosZ();
				DebugLog.Lua.debugln("coop player spawning at " + int1 + "," + int2 + "," + int3);
				player = new IsoPlayer(world.CurrentCell, world.getLuaPlayerDesc(), int1, int2, int3);
				IsoPlayer.setInstance(player2);
				world.CurrentCell.getAddList().remove(player);
				world.CurrentCell.getObjectList().remove(player);
				player.SaveFileName = null;
			}

			if (GameClient.bClient) {
				player.username = GameClient.username;
			}

			addPlayerToWorld(byte1, player, boolean1);
		}

		@LuaMethod(name = "revertToKeyboardAndMouse", global = true)
		public static void revertToKeyboardAndMouse() {
			JoypadManager.instance.revertToKeyboardAndMouse();
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

		@LuaMethod(name = "isJoypadLBPressed", global = true)
		public static boolean isJoypadLBPressed(int int1) {
			return JoypadManager.instance.isLBPressed(int1);
		}

		@LuaMethod(name = "isJoypadRBPressed", global = true)
		public static boolean isJoypadRBPressed(int int1) {
			return JoypadManager.instance.isRBPressed(int1);
		}

		@LuaMethod(name = "getButtonCount", global = true)
		public static int getButtonCount(int int1) {
			if (int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount()) {
				Controller controller = GameWindow.GameInput.getController(int1);
				return controller == null ? 0 : controller.getButtonCount();
			} else {
				return 0;
			}
		}

		@LuaMethod(name = "setDebugToggleControllerPluggedIn", global = true)
		public static void setDebugToggleControllerPluggedIn(int int1) {
			Controllers.setDebugToggleControllerPluggedIn(int1);
		}

		@LuaMethod(name = "getFileWriter", global = true)
		public static LuaManager.GlobalObject.LuaFileWriter getFileWriter(String string, boolean boolean1, boolean boolean2) {
			if (string.contains("..")) {
				DebugLog.Lua.warn("relative paths not allowed");
				return null;
			} else {
				String string2 = LuaManager.getLuaCacheDir();
				String string3 = string2 + File.separator + string;
				string3 = string3.replace("/", File.separator);
				string3 = string3.replace("\\", File.separator);
				String string4 = string3.substring(0, string3.lastIndexOf(File.separator));
				string4 = string4.replace("\\", "/");
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

				PrintWriter printWriter = null;
				try {
					FileOutputStream fileOutputStream = new FileOutputStream(file2, boolean2);
					OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
					printWriter = new PrintWriter(outputStreamWriter);
				} catch (IOException ioException2) {
					Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, ioException2);
				}

				return new LuaManager.GlobalObject.LuaFileWriter(printWriter);
			}
		}

		@LuaMethod(name = "getSandboxFileWriter", global = true)
		public static LuaManager.GlobalObject.LuaFileWriter getSandboxFileWriter(String string, boolean boolean1, boolean boolean2) {
			String string2 = LuaManager.getSandboxCacheDir();
			String string3 = string2 + File.separator + string;
			string3 = string3.replace("/", File.separator);
			string3 = string3.replace("\\", File.separator);
			String string4 = string3.substring(0, string3.lastIndexOf(File.separator));
			string4 = string4.replace("\\", "/");
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

			PrintWriter printWriter = null;
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(file2, boolean2);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
				printWriter = new PrintWriter(outputStreamWriter);
			} catch (IOException ioException2) {
				Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, (String)null, ioException2);
			}

			return new LuaManager.GlobalObject.LuaFileWriter(printWriter);
		}

		@LuaMethod(name = "createStory", global = true)
		public static void createStory(String string) {
			Core.GameMode = string;
			String string2 = ZomboidFileSystem.instance.getGameModeCacheDir();
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
		}

		@LuaMethod(name = "createWorld", global = true)
		public static void createWorld(String string) {
			if (string == null || string.isEmpty()) {
				string = "blah";
			}

			string = sanitizeWorldName(string);
			String string2 = ZomboidFileSystem.instance.getGameModeCacheDir();
			String string3 = string2 + File.separator + string + File.separator;
			string3 = string3.replace("/", File.separator);
			string3 = string3.replace("\\", File.separator);
			String string4 = string3.substring(0, string3.lastIndexOf(File.separator));
			string4 = string4.replace("\\", "/");
			File file = new File(string4);
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
			String string2 = LuaManager.getLuaCacheDir();
			String string3 = string2 + File.separator + string;
			string3 = string3.replace("/", File.separator);
			string3 = string3.replace("\\", File.separator);
			File file = new File(string3);
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

		@LuaMethod(name = "wasKeyDown", global = true)
		public static boolean wasKeyDown(int int1) {
			return GameKeyboard.wasKeyDown(int1);
		}

		@LuaMethod(name = "isKeyPressed", global = true)
		public static boolean isKeyPressed(int int1) {
			return GameKeyboard.isKeyPressed(int1);
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

		@LuaMethod(name = "isAltKeyDown", global = true)
		public static boolean isAltKeyDown() {
			return GameKeyboard.isKeyDown(56) || GameKeyboard.isKeyDown(184);
		}

		@LuaMethod(name = "getCore", global = true)
		public static Core getCore() {
			return Core.getInstance();
		}

		@LuaMethod(name = "getSquare", global = true)
		public static IsoGridSquare getSquare(double double1, double double2, double double3) {
			return IsoCell.getInstance().getGridSquare(double1, double2, double3);
		}

		@LuaMethod(name = "getDebugOptions", global = true)
		public static DebugOptions getDebugOptions() {
			return DebugOptions.instance;
		}

		@LuaMethod(name = "setShowPausedMessage", global = true)
		public static void setShowPausedMessage(boolean boolean1) {
			DebugLog.log("EXITDEBUG: setShowPausedMessage 1");
			UIManager.setShowPausedMessage(boolean1);
			DebugLog.log("EXITDEBUG: setShowPausedMessage 2");
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

		@LuaMethod(name = "isSystemLinux", global = true)
		public static boolean isSystemLinux() {
			return !isSystemMacOS() && !isSystemWindows();
		}

		@LuaMethod(name = "isSystemMacOS", global = true)
		public static boolean isSystemMacOS() {
			return System.getProperty("os.name").contains("OS X");
		}

		@LuaMethod(name = "isSystemWindows", global = true)
		public static boolean isSystemWindows() {
			return System.getProperty("os.name").startsWith("Win");
		}

		@LuaMethod(name = "isModActive", global = true)
		public static boolean isModActive(ChooseGameInfo.Mod mod) {
			String string = mod.getDir();
			if (!StringUtils.isNullOrWhitespace(mod.getId())) {
				string = mod.getId();
			}

			return ZomboidFileSystem.instance.getModIDs().contains(string);
		}

		@LuaMethod(name = "openUrl", global = true)
		public static void openURl(String string) {
			Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			if (desktop != null && desktop.isSupported(Action.BROWSE)) {
				try {
					URI uRI = new URI(string);
					desktop.browse(uRI);
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
				}
			}
		}

		@LuaMethod(name = "isDesktopOpenSupported", global = true)
		public static boolean isDesktopOpenSupported() {
			return !Desktop.isDesktopSupported() ? false : Desktop.getDesktop().isSupported(Action.OPEN);
		}

		@LuaMethod(name = "showFolderInDesktop", global = true)
		public static void showFolderInDesktop(String string) {
			File file = new File(string);
			if (file.exists() && file.isDirectory()) {
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
				if (desktop != null && desktop.isSupported(Action.OPEN)) {
					try {
						desktop.open(file);
					} catch (Exception exception) {
						ExceptionLogger.logException(exception);
					}
				}
			}
		}

		@LuaMethod(name = "getActivatedMods", global = true)
		public static ArrayList getActivatedMods() {
			return ZomboidFileSystem.instance.getModIDs();
		}

		@LuaMethod(name = "toggleModActive", global = true)
		public static void toggleModActive(ChooseGameInfo.Mod mod, boolean boolean1) {
			String string = mod.getDir();
			if (!StringUtils.isNullOrWhitespace(mod.getId())) {
				string = mod.getId();
			}

			ActiveMods.getById("default").setModActive(string, boolean1);
		}

		@LuaMethod(name = "saveModsFile", global = true)
		public static void saveModsFile() {
			ZomboidFileSystem.instance.saveModsFile();
		}

		private static void deleteSavefileFilesMatching(File file, String string) {
			Filter filter = (stringx)->{
    return stringx.getFileName().toString().matches(string);
};
			try {
				DirectoryStream directoryStream = Files.newDirectoryStream(file.toPath(), filter);
				try {
					Iterator iterator = directoryStream.iterator();
					while (iterator.hasNext()) {
						Path path = (Path)iterator.next();
						System.out.println("DELETE " + path);
						Files.deleteIfExists(path);
					}
				} catch (Throwable throwable) {
					if (directoryStream != null) {
						try {
							directoryStream.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}
					}

					throw throwable;
				}

				if (directoryStream != null) {
					directoryStream.close();
				}
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		}

		@LuaMethod(name = "manipulateSavefile", global = true)
		public static void manipulateSavefile(String string, String string2) {
			if (!StringUtils.isNullOrWhitespace(string)) {
				if (!string.contains("..")) {
					String string3 = ZomboidFileSystem.instance.getSaveDir();
					String string4 = string3 + File.separator + string;
					File file = new File(string4);
					if (file.exists() && file.isDirectory()) {
						byte byte1 = -1;
						switch (string2.hashCode()) {
						case -2086819803: 
							if (string2.equals("DeletePlayersDB")) {
								byte1 = 5;
							}

							break;
						
						case -1291996175: 
							if (string2.equals("DeleteMapMetaBin")) {
								byte1 = 2;
							}

							break;
						
						case -1139427258: 
							if (string2.equals("DeleteReanimatedBin")) {
								byte1 = 6;
							}

							break;
						
						case -1035136361: 
							if (string2.equals("WriteModsDotTxt")) {
								byte1 = 11;
							}

							break;
						
						case -970886236: 
							if (string2.equals("DeleteZPopXYBin")) {
								byte1 = 10;
							}

							break;
						
						case -351123098: 
							if (string2.equals("DeleteZOutfitsBin")) {
								byte1 = 8;
							}

							break;
						
						case 305594266: 
							if (string2.equals("DeleteChunkDataXYBin")) {
								byte1 = 0;
							}

							break;
						
						case 552703408: 
							if (string2.equals("DeleteVehiclesDB")) {
								byte1 = 7;
							}

							break;
						
						case 1411759445: 
							if (string2.equals("DeleteMapXYBin")) {
								byte1 = 1;
							}

							break;
						
						case 1430873892: 
							if (string2.equals("DeleteMapTBin")) {
								byte1 = 3;
							}

							break;
						
						case 1760896894: 
							if (string2.equals("DeleteZPopVirtualBin")) {
								byte1 = 9;
							}

							break;
						
						case 1936486634: 
							if (string2.equals("DeleteMapZoneBin")) {
								byte1 = 4;
							}

						
						}

						switch (byte1) {
						case 0: 
							deleteSavefileFilesMatching(file, "chunkdata_[0-9]+_[0-9]+\\.bin");
							break;
						
						case 1: 
							deleteSavefileFilesMatching(file, "map_[0-9]+_[0-9]+\\.bin");
							break;
						
						case 2: 
							deleteSavefileFilesMatching(file, "map_meta\\.bin");
							break;
						
						case 3: 
							deleteSavefileFilesMatching(file, "map_t\\.bin");
							break;
						
						case 4: 
							deleteSavefileFilesMatching(file, "map_zone\\.bin");
							break;
						
						case 5: 
							deleteSavefileFilesMatching(file, "players\\.db");
							break;
						
						case 6: 
							deleteSavefileFilesMatching(file, "reanimated\\.bin");
							break;
						
						case 7: 
							deleteSavefileFilesMatching(file, "vehicles\\.db");
							break;
						
						case 8: 
							deleteSavefileFilesMatching(file, "z_outfits\\.bin");
							break;
						
						case 9: 
							deleteSavefileFilesMatching(file, "zpop_virtual\\.bin");
							break;
						
						case 10: 
							deleteSavefileFilesMatching(file, "zpop_[0-9]+_[0-9]+\\.bin");
							break;
						
						case 11: 
							ActiveMods activeMods = ActiveMods.getById("currentGame");
							ActiveModsFile activeModsFile = new ActiveModsFile();
							activeModsFile.write(string4 + File.separator + "mods.txt", activeMods);
							break;
						
						default: 
							throw new IllegalArgumentException("unknown action \"" + string2 + "\"");
						
						}
					}
				}
			}
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
			synchronized (IsoSpriteManager.instance.NamedMap) {
				IsoSprite sprite = (IsoSprite)IsoSpriteManager.instance.NamedMap.get(string);
				if (sprite != null) {
					int int1 = 0;
					int int2 = 0;
					int int3 = 0;
					if (square != null) {
						int1 = square.getX();
						int2 = square.getY();
						int3 = square.getZ();
					}

					CellLoader.DoTileObjectCreation(sprite, sprite.getType(), square, IsoWorld.instance.CurrentCell, int1, int2, int3, string);
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

		@LuaMethod(name = "isoRegionsRenderer", global = true)
		public static IsoRegionsRenderer isoRegionsRenderer() {
			return new IsoRegionsRenderer();
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

		@LuaMethod(name = "luaDebug", global = true)
		public static void luaDebug() {
			try {
				throw new Exception("LuaDebug");
			} catch (Exception exception) {
				exception.printStackTrace();
			}
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

		@LuaMethod(name = "getLuaDebuggerErrorCount", global = true)
		public static int getLuaDebuggerErrorCount() {
			KahluaThread kahluaThread = LuaManager.thread;
			return KahluaThread.m_error_count;
		}

		@LuaMethod(name = "getLuaDebuggerErrors", global = true)
		public static ArrayList getLuaDebuggerErrors() {
			KahluaThread kahluaThread = LuaManager.thread;
			ArrayList arrayList = new ArrayList(KahluaThread.m_errors_list);
			return arrayList;
		}

		@LuaMethod(name = "doLuaDebuggerAction", global = true)
		public static void doLuaDebuggerAction(String string) {
			UIManager.luaDebuggerAction = string;
		}

		@LuaMethod(name = "getGameSpeed", global = true)
		public static int getGameSpeed() {
			return UIManager.getSpeedControls() != null ? UIManager.getSpeedControls().getCurrentGameSpeed() : 0;
		}

		@LuaMethod(name = "setGameSpeed", global = true)
		public static void setGameSpeed(int int1) {
			DebugLog.log("EXITDEBUG: setGameSpeed 1");
			if (UIManager.getSpeedControls() == null) {
				DebugLog.log("EXITDEBUG: setGameSpeed 2");
			} else {
				UIManager.getSpeedControls().SetCurrentGameSpeed(int1);
				DebugLog.log("EXITDEBUG: setGameSpeed 3");
			}
		}

		@LuaMethod(name = "isGamePaused", global = true)
		public static boolean isGamePaused() {
			return GameTime.isGamePaused();
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
			String string2 = ZomboidFileSystem.instance.getSaveDir();
			File file = new File(string2 + File.separator + string);
			if (!file.exists()) {
				return Translator.getText("UI_LastPlayed") + "???";
			} else {
				Date date = new Date(file.lastModified());
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				String string3 = simpleDateFormat.format(date);
				String string4 = Translator.getText("UI_LastPlayed");
				return string4 + string3;
			}
		}

		@LuaMethod(name = "getTextureFromSaveDir", global = true)
		public static Texture getTextureFromSaveDir(String string, String string2) {
			TextureID.UseFiltering = true;
			String string3 = ZomboidFileSystem.instance.getSaveDir() + File.separator + string2 + File.separator + string;
			Texture texture = Texture.getSharedTexture(string3);
			TextureID.UseFiltering = false;
			return texture;
		}

		@LuaMethod(name = "getSaveInfo", global = true)
		public static KahluaTable getSaveInfo(String string) {
			String string2;
			if (!string.contains(File.separator)) {
				string2 = IsoWorld.instance.getGameMode();
				string = string2 + File.separator + string;
			}

			KahluaTable kahluaTable = LuaManager.platform.newTable();
			String string3 = ZomboidFileSystem.instance.getSaveDir();
			File file = new File(string3 + File.separator + string);
			if (file.exists()) {
				Date date = new Date(file.lastModified());
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				String string4 = simpleDateFormat.format(date);
				kahluaTable.rawset("lastPlayed", string4);
				String[] stringArray = string.split("\\" + File.separator);
				kahluaTable.rawset("saveName", file.getName());
				kahluaTable.rawset("gameMode", stringArray[stringArray.length - 2]);
			}

			string3 = ZomboidFileSystem.instance.getSaveDir();
			file = new File(string3 + File.separator + string + File.separator + "map_ver.bin");
			String string5;
			if (file.exists()) {
				try {
					FileInputStream fileInputStream = new FileInputStream(file);
					try {
						DataInputStream dataInputStream = new DataInputStream(fileInputStream);
						try {
							int int1 = dataInputStream.readInt();
							kahluaTable.rawset("worldVersion", (double)int1);
							if (int1 >= 18) {
								try {
									string5 = GameWindow.ReadString(dataInputStream);
									if (string5.equals("DEFAULT")) {
										string5 = "Muldraugh, KY";
									}

									kahluaTable.rawset("mapName", string5);
								} catch (Exception exception) {
								}
							}

							if (int1 >= 74) {
								try {
									string5 = GameWindow.ReadString(dataInputStream);
									kahluaTable.rawset("difficulty", string5);
								} catch (Exception exception2) {
								}
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
				} catch (Exception exception3) {
					ExceptionLogger.logException(exception3);
				}
			}

			string2 = ZomboidFileSystem.instance.getSaveDir();
			String string6 = string2 + File.separator + string + File.separator + "mods.txt";
			ActiveMods activeMods = new ActiveMods(string);
			ActiveModsFile activeModsFile = new ActiveModsFile();
			if (activeModsFile.read(string6, activeMods)) {
				kahluaTable.rawset("activeMods", activeMods);
			}

			string2 = ZomboidFileSystem.instance.getSaveDir();
			string5 = string2 + File.separator + string;
			kahluaTable.rawset("playerAlive", PlayerDBHelper.isPlayerAlive(string5, 1));
			KahluaTable kahluaTable2 = LuaManager.platform.newTable();
			try {
				ArrayList arrayList = PlayerDBHelper.getPlayers(string5);
				for (int int2 = 0; int2 < arrayList.size(); int2 += 3) {
					Double Double1 = (Double)arrayList.get(int2);
					String string7 = (String)arrayList.get(int2 + 1);
					Boolean Boolean1 = (Boolean)arrayList.get(int2 + 2);
					KahluaTable kahluaTable3 = LuaManager.platform.newTable();
					kahluaTable3.rawset("sqlID", Double1);
					kahluaTable3.rawset("name", string7);
					kahluaTable3.rawset("isDead", Boolean1);
					kahluaTable2.rawset(int2 / 3 + 1, kahluaTable3);
				}
			} catch (Exception exception4) {
				ExceptionLogger.logException(exception4);
			}

			kahluaTable.rawset("players", kahluaTable2);
			return kahluaTable;
		}

		@LuaMethod(name = "renameSavefile", global = true)
		public static boolean renameSaveFile(String string, String string2, String string3) {
			if (string != null && !string.contains("/") && !string.contains("\\") && !string.contains(File.separator) && !string.contains("..")) {
				if (string2 != null && !string2.contains("/") && !string2.contains("\\") && !string2.contains(File.separator) && !string2.contains("..")) {
					if (string3 != null && !string3.contains("/") && !string3.contains("\\") && !string3.contains(File.separator) && !string3.contains("..")) {
						String string4 = sanitizeWorldName(string3);
						if (string4.equals(string3) && !string4.startsWith(".") && !string4.endsWith(".")) {
							if (!(new File(ZomboidFileSystem.instance.getSaveDirSub(string))).exists()) {
								return false;
							} else {
								Path path = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getSaveDirSub(string + File.separator + string2));
								Path path2 = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getSaveDirSub(string + File.separator + string4));
								try {
									Files.move(path, path2);
									return true;
								} catch (IOException ioException) {
									return false;
								}
							}
						} else {
							return false;
						}
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		@LuaMethod(name = "setSavefilePlayer1", global = true)
		public static void setSavefilePlayer1(String string, String string2, int int1) {
			String string3 = ZomboidFileSystem.instance.getSaveDirSub(string + File.separator + string2);
			try {
				PlayerDBHelper.setPlayer1(string3, int1);
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		}

		@LuaMethod(name = "getServerSavedWorldVersion", global = true)
		public static int getServerSavedWorldVersion(String string) {
			String string2 = ZomboidFileSystem.instance.getSaveDir();
			File file = new File(string2 + File.separator + string + File.separator + "map_t.bin");
			if (file.exists()) {
				try {
					FileInputStream fileInputStream = new FileInputStream(file);
					int int1;
					label64: {
						byte byte1;
						try {
							DataInputStream dataInputStream = new DataInputStream(fileInputStream);
							label60: {
								try {
									byte byte2 = dataInputStream.readByte();
									byte byte3 = dataInputStream.readByte();
									byte byte4 = dataInputStream.readByte();
									byte byte5 = dataInputStream.readByte();
									if (byte2 != 71 || byte3 != 77 || byte4 != 84 || byte5 != 77) {
										byte1 = 1;
										break label60;
									}

									int1 = dataInputStream.readInt();
								} catch (Throwable throwable) {
									try {
										dataInputStream.close();
									} catch (Throwable throwable2) {
										throwable.addSuppressed(throwable2);
									}

									throw throwable;
								}

								dataInputStream.close();
								break label64;
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
						return byte1;
					}

					fileInputStream.close();
					return int1;
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}

			return 0;
		}

		@LuaMethod(name = "getZombieInfo", global = true)
		public static KahluaTable getZombieInfo(IsoZombie zombie) {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			if (zombie == null) {
				return kahluaTable;
			} else {
				kahluaTable.rawset("OnlineID", zombie.OnlineID);
				kahluaTable.rawset("RealX", zombie.realx);
				kahluaTable.rawset("RealY", zombie.realy);
				kahluaTable.rawset("X", zombie.x);
				kahluaTable.rawset("Y", zombie.y);
				kahluaTable.rawset("TargetX", zombie.networkAI.targetX);
				kahluaTable.rawset("TargetY", zombie.networkAI.targetY);
				kahluaTable.rawset("PathLength", zombie.getPathFindBehavior2().getPathLength());
				kahluaTable.rawset("TargetLength", Math.sqrt((double)((zombie.x - zombie.getPathFindBehavior2().getTargetX()) * (zombie.x - zombie.getPathFindBehavior2().getTargetX()) + (zombie.y - zombie.getPathFindBehavior2().getTargetY()) * (zombie.y - zombie.getPathFindBehavior2().getTargetY()))));
				kahluaTable.rawset("clientActionState", zombie.getActionStateName());
				kahluaTable.rawset("clientAnimationState", zombie.getAnimationStateName());
				kahluaTable.rawset("finderProgress", zombie.getFinder().progress.name());
				kahluaTable.rawset("usePathFind", Boolean.toString(zombie.networkAI.usePathFind));
				kahluaTable.rawset("owner", zombie.authOwner.username);
				zombie.networkAI.DebugInterfaceActive = true;
				return kahluaTable;
			}
		}

		@LuaMethod(name = "getPlayerInfo", global = true)
		public static KahluaTable getPlayerInfo(IsoPlayer player) {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			if (player == null) {
				return kahluaTable;
			} else {
				long long1 = GameTime.getServerTime() / 1000000L;
				kahluaTable.rawset("OnlineID", player.OnlineID);
				kahluaTable.rawset("RealX", player.realx);
				kahluaTable.rawset("RealY", player.realy);
				kahluaTable.rawset("X", player.x);
				kahluaTable.rawset("Y", player.y);
				kahluaTable.rawset("TargetX", player.networkAI.targetX);
				kahluaTable.rawset("TargetY", player.networkAI.targetY);
				kahluaTable.rawset("TargetT", player.networkAI.targetZ);
				kahluaTable.rawset("ServerT", long1);
				kahluaTable.rawset("PathLength", player.getPathFindBehavior2().getPathLength());
				kahluaTable.rawset("TargetLength", Math.sqrt((double)((player.x - player.getPathFindBehavior2().getTargetX()) * (player.x - player.getPathFindBehavior2().getTargetX()) + (player.y - player.getPathFindBehavior2().getTargetY()) * (player.y - player.getPathFindBehavior2().getTargetY()))));
				kahluaTable.rawset("clientActionState", player.getActionStateName());
				kahluaTable.rawset("clientAnimationState", player.getAnimationStateName());
				kahluaTable.rawset("finderProgress", player.getFinder().progress.name());
				kahluaTable.rawset("usePathFind", Boolean.toString(player.networkAI.usePathFind));
				return kahluaTable;
			}
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

		@LuaMethod(name = "getRadioText", global = true)
		public static String getRadioText(String string) {
			return Translator.getRadioText(string);
		}

		@LuaMethod(name = "getTextMediaEN", global = true)
		public static String getTextMediaEN(String string) {
			return Translator.getTextMediaEN(string);
		}

		@LuaMethod(name = "getItemNameFromFullType", global = true)
		public static String getItemNameFromFullType(String string) {
			return Translator.getItemNameFromFullType(string);
		}

		@LuaMethod(name = "getRecipeDisplayName", global = true)
		public static String getRecipeDisplayName(String string) {
			return Translator.getRecipeName(string);
		}

		@LuaMethod(name = "getMyDocumentFolder", global = true)
		public static String getMyDocumentFolder() {
			return Core.getMyDocumentFolder();
		}

		@LuaMethod(name = "getSpriteManager", global = true)
		public static IsoSpriteManager getSpriteManager(String string) {
			return IsoSpriteManager.instance;
		}

		@LuaMethod(name = "getSprite", global = true)
		public static IsoSprite getSprite(String string) {
			return IsoSpriteManager.instance.getSprite(string);
		}

		@LuaMethod(name = "getServerModData", global = true)
		public static void getServerModData() {
			GameClient.getCustomModData();
		}

		@LuaMethod(name = "isXBOXController", global = true)
		public static boolean isXBOXController() {
			for (int int1 = 0; int1 < GameWindow.GameInput.getControllerCount(); ++int1) {
				Controller controller = GameWindow.GameInput.getController(int1);
				if (controller != null && controller.getGamepadName().contains("XBOX 360")) {
					return true;
				}
			}

			return false;
		}

		@LuaMethod(name = "sendClientCommand", global = true)
		public static void sendClientCommand(String string, String string2, KahluaTable kahluaTable) {
			if (GameClient.bClient && GameClient.bIngame) {
				GameClient.instance.sendClientCommand((IsoPlayer)null, string, string2, kahluaTable);
			} else {
				if (GameServer.bServer) {
					throw new IllegalStateException("can\'t call this function on the server");
				}

				SinglePlayerClient.sendClientCommand((IsoPlayer)null, string, string2, kahluaTable);
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
			return IsoPlayer.getInstance().getDisplayName();
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
			return GameClient.bClient && GameClient.connection.accessLevel == 32;
		}

		@LuaMethod(name = "canModifyPlayerScoreboard", global = true)
		public static boolean canModifyPlayerScoreboard() {
			return GameClient.bClient && GameClient.connection.accessLevel != 1;
		}

		@LuaMethod(name = "isAccessLevel", global = true)
		public static boolean isAccessLevel(String string) {
			if (GameClient.bClient) {
				if (GameClient.connection.accessLevel == 1) {
					return false;
				} else {
					return GameClient.connection.accessLevel == PlayerType.fromString(string);
				}
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
		public static void sendStitch(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, BodyPart bodyPart, InventoryItem inventoryItem, boolean boolean1) {
			GameClient.instance.sendStitch(gameCharacter, gameCharacter2, bodyPart, inventoryItem, boolean1);
		}

		@LuaMethod(name = "sendDisinfect", global = true)
		public static void sendDisinfect(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, BodyPart bodyPart, InventoryItem inventoryItem) {
			GameClient.instance.sendDisinfect(gameCharacter, gameCharacter2, bodyPart, inventoryItem);
		}

		@LuaMethod(name = "sendSplint", global = true)
		public static void sendSplint(int int1, int int2, boolean boolean1, float float1, String string) {
			GameClient.instance.sendSplint(int1, int2, boolean1, float1, string);
		}

		@LuaMethod(name = "sendRemoveGlass", global = true)
		public static void sendRemoveGlass(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, BodyPart bodyPart, boolean boolean1) {
			GameClient.instance.sendRemoveGlass(gameCharacter, gameCharacter2, bodyPart, boolean1);
		}

		@LuaMethod(name = "sendRemoveBullet", global = true)
		public static void sendRemoveBullet(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, BodyPart bodyPart) {
			GameClient.instance.sendRemoveBullet(gameCharacter, gameCharacter2, bodyPart);
		}

		@LuaMethod(name = "sendCleanBurn", global = true)
		public static void sendCleanBurn(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, BodyPart bodyPart, InventoryItem inventoryItem) {
			GameClient.instance.sendCleanBurn(gameCharacter, gameCharacter2, bodyPart, inventoryItem);
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
			GameClient.invMngRequestItem((int)long1, string, player);
		}

		@LuaMethod(name = "InvMngRemoveItem", global = true)
		public static void InvMngRemoveItem(long long1, IsoPlayer player) {
			GameClient.invMngRequestRemoveItem((int)long1, player);
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
				if (GameClient.connection.accessLevel == 32) {
					string = "";
				}

				GameClient.connection.accessLevel = PlayerType.fromString(string);
				IsoPlayer.getInstance().accessLevel = string;
				GameClient.SendCommandToServer("/setaccesslevel \"" + IsoPlayer.getInstance().username + "\" \"" + (string.equals("") ? "none" : string) + "\"");
				if (string.equals("") && IsoPlayer.getInstance().isInvisible() || string.equals("admin") && !IsoPlayer.getInstance().isInvisible()) {
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
		}

		@LuaMethod(name = "disconnect", global = true)
		public static void disconnect() {
			GameClient.connection.forceDisconnect("lua-disconnect");
		}

		@LuaMethod(name = "writeLog", global = true)
		public static void writeLog(String string, String string2) {
			LoggerManager.getLogger(string).write(string2);
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

		@LuaMethod(name = "getVehicleZoneAt", global = true)
		public static IsoMetaGrid.VehicleZone getVehicleZoneAt(int int1, int int2, int int3) {
			return IsoWorld.instance.MetaGrid.getVehicleZoneAt(int1, int2, int3);
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
				return "true".equals(string) || GameClient.connection.accessLevel == 32 && "admin".equals(string);
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

		@LuaMethod(name = "isSteamRunningOnSteamDeck", global = true)
		public static boolean isSteamRunningOnSteamDeck() {
			return SteamUtils.isSteamModeEnabled() ? SteamUtils.isRunningOnSteamDeck() : false;
		}

		@LuaMethod(name = "showSteamGamepadTextInput", global = true)
		public static boolean showSteamGamepadTextInput(boolean boolean1, boolean boolean2, String string, int int1, String string2) {
			return SteamUtils.isSteamModeEnabled() ? SteamUtils.showGamepadTextInput(boolean1, boolean2, string, int1, string2) : false;
		}

		@LuaMethod(name = "showSteamFloatingGamepadTextInput", global = true)
		public static boolean showSteamFloatingGamepadTextInput(boolean boolean1, int int1, int int2, int int3, int int4) {
			return SteamUtils.isSteamModeEnabled() ? SteamUtils.showFloatingGamepadTextInput(boolean1, int1, int2, int3, int4) : false;
		}

		@LuaMethod(name = "isFloatingGamepadTextInputVisible", global = true)
		public static boolean isFloatingGamepadTextInputVisible() {
			return SteamUtils.isSteamModeEnabled() ? SteamUtils.isFloatingGamepadTextInputVisible() : false;
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
				} else if (!gameServerDetails.tags.contains("hidden") && !gameServerDetails.tags.contains("hosted")) {
					if (!gameServerDetails.tags.contains("hidden") && !gameServerDetails.tags.contains("hosted")) {
						Server server = new Server();
						server.setName(gameServerDetails.name);
						server.setDescription("");
						server.setSteamId(Long.toString(gameServerDetails.steamId));
						server.setPing(Integer.toString(gameServerDetails.ping));
						server.setPlayers(Integer.toString(gameServerDetails.numPlayers));
						server.setMaxPlayers(Integer.toString(gameServerDetails.maxPlayers));
						server.setOpen(true);
						server.setPublic(true);
						if (gameServerDetails.tags.contains("hidden")) {
							server.setOpen(false);
							server.setPublic(false);
						}

						server.setIp(gameServerDetails.address);
						server.setPort(Integer.toString(gameServerDetails.port));
						server.setMods("");
						if (!gameServerDetails.tags.replace("hidden", "").replace("hosted", "").replace(";", "").isEmpty()) {
							server.setMods(gameServerDetails.tags.replace(";hosted", "").replace("hidden", ""));
						}

						server.setHosted(gameServerDetails.tags.contains("hosted"));
						server.setVersion("");
						server.setLastUpdate(1);
						server.setPasswordProtected(gameServerDetails.passwordProtected);
						return server;
					} else {
						return null;
					}
				} else {
					return null;
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
			float float1 = (float)Mouse.getX();
			float float2 = (float)Mouse.getY();
			int int1 = (int)IsoPlayer.getInstance().getZ();
			int int2 = (int)IsoUtils.XToIso(float1, float2, (float)int1);
			int int3 = (int)IsoUtils.YToIso(float1, float2, (float)int1);
			float float3 = 50.0F;
			float float4 = 1.0F;
			AmbientStreamManager.Ambient ambient = new AmbientStreamManager.Ambient("Meta/House Alarm", (float)int2, (float)int3, float3, float4);
			ambient.trackMouse = true;
			((AmbientStreamManager)AmbientStreamManager.instance).ambient.add(ambient);
		}

		@LuaMethod(name = "debugSetRoomType", global = true)
		public static void debugSetRoomType(Double Double1) {
			ParameterRoomType.setRoomType(Double1.intValue());
		}

		@LuaMethod(name = "copyTable", global = true)
		public static KahluaTable copyTable(KahluaTable kahluaTable) {
			return LuaManager.copyTable(kahluaTable);
		}

		@LuaMethod(name = "copyTable", global = true)
		public static KahluaTable copyTable(KahluaTable kahluaTable, KahluaTable kahluaTable2) {
			return LuaManager.copyTable(kahluaTable, kahluaTable2);
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
			if (GameClient.bClient) {
				GameClient.SendCommandToServer("/chopper start");
			} else {
				IsoWorld.instance.helicopter.pickRandomTarget();
			}
		}

		@LuaMethod(name = "endHelicopter", global = true)
		public static void endHelicopter() {
			if (GameClient.bClient) {
				GameClient.SendCommandToServer("/chopper stop");
			} else {
				IsoWorld.instance.helicopter.deactivate();
			}
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

		@LuaMethod(name = "sendSwitchSeat", global = true)
		public static void sendSwitchSeat(BaseVehicle baseVehicle, IsoGameCharacter gameCharacter, int int1, int int2) {
			if (GameClient.bClient) {
				VehicleManager.instance.sendSwitchSeat(GameClient.connection, baseVehicle, gameCharacter, int1, int2);
			}
		}

		@LuaMethod(name = "getVehicleById", global = true)
		public static BaseVehicle getVehicleById(int int1) {
			return VehicleManager.instance.getVehicleByID((short)int1);
		}

		@LuaMethod(name = "addBloodSplat", global = true)
		public void addBloodSplat(IsoGridSquare square, int int1) {
			for (int int2 = 0; int2 < int1; ++int2) {
				square.getChunk().addBloodSplat((float)square.x + Rand.Next(-0.5F, 0.5F), (float)square.y + Rand.Next(-0.5F, 0.5F), (float)square.z, Rand.Next(8));
			}
		}

		@LuaMethod(name = "addCarCrash", global = true)
		public static void addCarCrash() {
			IsoGridSquare square = IsoPlayer.getInstance().getCurrentSquare();
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

		@LuaMethod(name = "createRandomDeadBody", global = true)
		public static IsoDeadBody createRandomDeadBody(IsoGridSquare square, int int1) {
			if (square == null) {
				return null;
			} else {
				ItemPickerJava.ItemPickerRoom itemPickerRoom = (ItemPickerJava.ItemPickerRoom)ItemPickerJava.rooms.get("all");
				RandomizedBuildingBase.HumanCorpse humanCorpse = new RandomizedBuildingBase.HumanCorpse(IsoWorld.instance.getCell(), (float)square.x, (float)square.y, (float)square.z);
				humanCorpse.setDir(IsoDirections.getRandom());
				humanCorpse.setDescriptor(SurvivorFactory.CreateSurvivor());
				humanCorpse.setFemale(humanCorpse.getDescriptor().isFemale());
				humanCorpse.initWornItems("Human");
				humanCorpse.initAttachedItems("Human");
				Outfit outfit = humanCorpse.getRandomDefaultOutfit();
				humanCorpse.dressInNamedOutfit(outfit.m_Name);
				humanCorpse.initSpritePartsEmpty();
				humanCorpse.Dressup(humanCorpse.getDescriptor());
				for (int int2 = 0; int2 < int1; ++int2) {
					humanCorpse.addBlood((BloodBodyPartType)null, false, true, false);
				}

				IsoDeadBody deadBody = new IsoDeadBody(humanCorpse, true);
				ItemPickerJava.fillContainerType(itemPickerRoom, deadBody.getContainer(), humanCorpse.isFemale() ? "inventoryfemale" : "inventorymale", (IsoGameCharacter)null);
				return deadBody;
			}
		}

		@LuaMethod(name = "addZombieSitting", global = true)
		public void addZombieSitting(int int1, int int2, int int3) {
			IsoGridSquare square = IsoCell.getInstance().getGridSquare(int1, int2, int3);
			if (square != null) {
				VirtualZombieManager.instance.choices.clear();
				VirtualZombieManager.instance.choices.add(square);
				IsoZombie zombie = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.getRandom().index(), false);
				zombie.bDressInRandomOutfit = true;
				ZombiePopulationManager.instance.sitAgainstWall(zombie, square);
			}
		}

		@LuaMethod(name = "addZombiesEating", global = true)
		public void addZombiesEating(int int1, int int2, int int3, int int4, boolean boolean1) {
			IsoGridSquare square = IsoCell.getInstance().getGridSquare(int1, int2, int3);
			if (square != null) {
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
					for (int int5 = 0; int5 < 10; ++int5) {
						zombie.addHole((BloodBodyPartType)null);
						zombie.addBlood((BloodBodyPartType)null, false, true, false);
					}

					zombie.DoZombieInventory();
				}

				zombie.setSkeleton(boolean1);
				if (boolean1) {
					zombie.getHumanVisual().setSkinTextureIndex(2);
				}

				IsoDeadBody deadBody = new IsoDeadBody(zombie, true);
				VirtualZombieManager.instance.createEatingZombies(deadBody, int4);
			}
		}

		@LuaMethod(name = "addZombiesInOutfitArea", global = true)
		public ArrayList addZombiesInOutfitArea(int int1, int int2, int int3, int int4, int int5, int int6, String string, Integer integer) {
			ArrayList arrayList = new ArrayList();
			for (int int7 = 0; int7 < int6; ++int7) {
				arrayList.addAll(addZombiesInOutfit(Rand.Next(int1, int3), Rand.Next(int2, int4), int5, 1, string, integer));
			}

			return arrayList;
		}

		@LuaMethod(name = "addZombiesInOutfit", global = true)
		public static ArrayList addZombiesInOutfit(int int1, int int2, int int3, int int4, String string, Integer integer) {
			return addZombiesInOutfit(int1, int2, int3, int4, string, integer, false, false, false, false, 1.0F);
		}

		@LuaMethod(name = "addZombiesInOutfit", global = true)
		public static ArrayList addZombiesInOutfit(int int1, int int2, int int3, int int4, String string, Integer integer, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4, float float1) {
			ArrayList arrayList = new ArrayList();
			if (IsoWorld.getZombiesDisabled()) {
				return arrayList;
			} else {
				IsoGridSquare square = IsoCell.getInstance().getGridSquare(int1, int2, int3);
				if (square == null) {
					return arrayList;
				} else {
					for (int int5 = 0; int5 < int4; ++int5) {
						if (float1 <= 0.0F) {
							square.getChunk().AddCorpses(int1 / 10, int2 / 10);
						} else {
							VirtualZombieManager.instance.choices.clear();
							VirtualZombieManager.instance.choices.add(square);
							IsoZombie zombie = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.getRandom().index(), false);
							if (zombie != null) {
								if (integer != null) {
									zombie.setFemaleEtc(Rand.Next(100) < integer);
								}

								if (string != null) {
									zombie.dressInPersistentOutfit(string);
									zombie.bDressInRandomOutfit = false;
								} else {
									zombie.bDressInRandomOutfit = true;
								}

								zombie.bLunger = true;
								zombie.setKnockedDown(boolean4);
								if (boolean1) {
									zombie.setCrawler(true);
									zombie.setCanWalk(false);
									zombie.setOnFloor(true);
									zombie.setKnockedDown(true);
									zombie.setCrawlerType(1);
									zombie.DoZombieStats();
								}

								zombie.setFakeDead(boolean3);
								zombie.setFallOnFront(boolean2);
								zombie.setHealth(float1);
								arrayList.add(zombie);
							}
						}
					}

					ZombieSpawnRecorder.instance.record(arrayList, LuaManager.GlobalObject.class.getSimpleName());
					return arrayList;
				}
			}
		}

		@LuaMethod(name = "addZombiesInBuilding", global = true)
		public ArrayList addZombiesInBuilding(BuildingDef buildingDef, int int1, String string, RoomDef roomDef, Integer integer) {
			boolean boolean1 = roomDef == null;
			ArrayList arrayList = new ArrayList();
			if (IsoWorld.getZombiesDisabled()) {
				return arrayList;
			} else {
				if (roomDef == null) {
					roomDef = buildingDef.getRandomRoom(6);
				}

				int int2 = 2;
				int int3 = roomDef.area / 2;
				if (int1 == 0) {
					if (SandboxOptions.instance.Zombies.getValue() == 1) {
						int3 += 4;
					} else if (SandboxOptions.instance.Zombies.getValue() == 2) {
						int3 += 3;
					} else if (SandboxOptions.instance.Zombies.getValue() == 3) {
						int3 += 2;
					} else if (SandboxOptions.instance.Zombies.getValue() == 5) {
						int3 -= 4;
					}

					if (int3 > 8) {
						int3 = 8;
					}

					if (int3 < int2) {
						int3 = int2 + 1;
					}
				} else {
					int2 = int1;
					int3 = int1;
				}

				int int4 = Rand.Next(int2, int3);
				for (int int5 = 0; int5 < int4; ++int5) {
					IsoGridSquare square = RandomizedBuildingBase.getRandomSpawnSquare(roomDef);
					if (square == null) {
						break;
					}

					VirtualZombieManager.instance.choices.clear();
					VirtualZombieManager.instance.choices.add(square);
					IsoZombie zombie = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.getRandom().index(), false);
					if (zombie != null) {
						if (integer != null) {
							zombie.setFemaleEtc(Rand.Next(100) < integer);
						}

						if (string != null) {
							zombie.dressInPersistentOutfit(string);
							zombie.bDressInRandomOutfit = false;
						} else {
							zombie.bDressInRandomOutfit = true;
						}

						arrayList.add(zombie);
						if (boolean1) {
							roomDef = buildingDef.getRandomRoom(6);
						}
					}
				}

				ZombieSpawnRecorder.instance.record(arrayList, this.getClass().getSimpleName());
				return arrayList;
			}
		}

		@LuaMethod(name = "addVehicleDebug", global = true)
		public static BaseVehicle addVehicleDebug(String string, IsoDirections directions, Integer integer, IsoGridSquare square) {
			if (directions == null) {
				directions = IsoDirections.getRandom();
			}

			BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
			if (!StringUtils.isNullOrEmpty(string)) {
				baseVehicle.setScriptName(string);
				baseVehicle.setScript();
				if (integer != null) {
					baseVehicle.setSkinIndex(integer);
				}
			}

			baseVehicle.setDir(directions);
			float float1;
			for (float1 = directions.toAngle() + 3.1415927F + Rand.Next(-0.2F, 0.2F); (double)float1 > 6.283185307179586; float1 = (float)((double)float1 - 6.283185307179586)) {
			}

			baseVehicle.savedRot.setAngleAxis(float1, 0.0F, 1.0F, 0.0F);
			baseVehicle.jniTransform.setRotation(baseVehicle.savedRot);
			baseVehicle.setX((float)square.x);
			baseVehicle.setY((float)square.y);
			baseVehicle.setZ((float)square.z);
			if (IsoChunk.doSpawnedVehiclesInInvalidPosition(baseVehicle)) {
				baseVehicle.setSquare(square);
				square.chunk.vehicles.add(baseVehicle);
				baseVehicle.chunk = square.chunk;
				baseVehicle.addToWorld();
				VehiclesDB2.instance.addVehicle(baseVehicle);
			}

			baseVehicle.setGeneralPartCondition(1.3F, 10.0F);
			baseVehicle.rust = 0.0F;
			return baseVehicle;
		}

		@LuaMethod(name = "addVehicle", global = true)
		public static BaseVehicle addVehicle(String string) {
			if (!StringUtils.isNullOrWhitespace(string) && ScriptManager.instance.getVehicle(string) == null) {
				DebugLog.Lua.warn("No such vehicle script \"" + string + "\"");
				return null;
			} else {
				ArrayList arrayList = ScriptManager.instance.getAllVehicleScripts();
				if (arrayList.isEmpty()) {
					DebugLog.Lua.warn("No vehicle scripts defined");
					return null;
				} else {
					WorldSimulation.instance.create();
					BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
					if (StringUtils.isNullOrWhitespace(string)) {
						VehicleScript vehicleScript = (VehicleScript)PZArrayUtil.pickRandom((List)arrayList);
						string = vehicleScript.getFullName();
					}

					baseVehicle.setScriptName(string);
					baseVehicle.setX(IsoPlayer.getInstance().getX());
					baseVehicle.setY(IsoPlayer.getInstance().getY());
					baseVehicle.setZ(0.0F);
					if (IsoChunk.doSpawnedVehiclesInInvalidPosition(baseVehicle)) {
						baseVehicle.setSquare(IsoPlayer.getInstance().getSquare());
						baseVehicle.square.chunk.vehicles.add(baseVehicle);
						baseVehicle.chunk = baseVehicle.square.chunk;
						baseVehicle.addToWorld();
						VehiclesDB2.instance.addVehicle(baseVehicle);
					} else {
						DebugLog.Lua.error("ERROR: I can not spawn the vehicle. Invalid position. Try to change position.");
					}

					return null;
				}
			}
		}

		@LuaMethod(name = "attachTrailerToPlayerVehicle", global = true)
		public static void attachTrailerToPlayerVehicle(int int1) {
			IsoPlayer player = IsoPlayer.players[int1];
			IsoGridSquare square = player.getCurrentSquare();
			BaseVehicle baseVehicle = player.getVehicle();
			if (baseVehicle == null) {
				baseVehicle = addVehicleDebug("Base.OffRoad", IsoDirections.N, 0, square);
				baseVehicle.repair();
				player.getInventory().AddItem(baseVehicle.createVehicleKey());
			}

			square = IsoWorld.instance.CurrentCell.getGridSquare(square.x, square.y + 5, square.z);
			BaseVehicle baseVehicle2 = addVehicleDebug("Base.Trailer", IsoDirections.N, 0, square);
			baseVehicle2.repair();
			baseVehicle.addPointConstraint(player, baseVehicle2, "trailer", "trailer");
		}

		@LuaMethod(name = "getKeyName", global = true)
		public static String getKeyName(int int1) {
			return Input.getKeyName(int1);
		}

		@LuaMethod(name = "getKeyCode", global = true)
		public static int getKeyCode(String string) {
			return Input.getKeyCode(string);
		}

		@LuaMethod(name = "queueCharEvent", global = true)
		public static void queueCharEvent(String string) {
			RenderThread.queueInvokeOnRenderContext(()->{
				GameKeyboard.getEventQueuePolling().addCharEvent(string.charAt(0));
			});
		}

		@LuaMethod(name = "queueKeyEvent", global = true)
		public static void queueKeyEvent(int int1) {
			RenderThread.queueInvokeOnRenderContext(()->{
				int int2 = KeyCodes.toGlfwKey(int1);
				GameKeyboard.getEventQueuePolling().addKeyEvent(int2, 1);
				GameKeyboard.getEventQueuePolling().addKeyEvent(int2, 0);
			});
		}

		@LuaMethod(name = "addAllVehicles", global = true)
		public static void addAllVehicles() {
			addAllVehicles((var0)->{
				return !var0.getName().contains("Smashed") && !var0.getName().contains("Burnt");
			});
		}

		@LuaMethod(name = "addAllBurntVehicles", global = true)
		public static void addAllBurntVehicles() {
			addAllVehicles((var0)->{
				return var0.getName().contains("Burnt");
			});
		}

		@LuaMethod(name = "addAllSmashedVehicles", global = true)
		public static void addAllSmashedVehicles() {
			addAllVehicles((var0)->{
				return var0.getName().contains("Smashed");
			});
		}

		public static void addAllVehicles(Predicate predicate) {
			ArrayList arrayList = ScriptManager.instance.getAllVehicleScripts();
			Collections.sort(arrayList, Comparator.comparing(VehicleScript::getName));
			float float1 = (float)(IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMinTiles() + 5);
			float float2 = IsoPlayer.getInstance().getY();
			float float3 = 0.0F;
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				VehicleScript vehicleScript = (VehicleScript)arrayList.get(int1);
				if (vehicleScript.getModel() != null && predicate.test(vehicleScript) && IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, (double)float3) != null) {
					WorldSimulation.instance.create();
					BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
					baseVehicle.setScriptName(vehicleScript.getFullName());
					baseVehicle.setX(float1);
					baseVehicle.setY(float2);
					baseVehicle.setZ(float3);
					if (IsoChunk.doSpawnedVehiclesInInvalidPosition(baseVehicle)) {
						baseVehicle.setSquare(IsoPlayer.getInstance().getSquare());
						baseVehicle.square.chunk.vehicles.add(baseVehicle);
						baseVehicle.chunk = baseVehicle.square.chunk;
						baseVehicle.addToWorld();
						VehiclesDB2.instance.addVehicle(baseVehicle);
						IsoChunk.addFromCheckedVehicles(baseVehicle);
					} else {
						DebugLog.Lua.warn(vehicleScript.getName() + " not spawned, position invalid");
					}

					float1 += 4.0F;
					if (float1 > (float)(IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMaxTiles() - 5)) {
						float1 = (float)(IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMinTiles() + 5);
						float2 += 8.0F;
					}
				}
			}
		}

		@LuaMethod(name = "addPhysicsObject", global = true)
		public static BaseVehicle addPhysicsObject() {
			MPStatistic.getInstance().Bullet.Start();
			int int1 = Bullet.addPhysicsObject(getPlayer().getX(), getPlayer().getY());
			MPStatistic.getInstance().Bullet.End();
			IsoPushableObject pushableObject = new IsoPushableObject(IsoWorld.instance.getCell(), IsoPlayer.getInstance().getCurrentSquare(), IsoSpriteManager.instance.getSprite("trashcontainers_01_16"));
			WorldSimulation.instance.physicsObjectMap.put(int1, pushableObject);
			return null;
		}

		@LuaMethod(name = "toggleVehicleRenderToTexture", global = true)
		public static void toggleVehicleRenderToTexture() {
			BaseVehicle.RENDER_TO_TEXTURE = !BaseVehicle.RENDER_TO_TEXTURE;
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

		@LuaMethod(name = "getAnimationViewerState", global = true)
		public static AnimationViewerState getAnimationViewerState() {
			return AnimationViewerState.instance;
		}

		@LuaMethod(name = "getAttachmentEditorState", global = true)
		public static AttachmentEditorState getAttachmentEditorState() {
			return AttachmentEditorState.instance;
		}

		@LuaMethod(name = "getEditVehicleState", global = true)
		public static EditVehicleState getEditVehicleState() {
			return EditVehicleState.instance;
		}

		@LuaMethod(name = "showAnimationViewer", global = true)
		public static void showAnimationViewer() {
			IngameState.instance.showAnimationViewer = true;
		}

		@LuaMethod(name = "showAttachmentEditor", global = true)
		public static void showAttachmentEditor() {
			IngameState.instance.showAttachmentEditor = true;
		}

		@LuaMethod(name = "showChunkDebugger", global = true)
		public static void showChunkDebugger() {
			IngameState.instance.showChunkDebugger = true;
		}

		@LuaMethod(name = "showGlobalObjectDebugger", global = true)
		public static void showGlobalObjectDebugger() {
			IngameState.instance.showGlobalObjectDebugger = true;
		}

		@LuaMethod(name = "showVehicleEditor", global = true)
		public static void showVehicleEditor(String string) {
			IngameState.instance.showVehicleEditor = StringUtils.isNullOrWhitespace(string) ? "" : string;
		}

		@LuaMethod(name = "showWorldMapEditor", global = true)
		public static void showWorldMapEditor(String string) {
			IngameState.instance.showWorldMapEditor = StringUtils.isNullOrWhitespace(string) ? "" : string;
		}

		@LuaMethod(name = "reloadVehicles", global = true)
		public static void reloadVehicles() {
			try {
				Iterator iterator = ScriptManager.instance.scriptsWithVehicleTemplates.iterator();
				String string;
				while (iterator.hasNext()) {
					string = (String)iterator.next();
					ScriptManager.instance.LoadFile(string, true);
				}

				iterator = ScriptManager.instance.scriptsWithVehicles.iterator();
				while (iterator.hasNext()) {
					string = (String)iterator.next();
					ScriptManager.instance.LoadFile(string, true);
				}

				BaseVehicle.LoadAllVehicleTextures();
				iterator = IsoWorld.instance.CurrentCell.vehicles.iterator();
				while (iterator.hasNext()) {
					BaseVehicle baseVehicle = (BaseVehicle)iterator.next();
					baseVehicle.scriptReloaded();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		@LuaMethod(name = "reloadEngineRPM", global = true)
		public static void reloadEngineRPM() {
			try {
				ScriptManager.instance.LoadFile(ZomboidFileSystem.instance.getString("media/scripts/vehicles/engine_rpm.txt"), true);
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
				Thread thread = new Thread(ThreadGroups.Workers, ()->{
					ChatManager.getInstance().sendWhisperMessage(string4, string3);
				});

				thread.setUncaughtExceptionHandler(GameWindow::uncaughtException);
				thread.start();
				return string2;
			} else {
				ChatManager.getInstance().addMessage("Error", getText("IGUI_Commands_Whisper"));
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
				DebugLog.Lua.warn("Chat command not found");
			
			}
			return ChatManager.getInstance().isPlayerCanUseChat(chatType);
		}

		@LuaMethod(name = "reloadVehicleTextures", global = true)
		public static void reloadVehicleTextures(String string) {
			VehicleScript vehicleScript = ScriptManager.instance.getVehicle(string);
			if (vehicleScript == null) {
				DebugLog.Lua.warn("no such vehicle script");
			} else {
				for (int int1 = 0; int1 < vehicleScript.getSkinCount(); ++int1) {
					VehicleScript.Skin skin = vehicleScript.getSkin(int1);
					if (skin.texture != null) {
						Texture.reload("media/textures/" + skin.texture + ".png");
					}

					if (skin.textureRust != null) {
						Texture.reload("media/textures/" + skin.textureRust + ".png");
					}

					if (skin.textureMask != null) {
						Texture.reload("media/textures/" + skin.textureMask + ".png");
					}

					if (skin.textureLights != null) {
						Texture.reload("media/textures/" + skin.textureLights + ".png");
					}

					if (skin.textureDamage1Overlay != null) {
						Texture.reload("media/textures/" + skin.textureDamage1Overlay + ".png");
					}

					if (skin.textureDamage1Shell != null) {
						Texture.reload("media/textures/" + skin.textureDamage1Shell + ".png");
					}

					if (skin.textureDamage2Overlay != null) {
						Texture.reload("media/textures/" + skin.textureDamage2Overlay + ".png");
					}

					if (skin.textureDamage2Shell != null) {
						Texture.reload("media/textures/" + skin.textureDamage2Shell + ".png");
					}

					if (skin.textureShadow != null) {
						Texture.reload("media/textures/" + skin.textureShadow + ".png");
					}
				}
			}
		}

		@LuaMethod(name = "useStaticErosionRand", global = true)
		public static void useStaticErosionRand(boolean boolean1) {
			ErosionData.staticRand = boolean1;
		}

		@LuaMethod(name = "getClimateManager", global = true)
		public static ClimateManager getClimateManager() {
			return ClimateManager.getInstance();
		}

		@LuaMethod(name = "getClimateMoon", global = true)
		public static ClimateMoon getClimateMoon() {
			return ClimateMoon.getInstance();
		}

		@LuaMethod(name = "getWorldMarkers", global = true)
		public static WorldMarkers getWorldMarkers() {
			return WorldMarkers.instance;
		}

		@LuaMethod(name = "getIsoMarkers", global = true)
		public static IsoMarkers getIsoMarkers() {
			return IsoMarkers.instance;
		}

		@LuaMethod(name = "getErosion", global = true)
		public static ErosionMain getErosion() {
			return ErosionMain.getInstance();
		}

		@LuaMethod(name = "getAllOutfits", global = true)
		public static ArrayList getAllOutfits(boolean boolean1) {
			ArrayList arrayList = new ArrayList();
			ModelManager.instance.create();
			if (OutfitManager.instance == null) {
				return arrayList;
			} else {
				ArrayList arrayList2 = boolean1 ? OutfitManager.instance.m_FemaleOutfits : OutfitManager.instance.m_MaleOutfits;
				Iterator iterator = arrayList2.iterator();
				while (iterator.hasNext()) {
					Outfit outfit = (Outfit)iterator.next();
					arrayList.add(outfit.m_Name);
				}

				Collections.sort(arrayList);
				return arrayList;
			}
		}

		@LuaMethod(name = "getAllVehicles", global = true)
		public static ArrayList getAllVehicles() {
			return (ArrayList)ScriptManager.instance.getAllVehicleScripts().stream().map(VehicleScript::getFullName).sorted().collect(Collectors.toCollection(ArrayList::new));
		}

		@LuaMethod(name = "getAllHairStyles", global = true)
		public static ArrayList getAllHairStyles(boolean boolean1) {
			ArrayList arrayList = new ArrayList();
			if (HairStyles.instance == null) {
				return arrayList;
			} else {
				ArrayList arrayList2 = new ArrayList(boolean1 ? HairStyles.instance.m_FemaleStyles : HairStyles.instance.m_MaleStyles);
				arrayList2.sort((boolean1x,arrayListx)->{
					if (boolean1x.name.isEmpty()) {
						return -1;
					} else if (arrayListx.name.isEmpty()) {
						return 1;
					} else {
						String arrayList2 = getText("IGUI_Hair_" + boolean1x.name);
						String string = getText("IGUI_Hair_" + arrayListx.name);
						return arrayList2.compareTo(string);
					}
				});

				Iterator string = arrayList2.iterator();
				while (string.hasNext()) {
					HairStyle hairStyle = (HairStyle)string.next();
					arrayList.add(hairStyle.name);
				}

				return arrayList;
			}
		}

		@LuaMethod(name = "getHairStylesInstance", global = true)
		public static HairStyles getHairStylesInstance() {
			return HairStyles.instance;
		}

		@LuaMethod(name = "getBeardStylesInstance", global = true)
		public static BeardStyles getBeardStylesInstance() {
			return BeardStyles.instance;
		}

		@LuaMethod(name = "getAllBeardStyles", global = true)
		public static ArrayList getAllBeardStyles() {
			ArrayList arrayList = new ArrayList();
			if (BeardStyles.instance == null) {
				return arrayList;
			} else {
				ArrayList arrayList2 = new ArrayList(BeardStyles.instance.m_Styles);
				arrayList2.sort((arrayListx,arrayList2x)->{
					if (arrayListx.name.isEmpty()) {
						return -1;
					} else if (arrayList2x.name.isEmpty()) {
						return 1;
					} else {
						String string = getText("IGUI_Beard_" + arrayListx.name);
						String string2 = getText("IGUI_Beard_" + arrayList2x.name);
						return string.compareTo(string2);
					}
				});

				Iterator string = arrayList2.iterator();
				while (string.hasNext()) {
					BeardStyle string2 = (BeardStyle)string.next();
					arrayList.add(string2.name);
				}

				return arrayList;
			}
		}

		@LuaMethod(name = "getAllItemsForBodyLocation", global = true)
		public static KahluaTable getAllItemsForBodyLocation(String string) {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			if (StringUtils.isNullOrWhitespace(string)) {
				return kahluaTable;
			} else {
				int int1 = 1;
				ArrayList arrayList = ScriptManager.instance.getAllItems();
				Iterator iterator = arrayList.iterator();
				while (true) {
					Item item;
					do {
						do {
							if (!iterator.hasNext()) {
								return kahluaTable;
							}

							item = (Item)iterator.next();
						}			 while (StringUtils.isNullOrWhitespace(item.getClothingItem()));
					}		 while (!string.equals(item.getBodyLocation()) && !string.equals(item.CanBeEquipped));

					kahluaTable.rawset(int1++, item.getFullName());
				}
			}
		}

		@LuaMethod(name = "getAllDecalNamesForItem", global = true)
		public static ArrayList getAllDecalNamesForItem(InventoryItem inventoryItem) {
			ArrayList arrayList = new ArrayList();
			if (inventoryItem != null && ClothingDecals.instance != null) {
				ClothingItem clothingItem = inventoryItem.getClothingItem();
				if (clothingItem == null) {
					return arrayList;
				} else {
					String string = clothingItem.getDecalGroup();
					if (StringUtils.isNullOrWhitespace(string)) {
						return arrayList;
					} else {
						ClothingDecalGroup clothingDecalGroup = ClothingDecals.instance.FindGroup(string);
						if (clothingDecalGroup == null) {
							return arrayList;
						} else {
							clothingDecalGroup.getDecals(arrayList);
							return arrayList;
						}
					}
				}
			} else {
				return arrayList;
			}
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
		public void sendAddXp(IsoPlayer player, PerkFactory.Perk perk, int int1) {
			if (GameClient.bClient && player.isExistInTheWorld()) {
				GameClient.instance.sendAddXp(player, perk, int1);
			}
		}

		@LuaMethod(name = "SyncXp", global = true)
		public void SyncXp(IsoPlayer player) {
			if (GameClient.bClient) {
				GameClient.instance.sendSyncXp(player);
			}
		}

		@LuaMethod(name = "checkServerName", global = true)
		public String checkServerName(String string) {
			String string2 = ProfanityFilter.getInstance().validateString(string, true, true, true);
			return !StringUtils.isNullOrEmpty(string2) ? Translator.getText("UI_BadWordCheck", string2) : null;
		}

		@LuaMethod(name = "Render3DItem", global = true)
		public void Render3DItem(InventoryItem inventoryItem, IsoGridSquare square, float float1, float float2, float float3, float float4) {
			WorldItemModelDrawer.renderMain(inventoryItem, square, float1, float2, float3, 0.0F, float4);
		}

		@LuaMethod(name = "getContainerOverlays", global = true)
		public ContainerOverlays getContainerOverlays() {
			return ContainerOverlays.instance;
		}

		@LuaMethod(name = "getTileOverlays", global = true)
		public TileOverlays getTileOverlays() {
			return TileOverlays.instance;
		}

		@LuaMethod(name = "getAverageFPS", global = true)
		public Double getAverageFSP() {
			float float1 = GameWindow.averageFPS;
			if (!PerformanceSettings.isUncappedFPS()) {
				float1 = Math.min(float1, (float)PerformanceSettings.getLockFPS());
			}

			return BoxedStaticValues.toDouble(Math.floor((double)float1));
		}

		@LuaMethod(name = "createItemTransaction", global = true)
		public static void createItemTransaction(InventoryItem inventoryItem, ItemContainer itemContainer, ItemContainer itemContainer2) {
			if (GameClient.bClient && inventoryItem != null) {
				int int1 = (Integer)Optional.ofNullable(itemContainer).map(ItemContainer::getContainingItem).map(InventoryItem::getID).orElse(-1);
				int int2 = (Integer)Optional.ofNullable(itemContainer2).map(ItemContainer::getContainingItem).map(InventoryItem::getID).orElse(-1);
				ItemTransactionManager.createItemTransaction(inventoryItem.getID(), int1, int2);
			}
		}

		@LuaMethod(name = "removeItemTransaction", global = true)
		public static void removeItemTransaction(InventoryItem inventoryItem, ItemContainer itemContainer, ItemContainer itemContainer2) {
			if (GameClient.bClient && inventoryItem != null) {
				int int1 = (Integer)Optional.ofNullable(itemContainer).map(ItemContainer::getContainingItem).map(InventoryItem::getID).orElse(-1);
				int int2 = (Integer)Optional.ofNullable(itemContainer2).map(ItemContainer::getContainingItem).map(InventoryItem::getID).orElse(-1);
				ItemTransactionManager.removeItemTransaction(inventoryItem.getID(), int1, int2);
			}
		}

		@LuaMethod(name = "isItemTransactionConsistent", global = true)
		public static boolean isItemTransactionConsistent(InventoryItem inventoryItem, ItemContainer itemContainer, ItemContainer itemContainer2) {
			if (GameClient.bClient && inventoryItem != null) {
				int int1 = (Integer)Optional.ofNullable(itemContainer).map(ItemContainer::getContainingItem).map(InventoryItem::getID).orElse(-1);
				int int2 = (Integer)Optional.ofNullable(itemContainer2).map(ItemContainer::getContainingItem).map(InventoryItem::getID).orElse(-1);
				return ItemTransactionManager.isConsistent(inventoryItem.getID(), int1, int2);
			} else {
				return true;
			}
		}

		@LuaMethod(name = "getServerStatistic", global = true)
		public static KahluaTable getServerStatistic() {
			return MPStatistic.getInstance().getStatisticTableForLua();
		}

		@LuaMethod(name = "setServerStatisticEnable", global = true)
		public static void setServerStatisticEnable(boolean boolean1) {
			if (GameClient.bClient) {
				GameClient.setServerStatisticEnable(boolean1);
			}
		}

		@LuaMethod(name = "getServerStatisticEnable", global = true)
		public static boolean getServerStatisticEnable() {
			return GameClient.bClient ? GameClient.getServerStatisticEnable() : false;
		}

		@LuaMethod(name = "getSearchMode", global = true)
		public static SearchMode getSearchMode() {
			return SearchMode.getInstance();
		}

		@LuaMethod(name = "timSort", global = true)
		public static void timSort(KahluaTable kahluaTable, Object object) {
			KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)Type.tryCastTo(kahluaTable, KahluaTableImpl.class);
			if (kahluaTableImpl != null && kahluaTableImpl.len() >= 2 && object != null) {
				timSortComparator.comp = object;
				Object[] objectArray = kahluaTableImpl.delegate.values().toArray();
				Arrays.sort(objectArray, timSortComparator);
				for (int int1 = 0; int1 < objectArray.length; ++int1) {
					kahluaTableImpl.rawset(int1 + 1, objectArray[int1]);
					objectArray[int1] = null;
				}
			}
		}

		public static final class LuaFileWriter {
			private final PrintWriter writer;

			public LuaFileWriter(PrintWriter printWriter) {
				this.writer = printWriter;
			}

			public void write(String string) throws IOException {
				this.writer.write(string);
			}

			public void writeln(String string) throws IOException {
				this.writer.write(string);
				this.writer.write(System.lineSeparator());
			}

			public void close() throws IOException {
				this.writer.close();
			}
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

		private static final class TimSortComparator implements Comparator {
			Object comp;

			public int compare(Object object, Object object2) {
				if (Objects.equals(object, object2)) {
					return 0;
				} else {
					Boolean Boolean1 = LuaManager.thread.pcallBoolean(this.comp, object, object2);
					return Boolean1 == Boolean.TRUE ? -1 : 1;
				}
			}
		}
	}
}
