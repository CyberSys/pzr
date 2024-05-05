package se.krka.kahlua.converter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MultiLuaToJavaConverter implements LuaToJavaConverter {
	private final List converters = new ArrayList();
	private final Class luaType;
	private final Class javaType;

	public MultiLuaToJavaConverter(Class javaClass, Class javaClass2) {
		this.luaType = javaClass;
		this.javaType = javaClass2;
	}

	public Class getLuaType() {
		return this.luaType;
	}

	public Class getJavaType() {
		return this.javaType;
	}

	public Object fromLuaToJava(Object object, Class javaClass) {
		Iterator iterator = this.converters.iterator();
		Object object2;
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			LuaToJavaConverter luaToJavaConverter = (LuaToJavaConverter)iterator.next();
			object2 = luaToJavaConverter.fromLuaToJava(object, javaClass);
		} while (object2 == null);

		return object2;
	}

	public void add(LuaToJavaConverter luaToJavaConverter) {
		this.converters.add(luaToJavaConverter);
	}
}
