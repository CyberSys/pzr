package zombie.ai.states;

import java.util.Iterator;
import java.util.Stack;
import zombie.GameTime;
import zombie.SoundManager;
import zombie.Lua.LuaEventManager;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.input.Mouse;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWindow;


public class SwipeState extends State {
	static SwipeState _instance = new SwipeState();
	Stack HitList = new Stack();
	Stack RemoveList = new Stack();

	public static SwipeState instance() {
		return _instance;
	}

	public void WeaponLowerCondition(HandWeapon handWeapon, IsoGameCharacter gameCharacter) {
		if (handWeapon.getUses() > 1) {
			handWeapon.Use();
			InventoryItem inventoryItem = InventoryItemFactory.CreateItem(handWeapon.getModule() + "." + handWeapon.getType());
			((HandWeapon)inventoryItem).setCondition(handWeapon.getCondition() - 1);
			handWeapon.getContainer().AddItem(inventoryItem);
			gameCharacter.setPrimaryHandItem(inventoryItem);
			if (inventoryItem.getCondition() <= 0) {
				HandWeapon handWeapon2 = (HandWeapon)gameCharacter.getInventory().getBestWeapon(gameCharacter.getDescriptor());
				if (handWeapon2 != null) {
					gameCharacter.setPrimaryHandItem(handWeapon2);
				}
			}
		} else {
			handWeapon.setCondition(handWeapon.getCondition() - 1);
			if (handWeapon.getCondition() <= 0) {
				HandWeapon handWeapon3 = (HandWeapon)gameCharacter.getInventory().getBestWeapon(gameCharacter.getDescriptor());
				if (handWeapon3 != null) {
					gameCharacter.setPrimaryHandItem(handWeapon3);
				}
			}
		}
	}

