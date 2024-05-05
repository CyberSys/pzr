package zombie.interfaces;


public interface ICommonSoundEmitter {

	void setPos(float float1, float float2, float float3);

	long playSound(String string);

	@Deprecated
	long playSound(String string, boolean boolean1);

	void tick();

	boolean isEmpty();

	void setPitch(long long1, float float1);

	void setVolume(long long1, float float1);

	boolean hasSustainPoints(long long1);

	void triggerCue(long long1);

	int stopSound(long long1);

	void stopOrTriggerSound(long long1);

	void stopOrTriggerSoundByName(String string);

	boolean isPlaying(long long1);

	boolean isPlaying(String string);
}
