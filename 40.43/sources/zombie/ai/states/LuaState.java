package zombie.ai.states;

import zombie.SoundManager;
import zombie.Lua.LuaEventManager;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;


public class LuaState extends State {
	static LuaState _instance = new LuaState();

	public static LuaState instance() {
		return _instance;
	}

	public void execute(IsoGameCharacter gameCharacter) {
		LuaEventManager.triggerEvent("OnAIStateExecute", gameCharacter);
	}

	public void enter(IsoGameCharacter gameCharacter) {
		LuaEventManager.triggerEvent("OnAIStateEnter", gameCharacter);
	}

	public void exit(IsoGameCharacter gameCharacter) {
		LuaEventManager.triggerEvent("OnAIStateExit", gameCharacter);
	}

	void calculate() {
		SoundManager.instance.update3();
	}
}
