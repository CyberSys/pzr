package zombie.inventory;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.GameApplet;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.debug.DebugLog;
import zombie.inventory.types.InventoryContainer;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;

public class CompressIdenticalItems {
   private static final int BLOCK_SIZE = 1024;
   private static ThreadLocal perThreadVars = new ThreadLocal() {
      protected CompressIdenticalItems.PerThreadData initialValue() {
         return new CompressIdenticalItems.PerThreadData();
      }
   };

   private static int bufferSize(int var0) {
      return (var0 + 1024 - 1) / 1024 * 1024;
   }

   private static ByteBuffer ensureCapacity(ByteBuffer var0, int var1) {
      if (var0 == null || var0.capacity() < var1) {
         var0 = ByteBuffer.allocate(bufferSize(var1));
      }

      return var0;
   }

   private static ByteBuffer ensureCapacity(ByteBuffer var0) {
      if (var0 == null) {
         return ByteBuffer.allocate(1024);
      } else if (var0.capacity() - var0.position() < 1024) {
         ByteBuffer var1 = ensureCapacity((ByteBuffer)null, var0.position() + 1024);
         return var1.put(var0.array(), 0, var0.position());
      } else {
         return var0;
      }
   }

   private static boolean areItemsIdentical(CompressIdenticalItems.PerThreadData var0, InventoryItem var1, InventoryItem var2) throws IOException {
      if (var1 instanceof InventoryContainer) {
         ItemContainer var3 = ((InventoryContainer)var1).getInventory();
         ItemContainer var4 = ((InventoryContainer)var2).getInventory();
         if (!var3.getItems().isEmpty() || !var4.getItems().isEmpty()) {
            return false;
         }
      }

      ByteBuffer var19 = var0.itemCompareBuffer;
      var19.clear();
      long var20 = var1.id;
      long var6 = var2.id;
      var1.id = 0L;
      var2.id = 0L;

      while(true) {
         try {
            byte var8 = 0;
            var1.save(var19, false);
            int var9 = var19.position();
            int var10 = var19.position();
            var2.save(var19, false);
            int var11 = var19.position();
            if (var11 - var10 != var9 - var8) {
               boolean var21 = false;
               return var21;
            }

            for(int var12 = 0; var12 < var9 - var8; ++var12) {
               if (var19.get(var8 + var12) != var19.get(var10 + var12)) {
                  boolean var13 = false;
                  return var13;
               }
            }

            return true;
         } catch (BufferOverflowException var17) {
            var19 = ensureCapacity(var19);
            var19.clear();
            var0.itemCompareBuffer = var19;
         } finally {
            var1.id = var20;
            var2.id = var6;
         }
      }
   }

   public static ArrayList save(ByteBuffer var0, ArrayList var1, IsoGameCharacter var2) throws IOException {
      CompressIdenticalItems.PerThreadData var4 = (CompressIdenticalItems.PerThreadData)perThreadVars.get();
      CompressIdenticalItems.PerCallData var5 = var4.allocSaveVars();
      HashMap var6 = var5.typeToItems;
      ArrayList var7 = var5.types;

      try {
         int var8;
         for(var8 = 0; var8 < var1.size(); ++var8) {
            String var9 = ((InventoryItem)var1.get(var8)).getFullType();
            if (!var6.containsKey(var9)) {
               var6.put(var9, var5.allocItemList());
               var7.add(var9);
            }

            ((ArrayList)var6.get(var9)).add(var1.get(var8));
         }

         var8 = var0.position();
         var0.putShort((short)0);
         int var20 = 0;

         int var10;
         for(var10 = 0; var10 < var7.size(); ++var10) {
            ArrayList var11 = (ArrayList)var6.get(var7.get(var10));

            for(int var12 = 0; var12 < var11.size(); ++var12) {
               InventoryItem var13 = (InventoryItem)var11.get(var12);
               var5.savedItems.add(var13);
               int var14 = 1;
               int var15 = var12 + 1;
               if (var2 == null || !var2.isEquipped(var13)) {
                  while(var12 + 1 < var11.size() && areItemsIdentical(var4, var13, (InventoryItem)var11.get(var12 + 1))) {
                     var5.savedItems.add(var11.get(var12 + 1));
                     ++var12;
                     ++var14;
                  }
               }

               var0.putShort((short)var14);
               var13.saveWithSize(var0, false);
               if (var14 > 1) {
                  for(int var16 = var15; var16 <= var12; ++var16) {
                     var0.putLong(((InventoryItem)var11.get(var16)).id);
                  }
               }

               ++var20;
            }
         }

         var10 = var0.position();
         var0.position(var8);
         var0.putShort((short)var20);
         var0.position(var10);
      } finally {
         var5.next = var4.saveVars;
         var4.saveVars = var5;
      }

      return var5.savedItems;
   }

