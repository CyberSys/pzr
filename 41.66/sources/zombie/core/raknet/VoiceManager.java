package zombie.core.raknet;

import fmod.FMODSoundBuffer;
import fmod.FMOD_DriverInfo;
import fmod.FMOD_RESULT;
import fmod.SoundBuffer;
import fmod.javafmod;
import fmod.javafmodJNI;
import fmod.fmod.FMODManager;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;
import zombie.input.GameKeyboard;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Radio;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.FakeClientManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.MPStatistics;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.radio.devices.DeviceData;


public class VoiceManager {
	private static final int FMOD_SOUND_MODE;
	public static final int modePPT = 1;
	public static final int modeVAD = 2;
	public static final int modeMute = 3;
	public static final int VADModeQuality = 1;
	public static final int VADModeLowBitrate = 2;
	public static final int VADModeAggressive = 3;
	public static final int VADModeVeryAggressive = 4;
	public static final int AGCModeAdaptiveAnalog = 1;
	public static final int AGCModeAdaptiveDigital = 2;
	public static final int AGCModeFixedDigital = 3;
	private static final int bufferSize = 192;
	private static final int complexity = 1;
	private static boolean serverVOIPEnable;
	private static int sampleRate;
	private static int period;
	private static int buffering;
	private static float minDistance;
	private static float maxDistance;
	private static boolean is3D;
	private boolean isEnable = true;
	private boolean isModeVAD = false;
	private boolean isModePPT = false;
	private int vadMode = 3;
	private int agcMode = 2;
	private int volumeMic;
	private int volumePlayers;
	public static boolean VoipDisabled;
	private boolean isServer;
	private static FMODSoundBuffer FMODReceiveBuffer;
	private int FMODVoiceRecordDriverId;
	private long FMODChannelGroup = 0L;
	private long FMODRecordSound = 0L;
	private Semaphore recDevSemaphore;
	private boolean initialiseRecDev = false;
	private boolean initialisedRecDev = false;
	private long indicatorIsVoice = 0L;
	private Thread thread;
	private boolean bQuit;
	private long timeLast;
	private boolean isDebug = false;
	private boolean isDebugLoopback = false;
	private boolean isDebugLoopbackLong = false;
	public static VoiceManager instance;
	byte[] buf = new byte[192];
	private final Object notifier = new Object();
	private boolean bIsClient = false;
	private boolean bTestingMicrophone = false;
	private long testingMicrophoneMS = 0L;
	private final Long recBuf_Current_read = new Long(0L);
	private static long timestamp;

	public static VoiceManager getInstance() {
		return instance;
	}

	public void DeinitRecSound() {
		this.initialisedRecDev = false;
		if (this.FMODRecordSound != 0L) {
			javafmod.FMOD_RecordSound_Release(this.FMODRecordSound);
			this.FMODRecordSound = 0L;
		}

		FMODReceiveBuffer = null;
	}

	public void ResetRecSound() {
		this.DeinitRecSound();
		this.FMODRecordSound = javafmod.FMOD_System_CreateRecordSound((long)this.FMODVoiceRecordDriverId, (long)(FMODManager.FMOD_2D | FMODManager.FMOD_OPENUSER | FMODManager.FMOD_SOFTWARE), (long)FMODManager.FMOD_SOUND_FORMAT_PCM16, (long)sampleRate, this.agcMode);
		if (this.FMODRecordSound == 0L) {
			DebugLog.Voice.warn("FMOD_System_CreateSound result=%d", this.FMODRecordSound);
		}

		javafmod.FMOD_System_SetRecordVolume(1L - Math.round(Math.pow(1.4, (double)(11 - this.volumeMic))));
		if (this.initialiseRecDev) {
			int int1 = javafmod.FMOD_System_RecordStart(this.FMODVoiceRecordDriverId, this.FMODRecordSound, true);
			if (int1 != FMOD_RESULT.FMOD_OK.ordinal()) {
				DebugLog.Voice.warn("FMOD_System_RecordStart result=%d", int1);
			}
		}

		javafmod.FMOD_System_SetVADMode(this.vadMode - 1);
		FMODReceiveBuffer = new FMODSoundBuffer(this.FMODRecordSound);
		this.initialisedRecDev = true;
	}

	public void VoiceRestartClient(boolean boolean1) {
		if (GameClient.connection != null) {
			if (boolean1) {
				this.loadConfig();
				this.VoiceConnectReq(GameClient.connection.getConnectedGUID());
			} else {
				this.DeinitRecSound();
				this.VoiceConnectClose(GameClient.connection.getConnectedGUID());
				this.loadConfig();
			}
		} else {
			this.loadConfig();
			if (boolean1) {
				this.InitRecDeviceForTest();
			} else {
				this.DeinitRecSound();
			}
		}
	}

