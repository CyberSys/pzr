package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.inventory.ItemContainer;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptContainer;


public class AddInventory extends BaseCommand {
	String owner;
	String item;

	public void init(String string, String[] stringArray) {
		this.owner = string;
		this.item = stringArray[0];
	}

	public void begin() {
		IsoGameCharacter gameCharacter = this.module.getCharacterActual(this.owner);
		ItemContainer itemContainer = null;
		if (gameCharacter != null) {
			itemContainer = gameCharacter.getInventory();
		} else {
			ScriptContainer scriptContainer = this.module.getScriptContainer(this.owner);
			if (scriptContainer == null) {
				return;
			}

			itemContainer = scriptContainer.getActual();
			if (itemContainer == null) {
				return;
			}
		}

		String string = this.item;
		if (!string.contains(".")) {
			string = this.module.name + "." + string;
		}

		itemContainer.AddItem(string);
	}

	public void Finish() {
	}

	public boolean IsFinished() {
		return true;
	}

	public void update() {
	}

	public boolean DoesInstantly() {
		return true;
	}
}
