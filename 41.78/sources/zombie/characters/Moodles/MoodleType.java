package zombie.characters.Moodles;

import zombie.core.Translator;



public enum MoodleType {

	Endurance,
	Tired,
	Hungry,
	Panic,
	Sick,
	Bored,
	Unhappy,
	Bleeding,
	Wet,
	HasACold,
	Angry,
	Stress,
	Thirst,
	Injured,
	Pain,
	HeavyLoad,
	Drunk,
	Dead,
	Zombie,
	Hyperthermia,
	Hypothermia,
	Windchill,
	CantSprint,
	FoodEaten,
	MAX;

	public static MoodleType FromIndex(int int1) {
		switch (int1) {
		case 0: 
			return Endurance;
		
		case 1: 
			return Tired;
		
		case 2: 
			return Hungry;
		
		case 3: 
			return Panic;
		
		case 4: 
			return Sick;
		
		case 5: 
			return Bored;
		
		case 6: 
			return Unhappy;
		
		case 7: 
			return Bleeding;
		
		case 8: 
			return Wet;
		
		case 9: 
			return HasACold;
		
		case 10: 
			return Angry;
		
		case 11: 
			return Stress;
		
		case 12: 
			return Thirst;
		
		case 13: 
			return Injured;
		
		case 14: 
			return Pain;
		
		case 15: 
			return HeavyLoad;
		
		case 16: 
			return Drunk;
		
		case 17: 
			return Dead;
		
		case 18: 
			return Zombie;
		
		case 19: 
			return FoodEaten;
		
		case 20: 
			return Hyperthermia;
		
		case 21: 
			return Hypothermia;
		
		case 22: 
			return Windchill;
		
		case 23: 
			return CantSprint;
		
		default: 
			return MAX;
		
		}
	}
	public static MoodleType FromString(String string) {
		if (string.equals("Endurance")) {
			return Endurance;
		} else if (string.equals("Tired")) {
			return Tired;
		} else if (string.equals("Hungry")) {
			return Hungry;
		} else if (string.equals("Panic")) {
			return Panic;
		} else if (string.equals("Sick")) {
			return Sick;
		} else if (string.equals("Bored")) {
			return Bored;
		} else if (string.equals("Unhappy")) {
			return Unhappy;
		} else if (string.equals("Bleeding")) {
			return Bleeding;
		} else if (string.equals("Wet")) {
			return Wet;
		} else if (string.equals("HasACold")) {
			return HasACold;
		} else if (string.equals("Angry")) {
			return Angry;
		} else if (string.equals("Stress")) {
			return Stress;
		} else if (string.equals("Thirst")) {
			return Thirst;
		} else if (string.equals("Injured")) {
			return Injured;
		} else if (string.equals("Pain")) {
			return Pain;
		} else if (string.equals("HeavyLoad")) {
			return HeavyLoad;
		} else if (string.equals("Drunk")) {
			return Drunk;
		} else if (string.equals("Dead")) {
			return Dead;
		} else if (string.equals("Zombie")) {
			return Zombie;
		} else if (string.equals("Windchill")) {
			return Windchill;
		} else if (string.equals("FoodEaten")) {
			return FoodEaten;
		} else if (string.equals("Hyperthermia")) {
			return Hyperthermia;
		} else if (string.equals("Hypothermia")) {
			return Hypothermia;
		} else {
			return string.equals("CantSprint") ? CantSprint : MAX;
		}
	}
	public static String getDisplayName(MoodleType moodleType, int int1) {
		if (int1 > 4) {
			int1 = 4;
		}

		if (int1 == 0) {
			return "Invalid Moodle Level";
		} else if (moodleType == CantSprint) {
			return Translator.getText("Moodles_CantSprint");
		} else {
			if (moodleType == Endurance) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_endurance_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_endurance_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_endurance_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_endurance_lvl4");
				
				}
			}

