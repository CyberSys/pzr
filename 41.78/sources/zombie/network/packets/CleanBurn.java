package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.skills.PerkFactory;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugLogStream;
import zombie.inventory.InventoryItem;
import zombie.network.GameClient;
import zombie.network.PacketValidator;
import zombie.network.packets.hit.Player;
import zombie.network.packets.hit.PlayerBodyPart;
import zombie.network.packets.hit.PlayerItem;


public class CleanBurn implements INetworkPacket {
	protected final Player wielder = new Player();
	protected final Player target = new Player();
	protected PlayerBodyPart bodyPart = new PlayerBodyPart();
	protected PlayerItem bandage = new PlayerItem();

	public void set(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, BodyPart bodyPart, InventoryItem inventoryItem) {
		this.wielder.set(gameCharacter);
		this.target.set(gameCharacter2);
		this.bodyPart.set(bodyPart);
		this.bandage.set(inventoryItem);
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.wielder.parse(byteBuffer, udpConnection);
		this.wielder.parsePlayer(udpConnection);
		this.target.parse(byteBuffer, udpConnection);
		this.target.parsePlayer((UdpConnection)null);
		this.bodyPart.parse(byteBuffer, this.target.getCharacter());
		this.bandage.parse(byteBuffer, udpConnection);
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		this.wielder.write(byteBufferWriter);
		this.target.write(byteBufferWriter);
		this.bodyPart.write(byteBufferWriter);
		this.bandage.write(byteBufferWriter);
	}

	public void process() {
		int int1 = this.wielder.getCharacter().getPerkLevel(PerkFactory.Perks.Doctor);
		if (!this.wielder.getPlayer().isAccessLevel("None")) {
			int1 = 10;
		}

		if (this.wielder.getCharacter().HasTrait("Hemophobic")) {
			this.wielder.getCharacter().getStats().setPanic(this.wielder.getCharacter().getStats().getPanic() + 50.0F);
		}

		this.wielder.getCharacter().getXp().AddXP(PerkFactory.Perks.Doctor, 10.0F);
		int int2 = 60 - int1 * 1;
		this.bodyPart.getBodyPart().setAdditionalPain(this.bodyPart.getBodyPart().getAdditionalPain() + (float)int2);
		this.bodyPart.getBodyPart().setNeedBurnWash(false);
		this.bandage.getItem().Use();
	}

	public boolean isConsistent() {
		return this.wielder.getCharacter() != null && this.target.getCharacter() != null && this.bodyPart.getBodyPart() != null && this.bandage.getItem() != null;
	}

	public boolean validate(UdpConnection udpConnection) {
		if (GameClient.bClient && !this.bodyPart.getBodyPart().isNeedBurnWash()) {
			DebugLogStream debugLogStream = DebugLog.General;
			String string = this.getClass().getSimpleName();
			debugLogStream.warn(string + ": Validate error: " + this.getDescription());
			return false;
		} else {
			return PacketValidator.checkShortDistance(udpConnection, this.wielder, this.target, this.getClass().getSimpleName());
		}
	}

	public String getDescription() {
		String string = "\n\t" + this.getClass().getSimpleName() + " [";
		string = string + "wielder=" + this.wielder.getDescription() + " | ";
		string = string + "target=" + this.target.getDescription() + " | ";
		string = string + "bodyPart=" + this.bodyPart.getDescription() + " | ";
		string = string + "bandage=" + this.bandage + "] ";
		return string;
	}
}
