package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.network.packets.INetworkPacket;


public class PlayerHitSquarePacket extends PlayerHitPacket implements INetworkPacket {
	protected final Square square = new Square();

	public PlayerHitSquarePacket() {
		super(HitCharacterPacket.HitType.PlayerHitSquare);
	}

	public void set(IsoPlayer player, HandWeapon handWeapon, boolean boolean1) {
		super.set(player, handWeapon, boolean1);
		this.square.set((IsoGameCharacter)player);
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		super.parse(byteBuffer, udpConnection);
		this.square.parse(byteBuffer, udpConnection);
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		super.write(byteBufferWriter);
		this.square.write(byteBufferWriter);
	}

	public boolean isRelevant(UdpConnection udpConnection) {
		return this.wielder.isRelevant(udpConnection);
	}

	public boolean isConsistent() {
		return super.isConsistent() && this.square.isConsistent();
	}

	public String getDescription() {
		String string = super.getDescription();
		return string + "\n\tSquare " + this.square.getDescription();
	}

	protected void process() {
		this.square.process(this.wielder.getCharacter());
	}

	public boolean validate(UdpConnection udpConnection) {
		return true;
	}
}
