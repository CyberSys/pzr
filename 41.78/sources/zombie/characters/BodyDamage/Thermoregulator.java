package zombie.characters.BodyDamage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameTime;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.BloodClothingType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.Stats;
import zombie.characters.Moodles.MoodleType;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.WeaponType;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.Temperature;


public final class Thermoregulator {
	private static final boolean DISABLE_ENERGY_MULTIPLIER = false;
	private final BodyDamage bodyDamage;
	private final IsoGameCharacter character;
	private final IsoPlayer player;
	private final Stats stats;
	private final Nutrition nutrition;
	private final ClimateManager climate;
	private static final ItemVisuals itemVisuals = new ItemVisuals();
	private static final ItemVisuals itemVisualsCache = new ItemVisuals();
	private static final ArrayList coveredParts = new ArrayList();
	private static float SIMULATION_MULTIPLIER = 1.0F;
	private float setPoint = 37.0F;
	private float metabolicRate;
	private float metabolicRateReal;
	private float metabolicTarget;
	private double fluidsMultiplier;
	private double energyMultiplier;
	private double fatigueMultiplier;
	private float bodyHeatDelta;
	private float coreHeatDelta;
	private boolean thermalChevronUp;
	private Thermoregulator.ThermalNode core;
	private Thermoregulator.ThermalNode[] nodes;
	private float totalHeatRaw;
	private float totalHeat;
	private float primTotal;
	private float secTotal;
	private float externalAirTemperature;
	private float airTemperature;
	private float airAndWindTemp;
	private float rateOfChangeCounter;
	private float coreCelciusCache;
	private float coreRateOfChange;
	private float thermalDamage;
	private float damageCounter;

	public Thermoregulator(BodyDamage bodyDamage) {
		this.metabolicRate = Metabolics.Default.getMet();
		this.metabolicRateReal = this.metabolicRate;
		this.metabolicTarget = Metabolics.Default.getMet();
		this.fluidsMultiplier = 1.0;
		this.energyMultiplier = 1.0;
		this.fatigueMultiplier = 1.0;
		this.bodyHeatDelta = 0.0F;
		this.coreHeatDelta = 0.0F;
		this.thermalChevronUp = true;
		this.totalHeatRaw = 0.0F;
		this.totalHeat = 0.0F;
		this.primTotal = 0.0F;
		this.secTotal = 0.0F;
		this.externalAirTemperature = 27.0F;
		this.rateOfChangeCounter = 0.0F;
		this.coreCelciusCache = 37.0F;
		this.coreRateOfChange = 0.0F;
		this.thermalDamage = 0.0F;
		this.damageCounter = 0.0F;
		this.bodyDamage = bodyDamage;
		this.character = bodyDamage.getParentChar();
		this.stats = this.character.getStats();
		if (this.character instanceof IsoPlayer) {
			this.player = (IsoPlayer)this.character;
			this.nutrition = ((IsoPlayer)this.character).getNutrition();
		} else {
			this.player = null;
			this.nutrition = null;
		}

		this.climate = ClimateManager.getInstance();
		this.initNodes();
	}

