package zombie.behaviors;

import zombie.characters.IsoGameCharacter;


public class SelectorBehavior extends Behavior {
	public int ID = 0;

	public Behavior.BehaviorResult process(DecisionPath decisionPath, IsoGameCharacter gameCharacter) {
		if (this.ID >= this.childNodes.size()) {
			return Behavior.BehaviorResult.Failed;
		} else {
			Behavior.BehaviorResult behaviorResult = this.processChild(decisionPath, gameCharacter, this.ID);
			if (behaviorResult == Behavior.BehaviorResult.Failed) {
				++this.ID;
			}

			if (behaviorResult == Behavior.BehaviorResult.Succeeded) {
				return behaviorResult;
			} else if (this.ID == this.childNodes.size() && behaviorResult == Behavior.BehaviorResult.Failed) {
				this.ID = 0;
				return Behavior.BehaviorResult.Failed;
			} else {
				return Behavior.BehaviorResult.Working;
			}
		}
	}

	public void reset() {
	}

	public boolean valid() {
		return true;
	}
}