	void VoiceInitClient() {
		this.isServer = false;
		this.recDevSemaphore = new Semaphore(1);
		FMODReceiveBuffer = null;
		RakVoice.RVInit(192);
		RakVoice.SetComplexity(1);
	}

	void VoiceInitServer(boolean boolean1, int int1, int int2, int int3, int int4, double double1, double double2, boolean boolean2) {
		this.isServer = true;
		if (!(int2 == 2 | int2 == 5 | int2 == 10 | int2 == 20 | int2 == 40 | int2 == 60)) {
			DebugLog.Voice.error("Invalid period=%d", int2);
		} else if (!(int1 == 8000 | int1 == 16000 | int1 == 24000)) {
			DebugLog.Voice.error("Invalid sample rate=%d", int1);
		} else if (int3 < 0 | int3 > 10) {
			DebugLog.Voice.error("Invalid quality=%d", int3);
		} else if (int4 < 0 | int4 > 32000) {
			DebugLog.Voice.error("Invalid buffering=%d", int4);
		} else {
			sampleRate = int1;
			RakVoice.RVInitServer(boolean1, int1, int2, int3, int4, (float)double1, (float)double2, boolean2);
		}
	}

	void VoiceConnectAccept(long long1) {
		if (this.isEnable) {
			DebugLog.Voice.debugln("uuid=%x", long1);
		}
	}

	void InitRecDeviceForTest() {
		try {
			this.recDevSemaphore.acquire();
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}

		this.ResetRecSound();
		this.recDevSemaphore.release();
	}

	void VoiceOpenChannelReply(long long1, ByteBuffer byteBuffer) {
		if (this.isEnable) {
			DebugLog.Voice.debugln("uuid=%d", long1);
			if (this.isServer) {
				return;
			}

			try {
				if (GameClient.bClient) {
					serverVOIPEnable = byteBuffer.getInt() != 0;
					sampleRate = byteBuffer.getInt();
					period = byteBuffer.getInt();
					byteBuffer.getInt();
					buffering = byteBuffer.getInt();
					minDistance = byteBuffer.getFloat();
					maxDistance = byteBuffer.getFloat();
					is3D = byteBuffer.getInt() != 0;
				} else {
					serverVOIPEnable = RakVoice.GetServerVOIPEnable();
					sampleRate = RakVoice.GetSampleRate();
					period = RakVoice.GetSendFramePeriod();
					buffering = RakVoice.GetBuffering();
					minDistance = RakVoice.GetMinDistance();
					maxDistance = RakVoice.GetMaxDistance();
					is3D = RakVoice.GetIs3D();
				}
			} catch (Exception exception) {
				DebugLog.Voice.printException(exception, "RakVoice params set failed", LogSeverity.Error);
				return;
			}

			DebugLog.Voice.debugln("enabled=%b, sample-rate=%d, period=%d, complexity=%d, buffering=%d, is3D=%b", serverVOIPEnable, sampleRate, period, 1, buffering, is3D);
			try {
				this.recDevSemaphore.acquire();
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}

			int int1 = is3D ? FMODManager.FMOD_3D | FMOD_SOUND_MODE : FMOD_SOUND_MODE;
			Iterator iterator = VoiceManagerData.data.iterator();
			while (iterator.hasNext()) {
				VoiceManagerData voiceManagerData = (VoiceManagerData)iterator.next();
				if (voiceManagerData.userplaysound != 0L) {
					javafmod.FMOD_Sound_SetMode(voiceManagerData.userplaysound, int1);
				}
			}

			long long2 = javafmod.FMOD_System_SetRawPlayBufferingPeriod((long)buffering);
			if (long2 != (long)FMOD_RESULT.FMOD_OK.ordinal()) {
				DebugLog.Voice.warn("FMOD_System_SetRawPlayBufferingPeriod result=%d", long2);
			}

			this.ResetRecSound();
			this.recDevSemaphore.release();
			if (this.isDebug) {
				VoiceDebug.createAndShowGui();
			}
		}
	}

	public void VoiceConnectReq(long long1) {
		if (this.isEnable) {
			DebugLog.Voice.debugln("uuid=%x", long1);
			VoiceManagerData.data.clear();
			RakVoice.RequestVoiceChannel(long1);
		}
	}

	public void VoiceConnectClose(long long1) {
		if (this.isEnable) {
			DebugLog.Voice.debugln("uuid=%x", long1);
			RakVoice.CloseVoiceChannel(long1);
		}
	}

	public void setMode(int int1) {
		if (int1 == 3) {
			this.isModeVAD = false;
			this.isModePPT = false;
		} else if (int1 == 1) {
			this.isModeVAD = false;
			this.isModePPT = true;
		} else if (int1 == 2) {
			this.isModeVAD = true;
			this.isModePPT = false;
		}
	}

	public void setVADMode(int int1) {
		if (!(int1 < 1 | int1 > 4)) {
			this.vadMode = int1;
			if (this.initialisedRecDev) {
				javafmod.FMOD_System_SetVADMode(this.vadMode - 1);
			}
		}
	}

