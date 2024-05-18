package se.krka.kahlua.integration;

import java.util.AbstractList;


public abstract class LuaReturn extends AbstractList {
	protected final Object[] returnValues;

	protected LuaReturn(Object[] objectArray) {
		this.returnValues = objectArray;
	}

	public abstract boolean isSuccess();

	public abstract Object getErrorObject();

	public abstract String getErrorString();

	public abstract String getLuaStackTrace();

	public abstract RuntimeException getJavaException();

	public Object getFirst() {
		return this.get(0);
	}

	public Object getSecond() {
		return this.get(1);
	}

	public Object getThird() {
		return this.get(2);
	}

	public Object get(int int1) {
		int int2 = this.size();
		if (int1 >= 0 && int1 < int2) {
			return this.returnValues[int1 + 1];
		} else {
			throw new IndexOutOfBoundsException("The index " + int1 + " is outside the bounds [" + 0 + ", " + int2 + ")");
		}
	}

	public int size() {
		return this.returnValues.length - 1;
	}

	public static LuaReturn createReturn(Object[] objectArray) {
		Boolean Boolean1 = (Boolean)objectArray[0];
		return (LuaReturn)(Boolean1 ? new LuaSuccess(objectArray) : new LuaFail(objectArray));
	}
}
