package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.areas.SafeHouse;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;


public class ClimbThroughWindowState extends State {
	static ClimbThroughWindowState _instance = new ClimbThroughWindowState();
	static boolean first = true;

	public static ClimbThroughWindowState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		first = true;
		if (gameCharacter.StateMachineParams.get(0) instanceof IsoWindow) {
			IsoWindow window = (IsoWindow)gameCharacter.StateMachineParams.get(0);
			if (window.isDestroyed() && !window.isGlassRemoved() && Rand.Next(2) == 0) {
				gameCharacter.getBodyDamage().setScratchedWindow();
			}
		} else if (gameCharacter.StateMachineParams.get(0) instanceof IsoThumpable) {
			IsoThumpable thumpable = (IsoThumpable)gameCharacter.StateMachineParams.get(0);
			if (thumpable.isDestroyed() && Rand.Next(15) == 0) {
				gameCharacter.getBodyDamage().setScratchedWindow();
			}
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		boolean boolean1 = true;
		int int1 = 0;
		int int2 = 0;
		if (gameCharacter.StateMachineParams.get(0) instanceof IsoWindow) {
			IsoWindow window = (IsoWindow)gameCharacter.StateMachineParams.get(0);
			if (GameClient.bClient && gameCharacter instanceof IsoPlayer && SafeHouse.isSafeHouse(window.getOppositeSquare(), ((IsoPlayer)gameCharacter).getUsername(), true) != null && !ServerOptions.instance.SafehouseAllowTrepass.getValue()) {
				gameCharacter.getStateMachine().changeState(gameCharacter.getDefaultState());
			}

			boolean1 = window.north;
			int1 = window.getSquare().getX();
			int2 = window.getSquare().getY();
		} else if (gameCharacter.StateMachineParams.get(0) instanceof IsoThumpable) {
			IsoThumpable thumpable = (IsoThumpable)gameCharacter.StateMachineParams.get(0);
			Object object = null;
			boolean1 = thumpable.north;
			if (GameClient.bClient && gameCharacter instanceof IsoPlayer && SafeHouse.isSafeHouse(thumpable.getInsideSquare(), ((IsoPlayer)gameCharacter).getUsername(), true) != null && !ServerOptions.instance.SafehouseAllowTrepass.getValue()) {
				gameCharacter.getStateMachine().changeState(gameCharacter.getDefaultState());
			}

			int1 = thumpable.getSquare().getX();
			int2 = thumpable.getSquare().getY();
		} else if (gameCharacter.StateMachineParams.get(0) instanceof IsoObject) {
			IsoObject object2 = (IsoObject)gameCharacter.StateMachineParams.get(0);
			boolean1 = IsoWindowFrame.isWindowFrame(object2, true);
			int1 = object2.getSquare().getX();
			int2 = object2.getSquare().getY();
		}

		float float1 = 0.5F;
		float float2 = 0.5F;
		gameCharacter.PlayAnimUnlooped("Climb_WindowA");
		gameCharacter.getSpriteDef().AnimFrameIncrease = 0.33F;
		gameCharacter.getLegsSprite().Animate = true;
		if (gameCharacter.StateMachineParams.get(1) != null && first && gameCharacter.StateMachineParams.get(1) instanceof Integer) {
			gameCharacter.getSpriteDef().Frame = (float)(Integer)gameCharacter.StateMachineParams.get(1);
			first = false;
		}

		if (gameCharacter instanceof IsoZombie) {
			IsoSpriteInstance spriteInstance = gameCharacter.getSpriteDef();
			spriteInstance.AnimFrameIncrease *= 0.8F;
		}

		gameCharacter.setCollidable(false);
		float float3 = 0.0F;
		float float4 = 0.0F;
		if (boolean1) {
			if ((float)int2 < gameCharacter.getY()) {
				gameCharacter.setDir(IsoDirections.N);
				float1 = (float)int1 + 0.5F;
				float2 = (float)int2 + 0.682F;
				float4 = -0.682F;
			} else {
				gameCharacter.setDir(IsoDirections.S);
				float1 = (float)int1 + 0.5F;
				float2 = (float)(int2 - 1) + 0.682F;
				float4 = 0.31800002F;
			}
		} else if ((float)int1 < gameCharacter.getX()) {
			gameCharacter.setDir(IsoDirections.W);
			float2 = (float)int2 + 0.5F;
			float1 = (float)int1 + 0.682F;
			float3 = -0.682F;
		} else {
			gameCharacter.setDir(IsoDirections.E);
			float2 = (float)int2 + 0.5F;
			float1 = (float)(int1 - 1) + 0.682F;
			float3 = 0.31800002F;
		}

		float float5;
		if (gameCharacter.x != float1) {
			float5 = (float1 - gameCharacter.x) / 4.0F;
			gameCharacter.x += float5;
		}

		if (gameCharacter.y != float2) {
			float5 = (float2 - gameCharacter.y) / 4.0F;
			gameCharacter.y += float5;
		}

		gameCharacter.nx = gameCharacter.x;
		gameCharacter.ny = gameCharacter.y;
		gameCharacter.reqMovement.set(0.0F, 0.0F);
		gameCharacter.setIgnoreMovementForDirection(true);
		if (gameCharacter == IsoCamera.CamCharacter && gameCharacter instanceof IsoPlayer) {
			float5 = gameCharacter.getSpriteDef().Frame / (float)(gameCharacter.getSprite().CurrentAnim.Frames.size() - 1);
			if (float5 > 1.0F) {
				float5 = 1.0F;
			}

			IsoCamera.DeferedX[((IsoPlayer)gameCharacter).getPlayerNum()] = float3 * float5;
			IsoCamera.DeferedY[((IsoPlayer)gameCharacter).getPlayerNum()] = float4 * float5;
		}

		if (gameCharacter.getSpriteDef().Finished) {
			if (gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
				((IsoPlayer)gameCharacter).dirtyRecalcGridStackTime = 20.0F;
			}

			gameCharacter.x = float1;
			gameCharacter.y = float2;
			int int3 = (int)gameCharacter.x;
			int int4 = (int)gameCharacter.y;
			switch (gameCharacter.dir) {
			case N: 
				--int4;
				break;
			
			case S: 
				++int4;
				break;
			
			case W: 
				--int3;
				break;
			
			case E: 
				++int3;
			
			}

			IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int3 / 10, int4 / 10) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int3, int4, (int)gameCharacter.z);
			if (chunk == null) {
				gameCharacter.setDefaultState();
				return;
			}

			gameCharacter.StateMachineParams.put(1, gameCharacter.getStateMachine().getPrevious());
			gameCharacter.getStateMachine().changeState(ClimbThroughWindowState2.instance());
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		if (gameCharacter.StateMachineParams.get(0) instanceof IsoThumpable && gameCharacter instanceof IsoPlayer && ((IsoThumpable)gameCharacter.StateMachineParams.get(0)).getName().equals("Barbed Fence") && Rand.Next(101) > 75) {
			gameCharacter.getBodyDamage().setScratchedWindow();
		}

		gameCharacter.setIgnoreMovementForDirection(false);
	}
}
