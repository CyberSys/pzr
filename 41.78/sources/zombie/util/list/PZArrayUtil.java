package zombie.util.list;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.util.ICloner;
import zombie.util.Pool;
import zombie.util.StringUtils;


public class PZArrayUtil {
	public static final int[] emptyIntArray = new int[0];
	public static final float[] emptyFloatArray = new float[0];

	public static Object pickRandom(Object[] objectArray) {
		if (objectArray.length == 0) {
			return null;
		} else {
			int int1 = Rand.Next(objectArray.length);
			return objectArray[int1];
		}
	}

	public static Object pickRandom(List list) {
		if (list.isEmpty()) {
			return null;
		} else {
			int int1 = Rand.Next(list.size());
			return list.get(int1);
		}
	}

	public static Object pickRandom(Collection collection) {
		if (collection.isEmpty()) {
			return null;
		} else {
			int int1 = Rand.Next(collection.size());
			return getElementAt(collection, int1);
		}
	}

	public static Object pickRandom(Iterable iterable) {
		int int1 = getSize(iterable);
		if (int1 == 0) {
			return null;
		} else {
			int int2 = Rand.Next(int1);
			return getElementAt(iterable, int2);
		}
	}

	public static int getSize(Iterable iterable) {
		int int1 = 0;
		Iterator iterator = iterable.iterator();
		while (iterator.hasNext()) {
			++int1;
			iterator.next();
		}

		return int1;
	}

	public static Object getElementAt(Iterable iterable, int int1) throws ArrayIndexOutOfBoundsException {
		Object object = null;
		Iterator iterator = iterable.iterator();
		for (int int2 = 0; int2 <= int1; ++int2) {
			if (!iterator.hasNext()) {
				throw new ArrayIndexOutOfBoundsException(int2);
			}

			if (int2 == int1) {
				object = iterator.next();
			}
		}

		return object;
	}

	public static void copy(ArrayList arrayList, ArrayList arrayList2) {
		copy(arrayList, arrayList2, (arrayListx)->{
			return arrayListx;
		});
	}

	public static void copy(ArrayList arrayList, ArrayList arrayList2, ICloner iCloner) {
		if (arrayList != arrayList2) {
			arrayList.clear();
			arrayList.ensureCapacity(arrayList2.size());
			for (int int1 = 0; int1 < arrayList2.size(); ++int1) {
				Object object = arrayList2.get(int1);
				arrayList.add(iCloner.clone(object));
			}
		}
	}

	public static int indexOf(Object[] objectArray, Predicate predicate) {
		byte byte1;
		try {
			for (int int1 = 0; int1 < objectArray.length; ++int1) {
				Object object = objectArray[int1];
				if (predicate.test(object)) {
					int int2 = int1;
					return int2;
				}
			}

			byte1 = -1;
		} finally {
			Pool.tryRelease((Object)predicate);
		}

		return byte1;
	}

	public static int indexOf(List list, Predicate predicate) {
		try {
			int int1 = -1;
			int int2 = 0;
			while (true) {
				if (int2 < list.size()) {
					Object object = list.get(int2);
					if (!predicate.test(object)) {
						++int2;
						continue;
					}

					int1 = int2;
				}

				int2 = int1;
				return int2;
			}
		} finally {
			Pool.tryRelease((Object)predicate);
		}
	}

	public static boolean contains(Object[] objectArray, Predicate predicate) {
		return indexOf(objectArray, predicate) > -1;
	}

	public static boolean contains(List list, Predicate predicate) {
		return indexOf(list, predicate) > -1;
	}

	public static boolean contains(Collection collection, Predicate predicate) {
		if (collection instanceof List) {
			return contains((List)collection, predicate);
		} else {
			try {
				boolean boolean1 = false;
				Iterator iterator = collection.iterator();
				while (true) {
					if (iterator.hasNext()) {
						Object object = iterator.next();
						if (!predicate.test(object)) {
							continue;
						}

						boolean1 = true;
					}

					boolean boolean2 = boolean1;
					return boolean2;
				}
			} finally {
				Pool.tryRelease((Object)predicate);
			}
		}
	}

