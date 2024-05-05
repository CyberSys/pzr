package se.krka.kahlua.converter;

import java.util.HashMap;
import java.util.Map;


public class KahluaConverterManager {
	private static final Map PRIMITIVE_CLASS = new HashMap();
	private static final Map LUA_NULL_MAP;
	private final Map luaToJava = new HashMap();
	private final Map luatoJavaCache = new HashMap();
	private static final JavaToLuaConverter NULL_CONVERTER;
	private final Map javaToLua = new HashMap();
	private final Map javaToLuaCache = new HashMap();

	public void addLuaConverter(LuaToJavaConverter luaToJavaConverter) {
		Map map = this.getOrCreate(this.luaToJava, luaToJavaConverter.getLuaType());
		Class javaClass = luaToJavaConverter.getJavaType();
		LuaToJavaConverter luaToJavaConverter2 = (LuaToJavaConverter)map.get(javaClass);
		if (luaToJavaConverter2 != null) {
			if (luaToJavaConverter2 instanceof MultiLuaToJavaConverter) {
				((MultiLuaToJavaConverter)luaToJavaConverter2).add(luaToJavaConverter);
			} else {
				MultiLuaToJavaConverter multiLuaToJavaConverter = new MultiLuaToJavaConverter(luaToJavaConverter.getLuaType(), javaClass);
				multiLuaToJavaConverter.add(luaToJavaConverter2);
				multiLuaToJavaConverter.add(luaToJavaConverter);
				map.put(javaClass, multiLuaToJavaConverter);
			}
		} else {
			map.put(javaClass, luaToJavaConverter);
		}

		this.luatoJavaCache.clear();
	}

	public void addJavaConverter(JavaToLuaConverter javaToLuaConverter) {
		Class javaClass = javaToLuaConverter.getJavaType();
		JavaToLuaConverter javaToLuaConverter2 = (JavaToLuaConverter)this.javaToLua.get(javaClass);
		if (javaToLuaConverter2 != null) {
			if (javaToLuaConverter2 instanceof MultiJavaToLuaConverter) {
				((MultiJavaToLuaConverter)javaToLuaConverter2).add(javaToLuaConverter);
			} else {
				MultiJavaToLuaConverter multiJavaToLuaConverter = new MultiJavaToLuaConverter(javaClass);
				multiJavaToLuaConverter.add(javaToLuaConverter2);
				multiJavaToLuaConverter.add(javaToLuaConverter);
				this.javaToLua.put(javaClass, multiJavaToLuaConverter);
			}
		} else {
			this.javaToLua.put(javaClass, javaToLuaConverter);
		}

		this.javaToLuaCache.clear();
	}

	private Map getOrCreate(Map map, Class javaClass) {
		Object object = (Map)map.get(javaClass);
		if (object == null) {
			object = new HashMap();
			map.put(javaClass, object);
		}

		return (Map)object;
	}

	public Object fromLuaToJava(Object object, Class javaClass) {
		if (object == null) {
			return null;
		} else {
			if (javaClass.isPrimitive()) {
				javaClass = (Class)PRIMITIVE_CLASS.get(javaClass);
			}

			if (javaClass.isInstance(object)) {
				return object;
			} else {
				Class javaClass2 = object.getClass();
				Map map = this.getLuaCache(javaClass2);
				for (Class javaClass3 = javaClass; javaClass3 != null; javaClass3 = javaClass3.getSuperclass()) {
					LuaToJavaConverter luaToJavaConverter = (LuaToJavaConverter)map.get(javaClass3);
					if (luaToJavaConverter != null) {
						Object object2 = luaToJavaConverter.fromLuaToJava(object, javaClass);
						if (object2 != null) {
							return object2;
						}
					}
				}

				return this.tryInterfaces(map, javaClass, object);
			}
		}
	}

