package zombie.behaviors.survivor.orders;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.survivor.SatisfyIdleBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.characters.SurvivorDesc;


public class WaitUntilFollowersArrive extends Order {
	SatisfyIdleBehavior idle = new SatisfyIdleBehavior();
	int timeout = 600;

	public WaitUntilFollowersArrive(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
	}

	public Behavior.BehaviorResult process() {
		this.idle.process((DecisionPath)null, this.character);
		--this.timeout;
		return Behavior.BehaviorResult.Working;
	}

	public boolean complete() {
		if (this.timeout <= 0) {
			return true;
		} else {
			for (int int1 = 0; int1 < this.character.getDescriptor().getGroup().Members.size(); ++int1) {
				SurvivorDesc survivorDesc = (SurvivorDesc)this.character.getDescriptor().getGroup().Members.get(int1);
				if (survivorDesc.getInstance() != null && survivorDesc.getInstance() != this.character && !survivorDesc.getInstance().isDead() && survivorDesc.getInstance().getOrder() instanceof FollowOrder && ((FollowOrder)survivorDesc.getInstance().getOrder()).target == this.character && !survivorDesc.getInstance().InBuildingWith(this.character)) {
					return false;
				}
			}

			return true;
		}
	}

	public void update() {
	}
}
