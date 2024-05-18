package zombie.Lua;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;
import zombie.debug.DebugLog;


public final class LuaHookManager implements JavaFunction {
	public static ArrayList OnTickCallbacks = new ArrayList();
	private static ArrayList EventList = new ArrayList();
	private static HashMap EventMap = new HashMap();
	static Object[] a = new Object[1];
	static Object[] b = new Object[2];
	static Object[] c = new Object[3];
	static Object[] d = new Object[4];
	static Object[] f = new Object[5];
	static Object[] g = new Object[6];

	public static boolean TriggerHook(String string) {
		if (EventMap.containsKey(string)) {
			Event event = (Event)EventMap.get(string);
			a[0] = null;
			return event.trigger(LuaManager.env, LuaManager.caller, a);
		} else {
			return false;
		}
	}

	public static boolean TriggerHook(String string, Object object) {
		if (EventMap.containsKey(string)) {
			Event event = (Event)EventMap.get(string);
			a[0] = object;
			return event.trigger(LuaManager.env, LuaManager.caller, a);
		} else {
			return false;
		}
	}

	public static boolean TriggerHook(String string, Object object, Object object2) {
		if (EventMap.containsKey(string)) {
			Event event = (Event)EventMap.get(string);
			b[0] = object;
			b[1] = object2;
			return event.trigger(LuaManager.env, LuaManager.caller, b);
		} else {
			return false;
		}
	}

	public static boolean TriggerHook(String string, Object object, Object object2, Object object3) {
		if (EventMap.containsKey(string)) {
			Event event = (Event)EventMap.get(string);
			c[0] = object;
			c[1] = object2;
			c[2] = object3;
			return event.trigger(LuaManager.env, LuaManager.caller, c);
		} else {
			return false;
		}
	}

	public static boolean TriggerHook(String string, Object object, Object object2, Object object3, Object object4) {
		if (EventMap.containsKey(string)) {
			Event event = (Event)EventMap.get(string);
			d[0] = object;
			d[1] = object2;
			d[2] = object3;
			d[3] = object4;
			return event.trigger(LuaManager.env, LuaManager.caller, d);
		} else {
			return false;
		}
	}

	public static boolean TriggerHook(String string, Object object, Object object2, Object object3, Object object4, Object object5) {
		if (EventMap.containsKey(string)) {
			Event event = (Event)EventMap.get(string);
			f[0] = object;
			f[1] = object2;
			f[2] = object3;
			f[3] = object4;
			f[4] = object5;
			return event.trigger(LuaManager.env, LuaManager.caller, f);
		} else {
			return false;
		}
	}

	public static boolean TriggerHook(String string, Object object, Object object2, Object object3, Object object4, Object object5, Object object6) {
		if (EventMap.containsKey(string)) {
			Event event = (Event)EventMap.get(string);
			g[0] = object;
			g[1] = object2;
			g[2] = object3;
			g[3] = object4;
			g[4] = object5;
			g[5] = object6;
			return event.trigger(LuaManager.env, LuaManager.caller, g);
		} else {
			return false;
		}
	}

	public static void AddEvent(String string) {
		if (!EventMap.containsKey(string)) {
			Event event = new Event(string, EventList.size());
			EventList.add(event);
			EventMap.put(string, event);
			Object object = LuaManager.env.rawget("Hook");
			if (object instanceof KahluaTable) {
				KahluaTable kahluaTable = (KahluaTable)object;
				event.register(LuaManager.platform, kahluaTable);
			} else {
				DebugLog.log("ERROR: \'Hook\' table not found or not a table");
			}
		}
	}

	private static void AddEvents() {
		AddEvent("AutoDrink");
		AddEvent("UseItem");
		AddEvent("Attack");
		AddEvent("CalculateStats");
		AddEvent("WeaponHitCharacter");
		AddEvent("WeaponSwing");
		AddEvent("WeaponSwingHitPoint");
	}

	public static void clear() {
		a[0] = null;
		b[0] = null;
		b[1] = null;
		c[0] = null;
		c[1] = null;
		c[2] = null;
		d[0] = null;
		d[1] = null;
		d[2] = null;
		d[3] = null;
		f[0] = null;
		f[1] = null;
		f[2] = null;
		f[3] = null;
		f[4] = null;
		g[0] = null;
		g[1] = null;
		g[2] = null;
		g[3] = null;
		g[4] = null;
		g[5] = null;
	}

	public static void register(Platform platform, KahluaTable kahluaTable) {
		KahluaTable kahluaTable2 = platform.newTable();
		kahluaTable.rawset("Hook", kahluaTable2);
		AddEvents();
	}

	public int call(LuaCallFrame luaCallFrame, int int1) {
		return 0;
	}

	private int OnTick(LuaCallFrame luaCallFrame, int int1) {
		return 0;
	}

	public static void Reset() {
		Iterator iterator = EventList.iterator();
		while (iterator.hasNext()) {
			Event event = (Event)iterator.next();
			event.callbacks.clear();
		}

		EventList.clear();
		EventMap.clear();
	}
}
