package zombie.audio;

import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;


public class DummySoundEmitter extends BaseSoundEmitter {

	public void randomStart() {
	}

	public void setPos(float float1, float float2, float float3) {
	}

	public int stopSound(long long1) {
		return 0;
	}

	public int stopSoundByName(String string) {
		return 0;
	}

	public void stopOrTriggerSound(long long1) {
	}

	public void stopOrTriggerSoundByName(String string) {
	}

	public void setVolume(long long1, float float1) {
	}

	public void setPitch(long long1, float float1) {
	}

	public boolean hasSustainPoints(long long1) {
		return false;
	}

	public void setParameterValue(long long1, FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION, float float1) {
	}

	public void setTimelinePosition(long long1, String string) {
	}

	public void triggerCue(long long1) {
	}

	public void set3D(long long1, boolean boolean1) {
	}

	public void setVolumeAll(float float1) {
	}

	public void stopAll() {
	}

	public long playSound(String string) {
		return 0L;
	}

	public long playSound(String string, int int1, int int2, int int3) {
		return 0L;
	}

	public long playSound(String string, IsoGridSquare square) {
		return 0L;
	}

	public long playSoundImpl(String string, IsoGridSquare square) {
		return 0L;
	}

	public long playSound(String string, boolean boolean1) {
		return 0L;
	}

	public long playSoundImpl(String string, boolean boolean1, IsoObject object) {
		return 0L;
	}

	public long playSound(String string, IsoObject object) {
		return 0L;
	}

	public long playSoundImpl(String string, IsoObject object) {
		return 0L;
	}

	public long playClip(GameSoundClip gameSoundClip, IsoObject object) {
		return 0L;
	}

	public long playAmbientSound(String string) {
		return 0L;
	}

	public void tick() {
	}

	public boolean hasSoundsToStart() {
		return false;
	}

	public boolean isEmpty() {
		return true;
	}

	public boolean isPlaying(long long1) {
		return false;
	}

	public boolean isPlaying(String string) {
		return false;
	}

	public long playSoundLooped(String string) {
		return 0L;
	}

	public long playSoundLoopedImpl(String string) {
		return 0L;
	}

	public long playAmbientLoopedImpl(String string) {
		return 0L;
	}
}
