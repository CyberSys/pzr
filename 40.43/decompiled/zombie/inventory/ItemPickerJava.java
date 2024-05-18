package zombie.inventory;

import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.core.stash.StashSystem;
import zombie.debug.DebugLog;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Key;
import zombie.inventory.types.WeaponPart;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaChunk;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoStove;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.list.PZArrayList;

public class ItemPickerJava {
   private static IsoPlayer player;
   private static float OtherLootModifier;
   private static float FoodLootModifier;
   private static float WeaponLootModifier;
   public static float zombieDensityCap = 8.0F;
   public static ArrayList NoContainerFillRooms = new ArrayList();
   public static ArrayList WeaponUpgrades = new ArrayList();
   public static HashMap WeaponUpgradeMap = new HashMap();
   public static THashMap rooms = new THashMap();
   public static THashMap containers = new THashMap();
   public static THashMap overlayMap = new THashMap();

   public static void Parse() {
      rooms.clear();
      NoContainerFillRooms.clear();
      WeaponUpgradeMap.clear();
      WeaponUpgrades.clear();
      containers.clear();
      InitSandboxLootSettings();
      ParseOverlayMap();
      KahluaTableImpl var0 = (KahluaTableImpl)LuaManager.env.rawget("NoContainerFillRooms");
      Iterator var1 = var0.delegate.entrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         String var3 = var2.getKey().toString();
         NoContainerFillRooms.add(var3);
      }

      KahluaTableImpl var13 = (KahluaTableImpl)LuaManager.env.rawget("WeaponUpgrades");
      Iterator var14 = var13.delegate.entrySet().iterator();

      KahluaTableImpl var6;
      while(var14.hasNext()) {
         Entry var16 = (Entry)var14.next();
         String var4 = var16.getKey().toString();
         ItemPickerJava.ItemPickerUpgradeWeapons var5 = new ItemPickerJava.ItemPickerUpgradeWeapons();
         var5.name = var4;
         WeaponUpgrades.add(var5);
         WeaponUpgradeMap.put(var4, var5);
         var6 = (KahluaTableImpl)var16.getValue();
         Iterator var7 = var6.delegate.entrySet().iterator();

         while(var7.hasNext()) {
            Entry var8 = (Entry)var7.next();
            String var9 = var8.getValue().toString();
            var5.Upgrades.add(var9);
         }
      }

      KahluaTableImpl var15 = (KahluaTableImpl)LuaManager.env.rawget("SuburbsDistributions");
      Iterator var17 = var15.delegate.entrySet().iterator();