	public static void setSimulationMultiplier(float float1) {
		SIMULATION_MULTIPLIER = float1;
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.putFloat(this.setPoint);
		byteBuffer.putFloat(this.metabolicRate);
		byteBuffer.putFloat(this.metabolicTarget);
		byteBuffer.putFloat(this.bodyHeatDelta);
		byteBuffer.putFloat(this.coreHeatDelta);
		byteBuffer.putFloat(this.thermalDamage);
		byteBuffer.putFloat(this.damageCounter);
		byteBuffer.putInt(this.nodes.length);
		for (int int1 = 0; int1 < this.nodes.length; ++int1) {
			Thermoregulator.ThermalNode thermalNode = this.nodes[int1];
			byteBuffer.putInt(BodyPartType.ToIndex(thermalNode.bodyPartType));
			byteBuffer.putFloat(thermalNode.celcius);
			byteBuffer.putFloat(thermalNode.skinCelcius);
			byteBuffer.putFloat(thermalNode.heatDelta);
			byteBuffer.putFloat(thermalNode.primaryDelta);
			byteBuffer.putFloat(thermalNode.secondaryDelta);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		this.setPoint = byteBuffer.getFloat();
		this.metabolicRate = byteBuffer.getFloat();
		this.metabolicTarget = byteBuffer.getFloat();
		this.bodyHeatDelta = byteBuffer.getFloat();
		this.coreHeatDelta = byteBuffer.getFloat();
		this.thermalDamage = byteBuffer.getFloat();
		this.damageCounter = byteBuffer.getFloat();
		int int2 = byteBuffer.getInt();
		for (int int3 = 0; int3 < int2; ++int3) {
			int int4 = byteBuffer.getInt();
			float float1 = byteBuffer.getFloat();
			float float2 = byteBuffer.getFloat();
			float float3 = byteBuffer.getFloat();
			float float4 = byteBuffer.getFloat();
			float float5 = byteBuffer.getFloat();
			Thermoregulator.ThermalNode thermalNode = this.getNodeForType(BodyPartType.FromIndex(int4));
			if (thermalNode != null) {
				thermalNode.celcius = float1;
				thermalNode.skinCelcius = float2;
				thermalNode.heatDelta = float3;
				thermalNode.primaryDelta = float4;
				thermalNode.secondaryDelta = float5;
			} else {
				DebugLog.log("Couldnt load node: " + BodyPartType.ToString(BodyPartType.FromIndex(int4)));
			}
		}
	}

	public void reset() {
		this.setPoint = 37.0F;
		this.metabolicRate = Metabolics.Default.getMet();
		this.metabolicTarget = this.metabolicRate;
		this.core.celcius = 37.0F;
		this.bodyHeatDelta = 0.0F;
		this.coreHeatDelta = 0.0F;
		this.thermalDamage = 0.0F;
		for (int int1 = 0; int1 < this.nodes.length; ++int1) {
			Thermoregulator.ThermalNode thermalNode = this.nodes[int1];
			if (thermalNode != this.core) {
				thermalNode.celcius = 35.0F;
			}

			thermalNode.primaryDelta = 0.0F;
			thermalNode.secondaryDelta = 0.0F;
			thermalNode.skinCelcius = 33.0F;
			thermalNode.heatDelta = 0.0F;
		}
	}

	private void initNodes() {
		ArrayList arrayList = new ArrayList();
		int int1;
		for (int1 = 0; int1 < this.bodyDamage.getBodyParts().size(); ++int1) {
			BodyPart bodyPart = (BodyPart)this.bodyDamage.getBodyParts().get(int1);
			Thermoregulator.ThermalNode thermalNode = null;
			switch (bodyPart.getType()) {
			case Torso_Upper: 
				thermalNode = new Thermoregulator.ThermalNode(true, 37.0F, bodyPart, 0.25F);
				this.core = thermalNode;
				break;
			
			case Head: 
				thermalNode = new Thermoregulator.ThermalNode(37.0F, bodyPart, 1.0F);
				break;
			
			case Neck: 
				thermalNode = new Thermoregulator.ThermalNode(37.0F, bodyPart, 0.5F);
				break;
			
			case Torso_Lower: 
				thermalNode = new Thermoregulator.ThermalNode(37.0F, bodyPart, 0.25F);
				break;
			
			case Groin: 
				thermalNode = new Thermoregulator.ThermalNode(37.0F, bodyPart, 0.5F);
				break;
			
			case UpperLeg_L: 
			
			case UpperLeg_R: 
				thermalNode = new Thermoregulator.ThermalNode(37.0F, bodyPart, 0.5F);
				break;
			
			case LowerLeg_L: 
			
			case LowerLeg_R: 
				thermalNode = new Thermoregulator.ThermalNode(37.0F, bodyPart, 0.5F);
				break;
			
			case Foot_L: 
			
			case Foot_R: 
				thermalNode = new Thermoregulator.ThermalNode(37.0F, bodyPart, 0.5F);
				break;
			
			case UpperArm_L: 
			
			case UpperArm_R: 
				thermalNode = new Thermoregulator.ThermalNode(37.0F, bodyPart, 0.25F);
				break;
			
			case ForeArm_L: 
			
			case ForeArm_R: 
				thermalNode = new Thermoregulator.ThermalNode(37.0F, bodyPart, 0.25F);
				break;
			
			case Hand_L: 
			
			case Hand_R: 
				thermalNode = new Thermoregulator.ThermalNode(37.0F, bodyPart, 1.0F);
				break;
			
			default: 
				BodyPart bodyPart2 = (BodyPart)this.bodyDamage.getBodyParts().get(int1);
				DebugLog.log("Warning: couldnt init thermal node for body part \'" + bodyPart2.getType() + "\'.");
			
			}

			if (thermalNode != null) {
				bodyPart.thermalNode = thermalNode;
				arrayList.add(thermalNode);
			}
		}

		this.nodes = new Thermoregulator.ThermalNode[arrayList.size()];
		arrayList.toArray(this.nodes);
		for (int1 = 0; int1 < this.nodes.length; ++int1) {
			Thermoregulator.ThermalNode thermalNode2 = this.nodes[int1];
			BodyPartType bodyPartType = BodyPartContacts.getParent(thermalNode2.bodyPartType);
			if (bodyPartType != null) {
				thermalNode2.upstream = this.getNodeForType(bodyPartType);
			}

			BodyPartType[] bodyPartTypeArray = BodyPartContacts.getChildren(thermalNode2.bodyPartType);
			if (bodyPartTypeArray != null && bodyPartTypeArray.length > 0) {
				thermalNode2.downstream = new Thermoregulator.ThermalNode[bodyPartTypeArray.length];
				for (int int2 = 0; int2 < bodyPartTypeArray.length; ++int2) {
					thermalNode2.downstream[int2] = this.getNodeForType(bodyPartTypeArray[int2]);
				}
			}
		}

		this.core.celcius = this.setPoint;
	}

	public int getNodeSize() {
		return this.nodes.length;
	}

	public Thermoregulator.ThermalNode getNode(int int1) {
		return this.nodes[int1];
	}

	public Thermoregulator.ThermalNode getNodeForType(BodyPartType bodyPartType) {
		for (int int1 = 0; int1 < this.nodes.length; ++int1) {
			if (this.nodes[int1].bodyPartType == bodyPartType) {
				return this.nodes[int1];
			}
		}

		return null;
	}

	public Thermoregulator.ThermalNode getNodeForBloodType(BloodBodyPartType bloodBodyPartType) {
		for (int int1 = 0; int1 < this.nodes.length; ++int1) {
			if (this.nodes[int1].bloodBPT == bloodBodyPartType) {
				return this.nodes[int1];
			}
		}

		return null;
	}

	public float getBodyHeatDelta() {
		return this.bodyHeatDelta;
	}

	public double getFluidsMultiplier() {
		return this.fluidsMultiplier;
	}

	public double getEnergyMultiplier() {
		return this.energyMultiplier;
	}

	public double getFatigueMultiplier() {
		return this.fatigueMultiplier;
	}

	public float getMovementModifier() {
		float float1 = 1.0F;
		if (this.player != null) {
			int int1 = this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia);
			if (int1 == 2) {
				float1 = 0.66F;
			} else if (int1 == 3) {
				float1 = 0.33F;
			} else if (int1 == 4) {
				float1 = 0.0F;
			}

			int1 = this.player.getMoodles().getMoodleLevel(MoodleType.Hyperthermia);
			if (int1 == 2) {
				float1 = 0.66F;
			} else if (int1 == 3) {
				float1 = 0.33F;
			} else if (int1 == 4) {
				float1 = 0.0F;
			}
		}

		return float1;
	}

