package zombie.core;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import zombie.core.math.PZMath;


public final class GameVersion {
	private final int m_major;
	private final int m_minor;
	private final String m_suffix;
	private final String m_string;

	public GameVersion(int int1, int int2, String string) {
		if (int1 < 0) {
			throw new IllegalArgumentException("major version must be greater than zero");
		} else if (int2 >= 0 && int2 <= 999) {
			this.m_major = int1;
			this.m_minor = int2;
			this.m_suffix = string;
			this.m_string = String.format(Locale.ENGLISH, "%d.%d%s", this.m_major, this.m_minor, this.m_suffix == null ? "" : this.m_suffix);
		} else {
			throw new IllegalArgumentException("minor version must be from 0 to 999");
		}
	}

	public int getMajor() {
		return this.m_major;
	}

	public int getMinor() {
		return this.m_minor;
	}

	public String getSuffix() {
		return this.m_suffix;
	}

	public int getInt() {
		return this.m_major * 1000 + this.m_minor;
	}

	public boolean isGreaterThan(GameVersion gameVersion) {
		return this.getInt() > gameVersion.getInt();
	}

	public boolean isGreaterThanOrEqualTo(GameVersion gameVersion) {
		return this.getInt() >= gameVersion.getInt();
	}

	public boolean isLessThan(GameVersion gameVersion) {
		return this.getInt() < gameVersion.getInt();
	}

	public boolean isLessThanOrEqualTo(GameVersion gameVersion) {
		return this.getInt() <= gameVersion.getInt();
	}

	public boolean equals(Object object) {
		if (object == this) {
			return true;
		} else if (!(object instanceof GameVersion)) {
			return false;
		} else {
			GameVersion gameVersion = (GameVersion)object;
			return gameVersion.m_major == this.m_major && gameVersion.m_minor == this.m_minor;
		}
	}

	public String toString() {
		return this.m_string;
	}

	public static GameVersion parse(String string) {
		Matcher matcher = Pattern.compile("([0-9]+)\\.([0-9]+)(.*)").matcher(string);
		if (matcher.matches()) {
			int int1 = PZMath.tryParseInt(matcher.group(1), 0);
			int int2 = PZMath.tryParseInt(matcher.group(2), 0);
			String string2 = matcher.group(3);
			return new GameVersion(int1, int2, string2);
		} else {
			throw new IllegalArgumentException("invalid game version \"" + string + "\"");
		}
	}
}
