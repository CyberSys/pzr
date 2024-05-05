package zombie;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import zombie.core.profiling.TriggerGameProfilerFile;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.iso.IsoCamera;
import zombie.ui.TextManager;
import zombie.util.IPooledObject;
import zombie.util.Lambda;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.lambda.Invokers;


public final class GameProfiler {
	private static final String s_currentSessionUUID = UUID.randomUUID().toString();
	private static final ThreadLocal s_instance = ThreadLocal.withInitial(GameProfiler::new);
	private final Stack m_stack = new Stack();
	private final GameProfiler.RecordingFrame m_currentFrame = new GameProfiler.RecordingFrame();
	private final GameProfiler.RecordingFrame m_previousFrame = new GameProfiler.RecordingFrame();
	private boolean m_isInFrame;
	private final GameProfileRecording m_recorder;
	private static final Object m_gameProfilerRecordingTriggerLock = "Game Profiler Recording Watcher, synchronization lock";
	private static PredicatedFileWatcher m_gameProfilerRecordingTriggerWatcher;

	private GameProfiler() {
		String string = Thread.currentThread().getName();
		String string2 = string.replace("-", "").replace(" ", "");
		String string3 = String.format("%s_GameProfiler_%s", this.getCurrentSessionUUID(), string2);
		this.m_recorder = new GameProfileRecording(string3);
	}

	private static void onTrigger_setAnimationRecorderTriggerFile(TriggerGameProfilerFile triggerGameProfilerFile) {
		DebugOptions.instance.GameProfilerEnabled.setValue(triggerGameProfilerFile.isRecording);
	}

	private String getCurrentSessionUUID() {
		return s_currentSessionUUID;
	}

	public static GameProfiler getInstance() {
		return (GameProfiler)s_instance.get();
	}

	public void startFrame(String string) {
		if (this.m_isInFrame) {
			throw new RuntimeException("Already inside a frame.");
		} else {
			this.m_isInFrame = true;
			if (!this.m_stack.empty()) {
				throw new RuntimeException("Recording stack should be empty.");
			} else {
				int int1 = IsoCamera.frameState.frameCount;
				if (this.m_currentFrame.FrameNo != int1) {
					this.m_previousFrame.transferFrom(this.m_currentFrame);
					if (this.m_previousFrame.FrameNo != -1) {
						this.m_recorder.writeLine();
					}

					long long1 = getTimeNs();
					this.m_currentFrame.FrameNo = int1;
					this.m_currentFrame.m_frameInvokerKey = string;
					this.m_currentFrame.m_startTime = long1;
					this.m_recorder.reset();
					this.m_recorder.setFrameNumber(this.m_currentFrame.FrameNo);
					this.m_recorder.setStartTime(this.m_currentFrame.m_startTime);
				}
			}
		}
	}

	public void endFrame() {
		this.m_currentFrame.m_endTime = getTimeNs();
		this.m_currentFrame.m_totalTime = this.m_currentFrame.m_endTime - this.m_currentFrame.m_startTime;
		this.m_isInFrame = false;
	}

	public void invokeAndMeasureFrame(String string, Runnable runnable) {
		if (!isRunning()) {
			runnable.run();
		} else {
			this.startFrame(string);
			try {
				this.invokeAndMeasure(string, runnable);
			} finally {
				this.endFrame();
			}
		}
	}

	public void invokeAndMeasure(String string, Runnable runnable) {
		if (!isRunning()) {
			runnable.run();
		} else if (!this.m_isInFrame) {
			DebugLog.General.warn("Not inside in a frame. Find the root caller function for this thread, and add call to invokeAndMeasureFrame.");
		} else {
			GameProfiler.ProfileArea profileArea = this.start(string);
			try {
				runnable.run();
			} finally {
				this.end(profileArea);
			}
		}
	}

	public static boolean isRunning() {
		return DebugOptions.instance.GameProfilerEnabled.getValue();
	}

	public void invokeAndMeasure(String string, Object object, Invokers.Params1.ICallback iCallback) {
		if (!isRunning()) {
			iCallback.accept(object);
		} else {
			Lambda.capture(this, string, object, iCallback, (var0,stringx,objectx,iCallbackx,var4)->{
				stringx.invokeAndMeasure(objectx, var0.invoker(iCallbackx, var4));
			});
		}
	}

	public void invokeAndMeasure(String string, Object object, Object object2, Invokers.Params2.ICallback iCallback) {
		if (!isRunning()) {
			iCallback.accept(object, object2);
		} else {
			Lambda.capture(this, string, object, object2, iCallback, (var0,stringx,objectx,object2x,iCallbackx,var5)->{
				stringx.invokeAndMeasure(objectx, var0.invoker(object2x, iCallbackx, var5));
			});
		}
	}

	public void invokeAndMeasure(String string, Object object, Object object2, Object object3, Invokers.Params3.ICallback iCallback) {
		if (!isRunning()) {
			iCallback.accept(object, object2, object3);
		} else {
			Lambda.capture(this, string, object, object2, object3, iCallback, (var0,stringx,objectx,object2x,object3x,iCallbackx,var6)->{
				stringx.invokeAndMeasure(objectx, var0.invoker(object2x, object3x, iCallbackx, var6));
			});
		}
	}

