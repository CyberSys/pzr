package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import zombie.characters.skills.PerkFactory;
import zombie.core.Translator;
import zombie.inventory.InventoryItem;


public class Recipe extends BaseScriptObject {
	private boolean canBeDoneFromFloor = false;
	public float TimeToMake = 0.0F;
	public String Sound;
	public ArrayList Source = new ArrayList();
	public Recipe.Result Result = null;
	public String LuaTest = null;
	public String LuaCreate = null;
	public String LuaGrab = null;
	public String name = "recipe";
	private String originalname;
	private String nearItem;
	public Map skillRequired = null;
	public String LuaGiveXP;
	private boolean needToBeLearn = false;
	private String category = null;
	private boolean removeResultItem = false;
	private float heat = 0.0F;
	private boolean noBrokenItems = false;

	public boolean isCanBeDoneFromFloor() {
		return this.canBeDoneFromFloor;
	}

	public void setCanBeDoneFromFloor(boolean boolean1) {
		this.canBeDoneFromFloor = boolean1;
	}

	public Recipe() {
		this.setOriginalname("recipe");
	}

	public int FindIndexOf(InventoryItem inventoryItem) {
		return -1;
	}

	public ArrayList getSource() {
		return this.Source;
	}

	public int getNumberOfNeededItem() {
		int int1 = 0;
		for (int int2 = 0; int2 < this.getSource().size(); ++int2) {
			Recipe.Source source = (Recipe.Source)this.getSource().get(int2);
			if (!source.getItems().isEmpty()) {
				int1 = (int)((float)int1 + source.getCount());
			}
		}

		return int1;
	}

	public float getTimeToMake() {
		return this.TimeToMake;
	}

	public String getName() {
		return this.name;
	}

	public void Load(String string, String[] stringArray) {
		this.name = Translator.getRecipeName(string);
		this.originalname = string;
		boolean boolean1 = false;
		for (int int1 = 0; int1 < stringArray.length; ++int1) {
			if (!stringArray[int1].trim().isEmpty()) {
				if (stringArray[int1].contains(":")) {
					String[] stringArray2 = stringArray[int1].split(":");
					String string2 = stringArray2[0].trim();
					String string3 = stringArray2[1].trim();
					if (string2.equals("Override")) {
						boolean1 = string3.trim().equalsIgnoreCase("true");
					}

					if (string2.equals("Time")) {
						this.TimeToMake = Float.parseFloat(string3);
					}

					if (string2.equals("Sound")) {
						this.Sound = string3.trim();
					}

					if (string2.equals("Result")) {
						this.DoResult(string3);
					}

					if (string2.equals("OnTest")) {
						this.LuaTest = string3;
					}

					if (string2.equals("OnCreate")) {
						this.LuaCreate = string3;
					}

					if (string2.equals("OnGrab")) {
						this.LuaGrab = string3;
					}

					if (string2.toLowerCase().equals("needtobelearn")) {
						this.setNeedToBeLearn(string3.trim().equalsIgnoreCase("true"));
					}

					if (string2.toLowerCase().equals("category")) {
						this.setCategory(string3.trim());
					}

					if (string2.equals("RemoveResultItem")) {
						this.removeResultItem = string3.trim().equalsIgnoreCase("true");
					}

					if (string2.equals("CanBeDoneFromFloor")) {
						this.setCanBeDoneFromFloor(string3.trim().equalsIgnoreCase("true"));
					}

					if (string2.equals("NearItem")) {
						this.setNearItem(string3.trim());
					}

					if (string2.equals("SkillRequired")) {
						this.skillRequired = new HashMap();
						String[] stringArray3 = string3.split(";");
						for (int int2 = 0; int2 < stringArray3.length; ++int2) {
							String[] stringArray4 = stringArray3[int2].split("=");
							this.skillRequired.put(stringArray4[0], Integer.parseInt(stringArray4[1]));
						}
					}

					if (string2.equals("OnGiveXP")) {
						this.LuaGiveXP = string3;
					}

					if (string2.equals("Obsolete") && string3.trim().toLowerCase().equals("true")) {
						this.module.RecipeMap.remove(this);
						this.module.RecipesWithDotInName.remove(this);
						return;
					}

					if (string2.equals("Heat")) {
						this.heat = Float.parseFloat(string3);
					}

					if (string2.equals("NoBrokenItems")) {
						this.noBrokenItems = string3.trim().equalsIgnoreCase("true");
					}
				} else {
					this.DoSource(stringArray[int1].trim());
				}
			}
		}

		if (boolean1) {
			Recipe recipe = this.module.getRecipe(string);
			if (recipe != null && recipe != this) {
				this.module.RecipeMap.remove(recipe);
				this.module.RecipesWithDotInName.remove(string);
			}
		}
	}

