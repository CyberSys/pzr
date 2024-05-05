package zombie.network.packets;

import java.nio.ByteBuffer;
import java.util.Iterator;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoMovingObject;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.packets.hit.Square;


public class RemoveCorpseFromMap implements INetworkPacket {
	public Square position = new Square();
	private short id;
	private IsoDeadBody deadBody = null;

	public void set(IsoDeadBody deadBody) {
		this.position.set(deadBody.getSquare());
		this.id = deadBody.getOnlineID();
		this.deadBody = deadBody;
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.position.parse(byteBuffer, udpConnection);
		this.id = byteBuffer.getShort();
		this.deadBody = null;
		if (this.position.getSquare() != null) {
			Iterator iterator = this.position.getSquare().getStaticMovingObjects().iterator();
			while (iterator.hasNext()) {
				IsoMovingObject movingObject = (IsoMovingObject)iterator.next();
				if (movingObject instanceof IsoDeadBody && ((IsoDeadBody)movingObject).getOnlineID() == this.id) {
					this.deadBody = (IsoDeadBody)movingObject;
					break;
				}
			}
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		this.position.write(byteBufferWriter);
		byteBufferWriter.putShort(this.id);
	}

	public void process() {
		if (Core.bDebug) {
			String string = this.getDescription();
			if (!DebugLog.isEnabled(DebugType.Death)) {
				DebugLog.log(DebugType.Multiplayer, string);
			}

			DebugLog.log(DebugType.Death, string);
		}

		this.position.getSquare().removeCorpse(this.deadBody, true);
	}

	public String getDescription() {
		return String.format(this.getClass().getSimpleName() + " [ id=%d, position=%s ]", this.id, this.position.getDescription());
	}

	public boolean isConsistent() {
		if (!this.position.isConsistent()) {
			return false;
		} else {
			boolean boolean1 = false;
			Iterator iterator = this.position.getSquare().getStaticMovingObjects().iterator();
			while (iterator.hasNext()) {
				IsoMovingObject movingObject = (IsoMovingObject)iterator.next();
				if (movingObject instanceof IsoDeadBody && ((IsoDeadBody)movingObject).getOnlineID() == this.id) {
					boolean1 = true;
					break;
				}
			}

			return boolean1;
		}
	}
}
