package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;


public class FakeDeadZombieState extends State {
	static FakeDeadZombieState _instance = new FakeDeadZombieState();

	public static FakeDeadZombieState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setVisibleToNPCs(false);
		gameCharacter.setCollidable(false);
		((IsoZombie)gameCharacter).setFakeDead(true);
		gameCharacter.setOnFloor(true);
		gameCharacter.PlayAnimUnlooped("ZombieDeath");
		gameCharacter.def.Frame = (float)(gameCharacter.sprite.CurrentAnim.Frames.size() - 1);
		gameCharacter.setIgnoreMovementForDirection(true);
		gameCharacter.getStateMachine().Lock = true;
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		if (!zombie.isUnderVehicle() && zombie.isFakeDead() && zombie.target != null && zombie.target.DistTo(gameCharacter) < 2.5F) {
			gameCharacter.setIgnoreMovementForDirection(false);
			zombie.DirectionFromVector(zombie.vectorToTarget);
			gameCharacter.setIgnoreMovementForDirection(true);
			zombie.setFakeDead(false);
			zombie.bCrawling = true;
			gameCharacter.setVisibleToNPCs(true);
			gameCharacter.setCollidable(true);
			gameCharacter.PlayAnimUnlooped("ZombieDeadToCrawl");
			gameCharacter.def.setFrameSpeedPerFrame(0.27F);
			zombie.DoZombieStats();
			gameCharacter.getStateMachine().Lock = false;
			String string = "MaleZombieAttack";
			if (gameCharacter.isFemale()) {
				string = "FemaleZombieAttack";
			}

			gameCharacter.getEmitter().playSound(string);
			if (zombie.target instanceof IsoPlayer) {
				IsoPlayer player = (IsoPlayer)zombie.target;
				Stats stats = player.getStats();
				stats.Panic += player.getBodyDamage().getPanicIncreaseValue() * 3.0F;
			}
		}

		if (gameCharacter.getSprite().CurrentAnim.name.equals("ZombieDeadToCrawl") && (int)gameCharacter.getSpriteDef().Frame >= 16 && (int)gameCharacter.getSpriteDef().Frame <= 21) {
			if (gameCharacter.getHealth() > 0.0F && zombie.isTargetInCone(1.5F, 0.9F) && zombie.target instanceof IsoGameCharacter) {
				IsoGameCharacter gameCharacter2 = (IsoGameCharacter)zombie.target;
				if (gameCharacter2.getVehicle() == null || gameCharacter2.getVehicle().couldCrawlerAttackPassenger(gameCharacter2)) {
					gameCharacter2.getBodyDamage().AddRandomDamageFromZombie((IsoZombie)gameCharacter);
				}
			}

			gameCharacter.getStateMachine().Lock = false;
			gameCharacter.getStateMachine().changeState(WalkTowardState.instance());
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
	}
}
