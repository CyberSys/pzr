package zombie.scripting.objects;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.characters.skills.PerkFactory;
import zombie.core.Translator;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.scripting.ScriptManager;
import zombie.util.StringUtils;


public final class EvolvedRecipe extends BaseScriptObject {
	private static final DecimalFormat DECIMAL_FORMAT;
	public String name = null;
	public String DisplayName = null;
	private String originalname;
	public int maxItems = 0;
	public final Map itemsList = new HashMap();
	public String resultItem = null;
	public String baseItem = null;
	public boolean cookable = false;
	public boolean addIngredientIfCooked = false;
	public boolean canAddSpicesEmpty = false;
	public String addIngredientSound = null;
	public boolean hidden = false;
	public boolean allowFrozenItem = false;

	public EvolvedRecipe(String string) {
		this.name = string;
	}

	public void Load(String string, String[] stringArray) {
		this.DisplayName = Translator.getRecipeName(string);
		this.originalname = string;
		for (int int1 = 0; int1 < stringArray.length; ++int1) {
			if (!stringArray[int1].trim().isEmpty() && stringArray[int1].contains(":")) {
				String[] stringArray2 = stringArray[int1].split(":");
				String string2 = stringArray2[0].trim();
				String string3 = stringArray2[1].trim();
				if (string2.equals("BaseItem")) {
					this.baseItem = string3;
				} else if (string2.equals("Name")) {
					this.DisplayName = Translator.getRecipeName(string3);
					this.originalname = string3;
				} else if (string2.equals("ResultItem")) {
					this.resultItem = string3;
					if (!string3.contains(".")) {
						this.resultItem = string3;
					}
				} else if (string2.equals("Cookable")) {
					this.cookable = true;
				} else if (string2.equals("MaxItems")) {
					this.maxItems = Integer.parseInt(string3);
				} else if (string2.equals("AddIngredientIfCooked")) {
					this.addIngredientIfCooked = Boolean.parseBoolean(string3);
				} else if (string2.equals("AddIngredientSound")) {
					this.addIngredientSound = StringUtils.discardNullOrWhitespace(string3);
				} else if (string2.equals("CanAddSpicesEmpty")) {
					this.canAddSpicesEmpty = Boolean.parseBoolean(string3);
				} else if (string2.equals("IsHidden")) {
					this.hidden = Boolean.parseBoolean(string3);
				} else if (string2.equals("AllowFrozenItem")) {
					this.allowFrozenItem = Boolean.parseBoolean(string3);
				}
			}
		}
	}

	public boolean needToBeCooked(InventoryItem inventoryItem) {
		ItemRecipe itemRecipe = this.getItemRecipe(inventoryItem);
		if (itemRecipe == null) {
			return true;
		} else {
			return itemRecipe.cooked == inventoryItem.isCooked() || itemRecipe.cooked == inventoryItem.isBurnt() || !itemRecipe.cooked;
		}
	}

	public ArrayList getItemsCanBeUse(IsoGameCharacter gameCharacter, InventoryItem inventoryItem, ArrayList arrayList) {
		int int1 = gameCharacter.getPerkLevel(PerkFactory.Perks.Cooking);
		if (arrayList == null) {
			arrayList = new ArrayList();
		}

		ArrayList arrayList2 = new ArrayList();
		Iterator iterator = this.itemsList.keySet().iterator();
		if (!arrayList.contains(gameCharacter.getInventory())) {
			arrayList.add(gameCharacter.getInventory());
		}

		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			Iterator iterator2 = arrayList.iterator();
			while (iterator2.hasNext()) {
				ItemContainer itemContainer = (ItemContainer)iterator2.next();
				this.checkItemCanBeUse(itemContainer, string, inventoryItem, int1, arrayList2);
			}
		}

		if (inventoryItem.haveExtraItems() && inventoryItem.getExtraItems().size() >= 3) {
			for (int int2 = 0; int2 < arrayList.size(); ++int2) {
				ItemContainer itemContainer2 = (ItemContainer)arrayList.get(int2);
				for (int int3 = 0; int3 < itemContainer2.getItems().size(); ++int3) {
					InventoryItem inventoryItem2 = (InventoryItem)itemContainer2.getItems().get(int3);
					if (inventoryItem2 instanceof Food && ((Food)inventoryItem2).getPoisonLevelForRecipe() != null && gameCharacter.isKnownPoison(inventoryItem2) && !arrayList2.contains(inventoryItem2)) {
						arrayList2.add(inventoryItem2);
					}
				}
			}
		}

