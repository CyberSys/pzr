package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoFireManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketValidator;
import zombie.network.ServerOptions;
import zombie.network.packets.hit.Square;


public class StartFire implements INetworkPacket {
	protected final Square square = new Square();
	protected int fireEnergy;
	protected boolean ignite;
	protected int life;
	protected boolean smoke;
	protected int spreadDelay;
	protected int numParticles;

	public void set(IsoGridSquare square, boolean boolean1, int int1, int int2, boolean boolean2) {
		this.square.set(square);
		this.fireEnergy = int1;
		this.ignite = boolean1;
		this.life = int2;
		this.smoke = boolean2;
		this.spreadDelay = 0;
		this.numParticles = 0;
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.square.parse(byteBuffer, udpConnection);
		this.fireEnergy = byteBuffer.getInt();
		this.ignite = byteBuffer.get() == 1;
		this.life = byteBuffer.getInt();
		this.smoke = byteBuffer.get() == 1;
		if (GameClient.bClient) {
			this.spreadDelay = byteBuffer.getInt();
			this.numParticles = byteBuffer.getInt();
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		this.square.write(byteBufferWriter);
		byteBufferWriter.putInt(this.fireEnergy);
		byteBufferWriter.putBoolean(this.ignite);
		byteBufferWriter.putInt(this.life);
		byteBufferWriter.putBoolean(this.smoke);
		if (GameServer.bServer) {
			byteBufferWriter.putInt(this.spreadDelay);
			byteBufferWriter.putInt(this.numParticles);
		}
	}

	public void process() {
		IsoFire fire;
		if (GameServer.bServer) {
			fire = this.smoke ? new IsoFire(this.square.getSquare().getCell(), this.square.getSquare(), this.ignite, this.fireEnergy, this.life, true) : new IsoFire(this.square.getSquare().getCell(), this.square.getSquare(), this.ignite, this.fireEnergy, this.life);
			IsoFireManager.Add(fire);
			this.spreadDelay = fire.getSpreadDelay();
			this.numParticles = fire.numFlameParticles;
			this.square.getSquare().getObjects().add(fire);
		}

		if (GameClient.bClient) {
			fire = this.smoke ? new IsoFire(IsoWorld.instance.CurrentCell, this.square.getSquare(), this.ignite, this.fireEnergy, this.life, true) : new IsoFire(IsoWorld.instance.CurrentCell, this.square.getSquare(), this.ignite, this.fireEnergy, this.life);
			fire.SpreadDelay = this.spreadDelay;
			fire.numFlameParticles = this.numParticles;
			IsoFireManager.Add(fire);
			this.square.getSquare().getObjects().add(fire);
		}
	}

	public boolean isConsistent() {
		return this.square.getSquare() != null && this.life <= 500;
	}

	public boolean validate(UdpConnection udpConnection) {
		if (GameServer.bServer && !this.smoke && ServerOptions.instance.NoFire.getValue()) {
			if (ServerOptions.instance.AntiCheatProtectionType16.getValue() && PacketValidator.checkUser(udpConnection)) {
				PacketValidator.doKickUser(udpConnection, this.getClass().getSimpleName(), "Type16", this.getDescription());
			}

			return false;
		} else if (!this.smoke && !IsoFire.CanAddFire(this.square.getSquare(), this.ignite, this.smoke)) {
			float float1 = this.square.getX();
			DebugLog.log("not adding fire that on " + float1 + "," + this.square.getY());
			if (ServerOptions.instance.AntiCheatProtectionType17.getValue() && PacketValidator.checkUser(udpConnection)) {
				PacketValidator.doKickUser(udpConnection, this.getClass().getSimpleName(), "Type17", this.getDescription());
			}

			return false;
		} else if (this.smoke && !IsoFire.CanAddSmoke(this.square.getSquare(), this.ignite)) {
			if (ServerOptions.instance.AntiCheatProtectionType18.getValue() && PacketValidator.checkUser(udpConnection)) {
				PacketValidator.doKickUser(udpConnection, this.getClass().getSimpleName(), "Type18", this.getDescription());
			}

			return false;
		} else {
			return GameClient.bClient || udpConnection.RelevantTo(this.square.getX(), this.square.getY());
		}
	}

	public String getDescription() {
		String string = "\n\t" + this.getClass().getSimpleName() + " [";
		string = string + "square=" + this.square.getDescription() + " | ";
		string = string + "fireEnergy=" + this.fireEnergy + " | ";
		string = string + "ignite=" + this.ignite + " | ";
		string = string + "life=" + this.life + " | ";
		string = string + "smoke=" + this.smoke + " | ";
		string = string + "spreadDelay=" + this.spreadDelay + " | ";
		string = string + "numParticles=" + this.numParticles + "] ";
		return string;
	}
}
