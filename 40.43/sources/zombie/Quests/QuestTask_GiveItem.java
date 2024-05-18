package zombie.Quests;

import zombie.ui.UIManager;


public class QuestTask_GiveItem extends QuestTask {
	String CharacterName;
	String ItemName;

	public QuestTask_GiveItem(String string, String string2, String string3, String string4) {
		super(QuestTaskType.GiveItem, string, string2);
		this.ItemName = string3;
		this.CharacterName = string4;
	}

	public void Update() {
		if (!this.Unlocked) {
			this.Complete = false;
		} else {
			if (!this.Complete) {
				this.Complete = true;
				if (UIManager.getOnscreenQuest() != null) {
					UIManager.getOnscreenQuest().TriggerQuestWiggle();
				}
			}

			if (!this.Failed) {
				this.Failed = true;
				if (UIManager.getOnscreenQuest() != null) {
					UIManager.getOnscreenQuest().TriggerQuestWiggle();
				}
			}

			super.Update();
		}
	}
}
