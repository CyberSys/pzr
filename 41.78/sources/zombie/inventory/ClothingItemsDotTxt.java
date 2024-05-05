package zombie.inventory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.ZomboidFileSystem;
import zombie.core.logger.ExceptionLogger;
import zombie.util.StringUtils;


public final class ClothingItemsDotTxt {
	public static final ClothingItemsDotTxt instance = new ClothingItemsDotTxt();
	private final StringBuilder buf = new StringBuilder();

	private int readBlock(String string, int int1, ClothingItemsDotTxt.Block block) {
		int int2;
		for (int2 = int1; int2 < string.length(); ++int2) {
			if (string.charAt(int2) == '{') {
				ClothingItemsDotTxt.Block block2 = new ClothingItemsDotTxt.Block();
				block.children.add(block2);
				block.elements.add(block2);
				String string2 = string.substring(int1, int2).trim();
				int int3 = string2.indexOf(32);
				int int4 = string2.indexOf(9);
				int int5 = Math.max(int3, int4);
				if (int5 == -1) {
					block2.type = string2;
				} else {
					block2.type = string2.substring(0, int5);
					block2.id = string2.substring(int5).trim();
				}

				int2 = this.readBlock(string, int2 + 1, block2);
				int1 = int2;
			} else {
				if (string.charAt(int2) == '}') {
					String string3 = string.substring(int1, int2).trim();
					if (!string3.isEmpty()) {
						ClothingItemsDotTxt.Value value = new ClothingItemsDotTxt.Value();
						value.string = string.substring(int1, int2).trim();
						block.values.add(value.string);
						block.elements.add(value);
					}

					return int2 + 1;
				}

				if (string.charAt(int2) == ',') {
					ClothingItemsDotTxt.Value value2 = new ClothingItemsDotTxt.Value();
					value2.string = string.substring(int1, int2).trim();
					block.values.add(value2.string);
					block.elements.add(value2);
					int1 = int2 + 1;
				}
			}
		}

		return int2;
	}

