package zombie.Quests;

import zombie.characters.IsoPlayer;
import zombie.inventory.InventoryItem;
import zombie.ui.UIManager;


public class QuestTask_FindItem extends QuestTask {
	int QuestItemRequiredAmmount;
	String QuestItemType;

	public QuestTask_FindItem(String string, String string2, String string3, int int1) {
		super(QuestTaskType.FindItem, string, string2);
		this.QuestItemType = string3;
		this.QuestItemRequiredAmmount = int1;
	}

	public void Update() {
		if (!this.Unlocked) {
			this.Complete = false;
		} else {
			if (!this.Complete) {
				int int1 = 0;
				for (int int2 = 0; int2 < IsoPlayer.getInstance().getInventory().Items.size(); ++int2) {
					if (((InventoryItem)IsoPlayer.getInstance().getInventory().Items.get(int2)).getType().equals(this.QuestItemType)) {
						int1 += ((InventoryItem)IsoPlayer.getInstance().getInventory().Items.get(int2)).getUses();
					}
				}

				if (int1 >= this.QuestItemRequiredAmmount) {
					this.Complete = true;
					if (UIManager.getOnscreenQuest() != null) {
						UIManager.getOnscreenQuest().TriggerQuestWiggle();
					}
				}
			}

			super.Update();
		}
	}
}
