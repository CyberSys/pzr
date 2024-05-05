package zombie.scripting;

import java.util.ArrayList;
import java.util.Iterator;


public final class ScriptParser {
	private static StringBuilder stringBuilder = new StringBuilder();

	public static int readBlock(String string, int int1, ScriptParser.Block block) {
		int int2;
		for (int2 = int1; int2 < string.length(); ++int2) {
			if (string.charAt(int2) == '{') {
				ScriptParser.Block block2 = new ScriptParser.Block();
				block.children.add(block2);
				block.elements.add(block2);
				String string2 = string.substring(int1, int2).trim();
				String[] stringArray = string2.split("\\s+");
				block2.type = stringArray[0];
				block2.id = stringArray.length > 1 ? stringArray[1] : null;
				int2 = readBlock(string, int2 + 1, block2);
				int1 = int2;
			} else {
				if (string.charAt(int2) == '}') {
					return int2 + 1;
				}

				if (string.charAt(int2) == ',') {
					ScriptParser.Value value = new ScriptParser.Value();
					value.string = string.substring(int1, int2);
					block.values.add(value);
					block.elements.add(value);
					int1 = int2 + 1;
				}
			}
		}

		return int2;
	}

	public static ScriptParser.Block parse(String string) {
		ScriptParser.Block block = new ScriptParser.Block();
		readBlock(string, 0, block);
		return block;
	}

	public static String stripComments(String string) {
		stringBuilder.setLength(0);
		stringBuilder.append(string);
		int int1;
		for (int int2 = stringBuilder.lastIndexOf("*/"); int2 != -1; int2 = stringBuilder.lastIndexOf("*/", int1)) {
			int1 = stringBuilder.lastIndexOf("/*", int2 - 1);
			if (int1 == -1) {
				break;
			}

			int int3;
			for (int int4 = stringBuilder.lastIndexOf("*/", int2 - 1); int4 > int1; int4 = stringBuilder.lastIndexOf("*/", int3 - 2)) {
				int3 = int1;
				int1 = stringBuilder.lastIndexOf("/*", int1 - 2);
				if (int1 == -1) {
					break;
				}
			}

			if (int1 == -1) {
				break;
			}

			stringBuilder.replace(int1, int2 + 2, "");
		}

		string = stringBuilder.toString();
		stringBuilder.setLength(0);
		return string;
	}

	public static ArrayList parseTokens(String string) {
		ArrayList arrayList = new ArrayList();
		while (true) {
			int int1 = 0;
			int int2 = 0;
			int int3 = 0;
			if (string.indexOf("}", int2 + 1) == -1) {
				if (string.trim().length() > 0) {
					arrayList.add(string.trim());
				}

				return arrayList;
			}

			do {
				int2 = string.indexOf("{", int2 + 1);
				int3 = string.indexOf("}", int3 + 1);
				if ((int3 >= int2 || int3 == -1) && int2 != -1) {
					int3 = int2;
					++int1;
				} else {
					int2 = int3;
					--int1;
				}
			}	 while (int1 > 0);

			arrayList.add(string.substring(0, int2 + 1).trim());
			string = string.substring(int2 + 1);
		}
	}

	public static class Block implements ScriptParser.BlockElement {
		public String type;
		public String id;
		public final ArrayList elements = new ArrayList();
		public final ArrayList values = new ArrayList();
		public final ArrayList children = new ArrayList();

		public ScriptParser.Block asBlock() {
			return this;
		}

		public ScriptParser.Value asValue() {
			return null;
		}

		public boolean isEmpty() {
			return this.elements.isEmpty();
		}

		public void prettyPrint(int int1, StringBuilder stringBuilder, String string) {
			int int2;
			for (int2 = 0; int2 < int1; ++int2) {
				stringBuilder.append('\t');
			}

			stringBuilder.append(this.type);
			if (this.id != null) {
				stringBuilder.append(" ");
				stringBuilder.append(this.id);
			}

			stringBuilder.append(string);
			for (int2 = 0; int2 < int1; ++int2) {
				stringBuilder.append('\t');
			}

			stringBuilder.append('{');
			stringBuilder.append(string);
			this.prettyPrintElements(int1 + 1, stringBuilder, string);
			for (int2 = 0; int2 < int1; ++int2) {
				stringBuilder.append('\t');
			}

			stringBuilder.append('}');
			stringBuilder.append(string);
		}

