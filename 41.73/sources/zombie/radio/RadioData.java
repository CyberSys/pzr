package zombie.radio;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.Language;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.radio.scripting.RadioBroadCast;
import zombie.radio.scripting.RadioChannel;
import zombie.radio.scripting.RadioLine;
import zombie.radio.scripting.RadioScript;


public final class RadioData {
	static boolean PRINTDEBUG = false;
	private boolean isVanilla = false;
	private String GUID;
	private int version;
	private String xmlFilePath;
	private final ArrayList radioChannels = new ArrayList();
	private final ArrayList translationDataList = new ArrayList();
	private RadioTranslationData currentTranslation;
	private Node rootNode;
	private final Map advertQue = new HashMap();
	private static final String fieldStart = "\\$\\{t:";
	private static final String fieldEnd = "\\}";
	private static final String regex = "\\$\\{t:([^}]+)\\}";
	private static final Pattern pattern = Pattern.compile("\\$\\{t:([^}]+)\\}");

	public RadioData(String string) {
		this.xmlFilePath = string;
	}

	public ArrayList getRadioChannels() {
		return this.radioChannels;
	}

	public boolean isVanilla() {
		return this.isVanilla;
	}

	public static ArrayList getTranslatorNames(Language language) {
		ArrayList arrayList = new ArrayList();
		if (language != Translator.getDefaultLanguage()) {
			ArrayList arrayList2 = fetchRadioData(false);
			Iterator iterator = arrayList2.iterator();
			label36: while (iterator.hasNext()) {
				RadioData radioData = (RadioData)iterator.next();
				Iterator iterator2 = radioData.translationDataList.iterator();
				while (true) {
					RadioTranslationData radioTranslationData;
					do {
						if (!iterator2.hasNext()) {
							continue label36;
						}

						radioTranslationData = (RadioTranslationData)iterator2.next();
					}			 while (radioTranslationData.getLanguageEnum() != language);

					Iterator iterator3 = radioTranslationData.getTranslators().iterator();
					while (iterator3.hasNext()) {
						String string = (String)iterator3.next();
						if (!arrayList.contains(string)) {
							arrayList.add(string);
						}
					}
				}
			}
		}

		return arrayList;
	}

	private static ArrayList fetchRadioData(boolean boolean1) {
		return fetchRadioData(boolean1, DebugLog.isEnabled(DebugType.Radio));
	}

