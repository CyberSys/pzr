package se.krka.kahlua.integration.expose;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.annotations.Desc;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.caller.ConstructorCaller;
import se.krka.kahlua.integration.expose.caller.MethodCaller;
import se.krka.kahlua.integration.processor.ClassParameterInformation;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;


public class LuaJavaClassExposer {
	private static final Object DEBUGINFO_KEY = new Object();
	private final KahluaConverterManager manager;
	private final Platform platform;
	private final KahluaTable environment;
	private final KahluaTable classMetatables;
	private final Set visitedTypes;
	private final KahluaTable autoExposeBase;
	private final Map shouldExposeCache;
	public final HashMap TypeMap;

	public LuaJavaClassExposer(KahluaConverterManager kahluaConverterManager, Platform platform, KahluaTable kahluaTable) {
		this(kahluaConverterManager, platform, kahluaTable, (KahluaTable)null);
	}

	public LuaJavaClassExposer(KahluaConverterManager kahluaConverterManager, Platform platform, KahluaTable kahluaTable, KahluaTable kahluaTable2) {
		this.visitedTypes = new HashSet();
		this.shouldExposeCache = new HashMap();
		this.TypeMap = new HashMap();
		this.manager = kahluaConverterManager;
		this.platform = platform;
		this.environment = kahluaTable;
		this.autoExposeBase = kahluaTable2;
		this.classMetatables = KahluaUtil.getClassMetatables(platform, this.environment);
		if (this.classMetatables.getMetatable() == null) {
			KahluaTable kahluaTable3 = platform.newTable();
			kahluaTable3.rawset("__index", new JavaFunction(){
				
				public int call(LuaCallFrame kahluaConverterManager, int platform) {
					Object kahluaTable = kahluaConverterManager.get(0);
					Object kahluaTable2 = kahluaConverterManager.get(1);
					if (kahluaTable != LuaJavaClassExposer.this.classMetatables) {
						throw new IllegalArgumentException("Expected classmetatables as the first argument to __index");
					} else if (kahluaTable2 != null && kahluaTable2 instanceof Class) {
						Class kahluaTable3 = (Class)kahluaTable2;
						if (!LuaJavaClassExposer.this.isExposed(kahluaTable3) && LuaJavaClassExposer.this.shouldExpose(kahluaTable3)) {
							LuaJavaClassExposer.this.exposeLikeJavaRecursively(kahluaTable3, LuaJavaClassExposer.this.environment);
							return kahluaConverterManager.push(LuaJavaClassExposer.this.classMetatables.rawget(kahluaTable3));
						} else {
							return kahluaConverterManager.pushNil();
						}
					} else {
						return kahluaConverterManager.pushNil();
					}
				}
			});

			this.classMetatables.setMetatable(kahluaTable3);
		}
	}

	public Map getClassDebugInformation() {
		Object object = this.environment.rawget(DEBUGINFO_KEY);
		if (object == null || !(object instanceof Map)) {
			object = new HashMap();
			this.environment.rawset(DEBUGINFO_KEY, object);
		}

		return (Map)object;
	}

	private KahluaTable getMetaTable(Class javaClass) {
		return (KahluaTable)this.classMetatables.rawget(javaClass);
	}

	private KahluaTable getIndexTable(KahluaTable kahluaTable) {
		if (kahluaTable == null) {
			return null;
		} else {
			Object object = kahluaTable.rawget("__index");
			if (object == null) {
				return null;
			} else {
				return object instanceof KahluaTable ? (KahluaTable)object : null;
			}
		}
	}

	public void exposeGlobalObjectFunction(KahluaTable kahluaTable, Object object, Method method) {
		this.exposeGlobalObjectFunction(kahluaTable, object, method, method.getName());
	}

	public void exposeGlobalObjectFunction(KahluaTable kahluaTable, Object object, Method method, String string) {
		Class javaClass = object.getClass();
		this.readDebugData(javaClass);
		LuaJavaInvoker luaJavaInvoker = this.getMethodInvoker(javaClass, method, string, object, false);
		this.addInvoker(kahluaTable, string, luaJavaInvoker);
	}

	public void exposeGlobalClassFunction(KahluaTable kahluaTable, Class javaClass, Constructor constructor, String string) {
		this.readDebugData(javaClass);
		LuaJavaInvoker luaJavaInvoker = this.getConstructorInvoker(javaClass, constructor, string);
		this.addInvoker(kahluaTable, string, luaJavaInvoker);
	}

	private LuaJavaInvoker getMethodInvoker(Class javaClass, Method method, String string, Object object, boolean boolean1) {
		return new LuaJavaInvoker(this, this.manager, javaClass, string, new MethodCaller(method, object, boolean1));
	}

	private LuaJavaInvoker getConstructorInvoker(Class javaClass, Constructor constructor, String string) {
		return new LuaJavaInvoker(this, this.manager, javaClass, string, new ConstructorCaller(constructor));
	}

