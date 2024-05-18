package zombie.network;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import zombie.GameWindow;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.config.DoubleConfigOption;
import zombie.config.IntegerConfigOption;
import zombie.config.StringConfigOption;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.logger.LoggerManager;
import zombie.debug.DebugLog;


public class ServerOptions {
	public static final ServerOptions instance = new ServerOptions();
	private ArrayList publicOptions = new ArrayList();
	public static HashMap clientOptionsList = null;
	public static final int MAX_PORT = 65535;
	private ArrayList options = new ArrayList();
	private HashMap optionByName = new HashMap();
	public ServerOptions.DoubleServerOption nightlengthmodifier = new ServerOptions.DoubleServerOption(this, "nightlengthmodifier", 0.1, 1.0, 1.0);
	public ServerOptions.BooleanServerOption PVP = new ServerOptions.BooleanServerOption(this, "PVP", true);
	public ServerOptions.BooleanServerOption PauseEmpty = new ServerOptions.BooleanServerOption(this, "PauseEmpty", false);
	public ServerOptions.BooleanServerOption GlobalChat = new ServerOptions.BooleanServerOption(this, "GlobalChat", true);
	public ServerOptions.StringServerOption ChatStreams = new ServerOptions.StringServerOption(this, "ChatStreams", "s,r,a,w,y,sh,f,all");
	public ServerOptions.BooleanServerOption Open = new ServerOptions.BooleanServerOption(this, "Open", true);
	public ServerOptions.TextServerOption ServerWelcomeMessage = new ServerOptions.TextServerOption(this, "ServerWelcomeMessage", "Welcome to Project Zomboid Multiplayer! <LINE> <LINE> To interact with the Chat panel: press Tab, T, or Enter. <LINE> <LINE> The Tab key will change the target stream of the message. <LINE> <LINE> Global Streams: /all <LINE> Local Streams: /say, /yell <LINE> Special Steams: /whisper, /safehouse, /faction. <LINE> <LINE> Press the Up arrow to cycle through your message history. Click the Gear icon to customize chat. <LINE> <LINE> Happy surviving!");
	public ServerOptions.BooleanServerOption LogLocalChat = new ServerOptions.BooleanServerOption(this, "LogLocalChat", false);
	public ServerOptions.BooleanServerOption AutoCreateUserInWhiteList = new ServerOptions.BooleanServerOption(this, "AutoCreateUserInWhiteList", false);
	public ServerOptions.BooleanServerOption DisplayUserName = new ServerOptions.BooleanServerOption(this, "DisplayUserName", true);
	public ServerOptions.StringServerOption SpawnPoint = new ServerOptions.StringServerOption(this, "SpawnPoint", "0,0,0");
	public ServerOptions.BooleanServerOption SafetySystem = new ServerOptions.BooleanServerOption(this, "SafetySystem", true);
	public ServerOptions.BooleanServerOption ShowSafety = new ServerOptions.BooleanServerOption(this, "ShowSafety", true);
	public ServerOptions.IntegerServerOption SafetyToggleTimer = new ServerOptions.IntegerServerOption(this, "SafetyToggleTimer", 0, 1000, 2);
	public ServerOptions.IntegerServerOption SafetyCooldownTimer = new ServerOptions.IntegerServerOption(this, "SafetyCooldownTimer", 0, 1000, 3);
	public ServerOptions.StringServerOption SpawnItems = new ServerOptions.StringServerOption(this, "SpawnItems", "");
	public ServerOptions.IntegerServerOption DefaultPort = new ServerOptions.IntegerServerOption(this, "DefaultPort", 0, 65535, 16261);
	public ServerOptions.IntegerServerOption ResetID = new ServerOptions.IntegerServerOption(this, "ResetID", 0, Integer.MAX_VALUE, Rand.Next(1000000000));
	public ServerOptions.StringServerOption Mods = new ServerOptions.StringServerOption(this, "Mods", "");
	public ServerOptions.StringServerOption Map = new ServerOptions.StringServerOption(this, "Map", "Muldraugh, KY");
	public ServerOptions.BooleanServerOption DoLuaChecksum = new ServerOptions.BooleanServerOption(this, "DoLuaChecksum", true);
	public ServerOptions.BooleanServerOption Public = new ServerOptions.BooleanServerOption(this, "Public", false);
	public ServerOptions.StringServerOption PublicName = new ServerOptions.StringServerOption(this, "PublicName", "My PZ Server");
	public ServerOptions.TextServerOption PublicDescription = new ServerOptions.TextServerOption(this, "PublicDescription", "");
	public ServerOptions.IntegerServerOption MaxPlayers = new ServerOptions.IntegerServerOption(this, "MaxPlayers", 1, 256, 64);
	public ServerOptions.IntegerServerOption PingFrequency = new ServerOptions.IntegerServerOption(this, "PingFrequency", 0, Integer.MAX_VALUE, 10);
	public ServerOptions.IntegerServerOption PingLimit = new ServerOptions.IntegerServerOption(this, "PingLimit", 0, Integer.MAX_VALUE, 400);
	public ServerOptions.IntegerServerOption HoursForLootRespawn = new ServerOptions.IntegerServerOption(this, "HoursForLootRespawn", 0, Integer.MAX_VALUE, 0);
	public ServerOptions.IntegerServerOption MaxItemsForLootRespawn = new ServerOptions.IntegerServerOption(this, "MaxItemsForLootRespawn", 1, Integer.MAX_VALUE, 4);
	public ServerOptions.BooleanServerOption ConstructionPreventsLootRespawn = new ServerOptions.BooleanServerOption(this, "ConstructionPreventsLootRespawn", true);
	public ServerOptions.BooleanServerOption DropOffWhiteListAfterDeath = new ServerOptions.BooleanServerOption(this, "DropOffWhiteListAfterDeath", false);
	public ServerOptions.BooleanServerOption NoFire = new ServerOptions.BooleanServerOption(this, "NoFire", false);
	public ServerOptions.BooleanServerOption AnnounceDeath = new ServerOptions.BooleanServerOption(this, "AnnounceDeath", false);
	public ServerOptions.DoubleServerOption MinutesPerPage = new ServerOptions.DoubleServerOption(this, "MinutesPerPage", 0.0, 60.0, 1.0);
	public ServerOptions.IntegerServerOption SaveWorldEveryMinutes = new ServerOptions.IntegerServerOption(this, "SaveWorldEveryMinutes", 0, Integer.MAX_VALUE, 0);
	public ServerOptions.BooleanServerOption PlayerSafehouse = new ServerOptions.BooleanServerOption(this, "PlayerSafehouse", false);
	public ServerOptions.BooleanServerOption AdminSafehouse = new ServerOptions.BooleanServerOption(this, "AdminSafehouse", false);
	public ServerOptions.BooleanServerOption SafehouseAllowTrepass = new ServerOptions.BooleanServerOption(this, "SafehouseAllowTrepass", true);
	public ServerOptions.BooleanServerOption SafehouseAllowFire = new ServerOptions.BooleanServerOption(this, "SafehouseAllowFire", true);
	public ServerOptions.BooleanServerOption SafehouseAllowLoot = new ServerOptions.BooleanServerOption(this, "SafehouseAllowLoot", true);
	public ServerOptions.BooleanServerOption SafehouseAllowRespawn = new ServerOptions.BooleanServerOption(this, "SafehouseAllowRespawn", false);
	public ServerOptions.IntegerServerOption SafehouseDaySurvivedToClaim = new ServerOptions.IntegerServerOption(this, "SafehouseDaySurvivedToClaim", 0, Integer.MAX_VALUE, 0);
	public ServerOptions.IntegerServerOption SafeHouseRemovalTime = new ServerOptions.IntegerServerOption(this, "SafeHouseRemovalTime", 0, Integer.MAX_VALUE, 144);
	public ServerOptions.BooleanServerOption AllowDestructionBySledgehammer = new ServerOptions.BooleanServerOption(this, "AllowDestructionBySledgehammer", true);
	public ServerOptions.BooleanServerOption KickFastPlayers = new ServerOptions.BooleanServerOption(this, "KickFastPlayers", false);
	public ServerOptions.StringServerOption ServerPlayerID = new ServerOptions.StringServerOption(this, "ServerPlayerID", Integer.toString(Rand.Next(Integer.MAX_VALUE)));
	public ServerOptions.IntegerServerOption RCONPort = new ServerOptions.IntegerServerOption(this, "RCONPort", 0, 65535, 27015);
	public ServerOptions.StringServerOption RCONPassword = new ServerOptions.StringServerOption(this, "RCONPassword", "");
	public ServerOptions.BooleanServerOption DiscordEnable = new ServerOptions.BooleanServerOption(this, "DiscordEnable", false);
	public ServerOptions.StringServerOption DiscordToken = new ServerOptions.StringServerOption(this, "DiscordToken", "");
	public ServerOptions.StringServerOption DiscordChannel = new ServerOptions.StringServerOption(this, "DiscordChannel", "");
	public ServerOptions.StringServerOption DiscordChannelID = new ServerOptions.StringServerOption(this, "DiscordChannelID", "");
	public ServerOptions.StringServerOption Password = new ServerOptions.StringServerOption(this, "Password", "");
	public ServerOptions.IntegerServerOption MaxAccountsPerUser = new ServerOptions.IntegerServerOption(this, "MaxAccountsPerUser", 0, Integer.MAX_VALUE, 0);
	public ServerOptions.BooleanServerOption SleepAllowed = new ServerOptions.BooleanServerOption(this, "SleepAllowed", false);
	public ServerOptions.BooleanServerOption SleepNeeded = new ServerOptions.BooleanServerOption(this, "SleepNeeded", false);
	public ServerOptions.IntegerServerOption SteamPort1 = new ServerOptions.IntegerServerOption(this, "SteamPort1", 0, 65535, 8766);
	public ServerOptions.IntegerServerOption SteamPort2 = new ServerOptions.IntegerServerOption(this, "SteamPort2", 0, 65535, 8767);
	public ServerOptions.StringServerOption WorkshopItems = new ServerOptions.StringServerOption(this, "WorkshopItems", "");
	public ServerOptions.StringServerOption SteamScoreboard = new ServerOptions.StringServerOption(this, "SteamScoreboard", "true");
	public ServerOptions.BooleanServerOption SteamVAC = new ServerOptions.BooleanServerOption(this, "SteamVAC", true);
	public ServerOptions.BooleanServerOption UPnP = new ServerOptions.BooleanServerOption(this, "UPnP", true);
	public ServerOptions.IntegerServerOption UPnPLeaseTime = new ServerOptions.IntegerServerOption(this, "UPnPLeaseTime", 0, Integer.MAX_VALUE, 86400);
	public ServerOptions.BooleanServerOption UPnPZeroLeaseTimeFallback = new ServerOptions.BooleanServerOption(this, "UPnPZeroLeaseTimeFallback", true);
	public ServerOptions.BooleanServerOption UPnPForce = new ServerOptions.BooleanServerOption(this, "UPnPForce", true);
	public ServerOptions.IntegerServerOption CoopServerLaunchTimeout = new ServerOptions.IntegerServerOption(this, "CoopServerLaunchTimeout", 5, 600, 20);
	public ServerOptions.IntegerServerOption CoopMasterPingTimeout = new ServerOptions.IntegerServerOption(this, "CoopMasterPingTimeout", 5, 600, 60);
	public ServerOptions.BooleanServerOption VoiceEnable = new ServerOptions.BooleanServerOption(this, "VoiceEnable", true);
	public ServerOptions.IntegerServerOption VoiceComplexity = new ServerOptions.IntegerServerOption(this, "VoiceComplexity", 0, 10, 5);
	public ServerOptions.IntegerServerOption VoicePeriod = new ServerOptions.IntegerServerOption(this, "VoicePeriod", 2, 60, 20);
	public ServerOptions.IntegerServerOption VoiceSampleRate = new ServerOptions.IntegerServerOption(this, "VoiceSampleRate", 8000, 24000, 24000);
	public ServerOptions.IntegerServerOption VoiceBuffering = new ServerOptions.IntegerServerOption(this, "VoiceBuffering", 800, 32000, 8000);
	public ServerOptions.DoubleServerOption VoiceMinDistance = new ServerOptions.DoubleServerOption(this, "VoiceMinDistance", 0.0, 100000.0, 10.0);
	public ServerOptions.DoubleServerOption VoiceMaxDistance = new ServerOptions.DoubleServerOption(this, "VoiceMaxDistance", 0.0, 100000.0, 300.0);
	public ServerOptions.BooleanServerOption Voice3D = new ServerOptions.BooleanServerOption(this, "Voice3D", true);
	public ServerOptions.IntegerServerOption PhysicsDelay = new ServerOptions.IntegerServerOption(this, "PhysicsDelay", 300, 1000, 500);
	public ServerOptions.DoubleServerOption SpeedLimit = new ServerOptions.DoubleServerOption(this, "SpeedLimit", 10.0, 150.0, 50.0);
	public ServerOptions.StringServerOption server_browser_announced_ip = new ServerOptions.StringServerOption(this, "server_browser_announced_ip", "");
	public ServerOptions.BooleanServerOption UseTCPForMapDownloads = new ServerOptions.BooleanServerOption(this, "UseTCPForMapDownloads", false);
	public ServerOptions.BooleanServerOption PlayerRespawnWithSelf = new ServerOptions.BooleanServerOption(this, "PlayerRespawnWithSelf", false);
	public ServerOptions.BooleanServerOption PlayerRespawnWithOther = new ServerOptions.BooleanServerOption(this, "PlayerRespawnWithOther", false);
	public ServerOptions.DoubleServerOption FastForwardMultiplier = new ServerOptions.DoubleServerOption(this, "FastForwardMultiplier", 1.0, 100.0, 40.0);
	public ServerOptions.BooleanServerOption PlayerSaveOnDamage = new ServerOptions.BooleanServerOption(this, "PlayerSaveOnDamage", true);
	public ServerOptions.BooleanServerOption SaveTransactionID = new ServerOptions.BooleanServerOption(this, "SaveTransactionID", false);
	public ServerOptions.BooleanServerOption DisableSafehouseWhenPlayerConnected = new ServerOptions.BooleanServerOption(this, "DisableSafehouseWhenPlayerConnected", false);
	public ServerOptions.BooleanServerOption Faction = new ServerOptions.BooleanServerOption(this, "Faction", true);
	public ServerOptions.IntegerServerOption FactionDaySurvivedToCreate = new ServerOptions.IntegerServerOption(this, "FactionDaySurvivedToCreate", 0, Integer.MAX_VALUE, 0);
	public ServerOptions.IntegerServerOption FactionPlayersRequiredForTag = new ServerOptions.IntegerServerOption(this, "FactionPlayersRequiredForTag", 1, Integer.MAX_VALUE, 1);
	public ServerOptions.BooleanServerOption AllowTradeUI = new ServerOptions.BooleanServerOption(this, "AllowTradeUI", true);
	public ServerOptions.DoubleServerOption HoursForWorldItemRemoval = new ServerOptions.DoubleServerOption(this, "HoursForWorldItemRemoval", 0.0, 3.4028234663852886E38, 0.0);
	public ServerOptions.StringServerOption WorldItemRemovalList = new ServerOptions.StringServerOption(this, "WorldItemRemovalList", "Base.Vest,Base.Shirt,Base.Blouse,Base.Skirt,Base.Shoes");
	public ServerOptions.BooleanServerOption ItemRemovalListBlacklistToggle = new ServerOptions.BooleanServerOption(this, "ItemRemovalListBlacklistToggle", false);
	public ServerOptions.BooleanServerOption DisableRadioStaff = new ServerOptions.BooleanServerOption(this, "DisableRadioStaff", false);
	public ServerOptions.BooleanServerOption DisableRadioAdmin = new ServerOptions.BooleanServerOption(this, "DisableRadioAdmin", true);
	public ServerOptions.BooleanServerOption DisableRadioGM = new ServerOptions.BooleanServerOption(this, "DisableRadioGM", true);
	public ServerOptions.BooleanServerOption DisableRadioOverseer = new ServerOptions.BooleanServerOption(this, "DisableRadioOverseer", false);
	public ServerOptions.BooleanServerOption DisableRadioModerator = new ServerOptions.BooleanServerOption(this, "DisableRadioModerator", false);
	public ServerOptions.BooleanServerOption DisableRadioInvisible = new ServerOptions.BooleanServerOption(this, "DisableRadioInvisible", true);
	public ServerOptions.StringServerOption ClientCommandFilter = new ServerOptions.StringServerOption(this, "ClientCommandFilter", "-vehicle.*;+vehicle.damageWindow;+vehicle.fixPart;+vehicle.installPart;+vehicle.uninstallPart");
	public ServerOptions.IntegerServerOption ItemNumbersLimitPerContainer = new ServerOptions.IntegerServerOption(this, "ItemNumbersLimitPerContainer", 0, 9000, 0);
	public ServerOptions.IntegerServerOption BloodSplatLifespanDays = new ServerOptions.IntegerServerOption(this, "BloodSplatLifespanDays", 0, 365, 0);
	public ServerOptions.BooleanServerOption AllowNonAsciiUsername = new ServerOptions.BooleanServerOption(this, "AllowNonAsciiUsername", false);
	public ServerOptions.BooleanServerOption BanKickGlobalSound = new ServerOptions.BooleanServerOption(this, "BanKickGlobalSound", true);
	public ServerOptions.BooleanServerOption RemovePlayerCorpsesOnCorpseRemoval = new ServerOptions.BooleanServerOption(this, "RemovePlayerCorpsesOnCorpseRemoval", false);
	public ServerOptions.IntegerServerOption ZombieUpdateMaxHighPriority = new ServerOptions.IntegerServerOption(this, "ZombieUpdateMaxHighPriority", 1, 500, 300);
	public ServerOptions.DoubleServerOption ZombieUpdateDelta = new ServerOptions.DoubleServerOption(this, "ZombieUpdateDelta", 0.1, 5.0, 0.5);
	public ServerOptions.DoubleServerOption ZombieUpdateRadiusLowPriority = new ServerOptions.DoubleServerOption(this, "ZombieUpdateRadiusLowPriority", 0.0, 500.0, 0.0);
	public ServerOptions.DoubleServerOption ZombieUpdateRadiusHighPriority = new ServerOptions.DoubleServerOption(this, "ZombieUpdateRadiusHighPriority", 0.0, 500.0, 15.8);
	public static ArrayList cardList = null;

