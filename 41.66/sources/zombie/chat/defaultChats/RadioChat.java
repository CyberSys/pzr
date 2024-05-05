package zombie.chat.defaultChats;

import java.nio.ByteBuffer;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatMessage;
import zombie.chat.ChatMode;
import zombie.chat.ChatSettings;
import zombie.chat.ChatTab;
import zombie.core.Color;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.network.chat.ChatType;
import zombie.radio.ZomboidRadio;
import zombie.radio.devices.DeviceData;
import zombie.ui.UIFont;


public class RadioChat extends RangeBasedChat {

	public RadioChat(ByteBuffer byteBuffer, ChatTab chatTab, IsoPlayer player) {
		super(byteBuffer, ChatType.radio, chatTab, player);
		if (!this.isCustomSettings()) {
			this.setSettings(getDefaultSettings());
		}

		this.customTag = "radio";
	}

	public RadioChat(int int1, ChatTab chatTab) {
		super(int1, ChatType.radio, chatTab);
		if (!this.isCustomSettings()) {
			this.setSettings(getDefaultSettings());
		}

		this.customTag = "radio";
	}

	public RadioChat() {
		super(ChatType.radio);
		this.setSettings(getDefaultSettings());
		this.customTag = "radio";
	}

	public static ChatSettings getDefaultSettings() {
		ChatSettings chatSettings = new ChatSettings();
		chatSettings.setBold(true);
		chatSettings.setFontColor(Color.lightGray);
		chatSettings.setShowAuthor(false);
		chatSettings.setShowChatTitle(true);
		chatSettings.setShowTimestamp(true);
		chatSettings.setUnique(true);
		chatSettings.setAllowColors(true);
		chatSettings.setAllowFonts(false);
		chatSettings.setAllowBBcode(true);
		chatSettings.setAllowImages(false);
		chatSettings.setAllowChatIcons(true);
		return chatSettings;
	}

	public ChatMessage createMessage(String string) {
		ChatMessage chatMessage = super.createMessage(string);
		if (this.getMode() == ChatMode.SinglePlayer) {
			chatMessage.setOverHeadSpeech(true);
			chatMessage.setShowInChat(false);
		}

		chatMessage.setShouldAttractZombies(true);
		return chatMessage;
	}

	public ChatMessage createBroadcastingMessage(String string, int int1) {
		ChatMessage chatMessage = super.createBubbleMessage(string);
		chatMessage.setAuthor("");
		chatMessage.setShouldAttractZombies(false);
		chatMessage.setRadioChannel(int1);
		return chatMessage;
	}

	public ChatMessage createStaticSoundMessage(String string) {
		ChatMessage chatMessage = super.createBubbleMessage(string);
		chatMessage.setAuthor("");
		chatMessage.setShouldAttractZombies(false);
		return chatMessage;
	}

	protected void showInSpeechBubble(ChatMessage chatMessage) {
		Color color = this.getColor();
		this.getSpeechBubble().addChatLine(chatMessage.getText(), color.r, color.g, color.b, UIFont.Dialogue, this.getRange(), this.customTag, this.isAllowBBcode(), this.isAllowImages(), this.isAllowChatIcons(), this.isAllowColors(), this.isAllowFonts(), this.isEqualizeLineHeights());
	}

	public void showMessage(ChatMessage chatMessage) {
		if (this.isEnabled() && chatMessage.isShowInChat() && this.hasChatTab()) {
			LuaEventManager.triggerEvent("OnAddMessage", chatMessage, this.getTabID());
		}
	}

	public void sendToServer(ChatMessage chatMessage, DeviceData deviceData) {
		if (deviceData != null) {
			int int1 = PZMath.fastfloor(this.getChatOwner().getX());
			int int2 = PZMath.fastfloor(this.getChatOwner().getY());
			int int3 = deviceData.getTransmitRange();
			ZomboidRadio.getInstance().SendTransmission(int1, int2, chatMessage, int3);
		}
	}

	public ChatMessage unpackMessage(ByteBuffer byteBuffer) {
		ChatMessage chatMessage = super.unpackMessage(byteBuffer);
		chatMessage.setRadioChannel(byteBuffer.getInt());
		chatMessage.setOverHeadSpeech(byteBuffer.get() == 1);
		chatMessage.setShowInChat(byteBuffer.get() == 1);
		chatMessage.setShouldAttractZombies(byteBuffer.get() == 1);
		return chatMessage;
	}

	public void packMessage(ByteBufferWriter byteBufferWriter, ChatMessage chatMessage) {
		super.packMessage(byteBufferWriter, chatMessage);
		byteBufferWriter.putInt(chatMessage.getRadioChannel());
		byteBufferWriter.putBoolean(chatMessage.isOverHeadSpeech());
		byteBufferWriter.putBoolean(chatMessage.isShowInChat());
		byteBufferWriter.putBoolean(chatMessage.isShouldAttractZombies());
	}

	public String getMessagePrefix(ChatMessage chatMessage) {
		StringBuilder stringBuilder = new StringBuilder(this.getChatSettingsTags());
		if (this.isShowTimestamp()) {
			stringBuilder.append("[").append(LuaManager.getHourMinuteJava()).append("]");
		}

		if (this.isShowTitle()) {
			stringBuilder.append("[").append(this.getTitle()).append("]");
		}

		if (this.isShowAuthor() && chatMessage.getAuthor() != null && !chatMessage.getAuthor().equals("")) {
			stringBuilder.append(" ").append(chatMessage.getAuthor()).append(" ");
		} else {
			stringBuilder.append(" ").append("Radio").append(" ");
		}

		stringBuilder.append(" (").append(this.getRadioChannelStr(chatMessage)).append("): ");
		return stringBuilder.toString();
	}

	private String getRadioChannelStr(ChatMessage chatMessage) {
		StringBuilder stringBuilder = new StringBuilder();
		int int1 = chatMessage.getRadioChannel();
		int int2;
		for (int2 = int1 % 1000; int2 % 10 == 0 && int2 != 0; int2 /= 10) {
		}

		int int3 = int1 / 1000;
		stringBuilder.append(int3).append(".").append(int2).append(" MHz");
		return stringBuilder.toString();
	}
}
