package zombie.profanity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import zombie.profanity.locales.Locale;
import zombie.profanity.locales.LocaleChinese;
import zombie.profanity.locales.LocaleEnglish;
import zombie.profanity.locales.LocaleGerman;


public class ProfanityFilter {
	public static boolean DEBUG = false;
	private Map locales = new HashMap();
	private Locale locale;
	private Locale localeDefault;
	private Pattern prePattern;
	private boolean enabled = true;
	public static String LOCALES_DIR;
	private static ProfanityFilter instance;

	public static ProfanityFilter getInstance() {
		if (instance == null) {
			instance = new ProfanityFilter();
		}

		return instance;
	}

	private ProfanityFilter() {
		this.addLocale(new LocaleEnglish("EN"), true);
		this.addLocale(new LocaleGerman("GER"));
		this.addLocale(new LocaleChinese("CHIN"));
		this.prePattern = Pattern.compile("(?<spaced>(?:(?:\\s|\\W)[\\w\\$@](?=\\s|\\W)){2,20})|(?<word>[\\w\'\\$@_-]+)");
	}

	public static void printDebug(String string) {
		if (DEBUG) {
			System.out.println(string);
		}
	}

	public void enable(boolean boolean1) {
		this.enabled = boolean1;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public int getFilterWordsCount() {
		return this.locale != null ? this.locale.getFilterWordsCount() : 0;
	}

	public void addLocale(Locale locale) {
		this.addLocale(locale, false);
	}

	public void addLocale(Locale locale, boolean boolean1) {
		this.locales.put(locale.getID(), locale);
		if (boolean1) {
			this.locale = locale;
			this.localeDefault = locale;
		}
	}

	public Locale getLocale() {
		return this.locale;
	}

	public void addWhiteListWord(String string) {
		if (this.locale != null) {
			this.locale.addWhiteListWord(string);
		}
	}

	public void removeWhiteListWord(String string) {
		if (this.locale != null) {
			this.locale.removeWhiteListWord(string);
		}
	}

	public void addFilterWord(String string) {
		if (this.locale != null) {
			this.locale.addFilterWord(string);
		}
	}

	public void removeFilterWord(String string) {
		if (this.locale != null) {
			this.locale.removeFilterWord(string);
		}
	}

	public void setLocale(String string) {
		if (this.locales.containsKey(string)) {
			this.locale = (Locale)this.locales.get(string);
		} else {
			this.locale = this.localeDefault;
		}
	}

	public String filterString(String string) {
		if (this.enabled && this.locale != null && string != null && this.locale.getFilterWordsCount() > 0) {
			try {
				StringBuffer stringBuffer = new StringBuffer();
				Matcher matcher = this.prePattern.matcher(string);
				while (matcher.find()) {
					if (matcher.group("word") != null) {
						matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(this.locale.filterWord(matcher.group("word"), true)));
					} else if (matcher.group("spaced") != null) {
						Locale locale = this.locale;
						String string2 = matcher.group("spaced");
						matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(" " + locale.filterWord(string2.replaceAll("\\s+", ""))));
					}
				}

				matcher.appendTail(stringBuffer);
				return stringBuffer.toString();
			} catch (Exception exception) {
				System.out.println("Profanity failed for: " + string);
			}
		}

		return string;
	}

	public String validateString(String string) {
		return this.validateString(string, true, true, true);
	}

	public String validateString(String string, boolean boolean1, boolean boolean2, boolean boolean3) {
		if (this.enabled && this.locale != null && string != null && this.locale.getFilterWordsCount() > 0) {
			try {
				String string2 = null;
				boolean boolean4 = false;
				StringBuilder stringBuilder = new StringBuilder();
				Matcher matcher = this.prePattern.matcher(string);
				while (true) {
					while (matcher.find()) {
						if (boolean1 && matcher.group("word") != null) {
							string2 = this.locale.validateWord(matcher.group("word"), boolean2);
							if (string2 != null) {
								if (boolean4) {
									stringBuilder.append(", ");
								}

								stringBuilder.append(string2);
								boolean4 = true;
							}
						} else if (boolean3 && matcher.group("spaced") != null) {
							string2 = this.locale.validateWord(matcher.group("spaced").replaceAll("\\s+", ""), false);
							if (string2 != null) {
								if (boolean4) {
									stringBuilder.append(", ");
								}

								stringBuilder.append(string2);
								boolean4 = true;
							}
						}
					}

					return boolean4 ? stringBuilder.toString() : null;
				}
			} catch (Exception exception) {
				System.out.println("Profanity validate string failed for: " + string);
				exception.printStackTrace();
			}
		}

		return "Failed to parse string :(.";
	}

	static  {
		LOCALES_DIR = "media" + File.separator + "profanity" + File.separator + "locales" + File.separator;
	}
}