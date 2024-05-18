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

   public void Load(String var1, String[] var2) {
      this.name = var1;
      this.person = var2[0].trim();
      if (this.person.equals("null")) {
         this.person = null;
      }

      this.desc = new SurvivorDesc();
      this.desc.setForename(var2[1].trim());
      this.desc.setSurname(var2[2].trim());
      this.desc.setInventoryScript(var2[3].trim());
      if (var2.length > 6) {
         this.desc.setSkinpal(var2[4].trim());
         this.desc.setHead(var2[5].trim());
         this.desc.setTorso(var2[6].trim());
         this.desc.setLegs(var2[7].trim());
         this.desc.setToppal(var2[8].trim());
         this.desc.setBottomspal(var2[9].trim());
         this.desc.setShoes(var2[10].trim());
      }

   }

   public void Actualise(int var1, int var2, int var3) {
      if (this.Actual != null && !(this.Actual.getHealth() <= 0.0F) && !(this.Actual.getBodyDamage().getHealth() <= 0.0F)) {
         this.Actual.setX((float)var1);
         this.Actual.setY((float)var2);
         this.Actual.setZ((float)var3);
         this.Actual.getCurrentSquare().getMovingObjects().remove(this.Actual);
         this.Actual.setCurrent(IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, var3));
         if (this.Actual.getCurrentSquare() != null) {
            this.Actual.getCurrentSquare().getMovingObjects().add(this.Actual);
         }
      } else {
         if (this.person == null) {
            this.Actual = new IsoPlayer(IsoWorld.instance.CurrentCell, this.desc, var1, var2, var3);
         } else {
            this.Actual = new IsoSurvivor(SurvivorPersonality.Personality.valueOf(this.person), this.desc, IsoWorld.instance.CurrentCell, var1, var2, var3);
            this.Actual.getInventory().clear();
         }

         this.Actual.setScriptName(this.name);
         this.Actual.setScriptModule(this.module.name);
         ScriptManager.instance.FillInventory(this.Actual, this.Actual.getInventory(), this.desc.getInventoryScript());
      }

   }

   public boolean AllowBehaviours() {
      for(int var1 = 0; var1 < ScriptManager.instance.PlayingScripts.size(); ++var1) {
         Script.ScriptInstance var2 = (Script.ScriptInstance)ScriptManager.instance.PlayingScripts.get(var1);
         if (!var2.theScript.AllowCharacterBehaviour(this.name, var2)) {
            return false;
         }
      }

      return true;
   }
}
