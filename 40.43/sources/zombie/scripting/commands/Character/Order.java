package zombie.scripting.commands.Character;

import zombie.behaviors.survivor.orders.FollowOrder;
import zombie.behaviors.survivor.orders.IdleOrder;
import zombie.behaviors.survivor.orders.LittleTasks.FaceOrder;
import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;


public class Order extends BaseCommand {
	String owner;
	boolean bGory = false;
	String[] params;
	String order = null;

	public void init(String string, String[] stringArray) {
		this.owner = string;
		this.params = new String[stringArray.length - 1];
		int int1 = 0;
		String[] stringArray2 = stringArray;
		int int2 = stringArray.length;
		for (int int3 = 0; int3 < int2; ++int3) {
			String string2 = stringArray2[int3];
			if (int1 > 0) {
				this.params[int1 - 1] = string2.trim();
			}

			++int1;
		}

		this.order = stringArray[0].trim();
	}

	public zombie.behaviors.survivor.orders.Order orderInfo(IsoGameCharacter gameCharacter) {
		if (this.order.equals("Idle")) {
			gameCharacter.getOrders().push(new IdleOrder(gameCharacter));
		}

		IsoGameCharacter gameCharacter2;
		int int1;
		if (this.order.equals("Follow")) {
			gameCharacter2 = null;
			if (this.currentinstance.HasAlias(this.params[0])) {
				gameCharacter2 = this.currentinstance.getAlias(this.params[0]);
			} else {
				gameCharacter2 = this.module.getCharacterActual(this.params[0]);
			}

			int1 = Integer.parseInt(this.params[1]);
			gameCharacter.getOrders().push(new FollowOrder(gameCharacter, gameCharacter2, int1));
		} else if (this.order.equals("Face")) {
			gameCharacter2 = null;
			if (this.currentinstance.HasAlias(this.params[0])) {
				gameCharacter2 = this.currentinstance.getAlias(this.params[0]);
			} else {
				gameCharacter2 = this.module.getCharacterActual(this.params[0]);
			}

			gameCharacter.getOrders().push(new FaceOrder(gameCharacter, gameCharacter2));
		} else if (this.order.equals("FollowStrict")) {
			gameCharacter2 = null;
			if (this.currentinstance.HasAlias(this.params[0])) {
				gameCharacter2 = this.currentinstance.getAlias(this.params[0]);
			} else {
				gameCharacter2 = this.module.getCharacterActual(this.params[0]);
			}

			int1 = Integer.parseInt(this.params[1]);
			gameCharacter.getOrders().push(new FollowOrder(gameCharacter, gameCharacter2, int1, true));
		}

		gameCharacter.setOrder((zombie.behaviors.survivor.orders.Order)gameCharacter.getOrders().peek());
		return (zombie.behaviors.survivor.orders.Order)gameCharacter.getOrders().peek();
	}

	public void begin() {
		IsoGameCharacter gameCharacter = null;
		if (this.currentinstance.HasAlias(this.owner)) {
			gameCharacter = this.currentinstance.getAlias(this.owner);
		} else {
			gameCharacter = this.module.getCharacterActual(this.owner);
		}

		this.orderInfo(gameCharacter);
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
