package zombie.ui;

import java.util.ArrayList;
import java.util.Stack;
import se.krka.kahlua.integration.LuaReturn;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.RecipeManager;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.Food;
import zombie.scripting.objects.Recipe;

public class NewCraftingPanel extends NewWindow {
   public static NewCraftingPanel instance;
   public Texture crafting;
   public VirtualItemSlot ing1;
   public VirtualItemSlot ing2;
   public VirtualItemSlot ing3;
   public VirtualItemSlot ing4;
   public VirtualItemSlot result;
   GenericButton FaceCycleLeft;
   GenericButton FaceCycleRight;
   InventoryItem lastIng1Item;
   InventoryItem lastIng2Item;
   InventoryItem lastIng3Item;
   InventoryItem lastIng4Item;
   Stack RecipeList = null;
   public int activeRecipe = 0;

   public NewCraftingPanel(int var1, int var2) {
      super(var1, var2, 10, 10, true);
      this.ResizeToFitY = false;
      this.visible = false;
      instance = this;
      this.crafting = Texture.getSharedTexture("media/ui/Crafting_Overlay.png");
      this.width = 155.0F;
      this.height = (float)(84 + this.titleRight.getHeight() + 5);
      byte var3 = 6;
      byte var4 = 37;
      this.ing1 = new VirtualItemSlot("CraftingIngredient", var3, 25, "media/ui/ItemBackground_Grey.png", IsoPlayer.getInstance());
      this.ing2 = new VirtualItemSlot("CraftingIngredient", var3 + var4, 25, "media/ui/ItemBackground_Grey.png", IsoPlayer.getInstance());
      this.ing3 = new VirtualItemSlot("CraftingIngredient", var3 + var4 + var4, 25, "media/ui/ItemBackground_Grey.png", IsoPlayer.getInstance());
      this.ing4 = new VirtualItemSlot("CraftingIngredient", var3 + var4 + var4 + var4, 25, "media/ui/ItemBackground_Grey.png", IsoPlayer.getInstance());
      this.result = new VirtualItemSlot("CraftingResult", var3 + var4 + var4 / 2, 75, "media/ui/ItemBackground_Grey.png", IsoPlayer.getInstance());
      this.FaceCycleLeft = new GenericButton(this, (float)(this.result.getX().intValue() - 12), (float)(this.result.getY().intValue() + 5), 11.0F, 17.0F, "CycleFaceLeft", " ", Texture.getSharedTexture("media/ui/LeftArrow_Up.png"), Texture.getSharedTexture("media/ui/LeftArrow_Down.png"));
      this.FaceCycleRight = new GenericButton(this, (float)(this.result.getX().intValue() + this.result.getWidth().intValue() + 1), (float)(this.result.getY().intValue() + 5), 11.0F, 17.0F, "CycleFaceRight", " ", Texture.getSharedTexture("media/ui/RightArrow_Up.png"), Texture.getSharedTexture("media/ui/RightArrow_Down.png"));
      this.ing1.index = 0;
      this.ing2.index = 1;
      this.ing3.index = 2;
      this.ing4.index = 3;
      this.AddChild(this.ing1);
      this.AddChild(this.ing2);
      this.AddChild(this.ing3);
      this.AddChild(this.ing4);
      this.AddChild(this.result);
      this.AddChild(this.FaceCycleLeft);
      this.FaceCycleLeft.visible = false;
      this.FaceCycleRight.visible = false;
      this.AddChild(this.FaceCycleRight);
   }

   public void render() {
      if (this.isVisible() && SpeedControls.instance.getCurrentGameSpeed() != 0) {
         super.render();
         this.DrawTexture(this.crafting, 5.0D, (double)(this.titleRight.getHeight() - 5), (double)this.alpha);
      }
   }

