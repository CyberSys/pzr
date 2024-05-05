package zombie.characters.BodyDamage;

import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.inventory.types.Clothing;
import zombie.network.BodyDamageSync;


public final class BodyPart {
	BodyPartType Type;
	private float BiteDamage = 2.1875F;
	private float BleedDamage = 0.2857143F;
	private float DamageScaler = 0.0057142857F;
	private float Health;
	private boolean bandaged;
	private boolean bitten;
	private boolean bleeding;
	private boolean IsBleedingStemmed;
	private boolean IsCortorised;
	private boolean scratched;
	private boolean stitched;
	private boolean deepWounded;
	private boolean IsInfected;
	private boolean IsFakeInfected;
	private final IsoGameCharacter ParentChar;
	private float bandageLife = 0.0F;
	private float scratchTime = 0.0F;
	private float biteTime = 0.0F;
	private boolean alcoholicBandage = false;
	private float stiffness = 0.0F;
	private float woundInfectionLevel = 0.0F;
	private boolean infectedWound = false;
	private float ScratchDamage = 0.9375F;
	private float CutDamage = 1.875F;
	private float WoundDamage = 3.125F;
	private float BurnDamage = 3.75F;
	private float BulletDamage = 3.125F;
	private float FractureDamage = 3.125F;
	private float bleedingTime = 0.0F;
	private float deepWoundTime = 0.0F;
	private boolean haveGlass = false;
	private float stitchTime = 0.0F;
	private float alcoholLevel = 0.0F;
	private float additionalPain = 0.0F;
	private String bandageType = null;
	private boolean getBandageXp = true;
	private boolean getStitchXp = true;
	private boolean getSplintXp = true;
	private float fractureTime = 0.0F;
	private boolean splint = false;
	private float splintFactor = 0.0F;
	private boolean haveBullet = false;
	private float burnTime = 0.0F;
	private boolean needBurnWash = false;
	private float lastTimeBurnWash = 0.0F;
	private String splintItem = null;
	private float plantainFactor = 0.0F;
	private float comfreyFactor = 0.0F;
	private float garlicFactor = 0.0F;
	private float cutTime = 0.0F;
	private boolean cut = false;
	private float scratchSpeedModifier = 0.0F;
	private float cutSpeedModifier = 0.0F;
	private float burnSpeedModifier = 0.0F;
	private float deepWoundSpeedModifier = 0.0F;
	private float wetness = 0.0F;
	protected Thermoregulator.ThermalNode thermalNode;

	public BodyPart(BodyPartType bodyPartType, IsoGameCharacter gameCharacter) {
		this.Type = bodyPartType;
		this.ParentChar = gameCharacter;
		if (bodyPartType == BodyPartType.Neck) {
			this.DamageScaler *= 5.0F;
		}

		if (bodyPartType == BodyPartType.Hand_L || bodyPartType == BodyPartType.Hand_R || bodyPartType == BodyPartType.ForeArm_L || bodyPartType == BodyPartType.ForeArm_R) {
			this.scratchSpeedModifier = 85.0F;
			this.cutSpeedModifier = 95.0F;
			this.burnSpeedModifier = 45.0F;
			this.deepWoundSpeedModifier = 60.0F;
		}

		if (bodyPartType == BodyPartType.UpperArm_L || bodyPartType == BodyPartType.UpperArm_R) {
			this.scratchSpeedModifier = 65.0F;
			this.cutSpeedModifier = 75.0F;
			this.burnSpeedModifier = 35.0F;
			this.deepWoundSpeedModifier = 40.0F;
		}

		if (bodyPartType == BodyPartType.UpperLeg_L || bodyPartType == BodyPartType.UpperLeg_R || bodyPartType == BodyPartType.LowerLeg_L || bodyPartType == BodyPartType.LowerLeg_R) {
			this.scratchSpeedModifier = 45.0F;
			this.cutSpeedModifier = 55.0F;
			this.burnSpeedModifier = 15.0F;
			this.deepWoundSpeedModifier = 20.0F;
		}

		if (bodyPartType == BodyPartType.Foot_L || bodyPartType == BodyPartType.Foot_R) {
			this.scratchSpeedModifier = 35.0F;
			this.cutSpeedModifier = 45.0F;
			this.burnSpeedModifier = 10.0F;
			this.deepWoundSpeedModifier = 15.0F;
		}

		if (bodyPartType == BodyPartType.Groin) {
			this.scratchSpeedModifier = 45.0F;
			this.cutSpeedModifier = 55.0F;
			this.burnSpeedModifier = 15.0F;
			this.deepWoundSpeedModifier = 20.0F;
		}

		this.RestoreToFullHealth();
	}

	public void AddDamage(float float1) {
		this.Health -= float1;
		if (this.Health < 0.0F) {
			this.Health = 0.0F;
		}
	}

	public boolean isBandageDirty() {
		return this.getBandageLife() <= 0.0F;
	}

