package zombie.core.input;

import java.io.IOException;
import net.java.games.input.AbstractComponent;
import net.java.games.input.Component.Identifier;

public class XInputComponent extends AbstractComponent {
   public float pollData;

   protected XInputComponent(String var1, Identifier var2) {
      super(var1, var2);
   }

   protected float poll() throws IOException {
      return this.pollData;
   }

   public boolean isRelative() {
      return false;
   }
}
