package zombie.ai.states;

import java.util.HashMap;
import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.debug.DebugOptions;
import zombie.iso.IsoDirections;
import zombie.iso.objects.IsoWindow;


public final class CloseWindowState extends State {
	private static final CloseWindowState _instance = new CloseWindowState();

	public static CloseWindowState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		gameCharacter.setIgnoreMovement(true);
		gameCharacter.setHideWeaponModel(true);
		IsoWindow window = (IsoWindow)hashMap.get(0);
		if (Core.bDebug && DebugOptions.instance.CheatWindowUnlock.getValue()) {
			window.Locked = false;
			window.PermaLocked = false;
		}

		if (window.north) {
			if ((float)window.getSquare().getY() < gameCharacter.getY()) {
				gameCharacter.setDir(IsoDirections.N);
			} else {
				gameCharacter.setDir(IsoDirections.S);
			}
		} else if ((float)window.getSquare().getX() < gameCharacter.getX()) {
			gameCharacter.setDir(IsoDirections.W);
		} else {
			gameCharacter.setDir(IsoDirections.E);
		}

		gameCharacter.setVariable("bCloseWindow", true);
		gameCharacter.clearVariable("BlockWindow");
	}

	public void execute(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		if (gameCharacter.getVariableBoolean("bCloseWindow")) {
			IsoPlayer player = (IsoPlayer)gameCharacter;
			if (!player.pressedMovement(false) && !player.pressedCancelAction()) {
				if (!(hashMap.get(0) instanceof IsoWindow)) {
					gameCharacter.setVariable("bCloseWindow", false);
				} else {
					IsoWindow window = (IsoWindow)hashMap.get(0);
					if (window != null && window.getObjectIndex() != -1) {
						player.setCollidable(true);
						player.updateLOS();
						if (window.north) {
							if ((float)window.getSquare().getY() < gameCharacter.getY()) {
								gameCharacter.setDir(IsoDirections.N);
							} else {
								gameCharacter.setDir(IsoDirections.S);
							}
						} else if ((float)window.getSquare().getX() < gameCharacter.getX()) {
							gameCharacter.setDir(IsoDirections.W);
						} else {
							gameCharacter.setDir(IsoDirections.E);
						}
					} else {
						gameCharacter.setVariable("bCloseWindow", false);
					}
				}
			} else {
				gameCharacter.setVariable("bCloseWindow", false);
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.clearVariable("BlockWindow");
		gameCharacter.clearVariable("bCloseWindow");
		gameCharacter.clearVariable("CloseWindowOutcome");
		gameCharacter.clearVariable("StopAfterAnimLooped");
		gameCharacter.setIgnoreMovement(false);
		gameCharacter.setHideWeaponModel(false);
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		if (gameCharacter.getVariableBoolean("bCloseWindow")) {
			if (!(hashMap.get(0) instanceof IsoWindow)) {
				gameCharacter.setVariable("bCloseWindow", false);
			} else {
				IsoWindow window = (IsoWindow)hashMap.get(0);
				if (animEvent.m_EventName.equalsIgnoreCase("WindowAnimLooped")) {
					if ("start".equalsIgnoreCase(animEvent.m_ParameterValue)) {
						int int1 = Math.max(5 - gameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic), 1);
						if (!window.isPermaLocked() && window.getFirstCharacterClimbingThrough() == null) {
							gameCharacter.setVariable("CloseWindowOutcome", "success");
						} else {
							gameCharacter.setVariable("CloseWindowOutcome", "struggle");
						}

						return;
					}

					if (animEvent.m_ParameterValue.equalsIgnoreCase(gameCharacter.getVariableString("StopAfterAnimLooped"))) {
						gameCharacter.setVariable("bCloseWindow", false);
					}
				}

				if (animEvent.m_EventName.equalsIgnoreCase("WindowCloseAttempt")) {
					this.onAttemptFinished(gameCharacter, window);
				} else if (animEvent.m_EventName.equalsIgnoreCase("WindowCloseSuccess")) {
					this.onSuccess(gameCharacter, window);
				}
			}
		}
	}

	public boolean isDoingActionThatCanBeCancelled() {
		return true;
	}

	private void onAttemptFinished(IsoGameCharacter gameCharacter, IsoWindow window) {
		this.exert(gameCharacter);
		if (window.isPermaLocked()) {
			gameCharacter.getEmitter().playSound("WindowIsLocked", window);
			gameCharacter.setVariable("CloseWindowOutcome", "fail");
			gameCharacter.setVariable("StopAfterAnimLooped", "fail");
		} else {
			int int1 = Math.max(5 - gameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic), 3);
			if (!window.isPermaLocked() && window.getFirstCharacterClimbingThrough() == null) {
				gameCharacter.setVariable("CloseWindowOutcome", "success");
			} else {
				gameCharacter.setVariable("CloseWindowOutcome", "struggle");
			}
		}
	}

	private void onSuccess(IsoGameCharacter gameCharacter, IsoWindow window) {
		gameCharacter.setVariable("StopAfterAnimLooped", "success");
		IsoPlayer.getInstance().ContextPanic = 0.0F;
		if (window.getObjectIndex() != -1 && window.open) {
			window.ToggleWindow(gameCharacter);
		}
	}

	private void exert(IsoGameCharacter gameCharacter) {
		float float1 = GameTime.getInstance().getMultiplier() / 1.6F;
		switch (gameCharacter.getPerkLevel(PerkFactory.Perks.Fitness)) {
		case 1: 
			gameCharacter.exert(0.01F * float1);
			break;
		
		case 2: 
			gameCharacter.exert(0.009F * float1);
			break;
		
		case 3: 
			gameCharacter.exert(0.008F * float1);
			break;
		
		case 4: 
			gameCharacter.exert(0.007F * float1);
			break;
		
		case 5: 
			gameCharacter.exert(0.006F * float1);
			break;
		
		case 6: 
			gameCharacter.exert(0.005F * float1);
			break;
		
		case 7: 
			gameCharacter.exert(0.004F * float1);
			break;
		
		case 8: 
			gameCharacter.exert(0.003F * float1);
			break;
		
		case 9: 
			gameCharacter.exert(0.0025F * float1);
			break;
		
		case 10: 
			gameCharacter.exert(0.002F * float1);
		
		}
	}

	public IsoWindow getWindow(IsoGameCharacter gameCharacter) {
		if (!gameCharacter.isCurrentState(this)) {
			return null;
		} else {
			HashMap hashMap = gameCharacter.getStateMachineParams(this);
			return (IsoWindow)hashMap.get(0);
		}
	}
}
