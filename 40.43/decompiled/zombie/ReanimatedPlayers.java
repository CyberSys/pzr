package zombie;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.ai.states.ZombieStandState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.skinnedmodel.ModelManager;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.SliceY;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;

public class ReanimatedPlayers {
   public static ReanimatedPlayers instance = new ReanimatedPlayers();
   private ArrayList Zombies = new ArrayList();

   private static void noise(String var0) {
      DebugLog.log("reanimate: " + var0);
   }

   public void addReanimatedPlayersToChunk(IsoChunk var1) {
      int var2 = var1.wx * 10;
      int var3 = var1.wy * 10;
      int var4 = var2 + 10;
      int var5 = var3 + 10;

      for(int var6 = 0; var6 < this.Zombies.size(); ++var6) {
         IsoZombie var7 = (IsoZombie)this.Zombies.get(var6);
         if (var7.getX() >= (float)var2 && var7.getX() < (float)var4 && var7.getY() >= (float)var3 && var7.getY() < (float)var5) {
            IsoGridSquare var8 = var1.getGridSquare((int)var7.getX() - var2, (int)var7.getY() - var3, (int)var7.getZ());
            if (var8 != null) {
               if (GameServer.bServer) {
                  if (var7.OnlineID != -1) {
                     noise("ERROR? OnlineID != -1 for reanimated player zombie");
                  }

                  var7.OnlineID = ServerMap.instance.getUniqueZombieId();
                  if (var7.OnlineID == -1) {
                     continue;
                  }

                  ServerMap.instance.ZombieMap.put(var7.OnlineID, var7);
               }

               var7.setCurrent(var8);

               assert !IsoWorld.instance.CurrentCell.getObjectList().contains(var7);

               assert !IsoWorld.instance.CurrentCell.getZombieList().contains(var7);

               IsoWorld.instance.CurrentCell.getObjectList().add(var7);
               IsoWorld.instance.CurrentCell.getZombieList().add(var7);
               this.Zombies.remove(var6);
               --var6;
               SharedDescriptors.createPlayerZombieDescriptor(var7);
               noise("added to world " + var7);
            }
         }
      }

   }

   public void removeReanimatedPlayerFromWorld(IsoZombie var1) {
      if (var1.isReanimatedPlayer()) {
         if (!GameServer.bServer && ModelManager.instance.Contains.contains(var1) && !ModelManager.instance.ToRemove.contains(var1)) {
            ModelManager.instance.Remove((IsoGameCharacter)var1);
         }

         if (var1.isOnFire()) {
            IsoFireManager.RemoveBurningCharacter(var1);
            var1.setOnFire(false);
         }

         if (var1.AttachedAnimSprite != null) {
            ArrayList var2 = var1.AttachedAnimSprite;

            for(int var3 = 0; var3 < var2.size(); ++var3) {
               IsoSpriteInstance var4 = (IsoSpriteInstance)var2.get(var3);
               IsoSpriteInstance.add(var4);
            }

            var1.AttachedAnimSprite.clear();
         }

         if (var1.AttachedAnimSpriteActual != null) {
            var1.AttachedAnimSpriteActual.clear();
         }

         if (!GameServer.bServer) {
            for(int var5 = 0; var5 < IsoPlayer.numPlayers; ++var5) {
               IsoPlayer var6 = IsoPlayer.players[var5];
               if (var6 != null && var6.ReanimatedCorpse == var1) {
                  var6.ReanimatedCorpse = null;
                  var6.ReanimatedCorpseID = -1;
               }
            }
         }

         if (GameServer.bServer && var1.OnlineID != -1) {
            ServerMap.instance.ZombieMap.remove(var1.OnlineID);
            var1.OnlineID = -1;
         }

         SharedDescriptors.releasePlayerZombieDescriptor(var1);

         assert !VirtualZombieManager.instance.isReused(var1);

         if (!var1.isDead()) {
            if (!this.Zombies.contains(var1)) {
               this.Zombies.add(var1);
               noise("added to Zombies " + var1);
               var1.getStateMachine().Lock = false;
               var1.getStateMachine().setCurrent(ZombieStandState.instance());
            }
         }
      }
   }

   public void saveReanimatedPlayers() {
      if (!GameClient.bClient) {
         if (SliceY.SliceBuffer == null) {
            SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
         }

         ByteBuffer var1 = SliceY.SliceBuffer;
         ArrayList var2 = new ArrayList();

         try {
            var1.rewind();
            var1.putInt(143);
            var2.addAll(this.Zombies);

            int var3;
            for(var3 = 0; var3 < IsoWorld.instance.CurrentCell.getZombieList().size(); ++var3) {
               IsoZombie var4 = (IsoZombie)IsoWorld.instance.CurrentCell.getZombieList().get(var3);
               if (var4.isReanimatedPlayer() && !var4.isDead() && !var2.contains(var4)) {
                  var2.add(var4);
               }
            }

            var1.putInt(var2.size());

            for(var3 = 0; var3 < var2.size(); ++var3) {
               ((IsoZombie)var2.get(var3)).save(var1);
            }

            File var7 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "reanimated.bin");
            FileOutputStream var8 = new FileOutputStream(var7);
            BufferedOutputStream var5 = new BufferedOutputStream(var8);
            var5.write(var1.array(), 0, var1.position());
            var5.flush();
            var5.close();
         } catch (Exception var6) {
            var6.printStackTrace();
            return;
         }

         noise("saved " + var2.size() + " zombies");
      }
   }

   public void loadReanimatedPlayers() {
      if (!GameClient.bClient) {
         this.Zombies.clear();
         File var1 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "reanimated.bin");
         if (var1.exists()) {
            if (SliceY.SliceBuffer == null) {
               SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
            }

            ByteBuffer var2 = SliceY.SliceBuffer;

            try {
               FileInputStream var3 = new FileInputStream(var1);
               BufferedInputStream var4 = new BufferedInputStream(var3);
               var4.read(var2.array());
               var4.close();
               var2.rewind();
               int var5 = var2.getInt();
               int var6 = var2.getInt();

               for(int var7 = 0; var7 < var6; ++var7) {
                  IsoObject var8 = IsoObject.factoryFromFileInput(IsoWorld.instance.CurrentCell, var2);
                  if (!(var8 instanceof IsoZombie)) {
                     throw new RuntimeException("expected IsoZombie here");
                  }

                  IsoZombie var9 = (IsoZombie)var8;
                  var9.load(var2, var5);
                  var9.getDescriptor().setID(0);
                  var9.setReanimatedPlayer(true);
                  IsoWorld.instance.CurrentCell.getAddList().remove(var9);
                  IsoWorld.instance.CurrentCell.getObjectList().remove(var9);
                  IsoWorld.instance.CurrentCell.getZombieList().remove(var9);
                  this.Zombies.add(var9);
               }
            } catch (Exception var10) {
               var10.printStackTrace();
               return;
            }

            noise("loaded " + this.Zombies.size() + " zombies");
         }
      }
   }
}
