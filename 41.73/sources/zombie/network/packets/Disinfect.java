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
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.network.GameClient;
import zombie.network.PacketValidator;
import zombie.network.packets.hit.Player;
import zombie.network.packets.hit.PlayerBodyPart;
import zombie.network.packets.hit.PlayerItem;


public class Disinfect implements INetworkPacket {
	protected final Player wielder = new Player();
	protected final Player target = new Player();
	protected PlayerBodyPart bodyPart = new PlayerBodyPart();
	protected PlayerItem alcohol = new PlayerItem();

	public void set(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, BodyPart bodyPart, InventoryItem inventoryItem) {
		this.wielder.set(gameCharacter);
		this.target.set(gameCharacter2);
		this.bodyPart.set(bodyPart);
		this.alcohol.set(inventoryItem);
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.wielder.parse(byteBuffer, udpConnection);
		this.wielder.parsePlayer(udpConnection);
		this.target.parse(byteBuffer, udpConnection);
		this.target.parsePlayer((UdpConnection)null);
		this.bodyPart.parse(byteBuffer, this.target.getCharacter());
		this.alcohol.parse(byteBuffer, udpConnection);
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		this.wielder.write(byteBufferWriter);
		this.target.write(byteBufferWriter);
		this.bodyPart.write(byteBufferWriter);
		this.alcohol.write(byteBufferWriter);
	}

	public void process() {
		int int1 = this.wielder.getCharacter().getPerkLevel(PerkFactory.Perks.Doctor);
		if (!this.wielder.getPlayer().isAccessLevel("None")) {
			int1 = 10;
		}

		this.bodyPart.getBodyPart().setAlcoholLevel(this.bodyPart.getBodyPart().getAlcoholLevel() + this.alcohol.getItem().getAlcoholPower());
		float float1 = this.alcohol.getItem().getAlcoholPower() * 13.0F - (float)(int1 / 2);
		this.bodyPart.getBodyPart().setAdditionalPain(this.bodyPart.getBodyPart().getAdditionalPain() + float1);
		if (this.alcohol.getItem() instanceof Food) {
			Food food = (Food)this.alcohol.getItem();
			food.setThirstChange(food.getThirstChange() + 0.1F);
			if (food.getBaseHunger() < 0.0F) {
				food.setHungChange(food.getHungChange() + 0.1F);
			}
		}

		if (!((double)this.alcohol.getItem().getScriptItem().getThirstChange() > -0.01) && !((double)this.alcohol.getItem().getScriptItem().getHungerChange() > -0.01)) {
			if (this.alcohol.getItem() instanceof DrainableComboItem) {
				this.alcohol.getItem().Use();
			}
		} else {
			this.alcohol.getItem().Use();
		}
	}

	public boolean isConsistent() {
		return this.wielder.getCharacter() != null && this.wielder.getCharacter() instanceof IsoPlayer && this.target.getCharacter() != null && this.target.getCharacter() instanceof IsoPlayer && this.bodyPart.getBodyPart() != null && this.alcohol.getItem() != null;
	}

	public boolean validate(UdpConnection udpConnection) {
		if (GameClient.bClient && this.alcohol.getItem().getAlcoholPower() <= 0.0F) {
			DebugLogStream debugLogStream = DebugLog.General;
			String string = this.getClass().getSimpleName();
			debugLogStream.warn(string + ": Validate error: " + this.getDescription());
			return false;
		} else {
			return PacketValidator.checkType8(udpConnection, this.wielder, this.target, this.getClass().getSimpleName());
		}
	}

	public String getDescription() {
		String string = "\n\t" + this.getClass().getSimpleName() + " [";
		string = string + "wielder=" + this.wielder.getDescription() + " | ";
		string = string + "target=" + this.target.getDescription() + " | ";
		string = string + "bodyPart=" + this.bodyPart.getDescription() + " | ";
		string = string + "alcohol=" + this.alcohol.getDescription() + "] ";
		return string;
	}
}
