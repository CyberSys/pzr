package fmod;

import java.io.File;
import java.math.BigInteger;
import zombie.debug.DebugLog;


public class javafmodJNI {

	public static void init() {
		DebugLog.log("[javafmodJNI] Init: Start");
		try {
			if (System.getProperty("os.name").contains("OS X")) {
				System.loadLibrary("fmod");
				System.loadLibrary("fmodstudio");
				System.loadLibrary("fmodintegration");
			} else if (System.getProperty("os.name").startsWith("Win")) {
				if (System.getProperty("sun.arch.data.model").equals("64")) {
					DebugLog.log("[javafmodJNI] Init: WIN 64");
					System.loadLibrary("fmod64");
					System.loadLibrary("fmodstudio64");
					System.loadLibrary("fmodintegration64");
				} else {
					System.loadLibrary("fmod");
					System.loadLibrary("fmodstudio");
					System.loadLibrary("fmodintegration32");
				}
			} else {
				loadLibrary("libfmod.so.10.8");
				loadLibrary("libfmodstudio.so.10.8");
				if (System.getProperty("sun.arch.data.model").equals("64")) {
					System.loadLibrary("fmodintegration64");
				} else {
					System.loadLibrary("fmodintegration32");
				}
			}
		} catch (UnsatisfiedLinkError unsatisfiedLinkError) {
			System.out.println("Failed to load fmodintegration library");
			unsatisfiedLinkError.printStackTrace();
			throw new UnsatisfiedLinkError("Can\'t load native libraries");
		}
	}

