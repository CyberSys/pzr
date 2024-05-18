package zombie;

import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.astar.AStarPathMap;
import zombie.ai.astar.IPathfinder;
import zombie.ai.astar.Mover;
import zombie.ai.astar.heuristics.ManhattanHeuristic;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.debug.DebugLog;
import zombie.gameStates.IngameState;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;


public class PathfindManager {
	public static PathfindManager instance = new PathfindManager();
	public int MaxThreads = 1;
	public int Current = 0;
	public int maxInBatch = 1;
	public static int Failed = 0;
	public static int Succeeded = 0;
	public static float ZombiePathTime = 0.0F;
	public static float SurvivorPathTime = 0.0F;
	public HashMap Pathfind = new HashMap();
	public static float HighestPathTime = 0.0F;
	public PathfindManager.PathfindJob HighestJob = null;
	HashMap JobMap = new HashMap();
	public PathfindManager.PathfindThread[] threads;

	public boolean allPaused() {
		for (int int1 = 0; int1 < this.MaxThreads; ++int1) {
			if (!this.threads[int1].bPaused || this.threads[int1].processing) {
				return false;
			}
		}

		return true;
	}

	private void run() throws InterruptedException {
		PathfindManager.PathfindThread pathfindThread = (PathfindManager.PathfindThread)Thread.currentThread();
		pathfindThread.map = IsoWorld.instance.CurrentCell.getPathMap();
		pathfindThread.finder = new AStarPathFinder((IsoGameCharacter)null, pathfindThread.map, 50, true, new ManhattanHeuristic(1));
		while (!pathfindThread.bDone) {
			pathfindThread.processing = true;
			int int1 = this.maxInBatch;
			boolean boolean1 = true;
			pathfindThread.processing = true;
			synchronized (pathfindThread.Jobs) {
				while (!pathfindThread.Jobs.isEmpty() && ((PathfindManager.PathfindJob)pathfindThread.Jobs.get(0)).finished) {
					pathfindThread.Jobs.remove(0);
				}

				boolean1 = pathfindThread.Jobs.isEmpty();
			}

			pathfindThread.processing = false;
			if (boolean1) {
				pathfindThread.bPaused = true;
			} else {
				pathfindThread.bPaused = false;
			}

			for (; !boolean1 && int1 > 0; pathfindThread.processing = false) {
				PathfindManager.PathfindJob pathfindJob = null;
				pathfindThread.processing = true;
				synchronized (pathfindThread.Jobs) {
					pathfindJob = (PathfindManager.PathfindJob)pathfindThread.Jobs.get(0);
				}

				pathfindThread.processing = false;
				if (pathfindJob.mover instanceof IsoPlayer) {
					boolean boolean2 = false;
				}

				while (IngameState.AlwaysDebugPathfinding) {
					Thread.sleep(16L);
				}

				do {
					if (!pathfindThread.SwitchingCells) {
						pathfindThread.bPaused = false;
						pathfindJob.Process(pathfindThread.ID, pathfindThread.finder);
					} else {
						pathfindThread.bPaused = true;
					}

					Thread.sleep(16L);
				}		 while (pathfindJob.isNotFinished(pathfindThread.finder));

				while (pathfindThread.SwitchingCells) {
					pathfindThread.bPaused = true;
					Thread.sleep(16L);
				}

				pathfindThread.bPaused = false;
				pathfindThread.processing = true;
				synchronized (pathfindThread.Jobs) {
					pathfindThread.Jobs.remove(0);
					pathfindJob.finished = true;
				}

				pathfindThread.processing = false;
				--int1;
				pathfindThread.processing = true;
				synchronized (pathfindThread.Jobs) {
					boolean1 = pathfindThread.Jobs.isEmpty();
				}
			}

			try {
				Thread.sleep(60L);
			} catch (InterruptedException interruptedException) {
			}
		}
	}

	public AStarPathFinder getFinder() {
		return this.threads[0].finder;
	}

