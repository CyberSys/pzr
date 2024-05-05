package zombie;

import java.util.ArrayList;
import java.util.Stack;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.popman.MPDebugInfo;
import zombie.popman.ZombiePopulationManager;


public final class WorldSoundManager {
	public static final WorldSoundManager instance = new WorldSoundManager();
	public final ArrayList SoundList = new ArrayList();
	private final Stack freeSounds = new Stack();
	private static final WorldSoundManager.ResultBiggestSound resultBiggestSound = new WorldSoundManager.ResultBiggestSound();

	public void init(IsoCell cell) {
	}

	public void initFrame() {
	}

	public void KillCell() {
		this.freeSounds.addAll(this.SoundList);
		this.SoundList.clear();
	}

	public WorldSoundManager.WorldSound getNew() {
		return this.freeSounds.isEmpty() ? new WorldSoundManager.WorldSound() : (WorldSoundManager.WorldSound)this.freeSounds.pop();
	}

	public WorldSoundManager.WorldSound addSound(Object object, int int1, int int2, int int3, int int4, int int5) {
		return this.addSound(object, int1, int2, int3, int4, int5, false, 0.0F, 1.0F);
	}

	public WorldSoundManager.WorldSound addSound(Object object, int int1, int int2, int int3, int int4, int int5, boolean boolean1) {
		return this.addSound(object, int1, int2, int3, int4, int5, boolean1, 0.0F, 1.0F);
	}

	public WorldSoundManager.WorldSound addSound(Object object, int int1, int int2, int int3, int int4, int int5, boolean boolean1, float float1, float float2) {
		return this.addSound(object, int1, int2, int3, int4, int5, boolean1, float1, float2, false, true, false);
	}

	public WorldSoundManager.WorldSound addSound(Object object, int int1, int int2, int int3, int int4, int int5, boolean boolean1, float float1, float float2, boolean boolean2, boolean boolean3, boolean boolean4) {
		if (int4 <= 0) {
			return null;
		} else {
			WorldSoundManager.WorldSound worldSound;
			synchronized (this.SoundList) {
				worldSound = this.getNew().init(object, int1, int2, int3, int4, int5, boolean1, float1, float2);
				if (object == null) {
					worldSound.sourceIsZombie = boolean2;
				}

				if (!GameServer.bServer) {
					int int6 = SandboxOptions.instance.Lore.Hearing.getValue();
					if (int6 == 4) {
						int6 = 1;
					}

					int int7 = (int)PZMath.ceil((float)int4 * this.getHearingMultiplier(int6));
					int int8 = (int1 - int7) / 10;
					int int9 = (int2 - int7) / 10;
					int int10 = (int)Math.ceil((double)(((float)int1 + (float)int7) / 10.0F));
					int int11 = (int)Math.ceil((double)(((float)int2 + (float)int7) / 10.0F));
					for (int int12 = int8; int12 < int10; ++int12) {
						for (int int13 = int9; int13 < int11; ++int13) {
							IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunk(int12, int13);
							if (chunk != null) {
								chunk.SoundList.add(worldSound);
							}
						}
					}
				}

				this.SoundList.add(worldSound);
				ZombiePopulationManager.instance.addWorldSound(worldSound, boolean3);
			}

			if (boolean3) {
				if (GameClient.bClient) {
					GameClient.instance.sendWorldSound(worldSound);
				} else if (GameServer.bServer) {
					GameServer.sendWorldSound((WorldSoundManager.WorldSound)worldSound, (UdpConnection)null);
				}
			}

			if (Core.bDebug && GameClient.bClient) {
				MPDebugInfo.AddDebugSound(worldSound);
			}

			return worldSound;
		}
	}

	public WorldSoundManager.WorldSound addSoundRepeating(Object object, int int1, int int2, int int3, int int4, int int5, boolean boolean1) {
		WorldSoundManager.WorldSound worldSound = this.addSound(object, int1, int2, int3, int4, int5, boolean1, 0.0F, 1.0F);
		if (worldSound != null) {
			worldSound.bRepeating = true;
		}

		return worldSound;
	}

