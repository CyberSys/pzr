package zombie.core.utils;

import java.util.Stack;
import zombie.GameTime;
import zombie.core.Rand;


public class OnceEvery {
	public static int FPS = 60;
	int millioff = 0;
	int millimax = 0;
	private float seconds;
	static float comp = 0.0F;
	static float last = 0.0F;
	static Stack list = new Stack();

	public OnceEvery(float float1, boolean boolean1) {
		this.seconds = float1;
		this.millimax = (int)((float)FPS * float1);
		if (boolean1) {
			this.millioff = Rand.Next(this.millimax);
		}
	}

	public OnceEvery(float float1) {
		this.seconds = float1;
		this.millimax = (int)((float)FPS * float1);
	}

	public void SetFrequency(float float1) {
		this.millimax = (int)((float)FPS * float1);
	}

	public boolean Check() {
		this.millimax = (int)((float)FPS * this.seconds);
		if (this.millimax == 0) {
			return true;
		} else {
			long long1 = ((long)last - (long)this.millioff) % (long)this.millimax;
			long long2 = ((long)comp - (long)this.millioff) % (long)this.millimax;
			return long1 > long2 || (float)this.millimax < comp - last;
		}
	}

	public static void update() {
		last = comp;
		comp += 1.0F * GameTime.instance.getMultiplier();
	}
}
