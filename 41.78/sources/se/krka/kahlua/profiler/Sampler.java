package se.krka.kahlua.profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import se.krka.kahlua.vm.Coroutine;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;


public class Sampler {
	private static final AtomicInteger NEXT_ID = new AtomicInteger();
	private final KahluaThread thread;
	private final Timer timer;
	private final long period;
	private final Profiler profiler;

	public Sampler(KahluaThread kahluaThread, long long1, Profiler profiler) {
		this.thread = kahluaThread;
		this.period = long1;
		this.profiler = profiler;
		this.timer = new Timer("Kahlua Sampler-" + NEXT_ID.incrementAndGet(), true);
	}

	public void start() {
		TimerTask timerTask = new TimerTask(){
    
    public void run() {
        ArrayList timerTask = new ArrayList();
        Sampler.this.appendList(timerTask, Sampler.this.thread.currentCoroutine);
        Sampler.this.profiler.getSample(new Sample(timerTask, Sampler.this.period));
    }
};
		this.timer.scheduleAtFixedRate(timerTask, 0L, this.period);
	}

	private void appendList(List list, Coroutine coroutine) {
		while (coroutine != null) {
			LuaCallFrame[] luaCallFrameArray = coroutine.getCallframeStack();
			int int1 = Math.min(luaCallFrameArray.length, coroutine.getCallframeTop());
			for (int int2 = int1 - 1; int2 >= 0; --int2) {
				LuaCallFrame luaCallFrame = luaCallFrameArray[int2];
				int int3 = luaCallFrame.pc - 1;
				LuaClosure luaClosure = luaCallFrame.closure;
				JavaFunction javaFunction = luaCallFrame.javaFunction;
				if (luaClosure != null) {
					list.add(new LuaStacktraceElement(int3, luaClosure.prototype));
				} else if (javaFunction != null) {
					list.add(new JavaStacktraceElement(javaFunction));
				}
			}

			coroutine = coroutine.getParent();
		}
	}

	public void stop() {
		this.timer.cancel();
	}
}
