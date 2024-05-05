package zombie.network;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.textures.ColorInfo;


public class PlayerNetHistory {
	private ArrayList states = new ArrayList();
	private int INTERP_TIME = 100;
	private ColorInfo lightInfo = new ColorInfo(1.0F, 1.0F, 1.0F, 0.5F);

	public void add(PlayerNetState playerNetState) {
		this.INTERP_TIME = 400;
		this.states.add(playerNetState);
		long long1 = System.currentTimeMillis();
		String string = "";
		for (int int1 = 0; int1 < this.states.size(); ++int1) {
			string = string + (long1 - ((PlayerNetState)this.states.get(int1)).time) + " ";
		}

		while (!this.states.isEmpty() && ((PlayerNetState)this.states.get(0)).time < long1 - 1000L) {
			PlayerNetState.release((PlayerNetState)this.states.get(0));
			this.states.remove(0);
		}
	}

	public void interpolate(IsoPlayer player) {
	}

	public void render(IsoPlayer player) {
	}

	private int findStateBefore(long long1) {
		for (int int1 = this.states.size() - 1; int1 >= 0; --int1) {
			if (((PlayerNetState)this.states.get(int1)).time <= long1) {
				return int1;
			}
		}

		return -1;
	}
}
