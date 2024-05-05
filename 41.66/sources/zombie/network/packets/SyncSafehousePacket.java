package zombie.network.packets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import zombie.GameWindow;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.iso.areas.SafeHouse;
import zombie.network.GameClient;
import zombie.network.PacketValidator;


public class SyncSafehousePacket implements INetworkPacket {
	final byte requiredManagerAccessLevel = 56;
	int x;
	int y;
	short w;
	short h;
	public String ownerUsername;
	ArrayList members = new ArrayList();
	ArrayList membersRespawn = new ArrayList();
	public boolean remove = false;
	String title = "";
	public SafeHouse safehouse;
	public boolean shouldCreateChat;

	public void set(SafeHouse safeHouse, boolean boolean1) {
		this.x = safeHouse.getX();
		this.y = safeHouse.getY();
		this.w = (short)safeHouse.getW();
		this.h = (short)safeHouse.getH();
		this.ownerUsername = safeHouse.getOwner();
		this.members.clear();
		this.members.addAll(safeHouse.getPlayers());
		this.membersRespawn.clear();
		this.membersRespawn.addAll(safeHouse.playersRespawn);
		this.remove = boolean1;
		this.title = safeHouse.getTitle();
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.x = byteBuffer.getInt();
		this.y = byteBuffer.getInt();
		this.w = byteBuffer.getShort();
		this.h = byteBuffer.getShort();
		this.ownerUsername = GameWindow.ReadString(byteBuffer);
		short short1 = byteBuffer.getShort();
		this.members.clear();
		for (int int1 = 0; int1 < short1; ++int1) {
			this.members.add(GameWindow.ReadString(byteBuffer));
		}

		short short2 = byteBuffer.getShort();
		for (int int2 = 0; int2 < short2; ++int2) {
			this.membersRespawn.add(GameWindow.ReadString(byteBuffer));
		}

		this.remove = byteBuffer.get() == 1;
		this.title = GameWindow.ReadString(byteBuffer);
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putInt(this.x);
		byteBufferWriter.putInt(this.y);
		byteBufferWriter.putShort(this.w);
		byteBufferWriter.putShort(this.h);
		byteBufferWriter.putUTF(this.ownerUsername);
		byteBufferWriter.putShort((short)this.members.size());
		Iterator iterator = this.members.iterator();
		String string;
		while (iterator.hasNext()) {
			string = (String)iterator.next();
			byteBufferWriter.putUTF(string);
		}

		byteBufferWriter.putShort((short)this.membersRespawn.size());
		iterator = this.membersRespawn.iterator();
		while (iterator.hasNext()) {
			string = (String)iterator.next();
			byteBufferWriter.putUTF(string);
		}

		byteBufferWriter.putByte((byte)(this.remove ? 1 : 0));
		byteBufferWriter.putUTF(this.title);
	}

	public void process() {
		this.safehouse = SafeHouse.getSafeHouse(this.x, this.y, this.w, this.h);
		this.shouldCreateChat = false;
		if (this.safehouse == null) {
			this.safehouse = SafeHouse.addSafeHouse(this.x, this.y, this.w, this.h, this.ownerUsername, GameClient.bClient);
			this.shouldCreateChat = true;
		}

		if (this.safehouse != null) {
			this.safehouse.getPlayers().clear();
			this.safehouse.getPlayers().addAll(this.members);
			this.safehouse.playersRespawn.clear();
			this.safehouse.playersRespawn.addAll(this.membersRespawn);
			this.safehouse.setTitle(this.title);
			this.safehouse.setOwner(this.ownerUsername);
			if (this.remove) {
				SafeHouse.getSafehouseList().remove(this.safehouse);
				int int1 = this.x;
				DebugLog.log("safehouse: removed " + int1 + "," + this.y + "," + this.w + "," + this.h + " owner=" + this.safehouse.getOwner());
			}
		}
	}

	public boolean validate(UdpConnection udpConnection) {
		boolean boolean1 = (udpConnection.accessLevel & 56) != 0;
		this.safehouse = SafeHouse.getSafeHouse(this.x, this.y, this.w, this.h);
		if (this.safehouse == null) {
			if (udpConnection.accessLevel == 1 && SafeHouse.hasSafehouse(this.ownerUsername) != null) {
				if (PacketValidator.doKickUser(udpConnection, this.getClass().getSimpleName(), "UI_ValidationFailed_Type19")) {
					LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\" description=\"%s\"", udpConnection.username, "UI_ValidationFailed_Type19", this.getClass().getSimpleName(), this.getDescription()));
				}

				return false;
			} else if (udpConnection.accessLevel == 1 && (this.h > 100 || this.w > 100)) {
				if (PacketValidator.doKickUser(udpConnection, this.getClass().getSimpleName(), "UI_ValidationFailed_Type20")) {
					LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\" description=\"%s\"", udpConnection.username, "UI_ValidationFailed_Type20", this.getClass().getSimpleName(), this.getDescription()));
				}

				return false;
			} else {
				return true;
			}
		} else if (!boolean1) {
			return true;
		} else {
			return PacketValidator.checkType7(udpConnection, this.safehouse.getOwner(), SyncSafehousePacket.class.getSimpleName());
		}
	}

	public String getDescription() {
		String string = "\n\t" + this.getClass().getSimpleName() + " [";
		string = string + "position=(" + this.x + ", " + this.y + ", " + this.w + ", " + this.h + ") | ";
		string = string + "ownerUsername=" + this.ownerUsername + " | ";
		string = string + "members=" + Arrays.toString(this.members.toArray()) + " | ";
		string = string + "membersRespawn=" + Arrays.toString(this.membersRespawn.toArray()) + " | ";
		string = string + "remove=" + this.remove + " | ";
		string = string + "title=" + this.title + "] ";
		return string;
	}
}
