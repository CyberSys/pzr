package zombie.characters.professions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;
import zombie.characters.skills.PerkFactory;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.interfaces.IListBoxItem;


public final class ProfessionFactory {
	public static LinkedHashMap ProfessionMap = new LinkedHashMap();

	public static void init() {
	}

	public static ProfessionFactory.Profession addProfession(String string, String string2, String string3, int int1) {
		ProfessionFactory.Profession profession = new ProfessionFactory.Profession(string, string2, string3, int1, "");
		ProfessionMap.put(string, profession);
		return profession;
	}

	public static ProfessionFactory.Profession getProfession(String string) {
		Iterator iterator = ProfessionMap.values().iterator();
		ProfessionFactory.Profession profession;
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			profession = (ProfessionFactory.Profession)iterator.next();
		} while (!profession.type.equals(string));

		return profession;
	}

	public static ArrayList getProfessions() {
		ArrayList arrayList = new ArrayList();
		Iterator iterator = ProfessionMap.values().iterator();
		while (iterator.hasNext()) {
			ProfessionFactory.Profession profession = (ProfessionFactory.Profession)iterator.next();
			arrayList.add(profession);
		}

		return arrayList;
	}

	public static void Reset() {
		ProfessionMap.clear();
	}

	public static class Profession implements IListBoxItem {
		public String type;
		public String name;
		public int cost;
		public String description;
		public String IconPath;
		public Texture texture = null;
		public Stack FreeTraitStack = new Stack();
		private List freeRecipes = new ArrayList();
		public HashMap XPBoostMap = new HashMap();

		public Profession(String string, String string2, String string3, int int1, String string4) {
			this.type = string;
			this.name = string2;
			this.IconPath = string3;
			if (!string3.equals("")) {
				this.texture = Texture.trygetTexture(string3);
			}

			this.cost = int1;
			this.description = string4;
		}

		public Texture getTexture() {
			return this.texture;
		}

		public void addFreeTrait(String string) {
			this.FreeTraitStack.add(string);
		}

		public ArrayList getFreeTraits() {
			ArrayList arrayList = new ArrayList();
			arrayList.addAll(this.FreeTraitStack);
			return arrayList;
		}

		public String getLabel() {
			return this.getName();
		}

		public String getIconPath() {
			return this.IconPath;
		}

		public String getLeftLabel() {
			return this.getName();
		}

		public String getRightLabel() {
			int int1 = this.getCost();
			if (int1 == 0) {
				return "";
			} else {
				String string = "+";
				if (int1 > 0) {
					string = "-";
				} else if (int1 == 0) {
					string = "";
				}

				if (int1 < 0) {
					int1 = -int1;
				}

				return string + int1;
			}
		}

		public String getType() {
			return this.type;
		}

		public void setType(String string) {
			this.type = string;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String string) {
			this.name = string;
		}

		public int getCost() {
			return this.cost;
		}

		public void setCost(int int1) {
			this.cost = int1;
		}

		public String getDescription() {
			return this.description;
		}

		public void setDescription(String string) {
			this.description = string;
		}

		public void setIconPath(String string) {
			this.IconPath = string;
		}

		public Stack getFreeTraitStack() {
			return this.FreeTraitStack;
		}

		public void addXPBoost(PerkFactory.Perk perk, int int1) {
			if (perk != null && perk != PerkFactory.Perks.None && perk != PerkFactory.Perks.MAX) {
				this.XPBoostMap.put(perk, int1);
			} else {
				DebugLog.General.warn("invalid perk passed to Profession.addXPBoost profession=%s perk=%s", this.name, perk);
			}
		}

		public HashMap getXPBoostMap() {
			return this.XPBoostMap;
		}

		public void setFreeTraitStack(Stack stack) {
			this.FreeTraitStack = stack;
		}

		public List getFreeRecipes() {
			return this.freeRecipes;
		}

		public void setFreeRecipes(List list) {
			this.freeRecipes = list;
		}
	}
}
