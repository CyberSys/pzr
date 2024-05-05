package se.krka.kahlua.converter;


public class KahluaNumberConverter {

	private KahluaNumberConverter() {
	}

	public static void install(KahluaConverterManager kahluaConverterManager) {
		kahluaConverterManager.addLuaConverter(new LuaToJavaConverter(){
			
			public Long fromLuaToJava(Double var1, Class var2) {
				return new Long(var1.longValue());
			}

			
			public Class getJavaType() {
				return Long.class;
			}

			
			public Class getLuaType() {
				return Double.class;
			}
		});
		kahluaConverterManager.addLuaConverter(new LuaToJavaConverter(){
			
			public Integer fromLuaToJava(Double var1, Class var2) {
				return new Integer(var1.intValue());
			}

			
			public Class getJavaType() {
				return Integer.class;
			}

			
			public Class getLuaType() {
				return Double.class;
			}
		});
		kahluaConverterManager.addLuaConverter(new LuaToJavaConverter(){
			
			public Float fromLuaToJava(Double var1, Class var2) {
				return new Float(var1.floatValue());
			}

			
			public Class getJavaType() {
				return Float.class;
			}

			
			public Class getLuaType() {
				return Double.class;
			}
		});
		kahluaConverterManager.addLuaConverter(new LuaToJavaConverter(){
			
			public Byte fromLuaToJava(Double var1, Class var2) {
				return new Byte(var1.byteValue());
			}

			
			public Class getJavaType() {
				return Byte.class;
			}

			
			public Class getLuaType() {
				return Double.class;
			}
		});
		kahluaConverterManager.addLuaConverter(new LuaToJavaConverter(){
			
			public Character fromLuaToJava(Double var1, Class var2) {
				return new Character((char)var1.intValue());
			}

			
			public Class getJavaType() {
				return Character.class;
			}

			
			public Class getLuaType() {
				return Double.class;
			}
		});
		kahluaConverterManager.addLuaConverter(new LuaToJavaConverter(){
			
			public Short fromLuaToJava(Double var1, Class var2) {
				return new Short(var1.shortValue());
			}

			
			public Class getJavaType() {
				return Short.class;
			}

			
			public Class getLuaType() {
				return Double.class;
			}
		});
		kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter(Double.class));
		kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter(Float.class));
		kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter(Integer.class));
		kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter(Long.class));
		kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter(Short.class));
		kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter(Byte.class));
		kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter(Character.class));
		kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter(Double.TYPE));
		kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter(Float.TYPE));
		kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter(Integer.TYPE));
		kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter(Long.TYPE));
		kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter(Short.TYPE));
		kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter(Byte.TYPE));
		kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter(Character.TYPE));
		kahluaConverterManager.addJavaConverter(new JavaToLuaConverter(){
			
			public Object fromJavaToLua(Boolean var1) {
				return var1;
			}

			
			public Class getJavaType() {
				return Boolean.class;
			}
		});
	}

	private static class NumberToLuaConverter implements JavaToLuaConverter {
		private final Class clazz;

		public NumberToLuaConverter(Class javaClass) {
			this.clazz = javaClass;
		}

		public Object fromJavaToLua(Number number) {
			return new Double(number.doubleValue());
		}

		public Class getJavaType() {
			return this.clazz;
		}
	}
}
