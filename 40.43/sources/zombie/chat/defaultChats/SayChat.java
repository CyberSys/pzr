package zombie.chat.defaultChats;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatMessage;
import zombie.chat.ChatSettings;
import zombie.chat.ChatTab;
import zombie.core.Color;
import zombie.network.chat.ChatType;


public class SayChat extends RangeBasedChat {

	public SayChat(ByteBuffer byteBuffer, ChatTab chatTab, IsoPlayer player) {
		super(byteBuffer, ChatType.say, chatTab, player);
		if (!this.isCustomSettings()) {
			this.setSettings(getDefaultSettings());
		}
	}

	public SayChat(int int1, ChatTab chatTab) {
		super(int1, ChatType.say, chatTab);
		if (!this.isCustomSettings()) {
			this.setSettings(getDefaultSettings());
		}
	}

	public SayChat() {
		super(ChatType.say);
		this.setSettings(getDefaultSettings());
	}

	public static ChatSettings getDefaultSettings() {
		ChatSettings chatSettings = new ChatSettings();
		chatSettings.setBold(true);
		chatSettings.setFontColor(Color.white);
		chatSettings.setShowAuthor(true);
		chatSettings.setShowChatTitle(true);
		chatSettings.setShowTimestamp(true);
		chatSettings.setUnique(true);
		chatSettings.setAllowColors(false);
		chatSettings.setAllowFonts(false);
		chatSettings.setAllowBBcode(false);
		chatSettings.setEqualizeLineHeights(true);
		chatSettings.setRange(30.0F);
		chatSettings.setZombieAttractionRange(15.0F);
		return chatSettings;
	}

	public ChatMessage createInfoMessage(String string) {
		ChatMessage chatMessage = this.createBubbleMessage(string);
		chatMessage.setLocal(true);
		chatMessage.setShowInChat(false);
		return chatMessage;
	}

	public ChatMessage createCalloutMessage(String string) {
		ChatMessage chatMessage = this.createBubbleMessage(string);
		chatMessage.setLocal(false);
		chatMessage.setShouldAttractZombies(true);
		return chatMessage;
	}
}
