package zombie.ui;

import java.util.ArrayList;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.input.Mouse;


public final class FPSGraph extends UIElement {
	public static FPSGraph instance;
	private static final int NUM_BARS = 30;
	private static final int BAR_WID = 8;
	private final FPSGraph.Graph fpsGraph = new FPSGraph.Graph();
	private final FPSGraph.Graph upsGraph = new FPSGraph.Graph();
	private final FPSGraph.Graph lpsGraph = new FPSGraph.Graph();
	private final FPSGraph.Graph uiGraph = new FPSGraph.Graph();

	public FPSGraph() {
		this.setVisible(false);
	}

	public void addRender(long long1) {
		synchronized (this.fpsGraph) {
			this.fpsGraph.add(long1);
		}
	}

	public void addUpdate(long long1) {
		this.upsGraph.add(long1);
	}

	public void addLighting(long long1) {
		synchronized (this.lpsGraph) {
			this.lpsGraph.add(long1);
		}
	}

	public void addUI(long long1) {
		this.uiGraph.add(long1);
	}

	public void update() {
		if (this.isVisible()) {
			this.setHeight(108.0);
			this.setWidth(232.0);
			this.setX(20.0);
			this.setY((double)(Core.getInstance().getScreenHeight() - 20) - this.getHeight());
			super.update();
		}
	}

	public void render() {
		if (this.isVisible()) {
			if (UIManager.VisibleAllUI) {
				int int1 = this.getHeight().intValue() - 4;
				int int2 = -1;
				if (this.isMouseOver()) {
					this.DrawTextureScaledCol(UIElement.white, 0.0, 0.0, this.getWidth(), this.getHeight(), 0.0, 0.20000000298023224, 0.0, 0.5);
					int int3 = Mouse.getXA() - this.getAbsoluteX().intValue();
					int2 = int3 / 8;
				}

				synchronized (this.fpsGraph) {
					this.fpsGraph.render(0.0F, 1.0F, 0.0F);
					if (int2 >= 0 && int2 < this.fpsGraph.bars.size()) {
						this.DrawText("FPS: " + this.fpsGraph.bars.get(int2), 20.0, (double)(int1 / 2 - 10), 0.0, 1.0, 0.0, 1.0);
					}
				}

				synchronized (this.lpsGraph) {
					this.lpsGraph.render(1.0F, 1.0F, 0.0F);
					if (int2 >= 0 && int2 < this.lpsGraph.bars.size()) {
						this.DrawText("LPS: " + this.lpsGraph.bars.get(int2), 20.0, (double)(int1 / 2 + 20), 1.0, 1.0, 0.0, 1.0);
					}
				}

				this.upsGraph.render(0.0F, 1.0F, 1.0F);
				if (int2 >= 0 && int2 < this.upsGraph.bars.size()) {
					this.DrawText("UPS: " + this.upsGraph.bars.get(int2), 20.0, (double)(int1 / 2 + 5), 0.0, 1.0, 1.0, 1.0);
					this.DrawTextureScaledCol(UIElement.white, (double)(int2 * 8 + 4), 0.0, 1.0, this.getHeight(), 1.0, 1.0, 1.0, 0.5);
				}

				this.uiGraph.render(1.0F, 0.0F, 1.0F);
				if (int2 >= 0 && int2 < this.uiGraph.bars.size()) {
					this.DrawText("UI: " + this.uiGraph.bars.get(int2), 20.0, (double)(int1 / 2 - 26), 1.0, 0.0, 1.0, 1.0);
				}
			}
		}
	}

	private final class Graph {
		private final ArrayList times = new ArrayList();
		private final ArrayList bars = new ArrayList();

		public void add(long long1) {
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

		public void render(float float1, float float2, float float3) {
			if (!this.bars.isEmpty()) {
				float float4 = (float)(FPSGraph.this.getHeight().intValue() - 4);
				float float5 = (float)(FPSGraph.this.getHeight().intValue() - 2);
				int int1 = Math.max(PerformanceSettings.getLockFPS(), PerformanceSettings.LightingFPS);
				int int2 = 8;
				float float6 = float4 * ((float)Math.min(int1, (Integer)this.bars.get(0)) / (float)int1);
				for (int int3 = 1; int3 < this.bars.size() - 1; ++int3) {
					float float7 = float4 * ((float)Math.min(int1, (Integer)this.bars.get(int3)) / (float)int1);
					SpriteRenderer.instance.renderline((Texture)null, FPSGraph.this.getAbsoluteX().intValue() + int2 - 8 + 4, FPSGraph.this.getAbsoluteY().intValue() + (int)(float5 - float6), FPSGraph.this.getAbsoluteX().intValue() + int2 + 4, FPSGraph.this.getAbsoluteY().intValue() + (int)(float5 - float7), float1, float2, float3, 0.35F, 1);
					int2 += 8;
					float6 = float7;
				}
			}
		}
	}
}
