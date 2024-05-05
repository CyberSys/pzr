package zombie.ai.states;

import fmod.fmod.FMODManager;
import java.util.HashMap;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameTime;
import zombie.ZomboidGlobals;
import zombie.ai.State;
import zombie.audio.parameters.ParameterCharacterMovementSpeed;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.MoveDeltaModifiers;
import zombie.characters.Stats;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.skills.PerkFactory;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.properties.PropertyContainer;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.debug.DebugOptions;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoThumpable;
import zombie.util.StringUtils;
import zombie.util.Type;


public final class ClimbOverFenceState extends State {
	private static final ClimbOverFenceState _instance = new ClimbOverFenceState();
	static final Integer PARAM_START_X = 0;
	static final Integer PARAM_START_Y = 1;
	static final Integer PARAM_Z = 2;
	static final Integer PARAM_END_X = 3;
	static final Integer PARAM_END_Y = 4;
	static final Integer PARAM_DIR = 5;
	static final Integer PARAM_ZOMBIE_ON_FLOOR = 6;
	static final Integer PARAM_PREV_STATE = 7;
	static final Integer PARAM_SCRATCH = 8;
	static final Integer PARAM_COUNTER = 9;
	static final Integer PARAM_SOLID_FLOOR = 10;
	static final Integer PARAM_SHEET_ROPE = 11;
	static final Integer PARAM_RUN = 12;
	static final Integer PARAM_SPRINT = 13;
	static final Integer PARAM_COLLIDABLE = 14;
	static final int FENCE_TYPE_WOOD = 0;
	static final int FENCE_TYPE_METAL = 1;
	static final int FENCE_TYPE_SANDBAG = 2;
	static final int FENCE_TYPE_GRAVELBAG = 3;
	static final int FENCE_TYPE_BARBWIRE = 4;
	static final int FENCE_TYPE_ROADBLOCK = 5;
	static final int TRIP_WOOD = 0;
	static final int TRIP_METAL = 1;
	static final int TRIP_SANDBAG = 2;
	static final int TRIP_GRAVELBAG = 3;
	static final int TRIP_BARBWIRE = 4;
	public static final int TRIP_TREE = 5;
	public static final int TRIP_ZOMBIE = 6;
	public static final int COLLIDE_WITH_WALL = 7;

	public static ClimbOverFenceState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setVariable("FenceLungeX", 0.0F);
		gameCharacter.setVariable("FenceLungeY", 0.0F);
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		gameCharacter.setIgnoreMovement(true);
		Stats stats;
		if (hashMap.get(PARAM_RUN) == Boolean.TRUE) {
			gameCharacter.setVariable("VaultOverRun", true);
			stats = gameCharacter.getStats();
			stats.endurance = (float)((double)stats.endurance - ZomboidGlobals.RunningEnduranceReduce * 300.0);
		} else if (hashMap.get(PARAM_SPRINT) == Boolean.TRUE) {
			gameCharacter.setVariable("VaultOverSprint", true);
			stats = gameCharacter.getStats();
			stats.endurance = (float)((double)stats.endurance - ZomboidGlobals.RunningEnduranceReduce * 700.0);
		}

		boolean boolean1 = hashMap.get(PARAM_COUNTER) == Boolean.TRUE;
		gameCharacter.setVariable("ClimbingFence", true);
		gameCharacter.setVariable("ClimbFenceStarted", false);
		gameCharacter.setVariable("ClimbFenceFinished", false);
		gameCharacter.setVariable("ClimbFenceOutcome", boolean1 ? "obstacle" : "success");
		gameCharacter.clearVariable("ClimbFenceFlopped");
		if ((gameCharacter.getVariableBoolean("VaultOverRun") || gameCharacter.getVariableBoolean("VaultOverSprint")) && this.shouldFallAfterVaultOver(gameCharacter)) {
			gameCharacter.setVariable("ClimbFenceOutcome", "fall");
		}

