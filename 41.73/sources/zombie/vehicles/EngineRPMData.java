package zombie.vehicles;


public final class EngineRPMData {
	public float gearChange;
	public float afterGearChange;

	public EngineRPMData() {
	}

	public EngineRPMData(float float1, float float2) {
		this.gearChange = float1;
		this.afterGearChange = float2;
	}

	public void reset() {
		this.gearChange = 0.0F;
		this.afterGearChange = 0.0F;
	}
}
