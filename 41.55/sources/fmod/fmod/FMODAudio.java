package fmod.fmod;

import zombie.audio.BaseSoundEmitter;


public class FMODAudio implements Audio {
	public BaseSoundEmitter emitter;

	public FMODAudio(BaseSoundEmitter baseSoundEmitter) {
		this.emitter = baseSoundEmitter;
	}

	public boolean isPlaying() {
		return !this.emitter.isEmpty();
	}

	public void setVolume(float float1) {
		this.emitter.setVolumeAll(float1);
	}

	public void start() {
	}

	public void pause() {
	}

	public void stop() {
		this.emitter.stopAll();
	}

	public void setName(String string) {
	}

	public String getName() {
		return null;
	}
}
