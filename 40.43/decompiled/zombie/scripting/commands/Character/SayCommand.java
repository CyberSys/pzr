package zombie.scripting.commands.Character;

import java.util.Iterator;
import java.util.Stack;
import java.util.Map.Entry;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptCharacter;
import zombie.scripting.objects.ScriptTalker;

public class SayCommand extends BaseCommand {
   String owner;
   Stack say = new Stack();
   IsoGameCharacter chr;
   ScriptTalker talkerobj = null;
   boolean talker = false;

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
         var4 = this.module.getLanguage(var4);
         if (var4.indexOf("\"") == 0) {
            var4 = var4.substring(1);
            var4 = var4.substring(0, var4.length() - 1);
         }

         this.say.add(var4);
      }

   }

   public void begin() {
      if (!ScriptManager.instance.skipping) {
         if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
            this.chr = this.currentinstance.getAlias(this.owner);
            String var5 = (String)this.say.get(Rand.Next(this.say.size()));
            var5 = StringFunctions.EscapeChar(this.chr, var5);

            Entry var3;
            String var4;
            for(Iterator var6 = this.currentinstance.luaMap.entrySet().iterator(); var6.hasNext(); var5 = var5.replace(var4, (CharSequence)var3.getValue())) {
               var3 = (Entry)var6.next();
               var4 = "$" + ((String)var3.getKey()).toUpperCase() + "$";
            }

            this.chr.Say(var5);
         } else if (this.module.getTalker(this.owner) != null) {
            this.talker = true;
            this.talkerobj = this.module.getTalker(this.owner);
            this.talkerobj.getActual().Say((String)this.say.get(Rand.Next(this.say.size())));
         } else {
            ScriptCharacter var1 = this.module.getCharacter(this.owner);
            if (var1 != null) {
               this.chr = var1.Actual;
               if (this.chr != null) {
                  String var2 = (String)this.say.get(Rand.Next(this.say.size()));
                  var2 = StringFunctions.EscapeChar(this.chr, var2);
                  this.chr.Say(var2);
               }
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
