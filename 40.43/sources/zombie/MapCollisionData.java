package zombie;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.gameStates.IngameState;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLot;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaChunk;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.popman.ZombiePopulationManager;
import zombie.ui.UIManager;


public final class MapCollisionData {
	public static final MapCollisionData instance = new MapCollisionData();
	public static final byte BIT_SOLID = 1;
	public static final byte BIT_WALLN = 2;
	public static final byte BIT_WALLW = 4;
	public static final byte BIT_WATER = 8;
	public static final byte BIT_ROOM = 16;
	private static final int SQUARES_PER_CHUNK = 10;
	private static final int CHUNKS_PER_CELL = 30;
	private static final int SQUARES_PER_CELL = 300;
	private boolean bClient;
	private boolean bPaused;
	private boolean bNoSave;
	public final Object renderLock = new Object();
	private final Stack freePathTasks = new Stack();
	private final ConcurrentLinkedQueue pathTaskQueue = new ConcurrentLinkedQueue();
	private final ConcurrentLinkedQueue pathResultQueue = new ConcurrentLinkedQueue();
	private final MapCollisionData.Sync sync = new MapCollisionData.Sync();
	private static int[] curXY = new int[2];
	private MapCollisionData.MCDThread thread;
	private long lastUpdate;
	private final byte[] squares = new byte[100];
	private final int SQUARE_UPDATE_SIZE = 9;
	private final ByteBuffer squareUpdateBuffer = ByteBuffer.allocateDirect(1024);

	private static native void n_init(int int1, int int2, int int3, int int4);

	private static native void n_chunkUpdateTask(int int1, int int2, byte[] byteArray);

	private static native void n_squareUpdateTask(int int1, ByteBuffer byteBuffer);

	private static native int n_pathTask(int int1, int int2, int int3, int int4, int[] intArray);

	private static native boolean n_hasDataForThread();

	private static native boolean n_shouldWait();

	private static native void n_update();

	private static native void n_save();

	private static native void n_stop();

	private static native void n_setGameState(String string, boolean boolean1);

	private static native void n_setGameState(String string, double double1);

	private static native void n_setGameState(String string, float float1);

	private static native void n_setGameState(String string, int int1);

	private static native void n_setGameState(String string, String string2);

	private static native void n_initMetaGrid(int int1, int int2, int int3, int int4);

	private static native void n_initMetaCell(int int1, int int2, String string);

	private static native void n_initMetaChunk(int int1, int int2, int int3, int int4, int int5);

	public void init(IsoMetaGrid metaGrid) {
		this.bClient = GameClient.bClient;
		if (!this.bClient) {
			int int1 = metaGrid.getMinX();
			int int2 = metaGrid.getMinY();
			int int3 = metaGrid.getWidth();
			int int4 = metaGrid.getHeight();
			n_setGameState("Core.GameMode", Core.getInstance().getGameMode());
			n_setGameState("Core.GameSaveWorld", Core.GameSaveWorld);
			n_setGameState("Core.bLastStand", Core.bLastStand);
			n_setGameState("Core.noSave", this.bNoSave = Core.getInstance().isNoSave());
			n_setGameState("GameWindow.CacheDir", GameWindow.getCacheDir());
			n_setGameState("GameWindow.GameModeCacheDir", GameWindow.getGameModeCacheDir());
			n_setGameState("GameWindow.SaveDir", GameWindow.getSaveDir());
			n_setGameState("SandboxOptions.Distribution", SandboxOptions.instance.Distribution.getValue());
			n_setGameState("SandboxOptions.Zombies", SandboxOptions.instance.Zombies.getValue());
			n_setGameState("World.ZombiesDisabled", IsoWorld.getZombiesDisabled());
			n_setGameState("PAUSED", this.bPaused = true);
			n_initMetaGrid(int1, int2, int3, int4);
			for (int int5 = int2; int5 < int2 + int4; ++int5) {
				for (int int6 = int1; int6 < int1 + int3; ++int6) {
					IsoMetaCell metaCell = metaGrid.getCellData(int6, int5);
					n_initMetaCell(int6, int5, (String)IsoLot.InfoFileNames.get("chunkdata_" + int6 + "_" + int5 + ".bin"));
					if (metaCell != null) {
						for (int int7 = 0; int7 < 30; ++int7) {
							for (int int8 = 0; int8 < 30; ++int8) {
								IsoMetaChunk metaChunk = metaCell.getChunk(int8, int7);
								if (metaChunk != null) {
									n_initMetaChunk(int6, int5, int8, int7, metaChunk.getUnadjustedZombieIntensity());
								}
							}
						}
					}
				}
			}

			n_init(int1, int2, int3, int4);
		}
	}