	private static ArrayList fetchRadioData(boolean boolean1, boolean boolean2) {
		ArrayList arrayList = new ArrayList();
		try {
			ArrayList arrayList2 = ZomboidFileSystem.instance.getModIDs();
			if (boolean2) {
				System.out.println(":: Searching for radio data files:");
			}

			ArrayList arrayList3 = new ArrayList();
			searchForFiles(ZomboidFileSystem.instance.getMediaFile("radio"), "xml", arrayList3);
			ArrayList arrayList4 = new ArrayList(arrayList3);
			int int1;
			String string;
			if (boolean1) {
				for (int1 = 0; int1 < arrayList2.size(); ++int1) {
					string = ZomboidFileSystem.instance.getModDir((String)arrayList2.get(int1));
					if (string != null) {
						searchForFiles(new File(string + File.separator + "media" + File.separator + "radio"), "xml", arrayList3);
					}
				}
			}

			Iterator iterator = arrayList3.iterator();
			while (true) {
				Iterator iterator2;
				while (iterator.hasNext()) {
					string = (String)iterator.next();
					RadioData radioData = ReadFile(string);
					if (radioData != null) {
						if (boolean2) {
							System.out.println(" Found file: " + string);
						}

						iterator2 = arrayList4.iterator();
						while (iterator2.hasNext()) {
							String string2 = (String)iterator2.next();
							if (string2.equals(string)) {
								radioData.isVanilla = true;
							}
						}

						arrayList.add(radioData);
					} else {
						System.out.println("[Failure] Cannot parse file: " + string);
					}
				}

				if (boolean2) {
					System.out.println(":: Searching for translation files:");
				}

				arrayList3.clear();
				searchForFiles(ZomboidFileSystem.instance.getMediaFile("radio"), "txt", arrayList3);
				if (boolean1) {
					for (int1 = 0; int1 < arrayList2.size(); ++int1) {
						string = ZomboidFileSystem.instance.getModDir((String)arrayList2.get(int1));
						if (string != null) {
							searchForFiles(new File(string + File.separator + "media" + File.separator + "radio"), "txt", arrayList3);
						}
					}
				}

				iterator = arrayList3.iterator();
				while (true) {
					while (iterator.hasNext()) {
						string = (String)iterator.next();
						RadioTranslationData radioTranslationData = RadioTranslationData.ReadFile(string);
						if (radioTranslationData != null) {
							if (boolean2) {
								System.out.println(" Found file: " + string);
							}

							iterator2 = arrayList.iterator();
							while (iterator2.hasNext()) {
								RadioData radioData2 = (RadioData)iterator2.next();
								if (radioData2.GUID.equals(radioTranslationData.getGuid())) {
									if (boolean2) {
										System.out.println(" Adding translation: " + radioData2.GUID);
									}

									radioData2.translationDataList.add(radioTranslationData);
								}
							}
						} else if (boolean2) {
							System.out.println("[Failure] " + string);
						}
					}

					return arrayList;
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return arrayList;
		}
	}

	public static ArrayList fetchAllRadioData() {
		boolean boolean1 = DebugLog.isEnabled(DebugType.Radio);
		ArrayList arrayList = fetchRadioData(true);
		for (int int1 = arrayList.size() - 1; int1 >= 0; --int1) {
			RadioData radioData = (RadioData)arrayList.get(int1);
			if (radioData.loadRadioScripts()) {
				if (boolean1) {
					String string = radioData.isVanilla ? " (vanilla)" : "";
					DebugLog.Radio.println(" Adding" + string + " file: " + radioData.xmlFilePath);
					DebugLog.Radio.println(" - GUID: " + radioData.GUID);
				}

				radioData.currentTranslation = null;
				radioData.translationDataList.clear();
			} else {
				DebugLog.Radio.println("[Failure] Failed to load radio scripts for GUID: " + radioData.GUID);
				DebugLog.Radio.println("		  File: " + radioData.xmlFilePath);
				arrayList.remove(int1);
			}
		}

		return arrayList;
	}

	private static void searchForFiles(File file, String string, ArrayList arrayList) {
		if (file.isDirectory()) {
			String[] stringArray = file.list();
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				String string2 = file.getAbsolutePath();
				searchForFiles(new File(string2 + File.separator + stringArray[int1]), string, arrayList);
			}
		} else if (file.getAbsolutePath().toLowerCase().contains(string)) {
			arrayList.add(file.getAbsolutePath());
		}
	}

	private static RadioData ReadFile(String string) {
		RadioData radioData = new RadioData(string);
		boolean boolean1 = false;
		try {
			if (DebugLog.isEnabled(DebugType.Radio)) {
				DebugLog.Radio.println("Reading xml: " + string);
			}

			File file = new File(string);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			document.getDocumentElement().normalize();
			NodeList nodeList = document.getElementsByTagName("RadioData");
			if (DebugLog.isEnabled(DebugType.Radio)) {
				DebugLog.Radio.println("RadioData nodes len: " + nodeList.getLength());
			}

			if (nodeList.getLength() > 0) {
				radioData.rootNode = nodeList.item(0);
				boolean1 = radioData.loadRootInfo();
				if (DebugLog.isEnabled(DebugType.Radio)) {
					DebugLog.Radio.println("valid file: " + boolean1);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return boolean1 ? radioData : null;
	}

	private void print(String string) {
		if (PRINTDEBUG) {
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

	private String toLowerLocaleSafe(String string) {
		return string.toLowerCase(Locale.ENGLISH);
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
			string2 = this.toLowerLocaleSafe(string2);
		}

		return string2;
	}

	private boolean loadRootInfo() {
		boolean boolean1 = DebugLog.isEnabled(DebugType.Radio);
		if (boolean1) {
			DebugLog.Radio.println("Reading RootInfo...");
		}

		Iterator iterator = this.getChildNodes(this.rootNode).iterator();
		while (true) {
			Node node;
			do {
				if (!iterator.hasNext()) {
					return this.GUID != null && this.version >= 0;
				}

				node = (Node)iterator.next();
			}	 while (!this.nodeNameIs(node, "RootInfo"));

			if (boolean1) {
				DebugLog.Radio.println("RootInfo found");
			}

			Iterator iterator2 = this.getChildNodes(node).iterator();
			while (iterator2.hasNext()) {
				Node node2 = (Node)iterator2.next();
				String string = node2.getNodeName();
				String string2 = node2.getTextContent();
				if (string != null && string2 != null) {
					string = string.trim();
					if (boolean1) {
						DebugLog.Radio.println("Found element: " + string);
					}

					if (string.equals("Version")) {
						if (boolean1) {
							DebugLog.Radio.println("Version = " + this.version);
						}

						this.version = Integer.parseInt(string2);
					} else if (string.equals("FileGUID")) {
						if (boolean1) {
							DebugLog.Radio.println("GUID = " + string2);
						}

						this.GUID = string2;
					}
				}
			}
		}
	}

	private boolean loadRadioScripts() {
		boolean boolean1 = false;
		this.currentTranslation = null;
		this.advertQue.clear();
		Iterator iterator;
		if (Core.getInstance().getContentTranslationsEnabled() && Translator.getLanguage() != Translator.getDefaultLanguage()) {
			System.out.println("Attempting to load translation: " + Translator.getLanguage().toString());
			iterator = this.translationDataList.iterator();
			while (iterator.hasNext()) {
				RadioTranslationData radioTranslationData = (RadioTranslationData)iterator.next();
				if (radioTranslationData.getLanguageEnum() == Translator.getLanguage()) {
					System.out.println("Translation found!");
					if (radioTranslationData.loadTranslations()) {
						this.currentTranslation = radioTranslationData;
						System.out.println("Count = " + this.currentTranslation.getTranslationCount());
					} else {
						System.out.println("Error loading translations for " + this.GUID);
					}
				}
			}
		} else if (!Core.getInstance().getContentTranslationsEnabled()) {
			System.out.println("NOTE: Community Content Translations are disabled.");
		}

		iterator = this.getChildNodes(this.rootNode).iterator();
		Node node;
		while (iterator.hasNext()) {
			node = (Node)iterator.next();
			if (this.nodeNameIs(node, "Adverts")) {
				this.loadAdverts(node);
			}
		}

		iterator = this.getChildNodes(this.rootNode).iterator();
		while (iterator.hasNext()) {
			node = (Node)iterator.next();
			if (this.nodeNameIs(node, "Channels")) {
				this.loadChannels(node);
				boolean1 = true;
			}
		}

		return boolean1;
	}

	private void loadAdverts(Node node) {
		this.print(">>> Loading adverts...");
		ArrayList arrayList = new ArrayList();
		arrayList = this.loadScripts(node, arrayList, true);
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			RadioScript radioScript = (RadioScript)iterator.next();
			if (!this.advertQue.containsKey(radioScript.GetName())) {
				this.advertQue.put(radioScript.GetGUID(), radioScript);
			}
		}
	}

	private void loadChannels(Node node) {
		this.print(">>> Loading channels...");
		ArrayList arrayList = new ArrayList();
		Iterator iterator = this.getChildNodes(node).iterator();
		while (true) {
			Node node2;
			do {
				if (!iterator.hasNext()) {
					return;
				}

				node2 = (Node)iterator.next();
			}	 while (!this.nodeNameIs(node2, "ChannelEntry"));

			String string = this.getAttrib(node2, "ID");
			String string2 = this.getAttrib(node2, "name");
			String string3 = this.getAttrib(node2, "cat");
			String string4 = this.getAttrib(node2, "freq");
			String string5 = this.getAttrib(node2, "startscript");
			this.print(" -> Found channel: " + string2 + ", on freq: " + string4 + " , category: " + string3 + ", startscript: " + string5 + ", ID: " + string);
			RadioChannel radioChannel = new RadioChannel(string2, Integer.parseInt(string4), ChannelCategory.valueOf(string3), string);
			arrayList.clear();
			arrayList = this.loadScripts(node2, arrayList, false);
			Iterator iterator2 = arrayList.iterator();
			while (iterator2.hasNext()) {
				RadioScript radioScript = (RadioScript)iterator2.next();
				radioChannel.AddRadioScript(radioScript);
			}

			radioChannel.setActiveScript(string5, 0);
			this.radioChannels.add(radioChannel);
			radioChannel.setRadioData(this);
		}
	}

	private ArrayList loadScripts(Node node, ArrayList arrayList, boolean boolean1) {
		this.print(" --> Loading scripts...");
		Iterator iterator = this.getChildNodes(node).iterator();
		while (true) {
			Node node2;
			do {
				if (!iterator.hasNext()) {
					return arrayList;
				}

				node2 = (Node)iterator.next();
			}	 while (!this.nodeNameIs(node2, "ScriptEntry"));

			String string = this.getAttrib(node2, "ID");
			String string2 = this.getAttrib(node2, "name");
			String string3 = this.getAttrib(node2, "loopmin");
			String string4 = this.getAttrib(node2, "loopmax");
			this.print(" ---> Found script: " + string2);
			RadioScript radioScript = new RadioScript(string2, Integer.parseInt(string3), Integer.parseInt(string4), string);
			Iterator iterator2 = this.getChildNodes(node2).iterator();
			while (iterator2.hasNext()) {
				Node node3 = (Node)iterator2.next();
				if (this.nodeNameIs(node3, "BroadcastEntry")) {
					this.loadBroadcast(node3, radioScript);
				} else if (!boolean1 && this.nodeNameIs(node3, "ExitOptions")) {
					this.loadExitOptions(node3, radioScript);
				}
			}

			arrayList.add(radioScript);
		}
	}

	private RadioBroadCast loadBroadcast(Node node, RadioScript radioScript) {
		String string = this.getAttrib(node, "ID");
		String string2 = this.getAttrib(node, "timestamp");
		String string3 = this.getAttrib(node, "endstamp");
		this.print(" ----> BroadCast, Timestamp: " + string2 + ", endstamp: " + string3);
		int int1 = Integer.parseInt(string2);
		int int2 = Integer.parseInt(string3);
		String string4 = this.getAttrib(node, "isSegment");
		boolean boolean1 = this.toLowerLocaleSafe(string4).equals("true");
		String string5 = this.getAttrib(node, "advertCat");
		RadioBroadCast radioBroadCast = new RadioBroadCast(string, int1, int2);
		if (!boolean1 && !this.toLowerLocaleSafe(string5).equals("none") && this.advertQue.containsKey(string5) && Rand.Next(101) < 75) {
			RadioScript radioScript2 = (RadioScript)this.advertQue.get(string5);
			if (radioScript2.getBroadcastList().size() > 0) {
				if (Rand.Next(101) < 50) {
					radioBroadCast.setPreSegment((RadioBroadCast)radioScript2.getBroadcastList().get(Rand.Next(radioScript2.getBroadcastList().size())));
				} else {
					radioBroadCast.setPostSegment((RadioBroadCast)radioScript2.getBroadcastList().get(Rand.Next(radioScript2.getBroadcastList().size())));
				}
			}
		}

		Iterator iterator = this.getChildNodes(node).iterator();
		while (iterator.hasNext()) {
			Node node2 = (Node)iterator.next();
			if (this.nodeNameIs(node2, "LineEntry")) {
				String string6 = this.getAttrib(node2, "ID");
				String string7 = this.getAttrib(node2, "r");
				String string8 = this.getAttrib(node2, "g");
				String string9 = this.getAttrib(node2, "b");
				String string10 = null;
				if (node2.getAttributes().getNamedItem("codes") != null) {
					string10 = this.getAttrib(node2, "codes");
				}

				String string11 = node2.getTextContent();
				this.print(" -----> New Line, Color: " + string7 + ", " + string8 + ", " + string9);
				string11 = this.checkForTranslation(string6, string11);
				RadioLine radioLine = new RadioLine(string11, Float.parseFloat(string7) / 255.0F, Float.parseFloat(string8) / 255.0F, Float.parseFloat(string9) / 255.0F, string10);
				radioBroadCast.AddRadioLine(radioLine);
				string11 = string11.trim();
				if (string11.toLowerCase().startsWith("${t:")) {
					string11 = this.checkForCustomAirTimer(string11, radioLine);
					radioLine.setText(string11);
				}
			}
		}

		if (radioScript != null) {
			radioScript.AddBroadcast(radioBroadCast, boolean1);
		}

		return radioBroadCast;
	}

	private String checkForTranslation(String string, String string2) {
		if (this.currentTranslation != null) {
			String string3 = this.currentTranslation.getTranslation(string);
			if (string3 != null) {
				return string3;
			}

			DebugLog.log(DebugType.Radio, "no translation for: " + string);
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

	private String checkForCustomAirTimer(String string, RadioLine radioLine) {
		Matcher matcher = pattern.matcher(string);
		String string2 = string;
		float float1 = -1.0F;
		if (matcher.find()) {
			String string3 = matcher.group(1).toLowerCase().trim();
			try {
				float1 = Float.parseFloat(string3);
				radioLine.setAirTime(float1);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			string2 = string.replaceFirst("\\$\\{t:([^}]+)\\}", "");
		}

		return float1 >= 0.0F ? "[cdt=" + float1 + "]" + string2.trim() : string2.trim();
	}
}
