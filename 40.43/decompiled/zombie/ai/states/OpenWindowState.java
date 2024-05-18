package zombie.ai.states;

import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.core.Rand;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoDirections;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoWindow;

public class OpenWindowState extends State {
   static OpenWindowState _instance = new OpenWindowState();

   public static OpenWindowState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      byte var2 = 0;
      String var3 = null;
      byte var4 = 0;
      boolean var5 = false;
      boolean var6 = false;
      if (var1.StateMachineParams.size() > 1) {
         var3 = (String)var1.StateMachineParams.get(1);
      }

      if (var3 == null) {
         var3 = "WindowOpenIn";
      }

      IsoWindow var7 = (IsoWindow)var1.StateMachineParams.get(0);
      if ("WindowSmash".equals(var3)) {
         var2 = 1;
      } else if (var7.Locked && var1.getCurrentSquare().Is(IsoFlagType.exterior) || var7.PermaLocked) {
         var2 = 1;
      }

      if ("WindowSmash".equals(var3) && var1.getPrimaryHandItem() instanceof HandWeapon) {
         HandWeapon var8 = (HandWeapon)var1.getPrimaryHandItem();
         if (var8.getSwingAnim() != null && !var8.isRanged() && var1.legsSprite.CurrentAnim.name.equals("Attack_" + var8.getSwingAnim())) {
            var1.def.Finished = false;
            var1.def.Frame = 0.0F;
         }
      }

