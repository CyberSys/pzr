package zombie.scripting.objects;

import java.util.Arrays;
import zombie.characters.skills.PerkFactory;
import zombie.debug.DebugLog;


public class MovableRecipe extends Recipe {
	private boolean isValid = false;
	private String worldSprite = "";
	private PerkFactory.Perk xpPerk;
	private Recipe.Source primaryTools;
	private Recipe.Source secondaryTools;

	public MovableRecipe() {
		this.xpPerk = PerkFactory.Perks.MAX;
		this.AnimNode = "Disassemble";
		this.removeResultItem = true;
		this.noBrokenItems = true;
		this.name = "Disassemble Movable";
		this.setCanBeDoneFromFloor(false);
	}

	public void setResult(String string, int int1) {
		Recipe.Result result = new Recipe.Result();
		result.count = int1;
		if (string.contains(".")) {
			result.type = string.split("\\.")[1];
			result.module = string.split("\\.")[0];
		} else {
			DebugLog.log("MovableRecipe invalid result item. item = " + string);
		}

		this.Result = result;
	}

	public void setSource(String string) {
		Recipe.Source source = new Recipe.Source();
		source.getItems().add(string);
		this.Source.add(source);
	}

	public void setTool(String string, boolean boolean1) {
		Recipe.Source source = new Recipe.Source();
		source.keep = true;
		if (string.contains("/")) {
			string = string.replaceFirst("keep ", "").trim();
			source.getItems().addAll(Arrays.asList(string.split("/")));
		} else {
			source.getItems().add(string);
		}

		if (boolean1) {
			this.primaryTools = source;
		} else {
			this.secondaryTools = source;
		}

		this.Source.add(source);
	}

	public Recipe.Source getPrimaryTools() {
		return this.primaryTools;
	}

	public Recipe.Source getSecondaryTools() {
		return this.secondaryTools;
	}

	public void setRequiredSkill(PerkFactory.Perk perk, int int1) {
		Recipe.RequiredSkill requiredSkill = new Recipe.RequiredSkill(perk, int1);
		this.skillRequired.add(requiredSkill);
	}

	public void setXpPerk(PerkFactory.Perk perk) {
		this.xpPerk = perk;
	}

	public PerkFactory.Perk getXpPerk() {
		return this.xpPerk;
	}

	public boolean hasXpPerk() {
		return this.xpPerk != PerkFactory.Perks.MAX;
	}

	public void setOnCreate(String string) {
		this.LuaCreate = string;
	}

	public void setOnXP(String string) {
		this.LuaGiveXP = string;
	}

	public void setTime(float float1) {
		this.TimeToMake = float1;
	}

	public void setName(String string) {
		this.name = string;
	}

	public String getWorldSprite() {
		return this.worldSprite;
	}

	public void setWorldSprite(String string) {
		this.worldSprite = string;
	}

	public boolean isValid() {
		return this.isValid;
	}

	public void setValid(boolean boolean1) {
		this.isValid = boolean1;
	}
}
