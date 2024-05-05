package zombie.iso.weather;

import zombie.SandboxOptions;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;


public class Temperature {
	public static boolean DO_DEFAULT_BASE = false;
	public static boolean DO_DAYLEN_MOD = true;
	public static String CELSIUS_POSTFIX = "\u00b0C";
	public static String FAHRENHEIT_POSTFIX = "\u00b0F";
	public static final float skinCelciusMin = 20.0F;
	public static final float skinCelciusFavorable = 33.0F;
	public static final float skinCelciusMax = 42.0F;
	public static final float homeostasisDefault = 37.0F;
	public static final float FavorableNakedTemp = 27.0F;
	public static final float FavorableRoomTemp = 22.0F;
	public static final float coreCelciusMin = 20.0F;
	public static final float coreCelciusMax = 42.0F;
	public static final float neutralZone = 27.0F;
	public static final float Hypothermia_1 = 36.5F;
	public static final float Hypothermia_2 = 35.0F;
	public static final float Hypothermia_3 = 30.0F;
	public static final float Hypothermia_4 = 25.0F;
	public static final float Hyperthermia_1 = 37.5F;
	public static final float Hyperthermia_2 = 39.0F;
	public static final float Hyperthermia_3 = 40.0F;
	public static final float Hyperthermia_4 = 41.0F;
	public static final float TrueInsulationMultiplier = 2.0F;
	public static final float TrueWindresistMultiplier = 1.0F;
	public static final float BodyMinTemp = 20.0F;
	public static final float BodyMaxTemp = 42.0F;
	private static String cacheTempString = "";
	private static float cacheTemp = -9000.0F;
	private static Color tempColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);
	private static Color col_0 = new Color(29, 34, 237);
	private static Color col_25 = new Color(0, 255, 234);
	private static Color col_50 = new Color(84, 255, 55);
	private static Color col_75 = new Color(255, 246, 0);
	private static Color col_100 = new Color(255, 0, 0);

	public static String getCelsiusPostfix() {
		return CELSIUS_POSTFIX;
	}

	public static String getFahrenheitPostfix() {
		return FAHRENHEIT_POSTFIX;
	}

	public static String getTemperaturePostfix() {
		return Core.OptionTemperatureDisplayCelsius ? CELSIUS_POSTFIX : FAHRENHEIT_POSTFIX;
	}

	public static String getTemperatureString(float float1) {
		float float2 = Core.OptionTemperatureDisplayCelsius ? float1 : CelsiusToFahrenheit(float1);
		float2 = (float)Math.round(float2 * 10.0F) / 10.0F;
		if (cacheTemp != float2) {
			cacheTemp = float2;
			cacheTempString = float2 + " " + getTemperaturePostfix();
		}

		return cacheTempString;
	}

	public static float CelsiusToFahrenheit(float float1) {
		return float1 * 1.8F + 32.0F;
	}

	public static float FahrenheitToCelsius(float float1) {
		return (float1 - 32.0F) / 1.8F;
	}

	public static float WindchillCelsiusKph(float float1, float float2) {
		float float3 = 13.12F + 0.6215F * float1 - 11.37F * (float)Math.pow((double)float2, 0.1599999964237213) + 0.3965F * float1 * (float)Math.pow((double)float2, 0.1599999964237213);
		return float3 < float1 ? float3 : float1;
	}

	public static float getTrueInsulationValue(float float1) {
		return float1 * 2.0F + 0.5F * float1 * float1 * float1;
	}

	public static float getTrueWindresistanceValue(float float1) {
		return float1 * 1.0F + 0.5F * float1 * float1;
	}

	public static void reset() {
	}

	public static float getFractionForRealTimeRatePerMin(float float1) {
		if (DO_DEFAULT_BASE) {
			return float1 / (1440.0F / (float)SandboxOptions.instance.getDayLengthMinutesDefault());
		} else if (!DO_DAYLEN_MOD) {
			return float1 / (1440.0F / (float)SandboxOptions.instance.getDayLengthMinutes());
		} else {
			float float2 = (float)SandboxOptions.instance.getDayLengthMinutes() / (float)SandboxOptions.instance.getDayLengthMinutesDefault();
			if (float2 < 1.0F) {
				float2 = 0.5F + 0.5F * float2;
			} else if (float2 > 1.0F) {
				float2 = 1.0F + float2 / 16.0F;
			}

			return float1 / (1440.0F / (float)SandboxOptions.instance.getDayLengthMinutes()) * float2;
		}
	}

	public static Color getValueColor(float float1) {
		float1 = ClimateManager.clamp(0.0F, 1.0F, float1);
		tempColor.set(0.0F, 0.0F, 0.0F, 1.0F);
		float float2 = 0.0F;
		if (float1 < 0.25F) {
			float2 = float1 / 0.25F;
			col_0.interp(col_25, float2, tempColor);
		} else if (float1 < 0.5F) {
			float2 = (float1 - 0.25F) / 0.25F;
			col_25.interp(col_50, float2, tempColor);
		} else if (float1 < 0.75F) {
			float2 = (float1 - 0.5F) / 0.25F;
			col_50.interp(col_75, float2, tempColor);
		} else {
			float2 = (float1 - 0.75F) / 0.25F;
			col_75.interp(col_100, float2, tempColor);
		}

		return tempColor;
	}

	public static float getWindChillAmountForPlayer(IsoPlayer player) {
		if (player.getVehicle() == null && (player.getSquare() == null || !player.getSquare().isInARoom())) {
			ClimateManager climateManager = ClimateManager.getInstance();
			float float1 = climateManager.getAirTemperatureForCharacter(player, true);
			float float2 = 0.0F;
			if (float1 < climateManager.getTemperature()) {
				float2 = climateManager.getTemperature() - float1;
			}

			return float2;
		} else {
			return 0.0F;
		}
	}
}
