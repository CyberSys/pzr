package zombie.scripting.commands.quest;

import zombie.Quests.QuestCreator;
import zombie.scripting.commands.BaseCommand;

public class RunScriptOnComplete extends BaseCommand {
   String script;

   public void init(String var1, String[] var2) {
      if (var1 != null && var1.equals("Quest")) {
         this.script = var2[0].trim().replace("\"", "");
      }
   }

   public void begin() {
      if (this.script.contains(".")) {
         QuestCreator.AddQuestAction_RunScript(this.script);
      } else {
         QuestCreator.AddQuestAction_RunScript(this.module.name + "." + this.script);
      }

   }

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public boolean DoesInstantly() {
      return true;
   }
}