	public void DoDebugJob(int int1, int int2, int int3, int int4, int int5, int int6) {
		while (this.threads[0].processing) {
			try {
				Thread.sleep(1L);
			} catch (InterruptedException interruptedException) {
				Logger.getLogger(PathfindManager.class.getName()).log(Level.SEVERE, (String)null, interruptedException);
			}
		}

		IngameState.AlwaysDebugPathfinding = true;
		this.threads[0].finder.findPathActual((Mover)null, int1, int2, int3, int4, int5, int6);
		this.threads[0].finder.Cycle(0);
		IngameState.AlwaysDebugPathfinding = false;
	}

	public void reset() {
		for (int int1 = 0; int1 < this.threads.length; ++int1) {
			this.threads[int1] = new PathfindManager.PathfindThread(new Runnable(){
				
				public void run() {
					try {
						PathfindManager.instance.run();
					} catch (InterruptedException var2) {
						Logger.getLogger(LOSThread.class.getName()).log(Level.SEVERE, (String)null, var2);
					}
				}
			});

			this.threads[int1].setName("PathFind" + int1);
			this.threads[int1].ID = int1;
			this.threads[int1].setPriority(2);
			this.threads[int1].start();
		}
	}

	public void stop() {
		this.Pathfind.clear();
		this.HighestJob = null;
		for (int int1 = 0; int1 < this.threads.length; ++int1) {
			this.threads[int1].bDone = true;
			while (this.threads[int1].isAlive()) {
			}

			this.threads[int1].Jobs.clear();
			this.threads[int1].reset();
			this.threads[int1] = null;
		}

		this.threads = null;
		this.JobMap.clear();
	}

	public void init() {
		this.MaxThreads = 1;
		this.threads = new PathfindManager.PathfindThread[this.MaxThreads];
		IsoWorld.instance.CurrentCell.InitNodeMap(this.MaxThreads);
		for (int int1 = 0; int1 < this.MaxThreads; ++int1) {
			this.threads[int1] = new PathfindManager.PathfindThread(new Runnable(){
				
				public void run() {
					try {
						PathfindManager.instance.run();
					} catch (InterruptedException var2) {
						Logger.getLogger(LOSThread.class.getName()).log(Level.SEVERE, (String)null, var2);
					}
				}
			});

			this.threads[int1].setName("PathFind" + int1);
			this.threads[int1].ID = int1;
			if (int1 <= 1) {
				this.threads[int1].setPriority(2);
			} else {
				this.threads[int1].setPriority(2);
			}

			this.threads[int1].setDaemon(true);
			this.threads[int1].start();
		}
	}

	public void AddJob(IPathfinder iPathfinder, Mover mover, int int1, int int2, int int3, int int4, int int5, int int6) {
		this.AddJob(iPathfinder, mover, int1, int2, int3, int4, int5, int6, true);
	}

	public void AddJob(IPathfinder iPathfinder, Mover mover, int int1, int int2, int int3, int int4, int int5, int int6, boolean boolean1) {
		float float1 = IsoUtils.DistanceManhatten((float)int1, (float)int2, (float)int4, (float)int5);
		PathfindManager.PathfindJob pathfindJob;
		if (this.JobMap.containsKey(mover)) {
			pathfindJob = (PathfindManager.PathfindJob)this.JobMap.get(mover);
			if (!pathfindJob.finished && pathfindJob.sx == int1 && pathfindJob.sy == int2 && pathfindJob.sz == int3 && pathfindJob.tx == int4 && pathfindJob.ty == int5 && pathfindJob.tz == int6) {
				return;
			}

			pathfindJob.finished = true;
			synchronized (this.threads[0].Jobs) {
				this.threads[0].Jobs.remove(pathfindJob);
			}

			this.JobMap.remove(mover);
		}

		if (mover == IsoCamera.CamCharacter) {
			boolean boolean2 = false;
		}

		if (int6 < IsoWorld.instance.CurrentCell.getMaxFloors() && int6 >= 0) {
			if (int3 < IsoWorld.instance.CurrentCell.getMaxFloors() && int3 >= 0) {
				pathfindJob = new PathfindManager.PathfindJob();
				pathfindJob.mover = mover;
				pathfindJob.sx = int1;
				pathfindJob.sy = int2;
				pathfindJob.sz = int3;
				pathfindJob.tx = int4;
				pathfindJob.ty = int5;
				pathfindJob.tz = int6;
				pathfindJob.bClosest = true;
				pathfindJob.pathfinder = iPathfinder;
				this.JobMap.put(mover, pathfindJob);
				if (IngameState.DebugPathfinding) {
					pathfindJob.Process(0, this.threads[this.Current].finder);
				} else {
					boolean boolean3 = true;
					boolean boolean4 = true;
					boolean boolean5 = true;
					synchronized (this.threads[0].Jobs) {
						this.threads[0].Jobs.add(pathfindJob);
					}
				}
			} else {
				iPathfinder.Failed(mover);
			}
		} else {
			iPathfinder.Failed(mover);
		}
	}

