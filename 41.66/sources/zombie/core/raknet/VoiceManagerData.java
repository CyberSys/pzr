package zombie.core.raknet;

import java.util.ArrayList;
import zombie.radio.devices.DeviceData;


public class VoiceManagerData {
	public static ArrayList data = new ArrayList();
	public long userplaychannel = 0L;
	public long userplaysound = 0L;
	public boolean userplaymute = false;
	public long voicetimeout = 0L;
	public final ArrayList radioData = new ArrayList();
	public boolean isCanHearAll;
	short index;

	public VoiceManagerData(short short1) {
		this.index = short1;
	}

	public static VoiceManagerData get(short short1) {
		if (data.size() <= short1) {
			for (short short2 = (short)data.size(); short2 <= short1; ++short2) {
				VoiceManagerData voiceManagerData = new VoiceManagerData(short2);
				data.add(voiceManagerData);
			}
		}

		VoiceManagerData voiceManagerData2 = (VoiceManagerData)data.get(short1);
		if (voiceManagerData2 == null) {
			voiceManagerData2 = new VoiceManagerData(short1);
			data.set(short1, voiceManagerData2);
		}

		return voiceManagerData2;
	}

	public static class RadioData {
		DeviceData deviceData;
		public int freq;
		public float distance;
		public short x;
		public short y;
		float lastReceiveDistance;

		public RadioData(float float1, float float2, float float3) {
			this((DeviceData)null, 0, float1, float2, float3);
		}

		public RadioData(int int1, float float1, float float2, float float3) {
			this((DeviceData)null, int1, float1, float2, float3);
		}

		public RadioData(DeviceData deviceData, float float1, float float2) {
			this(deviceData, deviceData.getChannel(), deviceData.getMicIsMuted() ? 0.0F : (float)deviceData.getTransmitRange(), float1, float2);
		}

		private RadioData(DeviceData deviceData, int int1, float float1, float float2, float float3) {
			this.deviceData = deviceData;
			this.freq = int1;
			this.distance = float1;
			this.x = (short)((int)float2);
			this.y = (short)((int)float3);
		}

		public boolean isTransmissionAvailable() {
			return this.freq != 0 && this.deviceData.getIsTurnedOn() && this.deviceData.getIsTwoWay() && !this.deviceData.isNoTransmit() && !this.deviceData.getMicIsMuted();
		}

		public boolean isReceivingAvailable(int int1) {
			return this.freq != 0 && this.deviceData.getIsTurnedOn() && this.deviceData.getChannel() == int1 && this.deviceData.getDeviceVolume() != 0.0F && !this.deviceData.isPlayingMedia();
		}

		public DeviceData getDeviceData() {
			return this.deviceData;
		}
	}

	public static enum VoiceDataSource {

		Unknown,
		Voice,
		Radio,
		Cheat;

		private static VoiceManagerData.VoiceDataSource[] $values() {
			return new VoiceManagerData.VoiceDataSource[]{Unknown, Voice, Radio, Cheat};
		}
	}
}
