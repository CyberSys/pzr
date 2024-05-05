package zombie.network.chat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import zombie.GameWindow;
import zombie.characters.Faction;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatBase;
import zombie.chat.ChatMessage;
import zombie.chat.ChatTab;
import zombie.chat.ChatUtility;
import zombie.chat.ServerChatMessage;
import zombie.chat.defaultChats.AdminChat;
import zombie.chat.defaultChats.FactionChat;
import zombie.chat.defaultChats.GeneralChat;
import zombie.chat.defaultChats.RadioChat;
import zombie.chat.defaultChats.SafehouseChat;
import zombie.chat.defaultChats.SayChat;
import zombie.chat.defaultChats.ServerChat;
import zombie.chat.defaultChats.ShoutChat;
import zombie.chat.defaultChats.WhisperChat;
import zombie.core.Core;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.areas.SafeHouse;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;


public class ChatServer {
	private static ChatServer instance = null;
	private static final Stack availableChatsID = new Stack();
	private static int lastChatId = -1;
	private static final HashMap defaultChats = new HashMap();
	private static final ConcurrentHashMap chats = new ConcurrentHashMap();
	private static final ConcurrentHashMap factionChats = new ConcurrentHashMap();
	private static final ConcurrentHashMap safehouseChats = new ConcurrentHashMap();
	private static AdminChat adminChat = null;
	private static GeneralChat generalChat = null;
	private static ServerChat serverChat = null;
	private static RadioChat radioChat = null;
	private static boolean inited = false;
	private static final HashSet players = new HashSet();
	private static final String logName = "chat";
	private static ZLogger logger;
	private static final HashMap tabs = new HashMap();
	private static final String mainTabID = "main";
	private static final String adminTabID = "admin";

	public static ChatServer getInstance() {
		if (instance == null) {
			instance = new ChatServer();
		}

		return instance;
	}

	public static boolean isInited() {
		return inited;
	}

	private ChatServer() {
	}

	public void init() {
		if (!inited) {
			LoggerManager.createLogger("chat", Core.bDebug);
			logger = LoggerManager.getLogger("chat");
			logger.write("Start chat server initialization...", "info");
			ChatTab chatTab = new ChatTab((short)0, "UI_chat_main_tab_title_id");
			ChatTab chatTab2 = new ChatTab((short)1, "UI_chat_admin_tab_title_id");
			boolean boolean1 = ServerOptions.getInstance().DiscordEnable.getValue();
			GeneralChat generalChat = new GeneralChat(this.getNextChatID(), chatTab, boolean1);
			SayChat sayChat = new SayChat(this.getNextChatID(), chatTab);
			ShoutChat shoutChat = new ShoutChat(this.getNextChatID(), chatTab);
			RadioChat radioChat = new RadioChat(this.getNextChatID(), chatTab);
			AdminChat adminChat = new AdminChat(this.getNextChatID(), chatTab2);
			ServerChat serverChat = new ServerChat(this.getNextChatID(), chatTab);
			chats.put(generalChat.getID(), generalChat);
			chats.put(sayChat.getID(), sayChat);
			chats.put(shoutChat.getID(), shoutChat);
			chats.put(radioChat.getID(), radioChat);
			chats.put(adminChat.getID(), adminChat);
			chats.put(serverChat.getID(), serverChat);
			defaultChats.put(generalChat.getType(), generalChat);
			defaultChats.put(sayChat.getType(), sayChat);
			defaultChats.put(shoutChat.getType(), shoutChat);
			defaultChats.put(serverChat.getType(), serverChat);
			defaultChats.put(radioChat.getType(), radioChat);
			tabs.put("main", chatTab);
			tabs.put("admin", chatTab2);
			generalChat = generalChat;
			adminChat = adminChat;
			serverChat = serverChat;
			radioChat = radioChat;
			inited = true;
			logger.write("General chat has id = " + generalChat.getID(), "info");
			logger.write("Say chat has id = " + sayChat.getID(), "info");
			logger.write("Shout chat has id = " + shoutChat.getID(), "info");
			logger.write("Radio chat has id = " + radioChat.getID(), "info");
			logger.write("Admin chat has id = " + adminChat.getID(), "info");
			logger.write("Server chat has id = " + serverChat.getID(), "info");
			logger.write("Chat server successfully initialized", "info");
		}
	}

