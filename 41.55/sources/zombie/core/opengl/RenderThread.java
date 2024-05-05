package zombie.core.opengl;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjglx.LWJGLException;
import org.lwjglx.input.Controllers;
import org.lwjglx.opengl.Display;
import org.lwjglx.opengl.OpenGLException;
import org.lwjglx.opengl.Util;
import zombie.GameWindow;
import zombie.Lua.LuaManager;
import zombie.core.Clipboard;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.ThreadGroups;
import zombie.core.profiling.PerformanceProfileFrameProbe;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.sprite.SpriteRenderState;
import zombie.core.textures.TextureID;
import zombie.debug.DebugLog;
import zombie.debug.DebugLogStream;
import zombie.debug.DebugOptions;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.network.GameServer;
import zombie.ui.FPSGraph;
import zombie.util.Lambda;
import zombie.util.lambda.Invokers;
import zombie.util.list.PZArrayUtil;


public class RenderThread {
	private static Thread MainThread;
	public static Thread RenderThread;
	private static Thread ContextThread = null;
	private static boolean m_isDisplayCreated = false;
	private static int m_contextLockReentrantDepth = 0;
	public static final Object m_contextLock = "RenderThread borrowContext Lock";
	private static final ArrayList invokeOnRenderQueue = new ArrayList();
	private static final ArrayList invokeOnRenderQueue_Invoking = new ArrayList();
	private static boolean m_isInitialized = false;
	private static final Object m_initLock = "RenderThread Initialization Lock";
	private static volatile boolean m_isCloseRequested = false;
	private static volatile int m_displayWidth;
	private static volatile int m_displayHeight;
	private static final boolean s_legacyAllowSingleThreadRendering = false;
	private static volatile boolean m_renderingEnabled = true;
	private static volatile boolean m_waitForRenderState = false;
	private static volatile boolean m_hasContext = false;
	private static boolean m_cursorVisible = true;

	public static void init() {
		synchronized (m_initLock) {
			if (!m_isInitialized) {
				MainThread = Thread.currentThread();
				Core.bLoadedWithMultithreaded = Core.bMultithreadedRendering;
				RenderThread = Thread.currentThread();
				m_displayWidth = Display.getWidth();
				m_displayHeight = Display.getHeight();
				m_isInitialized = true;
			}
		}
	}

	public static void initServerGUI() {
		synchronized (m_initLock) {
			if (m_isInitialized) {
				return;
			}

			MainThread = Thread.currentThread();
			Core.bLoadedWithMultithreaded = Core.bMultithreadedRendering;
			RenderThread = new Thread(ThreadGroups.Main, RenderThread::renderLoop, "RenderThread Main Loop");
			RenderThread.setName("Render Thread");
			RenderThread.setUncaughtExceptionHandler(RenderThread::uncaughtException);
			m_displayWidth = Display.getWidth();
			m_displayHeight = Display.getHeight();
			m_isInitialized = true;
		}
		RenderThread.start();
	}

	public static void renderLoop() {
		if (!GameServer.bServer) {
			synchronized (m_initLock) {
				try {
					m_isInitialized = false;
					GameWindow.InitDisplay();
					Controllers.create();
					Clipboard.initMainThread();
				} catch (Exception exception) {
					throw new RuntimeException(exception);
				} finally {
					m_isInitialized = true;
				}
			}
		}

		acquireContextReentrant();
		for (boolean boolean1 = true; boolean1; Thread.yield()) {
			synchronized (m_contextLock) {
				if (!m_hasContext) {
					acquireContextReentrant();
				}

				m_displayWidth = Display.getWidth();
				m_displayHeight = Display.getHeight();
				if (m_renderingEnabled) {
					RenderThread.s_performance.renderStep.invokeAndMeasure(RenderThread::renderStep);
				} else if (m_isDisplayCreated && m_hasContext) {
					Display.processMessages();
				}

				flushInvokeQueue();
				if (!m_renderingEnabled) {
					m_isCloseRequested = false;
				} else {
					GameWindow.GameInput.poll();
					Mouse.poll();
					GameKeyboard.poll();
					m_isCloseRequested = m_isCloseRequested || Display.isCloseRequested();
				}

				if (!GameServer.bServer) {
					Clipboard.updateMainThread();
				}

				DebugOptions.testThreadCrash(0);
				boolean1 = !GameWindow.bGameThreadExited;
			}
		}

		releaseContextReentrant();
		synchronized (m_initLock) {
			RenderThread = null;
			m_isInitialized = false;
		}
		shutdown();
		System.exit(0);
	}

