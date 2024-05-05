package zombie.audio;

import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;


public abstract class BaseSoundEmitter {

	public abstract void randomStart();

	public abstract void setPos(float float1, float float2, float float3);

	public abstract int stopSound(long long1);

	public abstract void stopSoundLocal(long long1);

	public abstract int stopSoundByName(String string);

	public abstract void stopOrTriggerSound(long long1);

	public abstract void stopOrTriggerSoundByName(String string);

	public abstract void setVolume(long long1, float float1);

	public abstract void setPitch(long long1, float float1);

	public abstract boolean hasSustainPoints(long long1);

	public abstract void setParameterValue(long long1, FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION, float float1);

	public abstract void setTimelinePosition(long long1, String string);

	public abstract void triggerCue(long long1);

	public abstract void setVolumeAll(float float1);

	public abstract void stopAll();

	public abstract long playSound(String string);

	public abstract long playSound(String string, IsoGameCharacter gameCharacter);

	public abstract long playSound(String string, int int1, int int2, int int3);

	public abstract long playSound(String string, IsoGridSquare square);

	public abstract long playSoundImpl(String string, IsoGridSquare square);

	@Deprecated
	public abstract long playSound(String string, boolean boolean1);

	@Deprecated
	public abstract long playSoundImpl(String string, boolean boolean1, IsoObject object);

	public abstract long playSoundLooped(String string);

	public abstract long playSoundLoopedImpl(String string);

	public abstract long playSound(String string, IsoObject object);

	public abstract long playSoundImpl(String string, IsoObject object);

	public abstract long playClip(GameSoundClip gameSoundClip, IsoObject object);

	public abstract long playAmbientSound(String string);

	public abstract long playAmbientLoopedImpl(String string);

	public abstract void set3D(long long1, boolean boolean1);

	public abstract void tick();

	public abstract boolean hasSoundsToStart();

	public abstract boolean isEmpty();

	public abstract boolean isPlaying(long long1);

	public abstract boolean isPlaying(String string);

	public abstract boolean restart(long long1);
}
