package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoDeadBody;


public class RDSSpecificProfession extends RandomizedDeadSurvivorBase {
	private ArrayList specificProfessionDistribution = new ArrayList();

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		IsoGridSquare square = buildingDef.getFreeSquareInRoom();
		if (square != null) {
			IsoDeadBody deadBody = super.createRandomDeadBody(square.getX(), square.getY(), square.getZ());
			KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("ItemPicker");
			KahluaTable kahluaTable2 = (KahluaTable)LuaManager.env.rawget("SuburbsDistributions");
			KahluaTable kahluaTable3 = (KahluaTable)kahluaTable2.rawget(this.specificProfessionDistribution.get(Rand.Next(0, this.specificProfessionDistribution.size())));
			LuaManager.caller.pcall(LuaManager.thread, kahluaTable.rawget("rollItem"), kahluaTable3.rawget("counter"), deadBody.getContainer(), "", null);
		}
	}

	public RDSSpecificProfession() {
		this.specificProfessionDistribution.add("Carpenter");
		this.specificProfessionDistribution.add("Electrician");
		this.specificProfessionDistribution.add("Farmer");
		this.specificProfessionDistribution.add("Nurse");
	}
}
