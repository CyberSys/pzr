package zombie.core.znet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import zombie.core.textures.PNGDecoder;


public class SteamWorkshopItem {
	private String workshopFolder;
	private String PublishedFileId;
	private String title = "";
	private String description = "";
	private String visibility = "public";
	private ArrayList tags = new ArrayList();
	private String changeNote = "";
	private boolean bHasMod;
	private boolean bHasMap;
	private ArrayList modIDs = new ArrayList();
	private ArrayList mapFolders = new ArrayList();
	private static final int VERSION1 = 1;
	private static final int LATEST_VERSION = 1;

	public SteamWorkshopItem(String string) {
		this.workshopFolder = string;
	}

	public String getContentFolder() {
		return this.workshopFolder + File.separator + "Contents";
	}

	public String getFolderName() {
		return (new File(this.workshopFolder)).getName();
	}

	public void setID(String string) {
		if (string != null && !SteamUtils.isValidSteamID(string)) {
			string = null;
		}

		this.PublishedFileId = string;
	}

	public String getID() {
		return this.PublishedFileId;
	}

	public void setTitle(String string) {
		if (string == null) {
			string = "";
		}

		this.title = string;
	}

	public String getTitle() {
		return this.title;
	}

	public void setDescription(String string) {
		if (string == null) {
			string = "";
		}

		this.description = string;
	}

	public String getDescription() {
		return this.description;
	}

	public void setVisibility(String string) {
		this.visibility = string;
	}

	public String getVisibility() {
		return this.visibility;
	}

	public void setVisibilityInteger(int int1) {
		switch (int1) {
		case 0: 
			this.visibility = "public";
			break;
		
		case 1: 
			this.visibility = "friendsOnly";
			break;
		
		case 2: 
			this.visibility = "private";
			break;
		
		default: 
			this.visibility = "public";
		
		}
	}

	public int getVisibilityInteger() {
		if ("public".equals(this.visibility)) {
			return 0;
		} else if ("friendsOnly".equals(this.visibility)) {
			return 1;
		} else {
			return "private".equals(this.visibility) ? 2 : 0;
		}
	}

	public void setTags(ArrayList arrayList) {
		this.tags.clear();
		this.tags.addAll(arrayList);
	}

	public static ArrayList getAllowedTags() {
		ArrayList arrayList = new ArrayList();
		return arrayList;
	}

	public ArrayList getTags() {
		return this.tags;
	}

	public String getSubmitDescription() {
		String string = this.getDescription();
		if (!string.isEmpty()) {
			string = string + "\n\n";
		}

		string = string + "Workshop ID: " + this.getID();
		int int1;
		for (int1 = 0; int1 < this.modIDs.size(); ++int1) {
			string = string + "\nMod ID: " + (String)this.modIDs.get(int1);
		}

		for (int1 = 0; int1 < this.mapFolders.size(); ++int1) {
			string = string + "\nMap Folder: " + (String)this.mapFolders.get(int1);
		}

		return string;
	}

	public String[] getSubmitTags() {
		ArrayList arrayList = new ArrayList();
		arrayList.addAll(this.tags);
		if (this.bHasMod) {
			arrayList.add("Mod");
		}

		if (this.bHasMap) {
			arrayList.add("Map");
		}

		return (String[])arrayList.toArray(new String[arrayList.size()]);
	}

	public String getPreviewImage() {
		return this.workshopFolder + File.separator + "preview.png";
	}

	public void setChangeNote(String string) {
		if (string == null) {
			string = "";
		}

		this.changeNote = string;
	}

	public String getChangeNote() {
		return this.changeNote;
	}

	public boolean create() {
		return SteamWorkshop.instance.CreateWorkshopItem(this);
	}

	public boolean submitUpdate() {
		return SteamWorkshop.instance.SubmitWorkshopItem(this);
	}

	public boolean getUpdateProgress(KahluaTable kahluaTable) {
		if (kahluaTable == null) {
			throw new NullPointerException("table is null");
		} else {
			long[] longArray = new long[2];
			if (SteamWorkshop.instance.GetItemUpdateProgress(longArray)) {
				System.out.println(longArray[0] + "/" + longArray[1]);
				kahluaTable.rawset("processed", (double)longArray[0]);
				kahluaTable.rawset("total", (double)Math.max(longArray[1], 1L));
				return true;
			} else {
				return false;
			}
		}
	}

