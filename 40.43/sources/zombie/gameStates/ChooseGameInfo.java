package zombie.gameStates;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.IndieFileLoader;
import zombie.core.Translator;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.core.znet.SteamWorkshop;
import zombie.scripting.ScriptManager;


public class ChooseGameInfo extends GameState {
	Stack ModDetails = new Stack();
	Stack Mods = new Stack();
	Stack Stories = new Stack();
	Stack StoryDetails = new Stack();
	public int SelectedStory = 0;
	static HashMap Maps = new HashMap();

	public static void Reset() {
		Maps.clear();
	}

	public ChooseGameInfo.Mod getModDetails(String string) {
		String string2 = ZomboidFileSystem.instance.getModDir(string);
		if (string2 == null) {
			ArrayList arrayList = new ArrayList();
			ZomboidFileSystem.instance.getAllModFolders(arrayList);
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				File file = new File((String)arrayList.get(int1));
				string2 = ZomboidFileSystem.instance.searchForModInfo(file, string);
				if (string2 != null) {
					break;
				}
			}
		}

		return this.readModInfo(string2);
	}

	public ChooseGameInfo.Mod readModInfo(String string) {
		if (string != null) {
			String string2 = string + File.separator + "mod.info";
			File file = new File(string2);
			if (!file.exists()) {
				System.out.println("MOD: can\'t find \"" + string2 + "\"");
				return null;
			} else {
				ChooseGameInfo.Mod mod = new ChooseGameInfo.Mod(string);
				mod.setId(file.getParentFile().getName());
				InputStreamReader inputStreamReader = null;
				try {
					inputStreamReader = IndieFileLoader.getStreamReader(string2);
				} catch (FileNotFoundException fileNotFoundException) {
					fileNotFoundException.printStackTrace();
				}

				String string3 = null;
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				while (true) {
					try {
						while ((string3 = bufferedReader.readLine()) != null) {
							if (string3.contains("name=")) {
								mod.name = string3.replace("name=", "");
							} else if (string3.contains("poster=")) {
								Texture texture = Texture.getSharedTexture(string + File.separator + string3.replace("poster=", ""));
								if (Core.bDebug && texture == null) {
									System.out.println("MOD: failed to load poster " + string + File.separator + string3.replace("poster=", ""));
								}

								mod.tex = texture;
							} else if (string3.contains("description=")) {
								mod.desc = string3.replace("description=", "");
							} else if (string3.contains("require=")) {
								mod.setRequire(new ArrayList(Arrays.asList(string3.replace("require=", "").split(","))));
							} else if (!string3.contains("id=")) {
								if (string3.contains("url=")) {
									mod.setUrl(string3.replace("url=", ""));
								} else {
									File file2;
									if (!string3.contains("pack=")) {
										if (string3.contains("tiledef=")) {
											String[] stringArray = string3.replace("tiledef=", "").trim().split("\\s+");
											if (stringArray.length != 2) {
												System.out.println("MOD: tiledef= line requires file name and file number");
												file2 = null;
												return file2;
											}

											String string4 = stringArray[0];
											int int1;
											try {
												int1 = Integer.parseInt(stringArray[1]);
											} catch (NumberFormatException numberFormatException) {
												System.out.println("MOD: tiledef= line requires file name and file number");
												Object object = null;
												return (ChooseGameInfo.Mod)object;
											}

											if (int1 < 100 || int1 > 1000) {
												System.out.println("MOD: tiledef= file number must be from 100 to 1000");
											}

											mod.addTileDef(string4, int1);
										}
									} else {
										String string5 = string3.replace("pack=", "").trim();
										if (string5.isEmpty()) {
											System.out.println("MOD: pack= line requires a file name");
											file2 = null;
											return file2;
										}

										if (Core.TileScale == 2) {
											System.out.println("MOD: Looking for 2x texture packs");
											file2 = new File(string + File.separator + "media" + File.separator + "texturepacks" + File.separator + string5 + "2x.pack");
											if (file2.isFile()) {
												string5 = string5 + "2x";
											} else {
												System.out.println("MOD: 2x version of " + string5 + " not found. Loading default.");
											}
										}

										mod.addPack(string5);
									}
								}
							} else {
								mod.setId(string3.replace("id=", ""));
							}
						}

						if (mod.getTexture() == null) {
							mod.setTexture(Texture.getSharedTexture("media/ui/white.png"));
						}

						if (mod.getUrl() == null) {
							mod.setUrl("");
						}

						ChooseGameInfo.Mod mod2 = mod;
						return mod2;
					} catch (IOException ioException) {
						Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, ioException);
						return null;
					} finally {
						try {
							bufferedReader.close();
						} catch (Exception exception) {
						}
					}
				}
			}
		} else {
			return null;
		}
	}

	public void getStoryDetails(ChooseGameInfo.Story story, String string) throws FileNotFoundException {
		InputStreamReader inputStreamReader = IndieFileLoader.getStreamReader("media/stories/" + string + "/story.info");
		String string2 = null;
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		try {
			while ((string2 = bufferedReader.readLine()) != null) {
				String string3;
				if (string2.contains("name=")) {
					string3 = string2.replace("name=", "");
					story.name = string3;
				} else if (string2.contains("poster=")) {
					string3 = string2.replace("poster=", "");
					Texture texture = Texture.getSharedTexture("media/stories/" + string + "/" + string3);
					story.tex = texture;
				} else if (string2.contains("description=")) {
					string3 = string2.replace("description=", "");
					story.desc = string3;
				} else if (string2.contains("map=")) {
					string3 = string2.replace("map=", "");
					story.map = string3;
				}
			}
		} catch (IOException ioException) {
			Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, ioException);
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

								map.desc = map.desc + string2.replace("description=", "");
							} else if (string2.startsWith("fixed2x=")) {
								map.bFixed2x = Boolean.parseBoolean(string2.replace("fixed2x=", "").trim());
							}
						}
					} catch (IOException ioException) {
						Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, ioException);
					}

					bufferedReader.close();
					map.thumb = Texture.getSharedTexture(map.dir + "/thumb.png");
					if (Translator.getLanguage() != Translator.getDefaultLanguage()) {
						file = new File(ZomboidFileSystem.instance.getString("media/lua/shared/Translate/" + Translator.getLanguage().toString() + "/" + string + "/description.txt"));
						if (file != null && file.exists()) {
							map.desc = "";
							bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName(Translator.getLanguage().charset())));
							String string3 = null;
							while ((string3 = bufferedReader.readLine()) != null) {
								map.desc = map.desc + string3;
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
					return null;
				}

				Maps.put(string, map);
				return map;
			}
		}
	}

	public void enter() {
		this.getStoryList();
	}

	public void getStoryList() {
		TextureID.UseFiltering = true;
		Texture.getSharedTexture("media/ui/blank.png");
		this.StoryDetails.clear();
		try {
			this.Stories = ScriptManager.instance.getStoryList();
		} catch (IOException ioException) {
			Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, ioException);
		} catch (URISyntaxException uRISyntaxException) {
			Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, uRISyntaxException);
		}

		for (int int1 = 0; int1 < this.Stories.size(); ++int1) {
			String string = (String)this.Stories.get(int1);
			if (!string.contains("Sandbox")) {
				Core.storyDirectory = "mods/";
				ChooseGameInfo.Story story = new ChooseGameInfo.Story(string);
				try {
					this.getStoryDetails(story, string);
				} catch (FileNotFoundException fileNotFoundException) {
					Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, fileNotFoundException);
				}

				this.StoryDetails.add(story);
			}
		}

		TextureID.UseFiltering = false;
	}

	public static void DrawTexture(Texture texture, int int1, int int2, int int3, int int4, float float1) {
		texture.render(int1, int2, int3, int4, 1.0F, 1.0F, 1.0F, float1);
	}

	public static class Map {
		private String dir;
		private Texture thumb;
		private String title;
		private ArrayList lotsDir;
		private String desc;
		private boolean bFixed2x;

		public void setDirectory(String string) {
			this.dir = string;
		}

		public String getDirectory() {
			return this.dir;
		}

		public void setThumbnail(Texture texture) {
			this.thumb = texture;
		}

		public Texture getThumbnail() {
			return this.thumb;
		}

		public void setTitle(String string) {
			this.title = string;
		}

		public String getTitle() {
			return this.title;
		}

		public ArrayList getLotDirectories() {
			return this.lotsDir;
		}

		public void setDescription(String string) {
			this.desc = string;
		}

		public String getDescription() {
			return this.desc;
		}

		public void setFixed2x(boolean boolean1) {
			this.bFixed2x = boolean1;
		}

		public boolean isFixed2x() {
			return this.bFixed2x;
		}
	}

	public class Mod {
		private ArrayList require;
		public String dir;
		public Texture tex;
		private String name = "Unnamed Mod";
		private String desc = "An adventure by someone or other.";
		private String id;
		private String url;
		private String workshopID;
		private boolean available = true;
		private ArrayList packs = new ArrayList();
		private ArrayList tileDefs = new ArrayList();

		public Mod(String string) {
			this.dir = string;
			File file = new File(string);
			file = file.getParentFile();
			if (file != null) {
				file = file.getParentFile();
				if (file != null) {
					this.workshopID = SteamWorkshop.instance.getIDFromItemInstallFolder(file.getAbsolutePath());
				}
			}
		}

		public Texture getTexture() {
			return this.tex;
		}

		public void setTexture(Texture texture) {
			this.tex = texture;
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
			return this.available;
		}

		public void setAvailable(boolean boolean1) {
			this.available = boolean1;
		}

		public String getUrl() {
			return this.url == null ? "" : this.url;
		}

		public void setUrl(String string) {
			if (string.startsWith("http://theindiestone.com") || string.startsWith("http://www.theindiestone.com") || string.startsWith("http://pz-mods.net") || string.startsWith("http://www.pz-mods.net")) {
				this.url = string;
			}
		}

		public void addPack(String string) {
			this.packs.add(string);
		}

		public void addTileDef(String string, int int1) {
			this.tileDefs.add(ChooseGameInfo.this.new TileDef(string, int1));
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

	public class TileDef {
		public String name;
		public int fileNumber;

		public TileDef(String string, int int1) {
			this.name = string;
			this.fileNumber = int1;
		}
	}

	public class Story {
		public String dir;
		public Texture tex;
		private String name = "Unnamed Story";
		private String desc = "An adventure by someone or other.";
		private String map = "Muldraugh, KY";

		public Story(String string) {
			this.tex = this.tex;
			this.dir = string;
		}

		public Texture getTexture() {
			return this.tex;
		}

		public String getName() {
			return this.name;
		}

		public String getDescription() {
			return this.desc;
		}

		public String getMap() {
			return this.map;
		}
	}
}
