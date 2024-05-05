package zombie.util.list;

import java.util.AbstractList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;


public final class PZConvertList extends AbstractList implements RandomAccess {
	private final List m_list;
	private final Function m_converterST;
	private final Function m_converterTS;

	public PZConvertList(List list, Function function) {
		this(list, function, (Function)null);
	}

	public PZConvertList(List list, Function function, Function function2) {
		this.m_list = (List)Objects.requireNonNull(list);
		this.m_converterST = function;
		this.m_converterTS = function2;
	}

	public boolean isReadonly() {
		return this.m_converterTS == null;
	}

	public int size() {
		return this.m_list.size();
	}

	public Object[] toArray() {
		return this.m_list.toArray();
	}

	public Object[] toArray(Object[] objectArray) {
		int int1 = this.size();
		for (int int2 = 0; int2 < int1 && int2 < objectArray.length; ++int2) {
			Object object = this.get(int2);
			objectArray[int2] = object;
		}

		if (objectArray.length > int1) {
			objectArray[int1] = null;
		}

		return objectArray;
	}

	public Object get(int int1) {
		return this.convertST(this.m_list.get(int1));
	}

	public Object set(int int1, Object object) {
		Object object2 = this.get(int1);
		this.setS(int1, this.convertTS(object));
		return object2;
	}

	public Object setS(int int1, Object object) {
		Object object2 = this.m_list.get(int1);
		this.m_list.set(int1, object);
		return object2;
	}

	public int indexOf(Object object) {
		int int1 = -1;
		int int2 = 0;
		for (int int3 = this.size(); int2 < int3; ++int2) {
			if (objectsEqual(object, this.get(int2))) {
				int1 = int2;
				break;
			}
		}

		return int1;
	}

	private static boolean objectsEqual(Object object, Object object2) {
		return object == object2 || object != null && object.equals(object2);
	}

	public boolean contains(Object object) {
		return this.indexOf(object) != -1;
	}

	public void forEach(Consumer consumer) {
		int int1 = 0;
		for (int int2 = this.size(); int1 < int2; ++int1) {
			consumer.accept(this.get(int1));
		}
	}

	public void replaceAll(UnaryOperator unaryOperator) {
		Objects.requireNonNull(unaryOperator);
		int int1 = 0;
		for (int int2 = this.size(); int1 < int2; ++int1) {
			Object object = this.get(int1);
			Object object2 = unaryOperator.apply(object);
			this.set(int1, object2);
		}
	}

	public void sort(Comparator comparator) {
		this.m_list.sort((var2,var3)->{
			return comparator.compare(this.convertST(var2), this.convertST(var3));
		});
	}

	private Object convertST(Object object) {
		return this.m_converterST.apply(object);
	}

	private Object convertTS(Object object) {
		return this.m_converterTS.apply(object);
	}
}
