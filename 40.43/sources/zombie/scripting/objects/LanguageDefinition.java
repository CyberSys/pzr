package zombie.scripting.objects;

import java.util.ArrayList;


public class LanguageDefinition extends BaseScriptObject {
	public ArrayList Items = new ArrayList();

	public String get(int int1) {
		return (String)this.Items.get(int1);
	}

	public void Load(String string, String[] stringArray) {
		for (int int1 = 0; int1 < stringArray.length; ++int1) {
			if (stringArray[int1] != null) {
				this.DoSource(stringArray[int1].trim());
			}
		}
	}

	private void DoSource(String string) {
		String string2 = "";
		this.Items.add(string);
	}
}
