package zombie.chat.defaultChats;

import java.nio.ByteBuffer;
import java.util.Iterator;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatBase;
import zombie.chat.ChatManager;
import zombie.chat.ChatMessage;
import zombie.chat.ChatSettings;
import zombie.chat.ChatTab;
import zombie.chat.ServerChatMessage;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.chat.ChatType;


public class ServerChat extends ChatBase {

	public ServerChat(ByteBuffer byteBuffer, ChatTab chatTab, IsoPlayer player) {
		super(byteBuffer, ChatType.server, chatTab, player);
		this.setSettings(getDefaultSettings());
	}

	public ServerChat(int int1, ChatTab chatTab) {
		super(int1, ChatType.server, chatTab);
		this.setSettings(getDefaultSettings());
	}

	public static ChatSettings getDefaultSettings() {
		ChatSettings chatSettings = new ChatSettings();
		chatSettings.setBold(true);
		chatSettings.setFontColor(new Color(0, 128, 255, 255));
		chatSettings.setShowAuthor(false);
		chatSettings.setShowChatTitle(true);
		chatSettings.setShowTimestamp(false);
		chatSettings.setAllowColors(true);
		chatSettings.setAllowFonts(false);
		chatSettings.setAllowBBcode(false);
		return chatSettings;
	}

	public ChatMessage createMessage(String string, String string2, boolean boolean1) {
		ChatMessage chatMessage = this.createMessage(string2);
		chatMessage.setAuthor(string);
		if (boolean1) {
			chatMessage.setServerAlert(true);
		}

		return chatMessage;
	}

	public ServerChatMessage createServerMessage(String string, boolean boolean1) {
		ServerChatMessage serverChatMessage = this.createServerMessage(string);
		serverChatMessage.setServerAlert(boolean1);
		return serverChatMessage;
	}

	public short getTabID() {
		return !GameClient.bClient ? super.getTabID() : ChatManager.getInstance().getFocusTab().getID();
	}

	public ChatMessage unpackMessage(ByteBuffer byteBuffer) {
		ChatMessage chatMessage = super.unpackMessage(byteBuffer);
		chatMessage.setServerAlert(byteBuffer.get() == 1);
		chatMessage.setServerAuthor(byteBuffer.get() == 1);
		return chatMessage;
	}

	public void packMessage(ByteBufferWriter byteBufferWriter, ChatMessage chatMessage) {
		super.packMessage(byteBufferWriter, chatMessage);
		byteBufferWriter.putBoolean(chatMessage.isServerAlert());
		byteBufferWriter.putBoolean(chatMessage.isServerAuthor());
	}

	public String getMessagePrefix(ChatMessage chatMessage) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.getChatSettingsTags());
		boolean boolean1 = false;
		if (this.isShowTitle()) {
			stringBuilder.append("[").append(this.getTitle()).append("]");
			boolean1 = true;
		}

		if (!chatMessage.isServerAuthor() && this.isShowAuthor()) {
			stringBuilder.append("[").append(chatMessage.getAuthor()).append("]");
			boolean1 = true;
		}

		if (boolean1) {
			stringBuilder.append(": ");
		}

		return stringBuilder.toString();
	}

	public String getMessageTextWithPrefix(ChatMessage chatMessage) {
		String string = this.getMessagePrefix(chatMessage);
		return string + " " + chatMessage.getText();
	}

	public void showMessage(ChatMessage chatMessage) {
		this.messages.add(chatMessage);
		if (this.isEnabled()) {
			LuaEventManager.triggerEvent("OnAddMessage", chatMessage, this.getTabID());
		}
	}

	public void sendMessageToChatMembers(ChatMessage chatMessage) {
		Iterator iterator = this.members.iterator();
		while (iterator.hasNext()) {
			short short1 = (Short)iterator.next();
			this.sendMessageToPlayer(short1, chatMessage);
		}

		if (Core.bDebug) {
			DebugLog.log("New message \'" + chatMessage + "\' was sent members of chat \'" + this.getID() + "\'");
		}
	}
}
