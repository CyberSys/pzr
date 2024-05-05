package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.iso.weather.ClimateManager;


public final class ParameterSeason extends FMODGlobalParameter {
	public ParameterSeason() {
		super("Season");
	}

	public float calculateCurrentValue() {
		ClimateManager.DayInfo dayInfo = ClimateManager.getInstance().getCurrentDay();
		if (dayInfo == null) {
			return 0.0F;
		} else {
			float float1;
			switch (dayInfo.season.getSeason()) {
			case 1: 
				float1 = 0.0F;
				break;
			
			case 2: 
			
			case 3: 
				float1 = 1.0F;
				break;
			
			case 4: 
				float1 = 2.0F;
				break;
			
			case 5: 
				float1 = 3.0F;
				break;
			
			default: 
				float1 = 1.0F;
			
			}

			return float1;
		}
	}
}