	public void enter(IsoGameCharacter gameCharacter) {
		HandWeapon handWeapon = gameCharacter.getUseHandWeapon();
		LuaEventManager.triggerEvent("OnWeaponSwing", gameCharacter, handWeapon);
		if (handWeapon.isRanged()) {
			float float1 = 1.0F - GameTime.getInstance().Ambient / 0.8F;
			IsoWorld.instance.CurrentCell.getLamppostPositions().add(new IsoLightSource((int)gameCharacter.x, (int)gameCharacter.y, (int)gameCharacter.z, 0.8F * float1, 0.8F * float1, 0.6F * float1, 18, 6));
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		gameCharacter.StopAllActionQueue();
		HandWeapon handWeapon = gameCharacter.getUseHandWeapon();
		if (handWeapon.getCondition() <= 0) {
			handWeapon = null;
		}

		this.RemoveList.clear();
		if (handWeapon != null && gameCharacter.getAttackDelay() == gameCharacter.getAttackDelayUse()) {
			LuaEventManager.triggerEvent("OnWeaponSwingHitPoint", gameCharacter, handWeapon);
			if (!PerkFactory.newMode) {
				gameCharacter.getXp().AddXP(PerkFactory.Perks.Fitness, 1.0F);
			}

			this.HitList.clear();
			if (handWeapon.isUseEndurance()) {
			}

			if (handWeapon.getPhysicsObject() != null) {
				gameCharacter.Throw(handWeapon);
			}

			boolean boolean1 = false;
			int int1 = 0;
			int int2;
			float float1;
			if (gameCharacter instanceof IsoPlayer && handWeapon.isAimedFirearm()) {
				boolean1 = true;
				float float2 = (float)Mouse.getX();
				float float3 = (float)Mouse.getY();
				Vector2 vector2 = new Vector2(0.0F, 0.0F);
				float1 = (float)Rand.Next((int)(IsoPlayer.instance.AimRadius * 1000.0F)) / 1000.0F;
				if (Core.bDoubleSize) {
					float1 *= 2.0F;
				}

				int int3 = 1 * gameCharacter.getHitChancesMod();
				if (IsoPlayer.instance.EffectiveAimDistance < 1.0F) {
					int3 = 20;
				}

				for (int int4 = 0; int4 < int3; ++int4) {
					vector2.x = (float)Rand.Next(2000) / 1000.0F - 1.0F;
					vector2.y = (float)Rand.Next(2000) / 1000.0F - 1.0F;
					vector2.setLength(float1);
					IsoMovingObject movingObject = IsoObjectPicker.Instance.PickTarget((int)(vector2.x + float2), (int)(vector2.y + float3));
					if (movingObject != null && (!(IsoPlayer.instance.EffectiveAimDistance > 1.0F) || IsoObjectPicker.Instance.IsHeadShot(movingObject, (int)(vector2.x + float2), (int)(vector2.y + float3)))) {
						boolean1 = false;
						gameCharacter.getXp().AddXP(PerkFactory.Perks.Aiming, 3.0F);
						if (movingObject != null) {
							++int1;
							this.HitList.add(movingObject);
							break;
						}
					}

					if (movingObject != null && int4 == int3 - 1) {
						++int1;
						this.HitList.add(movingObject);
						break;
					}
				}
			} else {
				this.CheckObjectHit(gameCharacter, handWeapon);
				if (int1 < handWeapon.getMaxHitCount()) {
					for (int2 = 0; int2 < gameCharacter.getCell().getObjectList().size(); ++int2) {
						IsoMovingObject movingObject2 = (IsoMovingObject)gameCharacter.getCell().getObjectList().get(int2);
						if (movingObject2.isShootable() && gameCharacter.IsAttackRange(movingObject2.getX(), movingObject2.getY(), movingObject2.getZ())) {
							float float4 = 1.0F;
							if ((float)Rand.Next(100) <= float4 * handWeapon.getToHitModifier() * 140.0F || gameCharacter.isAttackWasSuperAttack()) {
								Vector2 vector22 = new Vector2(gameCharacter.getX(), gameCharacter.getY());
								Vector2 vector23 = new Vector2(movingObject2.getX(), movingObject2.getY());
								vector23.x -= vector22.x;
								vector23.y -= vector22.y;
								Vector2 vector24 = gameCharacter.getAngle();
								gameCharacter.DirectionFromVector(vector24);
								vector23.normalize();
								float float5 = vector23.dot(vector24);
								if (!handWeapon.isRanged() && gameCharacter.getDescriptor() != null && !(movingObject2 instanceof IsoZombie)) {
									if (!gameCharacter.getDescriptor().InGroupWith(movingObject2) && gameCharacter instanceof IsoSurvivor && movingObject2 instanceof IsoGameCharacter && !gameCharacter.getEnemyList().contains((IsoGameCharacter)movingObject2)) {
									}

									this.RemoveList.add(movingObject2);
								}

								if (float5 > 1.0F) {
									float5 = 1.0F;
								}

								if (float5 < -1.0F) {
									float5 = -1.0F;
								}

								if (float5 >= handWeapon.getMinAngle() && float5 <= handWeapon.getMaxAngle()) {
									this.HitList.add(movingObject2);
									movingObject2.setHitFromAngle(float5);
									++int1;
								}

								if (int1 >= handWeapon.getMaxHitCount()) {
									break;
								}
							}
						}
					}
				}

				if (this.RemoveList.size() != this.HitList.size()) {
					this.HitList.removeAll(this.RemoveList);
					int1 = this.HitList.size();
				}

				if (gameCharacter instanceof IsoPlayer && ((IsoPlayer)((IsoPlayer)gameCharacter)).isFakeAttack()) {
					this.HitList.clear();
					int1 = 0;
					((IsoPlayer)((IsoPlayer)gameCharacter)).setFakeAttack(false);
					((IsoPlayer)((IsoPlayer)gameCharacter)).getFakeAttackTarget().AttackObject(gameCharacter);
				}
			}

			gameCharacter.setLastHitCount(int1);
			if (!PerkFactory.newMode) {
				if (gameCharacter.getStats().endurance > gameCharacter.getStats().endurancewarn && !handWeapon.isRanged()) {
					gameCharacter.getXp().AddXP(PerkFactory.Perks.Fitness, 1.0F);
				}

				if (!handWeapon.isRanged() && int1 > 1) {
					gameCharacter.getXp().AddXP(PerkFactory.Perks.Strength, (float)(int1 / 2));
				}

				if (int1 > 0) {
					gameCharacter.getXp().AddXP(handWeapon, 1);
				}
			}

			if (int1 > 0 && handWeapon.getImpactSound() != null && gameCharacter instanceof IsoPlayer) {
				SoundManager.instance.PlaySound(handWeapon.getImpactSound(), false, 0.2F);
			}

			if (!handWeapon.isMultipleHitConditionAffected() && Rand.Next(handWeapon.getConditionLowerChance()) == 0) {
				this.WeaponLowerCondition(handWeapon, gameCharacter);
			}

			int2 = 1;
			Iterator iterator = this.HitList.iterator();
			while (iterator.hasNext()) {
				IsoMovingObject movingObject3 = (IsoMovingObject)iterator.next();
				if (LosUtil.lineClear(gameCharacter.getCell(), (int)gameCharacter.getX(), (int)gameCharacter.getY(), (int)gameCharacter.getZ(), (int)movingObject3.getX(), (int)movingObject3.getY(), (int)movingObject3.getZ(), false) != LosUtil.TestResults.Blocked) {
					float1 = handWeapon.getMinDamage();
					float float6 = handWeapon.getMaxDamage() - handWeapon.getMinDamage();
					if (float6 == 0.0F) {
						float1 += 0.0F;
					} else {
						float1 += (float)Rand.Next((int)(float6 * 1000.0F)) / 1000.0F;
					}

					if (!handWeapon.isRanged()) {
						float1 *= handWeapon.getDamageMod(gameCharacter) * gameCharacter.getHittingMod();
					}

					float float7 = float1 / (float)int2;
					if (gameCharacter.isAttackWasSuperAttack()) {
						float7 *= 10.0F;
					}

					int2 += 2;
					if (handWeapon.isUseEndurance() && handWeapon.isShareEndurance()) {
						switch (gameCharacter.getMoodles().getMoodleLevel(MoodleType.Endurance)) {
						case 0: 
						
						default: 
							break;
						
						case 1: 
							float7 *= 0.7F;
							break;
						
						case 2: 
							float7 *= 0.5F;
							break;
						
						case 3: 
							float7 *= 0.35F;
							break;
						
						case 4: 
							float7 *= 0.2F;
						
						}
					}

					if (handWeapon.isMultipleHitConditionAffected() && Rand.Next(handWeapon.getConditionLowerChance()) == 0) {
						this.WeaponLowerCondition(handWeapon, gameCharacter);
					}

					movingObject3.Hit(handWeapon, gameCharacter, float7, boolean1, 1.0F);
					if (movingObject3 instanceof IsoGameCharacter && ((IsoGameCharacter)movingObject3).getHealth() <= 0.0F) {
						Stats stats = gameCharacter.getStats();
						stats.stress -= 0.02F;
						if (gameCharacter instanceof IsoSurvivor) {
							((IsoSurvivor)gameCharacter).Killed((IsoGameCharacter)movingObject3);
						}
					}
				}
			}

			if (gameCharacter instanceof IsoPlayer && handWeapon.isAimedFirearm()) {
				((IsoPlayer)gameCharacter).AimRadius += ((IsoPlayer)gameCharacter).getRadiusKickback(handWeapon);
			}
		}

		if (gameCharacter.getAttackDelay() <= -5.0F || gameCharacter.def.Frame >= (float)(gameCharacter.sprite.CurrentAnim.Frames.size() - 1)) {
			gameCharacter.setAttackDelay(-1.0F);
			gameCharacter.getStateMachine().RevertToPrevious();
			gameCharacter.getStateMachine().Lock = false;
			gameCharacter.PlayAnim("Idle");
		}
	}

	private void CheckObjectHit(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		IsoDirections directions = IsoDirections.fromAngle(gameCharacter.getAngle());
		int int1 = 0;
		int int2 = 0;
		if (directions == IsoDirections.NE || directions == IsoDirections.N || directions == IsoDirections.NW) {
			--int2;
		}

		if (directions == IsoDirections.SE || directions == IsoDirections.S || directions == IsoDirections.SW) {
			++int2;
		}

		if (directions == IsoDirections.NW || directions == IsoDirections.W || directions == IsoDirections.SW) {
			--int1;
		}

		if (directions == IsoDirections.NE || directions == IsoDirections.E || directions == IsoDirections.SE) {
			++int1;
		}

		boolean boolean1 = false;
		IsoGridSquare square = gameCharacter.getCurrentSquare().getCell().getGridSquare(gameCharacter.getCurrentSquare().getX() + int1, gameCharacter.getCurrentSquare().getY() + int2, gameCharacter.getCurrentSquare().getZ());
		int int3;
		IsoObject object;
		if (square != null) {
			for (int3 = 0; int3 < square.getSpecialObjects().size(); ++int3) {
				object = (IsoObject)square.getSpecialObjects().get(int3);
				if (object instanceof IsoBarricade) {
					((IsoBarricade)object).WeaponHit(gameCharacter, handWeapon);
				}

				if (object instanceof IsoWindow) {
					((IsoWindow)object).WeaponHit(gameCharacter, handWeapon);
				}

				if (object instanceof IsoThumpable) {
					((IsoThumpable)object).WeaponHit(gameCharacter, handWeapon);
				}
			}

			for (int3 = 0; int3 < square.getObjects().size(); ++int3) {
				object = (IsoObject)square.getObjects().get(int3);
				if (object instanceof IsoTree) {
					((IsoTree)object).WeaponHit(gameCharacter, handWeapon);
				}
			}
		}

		if (directions == IsoDirections.NE || directions == IsoDirections.N || directions == IsoDirections.NW) {
			for (int3 = 0; int3 < gameCharacter.getCurrentSquare().getSpecialObjects().size(); ++int3) {
				object = (IsoObject)gameCharacter.getCurrentSquare().getSpecialObjects().get(int3);
				if (object instanceof IsoDoor && ((IsoDoor)object).north && !((IsoDoor)object).open) {
					((IsoDoor)object).WeaponHit(gameCharacter, handWeapon);
				}

				if (object instanceof IsoThumpable && ((IsoThumpable)object).north) {
					((IsoThumpable)object).WeaponHit(gameCharacter, handWeapon);
				}

				if (object instanceof IsoWindow && ((IsoWindow)object).north) {
					((IsoWindow)object).WeaponHit(gameCharacter, handWeapon);
				}

				if (object instanceof IsoThumpable && ((IsoThumpable)object).north) {
					((IsoThumpable)object).WeaponHit(gameCharacter, handWeapon);
				}
			}
		}

		IsoObject object2;
		IsoGridSquare square2;
		int int4;
		if (directions == IsoDirections.SE || directions == IsoDirections.S || directions == IsoDirections.SW) {
			square2 = gameCharacter.getCell().getGridSquare(gameCharacter.getCurrentSquare().getX(), gameCharacter.getCurrentSquare().getY() + 1, gameCharacter.getCurrentSquare().getZ());
			if (square2 != null) {
				for (int4 = 0; int4 < square2.getSpecialObjects().size(); ++int4) {
					object2 = (IsoObject)square2.getSpecialObjects().get(int4);
					if (object2 instanceof IsoDoor && ((IsoDoor)object2).north) {
						((IsoDoor)object2).WeaponHit(gameCharacter, handWeapon);
					}

					if (object2 instanceof IsoWindow && ((IsoWindow)object2).north) {
						((IsoWindow)object2).WeaponHit(gameCharacter, handWeapon);
					}

					if (object2 instanceof IsoThumpable && ((IsoThumpable)object2).north) {
						((IsoThumpable)object2).WeaponHit(gameCharacter, handWeapon);
					}
				}
			}
		}

		if (directions == IsoDirections.SE || directions == IsoDirections.E || directions == IsoDirections.NE) {
			square2 = gameCharacter.getCell().getGridSquare(gameCharacter.getCurrentSquare().getX() + 1, gameCharacter.getCurrentSquare().getY(), gameCharacter.getCurrentSquare().getZ());
			if (square2 != null) {
				for (int4 = 0; int4 < square2.getSpecialObjects().size(); ++int4) {
					object2 = (IsoObject)square2.getSpecialObjects().get(int4);
					if (object2 instanceof IsoDoor && !((IsoDoor)object2).north) {
						((IsoDoor)object2).WeaponHit(gameCharacter, handWeapon);
					}

					if (object2 instanceof IsoWindow && !((IsoWindow)object2).north) {
						((IsoWindow)object2).WeaponHit(gameCharacter, handWeapon);
					}

					if (object2 instanceof IsoThumpable && !((IsoThumpable)object2).north) {
						((IsoThumpable)object2).WeaponHit(gameCharacter, handWeapon);
					}
				}
			}
		}

		if (directions == IsoDirections.NW || directions == IsoDirections.W || directions == IsoDirections.SW) {
			for (int3 = 0; int3 < gameCharacter.getCurrentSquare().getSpecialObjects().size(); ++int3) {
				object = (IsoObject)gameCharacter.getCurrentSquare().getSpecialObjects().get(int3);
				if (object instanceof IsoDoor && !((IsoDoor)object).north) {
					((IsoDoor)object).WeaponHit(gameCharacter, handWeapon);
				}

				if (object instanceof IsoWindow && !((IsoWindow)object).north) {
					((IsoWindow)object).WeaponHit(gameCharacter, handWeapon);
				}

				if (object instanceof IsoThumpable && !((IsoThumpable)object).north) {
					((IsoThumpable)object).WeaponHit(gameCharacter, handWeapon);
				}
			}
		}
	}
}