	public void initPlayer(short short1) {
		logger.write("Player with id = \'" + short1 + "\' tries to connect", "info");
		synchronized (players) {
			if (players.contains(short1)) {
				logger.write("Player already connected!", "warning");
				return;
			}
		}
		logger.write("Adding player \'" + short1 + "\' to chat server", "info");
		IsoPlayer player = ChatUtility.findPlayer(short1);
		UdpConnection udpConnection = ChatUtility.findConnection(short1);
		if (udpConnection != null && player != null) {
			this.sendInitPlayerChatPacket(udpConnection);
			this.addDefaultChats(short1);
			logger.write("Player joined to default chats", "info");
			if (udpConnection.accessLevel == 32) {
				this.joinAdminChat(short1);
			}

			Faction faction = Faction.getPlayerFaction(player);
			if (faction != null) {
				this.addMemberToFactionChat(faction.getName(), short1);
			}

			SafeHouse safeHouse = SafeHouse.hasSafehouse(player);
			if (safeHouse != null) {
				this.addMemberToSafehouseChat(safeHouse.getId(), short1);
			}

			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.PlayerConnectedToChat.doPacket(byteBufferWriter);
			PacketTypes.PacketType.PlayerConnectedToChat.send(udpConnection);
			synchronized (players) {
				players.add(short1);
			}

			logger.write("Player " + player.getUsername() + "(" + short1 + ") joined to chat server successfully", "info");
		} else {
			logger.write("Player or connection is not found on server!", "error");
			logger.write((udpConnection == null ? "connection = null " : "") + (player == null ? "player = null" : ""), "error");
		}
	}

	public void processMessageFromPlayerPacket(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		synchronized (chats) {
			ChatBase chatBase = (ChatBase)chats.get(int1);
			ChatMessage chatMessage = chatBase.unpackMessage(byteBuffer);
			logger.write("Got message:" + chatMessage, "info");
			if (!ChatUtility.chatStreamEnabled(chatBase.getType())) {
				logger.write("Message ignored by server because the chat disabled by server settings", "warning");
			} else {
				this.sendMessage(chatMessage);
				logger.write("Message " + chatMessage + " sent to chat (id = " + chatBase.getID() + ") members", "info");
			}
		}
	}

	public void processPlayerStartWhisperChatPacket(ByteBuffer byteBuffer) {
		logger.write("Whisper chat starting...", "info");
		if (!ChatUtility.chatStreamEnabled(ChatType.whisper)) {
			logger.write("Message for whisper chat is ignored because whisper chat is disabled by server settings", "info");
		} else {
			String string = GameWindow.ReadString(byteBuffer);
			String string2 = GameWindow.ReadString(byteBuffer);
			logger.write("Player \'" + string + "\' attempt to start whispering with \'" + string2 + "\'", "info");
			IsoPlayer player = ChatUtility.findPlayer(string);
			IsoPlayer player2 = ChatUtility.findPlayer(string2);
			if (player == null) {
				logger.write("Player \'" + string + "\' is not found!", "error");
				throw new RuntimeException("Player not found");
			} else if (player2 == null) {
				logger.write("Player \'" + string + "\' attempt to start whisper dialog with \'" + string2 + "\' but this player not found!", "info");
				UdpConnection udpConnection = ChatUtility.findConnection(player.getOnlineID());
				this.sendPlayerNotFoundMessage(udpConnection);
			} else {
				logger.write("Both players found", "info");
				WhisperChat whisperChat = new WhisperChat(this.getNextChatID(), (ChatTab)tabs.get("main"), string, string2);
				whisperChat.addMember(player.getOnlineID());
				whisperChat.addMember(player2.getOnlineID());
				chats.put(whisperChat.getID(), whisperChat);
				ZLogger zLogger = logger;
				int int1 = whisperChat.getID();
				zLogger.write("Whisper chat (id = " + int1 + ") between \'" + player.getUsername() + "\' and \'" + player2.getUsername() + "\' started", "info");
			}
		}
	}

