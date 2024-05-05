package zombie.core.znet;

import zombie.debug.DebugLog;
import zombie.debug.DebugType;


public class PortMapper {
	private static String externalAddress = null;

	public static void startup() {
	}

	public static void shutdown() {
		_cleanup();
	}

	public static boolean discover() {
		_discover();
		return _igd_found();
	}

	public static boolean igdFound() {
		return _igd_found();
	}

	public static boolean addMapping(int int1, int int2, String string, String string2, int int3) {
		return addMapping(int1, int2, string, string2, int3, false);
	}

	public static boolean addMapping(int int1, int int2, String string, String string2, int int3, boolean boolean1) {
		boolean boolean2 = _add_mapping(int1, int2, string, string2, int3, boolean1);
		if (!boolean2 && int3 != 0) {
			DebugLog.log(DebugType.Network, "Failed to add port mapping, retrying with zero lease time");
			boolean2 = _add_mapping(int1, int2, string, string2, 0, boolean1);
		}

		return boolean2;
	}

	public static boolean removeMapping(int int1, String string) {
		return _remove_mapping(int1, string);
	}

	public static void fetchMappings() {
		_fetch_mappings();
	}

	public static int numMappings() {
		return _num_mappings();
	}

	public static PortMappingEntry getMapping(int int1) {
		return _get_mapping(int1);
	}

	public static String getGatewayInfo() {
		return _get_gateway_info();
	}

	public static synchronized String getExternalAddress(boolean boolean1) {
		if (boolean1 || externalAddress == null) {
			externalAddress = _get_external_address();
		}

		return externalAddress;
	}

	public static String getExternalAddress() {
		return getExternalAddress(false);
	}

	private static native void _discover();

	private static native void _cleanup();

	private static native boolean _igd_found();

	private static native boolean _add_mapping(int int1, int int2, String string, String string2, int int3, boolean boolean1);

	private static native boolean _remove_mapping(int int1, String string);

	private static native void _fetch_mappings();

	private static native int _num_mappings();

	private static native PortMappingEntry _get_mapping(int int1);

	private static native String _get_gateway_info();

	private static native String _get_external_address();
}
