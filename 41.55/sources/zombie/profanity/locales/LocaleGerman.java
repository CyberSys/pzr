package zombie.profanity.locales;

import java.util.regex.Matcher;
import zombie.profanity.Phonizer;


public class LocaleGerman extends LocaleEnglish {

	public LocaleGerman(String string) {
		super(string);
	}

	protected void Init() {
		this.storeVowelsAmount = 3;
		super.Init();
		this.addPhonizer(new Phonizer("ringelS", "(?<ringelS>\u00c3\u0178)"){
			
			public void execute(Matcher var1, StringBuffer var2) {
				if (var1.group(this.getName()) != null) {
					var1.appendReplacement(var2, "S");
				}
			}
		});
	}
}
