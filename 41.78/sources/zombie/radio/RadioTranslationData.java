package zombie.radio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import zombie.core.Language;
import zombie.core.Translator;


public final class RadioTranslationData {
	private String filePath;
	private String guid;
	private String language;
	private Language languageEnum;
	private int version = -1;
	private final ArrayList translators = new ArrayList();
	private final Map translations = new HashMap();

	public RadioTranslationData(String string) {
		this.filePath = string;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public String getGuid() {
		return this.guid;
	}

	public String getLanguage() {
		return this.language;
	}

	public Language getLanguageEnum() {
		return this.languageEnum;
	}

	public int getVersion() {
		return this.version;
	}

	public int getTranslationCount() {
		return this.translations.size();
	}

	public ArrayList getTranslators() {
		return this.translators;
	}

	public boolean validate() {
		return this.guid != null && this.language != null && this.version >= 0;
	}

	public boolean loadTranslations() {
		boolean boolean1 = false;
		if (Translator.getLanguage() != this.languageEnum) {
			System.out.println("Radio translations trying to load language that is not the current language...");
			return false;
		} else {
			try {
				File file = new File(this.filePath);
				if (file.exists() && !file.isDirectory()) {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.filePath), Charset.forName(this.languageEnum.charset())));
					String string = null;
					boolean boolean2 = false;
					ArrayList arrayList = new ArrayList();
					while (true) {
						while ((string = bufferedReader.readLine()) != null) {
							string = string.trim();
							if (string.equals("[Translations]")) {
								boolean2 = true;
							} else if (boolean2) {
								String string2;
								if (!string.equals("[Collection]")) {
									if (string.equals("[/Translations]")) {
										boolean1 = true;
										return boolean1;
									}

									String[] stringArray = string.split("=", 2);
									if (stringArray.length == 2) {
										String string3 = stringArray[0].trim();
										string2 = stringArray[1].trim();
										this.translations.put(string3, string2);
									}
								} else {
									String string4 = null;
									while ((string = bufferedReader.readLine()) != null) {
										string = string.trim();
										if (string.equals("[/Collection]")) {
											break;
										}

										String[] stringArray2 = string.split("=", 2);
										if (stringArray2.length == 2) {
											string2 = stringArray2[0].trim();
											String string5 = stringArray2[1].trim();
											if (string2.equals("text")) {
												string4 = string5;
											} else if (string2.equals("member")) {
												arrayList.add(string5);
											}
										}
									}

									if (string4 != null && arrayList.size() > 0) {
										Iterator iterator = arrayList.iterator();
										while (iterator.hasNext()) {
											string2 = (String)iterator.next();
											this.translations.put(string2, string4);
										}
									}

									arrayList.clear();
								}
							}
						}

						return boolean1;
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				boolean1 = false;
			}

			return boolean1;
		}
	}

	public String getTranslation(String string) {
		return this.translations.containsKey(string) ? (String)this.translations.get(string) : null;
	}

	public static RadioTranslationData ReadFile(String string) {
		RadioTranslationData radioTranslationData = new RadioTranslationData(string);
		File file = new File(string);
		if (file.exists() && !file.isDirectory()) {
			try {
				FileInputStream fileInputStream = new FileInputStream(string);
				try {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
					String string2 = null;
					while ((string2 = bufferedReader.readLine()) != null) {
						String[] stringArray = string2.split("=");
						if (stringArray.length > 1) {
							String string3 = stringArray[0].trim();
							String string4 = "";
							for (int int1 = 1; int1 < stringArray.length; ++int1) {
								string4 = string4 + stringArray[int1];
							}

							string4 = string4.trim();
							if (string3.equals("guid")) {
								radioTranslationData.guid = string4;
							} else if (string3.equals("language")) {
								radioTranslationData.language = string4;
							} else if (string3.equals("version")) {
								radioTranslationData.version = Integer.parseInt(string4);
							} else if (string3.equals("translator")) {
								String[] stringArray2 = string4.split(",");
								if (stringArray2.length > 0) {
									String[] stringArray3 = stringArray2;
									int int2 = stringArray2.length;
									for (int int3 = 0; int3 < int2; ++int3) {
										String string5 = stringArray3[int3];
										radioTranslationData.translators.add(string5);
									}
								}
							}
						}

						string2 = string2.trim();
						if (string2.equals("[/Info]")) {
							break;
						}
					}
				} catch (Throwable throwable) {
					try {
						fileInputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				fileInputStream.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		boolean boolean1 = false;
		if (radioTranslationData.language != null) {
			Iterator iterator = Translator.getAvailableLanguage().iterator();
			while (iterator.hasNext()) {
				Language language = (Language)iterator.next();
				if (language.toString().equals(radioTranslationData.language)) {
					radioTranslationData.languageEnum = language;
					boolean1 = true;
					break;
				}
			}
		}

		if (!boolean1 && radioTranslationData.language != null) {
			System.out.println("Language " + radioTranslationData.language + " not found");
			return null;
		} else {
			return radioTranslationData.guid != null && radioTranslationData.language != null && radioTranslationData.version >= 0 ? radioTranslationData : null;
		}
	}
}