	private void sendPlayerNotFoundMessage(UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.PlayerNotFound.doPacket(byteBufferWriter);
		PacketTypes.PacketType.PlayerNotFound.send(udpConnection);
		logger.write("\'Player not found\' packet was sent", "info");
	}

	public ChatMessage unpackChatMessage(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		return ((ChatBase)chats.get(int1)).unpackMessage(byteBuffer);
	}

	public void disconnectPlayer(short short1) {
		logger.write("Player " + short1 + " disconnecting...", "info");
		synchronized (chats) {
			Iterator iterator = chats.values().iterator();
			while (iterator.hasNext()) {
				ChatBase chatBase = (ChatBase)iterator.next();
				chatBase.removeMember(short1);
				if (chatBase.getType() == ChatType.whisper) {
					this.closeChat(chatBase.getID());
				}
			}
		}
		synchronized (players) {
			players.remove(short1);
		}
		logger.write("Disconnecting player " + short1 + " finished", "info");
	}

	private void closeChat(int int1) {
		synchronized (chats) {
			if (!chats.containsKey(int1)) {
				throw new RuntimeException("Chat \'" + int1 + "\' requested to close but it\'s not exists.");
			}

			ChatBase chatBase = (ChatBase)chats.get(int1);
			chatBase.close();
			chats.remove(int1);
		}
		synchronized (availableChatsID) {
			availableChatsID.push(int1);
		}
	}

	public void joinAdminChat(short short1) {
		if (adminChat == null) {
			logger.write("Admin chat is null! Can\'t add player to it", "warning");
		} else {
			adminChat.addMember(short1);
			logger.write("Player joined admin chat", "info");
		}
	}

	public void leaveAdminChat(short short1) {
		logger.write("Player " + short1 + " are leaving admin chat...", "info");
		UdpConnection udpConnection = ChatUtility.findConnection(short1);
		if (adminChat == null) {
			logger.write("Admin chat is null. Can\'t leave it! ChatServer", "warning");
		} else if (udpConnection == null) {
			logger.write("Connection to player is null. Can\'t leave admin chat! ChatServer.leaveAdminChat", "warning");
		} else {
			adminChat.leaveMember(short1);
			((ChatTab)tabs.get("admin")).sendRemoveTabPacket(udpConnection);
			logger.write("Player " + short1 + " leaved admin chat", "info");
		}
	}

	public FactionChat createFactionChat(String string) {
		logger.write("Creating faction chat \'" + string + "\'", "info");
		if (factionChats.containsKey(string)) {
			logger.write("Faction chat \'" + string + "\' already exists!", "warning");
			return (FactionChat)factionChats.get(string);
		} else {
			FactionChat factionChat = new FactionChat(this.getNextChatID(), (ChatTab)tabs.get("main"));
			chats.put(factionChat.getID(), factionChat);
			factionChats.put(string, factionChat);
			logger.write("Faction chat \'" + string + "\' created", "info");
			return factionChat;
		}
	}

	public SafehouseChat createSafehouseChat(String string) {
		logger.write("Creating safehouse chat \'" + string + "\'", "info");
		if (safehouseChats.containsKey(string)) {
			logger.write("Safehouse chat already has chat with name \'" + string + "\'", "warning");
			return (SafehouseChat)safehouseChats.get(string);
		} else {
			SafehouseChat safehouseChat = new SafehouseChat(this.getNextChatID(), (ChatTab)tabs.get("main"));
			chats.put(safehouseChat.getID(), safehouseChat);
			safehouseChats.put(string, safehouseChat);
			logger.write("Safehouse chat \'" + string + "\' created", "info");
			return safehouseChat;
		}
	}

	public void removeFactionChat(String string) {
		logger.write("Removing faction chat \'" + string + "\'...", "info");
		int int1;
		synchronized (factionChats) {
			if (!factionChats.containsKey(string)) {
				String string2 = "Faction chat \'" + string + "\' tried to delete but it\'s not exists.";
				logger.write(string2, "error");
				RuntimeException runtimeException = new RuntimeException(string2);
				logger.write((Exception)runtimeException);
				throw runtimeException;
			}

			FactionChat factionChat = (FactionChat)factionChats.get(string);
			int1 = factionChat.getID();
			factionChats.remove(string);
		}
		this.closeChat(int1);
		logger.write("Faction chat \'" + string + "\' removed", "info");
	}

