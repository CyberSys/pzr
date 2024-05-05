package zombie.scripting.objects;


public final class ItemRecipe {
	public String name;
	public Integer use = -1;
	public Boolean cooked = false;
	private String module = null;

	public Integer getUse() {
		return this.use;
	}

	public ItemRecipe(String string, String string2, Integer integer) {
		this.name = string;
		this.use = integer;
		this.setModule(string2);
	}

	public String getName() {
		return this.name;
	}

	public String getModule() {
		return this.module;
	}

	public void setModule(String string) {
		this.module = string;
	}

	public String getFullType() {
		return this.module + "." + this.name;
	}
}
