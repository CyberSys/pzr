package com.jcraft.jorbis;

import java.io.PrintStream;
import zombie.debug.DebugLog;


class ChainingExample {

	public static void main(String[] stringArray) {
		VorbisFile vorbisFile = null;
		try {
			if (stringArray.length > 0) {
				vorbisFile = new VorbisFile(stringArray[0]);
			} else {
				vorbisFile = new VorbisFile(System.in, (byte[])null, -1);
			}
		} catch (Exception exception) {
			System.err.println(exception);
			return;
		}

		if (vorbisFile.seekable()) {
			DebugLog.log("Input bitstream contained " + vorbisFile.streams() + " logical bitstream section(s).");
			DebugLog.log("Total bitstream playing time: " + vorbisFile.time_total(-1) + " seconds\n");
		} else {
			DebugLog.log("Standard input was not seekable.");
			DebugLog.log("First logical bitstream information:\n");
		}

		for (int int1 = 0; int1 < vorbisFile.streams(); ++int1) {
			Info info = vorbisFile.getInfo(int1);
			DebugLog.log("\tlogical bitstream section " + (int1 + 1) + " information:");
			int int2 = info.rate;
			DebugLog.log("\t\t" + int2 + "Hz " + info.channels + " channels bitrate " + vorbisFile.bitrate(int1) / 1000 + "kbps serial number=" + vorbisFile.serialnumber(int1));
			PrintStream printStream = System.out;
			long long1 = vorbisFile.raw_total(int1);
			printStream.print("\t\tcompressed length: " + long1 + " bytes ");
			float float1 = vorbisFile.time_total(int1);
			DebugLog.log(" play time: " + float1 + "s");
			Comment comment = vorbisFile.getComment(int1);
			DebugLog.log((Object)comment);
		}
	}
}
