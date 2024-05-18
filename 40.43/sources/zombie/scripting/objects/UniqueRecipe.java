package zombie.scripting.objects;

import java.util.ArrayList;


public class UniqueRecipe extends BaseScriptObject {
	private String name = null;
	private String baseRecipe = null;
	private ArrayList items = new ArrayList();
	private int hungerBonus = 0;
	private int hapinessBonus = 0;
	private int boredomBonus = 0;

	public UniqueRecipe(String string) {
		this.setName(string);
	}

	public void Load(String string, String[] stringArray) {
		for (int int1 = 0; int1 < stringArray.length; ++int1) {
			if (!stringArray[int1].trim().isEmpty() && stringArray[int1].contains(":")) {
				String[] stringArray2 = stringArray[int1].split(":");
				String string2 = stringArray2[0].trim();
				String string3 = stringArray2[1].trim();
				if (string2.equals("BaseRecipeItem")) {
					this.setBaseRecipe(string3);
				} else if (string2.equals("Item")) {
					this.items.add(string3);
				} else if (string2.equals("Hunger")) {
					this.setHungerBonus(Integer.parseInt(string3));
				} else if (string2.equals("Hapiness")) {
					this.setHapinessBonus(Integer.parseInt(string3));
				} else if (string2.equals("Boredom")) {
					this.setBoredomBonus(Integer.parseInt(string3));
				}
			}
		}
	}

	public String getName() {
		return this.name;
	}

	public void setName(String string) {
		this.name = string;
	}

	public String getBaseRecipe() {
		return this.baseRecipe;
	}

	public void setBaseRecipe(String string) {
		this.baseRecipe = string;
	}

	public int getHungerBonus() {
		return this.hungerBonus;
	}

	public void setHungerBonus(int int1) {
		this.hungerBonus = int1;
	}

	public int getHapinessBonus() {
		return this.hapinessBonus;
	}

	public void setHapinessBonus(int int1) {
		this.hapinessBonus = int1;
	}

	public ArrayList getItems() {
		return this.items;
	}

	public int getBoredomBonus() {
		return this.boredomBonus;
	}

	public void setBoredomBonus(int int1) {
		this.boredomBonus = int1;
	}
}
