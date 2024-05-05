package zombie.iso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclesDB2;


public class ChunkSaveWorker {
	public static ChunkSaveWorker instance = new ChunkSaveWorker();
	private final ArrayList tempList = new ArrayList();
	public ConcurrentLinkedQueue toSaveQueue = new ConcurrentLinkedQueue();
	public boolean bSaving;

	public void Update(IsoChunk chunk) {
		if (!GameServer.bServer) {
			IsoChunk chunk2 = null;
			IsoChunk chunk3 = null;
			this.bSaving = !this.toSaveQueue.isEmpty();
			if (this.bSaving) {
				if (chunk != null) {
					Iterator iterator = this.toSaveQueue.iterator();
					while (iterator.hasNext()) {
						chunk3 = (IsoChunk)iterator.next();
						if (chunk3.wx == chunk.wx && chunk3.wy == chunk.wy) {
							chunk2 = chunk3;
							break;
						}
					}
				}

				if (chunk2 == null) {
					chunk2 = (IsoChunk)this.toSaveQueue.poll();
				} else {
					this.toSaveQueue.remove(chunk2);
				}

				if (chunk2 != null) {
					try {
						chunk2.Save(false);
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		}
	}

	public void SaveNow(ArrayList arrayList) {
		this.tempList.clear();
		for (IsoChunk chunk = (IsoChunk)this.toSaveQueue.poll(); chunk != null; chunk = (IsoChunk)this.toSaveQueue.poll()) {
			boolean boolean1 = false;
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				IsoChunk chunk2 = (IsoChunk)arrayList.get(int1);
				if (chunk.wx == chunk2.wx && chunk.wy == chunk2.wy) {
					try {
						chunk.Save(false);
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}

					boolean1 = true;
					break;
				}
			}

			if (!boolean1) {
				this.tempList.add(chunk);
			}
		}

		for (int int2 = 0; int2 < this.tempList.size(); ++int2) {
			this.toSaveQueue.add((IsoChunk)this.tempList.get(int2));
		}

		this.tempList.clear();
	}

	public void SaveNow() {
		DebugLog.log("EXITDEBUG: ChunkSaveWorker.SaveNow 1");
		for (IsoChunk chunk = (IsoChunk)this.toSaveQueue.poll(); chunk != null; chunk = (IsoChunk)this.toSaveQueue.poll()) {
			try {
				DebugLog.log("EXITDEBUG: ChunkSaveWorker.SaveNow 2 (ch=" + chunk.wx + ", " + chunk.wy + ")");
				chunk.Save(false);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		this.bSaving = false;
		DebugLog.log("EXITDEBUG: ChunkSaveWorker.SaveNow 3");
	}

	public void Add(IsoChunk chunk) {
		if (Core.getInstance().isNoSave()) {
			for (int int1 = 0; int1 < chunk.vehicles.size(); ++int1) {
				VehiclesDB2.instance.updateVehicle((BaseVehicle)chunk.vehicles.get(int1));
			}
		}

		if (!this.toSaveQueue.contains(chunk)) {
			this.toSaveQueue.add(chunk);
		}
	}
}
