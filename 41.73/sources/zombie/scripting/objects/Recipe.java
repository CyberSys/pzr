package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.Arrays;
import zombie.characters.skills.PerkFactory;
import zombie.core.Translator;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.util.StringUtils;


public class Recipe extends BaseScriptObject {
	private boolean canBeDoneFromFloor = false;
	public float TimeToMake = 0.0F;
	public String Sound;
	protected String AnimNode;
	protected String Prop1;
	protected String Prop2;
	public final ArrayList Source = new ArrayList();
	public Recipe.Result Result = null;
	public boolean AllowDestroyedItem = false;
	public boolean AllowFrozenItem = false;
	public boolean InSameInventory = false;
	public String LuaTest = null;
	public String LuaCreate = null;
	public String LuaGrab = null;
	public String name = "recipe";
	private String originalname;
	private String nearItem;
	private String LuaCanPerform;
	private String tooltip = null;
	public ArrayList skillRequired = null;
	public String LuaGiveXP;
	private boolean needToBeLearn = false;
	protected String category = null;
	protected boolean removeResultItem = false;
	private float heat = 0.0F;
	protected boolean stopOnWalk = true;
	protected boolean stopOnRun = true;
	public boolean hidden = false;

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

	public String getFullType() {
		return this.module + "." + this.originalname;
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

					if (string2.equals("AnimNode")) {
						this.AnimNode = string3.trim();
					}

					if (string2.equals("Prop1")) {
						this.Prop1 = string3.trim();
					}

					if (string2.equals("Prop2")) {
						this.Prop2 = string3.trim();
					}

					if (string2.equals("Time")) {
						this.TimeToMake = Float.parseFloat(string3);
					}

					if (string2.equals("Sound")) {
						this.Sound = string3.trim();
					}

					if (string2.equals("InSameInventory")) {
						this.InSameInventory = Boolean.parseBoolean(string3);
					}

					if (string2.equals("Result")) {
						this.DoResult(string3);
					}

					if (string2.equals("OnCanPerform")) {
						this.LuaCanPerform = StringUtils.discardNullOrWhitespace(string3);
					}

					if (string2.equals("OnTest")) {
						this.LuaTest = string3;
					}

					if (string2.equals("OnCreate")) {
						this.LuaCreate = string3;
					}

					if (string2.equals("AllowDestroyedItem")) {
						this.AllowDestroyedItem = Boolean.parseBoolean(string3);
					}

					if (string2.equals("AllowFrozenItem")) {
						this.AllowFrozenItem = Boolean.parseBoolean(string3);
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
						this.skillRequired = new ArrayList();
						String[] stringArray3 = string3.split(";");
						for (int int2 = 0; int2 < stringArray3.length; ++int2) {
							String[] stringArray4 = stringArray3[int2].split("=");
							PerkFactory.Perk perk = PerkFactory.Perks.FromString(stringArray4[0]);
							if (perk == PerkFactory.Perks.MAX) {
								DebugLog.Recipe.warn("Unknown skill \"%s\" in recipe \"%s\"", stringArray4, this.name);
							} else {
								int int3 = PZMath.tryParseInt(stringArray4[1], 1);
								Recipe.RequiredSkill requiredSkill = new Recipe.RequiredSkill(perk, int3);
								this.skillRequired.add(requiredSkill);
							}
						}
					}

					if (string2.equals("OnGiveXP")) {
						this.LuaGiveXP = string3;
					}

					if (string2.equalsIgnoreCase("Tooltip")) {
						this.tooltip = StringUtils.discardNullOrWhitespace(string3);
					}

					if (string2.equals("Obsolete") && string3.trim().toLowerCase().equals("true")) {
						this.module.RecipeMap.remove(this);
						this.module.RecipeByName.remove(this.getOriginalname());
						this.module.RecipesWithDotInName.remove(this);
						return;
					}

					if (string2.equals("Heat")) {
						this.heat = Float.parseFloat(string3);
					}

					if (string2.equals("NoBrokenItems")) {
						this.AllowDestroyedItem = !StringUtils.tryParseBoolean(string3);
					}

					if (string2.equals("StopOnWalk")) {
						this.stopOnWalk = string3.trim().equalsIgnoreCase("true");
					}

					if (string2.equals("StopOnRun")) {
						this.stopOnRun = string3.trim().equalsIgnoreCase("true");
					}

					if (string2.equals("IsHidden")) {
						this.hidden = string3.trim().equalsIgnoreCase("true");
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
				this.module.RecipeByName.put(string, this);
			}
		}
	}

	public void DoSource(String string) {
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

	public void DoResult(String string) {
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

	public void setSound(String string) {
		this.Sound = string;
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
			for (int int1 = 0; int1 < this.skillRequired.size(); ++int1) {
				Recipe.RequiredSkill requiredSkill = (Recipe.RequiredSkill)this.skillRequired.get(int1);
				PerkFactory.Perk perk = PerkFactory.getPerk(requiredSkill.perk);
				if (perk == null) {
					arrayList.add(requiredSkill.perk.name + " " + requiredSkill.level);
				} else {
					String string = perk.name + " " + requiredSkill.level;
					arrayList.add(string);
				}
			}
		}

		return arrayList;
	}

	public int getRequiredSkillCount() {
		return this.skillRequired == null ? 0 : this.skillRequired.size();
	}

	public Recipe.RequiredSkill getRequiredSkill(int int1) {
		return this.skillRequired != null && int1 >= 0 && int1 < this.skillRequired.size() ? (Recipe.RequiredSkill)this.skillRequired.get(int1) : null;
	}

	public void clearRequiredSkills() {
		if (this.skillRequired != null) {
			this.skillRequired.clear();
		}
	}

	public void addRequiredSkill(PerkFactory.Perk perk, int int1) {
		if (this.skillRequired == null) {
			this.skillRequired = new ArrayList();
		}

		this.skillRequired.add(new Recipe.RequiredSkill(perk, int1));
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
			String string2 = this.getOriginalname();
			throw new RuntimeException("recipe " + string2 + " doesn\'t use item " + string);
		}
	}

	public boolean isKeep(String string) {
		Recipe.Source source = this.findSource(string);
		if (source != null) {
			return source.isKeep();
		} else {
			String string2 = this.getOriginalname();
			throw new RuntimeException("recipe " + string2 + " doesn\'t use item " + string);
		}
	}

	public float getHeat() {
		return this.heat;
	}

	public boolean noBrokenItems() {
		return !this.AllowDestroyedItem;
	}

	public boolean isAllowDestroyedItem() {
		return this.AllowDestroyedItem;
	}

	public void setAllowDestroyedItem(boolean boolean1) {
		this.AllowDestroyedItem = boolean1;
	}

	public boolean isAllowFrozenItem() {
		return this.AllowFrozenItem;
	}

	public void setAllowFrozenItem(boolean boolean1) {
		this.AllowFrozenItem = boolean1;
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

	public String getCanPerform() {
		return this.LuaCanPerform;
	}

	public void setCanPerform(String string) {
		this.LuaCanPerform = string;
	}

	public String getLuaTest() {
		return this.LuaTest;
	}

	public void setLuaTest(String string) {
		this.LuaTest = string;
	}

	public String getLuaCreate() {
		return this.LuaCreate;
	}

	public void setLuaCreate(String string) {
		this.LuaCreate = string;
	}

	public String getLuaGrab() {
		return this.LuaGrab;
	}

	public void setLuaGrab(String string) {
		this.LuaGrab = string;
	}

	public String getLuaGiveXP() {
		return this.LuaGiveXP;
	}

	public void setLuaGiveXP(String string) {
		this.LuaGiveXP = string;
	}

	public boolean isRemoveResultItem() {
		return this.removeResultItem;
	}

	public void setRemoveResultItem(boolean boolean1) {
		this.removeResultItem = boolean1;
	}

	public String getAnimNode() {
		return this.AnimNode;
	}

	public void setAnimNode(String string) {
		this.AnimNode = string;
	}

	public String getProp1() {
		return this.Prop1;
	}

	public void setProp1(String string) {
		this.Prop1 = string;
	}

	public String getProp2() {
		return this.Prop2;
	}

	public void setProp2(String string) {
		this.Prop2 = string;
	}

	public String getTooltip() {
		return this.tooltip;
	}

	public void setStopOnWalk(boolean boolean1) {
		this.stopOnWalk = boolean1;
	}

	public boolean isStopOnWalk() {
		return this.stopOnWalk;
	}

	public void setStopOnRun(boolean boolean1) {
		this.stopOnRun = boolean1;
	}

	public boolean isStopOnRun() {
		return this.stopOnRun;
	}

	public void setIsHidden(boolean boolean1) {
		this.hidden = boolean1;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public static final class Result {
		public String module = null;
		public String type;
		public int count = 1;
		public int drainableCount = 0;

		public String getType() {
			return this.type;
		}

		public void setType(String string) {
			this.type = string;
		}

		public int getCount() {
			return this.count;
		}

		public void setCount(int int1) {
			this.count = int1;
		}

		public String getModule() {
			return this.module;
		}

		public void setModule(String string) {
			this.module = string;
		}

		public String getFullType() {
			return this.module + "." + this.type;
		}

		public int getDrainableCount() {
			return this.drainableCount;
		}

		public void setDrainableCount(int int1) {
			this.drainableCount = int1;
		}
	}

	public static final class Source {
		public boolean keep = false;
		private final ArrayList items = new ArrayList();
		public boolean destroy = false;
		public float count = 1.0F;
		public float use = 0.0F;

		public boolean isDestroy() {
			return this.destroy;
		}

		public void setDestroy(boolean boolean1) {
			this.destroy = boolean1;
		}

		public boolean isKeep() {
			return this.keep;
		}

		public void setKeep(boolean boolean1) {
			this.keep = boolean1;
		}

		public float getCount() {
			return this.count;
		}

		public void setCount(float float1) {
			this.count = float1;
		}

		public float getUse() {
			return this.use;
		}

		public void setUse(float float1) {
			this.use = float1;
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

	public static final class RequiredSkill {
		private final PerkFactory.Perk perk;
		private final int level;

		public RequiredSkill(PerkFactory.Perk perk, int int1) {
			this.perk = perk;
			this.level = int1;
		}

		public PerkFactory.Perk getPerk() {
			return this.perk;
		}

		public int getLevel() {
			return this.level;
		}
	}
}
