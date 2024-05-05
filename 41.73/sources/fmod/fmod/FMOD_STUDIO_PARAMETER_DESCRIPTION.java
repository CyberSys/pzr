package fmod.fmod;


public final class FMOD_STUDIO_PARAMETER_DESCRIPTION {
	public final String name;
	public final FMOD_STUDIO_PARAMETER_ID id;
	public final int flags;
	public final int globalIndex;

	public FMOD_STUDIO_PARAMETER_DESCRIPTION(String string, FMOD_STUDIO_PARAMETER_ID fMOD_STUDIO_PARAMETER_ID, int int1, int int2) {
		this.name = string;
		this.id = fMOD_STUDIO_PARAMETER_ID;
		this.flags = int1;
		this.globalIndex = int2;
	}

	public boolean isGlobal() {
		return (this.flags & FMOD_STUDIO_PARAMETER_FLAGS.FMOD_STUDIO_PARAMETER_GLOBAL.bit) != 0;
	}
}