	public void DamageUpdate() {
		if (this.getDeepWoundTime() > 0.0F && !this.stitched()) {
			if (this.bandaged()) {
				this.Health -= this.WoundDamage / 2.0F * this.DamageScaler * GameTime.getInstance().getMultiplier();
			} else {
				this.Health -= this.WoundDamage * this.DamageScaler * GameTime.getInstance().getMultiplier();
			}
		}

		if (this.getScratchTime() > 0.0F && !this.bandaged()) {
			this.Health -= this.ScratchDamage * this.DamageScaler * GameTime.getInstance().getMultiplier();
		}

		if (this.getCutTime() > 0.0F && !this.bandaged()) {
			this.Health -= this.CutDamage * this.DamageScaler * GameTime.getInstance().getMultiplier();
		}

		if (this.getBiteTime() > 0.0F && !this.bandaged()) {
			this.Health -= this.BiteDamage * this.DamageScaler * GameTime.getInstance().getMultiplier();
		}

		if (this.getBleedingTime() > 0.0F && !this.bandaged()) {
			float float1 = this.BleedDamage * this.DamageScaler * GameTime.getInstance().getMultiplier() * (this.getBleedingTime() / 10.0F);
			this.ParentChar.getBodyDamage().ReduceGeneralHealth(float1);
			LuaEventManager.triggerEvent("OnPlayerGetDamage", this.ParentChar, "BLEEDING", float1);
			if (Rand.NextBool(Rand.AdjustForFramerate(1000))) {
				this.ParentChar.addBlood(BloodBodyPartType.FromIndex(BodyPartType.ToIndex(this.getType())), false, false, true);
			}
		}

		if (this.haveBullet()) {
			if (this.bandaged()) {
				this.Health -= this.BulletDamage / 2.0F * this.DamageScaler * GameTime.getInstance().getMultiplier();
			} else {
				this.Health -= this.BulletDamage * this.DamageScaler * GameTime.getInstance().getMultiplier();
			}
		}

		if (this.getBurnTime() > 0.0F && !this.bandaged()) {
			this.Health -= this.BurnDamage * this.DamageScaler * GameTime.getInstance().getMultiplier();
		}

		if (this.getFractureTime() > 0.0F && !this.isSplint()) {
			this.Health -= this.FractureDamage * this.DamageScaler * GameTime.getInstance().getMultiplier();
		}

		if (this.getBiteTime() > 0.0F) {
			if (this.bandaged()) {
				this.setBiteTime(this.getBiteTime() - (float)(1.0E-4 * (double)GameTime.getInstance().getMultiplier()));
				this.setBandageLife(this.getBandageLife() - (float)(1.0E-4 * (double)GameTime.getInstance().getMultiplier()));
			} else {
				this.setBiteTime(this.getBiteTime() - (float)(5.0E-6 * (double)GameTime.getInstance().getMultiplier()));
			}
		}

		if (this.getBurnTime() > 0.0F) {
			if (this.bandaged()) {
				this.setBurnTime(this.getBurnTime() - (float)(1.0E-4 * (double)GameTime.getInstance().getMultiplier()));
				this.setBandageLife(this.getBandageLife() - (float)(1.0E-4 * (double)GameTime.getInstance().getMultiplier()));
			} else {
				this.setBurnTime(this.getBurnTime() - (float)(5.0E-6 * (double)GameTime.getInstance().getMultiplier()));
			}

			if (this.getLastTimeBurnWash() - this.getBurnTime() >= 20.0F) {
				this.setLastTimeBurnWash(0.0F);
				this.setNeedBurnWash(true);
			}
		}

		if (this.getBleedingTime() > 0.0F) {
			if (this.bandaged()) {
				this.setBleedingTime(this.getBleedingTime() - (float)(2.0E-4 * (double)GameTime.getInstance().getMultiplier()));
				if (this.getDeepWoundTime() > 0.0F) {
					this.setBandageLife(this.getBandageLife() - (float)(0.005 * (double)GameTime.getInstance().getMultiplier()));
				} else {
					this.setBandageLife(this.getBandageLife() - (float)(3.0E-4 * (double)GameTime.getInstance().getMultiplier()));
				}
			} else {
				this.setBleedingTime(this.getBleedingTime() - (float)(2.0E-5 * (double)GameTime.getInstance().getMultiplier()));
			}

			if (this.getBleedingTime() < 3.0F && this.haveGlass()) {
				this.setBleedingTime(3.0F);
			}

			if (this.getBleedingTime() < 0.0F) {
				this.setBleedingTime(0.0F);
				this.setBleeding(false);
			}
		}

		if (this.isInfectedWound() || this.IsInfected || this.alcoholicBandage && this.getBandageLife() > 0.0F || !(this.getDeepWoundTime() > 0.0F) && !(this.getScratchTime() > 0.0F) && !(this.getCutTime() > 0.0F) && !(this.getStitchTime() > 0.0F)) {
			if (this.isInfectedWound()) {
				boolean boolean1 = false;
				if (this.getAlcoholLevel() > 0.0F) {
					this.setAlcoholLevel(this.getAlcoholLevel() - 2.0E-4F * GameTime.getInstance().getMultiplier());
					this.setWoundInfectionLevel(this.getWoundInfectionLevel() - 2.0E-4F * GameTime.getInstance().getMultiplier());
					if (this.getAlcoholLevel() < 0.0F) {
						this.setAlcoholLevel(0.0F);
					}

					boolean1 = true;
				}

				if (this.ParentChar.getReduceInfectionPower() > 0.0F) {
					this.setWoundInfectionLevel(this.getWoundInfectionLevel() - 2.0E-4F * GameTime.getInstance().getMultiplier());
					this.ParentChar.setReduceInfectionPower(this.ParentChar.getReduceInfectionPower() - 2.0E-4F * GameTime.getInstance().getMultiplier());
					if (this.ParentChar.getReduceInfectionPower() < 0.0F) {
						this.ParentChar.setReduceInfectionPower(0.0F);
					}

					boolean1 = true;
				}

				if (this.getGarlicFactor() > 0.0F) {
					this.setWoundInfectionLevel(this.getWoundInfectionLevel() - 2.0E-4F * GameTime.getInstance().getMultiplier());
					this.setGarlicFactor(this.getGarlicFactor() - 8.0E-4F * GameTime.getInstance().getMultiplier());
					boolean1 = true;
				}

				if (!boolean1) {
					if (this.IsInfected) {
						this.setWoundInfectionLevel(this.getWoundInfectionLevel() + 2.0E-4F * GameTime.getInstance().getMultiplier());
					} else if (this.haveGlass()) {
						this.setWoundInfectionLevel(this.getWoundInfectionLevel() + 1.0E-4F * GameTime.getInstance().getMultiplier());
					} else {
						this.setWoundInfectionLevel(this.getWoundInfectionLevel() + 1.0E-5F * GameTime.getInstance().getMultiplier());
					}

					if (this.getWoundInfectionLevel() > 10.0F) {
						this.setWoundInfectionLevel(10.0F);
					}
				}
			}
		} else {
			int int1 = 40000;
			if (!this.bandaged()) {
				int1 -= 10000;
			} else if (this.getBandageLife() == 0.0F) {
				int1 -= 35000;
			}

			if (this.getScratchTime() > 0.0F) {
				int1 -= 20000;
			}

			if (this.getCutTime() > 0.0F) {
				int1 -= 25000;
			}

			if (this.getDeepWoundTime() > 0.0F) {
				int1 -= 30000;
			}

			if (this.haveGlass()) {
				int1 -= 24000;
			}

			if (this.getBurnTime() > 0.0F) {
				int1 -= 23000;
				if (this.isNeedBurnWash()) {
					int1 -= 7000;
				}
			}

			Clothing clothing;
			if (BodyPartType.ToIndex(this.getType()) <= BodyPartType.ToIndex(BodyPartType.Torso_Lower) && this.ParentChar.getClothingItem_Torso() instanceof Clothing) {
				clothing = (Clothing)this.ParentChar.getClothingItem_Torso();
				if (clothing.isDirty()) {
					int1 -= 20000;
				}

				if (clothing.isBloody()) {
					int1 -= 24000;
				}
			}

			if (BodyPartType.ToIndex(this.getType()) >= BodyPartType.ToIndex(BodyPartType.UpperLeg_L) && BodyPartType.ToIndex(this.getType()) <= BodyPartType.ToIndex(BodyPartType.LowerLeg_R) && this.ParentChar.getClothingItem_Legs() instanceof Clothing) {
				clothing = (Clothing)this.ParentChar.getClothingItem_Legs();
				if (clothing.isDirty()) {
					int1 -= 20000;
				}

				if (clothing.isBloody()) {
					int1 -= 24000;
				}
			}

			if (int1 <= 5000) {
				int1 = 5000;
			}

			if (Rand.Next(Rand.AdjustForFramerate(int1)) == 0) {
				this.setInfectedWound(true);
			}
		}

		if (!this.isInfectedWound() && this.getAlcoholLevel() > 0.0F) {
			this.setAlcoholLevel(this.getAlcoholLevel() - 2.0E-4F * GameTime.getInstance().getMultiplier());
			if (this.getAlcoholLevel() < 0.0F) {
				this.setAlcoholLevel(0.0F);
			}
		}

		if (this.isInfectedWound() && this.getBandageLife() > 0.0F) {
			if (this.alcoholicBandage) {
				this.setWoundInfectionLevel(this.getWoundInfectionLevel() - 6.0E-4F * GameTime.getInstance().getMultiplier());
			}

			this.setBandageLife(this.getBandageLife() - (float)(2.0E-4 * (double)GameTime.getInstance().getMultiplier()));
		}

		if (this.getScratchTime() > 0.0F) {
			if (this.bandaged()) {
				this.setScratchTime(this.getScratchTime() - (float)(1.5E-4 * (double)GameTime.getInstance().getMultiplier()));
				this.setBandageLife(this.getBandageLife() - (float)(8.0E-5 * (double)GameTime.getInstance().getMultiplier()));
				if (this.getPlantainFactor() > 0.0F) {
					this.setScratchTime(this.getScratchTime() - (float)(1.0E-4 * (double)GameTime.getInstance().getMultiplier()));
					this.setPlantainFactor(this.getPlantainFactor() - (float)(8.0E-4 * (double)GameTime.getInstance().getMultiplier()));
				}
			} else {
				this.setScratchTime(this.getScratchTime() - (float)(1.0E-5 * (double)GameTime.getInstance().getMultiplier()));
			}

			if (this.getScratchTime() < 0.0F) {
				this.setScratchTime(0.0F);
				this.setGetBandageXp(true);
				this.setGetStitchXp(true);
				this.setScratched(false, false);
				this.setBleeding(false);
				this.setBleedingTime(0.0F);
			}
		}

		if (this.getCutTime() > 0.0F) {
			if (this.bandaged()) {
				this.setCutTime(this.getCutTime() - (float)(5.0E-5 * (double)GameTime.getInstance().getMultiplier()));
				this.setBandageLife(this.getBandageLife() - (float)(1.0E-5 * (double)GameTime.getInstance().getMultiplier()));
				if (this.getPlantainFactor() > 0.0F) {
					this.setCutTime(this.getCutTime() - (float)(5.0E-5 * (double)GameTime.getInstance().getMultiplier()));
					this.setPlantainFactor(this.getPlantainFactor() - (float)(8.0E-4 * (double)GameTime.getInstance().getMultiplier()));
				}
			} else {
				this.setCutTime(this.getCutTime() - (float)(1.0E-6 * (double)GameTime.getInstance().getMultiplier()));
			}

			if (this.getCutTime() < 0.0F) {
				this.setCutTime(0.0F);
				this.setGetBandageXp(true);
				this.setGetStitchXp(true);
				this.setBleeding(false);
				this.setBleedingTime(0.0F);
			}
		}

		if (this.getDeepWoundTime() > 0.0F) {
			if (this.bandaged()) {
				this.setDeepWoundTime(this.getDeepWoundTime() - (float)(2.0E-5 * (double)GameTime.getInstance().getMultiplier()));
				this.setBandageLife(this.getBandageLife() - (float)(1.0E-4 * (double)GameTime.getInstance().getMultiplier()));
				if (this.getPlantainFactor() > 0.0F) {
					this.setDeepWoundTime(this.getDeepWoundTime() - (float)(7.0E-6 * (double)GameTime.getInstance().getMultiplier()));
					this.setPlantainFactor(this.getPlantainFactor() - (float)(8.0E-4 * (double)GameTime.getInstance().getMultiplier()));
					if (this.getPlantainFactor() < 0.0F) {
						this.setPlantainFactor(0.0F);
					}
				}
			} else {
				this.setDeepWoundTime(this.getDeepWoundTime() - (float)(2.0E-6 * (double)GameTime.getInstance().getMultiplier()));
			}

			if ((this.haveGlass() || !this.bandaged()) && this.getDeepWoundTime() < 3.0F) {
				this.setDeepWoundTime(3.0F);
			}

			if (this.getDeepWoundTime() < 0.0F) {
				this.setGetBandageXp(true);
				this.setGetStitchXp(true);
				this.setDeepWoundTime(0.0F);
				this.setDeepWounded(false);
			}
		}

		if (this.getStitchTime() > 0.0F && this.getStitchTime() < 50.0F) {
			if (this.bandaged()) {
				this.setStitchTime(this.getStitchTime() + (float)(4.0E-4 * (double)GameTime.getInstance().getMultiplier()));
				this.setBandageLife(this.getBandageLife() - (float)(1.0E-4 * (double)GameTime.getInstance().getMultiplier()));
				if (!this.alcoholicBandage && Rand.Next(Rand.AdjustForFramerate(80000)) == 0) {
					this.setInfectedWound(true);
				}

				this.setStitchTime(this.getStitchTime() + (float)(1.0E-4 * (double)GameTime.getInstance().getMultiplier()));
			} else {
				this.setStitchTime(this.getStitchTime() + (float)(2.0E-4 * (double)GameTime.getInstance().getMultiplier()));
				if (Rand.Next(Rand.AdjustForFramerate(20000)) == 0) {
					this.setInfectedWound(true);
				}
			}

			if (this.getStitchTime() > 30.0F) {
				this.setGetStitchXp(true);
			}

			if (this.getStitchTime() > 50.0F) {
				this.setStitchTime(50.0F);
			}
		}

		if (this.getFractureTime() > 0.0F) {
			if (this.getSplintFactor() > 0.0F) {
				this.setFractureTime(this.getFractureTime() - (float)(5.0E-5 * (double)GameTime.getInstance().getMultiplier() * (double)this.getSplintFactor()));
			} else {
				this.setFractureTime(this.getFractureTime() - (float)(5.0E-6 * (double)GameTime.getInstance().getMultiplier()));
			}

			if (this.getComfreyFactor() > 0.0F) {
				this.setFractureTime(this.getFractureTime() - (float)(5.0E-6 * (double)GameTime.getInstance().getMultiplier()));
				this.setComfreyFactor(this.getComfreyFactor() - (float)(5.0E-4 * (double)GameTime.getInstance().getMultiplier()));
			}

			if (this.getFractureTime() < 0.0F) {
				this.setFractureTime(0.0F);
				this.setGetSplintXp(true);
			}
		}

		if (this.getAdditionalPain() > 0.0F) {
			this.setAdditionalPain(this.getAdditionalPain() - (float)(0.005 * (double)GameTime.getInstance().getMultiplier()));
			if (this.getAdditionalPain() < 0.0F) {
				this.setAdditionalPain(0.0F);
			}
		}

		if (this.getStiffness() > 0.0F && this.ParentChar instanceof IsoPlayer && ((IsoPlayer)this.ParentChar).getFitness() != null && !((IsoPlayer)this.ParentChar).getFitness().onGoingStiffness()) {
			this.setStiffness(this.getStiffness() - (float)(0.002 * (double)GameTime.getInstance().getMultiplier()));
			if (this.getStiffness() < 0.0F) {
				this.setStiffness(0.0F);
			}
		}

		if (this.getBandageLife() < 0.0F) {
			this.setBandageLife(0.0F);
			this.setGetBandageXp(true);
		}

		if ((this.getWoundInfectionLevel() > 0.0F || this.isInfectedWound()) && this.getBurnTime() <= 0.0F && this.getFractureTime() <= 0.0F && this.getDeepWoundTime() <= 0.0F && this.getScratchTime() <= 0.0F && this.getBiteTime() <= 0.0F && this.getCutTime() <= 0.0F && this.getStitchTime() <= 0.0F) {
			this.setWoundInfectionLevel(0.0F);
		}

		if (this.Health < 0.0F) {
			this.Health = 0.0F;
		}
	}