	private void DoSource(String string) {
		Recipe.Source source = new Recipe.Source();
		if (string.contains("=")) {
			source.count = new Float(string.split("=")[1].trim());
			string = string.split("=")[0].trim();
		}

		if (string.indexOf("keep") == 0) {
			string = string.replace("keep ", "");
			source.keep = true;
		}

		if (string.contains(";")) {
			String[] stringArray = string.split(";");
			string = stringArray[0];
			source.use = Float.parseFloat(stringArray[1]);
		}

		if (string.indexOf("destroy") == 0) {
			string = string.replace("destroy ", "");
			source.destroy = true;
		}

		if (string.equals("null")) {
			source.getItems().clear();
		} else if (string.contains("/")) {
			string = string.replaceFirst("keep ", "").trim();
			source.getItems().addAll(Arrays.asList(string.split("/")));
		} else {
			source.getItems().add(string);
		}

		if (!string.isEmpty()) {
			this.Source.add(source);
		}
	}

	private void DoResult(String string) {
		Recipe.Result result = new Recipe.Result();
		String[] stringArray;
		if (string.contains("=")) {
			stringArray = string.split("=");
			string = stringArray[0].trim();
			result.count = Integer.parseInt(stringArray[1].trim());
		}

		if (string.contains(";")) {
			stringArray = string.split(";");
			string = stringArray[0].trim();
			result.drainableCount = Integer.parseInt(stringArray[1].trim());
		}

		if (string.contains(".")) {
			result.type = string.split("\\.")[1];
			result.module = string.split("\\.")[0];
		} else {
			result.type = string;
		}

		this.Result = result;
	}

	public Recipe.Result getResult() {
		return this.Result;
	}

	public String getSound() {
		return this.Sound;
	}

	public String getOriginalname() {
		return this.originalname;
	}

	public void setOriginalname(String string) {
		this.originalname = string;
	}

	public boolean needToBeLearn() {
		return this.needToBeLearn;
	}

	public void setNeedToBeLearn(boolean boolean1) {
		this.needToBeLearn = boolean1;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String string) {
		this.category = string;
	}

	public ArrayList getRequiredSkills() {
		ArrayList arrayList = null;
		if (this.skillRequired != null) {
			arrayList = new ArrayList();
			Iterator iterator = this.skillRequired.keySet().iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				String string2 = PerkFactory.getPerk(PerkFactory.Perks.FromString(string)).name + " ";
				string2 = string2 + this.skillRequired.get(string);
				arrayList.add(string2);
			}
		}

		return arrayList;
	}

	public Recipe.Source findSource(String string) {
		for (int int1 = 0; int1 < this.Source.size(); ++int1) {
			Recipe.Source source = (Recipe.Source)this.Source.get(int1);
			for (int int2 = 0; int2 < source.getItems().size(); ++int2) {
				if (((String)source.getItems().get(int2)).equals(string)) {
					return source;
				}
			}
		}

		return null;
	}

	public boolean isDestroy(String string) {
		Recipe.Source source = this.findSource(string);
		if (source != null) {
			return source.isDestroy();
		} else {
			throw new RuntimeException("recipe " + this.getOriginalname() + " doesn\'t use item " + string);
		}
	}

	public boolean isKeep(String string) {
		Recipe.Source source = this.findSource(string);
		if (source != null) {
			return source.isKeep();
		} else {
			throw new RuntimeException("recipe " + this.getOriginalname() + " doesn\'t use item " + string);
		}
	}

	public float getHeat() {
		return this.heat;
	}

	public boolean noBrokenItems() {
		return this.noBrokenItems;
	}

	public int getWaterAmountNeeded() {
		Recipe.Source source = this.findSource("Water");
		return source != null ? (int)source.getCount() : 0;
	}

	public String getNearItem() {
		return this.nearItem;
	}

	public void setNearItem(String string) {
		this.nearItem = string;
	}

	public boolean isRemoveResultItem() {
		return this.removeResultItem;
	}

	public void setRemoveResultItem(boolean boolean1) {
		this.removeResultItem = boolean1;
	}

	public class Source {
		public boolean keep = false;
		private ArrayList items = new ArrayList();
		public boolean destroy = false;
		public float count = 1.0F;
		public float use = 0.0F;

		public boolean isDestroy() {
			return this.destroy;
		}

		public boolean isKeep() {
			return this.keep;
		}

		public float getCount() {
			return this.count;
		}

		public float getUse() {
			return this.use;
		}

		public ArrayList getItems() {
			return this.items;
		}

		public String getOnlyItem() {
			if (this.items.size() != 1) {
				throw new RuntimeException("items.size() == " + this.items.size());
			} else {
				return (String)this.items.get(0);
			}
		}
	}

	public class Result {
		public String type;
		public int count = 1;
		public int drainableCount = 0;
		public String module = null;

		public String getType() {
			return this.type;
		}

		public int getCount() {
			return this.count;
		}

		public String getModule() {
			return this.module;
		}

		public String getFullType() {
			return this.module + "." + this.type;
		}

		public int getDrainableCount() {
			return this.drainableCount;
		}
	}
}
