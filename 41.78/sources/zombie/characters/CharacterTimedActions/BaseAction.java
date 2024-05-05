package zombie.characters.CharacterTimedActions;

import java.util.ArrayList;
import java.util.Arrays;
import zombie.GameTime;
import zombie.ai.states.PlayerActionsState;
import zombie.characters.CharacterActionAnims;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.network.packets.EventPacket;
import zombie.ui.UIManager;
import zombie.util.StringUtils;
import zombie.util.Type;


public class BaseAction {
	public long SoundEffect = -1L;
	public float CurrentTime = -2.0F;
	public float LastTime = -1.0F;
	public int MaxTime = 60;
	public float PrevLastTime = 0.0F;
	public boolean UseProgressBar = true;
	public boolean ForceProgressBar = false;
	public IsoGameCharacter chr;
	public boolean StopOnWalk = true;
	public boolean StopOnRun = true;
	public boolean StopOnAim = false;
	public float caloriesModifier = 1.0F;
	public float delta = 0.0F;
	public boolean blockMovementEtc;
	public boolean overrideAnimation;
	public final ArrayList animVariables = new ArrayList();
	public boolean loopAction = false;
	public boolean bStarted = false;
	public boolean forceStop = false;
	public boolean forceComplete = false;
	private static final ArrayList specificNetworkAnim = new ArrayList(Arrays.asList("Reload", "Bandage", "Loot", "AttachItem", "Drink", "Eat", "Pour", "Read", "fill_container_tap", "drink_tap", "WearClothing"));
	private InventoryItem primaryHandItem = null;
	private InventoryItem secondaryHandItem = null;
	private String primaryHandMdl;
	private String secondaryHandMdl;
	public boolean overrideHandModels = false;

	public BaseAction(IsoGameCharacter gameCharacter) {
		this.chr = gameCharacter;
	}

	public void forceStop() {
		this.forceStop = true;
	}

	public void forceComplete() {
		this.forceComplete = true;
	}

	public void PlayLoopedSoundTillComplete(String string, int int1, float float1) {
		this.SoundEffect = this.chr.getEmitter().playSound(string);
	}

	public boolean hasStalled() {
		if (!this.bStarted) {
			return false;
		} else {
			return this.LastTime == this.CurrentTime && this.LastTime == this.PrevLastTime && this.LastTime < 0.0F || this.CurrentTime < 0.0F;
		}
	}

	public float getJobDelta() {
		return this.delta;
	}

	public void resetJobDelta() {
		this.delta = 0.0F;
		this.CurrentTime = 0.0F;
	}

	public void waitToStart() {
		if (!this.chr.shouldWaitToStartTimedAction()) {
			this.bStarted = true;
			this.start();
		}
	}

	public void update() {
		this.PrevLastTime = this.LastTime;
		this.LastTime = this.CurrentTime;
		this.CurrentTime += GameTime.instance.getMultiplier();
		if (this.CurrentTime < 0.0F) {
			this.CurrentTime = 0.0F;
		}

		boolean boolean1 = (Core.getInstance().isOptionProgressBar() || this.ForceProgressBar) && this.UseProgressBar && this.chr instanceof IsoPlayer && ((IsoPlayer)this.chr).isLocalPlayer();
		if (this.MaxTime == -1) {
			if (boolean1) {
				UIManager.getProgressBar((double)((IsoPlayer)this.chr).getPlayerNum()).setValue(Float.POSITIVE_INFINITY);
			}
		} else {
			if (this.MaxTime == 0) {
				this.delta = 0.0F;
			} else {
				this.delta = Math.min(this.CurrentTime / (float)this.MaxTime, 1.0F);
			}

			if (boolean1) {
				UIManager.getProgressBar((double)((IsoPlayer)this.chr).getPlayerNum()).setValue(this.delta);
			}
		}
	}

	public void start() {
		this.forceComplete = false;
		this.forceStop = false;
		if (this.chr.isCurrentState(PlayerActionsState.instance())) {
			InventoryItem inventoryItem = this.chr.getPrimaryHandItem();
			InventoryItem inventoryItem2 = this.chr.getSecondaryHandItem();
			this.chr.setHideWeaponModel(!(inventoryItem instanceof HandWeapon) && !(inventoryItem2 instanceof HandWeapon));
		}
	}

	public void reset() {
		this.CurrentTime = 0.0F;
		this.forceComplete = false;
		this.forceStop = false;
	}

