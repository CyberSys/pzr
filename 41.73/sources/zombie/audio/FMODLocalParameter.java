package zombie.audio;

import fmod.javafmod;
import gnu.trove.list.array.TLongArrayList;


public class FMODLocalParameter extends FMODParameter {
	private final TLongArrayList m_instances = new TLongArrayList();

	public FMODLocalParameter(String string) {
		super(string);
		if (this.getParameterDescription() != null && this.getParameterDescription().isGlobal()) {
			boolean boolean1 = true;
		}
	}

	public float calculateCurrentValue() {
		return 0.0F;
	}

	public void setCurrentValue(float float1) {
		for (int int1 = 0; int1 < this.m_instances.size(); ++int1) {
			long long1 = this.m_instances.get(int1);
			javafmod.FMOD_Studio_EventInstance_SetParameterByID(long1, this.getParameterID(), float1, false);
		}
	}

	public void startEventInstance(long long1) {
		this.m_instances.add(long1);
		javafmod.FMOD_Studio_EventInstance_SetParameterByID(long1, this.getParameterID(), this.getCurrentValue(), false);
	}

	public void stopEventInstance(long long1) {
		this.m_instances.remove(long1);
	}
}
