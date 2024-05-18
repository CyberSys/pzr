package zombie.characters.traits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import zombie.characters.skills.PerkFactory;
import zombie.core.textures.Texture;
import zombie.interfaces.IListBoxItem;


public class TraitFactory {
	public static LinkedHashMap TraitMap = new LinkedHashMap();

	public static void init() {
	}

	public static void setMutualExclusive(String string, String string2) {
		((TraitFactory.Trait)TraitMap.get(string)).MutuallyExclusive.add(string2);
		((TraitFactory.Trait)TraitMap.get(string2)).MutuallyExclusive.add(string);
	}

	public static void sortList() {
		LinkedList linkedList = new LinkedList(TraitMap.entrySet());
		Collections.sort(linkedList, new Comparator(){
			
			public int compare(Entry linkedHashMap, Entry iterator) {
				return ((TraitFactory.Trait)linkedHashMap.getValue()).name.compareTo(((TraitFactory.Trait)iterator.getValue()).name);
			}
		});
		LinkedHashMap linkedHashMap = new LinkedHashMap();
		Iterator iterator = linkedList.iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			linkedHashMap.put(entry.getKey(), entry.getValue());
		}

		TraitMap = linkedHashMap;
	}

	public static TraitFactory.Trait addTrait(String string, String string2, int int1, String string3, boolean boolean1) {
		TraitFactory.Trait trait = new TraitFactory.Trait(string, string2, int1, string3, boolean1, false);
		TraitMap.put(string, trait);
		return trait;
	}

	public static TraitFactory.Trait addTrait(String string, String string2, int int1, String string3, boolean boolean1, boolean boolean2) {
		TraitFactory.Trait trait = new TraitFactory.Trait(string, string2, int1, string3, boolean1, boolean2);
		TraitMap.put(string, trait);
		return trait;
	}

	public static ArrayList getTraits() {
		ArrayList arrayList = new ArrayList();
		Iterator iterator = TraitMap.values().iterator();
		while (iterator.hasNext()) {
			TraitFactory.Trait trait = (TraitFactory.Trait)iterator.next();
			arrayList.add(trait);
		}

		return arrayList;
	}

	public static TraitFactory.Trait getTrait(String string) {
		return TraitMap.containsKey(string) ? (TraitFactory.Trait)TraitMap.get(string) : null;
	}

	public static void Reset() {
		TraitMap.clear();
	}

	public static class Trait implements IListBoxItem {
		public String traitID;
		public String name;
		public int cost;
		public String description;
		public boolean prof;
		public Texture texture = null;
		private boolean removeInMP = false;
		private List freeRecipes = new ArrayList();
		public ArrayList MutuallyExclusive = new ArrayList(0);
		public HashMap XPBoostMap = new HashMap();

		public void addXPBoost(PerkFactory.Perks perks, int int1) {
			this.XPBoostMap.put(perks, int1);
		}

		public List getFreeRecipes() {
			return this.freeRecipes;
		}

		public void setFreeRecipes(List list) {
			this.freeRecipes = list;
		}

		public Trait(String string, String string2, int int1, String string3, boolean boolean1, boolean boolean2) {
			this.traitID = string;
			this.name = string2;
			this.cost = int1;
			this.description = string3;
			this.prof = boolean1;
			this.texture = Texture.getSharedTexture("media/ui/Traits/trait_" + this.traitID.toLowerCase() + ".png");
			if (this.texture == null) {
				this.texture = Texture.getSharedTexture("media/ui/Traits/trait_generic.png");
			}

			this.removeInMP = boolean2;
		}

		public String getType() {
			return this.traitID;
		}

		public Texture getTexture() {
			return this.texture;
		}

		public String getLabel() {
			return this.name;
		}

		public String getLeftLabel() {
			return this.name;
		}

		public String getRightLabel() {
			int int1 = this.cost;
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

				return string + (new Integer(int1)).toString();
			}
		}

		public int getCost() {
			return this.cost;
		}

		public boolean isFree() {
			return this.prof;
		}

		public String getDescription() {
			return this.description;
		}

		public void setDescription(String string) {
			this.description = string;
		}

		public ArrayList getMutuallyExclusiveTraits() {
			return this.MutuallyExclusive;
		}

		public HashMap getXPBoostMap() {
			return this.XPBoostMap;
		}

		public boolean isRemoveInMP() {
			return this.removeInMP;
		}

		public void setRemoveInMP(boolean boolean1) {
			this.removeInMP = boolean1;
		}
	}
}
