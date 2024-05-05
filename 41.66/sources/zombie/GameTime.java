package zombie;

import java.io.BufferedInputStream;
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
import java.util.concurrent.TimeUnit;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.erosion.ErosionMain;
import zombie.iso.IsoWorld;
import zombie.iso.SliceY;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.radio.ZomboidRadio;
import zombie.ui.SpeedControls;
import zombie.ui.UIManager;
import zombie.util.PZCalendar;


public final class GameTime {
	public static GameTime instance = new GameTime();
	public static final float MULTIPLIER = 0.8F;
	private static long serverTimeShift = 0L;
	private static boolean serverTimeShiftIsSet = false;
	private static boolean isUTest = false;
	public float TimeOfDay = 9.0F;
	public int NightsSurvived = 0;
	public PZCalendar Calender;
	public float FPSMultiplier = 1.0F;
	public float Moon = 0.0F;
	public float ServerTimeOfDay;
	public float ServerLastTimeOfDay;
	public int ServerNewDays;
	public float lightSourceUpdate = 0.0F;
	public float multiplierBias = 1.0F;
	public float LastLastTimeOfDay = 0.0F;
	private int HelicopterTime1Start = 0;
	public float PerObjectMultiplier = 1.0F;
	private int HelicopterTime1End = 0;
	private int HelicopterDay1 = 0;
	private float Ambient = 0.9F;
	private float AmbientMax = 1.0F;
	private float AmbientMin = 0.24F;
	private int Day = 22;
	private int StartDay = 22;
	private float MaxZombieCountStart = 750.0F;
	private float MinZombieCountStart = 750.0F;
	private float MaxZombieCount = 750.0F;
	private float MinZombieCount = 750.0F;
	private int Month = 7;
	private int StartMonth = 7;
	private float StartTimeOfDay = 9.0F;
	private float ViewDistMax = 42.0F;
	private float ViewDistMin = 19.0F;
	private int Year = 2012;
	private int StartYear = 2012;
	private double HoursSurvived = 0.0;
	private float MinutesPerDayStart = 30.0F;
	private float MinutesPerDay;
	private float LastTimeOfDay;
	private int TargetZombies;
	private boolean RainingToday;
	private boolean bGunFireEventToday;
	private float[] GunFireTimes;
	private int NumGunFireEvents;
	private long lastPing;
	private long lastClockSync;
	private KahluaTable table;
	private int minutesMod;
	private boolean thunderDay;
	private boolean randomAmbientToday;
	private float Multiplier;
	private int dusk;
	private int dawn;
	private float NightMin;
	private float NightMax;
	private long minutesStamp;
	private long previousMinuteStamp;

	public GameTime() {
		this.MinutesPerDay = this.MinutesPerDayStart;
		this.TargetZombies = (int)this.MinZombieCountStart;
		this.RainingToday = true;
		this.bGunFireEventToday = false;
		this.GunFireTimes = new float[5];
		this.NumGunFireEvents = 1;
		this.lastPing = 0L;
		this.lastClockSync = 0L;
		this.table = null;
		this.minutesMod = -1;
		this.thunderDay = true;
		this.randomAmbientToday = true;
		this.Multiplier = 1.0F;
		this.dusk = 3;
		this.dawn = 12;
		this.NightMin = 0.0F;
		this.NightMax = 1.0F;
		this.minutesStamp = 0L;
		this.previousMinuteStamp = 0L;
	}

	public static GameTime getInstance() {
		return instance;
	}

	public static void setInstance(GameTime gameTime) {
		instance = gameTime;
	}

