package zombie;

import zombie.characters.IsoPlayer;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemPickerJava;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;

public class LoadGridsquarePerformanceWorkaround {
   public static void init(int var0, int var1) {
      if (!GameClient.bClient) {
         LoadGridsquarePerformanceWorkaround.ItemPicker.instance.init();
      }
   }

   public static void LoadGridsquare(IsoGridSquare var0) {
      if (!GameClient.bClient) {
         LoadGridsquarePerformanceWorkaround.ItemPicker.instance.begin(var0);

         for(int var1 = 0; var1 < var0.getObjects().size(); ++var1) {
            IsoObject var2 = (IsoObject)var0.getObjects().get(var1);
            if (!(var2 instanceof IsoWorldInventoryObject) && LoadGridsquarePerformanceWorkaround.ItemPicker.instance.square != null && var2.sprite != null && var2.sprite.name != null && ItemPickerJava.overlayMap.containsKey(var2.sprite.name)) {
               LoadGridsquarePerformanceWorkaround.ItemPicker.instance.checkObject(var2);
            }
         }

         LoadGridsquarePerformanceWorkaround.ItemPicker.instance.end(var0);
      }
   }

   private static class ItemPicker {
      public static LoadGridsquarePerformanceWorkaround.ItemPicker instance = new LoadGridsquarePerformanceWorkaround.ItemPicker();
      private IsoGridSquare square;

      public void init() {
      }

      public void begin(IsoGridSquare var1) {
         if (var1.isOverlayDone()) {
            this.square = null;
         } else {
            this.square = var1;
         }

      }

      public void checkObject(IsoObject var1) {
         if (this.square != null) {
            IsoSprite var2 = var1.getSprite();
            if (var2 != null && var2.getName() != null) {
               ItemContainer var3 = var1.getContainer();
               if (var3 != null && !var3.isExplored()) {
                  ItemPickerJava.fillContainer(var3, IsoPlayer.getInstance());
                  if (var3 != null) {
                     var3.setExplored(true);
                  }

                  if (GameServer.bServer) {
                     GameServer.sendItemsInContainer(var1, var3);
                  }
               }

               if (var3 == null || !var3.getItems().isEmpty()) {
                  ItemPickerJava.updateOverlaySprite(var1);
               }
            }
         }
      }

      public void end(IsoGridSquare var1) {
         var1.setOverlayDone(true);
      }
   }
}
