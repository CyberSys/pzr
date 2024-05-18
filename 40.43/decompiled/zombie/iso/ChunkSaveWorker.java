package zombie.iso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.GameWindow;
import zombie.core.Core;
import zombie.inventory.ItemContainer;
import zombie.network.GameServer;

public class ChunkSaveWorker {
   public static ChunkSaveWorker instance = new ChunkSaveWorker();
   public ConcurrentLinkedQueue toSaveQueue = new ConcurrentLinkedQueue();
   public Stack toSaveContainers = new Stack();
   public Stack toLoadContainers = new Stack();
   public boolean bSaving;
   private final ArrayList tempList = new ArrayList();

   public void LoadContainers() throws IOException {
      for(int var1 = 0; var1 < this.toLoadContainers.size(); ++var1) {
         ItemContainer var2 = (ItemContainer)this.toLoadContainers.get(var1);
         var2.doLoadActual();
      }

      this.toLoadContainers.clear();
   }

   public void SaveContainers() throws IOException {
      for(int var1 = 0; var1 < this.toSaveContainers.size(); ++var1) {
         ItemContainer var2 = (ItemContainer)this.toSaveContainers.get(var1);
         File var3 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_con_" + var2.ID + ".bin");
         FileOutputStream var4 = new FileOutputStream(var3);

         try {
            synchronized(SliceY.SliceBuffer) {
               SliceY.SliceBuffer.rewind();
               BufferedOutputStream var6 = new BufferedOutputStream(var4);
               var2.save(SliceY.SliceBuffer, false);
               var6.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
               var6.close();
            }
         } catch (Exception var13) {
            var13.printStackTrace();
         } finally {
            var4.close();
         }
      }

      this.toSaveContainers.clear();
   }

   public void Update(IsoChunk var1) {
      if (!GameServer.bServer) {
         IsoChunk var2 = null;
         IsoChunk var3 = null;
         this.bSaving = !this.toSaveQueue.isEmpty();
         if (this.bSaving) {
            if (var1 != null) {
               Iterator var4 = this.toSaveQueue.iterator();

               while(var4.hasNext()) {
                  var3 = (IsoChunk)var4.next();
                  if (var3.wx == var1.wx && var3.wy == var1.wy) {
                     var2 = var3;
                     break;
                  }
               }
            }

            if (var2 != null) {
            }

            if (var2 == null) {
               var2 = (IsoChunk)this.toSaveQueue.poll();
            } else {
               this.toSaveQueue.remove(var2);
            }

            if (var2 != null) {
               try {
                  var2.Save(false);
               } catch (Exception var6) {
                  var6.printStackTrace();
               }

               try {
                  instance.SaveContainers();
               } catch (Exception var5) {
                  var5.printStackTrace();
               }

            }
         }
      }
   }

   public void SaveNow(ArrayList var1) {
      this.tempList.clear();

      for(IsoChunk var2 = (IsoChunk)this.toSaveQueue.poll(); var2 != null; var2 = (IsoChunk)this.toSaveQueue.poll()) {
         boolean var3 = false;

         for(int var4 = 0; var4 < var1.size(); ++var4) {
            IsoChunk var5 = (IsoChunk)var1.get(var4);
            if (var2.wx == var5.wx && var2.wy == var5.wy) {
               try {
                  var2.Save(false);
               } catch (IOException var7) {
                  var7.printStackTrace();
               }

               var3 = true;
               break;
            }
         }

         if (!var3) {
            this.tempList.add(var2);
         }
      }

      for(int var8 = 0; var8 < this.tempList.size(); ++var8) {
         this.toSaveQueue.add(this.tempList.get(var8));
      }

      this.tempList.clear();
   }

   public void SaveNow() {
      for(IsoChunk var1 = (IsoChunk)this.toSaveQueue.poll(); var1 != null; var1 = (IsoChunk)this.toSaveQueue.poll()) {
         try {
            var1.Save(false);
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

      this.bSaving = false;
   }

   public void Add(IsoChunk var1) {
      if (!this.toSaveQueue.contains(var1)) {
         this.toSaveQueue.add(var1);
      }

   }
}