	public static boolean contains(Iterable iterable, Predicate predicate) {
		if (iterable instanceof List) {
			return indexOf((List)iterable, predicate) > -1;
		} else {
			try {
				boolean boolean1 = false;
				Iterator iterator = iterable.iterator();
				while (true) {
					if (iterator.hasNext()) {
						Object object = iterator.next();
						if (!predicate.test(object)) {
							continue;
						}

						boolean1 = true;
					}

					boolean boolean2 = boolean1;
					return boolean2;
				}
			} finally {
				Pool.tryRelease((Object)predicate);
			}
		}
	}

	public static Object find(List list, Predicate predicate) {
		int int1 = indexOf(list, predicate);
		return int1 > -1 ? list.get(int1) : null;
	}

	public static Object find(Iterable iterable, Predicate predicate) {
		if (iterable instanceof List) {
			return find((List)iterable, predicate);
		} else {
			Object object;
			try {
				Iterator iterator = iterable.iterator();
				Object object2;
				do {
					if (!iterator.hasNext()) {
						iterator = null;
						return iterator;
					}

					object2 = iterator.next();
				}		 while (!predicate.test(object2));

				object = object2;
			} finally {
				Pool.tryRelease((Object)predicate);
			}

			return object;
		}
	}

	public static List listConvert(List list, Function function) {
		return (List)(list.isEmpty() ? PZArrayList.emptyList() : new PZConvertList(list, function));
	}

	public static Iterable itConvert(Iterable iterable, Function function) {
		return new PZConvertIterable(iterable, function);
	}

	public static List listConvert(List list, List list2, Function function) {
		list2.clear();
		for (int int1 = 0; int1 < list.size(); ++int1) {
			list2.add(function.apply(list.get(int1)));
		}

		return list2;
	}

	public static List listConvert(List list, List list2, Object object, PZArrayUtil.IListConverter1Param iListConverter1Param) {
		list2.clear();
		for (int int1 = 0; int1 < list.size(); ++int1) {
			list2.add(iListConverter1Param.convert(list.get(int1), object));
		}

		return list2;
	}

	private static List asList(Object[] objectArray) {
		return Arrays.asList(objectArray);
	}

	private static List asList(float[] floatArray) {
		return new PrimitiveFloatList(floatArray);
	}

	private static Iterable asSafeIterable(Object[] objectArray) {
		return (Iterable)(objectArray != null ? asList(objectArray) : PZEmptyIterable.getInstance());
	}

	private static Iterable asSafeIterable(float[] floatArray) {
		return (Iterable)(floatArray != null ? asList(floatArray) : PZEmptyIterable.getInstance());
	}

	public static String arrayToString(float[] floatArray) {
		return arrayToString(asSafeIterable(floatArray));
	}

	public static String arrayToString(float[] floatArray, String string, String string2, String string3) {
		return arrayToString(asSafeIterable(floatArray), string, string2, string3);
	}

	public static String arrayToString(Object[] objectArray) {
		return arrayToString(asSafeIterable(objectArray));
	}

	public static String arrayToString(Object[] objectArray, String string, String string2, String string3) {
		return arrayToString(asSafeIterable(objectArray), string, string2, string3);
	}

	public static String arrayToString(Iterable iterable, Function function) {
		return arrayToString(iterable, function, "{", "}", System.lineSeparator());
	}

	public static String arrayToString(Iterable iterable) {
		return arrayToString(iterable, String::valueOf, "{", "}", System.lineSeparator());
	}

	public static String arrayToString(Iterable iterable, String string, String string2, String string3) {
		return arrayToString(iterable, String::valueOf, string, string2, string3);
	}

	public static String arrayToString(Iterable iterable, Function function, String string, String string2, String string3) {
		StringBuilder stringBuilder = new StringBuilder(string);
		if (iterable != null) {
			boolean boolean1 = true;
			for (Iterator iterator = iterable.iterator(); iterator.hasNext(); boolean1 = false) {
				Object object = iterator.next();
				if (!boolean1) {
					stringBuilder.append(string3);
				}

				String string4 = (String)function.apply(object);
				stringBuilder.append(string4);
			}
		}

		stringBuilder.append(string2);
		Pool.tryRelease((Object)function);
		return stringBuilder.toString();
	}

	public static Object[] newInstance(Class javaClass, int int1) {
		return (Object[])Array.newInstance(javaClass, int1);
	}

	public static Object[] newInstance(Class javaClass, int int1, Supplier supplier) {
		Object[] objectArray = newInstance(javaClass, int1);
		int int2 = 0;
		for (int int3 = objectArray.length; int2 < int3; ++int2) {
			objectArray[int2] = supplier.get();
		}

		return objectArray;
	}

