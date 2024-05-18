package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;
import zombie.network.GameServer;
import zombie.vehicles.BaseVehicle;


public class LungeState extends State {
	static LungeState _instance = new LungeState();
	Vector2 temp = new Vector2();
	int turnTimer = 0;

	public static LungeState instance() {
		return _instance;
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (!((IsoZombie)gameCharacter).bCrawling) {
			gameCharacter.setOnFloor(false);
		}

		gameCharacter.setShootable(true);
		if (gameCharacter instanceof IsoZombie) {
			IsoZombie zombie = (IsoZombie)gameCharacter;
			zombie.DoNetworkDirty();
			float float1 = 0.0F;
			if (zombie.target != null) {
				float1 = zombie.getWidth() + zombie.target.getWidth();
			} else {
				float1 = zombie.getWidth() * 2.0F;
			}

			((IsoZombie)gameCharacter).setIgnoreMovementForDirection(false);
			if (zombie.target == null) {
				if (zombie.LastTargetSeenX != -1) {
					zombie.pathToLocation(zombie.LastTargetSeenX, zombie.LastTargetSeenY, zombie.LastTargetSeenZ);
					if (zombie.getCurrentState() == this) {
						zombie.setDefaultState();
					}
				} else {
					zombie.AllowRepathDelay = 0.0F;
					zombie.setDefaultState();
				}

				return;
			}

			if (zombie.vectorToTarget.getLength() - float1 >= 0.8F && zombie.LungeTimer <= 0.0F) {
				zombie.AllowRepathDelay = 0.0F;
				if (zombie.getStateMachine().getPrevious() == WalkTowardState.instance() || zombie.getStateMachine().getPrevious() == PathFindState.instance()) {
					if (zombie.target != null) {
						zombie.getStateMachine().setPrevious(ZombieStandState.instance());
						zombie.pathToCharacter((IsoGameCharacter)zombie.target);
					} else {
						zombie.getStateMachine().RevertToPrevious();
					}

					return;
				}
			}

			if (zombie.target instanceof IsoGameCharacter) {
				BaseVehicle baseVehicle = ((IsoGameCharacter)zombie.target).getVehicle();
				if (baseVehicle != null && baseVehicle.isCharacterAdjacentTo(zombie)) {
					zombie.AttemptAttack();
					return;
				}
			}

			float float2 = zombie.vectorToTarget.getLength();
			float float3 = float1;
			if (zombie.bCrawling) {
				float3 = 0.9F;
			}

			if (float2 < float3) {
				zombie.AttemptAttack();
				return;
			}

			if (zombie.bLunger) {
				zombie.walkVariantUse = "ZombieWalk3";
			}

			zombie.LungeTimer -= GameTime.getInstance().getMultiplier() / 1.6F;
			if (zombie.LungeTimer < 0.0F) {
				zombie.LungeTimer = 0.0F;
			}

			this.temp.x = zombie.vectorToTarget.x;
			this.temp.y = zombie.vectorToTarget.y;
			zombie.getZombieLungeSpeed(this.temp);
			zombie.Move(this.temp);
			zombie.updateFrameSpeed();
			this.temp.normalize();
			boolean boolean1 = false;
			if (zombie.NetRemoteState != 4) {
				zombie.NetRemoteState = 4;
				boolean1 = true;
			}

			if (zombie.target != null && zombie.target instanceof IsoPlayer && ((IsoPlayer)((IsoPlayer)zombie.target)).playerMoveDir.getLength() > 0.0F) {
			}

			if (!zombie.bCrawling) {
				zombie.DirectionFromVector(this.temp);
				zombie.getVectorFromDirection(zombie.angle);
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

			if (GameServer.bServer && boolean1) {
				GameServer.sendZombie((IsoZombie)gameCharacter);
			}
		}
	}
}
