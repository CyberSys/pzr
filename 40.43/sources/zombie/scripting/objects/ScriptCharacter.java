package zombie.scripting.objects;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorPersonality;
import zombie.iso.IsoWorld;
import zombie.scripting.ScriptManager;


public class ScriptCharacter extends BaseScriptObject {
	public IsoGameCharacter Actual;
	public SurvivorDesc desc;
	public String person;
	public String name;

	public void Load(String string, String[] stringArray) {
		this.name = string;
		this.person = stringArray[0].trim();
		if (this.person.equals("null")) {
			this.person = null;
		}

		this.desc = new SurvivorDesc();
		this.desc.setForename(stringArray[1].trim());
		this.desc.setSurname(stringArray[2].trim());
		this.desc.setInventoryScript(stringArray[3].trim());
		if (stringArray.length > 6) {
			this.desc.setSkinpal(stringArray[4].trim());
			this.desc.setHead(stringArray[5].trim());
			this.desc.setTorso(stringArray[6].trim());
			this.desc.setLegs(stringArray[7].trim());
			this.desc.setToppal(stringArray[8].trim());
			this.desc.setBottomspal(stringArray[9].trim());
			this.desc.setShoes(stringArray[10].trim());
		}
	}

	public void Actualise(int int1, int int2, int int3) {
		if (this.Actual != null && !(this.Actual.getHealth() <= 0.0F) && !(this.Actual.getBodyDamage().getHealth() <= 0.0F)) {
			this.Actual.setX((float)int1);
			this.Actual.setY((float)int2);
			this.Actual.setZ((float)int3);
			this.Actual.getCurrentSquare().getMovingObjects().remove(this.Actual);
			this.Actual.setCurrent(IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3));
			if (this.Actual.getCurrentSquare() != null) {
				this.Actual.getCurrentSquare().getMovingObjects().add(this.Actual);
			}
		} else {
			if (this.person == null) {
				this.Actual = new IsoPlayer(IsoWorld.instance.CurrentCell, this.desc, int1, int2, int3);
			} else {
				this.Actual = new IsoSurvivor(SurvivorPersonality.Personality.valueOf(this.person), this.desc, IsoWorld.instance.CurrentCell, int1, int2, int3);
				this.Actual.getInventory().clear();
			}

			this.Actual.setScriptName(this.name);
			this.Actual.setScriptModule(this.module.name);
			ScriptManager.instance.FillInventory(this.Actual, this.Actual.getInventory(), this.desc.getInventoryScript());
		}
	}

	public boolean AllowBehaviours() {
		for (int int1 = 0; int1 < ScriptManager.instance.PlayingScripts.size(); ++int1) {
			Script.ScriptInstance scriptInstance = (Script.ScriptInstance)ScriptManager.instance.PlayingScripts.get(int1);
			if (!scriptInstance.theScript.AllowCharacterBehaviour(this.name, scriptInstance)) {
				return false;
			}
		}

		return true;
	}
}
