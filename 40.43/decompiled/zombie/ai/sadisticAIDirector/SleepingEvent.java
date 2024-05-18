package zombie.ai.sadisticAIDirector;

import java.io.FileNotFoundException;
import java.io.IOException;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.Moodles.MoodleType;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoTelevision;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameClient;
import zombie.scripting.ScriptManager;
import zombie.ui.UIManager;

public class SleepingEvent {
   private int forceWakeUpTime = -1;
   private boolean zombiesIntruders = true;
   private int nightmareWakeUp = -1;
   private IsoWindow weakestWindow = null;
   private IsoDoor openDoor = null;
   public static SleepingEvent instance = new SleepingEvent();

   public void setPlayerFallAsleep(IsoPlayer var1, int var2) {
      var1.setTimeOfSleep(GameTime.instance.getTimeOfDay());
      this.doDelayToSleep(var1);
      this.checkNightmare(var1, var2);
      if (this.nightmareWakeUp <= -1) {
         if (SandboxOptions.instance.SleepingEvent.getValue() != 1) {
            if (var1.getCurrentSquare() == null || var1.getCurrentSquare().getZone() == null || !var1.getCurrentSquare().getZone().haveConstruction) {
               boolean var3 = false;
               if ((GameTime.instance.getHour() >= 0 && GameTime.instance.getHour() < 5 || GameTime.instance.getHour() > 18) && var2 >= 4) {
                  var3 = true;
               }

               byte var4 = 20;
               if (SandboxOptions.instance.SleepingEvent.getValue() == 3) {
                  var4 = 45;
               }

               if (Rand.Next(100) <= var4 && var1.getCell().getZombieList().size() >= 1 && var2 >= 4) {
                  int var5 = 0;
                  if (var1.getCurrentBuilding() != null) {
                     IsoGridSquare var6 = null;
                     IsoWindow var7 = null;

                     for(int var8 = 0; var8 < 3; ++var8) {
                        for(int var9 = var1.getCurrentBuilding().getDef().getX() - 2; var9 < var1.getCurrentBuilding().getDef().getX2() + 2; ++var9) {
                           for(int var10 = var1.getCurrentBuilding().getDef().getY() - 2; var10 < var1.getCurrentBuilding().getDef().getY2() + 2; ++var10) {
                              var6 = IsoWorld.instance.getCell().getGridSquare(var9, var10, var8);
                              if (var6 != null) {
                                 boolean var11 = var6.haveElectricity() || GameTime.instance.NightsSurvived < SandboxOptions.instance.getElecShutModifier();
                                 if (var11) {
                                    for(int var12 = 0; var12 < var6.getObjects().size(); ++var12) {
                                       IsoObject var13 = (IsoObject)var6.getObjects().get(var12);
                                       if (var13.getContainer() != null && (var13.getContainer().getType().equals("fridge") || var13.getContainer().getType().equals("freezer"))) {
                                          var5 += 3;
                                       }

                                       if (var13 instanceof IsoStove && ((IsoStove)var13).Activated()) {
                                          var5 += 5;
                                       }

                                       if (var13 instanceof IsoTelevision && ((IsoTelevision)var13).getDeviceData().getIsTurnedOn()) {
                                          var5 += 30;
                                       }

                                       if (var13 instanceof IsoRadio && ((IsoRadio)var13).getDeviceData().getIsTurnedOn()) {
                                          var5 += 30;
                                       }
                                    }
                                 }

                                 var7 = var6.getWindow();
                                 if (var7 != null) {
                                    var5 += this.checkWindowStatus(var7);
                                 }

                                 IsoDoor var14 = var6.getIsoDoor();
                                 if (var14 != null && var14.isExteriorDoor((IsoGameCharacter)null) && var14.IsOpen()) {
                                    var5 += 25;
                                    this.openDoor = var14;
                                 }
                              }
                           }
                        }
                     }

                     if (SandboxOptions.instance.SleepingEvent.getValue() == 3) {
                        var5 = (int)((double)var5 * 1.5D);
                     }

                     if (var5 > 70) {
                        var5 = 70;
                     }

                     if (!var3) {
                        var5 /= 2;
                     }

                     if (Rand.Next(100) <= var5) {
                        this.forceWakeUpTime = Rand.Next(var2 - 4, var2 - 1);
                        this.zombiesIntruders = true;
                     }
                  }
               }

            }
         }
      }
   }

   private void doDelayToSleep(IsoPlayer var1) {
      float var2 = 0.3F;
      float var3 = 2.0F;
      if (var1.HasTrait("Insomniac")) {
         var2 = 1.0F;
      }

      if (var1.getMoodles().getMoodleLevel(MoodleType.Pain) > 0) {
         var2 += 1.0F + (float)var1.getMoodles().getMoodleLevel(MoodleType.Pain) * 0.2F;
      }

      if (var1.getMoodles().getMoodleLevel(MoodleType.Stress) > 0) {
         var2 *= 1.2F;
      }

      if ("badBed".equals(var1.getBedType())) {
         var2 *= 1.3F;
      } else if ("goodBed".equals(var1.getBedType())) {
         var2 *= 0.8F;
      }

      if (var1.getSleepingTabletEffect() > 1000.0F) {
         var2 = 0.1F;
      }

      if (var2 > var3) {
         var2 = var3;
      }

      float var4 = Rand.Next(0.0F, var2);
      var1.setDelayToSleep(GameTime.instance.getTimeOfDay() + var4);
   }

