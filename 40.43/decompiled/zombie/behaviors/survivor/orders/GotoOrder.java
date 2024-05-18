package zombie.behaviors.survivor.orders;

import java.util.Stack;
import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.ui.TextManager;
import zombie.ui.UIFont;

public class GotoOrder extends Order {
   public int failedcount = 0;
   int x;
   int y;
   int z;
   public Stack Waypoints = new Stack();
   public int currentwaypoint = 0;
   float nextpathfind = 10.0F;
   PathFindBehavior PathFind = new PathFindBehavior("Goto");
   public IsoGameCharacter chr;
   static Vector2 vec = new Vector2();
   Behavior.BehaviorResult res;

   private GotoOrder.Waypoint AddIntermediate(float var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      new Vector2((float)var5, (float)var6);
      Vector2 var9 = new Vector2((float)var2, (float)var3);
      Vector2 var10 = new Vector2((float)(var5 - var2), (float)(var6 - var3));
      var10.setLength(var1 / 2.0F);
      var9.x += var10.x;
      var9.y += var10.y;
      int var11 = 0;
      IsoGridSquare var12 = null;

      do {
         int var13 = var11 * 3;
         if (var13 == 0) {
            var13 = 1;
         }

         for(int var14 = 0; var14 < var13; ++var14) {
            int var15 = (int)var9.x + Rand.Next(-var11, var11);
            int var16 = (int)var9.y + Rand.Next(-var11, var11);
            var12 = IsoWorld.instance.CurrentCell.getGridSquare(var15, var16, 0);
            if (!var12.isFree(false)) {
               var12 = null;
            }

            if (var12 != null) {
               var14 = var13;
            }
         }

         if (var12 == null) {
            ++var11;
         }
      } while(var12 == null);

      return new GotoOrder.Waypoint(var12.getX(), var12.getY(), 0);
   }

   private void SplitWaypoints(int var1, int var2, int var3, int var4, int var5, int var6) {
      Stack var7 = new Stack();
      byte var15 = 0;
      int var8 = this.Waypoints.size();

      for(int var9 = 0; var9 < var8; ++var9) {
         int var10 = var4;
         int var11 = var5;
         if (var8 > var9 + 1) {
            var10 = ((GotoOrder.Waypoint)this.Waypoints.get(var9 + 1)).x;
            var11 = ((GotoOrder.Waypoint)this.Waypoints.get(var9 + 1)).y;
         }

         float var13 = IsoUtils.DistanceManhatten((float)var1, (float)var2, (float)((GotoOrder.Waypoint)this.Waypoints.get(var9)).x, (float)((GotoOrder.Waypoint)this.Waypoints.get(var9)).y);
         GotoOrder.Waypoint var14 = this.AddIntermediate(var13, var1, var2, var15, var10, var11, var6);
         var1 = ((GotoOrder.Waypoint)this.Waypoints.get(var9)).x;
         var2 = ((GotoOrder.Waypoint)this.Waypoints.get(var9)).y;
         var7.add(var14);
         var7.add(this.Waypoints.get(var9));
      }

      this.Waypoints.clear();
      this.Waypoints.addAll(var7);
   }

   public int getAttackIfEnemiesAroundBias() {
      return this.character.getCurrentSquare().getRoom() != null ? -1000 : 0;
   }

   private void CalculateWaypoints(IsoGameCharacter var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.Waypoints.clear();
      this.Waypoints.add(new GotoOrder.Waypoint(var5, var6, var7));
      float var8 = IsoUtils.DistanceManhatten((float)var2, (float)var3, (float)var5, (float)var6);
      int var9 = (int)(var8 / 60.0F);
      if (this.failedcount > 2 && var8 > 60.0F) {
         var9 += this.failedcount - 2;
      }

      if (var9 > 4) {
         boolean var10 = true;
      }

   }

