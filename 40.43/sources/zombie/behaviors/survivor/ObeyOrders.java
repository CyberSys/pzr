package zombie.behaviors.survivor;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoCamera;


public class ObeyOrders extends Behavior {
	IsoGameCharacter character;
	public boolean Started = false;

	public ObeyOrders(IsoGameCharacter gameCharacter) {
		this.character = gameCharacter;
	}

	public Behavior.BehaviorResult process(DecisionPath decisionPath, IsoGameCharacter gameCharacter) {
		if (gameCharacter == IsoCamera.CamCharacter) {
			boolean boolean1 = false;
		}

		Behavior.BehaviorResult behaviorResult;
		if (gameCharacter.getPersonalNeed() != null) {
			if (!gameCharacter.getPersonalNeed().bInit) {
				gameCharacter.getPersonalNeed().initOrder();
				gameCharacter.getPersonalNeed().bInit = true;
			}

			behaviorResult = gameCharacter.getPersonalNeed().process();
			if (gameCharacter.getPersonalNeed().complete()) {
				gameCharacter.getPersonalNeeds().pop();
				gameCharacter.setPersonalNeed((Order)null);
			} else if (gameCharacter.getPersonalNeed().ActedThisFrame()) {
				return behaviorResult;
			}
		}

		if (gameCharacter.getOrder() == null) {
			return Behavior.BehaviorResult.Succeeded;
		} else {
			if (!gameCharacter.getOrder().bInit) {
				gameCharacter.getOrder().initOrder();
				gameCharacter.getOrder().bInit = true;
			}

			behaviorResult = gameCharacter.getOrder().process();
			if (gameCharacter.getOrder().complete()) {
				gameCharacter.getOrders().pop();
			}

			return behaviorResult;
		}
	}

	public void update() {
		if (this.character.getOrder() != null) {
			this.character.getOrder().update();
		}
	}

	public void reset() {
	}

	public float getPathSpeed() {
		if (this.character.getOrder() != null) {
			return this.character.getOrder().getPathSpeed();
		} else if (this.character.getDangerLevels() == 0.0F) {
			return 0.05F;
		} else {
			float float1 = 10.0F / this.character.getDangerLevels();
			if (float1 > 1.0F) {
				float1 = 1.0F;
			}

			if (float1 < 0.0F) {
				float1 = 0.0F;
			}

			return 0.05F + 0.02F * float1;
		}
	}

	public boolean valid() {
		return true;
	}

	public int renderDebug(int int1) {
		return this.character.getOrder() == null ? int1 : this.character.getOrder().renderDebug(int1);
	}
}
