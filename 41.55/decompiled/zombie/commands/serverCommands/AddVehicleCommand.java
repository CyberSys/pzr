package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.physics.WorldSimulation;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.VehicleScript;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclesDB2;

@CommandName(
   name = "addvehicle"
)
@CommandArgs(
   required = {"([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9.-]*)"},
   optional = "(.+)"
)
@CommandHelp(
   helpText = "UI_ServerOptionDesc_AddVehicle"
)
@RequiredRight(
   requiredRights = 60
)
public class AddVehicleCommand extends CommandBase {
   public AddVehicleCommand(String var1, String var2, String var3, UdpConnection var4) {
      super(var1, var2, var3, var4);
   }

   protected String Command() {
      String var1 = this.getCommandArg(0);
      String var2;
      if (this.getCommandArgsCount() == 2) {
         var2 = this.getCommandArg(1);
      } else {
         if (this.connection == null) {
            return "Pass a username";
         }

         var2 = this.getExecutorUsername();
      }

      IsoPlayer var6 = GameServer.getPlayerByUserNameForCommand(var2);
      if (var6 == null) {
         return "User \"" + var2 + "\" not found";
      } else {
         int var3 = (int)var6.getX();
         int var4 = (int)var6.getY();
         int var5 = (int)var6.getZ();
         if (var5 > 0) {
            return "Z coordinate must be 0 for now";
         } else {
            VehicleScript var7 = ScriptManager.instance.getVehicle(var1);
            if (var7 == null) {
               return "Unknown vehicle script \"" + var1 + "\"";
            } else {
               String var10000 = var7.getModule().getName();
               var1 = var10000 + "." + var7.getName();
               WorldSimulation.instance.create();
               if (!WorldSimulation.instance.created) {
                  return "Physics couldn't be created";
               } else {
                  IsoGridSquare var8 = ServerMap.instance.getGridSquare(var3, var4, var5);
                  if (var8 == null) {
                     return "Invalid location " + var3 + "," + var4 + "," + var5;
                  } else {
                     BaseVehicle var9 = new BaseVehicle(IsoWorld.instance.CurrentCell);
                     var9.setScriptName(var1);
                     var9.setX((float)var3 + 0.5F);
                     var9.setY((float)var4 + 0.5F);
                     var9.setZ((float)var5 + 0.2F);
                     if (IsoChunk.doSpawnedVehiclesInInvalidPosition(var9)) {
                        var9.setSquare(var8);
                        var9.square.chunk.vehicles.add(var9);
                        var9.chunk = var9.square.chunk;
                        var9.addToWorld();
                        VehiclesDB2.instance.addVehicle(var9);
                        return "Vehicle spawned";
                     } else {
                        return "ERROR: I can not spawn the vehicle. Invalid position. Try to change position.";
                     }
                  }
               }
            }
         }
      }
   }
}