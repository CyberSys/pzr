package zombie.behaviors.survivor.orders;

import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.iso.IsoGridSquare;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoomExit;


public class GotoSafehouseOrder extends GotoOrder {
	IsoBuilding b;
	IsoGameCharacter chr;

	public GotoSafehouseOrder(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
		this.chr = gameCharacter;
	}

	public void initOrder() {
		this.b = this.chr.getDescriptor().getGroup().Safehouse;
		IsoRoomExit roomExit = (IsoRoomExit)this.b.Exits.get(Rand.Next(this.b.Exits.size()));
		if (roomExit.From == null) {
			roomExit = roomExit.To;
		}

		IsoGridSquare square = roomExit.From.getFreeTile();
		this.init(square.getX(), square.getY(), square.getZ());
	}

	public boolean complete() {
		return super.complete();
	}
}
