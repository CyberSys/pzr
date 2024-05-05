package zombie.radio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.radio.globals.RadioGlobal;
import zombie.radio.globals.RadioGlobalBool;
import zombie.radio.globals.RadioGlobalFloat;
import zombie.radio.globals.RadioGlobalInt;
import zombie.radio.globals.RadioGlobalString;
import zombie.radio.globals.RadioGlobalType;
import zombie.radio.globals.RadioGlobalsManager;
import zombie.radio.scripting.RadioBroadCast;
import zombie.radio.scripting.RadioChannel;
import zombie.radio.scripting.RadioLine;
import zombie.radio.scripting.RadioScript;
import zombie.radio.scripting.RadioScriptManager;


public final class RadioXmlReader {
	private boolean printDebug;
	private ArrayList globalQueue;
	private ArrayList channelQueue;
	private Map advertQue;
	private final String charsNormal;
	private final String charsEncrypt;
	private String radioVersion;
	private float version;
	private float formatVersion;
	private final Map radioFileSettings;

	public RadioXmlReader() {
		this(false);
	}

	public RadioXmlReader(boolean boolean1) {
		this.printDebug = false;
		this.charsNormal = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		this.charsEncrypt = "UVWKLMABCDEFGXYZHIJOPQRSTNuvwklmabcdefgxyzhijopqrstn";
		this.radioVersion = "1.0";
		this.version = 1.0F;
		this.formatVersion = 1.0F;
		this.radioFileSettings = new HashMap();
		this.printDebug = boolean1;
	}

	public static RadioData ReadFileHeader(String string) {
		new RadioXmlReader(ZomboidRadio.DEBUG_XML);
		return null;
	}

	private void readfileheader(String string) throws ParserConfigurationException, IOException, SAXException {
		File file = new File(string);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(file);
		document.getDocumentElement().normalize();
		NodeList nodeList = document.getElementsByTagName("RadioData");
		if (nodeList.getLength() > 0) {
			Node node = nodeList.item(0);
			Node node2 = null;
			Iterator iterator = this.getChildNodes(node).iterator();
			while (iterator.hasNext()) {
				Node node3 = (Node)iterator.next();
				if (this.nodeNameIs(node3, "RootInfo")) {
					node2 = node3;
					break;
				}
			}

			this.loadRootInfo(node2);
		}
	}

	public static boolean LoadFile(String string) {
		RadioXmlReader radioXmlReader = new RadioXmlReader(ZomboidRadio.DEBUG_XML);
		try {
			radioXmlReader.start(string);
		} catch (Exception exception) {
			DebugLog.log(DebugType.Radio, "Error loading radio system: " + exception.getMessage());
			exception.printStackTrace();
			boolean boolean1 = false;
		} finally {
			DebugLog.log(DebugType.Radio, "RadioSystem online.");
			return true;
		}
	}

	public static ArrayList LoadTranslatorNames(String string) {
		ArrayList arrayList = new ArrayList();
		RadioXmlReader radioXmlReader = new RadioXmlReader(ZomboidRadio.DEBUG_XML);
		try {
			ArrayList arrayList2 = radioXmlReader.readTranslatorNames(string);
			arrayList = arrayList2;
		} catch (Exception exception) {
			DebugLog.log(DebugType.Radio, "Error reading translator names: " + exception.getMessage());
			exception.printStackTrace();
		} finally {
			DebugLog.log(DebugType.Radio, "Returning translator names.");
			return arrayList;
		}
	}

	private void print(String string) {
		if (this.printDebug) {
			DebugLog.log(DebugType.Radio, string);
		}
	}

	private ArrayList getChildNodes(Node node) {
		ArrayList arrayList = new ArrayList();
		if (node.hasChildNodes()) {
			Node node2 = node.getFirstChild();
			while (node2 != null) {
				if (!(node2 instanceof Element)) {
					node2 = node2.getNextSibling();
				} else {
					arrayList.add(node2);
					node2 = node2.getNextSibling();
				}
			}
		}

		return arrayList;
	}

	private boolean nodeNameIs(Node node, String string) {
		return node.getNodeName().equals(string);
	}

	private String getAttrib(Node node, String string, boolean boolean1) {
		return this.getAttrib(node, string, boolean1, false);
	}

	private String getAttrib(Node node, String string) {
		return this.getAttrib(node, string, true, false).trim();
	}

	private String getAttrib(Node node, String string, boolean boolean1, boolean boolean2) {
		String string2 = node.getAttributes().getNamedItem(string).getTextContent();
		if (boolean1) {
			string2 = string2.trim();
		}

		if (boolean2) {
			string2 = string2.toLowerCase();
		}

		return string2;
	}

