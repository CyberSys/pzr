package zombie.core.utils;


public class HashMap {
	private int capacity = 2;
	private int elements = 0;
	private HashMap.Bucket[] buckets;

	public HashMap() {
		this.buckets = new HashMap.Bucket[this.capacity];
		for (int int1 = 0; int1 < this.capacity; ++int1) {
			this.buckets[int1] = new HashMap.Bucket();
		}
	}

	public void clear() {
		this.elements = 0;
		for (int int1 = 0; int1 < this.capacity; ++int1) {
			this.buckets[int1].clear();
		}
	}

	private void grow() {
		HashMap.Bucket[] bucketArray = this.buckets;
		this.capacity *= 2;
		this.elements = 0;
		this.buckets = new HashMap.Bucket[this.capacity];
		int int1;
		for (int1 = 0; int1 < this.capacity; ++int1) {
			this.buckets[int1] = new HashMap.Bucket();
		}

		for (int1 = 0; int1 < bucketArray.length; ++int1) {
			HashMap.Bucket bucket = bucketArray[int1];
			for (int int2 = 0; int2 < bucket.size(); ++int2) {
				if (bucket.keys[int2] != null) {
					this.put(bucket.keys[int2], bucket.values[int2]);
				}
			}
		}
	}

	public Object get(Object object) {
		HashMap.Bucket bucket = this.buckets[Math.abs(object.hashCode()) % this.capacity];
		for (int int1 = 0; int1 < bucket.size(); ++int1) {
			if (bucket.keys[int1] != null && bucket.keys[int1].equals(object)) {
				return bucket.values[int1];
			}
		}

		return null;
	}

	public Object remove(Object object) {
		HashMap.Bucket bucket = this.buckets[Math.abs(object.hashCode()) % this.capacity];
		Object object2 = bucket.remove(object);
		if (object2 != null) {
			--this.elements;
			return object2;
		} else {
			return null;
		}
	}

	public Object put(Object object, Object object2) {
		if (this.elements + 1 >= this.buckets.length) {
			this.grow();
		}

		Object object3 = this.remove(object);
		HashMap.Bucket bucket = this.buckets[Math.abs(object.hashCode()) % this.capacity];
		bucket.put(object, object2);
		++this.elements;
		return object3;
	}

	public int size() {
		return this.elements;
	}

	public boolean isEmpty() {
		return this.size() == 0;
	}

	public HashMap.Iterator iterator() {
		return new HashMap.Iterator(this);
	}

	public String toString() {
		String string = new String();
		for (int int1 = 0; int1 < this.buckets.length; ++int1) {
			HashMap.Bucket bucket = this.buckets[int1];
			for (int int2 = 0; int2 < bucket.size(); ++int2) {
				if (bucket.keys[int2] != null) {
					if (string.length() > 0) {
						string = string + ", ";
					}

					string = string + bucket.keys[int2] + "=" + bucket.values[int2];
				}
			}
		}

		string = "HashMap[" + string + "]";
		return string;
	}

	private static class Bucket {
		public Object[] keys;
		public Object[] values;
		public int count;
		public int nextIndex;

		private Bucket() {
		}

		public void put(Object object, Object object2) throws IllegalStateException {
			if (this.keys == null) {
				this.grow();
				this.keys[0] = object;
				this.values[0] = object2;
				this.nextIndex = 1;
				this.count = 1;
			} else {
				if (this.count == this.keys.length) {
					this.grow();
				}

				for (int int1 = 0; int1 < this.keys.length; ++int1) {
					if (this.keys[int1] == null) {
						this.keys[int1] = object;
						this.values[int1] = object2;
						++this.count;
						this.nextIndex = Math.max(this.nextIndex, int1 + 1);
						return;
					}
				}

				throw new IllegalStateException("bucket is full");
			}
		}

		public Object remove(Object object) {
			for (int int1 = 0; int1 < this.nextIndex; ++int1) {
				if (this.keys[int1] != null && this.keys[int1].equals(object)) {
					Object object2 = this.values[int1];
					this.keys[int1] = null;
					this.values[int1] = null;
					--this.count;
					return object2;
				}
			}

			return null;
		}

		private void grow() {
			if (this.keys == null) {
				this.keys = new Object[2];
				this.values = new Object[2];
			} else {
				Object[] objectArray = this.keys;
				Object[] objectArray2 = this.values;
				this.keys = new Object[objectArray.length * 2];
				this.values = new Object[objectArray2.length * 2];
				System.arraycopy(objectArray, 0, this.keys, 0, objectArray.length);
				System.arraycopy(objectArray2, 0, this.values, 0, objectArray2.length);
			}
		}

		public int size() {
			return this.nextIndex;
		}

		public void clear() {
			for (int int1 = 0; int1 < this.nextIndex; ++int1) {
				this.keys[int1] = null;
				this.values[int1] = null;
			}

			this.count = 0;
			this.nextIndex = 0;
		}

		Bucket(Object object) {
			this();
		}
	}

	public static class Iterator {
		private HashMap hashMap;
		private int bucketIdx;
		private int keyValuePairIdx;
		private int elementIdx;
		private Object currentKey;
		private Object currentValue;

		public Iterator(HashMap hashMap) {
			this.hashMap = hashMap;
			this.reset();
		}

		public HashMap.Iterator reset() {
			this.bucketIdx = 0;
			this.keyValuePairIdx = 0;
			this.elementIdx = 0;
			this.currentKey = null;
			this.currentValue = null;
			return this;
		}

		public boolean hasNext() {
			return this.elementIdx < this.hashMap.elements;
		}

		public boolean advance() {
			while (this.bucketIdx < this.hashMap.buckets.length) {
				HashMap.Bucket bucket = this.hashMap.buckets[this.bucketIdx];
				if (this.keyValuePairIdx == bucket.size()) {
					this.keyValuePairIdx = 0;
					++this.bucketIdx;
				} else {
					while (this.keyValuePairIdx < bucket.size()) {
						if (bucket.keys[this.keyValuePairIdx] != null) {
							this.currentKey = bucket.keys[this.keyValuePairIdx];
							this.currentValue = bucket.values[this.keyValuePairIdx];
							++this.keyValuePairIdx;
							++this.elementIdx;
							return true;
						}

						++this.keyValuePairIdx;
					}

					this.keyValuePairIdx = 0;
					++this.bucketIdx;
				}
			}

			return false;
		}

		public Object getKey() {
			return this.currentKey;
		}

		public Object getValue() {
			return this.currentValue;
		}
	}
}