	public void abortJob(Mover mover) {
		PathfindManager.PathfindJob pathfindJob = (PathfindManager.PathfindJob)this.JobMap.remove(mover);
		if (pathfindJob != null) {
			pathfindJob.finished = true;
		}
	}

	public void pause() {
		long long1 = System.nanoTime();
		for (int int1 = 0; int1 < this.MaxThreads; ++int1) {
			this.threads[int1].SwitchingCells = true;
		}

		while (!this.allPaused()) {
			try {
				Thread.sleep(4L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}

		long long2 = System.nanoTime() - long1;
		if ((double)long2 > 5.0E7) {
			DebugLog.log("PathfindManager.pause() took " + (double)long2 / 1000000.0 + "ms");
		}
	}

	public void resume() {
		for (int int1 = 0; int1 < this.MaxThreads; ++int1) {
			this.threads[int1].SwitchingCells = false;
			this.threads[int1].SwitchedCells = true;
		}
	}

	public class PathfindThread extends Thread {
		public boolean bPaused;
		public boolean bDone;
		public boolean Catchup;
		public int ID;
		public Stack Jobs;
		public AStarPathFinder finder;
		public AStarPathMap map;
		private boolean processing;
		public boolean SwitchedCells;
		public boolean SwitchingCells;

		private PathfindThread(Runnable runnable) {
			super(runnable);
			this.bPaused = false;
			this.bDone = false;
			this.Catchup = false;
			this.ID = 0;
			this.Jobs = new Stack();
			this.processing = false;
			this.SwitchedCells = false;
			this.SwitchingCells = false;
		}

		private void reset() {
			this.map = null;
			this.finder = null;
		}

		PathfindThread(Runnable runnable, Object object) {
			this(runnable);
		}
	}

	public class PathfindJob {
		public int sx;
		public int sy;
		public int sz;
		public int tx;
		public int ty;
		public int tz;
		public Mover mover;
		public IPathfinder pathfinder;
		public volatile boolean finished = false;
		public boolean bClosest;

		private void Process(int int1, AStarPathFinder aStarPathFinder) {
			if (this.mover == IsoCamera.CamCharacter) {
				boolean boolean1 = false;
			}

			try {
				aStarPathFinder.findPathActual(this.mover, this.sx, this.sy, this.sz, this.tx, this.ty, this.tz, this.bClosest);
				aStarPathFinder.Cycle(int1, this);
			} catch (Exception exception) {
			}

			if (this.pathfinder != null) {
				if (aStarPathFinder.progress == AStarPathFinder.PathFindProgress.found) {
					this.pathfinder.Succeeded(aStarPathFinder.getPath(), this.mover);
					++PathfindManager.Succeeded;
				} else if (aStarPathFinder.progress != AStarPathFinder.PathFindProgress.notrunning) {
					if (PathfindManager.instance.threads[int1].SwitchedCells) {
						PathfindManager.instance.threads[int1].SwitchedCells = false;
						aStarPathFinder.progress = AStarPathFinder.PathFindProgress.notyetfound;
						return;
					}

					this.pathfinder.Failed(this.mover);
					++PathfindManager.Failed;
				}
			}
		}

		public boolean isNotFinished(AStarPathFinder aStarPathFinder) {
			return aStarPathFinder.progress == AStarPathFinder.PathFindProgress.notyetfound;
		}
	}
}