	private static void uncaughtException(Thread thread, Throwable throwable) {
		if (throwable instanceof ThreadDeath) {
			DebugLog.General.println("Render Thread exited: ", thread.getName());
		} else {
			boolean boolean1 = false;
			try {
				boolean1 = true;
				GameWindow.uncaughtException(thread, throwable);
				boolean1 = false;
			} finally {
				if (boolean1) {
					Runnable runnable = ()->{
						long thread = 120000L;
						long long1 = 0L;
						long long2 = System.currentTimeMillis();
						long long3 = long2;
						if (!GameWindow.bGameThreadExited) {
							try {
								Thread.sleep(1000L);
							} catch (InterruptedException interruptedException) {
							}

							DebugLog.General.error("  Waiting for GameThread to exit...");
							try {
								Thread.sleep(2000L);
							} catch (InterruptedException interruptedException2) {
							}

							while (!GameWindow.bGameThreadExited) {
								Thread.yield();
								long2 = System.currentTimeMillis();
								long long4 = long2 - long3;
								long1 += long4;
								if (long1 >= 120000L) {
									DebugLog.General.error("  GameThread failed to exit within time limit.");
									break;
								}

								long3 = long2;
							}
						}

						DebugLog.General.error("  Shutting down...");
						System.exit(1);
					};

					Thread long4 = new Thread(runnable, "ForceCloseThread");
					long4.start();
					DebugLog.General.error("Shutting down sequence starts.");
					m_isCloseRequested = true;
					DebugLog.General.error("  Notifying render state queue...");
					notifyRenderStateQueue();
					DebugLog.General.error("  Notifying InvokeOnRenderQueue...");
					synchronized (invokeOnRenderQueue) {
						invokeOnRenderQueue_Invoking.addAll(invokeOnRenderQueue);
						invokeOnRenderQueue.clear();
					}

					PZArrayUtil.forEach((List)invokeOnRenderQueue_Invoking, RenderContextQueueItem::notifyWaitingListeners);
				}
			}

			Runnable long1 = ()->{
				long thread = 120000L;
				long long1 = 0L;
				long long2 = System.currentTimeMillis();
				long long3 = long2;
				if (!GameWindow.bGameThreadExited) {
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException interruptedException) {
					}

					DebugLog.General.error("  Waiting for GameThread to exit...");
					try {
						Thread.sleep(2000L);
					} catch (InterruptedException interruptedException2) {
					}

					while (!GameWindow.bGameThreadExited) {
						Thread.yield();
						long2 = System.currentTimeMillis();
						long long4 = long2 - long3;
						long1 += long4;
						if (long1 >= 120000L) {
							DebugLog.General.error("  GameThread failed to exit within time limit.");
							break;
						}

						long3 = long2;
					}
				}

				DebugLog.General.error("  Shutting down...");
				System.exit(1);
			};

			Thread thread2 = new Thread(long1, "ForceCloseThread");
			thread2.start();
			DebugLog.General.error("Shutting down sequence starts.");
			m_isCloseRequested = true;
			DebugLog.General.error("  Notifying render state queue...");
			notifyRenderStateQueue();
			DebugLog.General.error("  Notifying InvokeOnRenderQueue...");
			synchronized (invokeOnRenderQueue) {
				invokeOnRenderQueue_Invoking.addAll(invokeOnRenderQueue);
				invokeOnRenderQueue.clear();
			}

			PZArrayUtil.forEach((List)invokeOnRenderQueue_Invoking, RenderContextQueueItem::notifyWaitingListeners);
		}
	}

	private static boolean renderStep() {
		boolean boolean1 = false;
		try {
			boolean1 = lockStepRenderStep();
		} catch (OpenGLException openGLException) {
			logGLException(openGLException);
		} catch (Exception exception) {
			DebugLogStream debugLogStream = DebugLog.General;
			String string = exception.getClass().getTypeName();
			debugLogStream.error("Thrown an " + string + ": " + exception.getMessage());
			exception.printStackTrace();
		}

		return boolean1;
	}

	private static boolean lockStepRenderStep() {
		SpriteRenderState spriteRenderState = SpriteRenderer.instance.acquireStateForRendering(RenderThread::waitForRenderStateCallback);
		if (spriteRenderState != null) {
			m_cursorVisible = spriteRenderState.bCursorVisible;
			RenderThread.s_performance.spriteRendererPostRender.invokeAndMeasure(()->{
				SpriteRenderer.instance.postRender();
			});

			RenderThread.s_performance.displayUpdate.invokeAndMeasure(()->{
				Display.update(true);
				checkControllers();
			});

			if (Core.bDebug && FPSGraph.instance != null) {
				FPSGraph.instance.addRender(System.currentTimeMillis());
			}

			return true;
		} else {
			notifyRenderStateQueue();
			if (!m_waitForRenderState || LuaManager.thread != null && LuaManager.thread.bStep) {
				RenderThread.s_performance.displayUpdate.invokeAndMeasure(()->{
					Display.processMessages();
				});
			}

			return true;
		}
	}

	private static void checkControllers() {
	}

	private static boolean waitForRenderStateCallback() {
		flushInvokeQueue();
		return shouldContinueWaiting();
	}

	private static boolean shouldContinueWaiting() {
		return !m_isCloseRequested && !GameWindow.bGameThreadExited && (m_waitForRenderState || SpriteRenderer.instance.isWaitingForRenderState());
	}

	public static boolean isWaitForRenderState() {
		return m_waitForRenderState;
	}

	public static void setWaitForRenderState(boolean boolean1) {
		m_waitForRenderState = boolean1;
	}

	private static void flushInvokeQueue() {
		synchronized (invokeOnRenderQueue) {
			invokeOnRenderQueue_Invoking.addAll(invokeOnRenderQueue);
			invokeOnRenderQueue.clear();
		}
		try {
			if (!invokeOnRenderQueue_Invoking.isEmpty()) {
				long long1 = System.nanoTime();
				while (!invokeOnRenderQueue_Invoking.isEmpty()) {
					RenderContextQueueItem renderContextQueueItem = (RenderContextQueueItem)invokeOnRenderQueue_Invoking.remove(0);
					long long2 = System.nanoTime();
					renderContextQueueItem.invoke();
					long long3 = System.nanoTime();
					if ((double)(long3 - long2) > 1.0E7) {
						boolean boolean1 = true;
					}

					if ((double)(long3 - long1) > 1.0E7) {
						break;
					}
				}

				label45: for (int int1 = invokeOnRenderQueue_Invoking.size() - 1; int1 >= 0; --int1) {
					RenderContextQueueItem renderContextQueueItem2 = (RenderContextQueueItem)invokeOnRenderQueue_Invoking.get(int1);
					if (renderContextQueueItem2.isWaiting()) {
						while (true) {
							if (int1 < 0) {
								break label45;
							}

							RenderContextQueueItem renderContextQueueItem3 = (RenderContextQueueItem)invokeOnRenderQueue_Invoking.remove(0);
							renderContextQueueItem3.invoke();
							--int1;
						}
					}
				}
			}

			if (TextureID.deleteTextureIDS.position() > 0) {
				TextureID.deleteTextureIDS.flip();
				GL11.glDeleteTextures(TextureID.deleteTextureIDS);
				TextureID.deleteTextureIDS.clear();
			}
		} catch (OpenGLException openGLException) {
			logGLException(openGLException);
		} catch (Exception exception) {
			DebugLogStream debugLogStream = DebugLog.General;
			String string = exception.getClass().getTypeName();
			debugLogStream.error("Thrown an " + string + ": " + exception.getMessage());
			exception.printStackTrace();
		}
	}

	public static void logGLException(OpenGLException openGLException) {
		logGLException(openGLException, true);
	}

	public static void logGLException(OpenGLException openGLException, boolean boolean1) {
		DebugLog.General.error("OpenGLException thrown: " + openGLException.getMessage());
		for (int int1 = GL11.glGetError(); int1 != 0; int1 = GL11.glGetError()) {
			String string = Util.translateGLErrorString(int1);
			DebugLog.General.error("  Also detected error: " + string + " ( code:" + int1 + ")");
		}

		if (boolean1) {
			DebugLog.General.error("Stack trace:");
			openGLException.printStackTrace();
		}
	}

	public static void Ready() {
		SpriteRenderer.instance.pushFrameDown();
		if (!m_isInitialized) {
			invokeOnRenderContext(RenderThread::renderStep);
		}
	}

	private static void acquireContextReentrant() {
		synchronized (m_contextLock) {
			acquireContextReentrantInternal();
		}
	}

	private static void releaseContextReentrant() {
		synchronized (m_contextLock) {
			releaseContextReentrantInternal();
		}
	}

	private static void acquireContextReentrantInternal() {
		Thread thread = Thread.currentThread();
		if (ContextThread != null && ContextThread != thread) {
			throw new RuntimeException("Context thread mismatch: " + ContextThread + ", " + thread);
		} else {
			++m_contextLockReentrantDepth;
			if (m_contextLockReentrantDepth <= 1) {
				ContextThread = thread;
				m_isDisplayCreated = Display.isCreated();
				if (m_isDisplayCreated) {
					try {
						m_hasContext = true;
						Display.makeCurrent();
						Display.setVSyncEnabled(Core.OptionVSync);
					} catch (LWJGLException lWJGLException) {
						DebugLog.General.error("Exception thrown trying to gain GL context.");
						lWJGLException.printStackTrace();
					}
				}
			}
		}
	}

	private static void releaseContextReentrantInternal() {
		Thread thread = Thread.currentThread();
		if (ContextThread != thread) {
			throw new RuntimeException("Context thread mismatch: " + ContextThread + ", " + thread);
		} else if (m_contextLockReentrantDepth == 0) {
			throw new RuntimeException("Context thread release overflow: 0: " + ContextThread + ", " + thread);
		} else {
			--m_contextLockReentrantDepth;
			if (m_contextLockReentrantDepth <= 0) {
				if (m_isDisplayCreated && m_hasContext) {
					try {
						m_hasContext = false;
						Display.releaseContext();
					} catch (LWJGLException lWJGLException) {
						DebugLog.General.error("Exception thrown trying to release GL context.");
						lWJGLException.printStackTrace();
					}
				}

				ContextThread = null;
			}
		}
	}

	public static void invokeOnRenderContext(Runnable runnable) throws RenderContextQueueException {
		RenderContextQueueItem renderContextQueueItem = RenderContextQueueItem.alloc(runnable);
		renderContextQueueItem.setWaiting();
		queueInvokeOnRenderContext(renderContextQueueItem);
		try {
			renderContextQueueItem.waitUntilFinished(()->{
				notifyRenderStateQueue();
				return !m_isCloseRequested && !GameWindow.bGameThreadExited;
			});
		} catch (InterruptedException interruptedException) {
			DebugLog.General.error("Thread Interrupted while waiting for queued item to finish:" + renderContextQueueItem);
			notifyRenderStateQueue();
		}

		Throwable throwable = renderContextQueueItem.getThrown();
		if (throwable != null) {
			throw new RenderContextQueueException(throwable);
		}
	}

	public static void invokeOnRenderContext(Object object, Invokers.Params1.ICallback iCallback) {
		Lambda.capture(object, iCallback, (objectx,iCallbackx,var2)->{
			invokeOnRenderContext(objectx.invoker(iCallbackx, var2));
		});
	}

	public static void invokeOnRenderContext(Object object, Object object2, Invokers.Params2.ICallback iCallback) {
		Lambda.capture(object, object2, iCallback, (objectx,object2x,iCallbackx,var3)->{
			invokeOnRenderContext(objectx.invoker(object2x, iCallbackx, var3));
		});
	}

	public static void invokeOnRenderContext(Object object, Object object2, Object object3, Invokers.Params3.ICallback iCallback) {
		Lambda.capture(object, object2, object3, iCallback, (objectx,object2x,object3x,iCallbackx,var4)->{
			invokeOnRenderContext(objectx.invoker(object2x, object3x, iCallbackx, var4));
		});
	}

	public static void invokeOnRenderContext(Object object, Object object2, Object object3, Object object4, Invokers.Params4.ICallback iCallback) {
		Lambda.capture(object, object2, object3, object4, iCallback, (objectx,object2x,object3x,object4x,iCallbackx,var5)->{
			invokeOnRenderContext(objectx.invoker(object2x, object3x, object4x, iCallbackx, var5));
		});
	}

	protected static void notifyRenderStateQueue() {
		if (SpriteRenderer.instance != null) {
			SpriteRenderer.instance.notifyRenderStateQueue();
		}
	}

	public static void queueInvokeOnRenderContext(Runnable runnable) {
		queueInvokeOnRenderContext(RenderContextQueueItem.alloc(runnable));
	}

	public static void queueInvokeOnRenderContext(RenderContextQueueItem renderContextQueueItem) {
		if (!m_isInitialized) {
			synchronized (m_initLock) {
				if (!m_isInitialized) {
					try {
						acquireContextReentrant();
						renderContextQueueItem.invoke();
					} finally {
						releaseContextReentrant();
					}

					return;
				}
			}
		}

		if (ContextThread == Thread.currentThread()) {
			renderContextQueueItem.invoke();
		} else {
			synchronized (invokeOnRenderQueue) {
				invokeOnRenderQueue.add(renderContextQueueItem);
			}
		}
	}

	public static void shutdown() {
		GameWindow.GameInput.quit();
		if (m_isInitialized) {
			queueInvokeOnRenderContext(Display::destroy);
		} else {
			Display.destroy();
		}
	}

	public static boolean isCloseRequested() {
		if (m_isCloseRequested) {
			DebugLog.log("EXITDEBUG: RenderThread.isCloseRequested 1");
			return m_isCloseRequested;
		} else {
			if (!m_isInitialized) {
				synchronized (m_initLock) {
					if (!m_isInitialized) {
						m_isCloseRequested = Display.isCloseRequested();
						if (m_isCloseRequested) {
							DebugLog.log("EXITDEBUG: RenderThread.isCloseRequested 2");
						}
					}
				}
			}

			return m_isCloseRequested;
		}
	}

	public static int getDisplayWidth() {
		return !m_isInitialized ? Display.getWidth() : m_displayWidth;
	}

	public static int getDisplayHeight() {
		return !m_isInitialized ? Display.getHeight() : m_displayHeight;
	}

	public static boolean isRunning() {
		return m_isInitialized;
	}

	public static void startRendering() {
		m_renderingEnabled = true;
	}

	public static void onGameThreadExited() {
		DebugLog.General.println("GameThread exited.");
		if (RenderThread != null) {
			RenderThread.interrupt();
		}
	}

	public static boolean isCursorVisible() {
		return m_cursorVisible;
	}

	private static class s_performance {
		static final PerformanceProfileFrameProbe renderStep = new PerformanceProfileFrameProbe("RenderThread.renderStep");
		static final PerformanceProfileProbe displayUpdate = new PerformanceProfileProbe("Display.update(true)");
		static final PerformanceProfileProbe spriteRendererPostRender = new PerformanceProfileProbe("SpriteRenderer.postRender");
	}
}
