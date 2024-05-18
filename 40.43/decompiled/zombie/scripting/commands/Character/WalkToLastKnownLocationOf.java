package zombie.scripting.commands.Character;

import java.security.InvalidParameterException;
import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;

public class WalkToLastKnownLocationOf extends BaseCommand {
   String chara;
   int x;
   int y;
   int z;
   int timer = 0;
   Behavior.BehaviorResult res;
   PathFindBehavior behavior;
   String owner;
   boolean bDone;
   IsoGameCharacter aowner;

   public WalkToLastKnownLocationOf() {
      this.res = Behavior.BehaviorResult.Working;
      this.behavior = new PathFindBehavior(true);
      this.bDone = false;
   }

   public void init(String var1, String[] var2) {
      if (var2.length == 1) {
         this.chara = var2[0].trim();
         this.owner = var1;
      }

   }

   public void begin() {
      if (this.module.getCharacter(this.owner).Actual == null) {
         throw new InvalidParameterException();
      } else if (this.module.getCharacter(this.chara).Actual == null) {
         throw new InvalidParameterException();
      } else {
         this.aowner = this.module.getCharacter(this.owner).Actual;
         IsoGameCharacter.Location var1 = this.aowner.getLastKnownLocationOf(this.chara);
         if (var1 != null) {
            this.behavior.sx = (int)this.aowner.getX();
            this.behavior.sy = (int)this.aowner.getY();
            this.behavior.sz = (int)this.aowner.getZ();
            this.behavior.tx = var1.x;
            this.behavior.ty = var1.y;
            this.behavior.tz = var1.z;
            this.behavior.pathIndex = 0;
         } else {
            this.bDone = true;
         }

         this.timer = 10;
      }
   }

   public boolean AllowCharacterBehaviour(String var1) {
      return false;
   }

   public void Finish() {
      this.aowner = null;
      this.res = Behavior.BehaviorResult.Working;
      this.behavior.reset();
   }

   public boolean IsFinished() {
      return this.bDone || this.res == Behavior.BehaviorResult.Succeeded;
   }

   public void update() {
      if (this.res == Behavior.BehaviorResult.Failed) {
         this.begin();
      }

      this.res = this.behavior.process((DecisionPath)null, this.aowner);
   }

   public boolean DoesInstantly() {
      return false;
   }
}
