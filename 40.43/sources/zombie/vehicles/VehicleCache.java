package zombie.vehicles;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TShortObjectHashMap;
import java.util.LinkedList;
import java.util.List;


public class VehicleCache {
	public short id;
	float x;
	float y;
	float z;
	private static TShortObjectHashMap mapId = new TShortObjectHashMap();
	private static TIntObjectHashMap mapXY = new TIntObjectHashMap();

	public static void vehicleUpdate(short short1, float float1, float float2, float float3) {
		VehicleCache vehicleCache = (VehicleCache)mapId.get(short1);
		int int1;
		int int2;
		if (vehicleCache != null) {
			int int3 = (int)(vehicleCache.x / 10.0F);
			int1 = (int)(vehicleCache.y / 10.0F);
			int2 = (int)(float1 / 10.0F);
			int int4 = (int)(float2 / 10.0F);
			if (int3 != int2 || int1 != int4) {
				((List)mapXY.get(int3 * 65536 + int1)).remove(vehicleCache);
				if (mapXY.get(int2 * 65536 + int4) == null) {
					mapXY.put(int2 * 65536 + int4, new LinkedList());
				}

				((List)mapXY.get(int2 * 65536 + int4)).add(vehicleCache);
			}

			vehicleCache.x = float1;
			vehicleCache.y = float2;
			vehicleCache.z = float3;
		} else {
			VehicleCache vehicleCache2 = new VehicleCache();
			vehicleCache2.id = short1;
			vehicleCache2.x = float1;
			vehicleCache2.y = float2;
			vehicleCache2.z = float3;
			mapId.put(short1, vehicleCache2);
			int1 = (int)(float1 / 10.0F);
			int2 = (int)(float2 / 10.0F);
			if (mapXY.get(int1 * 65536 + int2) == null) {
				mapXY.put(int1 * 65536 + int2, new LinkedList());
			}

			((List)mapXY.get(int1 * 65536 + int2)).add(vehicleCache2);
		}
	}

	public static List vehicleGet(float float1, float float2) {
		int int1 = (int)(float1 / 10.0F);
		int int2 = (int)(float2 / 10.0F);
		return (List)mapXY.get(int1 * 65536 + int2);
	}

	public static List vehicleGet(int int1, int int2) {
		return (List)mapXY.get(int1 * 65536 + int2);
	}

	public static void remove(short short1) {
		VehicleCache vehicleCache = (VehicleCache)mapId.get(short1);
		if (vehicleCache != null) {
			mapId.remove(short1);
			int int1 = (int)(vehicleCache.x / 10.0F);
			int int2 = (int)(vehicleCache.y / 10.0F);
			int int3 = int1 * 65536 + int2;
			assert mapXY.containsKey(int3);
			assert ((List)mapXY.get(int3)).contains(vehicleCache);
			((List)mapXY.get(int3)).remove(vehicleCache);
		}
	}

	public static void Reset() {
		mapId.clear();
		mapXY.clear();
	}
}
