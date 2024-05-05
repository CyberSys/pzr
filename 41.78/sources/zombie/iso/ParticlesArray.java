package zombie.iso;

import java.util.ArrayList;
import zombie.debug.DebugLog;


public final class ParticlesArray extends ArrayList {
	private boolean needToUpdate;
	private int ParticleSystemsCount = 0;
	private int ParticleSystemsLast = 0;

	public ParticlesArray() {
		this.ParticleSystemsCount = 0;
		this.ParticleSystemsLast = 0;
		this.needToUpdate = true;
	}

	public synchronized int addParticle(Object object) {
		if (object == null) {
			return -1;
		} else if (this.size() == this.ParticleSystemsCount) {
			this.add(object);
			++this.ParticleSystemsCount;
			this.needToUpdate = true;
			return this.size() - 1;
		} else {
			int int1;
			for (int1 = this.ParticleSystemsLast; int1 < this.size(); ++int1) {
				if (this.get(int1) == null) {
					this.ParticleSystemsLast = int1;
					this.set(int1, object);
					++this.ParticleSystemsCount;
					this.needToUpdate = true;
					return int1;
				}
			}

			for (int1 = 0; int1 < this.ParticleSystemsLast; ++int1) {
				if (this.get(int1) == null) {
					this.ParticleSystemsLast = int1;
					this.set(int1, object);
					++this.ParticleSystemsCount;
					this.needToUpdate = true;
					return int1;
				}
			}

			DebugLog.log("ERROR: ParticlesArray.addParticle has unknown error");
			return -1;
		}
	}

	public synchronized boolean deleteParticle(int int1) {
		if (int1 >= 0 && int1 < this.size() && this.get(int1) != null) {
			this.set(int1, (Object)null);
			--this.ParticleSystemsCount;
			this.needToUpdate = true;
			return true;
		} else {
			return false;
		}
	}

	public synchronized void defragmentParticle() {
		this.needToUpdate = false;
		if (this.ParticleSystemsCount != this.size() && this.size() != 0) {
			int int1 = -1;
			int int2;
			for (int2 = 0; int2 < this.size(); ++int2) {
				if (this.get(int2) == null) {
					int1 = int2;
					break;
				}
			}

			for (int2 = this.size() - 1; int2 >= 0; --int2) {
				if (this.get(int2) != null) {
					this.set(int1, this.get(int2));
					this.set(int2, (Object)null);
					for (int int3 = int1; int3 < this.size(); ++int3) {
						if (this.get(int3) == null) {
							int1 = int3;
							break;
						}
					}

					if (int1 + 1 >= int2) {
						this.ParticleSystemsLast = int1;
						break;
					}
				}
			}
		}
	}

	public synchronized int getCount() {
		return this.ParticleSystemsCount;
	}

	public synchronized boolean getNeedToUpdate() {
		return this.needToUpdate;
	}
}
