package zombie.text.templating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.debug.DebugLog;


public class ReplaceProvider implements IReplaceProvider {
	protected final Map m_keys = new HashMap();

	public void addKey(String string, String string2) {
		this.addReplacer(string, new IReplace(){
			
			public String getString() {
				return string2;
			}
		});
	}

	public void addKey(String string, KahluaTableImpl kahluaTableImpl) {
		try {
			ArrayList arrayList = new ArrayList();
			for (int int1 = 1; int1 < kahluaTableImpl.len() + 1; ++int1) {
				arrayList.add((String)kahluaTableImpl.rawget(int1));
			}

			if (arrayList.size() > 0) {
				this.addReplacer(string, new ReplaceList(arrayList));
			} else {
				DebugLog.log("ReplaceProvider -> key \'" + string + "\' contains no entries, ignoring.");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void addReplacer(String string, IReplace iReplace) {
		if (this.m_keys.containsKey(string.toLowerCase())) {
			DebugLog.log("ReplaceProvider -> Warning: key \'" + string + "\' replaces an existing key.");
		}

		this.m_keys.put(string.toLowerCase(), iReplace);
	}

	public boolean hasReplacer(String string) {
		return this.m_keys.containsKey(string);
	}

	public IReplace getReplacer(String string) {
		return (IReplace)this.m_keys.get(string);
	}
}