	public float getHealth() {
		return this.Health;
	}

	public void SetHealth(float float1) {
		this.Health = float1;
	}

	public void AddHealth(float float1) {
		this.Health += float1;
		if (this.Health > 100.0F) {
			this.Health = 100.0F;
		}
	}

	public void ReduceHealth(float float1) {
		this.Health -= float1;
		if (this.Health < 0.0F) {
			this.Health = 0.0F;
		}
	}

	public boolean HasInjury() {
		return this.bitten | this.scratched | this.deepWounded | this.bleeding | this.getBiteTime() > 0.0F | this.getScratchTime() > 0.0F | this.getCutTime() > 0.0F | this.getFractureTime() > 0.0F | this.haveBullet() | this.getBurnTime() > 0.0F;
	}

	public boolean bandaged() {
		return this.bandaged;
	}

	public boolean bitten() {
		return this.bitten;
	}

	public boolean bleeding() {
		return this.bleeding;
	}

	public boolean IsBleedingStemmed() {
		return this.IsBleedingStemmed;
	}

	public boolean IsCortorised() {
		return this.IsCortorised;
	}

	public boolean IsInfected() {
		return this.IsInfected;
	}

	public void SetInfected(boolean boolean1) {
		this.IsInfected = boolean1;
	}

	public void SetFakeInfected(boolean boolean1) {
		this.IsFakeInfected = boolean1;
	}

