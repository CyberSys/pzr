package zombie.iso;

import java.util.ArrayList;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.characters.IsoPlayer;
import zombie.core.opengl.RenderSettings;
import zombie.iso.areas.IsoBuilding;


public class IsoLightSource {
	public static int NextID = 1;
	public int ID;
	public int x;
	public int y;
	public int z;
	public float r;
	public float g;
	public float b;
	public float rJNI;
	public float gJNI;
	public float bJNI;
	public int radius;
	public boolean bActive;
	public boolean bWasActive;
	public boolean bActiveJNI;
	public int life = -1;
	public int startlife = -1;
	public IsoBuilding localToBuilding;
	public boolean bHydroPowered = false;
	public ArrayList switches = new ArrayList(0);
	public IsoChunk chunk;
	public Object lightMap;

	public IsoLightSource(int int1, int int2, int int3, float float1, float float2, float float3, int int4) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.r = float1;
		this.g = float2;
		this.b = float3;
		this.radius = int4;
		this.bActive = true;
	}

	public IsoLightSource(int int1, int int2, int int3, float float1, float float2, float float3, int int4, IsoBuilding building) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.r = float1;
		this.g = float2;
		this.b = float3;
		this.radius = int4;
		this.bActive = true;
		this.localToBuilding = building;
	}

	public IsoLightSource(int int1, int int2, int int3, float float1, float float2, float float3, int int4, int int5) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.r = float1;
		this.g = float2;
		this.b = float3;
		this.radius = int4;
		this.bActive = true;
		this.startlife = this.life = int5;
	}

	public void update() {
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, this.z);
		if (!this.bHydroPowered || GameTime.instance.NightsSurvived < SandboxOptions.instance.getElecShutModifier() || square != null && square.haveElectricity()) {
			if (this.bActive) {
				if (this.localToBuilding != null) {
					this.r = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
					this.g = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
					this.b = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
				}

				if (this.life > 0) {
					--this.life;
				}

				if (this.localToBuilding != null && square != null) {
					this.r = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8F * IsoGridSquare.rmod * 0.7F;
					this.g = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8F * IsoGridSquare.gmod * 0.7F;
					this.b = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8F * IsoGridSquare.bmod * 0.7F;
				}

				for (int int1 = this.x - this.radius; int1 < this.x + this.radius; ++int1) {
					for (int int2 = this.y - this.radius; int2 < this.y + this.radius; ++int2) {
						for (int int3 = 0; int3 < 8; ++int3) {
							square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
							if (square != null && (this.localToBuilding == null || this.localToBuilding == square.getBuilding())) {
								LosUtil.TestResults testResults = LosUtil.lineClear(square.getCell(), this.x, this.y, this.z, square.getX(), square.getY(), square.getZ(), false);
								if (square.getX() == this.x && square.getY() == this.y && square.getZ() == this.z || testResults != LosUtil.TestResults.Blocked) {
									float float1 = 0.0F;
									float float2;
									if (Math.abs(square.getZ() - this.z) <= 1) {
										float2 = IsoUtils.DistanceTo((float)this.x, (float)this.y, 0.0F, (float)square.getX(), (float)square.getY(), 0.0F);
									} else {
										float2 = IsoUtils.DistanceTo((float)this.x, (float)this.y, (float)this.z, (float)square.getX(), (float)square.getY(), (float)square.getZ());
									}

									if (!(float2 > (float)this.radius)) {
										float1 = float2 / (float)this.radius;
										float1 = 1.0F - float1;
										float1 *= float1;
										if (this.life > -1) {
											float1 *= (float)this.life / (float)this.startlife;
										}

										float float3 = float1 * this.r * 2.0F;
										float float4 = float1 * this.g * 2.0F;
										float float5 = float1 * this.b * 2.0F;
										square.setLampostTotalR(square.getLampostTotalR() + float3);
										square.setLampostTotalG(square.getLampostTotalG() + float4);
										square.setLampostTotalB(square.getLampostTotalB() + float5);
									}
								}
							}
						}
					}
				}
			}
		} else {
			this.bActive = false;
		}
	}

	public int getX() {
		return this.x;
	}

	public void setX(int int1) {
		this.x = int1;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int int1) {
		this.y = int1;
	}

	public int getZ() {
		return this.z;
	}

	public void setZ(int int1) {
		this.z = int1;
	}

	public float getR() {
		return this.r;
	}

	public void setR(float float1) {
		this.r = float1;
	}

	public float getG() {
		return this.g;
	}

	public void setG(float float1) {
		this.g = float1;
	}

	public float getB() {
		return this.b;
	}

	public void setB(float float1) {
		this.b = float1;
	}

	public int getRadius() {
		return this.radius;
	}

	public void setRadius(int int1) {
		this.radius = int1;
	}

	public boolean isActive() {
		return this.bActive;
	}

	public void setActive(boolean boolean1) {
		this.bActive = boolean1;
	}

	public boolean wasActive() {
		return this.bWasActive;
	}

	public void setWasActive(boolean boolean1) {
		this.bWasActive = boolean1;
	}

	public ArrayList getSwitches() {
		return this.switches;
	}

	public void setSwitches(ArrayList arrayList) {
		this.switches = arrayList;
	}

	public void clearInfluence() {
		for (int int1 = this.x - this.radius; int1 < this.x + this.radius; ++int1) {
			for (int int2 = this.y - this.radius; int2 < this.y + this.radius; ++int2) {
				for (int int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
					if (square != null) {
						square.setLampostTotalR(0.0F);
						square.setLampostTotalG(0.0F);
						square.setLampostTotalB(0.0F);
					}
				}
			}
		}
	}

	public boolean isInBounds(int int1, int int2, int int3, int int4) {
		return this.x >= int1 && this.x < int3 && this.y >= int2 && this.y < int4;
	}

	public boolean isInBounds() {
		IsoChunkMap[] chunkMapArray = IsoWorld.instance.CurrentCell.ChunkMap;
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			if (!chunkMapArray[int1].ignore) {
				int int2 = chunkMapArray[int1].getWorldXMinTiles();
				int int3 = chunkMapArray[int1].getWorldXMaxTiles();
				int int4 = chunkMapArray[int1].getWorldYMinTiles();
				int int5 = chunkMapArray[int1].getWorldYMaxTiles();
				if (this.isInBounds(int2, int4, int3, int5)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isHydroPowered() {
		return this.bHydroPowered;
	}

	public IsoBuilding getLocalToBuilding() {
		return this.localToBuilding;
	}
}
