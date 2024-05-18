package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.inventory.ItemContainer;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptContainer;


public class HasInventory extends BaseCommand {
	boolean invert = false;
	String character;
	String item;

	public void begin() {
	}

	public boolean getValue() {
		IsoGameCharacter gameCharacter = this.module.getCharacterActual(this.character);
		ItemContainer itemContainer = null;
		if (gameCharacter != null) {
			itemContainer = gameCharacter.getInventory();
		} else {
			ScriptContainer scriptContainer = this.module.getScriptContainer(this.character);
			if (scriptContainer == null) {
				return false;
			}

			itemContainer = scriptContainer.getActual();
			if (itemContainer == null) {
				return false;
			}
		}

		if (this.invert) {
			return !itemContainer.contains(this.item);
		} else {
			return itemContainer.contains(this.item);
		}
	}

	public boolean IsFinished() {
		return true;
	}

	public void update() {
	}

	public void init(String string, String[] stringArray) {
		this.character = string;
		this.item = stringArray[0].replace("\"", "");
		if (this.character.indexOf("!") == 0) {
			this.invert = true;
			this.character = this.character.substring(1);
		}
	}

	public boolean DoesInstantly() {
		return true;
	}
}
