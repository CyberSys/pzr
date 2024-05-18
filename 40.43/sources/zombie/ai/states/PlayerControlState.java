package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;


public class PlayerControlState extends State {
	static PlayerControlState _instance = new PlayerControlState();

	public static PlayerControlState instance() {
		return _instance;
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}
}
