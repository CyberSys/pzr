package se.krka.kahlua.stdlib;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;


public class OsLib implements JavaFunction {
	private static final int DATE = 0;
	private static final int DIFFTIME = 1;
	private static final int TIME = 2;
	private static final int NUM_FUNCS = 3;
	private static final String[] funcnames = new String[3];
	private static final OsLib[] funcs;
	private static final String TABLE_FORMAT = "*t";
	private static final String DEFAULT_FORMAT = "%c";
	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";
	private static final String HOUR = "hour";
	private static final String MIN = "min";
	private static final String SEC = "sec";
	private static final String WDAY = "wday";
	private static final String YDAY = "yday";
	private static final Object MILLISECOND;
	private static TimeZone tzone;
	public static final int TIME_DIVIDEND = 1000;
	public static final double TIME_DIVIDEND_INVERTED = 0.001;
	private static final int MILLIS_PER_DAY = 86400000;
	private static final int MILLIS_PER_WEEK = 604800000;
	private int methodId;
	private static String[] shortDayNames;
	private static String[] longDayNames;
	private static String[] shortMonthNames;
	private static String[] longMonthNames;

	public static void register(Platform platform, KahluaTable kahluaTable) {
		KahluaTable kahluaTable2 = platform.newTable();
		for (int int1 = 0; int1 < 3; ++int1) {
			kahluaTable2.rawset(funcnames[int1], funcs[int1]);
		}

		kahluaTable.rawset("os", kahluaTable2);
	}

	private OsLib(int int1) {
		this.methodId = int1;
	}

	public int call(LuaCallFrame luaCallFrame, int int1) {
		switch (this.methodId) {
		case 0: 
			return this.date(luaCallFrame, int1);
		
		case 1: 
			return this.difftime(luaCallFrame, int1);
		
		case 2: 
			return this.time(luaCallFrame, int1);
		
		default: 
			throw new RuntimeException("Undefined method called on os.");
		
		}
	}

	private int time(LuaCallFrame luaCallFrame, int int1) {
		if (int1 == 0) {
			double double1 = (double)System.currentTimeMillis() * 0.001;
			luaCallFrame.push(KahluaUtil.toDouble(double1));
		} else {
			KahluaTable kahluaTable = (KahluaTable)KahluaUtil.getArg(luaCallFrame, 1, "time");
			double double2 = (double)getDateFromTable(kahluaTable).getTime() * 0.001;
			luaCallFrame.push(KahluaUtil.toDouble(double2));
		}

		return 1;
	}

