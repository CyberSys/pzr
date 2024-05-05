package zombie.characters;

import java.util.ArrayList;
import java.util.Arrays;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;
import zombie.core.ImmutableColor;
import zombie.core.skinnedmodel.population.BeardStyle;
import zombie.core.skinnedmodel.population.HairStyle;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.iso.IsoWorld;
import zombie.util.StringUtils;
import zombie.util.Type;


public final class HairOutfitDefinitions {
	public static final HairOutfitDefinitions instance = new HairOutfitDefinitions();
	public boolean m_dirty = true;
	public String hairStyle;
	public int minWorldAge;
	public final ArrayList m_haircutDefinition = new ArrayList();
	public final ArrayList m_outfitDefinition = new ArrayList();
	private final ThreadLocal m_tempHairStyles = ThreadLocal.withInitial(ArrayList::new);

	public void checkDirty() {
		if (this.m_dirty) {
			this.m_dirty = false;
			this.init();
		}
	}

	private void init() {
		this.m_haircutDefinition.clear();
		this.m_outfitDefinition.clear();
		KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.env.rawget("HairOutfitDefinitions");
		if (kahluaTableImpl != null) {
			KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)Type.tryCastTo(kahluaTableImpl.rawget("haircutDefinition"), KahluaTableImpl.class);
			if (kahluaTableImpl2 != null) {
				KahluaTableIterator kahluaTableIterator = kahluaTableImpl2.iterator();
				KahluaTableImpl kahluaTableImpl3;
				while (kahluaTableIterator.advance()) {
					kahluaTableImpl3 = (KahluaTableImpl)Type.tryCastTo(kahluaTableIterator.getValue(), KahluaTableImpl.class);
					if (kahluaTableImpl3 != null) {
						HairOutfitDefinitions.HaircutDefinition haircutDefinition = new HairOutfitDefinitions.HaircutDefinition(kahluaTableImpl3.rawgetStr("name"), kahluaTableImpl3.rawgetInt("minWorldAge"), new ArrayList(Arrays.asList(kahluaTableImpl3.rawgetStr("onlyFor").split(","))));
						this.m_haircutDefinition.add(haircutDefinition);
					}
				}

				kahluaTableImpl3 = (KahluaTableImpl)Type.tryCastTo(kahluaTableImpl.rawget("haircutOutfitDefinition"), KahluaTableImpl.class);
				if (kahluaTableImpl3 != null) {
					kahluaTableIterator = kahluaTableImpl3.iterator();
					while (kahluaTableIterator.advance()) {
						KahluaTableImpl kahluaTableImpl4 = (KahluaTableImpl)Type.tryCastTo(kahluaTableIterator.getValue(), KahluaTableImpl.class);
						if (kahluaTableImpl4 != null) {
							HairOutfitDefinitions.HaircutOutfitDefinition haircutOutfitDefinition = new HairOutfitDefinitions.HaircutOutfitDefinition(kahluaTableImpl4.rawgetStr("outfit"), initStringChance(kahluaTableImpl4.rawgetStr("haircut")), initStringChance(kahluaTableImpl4.rawgetStr("beard")), initStringChance(kahluaTableImpl4.rawgetStr("haircutColor")));
							this.m_outfitDefinition.add(haircutOutfitDefinition);
						}
					}
				}
			}
		}
	}

	public boolean isHaircutValid(String string, String string2) {
		instance.checkDirty();
		if (StringUtils.isNullOrEmpty(string)) {
			return true;
		} else {
			for (int int1 = 0; int1 < instance.m_haircutDefinition.size(); ++int1) {
				HairOutfitDefinitions.HaircutDefinition haircutDefinition = (HairOutfitDefinitions.HaircutDefinition)instance.m_haircutDefinition.get(int1);
				if (haircutDefinition.hairStyle.equals(string2)) {
					if (!haircutDefinition.onlyFor.contains(string)) {
						return false;
					}

					if (IsoWorld.instance.getWorldAgeDays() < (float)haircutDefinition.minWorldAge) {
						return false;
					}
				}
			}

			return true;
		}
	}

	public void getValidHairStylesForOutfit(String string, ArrayList arrayList, ArrayList arrayList2) {
		arrayList2.clear();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			HairStyle hairStyle = (HairStyle)arrayList.get(int1);
			if (!hairStyle.isNoChoose() && this.isHaircutValid(string, hairStyle.name)) {
				arrayList2.add(hairStyle);
			}
		}
	}

	public String getRandomHaircut(String string, ArrayList arrayList) {
		ArrayList arrayList2 = (ArrayList)this.m_tempHairStyles.get();
		this.getValidHairStylesForOutfit(string, arrayList, arrayList2);
		if (arrayList2.isEmpty()) {
			return "";
		} else {
			String string2 = ((HairStyle)OutfitRNG.pickRandom(arrayList2)).name;
			boolean boolean1 = false;
			for (int int1 = 0; int1 < instance.m_outfitDefinition.size() && !boolean1; ++int1) {
				HairOutfitDefinitions.HaircutOutfitDefinition haircutOutfitDefinition = (HairOutfitDefinitions.HaircutOutfitDefinition)instance.m_outfitDefinition.get(int1);
				if (haircutOutfitDefinition.outfit.equals(string) && haircutOutfitDefinition.haircutChance != null) {
					float float1 = OutfitRNG.Next(0.0F, 100.0F);
					float float2 = 0.0F;
					for (int int2 = 0; int2 < haircutOutfitDefinition.haircutChance.size(); ++int2) {
						HairOutfitDefinitions.StringChance stringChance = (HairOutfitDefinitions.StringChance)haircutOutfitDefinition.haircutChance.get(int2);
						float2 += stringChance.chance;
						if (float1 < float2) {
							string2 = stringChance.str;
							if ("null".equalsIgnoreCase(stringChance.str)) {
								string2 = "";
							}

							if ("random".equalsIgnoreCase(stringChance.str)) {
								string2 = ((HairStyle)OutfitRNG.pickRandom(arrayList2)).name;
							}

							boolean1 = true;
							break;
						}
					}
				}
			}

			return string2;
		}
	}

	public ImmutableColor getRandomHaircutColor(String string) {
		ImmutableColor immutableColor = (ImmutableColor)SurvivorDesc.HairCommonColors.get(OutfitRNG.Next(SurvivorDesc.HairCommonColors.size()));
		String string2 = null;
		boolean boolean1 = false;
		for (int int1 = 0; int1 < instance.m_outfitDefinition.size() && !boolean1; ++int1) {
			HairOutfitDefinitions.HaircutOutfitDefinition haircutOutfitDefinition = (HairOutfitDefinitions.HaircutOutfitDefinition)instance.m_outfitDefinition.get(int1);
			if (haircutOutfitDefinition.outfit.equals(string) && haircutOutfitDefinition.haircutColor != null) {
				float float1 = OutfitRNG.Next(0.0F, 100.0F);
				float float2 = 0.0F;
				for (int int2 = 0; int2 < haircutOutfitDefinition.haircutColor.size(); ++int2) {
					HairOutfitDefinitions.StringChance stringChance = (HairOutfitDefinitions.StringChance)haircutOutfitDefinition.haircutColor.get(int2);
					float2 += stringChance.chance;
					if (float1 < float2) {
						string2 = stringChance.str;
						if ("random".equalsIgnoreCase(stringChance.str)) {
							immutableColor = (ImmutableColor)SurvivorDesc.HairCommonColors.get(OutfitRNG.Next(SurvivorDesc.HairCommonColors.size()));
							string2 = null;
						}

						boolean1 = true;
						break;
					}
				}
			}
		}

		if (!StringUtils.isNullOrEmpty(string2)) {
			String[] stringArray = string2.split(",");
			immutableColor = new ImmutableColor(Float.parseFloat(stringArray[0]), Float.parseFloat(stringArray[1]), Float.parseFloat(stringArray[2]));
		}

		return immutableColor;
	}

	public String getRandomBeard(String string, ArrayList arrayList) {
		String string2 = ((BeardStyle)OutfitRNG.pickRandom(arrayList)).name;
		boolean boolean1 = false;
		for (int int1 = 0; int1 < instance.m_outfitDefinition.size() && !boolean1; ++int1) {
			HairOutfitDefinitions.HaircutOutfitDefinition haircutOutfitDefinition = (HairOutfitDefinitions.HaircutOutfitDefinition)instance.m_outfitDefinition.get(int1);
			if (haircutOutfitDefinition.outfit.equals(string) && haircutOutfitDefinition.beardChance != null) {
				float float1 = OutfitRNG.Next(0.0F, 100.0F);
				float float2 = 0.0F;
				for (int int2 = 0; int2 < haircutOutfitDefinition.beardChance.size(); ++int2) {
					HairOutfitDefinitions.StringChance stringChance = (HairOutfitDefinitions.StringChance)haircutOutfitDefinition.beardChance.get(int2);
					float2 += stringChance.chance;
					if (float1 < float2) {
						string2 = stringChance.str;
						if ("null".equalsIgnoreCase(stringChance.str)) {
							string2 = "";
						}

						if ("random".equalsIgnoreCase(stringChance.str)) {
							string2 = ((BeardStyle)OutfitRNG.pickRandom(arrayList)).name;
						}

						boolean1 = true;
						break;
					}
				}
			}
		}

		return string2;
	}

	private static ArrayList initStringChance(String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			return null;
		} else {
			ArrayList arrayList = new ArrayList();
			String[] stringArray = string.split(";");
			int int1 = 0;
			String[] stringArray2 = stringArray;
			int int2 = stringArray.length;
			for (int int3 = 0; int3 < int2; ++int3) {
				String string2 = stringArray2[int3];
				String[] stringArray3 = string2.split(":");
				HairOutfitDefinitions.StringChance stringChance = new HairOutfitDefinitions.StringChance();
				stringChance.str = stringArray3[0];
				stringChance.chance = Float.parseFloat(stringArray3[1]);
				int1 = (int)((float)int1 + stringChance.chance);
				arrayList.add(stringChance);
			}

			if (int1 < 100) {
				HairOutfitDefinitions.StringChance stringChance2 = new HairOutfitDefinitions.StringChance();
				stringChance2.str = "random";
				stringChance2.chance = (float)(100 - int1);
				arrayList.add(stringChance2);
			}

			return arrayList;
		}
	}

	public static final class HaircutDefinition {
		public String hairStyle;
		public int minWorldAge;
		public ArrayList onlyFor;

		public HaircutDefinition(String string, int int1, ArrayList arrayList) {
			this.hairStyle = string;
			this.minWorldAge = int1;
			this.onlyFor = arrayList;
		}
	}

	public static final class HaircutOutfitDefinition {
		public String outfit;
		public ArrayList haircutChance;
		public ArrayList beardChance;
		public ArrayList haircutColor;

		public HaircutOutfitDefinition(String string, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
			this.outfit = string;
			this.haircutChance = arrayList;
			this.beardChance = arrayList2;
			this.haircutColor = arrayList3;
		}
	}

	private static final class StringChance {
		String str;
		float chance;
	}
}
