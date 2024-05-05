package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoWorld;
import zombie.iso.areas.NonPvpZone;
import zombie.util.StringUtils;


public class SyncNonPvpZonePacket implements INetworkPacket {
	public final NonPvpZone zone = new NonPvpZone();
	public boolean doRemove;

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.zone.load(byteBuffer, IsoWorld.getWorldVersion());
		this.doRemove = byteBuffer.get() == 1;
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		this.zone.save(byteBufferWriter.bb);
		byteBufferWriter.putBoolean(this.doRemove);
	}

	public boolean isConsistent() {
		return !StringUtils.isNullOrEmpty(this.zone.getTitle());
	}

	public String getDescription() {
		return String.format("\"%s\" remove=%b size=%d (%d;%d) (%d;%d)", this.zone.getTitle(), this.doRemove, this.zone.getSize(), this.zone.getX(), this.zone.getY(), this.zone.getX2(), this.zone.getY2());
	}

	public void process() {
		if (this.doRemove) {
			NonPvpZone.getAllZones().removeIf((var1)->{
				return var1.getTitle().equals(this.zone.getTitle());
			});
		} else if (NonPvpZone.getZoneByTitle(this.zone.getTitle()) == null) {
			NonPvpZone.getAllZones().add(this.zone);
		}
	}
}
