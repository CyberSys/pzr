package zombie.behaviors.survivor.orders;

import zombie.behaviors.Behavior;
import zombie.characters.IsoGameCharacter;


public abstract class Order {
	public IsoGameCharacter character = null;
	public String type = "Order";
	public String name = "unnamed";
	public boolean bInit = false;

	public Order(IsoGameCharacter gameCharacter) {
		this.character = gameCharacter;
	}

	public abstract Behavior.BehaviorResult process();

	public abstract boolean complete();

	public boolean ActedThisFrame() {
		return true;
	}

	public Behavior.BehaviorResult processNext() {
		for (int int1 = this.character.getOrders().size() - 1; int1 >= 0; --int1) {
			if (this.character.getOrders().get(int1) == this && int1 > 1) {
				return ((Order)this.character.getOrders().get(int1 - 1)).process();
			}
		}

		return Behavior.BehaviorResult.Succeeded;
	}

	public void updatenext() {
		for (int int1 = this.character.getOrders().size() - 1; int1 >= 0; --int1) {
			if (this.character.getOrders().get(int1) == this && int1 > 1) {
				((Order)this.character.getOrders().get(int1 - 1)).update();
			}
		}
	}

	public abstract void update();

	public boolean isCancelledOnAttack() {
		return true;
	}

	public void initOrder() {
	}

	public float getPriority(IsoGameCharacter gameCharacter) {
		return 100000.0F;
	}

	public int renderDebug(int int1) {
		return int1;
	}

	public float getPathSpeed() {
		if (this.character.getDangerLevels() == 0.0F) {
			return 0.06F;
		} else {
			float float1 = 10.0F / this.character.getDangerLevels();
			if (float1 > 1.0F) {
				float1 = 1.0F;
			}

			if (float1 < 0.0F) {
				float1 = 0.0F;
			}

			return 0.06F + 0.02F * float1;
		}
	}

	public int getAttackIfEnemiesAroundBias() {
		return 0;
	}

	public boolean isCritical() {
		return false;
	}
}
