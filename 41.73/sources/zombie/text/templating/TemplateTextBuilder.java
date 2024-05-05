package zombie.text.templating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.debug.DebugLog;


public class TemplateTextBuilder implements ITemplateBuilder {
	private static final String fieldStart = "\\$\\{";
	private static final String fieldEnd = "\\}";
	private static final String regex = "\\$\\{([^}]+)\\}";
	private static final Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
	private Map m_keys = new HashMap();

	protected TemplateTextBuilder() {
	}

	public void Reset() {
		this.m_keys.clear();
	}

	public String Build(String string) {
		return this.format(string, (IReplaceProvider)null);
	}

	public String Build(String string, IReplaceProvider iReplaceProvider) {
		return this.format(string, iReplaceProvider);
	}

	public String Build(String string, KahluaTableImpl kahluaTableImpl) {
		ReplaceProviderLua replaceProviderLua = ReplaceProviderLua.Alloc();
		replaceProviderLua.fromLuaTable(kahluaTableImpl);
		String string2 = this.format(string, replaceProviderLua);
		replaceProviderLua.release();
		return string2;
	}

	private String format(String string, IReplaceProvider iReplaceProvider) {
		Matcher matcher = pattern.matcher(string);
		String string2;
		String string3;
		for (string2 = string; matcher.find(); string2 = string2.replaceFirst("\\$\\{([^}]+)\\}", string3)) {
			String string4 = matcher.group(1).toLowerCase().trim();
			string3 = null;
			if (iReplaceProvider != null && iReplaceProvider.hasReplacer(string4)) {
				string3 = iReplaceProvider.getReplacer(string4).getString();
			} else {
				IReplace iReplace = (IReplace)this.m_keys.get(string4);
				if (iReplace != null) {
					string3 = iReplace.getString();
				}
			}

			if (string3 == null) {
				string3 = "missing_" + string4;
			}
		}

		return string2;
	}

	public void RegisterKey(String string, KahluaTableImpl kahluaTableImpl) {
		try {
			ArrayList arrayList = new ArrayList();
			for (int int1 = 1; int1 < kahluaTableImpl.len() + 1; ++int1) {
				arrayList.add((String)kahluaTableImpl.rawget(int1));
			}

			if (arrayList.size() > 0) {
				this.localRegisterKey(string, new ReplaceList(arrayList));
			} else {
				DebugLog.log("TemplateTextBuilder -> key \'" + string + "\' contains no entries, ignoring.");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void RegisterKey(String string, IReplace iReplace) {
		this.localRegisterKey(string, iReplace);
	}

	private void localRegisterKey(String string, IReplace iReplace) {
		if (this.m_keys.containsKey(string.toLowerCase().trim())) {
			DebugLog.log("TemplateTextBuilder -> Warning: key \'" + string + "\' replaces an existing key.");
		}

		this.m_keys.put(string.toLowerCase().trim(), iReplace);
	}
}
