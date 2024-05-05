package zombie.input;


public final class MouseStateCache {
	private final Object m_lock = "MouseStateCache Lock";
	private int m_stateIndexUsing = 0;
	private int m_stateIndexPolling = 1;
	private final MouseState[] m_states = new MouseState[]{new MouseState(), new MouseState()};

	public void poll() {
		synchronized (this.m_lock) {
			MouseState mouseState = this.getStatePolling();
			if (!mouseState.wasPolled()) {
				mouseState.poll();
			}
		}
	}

	public void swap() {
		synchronized (this.m_lock) {
			if (this.getStatePolling().wasPolled()) {
				this.m_stateIndexUsing = this.m_stateIndexPolling;
				this.m_stateIndexPolling = this.m_stateIndexPolling == 1 ? 0 : 1;
				this.getStatePolling().set(this.getState());
				this.getStatePolling().reset();
			}
		}
	}

	public MouseState getState() {
		synchronized (this.m_lock) {
			return this.m_states[this.m_stateIndexUsing];
		}
	}

	private MouseState getStatePolling() {
		synchronized (this.m_lock) {
			return this.m_states[this.m_stateIndexPolling];
		}
	}
}