	private int difftime(LuaCallFrame luaCallFrame, int int1) {
		double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 1, "difftime");
		double double2 = KahluaUtil.getDoubleArg(luaCallFrame, 2, "difftime");
		luaCallFrame.push(KahluaUtil.toDouble(double1 - double2));
		return 1;
	}

	private int date(LuaCallFrame luaCallFrame, int int1) {
		Platform platform = luaCallFrame.getPlatform();
		if (int1 == 0) {
			return luaCallFrame.push(getdate("%c", platform));
		} else {
			String string = KahluaUtil.getStringArg(luaCallFrame, 1, "date");
			if (int1 == 1) {
				return luaCallFrame.push(getdate(string, platform));
			} else {
				double double1 = KahluaUtil.getDoubleArg(luaCallFrame, 2, "date");
				long long1 = (long)(double1 * 1000.0);
				return luaCallFrame.push(getdate(string, long1, platform));
			}
		}
	}

	public static Object getdate(String string, Platform platform) {
		return getdate(string, Calendar.getInstance().getTime().getTime(), platform);
	}

	public static Object getdate(String string, long long1, Platform platform) {
		Calendar calendar = null;
		int int1 = 0;
		if (string.charAt(int1) == '!') {
			calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			++int1;
		} else {
			calendar = Calendar.getInstance(tzone);
		}

		calendar.setTime(new Date(long1));
		if (calendar == null) {
			return null;
		} else {
			return string.substring(int1, 2 + int1).equals("*t") ? getTableFromDate(calendar, platform) : formatTime(string.substring(int1), calendar);
		}
	}

	public static String formatTime(String string, Calendar calendar) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int int1 = 0; int1 < string.length(); ++int1) {
			if (string.charAt(int1) == '%' && int1 + 1 != string.length()) {
				++int1;
				stringBuilder.append(strftime(string.charAt(int1), calendar));
			} else {
				stringBuilder.append(string.charAt(int1));
			}
		}

		return stringBuilder.toString();
	}

	private static String format2Digits(int int1) {
		String string = Integer.toString(int1);
		if (int1 < 10) {
			string = "0" + string;
		}

		return string;
	}

	private static String strftime(char char1, Calendar calendar) {
		switch (char1) {
		case 'A': 
			return longDayNames[calendar.get(7) - 1];
		
		case 'B': 
			return longMonthNames[calendar.get(2)];
		
		case 'C': 
			return Integer.toString(calendar.get(1) / 100);
		
		case 'D': 
			return formatTime("%m/%d/%y", calendar);
		
		case 'E': 
		
		case 'F': 
		
		case 'G': 
		
		case 'J': 
		
		case 'K': 
		
		case 'L': 
		
		case 'N': 
		
		case 'O': 
		
		case 'P': 
		
		case 'Q': 
		
		case 'T': 
		
		case 'X': 
		
		case '[': 
		
		case '\\': 
		
		case ']': 
		
		case '^': 
		
		case '_': 
		
		case '`': 
		
		case 'f': 
		
		case 'g': 
		
		case 'i': 
		
		case 'k': 
		
		case 'l': 
		
		case 'o': 
		
		case 'q': 
		
		case 's': 
		
		case 't': 
		
		case 'u': 
		
		case 'v': 
		
		case 'x': 
		
		default: 
			return null;
		
		case 'H': 
			return format2Digits(calendar.get(11));
		
		case 'I': 
			return format2Digits(calendar.get(10));
		
		case 'M': 
			return format2Digits(calendar.get(12));
		
		case 'R': 
			return formatTime("%H:%M", calendar);
		
		case 'S': 
			return format2Digits(calendar.get(13));
		
		case 'U': 
			return Integer.toString(getWeekOfYear(calendar, true, false));
		
		case 'V': 
			return Integer.toString(getWeekOfYear(calendar, false, true));
		
		case 'W': 
			return Integer.toString(getWeekOfYear(calendar, false, false));
		
		case 'Y': 
			return Integer.toString(calendar.get(1));
		
		case 'Z': 
			return calendar.getTimeZone().getID();
		
		case 'a': 
			return shortDayNames[calendar.get(7) - 1];
		
		case 'b': 
			return shortMonthNames[calendar.get(2)];
		
		case 'c': 
			return calendar.getTime().toString();
		
		case 'd': 
			return format2Digits(calendar.get(5));
		
		case 'e': 
			return calendar.get(5) < 10 ? " " + strftime('d', calendar) : strftime('d', calendar);
		
		case 'h': 
			return strftime('b', calendar);
		
		case 'j': 
			return Integer.toString(getDayOfYear(calendar));
		
		case 'm': 
			return format2Digits(calendar.get(2) + 1);
		
		case 'n': 
			return "\n";
		
		case 'p': 
			return calendar.get(9) == 0 ? "AM" : "PM";
		
		case 'r': 
			return formatTime("%I:%M:%S %p", calendar);
		
		case 'w': 
			return Integer.toString(calendar.get(7) - 1);
		
		case 'y': 
			return Integer.toString(calendar.get(1) % 100);
		
		}
	}

	public static KahluaTable getTableFromDate(Calendar calendar, Platform platform) {
		KahluaTable kahluaTable = platform.newTable();
		kahluaTable.rawset("year", KahluaUtil.toDouble((long)calendar.get(1)));
		kahluaTable.rawset("month", KahluaUtil.toDouble((long)(calendar.get(2) + 1)));
		kahluaTable.rawset("day", KahluaUtil.toDouble((long)calendar.get(5)));
		kahluaTable.rawset("hour", KahluaUtil.toDouble((long)calendar.get(11)));
		kahluaTable.rawset("min", KahluaUtil.toDouble((long)calendar.get(12)));
		kahluaTable.rawset("sec", KahluaUtil.toDouble((long)calendar.get(13)));
		kahluaTable.rawset("wday", KahluaUtil.toDouble((long)calendar.get(7)));
		kahluaTable.rawset("yday", KahluaUtil.toDouble((long)getDayOfYear(calendar)));
		kahluaTable.rawset(MILLISECOND, KahluaUtil.toDouble((long)calendar.get(14)));
		return kahluaTable;
	}

	public static Date getDateFromTable(KahluaTable kahluaTable) {
		Calendar calendar = Calendar.getInstance(tzone);
		calendar.set(1, (int)KahluaUtil.fromDouble(kahluaTable.rawget("year")));
		calendar.set(2, (int)KahluaUtil.fromDouble(kahluaTable.rawget("month")) - 1);
		calendar.set(5, (int)KahluaUtil.fromDouble(kahluaTable.rawget("day")));
		Object object = kahluaTable.rawget("hour");
		Object object2 = kahluaTable.rawget("min");
		Object object3 = kahluaTable.rawget("sec");
		Object object4 = kahluaTable.rawget(MILLISECOND);
		if (object != null) {
			calendar.set(11, (int)KahluaUtil.fromDouble(object));
		} else {
			calendar.set(11, 0);
		}

		if (object2 != null) {
			calendar.set(12, (int)KahluaUtil.fromDouble(object2));
		} else {
			calendar.set(12, 0);
		}

		if (object3 != null) {
			calendar.set(13, (int)KahluaUtil.fromDouble(object3));
		} else {
			calendar.set(13, 0);
		}

		if (object4 != null) {
			calendar.set(14, (int)KahluaUtil.fromDouble(object4));
		} else {
			calendar.set(14, 0);
		}

		return calendar.getTime();
	}

	public static int getDayOfYear(Calendar calendar) {
		Calendar calendar2 = Calendar.getInstance(calendar.getTimeZone());
		calendar2.setTime(calendar.getTime());
		calendar2.set(2, 0);
		calendar2.set(5, 1);
		long long1 = calendar.getTime().getTime() - calendar2.getTime().getTime();
		return (int)Math.ceil((double)long1 / 8.64E7);
	}

	public static int getWeekOfYear(Calendar calendar, boolean boolean1, boolean boolean2) {
		Calendar calendar2 = Calendar.getInstance(calendar.getTimeZone());
		calendar2.setTime(calendar.getTime());
		calendar2.set(2, 0);
		calendar2.set(5, 1);
		int int1 = calendar2.get(7);
		if (boolean1 && int1 != 1) {
			calendar2.set(5, 7 - int1 + 1);
		} else if (int1 != 2) {
			calendar2.set(5, 7 - int1 + 1 + 1);
		}

		long long1 = calendar.getTime().getTime() - calendar2.getTime().getTime();
		int int2 = (int)(long1 / 604800000L);
		if (boolean2 && 7 - int1 >= 4) {
			++int2;
		}

		return int2;
	}

	static  {
		funcnames[0] = "date";
		funcnames[1] = "difftime";
		funcnames[2] = "time";
		funcs = new OsLib[3];
	for (int var0 = 0; var0 < 3; ++var0) {
		funcs[var0] = new OsLib(var0);
	}

		MILLISECOND = "milli";
		tzone = TimeZone.getTimeZone("UTC");
		shortDayNames = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
		longDayNames = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
		shortMonthNames = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		longMonthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	}
}
