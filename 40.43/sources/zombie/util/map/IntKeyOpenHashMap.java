package zombie.util.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import zombie.util.IntIterator;
import zombie.util.hash.DefaultIntHashFunction;
import zombie.util.hash.IntHashFunction;
import zombie.util.hash.Primes;
import zombie.util.set.AbstractIntSet;
import zombie.util.set.IntSet;
import zombie.util.util.Exceptions;


public class IntKeyOpenHashMap extends AbstractIntKeyMap implements IntKeyMap,Cloneable,Serializable {
	private static final long serialVersionUID = 1L;
	private static final int GROWTH_POLICY_RELATIVE = 0;
	private static final int GROWTH_POLICY_ABSOLUTE = 1;
	private static final int DEFAULT_GROWTH_POLICY = 0;
	public static final double DEFAULT_GROWTH_FACTOR = 1.0;
	public static final int DEFAULT_GROWTH_CHUNK = 10;
	public static final int DEFAULT_CAPACITY = 11;
	public static final double DEFAULT_LOAD_FACTOR = 0.75;
	private IntHashFunction keyhash;
	private int size;
	private transient int[] keys;
	private transient Object[] values;
	private transient byte[] states;
	private static final byte EMPTY = 0;
	private static final byte OCCUPIED = 1;
	private static final byte REMOVED = 2;
	private transient int used;
	private int growthPolicy;
	private double growthFactor;
	private int growthChunk;
	private double loadFactor;
	private int expandAt;
	private transient IntSet ckeys;
	private transient Collection cvalues;

	private IntKeyOpenHashMap(IntHashFunction intHashFunction, int int1, int int2, double double1, int int3, double double2) {
		if (intHashFunction == null) {
			Exceptions.nullArgument("hash function");
		}

		if (int1 < 0) {
			Exceptions.negativeArgument("capacity", String.valueOf(int1));
		}

		if (double1 <= 0.0) {
			Exceptions.negativeOrZeroArgument("growthFactor", String.valueOf(double1));
		}

		if (int3 <= 0) {
			Exceptions.negativeOrZeroArgument("growthChunk", String.valueOf(int3));
		}

		if (double2 <= 0.0) {
			Exceptions.negativeOrZeroArgument("loadFactor", String.valueOf(double2));
		}

		this.keyhash = intHashFunction;
		int1 = Primes.nextPrime(int1);
		this.keys = new int[int1];
		this.values = new Object[int1];
		this.states = new byte[int1];
		this.size = 0;
		this.expandAt = (int)Math.round(double2 * (double)int1);
		this.used = 0;
		this.growthPolicy = int2;
		this.growthFactor = double1;
		this.growthChunk = int3;
		this.loadFactor = double2;
	}

	private IntKeyOpenHashMap(int int1, int int2, double double1, int int3, double double2) {
		this(DefaultIntHashFunction.INSTANCE, int1, int2, double1, int3, double2);
	}

	public IntKeyOpenHashMap() {
		this(11);
	}

	public IntKeyOpenHashMap(IntKeyMap intKeyMap) {
		this();
		this.putAll(intKeyMap);
	}

	public IntKeyOpenHashMap(int int1) {
		this(int1, 0, 1.0, 10, 0.75);
	}

	public IntKeyOpenHashMap(double double1) {
		this(11, 0, 1.0, 10, double1);
	}

	public IntKeyOpenHashMap(int int1, double double1) {
		this(int1, 0, 1.0, 10, double1);
	}

	public IntKeyOpenHashMap(int int1, double double1, double double2) {
		this(int1, 0, double2, 10, double1);
	}

	public IntKeyOpenHashMap(int int1, double double1, int int2) {
		this(int1, 1, 1.0, int2, double1);
	}

	public IntKeyOpenHashMap(IntHashFunction intHashFunction) {
		this(intHashFunction, 11, 0, 1.0, 10, 0.75);
	}

	public IntKeyOpenHashMap(IntHashFunction intHashFunction, int int1) {
		this(intHashFunction, int1, 0, 1.0, 10, 0.75);
	}

	public IntKeyOpenHashMap(IntHashFunction intHashFunction, double double1) {
		this(intHashFunction, 11, 0, 1.0, 10, double1);
	}

	public IntKeyOpenHashMap(IntHashFunction intHashFunction, int int1, double double1) {
		this(intHashFunction, int1, 0, 1.0, 10, double1);
	}

