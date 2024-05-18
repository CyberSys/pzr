package zombie.Quests;

public enum QuestTaskType {
   GotoLocation,
   TalkTo,
   FindItem,
   GiveItem,
   UseItemOn,
   Custom,
   MAX;

   public static QuestTaskType FromString(String var0) {
      if (var0.equals("GotoLocation")) {
         return GotoLocation;
      } else if (var0.equals("TalkTo")) {
         return TalkTo;
      } else if (var0.equals("FindItem")) {
         return FindItem;
      } else if (var0.equals("GiveItem")) {
         return GiveItem;
      } else {
         return var0.equals("UseItemOn") ? UseItemOn : MAX;
      }
   }
}
