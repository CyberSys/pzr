package zombie.ai.states;

import java.nio.ByteBuffer;
import java.util.HashMap;
import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.debug.DebugOptions;
import zombie.iso.IsoDirections;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoWindow;


public final class OpenWindowState extends State {
	private static final OpenWindowState _instance = new OpenWindowState();
	private static final Integer PARAM_WINDOW = 1;

	public static OpenWindowState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(true);
		gameCharacter.setHideWeaponModel(true);
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		IsoWindow window = (IsoWindow)hashMap.get(PARAM_WINDOW);
		if (Core.bDebug && DebugOptions.instance.CheatWindowUnlock.getValue() && window.getSprite() != null && !window.getSprite().getProperties().Is("WindowLocked")) {
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

		gameCharacter.setVariable("bOpenWindow", true);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		if (gameCharacter.getVariableBoolean("bOpenWindow")) {
			if (!IsoPlayer.getInstance().pressedMovement(false) && !IsoPlayer.getInstance().pressedCancelAction()) {
				IsoWindow window = (IsoWindow)hashMap.get(PARAM_WINDOW);
				if (window != null && window.getObjectIndex() != -1) {
					if (IsoPlayer.getInstance().ContextPanic > 5.0F) {
						IsoPlayer.getInstance().ContextPanic = 0.0F;
						gameCharacter.setVariable("bOpenWindow", false);
						gameCharacter.smashWindow(window);
						gameCharacter.getStateMachineParams(SmashWindowState.instance()).put(3, Boolean.TRUE);
					} else {
						IsoPlayer player = (IsoPlayer)gameCharacter;
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

						if (Core.bTutorial) {
							if (gameCharacter.x != window.getX() + 0.5F && window.north) {
								this.slideX(gameCharacter, window.getX() + 0.5F);
							}

							if (gameCharacter.y != window.getY() + 0.5F && !window.north) {
								this.slideY(gameCharacter, window.getY() + 0.5F);
							}
						}
					}
				} else {
					gameCharacter.setVariable("bOpenWindow", false);
				}
			} else {
				gameCharacter.setVariable("bOpenWindow", false);
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(false);
		gameCharacter.clearVariable("bOpenWindow");
		gameCharacter.clearVariable("OpenWindowOutcome");
		gameCharacter.clearVariable("StopAfterAnimLooped");
		gameCharacter.setHideWeaponModel(false);
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		if (gameCharacter.getVariableBoolean("bOpenWindow")) {
			IsoWindow window = (IsoWindow)hashMap.get(PARAM_WINDOW);
			if (window == null) {
				gameCharacter.setVariable("bOpenWindow", false);
			} else {
				if (animEvent.m_EventName.equalsIgnoreCase("WindowAnimLooped")) {
					if ("start".equalsIgnoreCase(animEvent.m_ParameterValue)) {
						if (window.isPermaLocked() || window.Locked && gameCharacter.getCurrentSquare().Is(IsoFlagType.exterior)) {
							gameCharacter.setVariable("OpenWindowOutcome", "struggle");
						} else {
							gameCharacter.setVariable("OpenWindowOutcome", "success");
						}

						return;
					}

					if (animEvent.m_ParameterValue.equalsIgnoreCase(gameCharacter.getVariableString("StopAfterAnimLooped"))) {
						gameCharacter.setVariable("bOpenWindow", false);
					}
				}

				if (animEvent.m_EventName.equalsIgnoreCase("WindowOpenAttempt")) {
					this.onAttemptFinished(gameCharacter, window);
				} else if (animEvent.m_EventName.equalsIgnoreCase("WindowOpenSuccess")) {
					this.onSuccess(gameCharacter, window);
				} else if (animEvent.m_EventName.equalsIgnoreCase("WindowStruggleSound") && "struggle".equals(gameCharacter.getVariableString("OpenWindowOutcome"))) {
					gameCharacter.playSound("WindowIsLocked");
				}
			}
		}
	}

	public boolean isDoingActionThatCanBeCancelled() {
		return true;
	}

	private void onAttemptFinished(IsoGameCharacter gameCharacter, IsoWindow window) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		this.exert(gameCharacter);
		if (window.isPermaLocked()) {
			if (!gameCharacter.getEmitter().isPlaying("WindowIsLocked")) {
			}

			gameCharacter.setVariable("OpenWindowOutcome", "fail");
			gameCharacter.setVariable("StopAfterAnimLooped", "fail");
		} else {
			byte byte1 = 10;
			if (gameCharacter.Traits.Burglar.isSet()) {
				byte1 = 5;
			}

			if (window.Locked && gameCharacter.getCurrentSquare().Is(IsoFlagType.exterior)) {
				if (Rand.Next(100) < byte1) {
					gameCharacter.getEmitter().playSound("BreakLockOnWindow", window);
					window.setPermaLocked(true);
					window.syncIsoObject(false, (byte)0, (UdpConnection)null, (ByteBuffer)null);
					hashMap.put(PARAM_WINDOW, (Object)null);
					gameCharacter.setVariable("OpenWindowOutcome", "fail");
					gameCharacter.setVariable("StopAfterAnimLooped", "fail");
					return;
				}

				boolean boolean1 = false;
				if (gameCharacter.getPerkLevel(PerkFactory.Perks.Strength) > 7 && Rand.Next(100) < 20) {
					boolean1 = true;
				} else if (gameCharacter.getPerkLevel(PerkFactory.Perks.Strength) > 5 && Rand.Next(100) < 10) {
					boolean1 = true;
				} else if (gameCharacter.getPerkLevel(PerkFactory.Perks.Strength) > 3 && Rand.Next(100) < 6) {
					boolean1 = true;
				} else if (gameCharacter.getPerkLevel(PerkFactory.Perks.Strength) > 1 && Rand.Next(100) < 4) {
					boolean1 = true;
				} else if (Rand.Next(100) <= 1) {
					boolean1 = true;
				}

				if (boolean1) {
					gameCharacter.setVariable("OpenWindowOutcome", "success");
				}
			} else {
				gameCharacter.setVariable("OpenWindowOutcome", "success");
			}
		}
	}

	private void onSuccess(IsoGameCharacter gameCharacter, IsoWindow window) {
		gameCharacter.setVariable("StopAfterAnimLooped", "success");
		IsoPlayer.getInstance().ContextPanic = 0.0F;
		if (window.getObjectIndex() != -1 && !window.open) {
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

	private void slideX(IsoGameCharacter gameCharacter, float float1) {
		float float2 = 0.05F * GameTime.getInstance().getMultiplier() / 1.6F;
		float2 = float1 > gameCharacter.x ? Math.min(float2, float1 - gameCharacter.x) : Math.max(-float2, float1 - gameCharacter.x);
		gameCharacter.x += float2;
		gameCharacter.nx = gameCharacter.x;
	}

	private void slideY(IsoGameCharacter gameCharacter, float float1) {
		float float2 = 0.05F * GameTime.getInstance().getMultiplier() / 1.6F;
		float2 = float1 > gameCharacter.y ? Math.min(float2, float1 - gameCharacter.y) : Math.max(-float2, float1 - gameCharacter.y);
		gameCharacter.y += float2;
		gameCharacter.ny = gameCharacter.y;
	}

	public void setParams(IsoGameCharacter gameCharacter, IsoWindow window) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		hashMap.clear();
		hashMap.put(PARAM_WINDOW, window);
	}
}
