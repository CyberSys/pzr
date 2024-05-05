package se.krka.kahlua.converter;


public class KahluaEnumConverter {

	private KahluaEnumConverter() {
	}

	public static void install(KahluaConverterManager kahluaConverterManager) {
		kahluaConverterManager.addJavaConverter(new JavaToLuaConverter(){
			
			public Object fromJavaToLua(Enum var1) {
				return var1.name();
			}

			
			public Class getJavaType() {
				return Enum.class;
			}
		});
		kahluaConverterManager.addLuaConverter(new LuaToJavaConverter(){
			
			public Enum fromLuaToJava(String var1, Class var2) throws IllegalArgumentException {
				return Enum.valueOf(var2, var1);
			}

			
			public Class getJavaType() {
				return Enum.class;
			}

			
			public Class getLuaType() {
				return String.class;
			}
		});
	}
}
