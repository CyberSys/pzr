package zombie.scripting.commands.Character;

import java.util.Stack;
import zombie.characters.IsoGameCharacter;


class StringFunctions {

	static String EscapeChar(IsoGameCharacter gameCharacter, String string) {
		string = string.replace("$FIRSTNAME$", gameCharacter.getDescriptor().getForename());
		string = string.replace("$SURNAME$", gameCharacter.getDescriptor().getSurname());
		return string;
	}

	static String EscapeChar(IsoGameCharacter gameCharacter, Stack stack, String string) {
		string = EscapeChar(gameCharacter, string);
		for (int int1 = 0; int1 < stack.size(); ++int1) {
			string = string.replace("$FIRSTNAME" + (int1 + 1) + "$", ((IsoGameCharacter)stack.get(int1)).getDescriptor().getForename());
			string = string.replace("$SURNAME" + (int1 + 1) + "$", ((IsoGameCharacter)stack.get(int1)).getDescriptor().getSurname());
		}

		return string;
	}
}