	private Object tryInterfaces(Map map, Class javaClass, Object object) {
		if (javaClass == null) {
			return null;
		} else {
			LuaToJavaConverter luaToJavaConverter = (LuaToJavaConverter)map.get(javaClass);
			if (luaToJavaConverter != null) {
				Object object2 = luaToJavaConverter.fromLuaToJava(object, javaClass);
				if (object2 != null) {
					return object2;
				}
			}

			Class[] classArray = javaClass.getInterfaces();
			int int1 = classArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				Class javaClass2 = classArray[int2];
				Object object3 = this.tryInterfaces(map, javaClass2, object);
				if (object3 != null) {
					return object3;
				}
			}

			return this.tryInterfaces(map, javaClass.getSuperclass(), object);
		}
	}

	private Map createLuaCache(Class javaClass) {
		HashMap hashMap = new HashMap();
		this.luatoJavaCache.put(javaClass, hashMap);
		hashMap.putAll(this.getLuaCache(javaClass.getSuperclass()));
		Class[] classArray = javaClass.getInterfaces();
		int int1 = classArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			Class javaClass2 = classArray[int2];
			hashMap.putAll(this.getLuaCache(javaClass2));
		}

		Map map = (Map)this.luaToJava.get(javaClass);
		if (map != null) {
			hashMap.putAll(map);
		}

		return hashMap;
	}

	private Map getLuaCache(Class javaClass) {
		if (javaClass == null) {
			return LUA_NULL_MAP;
		} else {
			Map map = (Map)this.luatoJavaCache.get(javaClass);
			if (map == null) {
				map = this.createLuaCache(javaClass);
			}

			return map;
		}
	}

	public Object fromJavaToLua(Object object) {
		if (object == null) {
			return null;
		} else {
			Class javaClass = object.getClass();
			JavaToLuaConverter javaToLuaConverter = this.getJavaCache(javaClass);
			try {
				Object object2 = javaToLuaConverter.fromJavaToLua(object);
				return object2 == null ? object : object2;
			} catch (StackOverflowError stackOverflowError) {
				throw new RuntimeException("Could not convert " + object + ": it contained recursive elements.");
			}
		}
	}

	private JavaToLuaConverter getJavaCache(Class javaClass) {
		if (javaClass == null) {
			return NULL_CONVERTER;
		} else {
			JavaToLuaConverter javaToLuaConverter = (JavaToLuaConverter)this.javaToLuaCache.get(javaClass);
			if (javaToLuaConverter == null) {
				javaToLuaConverter = this.createJavaCache(javaClass);
				this.javaToLuaCache.put(javaClass, javaToLuaConverter);
			}

			return javaToLuaConverter;
		}
	}

	private JavaToLuaConverter createJavaCache(Class javaClass) {
		JavaToLuaConverter javaToLuaConverter = (JavaToLuaConverter)this.javaToLua.get(javaClass);
		if (javaToLuaConverter != null) {
			return javaToLuaConverter;
		} else {
			Class[] classArray = javaClass.getInterfaces();
			int int1 = classArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				Class javaClass2 = classArray[int2];
				javaToLuaConverter = this.getJavaCache(javaClass2);
				if (javaToLuaConverter != NULL_CONVERTER) {
					return javaToLuaConverter;
				}
			}

			return this.getJavaCache(javaClass.getSuperclass());
		}
	}

	static  {
		PRIMITIVE_CLASS.put(Boolean.TYPE, Boolean.class);
		PRIMITIVE_CLASS.put(Byte.TYPE, Byte.class);
		PRIMITIVE_CLASS.put(Character.TYPE, Character.class);
		PRIMITIVE_CLASS.put(Short.TYPE, Short.TYPE);
		PRIMITIVE_CLASS.put(Integer.TYPE, Integer.class);
		PRIMITIVE_CLASS.put(Long.TYPE, Long.class);
		PRIMITIVE_CLASS.put(Float.TYPE, Float.class);
		PRIMITIVE_CLASS.put(Double.TYPE, Double.class);
		LUA_NULL_MAP = new HashMap();
		NULL_CONVERTER = new JavaToLuaConverter(){
			
			public Object fromJavaToLua(Object var1) {
				return null;
			}

			
			public Class getJavaType() {
				return Object.class;
			}
		};
	}
}
