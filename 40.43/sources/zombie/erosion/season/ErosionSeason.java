package zombie.erosion.season;

import java.util.GregorianCalendar;
import zombie.erosion.utils.Noise2D;


public class ErosionSeason {
	public static final int SEASON_DEFAULT = 0;
	public static final int SEASON_SPRING = 1;
	public static final int SEASON_SUMMER = 2;
	public static final int SEASON_SUMMER2 = 3;
	public static final int SEASON_AUTUMN = 4;
	public static final int SEASON_WINTER = 5;
	public static final int NUM_SEASONS = 6;
	private int lat = 38;
	private int tempMax = 25;
	private int tempMin = 0;
	private int tempDiff = 7;
	private float highNoon = 12.5F;
	private float highNoonCurrent = 12.5F;
	private int seasonLag = 31;
	private float[] rain = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
	private double suSol;
	private double wiSol;
	private GregorianCalendar zeroDay = new GregorianCalendar(1970, 0, 1, 0, 0);
	private int day;
	private int month;
	private int year;
	private boolean isH1;
	private ErosionSeason.YearData[] yearData = new ErosionSeason.YearData[3];
	private int curSeason;
	private float curSeasonDay;
	private float curSeasonDays;
	private float curSeasonStrength;
	private float curSeasonProgression;
	private float dayMeanTemperature;
	private float dayTemperature;
	private float dayNoiseVal;
	private boolean isRainDay;
	private float rainYearAverage;
	private float rainDayStrength;
	private boolean isThunderDay;
	private boolean isSunnyDay;
	private float dayDusk;
	private float dayDawn;
	private float dayDaylight;
	private float winterMod;
	private float summerMod;
	private float summerTilt;
	private float curDayPercent = 0.0F;
	private Noise2D per = new Noise2D();
	private int seedA = 64;
	private int seedB = 128;
	private int seedC = 255;
	String[] names = new String[]{"Default", "Spring", "Early Summer", "Late Summer", "Autumn", "Winter"};

	public void init(int int1, int int2, int int3, int int4, int int5, float float1, int int6, int int7, int int8) {
		this.lat = int1;
		this.tempMax = int2;
		this.tempMin = int3;
		this.tempDiff = int4;
		this.seasonLag = int5;
		this.highNoon = float1;
		this.highNoonCurrent = float1;
		this.seedA = int6;
		this.seedB = int7;
		this.seedC = int8;
		this.summerTilt = 2.0F;
		this.winterMod = this.tempMin < 0 ? 0.05F * (float)(-this.tempMin) : 0.02F * (float)(-this.tempMin);
		this.summerMod = this.tempMax < 0 ? 0.05F * (float)this.tempMax : 0.02F * (float)this.tempMax;
		this.suSol = 2.0 * this.degree(Math.acos(-Math.tan(this.radian((double)this.lat)) * Math.tan(this.radian(23.44)))) / 15.0;
		this.wiSol = 2.0 * this.degree(Math.acos(Math.tan(this.radian((double)this.lat)) * Math.tan(this.radian(23.44)))) / 15.0;
		this.per.reset();
		this.per.addLayer(int6, 8.0F, 2.0F);
		this.per.addLayer(int7, 6.0F, 4.0F);
		this.per.addLayer(int8, 4.0F, 6.0F);
		this.yearData[0] = new ErosionSeason.YearData();
		this.yearData[1] = new ErosionSeason.YearData();
		this.yearData[2] = new ErosionSeason.YearData();
	}

	public int getLat() {
		return this.lat;
	}

	public int getTempMax() {
		return this.tempMax;
	}

	public int getTempMin() {
		return this.tempMin;
	}

	public int getTempDiff() {
		return this.tempDiff;
	}

	public int getSeasonLag() {
		return this.seasonLag;
	}

	public float getHighNoon() {
		return this.highNoon;
	}

	public int getSeedA() {
		return this.seedA;
	}

	public int getSeedB() {
		return this.seedB;
	}

	public int getSeedC() {
		return this.seedC;
	}