   public void update() {
      if (this.isVisible() && SpeedControls.instance.getCurrentGameSpeed() != 0) {
         super.update();
         if (this.FaceCycleRight.clicked) {
            this.FaceCycleRight.clicked = false;
            ++this.activeRecipe;
            this.RecalcRecipe();
         }

         if (this.FaceCycleLeft.clicked) {
            this.FaceCycleLeft.clicked = false;
            --this.activeRecipe;
            this.RecalcRecipe();
         }

         float var1 = (float)this.getAbsoluteY().intValue();
         float var2 = var1 - (float)(Sidebar.Crafting.getY().intValue() - 70);
         float var3 = (float)Core.getInstance().getOffscreenHeight(0) - var1;
         if (var3 > 0.0F) {
            var2 /= var3;
         } else {
            var2 = 1.0F;
         }

         var2 *= 4.0F;
         var2 = 1.0F - var2;
         if (var2 < 0.0F) {
            var2 = 0.0F;
         }

         if (this.ing1.item != this.lastIng1Item) {
            this.RecalcRecipe();
         }

         if (this.ing2.item != this.lastIng2Item) {
            this.RecalcRecipe();
         }

         if (this.ing3.item != this.lastIng3Item) {
            this.RecalcRecipe();
         }

         if (this.ing4.item != this.lastIng4Item) {
            this.RecalcRecipe();
         }

         this.lastIng1Item = this.ing1.item;
         this.lastIng2Item = this.ing2.item;
         this.lastIng3Item = this.ing3.item;
         this.lastIng4Item = this.ing4.item;
      }
   }

   void PerformMakeItem() {
      if (this.activeRecipe < this.RecipeList.size()) {
         Recipe var1 = (Recipe)this.RecipeList.get(this.activeRecipe);
         if (var1.LuaGrab != null) {
            InventoryItem[] var2 = new InventoryItem[5];
            var2[var1.FindIndexOf(this.ing1.item)] = this.ing1.item;
            var2[var1.FindIndexOf(this.ing2.item)] = this.ing2.item;
            var2[var1.FindIndexOf(this.ing3.item)] = this.ing3.item;
            var2[var1.FindIndexOf(this.ing4.item)] = this.ing4.item;
            InventoryItem var3 = var2[0];
            InventoryItem var4 = var2[1];
            InventoryItem var5 = var2[2];
            InventoryItem var6 = var2[3];
            LuaReturn var7 = LuaManager.caller.protectedCall(LuaManager.thread, LuaManager.env.rawget(((Recipe)this.RecipeList.get(this.activeRecipe)).LuaGrab), var3, var4, var5, var6, this.result.item);
         }

         if (this.result.item instanceof Food) {
            IsoPlayer.getInstance().getXp().AddXP(PerkFactory.Perks.Cooking, 3.0F);
         }

         if (this.result.item.getType().contains("Plank") || this.result.item.getType().contains("Log")) {
            IsoPlayer.getInstance().getXp().AddXP(PerkFactory.Perks.Woodwork, 3.0F);
         }

         float var8;
         int var9;
         if (this.ing1.item != null) {
            if (RecipeManager.DoesWipeUseDelta(this.ing1.item.getType(), this.result.item.getType())) {
               ((Drainable)this.ing1.item).setUsedDelta(0.0F);
            }

            if (RecipeManager.DoesUseItemUp(this.ing1.item.getType(), (Recipe)this.RecipeList.get(this.activeRecipe))) {
               var8 = RecipeManager.UseAmount(this.ing1.item.getType(), (Recipe)this.RecipeList.get(this.activeRecipe), (IsoGameCharacter)null);

               for(var9 = 0; (float)var9 < var8; ++var9) {
                  this.ing1.item.Use(true);
               }
            }

            if (this.ing1.item.getType().contains("Plank") || this.result.item.getType().contains("Log")) {
               IsoPlayer.getInstance().getXp().AddXP(PerkFactory.Perks.Woodwork, 3.0F);
            }
         }

         if (this.ing2.item != null) {
            if (RecipeManager.DoesWipeUseDelta(this.ing2.item.getType(), this.result.item.getType())) {
               ((Drainable)this.ing2.item).setUsedDelta(0.0F);
            }

            if (RecipeManager.DoesUseItemUp(this.ing2.item.getType(), (Recipe)this.RecipeList.get(this.activeRecipe))) {
               var8 = RecipeManager.UseAmount(this.ing2.item.getType(), (Recipe)this.RecipeList.get(this.activeRecipe), (IsoGameCharacter)null);

               for(var9 = 0; (float)var9 < var8; ++var9) {
                  this.ing2.item.Use(true);
               }
            }

            if (this.ing2.item.getType().contains("Plank") || this.ing2.item.getType().contains("Log")) {
               IsoPlayer.getInstance().getXp().AddXP(PerkFactory.Perks.Woodwork, 3.0F);
            }
         }

         if (this.ing3.item != null) {
            if (RecipeManager.DoesWipeUseDelta(this.ing3.item.getType(), this.result.item.getType())) {
               ((Drainable)this.ing3.item).setUsedDelta(0.0F);
            }

            if (RecipeManager.DoesUseItemUp(this.ing3.item.getType(), (Recipe)this.RecipeList.get(this.activeRecipe))) {
               var8 = RecipeManager.UseAmount(this.ing3.item.getType(), (Recipe)this.RecipeList.get(this.activeRecipe), (IsoGameCharacter)null);

               for(var9 = 0; (float)var9 < var8; ++var9) {
                  this.ing3.item.Use(true);
               }
            }

            if (this.ing3.item.getType().contains("Plank") || this.ing3.item.getType().contains("Log")) {
               IsoPlayer.getInstance().getXp().AddXP(PerkFactory.Perks.Woodwork, 3.0F);
            }
         }

         if (this.ing4.item != null) {
            if (RecipeManager.DoesWipeUseDelta(this.ing4.item.getType(), this.result.item.getType())) {
               ((Drainable)this.ing4.item).setUsedDelta(0.0F);
            }

            if (RecipeManager.DoesUseItemUp(this.ing4.item.getType(), (Recipe)this.RecipeList.get(this.activeRecipe))) {
               var8 = RecipeManager.UseAmount(this.ing4.item.getType(), (Recipe)this.RecipeList.get(this.activeRecipe), (IsoGameCharacter)null);

               for(var9 = 0; (float)var9 < var8; ++var9) {
                  this.ing4.item.Use(true);
               }
            }

            if (this.ing4.item.getType().contains("Plank") || this.ing4.item.getType().contains("Log")) {
               IsoPlayer.getInstance().getXp().AddXP(PerkFactory.Perks.Woodwork, 3.0F);
            }
         }

         this.RecalcRecipe();
      }
   }

