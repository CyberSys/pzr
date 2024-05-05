package zombie.vehicles;

import zombie.scripting.objects.VehicleScript;


public final class LightbarSirenMode {
	private int mode = 0;
	private final int modeMax = 3;

	public int get() {
		return this.mode;
	}

	public void set(int int1) {
		if (int1 > 3) {
			this.mode = 3;
		} else if (int1 < 0) {
			this.mode = 0;
		} else {
			this.mode = int1;
		}
	}

	public boolean isEnable() {
		return this.mode != 0;
	}

	public String getSoundName(VehicleScript.LightBar lightBar) {
		if (this.isEnable()) {
			if (this.mode == 1) {
				return lightBar.soundSiren0;
			}

			if (this.mode == 2) {
				return lightBar.soundSiren1;
			}

			if (this.mode == 3) {
				return lightBar.soundSiren2;
			}
		}

		return "";
	}
}
