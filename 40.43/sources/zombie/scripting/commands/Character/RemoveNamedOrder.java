package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;


public class RemoveNamedOrder extends BaseCommand {
	String owner;
	String name;

	public void init(String string, String[] stringArray) {
		this.owner = string;
		if (stringArray.length == 1) {
			this.name = stringArray[0].trim();
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
			for (int int1 = 0; int1 < gameCharacter.getOrders().size(); ++int1) {
				if (((zombie.behaviors.survivor.orders.Order)gameCharacter.getOrders().get(int1)).name.equals(this.name)) {
					gameCharacter.getOrders().remove(int1);
					--int1;
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
