package zombie;

import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.astar.AStarPathMap;
import zombie.ai.astar.IPathfinder;
import zombie.ai.astar.Mover;
import zombie.ai.astar.heuristics.ManhattanHeuristic;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.debug.DebugLog;
import zombie.gameStates.IngameState;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;

public class PathfindManager {
   public static PathfindManager instance = new PathfindManager();
   public int MaxThreads = 1;
   public int Current = 0;
   public int maxInBatch = 1;
   public static int Failed = 0;
   public static int Succeeded = 0;
   public static float ZombiePathTime = 0.0F;
   public static float SurvivorPathTime = 0.0F;
   public HashMap Pathfind = new HashMap();
   public static float HighestPathTime = 0.0F;
   public PathfindManager.PathfindJob HighestJob = null;
   HashMap JobMap = new HashMap();
   public PathfindManager.PathfindThread[] threads;

   public boolean allPaused() {
      for(int var1 = 0; var1 < this.MaxThreads; ++var1) {
         if (!this.threads[var1].bPaused || this.threads[var1].processing) {
            return false;
         }
      }

      return true;
   }

   private void run() throws InterruptedException {
      PathfindManager.PathfindThread var1 = (PathfindManager.PathfindThread)Thread.currentThread();
      var1.map = IsoWorld.instance.CurrentCell.getPathMap();
      var1.finder = new AStarPathFinder((IsoGameCharacter)null, var1.map, 50, true, new ManhattanHeuristic(1));

      while(!var1.bDone) {
         var1.processing = true;
         int var2 = this.maxInBatch;
         boolean var3 = true;
         var1.processing = true;
         synchronized(var1.Jobs) {
            while(!var1.Jobs.isEmpty() && ((PathfindManager.PathfindJob)var1.Jobs.get(0)).finished) {
               var1.Jobs.remove(0);
            }

            var3 = var1.Jobs.isEmpty();
         }

         var1.processing = false;
         if (var3) {
            var1.bPaused = true;
         } else {
            var1.bPaused = false;
         }

         for(; !var3 && var2 > 0; var1.processing = false) {
            PathfindManager.PathfindJob var4 = null;
            var1.processing = true;
            synchronized(var1.Jobs) {
               var4 = (PathfindManager.PathfindJob)var1.Jobs.get(0);
            }

            var1.processing = false;
            if (var4.mover instanceof IsoPlayer) {
               boolean var5 = false;
            }

            while(IngameState.AlwaysDebugPathfinding) {
               Thread.sleep(16L);
            }

            do {
               if (!var1.SwitchingCells) {
                  var1.bPaused = false;
                  var4.Process(var1.ID, var1.finder);
               } else {
                  var1.bPaused = true;
               }

               Thread.sleep(16L);
            } while(var4.isNotFinished(var1.finder));

            while(var1.SwitchingCells) {
               var1.bPaused = true;
               Thread.sleep(16L);
            }

            var1.bPaused = false;
            var1.processing = true;
            synchronized(var1.Jobs) {
               var1.Jobs.remove(0);
               var4.finished = true;
            }

            var1.processing = false;
            --var2;
            var1.processing = true;
            synchronized(var1.Jobs) {
               var3 = var1.Jobs.isEmpty();
            }
         }

         try {
            Thread.sleep(60L);
         } catch (InterruptedException var9) {
         }
      }

   }

   public AStarPathFinder getFinder() {
      return this.threads[0].finder;
   }

   public void DoDebugJob(int var1, int var2, int var3, int var4, int var5, int var6) {
      while(this.threads[0].processing) {
         try {
            Thread.sleep(1L);
         } catch (InterruptedException var8) {
            Logger.getLogger(PathfindManager.class.getName()).log(Level.SEVERE, (String)null, var8);
         }
      }

      IngameState.AlwaysDebugPathfinding = true;
      this.threads[0].finder.findPathActual((Mover)null, var1, var2, var3, var4, var5, var6);
      this.threads[0].finder.Cycle(0);
      IngameState.AlwaysDebugPathfinding = false;
   }

   public void reset() {
      for(int var1 = 0; var1 < this.threads.length; ++var1) {
         this.threads[var1] = new PathfindManager.PathfindThread(new Runnable() {
            public void run() {
               try {
                  PathfindManager.instance.run();
               } catch (InterruptedException var2) {
                  Logger.getLogger(LOSThread.class.getName()).log(Level.SEVERE, (String)null, var2);
               }

            }
         });
         this.threads[var1].setName("PathFind" + var1);
         this.threads[var1].ID = var1;
         this.threads[var1].setPriority(2);
         this.threads[var1].start();
      }

   }

   public void stop() {
      this.Pathfind.clear();
      this.HighestJob = null;

      for(int var1 = 0; var1 < this.threads.length; ++var1) {
         this.threads[var1].bDone = true;

         while(this.threads[var1].isAlive()) {
         }

         this.threads[var1].Jobs.clear();
         this.threads[var1].reset();
         this.threads[var1] = null;
      }

      this.threads = null;
      this.JobMap.clear();
   }

   public void init() {
      this.MaxThreads = 1;
      this.threads = new PathfindManager.PathfindThread[this.MaxThreads];
      IsoWorld.instance.CurrentCell.InitNodeMap(this.MaxThreads);

      for(int var1 = 0; var1 < this.MaxThreads; ++var1) {
         this.threads[var1] = new PathfindManager.PathfindThread(new Runnable() {
            public void run() {
               try {
                  PathfindManager.instance.run();
               } catch (InterruptedException var2) {
                  Logger.getLogger(LOSThread.class.getName()).log(Level.SEVERE, (String)null, var2);
               }

            }
         });
         this.threads[var1].setName("PathFind" + var1);
         this.threads[var1].ID = var1;
         if (var1 <= 1) {
            this.threads[var1].setPriority(2);
         } else {
            this.threads[var1].setPriority(2);
         }

         this.threads[var1].setDaemon(true);
         this.threads[var1].start();
      }

   }

