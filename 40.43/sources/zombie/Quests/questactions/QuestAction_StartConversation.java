package zombie.Quests.questactions;

import zombie.characters.Talker;


public class QuestAction_StartConversation implements QuestAction {
	Talker A;
	Talker B;
	String Conversation;

	public QuestAction_StartConversation(String string, Talker talker, Talker talker2) {
		this.Conversation = string;
		this.A = talker;
		this.B = talker2;
	}

	public void Execute() {
	}
}
