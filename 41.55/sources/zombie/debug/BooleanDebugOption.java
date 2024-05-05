package zombie.debug;

import zombie.config.BooleanConfigOption;
import zombie.core.Core;
import zombie.debug.options.IDebugOption;
import zombie.debug.options.IDebugOptionGroup;


public class BooleanDebugOption extends BooleanConfigOption implements IDebugOption {
	private IDebugOptionGroup m_parent;
	private final boolean m_debugOnly;

	public BooleanDebugOption(String string, boolean boolean1, boolean boolean2) {
		super(string, boolean2);
		this.m_debugOnly = boolean1;
	}

	public boolean getValue() {
		return !Core.bDebug && this.isDebugOnly() ? super.getDefaultValue() : super.getValue();
	}

	public boolean isDebugOnly() {
		return this.m_debugOnly;
	}

	public IDebugOptionGroup getParent() {
		return this.m_parent;
	}

	public void setParent(IDebugOptionGroup iDebugOptionGroup) {
		this.m_parent = iDebugOptionGroup;
	}
}
