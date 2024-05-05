package zombie.network;

import java.util.Stack;


public class ZomboidNetDataPool {
	public static ZomboidNetDataPool instance = new ZomboidNetDataPool();
	Stack Pool = new Stack();
	Stack LongPool = new Stack();

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
