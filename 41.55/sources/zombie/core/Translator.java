package zombie.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.characters.skills.PerkFactory;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;


public final class Translator {
	private static ArrayList availableLanguage = null;
	public static boolean debug = false;
	private static FileWriter debugFile = null;
	private static boolean debugErrors = false;
	private static final HashSet debugItemEvolvedRecipeName = new HashSet();
	private static final HashSet debugItem = new HashSet();
	private static final HashSet debugMultiStageBuild = new HashSet();
	private static final HashSet debugRecipe = new HashSet();
	private static final HashMap moodles = new HashMap();
	private static final HashMap ui = new HashMap();
	private static final HashMap survivalGuide = new HashMap();
	private static final HashMap contextMenu = new HashMap();
	private static final HashMap farming = new HashMap();
	private static final HashMap recipe = new HashMap();
	private static final HashMap igui = new HashMap();
	private static final HashMap sandbox = new HashMap();
	private static final HashMap tooltip = new HashMap();
	private static final HashMap challenge = new HashMap();
	private static final HashSet missing = new HashSet();
	private static ArrayList azertyLanguages = null;
	private static final HashMap news = new HashMap();
	private static final HashMap stash = new HashMap();
	private static final HashMap multiStageBuild = new HashMap();
	private static final HashMap moveables = new HashMap();
	private static final HashMap makeup = new HashMap();
	private static final HashMap gameSound = new HashMap();
	private static final HashMap dynamicRadio = new HashMap();
	private static final HashMap items = new HashMap();
	private static final HashMap itemName = new HashMap();
	private static final HashMap itemEvolvedRecipeName = new HashMap();
	private static final HashMap recordedMedia = new HashMap();
	private static final HashMap recordedMedia_EN = new HashMap();
	public static Language language = null;
	private static String newsHeader = "<IMAGE:media/ui/dot.png> <SIZE:small> ";

