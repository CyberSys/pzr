package zombie.text.templating;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedDeque;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.debug.DebugLog;


public class ReplaceProviderLua extends ReplaceProvider {
	private static final ConcurrentLinkedDeque pool_single = new ConcurrentLinkedDeque();
	private static final ConcurrentLinkedDeque pool_list = new ConcurrentLinkedDeque();
	private static final ConcurrentLinkedDeque pool = new ConcurrentLinkedDeque();

	private static ReplaceSingle alloc_single() {
		ReplaceSingle replaceSingle = (ReplaceSingle)pool_single.poll();
		if (replaceSingle == null) {
			replaceSingle = new ReplaceSingle();
		}

		return replaceSingle;
	}

	private static void release_single(ReplaceSingle replaceSingle) {
		pool_single.offer(replaceSingle);
	}

	private static ReplaceList alloc_list() {
		ReplaceList replaceList = (ReplaceList)pool_list.poll();
		if (replaceList == null) {
			replaceList = new ReplaceList();
		}

		return replaceList;
	}

	private static void release_list(ReplaceList replaceList) {
		replaceList.getReplacements().clear();
		pool_list.offer(replaceList);
	}

	protected static ReplaceProviderLua Alloc() {
		ReplaceProviderLua replaceProviderLua = (ReplaceProviderLua)pool.poll();
		if (replaceProviderLua == null) {
			replaceProviderLua = new ReplaceProviderLua();
		}

		replaceProviderLua.reset();
		return replaceProviderLua;
	}

	private void reset() {
		Iterator iterator = this.m_keys.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			if (entry.getValue() instanceof ReplaceList) {
				release_list((ReplaceList)entry.getValue());
			} else {
				release_single((ReplaceSingle)entry.getValue());
			}
		}

		this.m_keys.clear();
	}

	public void release() {
		this.reset();
		pool.offer(this);
	}

	public void fromLuaTable(KahluaTableImpl kahluaTableImpl) {
		Iterator iterator = kahluaTableImpl.delegate.entrySet().iterator();
		while (true) {
			while (true) {
				Entry entry;
				do {
					if (!iterator.hasNext()) {
						return;
					}

					entry = (Entry)iterator.next();
				}		 while (!(entry.getKey() instanceof String));

				if (entry.getValue() instanceof String) {
					this.addKey((String)entry.getKey(), (String)entry.getValue());
				} else if (entry.getValue() instanceof KahluaTableImpl) {
					KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)entry.getValue();
					ReplaceList replaceList = alloc_list();
					for (int int1 = 1; int1 < kahluaTableImpl2.len() + 1; ++int1) {
						replaceList.getReplacements().add((String)kahluaTableImpl2.rawget(int1));
					}

					if (replaceList.getReplacements().size() > 0) {
						this.addReplacer((String)entry.getKey(), replaceList);
					} else {
						DebugLog.log("ReplaceProvider -> key \'" + entry.getKey() + "\' contains no entries, ignoring.");
						release_list(replaceList);
					}
				}
			}
		}
	}

	public void addKey(String string, String string2) {
		ReplaceSingle replaceSingle = alloc_single();
		replaceSingle.setValue(string2);
		this.addReplacer(string, replaceSingle);
	}
}