	public void LoadFile() {
		String string = ZomboidFileSystem.instance.getString("media/scripts/clothingItems.txt");
		File file = new File(string);
		if (file.exists()) {
			try {
				FileReader fileReader = new FileReader(string);
				try {
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					try {
						this.buf.setLength(0);
						String string2;
						while ((string2 = bufferedReader.readLine()) != null) {
							this.buf.append(string2);
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
			} catch (Throwable throwable5) {
				ExceptionLogger.logException(throwable5);
				return;
			}

			int int1;
			for (int int2 = this.buf.lastIndexOf("*/"); int2 != -1; int2 = this.buf.lastIndexOf("*/", int1)) {
				int1 = this.buf.lastIndexOf("/*", int2 - 1);
				if (int1 == -1) {
					break;
				}

				int int3;
				for (int int4 = this.buf.lastIndexOf("*/", int2 - 1); int4 > int1; int4 = this.buf.lastIndexOf("*/", int3 - 2)) {
					int3 = int1;
					this.buf.substring(int1, int4 + 2);
					int1 = this.buf.lastIndexOf("/*", int1 - 2);
					if (int1 == -1) {
						break;
					}
				}

				if (int1 == -1) {
					break;
				}

				this.buf.substring(int1, int2 + 2);
				this.buf.replace(int1, int2 + 2, "");
			}

			ClothingItemsDotTxt.Block block = new ClothingItemsDotTxt.Block();
			this.readBlock(this.buf.toString(), 0, block);
			Path path = FileSystems.getDefault().getPath("media/clothing/clothingItems");
			try {
				DirectoryStream directoryStream = Files.newDirectoryStream(path);
				try {
					Iterator iterator = directoryStream.iterator();
					while (iterator.hasNext()) {
						Path path2 = (Path)iterator.next();
						if (!Files.isDirectory(path2, new LinkOption[0])) {
							String string3 = path2.getFileName().toString();
							if (string3.endsWith(".xml")) {
								String string4 = StringUtils.trimSuffix(string3, ".xml");
								System.out.println(string3 + " -> " + string4);
								this.addClothingItem(string4, (ClothingItemsDotTxt.Block)block.children.get(0));
							}
						}
					}
				} catch (Throwable throwable6) {
					if (directoryStream != null) {
						try {
							directoryStream.close();
						} catch (Throwable throwable7) {
							throwable6.addSuppressed(throwable7);
						}
					}

					throw throwable6;
				}

				if (directoryStream != null) {
					directoryStream.close();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				FileWriter fileWriter = new FileWriter(file);
				try {
					fileWriter.write(((ClothingItemsDotTxt.Block)block.children.get(0)).toString());
				} catch (Throwable throwable8) {
					try {
						fileWriter.close();
					} catch (Throwable throwable9) {
						throwable8.addSuppressed(throwable9);
					}

					throw throwable8;
				}

				fileWriter.close();
			} catch (Throwable throwable10) {
				ExceptionLogger.logException(throwable10);
			}

			System.out.println(block.children.get(0));
		}
	}

	private void addClothingItem(String string, ClothingItemsDotTxt.Block block) {
		if (!string.startsWith("FemaleHair_")) {
			if (!string.startsWith("MaleBeard_")) {
				if (!string.startsWith("MaleHair_")) {
					if (!string.startsWith("ZedDmg_")) {
						if (!string.startsWith("Bandage_")) {
							if (!string.startsWith("Zed_Skin")) {
								Iterator iterator = block.children.iterator();
								ClothingItemsDotTxt.Block block2;
								do {
									if (!iterator.hasNext()) {
										ClothingItemsDotTxt.Block block3 = new ClothingItemsDotTxt.Block();
										block3.type = "item";
										block3.id = string;
										ClothingItemsDotTxt.Value value = new ClothingItemsDotTxt.Value();
										value.string = "Type = Clothing";
										block3.elements.add(value);
										block3.values.add(value.string);
										value = new ClothingItemsDotTxt.Value();
										value.string = "DisplayName = " + string;
										block3.elements.add(value);
										block3.values.add(value.string);
										value = new ClothingItemsDotTxt.Value();
										value.string = "ClothingItem = " + string;
										block3.elements.add(value);
										block3.values.add(value.string);
										block.elements.add(block3);
										block.children.add(block3);
										return;
									}

									block2 = (ClothingItemsDotTxt.Block)iterator.next();
								}						 while (!"item".equals(block2.type) || !string.equals(block2.id));
							}
						}
					}
				}
			}
		}
	}

	private static class Block implements ClothingItemsDotTxt.BlockElement {
		public String type;
		public String id;
		public ArrayList elements = new ArrayList();
		public ArrayList values = new ArrayList();
		public ArrayList children = new ArrayList();

		public ClothingItemsDotTxt.Block asBlock() {
			return this;
		}

		public ClothingItemsDotTxt.Value asValue() {
			return null;
		}

		public String toString() {
			StringBuilder stringBuilder = new StringBuilder();
			String string = this.type;
			stringBuilder.append(string + (this.id == null ? "" : " " + this.id) + "\n");
			stringBuilder.append("{\n");
			Iterator iterator = this.elements.iterator();
			while (iterator.hasNext()) {
				ClothingItemsDotTxt.BlockElement blockElement = (ClothingItemsDotTxt.BlockElement)iterator.next();
				String string2 = blockElement.toString();
				String[] stringArray = string2.split("\n");
				int int1 = stringArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					String string3 = stringArray[int2];
					stringBuilder.append("\t" + string3 + "\n");
				}
			}

			stringBuilder.append("}\n");
			return stringBuilder.toString();
		}

		public String toXML() {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<Block type=\"" + this.type + "\" id=\"" + this.id + "\">\n");
			Iterator iterator = this.elements.iterator();
			while (iterator.hasNext()) {
				ClothingItemsDotTxt.BlockElement blockElement = (ClothingItemsDotTxt.BlockElement)iterator.next();
				String string = blockElement.toXML();
				String[] stringArray = string.split("\n");
				int int1 = stringArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					String string2 = stringArray[int2];
					stringBuilder.append("	" + string2 + "\n");
				}
			}

			stringBuilder.append("</Block>\n");
			return stringBuilder.toString();
		}
	}

	private static class Value implements ClothingItemsDotTxt.BlockElement {
		String string;

		public ClothingItemsDotTxt.Block asBlock() {
			return null;
		}

		public ClothingItemsDotTxt.Value asValue() {
			return this;
		}

		public String toString() {
			return this.string + ",\n";
		}

		public String toXML() {
			return "<Value>" + this.string + "</Value>\n";
		}
	}

	private interface BlockElement {

		ClothingItemsDotTxt.Block asBlock();

		ClothingItemsDotTxt.Value asValue();

		String toXML();
	}
}
