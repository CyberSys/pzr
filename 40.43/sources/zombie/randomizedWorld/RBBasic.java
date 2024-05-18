package zombie.randomizedWorld;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoDoor;
import zombie.network.GameServer;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBleach;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSDeadDrunk;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSGunmanInBathroom;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSGunslinger;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSpecificProfession;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSZombieLockedBathroom;
import zombie.randomizedWorld.randomizedDeadSurvivor.RandomizedDeadSurvivorBase;


public class RBBasic extends RandomizedBuildingBase {
	private ArrayList specificProfessionDistribution = new ArrayList();
	private Map specificProfessionRoomDistribution = new HashMap();
	private ArrayList coldFood = new ArrayList();
	private ArrayList deadSurvivors = new ArrayList();

	public void randomizeBuilding(BuildingDef buildingDef) {
		boolean boolean1 = Rand.Next(100) <= 20;
		ArrayList arrayList = new ArrayList();
		KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("ItemPicker");
		KahluaTable kahluaTable2 = (KahluaTable)LuaManager.env.rawget("SuburbsDistributions");
		String string = (String)this.specificProfessionDistribution.get(Rand.Next(0, this.specificProfessionDistribution.size()));
		KahluaTable kahluaTable3 = (KahluaTable)kahluaTable2.rawget(string);
		IsoCell cell = IsoWorld.instance.CurrentCell;
		for (int int1 = buildingDef.x - 1; int1 < buildingDef.x2 + 1; ++int1) {
			for (int int2 = buildingDef.y - 1; int2 < buildingDef.y2 + 1; ++int2) {
				for (int int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
					if (square != null) {
						for (int int4 = 0; int4 < square.getObjects().size(); ++int4) {
							IsoObject object = (IsoObject)square.getObjects().get(int4);
							if (Rand.Next(100) <= 65 && object instanceof IsoDoor && !((IsoDoor)object).isExteriorDoor((IsoGameCharacter)null)) {
								((IsoDoor)object).ToggleDoorSilent();
								((IsoDoor)object).syncIsoObject(true, (byte)1, (UdpConnection)null, (ByteBuffer)null);
							}

							if (boolean1 && Rand.Next(100) <= 70 && object.getContainer() != null && square.getRoom() != null && square.getRoom().getName() != null && ((String)this.specificProfessionRoomDistribution.get(string)).contains(square.getRoom().getName()) && kahluaTable3.rawget(object.getContainer().getType()) != null) {
								object.getContainer().clear();
								arrayList.add(object.getContainer());
								object.getContainer().setExplored(true);
							}

							if (Rand.Next(100) < 15 && object.getContainer() != null && object.getContainer().getType().equals("stove")) {
								InventoryItem inventoryItem = object.getContainer().AddItem((String)this.coldFood.get(Rand.Next(0, this.coldFood.size())));
								inventoryItem.setCooked(true);
								inventoryItem.setAutoAge();
							}
						}
					}
				}
			}
		}

		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			ItemContainer itemContainer = (ItemContainer)iterator.next();
			LuaManager.caller.pcall(LuaManager.thread, kahluaTable.rawget("fillContainerType"), kahluaTable3, itemContainer, "", null);
			if (GameServer.bServer) {
				GameServer.sendItemsInContainer(itemContainer.getParent(), itemContainer);
			}
		}

		if (Rand.Next(100) < 25) {
			this.addRandomDeadSurvivor(buildingDef);
			buildingDef.setAllExplored(true);
			buildingDef.bAlarmed = false;
		}
	}

	private void addRandomDeadSurvivor(BuildingDef buildingDef) {
		RandomizedDeadSurvivorBase randomizedDeadSurvivorBase = (RandomizedDeadSurvivorBase)this.deadSurvivors.get(Rand.Next(0, this.deadSurvivors.size()));
		randomizedDeadSurvivorBase.randomizeDeadSurvivor(buildingDef);
	}

	public RBBasic() {
		this.deadSurvivors.add(new RDSBleach());
		this.deadSurvivors.add(new RDSGunslinger());
		this.deadSurvivors.add(new RDSGunmanInBathroom());
		this.deadSurvivors.add(new RDSZombieLockedBathroom());
		this.deadSurvivors.add(new RDSDeadDrunk());
		this.deadSurvivors.add(new RDSSpecificProfession());
		this.specificProfessionDistribution.add("Carpenter");
		this.specificProfessionDistribution.add("Electrician");
		this.specificProfessionDistribution.add("Farmer");
		this.specificProfessionDistribution.add("Nurse");
		this.specificProfessionRoomDistribution.put("Carpenter", "kitchen");
		this.specificProfessionRoomDistribution.put("Electrician", "kitchen");
		this.specificProfessionRoomDistribution.put("Farmer", "kitchen");
		this.specificProfessionRoomDistribution.put("Nurse", "kitchen");
		this.specificProfessionRoomDistribution.put("Nurse", "bathroom");
		this.coldFood.add("Base.Chicken");
		this.coldFood.add("Base.Steak");
		this.coldFood.add("Base.PorkChop");
		this.coldFood.add("Base.MuttonChop");
		this.coldFood.add("Base.MeatPatty");
		this.coldFood.add("Base.FishFillet");
		this.coldFood.add("Base.Salmon");
	}
}
