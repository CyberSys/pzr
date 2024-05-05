package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;


public final class ClimbSheetRopeState extends State {
	public static final float CLIMB_SPEED = 0.16F;
	private static final float CLIMB_SLOWDOWN = 0.5F;
	private static final ClimbSheetRopeState _instance = new ClimbSheetRopeState();

	public static ClimbSheetRopeState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(true);
		gameCharacter.setbClimbing(true);
		gameCharacter.setVariable("ClimbRope", true);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		gameCharacter.getStateMachineParams(this);
		float float1 = 0.0F;
		float float2 = 0.0F;
		if (gameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetN) || gameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopN)) {
			gameCharacter.setDir(IsoDirections.N);
			float1 = 0.54F;
			float2 = 0.39F;
		}

		if (gameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetS) || gameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopS)) {
			gameCharacter.setDir(IsoDirections.S);
			float1 = 0.118F;
			float2 = 0.5756F;
		}

		if (gameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetW) || gameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopW)) {
			gameCharacter.setDir(IsoDirections.W);
			float1 = 0.4F;
			float2 = 0.7F;
		}

		if (gameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetE) || gameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopE)) {
			gameCharacter.setDir(IsoDirections.E);
			float1 = 0.5417F;
			float2 = 0.3144F;
		}

		float float3 = gameCharacter.x - (float)((int)gameCharacter.x);
		float float4 = gameCharacter.y - (float)((int)gameCharacter.y);
		float float5;
		if (float3 != float1) {
			float5 = (float1 - float3) / 4.0F;
			float3 += float5;
			gameCharacter.x = (float)((int)gameCharacter.x) + float3;
		}

		if (float4 != float2) {
			float5 = (float2 - float4) / 4.0F;
			float4 += float5;
			gameCharacter.y = (float)((int)gameCharacter.y) + float4;
		}

		gameCharacter.nx = gameCharacter.x;
		gameCharacter.ny = gameCharacter.y;
		float5 = this.getClimbSheetRopeSpeed(gameCharacter);
		gameCharacter.getSpriteDef().AnimFrameIncrease = float5;
		float float6 = gameCharacter.z + float5 / 10.0F * GameTime.instance.getMultiplier();
		float6 = Math.min(float6, 7.0F);
		for (int int1 = (int)gameCharacter.z; (float)int1 <= float6; ++int1) {
			IsoCell cell = IsoWorld.instance.getCell();
			IsoGridSquare square = cell.getGridSquare((double)gameCharacter.getX(), (double)gameCharacter.getY(), (double)int1);
			if (IsoWindow.isTopOfSheetRopeHere(square)) {
				gameCharacter.z = (float)int1;
				gameCharacter.setCurrent(square);
				gameCharacter.setCollidable(true);
				IsoGridSquare square2 = square.nav[gameCharacter.dir.index()];
				if (square2 != null) {
					if (!square2.TreatAsSolidFloor()) {
						gameCharacter.climbDownSheetRope();
						return;
					}

					IsoWindow window = square.getWindowTo(square2);
					if (window != null) {
						if (!window.open) {
							window.ToggleWindow(gameCharacter);
						}

						if (!window.canClimbThrough(gameCharacter)) {
							gameCharacter.climbDownSheetRope();
							return;
						}

						gameCharacter.climbThroughWindow(window, 4);
						return;
					}

					IsoThumpable thumpable = square.getWindowThumpableTo(square2);
					if (thumpable != null) {
						if (!thumpable.canClimbThrough(gameCharacter)) {
							gameCharacter.climbDownSheetRope();
							return;
						}

						gameCharacter.climbThroughWindow(thumpable, 4);
						return;
					}

					thumpable = square.getHoppableThumpableTo(square2);
					if (thumpable != null) {
						if (!IsoWindow.canClimbThroughHelper(gameCharacter, square, square2, gameCharacter.dir == IsoDirections.N || gameCharacter.dir == IsoDirections.S)) {
							gameCharacter.climbDownSheetRope();
							return;
						}

						gameCharacter.climbOverFence(gameCharacter.dir);
						return;
					}

					IsoObject object = square.getWindowFrameTo(square2);
					if (object != null) {
						if (!IsoWindowFrame.canClimbThrough(object, gameCharacter)) {
							gameCharacter.climbDownSheetRope();
							return;
						}

						gameCharacter.climbThroughWindowFrame(object);
						return;
					}

					IsoObject object2 = square.getWallHoppableTo(square2);
					if (object2 != null) {
						if (!IsoWindow.canClimbThroughHelper(gameCharacter, square, square2, gameCharacter.dir == IsoDirections.N || gameCharacter.dir == IsoDirections.S)) {
							gameCharacter.climbDownSheetRope();
							return;
						}

						gameCharacter.climbOverFence(gameCharacter.dir);
						return;
					}
				}

				return;
			}
		}

		gameCharacter.z = float6;
		if (gameCharacter.z >= 7.0F) {
			gameCharacter.setCollidable(true);
			gameCharacter.clearVariable("ClimbRope");
		}

		if (!IsoWindow.isSheetRopeHere(gameCharacter.getCurrentSquare())) {
			gameCharacter.setCollidable(true);
			gameCharacter.setbClimbing(false);
			gameCharacter.setbFalling(true);
			gameCharacter.clearVariable("ClimbRope");
		}

		if (gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
			((IsoPlayer)gameCharacter).dirtyRecalcGridStackTime = 2.0F;
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(false);
		gameCharacter.setbClimbing(false);
		gameCharacter.clearVariable("ClimbRope");
	}

	public float getClimbSheetRopeSpeed(IsoGameCharacter gameCharacter) {
		float float1 = 0.16F;
		switch (gameCharacter.getPerkLevel(PerkFactory.Perks.Strength)) {
		case 1: 
			float1 -= 0.1F;
			break;
		
		case 2: 
			float1 -= 0.1F;
		
		case 3: 
		
		case 4: 
		
		case 5: 
		
		default: 
			break;
		
		case 6: 
			float1 += 0.05F;
			break;
		
		case 7: 
			float1 += 0.05F;
			break;
		
		case 8: 
			float1 += 0.1F;
			break;
		
		case 9: 
			float1 += 0.1F;
		
		}
		float1 *= 0.5F;
		return float1;
	}
}
