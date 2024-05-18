package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameServer;
import zombie.ui.TutorialManager;


public class JustDieState extends State {
	static JustDieState _instance = new JustDieState();
	Vector2 dirThisFrame = new Vector2();
	int AnimDelayRate = 10;

	public static JustDieState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		if (gameCharacter instanceof IsoSurvivor) {
			((IsoSurvivor)gameCharacter).getDescriptor().bDead = true;
		}

		if (gameCharacter instanceof IsoZombie) {
			++IsoZombie.ZombieDeaths;
			gameCharacter.PlayAnim("ZombieStaggerBack");
			gameCharacter.def.setFrameSpeedPerFrame(0.3F);
		} else {
			boolean boolean1 = false;
		}

		if (gameCharacter.getHealth() <= 0.0F) {
			gameCharacter.playDeadSound();
		}

		gameCharacter.setStateEventDelayTimer(30.0F * gameCharacter.getHitForce() * gameCharacter.getStaggerTimeMod());
		Vector2 vector2 = gameCharacter.getHitDir();
		vector2.x *= gameCharacter.getHitForce();
		vector2 = gameCharacter.getHitDir();
		vector2.y *= gameCharacter.getHitForce();
		vector2 = gameCharacter.getHitDir();
		vector2.x *= 0.08F;
		vector2 = gameCharacter.getHitDir();
		vector2.y *= 0.08F;
		if (gameCharacter.getHitDir().getLength() > 0.08F) {
			gameCharacter.getHitDir().setLength(0.08F);
		}

		gameCharacter.setIgnoreMovementForDirection(true);
		gameCharacter.getStateMachine().Lock = true;
		gameCharacter.setReanimPhase(0);
		if (gameCharacter instanceof IsoZombie) {
			gameCharacter.setReanimateTimer((float)(Rand.Next(30) + 4));
		}

		if (Rand.Next(5) == 0) {
			gameCharacter.setReanimateTimer((float)(Rand.Next(30) + 30));
		}

		if (gameCharacter instanceof IsoZombie) {
			gameCharacter.setReanimAnimFrame(3);
			gameCharacter.setReanimAnimDelay(this.AnimDelayRate);
		}

		if (gameCharacter.getHealth() > 0.0F) {
			gameCharacter.setReanim(true);
			gameCharacter.setDieCount(gameCharacter.getDieCount() + 1);
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (!(gameCharacter instanceof IsoZombie)) {
			boolean boolean1 = false;
		}

		if (gameCharacter == TutorialManager.instance.wife) {
			gameCharacter.dir = IsoDirections.S;
		}

		gameCharacter.PlayAnimFrame("ZombieDeath", 13);
		if (GameServer.bServer && gameCharacter instanceof IsoZombie) {
			GameServer.sendDeadZombie((IsoZombie)gameCharacter);
		}

		new IsoDeadBody(gameCharacter);
		if (gameCharacter.getAttackedBy() != null) {
			gameCharacter.getAttackedBy().setZombieKills(gameCharacter.getAttackedBy().getZombieKills() + 1);
		}

		if (GameServer.bServer && gameCharacter instanceof IsoZombie && gameCharacter.getAttackedBy() instanceof IsoPlayer) {
			gameCharacter.getAttackedBy().sendObjectChange("AddZombieKill");
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovementForDirection(false);
	}
}
