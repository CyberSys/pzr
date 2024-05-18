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

	public BodyDamage(IsoGameCharacter gameCharacter) {
		this.BodyParts.add(new BodyPart(BodyPartType.Hand_L, gameCharacter));
		this.BodyParts.add(new BodyPart(BodyPartType.Hand_R, gameCharacter));
		this.BodyParts.add(new BodyPart(BodyPartType.ForeArm_L, gameCharacter));
		this.BodyParts.add(new BodyPart(BodyPartType.ForeArm_R, gameCharacter));
		this.BodyParts.add(new BodyPart(BodyPartType.UpperArm_L, gameCharacter));
		this.BodyParts.add(new BodyPart(BodyPartType.UpperArm_R, gameCharacter));
		this.BodyParts.add(new BodyPart(BodyPartType.Torso_Upper, gameCharacter));
		this.BodyParts.add(new BodyPart(BodyPartType.Torso_Lower, gameCharacter));
		this.BodyParts.add(new BodyPart(BodyPartType.Head, gameCharacter));
		this.BodyParts.add(new BodyPart(BodyPartType.Neck, gameCharacter));
		this.BodyParts.add(new BodyPart(BodyPartType.Groin, gameCharacter));
		this.BodyParts.add(new BodyPart(BodyPartType.UpperLeg_L, gameCharacter));
		this.BodyParts.add(new BodyPart(BodyPartType.UpperLeg_R, gameCharacter));
		this.BodyParts.add(new BodyPart(BodyPartType.LowerLeg_L, gameCharacter));
		this.BodyParts.add(new BodyPart(BodyPartType.LowerLeg_R, gameCharacter));
		this.BodyParts.add(new BodyPart(BodyPartType.Foot_L, gameCharacter));
		this.BodyParts.add(new BodyPart(BodyPartType.Foot_R, gameCharacter));
		this.RestoreToFullHealth();
		this.ParentChar = gameCharacter;
	}

	public BodyPart getBodyPart(BodyPartType bodyPartType) {
		return (BodyPart)this.BodyParts.get(BodyPartType.ToIndex(bodyPartType));
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		for (int int2 = 0; int2 < this.getBodyParts().size(); ++int2) {
			BodyPart bodyPart = (BodyPart)this.getBodyParts().get(int2);
			bodyPart.SetBitten(byteBuffer.get() == 1);
			bodyPart.setScratched(byteBuffer.get() == 1);
			bodyPart.setBandaged(byteBuffer.get() == 1, 0.0F);
			bodyPart.setBleeding(byteBuffer.get() == 1);
			bodyPart.setDeepWounded(byteBuffer.get() == 1);
			bodyPart.SetFakeInfected(byteBuffer.get() == 1);
			bodyPart.SetInfected(byteBuffer.get() == 1);
			bodyPart.SetHealth(byteBuffer.getFloat());
			if (int1 >= 37 && int1 <= 43) {
				byteBuffer.getInt();
			}

			if (int1 >= 44) {
				if (bodyPart.bandaged()) {
					bodyPart.setBandageLife(byteBuffer.getFloat());
				}

				bodyPart.setInfectedWound(byteBuffer.get() == 1);
				if (bodyPart.isInfectedWound()) {
					bodyPart.setWoundInfectionLevel(byteBuffer.getFloat());
				}

				bodyPart.setBiteTime(byteBuffer.getFloat());
				bodyPart.setScratchTime(byteBuffer.getFloat());
				bodyPart.setBleedingTime(byteBuffer.getFloat());
				bodyPart.setAlcoholLevel(byteBuffer.getFloat());
				bodyPart.setAdditionalPain(byteBuffer.getFloat());
				bodyPart.setDeepWoundTime(byteBuffer.getFloat());
				bodyPart.setHaveGlass(byteBuffer.get() == 1);
				bodyPart.setGetBandageXp(byteBuffer.get() == 1);
				if (int1 >= 48) {
					bodyPart.setStitched(byteBuffer.get() == 1);
					bodyPart.setStitchTime(byteBuffer.getFloat());
				}

				bodyPart.setGetStitchXp(byteBuffer.get() == 1);
				bodyPart.setGetSplintXp(byteBuffer.get() == 1);
				bodyPart.setFractureTime(byteBuffer.getFloat());
				bodyPart.setSplint(byteBuffer.get() == 1, 0.0F);
				if (bodyPart.isSplint()) {
					bodyPart.setSplintFactor(byteBuffer.getFloat());
				}

				bodyPart.setHaveBullet(byteBuffer.get() == 1, 0);
				bodyPart.setBurnTime(byteBuffer.getFloat());
				bodyPart.setNeedBurnWash(byteBuffer.get() == 1);
				bodyPart.setLastTimeBurnWash(byteBuffer.getFloat());
				if (int1 >= 50) {
					bodyPart.setSplintItem(GameWindow.ReadString(byteBuffer));
				}

				if (int1 >= 53) {
					bodyPart.setBandageType(GameWindow.ReadString(byteBuffer));
				}
			}
		}

		this.setInfectionLevel(byteBuffer.getFloat());
		this.setFakeInfectionLevel(byteBuffer.getFloat());
		this.setWetness(byteBuffer.getFloat());
		this.setCatchACold(byteBuffer.getFloat());
		this.setHasACold(byteBuffer.get() == 1);
		this.setColdStrength(byteBuffer.getFloat());
		this.setUnhappynessLevel(byteBuffer.getFloat());
		this.setBoredomLevel(byteBuffer.getFloat());
		if (int1 >= 30) {
			this.setFoodSicknessLevel(byteBuffer.getFloat());
			this.PoisonLevel = byteBuffer.getFloat();
		}

		if (int1 >= 43) {
			float float1 = byteBuffer.getFloat();
			if (int1 < 135) {
				float1 = 37.0F;
			}

			this.setTemperature(float1);
		}

		if (int1 >= 44) {
			this.setReduceFakeInfection(byteBuffer.get() == 1);
		}

		if (int1 >= 56) {
			this.setHealthFromFoodTimer(byteBuffer.getFloat());
		}

		if (int1 >= 74) {
			this.painReduction = byteBuffer.getFloat();
			this.coldReduction = byteBuffer.getFloat();
		}

		if (int1 >= 137) {
			this.InfectionTime = byteBuffer.getFloat();
			this.InfectionMortalityDuration = byteBuffer.getFloat();
		}

		if (int1 >= 139) {
			this.ColdDamageStage = byteBuffer.getFloat();
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		for (int int1 = 0; int1 < this.getBodyParts().size(); ++int1) {
			BodyPart bodyPart = (BodyPart)this.getBodyParts().get(int1);
			byteBuffer.put((byte)(bodyPart.bitten() ? 1 : 0));
			byteBuffer.put((byte)(bodyPart.scratched() ? 1 : 0));
			byteBuffer.put((byte)(bodyPart.bandaged() ? 1 : 0));
			byteBuffer.put((byte)(bodyPart.bleeding() ? 1 : 0));
			byteBuffer.put((byte)(bodyPart.deepWounded() ? 1 : 0));
			byteBuffer.put((byte)(bodyPart.IsFakeInfected() ? 1 : 0));
			byteBuffer.put((byte)(bodyPart.IsInfected() ? 1 : 0));
			byteBuffer.putFloat(bodyPart.getHealth());
			if (bodyPart.bandaged()) {
				byteBuffer.putFloat(bodyPart.getBandageLife());
			}

			byteBuffer.put((byte)(bodyPart.isInfectedWound() ? 1 : 0));
			if (bodyPart.isInfectedWound()) {
				byteBuffer.putFloat(bodyPart.getWoundInfectionLevel());
			}

			byteBuffer.putFloat(bodyPart.getBiteTime());
			byteBuffer.putFloat(bodyPart.getScratchTime());
			byteBuffer.putFloat(bodyPart.getBleedingTime());
			byteBuffer.putFloat(bodyPart.getAlcoholLevel());
			byteBuffer.putFloat(bodyPart.getAdditionalPain());
			byteBuffer.putFloat(bodyPart.getDeepWoundTime());
			byteBuffer.put((byte)(bodyPart.haveGlass() ? 1 : 0));
			byteBuffer.put((byte)(bodyPart.isGetBandageXp() ? 1 : 0));
			byteBuffer.put((byte)(bodyPart.stitched() ? 1 : 0));
			byteBuffer.putFloat(bodyPart.getStitchTime());
			byteBuffer.put((byte)(bodyPart.isGetStitchXp() ? 1 : 0));
			byteBuffer.put((byte)(bodyPart.isGetSplintXp() ? 1 : 0));
			byteBuffer.putFloat(bodyPart.getFractureTime());
			byteBuffer.put((byte)(bodyPart.isSplint() ? 1 : 0));
			if (bodyPart.isSplint()) {
				byteBuffer.putFloat(bodyPart.getSplintFactor());
			}

			byteBuffer.put((byte)(bodyPart.haveBullet() ? 1 : 0));
			byteBuffer.putFloat(bodyPart.getBurnTime());
			byteBuffer.put((byte)(bodyPart.isNeedBurnWash() ? 1 : 0));
			byteBuffer.putFloat(bodyPart.getLastTimeBurnWash());
			GameWindow.WriteString(byteBuffer, bodyPart.getSplintItem());
			GameWindow.WriteString(byteBuffer, bodyPart.getBandageType());
		}

		byteBuffer.putFloat(this.InfectionLevel);
		byteBuffer.putFloat(this.getFakeInfectionLevel());
		byteBuffer.putFloat(this.getWetness());
		byteBuffer.putFloat(this.getCatchACold());
		byteBuffer.put((byte)(this.isHasACold() ? 1 : 0));
		byteBuffer.putFloat(this.getColdStrength());
		byteBuffer.putFloat(this.getUnhappynessLevel());
		byteBuffer.putFloat(this.getBoredomLevel());
		byteBuffer.putFloat(this.getFoodSicknessLevel());
		byteBuffer.putFloat(this.PoisonLevel);
		byteBuffer.putFloat(this.Temperature);
		byteBuffer.put((byte)(this.isReduceFakeInfection() ? 1 : 0));
		byteBuffer.putFloat(this.HealthFromFoodTimer);
		byteBuffer.putFloat(this.painReduction);
		byteBuffer.putFloat(this.coldReduction);
		byteBuffer.putFloat(this.InfectionTime);
		byteBuffer.putFloat(this.InfectionMortalityDuration);
		byteBuffer.putFloat(this.ColdDamageStage);
	}

	public boolean IsFakeInfected() {
		return this.isIsFakeInfected();
	}

	public void OnFire(boolean boolean1) {
		this.setIsOnFire(boolean1);
	}

	public boolean IsOnFire() {
		return this.isIsOnFire();
	}

	public boolean WasBurntToDeath() {
		return this.isBurntToDeath();
	}

	public void IncreasePanic(int int1) {
		if (this.getParentChar().getVehicle() != null) {
			int1 /= 2;
		}

		float float1 = 1.0F;
		if (this.getParentChar().getBetaEffect() > 0.0F) {
			float1 -= this.getParentChar().getBetaDelta();
			if (float1 > 1.0F) {
				float1 = 1.0F;
			}

			if (float1 < 0.0F) {
				float1 = 0.0F;
			}
		}

		if (this.getParentChar().HasTrait("Cowardly")) {
			float1 *= 2.0F;
		}

		if (this.getParentChar().HasTrait("Brave")) {
			float1 *= 0.3F;
		}

		if (this.getParentChar().HasTrait("Desensitized")) {
			float1 = 0.0F;
		}

		Stats stats = this.ParentChar.getStats();
		stats.Panic += this.getPanicIncreaseValue() * (float)int1 * float1;
		if (this.getParentChar().getStats().Panic > 100.0F) {
			this.ParentChar.getStats().Panic = 100.0F;
		}
	}

	public void ReducePanic() {
		if (!(this.ParentChar.getStats().Panic <= 0.0F)) {
			float float1 = this.getPanicReductionValue() * (GameTime.getInstance().getMultiplier() / 1.6F);
			int int1 = (int)Math.floor(new Double((double)GameTime.instance.getNightsSurvived()) / 30.0);
			if (int1 > 5) {
				int1 = 5;
			}

			float1 += this.getPanicReductionValue() * (float)int1;
			if (this.ParentChar.isAsleep()) {
				float1 *= 2.0F;
			}

			Stats stats = this.ParentChar.getStats();
			stats.Panic -= float1;
			if (this.getParentChar().getStats().Panic < 0.0F) {
				this.ParentChar.getStats().Panic = 0.0F;
			}
		}
	}

	public void UpdatePanicState() {
		int int1 = this.getParentChar().getStats().NumVisibleZombies;
		if (int1 > this.getOldNumZombiesVisible()) {
			this.IncreasePanic(int1 - this.getOldNumZombiesVisible());
		} else {
			this.ReducePanic();
		}

		this.setOldNumZombiesVisible(int1);
	}

	@Deprecated
	public void JustDrankBooze() {
		this.JustDrankBooze((Food)null, 1.0F);
	}

	public void JustDrankBooze(Food food, float float1) {
		float float2 = 1.0F;
		if (this.getParentChar().HasTrait("HeavyDrinker")) {
			float2 = 0.3F;
		}

		if (this.getParentChar().HasTrait("LightDrinker")) {
			float2 = 4.0F;
		}

		if (food.getBaseHunger() != 0.0F) {
			float1 = food.getHungChange() * float1 / food.getBaseHunger() * 2.0F;
		}

		float2 *= float1;
		Stats stats = this.ParentChar.getStats();
		stats.Drunkenness += this.getDrunkIncreaseValue() * float2;
		if (this.getParentChar().getStats().Drunkenness > 100.0F) {
			this.ParentChar.getStats().Drunkenness = 100.0F;
		}

		this.getParentChar().SleepingTablet(0.02F * float1);
		this.getParentChar().BetaAntiDepress(0.4F * float1);
		this.getParentChar().BetaBlockers(0.2F * float1);
		this.getParentChar().PainMeds(0.2F * float1);
	}

	public void JustTookPill(InventoryItem inventoryItem) {
		if ("PillsBeta".equals(inventoryItem.getType())) {
			if (this.getParentChar() != null && this.getParentChar().getStats().Drunkenness > 10.0F) {
				this.getParentChar().BetaBlockers(0.15F);
			} else {
				this.getParentChar().BetaBlockers(0.3F);
			}

			inventoryItem.Use();
		} else if ("PillsAntiDep".equals(inventoryItem.getType())) {
			if (this.getParentChar() != null && this.getParentChar().getStats().Drunkenness > 10.0F) {
				this.getParentChar().BetaAntiDepress(0.15F);
			} else {
				this.getParentChar().BetaAntiDepress(0.3F);
			}

			inventoryItem.Use();
		} else if ("PillsSleepingTablets".equals(inventoryItem.getType())) {
			inventoryItem.Use();
			this.getParentChar().SleepingTablet(0.1F);
			if (this.getParentChar() instanceof IsoPlayer) {
				((IsoPlayer)this.getParentChar()).setSleepingPillsTaken(((IsoPlayer)this.getParentChar()).getSleepingPillsTaken() + 1);
			}
		} else if ("Pills".equals(inventoryItem.getType())) {
			inventoryItem.Use();
			if (this.getParentChar() != null && this.getParentChar().getStats().Drunkenness > 10.0F) {
				this.getParentChar().PainMeds(0.15F);
			} else {
				this.getParentChar().PainMeds(0.45F);
			}
		} else if ("PillsVitamins".equals(inventoryItem.getType())) {
			inventoryItem.Use();
			Stats stats;
			if (this.getParentChar() != null && this.getParentChar().getStats().Drunkenness > 10.0F) {
				stats = this.getParentChar().getStats();
				stats.fatigue += inventoryItem.getFatigueChange() / 2.0F;
			} else {
				stats = this.getParentChar().getStats();
				stats.fatigue += inventoryItem.getFatigueChange();
			}
		}
	}

	public void JustAteFood(Food food, float float1) {
		Stats stats;
		float float2;
		if (food.getPoisonPower() > 0) {
			float2 = (float)food.getPoisonPower() * float1;
			if (this.getParentChar().HasTrait("IronGut")) {
				float2 /= 2.0F;
			}

			if (this.getParentChar().HasTrait("WeakStomach")) {
				float2 *= 2.0F;
			}

			this.PoisonLevel += float2;
			stats = this.ParentChar.getStats();
			stats.Pain += (float)food.getPoisonPower() * float1 / 6.0F;
		}

		if (food.isTaintedWater()) {
			this.PoisonLevel += 20.0F * float1;
			stats = this.ParentChar.getStats();
			stats.Pain += 10.0F * float1 / 6.0F;
		}

		if (food.getReduceInfectionPower() > 0.0F) {
			this.getParentChar().setReduceInfectionPower(food.getReduceInfectionPower());
		}

		this.setBoredomLevel(this.getBoredomLevel() + food.getBoredomChange() * float1);
		if (this.getBoredomLevel() < 0.0F) {
			this.setBoredomLevel(0.0F);
		}

		this.setUnhappynessLevel(this.getUnhappynessLevel() + food.getUnhappyChange() * float1);
		if (this.getUnhappynessLevel() < 0.0F) {
			this.setUnhappynessLevel(0.0F);
		}

		if (food.isAlcoholic()) {
			this.JustDrankBooze(food, float1);
		}

		if (this.getParentChar().getStats().hunger <= 0.0F) {
			float2 = Math.abs(food.getHungerChange()) * float1;
			this.setHealthFromFoodTimer((float)((int)(this.getHealthFromFoodTimer() + float2 * this.getHealthFromFoodTimeByHunger())));
			if (food.isCooked()) {
				this.setHealthFromFoodTimer((float)((int)(this.getHealthFromFoodTimer() + float2 * this.getHealthFromFoodTimeByHunger())));
			}

			if (this.getHealthFromFoodTimer() > 11000.0F) {
				this.setHealthFromFoodTimer(11000.0F);
			}
		}

		if (!"Tutorial".equals(Core.getInstance().getGameMode())) {
			if (!food.isCooked() && food.isbDangerousUncooked()) {
				this.setHealthFromFoodTimer(0.0F);
				int int1 = 75;
				if (this.getParentChar().HasTrait("IronGut")) {
					int1 /= 2;
				}

				if (this.getParentChar().HasTrait("WeakStomach")) {
					int1 *= 2;
				}

				if (Rand.Next(100) < int1 && !this.isInfected()) {
					this.PoisonLevel += 15.0F * float1;
				}
			}

			if (food.getAge() >= (float)food.getOffAgeMax()) {
				float2 = food.getAge() - (float)food.getOffAgeMax();
				if (float2 == 0.0F) {
					float2 = 1.0F;
				}

				if (float2 > 5.0F) {
					float2 = 5.0F;
				}

				int int2;
				if (food.getOffAgeMax() > food.getOffAge()) {
					int2 = (int)(float2 / (float)(food.getOffAgeMax() - food.getOffAge()) * 100.0F);
				} else {
					int2 = 100;
				}

				if (this.getParentChar().HasTrait("IronGut")) {
					int2 /= 2;
				}

				if (this.getParentChar().HasTrait("WeakStomach")) {
					int2 *= 2;
				}

				if (Rand.Next(100) < int2 && !this.isInfected()) {
					this.PoisonLevel += 5.0F * Math.abs(food.getHungChange() * 10.0F) * float1;
				}
			}
		}
	}

	public void JustAteFood(Food food) {
		this.JustAteFood(food, 100.0F);
	}

	private float getHealthFromFoodTimeByHunger() {
		return 13000.0F;
	}

	public void JustReadSomething(Literature literature) {
		this.setBoredomLevel(this.getBoredomLevel() + literature.getBoredomChange());
		if (this.getBoredomLevel() < 0.0F) {
			this.setBoredomLevel(0.0F);
		}

		this.setUnhappynessLevel(this.getUnhappynessLevel() + literature.getUnhappyChange());
		if (this.getUnhappynessLevel() < 0.0F) {
			this.setUnhappynessLevel(0.0F);
		}
	}

	public void JustTookPainMeds() {
		Stats stats = this.ParentChar.getStats();
		stats.Pain -= this.getPainReductionFromMeds();
		if (this.getParentChar().getStats().Pain < 0.0F) {
			this.ParentChar.getStats().Pain = 0.0F;
		}
	}

	public void UpdateWetness() {
		IsoGridSquare square = this.getParentChar().getCurrentSquare();
		BaseVehicle baseVehicle = this.getParentChar().getVehicle();
		IsoGameCharacter gameCharacter = this.getParentChar();
		boolean boolean1 = square != null ? !square.isInARoom() : true;
		if (baseVehicle != null && baseVehicle.hasRoof(baseVehicle.getSeat(this.getParentChar()))) {
			boolean1 = false;
		}

		float float1 = 0.0F;
		float float2;
		if (baseVehicle != null && ClimateManager.getInstance().isRaining()) {
			VehiclePart vehiclePart = baseVehicle.getPartById("Windshield");
			if (vehiclePart != null) {
				VehicleWindow vehicleWindow = vehiclePart.getWindow();
				if (vehicleWindow != null && vehicleWindow.isDestroyed()) {
					float2 = ClimateManager.getInstance().getRainIntensity();
					float2 *= float2;
					float2 *= baseVehicle.getCurrentSpeedKmHour() / 50.0F;
					if (float2 < 0.1F) {
						float2 = 0.0F;
					}

					if (float2 > 1.0F) {
						float2 = 1.0F;
					}

					this.setWetness(this.getWetness() + (float)ZomboidGlobals.WetnessIncrease * float2 * GameTime.instance.getMultiplier());
					if (this.getWetness() > 100.0F) {
						this.setWetness(100.0F);
					}

					float1 = float2 * 3.0F;
				}
			}
		}

		if (boolean1 && gameCharacter.isAsleep() && gameCharacter.getBed() != null && "Tent".equals(gameCharacter.getBed().getName())) {
			boolean1 = false;
		}

		float float3;
		if (boolean1 && ClimateManager.getInstance().isRaining()) {
			float3 = ClimateManager.getInstance().getRainIntensity();
			if ((double)float3 < 0.1) {
				float3 = 0.0F;
			}

			if (this.getParentChar().hasEquipped("Umbrella")) {
				float3 *= 0.25F;
				if (this.getWetness() < 50.0F) {
					this.setWetness(this.getWetness() + (float)ZomboidGlobals.WetnessIncrease * float3 * GameTime.instance.getMultiplier());
				}
			} else {
				this.setWetness(this.getWetness() + (float)ZomboidGlobals.WetnessIncrease * float3 * GameTime.instance.getMultiplier());
			}

			if (this.getWetness() > 100.0F) {
				this.setWetness(100.0F);
			}
		} else if ((!boolean1 || !ClimateManager.getInstance().isRaining()) && this.getWetness() > 0.0F) {
			float3 = ClimateManager.getInstance().getAirTemperatureForCharacter(this.getParentChar());
			float float4 = 0.1F;
			if (float3 > 5.0F) {
				float4 += (float3 - 5.0F) / 10.0F;
			}

			float4 -= float1;
			if (float4 < 0.0F) {
				float4 = 0.0F;
			}

			float2 = (float)ZomboidGlobals.WetnessDecrease * GameTime.instance.getMultiplier();
			this.setWetness(Math.max(this.getWetness() - float2 * float4, 0.0F));
		}

		if (!this.ParentChar.HasTrait("Outdoorsman") && !this.isHasACold() && (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Wet) > 1 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hypothermia) > 2)) {
			float3 = 1.0F;
			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Wet) == 2) {
				float3 = 1.0F;
			}

			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Wet) == 3) {
				float3 = 1.5F;
			}

			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Wet) == 4 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hypothermia) > 4) {
				float3 = 2.0F;
			}

			if (this.getParentChar().HasTrait("ProneToIllness")) {
				float3 *= 1.7F;
			}

			if (this.getParentChar().HasTrait("Resilient")) {
				float3 *= 0.45F;
			}

			float3 *= 0.75F;
			this.setCatchACold(this.getCatchACold() + (float)ZomboidGlobals.CatchAColdIncreaseRate * float3 * GameTime.instance.getMultiplier());
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

			boolean boolean1 = false;
			if (this.getParentChar().getPrimaryHandItem() != null && this.getParentChar().getPrimaryHandItem().getType().equals("Tissue")) {
				if (((Drainable)this.getParentChar().getPrimaryHandItem()).getUsedDelta() > 0.0F) {
					((Drainable)this.getParentChar().getPrimaryHandItem()).setUsedDelta(((Drainable)this.getParentChar().getPrimaryHandItem()).getUsedDelta() - 0.1F);
					if (((Drainable)this.getParentChar().getPrimaryHandItem()).getUsedDelta() <= 0.0F) {
						this.getParentChar().getPrimaryHandItem().Use();
					}

					boolean1 = true;
				}
			} else if (this.getParentChar().getSecondaryHandItem() != null && this.getParentChar().getSecondaryHandItem().getType().equals("Tissue") && ((Drainable)this.getParentChar().getSecondaryHandItem()).getUsedDelta() > 0.0F) {
				((Drainable)this.getParentChar().getSecondaryHandItem()).setUsedDelta(((Drainable)this.getParentChar().getSecondaryHandItem()).getUsedDelta() - 0.1F);
				if (((Drainable)this.getParentChar().getSecondaryHandItem()).getUsedDelta() <= 0.0F) {
					this.getParentChar().getSecondaryHandItem().Use();
				}

				boolean1 = true;
			}

			if (boolean1) {
				this.setSneezeCoughActive(this.getSneezeCoughActive() + 2);
			} else {
				byte byte1 = 20;
				byte byte2 = 20;
				if (this.getSneezeCoughActive() == 1) {
					byte1 = 20;
					byte2 = 25;
				}

				if (this.getSneezeCoughActive() == 2) {
					byte1 = 35;
					byte2 = 40;
				}

				WorldSoundManager.instance.addSound(this.getParentChar(), (int)this.getParentChar().getX(), (int)this.getParentChar().getY(), (int)this.getParentChar().getZ(), byte1, byte2, true);
			}
		}
	}

	public int IsSneezingCoughing() {
		return this.getSneezeCoughActive();
	}

	public void UpdateCold() {
		if (this.isHasACold()) {
			boolean boolean1 = true;
			IsoGridSquare square = this.getParentChar().getCurrentSquare();
			if (square == null || !square.isInARoom() || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Wet) > 0 || this.getParentChar().getStats().fatigue > 0.5F || this.getParentChar().getStats().hunger > 0.25F || this.getParentChar().getStats().thirst > 0.25F) {
				boolean1 = false;
			}

			if (this.getColdReduction() > 0.0F) {
				boolean1 = true;
				this.setColdReduction(this.getColdReduction() - 0.005F * GameTime.instance.getMultiplier());
				if (this.getColdReduction() < 0.0F) {
					this.setColdReduction(0.0F);
				}
			}

			float float1;
			if (boolean1) {
				float1 = 1.0F;
				if (this.getParentChar().HasTrait("ProneToIllness")) {
					float1 = 0.5F;
				}

				if (this.getParentChar().HasTrait("Resilient")) {
					float1 = 1.5F;
				}

				this.setColdStrength(this.getColdStrength() - this.getColdProgressionRate() * float1 * GameTime.instance.getMultiplier());
				if (this.getColdReduction() > 0.0F) {
					this.setColdStrength(this.getColdStrength() - this.getColdProgressionRate() * float1 * GameTime.instance.getMultiplier());
				}

				if (this.getColdStrength() < 0.0F) {
					this.setColdStrength(0.0F);
					this.setHasACold(false);
					this.setCatchACold(0.0F);
				}
			} else {
				float1 = 1.0F;
				if (this.getParentChar().HasTrait("ProneToIllness")) {
					float1 = 1.2F;
				}

				if (this.getParentChar().HasTrait("Resilient")) {
					float1 = 0.8F;
				}

				this.setColdStrength(this.getColdStrength() + this.getColdProgressionRate() * float1 * GameTime.instance.getMultiplier());
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

	public void AddDamage(BodyPartType bodyPartType, float float1) {
		((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).AddDamage(float1);
	}

	public void AddGeneralHealth(float float1) {
		int int1 = 0;
		for (int int2 = 0; int2 < BodyPartType.ToIndex(BodyPartType.MAX); ++int2) {
			if (((BodyPart)this.getBodyParts().get(int2)).getHealth() < 100.0F) {
				++int1;
			}
		}

		if (int1 > 0) {
			float float2 = float1 / (float)int1;
			for (int int3 = 0; int3 < BodyPartType.ToIndex(BodyPartType.MAX); ++int3) {
				if (((BodyPart)this.getBodyParts().get(int3)).getHealth() < 100.0F) {
					((BodyPart)this.getBodyParts().get(int3)).AddHealth(float2);
				}
			}
		}
	}

	public void ReduceGeneralHealth(float float1) {
		float float2 = float1 / (float)BodyPartType.ToIndex(BodyPartType.MAX);
		for (int int1 = 0; int1 < BodyPartType.ToIndex(BodyPartType.MAX); ++int1) {
			((BodyPart)this.getBodyParts().get(int1)).ReduceHealth(float2);
		}
	}

	public void AddDamage(int int1, float float1) {
		((BodyPart)this.getBodyParts().get(int1)).AddDamage(float1);
	}

	public void DamageFromWeapon(HandWeapon handWeapon) {
		if (GameServer.bServer) {
			if (handWeapon != null) {
				this.getParentChar().sendObjectChange("DamageFromWeapon", new Object[]{"weapon", handWeapon.getFullType()});
			}
		} else {
			boolean boolean1 = false;
			byte byte1 = 1;
			int int1 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.Groin) + 1);
			this.getParentChar().splatBloodFloorBig(0.4F);
			this.getParentChar().splatBloodFloorBig(0.4F);
			this.getParentChar().splatBloodFloorBig(0.4F);
			boolean boolean2 = true;
			if (handWeapon.getCategories().contains("Blunt")) {
				boolean2 = false;
				byte1 = 0;
			}

			if (boolean2 && !handWeapon.isAimedFirearm()) {
				int1 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.MAX));
				this.SetScratchedFromWeapon(int1, true);
			}

			float float1 = Rand.Next(handWeapon.getMinDamage(), handWeapon.getMaxDamage()) * 15.0F;
			if (handWeapon.isAimedFirearm()) {
				((BodyPart)this.getBodyParts().get(int1)).damageFromFirearm(float1 * 2.0F);
			}

			if (int1 == BodyPartType.ToIndex(BodyPartType.Head)) {
				float1 *= 4.0F;
			}

			if (int1 == BodyPartType.ToIndex(BodyPartType.Neck)) {
				float1 *= 4.0F;
			}

			if (int1 == BodyPartType.ToIndex(BodyPartType.Torso_Upper)) {
				float1 *= 4.0F;
			}

			this.AddDamage(int1, float1);
			Stats stats;
			switch (byte1) {
			case 0: 
				stats = this.ParentChar.getStats();
				stats.Pain += this.getInitialThumpPain() * BodyPartType.getPainModifyer(int1);
				break;
			
			case 1: 
				stats = this.ParentChar.getStats();
				stats.Pain += this.getInitialScratchPain() * BodyPartType.getPainModifyer(int1);
				break;
			
			case 2: 
				stats = this.ParentChar.getStats();
				stats.Pain += this.getInitialBitePain() * BodyPartType.getPainModifyer(int1);
			
			}

			if (this.getParentChar().getStats().Pain > 100.0F) {
				this.ParentChar.getStats().Pain = 100.0F;
			}
		}
	}

	public void AddRandomDamageFromZombie(IsoZombie zombie) {
		this.getParentChar().setHitBy((IsoGameCharacter)null);
		if (GameServer.bServer) {
			this.getParentChar().sendObjectChange("AddRandomDamageFromZombie", new Object[]{"zombie", zombie.OnlineID});
		} else {
			int int1 = 450;
			byte byte1 = 0;
			if (this.getParentChar().getSprite() != null && this.getParentChar().getSprite().CurrentAnim != null && "Idle".equals(this.getParentChar().getSprite().CurrentAnim.name)) {
				int1 *= 3;
			}

			int1 *= this.CountSurroundingZombies(this.getParentChar().getCell(), this.getParentChar().getCurrentSquare());
			if (this.getParentChar().getBodyDamage().getHealth() <= 0.0F) {
				int1 *= 10;
			}

			boolean boolean1 = false;
			int int2 = 75 + this.getParentChar().getMeleeCombatMod();
			byte byte2 = 75;
			if (this.ParentChar.HasTrait("ThickSkinned")) {
				int2 = 85 + this.getParentChar().getMeleeCombatMod();
			}

			if (this.ParentChar.HasTrait("ThinSkinned")) {
				int2 = 65 + this.getParentChar().getMeleeCombatMod();
			}

			int int3;
			if (!zombie.bCrawling) {
				int3 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.Groin) + 1);
				if ((int3 == BodyPartType.ToIndex(BodyPartType.Head) || int3 == BodyPartType.ToIndex(BodyPartType.Neck)) && Rand.Next(100) > 70) {
					boolean boolean2 = false;
					label139: while (true) {
						do {
							if (boolean2) {
								break label139;
							}

							boolean2 = true;
							int3 = Rand.Next(BodyPartType.ToIndex(BodyPartType.MAX));
						}				 while (int3 != BodyPartType.ToIndex(BodyPartType.Head) && int3 != BodyPartType.ToIndex(BodyPartType.Neck));

						boolean2 = false;
					}
				}
			} else {
				if (Rand.Next(2) != 0) {
					return;
				}

				int3 = Rand.Next(BodyPartType.ToIndex(BodyPartType.UpperLeg_L), BodyPartType.ToIndex(BodyPartType.MAX));
			}

			float float1 = (float)Rand.Next(1000) / 1000.0F;
			float1 *= (float)(Rand.Next(10) + 10);
			this.AddDamage(int3, float1);
			if (GameServer.bServer && this.ParentChar instanceof IsoPlayer) {
				DebugLog.log(DebugType.Combat, "zombie did " + float1 + " dmg to " + ((IsoPlayer)this.ParentChar).getDisplayName());
			}

			boolean boolean3 = false;
			if (Rand.Next(100) > int2) {
				boolean3 = true;
				if (SandboxOptions.instance.ClothingDegradation.getValue() > 1 && int3 < BodyPartType.ToIndex(BodyPartType.Head) && this.getParentChar().getClothingItem_Torso() != null && this.getParentChar().getClothingItem_Torso() instanceof Clothing && Rand.Next(((Clothing)this.getParentChar().getClothingItem_Torso()).getConditionLowerChance()) == 0) {
					this.getParentChar().getClothingItem_Torso().setCondition(this.getParentChar().getClothingItem_Torso().getCondition() - 1);
				}

				if (SandboxOptions.instance.ClothingDegradation.getValue() > 1 && int3 > BodyPartType.ToIndex(BodyPartType.Groin) && int3 < BodyPartType.ToIndex(BodyPartType.Foot_L) && this.getParentChar().getClothingItem_Legs() != null && this.getParentChar().getClothingItem_Legs() instanceof Clothing && Rand.Next(((Clothing)this.getParentChar().getClothingItem_Legs()).getConditionLowerChance()) == 0) {
					this.getParentChar().getClothingItem_Legs().setCondition(this.getParentChar().getClothingItem_Legs().getCondition() - 1);
				}

				if (Rand.Next(100) > byte2) {
					boolean3 = false;
				}

				if (boolean3) {
					this.SetScratched(int3, true);
					if (this.getHealth() > 0.0F) {
						this.getParentChar().getEmitter().playSound("ZombieScratch");
					}

					byte1 = 1;
					if (GameServer.bServer && this.ParentChar instanceof IsoPlayer) {
						DebugLog.log(DebugType.Combat, "zombie scratched " + ((IsoPlayer)this.ParentChar).username);
					}

					this.getParentChar().Scratched();
				} else {
					if (this.getHealth() > 0.0F) {
						this.getParentChar().getEmitter().playSound("ZombieBite");
					}

					this.SetBitten(int3, true);
					if (GameServer.bServer && this.ParentChar instanceof IsoPlayer) {
						DebugLog.log(DebugType.Combat, "zombie bite " + ((IsoPlayer)this.ParentChar).username);
					}

					byte1 = 2;
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

			Stats stats;
			switch (byte1) {
			case 0: 
				stats = this.ParentChar.getStats();
				stats.Pain += this.getInitialThumpPain() * BodyPartType.getPainModifyer(int3);
				break;
			
			case 1: 
				stats = this.ParentChar.getStats();
				stats.Pain += this.getInitialScratchPain() * BodyPartType.getPainModifyer(int3);
				break;
			
			case 2: 
				stats = this.ParentChar.getStats();
				stats.Pain += this.getInitialBitePain() * BodyPartType.getPainModifyer(int3);
			
			}

			if (this.getParentChar().getStats().Pain > 100.0F) {
				this.ParentChar.getStats().Pain = 100.0F;
			}

			if (byte1 > 0) {
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

	private void HurtBloodSplats(int int1) {
	}

	private int CountSurroundingZombies(IsoCell cell, IsoGridSquare square) {
		if (square == null) {
			return 0;
		} else {
			int int1 = 0;
			IsoGridSquare square2 = null;
			square2 = cell.getGridSquare(square.getX(), square.getY() - 1, square.getZ());
			if (square2 != null) {
				int1 += square2.getMovingObjects().size();
			}

			square2 = cell.getGridSquare(square.getX() + 1, square.getY() - 1, square.getZ());
			if (square2 != null) {
				int1 += square2.getMovingObjects().size();
			}

			square2 = cell.getGridSquare(square.getX() + 1, square.getY(), square.getZ());
			if (square2 != null) {
				int1 += square2.getMovingObjects().size();
			}

			square2 = cell.getGridSquare(square.getX() + 1, square.getY() + 1, square.getZ());
			if (square2 != null) {
				int1 += square2.getMovingObjects().size();
			}

			square2 = cell.getGridSquare(square.getX(), square.getY() + 1, square.getZ());
			if (square2 != null) {
				int1 += square2.getMovingObjects().size();
			}

			square2 = cell.getGridSquare(square.getX() - 1, square.getY() + 1, square.getZ());
			if (square2 != null) {
				int1 += square2.getMovingObjects().size();
			}

			square2 = cell.getGridSquare(square.getX() - 1, square.getY(), square.getZ());
			if (square2 != null) {
				int1 += square2.getMovingObjects().size();
			}

			square2 = cell.getGridSquare(square.getX() - 1, square.getY() - 1, square.getZ());
			if (square2 != null) {
				int1 += square2.getMovingObjects().size();
			}

			square2 = cell.getGridSquare(square.getX(), square.getY(), square.getZ());
			if (square2 != null) {
				int1 += square2.getMovingObjects().size();
			}

			if (int1 > 0) {
				--int1;
			}

			return int1;
		}
	}

	public boolean DoesBodyPartHaveInjury(BodyPartType bodyPartType) {
		return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).HasInjury();
	}

	public void DrawUntexturedQuad(int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4) {
		SpriteRenderer.instance.render((Texture)null, int1, int2, int3, int4, float1, float2, float3, float4);
	}

	public float getBodyPartHealth(BodyPartType bodyPartType) {
		return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).getHealth();
	}

	public float getBodyPartHealth(int int1) {
		return ((BodyPart)this.getBodyParts().get(int1)).getHealth();
	}

	public String getBodyPartName(BodyPartType bodyPartType) {
		return BodyPartType.ToString(bodyPartType);
	}

	public String getBodyPartName(int int1) {
		return BodyPartType.ToString(BodyPartType.FromIndex(int1));
	}

	public float getHealth() {
		return this.getOverallBodyHealth();
	}

	public float getInfectionLevel() {
		return this.InfectionLevel;
	}

	public float getApparentInfectionLevel() {
		float float1 = this.getFakeInfectionLevel() > this.InfectionLevel ? this.getFakeInfectionLevel() : this.InfectionLevel;
		return this.getFoodSicknessLevel() > float1 ? this.getFoodSicknessLevel() : float1;
	}

	public int getNumPartsBleeding() {
		int int1 = 0;
		for (int int2 = 0; int2 < BodyPartType.ToIndex(BodyPartType.MAX); ++int2) {
			if (((BodyPart)this.getBodyParts().get(int2)).bleeding()) {
				++int1;
			}
		}

		return int1;
	}

	public int getNumPartsScratched() {
		int int1 = 0;
		for (int int2 = 0; int2 < BodyPartType.ToIndex(BodyPartType.MAX); ++int2) {
			if (((BodyPart)this.getBodyParts().get(int2)).scratched()) {
				++int1;
			}
		}

		return int1;
	}

	public int getNumPartsBitten() {
		int int1 = 0;
		for (int int2 = 0; int2 < BodyPartType.ToIndex(BodyPartType.MAX); ++int2) {
			if (((BodyPart)this.getBodyParts().get(int2)).bitten()) {
				++int1;
			}
		}

		return int1;
	}

	public boolean HasInjury() {
		for (int int1 = 0; int1 < BodyPartType.ToIndex(BodyPartType.MAX); ++int1) {
			if (((BodyPart)this.getBodyParts().get(int1)).HasInjury()) {
				return true;
			}
		}

		return false;
	}

	public boolean IsBandaged(BodyPartType bodyPartType) {
		return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).bandaged();
	}

	public boolean IsDeepWounded(BodyPartType bodyPartType) {
		return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).deepWounded();
	}

	public boolean IsBandaged(int int1) {
		return ((BodyPart)this.getBodyParts().get(int1)).bandaged();
	}

	public boolean IsBitten(BodyPartType bodyPartType) {
		return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).bitten();
	}

	public boolean IsBitten(int int1) {
		return ((BodyPart)this.getBodyParts().get(int1)).bitten();
	}

	public boolean IsBleeding(BodyPartType bodyPartType) {
		return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).bleeding();
	}

	public boolean IsBleeding(int int1) {
		return ((BodyPart)this.getBodyParts().get(int1)).bleeding();
	}

	public boolean IsBleedingStemmed(BodyPartType bodyPartType) {
		return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).IsBleedingStemmed();
	}

	public boolean IsBleedingStemmed(int int1) {
		return ((BodyPart)this.getBodyParts().get(int1)).IsBleedingStemmed();
	}

	public boolean IsCortorised(BodyPartType bodyPartType) {
		return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).IsCortorised();
	}

	public boolean IsCortorised(int int1) {
		return ((BodyPart)this.getBodyParts().get(int1)).IsCortorised();
	}

	public boolean IsInfected() {
		return this.IsInfected;
	}

	public boolean IsInfected(BodyPartType bodyPartType) {
		return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).IsInfected();
	}

	public boolean IsInfected(int int1) {
		return ((BodyPart)this.getBodyParts().get(int1)).IsInfected();
	}

	public boolean IsFakeInfected(int int1) {
		return ((BodyPart)this.getBodyParts().get(int1)).IsFakeInfected();
	}

	public void DisableFakeInfection(int int1) {
		((BodyPart)this.getBodyParts().get(int1)).DisableFakeInfection();
	}

	public boolean IsScratched(BodyPartType bodyPartType) {
		return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).scratched();
	}

	public boolean IsScratched(int int1) {
		return ((BodyPart)this.getBodyParts().get(int1)).scratched();
	}

	public boolean IsStitched(BodyPartType bodyPartType) {
		return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).stitched();
	}

	public boolean IsStitched(int int1) {
		return ((BodyPart)this.getBodyParts().get(int1)).stitched();
	}

	public boolean IsWounded(BodyPartType bodyPartType) {
		return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).deepWounded();
	}

	public boolean IsWounded(int int1) {
		return ((BodyPart)this.getBodyParts().get(int1)).deepWounded();
	}

	public void RestoreToFullHealth() {
		for (int int1 = 0; int1 < BodyPartType.ToIndex(BodyPartType.MAX); ++int1) {
			((BodyPart)this.getBodyParts().get(int1)).RestoreToFullHealth();
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

	public void SetBandaged(int int1, boolean boolean1, float float1, boolean boolean2, String string) {
		((BodyPart)this.getBodyParts().get(int1)).setBandaged(boolean1, float1, boolean2, string);
	}

	public void SetBitten(BodyPartType bodyPartType, boolean boolean1) {
		((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).SetBitten(boolean1);
	}

	public void SetBitten(int int1, boolean boolean1) {
		((BodyPart)this.getBodyParts().get(int1)).SetBitten(boolean1);
	}

	public void SetBitten(int int1, boolean boolean1, boolean boolean2) {
		((BodyPart)this.getBodyParts().get(int1)).SetBitten(boolean1, boolean2);
	}

	public void SetBleeding(BodyPartType bodyPartType, boolean boolean1) {
		((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).setBleeding(boolean1);
	}

	public void SetBleeding(int int1, boolean boolean1) {
		((BodyPart)this.getBodyParts().get(int1)).setBleeding(boolean1);
	}

	public void SetBleedingStemmed(BodyPartType bodyPartType, boolean boolean1) {
		((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).SetBleedingStemmed(boolean1);
	}

	public void SetBleedingStemmed(int int1, boolean boolean1) {
		((BodyPart)this.getBodyParts().get(int1)).SetBleedingStemmed(boolean1);
	}

	public void SetCortorised(BodyPartType bodyPartType, boolean boolean1) {
		((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).SetCortorised(boolean1);
	}

	public void SetCortorised(int int1, boolean boolean1) {
		((BodyPart)this.getBodyParts().get(int1)).SetCortorised(boolean1);
	}

	public BodyPart setScratchedWindow() {
		int int1 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.ForeArm_R) + 1);
		this.getBodyPart(BodyPartType.FromIndex(int1)).AddDamage(10.0F);
		this.getBodyPart(BodyPartType.FromIndex(int1)).SetScratchedWindow(true);
		return this.getBodyPart(BodyPartType.FromIndex(int1));
	}

	public void SetScratched(BodyPartType bodyPartType, boolean boolean1) {
		((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).setScratched(boolean1);
	}

	public void SetScratched(int int1, boolean boolean1) {
		((BodyPart)this.getBodyParts().get(int1)).setScratched(boolean1);
	}

	public void SetScratchedFromWeapon(int int1, boolean boolean1) {
		((BodyPart)this.getBodyParts().get(int1)).SetScratchedWeapon(boolean1);
	}

	public void SetWounded(BodyPartType bodyPartType, boolean boolean1) {
		((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).setDeepWounded(boolean1);
	}

	public void SetWounded(int int1, boolean boolean1) {
		((BodyPart)this.getBodyParts().get(int1)).setDeepWounded(boolean1);
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
						this.setBoredomLevel((float)((double)this.getBoredomLevel() + ZomboidGlobals.BoredomIncreaseRate / 5.0 * (double)GameTime.instance.getMultiplier()));
					}

					if (this.getParentChar().IsSpeaking() && !this.getParentChar().callOut) {
						this.setBoredomLevel((float)((double)this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * (double)GameTime.instance.getMultiplier()));
					}

					if (this.getParentChar().getNumSurvivorsInVicinity() > 0) {
						this.setBoredomLevel((float)((double)this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * 0.10000000149011612 * (double)GameTime.instance.getMultiplier()));
					}
				} else if (this.getParentChar().getVehicle() != null) {
					float float1 = this.getParentChar().getVehicle().getCurrentSpeedKmHour();
					if (Math.abs(float1) <= 0.1F) {
						if (this.getParentChar().isReading()) {
							this.setBoredomLevel((float)((double)this.getBoredomLevel() + ZomboidGlobals.BoredomIncreaseRate / 5.0 * (double)GameTime.instance.getMultiplier()));
						} else {
							this.setBoredomLevel((float)((double)this.getBoredomLevel() + ZomboidGlobals.BoredomIncreaseRate * (double)GameTime.instance.getMultiplier()));
						}
					} else {
						this.setBoredomLevel((float)((double)this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * 0.5 * (double)GameTime.instance.getMultiplier()));
					}
				} else {
					this.setBoredomLevel((float)((double)this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * 0.10000000149011612 * (double)GameTime.instance.getMultiplier()));
				}

				if (this.getParentChar().getStats().Drunkenness > 20.0F) {
					this.setBoredomLevel((float)((double)this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * 2.0 * (double)GameTime.instance.getMultiplier()));
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
					this.setUnhappynessLevel((float)((double)this.getUnhappynessLevel() + ZomboidGlobals.UnhappinessIncrease / 2.0 * (double)((float)this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Stress)) * (double)GameTime.instance.getMultiplier()));
				}

				if (this.getParentChar().HasTrait("Smoker")) {
					this.getParentChar().setTimeSinceLastSmoke(this.getParentChar().getTimeSinceLastSmoke() + 1.0E-4F * GameTime.instance.getMultiplier());
					if (this.getParentChar().getTimeSinceLastSmoke() > 1.0F) {
						double double1 = Math.floor((double)(this.getParentChar().getTimeSinceLastSmoke() / 10.0F)) + 1.0;
						if (double1 > 10.0) {
							double1 = 10.0;
						}

						this.getParentChar().getStats().setStressFromCigarettes((float)((double)this.getParentChar().getStats().getStressFromCigarettes() + ZomboidGlobals.StressFromBiteOrScratch / 8.0 * double1 * (double)GameTime.instance.getMultiplier()));
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
			int int1 = 0;
			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 2) {
				++int1;
			}

			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 3) {
				int1 += 2;
			}

			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4) {
				int1 += 2;
			}

			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 2) {
				++int1;
			}

			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 3) {
				int1 += 2;
			}

			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4) {
				int1 += 2;
			}

			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 2) {
				++int1;
			}

			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 3) {
				int1 += 2;
			}

			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 4) {
				int1 += 3;
			}

			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bleeding) == 2) {
				++int1;
			}

			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bleeding) == 3) {
				++int1;
			}

			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bleeding) == 4) {
				++int1;
			}

			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Injured) == 2) {
				++int1;
			}

			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Injured) == 3) {
				int1 += 2;
			}

			if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Injured) == 4) {
				int1 += 3;
			}

			this.getParentChar().setMaxWeight(Integer.valueOf((int)((float)this.getParentChar().getMaxWeightBase() * this.getParentChar().getWeightMod())) - int1);
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
		float float1 = 1.0F;
		if (this.getParentChar().HasTrait("Resilient")) {
			float1 = 1.25F;
		}

		if (this.getParentChar().HasTrait("ProneToIllness")) {
			float1 = 0.75F;
		}

		switch (SandboxOptions.instance.Lore.Mortality.getValue()) {
		case 1: 
			return 0.0F;
		
		case 2: 
			return Rand.Next(0.0F, 30.0F) / 3600.0F * float1;
		
		case 3: 
			return Rand.Next(0.5F, 1.0F) / 60.0F * float1;
		
		case 4: 
			return Rand.Next(3.0F, 12.0F) * float1;
		
		case 5: 
			return Rand.Next(2.0F, 3.0F) * 24.0F * float1;
		
		case 6: 
			return Rand.Next(1.0F, 2.0F) * 7.0F * 24.0F * float1;
		
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
				float float1 = this.ParentChar.getStats().Pain;
				int int1 = this.getNumPartsBleeding() * 2;
				int1 += this.getNumPartsScratched();
				int1 += this.getNumPartsBitten() * 6;
				float float2;
				if (int1 > 0 && this.getHealth() < 60.0F || int1 > 3) {
					float2 = 1.0F / (float)int1 * 200.0F * GameTime.instance.getInvMultiplier();
					if ((float)Rand.Next((int)float2) < float2 * 0.3F) {
						this.getParentChar().splatBloodFloor(0.3F);
					}

					if (Rand.Next((int)float2) == 0) {
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

					int int2;
					if (!this.isInfected()) {
						for (int2 = 0; int2 < BodyPartType.ToIndex(BodyPartType.MAX); ++int2) {
							if (this.IsInfected(int2)) {
								this.setInfected(true);
								if (this.IsFakeInfected(int2)) {
									this.DisableFakeInfection(int2);
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
						for (int2 = 0; int2 < BodyPartType.ToIndex(BodyPartType.MAX); ++int2) {
							if (this.IsFakeInfected(int2)) {
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

					Stats stats = this.ParentChar.getStats();
					stats.Drunkenness -= this.getDrunkReductionValue() * GameTime.instance.getMultiplier();
					if (this.getParentChar().getStats().Drunkenness < 0.0F) {
						this.ParentChar.getStats().Drunkenness = 0.0F;
					}

					float2 = 0.0F;
					if (this.getHealthFromFoodTimer() > 0.0F) {
						float2 += this.getHealthFromFood() * GameTime.instance.getMultiplier();
						this.setHealthFromFoodTimer(this.getHealthFromFoodTimer() - 1.0F * GameTime.instance.getMultiplier());
					}

					byte byte1 = 0;
					if (this.getParentChar() == this.getParentChar() && (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 2 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 2 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 2)) {
						byte1 = 1;
					}

					if (this.getParentChar() == this.getParentChar() && (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 3 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 3 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 3)) {
						byte1 = 2;
					}

					if (this.getParentChar() == this.getParentChar() && (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4)) {
						byte1 = 3;
					}

					if (this.getParentChar().isAsleep()) {
						byte1 = -1;
					}

					switch (byte1) {
					case 0: 
						float2 += this.getStandardHealthAddition() * GameTime.instance.getMultiplier();
						break;
					
					case 1: 
						float2 += this.getReducedHealthAddition() * GameTime.instance.getMultiplier();
						break;
					
					case 2: 
						float2 += this.getSeverlyReducedHealthAddition() * GameTime.instance.getMultiplier();
						break;
					
					case 3: 
						float2 += 0.0F;
					
					}

					if (this.getParentChar().isAsleep()) {
						if (GameClient.bClient) {
							float2 += 15.0F * GameTime.instance.getGameWorldSecondsSinceLastUpdate() / 3600.0F;
						} else {
							float2 += this.getSleepingHealthAddition() * GameTime.instance.getMultiplier();
						}

						if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4) {
							float2 = 0.0F;
						}
					}

					this.AddGeneralHealth(float2);
					float2 = 0.0F;
					float float3;
					if (this.PoisonLevel > 0.0F) {
						if (this.PoisonLevel > 10.0F && this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) >= 1) {
							float2 += 0.0035F * Math.min(this.PoisonLevel / 10.0F, 3.0F) * GameTime.instance.getMultiplier();
						}

						float3 = 0.0F;
						if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.FoodEaten) > 0) {
							float3 = 1.5E-4F * (float)this.getParentChar().getMoodles().getMoodleLevel(MoodleType.FoodEaten);
						}

						this.PoisonLevel = (float)((double)this.PoisonLevel - ((double)float3 + ZomboidGlobals.PoisonLevelDecrease * (double)GameTime.instance.getMultiplier()));
						if (this.PoisonLevel < 0.0F) {
							this.PoisonLevel = 0.0F;
						}

						this.setFoodSicknessLevel(this.getFoodSicknessLevel() + this.getInfectionGrowthRate() * (float)(2 + Math.round(this.PoisonLevel / 10.0F)) * GameTime.instance.getMultiplier());
						if (this.getFoodSicknessLevel() > 100.0F) {
							this.setFoodSicknessLevel(100.0F);
						}
					}

					if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4) {
						float2 += this.getHealthReductionFromSevereBadMoodles() / 50.0F * GameTime.instance.getMultiplier();
					}

					if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 4 && (this.FakeInfectionLevel > this.InfectionLevel || this.FoodSicknessLevel > this.InfectionLevel)) {
						float2 += this.getHealthReductionFromSevereBadMoodles() * GameTime.instance.getMultiplier();
					}

					if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bleeding) == 4) {
						float2 += this.getHealthReductionFromSevereBadMoodles() * GameTime.instance.getMultiplier();
					}

					if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4) {
						float2 += this.getHealthReductionFromSevereBadMoodles() / 10.0F * GameTime.instance.getMultiplier();
					}

					if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HeavyLoad) > 2 && this.getHealth() > 75.0F && Rand.Next(Rand.AdjustForFramerate(10)) == 0) {
						float2 += this.getHealthReductionFromSevereBadMoodles() / ((float)(5 - this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HeavyLoad)) / 10.0F) * GameTime.instance.getMultiplier();
					}

					this.ReduceGeneralHealth(float2);
					int int3;
					if (this.ParentChar.getPainEffect() > 0.0F) {
						stats = this.ParentChar.getStats();
						stats.Pain -= 0.023333333F * (GameTime.getInstance().getMultiplier() / 1.6F);
						this.ParentChar.setPainEffect(this.ParentChar.getPainEffect() - GameTime.getInstance().getMultiplier() / 3.0F);
					} else {
						this.ParentChar.setPainDelta(0.0F);
						float2 = 0.0F;
						for (int3 = 0; int3 < BodyPartType.ToIndex(BodyPartType.MAX); ++int3) {
							float2 += ((BodyPart)this.getBodyParts().get(int3)).getPain() * BodyPartType.getPainModifyer(int3);
						}

						float2 -= this.getPainReduction();
						if (float2 > this.ParentChar.getStats().Pain) {
							stats = this.ParentChar.getStats();
							stats.Pain += (float2 - this.ParentChar.getStats().Pain) / 500.0F;
						} else {
							this.ParentChar.getStats().Pain = float2;
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
						int2 = SandboxOptions.instance.Lore.Mortality.getValue();
						if (int2 == 1) {
							this.ReduceGeneralHealth(100.0F);
							this.setInfectionLevel(100.0F);
						} else if (int2 != 7) {
							float3 = this.getCurrentTimeForInfection();
							if (this.InfectionMortalityDuration < 0.0F) {
								this.InfectionMortalityDuration = this.pickMortalityDuration();
							}

							if (this.InfectionTime < 0.0F) {
								this.InfectionTime = float3;
							}

							if (this.InfectionTime > float3) {
								this.InfectionTime = float3;
							}

							float float4 = (float3 - this.InfectionTime) / this.InfectionMortalityDuration;
							float4 = Math.min(float4, 1.0F);
							this.setInfectionLevel(float4 * 100.0F);
							if (float4 == 1.0F) {
								this.ReduceGeneralHealth(100.0F);
							} else {
								float4 *= float4;
								float4 *= float4;
								float float5 = (1.0F - float4) * 100.0F;
								float float6 = this.getOverallBodyHealth() - float5;
								if (float6 > 0.0F && float5 <= 99.0F) {
									this.ReduceGeneralHealth(float6);
								}
							}
						}
					}

					float2 = 0.0F;
					for (int3 = 0; int3 < BodyPartType.ToIndex(BodyPartType.MAX); ++int3) {
						((BodyPart)this.getBodyParts().get(int3)).DamageUpdate();
						float2 += (100.0F - ((BodyPart)this.getBodyParts().get(int3)).getHealth()) * BodyPartType.getDamageModifyer(int3);
					}

					if (float2 > 100.0F) {
						float2 = 100.0F;
					}

					float2 += this.getDamageFromPills();
					this.setOverallBodyHealth(100.0F - float2);
					if (this.getOverallBodyHealth() == 0.0F) {
						if (this.isIsOnFire()) {
							this.setBurntToDeath(true);
							for (int3 = 0; int3 < BodyPartType.ToIndex(BodyPartType.MAX); ++int3) {
								((BodyPart)this.getBodyParts().get(int3)).SetHealth((float)Rand.Next(90));
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
						for (int3 = 0; int3 < BodyPartType.ToIndex(BodyPartType.MAX); ++int3) {
							((BodyPart)this.getBodyParts().get(int3)).SetFakeInfected(false);
						}

						this.setIsFakeInfected(false);
						this.setFakeInfectionLevel(0.0F);
						this.setReduceFakeInfection(false);
					}

					if (float1 == this.ParentChar.getStats().Pain) {
						stats = this.ParentChar.getStats();
						stats.Pain = (float)((double)stats.Pain - 0.25 * (double)(GameTime.getInstance().getMultiplier() / 1.6F));
					}

					if (this.ParentChar.getStats().Pain < 0.0F) {
						this.ParentChar.getStats().Pain = 0.0F;
					}
				}
			}
		}
	}

	public static float getSicknessFromCorpsesRate(int int1) {
		if (SandboxOptions.instance.DecayingCorpseHealthImpact.getValue() == 1) {
			return 0.0F;
		} else if (int1 > 5) {
			float float1 = (float)ZomboidGlobals.FoodSicknessDecrease * 0.07F;
			switch (SandboxOptions.instance.DecayingCorpseHealthImpact.getValue()) {
			case 2: 
				float1 *= 0.01F;
				break;
			
			case 4: 
				float1 *= 0.11F;
			
			}

			int int2 = Math.min(int1 - 5, 20);
			return float1 * (float)int2;
		} else {
			return 0.0F;
		}
	}

	private void UpdateIllness() {
		if (SandboxOptions.instance.DecayingCorpseHealthImpact.getValue() != 1) {
			int int1 = FliesSound.instance.getCorpseCount(this.getParentChar());
			float float1 = getSicknessFromCorpsesRate(int1);
			if (float1 > 0.0F) {
				this.setFoodSicknessLevel(this.getFoodSicknessLevel() + float1 * GameTime.getInstance().getMultiplier());
			}
		}
	}

	private void UpdateTemperatureState() {
		float float1 = 0.06F;
		int int1 = this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hypothermia);
		if (int1 == 2) {
			float1 = 0.05F;
		} else if (int1 == 3) {
			float1 = 0.04F;
		} else if (int1 == 4) {
			float1 = 0.03F;
		}

		int1 = this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hyperthermia);
		if (int1 == 2) {
			float1 = 0.05F;
		} else if (int1 == 3) {
			float1 = 0.04F;
		} else if (int1 == 4) {
			float1 = 0.03F;
		}

		if (this.getParentChar() instanceof IsoPlayer) {
			if (this.ColdDamageStage > 0.0F) {
				float float2 = 100.0F - this.ColdDamageStage * 100.0F;
				if (this.OverallBodyHealth > float2) {
					this.ReduceGeneralHealth(this.OverallBodyHealth - float2);
				}

				float1 -= 0.02F * this.ColdDamageStage;
			}

			((IsoPlayer)this.getParentChar()).setMoveSpeed(float1);
		}
	}

	private float getDamageFromPills() {
		if (this.getParentChar() instanceof IsoPlayer) {
			IsoPlayer player = (IsoPlayer)this.getParentChar();
			if (player.getSleepingPillsTaken() == 10) {
				return 40.0F;
			}

			if (player.getSleepingPillsTaken() == 11) {
				return 80.0F;
			}

			if (player.getSleepingPillsTaken() >= 12) {
				return 100.0F;
			}
		}

		return 0.0F;
	}

	public boolean UseBandageOnMostNeededPart() {
		int int1 = 0;
		BodyPart bodyPart = null;
		for (int int2 = 0; int2 < this.getBodyParts().size(); ++int2) {
			int int3 = 0;
			if (!((BodyPart)this.getBodyParts().get(int2)).bandaged()) {
				if (((BodyPart)this.getBodyParts().get(int2)).bleeding()) {
					int3 += 100;
				}

				if (((BodyPart)this.getBodyParts().get(int2)).scratched()) {
					int3 += 50;
				}

				if (((BodyPart)this.getBodyParts().get(int2)).bitten()) {
					int3 += 50;
				}

				if (int3 > int1) {
					int1 = int3;
					bodyPart = (BodyPart)this.getBodyParts().get(int2);
				}
			}
		}

		if (int1 > 0 && bodyPart != null) {
			bodyPart.setBandaged(true, 10.0F);
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

	public void setBodyParts(ArrayList arrayList) {
		this.BodyParts = arrayList;
	}

	public int getDamageModCount() {
		return this.DamageModCount;
	}

	public void setDamageModCount(int int1) {
		this.DamageModCount = int1;
	}

	public float getInfectionGrowthRate() {
		return this.InfectionGrowthRate;
	}

	public void setInfectionGrowthRate(float float1) {
		this.InfectionGrowthRate = float1;
	}

	public void setInfectionLevel(float float1) {
		this.InfectionLevel = float1;
	}

	public boolean isInfected() {
		return this.IsInfected;
	}

	public void setInfected(boolean boolean1) {
		this.IsInfected = boolean1;
	}

	public float getInfectionTime() {
		return this.InfectionTime;
	}

	public void setInfectionTime(float float1) {
		this.InfectionTime = float1;
	}

	public float getInfectionMortalityDuration() {
		return this.InfectionMortalityDuration;
	}

	public void setInfectionMortalityDuration(float float1) {
		this.InfectionMortalityDuration = float1;
	}

	private float getCurrentTimeForInfection() {
		return this.getParentChar() instanceof IsoPlayer ? (float)((IsoPlayer)this.getParentChar()).getHoursSurvived() : (float)GameTime.getInstance().getWorldAgeHours();
	}

	@Deprecated
	public boolean isInf() {
		return this.IsInfected;
	}

	@Deprecated
	public void setInf(boolean boolean1) {
		this.IsInfected = boolean1;
	}

	public float getFakeInfectionLevel() {
		return this.FakeInfectionLevel;
	}

	public void setFakeInfectionLevel(float float1) {
		this.FakeInfectionLevel = float1;
	}

	public boolean isIsFakeInfected() {
		return this.IsFakeInfected;
	}

	public void setIsFakeInfected(boolean boolean1) {
		this.IsFakeInfected = boolean1;
		((BodyPart)this.getBodyParts().get(0)).SetFakeInfected(boolean1);
	}

	public float getOverallBodyHealth() {
		return this.OverallBodyHealth;
	}

	public void setOverallBodyHealth(float float1) {
		this.OverallBodyHealth = float1;
	}

	public float getStandardHealthAddition() {
		return this.StandardHealthAddition;
	}

	public void setStandardHealthAddition(float float1) {
		this.StandardHealthAddition = float1;
	}

	public float getReducedHealthAddition() {
		return this.ReducedHealthAddition;
	}

	public void setReducedHealthAddition(float float1) {
		this.ReducedHealthAddition = float1;
	}

	public float getSeverlyReducedHealthAddition() {
		return this.SeverlyReducedHealthAddition;
	}

	public void setSeverlyReducedHealthAddition(float float1) {
		this.SeverlyReducedHealthAddition = float1;
	}

	public float getSleepingHealthAddition() {
		return this.SleepingHealthAddition;
	}

	public void setSleepingHealthAddition(float float1) {
		this.SleepingHealthAddition = float1;
	}

	public float getHealthFromFood() {
		return this.HealthFromFood;
	}

	public void setHealthFromFood(float float1) {
		this.HealthFromFood = float1;
	}

	public float getHealthReductionFromSevereBadMoodles() {
		return this.HealthReductionFromSevereBadMoodles;
	}

	public void setHealthReductionFromSevereBadMoodles(float float1) {
		this.HealthReductionFromSevereBadMoodles = float1;
	}

	public int getStandardHealthFromFoodTime() {
		return this.StandardHealthFromFoodTime;
	}

	public void setStandardHealthFromFoodTime(int int1) {
		this.StandardHealthFromFoodTime = int1;
	}

	public float getHealthFromFoodTimer() {
		return this.HealthFromFoodTimer;
	}

	public void setHealthFromFoodTimer(float float1) {
		this.HealthFromFoodTimer = float1;
	}

	public void setBoredomLevel(float float1) {
		this.BoredomLevel = float1;
	}

	public float getBoredomDecreaseFromReading() {
		return this.BoredomDecreaseFromReading;
	}

	public void setBoredomDecreaseFromReading(float float1) {
		this.BoredomDecreaseFromReading = float1;
	}

	public float getInitialThumpPain() {
		return this.InitialThumpPain;
	}

	public void setInitialThumpPain(float float1) {
		this.InitialThumpPain = float1;
	}

	public float getInitialScratchPain() {
		return this.InitialScratchPain;
	}

	public void setInitialScratchPain(float float1) {
		this.InitialScratchPain = float1;
	}

	public float getInitialBitePain() {
		return this.InitialBitePain;
	}

	public void setInitialBitePain(float float1) {
		this.InitialBitePain = float1;
	}

	public float getInitialWoundPain() {
		return this.InitialWoundPain;
	}

	public void setInitialWoundPain(float float1) {
		this.InitialWoundPain = float1;
	}

	public float getContinualPainIncrease() {
		return this.ContinualPainIncrease;
	}

	public void setContinualPainIncrease(float float1) {
		this.ContinualPainIncrease = float1;
	}

	public float getPainReductionFromMeds() {
		return this.PainReductionFromMeds;
	}

	public void setPainReductionFromMeds(float float1) {
		this.PainReductionFromMeds = float1;
	}

	public float getStandardPainReductionWhenWell() {
		return this.StandardPainReductionWhenWell;
	}

	public void setStandardPainReductionWhenWell(float float1) {
		this.StandardPainReductionWhenWell = float1;
	}

	public int getOldNumZombiesVisible() {
		return this.OldNumZombiesVisible;
	}

	public void setOldNumZombiesVisible(int int1) {
		this.OldNumZombiesVisible = int1;
	}

	public int getCurrentNumZombiesVisible() {
		return this.CurrentNumZombiesVisible;
	}

	public void setCurrentNumZombiesVisible(int int1) {
		this.CurrentNumZombiesVisible = int1;
	}

	public float getPanicIncreaseValue() {
		return this.PanicIncreaseValue;
	}

	public void setPanicIncreaseValue(float float1) {
		this.PanicIncreaseValue = float1;
	}

	public float getPanicReductionValue() {
		return this.PanicReductionValue;
	}

	public void setPanicReductionValue(float float1) {
		this.PanicReductionValue = float1;
	}

	public float getDrunkIncreaseValue() {
		return this.DrunkIncreaseValue;
	}

	public void setDrunkIncreaseValue(float float1) {
		this.DrunkIncreaseValue = float1;
	}

	public float getDrunkReductionValue() {
		return this.DrunkReductionValue;
	}

	public void setDrunkReductionValue(float float1) {
		this.DrunkReductionValue = float1;
	}

	public boolean isIsOnFire() {
		return this.IsOnFire;
	}

	public void setIsOnFire(boolean boolean1) {
		this.IsOnFire = boolean1;
	}

	public boolean isBurntToDeath() {
		return this.BurntToDeath;
	}

	public void setBurntToDeath(boolean boolean1) {
		this.BurntToDeath = boolean1;
	}

	public void setWetness(float float1) {
		this.Wetness = float1;
	}

	public float getCatchACold() {
		return this.CatchACold;
	}

	public void setCatchACold(float float1) {
		this.CatchACold = float1;
	}

	public boolean isHasACold() {
		return this.HasACold;
	}

	public void setHasACold(boolean boolean1) {
		this.HasACold = boolean1;
	}

	public void setColdStrength(float float1) {
		this.ColdStrength = float1;
	}

	public float getColdProgressionRate() {
		return this.ColdProgressionRate;
	}

	public void setColdProgressionRate(float float1) {
		this.ColdProgressionRate = float1;
	}

	public int getTimeToSneezeOrCough() {
		return this.TimeToSneezeOrCough;
	}

	public void setTimeToSneezeOrCough(int int1) {
		this.TimeToSneezeOrCough = int1;
	}

	public int getMildColdSneezeTimerMin() {
		return this.MildColdSneezeTimerMin;
	}

	public void setMildColdSneezeTimerMin(int int1) {
		this.MildColdSneezeTimerMin = int1;
	}

	public int getMildColdSneezeTimerMax() {
		return this.MildColdSneezeTimerMax;
	}

	public void setMildColdSneezeTimerMax(int int1) {
		this.MildColdSneezeTimerMax = int1;
	}

	public int getColdSneezeTimerMin() {
		return this.ColdSneezeTimerMin;
	}

	public void setColdSneezeTimerMin(int int1) {
		this.ColdSneezeTimerMin = int1;
	}

	public int getColdSneezeTimerMax() {
		return this.ColdSneezeTimerMax;
	}

	public void setColdSneezeTimerMax(int int1) {
		this.ColdSneezeTimerMax = int1;
	}

	public int getNastyColdSneezeTimerMin() {
		return this.NastyColdSneezeTimerMin;
	}

	public void setNastyColdSneezeTimerMin(int int1) {
		this.NastyColdSneezeTimerMin = int1;
	}

	public int getNastyColdSneezeTimerMax() {
		return this.NastyColdSneezeTimerMax;
	}

	public void setNastyColdSneezeTimerMax(int int1) {
		this.NastyColdSneezeTimerMax = int1;
	}

	public int getSneezeCoughActive() {
		return this.SneezeCoughActive;
	}

	public void setSneezeCoughActive(int int1) {
		this.SneezeCoughActive = int1;
	}

	public int getSneezeCoughTime() {
		return this.SneezeCoughTime;
	}

	public void setSneezeCoughTime(int int1) {
		this.SneezeCoughTime = int1;
	}

	public int getSneezeCoughDelay() {
		return this.SneezeCoughDelay;
	}

	public void setSneezeCoughDelay(int int1) {
		this.SneezeCoughDelay = int1;
	}

	public void setUnhappynessLevel(float float1) {
		this.UnhappynessLevel = float1;
	}

	public IsoGameCharacter getParentChar() {
		return this.ParentChar;
	}

	public void setParentChar(IsoGameCharacter gameCharacter) {
		this.ParentChar = gameCharacter;
	}

	public float getTemperature() {
		return this.Temperature;
	}

	public void setTemperature(float float1) {
		this.lastTemperature = this.Temperature;
		this.Temperature = float1;
	}

	public float getTemperatureChangeTick() {
		return this.Temperature - this.lastTemperature;
	}

	public void setPoisonLevel(float float1) {
		this.PoisonLevel = float1;
	}

	public float getPoisonLevel() {
		return this.PoisonLevel;
	}

	public float getFoodSicknessLevel() {
		return this.FoodSicknessLevel;
	}

	public void setFoodSicknessLevel(float float1) {
		this.FoodSicknessLevel = Math.max(float1, 0.0F);
	}

	public boolean isReduceFakeInfection() {
		return this.reduceFakeInfection;
	}

	public void setReduceFakeInfection(boolean boolean1) {
		this.reduceFakeInfection = boolean1;
	}

	public void AddRandomDamage() {
		BodyPart bodyPart = (BodyPart)this.getBodyParts().get(Rand.Next(this.getBodyParts().size()));
		switch (Rand.Next(4)) {
		case 0: 
			bodyPart.generateDeepWound();
			if (Rand.Next(4) == 0) {
				bodyPart.setInfectedWound(true);
			}

			break;
		
		case 1: 
			bodyPart.generateDeepShardWound();
			if (Rand.Next(4) == 0) {
				bodyPart.setInfectedWound(true);
			}

			break;
		
		case 2: 
			bodyPart.setFractureTime((float)Rand.Next(30, 50));
			break;
		
		case 3: 
			bodyPart.setBurnTime((float)Rand.Next(30, 50));
		
		}
	}

	public float getPainReduction() {
		return this.painReduction;
	}

	public void setPainReduction(float float1) {
		this.painReduction = float1;
	}

	public float getColdReduction() {
		return this.coldReduction;
	}

	public void setColdReduction(float float1) {
		this.coldReduction = float1;
	}

	public int getRemotePainLevel() {
		return this.RemotePainLevel;
	}

	public void setRemotePainLevel(int int1) {
		this.RemotePainLevel = int1;
	}

	public float getColdDamageStage() {
		return this.ColdDamageStage;
	}

	public void setColdDamageStage(float float1) {
		this.ColdDamageStage = float1;
	}
}