   private void checkNightmare(IsoPlayer var1, int var2) {
      if (!GameClient.bClient && var2 >= 3) {
         int var3 = 5 + var1.getMoodles().getMoodleLevel(MoodleType.Stress) * 10;
         if (Rand.Next(100) < var3) {
            this.nightmareWakeUp = Rand.Next(3, var2 - 2);
         }
      }

   }

   private int checkWindowStatus(IsoWindow var1) {
      IsoGridSquare var2 = var1.getSquare();
      if (var1.getSquare().getRoom() == null) {
         if (!var1.north) {
            var2 = var1.getSquare().getCell().getGridSquare(var1.getSquare().getX() - 1, var1.getSquare().getY(), var1.getSquare().getZ());
         } else {
            var2 = var1.getSquare().getCell().getGridSquare(var1.getSquare().getX(), var1.getSquare().getY() - 1, var1.getSquare().getZ());
         }
      }

      boolean var3 = false;
      boolean var4 = false;

      for(int var5 = 0; var5 < var2.getRoom().lightSwitches.size(); ++var5) {
         if (((IsoLightSwitch)var2.getRoom().lightSwitches.get(var5)).isActivated()) {
            var4 = true;
            break;
         }
      }

      int var6;
      IsoBarricade var7;
      if (var4) {
         var6 = 20;
         if (var1.HasCurtains() != null && !var1.HasCurtains().open) {
            var6 -= 17;
         }

         var7 = var1.getBarricadeOnOppositeSquare();
         if (var7 == null) {
            var7 = var1.getBarricadeOnSameSquare();
         }

         if (var7 != null && (var7.getNumPlanks() > 4 || var7.isMetal())) {
            var6 -= 20;
         }

         if (var6 < 0) {
            var6 = 0;
         }

         if (var2.getZ() > 0) {
            var6 /= 2;
         }

         return var6;
      } else {
         var6 = 5;
         if (var1.HasCurtains() != null && !var1.HasCurtains().open) {
            var6 -= 5;
         }

         var7 = var1.getBarricadeOnOppositeSquare();
         if (var7 == null) {
            var7 = var1.getBarricadeOnSameSquare();
         }

         if (var7 != null && (var7.getNumPlanks() > 3 || var7.isMetal())) {
            var6 -= 5;
         }

         if (var6 < 0) {
            var6 = 0;
         }

         if (var2.getZ() > 0) {
            var6 /= 2;
         }

         return var6;
      }
   }

   public void update(IsoPlayer var1) {
      if (var1 != null) {
         if (this.nightmareWakeUp == (int)var1.getAsleepTime()) {
            Stats var10000 = var1.getStats();
            var10000.Panic += 70.0F;
            var10000 = var1.getStats();
            var10000.stress += 0.5F;
            WorldSoundManager.instance.addSound(var1, (int)var1.getX(), (int)var1.getY(), (int)var1.getZ(), 6, 1);
            this.wakeUp(var1);
         }

         if (this.forceWakeUpTime == (int)var1.getAsleepTime() && this.zombiesIntruders) {
            this.spawnZombieIntruders(var1);
            WorldSoundManager.instance.addSound(var1, (int)var1.getX(), (int)var1.getY(), (int)var1.getZ(), 6, 1);
            this.wakeUp(var1);
         }

      }
   }

