package zombie.Quests;

import zombie.scripting.objects.ScriptCharacter;
import zombie.ui.UIManager;


public class QuestTask_UseItemOn extends QuestTask {
	ScriptCharacter Character;
	String QuestItemType;

	public QuestTask_UseItemOn(String string, String string2, String string3, ScriptCharacter scriptCharacter) {
		super(QuestTaskType.UseItemOn, string, string2);
		this.QuestItemType = string3;
		this.Character = scriptCharacter;
	}

	public void Update() {
		if (!this.Unlocked) {
			this.Complete = false;
		} else {
			if (!this.Complete) {
				if (this.Character.Actual == null) {
					super.Update();
					return;
				}

				boolean boolean1 = false;
				for (int int1 = 0; int1 < this.Character.Actual.getUsedItemsOn().size(); ++int1) {
					if (((String)this.Character.Actual.getUsedItemsOn().get(int1)).equals(this.QuestItemType)) {
						boolean1 = true;
						break;
					}
				}

				if (boolean1) {
					this.Character.Actual.getUsedItemsOn().remove(this.QuestItemType);
					if (UIManager.getOnscreenQuest() != null) {
						UIManager.getOnscreenQuest().TriggerQuestWiggle();
					}

					if (UIManager.getOnscreenQuest() != null) {
						UIManager.getOnscreenQuest().TriggerQuestWiggle();
					}

					this.Complete = true;
				}
			}

			if (!this.Failed) {
				if (this.Character.Actual == null) {
					super.Update();
					return;
				}

				if (this.Character.Actual.getHealth() <= 0.0F) {
					this.Failed = true;
					if (UIManager.getOnscreenQuest() != null) {
						UIManager.getOnscreenQuest().TriggerQuestWiggle();
					}
				}
			}

			super.Update();
		}
	}
}