	public WorldSoundManager.WorldSound getSoundZomb(IsoZombie zombie) {
		IsoChunk chunk = null;
		if (zombie.soundSourceTarget == null) {
			return null;
		} else if (zombie.getCurrentSquare() == null) {
			return null;
		} else {
			chunk = zombie.getCurrentSquare().chunk;
			ArrayList arrayList = null;
			if (chunk != null && !GameServer.bServer) {
				arrayList = chunk.SoundList;
			} else {
				arrayList = this.SoundList;
			}

			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				WorldSoundManager.WorldSound worldSound = (WorldSoundManager.WorldSound)arrayList.get(int1);
				if (zombie.soundSourceTarget == worldSound.source) {
					return worldSound;
				}
			}

			return null;
		}
	}

	public WorldSoundManager.ResultBiggestSound getBiggestSoundZomb(int int1, int int2, int int3, boolean boolean1, IsoZombie zombie) {
		float float1 = -1000000.0F;
		WorldSoundManager.WorldSound worldSound = null;
		IsoChunk chunk = null;
		if (zombie != null) {
			if (zombie.getCurrentSquare() == null) {
				return resultBiggestSound.init((WorldSoundManager.WorldSound)null, 0.0F);
			}

			chunk = zombie.getCurrentSquare().chunk;
		}

		ArrayList arrayList = null;
		if (chunk != null && !GameServer.bServer) {
			arrayList = chunk.SoundList;
		} else {
			arrayList = this.SoundList;
		}

		for (int int4 = 0; int4 < arrayList.size(); ++int4) {
			WorldSoundManager.WorldSound worldSound2 = (WorldSoundManager.WorldSound)arrayList.get(int4);
			if (worldSound2 != null && worldSound2.radius != 0) {
				float float2 = IsoUtils.DistanceToSquared((float)int1, (float)int2, (float)worldSound2.x, (float)worldSound2.y);
				float float3 = (float)worldSound2.radius * this.getHearingMultiplier(zombie);
				if (!(float2 > float3 * float3) && (!(float2 < worldSound2.zombieIgnoreDist * worldSound2.zombieIgnoreDist) || int3 != worldSound2.z) && (!boolean1 || !worldSound2.sourceIsZombie)) {
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(worldSound2.x, worldSound2.y, worldSound2.z);
					IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
					float float4 = float2 / (float3 * float3);
					if (square != null && square2 != null && square.getRoom() != square2.getRoom()) {
						float4 *= 1.2F;
						if (square2.getRoom() == null || square.getRoom() == null) {
							float4 *= 1.4F;
						}
					}

					float4 = 1.0F - float4;
					if (!(float4 <= 0.0F)) {
						if (float4 > 1.0F) {
							float4 = 1.0F;
						}

						float float5 = (float)worldSound2.volume * float4;
						if (float5 > float1) {
							float1 = float5;
							worldSound = worldSound2;
						}
					}
				}
			}
		}

		return resultBiggestSound.init(worldSound, float1);
	}

	public float getSoundAttract(WorldSoundManager.WorldSound worldSound, IsoZombie zombie) {
		if (worldSound == null) {
			return 0.0F;
		} else if (worldSound.radius == 0) {
			return 0.0F;
		} else {
			float float1 = IsoUtils.DistanceToSquared(zombie.x, zombie.y, (float)worldSound.x, (float)worldSound.y);
			float float2 = (float)worldSound.radius * this.getHearingMultiplier(zombie);
			if (float1 > float2 * float2) {
				return 0.0F;
			} else if (float1 < worldSound.zombieIgnoreDist * worldSound.zombieIgnoreDist && zombie.z == (float)worldSound.z) {
				return 0.0F;
			} else if (worldSound.sourceIsZombie) {
				return 0.0F;
			} else {
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(worldSound.x, worldSound.y, worldSound.z);
				IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare((double)zombie.x, (double)zombie.y, (double)zombie.z);
				float float3 = float1 / (float2 * float2);
				if (square != null && square2 != null && square.getRoom() != square2.getRoom()) {
					float3 *= 1.2F;
					if (square2.getRoom() == null || square.getRoom() == null) {
						float3 *= 1.4F;
					}
				}

				float3 = 1.0F - float3;
				if (float3 <= 0.0F) {
					return 0.0F;
				} else {
					if (float3 > 1.0F) {
						float3 = 1.0F;
					}

					float float4 = (float)worldSound.volume * float3;
					return float4;
				}
			}
		}
	}

	public float getStressFromSounds(int int1, int int2, int int3) {
		float float1 = 0.0F;
		for (int int4 = 0; int4 < this.SoundList.size(); ++int4) {
			WorldSoundManager.WorldSound worldSound = (WorldSoundManager.WorldSound)this.SoundList.get(int4);
			if (worldSound.stresshumans && worldSound.radius != 0) {
				float float2 = IsoUtils.DistanceManhatten((float)int1, (float)int2, (float)worldSound.x, (float)worldSound.y);
				float float3 = float2 / (float)worldSound.radius;
				float3 = 1.0F - float3;
				if (!(float3 <= 0.0F)) {
					if (float3 > 1.0F) {
						float3 = 1.0F;
					}

					float float4 = float3 * worldSound.stressMod;
					float1 += float4;
				}
			}
		}

		return float1;
	}

	public void update() {
		int int1;
		if (!GameServer.bServer) {
			for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int1];
				if (!chunkMap.ignore) {
					for (int int2 = 0; int2 < IsoChunkMap.ChunkGridWidth; ++int2) {
						for (int int3 = 0; int3 < IsoChunkMap.ChunkGridWidth; ++int3) {
							IsoChunk chunk = chunkMap.getChunk(int3, int2);
							if (chunk != null) {
								chunk.updateSounds();
							}
						}
					}
				}
			}
		}

		int1 = this.SoundList.size();
		for (int int4 = 0; int4 < int1; ++int4) {
			WorldSoundManager.WorldSound worldSound = (WorldSoundManager.WorldSound)this.SoundList.get(int4);
			if (worldSound != null && worldSound.life > 0) {
				--worldSound.life;
			} else {
				this.SoundList.remove(int4);
				this.freeSounds.push(worldSound);
				--int4;
				--int1;
			}
		}
	}

	public void render() {
		if (Core.bDebug && DebugOptions.instance.WorldSoundRender.getValue()) {
			if (!GameClient.bClient) {
				if (!GameServer.bServer || ServerGUI.isCreated()) {
					int int1 = SandboxOptions.instance.Lore.Hearing.getValue();
					if (int1 == 4) {
						int1 = 2;
					}

					float float1 = this.getHearingMultiplier(int1);
					for (int int2 = 0; int2 < this.SoundList.size(); ++int2) {
						WorldSoundManager.WorldSound worldSound = (WorldSoundManager.WorldSound)this.SoundList.get(int2);
						float float2 = (float)worldSound.radius * float1;
						for (double double1 = 0.0; double1 < 6.283185307179586; double1 += 0.15707963267948966) {
							this.DrawIsoLine((float)worldSound.x + float2 * (float)Math.cos(double1), (float)worldSound.y + float2 * (float)Math.sin(double1), (float)worldSound.x + float2 * (float)Math.cos(double1 + 0.15707963267948966), (float)worldSound.y + float2 * (float)Math.sin(double1 + 0.15707963267948966), (float)worldSound.z, 1.0F, 1.0F, 1.0F, 1.0F, 1);
						}
					}

					if (!GameServer.bServer) {
						IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.getChunkMap(0);
						if (chunkMap != null && !chunkMap.ignore) {
							for (int int3 = 0; int3 < IsoChunkMap.ChunkGridWidth; ++int3) {
								for (int int4 = 0; int4 < IsoChunkMap.ChunkGridWidth; ++int4) {
									IsoChunk chunk = chunkMap.getChunk(int4, int3);
									if (chunk != null) {
										for (int int5 = 0; int5 < chunk.SoundList.size(); ++int5) {
											WorldSoundManager.WorldSound worldSound2 = (WorldSoundManager.WorldSound)chunk.SoundList.get(int5);
											float float3 = (float)worldSound2.radius * float1;
											for (double double2 = 0.0; double2 < 6.283185307179586; double2 += 0.15707963267948966) {
												this.DrawIsoLine((float)worldSound2.x + float3 * (float)Math.cos(double2), (float)worldSound2.y + float3 * (float)Math.sin(double2), (float)worldSound2.x + float3 * (float)Math.cos(double2 + 0.15707963267948966), (float)worldSound2.y + float3 * (float)Math.sin(double2 + 0.15707963267948966), (float)worldSound2.z, 0.0F, 1.0F, 1.0F, 1.0F, 1);
												float float4 = (float)(chunk.wx * 10) + 0.1F;
												float float5 = (float)(chunk.wy * 10) + 0.1F;
												float float6 = (float)((chunk.wx + 1) * 10) - 0.1F;
												float float7 = (float)((chunk.wy + 1) * 10) - 0.1F;
												this.DrawIsoLine(float4, float5, float6, float5, (float)worldSound2.z, 0.0F, 1.0F, 1.0F, 1.0F, 1);
												this.DrawIsoLine(float6, float5, float6, float7, (float)worldSound2.z, 0.0F, 1.0F, 1.0F, 1.0F, 1);
												this.DrawIsoLine(float6, float7, float4, float7, (float)worldSound2.z, 0.0F, 1.0F, 1.0F, 1.0F, 1);
												this.DrawIsoLine(float4, float7, float4, float5, (float)worldSound2.z, 0.0F, 1.0F, 1.0F, 1.0F, 1);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void DrawIsoLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, int int1) {
		float float10 = IsoUtils.XToScreenExact(float1, float2, float5, 0);
		float float11 = IsoUtils.YToScreenExact(float1, float2, float5, 0);
		float float12 = IsoUtils.XToScreenExact(float3, float4, float5, 0);
		float float13 = IsoUtils.YToScreenExact(float3, float4, float5, 0);
		LineDrawer.drawLine(float10, float11, float12, float13, float6, float7, float8, float9, int1);
	}

	public float getHearingMultiplier(IsoZombie zombie) {
		return zombie == null ? this.getHearingMultiplier(2) : this.getHearingMultiplier(zombie.hearing);
	}

	public float getHearingMultiplier(int int1) {
		if (int1 == 1) {
			return 3.0F;
		} else {
			return int1 == 3 ? 0.45F : 1.0F;
		}
	}

	public class WorldSound {
		public Object source = null;
		public int life = 1;
		public int radius;
		public boolean stresshumans;
		public int volume;
		public int x;
		public int y;
		public int z;
		public float zombieIgnoreDist = 0.0F;
		public boolean sourceIsZombie;
		public float stressMod = 1.0F;
		public boolean bRepeating;

		public WorldSoundManager.WorldSound init(Object object, int int1, int int2, int int3, int int4, int int5) {
			return this.init(object, int1, int2, int3, int4, int5, false, 0.0F, 1.0F);
		}

		public WorldSoundManager.WorldSound init(Object object, int int1, int int2, int int3, int int4, int int5, boolean boolean1) {
			return this.init(object, int1, int2, int3, int4, int5, boolean1, 0.0F, 1.0F);
		}

		public WorldSoundManager.WorldSound init(Object object, int int1, int int2, int int3, int int4, int int5, boolean boolean1, float float1, float float2) {
			this.source = object;
			this.life = 1;
			this.x = int1;
			this.y = int2;
			this.z = int3;
			this.radius = int4;
			this.volume = int5;
			this.stresshumans = boolean1;
			this.zombieIgnoreDist = float1;
			this.stressMod = float2;
			this.sourceIsZombie = object instanceof IsoZombie;
			this.bRepeating = false;
			LuaEventManager.triggerEvent("OnWorldSound", int1, int2, int3, int4, int5, object);
			return this;
		}

		public WorldSoundManager.WorldSound init(boolean boolean1, int int1, int int2, int int3, int int4, int int5, boolean boolean2, float float1, float float2) {
			WorldSoundManager.WorldSound worldSound = this.init((Object)null, int1, int2, int3, int4, int5, boolean2, float1, float2);
			worldSound.sourceIsZombie = boolean1;
			return worldSound;
		}
	}

	public static final class ResultBiggestSound {
		public WorldSoundManager.WorldSound sound;
		public float attract;

		public WorldSoundManager.ResultBiggestSound init(WorldSoundManager.WorldSound worldSound, float float1) {
			this.sound = worldSound;
			this.attract = float1;
			return this;
		}
	}
}
