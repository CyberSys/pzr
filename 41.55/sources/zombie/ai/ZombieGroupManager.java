package zombie.ai;

import java.util.ArrayDeque;
import java.util.ArrayList;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.ai.states.PathFindState;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.ZombieIdleState;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.ZombieGroup;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class ZombieGroupManager {
	public static final ZombieGroupManager instance = new ZombieGroupManager();
	private final ArrayList groups = new ArrayList();
	private final ArrayDeque freeGroups = new ArrayDeque();
	private final Vector2 tempVec2 = new Vector2();
	private final Vector3 tempVec3 = new Vector3();
	private float tickCount = 30.0F;

	public void preupdate() {
		if (!GameClient.bClient) {
			this.tickCount += GameTime.getInstance().getMultiplier() / 1.6F;
			if (this.tickCount >= 30.0F) {
				this.tickCount = 0.0F;
			}

			int int1 = SandboxOptions.instance.zombieConfig.RallyGroupSize.getValue();
			for (int int2 = 0; int2 < this.groups.size(); ++int2) {
				ZombieGroup zombieGroup = (ZombieGroup)this.groups.get(int2);
				zombieGroup.update();
				if (zombieGroup.isEmpty()) {
					this.freeGroups.push(zombieGroup);
					this.groups.remove(int2--);
				} else if (zombieGroup.size() < int1) {
				}
			}
		}
	}

	public void Reset() {
		this.freeGroups.addAll(this.groups);
		this.groups.clear();
	}

	public boolean shouldBeInGroup(IsoZombie zombie) {
		if (zombie == null) {
			return false;
		} else if (SandboxOptions.instance.zombieConfig.RallyGroupSize.getValue() <= 1) {
			return false;
		} else if (!Core.getInstance().isZombieGroupSound()) {
			return false;
		} else if (zombie.isUseless()) {
			return false;
		} else if (!zombie.isDead() && !zombie.isFakeDead()) {
			if (zombie.isSitAgainstWall()) {
				return false;
			} else if (zombie.target != null) {
				return false;
			} else if (zombie.getCurrentBuilding() != null) {
				return false;
			} else if (VirtualZombieManager.instance.isReused(zombie)) {
				return false;
			} else {
				IsoGridSquare square = zombie.getSquare();
				IsoMetaGrid.Zone zone = square == null ? null : square.getZone();
				return zone == null || !"Forest".equals(zone.getType()) && !"DeepForest".equals(zone.getType());
			}
		} else {
			return false;
		}
	}

	public void update(IsoZombie zombie) {
		if (!GameClient.bClient) {
			if (!this.shouldBeInGroup(zombie)) {
				if (zombie.group != null) {
					zombie.group.remove(zombie);
				}
			} else if (this.tickCount == 0.0F) {
				if (zombie.group == null) {
					ZombieGroup zombieGroup = this.findNearestGroup(zombie.getX(), zombie.getY(), zombie.getZ());
					if (zombieGroup == null) {
						zombieGroup = this.freeGroups.isEmpty() ? new ZombieGroup() : ((ZombieGroup)this.freeGroups.pop()).reset();
						zombieGroup.add(zombie);
						this.groups.add(zombieGroup);
						return;
					}

					zombieGroup.add(zombie);
				}

				if (zombie.getCurrentState() == ZombieIdleState.instance()) {
					int int1;
					float float1;
					if (zombie == zombie.group.getLeader()) {
						float1 = (float)GameTime.getInstance().getWorldAgeHours();
						zombie.group.lastSpreadOutTime = Math.min(zombie.group.lastSpreadOutTime, float1);
						if (!(zombie.group.lastSpreadOutTime + 0.083333336F > float1)) {
							zombie.group.lastSpreadOutTime = float1;
							int int2 = SandboxOptions.instance.zombieConfig.RallyGroupSeparation.getValue();
							Vector2 vector2 = this.tempVec2.set(0.0F, 0.0F);
							for (int1 = 0; int1 < this.groups.size(); ++int1) {
								ZombieGroup zombieGroup2 = (ZombieGroup)this.groups.get(int1);
								if (zombieGroup2.getLeader() != null && zombieGroup2 != zombie.group && (int)zombieGroup2.getLeader().getZ() == (int)zombie.getZ()) {
									float float2 = zombieGroup2.getLeader().getX();
									float float3 = zombieGroup2.getLeader().getY();
									float float4 = IsoUtils.DistanceToSquared(zombie.x, zombie.y, float2, float3);
									if (!(float4 > (float)(int2 * int2))) {
										vector2.x = vector2.x - float2 + zombie.x;
										vector2.y = vector2.y - float3 + zombie.y;
									}
								}
							}

							int1 = this.lineClearCollideCount(zombie, zombie.getCell(), (int)(zombie.x + vector2.x), (int)(zombie.y + vector2.y), (int)zombie.z, (int)zombie.x, (int)zombie.y, (int)zombie.z, 10, this.tempVec3);
							if (int1 >= 1) {
								if (GameClient.bClient || GameServer.bServer || !(IsoPlayer.getInstance().getHoursSurvived() < 2.0)) {
									if (!(this.tempVec3.x < 0.0F) && !(this.tempVec3.y < 0.0F) && IsoWorld.instance.MetaGrid.isValidChunk((int)this.tempVec3.x / 10, (int)this.tempVec3.y / 10)) {
										zombie.pathToLocation((int)(this.tempVec3.x + 0.5F), (int)(this.tempVec3.y + 0.5F), (int)this.tempVec3.z);
										if (zombie.getCurrentState() == PathFindState.instance() || zombie.getCurrentState() == WalkTowardState.instance()) {
											zombie.setLastHeardSound(zombie.getPathTargetX(), zombie.getPathTargetY(), zombie.getPathTargetZ());
											zombie.AllowRepathDelay = 400.0F;
										}
									}
								}
							}
						}
					} else {
						float1 = zombie.group.getLeader().getX();
						float float5 = zombie.group.getLeader().getY();
						int int3 = SandboxOptions.instance.zombieConfig.RallyGroupRadius.getValue();
						if (!(IsoUtils.DistanceToSquared(zombie.x, zombie.y, float1, float5) < (float)(int3 * int3))) {
							if (GameClient.bClient || GameServer.bServer || !(IsoPlayer.getInstance().getHoursSurvived() < 2.0) || Core.bDebug) {
								int1 = (int)(float1 + (float)Rand.Next(-int3, int3));
								int int4 = (int)(float5 + (float)Rand.Next(-int3, int3));
								if (int1 >= 0 && int4 >= 0 && IsoWorld.instance.MetaGrid.isValidChunk(int1 / 10, int4 / 10)) {
									zombie.pathToLocation(int1, int4, (int)zombie.group.getLeader().getZ());
									if (zombie.getCurrentState() == PathFindState.instance() || zombie.getCurrentState() == WalkTowardState.instance()) {
										zombie.setLastHeardSound(zombie.getPathTargetX(), zombie.getPathTargetY(), zombie.getPathTargetZ());
										zombie.AllowRepathDelay = 400.0F;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public ZombieGroup findNearestGroup(float float1, float float2, float float3) {
		ZombieGroup zombieGroup = null;
		float float4 = Float.MAX_VALUE;
		int int1 = SandboxOptions.instance.zombieConfig.RallyTravelDistance.getValue();
		for (int int2 = 0; int2 < this.groups.size(); ++int2) {
			ZombieGroup zombieGroup2 = (ZombieGroup)this.groups.get(int2);
			if (zombieGroup2.isEmpty()) {
				this.groups.remove(int2--);
			} else if ((int)zombieGroup2.getLeader().getZ() == (int)float3 && zombieGroup2.size() < SandboxOptions.instance.zombieConfig.RallyGroupSize.getValue()) {
				float float5 = IsoUtils.DistanceToSquared(float1, float2, zombieGroup2.getLeader().getX(), zombieGroup2.getLeader().getY());
				if (float5 < (float)(int1 * int1) && float5 < float4) {
					float4 = float5;
					zombieGroup = zombieGroup2;
				}
			}
		}

		return zombieGroup;
	}

	private int lineClearCollideCount(IsoMovingObject movingObject, IsoCell cell, int int1, int int2, int int3, int int4, int int5, int int6, int int7, Vector3 vector3) {
		int int8 = 0;
		int int9 = int2 - int5;
		int int10 = int1 - int4;
		int int11 = int3 - int6;
		float float1 = 0.5F;
		float float2 = 0.5F;
		IsoGridSquare square = cell.getGridSquare(int4, int5, int6);
		vector3.set((float)int4, (float)int5, (float)int6);
		int int12;
		int int13;
		float float3;
		float float4;
		IsoGridSquare square2;
		boolean boolean1;
		if (Math.abs(int10) > Math.abs(int9) && Math.abs(int10) > Math.abs(int11)) {
			float3 = (float)int9 / (float)int10;
			float4 = (float)int11 / (float)int10;
			float1 += (float)int5;
			float2 += (float)int6;
			int10 = int10 < 0 ? -1 : 1;
			float3 *= (float)int10;
			float4 *= (float)int10;
			while (int4 != int1) {
				int4 += int10;
				float1 += float3;
				float2 += float4;
				square2 = cell.getGridSquare(int4, (int)float1, (int)float2);
				if (square2 != null && square != null) {
					boolean1 = square2.testCollideAdjacent(movingObject, square.getX() - square2.getX(), square.getY() - square2.getY(), square.getZ() - square2.getZ());
					if (boolean1) {
						return int8;
					}
				}

				square = square2;
				int12 = (int)float1;
				int13 = (int)float2;
				vector3.set((float)int4, (float)int12, (float)int13);
				++int8;
				if (int8 >= int7) {
					return int8;
				}
			}
		} else {
			int int14;
			if (Math.abs(int9) >= Math.abs(int10) && Math.abs(int9) > Math.abs(int11)) {
				float3 = (float)int10 / (float)int9;
				float4 = (float)int11 / (float)int9;
				float1 += (float)int4;
				float2 += (float)int6;
				int9 = int9 < 0 ? -1 : 1;
				float3 *= (float)int9;
				float4 *= (float)int9;
				while (int5 != int2) {
					int5 += int9;
					float1 += float3;
					float2 += float4;
					square2 = cell.getGridSquare((int)float1, int5, (int)float2);
					if (square2 != null && square != null) {
						boolean1 = square2.testCollideAdjacent(movingObject, square.getX() - square2.getX(), square.getY() - square2.getY(), square.getZ() - square2.getZ());
						if (boolean1) {
							return int8;
						}
					}

					square = square2;
					int14 = (int)float1;
					int13 = (int)float2;
					vector3.set((float)int14, (float)int5, (float)int13);
					++int8;
					if (int8 >= int7) {
						return int8;
					}
				}
			} else {
				float3 = (float)int10 / (float)int11;
				float4 = (float)int9 / (float)int11;
				float1 += (float)int4;
				float2 += (float)int5;
				int11 = int11 < 0 ? -1 : 1;
				float3 *= (float)int11;
				float4 *= (float)int11;
				while (int6 != int3) {
					int6 += int11;
					float1 += float3;
					float2 += float4;
					square2 = cell.getGridSquare((int)float1, (int)float2, int6);
					if (square2 != null && square != null) {
						boolean1 = square2.testCollideAdjacent(movingObject, square.getX() - square2.getX(), square.getY() - square2.getY(), square.getZ() - square2.getZ());
						if (boolean1) {
							return int8;
						}
					}

					square = square2;
					int14 = (int)float1;
					int12 = (int)float2;
					vector3.set((float)int14, (float)int12, (float)int6);
					++int8;
					if (int8 >= int7) {
						return int8;
					}
				}
			}
		}

		return int8;
	}
}
