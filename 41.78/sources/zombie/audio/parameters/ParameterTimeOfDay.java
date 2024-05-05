package zombie.audio.parameters;

import zombie.GameTime;
import zombie.audio.FMODGlobalParameter;
import zombie.iso.weather.ClimateManager;


public final class ParameterTimeOfDay extends FMODGlobalParameter {

	public ParameterTimeOfDay() {
		super("TimeOfDay");
	}

	public float calculateCurrentValue() {
		ClimateManager.DayInfo dayInfo = ClimateManager.getInstance().getCurrentDay();
		if (dayInfo == null) {
			return 1.0F;
		} else {
			float float1 = dayInfo.season.getDawn();
			float float2 = dayInfo.season.getDusk();
			float float3 = dayInfo.season.getDayHighNoon();
			float float4 = GameTime.instance.getTimeOfDay();
			if (float4 >= float1 - 1.0F && float4 < float1 + 1.0F) {
				return 0.0F;
			} else if (float4 >= float1 + 1.0F && float4 < float1 + 2.0F) {
				return 1.0F;
			} else if (float4 >= float1 + 2.0F && float4 < float2 - 2.0F) {
				return 2.0F;
			} else if (float4 >= float2 - 2.0F && float4 < float2 - 1.0F) {
				return 3.0F;
			} else {
				return float4 >= float2 - 1.0F && float4 < float2 + 1.0F ? 4.0F : 5.0F;
			}
		}
	}
}