	public int getUpdateProgressTotal() {
		return 1;
	}

	private String validateFileTypes(Path path) {
		try {
			DirectoryStream directoryStream = Files.newDirectoryStream(path);
			Throwable throwable = null;
			try {
				Iterator iterator = directoryStream.iterator();
				while (iterator.hasNext()) {
					Path path2 = (Path)iterator.next();
					String string;
					String string2;
					if (Files.isDirectory(path2, new LinkOption[0])) {
						string = this.validateFileTypes(path2);
						if (string != null) {
							string2 = string;
							return string2;
						}
					} else {
						string = path2.getFileName().toString();
						if (string.endsWith(".exe") || string.endsWith(".dll") || string.endsWith(".bat") || string.endsWith(".app") || string.endsWith(".dylib") || string.endsWith(".sh") || string.endsWith(".so") || string.endsWith(".zip")) {
							string2 = "FileTypeNotAllowed";
							return string2;
						}
					}
				}

				return null;
			} catch (Throwable throwable2) {
				throwable = throwable2;
				throw throwable2;
			} finally {
				if (directoryStream != null) {
					if (throwable != null) {
						try {
							directoryStream.close();
						} catch (Throwable throwable3) {
							throwable.addSuppressed(throwable3);
						}
					} else {
						directoryStream.close();
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return "IOError";
		}
	}

	private String validateModDotInfo(Path path) {
		String string = null;
		try {
			FileReader fileReader = new FileReader(path.toFile());
			Throwable throwable = null;
			try {
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				Throwable throwable2 = null;
				try {
					String string2;
					try {
						while ((string2 = bufferedReader.readLine()) != null) {
							if (string2.startsWith("id=")) {
								string = string2.replace("id=", "").trim();
								break;
							}
						}
					} catch (Throwable throwable3) {
						throwable2 = throwable3;
						throw throwable3;
					}
				} finally {
					if (bufferedReader != null) {
						if (throwable2 != null) {
							try {
								bufferedReader.close();
							} catch (Throwable throwable4) {
								throwable2.addSuppressed(throwable4);
							}
						} else {
							bufferedReader.close();
						}
					}
				}
			} catch (Throwable throwable5) {
				throwable = throwable5;
				throw throwable5;
			} finally {
				if (fileReader != null) {
					if (throwable != null) {
						try {
							fileReader.close();
						} catch (Throwable throwable6) {
							throwable.addSuppressed(throwable6);
						}
					} else {
						fileReader.close();
					}
				}
			}
		} catch (FileNotFoundException fileNotFoundException) {
			return "MissingModDotInfo";
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return "IOError";
		}

		if (string != null && !string.isEmpty()) {
			this.modIDs.add(string);
			return null;
		} else {
			return "InvalidModDotInfo";
		}
	}

	private String validateMapDotInfo(Path path) {
		return null;
	}

	private String validateMapFolder(Path path) {
		boolean boolean1 = false;
		try {
			DirectoryStream directoryStream = Files.newDirectoryStream(path);
			Throwable throwable = null;
			try {
				Iterator iterator = directoryStream.iterator();
				while (iterator.hasNext()) {
					Path path2 = (Path)iterator.next();
					if (!Files.isDirectory(path2, new LinkOption[0]) && "map.info".equals(path2.getFileName().toString())) {
						String string = this.validateMapDotInfo(path2);
						if (string != null) {
							String string2 = string;
							return string2;
						}

						boolean1 = true;
					}
				}
			} catch (Throwable throwable2) {
				throwable = throwable2;
				throw throwable2;
			} finally {
				if (directoryStream != null) {
					if (throwable != null) {
						try {
							directoryStream.close();
						} catch (Throwable throwable3) {
							throwable.addSuppressed(throwable3);
						}
					} else {
						directoryStream.close();
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return "IOError";
		}

		if (!boolean1) {
			return "MissingMapDotInfo";
		} else {
			this.mapFolders.add(path.getFileName().toString());
			return null;
		}
	}

	private String validateMapsFolder(Path path) {
		boolean boolean1 = false;
		try {
			DirectoryStream directoryStream = Files.newDirectoryStream(path);
			Throwable throwable = null;
			try {
				Iterator iterator = directoryStream.iterator();
				while (iterator.hasNext()) {
					Path path2 = (Path)iterator.next();
					if (Files.isDirectory(path2, new LinkOption[0])) {
						String string = this.validateMapFolder(path2);
						if (string != null) {
							String string2 = string;
							return string2;
						}

						boolean1 = true;
					}
				}
			} catch (Throwable throwable2) {
				throwable = throwable2;
				throw throwable2;
			} finally {
				if (directoryStream != null) {
					if (throwable != null) {
						try {
							directoryStream.close();
						} catch (Throwable throwable3) {
							throwable.addSuppressed(throwable3);
						}
					} else {
						directoryStream.close();
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return "IOError";
		}

		if (!boolean1) {
			return null;
		} else {
			this.bHasMap = true;
			return null;
		}
	}

	private String validateMediaFolder(Path path) {
		try {
			DirectoryStream directoryStream = Files.newDirectoryStream(path);
			Throwable throwable = null;
			try {
				Iterator iterator = directoryStream.iterator();
				while (iterator.hasNext()) {
					Path path2 = (Path)iterator.next();
					if (Files.isDirectory(path2, new LinkOption[0]) && "maps".equals(path2.getFileName().toString())) {
						String string = this.validateMapsFolder(path2);
						if (string != null) {
							String string2 = string;
							return string2;
						}
					}
				}

				return null;
			} catch (Throwable throwable2) {
				throwable = throwable2;
				throw throwable2;
			} finally {
				if (directoryStream != null) {
					if (throwable != null) {
						try {
							directoryStream.close();
						} catch (Throwable throwable3) {
							throwable.addSuppressed(throwable3);
						}
					} else {
						directoryStream.close();
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return "IOError";
		}
	}

	private String validateModFolder(Path path) {
		boolean boolean1 = false;
		try {
			DirectoryStream directoryStream = Files.newDirectoryStream(path);
			Throwable throwable = null;
			try {
				Iterator iterator = directoryStream.iterator();
				while (iterator.hasNext()) {
					Path path2 = (Path)iterator.next();
					String string;
					String string2;
					if (Files.isDirectory(path2, new LinkOption[0])) {
						if ("media".equals(path2.getFileName().toString())) {
							string = this.validateMediaFolder(path2);
							if (string != null) {
								string2 = string;
								return string2;
							}
						}
					} else if ("mod.info".equals(path2.getFileName().toString())) {
						string = this.validateModDotInfo(path2);
						if (string != null) {
							string2 = string;
							return string2;
						}

						boolean1 = true;
					}
				}

				return !boolean1 ? "MissingModDotInfo" : null;
			} catch (Throwable throwable2) {
				throwable = throwable2;
				throw throwable2;
			} finally {
				if (directoryStream != null) {
					if (throwable != null) {
						try {
							directoryStream.close();
						} catch (Throwable throwable3) {
							throwable.addSuppressed(throwable3);
						}
					} else {
						directoryStream.close();
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return "IOError";
		}
	}

	private String validateModsFolder(Path path) {
		boolean boolean1 = false;
		try {
			DirectoryStream directoryStream = Files.newDirectoryStream(path);
			Throwable throwable = null;
			try {
				for (Iterator iterator = directoryStream.iterator(); iterator.hasNext(); boolean1 = true) {
					Path path2 = (Path)iterator.next();
					String string;
					if (!Files.isDirectory(path2, new LinkOption[0])) {
						string = "FileNotAllowedInMods";
						return string;
					}

					string = this.validateModFolder(path2);
					if (string != null) {
						String string2 = string;
						return string2;
					}
				}
			} catch (Throwable throwable2) {
				throwable = throwable2;
				throw throwable2;
			} finally {
				if (directoryStream != null) {
					if (throwable != null) {
						try {
							directoryStream.close();
						} catch (Throwable throwable3) {
							throwable.addSuppressed(throwable3);
						}
					} else {
						directoryStream.close();
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return "IOError";
		}

		if (!boolean1) {
			return "EmptyModsFolder";
		} else {
			this.bHasMod = true;
			return null;
		}
	}

	private String validateBuildingsFolder(Path path) {
		return null;
	}

	private String validateCreativeFolder(Path path) {
		return null;
	}

	public String validatePreviewImage(Path path) throws IOException {
		if (Files.exists(path, new LinkOption[0]) && Files.isReadable(path) && !Files.isDirectory(path, new LinkOption[0])) {
			if (Files.size(path) > 1024000L) {
				return "PreviewFileSize";
			} else {
				try {
					FileInputStream fileInputStream = new FileInputStream(path.toFile());
					Throwable throwable = null;
					String string;
					try {
						BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
						Throwable throwable2 = null;
						try {
							PNGDecoder pNGDecoder = new PNGDecoder(bufferedInputStream, false);
							if (pNGDecoder.getWidth() == 256 && pNGDecoder.getHeight() == 256) {
								return null;
							}

							string = "PreviewDimensions";
						} catch (Throwable throwable3) {
							throwable2 = throwable3;
							throw throwable3;
						} finally {
							if (bufferedInputStream != null) {
								if (throwable2 != null) {
									try {
										bufferedInputStream.close();
									} catch (Throwable throwable4) {
										throwable2.addSuppressed(throwable4);
									}
								} else {
									bufferedInputStream.close();
								}
							}
						}
					} catch (Throwable throwable5) {
						throwable = throwable5;
						throw throwable5;
					} finally {
						if (fileInputStream != null) {
							if (throwable != null) {
								try {
									fileInputStream.close();
								} catch (Throwable throwable6) {
									throwable.addSuppressed(throwable6);
								}
							} else {
								fileInputStream.close();
							}
						}
					}

					return string;
				} catch (IOException ioException) {
					ioException.printStackTrace();
					return "PreviewFormat";
				}
			}
		} else {
			return "PreviewNotFound";
		}
	}

	public String validateContents() {
		this.bHasMod = false;
		this.bHasMap = false;
		this.modIDs.clear();
		this.mapFolders.clear();
		try {
			Path path = FileSystems.getDefault().getPath(this.getContentFolder());
			if (!Files.isDirectory(path, new LinkOption[0])) {
				return "MissingContents";
			} else {
				Path path2 = FileSystems.getDefault().getPath(this.getPreviewImage());
				String string = this.validatePreviewImage(path2);
				if (string != null) {
					return string;
				} else {
					boolean boolean1 = false;
					try {
						DirectoryStream directoryStream = Files.newDirectoryStream(path);
						Throwable throwable = null;
						try {
							for (Iterator iterator = directoryStream.iterator(); iterator.hasNext(); boolean1 = true) {
								Path path3 = (Path)iterator.next();
								String string2;
								if (!Files.isDirectory(path3, new LinkOption[0])) {
									string2 = "FileNotAllowedInContents";
									return string2;
								}

								if ("buildings".equals(path3.getFileName().toString())) {
									string = this.validateBuildingsFolder(path3);
									if (string != null) {
										string2 = string;
										return string2;
									}
								} else if ("creative".equals(path3.getFileName().toString())) {
									string = this.validateCreativeFolder(path3);
									if (string != null) {
										string2 = string;
										return string2;
									}
								} else {
									if (!"mods".equals(path3.getFileName().toString())) {
										string2 = "FolderNotAllowedInContents";
										return string2;
									}

									string = this.validateModsFolder(path3);
									if (string != null) {
										string2 = string;
										return string2;
									}
								}
							}

							return !boolean1 ? "EmptyContentsFolder" : this.validateFileTypes(path);
						} catch (Throwable throwable2) {
							throwable = throwable2;
							throw throwable2;
						} finally {
							if (directoryStream != null) {
								if (throwable != null) {
									try {
										directoryStream.close();
									} catch (Throwable throwable3) {
										throwable.addSuppressed(throwable3);
									}
								} else {
									directoryStream.close();
								}
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						return "IOError";
					}
				}
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return "IOError";
		}
	}

	public String getExtendedErrorInfo(String string) {
		if ("FolderNotAllowedInContents".equals(string)) {
			return "buildings/ creative/ mods/";
		} else {
			return "FileTypeNotAllowed".equals(string) ? "*.exe *.dll *.bat *.app *.dylib *.sh *.so *.zip" : "";
		}
	}

	public boolean readWorkshopTxt() {
		String string = this.workshopFolder + File.separator + "workshop.txt";
		if (!(new File(string)).exists()) {
			return true;
		} else {
			try {
				FileReader fileReader = new FileReader(string);
				Throwable throwable = null;
				boolean boolean1;
				try {
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					Throwable throwable2 = null;
					try {
						String string2;
						while ((string2 = bufferedReader.readLine()) != null) {
							string2 = string2.trim();
							if (!string2.isEmpty() && !string2.startsWith("#") && !string2.startsWith("//")) {
								if (string2.startsWith("id=")) {
									String string3 = string2.replace("id=", "");
									this.setID(string3);
								} else if (string2.startsWith("description=")) {
									if (!this.description.isEmpty()) {
										this.description = this.description + "\n";
									}

									this.description = this.description + string2.replace("description=", "");
								} else if (string2.startsWith("tags=")) {
									this.tags.addAll(Arrays.asList(string2.replace("tags=", "").split(";")));
								} else if (string2.startsWith("title=")) {
									this.title = string2.replace("title=", "");
								} else if (!string2.startsWith("version=") && string2.startsWith("visibility=")) {
									this.visibility = string2.replace("visibility=", "");
								}
							}
						}

						boolean1 = true;
					} catch (Throwable throwable3) {
						throwable2 = throwable3;
						throw throwable3;
					} finally {
						if (bufferedReader != null) {
							if (throwable2 != null) {
								try {
									bufferedReader.close();
								} catch (Throwable throwable4) {
									throwable2.addSuppressed(throwable4);
								}
							} else {
								bufferedReader.close();
							}
						}
					}
				} catch (Throwable throwable5) {
					throwable = throwable5;
					throw throwable5;
				} finally {
					if (fileReader != null) {
						if (throwable != null) {
							try {
								fileReader.close();
							} catch (Throwable throwable6) {
								throwable.addSuppressed(throwable6);
							}
						} else {
							fileReader.close();
						}
					}
				}

				return boolean1;
			} catch (IOException ioException) {
				ioException.printStackTrace();
				return false;
			}
		}
	}

	public boolean writeWorkshopTxt() {
		String string = this.workshopFolder + File.separator + "workshop.txt";
		File file = new File(string);
		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write("version=1");
			bufferedWriter.newLine();
			bufferedWriter.write("id=" + (this.PublishedFileId == null ? "" : this.PublishedFileId));
			bufferedWriter.newLine();
			bufferedWriter.write("title=" + this.title);
			bufferedWriter.newLine();
			String[] stringArray = this.description.split("\n");
			int int1 = stringArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				String string2 = stringArray[int2];
				bufferedWriter.write("description=" + string2);
				bufferedWriter.newLine();
			}

			String string3 = "";
			for (int1 = 0; int1 < this.tags.size(); ++int1) {
				string3 = string3 + (String)this.tags.get(int1);
				if (int1 < this.tags.size() - 1) {
					string3 = string3 + ";";
				}
			}

			bufferedWriter.write("tags=" + string3);
			bufferedWriter.newLine();
			bufferedWriter.write("visibility=" + this.visibility);
			bufferedWriter.newLine();
			bufferedWriter.close();
			return true;
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return false;
		}
	}

	public static enum ItemState {

		None,
		Subscribed,
		LegacyItem,
		Installed,
		NeedsUpdate,
		Downloading,
		DownloadPending,
		value;

		private ItemState(int int1) {
			this.value = int1;
		}
		public int getValue() {
			return this.value;
		}
		public boolean and(SteamWorkshopItem.ItemState itemState) {
			return (this.value & itemState.value) != 0;
		}
		public boolean and(long long1) {
			return ((long)this.value & long1) != 0L;
		}
		public static String toString(long long1) {
			if (long1 == (long)None.getValue()) {
				return "None";
			} else {
				StringBuilder stringBuilder = new StringBuilder();
				SteamWorkshopItem.ItemState[] itemStateArray = values();
				int int1 = itemStateArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					SteamWorkshopItem.ItemState itemState = itemStateArray[int2];
					if (itemState != None && itemState.and(long1)) {
						if (stringBuilder.length() > 0) {
							stringBuilder.append("|");
						}

						stringBuilder.append(itemState.name());
					}
				}

				return stringBuilder.toString();
			}
		}
	}
}
