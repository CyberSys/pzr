package zombie.inventory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.skills.PerkFactory;
import zombie.debug.DebugLog;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.Moveable;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.network.GameClient;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.EvolvedRecipe;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.MovableRecipe;
import zombie.scripting.objects.Recipe;
import zombie.scripting.objects.ScriptModule;
import zombie.util.StringUtils;


public final class RecipeManager {
	private static final ArrayList RecipeList = new ArrayList();

	public static void Loaded() {
		ArrayList arrayList = ScriptManager.instance.getAllRecipes();
		HashSet hashSet = new HashSet();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			Recipe recipe = (Recipe)arrayList.get(int1);
			for (int int2 = 0; int2 < recipe.getSource().size(); ++int2) {
				Recipe.Source source = (Recipe.Source)recipe.getSource().get(int2);
				for (int int3 = 0; int3 < source.getItems().size(); ++int3) {
					String string = (String)source.getItems().get(int3);
					if (!"Water".equals(string) && !string.contains(".") && !string.startsWith("[")) {
						Item item = resolveItemModuleDotType(recipe, string, hashSet, "recipe source");
						if (item == null) {
							source.getItems().set(int3, "???." + string);
						} else {
							source.getItems().set(int3, item.getFullName());
						}
					}
				}
			}

			if (recipe.getResult() != null && recipe.getResult().getModule() == null) {
				Item item2 = resolveItemModuleDotType(recipe, recipe.getResult().getType(), hashSet, "recipe result");
				if (item2 == null) {
					recipe.getResult().module = "???";
				} else {
					recipe.getResult().module = item2.getModule().getName();
				}
			}
		}
	}

	private static Item resolveItemModuleDotType(Recipe recipe, String string, Set set, String string2) {
		ScriptModule scriptModule = recipe.getModule();
		Item item = scriptModule.getItem(string);
		if (item != null && !item.getObsolete()) {
			return item;
		} else {
			for (int int1 = 0; int1 < ScriptManager.instance.ModuleList.size(); ++int1) {
				ScriptModule scriptModule2 = (ScriptModule)ScriptManager.instance.ModuleList.get(int1);
				item = scriptModule2.getItem(string);
				if (item != null && !item.getObsolete()) {
					String string3 = recipe.getModule().getName();
					if (!set.contains(string3)) {
						set.add(string3);
						DebugLog.Recipe.warn("WARNING: module \"%s\" may have forgot to import module Base", string3);
					}

					return item;
				}
			}

			DebugLog.Recipe.warn("ERROR: can\'t find %s \"%s\" in recipe \"%s\"", string2, string, recipe.getOriginalname());
			return null;
		}
	}

	public static void LoadedAfterLua() {
		ArrayList arrayList = new ArrayList();
		ArrayList arrayList2 = ScriptManager.instance.getAllRecipes();
		for (int int1 = 0; int1 < arrayList2.size(); ++int1) {
			Recipe recipe = (Recipe)arrayList2.get(int1);
			LoadedAfterLua(recipe, arrayList);
		}

		arrayList.clear();
	}

	private static void LoadedAfterLua(Recipe recipe, ArrayList arrayList) {
		LoadedAfterLua(recipe, recipe.LuaCreate, "LuaCreate");
		LoadedAfterLua(recipe, recipe.LuaGiveXP, "LuaGiveXP");
		LoadedAfterLua(recipe, recipe.LuaTest, "LuaTest");
		for (int int1 = 0; int1 < recipe.getSource().size(); ++int1) {
			Recipe.Source source = (Recipe.Source)recipe.getSource().get(int1);
			LoadedAfterLua(source, arrayList);
		}
	}

	private static void LoadedAfterLua(Recipe recipe, String string, String string2) {
		if (!StringUtils.isNullOrWhitespace(string)) {
			Object object = LuaManager.getFunctionObject(string);
			if (object == null) {
				DebugLog.General.error("no such function %s = \"%s\" in recipe \"%s\"", string2, string, recipe.name);
			}
		}
	}

	private static void LoadedAfterLua(Recipe.Source source, ArrayList arrayList) {
		for (int int1 = source.getItems().size() - 1; int1 >= 0; --int1) {
			String string = (String)source.getItems().get(int1);
			if (string.startsWith("[")) {
				source.getItems().remove(int1);
				String string2 = string.substring(1, string.indexOf("]"));
				Object object = LuaManager.getFunctionObject(string2);
				if (object != null) {
					arrayList.clear();
					LuaManager.caller.protectedCallVoid(LuaManager.thread, object, (Object)arrayList);
					for (int int2 = 0; int2 < arrayList.size(); ++int2) {
						Item item = (Item)arrayList.get(int2);
						source.getItems().add(int1 + int2, item.getFullName());
					}
				}
			}
		}
	}

	public static boolean DoesWipeUseDelta(String string, String string2) {
		return true;
	}

	public static int getKnownRecipesNumber(IsoGameCharacter gameCharacter) {
		int int1 = 0;
		ArrayList arrayList = ScriptManager.instance.getAllRecipes();
		for (int int2 = 0; int2 < arrayList.size(); ++int2) {
			Recipe recipe = (Recipe)arrayList.get(int2);
			if (gameCharacter.isRecipeKnown(recipe)) {
				++int1;
			}
		}

		return int1;
	}

	public static boolean DoesUseItemUp(String string, Recipe recipe) {
		assert "Water".equals(string) || string.contains(".");
		for (int int1 = 0; int1 < recipe.Source.size(); ++int1) {
			if (((Recipe.Source)recipe.Source.get(int1)).keep) {
				ArrayList arrayList = ((Recipe.Source)recipe.Source.get(int1)).getItems();
				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					if (string.equals(arrayList.get(int2))) {
						return false;
					}
				}
			}
		}

		return true;
	}

	public static boolean IsItemDestroyed(String string, Recipe recipe) {
		assert "Water".equals(string) || string.contains(".");
		for (int int1 = 0; int1 < recipe.Source.size(); ++int1) {
			Recipe.Source source = (Recipe.Source)recipe.getSource().get(int1);
			if (source.destroy) {
				for (int int2 = 0; int2 < source.getItems().size(); ++int2) {
					if (string.equals(source.getItems().get(int2))) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static float UseAmount(String string, Recipe recipe, IsoGameCharacter gameCharacter) {
		Recipe.Source source = recipe.findSource(string);
		return source.getCount();
	}

	public static ArrayList getUniqueRecipeItems(InventoryItem inventoryItem, IsoGameCharacter gameCharacter, ArrayList arrayList) {
		RecipeList.clear();
		ArrayList arrayList2 = ScriptManager.instance.getAllRecipes();
		for (int int1 = 0; int1 < arrayList2.size(); ++int1) {
			Recipe recipe = (Recipe)arrayList2.get(int1);
			if (IsRecipeValid(recipe, gameCharacter, inventoryItem, arrayList) && (!(inventoryItem instanceof Clothing) || inventoryItem.getCondition() > 0 || !recipe.getOriginalname().equalsIgnoreCase("rip clothing"))) {
				RecipeList.add(recipe);
			}
		}

		if (inventoryItem instanceof Moveable && RecipeList.size() == 0 && ((Moveable)inventoryItem).getWorldSprite() != null) {
			if (inventoryItem.type != null && inventoryItem.type.equalsIgnoreCase(((Moveable)inventoryItem).getWorldSprite())) {
				MovableRecipe movableRecipe = new MovableRecipe();
				LuaEventManager.triggerEvent("OnDynamicMovableRecipe", ((Moveable)inventoryItem).getWorldSprite(), movableRecipe, inventoryItem, gameCharacter);
				if (movableRecipe.isValid() && IsRecipeValid(movableRecipe, gameCharacter, inventoryItem, arrayList)) {
					RecipeList.add(movableRecipe);
				}
			} else {
				DebugLog.log("RecipeManager -> Cannot create recipe for this movable item: " + inventoryItem.getFullType());
			}
		}

		return RecipeList;
	}

	public static boolean IsRecipeValid(Recipe recipe, IsoGameCharacter gameCharacter, InventoryItem inventoryItem, ArrayList arrayList) {
		if (recipe.Result == null) {
			return false;
		} else if (!gameCharacter.isRecipeKnown(recipe)) {
			return false;
		} else if (inventoryItem != null && !RecipeContainsItem(recipe, inventoryItem)) {
			return false;
		} else if (!HasAllRequiredItems(recipe, gameCharacter, inventoryItem, arrayList)) {
			return false;
		} else if (!HasRequiredSkill(recipe, gameCharacter)) {
			return false;
		} else if (!isNearItem(recipe, gameCharacter)) {
			return false;
		} else if (!hasHeat(recipe, inventoryItem, arrayList, gameCharacter)) {
			return false;
		} else {
			return CanPerform(recipe, gameCharacter, inventoryItem);
		}
	}

	private static boolean isNearItem(Recipe recipe, IsoGameCharacter gameCharacter) {
		if (recipe.getNearItem() != null && !recipe.getNearItem().equals("")) {
			for (int int1 = gameCharacter.getSquare().getX() - 2; int1 < gameCharacter.getSquare().getX() + 2; ++int1) {
				for (int int2 = gameCharacter.getSquare().getY() - 2; int2 < gameCharacter.getSquare().getY() + 2; ++int2) {
					IsoGridSquare square = gameCharacter.getCell().getGridSquare(int1, int2, gameCharacter.getSquare().getZ());
					if (square != null) {
						for (int int3 = 0; int3 < square.getObjects().size(); ++int3) {
							if (recipe.getNearItem().equals(((IsoObject)square.getObjects().get(int3)).getName())) {
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

	private static boolean CanPerform(Recipe recipe, IsoGameCharacter gameCharacter, InventoryItem inventoryItem) {
		if (StringUtils.isNullOrWhitespace(recipe.getCanPerform())) {
			return true;
		} else {
			Object object = LuaManager.getFunctionObject(recipe.getCanPerform());
			if (object == null) {
				return false;
			} else {
				Boolean Boolean1 = LuaManager.caller.protectedCallBoolean(LuaManager.thread, object, recipe, gameCharacter, inventoryItem);
				return Boolean1 == Boolean.TRUE;
			}
		}
	}

	private static boolean HasRequiredSkill(Recipe recipe, IsoGameCharacter gameCharacter) {
		if (recipe.getRequiredSkillCount() == 0) {
			return true;
		} else {
			for (int int1 = 0; int1 < recipe.getRequiredSkillCount(); ++int1) {
				Recipe.RequiredSkill requiredSkill = recipe.getRequiredSkill(int1);
				if (gameCharacter.getPerkLevel(requiredSkill.getPerk()) < requiredSkill.getLevel()) {
					return false;
				}
			}

			return true;
		}
	}

	private static boolean RecipeContainsItem(Recipe recipe, InventoryItem inventoryItem) {
		for (int int1 = 0; int1 < recipe.Source.size(); ++int1) {
			Recipe.Source source = (Recipe.Source)recipe.getSource().get(int1);
			for (int int2 = 0; int2 < source.getItems().size(); ++int2) {
				String string = (String)source.getItems().get(int2);
				if ("Water".equals(string) && inventoryItem.isWaterSource()) {
					return true;
				}

				if (string.equals(inventoryItem.getFullType())) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean HasAllRequiredItems(Recipe recipe, IsoGameCharacter gameCharacter, InventoryItem inventoryItem, ArrayList arrayList) {
		ArrayList arrayList2 = getAvailableItemsNeeded(recipe, gameCharacter, arrayList, inventoryItem, (ArrayList)null);
		return !arrayList2.isEmpty();
	}

	public static boolean hasHeat(Recipe recipe, InventoryItem inventoryItem, ArrayList arrayList, IsoGameCharacter gameCharacter) {
		if (recipe.getHeat() == 0.0F) {
			return true;
		} else {
			InventoryItem inventoryItem2 = null;
			Iterator iterator = getAvailableItemsNeeded(recipe, gameCharacter, arrayList, inventoryItem, (ArrayList)null).iterator();
			while (iterator.hasNext()) {
				InventoryItem inventoryItem3 = (InventoryItem)iterator.next();
				if (inventoryItem3 instanceof DrainableComboItem) {
					inventoryItem2 = inventoryItem3;
					break;
				}
			}

			if (inventoryItem2 != null) {
				iterator = arrayList.iterator();
				while (iterator.hasNext()) {
					ItemContainer itemContainer = (ItemContainer)iterator.next();
					Iterator iterator2 = itemContainer.getItems().iterator();
					while (iterator2.hasNext()) {
						InventoryItem inventoryItem4 = (InventoryItem)iterator2.next();
						if (inventoryItem4.getName().equals(inventoryItem2.getName())) {
							if (recipe.getHeat() < 0.0F) {
								if (inventoryItem4.getInvHeat() <= recipe.getHeat()) {
									return true;
								}
							} else if (recipe.getHeat() > 0.0F && inventoryItem4.getInvHeat() + 1.0F >= recipe.getHeat()) {
								return true;
							}
						}
					}
				}
			}

			return false;
		}
	}

	public static ArrayList getAvailableItemsAll(Recipe recipe, IsoGameCharacter gameCharacter, ArrayList arrayList, InventoryItem inventoryItem, ArrayList arrayList2) {
		return getAvailableItems(recipe, gameCharacter, arrayList, inventoryItem, arrayList2, true).allItems;
	}

	public static ArrayList getAvailableItemsNeeded(Recipe recipe, IsoGameCharacter gameCharacter, ArrayList arrayList, InventoryItem inventoryItem, ArrayList arrayList2) {
		return getAvailableItems(recipe, gameCharacter, arrayList, inventoryItem, arrayList2, false).allItems;
	}

	private static RecipeManager.SourceItems getAvailableItems(Recipe recipe, IsoGameCharacter gameCharacter, ArrayList arrayList, InventoryItem inventoryItem, ArrayList arrayList2, boolean boolean1) {
		if (inventoryItem != null && (inventoryItem.getContainer() == null || !inventoryItem.getContainer().contains(inventoryItem))) {
			DebugLog.Recipe.warn("recipe: item appears to have been used already, ignoring " + inventoryItem.getFullType());
			inventoryItem = null;
		}

		RecipeManager.SourceItems sourceItems = new RecipeManager.SourceItems(recipe, gameCharacter, inventoryItem, arrayList2);
		if (arrayList == null) {
			arrayList = new ArrayList();
			arrayList.add(gameCharacter.getInventory());
		}

		if (inventoryItem != null && !RecipeContainsItem(recipe, inventoryItem)) {
			String string = inventoryItem.getFullType();
			throw new RuntimeException("item " + string + " isn\'t used in recipe " + recipe.getOriginalname());
		} else {
			RecipeManager.RMRecipe rMRecipe = RecipeManager.RMRecipe.alloc(recipe);
			rMRecipe.getItemsFromContainers(gameCharacter, arrayList, inventoryItem);
			if (boolean1 || rMRecipe.hasItems()) {
				rMRecipe.getAvailableItems(sourceItems, boolean1);
			}

			RecipeManager.RMRecipe.release(rMRecipe);
			return sourceItems;
		}
	}

	public static ArrayList getSourceItemsAll(Recipe recipe, int int1, IsoGameCharacter gameCharacter, ArrayList arrayList, InventoryItem inventoryItem, ArrayList arrayList2) {
		if (int1 >= 0 && int1 < recipe.getSource().size()) {
			RecipeManager.SourceItems sourceItems = getAvailableItems(recipe, gameCharacter, arrayList, inventoryItem, arrayList2, true);
			return sourceItems.itemsPerSource[int1];
		} else {
			return null;
		}
	}

	public static ArrayList getSourceItemsNeeded(Recipe recipe, int int1, IsoGameCharacter gameCharacter, ArrayList arrayList, InventoryItem inventoryItem, ArrayList arrayList2) {
		if (int1 >= 0 && int1 < recipe.getSource().size()) {
			RecipeManager.SourceItems sourceItems = getAvailableItems(recipe, gameCharacter, arrayList, inventoryItem, arrayList2, false);
			return sourceItems.itemsPerSource[int1];
		} else {
			return null;
		}
	}

	public static int getNumberOfTimesRecipeCanBeDone(Recipe recipe, IsoGameCharacter gameCharacter, ArrayList arrayList, InventoryItem inventoryItem) {
		int int1 = 0;
		RecipeManager.RMRecipe rMRecipe = RecipeManager.RMRecipe.alloc(recipe);
		if (arrayList == null) {
			arrayList = new ArrayList();
			arrayList.add(gameCharacter.getInventory());
		}

		rMRecipe.getItemsFromContainers(gameCharacter, arrayList, inventoryItem);
		ArrayList arrayList2 = new ArrayList();
		for (ArrayList arrayList3 = new ArrayList(); rMRecipe.hasItems(); ++int1) {
			arrayList3.clear();
			rMRecipe.Use(arrayList3);
			if (arrayList2.containsAll(arrayList3)) {
				int1 = -1;
				break;
			}

			arrayList2.addAll(arrayList3);
			for (int int2 = 0; int2 < arrayList3.size(); ++int2) {
				InventoryItem inventoryItem2 = (InventoryItem)arrayList3.get(int2);
				if (inventoryItem2 instanceof Food && ((Food)inventoryItem2).isFrozen() && !rMRecipe.recipe.isAllowFrozenItem()) {
					--int1;
					break;
				}
			}
		}

		RecipeManager.RMRecipe.release(rMRecipe);
		return int1;
	}

	public static InventoryItem GetMovableRecipeTool(boolean boolean1, Recipe recipe, InventoryItem inventoryItem, IsoGameCharacter gameCharacter, ArrayList arrayList) {
		if (!(recipe instanceof MovableRecipe)) {
			return null;
		} else {
			MovableRecipe movableRecipe = (MovableRecipe)recipe;
			Recipe.Source source = boolean1 ? movableRecipe.getPrimaryTools() : movableRecipe.getSecondaryTools();
			if (source != null && source.getItems() != null && source.getItems().size() != 0) {
				RecipeManager.SourceItems sourceItems = getAvailableItems(recipe, gameCharacter, arrayList, inventoryItem, (ArrayList)null, false);
				if (sourceItems.allItems != null && sourceItems.allItems.size() != 0) {
					for (int int1 = 0; int1 < sourceItems.allItems.size(); ++int1) {
						InventoryItem inventoryItem2 = (InventoryItem)sourceItems.allItems.get(int1);
						for (int int2 = 0; int2 < source.getItems().size(); ++int2) {
							if (inventoryItem2.getFullType().equalsIgnoreCase((String)source.getItems().get(int2))) {
								return inventoryItem2;
							}
						}
					}

					return null;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}

	public static InventoryItem PerformMakeItem(Recipe recipe, InventoryItem inventoryItem, IsoGameCharacter gameCharacter, ArrayList arrayList) {
		boolean boolean1 = gameCharacter.getPrimaryHandItem() == inventoryItem;
		boolean boolean2 = gameCharacter.getSecondaryHandItem() == inventoryItem;
		RecipeManager.SourceItems sourceItems = getAvailableItems(recipe, gameCharacter, arrayList, inventoryItem, (ArrayList)null, false);
		ArrayList arrayList2 = sourceItems.allItems;
		if (arrayList2.isEmpty()) {
			throw new RuntimeException("getAvailableItems() didn\'t return the required number of items");
		} else {
			gameCharacter.removeFromHands(inventoryItem);
			Recipe.Result result = recipe.getResult();
			InventoryItem inventoryItem2 = InventoryItemFactory.CreateItem(result.getFullType());
			boolean boolean3 = false;
			boolean boolean4 = false;
			int int1 = -1;
			int int2 = 0;
			boolean boolean5 = false;
			boolean boolean6 = false;
			float float1 = 0.0F;
			float float2 = 0.0F;
			int int3 = 0;
			int int4 = 0;
			int int5;
			label182: for (int5 = 0; int5 < recipe.getSource().size(); ++int5) {
				Recipe.Source source = (Recipe.Source)recipe.getSource().get(int5);
				if (!source.isKeep()) {
					ArrayList arrayList3 = sourceItems.itemsPerSource[int5];
					int int6;
					int int7;
					int int8;
					InventoryItem inventoryItem3;
					switch (sourceItems.typePerSource[int5]) {
					case DRAINABLE: 
						int int9 = (int)source.getCount();
						int6 = 0;
						for (; int6 < arrayList3.size(); ++int6) {
							InventoryItem inventoryItem4 = (InventoryItem)arrayList3.get(int6);
							int8 = AvailableUses(inventoryItem4);
							if (int8 >= int9) {
								ReduceUses(inventoryItem4, (float)int9, gameCharacter);
								int9 = 0;
							} else {
								ReduceUses(inventoryItem4, (float)int8, gameCharacter);
								int9 -= int8;
							}
						}

						if (int9 > 0) {
							throw new RuntimeException("required amount of " + source.getItems() + " wasn\'t available");
						}

						break;
					
					case FOOD: 
						int6 = (int)source.use;
						int7 = 0;
						while (true) {
							if (int7 >= arrayList3.size()) {
								continue label182;
							}

							inventoryItem3 = (InventoryItem)arrayList3.get(int7);
							int int10 = AvailableUses(inventoryItem3);
							if (int10 >= int6) {
								ReduceUses(inventoryItem3, (float)int6, gameCharacter);
								int6 = 0;
							} else {
								ReduceUses(inventoryItem3, (float)int10, gameCharacter);
								int6 -= int10;
							}

							++int7;
						}

					
					case DESTROY: 
						int7 = 0;
						while (true) {
							if (int7 >= arrayList3.size()) {
								continue label182;
							}

							inventoryItem3 = (InventoryItem)arrayList3.get(int7);
							ItemUser.RemoveItem(inventoryItem3);
							++int7;
						}

					
					case OTHER: 
						int7 = 0;
						while (true) {
							if (int7 >= arrayList3.size()) {
								continue label182;
							}

							inventoryItem3 = (InventoryItem)arrayList3.get(int7);
							ItemUser.UseItem(inventoryItem3, true, false);
							++int7;
						}

					
					case WATER: 
						int7 = recipe.getWaterAmountNeeded();
						for (int8 = 0; int8 < arrayList3.size(); ++int8) {
							InventoryItem inventoryItem5 = (InventoryItem)arrayList3.get(int8);
							int int11 = AvailableUses(inventoryItem5);
							if (int11 >= int7) {
								ReduceUses(inventoryItem5, (float)int7, gameCharacter);
								int7 = 0;
							} else {
								ReduceUses(inventoryItem5, (float)int11, gameCharacter);
								int7 -= int11;
							}
						}

						if (int7 > 0) {
							throw new RuntimeException("required amount of water wasn\'t available");
						}

					
					}
				}
			}

			InventoryItem inventoryItem6;
			for (int5 = 0; int5 < arrayList2.size(); ++int5) {
				inventoryItem6 = (InventoryItem)arrayList2.get(int5);
				if (inventoryItem6 instanceof Food) {
					if (((Food)inventoryItem6).isCooked()) {
						boolean3 = true;
					}

					if (((Food)inventoryItem6).isBurnt()) {
						boolean4 = true;
					}

					int1 = ((Food)inventoryItem6).getPoisonDetectionLevel();
					int2 = ((Food)inventoryItem6).getPoisonPower();
					++int4;
					if (inventoryItem6.getAge() > (float)inventoryItem6.getOffAgeMax()) {
						boolean5 = true;
					} else if (!boolean5 && inventoryItem6.getOffAgeMax() < 1000000000) {
						if (inventoryItem6.getAge() < (float)inventoryItem6.getOffAge()) {
							float2 += 0.5F * inventoryItem6.getAge() / (float)inventoryItem6.getOffAge();
						} else {
							boolean6 = true;
							float2 += 0.5F + 0.5F * (inventoryItem6.getAge() - (float)inventoryItem6.getOffAge()) / (float)(inventoryItem6.getOffAgeMax() - inventoryItem6.getOffAge());
						}
					}
				}

				if (inventoryItem2 instanceof Food && inventoryItem6.isTaintedWater()) {
					inventoryItem2.setTaintedWater(true);
				}

				if (inventoryItem2.getScriptItem() == inventoryItem6.getScriptItem() && inventoryItem6.isFavorite()) {
					inventoryItem2.setFavorite(true);
				}

				float1 += (float)inventoryItem6.getCondition() / (float)inventoryItem6.getConditionMax();
				++int3;
			}

			float2 /= (float)int4;
			if (inventoryItem2 instanceof Food && ((Food)inventoryItem2).IsCookable) {
				((Food)inventoryItem2).setCooked(boolean3);
				((Food)inventoryItem2).setBurnt(boolean4);
				((Food)inventoryItem2).setPoisonDetectionLevel(int1);
				((Food)inventoryItem2).setPoisonPower(int2);
			}

			if ((double)inventoryItem2.getOffAgeMax() != 1.0E9) {
				if (boolean5) {
					inventoryItem2.setAge((float)inventoryItem2.getOffAgeMax());
				} else {
					if (boolean6 && float2 < 0.5F) {
						float2 = 0.5F;
					}

					if (float2 < 0.5F) {
						inventoryItem2.setAge(2.0F * float2 * (float)inventoryItem2.getOffAge());
					} else {
						inventoryItem2.setAge((float)inventoryItem2.getOffAge() + 2.0F * (float2 - 0.5F) * (float)(inventoryItem2.getOffAgeMax() - inventoryItem2.getOffAge()));
					}
				}
			}

			inventoryItem2.setCondition(Math.round((float)inventoryItem2.getConditionMax() * (float1 / (float)int3)));
			for (int5 = 0; int5 < arrayList2.size(); ++int5) {
				inventoryItem6 = (InventoryItem)arrayList2.get(int5);
				inventoryItem2.setConditionFromModData(inventoryItem6);
			}

			GivePlayerExperience(recipe, arrayList2, inventoryItem2, gameCharacter);
			if (recipe.LuaCreate != null) {
				Object object = LuaManager.getFunctionObject(recipe.LuaCreate);
				if (object != null) {
					LuaManager.caller.protectedCall(LuaManager.thread, object, arrayList2, inventoryItem2, gameCharacter, inventoryItem, boolean1, boolean2);
				}
			}

			if (!recipe.isRemoveResultItem()) {
				return inventoryItem2;
			} else {
				return null;
			}
		}
	}

	private static boolean ReduceUses(InventoryItem inventoryItem, float float1, IsoGameCharacter gameCharacter) {
		float float2;
		if (inventoryItem instanceof DrainableComboItem) {
			DrainableComboItem drainableComboItem = (DrainableComboItem)inventoryItem;
			float2 = drainableComboItem.getUseDelta() * float1;
			drainableComboItem.setUsedDelta(drainableComboItem.getUsedDelta() - float2);
			if (AvailableUses(inventoryItem) < 1) {
				drainableComboItem.setUsedDelta(0.0F);
				ItemUser.UseItem(drainableComboItem);
				return true;
			}

			if (GameClient.bClient && !inventoryItem.isInPlayerInventory()) {
				GameClient.instance.sendItemStats(inventoryItem);
			}
		}

		if (inventoryItem instanceof Food) {
			Food food = (Food)inventoryItem;
			if (food.getHungerChange() < 0.0F) {
				float2 = Math.min(-food.getHungerChange() * 100.0F, float1);
				float float3 = float2 / (-food.getHungerChange() * 100.0F);
				if (float3 < 0.0F) {
					float3 = 0.0F;
				}

				if (float3 > 1.0F) {
					float3 = 1.0F;
				}

				food.setHungChange(food.getHungChange() - food.getHungChange() * float3);
				food.setCalories(food.getCalories() - food.getCalories() * float3);
				food.setCarbohydrates(food.getCarbohydrates() - food.getCarbohydrates() * float3);
				food.setLipids(food.getLipids() - food.getLipids() * float3);
				food.setProteins(food.getProteins() - food.getProteins() * float3);
				food.setThirstChange(food.getThirstChange() - food.getThirstChange() * float3);
				food.setFluReduction(food.getFluReduction() - (int)((float)food.getFluReduction() * float3));
				food.setPainReduction(food.getPainReduction() - food.getPainReduction() * float3);
				food.setEndChange(food.getEnduranceChange() - food.getEnduranceChange() * float3);
				food.setReduceFoodSickness(food.getReduceFoodSickness() - (int)((float)food.getReduceFoodSickness() * float3));
				food.setStressChange(food.getStressChange() - food.getStressChange() * float3);
				food.setFatigueChange(food.getFatigueChange() - food.getFatigueChange() * float3);
				if ((double)food.getHungerChange() > -0.01) {
					ItemUser.UseItem(food);
					return true;
				}

				if (GameClient.bClient && !inventoryItem.isInPlayerInventory()) {
					GameClient.instance.sendItemStats(inventoryItem);
				}
			}
		}

		return false;
	}

	private static int AvailableUses(InventoryItem inventoryItem) {
		if (inventoryItem instanceof DrainableComboItem) {
			DrainableComboItem drainableComboItem = (DrainableComboItem)inventoryItem;
			return drainableComboItem.getDrainableUsesInt();
		} else if (inventoryItem instanceof Food) {
			Food food = (Food)inventoryItem;
			return (int)(-food.getHungerChange() * 100.0F);
		} else {
			return 0;
		}
	}

	private static void GivePlayerExperience(Recipe recipe, ArrayList arrayList, InventoryItem inventoryItem, IsoGameCharacter gameCharacter) {
		String string = recipe.LuaGiveXP;
		if (string == null) {
			string = "Recipe.OnGiveXP.Default";
		}

		Object object = LuaManager.getFunctionObject(string);
		if (object == null) {
			DebugLog.Recipe.warn("ERROR: Lua method \"" + string + "\" not found (in RecipeManager.GivePlayerExperience())");
		} else {
			LuaManager.caller.protectedCall(LuaManager.thread, object, recipe, arrayList, inventoryItem, gameCharacter);
		}
	}

	public static ArrayList getAllEvolvedRecipes() {
		Stack stack = ScriptManager.instance.getAllEvolvedRecipes();
		ArrayList arrayList = new ArrayList();
		for (int int1 = 0; int1 < stack.size(); ++int1) {
			arrayList.add((EvolvedRecipe)stack.get(int1));
		}

		return arrayList;
	}

	public static ArrayList getEvolvedRecipe(InventoryItem inventoryItem, IsoGameCharacter gameCharacter, ArrayList arrayList, boolean boolean1) {
		ArrayList arrayList2 = new ArrayList();
		if (inventoryItem instanceof Food && ((Food)inventoryItem).isRotten() && gameCharacter.getPerkLevel(PerkFactory.Perks.Cooking) < 7) {
			return arrayList2;
		} else {
			Stack stack = ScriptManager.instance.getAllEvolvedRecipes();
			for (int int1 = 0; int1 < stack.size(); ++int1) {
				EvolvedRecipe evolvedRecipe = (EvolvedRecipe)stack.get(int1);
				if ((inventoryItem.isCooked() && evolvedRecipe.addIngredientIfCooked || !inventoryItem.isCooked()) && (inventoryItem.getType().equals(evolvedRecipe.baseItem) || inventoryItem.getType().equals(evolvedRecipe.getResultItem())) && (!inventoryItem.getType().equals("WaterPot") || !((double)((Drainable)inventoryItem).getUsedDelta() < 0.75))) {
					if (boolean1) {
						ArrayList arrayList3 = evolvedRecipe.getItemsCanBeUse(gameCharacter, inventoryItem, arrayList);
						if (!arrayList3.isEmpty()) {
							if (inventoryItem instanceof Food && ((Food)inventoryItem).isFrozen()) {
								if (evolvedRecipe.isAllowFrozenItem()) {
									arrayList2.add(evolvedRecipe);
								}
							} else {
								arrayList2.add(evolvedRecipe);
							}
						}
					} else {
						arrayList2.add(evolvedRecipe);
					}
				}
			}

			return arrayList2;
		}
	}

	private static void DebugPrintAllRecipes() {
		ArrayList arrayList = ScriptManager.instance.getAllRecipes();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			Recipe recipe = (Recipe)arrayList.get(int1);
			if (recipe == null) {
				DebugLog.Recipe.println("Null recipe.");
			} else if (recipe.Result == null) {
				DebugLog.Recipe.println("Null result.");
			} else {
				DebugLog.Recipe.println(recipe.Result.type);
				DebugLog.Recipe.println("-----");
				for (int int2 = 0; int2 < recipe.Source.size(); ++int2) {
					if (recipe.Source.get(int2) == null) {
						DebugLog.Recipe.println("Null ingredient.");
					} else if (((Recipe.Source)recipe.Source.get(int2)).getItems().isEmpty()) {
						DebugLog.Recipe.println(((Recipe.Source)recipe.Source.get(int2)).getItems().toString());
					}
				}
			}
		}
	}

	public static Recipe getDismantleRecipeFor(String string) {
		RecipeList.clear();
		ArrayList arrayList = ScriptManager.instance.getAllRecipes();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			Recipe recipe = (Recipe)arrayList.get(int1);
			ArrayList arrayList2 = recipe.getSource();
			if (arrayList2.size() > 0) {
				for (int int2 = 0; int2 < arrayList2.size(); ++int2) {
					Recipe.Source source = (Recipe.Source)arrayList2.get(int2);
					for (int int3 = 0; int3 < source.getItems().size(); ++int3) {
						if (((String)source.getItems().get(int3)).equalsIgnoreCase(string) && recipe.name.toLowerCase().startsWith("dismantle ")) {
							return recipe;
						}
					}
				}
			}
		}

		return null;
	}

	private static final class SourceItems {
		InventoryItem selectedItem;
		final ArrayList allItems = new ArrayList();
		final ArrayList[] itemsPerSource;
		final RecipeManager.RMRecipeItemList.Type[] typePerSource;

		SourceItems(Recipe recipe, IsoGameCharacter gameCharacter, InventoryItem inventoryItem, ArrayList arrayList) {
			this.itemsPerSource = new ArrayList[recipe.getSource().size()];
			for (int int1 = 0; int1 < this.itemsPerSource.length; ++int1) {
				this.itemsPerSource[int1] = new ArrayList();
			}

			this.typePerSource = new RecipeManager.RMRecipeItemList.Type[recipe.getSource().size()];
			this.selectedItem = inventoryItem;
		}

		public ArrayList getItems() {
			return this.allItems;
		}
	}

	private static final class RMRecipe {
		Recipe recipe;
		final ArrayList sources = new ArrayList();
		final ArrayList allItems = new ArrayList();
		boolean usesWater;
		final HashSet allSourceTypes = new HashSet();
		static ArrayDeque pool = new ArrayDeque();

		RecipeManager.RMRecipe init(Recipe recipe) {
			assert this.allItems.isEmpty();
			assert this.sources.isEmpty();
			assert this.allSourceTypes.isEmpty();
			this.recipe = recipe;
			this.usesWater = false;
			for (int int1 = 0; int1 < recipe.getSource().size(); ++int1) {
				RecipeManager.RMRecipeSource rMRecipeSource = RecipeManager.RMRecipeSource.alloc(this, int1);
				if (rMRecipeSource.usesWater) {
					this.usesWater = true;
				}

				this.allSourceTypes.addAll(rMRecipeSource.source.getItems());
				this.sources.add(rMRecipeSource);
			}

			return this;
		}

		RecipeManager.RMRecipe reset() {
			this.recipe = null;
			int int1;
			for (int1 = 0; int1 < this.allItems.size(); ++int1) {
				RecipeManager.RMRecipeItem.release((RecipeManager.RMRecipeItem)this.allItems.get(int1));
			}

			this.allItems.clear();
			for (int1 = 0; int1 < this.sources.size(); ++int1) {
				RecipeManager.RMRecipeSource.release((RecipeManager.RMRecipeSource)this.sources.get(int1));
			}

			this.sources.clear();
			this.allSourceTypes.clear();
			return this;
		}

		void getItemsFromContainers(IsoGameCharacter gameCharacter, ArrayList arrayList, InventoryItem inventoryItem) {
			int int1;
			for (int1 = 0; int1 < arrayList.size(); ++int1) {
				this.getItemsFromContainer(gameCharacter, (ItemContainer)arrayList.get(int1), inventoryItem);
			}

			if (this.Test(inventoryItem)) {
				for (int1 = 0; int1 < this.sources.size(); ++int1) {
					RecipeManager.RMRecipeSource rMRecipeSource = (RecipeManager.RMRecipeSource)this.sources.get(int1);
					rMRecipeSource.getItemsFrom(this.allItems, this);
				}
			}
		}

		void getItemsFromContainer(IsoGameCharacter gameCharacter, ItemContainer itemContainer, InventoryItem inventoryItem) {
			for (int int1 = 0; int1 < itemContainer.getItems().size(); ++int1) {
				InventoryItem inventoryItem2 = (InventoryItem)itemContainer.getItems().get(int1);
				if (inventoryItem != null && inventoryItem == inventoryItem2 || !gameCharacter.isEquippedClothing(inventoryItem2) || this.isKeep(inventoryItem2.getFullType())) {
					if (this.usesWater && inventoryItem2 instanceof DrainableComboItem && inventoryItem2.isWaterSource()) {
						this.allItems.add(RecipeManager.RMRecipeItem.alloc(inventoryItem2));
					} else if (this.allSourceTypes.contains(inventoryItem2.getFullType())) {
						this.allItems.add(RecipeManager.RMRecipeItem.alloc(inventoryItem2));
					}
				}
			}
		}

		boolean Test(InventoryItem inventoryItem) {
			if (inventoryItem != null && this.recipe.LuaTest != null) {
				Object object = LuaManager.getFunctionObject(this.recipe.LuaTest);
				if (object == null) {
					return false;
				} else {
					Boolean Boolean1 = LuaManager.caller.protectedCallBoolean(LuaManager.thread, object, inventoryItem, this.recipe.getResult());
					return Boolean1 == Boolean.TRUE;
				}
			} else {
				return true;
			}
		}

		boolean hasItems() {
			for (int int1 = 0; int1 < this.sources.size(); ++int1) {
				RecipeManager.RMRecipeSource rMRecipeSource = (RecipeManager.RMRecipeSource)this.sources.get(int1);
				if (!rMRecipeSource.hasItems()) {
					return false;
				}
			}

			return true;
		}

		boolean isKeep(String string) {
			for (int int1 = 0; int1 < this.sources.size(); ++int1) {
				RecipeManager.RMRecipeSource rMRecipeSource = (RecipeManager.RMRecipeSource)this.sources.get(int1);
				if (rMRecipeSource.isKeep(string)) {
					return true;
				}
			}

			return false;
		}

		void getAvailableItems(RecipeManager.SourceItems sourceItems, boolean boolean1) {
			assert boolean1 || this.hasItems();
			for (int int1 = 0; int1 < this.sources.size(); ++int1) {
				RecipeManager.RMRecipeSource rMRecipeSource = (RecipeManager.RMRecipeSource)this.sources.get(int1);
				assert boolean1 || rMRecipeSource.hasItems();
				rMRecipeSource.getAvailableItems(sourceItems, boolean1);
			}
		}

		void Use(ArrayList arrayList) {
			assert this.hasItems();
			for (int int1 = 0; int1 < this.sources.size(); ++int1) {
				RecipeManager.RMRecipeSource rMRecipeSource = (RecipeManager.RMRecipeSource)this.sources.get(int1);
				assert rMRecipeSource.hasItems();
				rMRecipeSource.Use(arrayList);
			}
		}

		static RecipeManager.RMRecipe alloc(Recipe recipe) {
			return pool.isEmpty() ? (new RecipeManager.RMRecipe()).init(recipe) : ((RecipeManager.RMRecipe)pool.pop()).init(recipe);
		}

		static void release(RecipeManager.RMRecipe rMRecipe) {
			assert !pool.contains(rMRecipe);
			pool.push(rMRecipe.reset());
		}
	}

	private static final class RMRecipeItemList {
		RecipeManager.RMRecipeSource source;
		final ArrayList items = new ArrayList();
		int index;
		int usesNeeded;
		RecipeManager.RMRecipeItemList.Type type;
		static ArrayDeque pool = new ArrayDeque();

		private RMRecipeItemList() {
			this.type = RecipeManager.RMRecipeItemList.Type.NONE;
		}

		RecipeManager.RMRecipeItemList init(RecipeManager.RMRecipeSource rMRecipeSource, int int1) {
			assert this.items.isEmpty();
			this.source = rMRecipeSource;
			this.index = int1;
			String string = (String)rMRecipeSource.source.getItems().get(int1);
			this.usesNeeded = (int)rMRecipeSource.source.getCount();
			if ("Water".equals(string)) {
				this.type = RecipeManager.RMRecipeItemList.Type.WATER;
			} else if (rMRecipeSource.source.isDestroy()) {
				this.type = RecipeManager.RMRecipeItemList.Type.DESTROY;
			} else if (ScriptManager.instance.isDrainableItemType(string)) {
				this.type = RecipeManager.RMRecipeItemList.Type.DRAINABLE;
			} else if (rMRecipeSource.source.use > 0.0F) {
				this.usesNeeded = (int)rMRecipeSource.source.use;
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

		void getItemsFrom(ArrayList arrayList, RecipeManager.RMRecipe rMRecipe) {
			String string = (String)this.source.source.getItems().get(this.index);
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				RecipeManager.RMRecipeItem rMRecipeItem = (RecipeManager.RMRecipeItem)arrayList.get(int1);
				DrainableComboItem drainableComboItem = (DrainableComboItem)zombie.util.Type.tryCastTo(rMRecipeItem.item, DrainableComboItem.class);
				Food food = (Food)zombie.util.Type.tryCastTo(rMRecipeItem.item, Food.class);
				if ("Water".equals(string)) {
					if (rMRecipe.Test(rMRecipeItem.item) && rMRecipeItem.item instanceof DrainableComboItem && rMRecipeItem.item.isWaterSource()) {
						rMRecipeItem.water = RecipeManager.AvailableUses(rMRecipeItem.item);
						this.items.add(rMRecipeItem);
					}
				} else if (string.equals(rMRecipeItem.item.getFullType()) && (!(rMRecipe.recipe.getHeat() > 0.0F) || drainableComboItem == null || !rMRecipeItem.item.IsCookable || !(rMRecipeItem.item.getInvHeat() + 1.0F < rMRecipe.recipe.getHeat())) && (!(rMRecipe.recipe.getHeat() < 0.0F) || drainableComboItem == null || !rMRecipeItem.item.IsCookable || !(rMRecipeItem.item.getInvHeat() > rMRecipe.recipe.getHeat())) && (food == null || !(food.getFreezingTime() > 0.0F) || rMRecipe.recipe.isAllowFrozenItem()) && (!rMRecipe.recipe.noBrokenItems() || !rMRecipeItem.item.isBroken()) && (!"Clothing".equals(rMRecipeItem.item.getCategory()) || !rMRecipeItem.item.isFavorite()) && rMRecipe.Test(rMRecipeItem.item)) {
					if (this.source.source.isDestroy()) {
						rMRecipeItem.uses = 1;
						this.items.add(rMRecipeItem);
					} else if (drainableComboItem != null) {
						rMRecipeItem.uses = RecipeManager.AvailableUses(rMRecipeItem.item);
						this.items.add(rMRecipeItem);
					} else if (this.source.source.use > 0.0F) {
						if (rMRecipeItem.item instanceof Food) {
							rMRecipeItem.uses = RecipeManager.AvailableUses(rMRecipeItem.item);
							this.items.add(rMRecipeItem);
						}
					} else {
						rMRecipeItem.uses = rMRecipeItem.item.getUses();
						this.items.add(rMRecipeItem);
					}
				}
			}
		}

		boolean hasItems() {
			String string = (String)this.source.source.getItems().get(this.index);
			int int1 = 0;
			for (int int2 = 0; int2 < this.items.size(); ++int2) {
				if ("Water".equals(string)) {
					int1 += ((RecipeManager.RMRecipeItem)this.items.get(int2)).water;
				} else {
					int1 += ((RecipeManager.RMRecipeItem)this.items.get(int2)).uses;
				}
			}

			return int1 >= this.usesNeeded;
		}

		int indexOf(InventoryItem inventoryItem) {
			for (int int1 = 0; int1 < this.items.size(); ++int1) {
				RecipeManager.RMRecipeItem rMRecipeItem = (RecipeManager.RMRecipeItem)this.items.get(int1);
				if (rMRecipeItem.item == inventoryItem) {
					return int1;
				}
			}

			return -1;
		}

		void getAvailableItems(RecipeManager.SourceItems sourceItems, boolean boolean1) {
			if (boolean1) {
				this.Use(sourceItems.itemsPerSource[this.source.index]);
				sourceItems.typePerSource[this.source.index] = this.type;
				sourceItems.allItems.addAll(sourceItems.itemsPerSource[this.source.index]);
			} else {
				assert this.hasItems();
				if (sourceItems.selectedItem != null) {
					int int1 = this.indexOf(sourceItems.selectedItem);
					if (int1 != -1) {
						RecipeManager.RMRecipeItem rMRecipeItem = (RecipeManager.RMRecipeItem)this.items.remove(int1);
						this.items.add(0, rMRecipeItem);
					}
				}

				this.Use(sourceItems.itemsPerSource[this.source.index]);
				sourceItems.typePerSource[this.source.index] = this.type;
				sourceItems.allItems.addAll(sourceItems.itemsPerSource[this.source.index]);
			}
		}

		void Use(ArrayList arrayList) {
			String string = (String)this.source.source.getItems().get(this.index);
			int int1 = this.usesNeeded;
			for (int int2 = 0; int2 < this.items.size(); ++int2) {
				RecipeManager.RMRecipeItem rMRecipeItem = (RecipeManager.RMRecipeItem)this.items.get(int2);
				if ("Water".equals(string) && rMRecipeItem.water > 0) {
					int1 -= rMRecipeItem.UseWater(int1);
					arrayList.add(rMRecipeItem.item);
				} else if (this.source.source.isKeep() && rMRecipeItem.uses > 0) {
					int1 -= Math.min(rMRecipeItem.uses, int1);
					arrayList.add(rMRecipeItem.item);
				} else if (rMRecipeItem.uses > 0) {
					int1 -= rMRecipeItem.Use(int1);
					arrayList.add(rMRecipeItem.item);
				}

				if (int1 <= 0) {
					break;
				}
			}
		}

		static RecipeManager.RMRecipeItemList alloc(RecipeManager.RMRecipeSource rMRecipeSource, int int1) {
			return pool.isEmpty() ? (new RecipeManager.RMRecipeItemList()).init(rMRecipeSource, int1) : ((RecipeManager.RMRecipeItemList)pool.pop()).init(rMRecipeSource, int1);
		}

		static void release(RecipeManager.RMRecipeItemList rMRecipeItemList) {
			assert !pool.contains(rMRecipeItemList);
			pool.push(rMRecipeItemList.reset());
		}

		static enum Type {

			NONE,
			WATER,
			DRAINABLE,
			FOOD,
			OTHER,
			DESTROY;

			private static RecipeManager.RMRecipeItemList.Type[] $values() {
				return new RecipeManager.RMRecipeItemList.Type[]{NONE, WATER, DRAINABLE, FOOD, OTHER, DESTROY};
			}
		}
	}

	private static final class RMRecipeItem {
		InventoryItem item;
		int uses;
		int water;
		static ArrayDeque pool = new ArrayDeque();

		RecipeManager.RMRecipeItem init(InventoryItem inventoryItem) {
			this.item = inventoryItem;
			return this;
		}

		RecipeManager.RMRecipeItem reset() {
			this.item = null;
			this.uses = 0;
			this.water = 0;
			return this;
		}

		int Use(int int1) {
			int int2 = Math.min(this.uses, int1);
			this.uses -= int2;
			return int2;
		}

		int UseWater(int int1) {
			int int2 = Math.min(this.water, int1);
			this.water -= int2;
			return int2;
		}

		static RecipeManager.RMRecipeItem alloc(InventoryItem inventoryItem) {
			return pool.isEmpty() ? (new RecipeManager.RMRecipeItem()).init(inventoryItem) : ((RecipeManager.RMRecipeItem)pool.pop()).init(inventoryItem);
		}

		static void release(RecipeManager.RMRecipeItem rMRecipeItem) {
			assert !pool.contains(rMRecipeItem);
			pool.push(rMRecipeItem.reset());
		}
	}

	private static final class RMRecipeSource {
		RecipeManager.RMRecipe recipe;
		Recipe.Source source;
		int index;
		final ArrayList itemLists = new ArrayList();
		boolean usesWater;
		static ArrayDeque pool = new ArrayDeque();

		RecipeManager.RMRecipeSource init(RecipeManager.RMRecipe rMRecipe, int int1) {
			this.recipe = rMRecipe;
			this.source = (Recipe.Source)rMRecipe.recipe.getSource().get(int1);
			this.index = int1;
			assert this.itemLists.isEmpty();
			for (int int2 = 0; int2 < this.source.getItems().size(); ++int2) {
				this.itemLists.add(RecipeManager.RMRecipeItemList.alloc(this, int2));
			}

			this.usesWater = this.source.getItems().contains("Water");
			return this;
		}

		RecipeManager.RMRecipeSource reset() {
			for (int int1 = 0; int1 < this.itemLists.size(); ++int1) {
				RecipeManager.RMRecipeItemList.release((RecipeManager.RMRecipeItemList)this.itemLists.get(int1));
			}

			this.itemLists.clear();
			return this;
		}

		void getItemsFrom(ArrayList arrayList, RecipeManager.RMRecipe rMRecipe) {
			for (int int1 = 0; int1 < this.itemLists.size(); ++int1) {
				RecipeManager.RMRecipeItemList rMRecipeItemList = (RecipeManager.RMRecipeItemList)this.itemLists.get(int1);
				rMRecipeItemList.getItemsFrom(arrayList, rMRecipe);
			}
		}

		boolean hasItems() {
			for (int int1 = 0; int1 < this.itemLists.size(); ++int1) {
				RecipeManager.RMRecipeItemList rMRecipeItemList = (RecipeManager.RMRecipeItemList)this.itemLists.get(int1);
				if (rMRecipeItemList.hasItems()) {
					return true;
				}
			}

			return false;
		}

		boolean isKeep(String string) {
			return this.source.getItems().contains(string) ? this.source.keep : false;
		}

		void getAvailableItems(RecipeManager.SourceItems sourceItems, boolean boolean1) {
			int int1;
			if (boolean1) {
				for (int1 = 0; int1 < this.itemLists.size(); ++int1) {
					RecipeManager.RMRecipeItemList rMRecipeItemList = (RecipeManager.RMRecipeItemList)this.itemLists.get(int1);
					rMRecipeItemList.getAvailableItems(sourceItems, boolean1);
				}
			} else {
				int1 = -1;
				for (int int2 = 0; int2 < this.itemLists.size(); ++int2) {
					RecipeManager.RMRecipeItemList rMRecipeItemList2 = (RecipeManager.RMRecipeItemList)this.itemLists.get(int2);
					if (rMRecipeItemList2.hasItems()) {
						if (sourceItems.selectedItem != null && rMRecipeItemList2.indexOf(sourceItems.selectedItem) != -1) {
							int1 = int2;
							break;
						}

						if (int1 == -1) {
							int1 = int2;
						}
					}
				}

				if (int1 != -1) {
					((RecipeManager.RMRecipeItemList)this.itemLists.get(int1)).getAvailableItems(sourceItems, boolean1);
				}
			}
		}

		void Use(ArrayList arrayList) {
			assert this.hasItems();
			for (int int1 = 0; int1 < this.itemLists.size(); ++int1) {
				RecipeManager.RMRecipeItemList rMRecipeItemList = (RecipeManager.RMRecipeItemList)this.itemLists.get(int1);
				if (rMRecipeItemList.hasItems()) {
					rMRecipeItemList.Use(arrayList);
					return;
				}
			}

			assert false;
		}

		static RecipeManager.RMRecipeSource alloc(RecipeManager.RMRecipe rMRecipe, int int1) {
			return pool.isEmpty() ? (new RecipeManager.RMRecipeSource()).init(rMRecipe, int1) : ((RecipeManager.RMRecipeSource)pool.pop()).init(rMRecipe, int1);
		}

		static void release(RecipeManager.RMRecipeSource rMRecipeSource) {
			assert !pool.contains(rMRecipeSource);
			pool.push(rMRecipeSource.reset());
		}
	}
}
