package zombie.radio.scripting;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.radio.ZomboidRadio;


public final class RadioScriptManager {
	private final Map channels = new LinkedHashMap();
	private static RadioScriptManager instance;
	private int currentTimeStamp = 0;
	private ArrayList channelsList = new ArrayList();

	public static boolean hasInstance() {
		return instance != null;
	}

	public static RadioScriptManager getInstance() {
		if (instance == null) {
			instance = new RadioScriptManager();
		}

		return instance;
	}

	private RadioScriptManager() {
	}

	public void init(int int1) {
	}

	public Map getChannels() {
		return this.channels;
	}

	public ArrayList getChannelsList() {
		this.channelsList.clear();
		Iterator iterator = this.channels.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			this.channelsList.add((RadioChannel)entry.getValue());
		}

		return this.channelsList;
	}

	public RadioChannel getRadioChannel(String string) {
		Iterator iterator = this.channels.entrySet().iterator();
		Entry entry;
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			entry = (Entry)iterator.next();
		} while (!((RadioChannel)entry.getValue()).getGUID().equals(string));

		return (RadioChannel)entry.getValue();
	}

	public void simulateScriptsUntil(int int1, boolean boolean1) {
		Iterator iterator = this.channels.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			this.simulateChannelUntil(((RadioChannel)entry.getValue()).GetFrequency(), int1, boolean1);
		}
	}

	public void simulateChannelUntil(int int1, int int2, boolean boolean1) {
		if (this.channels.containsKey(int1)) {
			RadioChannel radioChannel = (RadioChannel)this.channels.get(int1);
			if (radioChannel.isTimeSynced() && !boolean1) {
				return;
			}

			for (int int3 = 0; int3 < int2; ++int3) {
				int int4 = int3 * 24 * 60;
				radioChannel.UpdateScripts(this.currentTimeStamp, int4);
			}

			radioChannel.setTimeSynced(true);
		}
	}

	public int getCurrentTimeStamp() {
		return this.currentTimeStamp;
	}

	public void PlayerListensChannel(int int1, boolean boolean1, boolean boolean2) {
		if (this.channels.containsKey(int1) && ((RadioChannel)this.channels.get(int1)).IsTv() == boolean2) {
			((RadioChannel)this.channels.get(int1)).SetPlayerIsListening(boolean1);
		}
	}

	public void AddChannel(RadioChannel radioChannel, boolean boolean1) {
		String string;
		if (radioChannel == null || !boolean1 && this.channels.containsKey(radioChannel.GetFrequency())) {
			string = radioChannel != null ? radioChannel.GetName() : "null";
			DebugLog.log(DebugType.Radio, "Error adding radiochannel (" + string + "), channel is null or frequency key already exists");
		} else {
			this.channels.put(radioChannel.GetFrequency(), radioChannel);
			string = radioChannel.GetCategory().name();
			ZomboidRadio.getInstance().addChannelName(radioChannel.GetName(), radioChannel.GetFrequency(), string, boolean1);
		}
	}

	public void RemoveChannel(int int1) {
		if (this.channels.containsKey(int1)) {
			this.channels.remove(int1);
			ZomboidRadio.getInstance().removeChannelName(int1);
		}
	}

	public void UpdateScripts(int int1, int int2, int int3) {
		this.currentTimeStamp = int1 * 24 * 60 + int2 * 60 + int3;
		Iterator iterator = this.channels.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			((RadioChannel)entry.getValue()).UpdateScripts(this.currentTimeStamp, int1);
		}
	}

	public void update() {
		Iterator iterator = this.channels.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			((RadioChannel)entry.getValue()).update();
		}
	}

	public void reset() {
		instance = null;
	}

	public void Save(Writer writer) throws IOException {
		Iterator iterator = this.channels.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			Object object = entry.getKey();
			writer.write(object + "," + ((RadioChannel)entry.getValue()).getCurrentScriptLoop() + "," + ((RadioChannel)entry.getValue()).getCurrentScriptMaxLoops());
			RadioScript radioScript = ((RadioChannel)entry.getValue()).getCurrentScript();
			String string;
			if (radioScript != null) {
				string = radioScript.GetName();
				writer.write("," + string + "," + radioScript.getStartDay());
			}

			RadioBroadCast radioBroadCast = ((RadioChannel)entry.getValue()).getAiringBroadcast();
			if (radioBroadCast != null) {
				writer.write("," + radioBroadCast.getID());
			} else if (((RadioChannel)entry.getValue()).getLastBroadcastID() != null) {
				writer.write("," + ((RadioChannel)entry.getValue()).getLastBroadcastID());
			} else {
				writer.write(",none");
			}

			string = radioBroadCast != null ? radioBroadCast.getCurrentLineNumber().makeConcatWithConstants < invokedynamic > (radioBroadCast.getCurrentLineNumber()) : "-1";
			writer.write("," + string);
			writer.write(System.lineSeparator());
		}
	}

	public void Load(List list) throws IOException, NumberFormatException {
		int int1 = 1;
		int int2 = 1;
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			RadioChannel radioChannel = null;
			if (string != null) {
				string = string.trim();
				String[] stringArray = string.split(",");
				if (stringArray.length >= 3) {
					int int3 = Integer.parseInt(stringArray[0]);
					int1 = Integer.parseInt(stringArray[1]);
					int2 = Integer.parseInt(stringArray[2]);
					if (this.channels.containsKey(int3)) {
						radioChannel = (RadioChannel)this.channels.get(int3);
						radioChannel.setTimeSynced(true);
					}
				}

				if (radioChannel != null && stringArray.length >= 5) {
					String string2 = stringArray[3];
					int int4 = Integer.parseInt(stringArray[4]);
					if (radioChannel != null) {
						radioChannel.setActiveScript(string2, int4, int1, int2);
					}
				}

				if (radioChannel != null && stringArray.length >= 7) {
					String string3 = stringArray[5];
					if (!string3.equals("none")) {
						int int5 = Integer.parseInt(stringArray[6]);
						radioChannel.LoadAiringBroadcast(string3, int5);
					}
				}
			}
		}
	}
}
