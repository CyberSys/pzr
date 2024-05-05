package zombie.ai.states;

import java.util.HashMap;
import org.joml.Vector3f;
import zombie.GameTime;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.gameStates.IngameState;
import zombie.iso.Vector2;
import zombie.util.Type;


public class LungeNetworkState extends State {
	static LungeNetworkState _instance = new LungeNetworkState();
	private Vector2 temp = new Vector2();
	private final Vector3f worldPos = new Vector3f();
	private static final Integer PARAM_TICK_COUNT = 0;

	public static LungeNetworkState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		WalkTowardNetworkState.instance().enter(gameCharacter);
		IsoZombie zombie = (IsoZombie)gameCharacter;
		zombie.LungeTimer = 180.0F;
		hashMap.put(PARAM_TICK_COUNT, IngameState.instance.numberTicks);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		WalkTowardNetworkState.instance().execute(gameCharacter);
		IsoZombie zombie = (IsoZombie)gameCharacter;
		gameCharacter.setOnFloor(false);
		gameCharacter.setShootable(true);
		if (zombie.bLunger) {
			zombie.walkVariantUse = "ZombieWalk3";
		}

		zombie.LungeTimer -= GameTime.getInstance().getMultiplier() / 1.6F;
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(zombie.getTarget(), IsoPlayer.class);
		if (player != null && player.isGhostMode()) {
			zombie.LungeTimer = 0.0F;
		}

		if (zombie.LungeTimer < 0.0F) {
			zombie.LungeTimer = 0.0F;
		}

		if (zombie.LungeTimer <= 0.0F) {
			zombie.AllowRepathDelay = 0.0F;
		}

		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		long long1 = (Long)hashMap.get(PARAM_TICK_COUNT);
		if (IngameState.instance.numberTicks - long1 == 2L) {
			((IsoZombie)gameCharacter).parameterZombieState.setState(ParameterZombieState.State.LockTarget);
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		WalkTowardNetworkState.instance().exit(gameCharacter);
	}

	public boolean isMoving(IsoGameCharacter gameCharacter) {
		return true;
	}
}
