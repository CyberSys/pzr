package fmod.fmod;


public final class FMOD_GUID {
	private final long address;

	public FMOD_GUID(long long1) {
		this.address = long1;
	}

	public long address() {
		return this.address;
	}
}
