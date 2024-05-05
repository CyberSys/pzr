package zombie.ai.states;

import java.util.HashMap;
import zombie.GameTime;
import zombie.ai.State;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.MoveDeltaModifiers;
import zombie.characters.skills.PerkFactory;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.util.Type;


public final class ClimbThroughWindowState extends State {
	private static final ClimbThroughWindowState _instance = new ClimbThroughWindowState();
	static final Integer PARAM_START_X = 0;
	static final Integer PARAM_START_Y = 1;
	static final Integer PARAM_Z = 2;
	static final Integer PARAM_OPPOSITE_X = 3;
	static final Integer PARAM_OPPOSITE_Y = 4;
	static final Integer PARAM_DIR = 5;
	static final Integer PARAM_ZOMBIE_ON_FLOOR = 6;
	static final Integer PARAM_PREV_STATE = 7;
	static final Integer PARAM_SCRATCH = 8;
	static final Integer PARAM_COUNTER = 9;
	static final Integer PARAM_SOLID_FLOOR = 10;
	static final Integer PARAM_SHEET_ROPE = 11;
	static final Integer PARAM_END_X = 12;
	static final Integer PARAM_END_Y = 13;

	public static ClimbThroughWindowState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(true);
		gameCharacter.setHideWeaponModel(true);
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		boolean boolean1 = hashMap.get(PARAM_COUNTER) == Boolean.TRUE;
		gameCharacter.setVariable("ClimbWindowStarted", false);
		gameCharacter.setVariable("ClimbWindowEnd", false);
		gameCharacter.setVariable("ClimbWindowFinished", false);
		gameCharacter.clearVariable("ClimbWindowGetUpBack");
		gameCharacter.clearVariable("ClimbWindowGetUpFront");
		gameCharacter.setVariable("ClimbWindowOutcome", boolean1 ? "obstacle" : "success");
		gameCharacter.clearVariable("ClimbWindowFlopped");
		IsoZombie zombie = (IsoZombie)Type.tryCastTo(gameCharacter, IsoZombie.class);
		if (!boolean1 && zombie != null && zombie.shouldDoFenceLunge()) {
			this.setLungeXVars(zombie);
			gameCharacter.setVariable("ClimbWindowOutcome", "lunge");
		}

		if (hashMap.get(PARAM_SOLID_FLOOR) == Boolean.FALSE) {
			gameCharacter.setVariable("ClimbWindowOutcome", "fall");
		}

		if (!(gameCharacter instanceof IsoZombie) && hashMap.get(PARAM_SHEET_ROPE) == Boolean.TRUE) {
			gameCharacter.setVariable("ClimbWindowOutcome", "rope");
		}