	private LuaJavaInvoker getMethodInvoker(Class javaClass, Method method, String string) {
		return this.getMethodInvoker(javaClass, method, string, (Object)null, true);
	}

	private LuaJavaInvoker getGlobalInvoker(Class javaClass, Method method, String string) {
		return this.getMethodInvoker(javaClass, method, string, (Object)null, false);
	}

	public void exposeGlobalClassFunction(KahluaTable kahluaTable, Class javaClass, Method method, String string) {
		this.readDebugData(javaClass);
		if (Modifier.isStatic(method.getModifiers())) {
			this.addInvoker(kahluaTable, string, this.getGlobalInvoker(javaClass, method, string));
		}
	}

	public void exposeMethod(Class javaClass, Method method, KahluaTable kahluaTable) {
		this.exposeMethod(javaClass, method, method.getName(), kahluaTable);
	}

	public void exposeMethod(Class javaClass, Method method, String string, KahluaTable kahluaTable) {
		this.readDebugData(javaClass);
		if (!this.isExposed(javaClass)) {
			this.setupMetaTables(javaClass, kahluaTable);
		}

		KahluaTable kahluaTable2 = this.getMetaTable(javaClass);
		KahluaTable kahluaTable3 = this.getIndexTable(kahluaTable2);
		LuaJavaInvoker luaJavaInvoker = this.getMethodInvoker(javaClass, method, string);
		this.addInvoker(kahluaTable3, string, luaJavaInvoker);
	}

	private void addInvoker(KahluaTable kahluaTable, String string, LuaJavaInvoker luaJavaInvoker) {
		if (string.equals("setDir")) {
			boolean boolean1 = false;
		}

		Object object = kahluaTable.rawget(string);
		if (object != null) {
			if (object instanceof LuaJavaInvoker) {
				if (object.equals(luaJavaInvoker)) {
					return;
				}

				MultiLuaJavaInvoker multiLuaJavaInvoker = new MultiLuaJavaInvoker();
				multiLuaJavaInvoker.addInvoker((LuaJavaInvoker)object);
				multiLuaJavaInvoker.addInvoker(luaJavaInvoker);
				kahluaTable.rawset(string, multiLuaJavaInvoker);
			} else if (object instanceof MultiLuaJavaInvoker) {
				((MultiLuaJavaInvoker)object).addInvoker(luaJavaInvoker);
			}
		} else {
			kahluaTable.rawset(string, luaJavaInvoker);
		}
	}

