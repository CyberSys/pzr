package fmod.fmod;



public enum FMOD_STUDIO_PLAYBACK_STATE {

	FMOD_STUDIO_PLAYBACK_PLAYING,
	FMOD_STUDIO_PLAYBACK_SUSTAINING,
	FMOD_STUDIO_PLAYBACK_STOPPED,
	FMOD_STUDIO_PLAYBACK_STARTING,
	FMOD_STUDIO_PLAYBACK_STOPPING,
	FMOD_STUDIO_PLAYBACK_STATE,
	index;

	private FMOD_STUDIO_PLAYBACK_STATE(int int1) {
		this.index = int1;
	}
}
