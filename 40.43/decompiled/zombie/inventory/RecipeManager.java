package zombie.inventory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import se.krka.kahlua.integration.LuaReturn;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.skills.PerkFactory;
import zombie.debug.DebugLog;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.GameClient;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.EvolvedRecipe;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.Recipe;
import zombie.scripting.objects.ScriptModule;
import zombie.vehicles.VehiclePart;

public class RecipeManager {
   private static Stack RecipeList = new Stack();
   private static final ArrayList tempItems = new ArrayList();

   public static void Loaded() {
      Stack var0 = ScriptManager.instance.getAllRecipes();
      HashSet var1 = new HashSet();

      for(int var2 = 0; var2 < var0.size(); ++var2) {
         Recipe var3 = (Recipe)var0.get(var2);

         for(int var4 = 0; var4 < var3.getSource().size(); ++var4) {
            Recipe.Source var5 = (Recipe.Source)var3.getSource().get(var4);

            for(int var6 = 0; var6 < var5.getItems().size(); ++var6) {
               String var7 = (String)var5.getItems().get(var6);
               if (!"Water".equals(var7) && !var7.contains(".")) {
                  ScriptModule var8 = var3.getModule();
                  Item var9 = var8.getItem(var7);
                  if (var9 != null && !var9.getObsolete()) {
                     var5.getItems().set(var6, var9.getModule().getName() + "." + var7);
                  } else {
                     var8 = ScriptManager.instance.getModule("Base");
                     var9 = var8.getItem(var7);
                     if (var9 != null && !var9.getObsolete()) {
                        String var10 = var3.getModule().getName();
                        if (!var1.contains(var10)) {
                           var1.add(var10);
                           DebugLog.log("WARNING: module \"" + var10 + "\" may have forgot to import module Base");
                        }

                        var5.getItems().set(var6, var9.getModule().getName() + "." + var7);
                     } else {
                        DebugLog.log("ERROR: can't find recipe source \"" + var7 + "\" in recipe \"" + var3.getOriginalname() + "\"");
                        var5.getItems().set(var6, "???." + var7);
                     }
                  }
               }
            }
         }

         if (var3.getResult() != null && var3.getResult().getModule() == null) {
            ScriptModule var11 = var3.getModule();
            Item var12 = var11.getItem(var3.getResult().getType());
            if (var12 != null && !var12.getObsolete()) {
               var3.getResult().module = var12.getModule().getName();
            } else {
               var11 = ScriptManager.instance.getModule("Base");
               var12 = var11.getItem(var3.getResult().getType());
               if (var12 != null && !var12.getObsolete()) {
                  String var13 = var3.getModule().getName();
                  if (!var1.contains(var13)) {
                     var1.add(var13);
                     DebugLog.log("WARNING: module \"" + var13 + "\" may have forgot to import module Base");
                  }

                  var3.getResult().module = var12.getModule().getName();
               } else {
                  DebugLog.log("ERROR: can't find recipe result \"" + var3.getResult().getType() + "\" in recipe \"" + var3.getOriginalname() + "\"");
                  var3.getResult().module = "???";
               }
            }
         }
      }

   }

   public static boolean DoesWipeUseDelta(String var0, String var1) {
      return true;
   }

   public static int getKnownRecipesNumber(IsoGameCharacter var0) {
      int var1 = 0;
      Stack var2 = ScriptManager.instance.getAllRecipes();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         Recipe var4 = (Recipe)var2.get(var3);
         if (!var4.needToBeLearn() || var0.getKnownRecipes().contains(var4.getOriginalname())) {
            ++var1;
         }
      }

