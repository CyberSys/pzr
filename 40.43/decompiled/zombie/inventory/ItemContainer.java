package zombie.inventory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SystemDisabler;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorDesc;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Key;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.SliceY;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.sprite.IsoSpriteGrid;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;

public class ItemContainer {
   public boolean active = false;
   public boolean dirty = true;
   public boolean IsDevice = false;
   public float ageFactor = 1.0F;
   public float CookingFactor = 1.0F;
   private float customTemperature = 0.0F;
   private boolean hasBeenLooted = false;
   private String openSound = null;
   private String closeSound = null;
   private String putSound = null;
   private String OnlyAcceptCategory = null;
   public int Capacity = 50;
   public InventoryItem containingItem = null;
   public ArrayList Items = new ArrayList();
   public ArrayList IncludingObsoleteItems = new ArrayList();
   public IsoObject parent = null;
   public IsoGridSquare SourceGrid = null;
   public VehiclePart vehiclePart = null;
   private int weightReduction = 0;
   public InventoryContainer inventoryContainer = null;
   public boolean bExplored = false;
   public String type = "none";
   public int ID = 0;
   boolean drawDirty = true;
   static ArrayList tempList = new ArrayList();

   public int getCapacity() {
      return this.Capacity;
   }

   public InventoryItem FindAndReturnWaterItem(int var1) {
      for(int var2 = 0; var2 < this.getItems().size(); ++var2) {
         InventoryItem var3 = (InventoryItem)this.getItems().get(var2);
         if (var3 instanceof DrainableComboItem && var3.isWaterSource()) {
            DrainableComboItem var4 = (DrainableComboItem)var3;
            if (var4.getDrainableUsesInt() >= var1) {
               return var3;
            }
         }
      }

      return null;
   }

   public int getEffectiveCapacity(IsoGameCharacter var1) {
      if (var1 != null && !(this.parent instanceof IsoGameCharacter) && !"floor".equals(this.getType())) {
         if (var1.HasTrait("Organized")) {
            return (int)((float)this.Capacity * 1.3F);
         }

         if (var1.HasTrait("Disorganized")) {
            return (int)Math.max((float)this.Capacity * 0.7F, 1.0F);
         }
      }

      return this.Capacity;
   }

   public void setExplored(boolean var1) {
      this.bExplored = var1;
   }

   public boolean hasRoomFor(IsoGameCharacter var1, InventoryItem var2) {
      if (this.vehiclePart != null && this.vehiclePart.getId().contains("Seat") && this.Items.isEmpty()) {
         return true;
      } else if (floatingPointCorrection(this.getCapacityWeight()) + var2.getUnequippedWeight() <= (float)this.getEffectiveCapacity(var1)) {
         if (this.getContainingItem() != null && this.getContainingItem().getEquipParent() != null && this.getContainingItem().getEquipParent().getInventory() != null && !this.getContainingItem().getEquipParent().getInventory().contains(var2)) {
            return floatingPointCorrection(this.getContainingItem().getEquipParent().getInventory().getCapacityWeight()) + var2.getUnequippedWeight() <= (float)this.getContainingItem().getEquipParent().getInventory().getEffectiveCapacity(var1);
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean hasRoomFor(IsoGameCharacter var1, float var2) {
      return floatingPointCorrection(this.getCapacityWeight()) + var2 <= (float)this.getEffectiveCapacity(var1);
   }

   public boolean isExplored() {
      return this.bExplored;
   }

   public boolean isInCharacterInventory(IsoGameCharacter var1) {
      if (var1.getInventory() == this) {
         return true;
      } else {
         if (this.containingItem != null) {
            if (var1.getInventory().contains(this.containingItem)) {
               return true;
            }

            if (this.containingItem.getContainer() != null) {
               return this.containingItem.getContainer().isInCharacterInventory(var1);
            }
         }

         return false;
      }
   }

   public boolean isInside(InventoryItem var1) {
      if (this.containingItem == null) {
         return false;
      } else if (this.containingItem == var1) {
         return true;
      } else {
         return this.containingItem.getContainer() != null && this.containingItem.getContainer().isInside(var1);
      }
   }

   public InventoryItem getContainingItem() {
      return this.containingItem;
   }

   public ItemContainer(int var1, String var2, IsoGridSquare var3, IsoObject var4, int var5, int var6) {
      this.ID = var1;
      this.parent = var4;
      this.type = var2;
      this.SourceGrid = var3;
      if (var2.equals("fridge")) {
         this.ageFactor = 0.02F;
         this.CookingFactor = 0.0F;
      }

   }

   public ItemContainer(String var1, IsoGridSquare var2, IsoObject var3, int var4, int var5) {
      this.ID = -1;
      this.parent = var3;
      this.type = var1;
      this.SourceGrid = var2;
      if (var1.equals("fridge")) {
         this.ageFactor = 0.02F;
         this.CookingFactor = 0.0F;
      }

   }

   public ItemContainer(int var1) {
      this.ID = var1;
   }

   public ItemContainer() {
      this.ID = -1;
   }

   public InventoryItem DoAddItem(InventoryItem var1) {
      return this.AddItem(var1);
   }

   public InventoryItem DoAddItemBlind(InventoryItem var1) {
      return this.AddItem(var1);
   }

   public ArrayList AddItems(String var1, int var2) {
      ArrayList var3 = new ArrayList();

      for(int var4 = 0; var4 < var2; ++var4) {
         InventoryItem var5 = this.AddItem(var1);
         if (var5 != null) {
            var3.add(var5);
         }
      }

      return var3;
   }

   public void AddItems(InventoryItem var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         this.AddItem(var1.getFullType());
      }

   }

   public int getNumberOfItem(String var1, boolean var2) {
      return this.getNumberOfItem(var1, var2, false);
   }

   public int getNumberOfItem(String var1) {
      return this.getNumberOfItem(var1, false);
   }

   public int getNumberOfItem(String var1, boolean var2, ArrayList var3) {
      int var4 = this.getNumberOfItem(var1, var2);
      if (var3 != null) {
         for(int var5 = 0; var5 < var3.size(); ++var5) {
            if (var3.get(var5) != this) {
               var4 += ((ItemContainer)var3.get(var5)).getNumberOfItem(var1, var2);
            }
         }
      }

      return var4;
   }

   public int getNumberOfItem(String var1, boolean var2, boolean var3) {
      int var4 = 0;

      for(int var5 = 0; var5 < this.Items.size(); ++var5) {
         InventoryItem var6 = (InventoryItem)this.Items.get(var5);
         if (!var6.getFullType().equals(var1) && !var6.getType().equals(var1)) {
            if (var3 && var6 instanceof InventoryContainer) {
               var4 += ((InventoryContainer)var6).getItemContainer().getNumberOfItem(var1);
            } else if (var2 && var6 instanceof DrainableComboItem && ((DrainableComboItem)var6).getReplaceOnDeplete() != null) {
               DrainableComboItem var7 = (DrainableComboItem)var6;
               if (var7.getReplaceOnDepleteFullType().equals(var1) || var7.getReplaceOnDeplete().equals(var1)) {
                  ++var4;
               }
            }
         } else {
            ++var4;
         }
      }

      return var4;
   }

   public InventoryItem addItem(InventoryItem var1) {
      return this.AddItem(var1);
   }

   public InventoryItem AddItem(InventoryItem var1) {
      if (var1 == null) {
         return null;
      } else if (this.containsID(var1.id)) {
         System.out.println("Error, container already has id");
         return this.getItemWithID(var1.id);
      } else {
         this.drawDirty = true;
         if (this.parent != null) {
            this.dirty = true;
         }

         if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
            this.parent.DirtySlice();
         }

         if (var1.container != null) {
            var1.container.Remove(var1);
         }

         var1.container = this;
         this.Items.add(var1);
         if (IsoWorld.instance.CurrentCell != null) {
            IsoWorld.instance.CurrentCell.addToProcessItems(var1);
         }

         return var1;
      }
   }

   public InventoryItem AddItemBlind(InventoryItem var1) {
      if (var1 == null) {
         return null;
      } else if (var1.getWeight() + this.getCapacityWeight() > (float)this.getCapacity()) {
         return null;
      } else {
         if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
            this.parent.DirtySlice();
         }

         this.Items.add(var1);
         return var1;
      }
   }

