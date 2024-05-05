package zombie.core.Collections;

import java.util.Map.Entry;


abstract class AbstractEntry implements Entry {
	protected final Object _key;
	protected Object _val;

	public AbstractEntry(Object object, Object object2) {
		this._key = object;
		this._val = object2;
	}

	public AbstractEntry(Entry entry) {
		this._key = entry.getKey();
		this._val = entry.getValue();
	}

	public String toString() {
		return this._key + "=" + this._val;
	}

	public Object getKey() {
		return this._key;
	}

	public Object getValue() {
		return this._val;
	}

	public boolean equals(Object object) {
		if (!(object instanceof Entry)) {
			return false;
		} else {
			Entry entry = (Entry)object;
			return eq(this._key, entry.getKey()) && eq(this._val, entry.getValue());
		}
	}

	public int hashCode() {
		return (this._key == null ? 0 : this._key.hashCode()) ^ (this._val == null ? 0 : this._val.hashCode());
	}

	private static boolean eq(Object object, Object object2) {
		return object == null ? object2 == null : object.equals(object2);
	}
}
