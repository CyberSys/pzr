package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.survivor.orders.GotoOrder;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoGridSquare;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.IsoRoomExit;


public class GotoRoomOrder extends GotoOrder {
	IsoRoom b;
	IsoGameCharacter chr;

	public GotoRoomOrder(IsoGameCharacter gameCharacter, IsoRoom room) {
		super(gameCharacter);
		this.b = room;
		this.chr = gameCharacter;
		if (!room.Exits.isEmpty()) {
			IsoRoomExit roomExit = (IsoRoomExit)room.Exits.get(0);
			if (roomExit.From == null) {
				roomExit = roomExit.To;
			}

			IsoGridSquare square = roomExit.From.getFreeTile();
			this.init(square.getX(), square.getY(), square.getZ());
		}
	}

	public boolean complete() {
		return super.complete();
	}
}
