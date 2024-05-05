package zombie.util.map;

import zombie.util.hash.DefaultIntHashFunction;


public abstract class AbstractIntKeyMap implements IntKeyMap {

	protected AbstractIntKeyMap() {
	}

	public void clear() {
		IntKeyMapIterator intKeyMapIterator = this.entries();
		while (intKeyMapIterator.hasNext()) {
			intKeyMapIterator.next();
			intKeyMapIterator.remove();
		}
	}

	public Object remove(int int1) {
		IntKeyMapIterator intKeyMapIterator = this.entries();
		do {
			if (!intKeyMapIterator.hasNext()) {
				return null;
			}

			intKeyMapIterator.next();
		} while (intKeyMapIterator.getKey() != int1);

		Object object = intKeyMapIterator.getValue();
		intKeyMapIterator.remove();
		return object;
	}

	public void putAll(IntKeyMap intKeyMap) {
		IntKeyMapIterator intKeyMapIterator = intKeyMap.entries();
		while (intKeyMapIterator.hasNext()) {
			intKeyMapIterator.next();
			this.put(intKeyMapIterator.getKey(), intKeyMapIterator.getValue());
		}
	}

	public boolean containsKey(int int1) {
		IntKeyMapIterator intKeyMapIterator = this.entries();
		do {
			if (!intKeyMapIterator.hasNext()) {
				return false;
			}

			intKeyMapIterator.next();
		} while (intKeyMapIterator.getKey() != int1);

		return true;
	}

	public Object get(int int1) {
		IntKeyMapIterator intKeyMapIterator = this.entries();
		do {
			if (!intKeyMapIterator.hasNext()) {
				return null;
			}

			intKeyMapIterator.next();
		} while (intKeyMapIterator.getKey() != int1);

		return intKeyMapIterator.getValue();
	}

	public boolean containsValue(Object object) {
		IntKeyMapIterator intKeyMapIterator = this.entries();
		if (object == null) {
			while (intKeyMapIterator.hasNext()) {
				intKeyMapIterator.next();
				if (object == null) {
					return true;
				}
			}
		} else {
			while (intKeyMapIterator.hasNext()) {
				intKeyMapIterator.next();
				if (object.equals(intKeyMapIterator.getValue())) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean equals(Object object) {
		if (!(object instanceof IntKeyMap)) {
			return false;
		} else {
			IntKeyMap intKeyMap = (IntKeyMap)object;
			if (this.size() != intKeyMap.size()) {
				return false;
			} else {
				IntKeyMapIterator intKeyMapIterator = this.entries();
				while (intKeyMapIterator.hasNext()) {
					intKeyMapIterator.next();
					int int1 = intKeyMapIterator.getKey();
					Object object2 = intKeyMapIterator.getValue();
					if (object2 == null) {
						if (intKeyMap.get(int1) != null) {
							return false;
						}

						if (!intKeyMap.containsKey(int1)) {
							return false;
						}
					} else if (!object2.equals(intKeyMap.get(int1))) {
						return false;
					}
				}

				return true;
			}
		}
	}

	public int hashCode() {
		int int1 = 0;
		for (IntKeyMapIterator intKeyMapIterator = this.entries(); intKeyMapIterator.hasNext(); int1 += DefaultIntHashFunction.INSTANCE.hash(intKeyMapIterator.getKey()) ^ intKeyMapIterator.getValue().hashCode()) {
			intKeyMapIterator.next();
		}

		return int1;
	}

	public boolean isEmpty() {
		return this.size() == 0;
	}

	public int size() {
		int int1 = 0;
		for (IntKeyMapIterator intKeyMapIterator = this.entries(); intKeyMapIterator.hasNext(); ++int1) {
			intKeyMapIterator.next();
		}

		return int1;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append('[');
		IntKeyMapIterator intKeyMapIterator = this.entries();
		while (intKeyMapIterator.hasNext()) {
			if (stringBuilder.length() > 1) {
				stringBuilder.append(',');
			}

			intKeyMapIterator.next();
			stringBuilder.append(String.valueOf(intKeyMapIterator.getKey()));
			stringBuilder.append("->");
			stringBuilder.append(String.valueOf(intKeyMapIterator.getValue()));
		}

		stringBuilder.append(']');
		return stringBuilder.toString();
	}

	public void trimToSize() {
	}
}