   public static ArrayList load(ByteBuffer var0, int var1, ArrayList var2, ArrayList var3) throws IOException {
      CompressIdenticalItems.PerThreadData var5 = (CompressIdenticalItems.PerThreadData)perThreadVars.get();
      CompressIdenticalItems.PerCallData var6 = var5.allocSaveVars();
      if (var2 != null) {
         var2.clear();
      }

      if (var3 != null) {
         var3.clear();
      }

      try {
         short var7 = var0.getShort();

         for(int var8 = 0; var8 < var7; ++var8) {
            short var9 = var1 >= 128 ? var0.getShort() : 1;
            int var10 = -1;
            if (var1 >= 54) {
               var10 = var1 >= 72 ? var0.getInt() : var0.getShort();
            }

            int var11 = var0.position();
            String var12 = GameWindow.ReadString(var0);
            byte var13 = -1;
            if (var1 >= 70) {
               var13 = var0.get();
               if (var13 < 0) {
                  throw new IOException("invalid item save-type " + var13);
               }
            }

            if (GameWindow.DEBUG_SAVE) {
               DebugLog.log(var12);
            }

            if (var12.contains("..")) {
               var12 = var12.replace("..", ".");
            }

            InventoryItem var14 = InventoryItemFactory.CreateItem(var12);
            int var16;
            int var23;
            if (var14 == null && var10 != -1) {
               if (var12.length() > 40) {
                  var12 = "<unknown>";
               }

               DebugLog.log("Cannot load \"" + var12 + "\" item. Make sure all mods used in save are installed.");
               var23 = var9 > 1 ? (var9 - 1) * 8 : 0;
               var0.position(var11 + var10 + var23);

               for(var16 = 0; var16 < var9; ++var16) {
                  if (var3 != null) {
                     var3.add((Object)null);
                  }

                  var6.savedItems.add((Object)null);
               }
            } else {
               if (var14 == null) {
                  if (var12.length() > 40) {
                     var12 = "<unknown>";
                  }

                  Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, "Cannot load \"" + var12 + "\" item. Make sure all mods used in save are installed.", (Object)null);
                  throw new RuntimeException("Cannot load " + var12 + " item");
               }

               if (var13 != -1 && var14.getSaveType() != var13) {
                  DebugLog.log("ignoring \"" + var12 + "\" because type changed from " + var13 + " to " + var14.getSaveType());
                  var23 = var9 > 1 ? (var9 - 1) * 8 : 0;
                  var0.position(var11 + var10 + var23);

                  for(var16 = 0; var16 < var9; ++var16) {
                     if (var3 != null) {
                        var3.add((Object)null);
                     }

                     var6.savedItems.add((Object)null);
                  }
               } else {
                  Item var15 = ScriptManager.instance.FindItem(var12);
                  var16 = var0.position();

                  int var17;
                  for(var17 = 0; var17 < var9; ++var17) {
                     if (var17 > 0) {
                        var14 = InventoryItemFactory.CreateItem(var12);
                        var0.position(var16);
                     }

                     var14.load(var0, var1, false);
                     if (var10 != -1 && var0.position() != var11 + var10) {
                        throw new IOException("item load() read " + (var0.position() - var11) + " but save() wrote " + var10 + " (" + var12 + ")");
                     }

                     if (var15 != null && var15.getObsolete()) {
                        if (var3 != null) {
                           var3.add((Object)null);
                        }

                        var6.savedItems.add((Object)null);
                     } else {
                        if (var2 != null) {
                           var2.add(var14);
                        }

                        if (var3 != null) {
                           var3.add(var14);
                        }

                        var6.savedItems.add(var14);
                     }
                  }

                  if (var1 >= 128) {
                     for(var17 = 1; var17 < var9; ++var17) {
                        long var18 = var0.getLong();
                        var14 = (InventoryItem)var6.savedItems.get(var6.savedItems.size() - var9 + var17);
                        if (var14 != null) {
                           var14.id = var18;
                        }
                     }
                  }
               }
            }
         }
      } finally {
         var6.next = var5.saveVars;
         var5.saveVars = var6;
      }

      return var6.savedItems;
   }

   public static void save(ByteBuffer var0, InventoryItem var1) throws IOException {
      var0.putShort((short)1);
      var0.putShort((short)1);
      var1.saveWithSize(var0, false);
   }

   private static class PerThreadData {
      CompressIdenticalItems.PerCallData saveVars;
      ByteBuffer itemCompareBuffer;

      private PerThreadData() {
         this.itemCompareBuffer = ByteBuffer.allocate(1024);
      }

      CompressIdenticalItems.PerCallData allocSaveVars() {
         if (this.saveVars == null) {
            return new CompressIdenticalItems.PerCallData();
         } else {
            CompressIdenticalItems.PerCallData var1 = this.saveVars;
            var1.reset();
            this.saveVars = this.saveVars.next;
            return var1;
         }
      }

      // $FF: synthetic method
      PerThreadData(Object var1) {
         this();
      }
   }

   private static class PerCallData {
      final ArrayList types;
      final HashMap typeToItems;
      final ArrayDeque itemLists;
      final ArrayList savedItems;
      CompressIdenticalItems.PerCallData next;

      private PerCallData() {
         this.types = new ArrayList();
         this.typeToItems = new HashMap();
         this.itemLists = new ArrayDeque();
         this.savedItems = new ArrayList();
      }

      void reset() {
         for(int var1 = 0; var1 < this.types.size(); ++var1) {
            ArrayList var2 = (ArrayList)this.typeToItems.get(this.types.get(var1));
            var2.clear();
            this.itemLists.push(var2);
         }

         this.types.clear();
         this.typeToItems.clear();
         this.savedItems.clear();
      }

      ArrayList allocItemList() {
         return this.itemLists.isEmpty() ? new ArrayList() : (ArrayList)this.itemLists.pop();
      }

      // $FF: synthetic method
      PerCallData(Object var1) {
         this();
      }
   }
}
