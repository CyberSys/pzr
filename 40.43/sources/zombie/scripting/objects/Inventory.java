package zombie.scripting.objects;

import java.util.ArrayList;


public class Inventory extends BaseScriptObject {
	public ArrayList Items = new ArrayList();

	public void Load(String string, String[] stringArray) {
		for (int int1 = 0; int1 < stringArray.length; ++int1) {
			if (stringArray[int1] != null) {
				this.DoSource(stringArray[int1].trim());
			}
		}
	}

	private void DoSource(String string) {
		Inventory.Source source = new Inventory.Source();
		if (string.contains("=")) {
			source.count = Integer.parseInt(string.split("=")[1].trim());
			string = string.split("=")[0].trim();
		}

		if (string.equals("null")) {
			source.type = null;
		} else {
			source.type = string;
		}

		this.Items.add(source);
	}

	public class Source {
		public String type;
		public int count = 1;
	}
}
