package zombie.scripting.commands.Character;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;

public class WalkWithinRangeOf extends BaseCommand {
   String owner;
   String say;
   String Other;
   IsoGameCharacter other;
   IsoGameCharacter chr;
   int range;
   Behavior.BehaviorResult res;
   PathFindBehavior behavior;
   int cycle;

   public WalkWithinRangeOf() {
      this.res = Behavior.BehaviorResult.Working;
      this.behavior = new PathFindBehavior(true);
      this.cycle = 120;
   }

   public boolean IsFinished() {
      if (this.other == null) {
         return true;
      } else if (this.chr == null) {
         return true;
      } else {
         return this.other.DistTo(this.chr) <= (float)this.range && this.other.CanSee(this.chr);
      }
   }

   public void update() {
      --this.cycle;
      if (this.cycle <= 0) {
         this.cycle = 240;
         this.behavior.sx = (int)this.chr.getX();
         this.behavior.sy = (int)this.chr.getY();
         this.behavior.sz = (int)this.chr.getZ();
         this.behavior.tx = (int)this.other.getX();
         this.behavior.ty = (int)this.other.getY();
         this.behavior.tz = (int)this.other.getZ();
         this.behavior.pathIndex = 0;
      }

      this.res = this.behavior.process((DecisionPath)null, this.chr);
   }

   public void init(String var1, String[] var2) {
      this.owner = var1;
      String var3 = "";
      this.Other = var2[0].trim();
      if (this.Other.equals("null")) {
         this.Other = null;
      }

      this.range = Integer.parseInt(var2[1].trim());
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

      if (this.currentinstance != null && this.currentinstance.HasAlias(this.Other)) {
         this.other = this.currentinstance.getAlias(this.Other);
      } else {
         if (this.module.getCharacter(this.Other) == null) {
            return;
         }

         if (this.module.getCharacter(this.Other).Actual == null) {
            return;
         }

         this.other = this.module.getCharacter(this.Other).Actual;
      }

      this.behavior.sx = (int)this.chr.getX();
      this.behavior.sy = (int)this.chr.getY();
      this.behavior.sz = (int)this.chr.getZ();
      this.behavior.tx = (int)this.other.getX();
      this.behavior.ty = (int)this.other.getY();
      this.behavior.tz = (int)this.other.getZ();
      this.behavior.pathIndex = 0;
   }

   public boolean DoesInstantly() {
      return false;
   }

   public boolean AllowCharacterBehaviour(String var1) {
      return false;
   }

   public void Finish() {
      this.res = Behavior.BehaviorResult.Working;
      this.behavior.reset();
   }
}
