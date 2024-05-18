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

   public void execute(IsoGameCharacter var1) {
      LuaEventManager.triggerEvent("OnAIStateExecute", var1);
   }

   public void enter(IsoGameCharacter var1) {
      LuaEventManager.triggerEvent("OnAIStateEnter", var1);
   }

   public void exit(IsoGameCharacter var1) {
      LuaEventManager.triggerEvent("OnAIStateExit", var1);
   }

   void calculate() {
      SoundManager.instance.update3();
   }
}
