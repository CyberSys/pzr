package zombie.scripting.objects;

import java.util.Iterator;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;


public final class GameSoundScript extends BaseScriptObject {
	public final GameSound gameSound = new GameSound();

	public void Load(String string, String string2) {
		this.gameSound.name = string;
		ScriptParser.Block block = ScriptParser.parse(string2);
		block = (ScriptParser.Block)block.children.get(0);
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String[] stringArray = value.string.split("=");
			String string3 = stringArray[0].trim();
			String string4 = stringArray[1].trim();
			if ("category".equals(string3)) {
				this.gameSound.category = string4;
			} else if ("is3D".equals(string3)) {
				this.gameSound.is3D = Boolean.parseBoolean(string4);
			} else if ("loop".equals(string3)) {
				this.gameSound.loop = Boolean.parseBoolean(string4);
			} else if ("master".equals(string3)) {
				this.gameSound.master = GameSound.MasterVolume.valueOf(string4);
			} else if ("maxInstancesPerEmitter".equals(string3)) {
				this.gameSound.maxInstancesPerEmitter = PZMath.tryParseInt(string4, -1);
			}
		}

		iterator = block.children.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Block block2 = (ScriptParser.Block)iterator.next();
			if ("clip".equals(block2.type)) {
				GameSoundClip gameSoundClip = this.LoadClip(block2);
				this.gameSound.clips.add(gameSoundClip);
			}
		}
	}

	private GameSoundClip LoadClip(ScriptParser.Block block) {
		GameSoundClip gameSoundClip = new GameSoundClip(this.gameSound);
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String[] stringArray = value.string.split("=");
			String string = stringArray[0].trim();
			String string2 = stringArray[1].trim();
			if ("distanceMax".equals(string)) {
				gameSoundClip.distanceMax = (float)Integer.parseInt(string2);
				gameSoundClip.initFlags |= GameSoundClip.INIT_FLAG_DISTANCE_MAX;
			} else if ("distanceMin".equals(string)) {
				gameSoundClip.distanceMin = (float)Integer.parseInt(string2);
				gameSoundClip.initFlags |= GameSoundClip.INIT_FLAG_DISTANCE_MIN;
			} else if ("event".equals(string)) {
				gameSoundClip.event = string2;
			} else if ("file".equals(string)) {
				gameSoundClip.file = string2;
			} else if ("pitch".equals(string)) {
				gameSoundClip.pitch = Float.parseFloat(string2);
			} else if ("volume".equals(string)) {
				gameSoundClip.volume = Float.parseFloat(string2);
			} else if ("reverbFactor".equals(string)) {
				gameSoundClip.reverbFactor = Float.parseFloat(string2);
			} else if ("reverbMaxRange".equals(string)) {
				gameSoundClip.reverbMaxRange = Float.parseFloat(string2);
			}
		}

		return gameSoundClip;
	}

	public void reset() {
		this.gameSound.reset();
	}
}
