package zombie.iso;

import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.characters.IsoPlayer;
import zombie.core.opengl.RenderSettings;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.areas.IsoRoom;


public final class IsoRoomLight {
	public static int NextID = 1;
	private static int SHINE_DIST = 5;
	public int ID;
	public IsoRoom room;
	public int x;
	public int y;
	public int z;
	public int width;
	public int height;
	public float r;
	public float g;
	public float b;
	public boolean bActive;
	public boolean bActiveJNI;
	public boolean bHydroPowered = true;

	public IsoRoomLight(IsoRoom room, int int1, int int2, int int3, int int4, int int5) {
		this.room = room;
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.width = int4;
		this.height = int5;
		this.r = 0.9F;
		this.b = 0.8F;
		this.b = 0.7F;
		this.bActive = room.def.bLightsActive;
	}

	public void addInfluence() {
		this.r = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8F * IsoGridSquare.rmod * 0.7F;
		this.g = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8F * IsoGridSquare.gmod * 0.7F;
		this.b = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8F * IsoGridSquare.bmod * 0.7F;
		this.r *= 2.0F;
		this.g *= 2.0F;
		this.b *= 2.0F;
		this.shineIn(this.x - 1, this.y, this.x, this.y + this.height, SHINE_DIST, 0);
		this.shineIn(this.x, this.y - 1, this.x + this.width, this.y, 0, SHINE_DIST);
		this.shineIn(this.x + this.width, this.y, this.x + this.width + 1, this.y + this.height, -SHINE_DIST, 0);
		this.shineIn(this.x, this.y + this.height, this.x + this.width, this.y + this.height + 1, 0, -SHINE_DIST);
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, this.z);
		this.bActive = this.room.def.bLightsActive;
		if (this.bHydroPowered && GameTime.instance.NightsSurvived >= SandboxOptions.instance.getElecShutModifier() && (square == null || !square.haveElectricity())) {
			this.bActive = false;
		} else if (this.bActive) {
			this.r = 0.9F;
			this.g = 0.8F;
			this.b = 0.7F;
			for (int int1 = this.y; int1 < this.y + this.height; ++int1) {
				for (int int2 = this.x; int2 < this.x + this.width; ++int2) {
					square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int1, this.z);
					if (square != null) {
						square.setLampostTotalR(square.getLampostTotalR() + this.r);
						square.setLampostTotalG(square.getLampostTotalG() + this.g);
						square.setLampostTotalB(square.getLampostTotalB() + this.b);
					}
				}
			}

