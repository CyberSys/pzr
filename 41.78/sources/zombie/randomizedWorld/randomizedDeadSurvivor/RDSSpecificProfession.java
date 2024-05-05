package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import java.util.List;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.ItemPickerJava;
import zombie.iso.BuildingDef;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoDeadBody;
import zombie.util.list.PZArrayUtil;


public final class RDSSpecificProfession extends RandomizedDeadSurvivorBase {
	private final ArrayList specificProfessionDistribution = new ArrayList();

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		IsoGridSquare square = buildingDef.getFreeSquareInRoom();
		if (square != null) {
			IsoDeadBody deadBody = createRandomDeadBody(square.getX(), square.getY(), square.getZ(), (IsoDirections)null, 0);
			if (deadBody != null) {
				ItemPickerJava.ItemPickerRoom itemPickerRoom = (ItemPickerJava.ItemPickerRoom)ItemPickerJava.rooms.get(PZArrayUtil.pickRandom((List)this.specificProfessionDistribution));
				ItemPickerJava.rollItem((ItemPickerJava.ItemPickerContainer)itemPickerRoom.Containers.get("counter"), deadBody.getContainer(), true, (IsoGameCharacter)null, (ItemPickerJava.ItemPickerRoom)null);
			}
		}
	}

	public RDSSpecificProfession() {
		this.specificProfessionDistribution.add("Carpenter");
		this.specificProfessionDistribution.add("Electrician");
		this.specificProfessionDistribution.add("Farmer");
		this.specificProfessionDistribution.add("Nurse");
	}
}
