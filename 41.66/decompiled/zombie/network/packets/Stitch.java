package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.skills.PerkFactory;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugLogStream;
import zombie.inventory.InventoryItem;
import zombie.network.GameClient;
import zombie.network.PacketValidator;
import zombie.network.packets.hit.Player;
import zombie.network.packets.hit.PlayerBodyPart;
import zombie.network.packets.hit.PlayerItem;

public class Stitch implements INetworkPacket {
   protected final Player wielder = new Player();
   protected final Player target = new Player();
   protected PlayerBodyPart bodyPart = new PlayerBodyPart();
   protected PlayerItem item = new PlayerItem();
   protected float stitchTime = 0.0F;
   protected boolean doIt = false;
   protected boolean infect = false;

   public void set(IsoGameCharacter var1, IsoGameCharacter var2, BodyPart var3, InventoryItem var4, boolean var5) {
      this.wielder.set(var1);
      this.target.set(var2);
      this.bodyPart.set(var3);
      this.item.set(var4);
      this.stitchTime = var3.getStitchTime();
      this.doIt = var5;
      this.infect = var3.isInfectedWound();
   }

   public void parse(ByteBuffer var1, UdpConnection var2) {
      this.wielder.parse(var1, var2);
      this.wielder.parsePlayer(var2);
      this.target.parse(var1, var2);
      this.target.parsePlayer((UdpConnection)null);
      this.bodyPart.parse(var1, this.target.getCharacter());
      this.item.parse(var1, var2);
      var1.putFloat(this.stitchTime);
      var1.put((byte)(this.doIt ? 1 : 0));
      var1.put((byte)(this.infect ? 1 : 0));
   }

   public void write(ByteBufferWriter var1) {
      this.wielder.write(var1);
      this.target.write(var1);
      this.bodyPart.write(var1);
      this.item.write(var1);
      this.stitchTime = var1.bb.getFloat();
      this.doIt = var1.bb.get() == 1;
      this.infect = var1.bb.get() == 1;
   }

   public void process() {
      int var1 = this.wielder.getCharacter().getPerkLevel(PerkFactory.Perks.Doctor);
      if (((IsoPlayer)this.wielder.getCharacter()).getAccessLevel() != "None") {
         var1 = 10;
      }

      byte var2 = 20;
      if (this.doIt) {
         if (this.wielder.getCharacter().getInventory().contains("SutureNeedleHolder") || this.item.getItem().getType() == "SutureNeedle") {
            var2 = 10;
         }
      } else {
         var2 = 5;
      }

      if (this.wielder.getCharacter().HasTrait("Hemophobic")) {
         this.wielder.getCharacter().getStats().setPanic(this.wielder.getCharacter().getStats().getPanic() + 50.0F);
      }

      if (this.item.getItem() != null) {
         this.item.getItem().Use();
      }

      if (this.bodyPart.getBodyPart().isGetStitchXp()) {
         this.wielder.getCharacter().getXp().AddXP(PerkFactory.Perks.Doctor, 15.0F);
      }

      this.bodyPart.getBodyPart().setStitched(this.doIt);
      int var3 = var2 - var1 * 1;
      if (var3 < 0) {
         var3 = 0;
      }

      if (((IsoPlayer)this.wielder.getCharacter()).getAccessLevel() != "None") {
         this.bodyPart.getBodyPart().setAdditionalPain(this.bodyPart.getBodyPart().getAdditionalPain() + (float)var3);
      }

      if (this.doIt) {
         this.bodyPart.getBodyPart().setStitchTime(this.stitchTime);
      }

      if (this.infect) {
         this.bodyPart.getBodyPart().setInfectedWound(true);
      }

   }

   public boolean isConsistent() {
      return this.wielder.getCharacter() != null && this.wielder.getCharacter() instanceof IsoPlayer && this.target.getCharacter() != null && this.target.getCharacter() instanceof IsoPlayer && this.bodyPart.getBodyPart() != null && this.stitchTime < 50.0F && this.stitchTime >= 0.0F;
   }

   public boolean validate(UdpConnection var1) {
      if (!GameClient.bClient || this.bodyPart.getBodyPart().isDeepWounded() && !this.bodyPart.getBodyPart().haveGlass()) {
         return PacketValidator.checkType8(var1, this.wielder, this.target, this.getClass().getSimpleName());
      } else {
         DebugLogStream var10000 = DebugLog.General;
         String var10001 = this.getClass().getSimpleName();
         var10000.warn(var10001 + ": Validate error: " + this.getDescription());
         return false;
      }
   }

   public String getDescription() {
      String var1 = "\n\t" + this.getClass().getSimpleName() + " [";
      var1 = var1 + "wielder=" + this.wielder.getDescription() + " | ";
      var1 = var1 + "target=" + this.target.getDescription() + " | ";
      var1 = var1 + "bodyPart=" + this.bodyPart.getDescription() + " | ";
      var1 = var1 + "item=" + this.item.getDescription() + " | ";
      var1 = var1 + "stitchTime=" + this.stitchTime + " | ";
      var1 = var1 + "doIt=" + this.doIt + " | ";
      var1 = var1 + "infect=" + this.infect + "] ";
      return var1;
   }
}
