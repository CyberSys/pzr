package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;


public class PopOrder extends BaseCommand {
	String owner;
	int index = -1;

	public void init(String string, String[] stringArray) {
		this.owner = string;
		if (stringArray.length == 1) {
			this.index = Integer.parseInt(stringArray[0].trim());
		}
	}

	public void begin() {
		IsoGameCharacter gameCharacter = null;
		if (this.currentinstance.HasAlias(this.owner)) {
			gameCharacter = this.currentinstance.getAlias(this.owner);
		} else {
			gameCharacter = this.module.getCharacterActual(this.owner);
		}

		if (!gameCharacter.getOrders().empty()) {
			if (this.index == -1) {
				gameCharacter.getOrders().pop();
			} else {
				this.index = gameCharacter.getOrders().size() - this.index - 1;
				if (this.index < gameCharacter.getOrders().size() && this.index >= 0) {
					gameCharacter.getOrders().remove(this.index);
				}
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
