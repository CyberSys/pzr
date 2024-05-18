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
			DebugLog.log("Range not set for \'" + this.getTitle() + "\' chat. Message \'" + chatMessage.getText() + "\' ignored");
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

		if (chatMessage.isShouldAttractZombies() && !this.getChatOwnerName().trim().isEmpty() && chatMessage.getAuthor().equalsIgnoreCase(this.getChatOwnerName())) {
			IsoPlayer player = this.getChatOwner();
			int int1 = (int)this.getZombieAttractionRange();
			if ((float)int1 == -1.0F) {
				if (Core.bDebug) {
					throw new RuntimeException();
				}

				DebugLog.log("Range not set for " + this.getClass().getName());
			}

			WorldSoundManager.instance.addSound(player, (int)player.getX(), (int)player.getY(), (int)player.getZ(), int1, 50, false);
		}
	}

	protected ChatElement getSpeechBubble() {
		return overHeadChat;
	}

	protected void showInSpeechBubble(ChatMessage chatMessage) {
		Color color = this.getColor();
		String string = chatMessage.getAuthor();
		if (string != null && !"".equalsIgnoreCase(string) && !string.equalsIgnoreCase(currentPlayerName)) {
			if (!players.containsKey(string)) {
				players.put(string, this.getPlayer(string));
			}

			IsoPlayer player = (IsoPlayer)players.get(string);
			if (player.isDead()) {
				player = this.getPlayer(string);
				players.replace(string, player);
			}

			player.getChatElement().addChatLine(chatMessage.getText(), color.r, color.g, color.b, UIFont.Dialogue, this.getRange(), this.customTag, this.isAllowBBcode(), this.isAllowImages(), this.isAllowChatIcons(), this.isAllowColors(), this.isAllowFonts(), this.isEqualizeLineHeights());
		} else {
			overHeadChat.addChatLine(chatMessage.getText(), color.r, color.g, color.b, UIFont.Dialogue, this.getRange(), this.customTag, this.isAllowBBcode(), this.isAllowImages(), this.isAllowChatIcons(), this.isAllowColors(), this.isAllowFonts(), this.isEqualizeLineHeights());
		}
	}

	private IsoPlayer getPlayer(String string) {
		IsoPlayer player = GameClient.instance.getPlayerFromUsername(string);
		if (player != null) {
			return player;
		} else {
			for (int int1 = 0; int1 < IsoPlayer.players.length; ++int1) {
				if (IsoPlayer.players[int1].getUsername().equals(string)) {
					return IsoPlayer.players[int1];
				}
			}

			return null;
		}
	}

	static  {
		players = new HashMap();
	}
}