	public float getCurrentTime() {
		return this.CurrentTime;
	}

	public void stop() {
		UIManager.getProgressBar((double)((IsoPlayer)this.chr).getPlayerNum()).setValue(0.0F);
		if (this.SoundEffect > -1L) {
			this.chr.getEmitter().stopSound(this.SoundEffect);
			this.SoundEffect = -1L;
		}

		this.stopTimedActionAnim();
	}

	public boolean valid() {
		return true;
	}

	public boolean finished() {
		return this.CurrentTime >= (float)this.MaxTime && this.MaxTime != -1;
	}

	public void perform() {
		UIManager.getProgressBar((double)((IsoPlayer)this.chr).getPlayerNum()).setValue(1.0F);
		if (!this.loopAction) {
			this.stopTimedActionAnim();
		}
	}

	public void setUseProgressBar(boolean boolean1) {
		this.UseProgressBar = boolean1;
	}

	public void setBlockMovementEtc(boolean boolean1) {
		this.blockMovementEtc = boolean1;
	}

	public void setOverrideAnimation(boolean boolean1) {
		this.overrideAnimation = boolean1;
	}

	public void stopTimedActionAnim() {
		for (int int1 = 0; int1 < this.animVariables.size(); ++int1) {
			String string = (String)this.animVariables.get(int1);
			this.chr.clearVariable(string);
		}

		this.chr.setVariable("IsPerformingAnAction", false);
		if (this.overrideHandModels) {
			this.overrideHandModels = false;
			this.chr.resetEquippedHandsModels();
		}
	}

	public void setAnimVariable(String string, String string2) {
		if (!this.animVariables.contains(string)) {
			this.animVariables.add(string);
		}

		this.chr.setVariable(string, string2);
	}

	public void setAnimVariable(String string, boolean boolean1) {
		if (!this.animVariables.contains(string)) {
			this.animVariables.add(string);
		}

		this.chr.setVariable(string, String.valueOf(boolean1));
	}

	public String getPrimaryHandMdl() {
		return this.primaryHandMdl;
	}

	public String getSecondaryHandMdl() {
		return this.secondaryHandMdl;
	}

	public InventoryItem getPrimaryHandItem() {
		return this.primaryHandItem;
	}

	public InventoryItem getSecondaryHandItem() {
		return this.secondaryHandItem;
	}

	public void setActionAnim(CharacterActionAnims characterActionAnims) {
		this.setActionAnim(characterActionAnims.toString());
	}

	public void setActionAnim(String string) {
		this.setAnimVariable("PerformingAction", string);
		this.chr.setVariable("IsPerformingAnAction", true);
		if (Core.bDebug) {
			this.chr.advancedAnimator.printDebugCharacterActions(string);
		}
	}

	public void setOverrideHandModels(InventoryItem inventoryItem, InventoryItem inventoryItem2) {
		this.setOverrideHandModels(inventoryItem, inventoryItem2, true);
	}

	public void setOverrideHandModels(InventoryItem inventoryItem, InventoryItem inventoryItem2, boolean boolean1) {
		this.setOverrideHandModelsObject(inventoryItem, inventoryItem2, boolean1);
	}

	public void setOverrideHandModelsString(String string, String string2) {
		this.setOverrideHandModelsString(string, string2, true);
	}

	public void setOverrideHandModelsString(String string, String string2, boolean boolean1) {
		this.setOverrideHandModelsObject(string, string2, boolean1);
	}

	public void setOverrideHandModelsObject(Object object, Object object2, boolean boolean1) {
		this.overrideHandModels = true;
		this.primaryHandItem = (InventoryItem)Type.tryCastTo(object, InventoryItem.class);
		this.secondaryHandItem = (InventoryItem)Type.tryCastTo(object2, InventoryItem.class);
		this.primaryHandMdl = StringUtils.discardNullOrWhitespace((String)Type.tryCastTo(object, String.class));
		this.secondaryHandMdl = StringUtils.discardNullOrWhitespace((String)Type.tryCastTo(object2, String.class));
		if (boolean1) {
			this.chr.resetEquippedHandsModels();
		}

		if (this.primaryHandItem != null || this.secondaryHandItem != null) {
			this.chr.reportEvent(EventPacket.EventType.EventOverrideItem.name());
		}
	}

	public void OnAnimEvent(AnimEvent animEvent) {
	}

	public void setLoopedAction(boolean boolean1) {
		this.loopAction = boolean1;
	}
}