	public boolean IsFakeInfected() {
		return this.IsFakeInfected;
	}

	public void DisableFakeInfection() {
		this.IsFakeInfected = false;
	}

	public boolean scratched() {
		return this.scratched;
	}

	public boolean stitched() {
		return this.stitched;
	}

	public boolean deepWounded() {
		return this.deepWounded;
	}

	public void RestoreToFullHealth() {
		this.Health = 100.0F;
		this.additionalPain = 0.0F;
		this.alcoholicBandage = false;
		this.alcoholLevel = 0.0F;
		this.bleeding = false;
		this.bandaged = false;
		this.bandageLife = 0.0F;
		this.biteTime = 0.0F;
		this.bitten = false;
		this.bleedingTime = 0.0F;
		this.burnTime = 0.0F;
		this.comfreyFactor = 0.0F;
		this.deepWounded = false;
		this.deepWoundTime = 0.0F;
		this.fractureTime = 0.0F;
		this.garlicFactor = 0.0F;
		this.haveBullet = false;
		this.haveGlass = false;
		this.infectedWound = false;
		this.IsBleedingStemmed = false;
		this.IsCortorised = false;
		this.IsFakeInfected = false;
		this.IsInfected = false;
		this.lastTimeBurnWash = 0.0F;
		this.needBurnWash = false;
		this.plantainFactor = 0.0F;
		this.scratched = false;
		this.scratchTime = 0.0F;
		this.splint = false;
		this.splintFactor = 0.0F;
		this.splintItem = null;
		this.stitched = false;
		this.stitchTime = 0.0F;
		this.woundInfectionLevel = 0.0F;
		this.cutTime = 0.0F;
		this.cut = false;
	}

	public void setBandaged(boolean boolean1, float float1) {
		this.setBandaged(boolean1, float1, false, (String)null);
	}

	public void setBandaged(boolean boolean1, float float1, boolean boolean2, String string) {
		if (boolean1) {
			if (this.bleeding) {
				this.bleeding = false;
			}

			this.bitten = false;
			this.scratched = false;
			this.cut = false;
			this.alcoholicBandage = boolean2;
			this.stitched = false;
			this.deepWounded = false;
			this.setBandageType(string);
			this.setGetBandageXp(false);
		} else {
			if (this.getScratchTime() > 0.0F) {
				this.scratched = true;
			}

			if (this.getCutTime() > 0.0F) {
				this.cut = true;
			}

			if (this.getBleedingTime() > 0.0F) {
				this.bleeding = true;
			}

			if (this.getBiteTime() > 0.0F) {
				this.bitten = true;
			}

			if (this.getStitchTime() > 0.0F) {
				this.stitched = true;
			}

			if (this.getDeepWoundTime() > 0.0F) {
				this.deepWounded = true;
			}
		}

		this.setBandageLife(float1);
		this.bandaged = boolean1;
	}

	public void SetBitten(boolean boolean1) {
		this.bitten = boolean1;
		if (boolean1) {
			this.bleeding = true;
			this.IsBleedingStemmed = false;
			this.IsCortorised = false;
			this.bandaged = false;
			this.setInfectedWound(true);
			this.setBiteTime(Rand.Next(50.0F, 80.0F));
			if (this.ParentChar.Traits.FastHealer.isSet()) {
				this.setBiteTime(Rand.Next(30.0F, 50.0F));
			}

			if (this.ParentChar.Traits.SlowHealer.isSet()) {
				this.setBiteTime(Rand.Next(80.0F, 150.0F));
			}
		}

		if (SandboxOptions.instance.Lore.Transmission.getValue() != 4) {
			this.IsInfected = true;
			this.IsFakeInfected = false;
		}

		if (this.IsInfected && SandboxOptions.instance.Lore.Mortality.getValue() == 7) {
			this.IsInfected = false;
			this.IsFakeInfected = true;
		}

		this.generateBleeding();
	}

	public void SetBitten(boolean boolean1, boolean boolean2) {
		this.bitten = boolean1;
		if (SandboxOptions.instance.Lore.Transmission.getValue() == 4) {
			this.IsInfected = false;
			this.IsFakeInfected = false;
			boolean2 = false;
		}

		if (boolean1) {
			this.bleeding = true;
			this.IsBleedingStemmed = false;
			this.IsCortorised = false;
			this.bandaged = false;
			if (boolean2) {
				this.IsInfected = true;
			}

			this.IsFakeInfected = false;
			if (this.IsInfected && SandboxOptions.instance.Lore.Mortality.getValue() == 7) {
				this.IsInfected = false;
				this.IsFakeInfected = true;
			}
		}
	}

	public void setBleeding(boolean boolean1) {
		this.bleeding = boolean1;
	}

	public void SetBleedingStemmed(boolean boolean1) {
		if (this.bleeding) {
			this.bleeding = false;
			this.IsBleedingStemmed = true;
		}
	}

	public void SetCortorised(boolean boolean1) {
		this.IsCortorised = boolean1;
		if (boolean1) {
			this.bleeding = false;
			this.IsBleedingStemmed = false;
			this.deepWounded = false;
			this.bandaged = false;
		}
	}

	public void setCut(boolean boolean1) {
		this.setCut(boolean1, true);
	}

	public void setCut(boolean boolean1, boolean boolean2) {
		this.cut = boolean1;
		if (boolean1) {
			this.setStitched(false);
			this.setBandaged(false, 0.0F);
			float float1 = Rand.Next(10.0F, 20.0F);
			if (this.ParentChar.Traits.FastHealer.isSet()) {
				float1 = Rand.Next(5.0F, 10.0F);
			}

			if (this.ParentChar.Traits.SlowHealer.isSet()) {
				float1 = Rand.Next(20.0F, 30.0F);
			}

			switch (SandboxOptions.instance.InjurySeverity.getValue()) {
			case 1: 
				float1 *= 0.5F;
				break;
			
			case 3: 
				float1 *= 1.5F;
			
			}

			this.setCutTime(float1);
			this.generateBleeding();
			if (!boolean2) {
				this.generateZombieInfection(25);
			}
		} else {
			this.setBleeding(false);
		}
	}