	public static void loadFiles() {
		language = null;
		availableLanguage = null;
		String string = ZomboidFileSystem.instance.getCacheDir();
		File file = new File(string + File.separator + "translationProblems.txt");
		if (debug) {
			try {
				if (debugFile != null) {
					debugFile.close();
				}

				debugFile = new FileWriter(file);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		moodles.clear();
		ui.clear();
		survivalGuide.clear();
		items.clear();
		itemName.clear();
		contextMenu.clear();
		farming.clear();
		recipe.clear();
		igui.clear();
		sandbox.clear();
		tooltip.clear();
		challenge.clear();
		news.clear();
		missing.clear();
		stash.clear();
		multiStageBuild.clear();
		moveables.clear();
		makeup.clear();
		gameSound.clear();
		dynamicRadio.clear();
		itemEvolvedRecipeName.clear();
		recordedMedia.clear();
		DebugLog.log("translator: language is " + getLanguage());
		debugErrors = false;
		fillMapFromFile("Tooltip", tooltip);
		fillMapFromFile("IG_UI", igui);
		fillMapFromFile("Recipes", recipe);
		fillMapFromFile("Farming", farming);
		fillMapFromFile("ContextMenu", contextMenu);
		fillMapFromFile("SurvivalGuide", survivalGuide);
		fillMapFromFile("UI", ui);
		fillMapFromFile("Items", items);
		fillMapFromFile("ItemName", itemName);
		fillMapFromFile("Moodles", moodles);
		fillMapFromFile("Sandbox", sandbox);
		fillMapFromFile("Challenge", challenge);
		fillMapFromFile("Stash", stash);
		fillMapFromFile("MultiStageBuild", multiStageBuild);
		fillMapFromFile("Moveables", moveables);
		fillMapFromFile("MakeUp", makeup);
		fillMapFromFile("GameSound", gameSound);
		fillMapFromFile("DynamicRadio", dynamicRadio);
		fillMapFromFile("EvolvedRecipeName", itemEvolvedRecipeName);
		fillMapFromFile("Recorded_Media", recordedMedia);
		fillNewsFromFile(news);
		if (debug) {
			if (debugErrors) {
				DebugLog.log("translator: errors detected, please see " + file.getAbsolutePath());
			}

			debugItemEvolvedRecipeName.clear();
			debugItem.clear();
			debugMultiStageBuild.clear();
			debugRecipe.clear();
		}

		PerkFactory.initTranslations();
	}

	private static void fillNewsFromFile(HashMap hashMap) {
		HashMap hashMap2 = new HashMap();
		ArrayList arrayList = ZomboidFileSystem.instance.getModIDs();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			String string = ZomboidFileSystem.instance.getModDir((String)arrayList.get(int1));
			if (string != null) {
				tryFillNewsFromFile(string, hashMap, hashMap2, getLanguage());
				if (getLanguage() != getDefaultLanguage()) {
					tryFillNewsFromFile(string, hashMap, hashMap2, getDefaultLanguage());
				}
			}
		}

		tryFillNewsFromFile(".", hashMap, hashMap2, getLanguage());
		if (getLanguage() != getDefaultLanguage()) {
			tryFillNewsFromFile(".", hashMap, hashMap2, getDefaultLanguage());
		}

		Iterator iterator = hashMap2.values().iterator();
		while (iterator.hasNext()) {
			Translator.News news = (Translator.News)iterator.next();
			hashMap.put("News_" + news.version + "_Disclaimer", news.toRichText());
		}

		hashMap2.clear();
	}

	private static void tryFillNewsFromFile(String string, HashMap hashMap, HashMap hashMap2, Language language) {
		File file = new File(string + File.separator + "media" + File.separator + "lua" + File.separator + "shared" + File.separator + "Translate" + File.separator + language + File.separator + "News_" + language + ".txt");
		if (file.exists()) {
			doNews(file, hashMap2, language);
		}
	}

	private static void doNews(File file, HashMap hashMap, Language language) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			try {
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName(language.charset()));
				try {
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					try {
						Translator.News news = null;
						String string = "";
						String string2;
						while ((string2 = bufferedReader.readLine()) != null) {
							if (!string2.trim().isEmpty()) {
								if (string2.startsWith("[VERSION]")) {
									String string3 = string2.replaceFirst("\\[VERSION\\]", "").trim();
									if (hashMap.containsKey(string3)) {
										news = null;
									} else {
										hashMap.put(string3, news = new Translator.News(string3));
									}
								} else if (string2.startsWith("[NEWS]")) {
									string = "news";
								} else if (string2.startsWith("[BALANCE]")) {
									string = "balance";
								} else if (string2.startsWith("[BUG FIX]")) {
									string = "bugfix";
								} else if (news != null) {
									if ("news".equals(string)) {
										addNewsLine(string2, news.newsList);
									} else if ("balance".equals(string)) {
										addNewsLine(string2, news.balance);
									} else if ("bugfix".equals(string)) {
										addNewsLine(string2, news.bugfix);
									} else {
										addNewsLine(string2, news.other);
									}
								}
							}
						}
					} catch (Throwable throwable) {
						try {
							bufferedReader.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					bufferedReader.close();
				} catch (Throwable throwable3) {
					try {
						inputStreamReader.close();
					} catch (Throwable throwable4) {
						throwable3.addSuppressed(throwable4);
					}

					throw throwable3;
				}

				inputStreamReader.close();
			} catch (Throwable throwable5) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable6) {
					throwable5.addSuppressed(throwable6);
				}

				throw throwable5;
			}

			fileInputStream.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private static void addNewsLine(String string, ArrayList arrayList) {
		if (string.startsWith("[BOLD]")) {
			string = string.replaceFirst("\\[BOLD\\]", "<IMAGE:media/ui/dot.png> <SIZE:medium>");
			arrayList.add(string + " <LINE> ");
		} else if (string.startsWith("[DOT2]")) {
			string = string.replaceFirst("\\[DOT2\\]", "<IMAGE:media/ui/dot2.png> <SIZE:small>");
			arrayList.add(string + " <LINE> ");
		} else {
			arrayList.add(newsHeader + string + " <LINE> ");
		}
	}

	public static ArrayList getNewsVersions() {
		ArrayList arrayList = new ArrayList();
		arrayList.addAll(news.keySet());
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			String string = (String)arrayList.get(int1);
			string = string.replace("News_", "");
			string = string.replace("_Disclaimer", "");
			arrayList.set(int1, string);
		}

