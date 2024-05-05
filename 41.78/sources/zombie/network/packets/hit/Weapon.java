package zombie.network.packets.hit;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import zombie.characters.IsoLivingCharacter;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.network.packets.INetworkPacket;


public class Weapon extends Instance implements INetworkPacket {
	protected InventoryItem item;
	protected HandWeapon weapon;

	public void set(HandWeapon handWeapon) {
		super.set(handWeapon.getRegistry_id());
		this.item = handWeapon;
		this.weapon = handWeapon;
	}

	public void parse(ByteBuffer byteBuffer, IsoLivingCharacter livingCharacter) {
		boolean boolean1 = byteBuffer.get() == 1;
		if (boolean1) {
			this.ID = byteBuffer.getShort();
			byteBuffer.get();
			if (livingCharacter != null) {
				this.item = livingCharacter.getPrimaryHandItem();
				if (this.item == null || this.item.getRegistry_id() != this.ID) {
					this.item = InventoryItemFactory.CreateItem(this.ID);
				}

				if (this.item != null) {
					try {
						this.item.load(byteBuffer, 195);
					} catch (BufferUnderflowException | IOException error) {
						DebugLog.Multiplayer.printException(error, "Weapon load error", LogSeverity.Error);
						this.item = InventoryItemFactory.CreateItem("Base.BareHands");
					}
				}
			}
		} else {
			this.item = InventoryItemFactory.CreateItem("Base.BareHands");
		}

		if (livingCharacter != null) {
			this.weapon = livingCharacter.bareHands;
			if (this.item instanceof HandWeapon) {
				this.weapon = (HandWeapon)this.item;
			}
		}
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		DebugLog.Multiplayer.error("Weapon.parse is not implemented");
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
		return super.isConsistent() && this.weapon != null;
	}

	public String getDescription() {
		String string = super.getDescription();
		return string + "\n\tWeapon [ weapon=" + (this.weapon == null ? "?" : "\"" + this.weapon.getDisplayName() + "\"") + " ]";
	}

	HandWeapon getWeapon() {
		return this.weapon;
	}
}
