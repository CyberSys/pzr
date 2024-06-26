package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;


public final class ParameterFireSize extends FMODLocalParameter {
	private int size = 0;

	public ParameterFireSize() {
		super("FireSize");
	}

	public float calculateCurrentValue() {
		return (float)this.size;
	}

	public void setSize(int int1) {
		this.size = int1;
	}
}
