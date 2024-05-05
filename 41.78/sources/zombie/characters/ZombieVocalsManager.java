package zombie.characters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.iso.IsoUtils;
import zombie.network.GameServer;
import zombie.popman.ObjectPool;
import zombie.util.list.PZArrayUtil;


public final class ZombieVocalsManager {
	public static final ZombieVocalsManager instance = new ZombieVocalsManager();
	private final HashSet m_added = new HashSet();
	private final ObjectPool m_objectPool = new ObjectPool(ZombieVocalsManager.ObjectWithDistance::new);
	private final ArrayList m_objects = new ArrayList();
	private final ZombieVocalsManager.Slot[] m_slots;
	private long m_updateMS = 0L;
	private final Comparator comp = new Comparator(){
    
    public int compare(ZombieVocalsManager.ObjectWithDistance var1, ZombieVocalsManager.ObjectWithDistance var2) {
        return Float.compare(var1.distSq, var2.distSq);
    }
};

	public ZombieVocalsManager() {
		byte byte1 = 20;
		this.m_slots = (ZombieVocalsManager.Slot[])PZArrayUtil.newInstance(ZombieVocalsManager.Slot.class, byte1, ZombieVocalsManager.Slot::new);
	}

	public void addCharacter(IsoZombie zombie) {
		if (!this.m_added.contains(zombie)) {
			this.m_added.add(zombie);
			ZombieVocalsManager.ObjectWithDistance objectWithDistance = (ZombieVocalsManager.ObjectWithDistance)this.m_objectPool.alloc();
			objectWithDistance.character = zombie;
			this.m_objects.add(objectWithDistance);
		}
	}

	public void update() {
		if (!GameServer.bServer) {
			long long1 = System.currentTimeMillis();
			if (long1 - this.m_updateMS >= 500L) {
				this.m_updateMS = long1;
				int int1;
				for (int1 = 0; int1 < this.m_slots.length; ++int1) {
					this.m_slots[int1].playing = false;
				}

				if (this.m_objects.isEmpty()) {
					this.stopNotPlaying();
				} else {
					IsoZombie zombie;
					for (int1 = 0; int1 < this.m_objects.size(); ++int1) {
						ZombieVocalsManager.ObjectWithDistance objectWithDistance = (ZombieVocalsManager.ObjectWithDistance)this.m_objects.get(int1);
						zombie = objectWithDistance.character;
						objectWithDistance.distSq = this.getClosestListener(zombie.x, zombie.y, zombie.z);
					}

					this.m_objects.sort(this.comp);
					int1 = PZMath.min(this.m_slots.length, this.m_objects.size());
					int int2;
					int int3;
					for (int3 = 0; int3 < int1; ++int3) {
						zombie = ((ZombieVocalsManager.ObjectWithDistance)this.m_objects.get(int3)).character;
						if (this.shouldPlay(zombie)) {
							int2 = this.getExistingSlot(zombie);
							if (int2 != -1) {
								this.m_slots[int2].playSound(zombie);
							}
						}
					}

					for (int3 = 0; int3 < int1; ++int3) {
						zombie = ((ZombieVocalsManager.ObjectWithDistance)this.m_objects.get(int3)).character;
						if (this.shouldPlay(zombie)) {
							int2 = this.getExistingSlot(zombie);
							if (int2 == -1) {
								int2 = this.getFreeSlot();
								this.m_slots[int2].playSound(zombie);
							}
						}
					}

					this.stopNotPlaying();
					this.postUpdate();
					this.m_added.clear();
					this.m_objectPool.release((List)this.m_objects);
					this.m_objects.clear();
				}
			}
		}
	}

	boolean shouldPlay(IsoZombie zombie) {
		return zombie.getCurrentSquare() != null;
	}

	int getExistingSlot(IsoZombie zombie) {
		for (int int1 = 0; int1 < this.m_slots.length; ++int1) {
			if (this.m_slots[int1].character == zombie) {
				return int1;
			}
		}

		return -1;
	}

	int getFreeSlot() {
		for (int int1 = 0; int1 < this.m_slots.length; ++int1) {
			if (!this.m_slots[int1].playing) {
				return int1;
			}
		}

		return -1;
	}

	void stopNotPlaying() {
		for (int int1 = 0; int1 < this.m_slots.length; ++int1) {
			ZombieVocalsManager.Slot slot = this.m_slots[int1];
			if (!slot.playing) {
				slot.stopPlaying();
				slot.character = null;
			}
		}
	}

	public void postUpdate() {
	}

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

	public void render() {
		if (Core.bDebug) {
		}
	}

	public static void Reset() {
		for (int int1 = 0; int1 < instance.m_slots.length; ++int1) {
			instance.m_slots[int1].stopPlaying();
			instance.m_slots[int1].character = null;
			instance.m_slots[int1].playing = false;
		}
	}

	static final class Slot {
		IsoZombie character = null;
		boolean playing = false;

		void playSound(IsoZombie zombie) {
			if (this.character != null && this.character != zombie && this.character.vocalEvent != 0L) {
				this.character.getEmitter().stopSoundLocal(this.character.vocalEvent);
				this.character.vocalEvent = 0L;
			}

			this.character = zombie;
			this.playing = true;
			if (this.character.vocalEvent == 0L) {
				String string = zombie.isFemale() ? "FemaleZombieCombined" : "MaleZombieCombined";
				if (!zombie.getFMODParameters().parameterList.contains(zombie.parameterZombieState)) {
					zombie.parameterZombieState.update();
					zombie.getFMODParameters().add(zombie.parameterZombieState);
					zombie.parameterCharacterInside.update();
					zombie.getFMODParameters().add(zombie.parameterCharacterInside);
					zombie.parameterPlayerDistance.update();
					zombie.getFMODParameters().add(zombie.parameterPlayerDistance);
				}

				zombie.vocalEvent = zombie.getEmitter().playVocals(string);
			}
		}

		void stopPlaying() {
			if (this.character != null && this.character.vocalEvent != 0L) {
				this.character.getEmitter().stopSoundLocal(this.character.vocalEvent);
				this.character.vocalEvent = 0L;
			}
		}
	}

	static final class ObjectWithDistance {
		IsoZombie character;
		float distSq;
	}
}
