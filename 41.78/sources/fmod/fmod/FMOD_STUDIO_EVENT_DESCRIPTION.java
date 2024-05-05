package fmod.fmod;

import java.util.ArrayList;


public final class FMOD_STUDIO_EVENT_DESCRIPTION {
	public final long address;
	public final String path;
	public final FMOD_GUID id;
	public final boolean bHasSustainPoints;
	public final long length;
	public final ArrayList parameters = new ArrayList();

	public FMOD_STUDIO_EVENT_DESCRIPTION(long long1, String string, FMOD_GUID fMOD_GUID, boolean boolean1, long long2) {
		this.address = long1;
		this.path = string;
		this.id = fMOD_GUID;
		this.bHasSustainPoints = boolean1;
		this.length = long2;
	}

	public boolean hasParameter(FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION) {
		return this.parameters.contains(fMOD_STUDIO_PARAMETER_DESCRIPTION);
	}
}
