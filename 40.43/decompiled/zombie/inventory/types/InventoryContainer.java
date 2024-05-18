package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Translator;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;

public class InventoryContainer extends InventoryItem {
   ItemContainer container = new ItemContainer();
   int capacity = 0;
   int weightReduction = 0;
   private String CanBeEquipped = "";

   public InventoryContainer(String var1, String var2, String var3, String var4) {
      super(var1, var2, var3, var4);
      this.container.containingItem = this;
      this.container.type = var3;
      this.container.inventoryContainer = this;
   }

   public int getSaveType() {
      return Item.Type.Container.ordinal();
   }

   public String getCategory() {
      return this.mainCategory != null ? this.mainCategory : "Container";
   }

   public ItemContainer getInventory() {
      return this.container;
   }

   public void save(ByteBuffer var1, boolean var2) throws IOException {
      super.save(var1, var2);
      var1.putInt(this.container.ID);
      var1.putInt(this.weightReduction);
      this.container.save(var1, var2);
   }

   public void load(ByteBuffer var1, int var2, boolean var3) throws IOException {
      super.load(var1, var2, var3);
      int var4 = var1.getInt();
      this.setWeightReduction(var1.getInt());
      if (this.container == null) {
         this.container = new ItemContainer();
      }

      this.container.clear();
      this.container.containingItem = this;
      this.container.setWeightReduction(this.weightReduction);
      this.container.Capacity = this.capacity;
      this.container.ID = var4;
      this.container.load(var1, var2, var3);
      String var5 = null;
      if (var2 < 46) {
         if (var1.get() == 1) {
            var5 = GameWindow.ReadString(var1);
         }

         if ("back".equals(var5) && IsoPlayer.instance != null) {
            IsoPlayer.instance.setClothingItem_Back(this);
         }

         if ("secondary".equals(var5) && IsoPlayer.instance != null) {
            IsoPlayer.instance.setSecondaryHandItem(this);
         }

         if ("primary".equals(var5) && IsoPlayer.instance != null) {
            IsoPlayer.instance.setPrimaryHandItem(this);
         }
      }

   }

   public void setCapacity(int var1) {
      this.capacity = var1;
      if (this.container == null) {
         this.container = new ItemContainer();
      }

      this.container.Capacity = var1;
   }

   public int getCapacity() {
      return this.container.getCapacity();
   }

   public int getEffectiveCapacity(IsoGameCharacter var1) {
      return this.container.getEffectiveCapacity(var1);
   }

   public void setWeightReduction(int var1) {
      var1 = Math.min(var1, 100);
      var1 = Math.max(var1, 0);
      this.weightReduction = var1;
      this.container.setWeightReduction(var1);
   }

   public int getWeightReduction() {
      return this.weightReduction;
   }

   public void DoTooltip(ObjectTooltip var1) {
      var1.render();
      super.DoTooltip(var1);
      int var2 = var1.getHeight().intValue();
      var2 -= var1.padBottom;
      if (var1.getWidth() < 160.0D) {
         var1.setWidth(160.0D);
      }

      if (!this.getItemContainer().getItems().isEmpty()) {
         int var3 = 5;
         var2 += 4;
         HashSet var4 = new HashSet();

         for(int var5 = this.getItemContainer().getItems().size() - 1; var5 >= 0; --var5) {
            InventoryItem var6 = (InventoryItem)this.getItemContainer().getItems().get(var5);
            if (var6.getName() != null) {
               if (var4.contains(var6.getName())) {
                  continue;
               }

               var4.add(var6.getName());
            }

            var1.DrawTextureScaledAspect(var6.getTex(), (double)var3, (double)var2, 16.0D, 16.0D, 1.0D, 1.0D, 1.0D, 1.0D);
            var3 += 17;
            if ((float)(var3 + 16) > var1.width - (float)var1.padRight) {
               break;
            }
         }

         var2 += 16;
      }

      var2 += var1.padBottom;
      var1.setHeight((double)var2);
   }

   public void DoTooltip(ObjectTooltip var1, ObjectTooltip.Layout var2) {
      ObjectTooltip.LayoutItem var3;
      if (this.getEffectiveCapacity(var1.getCharacter()) != 0) {
         var3 = var2.addItem();
         var3.setLabel(Translator.getText("Tooltip_container_Capacity") + ":", 1.0F, 1.0F, 1.0F, 1.0F);
         var3.setValueRightNoPlus(this.getEffectiveCapacity(var1.getCharacter()));
      }

      if (this.getWeightReduction() != 0) {
         var3 = var2.addItem();
         var3.setLabel(Translator.getText("Tooltip_container_Weight_Reduction") + ":", 1.0F, 1.0F, 1.0F, 1.0F);
         var3.setValueRightNoPlus(this.getWeightReduction());
      }

   }

   public void setCanBeEquipped(String var1) {
      this.CanBeEquipped = var1;
   }

   public String canBeEquipped() {
      return this.CanBeEquipped;
   }

   public ItemContainer getItemContainer() {
      return this.container;
   }

   public float getContentsWeight() {
      return this.getInventory().getContentsWeight();
   }

   public float getEquippedWeight() {
      float var1 = 1.0F;
      if (this.getWeightReduction() > 0) {
         var1 = 1.0F - (float)this.getWeightReduction() / 100.0F;
      }

      return this.getActualWeight() * 0.3F + this.getContentsWeight() * var1;
   }
}
