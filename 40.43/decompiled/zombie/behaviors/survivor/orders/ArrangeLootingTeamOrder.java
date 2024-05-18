package zombie.behaviors.survivor.orders;

import java.util.HashMap;
import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.scripting.ScriptManager;

public class ArrangeLootingTeamOrder extends ConversationOrder {
   public ArrangeLootingTeamOrder(IsoGameCharacter var1, String var2) {
      super(var1, var2);
   }

   public void initOrder() {
      Stack var1 = ((IsoSurvivor)this.chr).getAvailableMembers();
      int var2 = var1.size();
      Stack var3 = new Stack();
      Stack var4 = new Stack();
      if (var2 != 0) {
         if (var2 == 1) {
            var3.addAll(var1);
         } else if (var2 == 2) {
            var3.add(var1.get(0));
            var4.add(var1.get(1));
         } else if (var2 == 3) {
            var3.add(var1.get(0));
            var4.add(var1.get(1));
            var4.add(var1.get(2));
         } else if (var2 == 4) {
            var3.add(var1.get(0));
            var4.add(var1.get(1));
            var4.add(var1.get(2));
            var3.add(var1.get(3));
         } else if (var2 >= 5) {
            var3.add(var1.get(0));
            var4.add(var1.get(1));
            var4.add(var1.get(2));
            var3.add(var1.get(3));
            var4.add(var1.get(4));

            for(int var5 = 5; var5 < var1.size(); ++var5) {
               var3.add(var1.get(var5));
            }
         }

         HashMap var7 = new HashMap();
         var7.put("Leader", this.chr);

         int var6;
         for(var6 = 0; var6 < var3.size(); ++var6) {
            var7.put("Guard" + (var6 + 1), var3.get(var6));
            ((IsoGameCharacter)var3.get(var6)).GiveOrder(new IdleOrder((IsoGameCharacter)var3.get(var6)), true);
         }

         for(var6 = 0; var6 < var4.size(); ++var6) {
            var7.put("Companion" + (var6 + 1), var4.get(var6));
            ((IsoGameCharacter)var4.get(var6)).GiveOrder(new FollowOrder((IsoGameCharacter)var4.get(var6), this.chr, 4), true);
         }

         this.inst = ScriptManager.instance.PlayInstanceScript((String)null, this.scriptName, (HashMap)var7);
      }
   }
}
