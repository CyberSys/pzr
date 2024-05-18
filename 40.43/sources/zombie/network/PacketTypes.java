package zombie.network;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import zombie.core.network.ByteBufferWriter;


public class PacketTypes {
	public static final short Disconnect = 0;
	public static final short ServerPulse = 1;
	public static final short Login = 2;
	public static final short ChunkRequest = 3;
	public static final short KeepAlive = 4;
	public static final short Vehicles = 5;
	public static final short PlayerConnect = 6;
	public static final short PlayerUpdateInfo = 7;
	public static final short CreateZombie = 8;
	public static final short MetaGrid = 9;
	public static final short ZombieUpdateInfo = 10;
	public static final short Helicopter = 11;
	public static final short SyncIsoObject = 12;
	public static final short PlayerTimeout = 13;
	public static final short SteamGeneric = 14;
	public static final short ServerMap = 15;
	public static final short PassengerMap = 16;
	public static final short AddItemToMap = 17;
	public static final short SentChunk = 18;
	public static final short SyncClock = 19;
	public static final short AddInventoryItemToContainer = 20;
	public static final short ConnectionDetails = 21;
	public static final short RemoveInventoryItemFromContainer = 22;
	public static final short RemoveItemFromSquare = 23;
	public static final short RequestLargeAreaZip = 24;
	public static final short Equip = 25;
	public static final short HitCharacter = 26;
	public static final short AddCoopPlayer = 27;
	public static final short WeaponHit = 28;
	public static final short ZombieDie = 29;
	public static final short KillZombie = 30;
	public static final short SandboxOptions = 31;
	public static final short SmashWindow = 32;
	public static final short PlayerDeath = 33;
	public static final short RequestZipList = 34;
	public static final short ItemStats = 35;
	public static final short NotRequiredInZip = 36;
	public static final short RequestData = 37;
	public static final short SendDeadZombie = 39;
	public static final short AccessDenied = 40;
	public static final short SendDamage = 41;
	public static final short Bandage = 42;
	public static final short EatFood = 43;
	public static final short RequestItemsForContainer = 44;
	public static final short Drink = 45;
	public static final short SyncAlarmClock = 46;
	public static final short PacketCounts = 47;
	public static final short SendModData = 48;
	public static final short RemoveContestedItemsFromInventory = 49;
	public static final short ScoreboardUpdate = 50;
	public static final short ReceiveModData = 51;
	public static final short ServerQuit = 52;
	public static final short PlaySound = 53;
	public static final short WorldSound = 54;
	public static final short AddAmbient = 55;
	public static final short SyncClothing = 56;
	public static final short ClientCommand = 57;
	public static final short ObjectModData = 58;
	public static final short ObjectChange = 59;
	public static final short BloodSplatter = 60;
	public static final short ZombieSound = 61;
	public static final short ZombieDescriptors = 62;
	public static final short SlowFactor = 63;
	public static final short Weather = 64;
	public static final short SyncPlayerInventory = 65;
	public static final short UNUSED66 = 66;
	public static final short RequestPlayerData = 67;
	public static final short RemoveCorpseFromMap = 68;
	public static final short AddCorpseToMap = 69;
	public static final short StartFire = 75;
	public static final short UpdateItemSprite = 76;
	public static final short StartRain = 77;
	public static final short StopRain = 78;
	public static final short WorldMessage = 79;
	public static final short getModData = 80;
	public static final short ReceiveCommand = 81;
	public static final short ReloadOptions = 82;
	public static final short Kicked = 83;
	public static final short ExtraInfo = 84;
	public static final short AddItemInInventory = 85;
	public static final short ChangeSafety = 86;
	public static final short Ping = 87;
	public static final short WriteLog = 88;
	public static final short AddXP = 89;
	public static final short UpdateOverlaySprite = 90;
	public static final short Checksum = 91;
	public static final short ConstructedZone = 92;
	public static final short UNUSED93 = 93;
	public static final short RegisterZone = 94;
	public static final short UNUSED95 = 95;
	public static final short UNUSED96 = 96;
	public static final short WoundInfection = 97;
	public static final short Stitch = 98;
	public static final short Disinfect = 99;
	public static final short AdditionalPain = 100;
	public static final short RemoveGlass = 101;
	public static final short Splint = 102;
	public static final short RemoveBullet = 103;
	public static final short CleanBurn = 104;
	public static final short SyncThumpable = 105;
	public static final short SyncDoorKey = 106;
	public static final short AddXpCommand = 107;
	public static final short Teleport = 108;
	public static final short RemoveBlood = 109;
	public static final short AddExplosiveTrap = 110;
	public static final short RemoveSpecialObjectFromSquare = 111;
	public static final short BodyDamageUpdate = 112;
	public static final short UNUSED113 = 113;
	public static final short SyncSafehouse = 114;
	public static final short SledgehammerDestroy = 115;
	public static final short StopFire = 116;
	public static final short Cataplasm = 117;
	public static final short AddAlarm = 118;
	public static final short PlaySoundEveryPlayer = 119;
	public static final short SyncFurnace = 120;
	public static final short SendCustomColor = 121;
	public static final short SyncCompost = 122;
	public static final short ChangePlayerStats = 123;
	public static final short AddXpFromPlayerStatsUI = 124;
	public static final short AddLevelUpPoint = 125;
	public static final short SyncXP = 126;
	public static final short Userlog = 128;
	public static final short AddUserlog = 129;
	public static final short RemoveUserlog = 130;
	public static final short AddWarningPoint = 131;
	public static final short MessageForAdmin = 132;
	public static final short WakeUpPlayer = 133;
	public static final short SendTransactionID = 134;
	public static final short GetDBSchema = 135;
	public static final short GetTableResult = 136;
	public static final short ExecuteQuery = 137;
	public static final short ChangeTextColor = 138;
	public static final short SyncNonPvpZone = 139;
	public static final short SyncFaction = 140;
	public static final short SendFactionInvite = 141;
	public static final short AcceptedFactionInvite = 142;
	public static final short AddTicket = 143;
	public static final short ViewTickets = 144;
	public static final short RemoveTicket = 145;
	public static final short RequestTrading = 146;
	public static final short TradingUIAddItem = 147;
	public static final short TradingUIRemoveItem = 148;
	public static final short TradingUIUpdateState = 149;
	public static final short SendItemListNet = 150;
	public static final short ChunkObjectState = 151;
	public static final short ReadAnnotedMap = 152;
	public static final short RequestInventory = 153;
	public static final short SendInventory = 154;
	public static final short InvMngReqItem = 155;
	public static final short InvMngGetItem = 156;
	public static final short InvMngRemoveItem = 157;
	public static final short StartPause = 158;
	public static final short StopPause = 159;
	public static final short TimeSync = 160;
	public static final short SyncIsoObjectReq = 161;
	public static final short PlayerSave = 162;
	public static final short SyncWorldObjectsReq = 163;
	public static final short SyncObjects = 164;
	public static final short PlayerOnBeaten = 165;
	public static final short SpawnRegion = 171;
	public static final short PlayerDamageFromCarCrash = 172;
	public static final short ClimateManagerPacket = 200;
	public static final short IsoRegionServerPacket = 201;
	public static final short IsoRegionClientRequestFullUpdate = 202;
	public static final short InitPlayerChat = 182;
	public static final short PlayerJoinChat = 183;
	public static final short PlayerLeaveChat = 184;
	public static final short ChatMessageFromPlayer = 185;
	public static final short ChatMessageToPlayer = 186;
	public static final short PlayerStartPMChat = 187;
	public static final short PlayerCloseChat = 188;
	public static final short AddChatTab = 189;
	public static final short RemoveChatTab = 190;
	public static final short PlayerConnectedToChat = 191;
	public static final short PlayerNotFound = 192;
	public static final short PacketTypeShort = 127;
	public static final short SteamGeneric_ProfileName = 0;
	public static final short ContainerDeadBody = 0;
	public static final short ContainerWorldObject = 1;
	public static final short ContainerObject = 2;
	public static final short ContainerVehicle = 3;
	private static HashMap packetTypeNames = new HashMap();

	public static void doPacket(short short1, ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putByte((byte)-122);
		byteBufferWriter.putShort(short1);
	}

	public static void doPingPacket(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putInt(28);
	}

	public static String packetTypeToString(short short1) {
		return packetTypeNames != null ? (String)packetTypeNames.get(short1) : "<wrong>";
	}

	static  {
	Field[] var0 = PacketTypes.class.getFields();
	Field[] var1 = var0;
	int var2 = var0.length;
	for (int var3 = 0; var3 < var2; ++var3) {
		Field var4 = var1[var3];
		if (var4.getType().equals(Short.TYPE) && Modifier.isStatic(var4.getModifiers())) {
			try {
				packetTypeNames.put(var4.getShort((Object)null), var4.getName());
			} catch (IllegalAccessException var6) {
				var6.printStackTrace();
			}
		}
	}
	}
}
