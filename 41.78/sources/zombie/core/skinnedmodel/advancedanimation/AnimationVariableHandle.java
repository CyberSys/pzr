package zombie.core.skinnedmodel.advancedanimation;


public class AnimationVariableHandle {
	private String m_name = null;
	private int m_varIndex = -1;

	AnimationVariableHandle() {
	}

	public static AnimationVariableHandle alloc(String string) {
		return AnimationVariableHandlePool.getOrCreate(string);
	}

	public String getVariableName() {
		return this.m_name;
	}

	public int getVariableIndex() {
		return this.m_varIndex;
	}

	void setVariableName(String string) {
		this.m_name = string;
	}

	void setVariableIndex(int int1) {
		this.m_varIndex = int1;
	}
}