	public void generateZombieInfection(int int1) {
		if (Rand.Next(100) < int1) {
			this.IsInfected = true;
		}

		if (!this.IsInfected && this.ParentChar.Traits.Hypercondriac.isSet() && Rand.Next(100) < 80) {
			this.IsFakeInfected = true;
		}

		if (SandboxOptions.instance.Lore.Transmission.getValue() == 2 || SandboxOptions.instance.Lore.Transmission.getValue() == 4) {
			this.IsInfected = false;
			this.IsFakeInfected = false;
		}

		if (this.IsInfected && SandboxOptions.instance.Lore.Mortality.getValue() == 7) {
			this.IsInfected = false;
			this.IsFakeInfected = true;
		}
	}

	public void setScratched(boolean boolean1, boolean boolean2) {
		this.scratched = boolean1;
		if (boolean1) {
			this.setStitched(false);
			this.setBandaged(false, 0.0F);
			float float1 = Rand.Next(7.0F, 15.0F);
			if (this.ParentChar.Traits.FastHealer.isSet()) {
				float1 = Rand.Next(4.0F, 10.0F);
			}

			if (this.ParentChar.Traits.SlowHealer.isSet()) {
				float1 = Rand.Next(15.0F, 25.0F);
			}

			switch (SandboxOptions.instance.InjurySeverity.getValue()) {
			case 1: 
				this.scratchTime *= 0.5F;
				break;
			
			case 3: 
				this.scratchTime *= 1.5F;
			
			}

			this.setScratchTime(float1);
			this.generateBleeding();
			if (!boolean2) {
				this.generateZombieInfection(7);
			}
		} else {
			this.setBleeding(false);
		}
	}

	public void SetScratchedWeapon(boolean boolean1) {
		this.scratched = boolean1;
		if (boolean1) {
			this.setStitched(false);
			this.setBandaged(false, 0.0F);
			float float1 = Rand.Next(5.0F, 10.0F);
			if (this.ParentChar.Traits.FastHealer.isSet()) {
				float1 = Rand.Next(1.0F, 5.0F);
			}

			if (this.ParentChar.Traits.SlowHealer.isSet()) {
				float1 = Rand.Next(10.0F, 20.0F);
			}

			switch (SandboxOptions.instance.InjurySeverity.getValue()) {
			case 1: 
				this.scratchTime *= 0.5F;
				break;
			
			case 3: 
				this.scratchTime *= 1.5F;
			
			}

			this.setScratchTime(float1);
			this.generateBleeding();
		}
	}

	public void generateDeepWound() {
		float float1 = Rand.Next(15.0F, 20.0F);
		if (this.ParentChar.Traits.FastHealer.isSet()) {
			float1 = Rand.Next(11.0F, 15.0F);
		} else if (this.ParentChar.Traits.SlowHealer.isSet()) {
			float1 = Rand.Next(20.0F, 32.0F);
		}

		switch (SandboxOptions.instance.InjurySeverity.getValue()) {
		case 1: 
			float1 *= 0.5F;
			break;
		
		case 3: 
			float1 *= 1.5F;
		
		}
		this.setDeepWoundTime(float1);
		this.setDeepWounded(true);
		this.generateBleeding();
	}

	public void generateDeepShardWound() {
		float float1 = Rand.Next(15.0F, 20.0F);
		if (this.ParentChar.Traits.FastHealer.isSet()) {
			float1 = Rand.Next(11.0F, 15.0F);
		} else if (this.ParentChar.Traits.SlowHealer.isSet()) {
			float1 = Rand.Next(20.0F, 32.0F);
		}

		switch (SandboxOptions.instance.InjurySeverity.getValue()) {
		case 1: 
			float1 *= 0.5F;
			break;
		
		case 3: 
			float1 *= 1.5F;
		
		}
		this.setDeepWoundTime(float1);
		this.setHaveGlass(true);
		this.setDeepWounded(true);
		this.generateBleeding();
	}

	public void SetScratchedWindow(boolean boolean1) {
		if (boolean1) {
			this.setBandaged(false, 0.0F);
			this.setStitched(false);
			if (Rand.Next(7) == 0) {
				this.generateDeepShardWound();
			} else {
				this.scratched = boolean1;
				float float1 = Rand.Next(12.0F, 20.0F);
				if (this.ParentChar.Traits.FastHealer.isSet()) {
					float1 = Rand.Next(5.0F, 10.0F);
				}

				if (this.ParentChar.Traits.SlowHealer.isSet()) {
					float1 = Rand.Next(20.0F, 30.0F);
				}

				switch (SandboxOptions.instance.InjurySeverity.getValue()) {
				case 1: 
					this.scratchTime *= 0.5F;
					break;
				
				case 3: 
					this.scratchTime *= 1.5F;
				
				}

				this.setScratchTime(float1);
			}

			this.generateBleeding();
		}
	}

	public void setStitched(boolean boolean1) {
		if (boolean1) {
			this.setBleedingTime(0.0F);
			this.setBleeding(false);
			this.setDeepWoundTime(0.0F);
			this.setDeepWounded(false);
			this.setGetStitchXp(false);
		} else if (this.stitched) {
			this.stitched = false;
			if (this.getStitchTime() < 40.0F) {
				this.setDeepWoundTime(Rand.Next(10.0F, this.getStitchTime()));
				this.setBleedingTime(Rand.Next(10.0F, this.getStitchTime()));
				this.setStitchTime(0.0F);
				this.setDeepWounded(true);
			} else {
				this.setScratchTime(Rand.Next(2.0F, this.getStitchTime() - 40.0F));
				this.scratched = true;
				this.setStitchTime(0.0F);
			}
		}

		this.stitched = boolean1;
	}

	public void damageFromFirearm(float float1) {
		this.setHaveBullet(true, 0);
	}

	public float getPain() {
		float float1 = 0.0F;
		if (this.getScratchTime() > 0.0F) {
			float1 += this.getScratchTime() * 1.7F;
		}

		if (this.getCutTime() > 0.0F) {
			float1 += this.getCutTime() * 2.5F;
		}

		if (this.getBiteTime() > 0.0F) {
			if (this.bandaged()) {
				float1 += 30.0F;
			} else if (!this.bandaged()) {
				float1 += 50.0F;
			}
		}

		if (this.getDeepWoundTime() > 0.0F) {
			float1 += this.getDeepWoundTime() * 3.7F;
		}

		if (this.getStitchTime() > 0.0F && this.getStitchTime() < 35.0F) {
			if (this.bandaged()) {
				float1 += (35.0F - this.getStitchTime()) / 2.0F;
			} else {
				float1 += 35.0F - this.getStitchTime();
			}
		}

		if (this.getFractureTime() > 0.0F) {
			if (this.getSplintFactor() > 0.0F) {
				float1 += this.getFractureTime() / 2.0F;
			} else {
				float1 += this.getFractureTime();
			}
		}

		if (this.haveBullet()) {
			float1 += 50.0F;
		}

		if (this.haveGlass()) {
			float1 += 10.0F;
		}

		if (this.getBurnTime() > 0.0F) {
			float1 += this.getBurnTime();
		}

		if (this.bandaged()) {
			float1 /= 1.5F;
		}

		if (this.getWoundInfectionLevel() > 0.0F) {
			float1 += this.getWoundInfectionLevel();
		}

		float1 += this.getAdditionalPain(true);
		switch (SandboxOptions.instance.InjurySeverity.getValue()) {
		case 1: 
			float1 *= 0.7F;
			break;
		
		case 3: 
			float1 *= 1.3F;
		
		}
		return float1;
	}

