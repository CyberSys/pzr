package zombie.ui;

import zombie.Lua.LuaHookManager;
import zombie.characters.IsoPlayer;
import zombie.characters.Stats;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.Literature;
import zombie.scripting.objects.Item;

public class Sidebar extends UIElement {
   private float HeartOscilationLevel = 0.0F;
   private float HeartOscilator = 0.0F;
   private float HeartOscilatorDecelerator = 0.93F;
   private float HeartOscilatorRate = 0.8F;
   private float HeartOscilatorScalar = 15.6F;
   private float HeartOscilatorStartLevel = 1.0F;
   private float HeartOscilatorStep = 0.0F;
   private float HeartDefaultXOffset = 0.0F;
   public static Sidebar instance = null;
   public static HUDButton InventoryIcon;
   public static HUDButton Heart;
   public static HUDButton Clothing;
   public static HUDButton Crafting;
   public static HUDButton UpInv;
   public static HUDButton DownInv;
   Texture bottom = null;
   public InventoryFlowControl InventoryFlow;
   public VirtualItemSlot MainHand = new VirtualItemSlot("Main", 60, 10, "media/ui/HandMain_Off.png", IsoPlayer.getInstance());
   Texture middle = null;
   public VirtualItemSlot SecondHand = new VirtualItemSlot("Secondary", 60, 10, "media/ui/HandSecondary_Off.png", IsoPlayer.getInstance());
   Texture top = null;
   Texture topRight = null;
   int wheel = 0;
   int lastwheel = 0;

   public Sidebar(int var1, int var2) {
      instance = this;
      this.x = (double)var1;
      this.y = (double)var2;
      this.InventoryFlow = new InventoryFlowControl(10, 60, 1, 8, IsoPlayer.getInstance().getInventory());
      UpInv = new HUDButton("upInv", 18.0D, 49.0D, "media/ui/Inventory_UpArrow_Up.png", "media/ui/Inventory_UpArrow_Down.png", this);
      DownInv = new HUDButton("downInv", 18.0D, 321.0D, "media/ui/Inventory_DownArrow_Up.png", "media/ui/Inventory_DownArrow_Down.png", this);
      InventoryIcon = new HUDButton("Inventory", 0.0D, 0.0D, "media/ui/Inventory_Off.png", "media/ui/Inventory_On.png", this);
      Heart = new HUDButton("Heart", 0.0D, 0.0D, "media/ui/Heart_Off.png", "media/ui/Heart_On.png", this);
      Clothing = new HUDButton("Clothing", 0.0D, 0.0D, "media/ui/Stats_Off.png", "media/ui/Stats_On.png", this);
      Crafting = new HUDButton("Crafting", 0.0D, 0.0D, "media/ui/CraftButton_Up.png", "media/ui/CraftButton_Down.png", this);
      this.bottom = Texture.getSharedTexture("media/ui/SidePanel_Bottom.png");
      this.middle = Texture.getSharedTexture("media/ui/SidePanel_Middle.png");
      this.top = Texture.getSharedTexture("media/ui/SidePanel_Top.png");
      this.topRight = Texture.getSharedTexture("media/ui/SidePanel_TopRight.png");
      this.width = this.width;
      this.height = (float)Core.getInstance().getOffscreenHeight(0);
      byte var3 = 59;
      byte var4 = 10;
      int var5 = (int)((float)var3 + this.MainHand.width / 2.0F);
      int var6 = (int)((float)var4 + this.MainHand.height / 2.0F);
      var6 += 42;
      this.SecondHand.x = (double)((float)var5 - this.SecondHand.width / 2.0F);
      this.SecondHand.y = (double)((float)var6 - this.SecondHand.height / 2.0F);
      var6 += 19;
      InventoryIcon.x = (double)((float)var5 - InventoryIcon.width / 2.0F);
      InventoryIcon.y = (double)var6;
      var6 += 36;
      Heart.x = (double)((float)var5 - Heart.width / 2.0F);
      Heart.y = (double)var6;
      var6 += 29;
      Clothing.x = (double)((float)var5 - Clothing.width / 2.0F);
      Clothing.y = (double)var6;
      this.HeartDefaultXOffset = (float)Heart.x;
      --var5;
      var6 += 29;
      var6 += 38;
      this.AddChild(this.MainHand);
      this.AddChild(this.SecondHand);
      this.AddChild(InventoryIcon);
      this.AddChild(Heart);
      this.AddChild(Clothing);
      this.AddChild(this.InventoryFlow);
      Crafting.x = 8.0D;
      Crafting.y = DownInv.y + (double)DownInv.height + 10.0D;
      this.AddChild(Crafting);
      this.AddChild(UpInv);
      this.AddChild(DownInv);
      this.width = 115.0F;
      this.setVisible(false);
   }

