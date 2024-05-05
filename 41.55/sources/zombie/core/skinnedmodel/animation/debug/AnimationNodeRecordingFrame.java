package zombie.core.skinnedmodel.animation.debug;

import java.util.ArrayList;
import java.util.List;
import zombie.ai.State;
import zombie.characters.action.ActionState;
import zombie.core.skinnedmodel.advancedanimation.AnimState;
import zombie.iso.Vector3;
import zombie.util.list.PZArrayUtil;


public final class AnimationNodeRecordingFrame extends GenericNameWeightRecordingFrame {
	private String m_actionStateName;
	private final ArrayList m_actionSubStateNames = new ArrayList();
	private String m_aiStateName;
	private String m_animStateName;
	private final ArrayList m_animSubStateNames = new ArrayList();
	private final ArrayList m_aiSubStateNames = new ArrayList();
	private final Vector3 m_characterToPlayerDiff = new Vector3();

	public AnimationNodeRecordingFrame(String string) {
		super(string);
	}

	public void logActionState(ActionState actionState, List list) {
		this.m_actionStateName = actionState != null ? actionState.getName() : null;
		PZArrayUtil.arrayConvert(this.m_actionSubStateNames, list, ActionState::getName);
	}

	public void logAIState(State state, List list) {
		this.m_aiStateName = state != null ? state.getName() : null;
		PZArrayUtil.arrayConvert(this.m_aiSubStateNames, list, (var0)->{
			return !var0.isEmpty() ? var0.getState().getName() : "";
		});
	}

	public void logAnimState(AnimState animState) {
		this.m_animStateName = animState != null ? animState.m_Name : null;
	}

	public void logCharacterToPlayerDiff(Vector3 vector3) {
		this.m_characterToPlayerDiff.set(vector3);
	}

	public void writeHeader(StringBuilder stringBuilder) {
		appendCell(stringBuilder, "toPlayer.x");
		appendCell(stringBuilder, "toPlayer.y");
		appendCell(stringBuilder, "actionState");
		appendCell(stringBuilder, "actionState.sub[0]");
		appendCell(stringBuilder, "actionState.sub[1]");
		appendCell(stringBuilder, "aiState");
		appendCell(stringBuilder, "aiState.sub[0]");
		appendCell(stringBuilder, "aiState.sub[1]");
		appendCell(stringBuilder, "animState");
		appendCell(stringBuilder, "animState.sub[0]");
		appendCell(stringBuilder, "animState.sub[1]");
		appendCell(stringBuilder, "nodeWeights.begin");
		super.writeHeader(stringBuilder);
	}

	protected void writeData(StringBuilder stringBuilder) {
		appendCell(stringBuilder, this.m_characterToPlayerDiff.x);
		appendCell(stringBuilder, this.m_characterToPlayerDiff.y);
		appendCellQuot(stringBuilder, this.m_actionStateName);
		appendCellQuot(stringBuilder, (String)PZArrayUtil.getOrDefault((List)this.m_actionSubStateNames, 0, ""));
		appendCellQuot(stringBuilder, (String)PZArrayUtil.getOrDefault((List)this.m_actionSubStateNames, 1, ""));
		appendCellQuot(stringBuilder, this.m_aiStateName);
		appendCellQuot(stringBuilder, (String)PZArrayUtil.getOrDefault((List)this.m_aiSubStateNames, 0, ""));
		appendCellQuot(stringBuilder, (String)PZArrayUtil.getOrDefault((List)this.m_aiSubStateNames, 1, ""));
		appendCellQuot(stringBuilder, this.m_animStateName);
		appendCellQuot(stringBuilder, (String)PZArrayUtil.getOrDefault((List)this.m_animSubStateNames, 0, ""));
		appendCellQuot(stringBuilder, (String)PZArrayUtil.getOrDefault((List)this.m_animSubStateNames, 1, ""));
		appendCell(stringBuilder);
		super.writeData(stringBuilder);
	}
}
