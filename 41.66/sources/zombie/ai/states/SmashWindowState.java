package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoDirections;
import zombie.iso.objects.IsoWindow;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleWindow;


public final class SmashWindowState extends State {
	private static final SmashWindowState _instance = new SmashWindowState();

	public static SmashWindowState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(true);
		gameCharacter.setVariable("bSmashWindow", true);
		HandWeapon handWeapon = (HandWeapon)Type.tryCastTo(gameCharacter.getPrimaryHandItem(), HandWeapon.class);
		if (handWeapon != null && handWeapon.isRanged()) {
			gameCharacter.playSound("AttackShove");
		} else if (handWeapon != null && !StringUtils.isNullOrWhitespace(handWeapon.getSwingSound())) {
			gameCharacter.playSound(handWeapon.getSwingSound());
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		if (!(hashMap.get(0) instanceof IsoWindow) && !(hashMap.get(0) instanceof VehicleWindow)) {
			gameCharacter.setVariable("bSmashWindow", false);
		} else {
			IsoPlayer player = (IsoPlayer)Type.tryCastTo(gameCharacter, IsoPlayer.class);
			if (!player.pressedMovement(false) && !player.pressedCancelAction()) {
				if (!(hashMap.get(0) instanceof IsoWindow)) {
					if (hashMap.get(0) instanceof VehicleWindow) {
						VehicleWindow vehicleWindow = (VehicleWindow)hashMap.get(0);
						gameCharacter.faceThisObject((BaseVehicle)hashMap.get(1));
						if (vehicleWindow.isDestroyed() && !"true".equals(gameCharacter.getVariableString("OwnerSmashedIt"))) {
							gameCharacter.setVariable("bSmashWindow", false);
							return;
						}
					}
				} else {
					IsoWindow window = (IsoWindow)hashMap.get(0);
					if (window.getObjectIndex() == -1 || window.isDestroyed() && !"true".equals(gameCharacter.getVariableString("OwnerSmashedIt"))) {
						gameCharacter.setVariable("bSmashWindow", false);
						return;
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
				}
			} else {
				gameCharacter.setVariable("bSmashWindow", false);
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(false);
		gameCharacter.clearVariable("bSmashWindow");
		gameCharacter.clearVariable("OwnerSmashedIt");
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		if (hashMap.get(0) instanceof IsoWindow) {
			IsoWindow window = (IsoWindow)hashMap.get(0);
			if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck")) {
				gameCharacter.setVariable("OwnerSmashedIt", true);
				IsoPlayer.getInstance().ContextPanic = 0.0F;
				window.WeaponHit(gameCharacter, (HandWeapon)null);
				if (!(gameCharacter.getPrimaryHandItem() instanceof HandWeapon) && !(gameCharacter.getSecondaryHandItem() instanceof HandWeapon)) {
					gameCharacter.getBodyDamage().setScratchedWindow();
				}
			} else if (animEvent.m_EventName.equalsIgnoreCase("ActiveAnimFinishing")) {
				gameCharacter.setVariable("bSmashWindow", false);
				if (Boolean.TRUE == hashMap.get(3)) {
					gameCharacter.climbThroughWindow(window);
				}
			}
		} else if (hashMap.get(0) instanceof VehicleWindow) {
			VehicleWindow vehicleWindow = (VehicleWindow)hashMap.get(0);
			if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck")) {
				gameCharacter.setVariable("OwnerSmashedIt", true);
				IsoPlayer.getInstance().ContextPanic = 0.0F;
				vehicleWindow.hit(gameCharacter);
				if (!(gameCharacter.getPrimaryHandItem() instanceof HandWeapon) && !(gameCharacter.getSecondaryHandItem() instanceof HandWeapon)) {
					gameCharacter.getBodyDamage().setScratchedWindow();
				}
			} else if (animEvent.m_EventName.equalsIgnoreCase("ActiveAnimFinishing")) {
				gameCharacter.setVariable("bSmashWindow", false);
			}
		}
	}

	public boolean isDoingActionThatCanBeCancelled() {
		return true;
	}
}