	public void start() {
		if (!this.bClient) {
			if (this.thread == null) {
				this.thread = new MapCollisionData.MCDThread();
				this.thread.setDaemon(true);
				this.thread.setName("MapCollisionDataJNI");
				this.thread.start();
			}
		}
	}

	public void updateMain() {
		if (!this.bClient) {
			for (MapCollisionData.PathTask pathTask = (MapCollisionData.PathTask)this.pathResultQueue.poll(); pathTask != null; pathTask = (MapCollisionData.PathTask)this.pathResultQueue.poll()) {
				pathTask.result.finished(pathTask.status, pathTask.curX, pathTask.curY);
				pathTask.release();
			}

			long long1 = System.currentTimeMillis();
			if (long1 - this.lastUpdate > 10000L) {
				this.lastUpdate = long1;
				this.notifyThread();
			}
		}
	}

	public boolean hasDataForThread() {
		if (this.squareUpdateBuffer.position() > 0) {
			try {
				n_squareUpdateTask(this.squareUpdateBuffer.position() / 9, this.squareUpdateBuffer);
			} finally {
				this.squareUpdateBuffer.clear();
			}
		}

		return n_hasDataForThread();
	}

	public void updateGameState() {
		boolean boolean1 = Core.getInstance().isNoSave();
		if (this.bNoSave != boolean1) {
			this.bNoSave = boolean1;
			n_setGameState("Core.noSave", this.bNoSave);
		}

		boolean boolean2 = UIManager.getSpeedControls() != null && UIManager.getSpeedControls().getCurrentGameSpeed() == 0;
		if (GameWindow.states.current != IngameState.instance) {
			boolean2 = true;
		}

		if (GameServer.bServer) {
			boolean2 = IngameState.instance.Paused;
		}

		if (boolean2 != this.bPaused) {
			this.bPaused = boolean2;
			n_setGameState("PAUSED", this.bPaused);
		}
	}

	public void notifyThread() {
		synchronized (this.thread.notifier) {
			this.thread.notifier.notify();
		}
	}

	public void addChunkToWorld(IsoChunk chunk) {
		if (!this.bClient) {
			for (int int1 = 0; int1 < 10; ++int1) {
				for (int int2 = 0; int2 < 10; ++int2) {
					IsoGridSquare square = chunk.getGridSquare(int2, int1, 0);
					if (square == null) {
						this.squares[int2 + int1 * 10] = 1;
					} else {
						byte byte1 = 0;
						if (this.isSolid(square)) {
							byte1 = (byte)(byte1 | 1);
						}

						if (this.isBlockedN(square)) {
							byte1 = (byte)(byte1 | 2);
						}

						if (this.isBlockedW(square)) {
							byte1 = (byte)(byte1 | 4);
						}

						if (this.isWater(square)) {
							byte1 = (byte)(byte1 | 8);
						}

						if (this.isRoom(square)) {
							byte1 = (byte)(byte1 | 16);
						}

						this.squares[int2 + int1 * 10] = byte1;
					}
				}
			}

			n_chunkUpdateTask(chunk.wx, chunk.wy, this.squares);
		}
	}

	public void removeChunkFromWorld(IsoChunk chunk) {
		if (!this.bClient) {
			;
		}
	}

