package zombie.scripting;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.objects.Inventory;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.QuestTaskCondition;
import zombie.scripting.objects.Recipe;
import zombie.scripting.objects.Room;
import zombie.scripting.objects.Script;
import zombie.scripting.objects.ScriptActivatable;
import zombie.scripting.objects.ScriptCharacter;
import zombie.scripting.objects.ScriptContainer;
import zombie.scripting.objects.ScriptFlag;
import zombie.scripting.objects.Waypoint;
import zombie.scripting.objects.Zone;


public interface IScriptObjectStore {

	Inventory getInventory(String string);

	ScriptCharacter getCharacter(String string);

	IsoGameCharacter getCharacterActual(String string);

	Waypoint getWaypoint(String string);

	ScriptContainer getScriptContainer(String string);

	Room getRoom(String string);

	ScriptActivatable getActivatable(String string);

	String getFlagValue(String string);

	ScriptFlag getFlag(String string);

	Zone getZone(String string);

	QuestTaskCondition getQuestCondition(String string);

	Item getItem(String string);

	Recipe getRecipe(String string);

	Script getScript(String string);
}