	public float getBiteTime() {
		return this.biteTime;
	}

	public void setBiteTime(float float1) {
		this.biteTime = float1;
	}

	public float getDeepWoundTime() {
		return this.deepWoundTime;
	}

	public void setDeepWoundTime(float float1) {
		this.deepWoundTime = float1;
	}

	public boolean haveGlass() {
		return this.haveGlass;
	}

	public void setHaveGlass(boolean boolean1) {
		this.haveGlass = boolean1;
	}

	public float getStitchTime() {
		return this.stitchTime;
	}

	public void setStitchTime(float float1) {
		this.stitchTime = float1;
	}

	public int getIndex() {
		return BodyPartType.ToIndex(this.Type);
	}

	public float getAlcoholLevel() {
		return this.alcoholLevel;
	}

	public void setAlcoholLevel(float float1) {
		this.alcoholLevel = float1;
	}

	public float getAdditionalPain(boolean boolean1) {
		return boolean1 ? this.additionalPain + this.stiffness / 3.5F : this.additionalPain;
	}

	public float getAdditionalPain() {
		return this.additionalPain;
	}

	public void setAdditionalPain(float float1) {
		this.additionalPain = float1;
	}

	public String getBandageType() {
		return this.bandageType;
	}

	public void setBandageType(String string) {
		this.bandageType = string;
	}

	public boolean isGetBandageXp() {
		return this.getBandageXp;
	}

	public void setGetBandageXp(boolean boolean1) {
		this.getBandageXp = boolean1;
	}

	public boolean isGetStitchXp() {
		return this.getStitchXp;
	}

	public void setGetStitchXp(boolean boolean1) {
		this.getStitchXp = boolean1;
	}

	public float getSplintFactor() {
		return this.splintFactor;
	}

	public void setSplintFactor(float float1) {
		this.splintFactor = float1;
	}

	public float getFractureTime() {
		return this.fractureTime;
	}

	public void setFractureTime(float float1) {
		this.fractureTime = float1;
	}

	public boolean isGetSplintXp() {
		return this.getSplintXp;
	}

	public void setGetSplintXp(boolean boolean1) {
		this.getSplintXp = boolean1;
	}

	public boolean isSplint() {
		return this.splint;
	}

	public void setSplint(boolean boolean1, float float1) {
		this.splint = boolean1;
		this.setSplintFactor(float1);
		if (boolean1) {
			this.setGetSplintXp(false);
		}
	}

	public boolean haveBullet() {
		return this.haveBullet;
	}

	public void setHaveBullet(boolean boolean1, int int1) {
		if (this.haveBullet && !boolean1) {
			float float1 = Rand.Next(17.0F, 23.0F) - (float)(int1 / 2);
			if (this.ParentChar != null && this.ParentChar.Traits != null) {
				if (this.ParentChar.Traits.FastHealer.isSet()) {
					float1 = Rand.Next(12.0F, 18.0F) - (float)(int1 / 2);
				} else if (this.ParentChar.Traits.SlowHealer.isSet()) {
					float1 = Rand.Next(22.0F, 28.0F) - (float)(int1 / 2);
				}
			}

			switch (SandboxOptions.instance.InjurySeverity.getValue()) {
			case 1: 
				float1 *= 0.5F;
				break;
			
			case 3: 
				float1 *= 1.5F;
			
			}

			this.setDeepWoundTime(float1);
			this.setDeepWounded(true);
			this.haveBullet = false;
			this.generateBleeding();
		} else if (boolean1) {
			this.haveBullet = true;
			this.generateBleeding();
		}

		this.haveBullet = boolean1;
	}

	public float getBurnTime() {
		return this.burnTime;
	}

	public void setBurnTime(float float1) {
		this.burnTime = float1;
	}

	public boolean isNeedBurnWash() {
		return this.needBurnWash;
	}

	public void setNeedBurnWash(boolean boolean1) {
		if (this.needBurnWash && !boolean1) {
			this.setLastTimeBurnWash(this.getBurnTime());
		}

		this.needBurnWash = boolean1;
	}

	public float getLastTimeBurnWash() {
		return this.lastTimeBurnWash;
	}

	public void setLastTimeBurnWash(float float1) {
		this.lastTimeBurnWash = float1;
	}

	public boolean isInfectedWound() {
		return this.infectedWound;
	}

	public void setInfectedWound(boolean boolean1) {
		this.infectedWound = boolean1;
	}

	public BodyPartType getType() {
		return this.Type;
	}

	public float getBleedingTime() {
		return this.bleedingTime;
	}

	public void setBleedingTime(float float1) {
		this.bleedingTime = float1;
		if (!this.bandaged()) {
			this.setBleeding(float1 > 0.0F);
		}
	}

	public boolean isDeepWounded() {
		return this.deepWounded;
	}

	public void setDeepWounded(boolean boolean1) {
		this.deepWounded = boolean1;
		if (boolean1) {
			this.bleeding = true;
			this.IsBleedingStemmed = false;
			this.IsCortorised = false;
			this.bandaged = false;
			this.stitched = false;
		}
	}

	public float getBandageLife() {
		return this.bandageLife;
	}

	public void setBandageLife(float float1) {
		this.bandageLife = float1;
		if (this.bandageLife <= 0.0F) {
			this.alcoholicBandage = false;
		}
	}

	public float getScratchTime() {
		return this.scratchTime;
	}

	public void setScratchTime(float float1) {
		float1 = Math.min(100.0F, float1);
		this.scratchTime = float1;
	}

	public float getWoundInfectionLevel() {
		return this.woundInfectionLevel;
	}

	public void setWoundInfectionLevel(float float1) {
		this.woundInfectionLevel = float1;
		if (this.woundInfectionLevel <= 0.0F) {
			this.setInfectedWound(false);
			if (this.woundInfectionLevel < -2.0F) {
				this.woundInfectionLevel = -2.0F;
			}
		} else {
			this.setInfectedWound(true);
		}
	}

	public void setBurned() {
		float float1 = Rand.Next(50.0F, 100.0F);
		switch (SandboxOptions.instance.InjurySeverity.getValue()) {
		case 1: 
			float1 *= 0.5F;
			break;
		
		case 3: 
			float1 *= 1.5F;
		
		}
		this.setBurnTime(float1);
		this.setNeedBurnWash(true);
		this.setLastTimeBurnWash(0.0F);
	}

	public String getSplintItem() {
		return this.splintItem;
	}

	public void setSplintItem(String string) {
		this.splintItem = string;
	}

	public float getPlantainFactor() {
		return this.plantainFactor;
	}

	public void setPlantainFactor(float float1) {
		this.plantainFactor = PZMath.clamp(float1, 0.0F, 100.0F);
	}

	public float getGarlicFactor() {
		return this.garlicFactor;
	}

