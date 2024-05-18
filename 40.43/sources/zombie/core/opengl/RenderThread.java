package zombie.core.opengl;

import java.util.concurrent.locks.ReentrantLock;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.ui.FPSGraph;


public class RenderThread {
	public static Thread MainThread;
	public static Thread RenderThread;
	public static boolean bReadyForFrame = false;
	public static final Object RenderThreadNotifier = new Object();
	private static boolean InRenderingBlock;
	private static boolean bRequestReleaseContext = false;
	private static final ReentrantLock SwitchThreadLock = new ReentrantLock(true);
	private static Thread ContextThread = null;
	private static final Object ContextCheckLock = new Object();

	public static void init() {
		MainThread = Thread.currentThread();
		if (!Core.bMultithreadedRendering) {
			SwitchThreadLock.lock();
			ContextThread = MainThread;
		} else {
			Core.bLoadedWithMultithreaded = true;
			RenderThread = new Thread(new Runnable(){
				
				public void run() {
					boolean var1 = true;
					while (var1) {
						boolean var2 = false;
						synchronized (zombie.core.opengl.RenderThread.RenderThreadNotifier) {
							while (!zombie.core.opengl.RenderThread.bReadyForFrame && !zombie.core.opengl.RenderThread.bRequestReleaseContext) {
								try {
									zombie.core.opengl.RenderThread.RenderThreadNotifier.wait();
								} catch (InterruptedException var11) {
								}
							}

							if (zombie.core.opengl.RenderThread.bRequestReleaseContext) {
								zombie.core.opengl.RenderThread.bRequestReleaseContext = false;
								var2 = true;
							} else {
								zombie.core.opengl.RenderThread.bReadyForFrame = false;
							}
						}

						if (var2) {
							zombie.core.opengl.RenderThread.returnContext();
						} else {
							synchronized (SpriteRenderer.instance.states) {
								SpriteRenderer.RenderState var4 = SpriteRenderer.instance.states[1];
								SpriteRenderer.instance.states[1] = SpriteRenderer.instance.states[2];
								SpriteRenderer.instance.states[2] = var4;
								SpriteRenderer.instance.states[1].bRendered = true;
							}

							if (!SpriteRenderer.instance.states[2].bRendered) {
								zombie.core.opengl.RenderThread.borrowContext();
								assert zombie.core.opengl.RenderThread.ContextThread == zombie.core.opengl.RenderThread.RenderThread;
								assert zombie.core.opengl.RenderThread.SwitchThreadLock.isHeldByCurrentThread();
								zombie.core.opengl.RenderThread.InRenderingBlock = true;
								try {
									if (Core.bDebug) {
										long var3 = System.nanoTime();
										SpriteRenderer.instance.postRender();
										long var5 = System.nanoTime();
										Display.update(false);
										long var7 = System.nanoTime();
										if (var5 - var3 > 50000000L) {
										}
									} else {
										SpriteRenderer.instance.postRender();
										Display.update(false);
									}
								} catch (Exception var9) {
									var9.printStackTrace();
								}

								zombie.core.opengl.RenderThread.InRenderingBlock = false;
								Core.getInstance().setLastRenderedFBO(SpriteRenderer.instance.states[2].fbo);
								if (Core.bDebug && FPSGraph.instance != null) {
									FPSGraph.instance.addRender(System.currentTimeMillis());
								}
							}
						}
					}
				}
			});

			RenderThread.setPriority(4);
			RenderThread.setDaemon(true);
			RenderThread.setName("Render Thread");
			RenderThread.start();
		}
	}

	public static void Ready() {
		SpriteRenderer.instance.pushFrameDown();
		if (Core.bLoadedWithMultithreaded) {
			synchronized (RenderThreadNotifier) {
				bReadyForFrame = true;
				RenderThreadNotifier.notify();
			}
		} else {
			SpriteRenderer.RenderState renderState = SpriteRenderer.instance.states[1];
			SpriteRenderer.instance.states[1] = SpriteRenderer.instance.states[2];
			SpriteRenderer.instance.states[2] = renderState;
			SpriteRenderer.instance.states[1].bRendered = true;
			if (ContextThread != MainThread) {
				borrowContext();
			}

			try {
				SpriteRenderer.instance.postRender();
				Display.update(false);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			Core.getInstance().setLastRenderedFBO(SpriteRenderer.instance.states[2].fbo);
			bReadyForFrame = false;
			if (bRequestReleaseContext) {
				returnContext();
				bRequestReleaseContext = false;
			}
		}
	}

	public static void borrowContext() {
		if (MainThread != null) {
			Thread thread = Thread.currentThread();
			synchronized (ContextCheckLock) {
				if (Core.bLoadedWithMultithreaded) {
					if (thread == RenderThread && ContextThread == RenderThread) {
						return;
					}

					if (thread != RenderThread) {
						synchronized (RenderThreadNotifier) {
							bRequestReleaseContext = true;
							RenderThreadNotifier.notify();
						}
					}
				} else if (thread != MainThread) {
					bRequestReleaseContext = true;
				}
			}

			SwitchThreadLock.lock();
			if (SwitchThreadLock.getHoldCount() <= 1) {
				synchronized (ContextCheckLock) {
					assert ContextThread == null;
					ContextThread = thread;
				}

				try {
					Display.makeCurrent();
				} catch (LWJGLException lWJGLException) {
					lWJGLException.printStackTrace();
				}
			}
		}
	}

	public static void returnContext() {
		if (SwitchThreadLock.isHeldByCurrentThread()) {
			if (SwitchThreadLock.getHoldCount() == 1) {
				assert Thread.currentThread() != RenderThread || !InRenderingBlock;
				try {
					Display.releaseContext();
				} catch (LWJGLException lWJGLException) {
					lWJGLException.printStackTrace();
				}

				synchronized (ContextCheckLock) {
					ContextThread = null;
				}
			}

			SwitchThreadLock.unlock();
		}
	}
}