	public void setAGCMode(int int1) {
		if (!(int1 < 1 | int1 > 3)) {
			this.agcMode = int1;
			if (this.initialisedRecDev) {
				this.ResetRecSound();
			}
		}
	}

	public void setVolumePlayers(int int1) {
		if (!(int1 < 0 | int1 > 11)) {
			if (int1 <= 10) {
				this.volumePlayers = int1;
			} else {
				this.volumePlayers = 12;
			}

			if (this.initialisedRecDev) {
				ArrayList arrayList = VoiceManagerData.data;
				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					VoiceManagerData voiceManagerData = (VoiceManagerData)arrayList.get(int2);
					if (voiceManagerData != null && voiceManagerData.userplaychannel != 0L) {
						javafmod.FMOD_Channel_SetVolume(voiceManagerData.userplaychannel, (float)((double)this.volumePlayers * 0.2));
					}
				}
			}
		}
	}

	public void setVolumeMic(int int1) {
		if (!(int1 < 0 | int1 > 11)) {
			if (int1 <= 10) {
				this.volumeMic = int1;
			} else {
				this.volumeMic = 12;
			}

			if (this.initialisedRecDev) {
				javafmod.FMOD_System_SetRecordVolume(1L - Math.round(Math.pow(1.4, (double)(11 - this.volumeMic))));
			}
		}
	}

	public static void playerSetMute(String string) {
		ArrayList arrayList = GameClient.instance.getPlayers();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			IsoPlayer player = (IsoPlayer)arrayList.get(int1);
			if (string.equals(player.username)) {
				VoiceManagerData voiceManagerData = VoiceManagerData.get(player.OnlineID);
				voiceManagerData.userplaymute = !voiceManagerData.userplaymute;
				player.isVoiceMute = voiceManagerData.userplaymute;
				break;
			}
		}
	}

	public static boolean playerGetMute(String string) {
		ArrayList arrayList = GameClient.instance.getPlayers();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			IsoPlayer player = (IsoPlayer)arrayList.get(int1);
			if (string.equals(player.username)) {
				boolean boolean1 = VoiceManagerData.get(player.OnlineID).userplaymute;
				return boolean1;
			}
		}

		return true;
	}

	public void LuaRegister(Platform platform, KahluaTable kahluaTable) {
		KahluaTable kahluaTable2 = platform.newTable();
		kahluaTable2.rawset("playerSetMute", new JavaFunction(){
			
			public int call(LuaCallFrame platform, int kahluaTable) {
				Object kahluaTable2 = platform.get(1);
				VoiceManager.playerSetMute((String)kahluaTable2);
				return 1;
			}
		});
		kahluaTable2.rawset("playerGetMute", new JavaFunction(){
			
			public int call(LuaCallFrame platform, int kahluaTable) {
				Object kahluaTable2 = platform.get(1);
				platform.push(VoiceManager.playerGetMute((String)kahluaTable2));
				return 1;
			}
		});
		kahluaTable2.rawset("RecordDevices", new JavaFunction(){
			
			public int call(LuaCallFrame platform, int kahluaTable) {
				if (!Core.SoundDisabled && !VoiceManager.VoipDisabled) {
					int var7 = javafmod.FMOD_System_GetRecordNumDrivers();
					KahluaTable var4 = platform.getPlatform().newTable();
					for (int var5 = 0; var5 < var7; ++var5) {
						FMOD_DriverInfo var6 = new FMOD_DriverInfo();
						javafmod.FMOD_System_GetRecordDriverInfo(var5, var6);
						var4.rawset(var5 + 1, var6.name);
					}

					platform.push(var4);
					return 1;
				} else {
					KahluaTable kahluaTable2 = platform.getPlatform().newTable();
					platform.push(kahluaTable2);
					return 1;
				}
			}
		});
		kahluaTable.rawset("VoiceManager", kahluaTable2);
	}

	private void setUserPlaySound(long long1, float float1) {
		float1 = IsoUtils.clamp(float1 * IsoUtils.lerp((float)this.volumePlayers, 0.0F, 12.0F), 0.0F, 1.0F);
		javafmod.FMOD_Channel_SetVolume(long1, float1);
	}

	private long getUserPlaySound(short short1) {
		VoiceManagerData voiceManagerData = VoiceManagerData.get(short1);
		if (voiceManagerData.userplaychannel == 0L) {
			voiceManagerData.userplaysound = 0L;
			int int1 = is3D ? FMODManager.FMOD_3D | FMOD_SOUND_MODE : FMOD_SOUND_MODE;
			voiceManagerData.userplaysound = javafmod.FMOD_System_CreateRAWPlaySound((long)int1, (long)FMODManager.FMOD_SOUND_FORMAT_PCM16, (long)sampleRate);
			if (voiceManagerData.userplaysound == 0L) {
				DebugLog.Voice.warn("FMOD_System_CreateSound result=%d", voiceManagerData.userplaysound);
			}

			voiceManagerData.userplaychannel = javafmod.FMOD_System_PlaySound(voiceManagerData.userplaysound, false);
			if (voiceManagerData.userplaychannel == 0L) {
				DebugLog.Voice.warn("FMOD_System_PlaySound result=%d", voiceManagerData.userplaychannel);
			}

			javafmod.FMOD_Channel_SetVolume(voiceManagerData.userplaychannel, (float)((double)this.volumePlayers * 0.2));
			if (is3D) {
				javafmod.FMOD_Channel_Set3DMinMaxDistance(voiceManagerData.userplaychannel, minDistance / 2.0F, maxDistance);
			}

			javafmod.FMOD_Channel_SetChannelGroup(voiceManagerData.userplaychannel, this.FMODChannelGroup);
		}

		return voiceManagerData.userplaysound;
	}

	public void InitVMClient() {
		if (!Core.SoundDisabled && !VoipDisabled) {
			int int1 = javafmod.FMOD_System_GetRecordNumDrivers();
			this.FMODVoiceRecordDriverId = Core.getInstance().getOptionVoiceRecordDevice() - 1;
			if (this.FMODVoiceRecordDriverId < 0 && int1 > 0) {
				Core.getInstance().setOptionVoiceRecordDevice(1);
				this.FMODVoiceRecordDriverId = Core.getInstance().getOptionVoiceRecordDevice() - 1;
			}

			if (int1 < 1) {
				DebugLog.Voice.debugln("Microphone not found");
				this.initialiseRecDev = false;
			} else if (this.FMODVoiceRecordDriverId < 0 | this.FMODVoiceRecordDriverId >= int1) {
				DebugLog.Voice.warn("Invalid record device");
				this.initialiseRecDev = false;
			} else {
				this.initialiseRecDev = true;
			}

			this.isEnable = Core.getInstance().getOptionVoiceEnable();
			this.setMode(Core.getInstance().getOptionVoiceMode());
			this.vadMode = Core.getInstance().getOptionVoiceVADMode();
			this.volumeMic = Core.getInstance().getOptionVoiceVolumeMic();
			this.volumePlayers = Core.getInstance().getOptionVoiceVolumePlayers();
			this.FMODChannelGroup = javafmod.FMOD_System_CreateChannelGroup("VOIP");
			this.VoiceInitClient();
			this.FMODRecordSound = 0L;
			if (this.isEnable) {
				this.InitRecDeviceForTest();
			}

			if (this.isDebug) {
				VoiceDebug.createAndShowGui();
			}

			this.timeLast = System.currentTimeMillis();
			this.bQuit = false;
			this.thread = new Thread(){
				
				public void run() {
					while (!VoiceManager.this.bQuit) {
						try {
							VoiceManager.this.UpdateVMClient();
							sleep((long)(VoiceManager.period / 2));
						} catch (Exception var2) {
							var2.printStackTrace();
						}
					}
				}
			};

			this.thread.setName("VoiceManagerClient");
			this.thread.start();
		} else {
			this.isEnable = false;
			this.initialiseRecDev = false;
			this.initialisedRecDev = false;
			DebugLog.Voice.debugln("Disabled");
		}
	}

	public void loadConfig() {
		this.isEnable = Core.getInstance().getOptionVoiceEnable();
		this.setMode(Core.getInstance().getOptionVoiceMode());
		this.vadMode = Core.getInstance().getOptionVoiceVADMode();
		this.volumeMic = Core.getInstance().getOptionVoiceVolumeMic();
		this.volumePlayers = Core.getInstance().getOptionVoiceVolumePlayers();
	}

	public void UpdateRecordDevice() {
		if (this.initialisedRecDev) {
			int int1 = javafmod.FMOD_System_RecordStop(this.FMODVoiceRecordDriverId);
			if (int1 != FMOD_RESULT.FMOD_OK.ordinal()) {
				DebugLog.Voice.warn("FMOD_System_RecordStop result=%d", int1);
			}

			this.FMODVoiceRecordDriverId = Core.getInstance().getOptionVoiceRecordDevice() - 1;
			if (this.FMODVoiceRecordDriverId < 0) {
				DebugLog.Voice.error("No record device found");
			} else {
				int1 = javafmod.FMOD_System_RecordStart(this.FMODVoiceRecordDriverId, this.FMODRecordSound, true);
				if (int1 != FMOD_RESULT.FMOD_OK.ordinal()) {
					DebugLog.Voice.warn("FMOD_System_RecordStart result=%d", int1);
				}
			}
		}
	}

	public void DeinitVMClient() {
		if (this.thread != null) {
			this.bQuit = true;
			synchronized (this.notifier) {
				this.notifier.notify();
			}

			while (this.thread.isAlive()) {
				try {
					Thread.sleep(10L);
				} catch (InterruptedException interruptedException) {
				}
			}

			this.thread = null;
		}

		this.DeinitRecSound();
		ArrayList arrayList = VoiceManagerData.data;
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			VoiceManagerData voiceManagerData = (VoiceManagerData)arrayList.get(int1);
			if (voiceManagerData.userplaychannel != 0L) {
				javafmod.FMOD_Channel_Stop(voiceManagerData.userplaychannel);
			}

			if (voiceManagerData.userplaysound != 0L) {
				javafmod.FMOD_RAWPlaySound_Release(voiceManagerData.userplaysound);
				voiceManagerData.userplaysound = 0L;
			}
		}

		VoiceManagerData.data.clear();
	}

	public void setTestingMicrophone(boolean boolean1) {
		if (boolean1) {
			this.testingMicrophoneMS = System.currentTimeMillis();
		}

		if (boolean1 != this.bTestingMicrophone) {
			this.bTestingMicrophone = boolean1;
			this.notifyThread();
		}
	}

	public void notifyThread() {
		synchronized (this.notifier) {
			this.notifier.notify();
		}
	}

	public void update() {
		if (!GameServer.bServer) {
			if (this.bTestingMicrophone) {
				long long1 = System.currentTimeMillis();
				if (long1 - this.testingMicrophoneMS > 1000L) {
					this.setTestingMicrophone(false);
				}
			}

			if ((!GameClient.bClient || GameClient.connection == null) && !FakeClientManager.isVOIPEnabled()) {
				if (this.bIsClient) {
					this.bIsClient = false;
					this.notifyThread();
				}
			} else if (!this.bIsClient) {
				this.bIsClient = true;
				this.notifyThread();
			}
		}
	}

	private float getCanHearAllVolume(float float1) {
		return float1 > minDistance ? IsoUtils.clamp(1.0F - IsoUtils.lerp(float1, minDistance, maxDistance), 0.2F, 1.0F) : 1.0F;
	}

	synchronized void UpdateVMClient() throws InterruptedException {
		while (!this.bQuit && !this.bIsClient && !this.bTestingMicrophone) {
			synchronized (this.notifier) {
				try {
					this.notifier.wait();
				} catch (InterruptedException interruptedException) {
				}
			}
		}

		if (serverVOIPEnable) {
			if (IsoPlayer.getInstance() != null) {
				IsoPlayer.getInstance().isSpeek = System.currentTimeMillis() - this.indicatorIsVoice <= 300L;
			}

			if (this.initialiseRecDev) {
				this.recDevSemaphore.acquire();
				javafmod.FMOD_System_GetRecordPosition(this.FMODVoiceRecordDriverId, this.recBuf_Current_read);
				if (FMODReceiveBuffer != null) {
					label210: while (true) {
						while (true) {
							if (!FMODReceiveBuffer.pull(this.recBuf_Current_read)) {
								break label210;
							}

							if ((IsoPlayer.getInstance() == null || GameClient.connection == null) && !FakeClientManager.isVOIPEnabled()) {
								break;
							}

							if (!is3D || !IsoPlayer.getInstance().isDead()) {
								if (this.isModePPT) {
									if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Enable voice transmit"))) {
										RakVoice.SendFrame(GameClient.connection.connectedGUID, (long)IsoPlayer.getInstance().OnlineID, FMODReceiveBuffer.buf(), FMODReceiveBuffer.get_size());
										this.indicatorIsVoice = System.currentTimeMillis();
									} else if (FakeClientManager.isVOIPEnabled()) {
										RakVoice.SendFrame(FakeClientManager.getConnectedGUID(), FakeClientManager.getOnlineID(), FMODReceiveBuffer.buf(), FMODReceiveBuffer.get_size());
										this.indicatorIsVoice = System.currentTimeMillis();
									}
								}

								if (this.isModeVAD && FMODReceiveBuffer.get_vad() != 0L) {
									RakVoice.SendFrame(GameClient.connection.connectedGUID, (long)IsoPlayer.getInstance().OnlineID, FMODReceiveBuffer.buf(), FMODReceiveBuffer.get_size());
									this.indicatorIsVoice = System.currentTimeMillis();
								}

								break;
							}
						}

						if (this.isDebug) {
							if (GameClient.IDToPlayerMap.values().size() > 0) {
								VoiceDebug.updateGui((SoundBuffer)null, FMODReceiveBuffer);
							} else if (this.isDebugLoopback) {
								VoiceDebug.updateGui((SoundBuffer)null, FMODReceiveBuffer);
							} else {
								VoiceDebug.updateGui((SoundBuffer)null, FMODReceiveBuffer);
							}
						}

						if (this.isDebugLoopback) {
							javafmod.FMOD_System_RAWPlayData(this.getUserPlaySound((short)0), FMODReceiveBuffer.buf(), FMODReceiveBuffer.get_size());
						}
					}
				}

				this.recDevSemaphore.release();
			}

			ArrayList arrayList = GameClient.instance.getPlayers();
			ArrayList arrayList2 = VoiceManagerData.data;
			int int1;
			for (int int2 = 0; int2 < arrayList2.size(); ++int2) {
				VoiceManagerData voiceManagerData = (VoiceManagerData)arrayList2.get(int2);
				boolean boolean1 = false;
				for (int1 = 0; int1 < arrayList.size(); ++int1) {
					IsoPlayer player = (IsoPlayer)arrayList.get(int1);
					if (player.OnlineID == voiceManagerData.index) {
						boolean1 = true;
						break;
					}
				}

				if (this.isDebugLoopback & voiceManagerData.index == 0) {
					break;
				}

				if (voiceManagerData.userplaychannel != 0L & !boolean1) {
					javafmod.FMOD_Channel_Stop(voiceManagerData.userplaychannel);
					voiceManagerData.userplaychannel = 0L;
				}
			}

			long long1 = System.currentTimeMillis() - this.timeLast;
			if (long1 >= (long)period) {
				this.timeLast += long1;
				if (IsoPlayer.getInstance() != null) {
					VoiceManagerData.VoiceDataSource voiceDataSource = VoiceManagerData.VoiceDataSource.Unknown;
					int1 = 0;
					Iterator iterator = arrayList.iterator();
					label172: while (true) {
						IsoPlayer player2;
						IsoPlayer player3;
						do {
							do {
								if (!iterator.hasNext()) {
									MPStatistics.setVOIPSource(voiceDataSource, int1);
									return;
								}

								player2 = (IsoPlayer)iterator.next();
								player3 = IsoPlayer.getInstance();
							}					 while (player2 == player3);
						}				 while (player2.getOnlineID() == -1);

						VoiceManagerData voiceManagerData2 = VoiceManagerData.get(player2.getOnlineID());
						while (true) {
							do {
								if (!RakVoice.ReceiveFrame((long)player2.getOnlineID(), this.buf)) {
									if (voiceManagerData2.voicetimeout == 0L) {
										player2.isSpeek = false;
									} else {
										--voiceManagerData2.voicetimeout;
										player2.isSpeek = true;
									}

									continue label172;
								}

								voiceManagerData2.voicetimeout = 10L;
							}					 while (voiceManagerData2.userplaymute);

							float float1 = IsoUtils.DistanceTo(player3.getX(), player3.getY(), player2.getX(), player2.getY());
							if (player3.isCanHearAll()) {
								javafmodJNI.FMOD_Channel_Set3DLevel(voiceManagerData2.userplaychannel, 0.0F);
								javafmod.FMOD_Channel_Set3DAttributes(voiceManagerData2.userplaychannel, player3.x, player3.y, player3.z, 0.0F, 0.0F, 0.0F);
								this.setUserPlaySound(voiceManagerData2.userplaychannel, this.getCanHearAllVolume(float1));
								voiceDataSource = VoiceManagerData.VoiceDataSource.Cheat;
								int1 = 0;
							} else {
								VoiceManagerData.RadioData radioData = this.checkForNearbyRadios(voiceManagerData2);
								if (radioData != null && radioData.deviceData != null) {
									javafmodJNI.FMOD_Channel_Set3DLevel(voiceManagerData2.userplaychannel, 0.0F);
									javafmod.FMOD_Channel_Set3DAttributes(voiceManagerData2.userplaychannel, player3.x, player3.y, player3.z, 0.0F, 0.0F, 0.0F);
									this.setUserPlaySound(voiceManagerData2.userplaychannel, radioData.deviceData.getDeviceVolume());
									radioData.deviceData.doReceiveMPSignal(radioData.lastReceiveDistance);
									voiceDataSource = VoiceManagerData.VoiceDataSource.Radio;
									int1 = radioData.freq;
								} else {
									if (radioData == null) {
										javafmodJNI.FMOD_Channel_Set3DLevel(voiceManagerData2.userplaychannel, 0.0F);
										javafmod.FMOD_Channel_Set3DAttributes(voiceManagerData2.userplaychannel, player3.x, player3.y, player3.z, 0.0F, 0.0F, 0.0F);
										javafmod.FMOD_Channel_SetVolume(voiceManagerData2.userplaychannel, 0.0F);
										voiceDataSource = VoiceManagerData.VoiceDataSource.Unknown;
									} else {
										if (is3D) {
											javafmodJNI.FMOD_Channel_Set3DLevel(voiceManagerData2.userplaychannel, IsoUtils.lerp(float1, 0.0F, minDistance));
											javafmod.FMOD_Channel_Set3DAttributes(voiceManagerData2.userplaychannel, player2.x, player2.y, player2.z, 0.0F, 0.0F, 0.0F);
										} else {
											javafmodJNI.FMOD_Channel_Set3DLevel(voiceManagerData2.userplaychannel, 0.0F);
											javafmod.FMOD_Channel_Set3DAttributes(voiceManagerData2.userplaychannel, player3.x, player3.y, player3.z, 0.0F, 0.0F, 0.0F);
										}

										this.setUserPlaySound(voiceManagerData2.userplaychannel, IsoUtils.smoothstep(maxDistance, minDistance, radioData.lastReceiveDistance));
										voiceDataSource = VoiceManagerData.VoiceDataSource.Voice;
									}

									int1 = 0;
									if (float1 > maxDistance) {
										logFrame(player3, player2, float1);
									}
								}
							}

							javafmod.FMOD_System_RAWPlayData(this.getUserPlaySound(player2.getOnlineID()), this.buf, (long)this.buf.length);
							if (this.isDebugLoopbackLong) {
								RakVoice.SendFrame(GameClient.connection.connectedGUID, (long)player3.getOnlineID(), this.buf, (long)this.buf.length);
							}
						}
					}
				}
			}
		}
	}

	private static void logFrame(IsoPlayer player, IsoPlayer player2, float float1) {
		long long1 = System.currentTimeMillis();
		if (long1 > timestamp) {
			timestamp = long1 + 5000L;
			DebugLog.Multiplayer.warn(String.format("\"%s\" (%b) received VOIP frame from \"%s\" (%b) at distance=%f", player.getUsername(), player.isCanHearAll(), player2.getUsername(), player2.isCanHearAll(), float1));
		}
	}

	private VoiceManagerData.RadioData checkForNearbyRadios(VoiceManagerData voiceManagerData) {
		IsoPlayer player = IsoPlayer.getInstance();
		VoiceManagerData voiceManagerData2 = VoiceManagerData.get(player.OnlineID);
		if (voiceManagerData2.isCanHearAll) {
			((VoiceManagerData.RadioData)voiceManagerData2.radioData.get(0)).lastReceiveDistance = 0.0F;
			return (VoiceManagerData.RadioData)voiceManagerData2.radioData.get(0);
		} else {
			VoiceManagerData.RadioData radioData;
			synchronized (voiceManagerData2.radioData) {
				int int1 = 1;
				while (true) {
					if (int1 >= voiceManagerData2.radioData.size()) {
						break;
					}

					synchronized (voiceManagerData.radioData) {
						int int2 = 1;
						while (true) {
							if (int2 >= voiceManagerData.radioData.size()) {
								break;
							}

							if (((VoiceManagerData.RadioData)voiceManagerData2.radioData.get(int1)).freq == ((VoiceManagerData.RadioData)voiceManagerData.radioData.get(int2)).freq) {
								float float1 = (float)(((VoiceManagerData.RadioData)voiceManagerData2.radioData.get(int1)).x - ((VoiceManagerData.RadioData)voiceManagerData.radioData.get(int2)).x);
								float float2 = (float)(((VoiceManagerData.RadioData)voiceManagerData2.radioData.get(int1)).y - ((VoiceManagerData.RadioData)voiceManagerData.radioData.get(int2)).y);
								((VoiceManagerData.RadioData)voiceManagerData2.radioData.get(int1)).lastReceiveDistance = (float)Math.sqrt((double)(float1 * float1 + float2 * float2));
								if (((VoiceManagerData.RadioData)voiceManagerData2.radioData.get(int1)).lastReceiveDistance < ((VoiceManagerData.RadioData)voiceManagerData.radioData.get(int2)).distance) {
									radioData = (VoiceManagerData.RadioData)voiceManagerData2.radioData.get(int1);
									return radioData;
								}
							}

							++int2;
						}
					}

					++int1;
				}
			}

			synchronized (voiceManagerData2.radioData) {
				synchronized (voiceManagerData.radioData) {
					if (!voiceManagerData.radioData.isEmpty()) {
						float float3 = (float)(((VoiceManagerData.RadioData)voiceManagerData2.radioData.get(0)).x - ((VoiceManagerData.RadioData)voiceManagerData.radioData.get(0)).x);
						float float4 = (float)(((VoiceManagerData.RadioData)voiceManagerData2.radioData.get(0)).y - ((VoiceManagerData.RadioData)voiceManagerData.radioData.get(0)).y);
						((VoiceManagerData.RadioData)voiceManagerData2.radioData.get(0)).lastReceiveDistance = (float)Math.sqrt((double)(float3 * float3 + float4 * float4));
						if (((VoiceManagerData.RadioData)voiceManagerData2.radioData.get(0)).lastReceiveDistance < ((VoiceManagerData.RadioData)voiceManagerData.radioData.get(0)).distance) {
							radioData = (VoiceManagerData.RadioData)voiceManagerData2.radioData.get(0);
							return radioData;
						}
					}

					return null;
				}
			}
		}
	}

	public void UpdateChannelsRoaming(UdpConnection udpConnection) {
		IsoPlayer player = IsoPlayer.getInstance();
		if (player.OnlineID != -1) {
			VoiceManagerData voiceManagerData = VoiceManagerData.get(player.OnlineID);
			boolean boolean1 = false;
			synchronized (voiceManagerData.radioData) {
				voiceManagerData.radioData.clear();
				int int1 = 0;
				while (true) {
					if (int1 >= IsoPlayer.numPlayers) {
						break;
					}

					IsoPlayer player2 = IsoPlayer.players[int1];
					if (player2 != null) {
						boolean1 |= player2.isCanHearAll();
						voiceManagerData.radioData.add(new VoiceManagerData.RadioData(RakVoice.GetMaxDistance(), player2.x, player2.y));
						int int2;
						for (int2 = 0; int2 < player2.getInventory().getItems().size(); ++int2) {
							InventoryItem inventoryItem = (InventoryItem)player2.getInventory().getItems().get(int2);
							if (inventoryItem instanceof Radio) {
								DeviceData deviceData = ((Radio)inventoryItem).getDeviceData();
								if (deviceData != null && deviceData.getIsTurnedOn()) {
									voiceManagerData.radioData.add(new VoiceManagerData.RadioData(deviceData, player2.x, player2.y));
								}
							}
						}

						for (int2 = (int)player2.getX() - 4; (float)int2 < player2.getX() + 5.0F; ++int2) {
							for (int int3 = (int)player2.getY() - 4; (float)int3 < player2.getY() + 5.0F; ++int3) {
								for (int int4 = (int)player2.getZ() - 1; (float)int4 < player2.getZ() + 1.0F; ++int4) {
									IsoGridSquare square = IsoCell.getInstance().getGridSquare(int2, int3, int4);
									if (square != null) {
										int int5;
										DeviceData deviceData2;
										if (square.getObjects() != null) {
											for (int5 = 0; int5 < square.getObjects().size(); ++int5) {
												IsoObject object = (IsoObject)square.getObjects().get(int5);
												if (object instanceof IsoRadio) {
													deviceData2 = ((IsoRadio)object).getDeviceData();
													if (deviceData2 != null && deviceData2.getIsTurnedOn()) {
														voiceManagerData.radioData.add(new VoiceManagerData.RadioData(deviceData2, (float)square.x, (float)square.y));
													}
												}
											}
										}

										if (square.getWorldObjects() != null) {
											for (int5 = 0; int5 < square.getWorldObjects().size(); ++int5) {
												IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int5);
												if (worldInventoryObject.getItem() != null && worldInventoryObject.getItem() instanceof Radio) {
													deviceData2 = ((Radio)worldInventoryObject.getItem()).getDeviceData();
													if (deviceData2 != null && deviceData2.getIsTurnedOn()) {
														voiceManagerData.radioData.add(new VoiceManagerData.RadioData(deviceData2, (float)square.x, (float)square.y));
													}
												}
											}
										}
									}
								}
							}
						}
					}

					++int1;
				}
			}

			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.SyncRadioData.doPacket(byteBufferWriter);
			byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
			byteBufferWriter.putInt(voiceManagerData.radioData.size() * 4);
			Iterator iterator = voiceManagerData.radioData.iterator();
			while (iterator.hasNext()) {
				VoiceManagerData.RadioData radioData = (VoiceManagerData.RadioData)iterator.next();
				byteBufferWriter.putInt(radioData.freq);
				byteBufferWriter.putInt((int)radioData.distance);
				byteBufferWriter.putInt(radioData.x);
				byteBufferWriter.putInt(radioData.y);
			}

			PacketTypes.PacketType.SyncRadioData.send(udpConnection);
		}
	}

	void InitVMServer() {
		this.VoiceInitServer(ServerOptions.instance.VoiceEnable.getValue(), 24000, 20, 5, 8000, ServerOptions.instance.VoiceMinDistance.getValue(), ServerOptions.instance.VoiceMaxDistance.getValue(), ServerOptions.instance.Voice3D.getValue());
	}

	public int getMicVolumeIndicator() {
		return FMODReceiveBuffer == null ? 0 : (int)FMODReceiveBuffer.get_loudness();
	}

	public boolean getMicVolumeError() {
		return FMODReceiveBuffer == null ? true : FMODReceiveBuffer.get_interror();
	}

	public boolean getServerVOIPEnable() {
		return serverVOIPEnable;
	}

	public void VMServerBan(short short1, boolean boolean1) {
		RakVoice.SetVoiceBan((long)short1, boolean1);
	}

	static  {
		FMOD_SOUND_MODE = FMODManager.FMOD_OPENUSER | FMODManager.FMOD_LOOP_NORMAL | FMODManager.FMOD_CREATESTREAM;
		serverVOIPEnable = true;
		sampleRate = 16000;
		period = 300;
		buffering = 8000;
		is3D = false;
		VoipDisabled = false;
		instance = new VoiceManager();
		timestamp = 0L;
	}
}
