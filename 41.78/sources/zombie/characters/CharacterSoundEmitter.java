package zombie.characters;

import fmod.fmod.EmitterType;
import fmod.fmod.FMODManager;
import fmod.fmod.FMODSoundBank;
import fmod.fmod.FMODSoundEmitter;
import fmod.fmod.FMODVoice;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import zombie.SoundManager;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.interfaces.ICommonSoundEmitter;
import zombie.iso.IsoObject;
import zombie.network.GameServer;


public final class CharacterSoundEmitter extends BaseCharacterSoundEmitter implements ICommonSoundEmitter {
	float currentPriority;
	final FMODSoundEmitter vocals = new FMODSoundEmitter();
	final FMODSoundEmitter footsteps = new FMODSoundEmitter();
	final FMODSoundEmitter extra = new FMODSoundEmitter();
	private long footstep1 = 0L;
	private long footstep2 = 0L;

	public CharacterSoundEmitter(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
		this.vocals.emitterType = EmitterType.Voice;
		this.vocals.parent = this.character;
		this.vocals.parameterUpdater = gameCharacter;
		this.footsteps.emitterType = EmitterType.Footstep;
		this.footsteps.parent = this.character;
		this.footsteps.parameterUpdater = gameCharacter;
		this.extra.emitterType = EmitterType.Extra;
		this.extra.parent = this.character;
		this.extra.parameterUpdater = gameCharacter;
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
			if (fMODVoice == null) {
				long long1 = this.vocals.playSoundImpl(string, false, (IsoObject)null);
				return long1;
			} else {
				float float1 = fMODVoice.priority;
				long long2 = this.vocals.playSound(fMODVoice.sound, this.character);
				this.currentPriority = float1;
				return long2;
			}
		}
	}

	CharacterSoundEmitter.footstep getFootstepToPlay() {
		if (FMODManager.instance.getNumListeners() == 1) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && player != this.character && !player.Traits.Deaf.isSet()) {
					if ((int)player.getZ() < (int)this.character.getZ()) {
						return CharacterSoundEmitter.footstep.upstairs;
					}

					break;
				}
			}
		}

		IsoObject object = this.character.getCurrentSquare().getFloor();
		if (object != null && object.getSprite() != null && object.getSprite().getName() != null) {
			String string = object.getSprite().getName();
			if (!string.endsWith("blends_natural_01_5") && !string.endsWith("blends_natural_01_6") && !string.endsWith("blends_natural_01_7") && !string.endsWith("blends_natural_01_0")) {
				if (!string.endsWith("blends_street_01_48") && !string.endsWith("blends_street_01_53") && !string.endsWith("blends_street_01_54") && !string.endsWith("blends_street_01_55")) {
					if (string.startsWith("blends_natural_01")) {
						return CharacterSoundEmitter.footstep.grass;
					} else if (string.startsWith("floors_interior_tilesandwood_01_")) {
						int int2 = Integer.parseInt(string.replaceFirst("floors_interior_tilesandwood_01_", ""));
						return int2 > 40 && int2 < 48 ? CharacterSoundEmitter.footstep.wood : CharacterSoundEmitter.footstep.concrete;
					} else if (string.startsWith("carpentry_02_")) {
						return CharacterSoundEmitter.footstep.wood;
					} else {
						return string.startsWith("floors_interior_carpet_") ? CharacterSoundEmitter.footstep.wood : CharacterSoundEmitter.footstep.concrete;
					}
				} else {
					return CharacterSoundEmitter.footstep.gravel;
				}
			} else {
				return CharacterSoundEmitter.footstep.gravel;
			}
		} else {
			return CharacterSoundEmitter.footstep.concrete;
		}
	}

	public void playFootsteps(String string, float float1) {
		if (!GameServer.bServer) {
			boolean boolean1 = this.footsteps.isPlaying(this.footstep1);
			boolean boolean2 = this.footsteps.isPlaying(this.footstep2);
			long long1;
			if (boolean1 && boolean2) {
				long1 = this.footstep1;
				this.footstep1 = this.footstep2;
				this.footstep2 = long1;
				if (this.footsteps.restart(this.footstep2)) {
					return;
				}

				this.footsteps.stopSoundLocal(this.footstep2);
				this.footstep2 = 0L;
			} else if (boolean2) {
				this.footstep1 = this.footstep2;
				this.footstep2 = 0L;
				boolean1 = true;
				boolean2 = false;
			}

			long1 = this.footsteps.playSoundImpl(string, false, (IsoObject)null);
			if (!boolean1) {
				this.footstep1 = long1;
			} else {
				this.footstep2 = long1;
			}
		}
	}

	public long playSound(String string) {
		if (this.character.isInvisible() && !DebugOptions.instance.Character.Debug.PlaySoundWhenInvisible.getValue()) {
			return 0L;
		} else {
			if (DebugLog.isEnabled(DebugType.Sound)) {
				DebugLog.Sound.debugln("Playing sound: " + string + (this.character.isZombie() ? " for zombie" : " for player"));
			}

			return this.extra.playSound(string);
		}
	}

	public long playSound(String string, boolean boolean1) {
		if (this.character.isInvisible() && !DebugOptions.instance.Character.Debug.PlaySoundWhenInvisible.getValue()) {
			return 0L;
		} else {
			if (DebugLog.isEnabled(DebugType.Sound)) {
				DebugLog.Sound.debugln("Playing sound: " + string + (this.character.isZombie() ? " for zombie" : " for player"));
			}

			return this.extra.playSound(string, boolean1);
		}
	}

	public long playSound(String string, IsoObject object) {
		if (this.character.isInvisible() && !DebugOptions.instance.Character.Debug.PlaySoundWhenInvisible.getValue()) {
			return 0L;
		} else {
			if (DebugLog.isEnabled(DebugType.Sound)) {
				DebugLog.Sound.debugln("Playing sound: " + string + (this.character.isZombie() ? " for zombie" : " for player"));
			}

			return GameServer.bServer ? 0L : this.extra.playSound(string, object);
		}
	}

	public long playSoundImpl(String string, IsoObject object) {
		if (this.character instanceof IsoPlayer && ((IsoPlayer)this.character).bRemote && this.character.isInvisible()) {
			return 0L;
		} else {
			if (DebugLog.isEnabled(DebugType.Sound)) {
				DebugLog.Sound.debugln("Playing sound: " + string + (this.character.isZombie() ? " for zombie" : " for player"));
			}

			return this.extra.playSoundImpl(string, false, object);
		}
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

	public void setPitch(long long1, float float1) {
		this.extra.setPitch(long1, float1);
		this.footsteps.setPitch(long1, float1);
		this.vocals.setPitch(long1, float1);
	}

	public void setVolume(long long1, float float1) {
		this.extra.setVolume(long1, float1);
		this.footsteps.setVolume(long1, float1);
		this.vocals.setVolume(long1, float1);
	}

	public boolean hasSustainPoints(long long1) {
		if (this.extra.isPlaying(long1)) {
			return this.extra.hasSustainPoints(long1);
		} else if (this.footsteps.isPlaying(long1)) {
			return this.footsteps.hasSustainPoints(long1);
		} else {
			return this.vocals.isPlaying(long1) ? this.vocals.hasSustainPoints(long1) : false;
		}
	}

	public void triggerCue(long long1) {
		if (this.extra.isPlaying(long1)) {
			this.extra.triggerCue(long1);
		} else if (this.footsteps.isPlaying(long1)) {
			this.footsteps.triggerCue(long1);
		} else if (this.vocals.isPlaying(long1)) {
			this.vocals.triggerCue(long1);
		}
	}

	public int stopSound(long long1) {
		this.extra.stopSound(long1);
		this.footsteps.stopSound(long1);
		this.vocals.stopSound(long1);
		return 0;
	}

	public void stopSoundLocal(long long1) {
		this.extra.stopSoundLocal(long1);
		this.footsteps.stopSoundLocal(long1);
		this.vocals.stopSoundLocal(long1);
	}

	public void stopOrTriggerSound(long long1) {
		this.extra.stopOrTriggerSound(long1);
		this.footsteps.stopOrTriggerSound(long1);
		this.vocals.stopOrTriggerSound(long1);
	}

	public void stopOrTriggerSoundByName(String string) {
		this.extra.stopOrTriggerSoundByName(string);
		this.footsteps.stopOrTriggerSoundByName(string);
		this.vocals.stopOrTriggerSoundByName(string);
	}

	public void stopAll() {
		this.extra.stopAll();
		this.footsteps.stopAll();
		this.vocals.stopAll();
	}

	public int stopSoundByName(String string) {
		this.extra.stopSoundByName(string);
		this.footsteps.stopSoundByName(string);
		this.vocals.stopSoundByName(string);
		return 0;
	}

	public boolean hasSoundsToStart() {
		return this.extra.hasSoundsToStart() || this.footsteps.hasSoundsToStart() || this.vocals.hasSoundsToStart();
	}

	public boolean isPlaying(long long1) {
		return this.extra.isPlaying(long1) || this.footsteps.isPlaying(long1) || this.vocals.isPlaying(long1);
	}

	public boolean isPlaying(String string) {
		return this.extra.isPlaying(string) || this.footsteps.isPlaying(string) || this.vocals.isPlaying(string);
	}

	public void setParameterValue(long long1, FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION, float float1) {
		this.extra.setParameterValue(long1, fMOD_STUDIO_PARAMETER_DESCRIPTION, float1);
	}

	static enum footstep {

		upstairs,
		grass,
		wood,
		concrete,
		gravel,
		snow;

		private static CharacterSoundEmitter.footstep[] $values() {
			return new CharacterSoundEmitter.footstep[]{upstairs, grass, wood, concrete, gravel, snow};
		}
	}
}
