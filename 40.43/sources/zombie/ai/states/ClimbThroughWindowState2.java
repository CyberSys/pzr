package zombie.ai.states;

import zombie.ai.State;
import zombie.ai.astar.Path;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.iso.IsoCamera;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSpriteInstance;


public class ClimbThroughWindowState2 extends State {
	static ClimbThroughWindowState2 _instance = new ClimbThroughWindowState2();

	public static ClimbThroughWindowState2 instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setPath((Path)null);
		float float1 = 0.0F;
		float float2 = 0.0F;
		if (gameCharacter.dir == IsoDirections.N) {
			gameCharacter.y = (float)((double)gameCharacter.y - 1.182);
			float2 = 0.5F;
		} else if (gameCharacter.dir == IsoDirections.S) {
			gameCharacter.y = (float)((double)gameCharacter.y + 0.818);
			float2 = -0.5F;
		}

		if (gameCharacter.dir == IsoDirections.W) {
			gameCharacter.x = (float)((double)gameCharacter.x - 1.182);
			float1 = 0.5F;
		} else if (gameCharacter.dir == IsoDirections.E) {
			gameCharacter.x = (float)((double)gameCharacter.x + 0.818);
			float1 = -0.5F;
		}

		gameCharacter.nx = gameCharacter.x;
		gameCharacter.ny = gameCharacter.y;
		IsoWorld.instance.CurrentCell.getOrCreateGridSquare((double)gameCharacter.nx, (double)gameCharacter.ny, (double)gameCharacter.getZ());
		gameCharacter.PlayAnimUnlooped("Climb_WindowB");
		if (gameCharacter.hasActiveModel()) {
			gameCharacter.sprite.modelSlot.DisableBlendingFrom("Climb_WindowA");
		}

		if (gameCharacter == IsoCamera.CamCharacter && gameCharacter instanceof IsoPlayer) {
			IsoCamera.DeferedX[((IsoPlayer)gameCharacter).getPlayerNum()] = float1;
			IsoCamera.DeferedY[((IsoPlayer)gameCharacter).getPlayerNum()] = float2;
			IsoCamera.update();
		}

		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)gameCharacter.x, (int)gameCharacter.y, (int)gameCharacter.z);
		if (!(gameCharacter instanceof IsoZombie) && gameCharacter.canClimbDownSheetRope(square)) {
			gameCharacter.setbClimbing(true);
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		gameCharacter.PlayAnimUnlooped("Climb_WindowB");
		gameCharacter.getSpriteDef().AnimFrameIncrease = 0.23F;
		if (gameCharacter instanceof IsoZombie) {
			IsoSpriteInstance spriteInstance = gameCharacter.getSpriteDef();
			spriteInstance.AnimFrameIncrease *= 0.8F;
		}

		float float1 = 0.5F;
		float float2 = 0.5F;
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
		if (gameCharacter == IsoCamera.CamCharacter && gameCharacter instanceof IsoPlayer) {
			float5 = 0.0F;
			float float6 = 0.0F;
			if (gameCharacter.dir == IsoDirections.N) {
				float6 = 0.5F;
			} else if (gameCharacter.dir == IsoDirections.S) {
				float6 = -0.5F;
			} else if (gameCharacter.dir == IsoDirections.W) {
				float5 = 0.5F;
			} else if (gameCharacter.dir == IsoDirections.E) {
				float5 = -0.5F;
			}

			float float7 = gameCharacter.getSpriteDef().Frame / (float)(gameCharacter.getSprite().CurrentAnim.Frames.size() - 1);
			if (float7 > 1.0F) {
				float7 = 1.0F;
			}

			float7 = 1.0F - float7;
			IsoCamera.DeferedX[((IsoPlayer)gameCharacter).getPlayerNum()] = float5 * float7;
			IsoCamera.DeferedY[((IsoPlayer)gameCharacter).getPlayerNum()] = float6 * float7;
		}

		if (!(gameCharacter instanceof IsoZombie) && (int)gameCharacter.getSpriteDef().Frame >= 6 && gameCharacter.canClimbDownSheetRope(gameCharacter.getCurrentSquare())) {
			gameCharacter.climbDownSheetRope();
			if (gameCharacter.getStateMachine().getCurrent() == instance()) {
				gameCharacter.getStateMachine().changeState(gameCharacter.getDefaultState());
			}
		} else {
			if (gameCharacter.getSpriteDef().Finished) {
				if (gameCharacter == IsoCamera.CamCharacter && gameCharacter instanceof IsoPlayer) {
					IsoCamera.DeferedX[((IsoPlayer)gameCharacter).getPlayerNum()] = IsoCamera.DeferedY[((IsoPlayer)gameCharacter).getPlayerNum()] = 0.0F;
				}

				if (gameCharacter.StateMachineParams.get(1) != PathFindState.instance() && gameCharacter.StateMachineParams.get(1) != WalkTowardState.instance()) {
					if (!(gameCharacter instanceof IsoZombie) || !((IsoZombie)gameCharacter).WanderFromWindow()) {
						gameCharacter.getStateMachine().changeState(gameCharacter.getDefaultState());
					}
				} else {
					gameCharacter.changeState((State)gameCharacter.StateMachineParams.get(1));
				}

				if (gameCharacter.getStateMachine().getCurrent() == instance()) {
					gameCharacter.getStateMachine().changeState(gameCharacter.getDefaultState());
				}

				gameCharacter.setbClimbing(false);
				gameCharacter.setCollidable(true);
				gameCharacter.StateMachineParams.clear();
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		if (gameCharacter == IsoCamera.CamCharacter && gameCharacter instanceof IsoPlayer) {
			IsoCamera.DeferedX[((IsoPlayer)gameCharacter).getPlayerNum()] = IsoCamera.DeferedY[((IsoPlayer)gameCharacter).getPlayerNum()] = 0.0F;
		}

		gameCharacter.setCollidable(true);
		gameCharacter.setbClimbing(false);
	}
}
