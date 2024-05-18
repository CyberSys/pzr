package zombie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.iso.SliceY;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;

public class SharedDescriptors {
   private static SharedDescriptors.Descriptor[] Descriptors;
   private static final int DESCRIPTOR_COUNT = 500;
   private static final int DESCRIPTOR_ID_START = 500;
   private static byte[] DESCRIPTOR_MAGIC = new byte[]{68, 69, 83, 67};
   private static SharedDescriptors.Descriptor[] PlayerZombieDescriptors = new SharedDescriptors.Descriptor[10];
   private static final int FIRST_PLAYER_ZOMBIE_DESCRIPTOR_ID = 1000;

   public static void initSharedDescriptors() {
      if (GameServer.bServer) {
         Descriptors = new SharedDescriptors.Descriptor[500];

         for(int var0 = 0; var0 < Descriptors.length; ++var0) {
            Descriptors[var0] = new SharedDescriptors.Descriptor();
            Descriptors[var0].desc = SurvivorFactory.CreateSurvivor();
            Descriptors[var0].desc.setID(500 + var0);
            Descriptors[var0].palette = Rand.Next(3) + 1;
         }

         if (!loadSharedDescriptors()) {
            saveSharedDescriptors();
         }
      }
   }

   private static boolean loadSharedDescriptors() {
      if (!GameServer.bServer) {
         return false;
      } else {
         File var0 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "descriptors.bin");
         if (!var0.exists()) {
            return false;
         } else {
            FileInputStream var1 = null;

            try {
               var1 = new FileInputStream(var0);
            } catch (Exception var20) {
               var20.printStackTrace();
               return false;
            }

            boolean var2 = false;

            try {
               if (SliceY.SliceBuffer == null) {
                  SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
               }

               synchronized(SliceY.SliceBuffer) {
                  SliceY.SliceBuffer.clear();
                  var1.read(SliceY.SliceBuffer.array());
                  ByteBuffer var4 = SliceY.SliceBuffer;
                  byte[] var5 = new byte[4];
                  var4.get(var5);
                  if (!Arrays.equals(var5, DESCRIPTOR_MAGIC)) {
                     throw new IOException("not magic");
                  }

                  int var6 = var4.getInt();
                  short var7 = var4.getShort();
                  int var24 = Math.min(var7, 500);

                  for(int var8 = 0; var8 < var24; ++var8) {
                     Descriptors[var8].desc.loadCompact(var4);
                     Descriptors[var8].desc.setID(500 + var8);
                     Descriptors[var8].palette = var4.get() & 255;
                  }

                  var2 = true;
               }
            } catch (Exception var22) {
               var22.printStackTrace();
               var2 = false;
            } finally {
               try {
                  var1.close();
               } catch (IOException var19) {
                  var19.printStackTrace();
                  var2 = false;
               }

            }

            return var2;
         }
      }
   }

   private static void saveSharedDescriptors() {
      if (GameServer.bServer) {
         File var0 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "descriptors.bin");
         FileOutputStream var1 = null;

         try {
            if (SliceY.SliceBuffer == null) {
               SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
            }

            synchronized(SliceY.SliceBuffer) {
               SliceY.SliceBuffer.rewind();
               ByteBuffer var3 = SliceY.SliceBuffer;
               var3.put(DESCRIPTOR_MAGIC);
               var3.putInt(1);
               var3.putShort((short)Descriptors.length);
               SharedDescriptors.Descriptor[] var4 = Descriptors;
               int var5 = var4.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  SharedDescriptors.Descriptor var7 = var4[var6];
                  var7.desc.saveCompact(var3);
                  var3.put((byte)var7.palette);
               }

               var1 = new FileOutputStream(var0);
               var1.write(var3.array(), 0, var3.position());
               var1.flush();
            }
         } catch (Exception var11) {
            var11.printStackTrace();
         }

         try {
            var1.close();
         } catch (IOException var9) {
            var9.printStackTrace();
         }

      }
   }

   public static void setSharedDescriptors(SharedDescriptors.Descriptor[] var0) {
      for(int var1 = 0; var1 < var0.length; ++var1) {
         var0[var1].desc.setID(500 + var1);
      }

      Descriptors = var0;
   }

   public static SharedDescriptors.Descriptor[] getSharedDescriptors() {
      return Descriptors;
   }

   public static SharedDescriptors.Descriptor getDescriptor(int var0) {
      if (var0 >= 500 && var0 < 500 + Descriptors.length) {
         return Descriptors[var0 - 500];
      } else {
         return var0 >= 1000 && var0 < 1000 + PlayerZombieDescriptors.length ? PlayerZombieDescriptors[var0 - 1000] : null;
      }
   }

   public static SharedDescriptors.Descriptor pickRandomDescriptor() {
      if (Descriptors != null && Descriptors.length != 0) {
         int var0 = Rand.Next(Descriptors.length);
         return Descriptors[var0];
      } else {
         return null;
      }
   }

   public static int pickRandomDescriptorID() {
      return Descriptors != null && Descriptors.length != 0 ? 500 + Rand.Next(Descriptors.length) : 0;
   }

   private static void noise(String var0) {
      DebugLog.log("shared-descriptor: " + var0);
   }

   public static void createPlayerZombieDescriptor(IsoZombie var0) {
      if (GameServer.bServer) {
         if (var0.isReanimatedPlayer()) {
            if (var0.getDescriptor().getID() == 0) {
               int var1 = -1;

               for(int var2 = 0; var2 < PlayerZombieDescriptors.length; ++var2) {
                  if (PlayerZombieDescriptors[var2] == null) {
                     var1 = var2;
                     break;
                  }
               }

               if (var1 == -1) {
                  SharedDescriptors.Descriptor[] var7 = new SharedDescriptors.Descriptor[PlayerZombieDescriptors.length + 10];
                  System.arraycopy(PlayerZombieDescriptors, 0, var7, 0, PlayerZombieDescriptors.length);
                  var1 = PlayerZombieDescriptors.length;
                  PlayerZombieDescriptors = var7;
                  noise("resized PlayerZombieDescriptors array size=" + PlayerZombieDescriptors.length);
               }

               var0.getDescriptor().setID(1000 + var1);
               SharedDescriptors.Descriptor var8 = new SharedDescriptors.Descriptor();
               var8.desc = var0.getDescriptor();
               var8.palette = var0.palette;
               PlayerZombieDescriptors[var1] = var8;
               noise("added id=" + var8.desc.getID());

               try {
                  for(int var3 = 0; var3 < GameServer.udpEngine.connections.size(); ++var3) {
                     UdpConnection var4 = (UdpConnection)GameServer.udpEngine.connections.get(var3);
                     ByteBufferWriter var5 = var4.startPacket();
                     PacketTypes.doPacket((short)62, var5);
                     var5.putShort((short)var8.desc.getID());
                     var8.desc.saveCompact(var5.bb);
                     var5.putByte((byte)var8.palette);
                     var4.endPacketImmediate();
                  }
               } catch (IOException var6) {
                  var6.printStackTrace();
               }

            }
         }
      }
   }

   public static void releasePlayerZombieDescriptor(IsoZombie var0) {
      if (GameServer.bServer) {
         if (var0.isReanimatedPlayer()) {
            int var1 = var0.getDescriptor().getID() - 1000;
            if (var1 >= 0 && var1 < PlayerZombieDescriptors.length) {
               noise("released id=" + var0.getDescriptor().getID());
               var0.getDescriptor().setID(0);
               PlayerZombieDescriptors[var1] = null;
            }
         }
      }
   }

   public static SharedDescriptors.Descriptor[] getPlayerZombieDescriptors() {
      return PlayerZombieDescriptors;
   }

   public static void registerPlayerZombieDescriptor(SurvivorDesc var0, int var1) {
      if (GameClient.bClient) {
         int var2 = var0.getID() - 1000;
         if (var2 >= 0 && var2 < 32767) {
            if (PlayerZombieDescriptors.length <= var2) {
               int var3 = (var2 + 10) / 10 * 10;
               SharedDescriptors.Descriptor[] var4 = new SharedDescriptors.Descriptor[var3];
               System.arraycopy(PlayerZombieDescriptors, 0, var4, 0, PlayerZombieDescriptors.length);
               PlayerZombieDescriptors = var4;
               noise("resized PlayerZombieDescriptors array size=" + PlayerZombieDescriptors.length);
            }

            SharedDescriptors.Descriptor var5 = new SharedDescriptors.Descriptor();
            var5.desc = var0;
            var5.palette = var1;
            PlayerZombieDescriptors[var2] = var5;
            noise("registered id=" + var0.getID());
         }
      }
   }

   public static class Descriptor {
      public SurvivorDesc desc;
      public int palette;

      public SurvivorDesc getDesc() {
         return this.desc;
      }

      public int getPalette() {
         return this.palette;
      }
   }
}
