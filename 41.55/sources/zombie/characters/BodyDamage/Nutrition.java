package zombie.characters.BodyDamage;

import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.SwipeStatePlayer;
import zombie.characters.IsoPlayer;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.characters.skills.PerkFactory;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class Nutrition {
	private IsoPlayer parent;
	private float carbohydrates = 0.0F;
	private float lipids = 0.0F;
	private float proteins = 0.0F;
	private float calories = 0.0F;
	private float carbohydratesDecreraseFemale = 0.0032F;
	private float carbohydratesDecreraseMale = 0.0035F;
	private float lipidsDecreraseFemale = 7.0E-4F;
	private float lipidsDecreraseMale = 0.00113F;
	private float proteinsDecreraseFemale = 7.0E-4F;
	private float proteinsDecreraseMale = 8.6E-4F;
	private float caloriesDecreraseFemaleNormal = 1.0E-5F;
	private float caloriesDecreaseMaleNormal = 0.016F;
	private float caloriesDecreraseFemaleExercise = 1.0E-5F;
	private float caloriesDecreaseMaleExercise = 0.13F;
	private float caloriesDecreraseFemaleSleeping = 0.01F;
	private float caloriesDecreaseMaleSleeping = 0.003F;
	private int caloriesToGainWeightMale = 1100;
	private int caloriesToGainWeightMaxMale = 4000;
	private int caloriesToGainWeightFemale = 1000;
	private int caloriesToGainWeightMaxFemale = 3000;
	private int caloriesDecreaseMax = 2500;
	private float weightGain = 1.3E-5F;
	private float weightLoss = 8.5E-6F;
	private float weight = 60.0F;
	private int updatedWeight = 0;
	private boolean isFemale = false;
	private int syncWeightTimer = 0;
	private float caloriesMax = 0.0F;
	private float caloriesMin = 0.0F;
	private boolean incWeight = false;
	private boolean incWeightLot = false;
	private boolean decWeight = false;

	public Nutrition(IsoPlayer player) {
		this.parent = player;
		if (this.isFemale) {
			this.setWeight(60.0F);
		} else {
			this.setWeight(80.0F);
		}

		this.setCalories(800.0F);
	}

	public void update() {
		if (!GameServer.bServer) {
			if (SandboxOptions.instance.Nutrition.getValue()) {
				if (this.parent != null && !this.parent.isDead()) {
					if (!GameClient.bClient || this.parent.isLocalPlayer()) {
						this.setCarbohydrates(this.getCarbohydrates() - (this.isFemale ? this.carbohydratesDecreraseFemale : this.carbohydratesDecreraseMale) * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
						this.setLipids(this.getLipids() - (this.isFemale ? this.lipidsDecreraseFemale : this.lipidsDecreraseMale) * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
						this.setProteins(this.getProteins() - (this.isFemale ? this.proteinsDecreraseFemale : this.proteinsDecreraseMale) * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
						this.updateCalories();
						this.updateWeight();
					}
				}
			}
		}
	}

	private void updateCalories() {
		float float1 = 1.0F;
		if (!this.parent.getCharacterActions().isEmpty()) {
			float1 = ((BaseAction)this.parent.getCharacterActions().get(0)).caloriesModifier;
		}

		if (this.parent.isCurrentState(SwipeStatePlayer.instance()) || this.parent.isCurrentState(ClimbOverFenceState.instance()) || this.parent.isCurrentState(ClimbThroughWindowState.instance())) {
			float1 = 8.0F;
		}

		float float2 = 1.0F;
		if (this.parent.getBodyDamage() != null && this.parent.getBodyDamage().getThermoregulator() != null) {
			float2 = (float)this.parent.getBodyDamage().getThermoregulator().getEnergyMultiplier();
		}

		float float3 = this.getWeight() / 80.0F;
		if (this.parent.IsRunning()) {
			float1 = 1.0F;
			this.setCalories(this.getCalories() - (this.isFemale ? this.caloriesDecreraseFemaleExercise : this.caloriesDecreaseMaleExercise) * float1 * float3 * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
		} else if (this.parent.isAsleep()) {
			this.setCalories(this.getCalories() - (this.isFemale ? this.caloriesDecreraseFemaleSleeping : this.caloriesDecreaseMaleSleeping) * float1 * float2 * float3 * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
		} else {
			this.setCalories(this.getCalories() - (this.isFemale ? this.caloriesDecreraseFemaleNormal : this.caloriesDecreaseMaleNormal) * float1 * float2 * float3 * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
		}

		if (this.getCalories() > this.caloriesMax) {
			this.caloriesMax = this.getCalories();
		}

		if (this.getCalories() < this.caloriesMin) {
			this.caloriesMin = this.getCalories();
		}
	}

	private void updateWeight() {
		if (this.parent.isGodMod()) {
			if (this.isFemale) {
				this.setWeight(60.0F);
			} else {
				this.setWeight(80.0F);
			}

			this.setCalories(1000.0F);
		}

		this.setIncWeight(false);
		this.setIncWeightLot(false);
		this.setDecWeight(false);
		float float1 = (float)this.caloriesToGainWeightMale;
		float float2 = (float)this.caloriesToGainWeightMaxMale;
		float float3 = 0.0F;
		if (this.isFemale) {
			float1 = (float)this.caloriesToGainWeightFemale;
			float2 = (float)this.caloriesToGainWeightMaxFemale;
		}

		float float4 = (this.getWeight() - 80.0F) * 40.0F;
		float1 = 1600.0F + float4;
		float3 = (this.getWeight() - 70.0F) * 30.0F;
		if (float3 > 0.0F) {
			float3 = 0.0F;
		}

		float float5;
		if (this.getCalories() > float1) {
			this.setIncWeight(true);
			float5 = this.getCalories() / float2;
			if (float5 > 1.0F) {
				float5 = 1.0F;
			}

			float float6 = this.weightGain;
			if (!(this.getCarbohydrates() > 700.0F) && !(this.getLipids() > 700.0F)) {
				if (this.getCarbohydrates() > 400.0F || this.getLipids() > 400.0F) {
					float6 *= 2.0F;
					this.setIncWeightLot(true);
				}
			} else {
				float6 *= 3.0F;
				this.setIncWeightLot(true);
			}

			this.setWeight(this.getWeight() + float6 * float5 * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
		} else if (this.getCalories() < float3) {
			this.setDecWeight(true);
			float5 = Math.abs(this.getCalories()) / (float)this.caloriesDecreaseMax;
			if (float5 > 1.0F) {
				float5 = 1.0F;
			}

			this.setWeight(this.getWeight() - this.weightLoss * float5 * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
		}

		++this.updatedWeight;
		if (this.updatedWeight >= 2000) {
			this.applyTraitFromWeight();
			this.updatedWeight = 0;
		}

		if (GameClient.bClient) {
			++this.syncWeightTimer;
			if (this.syncWeightTimer >= 5000) {
				GameClient.sendWeight(this.parent);
				this.syncWeightTimer = 0;
			}
		}
	}

	public void save(ByteBuffer byteBuffer) {
		byteBuffer.putFloat(this.getCalories());
		byteBuffer.putFloat(this.getProteins());
		byteBuffer.putFloat(this.getLipids());
		byteBuffer.putFloat(this.getCarbohydrates());
		byteBuffer.putFloat(this.getWeight());
	}

	public void load(ByteBuffer byteBuffer) {
		this.setCalories(byteBuffer.getFloat());
		this.setProteins(byteBuffer.getFloat());
		this.setLipids(byteBuffer.getFloat());
		this.setCarbohydrates(byteBuffer.getFloat());
		this.setWeight(byteBuffer.getFloat());
	}

	public void applyWeightFromTraits() {
		if (this.parent.getTraits() != null && !this.parent.getTraits().isEmpty()) {
			if (this.parent.Traits.Emaciated.isSet()) {
				this.setWeight(50.0F);
			}

			if (this.parent.Traits.VeryUnderweight.isSet()) {
				this.setWeight(60.0F);
			}

			if (this.parent.Traits.Underweight.isSet()) {
				this.setWeight(70.0F);
			}

			if (this.parent.Traits.Overweight.isSet()) {
				this.setWeight(95.0F);
			}

			if (this.parent.Traits.Obese.isSet()) {
				this.setWeight(105.0F);
			}
		}
	}

	public void applyTraitFromWeight() {
		this.parent.getTraits().remove("Underweight");
		this.parent.getTraits().remove("Very Underweight");
		this.parent.getTraits().remove("Emaciated");
		this.parent.getTraits().remove("Overweight");
		this.parent.getTraits().remove("Obese");
		if (this.getWeight() >= 100.0F) {
			this.parent.getTraits().add("Obese");
		}

		if (this.getWeight() >= 85.0F && this.getWeight() < 100.0F) {
			this.parent.getTraits().add("Overweight");
		}

		if (this.getWeight() > 65.0F && this.getWeight() <= 75.0F) {
			this.parent.getTraits().add("Underweight");
		}

		if (this.getWeight() > 50.0F && this.getWeight() <= 65.0F) {
			this.parent.getTraits().add("Very Underweight");
		}

		if (this.getWeight() <= 50.0F) {
			this.parent.getTraits().add("Emaciated");
		}
	}

	public boolean characterHaveWeightTrouble() {
		return this.parent.Traits.Emaciated.isSet() || this.parent.Traits.Obese.isSet() || this.parent.Traits.VeryUnderweight.isSet() || this.parent.Traits.Underweight.isSet() || this.parent.Traits.Overweight.isSet();
	}

	public boolean canAddFitnessXp() {
		if (this.parent.getPerkLevel(PerkFactory.Perks.Fitness) >= 9 && this.characterHaveWeightTrouble()) {
			return false;
		} else if (this.parent.getPerkLevel(PerkFactory.Perks.Fitness) < 6) {
			return true;
		} else {
			return !this.parent.Traits.Emaciated.isSet() && !this.parent.Traits.Obese.isSet() && !this.parent.Traits.VeryUnderweight.isSet();
		}
	}

	public float getCarbohydrates() {
		return this.carbohydrates;
	}

	public void setCarbohydrates(float float1) {
		if (float1 < -500.0F) {
			float1 = -500.0F;
		}

		if (float1 > 1000.0F) {
			float1 = 1000.0F;
		}

		this.carbohydrates = float1;
	}

	public float getProteins() {
		return this.proteins;
	}

	public void setProteins(float float1) {
		if (float1 < -500.0F) {
			float1 = -500.0F;
		}

		if (float1 > 1000.0F) {
			float1 = 1000.0F;
		}

		this.proteins = float1;
	}

	public float getCalories() {
		return this.calories;
	}

	public void setCalories(float float1) {
		if (float1 < -2200.0F) {
			float1 = -2200.0F;
		}

		if (float1 > 3700.0F) {
			float1 = 3700.0F;
		}

		this.calories = float1;
	}

	public float getLipids() {
		return this.lipids;
	}

	public void setLipids(float float1) {
		if (float1 < -500.0F) {
			float1 = -500.0F;
		}

		if (float1 > 1000.0F) {
			float1 = 1000.0F;
		}

		this.lipids = float1;
	}

	public float getWeight() {
		return this.weight;
	}

	public void setWeight(float float1) {
		if (float1 < 35.0F) {
			float1 = 35.0F;
			this.parent.getBodyDamage().ReduceGeneralHealth(this.parent.getBodyDamage().getHealthReductionFromSevereBadMoodles() * GameTime.instance.getMultiplier());
		}

		this.weight = float1;
	}

	public boolean isIncWeight() {
		return this.incWeight;
	}

	public void setIncWeight(boolean boolean1) {
		this.incWeight = boolean1;
	}

	public boolean isIncWeightLot() {
		return this.incWeightLot;
	}

	public void setIncWeightLot(boolean boolean1) {
		this.incWeightLot = boolean1;
	}

	public boolean isDecWeight() {
		return this.decWeight;
	}

	public void setDecWeight(boolean boolean1) {
		this.decWeight = boolean1;
	}
}
