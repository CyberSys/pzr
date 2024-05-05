package zombie.popman;

import zombie.core.network.ByteBufferWriter;
import zombie.network.GameClient;
import zombie.network.PacketTypes;


final class DebugCommands {
	protected static final byte PKT_LOADED = 1;
	protected static final byte PKT_REPOP = 2;
	protected static final byte PKT_SPAWN_TIME_TO_ZERO = 3;
	protected static final byte PKT_CLEAR_ZOMBIES = 4;
	protected static final byte PKT_SPAWN_NOW = 5;

	private static native void n_debugCommand(int int1, int int2, int int3);

	public void SpawnTimeToZero(int int1, int int2) {
		if (ZombiePopulationManager.instance.bClient) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.doPacket((short)4, byteBufferWriter);
			byteBufferWriter.bb.put((byte)3);
			byteBufferWriter.bb.putShort((short)int1);
			byteBufferWriter.bb.putShort((short)int2);
			GameClient.connection.endPacket();
		} else {
			n_debugCommand(3, int1, int2);
		}
	}

	public void ClearZombies(int int1, int int2) {
		if (ZombiePopulationManager.instance.bClient) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.doPacket((short)4, byteBufferWriter);
			byteBufferWriter.bb.put((byte)4);
			byteBufferWriter.bb.putShort((short)int1);
			byteBufferWriter.bb.putShort((short)int2);
			GameClient.connection.endPacket();
		} else {
			n_debugCommand(4, int1, int2);
		}
	}

	public void SpawnNow(int int1, int int2) {
		if (ZombiePopulationManager.instance.bClient) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.doPacket((short)4, byteBufferWriter);
			byteBufferWriter.bb.put((byte)5);
			byteBufferWriter.bb.putShort((short)int1);
			byteBufferWriter.bb.putShort((short)int2);
			GameClient.connection.endPacket();
		} else {
			n_debugCommand(5, int1, int2);
		}
	}
}
