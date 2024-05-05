package zombie.audio;

import fmod.fmod.FMODManager;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import java.util.ArrayList;
import zombie.SystemDisabler;
import zombie.core.Rand;


public final class GameSound {
	public String name;
	public String category = "General";
	public boolean loop = false;
	public boolean is3D = true;
	public final ArrayList clips = new ArrayList();
	private float userVolume = 1.0F;
	public GameSound.MasterVolume master;
	public int maxInstancesPerEmitter;
	public short reloadEpoch;

	public GameSound() {
		this.master = GameSound.MasterVolume.Primary;
		this.maxInstancesPerEmitter = -1;
	}

	public String getName() {
		return this.name;
	}

	public String getCategory() {
		return this.category;
	}

	public boolean isLooped() {
		return this.loop;
	}

	public void setUserVolume(float float1) {
		this.userVolume = Math.max(0.0F, Math.min(2.0F, float1));
	}

	public float getUserVolume() {
		return !SystemDisabler.getEnableAdvancedSoundOptions() ? 1.0F : this.userVolume;
	}

	public GameSoundClip getRandomClip() {
		return (GameSoundClip)this.clips.get(Rand.Next(this.clips.size()));
	}

	public String getMasterName() {
		return this.master.name();
	}

	public int numClipsUsingParameter(String string) {
		FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION = FMODManager.instance.getParameterDescription(string);
		if (fMOD_STUDIO_PARAMETER_DESCRIPTION == null) {
			return 0;
		} else {
			int int1 = 0;
			for (int int2 = 0; int2 < this.clips.size(); ++int2) {
				GameSoundClip gameSoundClip = (GameSoundClip)this.clips.get(int2);
				if (gameSoundClip.hasParameter(fMOD_STUDIO_PARAMETER_DESCRIPTION)) {
					++int1;
				}
			}

			return int1;
		}
	}

	public void reset() {
		this.name = null;
		this.category = "General";
		this.loop = false;
		this.is3D = true;
		this.clips.clear();
		this.userVolume = 1.0F;
		this.master = GameSound.MasterVolume.Primary;
		this.maxInstancesPerEmitter = -1;
		++this.reloadEpoch;
	}

	public static enum MasterVolume {

		Primary,
		Ambient,
		Music,
		VehicleEngine;

		private static GameSound.MasterVolume[] $values() {
			return new GameSound.MasterVolume[]{Primary, Ambient, Music, VehicleEngine};
		}
	}
}
