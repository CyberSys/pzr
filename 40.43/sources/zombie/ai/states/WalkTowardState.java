package zombie.ai.states;

import org.joml.Vector3f;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameServer;


public class WalkTowardState extends State {
	static WalkTowardState _instance = new WalkTowardState();
	Vector2 temp = new Vector2();
	int turnTimer = 0;
	float previousX;
	float previousY = 0.0F;

	public static WalkTowardState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		this.previousX = 0.0F;
		this.previousY = 0.0F;
		this.turnTimer = 0;
		if (gameCharacter instanceof IsoZombie && ((IsoZombie)gameCharacter).isUseless()) {
			gameCharacter.getStateMachine().changeState(ZombieStandState.instance());
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (gameCharacter instanceof IsoZombie) {
			if (SandboxOptions.instance.Lore.ActiveOnly.getValue() > 1) {
				if ((SandboxOptions.instance.Lore.ActiveOnly.getValue() != 2 || GameTime.instance.getHour() < 20 && GameTime.instance.getHour() > 8) && (SandboxOptions.instance.Lore.ActiveOnly.getValue() != 3 || GameTime.instance.getHour() <= 8 || GameTime.instance.getHour() >= 20)) {
					((IsoZombie)gameCharacter).walkVariant = "ZombieWalk1";
					((IsoZombie)gameCharacter).bRunning = false;
					((IsoZombie)gameCharacter).speedType = 3;
					((IsoZombie)gameCharacter).speedMod = 0.1F;
					((IsoZombie)gameCharacter).def.setFrameSpeedPerFrame(0.24F);
					IsoSpriteInstance spriteInstance = ((IsoZombie)gameCharacter).def;
					spriteInstance.AnimFrameIncrease *= ((IsoZombie)gameCharacter).speedMod;
					((IsoZombie)gameCharacter).inactive = true;
				} else if (((IsoZombie)gameCharacter).speedMod == 0.1F) {
					((IsoZombie)gameCharacter).inactive = false;
					if (SandboxOptions.instance.Lore.Speed.getValue() != 4) {
						((IsoZombie)gameCharacter).changeSpeed(SandboxOptions.instance.Lore.Speed.getValue());
					} else {
						((IsoZombie)gameCharacter).changeSpeed(Rand.Next(1, 4));
					}
				}
			}

			if (!((IsoZombie)gameCharacter).bCrawling) {
				gameCharacter.setOnFloor(false);
			}

			IsoZombie zombie = (IsoZombie)gameCharacter;
			if (this.previousX == gameCharacter.getX() && this.previousY == gameCharacter.getY()) {
			}

			this.previousX = gameCharacter.getX();
			this.previousY = gameCharacter.getY();
			zombie.DoNetworkDirty();
			if (zombie.target != null) {
				zombie.getPathFindBehavior2().pathToCharacter((IsoGameCharacter)zombie.target);
				if (zombie.target instanceof IsoGameCharacter && ((IsoGameCharacter)zombie.target).getVehicle() != null && zombie.DistToSquared(zombie.target) < 16.0F) {
					Vector3f vector3f = ((IsoGameCharacter)zombie.target).getVehicle().chooseBestAttackPosition((IsoGameCharacter)zombie.target, zombie);
					if (vector3f != null && (Math.abs(gameCharacter.x - zombie.getPathFindBehavior2().getTargetX()) > 0.1F || Math.abs(gameCharacter.y - zombie.getPathFindBehavior2().getTargetY()) > 0.1F)) {
						zombie.changeState(PathFindState.instance());
						return;
					}
				}
			}

			if (gameCharacter.getPathTargetX() == (int)gameCharacter.getX() && gameCharacter.getPathTargetY() == (int)gameCharacter.getY() && gameCharacter instanceof IsoZombie) {
				if (((IsoZombie)gameCharacter).target == null) {
					zombie.getStateMachine().changeState(ZombieStandState.instance());
					return;
				}

				if ((int)((IsoZombie)gameCharacter).target.getZ() != (int)gameCharacter.getZ()) {
					zombie.changeState(ZombieStandState.instance());
					return;
				}
			}

			if (gameCharacter.isCollidedThisFrame() || gameCharacter.isCollidedWithVehicle()) {
				zombie.AllowRepathDelay = 0.0F;
				zombie.pathToLocation(gameCharacter.getPathTargetX(), gameCharacter.getPathTargetY(), gameCharacter.getPathTargetZ());
				if (zombie.getCurrentState() == instance()) {
					zombie.changeState(PathFindState.instance());
				}

				return;
			}

			float float1 = IsoZombie.baseSpeed;
			Vector2 vector2;
			if (gameCharacter instanceof IsoZombie) {
				zombie.setIgnoreMovementForDirection(false);
				float float2 = (float)((gameCharacter.getID() + zombie.ZombieID) % 20) / 10.0F - 1.0F;
				float float3 = (float)((zombie.getID() + zombie.ZombieID) % 20) / 10.0F - 1.0F;
				this.temp.x = zombie.getPathFindBehavior2().getTargetX();
				this.temp.y = zombie.getPathFindBehavior2().getTargetY();
				vector2 = this.temp;
				vector2.x -= zombie.getX();
				vector2 = this.temp;
				vector2.y -= zombie.getY();
				float float4 = this.temp.getLength();
				float float5 = float4 / 2.0F;
				if (float5 > 4.0F) {
					float5 = 4.0F;
				}

				if (!GameServer.bServer) {
					vector2 = this.temp;
					vector2.x += zombie.getX();
					vector2 = this.temp;
					vector2.y += zombie.getY();
					vector2 = this.temp;
					vector2.x += float2 * float5;
					vector2 = this.temp;
					vector2.y += float3 * float5;
					vector2 = this.temp;
					vector2.x -= zombie.getX();
					vector2 = this.temp;
					vector2.y -= zombie.getY();
					zombie.bRunning = false;
				}

				zombie.bRunning = false;
				zombie.reqMovement.normalize();
				if (!(Math.abs(this.temp.x - zombie.reqMovement.x) > 1.0E-4F) && Math.abs(this.temp.y - zombie.reqMovement.y) > 1.0E-4F) {
				}

				zombie.getZombieWalkTowardSpeed(float1, float4, this.temp);
				zombie.Move(this.temp);
				zombie.updateFrameSpeed();
				this.temp.normalize();
				if (!zombie.bCrawling) {
					zombie.setDir(IsoDirections.fromAngle(this.temp));
				} else {
					++this.turnTimer;
					if (this.turnTimer > 3) {
						IsoDirections directions = gameCharacter.dir;
						IsoDirections directions2 = IsoDirections.fromAngle(this.temp);
						if (directions != directions2 && gameCharacter.getStateMachine().getCurrent() != CrawlingZombieTurnState.instance()) {
							gameCharacter.StateMachineParams.clear();
							gameCharacter.StateMachineParams.put(0, directions2);
							gameCharacter.getStateMachine().Lock = false;
							gameCharacter.getStateMachine().changeState(CrawlingZombieTurnState.instance());
						}

						this.turnTimer = 0;
					}
				}
			} else {
				this.temp.x = (float)gameCharacter.getPathTargetX() + 0.5F;
				this.temp.y = (float)gameCharacter.getPathTargetY() + 0.5F;
				vector2 = this.temp;
				vector2.x -= gameCharacter.getX();
				vector2 = this.temp;
				vector2.y -= gameCharacter.getY();
				this.temp.setLength(gameCharacter.getPathSpeed());
				gameCharacter.Move(this.temp);
				gameCharacter.angle.x = this.temp.x;
				gameCharacter.angle.y = this.temp.y;
				gameCharacter.angle.normalize();
			}
		}
	}
}