   public GotoOrder(IsoGameCharacter var1, int var2, int var3, int var4) {
      super(var1);
      this.res = Behavior.BehaviorResult.Working;
      this.PathFind.bDoClosest = true;
      this.chr = var1;
      this.x = var2;
      this.y = var3;
      this.z = var4;
      this.PathFind.tx = var2;
      this.PathFind.ty = var3;
      this.PathFind.tz = var4;
      this.nextpathfind = (float)Rand.Next(10);
      this.CalculateWaypoints(var1, (int)var1.getX(), (int)var1.getY(), (int)var1.getZ(), var2, var3, var4);
      this.currentwaypoint = 0;
      this.PathFind.reset();
      this.PathFind.sx = (int)var1.getX();
      this.PathFind.sy = (int)var1.getY();
      this.PathFind.sz = (int)var1.getZ();
      this.PathFind.tx = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).x;
      this.PathFind.ty = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).y;
      this.PathFind.tz = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).z;
      this.TestOutStreamRange(var1, var4);
      this.PathFind.otx = this.PathFind.tx;
      this.PathFind.oty = this.PathFind.ty;
      this.PathFind.otz = this.PathFind.tz;
   }

   private void TestOutStreamRange(IsoGameCharacter var1, int var2) {
      IsoChunkMap var3 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()];
      if (this.PathFind.tx >= var3.getWorldXMinTiles() && this.PathFind.tx <= var3.getWorldXMaxTiles() && this.PathFind.ty >= var3.getWorldYMinTiles() && this.PathFind.ty <= var3.getWorldYMaxTiles()) {
         boolean var8 = false;
      } else {
         vec.x = (float)this.PathFind.tx;
         vec.y = (float)this.PathFind.ty;
         Vector2 var10000 = vec;
         var10000.x -= (float)this.PathFind.sx;
         var10000 = vec;
         var10000.y -= (float)this.PathFind.sy;
         if (vec.x != 0.0F || vec.y != 0.0F) {
            var10000 = vec;
            var10000.x += (float)(Rand.Next(50) - 25);
            var10000 = vec;
            var10000.y += (float)(Rand.Next(50) - 25);
            vec.normalize();
            var10000 = vec;
            var10000.x *= 2.0F;
            var10000 = vec;
            var10000.y *= 2.0F;
            IsoGridSquare var4 = var1.getCurrentSquare();
            IsoGridSquare var5 = null;
            float var6 = (float)this.PathFind.sx;
            float var7 = (float)this.PathFind.sy;

            do {
               var5 = var4;
               var4 = IsoWorld.instance.CurrentCell.getGridSquare((double)var6, (double)var7, (double)var2);
               var6 += vec.x;
               var7 += vec.y;
            } while(var4 != null);

            while(var5 == null || !var5.isFree(false)) {
               var6 -= vec.x;
               var7 -= vec.y;
               var5 = IsoWorld.instance.CurrentCell.getGridSquare((double)var6, (double)var7, (double)var2);
            }

            this.PathFind.tx = var5.getX();
            this.PathFind.ty = var5.getY();
            this.PathFind.bDoClosest = true;
         }
      }

   }

   public GotoOrder(IsoGameCharacter var1) {
      super(var1);
      this.res = Behavior.BehaviorResult.Working;
      this.chr = var1;
   }

   public void init(int var1, int var2, int var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.PathFind.tx = var1;
      this.PathFind.ty = var2;
      this.PathFind.tz = var3;
      this.nextpathfind = (float)Rand.Next(120);
      this.CalculateWaypoints(this.chr, (int)this.chr.getX(), (int)this.chr.getY(), (int)this.chr.getZ(), var1, var2, var3);
      this.currentwaypoint = 0;
      this.PathFind.reset();
      this.PathFind.sx = (int)this.chr.getX();
      this.PathFind.sy = (int)this.chr.getY();
      this.PathFind.sz = (int)this.chr.getZ();
      this.PathFind.tx = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).x;
      this.PathFind.ty = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).y;
      this.PathFind.tz = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).z;
      this.PathFind.osx = this.PathFind.sx;
      this.PathFind.osy = this.PathFind.sy;
      this.PathFind.osz = this.PathFind.sz;
      this.TestOutStreamRange(this.chr, var3);
      this.PathFind.otx = this.PathFind.tx;
      this.PathFind.oty = this.PathFind.ty;
      this.PathFind.otz = this.PathFind.tz;
   }

   public boolean complete() {
      return this.currentwaypoint >= this.Waypoints.size() || this.failedcount > 20;
   }

   public void update() {
      if (this.res != Behavior.BehaviorResult.Working) {
         --this.nextpathfind;
      }

   }

   public Behavior.BehaviorResult process() {
      boolean var1;
      if (this.character == IsoCamera.CamCharacter) {
         var1 = false;
      }

      this.res = this.PathFind.process((DecisionPath)null, this.character);
      if (this.res == Behavior.BehaviorResult.Succeeded && this.currentwaypoint < this.Waypoints.size()) {
         GotoOrder.Waypoint var4 = (GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint);
         if ((int)this.character.getX() == var4.x && (int)this.character.getY() == var4.y && (int)this.character.getZ() == var4.z) {
            this.nextpathfind = -1.0F;
         } else {
            this.res = Behavior.BehaviorResult.Failed;
         }
      }

      if (this.res == Behavior.BehaviorResult.Failed && this.nextpathfind < 0.0F) {
         ++this.failedcount;
         if (this.failedcount > 100) {
         }

         if (this.character == IsoCamera.CamCharacter) {
            var1 = false;
         }

         this.nextpathfind = 10.0F;
         this.CalculateWaypoints(this.chr, (int)this.chr.getX(), (int)this.chr.getY(), (int)this.chr.getZ(), this.x, this.y, this.z);
         this.currentwaypoint = 0;
         this.PathFind.reset();
         this.PathFind.sx = (int)this.chr.getX();
         this.PathFind.sy = (int)this.chr.getY();
         this.PathFind.sz = (int)this.chr.getZ();
         this.PathFind.tx = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).x;
         this.PathFind.ty = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).y;
         this.PathFind.tz = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).z;
         this.PathFind.osx = this.PathFind.sx;
         this.PathFind.osy = this.PathFind.sy;
         this.PathFind.osz = this.PathFind.sz;
         this.TestOutStreamRange(this.chr, this.z);
         this.PathFind.otx = this.PathFind.tx;
         this.PathFind.oty = this.PathFind.ty;
         this.PathFind.otz = this.PathFind.tz;
         this.res = this.PathFind.process((DecisionPath)null, this.character);
      }

      if (this.res == Behavior.BehaviorResult.Succeeded && this.currentwaypoint < this.Waypoints.size() && this.nextpathfind < 0.0F) {
         if (this.x != this.PathFind.tx || this.y != this.PathFind.ty) {
            this.PathFind.reset();
            Behavior.BehaviorResult var10000 = this.res;
            return Behavior.BehaviorResult.Working;
         }

         if (this.character == IsoCamera.CamCharacter) {
            var1 = false;
         }

         ++this.currentwaypoint;
         this.nextpathfind = 10.0F;
         if (this.currentwaypoint < this.Waypoints.size()) {
            int var5 = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).x;
            int var2 = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).y;
            int var3 = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).z;
            this.x = var5;
            this.y = var2;
            this.z = var3;
            this.PathFind.reset();
            this.PathFind.tx = var5;
            this.PathFind.ty = var2;
            this.PathFind.tz = var3;
            this.PathFind.sx = (int)this.chr.getX();
            this.PathFind.sy = (int)this.chr.getY();
            this.PathFind.sz = (int)this.chr.getZ();
            this.res = this.PathFind.process((DecisionPath)null, this.character);
         } else {
            this.res = Behavior.BehaviorResult.Succeeded;
         }
      }

      --this.nextpathfind;
      return this.currentwaypoint >= this.Waypoints.size() ? Behavior.BehaviorResult.Succeeded : Behavior.BehaviorResult.Working;
   }

   public int renderDebug(int var1) {
      byte var2 = 50;
      TextManager.instance.DrawString(UIFont.Small, (double)var2, (double)var1, "GotoOrder", 1.0D, 1.0D, 1.0D, 1.0D);
      var1 += 30;

      for(int var3 = this.currentwaypoint; var3 < this.Waypoints.size(); ++var3) {
         GotoOrder.Waypoint var4 = (GotoOrder.Waypoint)this.Waypoints.get(var3);
         Integer var5 = var4.x;
         Integer var6 = var4.y;
         Integer var7 = var4.z;
         TextManager.instance.DrawString(UIFont.Small, (double)var2, (double)var1, "Waypoint " + var3 + " - x: " + var5 + " y: " + var6 + " z: " + var7, 1.0D, 1.0D, 1.0D, 1.0D);
         var1 += 30;
      }

      this.PathFind.renderDebug(var1);
      return var1;
   }

   public float getPriority(IsoGameCharacter var1) {
      return this.res != Behavior.BehaviorResult.Working && !(this.nextpathfind < 0.0F) ? -100.0F : 200.0F;
   }

   public static class Waypoint {
      public int x;
      public int y;
      public int z;

      public Waypoint(int var1, int var2, int var3) {
         this.x = var1;
         this.y = var2;
         this.z = var3;
      }
   }
}
