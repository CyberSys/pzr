package zombie.ai.states;

import fmod.fmod.FMODManager;
import java.util.HashMap;
import zombie.GameTime;
import zombie.ZomboidGlobals;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.properties.PropertyContainer;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;


public final class ClimbOverWallState extends State {
	private static final ClimbOverWallState _instance = new ClimbOverWallState();
	static final Integer PARAM_START_X = 0;
	static final Integer PARAM_START_Y = 1;
	static final Integer PARAM_Z = 2;
	static final Integer PARAM_END_X = 3;
	static final Integer PARAM_END_Y = 4;
	static final Integer PARAM_DIR = 5;
	static final int FENCE_TYPE_WOOD = 0;
	static final int FENCE_TYPE_METAL = 1;
	static final int FENCE_TYPE_METAL_BARS = 2;

	public static ClimbOverWallState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(true);
		gameCharacter.setHideWeaponModel(true);
		gameCharacter.getStateMachineParams(this);
		Stats stats = gameCharacter.getStats();
		stats.endurance = (float)((double)stats.endurance - ZomboidGlobals.RunningEnduranceReduce * 1200.0);
		IsoPlayer player = (IsoPlayer)gameCharacter;
		boolean boolean1 = player.isClimbOverWallStruggle();
		if (boolean1) {
			stats = gameCharacter.getStats();
			stats.endurance = (float)((double)stats.endurance - ZomboidGlobals.RunningEnduranceReduce * 500.0);
		}

