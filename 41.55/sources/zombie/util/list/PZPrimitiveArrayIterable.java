package zombie.util.list;

import java.util.Iterator;


public final class PZPrimitiveArrayIterable {

	public static Iterable fromArray(float[] floatArray) {
		return new Iterable(){
			private final float[] m_list = floatArray;
			
			public Iterator iterator() {
				return new Iterator(){
					private int pos = 0;
					
					public boolean hasNext() {
						return m_list.length > this.pos;
					}

					
					public Float next() {
						return m_list[this.pos++];
					}
				};
			}
		};
	}
}
