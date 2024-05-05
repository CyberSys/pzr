package zombie.world.moddata;

import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;


public final class ModData {
	private static final ArrayList temp_list = new ArrayList();

	public static ArrayList getTableNames() {
		GlobalModData.instance.collectTableNames(temp_list);
		return temp_list;
	}

	public static boolean exists(String string) {
		return GlobalModData.instance.exists(string);
	}

	public static KahluaTable getOrCreate(String string) {
		return GlobalModData.instance.getOrCreate(string);
	}

	public static KahluaTable get(String string) {
		return GlobalModData.instance.get(string);
	}

	public static String create() {
		return GlobalModData.instance.create();
	}

	public static KahluaTable create(String string) {
		return GlobalModData.instance.create(string);
	}

	public static KahluaTable remove(String string) {
		return GlobalModData.instance.remove(string);
	}

	public static void add(String string, KahluaTable kahluaTable) {
		GlobalModData.instance.add(string, kahluaTable);
	}

	public static void transmit(String string) {
		GlobalModData.instance.transmit(string);
	}

	public static void request(String string) {
		GlobalModData.instance.request(string);
	}
}
