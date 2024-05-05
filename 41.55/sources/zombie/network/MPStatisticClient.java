package zombie.network;

import java.util.Iterator;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.iso.IsoUtils;


public class MPStatisticClient {
	public static MPStatisticClient instance = new MPStatisticClient();
	private boolean needUpdate = true;
	private int zombiesLocalOwnership = 0;
	private float zombiesDesyncAVG = 0.0F;
	private float zombiesDesyncMax = 0.0F;
	private int zombiesTeleports = 0;
	private float remotePlayersDesyncAVG = 0.0F;
	private float remotePlayersDesyncMax = 0.0F;
	private int remotePlayersTeleports = 0;

	public static MPStatisticClient getInstance() {
		return instance;
	}

	public void incrementZombiesTeleports() {
		++this.zombiesTeleports;
	}

	public void incrementRemotePlayersTeleports() {
		++this.remotePlayersTeleports;
	}

	public void update() {
		if (this.needUpdate) {
			this.needUpdate = false;
			float float1;
			for (int int1 = 0; int1 < GameClient.IDToZombieMap.values().length; ++int1) {
				IsoZombie zombie = (IsoZombie)GameClient.IDToZombieMap.values()[int1];
				if (zombie.networkAI.isLocalControl()) {
					++this.zombiesLocalOwnership;
				} else {
					float1 = IsoUtils.DistanceTo(zombie.x, zombie.y, zombie.z, zombie.realx, zombie.realy, (float)zombie.realz);
					this.zombiesDesyncAVG += (float1 - this.zombiesDesyncAVG) * 0.05F;
					if (float1 > this.zombiesDesyncMax) {
						this.zombiesDesyncMax = float1;
					}
				}
			}

			Iterator iterator = GameClient.IDToPlayerMap.values().iterator();
			while (iterator.hasNext()) {
				IsoPlayer player = (IsoPlayer)iterator.next();
				if (!player.isLocalPlayer()) {
					float1 = IsoUtils.DistanceTo(player.x, player.y, player.z, player.realx, player.realy, (float)player.realz);
					this.remotePlayersDesyncAVG += (float1 - this.remotePlayersDesyncAVG) * 0.05F;
					if (float1 > this.remotePlayersDesyncMax) {
						this.remotePlayersDesyncMax = float1;
					}
				}
			}
		}
	}

	public void send(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putInt(GameClient.IDToZombieMap.size());
		byteBufferWriter.putInt(this.zombiesLocalOwnership);
		byteBufferWriter.putFloat(this.zombiesDesyncAVG);
		byteBufferWriter.putFloat(this.zombiesDesyncMax);
		byteBufferWriter.putInt(this.zombiesTeleports);
		byteBufferWriter.putInt(GameClient.IDToPlayerMap.size());
		byteBufferWriter.putFloat(this.remotePlayersDesyncAVG);
		byteBufferWriter.putFloat(this.remotePlayersDesyncMax);
		byteBufferWriter.putInt(this.remotePlayersTeleports);
		this.zombiesDesyncMax = 0.0F;
		this.zombiesTeleports = 0;
		this.remotePlayersDesyncMax = 0.0F;
		this.remotePlayersTeleports = 0;
		this.zombiesLocalOwnership = 0;
		this.needUpdate = true;
	}
}