		boolean boolean2 = player.isClimbOverWallSuccess();
		gameCharacter.setVariable("ClimbFenceFinished", false);
		gameCharacter.setVariable("ClimbFenceOutcome", boolean2 ? "success" : "fail");
		gameCharacter.setVariable("ClimbFenceStarted", false);
		gameCharacter.setVariable("ClimbFenceStruggle", boolean1);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		IsoDirections directions = (IsoDirections)hashMap.get(PARAM_DIR);
		gameCharacter.setAnimated(true);
		gameCharacter.setDir(directions);
		boolean boolean1 = gameCharacter.getVariableBoolean("ClimbFenceStarted");
		if (!boolean1) {
			int int1 = (Integer)hashMap.get(PARAM_START_X);
			int int2 = (Integer)hashMap.get(PARAM_START_Y);
			float float1 = 0.15F;
			float float2 = gameCharacter.getX();
			float float3 = gameCharacter.getY();
			switch (directions) {
			case N: 
				float3 = (float)int2 + float1;
				break;
			
			case S: 
				float3 = (float)(int2 + 1) - float1;
				break;
			
			case W: 
				float2 = (float)int1 + float1;
				break;
			
			case E: 
				float2 = (float)(int1 + 1) - float1;
			
			}

			float float4 = GameTime.getInstance().getMultiplier() / 1.6F / 8.0F;
			gameCharacter.setX(gameCharacter.x + (float2 - gameCharacter.x) * float4);
			gameCharacter.setY(gameCharacter.y + (float3 - gameCharacter.y) * float4);
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.clearVariable("ClimbFenceFinished");
		gameCharacter.clearVariable("ClimbFenceOutcome");
		gameCharacter.clearVariable("ClimbFenceStarted");
		gameCharacter.clearVariable("ClimbFenceStruggle");
		gameCharacter.setIgnoreMovement(false);
		gameCharacter.setHideWeaponModel(false);
		if (gameCharacter instanceof IsoZombie) {
			((IsoZombie)gameCharacter).networkAI.isClimbing = false;
		}
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if (animEvent.m_EventName.equalsIgnoreCase("PlayFenceSound")) {
			IsoObject object = this.getFence(gameCharacter);
			if (object == null) {
				return;
			}

			int int1 = this.getFenceType(object);
			long long1 = gameCharacter.playSound(animEvent.m_ParameterValue);
			gameCharacter.getEmitter().setParameterValue(long1, FMODManager.instance.getParameterDescription("FenceTypeHigh"), (float)int1);
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

	private IsoObject getClimbableWallN(IsoGridSquare square) {
		IsoObject[] objectArray = (IsoObject[])square.getObjects().getElements();
		int int1 = 0;
		for (int int2 = square.getObjects().size(); int1 < int2; ++int1) {
			IsoObject object = objectArray[int1];
			PropertyContainer propertyContainer = object.getProperties();
			if (propertyContainer != null && !propertyContainer.Is(IsoFlagType.CantClimb) && object.getType() == IsoObjectType.wall && propertyContainer.Is(IsoFlagType.collideN) && !propertyContainer.Is(IsoFlagType.HoppableN)) {
				return object;
			}
		}

		return null;
	}

	private IsoObject getClimbableWallW(IsoGridSquare square) {
		IsoObject[] objectArray = (IsoObject[])square.getObjects().getElements();
		int int1 = 0;
		for (int int2 = square.getObjects().size(); int1 < int2; ++int1) {
			IsoObject object = objectArray[int1];
			PropertyContainer propertyContainer = object.getProperties();
			if (propertyContainer != null && !propertyContainer.Is(IsoFlagType.CantClimb) && object.getType() == IsoObjectType.wall && propertyContainer.Is(IsoFlagType.collideW) && !propertyContainer.Is(IsoFlagType.HoppableW)) {
				return object;
			}
		}

		return null;
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
		if (square != null && square2 != null) {
			IsoDirections directions = (IsoDirections)hashMap.get(PARAM_DIR);
			IsoObject object;
			switch (directions) {
			case N: 
				object = this.getClimbableWallN(square);
				break;
			
			case S: 
				object = this.getClimbableWallN(square2);
				break;
			
			case W: 
				object = this.getClimbableWallW(square);
				break;
			
			case E: 
				object = this.getClimbableWallW(square2);
				break;
			
			default: 
				object = null;
			
			}

			return object;
		} else {
			return null;
		}
	}

	private int getFenceType(IsoObject object) {
		if (object.getSprite() == null) {
			return 0;
		} else {
			PropertyContainer propertyContainer = object.getSprite().getProperties();
			String string = propertyContainer.Val("FenceTypeHigh");
			if (string != null) {
				byte byte1 = -1;
				switch (string.hashCode()) {
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
				
				case 945336402: 
					if (string.equals("MetalGate")) {
						byte1 = 2;
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
				
				default: 
					byte2 = 0;
				
				}

				return byte2;
			} else {
				return 0;
			}
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
		hashMap.put(PARAM_START_X, int1);
		hashMap.put(PARAM_START_Y, int2);
		hashMap.put(PARAM_Z, int3);
		hashMap.put(PARAM_END_X, int4);
		hashMap.put(PARAM_END_Y, int5);
		hashMap.put(PARAM_DIR, directions);
		IsoPlayer player = (IsoPlayer)gameCharacter;
		if (player.isLocalPlayer()) {
			byte byte1 = 20;
			int int6 = byte1 + gameCharacter.getPerkLevel(PerkFactory.Perks.Fitness) * 2;
			int6 += gameCharacter.getPerkLevel(PerkFactory.Perks.Strength) * 2;
			int6 -= gameCharacter.getMoodles().getMoodleLevel(MoodleType.Endurance) * 5;
			int6 -= gameCharacter.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 8;
			if (gameCharacter.getTraits().contains("Emaciated") || gameCharacter.Traits.Obese.isSet() || gameCharacter.getTraits().contains("Very Underweight")) {
				int6 -= 25;
			}

			if (gameCharacter.getTraits().contains("Underweight") || gameCharacter.getTraits().contains("Overweight")) {
				int6 -= 15;
			}

			IsoGridSquare square = gameCharacter.getCurrentSquare();
			if (square != null) {
				for (int int7 = 0; int7 < square.getMovingObjects().size(); ++int7) {
					IsoMovingObject movingObject = (IsoMovingObject)square.getMovingObjects().get(int7);
					if (movingObject instanceof IsoZombie) {
						if (((IsoZombie)movingObject).target == gameCharacter && ((IsoZombie)movingObject).getCurrentState() == AttackState.instance()) {
							int6 -= 25;
						} else {
							int6 -= 7;
						}
					}
				}
			}

			int6 = Math.max(0, int6);
			boolean boolean1 = Rand.NextBool(int6 / 2);
			if ("Tutorial".equals(Core.GameMode)) {
				boolean1 = false;
			}

			boolean boolean2 = !Rand.NextBool(int6);
			player.setClimbOverWallStruggle(boolean1);
			player.setClimbOverWallSuccess(boolean2);
		}
	}
}
