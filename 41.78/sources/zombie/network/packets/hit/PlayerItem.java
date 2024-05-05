package zombie.network.packets.hit;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.network.packets.INetworkPacket;


public class PlayerItem extends Instance implements INetworkPacket {
	protected int itemId;
	protected InventoryItem item;

	public void set(InventoryItem inventoryItem) {
		super.set(inventoryItem.getRegistry_id());
		this.item = inventoryItem;
		this.itemId = this.item.getID();
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		boolean boolean1 = byteBuffer.get() == 1;
		if (boolean1) {
			this.ID = byteBuffer.getShort();
			byteBuffer.get();
			try {
				this.item = InventoryItemFactory.CreateItem(this.ID);
				if (this.item != null) {
					this.item.load(byteBuffer, 195);
				}
			} catch (BufferUnderflowException | IOException error) {
				DebugLog.Multiplayer.printException(error, "Item load error", LogSeverity.Error);
				this.item = null;
			}
		} else {
			this.item = null;
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		if (this.item == null) {
			byteBufferWriter.putByte((byte)0);
		} else {
			byteBufferWriter.putByte((byte)1);
			try {
				this.item.save(byteBufferWriter.bb, false);
			} catch (IOException ioException) {
				DebugLog.Multiplayer.printException(ioException, "Item write error", LogSeverity.Error);
			}
		}
	}

	public boolean isConsistent() {
		return super.isConsistent() && this.item != null;
	}

	public String getDescription() {
		String string = super.getDescription();
		return string + "\n\tItem [ Item=" + (this.item == null ? "?" : "\"" + this.item.getDisplayName() + "\"") + " ]";
	}

	public InventoryItem getItem() {
		return this.item;
	}
}
