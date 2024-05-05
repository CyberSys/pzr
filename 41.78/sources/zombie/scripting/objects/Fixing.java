package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.DrainableComboItem;
import zombie.util.Type;


public final class Fixing extends BaseScriptObject {
	private String name = null;
	private ArrayList require = null;
	private final LinkedList fixers = new LinkedList();
	private Fixing.Fixer globalItem = null;
	private float conditionModifier = 1.0F;
	private static final Fixing.PredicateRequired s_PredicateRequired = new Fixing.PredicateRequired();
	private static final ArrayList s_InventoryItems = new ArrayList();

	public void Load(String string, String[] stringArray) {
		this.setName(string);
		for (int int1 = 0; int1 < stringArray.length; ++int1) {
			if (!stringArray[int1].trim().isEmpty() && stringArray[int1].contains(":")) {
				String[] stringArray2 = stringArray[int1].split(":");
				String string2 = stringArray2[0].trim();
				String string3 = stringArray2[1].trim();
				if (string2.equals("Require")) {
					List list = Arrays.asList(string3.split(";"));
					for (int int2 = 0; int2 < list.size(); ++int2) {
						this.addRequiredItem(((String)list.get(int2)).trim());
					}
				} else if (!string2.equals("Fixer")) {
					if (string2.equals("GlobalItem")) {
						if (string3.contains("=")) {
							this.setGlobalItem(new Fixing.Fixer(string3.split("=")[0], (LinkedList)null, Integer.parseInt(string3.split("=")[1])));
						} else {
							this.setGlobalItem(new Fixing.Fixer(string3, (LinkedList)null, 1));
						}
					} else if (string2.equals("ConditionModifier")) {
						this.setConditionModifier(Float.parseFloat(string3.trim()));
					}
				} else if (!string3.contains(";")) {
					if (string3.contains("=")) {
						this.fixers.add(new Fixing.Fixer(string3.split("=")[0], (LinkedList)null, Integer.parseInt(string3.split("=")[1])));
					} else {
						this.fixers.add(new Fixing.Fixer(string3, (LinkedList)null, 1));
					}
				} else {
					LinkedList linkedList = new LinkedList();
					List list2 = Arrays.asList(string3.split(";"));
					for (int int3 = 1; int3 < list2.size(); ++int3) {
						String[] stringArray3 = ((String)list2.get(int3)).trim().split("=");
						linkedList.add(new Fixing.FixerSkill(stringArray3[0].trim(), Integer.parseInt(stringArray3[1].trim())));
					}

					if (string3.split(";")[0].trim().contains("=")) {
						String[] stringArray4 = string3.split(";")[0].trim().split("=");
						this.fixers.add(new Fixing.Fixer(stringArray4[0], linkedList, Integer.parseInt(stringArray4[1])));
					} else {
						this.fixers.add(new Fixing.Fixer(string3.split(";")[0].trim(), linkedList, 1));
					}
				}
			}
		}
	}

	public String getName() {
		return this.name;
	}

	public void setName(String string) {
		this.name = string;
	}

	public ArrayList getRequiredItem() {
		return this.require;
	}

	public void addRequiredItem(String string) {
		if (this.require == null) {
			this.require = new ArrayList();
		}

		this.require.add(string);
	}

	public LinkedList getFixers() {
		return this.fixers;
	}

	public Fixing.Fixer usedInFixer(InventoryItem inventoryItem, IsoGameCharacter gameCharacter) {
		for (int int1 = 0; int1 < this.getFixers().size(); ++int1) {
			Fixing.Fixer fixer = (Fixing.Fixer)this.getFixers().get(int1);
			if (fixer.getFixerName().equals(inventoryItem.getType())) {
				if (inventoryItem instanceof DrainableComboItem) {
					DrainableComboItem drainableComboItem = (DrainableComboItem)inventoryItem;
					if (!(drainableComboItem.getUsedDelta() < 1.0F)) {
						return fixer;
					}

					if (drainableComboItem.getDrainableUsesInt() >= fixer.getNumberOfUse()) {
						return fixer;
					}
				} else {
					ItemContainer itemContainer = gameCharacter.getInventory();
					String string = this.getModule().getName();
					if (itemContainer.getCountTypeRecurse(string + "." + fixer.getFixerName()) >= fixer.getNumberOfUse()) {
						return fixer;
					}
				}
			}
		}

		return null;
	}

