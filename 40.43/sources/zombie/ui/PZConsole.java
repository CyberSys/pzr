package zombie.ui;

import java.util.ArrayList;
import zombie.console.Commands;


public class PZConsole extends UIElement {
	public ArrayList buffer = new ArrayList();
	public String currentline = "";
	public static PZConsole instance = new PZConsole();
	ArrayList charactersTypedSinceUpdate = new ArrayList();
	ArrayList keysReleasedSinceUpdate = new ArrayList();

	public void render() {
	}

	public void update() {
		int int1;
		for (int1 = 0; int1 < this.charactersTypedSinceUpdate.size(); ++int1) {
			this.currentline = this.currentline + this.charactersTypedSinceUpdate.get(int1);
		}

		for (int1 = 0; int1 < this.keysReleasedSinceUpdate.size(); ++int1) {
			if ((Integer)this.keysReleasedSinceUpdate.get(int1) == 28) {
				this.buffer.add("> " + this.currentline.trim());
				this.Process(this.currentline);
				this.currentline = "";
			}
		}
	}

	public void Log(String string) {
		this.buffer.add(string);
	}

	public Boolean isVisible() {
		return Boolean.FALSE;
	}

	private void Process(String string) {
		try {
			String[] stringArray = string.split(",");
			String string2 = "";
			if (stringArray[0].trim().contains(" ")) {
				string2 = stringArray[0].split(" ")[0].trim();
				stringArray[0] = stringArray[0].trim().split(" ")[1].trim();
			} else {
				string2 = stringArray[0].trim();
			}

			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				stringArray[int1] = stringArray[int1].trim();
			}

			string2 = string2.toLowerCase();
			Commands.ProcessCommand(string2, stringArray);
		} catch (Exception exception) {
			this.buffer.add("Invalid command: " + string);
		}
	}
}
