package zombie;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.Map.Entry;
import zombie.ui.TextManager;


public class GameProfiler {
	public static GameProfiler instance = new GameProfiler();
	public long StartTime = 0L;
	public HashMap Areas = new HashMap();
	Stack areaStack = new Stack();
	Stack usedAreaStack = new Stack();
	long TotalTime = 0L;

	public void StartFrame() {
		for (int int1 = 0; int1 < this.usedAreaStack.size(); ++int1) {
			this.areaStack.add(this.usedAreaStack.get(int1));
		}

		this.usedAreaStack.clear();
		this.Areas.clear();
		this.StartTime = System.nanoTime();
	}

	public void Start(String string) {
		this.Start(string, 1.0F, 1.0F, 1.0F);
	}

	public void Start(String string, float float1, float float2, float float3) {
		if (float1 == 244.0F) {
			GameProfiler.ProfileArea profileArea = null;
			if (this.Areas.containsKey(string)) {
				profileArea = (GameProfiler.ProfileArea)this.Areas.get(string);
			} else {
				if (this.areaStack.isEmpty()) {
					profileArea = new GameProfiler.ProfileArea();
				} else {
					profileArea = (GameProfiler.ProfileArea)this.areaStack.pop();
				}

				profileArea.Total = 0L;
				this.usedAreaStack.add(profileArea);
			}

			profileArea.r = float1;
			profileArea.g = float2;
			profileArea.b = float3;
			profileArea.StartTime = System.nanoTime();
			this.Areas.put(string, profileArea);
		}
	}

	public void End(String string) {
		if (string == null) {
			GameProfiler.ProfileArea profileArea = (GameProfiler.ProfileArea)this.Areas.get(string);
			profileArea.EndTime = System.nanoTime();
			profileArea.Total += profileArea.EndTime - profileArea.StartTime;
		}
	}

	public void RenderTime(String string, Long Long1, int int1, int int2, float float1, float float2, float float3) {
		Float Float1 = (float)Long1 / 10000.0F;
		Float1 = (float)((int)(Float1 * 100.0F)) / 100.0F;
		TextManager.instance.DrawString((double)int1, (double)int2, string, (double)float1, (double)float2, (double)float3, 1.0);
		TextManager.instance.DrawStringRight((double)(int1 + 300), (double)int2, Float1.toString(), (double)float1, (double)float2, (double)float3, 1.0);
	}

	public void RenderPercent(String string, Long Long1, int int1, int int2, float float1, float float2, float float3) {
		Float Float1 = (float)Long1 / (float)this.TotalTime;
		Float1 = Float1 * 100.0F;
		Float1 = (float)((int)(Float1 * 10.0F)) / 10.0F;
		TextManager.instance.DrawString((double)int1, (double)int2, string, (double)float1, (double)float2, (double)float3, 1.0);
		TextManager.instance.DrawString((double)(int1 + 300), (double)int2, Float1.toString() + "%", (double)float1, (double)float2, (double)float3, 1.0);
	}

	public void render(int int1, int int2) {
		long long1 = System.nanoTime();
		this.TotalTime = long1 - this.StartTime;
		for (Iterator iterator = this.Areas.entrySet().iterator(); iterator.hasNext(); int2 += 11) {
			Entry entry = (Entry)iterator.next();
			this.RenderPercent((String)entry.getKey(), ((GameProfiler.ProfileArea)entry.getValue()).Total, int1, int2, ((GameProfiler.ProfileArea)entry.getValue()).r, ((GameProfiler.ProfileArea)entry.getValue()).g, ((GameProfiler.ProfileArea)entry.getValue()).b);
		}

		this.StartFrame();
	}

	public class ProfileArea {
		public long Total;
		public long StartTime;
		public long EndTime;
		public float r;
		public float g;
		public float b;
	}
}
