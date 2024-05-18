package zombie.iso;

import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.iso.objects.IsoTree;


public class WorldReuserThread {
	public static WorldReuserThread instance = new WorldReuserThread();
	private ConcurrentLinkedQueue reuseGridSquares = new ConcurrentLinkedQueue();
	public Stack objectsToReuse = new Stack();
	public Stack treesToReuse = new Stack();
	Thread worldReuser;
	public boolean finished;

	public void run() {
		this.worldReuser = new Thread(new Runnable(){
			
			public void run() {
				while (!WorldReuserThread.this.finished) {
					WorldReuserThread.this.testReuseChunk();
					WorldReuserThread.this.reconcileReuseObjects();
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException interruptedException) {
						interruptedException.printStackTrace();
					}
				}
			}
		});
		this.worldReuser.setName("WorldReuser");
		this.worldReuser.setDaemon(true);
		this.worldReuser.start();
	}

	public void reconcileReuseObjects() {
		synchronized (this.objectsToReuse) {
			if (!this.objectsToReuse.isEmpty()) {
				synchronized (CellLoader.isoObjectCache) {
					CellLoader.isoObjectCache.addAll(this.objectsToReuse);
				}

				this.objectsToReuse.clear();
			}
		}
		synchronized (this.treesToReuse) {
			if (!this.treesToReuse.isEmpty()) {
				synchronized (CellLoader.isoTreeCache) {
					CellLoader.isoTreeCache.addAll(this.treesToReuse);
				}

				this.treesToReuse.clear();
			}
		}
	}

	public void testReuseChunk() {
		for (IsoChunk chunk = (IsoChunk)this.reuseGridSquares.poll(); chunk != null; chunk = (IsoChunk)this.reuseGridSquares.poll()) {
			if (Core.bDebug) {
				if (ChunkSaveWorker.instance.toSaveQueue.contains(chunk)) {
					DebugLog.log("ERROR: reusing chunk that needs to be saved");
				}

				if (IsoChunkMap.chunkStore.contains(chunk)) {
					DebugLog.log("ERROR: reusing chunk in chunkStore");
				}

				if (!chunk.refs.isEmpty()) {
					DebugLog.log("ERROR: reusing chunk with refs");
				}
			}

			if (Core.bDebug) {
			}

			this.reuseGridSquares(chunk);
		}
	}

	public void addReuseChunk(IsoChunk chunk) {
		this.reuseGridSquares.add(chunk);
	}

	public void reuseGridSquares(IsoChunk chunk) {
		byte byte1 = 100;
		for (int int1 = 0; int1 < 8; ++int1) {
			for (int int2 = 0; int2 < byte1; ++int2) {
				IsoGridSquare square = chunk.squares[int1][int2];
				if (square != null) {
					for (int int3 = 0; int3 < square.getObjects().size(); ++int3) {
						IsoObject object = (IsoObject)square.getObjects().get(int3);
						if (object instanceof IsoTree) {
							object.reset();
							synchronized (this.treesToReuse) {
								this.treesToReuse.add((IsoTree)object);
							}
						} else if (object instanceof IsoObject && object.getObjectName().equals("IsoObject")) {
							object.reset();
							synchronized (this.objectsToReuse) {
								this.objectsToReuse.add(object);
							}
						} else {
							object.reuseGridSquare();
						}
					}

					square.discard();
					chunk.squares[int1][int2] = null;
				}
			}
		}

		chunk.resetForStore();
		IsoChunkMap.chunkStore.add(chunk);
	}
}
