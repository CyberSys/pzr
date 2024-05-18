package zombie.Quests;



public enum QuestTaskType {

	GotoLocation,
	TalkTo,
	FindItem,
	GiveItem,
	UseItemOn,
	Custom,
	MAX;

	public static QuestTaskType FromString(String string) {
		if (string.equals("GotoLocation")) {
			return GotoLocation;
		} else if (string.equals("TalkTo")) {
			return TalkTo;
		} else if (string.equals("FindItem")) {
			return FindItem;
		} else if (string.equals("GiveItem")) {
			return GiveItem;
		} else {
			return string.equals("UseItemOn") ? UseItemOn : MAX;
		}
	}
}
