package zombie.chat;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.network.chat.ChatType;

public final class ChatUtility {
   private static final boolean useEuclidean = true;

   private ChatUtility() {
   }

   public static float getScrambleValue(IsoObject var0, IsoPlayer var1, float var2) {
      return getScrambleValue(var0.getX(), var0.getY(), var0.getZ(), var0.getSquare(), var1, var2);
   }

   public static float getScrambleValue(float var0, float var1, float var2, IsoGridSquare var3, IsoPlayer var4, float var5) {
      float var6 = 1.0F;
      boolean var7 = false;
      boolean var8 = false;
      if (var3 != null && var4.getSquare() != null) {
         if (var4.getBuilding() != null && var3.getBuilding() != null && var4.getBuilding() == var3.getBuilding()) {
            if (var4.getSquare().getRoom() == var3.getRoom()) {
               var6 = (float)((double)var6 * 2.0D);
               var8 = true;
            } else if (Math.abs(var4.getZ() - var2) < 1.0F) {
               var6 = (float)((double)var6 * 2.0D);
            }
         } else if (var4.getBuilding() != null || var3.getBuilding() != null) {
            var6 = (float)((double)var6 * 0.5D);
            var7 = true;
         }

         if (Math.abs(var4.getZ() - var2) >= 1.0F) {
            var6 = (float)((double)var6 - (double)var6 * (double)Math.abs(var4.getZ() - var2) * 0.25D);
            var7 = true;
         }
      }

      float var9 = var5 * var6;
      float var10 = 1.0F;
      if (var6 > 0.0F && playerWithinBounds(var0, var1, var4, var9)) {
         float var11 = getDistance(var0, var1, var4);
         if (var11 >= 0.0F && var11 < var9) {
            float var12 = var9 * 0.6F;
            if (!var8 && (var7 || !(var11 < var12))) {
               if (var9 - var12 != 0.0F) {
                  var10 = (var11 - var12) / (var9 - var12);
                  if (var10 < 0.2F) {
                     var10 = 0.2F;
                  }
               }
            } else {
               var10 = 0.0F;
            }
         }
      }

      return var10;
   }

   public static boolean playerWithinBounds(IsoObject var0, IsoObject var1, float var2) {
      return playerWithinBounds(var0.getX(), var0.getY(), var1, var2);
   }

   public static boolean playerWithinBounds(float var0, float var1, IsoObject var2, float var3) {
      if (var2 == null) {
         return false;
      } else {
         return var2.getX() > var0 - var3 && var2.getX() < var0 + var3 && var2.getY() > var1 - var3 && var2.getY() < var1 + var3;
      }
   }

   public static float getDistance(IsoObject var0, IsoPlayer var1) {
      return var1 == null ? -1.0F : (float)Math.sqrt(Math.pow((double)(var0.getX() - var1.x), 2.0D) + Math.pow((double)(var0.getY() - var1.y), 2.0D));
   }

   public static float getDistance(float var0, float var1, IsoPlayer var2) {
      return var2 == null ? -1.0F : (float)Math.sqrt(Math.pow((double)(var0 - var2.x), 2.0D) + Math.pow((double)(var1 - var2.y), 2.0D));
   }

   public static UdpConnection findConnection(int var0) {
      UdpConnection var1 = null;
      if (GameServer.udpEngine != null) {
         for(int var2 = 0; var2 < GameServer.udpEngine.connections.size(); ++var2) {
            UdpConnection var3 = (UdpConnection)GameServer.udpEngine.connections.get(var2);

            for(int var4 = 0; var4 < var3.playerIDs.length; ++var4) {
               if (var3.playerIDs[var4] == var0) {
                  var1 = var3;
                  break;
               }
            }
         }
      }

      if (var1 == null) {
         DebugLog.log("Connection with PlayerID ='" + var0 + "' not found!");
      }

      return var1;
   }

   public static UdpConnection findConnection(String var0) {
      UdpConnection var1 = null;
      if (GameServer.udpEngine != null) {
         for(int var2 = 0; var2 < GameServer.udpEngine.connections.size() && var1 == null; ++var2) {
            UdpConnection var3 = (UdpConnection)GameServer.udpEngine.connections.get(var2);

            for(int var4 = 0; var4 < var3.players.length; ++var4) {
               if (var3.players[var4] != null && var3.players[var4].username.equalsIgnoreCase(var0)) {
                  var1 = var3;
                  break;
               }
            }
         }
      }

      if (var1 == null) {
         DebugLog.log("Player with nickname = '" + var0 + "' not found!");
      }

      return var1;
   }

   public static IsoPlayer findPlayer(int var0) {
      IsoPlayer var1 = null;
      if (GameServer.udpEngine != null) {
         for(int var2 = 0; var2 < GameServer.udpEngine.connections.size(); ++var2) {
            UdpConnection var3 = (UdpConnection)GameServer.udpEngine.connections.get(var2);

            for(int var4 = 0; var4 < var3.playerIDs.length; ++var4) {
               if (var3.playerIDs[var4] == var0) {
                  var1 = var3.players[var4];
                  break;
               }
            }
         }
      }

      if (var1 == null) {
         DebugLog.log("Player with PlayerID ='" + var0 + "' not found!");
      }

      return var1;
   }

   public static String findPlayerName(int var0) {
      return findPlayer(var0).getUsername();
   }

   public static IsoPlayer findPlayer(String var0) {
      IsoPlayer var1 = null;
      if (GameClient.bClient) {
         var1 = GameClient.instance.getPlayerFromUsername(var0);
      } else if (GameServer.bServer) {
         var1 = GameServer.getPlayerByUserName(var0);
      }

      if (var1 == null) {
         DebugLog.log("Player with nickname = '" + var0 + "' not found!");
      }

      return var1;
   }

   public static ArrayList getAllowedChatStreams() {
      String var0 = ServerOptions.getInstance().ChatStreams.getValue();
      var0 = var0.replaceAll("\"", "");
      String[] var1 = var0.split(",");
      ArrayList var2 = new ArrayList();
      var2.add(ChatType.server);
      String[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         byte var8 = -1;
         switch(var6.hashCode()) {
         case 97:
            if (var6.equals("a")) {
               var8 = 2;
            }
            break;
         case 102:
            if (var6.equals("f")) {
               var8 = 6;
            }
            break;
         case 114:
            if (var6.equals("r")) {
               var8 = 1;
            }
            break;
         case 115:
            if (var6.equals("s")) {
               var8 = 0;
            }
            break;
         case 119:
            if (var6.equals("w")) {
               var8 = 3;
            }
            break;
         case 121:
            if (var6.equals("y")) {
               var8 = 4;
            }
            break;
         case 3669:
            if (var6.equals("sh")) {
               var8 = 5;
            }
            break;
         case 96673:
            if (var6.equals("all")) {
               var8 = 7;
            }
         }

         switch(var8) {
         case 0:
            var2.add(ChatType.say);
            break;
         case 1:
            var2.add(ChatType.radio);
            break;
         case 2:
            var2.add(ChatType.admin);
            break;
         case 3:
            var2.add(ChatType.whisper);
            break;
         case 4:
            var2.add(ChatType.shout);
            break;
         case 5:
            var2.add(ChatType.safehouse);
            break;
         case 6:
            var2.add(ChatType.faction);
            break;
         case 7:
            var2.add(ChatType.general);
         }
      }

      return var2;
   }

   public static boolean chatStreamEnabled(ChatType var0) {
      ArrayList var1 = getAllowedChatStreams();
      return var1.contains(var0);
   }
}
