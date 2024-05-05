package zombie.network.packets;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.characters.IsoPlayer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.inventory.InventoryItem;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerGUI;


public class SyncClothingPacket implements INetworkPacket {
	private IsoPlayer player = null;
	private String location = "";
	private InventoryItem item = null;

	public void set(IsoPlayer player, String string, InventoryItem inventoryItem) {
		this.player = player;
		this.location = string;
		this.item = inventoryItem;
	}

	public boolean isEquals(IsoPlayer player, String string, InventoryItem inventoryItem) {
		return this.player.OnlineID == player.OnlineID && this.location.equals(string) && this.item == inventoryItem;
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		this.location = GameWindow.ReadString(byteBuffer);
		byte byte1 = byteBuffer.get();
		if (byte1 == 1) {
			try {
				this.item = InventoryItem.loadItem(byteBuffer, 195);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			if (this.item == null) {
				return;
			}
		}

		if (GameServer.bServer) {
			this.player = (IsoPlayer)GameServer.IDToPlayerMap.get(short1);
		} else {
			this.player = (IsoPlayer)GameClient.IDToPlayerMap.get(short1);
		}

		if (this.player != null) {
			try {
				this.player.getHumanVisual().load(byteBuffer, 195);
				this.player.getItemVisuals().load(byteBuffer, 195);
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
				return;
			}

			if (byte1 == 1) {
				this.player.getWornItems().setItem(this.location, this.item);
			}

			if (GameServer.bServer && ServerGUI.isCreated() || GameClient.bClient) {
				this.player.resetModelNextFrame();
			}
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putShort(this.player.OnlineID);
		byteBufferWriter.putUTF(this.location);
		if (this.item == null) {
			byteBufferWriter.putByte((byte)0);
		} else {
			byteBufferWriter.putByte((byte)1);
			try {
				this.item.saveWithSize(byteBufferWriter.bb, false);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		try {
			this.player.getHumanVisual().save(byteBufferWriter.bb);
			ItemVisuals itemVisuals = new ItemVisuals();
			this.player.getItemVisuals(itemVisuals);
			itemVisuals.save(byteBufferWriter.bb);
		} catch (IOException ioException2) {
			ioException2.printStackTrace();
		}
	}

	public boolean isConsistent() {
		return this.player != null;
	}

	public String getDescription() {
		String string;
		if (this.player == null) {
			string = "player=null";
		} else {
			string = String.format("player=%s(oid:%d)", this.player.username, this.player.OnlineID);
		}

		string = string + ", location=" + this.location;
		if (this.item == null) {
			string = string + ", item=null";
		} else {
			string = string + ", item=" + this.item.getFullType();
		}

		return string;
	}
}
