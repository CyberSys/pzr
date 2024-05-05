package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.skills.PerkFactory;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.packets.INetworkPacket;


public class Perk implements INetworkPacket {
	protected PerkFactory.Perk perk;
	protected byte perkIndex;

	public void set(PerkFactory.Perk perk) {
		this.perk = perk;
		if (this.perk == null) {
			this.perkIndex = -1;
		} else {
			this.perkIndex = (byte)this.perk.index();
		}
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.perkIndex = byteBuffer.get();
		if (this.perkIndex >= 0 && this.perkIndex <= PerkFactory.Perks.getMaxIndex()) {
			this.perk = PerkFactory.Perks.fromIndex(this.perkIndex);
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putByte(this.perkIndex);
	}

	public String getDescription() {
		String string = this.getClass().getSimpleName();
		return "\n\t" + string + " [ perk=( " + this.perkIndex + " )" + (this.perk == null ? "null" : this.perk.name) + " ]";
	}

	public boolean isConsistent() {
		return this.perk != null;
	}

	public PerkFactory.Perk getPerk() {
		return this.perk;
	}
}
