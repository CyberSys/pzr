package zombie.ai.states;

import zombie.GameTime;
import zombie.SoundManager;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.ZombieThumpManager;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;


public class ThumpState extends State {
	static ThumpState _instance = new ThumpState();

	public static ThumpState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		((IsoZombie)gameCharacter).thumpFrame = -1;
		gameCharacter.setIgnoreMovementForDirection(true);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		Thumpable thumpable = gameCharacter.getThumpTarget();
		gameCharacter.PlayAnim("ZombieDoor");
		gameCharacter.def.setFrameSpeedPerFrame(0.15F);
		if (thumpable instanceof IsoObject) {
			gameCharacter.setIgnoreMovementForDirection(false);
			gameCharacter.faceThisObject((IsoObject)thumpable);
			gameCharacter.setIgnoreMovementForDirection(true);
		}

		boolean boolean1 = GameServer.bServer && GameServer.bFastForward || !GameServer.bServer && IsoPlayer.allPlayersAsleep();
		int int1 = (int)gameCharacter.def.getFrame();
		IsoZombie zombie;
		if (boolean1 || ((IsoZombie)gameCharacter).thumpFrame < 5 && int1 >= 5) {
			gameCharacter.setTimeThumping(gameCharacter.getTimeThumping() + 1);
			if (((IsoZombie)gameCharacter).TimeSinceSeenFlesh < 5.0F) {
				gameCharacter.setTimeThumping(0);
			}

			zombie = (IsoZombie)gameCharacter;
			if (zombie.target != null && zombie.z == zombie.target.z && zombie.vectorToTarget.getLength() < 5.0F) {
				LosUtil.TestResults testResults = LosUtil.lineClear(zombie.getCell(), (int)zombie.x, (int)zombie.y, (int)zombie.z, (int)zombie.target.getX(), (int)zombie.target.getY(), (int)zombie.target.getZ(), false);
				boolean boolean2 = LosUtil.lineClearCollideCount(zombie, zombie.getCell(), (int)zombie.target.getX(), (int)zombie.target.getY(), (int)zombie.target.getZ(), (int)zombie.x, (int)zombie.y, (int)zombie.z) == 0;
				if (testResults != LosUtil.TestResults.Blocked && testResults != LosUtil.TestResults.ClearThroughWindow && !boolean2) {
					zombie.setTimeThumping(0);
					zombie.AllowRepathDelay = 0.0F;
					zombie.setDefaultState();
					return;
				}
			}

			int int2 = 1;
			if (gameCharacter.getCurrentSquare() != null) {
				int2 = gameCharacter.getCurrentSquare().getMovingObjects().size();
			}

			int int3 = 0;
			while (true) {
				if (int3 >= int2) {
					boolean boolean3 = GameServer.bServer || SoundManager.instance.isListenerInRange(gameCharacter.x, gameCharacter.y, 20.0F);
					if (boolean3 && !IsoPlayer.allPlayersAsleep()) {
						if (!(gameCharacter.getThumpTarget() instanceof IsoDoor) && !(gameCharacter.getThumpTarget() instanceof IsoThumpable) && (!(gameCharacter.getThumpTarget() instanceof IsoWindow) || !gameCharacter.getThumpTarget().isDestroyed() && !((IsoWindow)gameCharacter.getThumpTarget()).IsOpen() && ((IsoWindow)gameCharacter.getThumpTarget()).getBarricadeForCharacter(gameCharacter) == null)) {
							if (Rand.Next(3) == 0) {
								if (!GameServer.bServer) {
									ZombieThumpManager.instance.addCharacter((IsoZombie)gameCharacter);
								}

								((IsoZombie)gameCharacter).thumpFlag = 2;
							} else {
								if (!GameServer.bServer) {
									ZombieThumpManager.instance.addCharacter((IsoZombie)gameCharacter);
								}

								((IsoZombie)gameCharacter).thumpFlag = 3;
							}
						} else {
							String string = "ZombieThumpGeneric";
							if (gameCharacter.getThumpTarget() instanceof IsoWindow && ((IsoWindow)gameCharacter.getThumpTarget()).getBarricadeForCharacter(gameCharacter) != null) {
								IsoBarricade barricade = ((IsoWindow)gameCharacter.getThumpTarget()).getBarricadeForCharacter(gameCharacter);
								if (barricade.isMetal() || barricade.isMetalBar()) {
									string = "ZombieThumpMetal";
								}
							} else if (gameCharacter.getThumpTarget() instanceof IsoThumpable) {
								IsoThumpable thumpable2 = (IsoThumpable)gameCharacter.getThumpTarget();
								string = thumpable2.getThumpSound();
								IsoBarricade barricade2 = thumpable2.getBarricadeForCharacter(gameCharacter);
								if (barricade2 != null && (barricade2.isMetal() || barricade2.isMetalBar())) {
									string = "ZombieThumpMetal";
								}
							}

							if ("ZombieThumpGeneric".equals(string)) {
								((IsoZombie)gameCharacter).thumpFlag = 1;
							} else if ("ZombieThumpMetal".equals(string)) {
								((IsoZombie)gameCharacter).thumpFlag = 4;
							} else {
								((IsoZombie)gameCharacter).thumpFlag = 1;
							}

							if (!GameServer.bServer) {
								ZombieThumpManager.instance.addCharacter((IsoZombie)gameCharacter);
							}
						}
					}

					break;
				}

				if (gameCharacter.getThumpTarget() == null) {
					gameCharacter.setDefaultState();
					gameCharacter.setTimeThumping(0);
					return;
				}

				if (gameCharacter.getThumpTarget() instanceof IsoDoor && ((IsoDoor)((IsoDoor)gameCharacter.getThumpTarget())).open || gameCharacter.getThumpTarget() instanceof IsoThumpable && ((IsoThumpable)((IsoThumpable)gameCharacter.getThumpTarget())).open) {
					gameCharacter.setDefaultState();
					gameCharacter.setTimeThumping(0);
					return;
				}

				if (gameCharacter.getThumpTarget() instanceof IsoThumpable && !((IsoThumpable)((IsoThumpable)gameCharacter.getThumpTarget())).isThumpable()) {
					gameCharacter.getStateMachine().RevertToPrevious();
					gameCharacter.setTimeThumping(0);
					return;
				}

				gameCharacter.getThumpTarget().Thump(gameCharacter);
				++int3;
			}
		}