	public GameProfiler.ProfileArea start(String string) {
		long long1 = getTimeNs();
		GameProfiler.ProfileArea profileArea = GameProfiler.ProfileArea.alloc();
		profileArea.Key = string;
		return this.start(profileArea, long1);
	}

	public GameProfiler.ProfileArea start(GameProfiler.ProfileArea profileArea) {
		long long1 = getTimeNs();
		return this.start(profileArea, long1);
	}

	public GameProfiler.ProfileArea start(GameProfiler.ProfileArea profileArea, long long1) {
		profileArea.StartTime = long1;
		profileArea.Depth = this.m_stack.size();
		if (!this.m_stack.isEmpty()) {
			GameProfiler.ProfileArea profileArea2 = (GameProfiler.ProfileArea)this.m_stack.peek();
			profileArea2.Children.add(profileArea);
		}

		this.m_stack.push(profileArea);
		return profileArea;
	}

	public void end(GameProfiler.ProfileArea profileArea) {
		profileArea.EndTime = getTimeNs();
		profileArea.Total = profileArea.EndTime - profileArea.StartTime;
		if (this.m_stack.peek() != profileArea) {
			throw new RuntimeException("Incorrect exit. ProfileArea " + profileArea + " is not at the top of the stack: " + this.m_stack.peek());
		} else {
			this.m_stack.pop();
			if (this.m_stack.isEmpty()) {
				this.m_recorder.logTimeSpan(profileArea);
				profileArea.release();
			}
		}
	}

	private void renderPercent(String string, long long1, int int1, int int2, float float1, float float2, float float3) {
		float float4 = (float)long1 / (float)this.m_previousFrame.m_totalTime;
		float4 *= 100.0F;
		float4 = (float)((int)(float4 * 10.0F)) / 10.0F;
		TextManager.instance.DrawString((double)int1, (double)int2, string, (double)float1, (double)float2, (double)float3, 1.0);
		TextManager.instance.DrawString((double)(int1 + 300), (double)int2, String.valueOf(float4) + "%", (double)float1, (double)float2, (double)float3, 1.0);
	}

	public void render(int int1, int int2) {
		this.renderPercent(this.m_previousFrame.m_frameInvokerKey, this.m_previousFrame.m_totalTime, int1, int2, 1.0F, 1.0F, 1.0F);
	}

	public static long getTimeNs() {
		return System.nanoTime();
	}

	public static void init() {
		initTriggerWatcher();
	}

	private static void initTriggerWatcher() {
		if (m_gameProfilerRecordingTriggerWatcher == null) {
			synchronized (m_gameProfilerRecordingTriggerLock) {
				if (m_gameProfilerRecordingTriggerWatcher == null) {
					m_gameProfilerRecordingTriggerWatcher = new PredicatedFileWatcher(ZomboidFileSystem.instance.getMessagingDirSub("Trigger_PerformanceProfiler.xml"), TriggerGameProfilerFile.class, GameProfiler::onTrigger_setAnimationRecorderTriggerFile);
					DebugFileWatcher.instance.add(m_gameProfilerRecordingTriggerWatcher);
				}
			}
		}
	}

	public static class RecordingFrame {
		private String m_frameInvokerKey = "";
		private int FrameNo = -1;
		private long m_startTime = 0L;
		private long m_endTime = 0L;
		private long m_totalTime = 0L;

		public void transferFrom(GameProfiler.RecordingFrame recordingFrame) {
			this.clear();
			this.FrameNo = recordingFrame.FrameNo;
			this.m_frameInvokerKey = recordingFrame.m_frameInvokerKey;
			this.m_startTime = recordingFrame.m_startTime;
			this.m_endTime = recordingFrame.m_endTime;
			this.m_totalTime = recordingFrame.m_totalTime;
			recordingFrame.clear();
		}

		public void clear() {
			this.FrameNo = -1;
			this.m_frameInvokerKey = "";
			this.m_startTime = 0L;
			this.m_endTime = 0L;
			this.m_totalTime = 0L;
		}
	}

	public static class ProfileArea extends PooledObject {
		public String Key;
		public long StartTime;
		public long EndTime;
		public long Total;
		public int Depth;
		public float r = 1.0F;
		public float g = 1.0F;
		public float b = 1.0F;
		public final List Children = new ArrayList();
		private static final Pool s_pool = new Pool(GameProfiler.ProfileArea::new);

		public void onReleased() {
			super.onReleased();
			this.clear();
		}

		public void clear() {
			this.StartTime = 0L;
			this.EndTime = 0L;
			this.Total = 0L;
			this.Depth = 0;
			IPooledObject.release(this.Children);
		}

		public static GameProfiler.ProfileArea alloc() {
			return (GameProfiler.ProfileArea)s_pool.alloc();
		}
	}
}
