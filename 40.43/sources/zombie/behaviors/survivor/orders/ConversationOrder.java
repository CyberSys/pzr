package zombie.behaviors.survivor.orders;

import java.util.HashMap;
import java.util.Stack;
import zombie.behaviors.Behavior;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Script;


public class ConversationOrder extends Order {
	Script.ScriptInstance inst;
	String scriptName;
	IsoGameCharacter chr;

	public ConversationOrder(IsoGameCharacter gameCharacter, String string) {
		super(gameCharacter);
		this.chr = gameCharacter;
		this.scriptName = string;
	}

	public Behavior.BehaviorResult process() {
		return this.inst != null && !this.inst.finished() ? Behavior.BehaviorResult.Working : Behavior.BehaviorResult.Succeeded;
	}

	public void initOrder() {
		Stack stack = ((IsoSurvivor)this.chr).getAvailableMembers();
		HashMap hashMap = new HashMap();
		hashMap.put("Leader", this.chr);
		for (int int1 = 0; int1 < stack.size(); ++int1) {
			hashMap.put("Member" + (int1 + 1), stack.get(int1));
		}

		this.inst = ScriptManager.instance.PlayInstanceScript((String)null, this.scriptName, (HashMap)hashMap);
	}

	public boolean complete() {
		return this.inst == null || this.inst.finished();
	}

	public void update() {
	}
}
