package zombie.profanity.locales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import zombie.ZomboidFileSystem;
import zombie.profanity.Phonizer;
import zombie.profanity.ProfanityFilter;


public abstract class Locale {
	protected String id;
	protected int storeVowelsAmount = 3;
	protected String phoneticRules = "";
	protected Map phonizers = new HashMap();
	protected Map filterWords = new HashMap();
	protected List filterWordsRaw = new ArrayList();
	protected List filterContains = new ArrayList();
	protected ArrayList whitelistWords = new ArrayList();
	protected Pattern pattern;
	private Pattern preProcessLeet = Pattern.compile("(?<leet>[\\$@34701])\\k<leet>*|(?<nonWord>[^A-Z\\s\\$@34701]+)");
	private Pattern preProcessDoubles = Pattern.compile("(?<doublechar>[A-Z])\\k<doublechar>+");
	private Pattern preProcessVowels = Pattern.compile("(?<vowel>[AOUIE])");

	protected Locale(String string) {
		this.id = string;
		this.Init();
		this.finalizeData();
		this.loadFilterWords();
		this.loadFilterContains();
		this.loadWhiteListWords();
		ProfanityFilter.printDebug("Done init locale: " + this.id);
	}

	public String getID() {
		return this.id;
	}

	public String getPhoneticRules() {
		return this.phoneticRules;
	}

	public int getFilterWordsCount() {
		return this.filterWords.size();
	}

	protected abstract void Init();

	public void addWhiteListWord(String string) {
		string = string.toUpperCase().trim();
		if (!this.whitelistWords.contains(string)) {
			this.whitelistWords.add(string);
		}
	}

	public void removeWhiteListWord(String string) {
		string = string.toUpperCase().trim();
		if (this.whitelistWords.contains(string)) {
			this.whitelistWords.remove(string);
		}
	}

	public boolean isWhiteListedWord(String string) {
		return this.whitelistWords.contains(string.toUpperCase().trim());
	}

	public void addFilterWord(String string) {
		String string2 = this.phonizeWord(string);
		if (string2.length() > 2) {
			String string3 = "";
			if (this.filterWords.containsKey(string2)) {
				string3 = string3 + (String)this.filterWords.get(string2) + ",";
			}

			ProfanityFilter.printDebug("Adding word: " + string + ", Phonized: " + string2);
			this.filterWords.put(string2, string3 + string.toLowerCase());
		} else {
			ProfanityFilter.printDebug("Refusing word: " + string + ", Phonized: " + string2 + ", null or phonized < 2 characters");
		}
	}

	public void removeFilterWord(String string) {
		String string2 = this.phonizeWord(string);
		if (this.filterWords.containsKey(string2)) {
			this.filterWords.remove(string2);
		}
	}

	public void addFilterContains(String string) {
		if (string != null && !string.isEmpty() && !this.filterContains.contains(string.toUpperCase())) {
			this.filterContains.add(string.toUpperCase());
		}
	}

	public void removeFilterContains(String string) {
		this.filterContains.remove(string.toUpperCase());
	}

	public void addFilterRawWord(String string) {
		if (string != null && !string.isEmpty() && !this.filterWordsRaw.contains(string.toUpperCase())) {
			this.filterWordsRaw.add(string.toUpperCase());
		}
	}

	public void removeFilterWordRaw(String string) {
		this.filterWordsRaw.remove(string.toUpperCase());
	}

	protected String repeatString(int int1, char char1) {
		char[] charArray = new char[int1];
		Arrays.fill(charArray, char1);
		return new String(charArray);
	}

	protected boolean containsIgnoreCase(String string, String string2) {
		if (string != null && string2 != null) {
			int int1 = string2.length();
			if (int1 == 0) {
				return true;
			} else {
				for (int int2 = string.length() - int1; int2 >= 0; --int2) {
					if (string.regionMatches(true, int2, string2, 0, int1)) {
						return true;
					}
				}

				return false;
			}
		} else {
			return false;
		}
	}

	public String filterWord(String string) {
		return this.filterWord(string, false);
	}

