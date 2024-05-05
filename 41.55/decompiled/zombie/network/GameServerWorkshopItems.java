package zombie.network;

import java.util.ArrayList;
import java.util.Iterator;
import zombie.core.znet.ISteamWorkshopCallback;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.SteamWorkshop;
import zombie.core.znet.SteamWorkshopItem;
import zombie.debug.DebugLog;

public class GameServerWorkshopItems {
   private static void noise(String var0) {
      DebugLog.log("Workshop: " + var0);
   }

   public static boolean Install(ArrayList var0) {
      if (!GameServer.bServer) {
         return false;
      } else if (var0.isEmpty()) {
         return true;
      } else {
         ArrayList var1 = new ArrayList();
         Iterator var2 = var0.iterator();

         long var3;
         while(var2.hasNext()) {
            var3 = (Long)var2.next();
            GameServerWorkshopItems.WorkshopItem var5 = new GameServerWorkshopItems.WorkshopItem(var3);
            var1.add(var5);
         }

         while(true) {
            SteamUtils.runLoop();
            boolean var7 = false;

            for(int var8 = 0; var8 < var1.size(); ++var8) {
               GameServerWorkshopItems.WorkshopItem var4 = (GameServerWorkshopItems.WorkshopItem)var1.get(var8);
               var4.update();
               if (var4.state == GameServerWorkshopItems.WorkshopInstallState.Fail) {
                  return false;
               }

               if (var4.state != GameServerWorkshopItems.WorkshopInstallState.Ready) {
                  var7 = true;
                  break;
               }
            }

            if (!var7) {
               GameServer.WorkshopInstallFolders = new String[var0.size()];
               GameServer.WorkshopTimeStamps = new long[var0.size()];

               for(int var9 = 0; var9 < var0.size(); ++var9) {
                  var3 = (Long)var0.get(var9);
                  String var10 = SteamWorkshop.instance.GetItemInstallFolder(var3);
                  if (var10 == null) {
                     noise("GetItemInstallFolder() failed ID=" + var3);
                     return false;
                  }

                  noise(var3 + " installed to " + var10);
                  GameServer.WorkshopInstallFolders[var9] = var10;
                  GameServer.WorkshopTimeStamps[var9] = SteamWorkshop.instance.GetItemInstallTimeStamp(var3);
               }

               return true;
            }

            try {
               Thread.sleep(33L);
            } catch (Exception var6) {
               var6.printStackTrace();
            }
         }
      }
   }

   private static class WorkshopItem implements ISteamWorkshopCallback {
      long ID;
      GameServerWorkshopItems.WorkshopInstallState state;
      boolean downloaded;
      long downloadStartTime;
      long downloadQueryTime;
      String error;

      WorkshopItem(long var1) {
         this.state = GameServerWorkshopItems.WorkshopInstallState.CheckItemState;
         this.ID = var1;
      }

      void update() {
         switch(this.state) {
         case CheckItemState:
            this.CheckItemState();
            break;
         case DownloadPending:
            this.DownloadPending();
         case Ready:
         }

      }

      void setState(GameServerWorkshopItems.WorkshopInstallState var1) {
         GameServerWorkshopItems.noise("item state " + this.state + " -> " + var1 + " ID=" + this.ID);
         this.state = var1;
      }

      void CheckItemState() {
         long var1 = SteamWorkshop.instance.GetItemState(this.ID);
         String var10000 = SteamWorkshopItem.ItemState.toString(var1);
         GameServerWorkshopItems.noise("GetItemState()=" + var10000 + " ID=" + this.ID);
         if (var1 != (long)SteamWorkshopItem.ItemState.None.getValue() && !SteamWorkshopItem.ItemState.NeedsUpdate.and(var1)) {
            if (SteamWorkshopItem.ItemState.Installed.and(var1)) {
               this.setState(GameServerWorkshopItems.WorkshopInstallState.Ready);
            } else {
               this.error = "UnknownItemState";
               this.setState(GameServerWorkshopItems.WorkshopInstallState.Fail);
            }
         } else if (SteamWorkshop.instance.DownloadItem(this.ID, true, this)) {
            this.setState(GameServerWorkshopItems.WorkshopInstallState.DownloadPending);
            this.downloadStartTime = System.currentTimeMillis();
         } else {
            this.error = "DownloadItemFalse";
            this.setState(GameServerWorkshopItems.WorkshopInstallState.Fail);
         }
      }

      void DownloadPending() {
         long var1 = System.currentTimeMillis();
         if (this.downloadQueryTime + 100L <= var1) {
            this.downloadQueryTime = var1;
            long var3 = SteamWorkshop.instance.GetItemState(this.ID);
            String var10000 = SteamWorkshopItem.ItemState.toString(var3);
            GameServerWorkshopItems.noise("DownloadPending GetItemState()=" + var10000 + " ID=" + this.ID);
            if (SteamWorkshopItem.ItemState.NeedsUpdate.and(var3)) {
               long[] var5 = new long[2];
               if (SteamWorkshop.instance.GetItemDownloadInfo(this.ID, var5)) {
                  GameServerWorkshopItems.noise("download " + var5[0] + "/" + var5[1] + " ID=" + this.ID);
               }

            }
         }
      }

      public void onItemCreated(long var1, boolean var3) {
      }

      public void onItemNotCreated(int var1) {
      }

      public void onItemUpdated(boolean var1) {
      }

      public void onItemNotUpdated(int var1) {
      }

      public void onItemSubscribed(long var1) {
         GameServerWorkshopItems.noise("onItemSubscribed itemID=" + var1);
      }

      public void onItemNotSubscribed(long var1, int var3) {
         GameServerWorkshopItems.noise("onItemNotSubscribed itemID=" + var1 + " result=" + var3);
      }

      public void onItemDownloaded(long var1) {
         GameServerWorkshopItems.noise("onItemDownloaded itemID=" + var1 + " time=" + (System.currentTimeMillis() - this.downloadStartTime) + " ms");
         if (var1 == this.ID) {
            SteamWorkshop.instance.RemoveCallback(this);
            this.setState(GameServerWorkshopItems.WorkshopInstallState.CheckItemState);
         }
      }

      public void onItemNotDownloaded(long var1, int var3) {
         GameServerWorkshopItems.noise("onItemNotDownloaded itemID=" + var1 + " result=" + var3);
         if (var1 == this.ID) {
            SteamWorkshop.instance.RemoveCallback(this);
            this.error = "ItemNotDownloaded";
            this.setState(GameServerWorkshopItems.WorkshopInstallState.Fail);
         }
      }

      public void onItemQueryCompleted(long var1, int var3) {
         GameServerWorkshopItems.noise("onItemQueryCompleted handle=" + var1 + " numResult=" + var3);
      }

      public void onItemQueryNotCompleted(long var1, int var3) {
         GameServerWorkshopItems.noise("onItemQueryNotCompleted handle=" + var1 + " result=" + var3);
      }
   }

   private static enum WorkshopInstallState {
      CheckItemState,
      DownloadPending,
      Ready,
      Fail;

      // $FF: synthetic method
      private static GameServerWorkshopItems.WorkshopInstallState[] $values() {
         return new GameServerWorkshopItems.WorkshopInstallState[]{CheckItemState, DownloadPending, Ready, Fail};
      }
   }
}
