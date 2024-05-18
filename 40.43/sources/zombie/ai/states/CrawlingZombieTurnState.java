package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoDirections;


public class CrawlingZombieTurnState extends State {
	static CrawlingZombieTurnState _instance = new CrawlingZombieTurnState();

	public static CrawlingZombieTurnState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoDirections directions = (IsoDirections)gameCharacter.StateMachineParams.get(0);
		boolean boolean1 = this.calculateDir(gameCharacter, directions);
		if (boolean1) {
			gameCharacter.PlayAnimUnlooped("Zombie_CrawlTurnL");
		} else {
			gameCharacter.PlayAnimUnlooped("Zombie_CrawlTurnR");
		}

		gameCharacter.getSpriteDef().AnimFrameIncrease = 0.11F;
		if ((int)gameCharacter.getSpriteDef().Frame == gameCharacter.getSprite().CurrentAnim.Frames.size() - 1) {
			if (boolean1) {
				gameCharacter.dir = IsoDirections.fromIndex(gameCharacter.dir.index() + 1);
			} else {
				gameCharacter.dir = IsoDirections.fromIndex(gameCharacter.dir.index() - 1);
			}

			gameCharacter.getVectorFromDirection(gameCharacter.angle);
			if (gameCharacter.dir == directions) {
				gameCharacter.getSpriteDef().Frame = 0.0F;
				gameCharacter.getSpriteDef().Finished = false;
				if (gameCharacter.legsSprite.modelSlot != null) {
					gameCharacter.legsSprite.modelSlot.ResetToFrameOne();
				}

				gameCharacter.getStateMachine().Lock = false;
				gameCharacter.getStateMachine().changeState(gameCharacter.getStateMachine().getPrevious());
			} else {
				gameCharacter.getSpriteDef().Frame = 0.0F;
				gameCharacter.getSpriteDef().Finished = false;
				if (gameCharacter.legsSprite.modelSlot != null) {
					gameCharacter.legsSprite.modelSlot.ResetToFrameOne();
				}
			}
		}
	}

	private boolean calculateDir(IsoGameCharacter gameCharacter, IsoDirections directions) {
		if (directions.index() > gameCharacter.dir.index()) {
			return directions.index() - gameCharacter.dir.index() <= 4;
		} else {
			return directions.index() - gameCharacter.dir.index() < -4;
		}
	}
}
