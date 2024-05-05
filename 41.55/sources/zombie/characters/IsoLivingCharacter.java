package zombie.characters;

import zombie.WorldSoundManager;
import zombie.Lua.LuaHookManager;
import zombie.ai.states.SwipeStatePlayer;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.skills.PerkFactory;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoMovingObject;
import zombie.iso.Vector2;
import zombie.ui.UIManager;


public class IsoLivingCharacter extends IsoGameCharacter {
	public float useChargeDelta = 0.0F;
	public final HandWeapon bareHands = (HandWeapon)InventoryItemFactory.CreateItem("Base.BareHands");
	public boolean bDoShove = false;
	public boolean bCollidedWithPushable = false;
	public IsoGameCharacter targetOnGround;

	public IsoLivingCharacter(IsoCell cell, float float1, float float2, float float3) {
		super(cell, float1, float2, float3);
	}

	public boolean isCollidedWithPushableThisFrame() {
		return this.bCollidedWithPushable;
	}

	public boolean AttemptAttack(float float1) {
		HandWeapon handWeapon = null;
		if (this.leftHandItem instanceof HandWeapon) {
			handWeapon = (HandWeapon)this.leftHandItem;
		} else {
			handWeapon = this.bareHands;
		}

		if (handWeapon != this.bareHands && this instanceof IsoPlayer) {
			SwipeStatePlayer.AttackVars attackVars = SwipeStatePlayer.instance().attackVars;
			SwipeStatePlayer.instance().CalcAttackVars(this, attackVars);
			this.bDoShove = attackVars.bDoShove;
			if (LuaHookManager.TriggerHook("Attack", this, float1, handWeapon)) {
				return false;
			}
		}

		return this.DoAttack(float1);
	}

	public boolean DoAttack(float float1) {
		if (this.isDead()) {
			return false;
		} else {
			if (this.leftHandItem != null) {
				InventoryItem inventoryItem = this.leftHandItem;
				if (inventoryItem instanceof HandWeapon) {
					this.useHandWeapon = (HandWeapon)inventoryItem;
					if (this.useHandWeapon.getCondition() <= 0) {
						return false;
					}

					int int1 = this.Moodles.getMoodleLevel(MoodleType.Endurance);
					if (this.useHandWeapon.isCantAttackWithLowestEndurance() && int1 == 4) {
						return false;
					}

					int int2 = 0;
					int int3;
					if (this.useHandWeapon.isRanged()) {
						int3 = this.useHandWeapon.getRecoilDelay();
						Float Float1 = (float)int3 * (1.0F - (float)this.getPerkLevel(PerkFactory.Perks.Aiming) / 30.0F);
						this.setRecoilDelay((float)Float1.intValue());
					}

					if (this instanceof IsoSurvivor && this.useHandWeapon.isRanged() && int2 < this.useHandWeapon.getMaxHitCount()) {
						for (int3 = 0; int3 < this.getCell().getObjectList().size(); ++int3) {
							IsoMovingObject movingObject = (IsoMovingObject)this.getCell().getObjectList().get(int3);
							if (movingObject != this && movingObject.isShootable() && this.IsAttackRange(movingObject.getX(), movingObject.getY(), movingObject.getZ())) {
								float float2 = 1.0F;
								if (float2 > 0.0F) {
									Vector2 vector2 = new Vector2(this.getX(), this.getY());
									Vector2 vector22 = new Vector2(movingObject.getX(), movingObject.getY());
									vector22.x -= vector2.x;
									vector22.y -= vector2.y;
									boolean boolean1 = false;
									if (vector22.x == 0.0F && vector22.y == 0.0F) {
										boolean1 = true;
									}

									Vector2 vector23 = this.getForwardDirection();
									this.DirectionFromVector(vector23);
									vector22.normalize();
									float float3 = vector22.dot(vector23);
									if (boolean1) {
										float3 = 1.0F;
									}

									if (float3 > 1.0F) {
										float3 = 1.0F;
									}

									if (float3 < -1.0F) {
										float3 = -1.0F;
									}

									if (float3 >= this.useHandWeapon.getMinAngle() && float3 <= this.useHandWeapon.getMaxAngle()) {
										++int2;
									}

									if (int2 >= this.useHandWeapon.getMaxHitCount()) {
										break;
									}
								}
							}
						}
					}

					if (UIManager.getPicked() != null) {
						this.attackTargetSquare = UIManager.getPicked().square;
						if (UIManager.getPicked().tile instanceof IsoMovingObject) {
							this.attackTargetSquare = ((IsoMovingObject)UIManager.getPicked().tile).getCurrentSquare();
						}
					}

					if (this.useHandWeapon.getAmmoType() != null && !this.inventory.contains(this.useHandWeapon.getAmmoType())) {
						return false;
					}

					if (this.useHandWeapon.getOtherHandRequire() != null && (this.rightHandItem == null || !this.rightHandItem.getType().equals(this.useHandWeapon.getOtherHandRequire()))) {
						return false;
					}

					if (!this.useHandWeapon.isRanged()) {
						this.getEmitter().playSound(this.useHandWeapon.getSwingSound(), this);
						WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), this.useHandWeapon.getSoundRadius(), this.useHandWeapon.getSoundVolume());
					}

					this.AttackWasSuperAttack = this.superAttack;
					this.changeState(SwipeStatePlayer.instance());
					if (this.useHandWeapon.getAmmoType() != null) {
						if (this instanceof IsoPlayer) {
							IsoPlayer.getInstance().inventory.RemoveOneOf(this.useHandWeapon.getAmmoType());
						} else {
							this.inventory.RemoveOneOf(this.useHandWeapon.getAmmoType());
						}
					}

					if (this.useHandWeapon.isUseSelf() && this.leftHandItem != null) {
						this.leftHandItem.Use();
					}

					if (this.useHandWeapon.isOtherHandUse() && this.rightHandItem != null) {
						this.rightHandItem.Use();
					}

					return true;
				}
			}

			return false;
		}
	}

	public boolean isShoving() {
		return this.bDoShove;
	}
}
