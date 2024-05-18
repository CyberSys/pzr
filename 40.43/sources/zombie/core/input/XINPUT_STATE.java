package zombie.core.input;

import java.io.IOException;


public final class XINPUT_STATE {
	public int wButtons;
	public int bLeftTrigger;
	public int bRightTrigger;
	public int sThumbLX;
	public int sThumbLY;
	public int sThumbRX;
	public int sThumbRY;
	public boolean bConnected;

	public void setDisconnected() {
		this.bConnected = false;
	}

	public void set(int int1, int int2, int int3, int int4, int int5, int int6, int int7) {
		this.bConnected = true;
		this.wButtons = int1;
		this.bLeftTrigger = int2;
		this.bRightTrigger = int3;
		this.sThumbLX = int4;
		this.sThumbLY = int5;
		this.sThumbRX = int6;
		this.sThumbRY = int7;
	}

	public native boolean nGetState(int int1) throws IOException;
}