			if (moodleType == Angry) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_angry_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_angry_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_angry_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_angry_lvl4");
				
				}
			}

			if (moodleType == Stress) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_stress_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_stress_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_stress_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_stress_lvl4");
				
				}
			}

			if (moodleType == Thirst) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_thirst_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_thirst_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_thirst_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_thirst_lvl4");
				
				}
			}

			if (moodleType == Tired) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_tired_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_tired_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_tired_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_tired_lvl4");
				
				}
			}

			if (moodleType == Hungry) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_hungry_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_hungry_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_hungry_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_hungry_lvl4");
				
				}
			}

			if (moodleType == Panic) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_panic_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_panic_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_panic_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_panic_lvl4");
				
				}
			}

			if (moodleType == Sick) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_sick_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_sick_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_sick_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_sick_lvl4");
				
				}
			}

			if (moodleType == Bored) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_bored_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_bored_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_bored_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_bored_lvl4");
				
				}
			}

			if (moodleType == Unhappy) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_unhappy_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_unhappy_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_unhappy_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_unhappy_lvl4");
				
				}
			}

			if (moodleType == Bleeding) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_bleeding_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_bleeding_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_bleeding_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_bleeding_lvl4");
				
				}
			}

			if (moodleType == Wet) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_wet_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_wet_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_wet_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_wet_lvl4");
				
				}
			}

			if (moodleType == HasACold) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_hascold_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_hascold_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_hascold_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_hascold_lvl4");
				
				}
			}

			if (moodleType == Injured) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_injured_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_injured_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_injured_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_injured_lvl4");
				
				}
			}

			if (moodleType == Pain) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_pain_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_pain_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_pain_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_pain_lvl4");
				
				}
			}

			if (moodleType == HeavyLoad) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_heavyload_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_heavyload_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_heavyload_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_heavyload_lvl4");
				
				}
			}

			if (moodleType == Drunk) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_drunk_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_drunk_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_drunk_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_drunk_lvl4");
				
				}
			}

			if (moodleType == Dead) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_dead_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_dead_lvl1");
				
				case 3: 
					return Translator.getText("Moodles_dead_lvl1");
				
				case 4: 
					return Translator.getText("Moodles_dead_lvl1");
				
				}
			}

			if (moodleType == Zombie) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_zombie_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_zombie_lvl1");
				
				case 3: 
					return Translator.getText("Moodles_zombie_lvl1");
				
				case 4: 
					return Translator.getText("Moodles_zombie_lvl1");
				
				}
			}

			if (moodleType == Windchill) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_windchill_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_windchill_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_windchill_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_windchill_lvl4");
				
				}
			}

			if (moodleType == FoodEaten) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_foodeaten_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_foodeaten_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_foodeaten_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_foodeaten_lvl4");
				
				}
			}

			if (moodleType == Hyperthermia) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_hyperthermia_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_hyperthermia_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_hyperthermia_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_hyperthermia_lvl4");
				
				}
			}

			if (moodleType == Hypothermia) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_hypothermia_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_hypothermia_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_hypothermia_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_hypothermia_lvl4");
				
				}
			}

			return "Unkown Moodle Type";
		}
	}
	public static String getDescriptionText(MoodleType moodleType, int int1) {
		if (int1 > 4) {
			int1 = 4;
		}

		if (int1 == 0) {
			return "Invalid Moodle Level";
		} else if (moodleType == CantSprint) {
			return Translator.getText("Moodles_CantSprint_desc");
		} else {
			if (moodleType == Endurance) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_endurance_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_endurance_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_endurance_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_endurance_desc_lvl4");
				
				}
			}

			if (moodleType == Angry) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_angry_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_angry_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_angry_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_angry_desc_lvl4");
				
				}
			}

			if (moodleType == Stress) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_stress_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_stress_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_stress_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_stress_desc_lvl4");
				
				}
			}

			if (moodleType == Thirst) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_thirst_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_thirst_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_thirst_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_thirst_desc_lvl4");
				
				}
			}

			if (moodleType == Tired) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_tired_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_tired_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_tired_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_tired_desc_lvl4");
				
				}
			}

			if (moodleType == Hungry) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_hungry_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_hungry_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_hungry_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_hungry_desc_lvl4");
				
				}
			}

			if (moodleType == Panic) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_panic_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_panic_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_panic_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_panic_desc_lvl4");
				
				}
			}

			if (moodleType == Sick) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_sick_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_sick_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_sick_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_sick_desc_lvl4");
				
				}
			}

			if (moodleType == Bored) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_bored_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_bored_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_bored_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_bored_desc_lvl4");
				
				}
			}

			if (moodleType == Unhappy) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_unhappy_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_unhappy_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_unhappy_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_unhappy_desc_lvl4");
				
				}
			}

			if (moodleType == Bleeding) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_bleed_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_bleed_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_bleed_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_bleed_desc_lvl4");
				
				}
			}

			if (moodleType == Wet) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_wet_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_wet_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_wet_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_wet_desc_lvl4");
				
				}
			}

			if (moodleType == HasACold) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_hasacold_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_hasacold_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_hasacold_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_hasacold_desc_lvl4");
				
				}
			}

			if (moodleType == Injured) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_injured_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_injured_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_injured_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_injured_desc_lvl4");
				
				}
			}

			if (moodleType == Pain) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_pain_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_pain_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_pain_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_pain_desc_lvl4");
				
				}
			}

			if (moodleType == HeavyLoad) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_heavyload_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_heavyload_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_heavyload_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_heavyload_desc_lvl4");
				
				}
			}

			if (moodleType == Drunk) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_drunk_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_drunk_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_drunk_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_drunk_desc_lvl4");
				
				}
			}

			if (moodleType == Dead) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_dead_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_dead_desc_lvl1");
				
				case 3: 
					return Translator.getText("Moodles_dead_desc_lvl1");
				
				case 4: 
					return Translator.getText("Moodles_dead_desc_lvl1");
				
				}
			}

			if (moodleType == Zombie) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_zombified_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_zombified_desc_lvl1");
				
				case 3: 
					return Translator.getText("Moodles_zombified_desc_lvl1");
				
				case 4: 
					return Translator.getText("Moodles_zombified_desc_lvl1");
				
				}
			}

			if (moodleType == Windchill) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_windchill_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_windchill_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_windchill_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_windchill_desc_lvl4");
				
				}
			}

			if (moodleType == FoodEaten) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_foodeaten_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_foodeaten_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_foodeaten_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_foodeaten_desc_lvl4");
				
				}
			}

			if (moodleType == Hyperthermia) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_hyperthermia_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_hyperthermia_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_hyperthermia_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_hyperthermia_desc_lvl4");
				
				}
			}

			if (moodleType == Hypothermia) {
				switch (int1) {
				case 1: 
					return Translator.getText("Moodles_hypothermia_desc_lvl1");
				
				case 2: 
					return Translator.getText("Moodles_hypothermia_desc_lvl2");
				
				case 3: 
					return Translator.getText("Moodles_hypothermia_desc_lvl3");
				
				case 4: 
					return Translator.getText("Moodles_hypothermia_desc_lvl4");
				
				}
			}

			return "Unkown Moodle Type";
		}
	}
	public static int GoodBadNeutral(MoodleType moodleType) {
		if (moodleType == Endurance) {
			return 2;
		} else if (moodleType == Tired) {
			return 2;
		} else if (moodleType == Hungry) {
			return 2;
		} else if (moodleType == Panic) {
			return 2;
		} else if (moodleType == Sick) {
			return 2;
		} else if (moodleType == Bored) {
			return 2;
		} else if (moodleType == Unhappy) {
			return 2;
		} else if (moodleType == Bleeding) {
			return 2;
		} else if (moodleType == Wet) {
			return 2;
		} else if (moodleType == HasACold) {
			return 2;
		} else if (moodleType == Angry) {
			return 2;
		} else if (moodleType == Stress) {
			return 2;
		} else if (moodleType == Thirst) {
			return 2;
		} else if (moodleType == CantSprint) {
			return 2;
		} else if (moodleType == Injured) {
			return 2;
		} else if (moodleType == Pain) {
			return 2;
		} else if (moodleType == HeavyLoad) {
			return 2;
		} else if (moodleType == Drunk) {
			return 2;
		} else if (moodleType == Dead) {
			return 2;
		} else if (moodleType == Zombie) {
			return 2;
		} else if (moodleType == Windchill) {
			return 2;
		} else if (moodleType == FoodEaten) {
			return 1;
		} else if (moodleType == Hyperthermia) {
			return 2;
		} else {
			return moodleType == Hypothermia ? 2 : 2;
		}
	}
	public static int ToIndex(MoodleType moodleType) {
		if (moodleType == null) {
			return 0;
		} else {
			switch (moodleType) {
			case Endurance: 
				return 0;
			
			case Tired: 
				return 1;
			
			case Hungry: 
				return 2;
			
			case Panic: 
				return 3;
			
			case Sick: 
				return 4;
			
			case Bored: 
				return 5;
			
			case Unhappy: 
				return 6;
			
			case Bleeding: 
				return 7;
			
			case Wet: 
				return 8;
			
			case HasACold: 
				return 9;
			
			case Angry: 
				return 10;
			
			case Stress: 
				return 11;
			
			case Thirst: 
				return 12;
			
			case Injured: 
				return 13;
			
			case Pain: 
				return 14;
			
			case HeavyLoad: 
				return 15;
			
			case Drunk: 
				return 16;
			
			case Dead: 
				return 17;
			
			case Zombie: 
				return 18;
			
			case FoodEaten: 
				return 19;
			
			case Hyperthermia: 
				return 20;
			
			case Hypothermia: 
				return 21;
			
			case Windchill: 
				return 22;
			
			case CantSprint: 
				return 23;
			
			case MAX: 
				return 24;
			
			default: 
				return 0;
			
			}
		}
	}
	private static MoodleType[] $values() {
		return new MoodleType[]{Endurance, Tired, Hungry, Panic, Sick, Bored, Unhappy, Bleeding, Wet, HasACold, Angry, Stress, Thirst, Injured, Pain, HeavyLoad, Drunk, Dead, Zombie, Hyperthermia, Hypothermia, Windchill, CantSprint, FoodEaten, MAX};
	}
}
