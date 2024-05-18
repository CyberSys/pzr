package zombie.Quests;

import zombie.characters.IsoPlayer;
import zombie.iso.IsoWorld;
import zombie.ui.UIManager;


public class QuestTask_GotoLocation extends QuestTask {
	int Task_x;
	int Task_y;
	int Task_z;

	public QuestTask_GotoLocation(String string, String string2, int int1, int int2, int int3) {
		super(QuestTaskType.GotoLocation, string, string2);
		this.Task_x = int1;
		this.Task_y = int2;
		this.Task_z = int3;
	}

	public void Update() {
		if (!this.Unlocked) {
			this.Complete = false;
		} else {
			if (!this.Complete && IsoPlayer.getInstance().getCurrentSquare().getX() > this.Task_x - 2 && IsoPlayer.getInstance().getCurrentSquare().getX() < this.Task_x + 2 && IsoPlayer.getInstance().getCurrentSquare().getY() > this.Task_y - 2 && IsoPlayer.getInstance().getCurrentSquare().getY() < this.Task_y + 2 && IsoPlayer.getInstance().getCurrentSquare().getZ() == this.Task_z && IsoPlayer.getInstance().getCurrentSquare().getRoom() == IsoWorld.instance.CurrentCell.getGridSquare(this.Task_x, this.Task_y, this.Task_z).getRoom()) {
				this.Complete = true;
				if (UIManager.getOnscreenQuest() != null) {
					UIManager.getOnscreenQuest().TriggerQuestWiggle();
				}
			}

			super.Update();
		}
	}
}
