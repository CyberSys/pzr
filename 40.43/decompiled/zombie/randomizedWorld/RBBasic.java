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

   public void randomizeBuilding(BuildingDef var1) {
      boolean var2 = Rand.Next(100) <= 20;
      ArrayList var3 = new ArrayList();
      KahluaTable var4 = (KahluaTable)LuaManager.env.rawget("ItemPicker");
      KahluaTable var5 = (KahluaTable)LuaManager.env.rawget("SuburbsDistributions");
      String var6 = (String)this.specificProfessionDistribution.get(Rand.Next(0, this.specificProfessionDistribution.size()));
      KahluaTable var7 = (KahluaTable)var5.rawget(var6);
      IsoCell var8 = IsoWorld.instance.CurrentCell;

      for(int var9 = var1.x - 1; var9 < var1.x2 + 1; ++var9) {
         for(int var10 = var1.y - 1; var10 < var1.y2 + 1; ++var10) {
            for(int var11 = 0; var11 < 8; ++var11) {
               IsoGridSquare var12 = var8.getGridSquare(var9, var10, var11);
               if (var12 != null) {
                  for(int var13 = 0; var13 < var12.getObjects().size(); ++var13) {
                     IsoObject var14 = (IsoObject)var12.getObjects().get(var13);
                     if (Rand.Next(100) <= 65 && var14 instanceof IsoDoor && !((IsoDoor)var14).isExteriorDoor((IsoGameCharacter)null)) {
                        ((IsoDoor)var14).ToggleDoorSilent();
                        ((IsoDoor)var14).syncIsoObject(true, (byte)1, (UdpConnection)null, (ByteBuffer)null);
                     }

                     if (var2 && Rand.Next(100) <= 70 && var14.getContainer() != null && var12.getRoom() != null && var12.getRoom().getName() != null && ((String)this.specificProfessionRoomDistribution.get(var6)).contains(var12.getRoom().getName()) && var7.rawget(var14.getContainer().getType()) != null) {
                        var14.getContainer().clear();
                        var3.add(var14.getContainer());
                        var14.getContainer().setExplored(true);
                     }

                     if (Rand.Next(100) < 15 && var14.getContainer() != null && var14.getContainer().getType().equals("stove")) {
                        InventoryItem var15 = var14.getContainer().AddItem((String)this.coldFood.get(Rand.Next(0, this.coldFood.size())));
                        var15.setCooked(true);
                        var15.setAutoAge();
                     }
                  }
               }
            }
         }
      }

      Iterator var16 = var3.iterator();

      while(var16.hasNext()) {
         ItemContainer var17 = (ItemContainer)var16.next();
         LuaManager.caller.pcall(LuaManager.thread, var4.rawget("fillContainerType"), var7, var17, "", null);
         if (GameServer.bServer) {
            GameServer.sendItemsInContainer(var17.getParent(), var17);
         }
      }

      if (Rand.Next(100) < 25) {
         this.addRandomDeadSurvivor(var1);
         var1.setAllExplored(true);
         var1.bAlarmed = false;
      }

   }

   private void addRandomDeadSurvivor(BuildingDef var1) {
      RandomizedDeadSurvivorBase var2 = (RandomizedDeadSurvivorBase)this.deadSurvivors.get(Rand.Next(0, this.deadSurvivors.size()));
      var2.randomizeDeadSurvivor(var1);
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