   private void RecalcRecipe() {
      this.RecipeList = RecipeManager.getUniqueRecipeItems(this.ing1.item, IsoPlayer.instance, (ArrayList)null);
      if (this.RecipeList.isEmpty()) {
         this.result.item = null;
         this.FaceCycleLeft.setVisible(false);
         this.FaceCycleRight.setVisible(false);
      } else {
         if (this.RecipeList.size() > 1) {
            this.FaceCycleLeft.setVisible(true);
            this.FaceCycleRight.setVisible(true);
         } else {
            this.FaceCycleLeft.setVisible(false);
            this.FaceCycleRight.setVisible(false);
         }

         if (this.activeRecipe > this.RecipeList.size() - 1) {
            this.activeRecipe %= this.RecipeList.size();
         }

         if (this.activeRecipe < 0) {
            this.activeRecipe = this.RecipeList.size() - 1;
         }

         String var1 = ((Recipe)this.RecipeList.get(this.activeRecipe)).Result.type;
         if (!var1.contains(".")) {
            var1 = ((Recipe)this.RecipeList.get(this.activeRecipe)).module.name + "." + var1;
         }

         InventoryItem var2 = InventoryItemFactory.CreateItem(var1);
         this.result.item = var2;
         Recipe var3 = (Recipe)this.RecipeList.get(this.activeRecipe);
         if (var3.LuaCreate != null) {
            InventoryItem[] var4 = new InventoryItem[5];
            var4[var3.FindIndexOf(this.ing1.item)] = this.ing1.item;
            var4[var3.FindIndexOf(this.ing2.item)] = this.ing2.item;
            var4[var3.FindIndexOf(this.ing3.item)] = this.ing3.item;
            var4[var3.FindIndexOf(this.ing4.item)] = this.ing4.item;
            InventoryItem var5 = var4[0];
            InventoryItem var6 = var4[1];
            InventoryItem var7 = var4[2];
            InventoryItem var8 = var4[3];
            LuaReturn var9 = LuaManager.caller.protectedCall(LuaManager.thread, LuaManager.env.rawget(((Recipe)this.RecipeList.get(this.activeRecipe)).LuaCreate), var5, var6, var7, var8, this.result.item);
         }

      }
   }
}
