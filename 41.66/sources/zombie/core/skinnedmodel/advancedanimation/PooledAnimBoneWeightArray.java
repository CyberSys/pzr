package zombie.core.skinnedmodel.advancedanimation;

import java.util.List;
import zombie.util.Pool;
import zombie.util.PooledArrayObject;
import zombie.util.list.PZArrayUtil;


public class PooledAnimBoneWeightArray extends PooledArrayObject {
	private static final PooledAnimBoneWeightArray s_empty = new PooledAnimBoneWeightArray();
	private static final Pool s_pool = new Pool(PooledAnimBoneWeightArray::new);

	public static PooledAnimBoneWeightArray alloc(int int1) {
		if (int1 == 0) {
			return s_empty;
		} else {
			PooledAnimBoneWeightArray pooledAnimBoneWeightArray = (PooledAnimBoneWeightArray)s_pool.alloc();
			pooledAnimBoneWeightArray.initCapacity(int1, (int1x)->{
				return new AnimBoneWeight[int1x];
			});

			return pooledAnimBoneWeightArray;
		}
	}

	public static PooledAnimBoneWeightArray toArray(List list) {
		if (list == null) {
			return null;
		} else {
			PooledAnimBoneWeightArray pooledAnimBoneWeightArray = alloc(list.size());
			PZArrayUtil.arrayCopy((Object[])((AnimBoneWeight[])pooledAnimBoneWeightArray.array()), (List)list);
			return pooledAnimBoneWeightArray;
		}
	}

	public static PooledAnimBoneWeightArray toArray(PooledArrayObject pooledArrayObject) {
		if (pooledArrayObject == null) {
			return null;
		} else {
			PooledAnimBoneWeightArray pooledAnimBoneWeightArray = alloc(pooledArrayObject.length());
			PZArrayUtil.arrayCopy((Object[])((AnimBoneWeight[])pooledAnimBoneWeightArray.array()), (Object[])((AnimBoneWeight[])pooledArrayObject.array()));
			return pooledAnimBoneWeightArray;
		}
	}
}
