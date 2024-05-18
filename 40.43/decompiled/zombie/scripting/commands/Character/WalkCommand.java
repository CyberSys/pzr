package zombie.scripting.commands.Character;

import java.security.InvalidParameterException;
import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoWorld;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Waypoint;

public class WalkCommand extends BaseCommand {
   int x;
   int y;
   int z;
   Behavior.BehaviorResult res;
   PathFindBehavior behavior;
   String owner;
   IsoGameCharacter aowner;

   public void updateskip() {
      IsoGameCharacter var1 = this.module.getCharacterActual(this.owner);
      var1.setX((float)this.x + 0.5F);
      var1.setY((float)this.y + 0.5F);
      var1.setZ((float)this.z);
      var1.getCurrentSquare().getMovingObjects().remove(var1);
      var1.setCurrent(IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, this.z));
      if (var1.getCurrentSquare() != null) {
         var1.getCurrentSquare().getMovingObjects().add(var1);
      }

   }

   public WalkCommand() {
      this.res = Behavior.BehaviorResult.Working;
      this.behavior = new PathFindBehavior(true);
   }

   public void init(String var1, String[] var2) {
      if (var2.length == 1) {
         Waypoint var3 = this.module.getWaypoint(var2[0].trim());
         if (var3 != null) {
            this.x = var3.x;
            this.y = var3.y;
            this.z = var3.z;
         }

         this.owner = var1;
      }

   }

   public void begin() {
      if (this.module.getCharacter(this.owner).Actual == null) {
         throw new InvalidParameterException();
      } else {
         this.aowner = this.module.getCharacter(this.owner).Actual;
         this.behavior.sx = (int)this.aowner.getX();
         this.behavior.sy = (int)this.aowner.getY();
         this.behavior.sz = (int)this.aowner.getZ();
         this.behavior.tx = this.x;
         this.behavior.ty = this.y;
         this.behavior.tz = this.z;
         this.behavior.pathIndex = 0;
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
      return this.res == Behavior.BehaviorResult.Succeeded;
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
