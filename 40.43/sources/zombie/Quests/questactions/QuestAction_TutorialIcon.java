package zombie.Quests.questactions;

import zombie.iso.IsoObject;
import zombie.ui.UIElement;
import zombie.ui.UIManager;


public class QuestAction_TutorialIcon implements QuestAction {
	boolean bAutoExpand;
	UIElement Parent;
	private String message;
	private IsoObject obj;
	private String title;
	private final int x;
	private final int y;
	private final float yoff;

	public QuestAction_TutorialIcon(String string, String string2, IsoObject object, boolean boolean1, float float1) {
		this.bAutoExpand = boolean1;
		this.obj = object;
		this.message = string2;
		this.title = string;
		this.x = 0;
		this.y = 0;
		this.yoff = float1;
	}

	public QuestAction_TutorialIcon(UIElement uIElement, int int1, int int2, String string, String string2, boolean boolean1) {
		this.bAutoExpand = boolean1;
		this.message = string2;
		this.title = string;
		this.yoff = 0.0F;
		this.Parent = uIElement;
		this.x = int1;
		this.y = int2;
	}

	public void Execute() {
		if (this.Parent == null) {
			UIManager.AddTutorial((float)this.obj.square.getX(), (float)this.obj.square.getY(), (float)this.obj.square.getZ(), this.title, this.message, this.bAutoExpand, this.yoff);
		} else {
			UIManager.AddTutorial(this.Parent, (double)this.x, (double)this.y, this.title, this.message, this.bAutoExpand);
		}
	}
}
