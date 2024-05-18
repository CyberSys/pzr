package zombie.core.znet;


public class ZNet {
	public static native void init();

	public static native void setLogLevel(int int1);

	private static void logPutsCallback(String string) {
		long long1 = System.currentTimeMillis();
		System.out.print(long1 + " " + string);
	}
}
