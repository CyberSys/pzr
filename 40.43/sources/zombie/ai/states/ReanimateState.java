package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoCamera;


public class ReanimateState extends State {
	static ReanimateState _instance = new ReanimateState();
	int AnimDelayRate = 10;

	public static ReanimateState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		if (gameCharacter instanceof IsoSurvivor) {
			((IsoSurvivor)gameCharacter).getDescriptor().bDead = true;
		}

		gameCharacter.PlayAnim("ZombieDeath");
		gameCharacter.def.Frame = 0.0F;
		gameCharacter.def.Looped = false;
		gameCharacter.setDefaultState(this);
		gameCharacter.getStateMachine().Lock = true;
		gameCharacter.setReanimPhase(0);
		gameCharacter.setReanimateTimer((float)(Rand.Next(250) + 1200));
		gameCharacter.getBodyDamage().setOverallBodyHealth(0.0F);
		if (Rand.Next(4) == 0) {
			gameCharacter.setReanimateTimer(gameCharacter.getReanimateTimer() * 3.0F);
		}

		gameCharacter.setCollidable(false);
		if (gameCharacter instanceof IsoPlayer) {
			((IsoPlayer)gameCharacter).removeSaveFile();
		}

		gameCharacter.setReanimAnimFrame(3);
		gameCharacter.setReanimAnimDelay(this.AnimDelayRate);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (gameCharacter.getReanimPhase() == 0 && (int)gameCharacter.def.Frame == gameCharacter.sprite.CurrentAnim.Frames.size() - 1) {
			gameCharacter.setReanimPhase(1);
			gameCharacter.sprite.Animate = false;
			gameCharacter.setCollidable(false);
		}

		if (gameCharacter.getReanimPhase() == 1) {
			gameCharacter.setReanimateTimer(gameCharacter.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
			if (gameCharacter.getReanimateTimer() <= 0.0F && gameCharacter.getLeaveBodyTimedown() > 3600.0F) {
				gameCharacter.getCurrentSquare().getCell().getRemoveList().add(gameCharacter);
				gameCharacter.getCurrentSquare().getMovingObjects().remove(gameCharacter);
				IsoZombie zombie = new IsoZombie(gameCharacter.getCell(), gameCharacter.getDescriptor());
				zombie.setCurrent(gameCharacter.getCurrentSquare());
				zombie.getCurrentSquare().getMovingObjects().add(zombie);
				zombie.setX(gameCharacter.getX());
				zombie.setY(gameCharacter.getY());
				zombie.setZ(gameCharacter.getZ());
				zombie.setInventory(gameCharacter.getInventory());
				gameCharacter.getCell().getZombieList().add(zombie);
				zombie.setDir(gameCharacter.getDir());
				zombie.setPathSpeed(zombie.getPathSpeed() * 1.2F);
				zombie.wanderSpeed = zombie.getPathSpeed() * 0.5F * zombie.getSpeedMod();
				zombie.setHealth(zombie.getHealth() * 5.0F);
				zombie.PlayAnim("ZombieDeath");
				zombie.def.Frame = (float)(zombie.sprite.CurrentAnim.Frames.size() - 1);
				zombie.def.Looped = false;
				if (IsoCamera.CamCharacter == gameCharacter) {
					IsoCamera.SetCharacterToFollow(zombie);
				}

				zombie.def.Finished = false;
				zombie.PlayAnimUnlooped("ZombieGetUp");
				zombie.def.setFrameSpeedPerFrame(0.2F);
				zombie.getStateMachine().setCurrent(this);
				zombie.setReanimPhase(2);
				zombie.setShootable(true);
				zombie.getStateMachine().Lock = true;
			}
		}

		if (gameCharacter.getReanimPhase() == 2 && (int)gameCharacter.def.Frame >= gameCharacter.sprite.CurrentAnim.Frames.size() - 2) {
			gameCharacter.getStateMachine().Lock = false;
			gameCharacter.setReanimPhase(3);
			gameCharacter.setVisibleToNPCs(true);
			gameCharacter.setShootable(true);
		}

		if (gameCharacter.getReanimPhase() == 3) {
			gameCharacter.getStateMachine().Lock = false;
			gameCharacter.getStateMachine().setCurrent(ZombieStandState._instance);
		}
	}
}
