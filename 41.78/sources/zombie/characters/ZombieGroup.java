package zombie.characters;

import java.util.ArrayList;
import zombie.SandboxOptions;
import zombie.ai.ZombieGroupManager;
import zombie.iso.IsoUtils;


public final class ZombieGroup {
	private final ArrayList members = new ArrayList();
	public float lastSpreadOutTime;

	public ZombieGroup reset() {
		this.members.clear();
		this.lastSpreadOutTime = -1.0F;
		return this;
	}

	public void add(IsoZombie zombie) {
		if (!this.members.contains(zombie)) {
			if (zombie.group != null) {
				zombie.group.remove(zombie);
			}

			this.members.add(zombie);
			zombie.group = this;
		}
	}

	public void remove(IsoZombie zombie) {
		this.members.remove(zombie);
		zombie.group = null;
	}

	public IsoZombie getLeader() {
		return this.members.isEmpty() ? null : (IsoZombie)this.members.get(0);
	}

	public boolean isEmpty() {
		return this.members.isEmpty();
	}

	public int size() {
		return this.members.size();
	}

	public void update() {
		int int1 = SandboxOptions.instance.zombieConfig.RallyTravelDistance.getValue();
		for (int int2 = 0; int2 < this.members.size(); ++int2) {
			IsoZombie zombie = (IsoZombie)this.members.get(int2);
			float float1 = 0.0F;
			if (int2 > 0) {
				float1 = IsoUtils.DistanceToSquared(((IsoZombie)this.members.get(0)).getX(), ((IsoZombie)this.members.get(0)).getY(), zombie.getX(), zombie.getY());
			}

			if (zombie.group != this || float1 > (float)(int1 * int1) || !ZombieGroupManager.instance.shouldBeInGroup(zombie)) {
				if (zombie.group == this) {
					zombie.group = null;
				}

				this.members.remove(int2--);
			}
		}
	}
}
