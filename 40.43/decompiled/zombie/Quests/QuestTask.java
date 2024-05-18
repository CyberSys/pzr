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

   public QuestTask(QuestTaskType var1, String var2, String var3) {
      this.InternalTaskName = var2;
      this.TaskType = var1;
      this.TaskName = var3;
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
         Iterator var1 = this.OnCompleteActions.iterator();

         while(var1.hasNext()) {
            QuestAction var2 = (QuestAction)var1.next();
            var2.Execute();
         }
      }

      this.WasComplete = this.Complete;
   }
}
