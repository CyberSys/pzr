package zombie.iso;

import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.iso.objects.IsoTree;

public class WorldReuserThread {
   public static WorldReuserThread instance = new WorldReuserThread();
   private ConcurrentLinkedQueue reuseGridSquares = new ConcurrentLinkedQueue();
   public Stack objectsToReuse = new Stack();
   public Stack treesToReuse = new Stack();
   Thread worldReuser;
   public boolean finished;

   public void run() {
      this.worldReuser = new Thread(new Runnable() {
         public void run() {
            while(!WorldReuserThread.this.finished) {
               WorldReuserThread.this.testReuseChunk();
               WorldReuserThread.this.reconcileReuseObjects();

               try {
                  Thread.sleep(1000L);
               } catch (InterruptedException var2) {
                  var2.printStackTrace();
               }
            }

         }
      });
      this.worldReuser.setName("WorldReuser");
      this.worldReuser.setDaemon(true);
      this.worldReuser.start();
   }

   public void reconcileReuseObjects() {
      synchronized(this.objectsToReuse) {
         if (!this.objectsToReuse.isEmpty()) {
            synchronized(CellLoader.isoObjectCache) {
               CellLoader.isoObjectCache.addAll(this.objectsToReuse);
            }

            this.objectsToReuse.clear();
         }
      }

      synchronized(this.treesToReuse) {
         if (!this.treesToReuse.isEmpty()) {
            synchronized(CellLoader.isoTreeCache) {
               CellLoader.isoTreeCache.addAll(this.treesToReuse);
            }

            this.treesToReuse.clear();
         }

      }
   }

   public void testReuseChunk() {
      for(IsoChunk var1 = (IsoChunk)this.reuseGridSquares.poll(); var1 != null; var1 = (IsoChunk)this.reuseGridSquares.poll()) {
         if (Core.bDebug) {
            if (ChunkSaveWorker.instance.toSaveQueue.contains(var1)) {
               DebugLog.log("ERROR: reusing chunk that needs to be saved");
            }

            if (IsoChunkMap.chunkStore.contains(var1)) {
               DebugLog.log("ERROR: reusing chunk in chunkStore");
            }

            if (!var1.refs.isEmpty()) {
               DebugLog.log("ERROR: reusing chunk with refs");
            }
         }

         if (Core.bDebug) {
         }

         this.reuseGridSquares(var1);
      }

   }

   public void addReuseChunk(IsoChunk var1) {
      this.reuseGridSquares.add(var1);
   }

   public void reuseGridSquares(IsoChunk var1) {
      byte var2 = 100;

      for(int var3 = 0; var3 < 8; ++var3) {
         for(int var4 = 0; var4 < var2; ++var4) {
            IsoGridSquare var5 = var1.squares[var3][var4];
            if (var5 != null) {
               for(int var6 = 0; var6 < var5.getObjects().size(); ++var6) {
                  IsoObject var7 = (IsoObject)var5.getObjects().get(var6);
                  if (var7 instanceof IsoTree) {
                     var7.reset();
                     synchronized(this.treesToReuse) {
                        this.treesToReuse.add((IsoTree)var7);
                     }
                  } else if (var7 instanceof IsoObject && var7.getObjectName().equals("IsoObject")) {
                     var7.reset();
                     synchronized(this.objectsToReuse) {
                        this.objectsToReuse.add(var7);
                     }
                  } else {
                     var7.reuseGridSquare();
                  }
               }

               var5.discard();
               var1.squares[var3][var4] = null;
            }
         }
      }

      var1.resetForStore();
      IsoChunkMap.chunkStore.add(var1);
   }
}
