package zombie.characters;

import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import zombie.iso.IsoObject;


public abstract class BaseCharacterSoundEmitter {
	protected final IsoGameCharacter character;

	public BaseCharacterSoundEmitter(IsoGameCharacter gameCharacter) {
		this.character = gameCharacter;
	}

	public abstract void register();

	public abstract void unregister();

	public abstract long playVocals(String string);

	public abstract void playFootsteps(String string, float float1);

	public abstract long playSound(String string);

	public abstract long playSound(String string, IsoObject object);

	public abstract long playSoundImpl(String string, IsoObject object);

	public abstract void tick();

	public abstract void set(float float1, float float2, float float3);

	public abstract boolean isClear();

	public abstract void setPitch(long long1, float float1);

	public abstract void setVolume(long long1, float float1);

	public abstract int stopSound(long long1);

	public abstract void stopSoundLocal(long long1);

	public abstract int stopSoundByName(String string);

	public abstract void stopOrTriggerSound(long long1);

	public abstract void stopOrTriggerSoundByName(String string);

	public abstract void stopAll();

	public abstract boolean hasSoundsToStart();

	public abstract boolean isPlaying(long long1);

	public abstract boolean isPlaying(String string);

	public abstract void setParameterValue(long long1, FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION, float float1);
}
