package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
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


public class Stitch implements INetworkPacket {
	protected final Player wielder = new Player();
	protected final Player target = new Player();
	protected PlayerBodyPart bodyPart = new PlayerBodyPart();
	protected PlayerItem item = new PlayerItem();
	protected float stitchTime = 0.0F;
	protected boolean doIt = false;
	protected boolean infect = false;

	public void set(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, BodyPart bodyPart, InventoryItem inventoryItem, boolean boolean1) {
		this.wielder.set(gameCharacter);
		this.target.set(gameCharacter2);
		this.bodyPart.set(bodyPart);
		this.item.set(inventoryItem);
		this.stitchTime = bodyPart.getStitchTime();
		this.doIt = boolean1;
		this.infect = bodyPart.isInfectedWound();
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.wielder.parse(byteBuffer, udpConnection);
		this.wielder.parsePlayer(udpConnection);
		this.target.parse(byteBuffer, udpConnection);
		this.target.parsePlayer((UdpConnection)null);
		this.bodyPart.parse(byteBuffer, this.target.getCharacter());
		this.item.parse(byteBuffer, udpConnection);
		byteBuffer.putFloat(this.stitchTime);
		byteBuffer.put((byte)(this.doIt ? 1 : 0));
		byteBuffer.put((byte)(this.infect ? 1 : 0));
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		this.wielder.write(byteBufferWriter);
		this.target.write(byteBufferWriter);
		this.bodyPart.write(byteBufferWriter);
		this.item.write(byteBufferWriter);
		this.stitchTime = byteBufferWriter.bb.getFloat();
		this.doIt = byteBufferWriter.bb.get() == 1;
		this.infect = byteBufferWriter.bb.get() == 1;
	}

	public void process() {
		int int1 = this.wielder.getCharacter().getPerkLevel(PerkFactory.Perks.Doctor);
		if (((IsoPlayer)this.wielder.getCharacter()).getAccessLevel() != "None") {
			int1 = 10;
		}

		byte byte1 = 20;
		if (this.doIt) {
			if (this.wielder.getCharacter().getInventory().contains("SutureNeedleHolder") || this.item.getItem().getType() == "SutureNeedle") {
				byte1 = 10;
			}
		} else {
			byte1 = 5;
		}

		if (this.wielder.getCharacter().HasTrait("Hemophobic")) {
			this.wielder.getCharacter().getStats().setPanic(this.wielder.getCharacter().getStats().getPanic() + 50.0F);
		}

		if (this.item.getItem() != null) {
			this.item.getItem().Use();
		}

		if (this.bodyPart.getBodyPart().isGetStitchXp()) {
			this.wielder.getCharacter().getXp().AddXP(PerkFactory.Perks.Doctor, 15.0F);
		}

		this.bodyPart.getBodyPart().setStitched(this.doIt);
		int int2 = byte1 - int1 * 1;
		if (int2 < 0) {
			int2 = 0;
		}

		if (((IsoPlayer)this.wielder.getCharacter()).getAccessLevel() != "None") {
			this.bodyPart.getBodyPart().setAdditionalPain(this.bodyPart.getBodyPart().getAdditionalPain() + (float)int2);
		}

		if (this.doIt) {
			this.bodyPart.getBodyPart().setStitchTime(this.stitchTime);
		}

		if (this.infect) {
			this.bodyPart.getBodyPart().setInfectedWound(true);
		}
	}

	public boolean isConsistent() {
		return this.wielder.getCharacter() != null && this.wielder.getCharacter() instanceof IsoPlayer && this.target.getCharacter() != null && this.target.getCharacter() instanceof IsoPlayer && this.bodyPart.getBodyPart() != null && this.stitchTime < 50.0F && this.stitchTime >= 0.0F;
	}

	public boolean validate(UdpConnection udpConnection) {
		if (!GameClient.bClient || this.bodyPart.getBodyPart().isDeepWounded() && !this.bodyPart.getBodyPart().haveGlass()) {
			return PacketValidator.checkType8(udpConnection, this.wielder, this.target, this.getClass().getSimpleName());
		} else {
			DebugLogStream debugLogStream = DebugLog.General;
			String string = this.getClass().getSimpleName();
			debugLogStream.warn(string + ": Validate error: " + this.getDescription());
			return false;
		}
	}

	public String getDescription() {
		String string = "\n\t" + this.getClass().getSimpleName() + " [";
		string = string + "wielder=" + this.wielder.getDescription() + " | ";
		string = string + "target=" + this.target.getDescription() + " | ";
		string = string + "bodyPart=" + this.bodyPart.getDescription() + " | ";
		string = string + "item=" + this.item.getDescription() + " | ";
		string = string + "stitchTime=" + this.stitchTime + " | ";
		string = string + "doIt=" + this.doIt + " | ";
		string = string + "infect=" + this.infect + "] ";
		return string;
	}
}
