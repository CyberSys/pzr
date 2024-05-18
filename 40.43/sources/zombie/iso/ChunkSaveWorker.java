package zombie.iso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.GameWindow;
import zombie.core.Core;
import zombie.inventory.ItemContainer;
import zombie.network.GameServer;


public class ChunkSaveWorker {
	public static ChunkSaveWorker instance = new ChunkSaveWorker();
	public ConcurrentLinkedQueue toSaveQueue = new ConcurrentLinkedQueue();
	public Stack toSaveContainers = new Stack();
	public Stack toLoadContainers = new Stack();
	public boolean bSaving;
	private final ArrayList tempList = new ArrayList();

	public void LoadContainers() throws IOException {
		for (int int1 = 0; int1 < this.toLoadContainers.size(); ++int1) {
			ItemContainer itemContainer = (ItemContainer)this.toLoadContainers.get(int1);
			itemContainer.doLoadActual();
		}

		this.toLoadContainers.clear();
	}

	public void SaveContainers() throws IOException {
		for (int int1 = 0; int1 < this.toSaveContainers.size(); ++int1) {
			ItemContainer itemContainer = (ItemContainer)this.toSaveContainers.get(int1);
			File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_con_" + itemContainer.ID + ".bin");
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			try {
				synchronized (SliceY.SliceBuffer) {
					SliceY.SliceBuffer.rewind();
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
					itemContainer.save(SliceY.SliceBuffer, false);
					bufferedOutputStream.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
					bufferedOutputStream.close();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			} finally {
				fileOutputStream.close();
			}
		}

		this.toSaveContainers.clear();
	}

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

				if (chunk2 != null) {
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

					try {
						instance.SaveContainers();
					} catch (Exception exception2) {
						exception2.printStackTrace();
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
			this.toSaveQueue.add(this.tempList.get(int2));
		}

		this.tempList.clear();
	}

	public void SaveNow() {
		for (IsoChunk chunk = (IsoChunk)this.toSaveQueue.poll(); chunk != null; chunk = (IsoChunk)this.toSaveQueue.poll()) {
			try {
				chunk.Save(false);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		this.bSaving = false;
	}

	public void Add(IsoChunk chunk) {
		if (!this.toSaveQueue.contains(chunk)) {
			this.toSaveQueue.add(chunk);
		}
	}
}