	public void removeSafehouseChat(String string) {
		logger.write("Removing safehouse chat \'" + string + "\'...", "info");
		int int1;
		synchronized (safehouseChats) {
			if (!safehouseChats.containsKey(string)) {
				String string2 = "Safehouse chat \'" + string + "\' tried to delete but it\'s not exists.";
				logger.write(string2, "error");
				RuntimeException runtimeException = new RuntimeException(string2);
				logger.write((Exception)runtimeException);
				throw runtimeException;
			}

			SafehouseChat safehouseChat = (SafehouseChat)safehouseChats.get(string);
			int1 = safehouseChat.getID();
			safehouseChats.remove(string);
		}
		this.closeChat(int1);
		logger.write("Safehouse chat \'" + string + "\' removed", "info");
	}

	public void syncFactionChatMembers(String string, String string2, ArrayList arrayList) {
		logger.write("Start syncing faction chat \'" + string + "\'...", "info");
		if (string != null && string2 != null && arrayList != null) {
			synchronized (factionChats) {
				if (!factionChats.containsKey(string)) {
					logger.write("Faction chat \'" + string + "\' is not exist", "warning");
					return;
				}

				ArrayList arrayList2 = new ArrayList(arrayList);
				arrayList2.add(string2);
				FactionChat factionChat = (FactionChat)factionChats.get(string);
				factionChat.syncMembersByUsernames(arrayList2);
				StringBuilder stringBuilder = new StringBuilder("These members were added: ");
				Iterator iterator = factionChat.getJustAddedMembers().iterator();
				short short1;
				while (iterator.hasNext()) {
					short1 = (Short)iterator.next();
					stringBuilder.append("\'").append(ChatUtility.findPlayerName(short1)).append("\', ");
				}

				stringBuilder.append(". These members were removed: ");
				iterator = factionChat.getJustRemovedMembers().iterator();
				while (true) {
					if (!iterator.hasNext()) {
						logger.write(stringBuilder.toString(), "info");
						break;
					}

					short1 = (Short)iterator.next();
					stringBuilder.append("\'").append(ChatUtility.findPlayerName(short1)).append("\', ");
				}
			}

			logger.write("Syncing faction chat \'" + string + "\' finished", "info");
		} else {
			logger.write("Faction name or faction owner or players is null", "warning");
		}
	}

	public void syncSafehouseChatMembers(String string, String string2, ArrayList arrayList) {
		logger.write("Start syncing safehouse chat \'" + string + "\'...", "info");
		if (string != null && string2 != null && arrayList != null) {
			synchronized (safehouseChats) {
				if (!safehouseChats.containsKey(string)) {
					logger.write("Safehouse chat \'" + string + "\' is not exist", "warning");
					return;
				}

				ArrayList arrayList2 = new ArrayList(arrayList);
				arrayList2.add(string2);
				SafehouseChat safehouseChat = (SafehouseChat)safehouseChats.get(string);
				safehouseChat.syncMembersByUsernames(arrayList2);
				StringBuilder stringBuilder = new StringBuilder("These members were added: ");
				Iterator iterator = safehouseChat.getJustAddedMembers().iterator();
				short short1;
				while (iterator.hasNext()) {
					short1 = (Short)iterator.next();
					stringBuilder.append("\'").append(ChatUtility.findPlayerName(short1)).append("\', ");
				}

				stringBuilder.append("These members were removed: ");
				iterator = safehouseChat.getJustRemovedMembers().iterator();
				while (true) {
					if (!iterator.hasNext()) {
						logger.write(stringBuilder.toString(), "info");
						break;
					}

					short1 = (Short)iterator.next();
					stringBuilder.append("\'").append(ChatUtility.findPlayerName(short1)).append("\', ");
				}
			}

			logger.write("Syncing safehouse chat \'" + string + "\' finished", "info");
		} else {
			logger.write("Safehouse name or Safehouse owner or players is null", "warning");
		}
	}

