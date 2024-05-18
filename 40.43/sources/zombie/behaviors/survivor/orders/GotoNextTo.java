package zombie.behaviors.survivor.orders;

import java.util.Stack;
import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.LittleTasks.FaceOrder;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.ui.TextManager;
import zombie.ui.UIFont;


public class GotoNextTo extends Order {
	OrderSequence order = null;

	public GotoNextTo(IsoGameCharacter gameCharacter, int int1, int int2, int int3) {
		super(gameCharacter);
		this.order = new OrderSequence(gameCharacter);
		Stack stack = new Stack();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		for (int int4 = -1; int4 <= 1; ++int4) {
			for (int int5 = -1; int5 <= 1; ++int5) {
				if ((int4 == 0 || int5 == 0) && (int4 != 0 || int5 != 0)) {
					IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(int1 + int4, int2 + int5, int3);
					if (square2 != null && square2.isFree(false) && (square == null || square2.getRoom() == square.getRoom())) {
						stack.add(square2);
					}
				}
			}
		}

		if (!stack.isEmpty()) {
			IsoGridSquare square3 = (IsoGridSquare)stack.get(Rand.Next(stack.size()));
			this.order.Orders.add(new GotoOrder(gameCharacter, square3.getX(), square3.getY(), square3.getZ()));
			Vector2 vector2 = new Vector2(0.0F, 0.0F);
			vector2.x = (float)(int1 - square3.getX());
			vector2.y = (float)(int2 - square3.getY());
			this.order.Orders.add(new FaceOrder(gameCharacter, IsoDirections.fromAngle(vector2)));
		}
	}

	public int getAttackIfEnemiesAroundBias() {
		return this.character.getCurrentSquare().getRoom() != null ? -1000 : 0;
	}

	public void update() {
		if (this.order != null) {
			this.order.update();
		}
	}

	public boolean complete() {
		return this.order == null ? true : this.order.complete();
	}

	public Behavior.BehaviorResult process() {
		return this.order == null ? Behavior.BehaviorResult.Failed : this.order.process();
	}

	public int renderDebug(int int1) {
		byte byte1 = 50;
		TextManager.instance.DrawString(UIFont.Small, (double)byte1, (double)int1, "GotoNextTo", 1.0, 1.0, 1.0, 1.0);
		int1 += 30;
		if (this.order != null) {
			this.order.renderDebug(int1);
		}

		return int1;
	}

	public void initOrder() {
		if (this.order != null) {
			this.order.initOrder();
		}
	}

	public float getPriority(IsoGameCharacter gameCharacter) {
		return this.order == null ? -100000.0F : this.order.getPriority(gameCharacter);
	}
}
