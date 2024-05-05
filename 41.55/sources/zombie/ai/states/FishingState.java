package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.debug.DebugLog;
import zombie.network.GameClient;


public final class FishingState extends State {
	private static final FishingState _instance = new FishingState();
	float pauseTime = 0.0F;
	private String stage = null;

	public static FishingState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		DebugLog.log("FISHINGSTATE - ENTER");
		gameCharacter.setVariable("FishingFinished", false);
		this.pauseTime = Rand.Next(60.0F, 120.0F);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (GameClient.bClient && gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
			String string = gameCharacter.getVariableString("FishingStage");
			if (string != null && !string.equals(this.stage)) {
				this.stage = string;
				if (!string.equals("idle")) {
					GameClient.sendEventUpdate((IsoPlayer)gameCharacter, "EventFishing", false);
				}
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		DebugLog.log("FISHINGSTATE - EXIT");
		gameCharacter.clearVariable("FishingStage");
		gameCharacter.clearVariable("FishingFinished");
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
	}
}
