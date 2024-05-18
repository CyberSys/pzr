package zombie.behaviors.survivor.orders;

import java.util.HashMap;
import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.scripting.ScriptManager;


public class ArrangeLootingTeamOrder extends ConversationOrder {

	public ArrangeLootingTeamOrder(IsoGameCharacter gameCharacter, String string) {
		super(gameCharacter, string);
	}

	public void initOrder() {
		Stack stack = ((IsoSurvivor)this.chr).getAvailableMembers();
		int int1 = stack.size();
		Stack stack2 = new Stack();
		Stack stack3 = new Stack();
		if (int1 != 0) {
			if (int1 == 1) {
				stack2.addAll(stack);
			} else if (int1 == 2) {
				stack2.add(stack.get(0));
				stack3.add(stack.get(1));
			} else if (int1 == 3) {
				stack2.add(stack.get(0));
				stack3.add(stack.get(1));
				stack3.add(stack.get(2));
			} else if (int1 == 4) {
				stack2.add(stack.get(0));
				stack3.add(stack.get(1));
				stack3.add(stack.get(2));
				stack2.add(stack.get(3));
			} else if (int1 >= 5) {
				stack2.add(stack.get(0));
				stack3.add(stack.get(1));
				stack3.add(stack.get(2));
				stack2.add(stack.get(3));
				stack3.add(stack.get(4));
				for (int int2 = 5; int2 < stack.size(); ++int2) {
					stack2.add(stack.get(int2));
				}
			}

			HashMap hashMap = new HashMap();
			hashMap.put("Leader", this.chr);
			int int3;
			for (int3 = 0; int3 < stack2.size(); ++int3) {
				hashMap.put("Guard" + (int3 + 1), stack2.get(int3));
				((IsoGameCharacter)stack2.get(int3)).GiveOrder(new IdleOrder((IsoGameCharacter)stack2.get(int3)), true);
			}

			for (int3 = 0; int3 < stack3.size(); ++int3) {
				hashMap.put("Companion" + (int3 + 1), stack3.get(int3));
				((IsoGameCharacter)stack3.get(int3)).GiveOrder(new FollowOrder((IsoGameCharacter)stack3.get(int3), this.chr, 4), true);
			}

			this.inst = ScriptManager.instance.PlayInstanceScript((String)null, this.scriptName, (HashMap)hashMap);
		}
	}
}
