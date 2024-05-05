package zombie.chat;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.network.chat.ChatType;


public abstract class ChatBase {
	private static final int ID_NOT_SET = -29048394;
	private int id;
	private final String titleID;
	private final ChatType type;
	private ChatSettings settings;
	private boolean customSettings;
	private ChatTab chatTab;
	private String translatedTitle;
	protected final ArrayList members;
	private final ArrayList justAddedMembers;
	private final ArrayList justRemovedMembers;
	protected final ArrayList messages;
	private UdpConnection serverConnection;
	private ChatMode mode;
	private IsoPlayer chatOwner;
	private final Lock memberLock;

	protected ChatBase(ChatType chatType) {
		this.customSettings = false;
		this.chatTab = null;
		this.justAddedMembers = new ArrayList();
		this.justRemovedMembers = new ArrayList();
		this.memberLock = new ReentrantLock();
		this.settings = new ChatSettings();
		this.customSettings = false;
		this.messages = new ArrayList();
		this.id = -29048394;
		this.titleID = chatType.getTitleID();
		this.type = chatType;
		this.members = new ArrayList();
		this.mode = ChatMode.SinglePlayer;
		this.serverConnection = null;
		this.chatOwner = IsoPlayer.getInstance();
	}

	public ChatBase(ByteBuffer byteBuffer, ChatType chatType, ChatTab chatTab, IsoPlayer player) {
		this(chatType);
		this.id = byteBuffer.getInt();
		this.customSettings = byteBuffer.get() == 1;
		if (this.customSettings) {
			this.settings = new ChatSettings(byteBuffer);
		}

		this.chatTab = chatTab;
		this.mode = ChatMode.ClientMultiPlayer;
		this.serverConnection = GameClient.connection;
		this.chatOwner = player;
	}

	public ChatBase(int int1, ChatType chatType, ChatTab chatTab) {
		this(chatType);
		this.id = int1;
		this.chatTab = chatTab;
		this.mode = ChatMode.ServerMultiPlayer;
	}

	public boolean isEnabled() {
		return ChatUtility.chatStreamEnabled(this.type);
	}

	protected String getChatOwnerName() {
		if (this.chatOwner == null) {
			if (this.mode != ChatMode.ServerMultiPlayer) {
				if (Core.bDebug) {
					throw new NullPointerException("chat owner is null but name quired");
				}

				DebugLog.log("chat owner is null but name quired. Chat: " + this.getType());
			}

			return "";
		} else {
			return this.chatOwner.username;
		}
	}

	protected IsoPlayer getChatOwner() {
		if (this.chatOwner == null && this.mode != ChatMode.ServerMultiPlayer) {
			if (Core.bDebug) {
				throw new NullPointerException("chat owner is null");
			} else {
				DebugLog.log("chat owner is null. Chat: " + this.getType());
				return null;
			}
		} else {
			return this.chatOwner;
		}
	}

	public ChatMode getMode() {
		return this.mode;
	}

	public ChatType getType() {
		return this.type;
	}

	public int getID() {
		return this.id;
	}

	public String getTitleID() {
		return this.titleID;
	}

	public Color getColor() {
		return this.settings.getFontColor();
	}

	public short getTabID() {
		return this.chatTab.getID();
	}

	public float getRange() {
		return this.settings.getRange();
	}

	public boolean isSendingToRadio() {
		return false;
	}

	public float getZombieAttractionRange() {
		return this.settings.getZombieAttractionRange();
	}

	public void setSettings(ChatSettings chatSettings) {
		this.settings = chatSettings;
		this.customSettings = true;
	}

	public void setFontSize(String string) {
		this.settings.setFontSize(string.toLowerCase());
	}

	public void setShowTimestamp(boolean boolean1) {
		this.settings.setShowTimestamp(boolean1);
	}

	public void setShowTitle(boolean boolean1) {
		this.settings.setShowChatTitle(boolean1);
	}

	protected boolean isCustomSettings() {
		return this.customSettings;
	}

	protected boolean isAllowImages() {
		return this.settings.isAllowImages();
	}

	protected boolean isAllowChatIcons() {
		return this.settings.isAllowChatIcons();
	}

	protected boolean isAllowColors() {
		return this.settings.isAllowColors();
	}

	protected boolean isAllowFonts() {
		return this.settings.isAllowFonts();
	}

	protected boolean isAllowBBcode() {
		return this.settings.isAllowBBcode();
	}

	protected boolean isEqualizeLineHeights() {
		return this.settings.isEqualizeLineHeights();
	}

	protected boolean isShowAuthor() {
		return this.settings.isShowAuthor();
	}

	protected boolean isShowTimestamp() {
		return this.settings.isShowTimestamp();
	}

	protected boolean isShowTitle() {
		return this.settings.isShowChatTitle();
	}