      var1.StateMachineParams.put(1, var3);
      var1.StateMachineParams.put(2, Integer.valueOf(var2));
      var1.StateMachineParams.put(3, var6);
      var1.StateMachineParams.put(4, var5);
      var1.StateMachineParams.put(5, Integer.valueOf(var4));
   }

   public void execute(IsoGameCharacter var1) {
      if (!IsoPlayer.instance.pressedMovement() && !IsoPlayer.instance.pressedCancelAction() && var1.StateMachineParams.size() >= 6) {
         IsoWindow var2 = (IsoWindow)var1.StateMachineParams.get(0);
         String var3 = (String)var1.StateMachineParams.get(1);
         if (var2 == null) {
            var1.getStateMachine().changeState(var1.getDefaultState());
         } else {
            if (var3 == null) {
               this.enter(var1);
            }

            int var4 = (Integer)var1.StateMachineParams.get(2);
            boolean var5 = (Boolean)var1.StateMachineParams.get(3);
            boolean var6 = (Boolean)var1.StateMachineParams.get(4);
            int var7 = (Integer)var1.StateMachineParams.get(5);
            if (IsoPlayer.instance.ContextPanic > 5.0F) {
               var6 = true;
               var5 = true;
               IsoPlayer.instance.ContextPanic = 0.0F;
               var3 = "WindowSmash";
               var7 = 0;
               var4 = 1;
            }

            IsoPlayer var8 = (IsoPlayer)var1;
            var8.setCollidable(true);
            var8.updateLOS();
            if (var1.getPrimaryHandItem() instanceof HandWeapon && ((HandWeapon)var1.getPrimaryHandItem()).getSwingAnim() != null && "WindowSmash".equals(var3) && !((HandWeapon)var1.getPrimaryHandItem()).isRanged()) {
               var1.PlayAnimUnlooped("Attack_" + ((HandWeapon)var1.getPrimaryHandItem()).getSwingAnim());
            } else {
               var1.PlayAnimUnlooped(var3);
            }

            if (var1.sprite != null) {
               var1.sprite.Animate = true;
            }

            if (var1.sprite != null && var1.sprite.CurrentAnim != null && "WindowOpenStruggle".equals(var1.sprite.CurrentAnim.name)) {
               var1.sprite.CurrentAnim.FinishUnloopedOnFrame = 0;
            }

            if (var3 == "WindowOpenIn") {
               var1.getSpriteDef().AnimFrameIncrease = 0.23F;
            } else {
               var1.getSpriteDef().AnimFrameIncrease = 0.18F;
            }

            if (var3 == "WindowOpenSuccess" && (int)var1.getSpriteDef().Frame == 3 && !var2.open) {
               IsoPlayer.instance.ContextPanic = 0.0F;
               var2.ToggleWindow(var1);
            }

            if (var3 == "WindowSmash" && (int)var1.getSpriteDef().Frame == 5 && var2.Health > 0 && var4 > 0) {
               IsoPlayer.instance.ContextPanic = 0.0F;
               var2.WeaponHit(var1, (HandWeapon)null);
               var4 = -1;
               IsoPlayer.instance.ContextPanic = 0.0F;
               if (!(var1.getPrimaryHandItem() instanceof HandWeapon) && !(var1.getSecondaryHandItem() instanceof HandWeapon)) {
                  var1.getBodyDamage().setScratchedWindow();
               }
            }

            if (var2.north) {
               if ((float)var2.getSquare().getY() < var1.getY()) {
                  var1.setDir(IsoDirections.N);
               } else {
                  var1.setDir(IsoDirections.S);
               }
            } else if ((float)var2.getSquare().getX() < var1.getX()) {
               var1.setDir(IsoDirections.W);
            } else {
               var1.setDir(IsoDirections.E);
            }

            if (var1.getSpriteDef().Finished) {
               if (!"WindowSmash".equals(var3)) {
                  if (var2.PermaLocked) {
                     if ("WindowOpenStruggle".equals(var3)) {
                        var1.StateMachineParams.clear();
                        var1.getStateMachine().changeState(var1.getDefaultState());
                        return;
                     }

                     var1.getEmitter().playSound("WindowIsLocked", var2);
                  } else if (var2.Locked && var1.getCurrentSquare().Is(IsoFlagType.exterior)) {
                     var4 = 1;
                     if (Rand.Next(100) < 10) {
                        var1.getEmitter().playSound("BreakLockOnWindow", var2);
                        var2.setPermaLocked(true);
                        var2.syncIsoObject(false, (byte)0, (UdpConnection)null, (ByteBuffer)null);
                        var1.StateMachineParams.clear();
                        var1.getStateMachine().changeState(var1.getDefaultState());
                     } else if (var1.getPerkLevel(PerkFactory.Perks.Strength) > 7 && Rand.Next(100) < 20) {
                        var4 = 0;
                     } else if (var1.getPerkLevel(PerkFactory.Perks.Strength) > 5 && Rand.Next(100) < 10) {
                        var4 = 0;
                     } else if (var1.getPerkLevel(PerkFactory.Perks.Strength) > 3 && Rand.Next(100) < 6) {
                        var4 = 0;
                     } else if (var1.getPerkLevel(PerkFactory.Perks.Strength) > 1 && Rand.Next(100) < 4) {
                        var4 = 0;
                     } else if (Rand.Next(100) <= 1) {
                        var4 = 0;
                     } else {
                        var1.getEmitter().playSound("WindowIsLocked", var2);
                     }
                  }
               }

               if (var6 && var4 > -1) {
                  IsoPlayer.instance.ContextPanic = 0.0F;
                  var3 = "WindowSmash";
               } else if (var4 > 0) {
                  if (var3 == "WindowOpenStruggle") {
                     var1.getSpriteDef().Finished = false;
                  }

                  var3 = "WindowOpenStruggle";
               } else if (var4 == 0) {
                  IsoPlayer.instance.ContextPanic = 0.0F;
                  var3 = "WindowOpenSuccess";
                  var1.getEmitter().playSound("OpenWindow");
               } else if (var5) {
                  var1.getStateMachine().changeState(ClimbThroughWindowState.instance());
               } else {
                  var1.getStateMachine().changeState(var1.getDefaultState());
                  var1.StateMachineParams.clear();
               }
            }

            if ((float)var7 > var1.getSpriteDef().Frame) {
               --var4;
               float var9 = GameTime.getInstance().getMultiplier() / 1.6F;
               switch(var1.getPerkLevel(PerkFactory.Perks.Fitness)) {
               case 1:
                  var1.exert(0.01F * var9);
                  break;
               case 2:
                  var1.exert(0.009F * var9);
                  break;
               case 3:
                  var1.exert(0.008F * var9);
                  break;
               case 4:
                  var1.exert(0.007F * var9);
                  break;
               case 5:
                  var1.exert(0.006F * var9);
                  break;
               case 6:
                  var1.exert(0.005F * var9);
                  break;
               case 7:
                  var1.exert(0.004F * var9);
                  break;
               case 8:
                  var1.exert(0.003F * var9);
                  break;
               case 9:
                  var1.exert(0.0025F * var9);
                  break;
               case 10:
                  var1.exert(0.002F * var9);
               }
            }

            if (var1.getCurrentState() == this) {
               var7 = (int)var1.getSpriteDef().Frame;
               var1.StateMachineParams.put(1, var3);
               var1.StateMachineParams.put(2, var4);
               var1.StateMachineParams.put(3, var5);
               var1.StateMachineParams.put(4, var6);
               var1.StateMachineParams.put(5, var7);
            }
         }
      } else {
         var1.getStateMachine().changeState(var1.getDefaultState());
      }
   }
}
