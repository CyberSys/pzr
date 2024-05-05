package zombie.vehicles;

import java.util.ArrayList;
import java.util.Arrays;
import zombie.GameWindow;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.network.GameClient;


public final class VehicleIDMap {
	public static final VehicleIDMap instance = new VehicleIDMap();
	private static final int MAX_IDS = 32767;
	private static final int RESIZE_COUNT = 256;
	private int capacity = 256;
	private BaseVehicle[] idToVehicle;
	private short[] freeID;
	private short freeIDSize;
	private boolean noise = false;
	private int warnCount = 0;

	VehicleIDMap() {
		this.idToVehicle = new BaseVehicle[this.capacity];
		this.freeID = new short[this.capacity];
		for (int int1 = 0; int1 < this.capacity; ++int1) {
			short[] shortArray = this.freeID;
			short short1 = this.freeIDSize;
			this.freeIDSize = (short)(short1 + 1);
			shortArray[short1] = (short)int1;
		}
	}

	public void put(short short1, BaseVehicle baseVehicle) {
		if (Core.bDebug && this.noise) {
			DebugLog.log("VehicleIDMap.put()" + short1);
		}

		if (GameClient.bClient && short1 >= this.capacity) {
			this.resize((short1 / 256 + 1) * 256);
		}

		if (short1 >= 0 && short1 < this.capacity) {
			if (this.idToVehicle[short1] != null) {
				throw new IllegalArgumentException("duplicate vehicle with id " + short1);
			} else if (baseVehicle == null) {
				throw new IllegalArgumentException("vehicle is null");
			} else {
				this.idToVehicle[short1] = baseVehicle;
			}
		} else {
			throw new IllegalArgumentException("invalid vehicle id " + short1 + " max=" + this.capacity);
		}
	}

	public void remove(short short1) {
		if (Core.bDebug && this.noise) {
			DebugLog.log("VehicleIDMap.remove()" + short1);
		}

		if (short1 >= 0 && short1 < this.capacity) {
			if (this.idToVehicle[short1] == null) {
				throw new IllegalArgumentException("no vehicle with id " + short1);
			} else {
				this.idToVehicle[short1] = null;
				if (!GameClient.bClient && !GameWindow.bLoadedAsClient) {
					short[] shortArray = this.freeID;
					short short2 = this.freeIDSize;
					this.freeIDSize = (short)(short2 + 1);
					shortArray[short2] = short1;
				}
			}
		} else {
			throw new IllegalArgumentException("invalid vehicle id=" + short1 + " max=" + this.capacity);
		}
	}

	public BaseVehicle get(short short1) {
		return short1 >= 0 && short1 < this.capacity ? this.idToVehicle[short1] : null;
	}

	public boolean containsKey(short short1) {
		return short1 >= 0 && short1 < this.capacity && this.idToVehicle[short1] != null;
	}

	public void toArrayList(ArrayList arrayList) {
		for (int int1 = 0; int1 < this.capacity; ++int1) {
			if (this.idToVehicle[int1] != null) {
				arrayList.add(this.idToVehicle[int1]);
			}
		}
	}

	public void Reset() {
		Arrays.fill(this.idToVehicle, (Object)null);
		this.freeIDSize = (short)this.capacity;
		for (short short1 = 0; short1 < this.capacity; this.freeID[short1] = short1++) {
		}
	}

	public short allocateID() {
		if (GameClient.bClient) {
			throw new RuntimeException("client must not call this");
		} else if (this.freeIDSize > 0) {
			return this.freeID[--this.freeIDSize];
		} else if (this.capacity >= 32767) {
			if (this.warnCount < 100) {
				DebugLog.log("warning: ran out of unique vehicle ids");
				++this.warnCount;
			}

			return -1;
		} else {
			this.resize(this.capacity + 256);
			return this.allocateID();
		}
	}

	private void resize(int int1) {
		int int2 = this.capacity;
		this.capacity = Math.min(int1, 32767);
		this.capacity = Math.min(int1, 32767);
		this.idToVehicle = (BaseVehicle[])Arrays.copyOf(this.idToVehicle, this.capacity);
		this.freeID = Arrays.copyOf(this.freeID, this.capacity);
		for (int int3 = int2; int3 < this.capacity; ++int3) {
			short[] shortArray = this.freeID;
			short short1 = this.freeIDSize;
			this.freeIDSize = (short)(short1 + 1);
			shortArray[short1] = (short)int3;
		}
	}
}
