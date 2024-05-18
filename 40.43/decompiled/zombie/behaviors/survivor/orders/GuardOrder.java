package zombie.behaviors.survivor.orders;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.iso.IsoGridSquare;
import zombie.iso.Vector2;

public class GuardOrder extends Order {
   public int Range = 6;
   public boolean StayInRoom = true;
   PathFindBehavior PathFind = new PathFindBehavior("Guard");
   IsoGridSquare GuardStand;
   IsoGridSquare GuardFace = null;
   Vector2 vec = new Vector2();

   public GuardOrder(IsoSurvivor var1, IsoGridSquare var2, IsoGridSquare var3) {
      super(var1);
      this.GuardFace = var3;
      this.GuardStand = var2;
      this.PathFind.sx = this.character.getCurrentSquare().getX();
      this.PathFind.sy = this.character.getCurrentSquare().getY();
      this.PathFind.sz = this.character.getCurrentSquare().getZ();
      this.PathFind.tx = var2.getX();
      this.PathFind.ty = var2.getY();
      this.PathFind.tz = var2.getZ();
   }

   GuardOrder(IsoGameCharacter var1) {
      super(var1);
   }

   public Behavior.BehaviorResult process() {
      if (this.GuardFace == null) {
         return Behavior.BehaviorResult.Working;
      } else {
         if (this.character.getCurrentSquare() != this.GuardStand) {
            this.PathFind.tx = this.GuardStand.getX();
            this.PathFind.ty = this.GuardStand.getY();
            this.PathFind.tz = this.GuardStand.getZ();
            this.PathFind.process((DecisionPath)null, this.character);
         } else {
            this.vec.x = (float)this.GuardFace.getX() + 0.5F;
            this.vec.y = (float)this.GuardFace.getY() + 0.5F;
            Vector2 var10000 = this.vec;
            var10000.x -= this.character.getX();
            var10000 = this.vec;
            var10000.y -= this.character.getY();
            this.vec.normalize();
            this.character.DirectionFromVector(this.vec);
         }

         return Behavior.BehaviorResult.Working;
      }
   }

   public boolean complete() {
      return false;
   }

   public void update() {
   }

   public float getPriority(IsoGameCharacter var1) {
      return 200.0F;
   }
}
