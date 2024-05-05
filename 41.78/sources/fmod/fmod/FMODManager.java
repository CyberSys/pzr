package fmod.fmod;

import fmod.javafmod;
import fmod.javafmodJNI;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import zombie.ZomboidFileSystem;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.iso.Vector2;


public class FMODManager {
	public static FMODManager instance = new FMODManager();
	public static int FMOD_STUDIO_INIT_NORMAL = 0;
	public static int FMOD_STUDIO_INIT_LIVEUPDATE = 1;
	public static int FMOD_STUDIO_INIT_ALLOW_MISSING_PLUGINS = 2;
	public static int FMOD_STUDIO_INIT_SYNCHRONOUS_UPDATE = 4;
	public static int FMOD_STUDIO_INIT_DEFERRED_CALLBACKS = 8;
	public static int FMOD_INIT_NORMAL = 0;
	public static int FMOD_INIT_STREAM_FROM_UPDATE = 1;
	public static int FMOD_INIT_MIX_FROM_UPDATE = 2;
	public static int FMOD_INIT_3D_RIGHTHANDED = 4;
	public static int FMOD_INIT_CHANNEL_LOWPASS = 256;
	public static int FMOD_INIT_CHANNEL_DISTANCEFILTER = 512;
	public static int FMOD_INIT_PROFILE_ENABLE = 65536;
	public static int FMOD_INIT_VOL0_BECOMES_VIRTUAL = 131072;
	public static int FMOD_INIT_GEOMETRY_USECLOSEST = 262144;
	public static int FMOD_INIT_PREFER_DOLBY_DOWNMIX = 524288;
	public static int FMOD_INIT_THREAD_UNSAFE = 1048576;
	public static int FMOD_INIT_PROFILE_METER_ALL = 2097152;
	public static int FMOD_DEFAULT = 0;
	public static int FMOD_LOOP_OFF = 1;
	public static int FMOD_LOOP_NORMAL = 2;
	public static int FMOD_LOOP_BIDI = 4;
	public static int FMOD_2D = 8;
	public static int FMOD_3D = 16;
	public static int FMOD_HARDWARE = 32;
	public static int FMOD_SOFTWARE = 64;
	public static int FMOD_CREATESTREAM = 128;
	public static int FMOD_CREATESAMPLE = 256;
	public static int FMOD_CREATECOMPRESSEDSAMPLE = 512;
	public static int FMOD_OPENUSER = 1024;
	public static int FMOD_OPENMEMORY = 2048;
	public static int FMOD_OPENMEMORY_POINT = 268435456;
	public static int FMOD_OPENRAW = 4096;
	public static int FMOD_OPENONLY = 8192;
	public static int FMOD_ACCURATETIME = 16384;
	public static int FMOD_MPEGSEARCH = 32768;
	public static int FMOD_NONBLOCKING = 65536;
	public static int FMOD_UNIQUE = 131072;
	public static int FMOD_3D_HEADRELATIVE = 262144;
	public static int FMOD_3D_WORLDRELATIVE = 524288;
	public static int FMOD_3D_INVERSEROLLOFF = 1048576;
	public static int FMOD_3D_LINEARROLLOFF = 2097152;
	public static int FMOD_3D_LINEARSQUAREROLLOFF = 4194304;
	public static int FMOD_3D_INVERSETAPEREDROLLOFF = 8388608;
	public static int FMOD_3D_CUSTOMROLLOFF = 67108864;
	public static int FMOD_3D_IGNOREGEOMETRY = 1073741824;
	public static int FMOD_IGNORETAGS = 33554432;
	public static int FMOD_LOWMEM = 134217728;
	public static int FMOD_LOADSECONDARYRAM = 536870912;
	public static int FMOD_VIRTUAL_PLAYFROMSTART = Integer.MIN_VALUE;
	public static int FMOD_PRESET_OFF = 0;
	public static int FMOD_PRESET_GENERIC = 1;
	public static int FMOD_PRESET_PADDEDCELL = 2;
	public static int FMOD_PRESET_ROOM = 3;
	public static int FMOD_PRESET_BATHROOM = 4;
	public static int FMOD_PRESET_LIVINGROOM = 5;
	public static int FMOD_PRESET_STONEROOM = 6;
	public static int FMOD_PRESET_AUDITORIUM = 7;
	public static int FMOD_PRESET_CONCERTHALL = 8;
	public static int FMOD_PRESET_CAVE = 9;
	public static int FMOD_PRESET_ARENA = 10;
	public static int FMOD_PRESET_HANGAR = 11;
	public static int FMOD_PRESET_CARPETTEDHALLWAY = 12;
	public static int FMOD_PRESET_HALLWAY = 13;
	public static int FMOD_PRESET_STONECORRIDOR = 14;
	public static int FMOD_PRESET_ALLEY = 15;
	public static int FMOD_PRESET_FOREST = 16;
	public static int FMOD_PRESET_CITY = 17;
	public static int FMOD_PRESET_MOUNTAINS = 18;
	public static int FMOD_PRESET_QUARRY = 19;
	public static int FMOD_PRESET_PLAIN = 20;
	public static int FMOD_PRESET_PARKINGLOT = 21;
	public static int FMOD_PRESET_SEWERPIPE = 22;
	public static int FMOD_PRESET_UNDERWATER = 23;
	public static int FMOD_TIMEUNIT_MS = 1;
	public static int FMOD_TIMEUNIT_PCM = 2;
	public static int FMOD_TIMEUNIT_PCMBYTES = 4;
	public static int FMOD_STUDIO_PLAYBACK_PLAYING = 0;
	public static int FMOD_STUDIO_PLAYBACK_SUSTAINING = 1;
	public static int FMOD_STUDIO_PLAYBACK_STOPPED = 2;
	public static int FMOD_STUDIO_PLAYBACK_STARTING = 3;
	public static int FMOD_STUDIO_PLAYBACK_STOPPING = 4;
	public static int FMOD_SOUND_FORMAT_NONE = 0;
	public static int FMOD_SOUND_FORMAT_PCM8 = 1;
	public static int FMOD_SOUND_FORMAT_PCM16 = 2;
	public static int FMOD_SOUND_FORMAT_PCM24 = 3;
	public static int FMOD_SOUND_FORMAT_PCM32 = 4;
	public static int FMOD_SOUND_FORMAT_PCMFLOAT = 5;
	public static int FMOD_SOUND_FORMAT_BITSTREAM = 6;
	ArrayList Sounds = new ArrayList();
	ArrayList Instances = new ArrayList();
	public long channelGroupInGameNonBankSounds = 0L;
	private final HashMap parameterDescriptionMap = new HashMap();
	private final HashMap eventDescriptionMap = new HashMap();
	int c = 0;
	Vector2 move = new Vector2(0.0F, 0.0F);
	Vector2 pos = new Vector2(-400.0F, -400.0F);
	float x = 0.0F;
	float y = 0.0F;
	float z = 0.0F;
	float vx = 0.02F;
	float vy = 0.01F;
	float vz = 0.0F;
	private int numListeners = 1;
	private final HashMap fileToSoundMap = new HashMap();
	private final int[] reverbPreset = new int[]{-1, -1, -1, -1};

