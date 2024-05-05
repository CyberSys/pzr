package zombie.network;

import java.nio.ByteBuffer;
import zombie.core.utils.UpdateLimit;
import zombie.network.packets.ZombieUpdateInfoPacket;


public class ZombieUpdatePacker {
	public static final ZombieUpdatePacker instance = new ZombieUpdatePacker();
	private static final UpdateLimit s_zombieInfoUpdateRate = new UpdateLimit(200L);
	private final ZombieUpdateInfoPacket m_packetInfo = new ZombieUpdateInfoPacket();

	public void receivePacket(ByteBuffer byteBuffer) {
		this.m_packetInfo.receive(byteBuffer);
	}

	public void postupdate() {
		if (s_zombieInfoUpdateRate.Check()) {
			this.m_packetInfo.send();
		}
	}

	public void clearPacket() {
		this.m_packetInfo.clear();
	}
}
