package zombie.characters;

import java.util.ArrayList;
import java.util.Comparator;
import zombie.iso.IsoUtils;


public abstract class BaseZombieSoundManager {
	protected final ArrayList characters = new ArrayList();
	private final long[] soundTime;
	private final int staleSlotMS;
	private final Comparator comp = new Comparator(){
    
    public int compare(IsoZombie var1, IsoZombie var2) {
        float var3 = BaseZombieSoundManager.this.getClosestListener(var1.x, var1.y, var1.z);
        float var4 = BaseZombieSoundManager.this.getClosestListener(var2.x, var2.y, var2.z);
        if (var3 > var4) {
            return 1;
        } else {
            return var3 < var4 ? -1 : 0;
        }
    }
};

	public BaseZombieSoundManager(int int1, int int2) {
		this.soundTime = new long[int1];
		this.staleSlotMS = int2;
	}

	public void addCharacter(IsoZombie zombie) {
		if (!this.characters.contains(zombie)) {
			this.characters.add(zombie);
		}
	}

	public void update() {
		if (!this.characters.isEmpty()) {
			this.characters.sort(this.comp);
			long long1 = System.currentTimeMillis();
			for (int int1 = 0; int1 < this.soundTime.length && int1 < this.characters.size(); ++int1) {
				IsoZombie zombie = (IsoZombie)this.characters.get(int1);
				if (zombie.getCurrentSquare() != null) {
					int int2 = this.getFreeSoundSlot(long1);
					if (int2 == -1) {
						break;
					}

					this.playSound(zombie);
					this.soundTime[int2] = long1;
				}
			}

			this.postUpdate();
			this.characters.clear();
		}
	}

	public abstract void playSound(IsoZombie zombie);

	public abstract void postUpdate();

	private float getClosestListener(float float1, float float2, float float3) {
		float float4 = Float.MAX_VALUE;
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoPlayer player = IsoPlayer.players[int1];
			if (player != null && player.getCurrentSquare() != null) {
				float float5 = player.getX();
				float float6 = player.getY();
				float float7 = player.getZ();
				float float8 = IsoUtils.DistanceToSquared(float5, float6, float7 * 3.0F, float1, float2, float3 * 3.0F);
				if (player.Traits.HardOfHearing.isSet()) {
					float8 *= 4.5F;
				}

				if (float8 < float4) {
					float4 = float8;
				}
			}
		}

		return float4;
	}

	private int getFreeSoundSlot(long long1) {
		long long2 = Long.MAX_VALUE;
		int int1 = -1;
		for (int int2 = 0; int2 < this.soundTime.length; ++int2) {
			if (this.soundTime[int2] < long2) {
				long2 = this.soundTime[int2];
				int1 = int2;
			}
		}

		if (long1 - long2 < (long)this.staleSlotMS) {
			return -1;
		} else {
			return int1;
		}
	}
}