	public static Object[] newInstance(Class javaClass, Object[] objectArray, int int1) {
		return newInstance(javaClass, objectArray, int1, false, ()->{
			return null;
		});
	}

	public static Object[] newInstance(Class javaClass, Object[] objectArray, int int1, boolean boolean1) {
		return newInstance(javaClass, objectArray, int1, boolean1, ()->{
			return null;
		});
	}

	public static Object[] newInstance(Class javaClass, Object[] objectArray, int int1, Supplier supplier) {
		return newInstance(javaClass, objectArray, int1, false, supplier);
	}

	public static Object[] newInstance(Class javaClass, Object[] objectArray, int int1, boolean boolean1, Supplier supplier) {
		if (objectArray == null) {
			return newInstance(javaClass, int1, supplier);
		} else {
			int int2 = objectArray.length;
			if (int2 == int1) {
				return objectArray;
			} else if (boolean1 && int2 > int1) {
				return objectArray;
			} else {
				Object[] objectArray2 = newInstance(javaClass, int1);
				arrayCopy((Object[])objectArray2, (Object[])objectArray, 0, PZMath.min(int1, int2));
				int int3;
				if (int1 > int2) {
					for (int3 = int2; int3 < int1; ++int3) {
						objectArray2[int3] = supplier.get();
					}
				}

				if (int1 < int2) {
					for (int3 = int1; int3 < int2; ++int3) {
						objectArray[int3] = Pool.tryRelease(objectArray[int3]);
					}
				}

				return objectArray2;
			}
		}
	}

	public static float[] add(float[] floatArray, float float1) {
		float[] floatArray2 = new float[floatArray.length + 1];
		arrayCopy((float[])floatArray2, (float[])floatArray, 0, floatArray.length);
		floatArray2[floatArray.length] = float1;
		return floatArray2;
	}

	public static Object[] add(Object[] objectArray, Object object) {
		Object[] objectArray2 = newInstance(objectArray.getClass().getComponentType(), objectArray.length + 1);
		arrayCopy((Object[])objectArray2, (Object[])objectArray, 0, objectArray.length);
		objectArray2[objectArray.length] = object;
		return objectArray2;
	}

	public static Object[] concat(Object[] objectArray, Object[] objectArray2) {
		boolean boolean1 = objectArray == null || objectArray.length == 0;
		boolean boolean2 = objectArray2 == null || objectArray2.length == 0;
		if (boolean1 && boolean2) {
			return null;
		} else if (boolean1) {
			return clone(objectArray2);
		} else if (boolean2) {
			return objectArray;
		} else {
			Object[] objectArray3 = newInstance(objectArray.getClass().getComponentType(), objectArray.length + objectArray2.length);
			arrayCopy((Object[])objectArray3, (Object[])objectArray, 0, objectArray.length);
			arrayCopy(objectArray3, objectArray2, objectArray.length, objectArray3.length);
			return objectArray3;
		}
	}

	public static Object[] arrayCopy(Object[] objectArray, Object[] objectArray2, int int1, int int2) {
		for (int int3 = int1; int3 < int2; ++int3) {
			objectArray[int3] = objectArray2[int3];
		}

		return objectArray;
	}

	public static float[] arrayCopy(float[] floatArray, float[] floatArray2, int int1, int int2) {
		for (int int3 = int1; int3 < int2; ++int3) {
			floatArray[int3] = floatArray2[int3];
		}

		return floatArray;
	}

	public static int[] arrayCopy(int[] intArray, int[] intArray2, int int1, int int2) {
		for (int int3 = int1; int3 < int2; ++int3) {
			intArray[int3] = intArray2[int3];
		}

		return intArray;
	}

	public static List arrayCopy(List list, List list2) {
		list.clear();
		list.addAll(list2);
		return list;
	}

	public static Object[] arrayCopy(Object[] objectArray, List list) {
		for (int int1 = 0; int1 < list.size(); ++int1) {
			objectArray[int1] = list.get(int1);
		}

		return objectArray;
	}

	public static Object[] arrayCopy(Object[] objectArray, Object[] objectArray2) {
		System.arraycopy(objectArray2, 0, objectArray, 0, objectArray2.length);
		return objectArray;
	}

