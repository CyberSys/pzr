package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.Iterator;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;


public final class GameSoundScript extends BaseScriptObject {
	public GameSound gameSound = new GameSound();

	private int readBlock(String string, int int1, GameSoundScript.Block block) {
		int int2;
		for (int2 = int1; int2 < string.length(); ++int2) {
			if (string.charAt(int2) == '{') {
				GameSoundScript.Block block2 = new GameSoundScript.Block();
				block.children.add(block2);
				block.elements.add(block2);
				String string2 = string.substring(int1, int2).trim();
				String[] stringArray = string2.split("\\s+");
				block2.type = stringArray[0];
				block2.id = stringArray.length > 1 ? stringArray[1] : null;
				int2 = this.readBlock(string, int2 + 1, block2);
				int1 = int2;
			} else {
				if (string.charAt(int2) == '}') {
					return int2 + 1;
				}

				if (string.charAt(int2) == ',') {
					GameSoundScript.Value value = new GameSoundScript.Value();
					value.string = string.substring(int1, int2);
					block.values.add(value.string);
					block.elements.add(value);
					int1 = int2 + 1;
				}
			}
		}

		return int2;
	}

	public void Load(String string, String string2) {
		this.gameSound.name = string;
		GameSoundScript.Block block = new GameSoundScript.Block();
		this.readBlock(string2, 0, block);
		block = (GameSoundScript.Block)block.children.get(0);
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			String string3 = (String)iterator.next();
			String[] stringArray = string3.split("=");
			String string4 = stringArray[0].trim();
			String string5 = stringArray[1].trim();
			if ("category".equals(string4)) {
				this.gameSound.category = string5;
			} else if ("is3D".equals(string4)) {
				this.gameSound.is3D = Boolean.parseBoolean(string5);
			} else if ("loop".equals(string4)) {
				this.gameSound.loop = Boolean.parseBoolean(string5);
			} else if ("master".equals(string4)) {
				this.gameSound.master = GameSound.MasterVolume.valueOf(string5);
			}
		}

		iterator = block.children.iterator();
		while (iterator.hasNext()) {
			GameSoundScript.Block block2 = (GameSoundScript.Block)iterator.next();
			if ("clip".equals(block2.type)) {
				GameSoundClip gameSoundClip = this.LoadClip(block2);
				this.gameSound.clips.add(gameSoundClip);
			}
		}
	}

	private GameSoundClip LoadClip(GameSoundScript.Block block) {
		GameSoundClip gameSoundClip = new GameSoundClip(this.gameSound);
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			String[] stringArray = string.split("=");
			String string2 = stringArray[0].trim();
			String string3 = stringArray[1].trim();
			if ("distanceMax".equals(string2)) {
				gameSoundClip.distanceMax = (float)Integer.parseInt(string3);
				gameSoundClip.initFlags |= GameSoundClip.INIT_FLAG_DISTANCE_MAX;
			} else if ("distanceMin".equals(string2)) {
				gameSoundClip.distanceMin = (float)Integer.parseInt(string3);
				gameSoundClip.initFlags |= GameSoundClip.INIT_FLAG_DISTANCE_MIN;
			} else if ("event".equals(string2)) {
				gameSoundClip.event = string3;
			} else if ("file".equals(string2)) {
				gameSoundClip.file = string3;
			} else if ("pitch".equals(string2)) {
				gameSoundClip.pitch = Float.parseFloat(string3);
			} else if ("volume".equals(string2)) {
				gameSoundClip.volume = Float.parseFloat(string3);
			} else if ("reverbFactor".equals(string2)) {
				gameSoundClip.reverbFactor = Float.parseFloat(string3);
			} else if ("reverbMaxRange".equals(string2)) {
				gameSoundClip.reverbMaxRange = Float.parseFloat(string3);
			}
		}

		return gameSoundClip;
	}

	public void reset() {
		this.gameSound.reset();
	}

	private static class Block implements GameSoundScript.BlockElement {
		public String type;
		public String id;
		public ArrayList elements;
		public ArrayList values;
		public ArrayList children;

		private Block() {
			this.elements = new ArrayList();
			this.values = new ArrayList();
			this.children = new ArrayList();
		}

		public GameSoundScript.Block asBlock() {
			return this;
		}

		public GameSoundScript.Value asValue() {
			return null;
		}

		public boolean isEmpty() {
			return this.elements.isEmpty();
		}

		Block(Object object) {
			this();
		}
	}

	private static class Value implements GameSoundScript.BlockElement {
		String string;

		private Value() {
		}

		public GameSoundScript.Block asBlock() {
			return null;
		}

		public GameSoundScript.Value asValue() {
			return this;
		}

		Value(Object object) {
			this();
		}
	}

	private interface BlockElement {

		GameSoundScript.Block asBlock();

		GameSoundScript.Value asValue();
	}
}
