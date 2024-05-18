package zombie.chat.defaultChats;

import java.nio.ByteBuffer;
import java.util.Iterator;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatBase;
import zombie.chat.ChatMessage;
import zombie.chat.ChatSettings;
import zombie.chat.ChatTab;
import zombie.chat.ChatUtility;
import zombie.core.Color;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.network.chat.ChatType;


public class GeneralChat extends ChatBase {
	private boolean discordEnabled = false;
	private final Color discordMessageColor = new Color(114, 137, 218);

	public GeneralChat(ByteBuffer byteBuffer, ChatTab chatTab, IsoPlayer player) {
		super(byteBuffer, ChatType.general, chatTab, player);
		if (!this.isCustomSettings()) {
			this.setSettings(getDefaultSettings());
		}
	}

	public GeneralChat(int int1, ChatTab chatTab, boolean boolean1) {
		super(int1, ChatType.general, chatTab);
		this.discordEnabled = boolean1;
		if (!this.isCustomSettings()) {
			this.setSettings(getDefaultSettings());
		}
	}

	public GeneralChat() {
		super(ChatType.general);
	}

	public static ChatSettings getDefaultSettings() {
		ChatSettings chatSettings = new ChatSettings();
		chatSettings.setBold(true);
		chatSettings.setFontColor(new Color(255, 165, 0));
		chatSettings.setShowAuthor(true);
		chatSettings.setShowChatTitle(true);
		chatSettings.setShowTimestamp(true);
		chatSettings.setUnique(true);
		chatSettings.setAllowColors(true);
		chatSettings.setAllowFonts(true);
		chatSettings.setAllowBBcode(true);
		return chatSettings;
	}

	public void sendMessageToChatMembers(ChatMessage chatMessage) {
		if (this.discordEnabled) {
			IsoPlayer player = ChatUtility.findPlayer(chatMessage.getAuthor());
			Iterator iterator;
			int int1;
			if (chatMessage.isFromDiscord()) {
				iterator = this.members.iterator();
				while (iterator.hasNext()) {
					int1 = (Integer)iterator.next();
					this.sendMessageToPlayer(int1, chatMessage);
				}
			} else {
				GameServer.discordBot.sendMessage(chatMessage.getAuthor(), chatMessage.getText());
				iterator = this.members.iterator();
				label28: while (true) {
					do {
						if (!iterator.hasNext()) {
							break label28;
						}

						int1 = (Integer)iterator.next();
					}			 while (player != null && player.getOnlineID() == int1);

					this.sendMessageToPlayer(int1, chatMessage);
				}
			}
		} else {
			super.sendMessageToChatMembers(chatMessage);
		}

		DebugLog.log("New message \'" + chatMessage + "\' was sent members of chat \'" + this.getID() + "\'");
	}

	public void sendToDiscordGeneralChatDisabled() {
		GameServer.discordBot.sendMessage("Server", Translator.getText("UI_chat_general_chat_disabled"));
	}

	public String getMessagePrefix(ChatMessage chatMessage) {
		StringBuilder stringBuilder = new StringBuilder();
		if (chatMessage.isFromDiscord()) {
			stringBuilder.append(this.getColorTag(this.discordMessageColor));
		} else {
			stringBuilder.append(this.getColorTag());
		}

		stringBuilder.append(" ").append(this.getFontSizeTag()).append(" ");
		if (this.isShowTimestamp()) {
			stringBuilder.append("[").append(LuaManager.getHourMinuteJava()).append("]");
		}

		if (this.isShowTitle()) {
			stringBuilder.append("[").append(this.getTitle()).append("]");
		}

		if (this.isShowAuthor()) {
			stringBuilder.append("[").append(chatMessage.getAuthor()).append("]");
		}

		stringBuilder.append(": ");
		return stringBuilder.toString();
	}

	public void packMessage(ByteBufferWriter byteBufferWriter, ChatMessage chatMessage) {
		super.packMessage(byteBufferWriter, chatMessage);
		byteBufferWriter.putBoolean(chatMessage.isFromDiscord());
	}

	public ChatMessage unpackMessage(ByteBuffer byteBuffer) {
		ChatMessage chatMessage = super.unpackMessage(byteBuffer);
		if (byteBuffer.get() == 1) {
			chatMessage.makeFromDiscord();
		}

		return chatMessage;
	}
}
