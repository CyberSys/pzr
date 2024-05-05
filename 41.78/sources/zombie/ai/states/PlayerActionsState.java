package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.network.GameClient;
import zombie.util.StringUtils;


public final class PlayerActionsState extends State {
	private static final PlayerActionsState _instance = new PlayerActionsState();

	public static PlayerActionsState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		InventoryItem inventoryItem = gameCharacter.getPrimaryHandItem();
		InventoryItem inventoryItem2 = gameCharacter.getSecondaryHandItem();
		if (!(inventoryItem instanceof HandWeapon) && !(inventoryItem2 instanceof HandWeapon)) {
			gameCharacter.setHideWeaponModel(true);
		}

		String string = gameCharacter.getVariableString("PerformingAction");
		if (GameClient.bClient && gameCharacter instanceof IsoPlayer && gameCharacter.isLocal() && !gameCharacter.getCharacterActions().isEmpty() && gameCharacter.getNetworkCharacterAI().getAction() == null) {
			gameCharacter.getNetworkCharacterAI().setAction((BaseAction)gameCharacter.getCharacterActions().get(0));
			GameClient.sendAction(gameCharacter.getNetworkCharacterAI().getAction(), true);
			gameCharacter.getNetworkCharacterAI().setPerformingAction(string);
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (GameClient.bClient && gameCharacter instanceof IsoPlayer && gameCharacter.isLocal()) {
			String string = gameCharacter.getVariableString("PerformingAction");
			if (!gameCharacter.getCharacterActions().isEmpty() && (gameCharacter.getNetworkCharacterAI().getAction() != gameCharacter.getCharacterActions().get(0) || string != null && !string.equals(gameCharacter.getNetworkCharacterAI().getPerformingAction()))) {
				GameClient.sendAction(gameCharacter.getNetworkCharacterAI().getAction(), false);
				gameCharacter.getNetworkCharacterAI().setAction((BaseAction)gameCharacter.getCharacterActions().get(0));
				GameClient.sendAction(gameCharacter.getNetworkCharacterAI().getAction(), true);
				gameCharacter.getNetworkCharacterAI().setPerformingAction(string);
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setHideWeaponModel(false);
		if (GameClient.bClient && gameCharacter instanceof IsoPlayer && gameCharacter.isLocal() && gameCharacter.getNetworkCharacterAI().getAction() != null) {
			GameClient.sendAction(gameCharacter.getNetworkCharacterAI().getAction(), false);
			gameCharacter.getNetworkCharacterAI().setAction((BaseAction)null);
		}
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if (GameClient.bClient && animEvent != null && gameCharacter instanceof IsoPlayer && gameCharacter.getNetworkCharacterAI().getAction() != null && !gameCharacter.isLocal() && "changeWeaponSprite".equalsIgnoreCase(animEvent.m_EventName) && !StringUtils.isNullOrEmpty(animEvent.m_ParameterValue)) {
			if ("original".equals(animEvent.m_ParameterValue)) {
				gameCharacter.getNetworkCharacterAI().setOverride(false, (String)null, (String)null);
			} else {
				gameCharacter.getNetworkCharacterAI().setOverride(true, animEvent.m_ParameterValue, (String)null);
			}
		}
	}
}
