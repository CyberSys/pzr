package zombie.characters;

import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import java.util.HashMap;
import java.util.Iterator;
import zombie.core.Rand;
import zombie.iso.IsoObject;
import zombie.network.GameClient;


public final class DummyCharacterSoundEmitter extends BaseCharacterSoundEmitter {
	public float x;
	public float y;
	public float z;
	private final HashMap sounds = new HashMap();

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
		long long1 = (long)Rand.Next(Integer.MAX_VALUE);
		this.sounds.put(long1, string);
		if (GameClient.bClient) {
			GameClient.instance.PlaySound(string, false, this.character);
		}

		return long1;
	}

	public long playSound(String string, IsoObject object) {
		return this.playSound(string);
	}

	public long playSoundImpl(String string, IsoObject object) {
		long long1 = Rand.Next(Long.MAX_VALUE);
		this.sounds.put(long1, string);
		return long1;
	}

	public void tick() {
	}

	public void set(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
	}

	public boolean isClear() {
		return this.sounds.isEmpty();
	}

	public void setPitch(long long1, float float1) {
	}

	public void setVolume(long long1, float float1) {
	}

	public int stopSound(long long1) {
		if (GameClient.bClient) {
			GameClient.instance.StopSound(this.character, (String)this.sounds.get(long1), false);
		}

		this.sounds.remove(long1);
		return 0;
	}

	public void stopOrTriggerSound(long long1) {
		if (GameClient.bClient) {
			GameClient.instance.StopSound(this.character, (String)this.sounds.get(long1), true);
		}

		this.sounds.remove(long1);
	}

	public void stopOrTriggerSoundByName(String string) {
		this.sounds.values().remove(string);
	}

	public void stopAll() {
		if (GameClient.bClient) {
			Iterator iterator = this.sounds.values().iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				GameClient.instance.StopSound(this.character, string, false);
			}
		}

		this.sounds.clear();
	}

	public int stopSoundByName(String string) {
		this.sounds.values().remove(string);
		return 0;
	}

	public boolean hasSoundsToStart() {
		return false;
	}

	public boolean isPlaying(long long1) {
		return this.sounds.containsKey(long1);
	}

	public boolean isPlaying(String string) {
		return this.sounds.containsValue(string);
	}

	public void setParameterValue(long long1, FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION, float float1) {
	}

	public boolean hasSustainPoints(long long1) {
		return false;
	}
}
