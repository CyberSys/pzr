package fmod.fmod;


public final class FMOD_STUDIO_PARAMETER_ID {
	private final long nativePtr;

	public FMOD_STUDIO_PARAMETER_ID(long long1) {
		this.nativePtr = long1;
	}

	public long address() {
		return this.nativePtr;
	}
}
