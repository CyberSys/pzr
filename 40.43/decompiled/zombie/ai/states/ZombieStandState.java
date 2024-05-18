package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.Vector2;
import zombie.iso.objects.RainManager;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;

public class ZombieStandState extends State {
   static ZombieStandState _instance = new ZombieStandState();
   static Vector2 vec = new Vector2();

   public static ZombieStandState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      if (!GameClient.bClient && ((IsoZombie)var1).isFakeDead() && !((IsoZombie)var1).bCrawling && !"Zombie_CrawlLunge".equals(var1.getSprite().CurrentAnim.name)) {
         var1.getStateMachine().Lock = false;
         var1.getStateMachine().changeState(StaggerBackDieState.instance());
      } else {
         ((IsoZombie)var1).chasingSound = false;
         ((IsoZombie)var1).soundSourceTarget = null;
         ((IsoZombie)var1).soundAttract = 0.0F;
         var1.setIgnoreMovementForDirection(true);
         ((IsoZombie)var1).movex = 0.0F;
         ((IsoZombie)var1).movey = 0.0F;
         if (!((IsoZombie)var1).bCrawling) {
            boolean var2 = false;
            if (((IsoZombie)var1).NetRemoteState != 1) {
               var2 = true;
            }

            ((IsoZombie)var1).NetRemoteState = 1;
            ((IsoZombie)var1).movex = 0.0F;
            ((IsoZombie)var1).movey = 0.0F;
            var1.PlayAnim("ZombieIdle");
            var1.def.AnimFrameIncrease = 0.08F + (float)Rand.Next(1000) / 8000.0F;
            IsoSpriteInstance var10000 = var1.def;
            var10000.AnimFrameIncrease *= 0.5F;
            var1.def.Frame = (float)Rand.Next(20);
            if (var2) {
               GameServer.sendZombie((IsoZombie)var1);
            }
         } else {
            ((IsoZombie)var1).NetRemoteState = 1;
            var1.PlayAnim("ZombieCrawl");
            var1.def.AnimFrameIncrease = 0.0F;
         }

         GameServer.sendZombie((IsoZombie)var1);
      }
   }

   public void execute(IsoGameCharacter var1) {
      IsoZombie var2 = (IsoZombie)var1;
      var2.NetRemoteState = 1;
      var2.setRemoteMoveX(0.0F);
      var2.setRemoteMoveY(0.0F);
      var2.movex = 0.0F;
      var2.movey = 0.0F;
      int var5;
      if (Core.bLastStand) {
         IsoPlayer var6 = null;
         float var7 = 1000000.0F;

         for(var5 = 0; var5 < IsoPlayer.numPlayers; ++var5) {
            if (IsoPlayer.players[var5] != null && IsoPlayer.players[var5].DistTo(var1) < var7 && !IsoPlayer.players[var5].isDead()) {
               var7 = IsoPlayer.players[var5].DistTo(var1);
               var6 = IsoPlayer.players[var5];
            }
         }

         if (var6 != null) {
            var2.pathToCharacter(var6);
         }

      } else {
         if (!((IsoZombie)var1).bCrawling) {
            var1.setOnFloor(false);
            var1.PlayAnim("ZombieIdle");
            ((IsoZombie)var1).reqMovement.x = var1.angle.x;
            ((IsoZombie)var1).reqMovement.y = var1.angle.y;
            var1.def.AnimFrameIncrease = 0.08F + (float)Rand.Next(1000) / 8000.0F;
            IsoSpriteInstance var10000 = var1.def;
            var10000.AnimFrameIncrease *= 0.5F;
         } else {
            var1.PlayAnim("ZombieCrawl");
            var1.def.AnimFrameIncrease = 0.0F;
         }

         if (!((IsoZombie)var1).bIndoorZombie) {
            if (((IsoZombie)var1).isFakeDead()) {
               var1.getStateMachine().changeState(StaggerBackDieState.instance());
            } else {
               int var3 = RainManager.isRaining() ? 700 : 1100;
               var3 = Rand.AdjustForFramerate(var3);
               if (GameTime.getInstance().getTrueMultiplier() == 1.0F && var1.getStateMachine().getCurrent() == instance() && Rand.Next(var3) == 0 && !((IsoZombie)var1).isUseless()) {
                  int var4 = (int)var1.getX() + (Rand.Next(8) - 4);
                  var5 = (int)var1.getY() + (Rand.Next(8) - 4);
                  if (var1.getCell().getGridSquare((double)var4, (double)var5, (double)var1.getZ()) != null && var1.getCell().getGridSquare((double)var4, (double)var5, (double)var1.getZ()).isFree(true)) {
                     var1.pathToLocation(var4, var5, (int)var1.getZ());
                     ((IsoZombie)var1).AllowRepathDelay = 200.0F;
                  }
               }
            }

         }
      }
   }

   public void exit(IsoGameCharacter var1) {
      var1.setIgnoreMovementForDirection(false);
   }
}
