package zombie.core.raknet;

import fmod.FMODSoundBuffer;
import fmod.FMOD_DriverInfo;
import fmod.FMOD_RESULT;
import fmod.SoundBuffer;
import fmod.javafmod;
import fmod.fmod.FMODManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.input.GameKeyboard;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Radio;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;


public class VoiceManager {
	public static VoiceManager instance = new VoiceManager();
	private static int qulity;
	private static final int pcmsize = 2;
	private static final int bufferSize = 192;
	private static boolean serverVOIPEnable = true;
	private static int discretizate = 16000;
	private static int period = 300;
	private static int complexity = 1;
	private static int buffering = 8000;
	private static float minDistance;
	private static float maxDistance;
	private static boolean is3D = false;
	private Radio myRadio = null;
	private RakVoice voice;
	private int voice_bufsize;
	private long recSound;
	private static FMODSoundBuffer recBuf;
	private boolean startInit = false;
	private boolean initialiseRecDev = false;
	private boolean initialisedRecDev = false;
	private Semaphore RecDevSemaphore;
	private boolean isModeVAD = false;
	private boolean isModePPT = false;
	private int vadMode = 3;
	private int volumeMic;
	private int volumePlayers;
	public static boolean VoipDisabled = false;
	private boolean isEnable = true;
	private boolean isDebug = false;
	private boolean isDebugLoopback = false;
	private boolean isDebugLoopbackLong = false;
	private long fmod_channel_group_voip = 0L;
	private int FMODVoiceRecordDriverId;
	private byte[] serverbuf = null;
	private Thread thread;
	private boolean bQuit;
	private long time_last;
	private boolean isServer;
	private long indicator_is_voice = 0L;
	public static long tem_t;
	public static final int modePPT = 1;
	public static final int modeVAD = 2;
	public static final int modeMute = 3;
	public static final int VADModeQuality = 1;
	public static final int VADModeLowBitrate = 2;
	public static final int VADModeAggressive = 3;
	public static final int VADModeVeryAggressive = 4;
	byte[] buf = new byte[192];
	private final Long recBuf_Current_read = new Long(0L);
	private final Object notifier = new Object();
	private boolean bIsClient = false;
	private boolean bTestingMicrophone = false;
	private long testingMicrophoneMS = 0L;

	public static VoiceManager getInstance() {
		return instance;
	}

	int VoiceInitClient() {
		DebugLog.log("[VOICE MANAGER] VoiceInit");
		this.isServer = false;
		this.voice = new RakVoice();
		if (this.voice == null) {
			return -1;
		} else {
			this.RecDevSemaphore = new Semaphore(1);
			recBuf = null;
			this.voice_bufsize = 192;
			RakVoice rakVoice = this.voice;
			RakVoice.RVInit(this.voice_bufsize);
			rakVoice = this.voice;
			RakVoice.SetComplexity(complexity);
			return 0;
		}
	}

	int VoiceInitServer(boolean boolean1, int int1, int int2, int int3, int int4, double double1, double double2, boolean boolean2) {
		DebugLog.log("[VOICE MANAGER] VoiceInit");
		this.isServer = true;
		if (!(int2 == 2 | int2 == 5 | int2 == 10 | int2 == 20 | int2 == 40 | int2 == 60)) {
			DebugLog.log("[VOICE MANAGER] VoiceInit ERROR: invalid period");
			return -1;
		} else if (!(int1 == 8000 | int1 == 16000 | int1 == 24000)) {
			DebugLog.log("[VOICE MANAGER] VoiceInit ERROR: invalid samplerate");
			return -1;
		} else if (int3 < 0 | int3 > 10) {
			DebugLog.log("[VOICE MANAGER] VoiceInit ERROR: invalid qulity");
			return -1;
		} else if (int4 < 0 | int4 > 32000) {
			DebugLog.log("[VOICE MANAGER] VoiceInit ERROR: invalid buffering");
			return -1;
		} else {
			this.voice = new RakVoice();
			if (this.voice == null) {
				DebugLog.log("[VOICE MANAGER] VoiceInit ERROR: RakVoice internal error");
				return -1;
			} else {
				discretizate = int1;
				RakVoice rakVoice = this.voice;
				RakVoice.RVInitServer(boolean1, int1, int2, int3, int4, (float)double1, (float)double2, boolean2);
				return 0;
			}
		}
	}

