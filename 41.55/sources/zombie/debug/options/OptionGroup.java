package zombie.debug.options;

import java.util.ArrayList;
import zombie.debug.BooleanDebugOption;


public class OptionGroup implements IDebugOptionGroup {
	public final IDebugOptionGroup Group;
	private IDebugOptionGroup m_parentGroup;
	private final String m_groupName;
	private final ArrayList m_children = new ArrayList();

	public OptionGroup(String string) {
		this.m_groupName = string;
		this.Group = this;
	}

	public OptionGroup(IDebugOptionGroup iDebugOptionGroup, String string) {
		this.m_groupName = getCombinedName(iDebugOptionGroup, string);
		this.Group = this;
		iDebugOptionGroup.addChild(this);
	}

	public String getName() {
		return this.m_groupName;
	}

	public IDebugOptionGroup getParent() {
		return this.m_parentGroup;
	}

	public void setParent(IDebugOptionGroup iDebugOptionGroup) {
		this.m_parentGroup = iDebugOptionGroup;
	}

	public Iterable getChildren() {
		return this.m_children;
	}

	public void addChild(IDebugOption iDebugOption) {
		this.m_children.add(iDebugOption);
		iDebugOption.setParent(this);
		this.onChildAdded(iDebugOption);
	}

	public void onChildAdded(IDebugOption iDebugOption) {
		this.onDescendantAdded(iDebugOption);
	}

	public void onDescendantAdded(IDebugOption iDebugOption) {
		if (this.m_parentGroup != null) {
			this.m_parentGroup.onDescendantAdded(iDebugOption);
		}
	}

	public static BooleanDebugOption newOption(String string, boolean boolean1) {
		return newOptionInternal((IDebugOptionGroup)null, string, false, boolean1);
	}

	public static BooleanDebugOption newDebugOnlyOption(String string, boolean boolean1) {
		return newOptionInternal((IDebugOptionGroup)null, string, true, boolean1);
	}

	public static BooleanDebugOption newOption(IDebugOptionGroup iDebugOptionGroup, String string, boolean boolean1) {
		return newOptionInternal(iDebugOptionGroup, string, false, boolean1);
	}

	public static BooleanDebugOption newDebugOnlyOption(IDebugOptionGroup iDebugOptionGroup, String string, boolean boolean1) {
		return newOptionInternal(iDebugOptionGroup, string, true, boolean1);
	}

	private static BooleanDebugOption newOptionInternal(IDebugOptionGroup iDebugOptionGroup, String string, boolean boolean1, boolean boolean2) {
		String string2 = getCombinedName(iDebugOptionGroup, string);
		BooleanDebugOption booleanDebugOption = new BooleanDebugOption(string2, boolean1, boolean2);
		if (iDebugOptionGroup != null) {
			iDebugOptionGroup.addChild(booleanDebugOption);
		}

		return booleanDebugOption;
	}

	private static String getCombinedName(IDebugOptionGroup iDebugOptionGroup, String string) {
		String string2;
		if (iDebugOptionGroup != null) {
			string2 = String.format("%s.%s", iDebugOptionGroup.getName(), string);
		} else {
			string2 = string;
		}

		return string2;
	}
}
