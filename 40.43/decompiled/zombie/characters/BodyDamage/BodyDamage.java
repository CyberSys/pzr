package zombie.characters.BodyDamage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.FliesSound;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.WorldSoundManager;
import zombie.ZomboidGlobals;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.Literature;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehicleWindow;

public class BodyDamage {
   public ArrayList BodyParts = new ArrayList(18);
   public int DamageModCount = 60;
   public float InfectionGrowthRate = 0.001F;
   public float InfectionLevel = 0.0F;
   public boolean IsInfected;
   public float InfectionTime = -1.0F;
   public float InfectionMortalityDuration = -1.0F;
   public float FakeInfectionLevel = 0.0F;
   public boolean IsFakeInfected;
   public float OverallBodyHealth = 100.0F;
   public float StandardHealthAddition = 0.002F;
   public float ReducedHealthAddition = 0.0013F;
   public float SeverlyReducedHealthAddition = 8.0E-4F;
   public float SleepingHealthAddition = 0.02F;
   public float HealthFromFood = 0.015F;
   public float HealthReductionFromSevereBadMoodles = 0.0165F;
   public int StandardHealthFromFoodTime = 1600;
   public float HealthFromFoodTimer = 0.0F;
   public float BoredomLevel = 0.0F;
   public float BoredomDecreaseFromReading = 0.5F;
   public float InitialThumpPain = 14.0F;
   public float InitialScratchPain = 18.0F;
   public float InitialBitePain = 25.0F;
   public float InitialWoundPain = 80.0F;
   public float ContinualPainIncrease = 0.001F;
   public float PainReductionFromMeds = 30.0F;
   public float StandardPainReductionWhenWell = 0.01F;
   public int OldNumZombiesVisible = 0;
   public int CurrentNumZombiesVisible = 0;
   public float PanicIncreaseValue = 7.0F;
   public float PanicReductionValue = 0.06F;
   public float DrunkIncreaseValue = 20.5F;
   public float DrunkReductionValue = 0.0042F;
   public boolean IsOnFire = false;
   public boolean BurntToDeath = false;
   public float Wetness = 0.0F;
   public float CatchACold = 0.0F;
   public boolean HasACold = false;
   public float ColdStrength = 0.0F;
   public float ColdProgressionRate = 0.0112F;
   public int TimeToSneezeOrCough = 0;
   public int MildColdSneezeTimerMin = 600;
   public int MildColdSneezeTimerMax = 800;
   public int ColdSneezeTimerMin = 300;
   public int ColdSneezeTimerMax = 600;
   public int NastyColdSneezeTimerMin = 200;
   public int NastyColdSneezeTimerMax = 300;
   public int SneezeCoughActive = 0;
   public int SneezeCoughTime = 0;
   public int SneezeCoughDelay = 25;
   public float UnhappynessLevel = 0.0F;
   public float ColdDamageStage = 0.0F;
   public IsoGameCharacter ParentChar;
   private float FoodSicknessLevel = 0.0F;
   private int RemotePainLevel;
   private float Temperature = 37.0F;
   private float lastTemperature = 37.0F;
   private float PoisonLevel = 0.0F;
   private boolean reduceFakeInfection = false;
   private float painReduction = 0.0F;
   private float coldReduction = 0.0F;
   public static final float InfectionLevelToZombify = 0.001F;

   public BodyDamage(IsoGameCharacter var1) {
      this.BodyParts.add(new BodyPart(BodyPartType.Hand_L, var1));
      this.BodyParts.add(new BodyPart(BodyPartType.Hand_R, var1));
      this.BodyParts.add(new BodyPart(BodyPartType.ForeArm_L, var1));
      this.BodyParts.add(new BodyPart(BodyPartType.ForeArm_R, var1));
      this.BodyParts.add(new BodyPart(BodyPartType.UpperArm_L, var1));
      this.BodyParts.add(new BodyPart(BodyPartType.UpperArm_R, var1));
      this.BodyParts.add(new BodyPart(BodyPartType.Torso_Upper, var1));
      this.BodyParts.add(new BodyPart(BodyPartType.Torso_Lower, var1));
      this.BodyParts.add(new BodyPart(BodyPartType.Head, var1));
      this.BodyParts.add(new BodyPart(BodyPartType.Neck, var1));
      this.BodyParts.add(new BodyPart(BodyPartType.Groin, var1));
      this.BodyParts.add(new BodyPart(BodyPartType.UpperLeg_L, var1));
      this.BodyParts.add(new BodyPart(BodyPartType.UpperLeg_R, var1));
      this.BodyParts.add(new BodyPart(BodyPartType.LowerLeg_L, var1));
      this.BodyParts.add(new BodyPart(BodyPartType.LowerLeg_R, var1));
      this.BodyParts.add(new BodyPart(BodyPartType.Foot_L, var1));
      this.BodyParts.add(new BodyPart(BodyPartType.Foot_R, var1));
      this.RestoreToFullHealth();
      this.ParentChar = var1;
   }

   public BodyPart getBodyPart(BodyPartType var1) {
      return (BodyPart)this.BodyParts.get(BodyPartType.ToIndex(var1));
   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      for(int var3 = 0; var3 < this.getBodyParts().size(); ++var3) {
         BodyPart var4 = (BodyPart)this.getBodyParts().get(var3);
         var4.SetBitten(var1.get() == 1);
         var4.setScratched(var1.get() == 1);
         var4.setBandaged(var1.get() == 1, 0.0F);
         var4.setBleeding(var1.get() == 1);
         var4.setDeepWounded(var1.get() == 1);
         var4.SetFakeInfected(var1.get() == 1);
         var4.SetInfected(var1.get() == 1);
         var4.SetHealth(var1.getFloat());
         if (var2 >= 37 && var2 <= 43) {
            var1.getInt();
         }

         if (var2 >= 44) {
            if (var4.bandaged()) {
               var4.setBandageLife(var1.getFloat());
            }

            var4.setInfectedWound(var1.get() == 1);
            if (var4.isInfectedWound()) {
               var4.setWoundInfectionLevel(var1.getFloat());
            }

            var4.setBiteTime(var1.getFloat());
            var4.setScratchTime(var1.getFloat());
            var4.setBleedingTime(var1.getFloat());
            var4.setAlcoholLevel(var1.getFloat());
            var4.setAdditionalPain(var1.getFloat());
            var4.setDeepWoundTime(var1.getFloat());
            var4.setHaveGlass(var1.get() == 1);
            var4.setGetBandageXp(var1.get() == 1);
            if (var2 >= 48) {
               var4.setStitched(var1.get() == 1);
               var4.setStitchTime(var1.getFloat());
            }

            var4.setGetStitchXp(var1.get() == 1);
            var4.setGetSplintXp(var1.get() == 1);
            var4.setFractureTime(var1.getFloat());
            var4.setSplint(var1.get() == 1, 0.0F);
            if (var4.isSplint()) {
               var4.setSplintFactor(var1.getFloat());
            }

            var4.setHaveBullet(var1.get() == 1, 0);
            var4.setBurnTime(var1.getFloat());
            var4.setNeedBurnWash(var1.get() == 1);
            var4.setLastTimeBurnWash(var1.getFloat());
            if (var2 >= 50) {
               var4.setSplintItem(GameWindow.ReadString(var1));
            }

            if (var2 >= 53) {
               var4.setBandageType(GameWindow.ReadString(var1));
            }
         }
      }

      this.setInfectionLevel(var1.getFloat());
      this.setFakeInfectionLevel(var1.getFloat());
      this.setWetness(var1.getFloat());
      this.setCatchACold(var1.getFloat());
      this.setHasACold(var1.get() == 1);
      this.setColdStrength(var1.getFloat());
      this.setUnhappynessLevel(var1.getFloat());
      this.setBoredomLevel(var1.getFloat());
      if (var2 >= 30) {
         this.setFoodSicknessLevel(var1.getFloat());
         this.PoisonLevel = var1.getFloat();
      }

      if (var2 >= 43) {
         float var5 = var1.getFloat();
         if (var2 < 135) {
            var5 = 37.0F;
         }

         this.setTemperature(var5);
      }

      if (var2 >= 44) {
         this.setReduceFakeInfection(var1.get() == 1);
      }

      if (var2 >= 56) {
         this.setHealthFromFoodTimer(var1.getFloat());
      }

      if (var2 >= 74) {
         this.painReduction = var1.getFloat();
         this.coldReduction = var1.getFloat();
      }

      if (var2 >= 137) {
         this.InfectionTime = var1.getFloat();
         this.InfectionMortalityDuration = var1.getFloat();
      }

      if (var2 >= 139) {
         this.ColdDamageStage = var1.getFloat();
      }

   }

   public void save(ByteBuffer var1) throws IOException {
      for(int var2 = 0; var2 < this.getBodyParts().size(); ++var2) {
         BodyPart var3 = (BodyPart)this.getBodyParts().get(var2);
         var1.put((byte)(var3.bitten() ? 1 : 0));
         var1.put((byte)(var3.scratched() ? 1 : 0));
         var1.put((byte)(var3.bandaged() ? 1 : 0));
         var1.put((byte)(var3.bleeding() ? 1 : 0));
         var1.put((byte)(var3.deepWounded() ? 1 : 0));
         var1.put((byte)(var3.IsFakeInfected() ? 1 : 0));
         var1.put((byte)(var3.IsInfected() ? 1 : 0));
         var1.putFloat(var3.getHealth());
         if (var3.bandaged()) {
            var1.putFloat(var3.getBandageLife());
         }

         var1.put((byte)(var3.isInfectedWound() ? 1 : 0));
         if (var3.isInfectedWound()) {
            var1.putFloat(var3.getWoundInfectionLevel());
         }

         var1.putFloat(var3.getBiteTime());
         var1.putFloat(var3.getScratchTime());
         var1.putFloat(var3.getBleedingTime());
         var1.putFloat(var3.getAlcoholLevel());
         var1.putFloat(var3.getAdditionalPain());
         var1.putFloat(var3.getDeepWoundTime());
         var1.put((byte)(var3.haveGlass() ? 1 : 0));
         var1.put((byte)(var3.isGetBandageXp() ? 1 : 0));
         var1.put((byte)(var3.stitched() ? 1 : 0));
         var1.putFloat(var3.getStitchTime());
         var1.put((byte)(var3.isGetStitchXp() ? 1 : 0));
         var1.put((byte)(var3.isGetSplintXp() ? 1 : 0));
         var1.putFloat(var3.getFractureTime());
         var1.put((byte)(var3.isSplint() ? 1 : 0));
         if (var3.isSplint()) {
            var1.putFloat(var3.getSplintFactor());
         }

         var1.put((byte)(var3.haveBullet() ? 1 : 0));
         var1.putFloat(var3.getBurnTime());
         var1.put((byte)(var3.isNeedBurnWash() ? 1 : 0));
         var1.putFloat(var3.getLastTimeBurnWash());
         GameWindow.WriteString(var1, var3.getSplintItem());
         GameWindow.WriteString(var1, var3.getBandageType());
      }

      var1.putFloat(this.InfectionLevel);
      var1.putFloat(this.getFakeInfectionLevel());
      var1.putFloat(this.getWetness());
      var1.putFloat(this.getCatchACold());
      var1.put((byte)(this.isHasACold() ? 1 : 0));
      var1.putFloat(this.getColdStrength());
      var1.putFloat(this.getUnhappynessLevel());
      var1.putFloat(this.getBoredomLevel());
      var1.putFloat(this.getFoodSicknessLevel());
      var1.putFloat(this.PoisonLevel);
      var1.putFloat(this.Temperature);
      var1.put((byte)(this.isReduceFakeInfection() ? 1 : 0));
      var1.putFloat(this.HealthFromFoodTimer);
      var1.putFloat(this.painReduction);
      var1.putFloat(this.coldReduction);
      var1.putFloat(this.InfectionTime);
      var1.putFloat(this.InfectionMortalityDuration);
      var1.putFloat(this.ColdDamageStage);
   }

