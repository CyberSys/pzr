package zombie.util.list;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;


public class PrimitiveFloatList extends AbstractList implements RandomAccess {
	private final float[] m_array;

	public PrimitiveFloatList(float[] floatArray) {
		this.m_array = (float[])Objects.requireNonNull(floatArray);
	}

	public int size() {
		return this.m_array.length;
	}

	public Object[] toArray() {
		return Arrays.asList(this.m_array).toArray();
	}

	public Object[] toArray(Object[] objectArray) {
		int int1 = this.size();
		for (int int2 = 0; int2 < int1 && int2 < objectArray.length; ++int2) {
			Float Float1 = this.m_array[int2];
			objectArray[int2] = Float1;
		}

		if (objectArray.length > int1) {
			objectArray[int1] = null;
		}

		return objectArray;
	}

	public Float get(int int1) {
		return this.m_array[int1];
	}

	public Float set(int int1, Float Float1) {
		return this.set(int1, Float1);
	}

	public float set(int int1, float float1) {
		float float2 = this.m_array[int1];
		this.m_array[int1] = float1;
		return float2;
	}

	public int indexOf(Object object) {
		if (object == null) {
			return -1;
		} else {
			return object instanceof Number ? this.indexOf(((Number)object).floatValue()) : -1;
		}
	}

	public int indexOf(float float1) {
		int int1 = -1;
		int int2 = 0;
		for (int int3 = this.size(); int2 < int3; ++int2) {
			if (this.m_array[int2] == float1) {
				int1 = int2;
				break;
			}
		}

		return int1;
	}

	public boolean contains(Object object) {
		return this.indexOf(object) != -1;
	}

	public boolean contains(float float1) {
		return this.indexOf(float1) != -1;
	}

	public void forEach(Consumer consumer) {
		Objects.requireNonNull(consumer);
		Objects.requireNonNull(consumer);
		this.forEach(consumer::accept);
	}

	public void forEach(FloatConsumer floatConsumer) {
		int int1 = 0;
		for (int int2 = this.size(); int1 < int2; ++int1) {
			floatConsumer.accept(this.m_array[int1]);
		}
	}

	public void replaceAll(UnaryOperator unaryOperator) {
		Objects.requireNonNull(unaryOperator);
		float[] floatArray = this.m_array;
		for (int int1 = 0; int1 < floatArray.length; ++int1) {
			floatArray[int1] = (Float)unaryOperator.apply(floatArray[int1]);
		}
	}

	public void sort(Comparator comparator) {
		this.sort();
	}

	public void sort() {
		Arrays.sort(this.m_array);
	}
}
