package zombie.ai.states;

import zombie.ai.State;

public class IdleState extends State {
   static IdleState _instance = new IdleState();

   public static IdleState instance() {
      return _instance;
   }

   public void execute(Character var1) {
   }
}
