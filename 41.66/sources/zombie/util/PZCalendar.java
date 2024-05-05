package zombie.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;


public final class PZCalendar {
	private final Calendar calendar;

	public static PZCalendar getInstance() {
		return new PZCalendar(Calendar.getInstance());
	}

	public PZCalendar(Calendar calendar) {
		Objects.requireNonNull(calendar);
		this.calendar = calendar;
	}

	public void set(int int1, int int2, int int3, int int4, int int5) {
		this.calendar.set(int1, int2, int3, int4, int5);
	}

	public void setTimeInMillis(long long1) {
		this.calendar.setTimeInMillis(long1);
	}

	public int get(int int1) {
		return this.calendar.get(int1);
	}

	public final Date getTime() {
		return this.calendar.getTime();
	}

	public long getTimeInMillis() {
		return this.calendar.getTimeInMillis();
	}

	public boolean isLeapYear(int int1) {
		return ((GregorianCalendar)this.calendar).isLeapYear(int1);
	}
}
