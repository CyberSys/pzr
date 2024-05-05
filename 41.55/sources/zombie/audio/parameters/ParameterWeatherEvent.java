package zombie.audio.parameters;

import zombie.SandboxOptions;
import zombie.audio.FMODGlobalParameter;
import zombie.iso.weather.ClimateManager;


public final class ParameterWeatherEvent extends FMODGlobalParameter {
	private ParameterWeatherEvent.Event event;

	public ParameterWeatherEvent() {
		super("WeatherEvent");
		this.event = ParameterWeatherEvent.Event.None;
	}

	public float calculateCurrentValue() {
		float float1 = ClimateManager.getInstance().getSnowFracNow();
		if (!SandboxOptions.instance.EnableSnowOnGround.getValue()) {
			float1 = 0.0F;
		}

		return (float)this.event.value;
	}

	public static enum Event {

		None,
		FreshSnow,
		value;

		private Event(int int1) {
			this.value = int1;
		}
		private static ParameterWeatherEvent.Event[] $values() {
			return new ParameterWeatherEvent.Event[]{None, FreshSnow};
		}
	}
}
