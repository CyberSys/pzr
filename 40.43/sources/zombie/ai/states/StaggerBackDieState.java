package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameServer;


public class StaggerBackDieState extends State {
	static StaggerBackDieState _instance = new StaggerBackDieState();
	Vector2 dirThisFrame = new Vector2();
	int AnimDelayRate = 10;
	float idealx = 0.0F;
	float idealy = 0.0F;
	float moveDir = 0.6F;

	public static StaggerBackDieState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		if (gameCharacter instanceof IsoZombie) {
			((IsoZombie)gameCharacter).NetRemoteState = 8;
			((IsoZombie)gameCharacter).DoNetworkDirty();
		}

		if (GameServer.bServer && gameCharacter instanceof IsoPlayer) {
			GameServer.SendDeath((IsoPlayer)gameCharacter);
		}

		this.idealx = 0.0F;
		this.idealy = 0.0F;
		if (gameCharacter instanceof IsoSurvivor) {
			((IsoSurvivor)gameCharacter).getDescriptor().bDead = true;
		}

		if (gameCharacter instanceof IsoZombie) {
			if (!((IsoZombie)gameCharacter).isFakeDead()) {
				++IsoZombie.ZombieDeaths;
				gameCharacter.PlayAnim("ZombieStaggerBack");
				gameCharacter.def.setFrameSpeedPerFrame(0.3F);
			} else {
				gameCharacter.PlayAnimUnlooped("ZombieDeath");
				gameCharacter.def.Frame = (float)(gameCharacter.sprite.CurrentAnim.Frames.size() - 1);
				gameCharacter.setReanimateTimer(0.1F);
			}
		} else {
			gameCharacter.PlayAnimUnlooped("ZombieDeath");
			gameCharacter.def.setFrameSpeedPerFrame(0.3F);
		}

		boolean boolean1 = !Core.bLastStand && gameCharacter instanceof IsoZombie && (((IsoZombie)gameCharacter).isFakeDead() || gameCharacter.getHealth() < 1.0F && Rand.Next(10) == 0);
		gameCharacter.StateMachineParams.clear();
		gameCharacter.StateMachineParams.put(0, boolean1);
		gameCharacter.setStateEventDelayTimer(!boolean1 && !gameCharacter.isDead() ? this.getMaxStaggerTime(gameCharacter) : 0.0F);
		Vector2 vector2 = gameCharacter.getHitDir();
		vector2.x *= gameCharacter.getHitForce();
		vector2 = gameCharacter.getHitDir();
		vector2.y *= gameCharacter.getHitForce();
		vector2 = gameCharacter.getHitDir();
		vector2.x *= 0.08F;
		vector2 = gameCharacter.getHitDir();
		vector2.y *= 0.08F;
		if (gameCharacter.getHitDir().getLength() > 0.06F) {
			gameCharacter.getHitDir().setLength(0.06F);
		}

		if (gameCharacter instanceof IsoZombie) {
			((IsoZombie)gameCharacter).DoNetworkDirty();
		}

		this.dirThisFrame.x = gameCharacter.getHitDir().x;
		this.dirThisFrame.y = gameCharacter.getHitDir().y;
		if (this.dirThisFrame.getLength() > 0.0F) {
			this.dirThisFrame.normalize();
			gameCharacter.setDir(IsoDirections.reverse(IsoDirections.fromAngle(this.dirThisFrame)));
		}

		if (gameCharacter instanceof IsoZombie) {
			((IsoZombie)gameCharacter).reqMovement.x = gameCharacter.angle.x;
			((IsoZombie)gameCharacter).reqMovement.y = gameCharacter.angle.y;
		}

		gameCharacter.setIgnoreMovementForDirection(true);
		gameCharacter.getStateMachine().Lock = true;
		gameCharacter.setReanimPhase(0);
		if (gameCharacter instanceof IsoZombie) {
			gameCharacter.setReanimateTimer((float)(Rand.Next(60) + 30));
		}

		if (Rand.Next(5) == 0) {
			gameCharacter.setReanimateTimer((float)(Rand.Next(60) + 30));
		}

		if (gameCharacter instanceof IsoZombie) {
			gameCharacter.setReanimAnimFrame(3);
			gameCharacter.setReanimAnimDelay(this.AnimDelayRate);
		}

		if (gameCharacter.getHealth() > 0.0F && !(gameCharacter instanceof IsoPlayer)) {
			gameCharacter.setReanim(true);
			gameCharacter.setDieCount(gameCharacter.getDieCount() + 1);
		} else if (gameCharacter.getHealth() <= 0.0F) {
			gameCharacter.playDeadSound();
		}

