package zombie.radio.devices;

import zombie.iso.IsoGridSquare;


public interface WaveSignalDevice {

	DeviceData getDeviceData();

	void setDeviceData(DeviceData deviceData);

	float getDelta();

	void setDelta(float float1);

	IsoGridSquare getSquare();

	float getX();

	float getY();

	float getZ();

	void AddDeviceText(String string, float float1, float float2, float float3, String string2, int int1);

	boolean HasPlayerInRange();
}
