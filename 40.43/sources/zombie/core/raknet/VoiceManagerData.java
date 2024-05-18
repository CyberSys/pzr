package zombie.core.raknet;

import java.util.ArrayList;


public class VoiceManagerData {
	public static ArrayList data = new ArrayList();
	public long userplaychannel = 0L;
	public long userplaysound = 0L;
	public boolean userplaymute = false;
	public long voicetimeout = 0L;
	int index;

	public VoiceManagerData(int int1) {
		this.index = int1;
	}

	public static VoiceManagerData get(int int1) {
		if (data.size() <= int1) {
			for (int int2 = data.size(); int2 <= int1; ++int2) {
				VoiceManagerData voiceManagerData = new VoiceManagerData(int2);
				data.add(voiceManagerData);
			}
		}

		VoiceManagerData voiceManagerData2 = (VoiceManagerData)data.get(int1);
		if (voiceManagerData2 == null) {
			voiceManagerData2 = new VoiceManagerData(int1);
			data.set(int1, voiceManagerData2);
		}

		return voiceManagerData2;
	}
}
