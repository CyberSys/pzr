package zombie.scripting.commands;

import zombie.scripting.objects.Script;
import zombie.scripting.objects.ScriptModule;

public abstract class BaseCommand {
   public ScriptModule module;
   public Script script = null;
   public Script.ScriptInstance currentinstance = null;

   public abstract void begin();

   public abstract boolean IsFinished();

   public abstract void update();

   public abstract void init(String var1, String[] var2);

   public abstract boolean DoesInstantly();

   public boolean getValue() {
      return false;
   }

   public void Finish() {
   }

   public boolean AllowCharacterBehaviour(String var1) {
      return true;
   }

   public void updateskip() {
   }
}
