package zombie.ui;

import java.util.ArrayList;
import zombie.core.Core;


public final class ServerPulseGraph extends UIElement {
	public static ServerPulseGraph instance;
	private final ArrayList times = new ArrayList();
	private final ArrayList bars = new ArrayList();
	private final int NUM_BARS = 30;
	private final int BAR_WID = 4;
	private final int BAR_PAD = 1;

	public ServerPulseGraph() {
		this.setVisible(false);
	}

	public void add(long long1) {
		if (long1 < 0L) {
			this.setVisible(false);
		} else {
			this.setVisible(true);
			this.times.add(long1);
			this.bars.clear();
			long long2 = (Long)this.times.get(0);
			int int1 = 1;
			int int2;
			for (int2 = 1; int2 < this.times.size(); ++int2) {
				if (int2 != this.times.size() - 1 && (Long)this.times.get(int2) - long2 <= 1000L) {
					++int1;
				} else {
					long long3 = ((Long)this.times.get(int2) - long2) / 1000L - 1L;
					for (int int3 = 0; (long)int3 < long3; ++int3) {
						this.bars.add(0);
					}

					this.bars.add(int1);
					int1 = 1;
					long2 = (Long)this.times.get(int2);
				}
			}

			while (this.bars.size() > 30) {
				int2 = (Integer)this.bars.get(0);
				for (int int4 = 0; int4 < int2; ++int4) {
					this.times.remove(0);
				}

				this.bars.remove(0);
			}
		}
	}

	public void update() {
		if (this.isVisible()) {
			this.setX(20.0);
			this.setY((double)(Core.getInstance().getScreenHeight() - 20 - 36));
			this.setHeight(36.0);
			this.setWidth(149.0);
			super.update();
		}
	}

	public void render() {
		if (this.isVisible()) {
			if (UIManager.getClock() == null || UIManager.getClock().isVisible()) {
				this.DrawTextureScaledCol(UIElement.white, 0.0, 0.0, this.getWidth(), this.getHeight(), 0.0, 0.0, 0.0, 0.5);
				if (!this.bars.isEmpty()) {
					int int1 = 0;
					for (int int2 = 0; int2 < this.bars.size(); ++int2) {
						float float1 = (float)this.getHeight().intValue() * ((float)Math.min(10, (Integer)this.bars.get(int2)) / 10.0F);
						this.DrawTextureScaledCol(UIElement.white, (double)int1, this.getHeight() - (double)float1, 4.0, (double)float1, 1.0, 1.0, 1.0, 0.3499999940395355);
						int1 += 5;
					}
				}
			}
		}
	}
}
