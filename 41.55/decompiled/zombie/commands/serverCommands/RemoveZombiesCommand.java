package zombie.commands.serverCommands;

import zombie.characters.IsoZombie;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.math.PZMath;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.util.StringUtils;
import zombie.util.Type;

@CommandName(
   name = "removezombies"
)
@CommandArgs(
   varArgs = true
)
@CommandHelp(
   helpText = "UI_ServerOptionDesc_RemoveZombies"
)
@RequiredRight(
   requiredRights = 44
)
public class RemoveZombiesCommand extends CommandBase {
   public RemoveZombiesCommand(String var1, String var2, String var3, UdpConnection var4) {
      super(var1, var2, var3, var4);
   }

   protected String Command() {
      int var1 = -1;
      int var2 = -1;
      int var3 = -1;
      int var4 = -1;
      boolean var5 = false;

      int var6;
      for(var6 = 0; var6 < this.getCommandArgsCount() - 1; var6 += 2) {
         String var7 = this.getCommandArg(var6);
         String var8 = this.getCommandArg(var6 + 1);
         byte var10 = -1;
         switch(var7.hashCode()) {
         case 1515:
            if (var7.equals("-x")) {
               var10 = 2;
            }
            break;
         case 1516:
            if (var7.equals("-y")) {
               var10 = 3;
            }
            break;
         case 1517:
            if (var7.equals("-z")) {
               var10 = 4;
            }
            break;
         case 344381183:
            if (var7.equals("-radius")) {
               var10 = 0;
            }
            break;
         case 1149354179:
            if (var7.equals("-reanimated")) {
               var10 = 1;
            }
         }

         switch(var10) {
         case 0:
            var1 = PZMath.tryParseInt(var8, -1);
            break;
         case 1:
            var5 = StringUtils.tryParseBoolean(var8);
            break;
         case 2:
            var2 = PZMath.tryParseInt(var8, -1);
            break;
         case 3:
            var3 = PZMath.tryParseInt(var8, -1);
            break;
         case 4:
            var4 = PZMath.tryParseInt(var8, -1);
            break;
         default:
            return this.getHelp();
         }
      }

      if (var4 >= 0 && var4 < 8) {
         for(var6 = var3 - var1; var6 <= var3 + var1; ++var6) {
            for(int var11 = var2 - var1; var11 <= var2 + var1; ++var11) {
               IsoGridSquare var12 = IsoWorld.instance.CurrentCell.getGridSquare(var11, var6, var4);
               if (var12 != null && !var12.getMovingObjects().isEmpty()) {
                  for(int var9 = var12.getMovingObjects().size() - 1; var9 >= 0; --var9) {
                     IsoZombie var13 = (IsoZombie)Type.tryCastTo((IsoMovingObject)var12.getMovingObjects().get(var9), IsoZombie.class);
                     if (var13 != null && (var5 || !var13.isReanimatedPlayer())) {
                        var13.removeFromWorld();
                        var13.removeFromSquare();
                     }
                  }
               }
            }
         }

         LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " removed zombies near " + var2 + "," + var3, "IMPORTANT");
         return "Zombies removed.";
      } else {
         return "invalid z";
      }
   }
}
