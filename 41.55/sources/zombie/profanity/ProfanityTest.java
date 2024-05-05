package zombie.profanity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import zombie.ZomboidFileSystem;
import zombie.profanity.locales.Locale;


public class ProfanityTest {

	public static void runTest() {
		ProfanityFilter profanityFilter = ProfanityFilter.getInstance();
		System.out.println("");
		loadDictionary();
		testString(1, "profane stuff:  f u c k. sex xex h4rd \u00c3\u0178hit knight hello, @ $ $ H O L E   ass-hole f-u-c-k f_u_c_k_ @$$h0le fu\'ckeerr: sdsi: KUNT as\'as!! ffffuuuccckkkerrr");
	}

	public static void testString(int int1, String string) {
		ProfanityFilter profanityFilter = ProfanityFilter.getInstance();
		String string2 = "";
		System.out.println("Benchmarking " + int1 + " iterations: ");
		System.out.println("Original: " + string);
		long long1 = System.nanoTime();
		for (int int2 = 0; int2 < int1; ++int2) {
			string2 = profanityFilter.filterString(string);
		}

		long long2 = System.nanoTime();
		long long3 = long2 - long1;
		System.out.println("Done, time spent: " + (float)long3 / 1.0E9F + " seconds");
		System.out.println("Result: " + string2);
		System.out.println("");
	}

	public static void loadDictionary() {
		System.out.println("");
		System.out.println("Dictionary: ");
		long long1 = System.nanoTime();
		ProfanityFilter profanityFilter = ProfanityFilter.getInstance();
		try {
			File file = ZomboidFileSystem.instance.getMediaFile("profanity" + File.separator + "Dictionary.txt");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			new StringBuffer();
			int int1 = 0;
			int int2 = 0;
			String string;
			PrintStream printStream;
			for (Locale locale = profanityFilter.getLocale(); (string = bufferedReader.readLine()) != null; ++int1) {
				String string2 = locale.returnMatchSetForWord(string);
				if (string2 != null) {
					printStream = System.out;
					String string3 = string.trim();
					printStream.println("Found match: " + string3 + ", Phonized: " + locale.returnPhonizedWord(string.trim()) + ", Set: " + string2);
					++int2;
				}
			}

			fileReader.close();
			printStream = System.out;
			int int3 = profanityFilter.getFilterWordsCount();
			printStream.println("Profanity filter tested " + int3 + " blacklisted words against " + int1 + " words from dictionary.");
			System.out.println("Found " + int2 + " matches.");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		long long2 = System.nanoTime();
		long long3 = long2 - long1;
		System.out.println("Done, time spent: " + (float)long3 / 1.0E9F + " seconds");
		System.out.println("");
	}
}