	public IntKeyOpenHashMap(IntHashFunction intHashFunction, int int1, double double1, double double2) {
		this(intHashFunction, int1, 0, double2, 10, double1);
	}

	public IntKeyOpenHashMap(IntHashFunction intHashFunction, int int1, double double1, int int2) {
		this(intHashFunction, int1, 1, 1.0, int2, double1);
	}

	private void ensureCapacity(int int1) {
		if (int1 >= this.expandAt) {
			int int2;
			if (this.growthPolicy == 0) {
				int2 = (int)((double)this.keys.length * (1.0 + this.growthFactor));
			} else {
				int2 = this.keys.length + this.growthChunk;
			}

			if ((double)int2 * this.loadFactor < (double)int1) {
				int2 = (int)Math.round((double)int1 / this.loadFactor);
			}

			int2 = Primes.nextPrime(int2);
			this.expandAt = (int)Math.round(this.loadFactor * (double)int2);
			int[] intArray = new int[int2];
			Object[] objectArray = new Object[int2];
			byte[] byteArray = new byte[int2];
			this.used = 0;
			for (int int3 = 0; int3 < this.keys.length; ++int3) {
				if (this.states[int3] == 1) {
					++this.used;
					int int4 = this.keys[int3];
					Object object = this.values[int3];
					int int5 = Math.abs(this.keyhash.hash(int4));
					int int6 = int5 % int2;
					if (byteArray[int6] == 1) {
						int int7 = 1 + int5 % (int2 - 2);
						do {
							int6 -= int7;
							if (int6 < 0) {
								int6 += int2;
							}
						}				 while (byteArray[int6] != 0);
					}

					byteArray[int6] = 1;
					objectArray[int6] = object;
					intArray[int6] = int4;
				}
			}

			this.keys = intArray;
			this.values = objectArray;
			this.states = byteArray;
		}
	}

	public IntSet keySet() {
		if (this.ckeys == null) {
			this.ckeys = new IntKeyOpenHashMap.KeySet();
		}

		return this.ckeys;
	}

	public Object put(int int1, Object object) {
		int int2 = Math.abs(this.keyhash.hash(int1));
		int int3 = int2 % this.keys.length;
		if (this.states[int3] == 1) {
			if (this.keys[int3] == int1) {
				Object object2 = this.values[int3];
				this.values[int3] = object;
				return object2;
			}

			int int4 = 1 + int2 % (this.keys.length - 2);
			while (true) {
				int3 -= int4;
				if (int3 < 0) {
					int3 += this.keys.length;
				}

				if (this.states[int3] == 0 || this.states[int3] == 2) {
					break;
				}

				if (this.states[int3] == 1 && this.keys[int3] == int1) {
					Object object3 = this.values[int3];
					this.values[int3] = object;
					return object3;
				}
			}
		}

		if (this.states[int3] == 0) {
			++this.used;
		}

		this.states[int3] = 1;
		this.keys[int3] = int1;
		this.values[int3] = object;
		++this.size;
		this.ensureCapacity(this.used);
		return null;
	}

	public Collection values() {
		if (this.cvalues == null) {
			this.cvalues = new IntKeyOpenHashMap.ValueCollection();
		}

		return this.cvalues;
	}

	public Object clone() {
		try {
			IntKeyOpenHashMap intKeyOpenHashMap = (IntKeyOpenHashMap)super.clone();
			intKeyOpenHashMap.keys = new int[this.keys.length];
			System.arraycopy(this.keys, 0, intKeyOpenHashMap.keys, 0, this.keys.length);
			intKeyOpenHashMap.values = new Object[this.values.length];
			System.arraycopy(this.values, 0, intKeyOpenHashMap.values, 0, this.values.length);
			intKeyOpenHashMap.states = new byte[this.states.length];
			System.arraycopy(this.states, 0, intKeyOpenHashMap.states, 0, this.states.length);
			intKeyOpenHashMap.cvalues = null;
			intKeyOpenHashMap.ckeys = null;
			return intKeyOpenHashMap;
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			Exceptions.cloning();
			return null;
		}
	}

