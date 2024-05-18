package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorDesc;
import zombie.characters.skills.PerkFactory;
import zombie.core.Translator;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemType;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;


public class HandWeapon extends InventoryItem {
	public float SplatSize = 1.0F;
	protected String ammoType = null;
	protected boolean angleFalloff = false;
	protected boolean bCanBarracade = false;
	protected boolean directional = false;
	protected float doSwingBeforeImpact = 0.0F;
	protected String impactSound = "ZombieImpact";
	protected boolean knockBackOnNoDeath = true;
	protected float maxAngle = 1.0F;
	protected float maxDamage = 1.5F;
	protected int maxHitCount = 1000;
	protected float maxRange = 1.0F;
	protected boolean ranged = false;
	protected float minAngle = 0.5F;
	protected float minDamage = 0.4F;
	protected float minimumSwingTime = 0.5F;
	protected float minRange = 0.0F;
	protected float noiseFactor = 0.0F;
	protected String otherHandRequire = null;
	protected boolean otherHandUse = false;
	protected String physicsObject = null;
	protected float pushBackMod = 1.0F;
	protected boolean rangeFalloff = false;
	protected boolean shareDamage = true;
	protected int soundRadius = 0;
	protected int soundVolume = 0;
	protected boolean splatBloodOnNoDeath = false;
	protected int splatNumber = 2;
	protected String swingSound = "BatSwing";
	protected float swingTime = 1.0F;
	protected float toHitModifier = 1.0F;
	protected boolean useEndurance = true;
	protected boolean useSelf = false;
	protected String weaponSprite = null;
	protected float otherBoost = 1.0F;
	protected int DoorDamage = 1;
	protected String doorHitSound = "ChopDoor";
	protected int ConditionLowerChance = 10000;
	protected boolean MultipleHitConditionAffected = true;
	protected boolean shareEndurance = true;
	protected boolean AlwaysKnockdown = false;
	protected float EnduranceMod = 1.0F;
	protected float KnockdownMod = 1.0F;
	protected boolean CantAttackWithLowestEndurance = false;
	public boolean bIsAimedFirearm = false;
	public boolean bIsAimedHandWeapon = false;
	public String RunAnim = "Run";
	public String IdleAnim = "Idle";
	public float HitAngleMod = 0.0F;
	private String SubCategory = "";
	private ArrayList Categories = null;
	private int AimingPerkCritModifier = 0;
	private float AimingPerkRangeModifier = 0.0F;
	private float AimingPerkHitChanceModifier = 0.0F;
	private int HitChance = 0;
	private float AimingPerkMinAngleModifier = 0.0F;
	private int RecoilDelay = 0;
	private boolean PiercingBullets = false;
	private float soundGain = 1.0F;
	private WeaponPart scope = null;
	private WeaponPart canon = null;
	private WeaponPart clip = null;
	private WeaponPart recoilpad = null;
	private WeaponPart sling = null;
	private WeaponPart stock = null;
	private int ClipSize = 0;
	private int reloadTime = 0;
	private int aimingTime = 0;
	private float minRangeRanged = 0.0F;
	private int treeDamage = 0;
	private String bulletOutSound = null;
	private String shellFallSound = null;
	private int triggerExplosionTimer = 0;
	private boolean canBePlaced = false;
	private int explosionRange = 0;
	private int explosionPower = 0;
	private int fireRange = 0;
	private int firePower = 0;
	private int smokeRange = 0;
	private int noiseRange = 0;
	private float extraDamage = 0.0F;
	private int explosionTimer = 0;
	private String placedSprite = null;
	private boolean canBeReused = false;
	private int sensorRange = 0;
	public int ProjectileCount = 1;
	public float aimingMod = 1.0F;
	public float CriticalChance = 20.0F;
	private String hitSound = "BatHit";

	public float getSplatSize() {
		return this.SplatSize;
	}

	public boolean CanStack(InventoryItem inventoryItem) {
		return false;
	}

	public String getCategory() {
		return this.mainCategory != null ? this.mainCategory : "Weapon";
	}

	public HandWeapon(String string, String string2, String string3, String string4) {
		super(string, string2, string3, string4);
		this.cat = ItemType.Weapon;
	}

	public HandWeapon(String string, String string2, String string3, Item item) {
		super(string, string2, string3, item);
		this.cat = ItemType.Weapon;
	}

	public int getSaveType() {
		return Item.Type.Weapon.ordinal();
	}

	public float getScore(SurvivorDesc survivorDesc) {
		float float1 = 0.0F;
		if (this.ammoType != null && !this.ammoType.equals("none") && !this.container.contains(this.ammoType)) {
			float1 -= 100000.0F;
		}

		if (this.Condition == 0) {
			float1 -= 100000.0F;
		}

		float1 += this.maxDamage * 10.0F;
		float1 += this.maxAngle * 5.0F;
		float1 -= this.minimumSwingTime * 0.1F;
		float1 -= this.swingTime;
		if (survivorDesc != null && survivorDesc.getInstance().getThreatLevel() <= 2 && this.soundRadius > 5) {
			if (float1 > 0.0F && (float)this.soundRadius > float1) {
				float1 = 1.0F;
			}

			float1 -= (float)this.soundRadius;
		}

		return float1;
	}

