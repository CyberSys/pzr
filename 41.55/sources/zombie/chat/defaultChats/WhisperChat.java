package zombie.chat.defaultChats;

import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatBase;
import zombie.chat.ChatMessage;
import zombie.chat.ChatSettings;
import zombie.chat.ChatTab;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.network.chat.ChatType;


public class WhisperChat extends ChatBase {
	private String myName;
	private String companionName;
	private final String player1;
	private final String player2;
	private boolean isInited = false;

	public WhisperChat(int int1, ChatTab chatTab, String string, String string2) {
		super(int1, ChatType.whisper, chatTab);
		if (!this.isCustomSettings()) {
			this.setSettings(getDefaultSettings());
		}

		this.player1 = string;
		this.player2 = string2;
	}

	public WhisperChat(ByteBuffer byteBuffer, ChatTab chatTab, IsoPlayer player) {
		super(byteBuffer, ChatType.whisper, chatTab, player);
		if (!this.isCustomSettings()) {
			this.setSettings(getDefaultSettings());
		}

		this.player1 = GameWindow.ReadString(byteBuffer);
		this.player2 = GameWindow.ReadString(byteBuffer);
	}

	public static ChatSettings getDefaultSettings() {
		ChatSettings chatSettings = new ChatSettings();
		chatSettings.setBold(true);
		chatSettings.setFontColor(new Color(85, 26, 139));
		chatSettings.setShowAuthor(true);
		chatSettings.setShowChatTitle(true);
		chatSettings.setShowTimestamp(true);
		chatSettings.setUnique(true);
		chatSettings.setAllowColors(true);
		chatSettings.setAllowFonts(true);
		chatSettings.setAllowBBcode(true);
		return chatSettings;
	}

	public String getMessagePrefix(ChatMessage chatMessage) {
		if (!this.isInited) {
			this.init();
		}

		StringBuilder stringBuilder = new StringBuilder(this.getChatSettingsTags());
		if (this.isShowTimestamp()) {
			stringBuilder.append("[").append(LuaManager.getHourMinuteJava()).append("]");
		}

		if (this.isShowTitle()) {
			stringBuilder.append("[").append(this.getTitle()).append("]");
		}

		if (!this.myName.equalsIgnoreCase(chatMessage.getAuthor())) {
			stringBuilder.append("[").append(this.companionName).append("]");
		} else {
			stringBuilder.append("[to ").append(this.companionName).append("]");
		}

		stringBuilder.append(": ");
		return stringBuilder.toString();
	}

	protected void packChat(ByteBufferWriter byteBufferWriter) {
		super.packChat(byteBufferWriter);
		byteBufferWriter.putUTF(this.player1);
		byteBufferWriter.putUTF(this.player2);
	}

	public String getCompanionName() {
		return this.companionName;
	}

	public void init() {
		if (this.player1.equals(IsoPlayer.getInstance().getUsername())) {
			this.myName = IsoPlayer.getInstance().getUsername();
			this.companionName = this.player2;
		} else {
			if (!this.player2.equals(IsoPlayer.getInstance().getUsername())) {
				if (Core.bDebug) {
					throw new RuntimeException("Wrong id");
				}

				DebugLog.log("Wrong id in whisper chat. Whisper chat not inited for players: " + this.player1 + " " + this.player2);
				return;
			}

			this.myName = IsoPlayer.getInstance().getUsername();
			this.companionName = this.player1;
		}

		this.isInited = true;
	}

	public static enum ChatStatus {

		None,
		Creating,
		PlayerNotFound;

		private static WhisperChat.ChatStatus[] $values() {
			return new WhisperChat.ChatStatus[]{None, Creating, PlayerNotFound};
		}
	}
}
