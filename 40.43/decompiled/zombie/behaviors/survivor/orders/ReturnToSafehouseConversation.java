package zombie.behaviors.survivor.orders;

import java.util.HashMap;
import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.scripting.ScriptManager;

public class ReturnToSafehouseConversation extends ConversationOrder {
   public ReturnToSafehouseConversation(IsoGameCharacter var1, String var2) {
      super(var1, var2);
   }

   public void initOrder() {
      Stack var1 = ((IsoSurvivor)this.chr).getAvailableMembers();
      int var2 = var1.size();
      Stack var3 = new Stack();
      Stack var4 = new Stack();
      HashMap var5 = new HashMap();
      var5.put("Leader", this.chr);

      int var6;
      for(var6 = 0; var6 < var1.size(); ++var6) {
         IsoGameCharacter var7 = (IsoGameCharacter)var1.get(var6);
         if (var7.getOrder() instanceof FollowOrder) {
            var3.add(var7);
         } else {
            var4.add(var7);
         }
      }

      for(var6 = 0; var6 < var3.size(); ++var6) {
         var5.put("Returning" + (var6 + 1), var3.get(var6));
      }

      for(var6 = 0; var6 < var4.size(); ++var6) {
         var5.put("AtSafehouse" + (var6 + 1), var4.get(var6));
      }

      this.inst = ScriptManager.instance.PlayInstanceScript((String)null, this.scriptName, (HashMap)var5);
   }
}
