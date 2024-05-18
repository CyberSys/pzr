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
   Inventory getInventory(String var1);

   ScriptCharacter getCharacter(String var1);

   IsoGameCharacter getCharacterActual(String var1);

   Waypoint getWaypoint(String var1);

   ScriptContainer getScriptContainer(String var1);

   Room getRoom(String var1);

   ScriptActivatable getActivatable(String var1);

   String getFlagValue(String var1);

   ScriptFlag getFlag(String var1);

   Zone getZone(String var1);

   QuestTaskCondition getQuestCondition(String var1);

   Item getItem(String var1);

   Recipe getRecipe(String var1);

   Script getScript(String var1);
}
