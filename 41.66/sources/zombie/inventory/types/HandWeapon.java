package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorDesc;
import zombie.characters.skills.PerkFactory;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemType;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;
import zombie.util.StringUtils;
import zombie.util.io.BitHeader;
import zombie.util.io.BitHeaderRead;
import zombie.util.io.BitHeaderWrite;


public final class HandWeapon extends InventoryItem {
	public float WeaponLength;
	public float SplatSize = 1.0F;
	private int ammoPerShoot = 1;
	private String magazineType = null;
	protected boolean angleFalloff = false;
	protected boolean bCanBarracade = false;
	protected float doSwingBeforeImpact = 0.0F;
	protected String impactSound = "BaseballBatHit";
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
	protected String swingSound = "BaseballBatSwing";
	protected float swingTime = 1.0F;
	protected float toHitModifier = 1.0F;
	protected boolean useEndurance = true;
	protected boolean useSelf = false;
	protected String weaponSprite = null;
	private String originalWeaponSprite = null;
	protected float otherBoost = 1.0F;
	protected int DoorDamage = 1;
	protected String doorHitSound = "BaseballBatHit";
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
	private float critDmgMultiplier = 2.0F;
	private float baseSpeed = 1.0F;
	private float bloodLevel = 0.0F;
	private String ammoBox = null;
	private String insertAmmoStartSound = null;
	private String insertAmmoSound = null;
	private String insertAmmoStopSound = null;
	private String ejectAmmoStartSound = null;
	private String ejectAmmoSound = null;
	private String ejectAmmoStopSound = null;
	private String rackSound = null;
	private String clickSound = "Stormy9mmClick";
	private boolean containsClip = false;
	private String weaponReloadType = "handgun";
	private boolean rackAfterShoot = false;
	private boolean roundChambered = false;
	private boolean bSpentRoundChambered = false;
	private int spentRoundCount = 0;
	private float jamGunChance = 5.0F;
	private boolean isJammed = false;
	private ArrayList modelWeaponPart = null;
	private boolean haveChamber = true;
	private String bulletName = null;
	private String damageCategory = null;
	private boolean damageMakeHole = false;
	private String hitFloorSound = "BatOnFloor";
	private boolean insertAllBulletsReload = false;
	private String fireMode = null;
	private ArrayList fireModePossibilities = null;
	public int ProjectileCount = 1;
	public float aimingMod = 1.0F;
	public float CriticalChance = 20.0F;
	private String hitSound = "BaseballBatHit";

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

	public boolean IsWeapon() {
		return true;
	}

	public int getSaveType() {
		return Item.Type.Weapon.ordinal();
	}

	public float getScore(SurvivorDesc survivorDesc) {
		float float1 = 0.0F;
		if (this.getAmmoType() != null && !this.getAmmoType().equals("none") && !this.container.contains(this.getAmmoType())) {
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

	public float getContentsWeight() {
		float float1 = 0.0F;
		Item item;
		if (this.haveChamber() && this.isRoundChambered() && !StringUtils.isNullOrWhitespace(this.getAmmoType())) {
			item = ScriptManager.instance.FindItem(this.getAmmoType());
			if (item != null) {
				float1 += item.getActualWeight();
			}
		}

		if (this.isContainsClip() && !StringUtils.isNullOrWhitespace(this.getMagazineType())) {
			item = ScriptManager.instance.FindItem(this.getMagazineType());
			if (item != null) {
				float1 += item.getActualWeight();
			}
		}

		return float1 + super.getContentsWeight();
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
			float9 = this.getMaxRange(IsoPlayer.getInstance());
			float10 = 40.0F;
			float11 = float9 / float10;
			layoutItem.setProgress(float11, float5, float6, float7, float8);
		}

		if (this.isTwoHandWeapon() && !this.isRequiresEquippedBothHands()) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_item_TwoHandWeapon"), float1, float2, float3, float4);
		}

		if (!StringUtils.isNullOrEmpty(this.getFireMode())) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_item_FireMode") + ":", float1, float2, float3, float4);
			layoutItem.setValue(Translator.getText("ContextMenu_FireMode_" + this.getFireMode()), 1.0F, 1.0F, 1.0F, 1.0F);
		}

