package zombie.audio;

import fmod.fmod.FMOD_STUDIO_EVENT_DESCRIPTION;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import zombie.SoundManager;
import zombie.core.Core;


public final class GameSoundClip {
	public static short INIT_FLAG_DISTANCE_MIN = 1;
	public static short INIT_FLAG_DISTANCE_MAX = 2;
	public final GameSound gameSound;
	public String event;
	public FMOD_STUDIO_EVENT_DESCRIPTION eventDescription;
	public FMOD_STUDIO_EVENT_DESCRIPTION eventDescriptionMP;
	public String file;
	public float volume = 1.0F;
	public float pitch = 1.0F;
	public float distanceMin = 10.0F;
	public float distanceMax = 10.0F;
	public float reverbMaxRange = 10.0F;
	public float reverbFactor = 0.0F;
	public int priority = 5;
	public short initFlags = 0;
	public short reloadEpoch;

	public GameSoundClip(GameSound gameSound) {
		this.gameSound = gameSound;
		this.reloadEpoch = gameSound.reloadEpoch;
	}

	public String getEvent() {
		return this.event;
	}

	public String getFile() {
		return this.file;
	}

	public float getVolume() {
		return this.volume;
	}

	public float getPitch() {
		return this.pitch;
	}

	public boolean hasMinDistance() {
		return (this.initFlags & INIT_FLAG_DISTANCE_MIN) != 0;
	}

	public boolean hasMaxDistance() {
		return (this.initFlags & INIT_FLAG_DISTANCE_MAX) != 0;
	}

	public float getMinDistance() {
		return this.distanceMin;
	}

	public float getMaxDistance() {
		return this.distanceMax;
	}

	public float getEffectiveVolume() {
		float float1 = 1.0F;
		switch (this.gameSound.master) {
		case Primary: 
			float1 = SoundManager.instance.getSoundVolume();
			break;
		
		case Ambient: 
			float1 = SoundManager.instance.getAmbientVolume();
			break;
		
		case Music: 
			float1 = SoundManager.instance.getMusicVolume();
			break;
		
		case VehicleEngine: 
			float1 = SoundManager.instance.getVehicleEngineVolume();
		
		}
		float1 *= this.volume;
		float1 *= this.gameSound.getUserVolume();
		return float1;
	}

	public float getEffectiveVolumeInMenu() {
		float float1 = 1.0F;
		switch (this.gameSound.master) {
		case Primary: 
			float1 = (float)Core.getInstance().getOptionSoundVolume() / 10.0F;
			break;
		
		case Ambient: 
			float1 = (float)Core.getInstance().getOptionAmbientVolume() / 10.0F;
			break;
		
		case Music: 
			float1 = (float)Core.getInstance().getOptionMusicVolume() / 10.0F;
			break;
		
		case VehicleEngine: 
			float1 = (float)Core.getInstance().getOptionVehicleEngineVolume() / 10.0F;
		
		}
		float1 *= this.volume;
		float1 *= this.gameSound.getUserVolume();
		return float1;
	}

	public GameSoundClip checkReloaded() {
		if (this.reloadEpoch == this.gameSound.reloadEpoch) {
			return this;
		} else {
			GameSoundClip gameSoundClip = null;
			for (int int1 = 0; int1 < this.gameSound.clips.size(); ++int1) {
				GameSoundClip gameSoundClip2 = (GameSoundClip)this.gameSound.clips.get(int1);
				if (gameSoundClip2 == this) {
					return this;
				}

				if (gameSoundClip2.event != null && gameSoundClip2.event.equals(this.event)) {
					gameSoundClip = gameSoundClip2;
				}

				if (gameSoundClip2.file != null && gameSoundClip2.file.equals(this.file)) {
					gameSoundClip = gameSoundClip2;
				}
			}

			if (gameSoundClip == null) {
				this.reloadEpoch = this.gameSound.reloadEpoch;
				return this;
			} else {
				return gameSoundClip;
			}
		}
	}

	public boolean hasSustainPoints() {
		return this.eventDescription != null && this.eventDescription.bHasSustainPoints;
	}

	public boolean hasParameter(FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION) {
		return this.eventDescription != null && this.eventDescription.hasParameter(fMOD_STUDIO_PARAMETER_DESCRIPTION);
	}
}
