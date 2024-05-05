package zombie.core.profiling;

import zombie.GameProfiler;
import zombie.util.Lambda;
import zombie.util.lambda.Invokers;


public abstract class AbstractPerformanceProfileProbe {
	public final String Name;
	private boolean m_isEnabled = true;
	private boolean m_isRunning = false;
	private boolean m_isProfilerRunning = false;

	protected AbstractPerformanceProfileProbe(String string) {
		this.Name = string;
	}

	protected abstract void onStart();

	protected abstract void onEnd();

	public void start() {
		if (this.m_isRunning) {
			throw new RuntimeException("start() already called. " + this.getClass().getSimpleName() + " is Non-reentrant. Please call end() first.");
		} else {
			this.m_isProfilerRunning = this.isEnabled() && GameProfiler.isRunning();
			if (this.m_isProfilerRunning) {
				this.m_isRunning = true;
				this.onStart();
			}
		}
	}

	public boolean isEnabled() {
		return this.m_isEnabled;
	}

	public void setEnabled(boolean boolean1) {
		this.m_isEnabled = boolean1;
	}

	public void end() {
		if (this.m_isProfilerRunning) {
			if (!this.m_isRunning) {
				throw new RuntimeException("end() called without calling start().");
			} else {
				this.onEnd();
				this.m_isRunning = false;
			}
		}
	}

	public void invokeAndMeasure(Runnable runnable) {
		try {
			this.start();
			runnable.run();
		} finally {
			this.end();
		}
	}

	public void invokeAndMeasure(Object object, Invokers.Params1.ICallback iCallback) {
		Lambda.capture(this, object, iCallback, (var0,objectx,iCallbackx,var3)->{
			objectx.invokeAndMeasure(var0.invoker(iCallbackx, var3));
		});
	}

	public void invokeAndMeasure(Object object, Object object2, Invokers.Params2.ICallback iCallback) {
		Lambda.capture(this, object, object2, iCallback, (var0,objectx,object2x,iCallbackx,var4)->{
			objectx.invokeAndMeasure(var0.invoker(object2x, iCallbackx, var4));
		});
	}
}
