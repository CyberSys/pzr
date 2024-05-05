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
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoChunk;
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
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;


public final class ThumpState extends State {
	private static final ThumpState _instance = new ThumpState();

	public static ThumpState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		if (!GameClient.bClient || gameCharacter.isLocal()) {
			switch (Rand.Next(3)) {
			case 0: 
				gameCharacter.setVariable("ThumpType", "DoorClaw");
				break;
			
			case 1: 
				gameCharacter.setVariable("ThumpType", "Door");
				break;
			
			case 2: 
				gameCharacter.setVariable("ThumpType", "DoorBang");
			
			}
		}

		if (GameClient.bClient && gameCharacter.isLocal()) {
			GameClient.sendThump(gameCharacter, gameCharacter.getThumpTarget());
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		Thumpable thumpable = gameCharacter.getThumpTarget();
		if (thumpable instanceof IsoObject) {
			gameCharacter.faceThisObject((IsoObject)thumpable);
		}

		boolean boolean1 = GameServer.bServer && GameServer.bFastForward || !GameServer.bServer && IsoPlayer.allPlayersAsleep();
		if (boolean1 || gameCharacter.actionContext.hasEventOccurred("thumpframe")) {
			gameCharacter.actionContext.clearEvent("thumpframe");
			gameCharacter.setTimeThumping(gameCharacter.getTimeThumping() + 1);
			if (zombie.TimeSinceSeenFlesh < 5.0F) {
				gameCharacter.setTimeThumping(0);
			}

			int int1 = 1;
			if (gameCharacter.getCurrentSquare() != null) {
				int1 = gameCharacter.getCurrentSquare().getMovingObjects().size();
			}

			for (int int2 = 0; int2 < int1 && this.isThumpTargetValid(gameCharacter, gameCharacter.getThumpTarget()); ++int2) {
				gameCharacter.getThumpTarget().Thump(gameCharacter);
			}

			Thumpable thumpable2 = gameCharacter.getThumpTarget() == null ? null : gameCharacter.getThumpTarget().getThumpableFor(gameCharacter);
			boolean boolean2 = GameServer.bServer || SoundManager.instance.isListenerInRange(gameCharacter.x, gameCharacter.y, 20.0F);
			if (boolean2 && !IsoPlayer.allPlayersAsleep()) {
				if (thumpable2 instanceof IsoWindow) {
					zombie.setThumpFlag(Rand.Next(3) == 0 ? 2 : 3);
					zombie.setThumpCondition(thumpable2.getThumpCondition());
					if (!GameServer.bServer) {
						ZombieThumpManager.instance.addCharacter(zombie);
					}
				} else if (thumpable2 != null) {
					String string = "ZombieThumpGeneric";
					IsoBarricade barricade = (IsoBarricade)Type.tryCastTo(thumpable2, IsoBarricade.class);
					if (barricade == null || !barricade.isMetal() && !barricade.isMetalBar()) {
						if (thumpable2 instanceof IsoDoor) {
							string = ((IsoDoor)thumpable2).getThumpSound();
						} else if (thumpable2 instanceof IsoThumpable) {
							string = ((IsoThumpable)thumpable2).getThumpSound();
						}
					} else {
						string = "ZombieThumpMetal";
					}

					if ("ZombieThumpGeneric".equals(string)) {
						zombie.setThumpFlag(1);
					} else if ("ZombieThumpWindow".equals(string)) {
						zombie.setThumpFlag(3);
					} else if ("ZombieThumpMetal".equals(string)) {
						zombie.setThumpFlag(4);
					} else {
						zombie.setThumpFlag(1);
					}

					zombie.setThumpCondition(thumpable2.getThumpCondition());
					if (!GameServer.bServer) {
						ZombieThumpManager.instance.addCharacter(zombie);
					}
				}
			}
		}

		if (!this.isThumpTargetValid(gameCharacter, gameCharacter.getThumpTarget())) {
			gameCharacter.setThumpTarget((Thumpable)null);
			gameCharacter.setTimeThumping(0);
			if (thumpable instanceof IsoWindow && ((IsoWindow)thumpable).canClimbThrough(gameCharacter)) {
				gameCharacter.climbThroughWindow((IsoWindow)thumpable);
			} else {
				IsoGridSquare square;
				IsoGridSquare square2;
				if (thumpable instanceof IsoDoor && (((IsoDoor)thumpable).open || thumpable.isDestroyed())) {
					IsoDoor door = (IsoDoor)thumpable;
					square = door.getSquare();
					square2 = door.getOppositeSquare();
					if (this.lungeThroughDoor(zombie, square, square2)) {
						return;
					}
				}

				if (thumpable instanceof IsoThumpable && ((IsoThumpable)thumpable).isDoor && (((IsoThumpable)thumpable).open || thumpable.isDestroyed())) {
					IsoThumpable thumpable3 = (IsoThumpable)thumpable;
					square = thumpable3.getSquare();
					square2 = thumpable3.getInsideSquare();
					if (this.lungeThroughDoor(zombie, square, square2)) {
						return;
					}
				}

				if (zombie.LastTargetSeenX != -1) {
					gameCharacter.pathToLocation(zombie.LastTargetSeenX, zombie.LastTargetSeenY, zombie.LastTargetSeenZ);
				}
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setThumpTarget((Thumpable)null);
		((IsoZombie)gameCharacter).setThumpTimer(200);
		if (GameClient.bClient && gameCharacter.isLocal()) {
			GameClient.sendThump(gameCharacter, gameCharacter.getThumpTarget());
		}
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if (animEvent.m_EventName.equalsIgnoreCase("ThumpFrame")) {
		}
	}

	private IsoPlayer findPlayer(int int1, int int2, int int3, int int4, int int5) {
		for (int int6 = int3; int6 <= int4; ++int6) {
			for (int int7 = int1; int7 <= int2; ++int7) {
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int7, int6, int5);
				if (square != null) {
					for (int int8 = 0; int8 < square.getMovingObjects().size(); ++int8) {
						IsoMovingObject movingObject = (IsoMovingObject)square.getMovingObjects().get(int8);
						if (movingObject instanceof IsoPlayer && !((IsoPlayer)movingObject).isGhostMode()) {
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
				zombie.setTarget(player);
				zombie.vectorToTarget.x = player.getX();
				zombie.vectorToTarget.y = player.getY();
				Vector2 vector2 = zombie.vectorToTarget;
				vector2.x -= zombie.getX();
				vector2 = zombie.vectorToTarget;
				vector2.y -= zombie.getY();
				zombie.TimeSinceSeenFlesh = 0.0F;
				zombie.setThumpTarget((Thumpable)null);
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
			return IsoPlayer.allPlayersAsleep() ? (int)(200.0F * (30.0F / (float)PerformanceSettings.getLockFPS()) / 1.6F) : (int)gameTime.getTrueMultiplier();
		}
	}

	private boolean isThumpTargetValid(IsoGameCharacter gameCharacter, Thumpable thumpable) {
		if (thumpable == null) {
			return false;
		} else if (thumpable.isDestroyed()) {
			return false;
		} else {
			IsoObject object = (IsoObject)Type.tryCastTo(thumpable, IsoObject.class);
			if (object == null) {
				return false;
			} else if (thumpable instanceof BaseVehicle) {
				return object.getMovingObjectIndex() != -1;
			} else if (object.getObjectIndex() == -1) {
				return false;
			} else {
				int int1 = object.getSquare().getX() / 10;
				int int2 = object.getSquare().getY() / 10;
				IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int1, int2) : IsoWorld.instance.CurrentCell.getChunk(int1, int2);
				if (chunk == null) {
					return false;
				} else {
					return thumpable.getThumpableFor(gameCharacter) != null;
				}
			}
		}
	}
}
