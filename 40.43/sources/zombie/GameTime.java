package zombie;

import fmod.javafmod;
import fmod.fmod.FMODManager;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorGroup;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.erosion.ErosionMain;
import zombie.iso.IsoChunk;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoRoomLight;
import zombie.iso.IsoWorld;
import zombie.iso.SliceY;
import zombie.iso.objects.RainManager;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.radio.ZomboidRadio;
import zombie.ui.UIManager;


public class GameTime {
	public String millingtune = "longambient.ogg";
	public String zombieTune = "tune4.ogg";
	public static GameTime instance = new GameTime();
	private boolean lightUpdated = false;
	public int HelicopterTime1Start = 0;
	public int HelicopterTime1End = 0;
	public int HelicopterDay1 = 0;
	public float HelicopterTime2Start = 0.0F;
	public float HelicopterTime2End = 0.0F;
	public float HelicopterDay2 = 0.0F;
	private KahluaTable table = null;
	private int minutesMod = -1;
	private boolean thunderDay = true;
	private boolean randomAmbientToday = true;
	public float Ambient = 0.9F;
	public float AmbientMax = 1.0F;
	public float AmbientMin = 0.24F;
	public int Day = 22;
	public int StartDay = 22;
	public float MaxZombieCountStart = 750.0F;
	public float MinZombieCountStart = 750.0F;
	public float MaxZombieCount = 750.0F;
	public float MinZombieCount = 750.0F;
	public int Month = 7;
	public int StartMonth = 7;
	public float NightTint = 0.0F;
	public float TimeOfDay = 9.0F;
	public float StartTimeOfDay = 9.0F;
	public float ViewDist = 10.0F;
	public float ViewDistMax = 42.0F;
	public float ViewDistMin = 19.0F;
	public int Year = 2012;
	public int StartYear = 2012;
	public int NightsSurvived = 0;
	public double HoursSurvived = 0.0;
	public GregorianCalendar Calender;
	public float MinutesPerDayStart = 30.0F;
	public float MinutesPerDay;
	public float SleepMultiplier;
	public float LastTimeOfDay;
	public int TargetZombies;
	public int LastCookMinute;
	public boolean RainingToday;
	private float Multiplier;
	public float FPSMultiplier;
	private int dusk;
	private int dawn;
	private static long serverTimeShift = 0L;
	private static long serverTimeShiftLast = 0L;
	private static boolean serverTimeShiftIsSet = false;
	private static long serverTimeuQality = 10000000L;
	public static long serverTimePing = 100000000L;
	private static boolean isUTest = false;
	public boolean bGunFireEventToday;
	public float[] GunFireTimes;
	public int NumGunFireEvents;
	int timeSinceLastMusicChange;
	int timeToMusicChange;
	public long TicksSinceStart;
	private float Night;
	private float NightMin;
	private float NightMax;
	public float Moon;
	public long lastPing;
	public long lastClockSync;
	public float ServerTimeOfDay;
	public float ServerLastTimeOfDay;
	public int ServerNewDays;
	public long timestampForDbUpdate;
	private long minutesStamp;
	private GameTime.ThunderStorm thunderStorm;
	public float lightSourceUpdate;
	public float multiplierBias;
	public float LastLastTimeOfDay;
	public float FPSMultiplier2;
	private int berriesCreated;

	public GameTime() {
		this.MinutesPerDay = this.MinutesPerDayStart;
		this.SleepMultiplier = 1.0F;
		this.TargetZombies = (int)this.MinZombieCountStart;
		this.LastCookMinute = 0;
		this.RainingToday = true;
		this.Multiplier = 1.0F;
		this.FPSMultiplier = 1.1F;
		this.dusk = 3;
		this.dawn = 12;
		this.bGunFireEventToday = false;
		this.GunFireTimes = new float[5];
		this.NumGunFireEvents = 1;
		this.timeSinceLastMusicChange = 0;
		this.timeToMusicChange = 7200;
		this.TicksSinceStart = 0L;
		this.NightMin = 0.0F;
		this.NightMax = 1.0F;
		this.Moon = 0.0F;
		this.lastPing = 0L;
		this.lastClockSync = 0L;
		this.timestampForDbUpdate = 0L;
		this.minutesStamp = 0L;
		this.thunderStorm = new GameTime.ThunderStorm();
		this.lightSourceUpdate = 0.0F;
		this.multiplierBias = 1.0F;
		this.LastLastTimeOfDay = 0.0F;
		this.FPSMultiplier2 = 0.0F;
		this.berriesCreated = 0;
	}

	public static GameTime getInstance() {
		return instance;
	}

	public static void setInstance(GameTime gameTime) {
		instance = gameTime;
	}

	public float getRealworldSecondsSinceLastUpdate() {
		return 0.016666668F * this.FPSMultiplier;
	}

	public float getMultipliedSecondsSinceLastUpdate() {
		return 0.016666668F * this.getUnmoddedMultiplier();
	}

	public float getGameWorldSecondsSinceLastUpdate() {
		float float1 = 1440.0F / this.getMinutesPerDay();
		return 0.016666668F * this.getMultiplier() * float1;
	}

	public static void syncServerTime(long long1, long long2, long long3) {
		long long4 = long3 - long1;
		long long5 = long2 - long3 + long4 / 2L;
		serverTimeShiftLast = serverTimeShift;
		if (!serverTimeShiftIsSet) {
			serverTimeShift = long5;
		} else {
			serverTimeShift = (long)((float)serverTimeShift + (float)(long5 - serverTimeShift) * 0.05F);
		}

		if (Math.abs(serverTimeShift - serverTimeShiftLast) > serverTimeuQality) {
			sendTimeSync();
		} else {
			serverTimeShiftIsSet = true;
		}
	}

	public static long getServerTime() {
		if (isUTest) {
			return System.nanoTime() + serverTimeShift;
		} else if (GameServer.bServer) {
			return System.nanoTime();
		} else if (GameClient.bClient) {
			return !serverTimeShiftIsSet ? 0L : System.nanoTime() + serverTimeShift;
		} else {
			return 0L;
		}
	}

	public static boolean getServerTimeShiftIsSet() {
		return serverTimeShiftIsSet;
	}

	public static void setServerTimeShift(long long1) {
		isUTest = true;
		serverTimeShift = long1;
		serverTimeShiftIsSet = true;
	}

	public static void sendTimeSync() {
		ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
		PacketTypes.doPacket((short)160, byteBufferWriter);
		long long1 = System.nanoTime();
		byteBufferWriter.putLong(long1);
		byteBufferWriter.putLong(0L);
		GameClient.connection.endPacket();
	}

