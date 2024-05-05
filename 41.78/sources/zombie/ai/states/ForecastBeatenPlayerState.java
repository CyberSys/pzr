package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;


public final class ForecastBeatenPlayerState extends State {
	private static final ForecastBeatenPlayerState _instance = new ForecastBeatenPlayerState();

	public static ForecastBeatenPlayerState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(true);
		gameCharacter.setReanimateTimer(30.0F);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (gameCharacter.getCurrentSquare() != null) {
			gameCharacter.setReanimateTimer(gameCharacter.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
			if (gameCharacter.getReanimateTimer() <= 0.0F) {
				gameCharacter.setReanimateTimer(0.0F);
				gameCharacter.setVariable("bKnockedDown", true);
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(false);
	}
}