	public IntKeyMapIterator entries() {
		return new IntKeyMapIterator(){
			int nextEntry = this.nextEntry(0);
			int lastEntry = -1;
			
			int nextEntry(int var1) {
				while (var1 < IntKeyOpenHashMap.this.keys.length && IntKeyOpenHashMap.this.states[var1] != 1) {
					++var1;
				}

				return var1;
			}

			
			public boolean hasNext() {
				return this.nextEntry < IntKeyOpenHashMap.this.keys.length;
			}

			
			public void next() {
				if (!this.hasNext()) {
					Exceptions.endOfIterator();
				}

				this.lastEntry = this.nextEntry;
				this.nextEntry = this.nextEntry(this.nextEntry + 1);
			}

			
			public void remove() {
				if (this.lastEntry == -1) {
					Exceptions.noElementToRemove();
				}

				IntKeyOpenHashMap.this.states[this.lastEntry] = 2;
				IntKeyOpenHashMap.this.values[this.lastEntry] = null;
				IntKeyOpenHashMap.this.size--;
				this.lastEntry = -1;
			}

			
			public int getKey() {
				if (this.lastEntry == -1) {
					Exceptions.noElementToGet();
				}

				return IntKeyOpenHashMap.this.keys[this.lastEntry];
			}

			
			public Object getValue() {
				if (this.lastEntry == -1) {
					Exceptions.noElementToGet();
				}

				return IntKeyOpenHashMap.this.values[this.lastEntry];
			}
		};
	}

	public void clear() {
		Arrays.fill(this.states, (byte)0);
		Arrays.fill(this.values, (Object)null);
		this.size = 0;
		this.used = 0;
	}

	public boolean containsKey(int int1) {
		int int2 = Math.abs(this.keyhash.hash(int1));
		int int3 = int2 % this.keys.length;
		if (this.states[int3] == 0) {
			return false;
		} else if (this.states[int3] == 1 && this.keys[int3] == int1) {
			return true;
		} else {
			int int4 = 1 + int2 % (this.keys.length - 2);
			do {
				int3 -= int4;
				if (int3 < 0) {
					int3 += this.keys.length;
				}

				if (this.states[int3] == 0) {
					return false;
				}
			}	 while (this.states[int3] != 1 || this.keys[int3] != int1);

			return true;
		}
	}

	public boolean containsValue(Object object) {
		int int1;
		if (object == null) {
			for (int1 = 0; int1 < this.states.length; ++int1) {
				if (this.states[int1] == 1 && this.values[int1] == null) {
					return true;
				}
			}
		} else {
			for (int1 = 0; int1 < this.states.length; ++int1) {
				if (this.states[int1] == 1 && object.equals(this.values[int1])) {
					return true;
				}
			}
		}

		return false;
	}

	public Object get(int int1) {
		int int2 = Math.abs(this.keyhash.hash(int1));
		int int3 = int2 % this.keys.length;
		if (this.states[int3] == 0) {
			return null;
		} else if (this.states[int3] == 1 && this.keys[int3] == int1) {
			return this.values[int3];
		} else {
			int int4 = 1 + int2 % (this.keys.length - 2);
			do {
				int3 -= int4;
				if (int3 < 0) {
					int3 += this.keys.length;
				}

				if (this.states[int3] == 0) {
					return null;
				}
			}	 while (this.states[int3] != 1 || this.keys[int3] != int1);

			return this.values[int3];
		}
	}

	public boolean isEmpty() {
		return this.size == 0;
	}

	public Object remove(int int1) {
		int int2 = Math.abs(this.keyhash.hash(int1));
		int int3 = int2 % this.keys.length;
		if (this.states[int3] == 0) {
			return null;
		} else if (this.states[int3] == 1 && this.keys[int3] == int1) {
			Object object = this.values[int3];
			this.values[int3] = null;
			this.states[int3] = 2;
			--this.size;
			return object;
		} else {
			int int4 = 1 + int2 % (this.keys.length - 2);
			do {
				int3 -= int4;
				if (int3 < 0) {
					int3 += this.keys.length;
				}

				if (this.states[int3] == 0) {
					return null;
				}
			}	 while (this.states[int3] != 1 || this.keys[int3] != int1);

			Object object2 = this.values[int3];
			this.values[int3] = null;
			this.states[int3] = 2;
			--this.size;
			return object2;
		}
	}

	public int size() {
		return this.size;
	}