	public void squareChanged(IsoGridSquare square) {
		if (!this.bClient) {
			try {
				byte byte1 = 0;
				if (this.isSolid(square)) {
					byte1 = (byte)(byte1 | 1);
				}

				if (this.isBlockedN(square)) {
					byte1 = (byte)(byte1 | 2);
				}

				if (this.isBlockedW(square)) {
					byte1 = (byte)(byte1 | 4);
				}

				if (this.isWater(square)) {
					byte1 = (byte)(byte1 | 8);
				}

				if (this.isRoom(square)) {
					byte1 = (byte)(byte1 | 16);
				}

				this.squareUpdateBuffer.putInt(square.x);
				this.squareUpdateBuffer.putInt(square.y);
				this.squareUpdateBuffer.put(byte1);
				if (this.squareUpdateBuffer.remaining() < 9) {
					n_squareUpdateTask(this.squareUpdateBuffer.position() / 9, this.squareUpdateBuffer);
					this.squareUpdateBuffer.clear();
				}
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		}
	}

	public void save() {
		if (!this.bClient) {
			ZombiePopulationManager.instance.beginSaveRealZombies();
			this.thread.bSave = true;
			synchronized (this.thread.notifier) {
				this.thread.notifier.notify();
			}

			while (this.thread.bSave) {
				try {
					Thread.sleep(5L);
				} catch (InterruptedException interruptedException) {
				}
			}

			ZombiePopulationManager.instance.endSaveRealZombies();
		}
	}

	public void stop() {
		if (!this.bClient) {
			this.thread.bStop = true;
			synchronized (this.thread.notifier) {
				this.thread.notifier.notify();
			}

			while (this.thread.isAlive()) {
				try {
					Thread.sleep(5L);
				} catch (InterruptedException interruptedException) {
				}
			}

			n_stop();
			this.thread = null;
			this.pathTaskQueue.clear();
			this.pathResultQueue.clear();
			this.squareUpdateBuffer.clear();
		}
	}

	private boolean isSolid(IsoGridSquare square) {
		boolean boolean1 = square.isSolid() || square.isSolidTrans();
		if (square.HasStairs()) {
			boolean1 = true;
		}

		if (square.Is(IsoFlagType.water)) {
			boolean1 = false;
		}

		if (square.Has(IsoObjectType.tree)) {
			boolean1 = false;
		}

		return boolean1;
	}

	private boolean isBlockedN(IsoGridSquare square) {
		if (square.Is(IsoFlagType.HoppableN)) {
			return false;
		} else {
			boolean boolean1 = square.Is(IsoFlagType.collideN);
			if (square.Has(IsoObjectType.doorFrN)) {
				boolean1 = true;
			}

			if (square.getProperties().Is("DoorWallN")) {
				boolean1 = true;
			}

			if (square.Has(IsoObjectType.windowFN)) {
				boolean1 = true;
			}

			if (square.Is(IsoFlagType.windowN)) {
				boolean1 = true;
			}

			if (square.getProperties().Is(IsoFlagType.WindowN)) {
				boolean1 = true;
			}

			return boolean1;
		}
	}

	private boolean isBlockedW(IsoGridSquare square) {
		if (square.Is(IsoFlagType.HoppableW)) {
			return false;
		} else {
			boolean boolean1 = square.Is(IsoFlagType.collideW);
			if (square.Has(IsoObjectType.doorFrW)) {
				boolean1 = true;
			}

			if (square.getProperties().Is("DoorWallW")) {
				boolean1 = true;
			}

			if (square.Has(IsoObjectType.windowFW)) {
				boolean1 = true;
			}

			if (square.Is(IsoFlagType.windowW)) {
				boolean1 = true;
			}

			if (square.getProperties().Is(IsoFlagType.WindowW)) {
				boolean1 = true;
			}

			return boolean1;
		}
	}

	private boolean isWater(IsoGridSquare square) {
		boolean boolean1 = square.Is(IsoFlagType.water);
		return boolean1;
	}

	private boolean isRoom(IsoGridSquare square) {
		return square.getRoom() != null;
	}

	private static void writeToStdErr(String string) {
		System.err.println(string);
	}

	static class Sync {
		private int fps = 10;
		private long period;
		private long excess;
		private long beforeTime;
		private long overSleepTime;

		Sync() {
			this.period = 1000000000L / (long)this.fps;
			this.beforeTime = System.nanoTime();
			this.overSleepTime = 0L;
		}

		void begin() {
			this.beforeTime = System.nanoTime();
			this.overSleepTime = 0L;
		}

		void startFrame() {
			this.excess = 0L;
		}

		void endFrame() {
			long long1 = System.nanoTime();
			long long2 = long1 - this.beforeTime;
			long long3 = this.period - long2 - this.overSleepTime;
			if (long3 > 0L) {
				try {
					Thread.sleep(long3 / 1000000L);
				} catch (InterruptedException interruptedException) {
				}

				this.overSleepTime = System.nanoTime() - long1 - long3;
			} else {
				this.excess -= long3;
				this.overSleepTime = 0L;
			}

			this.beforeTime = System.nanoTime();
		}
	}

	private final class MCDThread extends Thread {
		public boolean bStop;
		public volatile boolean bSave;
		public final Object notifier;
		public Queue pathTasks;

		private MCDThread() {
			this.notifier = new Object();
			this.pathTasks = new ArrayDeque();
		}

		public void run() {
			while (!this.bStop) {
				try {
					this.runInner();
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
				}
			}
		}

		private void runInner() {
			MapCollisionData.this.sync.startFrame();
			synchronized (MapCollisionData.this.renderLock) {
				MapCollisionData.PathTask pathTask = (MapCollisionData.PathTask)MapCollisionData.this.pathTaskQueue.poll();
				while (true) {
					if (pathTask == null) {
						if (this.bSave) {
							MapCollisionData.n_save();
							ZombiePopulationManager.instance.save();
							this.bSave = false;
						}

						MapCollisionData.n_update();
						ZombiePopulationManager.instance.updateThread();
						break;
					}

					pathTask.execute();
					pathTask.release();
					pathTask = (MapCollisionData.PathTask)MapCollisionData.this.pathTaskQueue.poll();
				}
			}
			MapCollisionData.this.sync.endFrame();
			while (this.shouldWait()) {
				synchronized (this.notifier) {
					try {
						this.notifier.wait();
					} catch (InterruptedException interruptedException) {
					}
				}
			}
		}

		private boolean shouldWait() {
			if (!this.bStop && !this.bSave) {
				if (!MapCollisionData.n_shouldWait()) {
					return false;
				} else if (!ZombiePopulationManager.instance.shouldWait()) {
					return false;
				} else {
					return MapCollisionData.this.pathTaskQueue.isEmpty() && this.pathTasks.isEmpty();
				}
			} else {
				return false;
			}
		}

		MCDThread(Object object) {
			this();
		}
	}

	private final class PathTask {
		public int startX;
		public int startY;
		public int endX;
		public int endY;
		public int curX;
		public int curY;
		public int status;
		public MapCollisionData.IPathResult result;
		public boolean myThread;

		public void init(int int1, int int2, int int3, int int4, MapCollisionData.IPathResult iPathResult) {
			this.startX = int1;
			this.startY = int2;
			this.endX = int3;
			this.endY = int4;
			this.status = 0;
			this.result = iPathResult;
		}

		public void execute() {
			this.status = MapCollisionData.n_pathTask(this.startX, this.startY, this.endX, this.endY, MapCollisionData.curXY);
			this.curX = MapCollisionData.curXY[0];
			this.curY = MapCollisionData.curXY[1];
			if (this.myThread) {
				this.result.finished(this.status, this.curX, this.curY);
			} else {
				MapCollisionData.this.pathResultQueue.add(this);
			}
		}

		public void release() {
			MapCollisionData.this.freePathTasks.push(this);
		}
	}

	public interface IPathResult {

		void finished(int int1, int int2, int int3);
	}
}
