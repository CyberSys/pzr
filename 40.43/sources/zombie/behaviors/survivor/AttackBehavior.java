package zombie.behaviors.survivor;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemType;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.ui.TextManager;
import zombie.ui.UIFont;


public class AttackBehavior extends Behavior {
	public boolean HasRangeRequirement = false;
	public IsoGameCharacter RangeTest = null;
	public int TestRangeMax = 10000;
	public int thinkTime = 10;
	public int thinkTimeMax = 3;
	public boolean stayInside = false;
	PathFindBehavior pathFind = new PathFindBehavior("Attack");
	IsoGameCharacter Target = null;
	InventoryItem weapon = null;
	int timeout = 180;
	IsoGridSquare backuppoint = null;
	boolean backingup = false;
	public boolean bWaitForThem = false;
	int nextbackuptest = 0;
	private int failedTimeout = 60;
	Vector2 a = new Vector2();

	public Behavior.BehaviorResult process(DecisionPath decisionPath, IsoGameCharacter gameCharacter) {
		if (this.Target != null && !((IsoSurvivor)gameCharacter).getLocalRelevantEnemyList().contains(this.Target)) {
			this.Target = null;
			this.pathFind.reset();
			this.backuppoint = null;
			this.Target = null;
			this.weapon = null;
			this.bWaitForThem = false;
		}

		if (this.backuppoint != null) {
			if (!this.backingup || this.pathFind.sx == 0) {
				this.pathFind.reset();
				this.pathFind.sx = gameCharacter.getCurrentSquare().getX();
				this.pathFind.sy = gameCharacter.getCurrentSquare().getY();
				this.pathFind.sz = gameCharacter.getCurrentSquare().getZ();
				this.pathFind.tx = this.backuppoint.getX();
				this.pathFind.ty = this.backuppoint.getY();
				this.pathFind.tz = this.backuppoint.getZ();
			}

			Behavior.BehaviorResult behaviorResult = this.pathFind.process(decisionPath, gameCharacter);
			if (behaviorResult == Behavior.BehaviorResult.Working) {
				this.backingup = true;
				return Behavior.BehaviorResult.Working;
			}

			this.pathFind.reset();
			this.backuppoint = null;
			this.Target = null;
			this.weapon = null;
			if (behaviorResult == Behavior.BehaviorResult.Succeeded) {
				this.bWaitForThem = true;
			}

			this.nextbackuptest = 60;
			this.backingup = false;
		}

		boolean boolean1 = false;
		if (gameCharacter.getLastTargettedBy() != null) {
			if (this.Target != gameCharacter.getLastTargettedBy()) {
				boolean1 = true;
			}

			this.Target = gameCharacter.getLastTargettedBy();
			this.pathFind.sx = gameCharacter.getCurrentSquare().getX();
			this.pathFind.sy = gameCharacter.getCurrentSquare().getY();
			this.pathFind.sz = gameCharacter.getCurrentSquare().getZ();
		}

		if (this.Target != null) {
		}

		if (this.Target == null || !(this.Target.getHealth() <= 0.0F) && !(this.Target.getBodyDamage().getHealth() <= 0.0F)) {
			HandWeapon handWeapon;
			if (gameCharacter.getPrimaryHandItem() != null && gameCharacter.getPrimaryHandItem().getCat() == ItemType.Weapon) {
				handWeapon = (HandWeapon)gameCharacter.getPrimaryHandItem();
				if (!gameCharacter.HasItem(handWeapon.getAmmoType()) && handWeapon.getAmmoType() != null) {
					gameCharacter.setPrimaryHandItem((InventoryItem)null);
				}
			}

			if (gameCharacter.getPrimaryHandItem() == null || gameCharacter.getPrimaryHandItem().getCat() != ItemType.Weapon) {
				if (!gameCharacter.getInventory().HasType(ItemType.Weapon)) {
					this.timeout = 180;
					return Behavior.BehaviorResult.Succeeded;
				}

				handWeapon = (HandWeapon)gameCharacter.getInventory().getBestWeapon(gameCharacter.getDescriptor());
				if (handWeapon != null && (gameCharacter.HasItem(handWeapon.getAmmoType()) || handWeapon.getAmmoType() == null)) {
					gameCharacter.setPrimaryHandItem(handWeapon);
					if (gameCharacter.getPrimaryHandItem() == gameCharacter.getSecondaryHandItem()) {
						gameCharacter.setSecondaryHandItem((InventoryItem)null);
					}
				}
			}

			this.weapon = gameCharacter.getPrimaryHandItem();
			--this.thinkTime;
			if (this.weapon != null && this.weapon.getCondition() <= 0) {
				this.weapon = null;
			}

			if (this.Target == null) {
				if (this.HasRangeRequirement) {
					this.Target = gameCharacter.getCurrentSquare().FindEnemy(gameCharacter, gameCharacter.getPersonality().getHuntZombieRange(), gameCharacter.getLocalRelevantEnemyList(), this.RangeTest, this.TestRangeMax);
				} else {
					this.Target = gameCharacter.getCurrentSquare().FindEnemy(gameCharacter, gameCharacter.getPersonality().getHuntZombieRange(), gameCharacter.getLocalRelevantEnemyList());
				}

				if (this.Target != null && this.Target.getCurrentSquare() != null) {
					boolean1 = true;
				}

				this.thinkTime = this.thinkTimeMax;
				this.pathFind.sx = gameCharacter.getCurrentSquare().getX();
				this.pathFind.sy = gameCharacter.getCurrentSquare().getY();
				this.pathFind.sz = gameCharacter.getCurrentSquare().getZ();
			}

			if (this.Target == null) {
				this.weapon = null;
				this.timeout = 180;
				return Behavior.BehaviorResult.Succeeded;
			} else if (this.weapon == null) {
				return Behavior.BehaviorResult.Succeeded;
			} else {
				IsoGridSquare square = gameCharacter.getCurrentSquare();
				IsoGridSquare square2 = this.Target.getCurrentSquare();
				if (this.weapon instanceof HandWeapon && square != null && square2 != null) {
					HandWeapon handWeapon2 = (HandWeapon)this.weapon;
					float float1 = IsoUtils.DistanceTo(gameCharacter.getX(), gameCharacter.getY(), this.Target.getX(), this.Target.getY());
					if (square2.getZ() == square.getZ() && !(handWeapon2.getMaxRange(gameCharacter) * 0.9F < float1) && LosUtil.lineClear(square.getCell(), square.getX(), square.getY(), square.getZ(), square2.getX(), square2.getY(), square2.getZ(), false) == LosUtil.TestResults.Clear) {
						this.a.x = this.Target.getX();
						this.a.y = this.Target.getY();
						Vector2 vector2 = this.a;
						vector2.x -= gameCharacter.getX();
						vector2 = this.a;
						vector2.y -= gameCharacter.getY();
						if (this.a.getLength() > 0.0F) {
							this.a.normalize();
							gameCharacter.DirectionFromVector(this.a);
							gameCharacter.getAngle().x = this.a.x;
							gameCharacter.getAngle().y = this.a.y;
							boolean boolean2 = ((IsoSurvivor)gameCharacter).AttemptAttack(0.2F);
							if (!boolean2) {
								this.Target = null;
								this.weapon = null;
								return Behavior.BehaviorResult.Failed;
							}

							gameCharacter.PlayShootAnim();
						}

						this.bWaitForThem = false;
						this.timeout = 30;
						return Behavior.BehaviorResult.Succeeded;
					}

					if (boolean1) {
						this.pathFind.tx = square2.getX();
						this.pathFind.ty = square2.getY();
						this.pathFind.tz = square2.getZ();
					}

					Behavior.BehaviorResult behaviorResult2 = this.pathFind.process(decisionPath, gameCharacter);
					if (behaviorResult2 == Behavior.BehaviorResult.Failed) {
						this.Target = null;
						this.weapon = null;
						this.thinkTime = this.thinkTimeMax;
						return Behavior.BehaviorResult.Succeeded;
					}

					if (behaviorResult2 == Behavior.BehaviorResult.Succeeded) {
						float1 = IsoUtils.DistanceTo(gameCharacter.getX(), gameCharacter.getY(), this.Target.getX(), this.Target.getY());
						if (float1 > ((HandWeapon)this.weapon).getMaxRange(gameCharacter)) {
							this.pathFind.tx = square2.getX();
							this.pathFind.ty = square2.getY();
							this.pathFind.tz = square2.getZ();
						}

						return Behavior.BehaviorResult.Working;
					}
				}

				return Behavior.BehaviorResult.Working;
			}
		} else {
			this.Target = null;
			this.weapon = null;
			gameCharacter.getStats().idleboredom = 1.0F;
			this.timeout = 180;
			return Behavior.BehaviorResult.Succeeded;
		}
	}