	private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
		objectOutputStream.defaultWriteObject();
		objectOutputStream.writeInt(this.keys.length);
		IntKeyMapIterator intKeyMapIterator = this.entries();
		while (intKeyMapIterator.hasNext()) {
			intKeyMapIterator.next();
			objectOutputStream.writeInt(intKeyMapIterator.getKey());
			objectOutputStream.writeObject(intKeyMapIterator.getValue());
		}
	}

	private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
		objectInputStream.defaultReadObject();
		this.keys = new int[objectInputStream.readInt()];
		this.states = new byte[this.keys.length];
		this.values = new Object[this.keys.length];
		this.used = this.size;
		for (int int1 = 0; int1 < this.size; ++int1) {
			int int2 = objectInputStream.readInt();
			Object object = objectInputStream.readObject();
			int int3 = Math.abs(this.keyhash.hash(int2));
			int int4 = int3 % this.keys.length;
			if (this.states[int4] != 0) {
				int int5 = 1 + int3 % (this.keys.length - 2);
				do {
					int4 -= int5;
					if (int4 < 0) {
						int4 += this.keys.length;
					}
				}		 while (this.states[int4] != 0);
			}

			this.states[int4] = 1;
			this.keys[int4] = int2;
			this.values[int4] = object;
		}
	}

	private class ValueCollection extends AbstractCollection {

		private ValueCollection() {
		}

		public void clear() {
			IntKeyOpenHashMap.this.clear();
		}

		public boolean contains(Object object) {
			return IntKeyOpenHashMap.this.containsValue(object);
		}

		public Iterator iterator() {
			return new Iterator(){
				int nextEntry = this.nextEntry(0);
				int lastEntry = -1;
				
				int nextEntry(int var1) {
					while (var1 < IntKeyOpenHashMap.this.keys.length && IntKeyOpenHashMap.this.states[var1] != 1) {
						++var1;
					}

					return var1;
				}

				
				public boolean hasNext() {
					return this.nextEntry < IntKeyOpenHashMap.this.keys.length;
				}

				
				public Object next() {
					if (!this.hasNext()) {
						Exceptions.endOfIterator();
					}

					this.lastEntry = this.nextEntry;
					this.nextEntry = this.nextEntry(this.nextEntry + 1);
					return IntKeyOpenHashMap.this.values[this.lastEntry];
				}

				
				public void remove() {
					if (this.lastEntry == -1) {
						Exceptions.noElementToRemove();
					}

					IntKeyOpenHashMap.this.states[this.lastEntry] = 2;
					IntKeyOpenHashMap.this.values[this.lastEntry] = null;
					IntKeyOpenHashMap.this.size--;
					this.lastEntry = -1;
				}
			};
		}

		public int size() {
			return IntKeyOpenHashMap.this.size;
		}

		ValueCollection(Object object) {
			this();
		}
	}

	private class KeySet extends AbstractIntSet {

		private KeySet() {
		}

		public void clear() {
			IntKeyOpenHashMap.this.clear();
		}

		public boolean contains(int int1) {
			return IntKeyOpenHashMap.this.containsKey(int1);
		}

		public IntIterator iterator() {
			return new IntIterator(){
				int nextEntry = this.nextEntry(0);
				int lastEntry = -1;
				
				int nextEntry(int var1) {
					while (var1 < IntKeyOpenHashMap.this.keys.length && IntKeyOpenHashMap.this.states[var1] != 1) {
						++var1;
					}

					return var1;
				}

				
				public boolean hasNext() {
					return this.nextEntry < IntKeyOpenHashMap.this.keys.length;
				}

				
				public int next() {
					if (!this.hasNext()) {
						Exceptions.endOfIterator();
					}

					this.lastEntry = this.nextEntry;
					this.nextEntry = this.nextEntry(this.nextEntry + 1);
					return IntKeyOpenHashMap.this.keys[this.lastEntry];
				}

				
				public void remove() {
					if (this.lastEntry == -1) {
						Exceptions.noElementToRemove();
					}

					IntKeyOpenHashMap.this.states[this.lastEntry] = 2;
					IntKeyOpenHashMap.this.values[this.lastEntry] = null;
					IntKeyOpenHashMap.this.size--;
					this.lastEntry = -1;
				}
			};
		}

		public boolean remove(int int1) {
			boolean boolean1 = IntKeyOpenHashMap.this.containsKey(int1);
			if (boolean1) {
				IntKeyOpenHashMap.this.remove(int1);
			}

			return boolean1;
		}

		public int size() {
			return IntKeyOpenHashMap.this.size;
		}

		KeySet(Object object) {
			this();
		}
	}
}
