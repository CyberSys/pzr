package zombie.util.set;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import zombie.util.IntCollection;
import zombie.util.IntIterator;
import zombie.util.hash.DefaultIntHashFunction;
import zombie.util.hash.IntHashFunction;
import zombie.util.hash.Primes;
import zombie.util.util.Exceptions;


public class IntOpenHashSet extends AbstractIntSet implements IntSet,Cloneable,Serializable {
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
	private transient int[] data;
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

	private IntOpenHashSet(IntHashFunction intHashFunction, int int1, int int2, double double1, int int3, double double2) {
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
		this.data = new int[int1];
		this.states = new byte[int1];
		this.size = 0;
		this.expandAt = (int)Math.round(double2 * (double)int1);
		this.used = 0;
		this.growthPolicy = int2;
		this.growthFactor = double1;
		this.growthChunk = int3;
		this.loadFactor = double2;
	}

	private IntOpenHashSet(int int1, int int2, double double1, int int3, double double2) {
		this(DefaultIntHashFunction.INSTANCE, int1, int2, double1, int3, double2);
	}

	public IntOpenHashSet() {
		this(11);
	}

	public IntOpenHashSet(IntCollection intCollection) {
		this();
		this.addAll(intCollection);
	}

	public IntOpenHashSet(int[] intArray) {
		this();
		int[] intArray2 = intArray;
		int int1 = intArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			int int3 = intArray2[int2];
			this.add(int3);
		}
	}

	public IntOpenHashSet(int int1) {
		this(int1, 0, 1.0, 10, 0.75);
	}

	public IntOpenHashSet(double double1) {
		this(11, 0, 1.0, 10, double1);
	}

	public IntOpenHashSet(int int1, double double1) {
		this(int1, 0, 1.0, 10, double1);
	}

	public IntOpenHashSet(int int1, double double1, double double2) {
		this(int1, 0, double2, 10, double1);
	}

	public IntOpenHashSet(int int1, double double1, int int2) {
		this(int1, 1, 1.0, int2, double1);
	}

	public IntOpenHashSet(IntHashFunction intHashFunction) {
		this(intHashFunction, 11, 0, 1.0, 10, 0.75);
	}

	public IntOpenHashSet(IntHashFunction intHashFunction, int int1) {
		this(intHashFunction, int1, 0, 1.0, 10, 0.75);
	}

	public IntOpenHashSet(IntHashFunction intHashFunction, double double1) {
		this(intHashFunction, 11, 0, 1.0, 10, double1);
	}

	public IntOpenHashSet(IntHashFunction intHashFunction, int int1, double double1) {
		this(intHashFunction, int1, 0, 1.0, 10, double1);
	}

	public IntOpenHashSet(IntHashFunction intHashFunction, int int1, double double1, double double2) {
		this(intHashFunction, int1, 0, double2, 10, double1);
	}

	public IntOpenHashSet(IntHashFunction intHashFunction, int int1, double double1, int int2) {
		this(intHashFunction, int1, 1, 1.0, int2, double1);
	}

	private void ensureCapacity(int int1) {
		if (int1 >= this.expandAt) {
			int int2;
			if (this.growthPolicy == 0) {
				int2 = (int)((double)this.data.length * (1.0 + this.growthFactor));
			} else {
				int2 = this.data.length + this.growthChunk;
			}

			if ((double)int2 * this.loadFactor < (double)int1) {
				int2 = (int)Math.round((double)int1 / this.loadFactor);
			}

			int2 = Primes.nextPrime(int2);
			this.expandAt = (int)Math.round(this.loadFactor * (double)int2);
			int[] intArray = new int[int2];
			byte[] byteArray = new byte[int2];
			this.used = 0;
			for (int int3 = 0; int3 < this.data.length; ++int3) {
				if (this.states[int3] == 1) {
					++this.used;
					int int4 = this.data[int3];
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
					intArray[int6] = int4;
				}
			}

			this.data = intArray;
			this.states = byteArray;
		}
	}

	public boolean add(int int1) {
		this.ensureCapacity(this.used + 1);
		int int2 = Math.abs(this.keyhash.hash(int1));
		int int3 = int2 % this.data.length;
		if (this.states[int3] == 1) {
			if (this.data[int3] == int1) {
				return false;
			}

			int int4 = 1 + int2 % (this.data.length - 2);
			while (true) {
				int3 -= int4;
				if (int3 < 0) {
					int3 += this.data.length;
				}

				if (this.states[int3] == 0 || this.states[int3] == 2) {
					break;
				}

				if (this.states[int3] == 1 && this.data[int3] == int1) {
					return false;
				}
			}
		}

		if (this.states[int3] == 0) {
			++this.used;
		}

		this.states[int3] = 1;
		this.data[int3] = int1;
		++this.size;
		return true;
	}

	public IntIterator iterator() {
		return new IntIterator(){
			int nextEntry = this.nextEntry(0);
			int lastEntry = -1;
			
			int nextEntry(int var1) {
				while (var1 < IntOpenHashSet.this.data.length && IntOpenHashSet.this.states[var1] != 1) {
					++var1;
				}

				return var1;
			}

			
			public boolean hasNext() {
				return this.nextEntry < IntOpenHashSet.this.data.length;
			}

			
			public int next() {
				if (!this.hasNext()) {
					Exceptions.endOfIterator();
				}

				this.lastEntry = this.nextEntry;
				this.nextEntry = this.nextEntry(this.nextEntry + 1);
				return IntOpenHashSet.this.data[this.lastEntry];
			}

			
			public void remove() {
				if (this.lastEntry == -1) {
					Exceptions.noElementToRemove();
				}

				IntOpenHashSet.this.states[this.lastEntry] = 2;
				--IntOpenHashSet.this.size;
				this.lastEntry = -1;
			}
		};
	}

	public void trimToSize() {
	}

	public Object clone() {
		try {
			IntOpenHashSet intOpenHashSet = (IntOpenHashSet)super.clone();
			intOpenHashSet.data = new int[this.data.length];
			System.arraycopy(this.data, 0, intOpenHashSet.data, 0, this.data.length);
			intOpenHashSet.states = new byte[this.data.length];
			System.arraycopy(this.states, 0, intOpenHashSet.states, 0, this.states.length);
			return intOpenHashSet;
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			Exceptions.cloning();
			throw new RuntimeException();
		}
	}

	public int size() {
		return this.size;
	}

	public void clear() {
		this.size = 0;
		this.used = 0;
		Arrays.fill(this.states, (byte)0);
	}

	public boolean contains(int int1) {
		int int2 = Math.abs(this.keyhash.hash(int1));
		int int3 = int2 % this.data.length;
		if (this.states[int3] == 0) {
			return false;
		} else if (this.states[int3] == 1 && this.data[int3] == int1) {
			return true;
		} else {
			int int4 = 1 + int2 % (this.data.length - 2);
			do {
				int3 -= int4;
				if (int3 < 0) {
					int3 += this.data.length;
				}

				if (this.states[int3] == 0) {
					return false;
				}
			}	 while (this.states[int3] != 1 || this.data[int3] != int1);

			return true;
		}
	}

	public int hashCode() {
		int int1 = 0;
		for (int int2 = 0; int2 < this.data.length; ++int2) {
			if (this.states[int2] == 1) {
				int1 += this.data[int2];
			}
		}

		return int1;
	}

	public boolean remove(int int1) {
		int int2 = Math.abs(this.keyhash.hash(int1));
		int int3 = int2 % this.data.length;
		if (this.states[int3] == 0) {
			return false;
		} else if (this.states[int3] == 1 && this.data[int3] == int1) {
			this.states[int3] = 2;
			--this.size;
			return true;
		} else {
			int int4 = 1 + int2 % (this.data.length - 2);
			do {
				int3 -= int4;
				if (int3 < 0) {
					int3 += this.data.length;
				}

				if (this.states[int3] == 0) {
					return false;
				}
			}	 while (this.states[int3] != 1 || this.data[int3] != int1);

			this.states[int3] = 2;
			--this.size;
			return true;
		}
	}

	public int[] toArray(int[] intArray) {
		if (intArray == null || intArray.length < this.size) {
			intArray = new int[this.size];
		}

		int int1 = 0;
		for (int int2 = 0; int2 < this.data.length; ++int2) {
			if (this.states[int2] == 1) {
				intArray[int1++] = this.data[int2];
			}
		}

		return intArray;
	}

	private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
		objectOutputStream.defaultWriteObject();
		objectOutputStream.writeInt(this.data.length);
		IntIterator intIterator = this.iterator();
		while (intIterator.hasNext()) {
			int int1 = intIterator.next();
			objectOutputStream.writeInt(int1);
		}
	}

	private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
		objectInputStream.defaultReadObject();
		this.data = new int[objectInputStream.readInt()];
		this.states = new byte[this.data.length];
		this.used = this.size;
		for (int int1 = 0; int1 < this.size; ++int1) {
			int int2 = objectInputStream.readInt();
			int int3 = Math.abs(this.keyhash.hash(int2));
			int int4 = int3 % this.data.length;
			if (this.states[int4] == 1) {
				int int5 = 1 + int3 % (this.data.length - 2);
				do {
					int4 -= int5;
					if (int4 < 0) {
						int4 += this.data.length;
					}
				}		 while (this.states[int4] != 0);
			}

			this.states[int4] = 1;
			this.data[int4] = int2;
		}
	}
}
