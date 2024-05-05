package zombie.audio;

import fmod.fmod.FMODManager;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import fmod.fmod.FMOD_STUDIO_PARAMETER_ID;


public abstract class FMODParameter {
	private final String m_name;
	private final FMOD_STUDIO_PARAMETER_DESCRIPTION m_parameterDescription;
	private float m_currentValue = Float.NaN;

	public FMODParameter(String string) {
		this.m_name = string;
		this.m_parameterDescription = FMODManager.instance.getParameterDescription(string);
	}

	public String getName() {
		return this.m_name;
	}

	public FMOD_STUDIO_PARAMETER_DESCRIPTION getParameterDescription() {
		return this.m_parameterDescription;
	}

	public FMOD_STUDIO_PARAMETER_ID getParameterID() {
		return this.m_parameterDescription == null ? null : this.m_parameterDescription.id;
	}

	public float getCurrentValue() {
		return this.m_currentValue;
	}

	public void update() {
		float float1 = this.calculateCurrentValue();
		if (float1 != this.m_currentValue) {
			this.m_currentValue = float1;
			this.setCurrentValue(this.m_currentValue);
		}
	}

	public void resetToDefault() {
	}

	public abstract float calculateCurrentValue();

	public abstract void setCurrentValue(float float1);

	public abstract void startEventInstance(long long1);

	public abstract void stopEventInstance(long long1);
}