   public void AddJob(IPathfinder var1, Mover var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      this.AddJob(var1, var2, var3, var4, var5, var6, var7, var8, true);
   }

   public void AddJob(IPathfinder var1, Mover var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9) {
      float var10 = IsoUtils.DistanceManhatten((float)var3, (float)var4, (float)var6, (float)var7);
      PathfindManager.PathfindJob var11;
      if (this.JobMap.containsKey(var2)) {
         var11 = (PathfindManager.PathfindJob)this.JobMap.get(var2);
         if (!var11.finished && var11.sx == var3 && var11.sy == var4 && var11.sz == var5 && var11.tx == var6 && var11.ty == var7 && var11.tz == var8) {
            return;
         }

         var11.finished = true;
         synchronized(this.threads[0].Jobs) {
            this.threads[0].Jobs.remove(var11);
         }

         this.JobMap.remove(var2);
      }

      if (var2 == IsoCamera.CamCharacter) {
         boolean var19 = false;
      }

      if (var8 < IsoWorld.instance.CurrentCell.getMaxFloors() && var8 >= 0) {
         if (var5 < IsoWorld.instance.CurrentCell.getMaxFloors() && var5 >= 0) {
            var11 = new PathfindManager.PathfindJob();
            var11.mover = var2;
            var11.sx = var3;
            var11.sy = var4;
            var11.sz = var5;
            var11.tx = var6;
            var11.ty = var7;
            var11.tz = var8;
            var11.bClosest = true;
            var11.pathfinder = var1;
            this.JobMap.put(var2, var11);
            if (IngameState.DebugPathfinding) {
               var11.Process(0, this.threads[this.Current].finder);
            } else {
               boolean var12 = true;
               boolean var13 = true;
               boolean var14 = true;
               synchronized(this.threads[0].Jobs) {
                  this.threads[0].Jobs.add(var11);
               }
            }
         } else {
            var1.Failed(var2);
         }
      } else {
         var1.Failed(var2);
      }
   }

   public void abortJob(Mover var1) {
      PathfindManager.PathfindJob var2 = (PathfindManager.PathfindJob)this.JobMap.remove(var1);
      if (var2 != null) {
         var2.finished = true;
      }

   }

   public void pause() {
      long var1 = System.nanoTime();

      for(int var3 = 0; var3 < this.MaxThreads; ++var3) {
         this.threads[var3].SwitchingCells = true;
      }

      while(!this.allPaused()) {
         try {
            Thread.sleep(4L);
         } catch (InterruptedException var5) {
            var5.printStackTrace();
         }
      }

      long var6 = System.nanoTime() - var1;
      if ((double)var6 > 5.0E7D) {
         DebugLog.log("PathfindManager.pause() took " + (double)var6 / 1000000.0D + "ms");
      }

   }

   public void resume() {
      for(int var1 = 0; var1 < this.MaxThreads; ++var1) {
         this.threads[var1].SwitchingCells = false;
         this.threads[var1].SwitchedCells = true;
      }

   }

   public class PathfindThread extends Thread {
      public boolean bPaused;
      public boolean bDone;
      public boolean Catchup;
      public int ID;
      public Stack Jobs;
      public AStarPathFinder finder;
      public AStarPathMap map;
      private boolean processing;
      public boolean SwitchedCells;
      public boolean SwitchingCells;

      private PathfindThread(Runnable var2) {
         super(var2);
         this.bPaused = false;
         this.bDone = false;
         this.Catchup = false;
         this.ID = 0;
         this.Jobs = new Stack();
         this.processing = false;
         this.SwitchedCells = false;
         this.SwitchingCells = false;
      }

      private void reset() {
         this.map = null;
         this.finder = null;
      }

      // $FF: synthetic method
      PathfindThread(Runnable var2, Object var3) {
         this(var2);
      }
   }

   public class PathfindJob {
      public int sx;
      public int sy;
      public int sz;
      public int tx;
      public int ty;
      public int tz;
      public Mover mover;
      public IPathfinder pathfinder;
      public volatile boolean finished = false;
      public boolean bClosest;

      private void Process(int var1, AStarPathFinder var2) {
         if (this.mover == IsoCamera.CamCharacter) {
            boolean var3 = false;
         }

         try {
            var2.findPathActual(this.mover, this.sx, this.sy, this.sz, this.tx, this.ty, this.tz, this.bClosest);
            var2.Cycle(var1, this);
         } catch (Exception var4) {
         }

         if (this.pathfinder != null) {
            if (var2.progress == AStarPathFinder.PathFindProgress.found) {
               this.pathfinder.Succeeded(var2.getPath(), this.mover);
               ++PathfindManager.Succeeded;
            } else if (var2.progress != AStarPathFinder.PathFindProgress.notrunning) {
               if (PathfindManager.instance.threads[var1].SwitchedCells) {
                  PathfindManager.instance.threads[var1].SwitchedCells = false;
                  var2.progress = AStarPathFinder.PathFindProgress.notyetfound;
                  return;
               }

               this.pathfinder.Failed(this.mover);
               ++PathfindManager.Failed;
            }
         }

      }

      public boolean isNotFinished(AStarPathFinder var1) {
         return var1.progress == AStarPathFinder.PathFindProgress.notyetfound;
      }
   }
}