	private static void loadLibrary(String string) {
		String[] stringArray = System.getProperty("java.library.path", "").split(File.pathSeparator);
		int int1 = stringArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			String string2 = stringArray[int2];
			File file = new File(string2 + "/" + string);
			if (file.exists()) {
				System.load(file.getAbsolutePath());
				break;
			}
		}
	}

	private static void logPutsCallback(String string) {
		long long1 = System.currentTimeMillis();
		System.out.print(long1 + " " + string);
	}

	public static final native long FMOD_Studio_Create();

	public static final native long FMOD_Studio_GetEvent(String string);

	public static final native long FMOD_Studio_CreateEventInstance(long long1);

	public static final native long FMOD_Studio_LoadBankFile(String string);

	public static final native void FMOD_Studio_StartEvent(long long1);

	public static final native long FMOD_Studio_GetTimelinePosition(long long1);

	public static final native int FMOD_Studio_LoadSampleData(long long1);

	public static final native int FMOD_Studio_LoadEventSampleData(long long1);

	public static final native int FMOD_Memory_Initialize(long long1, int int1, long long2, long long3, long long4, long long5);

	public static final native int FMOD_Memory_GetStats(long long1, long long2, long long3);

	public static final native int FMOD_Debug_Initialize(long long1, long long2, long long3, String string);

	public static final native int FMOD_File_SetDiskBusy(int int1);

	public static final native int FMOD_File_GetDiskBusy(long long1);

	public static final native int FMOD_System_Create();

	public static final native int FMOD_System_Release(long long1);

	public static final native int FMOD_System_SetOutput(long long1, long long2);

	public static final native int FMOD_System_GetOutput(long long1, long long2);

	public static final native int FMOD_System_GetNumDrivers(long long1, long long2);

	public static final native int FMOD_System_GetDriverInfo(long long1, int int1, String string, int int2, long long2, long long3, long long4, long long5);

	public static final native int FMOD_System_SetDriver(long long1, int int1);

	public static final native int FMOD_System_GetDriver(long long1, long long2);

	public static final native int FMOD_System_SetSoftwareChannels(long long1, int int1);

	public static final native int FMOD_System_GetSoftwareChannels(long long1, long long2);

	public static final native int FMOD_System_SetSoftwareFormat(long long1, int int1, long long2, int int2);

	public static final native int FMOD_System_GetSoftwareFormat(long long1, long long2, long long3, long long4);

	public static final native int FMOD_System_SetDSPBufferSize(long long1, long long2, int int1);

	public static final native int FMOD_System_GetDSPBufferSize(long long1, long long2, long long3);

	public static final native int FMOD_System_SetFileSystem(long long1, long long2, long long3, long long4, long long5, long long6, long long7, int int1);

	public static final native int FMOD_System_AttachFileSystem(long long1, long long2, long long3, long long4, long long5);

	public static final native int FMOD_System_SetAdvancedSettings(long long1, long long2);

	public static final native int FMOD_System_GetAdvancedSettings(long long1, long long2);

	public static final native int FMOD_System_SetCallback(long long1, long long2, long long3);

	public static final native int FMOD_System_SetPluginPath(long long1, String string);

	public static final native int FMOD_System_LoadPlugin(long long1, String string, long long2, long long3);

	public static final native int FMOD_System_UnloadPlugin(long long1, long long2);

	public static final native int FMOD_System_GetNumPlugins(long long1, long long2, long long3);

	public static final native int FMOD_System_GetPluginHandle(long long1, long long2, int int1, long long3);

	public static final native int FMOD_System_GetPluginInfo(long long1, long long2, long long3, String string, int int1, long long4);

	public static final native int FMOD_System_SetOutputByPlugin(long long1, long long2);

	public static final native int FMOD_System_GetOutputByPlugin(long long1, long long2);

	public static final native int FMOD_System_CreateDSPByPlugin(long long1, long long2, long long3);

	public static final native int FMOD_System_GetDSPInfoByPlugin(long long1, long long2, long long3);

	public static final native int FMOD_System_RegisterCodec(long long1, long long2, long long3, long long4);

	public static final native int FMOD_System_RegisterDSP(long long1, long long2, long long3);

	public static final native int FMOD_System_Init(int int1, long long1, long long2);

	public static final native int FMOD_System_Close(long long1);

	public static final native int FMOD_System_Update();

	public static final native int FMOD_System_SetSpeakerPosition(long long1, long long2, float float1, float float2, long long3);

	public static final native int FMOD_System_GetSpeakerPosition(long long1, long long2, long long3, long long4, long long5);

	public static final native int FMOD_System_SetStreamBufferSize(long long1, long long2, long long3);

	public static final native int FMOD_System_GetStreamBufferSize(long long1, long long2, long long3);

	public static final native int FMOD_System_Set3DSettings(float float1, float float2, float float3);

	public static final native int FMOD_System_Get3DSettings(long long1, long long2, long long3);

	public static final native int FMOD_System_Set3DNumListeners(int int1);

	public static final native int FMOD_System_Get3DNumListeners(long long1, long long2);

	public static final native int FMOD_System_Set3DListenerAttributes(int int1, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12);

	public static final native int FMOD_System_Get3DListenerAttributes(long long1, int int1, long long2, long long3, long long4, long long5);

	public static final native int FMOD_System_Set3DRolloffCallback(long long1, long long2);

	public static final native int FMOD_System_MixerSuspend(long long1);

	public static final native int FMOD_System_MixerResume(long long1);

	public static final native int FMOD_System_GetVersion(long long1, long long2);

	public static final native int FMOD_System_GetOutputHandle(long long1, long long2);

	public static final native int FMOD_System_GetChannelsPlaying(long long1, long long2);

	public static final native int FMOD_System_GetCPUUsage(long long1, long long2, long long3, long long4, long long5, long long6);

	public static final native int FMOD_System_GetSoundRAM(long long1, long long2, long long3, long long4);

	public static final native long FMOD_System_CreateSound(String string, long long1);

	public static final native long FMOD_System_CreateRecordSound(long long1, long long2, long long3, long long4);

	public static final native long FMOD_System_SetVADMode(int int1);

	public static final native int FMOD_System_SetRecordVolume(int int1);

	public static final native long FMOD_System_CreateRAWPlaySound(long long1, long long2, long long3);

	public static final native long FMOD_System_SetRawPlayBufferingPeriod(long long1);

	public static final native int FMOD_System_RAWPlayData(long long1, short[] shortArray, long long2);

	public static final native int FMOD_System_CreateStream(long long1, String string, long long2, long long3, long long4);

	public static final native int FMOD_System_CreateDSP(long long1, long long2, long long3);

	public static final native int FMOD_System_CreateDSPByType(long long1, long long2, long long3);

	public static final native long FMOD_System_CreateChannelGroup(String string);

	public static final native int FMOD_System_CreateSoundGroup(long long1, String string, long long2);

	public static final native int FMOD_System_CreateReverb3D(long long1, long long2);

	public static final native long FMOD_System_PlaySound(long long1, long long2);

	public static final native int FMOD_System_PlayDSP();

	public static final native int FMOD_System_GetChannel(long long1, int int1, long long2);

	public static final native long FMOD_System_GetMasterChannelGroup();

	public static final native int FMOD_System_GetMasterSoundGroup(long long1, long long2);

	public static final native int FMOD_System_AttachChannelGroupToPort(long long1, long long2, long long3, long long4, long long5);

	public static final native int FMOD_System_DetachChannelGroupFromPort(long long1, long long2);

	public static final native int FMOD_System_SetReverbProperties(long long1, int int1, long long2);

	public static final native int FMOD_System_GetReverbProperties(long long1, int int1, long long2);

	public static final native int FMOD_System_LockDSP(long long1);

	public static final native int FMOD_System_UnlockDSP(long long1);

	public static final native int FMOD_System_GetRecordNumDrivers();

	public static final native int FMOD_System_GetRecordDriverInfo(int int1, FMOD_DriverInfo fMOD_DriverInfo);

	public static final native int FMOD_System_GetRecordPosition(int int1, Long Long1);

	public static final native int FMOD_System_RecordStart(int int1, long long1, boolean boolean1);

	public static final native int FMOD_System_RecordStop(int int1);

	public static final native int FMOD_System_IsRecording(long long1, int int1, long long2);

	public static final native int FMOD_System_CreateGeometry(long long1, int int1, int int2, long long2);

	public static final native int FMOD_System_SetGeometrySettings(long long1, float float1);

	public static final native int FMOD_System_GetGeometrySettings(long long1, long long2);

	public static final native int FMOD_System_LoadGeometry(long long1, long long2, int int1, long long3);

	public static final native int FMOD_System_GetGeometryOcclusion(long long1, long long2, long long3, long long4, long long5);

	public static final native int FMOD_System_SetNetworkProxy(long long1, String string);

	public static final native int FMOD_System_GetNetworkProxy(long long1, String string, int int1);

	public static final native int FMOD_System_SetNetworkTimeout(long long1, int int1);

	public static final native int FMOD_System_GetNetworkTimeout(long long1, long long2);

	public static final native int FMOD_System_SetUserData(long long1, long long2);

	public static final native int FMOD_System_GetUserData(long long1, long long2);

	public static final native int FMOD_Sound_Release(long long1);

	public static final native int FMOD_RAWPlaySound_Release(long long1);

	public static final native int FMOD_RecordSound_Release(long long1);

	public static final native int FMOD_Sound_GetSystemObject(long long1, long long2);

	public static final native int FMOD_Sound_Lock(long long1, long long2, long long3, byte[] byteArray, byte[] byteArray2, Long Long1, Long Long2, long[] longArray);

	public static final native int FMOD_Sound_GetData(long long1, byte[] byteArray, Long Long1, Long Long2, Long Long3);

	public static final native int FMOD_Sound_Unlock(long long1, long[] longArray);

	public static final native int FMOD_Sound_SetDefaults(long long1, float float1, int int1);

	public static final native int FMOD_Sound_GetDefaults(long long1, long long2, long long3);

	public static final native int FMOD_Sound_Set3DMinMaxDistance(long long1, float float1, float float2);

	public static final native int FMOD_Sound_Get3DMinMaxDistance(long long1, long long2, long long3);

	public static final native int FMOD_Sound_Set3DConeSettings(long long1, float float1, float float2, float float3);

	public static final native int FMOD_Sound_Get3DConeSettings(long long1, long long2, long long3, long long4);

	public static final native int FMOD_Sound_Set3DCustomRolloff(long long1, long long2, int int1);

	public static final native int FMOD_Sound_Get3DCustomRolloff(long long1, long long2, long long3);

	public static final native int FMOD_Sound_SetSubSound(long long1, int int1, long long2);

	public static final native int FMOD_Sound_GetSubSound(long long1, int int1, long long2);

	public static final native int FMOD_Sound_GetSubSoundParent(long long1, long long2);

	public static final native int FMOD_Sound_GetName(long long1, String string, int int1);

	public static final native int FMOD_Sound_GetLength(long long1, long long2);

	public static final native int FMOD_Sound_GetFormat(long long1, long long2, long long3, long long4, long long5);

	public static final native int FMOD_Sound_GetNumSubSounds(long long1, long long2);

	public static final native int FMOD_Sound_GetNumTags(long long1, long long2, long long3);

	public static final native int FMOD_Sound_GetTag(long long1, String string, int int1, long long2);

	public static final native int FMOD_Sound_GetOpenState(long long1, long long2, long long3, long long4, long long5);

	public static final native int FMOD_Sound_ReadData(long long1, long long2, long long3, long long4);

	public static final native int FMOD_Sound_SeekData(long long1, long long2);

	public static final native int FMOD_Sound_SetSoundGroup(long long1, long long2);

	public static final native int FMOD_Sound_GetSoundGroup(long long1, long long2);

	public static final native int FMOD_Sound_GetNumSyncPoints(long long1, long long2);

	public static final native int FMOD_Sound_GetSyncPoint(long long1, int int1, long long2);

	public static final native int FMOD_Sound_GetSyncPointInfo(long long1, long long2, String string, int int1, long long3, long long4);

	public static final native int FMOD_Sound_AddSyncPoint(long long1, long long2, long long3, String string, long long4);

	public static final native int FMOD_Sound_DeleteSyncPoint(long long1, long long2);

	public static final native int FMOD_Sound_SetMode(long long1, long long2);

	public static final native int FMOD_Sound_GetMode(long long1, long long2);

	public static final native int FMOD_Sound_SetLoopCount(long long1, int int1);

	public static final native int FMOD_Sound_GetLoopCount(long long1, long long2);

	public static final native int FMOD_Sound_SetLoopPoints(long long1, long long2, long long3, long long4, long long5);

	public static final native int FMOD_Sound_GetLoopPoints(long long1, long long2, long long3, long long4, long long5);

	public static final native int FMOD_Sound_GetMusicNumChannels(long long1, long long2);

	public static final native int FMOD_Sound_SetMusicChannelVolume(long long1, int int1, float float1);

	public static final native int FMOD_Sound_GetMusicChannelVolume(long long1, int int1, long long2);

	public static final native int FMOD_Sound_SetMusicSpeed(long long1, float float1);

	public static final native int FMOD_Sound_GetMusicSpeed(long long1, long long2);

	public static final native int FMOD_Sound_SetUserData(long long1, long long2);

	public static final native int FMOD_Sound_GetUserData(long long1, long long2);

	public static final native int FMOD_Channel_GetSystemObject(long long1, long long2);

	public static final native int FMOD_Channel_Stop(long long1);

	public static final native int FMOD_Channel_SetPaused(long long1, long long2);

	public static final native int FMOD_Channel_GetPaused(long long1, long long2);

	public static final native int FMOD_Channel_SetVolume(long long1, float float1);

	public static final native int FMOD_Channel_GetVolume(long long1, long long2);

	public static final native int FMOD_Channel_SetVolumeRamp(long long1, long long2);

	public static final native int FMOD_Channel_GetVolumeRamp(long long1, long long2);

	public static final native float FMOD_Channel_GetAudibility(long long1);

	public static final native int FMOD_Channel_SetPitch(long long1, float float1);

	public static final native int FMOD_Channel_GetPitch(long long1, long long2);

	public static final native int FMOD_Channel_SetMute(long long1, long long2);

	public static final native int FMOD_Channel_GetMute(long long1, long long2);

	public static final native int FMOD_Channel_SetReverbProperties(long long1, int int1, float float1);

	public static final native int FMOD_Channel_GetReverbProperties(long long1, int int1, long long2);

	public static final native int FMOD_Channel_SetLowPassGain(long long1, float float1);

	public static final native int FMOD_Channel_GetLowPassGain(long long1, long long2);

	public static final native int FMOD_Channel_SetMode(long long1, long long2);

	public static final native int FMOD_Channel_GetMode(long long1, long long2);

	public static final native int FMOD_Channel_SetCallback(long long1, long long2);

	public static final native boolean FMOD_Channel_IsPlaying(long long1);

	public static final native int FMOD_Channel_SetPan(long long1, float float1);

	public static final native int FMOD_Channel_SetMixLevelsOutput(long long1, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8);

	public static final native int FMOD_Channel_SetMixLevelsInput(long long1, long long2, int int1);

	public static final native int FMOD_Channel_SetMixMatrix(long long1, long long2, int int1, int int2, int int3);

	public static final native int FMOD_Channel_GetMixMatrix(long long1, long long2, long long3, long long4, int int1);

	public static final native int FMOD_Channel_GetDSPClock(long long1, long long2, long long3);

	public static final native int FMOD_Channel_SetDelay(long long1, BigInteger bigInteger, BigInteger bigInteger2, long long2);

	public static final native int FMOD_Channel_GetDelay(long long1, long long2, long long3, long long4);

	public static final native int FMOD_Channel_AddFadePoint(long long1, BigInteger bigInteger, float float1);

	public static final native int FMOD_Channel_RemoveFadePoints(long long1, BigInteger bigInteger, BigInteger bigInteger2);

	public static final native int FMOD_Channel_GetFadePoints(long long1, long long2, long long3, long long4);

	public static final native int FMOD_Channel_GetDSP(long long1, int int1, long long2);

	public static final native int FMOD_Channel_AddDSP(long long1, int int1, long long2);

	public static final native int FMOD_Channel_RemoveDSP(long long1, long long2);

	public static final native int FMOD_Channel_GetNumDSPs(long long1, long long2);

	public static final native int FMOD_Channel_SetDSPIndex(long long1, long long2, int int1);

	public static final native int FMOD_Channel_GetDSPIndex(long long1, long long2, long long3);

	public static final native int FMOD_Channel_OverridePanDSP(long long1, long long2);

	public static final native int FMOD_Channel_Set3DAttributes(long long1, float float1, float float2, float float3, float float4, float float5, float float6);

	public static final native int FMOD_Channel_Get3DAttributes(long long1, long long2, long long3, long long4);

	public static final native int FMOD_Channel_Set3DMinMaxDistance(long long1, float float1, float float2);

	public static final native int FMOD_Channel_Get3DMinMaxDistance(long long1, long long2, long long3);

	public static final native int FMOD_Channel_Set3DConeSettings(long long1, float float1, float float2, float float3);

	public static final native int FMOD_Channel_Get3DConeSettings(long long1, long long2, long long3, long long4);

	public static final native int FMOD_Channel_Set3DConeOrientation(long long1, long long2);

	public static final native int FMOD_Channel_Get3DConeOrientation(long long1, long long2);

	public static final native int FMOD_Channel_Set3DCustomRolloff(long long1, long long2, int int1);

	public static final native int FMOD_Channel_Get3DCustomRolloff(long long1, long long2, long long3);

	public static final native int FMOD_Channel_Set3DOcclusion(long long1, float float1, float float2);

	public static final native int FMOD_Channel_Get3DOcclusion(long long1, long long2, long long3);

	public static final native int FMOD_Channel_Set3DSpread(long long1, float float1);

	public static final native int FMOD_Channel_Get3DSpread(long long1, long long2);

	public static final native int FMOD_Channel_Set3DLevel(long long1, float float1);

	public static final native int FMOD_Channel_Get3DLevel(long long1, long long2);

	public static final native int FMOD_Channel_Set3DDopplerLevel(long long1, float float1);

	public static final native int FMOD_Channel_Get3DDopplerLevel(long long1, long long2);

	public static final native int FMOD_Channel_Set3DDistanceFilter(long long1, long long2, float float1, float float2);

	public static final native int FMOD_Channel_Get3DDistanceFilter(long long1, long long2, long long3, long long4);

	public static final native int FMOD_Channel_SetUserData(long long1, long long2);

	public static final native int FMOD_Channel_GetUserData(long long1, long long2);

	public static final native int FMOD_Channel_SetFrequency(long long1, float float1);

	public static final native int FMOD_Channel_GetFrequency(long long1, long long2);

	public static final native int FMOD_Channel_SetPriority(long long1, int int1);

	public static final native int FMOD_Channel_GetPriority(long long1, long long2);

	public static final native int FMOD_Channel_SetPosition(long long1, long long2);

	public static final native long FMOD_Channel_GetPosition(long long1, long long2);

	public static final native int FMOD_Channel_SetChannelGroup(long long1, long long2);

	public static final native int FMOD_Channel_GetChannelGroup(long long1, long long2);

	public static final native int FMOD_Channel_SetLoopCount(long long1, int int1);

	public static final native int FMOD_Channel_GetLoopCount(long long1, long long2);

	public static final native int FMOD_Channel_SetLoopPoints(long long1, long long2, long long3, long long4, long long5);

	public static final native int FMOD_Channel_GetLoopPoints(long long1, long long2, long long3, long long4, long long5);

	public static final native boolean FMOD_Channel_IsVirtual(long long1);

	public static final native int FMOD_Channel_GetCurrentSound(long long1, long long2);

	public static final native int FMOD_Channel_GetIndex(long long1, long long2);

	public static final native int FMOD_ChannelGroup_GetSystemObject(long long1, long long2);

	public static final native int FMOD_ChannelGroup_Stop(long long1);

	public static final native int FMOD_ChannelGroup_SetPaused(long long1, boolean boolean1);

	public static final native int FMOD_ChannelGroup_GetPaused(long long1, long long2);

	public static final native int FMOD_ChannelGroup_SetVolume(long long1, float float1);

	public static final native int FMOD_ChannelGroup_GetVolume(long long1, long long2);

	public static final native int FMOD_ChannelGroup_SetVolumeRamp(long long1, long long2);

	public static final native int FMOD_ChannelGroup_GetVolumeRamp(long long1, long long2);

	public static final native int FMOD_ChannelGroup_GetAudibility(long long1, long long2);

	public static final native int FMOD_ChannelGroup_SetPitch(long long1, float float1);

	public static final native int FMOD_ChannelGroup_GetPitch(long long1, long long2);

	public static final native int FMOD_ChannelGroup_SetMute(long long1, boolean boolean1);

	public static final native int FMOD_ChannelGroup_GetMute(long long1, long long2);

	public static final native int FMOD_ChannelGroup_SetReverbProperties(long long1, int int1, float float1);

	public static final native int FMOD_ChannelGroup_GetReverbProperties(long long1, int int1, long long2);

	public static final native int FMOD_ChannelGroup_SetLowPassGain(long long1, float float1);

	public static final native int FMOD_ChannelGroup_GetLowPassGain(long long1, long long2);

	public static final native int FMOD_ChannelGroup_SetMode(long long1, long long2);

	public static final native int FMOD_ChannelGroup_GetMode(long long1, long long2);

	public static final native int FMOD_ChannelGroup_SetCallback(long long1, long long2);

	public static final native int FMOD_ChannelGroup_IsPlaying(long long1, long long2);

	public static final native int FMOD_ChannelGroup_SetPan(long long1, float float1);

	public static final native int FMOD_ChannelGroup_SetMixLevelsOutput(long long1, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8);

	public static final native int FMOD_ChannelGroup_SetMixLevelsInput(long long1, long long2, int int1);

	public static final native int FMOD_ChannelGroup_SetMixMatrix(long long1, long long2, int int1, int int2, int int3);

	public static final native int FMOD_ChannelGroup_GetMixMatrix(long long1, long long2, long long3, long long4, int int1);

	public static final native int FMOD_ChannelGroup_GetDSPClock(long long1, long long2, long long3);

	public static final native int FMOD_ChannelGroup_SetDelay(long long1, BigInteger bigInteger, BigInteger bigInteger2, long long2);

	public static final native int FMOD_ChannelGroup_GetDelay(long long1, long long2, long long3, long long4);

	public static final native int FMOD_ChannelGroup_AddFadePoint(long long1, BigInteger bigInteger, float float1);

	public static final native int FMOD_ChannelGroup_RemoveFadePoints(long long1, BigInteger bigInteger, BigInteger bigInteger2);

	public static final native int FMOD_ChannelGroup_GetFadePoints(long long1, long long2, long long3, long long4);

	public static final native int FMOD_ChannelGroup_GetDSP(long long1, int int1, long long2);

	public static final native int FMOD_ChannelGroup_AddDSP(long long1, int int1, long long2);

	public static final native int FMOD_ChannelGroup_RemoveDSP(long long1, long long2);

	public static final native int FMOD_ChannelGroup_GetNumDSPs(long long1, long long2);

	public static final native int FMOD_ChannelGroup_SetDSPIndex(long long1, long long2, int int1);

	public static final native int FMOD_ChannelGroup_GetDSPIndex(long long1, long long2, long long3);

	public static final native int FMOD_ChannelGroup_OverridePanDSP(long long1, long long2);

	public static final native int FMOD_ChannelGroup_Set3DAttributes(long long1, long long2, long long3, long long4);

	public static final native int FMOD_ChannelGroup_Get3DAttributes(long long1, long long2, long long3, long long4);

	public static final native int FMOD_ChannelGroup_Set3DMinMaxDistance(long long1, float float1, float float2);

	public static final native int FMOD_ChannelGroup_Get3DMinMaxDistance(long long1, long long2, long long3);

	public static final native int FMOD_ChannelGroup_Set3DConeSettings(long long1, float float1, float float2, float float3);

	public static final native int FMOD_ChannelGroup_Get3DConeSettings(long long1, long long2, long long3, long long4);

	public static final native int FMOD_ChannelGroup_Set3DConeOrientation(long long1, long long2);

	public static final native int FMOD_ChannelGroup_Get3DConeOrientation(long long1, long long2);

	public static final native int FMOD_ChannelGroup_Set3DCustomRolloff(long long1, long long2, int int1);

	public static final native int FMOD_ChannelGroup_Get3DCustomRolloff(long long1, long long2, long long3);

	public static final native int FMOD_ChannelGroup_Set3DOcclusion(long long1, float float1, float float2);

	public static final native int FMOD_ChannelGroup_Get3DOcclusion(long long1, long long2, long long3);

	public static final native int FMOD_ChannelGroup_Set3DSpread(long long1, float float1);

	public static final native int FMOD_ChannelGroup_Get3DSpread(long long1, long long2);

	public static final native int FMOD_ChannelGroup_Set3DLevel(long long1, float float1);

	public static final native int FMOD_ChannelGroup_Get3DLevel(long long1, long long2);

	public static final native int FMOD_ChannelGroup_Set3DDopplerLevel(long long1, float float1);

	public static final native int FMOD_ChannelGroup_Get3DDopplerLevel(long long1, long long2);

	public static final native int FMOD_ChannelGroup_Set3DDistanceFilter(long long1, long long2, float float1, float float2);

	public static final native int FMOD_ChannelGroup_Get3DDistanceFilter(long long1, long long2, long long3, long long4);

	public static final native int FMOD_ChannelGroup_SetUserData(long long1, long long2);

	public static final native int FMOD_ChannelGroup_GetUserData(long long1, long long2);

	public static final native int FMOD_ChannelGroup_Release(long long1);

	public static final native int FMOD_ChannelGroup_AddGroup(long long1, long long2, long long3, long long4);

	public static final native int FMOD_ChannelGroup_GetNumGroups(long long1, long long2);

	public static final native int FMOD_ChannelGroup_GetGroup(long long1, int int1, long long2);

	public static final native int FMOD_ChannelGroup_GetParentGroup(long long1, long long2);

	public static final native int FMOD_ChannelGroup_GetName(long long1, String string, int int1);

	public static final native int FMOD_ChannelGroup_GetNumChannels(long long1, long long2);

	public static final native int FMOD_ChannelGroup_GetChannel(long long1, int int1, long long2);

	public static final native int FMOD_SoundGroup_Release(long long1);

	public static final native int FMOD_SoundGroup_GetSystemObject(long long1, long long2);

	public static final native int FMOD_SoundGroup_SetMaxAudible(long long1, int int1);

	public static final native int FMOD_SoundGroup_GetMaxAudible(long long1, long long2);

	public static final native int FMOD_SoundGroup_SetMaxAudibleBehavior(long long1, long long2);

	public static final native int FMOD_SoundGroup_GetMaxAudibleBehavior(long long1, long long2);

	public static final native int FMOD_SoundGroup_SetMuteFadeSpeed(long long1, float float1);

	public static final native int FMOD_SoundGroup_GetMuteFadeSpeed(long long1, long long2);

	public static final native int FMOD_SoundGroup_SetVolume(long long1, float float1);

	public static final native int FMOD_SoundGroup_GetVolume(long long1, long long2);

	public static final native int FMOD_SoundGroup_Stop(long long1);

	public static final native int FMOD_SoundGroup_GetName(long long1, String string, int int1);

	public static final native int FMOD_SoundGroup_GetNumSounds(long long1, long long2);

	public static final native int FMOD_SoundGroup_GetSound(long long1, int int1, long long2);

	public static final native int FMOD_SoundGroup_GetNumPlaying(long long1, long long2);

	public static final native int FMOD_SoundGroup_SetUserData(long long1, long long2);

	public static final native int FMOD_SoundGroup_GetUserData(long long1, long long2);

	public static final native int FMOD_DSP_Release(long long1);

	public static final native int FMOD_DSP_GetSystemObject(long long1, long long2);

	public static final native int FMOD_DSP_AddInput(long long1, long long2, long long3, long long4);

	public static final native int FMOD_DSP_DisconnectFrom(long long1, long long2, long long3);

	public static final native int FMOD_DSP_DisconnectAll(long long1, long long2, long long3);

	public static final native int FMOD_DSP_GetNumInputs(long long1, long long2);

	public static final native int FMOD_DSP_GetNumOutputs(long long1, long long2);

	public static final native int FMOD_DSP_GetInput(long long1, int int1, long long2, long long3);

	public static final native int FMOD_DSP_GetOutput(long long1, int int1, long long2, long long3);

	public static final native int FMOD_DSP_SetActive(long long1, long long2);

	public static final native int FMOD_DSP_GetActive(long long1, long long2);

	public static final native int FMOD_DSP_SetBypass(long long1, long long2);

	public static final native int FMOD_DSP_GetBypass(long long1, long long2);

	public static final native int FMOD_DSP_SetWetDryMix(long long1, float float1, float float2, float float3);

	public static final native int FMOD_DSP_GetWetDryMix(long long1, long long2, long long3, long long4);

	public static final native int FMOD_DSP_SetChannelFormat(long long1, long long2, int int1, long long3);

	public static final native int FMOD_DSP_GetChannelFormat(long long1, long long2, long long3, long long4);

	public static final native int FMOD_DSP_GetOutputChannelFormat(long long1, long long2, int int1, long long3, long long4, long long5, long long6);

	public static final native int FMOD_DSP_Reset(long long1);

	public static final native int FMOD_DSP_SetParameterFloat(long long1, int int1, float float1);

	public static final native int FMOD_DSP_SetParameterInt(long long1, int int1, int int2);

	public static final native int FMOD_DSP_SetParameterBool(long long1, int int1, long long2);

	public static final native int FMOD_DSP_SetParameterData(long long1, int int1, long long2, long long3);

	public static final native int FMOD_DSP_GetParameterFloat(long long1, int int1, long long2, String string, int int2);

	public static final native int FMOD_DSP_GetParameterInt(long long1, int int1, long long2, String string, int int2);

	public static final native int FMOD_DSP_GetParameterBool(long long1, int int1, long long2, String string, int int2);

	public static final native int FMOD_DSP_GetParameterData(long long1, int int1, long long2, long long3, String string, int int2);

	public static final native int FMOD_DSP_GetNumParameters(long long1, long long2);

	public static final native int FMOD_DSP_GetParameterInfo(long long1, int int1, long long2);

	public static final native int FMOD_DSP_GetDataParameterIndex(long long1, int int1, long long2);

	public static final native int FMOD_DSP_ShowConfigDialog(long long1, long long2, long long3);

	public static final native int FMOD_DSP_GetInfo(long long1, String string, long long2, long long3, long long4, long long5);

	public static final native int FMOD_DSP_GetType(long long1, long long2);

	public static final native int FMOD_DSP_GetIdle(long long1, long long2);

	public static final native int FMOD_DSP_SetUserData(long long1, long long2);

	public static final native int FMOD_DSP_GetUserData(long long1, long long2);

	public static final native int FMOD_DSP_SetMeteringEnabled(long long1, long long2, long long3);

	public static final native int FMOD_DSP_GetMeteringEnabled(long long1, long long2, long long3);

	public static final native int FMOD_DSP_GetMeteringInfo(long long1, long long2, long long3);

	public static final native int FMOD_DSPConnection_GetInput(long long1, long long2);

	public static final native int FMOD_DSPConnection_GetOutput(long long1, long long2);

	public static final native int FMOD_DSPConnection_SetMix(long long1, float float1);

	public static final native int FMOD_DSPConnection_GetMix(long long1, long long2);

	public static final native int FMOD_DSPConnection_SetMixMatrix(long long1, long long2, int int1, int int2, int int3);

	public static final native int FMOD_DSPConnection_GetMixMatrix(long long1, long long2, long long3, long long4, int int1);

	public static final native int FMOD_DSPConnection_GetType(long long1, long long2);

	public static final native int FMOD_DSPConnection_SetUserData(long long1, long long2);

	public static final native int FMOD_DSPConnection_GetUserData(long long1, long long2);

	public static final native int FMOD_Geometry_Release(long long1);

	public static final native int FMOD_Geometry_AddPolygon(long long1, float float1, float float2, long long2, int int1, long long3, long long4);

	public static final native int FMOD_Geometry_GetNumPolygons(long long1, long long2);

	public static final native int FMOD_Geometry_GetMaxPolygons(long long1, long long2, long long3);

	public static final native int FMOD_Geometry_GetPolygonNumVertices(long long1, int int1, long long2);

	public static final native int FMOD_Geometry_SetPolygonVertex(long long1, int int1, int int2, long long2);

	public static final native int FMOD_Geometry_GetPolygonVertex(long long1, int int1, int int2, long long2);

	public static final native int FMOD_Geometry_SetPolygonAttributes(long long1, int int1, float float1, float float2, long long2);

	public static final native int FMOD_Geometry_GetPolygonAttributes(long long1, int int1, long long2, long long3, long long4);

	public static final native int FMOD_Geometry_SetActive(long long1, long long2);

	public static final native int FMOD_Geometry_GetActive(long long1, long long2);

	public static final native int FMOD_Geometry_SetRotation(long long1, long long2, long long3);

	public static final native int FMOD_Geometry_GetRotation(long long1, long long2, long long3);

	public static final native int FMOD_Geometry_SetPosition(long long1, long long2);

	public static final native int FMOD_Geometry_GetPosition(long long1, long long2);

	public static final native int FMOD_Geometry_SetScale(long long1, long long2);

	public static final native int FMOD_Geometry_GetScale(long long1, long long2);

	public static final native int FMOD_Geometry_Save(long long1, long long2, long long3);

	public static final native int FMOD_Geometry_SetUserData(long long1, long long2);

	public static final native int FMOD_Geometry_GetUserData(long long1, long long2);

	public static final native int FMOD_Reverb3D_Release(long long1);

	public static final native int FMOD_Reverb3D_Set3DAttributes(long long1, long long2, float float1, float float2);

	public static final native int FMOD_Reverb3D_Get3DAttributes(long long1, long long2, long long3, long long4);

	public static final native int FMOD_Reverb3D_SetProperties(long long1, long long2);

	public static final native int FMOD_Reverb3D_GetProperties(long long1, long long2);

	public static final native int FMOD_Reverb3D_SetActive(long long1, long long2);

	public static final native int FMOD_Reverb3D_GetActive(long long1, long long2);

	public static final native int FMOD_Reverb3D_SetUserData(long long1, long long2);

	public static final native int FMOD_Reverb3D_GetUserData(long long1, long long2);

	public static final native void FMOD_System_SetReverbDefault(int int1, int int2);

	public static final native int FMOD_Studio_EventInstance3D(long long1, float float1, float float2, float float3);

	public static final native int FMOD_Studio_SetNumListeners(int int1);

	public static final native void FMOD_Studio_Listener3D(int int1, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12);

	public static final native int FMOD_Studio_SetParameter(long long1, String string, float float1);

	public static final native float FMOD_Studio_GetParameter(long long1, String string);

	public static final native int FMOD_Studio_GetPlaybackState(long long1);

	public static final native int FMOD_Studio_EventInstance_SetVolume(long long1, float float1);

	public static final native float FMOD_Studio_EventInstance_GetVolume(long long1);

	public static final native int FMOD_Studio_ReleaseEventInstance(long long1);

	public static final native int FMOD_Studio_StopInstance(long long1);

	public static final native void FMOD_Studio_System_FlushCommands();

	public static final native int FMOD_Studio_System_GetBankCount();

	public static final native int FMOD_Studio_System_GetBankList(long[] longArray);

	public static final native int FMOD_Studio_Bank_GetEventCount(long long1);

	public static final native int FMOD_Studio_Bank_GetEventList(long long1, long[] longArray);

	public static final native int FMOD_Studio_EventDescription_GetInstanceCount(long long1);

	public static final native int FMOD_Studio_EventDescription_GetInstanceList(long long1, long[] longArray);

	public static final native String FMOD_Studio_EventDescription_GetPath(long long1);

	public static final native boolean FMOD_Studio_EventInstance_GetPaused(long long1);

	public static final native void FMOD_Studio_EventInstance_SetPaused(long long1, boolean boolean1);

	public static final native void FMOD_Studio_EventInstance_SetProperty(long long1, int int1, float float1);
}
