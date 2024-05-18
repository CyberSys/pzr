package zombie.behaviors.survivor;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.behaviors.survivor.orders.FollowOrder;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.iso.IsoCamera;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.areas.IsoBuilding;
import zombie.ui.TextManager;
import zombie.ui.UIFont;

public class FleeBehaviour extends Behavior {
   public boolean Started = false;
   boolean OtherRoom = false;
   PathFindBehavior pathFind = new PathFindBehavior();
   IsoGridSquare sq = null;
   IsoGameCharacter character;
   int recalc = 240;
   FollowOrder order;
   public boolean bFollowFlee = false;
   public boolean bPickedFleeStyle = false;
   static Vector2 tempo = new Vector2();

   public void onSwitch() {
      this.bFollowFlee = false;
      this.bPickedFleeStyle = false;
   }

   public Behavior.BehaviorResult process(DecisionPath var1, IsoGameCharacter var2) {
      if (this.order == null) {
         this.order = new FollowOrder(var2);
      }

      this.pathFind.name = "FleeBehaviour";
      this.character = var2;
      Behavior.BehaviorResult var3 = Behavior.BehaviorResult.Failed;
      --this.recalc;
      if (!this.Started) {
         this.Started = true;
         this.OtherRoom = false;
         float var4 = 0.0F;
         float var5 = 0.0F;

         for(int var6 = 0; var6 < var2.getLocalEnemyList().size(); ++var6) {
            IsoGameCharacter var7 = (IsoGameCharacter)var2.getLocalEnemyList().get(var6);
            float var8 = var7.x - var2.x;
            float var9 = var7.y - var2.y;
            var8 = -var8;
            var9 = -var9;
            Vector2 var10 = tempo;
            tempo.x = var8;
            tempo.y = var9;
            float var11 = var10.getLength();
            if (var11 == 0.0F) {
               var11 = 0.1F;
            }

            var10.normalize();
            var4 += var10.x / var11;
            var5 += var10.y / var11;
         }

         tempo.x = var4;
         tempo.y = var5;
         tempo.setLength(4.0F);
         var4 = tempo.x;
         var5 = tempo.y;
         this.sq = IsoWorld.instance.CurrentCell.getGridSquare((double)(var2.getX() + var4), (double)(var2.getY() + var5), 0.0D);
      }

      if (this.sq != null) {
         if (!this.pathFind.running(var2)) {
            var2.setPathSpeed(0.08F);
            this.pathFind.reset();
            this.pathFind.sx = (int)var2.getX();
            this.pathFind.sy = (int)var2.getY();
            this.pathFind.sz = (int)var2.getZ();
            this.pathFind.tx = this.sq.getX();
            this.pathFind.ty = this.sq.getY();
            this.pathFind.tz = this.sq.getZ();
         }

         var3 = this.pathFind.process(var1, var2);
         if (var3 == Behavior.BehaviorResult.Failed) {
            this.sq = null;
            this.reset();
         } else if (var3 == Behavior.BehaviorResult.Succeeded) {
            var2.getStats().idleboredom = 0.0F;
            this.sq = null;
            this.reset();
         }
      }

      return Behavior.BehaviorResult.Working;
   }

   private boolean RunToBuilding(float var1, float var2, IsoGameCharacter var3) {
      boolean var4 = false;
      int var5 = 0;

      while(!var4) {
         ++var5;
         boolean var6 = false;
         boolean var7 = false;
         int var8 = Rand.Next(5) + 1;
         int var10 = (int)(var1 * (float)var8);
         int var11 = (int)(var2 * (float)var8);
         int var10000 = var10 + (Rand.Next(4) - 2);
         var10000 = var11 + (Rand.Next(4) - 2);
         IsoBuilding var9 = var3.getDescriptor().getGroup().Safehouse;
         if (var9 == null) {
            return false;
         }

         this.sq = var9.getRandomRoom().getFreeTile();
         if (var5 >= 20) {
            return true;
         }

         if (this.sq != null && !this.sq.getProperties().Is(IsoFlagType.solidtrans) && !this.sq.getProperties().Is(IsoFlagType.solid)) {
            var4 = true;
         }
      }

      return false;
   }

   public void reset() {
      this.Started = false;
      this.sq = null;
      this.pathFind.reset();
   }

   public boolean valid() {
      return true;
   }

   public float getPriority(IsoGameCharacter var1) {
      float var2 = 0.0F;
      if (var1 == IsoCamera.CamCharacter) {
         boolean var3 = false;
      }

      if (IsoPlayer.DemoMode && var1.IsArmed()) {
         return var1.getStats().endurance < var1.getStats().endurancewarn ? 1.0E8F : -100000.0F;
      } else {
         return !var1.IsArmed() && var1.getVeryCloseEnemyList().size() > 0 ? 1.0E8F : -1000000.0F;
      }
   }

   public float getPathSpeed() {
      return 0.08F;
   }

   public int renderDebug(int var1) {
      byte var2 = 50;
      TextManager.instance.DrawString(UIFont.Small, (double)var2, (double)var1, "FleeBehaviour", 1.0D, 1.0D, 1.0D, 1.0D);
      var1 += 30;
      return var1;
   }
}