		if (gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
			((IsoPlayer)gameCharacter).dirtyRecalcGridStackTime = 20.0F;
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		if (!this.isWindowClosing(gameCharacter)) {
			IsoDirections directions = (IsoDirections)hashMap.get(PARAM_DIR);
			gameCharacter.setDir(directions);
			String string = gameCharacter.getVariableString("ClimbWindowOutcome");
			int int1;
			int int2;
			if (gameCharacter instanceof IsoZombie) {
				boolean boolean1 = hashMap.get(PARAM_ZOMBIE_ON_FLOOR) == Boolean.TRUE;
				if (!gameCharacter.isFallOnFront() && boolean1) {
					int int3 = (Integer)hashMap.get(PARAM_OPPOSITE_X);
					int1 = (Integer)hashMap.get(PARAM_OPPOSITE_Y);
					int2 = (Integer)hashMap.get(PARAM_Z);
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int3, int1, int2);
					if (square != null && square.getBrokenGlass() != null) {
						gameCharacter.addBlood(BloodBodyPartType.Head, true, true, true);
						gameCharacter.addBlood(BloodBodyPartType.Head, true, true, true);
						gameCharacter.addBlood(BloodBodyPartType.Head, true, true, true);
						gameCharacter.addBlood(BloodBodyPartType.Head, true, true, true);
						gameCharacter.addBlood(BloodBodyPartType.Head, true, true, true);
						gameCharacter.addBlood(BloodBodyPartType.Neck, true, true, true);
						gameCharacter.addBlood(BloodBodyPartType.Neck, true, true, true);
						gameCharacter.addBlood(BloodBodyPartType.Neck, true, true, true);
						gameCharacter.addBlood(BloodBodyPartType.Neck, true, true, true);
						gameCharacter.addBlood(BloodBodyPartType.Torso_Upper, true, true, true);
						gameCharacter.addBlood(BloodBodyPartType.Torso_Upper, true, true, true);
						gameCharacter.addBlood(BloodBodyPartType.Torso_Upper, true, true, true);
					}
				}

				gameCharacter.setOnFloor(boolean1);
				((IsoZombie)gameCharacter).setKnockedDown(boolean1);
				gameCharacter.setFallOnFront(boolean1);
			}

			float float1 = (float)(Integer)hashMap.get(PARAM_START_X) + 0.5F;
			float float2 = (float)(Integer)hashMap.get(PARAM_START_Y) + 0.5F;
			if (!gameCharacter.getVariableBoolean("ClimbWindowStarted")) {
				if (gameCharacter.x != float1 && (directions == IsoDirections.N || directions == IsoDirections.S)) {
					this.slideX(gameCharacter, float1);
				}

				if (gameCharacter.y != float2 && (directions == IsoDirections.W || directions == IsoDirections.E)) {
					this.slideY(gameCharacter, float2);
				}
			}

			float float3;
			float float4;
			if (gameCharacter instanceof IsoPlayer && string.equalsIgnoreCase("obstacle")) {
				float3 = (float)(Integer)hashMap.get(PARAM_END_X) + 0.5F;
				float4 = (float)(Integer)hashMap.get(PARAM_END_Y) + 0.5F;
				if (gameCharacter.DistToSquared(float3, float4) < 0.5625F) {
					gameCharacter.setVariable("ClimbWindowOutcome", "obstacleEnd");
				}
			}

			if (gameCharacter instanceof IsoPlayer && !gameCharacter.getVariableBoolean("ClimbWindowEnd") && !"fallfront".equals(string) && !"back".equals(string) && !"fallback".equals(string)) {
				int1 = (Integer)hashMap.get(PARAM_OPPOSITE_X);
				int2 = (Integer)hashMap.get(PARAM_OPPOSITE_Y);
				int int4 = (Integer)hashMap.get(PARAM_Z);
				IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int4);
				if (square2 != null) {
					this.checkForFallingBack(square2, gameCharacter);
					if (square2 != gameCharacter.getSquare() && square2.TreatAsSolidFloor()) {
						this.checkForFallingFront(gameCharacter.getSquare(), gameCharacter);
					}
				}
			}

			if (gameCharacter.getVariableBoolean("ClimbWindowStarted") && !"back".equals(string) && !"fallback".equals(string) && !"lunge".equals(string) && !"obstacle".equals(string) && !"obstacleEnd".equals(string)) {
				float3 = (float)(Integer)hashMap.get(PARAM_START_X);
				float4 = (float)(Integer)hashMap.get(PARAM_START_Y);
				switch (directions) {
				case N: 
					float4 -= 0.1F;
					break;
				
				case S: 
					++float4;
					break;
				
				case W: 
					float3 -= 0.1F;
					break;
				
				case E: 
					++float3;
				
				}

				if ((int)gameCharacter.x != (int)float3 && (directions == IsoDirections.W || directions == IsoDirections.E)) {
					this.slideX(gameCharacter, float3);
				}

				if ((int)gameCharacter.y != (int)float4 && (directions == IsoDirections.N || directions == IsoDirections.S)) {
					this.slideY(gameCharacter, float4);
				}
			}

