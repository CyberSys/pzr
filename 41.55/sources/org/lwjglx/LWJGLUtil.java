package org.lwjglx;

import java.security.AccessController;
import java.security.PrivilegedAction;


public class LWJGLUtil {
	public static final int PLATFORM_LINUX = 1;
	public static final int PLATFORM_MACOSX = 2;
	public static final int PLATFORM_WINDOWS = 3;
	public static final String PLATFORM_LINUX_NAME = "linux";
	public static final String PLATFORM_MACOSX_NAME = "macosx";
	public static final String PLATFORM_WINDOWS_NAME = "windows";
	public static final boolean DEBUG = getPrivilegedBoolean("org.lwjgl.util.Debug");
	public static final boolean CHECKS = !getPrivilegedBoolean("org.lwjgl.util.NoChecks");
	private static final int PLATFORM;

	public static int getPlatform() {
		return PLATFORM;
	}

	public static String getPlatformName() {
		switch (getPlatform()) {
		case 1: 
			return "linux";
		
		case 2: 
			return "macosx";
		
		case 3: 
			return "windows";
		
		default: 
			return "unknown";
		
		}
	}

	private static String getPrivilegedProperty(String string) {
		return (String)AccessController.doPrivileged(new PrivilegedAction(){
			
			public String run() {
				return System.getProperty(string);
			}
		});
	}

	public static boolean getPrivilegedBoolean(String string) {
		return (Boolean)AccessController.doPrivileged(new PrivilegedAction(){
			
			public Boolean run() {
				return Boolean.getBoolean(string);
			}
		});
	}

	public static Integer getPrivilegedInteger(String string) {
		return (Integer)AccessController.doPrivileged(new PrivilegedAction(){
			
			public Integer run() {
				return Integer.getInteger(string);
			}
		});
	}

	public static Integer getPrivilegedInteger(String string, int int1) {
		return (Integer)AccessController.doPrivileged(new PrivilegedAction(){
			
			public Integer run() {
				return Integer.getInteger(string, int1);
			}
		});
	}

	static  {
	String var0 = getPrivilegedProperty("os.name");
	if (var0.startsWith("Windows")) {
		PLATFORM = 3;
	} else if (!var0.startsWith("Linux") && !var0.startsWith("FreeBSD") && !var0.startsWith("SunOS") && !var0.startsWith("Unix")) {
		if (!var0.startsWith("Mac OS X") && !var0.startsWith("Darwin")) {
			throw new LinkageError("Unknown platform: " + var0);
		}

		PLATFORM = 2;
	} else {
		PLATFORM = 1;
	}
	}
}
