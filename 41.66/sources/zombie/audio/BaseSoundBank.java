package zombie.audio;

import fmod.fmod.FMODFootstep;
import fmod.fmod.FMODVoice;


public abstract class BaseSoundBank {
	public static BaseSoundBank instance;

	public abstract void addVoice(String string, String string2, float float1);

	public abstract void addFootstep(String string, String string2, String string3, String string4, String string5);

	public abstract FMODVoice getVoice(String string);

	public abstract FMODFootstep getFootstep(String string);
}
