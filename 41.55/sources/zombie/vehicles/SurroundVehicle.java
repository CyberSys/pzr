package zombie.vehicles;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.joml.Vector2f;
import org.joml.Vector3f;
import zombie.VirtualZombieManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.iso.IsoUtils;
import zombie.popman.ObjectPool;
import zombie.scripting.objects.VehicleScript;
import zombie.util.Type;


public final class SurroundVehicle {
	private static final ObjectPool s_positionPool = new ObjectPool(SurroundVehicle.Position::new);
	private static final Vector3f s_tempVector3f = new Vector3f();
	private final BaseVehicle m_vehicle;
	public float x1;
	public float y1;
	public float x2;
	public float y2;
	public float x3;
	public float y3;
	public float x4;
	public float y4;
	private float x1p;
	private float y1p;
	private float x2p;
	private float y2p;
	private float x3p;
	private float y3p;
	private float x4p;
	private float y4p;
	private boolean m_bMoved = false;
	private final ArrayList m_positions = new ArrayList();
	private long m_updateMS = 0L;

	public SurroundVehicle(BaseVehicle baseVehicle) {
		Objects.requireNonNull(baseVehicle);
		this.m_vehicle = baseVehicle;
	}

	private void calcPositionsLocal() {
		s_positionPool.release((List)this.m_positions);
		this.m_positions.clear();
		VehicleScript vehicleScript = this.m_vehicle.getScript();
		if (vehicleScript != null) {
			Vector3f vector3f = vehicleScript.getExtents();
			Vector3f vector3f2 = vehicleScript.getCenterOfMassOffset();
			float float1 = vector3f.x;
			float float2 = vector3f.z;
			float float3 = 0.005F;
			float float4 = BaseVehicle.PLUS_RADIUS + float3;
			float float5 = vector3f2.x - float1 / 2.0F - float4;
			float float6 = vector3f2.z - float2 / 2.0F - float4;
			float float7 = vector3f2.x + float1 / 2.0F + float4;
			float float8 = vector3f2.z + float2 / 2.0F + float4;
			this.addPositions(float5, vector3f2.z - float2 / 2.0F, float5, vector3f2.z + float2 / 2.0F, SurroundVehicle.PositionSide.Right);
			this.addPositions(float7, vector3f2.z - float2 / 2.0F, float7, vector3f2.z + float2 / 2.0F, SurroundVehicle.PositionSide.Left);
			this.addPositions(float5, float6, float7, float6, SurroundVehicle.PositionSide.Rear);
			this.addPositions(float5, float8, float7, float8, SurroundVehicle.PositionSide.Front);
		}
	}

	private void addPositions(float float1, float float2, float float3, float float4, SurroundVehicle.PositionSide positionSide) {
		Vector3f vector3f = this.m_vehicle.getPassengerLocalPos(0, s_tempVector3f);
		if (vector3f != null) {
			float float5 = 0.3F;
			float float6;
			float float7;
			float float8;
			if (positionSide != SurroundVehicle.PositionSide.Left && positionSide != SurroundVehicle.PositionSide.Right) {
				float6 = 0.0F;
				float7 = float2;
				for (float8 = float6; float8 >= float1 + float5; float8 -= float5 * 2.0F) {
					this.addPosition(float8, float7, positionSide);
				}

				for (float8 = float6 + float5 * 2.0F; float8 < float3 - float5; float8 += float5 * 2.0F) {
					this.addPosition(float8, float7, positionSide);
				}
			} else {
				float6 = float1;
				float7 = vector3f.z;
				for (float8 = float7; float8 >= float2 + float5; float8 -= float5 * 2.0F) {
					this.addPosition(float6, float8, positionSide);
				}

				for (float8 = float7 + float5 * 2.0F; float8 < float4 - float5; float8 += float5 * 2.0F) {
					this.addPosition(float6, float8, positionSide);
				}
			}
		}
	}

	private SurroundVehicle.Position addPosition(float float1, float float2, SurroundVehicle.PositionSide positionSide) {
		SurroundVehicle.Position position = (SurroundVehicle.Position)s_positionPool.alloc();
		position.posLocal.set(float1, float2);
		position.side = positionSide;
		this.m_positions.add(position);
		return position;
	}

