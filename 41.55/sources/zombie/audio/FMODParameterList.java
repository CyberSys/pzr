package zombie.audio;

import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import java.util.ArrayList;


public final class FMODParameterList {
	public final ArrayList parameterList = new ArrayList();
	public final FMODParameter[] parameterArray = new FMODParameter[96];

	public void add(FMODParameter fMODParameter) {
		this.parameterList.add(fMODParameter);
		if (fMODParameter.getParameterDescription() != null) {
			this.parameterArray[fMODParameter.getParameterDescription().globalIndex] = fMODParameter;
		}
	}

	public FMODParameter get(FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION) {
		return fMOD_STUDIO_PARAMETER_DESCRIPTION == null ? null : this.parameterArray[fMOD_STUDIO_PARAMETER_DESCRIPTION.globalIndex];
	}

	public void update() {
		for (int int1 = 0; int1 < this.parameterList.size(); ++int1) {
			((FMODParameter)this.parameterList.get(int1)).update();
		}
	}
}
