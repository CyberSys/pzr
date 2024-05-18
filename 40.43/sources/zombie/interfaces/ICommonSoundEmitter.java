package zombie.interfaces;


public interface ICommonSoundEmitter {

	void setPos(float float1, float float2, float float3);

	long playSound(String string);

	@Deprecated
	long playSound(String string, boolean boolean1);

	void tick();

	boolean isEmpty();

	void setVolume(long long1, float float1);

	int stopSound(long long1);

	boolean isPlaying(long long1);

	boolean isPlaying(String string);
}