	private void calcPositionsWorld() {
		for (int int1 = 0; int1 < this.m_positions.size(); ++int1) {
			SurroundVehicle.Position position = (SurroundVehicle.Position)this.m_positions.get(int1);
			this.m_vehicle.getWorldPos(position.posLocal.x, 0.0F, position.posLocal.y, position.posWorld);
			switch (position.side) {
			case Front: 
			
			case Rear: 
				this.m_vehicle.getWorldPos(position.posLocal.x, 0.0F, 0.0F, position.posAxis);
				break;
			
			case Left: 
			
			case Right: 
				this.m_vehicle.getWorldPos(0.0F, 0.0F, position.posLocal.y, position.posAxis);
			
			}
		}

		PolygonalMap2.VehiclePoly vehiclePoly = this.m_vehicle.getPoly();
		this.x1p = vehiclePoly.x1;
		this.x2p = vehiclePoly.x2;
		this.x3p = vehiclePoly.x3;
		this.x4p = vehiclePoly.x4;
		this.y1p = vehiclePoly.y1;
		this.y2p = vehiclePoly.y2;
		this.y3p = vehiclePoly.y3;
		this.y4p = vehiclePoly.y4;
	}

	private SurroundVehicle.Position getClosestPositionFor(IsoZombie zombie) {
		if (zombie != null && zombie.getTarget() != null) {
			float float1 = Float.MAX_VALUE;
			SurroundVehicle.Position position = null;
			for (int int1 = 0; int1 < this.m_positions.size(); ++int1) {
				SurroundVehicle.Position position2 = (SurroundVehicle.Position)this.m_positions.get(int1);
				if (!position2.bBlocked) {
					float float2 = IsoUtils.DistanceToSquared(zombie.x, zombie.y, position2.posWorld.x, position2.posWorld.y);
					float float3;
					if (position2.isOccupied()) {
						float3 = IsoUtils.DistanceToSquared(position2.zombie.x, position2.zombie.y, position2.posWorld.x, position2.posWorld.y);
						if (float3 < float2) {
							continue;
						}
					}

					float3 = IsoUtils.DistanceToSquared(zombie.getTarget().x, zombie.getTarget().y, position2.posWorld.x, position2.posWorld.y);
					if (float3 < float1) {
						float1 = float3;
						position = position2;
					}
				}
			}

			return position;
		} else {
			return null;
		}
	}

	public Vector2f getPositionForZombie(IsoZombie zombie, Vector2f vector2f) {
		if ((!zombie.isOnFloor() || zombie.isCanWalk()) && (int)zombie.getZ() == (int)this.m_vehicle.getZ()) {
			float float1 = IsoUtils.DistanceToSquared(zombie.x, zombie.y, this.m_vehicle.x, this.m_vehicle.y);
			if (float1 > 100.0F) {
				return vector2f.set(this.m_vehicle.x, this.m_vehicle.y);
			} else {
				if (this.checkPosition()) {
					this.m_bMoved = true;
				}

				for (int int1 = 0; int1 < this.m_positions.size(); ++int1) {
					SurroundVehicle.Position position = (SurroundVehicle.Position)this.m_positions.get(int1);
					if (position.bBlocked) {
						position.zombie = null;
					}

					if (position.zombie == zombie) {
						return vector2f.set(position.posWorld.x, position.posWorld.y);
					}
				}

				SurroundVehicle.Position position2 = this.getClosestPositionFor(zombie);
				if (position2 == null) {
					return null;
				} else {
					position2.zombie = zombie;
					position2.targetX = zombie.getTarget().x;
					position2.targetY = zombie.getTarget().y;
					return vector2f.set(position2.posWorld.x, position2.posWorld.y);
				}
			}
		} else {
			return vector2f.set(this.m_vehicle.x, this.m_vehicle.y);
		}
	}

	private boolean checkPosition() {
		if (this.m_vehicle.getScript() == null) {
			return false;
		} else {
			if (this.m_positions.isEmpty()) {
				this.calcPositionsLocal();
				this.x1 = -1.0F;
			}

			PolygonalMap2.VehiclePoly vehiclePoly = this.m_vehicle.getPoly();
			if (this.x1 == vehiclePoly.x1 && this.x2 == vehiclePoly.x2 && this.x3 == vehiclePoly.x3 && this.x4 == vehiclePoly.x4 && this.y1 == vehiclePoly.y1 && this.y2 == vehiclePoly.y2 && this.y3 == vehiclePoly.y3 && this.y4 == vehiclePoly.y4) {
				return false;
			} else {
				this.x1 = vehiclePoly.x1;
				this.x2 = vehiclePoly.x2;
				this.x3 = vehiclePoly.x3;
				this.x4 = vehiclePoly.x4;
				this.y1 = vehiclePoly.y1;
				this.y2 = vehiclePoly.y2;
				this.y3 = vehiclePoly.y3;
				this.y4 = vehiclePoly.y4;
				this.calcPositionsWorld();
				return true;
			}
		}
	}