		if (this.CantAttackWithLowestEndurance) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_weapon_Unusable_at_max_exertion"), 1.0F, 0.0F, 0.0F, 1.0F);
		}

		String string = this.getAmmoType();
		if (Core.getInstance().isNewReloading()) {
			String string2;
			if (this.getMaxAmmo() > 0) {
				string2 = String.valueOf(this.getCurrentAmmoCount());
				if (this.isRoundChambered()) {
					string2 = string2 + "+1";
				}

				layoutItem = layout.addItem();
				if (this.bulletName == null) {
					if (this.getMagazineType() != null) {
						this.bulletName = InventoryItemFactory.CreateItem(this.getMagazineType()).getDisplayName();
					} else {
						this.bulletName = InventoryItemFactory.CreateItem(this.getAmmoType()).getDisplayName();
					}
				}

				layoutItem.setLabel(this.bulletName + ":", 1.0F, 1.0F, 0.8F, 1.0F);
				layoutItem.setValue(string2 + " / " + this.getMaxAmmo(), 1.0F, 1.0F, 1.0F, 1.0F);
			}

			if (this.isJammed()) {
				layoutItem = layout.addItem();
				layoutItem.setLabel(Translator.getText("Tooltip_weapon_Jammed"), 1.0F, 0.1F, 0.1F, 1.0F);
			} else if (this.haveChamber() && !this.isRoundChambered() && this.getCurrentAmmoCount() > 0) {
				layoutItem = layout.addItem();
				string2 = this.isSpentRoundChambered() ? "Tooltip_weapon_SpentRoundChambered" : "Tooltip_weapon_NoRoundChambered";
				layoutItem.setLabel(Translator.getText(string2), 1.0F, 0.1F, 0.1F, 1.0F);
			} else if (this.getSpentRoundCount() > 0) {
				layoutItem = layout.addItem();
				layoutItem.setLabel(Translator.getText("Tooltip_weapon_SpentRounds") + ":", 1.0F, 0.1F, 0.1F, 1.0F);
				layoutItem.setValue(this.getSpentRoundCount() + " / " + this.getMaxAmmo(), 1.0F, 1.0F, 1.0F, 1.0F);
			}

			if (!StringUtils.isNullOrEmpty(this.getMagazineType())) {
				if (this.isContainsClip()) {
					layoutItem = layout.addItem();
					layoutItem.setLabel(Translator.getText("Tooltip_weapon_ContainsClip"), 1.0F, 1.0F, 0.8F, 1.0F);
				} else {
					layoutItem = layout.addItem();
					layoutItem.setLabel(Translator.getText("Tooltip_weapon_NoClip"), 1.0F, 1.0F, 0.8F, 1.0F);
				}
			}
		} else {
			if (string == null && this.hasModData()) {
				Object object = this.getModData().rawget("defaultAmmo");
				if (object instanceof String) {
					string = (String)object;
				}
			}

			if (string != null) {
				Item item = ScriptManager.instance.FindItem(string);
				if (item == null) {
					ScriptManager scriptManager = ScriptManager.instance;
					String string3 = this.getModule();
					item = scriptManager.FindItem(string3 + "." + string);
				}

				if (item != null) {
					layoutItem = layout.addItem();
					layoutItem.setLabel(Translator.getText("Tooltip_weapon_Ammo") + ":", float1, float2, float3, float4);
					layoutItem.setValue(item.getDisplayName(), 1.0F, 1.0F, 1.0F, 1.0F);
				}

				Object object2 = this.getModData().rawget("currentCapacity");
				Object object3 = this.getModData().rawget("maxCapacity");
				if (object2 instanceof Double && object3 instanceof Double) {
					int int1 = ((Double)object2).intValue();
					String string4 = int1 + " / " + ((Double)object3).intValue();
					Object object4 = this.getModData().rawget("roundChambered");
					if (object4 instanceof Double && ((Double)object4).intValue() == 1) {
						int1 = ((Double)object2).intValue();
						string4 = int1 + "+1 / " + ((Double)object3).intValue();
					} else {
						Object object5 = this.getModData().rawget("emptyShellChambered");
						if (object5 instanceof Double && ((Double)object5).intValue() == 1) {
							int1 = ((Double)object2).intValue();
							string4 = int1 + "+x / " + ((Double)object3).intValue();
						}
					}

					layoutItem = layout.addItem();
					layoutItem.setLabel(Translator.getText("Tooltip_weapon_AmmoCount") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
					layoutItem.setValue(string4, 1.0F, 1.0F, 1.0F, 1.0F);
				}
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

		int int3 = gameCharacter.getPerkLevel(PerkFactory.Perks.Spear);
		if (this.ScriptItem.Categories.contains("Spear")) {
			if (int3 >= 3 && int3 <= 6) {
				return 1.1F;
			}

			if (int3 >= 7) {
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
			if (this.ScriptItem.Categories.contains("Axe") && int2 >= 7) {
				return 1.2F;
			} else {
				int int3 = gameCharacter.getPerkLevel(PerkFactory.Perks.Spear);
				return this.ScriptItem.Categories.contains("Spear") && int3 >= 7 ? 1.2F : 1.0F;
			}
		}
	}

	public float getFatigueMod(IsoGameCharacter gameCharacter) {
		int int1 = gameCharacter.getPerkLevel(PerkFactory.Perks.Blunt);
		if (this.ScriptItem.Categories.contains("Blunt") && int1 >= 8) {
			return 0.8F;
		} else {
			int int2 = gameCharacter.getPerkLevel(PerkFactory.Perks.Axe);
			if (this.ScriptItem.Categories.contains("Axe") && int2 >= 8) {
				return 0.8F;
			} else {
				int int3 = gameCharacter.getPerkLevel(PerkFactory.Perks.Spear);
				return this.ScriptItem.Categories.contains("Spear") && int3 >= 8 ? 0.8F : 1.0F;
			}
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
			if (gameCharacter.Traits.Axeman.isSet()) {
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
			if (this.ScriptItem.Categories.contains("Spear")) {
				int1 = gameCharacter.getPerkLevel(PerkFactory.Perks.Spear);
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

		int int3 = gameCharacter.getPerkLevel(PerkFactory.Perks.Spear);
		if (this.ScriptItem.Categories.contains("Spear")) {
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
		return this.isRanged() ? this.maxRange + this.getAimingPerkRangeModifier() * ((float)gameCharacter.getPerkLevel(PerkFactory.Perks.Aiming) / 2.0F) : this.maxRange;
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

	public void setProjectileCount(int int1) {
		this.ProjectileCount = int1;
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
		this.getModData().rawset("maxCapacity", BoxedStaticValues.toDouble((double)int1));
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		BitHeaderWrite bitHeaderWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Integer, byteBuffer);
		if (this.maxRange != 1.0F) {
			bitHeaderWrite.addFlags(1);
			byteBuffer.putFloat(this.maxRange);
		}

		if (this.minRangeRanged != 0.0F) {
			bitHeaderWrite.addFlags(2);
			byteBuffer.putFloat(this.minRangeRanged);
		}

		if (this.ClipSize != 0) {
			bitHeaderWrite.addFlags(4);
			byteBuffer.putInt(this.ClipSize);
		}

		if (this.minDamage != 0.4F) {
			bitHeaderWrite.addFlags(8);
			byteBuffer.putFloat(this.minDamage);
		}

		if (this.maxDamage != 1.5F) {
			bitHeaderWrite.addFlags(16);
			byteBuffer.putFloat(this.maxDamage);
		}

		if (this.RecoilDelay != 0) {
			bitHeaderWrite.addFlags(32);
			byteBuffer.putInt(this.RecoilDelay);
		}

		if (this.aimingTime != 0) {
			bitHeaderWrite.addFlags(64);
			byteBuffer.putInt(this.aimingTime);
		}

		if (this.reloadTime != 0) {
			bitHeaderWrite.addFlags(128);
			byteBuffer.putInt(this.reloadTime);
		}

		if (this.HitChance != 0) {
			bitHeaderWrite.addFlags(256);
			byteBuffer.putInt(this.HitChance);
		}

		if (this.minAngle != 0.5F) {
			bitHeaderWrite.addFlags(512);
			byteBuffer.putFloat(this.minAngle);
		}

		if (this.getScope() != null) {
			bitHeaderWrite.addFlags(1024);
			byteBuffer.putShort(this.getScope().getRegistry_id());
		}

		if (this.getClip() != null) {
			bitHeaderWrite.addFlags(2048);
			byteBuffer.putShort(this.getClip().getRegistry_id());
		}

		if (this.getRecoilpad() != null) {
			bitHeaderWrite.addFlags(4096);
			byteBuffer.putShort(this.getRecoilpad().getRegistry_id());
		}

		if (this.getSling() != null) {
			bitHeaderWrite.addFlags(8192);
			byteBuffer.putShort(this.getSling().getRegistry_id());
		}

		if (this.getStock() != null) {
			bitHeaderWrite.addFlags(16384);
			byteBuffer.putShort(this.getStock().getRegistry_id());
		}

		if (this.getCanon() != null) {
			bitHeaderWrite.addFlags(32768);
			byteBuffer.putShort(this.getCanon().getRegistry_id());
		}

		if (this.getExplosionTimer() != 0) {
			bitHeaderWrite.addFlags(65536);
			byteBuffer.putInt(this.getExplosionTimer());
		}

		if (this.maxAngle != 1.0F) {
			bitHeaderWrite.addFlags(131072);
			byteBuffer.putFloat(this.maxAngle);
		}

		if (this.bloodLevel != 0.0F) {
			bitHeaderWrite.addFlags(262144);
			byteBuffer.putFloat(this.bloodLevel);
		}

		if (this.containsClip) {
			bitHeaderWrite.addFlags(524288);
		}

		if (this.roundChambered) {
			bitHeaderWrite.addFlags(1048576);
		}

		if (this.isJammed) {
			bitHeaderWrite.addFlags(2097152);
		}

		bitHeaderWrite.write();
		bitHeaderWrite.release();
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		BitHeaderRead bitHeaderRead = BitHeader.allocRead(BitHeader.HeaderSize.Integer, byteBuffer);
		if (!bitHeaderRead.equals(0)) {
			if (bitHeaderRead.hasFlags(1)) {
				this.setMaxRange(byteBuffer.getFloat());
			}

			if (bitHeaderRead.hasFlags(2)) {
				this.setMinRangeRanged(byteBuffer.getFloat());
			}

			if (bitHeaderRead.hasFlags(4)) {
				this.setClipSize(byteBuffer.getInt());
			}

			if (bitHeaderRead.hasFlags(8)) {
				this.setMinDamage(byteBuffer.getFloat());
			}

			if (bitHeaderRead.hasFlags(16)) {
				this.setMaxDamage(byteBuffer.getFloat());
			}

			if (bitHeaderRead.hasFlags(32)) {
				this.setRecoilDelay(byteBuffer.getInt());
			}

			if (bitHeaderRead.hasFlags(64)) {
				this.setAimingTime(byteBuffer.getInt());
			}

			if (bitHeaderRead.hasFlags(128)) {
				this.setReloadTime(byteBuffer.getInt());
			}

			if (bitHeaderRead.hasFlags(256)) {
				this.setHitChance(byteBuffer.getInt());
			}

			if (bitHeaderRead.hasFlags(512)) {
				this.setMinAngle(byteBuffer.getFloat());
			}

			InventoryItem inventoryItem;
			if (bitHeaderRead.hasFlags(1024)) {
				inventoryItem = InventoryItemFactory.CreateItem(byteBuffer.getShort());
				if (inventoryItem != null && inventoryItem instanceof WeaponPart) {
					this.attachWeaponPart((WeaponPart)inventoryItem, false);
				}
			}

			if (bitHeaderRead.hasFlags(2048)) {
				inventoryItem = InventoryItemFactory.CreateItem(byteBuffer.getShort());
				if (inventoryItem != null && inventoryItem instanceof WeaponPart) {
					this.attachWeaponPart((WeaponPart)inventoryItem, false);
				}
			}

			if (bitHeaderRead.hasFlags(4096)) {
				inventoryItem = InventoryItemFactory.CreateItem(byteBuffer.getShort());
				if (inventoryItem != null && inventoryItem instanceof WeaponPart) {
					this.attachWeaponPart((WeaponPart)inventoryItem, false);
				}
			}

			if (bitHeaderRead.hasFlags(8192)) {
				inventoryItem = InventoryItemFactory.CreateItem(byteBuffer.getShort());
				if (inventoryItem != null && inventoryItem instanceof WeaponPart) {
					this.attachWeaponPart((WeaponPart)inventoryItem, false);
				}
			}

			if (bitHeaderRead.hasFlags(16384)) {
				inventoryItem = InventoryItemFactory.CreateItem(byteBuffer.getShort());
				if (inventoryItem != null && inventoryItem instanceof WeaponPart) {
					this.attachWeaponPart((WeaponPart)inventoryItem, false);
				}
			}

			if (bitHeaderRead.hasFlags(32768)) {
				inventoryItem = InventoryItemFactory.CreateItem(byteBuffer.getShort());
				if (inventoryItem != null && inventoryItem instanceof WeaponPart) {
					this.attachWeaponPart((WeaponPart)inventoryItem, false);
				}
			}

			if (bitHeaderRead.hasFlags(65536)) {
				this.setExplosionTimer(byteBuffer.getInt());
			}

			if (bitHeaderRead.hasFlags(131072)) {
				this.setMaxAngle(byteBuffer.getFloat());
			}

			if (bitHeaderRead.hasFlags(262144)) {
				this.setBloodLevel(byteBuffer.getFloat());
			}

			this.setContainsClip(bitHeaderRead.hasFlags(524288));
			if (StringUtils.isNullOrWhitespace(this.magazineType)) {
				this.setContainsClip(false);
			}

			this.setRoundChambered(bitHeaderRead.hasFlags(1048576));
			this.setJammed(bitHeaderRead.hasFlags(2097152));
		}

		bitHeaderRead.release();
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

	private void addPartToList(String string, ArrayList arrayList) {
		WeaponPart weaponPart = this.getWeaponPart(string);
		if (weaponPart != null) {
			arrayList.add(weaponPart);
		}
	}

	public ArrayList getAllWeaponParts() {
		ArrayList arrayList = new ArrayList();
		this.addPartToList("Scope", arrayList);
		this.addPartToList("Clip", arrayList);
		this.addPartToList("Sling", arrayList);
		this.addPartToList("Canon", arrayList);
		this.addPartToList("Stock", arrayList);
		this.addPartToList("RecoilPad", arrayList);
		return arrayList;
	}

	public void setWeaponPart(String string, WeaponPart weaponPart) {
		if (weaponPart == null || string.equalsIgnoreCase(weaponPart.getPartType())) {
			if ("Scope".equalsIgnoreCase(string)) {
				this.scope = weaponPart;
			} else if ("Clip".equalsIgnoreCase(string)) {
				this.clip = weaponPart;
			} else if ("Sling".equalsIgnoreCase(string)) {
				this.sling = weaponPart;
			} else if ("Canon".equalsIgnoreCase(string)) {
				this.canon = weaponPart;
			} else if ("Stock".equalsIgnoreCase(string)) {
				this.stock = weaponPart;
			} else if ("RecoilPad".equalsIgnoreCase(string)) {
				this.recoilpad = weaponPart;
			} else {
				DebugLog.log("ERROR: unknown WeaponPart type \"" + string + "\"");
			}
		}
	}

	public WeaponPart getWeaponPart(String string) {
		if ("Scope".equalsIgnoreCase(string)) {
			return this.scope;
		} else if ("Clip".equalsIgnoreCase(string)) {
			return this.clip;
		} else if ("Sling".equalsIgnoreCase(string)) {
			return this.sling;
		} else if ("Canon".equalsIgnoreCase(string)) {
			return this.canon;
		} else if ("Stock".equalsIgnoreCase(string)) {
			return this.stock;
		} else if ("RecoilPad".equalsIgnoreCase(string)) {
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

	public int getNoiseDuration() {
		return this.getScriptItem().getNoiseDuration();
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

	public float getCritDmgMultiplier() {
		return this.critDmgMultiplier;
	}

	public void setCritDmgMultiplier(float float1) {
		this.critDmgMultiplier = float1;
	}

	public String getStaticModel() {
		return this.staticModel != null ? this.staticModel : this.weaponSprite;
	}

	public float getBaseSpeed() {
		return this.baseSpeed;
	}

	public void setBaseSpeed(float float1) {
		this.baseSpeed = float1;
	}

	public float getBloodLevel() {
		return this.bloodLevel;
	}

	public void setBloodLevel(float float1) {
		this.bloodLevel = Math.max(0.0F, Math.min(1.0F, float1));
	}

	public void setWeaponLength(float float1) {
		this.WeaponLength = float1;
	}

	public String getAmmoBox() {
		return this.ammoBox;
	}

	public void setAmmoBox(String string) {
		this.ammoBox = string;
	}

	public String getMagazineType() {
		return this.magazineType;
	}

	public void setMagazineType(String string) {
		this.magazineType = string;
	}

	public String getEjectAmmoStartSound() {
		return this.getScriptItem().getEjectAmmoStartSound();
	}

	public String getEjectAmmoSound() {
		return this.getScriptItem().getEjectAmmoSound();
	}

	public String getEjectAmmoStopSound() {
		return this.getScriptItem().getEjectAmmoStopSound();
	}

	public String getInsertAmmoStartSound() {
		return this.getScriptItem().getInsertAmmoStartSound();
	}

	public String getInsertAmmoSound() {
		return this.getScriptItem().getInsertAmmoSound();
	}

	public String getInsertAmmoStopSound() {
		return this.getScriptItem().getInsertAmmoStopSound();
	}

	public String getRackSound() {
		return this.rackSound;
	}

	public void setRackSound(String string) {
		this.rackSound = string;
	}

	public boolean isReloadable(IsoGameCharacter gameCharacter) {
		return this.isRanged();
	}

	public boolean isContainsClip() {
		return this.containsClip;
	}

	public void setContainsClip(boolean boolean1) {
		this.containsClip = boolean1;
	}

	public InventoryItem getBestMagazine(IsoGameCharacter gameCharacter) {
		if (StringUtils.isNullOrEmpty(this.getMagazineType())) {
			return null;
		} else {
			InventoryItem inventoryItem = gameCharacter.getInventory().getBestTypeRecurse(this.getMagazineType(), (var0,gameCharacterx)->{
				return var0.getCurrentAmmoCount() - gameCharacterx.getCurrentAmmoCount();
			});

			return inventoryItem != null && inventoryItem.getCurrentAmmoCount() != 0 ? inventoryItem : null;
		}
	}

	public String getWeaponReloadType() {
		return this.weaponReloadType;
	}

	public void setWeaponReloadType(String string) {
		this.weaponReloadType = string;
	}

	public boolean isRackAfterShoot() {
		return this.rackAfterShoot;
	}

	public void setRackAfterShoot(boolean boolean1) {
		this.rackAfterShoot = boolean1;
	}

	public boolean isRoundChambered() {
		return this.roundChambered;
	}

	public void setRoundChambered(boolean boolean1) {
		this.roundChambered = boolean1;
	}

	public boolean isSpentRoundChambered() {
		return this.bSpentRoundChambered;
	}

	public void setSpentRoundChambered(boolean boolean1) {
		this.bSpentRoundChambered = boolean1;
	}

	public int getSpentRoundCount() {
		return this.spentRoundCount;
	}

	public void setSpentRoundCount(int int1) {
		this.spentRoundCount = PZMath.clamp(int1, 0, this.getMaxAmmo());
	}

	public boolean isManuallyRemoveSpentRounds() {
		return this.getScriptItem().isManuallyRemoveSpentRounds();
	}

	public int getAmmoPerShoot() {
		return this.ammoPerShoot;
	}

	public void setAmmoPerShoot(int int1) {
		this.ammoPerShoot = int1;
	}

	public float getJamGunChance() {
		return this.jamGunChance;
	}

	public void setJamGunChance(float float1) {
		this.jamGunChance = float1;
	}

	public boolean isJammed() {
		return this.isJammed;
	}

	public void setJammed(boolean boolean1) {
		this.isJammed = boolean1;
	}

	public String getClickSound() {
		return this.clickSound;
	}

	public void setClickSound(String string) {
		this.clickSound = string;
	}

	public ArrayList getModelWeaponPart() {
		return this.modelWeaponPart;
	}

	public void setModelWeaponPart(ArrayList arrayList) {
		this.modelWeaponPart = arrayList;
	}

	public String getOriginalWeaponSprite() {
		return this.originalWeaponSprite;
	}

	public void setOriginalWeaponSprite(String string) {
		this.originalWeaponSprite = string;
	}

	public boolean haveChamber() {
		return this.haveChamber;
	}

	public void setHaveChamber(boolean boolean1) {
		this.haveChamber = boolean1;
	}

	public String getDamageCategory() {
		return this.damageCategory;
	}

	public void setDamageCategory(String string) {
		this.damageCategory = string;
	}

	public boolean isDamageMakeHole() {
		return this.damageMakeHole;
	}

	public void setDamageMakeHole(boolean boolean1) {
		this.damageMakeHole = boolean1;
	}

	public String getHitFloorSound() {
		return this.hitFloorSound;
	}

	public void setHitFloorSound(String string) {
		this.hitFloorSound = string;
	}

	public boolean isInsertAllBulletsReload() {
		return this.insertAllBulletsReload;
	}

	public void setInsertAllBulletsReload(boolean boolean1) {
		this.insertAllBulletsReload = boolean1;
	}

	public String getFireMode() {
		return this.fireMode;
	}

	public void setFireMode(String string) {
		this.fireMode = string;
	}

	public ArrayList getFireModePossibilities() {
		return this.fireModePossibilities;
	}

	public void setFireModePossibilities(ArrayList arrayList) {
		this.fireModePossibilities = arrayList;
	}

	public void randomizeBullets() {
		if (this.isRanged() && !Rand.NextBool(4)) {
			this.setCurrentAmmoCount(Rand.Next(this.getMaxAmmo() - 2, this.getMaxAmmo()));
			if (!StringUtils.isNullOrEmpty(this.getMagazineType())) {
				this.setContainsClip(true);
			}

			if (this.haveChamber()) {
				this.setRoundChambered(true);
			}
		}
	}

	public float getStopPower() {
		return this.getScriptItem().stopPower;
	}

	public boolean isInstantExplosion() {
		return this.explosionTimer <= 0 && this.sensorRange <= 0 && this.getRemoteControlID() == -1;
	}
}
