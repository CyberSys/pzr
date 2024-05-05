package zombie.commands.serverCommands;

import zombie.Lua.LuaManager;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.Rand;
import zombie.core.logger.LoggerManager;
import zombie.core.math.PZMath;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.util.StringUtils;

@CommandName(
   name = "createhorde2"
)
@CommandArgs(
   varArgs = true
)
@CommandHelp(
   helpText = "UI_ServerOptionDesc_CreateHorde2"
)
@RequiredRight(
   requiredRights = 44
)
public class CreateHorde2Command extends CommandBase {
   public CreateHorde2Command(String var1, String var2, String var3, UdpConnection var4) {
      super(var1, var2, var3, var4);
   }

   protected String Command() {
      int var1 = -1;
      int var2 = -1;
      int var3 = -1;
      int var4 = -1;
      int var5 = -1;
      String var6 = null;

      for(int var7 = 0; var7 < this.getCommandArgsCount() - 1; var7 += 2) {
         String var8 = this.getCommandArg(var7);
         String var9 = this.getCommandArg(var7 + 1);
         byte var11 = -1;
         switch(var8.hashCode()) {
         case 1515:
            if (var8.equals("-x")) {
               var11 = 2;
            }
            break;
         case 1516:
            if (var8.equals("-y")) {
               var11 = 3;
            }
            break;
         case 1517:
            if (var8.equals("-z")) {
               var11 = 4;
            }
            break;
         case 277437552:
            if (var8.equals("-outfit")) {
               var11 = 5;
            }
            break;
         case 344381183:
            if (var8.equals("-radius")) {
               var11 = 1;
            }
            break;
         case 1383163138:
            if (var8.equals("-count")) {
               var11 = 0;
            }
         }

         switch(var11) {
         case 0:
            var1 = PZMath.tryParseInt(var9, -1);
            break;
         case 1:
            var2 = PZMath.tryParseInt(var9, -1);
            break;
         case 2:
            var3 = PZMath.tryParseInt(var9, -1);
            break;
         case 3:
            var4 = PZMath.tryParseInt(var9, -1);
            break;
         case 4:
            var5 = PZMath.tryParseInt(var9, -1);
            break;
         case 5:
            var6 = StringUtils.discardNullOrWhitespace(var9);
            break;
         default:
            return this.getHelp();
         }
      }

      var1 = PZMath.clamp(var1, 1, 500);
      IsoGridSquare var12 = IsoWorld.instance.CurrentCell.getGridSquare(var3, var4, var5);
      if (var12 == null) {
         return "invalid location";
      } else if (var6 != null && OutfitManager.instance.FindMaleOutfit(var6) == null && OutfitManager.instance.FindFemaleOutfit(var6) == null) {
         return "invalid outfit";
      } else {
         for(int var13 = 0; var13 < var1; ++var13) {
            int var14 = var2 <= 0 ? var3 : Rand.Next(var3 - var2, var3 + var2 + 1);
            int var10 = var2 <= 0 ? var4 : Rand.Next(var4 - var2, var4 + var2 + 1);
            LuaManager.GlobalObject.addZombiesInOutfit(var14, var10, var5, 1, var6, (Integer)null);
         }

         LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " created a horde of " + var1 + " zombies near " + var3 + "," + var4, "IMPORTANT");
         return "Horde spawned.";
      }
   }
}