		((IsoZombie)gameCharacter).thumpFrame = int1;
		if (!(thumpable instanceof IsoWindow) || ((IsoWindow)thumpable).canClimbThrough(gameCharacter)) {
			if (thumpable == null || thumpable.isDestroyed() || thumpable instanceof IsoObject && ((IsoObject)thumpable).getObjectIndex() == -1 || thumpable instanceof IsoDoor && ((IsoDoor)thumpable).open || thumpable instanceof IsoThumpable && ((IsoThumpable)thumpable).isDoor && ((IsoThumpable)thumpable).open || thumpable instanceof IsoWindow && ((IsoWindow)thumpable).canClimbThrough(gameCharacter)) {
				gameCharacter.setThumpTarget((Thumpable)null);
				if (thumpable instanceof IsoWindow && ((IsoWindow)thumpable).canClimbThrough(gameCharacter)) {
					gameCharacter.setTimeThumping(0);
					gameCharacter.StateMachineParams.put(0, thumpable);
					gameCharacter.changeState(ClimbThroughWindowState.instance());
					return;
				}

				gameCharacter.setTimeThumping(0);
				if (gameCharacter instanceof IsoZombie) {
					zombie = (IsoZombie)gameCharacter;
					IsoGridSquare square;
					IsoGridSquare square2;
					if (thumpable instanceof IsoDoor && (((IsoDoor)thumpable).open || thumpable.isDestroyed())) {
						IsoDoor door = (IsoDoor)thumpable;
						square2 = door.getSquare();
						square = door.getOppositeSquare();
						if (this.lungeThroughDoor(zombie, square2, square)) {
							return;
						}
					}

					if (thumpable instanceof IsoThumpable && ((IsoThumpable)thumpable).isDoor && (((IsoThumpable)thumpable).open || thumpable.isDestroyed())) {
						IsoThumpable thumpable3 = (IsoThumpable)thumpable;
						square2 = thumpable3.getSquare();
						square = thumpable3.getInsideSquare();
						if (this.lungeThroughDoor(zombie, square2, square)) {
							return;
						}
					}

					if (zombie.LastTargetSeenX != -1) {
						gameCharacter.pathToLocation(zombie.LastTargetSeenX, zombie.LastTargetSeenY, zombie.LastTargetSeenZ);
						if (gameCharacter.getStateMachine().getCurrent() == WalkTowardState.instance() || gameCharacter.getStateMachine().getCurrent() == PathFindState.instance()) {
							return;
						}
					}
				}

				gameCharacter.setDefaultState();
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovementForDirection(false);
	}

	private IsoPlayer findPlayer(int int1, int int2, int int3, int int4, int int5) {
		for (int int6 = int3; int6 <= int4; ++int6) {
			for (int int7 = int1; int7 <= int2; ++int7) {
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int7, int6, int5);
				if (square != null) {
					for (int int8 = 0; int8 < square.getMovingObjects().size(); ++int8) {
						IsoMovingObject movingObject = (IsoMovingObject)square.getMovingObjects().get(int8);
						if (movingObject instanceof IsoPlayer && !((IsoPlayer)movingObject).GhostMode) {
							return (IsoPlayer)movingObject;
						}
					}
				}
			}
		}

		return null;
	}

