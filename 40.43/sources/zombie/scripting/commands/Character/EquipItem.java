package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.inventory.ItemContainer;
import zombie.scripting.commands.BaseCommand;


public class EquipItem extends BaseCommand {
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
			String string = this.item;
			if (string.contains(".")) {
				string = string.substring(string.lastIndexOf(".") + 1);
			}

			if (itemContainer.contains(string)) {
				gameCharacter.setPrimaryHandItem(itemContainer.FindAndReturn(string));
			}
		}
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
