package zombie.inventory;

import java.util.ArrayList;
import zombie.SoundManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.skills.PerkFactory;
import zombie.core.Rand;
import zombie.inventory.types.DrainableComboItem;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Fixing;


public class FixingManager {

	public static ArrayList getFixes(InventoryItem inventoryItem) {
		ArrayList arrayList = new ArrayList();
		for (int int1 = 0; int1 < ScriptManager.instance.getAllFixing().size(); ++int1) {
			Fixing fixing = (Fixing)ScriptManager.instance.getAllFixing().get(int1);
			if (fixing.getRequiredItem().contains(inventoryItem.getType())) {
				arrayList.add(fixing);
			}
		}

		return arrayList;
	}

	public static InventoryItem fixItem(InventoryItem inventoryItem, IsoGameCharacter gameCharacter, Fixing fixing, Fixing.Fixer fixer) {
		if ((double)Rand.Next(100) >= getChanceOfFail(inventoryItem, gameCharacter, fixing, fixer)) {
			double double1 = getCondRepaired(inventoryItem, gameCharacter, fixing, fixer);
			int int1 = inventoryItem.getConditionMax() - inventoryItem.getCondition();
			Double Double1 = new Double((double)int1 * (double1 / 100.0));
			int int2 = (int)Math.round(Double1);
			if (int2 == 0) {
				int2 = 1;
			}

			inventoryItem.setCondition(inventoryItem.getCondition() + int2);
			inventoryItem.setHaveBeenRepaired(inventoryItem.getHaveBeenRepaired() + 1);
		} else if (inventoryItem.getCondition() > 0 && Rand.Next(5) == 0) {
			inventoryItem.setCondition(inventoryItem.getCondition() - 1);
			gameCharacter.getEmitter().playSound("FixingItemFailed");
		}

		useFixer(gameCharacter, fixer, inventoryItem);
		if (fixing.getGlobalItem() != null) {
			useFixer(gameCharacter, fixing.getGlobalItem(), inventoryItem);
		}

		addXp(gameCharacter, fixer);
		return inventoryItem;
	}

	private static void addXp(IsoGameCharacter gameCharacter, Fixing.Fixer fixer) {
		if (fixer.getFixerSkills() != null) {
			for (int int1 = 0; int1 < fixer.getFixerSkills().size(); ++int1) {
				Fixing.FixerSkill fixerSkill = (Fixing.FixerSkill)fixer.getFixerSkills().get(int1);
				gameCharacter.getXp().AddXP(PerkFactory.Perks.FromString(fixerSkill.getSkillName()), (float)Rand.Next(3, 6));
			}
		}
	}

	public static void useFixer(IsoGameCharacter gameCharacter, Fixing.Fixer fixer, InventoryItem inventoryItem) {
		int int1 = fixer.getNumberOfUse();
		for (int int2 = 0; int2 < gameCharacter.getInventory().getItems().size(); ++int2) {
			if (inventoryItem != gameCharacter.getInventory().getItems().get(int2)) {
				InventoryItem inventoryItem2 = (InventoryItem)gameCharacter.getInventory().getItems().get(int2);
				if (inventoryItem2 != null && inventoryItem2.getType().equals(fixer.getFixerName())) {
					if (!(inventoryItem2 instanceof DrainableComboItem)) {
						gameCharacter.getInventory().Remove(inventoryItem2);
						--int2;
						--int1;
					} else {
						if ("DuctTape".equals(inventoryItem2.getType()) || "Scotchtape".equals(inventoryItem2.getType())) {
							SoundManager.instance.PlaySound("FixWithTape", false, 1.0F);
						}

						int int3 = (int)Math.floor((double)(((DrainableComboItem)inventoryItem2).getUsedDelta() / ((DrainableComboItem)inventoryItem2).getUseDelta()));
						int int4 = Math.min(int3, int1);
						for (int int5 = 0; int5 < int4; ++int5) {
							inventoryItem2.Use();
							--int1;
							if (!gameCharacter.getInventory().getItems().contains(inventoryItem2)) {
								--int2;
								break;
							}
						}
					}
				}

				if (int1 == 0) {
					break;
				}
			}
		}
	}

	public static double getChanceOfFail(InventoryItem inventoryItem, IsoGameCharacter gameCharacter, Fixing fixing, Fixing.Fixer fixer) {
		double double1 = 3.0;
		if (fixer.getFixerSkills() != null) {
			for (int int1 = 0; int1 < fixer.getFixerSkills().size(); ++int1) {
				if (gameCharacter.getPerkLevel(PerkFactory.Perks.FromString(((Fixing.FixerSkill)fixer.getFixerSkills().get(int1)).getSkillName())) < ((Fixing.FixerSkill)fixer.getFixerSkills().get(int1)).getSkillLevel()) {
					double1 += (double)((((Fixing.FixerSkill)fixer.getFixerSkills().get(int1)).getSkillLevel() - gameCharacter.getPerkLevel(PerkFactory.Perks.FromString(((Fixing.FixerSkill)fixer.getFixerSkills().get(int1)).getSkillName()))) * 30);
				} else {
					double1 -= (double)((gameCharacter.getPerkLevel(PerkFactory.Perks.FromString(((Fixing.FixerSkill)fixer.getFixerSkills().get(int1)).getSkillName())) - ((Fixing.FixerSkill)fixer.getFixerSkills().get(int1)).getSkillLevel()) * 5);
				}
			}
		}

		double1 += (double)(inventoryItem.getHaveBeenRepaired() * 2);
		if (gameCharacter.HasTrait("Lucky")) {
			double1 -= 5.0;
		}

		if (gameCharacter.HasTrait("Unlucky")) {
			double1 += 5.0;
		}

		if (double1 > 100.0) {
			double1 = 100.0;
		}

		if (double1 < 0.0) {
			double1 = 0.0;
		}

		return double1;
	}

	public static double getCondRepaired(InventoryItem inventoryItem, IsoGameCharacter gameCharacter, Fixing fixing, Fixing.Fixer fixer) {
		double double1 = 0.0;
		switch (fixing.getFixers().indexOf(fixer)) {
		case 0: 
			double1 = 50.0 * (1.0 / (double)inventoryItem.getHaveBeenRepaired());
			break;
		
		case 1: 
			double1 = 20.0 * (1.0 / (double)inventoryItem.getHaveBeenRepaired());
			break;
		
		default: 
			double1 = 10.0 * (1.0 / (double)inventoryItem.getHaveBeenRepaired());
		
		}
		if (fixer.getFixerSkills() != null) {
			for (int int1 = 0; int1 < fixer.getFixerSkills().size(); ++int1) {
				Fixing.FixerSkill fixerSkill = (Fixing.FixerSkill)fixer.getFixerSkills().get(int1);
				int int2 = gameCharacter.getPerkLevel(PerkFactory.Perks.FromString(fixerSkill.getSkillName()));
				if (int2 > fixerSkill.getSkillLevel()) {
					double1 += (double)Math.min((int2 - fixerSkill.getSkillLevel()) * 5, 25);
				} else {
					double1 -= (double)((fixerSkill.getSkillLevel() - int2) * 15);
				}
			}
		}

		double1 *= (double)fixing.getConditionModifier();
		double1 = Math.max(0.0, double1);
		double1 = Math.min(100.0, double1);
		return double1;
	}
}