   public void ButtonClicked(String var1) {
      super.ButtonClicked(var1);
      if ("Clothing".equals(var1)) {
         ClothingPanel.instance.setVisible(!ClothingPanel.instance.isVisible());
         ClothingPanel.instance.setX(Clothing.getX() + 40.0D);
         ClothingPanel.instance.setY(Clothing.getY() - 70.0D);
      }

      if ("Heart".equals(var1)) {
         if (UIManager.getDragInventory() != null) {
            if (!LuaHookManager.TriggerHook("HookUseItem", IsoPlayer.getInstance(), UIManager.DragInventory)) {
               if ("PillsBeta".equals(UIManager.getDragInventory().getType())) {
                  if (IsoPlayer.instance != null && IsoPlayer.instance.getStats().Drunkenness > 10.0F) {
                     IsoPlayer.getInstance().BetaBlockers(0.15F);
                  } else {
                     IsoPlayer.getInstance().BetaBlockers(0.3F);
                  }

                  UIManager.getDragInventory().Use();
               } else if ("PillsAntiDep".equals(UIManager.getDragInventory().getType())) {
                  if (IsoPlayer.instance != null && IsoPlayer.instance.getStats().Drunkenness > 10.0F) {
                     IsoPlayer.getInstance().BetaAntiDepress(0.15F);
                  } else {
                     IsoPlayer.getInstance().BetaAntiDepress(0.3F);
                  }

                  UIManager.getDragInventory().Use();
               } else if ("PillsSleepingTablets".equals(UIManager.getDragInventory().getType())) {
                  UIManager.getDragInventory().Use();
                  IsoPlayer.getInstance().SleepingTablet(0.5F);
                  if (IsoPlayer.instance != null) {
                     IsoPlayer.instance.setSleepingPillsTaken(IsoPlayer.instance.getSleepingPillsTaken() + 1);
                  }
               } else if ("Pills".equals(UIManager.getDragInventory().getType())) {
                  UIManager.getDragInventory().Use();
                  if (IsoPlayer.instance != null && IsoPlayer.instance.getStats().Drunkenness > 10.0F) {
                     IsoPlayer.getInstance().PainMeds(0.15F);
                  } else {
                     IsoPlayer.getInstance().PainMeds(0.3F);
                  }
               } else if (UIManager.getDragInventory() instanceof Food) {
                  if (((Food)UIManager.getDragInventory()).isAlcoholic()) {
                     IsoPlayer.getInstance().Eat(UIManager.getDragInventory());
                     UIManager.getDragInventory().Use();
                     IsoPlayer.getInstance().SleepingTablet(0.02F);
                     IsoPlayer.getInstance().BetaAntiDepress(0.4F);
                     IsoPlayer.getInstance().BetaBlockers(0.2F);
                     IsoPlayer.getInstance().PainMeds(0.2F);
                     IsoPlayer.getInstance().getBodyDamage().JustDrankBooze();
                  } else if (!"TinnedSoup".equals(UIManager.DragInventory.getType())) {
                     Food var2 = (Food)UIManager.getDragInventory();
                     NewHealthPanel.instance.ParentChar.Eat(UIManager.getDragInventory());
                     NewHealthPanel.instance.ParentChar.getBodyDamage().JustAteFood(var2);
                     UIManager.getDragInventory().Use();
                  }
               } else if (UIManager.getDragInventory() instanceof Literature) {
                  NewHealthPanel.instance.ParentChar.getBodyDamage().JustReadSomething((Literature)UIManager.getDragInventory());
                  Stats var10000 = IsoPlayer.instance.getStats();
                  var10000.stress -= UIManager.getDragInventory().getStressChange() / 100.0F;
                  UIManager.getDragInventory().Use();
               }
            }
         } else {
            NewHealthPanel.instance.setVisible(!NewHealthPanel.instance.isVisible());
            NewHealthPanel.instance.setX(Heart.getX() + 40.0D);
            NewHealthPanel.instance.setY(Heart.getY() - 70.0D);
         }
      }

      if (var1.equals("upInv")) {
         --this.InventoryFlow.startRow;
      }

      if (var1.equals("downInv")) {
         ++this.InventoryFlow.startRow;
      }

      if ("Crafting".equals(var1)) {
         NewCraftingPanel.instance.setVisible(!NewCraftingPanel.instance.isVisible());
         NewCraftingPanel.instance.setX(Crafting.getX() + 60.0D);
         NewCraftingPanel.instance.setY(Crafting.getY() - 70.0D);
      }

      if ("Inventory".equals(var1)) {
         if (((MovementBlender)this.getParent()).getX() < -30.0D) {
            if (UIManager.getDragInventory() == null) {
               ((MovementBlender)this.getParent()).MoveTo(0.0F, 0.0F, 0.4F);
            } else {
               if (IsoPlayer.getInstance().getCraftIngredient1() == UIManager.getDragInventory()) {
                  IsoPlayer.getInstance().setCraftIngredient1((InventoryItem)null);
               }

               if (IsoPlayer.getInstance().getCraftIngredient2() == UIManager.getDragInventory()) {
                  IsoPlayer.getInstance().setCraftIngredient2((InventoryItem)null);
               }

               if (IsoPlayer.getInstance().getCraftIngredient3() == UIManager.getDragInventory()) {
                  IsoPlayer.getInstance().setCraftIngredient3((InventoryItem)null);
               }

               if (IsoPlayer.getInstance().getCraftIngredient4() == UIManager.getDragInventory()) {
                  IsoPlayer.getInstance().setCraftIngredient4((InventoryItem)null);
               }

               if (IsoPlayer.getInstance().getClothingItem_Head() == UIManager.getDragInventory() && UIManager.getDragInventory() != null) {
                  IsoPlayer.getInstance().setClothingItem_Head((InventoryItem)null);
               }

               if (IsoPlayer.getInstance().getClothingItem_Torso() == UIManager.getDragInventory() && UIManager.getDragInventory() != null) {
                  IsoPlayer.getInstance().setClothingItem_Torso((InventoryItem)null);
                  IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Top, (String)null, (String)null);
               }

               if (IsoPlayer.getInstance().getClothingItem_Hands() == UIManager.getDragInventory() && UIManager.getDragInventory() != null) {
                  IsoPlayer.getInstance().setClothingItem_Hands((InventoryItem)null);
               }

               if (IsoPlayer.getInstance().getClothingItem_Legs() == UIManager.getDragInventory() && UIManager.getDragInventory() != null) {
                  IsoPlayer.getInstance().setClothingItem_Legs((InventoryItem)null);
                  IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Bottoms, (String)null, (String)null);
               }

               if (IsoPlayer.getInstance().getClothingItem_Feet() == UIManager.getDragInventory() && UIManager.getDragInventory() != null) {
                  IsoPlayer.getInstance().setClothingItem_Feet((InventoryItem)null);
                  IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Shoes, (String)null, (String)null);
               }

               if (UIManager.getDragInventory() == this.MainHand.item) {
                  IsoPlayer.getInstance().setPrimaryHandItem((InventoryItem)null);
               }

               if (UIManager.getDragInventory() == this.SecondHand.item) {
                  IsoPlayer.getInstance().setSecondaryHandItem((InventoryItem)null);
               }

               if (UIManager.getDragInventory() == ClothingPanel.instance.HeadItem.item && UIManager.getDragInventory() != null) {
                  IsoPlayer.getInstance().setClothingItem_Head((InventoryItem)null);
               }

               if (UIManager.getDragInventory() == ClothingPanel.instance.TorsoItem.item && UIManager.getDragInventory() != null) {
                  IsoPlayer.getInstance().setClothingItem_Torso((InventoryItem)null);
                  IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Top, (String)null, (String)null);
               }

