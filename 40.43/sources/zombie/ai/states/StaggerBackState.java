package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;


public class StaggerBackState extends State {
	static StaggerBackState _instance = new StaggerBackState();
	Vector2 dirThisFrame = new Vector2();

	public static StaggerBackState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		if (gameCharacter instanceof IsoZombie) {
			((IsoZombie)gameCharacter).NetRemoteState = 8;
		}

		if (gameCharacter instanceof IsoZombie) {
			if (((IsoZombie)gameCharacter).bCrawling) {
				gameCharacter.getStateMachine().Lock = false;
				if (gameCharacter.getStateMachine().getPrevious() == CrawlingZombieTurnState.instance()) {
					gameCharacter.getStateMachine().changeState((State)null);
				} else {
					gameCharacter.getStateMachine().changeState(gameCharacter.getStateMachine().getPrevious());
				}

				return;
			}

			gameCharacter.PlayAnim("ZombieStaggerBack");
		}

		gameCharacter.setStateEventDelayTimer(this.getMaxStaggerTime(gameCharacter));
		Vector2 vector2 = gameCharacter.getHitDir();
		vector2.x *= gameCharacter.getHitForce();
		vector2 = gameCharacter.getHitDir();
		vector2.y *= gameCharacter.getHitForce();
		vector2 = gameCharacter.getHitDir();
		vector2.x *= 0.08F;
		vector2 = gameCharacter.getHitDir();
		vector2.y *= 0.08F;
		if (gameCharacter.getHitDir().getLength() > 0.06F) {
			gameCharacter.getHitDir().setLength(0.06F);
		}

		this.dirThisFrame.x = gameCharacter.getHitDir().x;
		this.dirThisFrame.y = gameCharacter.getHitDir().y;
		this.dirThisFrame.normalize();
		gameCharacter.setDir(IsoDirections.reverse(IsoDirections.fromAngle(this.dirThisFrame)));
		gameCharacter.setIgnoreMovementForDirection(true);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		this.dirThisFrame.x = gameCharacter.getHitDir().x;
		this.dirThisFrame.y = gameCharacter.getHitDir().y;
		float float1 = gameCharacter.getStateEventDelayTimer() / this.getMaxStaggerTime(gameCharacter);
		Vector2 vector2 = this.dirThisFrame;
		vector2.x *= float1;
		vector2 = this.dirThisFrame;
		vector2.y *= float1;
		gameCharacter.Move(this.dirThisFrame);
		if (gameCharacter instanceof IsoZombie) {
			((IsoZombie)gameCharacter).NetRemoteState = 7;
			if (gameCharacter.getStateEventDelayTimer() <= 0.0F) {
				if (((IsoZombie)gameCharacter).target instanceof IsoGameCharacter && ((IsoGameCharacter)((IsoZombie)gameCharacter).target).getVehicle() != null) {
					gameCharacter.changeState(WalkTowardState.instance());
					return;
				}

				if (((IsoZombie)gameCharacter).target instanceof IsoPlayer && ((IsoPlayer)((IsoZombie)gameCharacter).target).GhostMode) {
					gameCharacter.setShootable(true);
					gameCharacter.setDefaultState();
					return;
				}

				gameCharacter.getStateMachine().changeState(LungeState.instance());
				((IsoZombie)gameCharacter).LungeTimer = 90.0F;
				gameCharacter.setStateEventDelayTimer(40.0F);
				gameCharacter.setShootable(true);
				return;
			}
		} else if (gameCharacter.getStateEventDelayTimer() <= 0.0F) {
			gameCharacter.setShootable(true);
			gameCharacter.getStateMachine().changeState(gameCharacter.getDefaultState());
			return;
		}

		if (this.dirThisFrame.getLength() > 0.0F) {
			this.dirThisFrame.normalize();
			this.dirThisFrame.normalize();
			gameCharacter.setDir(IsoDirections.reverse(IsoDirections.fromAngle(this.dirThisFrame)));
		}

		if (gameCharacter instanceof IsoZombie) {
			((IsoZombie)gameCharacter).reqMovement.x = gameCharacter.angle.x;
			((IsoZombie)gameCharacter).reqMovement.y = gameCharacter.angle.y;
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovementForDirection(false);
	}

	private float getMaxStaggerTime(IsoGameCharacter gameCharacter) {
		float float1 = 35.0F * gameCharacter.getHitForce() * gameCharacter.getStaggerTimeMod();
		if (float1 < 30.0F) {
			return 30.0F;
		} else {
			return float1 > 50.0F ? 50.0F : float1;
		}
	}
}