	public InventoryItem haveGlobalItem(IsoGameCharacter gameCharacter) {
		s_InventoryItems.clear();
		ArrayList arrayList = this.getRequiredFixerItems(gameCharacter, this.getGlobalItem(), (InventoryItem)null, s_InventoryItems);
		return arrayList == null ? null : (InventoryItem)arrayList.get(0);
	}

	public InventoryItem haveThisFixer(IsoGameCharacter gameCharacter, Fixing.Fixer fixer, InventoryItem inventoryItem) {
		s_InventoryItems.clear();
		ArrayList arrayList = this.getRequiredFixerItems(gameCharacter, fixer, inventoryItem, s_InventoryItems);
		return arrayList == null ? null : (InventoryItem)arrayList.get(0);
	}

	public int countUses(IsoGameCharacter gameCharacter, Fixing.Fixer fixer, InventoryItem inventoryItem) {
		s_InventoryItems.clear();
		s_PredicateRequired.uses = 0;
		this.getRequiredFixerItems(gameCharacter, fixer, inventoryItem, s_InventoryItems);
		return s_PredicateRequired.uses;
	}

	private static int countUses(InventoryItem inventoryItem) {
		DrainableComboItem drainableComboItem = (DrainableComboItem)Type.tryCastTo(inventoryItem, DrainableComboItem.class);
		return drainableComboItem != null ? drainableComboItem.getDrainableUsesInt() : 1;
	}

	public ArrayList getRequiredFixerItems(IsoGameCharacter gameCharacter, Fixing.Fixer fixer, InventoryItem inventoryItem, ArrayList arrayList) {
		if (fixer == null) {
			return null;
		} else {
			assert Thread.currentThread() == GameWindow.GameThread;
			Fixing.PredicateRequired predicateRequired = s_PredicateRequired;
			predicateRequired.fixer = fixer;
			predicateRequired.brokenItem = inventoryItem;
			predicateRequired.uses = 0;
			gameCharacter.getInventory().getAllRecurse(predicateRequired, arrayList);
			return predicateRequired.uses >= fixer.getNumberOfUse() ? arrayList : null;
		}
	}

	public ArrayList getRequiredItems(IsoGameCharacter gameCharacter, Fixing.Fixer fixer, InventoryItem inventoryItem) {
		ArrayList arrayList = new ArrayList();
		if (this.getRequiredFixerItems(gameCharacter, fixer, inventoryItem, arrayList) == null) {
			arrayList.clear();
			return null;
		} else if (this.getGlobalItem() != null && this.getRequiredFixerItems(gameCharacter, this.getGlobalItem(), inventoryItem, arrayList) == null) {
			arrayList.clear();
			return null;
		} else {
			return arrayList;
		}
	}

	public Fixing.Fixer getGlobalItem() {
		return this.globalItem;
	}

	public void setGlobalItem(Fixing.Fixer fixer) {
		this.globalItem = fixer;
	}

	public float getConditionModifier() {
		return this.conditionModifier;
	}

	public void setConditionModifier(float float1) {
		this.conditionModifier = float1;
	}

	public static final class Fixer {
		private String fixerName = null;
		private LinkedList skills = null;
		private int numberOfUse = 1;

		public Fixer(String string, LinkedList linkedList, int int1) {
			this.fixerName = string;
			this.skills = linkedList;
			this.numberOfUse = int1;
		}

		public String getFixerName() {
			return this.fixerName;
		}

		public LinkedList getFixerSkills() {
			return this.skills;
		}

		public int getNumberOfUse() {
			return this.numberOfUse;
		}
	}

	public static final class FixerSkill {
		private String skillName = null;
		private int skillLvl = 0;

		public FixerSkill(String string, int int1) {
			this.skillName = string;
			this.skillLvl = int1;
		}

		public String getSkillName() {
			return this.skillName;
		}

		public int getSkillLevel() {
			return this.skillLvl;
		}
	}

	private static final class PredicateRequired implements Predicate {
		Fixing.Fixer fixer;
		InventoryItem brokenItem;
		int uses;

		public boolean test(InventoryItem inventoryItem) {
			if (this.uses >= this.fixer.getNumberOfUse()) {
				return false;
			} else if (inventoryItem == this.brokenItem) {
				return false;
			} else if (!this.fixer.getFixerName().equals(inventoryItem.getType())) {
				return false;
			} else {
				int int1 = Fixing.countUses(inventoryItem);
				if (int1 > 0) {
					this.uses += int1;
					return true;
				} else {
					return false;
				}
			}
		}
	}
}