	public static void receiveTimeSync(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		long long1;
		long long2;
		if (GameServer.bServer) {
			long1 = byteBuffer.getLong();
			long2 = System.nanoTime();
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)160, byteBufferWriter);
			byteBufferWriter.putLong(long1);
			byteBufferWriter.putLong(long2);
			udpConnection.endPacketImmediate();
		}

		if (GameClient.bClient) {
			long1 = byteBuffer.getLong();
			long2 = byteBuffer.getLong();
			long long3 = System.nanoTime();
			syncServerTime(long1, long2, long3);
		}
	}

	public int daysInMonth(int int1, int int2) {
		if (this.Calender == null) {
			this.updateCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), this.getMinutes());
		}

		int[] intArray = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		intArray[1] += this.getCalender().isLeapYear(int1) ? 1 : 0;
		return intArray[int2];
	}

	public String getDeathString(IsoPlayer player) {
		return Translator.getText("IGUI_Gametime_SurvivedFor", this.getTimeSurvived(player));
	}

	public int getDaysSurvived() {
		float float1 = 0.0F;
		int int1;
		for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoPlayer player = IsoPlayer.players[int1];
			if (player != null) {
				float1 = Math.max(float1, (float)player.getHoursSurvived());
			}
		}

		int1 = (int)float1 / 24;
		int1 %= 30;
		return int1;
	}

	public String getTimeSurvived(IsoPlayer player) {
		String string = "";
		float float1 = (float)player.getHoursSurvived();
		Integer integer = (int)float1 % 24;
		Integer integer2 = (int)float1 / 24;
		Integer integer3 = integer2 / 30;
		integer2 = integer2 % 30;
		Integer integer4 = integer3 / 12;
		integer3 = integer3 % 12;
		String string2 = Translator.getText("IGUI_Gametime_day");
		String string3 = Translator.getText("IGUI_Gametime_year");
		String string4 = Translator.getText("IGUI_Gametime_hour");
		String string5 = Translator.getText("IGUI_Gametime_month");
		if (integer4 != 0) {
			if (integer4 > 1) {
				string3 = Translator.getText("IGUI_Gametime_years");
			}

			string = string + integer4 + " " + string3;
		}

		if (integer3 != 0) {
			if (integer3 > 1) {
				string5 = Translator.getText("IGUI_Gametime_months");
			}

			if (string.length() > 0) {
				string = string + ", ";
			}

			string = string + integer3 + " " + string5;
		}

		if (integer2 != 0) {
			if (integer2 > 1) {
				string2 = Translator.getText("IGUI_Gametime_days");
			}

			if (string.length() > 0) {
				string = string + ", ";
			}

			string = string + integer2 + " " + string2;
		}

		if (integer != 0) {
			if (integer > 1) {
				string4 = Translator.getText("IGUI_Gametime_hours");
			}

			if (string.length() > 0) {
				string = string + ", ";
			}

			string = string + integer + " " + string4;
		}

		if (string.trim().length() == 0) {
			int int1 = (int)(float1 * 60.0F);
			int int2 = (int)(float1 * 60.0F * 60.0F) - int1 * 60;
			string = "" + int1 + " " + Translator.getText("IGUI_Gametime_minutes") + ", " + int2 + " " + Translator.getText("IGUI_Gametime_secondes");
		}

		return string;
	}

	public String getZombieKilledText(IsoPlayer player) {
		int int1 = player.getZombieKills();
		if (int1 != 0 && int1 <= 1) {
			return int1 == 1 ? Translator.getText("IGUI_Gametime_zombieCount", int1) : null;
		} else {
			return Translator.getText("IGUI_Gametime_zombiesCount", int1);
		}
	}

	public String getGameModeText() {
		String string = Translator.getText("IGUI_Gametime_" + Core.GameMode);
		if (string.startsWith("IGUI_")) {
			string = Core.GameMode;
		}

		String string2 = Translator.getText("IGUI_Gametime_GameMode", string);
		if (string2.startsWith("IGUI")) {
			string2 = "Game mode: " + string;
		}

		if (Core.bDebug) {
			string2 = string2 + " debug";
		}

		return string2;
	}

	public void init() {
		this.setDay(this.getStartDay());
		this.setTimeOfDay(this.getStartTimeOfDay());
		this.setMonth(this.getStartMonth());
		this.setYear(this.getStartYear());
		this.setMaxZombieCount(this.getMaxZombieCountStart());
		this.setMinZombieCount(this.getMinZombieCountStart());
		this.setTargetZombies((int)this.getMinZombieCountStart());
		if (SandboxOptions.instance.Helicopter.getValue() != 1) {
			this.HelicopterDay1 = Rand.Next(6, 10);
			this.HelicopterTime1Start = Rand.Next(9, 19);
			this.HelicopterTime1End = this.HelicopterTime1Start + Rand.Next(4) + 1;
		}

		if (IsoPlayer.DemoMode) {
			this.setTimeOfDay(11.0F);
		}

		this.setMinutesStamp();
	}

	public float Lerp(float float1, float float2, float float3) {
		if (float3 < 0.0F) {
			float3 = 0.0F;
		}

		if (float3 >= 1.0F) {
			float3 = 1.0F;
		}

		float float4 = float2 - float1;
		float float5 = float4 * float3;
		return float1 + float5;
	}

	public void RemoveZombies(int int1) {
	}

	public void RemoveZombiesIndiscriminate(int int1) {
		if (int1 != 0) {
			for (int int2 = 0; int2 < IsoWorld.instance.CurrentCell.getZombieList().size(); ++int2) {
				IsoZombie zombie = (IsoZombie)IsoWorld.instance.CurrentCell.getZombieList().get(0);
				IsoWorld.instance.CurrentCell.getZombieList().remove(int2);
				IsoWorld.instance.CurrentCell.getRemoveList().add(zombie);
				zombie.getCurrentSquare().getMovingObjects().remove(zombie);
				--int2;
				--int1;
				if (int1 == 0 || IsoWorld.instance.CurrentCell.getZombieList().isEmpty()) {
					return;
				}
			}
		}
	}

	public float TimeLerp(float float1, float float2, float float3, float float4) {
		float float5 = getInstance().getTimeOfDay();
		if (float4 < float3) {
			float4 += 24.0F;
		}

		boolean boolean1 = false;
		float float6;
		if (float5 > float4 && float5 > float3 || float5 < float4 && float5 < float3) {
			float3 += 24.0F;
			boolean1 = true;
			float6 = float3;
			float3 = float4;
			float4 = float6;
			if (float5 < float3) {
				float5 += 24.0F;
			}
		}

		float6 = float4 - float3;
		float float7 = float5 - float3;
		float float8 = 0.0F;
		if (float7 > float6) {
			float8 = 1.0F;
		}

		if (float7 < float6 && float7 > 0.0F) {
			float8 = float7 / float6;
		}

		float float9;
		if (GameClient.bClient) {
			float9 = (float)ServerOptions.instance.nightlengthmodifier.getValue();
			float8 *= float9;
		}

		if (boolean1) {
			float8 = 1.0F - float8;
		}

		float9 = 0.0F;
		float8 = (float8 - 0.5F) * 2.0F;
		if ((double)float8 < 0.0) {
			float9 = -1.0F;
		} else {
			float9 = 1.0F;
		}

		float8 = Math.abs(float8);
		float8 = 1.0F - float8;
		float8 = (float)Math.pow((double)float8, 8.0);
		float8 = 1.0F - float8;
		float8 *= float9;
		float8 = float8 * 0.5F + 0.5F;
		return this.Lerp(float1, float2, float8);
	}

	public float TimeLerpCompressed(float float1, float float2, float float3, float float4) {
		float float5 = getInstance().getTimeOfDay();
		if (float4 < float3) {
			float4 += 24.0F;
		}

		boolean boolean1 = false;
		float float6;
		if (float5 > float4 && float5 > float3 || float5 < float4 && float5 < float3) {
			float3 += 24.0F;
			boolean1 = true;
			float6 = float3;
			float3 = float4;
			float4 = float6;
			if (float5 < float3) {
				float5 += 24.0F;
			}
		}

		float6 = float4 - float3;
		float float7 = float5 - float3;
		float float8 = 0.0F;
		if (float7 > float6) {
			float8 = 1.0F;
		}

		if (float7 < float6 && float7 > 0.0F) {
			float8 = float7 / float6;
		}

		if (boolean1) {
			float8 = 1.0F - float8;
		}

		float float9 = 0.0F;
		float8 = (float8 - 0.5F) * 2.0F;
		if ((double)float8 < 0.0) {
			float9 = -1.0F;
		} else {
			float9 = 1.0F;
		}

		float8 = Math.abs(float8);
		float8 = 1.0F - float8;
		float8 = (float)Math.pow((double)float8, 8.0);
		float8 = 1.0F - float8;
		float8 *= float9;
		float8 = float8 * 0.5F + 0.5F;
		float8 = float8 * float8 * float8 * float8;
		return this.Lerp(float1, float2, float8);
	}

	public float getDeltaMinutesPerDay() {
		return this.MinutesPerDayStart / this.MinutesPerDay;
	}

	public float getNightMin() {
		return 1.0F - this.NightMin;
	}

	public float getNightMax() {
		return 1.0F - this.NightMax;
	}

	public void setNightMax(float float1) {
		this.NightMax = 1.0F - float1;
	}

	public void setNightMin(float float1) {
		this.NightMin = 1.0F - float1;
	}

	public int getMinutes() {
		return (int)((this.getTimeOfDay() - (float)((int)this.getTimeOfDay())) * 60.0F);
	}

	public void setMoon(float float1) {
		this.Moon = float1;
	}

	public void update(boolean boolean1) {
		long long1 = System.currentTimeMillis();
		if (GameClient.bClient && (this.lastPing == 0L || long1 - this.lastPing > (long)(ServerOptions.instance.PingFrequency.getValue() * 1000))) {
			sendTimeSync();
			this.lastPing = long1;
		}

		short short1 = 9000;
		if (SandboxOptions.instance.MetaEvent.getValue() == 1) {
			short1 = -1;
		}

		if (SandboxOptions.instance.MetaEvent.getValue() == 3) {
			short1 = 6000;
		}

		if (!GameClient.bClient && this.randomAmbientToday && short1 != -1 && Rand.Next(Rand.AdjustForFramerate(short1)) == 0 && (UIManager.getSpeedControls() == null || UIManager.getSpeedControls().getCurrentGameSpeed() != 0)) {
			AmbientStreamManager.instance.addRandomAmbient();
			if (SandboxOptions.instance.MetaEvent.getValue() == 3 && Rand.Next(3) == 0) {
				this.randomAmbientToday = true;
			} else {
				this.randomAmbientToday = false;
			}
		}

		if (GameServer.bServer && UIManager.getSpeedControls() != null) {
			UIManager.getSpeedControls().SetCurrentGameSpeed(1);
		}

		Core.getInstance();
		int int1;
		if (GameServer.bServer || !GameClient.bClient) {
			if (this.bGunFireEventToday) {
				for (int1 = 0; int1 < this.NumGunFireEvents; ++int1) {
					if (this.TimeOfDay > this.GunFireTimes[int1] && this.LastLastTimeOfDay < this.GunFireTimes[int1]) {
						AmbientStreamManager.instance.doGunEvent();
					}
				}
			}

			if (this.NightsSurvived == this.HelicopterDay1 && this.TimeOfDay > (float)this.HelicopterTime1Start && this.TimeOfDay < (float)this.HelicopterTime1End && !IsoWorld.instance.helicopter.isActive() && Rand.Next((int)(800.0F * this.getInvMultiplier())) == 0) {
				this.HelicopterTime1Start = (int)((float)this.HelicopterTime1Start + 0.5F);
				IsoWorld.instance.helicopter.pickRandomTarget();
			}

			if (this.NightsSurvived > this.HelicopterDay1 && (SandboxOptions.instance.Helicopter.getValue() == 3 || SandboxOptions.instance.Helicopter.getValue() == 4)) {
				if (SandboxOptions.instance.Helicopter.getValue() == 3) {
					this.HelicopterDay1 = this.NightsSurvived + Rand.Next(10, 16);
				}

				if (SandboxOptions.instance.Helicopter.getValue() == 4) {
					this.HelicopterDay1 = this.NightsSurvived + Rand.Next(6, 10);
				}

				this.HelicopterTime1Start = Rand.Next(9, 19);
				this.HelicopterTime1End = this.HelicopterTime1Start + Rand.Next(4) + 1;
			}
		}

		++this.TicksSinceStart;
		int1 = this.getHour();
		this.updateCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), (int)((this.getTimeOfDay() - (float)((int)this.getTimeOfDay())) * 60.0F));
		if (Rand.Next(300) == 0 && this.timeSinceLastMusicChange > this.timeToMusicChange) {
			this.timeSinceLastMusicChange = 0;
			if (Rand.Next(3) == 0) {
			}
		}

		float float1 = this.getTimeOfDay();
		float float2;
		int int2;
		if (UIManager.getSpeedControls() == null || UIManager.getSpeedControls() != null && UIManager.getSpeedControls().getCurrentGameSpeed() > 0) {
			float2 = 1.0F / this.getMinutesPerDay() / 60.0F * this.getMultiplier() / 2.0F;
			if (Core.bLastStand) {
				float2 = 1.0F / this.getMinutesPerDay() / 60.0F * this.getUnmoddedMultiplier() / 2.0F;
			}

			this.setTimeOfDay(this.getTimeOfDay() + float2);
			if (this.getHour() != int1) {
				LuaEventManager.triggerEvent("EveryHours");
			}

			IsoPlayer player;
			if (!GameServer.bServer) {
				for (int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
					player = IsoPlayer.players[int2];
					if (player != null && player.isAlive()) {
						player.setHoursSurvived(player.getHoursSurvived() + (double)float2);
					}
				}
			}

			IsoPlayer player2;
			ArrayList arrayList;
			int int3;
			if (GameServer.bServer) {
				arrayList = GameClient.instance.getPlayers();
				for (int3 = 0; int3 < arrayList.size(); ++int3) {
					player2 = (IsoPlayer)arrayList.get(int3);
					player2.setHoursSurvived(player2.getHoursSurvived() + (double)float2);
				}
			}

			if (GameClient.bClient) {
				arrayList = GameClient.instance.getPlayers();
				for (int3 = 0; int3 < arrayList.size(); ++int3) {
					player2 = (IsoPlayer)arrayList.get(int3);
					if (player2 != null && !player2.isDead() && !player2.isLocalPlayer()) {
						player2.setHoursSurvived(player2.getHoursSurvived() + (double)float2);
					}
				}
			}

			for (int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
				player = IsoPlayer.players[int2];
				if (player != null) {
					if (player.isAsleep()) {
						player.setAsleepTime(player.getAsleepTime() + float2);
						SleepingEvent.instance.update(player);
					} else {
						player.setAsleepTime(0.0F);
					}
				}
			}
		}

		if (!GameClient.bClient && float1 <= 7.0F && this.getTimeOfDay() > 7.0F) {
			this.setNightsSurvived(this.getNightsSurvived() + 1);
			this.doMetaEvents();
		}

		if (GameClient.bClient) {
			if (this.getTimeOfDay() >= 24.0F) {
				this.setTimeOfDay(this.getTimeOfDay() - 24.0F);
			}

			while (this.ServerNewDays > 0) {
				--this.ServerNewDays;
				this.setDay(this.getDay() + 1);
				if (this.getDay() >= this.daysInMonth(this.getYear(), this.getMonth())) {
					this.setDay(0);
					this.setMonth(this.getMonth() + 1);
					if (this.getMonth() >= 12) {
						this.setMonth(0);
						this.setYear(this.getYear() + 1);
					}
				}

				this.setCalender(new GregorianCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), this.getMinutes()));
				LuaEventManager.triggerEvent("EveryDays");
			}
		} else if (this.getTimeOfDay() >= 24.0F) {
			this.setTimeOfDay(this.getTimeOfDay() - 24.0F);
			this.setDay(this.getDay() + 1);
			if (this.getMaxZombieCount() < 1000.0F) {
			}

			if (this.getDay() >= this.daysInMonth(this.getYear(), this.getMonth())) {
				this.setDay(0);
				this.setMonth(this.getMonth() + 1);
				if (this.getMonth() >= 12) {
					this.setMonth(0);
					this.setYear(this.getYear() + 1);
				}
			}

			this.updateCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), this.getMinutes());
			LuaEventManager.triggerEvent("EveryDays");
			if (GameServer.bServer) {
				GameServer.syncClock();
				this.lastClockSync = long1;
			}
		}

		float2 = this.Moon * 20.0F;
		this.thunderStorm.update();
		if (!ClimateManager.getInstance().getThunderStorm().isModifyingNight()) {
			this.setAmbient(this.TimeLerp(this.getAmbientMin(), this.getAmbientMax(), (float)this.getDusk(), (float)this.getDawn()));
		}

		this.setViewDist(this.TimeLerp(this.getViewDistMin() + float2, this.getViewDistMax(), (float)this.getDusk(), (float)this.getDawn()));
		if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
			this.setNightTint(0.0F);
		}

		this.UpdateZombieCount();
		this.setMinutesStamp();
		int2 = (int)((this.getTimeOfDay() - (float)((int)this.getTimeOfDay())) * 60.0F);
		if (int2 / 10 != this.minutesMod) {
			IsoPlayer[] playerArray = IsoPlayer.players;
			for (int int4 = 0; int4 < playerArray.length; ++int4) {
				IsoPlayer player3 = playerArray[int4];
				if (player3 != null) {
					player3.dirtyRecalcGridStackTime = 1.0F;
				}
			}

			ErosionMain.EveryTenMinutes();
			ClimateManager.getInstance().updateEveryTenMins();
			LuaEventManager.triggerEvent("EveryTenMinutes");
			this.minutesMod = int2 / 10;
			ZomboidRadio.getInstance().UpdateScripts(this.getHour(), int2);
		}

		if (GameServer.bServer && (long1 - this.lastClockSync > 10000L || GameServer.bFastForward)) {
			GameServer.syncClock();
			this.lastClockSync = long1;
		}
	}

	private void setMinutesStamp() {
		this.minutesStamp = (long)(this.getWorldAgeHours() * 60.0) + (long)this.getMinutes();
	}

	public long getMinutesStamp() {
		return this.minutesStamp;
	}

	public void thunderStart() {
	}

	public void thunderStart(boolean boolean1) {
	}

	public void thunderStop() {
	}

	public boolean getThunderStorm() {
		return ClimateManager.getInstance().getIsThunderStorming();
	}

	public void lightingUpdate() {
		if (this.updateLightSource(false)) {
			this.lightSourceUpdate = 1000.0F;
		}

		if (this.lightSourceUpdate > 30.0F) {
			this.updateLightSource(true);
			this.lightSourceUpdate = 0.0F;
		}

		++this.lightSourceUpdate;
	}

	private void doMetaEvents() {
		byte byte1 = 3;
		if (SandboxOptions.instance.MetaEvent.getValue() == 1) {
			byte1 = -1;
		}

		if (SandboxOptions.instance.MetaEvent.getValue() == 3) {
			byte1 = 2;
		}

		this.bGunFireEventToday = byte1 != -1 && Rand.Next(byte1) == 0;
		if (this.bGunFireEventToday) {
			this.NumGunFireEvents = 1;
			for (int int1 = 0; int1 < this.NumGunFireEvents; ++int1) {
				this.GunFireTimes[int1] = (float)Rand.Next(18000) / 1000.0F + 7.0F;
			}
		}

		this.randomAmbientToday = true;
	}

	public boolean updateLightSource(boolean boolean1) {
		boolean boolean2 = false;
		if (IsoWorld.instance.CurrentCell == null) {
			return false;
		} else {
			for (int int1 = 0; int1 < IsoWorld.instance.CurrentCell.getLamppostPositions().size(); ++int1) {
				IsoLightSource lightSource = (IsoLightSource)IsoWorld.instance.CurrentCell.getLamppostPositions().get(int1);
				IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunkForGridSquare(lightSource.x, lightSource.y, lightSource.z);
				if (chunk != null && lightSource.chunk != null && lightSource.chunk != chunk) {
					lightSource.life = 0;
				}

				if (lightSource.life > 0) {
					boolean2 = true;
				}

				if (boolean1) {
					lightSource.clearInfluence();
				}

				if (lightSource.life == 0 || !lightSource.isInBounds()) {
					IsoWorld.instance.CurrentCell.getLamppostPositions().remove(int1);
					--int1;
					if (!boolean1) {
						lightSource.clearInfluence();
						boolean2 = true;
					}
				}
			}

			ArrayList arrayList = IsoWorld.instance.CurrentCell.roomLights;
			int int2;
			IsoRoomLight roomLight;
			for (int2 = 0; int2 < arrayList.size(); ++int2) {
				roomLight = (IsoRoomLight)arrayList.get(int2);
				if (boolean1) {
					roomLight.clearInfluence();
				}

				if (!roomLight.isInBounds()) {
					arrayList.remove(int2--);
					if (!boolean1) {
						roomLight.clearInfluence();
						boolean2 = true;
					}
				}
			}

			if (boolean1) {
				for (int2 = 0; int2 < arrayList.size(); ++int2) {
					roomLight = (IsoRoomLight)arrayList.get(int2);
					roomLight.addInfluence();
				}

				for (int2 = 0; int2 < IsoWorld.instance.CurrentCell.getLamppostPositions().size(); ++int2) {
					IsoLightSource lightSource2 = (IsoLightSource)IsoWorld.instance.CurrentCell.getLamppostPositions().get(int2);
					lightSource2.update();
				}
			}

			return boolean2;
		}
	}

	public void UpdateZombieCount() {
	}

	private void AddZombies(int int1) {
		if (int1 != 0) {
			;
		}
	}

	@Deprecated
	public float getAmbient() {
		return ClimateManager.getInstance().getAmbient();
	}

	public void setAmbient(float float1) {
		this.Ambient = float1;
	}

	public float getAmbientMax() {
		return this.AmbientMax;
	}

	public void setAmbientMax(float float1) {
		float1 = Math.min(1.0F, float1);
		float1 = Math.max(0.0F, float1);
		this.AmbientMax = float1;
	}

	public float getAmbientMin() {
		return this.AmbientMin;
	}

	public void setAmbientMin(float float1) {
		float1 = Math.min(1.0F, float1);
		float1 = Math.max(0.0F, float1);
		this.AmbientMin = float1;
	}

	public int getDay() {
		return this.Day;
	}

	public void setDay(int int1) {
		this.Day = int1;
	}

	public int getStartDay() {
		return this.StartDay;
	}

	public void setStartDay(int int1) {
		this.StartDay = int1;
	}

	public float getMaxZombieCountStart() {
		return 0.0F;
	}

	public void setMaxZombieCountStart(float float1) {
		this.MaxZombieCountStart = float1;
	}

	public float getMinZombieCountStart() {
		return 0.0F;
	}

	public void setMinZombieCountStart(float float1) {
		this.MinZombieCountStart = float1;
	}

	public float getMaxZombieCount() {
		return this.MaxZombieCount;
	}

	public void setMaxZombieCount(float float1) {
		this.MaxZombieCount = float1;
	}

	public float getMinZombieCount() {
		return this.MinZombieCount;
	}

	public void setMinZombieCount(float float1) {
		this.MinZombieCount = float1;
	}

	public int getMonth() {
		return this.Month;
	}

	public void setMonth(int int1) {
		this.Month = int1;
	}

	public int getStartMonth() {
		return this.StartMonth;
	}

	public void setStartMonth(int int1) {
		this.StartMonth = int1;
	}

	public float getNightTint() {
		return ClimateManager.getInstance().getNightStrength();
	}

	public void setNightTint(float float1) {
	}

	public float getNight() {
		return ClimateManager.getInstance().getNightStrength();
	}

	public void setNight(float float1) {
	}

	public float getTimeOfDay() {
		return this.TimeOfDay;
	}

	public void setTimeOfDay(float float1) {
		this.TimeOfDay = float1;
	}

	public float getStartTimeOfDay() {
		return this.StartTimeOfDay;
	}

	public void setStartTimeOfDay(float float1) {
		this.StartTimeOfDay = float1;
	}

	public float getViewDist() {
		return ClimateManager.getInstance().getViewDistance();
	}

	public void setViewDist(float float1) {
		this.ViewDist = float1;
	}

	public float getViewDistMax() {
		return this.ViewDistMax;
	}

	public void setViewDistMax(float float1) {
		this.ViewDistMax = float1;
	}

	public float getViewDistMin() {
		return this.ViewDistMin;
	}

	public void setViewDistMin(float float1) {
		this.ViewDistMin = float1;
	}

	public int getYear() {
		return this.Year;
	}

	public void setYear(int int1) {
		this.Year = int1;
	}

	public int getStartYear() {
		return this.StartYear;
	}

	public void setStartYear(int int1) {
		this.StartYear = int1;
	}

	public int getNightsSurvived() {
		return this.NightsSurvived;
	}

	public void setNightsSurvived(int int1) {
		this.NightsSurvived = int1;
	}

	public double getWorldAgeHours() {
		float float1 = (float)(this.getNightsSurvived() * 24);
		if (this.getTimeOfDay() >= 7.0F) {
			float1 += this.getTimeOfDay() - 7.0F;
		} else {
			float1 += this.getTimeOfDay() + 17.0F;
		}

		return (double)float1;
	}

	public double getHoursSurvived() {
		DebugLog.log("GameTime.getHoursSurvived() has no meaning, use IsoPlayer.getHourSurvived() instead");
		return this.HoursSurvived;
	}

	public int getHour() {
		double double1 = Math.floor((double)(this.getTimeOfDay() * 3600.0F));
		return (int)Math.floor(double1 / 3600.0);
	}

	public void setHoursSurvived(double double1) {
		DebugLog.log("GameTime.getHoursSurvived() has no meaning, use IsoPlayer.getHourSurvived() instead");
		this.HoursSurvived = double1;
	}

	public GregorianCalendar getCalender() {
		this.updateCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), (int)((this.getTimeOfDay() - (float)((int)this.getTimeOfDay())) * 60.0F));
		return this.Calender;
	}

	public void setCalender(GregorianCalendar gregorianCalendar) {
		this.Calender = gregorianCalendar;
	}

	public void updateCalendar(int int1, int int2, int int3, int int4, int int5) {
		if (this.Calender == null) {
			this.Calender = new GregorianCalendar();
		}

		this.Calender.set(int1, int2, int3, int4, int5);
	}

	public float getMinutesPerDay() {
		return this.MinutesPerDay;
	}

	public void setMinutesPerDay(float float1) {
		this.MinutesPerDay = float1;
	}

	public float getSleepMultiplier() {
		return this.SleepMultiplier;
	}

	public void setSleepMultiplier(float float1) {
		this.SleepMultiplier = float1;
	}

	public float getLastTimeOfDay() {
		return this.LastTimeOfDay;
	}

	public void setLastTimeOfDay(float float1) {
		this.LastTimeOfDay = float1;
	}

	public int getTargetZombies() {
		return (int)this.getMinZombieCountStart();
	}

	public void setTargetZombies(int int1) {
		this.TargetZombies = int1;
	}

	public int getLastCookMinute() {
		return this.LastCookMinute;
	}

	public void setLastCookMinute(int int1) {
		this.LastCookMinute = int1;
	}

	public boolean isRainingToday() {
		return this.RainingToday;
	}

	public void setRainingToday(boolean boolean1) {
		this.RainingToday = boolean1;
	}

	public float getMultiplier() {
		if (GameServer.bServer) {
			this.Multiplier = 1.0F;
		}

		if (GameServer.bServer) {
			this.Multiplier = GameServer.bFastForward ? new Float(ServerOptions.instance.FastForwardMultiplier.getValue()) / this.getDeltaMinutesPerDay() : 1.0F;
		} else if (GameClient.bClient) {
			this.Multiplier = GameClient.bFastForward ? new Float(ServerOptions.instance.FastForwardMultiplier.getValue()) / this.getDeltaMinutesPerDay() : 1.0F;
		}

		float float1 = this.Multiplier * this.FPSMultiplier;
		float1 *= 0.5F;
		if (!GameServer.bServer && !GameClient.bClient && IsoPlayer.instance != null && IsoPlayer.allPlayersAsleep()) {
			return 200.0F * (30.0F / (float)PerformanceSettings.LockFPS);
		} else {
			float1 *= 1.6F;
			float1 *= this.multiplierBias;
			return float1;
		}
	}

	public float getServerMultiplier() {
		float float1 = 10.0F / GameWindow.averageFPS / (float)(PerformanceSettings.ManualFrameSkips + 1);
		float float2 = this.Multiplier * float1;
		float2 *= 0.5F;
		if (!GameServer.bServer && !GameClient.bClient && IsoPlayer.instance != null && IsoPlayer.allPlayersAsleep()) {
			return 200.0F * (30.0F / (float)PerformanceSettings.LockFPS);
		} else {
			float2 *= 1.6F;
			float2 *= this.multiplierBias;
			return float2;
		}
	}

	public float getUnmoddedMultiplier() {
		float float1 = this.Multiplier * this.FPSMultiplier;
		return !GameServer.bServer && !GameClient.bClient && IsoPlayer.instance != null && IsoPlayer.allPlayersAsleep() ? 200.0F * (30.0F / (float)PerformanceSettings.LockFPS) : float1;
	}

	public float getInvMultiplier() {
		float float1 = this.getMultiplier();
		return 1.0F / float1;
	}

	public float getTrueMultiplier() {
		float float1 = this.Multiplier;
		return float1;
	}

	public void setMultiplier(float float1) {
		this.Multiplier = float1;
	}

	public void save() {
		File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_t.bin");
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();
			return;
		}

		DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(fileOutputStream));
		try {
			instance.save(dataOutputStream);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		try {
			dataOutputStream.flush();
			dataOutputStream.close();
		} catch (IOException ioException2) {
			ioException2.printStackTrace();
		}
	}

	public void save(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.writeByte(71);
		dataOutputStream.writeByte(77);
		dataOutputStream.writeByte(84);
		dataOutputStream.writeByte(77);
		dataOutputStream.writeInt(143);
		dataOutputStream.writeFloat(this.Multiplier);
		dataOutputStream.writeInt(this.NightsSurvived);
		dataOutputStream.writeInt(this.TargetZombies);
		dataOutputStream.writeFloat(this.LastTimeOfDay);
		dataOutputStream.writeFloat(this.TimeOfDay);
		dataOutputStream.writeInt(this.Day);
		dataOutputStream.writeInt(this.Month);
		dataOutputStream.writeInt(this.Year);
		dataOutputStream.writeFloat(0.0F);
		dataOutputStream.writeFloat(0.0F);
		dataOutputStream.writeInt(IsoWorld.instance.Groups.size());
		for (int int1 = 0; int1 < IsoWorld.instance.Groups.size(); ++int1) {
			SurvivorGroup survivorGroup = (SurvivorGroup)IsoWorld.instance.Groups.get(int1);
			if (survivorGroup == null || survivorGroup.Leader == null) {
				boolean boolean1 = false;
			}

			dataOutputStream.writeInt(survivorGroup.Leader.getID());
			dataOutputStream.writeInt(survivorGroup.Members.size());
			for (int int2 = 0; int2 < survivorGroup.Members.size(); ++int2) {
				dataOutputStream.writeInt(((SurvivorDesc)survivorGroup.Members.get(int2)).getID());
			}
		}

		if (this.table != null) {
			dataOutputStream.writeByte(1);
			this.table.save(dataOutputStream);
		} else {
			dataOutputStream.writeByte(0);
		}

		GameWindow.WriteString(dataOutputStream, Core.getInstance().getPoisonousBerry());
		GameWindow.WriteString(dataOutputStream, Core.getInstance().getPoisonousMushroom());
		dataOutputStream.writeInt(this.HelicopterDay1);
		dataOutputStream.writeInt(this.HelicopterTime1Start);
		dataOutputStream.writeInt(this.HelicopterTime1End);
		ClimateManager.getInstance().save(dataOutputStream);
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.putFloat(this.Multiplier);
		byteBuffer.putInt(this.NightsSurvived);
		byteBuffer.putInt(this.TargetZombies);
		byteBuffer.putFloat(this.LastTimeOfDay);
		byteBuffer.putFloat(this.TimeOfDay);
		byteBuffer.putInt(this.Day);
		byteBuffer.putInt(this.Month);
		byteBuffer.putInt(this.Year);
		byteBuffer.putFloat(0.0F);
		byteBuffer.putFloat(0.0F);
		byteBuffer.putInt(IsoWorld.instance.Groups.size());
		for (int int1 = 0; int1 < IsoWorld.instance.Groups.size(); ++int1) {
			SurvivorGroup survivorGroup = (SurvivorGroup)IsoWorld.instance.Groups.get(int1);
			if (survivorGroup == null || survivorGroup.Leader == null) {
				boolean boolean1 = false;
			}

			byteBuffer.putInt(survivorGroup.Leader.getID());
			byteBuffer.putInt(survivorGroup.Members.size());
			for (int int2 = 0; int2 < survivorGroup.Members.size(); ++int2) {
				byteBuffer.putInt(((SurvivorDesc)survivorGroup.Members.get(int2)).getID());
			}
		}

		if (this.table != null) {
			byteBuffer.put((byte)1);
			this.table.save(byteBuffer);
		} else {
			byteBuffer.put((byte)0);
		}
	}

	public void load(DataInputStream dataInputStream) throws IOException {
		int int1 = IsoWorld.SavedWorldVersion;
		if (int1 == -1) {
			int1 = 143;
		}

		dataInputStream.mark(0);
		byte byte1 = dataInputStream.readByte();
		byte byte2 = dataInputStream.readByte();
		byte byte3 = dataInputStream.readByte();
		byte byte4 = dataInputStream.readByte();
		if (byte1 == 71 && byte2 == 77 && byte3 == 84 && byte4 == 77) {
			int1 = dataInputStream.readInt();
		} else {
			dataInputStream.reset();
		}

		this.Multiplier = dataInputStream.readFloat();
		this.NightsSurvived = dataInputStream.readInt();
		this.TargetZombies = dataInputStream.readInt();
		this.LastTimeOfDay = dataInputStream.readFloat();
		this.TimeOfDay = dataInputStream.readFloat();
		this.Day = dataInputStream.readInt();
		this.Month = dataInputStream.readInt();
		this.Year = dataInputStream.readInt();
		dataInputStream.readFloat();
		dataInputStream.readFloat();
		int int2 = dataInputStream.readInt();
		for (int int3 = 0; int3 < int2; ++int3) {
			int int4 = dataInputStream.readInt();
			SurvivorGroup survivorGroup = new SurvivorGroup((SurvivorDesc)IsoWorld.instance.SurvivorDescriptors.get(int4));
			int int5 = dataInputStream.readInt();
			for (int int6 = 0; int6 < int5; ++int6) {
				SurvivorDesc survivorDesc = (SurvivorDesc)IsoWorld.instance.SurvivorDescriptors.get(dataInputStream.readInt());
				if (survivorDesc != null) {
					survivorGroup.addMember(survivorDesc);
				}
			}

			IsoWorld.instance.Groups.add(survivorGroup);
		}

		if (dataInputStream.readByte() == 1) {
			if (this.table == null) {
				this.table = LuaManager.platform.newTable();
			}

			this.table.load(dataInputStream, int1);
		}

		if (int1 >= 74) {
			Core.getInstance().setPoisonousBerry(GameWindow.ReadString(dataInputStream));
			Core.getInstance().setPoisonousMushroom(GameWindow.ReadString(dataInputStream));
		}

		if (int1 >= 90) {
			this.HelicopterDay1 = dataInputStream.readInt();
			this.HelicopterTime1Start = dataInputStream.readInt();
			this.HelicopterTime1End = dataInputStream.readInt();
		}

		if (int1 >= 135) {
			ClimateManager.getInstance().load(dataInputStream, int1);
		}

		this.setMinutesStamp();
	}

	public void load(ByteBuffer byteBuffer) throws IOException {
		short short1 = 143;
		this.Multiplier = byteBuffer.getFloat();
		this.NightsSurvived = byteBuffer.getInt();
		this.TargetZombies = byteBuffer.getInt();
		this.LastTimeOfDay = byteBuffer.getFloat();
		this.TimeOfDay = byteBuffer.getFloat();
		this.Day = byteBuffer.getInt();
		this.Month = byteBuffer.getInt();
		this.Year = byteBuffer.getInt();
		byteBuffer.getFloat();
		byteBuffer.getFloat();
		int int1 = byteBuffer.getInt();
		for (int int2 = 0; int2 < int1; ++int2) {
			int int3 = byteBuffer.getInt();
			SurvivorGroup survivorGroup = new SurvivorGroup((SurvivorDesc)IsoWorld.instance.SurvivorDescriptors.get(int3));
			int int4 = byteBuffer.getInt();
			for (int int5 = 0; int5 < int4; ++int5) {
				SurvivorDesc survivorDesc = (SurvivorDesc)IsoWorld.instance.SurvivorDescriptors.get(byteBuffer.getInt());
				if (survivorDesc != null) {
					survivorGroup.addMember(survivorDesc);
				}
			}

			IsoWorld.instance.Groups.add(survivorGroup);
		}

		if (byteBuffer.get() == 1) {
			if (this.table == null) {
				this.table = LuaManager.platform.newTable();
			}

			this.table.load((ByteBuffer)byteBuffer, short1);
		}

		this.setMinutesStamp();
	}

	public void load() {
		File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_t.bin");
		if (file.exists()) {
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				if (SliceY.SliceBuffer == null) {
					SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
				}

				synchronized (SliceY.SliceBuffer) {
					SliceY.SliceBuffer.rewind();
					fileInputStream.read(SliceY.SliceBuffer.array());
				}

				fileInputStream.close();
				DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(SliceY.SliceBuffer.array()));
				this.load(dataInputStream);
				dataInputStream.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public int getDawn() {
		return this.dawn;
	}

	public void setDawn(int int1) {
		this.dawn = int1;
	}

	public int getDusk() {
		return this.dusk;
	}

	public void setDusk(int int1) {
		this.dusk = int1;
	}

	public KahluaTable getModData() {
		if (this.table == null) {
			this.table = LuaManager.platform.newTable();
		}

		return this.table;
	}

	public boolean isThunderDay() {
		return this.thunderDay;
	}

	public void setThunderDay(boolean boolean1) {
		this.thunderDay = boolean1;
	}

	public void saveToPacket(ByteBuffer byteBuffer) throws IOException {
		KahluaTable kahluaTable = getInstance().getModData();
		Object object = kahluaTable.rawget("camping");
		Object object2 = kahluaTable.rawget("farming");
		Object object3 = kahluaTable.rawget("trapping");
		kahluaTable.rawset("camping", (Object)null);
		kahluaTable.rawset("farming", (Object)null);
		kahluaTable.rawset("trapping", (Object)null);
		this.save(byteBuffer);
		kahluaTable.rawset("camping", object);
		kahluaTable.rawset("farming", object2);
		kahluaTable.rawset("trapping", object3);
	}

	private static class ThunderStormOld {
		GameTime.ThunderStormOld.State state;
		int thunderX;
		int thunderY;
		GameTime.AnimTimer timer;
		boolean bigFirstStrikeSet;
		boolean bigFirstStrike;
		boolean stop;

		private ThunderStormOld() {
			this.state = GameTime.ThunderStormOld.State.NoThunder;
			this.timer = new GameTime.AnimTimer();
		}

		public void noise(String string) {
			if (Core.bDebug || GameServer.bServer && GameServer.bDebug) {
				DebugLog.log("thunder: " + string);
			}
		}

		public void start() {
			this.state = GameTime.ThunderStormOld.State.Start;
			this.bigFirstStrikeSet = false;
		}

		public void start(boolean boolean1) {
			this.state = GameTime.ThunderStormOld.State.Start;
			this.bigFirstStrike = boolean1;
			this.bigFirstStrikeSet = true;
		}

		public void stop() {
			if (this.state != GameTime.ThunderStormOld.State.NoThunder) {
				this.stop = true;
			}
		}

		public void update() {
			GameTime gameTime = GameTime.instance;
			float float1;
			switch (this.state) {
			case NoThunder: 
				if (!GameClient.bClient && gameTime.isThunderDay() && IsoWorld.instance.getWeather().equals("rain") && Rand.Next(Rand.AdjustForFramerate(800)) == 0) {
					if (!GameServer.bServer) {
						this.thunderX = IsoPlayer.instance.getCurrentSquare().getX();
						this.thunderY = IsoPlayer.instance.getCurrentSquare().getY();
					} else {
						if (GameServer.Players.isEmpty()) {
							return;
						}

						ArrayList arrayList = GameServer.getPlayers();
						for (int int1 = arrayList.size() - 1; int1 >= 0; --int1) {
							if (((IsoPlayer)arrayList.get(int1)).getCurrentSquare() == null) {
								arrayList.remove(int1);
							}
						}

						if (arrayList.isEmpty()) {
							return;
						}

						IsoPlayer player = (IsoPlayer)arrayList.get(Rand.Next(arrayList.size()));
						this.thunderX = player.getCurrentSquare().getX();
						this.thunderY = player.getCurrentSquare().getY();
					}

					if (Rand.Next(2) == 0) {
						this.thunderX -= Rand.Next(2000);
					} else {
						this.thunderX += Rand.Next(2000);
					}

					if (Rand.Next(2) == 0) {
						this.thunderY -= Rand.Next(2000);
					} else {
						this.thunderY += Rand.Next(2000);
					}

					this.state = GameTime.ThunderStormOld.State.Start;
				}

				break;
			
			case Start: 
				this.noise("started bigFirstStrikeSet=" + this.bigFirstStrikeSet);
				this.stop = false;
				if (!GameClient.bClient && !this.bigFirstStrikeSet) {
					this.bigFirstStrike = Rand.Next(3) == 0;
				}

				this.bigFirstStrikeSet = false;
				this.timer.init(5);
				this.state = GameTime.ThunderStormOld.State.Lightning1Brighter;
				LuaEventManager.triggerEvent("OnThunderStart");
				if (GameServer.bServer) {
					GameServer.sendServerCommandV("thunder", "start", "big", this.bigFirstStrike);
				}

				break;
			
			case Lightning1Brighter: 
				this.timer.update();
				float1 = gameTime.TimeLerp(gameTime.NightMax, gameTime.NightMin, (float)gameTime.getDusk(), (float)gameTime.getDawn());
				gameTime.setNight(float1 - 0.13F * (float)this.timer.Ticks * this.timer.ratio());
				if (this.timer.finished()) {
					this.noise("first strike maximum");
					if (this.bigFirstStrike) {
						if (GameServer.bServer) {
							this.noise("thunder sound");
							GameServer.PlaySoundAtEveryPlayer("event:/Meta/Thunder/Thunder", this.thunderX, this.thunderY, 100);
						} else if (!GameClient.bClient && !Core.SoundDisabled && !IsoPlayer.instance.isDeaf() && FMODManager.instance.getNumListeners() > 0) {
							this.noise("thunder sound");
							long long1 = javafmod.FMOD_Studio_System_GetEvent("event:/Meta/Thunder/Thunder");
							long long2 = javafmod.FMOD_Studio_System_CreateEventInstance(long1);
							javafmod.FMOD_Studio_EventInstance3D(long2, (float)this.thunderX, (float)this.thunderY, 100.0F);
							javafmod.FMOD_Studio_SetVolume(long2, SoundManager.instance.getSoundVolume());
							javafmod.FMOD_Studio_StartEvent(long2);
							javafmod.FMOD_Studio_ReleaseEventInstance(long2);
						}
					}

					this.timer.init(4);
					this.state = GameTime.ThunderStormOld.State.Lightning1Darker;
				}

				break;
			
			case Lightning1Darker: 
				this.timer.update();
				float1 = gameTime.TimeLerp(gameTime.NightMax, gameTime.NightMin, (float)gameTime.getDusk(), (float)gameTime.getDawn());
				gameTime.setNight(gameTime.getNight() + (float)this.timer.Ticks * 0.08F * this.timer.ratio());
				if (gameTime.getNight() > float1) {
					gameTime.setNight(float1);
				}

				if (this.timer.finished()) {
					this.noise("first strike finished");
					this.timer.init(7);
					this.state = GameTime.ThunderStormOld.State.Lightning2Brighter;
				}

				break;
			
			case Lightning2Brighter: 
				this.timer.update();
				float1 = gameTime.TimeLerp(gameTime.NightMax, gameTime.NightMin, (float)gameTime.getDusk(), (float)gameTime.getDawn());
				gameTime.setNight(float1 - 0.14F * (float)this.timer.Ticks * this.timer.ratio());
				if (this.timer.finished()) {
					this.noise("second strike maximum");
					gameTime.setNight(0.0F);
					this.timer.init(30);
					this.state = GameTime.ThunderStormOld.State.Lightning2Darker;
				}

				break;
			
			case Lightning2Darker: 
				this.timer.update();
				float1 = gameTime.TimeLerp(gameTime.NightMax, gameTime.NightMin, (float)gameTime.getDusk(), (float)gameTime.getDawn());
				gameTime.setNight(float1 * this.timer.ratio());
				if (this.timer.finished()) {
					this.noise("second strike finished");
					if (!this.bigFirstStrike) {
						this.timer.init(120);
						this.state = GameTime.ThunderStormOld.State.Rumble;
					} else {
						this.timer.init(360);
						this.state = GameTime.ThunderStormOld.State.Interval;
					}
				}

				break;
			
			case Rumble: 
				this.timer.update();
				if (this.timer.finished()) {
					if (GameServer.bServer) {
						this.noise("rumble sound");
						GameServer.PlaySoundAtEveryPlayer("event:/Meta/Thunder/RumbleThunder", this.thunderX, this.thunderY, 100);
					} else if (!GameClient.bClient && !Core.SoundDisabled && !IsoPlayer.instance.isDeaf() && FMODManager.instance.getNumListeners() > 0) {
						this.noise("rumble sound");
						long long3 = javafmod.FMOD_Studio_System_GetEvent("event:/Meta/Thunder/RumbleThunder");
						long long4 = javafmod.FMOD_Studio_System_CreateEventInstance(long3);
						javafmod.FMOD_Studio_EventInstance3D(long4, (float)this.thunderX, (float)this.thunderY, 200.0F);
						javafmod.FMOD_Studio_SetVolume(long4, SoundManager.instance.getSoundVolume());
						javafmod.FMOD_Studio_StartEvent(long4);
						javafmod.FMOD_Studio_ReleaseEventInstance(long4);
					}

					this.timer.init(360);
					this.state = GameTime.ThunderStormOld.State.Interval;
				}

				break;
			
			case Interval: 
				if (!GameClient.bClient && !RainManager.isRaining() && !IsoWorld.instance.getWeather().equals("rain") || Rand.Next(Rand.AdjustForFramerate(1200)) == 0) {
					this.stop = true;
				}

				if (this.stop) {
					this.noise("ended");
					LuaEventManager.triggerEvent("OnThunderStop");
					if (GameServer.bServer) {
						GameServer.sendServerCommandV("thunder", "stop");
					}

					this.state = GameTime.ThunderStormOld.State.NoThunder;
				} else if (!GameClient.bClient) {
					if (this.timer.finished()) {
						if (Rand.Next(Rand.AdjustForFramerate(400)) == 0) {
							this.state = GameTime.ThunderStormOld.State.Start;
						}
					} else {
						this.timer.update();
						if (this.timer.finished()) {
							this.noise("interval finished");
						}
					}
				}

			
			}
		}

		boolean isModifyingNight() {
			return this.state == GameTime.ThunderStormOld.State.Lightning1Brighter || this.state == GameTime.ThunderStormOld.State.Lightning1Darker || this.state == GameTime.ThunderStormOld.State.Lightning2Brighter || this.state == GameTime.ThunderStormOld.State.Lightning2Darker;
		}
		static enum State {

			NoThunder,
			Start,
			Lightning1Brighter,
			Lightning1Darker,
			Lightning2Brighter,
			Lightning2Darker,
			Rumble,
			Interval;
		}
	}

	private static class ThunderStorm {

		private ThunderStorm() {
		}

		public void noise(String string) {
			if (Core.bDebug || GameServer.bServer && GameServer.bDebug) {
				DebugLog.log("thunder: " + string);
			}
		}

		public void start(boolean boolean1) {
		}

		public void stop() {
		}

		public void start() {
		}

		public void update() {
		}

		boolean isModifyingNight() {
			return false;
		}

		ThunderStorm(Object object) {
			this();
		}
	}

	public static class AnimTimer {
		public float Elapsed;
		public float Duration;
		public boolean Finished = true;
		public int Ticks;

		public void initSeconds(int int1) {
			this.init(int1 * 60);
		}

		public void init(int int1) {
			this.Ticks = int1;
			this.Elapsed = 0.0F;
			this.Duration = (float)(int1 * 1) / 30.0F;
			this.Finished = false;
		}

		public void update() {
			this.Elapsed += GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F / 30.0F;
			if (this.Elapsed >= this.Duration) {
				this.Elapsed = this.Duration;
				this.Finished = true;
			}
		}

		public float ratio() {
			return this.Elapsed / this.Duration;
		}

		public boolean finished() {
			return this.Finished;
		}
	}
}
