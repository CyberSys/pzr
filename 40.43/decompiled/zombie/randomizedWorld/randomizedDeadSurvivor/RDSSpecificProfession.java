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

   public void randomizeDeadSurvivor(BuildingDef var1) {
      IsoGridSquare var2 = var1.getFreeSquareInRoom();
      if (var2 != null) {
         IsoDeadBody var3 = super.createRandomDeadBody(var2.getX(), var2.getY(), var2.getZ());
         KahluaTable var4 = (KahluaTable)LuaManager.env.rawget("ItemPicker");
         KahluaTable var5 = (KahluaTable)LuaManager.env.rawget("SuburbsDistributions");
         KahluaTable var6 = (KahluaTable)var5.rawget(this.specificProfessionDistribution.get(Rand.Next(0, this.specificProfessionDistribution.size())));
         LuaManager.caller.pcall(LuaManager.thread, var4.rawget("rollItem"), var6.rawget("counter"), var3.getContainer(), "", null);
      }
   }

   public RDSSpecificProfession() {
      this.specificProfessionDistribution.add("Carpenter");
      this.specificProfessionDistribution.add("Electrician");
      this.specificProfessionDistribution.add("Farmer");
      this.specificProfessionDistribution.add("Nurse");
   }
}
