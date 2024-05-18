package zombie.radio.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import zombie.characters.IsoPlayer;


public class LineEffectMemory {
	private Map memory = new HashMap();

	public void addLine(IsoPlayer player, String string) {
		int int1 = player.getDescriptor().getID();
		ArrayList arrayList;
		if (!this.memory.containsKey(int1)) {
			arrayList = new ArrayList();
			this.memory.put(int1, arrayList);
		} else {
			arrayList = (ArrayList)this.memory.get(int1);
		}

		if (!arrayList.contains(string)) {
			arrayList.add(string);
		}
	}

	public boolean contains(IsoPlayer player, String string) {
		int int1 = player.getDescriptor().getID();
		return !this.memory.containsKey(int1) ? false : ((ArrayList)this.memory.get(int1)).contains(string);
	}
}
