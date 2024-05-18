package zombie.characters.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import zombie.characters.IsoGameCharacter;
import zombie.core.Translator;


public class PerkFactory {
	public static boolean newMode = true;
	public static HashMap PerkMap = new HashMap();
	public static ArrayList PerkList = new ArrayList();
	static float PerkXPReqMultiplier = 1.5F;

	public static String getPerkName(PerkFactory.Perks perks) {
		for (int int1 = 0; int1 < PerkList.size(); ++int1) {
			PerkFactory.Perk perk = (PerkFactory.Perk)PerkList.get(int1);
			if (perk.getType() == perks) {
				return perk.getName();
			}
		}

		return perks.toString();
	}

	public static PerkFactory.Perks getPerkFromName(String string) {
		Iterator iterator = PerkMap.entrySet().iterator();
		Entry entry;
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			entry = (Entry)iterator.next();
		} while (!((PerkFactory.Perk)entry.getValue()).name.equals(string));

		return (PerkFactory.Perks)entry.getKey();
	}

	public static PerkFactory.Perk getPerk(PerkFactory.Perks perks) {
		Iterator iterator = PerkMap.entrySet().iterator();
		Entry entry;
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			entry = (Entry)iterator.next();
		} while (entry.getKey() != perks);

		return (PerkFactory.Perk)entry.getValue();
	}

	public static PerkFactory.Perk AddPerk(PerkFactory.Perks perks, String string, String string2, String string3, String string4, String string5, String string6, int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, int int9, int int10) {
		PerkFactory.Perk perk = new PerkFactory.Perk(string, string2, string3, string4, string5, string6);
		perk.type = perks;
		perk.xp1 = (int)((float)int1 * PerkXPReqMultiplier);
		perk.xp2 = (int)((float)int2 * PerkXPReqMultiplier);
		perk.xp3 = (int)((float)int3 * PerkXPReqMultiplier);
		perk.xp4 = (int)((float)int4 * PerkXPReqMultiplier);
		perk.xp5 = (int)((float)int5 * PerkXPReqMultiplier);
		perk.xp6 = (int)((float)int6 * PerkXPReqMultiplier);
		perk.xp7 = (int)((float)int7 * PerkXPReqMultiplier);
		perk.xp8 = (int)((float)int8 * PerkXPReqMultiplier);
		perk.xp9 = (int)((float)int9 * PerkXPReqMultiplier);
		perk.xp10 = (int)((float)int10 * PerkXPReqMultiplier);
		PerkMap.put(perks, perk);
		PerkList.add(perk);
		return perk;
	}

	public static PerkFactory.Perk AddPerk(PerkFactory.Perks perks, String string, String string2, String string3, String string4, String string5, String string6, int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, int int9, int int10, boolean boolean1) {
		PerkFactory.Perk perk = new PerkFactory.Perk(string, string2, string3, string4, string5, string6);
		perk.passiv = boolean1;
		perk.type = perks;
		perk.xp1 = (int)((float)int1 * PerkXPReqMultiplier);
		perk.xp2 = (int)((float)int2 * PerkXPReqMultiplier);
		perk.xp3 = (int)((float)int3 * PerkXPReqMultiplier);
		perk.xp4 = (int)((float)int4 * PerkXPReqMultiplier);
		perk.xp5 = (int)((float)int5 * PerkXPReqMultiplier);
		perk.xp6 = (int)((float)int6 * PerkXPReqMultiplier);
		perk.xp7 = (int)((float)int7 * PerkXPReqMultiplier);
		perk.xp8 = (int)((float)int8 * PerkXPReqMultiplier);
		perk.xp9 = (int)((float)int9 * PerkXPReqMultiplier);
		perk.xp10 = (int)((float)int10 * PerkXPReqMultiplier);
		PerkMap.put(perks, perk);
		PerkList.add(perk);
		return perk;
	}

	public static PerkFactory.Perk AddPerk(PerkFactory.Perks perks, String string, String string2, String string3, String string4, String string5, String string6, PerkFactory.Perks perks2, int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, int int9, int int10) {
		PerkFactory.Perk perk = new PerkFactory.Perk(string, string2, string3, string4, string5, string6, perks2);
		perk.type = perks;
		perk.xp1 = (int)((float)int1 * PerkXPReqMultiplier);
		perk.xp2 = (int)((float)int2 * PerkXPReqMultiplier);
		perk.xp3 = (int)((float)int3 * PerkXPReqMultiplier);
		perk.xp4 = (int)((float)int4 * PerkXPReqMultiplier);
		perk.xp5 = (int)((float)int5 * PerkXPReqMultiplier);
		perk.xp6 = (int)((float)int6 * PerkXPReqMultiplier);
		perk.xp7 = (int)((float)int7 * PerkXPReqMultiplier);
		perk.xp8 = (int)((float)int8 * PerkXPReqMultiplier);
		perk.xp9 = (int)((float)int9 * PerkXPReqMultiplier);
		perk.xp10 = (int)((float)int10 * PerkXPReqMultiplier);
		PerkMap.put(perks, perk);
		PerkList.add(perk);
		return perk;
	}

	public static PerkFactory.Perk AddPerk(PerkFactory.Perks perks, String string, String string2, String string3, String string4, String string5, String string6, PerkFactory.Perks perks2, int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, int int9, int int10, boolean boolean1) {
		PerkFactory.Perk perk = new PerkFactory.Perk(string, string2, string3, string4, string5, string6, perks2);
		perk.passiv = boolean1;
		perk.type = perks;
		perk.xp1 = (int)((float)int1 * PerkXPReqMultiplier);
		perk.xp2 = (int)((float)int2 * PerkXPReqMultiplier);
		perk.xp3 = (int)((float)int3 * PerkXPReqMultiplier);
		perk.xp4 = (int)((float)int4 * PerkXPReqMultiplier);
		perk.xp5 = (int)((float)int5 * PerkXPReqMultiplier);
		perk.xp6 = (int)((float)int6 * PerkXPReqMultiplier);
		perk.xp7 = (int)((float)int7 * PerkXPReqMultiplier);
		perk.xp8 = (int)((float)int8 * PerkXPReqMultiplier);
		perk.xp9 = (int)((float)int9 * PerkXPReqMultiplier);
		perk.xp10 = (int)((float)int10 * PerkXPReqMultiplier);
		PerkMap.put(perks, perk);
		PerkList.add(perk);
		return perk;
	}

	public static void init() {
		AddPerk(PerkFactory.Perks.BluntParent, Translator.getText("IGUI_perks_Blunt"), "", "", "", "", "", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.BladeParent, Translator.getText("IGUI_perks_Blade"), "", "", "", "", "", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Blunt, Translator.getText("IGUI_perks_Accuracy"), "", "", "", "", "", PerkFactory.Perks.BluntParent, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.BluntGuard, Translator.getText("IGUI_perks_Guard"), "", "", "", "", "", PerkFactory.Perks.BluntParent, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.BluntMaintenance, Translator.getText("IGUI_perks_Maintenance"), "", "", "", "", "", PerkFactory.Perks.BluntParent, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Axe, Translator.getText("IGUI_perks_Accuracy"), "", "", "", "", "", PerkFactory.Perks.BladeParent, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.BladeGuard, Translator.getText("IGUI_perks_Guard"), "", "", "", "", "", PerkFactory.Perks.BladeParent, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.BladeMaintenance, Translator.getText("IGUI_perks_Maintenance"), "", "", "", "", "", PerkFactory.Perks.BladeParent, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Firearm, Translator.getText("IGUI_perks_Firearm"), "", "", "", "", "", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Aiming, Translator.getText("IGUI_perks_Aiming"), "", "", "", "", "", PerkFactory.Perks.Firearm, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Reloading, Translator.getText("IGUI_perks_Reloading"), "", "", "", "", "", PerkFactory.Perks.Firearm, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Crafting, Translator.getText("IGUI_perks_Crafting"), "", "", "", "", "", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Woodwork, Translator.getText("IGUI_perks_Carpentry"), "", "", "", "", "", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Cooking, Translator.getText("IGUI_perks_Cooking"), "", "", "", "", "", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Farming, Translator.getText("IGUI_perks_Farming"), "", "", "", "", "", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Doctor, Translator.getText("IGUI_perks_Doctor"), "", "", "", "", "", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Electricity, Translator.getText("IGUI_perks_Electricity"), "", "", "", "", "", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.MetalWelding, Translator.getText("IGUI_perks_MetalWelding"), "", "", "", "", "", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Mechanics, Translator.getText("IGUI_perks_Mechanics"), "", "", "", "", "", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Survivalist, Translator.getText("IGUI_perks_Survivalist"), "", "", "", "", "", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Fishing, Translator.getText("IGUI_perks_Fishing"), "", "", "", "", "", PerkFactory.Perks.Survivalist, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Trapping, Translator.getText("IGUI_perks_Trapping"), "", "", "", "", "", PerkFactory.Perks.Survivalist, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.PlantScavenging, Translator.getText("IGUI_perks_Foraging"), "", "", "", "", "", PerkFactory.Perks.Survivalist, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Passiv, Translator.getText("IGUI_perks_Passive"), "", "", "", "", "", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Fitness, Translator.getText("IGUI_perks_Fitness"), "", "", "", "", "", PerkFactory.Perks.Passiv, 1000, 2000, 4000, 6000, 12000, 20000, 40000, 60000, 80000, 100000, true);
		AddPerk(PerkFactory.Perks.Strength, Translator.getText("IGUI_perks_Strength"), "", "", "", "", "", PerkFactory.Perks.Passiv, 1000, 2000, 4000, 6000, 12000, 20000, 40000, 60000, 80000, 100000, true);
		AddPerk(PerkFactory.Perks.Agility, Translator.getText("IGUI_perks_Agility"), "", "", "", "", "", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Sprinting, Translator.getText("IGUI_perks_Sprinting"), "", "", "", "", "", PerkFactory.Perks.Agility, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Lightfoot, Translator.getText("IGUI_perks_Lightfooted"), "", "", "", "", "", PerkFactory.Perks.Agility, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Nimble, Translator.getText("IGUI_perks_Nimble"), "", "", "", "", "", PerkFactory.Perks.Agility, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
		AddPerk(PerkFactory.Perks.Sneak, Translator.getText("IGUI_perks_Sneaking"), "", "", "", "", "", PerkFactory.Perks.Agility, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
	}

	public static void CheckForUnlockedPerks(IsoGameCharacter gameCharacter) {
		for (int int1 = 0; int1 < PerkList.size(); ++int1) {
			PerkFactory.Perk perk = (PerkFactory.Perk)PerkList.get(int1);
			int int2 = gameCharacter.getPerkLevel(perk.type);
			if (int2 != 10) {
				int int3 = (int)perk.getTotalXpForLevel(int2 + 1);
				if ((float)int3 <= gameCharacter.getXp().getXP(perk.type) && !gameCharacter.getCanUpgradePerk().contains(perk.type)) {
					if (perk.passiv) {
						gameCharacter.LevelPerk(perk.type, false);
					} else {
						gameCharacter.getCanUpgradePerk().add(perk.type);
					}
				}
			}
		}
	}

	public static void CheckForUnlockedPerks(IsoGameCharacter gameCharacter, PerkFactory.Perk perk) {
		int int1 = gameCharacter.getPerkLevel(perk.type);
		if (int1 != 10) {
			int int2 = (int)perk.getTotalXpForLevel(int1 + 1);
			if ((float)int2 <= gameCharacter.getXp().getXP(perk.type) && !gameCharacter.getCanUpgradePerk().contains(perk.type)) {
				if (perk.passiv) {
					gameCharacter.LevelPerk(perk.type, false);
					CheckForUnlockedPerks(gameCharacter, perk);
				} else {
					gameCharacter.getCanUpgradePerk().add(perk.type);
				}
			}
		}
	}

	public static class Perk {
		public String name;
		public String level1;
		public String level2;
		public String level3;
		public String level4;
		public String level5;
		public boolean passiv = false;
		public int xp1;
		public int xp2;
		public int xp3;
		public int xp4;
		public int xp5;
		public int xp6;
		public int xp7;
		public int xp8;
		public int xp9;
		public int xp10;
		public PerkFactory.Perks parent;
		public PerkFactory.Perks type;

		public Perk(String string, String string2, String string3, String string4, String string5, String string6) {
			this.parent = PerkFactory.Perks.None;
			this.type = PerkFactory.Perks.None;
			this.name = string;
			this.level1 = string2;
			this.level2 = string3;
			this.level3 = string4;
			this.level4 = string5;
			this.level5 = string6;
		}

		public Perk(String string, String string2, String string3, String string4, String string5, String string6, PerkFactory.Perks perks) {
			this.parent = PerkFactory.Perks.None;
			this.type = PerkFactory.Perks.None;
			this.name = string;
			this.level1 = string2;
			this.level2 = string3;
			this.level3 = string4;
			this.level4 = string5;
			this.level5 = string6;
			this.parent = perks;
		}

		public boolean isPassiv() {
			return this.passiv;
		}

		public PerkFactory.Perks getParent() {
			return this.parent;
		}

		public String getName() {
			return this.name;
		}

		public PerkFactory.Perks getType() {
			return this.type;
		}

		public int getXp1() {
			return this.xp1;
		}

		public int getXp2() {
			return this.xp2;
		}

		public int getXp3() {
			return this.xp3;
		}

		public int getXp4() {
			return this.xp4;
		}

		public int getXp5() {
			return this.xp5;
		}

		public int getXp6() {
			return this.xp6;
		}

		public int getXp7() {
			return this.xp7;
		}

		public int getXp8() {
			return this.xp8;
		}

		public int getXp9() {
			return this.xp9;
		}

		public int getXp10() {
			return this.xp10;
		}

		public float getXpForLevel(int int1) {
			if (int1 == 1) {
				return (float)this.xp1;
			} else if (int1 == 2) {
				return (float)this.xp2;
			} else if (int1 == 3) {
				return (float)this.xp3;
			} else if (int1 == 4) {
				return (float)this.xp4;
			} else if (int1 == 5) {
				return (float)this.xp5;
			} else if (int1 == 6) {
				return (float)this.xp6;
			} else if (int1 == 7) {
				return (float)this.xp7;
			} else if (int1 == 8) {
				return (float)this.xp8;
			} else if (int1 == 9) {
				return (float)this.xp9;
			} else {
				return int1 == 10 ? (float)this.xp10 : -1.0F;
			}
		}

		public float getTotalXpForLevel(int int1) {
			int int2 = 0;
			for (int int3 = 1; int3 <= int1; ++int3) {
				float float1 = this.getXpForLevel(int3);
				if (float1 != -1.0F) {
					int2 = (int)((float)int2 + float1);
				}
			}

			return (float)int2;
		}
	}

	public static enum Perks {

		None,
		Agility,
		Cooking,
		Melee,
		Crafting,
		Fitness,
		Strength,
		Blunt,
		Axe,
		Sprinting,
		Lightfoot,
		Nimble,
		Sneak,
		Woodwork,
		Aiming,
		Reloading,
		Farming,
		Survivalist,
		Fishing,
		Trapping,
		Passiv,
		Firearm,
		PlantScavenging,
		BluntParent,
		BladeParent,
		BluntGuard,
		BladeGuard,
		BluntMaintenance,
		BladeMaintenance,
		Doctor,
		Electricity,
		Blacksmith,
		MetalWelding,
		Melting,
		Mechanics,
		MAX,
		index;

		private Perks(int int1) {
			this.index = int1;
		}
		public int index() {
			return this.index;
		}
		public static int getMaxIndex() {
			return MAX.index();
		}
		public static PerkFactory.Perks fromIndex(int int1) {
			return ((PerkFactory.Perks[])PerkFactory.Perks.class.getEnumConstants())[int1];
		}
		public static PerkFactory.Perks FromString(String string) {
			try {
				return valueOf(string);
			} catch (Exception exception) {
				return MAX;
			}
		}
	}
}
