package zombie.Quests.questactions;

import zombie.scripting.ScriptManager;

public class QuestAction_RunScript implements QuestAction {
   String Script;

   public QuestAction_RunScript(String var1) {
      this.Script = var1;
   }

   public void Execute() {
      ScriptManager.instance.PlayScript(this.Script);
   }
}