	private boolean movedSincePositionsWereCalculated() {
		PolygonalMap2.VehiclePoly vehiclePoly = this.m_vehicle.getPoly();
		return this.x1p != vehiclePoly.x1 || this.x2p != vehiclePoly.x2 || this.x3p != vehiclePoly.x3 || this.x4p != vehiclePoly.x4 || this.y1p != vehiclePoly.y1 || this.y2p != vehiclePoly.y2 || this.y3p != vehiclePoly.y3 || this.y4p != vehiclePoly.y4;
	}

	private boolean hasOccupiedPositions() {
		for (int int1 = 0; int1 < this.m_positions.size(); ++int1) {
			SurroundVehicle.Position position = (SurroundVehicle.Position)this.m_positions.get(int1);
			if (position.zombie != null) {
				return true;
			}
		}

		return false;
	}

	public void update() {
		if (this.hasOccupiedPositions() && this.checkPosition()) {
			this.m_bMoved = true;
		}

		long long1 = System.currentTimeMillis();
		if (long1 - this.m_updateMS >= 1000L) {
			this.m_updateMS = long1;
			if (this.m_bMoved) {
				this.m_bMoved = false;
				for (int int1 = 0; int1 < this.m_positions.size(); ++int1) {
					SurroundVehicle.Position position = (SurroundVehicle.Position)this.m_positions.get(int1);
					position.zombie = null;
				}
			}

			boolean boolean1 = this.movedSincePositionsWereCalculated();
			for (int int2 = 0; int2 < this.m_positions.size(); ++int2) {
				SurroundVehicle.Position position2 = (SurroundVehicle.Position)this.m_positions.get(int2);
				if (!boolean1) {
					position2.checkBlocked(this.m_vehicle);
				}

				if (position2.zombie != null) {
					float float1 = IsoUtils.DistanceToSquared(position2.zombie.x, position2.zombie.y, this.m_vehicle.x, this.m_vehicle.y);
					if (float1 > 100.0F) {
						position2.zombie = null;
					} else {
						IsoGameCharacter gameCharacter = (IsoGameCharacter)Type.tryCastTo(position2.zombie.getTarget(), IsoGameCharacter.class);
						if (!position2.zombie.isDead() && !VirtualZombieManager.instance.isReused(position2.zombie) && !position2.zombie.isOnFloor() && gameCharacter != null && this.m_vehicle.getSeat(gameCharacter) != -1) {
							if (IsoUtils.DistanceToSquared(position2.targetX, position2.targetY, gameCharacter.x, gameCharacter.y) > 0.1F) {
								position2.zombie = null;
							}
						} else {
							position2.zombie = null;
						}
					}
				}
			}
		}
	}

	public void render() {
		if (this.hasOccupiedPositions()) {
			for (int int1 = 0; int1 < this.m_positions.size(); ++int1) {
				SurroundVehicle.Position position = (SurroundVehicle.Position)this.m_positions.get(int1);
				Vector3f vector3f = position.posWorld;
				float float1 = 1.0F;
				float float2 = 1.0F;
				float float3 = 1.0F;
				if (position.isOccupied()) {
					float3 = 0.0F;
					float1 = 0.0F;
				} else if (position.bBlocked) {
					float3 = 0.0F;
					float2 = 0.0F;
				}

				this.m_vehicle.getController().drawCircle(vector3f.x, vector3f.y, 0.3F, float1, float2, float3, 1.0F);
			}
		}
	}

	public void reset() {
		s_positionPool.release((List)this.m_positions);
		this.m_positions.clear();
	}

	private static enum PositionSide {

		Front,
		Rear,
		Left,
		Right;

		private static SurroundVehicle.PositionSide[] $values() {
			return new SurroundVehicle.PositionSide[]{Front, Rear, Left, Right};
		}
	}

	private static final class Position {
		final Vector2f posLocal = new Vector2f();
		final Vector3f posWorld = new Vector3f();
		final Vector3f posAxis = new Vector3f();
		SurroundVehicle.PositionSide side;
		IsoZombie zombie;
		float targetX;
		float targetY;
		boolean bBlocked;

		boolean isOccupied() {
			return this.zombie != null;
		}

		void checkBlocked(BaseVehicle baseVehicle) {
			this.bBlocked = PolygonalMap2.instance.lineClearCollide(this.posWorld.x, this.posWorld.y, this.posAxis.x, this.posAxis.y, (int)baseVehicle.z, baseVehicle);
			if (!this.bBlocked) {
				this.bBlocked = !PolygonalMap2.instance.canStandAt(this.posWorld.x, this.posWorld.y, (int)baseVehicle.z, baseVehicle, false, false);
			}
		}
	}
}