	public String filterWord(String string, boolean boolean1) {
		if (this.isWhiteListedWord(string)) {
			return string;
		} else {
			String string2 = this.phonizeWord(string);
			if (this.filterWords.containsKey(string2)) {
				return (new String(new char[string.length()])).replace(' ', '*');
			} else {
				if (this.filterWordsRaw.size() > 0) {
					for (int int1 = 0; int1 < this.filterWordsRaw.size(); ++int1) {
						if (string.equalsIgnoreCase((String)this.filterWordsRaw.get(int1))) {
							return (new String(new char[string.length()])).replace(' ', '*');
						}
					}
				}

				if (boolean1) {
					for (int int2 = 0; int2 < this.filterContains.size(); ++int2) {
						String string3 = (String)this.filterContains.get(int2);
						if (this.containsIgnoreCase(string, string3)) {
							string = string.replaceAll("(?i)" + Pattern.quote(string3), this.repeatString(string3.length(), '*'));
						}
					}
				}

				return string;
			}
		}
	}

	public String validateWord(String string, boolean boolean1) {
		if (this.isWhiteListedWord(string)) {
			return null;
		} else {
			String string2 = this.phonizeWord(string);
			if (this.filterWords.containsKey(string2)) {
				return string;
			} else {
				if (this.filterWordsRaw.size() > 0) {
					for (int int1 = 0; int1 < this.filterWordsRaw.size(); ++int1) {
						if (string.equalsIgnoreCase((String)this.filterWordsRaw.get(int1))) {
							return string;
						}
					}
				}

				if (boolean1) {
					for (int int2 = 0; int2 < this.filterContains.size(); ++int2) {
						String string3 = (String)this.filterContains.get(int2);
						if (this.containsIgnoreCase(string, string3)) {
							return string3.toLowerCase();
						}
					}
				}

				return null;
			}
		}
	}

	public String returnMatchSetForWord(String string) {
		String string2 = this.phonizeWord(string);
		return this.filterWords.containsKey(string2) ? (String)this.filterWords.get(string2) : null;
	}

	public String returnPhonizedWord(String string) {
		return this.phonizeWord(string);
	}

	protected String phonizeWord(String string) {
		string = string.toUpperCase().trim();
		if (this.whitelistWords.contains(string)) {
			return string;
		} else {
			string = this.preProcessWord(string);
			if (this.phonizers.size() <= 0) {
				return string;
			} else {
				Matcher matcher = this.pattern.matcher(string);
				StringBuffer stringBuffer = new StringBuffer();
				while (true) {
					while (matcher.find()) {
						Iterator iterator = this.phonizers.entrySet().iterator();
						while (iterator.hasNext()) {
							Entry entry = (Entry)iterator.next();
							if (matcher.group((String)entry.getKey()) != null) {
								((Phonizer)entry.getValue()).execute(matcher, stringBuffer);
								break;
							}
						}
					}

					matcher.appendTail(stringBuffer);
					return stringBuffer.toString();
				}
			}
		}
	}

