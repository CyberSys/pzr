package zombie.characters;

import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import zombie.iso.IsoObject;


public final class DummyCharacterSoundEmitter extends BaseCharacterSoundEmitter {

	public DummyCharacterSoundEmitter(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
	}

	public void register() {
	}

	public void unregister() {
	}

	public long playVocals(String string) {
		return 0L;
	}

	public void playFootsteps(String string, float float1) {
	}

	public long playSound(String string) {
		return 0L;
	}

	public long playSound(String string, IsoObject object) {
		return 0L;
	}

	public long playSoundImpl(String string, IsoObject object) {
		return 0L;
	}

	public void tick() {
	}

	public void set(float float1, float float2, float float3) {
	}

	public boolean isClear() {
		return false;
	}

	public void setPitch(long long1, float float1) {
	}

	public void setVolume(long long1, float float1) {
	}

	public int stopSound(long long1) {
		return 0;
	}

	public void stopOrTriggerSound(long long1) {
	}

	public void stopOrTriggerSoundByName(String string) {
	}

	public void stopAll() {
	}

	public int stopSoundByName(String string) {
		return 0;
	}

	public boolean hasSoundsToStart() {
		return false;
	}

	public boolean isPlaying(long long1) {
		return false;
	}

	public boolean isPlaying(String string) {
		return false;
	}

	public void setParameterValue(long long1, FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION, float float1) {
	}
}
