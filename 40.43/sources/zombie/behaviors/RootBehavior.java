package zombie.behaviors;

import zombie.characters.IsoGameCharacter;


public class RootBehavior extends Behavior {

	public Behavior.BehaviorResult process(DecisionPath decisionPath, IsoGameCharacter gameCharacter) {
		if (this.childNodes.size() == 0) {
			return Behavior.BehaviorResult.Working;
		} else {
			for (int int1 = 0; int1 < this.childNodes.size(); ++int1) {
				this.processChild(decisionPath, gameCharacter, int1);
			}

			return Behavior.BehaviorResult.Working;
		}
	}

	public void reset() {
	}

	public boolean valid() {
		return true;
	}
}