   private void spawnZombieIntruders(IsoPlayer var1) {
      IsoGridSquare var2 = null;
      if (this.openDoor != null) {
         var2 = this.openDoor.getSquare();
      } else {
         this.weakestWindow = this.getWeakestWindow(var1);
         if (this.weakestWindow != null && this.weakestWindow.getZ() == 0.0F) {
            if (!this.weakestWindow.north) {
               if (this.weakestWindow.getSquare().getRoom() == null) {
                  var2 = this.weakestWindow.getSquare();
               } else {
                  var2 = this.weakestWindow.getSquare().getCell().getGridSquare(this.weakestWindow.getSquare().getX() - 1, this.weakestWindow.getSquare().getY(), this.weakestWindow.getSquare().getZ());
               }
            } else if (this.weakestWindow.getSquare().getRoom() == null) {
               var2 = this.weakestWindow.getSquare();
            } else {
               var2 = this.weakestWindow.getSquare().getCell().getGridSquare(this.weakestWindow.getSquare().getX(), this.weakestWindow.getSquare().getY() + 1, this.weakestWindow.getSquare().getZ());
            }

            IsoBarricade var3 = this.weakestWindow.getBarricadeOnOppositeSquare();
            if (var3 == null) {
               var3 = this.weakestWindow.getBarricadeOnSameSquare();
            }

            if (var3 != null) {
               var3.Damage(Rand.Next(500, 900));
            } else {
               this.weakestWindow.Damage(200.0F);
               this.weakestWindow.smashWindow();
               if (this.weakestWindow.HasCurtains() != null) {
                  this.weakestWindow.removeSheet((IsoGameCharacter)null);
               }
            }
         }
      }

      var1.getStats().setPanic(var1.getStats().getPanic() + (float)Rand.Next(30, 60));
      if (var2 != null) {
         if (IsoWorld.getZombiesEnabled()) {
            int var6 = Rand.Next(3) + 1;

            for(int var4 = 0; var4 < var6; ++var4) {
               VirtualZombieManager.instance.choices.clear();
               VirtualZombieManager.instance.choices.add(var2);
               IsoZombie var5 = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.fromIndex(Rand.Next(8)).index(), false);
               if (var5 != null) {
                  var5.target = var1;
                  var5.pathToCharacter(var1);
                  var5.spotted(var1, true);
               }
            }
         }

      }
   }

   private IsoWindow getWeakestWindow(IsoPlayer var1) {
      IsoGridSquare var2 = null;
      IsoWindow var3 = null;
      IsoWindow var4 = null;
      int var5 = 0;

      for(int var6 = var1.getCurrentBuilding().getDef().getX() - 2; var6 < var1.getCurrentBuilding().getDef().getX2() + 2; ++var6) {
         for(int var7 = var1.getCurrentBuilding().getDef().getY() - 2; var7 < var1.getCurrentBuilding().getDef().getY2() + 2; ++var7) {
            var2 = IsoWorld.instance.getCell().getGridSquare(var6, var7, 0);
            if (var2 != null) {
               var3 = var2.getWindow();
               if (var3 != null) {
                  int var8 = this.checkWindowStatus(var3);
                  if (var8 > var5) {
                     var5 = var8;
                     var4 = var3;
                  }
               }
            }
         }
      }

      return var4;
   }

   public void wakeUp(IsoGameCharacter var1) {
      if (var1 != null) {
         this.wakeUp(var1, false);
      }
   }

   public void wakeUp(IsoGameCharacter var1, boolean var2) {
      if (GameClient.bClient && !var2) {
         GameClient.instance.wakeUpPlayer((IsoPlayer)var1);
      }

      boolean var3 = false;
      boolean var4 = false;

      for(int var5 = 0; var5 < IsoPlayer.players.length; ++var5) {
         if (IsoPlayer.players[var5] == var1) {
            var4 = true;
         }
      }

      if (var1 instanceof IsoPlayer && var4) {
         UIManager.setFadeBeforeUI(((IsoPlayer)var1).getPlayerNum(), true);
         UIManager.FadeIn((double)((IsoPlayer)var1).getPlayerNum(), 2.0D);
         if (!GameClient.bClient && IsoPlayer.allPlayersAsleep()) {
            UIManager.getSpeedControls().SetCurrentGameSpeed(1);
            var3 = true;
         }

         var1.setLastHourSleeped((int)((IsoPlayer)var1).getHoursSurvived());
      }

      var1.setForceWakeUpTime(-1.0F);
      var1.setAsleep(false);
      if (var3) {
         try {
            GameWindow.save(true);
         } catch (FileNotFoundException var6) {
            var6.printStackTrace();
         } catch (IOException var7) {
            var7.printStackTrace();
         }
      }

      if ("goodBed".equals(var1.getBedType())) {
         var1.getStats().setFatigue(var1.getStats().getFatigue() - Rand.Next(0.05F, 0.12F));
         if (var1.getStats().getFatigue() < 0.0F) {
            var1.getStats().setFatigue(0.0F);
         }
      } else if ("badBed".equals(var1.getBedType())) {
         var1.getStats().setFatigue(var1.getStats().getFatigue() + Rand.Next(0.1F, 0.2F));
         if (Rand.Next(5) == 0) {
            var1.getBodyDamage().getBodyPart(BodyPartType.Neck).AddDamage(Rand.Next(5.0F, 15.0F));
            var1.getBodyDamage().getBodyPart(BodyPartType.Neck).setAdditionalPain(var1.getBodyDamage().getBodyPart(BodyPartType.Neck).getAdditionalPain() + Rand.Next(30.0F, 50.0F));
         }
      } else if (Rand.Next(10) == 0) {
         var1.getBodyDamage().getBodyPart(BodyPartType.Neck).AddDamage(Rand.Next(3.0F, 12.0F));
         var1.getBodyDamage().getBodyPart(BodyPartType.Neck).setAdditionalPain(var1.getBodyDamage().getBodyPart(BodyPartType.Neck).getAdditionalPain() + Rand.Next(10.0F, 30.0F));
      }

      if (var1 instanceof IsoPlayer) {
         ScriptManager.instance.Trigger("OnPlayerWake");
      }

      this.forceWakeUpTime = -1;
      this.zombiesIntruders = false;
      this.nightmareWakeUp = -1;
      this.openDoor = null;
      this.weakestWindow = null;
   }
}
