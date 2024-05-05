package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;


public final class FakeDeadAttackState extends State {
	private static final FakeDeadAttackState _instance = new FakeDeadAttackState();

	public static FakeDeadAttackState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		zombie.DirectionFromVector(zombie.vectorToTarget);
		zombie.setFakeDead(false);
		gameCharacter.setVisibleToNPCs(true);
		gameCharacter.setCollidable(true);
		String string = "MaleZombieAttack";
		if (gameCharacter.isFemale()) {
			string = "FemaleZombieAttack";
		}

		gameCharacter.getEmitter().playSound(string);
		if (zombie.target instanceof IsoPlayer && !((IsoPlayer)zombie.target).getCharacterTraits().Desensitized.isSet()) {
			IsoPlayer player = (IsoPlayer)zombie.target;
			Stats stats = player.getStats();
			stats.Panic += player.getBodyDamage().getPanicIncreaseValue() * 3.0F;
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck") && gameCharacter.isAlive() && zombie.isTargetInCone(1.5F, 0.9F) && zombie.target instanceof IsoGameCharacter) {
			IsoGameCharacter gameCharacter2 = (IsoGameCharacter)zombie.target;
			if (gameCharacter2.getVehicle() == null || gameCharacter2.getVehicle().couldCrawlerAttackPassenger(gameCharacter2)) {
				gameCharacter2.getBodyDamage().AddRandomDamageFromZombie((IsoZombie)gameCharacter, (String)null);
			}
		}

		if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
			zombie.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
		}

		if (animEvent.m_EventName.equalsIgnoreCase("ActiveAnimFinishing")) {
			zombie.bCrawling = true;
		}
	}
}