	private String preProcessWord(String string) {
		Matcher matcher = this.preProcessLeet.matcher(string);
		StringBuffer stringBuffer = new StringBuffer();
		while (matcher.find()) {
			if (matcher.group("leet") != null) {
				String string2 = matcher.group("leet").toString();
				byte byte1 = -1;
				switch (string2.hashCode()) {
				case 36: 
					if (string2.equals("$")) {
						byte1 = 0;
					}

					break;
				
				case 48: 
					if (string2.equals("0")) {
						byte1 = 5;
					}

					break;
				
				case 49: 
					if (string2.equals("1")) {
						byte1 = 6;
					}

					break;
				
				case 51: 
					if (string2.equals("3")) {
						byte1 = 3;
					}

					break;
				
				case 52: 
					if (string2.equals("4")) {
						byte1 = 1;
					}

					break;
				
				case 55: 
					if (string2.equals("7")) {
						byte1 = 4;
					}

					break;
				
				case 64: 
					if (string2.equals("@")) {
						byte1 = 2;
					}

				
				}

				switch (byte1) {
				case 0: 
					matcher.appendReplacement(stringBuffer, "S");
					break;
				
				case 1: 
				
				case 2: 
					matcher.appendReplacement(stringBuffer, "A");
					break;
				
				case 3: 
					matcher.appendReplacement(stringBuffer, "E");
					break;
				
				case 4: 
					matcher.appendReplacement(stringBuffer, "T");
					break;
				
				case 5: 
					matcher.appendReplacement(stringBuffer, "O");
					break;
				
				case 6: 
					matcher.appendReplacement(stringBuffer, "I");
				
				}
			} else if (matcher.group("nonWord") != null) {
				matcher.appendReplacement(stringBuffer, "");
			}
		}

		matcher.appendTail(stringBuffer);
		matcher = this.preProcessDoubles.matcher(stringBuffer.toString());
		stringBuffer.delete(0, stringBuffer.capacity());
		while (matcher.find()) {
			if (matcher.group("doublechar") != null) {
				matcher.appendReplacement(stringBuffer, "${doublechar}");
			}
		}

		matcher.appendTail(stringBuffer);
		matcher = this.preProcessVowels.matcher(stringBuffer.toString());
		stringBuffer.delete(0, stringBuffer.capacity());
		int int1 = 0;
		while (matcher.find()) {
			if (matcher.group("vowel") != null) {
				if (int1 < this.storeVowelsAmount) {
					matcher.appendReplacement(stringBuffer, "${vowel}");
					++int1;
				} else {
					matcher.appendReplacement(stringBuffer, "");
				}
			}
		}

		matcher.appendTail(stringBuffer);
		return stringBuffer.toString();
	}

	protected void addPhonizer(Phonizer phonizer) {
		if (phonizer != null && !this.phonizers.containsKey(phonizer.getName())) {
			this.phonizers.put(phonizer.getName(), phonizer);
		}
	}

	protected void finalizeData() {
		this.phoneticRules = "";
		int int1 = this.phonizers.size();
		int int2 = 0;
		Iterator iterator = this.phonizers.values().iterator();
		while (iterator.hasNext()) {
			Phonizer phonizer = (Phonizer)iterator.next();
			String string = this.phoneticRules;
			this.phoneticRules = string + phonizer.getRegex();
			++int2;
			if (int2 < int1) {
				this.phoneticRules = this.phoneticRules + "|";
			}
		}

		ProfanityFilter.printDebug("PhoneticRules: " + this.phoneticRules);
		this.pattern = Pattern.compile(this.phoneticRules);
	}

	protected void loadFilterWords() {
		try {
			String string = ZomboidFileSystem.instance.getString(ProfanityFilter.LOCALES_DIR + "blacklist_" + this.id + ".txt");
			File file = new File(string);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String string2;
			int int1;
			for (int1 = 0; (string2 = bufferedReader.readLine()) != null; ++int1) {
				this.addFilterWord(string2);
			}

			fileReader.close();
			ProfanityFilter.printDebug("BlackList, " + int1 + " added.");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	protected void loadFilterContains() {
		try {
			String string = ZomboidFileSystem.instance.getString(ProfanityFilter.LOCALES_DIR + "blacklist_contains_" + this.id + ".txt");
			File file = new File(string);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int int1 = 0;
			String string2;
			while ((string2 = bufferedReader.readLine()) != null) {
				if (!string2.startsWith("//")) {
					this.addFilterContains(string2);
					++int1;
				}
			}

			fileReader.close();
			ProfanityFilter.printDebug("BlackList contains, " + int1 + " added.");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	protected void loadWhiteListWords() {
		try {
			String string = ZomboidFileSystem.instance.getString(ProfanityFilter.LOCALES_DIR + "whitelist_" + this.id + ".txt");
			File file = new File(string);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String string2;
			int int1;
			for (int1 = 0; (string2 = bufferedReader.readLine()) != null; ++int1) {
				this.addWhiteListWord(string2);
			}

			fileReader.close();
			ProfanityFilter.printDebug("WhiteList, " + int1 + " added.");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
}
