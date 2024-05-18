package zombie.behaviors;

import zombie.characters.IsoGameCharacter;


public class SequenceBehavior extends Behavior {
	public int ID = 0;
	public boolean ProcessNextOnFail = false;

	public Behavior.BehaviorResult process(DecisionPath decisionPath, IsoGameCharacter gameCharacter) {
		if (this.ID >= this.childNodes.size()) {
			return Behavior.BehaviorResult.Succeeded;
		} else {
			Behavior.BehaviorResult behaviorResult;
			do {
				do {
					if (this.ID >= this.childNodes.size()) {
						return Behavior.BehaviorResult.Working;
					}

					behaviorResult = this.processChild(decisionPath, gameCharacter, this.ID);
					if (behaviorResult == Behavior.BehaviorResult.Succeeded) {
						++this.ID;
					} else {
						if (behaviorResult != Behavior.BehaviorResult.Failed) {
							return behaviorResult;
						}

						if (!this.ProcessNextOnFail) {
							this.ID = 0;
							return behaviorResult;
						}

						++this.ID;
					}
				}		 while (this.ID != this.childNodes.size());
			}	 while (behaviorResult != Behavior.BehaviorResult.Succeeded && (behaviorResult != Behavior.BehaviorResult.Failed || !this.ProcessNextOnFail));

			this.ID = 0;
			return Behavior.BehaviorResult.Succeeded;
		}
	}

	public void reset() {
		this.ID = 0;
		for (int int1 = 0; int1 < this.childNodes.size(); ++int1) {
			((Behavior)this.childNodes.get(int1)).reset();
		}
	}

	public boolean valid() {
		return true;
	}
}