	int VoiceDeinit() {
		DebugLog.log("[VOICE MANAGER] VoiceDeinit");
		RakVoice rakVoice = this.voice;
		RakVoice.CloseAllChannels();
		rakVoice = this.voice;
		RakVoice.RVDeinit();
		return 0;
	}

	int VoiceConnectAccept(long long1) {
		if (!this.isEnable) {
			return 0;
		} else {
			DebugLog.log("[VOICE MANAGER] VoiceConnectAccept uuid=" + long1);
			return 0;
		}
	}

	int InitRecDeviceForTest() {
		try {
			this.RecDevSemaphore.acquire();
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}

		this.recSound = javafmod.FMOD_System_CreateRecordSound((long)this.FMODVoiceRecordDriverId, (long)(FMODManager.FMOD_2D | FMODManager.FMOD_OPENUSER | FMODManager.FMOD_SOFTWARE), (long)FMODManager.FMOD_SOUND_FORMAT_PCM16, (long)discretizate);
		if (this.recSound == 0L) {
			DebugLog.log("[VOICE MANAGER] Error: FMOD_System_CreateSound return zero");
		}

		DebugLog.log("[VOICE MANAGER] FMOD_System_CreateSound OK");
		javafmod.FMOD_System_SetRecordVolume(1L - Math.round(Math.pow(1.4, (double)(11 - this.volumeMic))));
		if (this.initialiseRecDev) {
			int int1 = javafmod.FMOD_System_RecordStart(this.FMODVoiceRecordDriverId, this.recSound, true);
			if (int1 != FMOD_RESULT.FMOD_OK.ordinal()) {
				DebugLog.log("[VOICE MANAGER] Error: FMOD_System_RecordStart return " + int1);
			}
		}

		javafmod.FMOD_System_SetVADMode(this.vadMode - 1);
		recBuf = new FMODSoundBuffer(this.recSound);
		this.initialisedRecDev = true;
		this.RecDevSemaphore.release();
		return 0;
	}

	int VoiceOpenChannelReply(long long1) {
		if (!this.isEnable) {
			return 0;
		} else {
			DebugLog.log("[VOICE MANAGER] VoiceOpenChannelReply uuid=" + long1);
			if (this.isServer) {
				return 0;
			} else {
				try {
					this.RecDevSemaphore.acquire();
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}

				this.initialisedRecDev = false;
				RakVoice rakVoice = this.voice;
				serverVOIPEnable = RakVoice.GetServerVOIPEnable();
				rakVoice = this.voice;
				discretizate = RakVoice.GetSampleRate();
				rakVoice = this.voice;
				period = RakVoice.GetSendFramePeriod();
				rakVoice = this.voice;
				buffering = RakVoice.GetBuffering();
				rakVoice = this.voice;
				minDistance = RakVoice.GetMinDistance();
				rakVoice = this.voice;
				maxDistance = RakVoice.GetMaxDistance();
				rakVoice = this.voice;
				is3D = RakVoice.GetIs3D();
				ArrayList arrayList = VoiceManagerData.data;
				int int1;
				for (int1 = 0; int1 < arrayList.size(); ++int1) {
					VoiceManagerData voiceManagerData = (VoiceManagerData)arrayList.get(int1);
					if (voiceManagerData.userplaysound != 0L) {
						if (is3D) {
							javafmod.FMOD_Sound_SetMode(voiceManagerData.userplaysound, FMODManager.FMOD_3D | FMODManager.FMOD_OPENUSER | FMODManager.FMOD_LOOP_NORMAL | FMODManager.FMOD_CREATESTREAM);
						} else {
							javafmod.FMOD_Sound_SetMode(voiceManagerData.userplaysound, FMODManager.FMOD_OPENUSER | FMODManager.FMOD_LOOP_NORMAL | FMODManager.FMOD_CREATESTREAM);
						}
					}
				}

				DebugLog.log("[VOICE MANAGER] VoiceOpenChannelReply discretizate=" + discretizate);
				DebugLog.log("[VOICE MANAGER] VoiceOpenChannelReply period=" + period);
				DebugLog.log("[VOICE MANAGER] VoiceOpenChannelReply buffering=" + buffering);
				if (javafmod.FMOD_System_SetRawPlayBufferingPeriod((long)buffering) != (long)FMOD_RESULT.FMOD_OK.ordinal()) {
					DebugLog.log("[VOICE MANAGER] Error: FMOD_System_SetRawPlayBufferingPeriod ");
				}

				if (this.recSound != 0L) {
					int1 = javafmod.FMOD_Sound_Release(this.recSound);
					if (int1 != FMOD_RESULT.FMOD_OK.ordinal()) {
						DebugLog.log("[VOICE MANAGER] Error: FMOD_Sound_Release return " + int1);
					}

					this.recSound = 0L;
				}

				this.recSound = javafmod.FMOD_System_CreateRecordSound((long)this.FMODVoiceRecordDriverId, (long)(FMODManager.FMOD_2D | FMODManager.FMOD_OPENUSER | FMODManager.FMOD_SOFTWARE), (long)FMODManager.FMOD_SOUND_FORMAT_PCM16, (long)discretizate);
				if (this.recSound == 0L) {
					DebugLog.log("[VOICE MANAGER] Error: FMOD_System_CreateSound return zero");
				}

				DebugLog.log("[VOICE MANAGER] FMOD_System_CreateSound OK");
				javafmod.FMOD_System_SetRecordVolume(1L - Math.round(Math.pow(1.4, (double)(11 - this.volumeMic))));
				if (this.initialiseRecDev) {
					int1 = javafmod.FMOD_System_RecordStart(this.FMODVoiceRecordDriverId, this.recSound, true);
					if (int1 != FMOD_RESULT.FMOD_OK.ordinal()) {
						DebugLog.log("[VOICE MANAGER] Error: FMOD_System_RecordStart return " + int1);
					}
				}

				javafmod.FMOD_System_SetVADMode(this.vadMode - 1);
				recBuf = new FMODSoundBuffer(this.recSound);
				if (this.isDebug) {
					VoiceDebug.createAndShowGui();
				}

				this.initialisedRecDev = true;
				this.RecDevSemaphore.release();
				return 0;
			}
		}
	}