	public void reset() {
		this.Target = null;
		this.weapon = null;
		this.timeout = 180;
		this.pathFind.reset();
	}

	public boolean valid() {
		return true;
	}

	float getPriority(IsoGameCharacter gameCharacter) {
		float float1 = 0.0F;
		if (!gameCharacter.IsArmed()) {
			return -1.0E7F;
		} else if (gameCharacter.getLocalRelevantEnemyList().isEmpty()) {
			return -1.0E7F;
		} else {
			if (IsoPlayer.DemoMode) {
				float1 += 1000.0F;
			}

			if (gameCharacter.getLocalRelevantEnemyList().size() < 5) {
				float1 += (float)(gameCharacter.getLocalRelevantEnemyList().size() * 5);
			}

			if (gameCharacter.getLocalRelevantEnemyList().size() > 10) {
				float1 -= (float)(gameCharacter.getLocalRelevantEnemyList().size() * 5);
			}

			if (gameCharacter.getLocalRelevantEnemyList().size() > 20) {
				float1 -= (float)(gameCharacter.getLocalRelevantEnemyList().size() * 10);
			}

			if (gameCharacter.getDangerLevels() > 300.0F) {
				float1 -= 10000.0F;
			}

			float1 += (float)(gameCharacter.getLocalNeutralList().size() * 10);
			float1 += gameCharacter.getDescriptor().getBravery() * 50.0F;
			float1 *= MasterSurvivorBehavior.AttackMultiplier;
			if (gameCharacter.getTimeSinceZombieAttack() < 30) {
				float1 += 1000.0F;
				float1 *= 100.0F;
			}

			if (this.Target == null && float1 > 0.0F) {
				float1 /= 100.0F;
			}

			if (gameCharacter.getStats().endurance < gameCharacter.getStats().endurancedanger) {
				float1 -= -10000.0F;
			}

			return gameCharacter.getLocalRelevantEnemyList().isEmpty() ? -1000000.0F : float1;
		}
	}

	public int renderDebug(int int1) {
		byte byte1 = 50;
		TextManager.instance.DrawString(UIFont.Small, (double)byte1, (double)int1, "AttackBehaviour", 1.0, 1.0, 1.0, 1.0);
		int1 += 30;
		return int1;
	}
}
