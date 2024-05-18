package zombie.characters;

import zombie.iso.IsoObject;


public abstract class BaseCharacterSoundEmitter {
	protected final IsoGameCharacter character;

	public BaseCharacterSoundEmitter(IsoGameCharacter gameCharacter) {
		this.character = gameCharacter;
	}

	public abstract void register();

	public abstract void unregister();

	public abstract long playVocals(String string);

	public abstract void playFootsteps(String string);

	public abstract long playSound(String string);

	@Deprecated
	public abstract long playSound(String string, boolean boolean1);

	public abstract long playSound(String string, IsoObject object);

	public abstract long playSoundImpl(String string, IsoObject object);

	public abstract void tick();

	public abstract void set(float float1, float float2, float float3);

	public abstract boolean isClear();

	public abstract void setVolume(long long1, float float1);

	public abstract int stopSound(long long1);

	public abstract boolean hasSoundsToStart();

	public abstract boolean isPlaying(long long1);

	public abstract boolean isPlaying(String string);
}
