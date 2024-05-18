package zombie.Quests;

import java.util.Iterator;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.iso.IsoMovingObject;
import zombie.ui.TutorialManager;
import zombie.ui.UIManager;


public class QuestTask_ArbitaryAction extends QuestTask {
	String ArbAction;

	public QuestTask_ArbitaryAction(String string, String string2, String string3) {
		super(QuestTaskType.Custom, string, string2);
		this.ArbAction = string3;
	}

	public void Update() {
		if (!this.Complete && this.ArbActionCheck()) {
			this.Complete = true;
			if (UIManager.getOnscreenQuest() != null) {
				UIManager.getOnscreenQuest().TriggerQuestWiggle();
			}
		}

		super.Update();
	}

	private boolean ArbActionCheck() {
		if (this.ArbAction.equals("barricadeTutorial")) {
			return TutorialManager.instance.BarricadeCount >= 7;
		} else {
			if (this.ArbAction.equals("spotzombie")) {
				Iterator iterator = IsoPlayer.getInstance().getSpottedList().iterator();
				while (iterator.hasNext()) {
					IsoMovingObject movingObject = (IsoMovingObject)iterator.next();
					if (movingObject instanceof IsoZombie && movingObject.alpha[IsoPlayer.getPlayerIndex()] > 0.5F) {
						return true;
					}
				}
			}

			if (this.ArbAction.equals("killzombie") && IsoPlayer.getInstance().getZombieKills() > IsoPlayer.getInstance().getLastZombieKills()) {
				return true;
			} else if (this.ArbAction.equals("PlayerOutside") && IsoPlayer.getInstance().getCurrentSquare().getRoom() == null) {
				return true;
			} else {
				return this.ArbAction.equals("tutSoupInStove") && TutorialManager.instance.tutorialStove.container.contains("PotOfSoup") && TutorialManager.instance.tutorialStove.Activated();
			}
		}
	}
}
