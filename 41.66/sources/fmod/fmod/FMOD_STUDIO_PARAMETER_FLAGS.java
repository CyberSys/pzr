package fmod.fmod;



public enum FMOD_STUDIO_PARAMETER_FLAGS {

	FMOD_STUDIO_PARAMETER_READONLY,
	FMOD_STUDIO_PARAMETER_AUTOMATIC,
	FMOD_STUDIO_PARAMETER_GLOBAL,
	FMOD_STUDIO_PARAMETER_DISCRETE,
	bit;

	private FMOD_STUDIO_PARAMETER_FLAGS(int int1) {
		this.bit = int1;
	}
	private static FMOD_STUDIO_PARAMETER_FLAGS[] $values() {
		return new FMOD_STUDIO_PARAMETER_FLAGS[]{FMOD_STUDIO_PARAMETER_READONLY, FMOD_STUDIO_PARAMETER_AUTOMATIC, FMOD_STUDIO_PARAMETER_GLOBAL, FMOD_STUDIO_PARAMETER_DISCRETE};
	}
}