		if (gameCharacter instanceof IsoZombie) {
			((IsoZombie)gameCharacter).DoNetworkDirty();
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (gameCharacter instanceof IsoZombie) {
			((IsoZombie)gameCharacter).NetRemoteState = 8;
			((IsoZombie)gameCharacter).DoNetworkDirty();
		}

		float float1;
		if (gameCharacter instanceof IsoZombie && gameCharacter.getSprite().CurrentAnim.name.equals("ZombieDeadToCrawl") && gameCharacter.getSpriteDef().Frame > 12.0F) {
			if (gameCharacter.getSprite().CurrentAnim.name.equals("ZombieDeadToCrawl") && (int)gameCharacter.getSpriteDef().Frame >= 16 && (int)gameCharacter.getSpriteDef().Frame <= 21 && ((IsoZombie)gameCharacter).isFakeDead()) {
				((IsoZombie)gameCharacter).setFakeDead(false);
				if (gameCharacter.getHealth() > 0.0F && ((IsoZombie)gameCharacter).isTargetInCone(1.5F, 0.9F) && ((IsoZombie)gameCharacter).target instanceof IsoGameCharacter) {
					IsoGameCharacter gameCharacter2 = (IsoGameCharacter)((IsoZombie)gameCharacter).target;
					if (gameCharacter2.getVehicle() == null || gameCharacter2.getVehicle().couldCrawlerAttackPassenger(gameCharacter2)) {
						gameCharacter2.getBodyDamage().AddRandomDamageFromZombie((IsoZombie)gameCharacter);
					}
				}

				gameCharacter.getStateMachine().changeState(WalkTowardState.instance());
			}

			if (this.idealx == 0.0F || this.idealy == 0.0F) {
				this.idealx = gameCharacter.getX() - (float)((int)gameCharacter.getX());
				this.idealy = gameCharacter.getY() - (float)((int)gameCharacter.getY());
				if (gameCharacter.getDir() == IsoDirections.N) {
					this.idealy -= this.moveDir;
				}

				if (gameCharacter.getDir() == IsoDirections.NE) {
					this.idealx += this.moveDir;
					this.idealy -= this.moveDir;
				}

				if (gameCharacter.getDir() == IsoDirections.NW) {
					this.idealx -= this.moveDir;
					this.idealy -= this.moveDir;
				}

				if (gameCharacter.getDir() == IsoDirections.S) {
					this.idealy += this.moveDir;
				}

				if (gameCharacter.getDir() == IsoDirections.SE) {
					this.idealx += this.moveDir;
					this.idealy += this.moveDir;
				}

				if (gameCharacter.getDir() == IsoDirections.SW) {
					this.idealx -= this.moveDir;
					this.idealy += this.moveDir;
				}

				if (gameCharacter.getDir() == IsoDirections.W) {
					this.idealx -= this.moveDir;
				}

				if (gameCharacter.getDir() == IsoDirections.E) {
					this.idealx += this.moveDir;
				}

				if (this.idealx > 0.99F) {
					this.idealx = 0.99F;
				}

				if (this.idealy > 0.99F) {
					this.idealy = 0.99F;
				}

				if (this.idealx < 0.0F) {
					this.idealx = 0.0F;
				}

				if (this.idealy < 0.0F) {
					this.idealy = 0.0F;
				}
			}

			float1 = gameCharacter.x - (float)((int)gameCharacter.x);
			float float2 = gameCharacter.y - (float)((int)gameCharacter.y);
			float float3;
			if (float1 != this.idealx) {
				float3 = (this.idealx - float1) / 4.0F;
				float1 += float3;
				gameCharacter.x = (float)((int)gameCharacter.x) + float1;
			}

			if (float2 != this.idealy) {
				float3 = (this.idealy - float2) / 4.0F;
				float2 += float3;
				gameCharacter.y = (float)((int)gameCharacter.y) + float2;
			}

			gameCharacter.nx = gameCharacter.x;
			gameCharacter.ny = gameCharacter.y;
			if (gameCharacter instanceof IsoZombie) {
				((IsoZombie)gameCharacter).DoNetworkDirty();
			}
		}

		if (gameCharacter.sprite != null && gameCharacter.sprite.CurrentAnim != null) {
			if (!gameCharacter.isIgnoreStaggerBack()) {
				if (gameCharacter.getReanimPhase() == 0 && gameCharacter.getStateEventDelayTimer() > 0.0F) {
					this.dirThisFrame.x = gameCharacter.getHitDir().x;
					this.dirThisFrame.y = gameCharacter.getHitDir().y;
					gameCharacter.setOnFloor(true);
					float1 = gameCharacter.getStateEventDelayTimer() / this.getMaxStaggerTime(gameCharacter);
					Vector2 vector2 = this.dirThisFrame;
					vector2.x *= float1;
					vector2 = this.dirThisFrame;
					vector2.y *= float1;
					if (!gameCharacter.isIgnoreStaggerBack()) {
						gameCharacter.Move(this.dirThisFrame);
					}

					if (gameCharacter.getHitBy() != null) {
						Stats stats = gameCharacter.getHitBy().getStats();
						stats.stress -= 0.0016F;
					}
				} else {
					this.dirThisFrame.set(0.0F, 0.0F);
				}

				if (this.dirThisFrame.getLength() > 0.0F) {
					this.dirThisFrame.normalize();
					gameCharacter.setDir(IsoDirections.reverse(IsoDirections.fromAngle(this.dirThisFrame)));
				}

				if (gameCharacter instanceof IsoZombie) {
					((IsoZombie)gameCharacter).reqMovement.x = gameCharacter.angle.x;
					((IsoZombie)gameCharacter).reqMovement.y = gameCharacter.angle.y;
				}
			}

			if ((gameCharacter.isIgnoreStaggerBack() || gameCharacter.isUnderVehicle() || gameCharacter.getStateEventDelayTimer() <= 0.0F && !gameCharacter.sprite.CurrentAnim.name.equals("ZombieDeath") && !gameCharacter.sprite.CurrentAnim.name.equals("ZombieGetUp") && !gameCharacter.sprite.CurrentAnim.name.equals("Die") && !gameCharacter.sprite.CurrentAnim.name.equals("ZombieDeadToCrawl")) && gameCharacter instanceof IsoZombie && !((IsoZombie)gameCharacter).bCrawling && !((IsoZombie)gameCharacter).isFakeDead()) {
				gameCharacter.PlayAnimUnlooped("ZombieDeath");
				gameCharacter.setOnFloor(true);
				if (gameCharacter.isReanim() && gameCharacter.getReanimPhase() > 0) {
					gameCharacter.setReanimPhase(0);
					gameCharacter.setReanimateTimer((float)(Rand.Next(60) + 30));
					gameCharacter.def.setFrameSpeedPerFrame(0.45F);
				}
			}

			if ((gameCharacter.sprite.CurrentAnim.name.equals("ZombieGetUp") || gameCharacter.sprite.CurrentAnim.name.equals("ZombieDeath") || gameCharacter.sprite.CurrentAnim.name.equals("Die") || gameCharacter.sprite.CurrentAnim.name.equals("ZombieDeadToCrawl")) && gameCharacter.def.Finished) {
				if (gameCharacter.isDead()) {
					gameCharacter.setReanim(false);
					if (GameServer.bServer && gameCharacter instanceof IsoZombie) {
						GameServer.sendDeadZombie((IsoZombie)gameCharacter);
					}

					IsoDeadBody deadBody = new IsoDeadBody(gameCharacter);
					if (gameCharacter instanceof IsoPlayer && gameCharacter.shouldBecomeZombieAfterDeath()) {
						deadBody.reanimateLater();
					}
				}

				if (gameCharacter.getReanimPhase() == 0) {
					gameCharacter.setCollidable(false);
					gameCharacter.setReanimPhase(1);
					gameCharacter.def.Finished = true;
				}

				if (gameCharacter.isReanim()) {
					if (gameCharacter.getReanimPhase() == 1) {
						if (gameCharacter.getHealth() <= 0.0F) {
							gameCharacter.setHealth(0.5F);
						}

						gameCharacter.setReanimateTimer(gameCharacter.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
						if (gameCharacter.getReanimateTimer() <= 0.0F) {
							if (gameCharacter.StateMachineParams.get(0) != null && gameCharacter.StateMachineParams.get(0).equals(true)) {
								gameCharacter.def.Frame = 0.0F;
								gameCharacter.def.Finished = false;
								this.playSafeDead(gameCharacter);
							} else if (!gameCharacter.isUnderVehicle()) {
								gameCharacter.def.Frame = 0.0F;
								gameCharacter.def.Finished = false;
								gameCharacter.setReanimPhase(2);
								gameCharacter.PlayAnimUnlooped("ZombieGetUp");
								gameCharacter.def.setFrameSpeedPerFrame(0.2F);
							}
						}
					}

					if (gameCharacter.getReanimPhase() == 2) {
						gameCharacter.setVisibleToNPCs(false);
						gameCharacter.setOnFloor(true);
						if ((int)gameCharacter.def.Frame >= gameCharacter.sprite.CurrentAnim.Frames.size() - 2) {
							gameCharacter.getStateMachine().Lock = false;
							gameCharacter.setReanimPhase(3);
							gameCharacter.setVisibleToNPCs(true);
							if (gameCharacter instanceof IsoZombie && !((IsoZombie)gameCharacter).bCrawling) {
								gameCharacter.setOnFloor(false);
							}

							gameCharacter.setCollidable(true);
						}
					}

					if (gameCharacter.getReanimPhase() == 3) {
						gameCharacter.def.setFrameSpeedPerFrame(0.23F);
						if (gameCharacter instanceof IsoZombie) {
							IsoSpriteInstance spriteInstance = gameCharacter.def;
							spriteInstance.AnimFrameIncrease *= ((IsoZombie)gameCharacter).getSpeedMod();
							if (((IsoZombie)gameCharacter).walkVariantUse == null) {
								((IsoZombie)gameCharacter).walkVariant = "ZombieWalk";
								((IsoZombie)gameCharacter).DoZombieStats();
							}

							gameCharacter.PlayAnim(((IsoZombie)gameCharacter).walkVariantUse);
						}

						gameCharacter.sprite.Animate = true;
						if (gameCharacter instanceof IsoZombie && !((IsoZombie)gameCharacter).isFakeDead()) {
							((IsoZombie)gameCharacter).AllowRepathDelay = 0.0F;
							gameCharacter.setDefaultState();
						} else {
							gameCharacter.getStateMachine().changeState(WalkTowardState.instance());
						}

						gameCharacter.setReanim(false);
						if (gameCharacter instanceof IsoZombie && !((IsoZombie)gameCharacter).bCrawling) {
							gameCharacter.setOnFloor(false);
						}
					}
				} else {
					if (gameCharacter instanceof IsoZombie && gameCharacter.getAttackedBy() != null && gameCharacter.upKillCount) {
						gameCharacter.getAttackedBy().setZombieKills(gameCharacter.getAttackedBy().getZombieKills() + 1);
					} else if (gameCharacter instanceof IsoSurvivor && gameCharacter.getAttackedBy() != null && gameCharacter.upKillCount) {
						gameCharacter.getAttackedBy().setSurvivorKills(gameCharacter.getAttackedBy().getSurvivorKills() + 1);
					}

					if (GameServer.bServer && gameCharacter instanceof IsoZombie && gameCharacter.getAttackedBy() instanceof IsoPlayer) {
						gameCharacter.getAttackedBy().sendObjectChange("AddZombieKill");
					}
				}
			}
		}
	}

	private void playSafeDead(IsoGameCharacter gameCharacter) {
		gameCharacter.setVisibleToNPCs(false);
		gameCharacter.setCollidable(false);
		((IsoZombie)gameCharacter).setFakeDead(true);
		((IsoZombie)gameCharacter).setOnFloor(true);
		if (!gameCharacter.isUnderVehicle() && ((IsoZombie)gameCharacter).target != null && gameCharacter.getZ() == ((IsoZombie)gameCharacter).target.getZ() && ((IsoZombie)gameCharacter).target.DistTo(gameCharacter) < 2.5F) {
			gameCharacter.setIgnoreMovementForDirection(false);
			((IsoZombie)gameCharacter).DirectionFromVector(((IsoZombie)gameCharacter).vectorToTarget);
			gameCharacter.setIgnoreMovementForDirection(true);
			((IsoZombie)gameCharacter).bCrawling = true;
			gameCharacter.setVisibleToNPCs(true);
			gameCharacter.setCollidable(true);
			gameCharacter.PlayAnimUnlooped("ZombieDeadToCrawl");
			gameCharacter.def.setFrameSpeedPerFrame(0.27F);
			((IsoZombie)gameCharacter).DoZombieStats();
			gameCharacter.getStateMachine().Lock = false;
			gameCharacter.setReanimPhase(2);
			String string = "MaleZombieAttack";
			if (gameCharacter.isFemale()) {
				string = "FemaleZombieAttack";
			}

			gameCharacter.getEmitter().playSound(string);
			if (((IsoZombie)gameCharacter).target instanceof IsoPlayer) {
				IsoPlayer player = (IsoPlayer)((IsoZombie)gameCharacter).target;
				Stats stats = player.getStats();
				stats.Panic += player.getBodyDamage().getPanicIncreaseValue() * 3.0F;
			}
		} else {
			gameCharacter.PlayAnimUnlooped("ZombieDeath");
			gameCharacter.def.Frame = (float)(gameCharacter.sprite.CurrentAnim.Frames.size() - 1);
			gameCharacter.def.Finished = true;
			gameCharacter.setReanimateTimer(0.1F);
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovementForDirection(false);
		gameCharacter.getStateMachine().Lock = false;
	}

	private float getMaxStaggerTime(IsoGameCharacter gameCharacter) {
		float float1 = 35.0F * gameCharacter.getHitForce() * gameCharacter.getStaggerTimeMod();
		if (float1 < 15.0F) {
			return 15.0F;
		} else {
			return float1 > 25.0F ? 25.0F : float1;
		}
	}
}