   public InventoryItem AddItem(String var1) {
      this.drawDirty = true;
      if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
         this.dirty = true;
      }

      Item var2 = ScriptManager.instance.FindItem(var1);
      if (var2 == null) {
         DebugLog.log("ERROR: ItemContainer.AddItem: can't find " + var1);
         return null;
      } else {
         InventoryItem var3 = null;
         int var4 = var2.getCount();

         for(int var5 = 0; var5 < var4; ++var5) {
            var3 = InventoryItemFactory.CreateItem(var1);
            if (var3 == null) {
               return null;
            }

            var3.container = this;
            this.Items.add(var3);
            if (var3 instanceof Food) {
               ((Food)var3).setHeat(this.getTemprature());
            }

            if (IsoWorld.instance.CurrentCell != null) {
               IsoWorld.instance.CurrentCell.addToProcessItems(var3);
            }
         }

         return var3;
      }
   }

   public boolean AddItem(String var1, float var2) {
      this.drawDirty = true;
      if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
         this.dirty = true;
      }

      InventoryItem var3 = InventoryItemFactory.CreateItem(var1);
      if (var3 == null) {
         return false;
      } else {
         if (var3 instanceof Drainable) {
            ((Drainable)var3).setUsedDelta(var2);
         }

         var3.container = this;
         this.Items.add(var3);
         return true;
      }
   }

   public boolean contains(InventoryItem var1) {
      return this.Items.contains(var1);
   }

   public boolean containsWithModule(String var1) {
      return this.containsWithModule(var1, false);
   }

   public boolean containsWithModule(String var1, boolean var2) {
      String var3 = var1;
      String var4 = "Base";
      if (var1.contains(".")) {
         var4 = var1.split("\\.")[0];
         var3 = var1.split("\\.")[1];
      }

      for(int var5 = 0; var5 < this.Items.size(); ++var5) {
         InventoryItem var6 = (InventoryItem)this.Items.get(var5);
         if (var6 == null) {
            this.Items.remove(var5);
            --var5;
         } else if (var6.type.equals(var3.trim()) && var4.equals(var6.getModule()) && (!var2 || !(var6 instanceof DrainableComboItem) || !(((DrainableComboItem)var6).getUsedDelta() <= 0.0F))) {
            return true;
         }
      }

      return false;
   }

   public void removeItemOnServer(InventoryItem var1) {
      if (GameClient.bClient) {
         if (this.containingItem != null && this.containingItem.getWorldItem() != null) {
            GameClient.instance.addToItemRemoveSendBuffer(this.containingItem.getWorldItem(), this, var1);
         } else {
            GameClient.instance.addToItemRemoveSendBuffer(this.parent, this, var1);
         }

         if (this.SourceGrid != null) {
            this.SourceGrid.clientModify();
         }
      }

   }

   public void addItemOnServer(InventoryItem var1) {
      if (GameClient.bClient) {
         if (this.containingItem != null && this.containingItem.getWorldItem() != null) {
            GameClient.instance.addToItemSendBuffer(this.containingItem.getWorldItem(), this, var1);
         } else {
            GameClient.instance.addToItemSendBuffer(this.parent, this, var1);
         }

         if (this.SourceGrid != null) {
            this.SourceGrid.clientModify();
         }
      }

   }

   public boolean contains(InventoryItem var1, boolean var2) {
      ArrayList var3 = new ArrayList();

      for(int var4 = 0; var4 < this.Items.size(); ++var4) {
         InventoryItem var5 = (InventoryItem)this.Items.get(var4);
         if (var5 == null) {
            this.Items.remove(var4);
            --var4;
         } else {
            if (var5 == var1) {
               return true;
            }

            if (var2 && this.getItems().get(var4) instanceof InventoryContainer && ((InventoryContainer)this.getItems().get(var4)).getItemContainer() != null && !var3.contains((InventoryContainer)this.getItems().get(var4))) {
               var3.add((InventoryContainer)this.getItems().get(var4));
            }
         }
      }

      boolean var6 = false;

      for(int var7 = 0; var7 < var3.size(); ++var7) {
         var6 = ((InventoryContainer)var3.get(var7)).getItemContainer().contains(var1, var2);
         if (var6) {
            return true;
         }
      }

      return false;
   }

   public boolean contains(String var1, boolean var2) {
      return this.contains(var1, var2, false);
   }

   private boolean testBroken(boolean var1, InventoryItem var2) {
      if (!var1) {
         return true;
      } else {
         return !var2.isBroken();
      }
   }

   public boolean contains(String var1, boolean var2, boolean var3) {
      ArrayList var4 = new ArrayList();
      int var5;
      InventoryItem var6;
      int var10;
      if (var1.contains("Type:")) {
         for(var5 = 0; var5 < this.Items.size(); ++var5) {
            var6 = (InventoryItem)this.Items.get(var5);
            if (var1.contains("Food") && var6 instanceof Food) {
               return true;
            }

            if (var1.contains("Weapon") && var6 instanceof HandWeapon && this.testBroken(var3, var6)) {
               return true;
            }

            if (var1.contains("AlarmClock") && var6 instanceof AlarmClock) {
               return true;
            }
         }
      } else if (var1.contains("/")) {
         String[] var9 = var1.split("/");

         for(var10 = 0; var10 < var9.length; ++var10) {
            for(int var7 = 0; var7 < this.Items.size(); ++var7) {
               InventoryItem var8 = (InventoryItem)this.Items.get(var7);
               if (var8.type.equals(var9[var10].trim()) && this.testBroken(var3, var8)) {
                  return true;
               }
            }
         }
      } else {
         for(var5 = 0; var5 < this.Items.size(); ++var5) {
            var6 = (InventoryItem)this.Items.get(var5);
            if (var6 == null) {
               this.Items.remove(var5);
               --var5;
            } else {
               if (var6.type.equals(var1.trim()) && this.testBroken(var3, var6)) {
                  return true;
               }

               if (var2 && this.getItems().get(var5) instanceof InventoryContainer && ((InventoryContainer)this.getItems().get(var5)).getItemContainer() != null && !var4.contains((InventoryContainer)this.getItems().get(var5))) {
                  var4.add((InventoryContainer)this.getItems().get(var5));
               }
            }
         }
      }

      boolean var11 = false;

      for(var10 = 0; var10 < var4.size(); ++var10) {
         var11 = ((InventoryContainer)var4.get(var10)).getItemContainer().contains(var1, var2, var3);
         if (var11) {
            return true;
         }
      }

      return false;
   }

   public boolean contains(String var1) {
      return this.contains(var1, false);
   }

   public InventoryItem getBestCondition(String var1) {
      if (var1 == null) {
         return null;
      } else {
         if (var1.contains(".")) {
            var1 = var1.substring(var1.indexOf(".") + 1);
         }

         InventoryItem var2 = null;
         int var3 = 0;

         for(int var4 = 0; var4 < this.Items.size(); ++var4) {
            InventoryItem var5 = (InventoryItem)this.Items.get(var4);
            if (var5.type != null && var5.type.equals(var1) && var5.Condition > var3) {
               var3 = var5.Condition;
               var2 = var5;
            }
         }

         return var2;
      }
   }

   public InventoryItem FindAndReturnCategory(String var1) {
      for(int var2 = 0; var2 < this.Items.size(); ++var2) {
         InventoryItem var3 = (InventoryItem)this.Items.get(var2);
         if (var3.getCategory().equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public ArrayList FindAndReturn(String var1, int var2) {
      ArrayList var3 = new ArrayList();
      int var4 = 0;

      while(var4 < var2) {
         InventoryItem var5 = this.FindAndReturn(var1, var3);
         if (var5 == null) {
            return var3;
         }

         ++var4;
         var3.add(var5);
      }

      return var3;
   }

   public InventoryItem FindAndReturn(String var1, ArrayList var2) {
      if (var1 == null) {
         return null;
      } else {
         if (var1.contains(".")) {
            var1 = var1.substring(var1.indexOf(".") + 1);
         }

         if (var1.contains("/")) {
            String[] var3 = var1.split("/");

            for(int var4 = 0; var4 < var3.length; ++var4) {
               for(int var5 = 0; var5 < this.Items.size(); ++var5) {
                  InventoryItem var6 = (InventoryItem)this.Items.get(var5);
                  if (var6.type != null && var6.type.equals(var3[var4]) && !var2.contains(var6)) {
                     return var6;
                  }
               }
            }
         } else {
            for(int var7 = 0; var7 < this.Items.size(); ++var7) {
               InventoryItem var8 = (InventoryItem)this.Items.get(var7);
               if (var8.type != null && var8.type.equals(var1) && !var2.contains(var8)) {
                  return var8;
               }
            }
         }

         return null;
      }
   }

   public InventoryItem FindAndReturn(String var1) {
      if (var1 == null) {
         return null;
      } else {
         if (var1.contains(".")) {
            var1 = var1.substring(var1.indexOf(".") + 1);
         }

         if (var1.contains("/")) {
            String[] var2 = var1.split("/");

            for(int var3 = 0; var3 < var2.length; ++var3) {
               for(int var4 = 0; var4 < this.Items.size(); ++var4) {
                  InventoryItem var5 = (InventoryItem)this.Items.get(var4);
                  if (var5.type != null && var5.type.equals(var2[var3])) {
                     return var5;
                  }
               }
            }
         } else {
            for(int var6 = 0; var6 < this.Items.size(); ++var6) {
               InventoryItem var7 = (InventoryItem)this.Items.get(var6);
               if (var7.type != null && var7.type.equals(var1)) {
                  return var7;
               }
            }
         }

         return null;
      }
   }

   public ArrayList FindAll(String var1) {
      ArrayList var2 = new ArrayList();
      if (var1 == null) {
         return var2;
      } else {
         if (var1.contains(".")) {
            var1 = var1.substring(var1.indexOf(".") + 1);
         }

         if (var1.contains("/")) {
            String[] var3 = var1.split("/");

            for(int var4 = 0; var4 < var3.length; ++var4) {
               for(int var5 = 0; var5 < this.Items.size(); ++var5) {
                  InventoryItem var6 = (InventoryItem)this.Items.get(var5);
                  if (var6.type != null && var6.type.equals(var3[var4]) && !var2.contains(var6)) {
                     var2.add(var6);
                  }
               }
            }
         } else {
            for(int var7 = 0; var7 < this.Items.size(); ++var7) {
               InventoryItem var8 = (InventoryItem)this.Items.get(var7);
               if (var8.type != null && var8.type.equals(var1)) {
                  var2.add(var8);
               }
            }
         }

         return var2;
      }
   }

   public InventoryItem FindAndReturnStack(String var1) {
      if (var1.contains(".")) {
         var1 = var1.substring(var1.indexOf(".") + 1);
      }

      for(int var2 = 0; var2 < this.Items.size(); ++var2) {
         InventoryItem var3 = (InventoryItem)this.Items.get(var2);
         InventoryItem var4 = InventoryItemFactory.CreateItem(var3.module + "." + var1);
         if (var3.type == null) {
            if (var1 != null) {
               continue;
            }
         } else if (!var3.type.equals(var1)) {
            continue;
         }

         if (var3.CanStack(var4)) {
            return var3;
         }
      }

      return null;
   }

   public InventoryItem FindAndReturnStack(InventoryItem var1) {
      String var2 = var1.type;

      for(int var3 = 0; var3 < this.Items.size(); ++var3) {
         InventoryItem var4 = (InventoryItem)this.Items.get(var3);
         if (var4.type == null) {
            if (var2 != null) {
               continue;
            }
         } else if (!var4.type.equals(var2)) {
            continue;
         }

         if (var4.CanStack(var1)) {
            return var4;
         }
      }

      return null;
   }

   public boolean HasType(ItemType var1) {
      for(int var2 = 0; var2 < this.Items.size(); ++var2) {
         InventoryItem var3 = (InventoryItem)this.Items.get(var2);
         if (var3.cat == var1) {
            return true;
         }
      }

      return false;
   }

   public void Remove(InventoryItem var1) {
      this.drawDirty = true;
      if (this.parent != null) {
         this.dirty = true;
      }

      for(int var2 = 0; var2 < this.Items.size(); ++var2) {
         InventoryItem var3 = (InventoryItem)this.Items.get(var2);
         if (var3 == var1) {
            if (var1.uses > 1) {
               --var1.uses;
            } else {
               this.Items.remove(var1);
            }

            var1.container = null;
            this.dirty = true;
            if (this.parent instanceof IsoDeadBody) {
               ((IsoDeadBody)this.parent).checkClothing();
            }

            return;
         }
      }

   }

   public void DoRemoveItem(InventoryItem var1) {
      this.drawDirty = true;
      if (this.parent != null) {
         this.dirty = true;
      }

      this.Items.remove(var1);
      var1.container = null;
      if (this.parent instanceof IsoDeadBody) {
         ((IsoDeadBody)this.parent).checkClothing();
      }

   }

   public void Remove(String var1) {
      this.drawDirty = true;
      if (this.parent != null) {
         this.dirty = true;
      }

      for(int var2 = 0; var2 < this.Items.size(); ++var2) {
         InventoryItem var3 = (InventoryItem)this.Items.get(var2);
         if (var3.type.equals(var1)) {
            if (var3.uses > 1) {
               --var3.uses;
            } else {
               this.Items.remove(var3);
            }

            var3.container = null;
            this.dirty = true;
            return;
         }
      }

   }

   public InventoryItem Remove(ItemType var1) {
      this.drawDirty = true;
      if (this.parent != null) {
         this.dirty = true;
      }

      for(int var2 = 0; var2 < this.Items.size(); ++var2) {
         InventoryItem var3 = (InventoryItem)this.Items.get(var2);
         if (var3.cat == var1) {
            this.Items.remove(var3);
            this.dirty = true;
            var3.container = null;
            return var3;
         }
      }

      return null;
   }

   public InventoryItem Find(ItemType var1) {
      for(int var2 = 0; var2 < this.Items.size(); ++var2) {
         InventoryItem var3 = (InventoryItem)this.Items.get(var2);
         if (var3.cat == var1) {
            return var3;
         }
      }

      return null;
   }

   public void RemoveAll(String var1) {
      this.drawDirty = true;
      if (this.parent != null) {
         this.dirty = true;
      }

      ArrayList var2 = new ArrayList();

      int var3;
      for(var3 = 0; var3 < this.Items.size(); ++var3) {
         InventoryItem var4 = (InventoryItem)this.Items.get(var3);
         if (var4.type.equals(var1)) {
            var4.container = null;
            var2.add(var4);
            this.dirty = true;
         }
      }

      for(var3 = 0; var3 < var2.size(); ++var3) {
         this.Items.remove(var2.get(var3));
      }

   }

   public boolean RemoveOneOf(String var1, boolean var2) {
      this.drawDirty = true;
      if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
         this.dirty = true;
      }

      int var3;
      InventoryItem var4;
      for(var3 = 0; var3 < this.Items.size(); ++var3) {
         var4 = (InventoryItem)this.Items.get(var3);
         if (var4.getFullType().equals(var1) || var4.type.equals(var1)) {
            if (var4.uses > 1) {
               --var4.uses;
            } else {
               var4.container = null;
               this.Items.remove(var4);
            }

            this.dirty = true;
            return true;
         }
      }

      if (var2) {
         for(var3 = 0; var3 < this.Items.size(); ++var3) {
            var4 = (InventoryItem)this.Items.get(var3);
            if (var4 instanceof InventoryContainer && ((InventoryContainer)var4).getItemContainer() != null && ((InventoryContainer)var4).getItemContainer().RemoveOneOf(var1, var2)) {
               return true;
            }
         }
      }

      return false;
   }

   public void RemoveOneOf(String var1) {
      this.RemoveOneOf(var1, true);
   }

   /** @deprecated */
   public int getWeight() {
      if (this.parent instanceof IsoPlayer && ((IsoPlayer)this.parent).GhostMode) {
         return 0;
      } else {
         float var1 = 0.0F;

         for(int var2 = 0; var2 < this.Items.size(); ++var2) {
            InventoryItem var3 = (InventoryItem)this.Items.get(var2);
            var1 += var3.ActualWeight * (float)var3.uses;
         }

         return (int)(var1 * ((float)this.weightReduction / 0.01F));
      }
   }

   public float getContentsWeight() {
      float var1 = 0.0F;

      for(int var2 = 0; var2 < this.Items.size(); ++var2) {
         InventoryItem var3 = (InventoryItem)this.Items.get(var2);
         var1 += var3.getUnequippedWeight();
      }

      return var1;
   }

   public float getMaxWeight() {
      return this.parent instanceof IsoGameCharacter ? (float)((IsoGameCharacter)this.parent).getMaxWeight() : (float)this.Capacity;
   }

   public float getCapacityWeight() {
      if (!(this.parent instanceof IsoPlayer) || (!Core.bDebug || !((IsoPlayer)this.parent).GhostMode) && (((IsoPlayer)this.parent).getAccessLevel().equals("None") || !((IsoPlayer)this.parent).isUnlimitedCarry())) {
         return this.parent instanceof IsoGameCharacter ? ((IsoGameCharacter)this.parent).getInventoryWeight() : this.getContentsWeight();
      } else {
         return 0.0F;
      }
   }

   public boolean isEmpty() {
      return this.Items.isEmpty();
   }

   public boolean isPowered() {
      boolean var1 = false;
      if (GameTime.getInstance().getNightsSurvived() < SandboxOptions.instance.getElecShutModifier()) {
         var1 = true;
      }

      if (this.parent != null && this.parent.getSquare() != null) {
         IsoGridSquare var2 = this.parent.getSquare();
         if (var2.haveElectricity()) {
            var1 = true;
         } else if (var2.getRoom() == null && var1) {
            var1 = false;
            if (var2.nav[IsoDirections.N.index()] != null && var2.nav[IsoDirections.N.index()].getRoom() != null || var2.nav[IsoDirections.S.index()] != null && var2.nav[IsoDirections.S.index()].getRoom() != null || var2.nav[IsoDirections.W.index()] != null && var2.nav[IsoDirections.W.index()].getRoom() != null || var2.nav[IsoDirections.E.index()] != null && var2.nav[IsoDirections.E.index()].getRoom() != null) {
               var1 = true;
            }

            if (!var1 && this.parent.getSprite() != null && this.parent.getSprite().getSpriteGrid() != null) {
               IsoSpriteGrid var3 = this.parent.getSprite().getSpriteGrid();
               int var4 = var3.getSpriteGridPosX(this.parent.getSprite());
               int var5 = var3.getSpriteGridPosY(this.parent.getSprite());
               int var6 = (int)this.parent.getX();
               int var7 = (int)this.parent.getY();
               int var8 = (int)this.parent.getZ();
               IsoGridSquare var9 = null;

               for(int var10 = var7 - var5; var10 < var7 - var5 + var3.getHeight(); ++var10) {
                  for(int var11 = var6 - var4; var11 < var6 - var4 + var3.getWidth(); ++var11) {
                     var9 = IsoWorld.instance.getCell().getGridSquare(var11, var10, var8);
                     if (var9 != null) {
                        if (var9.haveElectricity()) {
                           var1 = true;
                           break;
                        }

                        if (var9.getRoom() == null && (var9.nav[IsoDirections.N.index()] != null && var9.nav[IsoDirections.N.index()].getRoom() != null || var9.nav[IsoDirections.S.index()] != null && var9.nav[IsoDirections.S.index()].getRoom() != null || var9.nav[IsoDirections.W.index()] != null && var9.nav[IsoDirections.W.index()].getRoom() != null || var9.nav[IsoDirections.E.index()] != null && var9.nav[IsoDirections.E.index()].getRoom() != null)) {
                           var1 = true;
                           break;
                        }
                     }
                  }
               }
            }
         }
      } else {
         var1 = false;
      }

      return var1;
   }

   public float getTemprature() {
      if (this.customTemperature != 0.0F) {
         return this.customTemperature;
      } else {
         if (this.isPowered()) {
            if (this.type.equals("fridge") || this.type.equals("freezer")) {
               return 0.2F;
            }

            if (("stove".equals(this.type) || "microwave".equals(this.type)) && this.parent instanceof IsoStove) {
               return ((IsoStove)this.parent).getCurrentTemperature() / 100.0F;
            }
         }

         if ("barbecue".equals(this.type) && this.parent instanceof IsoBarbecue) {
            return ((IsoBarbecue)this.parent).getTemperature();
         } else if ("fireplace".equals(this.type) && this.parent instanceof IsoFireplace) {
            return ((IsoFireplace)this.parent).getTemperature();
         } else if ("woodstove".equals(this.type) && this.parent instanceof IsoFireplace) {
            return ((IsoFireplace)this.parent).getTemperature();
         } else if ((this.type.equals("fridge") || this.type.equals("freezer")) && GameTime.instance.NightsSurvived == SandboxOptions.instance.getElecShutModifier() && GameTime.instance.getTimeOfDay() < 13.0F) {
            float var1 = (GameTime.instance.getTimeOfDay() - 7.0F) / 6.0F;
            return GameTime.instance.Lerp(0.2F, 1.0F, var1);
         } else {
            return 1.0F;
         }
      }
   }

   public boolean isTemperatureChanging() {
      return this.parent instanceof IsoStove ? ((IsoStove)this.parent).isTemperatureChanging() : false;
   }

   public ArrayList save(ByteBuffer var1, boolean var2, IsoGameCharacter var3) throws IOException {
      GameWindow.WriteString(var1, this.type);
      var1.put((byte)(this.bExplored ? 1 : 0));
      ArrayList var4 = CompressIdenticalItems.save(var1, this.Items, (IsoGameCharacter)null);
      var1.put((byte)(this.isHasBeenLooted() ? 1 : 0));
      var1.putInt(this.Capacity);
      return var4;
   }

   public ArrayList save(ByteBuffer var1, boolean var2) throws IOException {
      return this.save(var1, var2, (IsoGameCharacter)null);
   }

   public ArrayList load(ByteBuffer var1, int var2, boolean var3) throws IOException {
      var3 = false;
      this.type = GameWindow.ReadString(var1);
      this.bExplored = var1.get() == 1;
      ArrayList var4 = CompressIdenticalItems.load(var1, var2, this.Items, this.IncludingObsoleteItems);

      for(int var5 = 0; var5 < this.Items.size(); ++var5) {
         InventoryItem var6 = (InventoryItem)this.Items.get(var5);
         var6.container = this;
      }

      if (var2 >= 37) {
         this.setHasBeenLooted(var1.get() == 1);
      }

      if (var2 >= 84) {
         this.Capacity = var1.getInt();
      }

      this.dirty = false;
      return var4;
   }

   public boolean isDrawDirty() {
      return this.drawDirty;
   }

   public void setDrawDirty(boolean var1) {
      this.drawDirty = var1;
   }

   public InventoryItem getBestWeapon(SurvivorDesc var1) {
      InventoryItem var2 = null;
      float var3 = -1.0E7F;

      for(int var4 = 0; var4 < this.Items.size(); ++var4) {
         InventoryItem var5 = (InventoryItem)this.Items.get(var4);
         if (var5 instanceof HandWeapon) {
            float var6 = ((HandWeapon)var5).getScore(var1);
            if (var6 >= var3) {
               var3 = var6;
               var2 = var5;
            }
         }
      }

      return var2;
   }

   public InventoryItem getBestWeapon() {
      InventoryItem var1 = null;
      float var2 = 0.0F;

      for(int var3 = 0; var3 < this.Items.size(); ++var3) {
         InventoryItem var4 = (InventoryItem)this.Items.get(var3);
         if (var4 instanceof HandWeapon) {
            float var5 = ((HandWeapon)var4).getScore((SurvivorDesc)null);
            if (var5 >= var2) {
               var2 = var5;
               var1 = var4;
            }
         }
      }

      return var1;
   }

   public float getTotalFoodScore(SurvivorDesc var1) {
      float var2 = 0.0F;

      for(int var3 = 0; var3 < this.Items.size(); ++var3) {
         InventoryItem var4 = (InventoryItem)this.Items.get(var3);
         if (var4 instanceof Food) {
            var2 += var4.getScore(var1);
         }
      }

      return var2;
   }

   public float getTotalWeaponScore(SurvivorDesc var1) {
      float var2 = 0.0F;

      for(int var3 = 0; var3 < this.Items.size(); ++var3) {
         InventoryItem var4 = (InventoryItem)this.Items.get(var3);
         if (var4 instanceof HandWeapon) {
            var2 += var4.getScore(var1);
         }
      }

      return var2;
   }

   public InventoryItem getBestFood(SurvivorDesc var1) {
      InventoryItem var2 = null;
      float var3 = 0.0F;

      for(int var4 = 0; var4 < this.Items.size(); ++var4) {
         InventoryItem var5 = (InventoryItem)this.Items.get(var4);
         if (var5 instanceof Food) {
            float var6 = var5.getScore(var1);
            if (((Food)var5).isbDangerousUncooked() && !((Food)var5).isCooked()) {
               var6 *= 0.2F;
            }

            if (((Food)var5).Age > (float)var5.OffAge) {
               var6 *= 0.2F;
            }

            if (var6 >= var3) {
               var3 = var6;
               var2 = var5;
            }
         }
      }

      return var2;
   }

   public InventoryItem getBestBandage(SurvivorDesc var1) {
      Object var2 = null;

      for(int var3 = 0; var3 < this.Items.size(); ++var3) {
         InventoryItem var4 = (InventoryItem)this.Items.get(var3);
         if (var4.CanBandage) {
            return var4;
         }
      }

      return (InventoryItem)var2;
   }

   public int getNumItems(String var1) {
      int var2 = 0;
      int var3;
      if (var1.contains("Type:")) {
         for(var3 = 0; var3 < this.Items.size(); ++var3) {
            if (this.Items.get(var3) instanceof Food && var1.contains("Food")) {
               var2 += ((InventoryItem)this.Items.get(var3)).uses;
            }

            if (this.Items.get(var3) instanceof HandWeapon && var1.contains("Weapon")) {
               var2 += ((InventoryItem)this.Items.get(var3)).uses;
            }
         }
      } else {
         for(var3 = 0; var3 < this.Items.size(); ++var3) {
            if (((InventoryItem)this.Items.get(var3)).type.equals(var1)) {
               var2 += ((InventoryItem)this.Items.get(var3)).uses;
            }
         }
      }

      return var2;
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean var1) {
      this.active = var1;
   }

   public boolean isDirty() {
      return this.dirty;
   }

   public void setDirty(boolean var1) {
      this.dirty = var1;
   }

   public boolean isIsDevice() {
      return this.IsDevice;
   }

   public void setIsDevice(boolean var1) {
      this.IsDevice = var1;
   }

   public float getAgeFactor() {
      return this.ageFactor;
   }

   public void setAgeFactor(float var1) {
      this.ageFactor = var1;
   }

   public float getCookingFactor() {
      return this.CookingFactor;
   }

   public void setCookingFactor(float var1) {
      this.CookingFactor = var1;
   }

   public ArrayList getItems() {
      return this.Items;
   }

   public void setItems(ArrayList var1) {
      this.Items = var1;
   }

   public IsoObject getParent() {
      return this.parent;
   }

   public void setParent(IsoObject var1) {
      this.parent = var1;
   }

   public IsoGridSquare getSourceGrid() {
      return this.SourceGrid;
   }

   public void setSourceGrid(IsoGridSquare var1) {
      this.SourceGrid = var1;
   }

   public String getType() {
      return this.type;
   }

   public void setType(String var1) {
      this.type = var1;
   }

   public void clear() {
      this.Items.clear();
      this.dirty = true;
      this.drawDirty = true;
   }

   public int getWaterContainerCount() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.Items.size(); ++var2) {
         InventoryItem var3 = (InventoryItem)this.Items.get(var2);
         if (var3.CanStoreWater) {
            ++var1;
         }
      }

      return var1;
   }

   public InventoryItem FindWaterSource() {
      for(int var1 = 0; var1 < this.Items.size(); ++var1) {
         InventoryItem var2 = (InventoryItem)this.Items.get(var1);
         if (var2.isWaterSource()) {
            if (!(var2 instanceof Drainable)) {
               return var2;
            }

            if (((Drainable)var2).getUsedDelta() > 0.0F) {
               return var2;
            }
         }
      }

      return null;
   }

   public ArrayList getAllWaterFillables() {
      tempList.clear();

      for(int var1 = 0; var1 < this.Items.size(); ++var1) {
         InventoryItem var2 = (InventoryItem)this.Items.get(var1);
         if (var2.CanStoreWater) {
            tempList.add(var2);
         }
      }

      return tempList;
   }

   public int getItemCount(String var1) {
      return this.getItemCount(var1, false);
   }

   public int getItemCount(String var1, boolean var2) {
      int var3 = 0;

      for(int var4 = 0; var4 < this.Items.size(); ++var4) {
         InventoryItem var5 = (InventoryItem)this.Items.get(var4);
         if (var1.equals(var5.getModule() + "." + var5.getType())) {
            ++var3;
         } else if (var5 instanceof InventoryContainer && var2) {
            var3 += ((InventoryContainer)var5).getInventory().getItemCount(var1);
         }
      }

      return var3;
   }

   public void setWeightReduction(int var1) {
      var1 = Math.min(var1, 100);
      var1 = Math.max(var1, 0);
      this.weightReduction = var1;
   }

   public int getWeightReduction() {
      return this.weightReduction;
   }

   public boolean doLoad() {
      return true;
   }

   public boolean doLoadActual() throws FileNotFoundException, IOException {
      if (SliceY.SliceBuffer2 == null) {
         SliceY.SliceBuffer2 = ByteBuffer.allocate(10000000);
      }

      File var1 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_con_" + this.ID + ".bin");
      if (!var1.exists()) {
         return false;
      } else {
         FileInputStream var2 = new FileInputStream(var1);
         BufferedInputStream var3 = new BufferedInputStream(var2);
         SliceY.SliceBuffer2.rewind();
         byte[] var4 = SliceY.SliceBuffer2.array();
         var3.read(SliceY.SliceBuffer2.array());
         SliceY.SliceBuffer2.rewind();
         this.load(SliceY.SliceBuffer2, 666, false);
         return true;
      }
   }

   public void removeAllItems() {
      this.drawDirty = true;
      if (this.parent != null) {
         this.dirty = true;
      }

      for(int var1 = 0; var1 < this.Items.size(); ++var1) {
         ((InventoryItem)this.Items.get(var1)).container = null;
      }

      this.Items.clear();
      if (this.parent instanceof IsoDeadBody) {
         ((IsoDeadBody)this.parent).checkClothing();
      }

   }

   public void setCustomTemperature(float var1) {
      this.customTemperature = var1;
   }

   public float getCustomTemperature() {
      return this.customTemperature;
   }

   public InventoryItem getItemFromType(String var1, IsoGameCharacter var2, boolean var3, boolean var4, boolean var5) {
      ArrayList var6 = new ArrayList();
      if (var1.contains(".")) {
         var1 = var1.split("\\.")[1];
      }

      int var7;
      InventoryItem var8;
      for(var7 = 0; var7 < this.getItems().size(); ++var7) {
         var8 = (InventoryItem)this.getItems().get(var7);
         if (!var8.getFullType().equals(var1) && !var8.getType().equals(var1)) {
            if (var5 && this.getItems().get(var7) instanceof InventoryContainer && ((InventoryContainer)this.getItems().get(var7)).getItemContainer() != null && !var6.contains((InventoryContainer)this.getItems().get(var7))) {
               var6.add((InventoryContainer)this.getItems().get(var7));
            }
         } else if ((!var3 || var2 == null || var8 != var2.getClothingItem_Back() && var8 != var2.getClothingItem_Feet() && var8 != var2.getClothingItem_Hands() && var8 != var2.getClothingItem_Head() && var8 != var2.getClothingItem_Legs() && var8 != var2.getClothingItem_Torso()) && this.testBroken(var4, var8)) {
            return var8;
         }
      }

      for(var7 = 0; var7 < var6.size(); ++var7) {
         var8 = ((InventoryContainer)var6.get(var7)).getItemContainer().getItemFromType(var1, var2, var3, var4, var5);
         if (var8 != null) {
            return var8;
         }
      }

      return null;
   }

   public InventoryItem getItemFromType(String var1, boolean var2, boolean var3) {
      return this.getItemFromType(var1, (IsoGameCharacter)null, false, var2, var3);
   }

   public InventoryItem getItemFromType(String var1) {
      return this.getItemFromType(var1, (IsoGameCharacter)null, false, false, false);
   }

   public ArrayList getItemsFromType(String var1) {
      ArrayList var2 = new ArrayList();

      for(int var3 = 0; var3 < this.getItems().size(); ++var3) {
         InventoryItem var4 = (InventoryItem)this.getItems().get(var3);
         if (var4.getFullType().equals(var1) || var4.getType().equals(var1)) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public ArrayList getItemsFromFullType(String var1) {
      ArrayList var2 = new ArrayList();

      for(int var3 = 0; var3 < this.getItems().size(); ++var3) {
         if (((InventoryItem)this.getItems().get(var3)).getFullType().equals(var1)) {
            var2.add(this.getItems().get(var3));
         }
      }

      return var2;
   }

   public ArrayList getItemsFromFullType(String var1, boolean var2) {
      ArrayList var3 = new ArrayList();

      for(int var4 = 0; var4 < this.getItems().size(); ++var4) {
         if (((InventoryItem)this.getItems().get(var4)).getFullType().equals(var1)) {
            var3.add(this.getItems().get(var4));
         }

         if (var2 && this.getItems().get(var4) instanceof InventoryContainer && ((InventoryContainer)this.getItems().get(var4)).getItemContainer() != null) {
            var3.addAll(((InventoryContainer)this.getItems().get(var4)).getItemContainer().getItemsFromFullType(var1, true));
         }
      }

      return var3;
   }

   public ArrayList getItemsFromType(String var1, boolean var2) {
      ArrayList var3 = new ArrayList();
      ArrayList var4 = new ArrayList();

      int var5;
      for(var5 = 0; var5 < this.getItems().size(); ++var5) {
         if (((InventoryItem)this.getItems().get(var5)).getType().equals(var1)) {
            var3.add(this.getItems().get(var5));
         }

         if (var2 && this.getItems().get(var5) instanceof InventoryContainer && ((InventoryContainer)this.getItems().get(var5)).getItemContainer() != null && !var4.contains((InventoryContainer)this.getItems().get(var5))) {
            var4.add((InventoryContainer)this.getItems().get(var5));
         }
      }

      for(var5 = 0; var5 < var4.size(); ++var5) {
         var3.addAll(((InventoryContainer)var4.get(var5)).getItemContainer().getItemsFromType(var1, true));
      }

      return var3;
   }

   public ArrayList getItemsFromCategory(String var1) {
      ArrayList var2 = new ArrayList();

      for(int var3 = 0; var3 < this.getItems().size(); ++var3) {
         if (((InventoryItem)this.getItems().get(var3)).getCategory().equals(var1)) {
            var2.add(this.getItems().get(var3));
         }
      }

      return var2;
   }

   public void sendContentsToRemoteContainer() {
      if (GameClient.bClient) {
         GameClient var10001 = GameClient.instance;
         this.sendContentsToRemoteContainer(GameClient.connection);
      }

   }

   public void requestSync() {
      if (GameClient.bClient) {
         if (this.parent == null || this.parent.square == null || this.parent.square.chunk == null) {
            return;
         }

         GameClient.instance.worldObjectsSyncReq.putRequestSyncIsoChunk(this.parent.square.chunk);
      }

   }

   public void requestServerItemsForContainer() {
      if (this.parent != null && this.parent.square != null) {
         GameClient var10000 = GameClient.instance;
         UdpConnection var1 = GameClient.connection;
         ByteBufferWriter var2 = var1.startPacket();
         PacketTypes.doPacket((short)44, var2);
         var2.putShort((short)IsoPlayer.instance.OnlineID);
         var2.putUTF(this.type);
         if (this.parent.square.getRoom() != null) {
            var2.putUTF(this.parent.square.getRoom().getName());
         } else {
            var2.putUTF("all");
         }

         var2.putInt(this.parent.square.getX());
         var2.putInt(this.parent.square.getY());
         var2.putInt(this.parent.square.getZ());
         int var3 = this.parent.square.getObjects().indexOf(this.parent);
         if (var3 == -1 && this.parent.square.getStaticMovingObjects().indexOf(this.parent) != -1) {
            var2.putShort((short)0);
            var3 = this.parent.square.getStaticMovingObjects().indexOf(this.parent);
            var2.putByte((byte)var3);
         } else if (this.parent instanceof IsoWorldInventoryObject) {
            var2.putShort((short)1);
            var2.putLong(((IsoWorldInventoryObject)this.parent).getItem().id);
         } else if (this.parent instanceof BaseVehicle) {
            var2.putShort((short)3);
            var2.putShort(((BaseVehicle)this.parent).VehicleID);
            var2.putByte((byte)this.vehiclePart.getIndex());
         } else {
            var2.putShort((short)2);
            var2.putByte((byte)var3);
            var2.putByte((byte)this.parent.getContainerIndex(this));
         }

         var1.endPacketUnordered();
      }
   }

   /** @deprecated */
   @Deprecated
   public void sendContentsToRemoteContainer(UdpConnection var1) {
      ByteBufferWriter var2 = var1.startPacket();
      PacketTypes.doPacket((short)20, var2);
      var2.putInt(0);
      boolean var3 = false;
      var2.putInt(this.parent.square.getX());
      var2.putInt(this.parent.square.getY());
      var2.putInt(this.parent.square.getZ());
      var2.putByte((byte)this.parent.square.getObjects().indexOf(this.parent));

      try {
         CompressIdenticalItems.save(var2.bb, this.Items, (IsoGameCharacter)null);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      var1.endPacketUnordered();
   }

   public InventoryItem getItemWithIDRecursiv(long var1) {
      for(int var3 = 0; var3 < this.Items.size(); ++var3) {
         InventoryItem var4 = (InventoryItem)this.Items.get(var3);
         if (var4.id == var1) {
            return var4;
         }

         if (var4 instanceof InventoryContainer && ((InventoryContainer)var4).getItemContainer() != null && !((InventoryContainer)var4).getItemContainer().getItems().isEmpty()) {
            var4 = ((InventoryContainer)var4).getItemContainer().getItemWithIDRecursiv(var1);
            if (var4 != null) {
               return var4;
            }
         }
      }

      return null;
   }

   public InventoryItem getItemWithID(long var1) {
      for(int var3 = 0; var3 < this.Items.size(); ++var3) {
         if (((InventoryItem)this.Items.get(var3)).id == var1) {
            return (InventoryItem)this.Items.get(var3);
         }
      }

      return null;
   }

   public boolean removeItemWithID(long var1) {
      for(int var3 = 0; var3 < this.Items.size(); ++var3) {
         if (((InventoryItem)this.Items.get(var3)).id == var1) {
            this.Remove((InventoryItem)this.Items.get(var3));
            return true;
         }
      }

      return false;
   }

   public boolean containsID(long var1) {
      for(int var3 = 0; var3 < this.Items.size(); ++var3) {
         if (((InventoryItem)this.Items.get(var3)).id == var1) {
            return true;
         }
      }

      return false;
   }

   public boolean removeItemWithIDRecurse(long var1) {
      for(int var3 = 0; var3 < this.Items.size(); ++var3) {
         InventoryItem var4 = (InventoryItem)this.Items.get(var3);
         if (var4.id == var1) {
            this.Remove((InventoryItem)this.Items.get(var3));
            return true;
         }

         if (var4 instanceof InventoryContainer && ((InventoryContainer)var4).getInventory().removeItemWithIDRecurse(var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean isHasBeenLooted() {
      return this.hasBeenLooted;
   }

   public void setHasBeenLooted(boolean var1) {
      this.hasBeenLooted = var1;
   }

   public String getOpenSound() {
      return this.openSound;
   }

   public void setOpenSound(String var1) {
      this.openSound = var1;
   }

   public String getCloseSound() {
      return this.closeSound;
   }

   public void setCloseSound(String var1) {
      this.closeSound = var1;
   }

   public String getPutSound() {
      return this.putSound;
   }

   public void setPutSound(String var1) {
      this.putSound = var1;
   }

   public InventoryItem haveThisKeyId(int var1) {
      for(int var2 = 0; var2 < this.getItems().size(); ++var2) {
         InventoryItem var3 = (InventoryItem)this.getItems().get(var2);
         if (var3 instanceof Key) {
            Key var4 = (Key)var3;
            if (var4.getKeyId() == var1) {
               return var4;
            }
         } else if (var3.getType().equals("KeyRing") && ((InventoryContainer)var3).getInventory().haveThisKeyId(var1) != null) {
            return ((InventoryContainer)var3).getInventory().haveThisKeyId(var1);
         }
      }

      return null;
   }

   public String getOnlyAcceptCategory() {
      return this.OnlyAcceptCategory;
   }

   public void setOnlyAcceptCategory(String var1) {
      this.OnlyAcceptCategory = var1;
   }

   public IsoGameCharacter getCharacter() {
      if (this.getParent() instanceof IsoGameCharacter) {
         return (IsoGameCharacter)this.getParent();
      } else {
         return this.containingItem != null && this.containingItem.getContainer() != null ? this.containingItem.getContainer().getCharacter() : null;
      }
   }

   public void emptyIt() {
      this.Items = new ArrayList();
   }

   public LinkedHashMap getItems4Admin() {
      LinkedHashMap var1 = new LinkedHashMap();

      for(int var2 = 0; var2 < this.getItems().size(); ++var2) {
         InventoryItem var3 = (InventoryItem)this.getItems().get(var2);
         var3.setCount(1);
         if (var3.getCat() != ItemType.Drainable && var3.getCat() != ItemType.Weapon && var1.get(var3.getFullType()) != null && !(var3 instanceof InventoryContainer)) {
            ((InventoryItem)var1.get(var3.getFullType())).setCount(((InventoryItem)var1.get(var3.getFullType())).getCount() + 1);
         } else if (var1.get(var3.getFullType()) != null) {
            var1.put(var3.getFullType() + Rand.Next(100000), var3);
         } else {
            var1.put(var3.getFullType(), var3);
         }
      }

      return var1;
   }

   public LinkedHashMap getAllItems(LinkedHashMap var1, boolean var2) {
      if (var1 == null) {
         var1 = new LinkedHashMap();
      }

      for(int var3 = 0; var3 < this.getItems().size(); ++var3) {
         InventoryItem var4 = (InventoryItem)this.getItems().get(var3);
         if (var2) {
            var4.setWorker("inInv");
         }

         var4.setCount(1);
         if (var4.getCat() != ItemType.Drainable && var4.getCat() != ItemType.Weapon && var1.get(var4.getFullType()) != null) {
            ((InventoryItem)var1.get(var4.getFullType())).setCount(((InventoryItem)var1.get(var4.getFullType())).getCount() + 1);
         } else if (var1.get(var4.getFullType()) != null) {
            var1.put(var4.getFullType() + Rand.Next(100000), var4);
         } else {
            var1.put(var4.getFullType(), var4);
         }

         if (var4 instanceof InventoryContainer && ((InventoryContainer)var4).getItemContainer() != null && !((InventoryContainer)var4).getItemContainer().getItems().isEmpty()) {
            var1 = ((InventoryContainer)var4).getItemContainer().getAllItems(var1, true);
         }
      }

      return var1;
   }

   public InventoryItem getItemById(long var1) {
      for(int var3 = 0; var3 < this.getItems().size(); ++var3) {
         InventoryItem var4 = (InventoryItem)this.getItems().get(var3);
         if (var4.getID() == var1) {
            return var4;
         }

         if (var4 instanceof InventoryContainer && ((InventoryContainer)var4).getItemContainer() != null && !((InventoryContainer)var4).getItemContainer().getItems().isEmpty()) {
            var4 = ((InventoryContainer)var4).getItemContainer().getItemById(var1);
            if (var4 != null) {
               return var4;
            }
         }
      }

      return null;
   }

   public static float floatingPointCorrection(float var0) {
      byte var1 = 100;
      float var2 = var0 * (float)var1;
      return (float)((int)(var2 - (float)((int)var2) >= 0.5F ? var2 + 1.0F : var2)) / (float)var1;
   }

   public void addItemsToProcessItems() {
      IsoWorld.instance.CurrentCell.addToProcessItems(this.Items);
   }

   public void removeItemsFromProcessItems() {
      IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this.Items);
      if (!"floor".equals(this.type)) {
         ItemSoundManager.removeItems(this.Items);
      }

   }

   public boolean isExistYet() {
      if (!SystemDisabler.doWorldSyncEnable) {
         return true;
      } else if (this.getCharacter() != null) {
         return true;
      } else if (this.getParent() instanceof BaseVehicle) {
         return true;
      } else if (this.parent instanceof IsoDeadBody) {
         return this.parent.getStaticMovingObjectIndex() != -1;
      } else if (this.parent instanceof IsoCompost) {
         return this.parent.getObjectIndex() != -1;
      } else if (this.containingItem != null && this.containingItem.worldItem != null) {
         return this.containingItem.worldItem.getWorldObjectIndex() != -1;
      } else if (this.getType().equals("floor")) {
         return true;
      } else if (this.SourceGrid == null) {
         return false;
      } else {
         IsoGridSquare var1 = this.SourceGrid;
         if (!var1.getObjects().contains(this.parent)) {
            return false;
         } else {
            return this.parent.getContainerIndex(this) != -1;
         }
      }
   }
}
