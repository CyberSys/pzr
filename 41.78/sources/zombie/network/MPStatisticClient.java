package zombie.network;

import java.util.Arrays;
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
	private float FPS = 0.0F;
	long lastRender = System.currentTimeMillis();
	short FPSAcc = 0;
	private float[] fpsArray = new float[1000];
	private short fpsArrayCount = 0;

	public static MPStatisticClient getInstance() {
		return instance;
	}

	public void incrementZombiesTeleports() {
		++this.zombiesTeleports;
	}

	public void incrementRemotePlayersTeleports() {
		++this.remotePlayersTeleports;
	}

	public float getFPS() {
		return this.FPS;
	}

	public void update() {
		if (this.needUpdate) {
			this.needUpdate = false;
			float float1;
			for (int int1 = 0; int1 < GameClient.IDToZombieMap.values().length; ++int1) {
				IsoZombie zombie = (IsoZombie)GameClient.IDToZombieMap.values()[int1];
				if (!zombie.isRemoteZombie()) {
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
		Object object = null;
		boolean boolean1 = false;
		float[] floatArray;
		short short1;
		synchronized (this.fpsArray) {
			floatArray = (float[])this.fpsArray.clone();
			Arrays.fill(this.fpsArray, 0, this.fpsArrayCount, 0.0F);
			short1 = this.fpsArrayCount;
			this.fpsArrayCount = 0;
		}
		float float1 = floatArray[0];
		float float2 = floatArray[0];
		float float3 = floatArray[0];
		short[] shortArray = new short[32];
		Arrays.fill(shortArray, (short)0);
		float float4;
		for (int int1 = 1; int1 < short1; ++int1) {
			float4 = floatArray[int1];
			if (float1 > float4) {
				float1 = float4;
			}

			if (float3 < float4) {
				float3 = float4;
			}

			float2 += float4;
		}

		float2 /= (float)short1;
		if (float2 < float1 + 16.0F) {
			float1 = float2 - 16.0F;
		}

		if (float3 < float2 + 16.0F) {
			float3 = float2 + 16.0F;
		}

		float float5 = (float2 - float1) / (float)(shortArray.length / 2);
		float4 = (float3 - float2) / (float)(shortArray.length / 2);
		int int2;
		for (int2 = 0; int2 < short1; ++int2) {
			float float6 = floatArray[int2];
			int int3;
			if (float6 < float2) {
				int3 = (int)Math.ceil((double)((float6 - float1) / float5));
				++shortArray[int3];
			}

			if (float6 >= float2) {
				int3 = (int)Math.ceil((double)((float6 - float2) / float4)) + shortArray.length / 2 - 1;
				++shortArray[int3];
			}
		}

		byteBufferWriter.putFloat(this.FPS);
		byteBufferWriter.putFloat(float1);
		byteBufferWriter.putFloat(float2);
		byteBufferWriter.putFloat(float3);
		for (int2 = 0; int2 < shortArray.length; ++int2) {
			byteBufferWriter.putShort(shortArray[int2]);
		}

		this.zombiesDesyncMax = 0.0F;
		this.zombiesTeleports = 0;
		this.remotePlayersDesyncMax = 0.0F;
		this.remotePlayersTeleports = 0;
		this.zombiesLocalOwnership = 0;
		this.needUpdate = true;
	}

	public void fpsProcess() {
		++this.FPSAcc;
		long long1 = System.currentTimeMillis();
		if (long1 - this.lastRender >= 1000L) {
			this.FPS = (float)this.FPSAcc;
			this.FPSAcc = 0;
			this.lastRender = long1;
			if (this.fpsArrayCount < this.fpsArray.length) {
				synchronized (this.fpsArray) {
					this.fpsArray[this.fpsArrayCount] = this.FPS;
					++this.fpsArrayCount;
				}
			}
		}
	}
}
