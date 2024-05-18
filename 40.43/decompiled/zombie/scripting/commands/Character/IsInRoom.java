package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Room;

public class IsInRoom extends BaseCommand {
   String owner;
   String room;
   boolean invert = false;

   public void init(String var1, String[] var2) {
      if (var2[0].contains(")")) {
         var1 = var1;
      }

      this.owner = var1;
      if (this.owner.indexOf("!") == 0) {
         this.invert = true;
         this.owner = this.owner.substring(1);
      }

      this.room = var2[0].trim();
   }

   public boolean getValue() {
      IsoGameCharacter var1 = this.module.getCharacterActual(this.owner);
      if (var1 == null) {
         return false;
      } else {
         Room var2 = this.module.getRoom(this.room);
         if (var2 != null) {
            if (var1.getCurrentSquare().getRoom() == null) {
               return false;
            } else if (this.invert) {
               return !var2.name.equals(var1.getCurrentSquare().getRoom().RoomDef);
            } else {
               return var2.name.equals(var1.getCurrentSquare().getRoom().RoomDef);
            }
         } else {
            IsoGameCharacter var3 = this.module.getCharacterActual(this.room);
            if (var3 == null) {
               return false;
            } else {
               boolean var4 = false;
               if (var1.getCurrentSquare() != null && var3.getCurrentSquare() != null) {
                  if (var1.getCurrentSquare().getRoom() == var3.getCurrentSquare().getRoom()) {
                     var4 = true;
                  }

                  if (this.invert) {
                     return !var4;
                  } else {
                     return var4;
                  }
               } else {
                  return false;
               }
            }
         }
      }
   }

   public void begin() {
   }

   public void Finish() {
   }

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public boolean DoesInstantly() {
      return true;
   }
}
