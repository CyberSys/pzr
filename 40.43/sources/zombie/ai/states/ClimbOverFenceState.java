package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameServer;
import zombie.network.ServerMap;


public class ClimbOverFenceState extends State {
	static ClimbOverFenceState _instance = new ClimbOverFenceState();

	public static ClimbOverFenceState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoDirections directions = (IsoDirections)gameCharacter.StateMachineParams.get(0);
		float float1 = 0.5F;
		float float2 = 0.5F;
		gameCharacter.PlayAnimUnlooped("Climb_WindowA");
		gameCharacter.getSpriteDef().AnimFrameIncrease = 0.33F;
		gameCharacter.setAnimated(true);
		if (gameCharacter instanceof IsoZombie) {
			IsoSpriteInstance spriteInstance = gameCharacter.getSpriteDef();
			spriteInstance.AnimFrameIncrease *= 0.8F;
		}

		gameCharacter.setCollidable(false);
		float float3 = 0.0F;
		float float4 = 0.0F;
		if (directions == IsoDirections.N) {
			gameCharacter.setDir(IsoDirections.N);
			float1 = 0.5F;
			float2 = 0.682F;
			float4 = -0.682F;
		} else if (directions == IsoDirections.S) {
			gameCharacter.setDir(IsoDirections.S);
			float1 = 0.5F;
			float2 = 0.682F;
			float4 = 0.31800002F;
		} else if (directions == IsoDirections.W) {
			gameCharacter.setDir(IsoDirections.W);
			float2 = 0.5F;
			float1 = 0.682F;
			float3 = -0.682F;
		} else if (directions == IsoDirections.E) {
			gameCharacter.setDir(IsoDirections.E);
			float2 = 0.5F;
			float1 = 0.682F;
			float3 = 0.31800002F;
		}

		float float5 = gameCharacter.x - (float)((int)gameCharacter.x);
		float float6 = gameCharacter.y - (float)((int)gameCharacter.y);
		float float7;
		if (float5 != float1) {
			float7 = (float1 - float5) / 4.0F;
			float5 += float7;
			gameCharacter.x = (float)((int)gameCharacter.x) + float5;
		}

		if (float6 != float2) {
			float7 = (float2 - float6) / 4.0F;
			float6 += float7;
			gameCharacter.y = (float)((int)gameCharacter.y) + float6;
		}

		gameCharacter.nx = gameCharacter.x;
		gameCharacter.ny = gameCharacter.y;
		gameCharacter.reqMovement.set(0.0F, 0.0F);
		gameCharacter.setIgnoreMovementForDirection(true);
		if (gameCharacter == IsoCamera.CamCharacter && gameCharacter instanceof IsoPlayer) {
			float7 = gameCharacter.getSpriteDef().Frame / (float)(gameCharacter.getSprite().CurrentAnim.Frames.size() - 1);
			if (float7 > 1.0F) {
				float7 = 1.0F;
			}

			IsoCamera.DeferedX[((IsoPlayer)gameCharacter).getPlayerNum()] = float3 * float7;
			IsoCamera.DeferedY[((IsoPlayer)gameCharacter).getPlayerNum()] = float4 * float7;
		}

		if (gameCharacter.getSpriteDef().Finished) {
			int int1 = (int)gameCharacter.x;
			int int2 = (int)gameCharacter.y;
			switch (directions) {
			case N: 
				--int2;
				break;
			
			case S: 
				++int2;
				break;
			
			case W: 
				--int1;
				break;
			
			case E: 
				++int1;
			
			}

			IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int1 / 10, int2 / 10) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int1, int2, (int)gameCharacter.z);
			if (chunk == null) {
				gameCharacter.setDefaultState();
				return;
			}

			if (gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
				((IsoPlayer)gameCharacter).dirtyRecalcGridStackTime = 20.0F;
			}

			gameCharacter.StateMachineParams.put(1, gameCharacter.getStateMachine().getPrevious());
			gameCharacter.getStateMachine().changeState(ClimbOverFenceState2.instance());
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovementForDirection(false);
	}
}
