package zombie.core.raknet;


public class RakVoice {

	public static native void RVInit(int int1);

	public static native void RVInitServer(boolean boolean1, int int1, int int2, int int3, int int4, float float1, float float2, boolean boolean2);

	public static native void RVDeinit();

	public static native int GetComplexity();

	public static native void SetComplexity(int int1);

	public static native void RequestVoiceChannel(long long1);

	public static native void CloseAllChannels();

	public static native int GetBufferSizeBytes();

	public static native boolean GetServerVOIPEnable();

	public static native int GetSampleRate();

	public static native int GetSendFramePeriod();

	public static native int GetBuffering();

	public static native float GetMinDistance();

	public static native float GetMaxDistance();

	public static native boolean GetIs3D();

	public static native void CloseVoiceChannel(long long1);

	public static native boolean ReceiveFrame(long long1, byte[] byteArray);

	public static native void SendFrame(long long1, long long2, byte[] byteArray, long long3);

	public static native void SetLoopbackMode(boolean boolean1);

	public static native void SetVoiceBan(long long1, boolean boolean1);

	public static native void SetPlayerCoordinate(long long1, float float1, float float2, float float3, boolean boolean1);
}