		public void prettyPrintElements(int int1, StringBuilder stringBuilder, String string) {
			ScriptParser.BlockElement blockElement = null;
			ScriptParser.BlockElement blockElement2;
			for (Iterator iterator = this.elements.iterator(); iterator.hasNext(); blockElement = blockElement2) {
				blockElement2 = (ScriptParser.BlockElement)iterator.next();
				if (blockElement2.asBlock() != null && blockElement != null) {
					stringBuilder.append(string);
				}

				if (blockElement2.asValue() != null && blockElement instanceof ScriptParser.Block) {
					stringBuilder.append(string);
				}

				blockElement2.prettyPrint(int1, stringBuilder, string);
			}
		}

		public ScriptParser.Block addBlock(String string, String string2) {
			ScriptParser.Block block = new ScriptParser.Block();
			block.type = string;
			block.id = string2;
			this.elements.add(block);
			this.children.add(block);
			return block;
		}

		public ScriptParser.Block getBlock(String string, String string2) {
			Iterator iterator = this.children.iterator();
			ScriptParser.Block block;
			do {
				do {
					if (!iterator.hasNext()) {
						return null;
					}

					block = (ScriptParser.Block)iterator.next();
				}	 while (!block.type.equals(string));
			} while ((block.id == null || !block.id.equals(string2)) && (block.id != null || string2 != null));

			return block;
		}

		public ScriptParser.Value getValue(String string) {
			Iterator iterator = this.values.iterator();
			ScriptParser.Value value;
			int int1;
			do {
				if (!iterator.hasNext()) {
					return null;
				}

				value = (ScriptParser.Value)iterator.next();
				int1 = value.string.indexOf(61);
			} while (int1 <= 0 || !value.getKey().trim().equals(string));

			return value;
		}

		public void setValue(String string, String string2) {
			ScriptParser.Value value = this.getValue(string);
			if (value == null) {
				this.addValue(string, string2);
			} else {
				value.string = string + " = " + string2;
			}
		}

		public ScriptParser.Value addValue(String string, String string2) {
			ScriptParser.Value value = new ScriptParser.Value();
			value.string = string + " = " + string2;
			this.elements.add(value);
			this.values.add(value);
			return value;
		}

		public void moveValueAfter(String string, String string2) {
			ScriptParser.Value value = this.getValue(string);
			ScriptParser.Value value2 = this.getValue(string2);
			if (value != null && value2 != null) {
				this.elements.remove(value);
				this.values.remove(value);
				this.elements.add(this.elements.indexOf(value2) + 1, value);
				this.values.add(this.values.indexOf(value2) + 1, value);
			}
		}
	}

	public static class Value implements ScriptParser.BlockElement {
		public String string;

		public ScriptParser.Block asBlock() {
			return null;
		}

		public ScriptParser.Value asValue() {
			return this;
		}

		public void prettyPrint(int int1, StringBuilder stringBuilder, String string) {
			for (int int2 = 0; int2 < int1; ++int2) {
				stringBuilder.append('\t');
			}

			stringBuilder.append(this.string.trim());
			stringBuilder.append(',');
			stringBuilder.append(string);
		}

		public String getKey() {
			int int1 = this.string.indexOf(61);
			return int1 == -1 ? this.string : this.string.substring(0, int1);
		}

		public String getValue() {
			int int1 = this.string.indexOf(61);
			return int1 == -1 ? "" : this.string.substring(int1 + 1);
		}
	}

	public interface BlockElement {

		ScriptParser.Block asBlock();

		ScriptParser.Value asValue();

		void prettyPrint(int int1, StringBuilder stringBuilder, String string);
	}
}
