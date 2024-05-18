package zombie.iso.weather;

import zombie.debug.DebugLog;


public class ClimateMoon {
	private static final int[] day_year = new int[]{-1, -1, 30, 58, 89, 119, 150, 180, 211, 241, 272, 303, 333};
	private static final String[] moon_phase_name = new String[]{"New", "Waxing crescent", "First quarter", "Waxing gibbous", "Full", "Waning gibbous", "Third quarter", "Waning crescent"};
	private static final float[] units = new float[]{0.0F, 0.25F, 0.5F, 0.75F, 1.0F, 0.75F, 0.5F, 0.25F};
	private static int last_year;
	private static int last_month;
	private static int last_day;
	private static int current_phase = 0;
	private static float current_float = 0.0F;

	public static void updatePhase(int int1, int int2, int int3) {
		if (int1 != last_year || int2 != last_month || int3 != last_day) {
			last_year = int1;
			last_month = int2;
			last_day = int3;
			current_phase = getMoonPhase(int1, int2, int3);
			if (current_phase > 7) {
				current_phase = 7;
			}

			if (current_phase < 0) {
				current_phase = 0;
			}

			current_float = units[current_phase];
			DebugLog.log("Updated MoonPhase = " + getPhaseName() + ", float = " + current_float + ", int = " + current_phase);
		}
	}

	public static String getPhaseName() {
		return moon_phase_name[current_phase];
	}

	public static float getMoonFloat() {
		return current_float;
	}

	private static int getMoonPhase(int int1, int int2, int int3) {
		if (int2 < 0 || int2 > 12) {
			int2 = 0;
		}

		int int4 = int3 + day_year[int2];
		if (int2 > 2 && isLeapYearP(int1)) {
			++int4;
		}

		int int5 = int1 / 100 + 1;
		int int6 = int1 % 19 + 1;
		int int7 = (11 * int6 + 20 + (8 * int5 + 5) / 25 - 5 - (3 * int5 / 4 - 12)) % 30;
		if (int7 <= 0) {
			int7 += 30;
		}

		if (int7 == 25 && int6 > 11 || int7 == 24) {
			++int7;
		}

		int int8 = ((int4 + int7) * 6 + 11) % 177 / 22 & 7;
		return int8;
	}

	private static int daysInMonth(int int1, int int2) {
		int int3 = 31;
		switch (int1) {
		case 2: 
			int3 = isLeapYearP(int2) ? 29 : 28;
		
		case 3: 
		
		case 5: 
		
		case 7: 
		
		case 8: 
		
		case 10: 
		
		default: 
			break;
		
		case 4: 
		
		case 6: 
		
		case 9: 
		
		case 11: 
			int3 = 30;
		
		}
		return int3;
	}

	private static boolean isLeapYearP(int int1) {
		return int1 % 4 == 0 && (int1 % 400 == 0 || int1 % 100 != 0);
	}
}