	public static List arrayConvert(List list, List list2, Function function) {
		list.clear();
		int int1 = 0;
		for (int int2 = list2.size(); int1 < int2; ++int1) {
			Object object = list2.get(int1);
			list.add(function.apply(object));
		}

		return list;
	}

	public static float[] clone(float[] floatArray) {
		if (isNullOrEmpty(floatArray)) {
			return floatArray;
		} else {
			float[] floatArray2 = new float[floatArray.length];
			arrayCopy((float[])floatArray2, (float[])floatArray, 0, floatArray.length);
			return floatArray2;
		}
	}

	public static Object[] clone(Object[] objectArray) {
		if (isNullOrEmpty(objectArray)) {
			return objectArray;
		} else {
			Object[] objectArray2 = newInstance(objectArray.getClass().getComponentType(), objectArray.length);
			arrayCopy((Object[])objectArray2, (Object[])objectArray, 0, objectArray.length);
			return objectArray2;
		}
	}

	public static boolean isNullOrEmpty(Object[] objectArray) {
		return objectArray == null || objectArray.length == 0;
	}

	public static boolean isNullOrEmpty(int[] intArray) {
		return intArray == null || intArray.length == 0;
	}

	public static boolean isNullOrEmpty(float[] floatArray) {
		return floatArray == null || floatArray.length == 0;
	}

	public static boolean isNullOrEmpty(List list) {
		return list == null || list.isEmpty();
	}

	public static boolean isNullOrEmpty(Iterable iterable) {
		if (iterable instanceof List) {
			return isNullOrEmpty((List)iterable);
		} else {
			boolean boolean1 = true;
			Iterator iterator = iterable.iterator();
			if (iterator.hasNext()) {
				Object object = iterator.next();
				boolean1 = false;
			}

			return boolean1;
		}
	}

	public static Object getOrDefault(List list, int int1) {
		return getOrDefault((List)list, int1, (Object)null);
	}

	public static Object getOrDefault(List list, int int1, Object object) {
		return int1 >= 0 && int1 < list.size() ? list.get(int1) : object;
	}

	public static Object getOrDefault(Object[] objectArray, int int1, Object object) {
		return objectArray != null && int1 >= 0 && int1 < objectArray.length ? objectArray[int1] : object;
	}

	public static float getOrDefault(float[] floatArray, int int1, float float1) {
		return floatArray != null && int1 >= 0 && int1 < floatArray.length ? floatArray[int1] : float1;
	}

	public static int[] arraySet(int[] intArray, int int1) {
		if (isNullOrEmpty(intArray)) {
			return intArray;
		} else {
			int int2 = 0;
			for (int int3 = intArray.length; int2 < int3; ++int2) {
				intArray[int2] = int1;
			}

			return intArray;
		}
	}

	public static float[] arraySet(float[] floatArray, float float1) {
		if (isNullOrEmpty(floatArray)) {
			return floatArray;
		} else {
			int int1 = 0;
			for (int int2 = floatArray.length; int1 < int2; ++int1) {
				floatArray[int1] = float1;
			}

			return floatArray;
		}
	}

	public static Object[] arraySet(Object[] objectArray, Object object) {
		if (isNullOrEmpty(objectArray)) {
			return objectArray;
		} else {
			int int1 = 0;
			for (int int2 = objectArray.length; int1 < int2; ++int1) {
				objectArray[int1] = object;
			}

			return objectArray;
		}
	}

	public static Object[] arrayPopulate(Object[] objectArray, Supplier supplier) {
		if (isNullOrEmpty(objectArray)) {
			return objectArray;
		} else {
			int int1 = 0;
			for (int int2 = objectArray.length; int1 < int2; ++int1) {
				objectArray[int1] = supplier.get();
			}

			return objectArray;
		}
	}

	public static void insertAt(int[] intArray, int int1, int int2) {
		for (int int3 = intArray.length - 1; int3 > int1; --int3) {
			intArray[int3] = intArray[int3 - 1];
		}

		intArray[int1] = int2;
	}

	public static void insertAt(float[] floatArray, int int1, float float1) {
		for (int int2 = floatArray.length - 1; int2 > int1; --int2) {
			floatArray[int2] = floatArray[int2 - 1];
		}

		floatArray[int1] = float1;
	}

