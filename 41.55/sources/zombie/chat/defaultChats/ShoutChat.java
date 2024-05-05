package zombie.chat.defaultChats;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatSettings;
import zombie.chat.ChatTab;
import zombie.core.Color;
import zombie.network.chat.ChatType;


public class ShoutChat extends RangeBasedChat {

	public ShoutChat(ByteBuffer byteBuffer, ChatTab chatTab, IsoPlayer player) {
		super(byteBuffer, ChatType.shout, chatTab, player);
		if (!this.isCustomSettings()) {
			this.setSettings(getDefaultSettings());
		}
	}

	public ShoutChat(int int1, ChatTab chatTab) {
		super(int1, ChatType.shout, chatTab);
		if (!this.isCustomSettings()) {
			this.setSettings(getDefaultSettings());
		}
	}

	public ShoutChat() {
		super(ChatType.shout);
		this.setSettings(getDefaultSettings());
	}

	public static ChatSettings getDefaultSettings() {
		ChatSettings chatSettings = new ChatSettings();
		chatSettings.setBold(true);
		chatSettings.setFontColor(new Color(255, 51, 51, 255));
		chatSettings.setShowAuthor(true);
		chatSettings.setShowChatTitle(true);
		chatSettings.setShowTimestamp(true);
		chatSettings.setUnique(true);
		chatSettings.setAllowColors(false);
		chatSettings.setAllowFonts(false);
		chatSettings.setAllowBBcode(false);
		chatSettings.setEqualizeLineHeights(true);
		chatSettings.setRange(60.0F);
		return chatSettings;
	}
}