      return var1;
   }

   public static boolean DoesUseItemUp(String var0, Recipe var1) {
      assert "Water".equals(var0) || var0.contains(".");

      for(int var2 = 0; var2 < var1.Source.size(); ++var2) {
         if (((Recipe.Source)var1.Source.get(var2)).keep) {
            ArrayList var3 = ((Recipe.Source)var1.Source.get(var2)).getItems();

            for(int var4 = 0; var4 < var3.size(); ++var4) {
               if (var0.equals(var3.get(var4))) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public static boolean IsItemDestroyed(String var0, Recipe var1) {
      assert "Water".equals(var0) || var0.contains(".");

      for(int var2 = 0; var2 < var1.Source.size(); ++var2) {
         Recipe.Source var3 = (Recipe.Source)var1.getSource().get(var2);
         if (var3.destroy) {
            for(int var4 = 0; var4 < var3.getItems().size(); ++var4) {
               if (var0.equals(var3.getItems().get(var4))) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static float UseAmount(String var0, Recipe var1, IsoGameCharacter var2) {
      Recipe.Source var3 = var1.findSource(var0);
      return var3.getCount();
   }

   public static Stack getUniqueRecipeItems(InventoryItem var0, IsoGameCharacter var1, ArrayList var2) {
      RecipeList.clear();
      Stack var3 = ScriptManager.instance.getAllRecipes();

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         Recipe var5 = (Recipe)var3.get(var4);
         if (IsRecipeValid(var5, var1, var0, var2)) {
            RecipeList.add(var5);
         }
      }

      return RecipeList;
   }

   public static boolean IsRecipeValid(Recipe var0, IsoGameCharacter var1, InventoryItem var2, ArrayList var3) {
      if (var0.Result == null) {
         return false;
      } else if (!var1.isRecipeKnown(var0)) {
         return false;
      } else if (var2 != null && !RecipeContainsItem(var0, var2)) {
         return false;
      } else if (!HasAllRequiredItems(var0, var1, var2, var3)) {
         return false;
      } else if (!HasRequiredSkill(var0, var1)) {
         return false;
      } else if (!isNearItem(var0, var1)) {
         return false;
      } else {
         return hasHeat(var0, var2, var3, var1);
      }
   }

   private static boolean isNearItem(Recipe var0, IsoGameCharacter var1) {
      if (var0.getNearItem() != null && !var0.getNearItem().equals("")) {
         for(int var2 = var1.getSquare().getX() - 2; var2 < var1.getSquare().getX() + 2; ++var2) {
            for(int var3 = var1.getSquare().getY() - 2; var3 < var1.getSquare().getY() + 2; ++var3) {
               IsoGridSquare var4 = var1.getCell().getGridSquare(var2, var3, 0);
               if (var4 != null) {
                  for(int var5 = 0; var5 < var4.getObjects().size(); ++var5) {
                     if (var0.getNearItem().equals(((IsoObject)var4.getObjects().get(var5)).getName())) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private static boolean HasRequiredSkill(Recipe var0, IsoGameCharacter var1) {
      if (var0.skillRequired != null) {
         Iterator var2 = var0.skillRequired.keySet().iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            if (var1.getPerkLevel(PerkFactory.Perks.FromString(var3)) < (Integer)var0.skillRequired.get(var3)) {
               return false;
            }
         }
      }

      return true;
   }

   private static boolean RecipeContainsItem(Recipe var0, InventoryItem var1) {
      for(int var2 = 0; var2 < var0.Source.size(); ++var2) {
         Recipe.Source var3 = (Recipe.Source)var0.getSource().get(var2);

         for(int var4 = 0; var4 < var3.getItems().size(); ++var4) {
            String var5 = (String)var3.getItems().get(var4);
            if ("Water".equals(var5) && var1.isWaterSource()) {
               return true;
            }

            if (var5.equals(var1.getFullType())) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean HasAllRequiredItems(Recipe var0, IsoGameCharacter var1, InventoryItem var2, ArrayList var3) {
      ArrayList var4 = getAvailableItemsNeeded(var0, var1, var3, var2, (ArrayList)null);
      Iterator var5 = var4.iterator();

      InventoryItem var6;
      do {
         if (!var5.hasNext()) {
            return !var4.isEmpty();
         }

         var6 = (InventoryItem)var5.next();
         if (var6 instanceof Food && ((Food)var6).getFreezingTime() > 0.0F) {
            return false;
         }
      } while(!var0.noBrokenItems() || !var6.isBroken());

      return false;
   }

   public static boolean hasHeat(Recipe var0, InventoryItem var1, ArrayList var2, IsoGameCharacter var3) {
      if (var0.getHeat() == 0.0F) {
         return true;
      } else {
         InventoryItem var4 = null;
         Iterator var5 = getAvailableItemsNeeded(var0, var3, var2, var1, (ArrayList)null).iterator();

         while(var5.hasNext()) {
            InventoryItem var6 = (InventoryItem)var5.next();
            if (var6 instanceof DrainableComboItem) {
               var4 = var6;
               break;
            }
         }

         if (var4 != null) {
            var5 = var2.iterator();

            while(var5.hasNext()) {
               ItemContainer var9 = (ItemContainer)var5.next();
               Iterator var7 = var9.getItems().iterator();

               while(var7.hasNext()) {
                  InventoryItem var8 = (InventoryItem)var7.next();
                  if (var8.getName().equals(var4.getName())) {
                     if (var0.getHeat() < 0.0F) {
                        if (var8.getInvHeat() <= var0.getHeat()) {
                           return true;
                        }
                     } else if (var0.getHeat() > 0.0F && var8.getInvHeat() + 1.0F >= var0.getHeat()) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   public static ArrayList getAvailableItemsAll(Recipe var0, IsoGameCharacter var1, ArrayList var2, InventoryItem var3, ArrayList var4) {
      return getAvailableItems(var0, var1, var2, var3, var4, true).allItems;
   }

   public static ArrayList getAvailableItemsNeeded(Recipe var0, IsoGameCharacter var1, ArrayList var2, InventoryItem var3, ArrayList var4) {
      return getAvailableItems(var0, var1, var2, var3, var4, false).allItems;
   }

   private static RecipeManager.SourceItems getAvailableItems(Recipe var0, IsoGameCharacter var1, ArrayList var2, InventoryItem var3, ArrayList var4, boolean var5) {
      if (var3 != null && (var3.getContainer() == null || !var3.getContainer().contains(var3))) {
         DebugLog.log("recipe: item appears to have been used already, ignoring " + var3.getFullType());
         var3 = null;
      }

      RecipeManager.SourceItems var6 = new RecipeManager.SourceItems(var0, var1, var3, var4);
      if (var2 == null) {
         var2 = new ArrayList();
         var2.add(var1.getInventory());
      }

      if (var3 != null && !RecipeContainsItem(var0, var3)) {
         throw new RuntimeException("item " + var3.getFullType() + " isn't used in recipe " + var0.getOriginalname());
      } else {
         RecipeManager.RMRecipe var7 = RecipeManager.RMRecipe.alloc(var0);
         var7.getItemsFromContainers(var1, var2, var3);
         if (var5 || var7.hasItems()) {
            var7.getAvailableItems(var6, var5);
         }

         RecipeManager.RMRecipe.release(var7);
         return var6;
      }
   }

   public static int getNumberOfTimesRecipeCanBeDone(Recipe var0, IsoGameCharacter var1, ArrayList var2, InventoryItem var3) {
      int var4 = 0;
      RecipeManager.RMRecipe var5 = RecipeManager.RMRecipe.alloc(var0);
      if (var2 == null) {
         var2 = new ArrayList();
         var2.add(var1.getInventory());
      }

      var5.getItemsFromContainers(var1, var2, var3);
      ArrayList var6 = new ArrayList();

      for(ArrayList var7 = new ArrayList(); var5.hasItems(); ++var4) {
         var7.clear();
         var5.Use(var7);
         if (var6.containsAll(var7)) {
            var4 = -1;
            break;
         }

         var6.addAll(var7);

         for(int var8 = 0; var8 < var7.size(); ++var8) {
            InventoryItem var9 = (InventoryItem)var7.get(var8);
            if (var9 instanceof Food && ((Food)var9).isFrozen()) {
               --var4;
               break;
            }
         }
      }

      RecipeManager.RMRecipe.release(var5);
      return var4;
   }

   public static InventoryItem PerformMakeItem(Recipe var0, InventoryItem var1, IsoGameCharacter var2, ArrayList var3) {
      RecipeManager.SourceItems var4 = getAvailableItems(var0, var2, var3, var1, (ArrayList)null, false);
      ArrayList var5 = var4.allItems;
      if (var5.isEmpty()) {
         throw new RuntimeException("getAvailableItems() didn't return the required number of items");
      } else {
         if (var1 == var2.getPrimaryHandItem()) {
            var2.setPrimaryHandItem((InventoryItem)null);
         }

         if (var1 == var2.getSecondaryHandItem()) {
            var2.setSecondaryHandItem((InventoryItem)null);
         }

         Recipe.Result var6 = var0.getResult();
         InventoryItem var7 = InventoryItemFactory.CreateItem(var6.getFullType());
         boolean var8 = false;
         boolean var9 = false;
         int var10 = -1;
         int var11 = 0;
         boolean var12 = false;
         float var13 = 0.0F;
         float var14 = 0.0F;
         int var15 = 0;
         int var16 = 0;

         int var17;
         label164:
         for(var17 = 0; var17 < var0.getSource().size(); ++var17) {
            Recipe.Source var18 = (Recipe.Source)var0.getSource().get(var17);
            if (!var18.isKeep()) {
               ArrayList var19 = var4.itemsPerSource[var17];
               int var21;
               int var22;
               int var23;
               InventoryItem var27;
               switch(var4.typePerSource[var17]) {
               case DRAINABLE:
                  int var20 = (int)var18.getCount();
                  var21 = 0;

                  for(; var21 < var19.size(); ++var21) {
                     InventoryItem var29 = (InventoryItem)var19.get(var21);
                     var23 = AvailableUses(var29);
                     if (var23 >= var20) {
                        ReduceUses(var29, (float)var20, var2);
                        var20 = 0;
                     } else {
                        ReduceUses(var29, (float)var23, var2);
                        var20 -= var23;
                     }
                  }

                  if (var20 > 0) {
                     throw new RuntimeException("required amount of " + var18.getItems() + " wasn't available");
                  }
                  break;
               case FOOD:
                  var21 = (int)var18.use;
                  var22 = 0;

                  while(true) {
                     if (var22 >= var19.size()) {
                        continue label164;
                     }

                     var27 = (InventoryItem)var19.get(var22);
                     int var28 = AvailableUses(var27);
                     if (var28 >= var21) {
                        ReduceUses(var27, (float)var21, var2);
                        var21 = 0;
                     } else {
                        ReduceUses(var27, (float)var28, var2);
                        var21 -= var28;
                     }

                     ++var22;
                  }
               case DESTROY:
                  var22 = 0;

                  while(true) {
                     if (var22 >= var19.size()) {
                        continue label164;
                     }

                     var27 = (InventoryItem)var19.get(var22);
                     RemoveItem(var27);
                     ++var22;
                  }
               case OTHER:
                  var22 = 0;

                  while(true) {
                     if (var22 >= var19.size()) {
                        continue label164;
                     }

                     var27 = (InventoryItem)var19.get(var22);
                     UseSync(var27, true, false);
                     ++var22;
                  }
               case WATER:
                  var22 = var0.getWaterAmountNeeded();

                  for(var23 = 0; var23 < var19.size(); ++var23) {
                     InventoryItem var24 = (InventoryItem)var19.get(var23);
                     int var25 = AvailableUses(var24);
                     if (var25 >= var22) {
                        ReduceUses(var24, (float)var22, var2);
                        var22 = 0;
                     } else {
                        ReduceUses(var24, (float)var25, var2);
                        var22 -= var25;
                     }
                  }

                  if (var22 > 0) {
                     throw new RuntimeException("required amount of water wasn't available");
                  }
               }
            }
         }

         InventoryItem var26;
         for(var17 = 0; var17 < var5.size(); ++var17) {
            var26 = (InventoryItem)var5.get(var17);
            if (var26 instanceof Food) {
               if (((Food)var26).isCooked()) {
                  var8 = true;
               }

               if (((Food)var26).isBurnt()) {
                  var9 = true;
               }

               var10 = ((Food)var26).getPoisonDetectionLevel();
               var11 = ((Food)var26).getPoisonPower();
               ++var16;
               if (var26.getAge() > (float)var26.getOffAgeMax()) {
                  var12 = true;
               } else if (!var12 && var26.getOffAgeMax() < 1000000000) {
                  var14 += var26.getAge() / (float)var26.getOffAgeMax();
               }
            }

            if (var7 instanceof Food && var26.isTaintedWater()) {
               var7.setTaintedWater(true);
            }

            var13 += (float)var26.getCondition() / (float)var26.getConditionMax();
            ++var15;
         }

         if (var7 instanceof Food && ((Food)var7).IsCookable) {
            ((Food)var7).setCooked(var8);
            ((Food)var7).setBurnt(var9);
            ((Food)var7).setPoisonDetectionLevel(var10);
            ((Food)var7).setPoisonPower(var11);
         }

         if ((double)var7.getOffAgeMax() != 1.0E9D) {
            if (!var12) {
               var17 = Math.round((float)var7.getOffAgeMax() * (var14 / (float)var16));
               if (var17 >= var7.getOffAgeMax()) {
                  var17 = var7.getOffAgeMax() - 1;
               }

               var7.setAge((float)var17);
            } else {
               var7.setAge((float)var7.getOffAgeMax());
            }
         }

         var7.setCondition(Math.round((float)var7.getConditionMax() * (var13 / (float)var15)));

         for(var17 = 0; var17 < var5.size(); ++var17) {
            var26 = (InventoryItem)var5.get(var17);
            var7.setConditionFromModData(var26);
         }

         GivePlayerExperience(var0, var5, var7, var2);
         if (var0.LuaCreate != null) {
            LuaManager.caller.protectedCall(LuaManager.thread, LuaManager.env.rawget(var0.LuaCreate), var5, var7, var2, var1);
         }

         if (!var0.isRemoveResultItem()) {
            return var7;
         } else {
            return null;
         }
      }
   }

   private static boolean ReduceUses(InventoryItem var0, float var1, IsoGameCharacter var2) {
      float var4;
      if (var0 instanceof DrainableComboItem) {
         DrainableComboItem var3 = (DrainableComboItem)var0;
         var4 = var3.getUseDelta() * var1;
         var3.setUsedDelta(var3.getUsedDelta() - var4);
         if (AvailableUses(var0) < 1) {
            var3.setUsedDelta(0.0F);
            UseSync(var3);
            return true;
         }

         if (GameClient.bClient && !var0.isInPlayerInventory()) {
            GameClient.instance.sendItemStats(var0);
         }
      }

      if (var0 instanceof Food) {
         Food var6 = (Food)var0;
         if (var6.getHungerChange() < 0.0F) {
            var4 = Math.min(-var6.getHungerChange() * 100.0F, var1);
            float var5 = var4 / (-var6.getHungerChange() * 100.0F);
            if (var5 < 0.0F) {
               var5 = 0.0F;
            }

            if (var5 > 1.0F) {
               var5 = 1.0F;
            }

            var6.setHungChange(var6.getHungChange() - var6.getHungChange() * var5);
            var6.setCalories(var6.getCalories() - var6.getCalories() * var5);
            var6.setCarbohydrates(var6.getCarbohydrates() - var6.getCarbohydrates() * var5);
            var6.setLipids(var6.getLipids() - var6.getLipids() * var5);
            var6.setProteins(var6.getProteins() - var6.getProteins() * var5);
            var6.setThirstChange(var6.getThirstChange() - var6.getThirstChange() * var5);
            var6.setFluReduction(var6.getFluReduction() - (int)((float)var6.getFluReduction() * var5));
            var6.setPainReduction(var6.getPainReduction() - var6.getPainReduction() * var5);
            var6.setEndChange(var6.getEnduranceChange() - var6.getEnduranceChange() * var5);
            var6.setReduceFoodSickness(var6.getReduceFoodSickness() - (int)((float)var6.getReduceFoodSickness() * var5));
            var6.setStressChange(var6.getStressChange() - var6.getStressChange() * var5);
            var6.setFatigueChange(var6.getFatigueChange() - var6.getFatigueChange() * var5);
            if ((double)var6.getHungerChange() > -0.01D) {
               UseSync(var6);
               return true;
            }

            if (GameClient.bClient && !var0.isInPlayerInventory()) {
               GameClient.instance.sendItemStats(var0);
            }
         }
      }

      return false;
   }

   private static int AvailableUses(InventoryItem var0) {
      if (var0 instanceof DrainableComboItem) {
         DrainableComboItem var2 = (DrainableComboItem)var0;
         return var2.getDrainableUsesInt();
      } else if (var0 instanceof Food) {
         Food var1 = (Food)var0;
         return (int)(-var1.getHungerChange() * 100.0F);
      } else {
         return 0;
      }
   }

   private static void UseSync(InventoryItem var0) {
      if (var0 instanceof DrainableComboItem) {
         DrainableComboItem var1 = (DrainableComboItem)var0;
         var1.setDelta(var1.getDelta() - var1.getUseDelta());
         InventoryItem var4;
         if (var1.uses > 1) {
            int var2 = var1.uses - 1;
            var1.uses = 1;
            CreateItem(var1.getFullType(), tempItems);
            byte var3 = 0;
            if (var3 < tempItems.size()) {
               var4 = (InventoryItem)tempItems.get(var3);
               var4.setUses(var2);
               AddItem(var1, var4);
            }
         }

         if (var1.getDelta() <= 1.0E-4F) {
            var1.setDelta(0.0F);
            if (var1.getReplaceOnDeplete() == null) {
               UseSync(var1, false, false);
            } else {
               String var5 = var1.getReplaceOnDepleteFullType();
               CreateItem(var5, tempItems);

               for(int var6 = 0; var6 < tempItems.size(); ++var6) {
                  var4 = (InventoryItem)tempItems.get(var6);
                  AddItem(var1, var4);
               }

               RemoveItem(var1);
            }
         }

         var1.updateWeight();
      } else {
         UseSync(var0, false, false);
      }

   }

   private static void UseSync(InventoryItem var0, boolean var1, boolean var2) {
      if (var0.DisappearOnUse || var1) {
         --var0.uses;
         if (var0.replaceOnUse != null && !var2 && !var1) {
            String var3 = var0.replaceOnUse;
            if (!var3.contains(".")) {
               var3 = var0.module + "." + var3;
            }

            CreateItem(var3, tempItems);

            for(int var4 = 0; var4 < tempItems.size(); ++var4) {
               InventoryItem var5 = (InventoryItem)tempItems.get(var4);
               var5.setConditionFromModData(var0);
               AddItem(var0, var5);
            }
         }

         if (var0.uses <= 0) {
            if (var0.keepOnDeplete) {
               return;
            }

            RemoveItem(var0);
         } else if (GameClient.bClient && !var0.isInPlayerInventory()) {
            GameClient.instance.sendItemStats(var0);
         }

      }
   }

   private static void CreateItem(String var0, ArrayList var1) {
      var1.clear();
      Item var2 = ScriptManager.instance.FindItem(var0);
      if (var2 == null) {
         DebugLog.log("ERROR: RecipeManager.CreateItem: can't find " + var0);
      } else {
         int var3 = var2.getCount();

         for(int var4 = 0; var4 < var3; ++var4) {
            InventoryItem var5 = InventoryItemFactory.CreateItem(var0);
            if (var5 == null) {
               return;
            }

            var1.add(var5);
         }

      }
   }

   private static void AddItem(InventoryItem var0, InventoryItem var1) {
      IsoWorldInventoryObject var2 = var0.getWorldItem();
      if (var2 != null && var2.getWorldObjectIndex() == -1) {
         var2 = null;
      }

      if (var2 != null) {
         var2.getSquare().AddWorldInventoryItem(var1, 0.0F, 0.0F, 0.0F, true);
      } else if (var0.container != null) {
         IsoObject var3 = var0.container.parent;
         VehiclePart var4 = var0.container.vehiclePart;
         if (!var0.isInPlayerInventory() && GameClient.bClient) {
            var0.container.addItemOnServer(var1);
         }

         var0.container.AddItem(var1);
         if (var4 != null) {
            var4.setContainerContentAmount(var4.getItemContainer().getCapacityWeight());
         }
      }

   }

   private static void RemoveItem(InventoryItem var0) {
      IsoWorldInventoryObject var1 = var0.getWorldItem();
      if (var1 != null && var1.getWorldObjectIndex() == -1) {
         var1 = null;
      }

      if (var1 != null) {
         var1.getSquare().transmitRemoveItemFromSquare(var1);
      } else if (var0.container != null) {
         IsoObject var2 = var0.container.parent;
         VehiclePart var3 = var0.container.vehiclePart;
         if (var2 instanceof IsoGameCharacter) {
            IsoGameCharacter var4 = (IsoGameCharacter)var2;
            if (var0 instanceof Clothing) {
               ((Clothing)var0).Unwear();
            }

            if (var4.getPrimaryHandItem() == var0) {
               var4.setPrimaryHandItem((InventoryItem)null);
            }

            if (var4.getSecondaryHandItem() == var0) {
               var4.setSecondaryHandItem((InventoryItem)null);
            }

            if (var4.getClothingItem_Back() == var0) {
               var4.setClothingItem_Back((InventoryItem)null);
            }
         } else if (!var0.isInPlayerInventory() && GameClient.bClient) {
            var0.container.removeItemOnServer(var0);
         }

         var0.container.Items.remove(var0);
         var0.container.dirty = true;
         var0.container.drawDirty = true;
         var0.container = null;
         if (var2 instanceof IsoDeadBody) {
            ((IsoDeadBody)var2).checkClothing();
         }

         if (var3 != null) {
            var3.setContainerContentAmount(var3.getItemContainer().getCapacityWeight());
         }
      }

   }

   private static void GivePlayerExperience(Recipe var0, ArrayList var1, InventoryItem var2, IsoGameCharacter var3) {
      String var4 = var0.LuaGiveXP;
      if (var4 == null) {
         var4 = "DefaultRecipe_OnGiveXP";
      }

      Object var5 = LuaManager.env.rawget(var4);
      if (var5 == null) {
         DebugLog.log("ERROR: Lua method \"" + var4 + "\" not found (in RecipeManager.GivePlayerExperience())");
      } else {
         LuaManager.caller.protectedCall(LuaManager.thread, var5, var0, var1, var2, var3);
      }
   }

   public static ArrayList getAllEvolvedRecipes() {
      Stack var0 = ScriptManager.instance.getAllEvolvedRecipes();
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < var0.size(); ++var2) {
         var1.add(var0.get(var2));
      }

      return var1;
   }

   public static ArrayList getEvolvedRecipe(InventoryItem var0, IsoGameCharacter var1, ArrayList var2, boolean var3) {
      ArrayList var4 = new ArrayList();
      if (var0 instanceof Food && ((Food)var0).isRotten() && var1.getPerkLevel(PerkFactory.Perks.Cooking) < 7) {
         return var4;
      } else if (var0 instanceof Food && ((Food)var0).isFrozen()) {
         return var4;
      } else {
         Stack var5 = ScriptManager.instance.getAllEvolvedRecipes();

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            EvolvedRecipe var7 = (EvolvedRecipe)var5.get(var6);
            if ((var0.isCooked() && var7.addIngredientIfCooked || !var0.isCooked()) && (var0.getType().equals(var7.baseItem) || var0.getType().equals(var7.getResultItem())) && (!var0.getType().equals("WaterPot") || !((double)((Drainable)var0).getUsedDelta() < 0.75D))) {
               if (var3) {
                  ArrayList var8 = var7.getItemsCanBeUse(var1, var0, var2);
                  if (!var8.isEmpty()) {
                     var4.add(var7);
                  }
               } else {
                  var4.add(var7);
               }
            }
         }

         return var4;
      }
   }

   private static void DebugPrintAllRecipes() {
      Stack var0 = ScriptManager.instance.getAllRecipes();

      for(int var1 = 0; var1 < var0.size(); ++var1) {
         Recipe var2 = (Recipe)var0.get(var1);
         if (var2 == null) {
            DebugLog.log("Null recipe.");
         } else if (var2.Result == null) {
            DebugLog.log("Null result.");
         } else {
            DebugLog.log(var2.Result.type);
            DebugLog.log("-----");

            for(int var3 = 0; var3 < var2.Source.size(); ++var3) {
               if (var2.Source.get(var3) == null) {
                  DebugLog.log("Null ingredient.");
               } else if (((Recipe.Source)var2.Source.get(var3)).getItems().isEmpty()) {
                  DebugLog.log(((Recipe.Source)var2.Source.get(var3)).getItems().toString());
               }
            }
         }
      }

   }

   private static class RMRecipeItem {
      InventoryItem item;
      int uses;
      int water;
      static ArrayDeque pool = new ArrayDeque();

      RecipeManager.RMRecipeItem init(InventoryItem var1) {
         this.item = var1;
         return this;
      }

      RecipeManager.RMRecipeItem reset() {
         this.item = null;
         this.uses = 0;
         this.water = 0;
         return this;
      }

      int Use(int var1) {
         int var2 = Math.min(this.uses, var1);
         this.uses -= var2;
         return var2;
      }

      int UseWater(int var1) {
         int var2 = Math.min(this.water, var1);
         this.water -= var2;
         return var2;
      }

      static RecipeManager.RMRecipeItem alloc(InventoryItem var0) {
         return pool.isEmpty() ? (new RecipeManager.RMRecipeItem()).init(var0) : ((RecipeManager.RMRecipeItem)pool.pop()).init(var0);
      }

      static void release(RecipeManager.RMRecipeItem var0) {
         assert !pool.contains(var0);

         pool.push(var0.reset());
      }
   }

   private static class RMRecipeItemList {
      RecipeManager.RMRecipeSource source;
      ArrayList items = new ArrayList();
      int index;
      int usesNeeded;
      RecipeManager.RMRecipeItemList.Type type;
      static ArrayDeque pool = new ArrayDeque();

      private RMRecipeItemList() {
         this.type = RecipeManager.RMRecipeItemList.Type.NONE;
      }

      RecipeManager.RMRecipeItemList init(RecipeManager.RMRecipeSource var1, int var2) {
         assert this.items.isEmpty();

         this.source = var1;
         this.index = var2;
         String var3 = (String)var1.source.getItems().get(var2);
         this.usesNeeded = (int)var1.source.getCount();
         if ("Water".equals(var3)) {
            this.type = RecipeManager.RMRecipeItemList.Type.WATER;
         } else if (var1.source.isDestroy()) {
            this.type = RecipeManager.RMRecipeItemList.Type.DESTROY;
         } else if (ScriptManager.instance.isDrainableItemType(var3)) {
            this.type = RecipeManager.RMRecipeItemList.Type.DRAINABLE;
         } else if (var1.source.use > 0.0F) {
            this.usesNeeded = (int)var1.source.use;
            this.type = RecipeManager.RMRecipeItemList.Type.FOOD;
         } else {
            this.type = RecipeManager.RMRecipeItemList.Type.OTHER;
         }

         return this;
      }

      RecipeManager.RMRecipeItemList reset() {
         this.source = null;
         this.items.clear();
         return this;
      }

      void getItemsFrom(ArrayList var1, RecipeManager.RMRecipe var2) {
         String var3 = (String)this.source.source.getItems().get(this.index);

         for(int var4 = 0; var4 < var1.size(); ++var4) {
            RecipeManager.RMRecipeItem var5 = (RecipeManager.RMRecipeItem)var1.get(var4);
            if ("Water".equals(var3)) {
               if (var5.item instanceof DrainableComboItem && var5.item.isWaterSource()) {
                  var5.water = RecipeManager.AvailableUses(var5.item);
                  this.items.add(var5);
               }
            } else if (var3.equals(var5.item.getFullType()) && var2.Test(var5.item)) {
               if (this.source.source.isDestroy()) {
                  var5.uses = 1;
                  this.items.add(var5);
               } else if (var5.item instanceof DrainableComboItem) {
                  var5.uses = RecipeManager.AvailableUses(var5.item);
                  this.items.add(var5);
               } else if (this.source.source.use > 0.0F) {
                  if (var5.item instanceof Food) {
                     var5.uses = RecipeManager.AvailableUses(var5.item);
                     this.items.add(var5);
                  }
               } else {
                  var5.uses = var5.item.getUses();
                  this.items.add(var5);
               }
            }
         }

      }

      boolean hasItems() {
         String var1 = (String)this.source.source.getItems().get(this.index);
         int var2 = 0;

         for(int var3 = 0; var3 < this.items.size(); ++var3) {
            if ("Water".equals(var1)) {
               var2 += ((RecipeManager.RMRecipeItem)this.items.get(var3)).water;
            } else {
               var2 += ((RecipeManager.RMRecipeItem)this.items.get(var3)).uses;
            }
         }

         return var2 >= this.usesNeeded;
      }

      int indexOf(InventoryItem var1) {
         for(int var2 = 0; var2 < this.items.size(); ++var2) {
            RecipeManager.RMRecipeItem var3 = (RecipeManager.RMRecipeItem)this.items.get(var2);
            if (var3.item == var1) {
               return var2;
            }
         }

         return -1;
      }

      void getAvailableItems(RecipeManager.SourceItems var1, boolean var2) {
         if (var2) {
            this.Use(var1.itemsPerSource[this.source.index]);
            var1.typePerSource[this.source.index] = this.type;
            var1.allItems.addAll(var1.itemsPerSource[this.source.index]);
         } else {
            assert this.hasItems();

            if (var1.selectedItem != null) {
               int var3 = this.indexOf(var1.selectedItem);
               if (var3 != -1) {
                  RecipeManager.RMRecipeItem var4 = (RecipeManager.RMRecipeItem)this.items.remove(var3);
                  this.items.add(0, var4);
               }
            }

            this.Use(var1.itemsPerSource[this.source.index]);
            var1.typePerSource[this.source.index] = this.type;
            var1.allItems.addAll(var1.itemsPerSource[this.source.index]);
         }
      }

      void Use(ArrayList var1) {
         String var2 = (String)this.source.source.getItems().get(this.index);
         int var3 = this.usesNeeded;

         for(int var4 = 0; var4 < this.items.size(); ++var4) {
            RecipeManager.RMRecipeItem var5 = (RecipeManager.RMRecipeItem)this.items.get(var4);
            if ("Water".equals(var2) && var5.water > 0) {
               var3 -= var5.UseWater(var3);
               var1.add(var5.item);
            } else if (this.source.source.isKeep() && var5.uses > 0) {
               var3 -= Math.min(var5.uses, var3);
               var1.add(var5.item);
            } else if (var5.uses > 0) {
               var3 -= var5.Use(var3);
               var1.add(var5.item);
            }

            if (var3 <= 0) {
               break;
            }
         }

      }

      static RecipeManager.RMRecipeItemList alloc(RecipeManager.RMRecipeSource var0, int var1) {
         return pool.isEmpty() ? (new RecipeManager.RMRecipeItemList()).init(var0, var1) : ((RecipeManager.RMRecipeItemList)pool.pop()).init(var0, var1);
      }

      static void release(RecipeManager.RMRecipeItemList var0) {
         assert !pool.contains(var0);

         pool.push(var0.reset());
      }

      static enum Type {
         NONE,
         WATER,
         DRAINABLE,
         FOOD,
         OTHER,
         DESTROY;
      }
   }

   private static class RMRecipeSource {
      RecipeManager.RMRecipe recipe;
      Recipe.Source source;
      int index;
      ArrayList itemLists = new ArrayList();
      boolean usesWater;
      static ArrayDeque pool = new ArrayDeque();

      RecipeManager.RMRecipeSource init(RecipeManager.RMRecipe var1, int var2) {
         this.recipe = var1;
         this.source = (Recipe.Source)var1.recipe.getSource().get(var2);
         this.index = var2;

         assert this.itemLists.isEmpty();

         for(int var3 = 0; var3 < this.source.getItems().size(); ++var3) {
            this.itemLists.add(RecipeManager.RMRecipeItemList.alloc(this, var3));
         }

         this.usesWater = this.source.getItems().contains("Water");
         return this;
      }

      RecipeManager.RMRecipeSource reset() {
         for(int var1 = 0; var1 < this.itemLists.size(); ++var1) {
            RecipeManager.RMRecipeItemList.release((RecipeManager.RMRecipeItemList)this.itemLists.get(var1));
         }

         this.itemLists.clear();
         return this;
      }

      void getItemsFrom(ArrayList var1, RecipeManager.RMRecipe var2) {
         for(int var3 = 0; var3 < this.itemLists.size(); ++var3) {
            RecipeManager.RMRecipeItemList var4 = (RecipeManager.RMRecipeItemList)this.itemLists.get(var3);
            var4.getItemsFrom(var1, var2);
         }

      }

      boolean hasItems() {
         for(int var1 = 0; var1 < this.itemLists.size(); ++var1) {
            RecipeManager.RMRecipeItemList var2 = (RecipeManager.RMRecipeItemList)this.itemLists.get(var1);
            if (var2.hasItems()) {
               return true;
            }
         }

         return false;
      }

      void getAvailableItems(RecipeManager.SourceItems var1, boolean var2) {
         int var3;
         if (var2) {
            for(var3 = 0; var3 < this.itemLists.size(); ++var3) {
               RecipeManager.RMRecipeItemList var6 = (RecipeManager.RMRecipeItemList)this.itemLists.get(var3);
               var6.getAvailableItems(var1, var2);
            }

         } else {
            var3 = -1;

            for(int var4 = 0; var4 < this.itemLists.size(); ++var4) {
               RecipeManager.RMRecipeItemList var5 = (RecipeManager.RMRecipeItemList)this.itemLists.get(var4);
               if (var5.hasItems()) {
                  if (var1.selectedItem != null && var5.indexOf(var1.selectedItem) != -1) {
                     var3 = var4;
                     break;
                  }

                  if (var3 == -1) {
                     var3 = var4;
                  }
               }
            }

            ((RecipeManager.RMRecipeItemList)this.itemLists.get(var3)).getAvailableItems(var1, var2);
         }
      }

      void Use(ArrayList var1) {
         assert this.hasItems();

         for(int var2 = 0; var2 < this.itemLists.size(); ++var2) {
            RecipeManager.RMRecipeItemList var3 = (RecipeManager.RMRecipeItemList)this.itemLists.get(var2);
            if (var3.hasItems()) {
               var3.Use(var1);
               return;
            }
         }

         assert false;

      }

      static RecipeManager.RMRecipeSource alloc(RecipeManager.RMRecipe var0, int var1) {
         return pool.isEmpty() ? (new RecipeManager.RMRecipeSource()).init(var0, var1) : ((RecipeManager.RMRecipeSource)pool.pop()).init(var0, var1);
      }

      static void release(RecipeManager.RMRecipeSource var0) {
         assert !pool.contains(var0);

         pool.push(var0.reset());
      }
   }

   private static class RMRecipe {
      Recipe recipe;
      ArrayList sources = new ArrayList();
      ArrayList allItems = new ArrayList();
      boolean usesWater;
      ArrayList allSourceTypes = new ArrayList();
      static ArrayDeque pool = new ArrayDeque();

      RecipeManager.RMRecipe init(Recipe var1) {
         assert this.allItems.isEmpty();

         assert this.sources.isEmpty();

         assert this.allSourceTypes.isEmpty();

         this.recipe = var1;
         this.usesWater = false;

         for(int var2 = 0; var2 < var1.getSource().size(); ++var2) {
            RecipeManager.RMRecipeSource var3 = RecipeManager.RMRecipeSource.alloc(this, var2);
            if (var3.usesWater) {
               this.usesWater = true;
            }

            this.allSourceTypes.addAll(var3.source.getItems());
            this.sources.add(var3);
         }

         return this;
      }

      RecipeManager.RMRecipe reset() {
         this.recipe = null;

         int var1;
         for(var1 = 0; var1 < this.allItems.size(); ++var1) {
            RecipeManager.RMRecipeItem.release((RecipeManager.RMRecipeItem)this.allItems.get(var1));
         }

         this.allItems.clear();

         for(var1 = 0; var1 < this.sources.size(); ++var1) {
            RecipeManager.RMRecipeSource.release((RecipeManager.RMRecipeSource)this.sources.get(var1));
         }

         this.sources.clear();
         this.allSourceTypes.clear();
         return this;
      }

      void getItemsFromContainers(IsoGameCharacter var1, ArrayList var2, InventoryItem var3) {
         int var4;
         for(var4 = 0; var4 < var2.size(); ++var4) {
            this.getItemsFromContainer(var1, (ItemContainer)var2.get(var4), var3);
         }

         for(var4 = 0; var4 < this.sources.size(); ++var4) {
            RecipeManager.RMRecipeSource var5 = (RecipeManager.RMRecipeSource)this.sources.get(var4);
            if (var5.recipe.Test(var3)) {
               var5.getItemsFrom(this.allItems, this);
            }
         }

      }

      void getItemsFromContainer(IsoGameCharacter var1, ItemContainer var2, InventoryItem var3) {
         for(int var4 = 0; var4 < var2.getItems().size(); ++var4) {
            InventoryItem var5 = (InventoryItem)var2.getItems().get(var4);
            if (var3 != null && var3 == var5 || !var1.isEquippedClothing(var5)) {
               if (this.usesWater && var5 instanceof DrainableComboItem && var5.isWaterSource()) {
                  this.allItems.add(RecipeManager.RMRecipeItem.alloc(var5));
               } else if (this.allSourceTypes.contains(var5.getFullType())) {
                  this.allItems.add(RecipeManager.RMRecipeItem.alloc(var5));
               }
            }
         }

      }

      boolean Test(InventoryItem var1) {
         if (var1 != null && this.recipe.LuaTest != null) {
            LuaReturn var2 = LuaManager.caller.protectedCall(LuaManager.thread, LuaManager.env.rawget(this.recipe.LuaTest), var1, this.recipe.getResult());
            return var2.isSuccess() && !var2.isEmpty() && var2.getFirst() instanceof Boolean && (Boolean)var2.getFirst();
         } else {
            return true;
         }
      }

      boolean hasItems() {
         for(int var1 = 0; var1 < this.sources.size(); ++var1) {
            RecipeManager.RMRecipeSource var2 = (RecipeManager.RMRecipeSource)this.sources.get(var1);
            if (!var2.hasItems()) {
               return false;
            }
         }

         return true;
      }

      void getAvailableItems(RecipeManager.SourceItems var1, boolean var2) {
         assert var2 || this.hasItems();

         for(int var3 = 0; var3 < this.sources.size(); ++var3) {
            RecipeManager.RMRecipeSource var4 = (RecipeManager.RMRecipeSource)this.sources.get(var3);

            assert var2 || var4.hasItems();

            var4.getAvailableItems(var1, var2);
         }

      }

      void Use(ArrayList var1) {
         assert this.hasItems();

         for(int var2 = 0; var2 < this.sources.size(); ++var2) {
            RecipeManager.RMRecipeSource var3 = (RecipeManager.RMRecipeSource)this.sources.get(var2);

            assert var3.hasItems();

            var3.Use(var1);
         }

      }

      static RecipeManager.RMRecipe alloc(Recipe var0) {
         return pool.isEmpty() ? (new RecipeManager.RMRecipe()).init(var0) : ((RecipeManager.RMRecipe)pool.pop()).init(var0);
      }

      static void release(RecipeManager.RMRecipe var0) {
         assert !pool.contains(var0);

         pool.push(var0.reset());
      }
   }

   private static final class SourceItems {
      InventoryItem selectedItem;
      ArrayList allItems = new ArrayList();
      ArrayList[] itemsPerSource;
      RecipeManager.RMRecipeItemList.Type[] typePerSource;

      SourceItems(Recipe var1, IsoGameCharacter var2, InventoryItem var3, ArrayList var4) {
         this.itemsPerSource = new ArrayList[var1.getSource().size()];

         for(int var5 = 0; var5 < this.itemsPerSource.length; ++var5) {
            this.itemsPerSource[var5] = new ArrayList();
         }

         this.typePerSource = new RecipeManager.RMRecipeItemList.Type[var1.getSource().size()];
         this.selectedItem = var3;
      }

      public ArrayList getItems() {
         return this.allItems;
      }
   }
}
