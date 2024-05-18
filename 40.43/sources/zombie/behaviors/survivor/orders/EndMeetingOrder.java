package zombie.behaviors.survivor.orders;

import zombie.characters.IsoGameCharacter;
import zombie.characters.SurvivorDesc;


public class EndMeetingOrder extends OrderSequence {
	IsoGameCharacter chr;

	public EndMeetingOrder(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
		this.chr = gameCharacter;
	}

	public boolean complete() {
		return true;
	}

	public void initOrder() {
		for (int int1 = 0; int1 < this.chr.getDescriptor().getGroup().Members.size(); ++int1) {
			SurvivorDesc survivorDesc = (SurvivorDesc)this.chr.getDescriptor().getGroup().Members.get(int1);
			if (survivorDesc != this.chr.getDescriptor() && survivorDesc.getInstance().InRoomWith(this.chr) && survivorDesc.getInstance().getOrder() instanceof GuardOrder) {
				survivorDesc.getInstance().getOrders().pop();
			}
		}
	}
}
