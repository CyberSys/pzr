package zombie.chat.defaultChats;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import zombie.WorldSoundManager;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatBase;
import zombie.chat.ChatElement;
import zombie.chat.ChatMessage;
import zombie.chat.ChatMode;
import zombie.chat.ChatTab;
import zombie.chat.ChatUtility;
import zombie.core.Color;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.chat.ChatType;
import zombie.ui.UIFont;

public abstract class RangeBasedChat extends ChatBase {
   private static ChatElement overHeadChat = null;
   private static HashMap players = null;
   private static String currentPlayerName = null;
   String customTag = "default";

   RangeBasedChat(ByteBuffer var1, ChatType var2, ChatTab var3, IsoPlayer var4) {
      super(var1, var2, var3, var4);
   }

   RangeBasedChat(ChatType var1) {
      super(var1);
   }

   RangeBasedChat(int var1, ChatType var2, ChatTab var3) {
      super(var1, var2, var3);
   }

   public void Init() {
      currentPlayerName = this.getChatOwnerName();
      if (players != null) {
         players.clear();
      }

      overHeadChat = this.getChatOwner().getChatElement();
   }

   public boolean isSendingToRadio() {
      return true;
   }

   public ChatMessage createMessage(String var1) {
      ChatMessage var2 = super.createMessage(var1);
      if (this.getMode() == ChatMode.SinglePlayer) {
         var2.setShowInChat(false);
      }

      var2.setOverHeadSpeech(true);
      var2.setShouldAttractZombies(true);
      return var2;
   }

   public ChatMessage createBubbleMessage(String var1) {
      ChatMessage var2 = super.createMessage(var1);
      var2.setOverHeadSpeech(true);
      var2.setShowInChat(false);
      return var2;
   }

   public void sendMessageToChatMembers(ChatMessage var1) {
      IsoPlayer var2 = ChatUtility.findPlayer(var1.getAuthor());
      if (this.getRange() == -1.0F) {
         DebugLog.log("Range not set for '" + this.getTitle() + "' chat. Message '" + var1.getText() + "' ignored");
      } else {
         Iterator var3 = this.members.iterator();

         while(var3.hasNext()) {
            int var4 = (Integer)var3.next();
            IsoPlayer var5 = ChatUtility.findPlayer(var4);
            if (var5 != null && var2.getOnlineID() != var4 && ChatUtility.getDistance(var2, var5) < this.getRange()) {
               this.sendMessageToPlayer(var4, var1);
            }
         }

      }
   }

   public void showMessage(ChatMessage var1) {
      super.showMessage(var1);
      if (var1.isOverHeadSpeech()) {
         this.showInSpeechBubble(var1);
      }

      if (var1.isShouldAttractZombies() && !this.getChatOwnerName().trim().isEmpty() && var1.getAuthor().equalsIgnoreCase(this.getChatOwnerName())) {
         IsoPlayer var2 = this.getChatOwner();
         int var3 = (int)this.getZombieAttractionRange();
         if ((float)var3 == -1.0F) {
            if (Core.bDebug) {
               throw new RuntimeException();
            }

            DebugLog.log("Range not set for " + this.getClass().getName());
         }

         WorldSoundManager.instance.addSound(var2, (int)var2.getX(), (int)var2.getY(), (int)var2.getZ(), var3, 50, false);
      }

   }

   protected ChatElement getSpeechBubble() {
      return overHeadChat;
   }

   protected void showInSpeechBubble(ChatMessage var1) {
      Color var2 = this.getColor();
      String var3 = var1.getAuthor();
      if (var3 != null && !"".equalsIgnoreCase(var3) && !var3.equalsIgnoreCase(currentPlayerName)) {
         if (!players.containsKey(var3)) {
            players.put(var3, this.getPlayer(var3));
         }

         IsoPlayer var4 = (IsoPlayer)players.get(var3);
         if (var4.isDead()) {
            var4 = this.getPlayer(var3);
            players.replace(var3, var4);
         }

         var4.getChatElement().addChatLine(var1.getText(), var2.r, var2.g, var2.b, UIFont.Dialogue, this.getRange(), this.customTag, this.isAllowBBcode(), this.isAllowImages(), this.isAllowChatIcons(), this.isAllowColors(), this.isAllowFonts(), this.isEqualizeLineHeights());
      } else {
         overHeadChat.addChatLine(var1.getText(), var2.r, var2.g, var2.b, UIFont.Dialogue, this.getRange(), this.customTag, this.isAllowBBcode(), this.isAllowImages(), this.isAllowChatIcons(), this.isAllowColors(), this.isAllowFonts(), this.isEqualizeLineHeights());
      }

   }

   private IsoPlayer getPlayer(String var1) {
      IsoPlayer var2 = GameClient.instance.getPlayerFromUsername(var1);
      if (var2 != null) {
         return var2;
      } else {
         for(int var3 = 0; var3 < IsoPlayer.players.length; ++var3) {
            if (IsoPlayer.players[var3].getUsername().equals(var1)) {
               return IsoPlayer.players[var3];
            }
         }

         return null;
      }
   }

   static {
      players = new HashMap();
   }
}