	public void init() {
		javafmodJNI.init();
		int int1 = javafmod.FMOD_System_Create();
		if (int1 != 0) {
			Core.SoundDisabled = true;
		} else {
			int int2 = Core.bDebug ? FMOD_STUDIO_INIT_LIVEUPDATE : 0;
			int int3 = Core.bDebug ? FMOD_INIT_PROFILE_ENABLE | FMOD_INIT_PROFILE_METER_ALL : 0;
			int1 = javafmod.FMOD_System_Init(1024, (long)(FMOD_STUDIO_INIT_NORMAL | int2), (long)(FMOD_INIT_NORMAL | FMOD_INIT_CHANNEL_DISTANCEFILTER | FMOD_INIT_CHANNEL_LOWPASS | FMOD_INIT_VOL0_BECOMES_VIRTUAL | int3));
			if (int1 != 0) {
				Core.SoundDisabled = true;
			} else {
				javafmod.FMOD_System_Set3DSettings(1.0F, 1.0F, 1.0F);
				this.loadBank("ZomboidSound.strings");
				this.loadBank("ZomboidMusic");
				this.loadBank("ZomboidSound");
				this.loadBank("ZomboidSoundMP");
				this.channelGroupInGameNonBankSounds = javafmod.FMOD_System_CreateChannelGroup("InGameNonBank");
				this.initGlobalParameters();
				this.initEvents();
			}
		}
	}