			if (gameCharacter.getVariableBoolean("ClimbWindowStarted") && hashMap.get(PARAM_SCRATCH) == Boolean.TRUE) {
				hashMap.put(PARAM_SCRATCH, Boolean.FALSE);
				gameCharacter.getBodyDamage().setScratchedWindow();
			}
		}
	}

	private void checkForFallingBack(IsoGridSquare square, IsoGameCharacter gameCharacter) {
		for (int int1 = 0; int1 < square.getMovingObjects().size(); ++int1) {
			IsoMovingObject movingObject = (IsoMovingObject)square.getMovingObjects().get(int1);
			IsoZombie zombie = (IsoZombie)Type.tryCastTo(movingObject, IsoZombie.class);
			if (zombie != null && !zombie.isOnFloor() && !zombie.isSitAgainstWall()) {
				if (!zombie.isVariable("AttackOutcome", "success") && Rand.Next(5 + gameCharacter.getPerkLevel(PerkFactory.Perks.Fitness)) != 0) {
					zombie.playHurtSound();
					gameCharacter.setVariable("ClimbWindowOutcome", "back");
				} else {
					zombie.playHurtSound();
					gameCharacter.setVariable("ClimbWindowOutcome", "fallback");
				}
			}
		}
	}

	private void checkForFallingFront(IsoGridSquare square, IsoGameCharacter gameCharacter) {
		for (int int1 = 0; int1 < square.getMovingObjects().size(); ++int1) {
			IsoMovingObject movingObject = (IsoMovingObject)square.getMovingObjects().get(int1);
			IsoZombie zombie = (IsoZombie)Type.tryCastTo(movingObject, IsoZombie.class);
			if (zombie != null && !zombie.isOnFloor() && !zombie.isSitAgainstWall() && zombie.isVariable("AttackOutcome", "success")) {
				zombie.playHurtSound();
				gameCharacter.setVariable("ClimbWindowOutcome", "fallfront");
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(false);
		gameCharacter.setHideWeaponModel(false);
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		if (gameCharacter.isVariable("ClimbWindowOutcome", "fall") || gameCharacter.isVariable("ClimbWindowOutcome", "fallback") || gameCharacter.isVariable("ClimbWindowOutcome", "fallfront")) {
			gameCharacter.setHitReaction("");
		}

		gameCharacter.clearVariable("ClimbWindowFinished");
		gameCharacter.clearVariable("ClimbWindowOutcome");
		gameCharacter.clearVariable("ClimbWindowStarted");
		gameCharacter.clearVariable("ClimbWindowFlopped");
		if (gameCharacter instanceof IsoZombie) {
			gameCharacter.setOnFloor(false);
			((IsoZombie)gameCharacter).setKnockedDown(false);
		}

		IsoZombie zombie = (IsoZombie)Type.tryCastTo(gameCharacter, IsoZombie.class);
		if (zombie != null) {
			zombie.AllowRepathDelay = 0.0F;
			if (hashMap.get(PARAM_PREV_STATE) == PathFindState.instance()) {
				if (gameCharacter.getPathFindBehavior2().getTargetChar() == null) {
					gameCharacter.setVariable("bPathFind", true);
					gameCharacter.setVariable("bMoving", false);
				} else if (zombie.isTargetLocationKnown()) {
					gameCharacter.pathToCharacter(gameCharacter.getPathFindBehavior2().getTargetChar());
				} else if (zombie.LastTargetSeenX != -1) {
					gameCharacter.pathToLocation(zombie.LastTargetSeenX, zombie.LastTargetSeenY, zombie.LastTargetSeenZ);
				}
			} else if (hashMap.get(PARAM_PREV_STATE) == WalkTowardState.instance() || hashMap.get(PARAM_PREV_STATE) == WalkTowardNetworkState.instance()) {
				gameCharacter.setVariable("bPathFind", false);
				gameCharacter.setVariable("bMoving", true);
			}
		}

		if (gameCharacter instanceof IsoZombie) {
			((IsoZombie)gameCharacter).networkAI.isClimbing = false;
		}
	}

	public void slideX(IsoGameCharacter gameCharacter, float float1) {
		float float2 = 0.05F * GameTime.getInstance().getMultiplier() / 1.6F;
		float2 = float1 > gameCharacter.x ? Math.min(float2, float1 - gameCharacter.x) : Math.max(-float2, float1 - gameCharacter.x);
		gameCharacter.x += float2;
		gameCharacter.nx = gameCharacter.x;
	}

	public void slideY(IsoGameCharacter gameCharacter, float float1) {
		float float2 = 0.05F * GameTime.getInstance().getMultiplier() / 1.6F;
		float2 = float1 > gameCharacter.y ? Math.min(float2, float1 - gameCharacter.y) : Math.max(-float2, float1 - gameCharacter.y);
		gameCharacter.y += float2;
		gameCharacter.ny = gameCharacter.y;
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		IsoZombie zombie = (IsoZombie)Type.tryCastTo(gameCharacter, IsoZombie.class);
		if (animEvent.m_EventName.equalsIgnoreCase("CheckAttack") && zombie != null && zombie.target instanceof IsoGameCharacter) {
			((IsoGameCharacter)zombie.target).attackFromWindowsLunge(zombie);
		}

		if (animEvent.m_EventName.equalsIgnoreCase("OnFloor") && zombie != null) {
			boolean boolean1 = Boolean.parseBoolean(animEvent.m_ParameterValue);
			hashMap.put(PARAM_ZOMBIE_ON_FLOOR, boolean1);
			if (boolean1) {
				this.setLungeXVars(zombie);
				IsoThumpable thumpable = (IsoThumpable)Type.tryCastTo(this.getWindow(gameCharacter), IsoThumpable.class);
				if (thumpable != null && thumpable.getSquare() != null && zombie.target != null) {
					thumpable.Health -= Rand.Next(10, 20);
					if (thumpable.Health <= 0) {
						thumpable.destroy();
					}
				}

				gameCharacter.setVariable("ClimbWindowFlopped", true);
			}
		}
	}

	public boolean isIgnoreCollide(IsoGameCharacter gameCharacter, int int1, int int2, int int3, int int4, int int5, int int6) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		int int7 = (Integer)hashMap.get(PARAM_START_X);
		int int8 = (Integer)hashMap.get(PARAM_START_Y);
		int int9 = (Integer)hashMap.get(PARAM_END_X);
		int int10 = (Integer)hashMap.get(PARAM_END_Y);
		int int11 = (Integer)hashMap.get(PARAM_Z);
		if (int11 == int3 && int11 == int6) {
			int int12 = PZMath.min(int7, int9);
			int int13 = PZMath.min(int8, int10);
			int int14 = PZMath.max(int7, int9);
			int int15 = PZMath.max(int8, int10);
			int int16 = PZMath.min(int1, int4);
			int int17 = PZMath.min(int2, int5);
			int int18 = PZMath.max(int1, int4);
			int int19 = PZMath.max(int2, int5);
			return int12 <= int16 && int13 <= int17 && int14 >= int18 && int15 >= int19;
		} else {
			return false;
		}
	}

	public IsoObject getWindow(IsoGameCharacter gameCharacter) {
		if (!gameCharacter.isCurrentState(this)) {
			return null;
		} else {
			HashMap hashMap = gameCharacter.getStateMachineParams(this);
			int int1 = (Integer)hashMap.get(PARAM_START_X);
			int int2 = (Integer)hashMap.get(PARAM_START_Y);
			int int3 = (Integer)hashMap.get(PARAM_Z);
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			int int4 = (Integer)hashMap.get(PARAM_END_X);
			int int5 = (Integer)hashMap.get(PARAM_END_Y);
			IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(int4, int5, int3);
			if (square != null && square2 != null) {
				Object object = square.getWindowTo(square2);
				if (object == null) {
					object = square.getWindowThumpableTo(square2);
				}

				if (object == null) {
					object = square.getHoppableTo(square2);
				}

				return (IsoObject)object;
			} else {
				return null;
			}
		}
	}

	public boolean isWindowClosing(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		if (gameCharacter.getVariableBoolean("ClimbWindowStarted")) {
			return false;
		} else {
			int int1 = (Integer)hashMap.get(PARAM_START_X);
			int int2 = (Integer)hashMap.get(PARAM_START_Y);
			int int3 = (Integer)hashMap.get(PARAM_Z);
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			if (gameCharacter.getCurrentSquare() != square) {
				return false;
			} else {
				IsoWindow window = (IsoWindow)Type.tryCastTo(this.getWindow(gameCharacter), IsoWindow.class);
				if (window == null) {
					return false;
				} else {
					IsoGameCharacter gameCharacter2 = window.getFirstCharacterClosing();
					if (gameCharacter2 != null && gameCharacter2.isVariable("CloseWindowOutcome", "success")) {
						if (gameCharacter.isZombie()) {
							gameCharacter.setHitReaction("HeadLeft");
						} else {
							gameCharacter.setVariable("ClimbWindowFinished", true);
						}

						return true;
					} else {
						return false;
					}
				}
			}
		}
	}

	public void getDeltaModifiers(IsoGameCharacter gameCharacter, MoveDeltaModifiers moveDeltaModifiers) {
		boolean boolean1 = gameCharacter.getPath2() != null;
		boolean boolean2 = gameCharacter instanceof IsoPlayer;
		if (boolean1 && boolean2) {
			moveDeltaModifiers.turnDelta = Math.max(moveDeltaModifiers.turnDelta, 10.0F);
		}

		if (boolean2 && gameCharacter.getVariableBoolean("isTurning")) {
			moveDeltaModifiers.turnDelta = Math.max(moveDeltaModifiers.turnDelta, 5.0F);
		}
	}

	private boolean isFreeSquare(IsoGridSquare square) {
		return square != null && square.TreatAsSolidFloor() && !square.Is(IsoFlagType.solid) && !square.Is(IsoFlagType.solidtrans);
	}

	private boolean isObstacleSquare(IsoGridSquare square) {
		return square != null && square.TreatAsSolidFloor() && !square.Is(IsoFlagType.solid) && square.Is(IsoFlagType.solidtrans) && !square.Is(IsoFlagType.water);
	}

	private IsoGridSquare getFreeSquareAfterObstacles(IsoGridSquare square, IsoDirections directions) {
		while (true) {
			IsoGridSquare square2 = square.getAdjacentSquare(directions);
			if (square2 == null || square.isSomethingTo(square2) || square.getWindowFrameTo(square2) != null || square.getWindowThumpableTo(square2) != null) {
				return null;
			}

			if (this.isFreeSquare(square2)) {
				return square2;
			}

			if (!this.isObstacleSquare(square2)) {
				return null;
			}

			square = square2;
		}
	}

	private void setLungeXVars(IsoZombie zombie) {
		IsoMovingObject movingObject = zombie.getTarget();
		if (movingObject != null) {
			zombie.setVariable("FenceLungeX", 0.0F);
			zombie.setVariable("FenceLungeY", 0.0F);
			float float1 = 0.0F;
			Vector2 vector2 = zombie.getForwardDirection();
			PZMath.SideOfLine sideOfLine = PZMath.testSideOfLine(zombie.x, zombie.y, zombie.x + vector2.x, zombie.y + vector2.y, movingObject.x, movingObject.y);
			float float2 = (float)Math.acos((double)zombie.getDotWithForwardDirection(movingObject.x, movingObject.y));
			float float3 = PZMath.clamp(PZMath.radToDeg(float2), 0.0F, 90.0F);
			switch (sideOfLine) {
			case Left: 
				float1 = -float3 / 90.0F;
				break;
			
			case OnLine: 
				float1 = 0.0F;
				break;
			
			case Right: 
				float1 = float3 / 90.0F;
			
			}

			zombie.setVariable("FenceLungeX", float1);
		}
	}

	public boolean isPastInnerEdgeOfSquare(IsoGameCharacter gameCharacter, int int1, int int2, IsoDirections directions) {
		if (directions == IsoDirections.N) {
			return gameCharacter.y < (float)(int2 + 1) - 0.3F;
		} else if (directions == IsoDirections.S) {
			return gameCharacter.y > (float)int2 + 0.3F;
		} else if (directions == IsoDirections.W) {
			return gameCharacter.x < (float)(int1 + 1) - 0.3F;
		} else if (directions == IsoDirections.E) {
			return gameCharacter.x > (float)int1 + 0.3F;
		} else {
			throw new IllegalArgumentException("unhandled direction");
		}
	}

	public boolean isPastOuterEdgeOfSquare(IsoGameCharacter gameCharacter, int int1, int int2, IsoDirections directions) {
		if (directions == IsoDirections.N) {
			return gameCharacter.y < (float)int2 - 0.3F;
		} else if (directions == IsoDirections.S) {
			return gameCharacter.y > (float)(int2 + 1) + 0.3F;
		} else if (directions == IsoDirections.W) {
			return gameCharacter.x < (float)int1 - 0.3F;
		} else if (directions == IsoDirections.E) {
			return gameCharacter.x > (float)(int1 + 1) + 0.3F;
		} else {
			throw new IllegalArgumentException("unhandled direction");
		}
	}

	public void setParams(IsoGameCharacter gameCharacter, IsoObject object) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		hashMap.clear();
		boolean boolean1 = false;
		boolean boolean2;
		if (object instanceof IsoWindow) {
			IsoWindow window = (IsoWindow)object;
			boolean2 = window.north;
			if (gameCharacter instanceof IsoPlayer && window.isDestroyed() && !window.isGlassRemoved() && Rand.Next(2) == 0) {
				boolean1 = true;
			}
		} else if (object instanceof IsoThumpable) {
			IsoThumpable thumpable = (IsoThumpable)object;
			boolean2 = thumpable.north;
			if (gameCharacter instanceof IsoPlayer && thumpable.getName().equals("Barbed Fence") && Rand.Next(101) > 75) {
				boolean1 = true;
			}
		} else {
			if (!IsoWindowFrame.isWindowFrame(object)) {
				throw new IllegalArgumentException("expected thumpable, window, or window-frame");
			}

			boolean2 = IsoWindowFrame.isWindowFrame(object, true);
		}

		int int1 = object.getSquare().getX();
		int int2 = object.getSquare().getY();
		int int3 = object.getSquare().getZ();
		int int4 = int1;
		int int5 = int2;
		int int6 = int1;
		int int7 = int2;
		IsoDirections directions;
		if (boolean2) {
			if ((float)int2 < gameCharacter.getY()) {
				int7 = int2 - 1;
				directions = IsoDirections.N;
			} else {
				int5 = int2 - 1;
				directions = IsoDirections.S;
			}
		} else if ((float)int1 < gameCharacter.getX()) {
			int6 = int1 - 1;
			directions = IsoDirections.W;
		} else {
			int4 = int1 - 1;
			directions = IsoDirections.E;
		}

		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int6, int7, int3);
		boolean boolean3 = square != null && square.Is(IsoFlagType.solidtrans);
		boolean boolean4 = square != null && square.TreatAsSolidFloor();
		boolean boolean5 = square != null && gameCharacter.canClimbDownSheetRope(square);
		int int8 = int6;
		int int9 = int7;
		IsoGridSquare square2;
		if (boolean3 && gameCharacter.isZombie()) {
			square2 = square.getAdjacentSquare(directions);
			if (this.isFreeSquare(square2) && !square.isSomethingTo(square2) && square.getWindowFrameTo(square2) == null && square.getWindowThumpableTo(square2) == null) {
				int8 = square2.x;
				int9 = square2.y;
			} else {
				boolean3 = false;
			}
		}

		if (boolean3 && !gameCharacter.isZombie()) {
			square2 = this.getFreeSquareAfterObstacles(square, directions);
			if (square2 == null) {
				boolean3 = false;
			} else {
				int8 = square2.x;
				int9 = square2.y;
			}
		}

		hashMap.put(PARAM_START_X, int4);
		hashMap.put(PARAM_START_Y, int5);
		hashMap.put(PARAM_Z, int3);
		hashMap.put(PARAM_OPPOSITE_X, int6);
		hashMap.put(PARAM_OPPOSITE_Y, int7);
		hashMap.put(PARAM_END_X, int8);
		hashMap.put(PARAM_END_Y, int9);
		hashMap.put(PARAM_DIR, directions);
		hashMap.put(PARAM_ZOMBIE_ON_FLOOR, Boolean.FALSE);
		hashMap.put(PARAM_PREV_STATE, gameCharacter.getCurrentState());
		hashMap.put(PARAM_SCRATCH, boolean1 ? Boolean.TRUE : Boolean.FALSE);
		hashMap.put(PARAM_COUNTER, boolean3 ? Boolean.TRUE : Boolean.FALSE);
		hashMap.put(PARAM_SOLID_FLOOR, boolean4 ? Boolean.TRUE : Boolean.FALSE);
		hashMap.put(PARAM_SHEET_ROPE, boolean5 ? Boolean.TRUE : Boolean.FALSE);
	}
}