	public void setGarlicFactor(float float1) {
		this.garlicFactor = PZMath.clamp(float1, 0.0F, 100.0F);
	}

	public float getComfreyFactor() {
		return this.comfreyFactor;
	}

	public void setComfreyFactor(float float1) {
		this.comfreyFactor = PZMath.clamp(float1, 0.0F, 100.0F);
	}

	public void sync(BodyPart bodyPart, BodyDamageSync.Updater updater) {
		if (updater.updateField((byte)1, this.Health, bodyPart.Health)) {
			bodyPart.Health = this.Health;
		}

		if (this.bandaged != bodyPart.bandaged) {
			updater.updateField((byte)2, this.bandaged);
			bodyPart.bandaged = this.bandaged;
		}

		if (this.bitten != bodyPart.bitten) {
			updater.updateField((byte)3, this.bitten);
			bodyPart.bitten = this.bitten;
		}

		if (this.bleeding != bodyPart.bleeding) {
			updater.updateField((byte)4, this.bleeding);
			bodyPart.bleeding = this.bleeding;
		}

		if (this.IsBleedingStemmed != bodyPart.IsBleedingStemmed) {
			updater.updateField((byte)5, this.IsBleedingStemmed);
			bodyPart.IsBleedingStemmed = this.IsBleedingStemmed;
		}

		if (this.scratched != bodyPart.scratched) {
			updater.updateField((byte)7, this.scratched);
			bodyPart.scratched = this.scratched;
		}

		if (this.cut != bodyPart.cut) {
			updater.updateField((byte)39, this.cut);
			bodyPart.cut = this.cut;
		}

		if (this.stitched != bodyPart.stitched) {
			updater.updateField((byte)8, this.stitched);
			bodyPart.stitched = this.stitched;
		}

		if (this.deepWounded != bodyPart.deepWounded) {
			updater.updateField((byte)9, this.deepWounded);
			bodyPart.deepWounded = this.deepWounded;
		}

		if (this.IsInfected != bodyPart.IsInfected) {
			updater.updateField((byte)10, this.IsInfected);
			bodyPart.IsInfected = this.IsInfected;
		}

		if (this.IsFakeInfected != bodyPart.IsFakeInfected) {
			updater.updateField((byte)11, this.IsFakeInfected);
			bodyPart.IsFakeInfected = this.IsFakeInfected;
		}

		if (updater.updateField((byte)12, this.bandageLife, bodyPart.bandageLife)) {
			bodyPart.bandageLife = this.bandageLife;
		}

		if (updater.updateField((byte)13, this.scratchTime, bodyPart.scratchTime)) {
			bodyPart.scratchTime = this.scratchTime;
		}

		if (updater.updateField((byte)14, this.biteTime, bodyPart.biteTime)) {
			bodyPart.biteTime = this.biteTime;
		}

		if (this.alcoholicBandage != bodyPart.alcoholicBandage) {
			updater.updateField((byte)15, this.alcoholicBandage);
			bodyPart.alcoholicBandage = this.alcoholicBandage;
		}

		if (updater.updateField((byte)16, this.woundInfectionLevel, bodyPart.woundInfectionLevel)) {
			bodyPart.woundInfectionLevel = this.woundInfectionLevel;
		}

		if (updater.updateField((byte)41, this.stiffness, bodyPart.stiffness)) {
			bodyPart.stiffness = this.stiffness;
		}

		if (this.infectedWound != bodyPart.infectedWound) {
			updater.updateField((byte)17, this.infectedWound);
			bodyPart.infectedWound = this.infectedWound;
		}

		if (updater.updateField((byte)18, this.bleedingTime, bodyPart.bleedingTime)) {
			bodyPart.bleedingTime = this.bleedingTime;
		}

		if (updater.updateField((byte)19, this.deepWoundTime, bodyPart.deepWoundTime)) {
			bodyPart.deepWoundTime = this.deepWoundTime;
		}

		if (updater.updateField((byte)40, this.cutTime, bodyPart.cutTime)) {
			bodyPart.cutTime = this.cutTime;
		}

		if (this.haveGlass != bodyPart.haveGlass) {
			updater.updateField((byte)20, this.haveGlass);
			bodyPart.haveGlass = this.haveGlass;
		}

		if (updater.updateField((byte)21, this.stitchTime, bodyPart.stitchTime)) {
			bodyPart.stitchTime = this.stitchTime;
		}

		if (updater.updateField((byte)22, this.alcoholLevel, bodyPart.alcoholLevel)) {
			bodyPart.alcoholLevel = this.alcoholLevel;
		}

		if (updater.updateField((byte)23, this.additionalPain, bodyPart.additionalPain)) {
			bodyPart.additionalPain = this.additionalPain;
		}

		if (this.bandageType != bodyPart.bandageType) {
			updater.updateField((byte)24, this.bandageType);
			bodyPart.bandageType = this.bandageType;
		}

		if (this.getBandageXp != bodyPart.getBandageXp) {
			updater.updateField((byte)25, this.getBandageXp);
			bodyPart.getBandageXp = this.getBandageXp;
		}

		if (this.getStitchXp != bodyPart.getStitchXp) {
			updater.updateField((byte)26, this.getStitchXp);
			bodyPart.getStitchXp = this.getStitchXp;
		}

		if (this.getSplintXp != bodyPart.getSplintXp) {
			updater.updateField((byte)27, this.getSplintXp);
			bodyPart.getSplintXp = this.getSplintXp;
		}

		if (updater.updateField((byte)28, this.fractureTime, bodyPart.fractureTime)) {
			bodyPart.fractureTime = this.fractureTime;
		}

		if (this.splint != bodyPart.splint) {
			updater.updateField((byte)29, this.splint);
			bodyPart.splint = this.splint;
		}

		if (updater.updateField((byte)30, this.splintFactor, bodyPart.splintFactor)) {
			bodyPart.splintFactor = this.splintFactor;
		}

		if (this.haveBullet != bodyPart.haveBullet) {
			updater.updateField((byte)31, this.haveBullet);
			bodyPart.haveBullet = this.haveBullet;
		}

		if (updater.updateField((byte)32, this.burnTime, bodyPart.burnTime)) {
			bodyPart.burnTime = this.burnTime;
		}

		if (this.needBurnWash != bodyPart.needBurnWash) {
			updater.updateField((byte)33, this.needBurnWash);
			bodyPart.needBurnWash = this.needBurnWash;
		}

		if (updater.updateField((byte)34, this.lastTimeBurnWash, bodyPart.lastTimeBurnWash)) {
			bodyPart.lastTimeBurnWash = this.lastTimeBurnWash;
		}

		if (this.splintItem != bodyPart.splintItem) {
			updater.updateField((byte)35, this.splintItem);
			bodyPart.splintItem = this.splintItem;
		}

		if (updater.updateField((byte)36, this.plantainFactor, bodyPart.plantainFactor)) {
			bodyPart.plantainFactor = this.plantainFactor;
		}

		if (updater.updateField((byte)37, this.comfreyFactor, bodyPart.comfreyFactor)) {
			bodyPart.comfreyFactor = this.comfreyFactor;
		}

		if (updater.updateField((byte)38, this.garlicFactor, bodyPart.garlicFactor)) {
			bodyPart.garlicFactor = this.garlicFactor;
		}
	}

