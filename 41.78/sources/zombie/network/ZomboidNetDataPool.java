package zombie.network;

import java.util.concurrent.ConcurrentLinkedQueue;


public class ZomboidNetDataPool {
	public static final ZomboidNetDataPool instance = new ZomboidNetDataPool();
	final ConcurrentLinkedQueue Pool = new ConcurrentLinkedQueue();

	public ZomboidNetData get() {
		ZomboidNetData zomboidNetData = (ZomboidNetData)this.Pool.poll();
		return zomboidNetData == null ? new ZomboidNetData() : zomboidNetData;
	}

	public void discard(ZomboidNetData zomboidNetData) {
		zomboidNetData.reset();
		if (zomboidNetData.buffer.capacity() == 2048) {
			this.Pool.add(zomboidNetData);
		}
	}

	public ZomboidNetData getLong(int int1) {
		return new ZomboidNetData(int1);
	}
}
