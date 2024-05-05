package zombie.audio;

import fmod.javafmod;


public abstract class FMODGlobalParameter extends FMODParameter {

	public FMODGlobalParameter(String string) {
		super(string);
		if (this.getParameterDescription() != null && !this.getParameterDescription().isGlobal()) {
			boolean boolean1 = true;
		}
	}

	public void setCurrentValue(float float1) {
		javafmod.FMOD_Studio_System_SetParameterByID(this.getParameterID(), float1, false);
	}

	public void startEventInstance(long long1) {
	}

	public void stopEventInstance(long long1) {
	}
}
