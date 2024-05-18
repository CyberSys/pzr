package zombie.core.input;

import java.io.IOException;
import net.java.games.input.AbstractComponent;
import net.java.games.input.Component.Identifier;


public class XInputComponent extends AbstractComponent {
	public float pollData;

	protected XInputComponent(String string, Identifier identifier) {
		super(string, identifier);
	}

	protected float poll() throws IOException {
		return this.pollData;
	}

	public boolean isRelative() {
		return false;
	}
}
