package zombie.characters.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import zombie.ZomboidFileSystem;
import zombie.debug.DebugLog;
import zombie.debug.DebugLogStream;
import zombie.debug.DebugType;


public final class ActionGroup {
	private static final Map actionGroupMap = new HashMap();
	String initialState;
	private List states = new ArrayList();
	private Map stateLookup;

	public static ActionGroup getActionGroup(String string) {
		string = string.toLowerCase();
		ActionGroup actionGroup = (ActionGroup)actionGroupMap.get(string);
		if (actionGroup == null && !actionGroupMap.containsKey(string)) {
			actionGroup = new ActionGroup();
			actionGroupMap.put(string, actionGroup);
			try {
				actionGroup.load(string);
			} catch (Exception exception) {
				DebugLog.ActionSystem.error("Error loading action group: " + string);
				exception.printStackTrace(DebugLog.ActionSystem);
			}

			return actionGroup;
		} else {
			return actionGroup;
		}
	}

	public static void reloadAll() {
		Iterator iterator = actionGroupMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			ActionGroup actionGroup = (ActionGroup)entry.getValue();
			Iterator iterator2 = actionGroup.states.iterator();
			while (iterator2.hasNext()) {
				ActionState actionState = (ActionState)iterator2.next();
				actionState.resetForReload();
			}

			actionGroup.load((String)entry.getKey());
		}
	}

	void load(String string) {
		if (DebugLog.isEnabled(DebugType.ActionSystem)) {
			DebugLog.ActionSystem.debugln("Loading ActionGroup: " + string);
		}

		File file = ZomboidFileSystem.instance.getMediaFile("actiongroups/" + string + "/actionGroup.xml");
		if (file.exists() && file.canRead()) {
			this.loadGroupData(file);
		}

		File file2 = ZomboidFileSystem.instance.getMediaFile("actiongroups/" + string);
		File[] fileArray = file2.listFiles();
		if (fileArray != null) {
			File[] fileArray2 = fileArray;
			int int1 = fileArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				File file3 = fileArray2[int2];
				if (file3.isDirectory()) {
					String string2 = file3.getPath();
					ActionState actionState = this.getOrCreate(file3.getName());
					actionState.load(string2);
				}
			}
		}
	}

	private void loadGroupData(File file) {
		Document document;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(file);
		} catch (SAXException | IOException | ParserConfigurationException error) {
			DebugLog.ActionSystem.error("Error loading: " + file.getPath());
			error.printStackTrace(DebugLog.ActionSystem);
			return;
		}

		document.getDocumentElement().normalize();
		Element element = document.getDocumentElement();
		if (!element.getNodeName().equals("actiongroup")) {
			DebugLogStream debugLogStream = DebugLog.ActionSystem;
			String string = file.getPath();
			debugLogStream.error("Error loading: " + string + ", expected root element \'<actiongroup>\', received \'<" + element.getNodeName() + ">\'");
		} else {
			Node node;
			for (node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
				if (node.getNodeName().equals("inherit") && node instanceof Element) {
					String string2 = node.getTextContent().trim();
					this.inherit(getActionGroup(string2));
				}
			}

			for (node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
				if (node instanceof Element) {
					Element element2 = (Element)node;
					String string3 = element2.getNodeName();
					byte byte1 = -1;
					switch (string3.hashCode()) {
					case 1946980603: 
						if (string3.equals("inherit")) {
							byte1 = 1;
						}

						break;
					
					case 1948342084: 
						if (string3.equals("initial")) {
							byte1 = 0;
						}

					
					}

					switch (byte1) {
					case 0: 
						this.initialState = element2.getTextContent().trim();
					
					case 1: 
						break;
					
					default: 
						DebugLog.ActionSystem.warn("Warning: Unknown element \'<>\' in \'" + file.getPath() + "\'");
					
					}
				}
			}
		}
	}

	private void inherit(ActionGroup actionGroup) {
		if (actionGroup != null) {
			if (actionGroup.initialState != null) {
				this.initialState = actionGroup.initialState;
			}

			Iterator iterator = actionGroup.states.iterator();
			while (iterator.hasNext()) {
				ActionState actionState = (ActionState)iterator.next();
				ActionState actionState2 = this.getOrCreate(actionState.name);
				Iterator iterator2 = actionState.transitions.iterator();
				while (iterator2.hasNext()) {
					ActionTransition actionTransition = (ActionTransition)iterator2.next();
					actionState2.transitions.add(actionTransition.clone());
					actionState2.sortTransitions();
				}
			}
		}
	}

	private void rebuildLookup() {
		HashMap hashMap = new HashMap();
		Iterator iterator = this.states.iterator();
		while (iterator.hasNext()) {
			ActionState actionState = (ActionState)iterator.next();
			hashMap.put(actionState.name.toLowerCase(), actionState);
		}

		this.stateLookup = hashMap;
	}

	public void addState(ActionState actionState) {
		this.states.add(actionState);
		this.stateLookup = null;
	}

	public ActionState get(String string) {
		if (this.stateLookup == null) {
			this.rebuildLookup();
		}

		return (ActionState)this.stateLookup.get(string.toLowerCase());
	}

	ActionState getOrCreate(String string) {
		if (this.stateLookup == null) {
			this.rebuildLookup();
		}

		string = string.toLowerCase();
		ActionState actionState = (ActionState)this.stateLookup.get(string);
		if (actionState == null) {
			actionState = new ActionState(string);
			this.states.add(actionState);
			this.stateLookup.put(string, actionState);
		}

		return actionState;
	}

	public ActionState getInitialState() {
		ActionState actionState = null;
		if (this.initialState != null) {
			actionState = this.get(this.initialState);
		}

		if (actionState == null && this.states.size() > 0) {
			actionState = (ActionState)this.states.get(0);
		}

		return actionState;
	}

	public ActionState getDefaultState() {
		return this.getInitialState();
	}
}
