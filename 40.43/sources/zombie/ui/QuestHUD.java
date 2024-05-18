package zombie.ui;

import java.util.Iterator;
import zombie.Quests.Quest;
import zombie.Quests.QuestManager;
import zombie.Quests.QuestTask;
import zombie.core.Core;


public class QuestHUD extends UIElement implements UIEventHandler {
	private boolean FirstQuestSet = false;
	private float QuestOscilationLevel = 0.0F;
	private float QuestOscilator = 0.0F;
	private float QuestOscilatorDecelerator = 0.93F;
	private float QuestOscilatorRate = 0.8F;
	private float QuestOscilatorScalar = 15.6F;
	private float QuestOscilatorStartLevel = 1.0F;
	private float QuestOscilatorStep = 0.0F;
	private float QuestDefaultXOffset = 0.0F;
	DialogButton QuestPanelButton = null;

	public QuestHUD() {
		this.QuestPanelButton = new DialogButton(this, 222.0F, 50.0F, "Quest Manager", "Quest Manager");
		this.AddChild(this.QuestPanelButton);
		this.FirstQuestSet = false;
	}

	public void TriggerQuestWiggle() {
		this.QuestOscilationLevel = this.QuestOscilatorStartLevel;
	}

	public void render() {
		if (this.QuestPanelButton.clicked) {
			UIManager.questPanel.setVisible(!UIManager.questPanel.isVisible());
			this.QuestPanelButton.clicked = false;
			UIManager.questPanel.setX((double)(Core.getInstance().getScreenWidth() - 463));
			UIManager.questPanel.setY(66.0);
		}

		this.QuestOscilatorStep += this.QuestOscilatorRate;
		this.QuestOscilator = (float)Math.sin((double)this.QuestOscilatorStep);
		float float1 = this.QuestOscilator * this.QuestOscilatorScalar * this.QuestOscilationLevel;
		this.QuestOscilationLevel *= this.QuestOscilatorDecelerator;
		String string = "";
		String string2 = "";
		super.render();
		Iterator iterator;
		if (QuestPanel.instance.ActiveQuest != null && !QuestPanel.instance.ActiveQuest.Failed && !QuestPanel.instance.ActiveQuest.Complete) {
			if (QuestPanel.instance.ActiveQuest != null) {
				iterator = QuestPanel.instance.ActiveQuest.QuestTaskStack.iterator();
				while (iterator.hasNext()) {
					QuestTask questTask = (QuestTask)iterator.next();
					if (questTask.Unlocked && !questTask.Failed && !questTask.Complete && !questTask.Hidden) {
						string = QuestPanel.instance.ActiveQuest.getName();
						string2 = questTask.getName();
						this.DrawTextRight(UIFont.Medium, string, this.getWidth(), 0.0, 1.0, 1.0, 1.0, 1.0);
						this.DrawTextRight(UIFont.Small, string2, this.getWidth() - 3.0 + (double)((int)float1), 19.0, 1.0, 1.0, 1.0, 1.0);
						return;
					}
				}
			}
		} else {
			QuestPanel.instance.ActiveQuest = null;
			this.FirstQuestSet = false;
			iterator = QuestManager.instance.QuestStack.iterator();
			while (iterator.hasNext()) {
				Quest quest = (Quest)iterator.next();
				if (quest.Unlocked && !quest.Complete && !quest.Failed && !this.FirstQuestSet) {
					this.FirstQuestSet = true;
					QuestPanel.instance.SetActiveQuest(quest);
				}
			}
		}
	}

	public void DoubleClick(String string, int int1, int int2) {
	}

	public void Selected(String string, int int1, int int2) {
	}

	public void ModalClick(String string, String string2) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