		return arrayList2;
	}

	private void checkItemCanBeUse(ItemContainer itemContainer, String string, InventoryItem inventoryItem, int int1, ArrayList arrayList) {
		ArrayList arrayList2 = itemContainer.getItemsFromType(string);
		for (int int2 = 0; int2 < arrayList2.size(); ++int2) {
			InventoryItem inventoryItem2 = (InventoryItem)arrayList2.get(int2);
			boolean boolean1 = false;
			if (inventoryItem2 instanceof Food && ((ItemRecipe)this.itemsList.get(string)).use != -1) {
				Food food = (Food)inventoryItem2;
				if (food.isSpice()) {
					if (this.isResultItem(inventoryItem)) {
						boolean1 = !this.isSpiceAdded(inventoryItem, food);
					} else if (this.canAddSpicesEmpty) {
						boolean1 = true;
					}

					if (food.isRotten() && int1 < 7) {
						boolean1 = false;
					}
				} else if ((!inventoryItem.haveExtraItems() || inventoryItem.extraItems.size() < this.maxItems) && (!food.isRotten() || int1 >= 7)) {
					boolean1 = true;
				}

				if (food.isFrozen() && !this.allowFrozenItem) {
					boolean1 = false;
				}
			} else {
				boolean1 = true;
			}

			this.getItemRecipe(inventoryItem2);
			if (boolean1) {
				arrayList.add(inventoryItem2);
			}
		}
	}

	public InventoryItem addItem(InventoryItem inventoryItem, InventoryItem inventoryItem2, IsoGameCharacter gameCharacter) {
		int int1 = gameCharacter.getPerkLevel(PerkFactory.Perks.Cooking);
		if (!this.isResultItem(inventoryItem)) {
			InventoryItem inventoryItem3 = inventoryItem instanceof Food ? inventoryItem : null;
			InventoryItem inventoryItem4 = InventoryItemFactory.CreateItem(this.resultItem);
			if (inventoryItem4 != null) {
				if (inventoryItem instanceof HandWeapon) {
					inventoryItem4.getModData().rawset("condition:" + inventoryItem.getType(), (double)inventoryItem.getCondition() / (double)inventoryItem.getConditionMax());
				}

				gameCharacter.getInventory().Remove(inventoryItem);
				gameCharacter.getInventory().AddItem(inventoryItem4);
				InventoryItem inventoryItem5 = inventoryItem;
				inventoryItem = inventoryItem4;
				if (inventoryItem4 instanceof Food) {
					((Food)inventoryItem4).setCalories(0.0F);
					((Food)inventoryItem4).setCarbohydrates(0.0F);
					((Food)inventoryItem4).setProteins(0.0F);
					((Food)inventoryItem4).setLipids(0.0F);
					if (inventoryItem2 instanceof Food && ((Food)inventoryItem2).getPoisonLevelForRecipe() != null) {
						this.addPoison(inventoryItem2, inventoryItem4, gameCharacter);
					}

					((Food)inventoryItem4).setIsCookable(this.cookable);
					if (inventoryItem3 != null) {
						((Food)inventoryItem4).setHungChange(((Food)inventoryItem3).getHungChange());
						((Food)inventoryItem4).setBaseHunger(((Food)inventoryItem3).getBaseHunger());
					} else {
						((Food)inventoryItem4).setHungChange(0.0F);
						((Food)inventoryItem4).setBaseHunger(0.0F);
					}

					if (inventoryItem5.isTaintedWater()) {
						inventoryItem4.setTaintedWater(true);
					}

					if (inventoryItem5 instanceof Food && inventoryItem5.getOffAgeMax() != 1000000000 && inventoryItem4.getOffAgeMax() != 1000000000) {
						float float1 = inventoryItem5.getAge() / (float)inventoryItem5.getOffAgeMax();
						inventoryItem4.setAge((float)inventoryItem4.getOffAgeMax() * float1);
					}

					if (inventoryItem3 instanceof Food) {
						((Food)inventoryItem4).setCalories(((Food)inventoryItem3).getCalories());
						((Food)inventoryItem4).setProteins(((Food)inventoryItem3).getProteins());
						((Food)inventoryItem4).setLipids(((Food)inventoryItem3).getLipids());
						((Food)inventoryItem4).setCarbohydrates(((Food)inventoryItem3).getCarbohydrates());
						((Food)inventoryItem4).setThirstChange(((Food)inventoryItem3).getThirstChange());
					}
				}

				inventoryItem4.setUnhappyChange(0.0F);
				inventoryItem4.setBoredomChange(0.0F);
			}
		}

		if (this.itemsList.get(inventoryItem2.getType()) != null && ((ItemRecipe)this.itemsList.get(inventoryItem2.getType())).use > -1) {
			if (inventoryItem2 instanceof Food) {
				float float2 = (float)((ItemRecipe)this.itemsList.get(inventoryItem2.getType())).use / 100.0F;
				Food food = (Food)inventoryItem2;
				if (food.isSpice() && inventoryItem instanceof Food) {
					this.useSpice(food, (Food)inventoryItem, float2, int1);
					return inventoryItem;
				}

				boolean boolean1 = false;
				DecimalFormat decimalFormat;
				if (food.isRotten()) {
					decimalFormat = DECIMAL_FORMAT;
					decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
					if (int1 != 7 && int1 != 8) {
						if (int1 == 9 || int1 == 10) {
							float2 = Float.parseFloat(decimalFormat.format((double)Math.abs(food.getBaseHunger() - (food.getBaseHunger() - 0.1F * food.getBaseHunger()))).replace(",", "."));
						}
					} else {
						float2 = Float.parseFloat(decimalFormat.format((double)Math.abs(food.getBaseHunger() - (food.getBaseHunger() - 0.05F * food.getBaseHunger()))).replace(",", "."));
					}

					boolean1 = true;
				}

				if (Math.abs(food.getHungerChange()) < float2) {
					decimalFormat = DECIMAL_FORMAT;
					decimalFormat.setRoundingMode(RoundingMode.DOWN);
					float2 = Math.abs(Float.parseFloat(decimalFormat.format((double)food.getHungerChange()).replace(",", ".")));
					boolean1 = true;
				}

				if (inventoryItem instanceof Food) {
					Food food2 = (Food)inventoryItem;
					if (inventoryItem2 instanceof Food && ((Food)inventoryItem2).getPoisonLevelForRecipe() != null) {
						this.addPoison(inventoryItem2, inventoryItem, gameCharacter);
					}

					food2.setHungChange(food2.getHungerChange() - float2);
					food2.setBaseHunger(food2.getBaseHunger() - float2);
					if (food.isbDangerousUncooked() && !food.isCooked()) {
						food2.setbDangerousUncooked(true);
					}

					int int2 = 0;
					if (inventoryItem.extraItems != null) {
						for (int int3 = 0; int3 < inventoryItem.extraItems.size(); ++int3) {
							if (((String)inventoryItem.extraItems.get(int3)).equals(inventoryItem2.getFullType())) {
								++int2;
							}
						}
					}

					if (inventoryItem.extraItems != null && inventoryItem.extraItems.size() - 2 > int1) {
						int2 += inventoryItem.extraItems.size() - 2 - int1 * 3;
					}

					float float3 = float2 - (float)(3 * int1) / 100.0F * float2;
					float float4 = Math.abs(float3 / food.getHungChange());
					if (float4 > 1.0F) {
						float4 = 1.0F;
					}

					inventoryItem.setUnhappyChange(inventoryItem.getUnhappyChange() - (float)(5 - int2 * 5));
					if (inventoryItem.getUnhappyChange() > 25.0F) {
						inventoryItem.setUnhappyChange(25.0F);
					}

					float float5 = (float)int1 / 15.0F + 1.0F;
					food2.setCalories(food2.getCalories() + food.getCalories() * float5 * float4);
					food2.setProteins(food2.getProteins() + food.getProteins() * float5 * float4);
					food2.setCarbohydrates(food2.getCarbohydrates() + food.getCarbohydrates() * float5 * float4);
					food2.setLipids(food2.getLipids() + food.getLipids() * float5 * float4);
					food2.setThirstChange(food2.getThirstChange() + food.getThirstChange() * float5 * float4);
					if (food.isCooked()) {
						float3 = (float)((double)float3 / 1.3);
					}

					food.setHungChange(food.getHungChange() + float3);
					food.setBaseHunger(food.getBaseHunger() + float3);
					food.setCalories(food.getCalories() - food.getCalories() * float4);
					food.setProteins(food.getProteins() - food.getProteins() * float4);
					food.setCarbohydrates(food.getCarbohydrates() - food.getCarbohydrates() * float4);
					food.setLipids(food.getLipids() - food.getLipids() * float4);
					if ((double)food.getHungerChange() >= -0.02 || boolean1) {
						inventoryItem2.Use();
					}

					if (food.getFatigueChange() < 0.0F) {
						inventoryItem.setFatigueChange(food.getFatigueChange() * float4);
						food.setFatigueChange(food.getFatigueChange() - food.getFatigueChange() * float4);
					}
				}
			} else {
				inventoryItem2.Use();
			}

			inventoryItem.addExtraItem(inventoryItem2.getFullType());
		} else if (inventoryItem2 instanceof Food && ((Food)inventoryItem2).getPoisonLevelForRecipe() != null) {
			this.addPoison(inventoryItem2, inventoryItem, gameCharacter);
		}

		this.checkUniqueRecipe(inventoryItem);
		gameCharacter.getXp().AddXP(PerkFactory.Perks.Cooking, 3.0F);
		return inventoryItem;
	}

	private void checkUniqueRecipe(InventoryItem inventoryItem) {
		if (inventoryItem instanceof Food) {
			Food food = (Food)inventoryItem;
			Stack stack = ScriptManager.instance.getAllUniqueRecipes();
			for (int int1 = 0; int1 < stack.size(); ++int1) {
				ArrayList arrayList = new ArrayList();
				UniqueRecipe uniqueRecipe = (UniqueRecipe)stack.get(int1);
				if (uniqueRecipe.getBaseRecipe().equals(inventoryItem.getType())) {
					boolean boolean1 = true;
					for (int int2 = 0; int2 < uniqueRecipe.getItems().size(); ++int2) {
						boolean boolean2 = false;
						for (int int3 = 0; int3 < food.getExtraItems().size(); ++int3) {
							if (!arrayList.contains(int3) && ((String)food.getExtraItems().get(int3)).equals(uniqueRecipe.getItems().get(int2))) {
								boolean2 = true;
								arrayList.add(int3);
								break;
							}
						}

						if (!boolean2) {
							boolean1 = false;
							break;
						}
					}

					if (food.getExtraItems().size() == uniqueRecipe.getItems().size() && boolean1) {
						food.setName(uniqueRecipe.getName());
						food.setBaseHunger(food.getBaseHunger() - (float)uniqueRecipe.getHungerBonus() / 100.0F);
						food.setHungChange(food.getBaseHunger());
						food.setBoredomChange(food.getBoredomChange() - (float)uniqueRecipe.getBoredomBonus());
						food.setUnhappyChange(food.getUnhappyChange() - (float)uniqueRecipe.getHapinessBonus());
						food.setCustomName(true);
					}
				}
			}
		}
	}

	private void addPoison(InventoryItem inventoryItem, InventoryItem inventoryItem2, IsoGameCharacter gameCharacter) {
		Food food = (Food)inventoryItem;
		if (inventoryItem2 instanceof Food) {
			Food food2 = (Food)inventoryItem2;
			int int1 = food.getPoisonLevelForRecipe() - gameCharacter.getPerkLevel(PerkFactory.Perks.Cooking);
			if (int1 < 1) {
				int1 = 1;
			}

			Float Float1 = 0.0F;
			float float1;
			if (food.getThirstChange() <= -0.01F) {
				float1 = (float)food.getUseForPoison() / 100.0F;
				if (Math.abs(food.getThirstChange()) < float1) {
					float1 = Math.abs(food.getThirstChange());
				}

				Float1 = Math.abs(float1 / food.getThirstChange());
				Float1 = new Float((double)Math.round(Float1.doubleValue() * 100.0) / 100.0);
				food.setThirstChange(food.getThirstChange() + float1);
				if ((double)food.getThirstChange() > -0.01) {
					food.Use();
				}
			} else if (food.getBaseHunger() <= -0.01F) {
				float1 = (float)food.getUseForPoison() / 100.0F;
				if (Math.abs(food.getBaseHunger()) < float1) {
					float1 = Math.abs(food.getThirstChange());
				}

				Float1 = Math.abs(float1 / food.getBaseHunger());
				Float1 = new Float((double)Math.round(Float1.doubleValue() * 100.0) / 100.0);
			}

			if (food2.getPoisonDetectionLevel() == -1) {
				food2.setPoisonDetectionLevel(0);
			}

			food2.setPoisonDetectionLevel(food2.getPoisonDetectionLevel() + int1);
			if (food2.getPoisonDetectionLevel() > 10) {
				food2.setPoisonDetectionLevel(10);
			}

			int int2 = (new Float(Float1 * ((float)food.getPoisonPower() / 100.0F) * 100.0F)).intValue();
			food2.setPoisonPower(food2.getPoisonPower() + int2);
			food.setPoisonPower(food.getPoisonPower() - int2);
		}
	}

	private void useSpice(Food food, Food food2, float float1, int int1) {
		if (!this.isSpiceAdded(food2, food)) {
			if (food2.spices == null) {
				food2.spices = new ArrayList();
			}

			food2.spices.add(food.getFullType());
			float float2 = float1;
			if (food.isRotten()) {
				DecimalFormat decimalFormat = DECIMAL_FORMAT;
				decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
				if (int1 != 7 && int1 != 8) {
					if (int1 == 9 || int1 == 10) {
						float1 = Float.parseFloat(decimalFormat.format((double)Math.abs(food.getBaseHunger() - (food.getBaseHunger() - 0.1F * food.getBaseHunger()))).replace(",", "."));
					}
				} else {
					float1 = Float.parseFloat(decimalFormat.format((double)Math.abs(food.getBaseHunger() - (food.getBaseHunger() - 0.05F * food.getBaseHunger()))).replace(",", "."));
				}
			}

			float float3 = Math.abs(float1 / food.getHungChange());
			if (float3 > 1.0F) {
				float3 = 1.0F;
			}

			float float4 = (float)int1 / 15.0F + 1.0F;
			food2.setUnhappyChange(food2.getUnhappyChange() - float1 * 200.0F);
			food2.setBoredomChange(food2.getBoredomChange() - float1 * 200.0F);
			food2.setCalories(food2.getCalories() + food.getCalories() * float4 * float3);
			food2.setProteins(food2.getProteins() + food.getProteins() * float4 * float3);
			food2.setCarbohydrates(food2.getCarbohydrates() + food.getCarbohydrates() * float4 * float3);
			food2.setLipids(food2.getLipids() + food.getLipids() * float4 * float3);
			float3 = Math.abs(float2 / food.getHungChange());
			if (float3 > 1.0F) {
				float3 = 1.0F;
			}

			food.setCalories(food.getCalories() - food.getCalories() * float3);
			food.setProteins(food.getProteins() - food.getProteins() * float3);
			food.setCarbohydrates(food.getCarbohydrates() - food.getCarbohydrates() * float3);
			food.setLipids(food.getLipids() - food.getLipids() * float3);
			food.setHungChange(food.getHungChange() + float2);
			if ((double)food.getHungerChange() > -0.01) {
				food.Use();
			}
		}
	}

	public ItemRecipe getItemRecipe(InventoryItem inventoryItem) {
		return (ItemRecipe)this.itemsList.get(inventoryItem.getType());
	}

	public String getName() {
		return this.DisplayName;
	}

	public String getOriginalname() {
		return this.originalname;
	}

	public String getUntranslatedName() {
		return this.name;
	}

	public String getBaseItem() {
		return this.baseItem;
	}

	public Map getItemsList() {
		return this.itemsList;
	}

	public ArrayList getPossibleItems() {
		ArrayList arrayList = new ArrayList();
		Iterator iterator = this.itemsList.values().iterator();
		while (iterator.hasNext()) {
			ItemRecipe itemRecipe = (ItemRecipe)iterator.next();
			arrayList.add(itemRecipe);
		}

		return arrayList;
	}

	public String getResultItem() {
		return !this.resultItem.contains(".") ? this.resultItem : this.resultItem.split("\\.")[1];
	}

	public String getFullResultItem() {
		return this.resultItem;
	}

	public boolean isCookable() {
		return this.cookable;
	}

	public int getMaxItems() {
		return this.maxItems;
	}

	public boolean isResultItem(InventoryItem inventoryItem) {
		return inventoryItem == null ? false : this.getResultItem().equals(inventoryItem.getType());
	}

	public boolean isSpiceAdded(InventoryItem inventoryItem, InventoryItem inventoryItem2) {
		if (!this.isResultItem(inventoryItem)) {
			return false;
		} else if (inventoryItem instanceof Food && inventoryItem2 instanceof Food) {
			if (!((Food)inventoryItem2).isSpice()) {
				return false;
			} else {
				ArrayList arrayList = ((Food)inventoryItem).getSpices();
				return arrayList == null ? false : arrayList.contains(inventoryItem2.getFullType());
			}
		} else {
			return false;
		}
	}

	public String getAddIngredientSound() {
		return this.addIngredientSound;
	}

	public void setIsHidden(boolean boolean1) {
		this.hidden = boolean1;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public boolean isAllowFrozenItem() {
		return this.allowFrozenItem;
	}

	public void setAllowFrozenItem(boolean boolean1) {
		this.allowFrozenItem = boolean1;
	}

	static  {
		DECIMAL_FORMAT = (DecimalFormat)NumberFormat.getInstance(Locale.US);
		DECIMAL_FORMAT.applyPattern("#.##");
	}
}
