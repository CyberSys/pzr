package zombie.scripting.commands.quest;

import zombie.Quests.QuestCreator;
import zombie.scripting.commands.BaseCommand;

public class UnlockLastButHide extends BaseCommand {
   String quest = null;

   public void init(String var1, String[] var2) {
      if (var1 == null || !var1.equals("Quest")) {
         ;
      }
   }

   public void begin() {
      QuestCreator.UnlockButHide();
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
