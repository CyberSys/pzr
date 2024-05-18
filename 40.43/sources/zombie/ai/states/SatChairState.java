package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoDirections;
import zombie.iso.IsoObject;


public class SatChairState extends State {
	static SatChairState _instance = new SatChairState();

	public static SatChairState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setSat(true);
		IsoObject object = (IsoObject)gameCharacter.StateMachineParams.get(0);
		int int1 = object.getSquare().getX();
		int int2 = object.getSquare().getY();
		float float1 = gameCharacter.getX() - (float)((int)gameCharacter.getX());
		gameCharacter.setX((float)int1);
		gameCharacter.setX(gameCharacter.getX() + float1);
		float float2 = gameCharacter.getY() - (float)((int)gameCharacter.getY());
		gameCharacter.setY((float)int2);
		gameCharacter.setY(gameCharacter.getY() + float2);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		boolean boolean1 = false;
		boolean boolean2 = false;
		boolean boolean3 = false;
		IsoObject object = (IsoObject)gameCharacter.StateMachineParams.get(0);
		int int1 = object.getSquare().getX();
		int int2 = object.getSquare().getY();
		float float1 = 0.2F;
		float float2 = 0.1F;
		gameCharacter.PlayAnimUnlooped("SatChairIn");
		gameCharacter.getSpriteDef().AnimFrameIncrease = 0.23F;
		if (boolean1) {
			if ((float)int2 < gameCharacter.getY()) {
				gameCharacter.setDir(IsoDirections.S);
			} else {
				gameCharacter.setDir(IsoDirections.N);
			}
		} else if ((float)int1 < gameCharacter.getX()) {
			gameCharacter.setDir(IsoDirections.W);
		} else {
			gameCharacter.setDir(IsoDirections.E);
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
		gameCharacter.setChair(object);
		if ((int)gameCharacter.getSpriteDef().Frame == gameCharacter.getSprite().CurrentAnim.Frames.size() - 1) {
			gameCharacter.getStateMachine().changeState(gameCharacter.getDefaultState());
			if (gameCharacter.getStateMachine().getCurrent() == instance()) {
				gameCharacter.getStateMachine().changeState(gameCharacter.getDefaultState());
			}

			gameCharacter.setCollidable(true);
			gameCharacter.StateMachineParams.clear();
		}
	}
}
