package zombie.scripting.commands.Character;

import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.scripting.commands.BaseCommand;

public class SayIdle extends BaseCommand {
   String owner;
   Stack say = new Stack();
   IsoGameCharacter chr;

   public boolean IsFinished() {
      if (this.chr == null) {
         return true;
      } else {
         return !this.chr.isSpeaking();
      }
   }

   public void update() {
   }

   public void init(String var1, String[] var2) {
      this.owner = var1;

      for(int var3 = 0; var3 < var2.length; ++var3) {
         String var4 = var2[var3];
         var4 = this.module.getLanguage(var4);
         if (var4.indexOf("\"") == 0) {
            var4 = var4.substring(1);
            var4 = var4.substring(0, var4.length() - 1);
         }

         this.say.add(var4);
      }

   }

   public void begin() {
      String var1;
      if (this.currentinstance.HasAlias(this.owner)) {
         this.chr = this.currentinstance.getAlias(this.owner);
         var1 = (String)this.say.get(Rand.Next(this.say.size()));
         var1 = StringFunctions.EscapeChar(this.chr, var1);
         this.chr.Say(var1);
      } else {
         this.chr = this.module.getCharacter(this.owner).Actual;
         if (this.chr != null) {
            var1 = (String)this.say.get(Rand.Next(this.say.size()));
            var1 = StringFunctions.EscapeChar(this.chr, var1);
            this.chr.Say(var1);
         }
      }
   }

   public boolean AllowCharacterBehaviour(String var1) {
      return true;
   }

   public boolean DoesInstantly() {
      return false;
   }
}