	private boolean lungeThroughDoor(IsoZombie zombie, IsoGridSquare square, IsoGridSquare square2) {
		if (square != null && square2 != null) {
			boolean boolean1 = square.getY() > square2.getY();
			IsoGridSquare square3 = null;
			IsoPlayer player = null;
			if (zombie.getCurrentSquare() == square) {
				square3 = square2;
				if (boolean1) {
					player = this.findPlayer(square2.getX() - 1, square2.getX() + 1, square2.getY() - 1, square2.getY(), square2.getZ());
				} else {
					player = this.findPlayer(square2.getX() - 1, square2.getX(), square2.getY() - 1, square2.getY() + 1, square2.getZ());
				}
			} else if (zombie.getCurrentSquare() == square2) {
				square3 = square;
				if (boolean1) {
					player = this.findPlayer(square.getX() - 1, square.getX() + 1, square.getY(), square.getY() + 1, square.getZ());
				} else {
					player = this.findPlayer(square.getX(), square.getX() + 1, square.getY() - 1, square.getY() + 1, square.getZ());
				}
			}

			if (player != null && !LosUtil.lineClearCollide(square3.getX(), square3.getY(), square3.getZ(), (int)player.getX(), (int)player.getY(), (int)player.getZ(), false)) {
				zombie.target = player;
				zombie.vectorToTarget.x = player.getX();
				zombie.vectorToTarget.y = player.getY();
				Vector2 vector2 = zombie.vectorToTarget;
				vector2.x -= zombie.getX();
				vector2 = zombie.vectorToTarget;
				vector2.y -= zombie.getY();
				zombie.TimeSinceSeenFlesh = 0.0F;
				zombie.setDefaultState();
				zombie.Lunge();
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static int getFastForwardDamageMultiplier() {
		GameTime gameTime = GameTime.getInstance();
		if (GameServer.bServer) {
			return (int)(GameServer.bFastForward ? ServerOptions.instance.FastForwardMultiplier.getValue() / (double)gameTime.getDeltaMinutesPerDay() : 1.0);
		} else if (GameClient.bClient) {
			return (int)(GameClient.bFastForward ? ServerOptions.instance.FastForwardMultiplier.getValue() / (double)gameTime.getDeltaMinutesPerDay() : 1.0);
		} else {
			return IsoPlayer.allPlayersAsleep() ? (int)(200.0F * (30.0F / (float)PerformanceSettings.LockFPS) / 1.6F) : (int)gameTime.getTrueMultiplier();
		}
	}
}
