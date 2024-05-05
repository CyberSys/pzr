package zombie.core.profiling;

import java.util.Stack;
import zombie.GameProfiler;


public class PerformanceProfileProbe extends AbstractPerformanceProfileProbe {
	private final Stack m_currentArea = new Stack();

	public PerformanceProfileProbe(String string) {
		super(string);
	}

	public PerformanceProfileProbe(String string, boolean boolean1) {
		super(string);
		this.setEnabled(boolean1);
	}

	protected void onStart() {
		this.m_currentArea.push(GameProfiler.getInstance().start(this.Name));
	}

	protected void onEnd() {
		GameProfiler.getInstance().end((GameProfiler.ProfileArea)this.m_currentArea.pop());
	}
}
