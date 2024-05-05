package zombie;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoDeadBody;
import zombie.util.Type;


public final class MovingObjectUpdateSchedulerUpdateBucket {
	public int frameMod;
	ArrayList[] buckets;

	public MovingObjectUpdateSchedulerUpdateBucket(int int1) {
		this.init(int1);
	}

	public void init(int int1) {
		this.frameMod = int1;
		this.buckets = new ArrayList[int1];
		for (int int2 = 0; int2 < this.buckets.length; ++int2) {
			this.buckets[int2] = new ArrayList();
		}
	}

	public void clear() {
		for (int int1 = 0; int1 < this.buckets.length; ++int1) {
			ArrayList arrayList = this.buckets[int1];
			arrayList.clear();
		}
	}

	public void remove(IsoMovingObject movingObject) {
		for (int int1 = 0; int1 < this.buckets.length; ++int1) {
			ArrayList arrayList = this.buckets[int1];
			arrayList.remove(movingObject);
		}
	}

	public void add(IsoMovingObject movingObject) {
		int int1 = movingObject.getID() % this.frameMod;
		this.buckets[int1].add(movingObject);
	}

	public void update(int int1) {
		GameTime.getInstance().PerObjectMultiplier = (float)this.frameMod;
		ArrayList arrayList = this.buckets[int1 % this.frameMod];
		for (int int2 = 0; int2 < arrayList.size(); ++int2) {
			IsoMovingObject movingObject = (IsoMovingObject)arrayList.get(int2);
			if (movingObject instanceof IsoDeadBody) {
				IsoWorld.instance.getCell().getRemoveList().add(movingObject);
			} else {
				IsoZombie zombie = (IsoZombie)Type.tryCastTo(movingObject, IsoZombie.class);
				if (zombie != null && VirtualZombieManager.instance.isReused(zombie)) {
					DebugLog.log(DebugType.Zombie, "REUSABLE ZOMBIE IN MovingObjectUpdateSchedulerUpdateBucket IGNORED " + movingObject);
				} else {
					movingObject.preupdate();
					movingObject.update();
				}
			}
		}

		GameTime.getInstance().PerObjectMultiplier = 1.0F;
	}

	public void postupdate(int int1) {
		GameTime.getInstance().PerObjectMultiplier = (float)this.frameMod;
		ArrayList arrayList = this.buckets[int1 % this.frameMod];
		for (int int2 = 0; int2 < arrayList.size(); ++int2) {
			IsoMovingObject movingObject = (IsoMovingObject)arrayList.get(int2);
			IsoZombie zombie = (IsoZombie)Type.tryCastTo(movingObject, IsoZombie.class);
			if (zombie != null && VirtualZombieManager.instance.isReused(zombie)) {
				DebugLog.log(DebugType.Zombie, "REUSABLE ZOMBIE IN MovingObjectUpdateSchedulerUpdateBucket IGNORED " + movingObject);
			} else {
				movingObject.postupdate();
			}
		}

		GameTime.getInstance().PerObjectMultiplier = 1.0F;
	}

	public void removeObject(IsoMovingObject movingObject) {
		for (int int1 = 0; int1 < this.buckets.length; ++int1) {
			ArrayList arrayList = this.buckets[int1];
			arrayList.remove(movingObject);
		}
	}

	public ArrayList getBucket(int int1) {
		return this.buckets[int1 % this.frameMod];
	}
}