	public boolean shouldExpose(Class javaClass) {
		if (javaClass == null) {
			return false;
		} else {
			Boolean Boolean1 = (Boolean)this.shouldExposeCache.get(javaClass);
			if (Boolean1 != null) {
				return Boolean1;
			} else if (this.autoExposeBase != null) {
				this.exposeLikeJavaRecursively(javaClass, this.autoExposeBase);
				return true;
			} else if (this.isExposed(javaClass)) {
				this.shouldExposeCache.put(javaClass, Boolean.TRUE);
				return true;
			} else if (this.shouldExpose(javaClass.getSuperclass())) {
				this.shouldExposeCache.put(javaClass, Boolean.TRUE);
				return true;
			} else {
				Class[] classArray = javaClass.getInterfaces();
				int int1 = classArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					Class javaClass2 = classArray[int2];
					if (this.shouldExpose(javaClass2)) {
						this.shouldExposeCache.put(javaClass, Boolean.TRUE);
						return true;
					}
				}

				this.shouldExposeCache.put(javaClass, Boolean.FALSE);
				return false;
			}
		}
	}

	private void setupMetaTables(Class javaClass, KahluaTable kahluaTable) {
		Class javaClass2 = javaClass.getSuperclass();
		this.exposeLikeJavaRecursively(javaClass2, kahluaTable);
		KahluaTable kahluaTable2 = this.getMetaTable(javaClass2);
		KahluaTable kahluaTable3 = this.platform.newTable();
		KahluaTable kahluaTable4 = this.platform.newTable();
		kahluaTable3.rawset("__index", kahluaTable4);
		if (kahluaTable2 != null) {
			kahluaTable3.rawset("__newindex", kahluaTable2.rawget("__newindex"));
		}

		kahluaTable4.setMetatable(kahluaTable2);
		this.classMetatables.rawset(javaClass, kahluaTable3);
	}

	private void addJavaEquals(KahluaTable kahluaTable) {
		kahluaTable.rawset("__eq", new JavaFunction(){
			
			public int call(LuaCallFrame kahluaTable, int var2) {
				boolean var3 = kahluaTable.get(0).equals(kahluaTable.get(1));
				kahluaTable.push(var3);
				return 1;
			}
		});
	}

	public void exposeGlobalFunctions(Object object) {
		Class javaClass = object.getClass();
		this.readDebugData(javaClass);
		Method[] methodArray = javaClass.getMethods();
		int int1 = methodArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			Method method = methodArray[int2];
			LuaMethod luaMethod = (LuaMethod)AnnotationUtil.getAnnotation(method, LuaMethod.class);
			if (luaMethod != null) {
				String string;
				if (luaMethod.name().equals("")) {
					string = method.getName();
				} else {
					string = luaMethod.name();
				}

				if (luaMethod.global()) {
					this.exposeGlobalObjectFunction(this.environment, object, method, string);
				}
			}
		}
	}

	public void exposeLikeJava(Class javaClass) {
		this.exposeLikeJava(javaClass, this.autoExposeBase);
	}

	public void exposeLikeJava(Class javaClass, KahluaTable kahluaTable) {
		if (javaClass != null && !this.isExposed(javaClass) && this.shouldExpose(javaClass)) {
			this.setupMetaTables(javaClass, kahluaTable);
			this.exposeMethods(javaClass, kahluaTable);
			if (!javaClass.isSynthetic() && !javaClass.isAnonymousClass() && !javaClass.isPrimitive() && !Proxy.isProxyClass(javaClass) && !javaClass.getSimpleName().startsWith("$")) {
				this.exposeStatics(javaClass, kahluaTable);
			}
		}
	}

	private void exposeStatics(Class javaClass, KahluaTable kahluaTable) {
		String[] stringArray = javaClass.getName().split("\\.");
		KahluaTable kahluaTable2 = this.createTableStructure(kahluaTable, stringArray);
		kahluaTable2.rawset("class", javaClass);
		if (kahluaTable.rawget(javaClass.getSimpleName()) == null) {
			kahluaTable.rawset(javaClass.getSimpleName(), kahluaTable2);
		}

		Method[] methodArray = javaClass.getMethods();
		int int1 = methodArray.length;
		int int2;
		String string;
		for (int2 = 0; int2 < int1; ++int2) {
			Method method = methodArray[int2];
			string = method.getName();
			if (Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())) {
				this.exposeGlobalClassFunction(kahluaTable2, javaClass, method, string);
			}
		}

		Field[] fieldArray = javaClass.getFields();
		int1 = fieldArray.length;
		for (int2 = 0; int2 < int1; ++int2) {
			Field field = fieldArray[int2];
			string = field.getName();
			if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
				try {
					kahluaTable2.rawset(string, field.get(javaClass));
				} catch (IllegalAccessException illegalAccessException) {
				}
			}
		}

		Constructor[] constructorArray = javaClass.getConstructors();
		int1 = constructorArray.length;
		for (int2 = 0; int2 < int1; ++int2) {
			Constructor constructor = constructorArray[int2];
			int int3 = constructor.getModifiers();
			if (!Modifier.isInterface(int3) && !Modifier.isAbstract(int3) && Modifier.isPublic(int3)) {
				this.addInvoker(kahluaTable2, "new", this.getConstructorInvoker(javaClass, constructor, "new"));
			}
		}
	}

	private void exposeMethods(Class javaClass, KahluaTable kahluaTable) {
		Method[] methodArray = javaClass.getMethods();
		int int1 = methodArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			Method method = methodArray[int2];
			String string = method.getName();
			if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
				this.exposeMethod(javaClass, method, string, kahluaTable);
			}
		}
	}

	private KahluaTable createTableStructure(KahluaTable kahluaTable, String[] stringArray) {
		String[] stringArray2 = stringArray;
		int int1 = stringArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			String string = stringArray2[int2];
			kahluaTable = KahluaUtil.getOrCreateTable(this.platform, kahluaTable, string);
		}

		return kahluaTable;
	}

	public boolean isExposed(Class javaClass) {
		return javaClass != null && this.getMetaTable(javaClass) != null;
	}

	ClassDebugInformation getDebugdata(Class javaClass) {
		this.readDebugDataD(javaClass);
		return (ClassDebugInformation)this.getClassDebugInformation().get(javaClass);
	}

	ClassDebugInformation getDebugdataA(Class javaClass) {
		return (ClassDebugInformation)this.getClassDebugInformation().get(javaClass);
	}

	private void readDebugDataD(Class javaClass) {
		if (this.getDebugdataA(javaClass) == null) {
			ClassParameterInformation classParameterInformation = null;
			try {
				classParameterInformation = ClassParameterInformation.getFromStream(javaClass);
			} catch (Exception exception) {
			}

			if (classParameterInformation == null) {
				classParameterInformation = new ClassParameterInformation(javaClass);
			}

			ClassDebugInformation classDebugInformation = new ClassDebugInformation(javaClass, classParameterInformation);
			Map map = this.getClassDebugInformation();
			map.put(javaClass, classDebugInformation);
		}
	}

	private void readDebugData(Class javaClass) {
	}

	@LuaMethod(global = true, name = "definition")
	@Desc("returns a string that describes the object")
	public String getDefinition(Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof LuaJavaInvoker) {
			MethodDebugInformation methodDebugInformation = ((LuaJavaInvoker)object).getMethodDebugData();
			return methodDebugInformation.toString();
		} else if (!(object instanceof MultiLuaJavaInvoker)) {
			return KahluaUtil.tostring(object, KahluaUtil.getWorkerThread(this.platform, this.environment));
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			Iterator iterator = ((MultiLuaJavaInvoker)object).getInvokers().iterator();
			while (iterator.hasNext()) {
				LuaJavaInvoker luaJavaInvoker = (LuaJavaInvoker)iterator.next();
				stringBuilder.append(luaJavaInvoker.getMethodDebugData().toString());
			}

			return stringBuilder.toString();
		}
	}

	public void exposeLikeJavaRecursively(Type type) {
		this.exposeLikeJavaRecursively(type, this.autoExposeBase);
	}

	public void exposeLikeJavaRecursively(Type type, KahluaTable kahluaTable) {
		this.exposeLikeJava(kahluaTable, this.visitedTypes, type);
	}

	private void exposeLikeJava(KahluaTable kahluaTable, Set set, Type type) {
		if (type != null) {
			if (!set.contains(type)) {
				set.add(type);
				if (type instanceof Class) {
					if (!this.shouldExpose((Class)type)) {
						return;
					}

					this.exposeLikeJavaByClass(kahluaTable, set, (Class)type);
				} else if (type instanceof WildcardType) {
					WildcardType wildcardType = (WildcardType)type;
					this.exposeList(kahluaTable, set, wildcardType.getLowerBounds());
					this.exposeList(kahluaTable, set, wildcardType.getUpperBounds());
				} else if (type instanceof ParameterizedType) {
					ParameterizedType parameterizedType = (ParameterizedType)type;
					this.exposeLikeJava(kahluaTable, set, parameterizedType.getRawType());
					this.exposeLikeJava(kahluaTable, set, parameterizedType.getOwnerType());
					this.exposeList(kahluaTable, set, parameterizedType.getActualTypeArguments());
				} else if (type instanceof TypeVariable) {
					TypeVariable typeVariable = (TypeVariable)type;
					this.exposeList(kahluaTable, set, typeVariable.getBounds());
				} else if (type instanceof GenericArrayType) {
					GenericArrayType genericArrayType = (GenericArrayType)type;
					this.exposeLikeJava(kahluaTable, set, genericArrayType.getGenericComponentType());
				}
			}
		}
	}

	private void exposeList(KahluaTable kahluaTable, Set set, Type[] typeArray) {
		Type[] typeArray2 = typeArray;
		int int1 = typeArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			Type type = typeArray2[int2];
			this.exposeLikeJava(kahluaTable, set, type);
		}
	}

	private void exposeLikeJavaByClass(KahluaTable kahluaTable, Set set, Class javaClass) {
		String string = javaClass.toString();
		string = string.substring(string.lastIndexOf(".") + 1);
		this.TypeMap.put(string, javaClass);
		this.exposeList(kahluaTable, set, javaClass.getInterfaces());
		this.exposeLikeJava(kahluaTable, set, javaClass.getGenericSuperclass());
		if (javaClass.isArray()) {
			this.exposeLikeJavaByClass(kahluaTable, set, javaClass.getComponentType());
		} else {
			this.exposeLikeJava(javaClass, kahluaTable);
		}

		Method[] methodArray = javaClass.getDeclaredMethods();
		int int1 = methodArray.length;
		int int2;
		for (int2 = 0; int2 < int1; ++int2) {
			Method method = methodArray[int2];
			this.exposeList(kahluaTable, set, method.getGenericParameterTypes());
			this.exposeList(kahluaTable, set, method.getGenericExceptionTypes());
			this.exposeLikeJava(kahluaTable, set, method.getGenericReturnType());
		}

		Field[] fieldArray = javaClass.getDeclaredFields();
		int1 = fieldArray.length;
		for (int2 = 0; int2 < int1; ++int2) {
			Field field = fieldArray[int2];
			this.exposeLikeJava(kahluaTable, set, field.getGenericType());
		}

		Constructor[] constructorArray = javaClass.getConstructors();
		int1 = constructorArray.length;
		for (int2 = 0; int2 < int1; ++int2) {
			Constructor constructor = constructorArray[int2];
			this.exposeList(kahluaTable, set, constructor.getParameterTypes());
			this.exposeList(kahluaTable, set, constructor.getExceptionTypes());
		}
	}

	public void destroy() {
		this.shouldExposeCache.clear();
		this.TypeMap.clear();
		this.visitedTypes.clear();
	}
}
