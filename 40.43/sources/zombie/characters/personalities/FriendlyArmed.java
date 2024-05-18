package zombie.characters.personalities;

import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.FollowBehaviour;
import zombie.behaviors.survivor.AttackBehavior;
import zombie.behaviors.survivor.FleeBehaviour;
import zombie.behaviors.survivor.ObeyOrders;
import zombie.behaviors.survivor.SatisfyIdleBehavior;
import zombie.characters.IsoSurvivor;
import zombie.characters.SurvivorPersonality;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;


public class FriendlyArmed extends SurvivorPersonality {

	public int getZombieFleeAmount() {
		return 10;
	}

	public void CreateBehaviours(IsoSurvivor survivor) {
		survivor.getMasterBehaviorList().addChild(new ObeyOrders(survivor));
		survivor.behaviours.AddTrigger("IdleBoredom", 0.0F, 0.6F, 1.0E-6F, new SatisfyIdleBehavior());
		survivor.getMasterBehaviorList().addChild(new FleeBehaviour());
		new FollowBehaviour();
		AttackBehavior attackBehavior = new AttackBehavior();
		survivor.getMasterBehaviorList().addChild(attackBehavior);
		attackBehavior.process((DecisionPath)null, survivor);
		if (survivor.getPrimaryHandItem() != null) {
			InventoryItem inventoryItem = survivor.getPrimaryHandItem();
			if (inventoryItem instanceof HandWeapon) {
				survivor.setUseHandWeapon((HandWeapon)inventoryItem);
			}
		}

		survivor.getMasterBehaviorList().addChild(survivor.behaviours);
	}

	public int getHuntZombieRange() {
		return 10;
	}
}
