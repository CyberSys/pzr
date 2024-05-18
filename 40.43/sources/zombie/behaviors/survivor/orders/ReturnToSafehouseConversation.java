package zombie.behaviors.survivor.orders;

import java.util.HashMap;
import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.scripting.ScriptManager;


public class ReturnToSafehouseConversation extends ConversationOrder {

	public ReturnToSafehouseConversation(IsoGameCharacter gameCharacter, String string) {
		super(gameCharacter, string);
	}

	public void initOrder() {
		Stack stack = ((IsoSurvivor)this.chr).getAvailableMembers();
		int int1 = stack.size();
		Stack stack2 = new Stack();
		Stack stack3 = new Stack();
		HashMap hashMap = new HashMap();
		hashMap.put("Leader", this.chr);
		int int2;
		for (int2 = 0; int2 < stack.size(); ++int2) {
			IsoGameCharacter gameCharacter = (IsoGameCharacter)stack.get(int2);
			if (gameCharacter.getOrder() instanceof FollowOrder) {
				stack2.add(gameCharacter);
			} else {
				stack3.add(gameCharacter);
			}
		}

		for (int2 = 0; int2 < stack2.size(); ++int2) {
			hashMap.put("Returning" + (int2 + 1), stack2.get(int2));
		}

		for (int2 = 0; int2 < stack3.size(); ++int2) {
			hashMap.put("AtSafehouse" + (int2 + 1), stack3.get(int2));
		}

		this.inst = ScriptManager.instance.PlayInstanceScript((String)null, this.scriptName, (HashMap)hashMap);
	}
}