		IsoZombie zombie = (IsoZombie)Type.tryCastTo(gameCharacter, IsoZombie.class);
		if (!boolean1 && zombie != null && zombie.shouldDoFenceLunge()) {
			gameCharacter.setVariable("ClimbFenceOutcome", "lunge");
			this.setLungeXVars(zombie);
		}

		if (hashMap.get(PARAM_SOLID_FLOOR) == Boolean.FALSE) {
			gameCharacter.setVariable("ClimbFenceOutcome", "falling");
		}

		if (!(gameCharacter instanceof IsoZombie) && hashMap.get(PARAM_SHEET_ROPE) == Boolean.TRUE) {
			gameCharacter.setVariable("ClimbFenceOutcome", "rope");
		}

		if (gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
			((IsoPlayer)gameCharacter).dirtyRecalcGridStackTime = 20.0F;
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

	public void execute(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		IsoDirections directions = (IsoDirections)Type.tryCastTo(hashMap.get(PARAM_DIR), IsoDirections.class);
		int int1 = (Integer)hashMap.get(PARAM_END_X);
		int int2 = (Integer)hashMap.get(PARAM_END_Y);
		gameCharacter.setAnimated(true);
		if (directions == IsoDirections.N) {
			gameCharacter.setDir(IsoDirections.N);
		} else if (directions == IsoDirections.S) {
			gameCharacter.setDir(IsoDirections.S);
		} else if (directions == IsoDirections.W) {
			gameCharacter.setDir(IsoDirections.W);
		} else if (directions == IsoDirections.E) {
			gameCharacter.setDir(IsoDirections.E);
		}

		String string = gameCharacter.getVariableString("ClimbFenceOutcome");
		float float1;
		if (!"lunge".equals(string)) {
			float1 = 0.05F;
			if (directions != IsoDirections.N && directions != IsoDirections.S) {
				if (directions == IsoDirections.W || directions == IsoDirections.E) {
					gameCharacter.y = gameCharacter.ny = PZMath.clamp(gameCharacter.y, (float)int2 + float1, (float)(int2 + 1) - float1);
				}
			} else {
				gameCharacter.x = gameCharacter.nx = PZMath.clamp(gameCharacter.x, (float)int1 + float1, (float)(int1 + 1) - float1);
			}
		}

		if (gameCharacter.getVariableBoolean("ClimbFenceStarted") && !"back".equals(string) && !"fallback".equals(string) && !"lunge".equalsIgnoreCase(string) && !"obstacle".equals(string) && !"obstacleEnd".equals(string)) {
			float1 = (float)(Integer)hashMap.get(PARAM_START_X);
			float float2 = (float)(Integer)hashMap.get(PARAM_START_Y);
			switch (directions) {
			case N: 
				float2 -= 0.1F;
				break;
			
			case S: 
				++float2;
				break;
			
			case W: 
				float1 -= 0.1F;
				break;
			
			case E: 
				++float1;
			
			}

			if ((int)gameCharacter.x != (int)float1 && (directions == IsoDirections.W || directions == IsoDirections.E)) {
				this.slideX(gameCharacter, float1);
			}

			if ((int)gameCharacter.y != (int)float2 && (directions == IsoDirections.N || directions == IsoDirections.S)) {
				this.slideY(gameCharacter, float2);
			}
		}

		if (gameCharacter instanceof IsoZombie) {
			boolean boolean1 = hashMap.get(PARAM_ZOMBIE_ON_FLOOR) == Boolean.TRUE;
			gameCharacter.setOnFloor(boolean1);
			((IsoZombie)gameCharacter).setKnockedDown(boolean1);
			gameCharacter.setFallOnFront(boolean1);
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		if (gameCharacter instanceof IsoPlayer && "fall".equals(gameCharacter.getVariableString("ClimbFenceOutcome"))) {
			gameCharacter.setSprinting(false);
		}

		gameCharacter.clearVariable("ClimbingFence");
		gameCharacter.clearVariable("ClimbFenceFinished");
		gameCharacter.clearVariable("ClimbFenceOutcome");
		gameCharacter.clearVariable("ClimbFenceStarted");
		gameCharacter.clearVariable("ClimbFenceFlopped");
		gameCharacter.ClearVariable("VaultOverSprint");
		gameCharacter.ClearVariable("VaultOverRun");
		gameCharacter.setIgnoreMovement(false);
		IsoZombie zombie = (IsoZombie)Type.tryCastTo(gameCharacter, IsoZombie.class);
		if (zombie != null) {
			zombie.AllowRepathDelay = 0.0F;
			if (hashMap.get(PARAM_PREV_STATE) == PathFindState.instance()) {
				if (gameCharacter.getPathFindBehavior2().getTargetChar() == null) {
					gameCharacter.setVariable("bPathfind", true);
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

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		if (animEvent.m_EventName.equalsIgnoreCase("CheckAttack")) {
			IsoZombie zombie = (IsoZombie)Type.tryCastTo(gameCharacter, IsoZombie.class);
			if (zombie != null && zombie.target instanceof IsoGameCharacter) {
				((IsoGameCharacter)zombie.target).attackFromWindowsLunge(zombie);
			}
		}

		if (animEvent.m_EventName.equalsIgnoreCase("ActiveAnimFinishing")) {
		}

		if (animEvent.m_EventName.equalsIgnoreCase("VaultSprintFallLanded")) {
			gameCharacter.dropHandItems();
			gameCharacter.fallenOnKnees();
		}

		if (animEvent.m_EventName.equalsIgnoreCase("FallenOnKnees")) {
			gameCharacter.fallenOnKnees();
		}

		IsoObject object;
		if (animEvent.m_EventName.equalsIgnoreCase("OnFloor")) {
			hashMap.put(PARAM_ZOMBIE_ON_FLOOR, Boolean.parseBoolean(animEvent.m_ParameterValue));
			if (Boolean.parseBoolean(animEvent.m_ParameterValue)) {
				this.setLungeXVars((IsoZombie)gameCharacter);
				object = this.getFence(gameCharacter);
				if (this.countZombiesClimbingOver(object) >= 2) {
					object.Damage = (short)(object.Damage - Rand.Next(7, 12) / (this.isMetalFence(object) ? 2 : 1));
					if (object.Damage <= 0) {
						IsoDirections directions = (IsoDirections)Type.tryCastTo(hashMap.get(PARAM_DIR), IsoDirections.class);
						object.destroyFence(directions);
					}
				}

				gameCharacter.setVariable("ClimbFenceFlopped", true);
			}
		}

		long long1;
		ParameterCharacterMovementSpeed parameterCharacterMovementSpeed;
		int int1;
		if (animEvent.m_EventName.equalsIgnoreCase("PlayFenceSound")) {
			object = this.getFence(gameCharacter);
			if (object == null) {
				return;
			}

			int1 = this.getFenceType(object);
			long1 = gameCharacter.playSound(animEvent.m_ParameterValue);
			parameterCharacterMovementSpeed = ((IsoPlayer)gameCharacter).getParameterCharacterMovementSpeed();
			gameCharacter.getEmitter().setParameterValue(long1, parameterCharacterMovementSpeed.getParameterDescription(), parameterCharacterMovementSpeed.calculateCurrentValue());
			gameCharacter.getEmitter().setParameterValue(long1, FMODManager.instance.getParameterDescription("FenceTypeLow"), (float)int1);
		}

		if (animEvent.m_EventName.equalsIgnoreCase("PlayTripSound")) {
			object = this.getFence(gameCharacter);
			if (object == null) {
				return;
			}

			int1 = this.getTripType(object);
			long1 = gameCharacter.playSound(animEvent.m_ParameterValue);
			parameterCharacterMovementSpeed = ((IsoPlayer)gameCharacter).getParameterCharacterMovementSpeed();
			gameCharacter.getEmitter().setParameterValue(long1, parameterCharacterMovementSpeed.getParameterDescription(), parameterCharacterMovementSpeed.calculateCurrentValue());
			gameCharacter.getEmitter().setParameterValue(long1, FMODManager.instance.getParameterDescription("TripObstacleType"), (float)int1);
		}

		if (animEvent.m_EventName.equalsIgnoreCase("SetCollidable")) {
			hashMap.put(PARAM_COLLIDABLE, Boolean.parseBoolean(animEvent.m_ParameterValue));
		}

		if (animEvent.m_EventName.equalsIgnoreCase("VaultOverStarted")) {
			if (gameCharacter instanceof IsoPlayer && !((IsoPlayer)gameCharacter).isLocalPlayer()) {
				return;
			}

			if (gameCharacter.isVariable("ClimbFenceOutcome", "fall")) {
				gameCharacter.reportEvent("EventFallClimb");
				gameCharacter.setVariable("BumpDone", true);
				gameCharacter.setFallOnFront(true);
			}
		}
	}

	public void getDeltaModifiers(IsoGameCharacter gameCharacter, MoveDeltaModifiers moveDeltaModifiers) {
		boolean boolean1 = gameCharacter.getPath2() != null;
		boolean boolean2 = gameCharacter instanceof IsoPlayer;
		if (boolean1 && boolean2) {
			moveDeltaModifiers.turnDelta = Math.max(moveDeltaModifiers.turnDelta, 10.0F);
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

	private void slideX(IsoGameCharacter gameCharacter, float float1) {
		float float2 = 0.05F * GameTime.getInstance().getMultiplier() / 1.6F;
		float2 = float1 > gameCharacter.x ? Math.min(float2, float1 - gameCharacter.x) : Math.max(-float2, float1 - gameCharacter.x);
		gameCharacter.x += float2;
		gameCharacter.nx = gameCharacter.x;
	}

	private void slideY(IsoGameCharacter gameCharacter, float float1) {
		float float2 = 0.05F * GameTime.getInstance().getMultiplier() / 1.6F;
		float2 = float1 > gameCharacter.y ? Math.min(float2, float1 - gameCharacter.y) : Math.max(-float2, float1 - gameCharacter.y);
		gameCharacter.y += float2;
		gameCharacter.ny = gameCharacter.y;
	}

	private IsoObject getFence(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		int int1 = (Integer)hashMap.get(PARAM_START_X);
		int int2 = (Integer)hashMap.get(PARAM_START_Y);
		int int3 = (Integer)hashMap.get(PARAM_Z);
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		int int4 = (Integer)hashMap.get(PARAM_END_X);
		int int5 = (Integer)hashMap.get(PARAM_END_Y);
		IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(int4, int5, int3);
		return square != null && square2 != null ? square.getHoppableTo(square2) : null;
	}

	private int getFenceType(IsoObject object) {
		if (object.getSprite() == null) {
			return 0;
		} else {
			PropertyContainer propertyContainer = object.getSprite().getProperties();
			String string = propertyContainer.Val("FenceTypeLow");
			if (string != null) {
				if ("Sandbag".equals(string) && object.getName() != null && StringUtils.containsIgnoreCase(object.getName(), "Gravel")) {
					string = "Gravelbag";
				}

				byte byte1 = -1;
				switch (string.hashCode()) {
				case -1687213100: 
					if (string.equals("Barbwire")) {
						byte1 = 4;
					}

					break;
				
				case -764914460: 
					if (string.equals("Sandbag")) {
						byte1 = 2;
					}

					break;
				
				case 2702029: 
					if (string.equals("Wood")) {
						byte1 = 0;
					}

					break;
				
				case 74234599: 
					if (string.equals("Metal")) {
						byte1 = 1;
					}

					break;
				
				case 1000008577: 
					if (string.equals("Gravelbag")) {
						byte1 = 3;
					}

					break;
				
				case 1111746861: 
					if (string.equals("RoadBlock")) {
						byte1 = 5;
					}

				
				}

				byte byte2;
				switch (byte1) {
				case 0: 
					byte2 = 0;
					break;
				
				case 1: 
					byte2 = 1;
					break;
				
				case 2: 
					byte2 = 2;
					break;
				
				case 3: 
					byte2 = 3;
					break;
				
				case 4: 
					byte2 = 4;
					break;
				
				case 5: 
					byte2 = 5;
					break;
				
				default: 
					byte2 = 0;
				
				}

				return byte2;
			} else {
				return 0;
			}
		}
	}

	private int getTripType(IsoObject object) {
		if (object.getSprite() == null) {
			return 0;
		} else {
			PropertyContainer propertyContainer = object.getSprite().getProperties();
			String string = propertyContainer.Val("FenceTypeLow");
			if (string != null) {
				if ("Sandbag".equals(string) && object.getName() != null && StringUtils.containsIgnoreCase(object.getName(), "Gravel")) {
					string = "Gravelbag";
				}

				byte byte1 = -1;
				switch (string.hashCode()) {
				case -1687213100: 
					if (string.equals("Barbwire")) {
						byte1 = 4;
					}

					break;
				
				case -764914460: 
					if (string.equals("Sandbag")) {
						byte1 = 2;
					}

					break;
				
				case 2702029: 
					if (string.equals("Wood")) {
						byte1 = 0;
					}

					break;
				
				case 74234599: 
					if (string.equals("Metal")) {
						byte1 = 1;
					}

					break;
				
				case 1000008577: 
					if (string.equals("Gravelbag")) {
						byte1 = 3;
					}

				
				}

				byte byte2;
				switch (byte1) {
				case 0: 
					byte2 = 0;
					break;
				
				case 1: 
					byte2 = 1;
					break;
				
				case 2: 
					byte2 = 2;
					break;
				
				case 3: 
					byte2 = 3;
					break;
				
				case 4: 
					byte2 = 4;
					break;
				
				default: 
					byte2 = 0;
				
				}

				return byte2;
			} else {
				return 0;
			}
		}
	}

	private boolean shouldFallAfterVaultOver(IsoGameCharacter gameCharacter) {
		if (gameCharacter instanceof IsoPlayer && !((IsoPlayer)gameCharacter).isLocalPlayer()) {
			return ((IsoPlayer)gameCharacter).networkAI.climbFenceOutcomeFall;
		} else if (DebugOptions.instance.Character.Debug.AlwaysTripOverFence.getValue()) {
			return true;
		} else {
			float float1 = 0.0F;
			if (gameCharacter.getVariableBoolean("VaultOverSprint")) {
				float1 = 10.0F;
			}

			if (gameCharacter.getMoodles() != null) {
				float1 += (float)(gameCharacter.getMoodles().getMoodleLevel(MoodleType.Endurance) * 10);
				float1 += (float)(gameCharacter.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 13);
				float1 += (float)(gameCharacter.getMoodles().getMoodleLevel(MoodleType.Pain) * 5);
			}

			BodyPart bodyPart = gameCharacter.getBodyDamage().getBodyPart(BodyPartType.Torso_Lower);
			if (bodyPart.getAdditionalPain(true) > 20.0F) {
				float1 += (bodyPart.getAdditionalPain(true) - 20.0F) / 10.0F;
			}

			if (gameCharacter.Traits.Clumsy.isSet()) {
				float1 += 10.0F;
			}

			if (gameCharacter.Traits.Graceful.isSet()) {
				float1 -= 10.0F;
			}

			if (gameCharacter.Traits.VeryUnderweight.isSet()) {
				float1 += 20.0F;
			}

			if (gameCharacter.Traits.Underweight.isSet()) {
				float1 += 10.0F;
			}

			if (gameCharacter.Traits.Obese.isSet()) {
				float1 += 20.0F;
			}

			if (gameCharacter.Traits.Overweight.isSet()) {
				float1 += 10.0F;
			}

			float1 -= (float)gameCharacter.getPerkLevel(PerkFactory.Perks.Fitness);
			return (float)Rand.Next(100) < float1;
		}
	}

	private int countZombiesClimbingOver(IsoObject object) {
		if (object != null && object.getSquare() != null) {
			byte byte1 = 0;
			IsoGridSquare square = object.getSquare();
			int int1 = byte1 + this.countZombiesClimbingOver(object, square);
			if (object.getProperties().Is(IsoFlagType.HoppableN)) {
				square = square.getAdjacentSquare(IsoDirections.N);
			} else {
				square = square.getAdjacentSquare(IsoDirections.W);
			}

			int1 += this.countZombiesClimbingOver(object, square);
			return int1;
		} else {
			return 0;
		}
	}

	private int countZombiesClimbingOver(IsoObject object, IsoGridSquare square) {
		if (square == null) {
			return 0;
		} else {
			int int1 = 0;
			for (int int2 = 0; int2 < square.getMovingObjects().size(); ++int2) {
				IsoZombie zombie = (IsoZombie)Type.tryCastTo((IsoMovingObject)square.getMovingObjects().get(int2), IsoZombie.class);
				if (zombie != null && zombie.target != null && zombie.isCurrentState(this) && this.getFence(zombie) == object) {
					++int1;
				}
			}

			return int1;
		}
	}

	private boolean isMetalFence(IsoObject object) {
		if (object != null && object.getProperties() != null) {
			PropertyContainer propertyContainer = object.getProperties();
			String string = propertyContainer.Val("Material");
			String string2 = propertyContainer.Val("Material2");
			String string3 = propertyContainer.Val("Material3");
			if (!"MetalBars".equals(string) && !"MetalBars".equals(string2) && !"MetalBars".equals(string3)) {
				if (!"MetalWire".equals(string) && !"MetalWire".equals(string2) && !"MetalWire".equals(string3)) {
					if (object instanceof IsoThumpable && object.hasModData()) {
						KahluaTableIterator kahluaTableIterator = object.getModData().iterator();
						while (kahluaTableIterator.advance()) {
							String string4 = (String)Type.tryCastTo(kahluaTableIterator.getKey(), String.class);
							if (string4 != null && string4.contains("MetalPipe")) {
								return true;
							}
						}
					}

					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public void setParams(IsoGameCharacter gameCharacter, IsoDirections directions) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		int int1 = gameCharacter.getSquare().getX();
		int int2 = gameCharacter.getSquare().getY();
		int int3 = gameCharacter.getSquare().getZ();
		int int4 = int1;
		int int5 = int2;
		switch (directions) {
		case N: 
			int5 = int2 - 1;
			break;
		
		case S: 
			int5 = int2 + 1;
			break;
		
		case W: 
			int4 = int1 - 1;
			break;
		
		case E: 
			int4 = int1 + 1;
			break;
		
		default: 
			throw new IllegalArgumentException("invalid direction");
		
		}
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int4, int5, int3);
		boolean boolean1 = false;
		boolean boolean2 = square != null && square.Is(IsoFlagType.solidtrans);
		boolean boolean3 = square != null && square.TreatAsSolidFloor();
		boolean boolean4 = square != null && gameCharacter.canClimbDownSheetRope(square);
		hashMap.put(PARAM_START_X, int1);
		hashMap.put(PARAM_START_Y, int2);
		hashMap.put(PARAM_Z, int3);
		hashMap.put(PARAM_END_X, int4);
		hashMap.put(PARAM_END_Y, int5);
		hashMap.put(PARAM_DIR, directions);
		hashMap.put(PARAM_ZOMBIE_ON_FLOOR, Boolean.FALSE);
		hashMap.put(PARAM_PREV_STATE, gameCharacter.getCurrentState());
		hashMap.put(PARAM_SCRATCH, boolean1 ? Boolean.TRUE : Boolean.FALSE);
		hashMap.put(PARAM_COUNTER, boolean2 ? Boolean.TRUE : Boolean.FALSE);
		hashMap.put(PARAM_SOLID_FLOOR, boolean3 ? Boolean.TRUE : Boolean.FALSE);
		hashMap.put(PARAM_SHEET_ROPE, boolean4 ? Boolean.TRUE : Boolean.FALSE);
		hashMap.put(PARAM_RUN, gameCharacter.isRunning() ? Boolean.TRUE : Boolean.FALSE);
		hashMap.put(PARAM_SPRINT, gameCharacter.isSprinting() ? Boolean.TRUE : Boolean.FALSE);
		hashMap.put(PARAM_COLLIDABLE, Boolean.FALSE);
	}
}
