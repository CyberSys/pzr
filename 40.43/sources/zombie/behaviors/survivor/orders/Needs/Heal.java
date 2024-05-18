package zombie.behaviors.survivor.orders.Needs;

import java.util.Stack;
import zombie.behaviors.survivor.orders.ObtainItem;
import zombie.behaviors.survivor.orders.OrderSequence;
import zombie.behaviors.survivor.orders.LittleTasks.EatFoodOrder;
import zombie.characters.IsoGameCharacter;
import zombie.characters.Moodles.MoodleType;


public class Heal extends OrderSequence {
	public Stack Items = new Stack();
	ObtainItem obtain = null;

	public Heal(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
		this.type = "Heal";
	}

	public void initOrder() {
		this.Items.add("Type:Food");
		if (this.character.getInventory().getBestFood(this.character.getDescriptor()) == null) {
			this.obtain = new ObtainItem(this.character, this.Items, 1000);
			this.Orders.add(this.obtain);
		}

		this.Orders.add(new EatFoodOrder(this.character));
	}

	public boolean isCritical() {
		return this.character.getMoodles().getMoodleLevel(MoodleType.Hungry) > 1 || !(this.character.getBodyDamage().getHealth() > 60.0F);
	}

	public boolean ActedThisFrame() {
		return this.obtain != null && this.Orders.get(this.ID) == this.obtain ? this.obtain.ActedThisFrame() : false;
	}
}