	public static Object[] toArray(List list) {
		if (list != null && !list.isEmpty()) {
			Object[] objectArray = newInstance(list.get(0).getClass(), list.size());
			arrayCopy(objectArray, list);
			return objectArray;
		} else {
			return null;
		}
	}

	public static int indexOf(Object[] objectArray, int int1, Object object) {
		for (int int2 = 0; int2 < int1; ++int2) {
			if (objectArray[int2] == object) {
				return int2;
			}
		}

		return -1;
	}

	public static int indexOf(float[] floatArray, int int1, float float1) {
		for (int int2 = 0; int2 < int1; ++int2) {
			if (floatArray[int2] == float1) {
				return int2;
			}
		}

		return -1;
	}

	public static boolean contains(float[] floatArray, int int1, float float1) {
		return indexOf(floatArray, int1, float1) != -1;
	}

	public static int indexOf(int[] intArray, int int1, int int2) {
		for (int int3 = 0; int3 < int1; ++int3) {
			if (intArray[int3] == int2) {
				return int3;
			}
		}

		return -1;
	}

	public static boolean contains(int[] intArray, int int1, int int2) {
		return indexOf(intArray, int1, int2) != -1;
	}

	public static void forEach(List list, Consumer consumer) {
		try {
			if (list != null) {
				int int1 = 0;
				for (int int2 = list.size(); int1 < int2; ++int1) {
					Object object = list.get(int1);
					consumer.accept(object);
				}

				return;
			}
		} finally {
			Pool.tryRelease((Object)consumer);
		}
	}

	public static void forEach(Iterable iterable, Consumer consumer) {
		if (iterable == null) {
			Pool.tryRelease((Object)consumer);
		} else if (iterable instanceof List) {
			forEach((List)iterable, consumer);
		} else {
			try {
				Iterator iterator = iterable.iterator();
				while (iterator.hasNext()) {
					Object object = iterator.next();
					consumer.accept(object);
				}
			} finally {
				Pool.tryRelease((Object)consumer);
			}
		}
	}

	public static void forEach(Object[] objectArray, Consumer consumer) {
		if (!isNullOrEmpty(objectArray)) {
			int int1 = 0;
			for (int int2 = objectArray.length; int1 < int2; ++int1) {
				consumer.accept(objectArray[int1]);
			}
		}
	}

	public static Object getOrCreate(HashMap hashMap, Object object, Supplier supplier) {
		Object object2 = hashMap.get(object);
		if (object2 == null) {
			object2 = supplier.get();
			hashMap.put(object, object2);
		}

		return object2;
	}

	public static void sort(Stack stack, Comparator comparator) {
		try {
			stack.sort(comparator);
		} finally {
			Pool.tryRelease((Object)comparator);
		}
	}

	public static boolean sequenceEqual(Object[] objectArray, List list) {
		return sequenceEqual(objectArray, list, PZArrayUtil.Comparators::objectsEqual);
	}

	public static boolean sequenceEqual(Object[] objectArray, List list, Comparator comparator) {
		return objectArray.length == list.size() && sequenceEqual(asList(objectArray), list, comparator);
	}

	public static boolean sequenceEqual(List list, List list2) {
		return sequenceEqual(list, list2, PZArrayUtil.Comparators::objectsEqual);
	}

	public static boolean sequenceEqual(List list, List list2, Comparator comparator) {
		if (list.size() != list2.size()) {
			return false;
		} else {
			boolean boolean1 = true;
			int int1 = 0;
			for (int int2 = list.size(); int1 < int2; ++int1) {
				Object object = list.get(int1);
				Object object2 = list2.get(int1);
				if (comparator.compare(object, object2) != 0) {
					boolean1 = false;
					break;
				}
			}

			return boolean1;
		}
	}

	public static int[] arrayAdd(int[] intArray, int[] intArray2) {
		for (int int1 = 0; int1 < intArray.length; ++int1) {
			intArray[int1] += intArray2[int1];
		}

		return intArray;
	}

	public interface IListConverter1Param {

		Object convert(Object object, Object object2);
	}

	public static class Comparators {

		public static int referencesEqual(Object object, Object object2) {
			return object == object2 ? 0 : 1;
		}

		public static int objectsEqual(Object object, Object object2) {
			return object != null && object.equals(object2) ? 0 : 1;
		}

		public static int equalsIgnoreCase(String string, String string2) {
			return StringUtils.equals(string, string2) ? 0 : 1;
		}
	}
}
