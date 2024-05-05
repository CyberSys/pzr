package zombie.input;

import org.lwjglx.input.KeyEventQueue;
import org.lwjglx.input.Keyboard;


public final class KeyboardState {
	private boolean m_isCreated = false;
	private boolean[] m_keyDownStates = null;
	private final KeyEventQueue m_keyEventQueue = new KeyEventQueue();
	private boolean m_wasPolled = false;

	public void poll() {
		boolean boolean1 = !this.m_isCreated;
		this.m_isCreated = this.m_isCreated || Keyboard.isCreated();
		if (this.m_isCreated) {
			if (boolean1) {
				this.m_keyDownStates = new boolean[256];
			}

			this.m_wasPolled = true;
			for (int int1 = 0; int1 < this.m_keyDownStates.length; ++int1) {
				this.m_keyDownStates[int1] = Keyboard.isKeyDown(int1);
			}
		}
	}

	public boolean wasPolled() {
		return this.m_wasPolled;
	}

	public void set(KeyboardState keyboardState) {
		this.m_isCreated = keyboardState.m_isCreated;
		if (keyboardState.m_keyDownStates != null) {
			if (this.m_keyDownStates == null || this.m_keyDownStates.length != keyboardState.m_keyDownStates.length) {
				this.m_keyDownStates = new boolean[keyboardState.m_keyDownStates.length];
			}

			System.arraycopy(keyboardState.m_keyDownStates, 0, this.m_keyDownStates, 0, this.m_keyDownStates.length);
		} else {
			this.m_keyDownStates = null;
		}

		this.m_wasPolled = keyboardState.m_wasPolled;
	}

	public void reset() {
		this.m_wasPolled = false;
	}

	public boolean isCreated() {
		return this.m_isCreated;
	}

	public boolean isKeyDown(int int1) {
		return this.m_keyDownStates[int1];
	}

	public int getKeyCount() {
		return this.m_keyDownStates.length;
	}

	public KeyEventQueue getEventQueue() {
		return this.m_keyEventQueue;
	}
}
