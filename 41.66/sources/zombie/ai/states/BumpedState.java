package zombie.ai.states;

import fmod.fmod.FMODManager;
import zombie.ai.State;
import zombie.audio.parameters.ParameterCharacterMovementSpeed;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.util.Type;


public final class BumpedState extends State {
	private static final BumpedState _instance = new BumpedState();

	public static BumpedState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setBumpDone(false);
		gameCharacter.setVariable("BumpFallAnimFinished", false);
		gameCharacter.getAnimationPlayer().setTargetToAngle();
		gameCharacter.getForwardDirection().setLengthAndDirection(gameCharacter.getAnimationPlayer().getAngle(), 1.0F);
		this.setCharacterBlockMovement(gameCharacter, true);
		if (gameCharacter.getVariableBoolean("BumpFall")) {
			long long1 = gameCharacter.playSound("TripOverObstacle");
			ParameterCharacterMovementSpeed parameterCharacterMovementSpeed = ((IsoPlayer)gameCharacter).getParameterCharacterMovementSpeed();
			gameCharacter.getEmitter().setParameterValue(long1, parameterCharacterMovementSpeed.getParameterDescription(), parameterCharacterMovementSpeed.calculateCurrentValue());
			String string = gameCharacter.getVariableString("TripObstacleType");
			if (string == null) {
				string = "zombie";
			}

			gameCharacter.clearVariable("TripObstacleType");
			byte byte1 = -1;
			switch (string.hashCode()) {
			case 3568542: 
				if (string.equals("tree")) {
					byte1 = 0;
				}

			
			default: 
				byte byte2;
				switch (byte1) {
				case 0: 
					byte2 = 5;
					break;
				
				default: 
					byte2 = 6;
				
				}

				byte byte3 = byte2;
				gameCharacter.getEmitter().setParameterValue(long1, FMODManager.instance.getParameterDescription("TripObstacleType"), (float)byte3);
			
			}
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		boolean boolean1 = gameCharacter.isBumpFall() || gameCharacter.isBumpStaggered();
		this.setCharacterBlockMovement(gameCharacter, boolean1);
	}

	private void setCharacterBlockMovement(IsoGameCharacter gameCharacter, boolean boolean1) {
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(gameCharacter, IsoPlayer.class);
		if (player != null) {
			player.setBlockMovement(boolean1);
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.clearVariable("BumpFallType");
		gameCharacter.clearVariable("BumpFallAnimFinished");
		gameCharacter.clearVariable("BumpAnimFinished");
		gameCharacter.setBumpType("");
		gameCharacter.setBumpedChr((IsoGameCharacter)null);
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(gameCharacter, IsoPlayer.class);
		if (player != null) {
			player.setInitiateAttack(false);
			player.attackStarted = false;
			player.setAttackType((String)null);
		}

		if (player != null && gameCharacter.isBumpFall()) {
			gameCharacter.fallenOnKnees();
		}

		gameCharacter.setOnFloor(false);
		gameCharacter.setBumpFall(false);
		this.setCharacterBlockMovement(gameCharacter, false);
		if (gameCharacter instanceof IsoZombie && ((IsoZombie)gameCharacter).target != null) {
			gameCharacter.pathToLocation((int)((IsoZombie)gameCharacter).target.getX(), (int)((IsoZombie)gameCharacter).target.getY(), (int)((IsoZombie)gameCharacter).target.getZ());
		}
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
			gameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
			gameCharacter.setOnFloor(gameCharacter.isFallOnFront());
		}

		if (animEvent.m_EventName.equalsIgnoreCase("FallOnBack")) {
			gameCharacter.setOnFloor(Boolean.parseBoolean(animEvent.m_ParameterValue));
		}
	}
}