	public static void syncServerTime(long long1, long long2, long long3) {
		long long4 = long3 - long1;
		long long5 = long2 - long3 + long4 / 2L;
		long long6 = serverTimeShift;
		if (!serverTimeShiftIsSet) {
			serverTimeShift = long5;
		} else {
			serverTimeShift = (long)((float)serverTimeShift + (float)(long5 - serverTimeShift) * 0.05F);
		}

		long long7 = 10000000L;
		if (Math.abs(serverTimeShift - long6) > long7) {
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

	public static long getServerTimeMills() {
		return TimeUnit.NANOSECONDS.toMillis(getServerTime());
	}

	public static boolean getServerTimeShiftIsSet() {
		return serverTimeShiftIsSet;
	}

	public static void setServerTimeShift(long long1) {
		isUTest = true;
		serverTimeShift = long1;
		serverTimeShiftIsSet = true;
	}

	private static void sendTimeSync() {
		ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
		PacketTypes.PacketType.TimeSync.doPacket(byteBufferWriter);
		long long1 = System.nanoTime();
		byteBufferWriter.putLong(long1);
		byteBufferWriter.putLong(0L);
		PacketTypes.PacketType.TimeSync.send(GameClient.connection);
	}

	public static void receiveTimeSync(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		long long1;
		long long2;
		if (GameServer.bServer) {
			long1 = byteBuffer.getLong();
			long2 = System.nanoTime();
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.TimeSync.doPacket(byteBufferWriter);
			byteBufferWriter.putLong(long1);
			byteBufferWriter.putLong(long2);
			PacketTypes.PacketType.TimeSync.send(udpConnection);
		}

		if (GameClient.bClient) {
			long1 = byteBuffer.getLong();
			long2 = byteBuffer.getLong();
			long long3 = System.nanoTime();
			syncServerTime(long1, long2, long3);
			DebugLog.printServerTime = true;
		}
	}

	public float getRealworldSecondsSinceLastUpdate() {
		return 0.016666668F * this.FPSMultiplier;
	}

	public float getMultipliedSecondsSinceLastUpdate() {
		return 0.016666668F * this.getUnmoddedMultiplier();
	}

	public float getGameWorldSecondsSinceLastUpdate() {
		float float1 = 1440.0F / this.getMinutesPerDay();
		return this.getTimeDelta() * float1;
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
			string = int1 + " " + Translator.getText("IGUI_Gametime_minutes") + ", " + int2 + " " + Translator.getText("IGUI_Gametime_secondes");
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
		String string = Translator.getTextOrNull("IGUI_Gametime_" + Core.GameMode);
		if (string == null) {
			string = Core.GameMode;
		}

		String string2 = Translator.getTextOrNull("IGUI_Gametime_GameMode", string);
		if (string2 == null) {
			string2 = "Game mode: " + string;
		}

		if (Core.bDebug) {
			string2 = string2 + " (DEBUG)";
		}

		return string2;
	}

	public void init() {
		this.setDay(this.getStartDay());
		this.setTimeOfDay(this.getStartTimeOfDay());
		this.setMonth(this.getStartMonth());
		this.setYear(this.getStartYear());
		if (SandboxOptions.instance.Helicopter.getValue() != 1) {
			this.HelicopterDay1 = Rand.Next(6, 10);
			this.HelicopterTime1Start = Rand.Next(9, 19);
			this.HelicopterTime1End = this.HelicopterTime1Start + Rand.Next(4) + 1;
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
		return this.Lerp(float1, float2, float8);
	}

	public float getDeltaMinutesPerDay() {
		return this.MinutesPerDayStart / this.MinutesPerDay;
	}

	public float getNightMin() {
		return 1.0F - this.NightMin;
	}

	public void setNightMin(float float1) {
		this.NightMin = 1.0F - float1;
	}

	public float getNightMax() {
		return 1.0F - this.NightMax;
	}

	public void setNightMax(float float1) {
		this.NightMax = 1.0F - float1;
	}

	public int getMinutes() {
		return (int)((this.getTimeOfDay() - (float)((int)this.getTimeOfDay())) * 60.0F);
	}

	public void setMoon(float float1) {
		this.Moon = float1;
	}

	public void update(boolean boolean1) {
		long long1 = System.currentTimeMillis();
		if (GameClient.bClient && (this.lastPing == 0L || long1 - this.lastPing > 10000L)) {
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

		if (!GameClient.bClient && this.randomAmbientToday && short1 != -1 && Rand.Next(Rand.AdjustForFramerate(short1)) == 0 && !isGamePaused()) {
			AmbientStreamManager.instance.addRandomAmbient();
			this.randomAmbientToday = SandboxOptions.instance.MetaEvent.getValue() == 3 && Rand.Next(3) == 0;
		}

		if (GameServer.bServer && UIManager.getSpeedControls() != null) {
			UIManager.getSpeedControls().SetCurrentGameSpeed(1);
		}

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

		int1 = this.getHour();
		this.updateCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), (int)((this.getTimeOfDay() - (float)((int)this.getTimeOfDay())) * 60.0F));
		float float1 = this.getTimeOfDay();
		float float2;
		int int2;
		if (!isGamePaused()) {
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

				this.updateCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), this.getMinutes());
				LuaEventManager.triggerEvent("EveryDays");
			}
		} else if (this.getTimeOfDay() >= 24.0F) {
			this.setTimeOfDay(this.getTimeOfDay() - 24.0F);
			this.setDay(this.getDay() + 1);
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
		if (!ClimateManager.getInstance().getThunderStorm().isModifyingNight()) {
			this.setAmbient(this.TimeLerp(this.getAmbientMin(), this.getAmbientMax(), (float)this.getDusk(), (float)this.getDawn()));
		}

		if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
			this.setNightTint(0.0F);
		}

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
			getInstance().updateRoomLight();
			LuaEventManager.triggerEvent("EveryTenMinutes");
			this.minutesMod = int2 / 10;
			ZomboidRadio.getInstance().UpdateScripts(this.getHour(), int2);
		}

		if (this.previousMinuteStamp != this.minutesStamp) {
			LuaEventManager.triggerEvent("EveryOneMinute");
			this.previousMinuteStamp = this.minutesStamp;
		}

		if (GameServer.bServer && (long1 - this.lastClockSync > 10000L || GameServer.bFastForward)) {
			GameServer.syncClock();
			this.lastClockSync = long1;
		}
	}

	private void updateRoomLight() {
	}

	private void setMinutesStamp() {
		this.minutesStamp = (long)this.getWorldAgeHours() * 60L + (long)this.getMinutes();
	}

	public long getMinutesStamp() {
		return this.minutesStamp;
	}

	public boolean getThunderStorm() {
		return ClimateManager.getInstance().getIsThunderStorming();
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

	public int getDayPlusOne() {
		return this.Day + 1;
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

	public void setHoursSurvived(double double1) {
		DebugLog.log("GameTime.getHoursSurvived() has no meaning, use IsoPlayer.getHourSurvived() instead");
		this.HoursSurvived = double1;
	}

	public int getHour() {
		double double1 = Math.floor((double)(this.getTimeOfDay() * 3600.0F));
		return (int)Math.floor(double1 / 3600.0);
	}

	public PZCalendar getCalender() {
		this.updateCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), (int)((this.getTimeOfDay() - (float)((int)this.getTimeOfDay())) * 60.0F));
		return this.Calender;
	}

	public void setCalender(PZCalendar pZCalendar) {
		this.Calender = pZCalendar;
	}

	public void updateCalendar(int int1, int int2, int int3, int int4, int int5) {
		if (this.Calender == null) {
			this.Calender = new PZCalendar(new GregorianCalendar());
		}

		this.Calender.set(int1, int2, int3, int4, int5);
	}

	public float getMinutesPerDay() {
		return this.MinutesPerDay;
	}

	public void setMinutesPerDay(float float1) {
		this.MinutesPerDay = float1;
	}

	public float getLastTimeOfDay() {
		return this.LastTimeOfDay;
	}

	public void setLastTimeOfDay(float float1) {
		this.LastTimeOfDay = float1;
	}

	public void setTargetZombies(int int1) {
		this.TargetZombies = int1;
	}

	public boolean isRainingToday() {
		return this.RainingToday;
	}

	public float getMultiplier() {
		if (!GameServer.bServer && !GameClient.bClient && IsoPlayer.getInstance() != null && IsoPlayer.allPlayersAsleep()) {
			return 200.0F * (30.0F / (float)PerformanceSettings.getLockFPS());
		} else {
			float float1 = 1.0F;
			if (GameServer.bServer && GameServer.bFastForward) {
				float1 = (float)ServerOptions.instance.FastForwardMultiplier.getValue() / this.getDeltaMinutesPerDay();
			} else if (GameClient.bClient && GameClient.bFastForward) {
				float1 = (float)ServerOptions.instance.FastForwardMultiplier.getValue() / this.getDeltaMinutesPerDay();
			}

			float1 *= this.Multiplier;
			float1 *= this.FPSMultiplier;
			float1 *= this.multiplierBias;
			float1 *= this.PerObjectMultiplier;
			if (DebugOptions.instance.GameTimeSpeedQuarter.getValue()) {
				float1 *= 0.25F;
			}

			if (DebugOptions.instance.GameTimeSpeedHalf.getValue()) {
				float1 *= 0.5F;
			}

			float1 *= 0.8F;
			return float1;
		}
	}

	public float getTimeDelta() {
		return this.getMultiplier() / (0.8F * this.multiplierBias) / 60.0F;
	}

	public static float getAnimSpeedFix() {
		return 0.8F;
	}

	public void setMultiplier(float float1) {
		this.Multiplier = float1;
	}

	public float getServerMultiplier() {
		float float1 = 10.0F / GameWindow.averageFPS / (float)(PerformanceSettings.ManualFrameSkips + 1);
		float float2 = this.Multiplier * float1;
		float2 *= 0.5F;
		if (!GameServer.bServer && !GameClient.bClient && IsoPlayer.getInstance() != null && IsoPlayer.allPlayersAsleep()) {
			return 200.0F * (30.0F / (float)PerformanceSettings.getLockFPS());
		} else {
			float2 *= 1.6F;
			float2 *= this.multiplierBias;
			return float2;
		}
	}

	public float getUnmoddedMultiplier() {
		if (!GameServer.bServer && !GameClient.bClient && IsoPlayer.getInstance() != null && IsoPlayer.allPlayersAsleep()) {
			return 200.0F * (30.0F / (float)PerformanceSettings.getLockFPS());
		} else {
			float float1 = this.Multiplier * this.FPSMultiplier * this.PerObjectMultiplier;
			return float1;
		}
	}

	public float getInvMultiplier() {
		return 1.0F / this.getMultiplier();
	}

	public float getTrueMultiplier() {
		return this.Multiplier * this.PerObjectMultiplier;
	}

	public void save() {
		File file = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("map_t.bin"));
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
		dataOutputStream.writeInt(186);
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
		dataOutputStream.writeInt(0);
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
		byteBuffer.putInt(0);
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
			int1 = 186;
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
		short short1 = 186;
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
		if (byteBuffer.get() == 1) {
			if (this.table == null) {
				this.table = LuaManager.platform.newTable();
			}

			this.table.load((ByteBuffer)byteBuffer, short1);
		}

		this.setMinutesStamp();
	}

	public void load() {
		File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_t.bin");
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			try {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				try {
					synchronized (SliceY.SliceBufferLock) {
						SliceY.SliceBuffer.clear();
						int int1 = bufferedInputStream.read(SliceY.SliceBuffer.array());
						SliceY.SliceBuffer.limit(int1);
						DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(SliceY.SliceBuffer.array(), 0, int1));
						this.load(dataInputStream);
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
		} catch (FileNotFoundException fileNotFoundException) {
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
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

	public int getHelicopterDay1() {
		return this.HelicopterDay1;
	}

	public int getHelicopterDay() {
		return this.HelicopterDay1;
	}

	public void setHelicopterDay(int int1) {
		this.HelicopterDay1 = PZMath.max(int1, 0);
	}

	public int getHelicopterStartHour() {
		return this.HelicopterTime1Start;
	}

	public void setHelicopterStartHour(int int1) {
		this.HelicopterTime1Start = PZMath.clamp(int1, 0, 24);
	}

	public int getHelicopterEndHour() {
		return this.HelicopterTime1End;
	}

	public void setHelicopterEndHour(int int1) {
		this.HelicopterTime1End = PZMath.clamp(int1, 0, 24);
	}

	public static boolean isGamePaused() {
		if (GameServer.bServer) {
			return GameServer.Players.isEmpty() && ServerOptions.instance.PauseEmpty.getValue();
		} else if (GameClient.bClient) {
			return GameClient.IsClientPaused();
		} else {
			SpeedControls speedControls = UIManager.getSpeedControls();
			return speedControls != null && speedControls.getCurrentGameSpeed() == 0;
		}
	}

	public static class AnimTimer {
		public float Elapsed;
		public float Duration;
		public boolean Finished = true;
		public int Ticks;

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
