package zombie.core.skinnedmodel.animation.debug;

import java.util.Iterator;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSlot;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSource;
import zombie.debug.DebugLog;
import zombie.util.list.PZArrayUtil;


public final class AnimationVariableRecordingFrame extends GenericNameValueRecordingFrame {
	private String[] m_variableValues = new String[0];

	public AnimationVariableRecordingFrame(String string) {
		super(string, "_values");
	}

	public void logVariables(IAnimationVariableSource iAnimationVariableSource) {
		Iterator iterator = iAnimationVariableSource.getGameVariables().iterator();
		while (iterator.hasNext()) {
			IAnimationVariableSlot iAnimationVariableSlot = (IAnimationVariableSlot)iterator.next();
			this.logVariable(iAnimationVariableSlot.getKey(), iAnimationVariableSlot.getValueString());
		}
	}

	protected void onColumnAdded() {
		this.m_variableValues = (String[])PZArrayUtil.add(this.m_variableValues, (Object)null);
	}

	public void logVariable(String string, String string2) {
		int int1 = this.getOrCreateColumn(string);
		if (this.m_variableValues[int1] != null) {
			DebugLog.General.error("Value for %s already set: %f, new value: %f", string, this.m_variableValues[int1], string2);
		}

		this.m_variableValues[int1] = string2;
	}

	public String getValueAt(int int1) {
		return this.m_variableValues[int1];
	}

	public void reset() {
		int int1 = 0;
		for (int int2 = this.m_variableValues.length; int1 < int2; ++int1) {
			this.m_variableValues[int1] = null;
		}
	}
}
