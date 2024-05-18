package zombie.Quests;

import zombie.characters.IsoPlayer;
import zombie.ui.UIManager;


public class QuestTask_EquipItem extends QuestTask {
	String QuestItemType;

	public QuestTask_EquipItem(String string, String string2, String string3) {
		super(QuestTaskType.FindItem, string, string2);
		this.QuestItemType = string3;
	}

	public void Update() {
		if (!this.Complete && IsoPlayer.getInstance().getPrimaryHandItem() != null && IsoPlayer.getInstance().getPrimaryHandItem().getType().equals(this.QuestItemType)) {
			this.Complete = true;
			if (UIManager.getOnscreenQuest() != null) {
				UIManager.getOnscreenQuest().TriggerQuestWiggle();
			}
		}

		super.Update();
	}
}