	int VoiceConnectReq(long long1) {
		if (!this.isEnable) {
			return 0;
		} else {
			DebugLog.log("[VOICE MANAGER] VoiceConnectReq uuid=" + long1);
			RakVoice rakVoice = this.voice;
			RakVoice.RequestVoiceChannel(long1);
			return 0;
		}
	}

	int VoiceConnectClose(long long1) {
		if (!this.isEnable) {
			return 0;
		} else {
			DebugLog.log("[VOICE MANAGER] VoiceConnectClose uuid=" + long1);
			RakVoice rakVoice = this.voice;
			RakVoice.CloseVoiceChannel(long1);
			return 0;
		}
	}

	public static void GetDataCallbackRnd(int int1, short[] shortArray) {
		Random random = new Random();
		for (int int2 = 0; int2 < shortArray.length; ++int2) {
			shortArray[int2] = (short)random.nextInt();
		}
	}

	public static void GetDataCallback100Hz(short[] shortArray) {
		DebugLog.log("[VOICE MANAGER] GetDataCallback: datasize=" + shortArray.length);
		for (int int1 = 0; int1 < shortArray.length; ++int1) {
			double double1 = 6.283185307179586 * ((double)tem_t / 8000.0) * 100.0;
			shortArray[int1] = (short)((int)(Math.sin(double1) * 16000.0));
			tem_t = (tem_t + 1L) % 8000L;
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

	private long getuserplaysound(int int1) {
		VoiceManagerData voiceManagerData = VoiceManagerData.get(int1);
		if (voiceManagerData.userplaychannel == 0L) {
			voiceManagerData.userplaysound = 0L;
			if (is3D) {
				voiceManagerData.userplaysound = javafmod.FMOD_System_CreateRAWPlaySound((long)(FMODManager.FMOD_3D | FMODManager.FMOD_OPENUSER | FMODManager.FMOD_LOOP_NORMAL | FMODManager.FMOD_CREATESTREAM), (long)FMODManager.FMOD_SOUND_FORMAT_PCM16, (long)discretizate);
			} else {
				voiceManagerData.userplaysound = javafmod.FMOD_System_CreateRAWPlaySound((long)(FMODManager.FMOD_OPENUSER | FMODManager.FMOD_LOOP_NORMAL | FMODManager.FMOD_CREATESTREAM), (long)FMODManager.FMOD_SOUND_FORMAT_PCM16, (long)discretizate);
			}

			if (voiceManagerData.userplaysound == 0L) {
				DebugLog.log("[VOICE MANAGER] Error: FMOD_System_CreateSound return zero");
			}

			voiceManagerData.userplaychannel = javafmod.FMOD_System_PlaySound(voiceManagerData.userplaysound, false);
			if (voiceManagerData.userplaychannel == 0L) {
				DebugLog.log("[VOICE MANAGER] Error: FMOD_System_PlaySound return zero");
			}

			javafmod.FMOD_Channel_SetVolume(voiceManagerData.userplaychannel, (float)((double)this.volumePlayers * 0.2));
			javafmod.FMOD_Channel_Set3DMinMaxDistance(voiceManagerData.userplaychannel, minDistance, maxDistance);
			javafmod.FMOD_Channel_SetChannelGroup(voiceManagerData.userplaychannel, this.fmod_channel_group_voip);
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
				DebugLog.log("[VOICE MANAGER] Any microphone not found");
				this.initialiseRecDev = false;
			} else if (this.FMODVoiceRecordDriverId < 0 | this.FMODVoiceRecordDriverId >= int1) {
				DebugLog.log("[VOICE MANAGER] Invalid record device");
				this.initialiseRecDev = false;
			} else {
				this.initialiseRecDev = true;
			}

			DebugLog.log("[VOICE MANAGER] Init: Start");
			this.isEnable = Core.getInstance().getOptionVoiceEnable();
			this.setMode(Core.getInstance().getOptionVoiceMode());
			this.vadMode = Core.getInstance().getOptionVoiceVADMode();
			this.volumeMic = Core.getInstance().getOptionVoiceVolumeMic();
			this.volumePlayers = Core.getInstance().getOptionVoiceVolumePlayers();
			if (!this.isEnable) {
				DebugLog.log("[VOICE MANAGER] Disabled");
			} else {
				this.fmod_channel_group_voip = javafmod.FMOD_System_CreateChannelGroup("VOIP");
				this.VoiceInitClient();
				this.recSound = 0L;
				this.InitRecDeviceForTest();
				if (this.isDebug) {
					VoiceDebug.createAndShowGui();
				}

				DebugLog.log("[VOICE MANAGER] Init: End");
				this.time_last = System.currentTimeMillis();
				this.bQuit = false;
				this.thread = new Thread(){
					
					public void run() {
						while (!VoiceManager.this.bQuit && !VoiceManager.this.bQuit) {
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
			}
		} else {
			this.isEnable = false;
			this.initialiseRecDev = false;
			this.initialisedRecDev = false;
			DebugLog.log("[VOICE MANAGER] Init: Disable");
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
				DebugLog.log("[VOICE MANAGER] Error: FMOD_System_RecordStop return " + int1);
			}

			this.FMODVoiceRecordDriverId = Core.getInstance().getOptionVoiceRecordDevice() - 1;
			if (this.FMODVoiceRecordDriverId < 0) {
				DebugLog.log("[VOICE MANAGER] Error: No record device found");
			} else {
				int1 = javafmod.FMOD_System_RecordStart(this.FMODVoiceRecordDriverId, this.recSound, true);
				if (int1 != FMOD_RESULT.FMOD_OK.ordinal()) {
					DebugLog.log("[VOICE MANAGER] Error: FMOD_System_RecordStart return " + int1);
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

		if (this.recSound != 0L) {
			javafmod.FMOD_RecordSound_Release(this.recSound);
		}

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

	void debug_print(byte[] byteArray, Long Long1) {
		long[] longArray = new long[16];
		long long1 = Long1 / 16L;
		for (int int1 = 0; int1 < 16; ++int1) {
			longArray[int1] = 0L;
			for (int int2 = 0; (long)int2 < long1; ++int2) {
				longArray[int1] += (long)byteArray[int1 * (int)long1 + int2];
			}
		}

		DebugLog.log("[VOICE MANAGER] UpdateVMClient: loc abuf1: " + longArray[0] + ", " + longArray[2] + ", " + longArray[3] + ", " + longArray[4] + ", " + longArray[5] + ", " + longArray[6] + ", " + longArray[7] + ", " + longArray[8] + ", " + longArray[9] + ", " + longArray[10] + ", " + longArray[11] + ", " + longArray[12] + ", " + longArray[13] + ", " + longArray[14] + ", " + longArray[15]);
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

			if (GameClient.bClient && GameClient.connection != null) {
				if (!this.bIsClient) {
					this.bIsClient = true;
					this.notifyThread();
				}
			} else if (this.bIsClient) {
				this.bIsClient = false;
				this.notifyThread();
			}
		}
	}

	synchronized void UpdateVMClient() throws InterruptedException {
		for (; !this.bQuit && !this.bIsClient && !this.bTestingMicrophone; DebugLog.log("[VOICE MANAGER] UpdateVMClient woke up")) {
			DebugLog.log("[VOICE MANAGER] UpdateVMClient going to sleep");
			synchronized (this.notifier) {
				try {
					this.notifier.wait();
				} catch (InterruptedException interruptedException) {
				}
			}
		}

		if (serverVOIPEnable) {
			if (IsoPlayer.getInstance() != null) {
				IsoPlayer.getInstance().isSpeek = System.currentTimeMillis() - this.indicator_is_voice <= 300L;
			}

			GameClient gameClient;
			RakVoice rakVoice;
			if (this.initialiseRecDev) {
				this.RecDevSemaphore.acquire();
				javafmod.FMOD_System_GetRecordPosition(this.FMODVoiceRecordDriverId, this.recBuf_Current_read);
				if (recBuf != null) {
					label184: while (true) {
						while (true) {
							if (!recBuf.pull(this.recBuf_Current_read)) {
								break label184;
							}

							if (IsoPlayer.getInstance() == null) {
								break;
							}

							gameClient = GameClient.instance;
							if (GameClient.connection == null) {
								break;
							}

							if (!is3D || !IsoPlayer.getInstance().isDead()) {
								if (this.isModePPT && GameKeyboard.isKeyDown(Core.getInstance().getKey("Enable voice transmit"))) {
									rakVoice = this.voice;
									gameClient = GameClient.instance;
									RakVoice.SendFrame(GameClient.connection.connectedGUID, (long)IsoPlayer.getInstance().OnlineID, recBuf.buf(), recBuf.get_size());
									this.indicator_is_voice = System.currentTimeMillis();
								}

								if (this.isModeVAD && recBuf.get_vad() != 0L) {
									rakVoice = this.voice;
									gameClient = GameClient.instance;
									RakVoice.SendFrame(GameClient.connection.connectedGUID, (long)IsoPlayer.getInstance().OnlineID, recBuf.buf(), recBuf.get_size());
									this.indicator_is_voice = System.currentTimeMillis();
								}

								break;
							}
						}

						if (this.isDebug) {
							if (GameClient.IDToPlayerMap.values().size() > 0) {
								VoiceDebug.updateGui((SoundBuffer)null, recBuf);
							} else if (this.isDebugLoopback) {
								VoiceDebug.updateGui((SoundBuffer)null, recBuf);
							} else {
								VoiceDebug.updateGui((SoundBuffer)null, recBuf);
							}
						}

						if (this.isDebugLoopback) {
							javafmod.FMOD_System_RAWPlayData(this.getuserplaysound(0), recBuf.buf(), recBuf.get_size());
						}
					}
				}

				this.RecDevSemaphore.release();
			}

			ArrayList arrayList = GameClient.instance.getPlayers();
			ArrayList arrayList2 = VoiceManagerData.data;
			int int1;
			for (int1 = 0; int1 < arrayList2.size(); ++int1) {
				VoiceManagerData voiceManagerData = (VoiceManagerData)arrayList2.get(int1);
				boolean boolean1 = false;
				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					IsoPlayer player = (IsoPlayer)arrayList.get(int2);
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

			long long1 = System.currentTimeMillis() - this.time_last;
			if (long1 >= (long)period) {
				this.time_last += long1;
				if (IsoPlayer.getInstance() == null) {
					return;
				}

				for (int1 = 0; int1 < arrayList.size(); ++int1) {
					IsoPlayer player2 = (IsoPlayer)arrayList.get(int1);
					if (player2 != IsoPlayer.getInstance()) {
						VoiceManagerData voiceManagerData2 = VoiceManagerData.get(player2.OnlineID);
						while (true) {
							rakVoice = this.voice;
							if (!RakVoice.ReceiveFrame((long)player2.OnlineID, this.buf)) {
								if (voiceManagerData2.voicetimeout == 0L) {
									player2.isSpeek = false;
								} else {
									--voiceManagerData2.voicetimeout;
									player2.isSpeek = true;
								}

								break;
							}

							voiceManagerData2.voicetimeout = 10L;
							if (!voiceManagerData2.userplaymute) {
								if (IsoPlayer.getInstance().isCanHearAll()) {
									javafmod.FMOD_Channel_Set3DAttributes(voiceManagerData2.userplaychannel, IsoPlayer.getInstance().x, IsoPlayer.getInstance().y, IsoPlayer.getInstance().z, 0.0F, 0.0F, 0.0F);
								} else if (is3D) {
									ArrayList arrayList3 = this.checkForNearbyRadios(player2);
									boolean boolean2 = true;
									if (this.myRadio != null) {
										javafmod.FMOD_Channel_SetVolume(voiceManagerData2.userplaychannel, this.myRadio.getDeviceData().getDeviceVolume());
									} else {
										javafmod.FMOD_Channel_SetVolume(voiceManagerData2.userplaychannel, 1.0F);
									}

									if (!arrayList3.isEmpty()) {
										javafmod.FMOD_Channel_Set3DAttributes(voiceManagerData2.userplaychannel, (float)(Integer)arrayList3.get(0), (float)(Integer)arrayList3.get(1), (float)(Integer)arrayList3.get(2), 0.0F, 0.0F, 0.0F);
									} else {
										javafmod.FMOD_Channel_Set3DAttributes(voiceManagerData2.userplaychannel, player2.x, player2.y, player2.z, 0.0F, 0.0F, 0.0F);
									}
								}

								javafmod.FMOD_System_RAWPlayData(this.getuserplaysound(player2.OnlineID), this.buf, (long)this.buf.length);
								if (this.isDebugLoopbackLong) {
									rakVoice = this.voice;
									gameClient = GameClient.instance;
									RakVoice.SendFrame(GameClient.connection.connectedGUID, (long)IsoPlayer.getInstance().OnlineID, this.buf, (long)this.buf.length);
								}
							}
						}
					}
				}
			}
		}
	}

	private ArrayList checkForNearbyRadios(IsoPlayer player) {
		ArrayList arrayList = new ArrayList();
		this.myRadio = null;
		IsoPlayer player2 = IsoPlayer.getInstance();
		int int1;
		for (int1 = 0; int1 < player2.getInventory().getItems().size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)player2.getInventory().getItems().get(int1);
			if (inventoryItem instanceof Radio) {
				Radio radio = (Radio)inventoryItem;
				if (radio.getDeviceData() != null && radio.getDeviceData().getIsTurnedOn() && player.invRadioFreq.contains(radio.getDeviceData().getChannel())) {
					arrayList.add((int)player2.x);
					arrayList.add((int)player2.y);
					arrayList.add((int)player2.z);
					this.myRadio = radio;
					break;
				}
			}
		}

		if (arrayList.isEmpty()) {
			for (int1 = (int)player2.getX() - 4; (float)int1 < player2.getX() + 5.0F; ++int1) {
				for (int int2 = (int)player2.getY() - 4; (float)int2 < player2.getY() + 5.0F; ++int2) {
					for (int int3 = (int)player2.getZ() - 1; (float)int3 < player2.getZ() + 1.0F; ++int3) {
						IsoGridSquare square = IsoCell.getInstance().getGridSquare(int1, int2, int3);
						if (square != null && square.getWorldObjects() != null) {
							for (int int4 = 0; int4 < square.getWorldObjects().size(); ++int4) {
								IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int4);
								if (worldInventoryObject.getItem() != null && worldInventoryObject.getItem() instanceof Radio) {
									Radio radio2 = (Radio)worldInventoryObject.getItem();
									if (radio2.getDeviceData() != null && radio2.getDeviceData().getIsTurnedOn() && player.invRadioFreq.contains(radio2.getDeviceData().getChannel())) {
										arrayList.add(square.x);
										arrayList.add(square.y);
										arrayList.add(square.z);
										this.myRadio = radio2;
										break;
									}
								}
							}
						}
					}
				}
			}
		}

		return arrayList;
	}

	void InitVMServer() {
		this.VoiceInitServer(ServerOptions.instance.VoiceEnable.getValue(), ServerOptions.instance.VoiceSampleRate.getValue(), ServerOptions.instance.VoicePeriod.getValue(), ServerOptions.instance.VoiceComplexity.getValue(), ServerOptions.instance.VoiceBuffering.getValue(), ServerOptions.instance.VoiceMinDistance.getValue(), ServerOptions.instance.VoiceMaxDistance.getValue(), ServerOptions.instance.Voice3D.getValue());
	}

	public int getMicVolumeIndicator() {
		return recBuf == null ? 0 : (int)recBuf.get_loudness();
	}

	public boolean getMicVolumeError() {
		return recBuf == null ? true : recBuf.get_interror();
	}

	public boolean getServerVOIPEnable() {
		return serverVOIPEnable;
	}

	public void VMServerBan(int int1, boolean boolean1) {
		RakVoice rakVoice = this.voice;
		RakVoice.SetVoiceBan((long)int1, boolean1);
	}
}