	public void setRain(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		this.rain[0] = float1;
		this.rain[1] = float2;
		this.rain[2] = float3;
		this.rain[3] = float4;
		this.rain[4] = float5;
		this.rain[5] = float6;
		this.rain[6] = float7;
		this.rain[7] = float8;
		this.rain[8] = float9;
		this.rain[9] = float10;
		this.rain[10] = float11;
		this.rain[11] = float12;
		float float13 = 0.0F;
		float[] floatArray = this.rain;
		int int1 = floatArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			float float14 = floatArray[int2];
			float13 += float14;
		}

		this.rainYearAverage = (float)((int)Math.floor((double)(365.0F * (float13 / (float)this.rain.length))));
	}

	public ErosionSeason clone() {
		ErosionSeason erosionSeason = new ErosionSeason();
		erosionSeason.init(this.lat, this.tempMax, this.tempMin, this.tempDiff, this.seasonLag, this.highNoon, this.seedA, this.seedB, this.seedC);
		erosionSeason.setRain(this.rain[0], this.rain[1], this.rain[2], this.rain[3], this.rain[4], this.rain[5], this.rain[6], this.rain[7], this.rain[8], this.rain[9], this.rain[10], this.rain[11]);
		return erosionSeason;
	}

	public float getCurDayPercent() {
		return this.curDayPercent;
	}

	public double getMaxDaylightWinter() {
		return this.wiSol;
	}

	public double getMaxDaylightSummer() {
		return this.suSol;
	}

	public float getDusk() {
		return this.dayDusk;
	}

	public float getDawn() {
		return this.dayDawn;
	}

	public float getDaylight() {
		return this.dayDaylight;
	}

	public float getDayTemperature() {
		return this.dayTemperature;
	}

	public float getDayMeanTemperature() {
		return this.dayMeanTemperature;
	}

	public int getSeason() {
		return this.curSeason;
	}

	public float getDayHighNoon() {
		return this.highNoonCurrent;
	}

	public String getSeasonName() {
		return this.names[this.curSeason];
	}

	public boolean isSeason(int int1) {
		return int1 == this.curSeason;
	}

	public GregorianCalendar getWinterStartDay(int int1, int int2, int int3) {
		GregorianCalendar gregorianCalendar = new GregorianCalendar(int3, int2, int1);
		long long1 = gregorianCalendar.getTime().getTime();
		return long1 < this.yearData[0].winterEndDayUnx ? this.yearData[0].winterStartDay : this.yearData[1].winterStartDay;
	}

	public float getSeasonDay() {
		return this.curSeasonDay;
	}

	public float getSeasonDays() {
		return this.curSeasonDays;
	}

	public float getSeasonStrength() {
		return this.curSeasonStrength;
	}

	public float getSeasonProgression() {
		return this.curSeasonProgression;
	}

	public float getDayNoiseVal() {
		return this.dayNoiseVal;
	}

	public boolean isRainDay() {
		return this.isRainDay;
	}

	public float getRainDayStrength() {
		return this.rainDayStrength;
	}

	public float getRainYearAverage() {
		return this.rainYearAverage;
	}

	public boolean isThunderDay() {
		return this.isThunderDay;
	}

	public boolean isSunnyDay() {
		return this.isSunnyDay;
	}

	public void setDay(int int1, int int2, int int3) {
		GregorianCalendar gregorianCalendar = new GregorianCalendar(int3, int2, int1, 0, 0);
		long long1 = gregorianCalendar.getTime().getTime();
		this.setYearData(int3);
		this.setSeasonData((float)long1, gregorianCalendar, int3, int2);
		this.setDaylightData(long1, gregorianCalendar);
	}

	private void setYearData(int int1) {
		if (this.yearData[1].year != int1) {
			for (int int2 = 0; int2 < 3; ++int2) {
				int int3 = int2 - 1;
				int int4 = int1 + int3;
				this.yearData[int2].year = int4;
				this.yearData[int2].winSols = new GregorianCalendar(int4, 11, 22);
				this.yearData[int2].sumSols = new GregorianCalendar(int4, 5, 22);
				this.yearData[int2].winSolsUnx = this.yearData[int2].winSols.getTime().getTime();
				this.yearData[int2].sumSolsUnx = this.yearData[int2].sumSols.getTime().getTime();
				this.yearData[int2].hottestDay = new GregorianCalendar(int4, 5, 22);
				this.yearData[int2].coldestDay = new GregorianCalendar(int4, 11, 22);
				this.yearData[int2].hottestDay.add(5, this.seasonLag);
				this.yearData[int2].coldestDay.add(5, this.seasonLag);
				this.yearData[int2].hottestDayUnx = this.yearData[int2].hottestDay.getTime().getTime();
				this.yearData[int2].coldestDayUnx = this.yearData[int2].coldestDay.getTime().getTime();
				this.yearData[int2].winterS = this.per.layeredNoise((float)(64 + int4), 64.0F);
				this.yearData[int2].winterE = this.per.layeredNoise(64.0F, (float)(64 + int4));
				this.yearData[int2].winterStartDay = new GregorianCalendar(int4, 11, 22);
				this.yearData[int2].winterEndDay = new GregorianCalendar(int4, 11, 22);
				this.yearData[int2].winterStartDay.add(5, (int)(-Math.floor((double)(40.0F + 40.0F * this.winterMod + 20.0F * this.yearData[int2].winterS))));
				this.yearData[int2].winterEndDay.add(5, (int)Math.floor((double)(40.0F + 40.0F * this.winterMod + 20.0F * this.yearData[int2].winterE)));
				this.yearData[int2].winterStartDayUnx = this.yearData[int2].winterStartDay.getTime().getTime();
				this.yearData[int2].winterEndDayUnx = this.yearData[int2].winterEndDay.getTime().getTime();
				this.yearData[int2].summerS = this.per.layeredNoise((float)(128 + int4), 128.0F);
				this.yearData[int2].summerE = this.per.layeredNoise(128.0F, (float)(128 + int4));
				this.yearData[int2].summerStartDay = new GregorianCalendar(int4, 5, 22);
				this.yearData[int2].summerEndDay = new GregorianCalendar(int4, 5, 22);
				this.yearData[int2].summerStartDay.add(5, (int)(-Math.floor((double)(40.0F + 40.0F * this.summerMod + 20.0F * this.yearData[int2].summerS))));
				this.yearData[int2].summerEndDay.add(5, (int)Math.floor((double)(40.0F + 40.0F * this.summerMod + 20.0F * this.yearData[int2].summerE)));
				this.yearData[int2].summerStartDayUnx = this.yearData[int2].summerStartDay.getTime().getTime();
				this.yearData[int2].summerEndDayUnx = this.yearData[int2].summerEndDay.getTime().getTime();
			}

			this.yearData[1].lastSummerStr = this.yearData[0].summerS + this.yearData[0].summerE - 1.0F;
			this.yearData[1].lastWinterStr = this.yearData[0].winterS + this.yearData[0].winterE - 1.0F;
			this.yearData[1].summerStr = this.yearData[1].summerS + this.yearData[1].summerE - 1.0F;
			this.yearData[1].winterStr = this.yearData[1].winterS + this.yearData[1].winterE - 1.0F;
			this.yearData[1].nextSummerStr = this.yearData[2].summerS + this.yearData[2].summerE - 1.0F;
			this.yearData[1].nextWinterStr = this.yearData[2].winterS + this.yearData[2].winterE - 1.0F;
		}
	}

	private void setSeasonData(float float1, GregorianCalendar gregorianCalendar, int int1, int int2) {
		GregorianCalendar gregorianCalendar2;
		GregorianCalendar gregorianCalendar3;
		if (float1 < (float)this.yearData[0].winterEndDayUnx) {
			this.curSeason = 5;
			gregorianCalendar2 = this.yearData[0].winterStartDay;
			gregorianCalendar3 = this.yearData[0].winterEndDay;
		} else if (float1 < (float)this.yearData[1].summerStartDayUnx) {
			this.curSeason = 1;
			gregorianCalendar2 = this.yearData[0].winterEndDay;
			gregorianCalendar3 = this.yearData[1].summerStartDay;
		} else if (float1 < (float)this.yearData[1].summerEndDayUnx) {
			this.curSeason = 2;
			gregorianCalendar2 = this.yearData[1].summerStartDay;
			gregorianCalendar3 = this.yearData[1].summerEndDay;
		} else if (float1 < (float)this.yearData[1].winterStartDayUnx) {
			this.curSeason = 4;
			gregorianCalendar2 = this.yearData[1].summerEndDay;
			gregorianCalendar3 = this.yearData[1].winterStartDay;
		} else {
			this.curSeason = 5;
			gregorianCalendar2 = this.yearData[1].winterStartDay;
			gregorianCalendar3 = this.yearData[1].winterEndDay;
		}

		this.curSeasonDay = this.dayDiff(gregorianCalendar, gregorianCalendar2);
		this.curSeasonDays = this.dayDiff(gregorianCalendar2, gregorianCalendar3);
		this.curSeasonStrength = this.curSeasonDays / 90.0F - 1.0F;
		this.curSeasonProgression = this.curSeasonDay / this.curSeasonDays;
		float float2;
		float float3;
		float float4;
		if (float1 < (float)this.yearData[0].coldestDayUnx && float1 >= (float)this.yearData[0].hottestDayUnx) {
			float2 = (float)this.tempMax + (float)(this.tempDiff / 2) * this.yearData[1].lastSummerStr;
			float3 = (float)this.tempMin + (float)(this.tempDiff / 2) * this.yearData[1].lastWinterStr;
			float4 = this.dayDiff(gregorianCalendar, this.yearData[0].hottestDay) / this.dayDiff(this.yearData[0].hottestDay, this.yearData[0].coldestDay);
		} else if (float1 < (float)this.yearData[1].hottestDayUnx && float1 >= (float)this.yearData[0].coldestDayUnx) {
			float2 = (float)this.tempMin + (float)(this.tempDiff / 2) * this.yearData[1].lastWinterStr;
			float3 = (float)this.tempMax + (float)(this.tempDiff / 2) * this.yearData[1].summerStr;
			float4 = this.dayDiff(gregorianCalendar, this.yearData[0].coldestDay) / this.dayDiff(this.yearData[1].hottestDay, this.yearData[0].coldestDay);
		} else if (float1 < (float)this.yearData[1].coldestDayUnx && float1 >= (float)this.yearData[1].hottestDayUnx) {
			float2 = (float)this.tempMax + (float)(this.tempDiff / 2) * this.yearData[1].summerStr;
			float3 = (float)this.tempMin + (float)(this.tempDiff / 2) * this.yearData[1].winterStr;
			float4 = this.dayDiff(gregorianCalendar, this.yearData[1].hottestDay) / this.dayDiff(this.yearData[1].hottestDay, this.yearData[1].coldestDay);
		} else {
			float2 = (float)this.tempMin + (float)(this.tempDiff / 2) * this.yearData[1].winterStr;
			float3 = (float)this.tempMax + (float)(this.tempDiff / 2) * this.yearData[1].nextSummerStr;
			float4 = this.dayDiff(gregorianCalendar, this.yearData[1].coldestDay) / this.dayDiff(this.yearData[1].coldestDay, this.yearData[2].hottestDay);
		}

		float float5 = (float)this.clerp((double)float4, (double)float2, (double)float3);
		float float6 = this.dayDiff(this.zeroDay, gregorianCalendar) / 20.0F;
		this.dayNoiseVal = this.per.layeredNoise(float6, 0.0F);
		float float7 = this.dayNoiseVal * 2.0F - 1.0F;
		this.dayTemperature = float5 + (float)this.tempDiff * float7;
		this.dayMeanTemperature = float5;
		this.isThunderDay = false;
		this.isRainDay = false;
		this.isSunnyDay = false;
		float float8 = 0.1F + this.rain[int2] <= 1.0F ? 0.1F + this.rain[int2] : 1.0F;
		if (float8 > 0.0F && this.dayNoiseVal < float8) {
			this.isRainDay = true;
			this.rainDayStrength = 1.0F - this.dayNoiseVal / float8;
			float float9 = this.per.layeredNoise(0.0F, float6);
			if ((double)float9 > 0.6) {
				this.isThunderDay = true;
			}
		}

		if ((double)this.dayNoiseVal > 0.6) {
			this.isSunnyDay = true;
		}
	}

	private void setDaylightData(long long1, GregorianCalendar gregorianCalendar) {
		GregorianCalendar gregorianCalendar2;
		GregorianCalendar gregorianCalendar3;
		if (long1 < this.yearData[1].winSolsUnx && long1 >= this.yearData[1].sumSolsUnx) {
			this.isH1 = false;
			gregorianCalendar2 = this.yearData[1].sumSols;
			gregorianCalendar3 = this.yearData[1].winSols;
		} else {
			this.isH1 = true;
			if (long1 >= this.yearData[1].winSolsUnx) {
				gregorianCalendar2 = this.yearData[1].winSols;
				gregorianCalendar3 = this.yearData[2].sumSols;
			} else {
				gregorianCalendar2 = this.yearData[0].winSols;
				gregorianCalendar3 = this.yearData[1].sumSols;
			}
		}

		float float1 = this.dayDiff(gregorianCalendar, gregorianCalendar2) / this.dayDiff(gregorianCalendar2, gregorianCalendar3);
		float float2 = float1;
		if (this.isH1) {
			this.dayDaylight = (float)this.clerp((double)float1, this.wiSol, this.suSol);
		} else {
			this.dayDaylight = (float)this.clerp((double)float1, this.suSol, this.wiSol);
			float2 = 1.0F - float1;
		}

		this.curDayPercent = float2;
		this.highNoonCurrent = this.highNoon + this.summerTilt * float2;
		this.dayDawn = this.highNoonCurrent - this.dayDaylight / 2.0F;
		this.dayDusk = this.highNoonCurrent + this.dayDaylight / 2.0F;
	}

	private float dayDiff(GregorianCalendar gregorianCalendar, GregorianCalendar gregorianCalendar2) {
		long long1 = gregorianCalendar.getTime().getTime() - gregorianCalendar2.getTime().getTime();
		return (float)Math.abs(long1 / 86400000L);
	}

	private double clerp(double double1, double double2, double double3) {
		double double4 = (1.0 - Math.cos(double1 * 3.141592653589793)) / 2.0;
		return double2 * (1.0 - double4) + double3 * double4;
	}

	private double lerp(double double1, double double2, double double3) {
		return double2 + double1 * (double3 - double2);
	}

	private double radian(double double1) {
		return double1 * 0.017453292519943295;
	}

	private double degree(double double1) {
		return double1 * 57.29577951308232;
	}

	public static void Reset() {
	}

	public void setCurSeason(int int1) {
		this.curSeason = int1;
	}

	private static class YearData {
		public int year;
		public GregorianCalendar winSols;
		public GregorianCalendar sumSols;
		public long winSolsUnx;
		public long sumSolsUnx;
		public GregorianCalendar hottestDay;
		public GregorianCalendar coldestDay;
		public long hottestDayUnx;
		public long coldestDayUnx;
		public float winterS;
		public float winterE;
		public GregorianCalendar winterStartDay;
		public GregorianCalendar winterEndDay;
		public long winterStartDayUnx;
		public long winterEndDayUnx;
		public float summerS;
		public float summerE;
		public GregorianCalendar summerStartDay;
		public GregorianCalendar summerEndDay;
		public long summerStartDayUnx;
		public long summerEndDayUnx;
		public float lastSummerStr;
		public float lastWinterStr;
		public float summerStr;
		public float winterStr;
		public float nextSummerStr;
		public float nextWinterStr;

		private YearData() {
		}

		YearData(Object object) {
			this();
		}
	}
}
