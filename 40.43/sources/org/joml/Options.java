package org.joml;


class Options {
	static final boolean DEBUG = hasOption("joml.debug");
	static final boolean NO_UNSAFE = hasOption("joml.nounsafe");
	static final boolean FASTMATH = hasOption("joml.fastmath");
	static final boolean SIN_LOOKUP = hasOption("joml.sinLookup");
	static final int SIN_LOOKUP_BITS = Integer.parseInt(System.getProperty("joml.sinLookup.bits", "14"));

	static boolean hasOption(String string) {
		String string2 = System.getProperty(string);
		if (string2 == null) {
			return false;
		} else {
			return string2.trim().length() == 0 ? true : Boolean.valueOf(string2);
		}
	}
}
