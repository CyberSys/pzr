package zombie.characters;

import fmod.fmod.EmitterType;
import fmod.fmod.FMODFootstep;
import fmod.fmod.FMODSoundBank;
import fmod.fmod.FMODSoundEmitter;
import fmod.fmod.FMODVoice;
import zombie.SoundManager;
import zombie.interfaces.ICommonSoundEmitter;
import zombie.iso.IsoObject;
import zombie.network.GameServer;


public class CharacterSoundEmitter extends BaseCharacterSoundEmitter implements ICommonSoundEmitter {
	float currentPriority;
	FMODSoundEmitter vocals = new FMODSoundEmitter();
	FMODSoundEmitter footsteps = new FMODSoundEmitter();
	FMODSoundEmitter extra = new FMODSoundEmitter();

	public CharacterSoundEmitter(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
		this.vocals.emitterType = EmitterType.Voice;
		this.vocals.parent = this.character;
		this.footsteps.emitterType = EmitterType.Footstep;
		this.footsteps.parent = this.character;
		this.extra.emitterType = EmitterType.Extra;
		this.extra.parent = this.character;
	}

	public void register() {
		SoundManager.instance.registerEmitter(this.vocals);
		SoundManager.instance.registerEmitter(this.footsteps);
		SoundManager.instance.registerEmitter(this.extra);
	}

	public void unregister() {
		SoundManager.instance.unregisterEmitter(this.vocals);
		SoundManager.instance.unregisterEmitter(this.footsteps);
		SoundManager.instance.unregisterEmitter(this.extra);
	}

	public long playVocals(String string) {
		if (GameServer.bServer) {
			return 0L;
		} else {
			FMODVoice fMODVoice = FMODSoundBank.instance.getVoice(string);
			float float1 = fMODVoice.priority;
			long long1 = this.vocals.playSoundImpl(fMODVoice.sound, false, (IsoObject)null);
			this.currentPriority = float1;
			return long1;
		}
	}

	public void playFootsteps(String string) {
		if (!GameServer.bServer) {
			FMODFootstep fMODFootstep = FMODSoundBank.instance.getFootstep(string);
			String string2 = fMODFootstep.getSoundToPlay(this.character);
			if (string2.equals(fMODFootstep.wood) && this.character.getCurrentSquare() != null) {
				for (int int1 = 0; int1 < this.character.getCurrentSquare().getSpecialObjects().size(); ++int1) {
					IsoObject object = (IsoObject)this.character.getCurrentSquare().getSpecialObjects().get(int1);
					if (object != null && object.getContainer() != null && object.getSprite() != null && object.getSprite().getName().startsWith("floors_interior_tilesandwood")) {
						string2 = fMODFootstep.woodCreak;
						break;
					}
				}
			}

			this.footsteps.playSoundImpl(string2, false, (IsoObject)null);
		}
	}

	public long playSound(String string) {
		return this.character.invisible ? 0L : this.extra.playSound(string);
	}

	public long playSound(String string, boolean boolean1) {
		return this.extra.playSound(string, boolean1);
	}

	public long playSound(String string, IsoObject object) {
		return GameServer.bServer ? 0L : this.extra.playSound(string, object);
	}

	public long playSoundImpl(String string, IsoObject object) {
		return this.extra.playSoundImpl(string, false, object);
	}

	public void tick() {
		this.vocals.tick();
		this.footsteps.tick();
		this.extra.tick();
	}

	public void setPos(float float1, float float2, float float3) {
		this.set(float1, float2, float3);
	}

	public void set(float float1, float float2, float float3) {
		this.vocals.x = this.footsteps.x = this.extra.x = float1;
		this.vocals.y = this.footsteps.y = this.extra.y = float2;
		this.vocals.z = this.footsteps.z = this.extra.z = float3;
	}

	public boolean isEmpty() {
		return this.isClear();
	}

	public boolean isClear() {
		return this.vocals.isEmpty() && this.footsteps.isEmpty() && this.extra.isEmpty();
	}

	public void setVolume(long long1, float float1) {
		this.extra.setVolume(long1, float1);
	}

	public int stopSound(long long1) {
		this.extra.stopSound(long1);
		return 0;
	}

	public boolean hasSoundsToStart() {
		return this.extra.hasSoundsToStart() || this.footsteps.hasSoundsToStart() || this.vocals.hasSoundsToStart();
	}

	public boolean isPlaying(long long1) {
		return this.extra.isPlaying(long1);
	}

	public boolean isPlaying(String string) {
		return this.extra.isPlaying(string) || this.vocals.isPlaying(string);
	}
}
