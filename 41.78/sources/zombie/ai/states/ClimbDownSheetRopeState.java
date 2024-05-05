package zombie.ai.states;

import java.util.HashMap;
import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoWindow;


public final class ClimbDownSheetRopeState extends State {
	public static final float CLIMB_DOWN_SPEED = 0.16F;
	private static final float CLIMB_DOWN_SLOWDOWN = 0.5F;
	private static final ClimbDownSheetRopeState _instance = new ClimbDownSheetRopeState();

	public static ClimbDownSheetRopeState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(true);
		gameCharacter.setHideWeaponModel(true);
		gameCharacter.setbClimbing(true);
		gameCharacter.setVariable("ClimbRope", true);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		float float1 = 0.0F;
		float float2 = 0.0F;
		if (gameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopN) || gameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetN)) {
			gameCharacter.setDir(IsoDirections.N);
			float1 = 0.54F;
			float2 = 0.39F;
		}

		if (gameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopS) || gameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetS)) {
			gameCharacter.setDir(IsoDirections.S);
			float1 = 0.118F;
			float2 = 0.5756F;
		}

		if (gameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopW) || gameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetW)) {
			gameCharacter.setDir(IsoDirections.W);
			float1 = 0.4F;
			float2 = 0.7F;
		}

		if (gameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopE) || gameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetE)) {
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
		float5 = this.getClimbDownSheetRopeSpeed(gameCharacter);
		gameCharacter.getSpriteDef().AnimFrameIncrease = float5;
		float float6 = gameCharacter.z - float5 / 10.0F * GameTime.instance.getMultiplier();
		float6 = Math.max(float6, 0.0F);
		for (int int1 = (int)gameCharacter.z; int1 >= (int)float6; --int1) {
			IsoCell cell = IsoWorld.instance.getCell();
			IsoGridSquare square = cell.getGridSquare((double)gameCharacter.getX(), (double)gameCharacter.getY(), (double)int1);
			if ((square.Is(IsoFlagType.solidtrans) || square.TreatAsSolidFloor() || int1 == 0) && float6 <= (float)int1) {
				gameCharacter.z = (float)int1;
				hashMap.clear();
				gameCharacter.clearVariable("ClimbRope");
				gameCharacter.setCollidable(true);
				gameCharacter.setbClimbing(false);
				return;
			}
		}

		gameCharacter.z = float6;
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
		gameCharacter.setHideWeaponModel(false);
		gameCharacter.clearVariable("ClimbRope");
		gameCharacter.setbClimbing(false);
	}

	public float getClimbDownSheetRopeSpeed(IsoGameCharacter gameCharacter) {
		float float1 = 0.16F;
		switch (gameCharacter.getPerkLevel(PerkFactory.Perks.Strength)) {
		case 0: 
			float1 -= 0.12F;
			break;
		
		case 1: 
		
		case 2: 
		
		case 3: 
			float1 -= 0.09F;
		
		case 4: 
		
		case 5: 
		
		default: 
			break;
		
		case 6: 
		
		case 7: 
			float1 += 0.05F;
			break;
		
		case 8: 
		
		case 9: 
			float1 += 0.09F;
			break;
		
		case 10: 
			float1 += 0.12F;
		
		}
		float1 *= 0.5F;
		return float1;
	}
}