			this.shineOut(this.x, this.y, this.x + 1, this.y + this.height, -SHINE_DIST, 0);
			this.shineOut(this.x, this.y, this.x + this.width, this.y + 1, 0, -SHINE_DIST);
			this.shineOut(this.x + this.width - 1, this.y, this.x + this.width, this.y + this.height, SHINE_DIST, 0);
			this.shineOut(this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, 0, SHINE_DIST);
		}
	}

	private void shineOut(int int1, int int2, int int3, int int4, int int5, int int6) {
		for (int int7 = int2; int7 < int4; ++int7) {
			for (int int8 = int1; int8 < int3; ++int8) {
				this.shineOut(int8, int7, int5, int6);
			}
		}
	}

	private void shineOut(int int1, int int2, int int3, int int4) {
		int int5;
		if (int3 > 0) {
			for (int5 = 1; int5 <= int3; ++int5) {
				this.shineFromTo(int1, int2, int1 + int5, int2);
			}
		} else if (int3 < 0) {
			for (int5 = 1; int5 <= -int3; ++int5) {
				this.shineFromTo(int1, int2, int1 - int5, int2);
			}
		} else if (int4 > 0) {
			for (int5 = 1; int5 <= int4; ++int5) {
				this.shineFromTo(int1, int2, int1, int2 + int5);
			}
		} else if (int4 < 0) {
			for (int5 = 1; int5 <= -int4; ++int5) {
				this.shineFromTo(int1, int2, int1, int2 - int5);
			}
		}
	}

	private void shineFromTo(int int1, int int2, int int3, int int4) {
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int3, int4, this.z);
		if (square != null) {
			if (square.getRoom() != this.room) {
				LosUtil.TestResults testResults = LosUtil.lineClear(IsoWorld.instance.CurrentCell, int1, int2, this.z, int3, int4, this.z, false);
				if (testResults != LosUtil.TestResults.Blocked) {
					float float1 = (float)(Math.abs(int1 - int3) + Math.abs(int2 - int4));
					float float2 = float1 / (float)SHINE_DIST;
					float2 = 1.0F - float2;
					float2 *= float2;
					float float3 = float2 * this.r * 2.0F;
					float float4 = float2 * this.g * 2.0F;
					float float5 = float2 * this.b * 2.0F;
					square.setLampostTotalR(square.getLampostTotalR() + float3);
					square.setLampostTotalG(square.getLampostTotalG() + float4);
					square.setLampostTotalB(square.getLampostTotalB() + float5);
				}
			}
		}
	}

	private void shineIn(int int1, int int2, int int3, int int4, int int5, int int6) {
		for (int int7 = int2; int7 < int4; ++int7) {
			for (int int8 = int1; int8 < int3; ++int8) {
				this.shineIn(int8, int7, int5, int6);
			}
		}
	}

	private void shineIn(int int1, int int2, int int3, int int4) {
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, this.z);
		if (square != null && square.Is(IsoFlagType.exterior)) {
			int int5;
			if (int3 > 0) {
				for (int5 = 1; int5 <= int3; ++int5) {
					this.shineFromToIn(int1, int2, int1 + int5, int2);
				}
			} else if (int3 < 0) {
				for (int5 = 1; int5 <= -int3; ++int5) {
					this.shineFromToIn(int1, int2, int1 - int5, int2);
				}
			} else if (int4 > 0) {
				for (int5 = 1; int5 <= int4; ++int5) {
					this.shineFromToIn(int1, int2, int1, int2 + int5);
				}
			} else if (int4 < 0) {
				for (int5 = 1; int5 <= -int4; ++int5) {
					this.shineFromToIn(int1, int2, int1, int2 - int5);
				}
			}
		}
	}

	private void shineFromToIn(int int1, int int2, int int3, int int4) {
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int3, int4, this.z);
		if (square != null) {
			LosUtil.TestResults testResults = LosUtil.lineClear(IsoWorld.instance.CurrentCell, int1, int2, this.z, int3, int4, this.z, false);
			if (testResults != LosUtil.TestResults.Blocked) {
				float float1 = (float)(Math.abs(int1 - int3) + Math.abs(int2 - int4));
				float float2 = float1 / (float)SHINE_DIST;
				float2 = 1.0F - float2;
				float2 *= float2;
				float float3 = float2 * this.r * 2.0F;
				float float4 = float2 * this.g * 2.0F;
				float float5 = float2 * this.b * 2.0F;
				square.setLampostTotalR(square.getLampostTotalR() + float3);
				square.setLampostTotalG(square.getLampostTotalG() + float4);
				square.setLampostTotalB(square.getLampostTotalB() + float5);
			}
		}
	}

	public void clearInfluence() {
		for (int int1 = this.y - SHINE_DIST; int1 < this.y + this.height + SHINE_DIST; ++int1) {
			for (int int2 = this.x - SHINE_DIST; int2 < this.x + this.width + SHINE_DIST; ++int2) {
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int1, this.z);
				if (square != null) {
					square.setLampostTotalR(0.0F);
					square.setLampostTotalG(0.0F);
					square.setLampostTotalB(0.0F);
				}
			}
		}
	}

	public boolean isInBounds() {
		IsoChunkMap[] chunkMapArray = IsoWorld.instance.CurrentCell.ChunkMap;
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			if (!chunkMapArray[int1].ignore) {
				int int2 = chunkMapArray[int1].getWorldXMinTiles();
				int int3 = chunkMapArray[int1].getWorldXMaxTiles();
				int int4 = chunkMapArray[int1].getWorldYMinTiles();
				int int5 = chunkMapArray[int1].getWorldYMaxTiles();
				if (this.x - SHINE_DIST < int3 && this.x + this.width + SHINE_DIST > int2 && this.y - SHINE_DIST < int5 && this.y + this.height + SHINE_DIST > int4) {
					return true;
				}
			}
		}

		return false;
	}
}
