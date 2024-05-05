package zombie.iso.areas;

import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Mapping0;
import java.io.FileInputStream;


public class IsoArea {
	public static String version = "0a2a0q";
	public static boolean Doobo;

	public static byte[] asasa(String string) throws Exception {
		new FileInputStream(string);
		byte[] byteArray = new byte[1024];
		return byteArray;
	}

	public static String Ardo(String string) throws Exception {
		byte[] byteArray = asasa(string);
		String string2 = "";
		for (int int1 = 0; int1 < byteArray.length; ++int1) {
			string2 = Block.asdsadsa(string2, byteArray, int1);
		}

		return string2;
	}

	public static boolean Thigglewhat2(String string, String string2) {
		String string3 = "";
		try {
			string3 = Ardo(string);
			if (!string3.equals(string2)) {
				return false;
			}
		} catch (Exception exception) {
			string3 = "";
			try {
				string3 = Ardo(IsoRoomExit.ThiggleQ + string);
			} catch (Exception exception2) {
				return false;
			}
		}

		return string3.equals(string2);
	}

	public static String Thigglewhat22(String string) {
		String string2 = "";
		try {
			string2 = Ardo(string);
		} catch (Exception exception) {
			string2 = "";
			try {
				string2 = Ardo(IsoRoomExit.ThiggleQ + string);
			} catch (Exception exception2) {
				return "";
			}
		}

		return string2;
	}

	public static boolean Thigglewhat() {
		String string = "";
		string = string + Thigglewhat22(Mapping0.ThiggleAQQ2 + Mapping0.ThiggleA + Mapping0.ThiggleAQ + Mapping0.ThiggleAQ2);
		string = string + Thigglewhat22(Mapping0.ThiggleAQQ2 + Mapping0.ThiggleB + Mapping0.ThiggleBB + Mapping0.ThiggleAQ + Mapping0.ThiggleAQ2);
		string = string + Thigglewhat22(Mapping0.ThiggleAQQ2 + Mapping0.ThiggleC + Mapping0.ThiggleCC + Mapping0.ThiggleAQ + Mapping0.ThiggleAQ2);
		string = string + Thigglewhat22(Mapping0.ThiggleAQQ2 + Mapping0.ThiggleD + Mapping0.ThiggleDA + Mapping0.ThiggleDB + Mapping0.ThiggleAQ + Mapping0.ThiggleAQ2);
		string = string + Thigglewhat22(Mapping0.ThiggleAQQ2 + Mapping0.ThiggleE + Mapping0.ThiggleEA + Mapping0.ThiggleAQ + Mapping0.ThiggleAQ2);
		string = string + Thigglewhat22(Mapping0.ThiggleAQQ2 + Mapping0.ThiggleF + Mapping0.ThiggleFA + Mapping0.ThiggleAQ + Mapping0.ThiggleAQ2);
		string = string + Thigglewhat22(Mapping0.ThiggleAQQ2 + Mapping0.ThiggleG + Mapping0.ThiggleGA + Mapping0.ThiggleGB + Mapping0.ThiggleGC + Mapping0.ThiggleAQ + Mapping0.ThiggleAQ2);
		string = string.toUpperCase();
		return true;
	}
}
