package zombie;


public interface ISoundSystem {

	void init();

	void update();

	void purge();

	void fadeOutAll(float float1);

	ISoundSystem.ISoundInstance playSound(ISoundSystem.SoundFormat soundFormat, String string, String string2, int int1, boolean boolean1, boolean boolean2, float float1);

	ISoundSystem.ISoundInstance playSound(ISoundSystem.SoundFormat soundFormat, String string, String string2, int int1, boolean boolean1, boolean boolean2, float float1, float float2);

	ISoundSystem.ISoundInstance playSound(ISoundSystem.SoundFormat soundFormat, String string, String string2, int int1, boolean boolean1, boolean boolean2, float float1, float float2, float float3);

	ISoundSystem.ISoundInstance playSound(ISoundSystem.SoundFormat soundFormat, String string, String string2, boolean boolean1, boolean boolean2, float float1);

	ISoundSystem.ISoundInstance playSound(ISoundSystem.SoundFormat soundFormat, String string, String string2, boolean boolean1, boolean boolean2, float float1, float float2);

	ISoundSystem.ISoundInstance playSound(ISoundSystem.SoundFormat soundFormat, String string, String string2, boolean boolean1, boolean boolean2, float float1, float float2, float float3);

	void cacheSound(ISoundSystem.SoundFormat soundFormat, String string, String string2, int int1);

	void cacheSound(ISoundSystem.SoundFormat soundFormat, String string, String string2);

	void clearSoundCache();

	int countInstances(String string);

	void setInstanceLimit(String string, int int1, ISoundSystem.InstanceFailAction instanceFailAction);
	public static enum InstanceFailAction {

		FailToPlay,
		StopOldest,
		StopRandom;
	}

	public interface ISoundInstance {

		boolean isStreamed();

		boolean isLooped();

		boolean isPlaying();

		int countInstances();

		void setLooped(boolean boolean1);

		void pause();

		void stop();

		void play();

		void blendVolume(float float1, float float2, boolean boolean1);

		void setVolume(float float1);

		float getVolume();

		void setPanning(float float1);

		float getPanning();

		void setPitch(float float1);

		float getPitch();

		boolean disposed();
	}
	public static enum SoundFormat {

		Ogg,
		Wav;
	}
}
