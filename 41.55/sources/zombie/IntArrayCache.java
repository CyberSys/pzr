package zombie;

import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.Stack;


public class IntArrayCache {
	public static IntArrayCache instance = new IntArrayCache();
	TIntObjectHashMap Map = new TIntObjectHashMap();

	public void Init() {
		for (int int1 = 0; int1 < 100; ++int1) {
			Stack stack = new Stack();
			for (int int2 = 0; int2 < 1000; ++int2) {
				stack.push(new Integer[int1]);
			}
		}
	}

	public void put(Integer[] integerArray) {
		if (this.Map.containsKey(integerArray.length)) {
			((Stack)this.Map.get(integerArray.length)).push(integerArray);
		} else {
			Stack stack = new Stack();
			stack.push(integerArray);
			this.Map.put(integerArray.length, stack);
		}
	}

	public Integer[] get(int int1) {
		if (this.Map.containsKey(int1)) {
			Stack stack = (Stack)this.Map.get(int1);
			if (!stack.isEmpty()) {
				return (Integer[])stack.pop();
			}
		}

		return new Integer[int1];
	}
}
