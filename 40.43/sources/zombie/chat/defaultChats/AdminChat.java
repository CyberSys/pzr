package zombie.chat.defaultChats;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatBase;
import zombie.chat.ChatSettings;
import zombie.chat.ChatTab;
import zombie.core.Color;
import zombie.network.chat.ChatType;


public class AdminChat extends ChatBase {

	public AdminChat(ByteBuffer byteBuffer, ChatTab chatTab, IsoPlayer player) {
		super(byteBuffer, ChatType.admin, chatTab, player);
		if (!this.isCustomSettings()) {
			this.setSettings(getDefaultSettings());
		}
	}

	public AdminChat(int int1, ChatTab chatTab) {
		super(int1, ChatType.admin, chatTab);
		super.setSettings(getDefaultSettings());
	}

	public AdminChat() {
		super(ChatType.admin);
	}

	public static ChatSettings getDefaultSettings() {
		ChatSettings chatSettings = new ChatSettings();
		chatSettings.setBold(true);
		chatSettings.setFontColor(Color.white);
		chatSettings.setShowAuthor(true);
		chatSettings.setShowChatTitle(true);
		chatSettings.setShowTimestamp(true);
		chatSettings.setUnique(true);
		chatSettings.setAllowColors(true);
		chatSettings.setAllowFonts(true);
		chatSettings.setAllowBBcode(true);
		return chatSettings;
	}
}
