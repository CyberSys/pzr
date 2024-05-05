package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoWorld;


public final class ParameterZoneWaterSide extends FMODGlobalParameter {
	private int m_playerX = -1;
	private int m_playerY = -1;
	private int m_distance = 40;

	public ParameterZoneWaterSide() {
		super("ZoneWaterSide");
	}

	public float calculateCurrentValue() {
		IsoGameCharacter gameCharacter = this.getCharacter();
		if (gameCharacter == null) {
			return 40.0F;
		} else {
			int int1 = (int)gameCharacter.getX();
			int int2 = (int)gameCharacter.getY();
			if (int1 != this.m_playerX || int2 != this.m_playerY) {
				this.m_playerX = int1;
				this.m_playerY = int2;
				this.m_distance = this.calculate(gameCharacter);
				if (this.m_distance < 40) {
					this.m_distance = PZMath.clamp(this.m_distance - 5, 0, 40);
				}
			}

			return (float)this.m_distance;
		}
	}

	private int calculate(IsoGameCharacter gameCharacter) {
		if (IsoWorld.instance != null && IsoWorld.instance.CurrentCell != null && IsoWorld.instance.CurrentCell.ChunkMap[0] != null) {
			IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[0];
			float float1 = Float.MAX_VALUE;
			for (int int1 = 0; int1 < IsoChunkMap.ChunkGridWidth; ++int1) {
				for (int int2 = 0; int2 < IsoChunkMap.ChunkGridWidth; ++int2) {
					IsoChunk chunk = chunkMap.getChunk(int2, int1);
					if (chunk != null && chunk.getNumberOfWaterTiles() == 100) {
						float float2 = (float)(chunk.wx * 10) + 5.0F;
						float float3 = (float)(chunk.wy * 10) + 5.0F;
						float float4 = gameCharacter.x - float2;
						float float5 = gameCharacter.y - float3;
						if (float4 * float4 + float5 * float5 < float1) {
							float1 = float4 * float4 + float5 * float5;
						}
					}
				}
			}

			return (int)PZMath.clamp(PZMath.sqrt(float1), 0.0F, 40.0F);
		} else {
			return 40;
		}
	}

	private IsoGameCharacter getCharacter() {
		IsoPlayer player = null;
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoPlayer player2 = IsoPlayer.players[int1];
			if (player2 != null && (player == null || player.isDead() && player2.isAlive())) {
				player = player2;
			}
		}

		return player;
	}
}