	private void addMemberToSafehouseChat(String string, short short1) {
		if (!safehouseChats.containsKey(string)) {
			logger.write("Safehouse chat is not initialized!", "warning");
		} else {
			synchronized (safehouseChats) {
				SafehouseChat safehouseChat = (SafehouseChat)safehouseChats.get(string);
				safehouseChat.addMember(short1);
			}

			logger.write("Player joined to chat of safehouse \'" + string + "\'", "info");
		}
	}

	private void addMemberToFactionChat(String string, short short1) {
		if (!factionChats.containsKey(string)) {
			logger.write("Faction chat is not initialized!", "warning");
		} else {
			synchronized (factionChats) {
				FactionChat factionChat = (FactionChat)factionChats.get(string);
				factionChat.addMember(short1);
			}

			logger.write("Player joined to chat of faction \'" + string + "\'", "info");
		}
	}

	public void sendServerAlertMessageToServerChat(String string, String string2) {
		serverChat.sendMessageToChatMembers(serverChat.createMessage(string, string2, true));
		logger.write("Server alert message: \'" + string2 + "\' by \'" + string + "\' sent.");
	}

	public void sendServerAlertMessageToServerChat(String string) {
		serverChat.sendMessageToChatMembers(serverChat.createServerMessage(string, true));
		logger.write("Server alert message: \'" + string + "\' sent.");
	}

	public ChatMessage createRadiostationMessage(String string, int int1) {
		return radioChat.createBroadcastingMessage(string, int1);
	}

	public void sendMessageToServerChat(UdpConnection udpConnection, String string) {
		ServerChatMessage serverChatMessage = serverChat.createServerMessage(string, false);
		serverChat.sendMessageToPlayer(udpConnection, serverChatMessage);
	}

	public void sendMessageToServerChat(String string) {
		ServerChatMessage serverChatMessage = serverChat.createServerMessage(string, false);
		serverChat.sendMessageToChatMembers(serverChatMessage);
	}

	public void sendMessageFromDiscordToGeneralChat(String string, String string2) {
		if (string != null && string2 != null) {
			logger.write("Got message \'" + string2 + "\' by author \'" + string + "\' from discord");
		}

		ChatMessage chatMessage = generalChat.createMessage(string2);
		chatMessage.makeFromDiscord();
		chatMessage.setAuthor(string);
		if (ChatUtility.chatStreamEnabled(ChatType.general)) {
			this.sendMessage(chatMessage);
			logger.write("Message \'" + string2 + "\' send from discord to general chat members");
		} else {
			generalChat.sendToDiscordGeneralChatDisabled();
			logger.write("General chat disabled so error message sent to discord", "warning");
		}
	}

	private int getNextChatID() {
		synchronized (availableChatsID) {
			if (availableChatsID.isEmpty()) {
				++lastChatId;
				availableChatsID.push(lastChatId);
			}

			return (Integer)availableChatsID.pop();
		}
	}

	private void sendMessage(ChatMessage chatMessage) {
		synchronized (chats) {
			if (chats.containsKey(chatMessage.getChatID())) {
				ChatBase chatBase = (ChatBase)chats.get(chatMessage.getChatID());
				chatBase.sendMessageToChatMembers(chatMessage);
			}
		}
	}

	private void sendInitPlayerChatPacket(UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.InitPlayerChat.doPacket(byteBufferWriter);
		byteBufferWriter.putShort((short)tabs.size());
		Iterator iterator = tabs.values().iterator();
		while (iterator.hasNext()) {
			ChatTab chatTab = (ChatTab)iterator.next();
			byteBufferWriter.putShort(chatTab.getID());
			byteBufferWriter.putUTF(chatTab.getTitleID());
		}

		PacketTypes.PacketType.InitPlayerChat.send(udpConnection);
	}

	private void addDefaultChats(short short1) {
		Iterator iterator = defaultChats.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			ChatBase chatBase = (ChatBase)entry.getValue();
			chatBase.addMember(short1);
		}
	}

	public void sendMessageToAdminChat(String string) {
		ServerChatMessage serverChatMessage = adminChat.createServerMessage(string);
		adminChat.sendMessageToChatMembers(serverChatMessage);
	}
}
