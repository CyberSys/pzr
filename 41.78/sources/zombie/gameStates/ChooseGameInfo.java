package zombie.gameStates;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.GameVersion;
import zombie.core.IndieFileLoader;
import zombie.core.Language;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.core.znet.SteamWorkshop;
import zombie.debug.DebugLog;
import zombie.util.StringUtils;


public final class ChooseGameInfo {
	private static final HashMap Maps = new HashMap();
	private static final HashMap Mods = new HashMap();
	private static final HashSet MissingMods = new HashSet();
	private static final ArrayList tempStrings = new ArrayList();

	private ChooseGameInfo() {
	}

	public static void Reset() {
		Maps.clear();
		Mods.clear();
		MissingMods.clear();
	}

	private static void readTitleDotTxt(ChooseGameInfo.Map map, String string, Language language) throws IOException {
		String string2 = language.toString();
		String string3 = "media/lua/shared/Translate/" + string2 + "/" + string + "/title.txt";
		File file = new File(ZomboidFileSystem.instance.getString(string3));
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			try {
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName(language.charset()));
				try {
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					try {
						String string4 = bufferedReader.readLine();
						string4 = StringUtils.stripBOM(string4);
						if (!StringUtils.isNullOrWhitespace(string4)) {
							map.title = string4.trim();
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
		} catch (FileNotFoundException fileNotFoundException) {
		}
	}

	private static void readDescriptionDotTxt(ChooseGameInfo.Map map, String string, Language language) throws IOException {
		String string2 = language.toString();
		String string3 = "media/lua/shared/Translate/" + string2 + "/" + string + "/description.txt";
		File file = new File(ZomboidFileSystem.instance.getString(string3));
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			try {
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName(language.charset()));
				try {
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					try {
						map.desc = "";
						String string4;
						for (boolean boolean1 = true; (string4 = bufferedReader.readLine()) != null; map.desc = map.desc + string4) {
							if (boolean1) {
								string4 = StringUtils.stripBOM(string4);
								boolean1 = false;
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
		} catch (FileNotFoundException fileNotFoundException) {
		}
	}

	public static ChooseGameInfo.Map getMapDetails(String string) {
		if (Maps.containsKey(string)) {
			return (ChooseGameInfo.Map)Maps.get(string);
		} else {
			File file = new File(ZomboidFileSystem.instance.getString("media/maps/" + string + "/map.info"));
			if (!file.exists()) {
				return null;
			} else {
				ChooseGameInfo.Map map = new ChooseGameInfo.Map();
				map.dir = (new File(file.getParent())).getAbsolutePath();
				map.title = string;
				map.lotsDir = new ArrayList();
				try {
					FileReader fileReader = new FileReader(file.getAbsolutePath());
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					String string2 = null;
					try {
						while ((string2 = bufferedReader.readLine()) != null) {
							string2 = string2.trim();
							if (string2.startsWith("title=")) {
								map.title = string2.replace("title=", "");
							} else if (string2.startsWith("lots=")) {
								map.lotsDir.add(string2.replace("lots=", "").trim());
							} else if (string2.startsWith("description=")) {
								if (map.desc == null) {
									map.desc = "";
								}

								String string3 = map.desc;
								map.desc = string3 + string2.replace("description=", "");
							} else if (string2.startsWith("fixed2x=")) {
								map.bFixed2x = Boolean.parseBoolean(string2.replace("fixed2x=", "").trim());
							}
						}
					} catch (IOException ioException) {
						Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, ioException);
					}

					bufferedReader.close();
					map.thumb = Texture.getSharedTexture(map.dir + "/thumb.png");
					ArrayList arrayList = new ArrayList();
					Translator.addLanguageToList(Translator.getLanguage(), arrayList);
					Translator.addLanguageToList(Translator.getDefaultLanguage(), arrayList);
					for (int int1 = arrayList.size() - 1; int1 >= 0; --int1) {
						Language language = (Language)arrayList.get(int1);
						readTitleDotTxt(map, string, language);
						readDescriptionDotTxt(map, string, language);
					}
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
					return null;
				}

				Maps.put(string, map);
				return map;
			}
		}
	}

	public static ChooseGameInfo.Mod getModDetails(String string) {
		if (MissingMods.contains(string)) {
			return null;
		} else if (Mods.containsKey(string)) {
			return (ChooseGameInfo.Mod)Mods.get(string);
		} else {
			String string2 = ZomboidFileSystem.instance.getModDir(string);
			if (string2 == null) {
				ArrayList arrayList = tempStrings;
				ZomboidFileSystem.instance.getAllModFolders(arrayList);
				ArrayList arrayList2 = new ArrayList();
				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					File file = new File((String)arrayList.get(int1), "mod.info");
					arrayList2.clear();
					ChooseGameInfo.Mod mod = ZomboidFileSystem.instance.searchForModInfo(file, string, arrayList2);
					for (int int2 = 0; int2 < arrayList2.size(); ++int2) {
						ChooseGameInfo.Mod mod2 = (ChooseGameInfo.Mod)arrayList2.get(int2);
						Mods.putIfAbsent(mod2.getId(), mod2);
					}

					if (mod != null) {
						return mod;
					}
				}
			}

			ChooseGameInfo.Mod mod3 = readModInfo(string2);
			if (mod3 == null) {
				MissingMods.add(string);
			}

			return mod3;
		}
	}

	public static ChooseGameInfo.Mod getAvailableModDetails(String string) {
		ChooseGameInfo.Mod mod = getModDetails(string);
		return mod != null && mod.isAvailable() ? mod : null;
	}

	public static ChooseGameInfo.Mod readModInfo(String string) {
		ChooseGameInfo.Mod mod = readModInfoAux(string);
		if (mod != null) {
			ChooseGameInfo.Mod mod2 = (ChooseGameInfo.Mod)Mods.get(mod.getId());
			if (mod2 == null) {
				Mods.put(mod.getId(), mod);
			} else if (mod2 != mod) {
				ZomboidFileSystem.instance.getAllModFolders(tempStrings);
				int int1 = tempStrings.indexOf(mod.getDir());
				int int2 = tempStrings.indexOf(mod2.getDir());
				if (int1 < int2) {
					Mods.put(mod.getId(), mod);
				}
			}
		}

		return mod;
	}

	private static ChooseGameInfo.Mod readModInfoAux(String string) {
		if (string != null) {
			ChooseGameInfo.Mod mod = ZomboidFileSystem.instance.getModInfoForDir(string);
			if (mod.bRead) {
				return mod.bValid ? mod : null;
			}

			mod.bRead = true;
			String string2 = string + File.separator + "mod.info";
			File file = new File(string2);
			if (!file.exists()) {
				DebugLog.Mod.warn("can\'t find \"" + string2 + "\"");
				return null;
			}

			mod.setId(file.getParentFile().getName());
			try {
				InputStreamReader inputStreamReader = IndieFileLoader.getStreamReader(string2);
				ChooseGameInfo.Mod mod2;
				label295: {
					Object object;
					label296: {
						label297: {
							Object object2;
							label298: {
								String string3;
								label299: {
									String string4;
									label300: {
										try {
											label301: {
												BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
												label273: {
													label272: {
														label271: {
															label270: {
																label269: {
																	label268: while (true) {
																		try {
																			label264: while (true) {
																				String string5;
																				while ((string5 = bufferedReader.readLine()) != null) {
																					if (string5.contains("name=")) {
																						mod.name = string5.replace("name=", "");
																					} else {
																						String string6;
																						if (string5.contains("poster=")) {
																							string6 = string5.replace("poster=", "");
																							if (!StringUtils.isNullOrWhitespace(string6)) {
																								mod.posters.add(string + File.separator + string6);
																							}
																						} else if (!string5.contains("description=")) {
																							if (string5.contains("require=")) {
																								mod.setRequire(new ArrayList(Arrays.asList(string5.replace("require=", "").split(","))));
																							} else if (string5.contains("id=")) {
																								mod.setId(string5.replace("id=", ""));
																							} else if (string5.contains("url=")) {
																								mod.setUrl(string5.replace("url=", ""));
																							} else {
																								int int1;
																								if (string5.contains("pack=")) {
																									string6 = string5.replace("pack=", "").trim();
																									if (string6.isEmpty()) {
																										DebugLog.Mod.error("pack= line requires a file name");
																										string4 = null;
																										break label270;
																									}

																									int int2 = TextureID.bUseCompressionOption ? 4 : 0;
																									int2 |= 64;
																									int1 = string6.indexOf("type=");
																									String string7;
																									if (int1 != -1) {
																										string7 = string6.substring(int1 + "type=".length());
																										byte byte1 = -1;
																										switch (string7.hashCode()) {
																										case 3732: 
																											if (string7.equals("ui")) {
																												byte1 = 0;
																											}

																										
																										default: 
																											switch (byte1) {
																											case 0: 
																												int2 = 2;
																												break;
																											
																											default: 
																												DebugLog.Mod.error("unknown pack type=" + string7);
																											
																											}

																											int int3 = string6.indexOf(32);
																											string6 = string6.substring(0, int3).trim();
																										
																										}
																									}

																									string7 = string6;
																									string3 = "";
																									if (string6.endsWith(".floor")) {
																										string7 = string6.substring(0, string6.lastIndexOf(46));
																										string3 = ".floor";
																										int2 &= -5;
																									}

																									int int4 = Core.getInstance().getOptionTexture2x() ? 2 : 1;
																									if (Core.SafeModeForced) {
																										int4 = 1;
																									}

																									if (int4 == 2) {
																										File file2 = new File(string + File.separator + "media" + File.separator + "texturepacks" + File.separator + string7 + "2x" + string3 + ".pack");
																										if (file2.isFile()) {
																											DebugLog.Mod.printf("2x version of %s.pack found.\n", string6);
																											string6 = string7 + "2x" + string3;
																										} else {
																											file2 = new File(string + File.separator + "media" + File.separator + "texturepacks" + File.separator + string6 + "2x.pack");
																											if (file2.isFile()) {
																												DebugLog.Mod.printf("2x version of %s.pack found.\n", string6);
																												string6 = string6 + "2x";
																											} else {
																												DebugLog.Mod.printf("2x version of %s.pack not found.\n", string6);
																											}
																										}
																									}

																									mod.addPack(string6, int2);
																								} else if (!string5.contains("tiledef=")) {
																									if (string5.startsWith("versionMax=")) {
																										string6 = string5.replace("versionMax=", "").trim();
																										if (!string6.isEmpty()) {
																											try {
																												mod.versionMax = GameVersion.parse(string6);
																											} catch (Exception exception) {
																												DebugLog.Mod.error("invalid versionMax: " + exception.getMessage());
																												object = null;
																												break label271;
																											}
																										}
																									} else if (string5.startsWith("versionMin=")) {
																										string6 = string5.replace("versionMin=", "").trim();
																										if (!string6.isEmpty()) {
																											try {
																												mod.versionMin = GameVersion.parse(string6);
																											} catch (Exception exception2) {
																												DebugLog.Mod.error("invalid versionMin: " + exception2.getMessage());
																												object = null;
																												break label269;
																											}
																										}
																									}
																								} else {
																									String[] stringArray = string5.replace("tiledef=", "").trim().split("\\s+");
																									if (stringArray.length == 2) {
																										string4 = stringArray[0];
																										try {
																											int1 = Integer.parseInt(stringArray[1]);
																										} catch (NumberFormatException numberFormatException) {
																											DebugLog.Mod.error("tiledef= line requires file name and file number");
																											string3 = null;
																											break label273;
																										}

																										byte byte2 = 100;
																										boolean boolean1 = true;
																										short short1 = 16382;
																										if (int1 >= byte2 && int1 <= short1) {
																											mod.addTileDef(string4, int1);
																											continue;
																										}

																										DebugLog.Mod.error("tiledef=%s %d file number must be from %d to %d", string4, int1, Integer.valueOf(byte2), Integer.valueOf(short1));
																										object2 = null;
																										break label272;
																									}

																									DebugLog.Mod.error("tiledef= line requires file name and file number");
																									string4 = null;
																									break label264;
																								}
																							}
																						} else {
																							String string8 = mod.desc;
																							mod.desc = string8 + string5.replace("description=", "");
																						}
																					}
																				}

																				if (mod.getUrl() == null) {
																					mod.setUrl("");
																				}

																				mod.bValid = true;
																				mod2 = mod;
																				break label268;
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
																		break label300;
																	}

																	bufferedReader.close();
																	break label295;
																}

																bufferedReader.close();
																break label296;
															}

															bufferedReader.close();
															break label301;
														}

														bufferedReader.close();
														break label297;
													}

													bufferedReader.close();
													break label298;
												}

												bufferedReader.close();
												break label299;
											}
										} catch (Throwable throwable3) {
											if (inputStreamReader != null) {
												try {
													inputStreamReader.close();
												} catch (Throwable throwable4) {
													throwable3.addSuppressed(throwable4);
												}
											}

											throw throwable3;
										}

										if (inputStreamReader != null) {
											inputStreamReader.close();
										}

										return string4;
									}

									if (inputStreamReader != null) {
										inputStreamReader.close();
									}

									return string4;
								}

								if (inputStreamReader != null) {
									inputStreamReader.close();
								}

								return string3;
							}

							if (inputStreamReader != null) {
								inputStreamReader.close();
							}

							return (ChooseGameInfo.Mod)object2;
						}

						if (inputStreamReader != null) {
							inputStreamReader.close();
						}

						return (ChooseGameInfo.Mod)object;
					}

					if (inputStreamReader != null) {
						inputStreamReader.close();
					}

					return (ChooseGameInfo.Mod)object;
				}

				if (inputStreamReader != null) {
					inputStreamReader.close();
				}

				return mod2;
			} catch (Exception exception3) {
				ExceptionLogger.logException(exception3);
			}
		}

		return null;
	}

	public static final class Map {
		private String dir;
		private Texture thumb;
		private String title;
		private ArrayList lotsDir;
		private String desc;
		private boolean bFixed2x;

		public String getDirectory() {
			return this.dir;
		}

		public void setDirectory(String string) {
			this.dir = string;
		}

		public Texture getThumbnail() {
			return this.thumb;
		}

		public void setThumbnail(Texture texture) {
			this.thumb = texture;
		}

		public String getTitle() {
			return this.title;
		}

		public void setTitle(String string) {
			this.title = string;
		}

		public ArrayList getLotDirectories() {
			return this.lotsDir;
		}

		public String getDescription() {
			return this.desc;
		}

		public void setDescription(String string) {
			this.desc = string;
		}

		public boolean isFixed2x() {
			return this.bFixed2x;
		}

		public void setFixed2x(boolean boolean1) {
			this.bFixed2x = boolean1;
		}
	}

	public static final class Mod {
		public String dir;
		public final File baseFile;
		public final File mediaFile;
		public final File actionGroupsFile;
		public final File animSetsFile;
		public final File animsXFile;
		private final ArrayList posters = new ArrayList();
		public Texture tex;
		private ArrayList require;
		private String name = "Unnamed Mod";
		private String desc = "";
		private String id;
		private String url;
		private String workshopID;
		private boolean bAvailableDone = false;
		private boolean available = true;
		private GameVersion versionMin;
		private GameVersion versionMax;
		private final ArrayList packs = new ArrayList();
		private final ArrayList tileDefs = new ArrayList();
		private boolean bRead = false;
		private boolean bValid = false;

		public Mod(String string) {
			this.dir = string;
			File file = (new File(string)).getAbsoluteFile();
			try {
				file = file.getCanonicalFile();
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}

			this.baseFile = file;
			this.mediaFile = new File(file, "media");
			this.actionGroupsFile = new File(this.mediaFile, "actiongroups");
			this.animSetsFile = new File(this.mediaFile, "AnimSets");
			this.animsXFile = new File(this.mediaFile, "anims_X");
			File file2 = file.getParentFile();
			if (file2 != null) {
				file2 = file2.getParentFile();
				if (file2 != null) {
					this.workshopID = SteamWorkshop.instance.getIDFromItemInstallFolder(file2.getAbsolutePath());
				}
			}
		}

		public Texture getTexture() {
			if (this.tex == null) {
				String string = this.posters.isEmpty() ? null : (String)this.posters.get(0);
				if (!StringUtils.isNullOrWhitespace(string)) {
					this.tex = Texture.getSharedTexture(string);
				}

				if (this.tex == null || this.tex.isFailure()) {
					if (Core.bDebug && this.tex == null) {
						String string2 = string == null ? this.id : string;
						DebugLog.Mod.println("failed to load poster " + string2);
					}

					this.tex = Texture.getWhite();
				}
			}

			return this.tex;
		}

		public void setTexture(Texture texture) {
			this.tex = texture;
		}

		public int getPosterCount() {
			return this.posters.size();
		}

		public String getPoster(int int1) {
			return int1 >= 0 && int1 < this.posters.size() ? (String)this.posters.get(int1) : null;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String string) {
			this.name = string;
		}

		public String getDir() {
			return this.dir;
		}

		public String getDescription() {
			return this.desc;
		}

		public ArrayList getRequire() {
			return this.require;
		}

		public void setRequire(ArrayList arrayList) {
			this.require = arrayList;
		}

		public String getId() {
			return this.id;
		}

		public void setId(String string) {
			this.id = string;
		}

		public boolean isAvailable() {
			if (this.bAvailableDone) {
				return this.available;
			} else {
				this.bAvailableDone = true;
				if (!this.isAvailableSelf()) {
					this.available = false;
					return false;
				} else {
					ChooseGameInfo.tempStrings.clear();
					ChooseGameInfo.tempStrings.add(this.getId());
					if (!this.isAvailableRequired(ChooseGameInfo.tempStrings)) {
						this.available = false;
						return false;
					} else {
						this.available = true;
						return true;
					}
				}
			}
		}

		private boolean isAvailableSelf() {
			GameVersion gameVersion = Core.getInstance().getGameVersion();
			if (this.versionMin != null && this.versionMin.isGreaterThan(gameVersion)) {
				return false;
			} else {
				return this.versionMax == null || !this.versionMax.isLessThan(gameVersion);
			}
		}

		private boolean isAvailableRequired(ArrayList arrayList) {
			if (this.require != null && !this.require.isEmpty()) {
				for (int int1 = 0; int1 < this.require.size(); ++int1) {
					String string = ((String)this.require.get(int1)).trim();
					if (!arrayList.contains(string)) {
						arrayList.add(string);
						ChooseGameInfo.Mod mod = ChooseGameInfo.getModDetails(string);
						if (mod == null) {
							return false;
						}

						if (!mod.isAvailableSelf()) {
							return false;
						}

						if (!mod.isAvailableRequired(arrayList)) {
							return false;
						}
					}
				}

				return true;
			} else {
				return true;
			}
		}

		@Deprecated
		public void setAvailable(boolean boolean1) {
		}

		public String getUrl() {
			return this.url == null ? "" : this.url;
		}

		public void setUrl(String string) {
			if (string.startsWith("http://theindiestone.com") || string.startsWith("http://www.theindiestone.com") || string.startsWith("http://pz-mods.net") || string.startsWith("http://www.pz-mods.net")) {
				this.url = string;
			}
		}

		public GameVersion getVersionMin() {
			return this.versionMin;
		}

		public GameVersion getVersionMax() {
			return this.versionMax;
		}

		public void addPack(String string, int int1) {
			this.packs.add(new ChooseGameInfo.PackFile(string, int1));
		}

		public void addTileDef(String string, int int1) {
			this.tileDefs.add(new ChooseGameInfo.TileDef(string, int1));
		}

		public ArrayList getPacks() {
			return this.packs;
		}

		public ArrayList getTileDefs() {
			return this.tileDefs;
		}

		public String getWorkshopID() {
			return this.workshopID;
		}
	}

	public static final class TileDef {
		public String name;
		public int fileNumber;

		public TileDef(String string, int int1) {
			this.name = string;
			this.fileNumber = int1;
		}
	}

	public static final class PackFile {
		public final String name;
		public final int flags;

		public PackFile(String string, int int1) {
			this.name = string;
			this.flags = int1;
		}
	}

	public static final class SpawnOrigin {
		public int x;
		public int y;
		public int w;
		public int h;

		public SpawnOrigin(int int1, int int2, int int3, int int4) {
			this.x = int1;
			this.y = int2;
			this.w = int3;
			this.h = int4;
		}
	}
}