	public void sync(ByteBuffer byteBuffer, byte byte1) {
		switch (byte1) {
		case 1: 
			this.Health = byteBuffer.getFloat();
			break;
		
		case 2: 
			this.bandaged = byteBuffer.get() == 1;
			break;
		
		case 3: 
			this.bitten = byteBuffer.get() == 1;
			break;
		
		case 4: 
			this.bleeding = byteBuffer.get() == 1;
			break;
		
		case 5: 
			this.IsBleedingStemmed = byteBuffer.get() == 1;
			break;
		
		case 6: 
			this.IsCortorised = byteBuffer.get() == 1;
			break;
		
		case 7: 
			this.scratched = byteBuffer.get() == 1;
			break;
		
		case 8: 
			this.stitched = byteBuffer.get() == 1;
			break;
		
		case 9: 
			this.deepWounded = byteBuffer.get() == 1;
			break;
		
		case 10: 
			this.IsInfected = byteBuffer.get() == 1;
			break;
		
		case 11: 
			this.IsFakeInfected = byteBuffer.get() == 1;
			break;
		
		case 12: 
			this.bandageLife = byteBuffer.getFloat();
			break;
		
		case 13: 
			this.scratchTime = byteBuffer.getFloat();
			break;
		
		case 14: 
			this.biteTime = byteBuffer.getFloat();
			break;
		
		case 15: 
			this.alcoholicBandage = byteBuffer.get() == 1;
			break;
		
		case 16: 
			this.woundInfectionLevel = byteBuffer.getFloat();
			break;
		
		case 17: 
			this.infectedWound = byteBuffer.get() == 1;
			break;
		
		case 18: 
			this.bleedingTime = byteBuffer.getFloat();
			break;
		
		case 19: 
			this.deepWoundTime = byteBuffer.getFloat();
			break;
		
		case 20: 
			this.haveGlass = byteBuffer.get() == 1;
			break;
		
		case 21: 
			this.stitchTime = byteBuffer.getFloat();
			break;
		
		case 22: 
			this.alcoholLevel = byteBuffer.getFloat();
			break;
		
		case 23: 
			this.additionalPain = byteBuffer.getFloat();
			break;
		
		case 24: 
			this.bandageType = GameWindow.ReadStringUTF(byteBuffer);
			break;
		
		case 25: 
			this.getBandageXp = byteBuffer.get() == 1;
			break;
		
		case 26: 
			this.getStitchXp = byteBuffer.get() == 1;
			break;
		
		case 27: 
			this.getSplintXp = byteBuffer.get() == 1;
			break;
		
		case 28: 
			this.fractureTime = byteBuffer.getFloat();
			break;
		
		case 29: 
			this.splint = byteBuffer.get() == 1;
			break;
		
		case 30: 
			this.splintFactor = byteBuffer.getFloat();
			break;
		
		case 31: 
			this.haveBullet = byteBuffer.get() == 1;
			break;
		
		case 32: 
			this.burnTime = byteBuffer.getFloat();
			break;
		
		case 33: 
			this.needBurnWash = byteBuffer.get() == 1;
			break;
		
		case 34: 
			this.lastTimeBurnWash = byteBuffer.getFloat();
			break;
		
		case 35: 
			this.splintItem = GameWindow.ReadStringUTF(byteBuffer);
			break;
		
		case 36: 
			this.plantainFactor = byteBuffer.getFloat();
			break;
		
		case 37: 
			this.comfreyFactor = byteBuffer.getFloat();
			break;
		
		case 38: 
			this.garlicFactor = byteBuffer.getFloat();
			break;
		
		case 39: 
			this.cut = byteBuffer.get() == 1;
			break;
		
		case 40: 
			this.cutTime = byteBuffer.getFloat();
			break;
		
		case 41: 
			this.stiffness = byteBuffer.getFloat();
		
		}
	}

	public float getCutTime() {
		return this.cutTime;
	}

	public void setCutTime(float float1) {
		float1 = Math.min(100.0F, float1);
		this.cutTime = float1;
	}

	public boolean isCut() {
		return this.cut;
	}

	public float getScratchSpeedModifier() {
		return this.scratchSpeedModifier;
	}

	public void setScratchSpeedModifier(float float1) {
		this.scratchSpeedModifier = float1;
	}

	public float getCutSpeedModifier() {
		return this.cutSpeedModifier;
	}

	public void setCutSpeedModifier(float float1) {
		this.cutSpeedModifier = float1;
	}

	public float getBurnSpeedModifier() {
		return this.burnSpeedModifier;
	}

	public void setBurnSpeedModifier(float float1) {
		this.burnSpeedModifier = float1;
	}

	public float getDeepWoundSpeedModifier() {
		return this.deepWoundSpeedModifier;
	}

	public void setDeepWoundSpeedModifier(float float1) {
		this.deepWoundSpeedModifier = float1;
	}

	public boolean isBurnt() {
		return this.getBurnTime() > 0.0F;
	}

	public void generateBleeding() {
		float float1 = 0.0F;
		if (this.scratched()) {
			float1 = Rand.Next(this.getScratchTime() * 0.3F, this.getScratchTime() * 0.6F);
		}

		if (this.isCut()) {
			float1 += Rand.Next(this.getCutTime() * 0.7F, this.getCutTime() * 1.0F);
		}

		if (this.isBurnt()) {
			float1 += Rand.Next(this.getBurnTime() * 0.3F, this.getBurnTime() * 0.6F);
		}

		if (this.isDeepWounded()) {
			float1 += Rand.Next(this.getDeepWoundTime() * 0.7F, this.getDeepWoundTime());
		}

		if (this.haveGlass()) {
			float1 += Rand.Next(5.0F, 10.0F);
		}

		if (this.haveBullet()) {
			float1 += Rand.Next(5.0F, 10.0F);
		}

		if (this.bitten()) {
			float1 += Rand.Next(7.5F, 15.0F);
		}

		switch (SandboxOptions.instance.InjurySeverity.getValue()) {
		case 1: 
			float1 *= 0.5F;
			break;
		
		case 3: 
			float1 *= 1.5F;
		
		}
		float1 *= BodyPartType.getBleedingTimeModifyer(BodyPartType.ToIndex(this.getType()));
		this.setBleedingTime(float1);
	}

	public float getInnerTemperature() {
		return this.thermalNode != null ? this.thermalNode.getCelcius() : 0.0F;
	}

	public float getSkinTemperature() {
		return this.thermalNode != null ? this.thermalNode.getSkinCelcius() : 0.0F;
	}

	public float getDistToCore() {
		return this.thermalNode != null ? this.thermalNode.getDistToCore() : BodyPartType.GetDistToCore(this.Type);
	}

	public float getSkinSurface() {
		return this.thermalNode != null ? this.thermalNode.getSkinSurface() : BodyPartType.GetSkinSurface(this.Type);
	}

	public Thermoregulator.ThermalNode getThermalNode() {
		return this.thermalNode;
	}

	public float getWetness() {
		return this.wetness;
	}

	public void setWetness(float float1) {
		this.wetness = PZMath.clamp(float1, 0.0F, 100.0F);
	}

	public float getStiffness() {
		return this.stiffness;
	}

	public void setStiffness(float float1) {
		this.stiffness = PZMath.clamp(float1, 0.0F, 100.0F);
	}
}