	public ServerOptions() {
		this.publicOptions.clear();
		this.publicOptions.addAll(this.optionByName.keySet());
		this.publicOptions.remove("Password");
		this.publicOptions.remove("RCONPort");
		this.publicOptions.remove("RCONPassword");
		this.publicOptions.remove(this.DiscordToken.getName());
		this.publicOptions.remove(this.DiscordChannel.getName());
		this.publicOptions.remove(this.DiscordChannelID.getName());
		Collections.sort(this.publicOptions);
	}

	private void initOptions() {
		initClientCommandsHelp();
		Iterator iterator = this.options.iterator();
		while (iterator.hasNext()) {
			ServerOptions.ServerOption serverOption = (ServerOptions.ServerOption)iterator.next();
			serverOption.asConfigOption().resetToDefault();
		}
	}

	public ArrayList getPublicOptions() {
		return this.publicOptions;
	}

	public ArrayList getOptions() {
		return this.options;
	}

	public static void initClientCommandsHelp() {
		clientOptionsList = new HashMap();
		clientOptionsList.put("help", Translator.getText("UI_ServerOptionDesc_Help"));
		clientOptionsList.put("changepwd", Translator.getText("UI_ServerOptionDesc_ChangePwd"));
		clientOptionsList.put("roll", Translator.getText("UI_ServerOptionDesc_Roll"));
		clientOptionsList.put("card", Translator.getText("UI_ServerOptionDesc_Card"));
		clientOptionsList.put("safehouse", Translator.getText("UI_ServerOptionDesc_SafeHouse"));
	}

