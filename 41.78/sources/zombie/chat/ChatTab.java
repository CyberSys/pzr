package zombie.chat;

import java.util.HashSet;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.PacketTypes;


public class ChatTab {
	private short id;
	private String titleID;
	private String translatedTitle;
	private HashSet containedChats;
	private boolean enabled;

	public ChatTab(short short1, String string) {
		this.enabled = false;
		this.id = short1;
		this.titleID = string;
		this.translatedTitle = Translator.getText(string);
		this.containedChats = new HashSet();
	}

	public ChatTab(short short1, String string, int int1) {
		this(short1, string);
		this.containedChats.add(int1);
	}

	public void RemoveChat(int int1) {
		if (!this.containedChats.contains(int1)) {
			throw new RuntimeException("Tab \'" + this.id + "\' doesn\'t contains a chat id: " + int1);
		} else {
			this.containedChats.remove(int1);
		}
	}

	public String getTitleID() {
		return this.titleID;
	}

	public String getTitle() {
		return this.translatedTitle;
	}

	public short getID() {
		return this.id;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean boolean1) {
		this.enabled = boolean1;
	}

	public void sendAddTabPacket(UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.AddChatTab.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(this.getID());
		PacketTypes.PacketType.AddChatTab.send(udpConnection);
	}

	public void sendRemoveTabPacket(UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.RemoveChatTab.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(this.getID());
		PacketTypes.PacketType.RemoveChatTab.send(udpConnection);
	}
}
