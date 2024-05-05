package zombie.characters.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import org.w3c.dom.Element;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.util.PZXmlUtil;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;


public final class ActionState {
	public final String name;
	public final ArrayList transitions = new ArrayList();
	private String[] m_tags;
	private String[] m_childTags;
	private static final Comparator transitionComparator = (var0,var1)->{
    return var1.conditions.size() - var0.conditions.size();
};

	public ActionState(String string) {
		this.name = string;
	}

	public final boolean canHaveSubStates() {
		return !PZArrayUtil.isNullOrEmpty((Object[])this.m_childTags);
	}

	public final boolean canBeSubstate() {
		return !PZArrayUtil.isNullOrEmpty((Object[])this.m_tags);
	}

	public final boolean canHaveSubState(ActionState actionState) {
		return canHaveSubState(this, actionState);
	}

	public static boolean canHaveSubState(ActionState actionState, ActionState actionState2) {
		String[] stringArray = actionState.m_childTags;
		String[] stringArray2 = actionState2.m_tags;
		return tagsOverlap(stringArray, stringArray2);
	}

	public static boolean tagsOverlap(String[] stringArray, String[] stringArray2) {
		if (PZArrayUtil.isNullOrEmpty((Object[])stringArray)) {
			return false;
		} else if (PZArrayUtil.isNullOrEmpty((Object[])stringArray2)) {
			return false;
		} else {
			boolean boolean1 = false;
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				String string = stringArray[int1];
				for (int int2 = 0; int2 < stringArray2.length; ++int2) {
					String string2 = stringArray2[int2];
					if (StringUtils.equalsIgnoreCase(string, string2)) {
						boolean1 = true;
						break;
					}
				}
			}

			return boolean1;
		}
	}

	public String getName() {
		return this.name;
	}

	public void load(String string) {
		File file = (new File(string)).getAbsoluteFile();
		File[] fileArray = file.listFiles((var0,stringx)->{
    return stringx.toLowerCase().endsWith(".xml");
});
		if (fileArray != null) {
			File[] fileArray2 = fileArray;
			int int1 = fileArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				File file2 = fileArray2[int2];
				this.parse(file2);
			}

			this.sortTransitions();
		}
	}

	public void parse(File file) {
		ArrayList arrayList = new ArrayList();
		ArrayList arrayList2 = new ArrayList();
		ArrayList arrayList3 = new ArrayList();
		String string = file.getPath();
		try {
			Element element = PZXmlUtil.parseXml(string);
			if (ActionTransition.parse(element, string, arrayList)) {
				this.transitions.addAll(arrayList);
				if (DebugLog.isEnabled(DebugType.ActionSystem)) {
					DebugLog.ActionSystem.debugln("Loaded transitions from file: %s", string);
				}

				return;
			}

			if (this.parseTags(element, arrayList2, arrayList3)) {
				this.m_tags = (String[])PZArrayUtil.concat(this.m_tags, (String[])arrayList2.toArray(new String[0]));
				this.m_childTags = (String[])PZArrayUtil.concat(this.m_childTags, (String[])arrayList3.toArray(new String[0]));
				if (DebugLog.isEnabled(DebugType.ActionSystem)) {
					DebugLog.ActionSystem.debugln("Loaded tags from file: %s", string);
				}

				return;
			}

			if (DebugLog.isEnabled(DebugType.ActionSystem)) {
				DebugLog.ActionSystem.warn("Unrecognized xml file. It does not appear to be a transition nor a tag(s). %s", string);
			}
		} catch (Exception exception) {
			DebugLog.ActionSystem.error("Error loading: " + string);
			DebugLog.ActionSystem.error(exception);
		}
	}

	private boolean parseTags(Element element, ArrayList arrayList, ArrayList arrayList2) {
		arrayList.clear();
		arrayList2.clear();
		if (element.getNodeName().equals("tags")) {
			PZXmlUtil.forEachElement(element, (elementx)->{
				if (elementx.getNodeName().equals("tag")) {
					arrayList2.add(elementx.getTextContent());
				}
			});

			return true;
		} else if (element.getNodeName().equals("childTags")) {
			PZXmlUtil.forEachElement(element, (elementx)->{
				if (elementx.getNodeName().equals("tag")) {
					arrayList2.add(elementx.getTextContent());
				}
			});

			return true;
		} else {
			return false;
		}
	}

	public void sortTransitions() {
		this.transitions.sort(transitionComparator);
	}

	public void resetForReload() {
		this.transitions.clear();
		this.m_tags = null;
		this.m_childTags = null;
	}
}
