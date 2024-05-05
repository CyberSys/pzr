package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;


public final class CrawlingZombieTurnState extends State {
	private static final CrawlingZombieTurnState _instance = new CrawlingZombieTurnState();
	private static final Vector2 tempVector2_1 = new Vector2();
	private static final Vector2 tempVector2_2 = new Vector2();

	public static CrawlingZombieTurnState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
		((IsoZombie)gameCharacter).AllowRepathDelay = 0.0F;
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if (animEvent.m_EventName.equalsIgnoreCase("TurnSome")) {
			Vector2 vector2 = tempVector2_1.set(gameCharacter.dir.ToVector());
			Vector2 vector22 = "left".equalsIgnoreCase(animEvent.m_ParameterValue) ? IsoDirections.fromIndex(gameCharacter.dir.index() + 1).ToVector() : IsoDirections.fromIndex(gameCharacter.dir.index() - 1).ToVector();
			Vector2 vector23 = PZMath.lerp(tempVector2_2, vector2, vector22, animEvent.m_TimePc);
			gameCharacter.setForwardDirection(vector23);
		} else {
			if (animEvent.m_EventName.equalsIgnoreCase("TurnComplete")) {
				if ("left".equalsIgnoreCase(animEvent.m_ParameterValue)) {
					gameCharacter.dir = IsoDirections.fromIndex(gameCharacter.dir.index() + 1);
				} else {
					gameCharacter.dir = IsoDirections.fromIndex(gameCharacter.dir.index() - 1);
				}

				gameCharacter.getVectorFromDirection(gameCharacter.getForwardDirection());
			}
		}
	}

	public static boolean calculateDir(IsoGameCharacter gameCharacter, IsoDirections directions) {
		if (directions.index() > gameCharacter.dir.index()) {
			return directions.index() - gameCharacter.dir.index() <= 4;
		} else {
			return directions.index() - gameCharacter.dir.index() < -4;
		}
	}
}
