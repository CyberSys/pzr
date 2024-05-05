package se.krka.kahlua.converter;


public interface LuaToJavaConverter {

	Class getLuaType();

	Class getJavaType();

	Object fromLuaToJava(Object object, Class javaClass);
}