	public float getCombatModifier() {
		float float1 = 1.0F;
		if (this.player != null) {
			int int1 = this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia);
			if (int1 == 2) {
				float1 = 0.66F;
			} else if (int1 == 3) {
				float1 = 0.33F;
			} else if (int1 == 4) {
				float1 = 0.1F;
			}

			int1 = this.player.getMoodles().getMoodleLevel(MoodleType.Hyperthermia);
			if (int1 == 2) {
				float1 = 0.66F;
			} else if (int1 == 3) {
				float1 = 0.33F;
			} else if (int1 == 4) {
				float1 = 0.1F;
			}
		}

		return float1;
	}

	public float getCoreTemperature() {
		return this.core.celcius;
	}

	public float getHeatGeneration() {
		return this.metabolicRateReal;
	}

	public float getMetabolicRate() {
		return this.metabolicRate;
	}

	public float getMetabolicTarget() {
		return this.metabolicTarget;
	}

	public float getMetabolicRateReal() {
		return this.metabolicRateReal;
	}

	public float getSetPoint() {
		return this.setPoint;
	}

	public float getCoreHeatDelta() {
		return this.coreHeatDelta;
	}

	public float getCoreRateOfChange() {
		return this.coreRateOfChange;
	}

	public float getExternalAirTemperature() {
		return this.externalAirTemperature;
	}

	public float getCoreTemperatureUI() {
		float float1 = PZMath.clamp(this.core.celcius, 20.0F, 42.0F);
		if (float1 < 37.0F) {
			float1 = (float1 - 20.0F) / 17.0F * 0.5F;
		} else {
			float1 = 0.5F + (float1 - 37.0F) / 5.0F * 0.5F;
		}

		return float1;
	}

	public float getHeatGenerationUI() {
		float float1 = PZMath.clamp(this.metabolicRateReal, 0.0F, Metabolics.MAX.getMet());
		if (float1 < Metabolics.Default.getMet()) {
			float1 = float1 / Metabolics.Default.getMet() * 0.5F;
		} else {
			float1 = 0.5F + (float1 - Metabolics.Default.getMet()) / (Metabolics.MAX.getMet() - Metabolics.Default.getMet()) * 0.5F;
		}

		return float1;
	}

	public boolean thermalChevronUp() {
		return this.thermalChevronUp;
	}

	public int thermalChevronCount() {
		if (this.coreRateOfChange > 0.01F) {
			return 3;
		} else if (this.coreRateOfChange > 0.001F) {
			return 2;
		} else {
			return this.coreRateOfChange > 1.0E-4F ? 1 : 0;
		}
	}

	public float getCatchAColdDelta() {
		float float1 = 0.0F;
		if (this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia) < 1) {
			return float1;
		} else {
			for (int int1 = 0; int1 < this.nodes.length; ++int1) {
				Thermoregulator.ThermalNode thermalNode = this.nodes[int1];
				float float2 = 0.0F;
				if (thermalNode.skinCelcius < 33.0F) {
					float2 = (thermalNode.skinCelcius - 20.0F) / 13.0F;
					float2 = 1.0F - float2;
					float2 *= float2;
				}

				float float3 = 0.25F * float2 * thermalNode.skinSurface;
				if (thermalNode.bodyWetness > 0.0F) {
					float3 *= 1.0F + thermalNode.bodyWetness * 1.0F;
				}

				if (thermalNode.clothingWetness > 0.5F) {
					float3 *= 1.0F + (thermalNode.clothingWetness - 0.5F) * 2.0F;
				}

				if (thermalNode.bodyPartType == BodyPartType.Neck) {
					float3 *= 8.0F;
				} else if (thermalNode.bodyPartType == BodyPartType.Torso_Upper) {
					float3 *= 16.0F;
				} else if (thermalNode.bodyPartType == BodyPartType.Head) {
					float3 *= 4.0F;
				}

				float1 += float3;
			}

			if (this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia) > 1) {
				float1 *= (float)this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia);
			}

			return float1;
		}
	}

	public float getTimedActionTimeModifier() {
		float float1 = 1.0F;
		for (int int1 = 0; int1 < this.nodes.length; ++int1) {
			Thermoregulator.ThermalNode thermalNode = this.nodes[int1];
			float float2 = 0.0F;
			if (thermalNode.skinCelcius < 33.0F) {
				float2 = (thermalNode.skinCelcius - 20.0F) / 13.0F;
				float2 = 1.0F - float2;
				float2 *= float2;
			}

			float float3 = 0.25F * float2 * thermalNode.skinSurface;
			if (thermalNode.bodyPartType != BodyPartType.Hand_R && thermalNode.bodyPartType != BodyPartType.Hand_L) {
				if (thermalNode.bodyPartType != BodyPartType.ForeArm_R && thermalNode.bodyPartType != BodyPartType.ForeArm_L) {
					if (thermalNode.bodyPartType == BodyPartType.UpperArm_R || thermalNode.bodyPartType == BodyPartType.UpperArm_L) {
						float1 += 0.1F * float3;
					}
				} else {
					float1 += 0.15F * float3;
				}
			} else {
				float1 += 0.3F * float3;
			}
		}

		return float1;
	}

	public static float getSkinCelciusMin() {
		return 20.0F;
	}

	public static float getSkinCelciusFavorable() {
		return 33.0F;
	}

	public static float getSkinCelciusMax() {
		return 42.0F;
	}

	public void setMetabolicTarget(Metabolics metabolics) {
		this.setMetabolicTarget(metabolics.getMet());
	}

	public void setMetabolicTarget(float float1) {
		if (!(float1 < 0.0F) && !(float1 < this.metabolicTarget)) {
			this.metabolicTarget = float1;
			if (this.metabolicTarget > Metabolics.MAX.getMet()) {
				this.metabolicTarget = Metabolics.MAX.getMet();
			}
		}
	}

	private void updateCoreRateOfChange() {
		this.rateOfChangeCounter += GameTime.instance.getMultiplier();
		if (this.rateOfChangeCounter > 100.0F) {
			this.rateOfChangeCounter = 0.0F;
			this.coreRateOfChange = this.core.celcius - this.coreCelciusCache;
			this.thermalChevronUp = this.coreRateOfChange >= 0.0F;
			this.coreRateOfChange = PZMath.abs(this.coreRateOfChange);
			this.coreCelciusCache = this.core.celcius;
		}
	}

	public float getSimulationMultiplier() {
		return SIMULATION_MULTIPLIER;
	}

	public float getDefaultMultiplier() {
		return this.getSimulationMultiplier(Thermoregulator.Multiplier.Default);
	}

	public float getMetabolicRateIncMultiplier() {
		return this.getSimulationMultiplier(Thermoregulator.Multiplier.MetabolicRateInc);
	}

	public float getMetabolicRateDecMultiplier() {
		return this.getSimulationMultiplier(Thermoregulator.Multiplier.MetabolicRateDec);
	}

	public float getBodyHeatMultiplier() {
		return this.getSimulationMultiplier(Thermoregulator.Multiplier.BodyHeat);
	}

	public float getCoreHeatExpandMultiplier() {
		return this.getSimulationMultiplier(Thermoregulator.Multiplier.CoreHeatExpand);
	}

	public float getCoreHeatContractMultiplier() {
		return this.getSimulationMultiplier(Thermoregulator.Multiplier.CoreHeatContract);
	}

	public float getSkinCelciusMultiplier() {
		return this.getSimulationMultiplier(Thermoregulator.Multiplier.SkinCelcius);
	}

	public float getTemperatureAir() {
		return this.climate.getAirTemperatureForCharacter(this.character, false);
	}

	public float getTemperatureAirAndWind() {
		return this.climate.getAirTemperatureForCharacter(this.character, true);
	}

	public float getDbg_totalHeatRaw() {
		return this.totalHeatRaw;
	}

	public float getDbg_totalHeat() {
		return this.totalHeat;
	}

	public float getCoreCelcius() {
		return this.core != null ? this.core.celcius : 0.0F;
	}

	public float getDbg_primTotal() {
		return this.primTotal;
	}

	public float getDbg_secTotal() {
		return this.secTotal;
	}

	private float getSimulationMultiplier(Thermoregulator.Multiplier multiplier) {
		float float1 = GameTime.instance.getMultiplier();
		switch (multiplier) {
		case MetabolicRateInc: 
			float1 *= 0.001F;
			break;
		
		case MetabolicRateDec: 
			float1 *= 4.0E-4F;
			break;
		
		case BodyHeat: 
			float1 *= 2.5E-4F;
			break;
		
		case CoreHeatExpand: 
			float1 *= 5.0E-5F;
			break;
		
		case CoreHeatContract: 
			float1 *= 5.0E-4F;
			break;
		
		case SkinCelcius: 
		
		case SkinCelciusExpand: 
			float1 *= 0.0025F;
			break;
		
		case SkinCelciusContract: 
			float1 *= 0.005F;
			break;
		
		case PrimaryDelta: 
			float1 *= 5.0E-4F;
			break;
		
		case SecondaryDelta: 
			float1 *= 2.5E-4F;
		
		case Default: 
		
		}
		return float1 * SIMULATION_MULTIPLIER;
	}

	public float getThermalDamage() {
		return this.thermalDamage;
	}

	private void updateThermalDamage(float float1) {
		this.damageCounter += GameTime.instance.getRealworldSecondsSinceLastUpdate();
		if (this.damageCounter > 1.0F) {
			this.damageCounter = 0.0F;
			float float2;
			float float3;
			if (this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia) == 4 && float1 < 0.0F && this.core.celcius - this.coreCelciusCache <= 0.0F) {
				float2 = (this.core.celcius - 20.0F) / 5.0F;
				float2 = 1.0F - float2;
				float3 = 120.0F;
				float3 += 480.0F * float2;
				this.thermalDamage += 1.0F / float3 * PZMath.clamp_01(PZMath.abs(float1) / 10.0F);
			} else if (this.player.getMoodles().getMoodleLevel(MoodleType.Hyperthermia) == 4 && float1 > 37.0F && this.core.celcius - this.coreCelciusCache >= 0.0F) {
				float2 = (this.core.celcius - 41.0F) / 1.0F;
				float3 = 120.0F;
				float3 += 480.0F * float2;
				this.thermalDamage += 1.0F / float3 * PZMath.clamp_01((float1 - 37.0F) / 8.0F);
				this.thermalDamage = Math.min(this.thermalDamage, 0.3F);
			} else {
				this.thermalDamage -= 0.011111111F;
			}

			this.thermalDamage = PZMath.clamp_01(this.thermalDamage);
		}

		this.player.getBodyDamage().ColdDamageStage = this.thermalDamage;
	}

	public void update() {
		this.airTemperature = this.climate.getAirTemperatureForCharacter(this.character, false);
		this.airAndWindTemp = this.climate.getAirTemperatureForCharacter(this.character, true);
		this.externalAirTemperature = this.airTemperature;
		this.updateSetPoint();
		this.updateCoreRateOfChange();
		this.updateMetabolicRate();
		this.updateClothing();
		this.updateNodesHeatDelta();
		this.updateHeatDeltas();
		this.updateNodes();
		this.updateBodyMultipliers();
		this.updateThermalDamage(this.airAndWindTemp);
	}

	private float getSicknessValue() {
		return this.stats.getSickness();
	}

	private void updateSetPoint() {
		this.setPoint = 37.0F;
		if (this.stats.getSickness() > 0.0F) {
			float float1 = 2.0F;
			this.setPoint += this.stats.getSickness() * float1;
		}
	}

	private void updateMetabolicRate() {
		this.setMetabolicTarget(Metabolics.Default.getMet());
		if (this.player != null) {
			if (this.player.isAttacking()) {
				WeaponType weaponType = WeaponType.getWeaponType((IsoGameCharacter)this.player);
				switch (weaponType) {
				case barehand: 
					this.setMetabolicTarget(Metabolics.MediumWork);
					break;
				
				case twohanded: 
					this.setMetabolicTarget(Metabolics.HeavyWork);
					break;
				
				case onehanded: 
					this.setMetabolicTarget(Metabolics.MediumWork);
					break;
				
				case heavy: 
					this.setMetabolicTarget(Metabolics.Running15kmh);
					break;
				
				case knife: 
					this.setMetabolicTarget(Metabolics.LightWork);
					break;
				
				case spear: 
					this.setMetabolicTarget(Metabolics.MediumWork);
					break;
				
				case handgun: 
					this.setMetabolicTarget(Metabolics.UsingTools);
					break;
				
				case firearm: 
					this.setMetabolicTarget(Metabolics.LightWork);
					break;
				
				case throwing: 
					this.setMetabolicTarget(Metabolics.MediumWork);
					break;
				
				case chainsaw: 
					this.setMetabolicTarget(Metabolics.Running15kmh);
				
				}
			}

			if (this.player.isPlayerMoving()) {
				if (this.player.isSprinting()) {
					this.setMetabolicTarget(Metabolics.Running15kmh);
				} else if (this.player.isRunning()) {
					this.setMetabolicTarget(Metabolics.Running10kmh);
				} else if (this.player.isSneaking()) {
					this.setMetabolicTarget(Metabolics.Walking2kmh);
				} else if (this.player.CurrentSpeed > 0.0F) {
					this.setMetabolicTarget(Metabolics.Walking5kmh);
				}
			}
		}

		float float1 = PZMath.clamp_01(1.0F - this.stats.getEndurance()) * Metabolics.DefaultExercise.getMet();
		this.setMetabolicTarget(float1 * this.getEnergy());
		float float2 = PZMath.clamp_01(this.player.getInventory().getCapacityWeight() / (float)this.player.getMaxWeight());
		float float3 = 1.0F + float2 * float2 * 0.35F;
		this.setMetabolicTarget(this.metabolicTarget * float3);
		float float4;
		if (!PZMath.equal(this.metabolicRate, this.metabolicTarget)) {
			float4 = this.metabolicTarget - this.metabolicRate;
			if (this.metabolicTarget > this.metabolicRate) {
				this.metabolicRate += float4 * this.getSimulationMultiplier(Thermoregulator.Multiplier.MetabolicRateInc);
			} else {
				this.metabolicRate += float4 * this.getSimulationMultiplier(Thermoregulator.Multiplier.MetabolicRateDec);
			}
		}

		float4 = 1.0F;
		if (this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia) >= 1) {
			float4 = this.getMovementModifier();
		}

		this.metabolicRateReal = this.metabolicRate * (0.2F + 0.8F * this.getEnergy() * float4);
		this.metabolicTarget = -1.0F;
	}

	private void updateNodesHeatDelta() {
		float float1 = PZMath.clamp_01((float)((this.player.getNutrition().getWeight() / 75.0 - 0.5) * 0.6660000085830688));
		float1 = (float1 - 0.5F) * 2.0F;
		float float2 = this.stats.getFitness();
		float float3 = 1.0F;
		if (this.airAndWindTemp > this.setPoint - 2.0F) {
			if (this.airTemperature < this.setPoint + 2.0F) {
				float3 = (this.airTemperature - (this.setPoint - 2.0F)) / 4.0F;
				float3 = 1.0F - float3;
			} else {
				float3 = 0.0F;
			}
		}

		float float4 = 1.0F;
		float float5;
		if (this.climate.getHumidity() > 0.5F) {
			float5 = (this.climate.getHumidity() - 0.5F) * 2.0F;
			float4 -= float5;
		}

		float5 = 1.0F;
		if (this.core.celcius < 37.0F) {
			float5 = (this.core.celcius - 20.0F) / 17.0F;
			float5 *= float5;
		}

		float float6 = 0.0F;
		for (int int1 = 0; int1 < this.nodes.length; ++int1) {
			Thermoregulator.ThermalNode thermalNode = this.nodes[int1];
			thermalNode.calculateInsulation();
			float float7 = this.airTemperature;
			if (this.airAndWindTemp < this.airTemperature) {
				float7 -= (this.airTemperature - this.airAndWindTemp) / (1.0F + thermalNode.windresist);
			}

			float float8 = float7 - thermalNode.skinCelcius;
			if (float8 <= 0.0F) {
				float8 *= 1.0F + 0.75F * thermalNode.bodyWetness;
			} else {
				float8 /= 1.0F + 3.0F * thermalNode.bodyWetness;
			}

			float8 *= 0.3F;
			float8 /= 1.0F + thermalNode.insulation;
			thermalNode.heatDelta = float8 * thermalNode.skinSurface;
			float float9;
			float float10;
			if (thermalNode.primaryDelta > 0.0F) {
				float10 = 0.2F + 0.8F * this.getBodyFluids();
				float9 = Metabolics.Default.getMet() * thermalNode.primaryDelta * thermalNode.skinSurface / (1.0F + thermalNode.insulation);
				float9 *= float10 * (0.1F + 0.9F * float3);
				float9 *= float4;
				float9 *= 1.0F - 0.2F * float1;
				float9 *= 1.0F + 0.2F * float2;
				thermalNode.heatDelta -= float9;
			} else {
				float10 = 0.2F + 0.8F * this.getEnergy();
				float9 = Metabolics.Default.getMet() * PZMath.abs(thermalNode.primaryDelta) * thermalNode.skinSurface;
				float9 *= float10;
				float9 *= 1.0F + 0.2F * float1;
				float9 *= 1.0F + 0.2F * float2;
				thermalNode.heatDelta += float9;
			}

			if (thermalNode.secondaryDelta > 0.0F) {
				float10 = 0.1F + 0.9F * this.getBodyFluids();
				float9 = Metabolics.MAX.getMet() * 0.75F * thermalNode.secondaryDelta * thermalNode.skinSurface / (1.0F + thermalNode.insulation);
				float9 *= float10;
				float9 *= 0.85F + 0.15F * float4;
				float9 *= 1.0F - 0.2F * float1;
				float9 *= 1.0F + 0.2F * float2;
				thermalNode.heatDelta -= float9;
			} else {
				float10 = 0.1F + 0.9F * this.getEnergy();
				float9 = Metabolics.Default.getMet() * PZMath.abs(thermalNode.secondaryDelta) * thermalNode.skinSurface;
				float9 *= float10;
				float9 *= 1.0F + 0.2F * float1;
				float9 *= 1.0F + 0.2F * float2;
				thermalNode.heatDelta += float9;
			}

			float6 += thermalNode.heatDelta;
		}

		this.totalHeatRaw = float6;
		float6 += this.metabolicRateReal;
		this.totalHeat = float6;
	}

	private void updateHeatDeltas() {
		this.coreHeatDelta = this.totalHeat * this.getSimulationMultiplier(Thermoregulator.Multiplier.BodyHeat);
		if (this.coreHeatDelta < 0.0F) {
			if (this.core.celcius > this.setPoint) {
				this.coreHeatDelta *= 1.0F + (this.core.celcius - this.setPoint) / 2.0F;
			}
		} else if (this.core.celcius < this.setPoint) {
			this.coreHeatDelta *= 1.0F + (this.setPoint - this.core.celcius) / 4.0F;
		}

		Thermoregulator.ThermalNode thermalNode = this.core;
		thermalNode.celcius += this.coreHeatDelta;
		this.core.celcius = PZMath.clamp(this.core.celcius, 20.0F, 42.0F);
		this.bodyDamage.setTemperature(this.core.celcius);
		this.bodyHeatDelta = 0.0F;
		if (this.core.celcius > this.setPoint) {
			this.bodyHeatDelta = this.core.celcius - this.setPoint;
		} else if (this.core.celcius < this.setPoint) {
			this.bodyHeatDelta = this.core.celcius - this.setPoint;
		}

		if (this.bodyHeatDelta < 0.0F) {
			float float1 = PZMath.abs(this.bodyHeatDelta);
			if (float1 <= 1.0F) {
				this.bodyHeatDelta *= 0.8F;
			} else {
				float1 = PZMath.clamp(float1, 1.0F, 11.0F) - 1.0F;
				float1 /= 10.0F;
				this.bodyHeatDelta = -0.8F + -0.2F * float1;
			}
		}

		this.bodyHeatDelta = PZMath.clamp(this.bodyHeatDelta, -1.0F, 1.0F);
	}

	private void updateNodes() {
		float float1 = 0.0F;
		float float2 = 0.0F;
		for (int int1 = 0; int1 < this.nodes.length; ++int1) {
			Thermoregulator.ThermalNode thermalNode = this.nodes[int1];
			float float3 = 1.0F + thermalNode.insulation;
			float float4 = this.metabolicRateReal / Metabolics.MAX.getMet();
			float4 *= float4;
			float float5;
			if (this.bodyHeatDelta < 0.0F) {
				float5 = thermalNode.distToCore;
				thermalNode.primaryDelta = this.bodyHeatDelta * (1.0F + float5);
			} else {
				thermalNode.primaryDelta = this.bodyHeatDelta * (1.0F + (1.0F - thermalNode.distToCore));
			}

			thermalNode.primaryDelta = PZMath.clamp(thermalNode.primaryDelta, -1.0F, 1.0F);
			thermalNode.secondaryDelta = thermalNode.primaryDelta * PZMath.abs(thermalNode.primaryDelta) * PZMath.abs(thermalNode.primaryDelta);
			float1 += thermalNode.primaryDelta * thermalNode.skinSurface;
			float2 += thermalNode.secondaryDelta * thermalNode.skinSurface;
			if (this.stats.getDrunkenness() > 0.0F) {
				thermalNode.primaryDelta += this.stats.getDrunkenness() * 0.02F;
			}

			thermalNode.primaryDelta = PZMath.clamp(thermalNode.primaryDelta, -1.0F, 1.0F);
			float5 = this.core.celcius - 20.0F;
			float float6 = this.core.celcius;
			float float7;
			float float8;
			if (float5 < this.airTemperature) {
				if (this.airTemperature < 33.0F) {
					float5 = this.airTemperature;
				} else {
					float7 = 0.4F + 0.6F * (1.0F - thermalNode.distToCore);
					float8 = (this.airTemperature - 33.0F) / 6.0F;
					float5 = 33.0F;
					float5 += 4.0F * float8 * float7;
					float5 = PZMath.clamp(float5, 33.0F, this.airTemperature);
					if (float5 > float6) {
						float5 = float6 - 0.25F;
					}
				}
			}

			float7 = this.core.celcius - 4.0F;
			float float9;
			if (thermalNode.primaryDelta < 0.0F) {
				float8 = 0.4F + 0.6F * thermalNode.distToCore;
				float9 = float7 - 12.0F * float8 / float3;
				float7 = PZMath.c_lerp(float7, float9, PZMath.abs(thermalNode.primaryDelta));
			} else {
				float8 = 0.4F + 0.6F * (1.0F - thermalNode.distToCore);
				float float10 = 4.0F * float8;
				float10 *= Math.max(float3 * 0.5F * float8, 1.0F);
				float9 = Math.min(float7 + float10, float6);
				float7 = PZMath.c_lerp(float7, float9, thermalNode.primaryDelta);
			}

			float7 = PZMath.clamp(float7, float5, float6);
			float8 = float7 - thermalNode.skinCelcius;
			float9 = this.getSimulationMultiplier(Thermoregulator.Multiplier.SkinCelcius);
			if (float8 < 0.0F && thermalNode.skinCelcius > 33.0F) {
				float9 *= 3.0F;
			} else if (float8 > 0.0F && thermalNode.skinCelcius < 33.0F) {
				float9 *= 3.0F;
			}

			if (float9 > 1.0F) {
				float9 = 1.0F;
			}

			thermalNode.skinCelcius += float8 * float9;
			if (thermalNode != this.core) {
				if (thermalNode.skinCelcius >= this.core.celcius) {
					thermalNode.celcius = this.core.celcius;
				} else {
					thermalNode.celcius = PZMath.lerp(thermalNode.skinCelcius, this.core.celcius, 0.5F);
				}
			}
		}

		this.primTotal = float1;
		this.secTotal = float2;
	}

	private void updateBodyMultipliers() {
		this.energyMultiplier = 1.0;
		this.fluidsMultiplier = 1.0;
		this.fatigueMultiplier = 1.0;
		float float1 = PZMath.abs(this.primTotal);
		float1 *= float1;
		if (this.primTotal < 0.0F) {
			this.energyMultiplier += (double)(0.05F * float1);
			this.fatigueMultiplier += (double)(0.25F * float1);
		} else if (this.primTotal > 0.0F) {
			this.fluidsMultiplier += (double)(0.25F * float1);
			this.fatigueMultiplier += (double)(0.25F * float1);
		}

		float1 = PZMath.abs(this.secTotal);
		float1 *= float1;
		if (this.secTotal < 0.0F) {
			this.energyMultiplier += (double)(0.1F * float1);
			this.fatigueMultiplier += (double)(0.75F * float1);
		} else if (this.secTotal > 0.0F) {
			this.fluidsMultiplier += (double)(3.75F * float1);
			this.fatigueMultiplier += (double)(1.75F * float1);
		}
	}

	private void updateClothing() {
		this.character.getItemVisuals(itemVisuals);
		boolean boolean1 = itemVisuals.size() != itemVisualsCache.size();
		int int1;
		if (!boolean1) {
			for (int1 = 0; int1 < itemVisuals.size(); ++int1) {
				if (int1 >= itemVisualsCache.size() || itemVisuals.get(int1) != itemVisualsCache.get(int1)) {
					boolean1 = true;
					break;
				}
			}
		}

		if (boolean1) {
			for (int1 = 0; int1 < this.nodes.length; ++int1) {
				this.nodes[int1].clothing.clear();
			}

			itemVisualsCache.clear();
			for (int1 = 0; int1 < itemVisuals.size(); ++int1) {
				ItemVisual itemVisual = (ItemVisual)itemVisuals.get(int1);
				InventoryItem inventoryItem = itemVisual.getInventoryItem();
				itemVisualsCache.add(itemVisual);
				if (inventoryItem instanceof Clothing) {
					Clothing clothing = (Clothing)inventoryItem;
					if (clothing.getInsulation() > 0.0F || clothing.getWindresistance() > 0.0F) {
						boolean boolean2 = false;
						ArrayList arrayList = inventoryItem.getBloodClothingType();
						if (arrayList != null) {
							coveredParts.clear();
							BloodClothingType.getCoveredParts(arrayList, coveredParts);
							for (int int2 = 0; int2 < coveredParts.size(); ++int2) {
								BloodBodyPartType bloodBodyPartType = (BloodBodyPartType)coveredParts.get(int2);
								if (bloodBodyPartType.index() >= 0 && bloodBodyPartType.index() < this.nodes.length) {
									boolean2 = true;
									this.nodes[bloodBodyPartType.index()].clothing.add(clothing);
								}
							}
						}

						if (!boolean2 && clothing.getBodyLocation() != null) {
							String string = clothing.getBodyLocation().toLowerCase();
							byte byte1 = -1;
							switch (string.hashCode()) {
							case 103067: 
								if (string.equals("hat")) {
									byte1 = 0;
								}

								break;
							
							case 3344108: 
								if (string.equals("mask")) {
									byte1 = 1;
								}

							
							}

							switch (byte1) {
							case 0: 
							
							case 1: 
								this.nodes[BodyPartType.ToIndex(BodyPartType.Head)].clothing.add(clothing);
							
							}
						}
					}
				}
			}
		}
	}

	public float getEnergy() {
		float float1 = 1.0F - (0.4F * this.stats.getHunger() + 0.6F * this.stats.getHunger() * this.stats.getHunger());
		float float2 = 1.0F - (0.4F * this.stats.getFatigue() + 0.6F * this.stats.getFatigue() * this.stats.getFatigue());
		return 0.6F * float1 + 0.4F * float2;
	}

	public float getBodyFluids() {
		return 1.0F - this.stats.getThirst();
	}

	public class ThermalNode {
		private final float distToCore;
		private final float skinSurface;
		private final BodyPartType bodyPartType;
		private final BloodBodyPartType bloodBPT;
		private final BodyPart bodyPart;
		private final boolean isCore;
		private final float insulationLayerMultiplierUI;
		private Thermoregulator.ThermalNode upstream;
		private Thermoregulator.ThermalNode[] downstream;
		private float insulation;
		private float windresist;
		private float celcius;
		private float skinCelcius;
		private float heatDelta;
		private float primaryDelta;
		private float secondaryDelta;
		private float clothingWetness;
		private float bodyWetness;
		private ArrayList clothing;

		public ThermalNode(float float1, BodyPart bodyPart, float float2) {
			this(false, float1, bodyPart, float2);
		}

		public ThermalNode(boolean boolean1, float float1, BodyPart bodyPart, float float2) {
			this.celcius = 37.0F;
			this.skinCelcius = 33.0F;
			this.heatDelta = 0.0F;
			this.primaryDelta = 0.0F;
			this.secondaryDelta = 0.0F;
			this.clothingWetness = 0.0F;
			this.bodyWetness = 0.0F;
			this.clothing = new ArrayList();
			this.isCore = boolean1;
			this.celcius = float1;
			this.distToCore = BodyPartType.GetDistToCore(bodyPart.Type);
			this.skinSurface = BodyPartType.GetSkinSurface(bodyPart.Type);
			this.bodyPartType = bodyPart.Type;
			this.bloodBPT = BloodBodyPartType.FromIndex(BodyPartType.ToIndex(bodyPart.Type));
			this.bodyPart = bodyPart;
			this.insulationLayerMultiplierUI = float2;
		}

		private void calculateInsulation() {
			int int1 = this.clothing.size();
			this.insulation = 0.0F;
			this.windresist = 0.0F;
			this.clothingWetness = 0.0F;
			this.bodyWetness = this.bodyPart != null ? this.bodyPart.getWetness() * 0.01F : 0.0F;
			this.bodyWetness = PZMath.clamp_01(this.bodyWetness);
			if (int1 > 0) {
				for (int int2 = 0; int2 < int1; ++int2) {
					Clothing clothing = (Clothing)this.clothing.get(int2);
					ItemVisual itemVisual = clothing.getVisual();
					float float1 = PZMath.clamp(clothing.getWetness() * 0.01F, 0.0F, 1.0F);
					this.clothingWetness += float1;
					boolean boolean1 = itemVisual.getHole(this.bloodBPT) > 0.0F;
					if (!boolean1) {
						float float2 = Temperature.getTrueInsulationValue(clothing.getInsulation());
						float float3 = Temperature.getTrueWindresistanceValue(clothing.getWindresistance());
						float float4 = PZMath.clamp(clothing.getCurrentCondition() * 0.01F, 0.0F, 1.0F);
						float4 = 0.5F + 0.5F * float4;
						float2 *= (1.0F - float1 * 0.75F) * float4;
						float3 *= (1.0F - float1 * 0.45F) * float4;
						this.insulation += float2;
						this.windresist += float3;
					}
				}

				this.clothingWetness /= (float)int1;
				this.insulation += (float)int1 * 0.05F;
				this.windresist += (float)int1 * 0.05F;
			}
		}

		public String getName() {
			return this.bodyPartType.toString();
		}

		public boolean hasUpstream() {
			return this.upstream != null;
		}

		public boolean hasDownstream() {
			return this.downstream != null && this.downstream.length > 0;
		}

		public float getDistToCore() {
			return this.distToCore;
		}

		public float getSkinSurface() {
			return this.skinSurface;
		}

		public boolean isCore() {
			return this.isCore;
		}

		public float getInsulation() {
			return this.insulation;
		}

		public float getWindresist() {
			return this.windresist;
		}

		public float getCelcius() {
			return this.celcius;
		}

		public float getSkinCelcius() {
			return this.skinCelcius;
		}

		public float getHeatDelta() {
			return this.heatDelta;
		}

		public float getPrimaryDelta() {
			return this.primaryDelta;
		}

		public float getSecondaryDelta() {
			return this.secondaryDelta;
		}

		public float getClothingWetness() {
			return this.clothingWetness;
		}

		public float getBodyWetness() {
			return this.bodyWetness;
		}

		public float getBodyResponse() {
			return PZMath.lerp(this.primaryDelta, this.secondaryDelta, 0.5F);
		}

		public float getSkinCelciusUI() {
			float float1 = PZMath.clamp(this.getSkinCelcius(), 20.0F, 42.0F);
			if (float1 < 33.0F) {
				float1 = (float1 - 20.0F) / 13.0F * 0.5F;
			} else {
				float1 = 0.5F + (float1 - 33.0F) / 9.0F;
			}

			return float1;
		}

		public float getHeatDeltaUI() {
			return PZMath.clamp((this.heatDelta * 0.2F + 1.0F) / 2.0F, 0.0F, 1.0F);
		}

		public float getPrimaryDeltaUI() {
			return PZMath.clamp((this.primaryDelta + 1.0F) / 2.0F, 0.0F, 1.0F);
		}

		public float getSecondaryDeltaUI() {
			return PZMath.clamp((this.secondaryDelta + 1.0F) / 2.0F, 0.0F, 1.0F);
		}

		public float getInsulationUI() {
			return PZMath.clamp(this.insulation * this.insulationLayerMultiplierUI, 0.0F, 1.0F);
		}

		public float getWindresistUI() {
			return PZMath.clamp(this.windresist * this.insulationLayerMultiplierUI, 0.0F, 1.0F);
		}

		public float getClothingWetnessUI() {
			return PZMath.clamp(this.clothingWetness, 0.0F, 1.0F);
		}

		public float getBodyWetnessUI() {
			return PZMath.clamp(this.bodyWetness, 0.0F, 1.0F);
		}

		public float getBodyResponseUI() {
			return PZMath.clamp((this.getBodyResponse() + 1.0F) / 2.0F, 0.0F, 1.0F);
		}
	}

	private static enum Multiplier {

		Default,
		MetabolicRateInc,
		MetabolicRateDec,
		BodyHeat,
		CoreHeatExpand,
		CoreHeatContract,
		SkinCelcius,
		SkinCelciusContract,
		SkinCelciusExpand,
		PrimaryDelta,
		SecondaryDelta;

		private static Thermoregulator.Multiplier[] $values() {
			return new Thermoregulator.Multiplier[]{Default, MetabolicRateInc, MetabolicRateDec, BodyHeat, CoreHeatExpand, CoreHeatContract, SkinCelcius, SkinCelciusContract, SkinCelciusExpand, PrimaryDelta, SecondaryDelta};
		}
	}
}
