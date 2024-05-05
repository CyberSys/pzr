package zombie;

import java.util.ArrayList;
import java.util.HashMap;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoBuilding;


public final class FliesSound {
	public static final FliesSound instance = new FliesSound();
	private static final IsoGridSquare[] tempSquares = new IsoGridSquare[100];
	private final FliesSound.PlayerData[] playerData = new FliesSound.PlayerData[4];
	private final ArrayList fadeEmitters = new ArrayList();
	private float fliesVolume = -1.0F;

	public FliesSound() {
		for (int int1 = 0; int1 < this.playerData.length; ++int1) {
			this.playerData[int1] = new FliesSound.PlayerData();
		}
	}

	public void Reset() {
		for (int int1 = 0; int1 < this.playerData.length; ++int1) {
			this.playerData[int1].Reset();
		}
	}

	public void update() {
		if (SandboxOptions.instance.DecayingCorpseHealthImpact.getValue() != 1) {
			int int1;
			for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && player.getCurrentSquare() != null) {
					this.playerData[int1].update(player);
				}
			}

			for (int1 = 0; int1 < this.fadeEmitters.size(); ++int1) {
				FliesSound.FadeEmitter fadeEmitter = (FliesSound.FadeEmitter)this.fadeEmitters.get(int1);
				if (fadeEmitter.update()) {
					this.fadeEmitters.remove(int1--);
				}
			}
		}
	}

	public void render() {
		IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[0];
		for (int int1 = 0; int1 < IsoChunkMap.ChunkGridWidth; ++int1) {
			for (int int2 = 0; int2 < IsoChunkMap.ChunkGridWidth; ++int2) {
				IsoChunk chunk = chunkMap.getChunk(int2, int1);
				if (chunk != null) {
					FliesSound.ChunkData chunkData = chunk.corpseData;
					if (chunkData != null) {
						int int3 = (int)IsoPlayer.players[0].z;
						FliesSound.ChunkLevelData chunkLevelData = chunkData.levelData[int3];
						for (int int4 = 0; int4 < chunkLevelData.emitters.length; ++int4) {
							FliesSound.FadeEmitter fadeEmitter = chunkLevelData.emitters[int4];
							if (fadeEmitter != null && fadeEmitter.emitter != null) {
								this.paintSquare(fadeEmitter.sq.x, fadeEmitter.sq.y, fadeEmitter.sq.z, 0.0F, 1.0F, 0.0F, 1.0F);
							}

							if (chunkLevelData.refCount[int4] > 0) {
								this.paintSquare(chunk.wx * 10 + 5, chunk.wy * 10 + 5, 0, 0.0F, 0.0F, 1.0F, 1.0F);
							}
						}

						IsoBuilding building = IsoPlayer.players[0].getCurrentBuilding();
						if (building != null && chunkLevelData.buildingCorpseCount != null && chunkLevelData.buildingCorpseCount.containsKey(building)) {
							this.paintSquare(chunk.wx * 10 + 5, chunk.wy * 10 + 5, int3, 1.0F, 0.0F, 0.0F, 1.0F);
						}
					}
				}
			}
		}
	}

	private void paintSquare(int int1, int int2, int int3, float float1, float float2, float float3, float float4) {
		int int4 = Core.TileScale;
		int int5 = (int)IsoUtils.XToScreenExact((float)int1, (float)(int2 + 1), (float)int3, 0);
		int int6 = (int)IsoUtils.YToScreenExact((float)int1, (float)(int2 + 1), (float)int3, 0);
		SpriteRenderer.instance.renderPoly((float)int5, (float)int6, (float)(int5 + 32 * int4), (float)(int6 - 16 * int4), (float)(int5 + 64 * int4), (float)int6, (float)(int5 + 32 * int4), (float)(int6 + 16 * int4), float1, float2, float3, float4);
	}

	public void chunkLoaded(IsoChunk chunk) {
		if (chunk.corpseData == null) {
			chunk.corpseData = new FliesSound.ChunkData(chunk.wx, chunk.wy);
		}

		chunk.corpseData.wx = chunk.wx;
		chunk.corpseData.wy = chunk.wy;
		chunk.corpseData.Reset();
	}

	public void corpseAdded(int int1, int int2, int int3) {
		if (int3 >= 0 && int3 < 8) {
			FliesSound.ChunkData chunkData = this.getChunkData(int1, int2);
			if (chunkData != null) {
				chunkData.corpseAdded(int1, int2, int3);
				for (int int4 = 0; int4 < this.playerData.length; ++int4) {
					if (chunkData.levelData[int3].refCount[int4] > 0) {
						this.playerData[int4].forceUpdate = true;
					}
				}
			}
		} else {
			DebugLog.General.error("invalid z-coordinate %d,%d,%d", int1, int2, int3);
		}
	}

	public void corpseRemoved(int int1, int int2, int int3) {
		if (int3 >= 0 && int3 < 8) {
			FliesSound.ChunkData chunkData = this.getChunkData(int1, int2);
			if (chunkData != null) {
				chunkData.corpseRemoved(int1, int2, int3);
				for (int int4 = 0; int4 < this.playerData.length; ++int4) {
					if (chunkData.levelData[int3].refCount[int4] > 0) {
						this.playerData[int4].forceUpdate = true;
					}
				}
			}
		} else {
			DebugLog.General.error("invalid z-coordinate %d,%d,%d", int1, int2, int3);
		}
	}

	public int getCorpseCount(IsoGameCharacter gameCharacter) {
		return gameCharacter != null && gameCharacter.getCurrentSquare() != null ? this.getCorpseCount((int)gameCharacter.getX() / 10, (int)gameCharacter.getY() / 10, (int)gameCharacter.getZ(), gameCharacter.getBuilding()) : 0;
	}

	private int getCorpseCount(int int1, int int2, int int3, IsoBuilding building) {
		int int4 = 0;
		for (int int5 = -1; int5 <= 1; ++int5) {
			for (int int6 = -1; int6 <= 1; ++int6) {
				FliesSound.ChunkData chunkData = this.getChunkData((int1 + int6) * 10, (int2 + int5) * 10);
				if (chunkData != null) {
					FliesSound.ChunkLevelData chunkLevelData = chunkData.levelData[int3];
					if (building == null) {
						int4 += chunkLevelData.corpseCount;
					} else if (chunkLevelData.buildingCorpseCount != null) {
						Integer integer = (Integer)chunkLevelData.buildingCorpseCount.get(building);
						if (integer != null) {
							int4 += integer;
						}
					}
				}
			}
		}

		return int4;
	}

	private FliesSound.ChunkData getChunkData(int int1, int int2) {
		IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunkForGridSquare(int1, int2, 0);
		return chunk != null ? chunk.corpseData : null;
	}

	private class PlayerData {
		int wx = -1;
		int wy = -1;
		int z = -1;
		IsoBuilding building = null;
		boolean forceUpdate = false;

		PlayerData() {
		}

		boolean isSameLocation(IsoPlayer player) {
			IsoGridSquare square = player.getCurrentSquare();
			if (square != null && square.getBuilding() != this.building) {
				return false;
			} else {
				return (int)player.getX() / 10 == this.wx && (int)player.getY() / 10 == this.wy && (int)player.getZ() == this.z;
			}
		}

		void update(IsoPlayer player) {
			if (this.forceUpdate || !this.isSameLocation(player)) {
				this.forceUpdate = false;
				int int1 = this.wx;
				int int2 = this.wy;
				int int3 = this.z;
				IsoGridSquare square = player.getCurrentSquare();
				this.wx = square.getX() / 10;
				this.wy = square.getY() / 10;
				this.z = square.getZ();
				this.building = square.getBuilding();
				int int4;
				int int5;
				FliesSound.ChunkData chunkData;
				FliesSound.ChunkLevelData chunkLevelData;
				for (int4 = -1; int4 <= 1; ++int4) {
					for (int5 = -1; int5 <= 1; ++int5) {
						chunkData = FliesSound.this.getChunkData((this.wx + int5) * 10, (this.wy + int4) * 10);
						if (chunkData != null) {
							chunkLevelData = chunkData.levelData[this.z];
							chunkLevelData.update(this.wx + int5, this.wy + int4, this.z, player);
						}
					}
				}

				if (int3 != -1) {
					for (int4 = -1; int4 <= 1; ++int4) {
						for (int5 = -1; int5 <= 1; ++int5) {
							chunkData = FliesSound.this.getChunkData((int1 + int5) * 10, (int2 + int4) * 10);
							if (chunkData != null) {
								chunkLevelData = chunkData.levelData[int3];
								chunkLevelData.deref(player);
							}
						}
					}
				}
			}
		}

		void Reset() {
			this.wx = this.wy = this.z = -1;
			this.building = null;
			this.forceUpdate = false;
		}
	}

	private class FadeEmitter {
		private static final float FADE_IN_RATE = 0.01F;
		private static final float FADE_OUT_RATE = -0.01F;
		BaseSoundEmitter emitter = null;
		float volume = 1.0F;
		float targetVolume = 1.0F;
		IsoGridSquare sq = null;

		boolean update() {
			if (this.emitter == null) {
				return true;
			} else {
				if (this.volume < this.targetVolume) {
					this.volume += 0.01F * (GameTime.getInstance().getMultiplier() / 1.6F);
					if (this.volume >= this.targetVolume) {
						this.volume = this.targetVolume;
						return true;
					}
				} else {
					this.volume += -0.01F * (GameTime.getInstance().getMultiplier() / 1.6F);
					if (this.volume <= 0.0F) {
						this.volume = 0.0F;
						this.emitter.stopAll();
						this.emitter = null;
						return true;
					}
				}

				this.emitter.setVolumeAll(this.volume);
				return false;
			}
		}

		void Reset() {
			this.emitter = null;
			this.volume = 1.0F;
			this.targetVolume = 1.0F;
			this.sq = null;
		}
	}

	public class ChunkData {
		private int wx;
		private int wy;
		private final FliesSound.ChunkLevelData[] levelData = new FliesSound.ChunkLevelData[8];

		private ChunkData(int int1, int int2) {
			this.wx = int1;
			this.wy = int2;
			for (int int3 = 0; int3 < this.levelData.length; ++int3) {
				this.levelData[int3] = FliesSound.this.new ChunkLevelData();
			}
		}

		private void corpseAdded(int int1, int int2, int int3) {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			IsoBuilding building = square == null ? null : square.getBuilding();
			int int4 = int1 - this.wx * 10;
			int int5 = int2 - this.wy * 10;
			this.levelData[int3].corpseAdded(int4, int5, building);
		}

		private void corpseRemoved(int int1, int int2, int int3) {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			IsoBuilding building = square == null ? null : square.getBuilding();
			int int4 = int1 - this.wx * 10;
			int int5 = int2 - this.wy * 10;
			this.levelData[int3].corpseRemoved(int4, int5, building);
		}

		private void Reset() {
			for (int int1 = 0; int1 < this.levelData.length; ++int1) {
				this.levelData[int1].Reset();
			}
		}
	}

	private class ChunkLevelData {
		int corpseCount = 0;
		HashMap buildingCorpseCount = null;
		final int[] refCount = new int[4];
		final FliesSound.FadeEmitter[] emitters = new FliesSound.FadeEmitter[4];

		ChunkLevelData() {
		}

		void corpseAdded(int int1, int int2, IsoBuilding building) {
			if (building == null) {
				++this.corpseCount;
			} else {
				if (this.buildingCorpseCount == null) {
					this.buildingCorpseCount = new HashMap();
				}

				Integer integer = (Integer)this.buildingCorpseCount.get(building);
				if (integer == null) {
					this.buildingCorpseCount.put(building, 1);
				} else {
					this.buildingCorpseCount.put(building, integer + 1);
				}
			}
		}

		void corpseRemoved(int int1, int int2, IsoBuilding building) {
			if (building == null) {
				--this.corpseCount;
			} else if (this.buildingCorpseCount != null) {
				Integer integer = (Integer)this.buildingCorpseCount.get(building);
				if (integer != null) {
					if (integer > 1) {
						this.buildingCorpseCount.put(building, integer - 1);
					} else {
						this.buildingCorpseCount.remove(building);
					}
				}
			}
		}

		IsoGridSquare calcSoundPos(int int1, int int2, int int3, IsoBuilding building) {
			IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunkForGridSquare(int1 * 10, int2 * 10, int3);
			if (chunk == null) {
				return null;
			} else {
				int int4 = 0;
				for (int int5 = 0; int5 < 10; ++int5) {
					for (int int6 = 0; int6 < 10; ++int6) {
						IsoGridSquare square = chunk.getGridSquare(int6, int5, int3);
						if (square != null && !square.getStaticMovingObjects().isEmpty() && square.getBuilding() == building) {
							FliesSound.tempSquares[int4++] = square;
						}
					}
				}

				if (int4 > 0) {
					return FliesSound.tempSquares[int4 / 2];
				} else {
					return null;
				}
			}
		}

		void update(int int1, int int2, int int3, IsoPlayer player) {
			int int4 = this.refCount[player.PlayerIndex]++;
			int int5 = FliesSound.this.getCorpseCount(int1, int2, int3, player.getCurrentBuilding());
			if ((double)BodyDamage.getSicknessFromCorpsesRate(int5) > ZomboidGlobals.FoodSicknessDecrease) {
				IsoBuilding building = player.getCurrentBuilding();
				IsoGridSquare square = this.calcSoundPos(int1, int2, int3, building);
				if (square == null) {
					return;
				}

				if (this.emitters[player.PlayerIndex] == null) {
					this.emitters[player.PlayerIndex] = FliesSound.this.new FadeEmitter();
				}

				FliesSound.FadeEmitter fadeEmitter = this.emitters[player.PlayerIndex];
				if (fadeEmitter.emitter == null) {
					fadeEmitter.emitter = IsoWorld.instance.getFreeEmitter((float)square.x, (float)square.y, (float)int3);
					fadeEmitter.emitter.playSoundLoopedImpl("CorpseFlies");
					fadeEmitter.emitter.setVolumeAll(0.0F);
					fadeEmitter.volume = 0.0F;
					FliesSound.this.fadeEmitters.add(fadeEmitter);
				} else {
					fadeEmitter.sq.setHasFlies(false);
					fadeEmitter.emitter.setPos((float)square.x, (float)square.y, (float)int3);
					if (fadeEmitter.targetVolume != 1.0F && !FliesSound.this.fadeEmitters.contains(fadeEmitter)) {
						FliesSound.this.fadeEmitters.add(fadeEmitter);
					}
				}

				fadeEmitter.targetVolume = 1.0F;
				fadeEmitter.sq = square;
				square.setHasFlies(true);
			} else {
				FliesSound.FadeEmitter fadeEmitter2 = this.emitters[player.PlayerIndex];
				if (fadeEmitter2 != null && fadeEmitter2.emitter != null) {
					if (!FliesSound.this.fadeEmitters.contains(fadeEmitter2)) {
						FliesSound.this.fadeEmitters.add(fadeEmitter2);
					}

					fadeEmitter2.targetVolume = 0.0F;
					fadeEmitter2.sq.setHasFlies(false);
				}
			}
		}

		void deref(IsoPlayer player) {
			int int1 = player.PlayerIndex;
			int int2 = this.refCount[int1]--;
			if (this.refCount[int1] <= 0) {
				if (this.emitters[int1] != null && this.emitters[int1].emitter != null) {
					if (!FliesSound.this.fadeEmitters.contains(this.emitters[int1])) {
						FliesSound.this.fadeEmitters.add(this.emitters[int1]);
					}

					this.emitters[int1].targetVolume = 0.0F;
					this.emitters[int1].sq.setHasFlies(false);
				}
			}
		}

		void Reset() {
			this.corpseCount = 0;
			if (this.buildingCorpseCount != null) {
				this.buildingCorpseCount.clear();
			}

			for (int int1 = 0; int1 < 4; ++int1) {
				this.refCount[int1] = 0;
				if (this.emitters[int1] != null) {
					this.emitters[int1].Reset();
				}
			}
		}
	}
}