               if (UIManager.getDragInventory() == ClothingPanel.instance.HandsItem.item) {
                  IsoPlayer.getInstance().setClothingItem_Hands((InventoryItem)null);
               }

               if (UIManager.getDragInventory() == ClothingPanel.instance.LegsItem.item && UIManager.getDragInventory() != null) {
                  IsoPlayer.getInstance().setClothingItem_Legs((InventoryItem)null);
                  IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Bottoms, (String)null, (String)null);
               }

               if (UIManager.getDragInventory() == ClothingPanel.instance.FeetItem.item && UIManager.getDragInventory() != null) {
                  IsoPlayer.getInstance().setClothingItem_Feet((InventoryItem)null);
                  IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Shoes, (String)null, (String)null);
               }

               if (IsoPlayer.getInstance().getInventory() == UIManager.getDragInventory().getContainer()) {
                  UIManager.setDragInventory((InventoryItem)null);
               } else {
                  if (UIManager.getDragInventory().getContainer() != null) {
                     UIManager.getDragInventory().getContainer().Items.remove(UIManager.getDragInventory());
                     UIManager.getDragInventory().getContainer().dirty = true;
                  }

                  UIManager.getDragInventory().setContainer(IsoPlayer.getInstance().getInventory());
                  IsoPlayer.getInstance().getInventory().AddItem(UIManager.getDragInventory());
                  UIManager.setDragInventory((InventoryItem)null);
               }
            }
         } else {
            if (UIManager.getDragInventory() != null) {
               if (UIManager.getDragInventory() == this.MainHand.item) {
                  IsoPlayer.getInstance().setPrimaryHandItem((InventoryItem)null);
               }

               if (UIManager.getDragInventory() == this.SecondHand.item) {
                  IsoPlayer.getInstance().setSecondaryHandItem((InventoryItem)null);
               }

               if (UIManager.getDragInventory() == ClothingPanel.instance.HeadItem.item) {
                  IsoPlayer.getInstance().setClothingItem_Head((InventoryItem)null);
               }

               if (UIManager.getDragInventory() == ClothingPanel.instance.TorsoItem.item) {
                  IsoPlayer.getInstance().setClothingItem_Torso((InventoryItem)null);
                  IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Top, (String)null, (String)null);
               }

               if (UIManager.getDragInventory() == ClothingPanel.instance.HandsItem.item) {
                  IsoPlayer.getInstance().setClothingItem_Hands((InventoryItem)null);
               }

               if (UIManager.getDragInventory() == ClothingPanel.instance.LegsItem.item) {
                  IsoPlayer.getInstance().setClothingItem_Legs((InventoryItem)null);
                  IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Bottoms, (String)null, (String)null);
               }

               if (UIManager.getDragInventory() == ClothingPanel.instance.FeetItem.item) {
                  IsoPlayer.getInstance().setClothingItem_Feet((InventoryItem)null);
                  IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Shoes, (String)null, (String)null);
               }
            }

            if (UIManager.getDragInventory() != null && IsoPlayer.getInstance().getInventory() == UIManager.getDragInventory().getContainer()) {
               UIManager.setDragInventory((InventoryItem)null);
            } else if (UIManager.getDragInventory() != null) {
               if (UIManager.getDragInventory().getContainer() != null) {
                  UIManager.getDragInventory().getContainer().Items.remove(UIManager.getDragInventory());
                  UIManager.getDragInventory().getContainer().dirty = true;
               }

               UIManager.getDragInventory().setContainer(IsoPlayer.getInstance().getInventory());
               IsoPlayer.getInstance().getInventory().AddItem(UIManager.getDragInventory());
               UIManager.setDragInventory((InventoryItem)null);
            } else if (!GameKeyboard.isKeyDown(57)) {
               ((MovementBlender)this.getParent()).MoveTo(-53.0F, 0.0F, 0.3F);
            }

            this.setClickedValue((String)null);
            UIManager.setDragInventory((InventoryItem)null);
         }
      }

   }

   public Boolean onMouseDown(int var1, int var2) {
      super.onMouseDown((double)var1, (double)var2);
      return var1 > 53 && var2 > 200 ? Boolean.FALSE : Boolean.TRUE;
   }

   public Boolean onMouseUp(int var1, int var2) {
      super.onMouseUp((double)var1, (double)var2);
      return var1 > 53 && var2 > 200 ? Boolean.FALSE : Boolean.TRUE;
   }

   public void TriggerHeartWiggle() {
   }

   public void update() {
      if (SpeedControls.instance != null && SpeedControls.instance.getCurrentGameSpeed() == 0) {
         Crafting.setVisible(false);
      } else {
         Crafting.setVisible(true);
      }

      if (IsoPlayer.getInstance() != null && !IsoPlayer.getInstance().isAsleep()) {
         super.update();
         this.HeartOscilatorStep += this.HeartOscilatorRate;
         this.HeartOscilator = (float)Math.sin((double)this.HeartOscilatorStep);
         float var1 = this.HeartOscilator * this.HeartOscilatorScalar * this.HeartOscilationLevel;
         this.HeartOscilationLevel *= this.HeartOscilatorDecelerator;
         Heart.setX((double)(this.HeartDefaultXOffset + var1));
         this.InventoryFlow.hTiles = (int)((double)Core.getInstance().getOffscreenHeight(0) - this.InventoryFlow.getY() - 100.0D) / 32;
         this.InventoryFlow.height = (float)(this.InventoryFlow.hTiles * 32);
         DownInv.setY(this.InventoryFlow.getY() + this.InventoryFlow.getHeight() + 10.0D);
         Crafting.setY(DownInv.getY() + DownInv.getHeight() + 10.0D);
         this.lastwheel = 0;
         this.wheel = Mouse.getWheelState();
         if (this.wheel != this.lastwheel) {
            InventoryFlowControl var10000 = this.InventoryFlow;
            var10000.startRow += this.wheel - this.lastwheel < 0 ? 1 : -1;
            this.InventoryFlow.update();
         }

         if (this.InventoryFlow.Container.Items.size() - this.InventoryFlow.startRow <= this.InventoryFlow.hTiles) {
            DownInv.setVisible(false);
         } else {
            DownInv.setVisible(true);
         }

         if (this.InventoryFlow.startRow > 0) {
            UpInv.setVisible(true);
         } else {
            UpInv.setVisible(false);
         }

         if (!PZConsole.instance.isVisible() && GameKeyboard.isKeyDown(57) && ((MovementBlender)this.getParent()).getX() < -30.0D) {
            ((MovementBlender)this.getParent()).MoveTo(0.0F, 0.0F, 0.4F);
         }

      }
   }

   public void render() {
      super.render();
   }
}
