package zombie.text.templating;

import java.util.Random;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.Lua.LuaEventManager;
import zombie.characters.SurvivorFactory;


public class TemplateText {
	private static final ITemplateBuilder builder = new TemplateTextBuilder();
	private static final Random m_random = new Random(4397238L);

	public static String Build(String string) {
		return builder.Build(string);
	}

	public static String Build(String string, IReplaceProvider iReplaceProvider) {
		return builder.Build(string, iReplaceProvider);
	}

	public static String Build(String string, KahluaTableImpl kahluaTableImpl) {
		try {
			return builder.Build(string, kahluaTableImpl);
		} catch (Exception exception) {
			exception.printStackTrace();
			return string;
		}
	}

	public static void RegisterKey(String string, KahluaTableImpl kahluaTableImpl) {
		builder.RegisterKey(string, kahluaTableImpl);
	}

	public static void RegisterKey(String string, IReplace iReplace) {
		builder.RegisterKey(string, iReplace);
	}

	public static void Initialize() {
		builder.RegisterKey("lastname", new IReplace(){
			
			public String getString() {
				return SurvivorFactory.getRandomSurname();
			}
		});
		builder.RegisterKey("firstname", new IReplace(){
			
			public String getString() {
				return TemplateText.RandNext(100) > 50 ? SurvivorFactory.getRandomForename(true) : SurvivorFactory.getRandomForename(false);
			}
		});
		builder.RegisterKey("maleName", new IReplace(){
			
			public String getString() {
				return SurvivorFactory.getRandomForename(false);
			}
		});
		builder.RegisterKey("femaleName", new IReplace(){
			
			public String getString() {
				return SurvivorFactory.getRandomForename(true);
			}
		});
		LuaEventManager.triggerEvent("OnTemplateTextInit");
	}

	public static void Reset() {
		builder.Reset();
	}

	public static float RandNext(float float1, float float2) {
		if (float1 == float2) {
			return float1;
		} else {
			if (float1 > float2) {
				float1 = float2;
				float2 = float2;
			}

			return float1 + m_random.nextFloat() * (float2 - float1);
		}
	}

	public static float RandNext(float float1) {
		return m_random.nextFloat() * float1;
	}

	public static int RandNext(int int1, int int2) {
		if (int1 == int2) {
			return int1;
		} else {
			if (int1 > int2) {
				int1 = int2;
				int2 = int2;
			}

			return int1 + m_random.nextInt(int2 - int1);
		}
	}

	public static int RandNext(int int1) {
		return m_random.nextInt(int1);
	}
}
