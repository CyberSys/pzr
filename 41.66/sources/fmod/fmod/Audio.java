package fmod.fmod;


public interface Audio {

	boolean isPlaying();

	void setVolume(float float1);

	void start();

	void pause();

	void stop();

	void setName(String string);

	String getName();
}
