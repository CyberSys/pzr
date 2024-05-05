package zombie.characters.traits;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import zombie.util.Lambda;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;


public class TraitCollection {
	private final List m_activeTraitNames = new ArrayList();
	private final List m_traits = new ArrayList();

	public boolean remove(Object object) {
		return this.remove(String.valueOf(object));
	}

	public boolean remove(String string) {
		int int1 = this.indexOfTrait(string);
		if (int1 > -1) {
			this.deactivateTraitSlot(int1);
		}

		return int1 > -1;
	}

	public void addAll(Collection collection) {
		PZArrayUtil.forEach((Iterable)collection, this::add);
	}

	public void removeAll(Collection collection) {
		PZArrayUtil.forEach((Iterable)collection, this::remove);
	}

	public void clear() {
		PZArrayUtil.forEach(this.m_traits, (var0)->{
			var0.m_isSet = false;
		});
		this.m_activeTraitNames.clear();
	}

	public int size() {
		return this.m_activeTraitNames.size();
	}

	public boolean isEmpty() {
		return this.m_activeTraitNames.isEmpty();
	}

	public boolean contains(Object object) {
		return this.contains(String.valueOf(object));
	}

	public boolean contains(String string) {
		int int1 = this.indexOfTrait(string);
		return int1 > -1 && this.getSlotInternal(int1).m_isSet;
	}

	public void add(String string) {
		if (string != null) {
			this.getOrCreateSlotInternal(string).m_isSet = true;
			this.m_activeTraitNames.add(string);
		}
	}

	public String get(int int1) {
		return (String)this.m_activeTraitNames.get(int1);
	}

	public void set(String string, boolean boolean1) {
		if (boolean1) {
			this.add(string);
		} else {
			this.remove(string);
		}
	}

	public TraitCollection.TraitSlot getTraitSlot(String string) {
		return StringUtils.isNullOrWhitespace(string) ? null : this.getOrCreateSlotInternal(string);
	}

	private int indexOfTrait(String string) {
		return PZArrayUtil.indexOf(this.m_traits, Lambda.predicate(string, TraitCollection.TraitSlot::isName));
	}

	private TraitCollection.TraitSlot getSlotInternal(int int1) {
		return (TraitCollection.TraitSlot)this.m_traits.get(int1);
	}

	private TraitCollection.TraitSlot getOrCreateSlotInternal(String string) {
		int int1 = this.indexOfTrait(string);
		if (int1 == -1) {
			int1 = this.m_traits.size();
			this.m_traits.add(new TraitCollection.TraitSlot(string));
		}

		return this.getSlotInternal(int1);
	}

	private void deactivateTraitSlot(int int1) {
		TraitCollection.TraitSlot traitSlot = this.getSlotInternal(int1);
		traitSlot.m_isSet = false;
		int int2 = PZArrayUtil.indexOf(this.m_activeTraitNames, Lambda.predicate(traitSlot.Name, String::equalsIgnoreCase));
		if (int2 != -1) {
			this.m_activeTraitNames.remove(int2);
		}
	}

	public String toString() {
		return "TraitCollection(" + PZArrayUtil.arrayToString((Iterable)this.m_activeTraitNames, "", "", ", ") + ")";
	}

	public class TraitSlot {
		public final String Name;
		private boolean m_isSet;

		private TraitSlot(String string) {
			this.Name = string;
			this.m_isSet = false;
		}

		public boolean isName(String string) {
			return StringUtils.equalsIgnoreCase(this.Name, string);
		}

		public boolean isSet() {
			return this.m_isSet;
		}

		public void set(boolean boolean1) {
			if (this.m_isSet != boolean1) {
				TraitCollection.this.set(this.Name, boolean1);
			}
		}

		public String toString() {
			return "TraitSlot(" + this.Name + ":" + this.m_isSet + ")";
		}
	}
}
