package org.lwjglx.util.glu;


public class Registry extends Util {
	private static final String versionString = "1.3";
	private static final String extensionString = "GLU_EXT_nurbs_tessellator GLU_EXT_object_space_tess ";

	public static String gluGetString(int int1) {
		if (int1 == 100800) {
			return "1.3";
		} else {
			return int1 == 100801 ? "GLU_EXT_nurbs_tessellator GLU_EXT_object_space_tess " : null;
		}
	}

	public static boolean gluCheckExtension(String string, String string2) {
		if (string2 != null && string != null) {
			return string2.indexOf(string) != -1;
		} else {
			return false;
		}
	}
}
