package zombie.spnetwork;

import java.util.ArrayDeque;


public final class ZomboidNetDataPool {
	public static ZomboidNetDataPool instance = new ZomboidNetDataPool();
	private final ArrayDeque Pool = new ArrayDeque();

	public ZomboidNetData get() {
		synchronized (this.Pool) {
			return this.Pool.isEmpty() ? new ZomboidNetData() : (ZomboidNetData)this.Pool.pop();
		}
	}

	public void discard(ZomboidNetData zomboidNetData) {
		zomboidNetData.reset();
		if (zomboidNetData.buffer.capacity() == 2048) {
			synchronized (this.Pool) {
				this.Pool.add(zomboidNetData);
			}
		}
	}

	public ZomboidNetData getLong(int int1) {
		return new ZomboidNetData(int1);
	}
}
