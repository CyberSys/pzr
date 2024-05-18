package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.DrainableComboItem;


public class Fixing extends BaseScriptObject {
	private String name = null;
	private ArrayList require = null;
	private LinkedList fixers = new LinkedList();
	private Fixing.Fixer globalItem = null;
	private float conditionModifier = 1.0F;

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
							this.setGlobalItem(new Fixing.Fixer(string3.split("=")[0], (LinkedList)null, new Integer(string3.split("=")[1])));
						} else {
							this.setGlobalItem(new Fixing.Fixer(string3, (LinkedList)null, 1));
						}
					} else if (string2.equals("ConditionModifier")) {
						this.setConditionModifier(Float.parseFloat(string3.trim()));
					}
				} else if (!string3.contains(";")) {
					if (string3.contains("=")) {
						this.fixers.add(new Fixing.Fixer(string3.split("=")[0], (LinkedList)null, new Integer(string3.split("=")[1])));
					} else {
						this.fixers.add(new Fixing.Fixer(string3, (LinkedList)null, 1));
					}
				} else {
					LinkedList linkedList = new LinkedList();
					List list2 = Arrays.asList(string3.split(";"));
					for (int int3 = 1; int3 < list2.size(); ++int3) {
						linkedList.add(new Fixing.FixerSkill(((String)list2.get(int3)).trim().split("=")[0].trim(), new Integer(((String)list2.get(int3)).trim().split("=")[1].trim())));
					}

					if (string3.split(";")[0].trim().contains("=")) {
						this.fixers.add(new Fixing.Fixer(string3.split(";")[0].trim().split("=")[0], linkedList, new Integer(string3.split(";")[0].trim().split("=")[1])));
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

					if (drainableComboItem.getUsedDelta() / drainableComboItem.getUseDelta() >= (float)fixer.getNumberOfUse()) {
						return fixer;
					}
				} else if (gameCharacter.getInventory().getItemCount(this.getModule().getName() + "." + fixer.getFixerName()) >= fixer.getNumberOfUse()) {
					return fixer;
				}
			}
		}

		return null;
	}

	public InventoryItem haveGlobalItem(IsoGameCharacter gameCharacter) {
		for (int int1 = 0; int1 < gameCharacter.getInventory().getItems().size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)gameCharacter.getInventory().getItems().get(int1);
			if (this.getGlobalItem().getFixerName().equals(inventoryItem.getType()) && this.countUses(gameCharacter, this.getGlobalItem(), (InventoryItem)null) > this.getGlobalItem().getNumberOfUse()) {
				return inventoryItem;
			}
		}

		return null;
	}

	public InventoryItem haveThisFixer(IsoGameCharacter gameCharacter, Fixing.Fixer fixer, InventoryItem inventoryItem) {
		if (this.countUses(gameCharacter, fixer, inventoryItem) < fixer.getNumberOfUse()) {
			return null;
		} else {
			for (int int1 = 0; int1 < gameCharacter.getInventory().getItems().size(); ++int1) {
				InventoryItem inventoryItem2 = (InventoryItem)gameCharacter.getInventory().getItems().get(int1);
				if (inventoryItem2 != inventoryItem && fixer.getFixerName().equals(inventoryItem2.getType())) {
					return inventoryItem2;
				}
			}

			return null;
		}
	}

	public int countUses(IsoGameCharacter gameCharacter, Fixing.Fixer fixer, InventoryItem inventoryItem) {
		int int1 = 0;
		for (int int2 = 0; int2 < gameCharacter.getInventory().getItems().size(); ++int2) {
			InventoryItem inventoryItem2 = (InventoryItem)gameCharacter.getInventory().getItems().get(int2);
			if (inventoryItem2 != inventoryItem && fixer.getFixerName().equals(inventoryItem2.getType())) {
				if (inventoryItem2 instanceof DrainableComboItem) {
					DrainableComboItem drainableComboItem = (DrainableComboItem)inventoryItem2;
					int1 = (int)((double)int1 + Math.floor((double)(drainableComboItem.getUsedDelta() / drainableComboItem.getUseDelta())));
				} else {
					++int1;
				}
			}
		}

		return int1;
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

	public class FixerSkill {
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

	public class Fixer {
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
}