		Collections.sort(arrayList);
		return arrayList;
	}

	private static void tryFillMapFromFile(String string, String string2, HashMap hashMap, Language language) {
		File file = new File(string + File.separator + "media" + File.separator + "lua" + File.separator + "shared" + File.separator + "Translate" + File.separator + language + File.separator + string2 + "_" + language + ".txt");
		if (file.exists()) {
			parseFile(file, hashMap, language);
		}
	}

	private static void tryFillMapFromMods(String string, HashMap hashMap, Language language) {
		ArrayList arrayList = ZomboidFileSystem.instance.getModIDs();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			String string2 = ZomboidFileSystem.instance.getModDir((String)arrayList.get(int1));
			if (string2 != null) {
				tryFillMapFromFile(string2, string, hashMap, language);
			}
		}
	}

	public static void addLanguageToList(Language language, ArrayList arrayList) {
		if (language != null) {
			if (!arrayList.contains(language)) {
				arrayList.add(language);
				if (language.base() != null) {
					language = Languages.instance.getByName(language.base());
					addLanguageToList(language, arrayList);
				}
			}
		}
	}

	private static void fillMapFromFile(String string, HashMap hashMap) {
		ArrayList arrayList = new ArrayList();
		addLanguageToList(getLanguage(), arrayList);
		addLanguageToList(getDefaultLanguage(), arrayList);
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			Language language = (Language)arrayList.get(int1);
			tryFillMapFromMods(string, hashMap, language);
			tryFillMapFromFile(ZomboidFileSystem.instance.base.getPath(), string, hashMap, language);
		}

		arrayList.clear();
	}

	private static void parseFile(File file, HashMap hashMap, Language language) {
		String string = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			try {
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName(language.charset()));
				try {
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					try {
						bufferedReader.readLine();
						boolean boolean1 = false;
						String string2 = "";
						String string3 = "";
						int int1 = 1;
						String string4 = file.getName().replace("_" + getDefaultLanguage(), "_" + getLanguage());
						while ((string = bufferedReader.readLine()) != null) {
							++int1;
							try {
								if (string.contains("=") && string.contains("\"")) {
									if (string.trim().startsWith("Recipe_")) {
										string2 = string.split("=")[0].replaceAll("Recipe_", "").replaceAll("_", " ").trim();
										string3 = string.split("=")[1];
										string3 = string3.substring(string3.indexOf("\"") + 1, string3.lastIndexOf("\""));
									} else {
										String[] stringArray;
										if (string.trim().startsWith("DisplayName")) {
											stringArray = string.split("=");
											if (string.trim().startsWith("DisplayName_")) {
												string2 = stringArray[0].replaceAll("DisplayName_", "").trim();
											} else {
												string2 = stringArray[0].replaceAll("DisplayName", "").trim();
											}

											if ("Anti_depressants".equals(string2)) {
												string2 = "Antidepressants";
											}

											string3 = stringArray[1];
											string3 = string3.substring(string3.indexOf("\"") + 1, string3.lastIndexOf("\""));
										} else {
											int int2;
											int int3;
											if (string.trim().startsWith("EvolvedRecipeName_")) {
												stringArray = string.split("=");
												string2 = stringArray[0].replaceAll("EvolvedRecipeName_", "").trim();
												string3 = stringArray[1];
												int2 = string3.indexOf("\"");
												int3 = string3.lastIndexOf("\"");
												string3 = string3.substring(int2 + 1, int3);
											} else if (string.trim().startsWith("ItemName_")) {
												stringArray = string.split("=");
												string2 = stringArray[0].replaceAll("ItemName_", "").trim();
												string3 = stringArray[1];
												int2 = string3.indexOf("\"");
												int3 = string3.lastIndexOf("\"");
												string3 = string3.substring(int2 + 1, int3);
											} else {
												string2 = string.split("=")[0].trim();
												string3 = string.substring(string.indexOf("=") + 1);
												string3 = string3.substring(string3.indexOf("\"") + 1, string3.lastIndexOf("\""));
												if (string.contains("..")) {
													boolean1 = true;
												}
											}
										}
									}
								} else if (string.contains("--") || string.trim().isEmpty() || !string.trim().endsWith("..") && !boolean1) {
									boolean1 = false;
								} else {
									boolean1 = true;
									string3 = string3 + string.substring(string.indexOf("\"") + 1, string.lastIndexOf("\""));
								}

								if (!boolean1 || !string.trim().endsWith("..")) {
									if (!string2.isEmpty()) {
										if (!hashMap.containsKey(string2)) {
											hashMap.put(string2, string3);
											if (hashMap == recordedMedia && language == getDefaultLanguage()) {
												recordedMedia_EN.put(string2, string3);
											}

											if (debug && language == getDefaultLanguage() && getLanguage() != getDefaultLanguage()) {
												if (string4 != null) {
													debugwrite(string4 + "\r\n");
													string4 = null;
												}

												debugwrite("\t" + string2 + " = \"" + string3 + "\",\r\n");
												debugErrors = true;
											}
										} else if (debug && language == getDefaultLanguage() && getLanguage() != getDefaultLanguage()) {
											String string5 = (String)hashMap.get(string2);
											if (countSubstitutions(string5) != countSubstitutions(string3)) {
												debugwrite("wrong number of % substitutions in " + string2 + "	" + getDefaultLanguage() + "=\"" + string3 + "\"	" + getLanguage() + "=\"" + string5 + "\"\r\n");
												debugErrors = true;
											}
										}
									}

									boolean1 = false;
									string3 = "";
									string2 = "";
								}
							} catch (Exception exception) {
								if (debug) {
									if (string4 != null) {
										debugwrite(string4 + "\r\n");
										string4 = null;
									}

									debugwrite("line " + int1 + ": " + string2 + " = " + string3 + "\r\n");
									if (debugFile != null) {
										exception.printStackTrace(new PrintWriter(debugFile));
									}

									debugwrite("\r\n");
									debugErrors = true;
								}
							}
						}
					} catch (Throwable throwable) {
						try {
							bufferedReader.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					bufferedReader.close();
				} catch (Throwable throwable3) {
					try {
						inputStreamReader.close();
					} catch (Throwable throwable4) {
						throwable3.addSuppressed(throwable4);
					}

					throw throwable3;
				}

				inputStreamReader.close();
			} catch (Throwable throwable5) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable6) {
					throwable5.addSuppressed(throwable6);
				}

				throw throwable5;
			}

			fileInputStream.close();
		} catch (Exception exception2) {
			exception2.printStackTrace();
		}
	}

	public static String getText(String string) {
		return getTextInternal(string, false);
	}

	public static String getTextOrNull(String string) {
		return getTextInternal(string, true);
	}

	private static String getTextInternal(String string, boolean boolean1) {
		if (ui == null) {
			loadFiles();
		}

		String string2 = null;
		if (string.startsWith("UI_")) {
			string2 = (String)ui.get(string);
		} else if (string.startsWith("Moodles_")) {
			string2 = (String)moodles.get(string);
		} else if (string.startsWith("SurvivalGuide_")) {
			string2 = (String)survivalGuide.get(string);
		} else if (string.startsWith("Farming_")) {
			string2 = (String)farming.get(string);
		} else if (string.startsWith("IGUI_")) {
			string2 = (String)igui.get(string);
		} else if (string.startsWith("ContextMenu_")) {
			string2 = (String)contextMenu.get(string);
		} else if (string.startsWith("GameSound_")) {
			string2 = (String)gameSound.get(string);
		} else if (string.startsWith("Sandbox_")) {
			string2 = (String)sandbox.get(string);
		} else if (string.startsWith("Tooltip_")) {
			string2 = (String)tooltip.get(string);
		} else if (string.startsWith("Challenge_")) {
			string2 = (String)challenge.get(string);
		} else if (string.startsWith("MakeUp")) {
			string2 = (String)makeup.get(string);
		} else if (string.startsWith("News_")) {
			string2 = (String)news.get(string);
		} else if (string.startsWith("Stash_")) {
			string2 = (String)stash.get(string);
		} else if (string.startsWith("RM_")) {
			string2 = (String)recordedMedia.get(string);
		}

		String string3 = Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue() ? "*" : null;
		if (string2 == null) {
			if (boolean1) {
				return null;
			}

			if (!missing.contains(string)) {
				DebugLog.log("ERROR: Missing translation \"" + string + "\"");
				if (debug) {
					debugwrite("ERROR: Missing translation \"" + string + "\"\r\n");
				}

				missing.add(string);
			}

			string2 = string;
			string3 = Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue() ? "!" : null;
		}

		if (string2.contains("<br>")) {
			return string2.replaceAll("<br>", "\n");
		} else {
			return string3 == null ? string2 : string3 + string2;
		}
	}

	private static int countSubstitutions(String string) {
		int int1 = 0;
		if (string.contains("%1")) {
			++int1;
		}

		if (string.contains("%2")) {
			++int1;
		}

		if (string.contains("%3")) {
			++int1;
		}

		if (string.contains("%4")) {
			++int1;
		}

		return int1;
	}

	private static String subst(String string, String string2, Object object) {
		if (object != null) {
			if (object instanceof Double) {
				double double1 = (Double)object;
				string = string.replaceAll(string2, double1 == (double)((long)double1) ? Long.toString((long)double1) : object.toString());
			} else {
				string = string.replaceAll(string2, Matcher.quoteReplacement(object.toString()));
			}
		}

		return string;
	}

	public static String getText(String string, Object object) {
		String string2 = getText(string);
		string2 = subst(string2, "%1", object);
		return string2;
	}

	public static String getText(String string, Object object, Object object2) {
		String string2 = getText(string);
		string2 = subst(string2, "%1", object);
		string2 = subst(string2, "%2", object2);
		return string2;
	}

	public static String getText(String string, Object object, Object object2, Object object3) {
		String string2 = getText(string);
		string2 = subst(string2, "%1", object);
		string2 = subst(string2, "%2", object2);
		string2 = subst(string2, "%3", object3);
		return string2;
	}

	public static String getText(String string, Object object, Object object2, Object object3, Object object4) {
		String string2 = getText(string);
		string2 = subst(string2, "%1", object);
		string2 = subst(string2, "%2", object2);
		string2 = subst(string2, "%3", object3);
		string2 = subst(string2, "%4", object4);
		return string2;
	}

	public static String getTextOrNull(String string, Object object) {
		String string2 = getTextOrNull(string);
		if (string2 == null) {
			return null;
		} else {
			string2 = subst(string2, "%1", object);
			return string2;
		}
	}

	public static String getTextOrNull(String string, Object object, Object object2) {
		String string2 = getTextOrNull(string);
		if (string2 == null) {
			return null;
		} else {
			string2 = subst(string2, "%1", object);
			string2 = subst(string2, "%2", object2);
			return string2;
		}
	}

	public static String getTextOrNull(String string, Object object, Object object2, Object object3) {
		String string2 = getTextOrNull(string);
		if (string2 == null) {
			return null;
		} else {
			string2 = subst(string2, "%1", object);
			string2 = subst(string2, "%2", object2);
			string2 = subst(string2, "%3", object3);
			return string2;
		}
	}

	public static String getTextOrNull(String string, Object object, Object object2, Object object3, Object object4) {
		String string2 = getTextOrNull(string);
		if (string2 == null) {
			return null;
		} else {
			string2 = subst(string2, "%1", object);
			string2 = subst(string2, "%2", object2);
			string2 = subst(string2, "%3", object3);
			string2 = subst(string2, "%4", object4);
			return string2;
		}
	}

	private static String getDefaultText(String string) {
		KahluaTable kahluaTable = LuaManager.env;
		String string2 = string.split("_")[0];
		return changeSomeStuff((String)((KahluaTable)kahluaTable.rawget(string2 + "_" + getDefaultLanguage().name())).rawget(string));
	}

	private static String changeSomeStuff(String string) {
		return string;
	}

	public static void setLanguage(Language language) {
		if (language == null) {
			language = getDefaultLanguage();
		}

		language = language;
	}

	public static void setLanguage(int int1) {
		Language language = Languages.instance.getByIndex(int1);
		setLanguage(language);
	}

	public static Language getLanguage() {
		if (language == null) {
			String string = Core.getInstance().getOptionLanguageName();
			if (!StringUtils.isNullOrWhitespace(string)) {
				language = Languages.instance.getByName(string);
			}
		}

		if (language == null) {
			language = Languages.instance.getByName(System.getProperty("user.language").toUpperCase());
		}

		if (language == null) {
			language = getDefaultLanguage();
		}

		return language;
	}

	public static String getCharset() {
		return getLanguage().charset();
	}

	public static ArrayList getAvailableLanguage() {
		if (availableLanguage == null) {
			availableLanguage = new ArrayList();
			for (int int1 = 0; int1 < Languages.instance.getNumLanguages(); ++int1) {
				availableLanguage.add(Languages.instance.getByIndex(int1));
			}
		}

		return availableLanguage;
	}

	public static String getDisplayItemName(String string) {
		String string2 = null;
		string2 = (String)items.get(string.replaceAll(" ", "_").replaceAll("-", "_"));
		return string2 == null ? string : string2;
	}

	public static String getItemNameFromFullType(String string) {
		if (!string.contains(".")) {
			throw new IllegalArgumentException("fullType must contain \".\" i.e. module.type");
		} else {
			String string2 = (String)itemName.get(string);
			if (string2 == null) {
				if (debug && getLanguage() != getDefaultLanguage() && !debugItem.contains(string)) {
					debugItem.add(string);
				}

				Item item = ScriptManager.instance.getItem(string);
				if (item == null) {
					string2 = string;
				} else {
					string2 = item.getDisplayName();
				}

				itemName.put(string, string2);
			}

			return string2;
		}
	}

	public static void setDefaultItemEvolvedRecipeName(String string, String string2) {
		if (getLanguage() == getDefaultLanguage()) {
			if (!string.contains(".")) {
				throw new IllegalArgumentException("fullType must contain \".\" i.e. module.type");
			} else if (!itemEvolvedRecipeName.containsKey(string)) {
				itemEvolvedRecipeName.put(string, string2);
			}
		}
	}

	public static String getItemEvolvedRecipeName(String string) {
		if (!string.contains(".")) {
			throw new IllegalArgumentException("fullType must contain \".\" i.e. module.type");
		} else {
			String string2 = (String)itemEvolvedRecipeName.get(string);
			if (string2 == null) {
				if (debug && getLanguage() != getDefaultLanguage() && !debugItemEvolvedRecipeName.contains(string)) {
					debugItemEvolvedRecipeName.add(string);
				}

				Item item = ScriptManager.instance.getItem(string);
				if (item == null) {
					string2 = string;
				} else {
					string2 = item.getDisplayName();
				}

				itemEvolvedRecipeName.put(string, string2);
			}

			return string2;
		}
	}

	public static String getMoveableDisplayName(String string) {
		String string2 = string.replaceAll(" ", "_").replaceAll("-", "_").replaceAll("\'", "").replaceAll("\\.", "");
		String string3 = (String)moveables.get(string2);
		if (string3 == null) {
			return Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue() ? "!" + string : string;
		} else {
			return Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue() ? "*" + string3 : string3;
		}
	}

	public static String getMultiStageBuild(String string) {
		String string2 = (String)multiStageBuild.get("MultiStageBuild_" + string);
		if (string2 == null) {
			if (debug && getLanguage() != getDefaultLanguage() && !debugMultiStageBuild.contains(string)) {
				debugMultiStageBuild.add(string);
			}

			return string;
		} else {
			return string2;
		}
	}

	public static String getRecipeName(String string) {
		String string2 = null;
		string2 = (String)recipe.get(string);
		if (string2 != null && !string2.isEmpty()) {
			return string2;
		} else {
			if (debug && getLanguage() != getDefaultLanguage() && !debugRecipe.contains(string)) {
				debugRecipe.add(string);
			}

			return string;
		}
	}

	public static Language getDefaultLanguage() {
		return Languages.instance.getDefaultLanguage();
	}

	public static void debugItemEvolvedRecipeNames() {
		if (debug && !debugItemEvolvedRecipeName.isEmpty()) {
			debugwrite("EvolvedRecipeName_" + getLanguage() + ".txt\r\n");
			ArrayList arrayList = new ArrayList();
			arrayList.addAll(debugItemEvolvedRecipeName);
			Collections.sort(arrayList);
			Iterator iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				debugwrite("\tEvolvedRecipeName_" + string + " = \"" + (String)itemEvolvedRecipeName.get(string) + "\",\r\n");
			}

			debugItemEvolvedRecipeName.clear();
		}
	}

	public static void debugItemNames() {
		if (debug && !debugItem.isEmpty()) {
			debugwrite("ItemName_" + getLanguage() + ".txt\r\n");
			ArrayList arrayList = new ArrayList();
			arrayList.addAll(debugItem);
			Collections.sort(arrayList);
			Iterator iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				debugwrite("\tItemName_" + string + " = \"" + (String)itemName.get(string) + "\",\r\n");
			}

			debugItem.clear();
		}
	}

	public static void debugMultiStageBuildNames() {
		if (debug && !debugMultiStageBuild.isEmpty()) {
			debugwrite("MultiStageBuild_" + getLanguage() + ".txt\r\n");
			ArrayList arrayList = new ArrayList();
			arrayList.addAll(debugMultiStageBuild);
			Collections.sort(arrayList);
			Iterator iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				debugwrite("\tMultiStageBuild_" + string + " = \"\",\r\n");
			}

			debugMultiStageBuild.clear();
		}
	}

	public static void debugRecipeNames() {
		if (debug && !debugRecipe.isEmpty()) {
			debugwrite("Recipes_" + getLanguage() + ".txt\r\n");
			ArrayList arrayList = new ArrayList();
			arrayList.addAll(debugRecipe);
			Collections.sort(arrayList);
			Iterator iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				debugwrite("\tRecipe_" + string.replace(" ", "_") + " = \"\",\r\n");
			}

			debugRecipe.clear();
		}
	}

	private static void debugwrite(String string) {
		if (debugFile != null) {
			try {
				debugFile.write(string);
				debugFile.flush();
			} catch (IOException ioException) {
			}
		}
	}

	public static ArrayList getAzertyMap() {
		if (azertyLanguages == null) {
			azertyLanguages = new ArrayList();
			azertyLanguages.add("FR");
		}

		return azertyLanguages;
	}

	public static String getRadioText(String string) {
		String string2 = (String)dynamicRadio.get(string);
		return string2 == null ? string : string2;
	}

	public static String getTextMediaEN(String string) {
		if (ui == null) {
			loadFiles();
		}

		String string2 = null;
		if (string.startsWith("RM_")) {
			string2 = (String)recordedMedia_EN.get(string);
		}

		String string3 = Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue() ? "*" : null;
		if (string2 == null) {
			if (!missing.contains(string)) {
				DebugLog.log("ERROR: Missing translation \"" + string + "\"");
				if (debug) {
					debugwrite("ERROR: Missing translation \"" + string + "\"\r\n");
				}

				missing.add(string);
			}

			string2 = string;
			string3 = Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue() ? "!" : null;
		}

		if (string2.contains("<br>")) {
			return string2.replaceAll("<br>", "\n");
		} else {
			return string3 == null ? string2 : string3 + string2;
		}
	}

	private static final class News {
		String version;
		ArrayList newsList = new ArrayList();
		ArrayList balance = new ArrayList();
		ArrayList bugfix = new ArrayList();
		ArrayList other = new ArrayList();

		News(String string) {
			this.version = string;
		}

		String toRichText() {
			StringBuilder stringBuilder = new StringBuilder("");
			Iterator iterator;
			String string;
			if (!this.newsList.isEmpty()) {
				stringBuilder.append("<LINE> <LEFT> <SIZE:medium> [New] <LINE> <LINE> ");
				iterator = this.newsList.iterator();
				while (iterator.hasNext()) {
					string = (String)iterator.next();
					stringBuilder.append(string);
				}
			}

			if (!this.balance.isEmpty()) {
				stringBuilder.append("<LINE> <LEFT> <SIZE:medium> [Balance] <LINE> <LINE> ");
				iterator = this.balance.iterator();
				while (iterator.hasNext()) {
					string = (String)iterator.next();
					stringBuilder.append(string);
				}
			}

			if (!this.bugfix.isEmpty()) {
				stringBuilder.append("<LINE> <LEFT> <SIZE:medium> [Bug Fix] <LINE> <LINE> ");
				iterator = this.bugfix.iterator();
				while (iterator.hasNext()) {
					string = (String)iterator.next();
					stringBuilder.append(string);
				}
			}

			if (!this.other.isEmpty()) {
				stringBuilder.append("<LINE> <LEFT> <SIZE:medium> [Other] <LINE> <LINE> ");
				iterator = this.other.iterator();
				while (iterator.hasNext()) {
					string = (String)iterator.next();
					stringBuilder.append(string);
				}
			}

			return stringBuilder.toString();
		}
	}
}
