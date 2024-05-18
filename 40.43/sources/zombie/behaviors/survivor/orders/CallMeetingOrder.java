package zombie.behaviors.survivor.orders;

import java.util.Stack;
import zombie.behaviors.survivor.orders.LittleTasks.GotoRoomOrder;
import zombie.characters.IsoGameCharacter;
import zombie.characters.SurvivorDesc;
import zombie.iso.IsoGridSquare;


public class CallMeetingOrder extends OrderSequence {
	IsoGameCharacter chr;
	Stack characters = new Stack();
	int timeout = 500;

	public CallMeetingOrder(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
		this.chr = gameCharacter;
	}

	public void update() {
		--this.timeout;
		super.update();
	}

	public boolean complete() {
		if (this.timeout < 0) {
			return true;
		} else {
			for (int int1 = 0; int1 < this.characters.size(); ++int1) {
				if (!((IsoGameCharacter)this.characters.get(int1)).isDead() && !((IsoGameCharacter)this.characters.get(int1)).InRoomWith(this.chr)) {
					return false;
				}
			}

			return true;
		}
	}

	public void initOrder() {
		for (int int1 = 0; int1 < this.chr.getDescriptor().getGroup().Members.size(); ++int1) {
			SurvivorDesc survivorDesc = (SurvivorDesc)this.chr.getDescriptor().getGroup().Members.get(int1);
			if (survivorDesc != this.chr.getDescriptor() && survivorDesc != null && survivorDesc.getInstance().InBuildingWith(this.chr)) {
				IsoGridSquare square = this.chr.getCurrentSquare().getRoom().getFreeTile();
				survivorDesc.getInstance().GiveOrder(new GuardOrder(survivorDesc.getInstance()), false);
				survivorDesc.getInstance().GiveOrder(new GotoRoomOrder(survivorDesc.getInstance(), this.chr.getCurrentSquare().getRoom()), false);
				survivorDesc.getInstance().GiveOrder(new GotoOrder(survivorDesc.getInstance(), square.getX(), square.getY(), square.getZ()), false);
				this.characters.add(survivorDesc.getInstance());
			}
		}

		this.Orders.add(new GuardOrder(this.chr));
	}
}
