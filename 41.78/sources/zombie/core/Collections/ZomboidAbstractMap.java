package zombie.core.Collections;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


public abstract class ZomboidAbstractMap implements Map {
	transient volatile Set keySet = null;
	transient volatile Collection values = null;

	protected ZomboidAbstractMap() {
	}

	public int size() {
		return this.entrySet().size();
	}

	public boolean isEmpty() {
		return this.size() == 0;
	}

	public boolean containsValue(Object object) {
		Iterator iterator = this.entrySet().iterator();
		Entry entry;
		if (object == null) {
			while (iterator.hasNext()) {
				entry = (Entry)iterator.next();
				if (entry.getValue() == null) {
					return true;
				}
			}
		} else {
			while (iterator.hasNext()) {
				entry = (Entry)iterator.next();
				if (object.equals(entry.getValue())) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean containsKey(Object object) {
		Iterator iterator = this.entrySet().iterator();
		Entry entry;
		if (object == null) {
			while (iterator.hasNext()) {
				entry = (Entry)iterator.next();
				if (entry.getKey() == null) {
					return true;
				}
			}
		} else {
			while (iterator.hasNext()) {
				entry = (Entry)iterator.next();
				if (object.equals(entry.getKey())) {
					return true;
				}
			}
		}

		return false;
	}

	public Object get(Object object) {
		Iterator iterator = this.entrySet().iterator();
		Entry entry;
		if (object == null) {
			while (iterator.hasNext()) {
				entry = (Entry)iterator.next();
				if (entry.getKey() == null) {
					return entry.getValue();
				}
			}
		} else {
			while (iterator.hasNext()) {
				entry = (Entry)iterator.next();
				if (object.equals(entry.getKey())) {
					return entry.getValue();
				}
			}
		}

		return null;
	}

	public Object put(Object object, Object object2) {
		throw new UnsupportedOperationException();
	}

	public Object remove(Object object) {
		Iterator iterator = this.entrySet().iterator();
		Entry entry = null;
		Entry entry2;
		if (object == null) {
			while (entry == null && iterator.hasNext()) {
				entry2 = (Entry)iterator.next();
				if (entry2.getKey() == null) {
					entry = entry2;
				}
			}
		} else {
			while (entry == null && iterator.hasNext()) {
				entry2 = (Entry)iterator.next();
				if (object.equals(entry2.getKey())) {
					entry = entry2;
				}
			}
		}

		Object object2 = null;
		if (entry != null) {
			object2 = entry.getValue();
			iterator.remove();
		}

		return object2;
	}

	public void putAll(Map map) {
		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			this.put(entry.getKey(), entry.getValue());
		}
	}

	public void clear() {
		this.entrySet().clear();
	}

	public Set keySet() {
		if (this.keySet == null) {
			this.keySet = new AbstractSet(){
				
				public Iterator iterator() {
					return new Iterator(){
						private Iterator i = ZomboidAbstractMap.this.entrySet().iterator();
						
						public boolean hasNext() {
							return this.i.hasNext();
						}

						
						public Object next() {
							return ((Entry)this.i.next()).getKey();
						}

						
						public void remove() {
							this.i.remove();
						}
					};
				}

				
				public int size() {
					return ZomboidAbstractMap.this.size();
				}

				
				public boolean contains(Object var1) {
					return ZomboidAbstractMap.this.containsKey(var1);
				}
			};
		}

		return this.keySet;
	}

	public Collection values() {
		if (this.values == null) {
			this.values = new AbstractCollection(){
				
				public Iterator iterator() {
					return new Iterator(){
						private Iterator i = ZomboidAbstractMap.this.entrySet().iterator();
						
						public boolean hasNext() {
							return this.i.hasNext();
						}

						
						public Object next() {
							return ((Entry)this.i.next()).getValue();
						}

						
						public void remove() {
							this.i.remove();
						}
					};
				}

				
				public int size() {
					return ZomboidAbstractMap.this.size();
				}

				
				public boolean contains(Object var1) {
					return ZomboidAbstractMap.this.containsValue(var1);
				}
			};
		}

		return this.values;
	}

	public abstract Set entrySet();

	public boolean equals(Object object) {
		if (object == this) {
			return true;
		} else if (!(object instanceof Map)) {
			return false;
		} else {
			Map map = (Map)object;
			if (map.size() != this.size()) {
				return false;
			} else {
				try {
					Iterator iterator = this.entrySet().iterator();
					Object object2;
					label43: do {
						Object object3;
						do {
							if (!iterator.hasNext()) {
								return true;
							}

							Entry entry = (Entry)iterator.next();
							object2 = entry.getKey();
							object3 = entry.getValue();
							if (object3 == null) {
								continue label43;
							}
						}				 while (object3.equals(map.get(object2)));

						return false;
					}			 while (map.get(object2) == null && map.containsKey(object2));

					return false;
				} catch (ClassCastException classCastException) {
					return false;
				} catch (NullPointerException nullPointerException) {
					return false;
				}
			}
		}
	}

	public int hashCode() {
		int int1 = 0;
		for (Iterator iterator = this.entrySet().iterator(); iterator.hasNext(); int1 += ((Entry)iterator.next()).hashCode()) {
		}

		return int1;
	}

	public String toString() {
		Iterator iterator = this.entrySet().iterator();
		if (!iterator.hasNext()) {
			return "{}";
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append('{');
			while (true) {
				Entry entry = (Entry)iterator.next();
				Object object = entry.getKey();
				Object object2 = entry.getValue();
				stringBuilder.append(object == this ? "(this Map)" : object);
				stringBuilder.append('=');
				stringBuilder.append(object2 == this ? "(this Map)" : object2);
				if (!iterator.hasNext()) {
					return stringBuilder.append('}').toString();
				}

				stringBuilder.append(", ");
			}
		}
	}

	protected Object clone() throws CloneNotSupportedException {
		ZomboidAbstractMap zomboidAbstractMap = (ZomboidAbstractMap)super.clone();
		zomboidAbstractMap.keySet = null;
		zomboidAbstractMap.values = null;
		return zomboidAbstractMap;
	}

	private static boolean eq(Object object, Object object2) {
		return object == null ? object2 == null : object.equals(object2);
	}

	public static class SimpleImmutableEntry implements Entry,Serializable {
		private static final long serialVersionUID = 7138329143949025153L;
		private final Object key;
		private final Object value;

		public SimpleImmutableEntry(Object object, Object object2) {
			this.key = object;
			this.value = object2;
		}

		public SimpleImmutableEntry(Entry entry) {
			this.key = entry.getKey();
			this.value = entry.getValue();
		}

		public Object getKey() {
			return this.key;
		}

		public Object getValue() {
			return this.value;
		}

		public Object setValue(Object object) {
			throw new UnsupportedOperationException();
		}

		public boolean equals(Object object) {
			if (!(object instanceof Entry)) {
				return false;
			} else {
				Entry entry = (Entry)object;
				return ZomboidAbstractMap.eq(this.key, entry.getKey()) && ZomboidAbstractMap.eq(this.value, entry.getValue());
			}
		}

		public int hashCode() {
			return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
		}

		public String toString() {
			return this.key + "=" + this.value;
		}
	}

	public static class SimpleEntry implements Entry,Serializable {
		private static final long serialVersionUID = -8499721149061103585L;
		private final Object key;
		private Object value;

		public SimpleEntry(Object object, Object object2) {
			this.key = object;
			this.value = object2;
		}

		public SimpleEntry(Entry entry) {
			this.key = entry.getKey();
			this.value = entry.getValue();
		}

		public Object getKey() {
			return this.key;
		}

		public Object getValue() {
			return this.value;
		}

		public Object setValue(Object object) {
			Object object2 = this.value;
			this.value = object;
			return object2;
		}

		public boolean equals(Object object) {
			if (!(object instanceof Entry)) {
				return false;
			} else {
				Entry entry = (Entry)object;
				return ZomboidAbstractMap.eq(this.key, entry.getKey()) && ZomboidAbstractMap.eq(this.value, entry.getValue());
			}
		}

		public int hashCode() {
			return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
		}

		public String toString() {
			return this.key + "=" + this.value;
		}
	}
}
