package zombie.Quests;

import java.util.Iterator;
import java.util.Stack;
import zombie.Quests.questactions.QuestAction;


public class QuestTask implements Completable {
	public boolean Complete;
	public boolean Failed;
	public boolean Hidden = false;
	public Stack OnCompleteActions = new Stack();
	public QuestTaskType TaskType;
	public boolean Unlocked = false;
	public boolean WasComplete;
	String InternalTaskName = "Default Task Name";
	String TaskName = "Default Task Name";

	public QuestTask(QuestTaskType questTaskType, String string, String string2) {
		this.InternalTaskName = string;
		this.TaskType = questTaskType;
		this.TaskName = string2;
		this.Complete = false;
		this.Failed = false;
		this.WasComplete = false;
	}

	public String getInternalName() {
		return this.InternalTaskName;
	}

	public String getName() {
		return this.TaskName;
	}

	public boolean IsComplete() {
		return this.Complete;
	}

	public boolean IsFailed() {
		return this.Failed;
	}

	public void Update() {
		if (this.Complete && !this.WasComplete) {
			Iterator iterator = this.OnCompleteActions.iterator();
			while (iterator.hasNext()) {
				QuestAction questAction = (QuestAction)iterator.next();
				questAction.Execute();
			}
		}

		this.WasComplete = this.Complete;
	}
}