   public boolean IsFakeInfected() {
      return this.isIsFakeInfected();
   }

   public void OnFire(boolean var1) {
      this.setIsOnFire(var1);
   }

   public boolean IsOnFire() {
      return this.isIsOnFire();
   }

   public boolean WasBurntToDeath() {
      return this.isBurntToDeath();
   }

   public void IncreasePanic(int var1) {
      if (this.getParentChar().getVehicle() != null) {
         var1 /= 2;
      }

      float var2 = 1.0F;
      if (this.getParentChar().getBetaEffect() > 0.0F) {
         var2 -= this.getParentChar().getBetaDelta();
         if (var2 > 1.0F) {
            var2 = 1.0F;
         }

         if (var2 < 0.0F) {
            var2 = 0.0F;
         }
      }

      if (this.getParentChar().HasTrait("Cowardly")) {
         var2 *= 2.0F;
      }

      if (this.getParentChar().HasTrait("Brave")) {
         var2 *= 0.3F;
      }

      if (this.getParentChar().HasTrait("Desensitized")) {
         var2 = 0.0F;
      }

      Stats var10000 = this.ParentChar.getStats();
      var10000.Panic += this.getPanicIncreaseValue() * (float)var1 * var2;
      if (this.getParentChar().getStats().Panic > 100.0F) {
         this.ParentChar.getStats().Panic = 100.0F;
      }

   }

   public void ReducePanic() {
      if (!(this.ParentChar.getStats().Panic <= 0.0F)) {
         float var1 = this.getPanicReductionValue() * (GameTime.getInstance().getMultiplier() / 1.6F);
         int var2 = (int)Math.floor(new Double((double)GameTime.instance.getNightsSurvived()) / 30.0D);
         if (var2 > 5) {
            var2 = 5;
         }

         var1 += this.getPanicReductionValue() * (float)var2;
         if (this.ParentChar.isAsleep()) {
            var1 *= 2.0F;
         }

         Stats var10000 = this.ParentChar.getStats();
         var10000.Panic -= var1;
         if (this.getParentChar().getStats().Panic < 0.0F) {
            this.ParentChar.getStats().Panic = 0.0F;
         }

      }
   }

   public void UpdatePanicState() {
      int var1 = this.getParentChar().getStats().NumVisibleZombies;
      if (var1 > this.getOldNumZombiesVisible()) {
         this.IncreasePanic(var1 - this.getOldNumZombiesVisible());
      } else {
         this.ReducePanic();
      }

      this.setOldNumZombiesVisible(var1);
   }

   /** @deprecated */
   @Deprecated
   public void JustDrankBooze() {
      this.JustDrankBooze((Food)null, 1.0F);
   }

   public void JustDrankBooze(Food var1, float var2) {
      float var3 = 1.0F;
      if (this.getParentChar().HasTrait("HeavyDrinker")) {
         var3 = 0.3F;
      }

      if (this.getParentChar().HasTrait("LightDrinker")) {
         var3 = 4.0F;
      }

      if (var1.getBaseHunger() != 0.0F) {
         var2 = var1.getHungChange() * var2 / var1.getBaseHunger() * 2.0F;
      }

      var3 *= var2;
      Stats var10000 = this.ParentChar.getStats();
      var10000.Drunkenness += this.getDrunkIncreaseValue() * var3;
      if (this.getParentChar().getStats().Drunkenness > 100.0F) {
         this.ParentChar.getStats().Drunkenness = 100.0F;
      }

      this.getParentChar().SleepingTablet(0.02F * var2);
      this.getParentChar().BetaAntiDepress(0.4F * var2);
      this.getParentChar().BetaBlockers(0.2F * var2);
      this.getParentChar().PainMeds(0.2F * var2);
   }

   public void JustTookPill(InventoryItem var1) {
      if ("PillsBeta".equals(var1.getType())) {
         if (this.getParentChar() != null && this.getParentChar().getStats().Drunkenness > 10.0F) {
            this.getParentChar().BetaBlockers(0.15F);
         } else {
            this.getParentChar().BetaBlockers(0.3F);
         }

         var1.Use();
      } else if ("PillsAntiDep".equals(var1.getType())) {
         if (this.getParentChar() != null && this.getParentChar().getStats().Drunkenness > 10.0F) {
            this.getParentChar().BetaAntiDepress(0.15F);
         } else {
            this.getParentChar().BetaAntiDepress(0.3F);
         }

         var1.Use();
      } else if ("PillsSleepingTablets".equals(var1.getType())) {
         var1.Use();
         this.getParentChar().SleepingTablet(0.1F);
         if (this.getParentChar() instanceof IsoPlayer) {
            ((IsoPlayer)this.getParentChar()).setSleepingPillsTaken(((IsoPlayer)this.getParentChar()).getSleepingPillsTaken() + 1);
         }
      } else if ("Pills".equals(var1.getType())) {
         var1.Use();
         if (this.getParentChar() != null && this.getParentChar().getStats().Drunkenness > 10.0F) {
            this.getParentChar().PainMeds(0.15F);
         } else {
            this.getParentChar().PainMeds(0.45F);
         }
      } else if ("PillsVitamins".equals(var1.getType())) {
         var1.Use();
         Stats var10000;
         if (this.getParentChar() != null && this.getParentChar().getStats().Drunkenness > 10.0F) {
            var10000 = this.getParentChar().getStats();
            var10000.fatigue += var1.getFatigueChange() / 2.0F;
         } else {
            var10000 = this.getParentChar().getStats();
            var10000.fatigue += var1.getFatigueChange();
         }
      }

   }

   public void JustAteFood(Food var1, float var2) {
      Stats var10000;
      float var3;
      if (var1.getPoisonPower() > 0) {
         var3 = (float)var1.getPoisonPower() * var2;
         if (this.getParentChar().HasTrait("IronGut")) {
            var3 /= 2.0F;
         }

         if (this.getParentChar().HasTrait("WeakStomach")) {
            var3 *= 2.0F;
         }

         this.PoisonLevel += var3;
         var10000 = this.ParentChar.getStats();
         var10000.Pain += (float)var1.getPoisonPower() * var2 / 6.0F;
      }

      if (var1.isTaintedWater()) {
         this.PoisonLevel += 20.0F * var2;
         var10000 = this.ParentChar.getStats();
         var10000.Pain += 10.0F * var2 / 6.0F;
      }

      if (var1.getReduceInfectionPower() > 0.0F) {
         this.getParentChar().setReduceInfectionPower(var1.getReduceInfectionPower());
      }

      this.setBoredomLevel(this.getBoredomLevel() + var1.getBoredomChange() * var2);
      if (this.getBoredomLevel() < 0.0F) {
         this.setBoredomLevel(0.0F);
      }

      this.setUnhappynessLevel(this.getUnhappynessLevel() + var1.getUnhappyChange() * var2);
      if (this.getUnhappynessLevel() < 0.0F) {
         this.setUnhappynessLevel(0.0F);
      }

      if (var1.isAlcoholic()) {
         this.JustDrankBooze(var1, var2);
      }

      if (this.getParentChar().getStats().hunger <= 0.0F) {
         var3 = Math.abs(var1.getHungerChange()) * var2;
         this.setHealthFromFoodTimer((float)((int)(this.getHealthFromFoodTimer() + var3 * this.getHealthFromFoodTimeByHunger())));
         if (var1.isCooked()) {
            this.setHealthFromFoodTimer((float)((int)(this.getHealthFromFoodTimer() + var3 * this.getHealthFromFoodTimeByHunger())));
         }

         if (this.getHealthFromFoodTimer() > 11000.0F) {
            this.setHealthFromFoodTimer(11000.0F);
         }
      }

      if (!"Tutorial".equals(Core.getInstance().getGameMode())) {
         if (!var1.isCooked() && var1.isbDangerousUncooked()) {
            this.setHealthFromFoodTimer(0.0F);
            int var5 = 75;
            if (this.getParentChar().HasTrait("IronGut")) {
               var5 /= 2;
            }

            if (this.getParentChar().HasTrait("WeakStomach")) {
               var5 *= 2;
            }

            if (Rand.Next(100) < var5 && !this.isInfected()) {
               this.PoisonLevel += 15.0F * var2;
            }
         }

         if (var1.getAge() >= (float)var1.getOffAgeMax()) {
            var3 = var1.getAge() - (float)var1.getOffAgeMax();
            if (var3 == 0.0F) {
               var3 = 1.0F;
            }

            if (var3 > 5.0F) {
               var3 = 5.0F;
            }

            int var4;
            if (var1.getOffAgeMax() > var1.getOffAge()) {
               var4 = (int)(var3 / (float)(var1.getOffAgeMax() - var1.getOffAge()) * 100.0F);
            } else {
               var4 = 100;
            }

            if (this.getParentChar().HasTrait("IronGut")) {
               var4 /= 2;
            }

            if (this.getParentChar().HasTrait("WeakStomach")) {
               var4 *= 2;
            }

            if (Rand.Next(100) < var4 && !this.isInfected()) {
               this.PoisonLevel += 5.0F * Math.abs(var1.getHungChange() * 10.0F) * var2;
            }
         }

      }
   }

   public void JustAteFood(Food var1) {
      this.JustAteFood(var1, 100.0F);
   }

   private float getHealthFromFoodTimeByHunger() {
      return 13000.0F;
   }

   public void JustReadSomething(Literature var1) {
      this.setBoredomLevel(this.getBoredomLevel() + var1.getBoredomChange());
      if (this.getBoredomLevel() < 0.0F) {
         this.setBoredomLevel(0.0F);
      }

      this.setUnhappynessLevel(this.getUnhappynessLevel() + var1.getUnhappyChange());
      if (this.getUnhappynessLevel() < 0.0F) {
         this.setUnhappynessLevel(0.0F);
      }

   }

   public void JustTookPainMeds() {
      Stats var10000 = this.ParentChar.getStats();
      var10000.Pain -= this.getPainReductionFromMeds();
      if (this.getParentChar().getStats().Pain < 0.0F) {
         this.ParentChar.getStats().Pain = 0.0F;
      }

   }