      while(true) {
         label46:
         while(var17.hasNext()) {
            Entry var18 = (Entry)var17.next();
            String var19 = var18.getKey().toString();
            var6 = (KahluaTableImpl)var18.getValue();
            if (var6.delegate.containsKey("rolls")) {
               ItemPickerJava.ItemPickerContainer var21 = ExtractContainersFromLua(var6);
               containers.put(var19, var21);
            } else {
               ItemPickerJava.ItemPickerRoom var20 = new ItemPickerJava.ItemPickerRoom();
               rooms.put(var19, var20);
               Iterator var22 = var6.delegate.entrySet().iterator();

               while(true) {
                  while(true) {
                     if (!var22.hasNext()) {
                        continue label46;
                     }

                     Entry var23 = (Entry)var22.next();
                     String var10 = var23.getKey().toString();
                     if (var23.getValue() instanceof Double) {
                        var20.fillRand = ((Double)var23.getValue()).intValue();
                     } else {
                        KahluaTableImpl var11 = (KahluaTableImpl)var23.getValue();
                        if (!var10.isEmpty() && var11.delegate.containsKey("rolls") && var11.delegate.containsKey("items")) {
                           ItemPickerJava.ItemPickerContainer var12 = ExtractContainersFromLua(var11);
                           var20.Containers.put(var10, var12);
                        } else {
                           DebugLog.log("ERROR: SuburbsDistributions[\"" + var19 + "\"] is broken");
                        }
                     }
                  }
               }
            }
         }

         return;
      }
   }

   private static void ParseOverlayMap() {
      overlayMap.clear();
      KahluaTableImpl var0 = (KahluaTableImpl)LuaManager.env.rawget("overlayMap");
      Iterator var1 = var0.delegate.entrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         String var3 = var2.getKey().toString();
         ItemPickerJava.Overlay var4 = new ItemPickerJava.Overlay();
         var4.name = var3;
         overlayMap.put(var4.name, var4);
         KahluaTableImpl var5 = (KahluaTableImpl)var2.getValue();
         Iterator var6 = var5.delegate.entrySet().iterator();

         while(var6.hasNext()) {
            Entry var7 = (Entry)var6.next();
            String var8 = var7.getKey().toString();
            KahluaTableImpl var9 = (KahluaTableImpl)var7.getValue();
            String var10 = null;
            if (var9.delegate.containsKey(1.0D)) {
               var10 = var9.rawget(1.0D).toString();
            }

            String var11 = null;
            if (var9.delegate.containsKey(2.0D)) {
               var11 = var9.rawget(2.0D).toString();
            }

            ItemPickerJava.OverlayEntry var12 = new ItemPickerJava.OverlayEntry();
            var12.a = var10;
            var12.b = var11;
            var12.room = var8;
            var4.entries.put(var12.room, var12);
         }
      }

   }

   private static ItemPickerJava.ItemPickerContainer ExtractContainersFromLua(KahluaTableImpl var0) {
      ItemPickerJava.ItemPickerContainer var1 = new ItemPickerJava.ItemPickerContainer();
      ArrayList var2 = new ArrayList();
      if (var0.delegate.containsKey("noAutoAge")) {
         var1.noAutoAge = var0.rawgetBool("noAutoAge");
      }

      if (var0.delegate.containsKey("fillRand")) {
         var1.fillRand = var0.rawgetInt("fillRand");
      }

      double var3 = (Double)var0.delegate.get("rolls");
      var1.rolls = (float)((int)var3);
      KahluaTableImpl var5 = (KahluaTableImpl)var0.delegate.get("items");
      boolean var6 = false;
      double var7 = 0.0D;
      String var9 = "";
      double var10 = 1.0D;
      if (var5.delegate.containsKey(var10)) {
         do {
            Object var12 = var5.delegate.get(var10);
            if (var6) {
               var7 = (Double)var12;
            } else {
               var9 = var12.toString();
            }

            if (var6) {
               ItemPickerJava.ItemPickerItem var13 = new ItemPickerJava.ItemPickerItem();
               var13.itemName = var9;
               var13.chance = (float)var7;
               var2.add(var13);
            }

            var6 = !var6;
            ++var10;
         } while(var5.delegate.containsKey(var10));
      }

      var1.Items = (ItemPickerJava.ItemPickerItem[])var2.toArray(var1.Items);
      return var1;
   }

   private static void InitSandboxLootSettings() {
      switch(SandboxOptions.getInstance().getOtherLootModifier()) {
      case 1:
         OtherLootModifier = 0.2F;
         break;
      case 2:
         OtherLootModifier = 0.6F;
         break;
      case 3:
         OtherLootModifier = 1.0F;
         break;
      case 4:
         OtherLootModifier = 2.0F;
         break;
      case 5:
         OtherLootModifier = 3.0F;
      }

      switch(SandboxOptions.getInstance().getFoodLootModifier()) {
      case 1:
         FoodLootModifier = 0.2F;
         break;
      case 2:
         FoodLootModifier = 0.6F;
         break;
      case 3:
         FoodLootModifier = 1.0F;
         break;
      case 4:
         FoodLootModifier = 2.0F;
         break;
      case 5:
         FoodLootModifier = 3.0F;
      }

      switch(SandboxOptions.getInstance().getWeaponLootModifier()) {
      case 1:
         WeaponLootModifier = 0.2F;
         break;
      case 2:
         WeaponLootModifier = 0.6F;
         break;
      case 3:
         WeaponLootModifier = 1.0F;
         break;
      case 4:
         WeaponLootModifier = 2.0F;
         break;
      case 5:
         WeaponLootModifier = 3.0F;
      }

   }

   public static void fillContainer(ItemContainer var0, IsoPlayer var1) {
      if (!GameClient.bClient) {
         if (var0 != null) {
            IsoGridSquare var2 = var0.getSourceGrid();
            IsoRoom var3 = var2.getRoom();
            if (var0.getType().equals("inventorymale") || var0.getType().equals("inventoryfemale")) {
               ItemPickerJava.ItemPickerContainer var4 = (ItemPickerJava.ItemPickerContainer)((ItemPickerJava.ItemPickerRoom)rooms.get("all")).Containers.get(var0.getType());
               rollItem(var4, var0, true, var1);
            }

            ItemPickerJava.ItemPickerRoom var8 = null;
            if (rooms.containsKey("all")) {
               var8 = (ItemPickerJava.ItemPickerRoom)rooms.get("all");
            }

            String var5;
            if (var3 != null && rooms.containsKey(var3.getName())) {
               var5 = var3.getName();
               ItemPickerJava.ItemPickerRoom var6 = (ItemPickerJava.ItemPickerRoom)rooms.get(var5);
               ItemPickerJava.ItemPickerContainer var7 = null;
               if (var6.Containers.containsKey(var0.getType())) {
                  var7 = (ItemPickerJava.ItemPickerContainer)var6.Containers.get(var0.getType());
               }

               if (var7 == null && var6.Containers.containsKey("other")) {
                  var7 = (ItemPickerJava.ItemPickerContainer)var6.Containers.get("other");
               }

               if (var7 == null && var6.Containers.containsKey("all")) {
                  var7 = (ItemPickerJava.ItemPickerContainer)var6.Containers.get("all");
                  var5 = "all";
               }

               if (var7 == null) {
                  fillContainerType(var8, var0, var5, var1);
                  LuaEventManager.triggerEvent("OnFillContainer", var5, var0.getType(), var0);
               } else if (var3 != null) {
                  if (rooms.containsKey(var3.getName())) {
                     var8 = (ItemPickerJava.ItemPickerRoom)rooms.get(var3.getName());
                  }

                  if (var8 != null) {
                     fillContainerType(var8, var0, var3.getName(), var1);
                     LuaEventManager.triggerEvent("OnFillContainer", var3.getName(), var0.getType(), var0);
                  }

               }
            } else {
               var5 = null;
               if (var3 != null) {
                  var5 = var3.getName();
               } else {
                  var5 = "all";
               }

               fillContainerType(var8, var0, var5, var1);
               LuaEventManager.triggerEvent("OnFillContainer", var5, var0.getType(), var0);
            }
         }
      }
   }

   public static void fillContainerType(ItemPickerJava.ItemPickerRoom var0, ItemContainer var1, String var2, IsoGameCharacter var3) {
      boolean var4 = true;
      if (NoContainerFillRooms.contains(var2)) {
         var4 = false;
      }

      ItemPickerJava.ItemPickerContainer var5 = null;
      if (var0.Containers.containsKey("all")) {
         var5 = (ItemPickerJava.ItemPickerContainer)var0.Containers.get("all");
         rollItem(var5, var1, var4, var3);
      }

      var5 = (ItemPickerJava.ItemPickerContainer)var0.Containers.get(var1.getType());
      if (var5 == null) {
         var5 = (ItemPickerJava.ItemPickerContainer)var0.Containers.get("other");
      }

      if (var5 != null) {
         rollItem(var5, var1, var4, var3);
      }

   }

   public static InventoryItem tryAddItemToContainer(ItemContainer var0, String var1) {
      Item var2 = ScriptManager.instance.FindItem(var1);
      if (var2 == null) {
         return null;
      } else {
         float var3 = var2.getActualWeight() * (float)var2.getCount();
         if (!var0.hasRoomFor((IsoGameCharacter)null, var3)) {
            return null;
         } else {
            if (var0.getContainingItem() instanceof InventoryContainer) {
               ItemContainer var4 = var0.getContainingItem().getContainer();
               if (var4 != null && !var4.hasRoomFor((IsoGameCharacter)null, var3)) {
                  return null;
               }
            }

            return var0.AddItem(var1);
         }
      }
   }

   public static void rollItem(ItemPickerJava.ItemPickerContainer var0, ItemContainer var1, boolean var2, IsoGameCharacter var3) {
      if (!GameClient.bClient && !GameServer.bServer) {
         player = IsoPlayer.getInstance();
      }

      if (var0 != null && var1 != null) {
         float var4 = 0.0F;
         IsoMetaChunk var5 = null;
         if (player != null && IsoWorld.instance != null) {
            var5 = IsoWorld.instance.getMetaChunk((int)player.getX() / 10, (int)player.getY() / 10);
         }

         if (var5 != null) {
            var4 = var5.getLootZombieIntensity();
         }

         if (var4 > zombieDensityCap) {
            var4 = zombieDensityCap;
         }

         boolean var6 = false;
         boolean var7 = false;
         String var8 = "";
         if (player != null && var3 != null) {
            var6 = var3.HasTrait("Lucky");
            var7 = var3.HasTrait("Unlucky");
         }

         for(int var9 = 0; (float)var9 < var0.rolls; ++var9) {
            ItemPickerJava.ItemPickerItem[] var10 = var0.Items;

            for(int var11 = 0; var11 < var10.length; ++var11) {
               ItemPickerJava.ItemPickerItem var12 = var10[var11];
               float var13 = var12.chance;
               var8 = var12.itemName;
               if (var6) {
                  var13 *= 1.1F;
               }

               if (var7) {
                  var13 *= 0.9F;
               }

               float var14 = getLootModifier(var8);
               if ((float)Rand.Next(10000) <= var13 * 100.0F * var14 + var4 * 10.0F) {
                  InventoryItem var15 = tryAddItemToContainer(var1, var8);
                  if (var15 == null) {
                     return;
                  }

                  StashSystem.checkStashItem(var15);
                  if (var1.getType().equals("freezer") && var15 instanceof Food && ((Food)var15).isFreezing()) {
                     ((Food)((Food)var15)).freeze();
                  }

                  if (var15 instanceof Key) {
                     Key var16 = (Key)var15;
                     var16.takeKeyId();
                     var16.setName("Key " + var16.getKeyId());
                     if (var1.getSourceGrid() != null && var1.getSourceGrid().getBuilding() != null && var1.getSourceGrid().getBuilding().getDef() != null) {
                        int var17 = var1.getSourceGrid().getBuilding().getDef().getKeySpawned();
                        if (var17 < 2) {
                           var1.getSourceGrid().getBuilding().getDef().setKeySpawned(var17 + 1);
                        } else {
                           var1.Remove(var15);
                        }
                     }
                  }

                  if (WeaponUpgradeMap.containsKey(var15.getType())) {
                     DoWeaponUpgrade(var15);
                  }

                  if (!var0.noAutoAge) {
                     var15.setAutoAge();
                  }

                  if (Rand.Next(100) < 40 && var15 instanceof DrainableComboItem) {
                     float var18 = 1.0F / ((DrainableComboItem)var15).getUseDelta();
                     ((DrainableComboItem)var15).setUsedDelta(Rand.Next(1.0F, var18 - 1.0F) * ((DrainableComboItem)var15).getUseDelta());
                  }

                  if (var15 instanceof HandWeapon && Rand.Next(100) < 40) {
                     var15.setCondition(Rand.Next(1, var15.getConditionMax()));
                  }

                  if (var15 instanceof InventoryContainer && containers.containsKey(var15.getType())) {
                     ItemPickerJava.ItemPickerContainer var19 = (ItemPickerJava.ItemPickerContainer)containers.get(var15.getType());
                     if (var2 && Rand.Next(var19.fillRand) == 0) {
                        rollContainerItem((InventoryContainer)var15, var3, (ItemPickerJava.ItemPickerContainer)containers.get(var15.getType()));
                     }
                  }
               }
            }
         }
      }

   }

   public static void rollContainerItem(InventoryContainer var0, IsoGameCharacter var1, ItemPickerJava.ItemPickerContainer var2) {
      if (var2 != null) {
         ItemContainer var3 = var0.getInventory();
         float var4 = 0.0F;
         IsoMetaChunk var5 = null;
         if (player != null && IsoWorld.instance != null) {
            var5 = IsoWorld.instance.getMetaChunk((int)player.getX() / 10, (int)player.getY() / 10);
         }

         if (var5 != null) {
            var4 = var5.getLootZombieIntensity();
         }

         if (var4 > zombieDensityCap) {
            var4 = zombieDensityCap;
         }

         boolean var6 = false;
         boolean var7 = false;
         String var8 = "";
         if (player != null && var1 != null) {
            var6 = var1.HasTrait("Lucky");
            var7 = var1.HasTrait("Unlucky");
         }

         for(int var9 = 0; (float)var9 < var2.rolls; ++var9) {
            ItemPickerJava.ItemPickerItem[] var10 = var2.Items;

            for(int var11 = 0; var11 < var10.length; ++var11) {
               ItemPickerJava.ItemPickerItem var12 = var10[var11];
               float var13 = var12.chance;
               var8 = var12.itemName;
               if (var6) {
                  var13 *= 1.1F;
               }

               if (var7) {
                  var13 *= 0.9F;
               }

               float var14 = getLootModifier(var8);
               if ((float)Rand.Next(10000) <= var13 * 100.0F * var14 + var4 * 10.0F) {
                  InventoryItem var15 = tryAddItemToContainer(var3, var8);
                  if (var15 == null) {
                     return;
                  }

                  StashSystem.checkStashItem(var15);
                  if (var3.getType().equals("freezer") && var15 instanceof Food && ((Food)var15).isFreezing()) {
                     ((Food)((Food)var15)).freeze();
                  }

                  if (var15 instanceof Key) {
                     Key var16 = (Key)var15;
                     var16.takeKeyId();
                     var16.setName("Key " + var16.getKeyId());
                     if (var3.getSourceGrid() != null && var3.getSourceGrid().getBuilding() != null && var3.getSourceGrid().getBuilding().getDef() != null) {
                        int var17 = var3.getSourceGrid().getBuilding().getDef().getKeySpawned();
                        if (var17 < 2) {
                           var3.getSourceGrid().getBuilding().getDef().setKeySpawned(var17 + 1);
                        } else {
                           var3.Remove(var15);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private static void DoWeaponUpgrade(InventoryItem var0) {
      ItemPickerJava.ItemPickerUpgradeWeapons var1 = (ItemPickerJava.ItemPickerUpgradeWeapons)WeaponUpgradeMap.get(var0.getName());
      if (var1 != null) {
         if (var1.Upgrades.size() != 0) {
            int var2 = Rand.Next(var1.Upgrades.size());

            for(int var3 = 0; var3 < var2; ++var3) {
               String var4 = (String)var1.Upgrades.get(Rand.Next(var1.Upgrades.size()));
               InventoryItem var5 = InventoryItemFactory.CreateItem(var4);
               ((HandWeapon)var0).attachWeaponPart((WeaponPart)var5);
            }

         }
      }
   }

   public static float getLootModifier(String var0) {
      Item var1 = ScriptManager.instance.FindItem(var0);
      if (var1 == null) {
         return 0.6F;
      } else {
         float var2 = OtherLootModifier;
         if (var1.getType() == Item.Type.Food) {
            var2 = FoodLootModifier;
         }

         if (var1.getType() == Item.Type.Weapon || var1.getType() == Item.Type.WeaponPart || "Ammo".equals(var1.getDisplayCategory())) {
            var2 = WeaponLootModifier;
         }

         return var2;
      }
   }

   public static void updateOverlaySprite(IsoObject var0) {
      if (var0 != null) {
         if (!(var0 instanceof IsoStove)) {
            IsoGridSquare var1 = var0.getSquare();
            if (var1 != null) {
               String var2 = "other";
               if (var1.getRoom() != null) {
                  var2 = var1.getRoom().getName();
               }

               String var3 = null;
               ItemContainer var4 = var0.getContainer();
               if ((var0.sprite != null && var0.sprite.name != null && var4 != null && var4.getItems() != null && !var4.getItems().isEmpty() || var4 == null) && overlayMap.containsKey(var0.sprite.name)) {
                  ItemPickerJava.Overlay var5 = (ItemPickerJava.Overlay)overlayMap.get(var0.sprite.name);
                  ItemPickerJava.OverlayEntry var6 = null;
                  if (var5.entries.containsKey(var2)) {
                     var6 = (ItemPickerJava.OverlayEntry)var5.entries.get(var2);
                  }

                  if (var6 == null && var5.entries.containsKey("other")) {
                     var6 = (ItemPickerJava.OverlayEntry)var5.entries.get("other");
                  }

                  if (var6 != null) {
                     String var7 = var6.a;
                     if (var7 != "none") {
                        if (var4 == null && Rand.Next(2) == 0) {
                           return;
                        }

                        var3 = var6.a;
                        if (var6.b != null && var4 != null && var4.getItems() != null && var4.getItems().size() < 7) {
                           var3 = var6.b;
                        }
                     }
                  }
               }

               var0.setOverlaySprite(var3);
            }
         }
      }
   }

   public static void doOverlaySprite(IsoGridSquare var0) {
      if (!GameClient.bClient) {
         if (var0 != null && var0.getRoom() != null && !var0.isOverlayDone()) {
            PZArrayList var1 = var0.getObjects();

            for(int var2 = 0; var2 < var1.size(); ++var2) {
               IsoObject var3 = (IsoObject)var1.get(var2);
               if (var3 != null && var3.getContainer() != null && !var3.getContainer().isExplored()) {
                  fillContainer(var3.getContainer(), IsoPlayer.getInstance());
                  var3.getContainer().setExplored(true);
                  if (GameServer.bServer) {
                     LuaManager.GlobalObject.sendItemsInContainer(var3, var3.getContainer());
                  }
               }

               updateOverlaySprite(var3);
            }

            var0.setOverlayDone(true);
         }
      }
   }

   public static class Overlay {
      public String name;
      public THashMap entries = new THashMap();
   }

   public static class OverlayEntry {
      public String room;
      public String a;
      public String b;
   }

   public static class ItemPickerUpgradeWeapons {
      public String name;
      public ArrayList Upgrades = new ArrayList();
   }

   public static class ItemPickerRoom {
      public THashMap Containers = new THashMap();
      public int fillRand;
   }

   public static class ItemPickerContainer {
      public ItemPickerJava.ItemPickerItem[] Items = new ItemPickerJava.ItemPickerItem[0];
      public float rolls;
      public boolean noAutoAge;
      public int fillRand;
   }

   public static class ItemPickerItem {
      public String itemName;
      public float chance;
   }
}
