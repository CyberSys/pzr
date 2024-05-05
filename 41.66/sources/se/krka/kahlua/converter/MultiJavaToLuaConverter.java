package se.krka.kahlua.converter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MultiJavaToLuaConverter implements JavaToLuaConverter {
	private final List converters = new ArrayList();
	private final Class clazz;

	public MultiJavaToLuaConverter(Class javaClass) {
		this.clazz = javaClass;
	}

	public Class getJavaType() {
		return this.clazz;
	}

	public Object fromJavaToLua(Object object) {
		Iterator iterator = this.converters.iterator();
		Object object2;
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			JavaToLuaConverter javaToLuaConverter = (JavaToLuaConverter)iterator.next();
			object2 = javaToLuaConverter.fromJavaToLua(object);
		} while (object2 == null);

		return object2;
	}

	public void add(JavaToLuaConverter javaToLuaConverter) {
		this.converters.add(javaToLuaConverter);
	}
}
