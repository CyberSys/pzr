package zombie.behaviors.survivor.orders;

import java.util.HashMap;
import java.util.Stack;
import zombie.behaviors.Behavior;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Script;

public class ConversationOrder extends Order {
   Script.ScriptInstance inst;
   String scriptName;
   IsoGameCharacter chr;

   public ConversationOrder(IsoGameCharacter var1, String var2) {
      super(var1);
      this.chr = var1;
      this.scriptName = var2;
   }

   public Behavior.BehaviorResult process() {
      return this.inst != null && !this.inst.finished() ? Behavior.BehaviorResult.Working : Behavior.BehaviorResult.Succeeded;
   }

   public void initOrder() {
      Stack var1 = ((IsoSurvivor)this.chr).getAvailableMembers();
      HashMap var2 = new HashMap();
      var2.put("Leader", this.chr);

      for(int var3 = 0; var3 < var1.size(); ++var3) {
         var2.put("Member" + (var3 + 1), var1.get(var3));
      }

      this.inst = ScriptManager.instance.PlayInstanceScript((String)null, this.scriptName, (HashMap)var2);
   }

   public boolean complete() {
      return this.inst == null || this.inst.finished();
   }

   public void update() {
   }
}
