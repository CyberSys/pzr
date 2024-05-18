package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.network.GameClient;
import zombie.ui.TutorialManager;


public class BeatenPlayerState extends State {
	static BeatenPlayerState _instance = new BeatenPlayerState();

	public static BeatenPlayerState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.getStateMachine().Lock = true;
		gameCharacter.PlayAnimUnlooped("ZombieDeath");
		gameCharacter.def.Frame = 0.0F;
		gameCharacter.def.AnimFrameIncrease = 0.4F;
		gameCharacter.setAnimated(true);
		gameCharacter.setReanimateTimer((float)(30 + Rand.Next(120)));
		if (gameCharacter instanceof IsoPlayer && !((IsoPlayer)gameCharacter).isLocalPlayer()) {
			((IsoPlayer)gameCharacter).setAnimForecasted(5000);
		}

		if (GameClient.bClient && gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
			GameClient.instance.sendPlayer((IsoPlayer)gameCharacter);
		}

		gameCharacter.setOnFloor(true);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (gameCharacter.getCurrentSquare() != null) {
			gameCharacter.setReanimateTimer(gameCharacter.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
			if (gameCharacter.getReanimateTimer() <= 0.0F && gameCharacter.getBodyDamage().getOverallBodyHealth() > 0.0F) {
				gameCharacter.setReanimateTimer(0.0F);
				gameCharacter.getStateMachine().Lock = false;
				gameCharacter.getStateMachine().changeState(ReanimatePlayerState.instance());
			}

			if ((int)gameCharacter.def.Frame == gameCharacter.sprite.CurrentAnim.Frames.size() - 1) {
				if (gameCharacter instanceof IsoSurvivor) {
					((IsoSurvivor)gameCharacter).SetAllFrames((short)((int)gameCharacter.def.Frame));
				}

				if (gameCharacter == TutorialManager.instance.wife) {
					gameCharacter.dir = IsoDirections.S;
				}
			}
		}
	}
}
