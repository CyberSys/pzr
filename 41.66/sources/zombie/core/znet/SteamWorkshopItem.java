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
import zombie.ZomboidFileSystem;
import zombie.core.logger.ExceptionLogger;
import zombie.core.textures.PNGDecoder;


public class SteamWorkshopItem {
	private String workshopFolder;
	private String PublishedFileId;
	private String title = "";
	private String description = "";
	private String visibility = "public";
	private final ArrayList tags = new ArrayList();
	private String changeNote = "";
	private boolean bHasMod;
	private boolean bHasMap;
	private final ArrayList modIDs = new ArrayList();
	private final ArrayList mapFolders = new ArrayList();
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
		
		case 3: 
			this.visibility = "unlisted";
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
		} else if ("private".equals(this.visibility)) {
			return 2;
		} else {
			return "unlisted".equals(this.visibility) ? 3 : 0;
		}
	}

	public void setTags(ArrayList arrayList) {
		this.tags.clear();
		this.tags.addAll(arrayList);
	}

	public static ArrayList getAllowedTags() {
		ArrayList arrayList = new ArrayList();
		File file = ZomboidFileSystem.instance.getMediaFile("WorkshopTags.txt");
		try {
			FileReader fileReader = new FileReader(file);
			try {
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String string;
				try {
					while ((string = bufferedReader.readLine()) != null) {
						string = string.trim();
						if (!string.isEmpty()) {
							arrayList.add(string);
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
					fileReader.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileReader.close();
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}

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
			String string;
			label92: {
				label93: {
					try {
						Iterator iterator = directoryStream.iterator();
						while (iterator.hasNext()) {
							Path path2 = (Path)iterator.next();
							String string2;
							if (Files.isDirectory(path2, new LinkOption[0])) {
								string2 = this.validateFileTypes(path2);
								if (string2 != null) {
									string = string2;
									break label92;
								}
							} else {
								string2 = path2.getFileName().toString();
								if (!string2.equalsIgnoreCase("pyramid.zip") && (string2.endsWith(".exe") || string2.endsWith(".dll") || string2.endsWith(".bat") || string2.endsWith(".app") || string2.endsWith(".dylib") || string2.endsWith(".sh") || string2.endsWith(".so") || string2.endsWith(".zip"))) {
									string = "FileTypeNotAllowed";
									break label93;
								}
							}
						}
					} catch (Throwable throwable) {
						if (directoryStream != null) {
							try {
								directoryStream.close();
							} catch (Throwable throwable2) {
								throwable.addSuppressed(throwable2);
							}
						}

						throw throwable;
					}

					if (directoryStream != null) {
						directoryStream.close();
					}

					return null;
				}

				if (directoryStream != null) {
					directoryStream.close();
				}

				return string;
			}

			if (directoryStream != null) {
				directoryStream.close();
			}

			return string;
		} catch (Exception exception) {
			exception.printStackTrace();
			return "IOError";
		}
	}

	private String validateModDotInfo(Path path) {
		String string = null;
		try {
			FileReader fileReader = new FileReader(path.toFile());
			try {
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String string2;
				try {
					while ((string2 = bufferedReader.readLine()) != null) {
						if (string2.startsWith("id=")) {
							string = string2.replace("id=", "").trim();
							break;
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
					fileReader.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileReader.close();
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
			label68: {
				DirectoryStream directoryStream;
				String string;
				label69: {
					directoryStream = Files.newDirectoryStream(path);
					try {
						Iterator iterator = directoryStream.iterator();
						while (iterator.hasNext()) {
							Path path2 = (Path)iterator.next();
							if (!Files.isDirectory(path2, new LinkOption[0]) && "map.info".equals(path2.getFileName().toString())) {
								String string2 = this.validateMapDotInfo(path2);
								if (string2 != null) {
									string = string2;
									break label69;
								}

								boolean1 = true;
							}
						}
					} catch (Throwable throwable) {
						if (directoryStream != null) {
							try {
								directoryStream.close();
							} catch (Throwable throwable2) {
								throwable.addSuppressed(throwable2);
							}
						}

						throw throwable;
					}

					if (directoryStream != null) {
						directoryStream.close();
					}

					break label68;
				}

				if (directoryStream != null) {
					directoryStream.close();
				}

				return string;
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
			label64: {
				DirectoryStream directoryStream;
				String string;
				label65: {
					directoryStream = Files.newDirectoryStream(path);
					try {
						Iterator iterator = directoryStream.iterator();
						while (iterator.hasNext()) {
							Path path2 = (Path)iterator.next();
							if (Files.isDirectory(path2, new LinkOption[0])) {
								String string2 = this.validateMapFolder(path2);
								if (string2 != null) {
									string = string2;
									break label65;
								}

								boolean1 = true;
							}
						}
					} catch (Throwable throwable) {
						if (directoryStream != null) {
							try {
								directoryStream.close();
							} catch (Throwable throwable2) {
								throwable.addSuppressed(throwable2);
							}
						}

						throw throwable;
					}

					if (directoryStream != null) {
						directoryStream.close();
					}

					break label64;
				}

				if (directoryStream != null) {
					directoryStream.close();
				}

				return string;
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
			String string;
			label55: {
				try {
					Iterator iterator = directoryStream.iterator();
					while (iterator.hasNext()) {
						Path path2 = (Path)iterator.next();
						if (Files.isDirectory(path2, new LinkOption[0]) && "maps".equals(path2.getFileName().toString())) {
							String string2 = this.validateMapsFolder(path2);
							if (string2 != null) {
								string = string2;
								break label55;
							}
						}
					}
				} catch (Throwable throwable) {
					if (directoryStream != null) {
						try {
							directoryStream.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}
					}

					throw throwable;
				}

				if (directoryStream != null) {
					directoryStream.close();
				}

				return null;
			}

			if (directoryStream != null) {
				directoryStream.close();
			}

			return string;
		} catch (Exception exception) {
			exception.printStackTrace();
			return "IOError";
		}
	}

	private String validateModFolder(Path path) {
		boolean boolean1 = false;
		try {
			DirectoryStream directoryStream;
			String string;
			label90: {
				label83: {
					directoryStream = Files.newDirectoryStream(path);
					try {
						Iterator iterator = directoryStream.iterator();
						while (true) {
							if (!iterator.hasNext()) {
								break label83;
							}

							Path path2 = (Path)iterator.next();
							String string2;
							if (Files.isDirectory(path2, new LinkOption[0])) {
								if ("media".equals(path2.getFileName().toString())) {
									string2 = this.validateMediaFolder(path2);
									if (string2 != null) {
										string = string2;
										break;
									}
								}
							} else if ("mod.info".equals(path2.getFileName().toString())) {
								string2 = this.validateModDotInfo(path2);
								if (string2 != null) {
									string = string2;
									break label90;
								}

								boolean1 = true;
							}
						}
					} catch (Throwable throwable) {
						if (directoryStream != null) {
							try {
								directoryStream.close();
							} catch (Throwable throwable2) {
								throwable.addSuppressed(throwable2);
							}
						}

						throw throwable;
					}

					if (directoryStream != null) {
						directoryStream.close();
					}

					return string;
				}

				if (directoryStream != null) {
					directoryStream.close();
				}

				return !boolean1 ? "MissingModDotInfo" : null;
			}

			if (directoryStream != null) {
				directoryStream.close();
			}

			return string;
		} catch (Exception exception) {
			exception.printStackTrace();
			return "IOError";
		}
	}

	private String validateModsFolder(Path path) {
		boolean boolean1 = false;
		try {
			DirectoryStream directoryStream;
			label75: {
				String string;
				label76: {
					directoryStream = Files.newDirectoryStream(path);
					String string2;
					try {
						Iterator iterator = directoryStream.iterator();
						while (true) {
							if (!iterator.hasNext()) {
								break label75;
							}

							Path path2 = (Path)iterator.next();
							if (!Files.isDirectory(path2, new LinkOption[0])) {
								string2 = "FileNotAllowedInMods";
								break;
							}

							string2 = this.validateModFolder(path2);
							if (string2 != null) {
								string = string2;
								break label76;
							}

							boolean1 = true;
						}
					} catch (Throwable throwable) {
						if (directoryStream != null) {
							try {
								directoryStream.close();
							} catch (Throwable throwable2) {
								throwable.addSuppressed(throwable2);
							}
						}

						throw throwable;
					}

					if (directoryStream != null) {
						directoryStream.close();
					}

					return string2;
				}

				if (directoryStream != null) {
					directoryStream.close();
				}

				return string;
			}

			if (directoryStream != null) {
				directoryStream.close();
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
					label60: {
						String string;
						try {
							BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
							label56: {
								try {
									PNGDecoder pNGDecoder = new PNGDecoder(bufferedInputStream, false);
									if (pNGDecoder.getWidth() != 256 || pNGDecoder.getHeight() != 256) {
										string = "PreviewDimensions";
										break label56;
									}
								} catch (Throwable throwable) {
									try {
										bufferedInputStream.close();
									} catch (Throwable throwable2) {
										throwable.addSuppressed(throwable2);
									}

									throw throwable;
								}

								bufferedInputStream.close();
								break label60;
							}

							bufferedInputStream.close();
						} catch (Throwable throwable3) {
							try {
								fileInputStream.close();
							} catch (Throwable throwable4) {
								throwable3.addSuppressed(throwable4);
							}

							throw throwable3;
						}

						fileInputStream.close();
						return string;
					}

					fileInputStream.close();
					return null;
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
						DirectoryStream directoryStream;
						label143: {
							String string2;
							label144: {
								label145: {
									label146: {
										label147: {
											directoryStream = Files.newDirectoryStream(path);
											try {
												Iterator iterator = directoryStream.iterator();
												while (true) {
													if (!iterator.hasNext()) {
														break label143;
													}

													Path path3 = (Path)iterator.next();
													if (!Files.isDirectory(path3, new LinkOption[0])) {
														string2 = "FileNotAllowedInContents";
														break;
													}

													if ("buildings".equals(path3.getFileName().toString())) {
														string = this.validateBuildingsFolder(path3);
														if (string != null) {
															string2 = string;
															break label144;
														}
													} else if ("creative".equals(path3.getFileName().toString())) {
														string = this.validateCreativeFolder(path3);
														if (string != null) {
															string2 = string;
															break label145;
														}
													} else {
														if (!"mods".equals(path3.getFileName().toString())) {
															string2 = "FolderNotAllowedInContents";
															break label147;
														}

														string = this.validateModsFolder(path3);
														if (string != null) {
															string2 = string;
															break label146;
														}
													}

													boolean1 = true;
												}
											} catch (Throwable throwable) {
												if (directoryStream != null) {
													try {
														directoryStream.close();
													} catch (Throwable throwable2) {
														throwable.addSuppressed(throwable2);
													}
												}

												throw throwable;
											}

											if (directoryStream != null) {
												directoryStream.close();
											}

											return string2;
										}

										if (directoryStream != null) {
											directoryStream.close();
										}

										return string2;
									}

									if (directoryStream != null) {
										directoryStream.close();
									}

									return string2;
								}

								if (directoryStream != null) {
									directoryStream.close();
								}

								return string2;
							}

							if (directoryStream != null) {
								directoryStream.close();
							}

							return string2;
						}

						if (directoryStream != null) {
							directoryStream.close();
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						return "IOError";
					}

					return !boolean1 ? "EmptyContentsFolder" : this.validateFileTypes(path);
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
				boolean boolean1;
				try {
					BufferedReader bufferedReader = new BufferedReader(fileReader);
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

									String string4 = this.description;
									this.description = string4 + string2.replace("description=", "");
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
						fileReader.close();
					} catch (Throwable throwable4) {
						throwable3.addSuppressed(throwable4);
					}

					throw throwable3;
				}

				fileReader.close();
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
			String string2 = this.PublishedFileId == null ? "" : this.PublishedFileId;
			bufferedWriter.write("id=" + string2);
			bufferedWriter.newLine();
			bufferedWriter.write("title=" + this.title);
			bufferedWriter.newLine();
			String[] stringArray = this.description.split("\n");
			int int1 = stringArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				String string3 = stringArray[int2];
				bufferedWriter.write("description=" + string3);
				bufferedWriter.newLine();
			}

			String string4 = "";
			for (int1 = 0; int1 < this.tags.size(); ++int1) {
				string4 = string4 + (String)this.tags.get(int1);
				if (int1 < this.tags.size() - 1) {
					string4 = string4 + ";";
				}
			}

			bufferedWriter.write("tags=" + string4);
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
		public boolean not(long long1) {
			return ((long)this.value & long1) == 0L;
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
		private static SteamWorkshopItem.ItemState[] $values() {
			return new SteamWorkshopItem.ItemState[]{None, Subscribed, LegacyItem, Installed, NeedsUpdate, Downloading, DownloadPending};
		}
	}
}
