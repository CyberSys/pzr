package zombie.characters;

import zombie.iso.IsoObject;


public class DummyCharacterSoundEmitter extends BaseCharacterSoundEmitter {

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

	public void playFootsteps(String string) {
	}

	public long playSound(String string) {
		return 0L;
	}

	public long playSound(String string, boolean boolean1) {
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

	public void setVolume(long long1, float float1) {
	}

	public int stopSound(long long1) {
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
}
