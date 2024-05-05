package zombie.core.profiling;

import zombie.util.list.PZArrayUtil;


public class PerformanceProfileProbeList {
	final String m_prefix;
	final PerformanceProfileProbe[] layers;

	public static PerformanceProfileProbeList construct(String string, int int1) {
		return new PerformanceProfileProbeList(string, int1, PerformanceProfileProbe.class, PerformanceProfileProbe::new);
	}

	public static PerformanceProfileProbeList construct(String string, int int1, Class javaClass, PerformanceProfileProbeList.Constructor constructor) {
		return new PerformanceProfileProbeList(string, int1, javaClass, constructor);
	}

	protected PerformanceProfileProbeList(String string, int int1, Class javaClass, PerformanceProfileProbeList.Constructor constructor) {
		this.m_prefix = string;
		this.layers = (PerformanceProfileProbe[])PZArrayUtil.newInstance(javaClass, int1 + 1);
		for (int int2 = 0; int2 < int1; ++int2) {
			this.layers[int2] = constructor.get(string + "_" + int2);
		}

		this.layers[int1] = constructor.get(string + "_etc");
	}

	public int count() {
		return this.layers.length;
	}

	public PerformanceProfileProbe at(int int1) {
		return int1 < this.count() ? this.layers[int1] : this.layers[this.count() - 1];
	}

	public PerformanceProfileProbe start(int int1) {
		PerformanceProfileProbe performanceProfileProbe = this.at(int1);
		performanceProfileProbe.start();
		return performanceProfileProbe;
	}

	public interface Constructor {

		PerformanceProfileProbe get(String string);
	}
}