   public void UpdateWetness() {
      IsoGridSquare var1 = this.getParentChar().getCurrentSquare();
      BaseVehicle var2 = this.getParentChar().getVehicle();
      IsoGameCharacter var3 = this.getParentChar();
      boolean var4 = var1 != null ? !var1.isInARoom() : true;
      if (var2 != null && var2.hasRoof(var2.getSeat(this.getParentChar()))) {
         var4 = false;
      }

      float var5 = 0.0F;
      float var8;
      if (var2 != null && ClimateManager.getInstance().isRaining()) {
         VehiclePart var6 = var2.getPartById("Windshield");
         if (var6 != null) {
            VehicleWindow var7 = var6.getWindow();
            if (var7 != null && var7.isDestroyed()) {
               var8 = ClimateManager.getInstance().getRainIntensity();
               var8 *= var8;
               var8 *= var2.getCurrentSpeedKmHour() / 50.0F;
               if (var8 < 0.1F) {
                  var8 = 0.0F;
               }

               if (var8 > 1.0F) {
                  var8 = 1.0F;
               }

               this.setWetness(this.getWetness() + (float)ZomboidGlobals.WetnessIncrease * var8 * GameTime.instance.getMultiplier());
               if (this.getWetness() > 100.0F) {
                  this.setWetness(100.0F);
               }

               var5 = var8 * 3.0F;
            }
         }
      }

      if (var4 && var3.isAsleep() && var3.getBed() != null && "Tent".equals(var3.getBed().getName())) {
         var4 = false;
      }

      float var9;
      if (var4 && ClimateManager.getInstance().isRaining()) {
         var9 = ClimateManager.getInstance().getRainIntensity();
         if ((double)var9 < 0.1D) {
            var9 = 0.0F;
         }

         if (this.getParentChar().hasEquipped("Umbrella")) {
            var9 *= 0.25F;
            if (this.getWetness() < 50.0F) {
               this.setWetness(this.getWetness() + (float)ZomboidGlobals.WetnessIncrease * var9 * GameTime.instance.getMultiplier());
            }
         } else {
            this.setWetness(this.getWetness() + (float)ZomboidGlobals.WetnessIncrease * var9 * GameTime.instance.getMultiplier());
         }

         if (this.getWetness() > 100.0F) {
            this.setWetness(100.0F);
         }
      } else if ((!var4 || !ClimateManager.getInstance().isRaining()) && this.getWetness() > 0.0F) {
         var9 = ClimateManager.getInstance().getAirTemperatureForCharacter(this.getParentChar());
         float var10 = 0.1F;
         if (var9 > 5.0F) {
            var10 += (var9 - 5.0F) / 10.0F;
         }

         var10 -= var5;
         if (var10 < 0.0F) {
            var10 = 0.0F;
         }

         var8 = (float)ZomboidGlobals.WetnessDecrease * GameTime.instance.getMultiplier();
         this.setWetness(Math.max(this.getWetness() - var8 * var10, 0.0F));
      }

      if (!this.ParentChar.HasTrait("Outdoorsman") && !this.isHasACold() && (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Wet) > 1 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hypothermia) > 2)) {
         var9 = 1.0F;
         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Wet) == 2) {
            var9 = 1.0F;
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Wet) == 3) {
            var9 = 1.5F;
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Wet) == 4 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hypothermia) > 4) {
            var9 = 2.0F;
         }

         if (this.getParentChar().HasTrait("ProneToIllness")) {
            var9 *= 1.7F;
         }

         if (this.getParentChar().HasTrait("Resilient")) {
            var9 *= 0.45F;
         }

         var9 *= 0.75F;
         this.setCatchACold(this.getCatchACold() + (float)ZomboidGlobals.CatchAColdIncreaseRate * var9 * GameTime.instance.getMultiplier());
         if (this.getCatchACold() >= 100.0F) {
            this.setCatchACold(0.0F);
            this.setHasACold(true);
            this.setColdStrength(20.0F);
            this.setTimeToSneezeOrCough(0);
         }
      }

      if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Wet) < 2 && this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hypothermia) < 3) {
         this.setCatchACold(this.getCatchACold() - (float)ZomboidGlobals.CatchAColdDecreaseRate);
         if (this.getCatchACold() <= 0.0F) {
            this.setCatchACold(0.0F);
         }
      }

   }

   public void TriggerSneezeCough() {
      if (this.getSneezeCoughActive() <= 0) {
         if (Rand.Next(100) > 50) {
            this.setSneezeCoughActive(1);
         } else {
            this.setSneezeCoughActive(2);
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HasACold) == 2) {
            this.setSneezeCoughActive(1);
         }

         this.setSneezeCoughTime(this.getSneezeCoughDelay());
         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HasACold) == 2) {
            this.setTimeToSneezeOrCough(this.getMildColdSneezeTimerMin() + Rand.Next(this.getMildColdSneezeTimerMax() - this.getMildColdSneezeTimerMin()));
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HasACold) == 3) {
            this.setTimeToSneezeOrCough(this.getColdSneezeTimerMin() + Rand.Next(this.getColdSneezeTimerMax() - this.getColdSneezeTimerMin()));
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HasACold) == 4) {
            this.setTimeToSneezeOrCough(this.getNastyColdSneezeTimerMin() + Rand.Next(this.getNastyColdSneezeTimerMax() - this.getNastyColdSneezeTimerMin()));
         }

         boolean var1 = false;
         if (this.getParentChar().getPrimaryHandItem() != null && this.getParentChar().getPrimaryHandItem().getType().equals("Tissue")) {
            if (((Drainable)this.getParentChar().getPrimaryHandItem()).getUsedDelta() > 0.0F) {
               ((Drainable)this.getParentChar().getPrimaryHandItem()).setUsedDelta(((Drainable)this.getParentChar().getPrimaryHandItem()).getUsedDelta() - 0.1F);
               if (((Drainable)this.getParentChar().getPrimaryHandItem()).getUsedDelta() <= 0.0F) {
                  this.getParentChar().getPrimaryHandItem().Use();
               }

               var1 = true;
            }
         } else if (this.getParentChar().getSecondaryHandItem() != null && this.getParentChar().getSecondaryHandItem().getType().equals("Tissue") && ((Drainable)this.getParentChar().getSecondaryHandItem()).getUsedDelta() > 0.0F) {
            ((Drainable)this.getParentChar().getSecondaryHandItem()).setUsedDelta(((Drainable)this.getParentChar().getSecondaryHandItem()).getUsedDelta() - 0.1F);
            if (((Drainable)this.getParentChar().getSecondaryHandItem()).getUsedDelta() <= 0.0F) {
               this.getParentChar().getSecondaryHandItem().Use();
            }

            var1 = true;
         }

         if (var1) {
            this.setSneezeCoughActive(this.getSneezeCoughActive() + 2);
         } else {
            byte var2 = 20;
            byte var3 = 20;
            if (this.getSneezeCoughActive() == 1) {
               var2 = 20;
               var3 = 25;
            }

            if (this.getSneezeCoughActive() == 2) {
               var2 = 35;
               var3 = 40;
            }

            WorldSoundManager.instance.addSound(this.getParentChar(), (int)this.getParentChar().getX(), (int)this.getParentChar().getY(), (int)this.getParentChar().getZ(), var2, var3, true);
         }

      }
   }

   public int IsSneezingCoughing() {
      return this.getSneezeCoughActive();
   }

   public void UpdateCold() {
      if (this.isHasACold()) {
         boolean var1 = true;
         IsoGridSquare var2 = this.getParentChar().getCurrentSquare();
         if (var2 == null || !var2.isInARoom() || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Wet) > 0 || this.getParentChar().getStats().fatigue > 0.5F || this.getParentChar().getStats().hunger > 0.25F || this.getParentChar().getStats().thirst > 0.25F) {
            var1 = false;
         }

         if (this.getColdReduction() > 0.0F) {
            var1 = true;
            this.setColdReduction(this.getColdReduction() - 0.005F * GameTime.instance.getMultiplier());
            if (this.getColdReduction() < 0.0F) {
               this.setColdReduction(0.0F);
            }
         }

         float var3;
         if (var1) {
            var3 = 1.0F;
            if (this.getParentChar().HasTrait("ProneToIllness")) {
               var3 = 0.5F;
            }

            if (this.getParentChar().HasTrait("Resilient")) {
               var3 = 1.5F;
            }

            this.setColdStrength(this.getColdStrength() - this.getColdProgressionRate() * var3 * GameTime.instance.getMultiplier());
            if (this.getColdReduction() > 0.0F) {
               this.setColdStrength(this.getColdStrength() - this.getColdProgressionRate() * var3 * GameTime.instance.getMultiplier());
            }

            if (this.getColdStrength() < 0.0F) {
               this.setColdStrength(0.0F);
               this.setHasACold(false);
               this.setCatchACold(0.0F);
            }
         } else {
            var3 = 1.0F;
            if (this.getParentChar().HasTrait("ProneToIllness")) {
               var3 = 1.2F;
            }

            if (this.getParentChar().HasTrait("Resilient")) {
               var3 = 0.8F;
            }

            this.setColdStrength(this.getColdStrength() + this.getColdProgressionRate() * var3 * GameTime.instance.getMultiplier());
            if (this.getColdStrength() > 100.0F) {
               this.setColdStrength(100.0F);
            }
         }

         if (this.getSneezeCoughTime() > 0) {
            this.setSneezeCoughTime(this.getSneezeCoughTime() - 1);
            if (this.getSneezeCoughTime() == 0) {
               this.setSneezeCoughActive(0);
            }
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HasACold) > 1 && this.getTimeToSneezeOrCough() >= 0 && !this.ParentChar.IsSpeaking()) {
            this.setTimeToSneezeOrCough(this.getTimeToSneezeOrCough() - 1);
            if (this.getTimeToSneezeOrCough() <= 0) {
               this.TriggerSneezeCough();
            }
         }
      }

   }

   public float getColdStrength() {
      return this.isHasACold() ? this.ColdStrength : 0.0F;
   }

   public float getWetness() {
      return this.Wetness;
   }

   public void AddDamage(BodyPartType var1, float var2) {
      ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).AddDamage(var2);
   }

   public void AddGeneralHealth(float var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < BodyPartType.ToIndex(BodyPartType.MAX); ++var3) {
         if (((BodyPart)this.getBodyParts().get(var3)).getHealth() < 100.0F) {
            ++var2;
         }
      }

      if (var2 > 0) {
         float var5 = var1 / (float)var2;

         for(int var4 = 0; var4 < BodyPartType.ToIndex(BodyPartType.MAX); ++var4) {
            if (((BodyPart)this.getBodyParts().get(var4)).getHealth() < 100.0F) {
               ((BodyPart)this.getBodyParts().get(var4)).AddHealth(var5);
            }
         }
      }

   }

   public void ReduceGeneralHealth(float var1) {
      float var2 = var1 / (float)BodyPartType.ToIndex(BodyPartType.MAX);

      for(int var3 = 0; var3 < BodyPartType.ToIndex(BodyPartType.MAX); ++var3) {
         ((BodyPart)this.getBodyParts().get(var3)).ReduceHealth(var2);
      }

   }

   public void AddDamage(int var1, float var2) {
      ((BodyPart)this.getBodyParts().get(var1)).AddDamage(var2);
   }

   public void DamageFromWeapon(HandWeapon var1) {
      if (GameServer.bServer) {
         if (var1 != null) {
            this.getParentChar().sendObjectChange("DamageFromWeapon", new Object[]{"weapon", var1.getFullType()});
         }

      } else {
         boolean var2 = false;
         byte var3 = 1;
         int var6 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.Groin) + 1);
         this.getParentChar().splatBloodFloorBig(0.4F);
         this.getParentChar().splatBloodFloorBig(0.4F);
         this.getParentChar().splatBloodFloorBig(0.4F);
         boolean var4 = true;
         if (var1.getCategories().contains("Blunt")) {
            var4 = false;
            var3 = 0;
         }

         if (var4 && !var1.isAimedFirearm()) {
            var6 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.MAX));
            this.SetScratchedFromWeapon(var6, true);
         }

         float var5 = Rand.Next(var1.getMinDamage(), var1.getMaxDamage()) * 15.0F;
         if (var1.isAimedFirearm()) {
            ((BodyPart)this.getBodyParts().get(var6)).damageFromFirearm(var5 * 2.0F);
         }

         if (var6 == BodyPartType.ToIndex(BodyPartType.Head)) {
            var5 *= 4.0F;
         }

         if (var6 == BodyPartType.ToIndex(BodyPartType.Neck)) {
            var5 *= 4.0F;
         }

         if (var6 == BodyPartType.ToIndex(BodyPartType.Torso_Upper)) {
            var5 *= 4.0F;
         }

         this.AddDamage(var6, var5);
         Stats var10000;
         switch(var3) {
         case 0:
            var10000 = this.ParentChar.getStats();
            var10000.Pain += this.getInitialThumpPain() * BodyPartType.getPainModifyer(var6);
            break;
         case 1:
            var10000 = this.ParentChar.getStats();
            var10000.Pain += this.getInitialScratchPain() * BodyPartType.getPainModifyer(var6);
            break;
         case 2:
            var10000 = this.ParentChar.getStats();
            var10000.Pain += this.getInitialBitePain() * BodyPartType.getPainModifyer(var6);
         }

         if (this.getParentChar().getStats().Pain > 100.0F) {
            this.ParentChar.getStats().Pain = 100.0F;
         }

      }
   }

   public void AddRandomDamageFromZombie(IsoZombie var1) {
      this.getParentChar().setHitBy((IsoGameCharacter)null);
      if (GameServer.bServer) {
         this.getParentChar().sendObjectChange("AddRandomDamageFromZombie", new Object[]{"zombie", var1.OnlineID});
      } else {
         int var2 = 450;
         byte var3 = 0;
         if (this.getParentChar().getSprite() != null && this.getParentChar().getSprite().CurrentAnim != null && "Idle".equals(this.getParentChar().getSprite().CurrentAnim.name)) {
            var2 *= 3;
         }

         var2 *= this.CountSurroundingZombies(this.getParentChar().getCell(), this.getParentChar().getCurrentSquare());
         if (this.getParentChar().getBodyDamage().getHealth() <= 0.0F) {
            var2 *= 10;
         }

         boolean var4 = false;
         int var5 = 75 + this.getParentChar().getMeleeCombatMod();
         byte var6 = 75;
         if (this.ParentChar.HasTrait("ThickSkinned")) {
            var5 = 85 + this.getParentChar().getMeleeCombatMod();
         }

         if (this.ParentChar.HasTrait("ThinSkinned")) {
            var5 = 65 + this.getParentChar().getMeleeCombatMod();
         }

         int var9;
         if (!var1.bCrawling) {
            var9 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.Groin) + 1);
            if ((var9 == BodyPartType.ToIndex(BodyPartType.Head) || var9 == BodyPartType.ToIndex(BodyPartType.Neck)) && Rand.Next(100) > 70) {
               boolean var7 = false;

               label139:
               while(true) {
                  do {
                     if (var7) {
                        break label139;
                     }

                     var7 = true;
                     var9 = Rand.Next(BodyPartType.ToIndex(BodyPartType.MAX));
                  } while(var9 != BodyPartType.ToIndex(BodyPartType.Head) && var9 != BodyPartType.ToIndex(BodyPartType.Neck));

                  var7 = false;
               }
            }
         } else {
            if (Rand.Next(2) != 0) {
               return;
            }

            var9 = Rand.Next(BodyPartType.ToIndex(BodyPartType.UpperLeg_L), BodyPartType.ToIndex(BodyPartType.MAX));
         }

         float var10 = (float)Rand.Next(1000) / 1000.0F;
         var10 *= (float)(Rand.Next(10) + 10);
         this.AddDamage(var9, var10);
         if (GameServer.bServer && this.ParentChar instanceof IsoPlayer) {
            DebugLog.log(DebugType.Combat, "zombie did " + var10 + " dmg to " + ((IsoPlayer)this.ParentChar).getDisplayName());
         }

         boolean var8 = false;
         if (Rand.Next(100) > var5) {
            var8 = true;
            if (SandboxOptions.instance.ClothingDegradation.getValue() > 1 && var9 < BodyPartType.ToIndex(BodyPartType.Head) && this.getParentChar().getClothingItem_Torso() != null && this.getParentChar().getClothingItem_Torso() instanceof Clothing && Rand.Next(((Clothing)this.getParentChar().getClothingItem_Torso()).getConditionLowerChance()) == 0) {
               this.getParentChar().getClothingItem_Torso().setCondition(this.getParentChar().getClothingItem_Torso().getCondition() - 1);
            }

            if (SandboxOptions.instance.ClothingDegradation.getValue() > 1 && var9 > BodyPartType.ToIndex(BodyPartType.Groin) && var9 < BodyPartType.ToIndex(BodyPartType.Foot_L) && this.getParentChar().getClothingItem_Legs() != null && this.getParentChar().getClothingItem_Legs() instanceof Clothing && Rand.Next(((Clothing)this.getParentChar().getClothingItem_Legs()).getConditionLowerChance()) == 0) {
               this.getParentChar().getClothingItem_Legs().setCondition(this.getParentChar().getClothingItem_Legs().getCondition() - 1);
            }

            if (Rand.Next(100) > var6) {
               var8 = false;
            }

            if (var8) {
               this.SetScratched(var9, true);
               if (this.getHealth() > 0.0F) {
                  this.getParentChar().getEmitter().playSound("ZombieScratch");
               }

               var3 = 1;
               if (GameServer.bServer && this.ParentChar instanceof IsoPlayer) {
                  DebugLog.log(DebugType.Combat, "zombie scratched " + ((IsoPlayer)this.ParentChar).username);
               }

               this.getParentChar().Scratched();
            } else {
               if (this.getHealth() > 0.0F) {
                  this.getParentChar().getEmitter().playSound("ZombieBite");
               }

               this.SetBitten(var9, true);
               if (GameServer.bServer && this.ParentChar instanceof IsoPlayer) {
                  DebugLog.log(DebugType.Combat, "zombie bite " + ((IsoPlayer)this.ParentChar).username);
               }

               var3 = 2;
               this.getParentChar().Bitten();
               this.getParentChar().splatBloodFloorBig(0.4F);
               this.getParentChar().splatBloodFloorBig(0.4F);
               this.getParentChar().splatBloodFloorBig(0.4F);
            }
         } else if (this.getParentChar().getPrimaryHandItem() != null && !this.getParentChar().getPrimaryHandItem().getName().contains("Bare Hands")) {
            if (this.getParentChar().haveBladeWeapon()) {
               this.getParentChar().getXp().AddXP(PerkFactory.Perks.BladeGuard, 4.0F);
            } else {
               this.getParentChar().getXp().AddXP(PerkFactory.Perks.BluntGuard, 4.0F);
            }
         }

         Stats var10000;
         switch(var3) {
         case 0:
            var10000 = this.ParentChar.getStats();
            var10000.Pain += this.getInitialThumpPain() * BodyPartType.getPainModifyer(var9);
            break;
         case 1:
            var10000 = this.ParentChar.getStats();
            var10000.Pain += this.getInitialScratchPain() * BodyPartType.getPainModifyer(var9);
            break;
         case 2:
            var10000 = this.ParentChar.getStats();
            var10000.Pain += this.getInitialBitePain() * BodyPartType.getPainModifyer(var9);
         }

         if (this.getParentChar().getStats().Pain > 100.0F) {
            this.ParentChar.getStats().Pain = 100.0F;
         }

         if (var3 > 0) {
            this.HurtBloodSplats(Rand.Next(2) + 1);
            this.HurtBloodSplats(Rand.Next(2) + 1);
            this.HurtBloodSplats(Rand.Next(2) + 1);
            this.HurtBloodSplats(Rand.Next(2) + 1);
         }

         if (GameClient.bClient && ServerOptions.instance.PlayerSaveOnDamage.getValue()) {
            GameWindow.savePlayer();
         }

      }
   }

   private void HurtBloodSplats(int var1) {
   }

   private int CountSurroundingZombies(IsoCell var1, IsoGridSquare var2) {
      if (var2 == null) {
         return 0;
      } else {
         int var3 = 0;
         IsoGridSquare var4 = null;
         var4 = var1.getGridSquare(var2.getX(), var2.getY() - 1, var2.getZ());
         if (var4 != null) {
            var3 += var4.getMovingObjects().size();
         }

         var4 = var1.getGridSquare(var2.getX() + 1, var2.getY() - 1, var2.getZ());
         if (var4 != null) {
            var3 += var4.getMovingObjects().size();
         }

         var4 = var1.getGridSquare(var2.getX() + 1, var2.getY(), var2.getZ());
         if (var4 != null) {
            var3 += var4.getMovingObjects().size();
         }

         var4 = var1.getGridSquare(var2.getX() + 1, var2.getY() + 1, var2.getZ());
         if (var4 != null) {
            var3 += var4.getMovingObjects().size();
         }

         var4 = var1.getGridSquare(var2.getX(), var2.getY() + 1, var2.getZ());
         if (var4 != null) {
            var3 += var4.getMovingObjects().size();
         }

         var4 = var1.getGridSquare(var2.getX() - 1, var2.getY() + 1, var2.getZ());
         if (var4 != null) {
            var3 += var4.getMovingObjects().size();
         }

         var4 = var1.getGridSquare(var2.getX() - 1, var2.getY(), var2.getZ());
         if (var4 != null) {
            var3 += var4.getMovingObjects().size();
         }

         var4 = var1.getGridSquare(var2.getX() - 1, var2.getY() - 1, var2.getZ());
         if (var4 != null) {
            var3 += var4.getMovingObjects().size();
         }

         var4 = var1.getGridSquare(var2.getX(), var2.getY(), var2.getZ());
         if (var4 != null) {
            var3 += var4.getMovingObjects().size();
         }

         if (var3 > 0) {
            --var3;
         }

         return var3;
      }
   }

   public boolean DoesBodyPartHaveInjury(BodyPartType var1) {
      return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).HasInjury();
   }

   public void DrawUntexturedQuad(int var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      SpriteRenderer.instance.render((Texture)null, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public float getBodyPartHealth(BodyPartType var1) {
      return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).getHealth();
   }

   public float getBodyPartHealth(int var1) {
      return ((BodyPart)this.getBodyParts().get(var1)).getHealth();
   }

   public String getBodyPartName(BodyPartType var1) {
      return BodyPartType.ToString(var1);
   }

   public String getBodyPartName(int var1) {
      return BodyPartType.ToString(BodyPartType.FromIndex(var1));
   }

   public float getHealth() {
      return this.getOverallBodyHealth();
   }

   public float getInfectionLevel() {
      return this.InfectionLevel;
   }

   public float getApparentInfectionLevel() {
      float var1 = this.getFakeInfectionLevel() > this.InfectionLevel ? this.getFakeInfectionLevel() : this.InfectionLevel;
      return this.getFoodSicknessLevel() > var1 ? this.getFoodSicknessLevel() : var1;
   }

   public int getNumPartsBleeding() {
      int var1 = 0;

      for(int var2 = 0; var2 < BodyPartType.ToIndex(BodyPartType.MAX); ++var2) {
         if (((BodyPart)this.getBodyParts().get(var2)).bleeding()) {
            ++var1;
         }
      }

      return var1;
   }

   public int getNumPartsScratched() {
      int var1 = 0;

      for(int var2 = 0; var2 < BodyPartType.ToIndex(BodyPartType.MAX); ++var2) {
         if (((BodyPart)this.getBodyParts().get(var2)).scratched()) {
            ++var1;
         }
      }

      return var1;
   }

   public int getNumPartsBitten() {
      int var1 = 0;

      for(int var2 = 0; var2 < BodyPartType.ToIndex(BodyPartType.MAX); ++var2) {
         if (((BodyPart)this.getBodyParts().get(var2)).bitten()) {
            ++var1;
         }
      }

      return var1;
   }

   public boolean HasInjury() {
      for(int var1 = 0; var1 < BodyPartType.ToIndex(BodyPartType.MAX); ++var1) {
         if (((BodyPart)this.getBodyParts().get(var1)).HasInjury()) {
            return true;
         }
      }

      return false;
   }

   public boolean IsBandaged(BodyPartType var1) {
      return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).bandaged();
   }

   public boolean IsDeepWounded(BodyPartType var1) {
      return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).deepWounded();
   }

   public boolean IsBandaged(int var1) {
      return ((BodyPart)this.getBodyParts().get(var1)).bandaged();
   }

   public boolean IsBitten(BodyPartType var1) {
      return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).bitten();
   }

   public boolean IsBitten(int var1) {
      return ((BodyPart)this.getBodyParts().get(var1)).bitten();
   }

   public boolean IsBleeding(BodyPartType var1) {
      return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).bleeding();
   }

   public boolean IsBleeding(int var1) {
      return ((BodyPart)this.getBodyParts().get(var1)).bleeding();
   }

   public boolean IsBleedingStemmed(BodyPartType var1) {
      return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).IsBleedingStemmed();
   }

   public boolean IsBleedingStemmed(int var1) {
      return ((BodyPart)this.getBodyParts().get(var1)).IsBleedingStemmed();
   }

   public boolean IsCortorised(BodyPartType var1) {
      return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).IsCortorised();
   }

   public boolean IsCortorised(int var1) {
      return ((BodyPart)this.getBodyParts().get(var1)).IsCortorised();
   }

   public boolean IsInfected() {
      return this.IsInfected;
   }

   public boolean IsInfected(BodyPartType var1) {
      return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).IsInfected();
   }

   public boolean IsInfected(int var1) {
      return ((BodyPart)this.getBodyParts().get(var1)).IsInfected();
   }

   public boolean IsFakeInfected(int var1) {
      return ((BodyPart)this.getBodyParts().get(var1)).IsFakeInfected();
   }

   public void DisableFakeInfection(int var1) {
      ((BodyPart)this.getBodyParts().get(var1)).DisableFakeInfection();
   }

   public boolean IsScratched(BodyPartType var1) {
      return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).scratched();
   }

   public boolean IsScratched(int var1) {
      return ((BodyPart)this.getBodyParts().get(var1)).scratched();
   }

   public boolean IsStitched(BodyPartType var1) {
      return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).stitched();
   }

   public boolean IsStitched(int var1) {
      return ((BodyPart)this.getBodyParts().get(var1)).stitched();
   }

   public boolean IsWounded(BodyPartType var1) {
      return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).deepWounded();
   }

   public boolean IsWounded(int var1) {
      return ((BodyPart)this.getBodyParts().get(var1)).deepWounded();
   }

   public void RestoreToFullHealth() {
      for(int var1 = 0; var1 < BodyPartType.ToIndex(BodyPartType.MAX); ++var1) {
         ((BodyPart)this.getBodyParts().get(var1)).RestoreToFullHealth();
      }

      if (this.getParentChar() != null && this.getParentChar().getStats() != null) {
         this.getParentChar().getStats().setEndurance(1.0F);
         this.getParentChar().getStats().setPain(0.0F);
         this.getParentChar().getStats().setDrunkenness(0.0F);
      }

      this.setInfected(false);
      this.setIsFakeInfected(false);
      this.setOverallBodyHealth(100.0F);
      this.setInfectionLevel(0.0F);
      this.setFakeInfectionLevel(0.0F);
      this.setBoredomLevel(0.0F);
      this.setWetness(0.0F);
      this.setCatchACold(0.0F);
      this.setHasACold(false);
      this.setColdStrength(0.0F);
      this.setSneezeCoughActive(0);
      this.setSneezeCoughTime(0);
      this.setTemperature(37.0F);
      this.setUnhappynessLevel(0.0F);
      this.PoisonLevel = 0.0F;
      this.setFoodSicknessLevel(0.0F);
      this.Temperature = 37.0F;
      this.lastTemperature = this.Temperature;
      this.setInfectionTime(-1.0F);
      this.setInfectionMortalityDuration(-1.0F);
   }

   public void SetBandaged(int var1, boolean var2, float var3, boolean var4, String var5) {
      ((BodyPart)this.getBodyParts().get(var1)).setBandaged(var2, var3, var4, var5);
   }

   public void SetBitten(BodyPartType var1, boolean var2) {
      ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).SetBitten(var2);
   }

   public void SetBitten(int var1, boolean var2) {
      ((BodyPart)this.getBodyParts().get(var1)).SetBitten(var2);
   }

   public void SetBitten(int var1, boolean var2, boolean var3) {
      ((BodyPart)this.getBodyParts().get(var1)).SetBitten(var2, var3);
   }

   public void SetBleeding(BodyPartType var1, boolean var2) {
      ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).setBleeding(var2);
   }

   public void SetBleeding(int var1, boolean var2) {
      ((BodyPart)this.getBodyParts().get(var1)).setBleeding(var2);
   }

   public void SetBleedingStemmed(BodyPartType var1, boolean var2) {
      ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).SetBleedingStemmed(var2);
   }

   public void SetBleedingStemmed(int var1, boolean var2) {
      ((BodyPart)this.getBodyParts().get(var1)).SetBleedingStemmed(var2);
   }

   public void SetCortorised(BodyPartType var1, boolean var2) {
      ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).SetCortorised(var2);
   }

   public void SetCortorised(int var1, boolean var2) {
      ((BodyPart)this.getBodyParts().get(var1)).SetCortorised(var2);
   }

   public BodyPart setScratchedWindow() {
      int var1 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.ForeArm_R) + 1);
      this.getBodyPart(BodyPartType.FromIndex(var1)).AddDamage(10.0F);
      this.getBodyPart(BodyPartType.FromIndex(var1)).SetScratchedWindow(true);
      return this.getBodyPart(BodyPartType.FromIndex(var1));
   }

   public void SetScratched(BodyPartType var1, boolean var2) {
      ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).setScratched(var2);
   }

   public void SetScratched(int var1, boolean var2) {
      ((BodyPart)this.getBodyParts().get(var1)).setScratched(var2);
   }

   public void SetScratchedFromWeapon(int var1, boolean var2) {
      ((BodyPart)this.getBodyParts().get(var1)).SetScratchedWeapon(var2);
   }

   public void SetWounded(BodyPartType var1, boolean var2) {
      ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(var1))).setDeepWounded(var2);
   }

   public void SetWounded(int var1, boolean var2) {
      ((BodyPart)this.getBodyParts().get(var1)).setDeepWounded(var2);
   }

   public void ShowDebugInfo() {
      if (this.getDamageModCount() > 0) {
         this.setDamageModCount(this.getDamageModCount() - 1);
      }

   }

   public void UpdateBoredom() {
      if (!(this.getParentChar() instanceof IsoSurvivor)) {
         if (!(this.getParentChar() instanceof IsoPlayer) || !((IsoPlayer)this.getParentChar()).Asleep) {
            if (this.getParentChar().getCurrentSquare().getRoom() != null) {
               if (!this.getParentChar().isReading()) {
                  this.setBoredomLevel((float)((double)this.getBoredomLevel() + ZomboidGlobals.BoredomIncreaseRate * (double)GameTime.instance.getMultiplier()));
               } else {
                  this.setBoredomLevel((float)((double)this.getBoredomLevel() + ZomboidGlobals.BoredomIncreaseRate / 5.0D * (double)GameTime.instance.getMultiplier()));
               }

               if (this.getParentChar().IsSpeaking() && !this.getParentChar().callOut) {
                  this.setBoredomLevel((float)((double)this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * (double)GameTime.instance.getMultiplier()));
               }

               if (this.getParentChar().getNumSurvivorsInVicinity() > 0) {
                  this.setBoredomLevel((float)((double)this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * 0.10000000149011612D * (double)GameTime.instance.getMultiplier()));
               }
            } else if (this.getParentChar().getVehicle() != null) {
               float var1 = this.getParentChar().getVehicle().getCurrentSpeedKmHour();
               if (Math.abs(var1) <= 0.1F) {
                  if (this.getParentChar().isReading()) {
                     this.setBoredomLevel((float)((double)this.getBoredomLevel() + ZomboidGlobals.BoredomIncreaseRate / 5.0D * (double)GameTime.instance.getMultiplier()));
                  } else {
                     this.setBoredomLevel((float)((double)this.getBoredomLevel() + ZomboidGlobals.BoredomIncreaseRate * (double)GameTime.instance.getMultiplier()));
                  }
               } else {
                  this.setBoredomLevel((float)((double)this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * 0.5D * (double)GameTime.instance.getMultiplier()));
               }
            } else {
               this.setBoredomLevel((float)((double)this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * 0.10000000149011612D * (double)GameTime.instance.getMultiplier()));
            }

            if (this.getParentChar().getStats().Drunkenness > 20.0F) {
               this.setBoredomLevel((float)((double)this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * 2.0D * (double)GameTime.instance.getMultiplier()));
            }

            if (this.getParentChar().getStats().Panic > 5.0F) {
               this.setBoredomLevel(0.0F);
            }

            if (this.getBoredomLevel() > 100.0F) {
               this.setBoredomLevel(100.0F);
            }

            if (this.getBoredomLevel() < 0.0F) {
               this.setBoredomLevel(0.0F);
            }

            if (this.getUnhappynessLevel() > 100.0F) {
               this.setUnhappynessLevel(100.0F);
            }

            if (this.getUnhappynessLevel() < 0.0F) {
               this.setUnhappynessLevel(0.0F);
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bored) > 1 && !this.getParentChar().isReading()) {
               this.setUnhappynessLevel((float)((double)this.getUnhappynessLevel() + ZomboidGlobals.UnhappinessIncrease * (double)((float)this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bored)) * (double)GameTime.instance.getMultiplier()));
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Stress) > 1 && !this.getParentChar().isReading()) {
               this.setUnhappynessLevel((float)((double)this.getUnhappynessLevel() + ZomboidGlobals.UnhappinessIncrease / 2.0D * (double)((float)this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Stress)) * (double)GameTime.instance.getMultiplier()));
            }

            if (this.getParentChar().HasTrait("Smoker")) {
               this.getParentChar().setTimeSinceLastSmoke(this.getParentChar().getTimeSinceLastSmoke() + 1.0E-4F * GameTime.instance.getMultiplier());
               if (this.getParentChar().getTimeSinceLastSmoke() > 1.0F) {
                  double var3 = Math.floor((double)(this.getParentChar().getTimeSinceLastSmoke() / 10.0F)) + 1.0D;
                  if (var3 > 10.0D) {
                     var3 = 10.0D;
                  }

                  this.getParentChar().getStats().setStressFromCigarettes((float)((double)this.getParentChar().getStats().getStressFromCigarettes() + ZomboidGlobals.StressFromBiteOrScratch / 8.0D * var3 * (double)GameTime.instance.getMultiplier()));
                  if (this.getParentChar().getStats().getStressFromCigarettes() > 0.51F) {
                     this.getParentChar().getStats().setStressFromCigarettes(0.51F);
                  }
               }
            }

         }
      }
   }

   public float getUnhappynessLevel() {
      return this.UnhappynessLevel;
   }

   public float getBoredomLevel() {
      return this.BoredomLevel;
   }

   public void UpdateStrength() {
      if (this.getParentChar() == this.getParentChar()) {
         int var1 = 0;
         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 2) {
            ++var1;
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 3) {
            var1 += 2;
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4) {
            var1 += 2;
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 2) {
            ++var1;
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 3) {
            var1 += 2;
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4) {
            var1 += 2;
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 2) {
            ++var1;
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 3) {
            var1 += 2;
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 4) {
            var1 += 3;
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bleeding) == 2) {
            ++var1;
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bleeding) == 3) {
            ++var1;
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bleeding) == 4) {
            ++var1;
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Injured) == 2) {
            ++var1;
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Injured) == 3) {
            var1 += 2;
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Injured) == 4) {
            var1 += 3;
         }

         this.getParentChar().setMaxWeight(Integer.valueOf((int)((float)this.getParentChar().getMaxWeightBase() * this.getParentChar().getWeightMod())) - var1);
         if (this.getParentChar().getMaxWeight() < 0) {
            this.getParentChar().setMaxWeight(0);
         }

         if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.FoodEaten) > 0) {
            this.getParentChar().setMaxWeight(this.getParentChar().getMaxWeight() + 2);
         }

         if (this.getParentChar() instanceof IsoPlayer) {
            this.getParentChar().setMaxWeight((int)((float)this.getParentChar().getMaxWeight() * ((IsoPlayer)this.getParentChar()).getMaxWeightDelta()));
         }

      }
   }

   public float pickMortalityDuration() {
      float var1 = 1.0F;
      if (this.getParentChar().HasTrait("Resilient")) {
         var1 = 1.25F;
      }

      if (this.getParentChar().HasTrait("ProneToIllness")) {
         var1 = 0.75F;
      }

      switch(SandboxOptions.instance.Lore.Mortality.getValue()) {
      case 1:
         return 0.0F;
      case 2:
         return Rand.Next(0.0F, 30.0F) / 3600.0F * var1;
      case 3:
         return Rand.Next(0.5F, 1.0F) / 60.0F * var1;
      case 4:
         return Rand.Next(3.0F, 12.0F) * var1;
      case 5:
         return Rand.Next(2.0F, 3.0F) * 24.0F * var1;
      case 6:
         return Rand.Next(1.0F, 2.0F) * 7.0F * 24.0F * var1;
      case 7:
         return -1.0F;
      default:
         return -1.0F;
      }
   }

   public void Update() {
      if (!(this.getParentChar() instanceof IsoZombie)) {
         if (GameServer.bServer) {
            this.RestoreToFullHealth();
         } else if (GameClient.bClient && this.getParentChar() instanceof IsoPlayer && ((IsoPlayer)this.getParentChar()).bRemote) {
            this.RestoreToFullHealth();
         } else if (this.getParentChar().godMod) {
            this.RestoreToFullHealth();
         } else {
            Core.getInstance();
            float var1 = this.ParentChar.getStats().Pain;
            int var2 = this.getNumPartsBleeding() * 2;
            var2 += this.getNumPartsScratched();
            var2 += this.getNumPartsBitten() * 6;
            float var3;
            if (var2 > 0 && this.getHealth() < 60.0F || var2 > 3) {
               var3 = 1.0F / (float)var2 * 200.0F * GameTime.instance.getInvMultiplier();
               if ((float)Rand.Next((int)var3) < var3 * 0.3F) {
                  this.getParentChar().splatBloodFloor(0.3F);
               }

               if (Rand.Next((int)var3) == 0) {
                  this.getParentChar().splatBloodFloor(0.3F);
               }
            }

            this.UpdateWetness();
            this.UpdateCold();
            this.UpdateBoredom();
            this.UpdateStrength();
            this.UpdatePanicState();
            this.UpdateTemperatureState();
            this.UpdateIllness();
            if (this.getOverallBodyHealth() != 0.0F) {
               if (this.PoisonLevel == 0.0F && this.getFoodSicknessLevel() > 0.0F) {
                  this.setFoodSicknessLevel(this.getFoodSicknessLevel() - (float)(ZomboidGlobals.FoodSicknessDecrease * (double)GameTime.instance.getMultiplier()));
               }

               int var8;
               if (!this.isInfected()) {
                  for(var8 = 0; var8 < BodyPartType.ToIndex(BodyPartType.MAX); ++var8) {
                     if (this.IsInfected(var8)) {
                        this.setInfected(true);
                        if (this.IsFakeInfected(var8)) {
                           this.DisableFakeInfection(var8);
                           this.setInfectionLevel(this.getFakeInfectionLevel());
                           this.setFakeInfectionLevel(0.0F);
                           this.setIsFakeInfected(false);
                           this.setReduceFakeInfection(false);
                        }
                     }
                  }

                  if (this.isInfected() && this.getInfectionTime() < 0.0F && SandboxOptions.instance.Lore.Mortality.getValue() != 7) {
                     this.setInfectionTime(this.getCurrentTimeForInfection());
                     this.setInfectionMortalityDuration(this.pickMortalityDuration());
                  }
               }

               if (!this.isInfected() && !this.isIsFakeInfected()) {
                  for(var8 = 0; var8 < BodyPartType.ToIndex(BodyPartType.MAX); ++var8) {
                     if (this.IsFakeInfected(var8)) {
                        this.setIsFakeInfected(true);
                        break;
                     }
                  }
               }

               if (this.isIsFakeInfected() && !this.isReduceFakeInfection() && this.getParentChar().getReduceInfectionPower() == 0.0F) {
                  this.setFakeInfectionLevel(this.getFakeInfectionLevel() + this.getInfectionGrowthRate() * GameTime.instance.getMultiplier());
                  if (this.getFakeInfectionLevel() > 100.0F) {
                     this.setFakeInfectionLevel(100.0F);
                     this.setReduceFakeInfection(true);
                  }
               }

               Stats var10000 = this.ParentChar.getStats();
               var10000.Drunkenness -= this.getDrunkReductionValue() * GameTime.instance.getMultiplier();
               if (this.getParentChar().getStats().Drunkenness < 0.0F) {
                  this.ParentChar.getStats().Drunkenness = 0.0F;
               }

               var3 = 0.0F;
               if (this.getHealthFromFoodTimer() > 0.0F) {
                  var3 += this.getHealthFromFood() * GameTime.instance.getMultiplier();
                  this.setHealthFromFoodTimer(this.getHealthFromFoodTimer() - 1.0F * GameTime.instance.getMultiplier());
               }

               byte var4 = 0;
               if (this.getParentChar() == this.getParentChar() && (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 2 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 2 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 2)) {
                  var4 = 1;
               }

               if (this.getParentChar() == this.getParentChar() && (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 3 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 3 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 3)) {
                  var4 = 2;
               }

               if (this.getParentChar() == this.getParentChar() && (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4)) {
                  var4 = 3;
               }

               if (this.getParentChar().isAsleep()) {
                  var4 = -1;
               }

               switch(var4) {
               case 0:
                  var3 += this.getStandardHealthAddition() * GameTime.instance.getMultiplier();
                  break;
               case 1:
                  var3 += this.getReducedHealthAddition() * GameTime.instance.getMultiplier();
                  break;
               case 2:
                  var3 += this.getSeverlyReducedHealthAddition() * GameTime.instance.getMultiplier();
                  break;
               case 3:
                  var3 += 0.0F;
               }

               if (this.getParentChar().isAsleep()) {
                  if (GameClient.bClient) {
                     var3 += 15.0F * GameTime.instance.getGameWorldSecondsSinceLastUpdate() / 3600.0F;
                  } else {
                     var3 += this.getSleepingHealthAddition() * GameTime.instance.getMultiplier();
                  }

                  if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4) {
                     var3 = 0.0F;
                  }
               }

               this.AddGeneralHealth(var3);
               var3 = 0.0F;
               float var9;
               if (this.PoisonLevel > 0.0F) {
                  if (this.PoisonLevel > 10.0F && this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) >= 1) {
                     var3 += 0.0035F * Math.min(this.PoisonLevel / 10.0F, 3.0F) * GameTime.instance.getMultiplier();
                  }

                  var9 = 0.0F;
                  if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.FoodEaten) > 0) {
                     var9 = 1.5E-4F * (float)this.getParentChar().getMoodles().getMoodleLevel(MoodleType.FoodEaten);
                  }

                  this.PoisonLevel = (float)((double)this.PoisonLevel - ((double)var9 + ZomboidGlobals.PoisonLevelDecrease * (double)GameTime.instance.getMultiplier()));
                  if (this.PoisonLevel < 0.0F) {
                     this.PoisonLevel = 0.0F;
                  }

                  this.setFoodSicknessLevel(this.getFoodSicknessLevel() + this.getInfectionGrowthRate() * (float)(2 + Math.round(this.PoisonLevel / 10.0F)) * GameTime.instance.getMultiplier());
                  if (this.getFoodSicknessLevel() > 100.0F) {
                     this.setFoodSicknessLevel(100.0F);
                  }
               }

               if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4) {
                  var3 += this.getHealthReductionFromSevereBadMoodles() / 50.0F * GameTime.instance.getMultiplier();
               }

               if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 4 && (this.FakeInfectionLevel > this.InfectionLevel || this.FoodSicknessLevel > this.InfectionLevel)) {
                  var3 += this.getHealthReductionFromSevereBadMoodles() * GameTime.instance.getMultiplier();
               }

               if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bleeding) == 4) {
                  var3 += this.getHealthReductionFromSevereBadMoodles() * GameTime.instance.getMultiplier();
               }

               if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4) {
                  var3 += this.getHealthReductionFromSevereBadMoodles() / 10.0F * GameTime.instance.getMultiplier();
               }

               if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HeavyLoad) > 2 && this.getHealth() > 75.0F && Rand.Next(Rand.AdjustForFramerate(10)) == 0) {
                  var3 += this.getHealthReductionFromSevereBadMoodles() / ((float)(5 - this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HeavyLoad)) / 10.0F) * GameTime.instance.getMultiplier();
               }

               this.ReduceGeneralHealth(var3);
               int var10;
               if (this.ParentChar.getPainEffect() > 0.0F) {
                  var10000 = this.ParentChar.getStats();
                  var10000.Pain -= 0.023333333F * (GameTime.getInstance().getMultiplier() / 1.6F);
                  this.ParentChar.setPainEffect(this.ParentChar.getPainEffect() - GameTime.getInstance().getMultiplier() / 3.0F);
               } else {
                  this.ParentChar.setPainDelta(0.0F);
                  var3 = 0.0F;

                  for(var10 = 0; var10 < BodyPartType.ToIndex(BodyPartType.MAX); ++var10) {
                     var3 += ((BodyPart)this.getBodyParts().get(var10)).getPain() * BodyPartType.getPainModifyer(var10);
                  }

                  var3 -= this.getPainReduction();
                  if (var3 > this.ParentChar.getStats().Pain) {
                     var10000 = this.ParentChar.getStats();
                     var10000.Pain += (var3 - this.ParentChar.getStats().Pain) / 500.0F;
                  } else {
                     this.ParentChar.getStats().Pain = var3;
                  }
               }

               this.setPainReduction(this.getPainReduction() - 0.005F * GameTime.getInstance().getMultiplier());
               if (this.getPainReduction() < 0.0F) {
                  this.setPainReduction(0.0F);
               }

               if (this.getParentChar().getStats().Pain > 100.0F) {
                  this.ParentChar.getStats().Pain = 100.0F;
               }

               if (this.isInfected()) {
                  var8 = SandboxOptions.instance.Lore.Mortality.getValue();
                  if (var8 == 1) {
                     this.ReduceGeneralHealth(100.0F);
                     this.setInfectionLevel(100.0F);
                  } else if (var8 != 7) {
                     var9 = this.getCurrentTimeForInfection();
                     if (this.InfectionMortalityDuration < 0.0F) {
                        this.InfectionMortalityDuration = this.pickMortalityDuration();
                     }

                     if (this.InfectionTime < 0.0F) {
                        this.InfectionTime = var9;
                     }

                     if (this.InfectionTime > var9) {
                        this.InfectionTime = var9;
                     }

                     float var5 = (var9 - this.InfectionTime) / this.InfectionMortalityDuration;
                     var5 = Math.min(var5, 1.0F);
                     this.setInfectionLevel(var5 * 100.0F);
                     if (var5 == 1.0F) {
                        this.ReduceGeneralHealth(100.0F);
                     } else {
                        var5 *= var5;
                        var5 *= var5;
                        float var6 = (1.0F - var5) * 100.0F;
                        float var7 = this.getOverallBodyHealth() - var6;
                        if (var7 > 0.0F && var6 <= 99.0F) {
                           this.ReduceGeneralHealth(var7);
                        }
                     }
                  }
               }

               var3 = 0.0F;

               for(var10 = 0; var10 < BodyPartType.ToIndex(BodyPartType.MAX); ++var10) {
                  ((BodyPart)this.getBodyParts().get(var10)).DamageUpdate();
                  var3 += (100.0F - ((BodyPart)this.getBodyParts().get(var10)).getHealth()) * BodyPartType.getDamageModifyer(var10);
               }

               if (var3 > 100.0F) {
                  var3 = 100.0F;
               }

               var3 += this.getDamageFromPills();
               this.setOverallBodyHealth(100.0F - var3);
               if (this.getOverallBodyHealth() == 0.0F) {
                  if (this.isIsOnFire()) {
                     this.setBurntToDeath(true);

                     for(var10 = 0; var10 < BodyPartType.ToIndex(BodyPartType.MAX); ++var10) {
                        ((BodyPart)this.getBodyParts().get(var10)).SetHealth((float)Rand.Next(90));
                     }
                  } else {
                     this.setBurntToDeath(false);
                  }
               }

               if (this.isReduceFakeInfection() && this.getOverallBodyHealth() > 0.0F) {
                  this.setFakeInfectionLevel(this.getFakeInfectionLevel() - this.getInfectionGrowthRate() * GameTime.instance.getMultiplier() * 2.0F);
               }

               if (this.getParentChar().getReduceInfectionPower() > 0.0F && this.getOverallBodyHealth() > 0.0F) {
                  this.setFakeInfectionLevel(this.getFakeInfectionLevel() - this.getInfectionGrowthRate() * GameTime.instance.getMultiplier());
                  this.getParentChar().setReduceInfectionPower(this.getParentChar().getReduceInfectionPower() - this.getInfectionGrowthRate() * GameTime.instance.getMultiplier());
                  if (this.getParentChar().getReduceInfectionPower() < 0.0F) {
                     this.getParentChar().setReduceInfectionPower(0.0F);
                  }
               }

               if (this.getFakeInfectionLevel() <= 0.0F) {
                  for(var10 = 0; var10 < BodyPartType.ToIndex(BodyPartType.MAX); ++var10) {
                     ((BodyPart)this.getBodyParts().get(var10)).SetFakeInfected(false);
                  }

                  this.setIsFakeInfected(false);
                  this.setFakeInfectionLevel(0.0F);
                  this.setReduceFakeInfection(false);
               }

               if (var1 == this.ParentChar.getStats().Pain) {
                  var10000 = this.ParentChar.getStats();
                  var10000.Pain = (float)((double)var10000.Pain - 0.25D * (double)(GameTime.getInstance().getMultiplier() / 1.6F));
               }

               if (this.ParentChar.getStats().Pain < 0.0F) {
                  this.ParentChar.getStats().Pain = 0.0F;
               }

            }
         }
      }
   }

   public static float getSicknessFromCorpsesRate(int var0) {
      if (SandboxOptions.instance.DecayingCorpseHealthImpact.getValue() == 1) {
         return 0.0F;
      } else if (var0 > 5) {
         float var1 = (float)ZomboidGlobals.FoodSicknessDecrease * 0.07F;
         switch(SandboxOptions.instance.DecayingCorpseHealthImpact.getValue()) {
         case 2:
            var1 *= 0.01F;
            break;
         case 4:
            var1 *= 0.11F;
         }

         int var2 = Math.min(var0 - 5, 20);
         return var1 * (float)var2;
      } else {
         return 0.0F;
      }
   }

   private void UpdateIllness() {
      if (SandboxOptions.instance.DecayingCorpseHealthImpact.getValue() != 1) {
         int var1 = FliesSound.instance.getCorpseCount(this.getParentChar());
         float var2 = getSicknessFromCorpsesRate(var1);
         if (var2 > 0.0F) {
            this.setFoodSicknessLevel(this.getFoodSicknessLevel() + var2 * GameTime.getInstance().getMultiplier());
         }

      }
   }

   private void UpdateTemperatureState() {
      float var1 = 0.06F;
      int var2 = this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hypothermia);
      if (var2 == 2) {
         var1 = 0.05F;
      } else if (var2 == 3) {
         var1 = 0.04F;
      } else if (var2 == 4) {
         var1 = 0.03F;
      }

      var2 = this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hyperthermia);
      if (var2 == 2) {
         var1 = 0.05F;
      } else if (var2 == 3) {
         var1 = 0.04F;
      } else if (var2 == 4) {
         var1 = 0.03F;
      }

      if (this.getParentChar() instanceof IsoPlayer) {
         if (this.ColdDamageStage > 0.0F) {
            float var3 = 100.0F - this.ColdDamageStage * 100.0F;
            if (this.OverallBodyHealth > var3) {
               this.ReduceGeneralHealth(this.OverallBodyHealth - var3);
            }

            var1 -= 0.02F * this.ColdDamageStage;
         }

         ((IsoPlayer)this.getParentChar()).setMoveSpeed(var1);
      }

   }

   private float getDamageFromPills() {
      if (this.getParentChar() instanceof IsoPlayer) {
         IsoPlayer var1 = (IsoPlayer)this.getParentChar();
         if (var1.getSleepingPillsTaken() == 10) {
            return 40.0F;
         }

         if (var1.getSleepingPillsTaken() == 11) {
            return 80.0F;
         }

         if (var1.getSleepingPillsTaken() >= 12) {
            return 100.0F;
         }
      }

      return 0.0F;
   }

   public boolean UseBandageOnMostNeededPart() {
      int var1 = 0;
      BodyPart var2 = null;

      for(int var3 = 0; var3 < this.getBodyParts().size(); ++var3) {
         int var4 = 0;
         if (!((BodyPart)this.getBodyParts().get(var3)).bandaged()) {
            if (((BodyPart)this.getBodyParts().get(var3)).bleeding()) {
               var4 += 100;
            }

            if (((BodyPart)this.getBodyParts().get(var3)).scratched()) {
               var4 += 50;
            }

            if (((BodyPart)this.getBodyParts().get(var3)).bitten()) {
               var4 += 50;
            }

            if (var4 > var1) {
               var1 = var4;
               var2 = (BodyPart)this.getBodyParts().get(var3);
            }
         }
      }

      if (var1 > 0 && var2 != null) {
         var2.setBandaged(true, 10.0F);
         return true;
      } else {
         return false;
      }
   }

   public void ReduceFactor() {
   }

   public ArrayList getBodyParts() {
      return this.BodyParts;
   }

   public void setBodyParts(ArrayList var1) {
      this.BodyParts = var1;
   }

   public int getDamageModCount() {
      return this.DamageModCount;
   }

   public void setDamageModCount(int var1) {
      this.DamageModCount = var1;
   }

   public float getInfectionGrowthRate() {
      return this.InfectionGrowthRate;
   }

   public void setInfectionGrowthRate(float var1) {
      this.InfectionGrowthRate = var1;
   }

   public void setInfectionLevel(float var1) {
      this.InfectionLevel = var1;
   }

   public boolean isInfected() {
      return this.IsInfected;
   }

   public void setInfected(boolean var1) {
      this.IsInfected = var1;
   }

   public float getInfectionTime() {
      return this.InfectionTime;
   }

   public void setInfectionTime(float var1) {
      this.InfectionTime = var1;
   }

   public float getInfectionMortalityDuration() {
      return this.InfectionMortalityDuration;
   }

   public void setInfectionMortalityDuration(float var1) {
      this.InfectionMortalityDuration = var1;
   }

   private float getCurrentTimeForInfection() {
      return this.getParentChar() instanceof IsoPlayer ? (float)((IsoPlayer)this.getParentChar()).getHoursSurvived() : (float)GameTime.getInstance().getWorldAgeHours();
   }

   /** @deprecated */
   @Deprecated
   public boolean isInf() {
      return this.IsInfected;
   }

   /** @deprecated */
   @Deprecated
   public void setInf(boolean var1) {
      this.IsInfected = var1;
   }

   public float getFakeInfectionLevel() {
      return this.FakeInfectionLevel;
   }

   public void setFakeInfectionLevel(float var1) {
      this.FakeInfectionLevel = var1;
   }

   public boolean isIsFakeInfected() {
      return this.IsFakeInfected;
   }

   public void setIsFakeInfected(boolean var1) {
      this.IsFakeInfected = var1;
      ((BodyPart)this.getBodyParts().get(0)).SetFakeInfected(var1);
   }

   public float getOverallBodyHealth() {
      return this.OverallBodyHealth;
   }

   public void setOverallBodyHealth(float var1) {
      this.OverallBodyHealth = var1;
   }

   public float getStandardHealthAddition() {
      return this.StandardHealthAddition;
   }

   public void setStandardHealthAddition(float var1) {
      this.StandardHealthAddition = var1;
   }

   public float getReducedHealthAddition() {
      return this.ReducedHealthAddition;
   }

   public void setReducedHealthAddition(float var1) {
      this.ReducedHealthAddition = var1;
   }

   public float getSeverlyReducedHealthAddition() {
      return this.SeverlyReducedHealthAddition;
   }

   public void setSeverlyReducedHealthAddition(float var1) {
      this.SeverlyReducedHealthAddition = var1;
   }

   public float getSleepingHealthAddition() {
      return this.SleepingHealthAddition;
   }

   public void setSleepingHealthAddition(float var1) {
      this.SleepingHealthAddition = var1;
   }

   public float getHealthFromFood() {
      return this.HealthFromFood;
   }

   public void setHealthFromFood(float var1) {
      this.HealthFromFood = var1;
   }

   public float getHealthReductionFromSevereBadMoodles() {
      return this.HealthReductionFromSevereBadMoodles;
   }

   public void setHealthReductionFromSevereBadMoodles(float var1) {
      this.HealthReductionFromSevereBadMoodles = var1;
   }

   public int getStandardHealthFromFoodTime() {
      return this.StandardHealthFromFoodTime;
   }

   public void setStandardHealthFromFoodTime(int var1) {
      this.StandardHealthFromFoodTime = var1;
   }

   public float getHealthFromFoodTimer() {
      return this.HealthFromFoodTimer;
   }

   public void setHealthFromFoodTimer(float var1) {
      this.HealthFromFoodTimer = var1;
   }

   public void setBoredomLevel(float var1) {
      this.BoredomLevel = var1;
   }

   public float getBoredomDecreaseFromReading() {
      return this.BoredomDecreaseFromReading;
   }

   public void setBoredomDecreaseFromReading(float var1) {
      this.BoredomDecreaseFromReading = var1;
   }

   public float getInitialThumpPain() {
      return this.InitialThumpPain;
   }

   public void setInitialThumpPain(float var1) {
      this.InitialThumpPain = var1;
   }

   public float getInitialScratchPain() {
      return this.InitialScratchPain;
   }

   public void setInitialScratchPain(float var1) {
      this.InitialScratchPain = var1;
   }

   public float getInitialBitePain() {
      return this.InitialBitePain;
   }

   public void setInitialBitePain(float var1) {
      this.InitialBitePain = var1;
   }

   public float getInitialWoundPain() {
      return this.InitialWoundPain;
   }

   public void setInitialWoundPain(float var1) {
      this.InitialWoundPain = var1;
   }

   public float getContinualPainIncrease() {
      return this.ContinualPainIncrease;
   }

   public void setContinualPainIncrease(float var1) {
      this.ContinualPainIncrease = var1;
   }

   public float getPainReductionFromMeds() {
      return this.PainReductionFromMeds;
   }

   public void setPainReductionFromMeds(float var1) {
      this.PainReductionFromMeds = var1;
   }

   public float getStandardPainReductionWhenWell() {
      return this.StandardPainReductionWhenWell;
   }

   public void setStandardPainReductionWhenWell(float var1) {
      this.StandardPainReductionWhenWell = var1;
   }

   public int getOldNumZombiesVisible() {
      return this.OldNumZombiesVisible;
   }

   public void setOldNumZombiesVisible(int var1) {
      this.OldNumZombiesVisible = var1;
   }

   public int getCurrentNumZombiesVisible() {
      return this.CurrentNumZombiesVisible;
   }

   public void setCurrentNumZombiesVisible(int var1) {
      this.CurrentNumZombiesVisible = var1;
   }

   public float getPanicIncreaseValue() {
      return this.PanicIncreaseValue;
   }

   public void setPanicIncreaseValue(float var1) {
      this.PanicIncreaseValue = var1;
   }

   public float getPanicReductionValue() {
      return this.PanicReductionValue;
   }

   public void setPanicReductionValue(float var1) {
      this.PanicReductionValue = var1;
   }

   public float getDrunkIncreaseValue() {
      return this.DrunkIncreaseValue;
   }

   public void setDrunkIncreaseValue(float var1) {
      this.DrunkIncreaseValue = var1;
   }

   public float getDrunkReductionValue() {
      return this.DrunkReductionValue;
   }

   public void setDrunkReductionValue(float var1) {
      this.DrunkReductionValue = var1;
   }

   public boolean isIsOnFire() {
      return this.IsOnFire;
   }

   public void setIsOnFire(boolean var1) {
      this.IsOnFire = var1;
   }

   public boolean isBurntToDeath() {
      return this.BurntToDeath;
   }

   public void setBurntToDeath(boolean var1) {
      this.BurntToDeath = var1;
   }

   public void setWetness(float var1) {
      this.Wetness = var1;
   }

   public float getCatchACold() {
      return this.CatchACold;
   }

   public void setCatchACold(float var1) {
      this.CatchACold = var1;
   }

   public boolean isHasACold() {
      return this.HasACold;
   }

   public void setHasACold(boolean var1) {
      this.HasACold = var1;
   }

   public void setColdStrength(float var1) {
      this.ColdStrength = var1;
   }

   public float getColdProgressionRate() {
      return this.ColdProgressionRate;
   }

   public void setColdProgressionRate(float var1) {
      this.ColdProgressionRate = var1;
   }

   public int getTimeToSneezeOrCough() {
      return this.TimeToSneezeOrCough;
   }

   public void setTimeToSneezeOrCough(int var1) {
      this.TimeToSneezeOrCough = var1;
   }

   public int getMildColdSneezeTimerMin() {
      return this.MildColdSneezeTimerMin;
   }

   public void setMildColdSneezeTimerMin(int var1) {
      this.MildColdSneezeTimerMin = var1;
   }

   public int getMildColdSneezeTimerMax() {
      return this.MildColdSneezeTimerMax;
   }

   public void setMildColdSneezeTimerMax(int var1) {
      this.MildColdSneezeTimerMax = var1;
   }

   public int getColdSneezeTimerMin() {
      return this.ColdSneezeTimerMin;
   }

   public void setColdSneezeTimerMin(int var1) {
      this.ColdSneezeTimerMin = var1;
   }

   public int getColdSneezeTimerMax() {
      return this.ColdSneezeTimerMax;
   }

   public void setColdSneezeTimerMax(int var1) {
      this.ColdSneezeTimerMax = var1;
   }

   public int getNastyColdSneezeTimerMin() {
      return this.NastyColdSneezeTimerMin;
   }

   public void setNastyColdSneezeTimerMin(int var1) {
      this.NastyColdSneezeTimerMin = var1;
   }

   public int getNastyColdSneezeTimerMax() {
      return this.NastyColdSneezeTimerMax;
   }

   public void setNastyColdSneezeTimerMax(int var1) {
      this.NastyColdSneezeTimerMax = var1;
   }

   public int getSneezeCoughActive() {
      return this.SneezeCoughActive;
   }

   public void setSneezeCoughActive(int var1) {
      this.SneezeCoughActive = var1;
   }

   public int getSneezeCoughTime() {
      return this.SneezeCoughTime;
   }

   public void setSneezeCoughTime(int var1) {
      this.SneezeCoughTime = var1;
   }

   public int getSneezeCoughDelay() {
      return this.SneezeCoughDelay;
   }

   public void setSneezeCoughDelay(int var1) {
      this.SneezeCoughDelay = var1;
   }

   public void setUnhappynessLevel(float var1) {
      this.UnhappynessLevel = var1;
   }

   public IsoGameCharacter getParentChar() {
      return this.ParentChar;
   }

   public void setParentChar(IsoGameCharacter var1) {
      this.ParentChar = var1;
   }

   public float getTemperature() {
      return this.Temperature;
   }

   public void setTemperature(float var1) {
      this.lastTemperature = this.Temperature;
      this.Temperature = var1;
   }

   public float getTemperatureChangeTick() {
      return this.Temperature - this.lastTemperature;
   }

   public void setPoisonLevel(float var1) {
      this.PoisonLevel = var1;
   }

   public float getPoisonLevel() {
      return this.PoisonLevel;
   }

   public float getFoodSicknessLevel() {
      return this.FoodSicknessLevel;
   }

   public void setFoodSicknessLevel(float var1) {
      this.FoodSicknessLevel = Math.max(var1, 0.0F);
   }

   public boolean isReduceFakeInfection() {
      return this.reduceFakeInfection;
   }

   public void setReduceFakeInfection(boolean var1) {
      this.reduceFakeInfection = var1;
   }

   public void AddRandomDamage() {
      BodyPart var1 = (BodyPart)this.getBodyParts().get(Rand.Next(this.getBodyParts().size()));
      switch(Rand.Next(4)) {
      case 0:
         var1.generateDeepWound();
         if (Rand.Next(4) == 0) {
            var1.setInfectedWound(true);
         }
         break;
      case 1:
         var1.generateDeepShardWound();
         if (Rand.Next(4) == 0) {
            var1.setInfectedWound(true);
         }
         break;
      case 2:
         var1.setFractureTime((float)Rand.Next(30, 50));
         break;
      case 3:
         var1.setBurnTime((float)Rand.Next(30, 50));
      }

   }

   public float getPainReduction() {
      return this.painReduction;
   }

   public void setPainReduction(float var1) {
      this.painReduction = var1;
   }

   public float getColdReduction() {
      return this.coldReduction;
   }

   public void setColdReduction(float var1) {
      this.coldReduction = var1;
   }

   public int getRemotePainLevel() {
      return this.RemotePainLevel;
   }

   public void setRemotePainLevel(int var1) {
      this.RemotePainLevel = var1;
   }

   public float getColdDamageStage() {
      return this.ColdDamageStage;
   }

   public void setColdDamageStage(float var1) {
      this.ColdDamageStage = var1;
   }
}
