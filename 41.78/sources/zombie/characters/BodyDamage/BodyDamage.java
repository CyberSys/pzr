package zombie.characters.BodyDamage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
import zombie.FliesSound;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.WorldSoundManager;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.ClothingWetness;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.characters.Moodles.MoodleType;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.Literature;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.util.StringUtils;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehicleWindow;


public final class BodyDamage {
	public final ArrayList BodyParts = new ArrayList(18);
	public final ArrayList BodyPartsLastState = new ArrayList(18);
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
	public float PanicIncreaseValueFrame = 0.035F;
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
	private Thermoregulator thermoregulator;
	public static final float InfectionLevelToZombify = 0.001F;
	static String behindStr = "BEHIND";
	static String leftStr = "LEFT";
	static String rightStr = "RIGHT";

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
		Iterator iterator = this.BodyParts.iterator();
		while (iterator.hasNext()) {
			BodyPart bodyPart = (BodyPart)iterator.next();
			this.BodyPartsLastState.add(new BodyPartLast());
		}

		this.RestoreToFullHealth();
		this.ParentChar = gameCharacter;
		if (this.ParentChar instanceof IsoPlayer) {
			this.thermoregulator = new Thermoregulator(this);
		}

		this.setBodyPartsLastState();
	}

	public BodyPart getBodyPart(BodyPartType bodyPartType) {
		return (BodyPart)this.BodyParts.get(BodyPartType.ToIndex(bodyPartType));
	}

	public BodyPartLast getBodyPartsLastState(BodyPartType bodyPartType) {
		return (BodyPartLast)this.BodyPartsLastState.get(BodyPartType.ToIndex(bodyPartType));
	}

	public void setBodyPartsLastState() {
		for (int int1 = 0; int1 < this.getBodyParts().size(); ++int1) {
			BodyPart bodyPart = (BodyPart)this.getBodyParts().get(int1);
			BodyPartLast bodyPartLast = (BodyPartLast)this.BodyPartsLastState.get(int1);
			bodyPartLast.copy(bodyPart);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		for (int int2 = 0; int2 < this.getBodyParts().size(); ++int2) {
			BodyPart bodyPart = (BodyPart)this.getBodyParts().get(int2);
			bodyPart.SetBitten(byteBuffer.get() == 1);
			bodyPart.setScratched(byteBuffer.get() == 1, false);
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
				bodyPart.setSplintItem(GameWindow.ReadString(byteBuffer));
				bodyPart.setBandageType(GameWindow.ReadString(byteBuffer));
				bodyPart.setCutTime(byteBuffer.getFloat());
				if (int1 >= 153) {
					bodyPart.setWetness(byteBuffer.getFloat());
				}

				if (int1 >= 167) {
					bodyPart.setStiffness(byteBuffer.getFloat());
				}
			}
		}

		this.setBodyPartsLastState();
		this.setInfectionLevel(byteBuffer.getFloat());
		this.setFakeInfectionLevel(byteBuffer.getFloat());
		this.setWetness(byteBuffer.getFloat());
		this.setCatchACold(byteBuffer.getFloat());
		this.setHasACold(byteBuffer.get() == 1);
		this.setColdStrength(byteBuffer.getFloat());
		this.setUnhappynessLevel(byteBuffer.getFloat());
		this.setBoredomLevel(byteBuffer.getFloat());
		this.setFoodSicknessLevel(byteBuffer.getFloat());
		this.PoisonLevel = byteBuffer.getFloat();
		float float1 = byteBuffer.getFloat();
		this.setTemperature(float1);
		this.setReduceFakeInfection(byteBuffer.get() == 1);
		this.setHealthFromFoodTimer(byteBuffer.getFloat());
		this.painReduction = byteBuffer.getFloat();
		this.coldReduction = byteBuffer.getFloat();
		this.InfectionTime = byteBuffer.getFloat();
		this.InfectionMortalityDuration = byteBuffer.getFloat();
		this.ColdDamageStage = byteBuffer.getFloat();
		this.calculateOverallHealth();
		if (int1 >= 153 && byteBuffer.get() == 1) {
			if (this.thermoregulator != null) {
				this.thermoregulator.load(byteBuffer, int1);
			} else {
				Thermoregulator thermoregulator = new Thermoregulator(this);
				thermoregulator.load(byteBuffer, int1);
				DebugLog.log("Couldnt load Thermoregulator, == null");
			}
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
			byteBuffer.putFloat(bodyPart.getCutTime());
			byteBuffer.putFloat(bodyPart.getWetness());
			byteBuffer.putFloat(bodyPart.getStiffness());
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
		byteBuffer.put((byte)(this.thermoregulator != null ? 1 : 0));
		if (this.thermoregulator != null) {
			this.thermoregulator.save(byteBuffer);
		}
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

	public void IncreasePanicFloat(float float1) {
		float float2 = 1.0F;
		if (this.getParentChar().getBetaEffect() > 0.0F) {
			float2 -= this.getParentChar().getBetaDelta();
			if (float2 > 1.0F) {
				float2 = 1.0F;
			}

			if (float2 < 0.0F) {
				float2 = 0.0F;
			}
		}

		if (this.getParentChar().getCharacterTraits().Cowardly.isSet()) {
			float2 *= 2.0F;
		}

		if (this.getParentChar().getCharacterTraits().Brave.isSet()) {
			float2 *= 0.3F;
		}

		if (this.getParentChar().getCharacterTraits().Desensitized.isSet()) {
			float2 = 0.0F;
		}

		Stats stats = this.ParentChar.getStats();
		stats.Panic += this.getPanicIncreaseValueFrame() * float1 * float2;
		if (this.getParentChar().getStats().Panic > 100.0F) {
			this.ParentChar.getStats().Panic = 100.0F;
		}
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

		if (this.getParentChar().getCharacterTraits().Cowardly.isSet()) {
			float1 *= 2.0F;
		}

		if (this.getParentChar().getCharacterTraits().Brave.isSet()) {
			float1 *= 0.3F;
		}

		if (this.getParentChar().getCharacterTraits().Desensitized.isSet()) {
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

	public void JustDrankBooze(Food food, float float1) {
		float float2 = 1.0F;
		if (this.getParentChar().Traits.HeavyDrinker.isSet()) {
			float2 = 0.3F;
		}

		if (this.getParentChar().Traits.LightDrinker.isSet()) {
			float2 = 4.0F;
		}

		if (food.getBaseHunger() != 0.0F) {
			float1 = food.getHungChange() * float1 / food.getBaseHunger() * 2.0F;
		}

		float2 *= float1;
		if (food.getName().toLowerCase().contains("beer") || food.hasTag("LowAlcohol")) {
			float2 *= 0.25F;
		}

		if ((double)this.getParentChar().getStats().hunger > 0.8) {
			float2 = (float)((double)float2 * 1.25);
		} else if ((double)this.getParentChar().getStats().hunger > 0.6) {
			float2 = (float)((double)float2 * 1.1);
		}

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
			if (this.getParentChar().Traits.IronGut.isSet()) {
				float2 /= 2.0F;
			}

			if (this.getParentChar().Traits.WeakStomach.isSet()) {
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
				if (food.hasTag("Egg")) {
					int1 = 5;
				}

				if (this.getParentChar().Traits.IronGut.isSet()) {
					int1 /= 2;
					if (food.hasTag("Egg")) {
						int1 = 0;
					}
				}

				if (this.getParentChar().Traits.WeakStomach.isSet()) {
					int1 *= 2;
				}

				if (int1 > 0 && Rand.Next(100) < int1 && !this.isInfected()) {
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

				if (this.getParentChar().Traits.IronGut.isSet()) {
					int2 /= 2;
				}

				if (this.getParentChar().Traits.WeakStomach.isSet()) {
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
		boolean boolean1 = square == null || !square.isInARoom() && !square.haveRoof;
		if (baseVehicle != null && baseVehicle.hasRoof(baseVehicle.getSeat(this.getParentChar()))) {
			boolean1 = false;
		}

		ClothingWetness clothingWetness = this.getParentChar().getClothingWetness();
		float float1 = 0.0F;
		float float2 = 0.0F;
		float float3 = 0.0F;
		if (baseVehicle != null && ClimateManager.getInstance().isRaining()) {
			VehiclePart vehiclePart = baseVehicle.getPartById("Windshield");
			if (vehiclePart != null) {
				VehicleWindow vehicleWindow = vehiclePart.getWindow();
				if (vehicleWindow != null && vehicleWindow.isDestroyed()) {
					float float4 = ClimateManager.getInstance().getRainIntensity();
					float4 *= float4;
					float4 *= baseVehicle.getCurrentSpeedKmHour() / 50.0F;
					if (float4 < 0.1F) {
						float4 = 0.0F;
					}

					if (float4 > 1.0F) {
						float4 = 1.0F;
					}

					float3 = float4 * 3.0F;
					float1 = float4;
				}
			}
		}

		if (boolean1 && gameCharacter.isAsleep() && gameCharacter.getBed() != null && "Tent".equals(gameCharacter.getBed().getName())) {
			boolean1 = false;
		}

		float float5;
		float float6;
		if (boolean1 && ClimateManager.getInstance().isRaining()) {
			float5 = ClimateManager.getInstance().getRainIntensity();
			if ((double)float5 < 0.1) {
				float5 = 0.0F;
			}

			float1 = float5;
		} else if (!boolean1 || !ClimateManager.getInstance().isRaining()) {
			float5 = ClimateManager.getInstance().getAirTemperatureForCharacter(this.getParentChar());
			float6 = 0.1F;
			if (float5 > 5.0F) {
				float6 += (float5 - 5.0F) / 10.0F;
			}

			float6 -= float3;
			if (float6 < 0.0F) {
				float6 = 0.0F;
			}

			float2 = float6;
		}

		if (clothingWetness != null) {
			clothingWetness.updateWetness(float1, float2);
		}

		float5 = 0.0F;
		if (this.BodyParts.size() > 0) {
			for (int int1 = 0; int1 < this.BodyParts.size(); ++int1) {
				float5 += ((BodyPart)this.BodyParts.get(int1)).getWetness();
			}

			float5 /= (float)this.BodyParts.size();
		}

		this.Wetness = PZMath.clamp(float5, 0.0F, 100.0F);
		float6 = 0.0F;
		if (this.thermoregulator != null) {
			float6 = this.thermoregulator.getCatchAColdDelta();
		}

		if (!this.isHasACold() && float6 > 0.1F) {
			if (this.getParentChar().Traits.ProneToIllness.isSet()) {
				float6 *= 1.7F;
			}

			if (this.getParentChar().Traits.Resilient.isSet()) {
				float6 *= 0.45F;
			}

			if (this.getParentChar().Traits.Outdoorsman.isSet()) {
				float6 *= 0.1F;
			}

			this.setCatchACold(this.getCatchACold() + (float)ZomboidGlobals.CatchAColdIncreaseRate * float6 * GameTime.instance.getMultiplier());
			if (this.getCatchACold() >= 100.0F) {
				this.setCatchACold(0.0F);
				this.setHasACold(true);
				this.setColdStrength(20.0F);
				this.setTimeToSneezeOrCough(0);
			}
		}

		if (float6 <= 0.1F) {
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
			if (this.getParentChar().getPrimaryHandItem() == null || !this.getParentChar().getPrimaryHandItem().getType().equals("Tissue") && !this.getParentChar().getPrimaryHandItem().getType().equals("ToiletPaper")) {
				if (this.getParentChar().getSecondaryHandItem() != null && (this.getParentChar().getSecondaryHandItem().getType().equals("Tissue") || this.getParentChar().getSecondaryHandItem().getType().equals("ToiletPaper")) && ((Drainable)this.getParentChar().getSecondaryHandItem()).getUsedDelta() > 0.0F) {
					((Drainable)this.getParentChar().getSecondaryHandItem()).setUsedDelta(((Drainable)this.getParentChar().getSecondaryHandItem()).getUsedDelta() - 0.1F);
					if (((Drainable)this.getParentChar().getSecondaryHandItem()).getUsedDelta() <= 0.0F) {
						this.getParentChar().getSecondaryHandItem().Use();
					}

					boolean1 = true;
				}
			} else if (((Drainable)this.getParentChar().getPrimaryHandItem()).getUsedDelta() > 0.0F) {
				((Drainable)this.getParentChar().getPrimaryHandItem()).setUsedDelta(((Drainable)this.getParentChar().getPrimaryHandItem()).getUsedDelta() - 0.1F);
				if (((Drainable)this.getParentChar().getPrimaryHandItem()).getUsedDelta() <= 0.0F) {
					this.getParentChar().getPrimaryHandItem().Use();
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
			if (square == null || !square.isInARoom() || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Wet) > 0 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hypothermia) >= 1 || this.getParentChar().getStats().fatigue > 0.5F || this.getParentChar().getStats().hunger > 0.25F || this.getParentChar().getStats().thirst > 0.25F) {
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
				if (this.getParentChar().Traits.ProneToIllness.isSet()) {
					float1 = 0.5F;
				}

				if (this.getParentChar().Traits.Resilient.isSet()) {
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
				if (this.getParentChar().Traits.ProneToIllness.isSet()) {
					float1 = 1.2F;
				}

				if (this.getParentChar().Traits.Resilient.isSet()) {
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
		if (this.getOverallBodyHealth() <= 10.0F) {
			this.getParentChar().forceAwake();
		}

		if (!(float1 <= 0.0F)) {
			float float2 = float1 / (float)BodyPartType.ToIndex(BodyPartType.MAX);
			for (int int1 = 0; int1 < BodyPartType.ToIndex(BodyPartType.MAX); ++int1) {
				((BodyPart)this.getBodyParts().get(int1)).ReduceHealth(float2 / BodyPartType.getDamageModifyer(int1));
			}
		}
	}

	public void AddDamage(int int1, float float1) {
		((BodyPart)this.getBodyParts().get(int1)).AddDamage(float1);
	}

	public void splatBloodFloorBig() {
		this.getParentChar().splatBloodFloorBig();
		this.getParentChar().splatBloodFloorBig();
		this.getParentChar().splatBloodFloorBig();
	}

	public void DamageFromWeapon(HandWeapon handWeapon) {
		if (GameServer.bServer) {
			if (handWeapon != null) {
				this.getParentChar().sendObjectChange("DamageFromWeapon", new Object[]{"weapon", handWeapon.getFullType()});
			}
		} else if (!(this.getParentChar() instanceof IsoPlayer) || ((IsoPlayer)this.getParentChar()).isLocalPlayer()) {
			boolean boolean1 = false;
			boolean boolean2 = true;
			boolean boolean3 = true;
			int int1 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.MAX));
			if (DebugOptions.instance.MultiplayerTorsoHit.getValue()) {
				int1 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Torso_Upper), BodyPartType.ToIndex(BodyPartType.Head));
			}

			boolean boolean4 = false;
			boolean boolean5 = false;
			boolean boolean6 = false;
			boolean boolean7 = true;
			boolean boolean8 = true;
			byte byte1;
			if (!handWeapon.getCategories().contains("Blunt") && !handWeapon.getCategories().contains("SmallBlunt")) {
				if (!handWeapon.isAimedFirearm()) {
					byte1 = 1;
					boolean5 = true;
				} else {
					boolean6 = true;
					byte1 = 2;
				}
			} else {
				boolean8 = false;
				byte1 = 0;
				boolean4 = true;
			}

			BodyPart bodyPart = this.getBodyPart(BodyPartType.FromIndex(int1));
			float float1 = this.getParentChar().getBodyPartClothingDefense(bodyPart.getIndex(), boolean5, boolean6);
			if ((float)Rand.Next(100) < float1) {
				boolean3 = false;
				this.getParentChar().addHoleFromZombieAttacks(BloodBodyPartType.FromIndex(int1), false);
			}

			if (boolean3) {
				this.getParentChar().addHole(BloodBodyPartType.FromIndex(int1));
				this.getParentChar().splatBloodFloorBig();
				this.getParentChar().splatBloodFloorBig();
				this.getParentChar().splatBloodFloorBig();
				if (boolean5) {
					if (Rand.NextBool(6)) {
						bodyPart.generateDeepWound();
					} else if (Rand.NextBool(3)) {
						bodyPart.setCut(true);
					} else {
						bodyPart.setScratched(true, true);
					}
				} else if (boolean4) {
					if (Rand.NextBool(4)) {
						bodyPart.setCut(true);
					} else {
						bodyPart.setScratched(true, true);
					}
				} else if (boolean6) {
					bodyPart.setHaveBullet(true, 0);
				}

				float float2 = Rand.Next(handWeapon.getMinDamage(), handWeapon.getMaxDamage()) * 15.0F;
				if (int1 == BodyPartType.ToIndex(BodyPartType.Head)) {
					float2 *= 4.0F;
				}

				if (int1 == BodyPartType.ToIndex(BodyPartType.Neck)) {
					float2 *= 4.0F;
				}

				if (int1 == BodyPartType.ToIndex(BodyPartType.Torso_Upper)) {
					float2 *= 2.0F;
				}

				if (GameClient.bClient) {
					if (handWeapon.isRanged()) {
						float2 = (float)((double)float2 * ServerOptions.getInstance().PVPFirearmDamageModifier.getValue());
					} else {
						float2 = (float)((double)float2 * ServerOptions.getInstance().PVPMeleeDamageModifier.getValue());
					}
				}

				this.AddDamage(int1, float2);
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

				if (this.ParentChar instanceof IsoPlayer && GameClient.bClient && ((IsoPlayer)this.ParentChar).isLocalPlayer()) {
					IsoPlayer player = (IsoPlayer)this.ParentChar;
					player.updateMovementRates();
					GameClient.sendPlayerInjuries(player);
					GameClient.sendPlayerDamage(player);
				}
			}
		}
	}

	public boolean AddRandomDamageFromZombie(IsoZombie zombie, String string) {
		if (StringUtils.isNullOrEmpty(string)) {
			string = "Bite";
		}

		this.getParentChar().setVariable("hitpvp", false);
		if (GameServer.bServer) {
			this.getParentChar().sendObjectChange("AddRandomDamageFromZombie", new Object[]{"zombie", zombie.OnlineID});
			return true;
		} else {
			byte byte1 = 0;
			boolean boolean1 = false;
			int int1 = 15 + this.getParentChar().getMeleeCombatMod();
			byte byte2 = 85;
			byte byte3 = 65;
			String string2 = this.getParentChar().testDotSide(zombie);
			boolean boolean2 = string2.equals(behindStr);
			boolean boolean3 = string2.equals(leftStr) || string2.equals(rightStr);
			int int2 = this.getParentChar().getSurroundingAttackingZombies();
			int2 = Math.max(int2, 1);
			int1 -= (int2 - 1) * 10;
			int int3 = byte2 - (int2 - 1) * 30;
			int int4 = byte3 - (int2 - 1) * 15;
			byte byte4 = 3;
			if (SandboxOptions.instance.Lore.Strength.getValue() == 1) {
				byte4 = 2;
			}

			if (SandboxOptions.instance.Lore.Strength.getValue() == 3) {
				byte4 = 6;
			}

			if (this.ParentChar.Traits.ThickSkinned.isSet()) {
				int1 = (int)((double)int1 * 1.3);
			}

			if (this.ParentChar.Traits.ThinSkinned.isSet()) {
				int1 = (int)((double)int1 / 1.3);
			}

			if (!"EndDeath".equals(this.getParentChar().getHitReaction())) {
				if (!this.getParentChar().isGodMod() && int2 >= byte4 && SandboxOptions.instance.Lore.ZombiesDragDown.getValue() && !this.getParentChar().isSitOnGround()) {
					int3 = 0;
					int4 = 0;
					int1 = 0;
					this.getParentChar().setHitReaction("EndDeath");
					this.getParentChar().setDeathDragDown(true);
				} else {
					this.getParentChar().setHitReaction(string);
				}
			}

			if (boolean2) {
				int1 -= 15;
				int3 -= 25;
				int4 -= 35;
				if (SandboxOptions.instance.RearVulnerability.getValue() == 1) {
					int1 += 15;
					int3 += 25;
					int4 += 35;
				}

				if (SandboxOptions.instance.RearVulnerability.getValue() == 2) {
					int1 += 7;
					int3 += 17;
					int4 += 23;
				}

				if (int2 > 2) {
					int3 -= 15;
					int4 -= 15;
				}
			}

			if (boolean3) {
				int1 -= 30;
				int3 -= 7;
				int4 -= 27;
				if (SandboxOptions.instance.RearVulnerability.getValue() == 1) {
					int1 += 30;
					int3 += 7;
					int4 += 27;
				}

				if (SandboxOptions.instance.RearVulnerability.getValue() == 2) {
					int1 += 15;
					int3 += 4;
					int4 += 15;
				}
			}

			float float1;
			boolean boolean4;
			int int5;
			if (!zombie.bCrawling) {
				if (Rand.Next(10) == 0) {
					int5 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.Groin) + 1);
				} else {
					int5 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.Neck) + 1);
				}

				float1 = 10.0F * (float)int2;
				if (boolean2) {
					float1 += 5.0F;
				}

				if (boolean3) {
					float1 += 2.0F;
				}

				if (boolean2 && (float)Rand.Next(100) < float1) {
					int5 = BodyPartType.ToIndex(BodyPartType.Neck);
				}

				if (int5 == BodyPartType.ToIndex(BodyPartType.Head) || int5 == BodyPartType.ToIndex(BodyPartType.Neck)) {
					byte byte5 = 70;
					if (boolean2) {
						byte5 = 90;
					}

					if (boolean3) {
						byte5 = 80;
					}

					if (Rand.Next(100) > byte5) {
						boolean4 = false;
						label228: while (true) {
							do {
								if (boolean4) {
									break label228;
								}

								boolean4 = true;
								int5 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Torso_Lower) + 1);
							}					 while (int5 != BodyPartType.ToIndex(BodyPartType.Head) && int5 != BodyPartType.ToIndex(BodyPartType.Neck) && int5 != BodyPartType.ToIndex(BodyPartType.Groin));

							boolean4 = false;
						}
					}
				}
			} else {
				if (Rand.Next(2) != 0) {
					return false;
				}

				if (Rand.Next(10) == 0) {
					int5 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Groin), BodyPartType.ToIndex(BodyPartType.MAX));
				} else {
					int5 = Rand.Next(BodyPartType.ToIndex(BodyPartType.UpperLeg_L), BodyPartType.ToIndex(BodyPartType.MAX));
				}
			}

			if (zombie.inactive) {
				int1 += 20;
				int3 += 20;
				int4 += 20;
			}

			float1 = (float)Rand.Next(1000) / 1000.0F;
			float1 *= (float)(Rand.Next(10) + 10);
			if (GameServer.bServer && this.ParentChar instanceof IsoPlayer || Core.bDebug && this.ParentChar instanceof IsoPlayer) {
				DebugLog.log(DebugType.Combat, "zombie did " + float1 + " dmg to " + ((IsoPlayer)this.ParentChar).getDisplayName() + " on body part " + BodyPartType.getDisplayName(BodyPartType.FromIndex(int5)));
			}

			boolean boolean5 = false;
			boolean4 = true;
			if (Rand.Next(100) > int1) {
				zombie.scratch = true;
				this.getParentChar().helmetFall(int5 == BodyPartType.ToIndex(BodyPartType.Neck) || int5 == BodyPartType.ToIndex(BodyPartType.Head));
				if (Rand.Next(100) > int4) {
					zombie.scratch = false;
					zombie.laceration = true;
				}

				if (Rand.Next(100) > int3) {
					zombie.scratch = false;
					zombie.laceration = false;
					boolean4 = false;
				}

				Float Float1;
				boolean boolean6;
				if (zombie.scratch) {
					Float1 = this.getParentChar().getBodyPartClothingDefense(int5, false, false);
					zombie.parameterZombieState.setState(ParameterZombieState.State.AttackScratch);
					if (this.getHealth() > 0.0F) {
						this.getParentChar().getEmitter().playSoundImpl("ZombieScratch", (IsoObject)null);
					}

					if ((float)Rand.Next(100) < Float1) {
						this.getParentChar().addHoleFromZombieAttacks(BloodBodyPartType.FromIndex(int5), boolean4);
						return false;
					}

					boolean6 = this.getParentChar().addHole(BloodBodyPartType.FromIndex(int5), true);
					if (boolean6) {
						this.getParentChar().getEmitter().playSoundImpl("ZombieRipClothing", (IsoObject)null);
					}

					boolean5 = true;
					this.AddDamage(int5, float1);
					this.SetScratched(int5, true);
					this.getParentChar().addBlood(BloodBodyPartType.FromIndex(int5), true, false, true);
					byte1 = 1;
					if (GameServer.bServer && this.ParentChar instanceof IsoPlayer) {
						DebugLog.log(DebugType.Combat, "zombie scratched " + ((IsoPlayer)this.ParentChar).username);
					}
				} else if (zombie.laceration) {
					Float1 = this.getParentChar().getBodyPartClothingDefense(int5, false, false);
					zombie.parameterZombieState.setState(ParameterZombieState.State.AttackLacerate);
					if (this.getHealth() > 0.0F) {
						this.getParentChar().getEmitter().playSoundImpl("ZombieScratch", (IsoObject)null);
					}

					if ((float)Rand.Next(100) < Float1) {
						this.getParentChar().addHoleFromZombieAttacks(BloodBodyPartType.FromIndex(int5), boolean4);
						return false;
					}

					boolean6 = this.getParentChar().addHole(BloodBodyPartType.FromIndex(int5), true);
					if (boolean6) {
						this.getParentChar().getEmitter().playSoundImpl("ZombieRipClothing", (IsoObject)null);
					}

					boolean5 = true;
					this.AddDamage(int5, float1);
					this.SetCut(int5, true);
					this.getParentChar().addBlood(BloodBodyPartType.FromIndex(int5), true, false, true);
					byte1 = 1;
					if (GameServer.bServer && this.ParentChar instanceof IsoPlayer) {
						DebugLog.log(DebugType.Combat, "zombie laceration " + ((IsoPlayer)this.ParentChar).username);
					}
				} else {
					Float1 = this.getParentChar().getBodyPartClothingDefense(int5, true, false);
					zombie.parameterZombieState.setState(ParameterZombieState.State.AttackBite);
					if (this.getHealth() > 0.0F) {
						this.getParentChar().getEmitter().playSoundImpl("ZombieBite", (IsoObject)null);
					}

					if ((float)Rand.Next(100) < Float1) {
						this.getParentChar().addHoleFromZombieAttacks(BloodBodyPartType.FromIndex(int5), boolean4);
						return false;
					}

					boolean6 = this.getParentChar().addHole(BloodBodyPartType.FromIndex(int5), true);
					if (boolean6) {
						this.getParentChar().getEmitter().playSoundImpl("ZombieRipClothing", (IsoObject)null);
					}

					boolean5 = true;
					this.AddDamage(int5, float1);
					this.SetBitten(int5, true);
					if (int5 == BodyPartType.ToIndex(BodyPartType.Neck)) {
						this.getParentChar().addBlood(BloodBodyPartType.FromIndex(int5), false, true, true);
						this.getParentChar().addBlood(BloodBodyPartType.FromIndex(int5), false, true, true);
						this.getParentChar().addBlood(BloodBodyPartType.Torso_Upper, false, true, false);
					}

					this.getParentChar().addBlood(BloodBodyPartType.FromIndex(int5), false, true, true);
					if (GameServer.bServer && this.ParentChar instanceof IsoPlayer) {
						DebugLog.log(DebugType.Combat, "zombie bite " + ((IsoPlayer)this.ParentChar).username);
					}

					byte1 = 2;
					this.getParentChar().splatBloodFloorBig();
					this.getParentChar().splatBloodFloorBig();
					this.getParentChar().splatBloodFloorBig();
				}
			}

			if (!boolean5) {
				this.getParentChar().addHoleFromZombieAttacks(BloodBodyPartType.FromIndex(int5), boolean4);
			}

			Stats stats;
			switch (byte1) {
			case 0: 
				stats = this.ParentChar.getStats();
				stats.Pain += this.getInitialThumpPain() * BodyPartType.getPainModifyer(int5);
				break;
			
			case 1: 
				stats = this.ParentChar.getStats();
				stats.Pain += this.getInitialScratchPain() * BodyPartType.getPainModifyer(int5);
				break;
			
			case 2: 
				stats = this.ParentChar.getStats();
				stats.Pain += this.getInitialBitePain() * BodyPartType.getPainModifyer(int5);
			
			}

			if (this.getParentChar().getStats().Pain > 100.0F) {
				this.ParentChar.getStats().Pain = 100.0F;
			}

			if (this.ParentChar instanceof IsoPlayer && GameClient.bClient && ((IsoPlayer)this.ParentChar).isLocalPlayer()) {
				IsoPlayer player = (IsoPlayer)this.ParentChar;
				player.updateMovementRates();
				GameClient.sendPlayerInjuries(player);
				GameClient.sendPlayerDamage(player);
			}

			return true;
		}
	}

	public boolean doesBodyPartHaveInjury(BodyPartType bodyPartType) {
		return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).HasInjury();
	}

	public boolean doBodyPartsHaveInjuries(BodyPartType bodyPartType, BodyPartType bodyPartType2) {
		return this.doesBodyPartHaveInjury(bodyPartType) || this.doesBodyPartHaveInjury(bodyPartType2);
	}

	public boolean isBodyPartBleeding(BodyPartType bodyPartType) {
		return this.getBodyPart(bodyPartType).getBleedingTime() > 0.0F;
	}

	public boolean areBodyPartsBleeding(BodyPartType bodyPartType, BodyPartType bodyPartType2) {
		return this.isBodyPartBleeding(bodyPartType) || this.isBodyPartBleeding(bodyPartType2);
	}

	public void DrawUntexturedQuad(int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4) {
		SpriteRenderer.instance.renderi((Texture)null, int1, int2, int3, int4, float1, float2, float3, float4, (Consumer)null);
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

	public boolean IsCut(BodyPartType bodyPartType) {
		return ((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).getCutTime() > 0.0F;
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
			this.getParentChar().getStats().resetStats();
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
		if (this.thermoregulator != null) {
			this.thermoregulator.reset();
		}
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
		((BodyPart)this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType))).setScratched(boolean1, false);
	}

	public void SetScratched(int int1, boolean boolean1) {
		((BodyPart)this.getBodyParts().get(int1)).setScratched(boolean1, false);
	}

	public void SetScratchedFromWeapon(int int1, boolean boolean1) {
		((BodyPart)this.getBodyParts().get(int1)).SetScratchedWeapon(boolean1);
	}

	public void SetCut(int int1, boolean boolean1) {
		((BodyPart)this.getBodyParts().get(int1)).setCut(boolean1, false);
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
				if (this.getParentChar().getCurrentSquare().isInARoom()) {
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

				if (this.getParentChar().Traits.Smoker.isSet()) {
					this.getParentChar().setTimeSinceLastSmoke(this.getParentChar().getTimeSinceLastSmoke() + 1.0E-4F * GameTime.instance.getMultiplier());
					if (this.getParentChar().getTimeSinceLastSmoke() > 1.0F) {
						double double1 = Math.floor((double)(this.getParentChar().getTimeSinceLastSmoke() / 10.0F)) + 1.0;
						if (double1 > 10.0) {
							double1 = 10.0;
						}

						this.getParentChar().getStats().setStressFromCigarettes((float)((double)this.getParentChar().getStats().getStressFromCigarettes() + ZomboidGlobals.StressFromBiteOrScratch / 8.0 * double1 * (double)GameTime.instance.getMultiplier()));
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

			this.getParentChar().setMaxWeight((int)((float)this.getParentChar().getMaxWeightBase() * this.getParentChar().getWeightMod()) - int1);
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
		if (this.getParentChar().Traits.Resilient.isSet()) {
			float1 = 1.25F;
		}

		if (this.getParentChar().Traits.ProneToIllness.isSet()) {
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
			byte byte1;
			float float1;
			if (GameServer.bServer) {
				this.RestoreToFullHealth();
				byte1 = ((IsoPlayer)this.getParentChar()).bleedingLevel;
				if (byte1 > 0) {
					float1 = 1.0F / (float)byte1 * 200.0F * GameTime.instance.getInvMultiplier();
					if ((float)Rand.Next((int)float1) < float1 * 0.3F) {
						this.getParentChar().splatBloodFloor();
					}

					if (Rand.Next((int)float1) == 0) {
						this.getParentChar().splatBloodFloor();
					}
				}
			} else if (GameClient.bClient && this.getParentChar() instanceof IsoPlayer && ((IsoPlayer)this.getParentChar()).bRemote) {
				if (this.getParentChar().isAlive()) {
					this.RestoreToFullHealth();
					byte1 = ((IsoPlayer)this.getParentChar()).bleedingLevel;
					if (byte1 > 0) {
						float1 = 1.0F / (float)byte1 * 200.0F * GameTime.instance.getInvMultiplier();
						if ((float)Rand.Next((int)float1) < float1 * 0.3F) {
							this.getParentChar().splatBloodFloor();
						}

						if (Rand.Next((int)float1) == 0) {
							this.getParentChar().splatBloodFloor();
						}
					}
				}
			} else if (this.getParentChar().isGodMod()) {
				this.RestoreToFullHealth();
				((IsoPlayer)this.getParentChar()).bleedingLevel = 0;
			} else if (this.getParentChar().isInvincible()) {
				this.setOverallBodyHealth(100.0F);
				for (int int1 = 0; int1 < BodyPartType.MAX.index(); ++int1) {
					this.getBodyPart(BodyPartType.FromIndex(int1)).SetHealth(100.0F);
				}
			} else {
				float float2 = this.ParentChar.getStats().Pain;
				int int2 = this.getNumPartsBleeding() * 2;
				int2 += this.getNumPartsScratched();
				int2 += this.getNumPartsBitten() * 6;
				if (this.getHealth() >= 60.0F && int2 <= 3) {
					int2 = 0;
				}

				((IsoPlayer)this.getParentChar()).bleedingLevel = (byte)int2;
				float float3;
				if (int2 > 0) {
					float3 = 1.0F / (float)int2 * 200.0F * GameTime.instance.getInvMultiplier();
					if ((float)Rand.Next((int)float3) < float3 * 0.3F) {
						this.getParentChar().splatBloodFloor();
					}

					if (Rand.Next((int)float3) == 0) {
						this.getParentChar().splatBloodFloor();
					}
				}

				if (this.thermoregulator != null) {
					this.thermoregulator.update();
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

					int int3;
					if (!this.isInfected()) {
						for (int3 = 0; int3 < BodyPartType.ToIndex(BodyPartType.MAX); ++int3) {
							if (this.IsInfected(int3)) {
								this.setInfected(true);
								if (this.IsFakeInfected(int3)) {
									this.DisableFakeInfection(int3);
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
						for (int3 = 0; int3 < BodyPartType.ToIndex(BodyPartType.MAX); ++int3) {
							if (this.IsFakeInfected(int3)) {
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

					float3 = 0.0F;
					if (this.getHealthFromFoodTimer() > 0.0F) {
						float3 += this.getHealthFromFood() * GameTime.instance.getMultiplier();
						this.setHealthFromFoodTimer(this.getHealthFromFoodTimer() - 1.0F * GameTime.instance.getMultiplier());
					}

					byte byte2 = 0;
					if (this.getParentChar() == this.getParentChar() && (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 2 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 2 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 2)) {
						byte2 = 1;
					}

					if (this.getParentChar() == this.getParentChar() && (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 3 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 3 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 3)) {
						byte2 = 2;
					}

					if (this.getParentChar() == this.getParentChar() && (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4)) {
						byte2 = 3;
					}

					if (this.getParentChar().isAsleep()) {
						byte2 = -1;
					}

					switch (byte2) {
					case 0: 
						float3 += this.getStandardHealthAddition() * GameTime.instance.getMultiplier();
						break;
					
					case 1: 
						float3 += this.getReducedHealthAddition() * GameTime.instance.getMultiplier();
						break;
					
					case 2: 
						float3 += this.getSeverlyReducedHealthAddition() * GameTime.instance.getMultiplier();
						break;
					
					case 3: 
						float3 += 0.0F;
					
					}

					if (this.getParentChar().isAsleep()) {
						if (GameClient.bClient) {
							float3 += 15.0F * GameTime.instance.getGameWorldSecondsSinceLastUpdate() / 3600.0F;
						} else {
							float3 += this.getSleepingHealthAddition() * GameTime.instance.getMultiplier();
						}

						if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4) {
							float3 = 0.0F;
						}
					}

					this.AddGeneralHealth(float3);
					float3 = 0.0F;
					float float4 = 0.0F;
					float float5 = 0.0F;
					float float6 = 0.0F;
					float float7 = 0.0F;
					float float8 = 0.0F;
					float float9 = 0.0F;
					if (this.PoisonLevel > 0.0F) {
						if (this.PoisonLevel > 10.0F && this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) >= 1) {
							float4 = 0.0035F * Math.min(this.PoisonLevel / 10.0F, 3.0F) * GameTime.instance.getMultiplier();
							float3 += float4;
						}

						float float10 = 0.0F;
						if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.FoodEaten) > 0) {
							float10 = 1.5E-4F * (float)this.getParentChar().getMoodles().getMoodleLevel(MoodleType.FoodEaten);
						}

						this.PoisonLevel = (float)((double)this.PoisonLevel - ((double)float10 + ZomboidGlobals.PoisonLevelDecrease * (double)GameTime.instance.getMultiplier()));
						if (this.PoisonLevel < 0.0F) {
							this.PoisonLevel = 0.0F;
						}

						this.setFoodSicknessLevel(this.getFoodSicknessLevel() + this.getInfectionGrowthRate() * (float)(2 + Math.round(this.PoisonLevel / 10.0F)) * GameTime.instance.getMultiplier());
						if (this.getFoodSicknessLevel() > 100.0F) {
							this.setFoodSicknessLevel(100.0F);
						}
					}

					if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4) {
						float5 = this.getHealthReductionFromSevereBadMoodles() / 50.0F * GameTime.instance.getMultiplier();
						float3 += float5;
					}

					if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 4 && this.FoodSicknessLevel > this.InfectionLevel) {
						float6 = this.getHealthReductionFromSevereBadMoodles() * GameTime.instance.getMultiplier();
						float3 += float6;
					}

					if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bleeding) == 4) {
						float7 = this.getHealthReductionFromSevereBadMoodles() * GameTime.instance.getMultiplier();
						float3 += float7;
					}

					if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4) {
						float8 = this.getHealthReductionFromSevereBadMoodles() / 10.0F * GameTime.instance.getMultiplier();
						float3 += float8;
					}

					if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HeavyLoad) > 2 && this.getParentChar().getVehicle() == null && !this.getParentChar().isAsleep() && !this.getParentChar().isSitOnGround() && this.getThermoregulator().getMetabolicTarget() != Metabolics.SeatedResting.getMet() && this.getHealth() > 75.0F && Rand.Next(Rand.AdjustForFramerate(10)) == 0) {
						float9 = this.getHealthReductionFromSevereBadMoodles() / ((float)(5 - this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HeavyLoad)) / 10.0F) * GameTime.instance.getMultiplier();
						float3 += float9;
					}

					this.ReduceGeneralHealth(float3);
					IsoGameCharacter gameCharacter = this.getParentChar();
					if (float4 > 0.0F) {
						LuaEventManager.triggerEvent("OnPlayerGetDamage", gameCharacter, "POISON", float4);
					}

					if (float5 > 0.0F) {
						LuaEventManager.triggerEvent("OnPlayerGetDamage", gameCharacter, "HUNGRY", float5);
					}

					if (float6 > 0.0F) {
						LuaEventManager.triggerEvent("OnPlayerGetDamage", gameCharacter, "SICK", float6);
					}

					if (float7 > 0.0F) {
						LuaEventManager.triggerEvent("OnPlayerGetDamage", gameCharacter, "BLEEDING", float7);
					}

					if (float8 > 0.0F) {
						LuaEventManager.triggerEvent("OnPlayerGetDamage", gameCharacter, "THIRST", float8);
					}

					if (float9 > 0.0F) {
						LuaEventManager.triggerEvent("OnPlayerGetDamage", gameCharacter, "HEAVYLOAD", float9);
					}

					if (this.ParentChar.getPainEffect() > 0.0F) {
						stats = this.ParentChar.getStats();
						stats.Pain -= 0.023333333F * (GameTime.getInstance().getMultiplier() / 1.6F);
						this.ParentChar.setPainEffect(this.ParentChar.getPainEffect() - GameTime.getInstance().getMultiplier() / 1.6F);
					} else {
						this.ParentChar.setPainDelta(0.0F);
						float3 = 0.0F;
						for (int int4 = 0; int4 < BodyPartType.ToIndex(BodyPartType.MAX); ++int4) {
							float3 += ((BodyPart)this.getBodyParts().get(int4)).getPain() * BodyPartType.getPainModifyer(int4);
						}

						float3 -= this.getPainReduction();
						if (float3 > this.ParentChar.getStats().Pain) {
							stats = this.ParentChar.getStats();
							stats.Pain += (float3 - this.ParentChar.getStats().Pain) / 500.0F;
						} else {
							this.ParentChar.getStats().Pain = float3;
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
						int3 = SandboxOptions.instance.Lore.Mortality.getValue();
						if (int3 == 1) {
							this.ReduceGeneralHealth(110.0F);
							LuaEventManager.triggerEvent("OnPlayerGetDamage", this.ParentChar, "INFECTION", 110);
							this.setInfectionLevel(100.0F);
						} else if (int3 != 7) {
							float4 = this.getCurrentTimeForInfection();
							if (this.InfectionMortalityDuration < 0.0F) {
								this.InfectionMortalityDuration = this.pickMortalityDuration();
							}

							if (this.InfectionTime < 0.0F) {
								this.InfectionTime = float4;
							}

							if (this.InfectionTime > float4) {
								this.InfectionTime = float4;
							}

							float5 = (float4 - this.InfectionTime) / this.InfectionMortalityDuration;
							float5 = Math.min(float5, 1.0F);
							this.setInfectionLevel(float5 * 100.0F);
							if (float5 == 1.0F) {
								this.ReduceGeneralHealth(110.0F);
								LuaEventManager.triggerEvent("OnPlayerGetDamage", this.ParentChar, "INFECTION", 110);
							} else {
								float5 *= float5;
								float5 *= float5;
								float6 = (1.0F - float5) * 100.0F;
								float7 = this.getOverallBodyHealth() - float6;
								if (float7 > 0.0F && float6 <= 99.0F) {
									this.ReduceGeneralHealth(float7);
									LuaEventManager.triggerEvent("OnPlayerGetDamage", this.ParentChar, "INFECTION", float7);
								}
							}
						}
					}

					for (int3 = 0; int3 < BodyPartType.ToIndex(BodyPartType.MAX); ++int3) {
						((BodyPart)this.getBodyParts().get(int3)).DamageUpdate();
					}

					this.calculateOverallHealth();
					if (this.getOverallBodyHealth() <= 0.0F) {
						if (GameClient.bClient && this.getParentChar() instanceof IsoPlayer && !((IsoPlayer)this.getParentChar()).bRemote) {
							GameClient.sendPlayerDamage((IsoPlayer)this.getParentChar());
						}

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

					if (float2 == this.ParentChar.getStats().Pain) {
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

	private void calculateOverallHealth() {
		float float1 = 0.0F;
		for (int int1 = 0; int1 < BodyPartType.ToIndex(BodyPartType.MAX); ++int1) {
			BodyPart bodyPart = (BodyPart)this.getBodyParts().get(int1);
			float1 += (100.0F - bodyPart.getHealth()) * BodyPartType.getDamageModifyer(int1);
		}

		float1 += this.getDamageFromPills();
		if (float1 > 100.0F) {
			float1 = 100.0F;
		}

		this.setOverallBodyHealth(100.0F - float1);
	}

	public static float getSicknessFromCorpsesRate(int int1) {
		if (SandboxOptions.instance.DecayingCorpseHealthImpact.getValue() == 1) {
			return 0.0F;
		} else if (int1 > 5) {
			float float1 = (float)ZomboidGlobals.FoodSicknessDecrease * 0.07F;
			switch (SandboxOptions.instance.DecayingCorpseHealthImpact.getValue()) {
			case 2: 
				float1 = (float)ZomboidGlobals.FoodSicknessDecrease * 0.01F;
				break;
			
			case 4: 
				float1 = (float)ZomboidGlobals.FoodSicknessDecrease * 0.11F;
			
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
		if (this.getParentChar() instanceof IsoPlayer) {
			if (this.ColdDamageStage > 0.0F) {
				float float2 = 100.0F - this.ColdDamageStage * 100.0F;
				if (this.OverallBodyHealth > float2) {
					this.ReduceGeneralHealth(this.OverallBodyHealth - float2);
				}
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

	public ArrayList getBodyParts() {
		return this.BodyParts;
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

	public float getPanicIncreaseValueFrame() {
		return this.PanicIncreaseValueFrame;
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
		float float2 = 0.0F;
		if (this.BodyParts.size() > 0) {
			for (int int1 = 0; int1 < this.BodyParts.size(); ++int1) {
				BodyPart bodyPart = (BodyPart)this.BodyParts.get(int1);
				bodyPart.setWetness(float1);
				float2 += bodyPart.getWetness();
			}

			float2 /= (float)this.BodyParts.size();
		}

		this.Wetness = PZMath.clamp(float2, 0.0F, 100.0F);
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

	public Thermoregulator getThermoregulator() {
		return this.thermoregulator;
	}

	public void decreaseBodyWetness(float float1) {
		float float2 = 0.0F;
		if (this.BodyParts.size() > 0) {
			for (int int1 = 0; int1 < this.BodyParts.size(); ++int1) {
				BodyPart bodyPart = (BodyPart)this.BodyParts.get(int1);
				bodyPart.setWetness(bodyPart.getWetness() - float1);
				float2 += bodyPart.getWetness();
			}

			float2 /= (float)this.BodyParts.size();
		}

		this.Wetness = PZMath.clamp(float2, 0.0F, 100.0F);
	}

	public void increaseBodyWetness(float float1) {
		float float2 = 0.0F;
		if (this.BodyParts.size() > 0) {
			for (int int1 = 0; int1 < this.BodyParts.size(); ++int1) {
				BodyPart bodyPart = (BodyPart)this.BodyParts.get(int1);
				bodyPart.setWetness(bodyPart.getWetness() + float1);
				float2 += bodyPart.getWetness();
			}

			float2 /= (float)this.BodyParts.size();
		}

		this.Wetness = PZMath.clamp(float2, 0.0F, 100.0F);
	}
}
