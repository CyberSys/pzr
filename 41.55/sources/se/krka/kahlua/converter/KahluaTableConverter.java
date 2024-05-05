package se.krka.kahlua.converter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.Platform;


public class KahluaTableConverter {
	private final Platform platform;

	public KahluaTableConverter(Platform platform) {
		this.platform = platform;
	}

	public void install(KahluaConverterManager kahluaConverterManager) {
		kahluaConverterManager.addJavaConverter(new KahluaTableConverter.CollectionToLuaConverter(kahluaConverterManager, Collection.class));
		kahluaConverterManager.addLuaConverter(new KahluaTableConverter.CollectionToJavaConverter(Collection.class));
		kahluaConverterManager.addJavaConverter(new JavaToLuaConverter(){
			
			public Object fromJavaToLua(Map kahluaConverterManagerx) {
				KahluaTable var3 = KahluaTableConverter.this.platform.newTable();
				Iterator var4 = kahluaConverterManagerx.entrySet().iterator();
				while (var4.hasNext()) {
					Entry var5 = (Entry)var4.next();
					Object var6 = kahluaConverterManager.fromJavaToLua(var5.getKey());
					Object var7 = kahluaConverterManager.fromJavaToLua(var5.getValue());
					var3.rawset(var6, var7);
				}

				return var3;
			}

			
			public Class getJavaType() {
				return Map.class;
			}
		});
		kahluaConverterManager.addLuaConverter(new LuaToJavaConverter(){
			
			public Map fromLuaToJava(KahluaTable kahluaConverterManager, Class var2) throws IllegalArgumentException {
				KahluaTableIterator var3 = kahluaConverterManager.iterator();
				HashMap var4 = new HashMap();
				while (var3.advance()) {
					Object var5 = var3.getKey();
					Object var6 = var3.getValue();
					var4.put(var5, var6);
				}

				return var4;
			}

			
			public Class getJavaType() {
				return Map.class;
			}

			
			public Class getLuaType() {
				return KahluaTable.class;
			}
		});
		kahluaConverterManager.addJavaConverter(new JavaToLuaConverter(){
			
			public Object fromJavaToLua(Object kahluaConverterManagerx) {
				if (!kahluaConverterManagerx.getClass().isArray()) {
					return null;
				} else {
					KahluaTable var2 = KahluaTableConverter.this.platform.newTable();
					int var3 = Array.getLength(kahluaConverterManagerx);
					for (int var4 = 0; var4 < var3; ++var4) {
						Object var5 = Array.get(kahluaConverterManagerx, var4);
						var2.rawset(var4 + 1, kahluaConverterManager.fromJavaToLua(var5));
					}

					return var2;
				}
			}

			
			public Class getJavaType() {
				return Object.class;
			}
		});
		kahluaConverterManager.addLuaConverter(new LuaToJavaConverter(){
			
			public Object fromLuaToJava(KahluaTable kahluaConverterManagerx, Class var2) throws IllegalArgumentException {
				if (var2.isArray()) {
					List var3 = (List)kahluaConverterManager.fromLuaToJava(kahluaConverterManagerx, List.class);
					return var3.toArray();
				} else {
					return null;
				}
			}

			
			public Class getJavaType() {
				return Object.class;
			}

			
			public Class getLuaType() {
				return KahluaTable.class;
			}
		});
	}

	private class CollectionToLuaConverter implements JavaToLuaConverter {
		private final Class clazz;
		private final KahluaConverterManager manager;

		public CollectionToLuaConverter(KahluaConverterManager kahluaConverterManager, Class javaClass) {
			this.manager = kahluaConverterManager;
			this.clazz = javaClass;
		}

		public Object fromJavaToLua(Iterable iterable) {
			KahluaTable kahluaTable = KahluaTableConverter.this.platform.newTable();
			int int1 = 0;
			Iterator iterator = iterable.iterator();
			while (iterator.hasNext()) {
				Object object = iterator.next();
				++int1;
				kahluaTable.rawset(int1, this.manager.fromJavaToLua(object));
			}

			return kahluaTable;
		}

		public Class getJavaType() {
			return this.clazz;
		}
	}

	private static class CollectionToJavaConverter implements LuaToJavaConverter {
		private final Class javaClass;

		private CollectionToJavaConverter(Class javaClass) {
			this.javaClass = javaClass;
		}

		public Object fromLuaToJava(KahluaTable kahluaTable, Class javaClass) throws IllegalArgumentException {
			int int1 = kahluaTable.len();
			ArrayList arrayList = new ArrayList(int1);
			for (int int2 = 1; int2 <= int1; ++int2) {
				Object object = kahluaTable.rawget(int2);
				arrayList.add(object);
			}

			return arrayList;
		}

		public Class getJavaType() {
			return this.javaClass;
		}

		public Class getLuaType() {
			return KahluaTable.class;
		}
	}
}
