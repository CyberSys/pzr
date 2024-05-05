package zombie.commands.serverCommands;

import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoWorld;

@CommandName(
   name = "chopper"
)
@CommandHelp(
   helpText = "UI_ServerOptionDesc_Chopper"
)
@RequiredRight(
   requiredRights = 60
)
public class ChopperCommand extends CommandBase {
   public ChopperCommand(String var1, String var2, String var3, UdpConnection var4) {
      super(var1, var2, var3, var4);
   }

   protected String Command() {
      IsoWorld.instance.helicopter.pickRandomTarget();
      LoggerManager.getLogger("admin").write(this.getExecutorUsername() + " did chopper");
      return "Choppers launched";
   }
}
