package zombie.scripting.commands.Character;

import java.util.Stack;
import zombie.characters.IsoGameCharacter;

class StringFunctions {
   static String EscapeChar(IsoGameCharacter var0, String var1) {
      var1 = var1.replace("$FIRSTNAME$", var0.getDescriptor().getForename());
      var1 = var1.replace("$SURNAME$", var0.getDescriptor().getSurname());
      return var1;
   }

   static String EscapeChar(IsoGameCharacter var0, Stack var1, String var2) {
      var2 = EscapeChar(var0, var2);

      for(int var3 = 0; var3 < var1.size(); ++var3) {
         var2 = var2.replace("$FIRSTNAME" + (var3 + 1) + "$", ((IsoGameCharacter)var1.get(var3)).getDescriptor().getForename());
         var2 = var2.replace("$SURNAME" + (var3 + 1) + "$", ((IsoGameCharacter)var1.get(var3)).getDescriptor().getSurname());
      }

      return var2;
   }
}