	public void init() {
		this.initOptions();
		File file = new File(GameWindow.getCacheDir() + File.separator + "Server");
		if (!file.exists()) {
			file.mkdirs();
		}

		File file2 = new File(GameWindow.getCacheDir() + File.separator + "Server" + File.separator + GameServer.ServerName + ".ini");
		if (file2.exists()) {
			try {
				Core.getInstance().loadOptions();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			if (this.loadServerTextFile(GameServer.ServerName)) {
				this.saveServerTextFile(GameServer.ServerName);
			}
		} else {
			this.initSpawnRegionsFile(new File(GameWindow.getCacheDir() + File.separator + "Server" + File.separator + GameServer.ServerName + "_spawnregions.lua"));
			this.saveServerTextFile(GameServer.ServerName);
		}

		LoggerManager.init();
	}

	public void resetRegionFile() {
		File file = new File(GameWindow.getCacheDir() + File.separator + "Server" + File.separator + GameServer.ServerName + "_spawnregions.lua");
		file.delete();
		this.initSpawnRegionsFile(file);
	}

	private void initSpawnRegionsFile(File file) {
		if (!file.exists()) {
			DebugLog.log("creating server spawnregions file \"" + file.getPath() + "\"");
			try {
				file.createNewFile();
				FileWriter fileWriter = new FileWriter(file);
				fileWriter.write(Translator.getText("UI_ServerOptions_SpawnRegionFile1") + System.lineSeparator());
				fileWriter.write(Translator.getText("UI_ServerOptions_SpawnRegionFile2") + System.lineSeparator());
				fileWriter.write(Translator.getText("UI_ServerOptions_SpawnRegionFile3") + System.lineSeparator());
				fileWriter.write("function SpawnRegions()" + System.lineSeparator());
				fileWriter.write("\treturn {" + System.lineSeparator());
				fileWriter.write("\t\t{ name = \"Muldraugh, KY\", file = \"media/maps/Muldraugh, KY/spawnpoints.lua\" }," + System.lineSeparator());
				fileWriter.write("\t\t{ name = \"West Point, KY\", file = \"media/maps/West Point, KY/spawnpoints.lua\" }," + System.lineSeparator());
				fileWriter.write("\t\t{ name = \"Rosewood, KY\", file = \"media/maps/Rosewood, KY/spawnpoints.lua\" }," + System.lineSeparator());
				fileWriter.write("\t\t{ name = \"Riverside, KY\", file = \"media/maps/Riverside, KY/spawnpoints.lua\" }," + System.lineSeparator());
				fileWriter.write("\t\t-- " + Translator.getText("UI_ServerOptions_SpawnRegionFile5") + System.lineSeparator());
				fileWriter.write("--\t\t{ name = \"Twiggy\'s Bar\", serverfile = \"" + GameServer.ServerName + "_spawnpoints.lua\" }," + System.lineSeparator());
				fileWriter.write("\t}" + System.lineSeparator());
				fileWriter.write("end" + System.lineSeparator());
				fileWriter.close();
				fileWriter = new FileWriter(file.getParent() + File.separator + GameServer.ServerName + "_spawnpoints.lua");
				fileWriter.write(Translator.getText("UI_ServerOptions_SpawnRegionFile1") + System.lineSeparator());
				fileWriter.write(Translator.getText("UI_ServerOptions_SpawnRegionFile4") + System.lineSeparator());
				fileWriter.write("function SpawnPoints()" + System.lineSeparator());
				fileWriter.write("\treturn {" + System.lineSeparator());
				fileWriter.write("\t\tunemployed = {" + System.lineSeparator());
				fileWriter.write("\t\t\t{ worldX = 40, worldY = 22, posX = 67, posY = 201 }" + System.lineSeparator());
				fileWriter.write("\t\t}" + System.lineSeparator());
				fileWriter.write("\t}" + System.lineSeparator());
				fileWriter.write("end" + System.lineSeparator());
				fileWriter.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public String getOption(String string) {
		ServerOptions.ServerOption serverOption = this.getOptionByName(string);
		return serverOption == null ? null : serverOption.asConfigOption().getValueAsString();
	}

	public Boolean getBoolean(String string) {
		ServerOptions.ServerOption serverOption = this.getOptionByName(string);
		return serverOption instanceof ServerOptions.BooleanServerOption ? (Boolean)((ServerOptions.BooleanServerOption)serverOption).getValueAsObject() : null;
	}

	public Float getFloat(String string) {
		ServerOptions.ServerOption serverOption = this.getOptionByName(string);
		return serverOption instanceof ServerOptions.DoubleServerOption ? (float)((ServerOptions.DoubleServerOption)serverOption).getValue() : null;
	}

	public Double getDouble(String string) {
		ServerOptions.ServerOption serverOption = this.getOptionByName(string);
		return serverOption instanceof ServerOptions.DoubleServerOption ? ((ServerOptions.DoubleServerOption)serverOption).getValue() : null;
	}

	public Integer getInteger(String string) {
		ServerOptions.ServerOption serverOption = this.getOptionByName(string);
		return serverOption instanceof ServerOptions.IntegerServerOption ? ((ServerOptions.IntegerServerOption)serverOption).getValue() : null;
	}

	public void putOption(String string, String string2) {
		ServerOptions.ServerOption serverOption = this.getOptionByName(string);
		if (serverOption != null) {
			serverOption.asConfigOption().parse(string2);
		}
	}

	public void putSaveOption(String string, String string2) {
		this.putOption(string, string2);
		this.saveServerTextFile(GameServer.ServerName);
	}

	public String changeOption(String string, String string2) {
		ServerOptions.ServerOption serverOption = this.getOptionByName(string);
		if (serverOption == null) {
			return "Option " + string + " doesn\'t exist.";
		} else {
			serverOption.asConfigOption().parse(string2);
			return !this.saveServerTextFile(GameServer.ServerName) ? "An error as occured." : "Option : " + string + " is now : " + serverOption.asConfigOption().getValueAsString();
		}
	}

	public static ServerOptions getInstance() {
		return instance;
	}

	public static ArrayList getClientCommandList(boolean boolean1) {
		String string = " <LINE> ";
		if (!boolean1) {
			string = "\n";
		}

		if (clientOptionsList == null) {
			initClientCommandsHelp();
		}

		ArrayList arrayList = new ArrayList();
		Iterator iterator = clientOptionsList.keySet().iterator();
		String string2 = null;
		arrayList.add("List of commands : " + string);
		while (iterator.hasNext()) {
			string2 = (String)iterator.next();
			arrayList.add("* " + string2 + " : " + (String)clientOptionsList.get(string2) + (iterator.hasNext() ? string : ""));
		}

		return arrayList;
	}

	public static String getRandomCard() {
		if (cardList == null) {
			cardList = new ArrayList();
			cardList.add("the Ace of Clubs");
			cardList.add("a Two of Clubs");
			cardList.add("a Three of Clubs");
			cardList.add("a Four of Clubs");
			cardList.add("a Five of Clubs");
			cardList.add("a Six of Clubs");
			cardList.add("a Seven of Clubs");
			cardList.add("a Height of Clubs");
			cardList.add("a Nine of Clubs");
			cardList.add("a Ten of Clubs");
			cardList.add("the Jack of Clubs");
			cardList.add("the Queen of Clubs");
			cardList.add("the King of Clubs");
			cardList.add("the Ace of Diamonds");
			cardList.add("a Two of Diamonds");
			cardList.add("a Three of Diamonds");
			cardList.add("a Four of Diamonds");
			cardList.add("a Five of Diamonds");
			cardList.add("a Six of Diamonds");
			cardList.add("a Seven of Diamonds");
			cardList.add("a Height of Diamonds");
			cardList.add("a Nine of Diamonds");
			cardList.add("a Ten of Diamonds");
			cardList.add("the Jack of Diamonds");
			cardList.add("the Queen of Diamonds");
			cardList.add("the King of Diamonds");
			cardList.add("the Ace of Hearts");
			cardList.add("a Two of Hearts");
			cardList.add("a Three of Hearts");
			cardList.add("a Four of Hearts");
			cardList.add("a Five of Hearts");
			cardList.add("a Six of Hearts");
			cardList.add("a Seven of Hearts");
			cardList.add("a Height of Hearts");
			cardList.add("a Nine of Hearts");
			cardList.add("a Ten of Hearts");
			cardList.add("the Jack of Hearts");
			cardList.add("the Queen of Hearts");
			cardList.add("the King of Hearts");
			cardList.add("the Ace of Spades");
			cardList.add("a Two of Spades");
			cardList.add("a Three of Spades");
			cardList.add("a Four of Spades");
			cardList.add("a Five of Spades");
			cardList.add("a Six of Spades");
			cardList.add("a Seven of Spades");
			cardList.add("a Height of Spades");
			cardList.add("a Nine of Spades");
			cardList.add("a Ten of Spades");
			cardList.add("the Jack of Spades");
			cardList.add("the Queen of Spades");
			cardList.add("the King of Spades");
		}

		return (String)cardList.get(Rand.Next(cardList.size()));
	}

	public void addOption(ServerOptions.ServerOption serverOption) {
		if (this.optionByName.containsKey(serverOption.asConfigOption().getName())) {
			throw new IllegalArgumentException();
		} else {
			this.options.add(serverOption);
			this.optionByName.put(serverOption.asConfigOption().getName(), serverOption);
		}
	}

	public int getNumOptions() {
		return this.options.size();
	}

	public ServerOptions.ServerOption getOptionByIndex(int int1) {
		return (ServerOptions.ServerOption)this.options.get(int1);
	}

	public ServerOptions.ServerOption getOptionByName(String string) {
		return (ServerOptions.ServerOption)this.optionByName.get(string);
	}

	public boolean loadServerTextFile(String string) {
		ConfigFile configFile = new ConfigFile();
		String string2 = GameWindow.getCacheDir() + File.separator + "Server" + File.separator + string + ".ini";
		if (configFile.read(string2)) {
			Iterator iterator = configFile.getOptions().iterator();
			while (iterator.hasNext()) {
				ConfigOption configOption = (ConfigOption)iterator.next();
				ServerOptions.ServerOption serverOption = (ServerOptions.ServerOption)this.optionByName.get(configOption.getName());
				if (serverOption != null) {
					serverOption.asConfigOption().parse(configOption.getValueAsString());
				}
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean saveServerTextFile(String string) {
		ConfigFile configFile = new ConfigFile();
		String string2 = GameWindow.getCacheDir() + File.separator + "Server" + File.separator + string + ".ini";
		ArrayList arrayList = new ArrayList();
		Iterator iterator = this.options.iterator();
		while (iterator.hasNext()) {
			ServerOptions.ServerOption serverOption = (ServerOptions.ServerOption)iterator.next();
			arrayList.add(serverOption.asConfigOption());
		}

		return configFile.write(string2, 0, arrayList);
	}

	public static class TextServerOption extends StringConfigOption implements ServerOptions.ServerOption {

		public TextServerOption(ServerOptions serverOptions, String string, String string2) {
			super(string, string2);
			serverOptions.addOption(this);
		}

		public String getType() {
			return "text";
		}

		public ConfigOption asConfigOption() {
			return this;
		}

		public String getTooltip() {
			return Translator.getTextOrNull("UI_ServerOption_" + this.name + "_tooltip");
		}
	}

	public static class StringServerOption extends StringConfigOption implements ServerOptions.ServerOption {

		public StringServerOption(ServerOptions serverOptions, String string, String string2) {
			super(string, string2);
			serverOptions.addOption(this);
		}

		public ConfigOption asConfigOption() {
			return this;
		}

		public String getTooltip() {
			return Translator.getTextOrNull("UI_ServerOption_" + this.name + "_tooltip");
		}
	}

	public static class IntegerServerOption extends IntegerConfigOption implements ServerOptions.ServerOption {

		public IntegerServerOption(ServerOptions serverOptions, String string, int int1, int int2, int int3) {
			super(string, int1, int2, int3);
			serverOptions.addOption(this);
		}

		public ConfigOption asConfigOption() {
			return this;
		}

		public String getTooltip() {
			return Translator.getTextOrNull("UI_ServerOption_" + this.name + "_tooltip");
		}
	}

	public static class DoubleServerOption extends DoubleConfigOption implements ServerOptions.ServerOption {

		public DoubleServerOption(ServerOptions serverOptions, String string, double double1, double double2, double double3) {
			super(string, double1, double2, double3);
			serverOptions.addOption(this);
		}

		public ConfigOption asConfigOption() {
			return this;
		}

		public String getTooltip() {
			return Translator.getTextOrNull("UI_ServerOption_" + this.name + "_tooltip");
		}
	}

	public static class BooleanServerOption extends BooleanConfigOption implements ServerOptions.ServerOption {

		public BooleanServerOption(ServerOptions serverOptions, String string, boolean boolean1) {
			super(string, boolean1);
			serverOptions.addOption(this);
		}

		public ConfigOption asConfigOption() {
			return this;
		}

		public String getTooltip() {
			return Translator.getTextOrNull("UI_ServerOption_" + this.name + "_tooltip");
		}
	}

	public interface ServerOption {

		ConfigOption asConfigOption();

		String getTooltip();
	}
}
