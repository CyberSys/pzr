package zombie.Quests.questactions;

import zombie.characters.Talker;

public class QuestAction_StartConversation implements QuestAction {
   Talker A;
   Talker B;
   String Conversation;

   public QuestAction_StartConversation(String var1, Talker var2, Talker var3) {
      this.Conversation = var1;
      this.A = var2;
      this.B = var3;
   }

   public void Execute() {
   }
}
