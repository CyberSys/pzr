package zombie.behaviors;

import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;


public class RandomBehavior extends Behavior {

	public Behavior.BehaviorResult process(DecisionPath decisionPath, IsoGameCharacter gameCharacter) {
		return this.processChild(decisionPath, gameCharacter, Rand.Next(this.childNodes.size()));
	}

	public void reset() {
	}

	public boolean valid() {
		return true;
	}
}
