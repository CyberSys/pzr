package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.PacketValidator;
import zombie.network.packets.hit.Perk;

public class AddXp implements INetworkPacket {
   public final PlayerID target = new PlayerID();
   protected Perk perk = new Perk();
   protected int amount = 0;

   public void set(IsoPlayer var1, PerkFactory.Perk var2, int var3) {
      this.target.set(var1);
      this.perk.set(var2);
      this.amount = var3;
   }

   public void parse(ByteBuffer var1, UdpConnection var2) {
      this.target.parse(var1, var2);
      this.target.parsePlayer(var2);
      this.perk.parse(var1, var2);
      this.amount = var1.getInt();
   }

   public void write(ByteBufferWriter var1) {
      this.target.write(var1);
      this.perk.write(var1);
      var1.putInt(this.amount);
   }

   public void process() {
      if (this.target.player != null && !this.target.player.isDead()) {
         if (this.target.getCharacter() != null && !this.target.getCharacter().isDead()) {
            this.target.getCharacter().getXp().AddXP(this.perk.getPerk(), (float)this.amount, false, false, true);
         }

      }
   }

   public boolean isConsistent() {
      return this.target.isConsistent() && this.perk.isConsistent();
   }

   public boolean validate(UdpConnection var1) {
      if (var1.accessLevel != 1 && var1.accessLevel != 2) {
         return true;
      } else if (!var1.havePlayer(this.target.getCharacter())) {
         if (PacketValidator.doKickUser(var1, this.getClass().getSimpleName(), "UI_ValidationFailed_Type14")) {
            LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\"", var1.username, "UI_ValidationFailed_Type14", this.getClass().getSimpleName()));
         }

         return false;
      } else if ((float)this.amount > 1000.0F) {
         if (PacketValidator.doKickUser(var1, this.getClass().getSimpleName(), "UI_ValidationFailed_Type15")) {
            LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\"", var1.username, "UI_ValidationFailed_Type15", this.getClass().getSimpleName()));
         }

         return false;
      } else {
         return true;
      }
   }

   public String getDescription() {
      String var1 = "\n\t" + this.getClass().getSimpleName() + " [";
      var1 = var1 + "target=" + this.target.getDescription() + " | ";
      var1 = var1 + "perk=" + this.perk.getDescription() + " | ";
      var1 = var1 + "amount=" + this.amount + "] ";
      return var1;
   }
}