	public boolean TestCanBarracade(IsoGameCharacter gameCharacter) {
		return gameCharacter.getInventory().contains("Nails") && gameCharacter.getInventory().contains("Plank");
	}

	public void DoTooltip(ObjectTooltip objectTooltip, ObjectTooltip.Layout layout) {
		float float1 = 1.0F;
		float float2 = 1.0F;
		float float3 = 0.8F;
		float float4 = 1.0F;
		float float5 = 0.0F;
		float float6 = 0.6F;
		float float7 = 0.0F;
		float float8 = 0.7F;
		ObjectTooltip.LayoutItem layoutItem = layout.addItem();
		layoutItem.setLabel(Translator.getText("Tooltip_weapon_Condition") + ":", float1, float2, float3, float4);
		float float9 = (float)this.Condition / (float)this.ConditionMax;
		layoutItem.setProgress(float9, float5, float6, float7, float8);
		float float10;
		float float11;
		if (this.getMaxDamage() > 0.0F) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_weapon_Damage") + ":", float1, float2, float3, float4);
			float9 = this.getMaxDamage() + this.getMinDamage();
			float10 = 5.0F;
			float11 = float9 / float10;
			layoutItem.setProgress(float11, float5, float6, float7, float8);
		}

		if (this.isRanged()) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_weapon_Range") + ":", float1, float2, float3, 1.0F);
			float9 = this.getMaxRange(IsoPlayer.instance);
			float10 = 40.0F;
			float11 = float9 / float10;
			layoutItem.setProgress(float11, float5, float6, float7, float8);
		}

		if (this.isTwoHandWeapon()) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_item_TwoHandWeapon"), float1, float2, float3, float4);
		}

		if (this.CantAttackWithLowestEndurance) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_weapon_Unusable_at_max_exertion"), 1.0F, 0.0F, 0.0F, 1.0F);
		}

		String string = this.getAmmoType();
		if (string == null && this.hasModData()) {
			Object object = this.getModData().rawget("defaultAmmo");
			if (object instanceof String) {
				string = (String)object;
			}
		}

		if (string != null) {
			Item item = ScriptManager.instance.FindItem(string);
			if (item == null) {
				item = ScriptManager.instance.FindItem(this.getModule() + "." + string);
			}

			if (item != null) {
				layoutItem = layout.addItem();
				layoutItem.setLabel(Translator.getText("Tooltip_weapon_Ammo") + ":", float1, float2, float3, float4);
				layoutItem.setValue(item.getDisplayName(), 1.0F, 1.0F, 1.0F, 1.0F);
			}

			Object object2 = this.getModData().rawget("currentCapacity");
			Object object3 = this.getModData().rawget("maxCapacity");
			if (object2 instanceof Double && object3 instanceof Double) {
				String string2 = ((Double)object2).intValue() + " / " + ((Double)object3).intValue();
				Object object4 = this.getModData().rawget("roundChambered");
				if (object4 instanceof Double && ((Double)object4).intValue() == 1) {
					string2 = ((Double)object2).intValue() + "+1 / " + ((Double)object3).intValue();
				} else {
					Object object5 = this.getModData().rawget("emptyShellChambered");
					if (object5 instanceof Double && ((Double)object5).intValue() == 1) {
						string2 = ((Double)object2).intValue() + "+x / " + ((Double)object3).intValue();
					}
				}

				layoutItem = layout.addItem();
				layoutItem.setLabel(Translator.getText("Tooltip_weapon_AmmoCount") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
				layoutItem.setValue(string2, 1.0F, 1.0F, 1.0F, 1.0F);
			}
		}

		ObjectTooltip.Layout layout2 = objectTooltip.beginLayout();
		if (this.getStock() != null) {
			layoutItem = layout2.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_weapon_Stock") + ":", float1, float2, float3, float4);
			layoutItem.setValue(this.getStock().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
		}

		if (this.getSling() != null) {
			layoutItem = layout2.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_weapon_Sling") + ":", float1, float2, float3, float4);
			layoutItem.setValue(this.getSling().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
		}

		if (this.getScope() != null) {
			layoutItem = layout2.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_weapon_Scope") + ":", float1, float2, float3, float4);
			layoutItem.setValue(this.getScope().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
		}

		if (this.getCanon() != null) {
			layoutItem = layout2.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_weapon_Canon") + ":", float1, float2, float3, float4);
			layoutItem.setValue(this.getCanon().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
		}

		if (this.getClip() != null) {
			layoutItem = layout2.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_weapon_Clip") + ":", float1, float2, float3, float4);
			layoutItem.setValue(this.getClip().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
		}

		if (this.getRecoilpad() != null) {
			layoutItem = layout2.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_weapon_RecoilPad") + ":", float1, float2, float3, float4);
			layoutItem.setValue(this.getRecoilpad().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
		}

		if (!layout2.items.isEmpty()) {
			layout.next = layout2;
			layout2.nextPadY = objectTooltip.getLineSpacing();
		} else {
			objectTooltip.endLayout(layout2);
		}
	}

	public float getDamageMod(IsoGameCharacter gameCharacter) {
		int int1 = gameCharacter.getPerkLevel(PerkFactory.Perks.Blunt);
		if (this.ScriptItem.Categories.contains("Blunt")) {
			if (int1 >= 3 && int1 <= 6) {
				return 1.1F;
			}

			if (int1 >= 7) {
				return 1.2F;
			}
		}

		int int2 = gameCharacter.getPerkLevel(PerkFactory.Perks.Axe);
		if (this.ScriptItem.Categories.contains("Axe")) {
			if (int2 >= 3 && int2 <= 6) {
				return 1.1F;
			}

			if (int2 >= 7) {
				return 1.2F;
			}
		}

		return 1.0F;
	}

	public float getRangeMod(IsoGameCharacter gameCharacter) {
		int int1 = gameCharacter.getPerkLevel(PerkFactory.Perks.Blunt);
		if (this.ScriptItem.Categories.contains("Blunt") && int1 >= 7) {
			return 1.2F;
		} else {
			int int2 = gameCharacter.getPerkLevel(PerkFactory.Perks.Axe);
			return this.ScriptItem.Categories.contains("Axe") && int2 >= 7 ? 1.2F : 1.0F;
		}
	}

	public float getFatigueMod(IsoGameCharacter gameCharacter) {
		int int1 = gameCharacter.getPerkLevel(PerkFactory.Perks.Blunt);
		if (this.ScriptItem.Categories.contains("Blunt") && int1 >= 8) {
			return 0.8F;
		} else {
			int int2 = gameCharacter.getPerkLevel(PerkFactory.Perks.Axe);
			return this.ScriptItem.Categories.contains("Axe") && int2 >= 8 ? 0.8F : 1.0F;
		}
	}

	public float getKnockbackMod(IsoGameCharacter gameCharacter) {
		int int1 = gameCharacter.getPerkLevel(PerkFactory.Perks.Axe);
		return this.ScriptItem.Categories.contains("Axe") && int1 >= 6 ? 2.0F : 1.0F;
	}

	public float getSpeedMod(IsoGameCharacter gameCharacter) {
		int int1;
		if (this.ScriptItem.Categories.contains("Blunt")) {
			int1 = gameCharacter.getPerkLevel(PerkFactory.Perks.Blunt);
			if (int1 >= 10) {
				return 0.65F;
			}

			if (int1 >= 9) {
				return 0.68F;
			}

			if (int1 >= 8) {
				return 0.71F;
			}

			if (int1 >= 7) {
				return 0.74F;
			}

			if (int1 >= 6) {
				return 0.77F;
			}

			if (int1 >= 5) {
				return 0.8F;
			}

			if (int1 >= 4) {
				return 0.83F;
			}

			if (int1 >= 3) {
				return 0.86F;
			}

			if (int1 >= 2) {
				return 0.9F;
			}

			if (int1 >= 1) {
				return 0.95F;
			}
		}

		if (this.ScriptItem.Categories.contains("Axe")) {
			int1 = gameCharacter.getPerkLevel(PerkFactory.Perks.Axe);
			float float1 = 1.0F;
			if (gameCharacter.HasTrait("Axeman")) {
				float1 = 0.95F;
			}

			if (int1 >= 10) {
				return 0.65F * float1;
			} else if (int1 >= 9) {
				return 0.68F * float1;
			} else if (int1 >= 8) {
				return 0.71F * float1;
			} else if (int1 >= 7) {
				return 0.74F * float1;
			} else if (int1 >= 6) {
				return 0.77F * float1;
			} else if (int1 >= 5) {
				return 0.8F * float1;
			} else if (int1 >= 4) {
				return 0.83F * float1;
			} else if (int1 >= 3) {
				return 0.86F * float1;
			} else if (int1 >= 2) {
				return 0.9F * float1;
			} else {
				return int1 >= 1 ? 0.95F * float1 : 1.0F * float1;
			}
		} else {
			return 1.0F;
		}
	}

	public float getToHitMod(IsoGameCharacter gameCharacter) {
		int int1 = gameCharacter.getPerkLevel(PerkFactory.Perks.Blunt);
		if (this.ScriptItem.Categories.contains("Blunt")) {
			if (int1 == 1) {
				return 1.2F;
			}

			if (int1 == 2) {
				return 1.3F;
			}

			if (int1 == 3) {
				return 1.4F;
			}

			if (int1 == 4) {
				return 1.5F;
			}

			if (int1 == 5) {
				return 1.6F;
			}

			if (int1 == 6) {
				return 1.7F;
			}

			if (int1 == 7) {
				return 1.8F;
			}

			if (int1 == 8) {
				return 1.9F;
			}

			if (int1 == 9) {
				return 2.0F;
			}

			if (int1 == 10) {
				return 100.0F;
			}
		}

		int int2 = gameCharacter.getPerkLevel(PerkFactory.Perks.Axe);
		if (this.ScriptItem.Categories.contains("Axe")) {
			if (int2 == 1) {
				return 1.2F;
			}

			if (int2 == 2) {
				return 1.3F;
			}

			if (int2 == 3) {
				return 1.4F;
			}

			if (int2 == 4) {
				return 1.5F;
			}

			if (int2 == 5) {
				return 1.6F;
			}

			if (int2 == 6) {
				return 1.7F;
			}

			if (int2 == 7) {
				return 1.8F;
			}

			if (int2 == 8) {
				return 1.9F;
			}

			if (int2 == 9) {
				return 2.0F;
			}

			if (int2 == 10) {
				return 100.0F;
			}
		}

		return 1.0F;
	}

	public String getAmmoType() {
		return this.ammoType;
	}

	public void setAmmoType(String string) {
		this.ammoType = string;
	}

	public boolean isAngleFalloff() {
		return this.angleFalloff;
	}

	public void setAngleFalloff(boolean boolean1) {
		this.angleFalloff = boolean1;
	}

	public boolean isCanBarracade() {
		return this.bCanBarracade;
	}

	public void setCanBarracade(boolean boolean1) {
		this.bCanBarracade = boolean1;
	}

	public boolean isDirectional() {
		return this.directional;
	}

	public void setDirectional(boolean boolean1) {
		this.directional = boolean1;
	}

	public float getDoSwingBeforeImpact() {
		return this.doSwingBeforeImpact;
	}

	public void setDoSwingBeforeImpact(float float1) {
		this.doSwingBeforeImpact = float1;
	}

	public String getImpactSound() {
		return this.impactSound;
	}

	public void setImpactSound(String string) {
		this.impactSound = string;
	}

	public boolean isKnockBackOnNoDeath() {
		return this.knockBackOnNoDeath;
	}

	public void setKnockBackOnNoDeath(boolean boolean1) {
		this.knockBackOnNoDeath = boolean1;
	}

	public float getMaxAngle() {
		return this.maxAngle;
	}

	public void setMaxAngle(float float1) {
		this.maxAngle = float1;
	}

	public float getMaxDamage() {
		return this.maxDamage;
	}

	public void setMaxDamage(float float1) {
		this.maxDamage = float1;
	}

	public int getMaxHitCount() {
		return this.maxHitCount;
	}

	public void setMaxHitCount(int int1) {
		this.maxHitCount = int1;
	}

	public float getMaxRange() {
		return this.maxRange;
	}

	public float getMaxRange(IsoGameCharacter gameCharacter) {
		return this.isRanged() ? this.maxRange + this.getAimingPerkRangeModifier() * (float)(gameCharacter.getPerkLevel(PerkFactory.Perks.Aiming) / 2) : this.maxRange;
	}

	public void setMaxRange(float float1) {
		this.maxRange = float1;
	}

	public boolean isRanged() {
		return this.ranged;
	}

	public void setRanged(boolean boolean1) {
		this.ranged = boolean1;
	}

	public float getMinAngle() {
		return this.minAngle;
	}

	public void setMinAngle(float float1) {
		this.minAngle = float1;
	}

	public float getMinDamage() {
		return this.minDamage;
	}

	public void setMinDamage(float float1) {
		this.minDamage = float1;
	}

	public float getMinimumSwingTime() {
		return this.minimumSwingTime;
	}

	public void setMinimumSwingTime(float float1) {
		this.minimumSwingTime = float1;
	}

	public float getMinRange() {
		return this.minRange;
	}

	public void setMinRange(float float1) {
		this.minRange = float1;
	}

	public float getNoiseFactor() {
		return this.noiseFactor;
	}

	public void setNoiseFactor(float float1) {
		this.noiseFactor = float1;
	}

	public String getOtherHandRequire() {
		return this.otherHandRequire;
	}

	public void setOtherHandRequire(String string) {
		this.otherHandRequire = string;
	}

	public boolean isOtherHandUse() {
		return this.otherHandUse;
	}

	public void setOtherHandUse(boolean boolean1) {
		this.otherHandUse = boolean1;
	}

	public String getPhysicsObject() {
		return this.physicsObject;
	}

	public void setPhysicsObject(String string) {
		this.physicsObject = string;
	}

	public float getPushBackMod() {
		return this.pushBackMod;
	}

	public void setPushBackMod(float float1) {
		this.pushBackMod = float1;
	}

	public boolean isRangeFalloff() {
		return this.rangeFalloff;
	}

	public void setRangeFalloff(boolean boolean1) {
		this.rangeFalloff = boolean1;
	}

	public boolean isShareDamage() {
		return this.shareDamage;
	}

	public void setShareDamage(boolean boolean1) {
		this.shareDamage = boolean1;
	}

	public int getSoundRadius() {
		return this.soundRadius;
	}

	public void setSoundRadius(int int1) {
		this.soundRadius = int1;
	}

	public int getSoundVolume() {
		return this.soundVolume;
	}

	public void setSoundVolume(int int1) {
		this.soundVolume = int1;
	}

	public boolean isSplatBloodOnNoDeath() {
		return this.splatBloodOnNoDeath;
	}

	public void setSplatBloodOnNoDeath(boolean boolean1) {
		this.splatBloodOnNoDeath = boolean1;
	}

	public int getSplatNumber() {
		return this.splatNumber;
	}

	public void setSplatNumber(int int1) {
		this.splatNumber = int1;
	}

	public String getSwingSound() {
		return this.swingSound;
	}

	public void setSwingSound(String string) {
		this.swingSound = string;
	}

	public float getSwingTime() {
		return this.swingTime;
	}

	public void setSwingTime(float float1) {
		this.swingTime = float1;
	}

	public float getToHitModifier() {
		return this.toHitModifier;
	}

	public void setToHitModifier(float float1) {
		this.toHitModifier = float1;
	}

	public boolean isUseEndurance() {
		return this.useEndurance;
	}

	public void setUseEndurance(boolean boolean1) {
		this.useEndurance = boolean1;
	}

	public boolean isUseSelf() {
		return this.useSelf;
	}

	public void setUseSelf(boolean boolean1) {
		this.useSelf = boolean1;
	}

	public String getWeaponSprite() {
		return this.weaponSprite;
	}

	public void setWeaponSprite(String string) {
		this.weaponSprite = string;
	}

	public float getOtherBoost() {
		return this.otherBoost;
	}

	public void setOtherBoost(float float1) {
		this.otherBoost = float1;
	}

	public int getDoorDamage() {
		return this.DoorDamage;
	}

	public void setDoorDamage(int int1) {
		this.DoorDamage = int1;
	}

	public String getDoorHitSound() {
		return this.doorHitSound;
	}

	public void setDoorHitSound(String string) {
		this.doorHitSound = string;
	}

	public int getConditionLowerChance() {
		return this.ConditionLowerChance;
	}

	public void setConditionLowerChance(int int1) {
		this.ConditionLowerChance = int1;
	}

	public boolean isMultipleHitConditionAffected() {
		return this.MultipleHitConditionAffected;
	}

	public void setMultipleHitConditionAffected(boolean boolean1) {
		this.MultipleHitConditionAffected = boolean1;
	}

	public boolean isShareEndurance() {
		return this.shareEndurance;
	}

	public void setShareEndurance(boolean boolean1) {
		this.shareEndurance = boolean1;
	}

	public boolean isAlwaysKnockdown() {
		return this.AlwaysKnockdown;
	}

	public void setAlwaysKnockdown(boolean boolean1) {
		this.AlwaysKnockdown = boolean1;
	}

	public float getEnduranceMod() {
		return this.EnduranceMod;
	}

	public void setEnduranceMod(float float1) {
		this.EnduranceMod = float1;
	}

	public float getKnockdownMod() {
		return this.KnockdownMod;
	}

	public void setKnockdownMod(float float1) {
		this.KnockdownMod = float1;
	}

	public boolean isCantAttackWithLowestEndurance() {
		return this.CantAttackWithLowestEndurance;
	}

	public void setCantAttackWithLowestEndurance(boolean boolean1) {
		this.CantAttackWithLowestEndurance = boolean1;
	}

	public boolean isAimedFirearm() {
		return this.bIsAimedFirearm;
	}

	public boolean isAimedHandWeapon() {
		return this.bIsAimedHandWeapon;
	}

	public int getProjectileCount() {
		return this.ProjectileCount;
	}

	public float getAimingMod() {
		return this.aimingMod;
	}

	public boolean isAimed() {
		return this.bIsAimedFirearm || this.bIsAimedHandWeapon;
	}

	public void setCriticalChance(float float1) {
		this.CriticalChance = float1;
	}

	public float getCriticalChance() {
		return this.CriticalChance;
	}

	public void setSubCategory(String string) {
		this.SubCategory = string;
	}

	public String getSubCategory() {
		return this.SubCategory;
	}

	public void setZombieHitSound(String string) {
		this.hitSound = string;
	}

	public String getZombieHitSound() {
		return this.hitSound;
	}

	public ArrayList getCategories() {
		return this.Categories;
	}

	public void setCategories(ArrayList arrayList) {
		this.Categories = arrayList;
	}

	public int getAimingPerkCritModifier() {
		return this.AimingPerkCritModifier;
	}

	public void setAimingPerkCritModifier(int int1) {
		this.AimingPerkCritModifier = int1;
	}

	public float getAimingPerkRangeModifier() {
		return this.AimingPerkRangeModifier;
	}

	public void setAimingPerkRangeModifier(float float1) {
		this.AimingPerkRangeModifier = float1;
	}

	public int getHitChance() {
		return this.HitChance;
	}

	public void setHitChance(int int1) {
		this.HitChance = int1;
	}

	public float getAimingPerkHitChanceModifier() {
		return this.AimingPerkHitChanceModifier;
	}

	public void setAimingPerkHitChanceModifier(float float1) {
		this.AimingPerkHitChanceModifier = float1;
	}

	public float getAimingPerkMinAngleModifier() {
		return this.AimingPerkMinAngleModifier;
	}

	public void setAimingPerkMinAngleModifier(float float1) {
		this.AimingPerkMinAngleModifier = float1;
	}

	public int getRecoilDelay() {
		return this.RecoilDelay;
	}

	public void setRecoilDelay(int int1) {
		this.RecoilDelay = int1;
	}

	public boolean isPiercingBullets() {
		return this.PiercingBullets;
	}

	public void setPiercingBullets(boolean boolean1) {
		this.PiercingBullets = boolean1;
	}

	public float getSoundGain() {
		return this.soundGain;
	}

	public void setSoundGain(float float1) {
		this.soundGain = float1;
	}

	public WeaponPart getScope() {
		return this.scope;
	}

	public void setScope(WeaponPart weaponPart) {
		this.scope = weaponPart;
	}

	public WeaponPart getClip() {
		return this.clip;
	}

	public void setClip(WeaponPart weaponPart) {
		this.clip = weaponPart;
	}

	public WeaponPart getCanon() {
		return this.canon;
	}

	public void setCanon(WeaponPart weaponPart) {
		this.canon = weaponPart;
	}

	public WeaponPart getRecoilpad() {
		return this.recoilpad;
	}

	public void setRecoilpad(WeaponPart weaponPart) {
		this.recoilpad = weaponPart;
	}

	public int getClipSize() {
		return this.ClipSize;
	}

	public void setClipSize(int int1) {
		this.ClipSize = int1;
		this.getModData().rawset("maxCapacity", (new Integer(int1)).doubleValue());
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		byteBuffer.putFloat(this.maxRange);
		byteBuffer.putFloat(this.minRangeRanged);
		byteBuffer.putInt(this.ClipSize);
		byteBuffer.putFloat(this.minDamage);
		byteBuffer.putFloat(this.maxDamage);
		byteBuffer.putInt(this.RecoilDelay);
		byteBuffer.putInt(this.aimingTime);
		byteBuffer.putInt(this.reloadTime);
		byteBuffer.putInt(this.HitChance);
		byteBuffer.putFloat(this.minAngle);
		if (this.getScope() != null) {
			byteBuffer.put((byte)1);
			GameWindow.WriteString(byteBuffer, this.getScope().getModule() + "." + this.getScope().getType());
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.getClip() != null) {
			byteBuffer.put((byte)1);
			GameWindow.WriteString(byteBuffer, this.getClip().getModule() + "." + this.getClip().getType());
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.getRecoilpad() != null) {
			byteBuffer.put((byte)1);
			GameWindow.WriteString(byteBuffer, this.getRecoilpad().getModule() + "." + this.getRecoilpad().getType());
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.getSling() != null) {
			byteBuffer.put((byte)1);
			GameWindow.WriteString(byteBuffer, this.getSling().getModule() + "." + this.getSling().getType());
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.getStock() != null) {
			byteBuffer.put((byte)1);
			GameWindow.WriteString(byteBuffer, this.getStock().getModule() + "." + this.getStock().getType());
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.getCanon() != null) {
			byteBuffer.put((byte)1);
			GameWindow.WriteString(byteBuffer, this.getCanon().getModule() + "." + this.getCanon().getType());
		} else {
			byteBuffer.put((byte)0);
		}

		byteBuffer.putInt(this.getExplosionTimer());
		byteBuffer.putFloat(this.maxAngle);
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		if (int1 >= 36) {
			this.setMaxRange(byteBuffer.getFloat());
			this.setMinRangeRanged(byteBuffer.getFloat());
			this.setClipSize(byteBuffer.getInt());
			this.setMinDamage(byteBuffer.getFloat());
			this.setMaxDamage(byteBuffer.getFloat());
			this.setRecoilDelay(byteBuffer.getInt());
			this.setAimingTime(byteBuffer.getInt());
			this.setReloadTime(byteBuffer.getInt());
			this.setHitChance(byteBuffer.getInt());
			this.setMinAngle(byteBuffer.getFloat());
			if (byteBuffer.get() == 1) {
				this.attachWeaponPart((WeaponPart)InventoryItemFactory.CreateItem(GameWindow.ReadString(byteBuffer)), false);
			}

			if (byteBuffer.get() == 1) {
				this.attachWeaponPart((WeaponPart)InventoryItemFactory.CreateItem(GameWindow.ReadString(byteBuffer)), false);
			}

			if (byteBuffer.get() == 1) {
				this.attachWeaponPart((WeaponPart)InventoryItemFactory.CreateItem(GameWindow.ReadString(byteBuffer)), false);
			}

			if (byteBuffer.get() == 1) {
				this.attachWeaponPart((WeaponPart)InventoryItemFactory.CreateItem(GameWindow.ReadString(byteBuffer)), false);
			}

			if (byteBuffer.get() == 1) {
				this.attachWeaponPart((WeaponPart)InventoryItemFactory.CreateItem(GameWindow.ReadString(byteBuffer)), false);
			}

			if (byteBuffer.get() == 1) {
				this.attachWeaponPart((WeaponPart)InventoryItemFactory.CreateItem(GameWindow.ReadString(byteBuffer)), false);
			}

			if (int1 >= 62) {
				this.setExplosionTimer(byteBuffer.getInt());
			}
		}

		if (int1 >= 105) {
			this.setMaxAngle(byteBuffer.getFloat());
		}
	}

	public float getMinRangeRanged() {
		return this.minRangeRanged;
	}

	public void setMinRangeRanged(float float1) {
		this.minRangeRanged = float1;
	}

	public int getReloadTime() {
		return this.reloadTime;
	}

	public void setReloadTime(int int1) {
		this.reloadTime = int1;
	}

	public WeaponPart getSling() {
		return this.sling;
	}

	public void setSling(WeaponPart weaponPart) {
		this.sling = weaponPart;
	}

	public int getAimingTime() {
		return this.aimingTime;
	}

	public void setAimingTime(int int1) {
		this.aimingTime = int1;
	}

	public WeaponPart getStock() {
		return this.stock;
	}

	public void setStock(WeaponPart weaponPart) {
		this.stock = weaponPart;
	}

	public int getTreeDamage() {
		return this.treeDamage;
	}

	public void setTreeDamage(int int1) {
		this.treeDamage = int1;
	}

	public String getBulletOutSound() {
		return this.bulletOutSound;
	}

	public void setBulletOutSound(String string) {
		this.bulletOutSound = string;
	}

	public String getShellFallSound() {
		return this.shellFallSound;
	}

	public void setShellFallSound(String string) {
		this.shellFallSound = string;
	}

	public void setWeaponPart(String string, WeaponPart weaponPart) {
		if (weaponPart == null || string.equals(weaponPart.getPartType())) {
			if (string.equals(Translator.getText("Tooltip_weapon_Scope"))) {
				this.scope = weaponPart;
			} else if (string.equals(Translator.getText("Tooltip_weapon_Clip"))) {
				this.clip = weaponPart;
			} else if (string.equals(Translator.getText("Tooltip_weapon_Sling"))) {
				this.sling = weaponPart;
			} else if (string.equals(Translator.getText("Tooltip_weapon_Canon"))) {
				this.canon = weaponPart;
			} else if (string.equals(Translator.getText("Tooltip_weapon_Stock"))) {
				this.stock = weaponPart;
			} else if (string.equals(Translator.getText("Tooltip_weapon_RecoilPad"))) {
				this.recoilpad = weaponPart;
			} else {
				DebugLog.log("ERROR: unknown WeaponPart type \"" + string + "\"");
			}
		}
	}

	public WeaponPart getWeaponPart(String string) {
		if (string.equals(Translator.getText("Tooltip_weapon_Scope"))) {
			return this.scope;
		} else if (string.equals(Translator.getText("Tooltip_weapon_Clip"))) {
			return this.clip;
		} else if (string.equals(Translator.getText("Tooltip_weapon_Sling"))) {
			return this.sling;
		} else if (string.equals(Translator.getText("Tooltip_weapon_Canon"))) {
			return this.canon;
		} else if (string.equals(Translator.getText("Tooltip_weapon_Stock"))) {
			return this.stock;
		} else if (string.equals(Translator.getText("Tooltip_weapon_RecoilPad"))) {
			return this.recoilpad;
		} else {
			DebugLog.log("ERROR: unknown WeaponPart type \"" + string + "\"");
			return null;
		}
	}

	public void attachWeaponPart(WeaponPart weaponPart) {
		this.attachWeaponPart(weaponPart, true);
	}

	public void attachWeaponPart(WeaponPart weaponPart, boolean boolean1) {
		if (weaponPart != null) {
			WeaponPart weaponPart2 = this.getWeaponPart(weaponPart.getPartType());
			if (weaponPart2 != null) {
				this.detachWeaponPart(weaponPart2);
			}

			this.setWeaponPart(weaponPart.getPartType(), weaponPart);
			if (boolean1) {
				this.setMaxRange(this.getMaxRange() + weaponPart.getMaxRange());
				this.setMinRangeRanged(this.getMinRangeRanged() + weaponPart.getMinRangeRanged());
				this.setClipSize(this.getClipSize() + weaponPart.getClipSize());
				this.setReloadTime(this.getReloadTime() + weaponPart.getReloadTime());
				this.setRecoilDelay((int)((float)this.getRecoilDelay() + weaponPart.getRecoilDelay()));
				this.setAimingTime(this.getAimingTime() + weaponPart.getAimingTime());
				this.setHitChance(this.getHitChance() + weaponPart.getHitChance());
				this.setMinAngle(this.getMinAngle() + weaponPart.getAngle());
				this.setActualWeight(this.getActualWeight() + weaponPart.getWeightModifier());
				this.setWeight(this.getWeight() + weaponPart.getWeightModifier());
				this.setMinDamage(this.getMinDamage() + weaponPart.getDamage());
				this.setMaxDamage(this.getMaxDamage() + weaponPart.getDamage());
			}
		}
	}

	public void detachWeaponPart(WeaponPart weaponPart) {
		if (weaponPart != null) {
			WeaponPart weaponPart2 = this.getWeaponPart(weaponPart.getPartType());
			if (weaponPart2 == weaponPart) {
				this.setWeaponPart(weaponPart.getPartType(), (WeaponPart)null);
				this.setMaxRange(this.getMaxRange() - weaponPart.getMaxRange());
				this.setMinRangeRanged(this.getMinRangeRanged() - weaponPart.getMinRangeRanged());
				this.setClipSize(this.getClipSize() - weaponPart.getClipSize());
				this.setReloadTime(this.getReloadTime() - weaponPart.getReloadTime());
				this.setRecoilDelay((int)((float)this.getRecoilDelay() - weaponPart.getRecoilDelay()));
				this.setAimingTime(this.getAimingTime() - weaponPart.getAimingTime());
				this.setHitChance(this.getHitChance() - weaponPart.getHitChance());
				this.setMinAngle(this.getMinAngle() - weaponPart.getAngle());
				this.setActualWeight(this.getActualWeight() - weaponPart.getWeightModifier());
				this.setWeight(this.getWeight() - weaponPart.getWeightModifier());
				this.setMinDamage(this.getMinDamage() - weaponPart.getDamage());
				this.setMaxDamage(this.getMaxDamage() - weaponPart.getDamage());
			}
		}
	}

	public int getTriggerExplosionTimer() {
		return this.triggerExplosionTimer;
	}

	public void setTriggerExplosionTimer(int int1) {
		this.triggerExplosionTimer = int1;
	}

	public boolean canBePlaced() {
		return this.canBePlaced;
	}

	public void setCanBePlaced(boolean boolean1) {
		this.canBePlaced = boolean1;
	}

	public int getExplosionRange() {
		return this.explosionRange;
	}

	public void setExplosionRange(int int1) {
		this.explosionRange = int1;
	}

	public int getExplosionPower() {
		return this.explosionPower;
	}

	public void setExplosionPower(int int1) {
		this.explosionPower = int1;
	}

	public int getFireRange() {
		return this.fireRange;
	}

	public void setFireRange(int int1) {
		this.fireRange = int1;
	}

	public int getSmokeRange() {
		return this.smokeRange;
	}

	public void setSmokeRange(int int1) {
		this.smokeRange = int1;
	}

	public int getFirePower() {
		return this.firePower;
	}

	public void setFirePower(int int1) {
		this.firePower = int1;
	}

	public int getNoiseRange() {
		return this.noiseRange;
	}

	public void setNoiseRange(int int1) {
		this.noiseRange = int1;
	}

	public float getExtraDamage() {
		return this.extraDamage;
	}

	public void setExtraDamage(float float1) {
		this.extraDamage = float1;
	}

	public int getExplosionTimer() {
		return this.explosionTimer;
	}

	public void setExplosionTimer(int int1) {
		this.explosionTimer = int1;
	}

	public String getPlacedSprite() {
		return this.placedSprite;
	}

	public void setPlacedSprite(String string) {
		this.placedSprite = string;
	}

	public boolean canBeReused() {
		return this.canBeReused;
	}

	public void setCanBeReused(boolean boolean1) {
		this.canBeReused = boolean1;
	}

	public int getSensorRange() {
		return this.sensorRange;
	}

	public void setSensorRange(int int1) {
		this.sensorRange = int1;
	}

	public String getRunAnim() {
		return this.RunAnim;
	}
}