	private RadioGlobal getGlobalFromQueue(String string) {
		Iterator iterator = this.globalQueue.iterator();
		RadioGlobal radioGlobal;
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			radioGlobal = (RadioGlobal)iterator.next();
		} while (radioGlobal == null || !radioGlobal.getName().equals(string));

		return radioGlobal;
	}

	private RadioGlobal createGlobal(String string, String string2) {
		return this.createGlobal("", string, string2);
	}

	private RadioGlobal createGlobal(String string, String string2, String string3) {
		if (string != null && string2 != null && string3 != null) {
			RadioGlobalType radioGlobalType = RadioGlobalType.valueOf(string2.trim());
			switch (radioGlobalType) {
			case String: 
				return new RadioGlobalString(string, string3);
			
			case Integer: 
				return new RadioGlobalInt(string, Integer.parseInt(string3.trim()));
			
			case Float: 
				return new RadioGlobalFloat(string, Float.parseFloat(string3.trim()));
			
			case Boolean: 
				return new RadioGlobalBool(string, Boolean.parseBoolean(string3.trim().toLowerCase()));
			
			default: 
				return null;
			
			}
		} else {
			return null;
		}
	}

	private ArrayList readTranslatorNames(String string) throws ParserConfigurationException, IOException, SAXException {
		File file = new File(string);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(file);
		document.getDocumentElement().normalize();
		ArrayList arrayList = new ArrayList();
		NodeList nodeList = document.getElementsByTagName("TranslationData");
		if (nodeList.getLength() > 0) {
			Node node = nodeList.item(0);
			Iterator iterator = this.getChildNodes(node).iterator();
			while (iterator.hasNext()) {
				Node node2 = (Node)iterator.next();
				if (this.nodeNameIs(node2, "RootInfo")) {
					Iterator iterator2 = this.getChildNodes(node2).iterator();
					Node node3;
					do {
						if (!iterator2.hasNext()) {
							return arrayList;
						}

						node3 = (Node)iterator2.next();
					}			 while (!this.nodeNameIs(node3, "Translators"));

					Iterator iterator3 = this.getChildNodes(node3).iterator();
					while (iterator3.hasNext()) {
						Node node4 = (Node)iterator3.next();
						String string2 = this.getAttrib(node4, "name", true, false);
						if (string2 != null) {
							arrayList.add(string2);
						}
					}

					return arrayList;
				}
			}
		}

		return arrayList;
	}

	private void start(String string) throws ParserConfigurationException, IOException, SAXException {
		File file = new File(string);
		this.print("RadioDataFile: " + file.getAbsolutePath());
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(file);
		document.getDocumentElement().normalize();
		this.globalQueue = new ArrayList();
		this.channelQueue = new ArrayList();
		this.advertQue = new HashMap();
		NodeList nodeList = document.getElementsByTagName("RadioData");
		Iterator iterator;
		if (nodeList.getLength() > 0) {
			Node node = nodeList.item(0);
			Node node2 = null;
			iterator = this.getChildNodes(node).iterator();
			Node node3;
			while (iterator.hasNext()) {
				node3 = (Node)iterator.next();
				if (this.nodeNameIs(node3, "RootInfo")) {
					node2 = node3;
					break;
				}
			}

			this.loadRootInfo(node2);
			iterator = this.getChildNodes(node).iterator();
			while (iterator.hasNext()) {
				node3 = (Node)iterator.next();
				if (this.nodeNameIs(node3, "Globals")) {
					this.loadGlobals(node3);
				} else if (this.nodeNameIs(node3, "Adverts")) {
					this.loadAdverts(node3);
				} else if (this.nodeNameIs(node3, "Channels")) {
					this.loadChannels(node3);
				}
			}
		}

		RadioGlobalsManager radioGlobalsManager = RadioGlobalsManager.getInstance();
		Iterator iterator2 = this.globalQueue.iterator();
		while (iterator2.hasNext()) {
			RadioGlobal radioGlobal = (RadioGlobal)iterator2.next();
			radioGlobalsManager.addGlobal(radioGlobal.getName(), radioGlobal);
		}

		RadioScriptManager radioScriptManager = RadioScriptManager.getInstance();
		iterator = this.channelQueue.iterator();
		while (iterator.hasNext()) {
			RadioChannel radioChannel = (RadioChannel)iterator.next();
			radioScriptManager.AddChannel(radioChannel, false);
		}
	}

	private void loadRootInfo(Node node) {
		this.print(">>> Loading root info...");
		if (node == null) {
			this.print(" -> root info not found, default version = " + this.radioVersion);
			this.radioFileSettings.put("Version", this.radioVersion);
		} else {
			this.print(" -> Reading RootInfo");
			Iterator iterator = this.getChildNodes(node).iterator();
			while (iterator.hasNext()) {
				Node node2 = (Node)iterator.next();
				String string = node2.getNodeName();
				String string2 = node2.getTextContent();
				if (string != null && string2 != null) {
					this.print("   -> " + string + " = " + string2);
					this.radioFileSettings.put(string, string2);
					if (string.equals("Version")) {
						this.radioVersion = string2;
						this.version = Float.parseFloat(this.radioVersion);
					}
				}
			}
		}
	}

	private void loadGlobals(Node node) {
		this.print(">>> Loading globals...");
		Iterator iterator = this.getChildNodes(node).iterator();
		while (iterator.hasNext()) {
			Node node2 = (Node)iterator.next();
			if (this.nodeNameIs(node2, "GlobalEntry")) {
				String string = this.getAttrib(node2, "name");
				String string2 = this.getAttrib(node2, "type");
				String string3 = node2.getTextContent();
				this.print(" -> Found global, name: " + string + ", type: " + string2 + ", value: " + string3);
				RadioGlobal radioGlobal = this.createGlobal(string, string2, string3);
				if (radioGlobal != null) {
					this.globalQueue.add(radioGlobal);
				} else {
					this.print(" -> Error adding Global, name: " + string + ", type: " + string2 + ", value: " + string3);
				}
			}
		}
	}

	private void loadAdverts(Node node) {
		this.print(">>> Loading adverts...");
		Iterator iterator = this.getChildNodes(node).iterator();
		while (true) {
			Node node2;
			do {
				if (!iterator.hasNext()) {
					return;
				}

				node2 = (Node)iterator.next();
			}	 while (!this.nodeNameIs(node2, "AdvertCategory"));

			String string = this.getAttrib(node2, "name");
			if (!this.advertQue.containsKey(string)) {
				this.advertQue.put(string, new ArrayList());
			}

			this.print(" -> Found category: " + string);
			Iterator iterator2 = this.getChildNodes(node2).iterator();
			while (iterator2.hasNext()) {
				Node node3 = (Node)iterator2.next();
				RadioBroadCast radioBroadCast = this.loadBroadcast(node3, (RadioScript)null);
				((ArrayList)this.advertQue.get(string)).add(radioBroadCast);
			}
		}
	}

	private void loadChannels(Node node) {
		this.print(">>> Loading channels...");
		Iterator iterator = this.getChildNodes(node).iterator();
		while (iterator.hasNext()) {
			Node node2 = (Node)iterator.next();
			if (this.nodeNameIs(node2, "ChannelEntry")) {
				String string = this.getAttrib(node2, "name");
				String string2 = this.getAttrib(node2, "cat");
				String string3 = this.getAttrib(node2, "freq");
				String string4 = this.getAttrib(node2, "startscript");
				this.print(" -> Found channel: " + string + ", on freq: " + string3 + " , category: " + string2 + ", startscript: " + string4);
				RadioChannel radioChannel = new RadioChannel(string, Integer.parseInt(string3), ChannelCategory.valueOf(string2));
				this.loadScripts(node2, radioChannel);
				radioChannel.setActiveScript(string4, 0);
				this.channelQueue.add(radioChannel);
			}
		}
	}

	private void loadScripts(Node node, RadioChannel radioChannel) {
		this.print(" --> Loading scripts...");
		Iterator iterator = this.getChildNodes(node).iterator();
		while (true) {
			Node node2;
			do {
				if (!iterator.hasNext()) {
					return;
				}

				node2 = (Node)iterator.next();
			}	 while (!this.nodeNameIs(node2, "ScriptEntry"));

			String string = this.getAttrib(node2, "name");
			String string2 = this.getAttrib(node2, "loopmin");
			String string3 = this.getAttrib(node2, "loopmin");
			this.print(" ---> Found script: " + string);
			RadioScript radioScript = new RadioScript(string, Integer.parseInt(string2), Integer.parseInt(string3));
			Iterator iterator2 = this.getChildNodes(node2).iterator();
			while (iterator2.hasNext()) {
				Node node3 = (Node)iterator2.next();
				if (this.nodeNameIs(node3, "BroadcastEntry")) {
					this.loadBroadcast(node3, radioScript);
				} else if (this.nodeNameIs(node3, "ExitOptions")) {
					this.loadExitOptions(node3, radioScript);
				}
			}

			radioChannel.AddRadioScript(radioScript);
		}
	}

	private RadioBroadCast loadBroadcast(Node node, RadioScript radioScript) {
		String string = this.getAttrib(node, "ID");
		String string2 = this.getAttrib(node, "timestamp");
		String string3 = this.getAttrib(node, "endstamp");
		this.print(" ----> BroadCast, Timestamp: " + string2 + ", endstamp: " + string3);
		int int1 = Integer.parseInt(string2);
		int int2 = Integer.parseInt(string3);
		String string4 = this.getAttrib(node, "preCat");
		int int3 = Integer.parseInt(this.getAttrib(node, "preChance"));
		String string5 = this.getAttrib(node, "postCat");
		int int4 = Integer.parseInt(this.getAttrib(node, "postChance"));
		RadioBroadCast radioBroadCast = new RadioBroadCast(string, int1, int2);
		int int5;
		int int6;
		if (!string4.equals("none") && this.advertQue.containsKey(string4)) {
			int5 = Rand.Next(101);
			int6 = ((ArrayList)this.advertQue.get(string4)).size();
			if (int6 > 0 && int5 <= int3) {
				radioBroadCast.setPreSegment((RadioBroadCast)((ArrayList)this.advertQue.get(string4)).get(Rand.Next(int6)));
			}
		}

		if (!string5.equals("none") && this.advertQue.containsKey(string5)) {
			int5 = Rand.Next(101);
			int6 = ((ArrayList)this.advertQue.get(string5)).size();
			if (int6 > 0 && int5 <= int4) {
				radioBroadCast.setPostSegment((RadioBroadCast)((ArrayList)this.advertQue.get(string5)).get(Rand.Next(int6)));
			}
		}

		Iterator iterator = this.getChildNodes(node).iterator();
		while (true) {
			Node node2;
			do {
				if (!iterator.hasNext()) {
					if (radioScript != null) {
						radioScript.AddBroadcast(radioBroadCast);
					}

					return radioBroadCast;
				}

				node2 = (Node)iterator.next();
			}	 while (!this.nodeNameIs(node2, "LineEntry"));

			String string6 = this.getAttrib(node2, "r");
			String string7 = this.getAttrib(node2, "g");
			String string8 = this.getAttrib(node2, "b");
			String string9 = null;
			String string10 = node2.getTextContent();
			this.print(" -----> New Line, Color: " + string6 + ", " + string7 + ", " + string8);
			Iterator iterator2 = this.getChildNodes(node2).iterator();
			label59: while (iterator2.hasNext()) {
				Node node3 = (Node)iterator2.next();
				if (this.nodeNameIs(node3, "LineEffects")) {
					string9 = "";
					Iterator iterator3 = this.getChildNodes(node2).iterator();
					while (true) {
						if (!iterator3.hasNext()) {
							break label59;
						}

						Node node4 = (Node)iterator3.next();
						if (this.nodeNameIs(node4, "Effect")) {
							String string11 = this.getAttrib(node4, "tag");
							String string12 = this.getAttrib(node4, "value");
							string9 = string9 + string11 + "=" + string12 + ",";
						}
					}
				}
			}

			string10 = this.simpleDecrypt(string10);
			RadioLine radioLine = new RadioLine(string10, Float.parseFloat(string6) / 255.0F, Float.parseFloat(string7) / 255.0F, Float.parseFloat(string8) / 255.0F, string9);
			radioBroadCast.AddRadioLine(radioLine);
		}
	}

	private String simpleDecrypt(String string) {
		String string2 = "";
		for (int int1 = 0; int1 < string.length(); ++int1) {
			char char1 = string.charAt(int1);
			if ("UVWKLMABCDEFGXYZHIJOPQRSTNuvwklmabcdefgxyzhijopqrstn".indexOf(char1) != -1) {
				string2 = string2 + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".charAt("UVWKLMABCDEFGXYZHIJOPQRSTNuvwklmabcdefgxyzhijopqrstn".indexOf(char1));
			} else {
				string2 = string2 + char1;
			}
		}

		return string2;
	}

	private void loadExitOptions(Node node, RadioScript radioScript) {
		Iterator iterator = this.getChildNodes(node).iterator();
		while (iterator.hasNext()) {
			Node node2 = (Node)iterator.next();
			if (this.nodeNameIs(node2, "ExitOption")) {
				String string = this.getAttrib(node2, "script");
				String string2 = this.getAttrib(node2, "chance");
				String string3 = this.getAttrib(node2, "delay");
				int int1 = Integer.parseInt(string2);
				int int2 = Integer.parseInt(string3);
				radioScript.AddExitOption(string, int1, int2);
			}
		}
	}
}
