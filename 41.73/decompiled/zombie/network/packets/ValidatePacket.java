package zombie.network.packets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.CRC32;
import zombie.characters.IsoPlayer;
import zombie.commands.PlayerType;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.gameStates.GameLoadingState;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.PacketValidator;
import zombie.network.ServerOptions;
import zombie.network.ServerWorldDatabase;
import zombie.network.Userlog;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Recipe;

public class ValidatePacket implements INetworkPacket {
   private static final Class[] resources = new Class[]{IsoPlayer.class, GameClient.class};
   private int salt;
   private long checksum;
   private long checksumClient;
   private ValidatePacket.ValidateState state;

   public void setSalt(UdpConnection var1) {
      var1.sessionSalt = Rand.Next(Integer.MAX_VALUE);
      this.salt = var1.sessionSalt;
      this.state = ValidatePacket.ValidateState.Request;
   }

   public void process(UdpConnection var1) {
      if (GameClient.bClient) {
         switch(this.state) {
         case Request:
            this.calculateChecksum();
            GameClient.sendValidatePacket(this);
            break;
         case Success:
            var1.checkState = UdpConnection.CheckState.Success;
            GameLoadingState.Done();
         }
      } else if (GameServer.bServer) {
         this.salt = var1.sessionSalt;
         this.calculateChecksum();
         if (this.checksumClient != this.checksum) {
            DebugLog.Multiplayer.trace("Invalid");
         }

         if (this.checksumClient != this.checksum && !isUntouchable(var1) && PacketValidator.doAntiCheatProtection() && ServerOptions.instance.AntiCheatProtectionType21.getValue()) {
            ServerWorldDatabase.instance.addUserlog(var1.username, Userlog.UserlogType.Kicked, "UI_ValidationFailed_Type21", ValidatePacket.class.getSimpleName(), 1);
            GameServer.kick(var1, "UI_Policy_Kick", "UI_ValidationFailed_Type21");
            var1.forceDisconnect(ValidatePacket.class.getSimpleName());
            GameServer.addDisconnect(var1);
            var1.checkState = UdpConnection.CheckState.None;
            var1.checkLimit.Reset(10000L);
            var1.timeSyncLimit.Reset(15000L);
         } else {
            var1.checkState = UdpConnection.CheckState.Success;
            this.state = ValidatePacket.ValidateState.Success;
            if (var1.isFullyConnected()) {
               var1.checkLimit.Reset(Rand.Next(1000L, 10000L));
            } else {
               ByteBufferWriter var2 = var1.startPacket();
               PacketTypes.PacketType.Validate.doPacket(var2);
               this.write(var2);
               PacketTypes.PacketType.Validate.send(var1);
            }

            DebugLog.Multiplayer.trace("Ok %d", var1.checkLimit.getDelay());
         }
      }

   }

   private void calculateChecksum() {
      DebugLog.Multiplayer.trace("in");
      CRC32 var1 = new CRC32();
      var1.update(this.salt);
      ArrayList var2 = ScriptManager.instance.getAllRecipes();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         Recipe var4 = (Recipe)var3.next();
         var1.update(var4.getOriginalname().getBytes());
         var1.update((int)var4.TimeToMake);
         Iterator var5;
         if (var4.skillRequired != null) {
            var5 = var4.skillRequired.iterator();

            while(var5.hasNext()) {
               Recipe.RequiredSkill var6 = (Recipe.RequiredSkill)var5.next();
               var1.update(var6.getPerk().index());
               var1.update(var6.getLevel());
            }
         }

         var5 = var4.getSource().iterator();

         while(var5.hasNext()) {
            Recipe.Source var13 = (Recipe.Source)var5.next();
            Iterator var7 = var13.getItems().iterator();

            while(var7.hasNext()) {
               String var8 = (String)var7.next();
               var1.update(var8.getBytes());
            }
         }

         var1.update(var4.getResult().getType().getBytes());
         var1.update(var4.getResult().getModule().getBytes());
         var1.update(var4.getResult().getCount());
      }

      try {
         Class[] var10 = resources;
         int var11 = var10.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            Class var14 = var10[var12];
            var1.update(var14.getResourceAsStream(var14.getSimpleName() + ".class").readAllBytes());
         }
      } catch (Exception var9) {
      }

      this.checksum = var1.getValue();
      DebugLog.Multiplayer.trace("out %d", this.checksum);
   }

   public void parse(ByteBuffer var1, UdpConnection var2) {
      if (GameClient.bClient) {
         this.state = ValidatePacket.ValidateState.values()[var1.get()];
         this.salt = var1.getInt();
      } else if (GameServer.bServer) {
         this.checksumClient = var1.getLong();
      }

   }

   public void write(ByteBufferWriter var1) {
      if (GameServer.bServer) {
         var1.putByte((byte)this.state.ordinal());
         var1.putInt(this.salt);
      } else if (GameClient.bClient) {
         var1.putLong(this.checksum);
      }

   }

   public boolean isConsistent() {
      return true;
   }

   public String getDescription() {
      return null;
   }

   public static boolean isUntouchable(UdpConnection var0) {
      return Core.bDebug || PlayerType.isPrivileged(var0.accessLevel);
   }

   public static void update(UdpConnection var0) {
      if (GameServer.bServer && var0.isFullyConnected()) {
         switch(var0.checkState) {
         case Sent:
            if (var0.checkLimit.Check()) {
               DebugLog.Multiplayer.trace("Timeout");
               if (ServerOptions.instance.AntiCheatProtectionType22.getValue() && PacketValidator.doKickUser(var0, ValidatePacket.class.getSimpleName(), "UI_ValidationFailed_Type22")) {
                  LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\"", var0.username, "UI_ValidationFailed_Type22", ValidatePacket.class.getSimpleName()));
               }

               var0.checkState = UdpConnection.CheckState.None;
               var0.checkLimit.Reset(10000L);
            }
            break;
         case None:
         case Success:
            if (var0.checkLimit.Check()) {
               DebugLog.Multiplayer.trace("Request");
               GameServer.sendValidatePacket(var0);
               var0.checkState = UdpConnection.CheckState.Sent;
               var0.checkLimit.Reset(2000L);
            }
         }
      }

   }

   public static enum ValidateState {
      Request,
      Success;

      // $FF: synthetic method
      private static ValidatePacket.ValidateState[] $values() {
         return new ValidatePacket.ValidateState[]{Request, Success};
      }
   }
}
