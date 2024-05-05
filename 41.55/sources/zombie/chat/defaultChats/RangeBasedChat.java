package zombie.chat.defaultChats;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatBase;
import zombie.chat.ChatElement;
import zombie.chat.ChatMessage;
import zombie.chat.ChatMode;
import zombie.chat.ChatTab;
import zombie.chat.ChatUtility;
import zombie.core.Color;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.chat.ChatType;
import zombie.ui.UIFont;


public abstract class RangeBasedChat extends ChatBase {
	private static ChatElement overHeadChat = null;
	private static HashMap players = null;
	private static String currentPlayerName = null;
	String customTag = "default";

	RangeBasedChat(ByteBuffer byteBuffer, ChatType chatType, ChatTab chatTab, IsoPlayer player) {
		super(byteBuffer, chatType, chatTab, player);
	}

	RangeBasedChat(ChatType chatType) {
		super(chatType);
	}

	RangeBasedChat(int int1, ChatType chatType, ChatTab chatTab) {
		super(int1, chatType, chatTab);
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

	public ChatMessage createMessage(String string) {
		ChatMessage chatMessage = super.createMessage(string);
		if (this.getMode() == ChatMode.SinglePlayer) {
			chatMessage.setShowInChat(false);
		}

		chatMessage.setOverHeadSpeech(true);
		chatMessage.setShouldAttractZombies(true);
		return chatMessage;
	}

	public ChatMessage createBubbleMessage(String string) {
		ChatMessage chatMessage = super.createMessage(string);
		chatMessage.setOverHeadSpeech(true);
		chatMessage.setShowInChat(false);
		return chatMessage;
	}

	public void sendMessageToChatMembers(ChatMessage chatMessage) {
		IsoPlayer player = ChatUtility.findPlayer(chatMessage.getAuthor());
		if (this.getRange() == -1.0F) {
			String string = this.getTitle();
			DebugLog.log("Range not set for \'" + string + "\' chat. Message \'" + chatMessage.getText() + "\' ignored");
		} else {
			Iterator iterator = this.members.iterator();
			while (iterator.hasNext()) {
				int int1 = (Integer)iterator.next();
				IsoPlayer player2 = ChatUtility.findPlayer(int1);
				if (player2 != null && player.getOnlineID() != int1 && ChatUtility.getDistance(player, player2) < this.getRange()) {
					this.sendMessageToPlayer(int1, chatMessage);
				}
			}
		}
	}

	public void showMessage(ChatMessage chatMessage) {
		super.showMessage(chatMessage);
		if (chatMessage.isOverHeadSpeech()) {
			this.showInSpeechBubble(chatMessage);
		}
	}

	protected ChatElement getSpeechBubble() {
		return overHeadChat;
	}

	protected void showInSpeechBubble(ChatMessage chatMessage) {
		Color color = this.getColor();
		String string = chatMessage.getAuthor();
		IsoPlayer player = this.getPlayer(string);
		float float1 = color.r;
		float float2 = color.g;
		float float3 = color.b;
		if (player != null) {
			float1 = player.getSpeakColour().r;
			float2 = player.getSpeakColour().g;
			float3 = player.getSpeakColour().b;
		}

		String string2 = ChatUtility.parseStringForChatBubble(chatMessage.getText());
		if (string != null && !"".equalsIgnoreCase(string) && !string.equalsIgnoreCase(currentPlayerName)) {
			if (!players.containsKey(string)) {
				players.put(string, this.getPlayer(string));
			}

			IsoPlayer player2 = (IsoPlayer)players.get(string);
			if (player2.isDead()) {
				player2 = this.getPlayer(string);
				players.replace(string, player2);
			}

			player2.getChatElement().addChatLine(string2, float1, float2, float3, UIFont.Dialogue, this.getRange(), this.customTag, this.isAllowBBcode(), this.isAllowImages(), this.isAllowChatIcons(), this.isAllowColors(), this.isAllowFonts(), this.isEqualizeLineHeights());
		} else {
			overHeadChat.addChatLine(string2, float1, float2, float3, UIFont.Dialogue, this.getRange(), this.customTag, this.isAllowBBcode(), this.isAllowImages(), this.isAllowChatIcons(), this.isAllowColors(), this.isAllowFonts(), this.isEqualizeLineHeights());
		}
	}

	private IsoPlayer getPlayer(String string) {
		IsoPlayer player = GameClient.bClient ? GameClient.instance.getPlayerFromUsername(string) : null;
		if (player != null) {
			return player;
		} else {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				player = IsoPlayer.players[int1];
				if (player != null && player.getUsername().equals(string)) {
					return player;
				}
			}

			return null;
		}
	}

	static  {
		players = new HashMap();
	}
}
