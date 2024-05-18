package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;


public class NamedOrder extends Order {
	String name;

	public void init(String string, String[] stringArray) {
		this.owner = string;
		this.params = new String[stringArray.length - 2];
		int int1 = 0;
		String[] stringArray2 = stringArray;
		int int2 = stringArray.length;
		for (int int3 = 0; int3 < int2; ++int3) {
			String string2 = stringArray2[int3];
			if (int1 > 1) {
				this.params[int1 - 2] = string2.trim();
			}

			++int1;
		}

		this.name = stringArray[0].trim();
		this.order = stringArray[1].trim();
	}

	public void begin() {
		IsoGameCharacter gameCharacter = null;
		if (this.currentinstance.HasAlias(this.owner)) {
			gameCharacter = this.currentinstance.getAlias(this.owner);
		} else {
			gameCharacter = this.module.getCharacterActual(this.owner);
		}

		zombie.behaviors.survivor.orders.Order order = this.orderInfo(gameCharacter);
		order.name = this.name;
	}
}