	private String bankPath(String string) throws IOException {
		return (new File("./media/sound/banks/Desktop/" + string + ".bank")).getCanonicalFile().getPath();
	}

	private long loadBank(String string) {
		try {
			String string2 = this.bankPath(string);
			long long1 = javafmod.FMOD_Studio_System_LoadBankFile(string2);
			if (long1 > 0L) {
				javafmod.FMOD_Studio_LoadSampleData(long1);
			}

			return long1;
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
			return 0L;
		}
	}

	private void loadZombieTest() {
		long long1 = javafmod.FMOD_Studio_System_LoadBankFile("media/sound/banks/chopper.bank");
		long long2 = javafmod.FMOD_Studio_System_LoadBankFile("media/sound/banks/chopper.strings.bank");
		javafmod.FMOD_Studio_LoadSampleData(long1);
		long long3 = javafmod.FMOD_Studio_System_GetEvent("{5deff1b6-984c-42e0-98ec-c133a83d0513}");
		long long4 = javafmod.FMOD_Studio_System_GetEvent("{c00fed39-230a-4c6a-b9c0-b0924876f53a}");
		javafmod.FMOD_Studio_LoadEventSampleData(long3);
		javafmod.FMOD_Studio_LoadEventSampleData(long4);
		short short1 = 2000;
		short short2 = 2000;
		SoundListener soundListener = new SoundListener(0);
		soundListener.x = (float)short1;
		soundListener.y = (float)short2;
		soundListener.tick();
		boolean boolean1 = false;
		ArrayList arrayList = new ArrayList();
		ArrayList arrayList2 = new ArrayList();
		FMODManager.TestZombieInfo testZombieInfo = new FMODManager.TestZombieInfo(long3, (float)(short1 - 5), (float)(short2 - 5));
		javafmod.FMOD_Studio_EventInstance_SetParameterByName(testZombieInfo.inst, "Pitch", (float)Rand.Next(200) / 100.0F - 1.0F);
		javafmod.FMOD_Studio_EventInstance_SetParameterByName(testZombieInfo.inst, "Aggitation", 0.0F);
		arrayList.add(testZombieInfo);
		++this.c;
		while (!boolean1) {
			int int1;
			FMODManager.TestZombieInfo testZombieInfo2;
			for (int1 = 0; int1 < arrayList.size(); ++int1) {
				testZombieInfo2 = (FMODManager.TestZombieInfo)arrayList.get(int1);
				if (Rand.Next(1000) == 0) {
					--this.c;
					arrayList2.add(testZombieInfo2);
					arrayList.remove(testZombieInfo2);
					float float1 = (float)(Rand.Next(40) + 60) / 100.0F;
					javafmod.FMOD_Studio_EventInstance_SetParameterByName(testZombieInfo2.inst, "Aggitation", float1);
					--int1;
				}
			}

			for (int1 = 0; int1 < arrayList2.size(); ++int1) {
				testZombieInfo2 = (FMODManager.TestZombieInfo)arrayList2.get(int1);
				Vector2 vector2 = new Vector2((float)short1 - testZombieInfo2.X, (float)short2 - testZombieInfo2.Y);
				if (vector2.getLength() < 2.0F) {
					arrayList2.remove(testZombieInfo2);
					javafmod.FMOD_Studio_EventInstance_Stop(testZombieInfo2.inst, false);
				}

				vector2.setLength(0.01F);
				testZombieInfo2.X += vector2.x;
				testZombieInfo2.Y += vector2.y;
				javafmod.FMOD_Studio_EventInstance3D(testZombieInfo2.inst, testZombieInfo2.X, testZombieInfo2.Y, 0.0F);
			}

			javafmod.FMOD_System_Update();
			try {
				Thread.sleep(10L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
	}

	private void loadTestEvent() {
		long long1 = javafmod.FMOD_Studio_System_LoadBankFile("media/sound/banks/chopper.bank");
		javafmod.FMOD_Studio_LoadSampleData(long1);
		long long2 = javafmod.FMOD_Studio_System_GetEvent("{47d0c496-7d0a-48e9-9bad-1c8fcf292986}");
		javafmod.FMOD_Studio_LoadEventSampleData(long2);
		long long3 = javafmod.FMOD_Studio_System_CreateEventInstance(long2);
		javafmod.FMOD_Studio_EventInstance3D(long3, this.pos.x, this.pos.y, 100.0F);
		javafmod.FMOD_Studio_Listener3D(0, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F);
		javafmod.FMOD_Studio_StartEvent(long3);
		int int1 = 0;
		boolean boolean1 = false;
		while (!boolean1) {
			if (int1 > 200) {
				this.pos.x = (float)(Rand.Next(400) - 200);
				this.pos.y = (float)(Rand.Next(400) - 200);
				if (Rand.Next(3) == 0) {
					Vector2 vector2 = this.pos;
					vector2.x /= 4.0F;
					vector2 = this.pos;
					vector2.y /= 4.0F;
				}

				javafmod.FMOD_Studio_StartEvent(long3);
				javafmod.FMOD_Studio_EventInstance3D(long3, this.pos.x, this.pos.y, 0.0F);
				int1 = 0;
			}

			++int1;
			javafmod.FMOD_System_Update();
			try {
				Thread.sleep(10L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
	}

	public void loadTest() {
		long long1 = javafmod.FMOD_System_CreateSound("media/sound/PZ_FemaleBeingEaten_Death.wav", (long)FMOD_3D);
		javafmod.FMOD_Sound_Set3DMinMaxDistance(long1, 0.005F, 100.0F);
		this.Sounds.add(long1);
		this.playTest();
	}

	public void playTest() {
		long long1 = (Long)this.Sounds.get(0);
		long long2 = javafmod.FMOD_System_PlaySound(long1, true);
		javafmod.FMOD_Channel_Set3DAttributes(long2, 10.0F, 10.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		javafmod.FMOD_Channel_SetPaused(long2, false);
		this.Instances.add(long2);
	}

	public void applyDSP() {
		javafmod.FMOD_System_PlayDSP();
	}

	public void tick() {
		if (Rand.Next(100) == 0) {
			this.vx = -this.vx;
		}

		if (Rand.Next(100) == 0) {
			this.vy = -this.vy;
		}

		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.z;
		this.x += this.vx;
		this.y += this.vy;
		this.z += this.vz;
		int int1;
		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			long long1 = (Long)this.Instances.get(int1);
			javafmod.FMOD_Channel_Set3DAttributes(long1, this.x, this.y, this.z, this.x - float1, this.y - float2, this.z - float3);
			float float4 = 40.0F;
			float float5 = (Math.abs(this.x) + Math.abs(this.y)) / float4;
			if (float5 < 0.1F) {
				float5 = 0.1F;
			}

			if (float5 > 1.0F) {
				float5 = 1.0F;
			}

			float5 *= float5;
			float5 *= 40.0F;
			javafmod.FMOD_Channel_SetReverbProperties(long1, 0, float5);
		}

		int1 = 0;
		for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
			IsoPlayer player = IsoPlayer.players[int2];
			if (player != null) {
				++int1;
			}
		}

		if (int1 > 0) {
			if (int1 != this.numListeners) {
				javafmod.FMOD_Studio_SetNumListeners(int1);
			}
		} else if (this.numListeners != 1) {
			javafmod.FMOD_Studio_SetNumListeners(1);
		}

		this.numListeners = int1;
		this.updateReverbPreset();
		javafmod.FMOD_System_Update();
	}

	public int getNumListeners() {
		return this.numListeners;
	}

	public long loadSound(String string) {
		string = ZomboidFileSystem.instance.getAbsolutePath(string);
		if (string == null) {
			return 0L;
		} else {
			Long Long1 = (Long)this.fileToSoundMap.get(string);
			if (Long1 != null) {
				return Long1;
			} else {
				Long1 = javafmod.FMOD_System_CreateSound(string, (long)FMOD_3D);
				if (Core.bDebug && Long1 == 0L) {
					DebugLog.log("ERROR: failed to load sound " + string);
				}

				this.fileToSoundMap.put(string, Long1);
				return Long1;
			}
		}
	}

	public void stopSound(long long1) {
		if (long1 != 0L) {
			javafmod.FMOD_Channel_Stop(long1);
		}
	}

	public boolean isPlaying(long long1) {
		return javafmod.FMOD_Channel_IsPlaying(long1);
	}

	public long loadSound(String string, boolean boolean1) {
		string = ZomboidFileSystem.instance.getAbsolutePath(string);
		if (string == null) {
			return 0L;
		} else {
			Long Long1 = (Long)this.fileToSoundMap.get(string);
			if (Long1 != null) {
				return Long1;
			} else {
				if (!boolean1) {
					Long1 = javafmod.FMOD_System_CreateSound(string, (long)FMOD_3D);
				} else {
					Long1 = javafmod.FMOD_System_CreateSound(string, (long)(FMOD_3D | FMOD_LOOP_NORMAL));
				}

				this.fileToSoundMap.put(string, Long1);
				return Long1;
			}
		}
	}

	public void updateReverbPreset() {
		boolean boolean1 = true;
		boolean boolean2 = true;
		boolean boolean3 = true;
		int int1 = FMOD_PRESET_FOREST;
		int int2 = FMOD_PRESET_CITY;
		int int3 = FMOD_PRESET_OFF;
		int1 = FMOD_PRESET_OFF;
		int2 = FMOD_PRESET_OFF;
		if (this.reverbPreset[0] != int3) {
			javafmod.FMOD_System_SetReverbDefault(0, int3);
			this.reverbPreset[0] = int3;
		}

		if (this.reverbPreset[1] != int1) {
			javafmod.FMOD_System_SetReverbDefault(1, int1);
			this.reverbPreset[1] = int1;
		}

		if (this.reverbPreset[2] != int2) {
			javafmod.FMOD_System_SetReverbDefault(2, int2);
			this.reverbPreset[2] = int2;
		}
	}

	private void initGlobalParameters() {
		int int1 = javafmodJNI.FMOD_Studio_System_GetParameterDescriptionCount();
		if (int1 > 0) {
			long[] longArray = new long[int1];
			int1 = javafmodJNI.FMOD_Studio_System_GetParameterDescriptionList(longArray);
			for (int int2 = 0; int2 < int1; ++int2) {
				long long1 = longArray[int2];
				this.initParameterDescription(long1);
				javafmodJNI.FMOD_Studio_ParameterDescription_Free(long1);
			}
		}
	}

	private FMOD_STUDIO_PARAMETER_DESCRIPTION initParameterDescription(long long1) {
		String string = javafmodJNI.FMOD_Studio_ParameterDescription_GetName(long1);
		FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION = (FMOD_STUDIO_PARAMETER_DESCRIPTION)this.parameterDescriptionMap.get(string);
		if (fMOD_STUDIO_PARAMETER_DESCRIPTION == null) {
			int int1 = javafmodJNI.FMOD_Studio_ParameterDescription_GetFlags(long1);
			long long2 = javafmodJNI.FMOD_Studio_ParameterDescription_GetID(long1);
			FMOD_STUDIO_PARAMETER_ID fMOD_STUDIO_PARAMETER_ID = new FMOD_STUDIO_PARAMETER_ID(long2);
			fMOD_STUDIO_PARAMETER_DESCRIPTION = new FMOD_STUDIO_PARAMETER_DESCRIPTION(string, fMOD_STUDIO_PARAMETER_ID, int1, this.parameterDescriptionMap.size());
			this.parameterDescriptionMap.put(fMOD_STUDIO_PARAMETER_DESCRIPTION.name, fMOD_STUDIO_PARAMETER_DESCRIPTION);
			boolean boolean1;
			if ((fMOD_STUDIO_PARAMETER_DESCRIPTION.flags & FMOD_STUDIO_PARAMETER_FLAGS.FMOD_STUDIO_PARAMETER_GLOBAL.bit) != 0) {
				boolean1 = true;
			} else {
				boolean1 = true;
			}
		}

		return fMOD_STUDIO_PARAMETER_DESCRIPTION;
	}

	private void initEvents() {
		int int1 = javafmodJNI.FMOD_Studio_System_GetBankCount();
		if (int1 > 0) {
			long[] longArray = new long[int1];
			int1 = javafmodJNI.FMOD_Studio_System_GetBankList(longArray);
			for (int int2 = 0; int2 < int1; ++int2) {
				int int3 = javafmodJNI.FMOD_Studio_Bank_GetEventCount(longArray[int2]);
				if (int3 > 0) {
					long[] longArray2 = new long[int3];
					int3 = javafmodJNI.FMOD_Studio_Bank_GetEventList(longArray[int2], longArray2);
					for (int int4 = 0; int4 < int3; ++int4) {
						this.initEvent(longArray2[int4]);
					}
				}
			}
		}
	}

	private void initEvent(long long1) {
		String string = javafmodJNI.FMOD_Studio_EventDescription_GetPath(long1);
		long long2 = javafmodJNI.FMOD_Studio_EventDescription_GetID(long1);
		FMOD_GUID fMOD_GUID = new FMOD_GUID(long2);
		boolean boolean1 = javafmodJNI.FMOD_Studio_EventDescription_HasSustainPoint(long1);
		long long3 = javafmodJNI.FMOD_Studio_EventDescription_GetLength(long1);
		FMOD_STUDIO_EVENT_DESCRIPTION fMOD_STUDIO_EVENT_DESCRIPTION = new FMOD_STUDIO_EVENT_DESCRIPTION(long1, string, fMOD_GUID, boolean1, long3);
		this.eventDescriptionMap.put(fMOD_STUDIO_EVENT_DESCRIPTION.path.toLowerCase(Locale.ENGLISH), fMOD_STUDIO_EVENT_DESCRIPTION);
		int int1 = javafmodJNI.FMOD_Studio_EventDescription_GetParameterDescriptionCount(long1);
		for (int int2 = 0; int2 < int1; ++int2) {
			long long4 = javafmodJNI.FMOD_Studio_EventDescription_GetParameterDescriptionByIndex(long1, int2);
			FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION = this.initParameterDescription(long4);
			fMOD_STUDIO_EVENT_DESCRIPTION.parameters.add(fMOD_STUDIO_PARAMETER_DESCRIPTION);
			javafmodJNI.FMOD_Studio_ParameterDescription_Free(long4);
		}
	}

	public FMOD_STUDIO_EVENT_DESCRIPTION getEventDescription(String string) {
		return (FMOD_STUDIO_EVENT_DESCRIPTION)this.eventDescriptionMap.get(string.toLowerCase(Locale.ENGLISH));
	}

	public FMOD_STUDIO_PARAMETER_DESCRIPTION getParameterDescription(String string) {
		return (FMOD_STUDIO_PARAMETER_DESCRIPTION)this.parameterDescriptionMap.get(string);
	}

	public FMOD_STUDIO_PARAMETER_ID getParameterID(String string) {
		FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION = this.getParameterDescription(string);
		return fMOD_STUDIO_PARAMETER_DESCRIPTION == null ? null : fMOD_STUDIO_PARAMETER_DESCRIPTION.id;
	}

	public int getParameterCount() {
		return this.parameterDescriptionMap.size();
	}

	public static class TestZombieInfo {
		public float X;
		public float Y;
		public long event;
		public long inst;

		public TestZombieInfo(long long1, float float1, float float2) {
			this.createZombieInstance(long1, float1, float2);
		}

		public long createZombieInstance(long long1, float float1, float float2) {
			long long2 = javafmod.FMOD_Studio_System_CreateEventInstance(long1);
			javafmod.FMOD_Studio_EventInstance3D(long2, float1, float2, 0.0F);
			javafmod.FMOD_Studio_StartEvent(long2);
			this.X = float1;
			this.Y = float2;
			this.event = long1;
			this.inst = long2;
			return long2;
		}
	}
}