	protected String getFontSize() {
		return this.settings.getFontSize().toString();
	}

	protected String getTitle() {
		if (this.translatedTitle == null) {
			this.translatedTitle = Translator.getText(this.titleID);
		}

		return this.translatedTitle;
	}

	public void close() {
		synchronized (this.memberLock) {
			ArrayList arrayList = new ArrayList(this.members);
			Iterator iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				Integer integer = (Integer)iterator.next();
				this.leaveMember(integer);
			}

			this.members.clear();
		}
	}

	protected void packChat(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putInt(this.type.getValue());
		byteBufferWriter.putShort(this.getTabID());
		byteBufferWriter.putInt(this.id);
		byteBufferWriter.putBoolean(this.customSettings);
		if (this.customSettings) {
			this.settings.pack(byteBufferWriter);
		}
	}

	public ChatMessage unpackMessage(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		ChatMessage chatMessage = this.createMessage(string2);
		chatMessage.setAuthor(string);
		return chatMessage;
	}

	public void packMessage(ByteBufferWriter byteBufferWriter, ChatMessage chatMessage) {
		byteBufferWriter.putInt(this.id);
		byteBufferWriter.putUTF(chatMessage.getAuthor());
		byteBufferWriter.putUTF(chatMessage.getText());
	}

	public ChatMessage createMessage(String string) {
		return this.createMessage(this.getChatOwnerName(), string);
	}

	private ChatMessage createMessage(String string, String string2) {
		ChatMessage chatMessage = new ChatMessage(this, string2);
		chatMessage.setAuthor(string);
		chatMessage.setServerAuthor(false);
		return chatMessage;
	}

	public ServerChatMessage createServerMessage(String string) {
		ServerChatMessage serverChatMessage = new ServerChatMessage(this, string);
		serverChatMessage.setServerAuthor(true);
		return serverChatMessage;
	}

	public void showMessage(String string, String string2) {
		ChatMessage chatMessage = new ChatMessage(this, LocalDateTime.now(), string);
		chatMessage.setAuthor(string2);
		this.showMessage(chatMessage);
	}

	public void showMessage(ChatMessage chatMessage) {
		this.messages.add(chatMessage);
		if (this.isEnabled() && chatMessage.isShowInChat() && this.chatTab != null) {
			LuaEventManager.triggerEvent("OnAddMessage", chatMessage, this.getTabID());
		}
	}

	public String getMessageTextWithPrefix(ChatMessage chatMessage) {
		String string = this.getMessagePrefix(chatMessage);
		return string + " " + chatMessage.getTextWithReplacedParentheses();
	}

	public void sendMessageToChatMembers(ChatMessage chatMessage) {
		IsoPlayer player = ChatUtility.findPlayer(chatMessage.getAuthor());
		if (player == null) {
			DebugLog.log("Author \'" + chatMessage.getAuthor() + "\' not found");
		} else {
			synchronized (this.memberLock) {
				Iterator iterator = this.members.iterator();
				while (true) {
					if (!iterator.hasNext()) {
						break;
					}

					int int1 = (Integer)iterator.next();
					IsoPlayer player2 = ChatUtility.findPlayer(int1);
					if (player2 != null && player.getOnlineID() != int1) {
						this.sendMessageToPlayer(int1, chatMessage);
					}
				}
			}

			if (Core.bDebug) {
				DebugLog.log("New message \'" + chatMessage + "\' was sent members of chat \'" + this.getID() + "\'");
			}
		}
	}

	public void sendMessageToChatMembers(ServerChatMessage serverChatMessage) {
		synchronized (this.memberLock) {
			Iterator iterator = this.members.iterator();
			while (true) {
				if (!iterator.hasNext()) {
					break;
				}

				int int1 = (Integer)iterator.next();
				IsoPlayer player = ChatUtility.findPlayer(int1);
				if (player != null) {
					this.sendMessageToPlayer(int1, serverChatMessage);
				}
			}
		}
		if (Core.bDebug) {
			DebugLog.log("New message \'" + serverChatMessage + "\' was sent members of chat \'" + this.getID() + "\'");
		}
	}

	public void sendMessageToPlayer(UdpConnection udpConnection, ChatMessage chatMessage) {
		synchronized (this.memberLock) {
			boolean boolean1 = false;
			int[] intArray = udpConnection.playerIDs;
			int int1 = intArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				Integer integer = intArray[int2];
				if (boolean1) {
					break;
				}

				boolean1 = this.members.contains(integer);
			}

			if (!boolean1) {
				throw new RuntimeException("Passed connection didn\'t contained member of chat");
			} else {
				this.sendMessagePacket((short)186, udpConnection, chatMessage);
			}
		}
	}

	public void sendMessageToPlayer(int int1, ChatMessage chatMessage) {
		UdpConnection udpConnection = ChatUtility.findConnection(int1);
		if (udpConnection != null) {
			this.sendMessagePacket((short)186, udpConnection, chatMessage);
			DebugLog.log("Message \'" + chatMessage + "\' was sent to player with id \'" + int1 + "\' of chat \'" + this.getID() + "\'");
		}
	}

	public String getMessagePrefix(ChatMessage chatMessage) {
		StringBuilder stringBuilder = new StringBuilder(this.getChatSettingsTags());
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

	protected String getColorTag() {
		Color color = this.getColor();
		return this.getColorTag(color);
	}

	protected String getColorTag(Color color) {
		return "<RGB:" + color.r + "," + color.g + "," + color.b + ">";
	}

	protected String getFontSizeTag() {
		return "<SIZE:" + this.settings.getFontSize() + ">";
	}

	protected String getChatSettingsTags() {
		String string = this.getColorTag();
		return string + " " + this.getFontSizeTag() + " ";
	}

	public void addMember(int int1) {
		synchronized (this.memberLock) {
			if (!this.hasMember(int1)) {
				this.members.add(int1);
				this.justAddedMembers.add(int1);
				UdpConnection udpConnection = ChatUtility.findConnection(int1);
				if (udpConnection != null) {
					this.sendPlayerJoinChatPacket(udpConnection);
					this.chatTab.sendAddTabPacket(udpConnection);
				} else if (Core.bDebug) {
					throw new RuntimeException("Connection should exist!");
				}
			}
		}
	}

	public void leaveMember(Integer integer) {
		synchronized (this.memberLock) {
			if (this.hasMember(integer)) {
				this.justRemovedMembers.add(integer);
				UdpConnection udpConnection = ChatUtility.findConnection(integer);
				if (udpConnection != null) {
					this.sendPlayerLeaveChatPacket(udpConnection);
				}

				this.members.remove(integer);
			}
		}
	}

	private boolean hasMember(int int1) {
		return this.members.contains(int1);
	}

	public void removeMember(Integer integer) {
		synchronized (this.memberLock) {
			if (this.hasMember(integer)) {
				this.members.remove(integer);
			}
		}
	}

	public void syncMembersByUsernames(ArrayList arrayList) {
		synchronized (this.memberLock) {
			this.justAddedMembers.clear();
			this.justRemovedMembers.clear();
			ArrayList arrayList2 = new ArrayList(arrayList.size());
			IsoPlayer player = null;
			Iterator iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				player = ChatUtility.findPlayer(string);
				if (player != null) {
					arrayList2.add(player.getOnlineID());
				}
			}

			this.syncMembers(arrayList2);
		}
	}

	public ArrayList getJustAddedMembers() {
		synchronized (this.memberLock) {
			return this.justAddedMembers;
		}
	}

	public ArrayList getJustRemovedMembers() {
		synchronized (this.memberLock) {
			return this.justRemovedMembers;
		}
	}

	private void syncMembers(ArrayList arrayList) {
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			Integer integer = (Integer)iterator.next();
			this.addMember(integer);
		}

		ArrayList arrayList2 = new ArrayList();
		synchronized (this.memberLock) {
			Iterator iterator2 = this.members.iterator();
			Integer integer2;
			while (iterator2.hasNext()) {
				integer2 = (Integer)iterator2.next();
				if (!arrayList.contains(integer2)) {
					arrayList2.add(integer2);
				}
			}

			iterator2 = arrayList2.iterator();
			while (iterator2.hasNext()) {
				integer2 = (Integer)iterator2.next();
				this.leaveMember(integer2);
			}
		}
	}

	public void sendPlayerJoinChatPacket(UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)183, byteBufferWriter);
		this.packChat(byteBufferWriter);
		udpConnection.endPacketImmediate();
	}

	public void sendPlayerLeaveChatPacket(Integer integer) {
		UdpConnection udpConnection = ChatUtility.findConnection(integer);
		this.sendPlayerLeaveChatPacket(udpConnection);
	}

	public void sendPlayerLeaveChatPacket(UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)184, byteBufferWriter);
		byteBufferWriter.putInt(this.getID());
		byteBufferWriter.putInt(this.getType().getValue());
		udpConnection.endPacketImmediate();
	}

	public void sendToServer(ChatMessage chatMessage) {
		if (this.serverConnection == null) {
			DebugLog.log("Connection to server is null in client chat");
		}

		this.sendMessagePacket((short)185, this.serverConnection, chatMessage);
	}

	private void sendMessagePacket(short short1, UdpConnection udpConnection, ChatMessage chatMessage) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket(short1, byteBufferWriter);
		this.packMessage(byteBufferWriter, chatMessage);
		udpConnection.endPacketImmediate();
	}

	protected boolean hasChatTab() {
		return this.chatTab != null;
	}
}
