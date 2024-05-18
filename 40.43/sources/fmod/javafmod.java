package fmod;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import zombie.core.Core;
import zombie.debug.DebugLog;


public class javafmod {
	static HashMap map = new HashMap();
	private static int[] reverb = new int[4];

	public static int FMOD_Memory_Initialize(SWIGTYPE_p_void sWIGTYPE_p_void, int int1, SWIGTYPE_p_FMOD_MEMORY_ALLOC_CALLBACK sWIGTYPE_p_FMOD_MEMORY_ALLOC_CALLBACK, SWIGTYPE_p_FMOD_MEMORY_REALLOC_CALLBACK sWIGTYPE_p_FMOD_MEMORY_REALLOC_CALLBACK, SWIGTYPE_p_FMOD_MEMORY_FREE_CALLBACK sWIGTYPE_p_FMOD_MEMORY_FREE_CALLBACK, SWIGTYPE_p_FMOD_MEMORY_TYPE sWIGTYPE_p_FMOD_MEMORY_TYPE) {
		return javafmodJNI.FMOD_Memory_Initialize(SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void), int1, SWIGTYPE_p_FMOD_MEMORY_ALLOC_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_MEMORY_ALLOC_CALLBACK), SWIGTYPE_p_FMOD_MEMORY_REALLOC_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_MEMORY_REALLOC_CALLBACK), SWIGTYPE_p_FMOD_MEMORY_FREE_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_MEMORY_FREE_CALLBACK), SWIGTYPE_p_FMOD_MEMORY_TYPE.getCPtr(sWIGTYPE_p_FMOD_MEMORY_TYPE));
	}

	public static int FMOD_Memory_GetStats(SWIGTYPE_p_int sWIGTYPE_p_int, SWIGTYPE_p_int sWIGTYPE_p_int2, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_Memory_GetStats(SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int2), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_Debug_Initialize(SWIGTYPE_p_FMOD_DEBUG_FLAGS sWIGTYPE_p_FMOD_DEBUG_FLAGS, SWIGTYPE_p_FMOD_DEBUG_MODE sWIGTYPE_p_FMOD_DEBUG_MODE, SWIGTYPE_p_FMOD_DEBUG_CALLBACK sWIGTYPE_p_FMOD_DEBUG_CALLBACK, String string) {
		return javafmodJNI.FMOD_Debug_Initialize(SWIGTYPE_p_FMOD_DEBUG_FLAGS.getCPtr(sWIGTYPE_p_FMOD_DEBUG_FLAGS), SWIGTYPE_p_FMOD_DEBUG_MODE.getCPtr(sWIGTYPE_p_FMOD_DEBUG_MODE), SWIGTYPE_p_FMOD_DEBUG_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_DEBUG_CALLBACK), string);
	}

	public static int FMOD_File_SetDiskBusy(int int1) {
		return javafmodJNI.FMOD_File_SetDiskBusy(int1);
	}

	public static int FMOD_File_GetDiskBusy(SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_File_GetDiskBusy(SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_System_Create() {
		return javafmodJNI.FMOD_System_Create();
	}

	public static int FMOD_System_Release(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM) {
		return javafmodJNI.FMOD_System_Release(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM));
	}

	public static int FMOD_System_SetOutput(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_OUTPUTTYPE sWIGTYPE_p_FMOD_OUTPUTTYPE) {
		return javafmodJNI.FMOD_System_SetOutput(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_OUTPUTTYPE.getCPtr(sWIGTYPE_p_FMOD_OUTPUTTYPE));
	}

	public static int FMOD_System_GetOutput(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_OUTPUTTYPE sWIGTYPE_p_FMOD_OUTPUTTYPE) {
		return javafmodJNI.FMOD_System_GetOutput(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_OUTPUTTYPE.getCPtr(sWIGTYPE_p_FMOD_OUTPUTTYPE));
	}

	public static int FMOD_System_GetNumDrivers(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_System_GetNumDrivers(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_System_GetDriverInfo(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, int int1, String string, int int2, SWIGTYPE_p_FMOD_GUID sWIGTYPE_p_FMOD_GUID, SWIGTYPE_p_int sWIGTYPE_p_int, SWIGTYPE_p_FMOD_SPEAKERMODE sWIGTYPE_p_FMOD_SPEAKERMODE, SWIGTYPE_p_int sWIGTYPE_p_int2) {
		return javafmodJNI.FMOD_System_GetDriverInfo(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), int1, string, int2, SWIGTYPE_p_FMOD_GUID.getCPtr(sWIGTYPE_p_FMOD_GUID), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int), SWIGTYPE_p_FMOD_SPEAKERMODE.getCPtr(sWIGTYPE_p_FMOD_SPEAKERMODE), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int2));
	}

	public static int FMOD_System_SetDriver(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, int int1) {
		return javafmodJNI.FMOD_System_SetDriver(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), int1);
	}

	public static int FMOD_System_GetDriver(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_System_GetDriver(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_System_SetSoftwareChannels(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, int int1) {
		return javafmodJNI.FMOD_System_SetSoftwareChannels(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), int1);
	}

	public static int FMOD_System_GetSoftwareChannels(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_System_GetSoftwareChannels(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_System_SetSoftwareFormat(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, int int1, SWIGTYPE_p_FMOD_SPEAKERMODE sWIGTYPE_p_FMOD_SPEAKERMODE, int int2) {
		return javafmodJNI.FMOD_System_SetSoftwareFormat(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), int1, SWIGTYPE_p_FMOD_SPEAKERMODE.getCPtr(sWIGTYPE_p_FMOD_SPEAKERMODE), int2);
	}

	public static int FMOD_System_GetSoftwareFormat(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_int sWIGTYPE_p_int, SWIGTYPE_p_FMOD_SPEAKERMODE sWIGTYPE_p_FMOD_SPEAKERMODE, SWIGTYPE_p_int sWIGTYPE_p_int2) {
		return javafmodJNI.FMOD_System_GetSoftwareFormat(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int), SWIGTYPE_p_FMOD_SPEAKERMODE.getCPtr(sWIGTYPE_p_FMOD_SPEAKERMODE), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int2));
	}

	public static int FMOD_System_SetDSPBufferSize(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, long long1, int int1) {
		return javafmodJNI.FMOD_System_SetDSPBufferSize(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), long1, int1);
	}

	public static int FMOD_System_GetDSPBufferSize(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_System_GetDSPBufferSize(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_System_SetFileSystem(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_FILE_OPEN_CALLBACK sWIGTYPE_p_FMOD_FILE_OPEN_CALLBACK, SWIGTYPE_p_FMOD_FILE_CLOSE_CALLBACK sWIGTYPE_p_FMOD_FILE_CLOSE_CALLBACK, SWIGTYPE_p_FMOD_FILE_READ_CALLBACK sWIGTYPE_p_FMOD_FILE_READ_CALLBACK, SWIGTYPE_p_FMOD_FILE_SEEK_CALLBACK sWIGTYPE_p_FMOD_FILE_SEEK_CALLBACK, SWIGTYPE_p_FMOD_FILE_ASYNCREAD_CALLBACK sWIGTYPE_p_FMOD_FILE_ASYNCREAD_CALLBACK, SWIGTYPE_p_FMOD_FILE_ASYNCCANCEL_CALLBACK sWIGTYPE_p_FMOD_FILE_ASYNCCANCEL_CALLBACK, int int1) {
		return javafmodJNI.FMOD_System_SetFileSystem(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_FILE_OPEN_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_FILE_OPEN_CALLBACK), SWIGTYPE_p_FMOD_FILE_CLOSE_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_FILE_CLOSE_CALLBACK), SWIGTYPE_p_FMOD_FILE_READ_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_FILE_READ_CALLBACK), SWIGTYPE_p_FMOD_FILE_SEEK_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_FILE_SEEK_CALLBACK), SWIGTYPE_p_FMOD_FILE_ASYNCREAD_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_FILE_ASYNCREAD_CALLBACK), SWIGTYPE_p_FMOD_FILE_ASYNCCANCEL_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_FILE_ASYNCCANCEL_CALLBACK), int1);
	}

	public static int FMOD_System_AttachFileSystem(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_FILE_OPEN_CALLBACK sWIGTYPE_p_FMOD_FILE_OPEN_CALLBACK, SWIGTYPE_p_FMOD_FILE_CLOSE_CALLBACK sWIGTYPE_p_FMOD_FILE_CLOSE_CALLBACK, SWIGTYPE_p_FMOD_FILE_READ_CALLBACK sWIGTYPE_p_FMOD_FILE_READ_CALLBACK, SWIGTYPE_p_FMOD_FILE_SEEK_CALLBACK sWIGTYPE_p_FMOD_FILE_SEEK_CALLBACK) {
		return javafmodJNI.FMOD_System_AttachFileSystem(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_FILE_OPEN_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_FILE_OPEN_CALLBACK), SWIGTYPE_p_FMOD_FILE_CLOSE_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_FILE_CLOSE_CALLBACK), SWIGTYPE_p_FMOD_FILE_READ_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_FILE_READ_CALLBACK), SWIGTYPE_p_FMOD_FILE_SEEK_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_FILE_SEEK_CALLBACK));
	}

	public static int FMOD_System_SetAdvancedSettings(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_ADVANCEDSETTINGS sWIGTYPE_p_FMOD_ADVANCEDSETTINGS) {
		return javafmodJNI.FMOD_System_SetAdvancedSettings(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_ADVANCEDSETTINGS.getCPtr(sWIGTYPE_p_FMOD_ADVANCEDSETTINGS));
	}

	public static int FMOD_System_GetAdvancedSettings(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_ADVANCEDSETTINGS sWIGTYPE_p_FMOD_ADVANCEDSETTINGS) {
		return javafmodJNI.FMOD_System_GetAdvancedSettings(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_ADVANCEDSETTINGS.getCPtr(sWIGTYPE_p_FMOD_ADVANCEDSETTINGS));
	}

	public static int FMOD_System_SetCallback(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_SYSTEM_CALLBACK sWIGTYPE_p_FMOD_SYSTEM_CALLBACK, SWIGTYPE_p_FMOD_SYSTEM_CALLBACK_TYPE sWIGTYPE_p_FMOD_SYSTEM_CALLBACK_TYPE) {
		return javafmodJNI.FMOD_System_SetCallback(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_SYSTEM_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_SYSTEM_CALLBACK), SWIGTYPE_p_FMOD_SYSTEM_CALLBACK_TYPE.getCPtr(sWIGTYPE_p_FMOD_SYSTEM_CALLBACK_TYPE));
	}

	public static int FMOD_System_SetPluginPath(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, String string) {
		return javafmodJNI.FMOD_System_SetPluginPath(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), string);
	}

	public static int FMOD_System_LoadPlugin(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, String string, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int, long long1) {
		return javafmodJNI.FMOD_System_LoadPlugin(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), string, SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int), long1);
	}

	public static int FMOD_System_UnloadPlugin(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, long long1) {
		return javafmodJNI.FMOD_System_UnloadPlugin(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), long1);
	}

	public static int FMOD_System_GetNumPlugins(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_PLUGINTYPE sWIGTYPE_p_FMOD_PLUGINTYPE, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_System_GetNumPlugins(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_PLUGINTYPE.getCPtr(sWIGTYPE_p_FMOD_PLUGINTYPE), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_System_GetPluginHandle(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_PLUGINTYPE sWIGTYPE_p_FMOD_PLUGINTYPE, int int1, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int) {
		return javafmodJNI.FMOD_System_GetPluginHandle(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_PLUGINTYPE.getCPtr(sWIGTYPE_p_FMOD_PLUGINTYPE), int1, SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int));
	}

	public static int FMOD_System_GetPluginInfo(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, long long1, SWIGTYPE_p_FMOD_PLUGINTYPE sWIGTYPE_p_FMOD_PLUGINTYPE, String string, int int1, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int) {
		return javafmodJNI.FMOD_System_GetPluginInfo(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), long1, SWIGTYPE_p_FMOD_PLUGINTYPE.getCPtr(sWIGTYPE_p_FMOD_PLUGINTYPE), string, int1, SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int));
	}

	public static int FMOD_System_SetOutputByPlugin(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, long long1) {
		return javafmodJNI.FMOD_System_SetOutputByPlugin(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), long1);
	}

	public static int FMOD_System_GetOutputByPlugin(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int) {
		return javafmodJNI.FMOD_System_GetOutputByPlugin(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int));
	}

	public static int FMOD_System_CreateDSPByPlugin(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, long long1, SWIGTYPE_p_p_FMOD_DSP sWIGTYPE_p_p_FMOD_DSP) {
		return javafmodJNI.FMOD_System_CreateDSPByPlugin(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), long1, SWIGTYPE_p_p_FMOD_DSP.getCPtr(sWIGTYPE_p_p_FMOD_DSP));
	}

	public static int FMOD_System_GetDSPInfoByPlugin(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, long long1, SWIGTYPE_p_p_FMOD_DSP_DESCRIPTION sWIGTYPE_p_p_FMOD_DSP_DESCRIPTION) {
		return javafmodJNI.FMOD_System_GetDSPInfoByPlugin(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), long1, SWIGTYPE_p_p_FMOD_DSP_DESCRIPTION.getCPtr(sWIGTYPE_p_p_FMOD_DSP_DESCRIPTION));
	}

	public static int FMOD_System_RegisterCodec(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_CODEC_DESCRIPTION sWIGTYPE_p_FMOD_CODEC_DESCRIPTION, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int, long long1) {
		return javafmodJNI.FMOD_System_RegisterCodec(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_CODEC_DESCRIPTION.getCPtr(sWIGTYPE_p_FMOD_CODEC_DESCRIPTION), SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int), long1);
	}

	public static int FMOD_System_RegisterDSP(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_DSP_DESCRIPTION sWIGTYPE_p_FMOD_DSP_DESCRIPTION, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int) {
		return javafmodJNI.FMOD_System_RegisterDSP(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_DSP_DESCRIPTION.getCPtr(sWIGTYPE_p_FMOD_DSP_DESCRIPTION), SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int));
	}

	public static int FMOD_System_Init(int int1, long long1, long long2) {
		return javafmodJNI.FMOD_System_Init(int1, long1, long2);
	}

	public static int FMOD_System_Close(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM) {
		return javafmodJNI.FMOD_System_Close(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM));
	}

	public static int FMOD_System_Update() {
		return javafmodJNI.FMOD_System_Update();
	}

	public static int FMOD_System_SetSpeakerPosition(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_SPEAKER sWIGTYPE_p_FMOD_SPEAKER, float float1, float float2, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_System_SetSpeakerPosition(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_SPEAKER.getCPtr(sWIGTYPE_p_FMOD_SPEAKER), float1, float2, SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_System_GetSpeakerPosition(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_SPEAKER sWIGTYPE_p_FMOD_SPEAKER, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_float sWIGTYPE_p_float2, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_System_GetSpeakerPosition(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_SPEAKER.getCPtr(sWIGTYPE_p_FMOD_SPEAKER), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float2), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_System_SetStreamBufferSize(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, long long1, SWIGTYPE_p_FMOD_TIMEUNIT sWIGTYPE_p_FMOD_TIMEUNIT) {
		return javafmodJNI.FMOD_System_SetStreamBufferSize(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), long1, SWIGTYPE_p_FMOD_TIMEUNIT.getCPtr(sWIGTYPE_p_FMOD_TIMEUNIT));
	}

	public static int FMOD_System_GetStreamBufferSize(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int, SWIGTYPE_p_FMOD_TIMEUNIT sWIGTYPE_p_FMOD_TIMEUNIT) {
		return javafmodJNI.FMOD_System_GetStreamBufferSize(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int), SWIGTYPE_p_FMOD_TIMEUNIT.getCPtr(sWIGTYPE_p_FMOD_TIMEUNIT));
	}

	public static int FMOD_System_Set3DSettings(float float1, float float2, float float3) {
		return javafmodJNI.FMOD_System_Set3DSettings(float1, float2, float3);
	}

	public static int FMOD_System_Get3DSettings(SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_float sWIGTYPE_p_float2, SWIGTYPE_p_float sWIGTYPE_p_float3) {
		return javafmodJNI.FMOD_System_Get3DSettings(SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float2), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float3));
	}

	public static int FMOD_System_Set3DNumListeners(int int1) {
		return javafmodJNI.FMOD_System_Set3DNumListeners(int1);
	}

	public static int FMOD_System_Get3DNumListeners(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_System_Get3DNumListeners(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_System_Set3DListenerAttributes(int int1, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		return javafmodJNI.FMOD_System_Set3DListenerAttributes(int1, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12);
	}

	public static int FMOD_System_Get3DListenerAttributes(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, int int1, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR2, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR3, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR4) {
		return javafmodJNI.FMOD_System_Get3DListenerAttributes(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), int1, SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR2), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR3), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR4));
	}

	public static int FMOD_System_Set3DRolloffCallback(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_3D_ROLLOFF_CALLBACK sWIGTYPE_p_FMOD_3D_ROLLOFF_CALLBACK) {
		return javafmodJNI.FMOD_System_Set3DRolloffCallback(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_3D_ROLLOFF_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_3D_ROLLOFF_CALLBACK));
	}

	public static int FMOD_System_MixerSuspend(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM) {
		return javafmodJNI.FMOD_System_MixerSuspend(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM));
	}

	public static int FMOD_System_MixerResume(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM) {
		return javafmodJNI.FMOD_System_MixerResume(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM));
	}

	public static int FMOD_System_GetVersion(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int) {
		return javafmodJNI.FMOD_System_GetVersion(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int));
	}

	public static int FMOD_System_GetOutputHandle(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_p_void sWIGTYPE_p_p_void) {
		return javafmodJNI.FMOD_System_GetOutputHandle(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_p_void.getCPtr(sWIGTYPE_p_p_void));
	}

	public static int FMOD_System_GetChannelsPlaying(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_System_GetChannelsPlaying(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_System_GetCPUUsage(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_float sWIGTYPE_p_float2, SWIGTYPE_p_float sWIGTYPE_p_float3, SWIGTYPE_p_float sWIGTYPE_p_float4, SWIGTYPE_p_float sWIGTYPE_p_float5) {
		return javafmodJNI.FMOD_System_GetCPUUsage(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float2), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float3), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float4), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float5));
	}

	public static int FMOD_System_GetSoundRAM(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_int sWIGTYPE_p_int, SWIGTYPE_p_int sWIGTYPE_p_int2, SWIGTYPE_p_int sWIGTYPE_p_int3) {
		return javafmodJNI.FMOD_System_GetSoundRAM(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int2), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int3));
	}

	public static long FMOD_System_CreateSound(String string, long long1) {
		return javafmodJNI.FMOD_System_CreateSound(string, long1);
	}

	public static long FMOD_System_CreateRecordSound(long long1, long long2, long long3, long long4) {
		return javafmodJNI.FMOD_System_CreateRecordSound(long1, long2, long3, long4);
	}

	public static long FMOD_System_SetVADMode(int int1) {
		return javafmodJNI.FMOD_System_SetVADMode(int1);
	}

	public static long FMOD_System_SetRecordVolume(long long1) {
		return (long)javafmodJNI.FMOD_System_SetRecordVolume((int)long1);
	}

	public static long FMOD_System_CreateRAWPlaySound(long long1, long long2, long long3) {
		return javafmodJNI.FMOD_System_CreateRAWPlaySound(long1, long2, long3);
	}

	public static long FMOD_System_SetRawPlayBufferingPeriod(long long1) {
		return javafmodJNI.FMOD_System_SetRawPlayBufferingPeriod(long1);
	}

	public static int FMOD_System_RAWPlayData(long long1, short[] shortArray, long long2) {
		return javafmodJNI.FMOD_System_RAWPlayData(long1, shortArray, long2);
	}

	public static int FMOD_System_RAWPlayData(long long1, byte[] byteArray, long long2) {
		short[] shortArray = new short[byteArray.length / 2];
		ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortArray);
		return javafmodJNI.FMOD_System_RAWPlayData(long1, shortArray, long2 / 2L);
	}

	public static void FMOD_Studio_LoadSampleData(long long1) {
		javafmodJNI.FMOD_Studio_LoadSampleData(long1);
	}

	public static void FMOD_Studio_LoadEventSampleData(long long1) {
		javafmodJNI.FMOD_Studio_LoadEventSampleData(long1);
	}

	public static int FMOD_System_CreateStream(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, String string, SWIGTYPE_p_FMOD_MODE sWIGTYPE_p_FMOD_MODE, SWIGTYPE_p_FMOD_CREATESOUNDEXINFO sWIGTYPE_p_FMOD_CREATESOUNDEXINFO, SWIGTYPE_p_p_FMOD_SOUND sWIGTYPE_p_p_FMOD_SOUND) {
		return javafmodJNI.FMOD_System_CreateStream(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), string, SWIGTYPE_p_FMOD_MODE.getCPtr(sWIGTYPE_p_FMOD_MODE), SWIGTYPE_p_FMOD_CREATESOUNDEXINFO.getCPtr(sWIGTYPE_p_FMOD_CREATESOUNDEXINFO), SWIGTYPE_p_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_p_FMOD_SOUND));
	}

	public static int FMOD_System_CreateDSP(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_DSP_DESCRIPTION sWIGTYPE_p_FMOD_DSP_DESCRIPTION, SWIGTYPE_p_p_FMOD_DSP sWIGTYPE_p_p_FMOD_DSP) {
		return javafmodJNI.FMOD_System_CreateDSP(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_DSP_DESCRIPTION.getCPtr(sWIGTYPE_p_FMOD_DSP_DESCRIPTION), SWIGTYPE_p_p_FMOD_DSP.getCPtr(sWIGTYPE_p_p_FMOD_DSP));
	}

	public static int FMOD_System_CreateDSPByType(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_DSP_TYPE sWIGTYPE_p_FMOD_DSP_TYPE, SWIGTYPE_p_p_FMOD_DSP sWIGTYPE_p_p_FMOD_DSP) {
		return javafmodJNI.FMOD_System_CreateDSPByType(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_DSP_TYPE.getCPtr(sWIGTYPE_p_FMOD_DSP_TYPE), SWIGTYPE_p_p_FMOD_DSP.getCPtr(sWIGTYPE_p_p_FMOD_DSP));
	}

	public static long FMOD_System_CreateChannelGroup(String string) {
		try {
			return javafmodJNI.FMOD_System_CreateChannelGroup(string);
		} catch (Throwable throwable) {
			DebugLog.log("ERROR: FMOD_System_CreateChannelGroup exception:" + throwable.getMessage());
			return 0L;
		}
	}

	public static int FMOD_System_CreateSoundGroup(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, String string, SWIGTYPE_p_p_FMOD_SOUNDGROUP sWIGTYPE_p_p_FMOD_SOUNDGROUP) {
		return javafmodJNI.FMOD_System_CreateSoundGroup(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), string, SWIGTYPE_p_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_p_FMOD_SOUNDGROUP));
	}

	public static int FMOD_System_CreateReverb3D(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_p_FMOD_REVERB3D sWIGTYPE_p_p_FMOD_REVERB3D) {
		return javafmodJNI.FMOD_System_CreateReverb3D(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_p_FMOD_REVERB3D.getCPtr(sWIGTYPE_p_p_FMOD_REVERB3D));
	}

	public static long FMOD_System_PlaySound(long long1, boolean boolean1) {
		return javafmodJNI.FMOD_System_PlaySound(long1, boolean1 ? 1L : 0L);
	}

	public static int FMOD_System_PlayDSP() {
		return javafmodJNI.FMOD_System_PlayDSP();
	}

	public static int FMOD_System_GetChannel(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, int int1, SWIGTYPE_p_p_FMOD_CHANNEL sWIGTYPE_p_p_FMOD_CHANNEL) {
		return javafmodJNI.FMOD_System_GetChannel(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), int1, SWIGTYPE_p_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_p_FMOD_CHANNEL));
	}

	public static long FMOD_System_GetMasterChannelGroup() {
		return javafmodJNI.FMOD_System_GetMasterChannelGroup();
	}

	public static int FMOD_System_GetMasterSoundGroup(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_p_FMOD_SOUNDGROUP sWIGTYPE_p_p_FMOD_SOUNDGROUP) {
		return javafmodJNI.FMOD_System_GetMasterSoundGroup(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_p_FMOD_SOUNDGROUP));
	}

	public static int FMOD_System_AttachChannelGroupToPort(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_PORT_TYPE sWIGTYPE_p_FMOD_PORT_TYPE, SWIGTYPE_p_FMOD_PORT_INDEX sWIGTYPE_p_FMOD_PORT_INDEX, SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_System_AttachChannelGroupToPort(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_PORT_TYPE.getCPtr(sWIGTYPE_p_FMOD_PORT_TYPE), SWIGTYPE_p_FMOD_PORT_INDEX.getCPtr(sWIGTYPE_p_FMOD_PORT_INDEX), SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_System_DetachChannelGroupFromPort(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP) {
		return javafmodJNI.FMOD_System_DetachChannelGroupFromPort(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP));
	}

	public static int FMOD_System_SetReverbProperties(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, int int1, SWIGTYPE_p_FMOD_REVERB_PROPERTIES sWIGTYPE_p_FMOD_REVERB_PROPERTIES) {
		return javafmodJNI.FMOD_System_SetReverbProperties(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), int1, SWIGTYPE_p_FMOD_REVERB_PROPERTIES.getCPtr(sWIGTYPE_p_FMOD_REVERB_PROPERTIES));
	}

	public static int FMOD_System_GetReverbProperties(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, int int1, SWIGTYPE_p_FMOD_REVERB_PROPERTIES sWIGTYPE_p_FMOD_REVERB_PROPERTIES) {
		return javafmodJNI.FMOD_System_GetReverbProperties(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), int1, SWIGTYPE_p_FMOD_REVERB_PROPERTIES.getCPtr(sWIGTYPE_p_FMOD_REVERB_PROPERTIES));
	}

	public static int FMOD_System_LockDSP(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM) {
		return javafmodJNI.FMOD_System_LockDSP(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM));
	}

	public static int FMOD_System_UnlockDSP(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM) {
		return javafmodJNI.FMOD_System_UnlockDSP(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM));
	}

	public static int FMOD_System_GetRecordNumDrivers() {
		return javafmodJNI.FMOD_System_GetRecordNumDrivers();
	}

	public static int FMOD_System_GetRecordDriverInfo(int int1, FMOD_DriverInfo fMOD_DriverInfo) {
		return javafmodJNI.FMOD_System_GetRecordDriverInfo(int1, fMOD_DriverInfo);
	}

	public static int FMOD_System_GetRecordPosition(int int1, Long Long1) {
		return javafmodJNI.FMOD_System_GetRecordPosition(int1, Long1);
	}

	public static int FMOD_System_RecordStart(int int1, long long1, boolean boolean1) {
		return javafmodJNI.FMOD_System_RecordStart(int1, long1, boolean1);
	}

	public static int FMOD_System_RecordStop(int int1) {
		return javafmodJNI.FMOD_System_RecordStop(int1);
	}

	public static int FMOD_System_IsRecording(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, int int1, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_System_IsRecording(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), int1, SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_System_CreateGeometry(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, int int1, int int2, SWIGTYPE_p_p_FMOD_GEOMETRY sWIGTYPE_p_p_FMOD_GEOMETRY) {
		return javafmodJNI.FMOD_System_CreateGeometry(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), int1, int2, SWIGTYPE_p_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_p_FMOD_GEOMETRY));
	}

	public static int FMOD_System_SetGeometrySettings(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, float float1) {
		return javafmodJNI.FMOD_System_SetGeometrySettings(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), float1);
	}

	public static int FMOD_System_GetGeometrySettings(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_System_GetGeometrySettings(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_System_LoadGeometry(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_void sWIGTYPE_p_void, int int1, SWIGTYPE_p_p_FMOD_GEOMETRY sWIGTYPE_p_p_FMOD_GEOMETRY) {
		return javafmodJNI.FMOD_System_LoadGeometry(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void), int1, SWIGTYPE_p_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_p_FMOD_GEOMETRY));
	}

	public static int FMOD_System_GetGeometryOcclusion(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR2, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_float sWIGTYPE_p_float2) {
		return javafmodJNI.FMOD_System_GetGeometryOcclusion(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR2), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float2));
	}

	public static int FMOD_System_SetNetworkProxy(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, String string) {
		return javafmodJNI.FMOD_System_SetNetworkProxy(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), string);
	}

	public static int FMOD_System_GetNetworkProxy(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, String string, int int1) {
		return javafmodJNI.FMOD_System_GetNetworkProxy(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), string, int1);
	}

	public static int FMOD_System_SetNetworkTimeout(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, int int1) {
		return javafmodJNI.FMOD_System_SetNetworkTimeout(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), int1);
	}

	public static int FMOD_System_GetNetworkTimeout(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_System_GetNetworkTimeout(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_System_SetUserData(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_void sWIGTYPE_p_void) {
		return javafmodJNI.FMOD_System_SetUserData(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void));
	}

	public static int FMOD_System_GetUserData(SWIGTYPE_p_FMOD_SYSTEM sWIGTYPE_p_FMOD_SYSTEM, SWIGTYPE_p_p_void sWIGTYPE_p_p_void) {
		return javafmodJNI.FMOD_System_GetUserData(SWIGTYPE_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_FMOD_SYSTEM), SWIGTYPE_p_p_void.getCPtr(sWIGTYPE_p_p_void));
	}

	public static int FMOD_Sound_Release(long long1) {
		return javafmodJNI.FMOD_Sound_Release(long1);
	}

	public static int FMOD_RAWPlaySound_Release(long long1) {
		return javafmodJNI.FMOD_RAWPlaySound_Release(long1);
	}

	public static int FMOD_RecordSound_Release(long long1) {
		return javafmodJNI.FMOD_RecordSound_Release(long1);
	}

	public static int FMOD_Sound_GetSystemObject(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_p_FMOD_SYSTEM sWIGTYPE_p_p_FMOD_SYSTEM) {
		return javafmodJNI.FMOD_Sound_GetSystemObject(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_p_FMOD_SYSTEM));
	}

	public static int FMOD_Sound_Lock(long long1, long long2, long long3, byte[] byteArray, byte[] byteArray2, Long Long1, Long Long2, long[] longArray) {
		return javafmodJNI.FMOD_Sound_Lock(long1, long2, long3, byteArray, byteArray2, Long1, Long2, longArray);
	}

	public static int FMOD_Sound_GetData(long long1, byte[] byteArray, Long Long1, Long Long2, Long Long3) {
		return javafmodJNI.FMOD_Sound_GetData(long1, byteArray, Long1, Long2, Long3);
	}

	public static int FMOD_Sound_Unlock(long long1, long[] longArray) {
		return javafmodJNI.FMOD_Sound_Unlock(long1, longArray);
	}

	public static int FMOD_Sound_SetDefaults(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, float float1, int int1) {
		return javafmodJNI.FMOD_Sound_SetDefaults(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), float1, int1);
	}

	public static int FMOD_Sound_GetDefaults(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_Sound_GetDefaults(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_Sound_Set3DMinMaxDistance(long long1, float float1, float float2) {
		return javafmodJNI.FMOD_Sound_Set3DMinMaxDistance(long1, float1, float2);
	}

	public static int FMOD_Sound_Get3DMinMaxDistance(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_float sWIGTYPE_p_float2) {
		return javafmodJNI.FMOD_Sound_Get3DMinMaxDistance(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float2));
	}

	public static long FMOD_Studio_System_Create() {
		return javafmodJNI.FMOD_Studio_Create();
	}

	public static void FMOD_Studio_StartEvent(long long1) {
		javafmodJNI.FMOD_Studio_StartEvent(long1);
	}

	public static long FMOD_Studio_GetTimelinePosition(long long1) {
		return javafmodJNI.FMOD_Studio_GetTimelinePosition(long1);
	}

	public static long FMOD_Studio_System_GetEvent(String string) {
		if (map.containsKey(string)) {
			return (Long)map.get(string);
		} else {
			long long1 = javafmodJNI.FMOD_Studio_GetEvent(string);
			map.put(string, long1);
			return long1;
		}
	}

	public static long FMOD_Studio_System_CreateEventInstance(long long1) {
		return javafmodJNI.FMOD_Studio_CreateEventInstance(long1);
	}

	public static long FMOD_Studio_System_LoadBankFile(String string) {
		return javafmodJNI.FMOD_Studio_LoadBankFile(string);
	}

	public static int FMOD_Sound_Set3DConeSettings(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, float float1, float float2, float float3) {
		return javafmodJNI.FMOD_Sound_Set3DConeSettings(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), float1, float2, float3);
	}

	public static int FMOD_Sound_Get3DConeSettings(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_float sWIGTYPE_p_float2, SWIGTYPE_p_float sWIGTYPE_p_float3) {
		return javafmodJNI.FMOD_Sound_Get3DConeSettings(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float2), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float3));
	}

	public static int FMOD_Sound_Set3DCustomRolloff(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR, int int1) {
		return javafmodJNI.FMOD_Sound_Set3DCustomRolloff(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR), int1);
	}

	public static int FMOD_Sound_Get3DCustomRolloff(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_p_FMOD_VECTOR sWIGTYPE_p_p_FMOD_VECTOR, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_Sound_Get3DCustomRolloff(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_p_FMOD_VECTOR), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_Sound_SetSubSound(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, int int1, SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND2) {
		return javafmodJNI.FMOD_Sound_SetSubSound(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), int1, SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND2));
	}

	public static int FMOD_Sound_GetSubSound(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, int int1, SWIGTYPE_p_p_FMOD_SOUND sWIGTYPE_p_p_FMOD_SOUND) {
		return javafmodJNI.FMOD_Sound_GetSubSound(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), int1, SWIGTYPE_p_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_p_FMOD_SOUND));
	}

	public static int FMOD_Sound_GetSubSoundParent(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_p_FMOD_SOUND sWIGTYPE_p_p_FMOD_SOUND) {
		return javafmodJNI.FMOD_Sound_GetSubSoundParent(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_p_FMOD_SOUND));
	}

	public static int FMOD_Sound_GetName(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, String string, int int1) {
		return javafmodJNI.FMOD_Sound_GetName(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), string, int1);
	}

	public static long FMOD_Sound_GetLength(long long1, long long2) {
		return (long)javafmodJNI.FMOD_Sound_GetLength(long1, long2);
	}

	public static int FMOD_Sound_GetFormat(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_FMOD_SOUND_TYPE sWIGTYPE_p_FMOD_SOUND_TYPE, SWIGTYPE_p_FMOD_SOUND_FORMAT sWIGTYPE_p_FMOD_SOUND_FORMAT, SWIGTYPE_p_int sWIGTYPE_p_int, SWIGTYPE_p_int sWIGTYPE_p_int2) {
		return javafmodJNI.FMOD_Sound_GetFormat(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_FMOD_SOUND_TYPE.getCPtr(sWIGTYPE_p_FMOD_SOUND_TYPE), SWIGTYPE_p_FMOD_SOUND_FORMAT.getCPtr(sWIGTYPE_p_FMOD_SOUND_FORMAT), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int2));
	}

	public static int FMOD_Sound_GetNumSubSounds(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_Sound_GetNumSubSounds(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_Sound_GetNumTags(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_int sWIGTYPE_p_int, SWIGTYPE_p_int sWIGTYPE_p_int2) {
		return javafmodJNI.FMOD_Sound_GetNumTags(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int2));
	}

	public static int FMOD_Sound_GetTag(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, String string, int int1, SWIGTYPE_p_FMOD_TAG sWIGTYPE_p_FMOD_TAG) {
		return javafmodJNI.FMOD_Sound_GetTag(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), string, int1, SWIGTYPE_p_FMOD_TAG.getCPtr(sWIGTYPE_p_FMOD_TAG));
	}

	public static int FMOD_Sound_GetOpenState(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_FMOD_OPENSTATE sWIGTYPE_p_FMOD_OPENSTATE, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL2) {
		return javafmodJNI.FMOD_Sound_GetOpenState(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_FMOD_OPENSTATE.getCPtr(sWIGTYPE_p_FMOD_OPENSTATE), SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL2));
	}

	public static int FMOD_Sound_ReadData(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_void sWIGTYPE_p_void, long long1, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int) {
		return javafmodJNI.FMOD_Sound_ReadData(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void), long1, SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int));
	}

	public static int FMOD_Sound_SeekData(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, long long1) {
		return javafmodJNI.FMOD_Sound_SeekData(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), long1);
	}

	public static int FMOD_Sound_SetSoundGroup(long long1, long long2) {
		return javafmodJNI.FMOD_Sound_SetSoundGroup(long1, long2);
	}

	public static int FMOD_Sound_GetSoundGroup(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_p_FMOD_SOUNDGROUP sWIGTYPE_p_p_FMOD_SOUNDGROUP) {
		return javafmodJNI.FMOD_Sound_GetSoundGroup(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_p_FMOD_SOUNDGROUP));
	}

	public static int FMOD_Sound_GetNumSyncPoints(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_Sound_GetNumSyncPoints(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_Sound_GetSyncPoint(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, int int1, SWIGTYPE_p_p_FMOD_SYNCPOINT sWIGTYPE_p_p_FMOD_SYNCPOINT) {
		return javafmodJNI.FMOD_Sound_GetSyncPoint(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), int1, SWIGTYPE_p_p_FMOD_SYNCPOINT.getCPtr(sWIGTYPE_p_p_FMOD_SYNCPOINT));
	}

	public static int FMOD_Sound_GetSyncPointInfo(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_FMOD_SYNCPOINT sWIGTYPE_p_FMOD_SYNCPOINT, String string, int int1, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int, SWIGTYPE_p_FMOD_TIMEUNIT sWIGTYPE_p_FMOD_TIMEUNIT) {
		return javafmodJNI.FMOD_Sound_GetSyncPointInfo(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_FMOD_SYNCPOINT.getCPtr(sWIGTYPE_p_FMOD_SYNCPOINT), string, int1, SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int), SWIGTYPE_p_FMOD_TIMEUNIT.getCPtr(sWIGTYPE_p_FMOD_TIMEUNIT));
	}

	public static int FMOD_Sound_AddSyncPoint(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, long long1, SWIGTYPE_p_FMOD_TIMEUNIT sWIGTYPE_p_FMOD_TIMEUNIT, String string, SWIGTYPE_p_p_FMOD_SYNCPOINT sWIGTYPE_p_p_FMOD_SYNCPOINT) {
		return javafmodJNI.FMOD_Sound_AddSyncPoint(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), long1, SWIGTYPE_p_FMOD_TIMEUNIT.getCPtr(sWIGTYPE_p_FMOD_TIMEUNIT), string, SWIGTYPE_p_p_FMOD_SYNCPOINT.getCPtr(sWIGTYPE_p_p_FMOD_SYNCPOINT));
	}

	public static int FMOD_Sound_DeleteSyncPoint(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_FMOD_SYNCPOINT sWIGTYPE_p_FMOD_SYNCPOINT) {
		return javafmodJNI.FMOD_Sound_DeleteSyncPoint(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_FMOD_SYNCPOINT.getCPtr(sWIGTYPE_p_FMOD_SYNCPOINT));
	}

	public static int FMOD_Sound_SetMode(long long1, int int1) {
		return javafmodJNI.FMOD_Sound_SetMode(long1, (long)int1);
	}

	public static int FMOD_Sound_GetMode(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_FMOD_MODE sWIGTYPE_p_FMOD_MODE) {
		return javafmodJNI.FMOD_Sound_GetMode(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_FMOD_MODE.getCPtr(sWIGTYPE_p_FMOD_MODE));
	}

	public static int FMOD_Sound_SetLoopCount(long long1, int int1) {
		return javafmodJNI.FMOD_Sound_SetLoopCount(long1, int1);
	}

	public static int FMOD_Sound_GetLoopCount(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_Sound_GetLoopCount(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_Sound_SetLoopPoints(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, long long1, SWIGTYPE_p_FMOD_TIMEUNIT sWIGTYPE_p_FMOD_TIMEUNIT, long long2, SWIGTYPE_p_FMOD_TIMEUNIT sWIGTYPE_p_FMOD_TIMEUNIT2) {
		return javafmodJNI.FMOD_Sound_SetLoopPoints(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), long1, SWIGTYPE_p_FMOD_TIMEUNIT.getCPtr(sWIGTYPE_p_FMOD_TIMEUNIT), long2, SWIGTYPE_p_FMOD_TIMEUNIT.getCPtr(sWIGTYPE_p_FMOD_TIMEUNIT2));
	}

	public static int FMOD_Sound_GetLoopPoints(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int, SWIGTYPE_p_FMOD_TIMEUNIT sWIGTYPE_p_FMOD_TIMEUNIT, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int2, SWIGTYPE_p_FMOD_TIMEUNIT sWIGTYPE_p_FMOD_TIMEUNIT2) {
		return javafmodJNI.FMOD_Sound_GetLoopPoints(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int), SWIGTYPE_p_FMOD_TIMEUNIT.getCPtr(sWIGTYPE_p_FMOD_TIMEUNIT), SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int2), SWIGTYPE_p_FMOD_TIMEUNIT.getCPtr(sWIGTYPE_p_FMOD_TIMEUNIT2));
	}

	public static int FMOD_Sound_GetMusicNumChannels(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_Sound_GetMusicNumChannels(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_Sound_SetMusicChannelVolume(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, int int1, float float1) {
		return javafmodJNI.FMOD_Sound_SetMusicChannelVolume(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), int1, float1);
	}

	public static int FMOD_Sound_GetMusicChannelVolume(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, int int1, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_Sound_GetMusicChannelVolume(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), int1, SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_Sound_SetMusicSpeed(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, float float1) {
		return javafmodJNI.FMOD_Sound_SetMusicSpeed(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), float1);
	}

	public static int FMOD_Sound_GetMusicSpeed(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_Sound_GetMusicSpeed(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_Sound_SetUserData(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_void sWIGTYPE_p_void) {
		return javafmodJNI.FMOD_Sound_SetUserData(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void));
	}

	public static int FMOD_Sound_GetUserData(SWIGTYPE_p_FMOD_SOUND sWIGTYPE_p_FMOD_SOUND, SWIGTYPE_p_p_void sWIGTYPE_p_p_void) {
		return javafmodJNI.FMOD_Sound_GetUserData(SWIGTYPE_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_FMOD_SOUND), SWIGTYPE_p_p_void.getCPtr(sWIGTYPE_p_p_void));
	}

	public static int FMOD_Channel_GetSystemObject(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_p_FMOD_SYSTEM sWIGTYPE_p_p_FMOD_SYSTEM) {
		return javafmodJNI.FMOD_Channel_GetSystemObject(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_p_FMOD_SYSTEM));
	}

	public static int FMOD_Channel_Stop(long long1) {
		return javafmodJNI.FMOD_Channel_Stop(long1);
	}

	public static int FMOD_Channel_SetPaused(long long1, boolean boolean1) {
		return javafmodJNI.FMOD_Channel_SetPaused(long1, boolean1 ? 1L : 0L);
	}

	public static int FMOD_Channel_GetPaused(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_Channel_GetPaused(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_Channel_SetVolume(long long1, float float1) {
		return javafmodJNI.FMOD_Channel_SetVolume(long1, float1);
	}

	public static int FMOD_Channel_GetVolume(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_Channel_GetVolume(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_Channel_SetVolumeRamp(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_Channel_SetVolumeRamp(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_Channel_GetVolumeRamp(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_Channel_GetVolumeRamp(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static float FMOD_Channel_GetAudibility(long long1) {
		return javafmodJNI.FMOD_Channel_GetAudibility(long1);
	}

	public static int FMOD_Channel_SetPitch(long long1, float float1) {
		return javafmodJNI.FMOD_Channel_SetPitch(long1, float1);
	}

	public static int FMOD_Channel_SetPitch(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, float float1) {
		return javafmodJNI.FMOD_Channel_SetPitch(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), float1);
	}

	public static int FMOD_Channel_GetPitch(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_Channel_GetPitch(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_Channel_SetMute(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_Channel_SetMute(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_Channel_GetMute(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_Channel_GetMute(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_Channel_SetReverbProperties(long long1, int int1, float float1) {
		return javafmodJNI.FMOD_Channel_SetReverbProperties(long1, int1, float1);
	}

	public static int FMOD_Channel_GetReverbProperties(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, int int1, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_Channel_GetReverbProperties(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), int1, SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_Channel_SetLowPassGain(long long1, float float1) {
		return javafmodJNI.FMOD_Channel_SetLowPassGain(long1, float1);
	}

	public static int FMOD_Channel_GetLowPassGain(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_Channel_GetLowPassGain(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_Channel_SetMode(long long1, long long2) {
		return javafmodJNI.FMOD_Channel_SetMode(long1, long2);
	}

	public static int FMOD_Channel_GetMode(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_FMOD_MODE sWIGTYPE_p_FMOD_MODE) {
		return javafmodJNI.FMOD_Channel_GetMode(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_FMOD_MODE.getCPtr(sWIGTYPE_p_FMOD_MODE));
	}

	public static int FMOD_Channel_SetCallback(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_FMOD_CHANNELCONTROL_CALLBACK sWIGTYPE_p_FMOD_CHANNELCONTROL_CALLBACK) {
		return javafmodJNI.FMOD_Channel_SetCallback(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_FMOD_CHANNELCONTROL_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_CHANNELCONTROL_CALLBACK));
	}

	public static boolean FMOD_Channel_IsPlaying(long long1) {
		return javafmodJNI.FMOD_Channel_IsPlaying(long1);
	}

	public static int FMOD_Channel_SetPan(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, float float1) {
		return javafmodJNI.FMOD_Channel_SetPan(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), float1);
	}

	public static int FMOD_Channel_SetMixLevelsOutput(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		return javafmodJNI.FMOD_Channel_SetMixLevelsOutput(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), float1, float2, float3, float4, float5, float6, float7, float8);
	}

	public static int FMOD_Channel_SetMixLevelsInput(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_float sWIGTYPE_p_float, int int1) {
		return javafmodJNI.FMOD_Channel_SetMixLevelsInput(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), int1);
	}

	public static int FMOD_Channel_SetMixMatrix(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_float sWIGTYPE_p_float, int int1, int int2, int int3) {
		return javafmodJNI.FMOD_Channel_SetMixMatrix(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), int1, int2, int3);
	}

	public static int FMOD_Channel_GetMixMatrix(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_int sWIGTYPE_p_int, SWIGTYPE_p_int sWIGTYPE_p_int2, int int1) {
		return javafmodJNI.FMOD_Channel_GetMixMatrix(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int2), int1);
	}

	public static int FMOD_Channel_GetDSPClock(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_unsigned_long_long sWIGTYPE_p_unsigned_long_long, SWIGTYPE_p_unsigned_long_long sWIGTYPE_p_unsigned_long_long2) {
		return javafmodJNI.FMOD_Channel_GetDSPClock(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_unsigned_long_long.getCPtr(sWIGTYPE_p_unsigned_long_long), SWIGTYPE_p_unsigned_long_long.getCPtr(sWIGTYPE_p_unsigned_long_long2));
	}

	public static int FMOD_Channel_SetDelay(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, BigInteger bigInteger, BigInteger bigInteger2, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_Channel_SetDelay(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), bigInteger, bigInteger2, SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_Channel_GetDelay(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_unsigned_long_long sWIGTYPE_p_unsigned_long_long, SWIGTYPE_p_unsigned_long_long sWIGTYPE_p_unsigned_long_long2, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_Channel_GetDelay(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_unsigned_long_long.getCPtr(sWIGTYPE_p_unsigned_long_long), SWIGTYPE_p_unsigned_long_long.getCPtr(sWIGTYPE_p_unsigned_long_long2), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_Channel_AddFadePoint(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, BigInteger bigInteger, float float1) {
		return javafmodJNI.FMOD_Channel_AddFadePoint(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), bigInteger, float1);
	}

	public static int FMOD_Channel_RemoveFadePoints(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, BigInteger bigInteger, BigInteger bigInteger2) {
		return javafmodJNI.FMOD_Channel_RemoveFadePoints(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), bigInteger, bigInteger2);
	}

	public static int FMOD_Channel_GetFadePoints(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int, SWIGTYPE_p_unsigned_long_long sWIGTYPE_p_unsigned_long_long, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_Channel_GetFadePoints(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int), SWIGTYPE_p_unsigned_long_long.getCPtr(sWIGTYPE_p_unsigned_long_long), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_Channel_GetDSP(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, int int1, SWIGTYPE_p_p_FMOD_DSP sWIGTYPE_p_p_FMOD_DSP) {
		return javafmodJNI.FMOD_Channel_GetDSP(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), int1, SWIGTYPE_p_p_FMOD_DSP.getCPtr(sWIGTYPE_p_p_FMOD_DSP));
	}

	public static int FMOD_Channel_AddDSP(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, int int1, SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP) {
		return javafmodJNI.FMOD_Channel_AddDSP(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), int1, SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP));
	}

	public static int FMOD_Channel_RemoveDSP(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP) {
		return javafmodJNI.FMOD_Channel_RemoveDSP(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP));
	}

	public static int FMOD_Channel_GetNumDSPs(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_Channel_GetNumDSPs(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_Channel_SetDSPIndex(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, int int1) {
		return javafmodJNI.FMOD_Channel_SetDSPIndex(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), int1);
	}

	public static int FMOD_Channel_GetDSPIndex(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_Channel_GetDSPIndex(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_Channel_OverridePanDSP(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP) {
		return javafmodJNI.FMOD_Channel_OverridePanDSP(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP));
	}

	public static int FMOD_Channel_Set3DAttributes(long long1, float float1, float float2, float float3, float float4, float float5, float float6) {
		return javafmodJNI.FMOD_Channel_Set3DAttributes(long1, float1, float2, float3, float4, float5, float6);
	}

	public static int FMOD_Channel_Get3DAttributes(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR2, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR3) {
		return javafmodJNI.FMOD_Channel_Get3DAttributes(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR2), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR3));
	}

	public static int FMOD_Channel_Set3DMinMaxDistance(long long1, float float1, float float2) {
		return javafmodJNI.FMOD_Channel_Set3DMinMaxDistance(long1, float1, float2);
	}

	public static int FMOD_Channel_Get3DMinMaxDistance(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_float sWIGTYPE_p_float2) {
		return javafmodJNI.FMOD_Channel_Get3DMinMaxDistance(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float2));
	}

	public static int FMOD_Channel_Set3DConeSettings(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, float float1, float float2, float float3) {
		return javafmodJNI.FMOD_Channel_Set3DConeSettings(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), float1, float2, float3);
	}

	public static int FMOD_Channel_Get3DConeSettings(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_float sWIGTYPE_p_float2, SWIGTYPE_p_float sWIGTYPE_p_float3) {
		return javafmodJNI.FMOD_Channel_Get3DConeSettings(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float2), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float3));
	}

	public static int FMOD_Channel_Set3DConeOrientation(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR) {
		return javafmodJNI.FMOD_Channel_Set3DConeOrientation(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR));
	}

	public static int FMOD_Channel_Get3DConeOrientation(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR) {
		return javafmodJNI.FMOD_Channel_Get3DConeOrientation(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR));
	}

	public static int FMOD_Channel_Set3DCustomRolloff(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR, int int1) {
		return javafmodJNI.FMOD_Channel_Set3DCustomRolloff(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR), int1);
	}

	public static int FMOD_Channel_Get3DCustomRolloff(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_p_FMOD_VECTOR sWIGTYPE_p_p_FMOD_VECTOR, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_Channel_Get3DCustomRolloff(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_p_FMOD_VECTOR), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_Channel_Set3DOcclusion(long long1, float float1, float float2) {
		return javafmodJNI.FMOD_Channel_Set3DOcclusion(long1, float1, float2);
	}

	public static int FMOD_Channel_Get3DOcclusion(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_float sWIGTYPE_p_float2) {
		return javafmodJNI.FMOD_Channel_Get3DOcclusion(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float2));
	}

	public static int FMOD_Channel_Set3DSpread(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, float float1) {
		return javafmodJNI.FMOD_Channel_Set3DSpread(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), float1);
	}

	public static int FMOD_Channel_Get3DSpread(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_Channel_Get3DSpread(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_Channel_Set3DLevel(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, float float1) {
		return javafmodJNI.FMOD_Channel_Set3DLevel(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), float1);
	}

	public static int FMOD_Channel_Get3DLevel(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_Channel_Get3DLevel(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_Channel_Set3DDopplerLevel(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, float float1) {
		return javafmodJNI.FMOD_Channel_Set3DDopplerLevel(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), float1);
	}

	public static int FMOD_Channel_Get3DDopplerLevel(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_Channel_Get3DDopplerLevel(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_Channel_Set3DDistanceFilter(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL, float float1, float float2) {
		return javafmodJNI.FMOD_Channel_Set3DDistanceFilter(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL), float1, float2);
	}

	public static int FMOD_Channel_Get3DDistanceFilter(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_float sWIGTYPE_p_float2) {
		return javafmodJNI.FMOD_Channel_Get3DDistanceFilter(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float2));
	}

	public static int FMOD_Channel_SetUserData(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_void sWIGTYPE_p_void) {
		return javafmodJNI.FMOD_Channel_SetUserData(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void));
	}

	public static int FMOD_Channel_GetUserData(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_p_void sWIGTYPE_p_p_void) {
		return javafmodJNI.FMOD_Channel_GetUserData(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_p_void.getCPtr(sWIGTYPE_p_p_void));
	}

	public static int FMOD_Channel_SetFrequency(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, float float1) {
		return javafmodJNI.FMOD_Channel_SetFrequency(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), float1);
	}

	public static int FMOD_Channel_GetFrequency(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_Channel_GetFrequency(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_Channel_SetPriority(long long1, int int1) {
		return javafmodJNI.FMOD_Channel_SetPriority(long1, int1);
	}

	public static int FMOD_Channel_GetPriority(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_Channel_GetPriority(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_Channel_SetPosition(long long1, long long2) {
		return javafmodJNI.FMOD_Channel_SetPosition(long1, long2);
	}

	public static long FMOD_Channel_GetPosition(long long1, int int1) {
		return javafmodJNI.FMOD_Channel_GetPosition(long1, (long)int1);
	}

	public static int FMOD_Channel_SetChannelGroup(long long1, long long2) {
		return javafmodJNI.FMOD_Channel_SetChannelGroup(long1, long2);
	}

	public static int FMOD_Channel_GetChannelGroup(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_p_FMOD_CHANNELGROUP sWIGTYPE_p_p_FMOD_CHANNELGROUP) {
		return javafmodJNI.FMOD_Channel_GetChannelGroup(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_p_FMOD_CHANNELGROUP));
	}

	public static int FMOD_Channel_SetLoopCount(long long1, int int1) {
		return javafmodJNI.FMOD_Channel_SetLoopCount(long1, int1);
	}

	public static int FMOD_Channel_GetLoopCount(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_Channel_GetLoopCount(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_Channel_SetLoopPoints(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, long long1, SWIGTYPE_p_FMOD_TIMEUNIT sWIGTYPE_p_FMOD_TIMEUNIT, long long2, SWIGTYPE_p_FMOD_TIMEUNIT sWIGTYPE_p_FMOD_TIMEUNIT2) {
		return javafmodJNI.FMOD_Channel_SetLoopPoints(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), long1, SWIGTYPE_p_FMOD_TIMEUNIT.getCPtr(sWIGTYPE_p_FMOD_TIMEUNIT), long2, SWIGTYPE_p_FMOD_TIMEUNIT.getCPtr(sWIGTYPE_p_FMOD_TIMEUNIT2));
	}

	public static int FMOD_Channel_GetLoopPoints(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int, SWIGTYPE_p_FMOD_TIMEUNIT sWIGTYPE_p_FMOD_TIMEUNIT, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int2, SWIGTYPE_p_FMOD_TIMEUNIT sWIGTYPE_p_FMOD_TIMEUNIT2) {
		return javafmodJNI.FMOD_Channel_GetLoopPoints(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int), SWIGTYPE_p_FMOD_TIMEUNIT.getCPtr(sWIGTYPE_p_FMOD_TIMEUNIT), SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int2), SWIGTYPE_p_FMOD_TIMEUNIT.getCPtr(sWIGTYPE_p_FMOD_TIMEUNIT2));
	}

	public static boolean FMOD_Channel_IsVirtual(long long1) {
		return javafmodJNI.FMOD_Channel_IsVirtual(long1);
	}

	public static int FMOD_Channel_GetCurrentSound(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_p_FMOD_SOUND sWIGTYPE_p_p_FMOD_SOUND) {
		return javafmodJNI.FMOD_Channel_GetCurrentSound(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_p_FMOD_SOUND));
	}

	public static int FMOD_Channel_GetIndex(SWIGTYPE_p_FMOD_CHANNEL sWIGTYPE_p_FMOD_CHANNEL, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_Channel_GetIndex(SWIGTYPE_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_FMOD_CHANNEL), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_ChannelGroup_GetSystemObject(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_p_FMOD_SYSTEM sWIGTYPE_p_p_FMOD_SYSTEM) {
		return javafmodJNI.FMOD_ChannelGroup_GetSystemObject(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_p_FMOD_SYSTEM));
	}

	public static int FMOD_ChannelGroup_Stop(long long1) {
		return javafmodJNI.FMOD_ChannelGroup_Stop(long1);
	}

	public static int FMOD_ChannelGroup_SetPaused(long long1, boolean boolean1) {
		return javafmodJNI.FMOD_ChannelGroup_SetPaused(long1, boolean1);
	}

	public static int FMOD_ChannelGroup_GetPaused(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_ChannelGroup_GetPaused(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_ChannelGroup_SetVolume(long long1, float float1) {
		return javafmodJNI.FMOD_ChannelGroup_SetVolume(long1, float1);
	}

	public static int FMOD_ChannelGroup_GetVolume(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_ChannelGroup_GetVolume(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_ChannelGroup_SetVolumeRamp(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_ChannelGroup_SetVolumeRamp(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_ChannelGroup_GetVolumeRamp(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_ChannelGroup_GetVolumeRamp(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_ChannelGroup_GetAudibility(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_ChannelGroup_GetAudibility(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_ChannelGroup_SetPitch(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, float float1) {
		return javafmodJNI.FMOD_ChannelGroup_SetPitch(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), float1);
	}

	public static int FMOD_ChannelGroup_GetPitch(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_ChannelGroup_GetPitch(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_ChannelGroup_SetMute(long long1, boolean boolean1) {
		return javafmodJNI.FMOD_ChannelGroup_SetMute(long1, boolean1);
	}

	public static int FMOD_ChannelGroup_GetMute(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_ChannelGroup_GetMute(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_ChannelGroup_SetReverbProperties(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, int int1, float float1) {
		return javafmodJNI.FMOD_ChannelGroup_SetReverbProperties(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), int1, float1);
	}

	public static int FMOD_ChannelGroup_GetReverbProperties(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, int int1, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_ChannelGroup_GetReverbProperties(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), int1, SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_ChannelGroup_SetLowPassGain(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, float float1) {
		return javafmodJNI.FMOD_ChannelGroup_SetLowPassGain(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), float1);
	}

	public static int FMOD_ChannelGroup_GetLowPassGain(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_ChannelGroup_GetLowPassGain(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_ChannelGroup_SetMode(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_MODE sWIGTYPE_p_FMOD_MODE) {
		return javafmodJNI.FMOD_ChannelGroup_SetMode(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_MODE.getCPtr(sWIGTYPE_p_FMOD_MODE));
	}

	public static int FMOD_ChannelGroup_GetMode(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_MODE sWIGTYPE_p_FMOD_MODE) {
		return javafmodJNI.FMOD_ChannelGroup_GetMode(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_MODE.getCPtr(sWIGTYPE_p_FMOD_MODE));
	}

	public static int FMOD_ChannelGroup_SetCallback(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_CHANNELCONTROL_CALLBACK sWIGTYPE_p_FMOD_CHANNELCONTROL_CALLBACK) {
		return javafmodJNI.FMOD_ChannelGroup_SetCallback(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_CHANNELCONTROL_CALLBACK.getCPtr(sWIGTYPE_p_FMOD_CHANNELCONTROL_CALLBACK));
	}

	public static int FMOD_ChannelGroup_IsPlaying(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_ChannelGroup_IsPlaying(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_ChannelGroup_SetPan(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, float float1) {
		return javafmodJNI.FMOD_ChannelGroup_SetPan(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), float1);
	}

	public static int FMOD_ChannelGroup_SetMixLevelsOutput(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		return javafmodJNI.FMOD_ChannelGroup_SetMixLevelsOutput(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), float1, float2, float3, float4, float5, float6, float7, float8);
	}

	public static int FMOD_ChannelGroup_SetMixLevelsInput(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_float sWIGTYPE_p_float, int int1) {
		return javafmodJNI.FMOD_ChannelGroup_SetMixLevelsInput(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), int1);
	}

	public static int FMOD_ChannelGroup_SetMixMatrix(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_float sWIGTYPE_p_float, int int1, int int2, int int3) {
		return javafmodJNI.FMOD_ChannelGroup_SetMixMatrix(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), int1, int2, int3);
	}

	public static int FMOD_ChannelGroup_GetMixMatrix(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_int sWIGTYPE_p_int, SWIGTYPE_p_int sWIGTYPE_p_int2, int int1) {
		return javafmodJNI.FMOD_ChannelGroup_GetMixMatrix(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int2), int1);
	}

	public static int FMOD_ChannelGroup_GetDSPClock(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_unsigned_long_long sWIGTYPE_p_unsigned_long_long, SWIGTYPE_p_unsigned_long_long sWIGTYPE_p_unsigned_long_long2) {
		return javafmodJNI.FMOD_ChannelGroup_GetDSPClock(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_unsigned_long_long.getCPtr(sWIGTYPE_p_unsigned_long_long), SWIGTYPE_p_unsigned_long_long.getCPtr(sWIGTYPE_p_unsigned_long_long2));
	}

	public static int FMOD_ChannelGroup_SetDelay(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, BigInteger bigInteger, BigInteger bigInteger2, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_ChannelGroup_SetDelay(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), bigInteger, bigInteger2, SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_ChannelGroup_GetDelay(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_unsigned_long_long sWIGTYPE_p_unsigned_long_long, SWIGTYPE_p_unsigned_long_long sWIGTYPE_p_unsigned_long_long2, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_ChannelGroup_GetDelay(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_unsigned_long_long.getCPtr(sWIGTYPE_p_unsigned_long_long), SWIGTYPE_p_unsigned_long_long.getCPtr(sWIGTYPE_p_unsigned_long_long2), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_ChannelGroup_AddFadePoint(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, BigInteger bigInteger, float float1) {
		return javafmodJNI.FMOD_ChannelGroup_AddFadePoint(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), bigInteger, float1);
	}

	public static int FMOD_ChannelGroup_RemoveFadePoints(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, BigInteger bigInteger, BigInteger bigInteger2) {
		return javafmodJNI.FMOD_ChannelGroup_RemoveFadePoints(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), bigInteger, bigInteger2);
	}

	public static int FMOD_ChannelGroup_GetFadePoints(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int, SWIGTYPE_p_unsigned_long_long sWIGTYPE_p_unsigned_long_long, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_ChannelGroup_GetFadePoints(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int), SWIGTYPE_p_unsigned_long_long.getCPtr(sWIGTYPE_p_unsigned_long_long), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_ChannelGroup_GetDSP(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, int int1, SWIGTYPE_p_p_FMOD_DSP sWIGTYPE_p_p_FMOD_DSP) {
		return javafmodJNI.FMOD_ChannelGroup_GetDSP(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), int1, SWIGTYPE_p_p_FMOD_DSP.getCPtr(sWIGTYPE_p_p_FMOD_DSP));
	}

	public static int FMOD_ChannelGroup_AddDSP(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, int int1, SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP) {
		return javafmodJNI.FMOD_ChannelGroup_AddDSP(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), int1, SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP));
	}

	public static int FMOD_ChannelGroup_RemoveDSP(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP) {
		return javafmodJNI.FMOD_ChannelGroup_RemoveDSP(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP));
	}

	public static int FMOD_ChannelGroup_GetNumDSPs(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_ChannelGroup_GetNumDSPs(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_ChannelGroup_SetDSPIndex(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, int int1) {
		return javafmodJNI.FMOD_ChannelGroup_SetDSPIndex(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), int1);
	}

	public static int FMOD_ChannelGroup_GetDSPIndex(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_ChannelGroup_GetDSPIndex(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_ChannelGroup_OverridePanDSP(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP) {
		return javafmodJNI.FMOD_ChannelGroup_OverridePanDSP(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP));
	}

	public static int FMOD_ChannelGroup_Set3DAttributes(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR2, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR3) {
		return javafmodJNI.FMOD_ChannelGroup_Set3DAttributes(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR2), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR3));
	}

	public static int FMOD_ChannelGroup_Get3DAttributes(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR2, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR3) {
		return javafmodJNI.FMOD_ChannelGroup_Get3DAttributes(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR2), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR3));
	}

	public static int FMOD_ChannelGroup_Set3DMinMaxDistance(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, float float1, float float2) {
		return javafmodJNI.FMOD_ChannelGroup_Set3DMinMaxDistance(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), float1, float2);
	}

	public static int FMOD_ChannelGroup_Get3DMinMaxDistance(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_float sWIGTYPE_p_float2) {
		return javafmodJNI.FMOD_ChannelGroup_Get3DMinMaxDistance(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float2));
	}

	public static int FMOD_ChannelGroup_Set3DConeSettings(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, float float1, float float2, float float3) {
		return javafmodJNI.FMOD_ChannelGroup_Set3DConeSettings(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), float1, float2, float3);
	}

	public static int FMOD_ChannelGroup_Get3DConeSettings(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_float sWIGTYPE_p_float2, SWIGTYPE_p_float sWIGTYPE_p_float3) {
		return javafmodJNI.FMOD_ChannelGroup_Get3DConeSettings(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float2), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float3));
	}

	public static int FMOD_ChannelGroup_Set3DConeOrientation(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR) {
		return javafmodJNI.FMOD_ChannelGroup_Set3DConeOrientation(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR));
	}

	public static int FMOD_ChannelGroup_Get3DConeOrientation(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR) {
		return javafmodJNI.FMOD_ChannelGroup_Get3DConeOrientation(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR));
	}

	public static int FMOD_ChannelGroup_Set3DCustomRolloff(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR, int int1) {
		return javafmodJNI.FMOD_ChannelGroup_Set3DCustomRolloff(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR), int1);
	}

	public static int FMOD_ChannelGroup_Get3DCustomRolloff(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_p_FMOD_VECTOR sWIGTYPE_p_p_FMOD_VECTOR, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_ChannelGroup_Get3DCustomRolloff(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_p_FMOD_VECTOR), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_ChannelGroup_Set3DOcclusion(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, float float1, float float2) {
		return javafmodJNI.FMOD_ChannelGroup_Set3DOcclusion(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), float1, float2);
	}

	public static int FMOD_ChannelGroup_Get3DOcclusion(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_float sWIGTYPE_p_float2) {
		return javafmodJNI.FMOD_ChannelGroup_Get3DOcclusion(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float2));
	}

	public static int FMOD_ChannelGroup_Set3DSpread(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, float float1) {
		return javafmodJNI.FMOD_ChannelGroup_Set3DSpread(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), float1);
	}

	public static int FMOD_ChannelGroup_Get3DSpread(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_ChannelGroup_Get3DSpread(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_ChannelGroup_Set3DLevel(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, float float1) {
		return javafmodJNI.FMOD_ChannelGroup_Set3DLevel(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), float1);
	}

	public static int FMOD_ChannelGroup_Get3DLevel(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_ChannelGroup_Get3DLevel(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_ChannelGroup_Set3DDopplerLevel(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, float float1) {
		return javafmodJNI.FMOD_ChannelGroup_Set3DDopplerLevel(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), float1);
	}

	public static int FMOD_ChannelGroup_Get3DDopplerLevel(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_ChannelGroup_Get3DDopplerLevel(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_ChannelGroup_Set3DDistanceFilter(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL, float float1, float float2) {
		return javafmodJNI.FMOD_ChannelGroup_Set3DDistanceFilter(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL), float1, float2);
	}

	public static int FMOD_ChannelGroup_Get3DDistanceFilter(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_float sWIGTYPE_p_float2) {
		return javafmodJNI.FMOD_ChannelGroup_Get3DDistanceFilter(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float2));
	}

	public static int FMOD_ChannelGroup_SetUserData(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_void sWIGTYPE_p_void) {
		return javafmodJNI.FMOD_ChannelGroup_SetUserData(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void));
	}

	public static int FMOD_ChannelGroup_GetUserData(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_p_void sWIGTYPE_p_p_void) {
		return javafmodJNI.FMOD_ChannelGroup_GetUserData(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_p_void.getCPtr(sWIGTYPE_p_p_void));
	}

	public static int FMOD_ChannelGroup_Release(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP) {
		return javafmodJNI.FMOD_ChannelGroup_Release(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP));
	}

	public static int FMOD_ChannelGroup_AddGroup(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP2, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL, SWIGTYPE_p_p_FMOD_DSPCONNECTION sWIGTYPE_p_p_FMOD_DSPCONNECTION) {
		return javafmodJNI.FMOD_ChannelGroup_AddGroup(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP2), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL), SWIGTYPE_p_p_FMOD_DSPCONNECTION.getCPtr(sWIGTYPE_p_p_FMOD_DSPCONNECTION));
	}

	public static int FMOD_ChannelGroup_GetNumGroups(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_ChannelGroup_GetNumGroups(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_ChannelGroup_GetGroup(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, int int1, SWIGTYPE_p_p_FMOD_CHANNELGROUP sWIGTYPE_p_p_FMOD_CHANNELGROUP) {
		return javafmodJNI.FMOD_ChannelGroup_GetGroup(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), int1, SWIGTYPE_p_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_p_FMOD_CHANNELGROUP));
	}

	public static int FMOD_ChannelGroup_GetParentGroup(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_p_FMOD_CHANNELGROUP sWIGTYPE_p_p_FMOD_CHANNELGROUP) {
		return javafmodJNI.FMOD_ChannelGroup_GetParentGroup(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_p_FMOD_CHANNELGROUP));
	}

	public static int FMOD_ChannelGroup_GetName(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, String string, int int1) {
		return javafmodJNI.FMOD_ChannelGroup_GetName(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), string, int1);
	}

	public static int FMOD_ChannelGroup_GetNumChannels(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_ChannelGroup_GetNumChannels(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_ChannelGroup_GetChannel(SWIGTYPE_p_FMOD_CHANNELGROUP sWIGTYPE_p_FMOD_CHANNELGROUP, int int1, SWIGTYPE_p_p_FMOD_CHANNEL sWIGTYPE_p_p_FMOD_CHANNEL) {
		return javafmodJNI.FMOD_ChannelGroup_GetChannel(SWIGTYPE_p_FMOD_CHANNELGROUP.getCPtr(sWIGTYPE_p_FMOD_CHANNELGROUP), int1, SWIGTYPE_p_p_FMOD_CHANNEL.getCPtr(sWIGTYPE_p_p_FMOD_CHANNEL));
	}

	public static int FMOD_SoundGroup_Release(SWIGTYPE_p_FMOD_SOUNDGROUP sWIGTYPE_p_FMOD_SOUNDGROUP) {
		return javafmodJNI.FMOD_SoundGroup_Release(SWIGTYPE_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP));
	}

	public static int FMOD_SoundGroup_GetSystemObject(SWIGTYPE_p_FMOD_SOUNDGROUP sWIGTYPE_p_FMOD_SOUNDGROUP, SWIGTYPE_p_p_FMOD_SYSTEM sWIGTYPE_p_p_FMOD_SYSTEM) {
		return javafmodJNI.FMOD_SoundGroup_GetSystemObject(SWIGTYPE_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP), SWIGTYPE_p_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_p_FMOD_SYSTEM));
	}

	public static int FMOD_SoundGroup_SetMaxAudible(SWIGTYPE_p_FMOD_SOUNDGROUP sWIGTYPE_p_FMOD_SOUNDGROUP, int int1) {
		return javafmodJNI.FMOD_SoundGroup_SetMaxAudible(SWIGTYPE_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP), int1);
	}

	public static int FMOD_SoundGroup_GetMaxAudible(SWIGTYPE_p_FMOD_SOUNDGROUP sWIGTYPE_p_FMOD_SOUNDGROUP, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_SoundGroup_GetMaxAudible(SWIGTYPE_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_SoundGroup_SetMaxAudibleBehavior(SWIGTYPE_p_FMOD_SOUNDGROUP sWIGTYPE_p_FMOD_SOUNDGROUP, SWIGTYPE_p_FMOD_SOUNDGROUP_BEHAVIOR sWIGTYPE_p_FMOD_SOUNDGROUP_BEHAVIOR) {
		return javafmodJNI.FMOD_SoundGroup_SetMaxAudibleBehavior(SWIGTYPE_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP), SWIGTYPE_p_FMOD_SOUNDGROUP_BEHAVIOR.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP_BEHAVIOR));
	}

	public static int FMOD_SoundGroup_GetMaxAudibleBehavior(SWIGTYPE_p_FMOD_SOUNDGROUP sWIGTYPE_p_FMOD_SOUNDGROUP, SWIGTYPE_p_FMOD_SOUNDGROUP_BEHAVIOR sWIGTYPE_p_FMOD_SOUNDGROUP_BEHAVIOR) {
		return javafmodJNI.FMOD_SoundGroup_GetMaxAudibleBehavior(SWIGTYPE_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP), SWIGTYPE_p_FMOD_SOUNDGROUP_BEHAVIOR.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP_BEHAVIOR));
	}

	public static int FMOD_SoundGroup_SetMuteFadeSpeed(SWIGTYPE_p_FMOD_SOUNDGROUP sWIGTYPE_p_FMOD_SOUNDGROUP, float float1) {
		return javafmodJNI.FMOD_SoundGroup_SetMuteFadeSpeed(SWIGTYPE_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP), float1);
	}

	public static int FMOD_SoundGroup_GetMuteFadeSpeed(SWIGTYPE_p_FMOD_SOUNDGROUP sWIGTYPE_p_FMOD_SOUNDGROUP, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_SoundGroup_GetMuteFadeSpeed(SWIGTYPE_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_SoundGroup_SetVolume(SWIGTYPE_p_FMOD_SOUNDGROUP sWIGTYPE_p_FMOD_SOUNDGROUP, float float1) {
		return javafmodJNI.FMOD_SoundGroup_SetVolume(SWIGTYPE_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP), float1);
	}

	public static int FMOD_SoundGroup_GetVolume(SWIGTYPE_p_FMOD_SOUNDGROUP sWIGTYPE_p_FMOD_SOUNDGROUP, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_SoundGroup_GetVolume(SWIGTYPE_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_SoundGroup_Stop(SWIGTYPE_p_FMOD_SOUNDGROUP sWIGTYPE_p_FMOD_SOUNDGROUP) {
		return javafmodJNI.FMOD_SoundGroup_Stop(SWIGTYPE_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP));
	}

	public static int FMOD_SoundGroup_GetName(SWIGTYPE_p_FMOD_SOUNDGROUP sWIGTYPE_p_FMOD_SOUNDGROUP, String string, int int1) {
		return javafmodJNI.FMOD_SoundGroup_GetName(SWIGTYPE_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP), string, int1);
	}

	public static int FMOD_SoundGroup_GetNumSounds(SWIGTYPE_p_FMOD_SOUNDGROUP sWIGTYPE_p_FMOD_SOUNDGROUP, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_SoundGroup_GetNumSounds(SWIGTYPE_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_SoundGroup_GetSound(SWIGTYPE_p_FMOD_SOUNDGROUP sWIGTYPE_p_FMOD_SOUNDGROUP, int int1, SWIGTYPE_p_p_FMOD_SOUND sWIGTYPE_p_p_FMOD_SOUND) {
		return javafmodJNI.FMOD_SoundGroup_GetSound(SWIGTYPE_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP), int1, SWIGTYPE_p_p_FMOD_SOUND.getCPtr(sWIGTYPE_p_p_FMOD_SOUND));
	}

	public static int FMOD_SoundGroup_GetNumPlaying(SWIGTYPE_p_FMOD_SOUNDGROUP sWIGTYPE_p_FMOD_SOUNDGROUP, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_SoundGroup_GetNumPlaying(SWIGTYPE_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_SoundGroup_SetUserData(SWIGTYPE_p_FMOD_SOUNDGROUP sWIGTYPE_p_FMOD_SOUNDGROUP, SWIGTYPE_p_void sWIGTYPE_p_void) {
		return javafmodJNI.FMOD_SoundGroup_SetUserData(SWIGTYPE_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void));
	}

	public static int FMOD_SoundGroup_GetUserData(SWIGTYPE_p_FMOD_SOUNDGROUP sWIGTYPE_p_FMOD_SOUNDGROUP, SWIGTYPE_p_p_void sWIGTYPE_p_p_void) {
		return javafmodJNI.FMOD_SoundGroup_GetUserData(SWIGTYPE_p_FMOD_SOUNDGROUP.getCPtr(sWIGTYPE_p_FMOD_SOUNDGROUP), SWIGTYPE_p_p_void.getCPtr(sWIGTYPE_p_p_void));
	}

	public static int FMOD_DSP_Release(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP) {
		return javafmodJNI.FMOD_DSP_Release(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP));
	}

	public static int FMOD_DSP_GetSystemObject(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_p_FMOD_SYSTEM sWIGTYPE_p_p_FMOD_SYSTEM) {
		return javafmodJNI.FMOD_DSP_GetSystemObject(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_p_FMOD_SYSTEM.getCPtr(sWIGTYPE_p_p_FMOD_SYSTEM));
	}

	public static int FMOD_DSP_AddInput(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP2, SWIGTYPE_p_p_FMOD_DSPCONNECTION sWIGTYPE_p_p_FMOD_DSPCONNECTION, SWIGTYPE_p_FMOD_DSPCONNECTION_TYPE sWIGTYPE_p_FMOD_DSPCONNECTION_TYPE) {
		return javafmodJNI.FMOD_DSP_AddInput(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP2), SWIGTYPE_p_p_FMOD_DSPCONNECTION.getCPtr(sWIGTYPE_p_p_FMOD_DSPCONNECTION), SWIGTYPE_p_FMOD_DSPCONNECTION_TYPE.getCPtr(sWIGTYPE_p_FMOD_DSPCONNECTION_TYPE));
	}

	public static int FMOD_DSP_DisconnectFrom(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP2, SWIGTYPE_p_FMOD_DSPCONNECTION sWIGTYPE_p_FMOD_DSPCONNECTION) {
		return javafmodJNI.FMOD_DSP_DisconnectFrom(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP2), SWIGTYPE_p_FMOD_DSPCONNECTION.getCPtr(sWIGTYPE_p_FMOD_DSPCONNECTION));
	}

	public static int FMOD_DSP_DisconnectAll(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL2) {
		return javafmodJNI.FMOD_DSP_DisconnectAll(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL2));
	}

	public static int FMOD_DSP_GetNumInputs(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_DSP_GetNumInputs(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_DSP_GetNumOutputs(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_DSP_GetNumOutputs(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_DSP_GetInput(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, int int1, SWIGTYPE_p_p_FMOD_DSP sWIGTYPE_p_p_FMOD_DSP, SWIGTYPE_p_p_FMOD_DSPCONNECTION sWIGTYPE_p_p_FMOD_DSPCONNECTION) {
		return javafmodJNI.FMOD_DSP_GetInput(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), int1, SWIGTYPE_p_p_FMOD_DSP.getCPtr(sWIGTYPE_p_p_FMOD_DSP), SWIGTYPE_p_p_FMOD_DSPCONNECTION.getCPtr(sWIGTYPE_p_p_FMOD_DSPCONNECTION));
	}

	public static int FMOD_DSP_GetOutput(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, int int1, SWIGTYPE_p_p_FMOD_DSP sWIGTYPE_p_p_FMOD_DSP, SWIGTYPE_p_p_FMOD_DSPCONNECTION sWIGTYPE_p_p_FMOD_DSPCONNECTION) {
		return javafmodJNI.FMOD_DSP_GetOutput(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), int1, SWIGTYPE_p_p_FMOD_DSP.getCPtr(sWIGTYPE_p_p_FMOD_DSP), SWIGTYPE_p_p_FMOD_DSPCONNECTION.getCPtr(sWIGTYPE_p_p_FMOD_DSPCONNECTION));
	}

	public static int FMOD_DSP_SetActive(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_DSP_SetActive(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_DSP_GetActive(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_DSP_GetActive(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_DSP_SetBypass(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_DSP_SetBypass(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_DSP_GetBypass(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_DSP_GetBypass(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_DSP_SetWetDryMix(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, float float1, float float2, float float3) {
		return javafmodJNI.FMOD_DSP_SetWetDryMix(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), float1, float2, float3);
	}

	public static int FMOD_DSP_GetWetDryMix(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_float sWIGTYPE_p_float2, SWIGTYPE_p_float sWIGTYPE_p_float3) {
		return javafmodJNI.FMOD_DSP_GetWetDryMix(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float2), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float3));
	}

	public static int FMOD_DSP_SetChannelFormat(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_FMOD_CHANNELMASK sWIGTYPE_p_FMOD_CHANNELMASK, int int1, SWIGTYPE_p_FMOD_SPEAKERMODE sWIGTYPE_p_FMOD_SPEAKERMODE) {
		return javafmodJNI.FMOD_DSP_SetChannelFormat(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_FMOD_CHANNELMASK.getCPtr(sWIGTYPE_p_FMOD_CHANNELMASK), int1, SWIGTYPE_p_FMOD_SPEAKERMODE.getCPtr(sWIGTYPE_p_FMOD_SPEAKERMODE));
	}

	public static int FMOD_DSP_GetChannelFormat(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_FMOD_CHANNELMASK sWIGTYPE_p_FMOD_CHANNELMASK, SWIGTYPE_p_int sWIGTYPE_p_int, SWIGTYPE_p_FMOD_SPEAKERMODE sWIGTYPE_p_FMOD_SPEAKERMODE) {
		return javafmodJNI.FMOD_DSP_GetChannelFormat(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_FMOD_CHANNELMASK.getCPtr(sWIGTYPE_p_FMOD_CHANNELMASK), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int), SWIGTYPE_p_FMOD_SPEAKERMODE.getCPtr(sWIGTYPE_p_FMOD_SPEAKERMODE));
	}

	public static int FMOD_DSP_GetOutputChannelFormat(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_FMOD_CHANNELMASK sWIGTYPE_p_FMOD_CHANNELMASK, int int1, SWIGTYPE_p_FMOD_SPEAKERMODE sWIGTYPE_p_FMOD_SPEAKERMODE, SWIGTYPE_p_FMOD_CHANNELMASK sWIGTYPE_p_FMOD_CHANNELMASK2, SWIGTYPE_p_int sWIGTYPE_p_int, SWIGTYPE_p_FMOD_SPEAKERMODE sWIGTYPE_p_FMOD_SPEAKERMODE2) {
		return javafmodJNI.FMOD_DSP_GetOutputChannelFormat(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_FMOD_CHANNELMASK.getCPtr(sWIGTYPE_p_FMOD_CHANNELMASK), int1, SWIGTYPE_p_FMOD_SPEAKERMODE.getCPtr(sWIGTYPE_p_FMOD_SPEAKERMODE), SWIGTYPE_p_FMOD_CHANNELMASK.getCPtr(sWIGTYPE_p_FMOD_CHANNELMASK2), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int), SWIGTYPE_p_FMOD_SPEAKERMODE.getCPtr(sWIGTYPE_p_FMOD_SPEAKERMODE2));
	}

	public static int FMOD_DSP_Reset(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP) {
		return javafmodJNI.FMOD_DSP_Reset(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP));
	}

	public static int FMOD_DSP_SetParameterFloat(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, int int1, float float1) {
		return javafmodJNI.FMOD_DSP_SetParameterFloat(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), int1, float1);
	}

	public static int FMOD_DSP_SetParameterInt(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, int int1, int int2) {
		return javafmodJNI.FMOD_DSP_SetParameterInt(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), int1, int2);
	}

	public static int FMOD_DSP_SetParameterBool(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, int int1, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_DSP_SetParameterBool(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), int1, SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_DSP_SetParameterData(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, int int1, SWIGTYPE_p_void sWIGTYPE_p_void, long long1) {
		return javafmodJNI.FMOD_DSP_SetParameterData(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), int1, SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void), long1);
	}

	public static int FMOD_DSP_GetParameterFloat(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, int int1, SWIGTYPE_p_float sWIGTYPE_p_float, String string, int int2) {
		return javafmodJNI.FMOD_DSP_GetParameterFloat(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), int1, SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), string, int2);
	}

	public static int FMOD_DSP_GetParameterInt(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, int int1, SWIGTYPE_p_int sWIGTYPE_p_int, String string, int int2) {
		return javafmodJNI.FMOD_DSP_GetParameterInt(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), int1, SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int), string, int2);
	}

	public static int FMOD_DSP_GetParameterBool(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, int int1, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL, String string, int int2) {
		return javafmodJNI.FMOD_DSP_GetParameterBool(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), int1, SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL), string, int2);
	}

	public static int FMOD_DSP_GetParameterData(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, int int1, SWIGTYPE_p_p_void sWIGTYPE_p_p_void, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int, String string, int int2) {
		return javafmodJNI.FMOD_DSP_GetParameterData(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), int1, SWIGTYPE_p_p_void.getCPtr(sWIGTYPE_p_p_void), SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int), string, int2);
	}

	public static int FMOD_DSP_GetNumParameters(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_DSP_GetNumParameters(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_DSP_GetParameterInfo(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, int int1, SWIGTYPE_p_p_FMOD_DSP_PARAMETER_DESC sWIGTYPE_p_p_FMOD_DSP_PARAMETER_DESC) {
		return javafmodJNI.FMOD_DSP_GetParameterInfo(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), int1, SWIGTYPE_p_p_FMOD_DSP_PARAMETER_DESC.getCPtr(sWIGTYPE_p_p_FMOD_DSP_PARAMETER_DESC));
	}

	public static int FMOD_DSP_GetDataParameterIndex(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, int int1, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_DSP_GetDataParameterIndex(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), int1, SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_DSP_ShowConfigDialog(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_void sWIGTYPE_p_void, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_DSP_ShowConfigDialog(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_DSP_GetInfo(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, String string, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int, SWIGTYPE_p_int sWIGTYPE_p_int, SWIGTYPE_p_int sWIGTYPE_p_int2, SWIGTYPE_p_int sWIGTYPE_p_int3) {
		return javafmodJNI.FMOD_DSP_GetInfo(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), string, SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int2), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int3));
	}

	public static int FMOD_DSP_GetType(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_FMOD_DSP_TYPE sWIGTYPE_p_FMOD_DSP_TYPE) {
		return javafmodJNI.FMOD_DSP_GetType(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_FMOD_DSP_TYPE.getCPtr(sWIGTYPE_p_FMOD_DSP_TYPE));
	}

	public static int FMOD_DSP_GetIdle(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_DSP_GetIdle(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_DSP_SetUserData(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_void sWIGTYPE_p_void) {
		return javafmodJNI.FMOD_DSP_SetUserData(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void));
	}

	public static int FMOD_DSP_GetUserData(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_p_void sWIGTYPE_p_p_void) {
		return javafmodJNI.FMOD_DSP_GetUserData(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_p_void.getCPtr(sWIGTYPE_p_p_void));
	}

	public static int FMOD_DSP_SetMeteringEnabled(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL2) {
		return javafmodJNI.FMOD_DSP_SetMeteringEnabled(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL2));
	}

	public static int FMOD_DSP_GetMeteringEnabled(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL2) {
		return javafmodJNI.FMOD_DSP_GetMeteringEnabled(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL2));
	}

	public static int FMOD_DSP_GetMeteringInfo(SWIGTYPE_p_FMOD_DSP sWIGTYPE_p_FMOD_DSP, SWIGTYPE_p_FMOD_DSP_METERING_INFO sWIGTYPE_p_FMOD_DSP_METERING_INFO, SWIGTYPE_p_FMOD_DSP_METERING_INFO sWIGTYPE_p_FMOD_DSP_METERING_INFO2) {
		return javafmodJNI.FMOD_DSP_GetMeteringInfo(SWIGTYPE_p_FMOD_DSP.getCPtr(sWIGTYPE_p_FMOD_DSP), SWIGTYPE_p_FMOD_DSP_METERING_INFO.getCPtr(sWIGTYPE_p_FMOD_DSP_METERING_INFO), SWIGTYPE_p_FMOD_DSP_METERING_INFO.getCPtr(sWIGTYPE_p_FMOD_DSP_METERING_INFO2));
	}

	public static int FMOD_DSPConnection_GetInput(SWIGTYPE_p_FMOD_DSPCONNECTION sWIGTYPE_p_FMOD_DSPCONNECTION, SWIGTYPE_p_p_FMOD_DSP sWIGTYPE_p_p_FMOD_DSP) {
		return javafmodJNI.FMOD_DSPConnection_GetInput(SWIGTYPE_p_FMOD_DSPCONNECTION.getCPtr(sWIGTYPE_p_FMOD_DSPCONNECTION), SWIGTYPE_p_p_FMOD_DSP.getCPtr(sWIGTYPE_p_p_FMOD_DSP));
	}

	public static int FMOD_DSPConnection_GetOutput(SWIGTYPE_p_FMOD_DSPCONNECTION sWIGTYPE_p_FMOD_DSPCONNECTION, SWIGTYPE_p_p_FMOD_DSP sWIGTYPE_p_p_FMOD_DSP) {
		return javafmodJNI.FMOD_DSPConnection_GetOutput(SWIGTYPE_p_FMOD_DSPCONNECTION.getCPtr(sWIGTYPE_p_FMOD_DSPCONNECTION), SWIGTYPE_p_p_FMOD_DSP.getCPtr(sWIGTYPE_p_p_FMOD_DSP));
	}

	public static int FMOD_DSPConnection_SetMix(SWIGTYPE_p_FMOD_DSPCONNECTION sWIGTYPE_p_FMOD_DSPCONNECTION, float float1) {
		return javafmodJNI.FMOD_DSPConnection_SetMix(SWIGTYPE_p_FMOD_DSPCONNECTION.getCPtr(sWIGTYPE_p_FMOD_DSPCONNECTION), float1);
	}

	public static int FMOD_DSPConnection_GetMix(SWIGTYPE_p_FMOD_DSPCONNECTION sWIGTYPE_p_FMOD_DSPCONNECTION, SWIGTYPE_p_float sWIGTYPE_p_float) {
		return javafmodJNI.FMOD_DSPConnection_GetMix(SWIGTYPE_p_FMOD_DSPCONNECTION.getCPtr(sWIGTYPE_p_FMOD_DSPCONNECTION), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float));
	}

	public static int FMOD_DSPConnection_SetMixMatrix(SWIGTYPE_p_FMOD_DSPCONNECTION sWIGTYPE_p_FMOD_DSPCONNECTION, SWIGTYPE_p_float sWIGTYPE_p_float, int int1, int int2, int int3) {
		return javafmodJNI.FMOD_DSPConnection_SetMixMatrix(SWIGTYPE_p_FMOD_DSPCONNECTION.getCPtr(sWIGTYPE_p_FMOD_DSPCONNECTION), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), int1, int2, int3);
	}

	public static int FMOD_DSPConnection_GetMixMatrix(SWIGTYPE_p_FMOD_DSPCONNECTION sWIGTYPE_p_FMOD_DSPCONNECTION, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_int sWIGTYPE_p_int, SWIGTYPE_p_int sWIGTYPE_p_int2, int int1) {
		return javafmodJNI.FMOD_DSPConnection_GetMixMatrix(SWIGTYPE_p_FMOD_DSPCONNECTION.getCPtr(sWIGTYPE_p_FMOD_DSPCONNECTION), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int2), int1);
	}

	public static int FMOD_DSPConnection_GetType(SWIGTYPE_p_FMOD_DSPCONNECTION sWIGTYPE_p_FMOD_DSPCONNECTION, SWIGTYPE_p_FMOD_DSPCONNECTION_TYPE sWIGTYPE_p_FMOD_DSPCONNECTION_TYPE) {
		return javafmodJNI.FMOD_DSPConnection_GetType(SWIGTYPE_p_FMOD_DSPCONNECTION.getCPtr(sWIGTYPE_p_FMOD_DSPCONNECTION), SWIGTYPE_p_FMOD_DSPCONNECTION_TYPE.getCPtr(sWIGTYPE_p_FMOD_DSPCONNECTION_TYPE));
	}

	public static int FMOD_DSPConnection_SetUserData(SWIGTYPE_p_FMOD_DSPCONNECTION sWIGTYPE_p_FMOD_DSPCONNECTION, SWIGTYPE_p_void sWIGTYPE_p_void) {
		return javafmodJNI.FMOD_DSPConnection_SetUserData(SWIGTYPE_p_FMOD_DSPCONNECTION.getCPtr(sWIGTYPE_p_FMOD_DSPCONNECTION), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void));
	}

	public static int FMOD_DSPConnection_GetUserData(SWIGTYPE_p_FMOD_DSPCONNECTION sWIGTYPE_p_FMOD_DSPCONNECTION, SWIGTYPE_p_p_void sWIGTYPE_p_p_void) {
		return javafmodJNI.FMOD_DSPConnection_GetUserData(SWIGTYPE_p_FMOD_DSPCONNECTION.getCPtr(sWIGTYPE_p_FMOD_DSPCONNECTION), SWIGTYPE_p_p_void.getCPtr(sWIGTYPE_p_p_void));
	}

	public static int FMOD_Geometry_Release(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY) {
		return javafmodJNI.FMOD_Geometry_Release(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY));
	}

	public static int FMOD_Geometry_AddPolygon(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, float float1, float float2, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL, int int1, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_Geometry_AddPolygon(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), float1, float2, SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL), int1, SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_Geometry_GetNumPolygons(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_Geometry_GetNumPolygons(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_Geometry_GetMaxPolygons(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, SWIGTYPE_p_int sWIGTYPE_p_int, SWIGTYPE_p_int sWIGTYPE_p_int2) {
		return javafmodJNI.FMOD_Geometry_GetMaxPolygons(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int2));
	}

	public static int FMOD_Geometry_GetPolygonNumVertices(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, int int1, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_Geometry_GetPolygonNumVertices(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), int1, SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_Geometry_SetPolygonVertex(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, int int1, int int2, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR) {
		return javafmodJNI.FMOD_Geometry_SetPolygonVertex(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), int1, int2, SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR));
	}

	public static int FMOD_Geometry_GetPolygonVertex(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, int int1, int int2, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR) {
		return javafmodJNI.FMOD_Geometry_GetPolygonVertex(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), int1, int2, SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR));
	}

	public static int FMOD_Geometry_SetPolygonAttributes(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, int int1, float float1, float float2, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_Geometry_SetPolygonAttributes(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), int1, float1, float2, SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_Geometry_GetPolygonAttributes(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, int int1, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_float sWIGTYPE_p_float2, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_Geometry_GetPolygonAttributes(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), int1, SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float2), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_Geometry_SetActive(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_Geometry_SetActive(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_Geometry_GetActive(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_Geometry_GetActive(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_Geometry_SetRotation(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR2) {
		return javafmodJNI.FMOD_Geometry_SetRotation(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR2));
	}

	public static int FMOD_Geometry_GetRotation(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR2) {
		return javafmodJNI.FMOD_Geometry_GetRotation(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR2));
	}

	public static int FMOD_Geometry_SetPosition(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR) {
		return javafmodJNI.FMOD_Geometry_SetPosition(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR));
	}

	public static int FMOD_Geometry_GetPosition(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR) {
		return javafmodJNI.FMOD_Geometry_GetPosition(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR));
	}

	public static int FMOD_Geometry_SetScale(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR) {
		return javafmodJNI.FMOD_Geometry_SetScale(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR));
	}

	public static int FMOD_Geometry_GetScale(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR) {
		return javafmodJNI.FMOD_Geometry_GetScale(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR));
	}

	public static int FMOD_Geometry_Save(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, SWIGTYPE_p_void sWIGTYPE_p_void, SWIGTYPE_p_int sWIGTYPE_p_int) {
		return javafmodJNI.FMOD_Geometry_Save(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
	}

	public static int FMOD_Geometry_SetUserData(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, SWIGTYPE_p_void sWIGTYPE_p_void) {
		return javafmodJNI.FMOD_Geometry_SetUserData(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void));
	}

	public static int FMOD_Geometry_GetUserData(SWIGTYPE_p_FMOD_GEOMETRY sWIGTYPE_p_FMOD_GEOMETRY, SWIGTYPE_p_p_void sWIGTYPE_p_p_void) {
		return javafmodJNI.FMOD_Geometry_GetUserData(SWIGTYPE_p_FMOD_GEOMETRY.getCPtr(sWIGTYPE_p_FMOD_GEOMETRY), SWIGTYPE_p_p_void.getCPtr(sWIGTYPE_p_p_void));
	}

	public static int FMOD_Reverb3D_Release(SWIGTYPE_p_FMOD_REVERB3D sWIGTYPE_p_FMOD_REVERB3D) {
		return javafmodJNI.FMOD_Reverb3D_Release(SWIGTYPE_p_FMOD_REVERB3D.getCPtr(sWIGTYPE_p_FMOD_REVERB3D));
	}

	public static int FMOD_Reverb3D_Set3DAttributes(SWIGTYPE_p_FMOD_REVERB3D sWIGTYPE_p_FMOD_REVERB3D, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR, float float1, float float2) {
		return javafmodJNI.FMOD_Reverb3D_Set3DAttributes(SWIGTYPE_p_FMOD_REVERB3D.getCPtr(sWIGTYPE_p_FMOD_REVERB3D), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR), float1, float2);
	}

	public static int FMOD_Reverb3D_Get3DAttributes(SWIGTYPE_p_FMOD_REVERB3D sWIGTYPE_p_FMOD_REVERB3D, SWIGTYPE_p_FMOD_VECTOR sWIGTYPE_p_FMOD_VECTOR, SWIGTYPE_p_float sWIGTYPE_p_float, SWIGTYPE_p_float sWIGTYPE_p_float2) {
		return javafmodJNI.FMOD_Reverb3D_Get3DAttributes(SWIGTYPE_p_FMOD_REVERB3D.getCPtr(sWIGTYPE_p_FMOD_REVERB3D), SWIGTYPE_p_FMOD_VECTOR.getCPtr(sWIGTYPE_p_FMOD_VECTOR), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float), SWIGTYPE_p_float.getCPtr(sWIGTYPE_p_float2));
	}

	public static int FMOD_Reverb3D_SetProperties(SWIGTYPE_p_FMOD_REVERB3D sWIGTYPE_p_FMOD_REVERB3D, SWIGTYPE_p_FMOD_REVERB_PROPERTIES sWIGTYPE_p_FMOD_REVERB_PROPERTIES) {
		return javafmodJNI.FMOD_Reverb3D_SetProperties(SWIGTYPE_p_FMOD_REVERB3D.getCPtr(sWIGTYPE_p_FMOD_REVERB3D), SWIGTYPE_p_FMOD_REVERB_PROPERTIES.getCPtr(sWIGTYPE_p_FMOD_REVERB_PROPERTIES));
	}

	public static int FMOD_Reverb3D_GetProperties(SWIGTYPE_p_FMOD_REVERB3D sWIGTYPE_p_FMOD_REVERB3D, SWIGTYPE_p_FMOD_REVERB_PROPERTIES sWIGTYPE_p_FMOD_REVERB_PROPERTIES) {
		return javafmodJNI.FMOD_Reverb3D_GetProperties(SWIGTYPE_p_FMOD_REVERB3D.getCPtr(sWIGTYPE_p_FMOD_REVERB3D), SWIGTYPE_p_FMOD_REVERB_PROPERTIES.getCPtr(sWIGTYPE_p_FMOD_REVERB_PROPERTIES));
	}

	public static int FMOD_Reverb3D_SetActive(SWIGTYPE_p_FMOD_REVERB3D sWIGTYPE_p_FMOD_REVERB3D, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_Reverb3D_SetActive(SWIGTYPE_p_FMOD_REVERB3D.getCPtr(sWIGTYPE_p_FMOD_REVERB3D), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_Reverb3D_GetActive(SWIGTYPE_p_FMOD_REVERB3D sWIGTYPE_p_FMOD_REVERB3D, SWIGTYPE_p_FMOD_BOOL sWIGTYPE_p_FMOD_BOOL) {
		return javafmodJNI.FMOD_Reverb3D_GetActive(SWIGTYPE_p_FMOD_REVERB3D.getCPtr(sWIGTYPE_p_FMOD_REVERB3D), SWIGTYPE_p_FMOD_BOOL.getCPtr(sWIGTYPE_p_FMOD_BOOL));
	}

	public static int FMOD_Reverb3D_SetUserData(SWIGTYPE_p_FMOD_REVERB3D sWIGTYPE_p_FMOD_REVERB3D, SWIGTYPE_p_void sWIGTYPE_p_void) {
		return javafmodJNI.FMOD_Reverb3D_SetUserData(SWIGTYPE_p_FMOD_REVERB3D.getCPtr(sWIGTYPE_p_FMOD_REVERB3D), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void));
	}

	public static int FMOD_Reverb3D_GetUserData(SWIGTYPE_p_FMOD_REVERB3D sWIGTYPE_p_FMOD_REVERB3D, SWIGTYPE_p_p_void sWIGTYPE_p_p_void) {
		return javafmodJNI.FMOD_Reverb3D_GetUserData(SWIGTYPE_p_FMOD_REVERB3D.getCPtr(sWIGTYPE_p_FMOD_REVERB3D), SWIGTYPE_p_p_void.getCPtr(sWIGTYPE_p_p_void));
	}

	public static void FMOD_System_SetReverbDefault(int int1, int int2) {
		if (reverb[int1] != int2) {
			if (Core.bDebug) {
				DebugLog.log("reverb instance=" + int1 + " preset=" + int2);
			}

			reverb[int1] = int2;
		}

		javafmodJNI.FMOD_System_SetReverbDefault(int1, int2);
	}

	public static int FMOD_Studio_EventInstance3D(long long1, float float1, float float2, float float3) {
		return javafmodJNI.FMOD_Studio_EventInstance3D(long1, float1, float2, float3);
	}

	public static int FMOD_Studio_SetNumListeners(int int1) {
		return javafmodJNI.FMOD_Studio_SetNumListeners(int1);
	}

	public static void FMOD_Studio_Listener3D(int int1, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		javafmodJNI.FMOD_Studio_Listener3D(int1, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12);
	}

	public static int FMOD_Studio_SetParameter(long long1, String string, float float1) {
		return javafmodJNI.FMOD_Studio_SetParameter(long1, string, float1);
	}

	public static float FMOD_Studio_GetParameter(long long1, String string) {
		return javafmodJNI.FMOD_Studio_GetParameter(long1, string);
	}

	public static int FMOD_Studio_GetPlaybackState(long long1) {
		return javafmodJNI.FMOD_Studio_GetPlaybackState(long1);
	}

	public static int FMOD_Studio_SetVolume(long long1, float float1) {
		return javafmodJNI.FMOD_Studio_EventInstance_SetVolume(long1, float1);
	}

	public static int FMOD_Studio_ReleaseEventInstance(long long1) {
		return javafmodJNI.FMOD_Studio_ReleaseEventInstance(long1);
	}

	public static int FMOD_Studio_StopInstance(long long1) {
		return javafmodJNI.FMOD_Studio_StopInstance(long1);
	}
}
