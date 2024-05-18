package zombie.scripting.commands.Character;

import java.security.InvalidParameterException;
import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptTalker;

public class SayAt extends BaseCommand {
   String owner;
   Stack chrs = new Stack();
   Stack say = new Stack();
   IsoGameCharacter chr;
   ScriptTalker talkerobj = null;
   boolean talker = false;
   Stack chras = new Stack();

   public boolean IsFinished() {
      if (this.talker && this.talkerobj != null) {
         return !this.talkerobj.getActual().IsSpeaking();
      } else if (this.chr == null) {
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
         if (var4.contains("\"")) {
            var4 = this.module.getLanguage(var4);
            if (var4.indexOf("\"") == 0) {
               var4 = var4.substring(1);
               var4 = var4.substring(0, var4.length() - 1);
            }

            this.say.add(var4);
         } else {
            this.chrs.add(var4.trim());
         }
      }

   }

   public void begin() {
      if (!ScriptManager.instance.skipping) {
         String var1;
         if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
            this.chr = this.currentinstance.getAlias(this.owner);

            for(int var2 = 0; var2 < this.chrs.size(); ++var2) {
               if (this.currentinstance.HasAlias((String)this.chrs.get(var2))) {
                  this.chras.add(this.currentinstance.CharacterAliases.get(this.chrs.get(var2)));
               }
            }

            var1 = (String)this.say.get(Rand.Next(this.say.size()));
            var1 = StringFunctions.EscapeChar(this.chr, this.chras, var1);
            this.chr.Say(var1);
         } else if (this.module.getTalker(this.owner) != null) {
            this.talker = true;
            this.talkerobj = this.module.getTalker(this.owner);
            this.talkerobj.getActual().Say((String)this.say.get(Rand.Next(this.say.size())));
         } else if (this.module.getCharacter(this.owner).Actual == null) {
            throw new InvalidParameterException();
         } else {
            this.chr = this.module.getCharacter(this.owner).Actual;
            if (this.chr != null) {
               var1 = (String)this.say.get(Rand.Next(this.say.size()));
               var1 = StringFunctions.EscapeChar(this.chr, var1);
               this.chr.Say(var1);
            }
         }
      }
   }

   public boolean AllowCharacterBehaviour(String var1) {
      return false;
   }

   public boolean DoesInstantly() {
      return false;
   }
}
