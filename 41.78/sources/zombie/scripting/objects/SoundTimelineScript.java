package zombie.scripting.objects;

import java.util.HashMap;
import java.util.Iterator;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;


public final class SoundTimelineScript extends BaseScriptObject {
	private String eventName;
	private HashMap positionByName = new HashMap();

	public void Load(String string, String string2) {
		this.eventName = string;
		ScriptParser.Block block = ScriptParser.parse(string2);
		block = (ScriptParser.Block)block.children.get(0);
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String string3 = value.getKey().trim();
			String string4 = value.getValue().trim();
			this.positionByName.put(string3, PZMath.tryParseInt(string4, 0));
		}
	}

	public String getEventName() {
		return this.eventName;
	}

	public int getPosition(String string) {
		return this.positionByName.containsKey(string) ? (Integer)this.positionByName.get(string) : -1;
	}

	public void reset() {
		this.positionByName.clear();
	}
}
