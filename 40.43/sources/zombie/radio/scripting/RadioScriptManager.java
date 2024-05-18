package zombie.radio.scripting;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import zombie.GameWindow;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.radio.ZomboidRadio;


public class RadioScriptManager {
	private Map channels = new LinkedHashMap();
	private static RadioScriptManager instance;
	private int currentTimeStamp = 0;

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

	public void Save(FileWriter fileWriter) throws IOException {
		Iterator iterator = this.channels.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			fileWriter.write(entry.getKey() + "," + ((RadioChannel)entry.getValue()).getCurrentScriptLoop() + "," + ((RadioChannel)entry.getValue()).getCurrentScriptMaxLoops());
			RadioScript radioScript = ((RadioChannel)entry.getValue()).getCurrentScript();
			if (radioScript != null) {
				fileWriter.write("," + radioScript.GetName() + "," + radioScript.getStartDay());
			}

			RadioBroadCast radioBroadCast = ((RadioChannel)entry.getValue()).getAiringBroadcast();
			if (radioBroadCast != null) {
				fileWriter.write("," + radioBroadCast.getID());
			} else if (((RadioChannel)entry.getValue()).getLastBroadcastID() != null) {
				fileWriter.write("," + ((RadioChannel)entry.getValue()).getLastBroadcastID());
			} else {
				fileWriter.write(",none");
			}

			fileWriter.write("," + (radioBroadCast != null ? radioBroadCast.getCurrentLineNumber() + "" : "-1"));
			fileWriter.write(System.lineSeparator());
		}
	}

	public void SaveOLD(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.writeInt(this.channels.size());
		Iterator iterator = this.channels.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			dataOutputStream.writeInt((Integer)entry.getKey());
			dataOutputStream.writeInt(((RadioChannel)entry.getValue()).getCurrentScriptLoop());
			dataOutputStream.writeInt(((RadioChannel)entry.getValue()).getCurrentScriptMaxLoops());
			RadioScript radioScript = ((RadioChannel)entry.getValue()).getCurrentScript();
			dataOutputStream.writeByte(radioScript != null ? 1 : 0);
			if (radioScript != null) {
				GameWindow.WriteString(dataOutputStream, radioScript.GetName());
				dataOutputStream.writeInt(radioScript.getStartDay());
			}
		}
	}

	public void Load(BufferedReader bufferedReader) throws IOException {
		int int1 = 1;
		int int2 = 1;
		String string;
		while ((string = bufferedReader.readLine()) != null) {
			RadioChannel radioChannel = null;
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

	public void LoadOLD(DataInputStream dataInputStream) throws IOException {
		Iterator iterator = this.channels.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			((RadioChannel)entry.getValue()).setActiveScriptNull();
		}

		int int1 = dataInputStream.readInt();
		for (int int2 = 0; int2 < int1; ++int2) {
			RadioChannel radioChannel = null;
			int int3 = dataInputStream.readInt();
			if (this.channels.containsKey(int3)) {
				radioChannel = (RadioChannel)this.channels.get(int3);
				radioChannel.setTimeSynced(true);
			}

			int int4 = dataInputStream.readInt();
			int int5 = dataInputStream.readInt();
			boolean boolean1 = dataInputStream.readByte() == 1;
			if (boolean1) {
				String string = GameWindow.ReadString(dataInputStream);
				int int6 = dataInputStream.readInt();
				if (radioChannel != null) {
					radioChannel.setActiveScript(string, int6, int4, int5);
				}
			}
		}
	}
}
