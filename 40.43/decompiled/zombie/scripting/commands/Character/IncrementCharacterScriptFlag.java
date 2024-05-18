package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptFlag;

public class IncrementCharacterScriptFlag extends BaseCommand {
   String owner;
   String stat;
   int modifier = 0;
   IsoGameCharacter chr;
   public String name;
   String Other = "";
   boolean invert = false;

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public void init(String var1, String[] var2) {
      if (var1.indexOf("!") == 0) {
         this.invert = true;
         var1 = var1.substring(1);
      }

      this.owner = var1;
      this.Other = var2[0].trim();
      this.name = var2[1].trim().replace("\"", "");
   }

   public boolean getValue() {
      return true;
   }

   public void begin() {
      if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
         this.chr = this.currentinstance.getAlias(this.owner);
      } else {
         if (this.module.getCharacter(this.owner) == null) {
            return;
         }

         if (this.module.getCharacter(this.owner).Actual == null) {
            return;
         }

         this.chr = this.module.getCharacter(this.owner).Actual;
      }

      IsoGameCharacter var1;
      if (this.currentinstance != null && this.currentinstance.HasAlias(this.Other)) {
         var1 = this.currentinstance.getAlias(this.Other);
      } else if (this.module.getCharacter(this.Other) == null) {
         var1 = null;
      } else if (this.module.getCharacter(this.Other).Actual == null) {
         var1 = null;
      } else {
         var1 = this.module.getCharacter(this.Other).Actual;
      }

      if (this.chr != null) {
         String var2 = "";
         if (var1 != null) {
            (new StringBuilder()).append(this.chr.getDescriptor().getID()).append("_").append(var1.getDescriptor().getID()).append("_").append(this.name).toString();
         } else {
            (new StringBuilder()).append(this.chr.getDescriptor().getID()).append("_").append(this.name).toString();
         }

         ScriptFlag var3 = this.module.getFlag(this.name);
         if (var3 == null) {
            var3 = new ScriptFlag();
            var3.module = this.module;
            var3.name = this.name;
            var3.value = "1";
            this.module.FlagMap.put(this.name, var3);
         } else {
            var3.value = (new Integer(Integer.parseInt(var3.value) + 1)).toString();
         }

      }
   }

   public boolean AllowCharacterBehaviour(String var1) {
      return true;
   }

   public boolean DoesInstantly() {
      return true;
   }
}
